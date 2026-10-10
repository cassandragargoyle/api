/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import type { PreferencesStore } from "./store";

/**
 * In-memory store without persistence. It is the default store of
 * {@link Preferences.create}, so preferences work in a unit test without any
 * application bootstrap, and every instance is isolated from the others.
 */
export class MemoryPreferencesStore implements PreferencesStore {
  private data: Map<string, unknown>;

  /**
   * @param initial values available from the start, keyed by the full key
   */
  constructor(initial: Record<string, unknown> = {}) {
    this.data = new Map(Object.entries(initial));
  }

  load(): Promise<Record<string, unknown>> {
    return Promise.resolve(Object.fromEntries(this.data));
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
    this.data = new Map(Object.entries(snapshot));
    return Promise.resolve();
  }
}
