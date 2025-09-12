from typing import List, Dict, Optional
from pydantic import BaseModel, Field


class ParsedNewsContent(BaseModel):
    source_url: str = Field(alias="source_url")
    title: str
    content: str
    published_at: str = Field(alias="published_at")

    class Config:
        populate_by_name = True


class CategoryScore(BaseModel):
    category: str = Field(..., description="카테고리 ID")
    confidence: float = Field(..., description="신뢰도 (0.0 ~ 1.0)", ge=0.0, le=1.0)


class CategoryAnalysisResponse(BaseModel):
    major_categories: List[CategoryScore] = Field(..., description="대분류 카테고리별 신뢰도 (신뢰도 순으로 정렬)")
    sub_categories: Dict[str, List[CategoryScore]] = Field(..., description="중분류 카테고리별 신뢰도 (대분류별로 그룹화)")
    debug_similarities: Dict[str, float] = Field(default={}, description="디버깅용 원시 유사도 값들")
    
    class Config:
        json_schema_extra = {
            "example": {
                "major_categories": [
                    {"category": "economy", "confidence": 0.85},
                    {"category": "environment", "confidence": 0.42},
                    {"category": "publicSentiment", "confidence": 0.38},
                    {"category": "defense", "confidence": 0.15}
                ],
                "sub_categories": {
                    "economy": [
                        {"category": "macroEconomy", "confidence": 0.78},
                        {"category": "financialMarket", "confidence": 0.65},
                        {"category": "fiscalPolicy", "confidence": 0.52},
                        {"category": "tradeInvestment", "confidence": 0.41}
                    ],
                    "environment": [
                        {"category": "climateChange", "confidence": 0.55},
                        {"category": "pollutionControl", "confidence": 0.48},
                        {"category": "greenIndustry", "confidence": 0.35},
                        {"category": "naturalResource", "confidence": 0.22}
                    ],
                    "publicSentiment": [
                        {"category": "livingStandard", "confidence": 0.61},
                        {"category": "welfare", "confidence": 0.44},
                        {"category": "publicSafety", "confidence": 0.33},
                        {"category": "socialConflict", "confidence": 0.28}
                    ],
                    "defense": [
                        {"category": "diplomacy", "confidence": 0.31},
                        {"category": "militaryStrength", "confidence": 0.18},
                        {"category": "cybersecurity", "confidence": 0.12},
                        {"category": "northKorea", "confidence": 0.08}
                    ]
                }
            }
        }


class CategoryAnalysisRequest(BaseModel):
    title: str = Field(..., description="뉴스 제목")
    content: str = Field(..., description="뉴스 본문")
    
    class Config:
        json_schema_extra = {
            "example": {
                "title": "한국은행, 기준금리 0.25%p 인하 결정",
                "content": "한국은행 금융통화위원회가 기준금리를 연 3.0%에서 2.75%로 0.25%포인트 인하했다고 발표했다. 이는 경기둔화 우려와 물가 안정세를 고려한 결정으로 분석된다..."
            }
        }