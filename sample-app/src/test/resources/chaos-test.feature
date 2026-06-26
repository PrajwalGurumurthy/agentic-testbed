Feature: Chaos Testing the Sample Application
  As a developer
  I want to verify my application behaves correctly under chaos
  So that I can ensure resilience

  Scenario: Application works normally without chaos
    Given chaos monkey is disabled
    When I request the message
    Then the response should be "Aggregated: ServiceA_OK & ServiceB_OK"

  Scenario: External API latency triggers fallback resilience pattern
    Given chaos monkey is enabled
    When chaos watcher for "restController" is disabled and "restTemplate" is enabled
    And latency assault of 2000 ms is configured
    And I request the message
    Then the response should be "Aggregated: Fallback data & Fallback data"
    And all assaults are reset
    And chaos monkey is disabled

  Scenario: External API exception triggers fallback resilience pattern
    Given chaos monkey is enabled
    When chaos watcher for "restController" is disabled and "restTemplate" is enabled
    And exception assault of type "java.lang.RuntimeException" is configured
    And I request the message
    Then the response should be "Aggregated: Fallback data & Fallback data"
    And all assaults are reset
    And chaos monkey is disabled
