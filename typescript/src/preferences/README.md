# `@cassandragargoyle/api/preferences`

User preferences storage for CassandraGargoyle TypeScript / React modules. An
application uses it to save and restore what the user customized in the UI
(component sizes, panel layout, view flags) between sessions.

It is the first implementation of
[ADR-004](../../../docs/adr/004-user-preferences-storage.md). Java, Python and Go
follow in separate issues.

The main design rule: preferences depend only on an injected store, never on a
running application container. `Preferences.create()` with no arguments works in
a plain unit test, with no bootstrap and no state shared between tests.

The module is **isomorphic**: it works in the browser / React webview
(`localStorage`) and under Node / the VS Code extension host (JSON file). The
Node-only file code is loaded lazily, so browser bundles tree-shake it away.

## Public surface

| Export                    | Kind      | Purpose                                                            |
| ------------------------- | --------- | ------------------------------------------------------------------ |
| `Preferences`             | class     | Facade over a store: `create`, `initialize`, `root`, `flush`       |
| `PreferencesNode`         | interface | One namespace: typed `get*` / `put*`, `remove`, `node`, `onChange` |
| `PreferencesStore`        | interface | Backend seam: `load` / `read` / `write` / `flush`                  |
| `MemoryPreferencesStore`  | class     | Default store, in memory only, used in unit tests                  |
| `LocalPreferencesStore`   | class     | Local mode: `localStorage` in the browser, JSON file under Node    |
| `LocalPreferencesOptions` | interface | `appId`, `filePath`, `storageKey`, `storage`                       |
| `PreferencesStorage`      | interface | The part of the Web Storage API that the local store needs         |
| `PreferencesOptions`      | interface | `flushDelayMs`                                                     |
| `PreferencesListener`     | type      | `(key, value) => void`, `value` is `undefined` after a remove      |

## Typical usage

### Composing preferences at startup

```ts
import { LocalPreferencesStore, Preferences } from "@cassandragargoyle/api/preferences";

const preferences = Preferences.create(new LocalPreferencesStore({ appId: "portunix" }));
await preferences.initialize(); // loads the stored values into the cache
```

### Reading and writing

```ts
const mainWindow = preferences.root().node("ui").node("mainWindow");

// Reads are synchronous, so they can run in a React render
const width = mainWindow.getInt("sidebarWidth", 240);

// Writes go to the cache at once and are persisted in the background
mainWindow.putInt("sidebarWidth", 320);
mainWindow.remove("sidebarWidth");
```

`node("ui/mainWindow")` is the same node as `node("ui").node("mainWindow")`.

A getter returns the default when the key is missing or when the stored value
has another type. `putInt` throws a `TypeError` for a value that is not an
integer, `putNumber` for a value that is not a finite number. A key must not be
empty and must not contain `/`.

### Reacting to changes

```ts
const unsubscribe = mainWindow.onChange((key, value) => {
  // called for the keys of this node, not for nested nodes
});
unsubscribe();
```

### Unit tests

```ts
const preferences = Preferences.create(); // MemoryPreferencesStore, no bootstrap
preferences.root().putBoolean("visible", true);
```

Each `Preferences.create()` gets its own store, so tests do not share state.
Initial values can be given with
`new MemoryPreferencesStore({ "ui/mainWindow/sidebarWidth": 320 })`.

## Persistence

- `initialize()` loads the store into an in-memory cache. Values written before
  the load finishes are kept.
- A write starts a debounced flush (250 ms by default, see `flushDelayMs`).
- `flush()` persists at once and returns a promise that rejects when the store
  fails. A failed background flush is reported with `console.error`, the values
  stay in the cache and the next flush writes them.
- Pending writes are also flushed on `pagehide` / `beforeunload` in the browser
  and on `beforeExit` / `exit` under Node. The hooks exist only while there are
  unflushed writes.

### Where `LocalPreferencesStore` saves

| Runtime                              | Location                                               |
| ------------------------------------ | ------------------------------------------------------ |
| Node / VS Code extension host        | `~/.<appId>/var/preferences.json` (or `filePath`)      |
| Browser / webview                    | `localStorage` key `cg.pref/<appId>` (or `storageKey`) |
| `storage` option given               | that storage, for example `sessionStorage`             |
| No file system and no `localStorage` | memory only                                            |

The default `appId` is `cassandragargoyle`. All values are stored as one JSON
document, keyed by the full key (`ui/mainWindow/sidebarWidth`). A missing or
damaged document gives no values and a `console.warn`. The file is written
through a temporary file, so a crash does not leave it half-written.

### Limits

- Call `await preferences.flush()` in the shutdown path of the application
  (for example in `deactivate()` of a VS Code extension). The exit hooks are a
  safety net only: a process that is ended by a signal (`SIGTERM`, `SIGKILL`)
  runs no hook, so the writes of the last 250 ms are lost.
- Under Node, call `initialize()` before the first write. A `process.exit()`
  before the store is loaded cannot save the pending writes, because the file
  code is loaded asynchronously.
- One preferences file has one writer. A flush writes the whole document, so
  two instances (or two processes) on the same file overwrite the values of
  each other. Give each of them its own `appId` or `filePath`.

## Not included yet

- `RemotePreferencesStore` (Server mode). It will implement `PreferencesStore`,
  so consumer code does not change.
- React helpers (`usePreference` hook)
- Versioning and migration of stored preferences

## See also

- [ADR-004: User Preferences Storage Interface](../../../docs/adr/004-user-preferences-storage.md)
- Log module with the same packaging: [`@cassandragargoyle/api/log`](../log/README.md)
- First consumer: [portunix-vscode](https://github.com/CassandraGargoyle/portunix-vscode)
  Pilot UI (`src/pilot/`)
