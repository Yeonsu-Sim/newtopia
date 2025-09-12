from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import time
import logging
from typing import List

from models.classifier import NewsClassifier
from models.schemas import (
    ClassificationRequest, 
    ClassificationResponse,
    BatchClassificationRequest,
    BatchClassificationResponse,
    CategoriesResponse,
    CategoryInfo
)

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# FastAPI 앱 초기화
app = FastAPI(
    title="News Classification API",
    description="뉴스 기사를 카테고리별로 분류하는 AI 서비스",
    version="1.0.0"
)

# CORS 미들웨어 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 전역 분류기 인스턴스 (더 많은 워커 사용)
classifier = NewsClassifier(max_workers=8)

@app.on_event("startup")
async def startup_event():
    """서버 시작 시 모델 로딩"""
    logger.info("Starting News Classification API...")
    try:
        classifier.load_model()
        logger.info("Model loaded successfully")
    except Exception as e:
        logger.error(f"Failed to load model: {e}")
        raise e

@app.get("/")
async def root():
    """API 루트 엔드포인트"""
    return {
        "message": "News Classification API", 
        "version": "1.0.0",
        "status": "running"
    }

@app.get("/health")
async def health_check():
    """헬스 체크 엔드포인트"""
    return {"status": "healthy", "timestamp": time.time()}

@app.post("/classify", response_model=ClassificationResponse)
async def classify_news(request: ClassificationRequest):
    """단일 뉴스 기사 분류 (비동기)"""
    try:
        start_time = time.time()
        
        # 비동기 분류 수행
        result = await classifier.classify_news_async(
            title=request.news.title,
            article=request.news.article
        )
        
        processing_time = time.time() - start_time
        
        # 카테고리가 없으면 404 반환
        if result is None:
            raise HTTPException(
                status_code=404, 
                detail="No categories found for this news article"
            )
        
        return ClassificationResponse(
            result=result,
            processing_time=round(processing_time, 4)
        )
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Classification error: {e}")
        raise HTTPException(status_code=500, detail=f"Classification failed: {str(e)}")

@app.post("/classify/batch", response_model=BatchClassificationResponse)
async def classify_batch_news(request: BatchClassificationRequest):
    """여러 뉴스 기사 일괄 분류 (병렬 처리)"""
    try:
        start_time = time.time()
        
        # 뉴스 리스트를 튜플 형태로 변환
        news_tuples = [(news.title, news.article) for news in request.news_list]
        
        # 비동기 배치 분류 수행 (병렬 처리)
        results = await classifier.classify_batch_async(news_tuples)
        
        processing_time = time.time() - start_time
        
        return BatchClassificationResponse(
            results=results,
            processing_time=round(processing_time, 4),
            total_processed=len(results)
        )
    
    except Exception as e:
        logger.error(f"Batch classification error: {e}")
        raise HTTPException(status_code=500, detail=f"Batch classification failed: {str(e)}")

@app.post("/classify/batch/chunked")
async def classify_batch_chunked_news(request: BatchClassificationRequest, chunk_size: int = 10):
    """청크 단위 배치 분류 (메모리 효율적 병렬 처리)"""
    try:
        start_time = time.time()
        
        # 뉴스 리스트를 튜플 형태로 변환
        news_tuples = [(news.title, news.article) for news in request.news_list]
        
        # 청크 단위 비동기 배치 분류 수행
        results = await classifier.classify_batch_chunked(news_tuples, chunk_size=chunk_size)
        
        processing_time = time.time() - start_time
        
        return BatchClassificationResponse(
            results=results,
            processing_time=round(processing_time, 4),
            total_processed=len(results)
        )
    
    except Exception as e:
        logger.error(f"Chunked batch classification error: {e}")
        raise HTTPException(status_code=500, detail=f"Chunked batch classification failed: {str(e)}")

@app.get("/categories", response_model=CategoriesResponse)
async def get_categories():
    """지원되는 카테고리 정보 조회"""
    try:
        category_definitions = classifier.get_category_info()
        
        # 응답 형식에 맞게 변환
        categories = {}
        for category, info in category_definitions.items():
            categories[category] = CategoryInfo(
                description=info['description'],
                keywords=info['keywords'],
                subcategories=info['subcategories']
            )
        
        return CategoriesResponse(categories=categories)
    
    except Exception as e:
        logger.error(f"Get categories error: {e}")
        raise HTTPException(status_code=500, detail=f"Failed to get categories: {str(e)}")

@app.get("/model/info")
async def get_model_info():
    """사용 중인 모델 정보 조회"""
    return {
        "model_name": classifier.model_name,
        "status": "loaded" if classifier.model is not None else "not_loaded",
        "categories": list(classifier.get_category_info().keys())
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app", 
        host="0.0.0.0", 
        port=8000, 
        reload=True,
        log_level="info"
    )