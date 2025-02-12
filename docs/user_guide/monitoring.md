# Monitoring and Management

The geOrchestra Gateway provides comprehensive monitoring and management capabilities through Spring Boot Actuator. These features help administrators monitor the Gateway's health, track performance metrics, and manage its runtime behavior.

## Actuator Overview

Spring Boot Actuator adds several production-ready endpoints to the Gateway that provide operational information about the running application. These endpoints are exposed separately from the main application on port 8090 by default.

## Actuator Configuration

Actuator is configured in `application.yml` with the following default settings:

```yaml
management:
  server:
    port: 8090  # Dedicated port for management endpoints
  info:
    build.enabled: true
    java.enabled: true
    env.enabled: true
    git:
      enabled: true
      mode: simple
  endpoints:
    enabled-by-default: true
    web.exposure.include: "*"  # Exposes all endpoints
  endpoint:
    info.enabled: true
    metrics.enabled: true
    shutdown.enabled: true
    health:
      enabled: true
      probes.enabled: true
      show-details: always
```

## Key Endpoints

### Health Information

Health endpoints provide information about the Gateway's operational state:

- `/actuator/health` - Overall system health with component status
- `/actuator/health/liveness` - Simple check if the application is running
- `/actuator/health/readiness` - Checks if the application is ready to handle requests

Example health response:

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500107862016,
        "free": 365296930816,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ldap": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Metrics

Metrics endpoints provide performance and usage statistics:

- `/actuator/metrics` - Lists all available metrics categories
- `/actuator/metrics/{metric.name}` - Details of a specific metric
- `/actuator/prometheus` - Metrics in Prometheus format for monitoring systems

Example metrics include:
- `http.server.requests` - HTTP request statistics
- `jvm.memory.used` - JVM memory usage
- `system.cpu.usage` - CPU usage
- `process.uptime` - Application uptime

### Information and Environment

These endpoints provide details about the application configuration:

- `/actuator/info` - Application information (version, git details)
- `/actuator/env` - Environment and configuration properties
- `/actuator/configprops` - Configuration properties with current values
- `/actuator/beans` - Spring beans in the application context

### Logging

The logging endpoints allow viewing and modifying log levels at runtime:

- `/actuator/loggers` - Shows all configured loggers
- `/actuator/loggers/{name}` - Get or update the log level for a specific logger

To change a log level temporarily (until restart):

```bash
curl -X POST http://localhost:8090/actuator/loggers/org.georchestra.gateway \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

### Request Mappings

View all request mappings in the application:

- `/actuator/mappings` - Detailed information about request mappings

## Securing Actuator Endpoints

By default, Actuator endpoints are exposed on a separate port (8090) for security reasons. In production environments, you should:

1. Configure firewall rules to restrict access to the Actuator port
2. Use Spring Security to require authentication for sensitive endpoints
3. Consider exposing only essential endpoints

Example secure configuration:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
```

## Monitoring Integration

### Prometheus and Grafana

The Gateway can be monitored using Prometheus and Grafana:

1. Configure Prometheus to scrape the `/actuator/prometheus` endpoint
2. Set up Grafana dashboards to visualize the metrics

Example Prometheus configuration:

```yaml
scrape_configs:
  - job_name: 'georchestra-gateway'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['gateway:8090']
```

### Health Checks in Kubernetes

For Kubernetes deployments, the Gateway's health endpoints can be used for liveness and readiness probes:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8090
  initialDelaySeconds: 120
  periodSeconds: 30
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8090
  initialDelaySeconds: 60
  periodSeconds: 10
```

## Troubleshooting with Actuator

Actuator endpoints are valuable for troubleshooting issues:

1. Check `/actuator/health` for component status
2. Examine `/actuator/env` to verify configuration
3. Use `/actuator/loggers` to enable DEBUG logging for problem areas
4. Review metrics at `/actuator/metrics` to identify performance bottlenecks

For example, to debug authentication issues:

```bash
# Enable debug logging for security components
curl -X POST http://localhost:8090/actuator/loggers/org.georchestra.gateway.security \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```