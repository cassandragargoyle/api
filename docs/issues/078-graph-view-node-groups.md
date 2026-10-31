# Issue #78: Contract: node groups with colors in graph-view (node.group, meta.groups)

**Type**: Enhancement
**Priority**: Medium
**Status**: Open
**Created**: 2026-10-31
**GitHub**: #78
**Component**: contract
**Related**: react-components #7, portunix-vscode #140, portunix-plugins #210

## Summary

The GraphView contract has no way to say how nodes are grouped or colored. Colors are hard-coded in the
GraphLens3D component for `tag` and `voice` nodes, and any other node type is grey. This issue adds optional,
backward-compatible group definitions to `graph-view.schema.json`.

## Problem

`portunix-architecture` generates `docs/manifests/diagrams/manifests-by-language.glens.json` with the node types
`language` and `manifest`. The viewer shows all 110 nodes in the same grey, and its filter puts them in one group.

The schema does not allow a fix in the document: nodes have `additionalProperties: false`, and the viewer does not
read colors from `meta` or `node.meta`.

## Proposal

Add one optional node field and define one optional `meta` key. Schema `$version` goes from 1.0.0 to 1.1.0.

| Field | Type | Required | Description |
| ----- | ---- | -------- | ----------- |
| `node.group` | string | no | Group key of the node. When missing, readers use their default grouping |
| `meta.groups` | object | no | Map of group key to group definition |
| `meta.groups.<key>.label` | string | no | Legend and filter label. When missing, the key is shown |
| `meta.groups.<key>.color` | string | no | Color as `#RRGGBB`. When missing, readers use their default color |

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

- Existing documents stay valid and look the same
- A document that uses `node.group` fails validation against schema 1.0.0, so readers with strict validation must
  update
- A group key in `node.group` that is not in `meta.groups` is valid and uses default styling

## Scope

- Update `contract/schemas/graph-view.schema.json`
- Add a valid example with groups in `contract/examples/`, and a negative example with a wrong color format
- Update the graph-view row in `contract/README.md` and `docs/GLOSSARY.md` if needed

Out of scope: per-node colors, link colors, and automatic palettes.

## Acceptance Criteria

- The new example passes `make validate-contract`, and the negative example fails it
- All existing `*.glens.json` examples still pass

## Follow-up

Implementation order: this issue, react-components #7, portunix-vscode #140, portunix-plugins #210. Then the
`manifest-diagrams` generator in `portunix-architecture` starts to write groups.
