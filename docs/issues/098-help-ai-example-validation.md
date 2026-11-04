# Issue #98: Contract: Validate help-ai Examples and Refresh text-extractor Example

**Type**: Enhancement
**Priority**: Medium
**Status**: Open
**Created**: 2026-11-04
**GitHub**: #98
**Component**: contract
**Related**: portunix-plugins #92, portunix-plugins #91, portunix-plugins #60

## Summary

`contract/examples/help-ai-text-extractor.json` matches no suffix in `scripts/validate-contracts.mjs`,
so `make validate-contract` skips it and CI never checks it against `help-ai.schema.json`. The
example is also out of date. Wire `--help-ai` examples into the validator, refresh the example from
the current text-extractor output, and add a `global_flags` example and negative tests.

## Problem Description / Motivation

`help-ai.schema.json` and the text-extractor example were added in the initial public release. The
schema itself is correct: the current `--help-ai` output of six Portunix plugins (text-extractor,
redmine, docgen, fulltext, modeler, neuralink) validates against it with Ajv (draft 2020-12, the same
setup as `validate-contracts.mjs`).

Three gaps remain:

1. **The example is not validated.** The validator maps examples to schemas by filename suffix
   (`SUFFIX_SCHEMA`). There is no entry for `--help-ai`, and the name `help-ai-text-extractor.json`
   has no suffix that could match one. A future schema change that breaks the example goes unnoticed.
2. **The example is out of date.** It shows text-extractor 1.1.10. The current output (1.1.18, after
   the Picocli migration) has more parameters per command and one more supported extension and MIME
   type.
3. **`global_flags` has no example.** The schema defines the optional top-level `global_flags` array
   (emitted by redmine, modeler and neuralink), but no example covers it. There are also no negative
   tests for the `--help-ai` schema.

## Scope

### In scope

- Add `[".help-ai.json", "help-ai.schema.json"]` to `SUFFIX_SCHEMA` in `scripts/validate-contracts.mjs`
- Rename the example to `contract/examples/text-extractor.help-ai.json` (`git mv`) and replace its
  content with the current `text-extractor --help-ai` output
- Add `contract/examples/redmine.help-ai.json` from the current `ptx-redmine --help-ai` output, as the
  example with `global_flags`
- Add negative tests in `contract/examples/invalid/`, for example:
  - `help-ai-extension-uppercase.help-ai.json`: `supported_extensions` entry `.PDF`
  - `help-ai-extension-without-dot.help-ai.json`: `supported_extensions` entry `pdf`
  - `help-ai-command-without-name.help-ai.json`: a command without `name`
  - `help-ai-unknown-key.help-ai.json`: an unknown top-level property
  - `help-ai-missing-tool.help-ai.json`: a document without `tool`
- Update the example tables in `contract/README.md` and `contract/README.cs.md`
- `CHANGELOG.md` entry, if contract example changes are recorded there

### Out of scope (follow-up issues)

- Schema changes: the current outputs validate, so `help-ai.schema.json` stays as it is
- Changes in portunix-plugins (a producer-side contract test and the fulltext version mismatch are
  tracked there, see portunix-plugins #92)

## Acceptance Criteria

1. `make validate-contract` validates `text-extractor.help-ai.json` and `redmine.help-ai.json` against
   `help-ai.schema.json`, and both pass
2. Every new document in `contract/examples/invalid/` fails the schema check
3. The text-extractor example matches the current `text-extractor --help-ai` output (version 1.1.18
   or later)
4. No link in `contract/README.md` or `contract/README.cs.md` points to the old filename
5. The `validate-contract` CI job passes

## Testing

```bash
make validate-contract
```

To check that the new suffix is active, break one field in `text-extractor.help-ai.json` locally (for
example remove `tool`) and confirm that `make validate-contract` fails, then restore the file.

## Compatibility

Examples, validator mapping and README tables only. No schema change, no runtime behavior. Consumers
that read the example by its old path (`examples/help-ai-text-extractor.json`) must use the new name.
Rollback: revert the PR.
