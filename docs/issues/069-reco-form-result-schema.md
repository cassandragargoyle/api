# Issue #69: Contract: reco-form-result schema for the JSON result of reco form runs

**Type**: Enhancement
**Priority**: Medium
**Status**: Open
**Created**: 2026-10-29
**GitHub**: #69
**Component**: contract
**Related**: #56 (reco Engine Lab contract), portunix-architecture ADR-011 (decision 6),
ADR-004, portunix-reco #55, #57, #71

## Summary

portunix-reco writes the result of a form recognition run as JSON and, optionally, as the
XML that DENS reads. The shape of the JSON result is defined only by the reco code and
tests. This issue adds a contract schema `reco-form-result.schema.json` for it.

## Problem Description / Motivation

ADR-011 (portunix-architecture, 2026-10-29) decided that the JSON result and the DENS XML
are the output of reco form results. VIM is not used for forms until a trigger occurs
(multi-page merge, operator corrections, combined VIM consumer, replay). With the JSON
result as the output contract for form consumers, it needs a schema like the other
formats of the ecosystem:

- A consumer cannot validate a result without reading reco code
- Changes of the result keys are not visible as contract changes
- reco #71 added the run record `run_info` to the result; its shape is also defined only
  in code

## Scope

### In scope

1. `contract/schemas/reco-form-result.schema.json` (JSON Schema Draft 2020-12) for:
   - the `result` object of the task `reco.match.template` (built by
     `map_alignment_result` in `ptx_reco.worker.task.mapper`, plus `run_info` and the
     optional `xml_path`)
   - the file `<stem>_result.json`. Whether the file holds the `result` object alone or
     the whole task response is decided by portunix-reco #57; the schema description
     states it
2. Content: template identification, alignment result, barcode zone results, recognition
   zone values with confidence and bbox
3. `$defs/RunInfo` for the run record of reco #71: tool and version, run, template and how
   it was chosen, phases with status, engines and models, warnings
4. Example in `contract/examples/` taken from a real run of `reco.match.template`
5. Row in `contract/README.md`

### Rules

- **Name** — `reco-form-result`. The name `reco-run` is already used for the Engine Lab
  run record (`*.run.json`, #56) and is not reused
- **Keys** — the schema follows the current keys of the reco code. No renames in this step
- **No VIM concepts** — sources, merge rules, `collect_mode` and revisions belong to the
  VIM `extraction` layer, not to this schema

### Out of scope

- DENS XML. Its shape is owned by DENS; reco tests the parity with the JSON result
  (portunix-reco #57)
- The output of `ptx-reco-worker process` (`ocr_results`, `barcode_results`,
  `match_results`; `map_pipeline_result`). It is not a form result
- Validation of the reco output against the schema: follow-up issue in portunix-reco

## Open Question

Should `<stem>_result.json` and the task `result` share one root, or should the schema
define the file as the task response with this result inside? The answer follows
portunix-reco #57.

## Acceptance Criteria

1. The example from a real `reco.match.template` run is valid
2. A result without `run_info` (older reco versions) is valid
3. The run record of a degraded alignment (`partial` phase with missing markers) is valid
4. `make validate-contract` passes

## Testing

- `make validate-contract` with the new example
- Validate a result produced by the current portunix-reco against the schema
