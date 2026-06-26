# Skill: Generate Chaos Scenarios
**Description:** Generates `*-chaos.feature` files containing Chaos Testing scenarios based on standard Gherkin feature files.

## Instructions
1. **Input Variables:**
   * `feature_path` (required): The path or glob pattern pointing to the `.feature` file(s) you wish to process.
   * `chaos_type` (optional): The type of chaos to introduce (e.g., `latency`, `exception`, `assaults`). If omitted, assume `all`.
2. **Context Gathering:**
   * You must read the provided `.feature` file(s).
   * You must read the available chaos Gherkin steps. First, attempt to read `.ai/references/chaos_steps.txt`. If this file does not exist, attempt to read `chaos-bdd-lib/src/main/java/com/stockservices/common/chaos/lib/ChaosSteps.java` directly. If neither can be found, prompt the user to configure the file with the valid steps.
3. **Scenario Generation Logic:**
   * Do not alter the original `.feature` file. Instead, generate a new file named `[original-name]-chaos.feature` in the same directory.
   * Based on the content of the original scenarios, intelligently deduce which chaos steps are necessary. For example, if a scenario calls an external API, generate scenarios that test resilience using the `restTemplate` watcher and the requested `chaos_type` assaults.
   * For every original scenario that interacts with components vulnerable to chaos, append a corresponding chaos scenario.
   * Make sure every chaos scenario begins with enabling chaos monkey, toggling the correct watcher, setting the assault, and ends with resetting assaults and disabling chaos monkey.
4. **Validation:**
   * Only use Gherkin steps that strictly exist within the provided configuration or `ChaosSteps.java` file.
   * If you are in doubt about which component the original scenario interacts with (e.g., whether to use the `restController`, `service`, or `restTemplate` watcher), use the `request_user_input` tool to prompt the user for clarification before generating the file.

## Example Usage
**User:** Generate chaos scenarios for `sample-app/src/test/resources/standard.feature` focusing on `latency`.
**Agent:**
1. Reads `standard.feature`.
2. Reads `.ai/references/chaos_steps.txt`.
3. Determines the standard scenario makes an outbound HTTP call.
4. Generates `sample-app/src/test/resources/standard-chaos.feature` appending a latency scenario targeting the `restTemplate` watcher.
