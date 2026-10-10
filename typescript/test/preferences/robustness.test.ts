/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { afterEach, describe, expect, it, vi } from "vitest";
import { mkdirSync, readFileSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { LocalPreferencesStore, MemoryPreferencesStore, Preferences } from "../../src/preferences";
import type { PreferencesStorage, PreferencesStore } from "../../src/preferences";

const workDir = join(tmpdir(), `cg-preferences-robustness-${process.pid}`);
const filePath = join(workDir, "preferences.json");

afterEach(() => {
  rmSync(workDir, { recursive: true, force: true });
  vi.useRealTimers();
  vi.restoreAllMocks();
});

describe("unusual keys and values", () => {
  it("persists keys that are names of Object.prototype members", async () => {
    const store = new MemoryPreferencesStore();
    const prefs = Preferences.create(store);
    prefs.root().putInt("__proto__", 1);
    prefs.root().putInt("constructor", 2);
    prefs.root().node("__proto__").putInt("toString", 3);
    await prefs.flush();

    const reloaded = Preferences.create(store);
    await reloaded.initialize();

    expect(reloaded.root().getInt("__proto__", 0)).toBe(1);
    expect(reloaded.root().getInt("constructor", 0)).toBe(2);
    expect(reloaded.root().node("__proto__").getInt("toString", 0)).toBe(3);
    expect(({} as Record<string, unknown>).toString).toBe(Object.prototype.toString);
  });

  it("round-trips a __proto__ key through the JSON file", async () => {
    const first = Preferences.create(new LocalPreferencesStore({ filePath }));
    first.root().putString("__proto__", "value");
    await first.flush();

    const second = Preferences.create(new LocalPreferencesStore({ filePath }));
    await second.initialize();

    expect(second.root().getString("__proto__", "none")).toBe("value");
  });

  it("returns the default for a stored null, object or array", async () => {
    const prefs = Preferences.create(
      new MemoryPreferencesStore({ a: null, b: { width: 1 }, c: [1], d: "1" }),
    );
    await prefs.initialize();
    const root = prefs.root();

    expect(root.getInt("a", 7)).toBe(7);
    expect(root.getString("b", "none")).toBe("none");
    expect(root.getNumber("c", 7)).toBe(7);
    expect(root.getInt("d", 7)).toBe(7);
    expect(root.getBoolean("d", true)).toBe(true);
  });

  it("keeps unicode keys, an empty string and extreme numbers", async () => {
    const store = new MemoryPreferencesStore();
    const prefs = Preferences.create(store);
    const node = prefs.root().node("uživatel 🙂");
    node.putString("název", "");
    node.putInt("max", Number.MAX_SAFE_INTEGER);
    node.putNumber("small", -1e-300);
    node.putInt("zero", 0);
    node.putBoolean("off", false);
    await prefs.flush();

    const reloaded = Preferences.create(store);
    await reloaded.initialize();
    const again = reloaded.root().node("uživatel 🙂");

    expect(again.getString("název", "none")).toBe("");
    expect(again.getInt("max", 0)).toBe(Number.MAX_SAFE_INTEGER);
    expect(again.getNumber("small", 0)).toBe(-1e-300);
    expect(again.getInt("zero", 5)).toBe(0);
    expect(again.getBoolean("off", true)).toBe(false);
  });

  it("reads the values of a store before initialize()", () => {
    const prefs = Preferences.create(new MemoryPreferencesStore({ "ui/width": 1024 }));

    expect(prefs.root().node("ui").getInt("width", 0)).toBe(1024);
  });
});

describe("listeners that change the preferences", () => {
  it("lets a listener unsubscribe itself during the notification", () => {
    const root = Preferences.create().root();
    const second = vi.fn();
    const unsubscribe = root.onChange(() => unsubscribe());
    root.onChange(second);

    root.putInt("width", 1);
    root.putInt("width", 2);

    expect(second).toHaveBeenCalledTimes(2);
  });

  it("lets a listener write another key", () => {
    const root = Preferences.create().root();
    const seen: string[] = [];
    root.onChange((key) => {
      seen.push(key);
      if (key === "width") {
        root.putInt("height", 2);
      }
    });

    root.putInt("width", 1);

    expect(seen).toEqual(["width", "height"]);
    expect(root.getInt("height", 0)).toBe(2);
  });

  it("registers the same listener once", () => {
    const root = Preferences.create().root();
    const listener = vi.fn();
    root.onChange(listener);
    root.onChange(listener);

    root.putInt("width", 1);

    expect(listener).toHaveBeenCalledTimes(1);
  });
});

describe("failing storage", () => {
  it("rejects initialize() when the path is a directory and keeps written values", async () => {
    mkdirSync(filePath, { recursive: true });
    const prefs = Preferences.create(new LocalPreferencesStore({ filePath }));
    prefs.root().putInt("width", 1024);

    await expect(prefs.initialize()).rejects.toThrow();
    expect(prefs.root().getInt("width", 0)).toBe(1024);
  });

  it("reports a failed background flush and keeps the value readable", async () => {
    vi.useFakeTimers();
    const error = vi.spyOn(console, "error").mockImplementation(() => {});
    // the parent of the file is a regular file, so the directory cannot be created
    mkdirSync(workDir, { recursive: true });
    writeFileSync(join(workDir, "blocker"), "");
    const blocked = join(workDir, "blocker", "preferences.json");
    const prefs = Preferences.create(new LocalPreferencesStore({ filePath: blocked }));

    prefs.root().putInt("width", 1024);
    await vi.advanceTimersByTimeAsync(250);
    vi.useRealTimers();
    await vi.waitFor(() => expect(error).toHaveBeenCalledTimes(1));

    expect(prefs.root().getInt("width", 0)).toBe(1024);
  });

  it.each(["[1, 2]", "null", "42", '"text"', ""])(
    "starts with no values when the document is %j",
    async (content) => {
      const warn = vi.spyOn(console, "warn").mockImplementation(() => {});
      mkdirSync(workDir, { recursive: true });
      writeFileSync(filePath, content);

      const prefs = Preferences.create(new LocalPreferencesStore({ filePath }));
      await prefs.initialize();

      expect(prefs.root().getInt("width", 800)).toBe(800);
      expect(warn).toHaveBeenCalledTimes(1);
    },
  );

  it("replaces a damaged file with the next flush", async () => {
    vi.spyOn(console, "warn").mockImplementation(() => {});
    mkdirSync(workDir, { recursive: true });
    writeFileSync(filePath, "{ not json");
    const prefs = Preferences.create(new LocalPreferencesStore({ filePath }));
    await prefs.initialize();

    prefs.root().putInt("width", 1024);
    await prefs.flush();

    expect(JSON.parse(readFileSync(filePath, "utf8"))).toEqual({ width: 1024 });
  });

  it("rejects initialize() when the storage cannot be read", async () => {
    const storage: PreferencesStorage = {
      getItem: () => {
        throw new Error("storage disabled");
      },
      setItem: () => {},
    };
    const prefs = Preferences.create(new LocalPreferencesStore({ storage }));

    await expect(prefs.initialize()).rejects.toThrow("storage disabled");
    expect(prefs.root().getInt("width", 800)).toBe(800);
  });
});

describe("slow asynchronous store", () => {
  /** Store whose flushes finish in the order given by `delays`. */
  function createSlowStore(delays: number[]): PreferencesStore & { persisted: unknown[] } {
    const persisted: unknown[] = [];
    return {
      persisted,
      load: () => Promise.resolve({}),
      read: () => undefined,
      write: () => {},
      flush: (snapshot) =>
        new Promise((resolve) => {
          setTimeout(() => {
            persisted.push(snapshot);
            resolve();
          }, delays.shift() ?? 0);
        }),
    };
  }

  it("persists overlapping flushes in the order of the writes", async () => {
    const store = createSlowStore([50, 5]);
    const prefs = Preferences.create(store);

    prefs.root().putInt("width", 1);
    const first = prefs.flush();
    prefs.root().putInt("width", 2);
    const second = prefs.flush();
    await Promise.all([first, second]);

    expect(store.persisted.at(-1)).toEqual({ width: 2 });
  });

  it("serves reads and writes while a flush is in progress", async () => {
    const store = createSlowStore([20]);
    const prefs = Preferences.create(store);
    prefs.root().putInt("width", 1);

    const flushing = prefs.flush();
    prefs.root().putInt("width", 2);
    expect(prefs.root().getInt("width", 0)).toBe(2);
    await flushing;
    await prefs.flush();

    expect(store.persisted.at(-1)).toEqual({ width: 2 });
  });
});
