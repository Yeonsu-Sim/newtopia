# file: src/ai_core/category_analyzer.py
# -*- coding: utf-8 -*-
from __future__ import annotations
from dataclasses import dataclass
from typing import Dict, List, Optional, Tuple
import logging
import asyncio

import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

from config import cfg
from ai_core.classifier.news_classifier import NewsClassifier
from ai_core.classifier.categories import CATEGORY_DEFINITIONS
from ai_core.classifier.preprocessor import TextPreprocessor


@dataclass
class ScoredLabel:
    category: str
    confidence: float  # 0.0 ~ 1.0


@dataclass
class CategoryAnalyzeResult:
    major_categories: List[ScoredLabel]
    sub_categories: Dict[str, List[ScoredLabel]]  # key = major
    debug_similarities: Dict[str, float]          # per-major cosine scores


class CategoryAnalyzer:
    """
    KoBERT 대신 sentence-transformers MiniLM 임베딩을 사용한 카테고리 분류기 래퍼.
    worker.py가 기대하는 인터페이스(analyze_news)와 결과 포맷을 그대로 제공한다.
    """
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.model: Optional[NewsClassifier] = None
        self.prep = TextPreprocessor()
        # 임계값: config에서 가져오되 기본값과 일치
        self.major_thresh = float(getattr(cfg, "MIN_MAJOR_CONF", 0.30))
        self.minor_thresh = float(getattr(cfg, "MIN_MINOR_CONF", 0.30))
        self.model_name = getattr(cfg, "MODEL_NAME", "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")
        self.max_workers = int(getattr(cfg, "CLASSIFY_MAX_WORKERS", 4))

    async def initialize(self):
        """
        모델과 카테고리 임베딩 준비(워커 시작 시 1회).
        event loop를 블로킹하지 않도록 to_thread로 래핑.
        """
        if self.model is not None:
            return

        def _load():
            m = NewsClassifier(model_name=self.model_name, max_workers=self.max_workers)
            m.load_model()  # 내부에서 category/subcategory 임베딩 준비
            return m

        self.logger.info("Loading sentence-transformers model & category embeddings...")
        self.model = await asyncio.to_thread(_load)
        self.logger.info("CategoryAnalyzer initialized with model: %s", self.model_name)

    def _encode(self, text: str) -> np.ndarray:
        assert self.model is not None, "CategoryAnalyzer not initialized"
        # sentence-transformers encode: (D,) vector
        return self.model.model.encode([text])[0]

    @staticmethod
    def _cos(a: np.ndarray, b: np.ndarray) -> float:
        return float(cosine_similarity(a.reshape(1, -1), b.reshape(1, -1))[0][0])

    def analyze_news(self, title: str, content: str) -> CategoryAnalyzeResult:
        """
        동기 메서드: worker.py에서 직접 호출.
        - 제목+본문을 전처리 후 임베딩
        - 메인/서브 카테고리 유사도 계산 → 임계치 이상만 반환
        - confidence는 cosine(0~1) 그대로 사용 (worker의 MIN_MAJOR_CONF와 호환)
        """
        assert self.model is not None, "CategoryAnalyzer not initialized"

        # 1) 전처리 & 임베딩
        text = self.prep.prepare_for_embedding(title or "", content or "")
        news_vec = self._encode(text)

        # 2) 메인 카테고리 점수
        major_scores: List[Tuple[str, float]] = []
        debug_sim: Dict[str, float] = {}

        for major, emb in self.model.category_embeddings.items():
            score = self._cos(news_vec, emb)
            debug_sim[major] = score
            if score >= self.major_thresh:
                major_scores.append((major, score))

        # 내림차순 정렬
        major_scores.sort(key=lambda x: x[1], reverse=True)

        major_labels = [ScoredLabel(category=m, confidence=float(s)) for m, s in major_scores]

        # 3) 서브카테고리(메이저별로 필터 후 스코어링)
        sub_map: Dict[str, List[ScoredLabel]] = {}
        for major, _ in major_scores:
            subs = CATEGORY_DEFINITIONS[major]["subcategories"].keys()
            sub_scored: List[ScoredLabel] = []
            for sub in subs:
                sub_emb = self.model.subcategory_embeddings[major][sub]
                s = self._cos(news_vec, sub_emb)
                if s >= self.minor_thresh:
                    sub_scored.append(ScoredLabel(category=sub, confidence=float(s)))
            sub_scored.sort(key=lambda x: x.confidence, reverse=True)
            if sub_scored:
                sub_map[major] = sub_scored

        return CategoryAnalyzeResult(
            major_categories=major_labels,
            sub_categories=sub_map,
            debug_similarities=debug_sim
        )
