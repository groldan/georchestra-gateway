# Authentication Developer Guide

This guide explains the authentication mechanisms used in the geOrchestra Gateway and how to extend or customize them.

## Authentication Architecture

The geOrchestra Gateway uses Spring Security's WebFlux-based security framework, with a reactive architecture. The main authentication components are:

1. **Authentication Providers**: Handle different authentication methods
2. **User Mappers**: Convert authentication results to GeorchestraUser objects
3. **User Customizers**: Enhance user information after authentication
4. **Global Filters**: Process authentication in the request pipeline

## Authentication Methods

### LDAP Authentication

LDAP authentication is implemented in the `org.georchestra.gateway.security.ldap` package with two implementations:

1. **Basic LDAP**: Simple username/password binding with minimal user attributes
2. **Extended LDAP**: Full geOrchestra LDAP schema support with roles and organization information

Key classes:

- `LdapAuthenticationConfiguration`: Spring configuration for LDAP
- `GeorchestraLdapAuthenticationProvider`: Authentication provider implementation
- `GeorchestraLdapAuthenticatedUserMapper`: Maps LDAP users to GeorchestraUser objects

To customize LDAP authentication, implement a custom `LdapAuthenticatedUserMapper`.

### OAuth2/OpenID Connect

OAuth2 and OpenID Connect are implemented in the `org.georchestra.gateway.security.oauth2` package.

Key classes:

- `OAuth2Configuration`: Spring configuration for OAuth2
- `OpenIdConnectUserMapper`: Maps OAuth2 authentication to GeorchestraUser objects
- `OpenIdConnectCustomConfig`: Customization for specific providers

To customize OAuth2 authentication:
1. Implement a custom `OAuth2UserMapper`
2. Configure custom claim mappings in application properties

### Header Pre-Authentication

Header-based pre-authentication is implemented in the `org.georchestra.gateway.security.preauth` package.

Key classes:

- `HeaderPreAuthenticationConfiguration`: Spring configuration for pre-authentication
- `PreauthAuthenticationManager`: Validates pre-authentication headers
- `PreauthenticatedUserMapperExtension`: Maps header values to GeorchestraUser objects

To customize header pre-authentication:
1. Implement a custom `PreauthenticatedUserMapperExtension`
2. Configure custom header mappings in application properties

## The GeorchestraUser Model

The `GeorchestraUser` class is the core user model used throughout the application. It contains:

- Basic user information (username, email, etc.)
- Roles and organization details
- Extended attributes

```java
public class GeorchestraUser implements UserDetails {
    private String username;
    private String organization;
    private String email;
    private Set<String> roles;
    private Map<String, String> attributes;
    // methods omitted for brevity
}
```

## User Customizers

The `GeorchestraUserCustomizerExtension` interface allows customizing user information after authentication:

```java
public interface GeorchestraUserCustomizerExtension {
    Mono<GeorchestraUser> customize(GeorchestraUser user);
}
```

Implementations include:

- `RolesMappingsUserCustomizer`: Applies role mappings based on configuration
- `CreateAccountUserCustomizer`: Handles user creation through login

To implement a custom user customizer:
1. Create a class implementing `GeorchestraUserCustomizerExtension`
2. Register it as a Spring bean

```java
@Component
public class MyCustomUserCustomizer implements GeorchestraUserCustomizerExtension {
    @Override
    public Mono<GeorchestraUser> customize(GeorchestraUser user) {
        // Customize user here
        return Mono.just(user);
    }
}
```

## Authentication Flow

1. A request arrives at the gateway
2. Spring Security filter chain processes the request
3. Authentication is performed via the appropriate provider
4. User information is mapped to a GeorchestraUser object
5. User customizers are applied
6. Authorization decisions are made based on user roles
7. User information is added to request headers for backend services

## Security Context Propagation

In a reactive environment, the security context is propagated via the Reactor Context:

```java
public Mono<ServerResponse> handleRequest(ServerRequest request) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .flatMap(auth -> {
            GeorchestraUser user = (GeorchestraUser) auth.getPrincipal();
            // Use user information
            return ServerResponse.ok().build();
        });
}
```

## Adding a New Authentication Method

To add a new authentication method:

1. Create an authentication configuration class
2. Implement a user mapper for the new authentication method
3. Register the configuration with Spring Boot auto-configuration
4. Add configuration properties for the new method

Example structure:
```
org.georchestra.gateway.security.newauth
├── NewAuthConfiguration.java
├── NewAuthProvider.java
├── NewAuthUserMapper.java
└── NewAuthProperties.java
```

## Debugging Authentication

For debugging authentication issues:

1. Enable debug logging for security components:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.georchestra.gateway.security: DEBUG
```

2. Use the `/whoami` endpoint to check the authenticated user information

3. Monitor the authentication flow in logs

## Security Considerations

1. **Password Security**: LDAP passwords should be one-way hashed
2. **HTTPS**: Always use HTTPS in production
3. **OAuth2 Client Secrets**: Keep client secrets secure
4. **Header Security**: Validate and sanitize pre-authentication headers
5. **Role Validation**: Validate role assignments for security-critical operations