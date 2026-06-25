Feature: Chaos Testing the Sample Application
  As a developer
  I want to verify my application behaves correctly under chaos
  So that I can ensure resilience

  Scenario: Application works normally without chaos
    Given chaos monkey is disabled
    When I request the message
    Then the response should be "Hello from DemoService!"

  Scenario: Application throws exception when exception assault is configured
    Given chaos monkey is enabled
    When exception assault of type "java.lang.RuntimeException" is configured
    And I request the message expecting an error
    Then the application should return an error status
    And all assaults are reset
    And chaos monkey is disabled

  Scenario: Application is slow when latency assault is configured
    Given chaos monkey is enabled
    When latency assault of 500 ms to 1000 ms is configured
    And I request the message
    Then the response time should be at least 500 ms
    And all assaults are reset
    And chaos monkey is disabled
