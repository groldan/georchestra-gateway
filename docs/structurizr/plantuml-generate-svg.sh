#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
EXPORT_DIR="${SCRIPT_DIR}/exports"
OUTPUT_DIR="${SCRIPT_DIR}/../assets/images/structurizr"

# Create the output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Loop through all PlantUML files and convert them to SVG
for puml_file in "$EXPORT_DIR"/*.puml; do
  base_name=$(basename "$puml_file" .puml)
  output_file="${OUTPUT_DIR}/${base_name}.svg"
  
  echo "Converting $puml_file to $output_file"
  
  # Use PlantUML Docker image to convert PUML to SVG
  docker run --rm -v "${SCRIPT_DIR}:/data" plantuml/plantuml -tsvg -o "/data/../assets/images/structurizr" "/data/exports/$(basename "$puml_file")"
done

echo "Conversion complete. SVG files are in: $OUTPUT_DIR"