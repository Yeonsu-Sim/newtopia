from fastapi import FastAPI
import uvicorn

from .api.category_api import router as category_router

app = FastAPI(
    title="KoBERT FastAPI Server",
    description="KoBERT 기반 한국어 텍스트 분석 API 서버 (의존성 주입 패턴)",
    version="1.0.0"
)

# API 라우터 등록
app.include_router(category_router)

@app.get("/")
async def root():
    return {"message": "KoBERT FastAPI Server"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)