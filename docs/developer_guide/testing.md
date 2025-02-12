# Testing Guide

This guide outlines the testing approaches and tools used in the geOrchestra Gateway project to help developers write effective tests.

## Test Types

The project uses several types of tests:

1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test component interactions within the application context
3. **End-to-End Tests**: Test complete request-response cycles

## Testing Framework

The Gateway uses:

- **JUnit 5**: Primary testing framework
- **Spring Boot Test**: For Spring-specific testing support
- **Mockito**: For mocking dependencies
- **AssertJ**: For fluent assertions

## Test Structure

Tests should be placed in the `src/test/java` directory, mirroring the package structure of the main code:

- Unit tests: `*Test.java`
- Integration tests: `*IT.java`

## Writing Unit Tests

Unit tests should:

1. Test a single unit of functionality
2. Mock all dependencies
3. Be fast and isolated
4. Follow the Arrange-Act-Assert pattern

Example unit test:

```java
@ExtendWith(MockitoExtension.class)
class GeorchestraUserMapperTest {

    @Mock
    private RolesMappingsUserCustomizer customizer;

    @InjectMocks
    private GeorchestraUserMapper mapper;

    @Test
    void testMappingBasicUser() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("testuser");

        // Act
        GeorchestraUser user = mapper.mapUser(authentication);

        // Assert
        assertThat(user.getUsername()).isEqualTo("testuser");
    }
}
```

## Writing Integration Tests

Integration tests use Spring's test context to test component interactions:

```java
@SpringBootTest
@ActiveProfiles("test")
class HeaderPreAuthenticationConfigurationIT {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testPreAuthentication() {
        webClient.get()
                .uri("/whoami")
                .header("sec-username", "testuser")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("testuser");
    }
}
```

## Test Configuration

Spring Boot test configuration can be defined in `src/test/resources/application-test.yml` or through `@TestConfiguration` classes.

## Mock Objects

For LDAP, OAuth, and other external services, use mock implementations:

- `MockLdapServer` for LDAP testing
- `MockWebServer` for OAuth endpoint testing

## Testing Reactive Components

For testing reactive components:

1. Use `StepVerifier` from `reactor-test`
2. Test both happy paths and error scenarios
3. Verify completion signals

Example:

```java
@Test
void testReactiveFlow() {
    Flux<String> result = service.processData("test");

    StepVerifier.create(result)
        .expectNext("processed:test")
        .verifyComplete();
}
```

## Testing Security

For testing security components:

1. Use `@WithMockUser` or custom security annotations
2. Test both authenticated and unauthenticated scenarios
3. Verify proper authorization decisions

## Test Data

Store test data in:

- `src/test/resources` for files
- Test classes for small data objects
- Factories for complex test objects

## Running Tests

Run all tests:

```bash
./mvnw test
```

Run integration tests:

```bash
./mvnw verify
```

Run a specific test:

```bash
./mvnw test -Dtest=GeorchestraUserMapperTest
```

## Test Coverage

Use JaCoCo for test coverage:

```bash
./mvnw test jacoco:report
```

Coverage reports are generated in `target/site/jacoco/index.html`.