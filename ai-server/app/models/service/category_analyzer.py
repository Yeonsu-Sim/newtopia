import logging
from typing import Dict, List, Any
import numpy as np
from dataclasses import dataclass

from .kobert_service import kobert_service

logger = logging.getLogger(__name__)


@dataclass 
class CategoryScore:
    category: str
    confidence: float
    

@dataclass
class CategoryAnalysisResult:
    major_categories: List[CategoryScore]
    sub_categories: Dict[str, List[CategoryScore]]  # major_category -> [CategoryScore]
    debug_similarities: Dict[str, float] = None  # 디버깅용


class CategoryDefinitions:
    MAJOR_CATEGORIES = {
        "economy": {
            "name": "💰 경제",
            "description": "거시경제, 재정, 금융시장, 산업·기업 활동 등 경제 전반",
            "keywords": ["GDP","성장률","인플레이션","금리","환율","주가","채권","은행",
                         "증권","예산","세금","재정","수출","수입","무역","투자","산업","기업실적"]
        },
        "defense": {
            "name": "🛡️ 국방",
            "description": "군사·동맹·사이버/우주·공공안보 등 국가안보 전반",
            "keywords": ["군사","무기","훈련","안보","동맹","확장억제","사이버","해킹",
                         "위성","GPS","테러","재난","팬데믹","국가안전"]
        },
        "publicSentiment": {
            "name": "👥 민심",
            "description": "여론·사회현안·집회/파업·건강/복지 등 국민 삶과 인식",
            "keywords": ["여론","지지율","설문","교육","주거","범죄","젠더","시위","파업",
                         "노동","의료","복지","사회보장","생활비","삶의질"]
        },
        "environment": {
            "name": "🌱 환경",
            "description": "기후·에너지·오염/재난·생물다양성·자원관리",
            "keywords": ["탄소중립","온실가스","재생에너지","원자력","대기오염","수질",
                         "산불","생물다양성","국립공원","수자원","광물자원","토지이용"]
        }
    }

    SUB_CATEGORIES = {
        "economy": {
            "macroeconomy": {
                "name": "거시경제",
                "description": "GDP·물가·고용 등 거시 지표",
                "keywords": ["GDP","성장","물가","고용","실업률","경기","생산","소비"]
            },
            "fiscalPolicy": {
                "name": "재정정책",
                "description": "예산·세제·국채 등 재정 운용",
                "keywords": ["예산","세금","세제","국채","부채","정부지출","감세","증세"]
            },
            "financialMarkets": {
                "name": "금융시장",
                "description": "주식·채권·환율·금리·규제",
                "keywords": ["증시","코스피","코스닥","환율","금리","주식","채권","자본시장","금융규제"]
            },
            "industryBusiness": {
                "name": "산업·기업",
                "description": "기업 실적·산업 정책·투자 동향",
                "keywords": ["실적","산업정책","투자","M&A","공장","생산능력","수주","신사업"]
            }
        },
        "defense": {
            "militarySecurity": {
                "name": "군사·무기 안보",
                "description": "훈련·교전 태세·무기 개발",
                "keywords": ["훈련","교전","전력","무기개발","실험","전투력"]
            },
            "alliances": {
                "name": "동맹·안보 협력",
                "description": "동맹·다자 안보 협의·확장억제",
                "keywords": ["동맹","합동훈련","확장억제","다자안보","상호방위"]
            },
            "cyberSpace": {
                "name": "사이버·우주",
                "description": "해킹·전자전·위성/GPS 교란",
                "keywords": ["사이버공격","해킹","랜섬웨어","전자전","위성","GPS","우주안보"]
            },
            "publicSafety": {
                "name": "공공안전",
                "description": "테러·대규모 재난·팬데믹",
                "keywords": ["테러","재난","지진","태풍","감염병","팬데믹","국민안전"]
            }
        },
        "publicSentiment": {
            "publicOpinion": {
                "name": "여론",
                "description": "여론조사·지지율·정책 선호",
                "keywords": ["여론조사","지지율","정책선호","평판","호감도"]
            },
            "socialIssues": {
                "name": "사회 현안",
                "description": "교육·주거·범죄·젠더 등",
                "keywords": ["교육","주거","범죄","젠더","청년","고령화","이민","치안"]
            },
            "protestsStrikes": {
                "name": "집회·시위·파업",
                "description": "노동 쟁의·시민운동·대규모 시위",
                "keywords": ["시위","파업","노조","쟁의","집회","사회운동"]
            },
            "healthWelfare": {
                "name": "건강·복지",
                "description": "의료 서비스·사회보장·복지 정책",
                "keywords": ["의료","건강보험","복지","연금","돌봄","보육","취약계층"]
            }
        },
        "environment": {
            "climateChangeEnergy": {
                "name": "기후변화·에너지",
                "description": "탄소중립·재생에너지·원자력",
                "keywords": ["탄소중립","온실가스","재생에너지","태양광","풍력","원자력","수소"]
            },
            "pollutionDisaster": {
                "name": "오염·재난",
                "description": "대기/수질/토양 오염·산불 등",
                "keywords": ["대기오염","미세먼지","수질오염","토양오염","산불","유출"]
            },
            "biodiversity": {
                "name": "생물다양성",
                "description": "종 보전·생태 복원·보호구역",
                "keywords": ["멸종위기","생태계","복원","국립공원","보호구역"]
            },
            "resourceManagement": {
                "name": "자원 관리",
                "description": "산림·수자원·광물·토지이용",
                "keywords": ["산림","수자원","광물자원","토지이용","개발","보전"]
            }
        }
    }



