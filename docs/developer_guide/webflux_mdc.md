# WebFlux MDC Propagation

The geOrchestra Gateway includes a sophisticated mechanism for propagating Mapped Diagnostic Context (MDC) information throughout reactive WebFlux chains. This document explains how this mechanism works and how you can leverage it in your own code.

This guide focuses on the technical details of using WebFlux MDC propagation. For an overview of the general logging system, see the [Logging Module](logging.md) guide.

## Problem: MDC in Reactive Applications

In traditional servlet-based applications, MDC (Mapped Diagnostic Context) is tied to the current thread. Since a single request is processed entirely within a single thread, MDC values set at the beginning of a request are available throughout the entire request processing lifecycle.

However, in reactive applications like those built with Spring WebFlux, a single request may be processed across multiple threads or executed asynchronously. This means that thread-local MDC values set in one part of the processing chain are not automatically available in other parts of the chain.

## Solution: ReactorContextHolder

The geOrchestra Gateway uses a custom `ReactorContextHolder` class to solve this problem. This utility coordinates between Reactor's `Context` and the thread-local MDC.

### How It Works

The WebFlux MDC propagation system works as follows:

1. A WebFilter intercepts incoming requests and extracts relevant information (request ID, URL, method, etc.)
2. It populates a map with MDC key-value pairs
3. This map is stored in the Reactor Context using a standard key
4. Operators in the reactive chain can access the MDC map from the Context when needed
5. When code needs to log something, it retrieves the MDC map from the Context and temporarily sets it in the thread-local MDC

## Core Components

### ReactorContextHolder

The `ReactorContextHolder` class is the central component of the WebFlux MDC propagation mechanism. It provides static methods to interact with MDC in reactive contexts:

```java
public class ReactorContextHolder {

    public static final String MDC_CONTEXT_KEY = "MDC";

    /**
     * Gets the MDC map from the current thread's MDC
     */
    public static Map<String, String> getMdcMap() {
        return MDC.getCopyOfContextMap() != null ? 
               MDC.getCopyOfContextMap() : new HashMap<>();
    }

    /**
     * Sets the thread-local MDC from a map
     */
    public static void setThreadLocalMdc(Map<String, String> mdcValues) {
        if (mdcValues != null && !mdcValues.isEmpty()) {
            MDC.clear();
            mdcValues.forEach(MDC::put);
        }
    }

    /**
     * Sets the thread-local MDC from Reactor context
     */
    public static void setMdcFromContext(Context context) {
        if (context.hasKey(MDC_CONTEXT_KEY)) {
            Map<String, String> mdcMap = context.get(MDC_CONTEXT_KEY);
            setThreadLocalMdc(mdcMap);
        }
    }
}
```

### MDCWebFilter

The `MDCWebFilter` initializes the MDC context for each request:

```java
@Component
public class MDCWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Create MDC map with request info
        Map<String, String> mdcContext = buildMdcMap(exchange.getRequest());
        
        // Add MDC map to Reactor Context
        return chain.filter(exchange)
                .contextWrite(context -> 
                    context.put(ReactorContextHolder.MDC_CONTEXT_KEY, mdcContext));
    }
    
    private Map<String, String> buildMdcMap(ServerHttpRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("http.request.id", UUID.randomUUID().toString());
        map.put("http.request.method", request.getMethodValue());
        map.put("http.request.url", request.getURI().getPath());
        // Add other attributes as needed
        return map;
    }
}
```

## Extending the Logging Module

### Adding Custom MDC Attributes

To add custom MDC attributes, you can create a new filter that adds your attributes to the MDC context:

```java
@Component
public class CustomMdcFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
            .contextWrite(context -> {
                Map<String, String> mdcMap = context.getOrEmpty(ReactorContextHolder.MDC_CONTEXT_KEY)
                    .map(map -> new HashMap<>((Map<String, String>) map))
                    .orElseGet(HashMap::new);

                // Add custom MDC attributes
                mdcMap.put("custom.attribute", "custom-value");

                return context.put(ReactorContextHolder.MDC_CONTEXT_KEY, mdcMap);
            });
    }

    @Override
    public int getOrder() {
        return OrderedWebFilter.HIGHEST_PRECEDENCE + 10; // Run early in the filter chain
    }
}
```

## Using MDC in Reactive Components

### Basic Logger Usage

To use MDC values in your reactive code, you need to temporarily set the MDC context before logging:

```java
import reactor.util.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactiveService {
    private final Logger logger = LoggerFactory.getLogger(ReactiveService.class);

    public Mono<String> processData(String data) {
        return Mono.just(data)
            .doOnEach(signal -> {
                if (signal.isOnNext() || signal.isOnError()) {
                    Context context = signal.getContext();
                    try {
                        // Set MDC from Context
                        ReactorContextHolder.setMdcFromContext(context);
                        // Log with MDC values
                        logger.info("Processing data: {}", data);
                    } finally {
                        // Always clear MDC
                        MDC.clear();
                    }
                }
            })
            .map(this::transform);
    }
    
    private String transform(String data) {
        // Processing logic
        return data.toUpperCase();
    }
}
```

### Creating MDC-Aware Logger Utilities

You can create a custom reactive logging handler that automatically includes MDC context:

