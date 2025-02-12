# Structurizr Generated Diagrams

This directory contains SVG diagrams generated from the Structurizr DSL definitions in the `/docs/structurizr/` directory.

## About These Files

These SVG files are generated using:
1. Structurizr CLI to convert DSL to PlantUML
2. PlantUML to convert to SVG

## Regeneration Process

These files are automatically regenerated:
- During the GitHub Actions workflow for documentation
- When running the `./run_mkdocs.sh` script locally (if Docker is available)

## Version Control

While these are generated files, we keep them in version control to:
- Ensure diagrams are visible without requiring Docker
- Provide a fallback if generation fails
- Make diagrams immediately visible for new contributors

## Source of Truth

The Structurizr DSL files in `/docs/structurizr/` are the source of truth for these diagrams.
If you need to make changes to the diagrams, modify the DSL files and regenerate the SVGs.

## Placeholder Files

The SVG files in this directory may be placeholders until the workflow runs
or until you generate them locally by running `./run_mkdocs.sh`.