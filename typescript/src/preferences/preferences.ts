/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { isNode } from "../log/logging";
import { MemoryPreferencesStore } from "./memoryStore";
import type { PreferencesStore } from "./store";

const DEFAULT_FLUSH_DELAY_MS = 250;
const SEPARATOR = "/";

/** Listener for a changed key of one node; `value` is `undefined` after a remove. */
export type PreferencesListener = (key: string, value: unknown) => void;

/** One namespace of preferences with typed accessors. */
export interface PreferencesNode {
  /** Obtain a nested namespace, e.g. `node("ui").node("mainWindow")` or `node("ui/mainWindow")`. */
  node(path: string): PreferencesNode;

  /** Return the stored string, or `def` when the key is missing or not a string. */
  getString(key: string, def: string): string;
  /** Return the stored integer, or `def` when the key is missing or not an integer. */
  getInt(key: string, def: number): number;
  /** Return the stored number, or `def` when the key is missing or not a finite number. */
  getNumber(key: string, def: number): number;
  /** Return the stored boolean, or `def` when the key is missing or not a boolean. */
  getBoolean(key: string, def: boolean): boolean;

  putString(key: string, value: string): void;
  /** @throws TypeError when `value` is not an integer */
  putInt(key: string, value: number): void;
  /** @throws TypeError when `value` is not a finite number */
  putNumber(key: string, value: number): void;
  putBoolean(key: string, value: boolean): void;

  remove(key: string): void;

  /**
   * React to changes of the keys of this node (not of nested nodes).
   *
   * @returns a function that removes the listener
   */
  onChange(listener: PreferencesListener): () => void;
}

/** Options for {@link Preferences.create}. */
export interface PreferencesOptions {
  /** Delay of the debounced flush after a write, in milliseconds (default 250). */
  flushDelayMs?: number;
}

// What a node needs from its Preferences instance
interface NodeHost {
  read(fullKey: string): unknown;
  write(path: string, key: string, value: unknown): void;
  subscribe(path: string, listener: PreferencesListener): () => void;
}

class Node implements PreferencesNode {
  constructor(
    private readonly host: NodeHost,
    private readonly path: string,
  ) {}

  node(path: string): PreferencesNode {
    const segments = path.split(SEPARATOR).filter((segment) => segment.length > 0);
    if (segments.length === 0) {
      return this;
    }
    const prefix = this.path === "" ? [] : [this.path];
    return new Node(this.host, [...prefix, ...segments].join(SEPARATOR));
  }

  getString(key: string, def: string): string {
    const value = this.get(key);
    return typeof value === "string" ? value : def;
  }

  getInt(key: string, def: number): number {
    const value = this.get(key);
    return Number.isInteger(value) ? (value as number) : def;
  }

  getNumber(key: string, def: number): number {
    const value = this.get(key);
    return typeof value === "number" && Number.isFinite(value) ? value : def;
  }

  getBoolean(key: string, def: boolean): boolean {
    const value = this.get(key);
    return typeof value === "boolean" ? value : def;
  }

  putString(key: string, value: string): void {
    this.put(key, value);
  }

  putInt(key: string, value: number): void {
    if (!Number.isInteger(value)) {
      throw new TypeError(`Preference "${key}" needs an integer, got ${value}`);
    }
    this.put(key, value);
  }

  putNumber(key: string, value: number): void {
    if (!Number.isFinite(value)) {
      throw new TypeError(`Preference "${key}" needs a finite number, got ${value}`);
    }
    this.put(key, value);
  }

  putBoolean(key: string, value: boolean): void {
    this.put(key, value);
  }

  remove(key: string): void {
    this.put(key, undefined);
  }

  onChange(listener: PreferencesListener): () => void {
    return this.host.subscribe(this.path, listener);
  }

  private get(key: string): unknown {
    checkKey(key);
    return this.host.read(this.path === "" ? key : this.path + SEPARATOR + key);
  }

  private put(key: string, value: unknown): void {
    checkKey(key);
    this.host.write(this.path, key, value);
  }
}

// A key with the separator would collide with a key of a nested node
function checkKey(key: string): void {
  if (key.length === 0 || key.includes(SEPARATOR)) {
    throw new TypeError(`Invalid preference key: "${key}"`);
  }
}

// Flush functions of the instances with unflushed writes. All instances share
// one set of exit hooks, and the hooks exist only while this set is not empty,
// so an instance never leaves a listener behind
const unflushed = new Set<() => void>();

function flushUnflushed(): void {
  for (const flush of [...unflushed]) {
    flush();
  }
}

function watchExit(flush: () => void): void {
  const first = unflushed.size === 0;
  unflushed.add(flush);
  if (!first) {
    return;
  }
  if (isNode()) {
    process.on("beforeExit", flushUnflushed);
    process.on("exit", flushUnflushed);
  } else if (typeof globalThis.addEventListener === "function") {
    globalThis.addEventListener("pagehide", flushUnflushed);
    globalThis.addEventListener("beforeunload", flushUnflushed);
  }
}

function unwatchExit(flush: () => void): void {
  if (!unflushed.delete(flush) || unflushed.size > 0) {
    return;
  }
  if (isNode()) {
    process.off("beforeExit", flushUnflushed);
    process.off("exit", flushUnflushed);
  } else if (typeof globalThis.removeEventListener === "function") {
    globalThis.removeEventListener("pagehide", flushUnflushed);
    globalThis.removeEventListener("beforeunload", flushUnflushed);
  }
}

