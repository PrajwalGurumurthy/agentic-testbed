# Java Coding Conventions

When writing Java code in this repository, follow these standard guidelines:

1. **Naming Conventions:**
   - Classes and Interfaces: `CamelCase` (e.g., `UserRepository`)
   - Methods and Variables: `camelCase` (e.g., `getUserById`)
   - Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
   - Packages: lowercase, separated by dots (e.g., `com.company.project.module`)

2. **Formatting:**
   - Use 4 spaces for indentation (no tabs).
   - Maximum line length is 120 characters.
   - Opening braces `{` should be on the same line as the declaration.

3. **Best Practices:**
   - Prefer immutability: use `final` where applicable.
   - Use `Optional` to prevent `NullPointerException` when returning potentially null values.
   - Keep methods small and focused on a single responsibility.
   - Write clear JavaDoc for public interfaces and complex logic.

4. **Testing:**
   - Write JUnit 5 tests for all new logic.
   - Use Mockito for mocking dependencies.
   - Aim for high code coverage (at least 80%).
