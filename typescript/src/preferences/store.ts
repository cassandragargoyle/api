/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

/**
 * The backend seam of the preferences API (ADR-004). {@link Preferences}
 * depends only on this interface, so a store can be swapped without a change
 * in the consumer code.
 *
 * Keys are full keys: the node path and the key joined with `/`, for example
 * `ui/mainWindow/width`. Values must be JSON-serializable.
 */
export interface PreferencesStore {
  /** Load the persisted values and return the full snapshot. */
  load(): Promise<Record<string, unknown>>;

  /** Read one value from the in-memory state of the store, `undefined` when missing. */
  read(key: string): unknown;

  /** Write one value to the in-memory state of the store; `undefined` removes the key. */
  write(key: string, value: unknown): void;

  /** Persist the full snapshot. */
  flush(snapshot: Record<string, unknown>): Promise<void>;
}
