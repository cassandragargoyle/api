#!/bin/bash
# setup-venv.sh
# Create and activate Python virtual environment for project scripts

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
VENV_DIR="$PROJECT_DIR/.venv"

if [ -d "$VENV_DIR" ]; then
    echo "Virtual environment already exists: $VENV_DIR"
else
    echo "Creating virtual environment: $VENV_DIR"
    python3 -m venv "$VENV_DIR"
    echo "Virtual environment created."
fi

echo ""
echo "To activate, run:"
echo "  source $VENV_DIR/bin/activate"
