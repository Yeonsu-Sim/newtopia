# workers/sentiment-worker/src/worker.py
# -*- coding: utf-8 -*-\
import re
import asyncio
import hashlib
import logging
from datetime import datetime, timezone
from typing import Dict, Any, List

import orjson
import boto3
from botocore.config import Config as BotoConfig
from botocore.exceptions import ClientError
from aiokafka import AIOKafkaConsumer, AIOKafkaProducer

from config import cfg                       # SentimentConfig(BaseConfig) 인스턴스
from schemas import is_minimum_valid         # 최소 스키마 검증
from preprocessor import content_cut         # 본문 길이 제한
from ai_core.sentiment_model import load_model, infer_batch, infer_longtext  # FinBERT 로더/배치추론


# -----------------------
# 유틸
# -----------------------
def now_iso_utc() -> str:
    return datetime.now(timezone.utc).isoformat()


def setup_logging():
    logging.basicConfig(
        level=getattr(logging, cfg.LOG_LEVEL, logging.INFO),
        format="%(asctime)s | %(levelname)s | %(name)s | %(message)s",
    )


# -----------------------
# S3 / MinIO
# -----------------------
def make_s3():
    """boto3 resource 생성"""
    session = boto3.session.Session()
    return session.resource(
        "s3",
        endpoint_url=cfg.S3_ENDPOINT,
        aws_access_key_id=cfg.S3_ACCESS_KEY,
        aws_secret_access_key=cfg.S3_SECRET_KEY,
        region_name=getattr(cfg, "S3_REGION", "us-east-1"),
        config=BotoConfig(
            signature_version="s3v4",
            s3={"addressing_style": "path" if cfg.S3_PATH_STYLE else "auto"},
        ),
        verify=cfg.S3_SSL_ENABLED if hasattr(cfg, "S3_SSL_ENABLED") else False,
    )

def _extract_year(published_at: str | None) -> str:
    """
    '2025.08.29. 오전 11:11' 같이 지역 형식이 섞여도 연도 4자리만 뽑아냄.
    실패하면 현재 UTC 연도.
    """
    if published_at:
        m = re.search(r"\b(19|20)\d{2}\b", published_at)
        if m:
            return m.group(0)
    return datetime.now(timezone.utc).strftime("%Y")


def _extract_major(doc: Dict[str, Any]) -> str:
    """
    analyzer가 넣어준 카테고리에서 대분류 추출.
    없으면 'unknown' 폴더로 보냄.
    """
    try:
        majors = doc["categories"]["major_categories"]
        if isinstance(majors, list) and majors:
            c = majors[0].get("category")
            if c:
                return str(c)
    except Exception:
        pass
    return "unknown"



def s3_key_for(doc: Dict[str, Any]) -> str:
    """
    파티셔닝 규칙 변경:
      <prefix>/year=YYYY/major=<major>/<sha1(source_url)>.json
    예) news/sentiment/year=2025/major=economy/3a1f....json
    """
    y = _extract_year(doc.get("published_at"))
    major = _extract_major(doc)
    prefix = getattr(cfg, "S3_PREFIX_SENTIMENT", "news/sentiment")
    url = doc.get("source_url", "")
    h = hashlib.sha1(url.encode("utf-8")).hexdigest() if url else hashlib.sha1(orjson.dumps(doc)).hexdigest()
    return f"{prefix}/year={y}/major={major}/{h}.json"


def put_s3_json(s3, doc: Dict[str, Any]):
    key = s3_key_for(doc)
    body = orjson.dumps(doc)
    s3.Object(cfg.S3_BUCKET, key).put(Body=body, ContentType="application/json")


# -----------------------
# Kafka Helper
# -----------------------
async def send_with_retry(producer, topic, key, value,
                          retries: int = 2, backoff_ms: int = 400):
    """
    Kafka 전송을 재시도하는 헬퍼 (지수 백오프)
    """
    last_exc = None
    for attempt in range(retries + 1):
        try:
            await producer.send_and_wait(topic, value=value, key=key)
            return
        except Exception as e:
            last_exc = e
            if attempt == retries:
                break
            await asyncio.sleep((backoff_ms / 1000.0) * (2 ** attempt))
    raise last_exc