/**
 * The consumer-facing preferences facade (ADR-004). It depends only on an
 * injected {@link PreferencesStore}, never on an application container, so it
 * works in a plain unit test.
 *
 * Reads are synchronous from an in-memory cache. Writes go to the cache at
 * once and are persisted by a debounced asynchronous flush, and again when
 * the process exits or the page unloads.
 */
export class Preferences {
  // Loaded and written values by full key; `undefined` marks a removed key
  private readonly cache = new Map<string, unknown>();
  // Keys written since the last flush; they win over the values of a load
  private readonly pending = new Set<string>();
  private readonly listeners = new Map<string, Set<PreferencesListener>>();
  private readonly rootNode: PreferencesNode;
  private timer: ReturnType<typeof setTimeout> | undefined;
  // The flush in progress, `undefined` when there is none
  private flushing: Promise<void> | undefined;

  private constructor(
    private readonly store: PreferencesStore,
    private readonly flushDelayMs: number,
  ) {
    this.rootNode = new Node(
      {
        read: (fullKey) => this.read(fullKey),
        write: (path, key, value) => this.write(path, key, value),
        subscribe: (path, listener) => this.subscribe(path, listener),
      },
      "",
    );
  }

  /**
   * Compose the preferences facade over a store. If `store` is omitted,
   * a {@link MemoryPreferencesStore} is used — this is what makes unit tests
   * work with zero application bootstrap (ADR-004).
   */
  static create(store?: PreferencesStore, options: PreferencesOptions = {}): Preferences {
    return new Preferences(
      store ?? new MemoryPreferencesStore(),
      options.flushDelayMs ?? DEFAULT_FLUSH_DELAY_MS,
    );
  }

  /**
   * Load the backing store into the in-memory cache (async). Values written
   * before the load finishes are kept.
   */
  async initialize(): Promise<void> {
    const loaded = await this.store.load();
    const unflushed = [...this.pending].map((key) => [key, this.cache.get(key)] as const);
    this.cache.clear();
    for (const [key, value] of Object.entries(loaded)) {
      this.cache.set(key, value);
    }
    for (const [key, value] of unflushed) {
      this.cache.set(key, value);
      // the load replaced the state of the store
      this.store.write(key, value);
    }
  }

  /** Root namespace. Reads are synchronous from the cache. */
  root(): PreferencesNode {
    return this.rootNode;
  }

  /** Force a flush of pending writes (also runs on process exit / page unload). */
  flush(): Promise<void> {
    this.cancelScheduledFlush();
    // With no flush in progress the store is called before this method
    // returns, so a store with a synchronous flush also completes in an
    // exit/unload handler. Otherwise wait, so that an older snapshot never
    // overwrites a newer one
    const flushed =
      this.flushing === undefined
        ? this.persist()
        : this.flushing.then(
            () => this.persist(),
            () => this.persist(),
          );
    this.flushing = flushed;
    const done = (): void => {
      if (this.flushing === flushed) {
        this.flushing = undefined;
      }
    };
    flushed.then(done, done);
    return flushed;
  }

  private async persist(): Promise<void> {
    const keys = [...this.pending];
    this.pending.clear();
    // fromEntries defines own properties, also for a key like `__proto__`
    const snapshot: Record<string, unknown> = Object.fromEntries(
      [...this.cache].filter(([, value]) => value !== undefined),
    );
    try {
      await this.store.flush(snapshot);
    } catch (err) {
      for (const key of keys) {
        this.pending.add(key);
      }
      throw err;
    }
  }

  private read(fullKey: string): unknown {
    return this.cache.has(fullKey) ? this.cache.get(fullKey) : this.store.read(fullKey);
  }

  private write(path: string, key: string, value: unknown): void {
    const fullKey = path === "" ? key : path + SEPARATOR + key;
    if (Object.is(this.read(fullKey), value)) {
      return;
    }
    this.cache.set(fullKey, value);
    this.store.write(fullKey, value);
    this.pending.add(fullKey);
    this.scheduleFlush();
    for (const listener of [...(this.listeners.get(path) ?? [])]) {
      try {
        listener(key, value);
      } catch (err) {
        console.error("Preferences change listener failed:", err);
      }
    }
  }

  private subscribe(path: string, listener: PreferencesListener): () => void {
    let set = this.listeners.get(path);
    if (set === undefined) {
      set = new Set();
      this.listeners.set(path, set);
    }
    set.add(listener);
    return () => {
      set.delete(listener);
    };
  }

  private readonly flushInBackground = (): void => {
    this.flush().catch((err: unknown) => {
      console.error("Failed to flush preferences:", err);
    });
  };

  private scheduleFlush(): void {
    if (this.timer !== undefined) {
      clearTimeout(this.timer);
    }
    this.timer = setTimeout(this.flushInBackground, this.flushDelayMs);
    // Do not keep a Node process alive for the timer, the exit hooks flush
    (this.timer as { unref?: () => void }).unref?.();
    watchExit(this.flushInBackground);
  }

  private cancelScheduledFlush(): void {
    if (this.timer !== undefined) {
      clearTimeout(this.timer);
      this.timer = undefined;
    }
    unwatchExit(this.flushInBackground);
  }
}
