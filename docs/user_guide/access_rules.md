# Security Access Rules

Security access rules define how the geOrchestra Gateway controls access to different services and endpoints based on user roles.

## Configuration Structure

Security access rules are organized in a hierarchical structure:

1. **Global Headers**: Default header settings applied to all services
2. **Global Access Rules**: Security rules applied to all services
3. **Services**: Individual application configurations 
     - Each service can have its own URL target
     - Each service can override header settings
     - Each service can define specific access rules

This structure allows for both global defaults and service-specific customizations.

## Externalized Configuration

Access rules are configured in the `gateway.yaml` file:

```yaml
georchestra:
  gateway:
    default-headers:
      proxy: true
      username: true
      roles: true
      org: true
      orgname: true
    global-access-rules:
    - intercept-url: /**
      anonymous: true
    services:
      analytics:
        target: http://analytics:8080/analytics/
        access-rules:
        - intercept-url: /analytics/**
          allowed-roles: SUPERUSER, ORGADMIN
      atlas:
        target: http://atlas:8080/atlas/
      console:
        target: http://console:8080/console/
        access-rules:
        - intercept-url:
          - /console/public/**
          - /console/manager/public/**
          anonymous: true
        - intercept-url:
          - /console/private/**
          - /console/manager/**
          allowed-roles: SUPERUSER, ORGADMIN
```

## Understanding Access Rules

Each service can have its own set of access rules. Access rules are defined using the following properties:

- `intercept-url`: The URL pattern to match against the request path
- `anonymous`: Whether the URL can be accessed by anonymous users
- `allowed-roles`: The roles allowed to access the URL

Access rules are evaluated in order, with the first matching rule being applied. If no rule matches, access is denied.

### Global Access Rules

Global access rules apply to all services:

```yaml
georchestra:
  gateway:
    global-access-rules:
    - intercept-url: /**
      anonymous: true
```

This rule allows anonymous access to all URLs. More specific rules can be defined at the service level to override this.

### Service-Specific Access Rules

Service-specific access rules apply only to the specified service:

```yaml
georchestra:
  gateway:
    services:
      console:
        target: http://console:8080/console/
        access-rules:
        - intercept-url:
          - /console/public/**
          - /console/manager/public/**
          anonymous: true
        - intercept-url:
          - /console/private/**
          - /console/manager/**
          allowed-roles: SUPERUSER, ORGADMIN
```

In this example:
- `/console/public/**` and `/console/manager/public/**` can be accessed by anonymous users
- `/console/private/**` and `/console/manager/**` can only be accessed by users with the SUPERUSER or ORGADMIN role

### Connecting Services to Routes

!!! important "Service URL and Route URL Relationship"
    The `target` URL defined for each geOrchestra service **must match** a route URL configured in `routes.yaml`. This is how a Gateway route is mapped to a geOrchestra service, and how the service's authorization rules get applied to the route.
    
    For example, if you define a service:
    ```yaml
    services:
      console:
        target: http://console:8080/console/
    ```
    
    There must be a corresponding route in `routes.yaml` with the same URL:
    ```yaml
    - id: console
      uri: http://console:8080/console/
      predicates:
        - Path=/console/**
    ```
    
    Without this matching, the access rules defined for the service won't be applied to the route.

!!! note "Future Improvements"
    We recognize this dual configuration requirement is inconvenient. In a future iteration, we plan to merge the geOrchestra service configuration with the Gateway routes to create a more streamlined configuration experience.

## URL Patterns

URL patterns use Ant-style path patterns:

- `?` matches one character
- `*` matches zero or more characters
- `**` matches zero or more directories

For example:

- `/console/*.html` matches all HTML files in the root console directory
- `/console/**` matches all files in the console directory and its subdirectories

## Role Mapping

Role mappings can be used to map external roles (e.g., from OAuth providers) to geOrchestra roles. This is configured in the `security.yaml` file:

```yaml
georchestra:
  gateway:
    roles-mappings:
      ADMIN: ADMINISTRATOR
      USER: USER
```

In this example, users with the external role "ADMIN" will be given the geOrchestra role "ADMINISTRATOR".
