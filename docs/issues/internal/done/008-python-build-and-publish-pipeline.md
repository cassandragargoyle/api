# Issue #008: Python build and publish pipeline

**Type**: Feature
**Priority**: Medium
**Status**: ✅ Implemented
**Created**: 2026-03-19
**Closed**: 2026-03-19
**Labels**: python, ci, build, distribution
**Related**: Issue #005 (Python TelemetryProvider), Issue #006 (Restructure)
**Repository**: Api

## Problem

The Python module (`python/`) has no build or publish pipeline. Java artifacts are built as JAR and published to GitHub Packages via `mvn deploy`, but Python has no equivalent workflow. Clients would need to install from source code, which is impractical for production use.

## Scope

Add build, test, and publish targets for the Python module to the Makefile and CI/CD workflows.

## Tasks

### Phase 1: Makefile targets

- [ ] **1.1** Add `test-python` target - run `python3 -m pytest python/tests/`
- [ ] **1.2** Add `build-python` target - build wheel and sdist (`python3 -m build python/`)
- [ ] **1.3** Add `install-python` target - install in editable mode (`pip install -e python/`)
- [ ] **1.4** Add `clean-python` target - remove `python/dist/`, `python/build/`, `*.egg-info`
- [ ] **1.5** Update `make help` with Python targets
- [ ] **1.6** Update `clean` target to include `clean-python`

### Phase 2: CI/CD

- [ ] **2.1** Add Python test job to `.github/workflows/build.yml`
  - Set up Python 3.13
  - Install dev dependencies (`pip install -e "python/[dev]"`)
  - Run pytest
- [ ] **2.2** Add Python publish workflow or extend `.github/workflows/publish.yml`
  - Build wheel and sdist
  - Publish to GitHub Packages or PyPI
  - Trigger on version tags (same as Java)

### Phase 3: Package metadata

- [ ] **3.1** Verify `python/pyproject.toml` has correct metadata for distribution
  - Version sync with Java module (currently 1.0.0.4, should be 1.0.0.6)
  - Author, URL, classifiers
- [ ] **3.2** Add `build` to dev dependencies in `pyproject.toml`

## Acceptance Criteria

1. `make test-python` runs Python tests
2. `make build-python` produces wheel and sdist in `python/dist/`
3. CI runs Python tests alongside Java tests
4. Python package can be published to a package registry
5. Python package version is in sync with Java module

## References

- [Python Packaging Guide](https://packaging.python.org/)
- [pyproject.toml specification](https://packaging.python.org/en/latest/specifications/pyproject-toml/)