```java
public class MdcAwareLogger {
    private final Logger logger;
    
    public MdcAwareLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    
    public <T> Mono<T> logInfo(Mono<T> mono, String message) {
        return mono.doOnEach(signal -> {
            if (signal.isOnNext() || signal.isOnError()) {
                Context context = signal.getContext();
                if (context.hasKey(ReactorContextHolder.MDC_CONTEXT_KEY)) {
                    Map<String, String> mdcMap = context.get(ReactorContextHolder.MDC_CONTEXT_KEY);
                    try {
                        ReactorContextHolder.setThreadLocalMdc(mdcMap);
                        logger.info(message);
                    } finally {
                        MDC.clear();
                    }
                } else {
                    logger.info(message);
                }
            }
        });
    }
    
    // Add more methods as needed (logDebug, logError, etc.)
}
```

Usage:

```java
public class MyService {
    private final MdcAwareLogger logger = new MdcAwareLogger(MyService.class);
    
    public Mono<Response> handleRequest(Request request) {
        return Mono.just(request)
            .map(this::process)
            .transform(mono -> logger.logInfo(mono, "Request processed successfully"));
    }
}
```

## Modifying MDC Values in a Reactive Chain

Sometimes you need to add or modify MDC values during the execution of a reactive chain:

```java
public Mono<Response> processWithExtraContext(Request request) {
    return Mono.just(request)
        .map(this::validate)
        // Add or modify MDC values
        .contextWrite(context -> {
            Map<String, String> mdcMap = context.getOrEmpty(ReactorContextHolder.MDC_CONTEXT_KEY)
                .map(map -> new HashMap<>((Map<String, String>) map))
                .orElseGet(HashMap::new);
            
            // Add a new MDC value
            mdcMap.put("process.stage", "validation");
            
            return context.put(ReactorContextHolder.MDC_CONTEXT_KEY, mdcMap);
        })
        .map(this::transform)
        // Add more MDC values in another stage
        .contextWrite(context -> {
            Map<String, String> mdcMap = context.getOrEmpty(ReactorContextHolder.MDC_CONTEXT_KEY)
                .map(map -> new HashMap<>((Map<String, String>) map))
                .orElseGet(HashMap::new);
            
            mdcMap.put("process.stage", "transformation");
            
            return context.put(ReactorContextHolder.MDC_CONTEXT_KEY, mdcMap);
        });
}
```

## Testing

When testing code that relies on MDC propagation, you need to ensure the Reactor Context contains the expected MDC values:

```java
@Test
void testMdcPropagation() {
    // Create test MDC map
    Map<String, String> testMdc = new HashMap<>();
    testMdc.put("test-key", "test-value");
    
    // Create a Mono with MDC context
    Mono<String> monoWithMdc = Mono.just("test-data")
        .contextWrite(context -> context.put(ReactorContextHolder.MDC_CONTEXT_KEY, testMdc));
    
    // Test that processing preserves MDC
    StepVerifier.create(monoWithMdc
            .flatMap(service::process)
            .doOnEach(signal -> {
                if (signal.isOnNext()) {
                    Context context = signal.getContext();
                    Map<String, String> mdcMap = context.get(ReactorContextHolder.MDC_CONTEXT_KEY);
                    // Verify MDC is preserved
                    assertThat(mdcMap).containsEntry("test-key", "test-value");
                }
            }))
            .expectNextMatches(result -> result.equals("EXPECTED_RESULT"))
            .verifyComplete();
}
```

## Best Practices

1. **Always clear MDC**: After using MDC, always clear it to prevent leaking MDC values to other operations

2. **Use doOnEach**: Prefer using `doOnEach` over `doOnNext` or `doOnError` to access signal context

3. **Create helper utilities**: Create MDC-aware utilities to simplify common logging patterns

4. **Use contextWrite correctly**: When adding to existing MDC values, make sure to retrieve the existing map first

5. **Don't overuse MDC**: Only include important diagnostic information in MDC to avoid performance overhead

6. **Be careful with flatMap**: When using `flatMap`, context is automatically propagated but might need special handling in complex flows

## Testing MDC Propagation

### Using StepVerifier

You can test MDC propagation using Reactor's `StepVerifier`:

```java
@Test
void testMdcPropagation() {
    // Setup
    Map<String, String> mdcMap = new HashMap<>();
    mdcMap.put("test-key", "test-value");

    // Create a Mono with MDC context
    Mono<String> monoWithMdc = Mono.just("test")
        .contextWrite(ctx -> ctx.put(ReactorContextHolder.MDC_CONTEXT_KEY, mdcMap));

    // Test that MDC is propagated
    StepVerifier.create(monoWithMdc
            .doOnEach(signal -> {
                if (signal.isOnNext()) {
                    Context context = signal.getContext();
                    ReactorContextHolder.setMdcFromContext(context);
                    // Verify MDC is set correctly
                    assertThat(MDC.get("test-key")).isEqualTo("test-value");
                }
            }))
            .expectNext("test")
            .verifyComplete();
}
```

### Mock Testing with Server WebExchange

You can test WebFlux filters using a mock `ServerWebExchange`:

```java
@Test
void testMdcFilter() {
    // Setup
    MdcWebFilter filter = new MdcWebFilter();

    // Mock ServerWebExchange
    ServerHttpRequest request = MockServerHttpRequest
        .get("/api/test")
        .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    // Mock WebFilterChain
    WebFilterChain chain = mock(WebFilterChain.class);
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Execute filter
    filter.filter(exchange, chain).block();

    // Verify
    verify(chain).filter(exchange);
}
```

## Performance Considerations

- MDC propagation adds some overhead to the reactive execution chain
- Use it judiciously, especially for high-throughput applications
- Consider using sampling for very high volume logs
- The implementation is optimized to minimize the performance impact while maintaining context
