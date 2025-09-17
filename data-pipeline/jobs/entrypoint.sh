#!/bin/sh
set -e

: "${S3_ENDPOINT}"
: "${S3_ACCESS_KEY}"
: "${S3_SECRET_KEY}"
: "${SPARK_UI_PORT:=4040}"

[ -f /opt/app/raw_to_clean.py ] || { echo "raw_to_clean.py missing!"; exit 1; }

exec /opt/spark/bin/spark-submit \
  --conf spark.jars.ivy=/tmp/.ivy2 \
  --conf spark.ui.enabled=true \
  --conf spark.ui.port="${SPARK_UI_PORT}" \
  --conf spark.driver.bindAddress=0.0.0.0 \
  --conf spark.port.maxRetries=50 \
  --conf spark.hadoop.fs.s3a.endpoint="${S3_ENDPOINT}" \
  --conf spark.hadoop.fs.s3a.access.key="${S3_ACCESS_KEY}" \
  --conf spark.hadoop.fs.s3a.secret.key="${S3_SECRET_KEY}" \
  --conf spark.hadoop.fs.s3a.path.style.access=true \
  --conf spark.hadoop.fs.s3a.connection.ssl.enabled=false \
  --conf spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem \
  --conf spark.hadoop.fs.s3a.fast.upload=true \
  --conf spark.hadoop.fs.s3a.aws.credentials.provider=org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider \
  --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.4.0,org.apache.hadoop:hadoop-aws:3.3.4,com.amazonaws:aws-java-sdk-bundle:1.12.262 \
  /opt/app/raw_to_clean.py
