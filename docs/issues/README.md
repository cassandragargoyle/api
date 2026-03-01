# Issues Documentation & Tracking

This directory contains detailed documentation for all issues, feature requests, and development planning.

## Dual Numbering System

We use a dual numbering system to separate internal development tracking from public GitHub issues:

- **Internal**: All issues (bugs, security, features) tracked in `internal/` with sequential numbering (#001, #002, etc.)
- **Public**: Selected features and enhancements published to GitHub with PUB- prefix (PUB-001, PUB-002, etc.)

## Issues List

| Internal | Public | Title | Status | Priority | Type | Labels |
| -------- | ------ | ----- | ------ | -------- | ---- | ------ |
| #001 | - | Github packages | 📋 Open | Medium | Feature | |
| #002 | [GH#6](https://github.com/cassandragargoyle/api/issues/6) | GitHub Packages publishing workflow | 📋 Open | Medium | Feature | ci, maven, github |

## Directory Structure

```text
docs/issues/
├── README.md           # This file - main tracking table
├── internal/           # All internal issues (not published to GitHub)
│   ├── 001-*.md
│   ├── 002-*.md
│   └── ...
└── public/            
    └── mapping.json   # Mapping between internal and public issue numbers
```

## Usage

### Creating New Issues

1. **Internal Issue (all types):**
   - Create file: `internal/{next-number}-{short-title}.md`
   - Update this README with issue entry
   - Set Public column to `-` initially

2. **Publishing to GitHub (features/enhancements only):**
   - Assign next PUB- number in mapping.json
   - Update Public column in this README
   - Create GitHub issue with PUB- number
   - Never publish: bugs, security issues, internal tasks

### Issue Types

- **Feature**: New functionality (can be public)
- **Enhancement**: Improvement to existing features (can be public)  
- **Bug Fix**: Fixing broken functionality (internal only)
- **Security**: Security-related issues (internal only)
- **Plugin**: Plugin-specific features (selective public)

### Status Legend

- 📋 Open - Issue is open and needs work
- 🔄 In Progress - Issue is being actively worked on  
- ✅ Implemented - Issue has been completed and implemented
- ❌ Closed - Issue has been closed without implementation
- ⏸️ On Hold - Issue is temporarily paused

### Priority Legend

- **Critical** - Must be fixed immediately
- **High** - Important feature or significant bug
- **Medium** - Nice to have feature or minor bug
- **Low** - Enhancement or cosmetic issue

## Publishing Guidelines

✅ **Can be published to GitHub:**

- New features
- Enhancements
- Feature requests
- Roadmap items
- Success stories

❌ **Keep internal only:**

- Bug reports and fixes
- Security vulnerabilities
- Performance issues
- Critical errors
- Internal refactoring
- Technical debt
