# Skill: Search Jira

This skill teaches the agent how to fetch details of a Jira ticket.

## Prerequisites
Ensure the following environment variables are set in your execution environment:
- `JIRA_URL` (e.g., https://your-domain.atlassian.net)
- `JIRA_USER_EMAIL`
- `JIRA_API_TOKEN`
- Python `requests` library must be installed (`pip install -r .ai/tools/requirements.txt`)

## How to execute

Run the provided Python script located in `.ai/tools/jira_connector.py` passing the Ticket ID as the first argument.

### Example Command
```bash
python3 .ai/tools/jira_connector.py "PROJ-123"
```

### Expected Output
The script will output a JSON payload containing the `ticket_id`, `type`, `summary`, and `description`. Use this information to understand the user story and acceptance criteria.
