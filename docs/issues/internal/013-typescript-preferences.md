# Issue #13: User preferences storage — TypeScript first implementation

**Type**: Feature
**Priority**: Medium
**Status**: 📋 Open
**Created**: 2026-07-05
**Labels**: feature, typescript, react, preferences, settings, multi-language
**Repository**: Api
**Related**: ADR-004 (User Preferences Storage Interface), Issue #012 (TypeScript `log` module)
**GitHub**: #13

## Summary

Implement the user-preferences abstraction defined in
[ADR-004](../../adr/004-user-preferences-storage.md) as the **first** language
target: **TypeScript**, in the `typescript/` module being established for `log`
in [#012](012-typescript-log-module.md). Ship a `preferences` package under the
`@cassandragargoyle/api` npm package that lets an application persist and restore
user UI customizations (component sizes, panel layout, view flags) across
sessions — without coupling to a running application container, so it stays
fully usable in unit tests.

## Motivation

Users customize their apps — resizing panels and components, rearranging layout.
Those choices must survive an app restart. The first consumer is
[**portunix-vscode**](https://github.com/CassandraGargoyle/portunix-vscode): the
Pilot UI React panels (`src/pilot/`) are exactly where a user drags a splitter or
resizes a component and expects it to stick.

ADR-004 records **why** and **what shape**; this issue is the TypeScript **how**.
TypeScript goes first because it has the immediate consumer and the module is
already being created for logging (#012). Java / Python / Go follow as separate
issues once the surface is proven.

## Scope

### In scope (this issue)

- New `preferences` package in `typescript/` (subpath export
  `@cassandragargoyle/api/preferences`)
- The consumer-facing **`Preferences`** surface (namespaced, typed get/put/remove,
  change listeners) per ADR-004
- The **`PreferencesStore`** backend seam (`load` / `read` / `write` / `flush`)
- Two concrete stores available now:
  - **`MemoryPreferencesStore`** — the default; self-contained, zero bootstrap,
    used in unit tests and as the safe fallback
  - **`LocalPreferencesStore`** (Local mode) — persists to `localStorage` in the
    browser / webview, and to a JSON file (`~/.<app>/var/preferences.json`) under
    Node / the VS Code extension host
- **Sync read / async persist**: load into an in-memory cache on `initialize()`,
  serve reads synchronously, write-through + debounced async flush, flush-on-exit
- Isomorphic packaging identical to #012 (dual ESM/CJS, `.d.ts`, no side-effectful
  top-level I/O; Node-only file code isolated so browser bundles tree-shake it)
- Unit tests proving the **NetBeans flaw is gone** (see Acceptance Criteria)
- TSDoc + a `preferences/README.md`

### Out of scope (follow-up issues)

- **`RemotePreferencesStore`** (Server mode) — interface is reserved per ADR-004,
  no implementation here
- Java / Python / Go implementations (separate parity issues)
- React ergonomics helpers (`usePreference` hook, a layout-persistence component
  wrapper) — can be a thin follow-up once the core lands
- A settings UI / preferences editor
- Migration/versioning of stored preference schemas

## Proposed API

Package: `@cassandragargoyle/api` (subpath export `@cassandragargoyle/api/preferences`)
Location: `typescript/src/preferences/`

```ts
// typescript/src/preferences/index.ts (re-exports)
export { Preferences, type PreferencesNode } from "./preferences";
export { type PreferencesStore } from "./store";
export { MemoryPreferencesStore } from "./memoryStore";
export { LocalPreferencesStore } from "./localStore";
```

### Consumer surface

```ts
export interface PreferencesNode {
  /** Obtain a nested namespace, e.g. node("ui").node("mainWindow"). */
  node(path: string): PreferencesNode;

  getString(key: string, def: string): string;
  getInt(key: string, def: number): number;
  getNumber(key: string, def: number): number;
  getBoolean(key: string, def: boolean): boolean;

  putString(key: string, value: string): void;
  putInt(key: string, value: number): void;
  putNumber(key: string, value: number): void;
  putBoolean(key: string, value: boolean): void;

  remove(key: string): void;

  /** React to external changes (e.g. a future server sync). Returns an unsubscribe. */
  onChange(listener: (key: string, value: unknown) => void): () => void;
}

export class Preferences {
  /**
   * Compose the preferences facade over a store. If `store` is omitted,
   * a MemoryPreferencesStore is used — this is what makes unit tests work
   * with zero application bootstrap (ADR-004).
   */
  static create(store?: PreferencesStore): Preferences;

  /** Load the backing store into the in-memory cache (async). */
  initialize(): Promise<void>;

  /** Root namespace. Reads are synchronous from the cache. */
  root(): PreferencesNode;

  /** Force a flush of pending writes (also runs on process exit / page unload). */
  flush(): Promise<void>;
}
```

### Backend seam

```ts
export interface PreferencesStore {
  load(): Promise<Record<string, unknown>>;      // full snapshot into cache
  read(key: string): unknown;                     // sync, from cache
  write(key: string, value: unknown): void;       // write-through to cache
  flush(snapshot: Record<string, unknown>): Promise<void>; // persist
}
```

`MemoryPreferencesStore` keeps everything in a plain object (no persistence).
`LocalPreferencesStore` persists via `localStorage` (browser) or a JSON file
(Node), selected by the same runtime capability check used by the `log` module.

Tests: `typescript/test/preferences/*.test.ts`

## How this issue proves the NetBeans flaw is fixed

The defect ADR-004 calls out is preferences being coupled to a running
application container, breaking unit tests. This issue must demonstrate the fix:

- A test that calls `Preferences.create()` (no store, no app) and reads a key
  returns the supplied default and **does not** throw, block, or require any
  bootstrap.
- A test that writes then reads back a value works entirely in-memory.
- Each test gets an isolated store — **no shared OS-level state** leaks between
  tests (contrast with `java.util.prefs`, which NetBeans builds on).
- A browser-like environment test confirms importing the package does no I/O and
  does not touch `process`/`fs`.

## Tasks

### Phase 1: Core

- [ ] Create `typescript/src/preferences/` with `Preferences`, `PreferencesNode`
- [ ] Define `PreferencesStore` seam
- [ ] Implement `MemoryPreferencesStore` (default)
- [ ] Implement the sync-read / async-flush cache + namespacing + change listeners

### Phase 2: Local persistence

- [ ] Implement `LocalPreferencesStore`: `localStorage` (browser) + JSON file (Node)
- [ ] Debounced flush + flush-on-exit (`beforeunload` / `process` exit hooks)
- [ ] Isolate Node-only file code so browser bundles tree-shake it

### Phase 3: Tests

- [ ] No-bootstrap unit test (default memory store; read returns default; write/read)
- [ ] Isolation test (fresh store per test; no cross-test leakage)
- [ ] Local store round-trip test (browser mock + Node temp file)
- [ ] Browser-safe import test (no I/O, no `process`/`fs` at import)
- [ ] Namespacing + typed accessors + listener tests

### Phase 4: Packaging & docs

- [ ] Add `./preferences` subpath to the package `exports` map (ESM/CJS/types)
- [ ] `typescript/src/preferences/README.md`
- [ ] Ensure `make test-typescript` / `build-typescript` cover the new package
- [ ] Update root `README.md` Core Features with a "Preferences" section
- [ ] Note portunix-vscode `src/pilot/` as first consumer

## Acceptance Criteria

1. `@cassandragargoyle/api/preferences` exports `Preferences`, `PreferencesNode`,
   `PreferencesStore`, `MemoryPreferencesStore`, `LocalPreferencesStore`.
2. `Preferences.create()` with **no** store works in a plain unit test — reads
   return defaults, writes persist in-memory — with **zero** application bootstrap.
3. Tests are isolated: no shared OS-level state leaks between test cases.
4. `LocalPreferencesStore` round-trips values via `localStorage` (browser) and a
   JSON file (Node); a written value is restored after re-`initialize()`.
5. Reads are synchronous; writes flush asynchronously and on exit/unload.
6. Importing the package entry does no filesystem or `process` access in a
   browser-like environment (verified by test).
7. `RemotePreferencesStore` is **not** implemented but the `PreferencesStore` seam
   accommodates it without consumer changes.
8. portunix-vscode can consume the package and persist/restore a panel size from a
   `src/pilot/` React component.
9. `make test-typescript` and `make build-typescript` green; lint clean.

## Dependencies

- **Blocks on [#012](012-typescript-log-module.md)** — the `typescript/` module,
  its build (dual ESM/CJS + `.d.ts`), test runner, and Makefile wiring are created
  there. This issue adds a second package into that module.

## Open Questions

1. **JSON file location (Node).** `~/.<app>/var/preferences.json` — is `<app>`
   supplied by the consumer at `initialize()`, or derived from `package.json`
   name? (Leaning: consumer-supplied app id, defaulting to a generic path.)
2. **Key encoding for `localStorage`.** One key per preference
   (`cg.pref/ui/mainWindow/width`) vs a single serialized blob under one key.
   (Leaning: single blob, simpler flush and atomic writes.)
3. **Number vs int.** Keep both `getInt`/`getNumber` for Java parity, or collapse
   to `getNumber` in TS and reintroduce `getInt` only in the Java port?

## References

- [ADR-004: User Preferences Storage Interface](../../adr/004-user-preferences-storage.md)
- [Issue #012: TypeScript `log` module](012-typescript-log-module.md)
- [portunix-vscode README](https://github.com/CassandraGargoyle/portunix-vscode)
  — first consumer (Pilot UI `src/pilot/`)
