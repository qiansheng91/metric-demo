package com.alibaba.sls.demo.ot.controller;

import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @Autowired
    private OTELMetricsClient metricsClient;

    @RequestMapping("/hello-world")
    public String sayHello() {
        LongHistogram recorder = metricsClient.getHistogram("sayHello");
        long startTime = System.currentTimeMillis();
        Span span = Span.current();
        span.setAttribute("test", span.getSpanContext().getSpanId());
        span.setStatus(StatusCode.ERROR);
        recorder.record(System.currentTimeMillis() - startTime);
        return "hello world";
    }


}
