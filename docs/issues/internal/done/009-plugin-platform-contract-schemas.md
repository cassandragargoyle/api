# Issue #009: Plugin Platform Contract Schemas

**Type**: Feature
**Priority**: High
**Status**: ✅ Implemented
**Created**: 2026-03-21
**Labels**: architecture, contract, plugin-platform
**Related**: portunix-architecture ADR-001 (Unified AI Plugin & Task Platform), API ADR-002

## Summary

Add the three planned Plugin Platform JSON schemas to `contract/schemas/`.
These schemas formalize the de facto `plugin.json` standard that already
exists across 15+ plugins in the Portunix ecosystem into proper JSON Schema
definitions that can be validated programmatically.

## Context

The Unified AI Plugin Platform (ADR-001) requires three plugin-level contracts
that were listed as **(planned)** in `ARCHITECTURE-SYNC.md`:

1. `plugin-manifest.schema.json` — plugin registration and identity
2. `plugin-permissions.schema.json` — permission model
3. `plugin-health.schema.json` — health check response

The Task Platform contracts (task-request, task-response, error, task-manifest)
already exist and are in use. The plugin contracts complete the platform by
covering the plugin lifecycle layer.

### Source Material

The schemas are derived from:

- **gRPC proto**: `portunix/src/app/plugins/proto/plugin.proto` — PluginInfo,
  PluginPermissions, PluginCapabilities, HealthResponse messages
- **De facto plugin.json**: production `plugin.json` files across scraper, vox,
  text-extractor, agent, fulltext, docgen, redmine, and other plugins
- **Go plugin template**: `docs/plugin-development/languages/go/template/proto/plugin.proto`
  — includes MCP tool support

## Deliverables

### Schemas Created

| Schema | Proto Equivalent | Description |
| ------ | ---------------- | ----------- |
| `plugin-manifest.schema.json` | `PluginInfo`, `PluginCommand`, `PluginParameter`, `PluginCapabilities` | Plugin identity, runtime, commands, AI integration |
| `plugin-permissions.schema.json` | `PluginPermissions` | Filesystem, network, database, system permissions |
| `plugin-health.schema.json` | `HealthResponse`, `HealthCheckResponse` | Status, uptime, metrics, sub-component checks |

### Examples Created

| Example | Description |
| ------- | ----------- |
| `plugin-manifest-reco.json` | Python helper plugin (reco) |
| `plugin-manifest-text-extractor.json` | Java plugin with MCP tools |
| `plugin-health-serving.json` | Healthy gRPC plugin with sub-checks |

### Key Design Decisions

- **JSON Schema, not proto** — contracts are in JSON Schema (Draft 2020-12)
  to match the existing task platform contracts. The proto definitions remain
  as the gRPC implementation layer; the JSON schemas are the platform contract.
- **`$ref` for permissions** — plugin-manifest uses `$ref` to plugin-permissions,
  keeping the permission model reusable independently.
- **Backward compatible** — all fields from existing production plugin.json
  files are represented. No existing plugin.json needs to change.
- **`additionalProperties: false`** — strict validation, matching the pattern
  of existing task platform schemas.

## References

- [portunix-architecture protobuf-overview.md](protobuf-overview.md) — all proto files
- [Core plugin.proto](portunix/src/app/plugins/proto/plugin.proto) — source messages
- [Go plugin template proto](portunix/docs/plugin-development/languages/go/template/proto/plugin.proto) — MCP tool messages
