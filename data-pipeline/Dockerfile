# Kafka Connect 런타임
FROM confluentinc/cp-kafka-connect:7.5.0

# 버전 고정(필요시 조정)
ARG JDBC_CONNECTOR_VER=10.7.4
ARG PG_JDBC_VER=42.7.4

# JDBC Sink 커넥터 설치 (Confluent Hub)
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:${JDBC_CONNECTOR_VER}

# Postgres JDBC 드라이버 추가
ADD https://repo1.maven.org/maven2/org/postgresql/postgresql/${PG_JDBC_VER}/postgresql-${PG_JDBC_VER}.jar \
    /usr/share/confluent-hub-components/confluentinc-kafka-connect-jdbc/lib/

# 플러그인 경로
ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"
