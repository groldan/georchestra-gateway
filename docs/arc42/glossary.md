# Glossary

This glossary provides definitions for terms and acronyms used throughout the geOrchestra Gateway documentation.

## A

**Access Rule**
: A configuration rule that determines which users can access specific resources based on their roles and other attributes.

**Authentication**
: The process of verifying the identity of a user or service. The Gateway supports LDAP, OAuth2/OpenID Connect, and header-based pre-authentication.

**Authorization**
: The process of determining if an authenticated user has permission to access a specific resource.

**Authorization Manager**
: A component that implements the logic for deciding if a user can access a resource.

## D

**Data Directory**
: A file system directory containing configuration files for geOrchestra components. The Gateway reads its configuration from `${georchestra.datadir}/gateway/`.

## F

**Filter**
: A component in Spring Cloud Gateway that intercepts and modifies HTTP requests and responses.

**Filter Chain**
: The sequence of filters that process each request and response.

**Filter Factory**
: A component that creates filters based on configuration.

## G

**Gateway**
: A service that acts as an entry point for client requests, routing them to the appropriate backend services after performing tasks like authentication, authorization, and request transformation.

**Gateway Filter**
: A filter that is applied to a specific route.

**geOrchestra**
: A free, modular, and interoperable Spatial Data Infrastructure (SDI) solution that follows open standards.

**Global Filter**
: A filter that is applied to all routes.

## H

**Header Authentication**
: Authentication method where user identity information is provided by HTTP headers set by an upstream component like a proxy server.

**Header Contributor**
: A component that provides specific HTTP headers to be added to outgoing requests.

**Header Forwarding**
: The process of adding HTTP headers to requests sent to backend services to communicate user information.

## L

**LDAP (Lightweight Directory Access Protocol)**
: A protocol for accessing and maintaining distributed directory information. Used by geOrchestra for user authentication and user attribute management.

## M

**MDC (Mapped Diagnostic Context)**
: A mechanism to enrich log messages with contextual information such as user ID, request ID, etc.

## O

**OAuth2**
: An authorization framework that enables third-party applications to obtain limited access to a user's account on an HTTP service.

**OGC (Open Geospatial Consortium)**
: An international voluntary consensus standards organization that develops standards for geospatial content and services.

**OpenID Connect**
: An identity layer on top of the OAuth 2.0 protocol that allows clients to verify the identity of end-users based on the authentication performed by an authorization server.

**Organization**
: A logical grouping of users in geOrchestra. Users can belong to one organization at a time.

## P

**Pre-authentication**
: Authentication mechanism where user identity is verified by a component upstream of the Gateway, and the user information is passed via HTTP headers.

**Predicate**
: A condition that is used to match a route to an incoming request.

**PSC (Project Steering Committee)**
: The governing body that oversees the geOrchestra project.

## R

**Role**
: A named collection of permissions that can be assigned to users. Used to control access to resources.

**Role Mapping**
: The process of translating roles from one format to another, often used to map external identity provider roles to geOrchestra-specific roles.

**Route**
: A configuration element that defines a mapping from a request to a backend service, including predicates and filters to apply.

**Reactive Programming**
: A programming paradigm oriented around data flows and the propagation of change. The Gateway uses Spring WebFlux, a reactive web framework.

## S

**SDI (Spatial Data Infrastructure)**
: A framework of geographic data, metadata, users, and tools that are connected to use spatial data in an efficient and flexible way.

**Sec Headers**
: HTTP headers used by geOrchestra components to communicate user information. Typically prefixed with `sec-`.

**Security Context**
: An object that holds authentication information for the current user.

**Spring Cloud Gateway**
: A framework for building API gateways on top of Spring WebFlux. The foundation for the geOrchestra Gateway.

**Spring WebFlux**
: A reactive web framework that is part of the Spring Framework, designed for use in non-blocking, event-loop execution models.

## T

**Target Service**
: A backend service that the Gateway routes requests to after authentication and authorization.

## U

**User Customizer**
: A component that extends or modifies user attributes after authentication.

**User Mapper**
: A component that maps authentication information to a GeorchestraUser object.

## W

**WebFlux**
: A reactive web framework that is part of the Spring Framework, designed for non-blocking applications. The Gateway is built on WebFlux for high concurrency support.

**Whoami Endpoint**
: An API endpoint that returns information about the currently authenticated user.