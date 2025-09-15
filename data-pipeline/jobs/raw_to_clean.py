# file: raw_to_clean.py
import os, re, datetime
from typing import List

from pyspark.sql import SparkSession, functions as F, types as T
from config import cfg

# ===== cfg에서 필요한 값 바인딩 =====
KAFKA_BOOTSTRAP  = cfg.KAFKA_BOOTSTRAP
SRC_TOPIC        = cfg.SRC_TOPIC
DST_TOPIC        = cfg.DST_TOPIC
STARTING_OFFSETS = cfg.STARTING_OFFSETS

CHECKPOINT_DIR = cfg.CHECKPOINT_DIR
INDEX_BASE     = cfg.INDEX_BASE
CLEAN_BASE     = cfg.CLEAN_BASE
INDEX_TTL_DAYS = cfg.INDEX_TTL_DAYS

HAMMING_THRESH = cfg.HAMMING_THRESH
BANDS          = cfg.BANDS
BBITS          = cfg.BBITS
NGRAM          = cfg.NGRAM

# ===== helpers =====
def normalize_text(txt: str) -> str:
    if not txt:
        return ""
    txt = re.sub(r"[^0-9A-Za-z가-힣\s]", " ", txt)
    txt = re.sub(r"\s+", " ", txt).strip().lower()
    return txt

def simhash64(s: str, k: int = 4) -> int:
    import mmh3
    s = normalize_text(s)
    if not s:
        return 0
    tokens = [s[i:i+k] for i in range(max(1, len(s)-k+1))]
    vec = [0]*64
    for t in tokens:
        h = mmh3.hash128(t, signed=False)
        val = h & ((1 << 64) - 1)
        for i in range(64):
            vec[i] += 1 if ((val >> i) & 1) else -1
    out = 0
    for i in range(64):
        if vec[i] >= 0:
            out |= (1 << i)
    return out

def split_to_bands(simv: int, bands: int, bbits: int):
    mask = (1 << bbits) - 1
    out = []
    for b in range(bands):
        shift = b * bbits
        prefix = (simv >> shift) & mask
        out.append((b, f"{prefix:0{(bbits+3)//4}x}"))
    return out

def hamming(a: int, b: int) -> int:
    return (a ^ b).bit_count()

def parse_year_from_str(s: str) -> int:
    """
    published_at이 '2025.08.28. 오전 6:53' 처럼 불규칙해도
    앞쪽 4자리 연도(1900~2100)를 안전 추출. 없으면 오늘 연도.
    """
    if not s:
        return datetime.date.today().year
    m = re.search(r"(19\d{2}|20\d{2}|2100)", s)
    if m:
        try:
            y = int(m.group(1))
            if 1900 <= y <= 2100:
                return y
        except:
            pass
    return datetime.date.today().year

# ===== Spark =====
spark = (SparkSession.builder
         .appName("raw-to-clean-simhash-year-partition")
         .getOrCreate())
spark.sparkContext.setLogLevel("WARN")
spark.conf.set("spark.sql.session.timeZone", "UTC")
# 🔸 apply_minio_conf 호출 없음. (엔트리포인트의 --conf로 처리한다고 가정)

# ===== UDF =====
simhash_udf = F.udf(lambda s: simhash64(s, NGRAM), T.LongType())
bands_udf   = F.udf(lambda x: split_to_bands(int(x), BANDS, BBITS),
                    T.ArrayType(T.StructType([
                        T.StructField("band", T.IntegerType()),
                        T.StructField("prefix", T.StringType())
                    ])))
hamming_udf = F.udf(lambda a, b: hamming(int(a), int(b)), T.IntegerType())
year_udf    = F.udf(parse_year_from_str, T.IntegerType())

# ===== schema =====
schema = T.StructType([
    T.StructField("source_url",   T.StringType(), True),
    T.StructField("title",        T.StringType(), True),
    T.StructField("content",      T.StringType(), True),
    T.StructField("published_at", T.StringType(), True),
])

# ===== Kafka source =====
raw_df = (spark.readStream.format("kafka")
          .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP)
          .option("subscribe", SRC_TOPIC)
          .option("startingOffsets", STARTING_OFFSETS)
          .option("failOnDataLoss", "false")
          .load())

