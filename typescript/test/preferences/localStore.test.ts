/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { afterEach, describe, expect, it, vi } from "vitest";
import { existsSync, mkdirSync, readFileSync, readdirSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { LocalPreferencesStore, Preferences } from "../../src/preferences/index";
import type { PreferencesStorage } from "../../src/preferences/index";

const workDir = join(tmpdir(), `cg-preferences-test-${process.pid}`);
const filePath = join(workDir, "var", "preferences.json");

/** Minimal Web Storage mock backed by a map. */
function createStorage(): PreferencesStorage & { items: Map<string, string> } {
  const items = new Map<string, string>();
  return {
    items,
    getItem: (key) => items.get(key) ?? null,
    setItem: (key, value) => {
      items.set(key, value);
    },
  };
}

afterEach(() => {
  rmSync(workDir, { recursive: true, force: true });
  vi.unstubAllEnvs();
  vi.restoreAllMocks();
});

describe("LocalPreferencesStore (Node JSON file)", () => {
  it("restores a written value after a new initialize()", async () => {
    const first = Preferences.create(new LocalPreferencesStore({ filePath }));
    await first.initialize();
    first.root().node("ui/mainWindow").putInt("width", 1024);
    await first.flush();

    const second = Preferences.create(new LocalPreferencesStore({ filePath }));
    await second.initialize();

    expect(second.root().node("ui/mainWindow").getInt("width", 0)).toBe(1024);
    expect(JSON.parse(readFileSync(filePath, "utf8"))).toEqual({ "ui/mainWindow/width": 1024 });
  });

  it("starts with no values when the file does not exist", async () => {
    const prefs = Preferences.create(new LocalPreferencesStore({ filePath }));
    await prefs.initialize();

    expect(prefs.root().getInt("width", 800)).toBe(800);
    expect(existsSync(filePath)).toBe(false);
  });

  it("flushes without a previous initialize() and leaves no temporary file", async () => {
    const prefs = Preferences.create(new LocalPreferencesStore({ filePath }));
    prefs.root().putBoolean("visible", true);
    await prefs.flush();

    expect(readdirSync(join(workDir, "var"))).toEqual(["preferences.json"]);
  });

  it("writes the file synchronously once the store is loaded", async () => {
    const prefs = Preferences.create(new LocalPreferencesStore({ filePath }));
    await prefs.initialize();
    prefs.root().putInt("width", 1024);

    // no await: this is what a process exit handler can do
    void prefs.flush();

    expect(JSON.parse(readFileSync(filePath, "utf8"))).toEqual({ width: 1024 });
  });

  it("starts with no values when the file is damaged", async () => {
    const warn = vi.spyOn(console, "warn").mockImplementation(() => {});
    mkdirSync(join(workDir, "var"), { recursive: true });
    writeFileSync(filePath, "{ not json");

    const prefs = Preferences.create(new LocalPreferencesStore({ filePath }));
    await prefs.initialize();

    expect(prefs.root().getInt("width", 800)).toBe(800);
    expect(warn).toHaveBeenCalledTimes(1);
  });

  it("uses ~/.<appId>/var/preferences.json by default", async () => {
    // os.homedir() reads HOME on POSIX and USERPROFILE on Windows
    vi.stubEnv("HOME", workDir);
    vi.stubEnv("USERPROFILE", workDir);
    const prefs = Preferences.create(new LocalPreferencesStore({ appId: "pilot" }));
    await prefs.initialize();
    prefs.root().putInt("width", 1024);
    await prefs.flush();

    const expected = join(workDir, ".pilot", "var", "preferences.json");
    expect(JSON.parse(readFileSync(expected, "utf8"))).toEqual({ width: 1024 });
  });

  it("reads and removes values through the store seam", async () => {
    const store = new LocalPreferencesStore({ filePath });
    store.write("width", 1024);
    expect(store.read("width")).toBe(1024);

    store.write("width", undefined);

    expect(store.read("width")).toBeUndefined();
  });

  it("rejects an app id that is not a plain name", () => {
    expect(() => new LocalPreferencesStore({ appId: "../etc" })).toThrow(TypeError);
    expect(() => new LocalPreferencesStore({ appId: "" })).toThrow(TypeError);
  });
});

describe("LocalPreferencesStore (Web Storage)", () => {
  it("round-trips the values as one JSON document under one key", async () => {
    const storage = createStorage();
    const first = Preferences.create(new LocalPreferencesStore({ appId: "pilot", storage }));
    await first.initialize();
    first.root().node("ui").putNumber("ratio", 0.25);
    first.root().putString("theme", "dark");
    await first.flush();

    const second = Preferences.create(new LocalPreferencesStore({ appId: "pilot", storage }));
    await second.initialize();

    expect([...storage.items.keys()]).toEqual(["cg.pref/pilot"]);
    expect(second.root().node("ui").getNumber("ratio", 0)).toBe(0.25);
    expect(second.root().getString("theme", "light")).toBe("dark");
  });

  it("keeps applications with different ids apart", async () => {
    const storage = createStorage();
    const first = Preferences.create(new LocalPreferencesStore({ appId: "one", storage }));
    first.root().putInt("width", 1024);
    await first.flush();

    const second = Preferences.create(new LocalPreferencesStore({ appId: "two", storage }));
    await second.initialize();

    expect(second.root().getInt("width", 800)).toBe(800);
  });

  it("rejects the flush when the storage refuses the write", async () => {
    const storage = createStorage();
    storage.setItem = () => {
      throw new Error("quota exceeded");
    };
    const prefs = Preferences.create(new LocalPreferencesStore({ storage }));
    prefs.root().putInt("width", 1024);

    await expect(prefs.flush()).rejects.toThrow("quota exceeded");
    expect(prefs.root().getInt("width", 0)).toBe(1024);
  });
});
