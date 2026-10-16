# Issue #51: VIM contract: bind layers to vim_version and check identifiers of the prescriptive layer

**Type**: Enhancement
**Priority**: Low
**Status**: ✅ Implemented (PR #54, 2026-10-16)
**Created**: 2026-10-11
**Closed**: 2026-10-16
**GitHub**: #51
**Component**: contract
**Related**: #42 (schemas for the prescriptive layer),
cassandragargoyle/portunix-reco#62 (Python model, acceptance protocol
`docs/testing/acceptance-062.md`, findings 5 and 6),
cassandragargoyle/portunix-plugins#159 (Java model),
portunix-architecture specification `docs/architecture/specifications/vim-prescriptive-layer.md`
(VIM Prescriptive Layer, VIM 2.1)

## Summary

The VIM document schema accepts two kinds of documents that the specification does not
allow. A document can carry the `prescriptive` or the `extraction` layer together with an
older `vim_version`. A `prescriptive` layer can have duplicate identifiers and links to
items that do not exist. Both gaps were found in the acceptance test of
cassandragargoyle/portunix-reco#62.

## Problem Description / Motivation

The contract schemas are the binding check for every producer of VIM documents. A producer
that passes the schema check today can still write a document that a consumer cannot use
correctly.

### Part 1: The layers are not bound to `vim_version`

The specification says in section 2: "A producer that writes the `prescriptive` layer sets
`vim_version` to `"2.1"`." Section 6 lists the layers of every version: `extraction` since
2.0, `prescriptive` since 2.1.

`contract/schemas/vim-document.schema.json` defines `vim_version` as an enum of all
versions and has no rule that connects a layer with a version. The Java and the Python
model keep their default version `"1.5"` and do not change it when a layer is set, so a
producer that forgets the version writes such a document.

Result of a check with the examples of this repository, with only `vim_version` changed:

| Example | `vim_version` | Schema result |
| ------- | ------------- | ------------- |
| `contract/examples/definition-file.vim.json` (has `prescriptive`) | `"1.0"`, `"1.5"`, `"2.0"` | valid |
| `contract/examples/form-extraction.vim.json` (has `extraction`) | `"1.0"`, `"1.5"` | valid |

A consumer that selects its reader by `vim_version` ignores the layer in such a document.

### Part 2: Identifiers of the prescriptive layer are not checked

The specification says that `provision_id`, `reference_id`, and `term_id` are unique in
the layer. A provision links to other items by these identifiers:

- `provisions[].references[]` holds `reference_id` values
- `provisions[].terms[]` holds `term_id` values
- `provisions[].relations[].provision_id` holds a `provision_id`

The schema accepts all of these documents, made from
`contract/examples/definition-file.vim.json`:

| Change of the example | Schema result |
| --------------------- | ------------- |
| Two provisions with the same `provision_id` | valid |
| Two terms with the same `term_id`, two references with the same `reference_id` | valid |
| A provision with a `terms` value, a `references` value, and a relation `provision_id` that no item has | valid |

JSON Schema Draft 2020-12 cannot express these rules. `uniqueItems` compares whole items,
not one property, and no keyword checks a value against the values of another array. The
rules need a second check next to the schema, or a clear statement that they are not part
of the contract check.

## Scope

### In scope

- Part 1: a rule in `vim-document.schema.json` (`if` / `then`) that requires
  `vim_version` `"2.1"` when `layers.prescriptive` is present, and `"2.0"` or `"2.1"` when
  `layers.extraction` is present
- Part 1: negative examples in `contract/examples/invalid/` for both layers
- Part 2: a decision how the identifier rules are checked. Options:
    - a consistency check in the contract validation of this repository
      (`make validate-contract`), which other projects can reuse
    - a documented rule only, with the check left to every producer and consumer
- Part 2: the chosen check, with negative examples for a duplicate identifier and for a
  link without a target
- A note in `contract/README.md` about what the schema checks and what the second check
  covers
- A `$version` change of the changed schemas, and a `CHANGELOG.md` entry

### Out of scope (follow-up issues)

- Changes in the Java model (portunix-plugins) and in the Python model (portunix-reco).
  They need their own issues when the contract changes
- A check that a `reference` with the scope `internal` points to a clause that exists in
  the same file
- Identifier rules of the `extraction` layer (`source_id`, `field_id`)

## Acceptance Criteria

1. A document with `layers.prescriptive` and a `vim_version` other than `"2.1"` is
   rejected by `vim-document.schema.json`.
2. A document with `layers.extraction` and a `vim_version` lower than `"2.0"` is rejected.
3. A document without these two layers validates with every listed `vim_version`, as
   before. All existing valid examples stay valid.
4. The identifier rules of part 2 have a documented decision. When a check is chosen, it
   rejects a duplicate `provision_id`, `reference_id`, and `term_id`, and a link in
   `references`, `terms`, or `relations` without a target.
5. `make validate-contract` runs the new negative examples and passes.
6. The golden fixtures of the Java model and of the Python model still validate. Both use
   `vim_version` `"2.1"` with the `prescriptive` layer and `"2.0"` with the `extraction`
   layer.

## Testing

- Negative examples in `contract/examples/invalid/`, in the style of the existing files
  (`anchor-page-zero.vim.json` and others): one file per rejected case.
- Positive check: `definition-file.vim.json` and `form-extraction.vim.json` validate
  unchanged.
- Cross-project check: validate
  `vim-parser-api/src/test/resources/fixtures/prescriptive-layer-v2.1.vim.json` and
  `extraction-layer-v2.vim.json` from portunix-plugins against the changed schemas.
- Before the schema change, search the consumer projects for VIM documents that carry the
  `extraction` layer with an older `vim_version`. Part 1 turns them into invalid
  documents.
