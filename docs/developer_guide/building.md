# Building geOrchestra Gateway

This document describes how to build the geOrchestra Gateway from source and set up a development environment.

!!! note "Development Environment"
    The repository contains `docker-compose.yml` and `docker-compose-preauth.yaml` files in the root directory. These are **for development purposes only** and should not be used for production deployments. They provide a convenient way to run a local development environment with all required services.

## Prerequisites

Before you can build the geOrchestra Gateway, you need:

- Java 21 or higher
- Maven 3.6.3 or higher
- Git

## Clone the Repository

First, clone the repository:

```bash
git clone https://github.com/georchestra/georchestra-gateway.git
cd georchestra-gateway
```

## Build Commands

geOrchestra Gateway uses Maven as its build system. Here are the main build commands:

### Basic Build

To build the project:

```bash
./mvnw clean install
```

Or using the Makefile:

```bash
make install
```

This will:

1. Clean any previous build artifacts
2. Compile the code
3. Run the tests
4. Install the artifacts to your local Maven repository

### Skip Tests

To build without running tests:

```bash
./mvnw clean install -DskipTests
```

Or using the Makefile:

```bash
make install
```

### Run Tests

To run the tests:

```bash
./mvnw verify
```

Or using the Makefile:

```bash
make test
```

### Run a Single Test

To run a single test class:

```bash
./mvnw test -pl :georchestra-gateway -ntp -Dtest=TestClassName
```

For integration tests:

```bash
./mvnw verify -pl :georchestra-gateway -ntp -Dit.test=ITClassName
```

### Build Docker Image

To build the Docker image:

```bash
./mvnw package -f gateway/ -Pdocker -ntp -DskipTests
```

Or using the Makefile:

```bash
make docker
```

### Format Code

To format the code according to the project's style guidelines:

```bash
./mvnw formatter:format -pl :georchestra-gateway
```

### Validate Formatting

To validate that the code follows the project's style guidelines:

```bash
./mvnw formatter:validate -pl :georchestra-gateway
```

## Development Environment

### Running from the Command Line

The Gateway can be run directly from the command line during development:

```bash
# Run with spring-boot maven plugin
./mvnw spring-boot:run -Dspring-boot.run.profiles=json-logs,dev -pl :georchestra-gateway
```

This runs the Gateway with the following profiles:

- `json-logs` - Enables JSON-formatted logging for better readability during development
- `dev` - Configures the Gateway to load configuration from the local development datadir (`../datadir/`) instead of the default location (`/etc/georchestra/`)

You can specify other profiles as needed, based on the functionality you want to enable:

- `logging_debug` - Enable detailed debug logging
- `preauth` - Enable pre-authentication mode
- `docker` - Use Docker-specific settings

Alternatively, you can use environment variables to set profiles:

```bash
# Using environment variables
export SPRING_PROFILES_ACTIVE=json-logs
./mvnw spring-boot:run -pl :georchestra-gateway
```

### Running with Docker Compose

The repository includes two Docker Compose files for local development:

- `docker-compose.yml`: Basic development setup with standard authentication
- `docker-compose-preauth.yaml`: Development setup with pre-authentication via nginx

To start the development environment:

```bash
# Standard setup
docker-compose up -d

# Or with pre-authentication
docker-compose -f docker-compose-preauth.yaml up -d
```

### Remote Debugging

To enable remote debugging when running with maven:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" -pl :georchestra-gateway
```

Then connect your IDE to port 5005.

!!! warning "Development Only"
    These Docker Compose configurations are specifically designed for development and testing. They include:

    - Debug ports exposed (5005)
    - Development-oriented memory settings
    - Local datadir binding
    - Non-production security settings

    For production deployments, please use the official [geOrchestra Docker Compose project](https://github.com/georchestra/docker) or follow the installation guide.

### Development Datadir

The repository includes a `datadir/` directory that contains development configurations. This is mounted into the containers when using the development Docker Compose files.

## Project Structure

The project is organized as follows:

- `gateway/`: Main module containing the gateway code
  - `src/main/java/`: Java source code
  - `src/main/resources/`: Resources (templates, static files, etc.)
  - `src/test/java/`: Java test code
  - `src/test/resources/`: Test resources
- `datadir/`: Development configuration files
- `docker-compose.yml`: Development Docker Compose configuration
- `docker-compose-preauth.yaml`: Development Docker Compose with pre-authentication

## Dependency Management

The project uses Maven for dependency management. The main dependencies are:

- Spring Boot
- Spring Cloud Gateway
- Spring Security
- Spring LDAP
- Lombok
- Jackson
- JUnit

Dependencies are defined in the `pom.xml` files.

## IDE Setup

### IntelliJ IDEA

1. Import the project as a Maven project
2. Enable annotation processing for Lombok
3. Set the project Java version to 21
4. Install the Spring Boot plugin

### Eclipse

1. Import the project as a Maven project
2. Install the Lombok plugin
3. Enable annotation processing
4. Set the project Java version to 21

### VS Code

1. Install the Java Extension Pack
2. Install the Lombok Annotations Support for VS Code
3. Import the project as a Maven project
