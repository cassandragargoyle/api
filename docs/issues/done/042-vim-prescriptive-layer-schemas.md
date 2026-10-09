# Issue #42: VIM contract: schemas for the prescriptive layer

**Type**: Enhancement
**Priority**: Medium
**Status**: ✅ Implemented (PR #45, 2026-10-09)
**Created**: 2026-10-09
**GitHub**: #42
**Component**: contract
**Related**: portunix-architecture specification `docs/architecture/specifications/vim-prescriptive-layer.md`
(VIM Prescriptive Layer, VIM 2.1 — the binding source for this issue),
portunix-architecture ADR-010 (VIM Prescriptive Layer),
portunix-architecture issue 008 (VIM Prescriptive Layer, cross-project tracker),
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

The format is defined in the specification **VIM Prescriptive Layer (VIM 2.1)** in
portunix-architecture: `docs/architecture/specifications/vim-prescriptive-layer.md`. The
specification is a draft that is released for contract work. It is the binding source for
names, types, required fields, and enum values. When this issue and the specification
disagree, the specification wins.

```text
layers.prescriptive
├── definition      identity of the definition file
├── provenance      default provenance of all items
├── provisions[]    provision_id, kind, clause, text, statement, text_hash, anchors,
│                   condition, references, terms, relations, verification, status,
│                   confidence, provenance, revisions
├── references[]    reference_id, scope, binding, target, anchors
└── terms[]         term_id, term, definition, clause, anchors
```

The shape is not yet tested on real definition files. A change of the specification
before the first implementation is possible. Keep the schemas easy to change until the
model issues start.

## Scope

### In scope

- New schema `contract/schemas/vim-prescriptive.schema.json`: the layer, `Definition`,
  `Reference`, `Target`, `Term`
- New schema `contract/schemas/vim-prescriptive-provision.schema.json`: `Provision`,
  `Anchor`, `Relation`, `Verification`
- Change of `contract/schemas/vim-document.schema.json`:
  - property `prescriptive` in `layers`, as a `$ref` to the new schema, optional
  - value `"2.1"` in the `vim_version` enum
- Reuse of existing definitions by `$ref`: `Provenance` from
  `vim-extraction-source.schema.json`, `Revision` and `BoundingBox` from
  `vim-extraction-field.schema.json`
- New optional property `property` (string) on `Revision`
- Enum values:
  - `kind`: `requirement`, `recommendation`, `permission`
  - `status`: `proposed`, `confirmed`, `rejected`
  - `verification.method`: `automatic`, `test`, `manual`
  - `relation`: `refines`, `depends_on`, `exception_to`, `conflicts_with`
  - `scope`: `external`, `internal`
  - `binding`: `mandatory`, `informative`
  - `element_type`: `block`, `table`, `graphic`
- Rule "at least one of `text` and `statement`" on a provision
- `verification.rule` as an open object
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
4. `Provenance`, `Revision`, and `BoundingBox` are not defined a second time. The new
   schemas refer to the existing definitions.
5. Schema names, property names, and descriptions use neutral words. The layer is not
   bound to one kind of document. The input is called a definition file.
6. `contract/README.md` lists the new schemas.

## Testing

- Validate every file in `contract/examples/` against its schema.
- Negative test: a provision with an unknown `kind` does not validate.
- Negative test: a provision without `text` and without `statement` does not validate.
- Negative test: a provision without `anchors` does not validate.
- An existing example with a `Revision` without `property` still validates.
- Negative test: an unknown key in `layers` still does not validate.
