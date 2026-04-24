# Skill: Raise GitHub PR

This skill teaches the agent how to raise a Pull Request on GitHub after code is reviewed and approved.

## Prerequisites
Ensure the following environment variable is set in your execution environment:
- `GITHUB_TOKEN` (Must have `repo` permissions)
- Python `requests` library must be installed (`pip install -r .ai/tools/requirements.txt`)

## How to execute

Run the provided Python script located in `.ai/tools/github_connector.py` passing the target repository, branch names, title, and body.

### Arguments Required
1. `repo`: The repository in format `owner/repo` (e.g., `mycompany/my-java-app`)
2. `head_branch`: The branch you just created and pushed (e.g., `feature/JIRA-123`)
3. `base_branch`: The target branch you want to merge into (e.g., `main` or `develop`)
4. `title`: The title of the PR.
5. `body`: The Markdown description of the PR.

### Example Command
```bash
python3 .ai/tools/github_connector.py "mycompany/my-java-app" "feature/PROJ-123" "main" "feat: Add new user endpoint" "Fixes ticket PROJ-123. Adds the REST endpoint as designed."
```

### Expected Output
The script will output the URL of the newly created Pull Request.
