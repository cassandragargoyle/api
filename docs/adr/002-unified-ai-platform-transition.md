# ADR-002: Portunix Transition to Unified AI Platform

**Status**: Proposed
**Date**: 2026-03-19
**Deciders**: Zdenek
**Related**: portunix-architecture (design repository)

## Context

Portunix is transitioning to a new architecture based on two unified platforms:

1. **Unified AI Plugin Platform** - standardized platform for AI-powered plugins
2. **Unified AI Task Platform** - standardized platform for AI-driven task orchestration

This transition fundamentally changes how plugins are structured, how tasks are dispatched and executed, and how the shared API module provides base abstractions. The current API module provides entity abstractions, utilities, and telemetry infrastructure that plugins depend on. The new platform architecture will require changes to these contracts.

### Current State

```text
┌──────────────────────────────────┐
│  API Module (current)            │
│  ├── Entity abstractions         │
│  ├── Software management         │
│  ├── Utilities                   │
│  ├── Logging                     │
│  ├── TelemetryProvider (OTel)    │
│  └── CLI support                 │
├──────────────────────────────────┤
│  Plugins (current)               │
│  ├── Individual plugin structure │
│  ├── Plugin-specific configs     │
│  └── Ad-hoc task handling        │
└──────────────────────────────────┘
```

### Target State

```text
┌──────────────────────────────────────────────────────┐
│  Unified AI Plugin Platform                          │
│  ├── Standardized plugin lifecycle                   │
│  ├── Common plugin interfaces & contracts            │
│  └── AI-aware plugin infrastructure                  │
├──────────────────────────────────────────────────────┤
│  Unified AI Task Platform                            │
│  ├── Task definition & orchestration                 │
│  ├── Task scheduling & execution                     │
│  └── AI task pipeline management                     │
├──────────────────────────────────────────────────────┤
│  API Module (adapted)                                │
│  ├── Base abstractions aligned with new platforms    │
│  ├── TelemetryProvider (unchanged, ADR-001)          │
│  └── Shared utilities                                │
└──────────────────────────────────────────────────────┘
```

### Design Ownership

All architectural design for the unified platforms is done in the **portunix-architecture** repository. The API project will receive changes derived from those designs once they are finalized and approved.

## Decision

**Accept the Unified AI Plugin Platform and Unified AI Task Platform as the target architecture. All required API changes will be designed in portunix-architecture and then applied to this project.**

### 1. Core Principles

- Architecture and interface design happens in **portunix-architecture** first
- API module changes are **derived from** finalized portunix-architecture designs
- No speculative changes in the API project before design approval
- Existing functionality (telemetry, logging, utilities) remains stable during transition
- Changes are introduced incrementally, not as a big-bang migration

### 2. Workflow

```text
portunix-architecture          API project
─────────────────────          ───────────
  Design & RFC                     │
       │                           │
  Review & approve                 │
       │                           │
  Finalize contracts ──────► Implement changes
       │                           │
  Validate integration ◄────  PR & review
       │                           │
  Close design issue          Merge & release
```

### 3. Expected Impact Areas in API Module

| Area | Expected Change | Priority |
| ---- | --------------- | -------- |
| Entity abstractions | New base interfaces for plugin/task entities | High |
| Plugin contracts | New interfaces for Unified AI Plugin Platform | High |
| Task abstractions | New interfaces for Unified AI Task Platform | High |
| Telemetry | No change (ADR-001 remains valid) | None |
| Utilities | Possible extensions, no breaking changes | Low |
| Logging | Possible extensions for platform-level logging | Low |

### 4. Versioning Strategy

- Current API version (1.0.x) continues with non-breaking changes
- Platform-aligned changes will target a new major or minor version
- Existing plugin compatibility maintained through deprecation cycle

## Consequences

### Positive

1. **Unified architecture** - consistent plugin and task handling across the ecosystem
2. **AI-first design** - platforms designed with AI capabilities as first-class concern
3. **Clear design ownership** - portunix-architecture is the single source of truth for design
4. **Incremental migration** - no big-bang rewrite, changes applied as designs are finalized
5. **Stable foundation** - existing telemetry and utility infrastructure remains unchanged

### Negative

1. **External dependency** - API project changes depend on portunix-architecture timeline
2. **Transition period** - temporary coexistence of old and new abstractions
3. **Coordination overhead** - changes require cross-repository alignment

### Risks

| Risk | Probability | Impact | Mitigation |
| ---- | ----------- | ------ | ---------- |
| Design delays in portunix-architecture | Medium | Medium | API continues independent development on non-platform features |
| Breaking changes to existing plugins | Medium | High | Deprecation cycle, compatibility adapters if needed |
| Scope creep in platform requirements | Medium | Medium | Strict RFC process in portunix-architecture |
| Design-implementation mismatch | Low | Medium | Validation step in workflow, integration tests |

### Rollback Strategy

1. API module maintains backward compatibility during transition
2. Old abstractions are deprecated but not removed until all plugins migrate
3. If platform design is abandoned, deprecated code is restored to active status
