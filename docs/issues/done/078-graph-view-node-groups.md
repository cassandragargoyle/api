# Issue #78: Contract: node groups with colors in graph-view (node.group, meta.groups)

**Type**: Enhancement
**Priority**: Medium
**Status**: ✅ Implemented (PR #80, 2026-10-31)
**Created**: 2026-10-31
**Closed**: 2026-10-31
**GitHub**: #78
**Component**: contract
**Related**: react-components #7, portunix-vscode #140, portunix-plugins #210

## Summary

The GraphView contract has no way to say how nodes are grouped or colored. Colors are hard-coded in the
GraphLens3D component for `tag` and `voice` nodes, and any other node type is grey. This issue adds optional group
definitions to `graph-view.schema.json`. It is an optional extension: no document, writer, or reader has to use it.

## Problem

`portunix-architecture` generates `docs/manifests/diagrams/manifests-by-language.glens.json` with the node types
`language` and `manifest`. The viewer shows all 110 nodes in the same grey, and its filter puts them in one group.

The schema does not allow a fix in the document: nodes have `additionalProperties: false`, and the viewer does not
read colors from `meta` or `node.meta`.

## Proposal

Add one optional node field and define one optional `meta` key. No field becomes required, and nothing else in the
schema changes. Schema `$version` goes from 1.0.0 to 1.1.0.

| Field | Type | Required | Description |
| ----- | ---- | -------- | ----------- |
| `node.group` | string | no | Group key of the node, not empty. When missing, readers use their default grouping |
| `meta.groups` | object | no | Map of group key to group definition |
| `meta.groups.<key>.label` | string | no | Legend and filter label. When missing, the key is shown |
| `meta.groups.<key>.color` | string | no | Color as `#RRGGBB`, 7 characters. When missing, readers use their default color |

```json
{
  "meta": {
    "title": "Software manifests by programming language",
    "groups": {
      "language": { "label": "Programming language", "color": "#d29922" },
      "manifest": { "label": "Manifest", "color": "#58a6ff" }
    }
  },
  "nodes": [
    { "id": "language:c", "type": "language", "label": "C", "group": "language" }
  ],
  "links": []
}
```

`group` is separate from `type` because `type` says what a node is, while a group can follow any property. A later
diagram can color manifests by category without changing their type.

## Compatibility

This is an optional extension. Nothing in the contract requires the new fields.

- Documents without groups stay valid and look the same
- Writers can leave out groups, and readers can ignore them and use their default styling
- A writer that uses `node.group` needs readers with schema 1.1.0, because schema 1.0.0 rejects the field
- A group key in `node.group` that is not in `meta.groups` is valid and uses default styling
- `meta.groups` becomes a defined key. `meta` still allows other keys, but a document that already uses `groups` for
  other data must rename it. No `*.glens.json` document in this repository does this

## Scope

- Update `contract/schemas/graph-view.schema.json`
- Add a valid example with groups in `contract/examples/`, and negative examples for a wrong color format, a line end
  in a color, an unknown group key, and an empty `node.group`
- Update the graph-view row in `contract/README.md` and `docs/GLOSSARY.md` if needed

Out of scope: per-node colors, link colors, and automatic palettes.

## Acceptance Criteria

- The new example passes `make validate-contract`, and the negative examples fail it
- All existing `*.glens.json` examples still pass without changes

## Follow-up

Implementation order: this issue, react-components #7, portunix-vscode #140, portunix-plugins #210. Then the
`manifest-diagrams` generator in `portunix-architecture` starts to write groups. The contract change does not depend
on this follow-up work.
