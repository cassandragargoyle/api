<!-- markdownlint-disable MD024 -- repeated "Added"/"Changed"/... headings are intrinsic to the per-version changelog format -->

# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
This project uses [Semantic Versioning](https://semver.org/) (`MAJOR.MINOR.PATCH`)
shared across the Java, Python, Go and TypeScript modules, see
[ADR-005](docs/adr/005-semantic-versioning.md). The Go module is tagged with a
`go/` prefix per the Go submodule tagging rule (e.g. `go/v1.1.0`). Versions up
to `1.0.0.9` used the four-part scheme `MAJOR.MINOR.PATCH.BUILD`.

## [Unreleased]

### Changed

- **Java dependencies**: OpenTelemetry 1.40.0 → 1.62.0, Maven Surefire
  plugin 3.5.5 → 3.5.6.

- **VIM prescriptive layer** (#64, from a shape test on a design report and a
  drawing; all changes are additive or relax a constraint, `vim_version` stays
  `"2.1"`): `vim-prescriptive.schema.json` 1.1.0 —
  an `external` reference target needs `identifier` **or** `title`;
  `definition.issued` accepts a year, a year and month, or a full date
  (`2008`, `2008-11`, `2008-11-01`); new optional `terms[].references`
  (terms defined in a referenced document, checked by
  `scripts/vim-identifier-check.mjs`) and `definition.language` (BCP 47 tag of
  the verbatim text). `vim-prescriptive-provision.schema.json` 1.0.2 —
  descriptions only: `kind` follows the meaning of a provision, an anchor
  always has `bbox`, and `element_id` only when the element is not much larger
  than the provision. New example `drawing-definition-file.vim.json` and
  negative examples `term-reference-without-target`, `definition-bad-language`;
  `reference-external-without-identifier` is renamed to
  `reference-external-without-identifier-and-title`.

- **VIM name**: VIM is the *Layered Information Model* (Czech original
  *Vrstvený Informační Model*) in `README.md`, `contract/README.md` and
  `docs/GLOSSARY.md`; the former expansion "Versatile Information Model" is no
  longer used. Documentation only, the schemas do not change.

### Added

- **Java `StringUtil.isNullOrBlank` and `StringUtil.notBlank`**: like
  `isNullOrEmpty` and `notEmpty`, but a string of white space only counts as
  blank. Java only; the other modules have no `StringUtil`.
- **VIM book** (`docs/book/vim/`): a book in English and Czech about the
  Layered Information Model, its history, layers, parser, contract and use,
  with figures computed from a real `vim.json` of a synthetic sample invoice
  (`docs/book/vim/samples/`) and a prompt for a generated cover image.
- **Book generator** taken over from portunix-reco-studio:
  `scripts/build-book-pdf.py` (Markdown to PDF with WeasyPrint, Mermaid
  diagrams, several books under `docs/book/*/book.json`), the shared template
  and writing style in `docs/book/`, `scripts/generate-book-vim-figures.py`,
  and the Makefile targets `book` and `book-figures`.
- `book-chapter` skill for writing chapters of these books.

## [1.1.0] - 2026-10-24

### Changed

- **Versioning** (#59): all modules move from the four-part version
  `MAJOR.MINOR.PATCH.BUILD` to semver `MAJOR.MINOR.PATCH`, so that the version
  is valid for npm and Go ([ADR-005](docs/adr/005-semantic-versioning.md)). The
  next version is `1.1.0`, tags are `v1.1.0` and `go/v1.1.0`.

### Added

- **npm package** (#59): the TypeScript module is published on npmjs.com as
  `@cassandragargoyle/api` by a new `publish-npm` job in
  `.github/workflows/publish.yml` (npm Trusted Publishing with provenance, no
  npm token). The job also runs on a manual `workflow_dispatch`, with a
  `dry_run` option. Release steps in `CONTRIBUTING.md`.
- **Recognition Engine Lab contract** (#56): `reco-engine-lab.schema.json`
  (`*.recolab.json`) describes one recognition engine (simple or composite),
  its datasets with ground truth, the methodology, the commands of each phase
  (runner `portunix-plugin` or `exec`, placeholders, progress on stdout), and
  the metrics. `reco-run.schema.json` (`*.run.json`, training history
  `*.history.jsonl`) records one run, and `reco-run-sample.schema.json`
  (`*.samples.jsonl`) holds the results per sample and field, so that two runs
  can be compared. Examples for the stock count form pipeline and negative
  examples in `contract/examples/`. `scripts/validate-contracts.mjs` now
  validates `.jsonl` files line by line and can map a suffix to a part of a
  schema.
- **Textbook documentation for beginners**:
  `java/src/main/java/org/cassandragargoyle/api/fuzzy/README.md` explains
  `Levenshtein.java` step by step for secondary school students who learn to
  program, with embedded figures, a runnable example, and exercises. A new
  **Learning to Program** section in `README.md` and `README.cs.md` describes
  how to fork the project and links to this kind of documentation. Czech
  translation in `README.cs.md` next to it.
- The README of the Java `log` package is rewritten as a textbook chapter
  with figures, a tested example program, traps, and exercises; the
  developer reference is kept at the end and corrected (the default filter
  applies only to the file handler, `initialize` needs a JUL level name); the
  Czech `README.cs.md` is translated again from the new version.
- `contract/README.md`: figures of the contract directory, of the VIM layers
  and their `vim_version` values, and of the validation flow.
- `java/src/main/java/org/cassandragargoyle/api/util/README.md`: a textbook
  chapter for the Java `util` package (`StringUtil`, `Base64Encoder`,
  `DateUtil`, `PreferencesUtil`, `OsDetector`, `SystemUtil`) with figures, a
  tested example program, exercises, and a list of known traps in the code;
  Czech translation in `README.cs.md`.
- `java/src/main/java/org/cassandragargoyle/api/telemetry/README.md`: a
  textbook chapter for `TelemetryProvider` and `ExporterType` with figures, a
  tested example program, exercises, and a list of known traps in the code.
- Javadoc for `Base64Encoder`, `StringUtil`, `SystemUtil`, `BaseClass`, and `Edge`.
- **VIM identifier check** (`scripts/vim-identifier-check.mjs`): checks the rules of
  the prescriptive layer that JSON Schema cannot express. `provision_id`,
  `reference_id`, and `term_id` are unique in the layer, and every value of
  `provisions[].references`, `provisions[].terms`, and
  `provisions[].relations[].provision_id` has a target in the layer.
  `make validate-contract` runs it after the schema check; other projects can
  import `checkVimIdentifiers()`. Six negative examples in
  `contract/examples/invalid/` (#51).
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

### Changed

- **Breaking (contract)**: `vim-document.schema.json` (`$version` 1.1.0 → 1.2.0)
  binds the layers to `vim_version`, as the VIM specification requires. A
  document with `layers.prescriptive` must have `vim_version` `"2.1"`, a
  document with `layers.extraction` must have `"2.0"` or `"2.1"`. Such
  documents with an older version validated before and are rejected now.
  Documents without these layers are not affected. Producers that keep the
  default version `"1.5"` of the Java and Python models must set the version
  when they write a layer. Two negative examples in
  `contract/examples/invalid/` (#51).
- `vim-prescriptive.schema.json` and `vim-prescriptive-provision.schema.json`
  (`$version` 1.0.0 → 1.0.1): the descriptions of the identifiers and links
  name the identifier check; no rule change (#51).

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

[Unreleased]: https://github.com/CassandraGargoyle/api/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.9...v1.1.0
[1.0.0.9]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.8...v1.0.0.9
[1.0.0.8]: https://github.com/CassandraGargoyle/api/compare/v1.0.0.3...v1.0.0.8
[1.0.0.3]: https://github.com/CassandraGargoyle/api/releases/tag/v1.0.0.3
