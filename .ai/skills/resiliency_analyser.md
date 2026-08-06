# Skill: Resiliency Analyser

## Description
This skill performs a complete resiliency analysis of Java applications, particularly Spring Boot applications. It analyzes the codebase in phases, starting with static analysis to build a call chain, followed by a mental simulation of various error conditions, and finally scoring the resiliency of the code.

## Phase 1: Static Analysis and Call Chain Generation
1. **Analyze the Codebase**: Perform static code analysis on the provided Java/Spring Boot code. Use your own capabilities to perform AST traversal and call graphing across the codebase. You may also suggest or utilize available tools for bytecode analysis or call graphing if appropriate.
2. **Identify Dependencies**: List all external and internal dependent entities (e.g., databases, external APIs, downstream microservices, message brokers).
3. **Generate `call_chain.json`**: Construct a comprehensive call chain JSON representing the application's execution paths and dependencies. The structure should be dynamic but must contain all necessary details required for deep resiliency analysis (e.g., caller, callee, interaction type, synchronous/asynchronous, existing resilience annotations/mechanisms).

## Phase 2: Resiliency Mental Simulation
1. **Input Analysis**: Use the `call_chain.json` generated in Phase 1 as the primary input.
2. **Simulate Error Conditions**: For each dependent entity and execution path, mentally simulate all possible error conditions, including but not limited to:
   - Timeouts
   - 5xx Server Errors
   - Service Unavailable
   - Concurrent call limits / Thread pool exhaustion
   - Other resilience-related error conditions
3. **Generate Detailed Report**: Create a detailed report documenting the findings. This must include a table mapping each dependent entity to the simulated error conditions and the expected behavior of the application code based on your simulation.

## Phase 3: Scoring and Summarization
1. **Summarize Findings**: Summarize the detailed report from Phase 2 into a concise table.
2. **Assign Resiliency Scores**: Assign a resiliency score for each dependency and error condition combination based on how well the code currently handles that specific error. Use a simple **Red / Amber / Green** scoring system:
   - **Green**: The code robustly handles the error (e.g., proper timeouts, circuit breakers, fallbacks, graceful degradation).
   - **Amber**: The code has partial handling but might suffer performance degradation or expose minor issues (e.g., retries without backoff, missing fallbacks).
   - **Red**: The code does not handle the error well and will likely fail catastrophically (e.g., infinite blocking, unhandled exceptions, lack of timeouts).