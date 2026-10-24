# Issue #59: Publish the TypeScript package @cassandragargoyle/api to npmjs.com

**Type**: Enhancement
**Priority**: Medium
**Status**: Open
**Created**: 2026-10-24
**GitHub**: #59
**Component**: typescript, build-infra
**Related**: cassandragargoyle/react-components (`.github/workflows/publish.yml`, reference setup)

## Summary

Publish the TypeScript module (`typescript/`) as the public npm package `@cassandragargoyle/api`
on npmjs.com. Use npm Trusted Publishing (GitHub OIDC, no npm token in the repository secrets)
with provenance, in the same way as `cassandragargoyle/react-components`.

## Problem Description / Motivation

The Java module is published to GitHub Packages and the Python module to PyPI from
`.github/workflows/publish.yml` on a `v*` tag. The TypeScript module is not published at all, so
TypeScript and JavaScript projects cannot install `@cassandragargoyle/api` from a registry. The
name `@cassandragargoyle/api` is still free on npmjs.com (`npm view` returns 404).

`cassandragargoyle/react-components` already publishes `@cassandragargoyle/react-components` to
npmjs.com. Its workflow:

- runs on a `v*` tag or on `workflow_dispatch` with a `dry_run` input
- uses `permissions: id-token: write` and npm Trusted Publishing (npm 11.5.1 or newer), so no
  `NPM_TOKEN` secret is necessary and npm adds provenance
- runs typecheck, tests, and build before the publish
- fails when the tag does not match the `package.json` version
- skips the publish when the version already exists on the registry, so a re-run does not fail

### Blocker: the version is not valid semver

The module versions are kept the same in `java/pom.xml`, `python/pyproject.toml`, and
`typescript/package.json`, and the current version is `1.0.0.9` (four parts). npm accepts only
semver (`MAJOR.MINOR.PATCH[-prerelease][+build]`), so `npm publish` rejects `1.0.0.9`. Before the
first publish, choose one of these options:

1. Change the version scheme of all modules to three-part semver (for example `1.1.0`). This
   changes the tag format (`v1.0.0.9` to `v1.1.0`) and the Go tags (`go/v...`)
2. Keep the four-part project version and map it to a semver version for npm only (for example
   `1.0.9` or `1.0.0-9`). This breaks the rule that `package.json` has the same version as the
   other modules, and the tag check in the workflow must use the mapping
3. Other mapping agreed by the team

Record the decision (an ADR if the version scheme of all modules changes).

## Scope

### In scope

- Decide the version scheme (see the blocker above) and apply it to `typescript/package.json`
- Add to `typescript/package.json`: `publishConfig` (`access: public`,
  `registry: https://registry.npmjs.org`), `repository` (with `directory: typescript`, which
  provenance needs for a package in a subdirectory), `homepage`, `bugs`, `keywords`, `engines`
- Add a `typecheck` script (`tsc --noEmit`) if the build does not already do the type check
- Add an `.nvmrc` (Node 22) or set `node-version` in the workflow directly
- Add a `publish-npm` job to `.github/workflows/publish.yml`, based on the react-components
  workflow, with `working-directory: typescript`: install (`npm ci`), lint, test, build, tag and
  version check, "already published" check, `npm publish` (or `--dry-run`)
- Add `workflow_dispatch` with a `dry_run` input, so that the publish can be tested without a tag
- Add a `README.md` in `typescript/` (the npm package page shows it), and include it in the
  package (npm includes `README.md` and `LICENSE` automatically, the `LICENSE` file must be copied
  or referenced because it is in the repository root)
- Document the release steps (tag, Trusted Publisher setup) in `CONTRIBUTING.md` or in the
  existing release documentation
- Update `CHANGELOG.md`

### Manual steps (outside the repository)

- First publish: npm Trusted Publishing can be configured only for a package that exists. Either
  do the first publish manually (`npm publish --access public` by an owner of the
  `@cassandragargoyle` scope), or use the current npm option to configure a trusted publisher for
  a new package, if it is available
- On npmjs.com, in the package settings, add the trusted publisher: repository
  `cassandragargoyle/api`, workflow `publish.yml`
- Optionally, in the package settings, require 2FA and disallow publishing with tokens

### Out of scope (follow-up issues)

- Publishing the Go module (Go uses the `go/v*` tags and the Go module proxy)
- Changes to the Java and Python publish jobs, except a change that the version decision makes
  necessary
- Automatic version bumps or release notes generation

## Acceptance Criteria

1. The version decision is made and recorded; `typescript/package.json` has a valid semver version
2. `npm pack --dry-run` in `typescript/` lists only `dist/`, `package.json`, `README.md`, and
   `LICENSE`
3. The `publish-npm` job runs on a `v*` tag and on `workflow_dispatch`; `dry_run: true` builds and
   packs without a publish
4. The job fails when the tag does not match the `package.json` version (after the mapping, if
   option 2 is selected)
5. The job skips the publish when the version is already on npmjs.com
6. The job uses Trusted Publishing (`id-token: write`), no npm token is stored in the secrets
7. `@cassandragargoyle/api` is on npmjs.com with provenance, and `npm install @cassandragargoyle/api`
   works, including the subpath imports `@cassandragargoyle/api/log` and
   `@cassandragargoyle/api/preferences` (ESM and CommonJS)
8. The release steps are documented

## Testing

- `make test-typescript`, `make lint-typescript`, `make build-typescript`
- `npm pack` in `typescript/`, install the `.tgz` file in an empty project, and check `import` and
  `require` of `@cassandragargoyle/api`, `@cassandragargoyle/api/log`, and
  `@cassandragargoyle/api/preferences`
- Run the workflow with `workflow_dispatch` and `dry_run: true`
- After the first real publish: `npm view @cassandragargoyle/api` shows the version, and the
  package page on npmjs.com shows the provenance
