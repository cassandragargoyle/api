# Issue #56: Recognition engine lab contract: engine, samples, methodology, and run results

**Type**: Feature
**Priority**: Medium
**Status**: ✅ Implemented (PR #58, 2026-10-17)
**Created**: 2026-10-17
**Closed**: 2026-10-17
**GitHub**: #56
**Component**: contract
**Related**: cassandragargoyle/portunix-reco-studio#55 (Engine Lab component, consumer of this
contract), [ocr-result.schema.json](../../../contract/schemas/ocr-result.schema.json)

## Summary

Add a JSON Schema contract for a document that describes a recognition engine (model), the
location of its training, validation, and test samples, the methodology and commands for
training and evaluation, the metrics, and the run records. Reco Studio uses the document to show
the engine, start training and test runs, show their evaluation, and compare runs.

## Problem Description / Motivation

Recognition engines (OCR engines, detectors, classifiers, trained models) are trained and tested
without one shared description. The information about one engine is spread across scripts and
folders:

- which engine or model it is, and which version
- where the training, validation, and test samples are, and where the ground truth is
- how to train and how to test it (commands, parameters, data split, metrics)
- where the results of earlier runs are

Without a contract, a tool cannot show an engine, start its training or test, or compare two
runs in a reproducible way. The contract must be language-independent, because the documents are
read by TypeScript (Reco Studio) and Python (`ptx-reco`).

## Scope

### In scope

- Three schemas in `contract/schemas/` (JSON Schema 2020-12, `$version` `1.0.0`, same style as
  the other schemas):
  - `reco-engine-lab.schema.json` for the lab document `*.recolab.json`
  - `reco-run.schema.json` for one run record `*.run.json`, including the line shape of the
    training history `*.history.jsonl` in `$defs`
  - `reco-run-sample.schema.json` for one line of the per-sample results `*.samples.jsonl`
- Valid examples in `contract/examples/` and negative examples in `contract/examples/invalid/`
- Support for `.jsonl` files in `scripts/validate-contracts.mjs` (each line is validated against
  the schema of its suffix)
- Entries in `contract/README.md` and `contract/schemas/README.md`
- `make validate-contract` covers the new schemas and their examples

Content of the lab document:

| Section | Content |
| ------- | ------- |
| `meta` | Document version, title, description, author, created/updated dates |
| `engine` | The engine that is tested as one unit: identifier, name, kind (`ocr`, `detector`, `classifier`, `segmenter`, `form`, ...), version; for a simple engine `implementation` and `model`, for a composite engine named `components`; `config` files that change the output (for example a template) |
| `datasets` | Named sample sets with role (`train`, `validation`, `test`), location (path or glob, relative to the document), sample format, ground-truth location and format, optional sample count and checksum |
| `methodology` | Free-text description of the procedure plus structured steps: data preparation, data split (ratio, seed), training parameters (hyperparameters), stop criteria, evaluation protocol |
| `commands` | How to run each phase (`prepare`, `train`, `test`, `evaluate`): runner (`portunix-plugin` or `exec`), arguments with placeholders, working directory, environment, progress reporting |
| `metrics` | Metrics to compute and show (for example field accuracy, CER, WER, accuracy, precision, recall, F1, mAP), with direction (`higher_is_better` / `lower_is_better`) and optional target thresholds |
| `runs` | Location of the run records (folder or glob of `*.run.json`) |

Design rules:

- Paths are relative to the document folder, so the document can move together with its samples
- The contract is engine-neutral: it describes how to call an engine, it does not depend on one
- Large per-sample results must not make the main document large

### Out of scope (follow-up issues)

