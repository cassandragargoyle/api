# ADR-001: OpenTelemetry as Tracing Foundation

**Status**: Accepted
**Date**: 2026-03-18
**Deciders**: Zdenek
**Related**: Issue #004, portunix-plugins ADR-016, portunix-plugins Issue #050

## Context

Portunix plugins operate as distributed components: CLI tools invoke text extraction, fulltext indexing, gRPC services, and external search engines (ElasticSearch). Currently, observability is limited to structured file logging. When a request traverses multiple components (e.g., `index` command calls text-extractor, then ElasticSearch), there is no way to:

1. Correlate logs across plugin boundaries
2. Measure latency of individual processing stages
3. Identify bottlenecks in multi-step pipelines (extraction -> indexing -> search)
4. Propagate trace context through gRPC calls

The ecosystem needs a vendor-neutral, standards-based tracing solution that works across Java, Go, and Python plugins. The shared API library is the natural place for the core tracing infrastructure (`TelemetryProvider`), since all Java plugins already depend on it.

### Evaluated Alternatives

| Alternative | Pros | Cons |
| ----------- | ---- | ---- |
| **OpenTelemetry** | Vendor-neutral, CNCF standard, multi-language SDKs, broad backend support | Additional dependencies, learning curve |
| **Jaeger client (direct)** | Simple, lightweight | Vendor-specific, deprecated in favor of OTel |
| **Zipkin client (direct)** | Mature, simple | Vendor-specific, limited propagation formats |
| **Custom trace IDs in logs** | No dependencies | Manual correlation, no standard propagation, no tooling |
| **Micrometer Tracing** | Spring ecosystem integration | Java-only, not applicable to Go/Python plugins |

## Decision

**Adopt OpenTelemetry (OTel) as the standard tracing foundation. Provide shared `TelemetryProvider` in the API module.**

### 1. Core Principles

- All Java plugins MUST use `TelemetryProvider` from API module for tracing initialization
- Trace context MUST be propagated across gRPC boundaries using W3C TraceContext format
- Tracing MUST be opt-in and have zero overhead when disabled (NoOp tracer by default)
- Plugins MUST NOT depend on a specific tracing backend (Jaeger, Zipkin, OTLP collector)

### 2. SDK Selection

| Language | SDK | Min Version |
| -------- | --- | ----------- |
| Java | `io.opentelemetry:opentelemetry-sdk` | 1.40+ |
| Go | `go.opentelemetry.io/otel` | 0.48+ |
| Python | `opentelemetry-sdk` | 1.25+ |

### 3. Architecture

```text
┌─────────────────────────────────────────────────────────────┐
│  CassandraGargoyle API Module                               │
│  └── org.cassandragargoyle.api.telemetry                    │
│      └── TelemetryProvider                                  │
│          ├── TracerProvider (configured per plugin)          │
│          ├── SpanProcessor  (batch or simple)               │
│          ├── SpanExporter   (OTLP, Console, NoOp)           │
│          └── TextMapPropagator (W3C TraceContext)            │
├─────────────────────────────────────────────────────────────┤
│  Plugin Usage (fulltext, text-extractor, etc.)              │
│  ├── span: "index-documents"                                │
│  │   ├── span: "extract-text"                               │
│  │   ├── span: "transform-metadata"                         │
│  │   └── span: "store-index"                                │
│  └── span: "search-query"                                   │
├─────────────────────────────────────────────────────────────┤
│  Export Layer (configurable)                                 │
│  ├── OTLP/gRPC  → OTel Collector → Backend                  │
│  ├── Console    → stdout (development)                       │
│  └── NoOp       → disabled (default)                         │
└─────────────────────────────────────────────────────────────┘
```

### 4. TelemetryProvider API

```java
// Builder pattern - clean initialization
TelemetryProvider telemetry = TelemetryProvider.builder("ptx-fulltext")
    .enabled(true)
    .exporter(ExporterType.CONSOLE)
    .build();

Tracer tracer = telemetry.getTracer("fulltext-indexing");

// NoOp mode - zero overhead (default)
TelemetryProvider noop = TelemetryProvider.noOp();
```

### 5. Configuration

Plugins configure tracing through their own config files, passing values to `TelemetryProvider.Builder`:

```yaml
telemetry:
  enabled: false          # opt-in, default off
  exporter: otlp          # otlp | console | none
  endpoint: localhost:4317 # OTLP collector endpoint
  protocol: grpc          # grpc | http
  service_name: ptx-fulltext
  sample_rate: 1.0        # 1.0 = all, 0.1 = 10%
```

Environment variable overrides (OTel standard):

- `OTEL_EXPORTER_OTLP_ENDPOINT`
- `OTEL_SERVICE_NAME`
- `OTEL_TRACES_SAMPLER`
- `OTEL_TRACES_EXPORTER`

### 6. Instrumentation Guidelines

- Create spans for operations > 1ms (I/O, network, file processing)
- Add attributes: `file.path`, `file.size`, `document.type`, `index.name`
- Record errors as span events with exception details
- Use semantic conventions from OTel specification where applicable

## Consequences

### Positive

1. **Shared infrastructure** - all Java plugins get tracing through API dependency
2. **Vendor-neutral** - switch backends (Jaeger, Zipkin, Grafana Tempo) without code changes
3. **Cross-language** - consistent tracing across Java, Go, Python plugins
4. **Standards-based** - W3C TraceContext propagation works with any OTel-compatible system
5. **Zero overhead when disabled** - NoOp tracer adds negligible cost
6. **Consistent API** - follows existing patterns (LogFactory) in the API module

### Negative

1. **Dependency footprint** - OTel SDK adds ~5-10MB to API JAR
2. **API version coupling** - plugins must update API dependency to get tracing
3. **Learning curve** - team needs to learn OTel concepts (spans, context propagation, exporters)

### Risks

| Risk | Probability | Impact | Mitigation |
| ---- | ----------- | ------ | ---------- |
| OTel API breaking changes | Low | Medium | Pin SDK versions, use stable APIs only |
| Performance impact on hot paths | Low | Medium | Use sampling, instrument only I/O operations |
| API JAR size increase | Medium | Low | OTel dependencies are compile-optional for non-tracing users |

### Rollback Strategy

1. Set `telemetry.enabled: false` in plugin config to disable without code changes
2. `TelemetryProvider.noOp()` is always available as zero-cost fallback
3. Existing logging remains unchanged and independent

## References

- [OpenTelemetry Java SDK](https://opentelemetry.io/docs/languages/java/)
- [W3C Trace Context](https://www.w3.org/TR/trace-context/)
- [OTel Semantic Conventions](https://opentelemetry.io/docs/specs/semconv/)
- Existing pattern: `org.cassandragargoyle.api.log.LogFactory`