class CategoryAnalyzer:
    def __init__(self):
        self.service = kobert_service
        self._category_embeddings = {}
        self._is_initialized = False
    
    async def initialize(self):
        """카테고리 임베딩을 미리 계산하여 캐시"""
        # 강제로 재초기화 (키워드 변경 반영을 위해)
        self._is_initialized = False
        self._category_embeddings = {}
        
        if self._is_initialized:
            return
            
        logger.info("Initializing category embeddings...")
        
        # 대분류 임베딩 계산
        for cat_id, cat_info in CategoryDefinitions.MAJOR_CATEGORIES.items():
            text = f"{cat_info['name']} {cat_info['description']} {' '.join(cat_info['keywords'])}"
            embedding = self.service.encode_text(text)
            self._category_embeddings[f"major_{cat_id}"] = embedding
            
        # 중분류 임베딩 계산  
        for major_cat, sub_cats in CategoryDefinitions.SUB_CATEGORIES.items():
            for sub_cat_id, sub_cat_info in sub_cats.items():
                key = f"sub_{major_cat}_{sub_cat_id}"
                text = f"{sub_cat_info['name']} {sub_cat_info['description']} {' '.join(sub_cat_info['keywords'])}"
                embedding = self.service.encode_text(text)
                self._category_embeddings[key] = embedding
        
        self._is_initialized = True
        logger.info("Category embeddings initialized successfully")
    
    def analyze_news(self, title: str, content: str) -> CategoryAnalysisResult:
        """뉴스 제목과 내용을 분석하여 모든 카테고리의 신뢰도 반환"""
        if not self._is_initialized:
            raise RuntimeError("CategoryAnalyzer not initialized. Call initialize() first.")
        
        # 뉴스 텍스트
        news_text = f"{title} {content}"

        news_embedding = self.service.encode_text(news_text)
        
        # 하이브리드 방식: 키워드 매칭 + 임베딩 유사도
        major_similarities: Dict[str, float] = {}
        
        for cat_id, cat_info in CategoryDefinitions.MAJOR_CATEGORIES.items():
            # 1. 임베딩 유사도 계산
            news_embedding = self.service.encode_text(news_text)
            cat_embedding = self._category_embeddings[f"major_{cat_id}"]
            embedding_sim = self._calculate_cosine_similarity(news_embedding, cat_embedding)
            
            # 2. 키워드 직접 매칭 점수 계산
            keyword_score = self._calculate_keyword_score(news_text, cat_info["keywords"])
            
            # 3. 하이브리드 점수 계산 (키워드 60% + 임베딩 40%)
            hybrid_score = 0.6 * keyword_score + 0.4 * embedding_sim
            major_similarities[cat_id] = hybrid_score
        
        # 디버깅용 출력
        print(f"DEBUG - Title: {title[:50]}...")
        print(f"DEBUG - Hybrid similarities: {major_similarities}")
        logger.info(f"Hybrid similarities for '{title[:50]}...': {major_similarities}")
        
        # 상대적 신뢰도 계산
        major_scores = self._calculate_relative_confidence(major_similarities)
        
        # 중분류 분석
        sub_categories_result = {}
        for major_cat in CategoryDefinitions.SUB_CATEGORIES.keys():
            sub_similarities = {}
            sub_cats = CategoryDefinitions.SUB_CATEGORIES[major_cat]
            
            for sub_cat_id in sub_cats.keys():
                key = f"sub_{major_cat}_{sub_cat_id}"
                sub_embedding = self._category_embeddings[key]
                similarity = self._calculate_cosine_similarity(news_embedding, sub_embedding)
                sub_similarities[sub_cat_id] = similarity
            
            # 상대적 신뢰도 계산
            sub_scores = self._calculate_relative_confidence(sub_similarities)
            sub_categories_result[major_cat] = sub_scores
        
        return CategoryAnalysisResult(
            major_categories=major_scores,
            sub_categories=sub_categories_result,
            debug_similarities=major_similarities  # 디버깅용 원시 유사도 추가
        )
    
    def _calculate_cosine_similarity(self, embedding1: np.ndarray, embedding2: np.ndarray) -> float:
        """코사인 유사도 계산"""
        embedding1_flat = embedding1.flatten()
        embedding2_flat = embedding2.flatten()
        
        dot_product = np.dot(embedding1_flat, embedding2_flat)
        norm1 = np.linalg.norm(embedding1_flat)
        norm2 = np.linalg.norm(embedding2_flat)
        
        if norm1 == 0 or norm2 == 0:
            return 0.0
        
        return float(dot_product / (norm1 * norm2))
    
    def _calculate_relative_confidence(self, similarities: Dict[str, float]) -> List[CategoryScore]:
        """상대적 신뢰도 계산 (가장 높은 유사도 기준으로 정규화)"""
        if not similarities:
            return []
        
        # 최고값과 최저값 찾기
        max_sim = max(similarities.values())
        min_sim = min(similarities.values())
        
        scores = []
        for category, similarity in similarities.items():
            # 상대적 신뢰도 계산 (0~1 범위)
            if max_sim - min_sim > 0:
                relative_confidence = (similarity - min_sim) / (max_sim - min_sim)
            else:
                relative_confidence = 0.5  # 모든 유사도가 같은 경우
            
            scores.append(CategoryScore(category=category, confidence=relative_confidence))
        
        # 신뢰도 순으로 정렬
        scores.sort(key=lambda x: x.confidence, reverse=True)
        return scores
    
    def _calculate_keyword_score(self, text: str, keywords: List[str]) -> float:
        """키워드 직접 매칭 점수 계산"""
        text_lower = text.lower()
        matched_keywords = 0
        total_keywords = len(keywords)
        
        for keyword in keywords:
            if keyword.lower() in text_lower:
                matched_keywords += 1
        
        # 매칭된 키워드 비율을 0-1 범위로 반환
        return matched_keywords / total_keywords if total_keywords > 0 else 0.0
    
    def _normalize_confidence(self, similarity: float) -> float:
        """유사도를 0-1 범위의 신뢰도로 변환 (더 구별력 있게)"""
        # 코사인 유사도는 보통 0.3~0.95 범위에 있음
        # 0.5 이하는 낮은 신뢰도, 0.8 이상은 높은 신뢰도로 매핑
        if similarity < 0.3:
            return 0.0
        elif similarity > 0.9:
            return 1.0
        else:
            # 0.3~0.9를 0~1로 선형 매핑
            return (similarity - 0.3) / 0.6


# 싱글톤 인스턴스
category_analyzer = CategoryAnalyzer()