# Issue #64: VIM contract: prescriptive layer changes from the shape test on real definition files

**Type**: Enhancement
**Priority**: Medium
**Status**: Open
**Created**: 2026-10-26
**GitHub**: #64
**Component**: contract
**Related**: #42, #51, portunix-architecture issue 008 (section *Shape Test on Definition Files*)

## Summary

The VIM prescriptive layer (VIM 2.1) was tested on two real definition files: a design
report (text, 18 pages, no numbered clauses) and a drawing (one sheet, bilingual legend).
Both files fit into the layer and validate. The test found four gaps in the contract. All
changes add an optional field or relax a constraint.

## Problem Description / Motivation

| ID | Gap | Today |
| --- | --- | ----- |
| R2 | A definition file refers to another document that it names only by a description, for example *the fire safety drawings of this project*. There is no designation | An `external` target must have `identifier`; a producer has to put the description into `identifier` |
| D1 | A definition file gives only the month or the year of issue | `Definition.issued` has `format: date`, so a producer has to invent a day |
| T1 | Many terms that provisions use are defined in a referenced document (a standard), not in the definition file | `Term` cannot point to the document that defines it |
| B1 | A drawing is bilingual; the verbatim `text` and the producer's `statement` can be in different languages | Nothing says which language the verbatim text is in |

The descriptions of `kind` and `Anchor` also follow rules that the specification changed
after the test:

- `kind` follows the **meaning** of a provision; the verbal form (shall, should, may) is
  only a hint. A design report or a drawing states most requirements without a modal verb
- An anchor always has `bbox`; `element_id` is written only when the element is not much
  larger than the provision (parser blocks often hold a whole page body)

## Scope

### In scope

1. `vim-prescriptive.schema.json`, `Reference`: an `external` target has `identifier`
   **or** `title` (`anyOf`), instead of `identifier` only
2. `vim-prescriptive.schema.json`, `Definition.issued`: pattern
   `^[0-9]{4}(-[0-9]{2}(-[0-9]{2})?)?$` instead of `format: date`
3. `vim-prescriptive.schema.json`, `Term`: optional `references` (array of
   `reference_id`), like `Provision.references`
4. `vim-prescriptive.schema.json`, `Definition`: optional `language` (BCP 47 tag, for
   example `cs`, `en`) — the language of the verbatim `text` in the layer
5. `scripts/vim-identifier-check.mjs`: every value of `Term.references` exists in the layer
6. Descriptions of `kind`, `Anchor.bbox` and `Anchor.element_id` follow the specification
7. Examples: one positive example that uses items 1–4; negative examples for a term
   reference to an unknown `reference_id` and for an external target without
   `identifier` and `title`
8. Schema `$version` 1.0.1 → 1.1.0, `CHANGELOG.md`

### Out of scope (follow-up issues)

- Java model in portunix-plugins (`vim-parser-api`): `Term.references`,
  `Definition.language`
- Python model in portunix-reco (`ptx_reco.vim`): the same two fields
- Items 1 and 2 need no model change: both models store `identifier`, `title` and
  `issued` as strings

## Open Question

`vim_version` stays `"2.1"`: the changes are additive or relax a constraint, and no
producer writes the layer yet. If a released document already uses 2.1, the alternative
is `"2.2"`.

## Acceptance Criteria

1. A document with an external target that has only `title` is valid
2. `issued` accepts `2008`, `2008-11` and `2008-11-01`, and rejects `11/2008`
3. A term with `references` to existing references is valid; a reference to an unknown
   `reference_id` fails the identifier check
4. `Definition.language` is accepted; a document without it is valid as before
5. All existing examples stay valid; the existing negative examples still fail
6. `make validate-contract` passes

## Testing

- `make validate-contract` with the new positive and negative examples
- Check that the official example `definition-file.vim.json` is unchanged and valid
