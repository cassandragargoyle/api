# `@cassandragargoyle/api/log`

Logging infrastructure shared by CassandraGargoyle TypeScript / React modules.
It mirrors the public surface of the Java
[`org.cassandragargoyle.api.log`](../../../java/src/main/java/org/cassandragargoyle/api/log/README.md)
package and its Python parity module
[`cassandragargoyle.api.log`](../../../python/src/cassandragargoyle/api/log) —
same root logger concept, same output format, same terse level aliases — so log
output from Java, Python and TypeScript components looks identical and ingests
through the same tooling.

The module is **isomorphic**: the console transport works in the browser / React
webview (no filesystem), and a Node-only file transport is loaded lazily when
requested, so browser bundles tree-shake the filesystem code away.

## Public surface

| Export                      | Kind      | Purpose                                                                             |
| --------------------------- | --------- | ----------------------------------------------------------------------------------- |
| `LogFactory`                | class     | Allocates `Logger` instances bound to a name                                        |
| `Logger`                    | interface | `trace` / `debug` / `info` / `warn` / `error` methods with `{0}` placeholders       |
| `Logging`                   | class     | Initializes transports (console + optional Node file), sets levels                  |
| `LogTransport`              | interface | A sink for formatted log lines; custom sinks may be supplied                        |
| `markLogSkip` / `isLogSkip` | functions | Tag / detect errors to suppress from log output (TS analogue of `LogSkipException`) |

## Installation

For v1 the package is consumed via a local pack / file dependency (no registry):

```bash
cd typescript
npm install
npm run build       # emits dist/ (ESM + CJS + .d.ts)
npm pack            # produces cassandragargoyle-api-<version>.tgz
```

Then in the consumer (e.g. portunix-vscode `src/pilot/`):

```bash
npm install /path/to/cassandragargoyle-api-<version>.tgz
```

## Typical usage

### Allocating a logger

```ts
import { LogFactory } from "@cassandragargoyle/api/log";

const log = LogFactory.getLogger("com.example.Svc");

log.info("Starting {0}", "job-7");
log.error("Failed {0}: {1}", err, "job-7", "timeout");
```

`message` uses the same `{0}`, `{1}` positional-placeholder convention as the
Java `Logger.log(level, msg, args)` calls, so log strings can be copied between
runtimes unchanged.

### Initializing logging at startup

```ts
import { Logging } from "@cassandragargoyle/api/log";

// Browser / webview: console only (file is a documented no-op).
Logging.initialize("INFO");

// Node / VS Code extension host: console + rolling per-PID file.
Logging.initialize("INFO", { console: true, file: true });
```

After `initialize`, log records flow to:

- **Console** (browser + Node) — routed to `console.debug/info/warn/error` by level.
- **File** (Node only) — `~/.cassandragargoyle/var/log/messages.<PID>.log`, with
  two rotated copies (`.1`, `.2`); files older than 10 days are pruned at startup.
  In the browser this is a documented no-op. The file transport is loaded
  asynchronously; `await Logging.ready()` resolves once it is attached.

### Level aliases

`Logging.setLogLevel(level)` accepts terse aliases identical to Java/Python:

| Alias               | Resolves to |
| ------------------- | ----------- |
| `TRACE`, `T`, `4`   | trace       |
| `DEBUG`, `D`, `3`   | debug       |
| `INFO`, `I`, `2`    | info        |
| `WARNING`, `W`, `1` | warn        |
| `ERROR`, `E`, `0`   | error       |

### Suppressing noisy throwables

If a third-party error is expected and uninteresting, tag it with `markLogSkip`;
the default filter drops matching records before they reach any transport — the
TS analogue of the Java `LogSkipException` marker interface.

```ts
import { markLogSkip } from "@cassandragargoyle/api/log";

log.error("harmless boot warning", markLogSkip(err));
```

## Output format

```text
dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message
```

Multi-line messages and stack traces are appended verbatim. Note the `warn()`
method emits the `WARNING` level token to match Java/Python byte-for-byte.

## See also

- Java log package: [`org.cassandragargoyle.api.log`](../../../java/src/main/java/org/cassandragargoyle/api/log/README.md)
- Python parity module: [`cassandragargoyle.api.log`](../../../python/src/cassandragargoyle/api/log)
- First consumer: [portunix-vscode](https://github.com/CassandraGargoyle/portunix-vscode)
  Pilot UI (`src/pilot/`) and VS Code extension (`src/vscode-extension/`)
