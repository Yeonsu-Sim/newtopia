# workers/sentiment-worker/src/config.py
from common.config_base import BaseConfig
import os

class SentimentConfig(BaseConfig):
    # Kafka topics/group
    SRC_TOPIC   = os.getenv("SRC_TOPIC", "newtopia.news.analyzed")
    DST_TOPIC   = os.getenv("DST_TOPIC", "newtopia.news.sentiment")
    DLQ_TOPIC   = os.getenv("DLQ_TOPIC", "newtopia.news.sentiment.dlq")
    GROUP_ID    = os.getenv("GROUP_ID", "newtopia.sentiment.v1")
    AUTO_OFFSET_RESET = os.getenv("AUTO_OFFSET_RESET", "earliest")

    # S3 / MinIO connection
    S3_BUCKET     = os.getenv("S3_BUCKET", "newtopia")
    S3_REGION     = os.getenv("S3_REGION", "us-east-1")
    S3_SSL_ENABLED = os.getenv("S3_SSL_ENABLED", "false").lower() == "true"

    # S3 prefixes
    S3_PREFIX_SENTIMENT = os.getenv("S3_PREFIX_SENTIMENT", "news/sentiment")
    S3_PREFIX_DLQ       = os.getenv("S3_PREFIX_DLQ", "news/dlq/topic=newtopia.news.sentiment")

    # Biz thresholds (필요하다면 확장)
    MAX_RECORDS     = int(os.getenv("MAX_RECORDS", "200"))
    POLL_TIMEOUT_MS = int(os.getenv("POLL_TIMEOUT_MS", "1000"))
    SEND_RETRIES    = int(os.getenv("SEND_RETRIES", "2"))
    SEND_BACKOFF_MS = int(os.getenv("SEND_BACKOFF_MS", "400"))
    MAX_CONCURRENCY = int(os.getenv("MAX_CONCURRENCY", "1"))

    # Model
    MODEL_NAME   = os.getenv("MODEL_NAME", "snunlp/KR-FinBert-SC")
    USE_CUDA     = os.getenv("USE_CUDA", "false").lower() == "true"
    BATCH_SIZE   = int(os.getenv("BATCH_SIZE", "16"))
    MAX_CONTENT_LEN = int(os.getenv("MAX_CONTENT_LEN", "4000"))
    MAX_SEQ_LEN_TOKENS    = int(os.getenv("MAX_SEQ_LEN_TOKENS", "256"))
    SLIDING_STRIDE_TOKENS = int(os.getenv("SLIDING_STRIDE_TOKENS", "128"))

cfg = SentimentConfig()
