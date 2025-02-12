#!/bin/bash

# Script to run MkDocs from the virtual environment and generate diagrams

# Check if Docker is available
if ! command -v docker &> /dev/null
then
    echo "Warning: Docker is not installed or not in PATH. Diagram generation will be skipped."
    SKIP_DIAGRAMS=true
else
    SKIP_DIAGRAMS=false
fi

# Function to generate diagrams
generate_diagrams() {
    echo "Generating C4 diagrams from Structurizr DSL..."
    
    # Run the generate-diagrams.sh script
    "${SCRIPT_DIR}/docs/structurizr/generate-diagrams.sh"
}

# Get the script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Activate the virtual environment
if [ -d ".venv" ]; then
    source .venv/bin/activate
    echo "Virtual environment activated"
else
    echo "Error: Virtual environment not found. Please run ./setup_mkdocs.sh first."
    exit 1
fi

# Generate diagrams if Docker is available
if [ "$SKIP_DIAGRAMS" = false ]; then
    # Check if --skip-diagrams flag is provided
    if [[ ! "$*" == *"--skip-diagrams"* ]]; then
        generate_diagrams
    else
        echo "Diagram generation skipped (--skip-diagrams flag provided)"
        # Remove the --skip-diagrams flag from the arguments
        args=()
        for arg in "$@"; do
            if [ "$arg" != "--skip-diagrams" ]; then
                args+=("$arg")
            fi
        done
        set -- "${args[@]}"
    fi
else
    echo "Diagram generation skipped (Docker not available)"
fi

# Run MkDocs with provided arguments (default to serve)
if [ "$#" -eq 0 ]; then
    echo "Running 'mkdocs serve'..."
    mkdocs serve
else
    echo "Running 'mkdocs $@'..."
    mkdocs "$@"
fi