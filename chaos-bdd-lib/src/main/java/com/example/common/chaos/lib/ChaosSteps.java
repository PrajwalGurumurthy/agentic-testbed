package com.example.common.chaos.lib;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class ChaosSteps {

    @Autowired
    private ChaosMonkeyFacade chaosMonkeyFacade;

    @Given("chaos monkey is enabled")
    public void chaosMonkeyIsEnabled() {
        chaosMonkeyFacade.enableChaos();
    }

    @Then("chaos monkey is disabled")
    public void chaosMonkeyIsDisabled() {
        chaosMonkeyFacade.disableChaos();
        chaosMonkeyFacade.resetAssaults();
    }

    @When("latency assault of {int} ms to {int} ms is configured")
    public void latencyAssaultIsConfigured(int minMs, int maxMs) {
        chaosMonkeyFacade.configureLatencyAssault(minMs, maxMs);
    }

    @When("latency assault of {int} ms is configured")
    public void latencyAssaultOfFixedMsIsConfigured(int ms) {
        chaosMonkeyFacade.configureLatencyAssault(ms, ms);
    }

    @When("exception assault of type {string} is configured")
    public void exceptionAssaultIsConfigured(String exceptionClassName) {
        chaosMonkeyFacade.configureExceptionAssault(exceptionClassName);
    }

    @When("all assaults are reset")
    public void allAssaultsAreReset() {
        chaosMonkeyFacade.resetAssaults();
    }
}
