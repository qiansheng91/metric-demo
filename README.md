# metric-demo

# Quick start

1. Build project
```shell
./mvnw clean package
```

2. Start Project
```shell
export OTEL_EXPORTER_OTLP_ENDPOINT=<YOUR_ENDPOINT>
export OTEL_EXPORTER_OTLP_PROTOCOL=grpc

java -javaagent:$PWD/target/opentelemetry-javaagent.jar -jar $PWD/target/opentelemetry-java.jar
```