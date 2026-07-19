/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { emit } from "./logging";

/**
 * A named logger. Placeholders in `message` use the `{0}`, `{1}` positional
 * convention shared with Java/Python.
 */
export interface Logger {
  trace(message: string, ...args: unknown[]): void;
  debug(message: string, ...args: unknown[]): void;
  info(message: string, ...args: unknown[]): void;
  warn(message: string, ...args: unknown[]): void;
  /** Log at ERROR. An optional error is appended verbatim (and honors `markLogSkip`). */
  error(message: string, error?: unknown, ...args: unknown[]): void;
}

/** Allocates {@link Logger} instances bound to a name, mirroring the Java `LogFactory`. */
export class LogFactory {
  /**
   * Allocate a logger bound to the given name (typically the module or class
   * name). Loggers with the same name share configuration.
   */
  static getLogger(name: string): Logger {
    return {
      trace(message: string, ...args: unknown[]): void {
        emit("TRACE", name, message, args);
      },
      debug(message: string, ...args: unknown[]): void {
        emit("DEBUG", name, message, args);
      },
      info(message: string, ...args: unknown[]): void {
        emit("INFO", name, message, args);
      },
      warn(message: string, ...args: unknown[]): void {
        emit("WARNING", name, message, args);
      },
      error(message: string, error?: unknown, ...args: unknown[]): void {
        emit("ERROR", name, message, args, error);
      },
    };
  }
}
