---
title: Issue Management
description: Workflow for creating, tracking, and archiving issues in Api — GitHub-first, where GitHub assigns the number and each issue keeps a long-form write-up in docs/issues/NNN-*.md (archived to docs/issues/done/ on completion). No overview tables.
category: contributing-guide
status: active
language: en
last_updated: 2026-10-09
---

# Issue Management for Api

## Principle: GitHub-First

**GitHub is the single source of truth for the issue list.** The issue is
created on GitHub first, so **GitHub assigns the number `N`**. The GitHub issue
carries only the *basics* — a short description plus a link to the detail file.
The full write-up lives in the repo at `docs/issues/NNN-name.md`, where `NNN` is
the GitHub issue number zero-padded to three digits.

> There are **no overview tables**, no README index, and no mapping files in
> `docs/issues/` — a shared Markdown table edited by everyone caused constant
> merge conflicts and was removed. Status, assignee, labels, and milestone live
> on GitHub; the repo keeps only the long-form write-up per issue.

- Open issues: <https://github.com/cassandragargoyle/api/issues>
- Closed issues: <https://github.com/cassandragargoyle/api/issues?q=is%3Aissue+is%3Aclosed>

The `/create-issue` skill automates this flow.

## Workflow

1. **Create the GitHub issue** (GitHub assigns `N`). Keep the body minimal —
   a one-line summary plus a link placeholder:

   ```bash
   gh issue create --repo cassandragargoyle/api \
     --title "Short descriptive title" \
     --body "One-line summary.

   Full details: docs/issues/NNN-name.md" \
     [--label <type>,<priority>,<component>]
   ```

   `gh issue create` prints the URL — take `N` from it (`.../issues/7` → `N=7`,
   `NNN=007`). Never invent a number. GitHub issues and pull requests share one
   counter, so numbers may skip where a PR consumed one.

2. **Write the detail file** `docs/issues/NNN-name.md` (problem, acceptance
   criteria, testing, related issues). Use `NNN` in the filename and `N` in the
   `# Issue #N:` heading.

3. **Update the GitHub body** with a working link once `N` and the filename are
   known (blob URL on `main`):

   ```bash
   gh issue edit N --repo cassandragargoyle/api \
     --body "One-line summary.

   📄 [Full details](https://github.com/cassandragargoyle/api/blob/main/docs/issues/NNN-name.md)"
   ```

   The link starts working once the file reaches `main`.

4. **Reference `N`** in branches and commits: `feature/N-name`, `feat(#N): …`,
   `closes #N`.

5. **Archive on completion**:

   ```bash
   git mv docs/issues/NNN-name.md docs/issues/done/NNN-name.md
   gh issue close N --repo cassandragargoyle/api --comment "Implemented in <version/commit>."
   ```

   Update the GitHub body link to the `done/` path when archiving.

## Publication and Sensitive Content

`docs/issues/` is **public** — it is pushed to GitHub together with the rest of
the repository. Write every issue as public content:

- No customer names, business relationships, deals, or priorities tied to a customer
- No third-party or employer project names and internal identifiers
- No local paths such as `/home/<user>/…` — use repository-relative paths
- No internal hostnames, IP addresses, credentials, or tokens (placeholders like
  `<token>` or `example.com` are fine)

The preflight check (`scripts/github_01_preflight_check.py`, patterns in
`scripts/github_publish.json` under `sensitive_content.patterns`) scans for
sensitive patterns. Content that must stay internal belongs in `docs/private/`,
not in an issue file.

## Detail file template

```markdown
# Issue #N: <Title>

**Type**: Feature | Enhancement | Bug Fix | Security | Task
**Priority**: Critical | High | Medium | Low
**Status**: Open
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

### Formatting rules

- **No icons or emoji** in issue files: not in headings and not in the `Status`
  field (`Open`, not `📋 Open`)
- Older issues using emoji are legacy style — do not copy it into new issues

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
├── NNN-name.md               # Active issue write-up (NNN = GitHub issue number)
└── done/                     # Archived (Implemented / Closed) issues
    └── NNN-*.md
```

## Numbering note

- Files use three-digit zero padding (`013-…`, `042-…`); GitHub references do
  not (`#13`, `#42`).
- Never invent a number — always take the one GitHub returns.
- Legacy internal `#010` (`fuzzy` package) collided with pull request #10 and
  was renumbered to GitHub issue #43. Older documents (CHANGELOG) still use
  `#010`.
- GitHub issue #6 is a duplicate of #19 (GitHub Packages publishing workflow);
  the write-up is `done/019-github-packages-publishing.md`.
- Until 2026-10 the write-ups lived in `docs/issues/internal/`. Older documents
  and GitHub issue bodies may still reference that path.

## Best practices

- Search existing GitHub issues before opening a new one.
- Keep the GitHub body short; put depth in the detail file.
- Assign type + priority + component labels immediately.
- Include reproduction steps for bugs and acceptance criteria for features.
- All issue text in **English** (project convention).

---

*Last updated: 2026-10-09 — write-ups moved from `docs/issues/internal/` to `docs/issues/`; three-digit file numbering; sensitive-content rules.*
