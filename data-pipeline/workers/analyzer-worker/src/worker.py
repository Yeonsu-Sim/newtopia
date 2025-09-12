# file: data-pipeline/workers/analyzer-worker/src/worker.py
# -*- coding: utf-8 -*-
import asyncio
import json
import logging
import time
import hashlib
from datetime import datetime, timezone
from typing import Any, Dict, Optional, Tuple

from aiokafka import AIOKafkaConsumer, AIOKafkaProducer

import boto3
from botocore.config import Config as BotoConfig
from botocore.exceptions import ClientError

# 🔹 프로젝트 내부 모듈
from ai_core.category_analyzer import CategoryAnalyzer  # 4×4 체계 반영
from ai_core.sentiment_analyzer import SentimentAnalyzer

# 🔹 서비스별 설정 (레이어드 config)
from config import cfg  # data-pipeline/workers/analyzer-worker/src/config.py 에서 제공


# --------------------
# 유틸
# --------------------
def now_iso_utc() -> str:
    return datetime.now(timezone.utc).isoformat()


def today_utc() -> str:
    return datetime.now(timezone.utc).strftime("%Y-%m-%d")


def ensure_article_id(rec: Dict[str, Any]) -> str:
    """
    article_id가 없으면 source_url(+보조필드)로 결정적 해시 생성
    """
    if rec.get("article_id"):
        return str(rec["article_id"])
    src = rec.get("source_url") or (rec.get("title", "") + rec.get("published_at", ""))
    if not src:
        src = str(int(time.time() * 1000))
    return hashlib.sha1(src.encode("utf-8")).hexdigest()[:20]


def build_analyzed_key(major: str, article_id: str, dt: Optional[str] = None) -> str:
    dt = dt or today_utc()
    prefix = cfg.S3_PREFIX_ANALYZED.rstrip("/")
    return f"{prefix}/major={major}/dt={dt}/{article_id}.json"


def build_rejected_key(article_id: str, dt: Optional[str] = None) -> str:
    dt = dt or today_utc()
    prefix = cfg.S3_PREFIX_REJECTED.rstrip("/")
    return f"{prefix}/dt={dt}/{article_id}.json"


def build_dlq_key(article_id: str, dt: Optional[str] = None) -> str:
    dt = dt or today_utc()
    prefix = cfg.S3_PREFIX_DLQ.rstrip("/")
    return f"{prefix}/dt={dt}/{article_id}.json"


def top_major_and_conf(categories: Dict[str, Any]) -> Tuple[Optional[str], float]:
    try:
        arr = categories.get("major_categories") or []
        if not arr:
            return None, 0.0
        top = arr[0]
        return top.get("category"), float(top.get("confidence", 0.0))
    except Exception:
        return None, 0.0


# --------------------
# S3(=MinIO) 클라이언트
# --------------------
def build_s3_client():
    """
    cfg.S3_* 설정(https, path-style 등)을 반영한 boto3 client 생성
    """
    return boto3.client(
        "s3",
        endpoint_url=cfg.S3_ENDPOINT,
        aws_access_key_id=cfg.S3_ACCESS_KEY,
        aws_secret_access_key=cfg.S3_SECRET_KEY,
        region_name=cfg.S3_REGION,
        verify=cfg.S3_SSL_ENABLED,  # True면 서버 인증서 검증
        config=BotoConfig(s3={"addressing_style": "path" if cfg.S3_PATH_STYLE else "virtual"}),
    )


def s3_put_json(s3, key: str, obj: Dict[str, Any], meta: Optional[Dict[str, str]] = None):
    extra = {"ContentType": "application/json"}
    if meta:
        extra["Metadata"] = meta
    s3.put_object(
        Bucket=cfg.S3_BUCKET,
        Key=key,
        Body=json.dumps(obj, ensure_ascii=False).encode("utf-8"),
        **extra,
    )


