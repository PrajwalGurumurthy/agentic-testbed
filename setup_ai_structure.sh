#!/bin/bash

# setup_ai_structure.sh
# This script copies the .ai folder structure to a specified target directory.

# Check if target directory is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <target_directory>"
  echo "Example: $0 /path/to/your/repo"
  exit 1
fi

TARGET_DIR="$1"

# Check if the source .ai folder exists in the current directory
if [ ! -d ".ai" ]; then
  echo "Error: The source '.ai' folder does not exist in the current directory."
  echo "Please run this script from the root of the repository containing the '.ai' folder."
  exit 1
fi

# Create target .ai directory
TARGET_AI_DIR="${TARGET_DIR}/.ai"

echo "Copying AI folder structure to ${TARGET_AI_DIR}..."

# Copy the structure
mkdir -p "$TARGET_AI_DIR"
cp -r .ai/* "$TARGET_AI_DIR/"

# Check if copy was successful
if [ $? -eq 0 ]; then
  echo "Successfully created the AI folder structure at ${TARGET_AI_DIR}"
  echo "Contents:"
  ls -la "${TARGET_AI_DIR}"
else
  echo "Error: Failed to copy the .ai folder structure."
  exit 1
fi
