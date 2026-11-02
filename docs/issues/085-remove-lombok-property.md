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

## Testing

- Run `mvn -f java/pom.xml clean verify` before and after the change and
  compare the test results
- Build one dependent project against the new `SNAPSHOT` to check that no
  project uses `${lombok.version}` from this POM. The POM is not a parent
  POM, so no project should be affected
