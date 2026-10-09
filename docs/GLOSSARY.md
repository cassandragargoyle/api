---
title: Glossary
description: Use when you meet an unknown project term in the code, the contracts, or the issues. Short definitions of the terms used in the Api repository, each with a link to the authoritative source.
category: reference
ai_load: on-demand
status: active
language: en
created: 2026-10-09
last_updated: 2026-10-09
related:
  - contributing/TERMINOLOGY.md
  - ../contract/README.md
  - ../contract/schemas/README.md
  - adr/README.md
---

# Glossary

Short definitions of the terms used in this repository. Each section names the
authoritative source. When a definition here and the source disagree, the source wins.

General software and process terms (branch, pull request, sprint, and so on) are in
[contributing/TERMINOLOGY.md](contributing/TERMINOLOGY.md).

## Repository and Ecosystem

| Term | Meaning |
| ---- | ------- |
| **Api** | This repository. A shared library in four languages (Java, Python, Go, TypeScript) plus the language-independent contracts |
| **Language module** | One of the directories `java/`, `python/`, `go/`, `typescript/`. Each module builds and publishes on its own |
| **Contract** | A language-independent definition of a data format or a service, stored in `contract/`. Contracts are the source of truth; code in each language follows them |
| **Schema** | A JSON Schema file (Draft 2020-12) in `contract/schemas/`. It has a stable `$id` and a semantic `$version` |
| **Proto** | A gRPC service definition in `contract/proto/`. Servers and clients copy the file into their own tree |
| **Example** | A sample document in `contract/examples/`. `make validate-contract` validates the examples against the schemas |
| **Closed object** | A schema object with `additionalProperties: false`. Unknown keys do not validate |
| **Portunix** | The ecosystem that uses these contracts: the platform and its plugins |
| **Plugin** | An extension of Portunix. A plugin describes itself with a plugin manifest. Plugin names often start with `ptx-` |
| **Unified AI Plugin Platform** | The part of the platform that registers plugins and checks their health and permissions |
| **Unified AI Task Platform** | The part of the platform that finds and runs tasks with one request format and one response format |
| **ADR** | Architecture Decision Record. A document in `docs/adr/` that records one decision, its context, and its consequences |

Sources: [README.md](../README.md), [contract/README.md](../contract/README.md),
[adr/README.md](adr/README.md).

## Task Platform

| Term | Meaning |
| ---- | ------- |
| **Task** | One unit of work that a plugin can run. A task type identifier names it, for example `image.resize` |
| **Task manifest** | The description of one task for AI assistants: when to use it, the parameters, the expected artifacts, and how to read the result |
| **Task request** | The unified input of a task: `task`, `input`, `config`, `context`, plus `trace_id` and `job_id` |
| **Task response** | The unified output of a task. `status` is `success`, `error`, `partial`, or `cancelled` |
| **Error model** | The structured error in a task response: `code`, `message`, `details`, and the `retryable` flag |
| **Trace ID** | The OpenTelemetry identifier of one distributed trace. The response repeats the value from the request |
| **Job ID** | The unique identifier of one job. It is used for tracking and to detect duplicates |
| **OCR result** | The standard result of text recognition, with the position of the text in a declared coordinate system |

## Plugin Platform

| Term | Meaning |
| ---- | ------- |
| **Plugin manifest** | The registration document of a plugin: identity, runtime, dependencies, permissions, commands, and AI integration |
| **Plugin permissions** | The resources that a plugin needs: filesystem, network, database, system. The overall level is `limited`, `standard`, or `full` |
| **Plugin health** | The answer to a health check. `status` is `serving`, `not_serving`, or `unknown` |
| **`--help-ai`** | A plugin option that prints machine-readable help. AI assistants and editors read it to find the commands of the plugin |
| **Supported platforms** | The hosting platforms that a plugin extends, for example `synapse` |

## VIM

| Term | Meaning |
| ---- | ------- |
| **VIM** | Versatile Information Model. The universal result container of the Portunix ecosystem. One VIM document holds the metadata of a source document and its layers |
| **Layer** | One optional part of a VIM document. Each layer holds one view of the same source document. The `layers` object is closed, so a new layer needs a contract change |
| **Physical layer** | Layer 1. Raw data with coordinates |
| **Structural layer** | Layer 2. Logical grouping of the raw data |
| **Semantic layer** | Layer 3. Interpreted meaning |
| **TOC layer** | Layer 4. Table of contents |
| **Extraction layer** | Layer 5. Structured values extracted from forms and zones. The `reco` plugin produces it |
| **Template** | The definition of the fields and zones to extract. The extraction layer refers to it with `template_ref` |
| **Source** | One input of an extraction (a file or a page) together with its raw result |
| **Field** | One extracted value with its label, data type, confidence, and position |
| **Provenance** | The record of where a value comes from and how it was produced. It allows replay and debugging |
| **Confidence** | A number from 0.0 to 1.0 that says how sure the extraction is |
| **Revision** | One entry in the change history of a value |
| **Prescriptive layer** | The optional layer of VIM 2.1 that stores what a definition file prescribes: provisions, references, and terms |
| **Definition file** | A document that prescribes something, for example a specification, a guideline, or a contract |
| **Provision** | One statement of a definition file that prescribes something. Its `kind` is `requirement`, `recommendation`, or `permission` |
| **Reference** | A link from a definition file to another document (`external`) or to another clause of the same file (`internal`) |
| **Term** | A term that a definition file defines and that provisions use |
| **Anchor** | A place in the definition file: a page, and optionally an element of the physical layer and a region |
| **Verification** | How a provision can be checked: `automatic`, `test`, or `manual`, with an optional machine-readable rule |

