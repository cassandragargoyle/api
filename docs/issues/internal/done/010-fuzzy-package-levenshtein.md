# Issue #010: Add `fuzzy` package with Levenshtein distance (Java, Go, Python)

**Type**: Feature
**Priority**: Medium
**Status**: ✅ Implemented
**Created**: 2026-05-01
**Closed**: 2026-05-01
**Labels**: feature, fuzzy, algorithms, java, go, python, multi-language
**Repository**: Api

## Summary

Introduce a new `fuzzy` package that provides string-similarity / fuzzy-matching
helper functions. The first algorithm to ship is **Levenshtein distance**.
The package is to be implemented consistently in all three supported language
modules: **Java**, **Go**, and **Python**.

## Motivation

Several downstream projects (plugins, CLI tools, search/lookup features) need
basic fuzzy-string capabilities (typo-tolerant matching, "did you mean…?"
suggestions, deduplication of near-identical labels, ranking of close matches).
Today each consumer reimplements its own variant or pulls a third-party
dependency. A shared `fuzzy` package in the Api repo gives every language
binding the same algorithm, the same edge-case behavior, and the same test
vectors — matching the pattern already established for `telemetry`, `log`,
and `util`.

## Scope

### In scope (this issue)

- New `fuzzy` package in each language module
- **Levenshtein distance** (classic edit distance: insert, delete, substitute,
  cost = 1 each)
- A normalized similarity helper derived from the distance
  (`similarity = 1 - distance / max(len(a), len(b))`, returns `0.0` for two
  empty strings)
- Unit tests with a shared set of test vectors (see Acceptance Criteria)
- Public API documentation (Javadoc / Go doc comments / Python docstrings)

### Out of scope (follow-up issues)

- Damerau–Levenshtein, Jaro, Jaro–Winkler, Hamming, Soundex, n-gram, etc.
  (the package is named `fuzzy` precisely so that more algorithms can be
  added without renaming)
- Approximate matching over collections (`fuzzy.bestMatch`, ranked candidates)
- Locale-aware / case-folding normalization helpers
- CLI wrapper

## Proposed API

The three implementations should expose the same conceptual surface, adapted
to each language's idioms.

### Java

Package: `org.cassandragargoyle.api.fuzzy`
Location: `java/src/main/java/org/cassandragargoyle/api/fuzzy/Levenshtein.java`

```java
public final class Levenshtein {
    private Levenshtein() { /* utility class */ }

    /** Classic Levenshtein edit distance. Null inputs throw NPE. */
    public static int distance(CharSequence a, CharSequence b);

    /** Normalized similarity in [0.0, 1.0]. similarity("","") == 1.0. */
    public static double similarity(CharSequence a, CharSequence b);
}
```

Tests: `java/src/test/java/org/cassandragargoyle/api/fuzzy/LevenshteinTest.java`

### Python

Package: `cassandragargoyle.api.fuzzy`
Location: `python/src/cassandragargoyle/api/fuzzy/__init__.py`
(plus `levenshtein.py` for the implementation)

```python
def distance(a: str, b: str) -> int: ...
def similarity(a: str, b: str) -> float: ...
```

Tests: `python/tests/fuzzy/test_levenshtein.py`

### Go

Package: `fuzzy`
Location: `go/fuzzy/levenshtein.go` (creates the `go/` top-level module — see
"Open question" below)

```go
package fuzzy

// Distance returns the classic Levenshtein edit distance between a and b.
func Distance(a, b string) int

// Similarity returns a value in [0.0, 1.0]; Similarity("", "") == 1.0.
func Similarity(a, b string) float64
```

Tests: `go/fuzzy/levenshtein_test.go`

## Implementation notes

- Use the standard two-row dynamic-programming variant — O(n·m) time, O(min(n,m))
  memory. No need for the full matrix.
