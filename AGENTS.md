# AGENTS.md

Instructions for AI coding assistants working in this repository.

## Project Overview

- **Project**: CassandraGargoyle API
- **Purpose**: Multi-language shared library with entities, utilities, and base abstractions for CassandraGargoyle projects, plus language-independent contracts
- **Repository**: <https://github.com/cassandragargoyle/api>
- **Languages**: Java 21 (Maven), Python 3.11+ (uv), Go 1.22+, TypeScript (npm, Node 22+)
- **Platforms**: Linux, Windows
- **License**: MIT (see [LICENSE](LICENSE))

Human-oriented documentation: [README.md](README.md), [CONTRIBUTING.md](CONTRIBUTING.md), [docs/GLOSSARY.md](docs/GLOSSARY.md).

## Project Structure

| Path | Content |
| ---- | ------- |
| `java/` | Java module `org.cassandragargoyle.api` (Maven, NetBeans Platform, Checkstyle) |
| `python/` | Python package `cassandragargoyle.api` (sources in `python/src`, tests in `python/tests`) |
| `go/` | Go module `github.com/cassandragargoyle/api/go` |
| `typescript/` | npm package `@cassandragargoyle/api` (sources in `typescript/src`, tests in `typescript/test`) |
| `contract/` | Language-independent contracts: `schemas/` (JSON Schema Draft 2020-12), `examples/`, `proto/` |
| `docs/adr/` | Architecture Decision Records |
| `docs/contributing/` | Code style, testing, Git, and issue guidelines |
| `docs/issues/` | Issue write-ups named `NNN-short-name.md`; finished ones in `docs/issues/done/` |
| `scripts/` | Helper scripts (for example `validate-contracts.mjs`) |
| `.claude/` | Claude Code roles, commands, and skills |

Each language module builds and publishes independently. The top-level `Makefile` runs build, test, and lint for all of them.

## Build, Test, and Lint

Run all commands from the repository root. `make help` lists every target.

| Task | All modules | Java | Python | Go | TypeScript |
| ---- | ----------- | ---- | ------ | -- | ---------- |
| Build | `make build` | `make build-java` | `make build-python` | - | `make build-typescript` |
| Test | `make test` | `make test-java` | `make test-python` | `make test-go` | `make test-typescript` |
| Lint | `make lint` | `make lint-java` | `make lint-python` | `make vet-go` | `make lint-typescript` |

Other targets:

- `make validate-contract` - validate `contract/examples/` against `contract/schemas/`
- `make lint-md` - Markdown lint (`markdownlint-cli2`), also part of `make lint`
- `make venv` - create `python/.venv` and install the dev dependencies
- `make lock` - refresh `python/uv.lock` after a dependency change in `python/pyproject.toml`

Notes:

- `make lint` does not run `make vet-go`, run it separately for Go changes
- `make build` does not build Go, the Go module has no build artifact
- CI (`.github/workflows/build.yml`) runs only the Java and Python tests, so run the Go, TypeScript, and contract checks locally

## Before You Finish a Change

- Run the test and lint targets of every module you changed
- Run `make validate-contract` after any change in `contract/`
- Run `make lint-md` after any change in Markdown files
- Add or update tests for new features and bug fixes (coverage target: 80%)
- Update `CHANGELOG.md` for changes that are visible to users
- Report test or lint failures as they are, do not hide them

## Working Rules

### Safety

- Never run destructive commands without confirmation
- Warn about possible data loss before a risky operation
- Ask for explicit confirmation before an operation that can lose implemented code

### Language

- Code, comments, messages, labels, constants, documentation, commit messages, and PR descriptions are in English
- Use simple, clear English, and avoid idioms and culture-specific references
- Comments are written as phrases without an ending period
- Chat with a team member can be in their preferred language

### Code

- Explore the existing code before you add a new implementation
- Follow the existing conventions and patterns
- Prefer editing existing files over creating new ones
- Keep changes small and focused on the task
- Follow the style guide of the language: [Java](docs/contributing/CODE-STYLE-JAVA.md), [Python](docs/contributing/CODE-STYLE-PYTHON.md), [Go](docs/contributing/CODE-STYLE-GO.md), [C++](docs/contributing/CODE-STYLE-CPP.md)
- Follow the testing guide of the language: [Java](docs/contributing/TESTING-JAVA.md), [Python](docs/contributing/TESTING-PYTHON.md), [Go](docs/contributing/TESTING-GO.md), [C++](docs/contributing/TESTING-CPP.md)
- Follow [NAMING-CONVENTIONS.md](docs/contributing/NAMING-CONVENTIONS.md) and [TERMINOLOGY.md](docs/contributing/TERMINOLOGY.md)

### Cross-Language Parity

- Features that exist in more than one module (`fuzzy`, `log`, `telemetry`) must behave the same in every module
- When you change such a feature in one language, change the other languages too, or state clearly what is left
- The module versions are kept the same in `java/pom.xml`, `python/pyproject.toml`, and `typescript/package.json`

### Contracts

- Every schema in `contract/schemas/` needs at least one valid example in `contract/examples/`
- Update `contract/README.md` and `contract/schemas/README.md` when you add or change a schema
- Other projects depend on the contracts, so point out every breaking change

### TODO Comments

- Format: `TODO:NNN [INITIALS]: description`
- Numbers are sequential per file (001, 002, 003)
- Use `TODO:XXX` as a temporary placeholder, then ask for the final number
- Team initials are unique, a collision is resolved with one more letter (JS, then JSm)

### System Information

- Use `portunix system info` for OS detection when Portunix is available
- Use manual detection only when Portunix is not available
- Do not write custom OS detection scripts

## Git and Pull Requests

- The main branch is `main`, never commit to it directly
- Branch names: `feature/<issue-number>-<short-name>`, `fix/<issue-number>-<short-name>`, `docs/<short-name>`
- Commit messages follow Conventional Commits with an optional scope, for example `feat(contract): add epic schema` or `fix(ci): pin setup-uv`
- Do not add `Co-Authored-By` lines for AI tools to commits
- Do not add `Generated with [Claude Code]` or similar AI signatures to code, files, commits, or PR descriptions
- Commit and push only when the user asks
- Details: [GIT-WORKFLOW.md](docs/contributing/GIT-WORKFLOW.md)

## Issues, ADRs, and Documentation

- GitHub is the source of truth for the issue list, and GitHub assigns the issue number
- The long write-up is in `docs/issues/NNN-short-name.md`, where `NNN` is the GitHub issue number
- `docs/issues/` is public, do not put sensitive content there
- Record important architecture decisions as an ADR in `docs/adr/`
- Details: [ISSUE-MANAGEMENT.md](docs/contributing/ISSUE-MANAGEMENT.md), [MARKDOWN-FRONTMATTER.md](docs/contributing/MARKDOWN-FRONTMATTER.md)

## Translations

- `.translated/` contains temporary translations for team members, it is not tracked and can be generated again
- Use ISO 639-1 language codes (`cs`, `de`, `fr`)
- The English file is always the authoritative version
- Details: [TRANSLATION-WORKFLOW.md](docs/contributing/TRANSLATION-WORKFLOW.md)

## More Guidelines

- [AI-ASSISTANTS.md](docs/contributing/AI-ASSISTANTS.md) - how the team uses AI assistants
- [BUG-REPORTING.md](docs/contributing/BUG-REPORTING.md) - bug report format
- [GITIGNORE.md](docs/contributing/GITIGNORE.md) - rules for `.gitignore`
- [TOOLS-RECOMMENDATIONS.md](docs/contributing/TOOLS-RECOMMENDATIONS.md) - recommended tools
