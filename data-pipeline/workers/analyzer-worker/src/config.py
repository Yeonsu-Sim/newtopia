from common.config_base import BaseConfig
import os

class AnalyzerConfig(BaseConfig):
    # Kafka topics/group
    SRC_TOPIC   = os.getenv("SRC_TOPIC", "newtopia.news.clean")
    DST_TOPIC   = os.getenv("DST_TOPIC", "newtopia.news.analyzed")
    DLQ_TOPIC   = os.getenv("DLQ_TOPIC", "newtopia.news.clean.dlq")
    GROUP_ID    = os.getenv("GROUP_ID", "newtopia.analyzer.v1")
    AUTO_OFFSET_RESET = os.getenv("AUTO_OFFSET_RESET", "earliest")

    # S3 / MinIO connection
    S3_ENDPOINT   = os.getenv("S3_ENDPOINT", "http://minio:9000")
    S3_ACCESS_KEY = os.getenv("S3_ACCESS_KEY", "minio")
    S3_SECRET_KEY = os.getenv("S3_SECRET_KEY", "minio123")
    S3_BUCKET     = os.getenv("S3_BUCKET", "newtopia")
    S3_REGION     = os.getenv("S3_REGION", "us-east-1")
    S3_PATH_STYLE = os.getenv("S3_PATH_STYLE", "true").lower() == "true"
    S3_SSL_ENABLED = os.getenv("S3_SSL_ENABLED", "false").lower() == "true"

    # S3 prefixes
    S3_PREFIX_ANALYZED = os.getenv("S3_PREFIX_ANALYZED", "news/analyzed")
    S3_PREFIX_REJECTED = os.getenv("S3_PREFIX_REJECTED", "news/rejected")
    S3_PREFIX_DLQ      = os.getenv("S3_PREFIX_DLQ", "news/dlq/topic=newtopia.news.clean")

    # Biz thresholds
    ACCEPTED_MAJORS  = set(os.getenv("ACCEPTED_MAJORS", "economy,defense,publicSentiment,environment").split(","))
    MIN_MAJOR_CONF   = float(os.getenv("MIN_MAJOR_CONF", "0.30"))
    MIN_MINOR_CONF   = float(os.getenv("MIN_MINOR_CONF", "0.30"))
    SAVE_REJECTED_TO_MINIO = os.getenv("SAVE_REJECTED_TO_MINIO", "true").lower() == "true"

    # Tuning
    MAX_RECORDS     = int(os.getenv("MAX_RECORDS", "200"))
    POLL_TIMEOUT_MS = int(os.getenv("POLL_TIMEOUT_MS", "1000"))
    SEND_RETRIES    = int(os.getenv("SEND_RETRIES", "2"))
    SEND_BACKOFF_MS = int(os.getenv("SEND_BACKOFF_MS", "400"))
    MAX_CONCURRENCY = int(os.getenv("MAX_CONCURRENCY", "1"))

    # Model
    MODEL_NAME       = os.getenv("MODEL_NAME", "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")
    CLASSIFY_MAX_WORKERS = int(os.getenv("CLASSIFY_MAX_WORKERS", "4"))

cfg = AnalyzerConfig()
