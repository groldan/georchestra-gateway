# About geOrchestra Gateway

<div style="text-align: center; margin-bottom: 30px;">
    <img src="../assets/images/georchestra-logo.svg" alt="geOrchestra Logo" width="300">
</div>

## About geOrchestra

[geOrchestra](https://www.georchestra.org/) is a free, modular, and interoperable Spatial Data Infrastructure (SDI) solution that provides a comprehensive suite of integrated geospatial applications. The project was born in 2009 to meet the requirements of the INSPIRE European directive and has evolved into a fully-featured platform for managing and sharing geospatial data.

geOrchestra is designed around these core principles:

- **Free and Open Source**: All components are open source, allowing for complete transparency and customization
- **Modular**: Components can be deployed selectively based on specific needs
- **Interoperable**: Built on open standards to ensure compatibility with other systems
- **Community-driven**: Development guided by actual user needs and contributions

## Project Governance

The geOrchestra project is governed by a Project Steering Committee (PSC) consisting of 9 members who ensure the project complies with its founding principles. The PSC can be contacted at [psc@georchestra.org](mailto:psc@georchestra.org).

The project uses a "geOrchestra Improvement Proposal" (GIP) process for community-driven changes, allowing stakeholders to participate in the evolution of the platform.

## geOrchestra Components

The geOrchestra platform consists of several integrated components:

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
    - **DataFeeder**: Data integration tool

## Gateway Component

The Gateway component (this project) is a Spring Cloud Gateway service that serves as the central entry point to all backend geOrchestra applications. It replaces the previous security-proxy component, offering enhanced security features and modern authentication methods.

Key features of the Gateway include:

- OAuth2 and OpenID Connect authentication
- LDAP authentication and authorization
- Role-based access control
- HTTP/2 and Websockets support
- Flexible header management for communicating with backend services

## Community Resources

- **Main website**: [georchestra.org](https://www.georchestra.org/)
- **GitHub organization**: [github.com/georchestra](https://github.com/georchestra)
- **Main mailing list**: [georchestra@googlegroups.com](mailto:georchestra@googlegroups.com)
- **Developer mailing list**: [georchestra-dev@googlegroups.com](mailto:georchestra-dev@googlegroups.com)
- **Matrix chat**: #georchestra on osgeo.org server
- **Twitter**: [@georchestra](https://twitter.com/georchestra)

The community organizes annual meetings (geOcom) where contributors and users gather to share experiences and plan future developments.

## Contributing

If you're interested in contributing to geOrchestra Gateway, please refer to the [Contributing](developer_guide/contributing.md) section in the Developer Guide.
