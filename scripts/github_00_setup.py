#!/usr/bin/env python3
"""One-time setup script for GitHub publishing workflow.

Project: CassandraGargoyle Api
"""

import sys
from pathlib import Path

# Allow running from project root or scripts/ directory
sys.path.insert(0, str(Path(__file__).parent))

from github_config import (
    GITHUB_REMOTE, GITHUB_REPO,
    Color, print_ok, print_warn, print_info, print_header,
    run_git, git_output, ensure_git_repo, has_remote,
)


def main() -> None:
    print_header(f"Setting up GitHub publishing workflow")

    ensure_git_repo()

    # Show current remotes
    print("Current git remotes:")
    result = run_git("remote", "-v", capture=False)
    print()

    # Add GitHub remote if it does not exist
    if not has_remote(GITHUB_REMOTE):
        print_warn("Adding GitHub remote...")
        run_git("remote", "add", GITHUB_REMOTE, GITHUB_REPO)
        print_ok("GitHub remote added")
    else:
        print_ok("GitHub remote already exists")

    print()
    print(f"{Color.GREEN}Setup complete!{Color.NC}")
    print()
    print("Available commands:")
    print("  python scripts/github_01_preflight_check.py  - Pre-flight security check")
    print("  python scripts/github_02_sync_publish.py     - Enhanced sync & publish workflow")
    print("  python scripts/github_02_quick_publish.py    - Quick squash publish")
    print("  git remote -v                                - View all remotes")
    print()
    print("Usage workflow:")
    print("  1. Develop locally, commit to your Gitea")
    print("  2. When ready to publish: python scripts/github_02_sync_publish.py")
    print("  3. Script will:")
    print("     - Fetch current GitHub state")
    print("     - Help create meaningful branch name")
    print("     - Sync your files (excluding private)")
    print("     - Publish as feature branch")
    print()


if __name__ == "__main__":
    main()
