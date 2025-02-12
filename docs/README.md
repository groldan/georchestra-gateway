# geOrchestra Gateway Documentation

This directory contains the MkDocs-based documentation for the geOrchestra Gateway.

## About geOrchestra

[geOrchestra](https://www.georchestra.org/) is a free, modular, and interoperable Spatial Data Infrastructure (SDI) solution that provides a comprehensive suite of integrated geospatial applications. The project is governed by a 9-member Project Steering Committee (PSC) that ensures the project complies with its founding principles.

## Community Resources

- **Main website**: [georchestra.org](https://www.georchestra.org/)
- **GitHub organization**: [github.com/georchestra](https://github.com/georchestra)
- **Main mailing list**: [georchestra@googlegroups.com](mailto:georchestra@googlegroups.com)
- **Developer mailing list**: [georchestra-dev@googlegroups.com](mailto:georchestra-dev@googlegroups.com)
- **Matrix chat**: #georchestra on osgeo.org server
- **PSC contact**: [psc@georchestra.org](mailto:psc@georchestra.org)

## Setup

To set up MkDocs and create placeholder files, run the setup script:

```bash
./setup_mkdocs.sh
```

This will install MkDocs and required plugins, and create placeholder files for any missing documentation.

## Structure

The documentation is organized into three main sections:

1. **User Guide** - Documentation for users and system administrators
2. **Technical Documentation (arc42)** - Structured documentation of the system architecture
3. **Developer Guide** - Documentation for developers

## Building the Documentation

To build the documentation, run:

```bash
mkdocs build
```

This will create a `site` directory with the built documentation.

## Serving the Documentation

First, activate the virtual environment:

```bash
source .venv/bin/activate
```

Then serve the documentation locally:

```bash
mkdocs serve
```

Or if you're not in the virtual environment:

```bash
.venv/bin/mkdocs serve
```

Then visit http://127.0.0.1:8000 in your browser.

## Adding Content

To add content to the documentation:

1. Create or edit Markdown files in the appropriate directory
2. Update the `mkdocs.yml` file if you add new files
3. Run `mkdocs serve` to preview your changes

## Converting from AsciiDoc

The original documentation is in AsciiDoc format in the parent directory. When converting content from AsciiDoc to Markdown:

1. Convert the basic markup (headings, lists, links, etc.)
2. Convert code blocks from AsciiDoc syntax to Markdown syntax
3. Convert diagrams to Mermaid syntax
4. Add front matter if needed
