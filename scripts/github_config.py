#!/usr/bin/env python3
"""Shared configuration and utilities for GitHub publishing scripts.

Project: CassandraGargoyle Api
"""

import os
import subprocess
import sys
from pathlib import Path

# GitHub configuration
GITHUB_REMOTE = "github"
GITHUB_REPO = "https://github.com/cassandragargoyle/portunix-api.git"
PROJECT_NAME = "Api"

# Files and directories that must not be published to GitHub
PRIVATE_FILES = [
    "CLAUDE.md",
    "CLAUDE.local.md",
    "GEMINI.md",
    "NOTES.md",
    "TODO.md",
    ".claude/",
    ".vscode/",
    ".translated/",
    ".venv/",
    "docs/private/",
    "docs/issues/internal/",
    "docs/adr/",
    "config/dev/",
    "docs/notes/",
    # Publishing scripts themselves
    "scripts/github_config.py",
    "scripts/github_00_setup.py",
    "scripts/github_01_preflight_check.py",
    "scripts/github_02_quick_publish.py",
    "scripts/github_02_sync_publish.py",
    # Legacy bash scripts
    "scripts/github-00-setup.sh",
    "scripts/github-01-preflight-check.sh",
    "scripts/github-02-quick-publish.sh",
    "scripts/github-02-sync-publish.sh",
    # Internal install scripts
    "install-from-server.ps1",
    "install-from-server.sh",
    # Build artifacts
    "target/",
    "*.class",
    "*.jar",
    "*.war",
    "*.ear",
    # Internal docs
    "docs/contributing/GITEA-INTERNAL-METHODOLOGY.md",
    "docs/contributing/GITHUB-WORKFLOW.md",
    "docs/contributing/README-DUAL-SYSTEM.md",
    # Dual README system - GitHub version is renamed to README.md during publish
    "README.github.md",
]

# Patterns in content that indicate sensitive data
SENSITIVE_PATTERNS = [
    "git@gitea:",
    "gitea.cassandragargoyle",
    "cassandragargoyle.cz",
    "192.168.",
    "10.0.",
    r"password.*=.*['\"]",
    r"api_key.*=.*['\"]",
    r"secret.*=.*['\"]",
    r"token.*=.*['\"]",
    "PRIVATE",
    "INTERNAL",
    "DO NOT PUBLISH",
]

# Allowed exceptions (patterns that look sensitive but are OK)
ALLOWED_EXCEPTIONS = [
    "github.com/CassandraGargoyle",
    "ProductVersion",
    "internally",
    "Internal",
    "internal_type",
]

# File extensions to scan for sensitive content
SCAN_EXTENSIONS = [
    ".java", ".xml", ".properties", ".yml", ".yaml",
    ".md", ".json", ".py", ".sh", ".ps1", ".txt",
    ".gradle", ".kts",
]

# Paths to skip during content scanning
SKIP_PATHS = [
    ".claude/",
    ".git/",
    ".vscode/",
    ".venv/",
    ".translated/",
    "docs/adr/",
    "docs/issues/internal/",
    "docs/private/",
    "target/",
]

# Paths where documentation examples are allowed
DOCS_EXAMPLE_PATHS = [
    "docs/commands/",
    "docs/ai-assistants/",
    "docs/contributing/",
]


# Terminal colors
class Color:
    RED = "\033[0;31m"
    GREEN = "\033[0;32m"
    YELLOW = "\033[1;33m"
    BLUE = "\033[0;34m"
    CYAN = "\033[0;36m"
    NC = "\033[0m"


def print_ok(msg: str) -> None:
    print(f"{Color.GREEN}  * {msg}{Color.NC}")


def print_warn(msg: str) -> None:
    print(f"{Color.YELLOW}  ! {msg}{Color.NC}")


def print_err(msg: str) -> None:
    print(f"{Color.RED}  X {msg}{Color.NC}")


def print_info(msg: str) -> None:
    print(f"{Color.CYAN}  i {msg}{Color.NC}")


def print_step(num: int, msg: str) -> None:
    print(f"\n{Color.GREEN}Step {num}: {msg}{Color.NC}\n")


def print_header(title: str) -> None:
    width = max(len(title) + 6, 40)
    border = "=" * width
    print(f"\n{Color.BLUE}{border}")
    print(f"   {title}")
    print(f"{border}{Color.NC}\n")


def run_git(*args: str, capture: bool = True, check: bool = True,
            cwd: str | None = None) -> subprocess.CompletedProcess:
    """Run a git command and return the result."""
    cmd = ["git"] + list(args)
    return subprocess.run(
        cmd,
        capture_output=capture,
        text=True,
        check=check,
        cwd=cwd,
    )


def git_output(*args: str, cwd: str | None = None) -> str:
    """Run a git command and return stripped stdout."""
    result = run_git(*args, cwd=cwd)
    return result.stdout.strip()


def is_git_repo(path: str | None = None) -> bool:
    """Check if the current directory is a git repository."""
    try:
        run_git("rev-parse", "--git-dir", cwd=path)
        return True
    except subprocess.CalledProcessError:
        return False


def ensure_git_repo(path: str | None = None) -> None:
    """Exit if not in a git repository."""
    if not is_git_repo(path):
        print_err("Not in a git repository")
        sys.exit(1)


def has_remote(name: str, cwd: str | None = None) -> bool:
    """Check if a git remote exists."""
    try:
        run_git("remote", "get-url", name, cwd=cwd)
        return True
    except subprocess.CalledProcessError:
        return False


def confirm(prompt: str, default: bool = False) -> bool:
    """Ask user for yes/no confirmation."""
    suffix = "[Y/n]" if default else "[y/N]"
    try:
        answer = input(f"{prompt} {suffix}: ").strip().lower()
    except (EOFError, KeyboardInterrupt):
        print()
        return False

    if not answer:
        return default
    return answer in ("y", "yes")


def get_repo_root() -> Path:
    """Get the root directory of the current git repository."""
    root = git_output("rev-parse", "--show-toplevel")
    return Path(root)
