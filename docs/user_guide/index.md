# User Guide

Welcome to the geOrchestra Gateway user guide. This guide is intended for system administrators and users who want to install, configure, and use geOrchestra Gateway.

## What is geOrchestra Gateway?

geOrchestra Gateway is a component of the geOrchestra Spatial Data Infrastructure (SDI) that acts as the single entry point to backend applications. It handles:

- Authentication (LDAP, OAuth2/OpenID Connect)
- Authorization (role-based access control)
- Request routing to backend services
- Header forwarding to backend applications

The gateway provides a unified security layer allowing you to:

- Implement single sign-on across all geOrchestra applications
- Control access to specific applications or endpoints based on user roles
- Forward user information to backend applications via HTTP headers
- Customize the login page and user interface

## Table of Contents

- [Installation](installation.md) - How to install geOrchestra Gateway
- [Configuration](configuration.md) - Basic configuration options
- [Authentication](authentication.md) - Setting up LDAP and OAuth2/OpenID Connect
- [Proxying Applications](proxying_applications.md) - How to proxy applications through the gateway
- [Access Rules](access_rules.md) - How to configure access control rules
- [Headers](headers.md) - How to configure header forwarding
- [Logging](logging.md) - How to configure structured logging
- [Monitoring and Management](monitoring.md) - How to monitor and manage the Gateway using Actuator
- [UI Customization](ui_customization.md) - How to customize the login page and UI
- [Migration Guide](migration.md) - How to migrate from security-proxy to Gateway

## Data Directory Structure

geOrchestra Gateway relies on the geOrchestra "data directory" for its configuration. The relevant files are:

- `default.properties` - Configuration properties common to all geOrchestra applications
- `gateway/gateway.yaml` - Gateway-specific configuration
- `gateway/security.yaml` - Security configuration
- `gateway/routes.yaml` - Route definitions
- `gateway/roles-mappings.yaml` - Role mapping configuration
- `gateway/logging.yaml` - Logging configuration
