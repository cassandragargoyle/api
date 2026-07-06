/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */

import { afterEach, describe, expect, it } from "vitest";
import { existsSync, readFileSync, rmSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { createFileTransport } from "../../src/log/fileTransport";
import { LogFactory } from "../../src/log/factory";
import { Logging } from "../../src/log/logging";

const workDir = join(tmpdir(), `cg-log-test-${process.pid}`);
const logPath = join(workDir, "messages.test.log");

afterEach(() => {
  rmSync(workDir, { recursive: true, force: true });
});

describe("createFileTransport (Node)", () => {
  it("writes formatted lines with a trailing newline", () => {
    const transport = createFileTransport(logPath);
    transport.write("first line", "INFO");
    transport.write("second line", "ERROR");
    transport.close();

    expect(readFileSync(logPath, "utf8")).toBe("first line\nsecond line\n");
  });

  it("rotates the previous file to .1 on re-creation", () => {
    createFileTransport(logPath).close();
    expect(existsSync(logPath)).toBe(true);

    createFileTransport(logPath).close();
    expect(existsSync(`${logPath}.1`)).toBe(true);
  });
});

describe("Logging.initialize with file transport", () => {
  it("attaches the Node file transport and writes the log line", async () => {
    Logging.initialize("INFO", { console: false, file: true, filePath: logPath });
    await Logging.ready();

    LogFactory.getLogger("com.example.Svc").info("Starting {0}", "job-7");

    const contents = readFileSync(logPath, "utf8");
    expect(contents).toContain("INFO [com.example.Svc]: Starting job-7");
  });
});
