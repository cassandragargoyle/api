# Issue #23: Restructure project layout for multi-language platform and first MVP adjustments

**Type**: Feature
**Priority**: High
**Status**: ✅ Implemented
**Created**: 2026-03-19
**Labels**: architecture, restructuring, mvp, unified-platform
**Related**: ADR-002 (Unified AI Platform Transition), portunix-architecture ADR-001
**Repository**: Api
**GitHub**: #23

## Problem

The current directory layout is impractical for a multi-language platform:

```text
api/                              # Repo root (parent POM)
├── api/                          # Java module (confusing: api/api/)
│   ├── pom.xml
│   ├── python/                   # Python nested INSIDE Java module
│   │   ├── pyproject.toml
│   │   └── src/cassandragargoyle/api/
│   ├── src/main/java/
│   └── src/test/java/
├── persistence/
└── pom.xml
```

Issues:

1. **`api/api/`** - redundant nesting, confusing for developers and CI
2. **`api/api/python/`** - Python package buried inside a Java Maven module; Maven `clean` could affect Python artifacts, IDE indexing is confused, and it suggests Python is subordinate to Java
3. **No room for Go or other languages** without deepening the nesting further
4. **No shared contract location** - the unified task contract (JSON Schema) has no natural home

## Proposed Structure

```text
api/                              # Repo root (parent POM)
├── java/                         # Java API module (renamed from api/)
│   ├── pom.xml                   # artifactId stays org-cassandragargoyle-api
│   ├── src/main/java/
│   └── src/test/java/
├── python/                       # Python API module (promoted to top level)
│   ├── pyproject.toml
│   ├── src/cassandragargoyle/api/
│   └── tests/
├── contract/                     # NEW: Language-independent task contracts
│   ├── schemas/                  # JSON Schema for TaskRequest, TaskResponse, Error
│   └── examples/                 # Example payloads (from brainstorming)
├── persistence/                  # Unchanged
│   ├── pom.xml
│   └── src/main/java/
├── pom.xml                       # Parent POM (modules: java, persistence)
├── Makefile                      # Updated targets
└── docs/
```

## Tasks

### Phase 1: Directory restructure

- [ ] **1.1** Rename `api/` directory to `java/`
  - Move `api/api/src/` to `java/src/`
  - Move `api/api/pom.xml` to `java/pom.xml`
  - Preserve git history (`git mv`)
- [ ] **1.2** Promote `python/` to top level
  - Move `api/api/python/` to `python/`
  - Preserve git history (`git mv`)
- [ ] **1.3** Update parent `pom.xml`
  - Change `<module>api</module>` to `<module>java</module>`
  - `persistence` module reference stays unchanged
- [ ] **1.4** Update `java/pom.xml`
  - Verify `<parent>` relativePath still resolves (`../pom.xml` - unchanged)
  - artifactId `org-cassandragargoyle-api` stays the same (no Maven coordinate change)
- [ ] **1.5** Update `python/pyproject.toml`
  - Verify package name and paths are correct after move
  - Test `pip install -e .` from new location
- [ ] **1.6** Update `Makefile`
  - Fix any paths that reference `api/` subdirectory
- [ ] **1.7** Update CI/CD
  - `.github/workflows/build.yml` - update paths if referencing `api/` subdir
  - `.github/workflows/publish.yml` - same
- [ ] **1.8** Update documentation
  - `README.md` - update project structure section
  - `CLAUDE.md` - update any path references
  - Issue #22 - note that file paths have changed (add migration note)
- [ ] **1.9** Clean up `.egg-info` and `__pycache__` artifacts left in old location

### Phase 2: Add contract directory (MVP foundation)

- [ ] **2.1** Create `contract/schemas/` directory
- [ ] **2.2** Add `task-request.schema.json` - JSON Schema for unified TaskRequest
  - Fields: `task`, `trace_id`, `job_id`, `input`, `config`, `context`
  - Based on brainstorming: `docs/architecture/brainstorming/Unified-AI-Task-Architecture/`
- [ ] **2.3** Add `task-response.schema.json` - JSON Schema for unified TaskResponse
  - Fields: `status`, `trace_id`, `job_id`, `result`, `error`, `meta`
- [ ] **2.4** Add `error.schema.json` - JSON Schema for structured error model
  - Fields: `code`, `message`, `details`, `retryable`
- [ ] **2.5** Add `task-manifest.schema.json` - JSON Schema for plugin task capability manifest
  - AI-readability descriptors: `title`, `summary`, `when_to_use`, `when_not_to_use`, `tunable_parameters`, `expected_artifacts`, `result_interpretation`
- [ ] **2.6** Copy and normalize example payloads from brainstorming into `contract/examples/`
  - `image_resize_task.json`, `response.json`, `error_response.json`, `frame_filter_task.json`
- [ ] **2.7** Add `contract/README.md` documenting the contract versioning strategy

### Phase 3: Validate

- [ ] **3.1** `mvn clean install` passes from repo root
- [ ] **3.2** `pip install -e .` works from `python/`
- [ ] **3.3** Python tests pass: `cd python && python -m pytest`
- [ ] **3.4** Java tests pass: `mvn test`
- [ ] **3.5** GitHub Actions build passes
- [ ] **3.6** All downstream projects that depend on `org-cassandragargoyle-api` still resolve (Maven coordinates unchanged)

## Migration Notes

### What changes

| Before | After | Impact |
| ------ | ----- | ------ |
| `api/api/pom.xml` | `java/pom.xml` | Parent POM module reference |
| `api/api/src/` | `java/src/` | Local file paths only |
| `api/api/python/` | `python/` | pyproject.toml paths, pip install location |
| N/A | `contract/` | New directory |

### What does NOT change

- **Maven coordinates** - `org.cassandragargoyle:org-cassandragargoyle-api:1.0.0.5-SNAPSHOT` stays identical
- **Python package name** - `cassandragargoyle-api` stays identical
- **Java package structure** - `org.cassandragargoyle.api.*` unchanged
- **Python module paths** - `cassandragargoyle.api.*` unchanged
- **persistence module** - untouched
- **Published artifacts** - JAR and Python package contents are identical

### Downstream impact

- **portunix-plugins** (Java plugins) - no change, they depend on Maven coordinates
- **portunix-reco** (Python) - may need to update `pip install -e` path if using local dev install
- **portunix-synapse** - no change, depends on Maven coordinates

## Acceptance Criteria

1. Repository builds successfully with `mvn clean install` from root
2. Python package installs and tests pass from `python/` directory
3. No Maven coordinate changes (downstream projects unaffected)
4. `contract/` directory contains validated JSON Schemas for TaskRequest, TaskResponse, Error, TaskManifest
5. CI/CD pipelines pass on the restructured layout
6. README reflects the new structure

## References

- [ADR-002: Unified AI Platform Transition](../../adr/002-unified-ai-platform-transition.md)
- [portunix-architecture ADR-001](portunix-architecture: docs/adr/ADR-001-unified-ai-plugin-and-task-platform.md)
- [Brainstorming: Unified AI Task Architecture](portunix-architecture: docs/architecture/brainstorming/Unified-AI-Task-Architecture/)
