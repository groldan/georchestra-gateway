# Logging Module

The geOrchestra Gateway includes a comprehensive logging module that provides structured logging, MDC propagation in WebFlux reactive contexts, and access logging capabilities. This document explains how the module works and how to extend it.

## Architecture

The logging module is organized into several components:

- **MDC Configuration**: Controls which attributes are included in log entries
- **WebFlux MDC Propagation**: Ensures MDC context is maintained in reactive applications
- **Access Logging**: Configurable request logging with pattern-based log levels
- **JSON Formatting**: Structured log output for machine processing

### Module Structure

The logging functionality is implemented in the `georchestra-gateway-logging` module with the following key classes:

- `ReactorContextHolder`: Provides utilities for accessing MDC data in reactive contexts
- `LoggingConfiguration`: Configures logging components and settings
- `AccessLogFilter`: Records HTTP request details based on configurable patterns
- `MdcLogEnhancerFilter`: Adds MDC attributes to the reactive context
- `WebFluxMdcConfiguration`: Sets up WebFlux MDC propagation

## MDC in Reactive Applications

A key feature of the logging module is its ability to maintain MDC context in reactive applications using WebFlux, where code typically executes across different threads.

The Gateway implements a comprehensive WebFlux MDC propagation system that ensures contextual information like request IDs, user details, and application metadata are preserved throughout the reactive chain, even across thread boundaries.

### WebFlux MDC Propagation

For detailed information about using MDC in reactive applications, including:

- How MDC propagation works in reactive applications
- Using the ReactorContextHolder
- Accessing MDC context in your reactive code
- Adding custom MDC attributes
- Creating custom logging utilities
- Testing MDC propagation code
- Best practices and performance considerations

Please see the dedicated guide: [WebFlux MDC Propagation](webflux_mdc.md)

## Best Practices

1. **Enable Structured Logging**: Use the `json-logs` profile for structured logging that's easier to analyze with tools like ELK.

2. **Use Correlation IDs**: Include a correlation ID in your logs to track requests across services.

3. **Configure Appropriate Log Levels**: Set appropriate log levels for different packages to manage log volume.

4. **Be Selective with MDC Properties**: Only include essential contextual information in MDC to avoid performance overhead.

5. **Consider Log Rotation**: Implement log rotation policies to prevent disk space issues.

## Monitoring and Metrics

The logging module works alongside Spring Boot Actuator to provide comprehensive monitoring capabilities:

- Actuator exposes metrics and health information on port 8090
- Log levels can be dynamically changed via the `/actuator/loggers` endpoint
- MDC properties can be viewed in metrics through custom tags

For more details on monitoring and metrics, see the [User Guide: Monitoring and Management](../user_guide/monitoring.md).
