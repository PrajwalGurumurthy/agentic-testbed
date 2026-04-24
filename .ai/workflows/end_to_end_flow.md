# End-to-End Development Workflow

This is the master workflow prompt to automate software development using Claude Code.

**Trigger:** A Jira ticket is assigned to a developer.

**Input Variables:**
- `JIRA_TICKET_URL`: The link to the assigned Jira ticket.

## Execution Steps

1. **Analysis (Jira Analyzer Agent):**
   - Read the instructions in `.ai/agents/jira_analyzer.md`.
   - Access the `JIRA_TICKET_URL` to extract the user story.
   - Search Confluence for relevant technical details to enhance the story.
   - Output a concrete implementation plan.

2. **Context Generation (Context Generator Agent):**
   - Read the instructions in `.ai/agents/context_generator.md`.
   - Check if `.ai/references/generated/project_context.md` exists and is up to date.
   - If missing or outdated, run shell commands to analyze the repository structure and create it.

3. **Implementation (Developer Agent):**
   - Read the instructions in `.ai/agents/developer.md`.
   - Review `.ai/references/generated/project_context.md` to understand the architecture.
   - Review `.ai/references/java_conventions.md` and `.ai/references/repo_rules.md`.
   - Write the Java code and unit tests to fulfill the implementation plan.

4. **Review (Reviewer Agent):**
   - Read the instructions in `.ai/agents/reviewer.md`.
   - Analyze the changes made by the Developer agent.
   - If issues are found, iteratively fix them with the Developer agent.
   - Proceed only when the code passes all standards and tests.

5. **Submission (PR Manager Agent):**
   - Read the instructions in `.ai/agents/pr_manager.md`.
   - Commit the approved changes to a new branch.
   - Push the branch and raise a Pull Request in GitHub with a link to the Jira ticket.

**Instructions for Claude:**
Execute these steps sequentially. Do not proceed to the next step until the current step is fully and successfully completed. Ask for user confirmation only if a critical ambiguity arises that cannot be resolved via documentation.
