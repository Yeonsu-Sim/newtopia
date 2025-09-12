# ENTRYPOINT 와 CMD는 해당 컨테이너가 수행하게 될 실행 명령을 정의하는 선언문이다.
FROM apache/spark-py:v3.4.0
USER root

RUN apt-get update \
 && apt-get install -y --no-install-recommends build-essential python3-dev \
 && rm -rf /var/lib/apt/lists/*

RUN python3 -m pip install --upgrade pip \
 && python3 -m pip install --no-cache-dir "mmh3>=5,<6" redis

WORKDIR /opt/app
COPY jobs/raw_to_clean.py /opt/app/raw_to_clean.py
COPY config.py /opt/app/config.py
COPY entrypoint.sh /opt/app/entrypoint.sh
RUN chmod +x /opt/app/entrypoint.sh

ENV PYSPARK_PYTHON=python3

ENTRYPOINT ["/opt/app/entrypoint.sh"]
