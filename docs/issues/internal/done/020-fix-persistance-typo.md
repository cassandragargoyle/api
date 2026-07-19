# Issue #20: Fix "persistance" typo to "persistence"

**Type**: Bug Fix
**Priority**: Low
**Status**: Implemented
**Closed**: 2026-03-03
**Created**: 2026-03-03
**Labels**: refactoring, naming
**Repository**: Api
**Reported by**: Adam
**GitHub**: #20

## Description

The module name `persistance` is a misspelling of the correct English word **persistence**. The typo is propagated across the directory name, Java package, Maven artifact ID, and documentation references.

## Affected Files

### Directory / Module

- `persistance/` directory -> rename to `persistence/`

### Maven Configuration

- `pom.xml` (root) - module reference `<module>persistance</module>`
- `persistance/pom.xml` - artifactId `org-cassandragargoyle-persistance`

### Java Source

- `persistance/src/main/java/org/cassandragargoyle/persistance/EntityRepository.java` - package declaration

### Documentation

- `README.md` - project structure section
- `README.github.md` - project structure section
- `docs/contributing/CODE-STYLE-JAVA.md` - references to the module

## Tasks

- [ ] Rename `persistance/` directory to `persistence/`
- [ ] Rename Java package from `org.cassandragargoyle.persistance` to `org.cassandragargoyle.persistence`
- [ ] Update root `pom.xml` module reference
- [ ] Update `persistance/pom.xml` artifactId
- [ ] Update all documentation references
- [ ] Clean old build artifacts (`target/` directories)
- [ ] Verify build passes after rename

## Notes

- This is a breaking change for any consumer that depends on the current artifact ID or package name
- Since the project is in early development, impact should be minimal
- The `target/` directories with old naming will need to be cleaned
