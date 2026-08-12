package com.example.spring_concurrency_demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class ConcurrencyController {

    private static final Logger log = LoggerFactory.getLogger(ConcurrencyController.class);
    private final RestClient restClient;
    private final WebClient webClient;
    private final ExecutorService fixedThreadPool;
    private final ExecutorService virtualThreadPool;

    private static final String MOCK_SERVER_URL = "http://localhost:8081";

    public ConcurrencyController() {
        this.restClient = RestClient.create();
        this.webClient = WebClient.create();
        this.fixedThreadPool = Executors.newFixedThreadPool(200);
        this.virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor();
    }

    // 1. Standard Thread Per Request (Tomcat Default)
    @GetMapping("/standard")
    public String standardCall() {
        // This will block the current Tomcat worker thread
        return restClient.get()
                .uri(MOCK_SERVER_URL)
                .retrieve()
                .body(String.class);
    }

    // 2. Custom Executor Pool
    @GetMapping("/executor")
    public CompletableFuture<String> executorCall() {
        return CompletableFuture.supplyAsync(() -> {
            return restClient.get()
                    .uri(MOCK_SERVER_URL)
                    .retrieve()
                    .body(String.class);
        }, fixedThreadPool);
    }

    // 3. Virtual Thread Per Task Executor
    @GetMapping("/virtual")
    public CompletableFuture<String> virtualCall() {
        return CompletableFuture.supplyAsync(() -> {
            return restClient.get()
                    .uri(MOCK_SERVER_URL)
                    .retrieve()
                    .body(String.class);
        }, virtualThreadPool);
    }

    // 4. Reactive WebClient
    @GetMapping("/reactive")
    public Mono<String> reactiveCall() {
        return webClient.get()
                .uri(MOCK_SERVER_URL)
                .retrieve()
                .bodyToMono(String.class);
    }
}
