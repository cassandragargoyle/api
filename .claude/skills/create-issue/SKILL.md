---
name: create-issue
description: 'Create a new issue GitHub-first — the GitHub issue is created first (GitHub assigns the number), then a detailed write-up is added to docs/issues/internal/N-*.md. Use when the user wants to create/open an issue, feature request, or bug. Examples: "open an issue …", "/create-issue typescript preferences store", "create an issue for …".'
argument-hint: [short issue description]
allowed-tools: Read, Glob, Grep, Bash, AskUserQuestion, Write, Edit
user-invocable: true
---

# Create issue (GitHub-first)

Create a new issue in the Api project.

**Argument:** $ARGUMENTS

## Principle

**GitHub is the single source of truth for the issue list.** GitHub assigns the
number on creation. The GitHub issue holds only the *basics* (title + short
description + link to the detail file). Details live in
`docs/issues/internal/N-*.md`, where `N` = the GitHub issue number.

**No overview tables** — the list lives on GitHub.
Full methodology: `docs/contributing/ISSUE-MANAGEMENT.md`.

## Instructions

### STEP 1: Gather inputs

From the argument / context (or via `AskUserQuestion` if missing) determine:

- **Title** — short, descriptive, in English (no number; GitHub adds it)
- **Slug** — kebab-case for the filename (derive from the title, e.g. `typescript-preferences-store`)
- **Type**: Feature | Enhancement | Bug Fix | Security | Task
- **Priority**: Critical | High | Medium | Low
- **Component** — by top-level module: `java`, `python`, `go`, `typescript`,
  `contract`; cross-cutting: `build-infra`, `core`, `docs`. Issues are filtered
  by this label (`label:typescript`)
- **Short description** (1–3 sentences) — goes into the GitHub issue body
- **Detailed content** — goes into the local `.md` (problem, steps, acceptance criteria, tests)

All text in **English** (project convention).

### STEP 2: Preflight — check `gh`

```bash
gh --version && gh auth status
```

If not authenticated, stop and prompt the user: `gh auth login` (feel free to use the `!` prefix).

### STEP 3: Create the GitHub issue (GitHub assigns the number)

Keep the body **minimal** — short description + a link placeholder (filled in at STEP 5):

```bash
REPO="cassandragargoyle/api"
gh issue create --repo "$REPO" \
  --title "<TITLE>" \
  --body "<SHORT DESCRIPTION>

📄 Full details: docs/issues/internal/<N>-<slug>.md" \
  [--label <type>,<priority>,<component>]
```

`gh issue create` prints the URL — take the **number `N`** from it
(`.../issues/7` → `N=7`). Only pass labels that already exist on GitHub;
otherwise create them first (`gh label create <name> --repo "$REPO" --color 1d76db`).

### STEP 4: Write the detail file

Create `docs/issues/internal/<N>-<slug>.md` with the full content. Skeleton:

```markdown
# Issue #<N>: <Title>

**Type**: <type>
**Priority**: <priority>
**Status**: 📋 Open
**Created**: <YYYY-MM-DD>
**GitHub**: #<N>
**Component**: <component>
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

### STEP 5: Add the exact link back to the GitHub issue

Now that `N` and the filename are known, update the body with a working link
(blob URL on `main`):

```bash
URL="https://github.com/$REPO/blob/main/docs/issues/internal/<N>-<slug>.md"
gh issue edit <N> --repo "$REPO" \
  --body "<SHORT DESCRIPTION>

📄 [Full details](${URL})"
```

> The link starts working once the file is on `main`.

### STEP 6: Summary

Show the user:

```text
Issue #<N> created.

GitHub:  https://github.com/cassandragargoyle/api/issues/<N>
Detail:  docs/issues/internal/<N>-<slug>.md
Type/Priority: <type> / <priority>

Next steps:
  git switch -c feat/<N>-<slug>
  # commit:  feat(#<N>): <title>   (closes via: closes #<N>)
```

## Notes

- **Never invent** the number — GitHub always assigns it. GitHub issues and PRs
  share one counter, so numbers may skip.
- To archive a finished issue: `gh issue close <N>` +
  `git mv docs/issues/internal/<N>-*.md docs/issues/internal/done/`.
- Legacy internal numbers `001`–`017` (in `done/`) were never mirrored to GitHub —
  they exist for history only.
