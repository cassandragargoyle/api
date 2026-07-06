/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { describe, expect, it } from "vitest";
import {
  formatLine,
  formatTimestamp,
  substituteArgs,
  type CanonicalLevel,
} from "../../src/log/format";

// Fixed timestamp shared with the Java/Python parity vectors: 05/07/2026 14:30:00.123
const FIXED = new Date(2026, 6, 5, 14, 30, 0, 123);
const NAME = "com.example.Svc";

interface Vector {
  level: CanonicalLevel;
  message: string;
  args: unknown[];
  expected: string;
}

const VECTORS: Vector[] = [
  {
    level: "INFO",
    message: "Starting {0}",
    args: ["job-7"],
    expected: "05/07/2026 14:30:00.123 INFO [com.example.Svc]: Starting job-7",
  },
  {
    level: "ERROR",
    message: "Failed {0}: {1}",
    args: ["job-7", "timeout"],
    expected: "05/07/2026 14:30:00.123 ERROR [com.example.Svc]: Failed job-7: timeout",
  },
  {
    level: "WARNING",
    message: "Low disk",
    args: [],
    expected: "05/07/2026 14:30:00.123 WARNING [com.example.Svc]: Low disk",
  },
  {
    level: "DEBUG",
    message: "x={0}",
    args: [42],
    expected: "05/07/2026 14:30:00.123 DEBUG [com.example.Svc]: x=42",
  },
];

describe("formatter parity vectors", () => {
  it.each(VECTORS)("$level $message -> $expected", ({ level, message, args, expected }) => {
    expect(formatLine(level, NAME, message, args, FIXED)).toBe(expected);
  });
});

describe("formatTimestamp", () => {
  it("renders dd/MM/yyyy HH:mm:ss.SSS with zero padding", () => {
    const d = new Date(2026, 0, 9, 3, 4, 5, 7);
    expect(formatTimestamp(d)).toBe("09/01/2026 03:04:05.007");
  });
});

describe("substituteArgs", () => {
  it("leaves the message unchanged when there are no args", () => {
    expect(substituteArgs("no placeholders {0}", [])).toBe("no placeholders {0}");
  });

  it("keeps unmatched placeholders verbatim", () => {
    expect(substituteArgs("{0} and {1}", ["a"])).toBe("a and {1}");
  });
});
