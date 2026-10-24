# Contributing to CassandraGargoyle API

Thank you for considering a contribution. This file is the entry point —
the full set of guidelines lives under
[`docs/contributing/`](docs/contributing/).

## Quick Start

1. **Pick or open an issue** — browse
   [open issues](https://github.com/cassandragargoyle/api/issues) or open
   a new one describing the change you want to make. Non-trivial changes
   should have an issue first so the scope can be agreed on before code
   is written. See
   [docs/contributing/ISSUE-MANAGEMENT.md](docs/contributing/ISSUE-MANAGEMENT.md)
   for issue-tracking conventions.

2. **Fork and clone**

   ```bash
   git clone https://github.com/<your-user>/api.git
   cd api
   ```

3. **Create a feature branch** following the project convention:

   ```bash
   git checkout -b feature/<issue-number>-<short-name>
   # or fix/<issue-number>-<short-name> for bug fixes
   ```

4. **Set up the development environment** — install the toolchains for
   the language module(s) you plan to touch:

   - **Java**: JDK 21 + Maven
   - **Python**: Python 3.11+ + [uv](https://docs.astral.sh/uv/);
     `make venv` provisions `python/.venv` and installs dev extras
   - **Go**: Go 1.22+

5. **Build and test**

   ```bash
   make build              # all artifacts (Java JAR + Python wheel/sdist)
   make test               # all language modules (Java + Python + Go)
   make test-java          # Java only (mvn test)
   make test-python        # Python only (pytest)
   make test-go            # Go only (go test ./...)
   make lint-java          # Java Checkstyle
   make lint-python        # Python ruff
   make lint-md            # Markdown lint
   ```

   Run `make help` to see every available target.

6. **Commit and push** using Conventional-Commit-style messages
   (`feat:`, `fix:`, `docs:`, `chore:`, `refactor:`, `test:` …). See
   [docs/contributing/GIT-WORKFLOW.md](docs/contributing/GIT-WORKFLOW.md)
   for details.

7. **Open a Pull Request** from your feature branch into `main`.

## Ground Rules

- All code, comments, commit messages, and PR descriptions are written in
  **English**. Other languages are welcome in private team communication;
  documents under `.translated/` carry temporary localized copies, and
  permanent companion files (e.g. `README.cs.md`) point back at the
  English source as the authoritative version.
- Follow existing conventions — explore the codebase before introducing
  new patterns. Language-specific style guides:
  [Java](docs/contributing/CODE-STYLE-JAVA.md),
  [Python](docs/contributing/CODE-STYLE-PYTHON.md),
  [Go](docs/contributing/CODE-STYLE-GO.md),
  [C++](docs/contributing/CODE-STYLE-CPP.md).
- Write tests for new features and bug fixes. Per-language testing
  guides:
  [TESTING-JAVA.md](docs/contributing/TESTING-JAVA.md),
  [TESTING-PYTHON.md](docs/contributing/TESTING-PYTHON.md),
  [TESTING-GO.md](docs/contributing/TESTING-GO.md),
  [TESTING-CPP.md](docs/contributing/TESTING-CPP.md).
- Do not add `Co-Authored-By` attributions for AI tools in commits.
- Do not add `Generated with [Claude Code]` (or similar AI-tool)
  signatures to source files.

## Detailed Guidelines

The `docs/contributing/` directory contains the full set of project
standards:

- [Issue Management](docs/contributing/ISSUE-MANAGEMENT.md) — issue
  tracking conventions; internal vs public issues
- [Git Workflow](docs/contributing/GIT-WORKFLOW.md) and
  [GitHub Workflow](docs/contributing/GITHUB-WORKFLOW.md)
- [Bug Reporting](docs/contributing/BUG-REPORTING.md)
- [Naming Conventions](docs/contributing/NAMING-CONVENTIONS.md)
- [Terminology](docs/contributing/TERMINOLOGY.md)
- [AI Assistants](docs/contributing/AI-ASSISTANTS.md) — using Claude
  Code and other AI assistants on this codebase
- [Translation Workflow](docs/contributing/TRANSLATION-WORKFLOW.md)
- [`.gitignore` Guidelines](docs/contributing/GITIGNORE.md)
- [Tools Recommendations](docs/contributing/TOOLS-RECOMMENDATIONS.md)

Architectural decisions are recorded in
[`docs/adr/`](docs/adr/).

## Reporting Bugs and Requesting Features

- **Bugs** — open an issue using the bug reporting guidelines in
  [BUG-REPORTING.md](docs/contributing/BUG-REPORTING.md). Include:
  - The relevant module version (from `java/pom.xml`,
    `python/pyproject.toml`, or `go/go.mod`)
  - OS and JDK / Python / Go version
  - Steps to reproduce
  - Expected vs. actual behavior
  - Relevant logs (see the
    [`org.cassandragargoyle.api.log`](java/src/main/java/org/cassandragargoyle/api/log/README.md)
    package for log file location and format)
- **Feature requests** — open an issue describing the use case and the
  motivation. For substantial changes, propose an ADR in
  [`docs/adr/`](docs/adr/) alongside the request.

## Releasing

All modules have the same semver version `MAJOR.MINOR.PATCH`
([ADR-005](docs/adr/005-semantic-versioning.md)). A tag `vMAJOR.MINOR.PATCH`
(for example `v1.1.0`) on `main` starts `.github/workflows/publish.yml`, which
publishes:

- the Java module to GitHub Packages
- the Python module to PyPI
- the TypeScript module to npmjs.com as `@cassandragargoyle/api`

The tag must match `"version"` in `typescript/package.json`, otherwise the npm
job fails. The Go module gets its own tag `go/vMAJOR.MINOR.PATCH`. The job skips a version
that is already on npmjs.com. A manual run of the workflow (`workflow_dispatch`)
runs only the npm job; with `dry_run` it builds and packs without a publish.

The npm job uses npm Trusted Publishing (GitHub OIDC), so no npm token is
stored in the repository. One-time setup on npmjs.com:

1. An owner of the `@cassandragargoyle` scope publishes the first version
   manually: `cd typescript && npm publish`
2. In the package settings, add a trusted publisher: GitHub Actions,
   repository `cassandragargoyle/api`, workflow `publish.yml`
3. Optionally, in the package settings, disallow publishing with tokens

## Security

For security-sensitive reports, please **do not open a public issue**.
Instead, contact the maintainers privately — see the
[CassandraGargoyle team](https://github.com/cassandragargoyle) page for
contact options.

## License

By contributing to CassandraGargoyle API you agree that your contributions
will be licensed under the [MIT License](LICENSE). Project governance —
including the warranty disclaimer that follows from MIT — is summarized
in [GOVERNANCE.md](GOVERNANCE.md).
