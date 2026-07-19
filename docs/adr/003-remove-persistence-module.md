# ADR-003: Remove Persistence Module

**Status**: Accepted
**Date**: 2026-03-19
**Deciders**: Zdenek
**Related**: ADR-002 (Unified AI Platform Transition)

## Context

The `persistence` module currently contains a single generic interface (`EntityRepository<T, ID>`) with no implementations, no tests, and no consumers within the API project or downstream projects.

```text
persistence/
└── src/main/java/org/cassandragargoyle/persistence/
    └── EntityRepository.java    # 7 method signatures, no implementation
```

With the transition to Unified AI Plugin Platform and Unified AI Task Platform (ADR-002), persistence concerns will be redesigned as part of the new platform architecture in **portunix-architecture**. The current premature abstraction does not align with the future direction and adds unnecessary build complexity.

### Reasons for Removal

1. **No consumers** - no code in the API module or any downstream project imports `EntityRepository`
2. **No implementations** - the interface exists in isolation with no concrete classes
3. **No tests** - zero test coverage confirms the module is unused
4. **Premature abstraction** - a generic repository pattern was created before any persistence requirements were defined
5. **Build overhead** - Maven compiles, packages, and deploys an empty JAR on every build
6. **ADR-002 supersedes** - persistence design will be part of the unified platform architecture

## Decision

**Remove the `persistence` module entirely from the API project.**

- Delete the `persistence/` directory
- Remove `<module>persistence</module>` from the parent `pom.xml`
- Update documentation (README, project structure references)

If a persistence abstraction is needed in the future, it will be designed in **portunix-architecture** and introduced as a new module aligned with the unified platform contracts.

## Consequences

### Positive

1. **Simpler build** - one fewer module to compile, test, package, and deploy
2. **No dead code** - eliminates unused abstraction from the codebase
3. **Cleaner project structure** - removes confusion about where persistence belongs
4. **Unblocks platform redesign** - no legacy persistence pattern to work around

### Negative

1. **Re-creation cost** - if persistence is needed before the platform design is finalized, a new module must be created (minimal cost given the module had one interface)

### Risks

| Risk | Probability | Impact | Mitigation |
| ---- | ----------- | ------ | ---------- |
| Unknown downstream consumer exists | Very Low | Low | Grep across all CassandraGargoyle repos confirms no imports |
| Need persistence abstraction soon | Low | Low | Interface can be recreated in minutes; git history preserves the original |

### Rollback Strategy

1. `git revert` the removal commit to restore the module
2. Or recreate `EntityRepository` from git history if only the interface is needed
