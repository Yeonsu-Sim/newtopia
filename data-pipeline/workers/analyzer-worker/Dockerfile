# data-pipeline/workers/analyzer-worker/Dockerfile
FROM pytorch/pytorch:2.3.1-cuda12.1-cudnn8-runtime

ENV PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    TZ=Asia/Seoul \
    HF_HUB_DISABLE_TELEMETRY=1 \
    TOKENIZERS_PARALLELISM=false \
    TRANSFORMERS_CACHE=/app/.cache/huggingface \
    HF_HOME=/app/.cache/huggingface \
    PYTHONPATH=/app:/app/src:/app/common

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    git curl ca-certificates build-essential && \
    rm -rf /var/lib/apt/lists/*

# 컨텍스트 = ./data-pipeline
COPY workers/analyzer-worker/requirements.txt /app/requirements.txt
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r /app/requirements.txt

COPY workers/analyzer-worker/src /app/src
COPY common /app/common

# ✔ 캐시 디렉터리 만들고, UID 1000 유저 생성
RUN mkdir -p /app/.cache/huggingface && \
    useradd -m -u 1000 appuser && \
    chown -R appuser:appuser /app

USER appuser
WORKDIR /app/src
CMD ["python", "-m", "worker"]
