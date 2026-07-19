# Issue #015: Opportunity Management v2 — Venture / Initiative / Use-Case / Epic / Team / Product contracts

**Type**: Feature
**Priority**: Medium
**Status**: ✅ Implemented
**Created**: 2026-07-10
**Closed**: 2026-07-10
**Labels**: contract, schema, opportunity-management, use-case, initiative, epic, team, product, iso16355, pft, multi-language
**Repository**: Api
**Related**: #014 (Opportunity Management contracts v1 — extended by this issue),
ADR-008 (Opportunity Management — portunix-architecture),
portunix #197 (ptx-pft `pft ideas` v2), portunix-vscode #098 (Pilot v2 GUI),
#011 (Graph View Contract — reused for both backlogs)

## Summary

Extend the Opportunity Management contract set (#014) to the **v2 entity model**
agreed in the 2026-07-10 brainstorming session
(`portunix-architecture/docs/architecture/brainstorming/opportunity-management/journal-20260710-01.json`).
The model grows from a single-tier "discovery owns opportunities" shape into a
**two-spine** model with seven entities, and the container is renamed
`.discovery` → **`.venture`**. Schemas remain the source of truth for the Go
writer (`ptx-pft`) and the TypeScript reader (Pilot), and stay **SaaS-ready**.

## Motivation

The v1 model conflated demand and delivery: an *idea* carried story points, which
cannot be estimated without a target product. v2 separates the two spines:

- **Discovery / strategy (demand, permanent asset):** Venture → Initiative → Idea.
  An **Idea** is product-agnostic and carries a coarse **`complexity`** (not SP).
- **Delivery (supply, time-boxed):** Team/Project → Epic → Use-Case. A **Use-Case**
  is bound to exactly one **Product**, `implements` one or more Ideas, and is where
  real **`storyPoints`** live (planning-poker `estimation`).

**Linking principle (drives every `*Ref` field):** references point from the
volatile/derived entity to the stable/asset entity, and each relationship is
owned by exactly one side. Time-boxed entities (initiative, epic, use-case)
reference stable assets (idea, product) — never the reverse — so JSON edits stay
local and git-merge-friendly.

## Storage model (v2.1 — looser, backlog is first-class)

`teams/` and `backlogs/` are **sibling collections** at the venture level — a
backlog is **not** owned by (nested in) a team. Coupling is loose: a **Backlog**
has an *optional* `teamRef`, and an **Epic** belongs to a backlog (`backlogRef`),
so the team link is indirect (epic → backlog → team). Records (ideas + use-cases +
their estimation + generic discussion) live in a `records/` pool.

```text
<name>.venture/                      # top-level, self-contained (renamed from .discovery)
├── venture.json                     # neutral container metadata
├── products/
│   └── <slug>.product.json          # Product registry (inside the venture)
├── teams/
│   └── <slug>.team.json             # Team/Project identity (registry)
├── initiatives/
│   └── <slug>.initiative.json       # Initiative (strategic intent), ideaRefs[]
├── records/                         # records pool (ideas + use-cases together)
│   ├── <slug>.opportunity.json      # Idea: complexity, scores, interest 💡, volunteers 🔨
│   ├── <slug>.usecase.json          # Use-Case: storyPoints, productRef, implementsIdeaRefs[]
│   ├── <slug>.estimation.json       # planning-poker — beside its use-case
│   └── <slug>.discussion.json       # generic thread (any subject: idea|use-case|initiative|epic)
└── backlogs/                        # SAME LEVEL as teams (first-class)
    ├── <slug>.backlog.json          # kind (discovery|delivery), optional teamRef, member+relation refs → GraphView (#011)
    └── <slug>.epic.json             # Epic: backlogRef, initiativeRefs[], useCaseRefs[]
```

## Scope

### New schemas under `contract/schemas/`

- **`venture.schema.json`** — supersedes `discovery.schema.json`: `schemaVersion`,
  `tenantId`, `workspaceId`, `id`, `slug`, `name`, `description`, timestamps.
  (A venture may hold one or more initiatives; it is a neutral container, not an
  initiative itself.)
- **`initiative.schema.json`** — `id`, `slug`, `title`, `description`,
  `approach` (`iso16355` | `ai-funnel` | `mixed`), `status`
  (`active` | `closed` | `archived`), `period` (`{ start, end }`), `owner`,
  **`ideaRefs[]`** (M:N — the ideas this initiative pursues), timestamps.
- **`use-case.schema.json`** (new) — `id`, `slug`, `title`, `actor`, `goal`,
  `status`, **`storyPoints`** (rollup = latest estimation), **`productRef`**
  (N:1), **`implementsIdeaRefs[]`** (M:N, usually one), optional **`ucDocRef`**
  (link to a formal `UC-<COMPONENT>-<NNN>` document — see open questions),
  `tags[]`, timestamps.
- **`product.schema.json`** (new) — `id`, `slug`, `name`, `description`,
  optional `repo`/`component`, timestamps. Referenced by stable `id`.
- **`team.schema.json`** (new) — `id`, `slug`, `name`, `kind` (`team` | `project`),
  optional **`productRefs[]`** (else derived from use-cases), timestamps.
- **`backlog.schema.json`** (new, **first-class**) — `id`, `slug`, `name`,
  **`kind`** (`discovery` | `delivery`), optional **`teamRef`** (N:1 — a backlog
  may belong to a team, be shared, or be team-less), member refs (ideas for a
  discovery backlog; epics/use-cases for a delivery backlog), and relations. The
  node/link **graph projection reuses GraphView** (#011); this schema carries the
  backlog's own metadata + membership.
- **`epic.schema.json`** (new) — `id`, `slug`, `title`, `status`,
  **`backlogRef`** (N:1 — the epic lives in a backlog; team is reached indirectly
  via the backlog), **`initiativeRefs[]`** (M:N strategic parent),
  **`useCaseRefs[]`** (link-set), timestamps. `storyPoints` is **derived**
  (Σ of referenced use-cases) — computed, or stored as a cached rollup.

### Revised from #014

- **`opportunity.schema.json`** — replace `estimate.storyPoints` with
  **`complexity`** (coarse, product-agnostic; see open questions for the scale).
  Keep `scores` (5×1–5), `interest` 💡, `volunteers` 🔨, `aiAmbassador`, `origin`,
  `solutionVariants[]`, `tags`, `domain`.
- **`estimation.schema.json`** — generalize `opportunity-estimation` to attach to
  a **use-case** (`subjectRef` = use-case id): `estimations[]`
  (`{ estimator, argumentation, storyPoints, timestamp }`); latest → use-case
  `storyPoints`.
- **`discussion.schema.json`** — a **general thread attachable to any entity**,
  not just an idea. Carries `subjectRef` + `subjectKind`
  (`idea` | `use-case` | `initiative` | `epic`) and `messages[]`
  (`{ role: user|assistant, text, author, timestamp }`). One
  `<slug>.discussion.json` sits beside whatever entity it belongs to.

### Reused, not redefined

- Each backlog's **graph projection** (both `discovery` and `delivery` kinds)
  reuses the existing **GraphView** contract (`graph-view.schema.json`,
  `*.glens.json`, #011). Nodes = ideas / use-cases / epics; links = relations
  (`inspired-by`, `duplicate`, `split-into`, `merged-with`) **plus** the new
  **`implements`** (use-case → idea) edge. Confirm `link.rel` covers `implements`;
  extend only if needed. `backlog.schema.json` (above) holds the metadata; the
  graph is a derived `*.glens.json`.

### Out of scope (follow-up)

- Java / Python bindings (schemas + TS reader consume these now).
- `RemoteStore` / multi-tenant server semantics (headers reserved, no behavior).
- Global cross-venture product/team catalog (future federation/SaaS layer).
- External-system backlog sync mapping (Jira/ADO/Redmine — future, ptx-pft
  provider abstraction).

## Tasks

- [x] `venture.schema.json` (supersede `discovery.schema.json`) + example
- [x] `initiative.schema.json` (with `ideaRefs[]`) + example
- [x] `use-case.schema.json` (storyPoints, productRef, implementsIdeaRefs[]) + example
- [x] `product.schema.json` + example
- [x] `team.schema.json` (registry) + example
- [x] `backlog.schema.json` (first-class: kind, optional teamRef, membership) + example
- [x] `epic.schema.json` (backlogRef, initiativeRefs[], useCaseRefs[]) + example
- [x] Revise `opportunity.schema.json`: `complexity` replaces `estimate.storyPoints`
- [x] Generalize `estimation.schema.json` to a use-case `subjectRef`
- [x] Confirm/extend GraphView `link.rel` for `implements`; document backlog → `*.glens.json`
      (no extension needed — `link.rel` is a free-form string; `implements` documented)
- [x] Full `ai-in-HR.venture` example set (venture, 1 initiative, 2 ideas, 2 use-cases,
      1 product, 1 team, 1 delivery backlog + 1 epic, discussion + estimation)
      with `teams/` and `backlogs/` as siblings — validated against schemas
- [x] README: add v2 schema + example rows; note #014 → #015 supersession
      (`contract/README.md` + new `contract/schemas/README.md`)
- [x] `make` validation green (`make validate-contract`)

## Acceptance Criteria

1. All v2 schemas exist, Draft 2020-12, with `$id` + `$version`.
2. The `ai-in-HR.venture` example set validates against the schemas.
3. Both backlogs validate against the **existing** GraphView schema (no duplicate
   graph schema); an `implements` edge is expressible.
4. `opportunity.complexity` is present; `estimate.storyPoints` is gone from the idea.
5. `use-case.storyPoints` is the rollup; `estimation` attaches to the use-case.
6. Every `*Ref` field follows the linking principle (volatile → stable, single owner).
7. README lists the v2 schemas/examples and records that #015 extends #014.

## Open Questions

1. **`complexity` scale** — t-shirt (`xs|s|m|l|xl`) vs. integer 1–5? (Proposal:
   t-shirt, so it reads as coarse and stays visually distinct from numeric SP.)
2. **Use-case identity** — is the tool use-case the *same* entity as a formal
   `UC-<COMPONENT>-<NNN>` document (USE-CASE-METHODOLOGY), or a lightweight entity
   that only references one via `ucDocRef`? (Proposal: lightweight + optional ref.)
3. **Epic `storyPoints`** — computed-on-read vs. cached rollup field?
4. **Product/Team `id` stability** — convention for cross-venture identity now that
   both live inside the venture (future federation joins on `id`).

## References

- [ADR-008: Opportunity Management](https://github.com/CassandraGargoyle/portunix-architecture)
  (`docs/adr/ADR-008-opportunity-management.md`) — to be updated for v2
- #014 Opportunity Management contracts v1 (extended here)
- [GraphView contract](../../../../contract/schemas/graph-view.schema.json) — reused for both backlogs
- v2 model: `portunix-architecture` →
  `docs/architecture/brainstorming/opportunity-management/journal-20260710-01.json`
