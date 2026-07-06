# Issue Management for Api

## Overview

This document outlines how issues are managed in the Api project.

## Issue Types

### Bug Reports

- Software defects and errors
- Performance issues
- Security vulnerabilities
- Regression bugs

### Feature Requests

- New functionality proposals
- Enhancement suggestions
- API improvements
- User experience improvements

### Tasks

- Development tasks
- Documentation updates
- Maintenance work
- Infrastructure changes

## Labels and Categories

### Type Labels

- `bug` - Bug reports
- `enhancement` - Feature requests
- `task` - General tasks
- `documentation` - Documentation issues
- `question` - Questions and discussions

### Priority Labels

- `critical` - Urgent fixes needed
- `high` - High priority
- `medium` - Normal priority
- `low` - Low priority, nice to have

### Status Labels

- `needs-triage` - Requires initial review
- `in-progress` - Currently being worked on
- `blocked` - Cannot proceed due to dependencies
- `ready-for-review` - Ready for code review
- `needs-info` - More information required

## Workflow

### 1. Issue Creation

- Create file `docs/issues/internal/{number}-{short-title}.md` (active location)
- Add a row to the **Active** table in `docs/issues/README.md`
- Use appropriate template
- Add relevant labels
- Assign to milestone if applicable
- Provide clear description

### 2. Triage Process

- Review new issues weekly
- Assign priority and labels
- Add to appropriate project board
- Assign team members if needed

### 3. Development

- Link pull requests to issues
- Update issue status regularly
- Communicate blockers promptly
- Document decisions and changes

### 4. Closure

- Verify fix or implementation
- Update documentation if needed
- Close with appropriate comment
- Reference in release notes
- **Archive the issue file**: as the last step, move the issue file
  to the `done/` subfolder —
  `git mv docs/issues/internal/{file}.md docs/issues/internal/done/`
  — and move its row from the **Active** to the **Done** table in
  `docs/issues/README.md` (update the link path to `internal/done/...`).
  Commit the archive move together with the status change.

## Directory Layout

Active issues (`new`, `open`, `in-progress`, `blocked`, `on-hold`) live in
`docs/issues/internal/`. Completed issues (`implemented`, `closed`) are
archived in `docs/issues/internal/done/`. The README table at
`docs/issues/README.md` remains the single human entry point and links
to whichever location each file currently occupies.

Rationale: keeps `ls docs/issues/internal/` focused on active work
without fragmenting the archive by status or year. See
`docs/issues/README.md` for the full description.

## Templates

### Bug Report Template

```markdown
**Bug Description**
Brief description of the issue

**Environment**
- OS: [e.g. Ubuntu 20.04]
- Version: [e.g. v1.2.3]
- Browser: [if applicable]

**Steps to Reproduce**
1. Step one
2. Step two
3. ...

**Expected Behavior**
What should happen

**Actual Behavior**
What actually happens

**Additional Context**
Any other relevant information
```

### Feature Request Template

```markdown
**Feature Description**
Clear description of the requested feature

**Use Case**
Why is this feature needed?

**Proposed Solution**
How should this be implemented?

**Alternatives Considered**
Other approaches you've considered

**Additional Context**
Screenshots, mockups, or examples
```

## Best Practices

### For Contributors

- Search existing issues before creating new ones
- Provide complete information
- Follow issue templates
- Be respectful and constructive
- Update issues with progress

### For Maintainers

- Respond to issues promptly
- Provide clear feedback
- Use consistent labeling
- Keep issues organized
- Close stale issues appropriately

---

*Effective issue management helps maintain project quality and team productivity.*
*Last updated: 2026-04-03*
*Maintained by: Api Team*
