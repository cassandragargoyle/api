# Issue #72: Contract: Stability Field in Task Manifest (stable / preview)

**Type**: Enhancement
**Priority**: Low
**Status**: Open
**Created**: 2026-10-29
**GitHub**: #72
**Component**: contract
**Related**: portunix-reco-studio #92 (VIM form extraction marked as preview),
portunix-architecture ADR-011

## Summary

The task manifest has no field that marks a task as preview. This issue adds an optional
`stability` field to `task-manifest.schema.json` and to the `TaskManifest` and
`TaskManifestSummary` messages of `task-platform.proto`, so that clients can show or filter
preview tasks without reading free text.

## Problem Description / Motivation

portunix-reco-studio #92 marked the task `reco.pipeline.extract_form` as preview: its output
is not the production form result until a trigger of ADR-011 is confirmed. The contract gives
no way to say this, so the plugin writes "Preview:" at the start of `summary` and adds a
sentence to `when_not_to_use`. This has limits:

- A client that shows only `title` (menus, sidebars) does not see the note
- A client or an AI agent cannot filter out preview tasks without parsing text
- Each plugin can choose a different wording; nothing checks it
- `task-manifest.schema.json` has `additionalProperties: false`, so a plugin cannot add its own
  field

## Scope

### In scope

1. `contract/schemas/task-manifest.schema.json`: optional property `stability`, a string enum
   with the values `stable` and `preview`. A missing value means `stable`. The description
   says that a `preview` task can change its output without a major version and is not meant
   for production results
2. `contract/proto/task-platform.proto`: field `stability` (string, new field number) in
   `TaskManifest` and in `TaskManifestSummary`, so that `ListTasks` carries it too. An empty
   string means `stable`
3. Generated code and models for all language modules (`go`, `java`, `python`, `typescript`)
4. Minor version bump of the schema (`$version`) and an entry in `CHANGELOG.md`
5. An example manifest in `contract/examples/` with `"stability": "preview"`

### Out of scope (follow-up issues)

- A value `deprecated` and a replacement pointer (for example `superseded_by`); open
  question, see below
- Use of the field in plugins (portunix-reco-studio sets it for `reco.pipeline.extract_form`
  and can then shorten the text note in a separate issue)
- UI changes in clients (badge, filter)

## Open Questions

1. Is `deprecated` needed now, or only when the first task is deprecated?
2. Should the proto use an enum instead of a string? A string matches the JSON schema and the
   other text fields of the manifest; an enum is safer for clients

## Acceptance Criteria

1. A manifest with `"stability": "preview"` validates against `task-manifest.schema.json`; a
   manifest without the field still validates; any other value fails
2. All existing example manifests still validate (backward compatible)
3. `TaskManifest` and `TaskManifestSummary` in `task-platform.proto` have the field; generated
   code builds in all language modules
4. `CHANGELOG.md` and the schema version are updated

## Testing

- Schema validation tests: with `preview`, with `stable`, without the field, with an invalid
  value
- Validation of all files in `contract/examples/`
- Build and tests of each language module (`make test` or the module equivalent)
