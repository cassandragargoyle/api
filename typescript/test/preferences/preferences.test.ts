/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { afterEach, describe, expect, it, vi } from "vitest";
import { MemoryPreferencesStore, Preferences } from "../../src/preferences/index";
import type { PreferencesStore } from "../../src/preferences/index";

afterEach(() => {
  vi.useRealTimers();
  vi.restoreAllMocks();
});

describe("Preferences without bootstrap (ADR-004)", () => {
  it("returns the defaults with no store and no initialize()", () => {
    const prefs = Preferences.create().root();

    expect(prefs.getString("theme", "light")).toBe("light");
    expect(prefs.getInt("width", 800)).toBe(800);
    expect(prefs.getNumber("ratio", 0.5)).toBe(0.5);
    expect(prefs.getBoolean("visible", true)).toBe(true);
  });

  it("reads back a written value in memory", () => {
    const prefs = Preferences.create().root();

    prefs.putString("theme", "dark");
    prefs.putInt("width", 1024);
    prefs.putNumber("ratio", 0.25);
    prefs.putBoolean("visible", false);

    expect(prefs.getString("theme", "light")).toBe("dark");
    expect(prefs.getInt("width", 800)).toBe(1024);
    expect(prefs.getNumber("ratio", 0.5)).toBe(0.25);
    expect(prefs.getBoolean("visible", true)).toBe(false);
  });

  it("isolates instances from each other", async () => {
    const first = Preferences.create();
    first.root().putInt("width", 1024);
    await first.flush();

    const second = Preferences.create();
    await second.initialize();

    expect(second.root().getInt("width", 800)).toBe(800);
  });
});

describe("typed accessors", () => {
  it("returns the default when the stored type does not match", () => {
    const prefs = Preferences.create().root();
    prefs.putString("width", "wide");
    prefs.putNumber("ratio", 0.5);

    expect(prefs.getInt("width", 800)).toBe(800);
    expect(prefs.getBoolean("width", true)).toBe(true);
    expect(prefs.getInt("ratio", 1)).toBe(1);
    expect(prefs.getString("ratio", "none")).toBe("none");
  });

  it("rejects values that are not an integer or a finite number", () => {
    const prefs = Preferences.create().root();

    expect(() => prefs.putInt("width", 1.5)).toThrow(TypeError);
    expect(() => prefs.putNumber("ratio", Number.NaN)).toThrow(TypeError);
    expect(() => prefs.putNumber("ratio", Number.POSITIVE_INFINITY)).toThrow(TypeError);
  });

  it("rejects an empty key and a key with the separator", () => {
    const prefs = Preferences.create().root();

    expect(() => prefs.putInt("", 1)).toThrow(TypeError);
    expect(() => prefs.putInt("ui/width", 1)).toThrow(TypeError);
    expect(() => prefs.getInt("ui/width", 1)).toThrow(TypeError);
  });

  it("does not read properties of Object.prototype as values", () => {
    const prefs = Preferences.create().root();

    expect(prefs.getString("constructor", "none")).toBe("none");
    expect(prefs.getString("__proto__", "none")).toBe("none");
  });

  it("removes a key", () => {
    const prefs = Preferences.create().root();
    prefs.putInt("width", 1024);

    prefs.remove("width");

    expect(prefs.getInt("width", 800)).toBe(800);
  });
});

describe("namespaces", () => {
  it("keeps the same key apart in different nodes", () => {
    const root = Preferences.create().root();
    root.node("ui").node("mainWindow").putInt("width", 1024);
    root.node("ui").node("sidebar").putInt("width", 240);

    expect(root.node("ui/mainWindow").getInt("width", 0)).toBe(1024);
    expect(root.node("/ui/sidebar/").getInt("width", 0)).toBe(240);
    expect(root.getInt("width", 0)).toBe(0);
  });

  it("stores the full key as the path and the key joined with a slash", async () => {
    const store = new MemoryPreferencesStore();
    const prefs = Preferences.create(store);
    prefs.root().node("ui").node("mainWindow").putInt("width", 1024);
    prefs.root().putString("theme", "dark");
    await prefs.flush();

    expect(await store.load()).toEqual({ "ui/mainWindow/width": 1024, theme: "dark" });
  });

  it("returns the same node for an empty path", () => {
    const root = Preferences.create().root();
    root.node("").putInt("width", 1024);

    expect(root.getInt("width", 0)).toBe(1024);
  });
});

