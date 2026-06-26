package com.stockservices.sample;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Component
public class ExternalApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiClient.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalApiClient(RestTemplate restTemplate, @Value("${external.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackForGetDataFuture")
    @Retry(name = "externalApi", fallbackMethod = "fallbackForGetDataFuture")
    @TimeLimiter(name = "externalApi", fallbackMethod = "fallbackForGetDataFuture")
    public CompletableFuture<String> getDataFromServiceA() {
        return CompletableFuture.supplyAsync(() ->
            restTemplate.getForObject(baseUrl + "/service-a", String.class)
        );
    }

    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackForGetDataFuture")
    @Retry(name = "externalApi", fallbackMethod = "fallbackForGetDataFuture")
    @TimeLimiter(name = "externalApi", fallbackMethod = "fallbackForGetDataFuture")
    public CompletableFuture<String> getDataFromServiceB() {
        return CompletableFuture.supplyAsync(() ->
            restTemplate.getForObject(baseUrl + "/service-b", String.class)
        );
    }

    public CompletableFuture<String> fallbackForGetDataFuture(Throwable t) {
        logger.error("External call failed (async), using fallback. Reason: {}", t.getMessage());
        return CompletableFuture.completedFuture("Fallback data");
    }
}
