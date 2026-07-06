# CassandraGargoyle API

Multi-language shared library (Java, Python, Go, TypeScript) providing entities, utilities, and base abstractions for CassandraGargoyle projects.

## Documentation

See [docs/contributing/](docs/contributing/) for development guidelines.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for the release history.

## Project Information

- **Version:** 1.0.0.9-SNAPSHOT
- **Java:** 21 (Maven, NetBeans Platform)
- **Python:** ≥ 3.11 (uv-managed)
- **Go:** ≥ 1.22
- **Repository**: <https://github.com/cassandragargoyle/api>

## Project Structure

```text
Api/
├── java/          # Core API module (Java, Maven)
├── python/        # Shared API modules (Python, uv)
├── go/            # Shared API modules (Go modules)
├── typescript/    # Shared API modules (TypeScript / React, npm) — requires Node 18+
└── contract/      # Language-independent task contracts (JSON Schema)
```

Each language module is independently buildable and publishable. The
top-level Makefile orchestrates build, test, and lint across all four.

## Core Features

### Entity Management

- Base entity abstractions and implementations
- Version management
- Platform-specific entity handling
- Diagram, Node, and Edge entities for graph-like structures
- Data container entities for data management

### Software Management

- Software entity definitions
- Code language support
- Operating system type detection
- Software categorization and features
- Platform compatibility handling

### Utilities

- **Date Utilities:** Date manipulation and formatting
- **String Utilities:** String processing and manipulation
- **System Utilities:** System-level operations
- **Preferences Utilities:** Application preferences management
- **Base64 Encoding:** Data encoding utilities
- **OS Detection:** Operating system detection and identification

### Fuzzy String Matching

- **Levenshtein distance** with derived normalized similarity in `[0.0, 1.0]`
- Operates on Unicode code points / runes (correct for accented characters,
  CJK, emoji)
- Available in all three language modules (`org.cassandragargoyle.api.fuzzy`,
  `cassandragargoyle.api.fuzzy`, `github.com/cassandragargoyle/api/go/fuzzy`)

### Telemetry (OpenTelemetry)

- **TelemetryProvider** utility for centralized tracing configuration
- Three operating modes: NoOp (default, zero overhead), Console
  (development), OTLP (production)
- Builder pattern with environment-variable overrides
  (`OTEL_EXPORTER_OTLP_ENDPOINT`, `OTEL_SERVICE_NAME`)
- W3C TraceContext propagation; Java + Python parity

### Logging

- Custom logging framework with a consistent format across Java, Python and
  TypeScript (`org.cassandragargoyle.api.log`, `cassandragargoyle.api.log`,
  `@cassandragargoyle/api/log`) — same root logger concept, output format
  (`dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message`) and terse level
  aliases
- Log factory pattern (`LogFactory`)
- The TypeScript module is isomorphic: a console transport for the browser /
  React webview and an optional Node-only rolling file transport

### CLI Support

- Command-line interface implementation
- Apache Commons CLI integration

## Dependencies

### Java

- **NetBeans Platform Modules** — `org-openide-util`, `org-openide-util-ui`,
  `org-openide-util-lookup`, `org-openide-filesystems`, `org-openide-modules`
- **Spring Framework** (5.3.39) — DI and component management
- **Apache Commons** — `commons-cli` (1.11.0), `commons-lang3` (3.20.0),
  `commons-text` (1.15.0)
- **OpenTelemetry** (1.40.0) — distributed tracing
- **JUnit Jupiter** (6.0.3) — testing

### Python

- **OpenTelemetry** (≥ 1.25) — API + SDK + OTLP gRPC/HTTP exporters
- **pytest** (≥ 7.0), **ruff** (≥ 0.15) — testing and linting (dev extras)

### Go

- Standard library only (no external dependencies for the current `fuzzy`
  package)

## Building the Project

```bash
make build            # all artifacts (Java JAR + Python wheel/sdist + TypeScript dist)
make build-java       # JAR only (skip tests)
make build-python     # wheel + sdist only
make build-typescript # TypeScript ESM + CJS + .d.ts only
```

Or directly per language:

```bash
mvn clean install -f java/pom.xml      # Java to ~/.m2
uv --project python build              # Python wheel/sdist to python/dist/
cd go && go build ./...                # Go (stdlib only, no install step)
npm --prefix typescript run build      # TypeScript to typescript/dist/
```

## Running Tests

```bash
make test            # all language modules (Java + Python + Go + TypeScript)
make test-java       # Java only (mvn test)
make test-python     # Python only (pytest)
make test-go         # Go only (go test ./...)
make test-typescript # TypeScript only (Vitest)
```

Run `make help` to see all available targets.

## Development

This project uses:

- **Java**: Maven for build management, Spring for DI, NetBeans Platform for
  application framework
- **Python**: uv for venv and dependency management, pytest for testing,
  ruff for linting
- **Go**: Go modules (`go.mod`) for build and dependency management,
  `go test ./...` for testing, `go vet` for static analysis, `gofmt` for
  formatting; the module lives in `go/` with import path
  `github.com/cassandragargoyle/api/go` (release tags use the `go/` prefix
  per the [submodule tagging rule](https://go.dev/ref/mod#vcs-version),
  e.g. `go/v1.0.0.8`)
- **TypeScript**: npm for dependency management, `tsup` for the dual ESM/CJS
  build with emitted `.d.ts`, Vitest for testing, ESLint + Prettier for
  linting; the package lives in `typescript/` as `@cassandragargoyle/api`
  (subpath export `@cassandragargoyle/api/log`) and requires Node 18+

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

This software is developed and maintained by the CassandraGargoyle Community.

## Contributing

Contributions are welcome. Please see the [contributing guidelines](docs/contributing/) before submitting changes.
