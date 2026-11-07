# Issue #101: Contract: wheel and extra_wheels in plugin manifest schema

**Type**: Enhancement
**Priority**: Medium
**Status**: Implemented (PR #102, 2026-11-07)
**Created**: 2026-11-06
**Closed**: 2026-11-07
**GitHub**: #101
**Component**: contract
**Related**: #26

## Summary

Add the `plugin.wheel` and `plugin.extra_wheels` properties to
`contract/schemas/plugin-manifest.schema.json`. The Portunix plugin installer reads both
keys from the `plugin.json` of a Python wheel plugin, but the schema forbids them, so every
distribution manifest of a Python wheel plugin fails validation.

## Problem Description / Motivation

The `plugin` object in `plugin-manifest.schema.json` has `"additionalProperties": false`
and lists only `type`, `binary`, `runtime`, `runtime_version`, `python_min_version`,
`python_version`, `jvm_args`, `mode`, `port`, `health_check_interval` and `interfaces`.

The Portunix plugin manager (`src/app/plugins/types.go` in `cassandragargoyle/portunix`,
struct `PluginBinaryConfig`) also reads:

- `wheel` (string) — file name of the plugin's Python wheel in the distribution
  directory. When `runtime` is `python` and `wheel` is set, the installer creates a venv
  in the plugin directory, installs the wheel and runs `binary` as the entry point script
  from the venv.
- `extra_wheels` (array of strings) — glob patterns, relative to the distribution
  directory, of dependency wheels installed before the plugin wheel. A pattern that
  matches no file fails the installation.

Python plugin build scripts (e.g. `scripts/build_dist.py` in `portunix-bmi`) add these
keys to `dist/plugin.json`. As a result:

- the source `plugin.json` validates, the distribution manifest that Portunix actually
  installs does not (`Additional properties are not allowed ('wheel' was unexpected)`),
- build scripts can only validate the manifest *before* adding the keys, so the keys
  the installer depends on are never checked.

## Scope

### In scope

- `plugin.wheel`: string, pattern `^[^/\\]+\.whl$` (a file name, not a path), with a
  description of the installer behaviour.
- `plugin.extra_wheels`: array of non-empty strings (glob patterns relative to the
  distribution directory), `uniqueItems: true`, with a description.
- Document that both apply only to `runtime: python` (description; optionally an
  `if/then` rule that rejects them for other runtimes).
- A new example `contract/examples/plugin-manifest-python-wheel.json` with `wheel`
  and `extra_wheels`, validated by `make validate-contract`.
- An invalid case (e.g. `wheel` with a path or without `.whl`) covered by the contract
  tests, if the repository has negative examples.
- `CHANGELOG.md` entry.

### Out of scope (follow-up issues)

- Changes to the Portunix installer itself.
- Renaming or restructuring existing `plugin` properties.
- The `--help-ai` contract (`help-ai.schema.json`).

## Acceptance Criteria

1. `plugin-manifest.schema.json` defines `plugin.wheel` and `plugin.extra_wheels`
   with types and descriptions matching the installer behaviour above.
2. A distribution manifest of a Python wheel plugin with `wheel` and `extra_wheels`
   validates against the schema.
3. `wheel` containing a path separator or not ending in `.whl` fails validation.
4. Existing examples in `contract/examples/` still validate.
5. `make validate-contract` and `make lint` pass.

## Testing

- `make validate-contract` with the new example.
- Manual check: validate the `dist/plugin.json` produced by the `portunix-bmi` build
  (`make dist`) against the updated schema.
- Negative check: `wheel` set to `dist/x.whl` and to `x.zip` is rejected.
