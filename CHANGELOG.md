<!-- markdownlint-disable MD024 -- repeated "Added"/"Changed"/... headings are intrinsic to the per-version changelog format -->

# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
This project uses a four-part version scheme (`MAJOR.MINOR.PATCH.BUILD`) shared
across the Java, Python, Go and TypeScript modules. The Go module is tagged with
a `go/` prefix per the Go submodule tagging rule (e.g. `go/v1.0.0.8`).

## [Unreleased]

### Added

- **TypeScript `preferences` module** (`@cassandragargoyle/api/preferences`),
  the first implementation of ADR-004: `Preferences` facade with namespaced
  `PreferencesNode` (typed `get*` / `put*` with defaults, `remove`, change
  listeners), the `PreferencesStore` backend seam, `MemoryPreferencesStore`
  (default, no application bootstrap needed in unit tests) and
  `LocalPreferencesStore` (`localStorage` in the browser / webview, JSON file
  `~/.<appId>/var/preferences.json` under Node). Reads are synchronous from a
  cache; writes are flushed with a debounce and on exit / page unload (#13).
- README: new **Contracts** section that describes the language-independent
  contracts in `contract/` (areas, rules, validation).
- **VIM prescriptive layer (VIM 2.1)**: new schemas
  `contract/schemas/vim-prescriptive.schema.json` (layer, `Definition`,
  `Reference`, `Target`, `Term`) and
  `contract/schemas/vim-prescriptive-provision.schema.json` (`Provision`,
  `Anchor`, `Relation`, `Verification`). The layer stores what a definition
  file prescribes: provisions, references and defined terms.
  `vim-document.schema.json` (`$version` 1.0.0 → 1.1.0) gets the optional
  `layers.prescriptive` property and the `vim_version` value `"2.1"`.
  `Revision` in `vim-extraction-field.schema.json` (`$version` 1.0.0 → 1.1.0)
  gets the optional `property` field. Additive: VIM 2.0 documents stay valid
  (#42).
- Examples `definition-file.vim.json` and `form-extraction.vim.json`; the
  `.vim.json` suffix is mapped in `scripts/validate-contracts.mjs`. Documents
  under `contract/examples/invalid/` are negative tests that must not
  validate, one file per rule of the prescriptive schemas (#42).
- CI job **Contract Validation** in `.github/workflows/build.yml` runs
  `make validate-contract` and is part of the quality gate (#42).
- **`ModelerContractService`** in `contract/proto/modeler.proto`
  (`GetContractInfo`, `ListContractFiles`, `GetContractFile`) and
  `HealthResponse.contract_version`: the modeler plugin serves its embedded,
  versioned snapshot of this contract (manifest with source ref/commit and
  sha256 per file) so clients need no checkout of this repository. The header
  line `// contract-version: 1.0.0` is the semver of the modeler contract,
  read by the plugin's `make contract-sync`. Additive; no language module
  changes (portunix-plugins #144).
- Optional `contract` object (`name`, `version`) in
  **`plugin-manifest.schema.json`** (`$version` 1.1.0 → 1.2.0): the API
  contract a plugin embeds and serves, shown by Portunix without starting the
  plugin. Additive (portunix-plugins #144).
- Provenance fields in the `meta` object of **`modeler-symbol.schema.json`**
  (`$version` 1.1.0): `author`, `license` (SPDX identifier), `licenseUrl`,
  `source`, `sourceUrl` and `attribution`, so symbols converted from other
  collections (QElectroTech elements under CC BY 3.0) keep their attribution.
  Additive: further `meta` keys stay free strings and existing documents remain
  valid (portunix-plugins #128).

- **`contract/schemas/modeler-symbol.schema.json`** — symbol element format of
  the Portunix Modeler (`*.msym.json`, `format: portunix.modeler.symbol/v1`):
  metadata, embedded SVG fragment restricted to a safe subset, terminals, text
  fields and standard compliance (e.g. IEC 60617). Examples
  `socket-single.msym.json` and `sensor-temperature.msym.json`; the
  `.msym.json` suffix is mapped in `scripts/validate-contracts.mjs`. The plugin
  embeds an identical copy (`ptx-modeler schema --symbol`). No language module
  changes (portunix-plugins #124).

- **`contract/proto/scribe.proto`** — canonical gRPC contract for the scribe
  transcript-correction backend (`portunix.scribe.ScribeService`), the service
  counterpart to `contract/schemas/transcript.schema.json`. Servers and clients
  vendor a copy rather than hand-writing the wire format: `portunix-plugins`
  (Python server) and `portunix-vscode` (transcript editor client, synced by
  `make sync-scribe-proto`). No language module changes (portunix-vscode #112).
- A **Proto** section in `contract/README.md`, listing the gRPC service contracts
  alongside the JSON Schemas.
- `Speaker.languages` in `contract/proto/scribe.proto` — ordered ISO 639-1 codes
  for the languages a participant speaks in the recording, the first being their
  primary one (`["cs", "en"]` = speaks Czech, occasionally uses English). A
  meeting has no single language, and the language belongs to the person rather
  than to the file, so `Media.language` cannot answer it. Additive proto3 field;
  no server change is required to keep reading existing documents, but writing
  the value needs an RPC that does not exist yet (portunix-vscode #112).
- `UseCase.solution` in `contract/schemas/use-case.schema.json` (schema `$version`
  1.0.0 → 1.1.0) — a free-text sketch of how the use-case is to be solved, bound
  to the target product. `opportunity.aiSolution` cannot answer it: an idea's
  solution is product-agnostic by definition, while a use-case is bound to one
  product, and the schema's `additionalProperties: false` left analyses that
  arrive with a proposed approach nowhere to put it. Optional and nullable, so
  existing documents keep validating; no language module changes.
- **Contract terms on `UseCase`** (schema `$version` 1.1.0 → 1.2.0) —
  `contractRef` plus `deliverables`, `acceptanceCriteria`, `outOfScope` and
  `assumptions`. A non-null `contractRef` is what separates a use-case somebody
  is obliged to deliver from one that is merely catalogued; the four arrays carry
  the terms that obligation comes with. `status` stays what it was — a delivery
  lifecycle state, not a commitment. `contractRef` is a plain reference on
  purpose: the contract's own terms (parties, price, dates) belong to the
  contract system, not to the delivery spine. All optional, so existing documents
  keep validating; no language module changes.
- **TypeScript module** (`typescript/`, npm package `@cassandragargoyle/api`) —
  the fourth top-level language module, mirroring `java/`, `python/` and `go/`.
  Dual ESM + CJS build with emitted `.d.ts`, Vitest tests, ESLint + Prettier
  (#012).
- **`log` package** (`@cassandragargoyle/api/log`) with Java/Python parity:
  `LogFactory`, `Logging`, `markLogSkip` / `isLogSkip`, shared formatter and the
  full set of terse level aliases. Isomorphic design — a console transport for
  the browser / React webview and a lazily-loaded, Node-only rolling per-PID file
  transport — so browser bundles tree-shake the filesystem code away (#012).
- `build-typescript`, `test-typescript`, `lint-typescript` and
  `clean-typescript` Makefile targets, wired into the aggregate `build`, `test`
  and `clean` targets.
- Internal issue #013 (User preferences storage — TypeScript first) and
  ADR-004 (user preferences storage).
- `deploy-local` and `finish-branch` skills.
- `plugin.python_version` field in the plugin manifest schema (for bytecode
  wheels).

### Fixed

- `scripts/validate-contracts.mjs`: a document under
  `contract/examples/invalid/` without a known contract suffix now fails the
  run. Before, it was skipped and tested nothing.
- `make lint-md` no longer needs a global `markdownlint-cli2`. The linter is a
  devDependency of the TypeScript module and the target installs it when it is
  missing.
- The documented Node.js requirement of the TypeScript module is now Node 22+
  (`README.md`, `README.cs.md`, `AGENTS.md`), the version the development
  tools need and CI uses. Before, the documents said Node 18+.

## [1.0.0.9] - 2026-05-02

### Changed

- Updated Java library dependency versions.
- Switched Maven compiler configuration to `maven.compiler.release`.

### Added

- `CONTRIBUTING.md` and `GOVERNANCE.md` tailored to the Api project.

## [1.0.0.8] - 2026-05-01

### Added

- **`fuzzy` package** with Levenshtein distance and normalized similarity across
  all three language modules — Java, Python and Go (#010).
- OpenTelemetry `TelemetryProvider` utility with NoOp / Console / OTLP modes and
  `enableTraceLogging()` for CLI trace output (#004), plus the Python
  `TelemetryProvider` parity module (#005).
- Multi-language project layout: top-level `java/`, `python/`, `go/` modules and
  the language-independent `contract/` JSON Schemas (#006).
- Python build and publish pipeline (#008); Plugin Platform contract schemas
  (#009).
- Tooling: Checkstyle for Java, ruff for Python, markdownlint for Markdown, and
  the `preflight` security check.

### Changed

- Migrated Python tooling to `uv`.
- Unified `make test` to run every language module.

### Removed

- Standalone persistence module, consolidated into the Maven POM (#007).

## [1.0.0.3] - 2026-03-01

### Added

- Initial public baseline: core entities, utilities and base abstractions.
- GitHub Packages publishing workflow (#002).

### Fixed

- Renamed the `persistance` module to `persistence` (#003).
- Strip `-SNAPSHOT` from `pom.xml` versions before publishing (#002).

[Unreleased]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.9...HEAD
[1.0.0.9]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.8...v1.0.0.9
[1.0.0.8]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.3...v1.0.0.8
[1.0.0.3]: https://github.com/CassandraGargoyle/api/releases/tag/v1.0.0.3
