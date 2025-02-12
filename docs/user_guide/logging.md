# Structured Logging

The geOrchestra Gateway includes a comprehensive logging module that provides structured logging, request tracing, and access logging capabilities. These features help administrators monitor, debug, and audit the Gateway's operation.

## Overview

The logging module provides:

* **Structured JSON Logging** - Machine-readable logs that are easy to parse and analyze

* **Request Tracing** - Unique identifiers for each request to track request flow

* **MDC (Mapped Diagnostic Context)** - Contextual information added to every log entry

* **Access Logging** - Detailed logs of all HTTP requests with configurable levels

* **WebFlux MDC Propagation** - Consistent logging context in reactive applications

## Enabling JSON Logging

To enable JSON-formatted logs, activate the `json-logs` Spring profile:

```yaml
spring:
  profiles:
    active: json-logs
```

When the JSON profile is active, logs will be formatted as structured JSON objects instead of plain text, making them suitable for ingestion into log analysis tools like Elasticsearch, Splunk, or Graylog.

Examples of JSON log entries:

**Application Startup Log**
```json
{
  "@timestamp": "2025-03-30T18:25:12.345Z",
  "level": "INFO",
  "logger_name": "org.georchestra.gateway.app.GeorchestraGatewayApplication",
  "thread_name": "main",
  "message": "Started GeorchestraGatewayApplication in 5.832 seconds",
  "application.name": "gateway-service",
  "application.profile": "prod",
  "application.version": "1.2.0"
}
```

**HTTP Request Access Log**
```json
{
  "@timestamp": "2025-03-30T18:26:34.789Z",
  "message": "GET /api/maps/123 200",
  "logger_name": "org.georchestra.gateway.accesslog",
  "thread_name": "reactor-http-nio-3",
  "level": "INFO",
  "http.request.id": "01HRG3ZNVN4CKF74DNKB1S2XRM",
  "http.request.method": "GET",
  "http.request.url": "/api/maps/123",
  "http.request.remote-addr": "192.168.1.25",
  "application.name": "gateway-service",
  "application.version": "1.2.0",
  "enduser.id": "admin",
  "enduser.roles": "ROLE_ADMINISTRATOR,ROLE_USER"
}
```

**Authentication Event Log**
```json
{
  "@timestamp": "2025-03-30T18:26:12.678Z",
  "message": "User authenticated successfully",
  "logger_name": "org.georchestra.gateway.security",
  "thread_name": "reactor-http-nio-2",
  "level": "INFO",
  "http.request.id": "01HRG3ZMPTS7FV9JD5YE5TH9MQ",
  "http.request.method": "POST",
  "http.request.url": "/login",
  "http.request.remote-addr": "192.168.1.25",
  "enduser.id": "admin",
  "enduser.auth-method": "FormLoginAuthenticationToken"
}
```

## Configuration

### Main Configuration File

The logging configuration is defined in `datadir/gateway/logging.yaml`. This file should be included in the `spring.config.import` list in `gateway.yaml`:

```yaml
spring.config.import: application.yaml, security.yaml, routes.yaml, roles-mappings.yaml, logging.yaml
```

### Access Logging

Access logging records details about incoming HTTP requests. You can configure which URL patterns are logged at different levels:

```yaml
logging:
  accesslog:
    enabled: true
    # URLs to log at INFO level
    info:
      - ".*/(ows|ogc|wms|wfs|wcs|wps)(/.*|\?.*)?$"
    # URLs to log at DEBUG level
    debug:
      - ".*/(admin|manager)/.*"
    # URLs to log at TRACE level
    trace:
      - "^(?!.*/static/)(?!.*\.(png|jpg|jpeg|gif|svg|webp|ico)(\?.*)?$).*$"
```

Each pattern is a Java regular expression that is matched against the full request URL.

### MDC Properties

The MDC (Mapped Diagnostic Context) provides contextual information for each log entry. When using JSON logging, MDC values are added as top-level fields in the JSON structure rather than being nested in an "mdc" object. This flattened structure makes it easier to query and filter logs based on these values.

You can control which attributes are included:

```yaml
logging:
  mdc:
    include:
      # User authentication MDC properties
      user:
        id: true
        roles: false
        org: true
        auth-method: true

      # Application environment MDC properties
      application:
        name: true
        version: true
        instance-id: true
        active-profiles: false

      # HTTP request MDC properties
      http:
        id: true
        method: true
        url: true
        query-string: false
        parameters: false
        headers: false
        headers-pattern: ".*"
        cookies: false
        remote-addr: true
        remote-host: false
        session-id: false
```

### Log Levels

You can configure log levels for different packages:

```yaml
logging:
  level:
    root: warn
    org.springframework: warn
    org.georchestra: info
    org.georchestra.gateway: debug
```

### Logging Profiles

Several built-in profiles are available to enable more detailed logging for specific components:

- `logging_debug` - Set most components to INFO level

- `logging_debug_security` - Detailed security-related logging


To activate a profile:

```yaml
spring:
  profiles:
    active: default,json-logs,logging_debug_security
```

## WebFlux MDC Propagation

The logging module includes special support for maintaining MDC context in reactive applications using WebFlux. This ensures that contextual information is preserved throughout the reactive chain, even across thread boundaries.

This feature allows logs to consistently contain the same MDC attributes (like request ID, user information, etc.) across all components of the gateway, even in asynchronous code running on different threads. Each log entry can be correlated with the specific request that triggered it, making troubleshooting much easier.

This feature is automatically enabled when the logging module is present. No additional configuration is needed.

!!! info "Developer Information"
    For developers who want to use or extend the WebFlux MDC Propagation functionality, refer to the [Developer Guide: WebFlux MDC Propagation](../developer_guide/webflux_mdc.md).

## Troubleshooting

### Verifying JSON Logging

To verify that JSON logging is working correctly, look for log entries that are formatted as JSON objects. Each entry should be a single line starting with `{` and ending with `}`.

### Common Issues

1. **Missing MDC properties**: Check the `logging.mdc.include` configuration to ensure the desired properties are enabled.

2. **Log format not changing**: Ensure the `json-logs` profile is active in your configuration.

3. **MDC context lost in reactive chain**: This indicates a potential issue with the MDC propagation. Check that you're using the latest version of the Gateway.

### Testing with curl

You can test access logging with a simple curl command:

```bash
curl -v http://localhost:8080/console/
```

Then check the logs to see if the request was logged with the expected level and MDC properties.
