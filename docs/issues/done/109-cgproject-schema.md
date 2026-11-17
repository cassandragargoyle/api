# Issue #109: Contract: cgproject project manifest schema (cgproject.schema.json)

**Type**: Enhancement
**Priority**: Medium
**Status**: ✅ Implemented (PR #112, 2026-11-17)
**Created**: 2026-11-16
**Closed**: 2026-11-17
**GitHub**: #109
**Component**: contract
**Related**: portunix-architecture [ADR-012](https://github.com/cassandragargoyle/portunix-architecture/blob/main/docs/adr/ADR-012-cgproject-project-manifest.md)
(accepted 2026-11-16), brainstorming `cgproject` (session BS-CGPROJECT-20261115-01)

## Summary

Add a contract for a general project manifest `<name>.cgproject.jsonc`. A project file says what belongs to a
project and what can be done with it, similar to a Visual Studio project or a Maven POM. A project is a general unit
of work, not only a software project. One example is a building energy calculation.

## Problem Description / Motivation

The CassandraGargoyle tools work on many kinds of files: VIM documents, Modeler models, downloaded sources, code. No
file says which of them belong together, how they depend on each other, and which steps make one from another. Today
a person keeps this knowledge in their head or in scripts.

A project manifest gives one place for this. Portunix and its plugins can read it, install what the project needs,
and run its steps (goals) in the right order.

## Design

The design is decided in portunix-architecture
[ADR-012](https://github.com/cassandragargoyle/portunix-architecture/blob/main/docs/adr/ADR-012-cgproject-project-manifest.md).
The examples are in `docs/architecture/brainstorming/cgproject/examples/` of portunix-architecture:
`epbd-od-dvorak.cgproject.jsonc` (a building energy assessment) and `portunix-reco.cgproject.jsonc` (a software
project).

The rules that the schema must follow:

- One schema for `<name>.cgproject.jsonc` (`kind: Project`) and `<name>.cgworkspace.jsonc` (`kind: Workspace`)
- The envelope is `apiVersion` (`portunix.ai/v1`), `kind`, `metadata`, `spec`
- `metadata`: `id`, `name`, `description`, `version` or `versionFrom` (the id of a `build-descriptor` artifact),
  `language`, `repository`, `product` (the id of a `product` contract); no `tenantId` or `workspaceId`
- `spec.type` is a string label with no rules per type
- `spec.requires`: items with `plugin` or `package`, optional `version` and `optional`
- `spec.artifacts[]`: fixed core fields `id`, `type`, `path`, `url`, `title`, `layer`, `template`, `contract`,
  `derivedFrom`, `optional`; extra fields are allowed. `type` is an open kebab-case string; the description lists
  the known values `source`, `vim-document`, `modeler-model`, `build-descriptor`, `source-tree`, `test-tree`,
  `plugin-manifest`, `task-manifest`, `dataset`, `model`, `package`, `document`, `assessment`
- `spec.dependencies[]`: `project` (a `metadata.id`), optional `url`, `use`, `artifacts`; no versions in version 1
- `spec.goals{}`: each goal has exactly one of `run` (a command) or `task` (a task type), and optional
  `description`, `forEach` (an artifact filter), `inputs`, `outputs` (artifact ids), `dependsOn` (goal names)
- Placeholders in strings are only `{artifact.<field>}` and `{metadata.<field>}`; the schema describes them but
  does not parse them

Building facts are not part of this contract. They are a VIM document with the `extraction` layer and a
`building-facts` template (ADR-012, decision 5).

Short example:

```jsonc
{
  "$schema": "https://cassandragargoyle.org/schemas/cgproject.schema.json",
  "apiVersion": "portunix.ai/v1",
  "kind": "Project",
  "metadata": { "id": "epbd-od-dvorak", "name": "EPBD – Obchodní dům Dvořák", "version": "0.1.0" },
  "spec": {
    "type": "building-energy",
    "requires": [ { "plugin": "crawler" }, { "plugin": "vim-parser" } ],
    "artifacts": [
      { "id": "src-cz-406-2000", "type": "source", "path": "sources/cz-406-2000.md" },
      { "id": "law-cz-406-2000", "type": "vim-document", "layer": "prescriptive",
        "path": "vim/cz-406-2000.vim.json", "derivedFrom": ["src-cz-406-2000"] }
    ],
    "goals": {
      "extract-provisions": {
        "task": "vim-parser.prescriptive",
        "forEach": { "type": "vim-document", "layer": "prescriptive" }
      }
    }
  }
}
```

## Scope

### In scope

- `contract/schemas/cgproject.schema.json` (JSON Schema draft 2020-12) for the structure above
- `cgproject.schema.md` with the description of the fields
- Examples in `contract/examples/`: the EPBD project and the portunix-reco project from ADR-012
- Negative examples for the validation tests
- An entry in `contract/schemas/README.md`

### Out of scope

- A runner that executes goals (Portunix side)
- A contract for building facts (a VIM `extraction` document, ADR-012)
- The `spec` of a workspace (the list of projects, shared settings); the schema accepts `kind: Workspace` with the
  same envelope
- Inheritance between projects (rejected in ADR-012)

## Open Questions

All open questions of the brainstorming are answered in ADR-012. Questions left for later, not for this issue:

- Profiles per project type (a schema per type that extends cgproject)
- Versions of project dependencies

## Acceptance Criteria

- [x] The open questions are answered in portunix-architecture ADR-012
- [x] `cgproject.schema.json` and `cgproject.schema.md` exist
- [x] Both examples (building energy and software) pass the schema check
- [x] Negative examples fail the schema check
- [x] The schema README lists the new contract
