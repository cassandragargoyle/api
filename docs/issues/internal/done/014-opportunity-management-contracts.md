# Issue #14: Opportunity Management contract schemas

**Type**: Feature
**Priority**: Medium
**Status**: 🗄️ Archived — superseded by #015
**Created**: 2026-07-08
**Archived**: 2026-07-10
**Labels**: contract, schema, opportunity-management, iso16355, pft, multi-language
**Repository**: Api
**Related**: ADR-008 (Opportunity Management — portunix-architecture),
**GitHub**: #14
portunix #196 (ptx-pft `pft ideas`), portunix-vscode #092 (Pilot gallery),
Issue #011 (Graph View Contract — reused for the backlog graph)
**Extended by**: #015 (v2 model — Venture/Initiative/Use-Case/Epic/Team/Backlog/Product;
`.discovery` → `.venture`, idea `complexity` replaces story points)

> **Superseded by [#015](015-opportunity-management-v2-entities.md) (2026-07-10).**
> The v1 single-tier model in this issue was never shipped as schemas; the v2
> two-spine model (Venture / Initiative / Idea + Team / Epic / Use-Case) replaces
> it. `.discovery` → `.venture`, `discovery.schema.json` → `venture.schema.json`,
> and idea `estimate.storyPoints` → `complexity`. This issue is retained for
> historical context only; implement #015.

## Summary

Define the language-independent JSON Schemas for the **Opportunity Management**
model introduced in ADR-008. The model is a directory-based, git-tracked set of
JSON documents authored by `ptx-pft` (`pft ideas`) and rendered by the Pilot UI.
These schemas are the source of truth for both the Go writer and the TypeScript
reader, and keep the data **SaaS-ready** for a later web/DB migration.

## Motivation

ADR-008 specifies a `.discovery` directory that owns one backlog of
opportunities; each opportunity has a data file, a discussion thread, and an
estimation history. The Go side (`ptx-pft`) writes these files and the
TypeScript side (Pilot) reads them. Without shared schemas the two drift. The
backlog's list+relations shape is already covered by the existing **GraphView**
contract (`*.glens.json`, #011) and must be reused, not duplicated.

## Scope

### In scope (this issue)

New schemas under `contract/schemas/`:

- **`opportunity.schema.json`** — a single opportunity (card data). Fields per
  ADR-008: `schemaVersion`, `tenantId`, `workspaceId`, `id`, `slug`, `title`,
  `origin` (`ai-funnel` | `iso16355`), `status`
  (`new` | `ready` | `evaluated` | `poc` | `done` | `rejected`), `problem`,
  `benefit`, `aiSolution`, `solutionVariants[]`
  (`{ type: ai|automation|process-change|training, desc, selected }`),
  `scores` (`businessValue`, `feasibility`, `dataAvailability`, `risk`,
  `pocSpeed` — integer 1–5 or null), `estimate.storyPoints` (rollup = latest),
  `interest` (`count`, `voters[]` — demand, 💡), `volunteers` (`count`,
  `people[]` — supply / "I'd work on this", 🔨), `aiAmbassador`
  (optional: `name`, `persona`, `avatar`, `assignedAt`), `tags[]`, `domain`,
  `author`, `createdAt`, `updatedAt`.
- **`discovery.schema.json`** — initiative metadata: `schemaVersion`, `id`,
  `name`, `description`, `approach` (`iso16355` | `ai-funnel` | `mixed`),
  `status` (`active` | `closed` | `archived`), timestamps.
- **`opportunity-discussion.schema.json`** — chat thread for one opportunity:
  `opportunityId`, `messages[]` (`{ role: user|assistant, text, author,
  timestamp }`).
- **`opportunity-estimation.schema.json`** — planning-poker estimation history:
  `opportunityId`, `estimations[]`
  (`{ estimator, argumentation, storyPoints, timestamp }`).
- Contract **examples** under `contract/examples/` for each schema (a small
  `ai-in-HR.discovery` set: one discovery, two opportunities with discussion +
  estimation).
- README entries for the new schemas and examples.

### Reused, not redefined

- **`backlog.json`** = the opportunity list + relations. It **reuses the existing
  GraphView contract** (`graph-view.schema.json`, `*.glens.json`, #011): nodes =
  opportunities, links = relations (`rel` ∈
  `inspired-by` | `duplicate` | `split-into` | `merged-with`). Document the
  mapping in the GraphView README rather than adding a new schema. Confirm the
  relation vocabulary fits `link.rel`; extend only if needed.

### Out of scope (follow-up)

- Java / Python bindings (only schemas + TS reader consume these now).
- `RemoteStore` / multi-tenant server semantics (headers reserved, no behavior).
- Variant B (global backlog with M:N Discovery references) — see ADR-008 open
  questions.

## Tasks

- [ ] `opportunity.schema.json` (Draft 2020-12) + example
- [ ] `discovery.schema.json` + example
- [ ] `opportunity-discussion.schema.json` + example
- [ ] `opportunity-estimation.schema.json` + example
- [ ] Validate the `ai-in-HR.discovery` example set against the schemas
- [ ] Confirm/extend GraphView `link.rel` vocabulary for opportunity relations;
      document the `backlog.json` → `*.glens.json` mapping
- [ ] README: add "Opportunity Management" schema + example rows
- [ ] `make` validation green (schema lint / example validation)

## Acceptance Criteria

1. The four new schemas exist, are Draft 2020-12, and have `$id` + `$version`.
2. A sample `.discovery` example set validates against the schemas.
3. `backlog.json` for the example validates against the **existing** GraphView
   schema (no duplicate graph schema introduced).
4. `origin` and `solutionVariants` express both ISO 16355 and AI-funnel modes.
5. `scores` are integer 1–5 (or null); `estimate.storyPoints` is present as the
   rollup; `interest.count`/`voters` present.
6. README lists the new schemas and examples.

## Open Questions

1. Are discussion/estimation better as separate schemas (as above) or embedded
   `$defs` referenced from a single `opportunity-bundle` schema?
2. Should `scores` criteria be a fixed set (ChatGPT five) or an open map to allow
   ISO 16355-specific criteria per Discovery? (ADR-008 open question.)
3. Suffix convention for opportunity files (`*.opportunity.json` etc.) — document
   in the contract README, or leave to the ptx-pft/pilot implementations?

## References

- [ADR-008: Opportunity Management](https://github.com/CassandraGargoyle/portunix-architecture)
  (`docs/adr/ADR-008-opportunity-management.md`)
- [GraphView contract](../../../../contract/schemas/graph-view.schema.json) — reused for `backlog.json`
- Design mock: `portunix-architecture` →
  `docs/architecture/brainstorming/opportunity-management/opportunity-card-mockup.svg`
