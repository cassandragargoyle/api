/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { beforeEach, describe, expect, it, vi } from "vitest";

// Record every call into node:fs. If importing the entry or console-only logging
// touched the filesystem, this array would be non-empty. (This mock is scoped to
// this test file only.)
const fsCalls: string[] = [];
vi.mock("node:fs", () => {
  const rec =
    (name: string) =>
    (...args: unknown[]): unknown => {
      void args;
      fsCalls.push(name);
      return undefined;
    };
  return {
    openSync: rec("openSync"),
    closeSync: rec("closeSync"),
    mkdirSync: rec("mkdirSync"),
    readdirSync: rec("readdirSync"),
    renameSync: rec("renameSync"),
    rmSync: rec("rmSync"),
    statSync: rec("statSync"),
    writeSync: rec("writeSync"),
  };
});

beforeEach(() => {
  fsCalls.length = 0;
  // Keep test output clean; the console transport routes info -> console.info.
  vi.spyOn(console, "info").mockImplementation(() => {});
});

describe("browser-safe import", () => {
  it("importing the entry and console logging performs no filesystem access", async () => {
    const mod = await import("../../src/log/index");

    // file:false is the browser scenario: isNode() is never consulted (process
    // is not accessed) and the Node-only file transport module is never loaded.
    mod.Logging.initialize("INFO", { console: true, file: false });
    const log = mod.LogFactory.getLogger("test.Svc");
    log.info("hello {0}", "world");
    await mod.Logging.ready();

    expect(fsCalls).toEqual([]);
  });
});
