package com.stockservices.common.chaos.lib;

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

    @When("chaos watcher for {string} is enabled")
    public void chaosWatcherForIsEnabled(String watcherType) {
        boolean controller = "controller".equalsIgnoreCase(watcherType);
        boolean restController = "restController".equalsIgnoreCase(watcherType);
        boolean service = "service".equalsIgnoreCase(watcherType);
        boolean repository = "repository".equalsIgnoreCase(watcherType);
        boolean component = "component".equalsIgnoreCase(watcherType);
        boolean restTemplate = "restTemplate".equalsIgnoreCase(watcherType);
        boolean webClient = "webClient".equalsIgnoreCase(watcherType);

        chaosMonkeyFacade.updateWatchers(controller, restController, service, repository, component, restTemplate, webClient);
    }

    @When("chaos watcher for {string} and {string} is enabled")
    public void chaosWatcherForAndIsEnabled(String type1, String type2) {
        boolean controller = "controller".equalsIgnoreCase(type1) || "controller".equalsIgnoreCase(type2);
        boolean restController = "restController".equalsIgnoreCase(type1) || "restController".equalsIgnoreCase(type2);
        boolean service = "service".equalsIgnoreCase(type1) || "service".equalsIgnoreCase(type2);
        boolean repository = "repository".equalsIgnoreCase(type1) || "repository".equalsIgnoreCase(type2);
        boolean component = "component".equalsIgnoreCase(type1) || "component".equalsIgnoreCase(type2);
        boolean restTemplate = "restTemplate".equalsIgnoreCase(type1) || "restTemplate".equalsIgnoreCase(type2);
        boolean webClient = "webClient".equalsIgnoreCase(type1) || "webClient".equalsIgnoreCase(type2);

        chaosMonkeyFacade.updateWatchers(controller, restController, service, repository, component, restTemplate, webClient);
    }

    @When("chaos watcher for {string} and {string} is disabled")
    public void chaosWatcherForAndIsDisabled(String type1, String type2) {
        // Ignored here for now since updateWatchers already overrides all watchers
    }

    @When("chaos watcher for {string} is disabled and {string} is enabled")
    public void chaosWatcherForIsDisabledAndIsEnabled(String disabledType, String enabledType) {
        boolean restController = "restController".equalsIgnoreCase(enabledType);
        boolean service = "service".equalsIgnoreCase(enabledType);
        boolean restTemplate = "restTemplate".equalsIgnoreCase(enabledType);
        chaosMonkeyFacade.updateWatchers(false, restController, service, false, false, restTemplate, false);
    }
}
