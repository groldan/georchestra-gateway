# Developer Guide

Welcome to the geOrchestra Gateway Developer Guide. This guide is intended for developers who want to contribute to or extend the geOrchestra Gateway.

## Introduction

geOrchestra Gateway is built on [Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/), a library for building API gateways on top of Spring WebFlux. It provides a centralized entry point for all geOrchestra applications, handling authentication, authorization, and request routing.

## Overview

The gateway codebase is organized into the following main packages:

- `org.georchestra.gateway.app`: Main application code
- `org.georchestra.gateway.security`: Security configuration and components
- `org.georchestra.gateway.filter`: Custom gateway filters
- `org.georchestra.gateway.model`: Data models
- `org.georchestra.gateway.handler`: Request handlers
- `org.georchestra.gateway.autoconfigure`: Auto-configuration classes
- `org.georchestra.gateway.logging`: Structured logging and MDC propagation

## Key Extension Points

The gateway provides several extension points:

1. **Custom Filters**: Add custom logic to the request processing pipeline
2. **Authentication Providers**: Add new authentication methods
3. **User Mappers**: Customize how authentication information is mapped to geOrchestra users
4. **Authorization Managers**: Customize access control logic
5. **Logging and MDC**: Extend structured logging and MDC propagation in reactive contexts

## Getting Started

To get started with contributing to the geOrchestra Gateway, follow these steps:

1. Fork the repository
2. Clone your fork
3. Set up your development environment
4. Make your changes
5. Run tests
6. Submit a pull request

See the following sections for more detailed information:

- [Building](building.md): How to build the gateway
- [Code Style](code_style.md): Coding conventions and style guidelines
- [Project Structure](project_structure.md): Overview of the project structure
- [Testing](testing.md): How to write and run tests
- [Custom Filters](custom_filters.md): How to create custom gateway filters
- [Authentication](authentication.md): How to extend authentication mechanisms
- [Logging](logging.md): How to work with the structured logging system
- [WebFlux MDC Propagation](webflux_mdc.md): How to use MDC context in reactive applications
- [Contributing](contributing.md): Guidelines for contributing to the project
