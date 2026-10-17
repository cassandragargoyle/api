#!/usr/bin/env node
// SPDX-License-Identifier: MIT
// Validate contract example JSON documents against their JSON Schemas (Draft 2020-12).
// Examples are matched to schemas by filename convention (see SUFFIX_SCHEMA below),
// so new example files are picked up automatically without editing this script.
// Documents under an 'invalid' directory are negative tests: they must NOT validate.
// Some contracts have rules that JSON Schema cannot express; their extra checks
// (see EXTRA_CHECKS below) run after the schema check on every accepted document.
// A JSON Lines file ('.jsonl') is validated line by line: every line is one document.
//
// Run: npm --prefix typescript run validate:contracts   (or: make validate-contract)

import { createRequire } from "node:module";
import { readFileSync, readdirSync, statSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { checkVimIdentifiers } from "./vim-identifier-check.mjs";

const HERE = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(HERE, "..");
const SCHEMAS = path.join(ROOT, "contract", "schemas");
const EXAMPLES = path.join(ROOT, "contract", "examples");

// ajv@8 / ajv-formats are devDependencies of the TypeScript module (npm-managed).
const require = createRequire(path.join(ROOT, "typescript", "package.json"));
const Ajv2020 = require("ajv/dist/2020.js");
const addFormats = require("ajv-formats");

// Map filename suffix -> schema filename. Longest suffix wins.
const SUFFIX_SCHEMA = [
	["venture.json", "venture.schema.json"],
	[".product.json", "product.schema.json"],
	[".team.json", "team.schema.json"],
	[".initiative.json", "initiative.schema.json"],
	[".opportunity.json", "opportunity.schema.json"],
	[".usecase.json", "use-case.schema.json"],
	[".estimation.json", "estimation.schema.json"],
	[".discussion.json", "discussion.schema.json"],
	[".backlog.json", "backlog.schema.json"],
	[".epic.json", "epic.schema.json"],
	[".glens.json", "graph-view.schema.json"],
	[".scribe.json", "transcript.schema.json"],
	[".competency-area.json", "competency-area.schema.json"],
	[".competency-relations.json", "competency-relations.schema.json"],
	[".competency.json", "competency.schema.json"],
	[".model.json", "modeler-model.schema.json"],
	[".ops.json", "modeler-ops.schema.json"],
	[".msym.json", "modeler-symbol.schema.json"],
	[".msrc.json", "modeler-symbol-source.schema.json"],
	[".symbol-discovery.json", "modeler-symbol-discovery.schema.json"],
	[".bom.json", "modeler-bom.schema.json"],
	[".vim.json", "vim-document.schema.json"],
	[".recolab.json", "reco-engine-lab.schema.json"],
	[".run.json", "reco-run.schema.json"],
	[".samples.jsonl", "reco-run-sample.schema.json"],
	[".history.jsonl", "reco-run.schema.json#/$defs/HistoryLine"],
];

// Map schema filename -> extra check. A check returns a list of {path, message}
const EXTRA_CHECKS = new Map([["vim-document.schema.json", checkVimIdentifiers]]);

// Return the problems of the document: schema errors first, then the extra check
function problemsOf(schemaFile, doc) {
	const validate = validatorFor(schemaFile);
	if (!validate(doc)) {
		return validate.errors.map((e) => ({ path: e.instancePath || "/", message: e.message }));
	}
	const extra = EXTRA_CHECKS.get(schemaFile);
	return extra ? extra(doc) : [];
}

const readJson = (p) => JSON.parse(readFileSync(p, "utf8"));

// Return the problems of the file; a '.jsonl' file is checked line by line
function problemsOfFile(schemaFile, file) {
	if (!file.endsWith(".jsonl")) return problemsOf(schemaFile, readJson(file));
	const lines = readFileSync(file, "utf8").split(/\r?\n/);
	if (lines.at(-1) === "") lines.pop();
	const problems = [];
	lines.forEach((line, i) => {
		const where = `line ${i + 1}: `;
		let doc;
		try {
			doc = JSON.parse(line);
		} catch (e) {
			problems.push({ path: where + "/", message: `is not JSON (${e.message})` });
			return;
		}
		for (const p of problemsOf(schemaFile, doc)) problems.push({ ...p, path: where + p.path });
	});
	return problems;
}

function walk(dir) {
	const out = [];
	for (const name of readdirSync(dir)) {
		const full = path.join(dir, name);
		if (statSync(full).isDirectory()) out.push(...walk(full));
		else if (name.endsWith(".json") || name.endsWith(".jsonl")) out.push(full);
	}
	return out;
}

function schemaFor(file) {
	const name = path.basename(file);
	// Prefer the longest matching suffix so 'venture.json' does not shadow anything.
	let best = null;
	for (const [suffix, schema] of SUFFIX_SCHEMA) {
		if (name.endsWith(suffix) && (!best || suffix.length > best.suffix.length)) {
			best = { suffix, schema };
		}
	}
	return best ? best.schema : null;
}

const ajv = new Ajv2020({ strict: false, allErrors: true });
addFormats(ajv);

// Preload every schema by $id so cross-file $ref (e.g. competency-*.schema.json
// referencing competency-verification.schema.json) resolves regardless of compile order.
for (const name of readdirSync(SCHEMAS)) {
	if (!name.endsWith(".json")) continue;
	const schema = readJson(path.join(SCHEMAS, name));
	if (schema.$id) ajv.addSchema(schema);
}

// A schema name can point into a schema with a fragment (e.g. 'x.schema.json#/$defs/Line')
const validators = new Map();
function validatorFor(schemaName) {
	if (!validators.has(schemaName)) {
		const [schemaFile, fragment] = schemaName.split("#");
		const schema = readJson(path.join(SCHEMAS, schemaFile));
		let validate;
		if (fragment) {
			validate = ajv.getSchema(`${schema.$id}#${fragment}`);
			if (!validate) throw new Error(`unknown schema fragment: ${schemaName}`);
		} else {
			// Reuse the preloaded compiled schema when it declares an $id; fall back to compile otherwise.
			validate = (schema.$id && ajv.getSchema(schema.$id)) || ajv.compile(schema);
		}
		validators.set(schemaName, validate);
	}
	return validators.get(schemaName);
}

let checked = 0;
let failed = 0;
let skipped = 0;

for (const file of walk(EXAMPLES).sort()) {
	const schemaFile = schemaFor(file);
	const rel = path.relative(ROOT, file);
	const negative = rel.split(path.sep).includes("invalid");
	if (!schemaFile) {
		if (negative) {
			// A negative test that matches no schema would test nothing
			failed++;
			console.log(`FAIL  [no schema] ${rel}`);
			console.log("      negative test without a known contract suffix");
			continue;
		}
		skipped++;
		continue; // example not covered by the Opportunity Management contract suffixes
	}
	const problems = problemsOfFile(schemaFile, file);
	checked++;
	if (negative) {
		// Negative test: the document must be rejected; show the first reason
		if (problems.length === 0) {
			failed++;
			console.log(`FAIL  [${schemaFile}] ${rel}`);
			console.log("      expected to be invalid, but it validates");
		} else {
			const why = `${problems[0].path} ${problems[0].message}`;
			console.log(`PASS  [${schemaFile}] ${rel} (invalid as expected: ${why})`);
		}
		continue;
	}
	if (problems.length === 0) {
		console.log(`PASS  [${schemaFile}] ${rel}`);
	} else {
		failed++;
		console.log(`FAIL  [${schemaFile}] ${rel}`);
		for (const e of problems) console.log(`      ${e.path} ${e.message}`);
	}
}

console.log(
	`\n${checked} checked, ${failed} failed, ${skipped} skipped (no matching contract suffix).`,
);
process.exit(failed ? 1 : 0);
