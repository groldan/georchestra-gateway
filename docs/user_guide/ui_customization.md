# UI Customization

This guide explains how to customize the user interface of the geOrchestra Gateway, including login pages, error pages, and static resources.

## Main Principles

To customize templates like the login page or error pages:

1. Copy the template files from `gateway/src/main/resources/templates` to a custom directory
2. Modify the templates to suit your needs
3. Configure the Gateway to use the custom templates

```yaml
spring:
  thymeleaf:
    prefix: file:${georchestra.datadir}/gateway/templates/
```

Create a `login.html` file in this directory to customize login, and a `logout.html` file for logout.

## Header Customization

The geOrchestra header is integrated by default on the Gateway login page using the `<geor-header></geor-header>` HTML tag.

The web component is loaded from a JavaScript file defined in the `default.properties` file:

```properties
# From georchestra datadir's default.properties
# URL to the Javascript definition of the <geor-header> web component
headerScript: https://cdn.jsdelivr.net/gh/georchestra/header@dist/header.js

# From the gateway's yaml configuration
# includes or disables the <geor-header/> web component
georchestra.gateway.headerEnabled: true
```

You can customize the header by:

1. Creating your own version of the header component
2. Updating the `headerScript` property to point to your custom implementation
3. Or completely disabling the header by setting `georchestra.gateway.headerEnabled: false`

## Error Pages

### Custom Error Pages for Gateway Errors

To customize error pages shown by the Gateway:

1. Add the following to your configuration:

```yaml
server:
  error:
    whitelabel:
      enabled: false
```

2. Create an `error` subdirectory in your templates directory
3. Create error page files named by status code (e.g., `404.html`, `500.html`)
4. Restart the Gateway

### Custom Error Pages for Application Errors

You can also use custom error pages when applications behind the Gateway return errors:

To enable globally:

```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - ApplicationError  # Intercepts error responses from backend services and renders custom error pages
```

The ApplicationError filter does the following:

1. Intercepts HTTP responses with 4xx/5xx status codes from backend services
2. Converts these error responses from backend services to render your custom error templates
3. Works only for idempotent HTTP methods (GET, HEAD, OPTIONS) that accept HTML responses
4. Uses your custom error templates (e.g., `404.html`, `500.html`) located in the `templates/error` directory

This allows you to provide a consistent error experience across all your services without requiring each backend service to implement custom error pages.

To enable on specific routes only:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: myservice
          uri: http://myservice:8080
          filters:
            - name: ApplicationError
```

## Translations

You can provide translation files for text used in your custom templates:

1. Create properties files with translations using the naming pattern `basename_LANGUAGE.properties` (e.g., `login_fr.properties` for French)
2. Use UTF-8 encoding and `key = value` format
3. Configure the basename in your configuration:

```yaml
spring:
  messages:
    basename: file:${georchestra.datadir}/gateway/templates/messages/login
```

In your templates, use the Spring Thymeleaf expressions to access translations:

```html
<h1 th:text="#{login.title}">Login</h1>
```

## Static Resources

To serve custom static files (images, CSS, JavaScript):

1. Create a directory for your static resources
2. Configure the location in your configuration:

```yaml
spring:
  web:
    resources:
      static-locations: file:${georchestra.datadir}/gateway/templates/static/
```

3. Specify the URL path pattern:

```yaml
spring:
  webflux:
    static-path-pattern: /static/**
```

!!! note "Path Collision"
    Be careful when choosing the static path pattern to avoid collisions with Gateway routes.

## Example Directory Structure

A complete example of customized UI resources might look like:

```
${georchestra.datadir}/gateway/
  ├── templates/
  │   ├── login.html
  │   ├── logout.html
  │   ├── error/
  │   │   ├── 403.html
  │   │   ├── 404.html
  │   │   └── 500.html
  │   ├── messages/
  │   │   ├── login.properties
  │   │   ├── login_fr.properties
  │   │   └── login_es.properties
  │   └── static/
  │       ├── css/
  │       │   └── custom.css
  │       ├── js/
  │       │   └── custom.js
  │       └── img/
  │           └── logo.png
  └── application.yaml
```

## Login Page Elements

The default login page includes:

- geOrchestra header
- Login form for username/password authentication
- OAuth2/OpenID Connect buttons (when configured)
- Links to account creation and password recovery

When customizing the login page, make sure to maintain these functional components to ensure a consistent user experience.
