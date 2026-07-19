# Issue #004: OpenTelemetry TelemetryProvider utility class

**Type**: Feature
**Priority**: Medium
**Status**: ✅ Implemented
**Closed**: 2026-03-18
**Created**: 2026-03-18
**Labels**: feature, observability, opentelemetry, tracing
**Related**: portunix-plugins#050, ADR-016
**Repository**: Api

## Description

Create a shared `TelemetryProvider` utility class in the API module (`org.cassandragargoyle.api.telemetry`) that provides centralized OpenTelemetry TracerProvider initialization for all Portunix plugins (Java, and potentially as a reference for Go/Python).

This is a prerequisite for Issue #050 in portunix-plugins (fulltext plugin OTel tracing).

## Requirements

### 1. Add OpenTelemetry dependencies to API module pom.xml

```xml
<!-- OpenTelemetry BOM for version management -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-bom</artifactId>
    <version>1.40.0</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- Core dependencies -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-logging</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk-extension-autoconfigure</artifactId>
</dependency>
```

### 2. TelemetryProvider class

**Package**: `org.cassandragargoyle.api.telemetry`

**Responsibilities**:
- Initialize `TracerProvider` based on configuration
- Support three modes: **NoOp** (default), **Console** (development), **OTLP** (production)
- Provide `Tracer` instances via `getTracer(String instrumentationName)`
- Support OTel standard environment variable overrides (`OTEL_*`)
- Handle graceful shutdown (flush pending spans)

**Public API**:

```java
public class TelemetryProvider {
    // Builder pattern for configuration
    public static Builder builder(String serviceName) { ... }

    // Get tracer for instrumentation
    public Tracer getTracer(String instrumentationName) { ... }

    // Shutdown and flush
    public void shutdown() { ... }

    // NoOp instance (singleton, zero overhead)
    public static TelemetryProvider noOp() { ... }

    public static class Builder {
        Builder serviceName(String name);
        Builder enabled(boolean enabled);
        Builder exporter(ExporterType type);  // NONE, CONSOLE, OTLP
        Builder endpoint(String endpoint);     // OTLP endpoint
        Builder protocol(String protocol);     // grpc | http
        Builder sampleRate(double rate);       // 0.0 - 1.0
        TelemetryProvider build();
    }

    public enum ExporterType {
        NONE, CONSOLE, OTLP
    }
}
```

### 3. Design principles

- **Zero overhead when disabled**: `noOp()` returns OTel NoOp tracer, no SDK initialization
- **Thread-safe**: singleton-safe TracerProvider
- **Standard OTel env vars**: `OTEL_EXPORTER_OTLP_ENDPOINT`, `OTEL_SERVICE_NAME`, `OTEL_TRACES_SAMPLER`, `OTEL_TRACES_EXPORTER` override programmatic config
- **Follows existing patterns**: similar to `LogFactory` - static factory, utility class style
- **No Spring dependency**: pure Java, usable from any plugin

## Acceptance Criteria

1. `TelemetryProvider.noOp()` returns a functional provider with zero overhead
2. Console exporter prints spans to stdout in readable format
3. OTLP exporter connects to configurable endpoint
4. OTel environment variables override programmatic configuration
5. `shutdown()` flushes pending spans before application exit
6. Unit tests cover all three modes (NoOp, Console, OTLP)

## Test Plan

- [ ] Unit test: `noOp()` creates valid provider, `getTracer()` returns NoOp tracer
- [ ] Unit test: Console exporter mode produces output
- [ ] Unit test: Builder validation (missing service name, invalid sample rate)
- [ ] Unit test: environment variable override behavior

## References

- [ADR-016: OpenTelemetry as Tracing Foundation](../../../portunix-plugins/docs/adr/016-opentelemetry-tracing.md)
- [OpenTelemetry Java SDK](https://opentelemetry.io/docs/languages/java/)
- Existing pattern: `org.cassandragargoyle.api.log.LogFactory`