Layers 1 to 4 are produced by `vim-parser`. The prescriptive layer is defined by the
specification *VIM Prescriptive Layer (VIM 2.1)* in portunix-architecture; its shape is not
yet tested on real definition files.

## Opportunity Management

| Term | Meaning |
| ---- | ------- |
| **Venture** | The top-level container. A directory `<name>.venture/` that holds all other entities |
| **Spine** | One of the two chains of the model: discovery and delivery |
| **Discovery spine** | The demand side, a permanent asset: Venture → Initiative → Idea |
| **Delivery spine** | The supply side, limited in time: Team/Project → Epic → Use-Case |
| **Initiative** | A strategic intent. It refers to ideas with `ideaRefs[]` |
| **Idea** | A demand asset that is not bound to a product. The schema file is `opportunity.schema.json`. It carries a `complexity`, never story points |
| **Complexity** | A coarse t-shirt size of an idea: `xs`, `s`, `m`, `l`, `xl`. It is not a number on purpose |
| **Use-Case** | A delivery unit. It belongs to exactly one product, implements one or more ideas, and carries the story points |
| **Story points** | The real estimate of a use-case. An epic can cache the sum of its use-cases |
| **Product** | A stable asset registered in the venture. It can refer to its owner organization with `vendorRef` |
| **Team / Project** | An entry in the team registry. `kind` says which of the two it is |
| **Backlog** | A first-class collection in a venture, at the same level as teams. `kind` is `discovery` (ideas) or `delivery` (epics and use-cases) |
| **Epic** | A delivery grouping of use-cases. It belongs to one backlog |
| **Estimation** | The planning-poker history of one use-case |
| **Discussion** | A thread that can be attached to an idea, a use-case, an initiative, or an epic. Messages have a type and an actor |
| **Actor** | The author of a discussion message: `user`, `ai_agent`, or `system` |
| **Records** | The directory `records/` in a venture. It holds ideas, use-cases, estimations, and discussions together |
| **`*Ref` field** | A link to another entity by its id. It always points from the volatile entity to the stable one, and one side only owns the link |
| **Relation** | A link between two members of a backlog: `inspired-by`, `duplicate`, `split-into`, `merged-with`, or `implements` |
| **`implements`** | The relation from a use-case to the idea that it delivers |
| **`tenantId` / `workspaceId`** | Reserved fields for a future multi-tenant layer. They are `null` in the local store |

`ptx-pft` writes these documents and the Pilot UI reads them. Source:
[contract/schemas/README.md](../contract/schemas/README.md).

## Competency Model

| Term | Meaning |
| ---- | ------- |
| **Competency** | A concrete professional capability (`competency:*`). It describes *how* work is delivered |
| **Competency area** | A group of competencies (`area:*`). It answers *what* the organization can do |
| **Sector** | A field of application (`sector:*`). It answers *where* a competency is applied |
| **Person** | The bearer of a competency (`person:*`) |
| **Resource** | A technology, tool, framework, method, or standard behind a competency (`resource:*`) |
| **Evidence** | A proof of a competency: a project, reference, certification, repository, methodology, or benchmark (`evidence:*`) |
| **Assessment** | The proficiency of a person in a competency (0 to 5) plus the AI enablement |
| **Maturity** | The maturity of the organization in a competency. It is derived from evidence and assessments, never entered by hand |
| **Verification block** | A shared block on every node and edge: `status`, `source`, `lastChecked`, `evidence` |
| **Relations file** | A file with typed edges between nodes, for example `has_competency` or `classified_in` |
| **Node id** | An identifier of the form `<prefix>:<slug>`, for example `competency:rag-knowledge-retrieval` |

## People Registry

| Term | Meaning |
| ---- | ------- |
| **Master record** | The one record of a person that holds the master data. Other layers refer to it and do not copy it |
| **Layer 1: shared registry** | The registry shared by delivery projects (`projects/users.json`). It is the target of `ref` |
| **Layer 2: project participants** | The people on one project: `ref` plus `projectRole` |
| **Layer 3: project contacts** | Contacts that wait for a role. They move to layer 2 when the role is clear |
| **Company registry** | A separate registry for the whole firm. Project layers do not refer to it |
| **`ref`** | The id of a master record in layer 1 |

