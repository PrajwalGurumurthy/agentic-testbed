#!/usr/bin/env python3
"""
GitHub Connector Tool
This script creates a Pull Request on GitHub.

Environment variables required:
- GITHUB_TOKEN
"""

import os
import sys
import json
import requests

def create_pull_request(repo, head_branch, base_branch, title, body):
    token = os.environ.get('GITHUB_TOKEN')

    if not token:
        print("Error: Missing required environment variable (GITHUB_TOKEN)", file=sys.stderr)
        sys.exit(1)

    url = f"https://api.github.com/repos/{repo}/pulls"
    headers = {
        "Accept": "application/vnd.github.v3+json",
        "Authorization": f"token {token}"
    }

    payload = {
        "title": title,
        "head": head_branch,
        "base": base_branch,
        "body": body
    }

    response = requests.post(url, headers=headers, json=payload)

    if response.status_code == 201:
        data = response.json()
        print(f"Successfully created Pull Request: {data.get('html_url')}")
    else:
        print(f"Error: Failed to create PR. Status Code: {response.status_code}", file=sys.stderr)
        print(response.text, file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) != 6:
        print("Usage: python github_connector.py <owner/repo> <head_branch> <base_branch> <title> <body>")
        print("Example: python github_connector.py myorg/myrepo feature-branch main \"Fix bug\" \"Fixes JIRA-123\"")
        sys.exit(1)

    create_pull_request(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5])
