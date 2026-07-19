# `org.cassandragargoyle.api.log`

Logging infrastructure shared by all CassandraGargoyle Java modules. Wraps
`java.util.logging` (JUL) with a project-wide formatter, a non-blocking file
handler, and a small set of helpers and annotations used by the NetBeans
layer.xml integration.

The Python module under
[`python/src/cassandragargoyle/api/log/`](../../../../../../../../python/src/cassandragargoyle/api/log)
mirrors the public surface of this package — same root logger name, same
output format, same level aliases — so log output from Java and Python
components looks identical and ingests through the same tooling.

## Public surface

| Type | Kind | Purpose |
| --- | --- | --- |
| [`LogFactory`](LogFactory.java) | utility class | Allocates `Logger` instances bound to the calling class's resource bundle, plus `args(...)` helper for parameterized log calls |
| [`Logging`](Logging.java) | utility class | Initializes JUL handlers (console + rolling per-PID file), sets levels, supplies the project formatter and a default filter |
| [`LogSkipException`](LogSkipException.java) | marker interface | Tag thrown values with this interface to suppress them from log output |
| `LogFactory.IgnoreLoggerForUI` | annotation (`@Target FIELD`) | Marks logger fields whose names belong in `CassandraGargoyle/Log/IngoredLoggers/` of the NetBeans layer |
| `LogFactory.ForcedLogMessages` | annotation (`@Target TYPE / METHOD / CONSTRUCTOR`) | Lists message ids forced into the user-visible log under `CassandraGargoyle/Log/ForcedMessages/` |
| `LogFactory.ShownLogMessages` | annotation (`@Target TYPE / METHOD / CONSTRUCTOR`) | Lists message ids surfaced from the status bar to the main log under `CassandraGargoyle/Log/ShownMessages/` |

The two inner annotations are source-retention only (`RetentionPolicy.SOURCE`)
and are read by the NetBeans `layer.xml` annotation processor — they do not
appear in the runtime classpath.

## Typical usage

### Allocating a logger

```java
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cassandragargoyle.api.log.LogFactory;

public class MyService
{
    private static final Logger LOG = LogFactory.getLogger(MyService.class);

    public void doSomething(String entityName)
    {
        LOG.log(Level.INFO, "Starting operation for {0}", entityName);
        LOG.log(Level.SEVERE, "Failed: {0}, cause: {1}",
                LogFactory.args(entityName, ex.getMessage()));
    }
}
```

`LogFactory.getLogger(MyService.class)` looks up a `Bundle.properties` in the
same package as `MyService` and binds the logger to it; if the bundle is
absent the logger is allocated without localization. The logger field name
must be `LOG` per the project Java code style.

### Initializing logging at startup

```java
import org.cassandragargoyle.api.log.Logging;

Logging.initialize("INFO", /* console */ true, /* file */ true);
```

After `initialize`, log records flow to:

- **Console** — formatted via `Logging.FORMATTER` (the project `CustomFormatter`)
- **File** — `~/.myapp/var/log/messages.<PID>.log`, with two rotated copies
  (`.1`, `.2`); files older than 10 days are pruned at startup. The handler
  is wrapped in `NonCloseHandler` and writes asynchronously through a
  bounded queue to keep logging off the application's hot path.

### Level aliases

`Logging.setLogLevel(String)` accepts terse aliases in addition to the
standard JUL level names:

| Alias | Resolves to |
| ----- | ----------- |
| `TRACE`, `T`, `4` | `Level.ALL` |
| `DEBUG`, `D`, `3` | `Level.FINE` |
| `INFO`, `I`, `2` | `Level.INFO` |
| `WARNING`, `W`, `1` | `Level.WARNING` |
| `ERROR`, `E`, `0` | `Level.SEVERE` |

The Python module accepts the same aliases (mapped to the closest
`logging` level) — useful when a single CLI flag drives both runtimes.

### Suppressing noisy throwables

If a third-party stack trace is expected and uninteresting, mark its
exception type with the [`LogSkipException`](LogSkipException.java)
marker interface; the `DefaultLoggerFilter` will drop matching records
before they reach any handler.

```java
public class HarmlessBootError extends RuntimeException implements LogSkipException
{
    public HarmlessBootError(String message) { super(message); }
}
```

## Output format

```text
dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message
```

Multi-line messages and stack traces are appended verbatim. The `[catch]`
prefix is inserted at the frame where an exception was caught (vs thrown),
which is helpful when reading deeply nested causes.

## Level conventions

Per the Javadoc on [`LogFactory`](LogFactory.java), the project assigns
JUL levels as follows:

- `CONFIG` — non-localised configuration dumps
- `FINEST` / `FINER` / `FINE` — non-localised debug detail (file only)
- `INFO` — localised, status-bar-by-default user-facing message
- `WARNING` — localised, status-bar-by-default warning
- `SEVERE` — localised, dialog-by-default error

## See also

- Java code style for logging: [`docs/contributing/CODE-STYLE-JAVA.md`](../../../../../../../../docs/contributing/CODE-STYLE-JAVA.md#logging)
- Python parity module: [`python/src/cassandragargoyle/api/log/`](../../../../../../../../python/src/cassandragargoyle/api/log)
