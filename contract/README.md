# Contract - Unified AI Plugin & Task Platform

Language-independent contracts for the Unified AI Plugin Platform and Unified AI Task Platform.

## Purpose

This directory contains JSON Schema definitions that define the communication contracts between plugins and the platform. All plugins (Java, Python, Go) must conform to these schemas.

## Schemas

### Task Platform

| Schema | Description |
| ------ | ----------- |
| [task-request.schema.json](schemas/task-request.schema.json) | Unified task request format |
| [task-response.schema.json](schemas/task-response.schema.json) | Unified task response format |
| [error.schema.json](schemas/error.schema.json) | Structured error model |
| [task-manifest.schema.json](schemas/task-manifest.schema.json) | Plugin task capability manifest |
| [ocr-result.schema.json](schemas/ocr-result.schema.json) | Standardized OCR result with text positioning |

### VIM (Versatile Information Model)

| Schema | Description |
| ------ | ----------- |
| [vim-document.schema.json](schemas/vim-document.schema.json) | VIM v2.0 root document structure |
| [vim-extraction.schema.json](schemas/vim-extraction.schema.json) | Extraction layer (template ref, sources, merged document) |
| [vim-extraction-source.schema.json](schemas/vim-extraction-source.schema.json) | Per-source extraction result with provenance |
| [vim-extraction-field.schema.json](schemas/vim-extraction-field.schema.json) | Extracted field, table, cell definitions |

### Plugin Platform

| Schema | Description |
| ------ | ----------- |
| [plugin-manifest.schema.json](schemas/plugin-manifest.schema.json) | Plugin registration manifest (identity, runtime, commands, AI integration) |
| [plugin-permissions.schema.json](schemas/plugin-permissions.schema.json) | Plugin permission model (filesystem, network, database, system) |
| [plugin-health.schema.json](schemas/plugin-health.schema.json) | Plugin health check response (status, metrics, sub-checks) |
| [help-ai.schema.json](schemas/help-ai.schema.json) | Machine-readable `--help-ai` output (tool identity, commands, parameters, optional per-command `supported_extensions` / `supported_mime_types`) |

### Visualization

| Schema | Description |
| ------ | ----------- |
| [graph-view.schema.json](schemas/graph-view.schema.json) | 3D force-directed node-graph document for the graphlens viewer (`meta` + `nodes` + `links`) |

Graph-view documents use the **`*.glens.json`** filename suffix (tied to graphlens / `ptx-graphlens`) so tools, editors, and viewers recognize the type without inspecting contents.