describe("change listeners", () => {
  it("notifies the listeners of the node about a put and a remove", () => {
    const root = Preferences.create().root();
    const node = root.node("ui");
    const changes: [string, unknown][] = [];
    node.onChange((key, value) => changes.push([key, value]));

    root.node("ui").putInt("width", 1024);
    node.remove("width");

    expect(changes).toEqual([
      ["width", 1024],
      ["width", undefined],
    ]);
  });

  it("does not notify about other nodes or an unchanged value", () => {
    const root = Preferences.create().root();
    const listener = vi.fn();
    root.node("ui").onChange(listener);
    root.node("ui").putInt("width", 1024);
    listener.mockClear();

    root.putInt("width", 1);
    root.node("ui/sidebar").putInt("width", 2);
    root.node("ui").putInt("width", 1024);

    expect(listener).not.toHaveBeenCalled();
  });

  it("stops after the unsubscribe", () => {
    const root = Preferences.create().root();
    const listener = vi.fn();
    const unsubscribe = root.onChange(listener);

    unsubscribe();
    root.putInt("width", 1024);

    expect(listener).not.toHaveBeenCalled();
  });

  it("keeps the write and the other listeners when a listener throws", () => {
    vi.spyOn(console, "error").mockImplementation(() => {});
    const root = Preferences.create().root();
    const listener = vi.fn();
    root.onChange(() => {
      throw new Error("broken listener");
    });
    root.onChange(listener);

    root.putInt("width", 1024);

    expect(listener).toHaveBeenCalledWith("width", 1024);
    expect(root.getInt("width", 0)).toBe(1024);
  });
});

describe("sync read / async persist", () => {
  it("loads the values of the store in initialize()", async () => {
    const prefs = Preferences.create(new MemoryPreferencesStore({ "ui/width": 1024 }));
    await prefs.initialize();

    expect(prefs.root().node("ui").getInt("width", 0)).toBe(1024);
  });

  it("keeps a value written before initialize() finished", async () => {
    const prefs = Preferences.create(new MemoryPreferencesStore({ width: 1, height: 2 }));
    prefs.root().putInt("width", 1024);
    prefs.root().remove("height");

    await prefs.initialize();

    expect(prefs.root().getInt("width", 0)).toBe(1024);
    expect(prefs.root().getInt("height", 0)).toBe(0);
  });

  it("flushes once after the debounce delay", async () => {
    vi.useFakeTimers();
    const store = new MemoryPreferencesStore();
    const flush = vi.spyOn(store, "flush");
    const prefs = Preferences.create(store, { flushDelayMs: 100 });

    prefs.root().putInt("width", 1);
    prefs.root().putInt("width", 2);
    expect(flush).not.toHaveBeenCalled();
    await vi.advanceTimersByTimeAsync(100);

    expect(flush).toHaveBeenCalledTimes(1);
    expect(flush).toHaveBeenCalledWith({ width: 2 });
  });

  it("does not persist removed keys", async () => {
    const store = new MemoryPreferencesStore({ width: 1024, theme: "dark" });
    const prefs = Preferences.create(store);
    await prefs.initialize();

    prefs.root().remove("width");
    await prefs.flush();

    expect(await store.load()).toEqual({ theme: "dark" });
  });

  it("reports a failed flush and writes the values with the next flush", async () => {
    let fail = true;
    const flushed: Record<string, unknown>[] = [];
    const store: PreferencesStore = {
      load: () => Promise.resolve({ width: 1 }),
      read: () => undefined,
      write: () => {},
      flush: (snapshot) => {
        if (fail) {
          return Promise.reject(new Error("disk full"));
        }
        flushed.push(snapshot);
        return Promise.resolve();
      },
    };
    const prefs = Preferences.create(store);
    prefs.root().putInt("width", 1024);

    await expect(prefs.flush()).rejects.toThrow("disk full");
    fail = false;
    // the unflushed value must still win over the loaded one
    await prefs.initialize();
    await prefs.flush();

    expect(flushed).toEqual([{ width: 1024 }]);
  });
});
