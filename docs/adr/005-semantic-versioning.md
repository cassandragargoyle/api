# ADR-005: Semantic Versioning for All Modules

**Status**: Accepted
**Date**: 2026-10-24
**Deciders**: Zdenek
**Related**: Issue #59 (Publish the TypeScript package to npmjs.com)

## Context

Up to version `1.0.0.9`, all modules used one four-part version
`MAJOR.MINOR.PATCH.BUILD`, kept the same in `java/pom.xml`,
`python/pyproject.toml`, and `typescript/package.json`. The release tags were
`v1.0.0.9`, and the Go module tags were `go/v1.0.0.8`.

Maven and PyPI accept four parts, but two ecosystems do not:

- npm accepts only semver (`MAJOR.MINOR.PATCH`), so `npm publish` rejects
  `1.0.0.9`. Issue #59 publishes the TypeScript package on npmjs.com
- Go module versions must be semver too, so the Go toolchain does not accept
  `go/v1.0.0.8` as a version of the module

A mapping for npm only (`1.0.0.9` to `1.0.9`) was tried, but it breaks the rule
that all modules have the same version, and it needs a special tag check.

## Decision

All modules use [Semantic Versioning 2.0.0](https://semver.org/):
`MAJOR.MINOR.PATCH`.

- All modules have the same version in `java/pom.xml` (with `-SNAPSHOT` during
  development), `python/pyproject.toml`, and `typescript/package.json`
- Release tags are `vMAJOR.MINOR.PATCH`, Go module tags are
  `go/vMAJOR.MINOR.PATCH`
- The first version with the new scheme is `1.1.0`. It is higher than
  `1.0.0.9` in Maven, PEP 440, and npm ordering, and MINOR increases because
  the unreleased changes add features

## Consequences

### Positive

- One version string works in Maven, PyPI, npm, and Go
- The publish workflow checks the tag against `package.json` without a mapping
- Go consumers can use version numbers instead of pseudo-versions

### Negative

- The older four-part tags (`v1.0.0.3`, `v1.0.0.8`, `v1.0.0.9`) stay in the
  history and are not valid semver; they are not changed
- A change that would be a BUILD increase before is now a PATCH increase
