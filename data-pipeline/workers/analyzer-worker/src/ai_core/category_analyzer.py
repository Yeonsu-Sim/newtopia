# file: data-pipeline/workers/analyzer-worker/src/ai_core/category_analyzer.py
import logging
from typing import Dict, List, Any, Optional
import numpy as np
from dataclasses import dataclass

from .kobert_service import kobert_service, ModelError

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
    # 하이브리드 가중치
    W_KEYWORD: float = 0.6
    W_EMBED: float = 0.4

    def __init__(self):
        self.service = kobert_service
        self._category_embeddings: Dict[str, np.ndarray] = {}
        self._is_initialized: bool = False

    @staticmethod
    def _l2norm(v: np.ndarray) -> np.ndarray:
        v = v.astype(np.float32, copy=False).reshape(-1)
        n = np.linalg.norm(v)
        return v / n if n > 0 else v

    async def initialize(self) -> None:
        """카테고리 임베딩을 미리 계산하여 캐시 (L2 정규화 저장)"""
        self._is_initialized = False
        self._category_embeddings.clear()

        logger.info("Initializing category embeddings...")
        if not self.service.is_ready:
            await self.service.initialize()
            logger.info("KoBERT service is ready: %s", self.service.health_check())

        # 대분류 임베딩
        for cat_id, cat_info in CategoryDefinitions.MAJOR_CATEGORIES.items():
            text = f"{cat_info['name']} {cat_info['description']} {' '.join(cat_info['keywords'])}"
            emb = self.service.encode_text(text)
            self._category_embeddings[f"major_{cat_id}"] = self._l2norm(emb)

        # 중분류 임베딩
        for major_cat, sub_cats in CategoryDefinitions.SUB_CATEGORIES.items():
            for sub_cat_id, sub_cat_info in sub_cats.items():
                key = f"sub_{major_cat}_{sub_cat_id}"
                text = f"{sub_cat_info['name']} {sub_cat_info['description']} {' '.join(sub_cat_info['keywords'])}"
                emb = self.service.encode_text(text)
                self._category_embeddings[key] = self._l2norm(emb)

        self._is_initialized = True
        logger.info("Category embeddings initialized successfully")

    def analyze_news(self, title: Optional[str], content: Optional[str]) -> CategoryAnalysisResult:
        """뉴스 제목과 내용을 분석하여 모든 카테고리의 신뢰도 반환"""
        if not self._is_initialized:
            raise RuntimeError("CategoryAnalyzer not initialized. Call initialize() first.")

        # 입력 정리
        title = title or ""
        content = content or ""
        news_text = f"{title} {content}".strip() or "[UNK]"

        # 뉴스 임베딩 1회 계산 + 정규화
        try:
            news_emb = self._l2norm(self.service.encode_text(news_text))
        except ModelError as e:
            logger.exception("Failed to encode news text: %s", e)
            # 안전하게 모두 0점 반환
            return CategoryAnalysisResult(major_categories=[], sub_categories={}, debug_similarities={})

        # 하이브리드: 키워드(0.6) + 임베딩(0.4)
        # 합이 1이 아니면 정규화
        w_sum = self.W_KEYWORD + self.W_EMBED
        kw_w = self.W_KEYWORD / w_sum
        emb_w = self.W_EMBED / w_sum

        major_similarities: Dict[str, float] = {}
        for cat_id, cat_info in CategoryDefinitions.MAJOR_CATEGORIES.items():
            cat_emb = self._category_embeddings[f"major_{cat_id}"]  # 정규화 벡터
            embedding_sim = float(np.dot(news_emb, cat_emb))  # 코사인 == dot(normalized)
            keyword_score = self._calculate_keyword_score(news_text, cat_info["keywords"])
            hybrid_score = kw_w * keyword_score + emb_w * embedding_sim
            major_similarities[cat_id] = hybrid_score

        logger.debug("Hybrid similarities for '%s...': %s", title[:50], major_similarities)

        major_scores = self._calculate_relative_confidence(major_similarities)

        # 중분류: 각 대분류 아래에서 코사인만 사용(정규화된 벡터끼리 dot)
        sub_categories_result: Dict[str, List[CategoryScore]] = {}
        for major_cat, sub_cats in CategoryDefinitions.SUB_CATEGORIES.items():
            sub_sims: Dict[str, float] = {}
            for sub_cat_id in sub_cats.keys():
                key = f"sub_{major_cat}_{sub_cat_id}"
                sub_emb = self._category_embeddings[key]
                sub_sims[sub_cat_id] = float(np.dot(news_emb, sub_emb))
            sub_categories_result[major_cat] = self._calculate_relative_confidence(sub_sims)

        return CategoryAnalysisResult(
            major_categories=major_scores,
            sub_categories=sub_categories_result,
            debug_similarities=major_similarities
        )

    @staticmethod
    def _calculate_relative_confidence(similarities: Dict[str, float]) -> List[CategoryScore]:
        """상대적 신뢰도 계산 (0~1 스케일링)"""
        if not similarities:
            return []
        max_sim = max(similarities.values())
        min_sim = min(similarities.values())
        denom = (max_sim - min_sim)

        scores: List[CategoryScore] = []
        for category, sim in similarities.items():
            if denom > 1e-12:
                conf = (sim - min_sim) / denom
            else:
                conf = 0.5
            scores.append(CategoryScore(category=category, confidence=float(conf)))
        scores.sort(key=lambda x: x.confidence, reverse=True)
        return scores

    @staticmethod
    def _calculate_keyword_score(text: str, keywords: List[str]) -> float:
        """키워드 직접 매칭 점수 (간단 비율)"""
        if not keywords:
            return 0.0
        t = text.lower()
        hit = sum(1 for kw in keywords if isinstance(kw, str) and kw.lower() in t)
        return float(hit) / float(len(keywords))

    # (참고용) 필요 시 유사도→신뢰도 별도 매핑을 쓸 수 있음
    @staticmethod
    def _normalize_confidence(similarity: float) -> float:
        if similarity < 0.3:
            return 0.0
        if similarity > 0.9:
            return 1.0
        return (similarity - 0.3) / 0.6


# 싱글톤 인스턴스(필요 시 유지)
category_analyzer = CategoryAnalyzer()
