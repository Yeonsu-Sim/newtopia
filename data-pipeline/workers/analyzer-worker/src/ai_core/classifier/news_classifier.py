# file: src/ai_core/classifier/news_classifier.py
import numpy as np
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
from typing import Dict, List, Tuple
import logging
import asyncio
from concurrent.futures import ThreadPoolExecutor
import threading

from ai_core.classifier.categories import CATEGORY_DEFINITIONS
from ai_core.classifier.preprocessor import TextPreprocessor
from ai_core.classifier.schemas import NewsItemWithCategories


class NewsClassifier:
    def __init__(self, model_name: str = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2", max_workers: int = 4):
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
        with self._model_lock:
            if self.model is None:
                self.logger.info(f"Loading model: {self.model_name}")
                import torch
                device = 'cuda' if torch.cuda.is_available() else 'cpu'
                self.model = SentenceTransformer(self.model_name, device=device)
                device = self.model.device
                self.logger.info(f"Model loaded on device: {device}")
                self._prepare_category_embeddings()
    
    def _prepare_category_embeddings(self):
        self.logger.info("Preparing category embeddings...")
        for category, info in CATEGORY_DEFINITIONS.items():
            category_text = f"{info['description']} {' '.join(info['keywords'])}"
            embedding = self.model.encode([category_text])[0]
            self.category_embeddings[category] = embedding
            
            self.subcategory_embeddings[category] = {}
            for subcategory, sub_info in info['subcategories'].items():
                sub_text = f"{sub_info['description']} {' '.join(sub_info['keywords'])}"
                sub_embedding = self.model.encode([sub_text])[0]
                self.subcategory_embeddings[category][subcategory] = sub_embedding
    
    def _calculate_similarity(self, news_embedding: np.ndarray, target_embedding: np.ndarray) -> float:
        similarity = cosine_similarity(
            news_embedding.reshape(1, -1), 
            target_embedding.reshape(1, -1)
        )[0][0]
        return float(similarity)
    
    def _classify_single_news(self, title: str, article: str) -> NewsItemWithCategories:
        processed_text = self.preprocessor.prepare_for_embedding(title, article)
        news_embedding = self.model.encode([processed_text])[0]
        
        main_categories = []
        subcategories = []
        
        for category in CATEGORY_DEFINITIONS.keys():
            category_similarity = self._calculate_similarity(
                news_embedding, 
                self.category_embeddings[category]
            )
            if category_similarity >= 0.3:
                main_categories.append(category)
                
                for subcategory in CATEGORY_DEFINITIONS[category]['subcategories'].keys():
                    sub_similarity = self._calculate_similarity(
                        news_embedding,
                        self.subcategory_embeddings[category][subcategory]
                    )
                    if sub_similarity >= 0.3:
                        subcategories.append(subcategory)
        
        if not main_categories and not subcategories:
            return None
        
        return NewsItemWithCategories(
            title=title,
            article=article,
            main_categories=main_categories,
            subcategories=subcategories
        )
    
    def classify_news(self, title: str, article: str) -> NewsItemWithCategories:
        if self.model is None:
            self.load_model()
        return self._classify_single_news(title, article)
    
    async def classify_news_async(self, title: str, article: str) -> NewsItemWithCategories:
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(
            self.executor, 
            self.classify_news, 
            title, 
            article
        )
    
    def classify_batch(self, news_list: List[Tuple[str, str]]) -> List[NewsItemWithCategories]:
        if self.model is None:
            self.load_model()
        
        results = []
        for title, article in news_list:
            result = self._classify_single_news(title, article)
            if result is not None:
                results.append(result)
        return results
    
    def _classify_batch_optimized(self, news_list: List[Tuple[str, str]]) -> List[NewsItemWithCategories]:
        if self.model is None:
            self.load_model()
        if not news_list:
            return []
        
        processed_texts = []
        for title, article in news_list:
            processed_text = self.preprocessor.prepare_for_embedding(title, article)
            processed_texts.append(processed_text)
        
        news_embeddings = self.model.encode(processed_texts)
        
        results = []
        for i, (title, article) in enumerate(news_list):
            news_embedding = news_embeddings[i]
            main_categories = []
            subcategories = []
            
            for category in CATEGORY_DEFINITIONS.keys():
                category_similarity = self._calculate_similarity(
                    news_embedding, 
                    self.category_embeddings[category]
                )
                if category_similarity >= 0.3:
                    main_categories.append(category)
                    for subcategory in CATEGORY_DEFINITIONS[category]['subcategories'].keys():
                        sub_similarity = self._calculate_similarity(
                            news_embedding,
                            self.subcategory_embeddings[category][subcategory]
                        )
                        if sub_similarity >= 0.3:
                            subcategories.append(subcategory)
            if main_categories or subcategories:
                results.append(NewsItemWithCategories(
                    title=title,
                    article=article,
                    main_categories=main_categories,
                    subcategories=subcategories
                ))
        return results

    async def classify_batch_async(self, news_list: List[Tuple[str, str]]) -> List[NewsItemWithCategories]:
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(
            self.executor,
            self._classify_batch_optimized,
            news_list
        )
    
    async def classify_batch_chunked(self, news_list: List[Tuple[str, str]], chunk_size: int = 10) -> List[NewsItemWithCategories]:
        chunks = [
            news_list[i:i + chunk_size] 
            for i in range(0, len(news_list), chunk_size)
        ]
        total_chunks = len(chunks)
        self.logger.info(f"Processing {len(news_list)} news items in {total_chunks} chunks (chunk_size={chunk_size})")
        
        chunk_tasks = [
            self.classify_batch_async(chunk) 
            for chunk in chunks
        ]
        chunk_results = await asyncio.gather(*chunk_tasks, return_exceptions=True)
        
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
        return CATEGORY_DEFINITIONS
