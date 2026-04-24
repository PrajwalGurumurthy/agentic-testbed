# Skill: Search Confluence

This skill teaches the agent how to search for technical documentation in Confluence.

## Prerequisites
Ensure the following environment variables are set in your execution environment:
- `CONFLUENCE_URL` (e.g., https://your-domain.atlassian.net/wiki)
- `CONFLUENCE_USER_EMAIL`
- `CONFLUENCE_API_TOKEN`
- Python `requests` library must be installed (`pip install -r .ai/tools/requirements.txt`)

## How to execute

Run the provided Python script located in `.ai/tools/confluence_connector.py` passing a valid CQL (Confluence Query Language) string as the first argument.

### Example Command
```bash
python3 .ai/tools/confluence_connector.py "text ~ 'database architecture'"
```

### Expected Output
The script will output a JSON array of matching pages, including their `title`, `url`, and a `content_snippet`. Use this to gather context on how to implement the feature based on existing architectural documentation.