# --------------------
# Kafka 공통 설정
# --------------------
def _kafka_common_conf() -> Dict[str, Any]:
    conf: Dict[str, Any] = {
        "bootstrap_servers": cfg.KAFKA_BOOTSTRAP,
        "client_id": f"{cfg.GROUP_ID}-client",
        "request_timeout_ms": 30000,
        "retry_backoff_ms": cfg.SEND_BACKOFF_MS,
    }
    # 선택: 보안(SASL_SSL 등) 적용
    proto = getattr(cfg, "KAFKA_SECURITY_PROTOCOL", "PLAINTEXT")
    if proto and proto != "PLAINTEXT":
        conf["security_protocol"] = proto  # e.g. SASL_SSL
        mech = getattr(cfg, "KAFKA_SASL_MECHANISM", None)
        if mech:
            conf["sasl_mechanism"] = mech  # e.g. SCRAM-SHA-512
        # username/password를 별도 제공하는 경우
        user = getattr(cfg, "KAFKA_SASL_USERNAME", None)
        pwd = getattr(cfg, "KAFKA_SASL_PASSWORD", None)
        if user is not None and pwd is not None:
            conf["sasl_plain_username"] = user
            conf["sasl_plain_password"] = pwd
    return conf


def build_consumer() -> AIOKafkaConsumer:
    conf = _kafka_common_conf()
    return AIOKafkaConsumer(
        cfg.SRC_TOPIC,
        group_id=cfg.GROUP_ID,
        auto_offset_reset=cfg.AUTO_OFFSET_RESET,
        enable_auto_commit=False,  # 처리 성공 후 수동 커밋 권장
        value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        key_deserializer=lambda v: v.decode("utf-8") if v else None,
        **conf,
    )


def build_producer() -> AIOKafkaProducer:
    def _key_serializer(v):
        if v is None:
            return None
        if isinstance(v, bytes):
            return v
        if isinstance(v, str):
            return v.encode("utf-8")
        return str(v).encode("utf-8")

    conf = _kafka_common_conf()
    return AIOKafkaProducer(
        key_serializer=_key_serializer,
        value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode("utf-8"),
        **conf,
    )


# --------------------
# Kafka send with retry
# --------------------
async def send_with_retry(
    producer: AIOKafkaProducer, topic: str, value: Dict[str, Any], key: Optional[str] = None
):
    last = None
    for i in range(cfg.SEND_RETRIES + 1):
        try:
            await producer.send_and_wait(
                topic,
                key=key,  # serializer가 처리
                value=value,
                headers=[("pipeline_stage", b"category+sentiment")],
            )
            return
        except Exception as e:
            last = e
            if i < cfg.SEND_RETRIES:
                await asyncio.sleep((cfg.SEND_BACKOFF_MS / 1000.0) * (2**i))
            else:
                raise last


# --------------------
# 인프로세스 분석기 (전역 1회 초기화)
# --------------------
cat_analyzer: Optional[CategoryAnalyzer] = None
sent_analyzer: Optional[SentimentAnalyzer] = None
sem = asyncio.Semaphore(cfg.MAX_CONCURRENCY)


async def init_analyzers_once():
    global cat_analyzer, sent_analyzer
    if cat_analyzer is None:
        cat_analyzer = CategoryAnalyzer()
        await cat_analyzer.initialize()
        logging.getLogger("analyzer-worker").info("CategoryAnalyzer initialized")
    if sent_analyzer is None:
        sent_analyzer = SentimentAnalyzer()
        await sent_analyzer.initialize()
        logging.getLogger("analyzer-worker").info("SentimentAnalyzer initialized")


def build_output(req: Dict[str, Any], cat_res, sent_res) -> Dict[str, Any]:
    return {
        "source_url": req.get("source_url"),
        "title": req.get("title"),
        "content": req.get("content"),
        "published_at": req.get("published_at"),
        "categories": {
            "major_categories": [
                {"category": s.category, "confidence": s.confidence} for s in cat_res.major_categories
            ],
            "sub_categories": {
                k: [{"category": s.category, "confidence": s.confidence} for s in v]
                for k, v in cat_res.sub_categories.items()
            },
            "debug_similarities": getattr(cat_res, "debug_similarities", {}) or {},
        },
        "sentiment": {
            "positive": float(sent_res.get("positive", 0.0)),
            "negative": float(sent_res.get("negative", 0.0)),
        },
    }


