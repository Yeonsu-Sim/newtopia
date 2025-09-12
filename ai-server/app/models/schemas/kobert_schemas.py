from pydantic import BaseModel, Field
from typing import List, Any, Optional
import numpy as np


class TextRequest(BaseModel):
    text: str = Field(..., description="Input text to process", min_length=1, max_length=1000)


class BatchTextRequest(BaseModel):
    texts: List[str] = Field(..., description="List of texts to process", min_items=1, max_items=100)


class SimilarityRequest(BaseModel):
    text1: str = Field(..., description="First text for comparison", min_length=1, max_length=1000)
    text2: str = Field(..., description="Second text for comparison", min_length=1, max_length=1000)


class EmbeddingResponse(BaseModel):
    text: str
    embedding: List[float] = Field(..., description="Text embedding vector")
    embedding_dimension: int


class BatchEmbeddingResponse(BaseModel):
    results: List[EmbeddingResponse]
    total_count: int


class SimilarityResponse(BaseModel):
    text1: str
    text2: str
    similarity_score: float = Field(..., description="Cosine similarity score between -1 and 1")


class EmbeddingInfoResponse(BaseModel):
    text: str
    embedding_shape: List[int]
    embedding_dimension: int
    embedding_mean: float
    embedding_std: float
    embedding_min: float
    embedding_max: float


class HealthResponse(BaseModel):
    service_name: str
    is_ready: bool
    model_info: Optional[dict] = None
    status: str = Field(default="healthy")


class ErrorResponse(BaseModel):
    error: str
    detail: str
    status_code: int