#!/usr/bin/env python3
"""Enhanced GitHub sync & publish workflow.

Fetches current GitHub state, creates a feature branch, syncs files
from local Gitea repository (excluding private files), and publishes.

Project: CassandraGargoyle Api
"""

import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))

from github_config import (
    GITHUB_REMOTE, GITHUB_REPO, PROJECT_NAME, PRIVATE_FILES,
    load_config, is_publishable, remove_private_files,
    Color, print_ok, print_warn, print_err, print_info,
    print_step, print_header,
    run_git, git_output, ensure_git_repo, has_remote, confirm,
)

TIMESTAMP = datetime.now().strftime("%Y%m%d-%H%M%S")
WORK_DIR = Path("..").resolve() / "api-github-sync"


def step1_fetch_github(local_repo: Path) -> None:
    print_step(1, "Fetching current GitHub state")

    # Setup GitHub remote if needed
    if not has_remote(GITHUB_REMOTE, cwd=str(local_repo)):
        print_info(f"Adding GitHub remote: {GITHUB_REPO}")
        run_git("remote", "add", GITHUB_REMOTE, GITHUB_REPO, cwd=str(local_repo))

    # Clean working directory
    if WORK_DIR.exists():
        print_warn("Removing existing sync directory...")
        shutil.rmtree(WORK_DIR)

    print_info(f"Cloning GitHub repository to: {WORK_DIR}")
    try:
        run_git("clone", GITHUB_REPO, str(WORK_DIR), "--quiet")
    except subprocess.CalledProcessError:
        print_err("Failed to clone GitHub repository")
        sys.exit(1)

    commit = git_output("log", "--oneline", "-1", "HEAD", cwd=str(WORK_DIR))
    print_ok("GitHub repository cloned")
    print(f"   Latest commit: {commit}")


def step2_analyze_changes(local_repo: Path) -> None:
    print_step(2, "Analyzing local changes for publication")

    branch = git_output("branch", "--show-current", cwd=str(local_repo))
    commit = git_output("log", "--oneline", "-1", "HEAD", cwd=str(local_repo))
    total = git_output("rev-list", "--count", "HEAD", cwd=str(local_repo))

    print("Local repository analysis:")
    print(f"   Current branch: {Color.CYAN}{branch}{Color.NC}")
    print(f"   Latest commit: {commit}")
    print(f"   Total commits: {total}")
    print()

    # Recent commits
    print("Recent local commits (for branch naming):")
    log = git_output("log", "--oneline", "-5", "HEAD", cwd=str(local_repo))
    for line in log.splitlines():
        print(f"   {line}")
    print()

    # File count
    total_files = sum(
        1 for f in local_repo.rglob("*")
        if f.is_file() and ".git" not in f.parts
    )
    print(f"Files analysis:")
    print(f"   Total files: {total_files}")

    # Estimate private files using is_publishable
    publishable_count = sum(
        1 for f in local_repo.rglob("*")
        if f.is_file() and ".git" not in f.parts and is_publishable(f, local_repo)
    )
    private_count = total_files - publishable_count
    print(f"   Private files (will be excluded): ~{private_count}")
    print(f"   Files to publish: ~{publishable_count}")


def step3_create_branch(local_repo: Path) -> str:
    print_step(3, "Creating feature branch")

    print("Let's create a meaningful branch name based on your changes...\n")

    # Analyze recent commits for branch name suggestions
    log = git_output("log", "--oneline", "-5", "HEAD", cwd=str(local_repo))
    recent_messages = " ".join(
        line.split(" ", 1)[1] if " " in line else line
        for line in log.splitlines()
    ).lower()

    suggestions: list[str] = []
    if "fix" in recent_messages:
        suggestions.append(f"fix/bug-fixes-{TIMESTAMP}")
    if "feat" in recent_messages:
        suggestions.append(f"feature/new-features-{TIMESTAMP}")
    if "test" in recent_messages:
        suggestions.append("feature/testing-enhancements")
    if "refactor" in recent_messages:
        suggestions.append(f"refactor/code-improvements-{TIMESTAMP}")

    # Default suggestions
    suggestions.append(f"feature/development-sync-{TIMESTAMP}")
    suggestions.append("update/codebase-improvements")

    print(f"{Color.CYAN}Branch naming suggestions:{Color.NC}")
    for i, name in enumerate(suggestions, 1):
        print(f"   {i}. {name}")
    print(f"   0. Custom branch name")
    print()

    try:
        choice_str = input("Select branch name [1]: ").strip()
    except (EOFError, KeyboardInterrupt):
        choice_str = ""

    if not choice_str:
        choice = 1
    else:
        try:
            choice = int(choice_str)
        except ValueError:
            choice = 1

    if choice == 0:
        try:
            branch_name = input("Enter custom branch name: ").strip()
        except (EOFError, KeyboardInterrupt):
            branch_name = suggestions[0]
    elif 1 <= choice <= len(suggestions):
        branch_name = suggestions[choice - 1]
    else:
        branch_name = suggestions[0]

    print_info(f"Creating branch: {branch_name}")
    run_git("checkout", "-b", branch_name, cwd=str(WORK_DIR))
    print_ok(f"Branch created: {branch_name}")

    return branch_name


