#!/usr/bin/env python3
"""
Confluence Connector Tool
This script searches for and fetches Confluence pages.

Environment variables required:
- CONFLUENCE_URL (e.g., https://your-domain.atlassian.net/wiki)
- CONFLUENCE_USER_EMAIL
- CONFLUENCE_API_TOKEN
"""

import os
import sys
import json
import requests
from requests.auth import HTTPBasicAuth

def search_confluence(cql_query):
    base_url = os.environ.get('CONFLUENCE_URL')
    email = os.environ.get('CONFLUENCE_USER_EMAIL')
    api_token = os.environ.get('CONFLUENCE_API_TOKEN')

    if not all([base_url, email, api_token]):
        print("Error: Missing required environment variables (CONFLUENCE_URL, CONFLUENCE_USER_EMAIL, CONFLUENCE_API_TOKEN)", file=sys.stderr)
        sys.exit(1)

    url = f"{base_url.rstrip('/')}/rest/api/content/search"
    auth = HTTPBasicAuth(email, api_token)
    headers = {"Accept": "application/json"}

    # Expand to get the body content
    params = {
        "cql": cql_query,
        "expand": "body.storage"
    }

    response = requests.get(url, headers=headers, params=params, auth=auth)

    if response.status_code == 200:
        data = response.json()
        results = []
        for item in data.get('results', []):
            title = item.get('title')
            link = item.get('_links', {}).get('webui')
            body = item.get('body', {}).get('storage', {}).get('value', 'No content')

            results.append({
                "title": title,
                "url": f"{base_url.rstrip('/wiki')}{link}" if link else None,
                "content_snippet": body[:500] + "..." if len(body) > 500 else body
            })

        print(json.dumps(results, indent=2))
    else:
        print(f"Error: Failed to search Confluence. Status Code: {response.status_code}", file=sys.stderr)
        print(response.text, file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python confluence_connector.py \"<cql_query>\"")
        print("Example: python confluence_connector.py \"text ~ 'architecture'\"")
        sys.exit(1)

    search_confluence(sys.argv[1])
