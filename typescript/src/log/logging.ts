/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { type CanonicalLevel, type LogLevel, LEVEL_RANK, formatLine, parseLevel } from "./format";
import { isLogSkip } from "./logSkip";

/** A sink for formatted log lines. Custom sinks may be supplied to {@link Logging.initialize}. */
export interface LogTransport {
  /**
   * Write one already-formatted log line.
   *
   * @param formatted the project-standard line, without a trailing newline
   * @param level the canonical level of the record
   */
  write(formatted: string, level: CanonicalLevel): void;
}

/** Options for {@link Logging.initialize}. */
export interface LoggingOptions {
  /** Enable the console transport (default `true`). */
  console?: boolean;
  /** Enable the Node-only file transport (default `false`; no-op in the browser). */
  file?: boolean;
  /** Override the file path (Node only); defaults to the rolling per-PID path. */
  filePath?: string;
  /** Extra sinks appended to the console/file transports. */
  transports?: LogTransport[];
}

/** Runtime capability check: true only on a Node runtime (never in the browser/webview). */
export function isNode(): boolean {
  return (
    typeof process !== "undefined" && process.versions != null && process.versions.node != null
  );
}

/** Console transport: routes the formatted line to the console method matching the level. */
export class ConsoleTransport implements LogTransport {
  write(formatted: string, level: CanonicalLevel): void {
    switch (level) {
      case "ERROR":
        console.error(formatted);
        break;
      case "WARNING":
        console.warn(formatted);
        break;
      case "INFO":
        console.info(formatted);
        break;
      default:
        // TRACE and DEBUG
        console.debug(formatted);
        break;
    }
  }
}

interface Registry {
  activeRank: number;
  transports: LogTransport[];
}

// Module-level shared configuration. Loggers with the same name share it because
// there is a single registry, matching the root-logger semantics of Java/Python.
const registry: Registry = {
  activeRank: LEVEL_RANK.INFO,
  transports: [new ConsoleTransport()],
};

// Resolves once the async Node file transport (if requested) has been attached.
let ready: Promise<void> = Promise.resolve();

/**
 * Internal emit used by loggers. Applies the level threshold and the default
 * `LogSkip` filter, then fans the formatted line out to every transport.
 *
 * @internal
 */
export function emit(
  level: CanonicalLevel,
  name: string,
  message: string,
  args: readonly unknown[],
  error?: unknown,
): void {
  if (LEVEL_RANK[level] > registry.activeRank) {
    return;
  }
  // Default filter: drop records whose error is tagged with markLogSkip.
  if (error !== undefined && isLogSkip(error)) {
    return;
  }
  let formatted = formatLine(level, name, message, args, new Date());
  if (error !== undefined) {
    formatted += "\n" + stringifyError(error);
  }
  for (const transport of registry.transports) {
    transport.write(formatted, level);
  }
}

function stringifyError(error: unknown): string {
  if (error instanceof Error && error.stack) {
    return error.stack;
  }
  return String(error);
}

/** Centralized logging initialization and runtime level management. */
export class Logging {
  /**
   * Initialize transports and the active level. In the browser, `file` is a
   * documented no-op. Safe to call more than once; it replaces the transports.
   */
  static initialize(level: LogLevel, options: LoggingOptions = {}): void {
    const { console: useConsole = true, file = false, filePath, transports = [] } = options;

    const sinks: LogTransport[] = [];
    if (useConsole) {
      sinks.push(new ConsoleTransport());
    }
    registry.transports = sinks;
    Logging.setLogLevel(level);

    if (file && isNode()) {
      // Loaded lazily from a separate module so browser bundles tree-shake the
      // Node-only filesystem code. `ready` lets callers/tests await attachment.
      ready = import("./fileTransport")
        .then((m) => {
          registry.transports.push(m.createFileTransport(filePath));
        })
        .catch((err) => {
          console.error("Failed to initialize file transport:", err);
        });
    } else {
      ready = Promise.resolve();
    }

    // Append any custom sinks after the built-in ones.
    for (const t of transports) {
      registry.transports.push(t);
    }
  }

  /** Change the active level at runtime; accepts the terse aliases. */
  static setLogLevel(level: LogLevel): void {
    const canonical: CanonicalLevel | undefined = parseLevel(level);
    if (canonical === undefined) {
      console.warn(`Invalid log level specified: ${level}`);
      return;
    }
    registry.activeRank = LEVEL_RANK[canonical];
  }

  /** Get the current active level as a canonical name. */
  static getLevel(): CanonicalLevel {
    return (Object.keys(LEVEL_RANK) as CanonicalLevel[]).find(
      (name) => LEVEL_RANK[name] === registry.activeRank,
    )!;
  }

  /** Resolve once any asynchronously-attached transport (Node file) is ready. */
  static ready(): Promise<void> {
    return ready;
  }
}
