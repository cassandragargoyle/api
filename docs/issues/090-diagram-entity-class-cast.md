# Issue #90: DiagramEntity collections fail with ClassCastException on second access

**Type**: Bug Fix
**Priority**: Medium
**Status**: Open
**Created**: 2026-11-02
**GitHub**: #90
**Component**: java
**Related**: #85

## Summary

`DiagramEntity.getNodes()`, `getEdges()`, and `getDiagrams()` fail with
`ClassCastException` on the second call on the same diagram. The defect was
found during the testing of #85.

## Problem Description / Motivation

- The getters create the collection lazily and store it with
  `setProperty("nodes", nodes)`
- The next call reads it back with `getProperty("nodes")` and casts the
  result to `Set<Entity>`
- `AbstractEntity.getProperty()` returns the `org.cassandragargoyle.api.Property`
  wrapper, not the stored value, so the cast fails
- The first call works because the property does not exist yet and
  `getProperty()` returns `null`
- Every method that calls a getter twice fails: `addNode` followed by
  `getNodes`, two `addEdge` calls, `getAllNodes` after `addNode`, and
  `getNode(String)` after `addNode`

Steps to reproduce:

```java
DiagramEntity diagram = new DiagramEntity("d");
diagram.addNode(new NodeEntity("a"));
diagram.getNodes(); // ClassCastException: Property cannot be cast to Set
```

## Scope

### In scope

1. Read the stored value in `getNodes()`, `getEdges()`, and `getDiagrams()`,
   for example with `properties.getValue("nodes")`
2. Unit tests for repeated access and for `getAllNodes`, `getAllEdges`, and
   `getNode(String)`
3. Remove the workaround comment in `EntityNullCheckTest`

### Out of scope (follow-up issues)

- A change of the `AbstractEntity.getProperty()` contract (it returns the
  wrapper on purpose, see its Javadoc)
- Other callers of `getProperty()` that cast the result; review them in a
  separate issue if needed

## Acceptance Criteria

1. Two calls of each getter on the same diagram return the same collection
2. `addNode`, `addEdge`, and `addDiagram` can be called more than once
3. `getAllNodes()` and `getAllEdges()` include the items of sub-diagrams
4. `mvn -f java/pom.xml clean verify` passes

## Testing

- New unit tests in `DiagramEntityTest` for the cases above
- Run `make coverage-java` and check that `DiagramEntity` line coverage rises
  from 14.3%
