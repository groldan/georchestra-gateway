# Proxying Applications

One of the main functions of geOrchestra Gateway is to proxy requests to backend applications. This document describes how to add a new application to the gateway.

## Adding a New Application

To add a new application to geOrchestra Gateway, you need to:

1. Configure the route in `routes.yaml`
2. Configure access rules in `gateway.yaml`
3. Configure headers forwarding

### Step 1: Configure Routes

Routes are defined in `gateway/routes.yaml` within the geOrchestra data directory. Changes to this file require restarting the gateway.

First, define your application's target URL:

```yaml
georchestra.gateway.services:
  myapp.target: http://localhost:8280/app_path/
```

Then, define the route:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: myappname
          uri: ${georchestra.gateway.services.myapp.target}
          predicates:
            - Path=/app_path,/app_path/**
```

- `id` is the name of the route
- `uri` points to the internal target
- `predicates` defines when this route is used (in this case, when the path starts with `/app_path`)

### Step 2: Configure Access Rules

Access rules are defined in `gateway/gateway.yaml`. They control which users can access which parts of your application.

```yaml
georchestra:
  gateway:
    services:
      myappbackend:
        target: http://localhost:8080/myapp/backend/
        access-rules:
        - intercept-url: /myapp/backend/admin*
          allowed-roles: ADMINISTRATOR
        - intercept-url: /myapp/backend/public*
          anonymous: true
      myappfrontend:
        target: http://localhost:80/myapp/frontend
        access-rules:
        - intercept-url: /myapp/**
          anonymous: true
        headers:
          proxy: true
          username: false
          roles: false
          org: false
          orgname: true
          json-user: true
```

In this example:

- `/myapp/backend/admin*` URLs are restricted to users with the ADMINISTRATOR role
- `/myapp/backend/public*` URLs are accessible to anonymous users
- All other `/myapp/**` URLs are accessible to anonymous users

!!! important "Matching Service Target with Route URI"
    For the access rules to be applied correctly, the `target` URL defined for each service in `gateway.yaml` **must exactly match** the corresponding `uri` defined in the route configuration in `routes.yaml`. 
    
    The Gateway uses this matching to determine which service's access rules to apply to each request. If they don't match, the access rules won't be applied.

### Step 3: Cookie Affinity Mapping

Sometimes, cookies sent by one backing service need to be readable by another. The Gateway will set a cookie path to all backend service cookies to match the service base path. Cookie Affinity Mapping allows you to duplicate cookies set to one path with another path.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: geonetwork
          uri: ${georchestra.gateway.services.geonetwork.target}
          predicates:
          - Path=/geonetwork/**
          filters:
          - name: CookieAffinity
            args:
              name: XSRF-TOKEN
              from: /geonetwork
              to: /datahub
```

## Headers

The geOrchestra gateway adds specific HTTP headers to requests forwarded to your application:

- `sec-proxy` indicates that the request comes from the proxy
- `sec-username` contains the username (not provided for anonymous users)
- `sec-roles` is a semi-colon separated list of roles (not provided for anonymous users)
- `sec-org` is the organization ID
- `sec-orgname` is the human-readable organization name
- `sec-email` is the user's email
- `sec-firstname` is the user's first name
- `sec-lastname` is the user's last name
- `sec-tel` is the user's telephone number
- `sec-json-user` is a Base64-encoded JSON representation of the user object
- `sec-json-organization` is a Base64-encoded JSON representation of the organization object

You can configure which headers are sent to each service using the `headers` section in `gateway.yaml`:

```yaml
georchestra:
  gateway:
    default-headers:
      proxy: true
      username: true
      roles: true
      org: true
      orgname: true
    services:
      myapp:
        headers:
          proxy: true
          username: true
          roles: false
          # Override other headers as needed
```

## Authentication Endpoints

The following endpoints are available for authentication:

- `/login` - Login page
- Any URL with the `login` GET parameter (e.g., `/myapp/frontend/?login`) - Forces login
- `/logout` - Logout endpoint
- `/console/account/passwordRecovery` - Password recovery form
- `/console/account/new` - Account creation form

Your application can link to these endpoints as needed.

## Adding geOrchestra Header

To add the geOrchestra header to your application, implement a `script` and `<geor-header>` tag in your application. For full configuration, see the [header repository](https://github.com/georchestra/header/).