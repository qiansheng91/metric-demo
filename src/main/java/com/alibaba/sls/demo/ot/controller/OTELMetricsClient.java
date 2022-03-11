package com.alibaba.sls.demo.ot.controller;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.MetricReaderFactory;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

/**
 * opentelemetry metrics初始化
 */
@Component
public class OTELMetricsClient implements InitializingBean {
    private static Logger log = LoggerFactory.getLogger(OTELMetricsClient.class);

    private Meter meter;

    private ConcurrentHashMap<String, LongCounter> counterModelMap;
    private ConcurrentHashMap<String, LongHistogram> histogramModelMap;


    public OTELMetricsClient() {
        this.counterModelMap = new ConcurrentHashMap();
        this.histogramModelMap = new ConcurrentHashMap();
    }


    public LongCounter getLongCounter(String name) {
        return counterModelMap.getOrDefault(name, meter.counterBuilder(name).build());
    }

    public LongHistogram getHistogram(String name) {
        return histogramModelMap.getOrDefault(name, meter.histogramBuilder(name).ofLongs().setUnit("ms").build());
    }

    @Override
    public void afterPropertiesSet() {
        String endpoint = System.getenv("ENDPOINT");
        Resource RESOURCE =
                Resource.create(Attributes.of(stringKey("podId"), System.getenv("MY_POD_IP")));

        MetricExporter metricExporter = OtlpGrpcMetricExporter.builder()
                .setEndpoint(endpoint)
                .build();
        MetricReaderFactory readerFactory = PeriodicMetricReader.builder(metricExporter)
                .setInterval(Duration.ofSeconds(1)).newMetricReaderFactory();
        SdkMeterProvider sdkMeterProvider =
                SdkMeterProvider.builder()
                        .registerMetricReader(readerFactory)
                        .setResource(RESOURCE)
                        .build();

        meter = sdkMeterProvider.get(this.getClass().getName());
    }

    public static void main(String[] args) {

    }
}
