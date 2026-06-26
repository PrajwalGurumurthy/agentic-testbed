package com.stockservices.sample;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.profiles.active=chaos-monkey"
})
@org.springframework.context.annotation.Import(com.stockservices.common.chaos.lib.ChaosMonkeyFacade.class)
public class SampleSteps {

    @LocalServerPort
    private int port;

    // We create our own RestTemplate for tests to avoid ChaosMonkey intercepting TestRestTemplate.
    // If we create it directly with new RestTemplate(), it won't be a bean and won't be proxied.
    private RestTemplate testClient = new RestTemplate();

    private ResponseEntity<String> lastResponse;
    private long responseTime;
    private WireMockServer wireMockServer;

    @Before
    public void setup() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);

        stubFor(get(urlEqualTo("/service-a"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("ServiceA_OK")));

        stubFor(get(urlEqualTo("/service-b"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("ServiceB_OK")));
    }

    @After
    public void teardown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @When("I request the message")
    public void iRequestTheMessage() {
        long start = System.currentTimeMillis();
        lastResponse = testClient.getForEntity("http://localhost:" + port + "/api/message", String.class);
        responseTime = System.currentTimeMillis() - start;
    }

    @When("I request the message expecting an error")
    public void iRequestTheMessageExpectingAnError() {
        lastResponse = testClient.getForEntity("http://localhost:" + port + "/api/message", String.class);
    }

    @Then("the response should be {string}")
    public void theResponseShouldBe(String expectedMessage) {
        assertEquals(200, lastResponse.getStatusCode().value());
        assertEquals(expectedMessage, lastResponse.getBody());
    }

    @Then("the application should return an error status")
    public void theApplicationShouldReturnAnErrorStatus() {
        assertTrue(lastResponse.getStatusCode().value() >= 500, "Status code should be 5xx, but was " + lastResponse.getStatusCode().value());
    }

    @Then("the response time should be at least {int} ms")
    public void theResponseTimeShouldBeAtLeast(int minTimeMs) {
        assertTrue(responseTime >= minTimeMs, "Response time was " + responseTime + " ms, expected at least " + minTimeMs + " ms");
    }
}
