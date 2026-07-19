#!/usr/bin/env node
// Validate contract example JSON documents against their JSON Schemas (Draft 2020-12).
// Examples are matched to schemas by filename convention (see SUFFIX_SCHEMA below),
// so new example files are picked up automatically without editing this script.
//
// Run: npm --prefix typescript run validate:contracts   (or: make validate-contract)

import { createRequire } from "node:module";
import { readFileSync, readdirSync, statSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

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
	[".competency-area.json", "competency-area.schema.json"],
	[".competency-relations.json", "competency-relations.schema.json"],
	[".competency.json", "competency.schema.json"],
];

const readJson = (p) => JSON.parse(readFileSync(p, "utf8"));

function walk(dir) {
	const out = [];
	for (const name of readdirSync(dir)) {
		const full = path.join(dir, name);
		if (statSync(full).isDirectory()) out.push(...walk(full));
		else if (name.endsWith(".json")) out.push(full);
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

const validators = new Map();
function validatorFor(schemaFile) {
	if (!validators.has(schemaFile)) {
		const schema = readJson(path.join(SCHEMAS, schemaFile));
		// Reuse the preloaded compiled schema when it declares an $id; fall back to compile otherwise.
		const validate = (schema.$id && ajv.getSchema(schema.$id)) || ajv.compile(schema);
		validators.set(schemaFile, validate);
	}
	return validators.get(schemaFile);
}

let checked = 0;
let failed = 0;
let skipped = 0;

for (const file of walk(EXAMPLES).sort()) {
	const schemaFile = schemaFor(file);
	const rel = path.relative(ROOT, file);
	if (!schemaFile) {
		skipped++;
		continue; // example not covered by the Opportunity Management contract suffixes
	}
	const validate = validatorFor(schemaFile);
	checked++;
	if (validate(readJson(file))) {
		console.log(`PASS  [${schemaFile}] ${rel}`);
	} else {
		failed++;
		console.log(`FAIL  [${schemaFile}] ${rel}`);
		for (const e of validate.errors) console.log(`      ${e.instancePath || "/"} ${e.message}`);
	}
}

console.log(
	`\n${checked} checked, ${failed} failed, ${skipped} skipped (no matching contract suffix).`,
);
process.exit(failed ? 1 : 0);
