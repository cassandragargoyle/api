# Issue #109: Contract: cgproject project manifest schema (cgproject.schema.json)

**Type**: Enhancement
**Priority**: Medium
**Status**: 📋 Open
**Created**: 2026-11-16
**GitHub**: #109
**Component**: contract
**Related**: portunix-architecture brainstorming `cgproject` (session BS-CGPROJECT-20261115-01)

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

## Design So Far

The design comes from the brainstorming in portunix-architecture
(`docs/architecture/brainstorming/cgproject/`). The first example is
`examples/epbd-od-dvorak.cgproject.jsonc`, an EPBD energy assessment of a department store.

### Accepted

- The file name is `<name>.cgproject.jsonc`
- JSONC is the authoring format; JSON or BSON is the storage format
- A project is a general unit of work, not only a software project

### Proposed

- The structure follows `.ptxbook` and the ptx-installer packages: `apiVersion`, `kind`, `metadata`, `spec`
- `kind` is the document kind (`Project`, later maybe `Workspace`); `spec.type` is the project type, for example
  `building-energy` or `software`
- `spec.requires` lists the plugins and packages that the goals need
- `spec.artifacts` holds typed references, not data. Each artifact has `id`, `type`, `path`, and optional `url`,
  `title`, `layer`, `derivedFrom`, `optional`
- `spec.goals` is a map of named goals. A goal has either `run` (a command) or `task` (a plugin task type), and
  optional `forEach` (an artifact filter), `inputs`, `outputs`, `dependsOn`

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
- Examples in `contract/examples/`: the EPBD project and a software project
- Negative examples for the validation tests
- An entry in `contract/schemas/README.md`

### Out of scope

- A runner that executes goals (Portunix side)
- A contract for building facts (see open questions)
- Workspaces and inheritance

## Open Questions

These come from the brainstorming. The schema should not start before the first four have an answer.

1. Does `spec.type` prescribe required artifacts, or is it only a label?
2. Project and workspace: one file type with `kind`, or two extensions?
3. Placeholder syntax in goals (`{artifact.path}`): is a simple template enough for version 1?
4. Is the closed list of artifact types part of the schema, or an open string with known values?
5. Is the file the source of truth, with a database (Synapse Project Registry) only as an index?
6. How does the manifest relate to Synapse ADR-002 (Tenant, Project, Artifact) and to the `product` and `team`
   contracts?
7. Which contract holds building facts with provenance per value: a new `building-facts` schema, or a VIM extraction
   document?

## Acceptance Criteria

- [ ] Open questions 1–4 are answered in the portunix-architecture ADR
- [ ] `cgproject.schema.json` and `cgproject.schema.md` exist
- [ ] Both examples (building energy and software) pass the schema check
- [ ] Negative examples fail the schema check
- [ ] The schema README lists the new contract

## Next Steps in portunix-architecture

1. Write the second example (a software project, for example portunix-reco)
2. Compare both examples and answer the open questions
3. Write the ADR
