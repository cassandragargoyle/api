/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { isNode } from "../log/logging";
import type { PreferencesStore } from "./store";

type FileBackend = typeof import("./fileBackend");

const DEFAULT_APP_ID = "cassandragargoyle";
const APP_ID_PATTERN = /^[A-Za-z0-9][A-Za-z0-9._-]*$/;

/** The part of the Web Storage API that {@link LocalPreferencesStore} needs. */
export interface PreferencesStorage {
  getItem(key: string): string | null;
  setItem(key: string, value: string): void;
}

/** Options for {@link LocalPreferencesStore}. */
export interface LocalPreferencesOptions {
  /**
   * Application id (letters, digits, `.`, `_`, `-`). It selects the file
   * `~/.<appId>/var/preferences.json` under Node and the storage key
   * `cg.pref/<appId>` in the browser. Default `cassandragargoyle`.
   */
  appId?: string;
  /** Override the file path (Node only). */
  filePath?: string;
  /** Override the storage key (browser only). */
  storageKey?: string;
  /** Use this storage in place of the runtime detection, for example `sessionStorage`. */
  storage?: PreferencesStorage;
}

/**
 * Local mode store (ADR-004). It keeps all values as one JSON document: in a
 * JSON file under Node / the VS Code extension host, and under one
 * `localStorage` key in the browser / webview. When the runtime has neither,
 * the values stay in memory only.
 */
export class LocalPreferencesStore implements PreferencesStore {
  private readonly appId: string;
  private readonly storageKey: string;
  private readonly filePath: string | undefined;
  private readonly storage: PreferencesStorage | undefined;
  private data = new Map<string, unknown>();
  private backend: FileBackend | undefined;

  constructor(options: LocalPreferencesOptions = {}) {
    this.appId = options.appId ?? DEFAULT_APP_ID;
    if (!APP_ID_PATTERN.test(this.appId)) {
      throw new TypeError(`Invalid preferences app id: ${this.appId}`);
    }
    this.storageKey = options.storageKey ?? `cg.pref/${this.appId}`;
    this.filePath = options.filePath;
    this.storage = options.storage;
  }

  async load(): Promise<Record<string, unknown>> {
    let text: string | undefined;
    if (this.usesFile()) {
      const backend = await this.loadBackend();
      text = backend.readPreferencesFile(this.resolveFilePath(backend));
    } else {
      text = this.resolveStorage()?.getItem(this.storageKey) ?? undefined;
    }
    this.data = new Map(Object.entries(parse(text)));
    return Object.fromEntries(this.data);
  }

  read(key: string): unknown {
    return this.data.get(key);
  }

  write(key: string, value: unknown): void {
    if (value === undefined) {
      this.data.delete(key);
    } else {
      this.data.set(key, value);
    }
  }

  flush(snapshot: Record<string, unknown>): Promise<void> {
    // Not an async function on purpose: once the file backend is loaded, the
    // whole write is synchronous, so it also completes in an exit/unload handler.
    try {
      this.data = new Map(Object.entries(snapshot));
      const text = JSON.stringify(snapshot);
      if (!this.usesFile()) {
        this.resolveStorage()?.setItem(this.storageKey, text);
        return Promise.resolve();
      }
      if (this.backend === undefined) {
        return this.loadBackend().then((backend) => {
          backend.writePreferencesFile(this.resolveFilePath(backend), text);
        });
      }
      this.backend.writePreferencesFile(this.resolveFilePath(this.backend), text);
      return Promise.resolve();
    } catch (err) {
      return Promise.reject(err);
    }
  }

  private usesFile(): boolean {
    return this.storage === undefined && isNode();
  }

  private async loadBackend(): Promise<FileBackend> {
    // Loaded lazily from a separate module so browser bundles tree-shake the
    // Node-only filesystem code.
    this.backend ??= await import("./fileBackend");
    return this.backend;
  }

  private resolveFilePath(backend: FileBackend): string {
    return this.filePath ?? backend.defaultPreferencesPath(this.appId);
  }

  private resolveStorage(): PreferencesStorage | undefined {
    if (this.storage !== undefined) {
      return this.storage;
    }
    try {
      return (globalThis as { localStorage?: PreferencesStorage }).localStorage;
    } catch {
      // access to localStorage can be denied, for example in a sandboxed frame
      return undefined;
    }
  }
}

/** Parse the stored JSON document; a missing or damaged document gives no values. */
function parse(text: string | undefined): Record<string, unknown> {
  if (text === undefined) {
    return {};
  }
  try {
    const parsed: unknown = JSON.parse(text);
    if (typeof parsed === "object" && parsed !== null && !Array.isArray(parsed)) {
      return parsed as Record<string, unknown>;
    }
  } catch {
    // reported below
  }
  console.warn("Stored preferences are not a valid JSON object, starting with no values");
  return {};
}