- The Reco Studio component (cassandragargoyle/portunix-reco-studio#55)
- `train` and `test` subcommands of a `ptx-reco` plugin that run the phases from the document
- The implementation of the `evaluate` phase that writes `*.samples.jsonl` and the metrics
  (`ptx-reco` or Reco Studio); the contract defines only the shape of the data
- A tool that computes or checks the dataset checksum
- TypeScript and Python types: the modules do not provide types for the other contracts

## Open Questions

All open questions are decided, see [Decisions](#decisions).

- Filename suffix: `*.recolab.json`, `*.reco-engine.json`, or other
- Inline run records vs. separate run files (for example `*.run.json` with its own schema)
- Format of the per-sample results (needed to list the worst samples and open them in a viewer)
- Progress reporting of a running command: JSON lines on stdout or a progress file
- Whether the per-sample results can use `ocr-result.schema.json` for OCR engines

## Decisions

Decided on 2026-10-17.

### Files

- Filename suffixes: `*.recolab.json` (lab document), `*.run.json` (run record),
  `*.samples.jsonl` (per-sample results), `*.history.jsonl` (training history)
- Run records are separate files with their own schema; the lab document only references them
  (folder or glob), because the lab document is written by a person and the run records are
  written by a tool

### Engine

- One lab document describes one engine; the engine is the unit that is tested and compared
- A simple engine has `implementation` and `model`; a composite engine (for example a form
  pipeline with `trocr` for digits and `tesseract` for text) has named `components`, each with
  `name`, `implementation`, optional `model` and `version`
- `engine.config` lists the files that change the output, each with `role` and `path` (for
  example `{"role": "template", "path": "stock-count.reco-template.json"}`), so the contract does
  not depend on portunix-reco templates

### Commands

- Each phase has a `runner`, and the runner decides the required fields (`if`/`then` on
  `runner` in the schema, so an error names the real cause):
  - `portunix-plugin` (preferred): the host runs `portunix <plugin> <args...>`; required
    `plugin` (the `name` from `plugin-manifest.schema.json`) and `args`; optional `min_version`,
    so the host can report a missing or old plugin before it starts the run
  - `exec`: the host runs `argv` directly; for engines that are not Portunix plugins
- Optional `cwd` (relative to the document folder, default `.`) and `env` for both runners
- Arguments are arrays, not shell strings, so they work the same on Linux and Windows
- Placeholders come from a fixed list: `{doc_dir}`, `{run_dir}`, `{run_id}`, `{dataset.<name>}`,
  `{config.<role>}`, `{model}`, `{baseline_run_dir}`; the list is documented in the README,
  the schema does not check unknown placeholders
- Exit code `0` means success, any other exit code means failure; a cancelled run is stopped by
  the host, which writes `status: "cancelled"` to the run record
- Progress reporting: `progress.format` is `jsonl-stdout` (lines such as
  `{"progress":0.4,"message":"..."}` on stdout) or `none`

### Run record

| Field | Content |
| ----- | ------- |
| `run_id`, `type` | Run identifier, `training` or `test` |
| `status` | `running`, `succeeded`, `failed`, `cancelled` |
| `started`, `finished` | Start and end time |
| `engine` | `version`, `model_sha256`, `config` (path and `sha256` of each config file) |
| `datasets` | Dataset name and `sha256` of the dataset; only runs with the same dataset checksum are compared |
| `parameters` | The parameters that the run used |
| `metrics` | Summary metric values, for example `{"field_accuracy": 0.94}` |
| `baseline_run` | Optional run that this run is compared with |
| `files` | `samples` (`*.samples.jsonl`), `history` (`*.history.jsonl`), `log`, `artifacts` |

- Dataset checksum: SHA-256 of the sorted lines "relative path + SHA-256 of the file"; the field
  is optional and the rule is documented as a convention

### Per-sample results

- One line of `*.samples.jsonl` per sample: `sample`, `ground_truth`, `prediction` (or
  `prediction_value` for a short value such as a class label), `prediction_format`, `metrics`,
  and optional `fields`
- Each item of `fields` has `field`, optional `zone_id` (link to the zone in the Canvas),
  optional `component`, `expected`, `predicted`, `correct`, and optional `flagged` (marked as
  suspicious by the engine)
- Two runs are compared per sample and field: improved, regressed, changed and still wrong,
  unchanged; the tool computes the comparison, it is not stored
- `prediction_format` is a string; the README lists the known values `reco-export-xml` (export
  of portunix-reco, its XSD stays in portunix-reco), `ocr-result`
  (`ocr-result.schema.json`), and `label`

### Training history

- One line of `*.history.jsonl` per step: `step`, optional `epoch`, `metrics` (for example
  `{"step": 3, "epoch": 1, "metrics": {"loss": 0.41, "cer": 0.08}}`); the line shape is in
  `$defs` of `reco-run.schema.json`

## Acceptance Criteria

1. `reco-engine-lab.schema.json` exists and describes the engine, the datasets (train,
   validation, test with ground truth), the methodology, the commands of each phase, the
   metrics, and the location of the run records
2. `reco-run.schema.json` and `reco-run-sample.schema.json` exist and describe the run record,
   the training history line, and the per-sample result line
3. A valid example of each file kind passes the schema check, and each negative example fails
   for the documented reason
4. `scripts/validate-contracts.mjs` validates `.jsonl` examples line by line
5. `make validate-contract` passes
6. `contract/README.md` and `contract/schemas/README.md` list the schemas, the filename
   suffixes, the placeholders, and the known `prediction_format` values
7. The open questions are decided and the decisions are recorded in this file

## Testing

- `make validate-contract` with the valid examples and the negative examples
- A negative `.jsonl` example with one invalid line fails with the line number in the message
