---
title: Issue Management
description: Workflow for creating, tracking, and archiving issues in Api — GitHub-first, where GitHub assigns the number and each issue keeps a long-form mirror in docs/issues/internal/N-*.md (archived to done/ on completion). No overview tables.
category: contributing-guide
status: active
language: en
last_updated: 2026-07-19
---

# Issue Management for Api

## Principle: GitHub-First

**GitHub is the single source of truth for the issue list.** The issue is
created on GitHub first, so **GitHub assigns the number `N`**. The GitHub issue
carries only the *basics* — a short description plus a link to the detail file.
The full write-up lives in the repo at `docs/issues/internal/N-name.md`, where
the filename number **equals the GitHub issue number**.

> There are **no overview tables** in `docs/issues/` — a shared Markdown table
> edited by everyone caused constant merge conflicts and was removed. Status,
> assignee, labels, and milestone live on GitHub; the repo keeps only the
> long-form write-up per issue.

The `/create-issue` skill automates this flow.

## Workflow

1. **Create the GitHub issue** (GitHub assigns `N`). Keep the body minimal —
   a one-line summary plus a link placeholder:

   ```bash
   gh issue create --repo cassandragargoyle/api \
     --title "Short descriptive title" \
     --body "One-line summary.

   📄 Full details: docs/issues/internal/N-name.md" \
     [--label <type>,<priority>,<component>]
   ```

   `gh issue create` prints the URL — take `N` from it (`.../issues/7` → `N=7`).
   Never invent a number. GitHub issues and pull requests share one counter, so
   numbers may skip where a PR consumed one.

2. **Write the detail file** `docs/issues/internal/N-name.md` (problem,
   acceptance criteria, testing, related issues). Use `N` in the filename and in
   the `# Issue #N:` heading.

3. **Update the GitHub body** with a working link once `N` and the filename are
   known (blob URL on `main`):

   ```bash
   gh issue edit N --repo cassandragargoyle/api \
     --body "One-line summary.

   📄 [Full details](https://github.com/cassandragargoyle/api/blob/main/docs/issues/internal/N-name.md)"
   ```

4. **Reference `N`** in branches and commits: `feat/N-name`, `feat(#N): …`,
   `closes #N`.

5. **Archive on completion**: `gh issue close N` and
   `git mv docs/issues/internal/N-name.md docs/issues/internal/done/`.

## Detail file template

```markdown
# Issue #N: <Title>

**Type**: Feature | Enhancement | Bug Fix | Security | Task
**Priority**: Critical | High | Medium | Low
**Status**: 📋 Open
**Created**: <YYYY-MM-DD>
**GitHub**: #N
**Component**: <java | python | go | typescript | contract | build-infra | core | docs>
**Related**: #<...>, ADR-<...>

## Summary
<1–3 sentences>

## Problem Description / Motivation
...

## Scope
### In scope
### Out of scope (follow-up issues)

## Acceptance Criteria
1. ...

## Testing
...
```

## Labels

Assign labels on the GitHub issue (create them once with `gh label create`):

- **Type**: `bug`, `enhancement`, `documentation`, `question`, `task`
- **Priority**: `critical`, `high`, `medium`, `low`
- **Component** — one per language module or area, matching the top-level
  directory: `java`, `python`, `go`, `typescript`, `contract`. Cross-cutting
  areas use `build-infra`, `core`, or `docs`. This is how issues are filtered:

  ```text
  is:issue is:open label:typescript
  ```

- **Status** (optional): `needs-triage`, `in-progress`, `blocked`,
  `ready-for-review`
- **Platform** (optional): `windows`, `linux`, `macos`, `cross-platform`

Create a component label once:

```bash
gh label create typescript --repo cassandragargoyle/api \
  --color 1d76db --description "Issues for the TypeScript module"
```

## File structure

```text
docs/issues/
├── README.md                 # Process pointer only — NO overview table
└── internal/                 # Active issue detail files (N = GitHub issue number)
    ├── N-name.md             # Long-form write-up
    └── done/                 # Archived (✅ Implemented / ❌ Closed) issues
        └── N-*.md
```

## Numbering note

- Filenames from the adoption of this model onward use the **GitHub issue
  number** `N`. Legacy internal-only numbers (`001`–`017`, in `done/`) were
  never mirrored to GitHub and are kept for history only.
- Never invent a number — always take the one GitHub returns.

## Best practices

- Search existing GitHub issues before opening a new one.
- Keep the GitHub body short; put depth in the detail file.
- Assign type + priority + component labels immediately.
- Include reproduction steps for bugs and acceptance criteria for features.
- All issue text in **English** (project convention).

---

*Last updated: 2026-07-19 — switched to the GitHub-first model (Model A); removed overview tables and local-first numbering.*
