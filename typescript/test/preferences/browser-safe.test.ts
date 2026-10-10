/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

// Browser-like environment for this test file: the runtime check reports no
// Node, every call into node:fs is recorded, and the globals `localStorage`
// and `addEventListener` are recording stubs.
vi.mock("../../src/log/logging", () => ({ isNode: () => false }));

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
    mkdirSync: rec("mkdirSync"),
    readFileSync: rec("readFileSync"),
    renameSync: rec("renameSync"),
    writeFileSync: rec("writeFileSync"),
  };
});

const storageCalls: string[] = [];
const items = new Map<string, string>();
const pageListeners = new Map<string, () => void>();

beforeEach(() => {
  fsCalls.length = 0;
  storageCalls.length = 0;
  items.clear();
  pageListeners.clear();
  vi.stubGlobal("localStorage", {
    getItem: (key: string) => {
      storageCalls.push("getItem");
      return items.get(key) ?? null;
    },
    setItem: (key: string, value: string) => {
      storageCalls.push("setItem");
      items.set(key, value);
    },
  });
  vi.stubGlobal("addEventListener", (type: string, listener: () => void) => {
    pageListeners.set(type, listener);
  });
  vi.stubGlobal("removeEventListener", (type: string) => {
    pageListeners.delete(type);
  });
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("browser-safe import", () => {
  it("importing the entry and reading a default performs no I/O", async () => {
    const processListeners = process.listenerCount("exit");

    const mod = await import("../../src/preferences/index");
    const prefs = mod.Preferences.create();
    expect(prefs.root().node("ui").getInt("width", 800)).toBe(800);

    expect(fsCalls).toEqual([]);
    expect(storageCalls).toEqual([]);
    expect(pageListeners.size).toBe(0);
    expect(process.listenerCount("exit")).toBe(processListeners);
  });

  it("LocalPreferencesStore uses localStorage and never the filesystem", async () => {
    const mod = await import("../../src/preferences/index");

    const first = mod.Preferences.create(new mod.LocalPreferencesStore({ appId: "pilot" }));
    await first.initialize();
    first.root().node("ui").putInt("width", 1024);
    await first.flush();

    const second = mod.Preferences.create(new mod.LocalPreferencesStore({ appId: "pilot" }));
    await second.initialize();

    expect(second.root().node("ui").getInt("width", 800)).toBe(1024);
    expect(items.get("cg.pref/pilot")).toBe('{"ui/width":1024}');
    expect(fsCalls).toEqual([]);
  });

  it("flushes pending writes when the page unloads", async () => {
    const processListeners = process.listenerCount("exit");
    const mod = await import("../../src/preferences/index");
    const prefs = mod.Preferences.create(new mod.LocalPreferencesStore({ appId: "pilot" }));

    prefs.root().putBoolean("visible", true);
    expect([...pageListeners.keys()].sort()).toEqual(["beforeunload", "pagehide"]);
    expect(process.listenerCount("exit")).toBe(processListeners);

    // the write must be complete when the handler returns
    pageListeners.get("pagehide")!();

    expect(items.get("cg.pref/pilot")).toBe('{"visible":true}');
    expect(pageListeners.size).toBe(0);
  });

  it("stays in memory when the access to localStorage is denied", async () => {
    vi.unstubAllGlobals();
    Object.defineProperty(globalThis, "localStorage", {
      configurable: true,
      get: () => {
        throw new Error("SecurityError");
      },
    });
    try {
      const mod = await import("../../src/preferences/index");
      const prefs = mod.Preferences.create(new mod.LocalPreferencesStore());
      await prefs.initialize();
      prefs.root().putInt("width", 1024);
      await prefs.flush();

      expect(prefs.root().getInt("width", 800)).toBe(1024);
      expect(fsCalls).toEqual([]);
    } finally {
      Reflect.deleteProperty(globalThis, "localStorage");
    }
  });
});
