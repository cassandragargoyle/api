<!-- markdownlint-disable MD024 -- repeated "Added"/"Changed"/... headings are intrinsic to the per-version changelog format -->

# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
This project uses a four-part version scheme (`MAJOR.MINOR.PATCH.BUILD`) shared
across the Java, Python, Go and TypeScript modules. The Go module is tagged with
a `go/` prefix per the Go submodule tagging rule (e.g. `go/v1.0.0.8`).

## [Unreleased]

### Added

- **TypeScript module** (`typescript/`, npm package `@cassandragargoyle/api`) —
  the fourth top-level language module, mirroring `java/`, `python/` and `go/`.
  Dual ESM + CJS build with emitted `.d.ts`, Vitest tests, ESLint + Prettier
  (#012).
- **`log` package** (`@cassandragargoyle/api/log`) with Java/Python parity:
  `LogFactory`, `Logging`, `markLogSkip` / `isLogSkip`, shared formatter and the
  full set of terse level aliases. Isomorphic design — a console transport for
  the browser / React webview and a lazily-loaded, Node-only rolling per-PID file
  transport — so browser bundles tree-shake the filesystem code away (#012).
- `build-typescript`, `test-typescript`, `lint-typescript` and
  `clean-typescript` Makefile targets, wired into the aggregate `build`, `test`
  and `clean` targets.
- Internal issue #013 (User preferences storage — TypeScript first) and
  ADR-004 (user preferences storage).
- `deploy-local` and `finish-branch` skills.
- `plugin.python_version` field in the plugin manifest schema (for bytecode
  wheels).

## [1.0.0.9] - 2026-05-02

### Changed

- Updated Java library dependency versions.
- Switched Maven compiler configuration to `maven.compiler.release`.

### Added

- `CONTRIBUTING.md` and `GOVERNANCE.md` tailored to the Api project.

## [1.0.0.8] - 2026-05-01

### Added

- **`fuzzy` package** with Levenshtein distance and normalized similarity across
  all three language modules — Java, Python and Go (#010).
- OpenTelemetry `TelemetryProvider` utility with NoOp / Console / OTLP modes and
  `enableTraceLogging()` for CLI trace output (#004), plus the Python
  `TelemetryProvider` parity module (#005).
- Multi-language project layout: top-level `java/`, `python/`, `go/` modules and
  the language-independent `contract/` JSON Schemas (#006).
- Python build and publish pipeline (#008); Plugin Platform contract schemas
  (#009).
- Tooling: Checkstyle for Java, ruff for Python, markdownlint for Markdown, and
  the `preflight` security check.

### Changed

- Migrated Python tooling to `uv`.
- Unified `make test` to run every language module.

### Removed

- Standalone persistence module, consolidated into the Maven POM (#007).

## [1.0.0.3] - 2026-03-01

### Added

- Initial public baseline: core entities, utilities and base abstractions.
- GitHub Packages publishing workflow (#002).

### Fixed

- Renamed the `persistance` module to `persistence` (#003).
- Strip `-SNAPSHOT` from `pom.xml` versions before publishing (#002).

[Unreleased]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.9...HEAD
[1.0.0.9]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.8...v1.0.0.9
[1.0.0.8]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.3...v1.0.0.8
[1.0.0.3]: https://github.com/CassandraGargoyle/api/releases/tag/v1.0.0.3
