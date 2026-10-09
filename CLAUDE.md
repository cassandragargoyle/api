# CLAUDE.md

The project instructions for all AI assistants are in [AGENTS.md](AGENTS.md). The line below imports them into Claude Code.

@AGENTS.md

## Claude Code Specific

- The active role is set in `CLAUDE.local.md` (not tracked), switch it with `/role <name>`; the roles are in `.claude/roles/`
- Project skills are in `.claude/skills/` (`create-issue`, `implement-issue`, `finish-branch`, `deploy-local`, `translate`, `create-manifest`)
- Put instructions that apply to every AI assistant in `AGENTS.md`, keep only Claude Code specific instructions in this file
