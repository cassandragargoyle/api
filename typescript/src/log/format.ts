/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

/** Terse level aliases accepted by the log API, identical to the Java/Python modules. */
export type LogLevel =
  | "TRACE"
  | "DEBUG"
  | "INFO"
  | "WARNING"
  | "ERROR"
  | "T"
  | "D"
  | "I"
  | "W"
  | "E"
  | "4"
  | "3"
  | "2"
  | "1"
  | "0";

/** Canonical level name emitted in the output line. */
export type CanonicalLevel = "TRACE" | "DEBUG" | "INFO" | "WARNING" | "ERROR";

/**
 * Severity rank shared with the numeric aliases: ERROR is most severe (0),
 * TRACE least (4). A record is emitted when its rank is <= the active rank.
 */
export const LEVEL_RANK: Record<CanonicalLevel, number> = {
  ERROR: 0,
  WARNING: 1,
  INFO: 2,
  DEBUG: 3,
  TRACE: 4,
};

const ALIASES: Record<string, CanonicalLevel> = {
  TRACE: "TRACE",
  T: "TRACE",
  "4": "TRACE",
  DEBUG: "DEBUG",
  D: "DEBUG",
  "3": "DEBUG",
  INFO: "INFO",
  I: "INFO",
  "2": "INFO",
  WARNING: "WARNING",
  W: "WARNING",
  "1": "WARNING",
  ERROR: "ERROR",
  E: "ERROR",
  "0": "ERROR",
};

/**
 * Resolve a terse alias to its canonical level.
 *
 * @returns the canonical level, or `undefined` when the alias is unknown
 */
export function parseLevel(level: string): CanonicalLevel | undefined {
  return ALIASES[level.toUpperCase()];
}

function pad2(n: number): string {
  return n < 10 ? "0" + n : String(n);
}

function pad3(n: number): string {
  return n.toString().padStart(3, "0");
}

/** Format a timestamp as `dd/MM/yyyy HH:mm:ss.SSS` (matches the Java/Python formatter). */
export function formatTimestamp(date: Date): string {
  return (
    pad2(date.getDate()) +
    "/" +
    pad2(date.getMonth() + 1) +
    "/" +
    date.getFullYear() +
    " " +
    pad2(date.getHours()) +
    ":" +
    pad2(date.getMinutes()) +
    ":" +
    pad2(date.getSeconds()) +
    "." +
    pad3(date.getMilliseconds())
  );
}

/**
 * Substitute `{0}`, `{1}`, ... positional placeholders with their arguments,
 * matching the Java `Logger.log(level, msg, args)` / Python `%`-style calls so
 * log strings can be copied between runtimes unchanged.
 */
export function substituteArgs(message: string, args: readonly unknown[]): string {
  if (args.length === 0) {
    return message;
  }
  return message.replace(/\{(\d+)\}/g, (match, index: string) => {
    const i = Number(index);
    return i < args.length ? String(args[i]) : match;
  });
}

/**
 * Build the project-standard log line (without a trailing newline):
 * `dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message`.
 */
export function formatLine(
  level: CanonicalLevel,
  name: string,
  message: string,
  args: readonly unknown[],
  date: Date,
): string {
  const body = substituteArgs(message, args);
  const namePart = name !== "" ? ` [${name}]` : "";
  return `${formatTimestamp(date)} ${level}${namePart}: ${body}`;
}
