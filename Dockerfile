FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp -DskipTests package

RUN apt-get update && apt-get install -y curl
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 \
    --create-home appuser

WORKDIR /app

COPY --from=build \
    /workspace/target/apex-operations-api-1.0.0.jar \
    /app/app.jar

COPY opentelemetry-javaagent.jar \
    /opt/otel/opentelemetry-javaagent.jar

ENV OTEL_SERVICE_NAME=apex-operations-api \
    OTEL_EXPORTER_OTLP_PROTOCOL=grpc \
    OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317 \
    OTEL_TRACES_EXPORTER=otlp \
    OTEL_METRICS_EXPORTER=none \
    OTEL_LOGS_EXPORTER=none

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
    "-javaagent:/opt/otel/opentelemetry-javaagent.jar", \
    "-jar", \
    "/app/app.jar"]