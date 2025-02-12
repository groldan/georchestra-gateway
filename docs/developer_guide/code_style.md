# Code Style Guidelines

This document outlines the code style guidelines for the geOrchestra Gateway project. Following these guidelines ensures consistency across the codebase and makes the code more maintainable.

## Java Code Style

### Formatting

- Use **4 spaces** for indentation, not tabs
- Maximum line length of **120 characters**
- Use Java coding conventions for naming:
    - `CamelCase` for class names
    - `camelCase` for method names and variables
    - `UPPER_SNAKE_CASE` for constants
    - `_camelCase` for private fields to distinguish them from local variables

### Imports

- No wildcard imports (`import package.*`)
- Organize imports in the following order:
    1. Java core packages
    2. Third-party libraries
    3. Project packages
- Remove unused imports

### Documentation

- All public classes, methods, and fields should have Javadoc comments
- Include `@param`, `@return`, and `@throws` tags as appropriate
- Document non-obvious implementation details

Example:

```java
/**
 * Maps authentication information to a GeorchestraUser object.
 *
 * @param authentication The authentication object containing user credentials
 * @return A fully populated GeorchestraUser object
 * @throws AuthenticationException If the user cannot be mapped
 */
public GeorchestraUser mapUser(Authentication authentication) {
    // Implementation
}
```

### Null Handling

- Use `@NonNull` and `@Nullable` annotations from Spring to document nullability
- Prefer Optional over null returns for API methods
- Validate method parameters using `Objects.requireNonNull` or Spring's `Assert`

### Exception Handling

- Prefer specific exceptions over general ones
- Document exceptions in Javadoc
- Don't catch exceptions you can't handle properly
- Use try-with-resources for closeable resources

### Reactive Programming

- Follow the reactive programming paradigm consistently
- Avoid blocking operations in reactive chains
- Properly handle backpressure
- Use appropriate operators to transform data

## Architecture Guidelines

### Package Structure

- Follow the established package structure
- Place related classes in the same package
- Don't create packages with just one class
- Use `impl` subpackages for implementations of interfaces

### Dependencies

- Follow dependency injection principles
- Prefer constructor injection over field injection
- Minimize dependencies between components
- Use interfaces to define component boundaries

### Configuration

- Use Spring Boot's configuration properties pattern
- Document configuration properties with `@ConfigurationProperties`
- Provide sensible defaults for all configuration properties
- Validate configuration at startup

## Testing Guidelines

- Write tests for all new code
- Maintain high test coverage
- Name tests clearly to indicate their purpose
- Follow the AAA pattern (Arrange, Act, Assert)
- Keep tests independent and idempotent

## Build and CI

- Ensure your code passes all CI checks before submitting a PR
- Address all static analysis warnings
- Keep dependencies up to date
- Don't commit IDE-specific files

## Commit Guidelines

- Write meaningful commit messages
- Keep commits focused on a single task
- Reference issue numbers in commit messages
- Follow the conventional commits specification:
    - `feat:` for new features
    - `fix:` for bug fixes
    - `docs:` for documentation changes
    - `style:` for code style changes
    - `refactor:` for code refactoring
    - `test:` for adding or modifying tests
    - `chore:` for build/tooling changes

Example commit message:

```
feat: Add OAuth2 authentication support

Implements OpenID Connect authentication flow using Spring Security OAuth2.
Includes custom user mapping and role extraction from JWT claims.

Closes #123
```

## Code Review Guidelines

- Review for functionality, correctness, and style
- Ensure code follows these guidelines
- Check for security vulnerabilities
- Look for performance issues
- Verify test coverage

## Tools

The project uses several tools to enforce code style:

- **Checkstyle**: For Java code style checks
- **SpotBugs**: For finding potential bugs
- **Jacoco**: For test coverage reports

Run these tools using Maven:

```bash
./mvnw clean verify
```