def _build_rsync_excludes() -> list[str]:
    """Build rsync --exclude arguments from JSON config."""
    cfg = load_config()
    pf = cfg["private_files"]
    rds = cfg["readme_dual_system"]
    excludes: list[str] = ["--exclude=.git/"]

    # File managed by dual README system must be copied (not excluded)
    readme_source = rds["source_filename"] if rds.get("enabled") else None

    for name in pf.get("exact", []):
        if name == readme_source:
            continue
        excludes.append(f"--exclude={name}")
    for d in pf.get("directories", []):
        excludes.append(f"--exclude={d}")
    for g in pf.get("globs", []):
        excludes.append(f"--exclude={g}")
    for s in pf.get("scripts", []):
        excludes.append(f"--exclude={s}")
    for doc in pf.get("internal_docs", []):
        excludes.append(f"--exclude={doc}")

    return excludes


def step4_sync_files(local_repo: Path) -> None:
    print_step(4, "Syncing files from local repository")

    print_info(f"Source: {local_repo}")
    print_info(f"Target: {WORK_DIR}")

    # Clear existing files (except .git)
    for item in WORK_DIR.iterdir():
        if item.name == ".git":
            continue
        if item.is_dir():
            shutil.rmtree(item)
        else:
            item.unlink()

    # Build rsync exclude arguments from config
    exclude_args = _build_rsync_excludes()

    print_info("Copying files from local repository...")
    try:
        subprocess.run(
            ["rsync", "-a"] + exclude_args + [f"{local_repo}/", f"{WORK_DIR}/"],
            check=True,
            capture_output=True,
            text=True,
        )
    except FileNotFoundError:
        # rsync not available, fallback to manual copy
        print_warn("rsync not available, using Python copy fallback...")
        _copy_files_fallback(local_repo, WORK_DIR)
    except subprocess.CalledProcessError as e:
        print_err(f"Failed to sync files: {e.stderr}")
        sys.exit(1)

    # Explicitly remove private files (in case rsync missed globs)
    print_info("Verifying private files removed...")
    removed = remove_private_files(WORK_DIR)
    if removed > 0:
        print_ok(f"Removed {removed} additional private files")

    print_ok("Files synchronized")

    # Show changes
    status = git_output("status", "--porcelain", cwd=str(WORK_DIR))
    if status:
        lines = status.splitlines()
        print(f"\nChanges detected:")
        for line in lines[:20]:
            print(f"   {line}")
        if len(lines) > 20:
            print(f"   ... and {len(lines) - 20} more files")
    else:
        print_warn("No changes detected - files are already in sync")


def _copy_files_fallback(src: Path, dst: Path) -> None:
    """Copy files manually when rsync is not available."""
    for item in src.rglob("*"):
        if not item.is_file():
            continue
        if not is_publishable(item, src):
            continue

        rel = item.relative_to(src)
        target = dst / rel
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(item, target)


