package com.example.sample;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.profiles.active=chaos-monkey"
})
@ComponentScan(basePackages = {"com.example.common.chaos.lib", "com.example.sample"})
public class SampleSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> lastResponse;
    private long responseTime;

    @When("I request the message")
    public void iRequestTheMessage() {
        long start = System.currentTimeMillis();
        lastResponse = restTemplate.getForEntity("/api/message", String.class);
        responseTime = System.currentTimeMillis() - start;
    }

    @When("I request the message expecting an error")
    public void iRequestTheMessageExpectingAnError() {
        lastResponse = restTemplate.getForEntity("/api/message", String.class);
    }

    @Then("the response should be {string}")
    public void theResponseShouldBe(String expectedMessage) {
        assertEquals(200, lastResponse.getStatusCodeValue());
        assertEquals(expectedMessage, lastResponse.getBody());
    }

    @Then("the application should return an error status")
    public void theApplicationShouldReturnAnErrorStatus() {
        assertTrue(lastResponse.getStatusCodeValue() >= 500, "Status code should be 5xx, but was " + lastResponse.getStatusCodeValue());
    }

    @Then("the response time should be at least {int} ms")
    public void theResponseTimeShouldBeAtLeast(int minTimeMs) {
        assertTrue(responseTime >= minTimeMs, "Response time was " + responseTime + " ms, expected at least " + minTimeMs + " ms");
    }
}