## Visualization and Transcription

| Term | Meaning |
| ---- | ------- |
| **Graph view** | A document for the 3D graph viewer: `meta`, `nodes`, and `links` |
| **graphlens** | The 3D force-directed graph viewer (`ptx-graphlens`) that reads graph-view documents |
| **Link** | An edge in a graph-view document. The 2D `*.graph.json` format uses the word `edges` and is a different contract |
| **Graph projection** | A graph-view document derived from other data, for example from a backlog |
| **Transcript** | The working document of a video or audio transcript: `media`, `speakers`, `segments`, and flags. The contract is provisional |
| **scribe** | The transcript correction backend (`ptx-scribe`) |
| **Segment** | One timed part of a transcript |
| **Flag** | A mark that the assistant puts on a place in a transcript |

## Modeler

| Term | Meaning |
| ---- | ------- |
| **Modeler** | The Portunix Modeler (`ptx-modeler`), a plugin that keeps a building model |
| **Model** | The authoritative document: `layers`, `entities`, and `log`. Drawings (SVG, glTF) are derived views. Coordinates are whole millimetres |
| **Entity** | One item in the model: `id`, `kind`, `parent`, `layer`, `props` |
| **Wall graph** | The walls of a storey as a graph: junctions are the nodes and walls are the edges |
| **Device** | An electrical device in the model, for example a socket or a switch |
| **Circuit** | An electrical circuit. A device refers to it with `device.circuit` |
| **Host-relative** | A position given relative to a host wall, not in absolute coordinates |
| **Standoff** | The drawing distance of a wall-hosted symbol from the face of the wall |
| **Underlay** | An imported drawing that lies under the model as a reference |
| **Operation** | One change of the model, for example `device.attach`. An operations file is applied as one transaction |
| **Operation log** | The list of applied operations stored in the model |
| **BOM** | Bill of materials. The count of devices grouped by symbol, room, or circuit |
| **Symbol** | One drawing element with metadata, an SVG fragment, terminals, and text fields |
| **Terminal** | A connection point of a symbol |
| **Standard compliance** | How a symbol relates to a standard such as IEC 60617: `compliant`, `derived`, `custom`, or `vendor` |
| **Origin** | The pinned source of an imported symbol: repository, revision, path, format, and checksum |
| **Extensions** | Data of other tools stored in a symbol under a namespace, for example `qet` for QElectroTech |
| **Symbol pack** | A set of symbol files in `symbols/<category>/<name>.msym.json` |
| **Symbol source** | One external collection of symbols in the source registry, with its license facts |
| **Symbol discovery** | The search for symbols across sources. The result lists the availability and the next actions |

## File Suffixes

| Suffix | Content |
| ------ | ------- |
| `*.schema.json` | JSON Schema |
| `*.venture/` | Venture directory |
| `*.initiative.json`, `*.opportunity.json`, `*.usecase.json` | Initiative, idea, use-case |
| `*.product.json`, `*.team.json` | Product, team or project |
| `*.backlog.json`, `*.epic.json` | Backlog, epic |
| `*.estimation.json`, `*.discussion.json` | Estimation, discussion |
| `*.glens.json` | Graph view |
| `*.scribe.json` | Transcript |
| `*.model.json`, `*.ops.json`, `*.bom.json` | Modeler model, operations, bill of materials |
| `*.msym.json`, `*.msrc.json` | Modeler symbol, symbol source |
| `*.symbol-discovery.json` | Symbol discovery output |

## Shared Library

| Term | Meaning |
| ---- | ------- |
| **Entity** | The base abstraction of the Java module (`org.cassandragargoyle.api.entity`). Not the same as a modeler entity |
| **Diagram, Node, Edge** | Entities for graph-like structures in the Java module |
| **Data container** | An entity that holds data |
| **Levenshtein distance** | The smallest number of single-character edits that change one string into another. It is counted on Unicode code points |
| **Normalized similarity** | A value from 0.0 to 1.0 derived from the Levenshtein distance. 1.0 means equal strings |
| **TelemetryProvider** | The utility that configures tracing in one place (Java and Python) |
| **Exporter type** | The tracing mode: NoOp (default, no overhead), Console (development), or OTLP (production) |
| **OTLP** | OpenTelemetry Protocol. The protocol that sends traces to a collector |
| **LogFactory** | The factory that creates loggers with the shared output format |
| **Transport** | In the TypeScript log module, the target of the log output: the console or a rolling file |

Sources: [README.md](../README.md),
[ADR-001](adr/001-opentelemetry-tracing.md).