# -----------------------
# 메인 루프
# -----------------------
async def run():
    setup_logging()
    log = logging.getLogger("sentiment-worker")

    # 모델 로드
    tokenizer, model, device = load_model()
    log.info("Model loaded on device=%s, name=%s", device, cfg.MODEL_NAME)

    # S3 핸들러
    s3 = make_s3()
    log.info("S3 ready: endpoint=%s bucket=%s path_style=%s", cfg.S3_ENDPOINT, cfg.S3_BUCKET, cfg.S3_PATH_STYLE)

    # Kafka 준비
    consumer = AIOKafkaConsumer(
        cfg.SRC_TOPIC,
        bootstrap_servers=cfg.KAFKA_BOOTSTRAP,
        group_id=cfg.GROUP_ID,
        enable_auto_commit=True,
        auto_offset_reset=cfg.AUTO_OFFSET_RESET,
        value_deserializer=lambda v: orjson.loads(v) if v else None,
        security_protocol=getattr(cfg, "KAFKA_SECURITY_PROTOCOL", "PLAINTEXT"),
        sasl_mechanism=getattr(cfg, "KAFKA_SASL_MECHANISM", None) or None,
    )
    producer = AIOKafkaProducer(
        bootstrap_servers=cfg.KAFKA_BOOTSTRAP,
        value_serializer=lambda v: orjson.dumps(v),
        security_protocol=getattr(cfg, "KAFKA_SECURITY_PROTOCOL", "PLAINTEXT"),
        sasl_mechanism=getattr(cfg, "KAFKA_SASL_MECHANISM", None) or None,
        linger_ms=getattr(cfg, "SEND_BACKOFF_MS", 400),
    )

    await consumer.start()
    await producer.start()
    log.info("Kafka connected. src=%s dst=%s dlq=%s group=%s",
             cfg.SRC_TOPIC, cfg.DST_TOPIC, cfg.DLQ_TOPIC, cfg.GROUP_ID)

    try:
        while True:
            batches = await consumer.getmany(
                timeout_ms=getattr(cfg, "POLL_TIMEOUT_MS", 1000),
                max_records=getattr(cfg, "BATCH_SIZE", 16),
            )

            items: List[Dict[str, Any]] = []
            for _, records in batches.items():
                for r in records:
                    if r.value:
                        items.append(r.value)

            if not items:
                await asyncio.sleep(0.05)
                continue

            # 스키마 필터링
            valids = [x for x in items if is_minimum_valid(x)]
            if not valids:
                continue

            # 본문 전처리 + 배치 추론
            texts = [content_cut(x.get("content", "")) for x in valids]
            preds = infer_longtext(tokenizer, model, device, texts)

            # 결과 병합 후 전송/저장
            for x, (label, score) in zip(valids, preds):
                # neutral은 드랍 (Kafka, MinIO 모두 미저장)
                if label == "neutral":
                    continue

                x["sentiment"] = {"label": label, "score": round(score, 6)}

                # Kafka 출력 (positive/negative 만)
                try:
                    await send_with_retry(
                        producer,
                        cfg.DST_TOPIC,
                        key=None,
                        value=x,
                        retries=getattr(cfg, "SEND_RETRIES", 2),
                        backoff_ms=getattr(cfg, "SEND_BACKOFF_MS", 400),
                    )
                except Exception as e:
                    dlq = {"error": f"kafka:{e}", "payload": x, "ts": now_iso_utc()}
                    try:
                        await producer.send_and_wait(cfg.DLQ_TOPIC, dlq)
                    except Exception as e2:
                        log.error("DLQ send failed: %s", e2)
                    continue

                # MinIO 스냅샷 저장 (positive/negative 만)
                try:
                    await asyncio.get_event_loop().run_in_executor(None, put_s3_json, s3, x)
                except ClientError as e:
                    dlq = {"error": f"s3:{e}", "payload": x, "ts": now_iso_utc()}
                    try:
                        await producer.send_and_wait(cfg.DLQ_TOPIC, dlq)
                    except Exception as e2:
                        log.error("DLQ send failed(S3): %s", e2)

    finally:
        await consumer.stop()
        await producer.stop()


if __name__ == "__main__":
    try:
        try:
            import uvloop
            uvloop.install()
        except Exception:
            pass
        asyncio.run(run())
    except KeyboardInterrupt:
        pass
