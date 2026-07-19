# Issue #22: Python TelemetryProvider shared module

**Type**: Feature
**Priority**: Medium
**Status**: ✅ Implemented
**Created**: 2026-03-19
**Labels**: feature, observability, opentelemetry, tracing, logging, python
**Related**: API#21 (Java TelemetryProvider), portunix-reco#INT-002
**Repository**: Api
**GitHub**: #22

## Description

Create a shared Python `TelemetryProvider` module in the API project (`cassandragargoyle.api.telemetry`) that mirrors the functionality of the existing Java `TelemetryProvider` (Issue #21). Additionally, provide standard logging utilities (`cassandragargoyle.api.log`) mirroring the Java `LogFactory` and `Logging` classes. These modules will be used by all Python-based Portunix components (starting with portunix-reco) to provide centralized OpenTelemetry TracerProvider initialization and unified logging.

This is a prerequisite for portunix-reco Issue INT-002 (OpenTelemetry tracing).

## Requirements

### 1. Python package structure in API project

Create a Python package alongside the existing Java module:

```text
api/
├── api/                          # Existing Java module
│   └── src/main/java/org/cassandragargoyle/api/telemetry/
│   └── src/main/java/org/cassandragargoyle/api/log/
├── python/                       # New Python shared modules
│   ├── pyproject.toml
│   └── src/
│       └── cassandragargoyle/
│           └── api/
│               ├── log/
│               │   ├── __init__.py
│               │   ├── factory.py
│               │   └── logging_config.py
│               └── telemetry/
│                   ├── __init__.py
│                   ├── provider.py
│                   └── exporter_type.py
```

### 2. Dependencies

```toml
[project]
dependencies = [
    "opentelemetry-api>=1.25.0",
    "opentelemetry-sdk>=1.25.0",
    "opentelemetry-exporter-otlp-proto-grpc>=1.25.0",
    "opentelemetry-exporter-otlp-proto-http>=1.25.0",
]
```

### 3. TelemetryProvider class

**Module**: `cassandragargoyle.api.telemetry`

**Responsibilities**:

- Initialize `TracerProvider` based on configuration
- Support three modes: **NoOp** (default), **Console** (development), **OTLP** (production)
- Provide `Tracer` instances via `get_tracer(instrumentation_name)`
- Support OTel standard environment variable overrides (`OTEL_*`)
- Handle graceful shutdown (flush pending spans)

**Public API**:

```python
from enum import Enum
from opentelemetry.trace import Tracer

class ExporterType(Enum):
    NONE = "none"
    CONSOLE = "console"
    OTLP = "otlp"

class TelemetryProvider:
    """Centralized OpenTelemetry TracerProvider for Python Portunix components."""

    @classmethod
    def builder(cls, service_name: str) -> "TelemetryProviderBuilder":
        """Create a builder for configuring the provider."""
        ...

    @classmethod
    def no_op(cls) -> "TelemetryProvider":
        """Return a NoOp provider singleton (zero overhead)."""
        ...

    def get_tracer(self, instrumentation_name: str) -> Tracer:
        """Get a tracer for the given instrumentation scope."""
        ...

    def shutdown(self) -> None:
        """Shutdown and flush pending spans."""
        ...


class TelemetryProviderBuilder:
    def service_name(self, name: str) -> "TelemetryProviderBuilder": ...
    def enabled(self, enabled: bool) -> "TelemetryProviderBuilder": ...
    def exporter(self, exporter_type: ExporterType) -> "TelemetryProviderBuilder": ...
    def endpoint(self, endpoint: str) -> "TelemetryProviderBuilder": ...
    def protocol(self, protocol: str) -> "TelemetryProviderBuilder": ...
    def sample_rate(self, rate: float) -> "TelemetryProviderBuilder": ...
    def build(self) -> TelemetryProvider: ...
```

### 4. Standard logging utilities

**Module**: `cassandragargoyle.api.log`

Mirror the Java `LogFactory` and `Logging` classes to provide unified logging across Python Portunix components. Uses Python's standard `logging` module.

#### 4.1 LogFactory

```python
import logging

class LogFactory:
    """Factory for creating loggers with consistent naming."""

    @staticmethod
    def get_logger(name: str | None = None) -> logging.Logger:
        """Create a logger for the given module/class name.

        If name is None, uses the caller's module name.
        """
        ...

    @staticmethod
    def log_debug_with_trace(message: str, log_condition: bool = True) -> None:
        """Log message at DEBUG level with stack trace when condition is met
        and cassandragargoyle.debug flag is enabled."""
        ...
```

#### 4.2 Logging

```python
import logging

class Logging:
    """Centralized logging initialization and level management."""

    ROOT_LOG = "cassandragargoyle"

    @classmethod
    def initialize(
        cls,
        log_level: str = "INFO",
        log_on_console: bool = True,
        log_to_file: bool = False,
    ) -> None:
        """Initialize logging system with console and/or file handlers.

        Configures root CassandraGargoyle logger with custom formatter.
        """
        ...

    @classmethod
    def set_level(cls, level: int) -> None:
        """Set log level for all CassandraGargoyle loggers."""
        ...

    @classmethod
    def get_level(cls) -> int:
        """Get current log level."""
        ...

    @classmethod
    def set_log_level(cls, log_level: str) -> None:
        """Set log level using aliases.

        Supported aliases:
            TRACE/T/4 -> DEBUG (Python has no TRACE, maps to DEBUG)
            DEBUG/D/3 -> DEBUG
            INFO/I/2  -> INFO
            WARNING/W/1 -> WARNING
            ERROR/E/0 -> ERROR
        """
        ...
```

#### 4.3 TelemetryProvider logging integration

Add `enable_trace_logging()` to `TelemetryProvider`:

```python
class TelemetryProvider:
    # ... existing methods ...

    @staticmethod
    def enable_trace_logging() -> None:
        """Enable INFO-level logging for OpenTelemetry span output.

        Call when trace output should be visible in console/logs
        (e.g., when --trace CLI flag is active).
        Sets the 'opentelemetry' logger to INFO level.
        """
        ...
```

#### 4.4 Package structure (updated)

```text
python/
├── pyproject.toml
└── src/
    └── cassandragargoyle/
        └── api/
            ├── log/
            │   ├── __init__.py
            │   ├── factory.py          # LogFactory
            │   └── logging_config.py   # Logging
            └── telemetry/
                ├── __init__.py
                ├── provider.py
                └── exporter_type.py
```

#### 4.5 Log level mapping (Java → Python)

| Java Level | Python Level | Alias |
|------------|-------------|-------|
| ALL        | DEBUG       | TRACE/T/4 |
| FINE       | DEBUG       | DEBUG/D/3 |
| CONFIG     | INFO        | — |
| INFO       | INFO        | INFO/I/2 |
| WARNING    | WARNING     | WARNING/W/1 |
| SEVERE     | ERROR       | ERROR/E/0 |

#### 4.6 Log format

Default format matching Java `CustomFormatter` output:

```
dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message
```

### 5. Design principles

- **1:1 parity with Java TelemetryProvider** (Issue #21) - same builder pattern, same ExporterType enum, same behavior
- **Zero overhead when disabled**: `no_op()` returns OTel NoOp tracer, no SDK initialization
- **Thread-safe**: singleton-safe TracerProvider
- **Standard OTel env vars**: `OTEL_EXPORTER_OTLP_ENDPOINT`, `OTEL_SERVICE_NAME`, `OTEL_TRACES_SAMPLER`, `OTEL_TRACES_EXPORTER` override programmatic config
- **Pythonic API**: snake_case naming, type hints, dataclasses where appropriate
- **No framework dependency**: pure Python + opentelemetry-sdk, usable from any project

## Acceptance Criteria

1. `TelemetryProvider.no_op()` returns a functional provider with zero overhead
2. Console exporter prints spans to stdout in readable format
3. OTLP exporter connects to configurable endpoint (both gRPC and HTTP protocols)
4. OTel environment variables override programmatic configuration
5. `shutdown()` flushes pending spans before application exit
6. Unit tests cover all three modes (NoOp, Console, OTLP)
7. Package is installable via pip (editable mode at minimum)
8. API surface mirrors Java TelemetryProvider (#21) for cross-language consistency
9. `LogFactory.get_logger()` returns a properly named `logging.Logger`
10. `Logging.initialize()` configures console and/or file handlers with custom formatter
11. `Logging.set_log_level()` accepts string aliases (TRACE/DEBUG/INFO/WARNING/ERROR and shorthand T/D/I/W/E and numeric 0-4)
12. `TelemetryProvider.enable_trace_logging()` sets OpenTelemetry logger to INFO level
13. Log output format matches Java `CustomFormatter` pattern (`dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger]: message`)

## Test Plan

- [ ] Unit test: `no_op()` creates valid provider, `get_tracer()` returns NoOp tracer
- [ ] Unit test: Console exporter mode produces output
- [ ] Unit test: Builder validation (missing service name, invalid sample rate)
- [ ] Unit test: environment variable override behavior
- [ ] Unit test: shutdown flushes spans
- [ ] Unit test: `LogFactory.get_logger()` returns logger with correct name
- [ ] Unit test: `LogFactory.get_logger(None)` uses caller's module name
- [ ] Unit test: `log_debug_with_trace()` logs with stack trace when condition met
- [ ] Unit test: `Logging.initialize()` configures console handler
- [ ] Unit test: `Logging.initialize()` configures file handler
- [ ] Unit test: `Logging.set_level()` / `get_level()` round-trip
- [ ] Unit test: `Logging.set_log_level()` alias mapping (all variants)
- [ ] Unit test: `enable_trace_logging()` sets OpenTelemetry logger level
- [ ] Unit test: Custom formatter output matches expected pattern

## References

- [Issue #21: Java TelemetryProvider](021-opentelemetry-telemetry-provider.md) - reference implementation
- [OpenTelemetry Python SDK](https://opentelemetry.io/docs/languages/python/)
- [OpenTelemetry Python API Reference](https://opentelemetry-python.readthedocs.io/)
