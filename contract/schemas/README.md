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
Management** schema set in depth.

## Opportunity Management (v2)

Model introduced in ADR-008 and revised to the **v2 two-spine** shape in
[issue #015](../../docs/issues/internal/015-opportunity-management-v2-entities.md)
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
([issue #016](../../docs/issues/internal/016-discussion-contract-v3-structured.md)).
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
derived `*.glens.json`.

### Design decisions (from #015 open questions)

- **`complexity` scale** — t-shirt (`xs|s|m|l|xl`, or `null`), deliberately
  non-numeric so it stays visually distinct from real story points.
- **Use-case identity** — a lightweight entity with an optional `ucDocRef` to a
  formal `UC-<COMPONENT>-<NNN>` document, not the document itself.
- **Epic `storyPoints`** — optional cached rollup (Σ of referenced use-cases);
  omit/`null` to compute on read.
- **SaaS-readiness** — every entity carries reserved `tenantId`/`workspaceId`
  (nullable, no behavior in the local store) for a future multi-tenant layer.

## Validating examples

Contract examples under [`../examples/`](../examples/) are validated against these
schemas by filename convention:

```bash
make validate-contract          # or: npm --prefix typescript run validate:contracts
```
