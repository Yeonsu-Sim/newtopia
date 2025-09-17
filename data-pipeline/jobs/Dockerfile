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
COPY jobs/config.py       /opt/app/config.py
COPY jobs/entrypoint.sh   /opt/app/entrypoint.sh

# 윈도우 개행(CRLF) & BOM 제거 + 실행 권한 부여
RUN sed -i 's/\r$//' /opt/app/entrypoint.sh \
 && sed -i '1s/^\xEF\xBB\xBF//' /opt/app/entrypoint.sh \
 && chmod +x /opt/app/entrypoint.sh

# common 패키지 경로로 config_base.py 배치
RUN mkdir -p /opt/app/common
COPY common/config_base.py /opt/app/common/config_base.py
RUN touch /opt/app/common/__init__.py

ENV PYTHONPATH=/opt/app:/opt/app/common
ENV PYSPARK_PYTHON=python3

ENTRYPOINT ["/opt/app/entrypoint.sh"]