def step5_create_commit(local_repo: Path) -> None:
    print_step(5, "Creating commit with detailed description")

    status = git_output("status", "--porcelain", cwd=str(WORK_DIR))
    if not status:
        print_warn("No changes to commit")
        return

    # Generate default commit message
    recent = git_output("log", "--oneline", "-10", "HEAD", cwd=str(local_repo))
    branch = git_output("branch", "--show-current", cwd=str(local_repo))
    total = git_output("rev-list", "--count", "HEAD", cwd=str(local_repo))

    recent_lines = "\n".join(f"- {line.split(' ', 1)[1]}" for line in recent.splitlines()[:5] if " " in line)

    default_title = "feat: sync development changes from local repository"
    default_body = (
        f"Summary of integrated changes:\n\n"
        f"{recent_lines}\n\n"
        f"Synchronized from local development repository\n"
        f"Branch: {branch}\n"
        f"Total local commits: {total}\n"
    )

    print("Commit details:\n")
    try:
        title = input(f"Commit title [{default_title}]: ").strip()
    except (EOFError, KeyboardInterrupt):
        title = ""
    title = title or default_title

    print()
    print("Commit description (enter lines, empty line to finish):")
    print("[Press Enter to use default description]")

    try:
        first_line = input().strip()
    except (EOFError, KeyboardInterrupt):
        first_line = ""

    if not first_line:
        body = default_body
    else:
        body_lines = [first_line]
        while True:
            try:
                line = input()
            except (EOFError, KeyboardInterrupt):
                break
            if not line.strip():
                break
            body_lines.append(line)
        body = "\n".join(body_lines)

    full_message = f"{title}\n\n{body}"

    run_git("add", "-A", cwd=str(WORK_DIR))
    run_git("commit", "-m", full_message, cwd=str(WORK_DIR))

    commit_line = git_output("log", "--oneline", "-1", "HEAD", cwd=str(WORK_DIR))
    print_ok("Commit created")
    print(f"   Commit: {commit_line}")


def step6_publish(branch_name: str) -> None:
    print_step(6, "Publishing to GitHub")

    commit_line = git_output("log", "--oneline", "-1", "HEAD", cwd=str(WORK_DIR))

    print("Ready to publish to GitHub")
    print(f"   Repository: {Color.CYAN}{GITHUB_REPO}{Color.NC}")
    print(f"   Branch: {Color.CYAN}{branch_name}{Color.NC}")
    print(f"   Commit: {commit_line}")
    print()

    if confirm("Publish to GitHub?", default=True):
        print_info("Pushing to GitHub...")
        run_git("push", "origin", branch_name, capture=False, cwd=str(WORK_DIR))

        print_ok("Successfully published to GitHub!")
        print()
        print(f"GitHub repository: {GITHUB_REPO}")
        print(f"Branch: {branch_name}")
        print(f"Commit: {commit_line}")
        print()
        print(f"{Color.CYAN}Next steps:{Color.NC}")
        print("   - Visit GitHub to create a Pull Request")
        print("   - Review changes before merging to main")
        print("   - Delete this branch after merging")
    else:
        print_warn("Publication cancelled")
        print(f"   Working directory preserved: {WORK_DIR}")
        print("   You can review and push manually")


def step7_cleanup() -> None:
    print_step(7, "Cleanup")

    print("Cleanup options:")
    print("   1. Keep working directory for review")
    print("   2. Remove working directory")
    print()

    try:
        choice = input("Choose [1]: ").strip()
    except (EOFError, KeyboardInterrupt):
        choice = "1"

    if choice == "2":
        shutil.rmtree(WORK_DIR)
        print_ok("Working directory removed")
    else:
        print_warn(f"Working directory preserved: {WORK_DIR}")
        print("   You can continue working or remove it manually")


def main() -> None:
    print_header(f"{PROJECT_NAME} SYNC & PUBLISH")

    ensure_git_repo()
    local_repo = Path.cwd().resolve()

    print("This workflow will:")
    print("   1. Fetch current GitHub state")
    print("   2. Analyze your local changes")
    print("   3. Create a feature branch")
    print("   4. Sync files from local repo (excluding private files)")
    print("   5. Create a descriptive commit")
    print("   6. Publish to GitHub")
    print("   7. Cleanup")
    print()

    if not confirm("Continue?", default=True):
        print("Operation cancelled")
        sys.exit(0)

    step1_fetch_github(local_repo)
    step2_analyze_changes(local_repo)
    branch_name = step3_create_branch(local_repo)
    step4_sync_files(local_repo)
    step5_create_commit(local_repo)
    step6_publish(branch_name)
    step7_cleanup()

    print(f"\n{Color.GREEN}GitHub sync & publish workflow completed!{Color.NC}")
    print()
    print("Summary:")
    print("   - Local changes synced to GitHub")
    print(f"   - Branch: {branch_name}")
    print("   - Ready for Pull Request")


if __name__ == "__main__":
    main()
