# Issue #12: Add TypeScript (`typescript/`) module with `log` package

**Type**: Feature
**Priority**: Medium
**Status**: ✅ Implemented
**Created**: 2026-07-05
**Closed**: 2026-07-06
**Labels**: feature, typescript, react, log, logging, multi-language
**Repository**: Api
**GitHub**: #12

## Summary

Introduce a fourth language module — **TypeScript** — as a new top-level
`typescript/` directory, mirroring the existing `java/`, `python/`, and `go/`
modules. The first (and, for this issue, **only**) package to ship is
**`log`**, providing the same logging surface already offered by the Java
`org.cassandragargoyle.api.log` package and its Python parity module
`cassandragargoyle.api.log`.

The module is published as the npm package `@cassandragargoyle/api`, consumable
by TypeScript / React projects. Its first consumer is
[**portunix-vscode**](https://github.com/CassandraGargoyle/portunix-vscode) —
the shared Pilot UI React component suite (`src/pilot/`) and the VS Code
extension (`src/vscode-extension/`), both bundled with esbuild.

## Motivation

The Api repo already gives Java and Python components an identical logging
surface: same root logger concept, same output format, same terse level
aliases, so log output ingests through the same tooling regardless of runtime
(see the [Java log README](../../../java/src/main/java/org/cassandragargoyle/api/log/README.md)).
The TypeScript/React frontends in portunix-vscode currently have no shared
logging convention — each panel reaches for `console.log` with ad-hoc
prefixes. A `@cassandragargoyle/api/log` package brings the same format and
level semantics to the TypeScript side, so a VS Code extension, an Electron
window, and a webview panel all emit the project-standard log line.

This establishes the `typescript/` module so future shared code (utilities,
contract types, fuzzy matching) can be added alongside `log` without another
restructuring — matching the precedent set by
[#23](done/023-restructure-multi-language-layout-mvp.md) for `go/` and
[#010](done/010-fuzzy-package-levenshtein.md) for adding a package across
languages.

## Scope

### In scope (this issue)

- New top-level `typescript/` module (npm package `@cassandragargoyle/api`)
- A `log` package exposing `LogFactory` and `Logging`, parity with Java/Python
- **Isomorphic** design: works in the browser / React webview (console
  transport, no filesystem) **and** in Node / the VS Code extension host
  (console + optional file transport)
- Terse level aliases identical to Java/Python (`TRACE/DEBUG/INFO/WARNING/ERROR`
  plus single-letter and numeric forms)
- Project-standard output format (byte-for-byte identical to Java/Python)
- A marker for suppressing uninteresting throwables (the TS analogue of `LogSkipException`)
- Unit tests (parity test vectors shared with Java/Python — see below)
- Public API documentation (TSDoc) + a `log/README.md` mirroring the Java one
- Build tooling: TypeScript compile to **both ESM and CJS**, emitted `.d.ts`
  type declarations, esbuild-friendly (no side-effectful top-level I/O)
- Wire into `Makefile` (`build-typescript`, `test-typescript`, `lint-typescript`,
  `clean-typescript`) and into `test` / `build` aggregate targets

### Out of scope (follow-up issues)

- Any package other than `log` (util, fuzzy, telemetry, contract types, entities)
- Publishing to a public npm registry / GitHub Packages npm feed
  (build + local `npm pack` is enough for v1; portunix-vscode can consume via
  file/link dependency until a registry is decided)
- Browser log shipping / remote sink (OTLP-over-HTTP log export)
- React-specific helpers (a `useLogger` hook, an error-boundary logger)
- NetBeans-style annotations (`@IgnoreLoggerForUI`, `ForcedLogMessages`,
  `ShownLogMessages`) — those are IDE-platform specific and have no TS analogue

## Proposed API

The TypeScript surface follows the same two-type shape as Java/Python
(`LogFactory` + `Logging`), adapted to TS idioms.

Package: `@cassandragargoyle/api` (subpath export `@cassandragargoyle/api/log`)
Location: `typescript/src/log/`

```ts
// typescript/src/log/index.ts (re-exports)
export { LogFactory } from "./factory";
export { Logging, type LogLevel, type LogTransport } from "./logging";
export { isLogSkip, markLogSkip } from "./logSkip";
```

### `LogFactory`

```ts
export class LogFactory {
  /**
   * Allocate a logger bound to the given name (typically the module or
   * class name). Loggers with the same name share configuration.
   */
  static getLogger(name: string): Logger;
}

export interface Logger {
  trace(message: string, ...args: unknown[]): void;
  debug(message: string, ...args: unknown[]): void;
  info(message: string, ...args: unknown[]): void;
  warn(message: string, ...args: unknown[]): void;
  error(message: string, error?: unknown, ...args: unknown[]): void;
}
```

`message` uses the same `{0}`, `{1}` positional-placeholder convention as the
Java `Logger.log(level, msg, args)` calls, so log strings can be copied
between runtimes unchanged.

### `Logging`

```ts
export type LogLevel =
  | "TRACE" | "DEBUG" | "INFO" | "WARNING" | "ERROR"
  | "T" | "D" | "I" | "W" | "E"
  | "4" | "3" | "2" | "1" | "0";

export interface LogTransport {
  write(formatted: string, level: LogLevel): void;
}

export class Logging {
  /** Initialize transports and the active level. In the browser, `file` is ignored. */
  static initialize(level: LogLevel, options?: {
    console?: boolean;   // default true
    file?: boolean;      // default false; Node only, no-op in browser
    filePath?: string;   // default ~/.<app>/var/log/messages.<pid>.log (Node)
    transports?: LogTransport[]; // custom sinks (append to console/file)
  }): void;

  /** Change the active level at runtime; accepts the terse aliases below. */
  static setLogLevel(level: LogLevel): void;
}
```

### `LogSkip` marker

The Java `LogSkipException` marker interface has no direct TS analogue
(no marker interfaces at runtime). Provide a tag helper instead:

```ts
/** Tag an error so the default filter drops its records before any transport. */
export function markLogSkip<E extends object>(error: E): E;
export function isLogSkip(error: unknown): boolean;
```

Tests: `typescript/test/log/*.test.ts`

## Level aliases (must match Java/Python)

| Alias | Level |
| ----- | ----- |
| `TRACE`, `T`, `4` | trace (JUL `ALL`) |
| `DEBUG`, `D`, `3` | debug (JUL `FINE`) |
| `INFO`, `I`, `2` | info (JUL `INFO`) |
| `WARNING`, `W`, `1` | warn (JUL `WARNING`) |
| `ERROR`, `E`, `0` | error (JUL `SEVERE`) |

## Output format (must match Java/Python)

```text
dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message
```

Multi-line messages and stack traces are appended verbatim. A `[catch]`
prefix marks the frame where an error was caught vs thrown, matching the Java
formatter.

## Isomorphic (browser + Node) design notes

- The package must not perform filesystem or `process`-dependent work at
  import time — esbuild bundles `src/pilot/` for a webview where `fs`/`process`
  are absent. Guard Node-only code behind a runtime capability check
  (`typeof process !== "undefined" && process.versions?.node`), and put the
  file transport in a separately-imported module so tree-shaking drops it from
  browser bundles.
- **Console transport** (browser + Node): formats the standard line and routes
  to `console.debug/info/warn/error` by level.
- **File transport** (Node only): rolling per-PID file
  `~/.<app>/var/log/messages.<pid>.log` with two rotated copies (`.1`, `.2`),
  pruning files older than 10 days at startup — mirroring the Java handler.
  In the browser this is a documented no-op.
- Ship dual output: `exports` map with `import` (ESM) and `require` (CJS)
  conditions plus `types`, so both the esbuild extension bundle and any
  ts-node tooling resolve correctly.

## Tasks

### Phase 1: Module scaffold

- [ ] Create `typescript/` with `package.json` (`name: "@cassandragargoyle/api"`,
      `version` aligned with the repo `1.0.0.9`), `tsconfig.json`, `.gitignore`
- [ ] Choose + configure the test runner (**Vitest** recommended — native ESM,
      fast, fits React tooling) and the linter/formatter
      (**ESLint + Prettier**, or **Biome**)
- [ ] Configure dual ESM/CJS build with emitted `.d.ts` and an `exports` map
      exposing `.` and `./log`

### Phase 2: `log` package

- [ ] Implement the shared formatter (project output format) + level-alias parser
- [ ] Implement `LogFactory.getLogger`
- [ ] Implement `Logging.initialize` / `setLogLevel` with the console transport
- [ ] Implement the Node-only file transport (rolling per-PID, isolated module)
- [ ] Implement `markLogSkip` / `isLogSkip` + the default filter
- [ ] Add `typescript/src/log/README.md` mirroring the Java log README

### Phase 3: Tests

- [ ] Formatter parity tests using the shared vectors below
- [ ] Level-alias parity tests (every alias resolves to the expected level)
- [ ] Filter test: a `markLogSkip`-tagged error is dropped before transports
- [ ] Browser-safe import test: importing the package entry does no I/O and
      does not touch `process`/`fs`

### Phase 4: Cross-cutting

- [ ] Add `build-typescript`, `test-typescript`, `lint-typescript`,
      `clean-typescript` to the `Makefile`; add them to `build` / `test`
- [ ] Update root `README.md`: add `typescript/` to Project Structure, note the
      Node/TS version, and add the module to the Logging feature section
- [ ] Add a short "first consumer" note pointing at portunix-vscode `src/pilot/`
- [ ] Parity check: the TS formatter output matches Java and Python for every
      shared vector

## Shared parity test vectors

Given a fixed timestamp `05/07/2026 14:30:00.123`, logger name `com.example.Svc`:

| level input | message | args | expected line |
| --- | --- | --- | --- |
| `INFO` | `Starting {0}` | `["job-7"]` | `05/07/2026 14:30:00.123 INFO [com.example.Svc]: Starting job-7` |
| `ERROR` | `Failed {0}: {1}` | `["job-7","timeout"]` | `05/07/2026 14:30:00.123 ERROR [com.example.Svc]: Failed job-7: timeout` |
| `WARNING` | `Low disk` | `[]` | `05/07/2026 14:30:00.123 WARNING [com.example.Svc]: Low disk` |
| `DEBUG` | `x={0}` | `[42]` | `05/07/2026 14:30:00.123 DEBUG [com.example.Svc]: x=42` |

(The Java/Python formatters must be run against the same inputs to lock the
exact byte output; adjust the table if the existing runtimes render `LEVEL`
tokens differently — the TS output is the one that must conform, not the
reverse.)

## Acceptance Criteria

1. A `typescript/` top-level module exists and builds to ESM + CJS + `.d.ts`.
2. `@cassandragargoyle/api/log` exports `LogFactory`, `Logging`, `markLogSkip`/`isLogSkip`.
3. Level aliases resolve identically to the Java/Python modules.
4. Formatter output matches the shared parity vectors byte-for-byte.
5. Importing the package entry in a browser-like environment performs no
   filesystem or `process` access (verified by test).
6. The Node file transport writes the rolling per-PID file; in the browser it is a documented no-op.
7. `make test-typescript` and `make build-typescript` are green; lint clean.
8. portunix-vscode can consume the package (via `npm pack` / file dependency)
   and emit a project-standard log line from a `src/pilot/` React component.

## Open Questions

1. **npm distribution.** Local `npm pack` + file dependency for v1, or set up a
   GitHub Packages npm feed now? (Leaning: local pack for v1; registry is a
   follow-up, mirroring how Java started before GitHub Packages in
   [#19](done/019-github-packages-publishing.md).)
2. **Test runner / linter.** Vitest + ESLint/Prettier vs Biome — pick to match
   whatever portunix-vscode already uses so contributors share one toolchain.
3. **Version alignment.** Reuse the repo's `1.0.0.9` scheme, or start the npm
   package at its own `0.x` until the surface stabilizes?

## References

- [Java log package README](../../../java/src/main/java/org/cassandragargoyle/api/log/README.md)
  — the surface this module mirrors
- [Issue #23: Restructure project layout for multi-language platform](done/023-restructure-multi-language-layout-mvp.md)
  — precedent for adding a top-level language module (`go/`)
- [Issue #010: Add `fuzzy` package (Java, Go, Python)](done/010-fuzzy-package-levenshtein.md)
  — precedent for a cross-language package with shared test vectors
- [Issue #22: Python TelemetryProvider shared module](done/022-python-telemetry-provider.md)
  — precedent for a Java↔Python parity package
- [portunix-vscode README](https://github.com/CassandraGargoyle/portunix-vscode)
  — first consumer (Pilot UI `src/pilot/`, VS Code extension `src/vscode-extension/`)
