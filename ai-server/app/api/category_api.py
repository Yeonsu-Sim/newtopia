from fastapi import APIRouter, HTTPException, Depends
from pydantic import BaseModel
from typing import Dict, List, Optional, Any
import logging
import inspect

from ..models.dto.category_dto import (
    CategoryAnalysisRequest,
    CategoryAnalysisResponse,
    ParsedNewsContent,
)
from ..models.service.category_analyzer import CategoryAnalyzer, CategoryDefinitions
from ..models.service.sentiment_analyzer import SentimentAnalyzer 
from ..dependencies import get_category_analyzer, get_sentiment_analyzer
from ..models.dto.analysis_dto import SentimentBinary, AnalyzeBothSlimResponse

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/category", tags=["Category Analysis"])

# ===== 통합 응답 DTO =====
class SentimentScores(BaseModel):
    positive: float
    neutral: float
    negative: float

class AnalyzeBothResponse(BaseModel):
    categories: CategoryAnalysisResponse
    sentiment: SentimentScores
    model: Dict[str, str] = {}
    debug: Optional[Dict[str, Any]] = None


@router.post("/analyze", response_model=CategoryAnalysisResponse)
async def analyze_category(
    request: CategoryAnalysisRequest,
    analyzer: CategoryAnalyzer = Depends(get_category_analyzer)
):
    """
    (기존) 카테고리만 분석
    """
    try:
        if not getattr(analyzer, "_is_initialized", False):
            await analyzer.initialize()

        result = analyzer.analyze_news(request.title, request.content)

        return CategoryAnalysisResponse(
            major_categories=[
                {"category": score.category, "confidence": score.confidence}
                for score in result.major_categories
            ],
            sub_categories={
                major_cat: [
                    {"category": score.category, "confidence": score.confidence}
                    for score in scores
                ]
                for major_cat, scores in result.sub_categories.items()
            },
            debug_similarities=result.debug_similarities or {}
        )
    except RuntimeError as e:
        logger.error(f"Category analyzer not ready: {e}")
        raise HTTPException(status_code=503, detail="Category analysis service not ready")
    except Exception as e:
        logger.error(f"Error analyzing category: {e}")
        raise HTTPException(status_code=500, detail=f"Category analysis failed: {str(e)}")


@router.post("/analyze-news", response_model=CategoryAnalysisResponse)
async def analyze_parsed_news(
    news: ParsedNewsContent,
    analyzer: CategoryAnalyzer = Depends(get_category_analyzer)
):
    """
    (기존) 카테고리만 분석 - Java ParsedNewsContent 호환
    """
    try:
        if not getattr(analyzer, "_is_initialized", False):
            await analyzer.initialize()

        result = analyzer.analyze_news(news.title, news.content)

        return CategoryAnalysisResponse(
            major_categories=[
                {"category": score.category, "confidence": score.confidence}
                for score in result.major_categories
            ],
            sub_categories={
                major_cat: [
                    {"category": score.category, "confidence": score.confidence}
                    for score in scores
                ]
                for major_cat, scores in result.sub_categories.items()
            },
            debug_similarities=result.debug_similarities or {}
        )
    except RuntimeError as e:
        logger.error(f"Category analyzer not ready: {e}")
        raise HTTPException(status_code=503, detail="Category analysis service not ready")
    except Exception as e:
        logger.error(f"Error analyzing parsed news: {e}")
        raise HTTPException(status_code=500, detail=f"News analysis failed: {str(e)}")


# ========= 신규: 카테고리 + 감정 통합 =========
@router.post("/analyze-both", response_model=AnalyzeBothSlimResponse)
async def analyze_both(
    news: ParsedNewsContent,  # {source_url,title,content,published_at}
    cat: CategoryAnalyzer = Depends(get_category_analyzer),
    sent: SentimentAnalyzer = Depends(get_sentiment_analyzer),
):
    try:
        if not getattr(cat, "_is_initialized", False):  await cat.initialize()
        if not getattr(sent, "_is_initialized", False): await sent.initialize()

        # 1) 카테고리
        cat_res = cat.analyze_news(news.title, news.content)
        categories = CategoryAnalysisResponse(
            major_categories=[
                {"category": s.category, "confidence": s.confidence}
                for s in cat_res.major_categories
            ],
            sub_categories={
                k: [{"category": s.category, "confidence": s.confidence} for s in v]
                for k, v in cat_res.sub_categories.items()
            },
            debug_similarities=cat_res.debug_similarities or {}
        )

        # 2) 감정(3클래스 → 2클래스로 이진화)
        base = sent.analyze(news.content or news.title or "")
        pos = float(base.get("positive", 0.0))
        neg = float(base.get("negative", 0.0))
        denom = pos + neg if (pos + neg) > 0 else 1.0
        bin_sent = SentimentBinary(
            positive=pos / denom,
            negative=neg / denom,
        )

        return AnalyzeBothSlimResponse(
            source_url=news.source_url,
            title=news.title,
            content=news.content,
            published_at=news.published_at,
            categories=categories,
            sentiment=bin_sent
        )

    except RuntimeError as e:
        logger.error(f"Analyzer not ready: {e}")
        raise HTTPException(status_code=503, detail="Analysis service not ready")
    except Exception as e:
        logger.exception("Analyze-both failed")
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")



@router.get("/categories")
async def get_categories():
    """
    사용 가능한 카테고리 목록 반환
    """
    return {
        "major_categories": {
            cat_id: {
                "name": cat_info["name"],
                "description": cat_info["description"]
            }
            for cat_id, cat_info in CategoryDefinitions.MAJOR_CATEGORIES.items()
        },
        "sub_categories": {
            major_cat: {
                sub_id: {
                    "name": sub_info["name"],
                    "description": sub_info["description"]
                }
                for sub_id, sub_info in sub_cats.items()
            }
            for major_cat, sub_cats in CategoryDefinitions.SUB_CATEGORIES.items()
        }
    }


@router.get("/health")
async def health_check():
    """
    카테고리/감정 분석 준비 상태 확인
    """
    try:
        from ..dependencies import get_service_status
        status = get_service_status()  # 예: {"category": True, "sentiment": True, ...}
        all_ready = all(status.values())
        return {
            "status": "healthy" if all_ready else "initializing",
            "service": "Category/Sentiment Analysis Service",
            "services": status,
            "ready": all_ready
        }
    except Exception as e:
        logger.error(f"Health check failed: {e}")
        return {
            "status": "unhealthy",
            "service": "Category/Sentiment Analysis Service",
            "ready": False,
            "error": str(e)
        }
