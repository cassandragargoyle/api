# ADR-004: User Preferences Storage Interface

**Status**: Proposed
**Date**: 2026-07-05
**Deciders**: Zdenek
**Related**: Issue #013 (Preferences — TypeScript first implementation), Issue #012 (TypeScript `log` module), ADR-002 (Unified AI Platform Transition)

## Context

Applications built on CassandraGargoyle libraries let users customize their UI —
resizing panels and components, rearranging layout, toggling view options. These
choices must **persist**: when the user reopens the application, it should restore
the same sizes and arrangement they left. Today there is no shared abstraction for
this; each application would reinvent its own storage.

We want the API libraries to provide a **user-preferences interface** — the same
conceptual surface across languages, matching the pattern already established for
`log`, `telemetry`, and `fuzzy`.

### Requirements

1. **Two storage modes:**
   - **Local** — preferences stored on the user's machine (browser storage,
     a local JSON file, or the platform's native store). Available now.
   - **Server** — preferences synchronized to a backend so they follow the user
     across devices. Deferred to the future; the interface must not preclude it.
2. **Layout persistence use case** — store arbitrary keyed values (component
   sizes, panel positions, view flags) and read them back on next launch.
3. **Multi-language parity** — same conceptual API across TypeScript, Java,
   Python, Go (implemented incrementally; see Decision).

### The NetBeans lesson (the flaw we must not repeat)

The inspiration is how the **NetBeans Platform** exposes preferences
(`NbPreferences.forModule(Class)`, backed by the `java.util.prefs` API and the
module system). It works well **inside a running application**, but it has one
fundamental defect:

> **Preferences are coupled to the running application container.** In a unit
> test the full application (NetBeans runtime / module `Lookup`) is **not**
> booted, so the preferences subsystem is unavailable or misconfigured. Code that
> reads a preference during a unit test either fails, blocks, or silently gets
> wrong values — purely because it is being exercised outside the app.

This forces test authors to either boot the whole platform (slow, brittle) or
wrap every preference access in test-only guards. **Any preferences abstraction we
ship must be usable in a plain unit test with zero application bootstrap.**

## Decision

**Introduce a user-preferences abstraction in the API libraries, built around a
storage-backend that is injected — never a container-coupled global.**

### 1. Two-layer design

- **`Preferences`** — the consumer-facing surface. A namespaced key/value store
  with typed accessors and defaults:
  - `node(path)` — obtain a sub-namespace (e.g. `"ui/mainWindow"`), avoiding key
    collisions between features
  - typed get-with-default: `getString/getInt/getBoolean/getNumber(key, default)`
  - `put*(key, value)` and `remove(key)`
  - change listeners so UI can react to external updates (e.g. a server sync)
- **`PreferencesStore`** — the pluggable backend interface (`load`, `read`,
  `write`, `flush`). Consumers depend on `Preferences`; the store is chosen at
  application composition time.

### 2. Backends (the two modes + the test default)

| Store | Mode | Availability | Purpose |
| ----- | ---- | ------------ | ------- |
| `MemoryPreferencesStore` | — | now | **Default.** Self-contained, in-process, no bootstrap. Used in unit tests and as a safe fallback. |
| `LocalPreferencesStore` | Local | now | Browser storage / local JSON file / native store, per runtime. |
| `RemotePreferencesStore` | Server | future | Syncs to a backend service; deferred, interface reserved. |

### 3. How the NetBeans flaw is solved

- **No container coupling.** `Preferences` depends only on an injected
  `PreferencesStore`, not on any application runtime, module system, or global
  service lookup.
- **Safe default is in-memory.** If no store is configured (exactly the unit-test
  situation), the API resolves to `MemoryPreferencesStore` automatically. Reads
  return the caller-supplied default; writes stay in memory. **A unit test that
  touches preferences neither fails nor needs the app booted** — it gets a clean,
  isolated, in-memory store.
- **Explicit composition in the real app.** At startup the application injects
  `LocalPreferencesStore` (or, later, `RemotePreferencesStore`). Nothing about the
  API forces a global singleton, so tests can inject their own store or accept the
  in-memory default.
- **Deterministic tests.** Each test can create a fresh `Preferences` over a fresh
  `MemoryPreferencesStore`; no shared OS-level state (unlike `java.util.prefs`,
  which writes to a shared registry/backing store and leaks state between tests).

### 4. Sync read, async persist

Server mode implies I/O that cannot be synchronous, yet UI layout restore wants a
value **at mount time**. Resolve this by loading the backing store into an
in-memory cache during `initialize()` (async), then serving **reads synchronously
from the cache** and performing **writes asynchronously** (write-through + debounced
flush). This one shape fits local and server modes and keeps the render path
synchronous.

### 5. Incremental language rollout

The **first implementation targets TypeScript**, riding on the `typescript/` module
being established for `log` (Issue #012). It is the module with an immediate
consumer (portunix-vscode UI, which is exactly where users resize panels). Java,
Python, and Go follow as separate issues once the surface is proven. See Issue #013.

## Consequences

### Positive

1. **Testable by construction** — the defect that plagued NetBeans preferences is
   designed out: no app bootstrap needed, in-memory default, injectable store.
2. **Future-proof for server sync** — the async-persist / sync-read shape and the
   `PreferencesStore` seam let `RemotePreferencesStore` drop in without touching
   consumers.
3. **Consistent cross-language surface** — same concept as `log`/`telemetry`,
   ingested and reasoned about uniformly.
4. **Layout persistence solved once** — every app gets panel/component size
   persistence from a shared, tested library.
5. **No global mutable singleton** — composition-time injection avoids the hidden
   coupling that makes container-bound preferences hard to test and reason about.

### Negative

1. **Explicit wiring** — applications must inject a concrete store at startup
   rather than calling a magic global. (Mitigated by a sensible in-memory default
   and a one-line setup helper.)
2. **Cache/flush complexity** — the sync-read/async-flush cache adds moving parts
   (debounce, flush-on-exit) versus a naive synchronous file write.

### Risks

| Risk | Probability | Impact | Mitigation |
| ---- | ----------- | ------ | ---------- |
| Async flush loses last writes on abrupt exit | Medium | Medium | Flush-on-exit hook; `flush()` awaited on known shutdown paths; write-through cache means reads are never stale |
| Local storage quota exceeded (browser) | Low | Low | Namespaced keys, size guard, drop-oldest or surface an error via listener |
| Server API shape constrains future sync | Low | Medium | Reserve `RemotePreferencesStore` behind the same interface; defer concrete contract to a dedicated ADR/issue |
| Consumers rely on the in-memory default in production (forgot to inject) | Low | Medium | Startup helper + a dev-mode warning when running on the memory store outside tests |

### Rollback Strategy

1. The abstraction ships as a new, additively-exported package — if rejected,
   `git revert` the introduction commit; no existing API changes.
2. Consumers depend on the `Preferences` interface, so a store implementation can
   be swapped or removed without touching call sites.