- Operate on Unicode code points, not bytes:
  - **Java**: iterate via `String#codePointAt` / `Character.charCount`
    (or convert to `int[]` of code points up front for clarity)
  - **Go**: convert each input to `[]rune` once
  - **Python**: iterate over `str` directly (already code-point-indexed)
- Early exit when `abs(len(a) - len(b))` exceeds an optional cutoff is **not**
  required for v1, but leave the implementation amenable to adding it later.

## Tasks

### Phase 1: Java

- [x] Create `org.cassandragargoyle.api.fuzzy` package
- [x] Implement `Levenshtein.distance` and `Levenshtein.similarity`
- [x] Add `LevenshteinTest` with the shared test vectors
- [x] Update `java/checkstyle.xml`-driven checks pass
- [x] `mvn test -f java/pom.xml` green

### Phase 2: Python

- [x] Create `cassandragargoyle.api.fuzzy` package
- [x] Implement `distance` and `similarity` in `levenshtein.py`
- [x] Re-export from `fuzzy/__init__.py`
- [x] Add `tests/fuzzy/test_levenshtein.py` with the shared test vectors
- [x] `cd python && python -m pytest` green; ruff/lint clean

### Phase 3: Go

- [x] Decide on Go module layout (see Open Question)
- [x] Create `go/fuzzy/` package
- [x] Implement `Distance` and `Similarity`
- [x] Add `levenshtein_test.go` with the shared test vectors
  (use Go table tests)
- [x] `go test ./...` green; `go vet` clean
- [x] Wire into `Makefile` (new `go-test` target alongside `test` and Python tests)

### Phase 4: Cross-cutting

- [x] Update `README.md` "Project Structure" to list `go/` (if added) and
      mention the new `fuzzy` package under "Core Features"
- [x] Cross-check that all three implementations agree on every shared
      test vector (parity test)

## Shared test vectors

All three implementations must agree on at least these cases:

| a | b | distance | similarity |
| --- | --- | --- | --- |
| `""` | `""` | 0 | 1.0 |
| `"abc"` | `""` | 3 | 0.0 |
| `""` | `"abc"` | 3 | 0.0 |
| `"abc"` | `"abc"` | 0 | 1.0 |
| `"kitten"` | `"sitting"` | 3 | 1 − 3/7 |
| `"flaw"` | `"lawn"` | 2 | 0.5 |
| `"gumbo"` | `"gambol"` | 2 | 1 − 2/6 |
| `"Saturday"` | `"Sunday"` | 3 | 1 − 3/8 |
| `"café"` | `"cafe"` | 1 | 0.75 |
| `"日本語"` | `"日本"` | 1 | 1 − 1/3 |

The Unicode rows (`café`/`cafe`, `日本語`/`日本`) are mandatory — they catch
the "byte length vs code-point length" bug in Go (and any naive Java
`String#length` use on supplementary characters).

## Acceptance Criteria

1. `org.cassandragargoyle.api.fuzzy.Levenshtein` exists and is exported from
   the published Maven artifact.
2. `cassandragargoyle.api.fuzzy` exists and is importable from the published
   Python package.
3. A `go/fuzzy` package exists and is buildable (module path TBD per Open
   Question).
4. All three implementations return identical results for every shared test
   vector listed above (parity).
5. All three implementations operate on Unicode code points / runes, not
   bytes.
6. Tests are green in CI for every language module.

## Resolved Question

**Go module layout.** Resolved during implementation. Adopted option 1:
top-level Go module at `go/` with module path
`github.com/cassandragargoyle/api/go`, mirroring `java/` and `python/`. Note
that for v0.x / v1.x the module path carries no major-version suffix; once the
module reaches v2 it would become `github.com/cassandragargoyle/api/go/v2`.

## References

- [Issue #23: Restructure project layout for multi-language platform](done/023-restructure-multi-language-layout-mvp.md)
- [Issue #22: Python TelemetryProvider shared module](done/022-python-telemetry-provider.md)
  — precedent for a Java↔Python parity package
- [Wikipedia: Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance)
