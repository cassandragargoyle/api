# Contract Schemas

Language-independent [JSON Schema](https://json-schema.org/) (Draft 2020-12)
definitions. Schemas are the **source of truth**; language bindings (Go writer,
TypeScript reader, …) are aligned to them, not the other way around.

Each schema carries a `$schema` (Draft 2020-12), a stable `$id`
(`https://cassandragargoyle.org/schemas/<name>.schema.json`) and a semantic
`$version`. Objects are closed (`additionalProperties: false`) unless a field is
explicitly documented as free-form.

For the platform-wide index (task platform, VIM, plugin platform, visualization)
see [`../README.md`](../README.md). This file documents the **Opportunity
Management** and the **Recognition Engine Lab** schema sets in depth.

## Opportunity Management (v2)

Model introduced in ADR-008 and revised to the **v2 two-spine** shape in
[issue #015](../../docs/issues/done/015-opportunity-management-v2-entities.md)
(supersedes the v1 set in #014). The data is a directory-based, git-tracked set
of JSON documents authored by `ptx-pft` (`pft ideas`) and rendered by the Pilot
UI. The two spines are:

- **Discovery / strategy** (demand, permanent asset): **Venture → Initiative → Idea**.
  An *Idea* (`opportunity.schema.json`) is product-agnostic and carries a coarse
  t-shirt **`complexity`** — never story points.
- **Delivery** (supply, time-boxed): **Team/Project → Epic → Use-Case**. A
  *Use-Case* is bound to exactly one **Product**, `implements` one or more Ideas,
  and is where real **`storyPoints`** live (planning-poker `estimation`).

### Linking principle

Every `*Ref` field points from the **volatile/derived** entity to the
**stable/asset** entity, and each relationship is owned by exactly one side.
Time-boxed entities (initiative, epic, use-case) reference stable assets (idea,
product) — never the reverse — so JSON edits stay local and git-merge-friendly.

### Storage model (v2.1)

`teams/` and `backlogs/` are **sibling** collections at the venture level — a
backlog is *not* nested in a team. A **Backlog** has an optional `teamRef`; an
**Epic** belongs to a backlog (`backlogRef`), so the team link is indirect
(epic → backlog → team). Ideas, use-cases, their estimation and discussion
threads live together in a `records/` pool.

```text
<name>.venture/                      # top-level, self-contained
├── venture.json                     # venture.schema.json
├── products/
│   └── <slug>.product.json          # product.schema.json
├── teams/
│   └── <slug>.team.json             # team.schema.json
├── initiatives/
│   └── <slug>.initiative.json       # initiative.schema.json (ideaRefs[])
├── records/                         # ideas + use-cases together
│   ├── <slug>.opportunity.json      # opportunity.schema.json (Idea: complexity)
│   ├── <slug>.usecase.json          # use-case.schema.json (storyPoints, productRef)
│   ├── <slug>.estimation.json       # estimation.schema.json (subjectRef = use-case)
│   └── <slug>.discussion.json       # discussion.schema.json (any subject)
└── backlogs/                        # SAME LEVEL as teams (first-class)
    ├── <slug>.backlog.json          # backlog.schema.json (kind, teamRef?, members)
    ├── <slug>.epic.json             # epic.schema.json (backlogRef, useCaseRefs[])
    └── <slug>.glens.json            # derived GraphView projection (reused, #011)
```

### Schemas

| Schema | Entity | Key fields |
| ------ | ------ | ---------- |
| [venture.schema.json](venture.schema.json) | Venture (container) | `id`, `slug`, `name`, reserved `tenantId`/`workspaceId` |
| [initiative.schema.json](initiative.schema.json) | Initiative (strategic intent) | `approach`, `status`, `period`, **`ideaRefs[]`** |
| [opportunity.schema.json](opportunity.schema.json) | Idea (demand asset) | **`complexity`** (t-shirt), `scores`, `interest` 💡, `volunteers` 🔨 |
| [use-case.schema.json](use-case.schema.json) | Use-Case (delivery unit) | **`storyPoints`**, **`productRef`**, **`implementsIdeaRefs[]`**, `ucDocRef?` |
| [product.schema.json](product.schema.json) | Product (asset) | `repo`, `component`, optional `vendorRef`; referenced by stable `id` |
| [team.schema.json](team.schema.json) | Team / Project (registry) | `kind`, optional `productRefs[]` |
| [backlog.schema.json](backlog.schema.json) | Backlog (first-class) | **`kind`** (discovery\|delivery), `teamRef?`, `memberRefs[]`, `relations[]`, `graphRef?` |
| [epic.schema.json](epic.schema.json) | Epic (delivery grouping) | **`backlogRef`**, `initiativeRefs[]`, `useCaseRefs[]`, derived `storyPoints` |
| [estimation.schema.json](estimation.schema.json) | Estimation (planning poker) | `subjectRef` = use-case id, `estimations[]` (latest → rollup) |
| [discussion.schema.json](discussion.schema.json) | Discussion (thread, **v3**) | `subjectRef` + `subjectKind`, `status`, `participants[]`, typed `messages[]` |

### Discussion contract (v3.0.0, structured multi-actor)

The [discussion.schema.json](discussion.schema.json) thread evolved from the thin
2-party v2 chat (`role` ∈ {user, assistant}) to a **structured, multi-actor** model
([issue #016](../../docs/issues/done/016-discussion-contract-v3-structured.md)).
The contract is the source of truth for the Go writer (`ptx-pft`) and the
TypeScript reader (Pilot `VentureDiscussionView`), and stays SaaS-ready.

**Top level** — `subjectRef` + `subjectKind` (`idea|use-case|initiative|epic`),
optional `status` (`open|resolved|archived`) and `participants[]`
(`{ kind: user|ai_agent|system, id, displayName, avatarUrl? }`).

**Per message** — `id` (required, stable), optional `parentId` (replies form an
unbounded tree), `actor` (`{ kind, id, displayName }`, replaces v2 `role`/`author`),
`type` (`comment|question|proposal|decision|ai_summary|system`, drives UI rendering),
`text` (markdown), `timestamp` (ISO 8601). Optional team affordances: `reactions[]`
(`{ emoji, actorIds[] }`), `mentions[]` (actor ids), `attachments[]`
(`{ kind: file|link, ref, label? }`) and `produced[]`
(`{ kind: idea|use-case|initiative|epic|decision|risk, ref }`) for dereferenceable
provenance. All objects stay closed (`additionalProperties: false`).

#### Migration (v2 → v3, breaking)

Mechanical, per message:

| v2 field | v3 field |
| -------- | -------- |
| `role: "user"` | `actor.kind: "user"` |
| `role: "assistant"` | `actor.kind: "ai_agent"` |
| `author` | `actor.id` / `actor.displayName` |
| — | `id` (new, required) |
| — | `type` (new, required; default `comment`) |
| — | `parentId` (new, optional; absent for flat v2 threads) |

Top-level `status` and `participants[]` are new and optional. See
[`../examples/ai-in-HR.venture/records/onboarding-assistant.discussion.json`](../examples/ai-in-HR.venture/records/onboarding-assistant.discussion.json)
for a v3 thread exercising typed messages, a reply tree, reactions, mentions and a
`produced[]` provenance link.

### Graph projection (reused, not redefined)

Each backlog's node/link graph reuses the existing
[graph-view.schema.json](graph-view.schema.json) (`*.glens.json`, #011). Nodes =
ideas / use-cases / epics; links = relations (`inspired-by`, `duplicate`,
`split-into`, `merged-with`) **plus** the `implements` edge (use-case → idea).
`link.rel` is a free-form string, so `implements` needs **no schema change**. The
`*.backlog.json` file holds the backlog's metadata + membership; the graph is a
derived `*.glens.json`. A projection can also use the optional node groups of
schema 1.1.0 (`node.group`, `meta.groups`, #78).

### Design decisions (from #015 open questions)

- **`complexity` scale** — t-shirt (`xs|s|m|l|xl`, or `null`), deliberately
  non-numeric so it stays visually distinct from real story points.
- **Use-case identity** — a lightweight entity with an optional `ucDocRef` to a
  formal `UC-<COMPONENT>-<NNN>` document, not the document itself.
- **Epic `storyPoints`** — optional cached rollup (Σ of referenced use-cases);
  omit/`null` to compute on read.
- **SaaS-readiness** — every entity carries reserved `tenantId`/`workspaceId`
  (nullable, no behavior in the local store) for a future multi-tenant layer.

## Recognition Engine Lab

Contract for training, testing, and comparing a recognition engine
([issue #56](../../docs/issues/done/056-reco-engine-lab-contract.md), consumer:
portunix-reco-studio #55). The contract describes how to call an engine, it does not depend on
one.

| Schema | Suffix | Written by |
| ------ | ------ | ---------- |
| [reco-engine-lab.schema.json](reco-engine-lab.schema.json) | `*.recolab.json` | a person |
| [reco-run.schema.json](reco-run.schema.json) | `*.run.json` | the tool that runs the engine |
| [reco-run-sample.schema.json](reco-run-sample.schema.json) | `*.samples.jsonl` (one line per sample) | the `evaluate` phase |
| [reco-run.schema.json](reco-run.schema.json) `#/$defs/HistoryLine` | `*.history.jsonl` (one line per step) | the `train` phase |

Paths in the lab document are relative to its folder. Paths in a run record and in its
`.jsonl` files are relative to the folder of the run record. The lab document points to the
run records with `runs.location` (for example `runs/*.run.json`).

### Engine

The engine is the unit that is tested and compared. A simple engine has `implementation` and
`model`. A composite engine, for example a form pipeline, has named `components`; the result of a
field names the component that recognized it. `engine.config` lists the files that change the
output, each with a `role` (for example `template`). A run record stores the checksum of each of
these files, so a changed template is visible in the comparison.

### Commands

Each phase (`prepare`, `train`, `test`, `evaluate`) has a `runner`:

| Runner | The host runs | Required fields |
| ------ | ------------- | --------------- |
| `portunix-plugin` | `portunix <plugin> <args...>` | `plugin` (the `name` of the plugin manifest), `args`; optional `min_version` |
| `exec` | `argv` directly, for engines that are not Portunix plugins | `argv` |

Both runners take an optional `cwd` (relative to the lab document, default `.`), `env`, and
`progress`. Arguments are arrays, not shell strings. Exit code `0` means success. A cancelled
run is stopped by the host, which writes `status: "cancelled"`. With `progress.format`
`jsonl-stdout`, the command writes lines such as `{"progress":0.4,"message":"..."}` to stdout;
with `none` it reports no progress.

The host replaces these placeholders in `args` and `argv`. The schema does not check unknown
placeholders.

| Placeholder | Value |
| ----------- | ----- |
| `{doc_dir}` | Folder of the lab document |
| `{run_dir}` | Output folder of the run |
| `{run_id}` | Identifier of the run |
| `{dataset.<name>}` | `location` of the dataset `<name>` |
| `{config.<role>}` | `path` of the config file with this `role` |
| `{model}` | `engine.model` |
| `{baseline_run_dir}` | Output folder of the `baseline_run` |

### Dataset checksum

`sha256` of a dataset is the SHA-256 of a text with one line per sample file:
`<path relative to the lab document> <SHA-256 of the file>`, sorted, each line ending with
`\n`. Runs are compared only when their datasets have the same checksum. The field is optional.

### Per-sample results and comparison

`prediction_format` names the format of the output of one sample. Known values:

| Value | Format |
| ----- | ------ |
| `reco-export-xml` | Export of portunix-reco with a form template (FormData XSD in portunix-reco) |
| `ocr-result` | [ocr-result.schema.json](ocr-result.schema.json) |
| `label` | Short value in `prediction_value`, for example a class label |

A tool compares two runs per sample (`sample`) and field (`field`): **improved** (wrong, then
correct), **regressed** (correct, then wrong), **changed and still wrong**, **unchanged**. The
comparison is not stored; a run can name the run it is compared with in `baseline_run`.

## Recognition Form Result

[reco-form-result.schema.json](reco-form-result.schema.json) describes the JSON result of one
form recognition run of portunix-reco ([issue #69](../../docs/issues/done/069-reco-form-result-schema.md),
portunix-architecture ADR-011, decision 6). The schema follows the keys of the reco code
(`map_alignment_result` in `ptx_reco.worker.task.mapper`), it has no VIM concepts.

| Schema | Suffix | Written by |
| ------ | ------ | ---------- |
| [reco-form-result.schema.json](reco-form-result.schema.json) | `*.reco-result.json` | portunix-reco, task `reco.match.template` |

- **Root** — the `result` object of the task. A degraded alignment gives a task response with
  the status `partial`, the same `result`, and `error` with the code `ALIGNMENT_DEGRADED`.
  Whether `<stem>_result.json` holds the `result` object alone or the whole task response is
  decided by portunix-reco #57. `ptx-reco task execute` exits with code 1 for every status
  other than `success`, so a caller reads the status of the response, not only the exit code
- **Positions** — pixels, origin top-left. Markers are in the input image, zones in the
  aligned image
- **`bbox` of a recognition zone** — defined by the contract, not yet written by
  portunix-reco 0.3.11; a consumer must accept a zone without it
- **`run_info`** — optional, older reco versions do not write it. A phase has `name` and
  `status` (`ok`, `partial`, `skipped`, `failed`) and can add details beyond the ones the
  schema lists. `started` and `duration_ms` differ between two runs on the same input
- **Closed objects** — all other objects reject unknown keys, so a new key in the reco
  output is a contract change
- **Consistency** — `transform_matrix` has 2 rows for `affine` and 3 rows for `homography`
  (schema). `markers_expected` is the number of `marker_details`, `markers_detected` the number
  of them with `detected` true, and `run_info.run.status` is `failed` when a phase failed,
  `partial` when a phase is partial, otherwise `ok` (extra check, see
  [Validating examples](#validating-examples))

## Validating examples

Contract examples under [`../examples/`](../examples/) are validated against these
schemas by filename convention:

```bash
make validate-contract          # or: npm --prefix typescript run validate:contracts
```

Some rules cannot be expressed in JSON Schema. The validation runs an extra check
after the schema for these contracts:

| Schema | Extra check |
| ------ | ----------- |
| `vim-document.schema.json` | [`scripts/vim-identifier-check.mjs`](../../scripts/vim-identifier-check.mjs): unique identifiers and existing link targets of the prescriptive layer |
| `reco-form-result.schema.json` | [`scripts/reco-form-result-check.mjs`](../../scripts/reco-form-result-check.mjs): marker counts match `marker_details`, `run_info.run.status` follows the phase statuses |

A document under `examples/invalid/` passes as a negative test when the schema or the
extra check rejects it.

A `.jsonl` file is checked line by line, and an error names the line (`line 2: /fields/0 ...`).
A negative `.jsonl` test passes when at least one line is rejected. A suffix can map to a part
of a schema (`*.history.jsonl` to `reco-run.schema.json#/$defs/HistoryLine`).
