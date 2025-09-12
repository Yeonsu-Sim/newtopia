# data-pipeline/workers/analyzer-worker/Dockerfile
# GPU용: PyTorch + CUDA + cuDNN 런타임 포함 베이스 이미지
FROM pytorch/pytorch:2.3.1-cuda12.1-cudnn8-runtime

ENV PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    TZ=Asia/Seoul \
    HF_HUB_DISABLE_TELEMETRY=1 \
    TOKENIZERS_PARALLELISM=false

WORKDIR /app

# 필수 유틸
RUN apt-get update && apt-get install -y --no-install-recommends \
    git curl ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# 의존성 먼저 복사 → 빌드 캐시 활용
COPY requirements.txt /app/requirements.txt

RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r /app/requirements.txt

# 앱 소스
COPY src /app/src

# 비루트 권장
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

# 실행(compose에서 command를 덮어써도 됨)
WORKDIR /app/src
CMD ["python", "-m", "worker"]
