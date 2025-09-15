# common/config_base.py
import os

class BaseConfig:
    # ----- Kafka (공통) -----
    KAFKA_BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP", "kafka:9092")
    KAFKA_SECURITY_PROTOCOL = os.getenv("KAFKA_SECURITY_PROTOCOL", "PLAINTEXT")
    KAFKA_SASL_MECHANISM = os.getenv("KAFKA_SASL_MECHANISM", "")
    KAFKA_SASL_JAAS = os.getenv("KAFKA_SASL_JAAS", "")

    # ----- S3/MinIO (공통) -----
    S3_ENDPOINT   = os.getenv("S3_ENDPOINT", "http://minio:9000")
    S3_ACCESS_KEY = os.getenv("S3_ACCESS_KEY", "minio")
    S3_SECRET_KEY = os.getenv("S3_SECRET_KEY", "minio123")
    S3_PATH_STYLE = os.getenv("S3_PATH_STYLE", "true").lower() == "true"

    # ----- Logging -----
    LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO").upper()
