package com.example.common.chaos.lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
public class ChaosMonkeyFacade {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String CHAOS_MONKEY_ENABLE_URL = "/actuator/chaosmonkey/enable";
    private static final String CHAOS_MONKEY_DISABLE_URL = "/actuator/chaosmonkey/disable";
    private static final String CHAOS_MONKEY_ASSAULTS_URL = "/actuator/chaosmonkey/assaults";

    public void enableChaos() {
        ResponseEntity<String> response = restTemplate.postForEntity(CHAOS_MONKEY_ENABLE_URL, null, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to enable Chaos Monkey: " + response.getStatusCode());
        }
    }

    public void disableChaos() {
        ResponseEntity<String> response = restTemplate.postForEntity(CHAOS_MONKEY_DISABLE_URL, null, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to disable Chaos Monkey: " + response.getStatusCode());
        }
    }

    public void configureLatencyAssault(int minMs, int maxMs) {
        Map<String, Object> assaults = new HashMap<>();
        assaults.put("level", 1);
        assaults.put("latencyRangeStart", minMs);
        assaults.put("latencyRangeEnd", maxMs);
        assaults.put("latencyActive", true);
        assaults.put("exceptionsActive", false);
        assaults.put("killApplicationActive", false);
        assaults.put("memoryActive", false);
        assaults.put("cpuActive", false);

        updateAssaults(assaults);
    }

    public void configureExceptionAssault(String exceptionClassName) {
        Map<String, Object> exceptions = new HashMap<>();
        exceptions.put("type", exceptionClassName);
        exceptions.put("arguments", new Object[0]);

        Map<String, Object> assaults = new HashMap<>();
        assaults.put("level", 1);
        assaults.put("latencyActive", false);
        assaults.put("exceptionsActive", true);
        assaults.put("exception", exceptions);
        assaults.put("killApplicationActive", false);
        assaults.put("memoryActive", false);
        assaults.put("cpuActive", false);

        updateAssaults(assaults);
    }

    public void resetAssaults() {
        Map<String, Object> assaults = new HashMap<>();
        assaults.put("level", 1);
        assaults.put("latencyActive", false);
        assaults.put("exceptionsActive", false);
        assaults.put("killApplicationActive", false);
        assaults.put("memoryActive", false);
        assaults.put("cpuActive", false);

        updateAssaults(assaults);
    }

    private void updateAssaults(Map<String, Object> assaults) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(assaults, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(CHAOS_MONKEY_ASSAULTS_URL, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to update assaults: " + response.getStatusCode());
        }
    }
}
