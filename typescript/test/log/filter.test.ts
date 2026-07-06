/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { beforeEach, describe, expect, it } from "vitest";
import { LogFactory } from "../../src/log/factory";
import { Logging, type LogTransport } from "../../src/log/logging";
import { markLogSkip } from "../../src/log/logSkip";
import type { CanonicalLevel } from "../../src/log/format";

class CapturingTransport implements LogTransport {
  lines: Array<{ formatted: string; level: CanonicalLevel }> = [];
  write(formatted: string, level: CanonicalLevel): void {
    this.lines.push({ formatted, level });
  }
}

describe("default LogSkip filter", () => {
  let capture: CapturingTransport;

  beforeEach(() => {
    capture = new CapturingTransport();
    // Console off so the test only observes the capturing transport.
    Logging.initialize("TRACE", { console: false, transports: [capture] });
  });

  it("drops a markLogSkip-tagged error before any transport", () => {
    const log = LogFactory.getLogger("test.Svc");
    log.error("boom", markLogSkip(new Error("suppress me")));
    expect(capture.lines).toHaveLength(0);
  });

  it("emits an ordinary error", () => {
    const log = LogFactory.getLogger("test.Svc");
    log.error("boom", new Error("real failure"));
    expect(capture.lines).toHaveLength(1);
    expect(capture.lines[0]!.level).toBe("ERROR");
    expect(capture.lines[0]!.formatted).toContain("[test.Svc]: boom");
  });
});

describe("level threshold", () => {
  it("suppresses records below the active level", () => {
    const capture = new CapturingTransport();
    Logging.initialize("INFO", { console: false, transports: [capture] });

    const log = LogFactory.getLogger("test.Svc");
    log.debug("hidden");
    log.info("shown");

    expect(capture.lines).toHaveLength(1);
    expect(capture.lines[0]!.formatted).toContain("shown");
  });
});