> **Distinct from the 2D `*.graph.json` format.** The 2D Graph Canvas format (`GraphFile`: `nodes` + `edges` + `metadata`, Cytoscape, portunix-vscode #030) is a different contract. This 3D graph-view format uses `links` (not `edges`) and `meta` (not `metadata`), and must not reuse the `*.graph.json` suffix.

### Transcription

| Schema | Description |
| ------ | ----------- |
| [transcript.schema.json](schemas/transcript.schema.json) | Video/audio transcript working document for the scribe correction backend (`media` + `speakers` + `segments` + `flags`) |

Transcript documents use the **`*.scribe.json`** filename suffix (tied to scribe / `ptx-scribe`, portunix-plugins #113) so tools, editors, and the future VS Code transcript editor recognize the type without inspecting contents.

> **Provisional.** The shape may still change once reconciled against a real transcript sample (portunix-plugins #113, criterion 15); `schema_version` tracks that evolution. The transcript Markdown (front matter with `source` + `[mm:ss]` segments) is a lighter, related convention shared with `vox` and is not (yet) a formal schema here.

### Modeling

Building model of the Portunix Modeler (`ptx-modeler`, portunix-plugins #116): a flat entity
store (storeys, wall graph of junctions and walls, doors/windows, rooms, electrical devices,
imported underlay/annotations, title block) with layers and an operation log. The model is
authoritative; SVG/glTF drawings are derived views. Coordinates are integer millimetres.

| Schema | Description |
| ------ | ----------- |
| [modeler-model.schema.json](schemas/modeler-model.schema.json) | Model document (`schema: portunix.modeler/v1`): `layers` + `entities` (`id`, `kind`, `parent`, `layer`, `props`) + `log` |
| [modeler-ops.schema.json](schemas/modeler-ops.schema.json) | Operations (`op apply`): JSON array applied as one transaction; payloads reuse the entity props of the model schema |
| [modeler-symbol.schema.json](schemas/modeler-symbol.schema.json) | Symbol element (`format: portunix.modeler.symbol/v1`, portunix-plugins #124): metadata, embedded SVG fragment (safe subset), terminals, text fields, standard compliance (`compliant`/`derived`/`custom`/`vendor`, e.g. IEC 60617) and provenance in `meta` (`author`, `license` as SPDX id, `licenseUrl`, `source`, `sourceUrl`, `attribution`). 1.2.0 (portunix-plugins #128): pinned `origin` (`type` git/file, `url`, `revision`, `path`, `format`, `sha256`) and opaque `extensions` keyed by namespace (e.g. `qet` for lossless QElectroTech round trips) |
| [modeler-symbol-source.schema.json](schemas/modeler-symbol-source.schema.json) | Symbol source (`format: portunix.modeler.symbol-source/v1`, portunix-plugins #130): one external symbol collection of the ptx-modeler source registry (e.g. QElectroTech elements, KiCad libraries) with repository, verified license facts (SPDX, usage, restrictions such as `no-ml-training`), formats, domains, standards, searchable categories, index strategy and tracking issue. The support status is computed by the plugin, never stored |
| [modeler-symbol-discovery.schema.json](schemas/modeler-symbol-discovery.schema.json) | JSON output of the symbol discovery (`ptx-modeler symbol search / sources / index / source probe\|add\|report -f json`, portunix-plugins #130) and of the `ModelerSymbolService` discovery RPCs (byte-identical); `kind` selects search, probe, issue-draft, sources, source, index, source-added or symbol-ready (`symbol search --use N`). Results carry availability levels and complete next-step actions for AI agents |

Model documents use the **`*.model.json`** filename suffix and operation files the
**`*.ops.json`** suffix, so tools, editors and AI agents recognize them without inspecting contents.
Symbol elements use the **`*.msym.json`** suffix (one self-contained file per symbol at
`symbols/<category>/<name>.msym.json` inside a symbol pack), so the VS Code viewer can open them directly.
Symbol sources use the **`*.msrc.json`** suffix (`<id>.msrc.json`) and discovery outputs saved to
files the **`*.symbol-discovery.json`** suffix.
Their `meta` object carries the provenance of the drawing, so symbols converted from other
collections keep their attribution when edited or redistributed:

| `meta` field | Meaning |
| ------------ | ------- |
| `author` | Author(s) of the drawing, e.g. `The QElectroTech team` |
| `license` | SPDX license identifier, e.g. `MIT`, `CC-BY-3.0` (`LicenseRef-<name>` when there is none) |
| `licenseUrl` | http(s) link to the license text or the collection's license terms |
| `source` | Human-readable origin, e.g. collection and original file path |
| `sourceUrl` | http(s) link to the original symbol or collection |
| `attribution` | Notice to show when redistributing, as required by attribution licenses (CC BY) |

All fields are optional; further keys are allowed as free strings.

> **Single source of truth.** The plugin embeds identical copies of all modeler schemas and of
> `help-ai.schema.json` (`ptx-modeler schema [--ops | --symbol | --source | --discovery]`); a plugin
> test fails when they drift from this directory.

### Opportunity Management (v2)

Two-spine model authored by `ptx-pft` and read by Pilot (ADR-008, issue #015 —
**supersedes** the v1 set in #014). Discovery: Venture → Initiative → Idea.
Delivery: Team/Project → Epic → Use-Case. See
[schemas/README.md](schemas/README.md) for the storage model, linking principle,
and design decisions.

| Schema | Description |
| ------ | ----------- |
| [venture.schema.json](schemas/venture.schema.json) | Neutral `.venture` container (supersedes `discovery.schema.json`) |
| [initiative.schema.json](schemas/initiative.schema.json) | Strategic intent; `ideaRefs[]` (M:N) |
| [opportunity.schema.json](schemas/opportunity.schema.json) | Idea (demand asset); coarse `complexity` replaces story points |
| [use-case.schema.json](schemas/use-case.schema.json) | Delivery unit; `storyPoints`, `productRef`, `implementsIdeaRefs[]`, product-bound `solution`, contract terms (`contractRef`, `deliverables`, `acceptanceCriteria`, `outOfScope`, `assumptions`) |
| [product.schema.json](schemas/product.schema.json) | Product registered inside the venture; optional `vendorRef` to the owning organization |
| [team.schema.json](schemas/team.schema.json) | Team/Project registry; `kind`, optional `productRefs[]` |
| [backlog.schema.json](schemas/backlog.schema.json) | First-class backlog; `kind`, optional `teamRef`, membership + relations |
| [epic.schema.json](schemas/epic.schema.json) | Delivery grouping; `backlogRef`, `initiativeRefs[]`, `useCaseRefs[]` |
| [estimation.schema.json](schemas/estimation.schema.json) | Planning-poker history attached to a use-case (`subjectRef`) |
| [discussion.schema.json](schemas/discussion.schema.json) | General thread attachable to any entity (`subjectRef` + `subjectKind`) |

Each backlog's graph projection **reuses** `graph-view.schema.json`
(`*.glens.json`); the `implements` (use-case → idea) edge needs no schema change.

### Competency Model

Machine-readable contract for the AI competency model — a single source of truth
that projects into generated views (coverage map, gap analysis, bus-factor, role
staffing). Two core node types (**CompetencyArea**, **Competency**) stay stable;
people, resources, sectors and evidence attach through relations. Two independent
taxonomies: `area:*` (**WHAT** the organization can do) and `sector:*`
(**WHERE** it is applied). A competency describes *how* work is delivered; *what*
is delivered to a customer lives in the separate Capability Registry.

| Schema | Description |
| ------ | ----------- |
| [competency.schema.json](schemas/competency.schema.json) | Competency node — a concrete professional capability (`competency:*`) |
| [competency-area.schema.json](schemas/competency-area.schema.json) | CompetencyArea node — classification grouping competencies (`area:*`) |
| [competency-person.schema.json](schemas/competency-person.schema.json) | Person node — bearer of a competency (`person:*`), optional external registry `ref` |
| [competency-resource.schema.json](schemas/competency-resource.schema.json) | Resource node — technology/tool/framework/method/standard behind a competency (`resource:*`) |
| [competency-sector.schema.json](schemas/competency-sector.schema.json) | Sector node — field of application (`sector:*`) |
| [competency-evidence.schema.json](schemas/competency-evidence.schema.json) | Evidence node — project/reference/certification/repository/methodology/benchmark (`evidence:*`) |
| [competency-assessment.schema.json](schemas/competency-assessment.schema.json) | Assessment — proficiency (0-5) + AI enablement; inline on `has_competency` or a standalone node (`assessment:*`) |
| [competency-maturity.schema.json](schemas/competency-maturity.schema.json) | Maturity node — **derived** organizational maturity, computed from evidence + assessments (`maturity:*`) |
| [competency-verification.schema.json](schemas/competency-verification.schema.json) | Shared verification block (`status`, `source`, `lastChecked`, `evidence`) attached to every node and edge |
| [competency-relations.schema.json](schemas/competency-relations.schema.json) | Relations file — typed edges (`classified_in`, `owned_by`, `has_competency`, `backed_by`, `applied_in`, `evidenced_by`) |

Node ids follow the grammar `<prefix>:<slug>` where the slug uses dot-separated
levels of `[a-z0-9-]` (e.g. `competency:rag-knowledge-retrieval`). Edge-specific
fields are constrained by the relations schema: `has_competency` requires an
`assessment`, and `primary` is only valid on `classified_in`.

### People Registry

Layered people model that keeps master data in one place and lets projects
reference it instead of copying. Master data is never edited twice: per-project
layers carry only a `ref` plus project-specific fields. The company-wide registry
is a **separate** firm-scoped registry — project layers do **not** `ref` it; a
person present in both is keyed by the same id.

| Schema | Layer | Description |
| ------ | ----- | ----------- |
| [people-registry.schema.json](schemas/people-registry.schema.json) | 1 | Delivery-project shared registry (`projects/users.json`); master record per person, the `ref` target for layers 2/3 |
| [project-participants.schema.json](schemas/project-participants.schema.json) | 2 | Who is on a concrete project (`projects/<slug>/users.json`); `ref` + `projectRole`, optional `override` |
| [project-contacts.schema.json](schemas/project-contacts.schema.json) | 3 | Pending-contacts "waiting room" (`projects/<slug>/contacts.json`); `ref` + note, promoted to layer 2 once the role is clear |
| [company-people-registry.schema.json](schemas/company-people-registry.schema.json) | — | Firm-wide registry (root `users.json`); superset of the master fields plus `relation`; presence = active cooperation |

## Proto

gRPC service contracts. These are the canonical definitions — servers and clients vendor a copy
into their own tree rather than hand-writing the wire format, and re-sync when the contract moves.

| Proto | Service | Description |
| ----- | ------- | ----------- |
| [task-platform.proto](proto/task-platform.proto) | `portunix.platform.v1.TaskPlatformService` | Universal task discovery and execution; mirrors `task-manifest` / `task-request` / `task-response` schemas |
| [scribe.proto](proto/scribe.proto) | `portunix.scribe.ScribeService` | Transcript correction backend (segments, speakers, flags, Markdown export); mirrors [transcript.schema.json](schemas/transcript.schema.json) |
| [modeler.proto](proto/modeler.proto) | `portunix.modeler.ModelerSymbolService` | Portunix Modeler symbol conversions (portunix-plugins #128): QElectroTech `.elmt` ↔ `*.msym.json` for single elements (bytes) and collections/packs (server paths), with the CLI's conversion warnings; symbols validate against [modeler-symbol.schema.json](schemas/modeler-symbol.schema.json). Symbol source discovery (portunix-plugins #130): `SearchSymbols`, `ListSources`, `GetSource`, `ProbeSource`, `DraftSourceIssue` return the CLI's JSON documents ([modeler-symbol-discovery.schema.json](schemas/modeler-symbol-discovery.schema.json)); no RPC creates issues |

## Examples

### Task Platform Examples

| Example | Description |
| ------- | ----------- |
| [image-resize-request.json](examples/image-resize-request.json) | Image resize task request |
| [response-success.json](examples/response-success.json) | Successful task response |
| [response-error.json](examples/response-error.json) | Error task response |
| [task-manifest-image-resize.json](examples/task-manifest-image-resize.json) | Task manifest for image resize plugin |

### Plugin Platform Examples

| Example | Description |
| ------- | ----------- |
| [plugin-manifest-reco.json](examples/plugin-manifest-reco.json) | Plugin manifest for reco (Python, helper) |
| [plugin-manifest-text-extractor.json](examples/plugin-manifest-text-extractor.json) | Plugin manifest for text-extractor (Java, MCP) |
| [plugin-health-serving.json](examples/plugin-health-serving.json) | Healthy plugin with sub-checks |
| [help-ai-text-extractor.json](examples/help-ai-text-extractor.json) | `--help-ai` output for text-extractor (with `supported_extensions` / `supported_mime_types`) |

### Visualization Examples

| Example | Description |
| ------- | ----------- |
| [voices.glens.json](examples/voices.glens.json) | Small voices graph for the graphlens 3D viewer |

### Modeling Examples

| Example | Description |
| ------- | ----------- |
| [house.ops.json](examples/house.ops.json) | Operations creating a sample house: 4 rooms, wall graph with L/T/X joins, doors, windows, host-relative sockets/switches and ceiling lights. Validated by `make validate-contract`. |
| [house.model.json](examples/house.model.json) | The model produced by `ptx-modeler init` + `op apply` of `house.ops.json` (incl. operation log). Validated by `make validate-contract`. |
| [socket-single.msym.json](examples/socket-single.msym.json) | Symbol element derived from IEC 60617-11 (single socket outlet) with a terminal and a label text field. Validated by `make validate-contract`. |
| [sensor-temperature.msym.json](examples/sensor-temperature.msym.json) | `custom` symbol element (no standard symbol referenced), as in the ptx-modeler core pack. Validated by `make validate-contract`. |
| [smart-relay-qet.msym.json](examples/smart-relay-qet.msym.json) | Symbol imported from a QElectroTech `.elmt` element (`ptx-modeler symbol import-elmt`): `data-qet-*` SVG attributes, `extensions.qet` (link type, kind/element informations, terminal and dynamic text data) and a pinned `origin`. Validated by `make validate-contract`. |
| [qelectrotech-elements.msrc.json](examples/qelectrotech-elements.msrc.json) | Curated symbol source of the ptx-modeler registry: QElectroTech elements (CC BY 3.0 with `no-ml-training`, license verified 2026-09-27), IEC 60617 path and localized categories. Validated by `make validate-contract`. |
| [kicad-symbols.msrc.json](examples/kicad-symbols.msrc.json) | Curated symbol source: KiCad schematic symbol libraries on GitLab (CC BY-SA 4.0 with the design exception), 223 libraries as categories, tracking issue for the `kicad_sym` converter. Validated by `make validate-contract`. |
| [search-en-60617.symbol-discovery.json](examples/search-en-60617.symbol-discovery.json) | `ptx-modeler symbol search -f json "EN 60617 07-02-01"`: a `remote-source` result with license-review, fetch, index, convert and use actions. Validated by `make validate-contract`. |
| [probe-kicad.symbol-discovery.json](examples/probe-kicad.symbol-discovery.json) | `ptx-modeler symbol source probe -f json` of the registered KiCad source (answered offline from the registry, `track` action). Validated by `make validate-contract`. |
| [ready-jistic.symbol-discovery.json](examples/ready-jistic.symbol-discovery.json) | `ptx-modeler symbol search --use N -f json --lang cs jistič`: a QElectroTech element converted into the managed pack, with the symbol key and the `device.place` operation (`kind: symbol-ready`). Validated by `make validate-contract`. |

### Opportunity Management Examples

| Example | Description |
| ------- | ----------- |
| [ai-in-HR.venture/](examples/ai-in-HR.venture/) | Full v2 venture: 1 initiative, 2 ideas, 2 use-cases, 1 product, 1 team, a delivery backlog + epic, plus a discussion, an estimation, and the backlog's `*.glens.json` projection. Validated by `make validate-contract`. |

### Competency Model Examples

| Example | Description |
| ------- | ----------- |
| [llm-foundations.competency-area.json](examples/llm-foundations.competency-area.json) | A CompetencyArea node |
| [rag-knowledge-retrieval.competency.json](examples/rag-knowledge-retrieval.competency.json) | A Competency node with a `production` verification block |
| [sample.competency-relations.json](examples/sample.competency-relations.json) | A relations file: one competency classified into an area, owned by a person (with inline assessment), backed by a resource, applied in a sector, and evidenced. Validated by `make validate-contract`. |

## Versioning

- Schemas use JSON Schema Draft 2020-12
- Schema versions follow the API project version
- Breaking changes require a new major version
- Backward-compatible additions (new optional fields) are minor version changes
- Schemas are the source of truth; language-specific implementations are generated or manually aligned

## Related

- [ADR-002: Unified AI Platform Transition](../docs/adr/002-unified-ai-platform-transition.md)
- [ADR-004: VIM as Universal Result Container](../portunix/portunix-architecture/docs/adr/ADR-004-vim-universal-result-container.md)
- [VIM v2.0 Format Specification](../portunix/portunix-architecture/docs/architecture/specifications/vim-v2-format.md)
