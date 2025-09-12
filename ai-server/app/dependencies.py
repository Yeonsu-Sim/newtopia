from functools import lru_cache
from typing import Dict, Any, Optional
import logging
import os

from .models.loader.kobert_loader import KoBERTLoader
from .models.service.kobert_service import KoBERTService
from .models.service.category_analyzer import CategoryAnalyzer
from .models.service.sentiment_analyzer import SentimentAnalyzer  # ✅ 추가

logger = logging.getLogger(__name__)

# =========================
# Global singletons
# =========================
_kobert_loader: Optional[KoBERTLoader] = None
_kobert_service: Optional[KoBERTService] = None
_category_analyzer: Optional[CategoryAnalyzer] = None
_sentiment_analyzer: Optional[SentimentAnalyzer] = None  # ✅ 추가


# =========================
# KoBERT Loader / Service
# =========================
@lru_cache()
def get_kobert_loader() -> KoBERTLoader:
    """KoBERT 로더 의존성 제공 (싱글톤)"""
    global _kobert_loader
    if _kobert_loader is None:
        _kobert_loader = KoBERTLoader()
        # 모델 로드 (싱글톤이므로 한 번만 실행됨)
        _kobert_loader.get_model_and_tokenizer()
        logger.info("KoBERT loader initialized")
    return _kobert_loader


@lru_cache()
def get_kobert_service() -> KoBERTService:
    """KoBERT 서비스 의존성 제공 (싱글톤)"""
    global _kobert_service
    if _kobert_service is None:
        loader = get_kobert_loader()  # 로더 먼저 초기화
        _kobert_service = KoBERTService()
        # 동기적으로 초기화
        _kobert_service.loader = loader
        _kobert_service.is_ready = True
        logger.info("KoBERT service initialized")
    return _kobert_service


# =========================
# Category Analyzer
# =========================
def get_category_analyzer() -> CategoryAnalyzer:
    """
    카테고리 분석기 의존성 제공
    - 매 호출마다 새 인스턴스를 생성(키워드/가중치 갱신 반영 목적)
    - 실제 모델/리소스는 KoBERTService 싱글톤을 참조
    """
    global _category_analyzer
    service = get_kobert_service()  # 서비스 먼저 초기화
    _category_analyzer = CategoryAnalyzer()
    _category_analyzer.service = service
    logger.info("Category analyzer re-initialized")
    return _category_analyzer


# =========================
# Sentiment Analyzer (KoBERT/HF)
# =========================
@lru_cache()
def get_sentiment_analyzer() -> SentimentAnalyzer:
    """
    감정분석기 의존성 제공 (싱글톤)
    - 기본적으로 내부에서 HuggingFace/KoBERT 기반 모델을 로드
    - 환경변수로 모델/라벨 순서를 제어할 수 있음
        * SENTIMENT_MODEL_NAME: HF 모델 ID 또는 로컬 경로 (예: /models/kobert-sentiment-v1)
        * SENTIMENT_LABEL_ORDER: "negative,neutral,positive" 처럼 라벨 순서 명시
        * SENTIMENT_DEVICE: "cuda" 또는 "cpu" (미설정 시 자동 결정)
    - 실제 로딩( initialize() )은 엔드포인트에서 최초 호출 시 await로 수행(지연 로딩)
    """
    global _sentiment_analyzer
    if _sentiment_analyzer is None:
        model_name = os.getenv("SENTIMENT_MODEL_NAME", "").strip() or None
        _sentiment_analyzer = SentimentAnalyzer(model_name=model_name)
        logger.info(
            "Sentiment analyzer created (model=%s)",
            model_name or "kobert-fallback"
        )
    return _sentiment_analyzer


# =========================
# Health / Status
# =========================
def get_service_status() -> Dict[str, Any]:
    """
    전체 서비스 상태 확인
    - 주의: CategoryAnalyzer / SentimentAnalyzer는 실제 initialize() 호출 전에는
      _is_initialized 가 False일 수 있습니다(지연 로딩).
    """
    return {
        "kobert_loader": _kobert_loader is not None and _kobert_loader.is_loaded(),
        "kobert_service": _kobert_service is not None and _kobert_service.is_ready,
        "category_analyzer": _category_analyzer is not None and getattr(_category_analyzer, "_is_initialized", False),
        "sentiment_analyzer": _sentiment_analyzer is not None and getattr(_sentiment_analyzer, "_is_initialized", False),  # ✅ 추가
    }
