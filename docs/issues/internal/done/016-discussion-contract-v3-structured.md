# Issue #016: Discussion contract v3.0.0 — structured, multi-actor threads

**Type**: Feature
**Priority**: Medium
**Status**: ✅ Implemented
**Created**: 2026-07-10
**Closed**: 2026-07-10
**Labels**: contract, schema, opportunity-management, discussion, chat, multi-actor, provenance, breaking-change
**Repository**: Api
**Related**: #015 (Opportunity Management v2 entities — `discussion.schema.json`
v2.0.0 introduced there), #014 (Opportunity Management contracts v1),
ADR-008 (Opportunity Management — portunix-architecture),
portunix-vscode #099 (Pilot `VentureDiscussionView` — consumer of this contract),
portunix #197 (ptx-pft `pft ideas` v2 — writer)

## Summary

Evolve `discussion.schema.json` from the thin **v2.0.0** (2-party `user`/`assistant`
messages) to a **v3.0.0 structured, multi-actor discussion** model, agreed in the
2026-07-10 brainstorming session
(`portunix-architecture/docs/architecture/brainstorming/venture-discussion-view/journal-20260710-01.json`).
The contract is the source of truth for the Go writer (`ptx-pft`) and the
TypeScript reader (Pilot `VentureDiscussionView`), and stays **SaaS-ready**.

## Motivation

v2.0.0 only supports a two-party AI bubble chat (`role` ∈ {user, assistant}). Real
team collaboration over a venture entity needs:

- **Multiple actors** — several humans, multiple AI agents (personas), and the system.
- **Typed messages** — comment / question / proposal / decision / ai_summary / system,
  each rendered differently by the UI (bubble vs. decision/proposal/summary card).
- **Threading** — replies form a tree (`parentId`).
- **Provenance** — a message can *produce* a structured object (idea, use-case,
  risk, decision…); the link is dereferenceable (`produced[]`).
- **Team affordances** — reactions, @mentions, attachments, an AI summary.

## Contract changes (v2.0.0 → v3.0.0, breaking)

Bump `$version` to `3.0.0`. Migration is mechanical (`role` → `actor.kind`).

### Top level

- Add `status` — `open | resolved | archived`.
- Add `participants[]` — `{ kind: user|ai_agent|system, id, displayName, avatarUrl? }`.
- Keep `subjectRef`; keep `subjectKind` (`idea|use-case|initiative|epic`).

### Per message (was: `role`, `text`, `author`, `timestamp`)

- `id` (required) — stable message id.
- `parentId?` — parent message id; absent = top-level (full tree, unbounded depth).
- `actor` (required) — `{ kind: user|ai_agent|system, id, displayName }` (replaces `role`).
- `type` (required) — `comment | question | proposal | decision | ai_summary | system`.
- `text` (required) — message body (markdown).
- `reactions[]?` — `{ emoji, actorIds[] }`.
- `mentions[]?` — actor ids referenced in the text.
- `attachments[]?` — `{ kind: file|link, ref, label? }`.
- `produced[]?` — `{ kind: idea|use-case|initiative|epic|decision|risk, ref }` (provenance).
- `timestamp` (required) — ISO 8601.
- Keep objects closed (`additionalProperties: false`).

## Migration (v2 → v3)

- `role: "user"` → `actor.kind: "user"`; `role: "assistant"` → `actor.kind: "ai_agent"`.
- `author` → `actor.id` / `actor.displayName`.
- Every message gets an `id`, a `type` (default `comment`), and (for v2 threads) no `parentId`.
- Provide a short migration note in the schema `README.md` (Opportunity Management section).

## Example

Update `contract/examples/ai-in-HR.venture/records/onboarding-assistant.discussion.json`
(currently v2.0.0) to a rich v3 thread demonstrating: multiple actor kinds, several
message types (incl. a `decision` and an `ai_summary`), a nested reply (`parentId`),
`reactions`, `mentions`, and at least one `produced[]` provenance link.

## Scope

### In scope

- `discussion.schema.json` v3.0.0 (fields above; closed objects; `$version` bump).
- Migrate the `ai-in-HR.venture` example thread to v3.
- Update the schema `README.md` (Opportunity Management section) with the v3 shape
  and the v2→v3 migration note.

### Out of scope (other issues)

- Pilot `VentureDiscussionView` rendering (portunix-vscode #099).
- `ptx-pft` writer support for the new fields (portunix #197).
- Real-time presence / notifications; SaaS backend.

## Acceptance Criteria

1. `discussion.schema.json` `$version` is `3.0.0`; objects remain closed.
2. `messages[].actor.kind` supports `user | ai_agent | system`; `role` is removed.
3. `messages[].type` enum present; `parentId`, `reactions`, `mentions`,
   `attachments`, `produced[]` are defined per the shape above.
4. Top-level `status` and `participants[]` are defined.
5. The `ai-in-HR.venture` example validates against v3 and exercises every new
   feature (typed messages, a thread, reactions, mentions, a `produced[]` link).
6. `README.md` documents the v3 shape and the v2→v3 migration mapping.

## References

- Brainstorming: `portunix-architecture` →
  `docs/architecture/brainstorming/venture-discussion-view/journal-20260710-01.json`
- ChatGPT input: same dir, `20260710-chatgpt-discussion-view.md`
- Mockup: `portunix-vscode/docs/architecture/ui-mockups/venture-discussion-view.svg`
- Consumer: portunix-vscode #099 (`VentureDiscussionView`)
- Prior contract: `discussion.schema.json` v2.0.0 (introduced in #015)
- ADR-008 Opportunity Management (`portunix-architecture/docs/adr/`)
