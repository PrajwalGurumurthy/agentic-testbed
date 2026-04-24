#!/usr/bin/env python3
"""
Jira Connector Tool
This script fetches details for a given Jira ticket.

Environment variables required:
- JIRA_URL (e.g., https://your-domain.atlassian.net)
- JIRA_USER_EMAIL
- JIRA_API_TOKEN
"""

import os
import sys
import json
import requests
from requests.auth import HTTPBasicAuth

def get_jira_ticket(ticket_id):
    jira_url = os.environ.get('JIRA_URL')
    email = os.environ.get('JIRA_USER_EMAIL')
    api_token = os.environ.get('JIRA_API_TOKEN')

    if not all([jira_url, email, api_token]):
        print("Error: Missing required environment variables (JIRA_URL, JIRA_USER_EMAIL, JIRA_API_TOKEN)", file=sys.stderr)
        sys.exit(1)

    url = f"{jira_url.rstrip('/')}/rest/api/2/issue/{ticket_id}"
    auth = HTTPBasicAuth(email, api_token)
    headers = {"Accept": "application/json"}

    response = requests.get(url, headers=headers, auth=auth)

    if response.status_code == 200:
        data = response.json()

        # Extract meaningful fields
        summary = data.get('fields', {}).get('summary', 'No summary')
        description = data.get('fields', {}).get('description', 'No description')
        issue_type = data.get('fields', {}).get('issuetype', {}).get('name', 'Unknown')

        result = {
            "ticket_id": ticket_id,
            "type": issue_type,
            "summary": summary,
            "description": description
        }
        print(json.dumps(result, indent=2))
    else:
        print(f"Error: Failed to fetch ticket {ticket_id}. Status Code: {response.status_code}", file=sys.stderr)
        print(response.text, file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python jira_connector.py <ticket_id>")
        sys.exit(1)

    get_jira_ticket(sys.argv[1])
