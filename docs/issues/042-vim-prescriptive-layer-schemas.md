# Issue #42: VIM contract: schemas for the prescriptive layer

**Type**: Enhancement
**Priority**: Medium
**Status**: 📋 Open (blocked by the format specification)
**Created**: 2026-10-09
**GitHub**: #42
**Component**: contract
**Related**: portunix-architecture issue 008 (VIM Prescriptive Layer, cross-project tracker),
portunix-architecture ADR-009 (Criteria for Adding a New VIM Layer),
portunix-architecture ADR-004 (VIM as Universal Result Container),
cassandragargoyle/portunix-plugins#159 (Java model),
cassandragargoyle/portunix-reco#62 (Python model)

## Summary

Add JSON Schema contracts for a new optional VIM layer `prescriptive`. The layer stores
what a definition file prescribes: provisions (requirements, recommendations, permissions),
references to other documents, and defined terms. The schemas are the binding contract for
the Java and the Python implementation of VIM.

## Problem Description / Motivation

A **definition file** is a document that prescribes something, for example a
specification, a guideline, or a contract. VIM can store its content today, but not what
it prescribes.

The `layers` object in `contract/schemas/vim-document.schema.json` has
`additionalProperties: false`. No producer can add the new layer without a contract
change. This issue is that change.

The format specification of the layer is written in portunix-architecture (issue 008,
Phase 1). **This issue is blocked until that specification is accepted.** The shape below
is the current draft and can change.

```text
layers.prescriptive
├── provisions[]    kind, clause, text, anchors, conditions, verification,
│                   provenance, confidence, revisions
├── references[]    target document, edition, kind of reference
└── terms[]         defined terms that provisions use
```

## Scope

### In scope

- New schema `contract/schemas/vim-prescriptive.schema.json` for the layer
- New schema `contract/schemas/vim-prescriptive-provision.schema.json` for one provision,
  including `verification`, `provenance`, and `revisions`
- Change of `contract/schemas/vim-document.schema.json`:
  - property `prescriptive` in `layers`, as a `$ref` to the new schema, optional
  - value `"2.1"` in the `vim_version` enum
- Enum values: `kind` (`requirement`, `recommendation`, `permission`),
  `verification.method` (`automatic`, `test`, `manual`)
- At least one valid example document in `contract/examples/`
- Update of `contract/README.md`

### Out of scope (follow-up issues)

- Model classes in any language
- A schema for the assessment document (result of a compliance check)
- A schema for derived software requirements

## Acceptance Criteria

1. A VIM document without the `prescriptive` layer validates as before. No existing
   example changes.
2. The example document with the `prescriptive` layer validates against
   `vim-document.schema.json`.
3. The new schemas follow the style of `vim-extraction*.schema.json`: the same `$id`
   pattern, `title`, `description` on every property.
4. Where the layer needs provenance or revisions, the schemas reuse the shape of the
   `extraction` layer, or the description says why the shape differs.
5. Schema names, property names, and descriptions use neutral words. The layer is not
   bound to one kind of document. The input is called a definition file.
6. `contract/README.md` lists the new schemas.

## Testing

- Validate every file in `contract/examples/` against its schema.
- Negative test: a provision with an unknown `kind` does not validate.
- Negative test: an unknown key in `layers` still does not validate.
