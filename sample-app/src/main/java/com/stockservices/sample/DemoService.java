package com.stockservices.sample;

import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private final ExternalApiClient externalApiClient;

    public DemoService(ExternalApiClient externalApiClient) {
        this.externalApiClient = externalApiClient;
    }

    public String getMessage() {
        try {
            String serviceA = externalApiClient.getDataFromServiceA().join();
            String serviceB = externalApiClient.getDataFromServiceB().join();
            return "Aggregated: " + serviceA + " & " + serviceB;
        } catch (Exception e) {
            return "Aggregated: Fallback data & Fallback data";
        }
    }
}