json_df = (raw_df
           .select(F.col("value").cast("string").alias("json_str"))
           .select(F.from_json("json_str", schema).alias("data"))
           .select("data.*"))

# ===== foreachBatch (MinIO dedup + year partition) =====
def process_batch(batch_df, epoch_id: int):
    if batch_df.rdd.isEmpty():
        return

    # 1) simhash 계산 (본문 짧은 노이즈 필터)
    docs = (batch_df
            .withColumn("txt", F.concat_ws(" ", "title", "content"))
            .filter(F.length("txt") >= 20)
            .withColumn("simhash64", simhash_udf("txt"))
            .withColumn("year", year_udf(F.col("published_at")))
            .drop("txt"))

    if docs.rdd.isEmpty():
        return

    # 2) bands 분해
    exploded = (docs
                .withColumn("bands", bands_udf("simhash64"))
                .select("*", F.explode("bands").alias("bp"))
                .select("source_url", "title", "content", "published_at",
                        "simhash64", "year",
                        F.col("bp.band").alias("band"), F.col("bp.prefix").alias("prefix")))

    # 3) 과거 인덱스 로드 (TTL→최소 연도)
    today = datetime.date.today()
    min_year = (today - datetime.timedelta(days=INDEX_TTL_DAYS)).year

    try:
        past = (spark.read.format("parquet")
                .load(INDEX_BASE)
                .where(F.col("year") >= F.lit(min_year)))
    except Exception:
        past = spark.createDataFrame([], T.StructType([
            T.StructField("year",         T.IntegerType(), True),
            T.StructField("band",         T.IntegerType(), True),
            T.StructField("prefix",       T.StringType(),  True),
            T.StructField("simhash64",    T.LongType(),    True),
            T.StructField("source_url",   T.StringType(),  True),
            T.StructField("title",        T.StringType(),  True),
            T.StructField("content",      T.StringType(),  True),
            T.StructField("published_at", T.StringType(),  True),
        ]))

    # 4) 후보 조인 (year, band, prefix) → 해밍거리 필터
    candidates = (exploded.alias("cur")
                  .join(past.alias("hist"),
                        on=["year", "band", "prefix"], how="left"))

    dup_keys = (candidates
                .withColumn("hd", hamming_udf(F.col("cur.simhash64"), F.col("hist.simhash64")))
                .where(F.col("hist.simhash64").isNotNull())
                .where(F.col("hd") <= HAMMING_THRESH)
                .select(F.col("cur.source_url").alias("source_url"))
                .distinct())

    # 5) 신규만 선별 (문서 단위)
    new_docs = (docs
                .select("source_url", "title", "content", "published_at", "simhash64", "year")
                .join(dup_keys, on="source_url", how="left_anti")
                .dropDuplicates(["source_url"]))

    if new_docs.rdd.isEmpty():
        return

    # 6) 신규 → Kafka (clean 토픽)
    out_df = new_docs.select(
        F.to_json(F.struct("source_url", "title", "content", "published_at")).alias("value")
    )
    (out_df.select(F.col("value").cast("binary"))
           .write.format("kafka")
           .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP)
           .option("topic", DST_TOPIC)
           .save())

    # 7) 신규 → MinIO (Parquet, year 파티션)
    clean_parquet = (new_docs
                     .withColumn("ingested_at", F.current_timestamp()))
    (clean_parquet
        .repartition("year")
        .write.mode("append")
        .partitionBy("year")
        .format("parquet")
        .save(CLEAN_BASE))

    # 8) 인덱스 append: 신규 문서만 (year/band/prefix 파티션)
    new_index = (exploded
                 .join(new_docs.select("source_url").distinct(), on="source_url", how="inner")
                 .select("year", "band", "prefix", "simhash64",
                         "source_url", "title", "content", "published_at"))

    if not new_index.rdd.isEmpty():
        (new_index
            .repartition("year", "band")
            .write.mode("append")
            .partitionBy("year", "band", "prefix")
            .format("parquet")
            .save(INDEX_BASE))

# ===== start query =====
query = (json_df.writeStream
         .foreachBatch(process_batch)
         .option("checkpointLocation", CHECKPOINT_DIR)
         .start())

query.awaitTermination()
