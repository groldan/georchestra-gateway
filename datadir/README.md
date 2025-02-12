# geOrchestra Gateway Development Datadir

This directory contains development configuration files for the geOrchestra Gateway module. These files are intended **for development purposes only** and should not be used in production environments.

For production, you should use the official geOrchestra datadir from the [georchestra/datadir](https://github.com/georchestra/datadir) repository.

The structure is similar to the production datadir, but contains development-specific settings and is used with the development Docker Compose files in the repository root.

## Development Configuration

This datadir contains configuration files used by the Gateway and related services when running with the development Docker Compose configurations (`docker-compose.yml` and `docker-compose-preauth.yaml`).

At startup, the Gateway will read this configuration when the `georchestra.datadir` parameter is set, which happens automatically when using the provided Docker Compose files.


## Development Usage

When using the development Docker Compose files, this datadir is automatically mounted into the containers and used for configuration. No additional setup is required.

If you want to run the Gateway in development mode outside of Docker:

1. Run the Gateway with the datadir parameter pointing to this directory:
   ```bash
   # Using Maven
   mvn spring-boot:run -Dgeorchestra.datadir=/path/to/datadir
   
   # Or with Java
   java -Dgeorchestra.datadir=/path/to/datadir -jar gateway/target/georchestra-gateway-X.Y.Z.jar
   ```

2. Configure your IDE run/debug configuration to include the parameter:
   ```
   -Dgeorchestra.datadir=/path/to/datadir
   ```

Remember that these configurations are only suitable for development and should not be used in production environments.

## Development Customization

This datadir is pre-configured for development purposes with a default setup. However, you may want to customize some settings:

1. **FQDN**: The default FQDN is configured for local development. If you need to change it for your dev environment:
   ```bash
   cd /path/to/datadir
   find ./ -type f -exec sed -i 's/georchestra-127-0-1-1.traefik.me/my.dev.fqdn/' {} \;
   ```

2. **Security Passwords**: For security in development, you may want to change default passwords. However, remember that this is a development environment and should never be exposed publicly.

3. **Configuration Files**: Explore and modify the configuration files in the datadir to experiment with different Gateway configurations.

Remember to restart the Gateway service after making changes to the datadir.


## Development vs Production

For a production environment, you should:

1. Use the official [geOrchestra datadir repository](https://github.com/georchestra/datadir)
2. Follow the [Installation Guide](https://docs.georchestra.org/gateway/user_guide/installation/) for proper deployment
3. Use the official [geOrchestra Docker Compose project](https://github.com/georchestra/docker) if deploying with Docker

The docker-compose files in the repository root and this datadir are specifically designed for developer convenience and should not be used for any production deployment.

