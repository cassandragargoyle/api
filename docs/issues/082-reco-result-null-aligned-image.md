# Issue #82: Contract: Allow Null aligned_image_path in reco-form-result

**Type**: Bug Fix
**Priority**: Medium
**Status**: Open
**Created**: 2026-11-01
**GitHub**: #82
**Component**: contract
**Related**: #69, portunix-reco #107, portunix-reco #115, portunix-reco #116

## Summary

`reco-form-result.schema.json` requires `aligned_image_path` as a string. portunix-reco returns
`null` when no aligned image is written, so a real result fails the schema check. The schema allows
`null` for this property, and the property stays required.

## Problem Description / Motivation

Since portunix-reco #107, the task `reco.match.template` writes the aligned image only when the
config has `output_path`. Without it, no file is written and `aligned_image_path` is `null`. This is
intended: it stops the task from leaving files in the temporary directory.

The schema was written from a result that had a path. In the acceptance test of portunix-reco #115,
all 283 results of real scans without an error had the value `null`. The validator reports:

```text
['aligned_image_path'] None is not of type 'string'
```

## Scope

### In scope

- `"type": ["string", "null"]` for `aligned_image_path`, with a description of the meaning of `null`
- A valid example without an aligned image

### Out of scope (follow-up issues)

- A change of `required`: the property stays in the result
- Code changes in portunix-reco: the contract test is added there in portunix-reco #116

## Acceptance Criteria

1. A result with `aligned_image_path: null` passes the schema check
2. A result without the property still fails the schema check
3. A result with a string path still passes the schema check

## Compatibility

This relaxes the type of a required property. A document that was valid stays valid. A consumer that
expects a string must handle `null`; this is the behavior of portunix-reco since #107, not a new one.

## Testing

- `make validate-contract` with the new example `form-no-aligned-image.reco-result.json`
- The negative example `form-result-missing-aligned-image-path.reco-result.json`
