/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { describe, expect, it } from "vitest";
import { parseLevel, type CanonicalLevel, type LogLevel } from "../../src/log/format";
import { Logging } from "../../src/log/logging";

// Every alias must resolve identically to the Java/Python modules.
const ALIAS_VECTORS: Array<[LogLevel[], CanonicalLevel]> = [
  [["TRACE", "T", "4"], "TRACE"],
  [["DEBUG", "D", "3"], "DEBUG"],
  [["INFO", "I", "2"], "INFO"],
  [["WARNING", "W", "1"], "WARNING"],
  [["ERROR", "E", "0"], "ERROR"],
];

describe("parseLevel alias parity", () => {
  for (const [aliases, canonical] of ALIAS_VECTORS) {
    for (const alias of aliases) {
      it(`${alias} -> ${canonical}`, () => {
        expect(parseLevel(alias)).toBe(canonical);
      });
      it(`${alias.toLowerCase()} (lower-case) -> ${canonical}`, () => {
        expect(parseLevel(alias.toLowerCase())).toBe(canonical);
      });
    }
  }

  it("returns undefined for an unknown alias", () => {
    expect(parseLevel("BOGUS")).toBeUndefined();
  });
});

describe("Logging.setLogLevel / getLevel round trip", () => {
  it.each(
    ALIAS_VECTORS.flatMap(([aliases, canonical]) => aliases.map((a) => [a, canonical] as const)),
  )("setLogLevel(%s) -> getLevel() === %s", (alias, canonical) => {
    Logging.setLogLevel(alias);
    expect(Logging.getLevel()).toBe(canonical);
  });
});
