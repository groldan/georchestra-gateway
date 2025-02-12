# Contributing to geOrchestra Gateway

Thank you for your interest in contributing to the geOrchestra Gateway project! This guide will help you get started.

## About geOrchestra

geOrchestra is a free, modular, and interoperable Spatial Data Infrastructure (SDI) solution that started in 2009 to meet the requirements of the INSPIRE European directive. It is a community-driven project governed by a Project Steering Committee (PSC) consisting of 9 members who ensure the project complies with its founding principles.

The geOrchestra community motto is "made by people for people" and welcomes contributions from individuals and organizations at all levels of expertise.

## Code of Conduct

This project adheres to the [geOrchestra Code of Conduct](https://github.com/georchestra/georchestra/blob/master/CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## Ways to Contribute

There are many ways to contribute to geOrchestra Gateway:

1. **Reporting bugs**: File issues for bugs you encounter
2. **Suggesting enhancements**: Propose new features or improvements
3. **Documentation**: Improve or correct documentation
4. **Code contributions**: Submit pull requests with bug fixes or features
5. **Testing**: Test new releases and report issues
6. **Translations**: Help translate the UI into different languages

## Getting Started

### Prerequisites

To contribute to the Gateway, you'll need:

- Java 21 or later
- Maven 3.8+
- Git
- A GitHub account

### Development Environment Setup

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR-USERNAME/georchestra-gateway.git
   cd georchestra-gateway
   ```

3. Add the upstream repository as a remote:
   ```bash
   git remote add upstream https://github.com/georchestra/georchestra-gateway.git
   ```

4. Create a branch for your changes:
   ```bash
   git checkout -b my-feature-branch
   ```

5. Build the project:
   ```bash
   ./mvnw clean install
   ```

## Development Workflow

### Working on Issues

1. Find an issue to work on in the [issue tracker](https://github.com/georchestra/georchestra-gateway/issues)
2. Comment on the issue to let others know you're working on it
3. Create a branch with a descriptive name (e.g., `fix-login-page` or `add-oauth-provider`)
4. Make your changes, following the [code style guidelines](code_style.md)
5. Write tests for your changes
6. Update documentation as needed

### Commit Guidelines

- Use clear, descriptive commit messages
- Reference issue numbers in commit messages
- Keep commits focused on a single task
- Follow the [conventional commits specification](https://www.conventionalcommits.org/)

Example:
```
fix: Prevent login form submission when inputs are invalid

Added form validation to prevent submission when username or password
is empty. This improves user experience by providing immediate feedback.

Fixes #123
```

### Pull Request Process

1. Update your branch with the latest changes from upstream:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. Push your branch to your fork:
   ```bash
   git push origin my-feature-branch
   ```

3. Create a pull request from your branch to the upstream main branch
4. In your PR description:
   - Describe what the PR does
   - Link to relevant issues
   - Note any breaking changes
   - Include screenshots for UI changes

5. A maintainer will review your PR and may request changes
6. Once approved, a maintainer will merge your PR

## Testing

### Running Tests

Run all tests with:
```bash
./mvnw test
```

Run a specific test with:
```bash
./mvnw test -Dtest=TestClassName
```

### Testing Your Changes

Before submitting a PR, ensure:

1. All tests pass
2. Your code has adequate test coverage
3. The application runs correctly with your changes
4. You've tested all relevant authentication methods

## Documentation

When making changes, please update the relevant documentation:

1. Update Javadoc comments for API changes
2. Update or create user guide pages for user-facing changes
3. Update developer guide for infrastructure or architecture changes

## Release Process

Releases are managed by the project maintainers. The process generally involves:

1. Creating a release branch
2. Updating version numbers
3. Running final tests
4. Building release artifacts
5. Publishing to Maven Central
6. Creating GitHub release notes

## Community

Join the geOrchestra community:

- **GitHub**: [github.com/georchestra](https://github.com/georchestra)
- **Main mailing list**: [georchestra@googlegroups.com](mailto:georchestra@googlegroups.com)
- **Developer mailing list**: [georchestra-dev@googlegroups.com](mailto:georchestra-dev@googlegroups.com)
- **Matrix chat**: #georchestra on osgeo.org server
- **Twitter**: [@georchestra](https://twitter.com/georchestra)
- **Annual community meetings**: The geOrchestra community organizes annual meetings (geOcom) where contributors and users gather to share experiences and plan future developments

### Project Governance

The geOrchestra project is governed by a 9-member Project Steering Committee (PSC) that can be contacted at [psc@georchestra.org](mailto:psc@georchestra.org). The PSC ensures the project complies with its founding principles and uses a "geOrchestra Improvement Proposal" (GIP) process for community-driven changes.

For more information about the geOrchestra project, visit [georchestra.org](https://www.georchestra.org/).

## License

By contributing to geOrchestra Gateway, you agree that your contributions will be licensed under the project's [license](https://github.com/georchestra/georchestra-gateway/blob/main/LICENSE.txt).