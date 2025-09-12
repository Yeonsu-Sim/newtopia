from pydantic import BaseModel, Field
from typing import Dict, List


class ClassificationResult(BaseModel):
    main_categories: List[str] = Field(default_factory=list)
    subcategories: List[str] = Field(default_factory=list)


class NewsItemWithCategories(BaseModel):
    title: str = Field(..., min_length=1, max_length=500)
    article: str = Field(..., min_length=1, max_length=10000)
    main_categories: List[str] = Field(default_factory=list)
    subcategories: List[str] = Field(default_factory=list)


class NewsItem(BaseModel):
    title: str = Field(..., min_length=1, max_length=500)
    article: str = Field(..., min_length=1, max_length=10000)
    

class ClassificationRequest(BaseModel):
    news: NewsItem
    

class BatchClassificationRequest(BaseModel):
    news_list: list[NewsItem] = Field(..., min_items=1, max_items=100)


class ClassificationResponse(BaseModel):
    result: NewsItemWithCategories
    processing_time: float


class BatchClassificationResponse(BaseModel):
    results: list[NewsItemWithCategories]
    processing_time: float
    total_processed: int


class CategoryInfo(BaseModel):
    description: str
    keywords: list[str]
    subcategories: Dict[str, Dict[str, str]]


class CategoriesResponse(BaseModel):
    categories: Dict[str, CategoryInfo]