# Issues Documentation & Tracking

This directory contains detailed documentation for all issues, feature requests, and development planning.

## Dual Numbering System

We use a dual numbering system to separate internal development tracking from public GitHub issues:

- **Internal**: All issues (bugs, security, features) tracked in `internal/` with sequential numbering (#001, #002, etc.)
- **Public**: Selected features and enhancements published to GitHub with PUB- prefix (PUB-001, PUB-002, etc.)

## Directory Layout

- Active issues (`new`, `open`, `in-progress`, `blocked`, `on-hold`) live in `docs/issues/internal/`.
- Completed issues (`implemented`, `closed`) are archived in `docs/issues/internal/done/` — moved there as the last step of issue close.

## Internal Issues — Active

| Internal | Public | Title | Status | Priority | Type | Labels |
| -------- | ------ | ----- | ------ | -------- | ---- | ------ |
| [#013](internal/013-typescript-preferences.md) | - | User preferences storage — TypeScript first implementation | 📋 Open | Medium | Feature | typescript, react, preferences, settings, multi-language |

## Internal Issues — Done

| Internal | Public | Title | Status | Priority | Type | Labels |
| -------- | ------ | ----- | ------ | -------- | ---- | ------ |
| [#017](internal/done/017-product-vendor-ownership.md) | - | Product schema — optional vendor / owner organization reference | ✅ Implemented | Medium | Enhancement | contract, schema, product, opportunity-management, federation |
| [#016](internal/done/016-discussion-contract-v3-structured.md) | - | Discussion contract v3.0.0 — structured, multi-actor threads | ✅ Implemented | Medium | Feature | contract, schema, discussion, multi-actor, provenance, breaking-change |
| [#015](internal/done/015-opportunity-management-v2-entities.md) | - | Opportunity Management v2 — Venture/Initiative/Use-Case/Epic/Team/Product contracts | ✅ Implemented | Medium | Feature | contract, schema, opportunity-management, use-case, iso16355, pft |
| [#014](internal/done/014-opportunity-management-contracts.md) | - | Opportunity Management contract schemas | 🗄️ Archived (superseded by #015) | Medium | Feature | contract, schema, opportunity-management, iso16355, pft |
| [#012](internal/done/012-typescript-log-module.md) | - | Add TypeScript (`typescript/`) module with `log` package | ✅ Implemented | Medium | Feature | typescript, react, log, logging, multi-language |
| [#011](internal/done/011-graph-view-contract.md) | - | Graph View Contract: JSON Schema + file extension for the 3D node-graph viewer | ✅ Implemented | Medium | Feature | contract, schema, graph, visualization |
| [#001](internal/done/001-github%20packages.md) | - | Github packages (design rationale) | ✅ Documented | Medium | Feature | architecture, maven, github-packages |
| [#002](internal/done/002-github-packages-publishing.md) | [GH#6](https://github.com/cassandragargoyle/api/issues/6) | GitHub Packages publishing workflow | ✅ Implemented | Medium | Feature | ci, maven, github |
| [#003](internal/done/003-fix-persistance-typo.md) | - | Fix "persistance" typo to "persistence" | ✅ Implemented | Low | Bug Fix | refactoring, naming |
| [#004](internal/done/004-opentelemetry-telemetry-provider.md) | - | OpenTelemetry TelemetryProvider utility class | ✅ Implemented | Medium | Feature | observability, opentelemetry, tracing |
| [#005](internal/done/005-python-telemetry-provider.md) | - | Python TelemetryProvider shared module | ✅ Implemented | Medium | Feature | observability, opentelemetry, tracing, python |
| [#006](internal/done/006-restructure-multi-language-layout-mvp.md) | - | Restructure multi-language layout + MVP contracts | ✅ Implemented | High | Feature | architecture, restructuring, mvp, unified-platform |
| [#007](internal/done/007-remove-persistence-module.md) | - | Remove persistence module | ✅ Implemented | Medium | Feature | architecture, cleanup, unified-platform |
| [#008](internal/done/008-python-build-and-publish-pipeline.md) | - | Python build and publish pipeline | ✅ Implemented | Medium | Feature | python, ci, build, distribution |
| [#009](internal/done/009-plugin-platform-contract-schemas.md) | - | Plugin Platform Contract Schemas | ✅ Implemented | High | Feature | architecture, contract, plugin-platform |
| [#010](internal/done/010-fuzzy-package-levenshtein.md) | - | Add `fuzzy` package with Levenshtein distance (Java, Go, Python) | ✅ Implemented | Medium | Feature | feature, fuzzy, algorithms, java, go, python, multi-language |

## Public Issues

_No public-only issues yet._

## Directory Structure

```text
docs/issues/
├── README.md              # This file - main tracking table
├── internal/              # Active internal issues (not yet closed)
│   ├── 001-*.md
│   └── done/              # Archive of completed (implemented/closed) issues
│       ├── 002-*.md
│       └── ...
└── public/
    └── mapping.json       # Mapping between internal and public issue numbers
```

## Usage

### Creating New Issues

1. **Internal Issue (all types):**
   - Create file: `internal/{next-number}-{short-title}.md`
   - Update the **Active** table in this README with the issue entry
   - Set Public column to `-` initially

2. **Publishing to GitHub (features/enhancements only):**
   - Assign next PUB- number in mapping.json
   - Update Public column in this README
   - Create GitHub issue with PUB- number
   - Never publish: bugs, security issues, internal tasks

### Closing an Issue

When an issue reaches `✅ Implemented` (or is otherwise closed), the last step of the close workflow is:

1. `git mv docs/issues/internal/{file}.md docs/issues/internal/done/`
2. Move the row from the **Active** table to the **Done** table in this README and update the link path to `internal/done/...`.
3. Commit the archive move together with the status change.

Rationale: `ls docs/issues/internal/` then shows only active work, matching the signal the operator expects. Rejected alternatives (status-per-folder, year-based archive) would cause churn or fragmentation for little gain.

### Issue Types

- **Feature**: New functionality (can be public)
- **Enhancement**: Improvement to existing features (can be public)
- **Bug Fix**: Fixing broken functionality (internal only)
- **Security**: Security-related issues (internal only)
- **Plugin**: Plugin-specific features (selective public)

### Status Legend

- 📋 Open - Issue is open and needs work
- 🔄 In Progress - Issue is being actively worked on
- ✅ Implemented - Issue has been completed and implemented (file lives in `internal/done/`)
- ❌ Closed - Issue has been closed without implementation (file lives in `internal/done/`)
- ⏸️ On Hold - Issue is temporarily paused

### Priority Legend

- **Critical** - Must be fixed immediately
- **High** - Important feature or significant bug
- **Medium** - Nice to have feature or minor bug
- **Low** - Enhancement or cosmetic issue

## Publishing Guidelines

✅ **Can be published to GitHub:**

- New features
- Enhancements
- Feature requests
- Roadmap items
- Success stories

❌ **Keep internal only:**

- Bug reports and fixes
- Security vulnerabilities
- Performance issues
- Critical errors
- Internal refactoring
- Technical debt
