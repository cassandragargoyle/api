/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 *
 * Node-only file backend of `LocalPreferencesStore`. This module is imported
 * lazily by `localStore.ts` so that browser/webview bundles tree-shake the
 * filesystem code away — it must never be imported from the browser-facing
 * entry point.
 */

import { mkdirSync, readFileSync, renameSync, writeFileSync } from "node:fs";
import { homedir } from "node:os";
import { dirname, join } from "node:path";

/** Default path: `~/.<appId>/var/preferences.json`. */
export function defaultPreferencesPath(appId: string): string {
  return join(homedir(), `.${appId}`, "var", "preferences.json");
}

/** Read the preferences file, `undefined` when it does not exist. */
export function readPreferencesFile(path: string): string | undefined {
  try {
    return readFileSync(path, "utf8");
  } catch (err) {
    if ((err as NodeJS.ErrnoException).code === "ENOENT") {
      return undefined;
    }
    throw err;
  }
}

/**
 * Write the preferences file. The write is synchronous, so it also completes
 * in a process `exit` handler, and it goes through a temporary file, so a
 * crash never leaves a half-written file.
 */
export function writePreferencesFile(path: string, content: string): void {
  mkdirSync(dirname(path), { recursive: true });
  const tmp = `${path}.${process.pid}.tmp`;
  writeFileSync(tmp, content, "utf8");
  renameSync(tmp, path);
}
