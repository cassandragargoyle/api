#!/usr/bin/env node
// SPDX-License-Identifier: MIT
// Print the totals of a JaCoCo coverage report and the path to its HTML page.
// The script reads jacoco.csv, so it does not need awk and runs the same on Linux and Windows.
//
// Run: node scripts/coverage-summary.mjs java/target/site/jacoco   (or: make coverage-java)

import { readFileSync } from "node:fs";
import path from "node:path";

const reportDir = process.argv[2] ?? "java/target/site/jacoco";
const csvPath = path.join(reportDir, "jacoco.csv");

let lines;
try {
	lines = readFileSync(csvPath, "utf8").trim().split(/\r?\n/);
} catch (e) {
	console.error(`Coverage report not found: ${csvPath} (${e.code ?? e.message})`);
	process.exit(1);
}

// Header: GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,...
const header = lines[0].split(",");
const totals = {};
for (const counter of ["LINE", "BRANCH", "INSTRUCTION"]) {
	const missedIndex = header.indexOf(`${counter}_MISSED`);
	const coveredIndex = header.indexOf(`${counter}_COVERED`);
	let missed = 0;
	let covered = 0;
	for (const row of lines.slice(1)) {
		const cells = row.split(",");
		missed += Number(cells[missedIndex]);
		covered += Number(cells[coveredIndex]);
	}
	totals[counter] = { covered, total: missed + covered };
}

const format = ({ covered, total }) =>
	`${total === 0 ? "n/a" : ((100 * covered) / total).toFixed(1) + "%"} (${covered}/${total})`;

console.log("");
console.log(
	`Java coverage: lines ${format(totals.LINE)}, branches ${format(totals.BRANCH)}, instructions ${format(totals.INSTRUCTION)}`
);
console.log(`Report: ${path.join(reportDir, "index.html")}`);
