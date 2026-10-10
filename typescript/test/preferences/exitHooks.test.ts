/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { describe, expect, it } from "vitest";
import { Preferences } from "../../src/preferences/index";

describe("exit hooks (Node)", () => {
  it("adds no process listener for an instance without writes", async () => {
    const before = process.listenerCount("exit");

    const prefs = Preferences.create();
    await prefs.initialize();
    prefs.root().getInt("width", 800);

    expect(process.listenerCount("exit")).toBe(before);
  });

  it("has the hooks only while writes are not flushed", async () => {
    const exit = process.listenerCount("exit");
    const beforeExit = process.listenerCount("beforeExit");
    const prefs = Preferences.create();

    prefs.root().putInt("width", 1024);
    expect(process.listenerCount("exit")).toBe(exit + 1);
    expect(process.listenerCount("beforeExit")).toBe(beforeExit + 1);

    await prefs.flush();
    expect(process.listenerCount("exit")).toBe(exit);
    expect(process.listenerCount("beforeExit")).toBe(beforeExit);
  });

  it("shares one listener between all instances with unflushed writes", async () => {
    const before = process.listenerCount("exit");
    const all = Array.from({ length: 20 }, () => Preferences.create());

    for (const prefs of all) {
      prefs.root().putInt("width", 1024);
    }
    expect(process.listenerCount("exit")).toBe(before + 1);

    await Promise.all(all.map((prefs) => prefs.flush()));
    expect(process.listenerCount("exit")).toBe(before);
  });
});
