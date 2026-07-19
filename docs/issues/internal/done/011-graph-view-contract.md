# #011 — Graph View Contract: JSON Schema + File Extension for the 3D Node-Graph Viewer

**Status:** ✅ Implemented
**Priority:** Medium
**Type:** Feature
**Labels:** contract, schema, graph, visualization
**Created:** 2026-06-28
**Closed:** 2026-06-29

---

## Summary

Define a language-independent **contract** in `contract/` for the data consumed by
the node-graph viewer (the graphlens 3D force-directed viewer), and define a
dedicated **file-name suffix before `.json`** that marks a file as this graph
type. Two deliverables:

1. A **JSON Schema** (`contract/schemas/…`) for the `meta` + `nodes` + `links` document.
2. A **filename convention** (a token before `.json`) so tools, editors, and
   viewers can recognize the file as this graph type without inspecting its
   contents.

This contract is the single source of truth, consumed by:

- **portunix-plugins #086** — the `graphlens` plugin (`ptx-graphlens`), whose
  `viewer.py` serves the file and whose `model.py` builds it.
- **portunix-vscode #050** — the `GraphLens3D` React component that renders it.
- Any caller that builds the graph (e.g. `ptx-pft`, portunix #190).

## Motivation

The viewer is domain-agnostic: it renders any conforming document regardless of
origin. Today the shape lives informally in the plugin's `model.py` and in the
hand-written `app.js`. Promoting it to an Api contract gives every language
(Python plugin, TypeScript component, future Go/Java callers) one validated,
versioned definition — exactly as done for the VIM and plugin-platform schemas
(see #009).

## Distinct from the existing `*.graph.json` (2D)

portunix-vscode already has a 2D Graph Canvas format — `GraphFile` with
**`nodes` + `edges` + `metadata`** (Cytoscape, see portunix-vscode #030,
`src/shared/types/graphFile.ts`, files named `*.graph.json`).

This contract is **different**: a 3D force-directed graph with
**`nodes` + `links` + `meta`** (`links`, not `edges`; `meta`, not `metadata`).
It therefore needs its **own schema and its own file extension** — it must not
reuse `*.graph.json`.

## Deliverable 1 — JSON Schema

Add `contract/schemas/graph-view.schema.json` (JSON Schema Draft 2020-12, with
`$id` `https://cassandragargoyle.org/schemas/graph-view.schema.json` and
`$version`), plus an example under `contract/examples/`, and a row in `contract/README.md`.

Document shape:

```json
{
  "meta": { "title": "…" },
  "nodes": [
    { "id": "tag:hr", "type": "tag", "label": "hr" },
    { "id": "VC-V015", "type": "voice", "voice": "VoC", "label": "VC-V015",
      "title": "…", "description": "…", "meta": { } }
  ],
  "links": [
    { "source": "tag:hr", "target": "VC-V015", "description": "…", "rel": "related" }
  ]
}
```

Field definitions (to formalize in the schema):

| Object | Field | Req | Type | Notes |
| ------ | ----- | --- | ---- | ----- |
| root | `meta` | no | object | viewer metadata; `title` (string) is the known key, others allowed |
| root | `nodes` | yes | array | node list |
| root | `links` | yes | array | edge list referencing node ids |
| node | `id` | yes | string | unique node id |
| node | `type` | yes | string | node kind (e.g. `tag`, `voice`); drives styling |
| node | `label` | yes | string | display label |
| node | `voice` | no | string | voice category (voice nodes) |
| node | `title` | no | string | detail-panel title |
| node | `description` | no | string | detail-panel body |
| node | `meta` | no | object | free-form extra metadata for the detail panel |
| link | `source` | yes | string | source node id |
| link | `target` | yes | string | target node id |
| link | `description` | no | string | optional edge description |
| link | `rel` | no | string | relationship category for styling (e.g. `related`) |

Open schema questions to resolve during implementation:

- `additionalProperties` policy per object (the viewer ignores unknown fields;
  recommend permissive at node/link level via `node.meta`, but consider
  `additionalProperties: false` on the typed envelope for early validation).
- Whether `meta.title` should be `required` (recommend optional).
- Optional top-level `version` field for the document format itself.

## Deliverable 2 — File extension convention

Define a token placed **before `.json`** that identifies the file as a graph-view document, e.g. `voices.glens.json`.

**Recommended:** `*.glens.json` — short, tied to graphlens / `ptx-graphlens`,
and unambiguous against the existing `*.graph.json` (2D) and `*.canvas.json`.

Alternatives to consider: `*.graphlens.json`, `*.graph3d.json`,
`*.fgraph.json`. **Decision to confirm** before the schema's examples and the
consumers' file associations are wired up.

Once chosen, the token should be:

- referenced in the schema description / `contract/README.md`,
- used by portunix-plugins #086 example data and docs,
- used by portunix-vscode #050 for the custom-editor / file association.

## Acceptance Criteria

- [ ] `contract/schemas/graph-view.schema.json` added (Draft 2020-12, `$id`, `$version`, full `meta`/`nodes`/`links` definition).
- [ ] At least one example in `contract/examples/` (e.g. a small voices graph) that validates against the schema.
- [ ] `contract/README.md` lists the new schema (and example).
- [ ] File-extension token chosen and documented (recommended `*.glens.json`).
- [ ] Contract explicitly distinguished from the 2D `*.graph.json` / `GraphFile` format.
- [ ] Cross-references recorded to portunix-plugins #086 and portunix-vscode #050.

## Related

- Api #009 — Plugin Platform Contract Schemas (schema layout / conventions)
- portunix-plugins #086 — `graphlens` plugin (consumer; `model.py` builds it, `viewer.py` serves it)
- portunix-vscode #050 — `GraphLens3D` React component (consumer; renderer)
- portunix-vscode #030 — 2D Graph Canvas `*.graph.json` (the format this one must stay distinct from)
- portunix #190 — `ptx-pft` (first caller that builds a graph-view file)
