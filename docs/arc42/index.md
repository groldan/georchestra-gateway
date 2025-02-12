# Technical Documentation (arc42)

This technical documentation follows the [arc42](https://arc42.org/) template for documenting software and system architectures.

## Overview

The geOrchestra Gateway is a specialization of Spring Cloud Gateway that provides authentication and proxying services for the geOrchestra Spatial Data Infrastructure. It acts as a central entry point for all geOrchestra applications, handling authentication, authorization, and request routing.

## geOrchestra Architecture

geOrchestra is a comprehensive Spatial Data Infrastructure (SDI) solution composed of multiple modular components that work together:

- **Security Components**:
  - **Gateway** (this component): Central entry point, handles authentication, authorization, and routing
  - **LDAP**: User directory and authentication source

- **Core Services**:
  - **GeoServer** (with GeoFence): OGC-compliant map and feature server with fine-grained access control
  - **GeoNetwork**: Metadata catalog for discovering geospatial resources
  - **GeoWebCache**: Map tile caching service
  - **MapStore**: Web mapping application for visualization and editing

- **Administrative Components**:
  - **Console**: User and organization management interface
  - **Analytics**: Usage statistics and monitoring

All these components communicate through standardized interfaces, allowing for interoperability and modular deployment. The Gateway component serves as the foundation of the security architecture, ensuring that access to backend services is properly authenticated and authorized.

## Table of Contents

1. [Introduction](introduction.md)
2. [Architecture Goals](architecture_goals.md)
3. [Context View](context_view.md)
4. [Container View](container_view.md)
5. [Component View](component_view.md)
6. [Runtime View](runtime_view.md)
7. [Deployment View](deployment_view.md)
8. [Crosscutting Concerns](crosscutting.md)
9. [Quality Requirements](quality_requirements.md)
10. [Risks and Technical Debt](risks.md)
11. [Glossary](glossary.md)

## About arc42

arc42 is a template for documenting software and system architectures. It provides a structured approach to capture all the important aspects of a software architecture, making it easier to communicate and understand the system's design.

The template consists of 12 sections (we've combined some in this documentation):

1. Introduction and Goals
2. Constraints
3. Context and Scope
4. Solution Strategy
5. Building Block View
6. Runtime View
7. Deployment View
8. Crosscutting Concepts
9. Architecture Decisions
10. Quality Requirements
11. Risks and Technical Debt
12. Glossary