# app/models/dto/analysis_dto.py
from typing import Optional
from pydantic import BaseModel
from ..dto.category_dto import CategoryAnalysisResponse  # 기존 DTO 재사용

# 신규 이진 감정 스키마
class SentimentBinary(BaseModel):
    positive: float
    negative: float

# 통합 응답(슬림)
class AnalyzeBothSlimResponse(BaseModel):
    source_url: Optional[str] = None
    title: Optional[str] = None
    content: Optional[str] = None
    published_at: Optional[str] = None
    categories: CategoryAnalysisResponse
    sentiment: SentimentBinary
