# jobs/config.py
from common.config_base import BaseConfig
import os

class RawToCleanConfig(BaseConfig):
    # Kafka topic
    SRC_TOPIC       = os.getenv("SRC_TOPIC", "newtopia.news.raw")
    DST_TOPIC       = os.getenv("DST_TOPIC", "newtopia.news.clean")
    STARTING_OFFSETS= os.getenv("STARTING_OFFSETS", "earliest")

    # S3 paths
    CHECKPOINT_DIR  = os.getenv("CHECKPOINT_DIR", "s3a://newtopia/ckpt/raw_to_clean")
    INDEX_BASE      = os.getenv("INDEX_BASE", "s3a://newtopia/index/simhash")
    CLEAN_BASE      = os.getenv("CLEAN_BASE", "s3a://newtopia/news/clean")
    INDEX_TTL_DAYS  = int(os.getenv("INDEX_TTL_DAYS", "7"))

    # SimHash params
    HAMMING_THRESH = int(os.getenv("HAMMING_THRESH", "4"))
    BANDS          = int(os.getenv("BANDS", "8"))
    BBITS          = int(os.getenv("BBITS", "8"))
    NGRAM          = int(os.getenv("NGRAM", "4"))

cfg = RawToCleanConfig()
