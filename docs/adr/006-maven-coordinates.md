# ADR-006: Maven Coordinates of the Java Library

**Status**: Accepted
**Date**: 2026-11-02
**Deciders**: Zdenek
**Related**: ADR-005

## Context

The Java library in `java/pom.xml` has these coordinates:

```xml
<groupId>org.cassandragargoyle</groupId>
<artifactId>org-cassandragargoyle-api</artifactId>
<version>1.2.0-SNAPSHOT</version>
<packaging>jar</packaging>
```

The `artifactId` repeats the `groupId`. In most Maven projects the
`artifactId` does not repeat the group, so a name like `cassandragargoyle-api`
is more common.

The project uses the NetBeans Platform (`netbeans.version`, `brandingToken` in
`java/pom.xml`). NetBeans names its artifacts after the module code name, with
dots replaced by dashes, for example `org-openide-util` or
`org-netbeans-api-progress`. The current `artifactId` follows this style.

Many projects already depend on the current `artifactId`. In the `portunix`
repository there are 15 `pom.xml` files that use it, for example the
`vim-parser-*`, `table-detector-*`, `fulltext`, `text-extractor`,
`m365-extractor` plugins and `synapse-platform`.

## Decision

Keep the current coordinates:

- `groupId` is `org.cassandragargoyle`. It matches the Java package
  `org.cassandragargoyle.api` and the project domain
- `artifactId` is `org-cassandragargoyle-api`. It follows the NetBeans naming
  style and is not renamed
- `version` follows ADR-005 (semver, `-SNAPSHOT` during development)
- `packaging` is `jar`. The library is a plain Java library, not a NetBeans
  module, so `nbm` is not needed

If a new Java library is created in this repository, its `artifactId` uses
the same style: `org-cassandragargoyle-<name>`.

## Consequences

### Positive

- No change is needed in the projects that depend on the library
- The name is consistent with the NetBeans artifacts used in the same projects
- Only one artifact with these classes exists in Maven repositories

### Negative

- The `artifactId` is longer than the common Maven style and repeats the
  `groupId`

### Risks

- A rename in the future needs a change in all dependent `pom.xml` files.
  During the change, the old and the new artifact can both be on the
  classpath, which gives duplicate classes. Mitigation: if a rename is ever
  needed, publish a relocation POM (`<distributionManagement><relocation>`)
  for the old `artifactId` and update all dependent projects in one step
