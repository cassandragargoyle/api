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
| [use-case.schema.json](schemas/use-case.schema.json) | Delivery unit; `storyPoints`, `productRef`, `implementsIdeaRefs[]` |
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
