# Build stage
FROM gradle:8-jdk17 AS builder

WORKDIR /build

# Copy gradle files first for better caching
COPY gradle.properties settings.gradle.kts build.gradle.kts /build/
COPY gradle /build/gradle

# Copy source code
COPY launchers /build/launchers

# Build the connector JAR
RUN gradle :launchers:connector:shadowJar --no-daemon

# Runtime stage - using YOUR original image choice
FROM openjdk:17-slim

# Install curl
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /build/launchers/connector/build/libs/connector.jar /app/

EXPOSE 19191 19193 19194 19199 19291

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl -H "X-Api-Key: $EDC_API_AUTH_KEY" --fail http://localhost:19191/api/check/health

ENV WEB_HTTP_PORT="19191"
ENV WEB_HTTP_PATH="/api"
ENV WEB_HTTP_CONTROL_PORT="19192"
ENV WEB_HTTP_CONTROL_PATH="/api/control"
ENV WEB_HTTP_MANAGEMENT_PORT="19193"
ENV WEB_HTTP_MANAGEMENT_PATH="/api/management"
ENV WEB_HTTP_PROTOCOL_PORT="19194"
ENV WEB_HTTP_PROTOCOL_PATH="/api/dsp"
ENV WEB_HTTP_PUBLIC_PORT="19291"
ENV WEB_HTTP_PUBLIC_PATH="/public"

# Optional JVM arguments
ARG JVM_ARGS=""
ENV JVM_ARGS=$JVM_ARGS

ENTRYPOINT [ "sh", "-c", "exec java $JVM_ARGS -jar connector.jar --log-level=DEBUG"]