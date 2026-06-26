# Chaos BDD Library

This library provides a reusable BDD (Behavior-Driven Development) framework for introducing chaos engineering into your Spring Boot tests. Built on top of **Chaos Monkey for Spring Boot** and **Cucumber Java**, it allows developers to write Gherkin scenarios to simulate and verify application resilience against network latencies and unexpected exceptions.

## Features
- **Programmatic Chaos Orchestration:** Communicates directly with Chaos Monkey's Actuator endpoints via a safe, unwatched internal `RestTemplate`.
- **Reusable Gherkin Steps:** A pre-built library of Cucumber steps to dynamically configure assaults and watchers at runtime.
- **Resilience Testing:** Easily verify your application's Circuit Breakers, Retries, and Fallbacks.

---

## Quick Start

### 1. Add Maven Dependency
Include the `chaos-bdd-lib` as a `test` dependency in your module's `pom.xml`.

```xml
<dependency>
    <groupId>com.stockservices.common.chaos</groupId>
    <artifactId>chaos-bdd-lib</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

*(Note: Ensure `spring-boot-starter-web` and `chaos-monkey-spring-boot` are also present in your dependencies).*

### 2. Configure Spring Boot Properties
To allow the library to communicate with Chaos Monkey, you must enable the required profiles and expose the Actuator endpoints in your test configuration (e.g., `application-test.properties`):

```properties
# Enable Chaos Monkey profile
spring.profiles.active=chaos-monkey

# Expose chaosmonkey actuator endpoints
management.endpoints.web.exposure.include=chaosmonkey,health
management.endpoint.chaosmonkey.enabled=true

# Enable global chaos monkey and set default watchers
chaos.monkey.enabled=true
chaos.monkey.watcher.rest-controller=false
chaos.monkey.watcher.service=false
chaos.monkey.watcher.rest-template=true
```

### 3. Cucumber Context Configuration
In your Cucumber step definitions class (e.g., `SampleSteps.java`), ensure you scan the library's package so the `ChaosSteps` and `ChaosMonkeyFacade` are loaded into the test context.

```java
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {
    "com.stockservices.common.chaos.lib", // <-- Scan the library
    "com.yourcompany.app"                 // <-- Scan your application
})
public class SampleSteps {
    // Your application-specific step definitions
}
```

---

## Available Gherkin Steps

You can use the following steps directly in your `.feature` files:

### State Management
* `Given chaos monkey is enabled`
* `Then chaos monkey is disabled`
* `When all assaults are reset`

### Assault Configuration
* `When latency assault of {int} ms is configured`
* `When latency assault of {int} ms to {int} ms is configured`
* `When exception assault of type "{string}" is configured` *(e.g., "java.lang.RuntimeException")*

### Watcher Toggles
Chaos Monkey uses "Watchers" to know which Spring components to intercept (e.g., `restTemplate`, `restController`, `service`, `repository`, `component`, `webClient`).

* `When chaos watcher for "{string}" is enabled`
* `When chaos watcher for "{string}" and "{string}" is enabled`
* `When chaos watcher for "{string}" is disabled and "{string}" is enabled`

---

## Example Usage

Here is a practical example of testing a fallback mechanism when an external API fails due to an injected exception:

```gherkin
Feature: Resilience Testing

  Scenario: External API exception triggers fallback resilience pattern
    Given chaos monkey is enabled
    # Target outbound HTTP calls explicitly
    When chaos watcher for "restController" is disabled and "restTemplate" is enabled
    # Inject a RuntimeException
    And exception assault of type "java.lang.RuntimeException" is configured

    # Application-specific action
    And I request the external data

    # Application-specific assertion
    Then the response should contain "Fallback Data"

    # Clean up state for the next test
    And all assaults are reset
    And chaos monkey is disabled
```