# --------------------
# 메인 루프
# --------------------
async def run():
    logger = logging.getLogger("analyzer-worker")
    # 분석기 1회 초기화
    await init_analyzers_once()

    consumer = build_consumer()
    producer = build_producer()
    await consumer.start()
    await producer.start()

    logger.info(
        "Consuming from %s | Producing to %s | DLQ=%s | Bootstrap=%s | Group=%s",
        cfg.SRC_TOPIC,
        cfg.DST_TOPIC,
        cfg.DLQ_TOPIC,
        cfg.KAFKA_BOOTSTRAP,
        cfg.GROUP_ID,
    )

    s3 = build_s3_client()

    # 버킷 존재 확인/생성
    try:
        s3.head_bucket(Bucket=cfg.S3_BUCKET)
    except ClientError:
        create_args = {"Bucket": cfg.S3_BUCKET}
        if getattr(cfg, "S3_REGION", None) and cfg.S3_REGION != "us-east-1":
            create_args["CreateBucketConfiguration"] = {"LocationConstraint": cfg.S3_REGION}
        s3.create_bucket(**create_args)
        logger.info("Created MinIO bucket: %s", cfg.S3_BUCKET)

    try:
        while True:
            batches = await consumer.getmany(timeout_ms=cfg.POLL_TIMEOUT_MS, max_records=cfg.MAX_RECORDS)

            for _tp, records in batches.items():
                if not records:
                    continue

                for msg in records:
                    rec = msg.value  # 예상 필드: source_url, title, content, published_at, (article_id?)
                    article_id = ensure_article_id(rec)

                    req = {
                        "source_url": rec.get("source_url"),
                        "title": rec.get("title"),
                        "content": rec.get("content"),
                        "published_at": rec.get("published_at"),
                    }

                    try:
                        # 🔹 인프로세스 분석 호출 (동시성 제한)
                        async with sem:
                            cat_res = cat_analyzer.analyze_news(req["title"], req["content"])
                            sent_res = sent_analyzer.analyze(req["content"] or req["title"] or "")
                            out = build_output(req, cat_res, sent_res)

                        # 🔹 4대분류 + 임계치 필터
                        categories = out.get("categories", {}) or {}
                        major, conf = top_major_and_conf(categories)

                        if (not major) or (major not in cfg.ACCEPTED_MAJORS) or (conf < cfg.MIN_MAJOR_CONF):
                            # 분류 불가 → Kafka 미전송, (옵션) MinIO rejected 기록
                            if cfg.SAVE_REJECTED_TO_MINIO:
                                key = build_rejected_key(article_id)
                                payload = {
                                    "article_id": article_id,
                                    **req,
                                    "reason": f"rejected_major={major}, conf={conf}",
                                    "processed_at": now_iso_utc(),
                                }
                                s3_put_json(
                                    s3,
                                    key,
                                    payload,
                                    meta={"pipeline-stage": "rejected", "reason": "not-classified-or-low-confidence"},
                                )
                            # 처리 완료로 간주하고 커밋
                            await consumer.commit()
                            continue

                        # 🔹 성공 결과(분류+감정) → Kafka & MinIO
                        enriched = dict(out)
                        enriched["article_id"] = article_id
                        enriched["processed_at"] = now_iso_utc()

                        # Kafka
                        await send_with_retry(producer, cfg.DST_TOPIC, enriched, key=article_id)

                        # MinIO
                        dt = today_utc()
                        key = build_analyzed_key(major, article_id, dt=dt)
                        s3_put_json(s3, key, enriched, meta={"pipeline-stage": "analyzed", "major": major})

                        # 🔹 성공 후 커밋
                        await consumer.commit()

                    except Exception as e:
                        logger.exception("processing failed: %s", e)
                        dlq_payload = {
                            "error": str(e),
                            "raw": rec,
                            "article_id": article_id,
                            "ts": int(time.time() * 1000),
                        }
                        # DLQ 전송 실패해도 무한재시도 방지를 위해 커밋은 수행
                        try:
                            await send_with_retry(producer, cfg.DLQ_TOPIC, dlq_payload, key=article_id)
                        finally:
                            try:
                                s3_put_json(s3, build_dlq_key(article_id), dlq_payload, meta={"pipeline-stage": "dlq"})
                            except Exception:
                                logger.warning("failed to mirror DLQ to MinIO (ignored)")
                            await consumer.commit()

            # 폴링 결과가 비어있으면 살짝 대기 (busy loop 방지)
            if not any(batches.values()):
                await asyncio.sleep(0.05)

    finally:
        await consumer.stop()
        await producer.stop()


# --------------------
# Entrypoint
# --------------------
if __name__ == "__main__":
    logging.basicConfig(level=cfg.LOG_LEVEL, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")
    asyncio.run(run())
