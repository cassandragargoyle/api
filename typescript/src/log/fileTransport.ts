/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 *
 * Node-only file transport. This module is imported lazily by `logging.ts` so
 * that browser/webview bundles tree-shake the filesystem code away — it must
 * never be imported from the browser-facing entry point.
 */

import {
  closeSync,
  mkdirSync,
  openSync,
  readdirSync,
  renameSync,
  rmSync,
  statSync,
  writeSync,
} from "node:fs";
import { homedir } from "node:os";
import { join } from "node:path";

const APP_DIR = ".cassandragargoyle";
const EXPIRATION_DAYS = 10;
const MS_PER_DAY = 24 * 60 * 60 * 1000;

import type { LogTransport } from "./logging";

/** A {@link LogTransport} that also owns a file descriptor and can be closed. */
export interface ClosableTransport extends LogTransport {
  /** Close the underlying file descriptor (for shutdown / tests). */
  close(): void;
}

/** Default rolling per-PID path: `~/.cassandragargoyle/var/log/messages.<pid>.log`. */
function defaultFilePath(): string {
  const dir = join(homedir(), APP_DIR, "var", "log");
  const pid = process.pid.toString().padStart(6, "0");
  return join(dir, `messages.${pid}.log`);
}

/** Delete files in `dir` older than {@link EXPIRATION_DAYS}, mirroring the Java handler. */
function cleanUpOldFiles(dir: string): void {
  let entries: string[];
  try {
    entries = readdirSync(dir);
  } catch {
    return;
  }
  const now = Date.now();
  for (const entry of entries) {
    const full = join(dir, entry);
    try {
      const age = now - statSync(full).mtimeMs;
      if (age > EXPIRATION_DAYS * MS_PER_DAY) {
        rmSync(full, { force: true });
      }
    } catch {
      // ignore files that vanish or cannot be stat'd
    }
  }
}

/** Rotate `path` -> `path.1` -> `path.2` (dropping the previous `.2`), like the Java handler. */
function rotate(path: string): void {
  const f1 = `${path}.1`;
  const f2 = `${path}.2`;
  try {
    rmSync(f2, { force: true });
    renameSync(f1, f2);
  } catch {
    // .1 may not exist yet
  }
  try {
    renameSync(path, f1);
  } catch {
    // path may not exist yet
  }
}

/**
 * Create the Node file transport: prunes old logs, rotates the two previous
 * copies, then opens a fresh per-PID file that each write appends to.
 */
export function createFileTransport(filePath?: string): ClosableTransport {
  const path = filePath ?? defaultFilePath();
  const dir = join(path, "..");
  mkdirSync(dir, { recursive: true });
  cleanUpOldFiles(dir);
  rotate(path);

  // Open truncating; subsequent writes append via the returned fd.
  const fd = openSync(path, "w");

  return {
    write(formatted: string): void {
      try {
        writeSync(fd, formatted + "\n");
      } catch {
        // best-effort: never let logging crash the caller
      }
    },
    // Exposed for tests/shutdown; not part of LogTransport but callable.
    close(): void {
      try {
        closeSync(fd);
      } catch {
        // already closed
      }
    },
  };
}
