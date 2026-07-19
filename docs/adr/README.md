# Architecture Decision Records (ADR)

This directory contains Architecture Decision Records for CassandraGargoyle API project.

## What is an ADR?

An Architecture Decision Record (ADR) captures an important architectural decision made along with its context and consequences.

## ADR List

| ADR | Title | Status | Date |
| --- | ----- | ------ | ---- |
| [ADR-001](001-opentelemetry-tracing.md) | OpenTelemetry as Tracing Foundation | Accepted | 2026-03-18 |
| [ADR-002](002-unified-ai-platform-transition.md) | Portunix Transition to Unified AI Platform | Proposed | 2026-03-19 |
| [ADR-003](003-remove-persistence-module.md) | Remove Persistence Module | Accepted | 2026-03-19 |
| [ADR-004](004-user-preferences-storage.md) | User Preferences Storage Interface | Proposed | 2026-07-05 |

## Status Legend

- **Proposed** - Decision is under consideration
- **Accepted** - Decision has been accepted and should be followed
- **Deprecated** - Decision is no longer valid
- **Superseded** - Decision has been replaced by a newer ADR

## ADR Template

```markdown
# ADR-XXX: Title

**Status**: Proposed | Accepted | Deprecated | Superseded
**Date**: YYYY-MM-DD
**Deciders**: Names
**Related**: Issue #XXX, ADR-XXX

## Context

[Describe the context and problem statement]

## Decision

[Describe the decision and its rationale]

## Consequences

### Positive
[List positive consequences]

### Negative
[List negative consequences]

### Risks
[List risks and mitigations]
```

## Creating a New ADR

1. Copy the template above
2. Use next sequential number (currently: 002)
3. Create file: `XXX-short-title.md`
4. Update this README with new entry
5. Link to related issues

---

**Last Updated**: 2026-03-19

