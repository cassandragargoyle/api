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

### Visualization

| Schema | Description |
| ------ | ----------- |
| [graph-view.schema.json](schemas/graph-view.schema.json) | 3D force-directed node-graph document for the graphlens viewer (`meta` + `nodes` + `links`) |

Graph-view documents use the **`*.glens.json`** filename suffix (tied to graphlens / `ptx-graphlens`) so tools, editors, and viewers recognize the type without inspecting contents.

> **Distinct from the 2D `*.graph.json` format.** The 2D Graph Canvas format (`GraphFile`: `nodes` + `edges` + `metadata`, Cytoscape, portunix-vscode #030) is a different contract. This 3D graph-view format uses `links` (not `edges`) and `meta` (not `metadata`), and must not reuse the `*.graph.json` suffix.

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

### Visualization Examples

| Example | Description |
| ------- | ----------- |
| [voices.glens.json](examples/voices.glens.json) | Small voices graph for the graphlens 3D viewer |

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
