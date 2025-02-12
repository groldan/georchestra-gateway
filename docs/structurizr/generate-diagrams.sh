#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
EXPORTS_DIR="${SCRIPT_DIR}/exports"
OUTPUT_DIR="${SCRIPT_DIR}/../assets/images/structurizr"

# Create the export and output directories if they don't exist
mkdir -p "$EXPORTS_DIR"
mkdir -p "$OUTPUT_DIR"

echo "Step 1: Generating PlantUML files from Structurizr DSL"

# Export workspace.dsl to PlantUML
echo "Exporting workspace.dsl to PlantUML..."
docker run --rm -v "${SCRIPT_DIR}:/usr/local/structurizr" structurizr/cli export \
  -workspace /usr/local/structurizr/workspace.dsl \
  -format plantuml/c4plantuml \
  -output /usr/local/structurizr/exports

# Export dynamic-views.dsl to PlantUML
echo "Exporting dynamic-views.dsl to PlantUML..."
docker run --rm -v "${SCRIPT_DIR}:/usr/local/structurizr" structurizr/cli export \
  -workspace /usr/local/structurizr/dynamic-views.dsl \
  -format plantuml/c4plantuml \
  -output /usr/local/structurizr/exports

echo "Step 2: Generating SVG files from PlantUML"

# Process each PlantUML file individually to SVG
for puml_file in "${EXPORTS_DIR}"/*.puml; do
  file_name=$(basename "$puml_file")
  output_file="${OUTPUT_DIR}/$(basename "$puml_file" .puml).svg"
  echo "Generating SVG for $file_name to $output_file..."
  
  # Make sure we have full paths
  puml_abs_path=$(realpath "$puml_file")
  output_abs_path=$(realpath "$OUTPUT_DIR")
  
  # Run PlantUML directly with absolute paths
  docker run --rm -v "$(dirname "$puml_abs_path"):/work" plantuml/plantuml -tsvg "/work/$(basename "$puml_file")"
  
  # Move generated SVG to the correct location
  mv "$(dirname "$puml_abs_path")/$(basename "$puml_file" .puml).svg" "$output_abs_path/$(basename "$puml_file" .puml).svg"
done

echo "Diagram generation complete. SVG files are available in: $OUTPUT_DIR"

# List the generated SVG files
echo "Generated SVG files:"
find "$OUTPUT_DIR" -name "*.svg" | sort | while read -r file; do
  echo " - $(basename "$file")"
  # Check the file size to make sure it's actually a proper SVG file
  file_size=$(stat -f%z "$file")
  echo "   Size: $file_size bytes"
done