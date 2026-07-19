# Issues Documentation & Tracking

**GitHub is the single source of truth for the issue list.**
See <https://github.com/cassandragargoyle/api/issues>.

This directory holds only the **long-form write-up** for each issue. There are
**no overview tables** here — status, assignee, labels, and milestone live on
GitHub. A shared Markdown table edited by everyone caused constant merge
conflicts and was removed.

## Model: GitHub-First

The issue is created on GitHub first, so **GitHub assigns the number `N`**. The
GitHub issue carries only a short description plus a link to the detail file. The
full write-up lives at `docs/issues/internal/N-name.md`, where the filename
number **equals the GitHub issue number**.

The `/create-issue` skill automates this flow. Full methodology:
[`docs/contributing/ISSUE-MANAGEMENT.md`](../contributing/ISSUE-MANAGEMENT.md).

## Directory Layout

```text
docs/issues/
└── internal/                 # Active issue detail files (N = GitHub issue number)
    ├── N-name.md             # Long-form write-up
    └── done/                 # Archived issues (moved here on close)
        └── N-*.md
```

- Active issues live in `docs/issues/internal/`.
- On completion: `gh issue close N` and
  `git mv docs/issues/internal/N-name.md docs/issues/internal/done/`.

## Numbering note

Filenames from the adoption of the GitHub-first model onward use the **GitHub
issue number**. Legacy internal-only numbers `001`–`017` (in
`internal/done/`) were never mirrored to GitHub and are kept for history only.
