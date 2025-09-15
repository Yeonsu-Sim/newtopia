import numpy as np
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
from typing import Dict, List, Tuple
import logging
import asyncio
from concurrent.futures import ThreadPoolExecutor
import threading

from config.categories import CATEGORY_DEFINITIONS
from utils.preprocessor import TextPreprocessor
from models.schemas import NewsItemWithCategories


class NewsClassifier:
    def __init__(self, model_name: str = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2", max_workers: int = 4):
        """뉴스 분류기 초기화"""
        self.model_name = model_name
        self.model = None
        self.preprocessor = TextPreprocessor()
        self.category_embeddings = {}
        self.subcategory_embeddings = {}
        self.max_workers = max_workers
        self.executor = ThreadPoolExecutor(max_workers=max_workers)
        self._model_lock = threading.Lock()
        
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(__name__)
    
    def load_model(self):
        """모델 로딩 (스레드 안전)"""
        with self._model_lock:
            if self.model is None:
                self.logger.info(f"Loading model: {self.model_name}")
                # GPU 우선 사용, GPU 없으면 CPU 사용
                import torch
                device = 'cuda' if torch.cuda.is_available() else 'cpu'
                self.model = SentenceTransformer(self.model_name, device=device)
                device = self.model.device
                self.logger.info(f"Model loaded on device: {device}")
                self._prepare_category_embeddings()
    
    def _prepare_category_embeddings(self):
        """카테고리 설명을 임베딩으로 변환"""
        self.logger.info("Preparing category embeddings...")
        
        # 메인 카테고리 임베딩
        for category, info in CATEGORY_DEFINITIONS.items():
            # 카테고리 설명과 키워드를 결합
            category_text = f"{info['description']} {' '.join(info['keywords'])}"
            embedding = self.model.encode([category_text])[0]
            self.category_embeddings[category] = embedding
            
            # 서브카테고리 임베딩
            self.subcategory_embeddings[category] = {}
            for subcategory, sub_info in info['subcategories'].items():
                sub_text = f"{sub_info['description']} {' '.join(sub_info['keywords'])}"
                sub_embedding = self.model.encode([sub_text])[0]
                self.subcategory_embeddings[category][subcategory] = sub_embedding
    
    def _calculate_similarity(self, news_embedding: np.ndarray, target_embedding: np.ndarray) -> float:
        """코사인 유사도 계산"""
        similarity = cosine_similarity(
            news_embedding.reshape(1, -1), 
            target_embedding.reshape(1, -1)
        )[0][0]
        return float(similarity)
    
    def _classify_single_news(self, title: str, article: str) -> NewsItemWithCategories:
        """단일 뉴스 기사 분류"""
        # 텍스트 전처리
        processed_text = self.preprocessor.prepare_for_embedding(title, article)
        
        # 뉴스 임베딩 생성
        news_embedding = self.model.encode([processed_text])[0]
        
        main_categories = []
        subcategories = []
        
        for category in CATEGORY_DEFINITIONS.keys():
            # 메인 카테고리 유사도 계산
            category_similarity = self._calculate_similarity(
                news_embedding, 
                self.category_embeddings[category]
            )
            
            # 임계값 이상이면 메인 카테고리에 포함
            if category_similarity >= 0.3:
                main_categories.append(category)
                
                # 서브카테고리 분석
                for subcategory in CATEGORY_DEFINITIONS[category]['subcategories'].keys():
                    # 서브카테고리 유사도 계산
                    sub_similarity = self._calculate_similarity(
                        news_embedding,
                        self.subcategory_embeddings[category][subcategory]
                    )
                    
                    # 임계값 이상이면 서브카테고리에 포함
                    if sub_similarity >= 0.3:
                        subcategories.append(subcategory)
        
        # 메인카테고리와 서브카테고리가 모두 비어있으면 None 반환
        if not main_categories and not subcategories:
            return None
        
        return NewsItemWithCategories(
            title=title,
            article=article,
            main_categories=main_categories,
            subcategories=subcategories
        )
    
    def classify_news(self, title: str, article: str) -> NewsItemWithCategories:
        """뉴스 분류 메인 메서드"""
        if self.model is None:
            self.load_model()
        
        return self._classify_single_news(title, article)
    
    async def classify_news_async(self, title: str, article: str) -> NewsItemWithCategories:
        """비동기 뉴스 분류"""
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(
            self.executor, 
            self.classify_news, 
            title, 
            article
        )
    
    def classify_batch(self, news_list: List[Tuple[str, str]]) -> List[NewsItemWithCategories]:
        """배치 뉴스 분류 (순차 처리)"""
        if self.model is None:
            self.load_model()
        
        results = []
        for title, article in news_list:
            result = self._classify_single_news(title, article)
            # None이 아닌 결과만 추가 (카테고리가 있는 뉴스만)
            if result is not None:
                results.append(result)
        
        return results
    
    def _classify_batch_optimized(self, news_list: List[Tuple[str, str]]) -> List[NewsItemWithCategories]:
        """최적화된 배치 분류 (GPU 배치 처리)"""
        if self.model is None:
            self.load_model()
        
        if not news_list:
            return []
        
        # 모든 텍스트를 한번에 전처리
        processed_texts = []
        for title, article in news_list:
            processed_text = self.preprocessor.prepare_for_embedding(title, article)
            processed_texts.append(processed_text)
        
        # 배치로 임베딩 생성 (GPU에서 훨씬 효율적)
        news_embeddings = self.model.encode(processed_texts)
        
        results = []
        for i, (title, article) in enumerate(news_list):
            news_embedding = news_embeddings[i]
            
            main_categories = []
            subcategories = []
            
            for category in CATEGORY_DEFINITIONS.keys():
                # 메인 카테고리 유사도 계산
                category_similarity = self._calculate_similarity(
                    news_embedding, 
                    self.category_embeddings[category]
                )
                
                # 임계값 이상이면 메인 카테고리에 포함
                if category_similarity >= 0.3:
                    main_categories.append(category)
                    
                    # 서브카테고리 분석
                    for subcategory in CATEGORY_DEFINITIONS[category]['subcategories'].keys():
                        # 서브카테고리 유사도 계산
                        sub_similarity = self._calculate_similarity(
                            news_embedding,
                            self.subcategory_embeddings[category][subcategory]
                        )
                        
                        # 임계값 이상이면 서브카테고리에 포함
                        if sub_similarity >= 0.3:
                            subcategories.append(subcategory)
            
            # 메인카테고리와 서브카테고리가 모두 비어있지 않으면 결과에 추가
            if main_categories or subcategories:
                results.append(NewsItemWithCategories(
                    title=title,
                    article=article,
                    main_categories=main_categories,
                    subcategories=subcategories
                ))
        
        return results

    async def classify_batch_async(self, news_list: List[Tuple[str, str]]) -> List[NewsItemWithCategories]:
        """비동기 배치 뉴스 분류 (최적화된 GPU 배치 처리)"""
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(
            self.executor,
            self._classify_batch_optimized,
            news_list
        )
    
    async def classify_batch_chunked(self, news_list: List[Tuple[str, str]], chunk_size: int = 10) -> List[NewsItemWithCategories]:
        """청크 단위 배치 분류 (청크들도 병렬 처리)"""
        # 청크들을 생성
        chunks = [
            news_list[i:i + chunk_size] 
            for i in range(0, len(news_list), chunk_size)
        ]
        
        total_chunks = len(chunks)
        self.logger.info(f"Processing {len(news_list)} news items in {total_chunks} chunks (chunk_size={chunk_size})")
        
        # 모든 청크를 병렬로 처리
        chunk_tasks = [
            self.classify_batch_async(chunk) 
            for chunk in chunks
        ]
        
        # 모든 청크 결과를 동시에 기다림
        chunk_results = await asyncio.gather(*chunk_tasks, return_exceptions=True)
        
        # 결과 합치기
        all_results = []
        for i, chunk_result in enumerate(chunk_results):
            if isinstance(chunk_result, Exception):
                self.logger.error(f"Error in chunk {i+1}: {chunk_result}")
            else:
                all_results.extend(chunk_result)
                self.logger.info(f"Completed chunk {i+1}/{total_chunks} with {len(chunk_result)} results")
        
        self.logger.info(f"Total processed: {len(all_results)} successful classifications")
        return all_results
    
    def get_category_info(self) -> Dict:
        """카테고리 정보 반환"""
        return CATEGORY_DEFINITIONS