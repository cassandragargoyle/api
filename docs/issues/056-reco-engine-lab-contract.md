# Issue #56: Recognition engine lab contract: engine, samples, methodology, and run results

**Type**: Feature
**Priority**: Medium
**Status**: Open
**Created**: 2026-10-17
**GitHub**: #56
**Component**: contract
**Related**: cassandragargoyle/portunix-reco-studio#55 (Engine Lab component, consumer of this
contract), [ocr-result.schema.json](../../contract/schemas/ocr-result.schema.json)

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

- `contract/schemas/reco-engine-lab.schema.json` (JSON Schema 2020-12, `$version` `1.0.0`,
  same style as the other schemas)
- A valid example in `contract/examples/` and negative examples in `contract/examples/invalid/`
- Entries in `contract/README.md` and `contract/schemas/README.md`
- `make validate-contract` covers the new schema and its examples
- TypeScript and Python types, if the module provides types for the other contracts

Proposed content of the document (draft, to be refined in the design):

| Section | Content |
| ------- | ------- |
| `meta` | Document version, title, description, author, created/updated dates |
| `engine` | Engine identifier, name, kind (`ocr`, `detector`, `classifier`, `segmenter`, ...), implementation, version, model file location, base model |
| `datasets` | Named sample sets with role (`train`, `validation`, `test`), location (path or glob, relative to the document), sample format, ground-truth location and format, optional sample count and checksum |
| `methodology` | Free-text description of the procedure plus structured steps: data preparation, data split (ratio, seed), training parameters (hyperparameters), stop criteria, evaluation protocol |
| `commands` | How to run each phase (`prepare`, `train`, `test`, `evaluate`): command, arguments, working directory, environment; placeholders for dataset and output paths; how the command reports progress |
| `metrics` | Metrics to compute and show (for example CER, WER, accuracy, precision, recall, F1, mAP), with direction (`higher_is_better` / `lower_is_better`) and optional target thresholds |
| `runs` | Run records or a reference to them: run id, type (`training` / `test`), start/end time, engine version, dataset references, parameters, status, metric values, location of logs and artifacts |

Design rules:

- Paths are relative to the document folder, so the document can move together with its samples
- The contract is engine-neutral: it describes how to call an engine, it does not depend on one
- Large per-sample results must not make the main document large

### Out of scope (follow-up issues)

- The Reco Studio component (cassandragargoyle/portunix-reco-studio#55)
- A `ptx-reco` CLI wrapper that runs the phases from the document
- A schema for per-sample results, if the design decides on separate files

## Open Questions

- Filename suffix: `*.recolab.json`, `*.reco-engine.json`, or other
- Inline run records vs. separate run files (for example `*.run.json` with its own schema)
- Format of the per-sample results (needed to list the worst samples and open them in a viewer)
- Progress reporting of a running command: JSON lines on stdout or a progress file
- Whether the per-sample results can use `ocr-result.schema.json` for OCR engines

## Acceptance Criteria

1. `reco-engine-lab.schema.json` exists and describes the engine, the datasets (train,
   validation, test with ground truth), the methodology, the commands of each phase, the
   metrics, and the run records
2. A valid example passes the schema check, and each negative example fails for the documented
   reason
3. `make validate-contract` passes
4. `contract/README.md` and `contract/schemas/README.md` list the schema and the filename suffix
5. The open questions above are decided and the decisions are recorded in this file or in an ADR
6. Types for TypeScript and Python exist, if the module provides types for the other contracts

## Testing

- `make validate-contract` with the valid example and the negative examples
- Type tests of the TypeScript and Python modules, if types are added
