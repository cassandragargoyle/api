# Issue #85: Remove unused Lombok version property from java/pom.xml

**Type**: Task
**Priority**: Low
**Status**: Open
**Created**: 2026-11-02
**GitHub**: #85
**Component**: java
**Related**: ADR-006

## Summary

`java/pom.xml` defines the property `lombok.version` (1.18.30), but the
project does not use Lombok. Remove the property and keep the library free
of Lombok. Null checks such as `@NonNull` are written directly in code.
Review the Java code, find the parameters that must not be null, and add
the null check there.

## Problem Description / Motivation

- `lombok.version` is defined in `<properties>` in `java/pom.xml`
- No `<dependency>` and no annotation processor uses this property
- No Java source file imports `lombok.*` or uses `@NonNull`, `@Getter`,
  `@Builder` or other Lombok annotations. This is true since the first
  public commit
- The property suggests that Lombok is part of the build. This is not true,
  and a contributor can start to use Lombok because of it
- Lombok works through internal compiler APIs. New JDK versions often need a
  new Lombok version. A shared library used by many projects should not have
  this dependency

## Scope

### In scope

1. Remove `<lombok.version>1.18.30</lombok.version>` from `java/pom.xml`
2. Document the rule for null checks without Lombok in
   `docs/contributing/CODE-STYLE-JAVA.md`
3. Review the public and protected constructors and methods in
   `java/src/main/java`, find the parameters that must not be null, and add
   the null check from the rule below

### Code review for null parameters

A parameter must not be null when one of these is true:

- The code dereferences the parameter (calls a method on it, reads its
  length, unboxes it), so null causes a `NullPointerException` later in the
  call, often with an unclear message
- The constructor or setter stores the parameter in a field, and other code
  uses the field without a null check

Rules for the review:

- Do not change methods that accept null on purpose. Examples:
  `StringUtil.isNullOrEmpty`, `BaseClass.toString(Object)`,
  `DateUtil` compare methods, and `Levenshtein`, which handles null
  arguments already
- Do not change the behavior of features that exist in more than one
  module (`fuzzy`, `log`, `telemetry`) unless the other modules change too
  (see Cross-Language Parity in `AGENTS.md`)
- A new check that only replaces a later `NullPointerException` with an
  earlier one is not a breaking change
- A new check where null was accepted and stored before is a breaking
  change. List every such place in the PR description and in `CHANGELOG.md`
- Add a unit test for every new null check that asserts the
  `NullPointerException` and the parameter name in the message

### Rule for null checks

Write the check that Lombok `@NonNull` generates directly in code, at the
start of a constructor or method:

```java
public Speaker(String name)
{
	this.name = Objects.requireNonNull(name, "name");
}

public void setName(String name)
{
	this.name = Objects.requireNonNull(name, "name");
}
```

- Use `java.util.Objects.requireNonNull(value, "parameterName")`
- The behavior is the same as Lombok `@NonNull`: `NullPointerException`
  with the parameter name in the message
- Do not add a Lombok replacement library (for example a different
  annotation processor) for this

### Out of scope (follow-up issues)

- Lombok in other repositories that depend on this library
- Static null analysis annotations (JSpecify, JetBrains annotations)
- Null checks in `fuzzy`, `log`, and `telemetry`, because these features
  must behave the same in every language module
- The defect in `DiagramEntity` found during testing, see Test Results

## Acceptance Criteria

1. `java/pom.xml` does not contain `lombok`
2. `grep -ri lombok java/` returns no result
3. `mvn -f java/pom.xml clean verify` passes with the same test results as
   before the change
4. The null check rule is in `docs/contributing/CODE-STYLE-JAVA.md`
5. The parameters that must not be null have a
   `Objects.requireNonNull(value, "parameterName")` check, and each check
   has a unit test
6. Breaking changes from new null checks are listed in `CHANGELOG.md`
7. A test fails when any one null check is removed (mutation test), except
   when the method calls another method with the same check first
8. `make coverage-java` and `mvn -f java/pom.xml verify` write a JaCoCo
   coverage report to `java/target/site/jacoco/`

## Testing

- Run `mvn -f java/pom.xml clean verify` before and after the change and
  compare the test results
- Build one dependent project against the new `SNAPSHOT` to check that no
  project uses `${lombok.version}` from this POM. The POM is not a parent
  POM, so no project should be affected
- Remove each null check one at a time and run the `*NullCheckTest`
  classes. Each change must make a test fail
- Remove all null checks at the same time. All negative tests must fail and
  the tests for methods that accept null must still pass

## Test Results

Results on 2026-11-02, branch `fix/85-remove-lombok-property`:

| Check | Result |
| ----- | ------ |
| `mvn -f java/pom.xml clean verify` | 58 tests before, 143 tests after (1 skipped), 0 failures |
| `make lint-java`, `make lint-md` | 0 violations |
| Python, TypeScript, Go tests, `make validate-contract` | Pass after the version change to 1.2.1 |
| `grep -ri lombok java/` | No result |
| Each null check has a test | 83 of 83 |
| Mutation test, one check removed at a time | 82 of 83 failed a test |

The one mutant that does not fail a test is `DateUtil.isToday`. Its first
statement calls `DateUtil.toMidnight(date)`, which has the same check, so
the behavior without the check is the same. The check stays because it
documents the contract.

The first mutation run found a gap: `StringUtil.appendToHtml(null, "a")`
reached the check in `appendHtmlEscaped`, which has the same parameter
name. A test for the HTML branch closes the gap.

JaCoCo line coverage of the Java module was 20.1% (branch coverage 13.6%).
New behavior tests for `StringUtil` (`StringUtilTest`, based on its
Javadoc) raise the coverage of `StringUtil` from 7.9% to 97.3% of lines
(92.8% of branches), and of the module to 42.4% of lines (40.1% of
branches). This is still below the 80% target, so the build does not
enforce a minimum yet.

The `StringUtil` tests found one defect: `abbreviateHtmlString` cuts the
text one visible character before `maxLength`, for example `"abcdef"` with
`maxLength` 3 gives `"ab..."` and not `"abc..."` as the Javadoc says. The
test for the documented behavior is disabled until the defect is fixed.

### Follow-up

- `DiagramEntity.getNodes()`, `getEdges()`, and `getDiagrams()` fail with
  `ClassCastException` on the second call on the same diagram, because
  `getProperty()` returns the `Property` wrapper and not the `Set`. This
  defect existed before this issue
- Raise the Java test coverage to 80%, then enforce the minimum with the
  JaCoCo `check` goal
- Add PIT mutation testing to replace the one-time mutation script
- Fix the off-by-one cut in `StringUtil.abbreviateHtmlString` and enable
  `StringUtilTest.abbreviateHtmlStringKeepsMaxLengthVisibleCharacters`
- Fix `StringUtil.stringPart`, which finds only part 0 (documented in its
  Javadoc as a known defect)
