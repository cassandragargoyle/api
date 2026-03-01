#!/usr/bin/env python3
"""Quick squash publish to GitHub from local Gitea development.

Creates a staging clone, removes private files, squashes commits,
and publishes to GitHub as a single clean commit.

Project: CassandraGargoyle Api
"""

import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))

from github_config import (
    GITHUB_REMOTE, GITHUB_REPO, PROJECT_NAME,
    remove_private_files, apply_readme_dual_system,
    Color, print_ok, print_warn, print_err, print_info,
    print_step, print_header,
    run_git, git_output, ensure_git_repo, has_remote, confirm,
)

TEMP_DIR = Path("..") / "api-github-staging"


def check_prerequisites() -> None:
    print_step(1, "Checking prerequisites")

    ensure_git_repo()

    # Check if GitHub remote exists
    if not has_remote(GITHUB_REMOTE):
        print_warn(f"GitHub remote '{GITHUB_REMOTE}' not found")
        print(f"Adding GitHub remote: {GITHUB_REPO}")
        run_git("remote", "add", GITHUB_REMOTE, GITHUB_REPO)
        print_ok("GitHub remote added")
    else:
        print_ok("GitHub remote exists")

    # Check working directory status
    status = git_output("status", "--porcelain")
    if status:
        print_warn("Working directory has uncommitted changes")
        run_git("status", "--short", capture=False)
        print()
        if not confirm("Continue anyway?"):
            sys.exit(1)
    else:
        print_ok("Working directory clean")


def show_changes_summary() -> None:
    print_step(2, "Analyzing changes for publication")

    # Check if GitHub remote has a main branch
    last_github_commit = ""
    try:
        run_git("fetch", GITHUB_REMOTE, "main", "--quiet")
        last_github_commit = git_output("rev-parse", f"{GITHUB_REMOTE}/main")
    except subprocess.CalledProcessError:
        pass

    if last_github_commit:
        print("Changes since last GitHub publish:")
        try:
            last_msg = git_output("log", "--oneline", "-1", last_github_commit)
            print(f"   Last GitHub commit: {last_msg}")
        except subprocess.CalledProcessError:
            print("   Last GitHub commit: Not found")
        current_msg = git_output("log", "--oneline", "-1", "HEAD")
        print(f"   Current commit: {current_msg}")
        print()
        try:
            count = int(git_output("rev-list", f"{last_github_commit}..HEAD", "--count"))
            print(f"Commits to be published: {count}")
            print()
            log = git_output("log", "--oneline", f"{last_github_commit}..HEAD")
            for line in log.splitlines()[:10]:
                print(f"   {line}")
            if count > 10:
                print(f"   ... and {count - 10} more commits")
        except subprocess.CalledProcessError:
            print("   Could not determine commit range")
            print("   Will publish current state")
    else:
        print("First time publishing to GitHub")
        current_msg = git_output("log", "--oneline", "-1", "HEAD")
        total = git_output("rev-list", "--count", "HEAD")
        print(f"   Current commit: {current_msg}")
        print(f"   Total commits to publish: {total}")


def create_release_commit() -> None:
    print_step(3, "Creating release commit")

    temp_dir = TEMP_DIR.resolve()

    if temp_dir.exists():
        print_warn("Staging directory exists, removing...")
        shutil.rmtree(temp_dir)

    print(f"Creating staging area: {temp_dir}")
    run_git("clone", ".", str(temp_dir), "--quiet")

    # Remove private files in staging
    print("Cleaning private files...")
    removed = remove_private_files(temp_dir)

    if removed > 0:
        print_ok(f"Removed {removed} private files/directories")
    else:
        print_warn("No private files found to remove")

    # Dual README system
    apply_readme_dual_system(temp_dir)

    # Check for changes and commit
    status = git_output("status", "--porcelain", cwd=str(temp_dir))
    if status:
        run_git("add", "-A", cwd=str(temp_dir))
        run_git("commit", "-m", "cleanup: remove private files for GitHub publication",
                cwd=str(temp_dir))
        print_ok("Cleanup commit created")


def prepare_release_message() -> str:
    print_step(4, "Preparing release commit message")

    temp_dir = TEMP_DIR.resolve()

    default_title = "feat: publish development changes"
    try:
        recent = git_output("log", "--oneline", "HEAD~5..HEAD", cwd=str(temp_dir))
        default_body = (
            "Summary of changes since last GitHub release:\n\n"
            + "\n".join(f"- {line}" for line in recent.splitlines())
        )
    except subprocess.CalledProcessError:
        default_body = "- Development updates"

    print("Enter release commit details:\n")
    try:
        title = input(f"Release title [{default_title}]: ").strip()
    except (EOFError, KeyboardInterrupt):
        title = ""
    title = title or default_title

    print()
    print("Release description (enter lines, empty line to finish):")
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

    print()
    print("Release commit will be:")
    print(f"Title: {title}")
    print(f"Body:\n{body}")
    return f"{title}\n\n{body}"


def create_final_commit(message: str) -> None:
    print_step(5, "Creating final release commit")

    temp_dir = TEMP_DIR.resolve()

    # Squash all commits into one
    initial = git_output("rev-list", "--max-parents=0", "HEAD", cwd=str(temp_dir))
    run_git("reset", "--soft", initial, cwd=str(temp_dir))
    run_git("commit", "-m", message, cwd=str(temp_dir))

    commit_line = git_output("log", "--oneline", "-1", "HEAD", cwd=str(temp_dir))
    print_ok("Release commit created")
    print(f"   Commit: {commit_line}")


def publish_to_github() -> None:
    print_step(6, "Publishing to GitHub")

    temp_dir = TEMP_DIR.resolve()

    print("Ready to publish to GitHub")
    print(f"   Target: {GITHUB_REPO}")
    print(f"   Branch: main")
    print()

    if confirm("Proceed with publication?"):
        print("Pushing to GitHub...")
        run_git("push", GITHUB_REMOTE, "HEAD:main", "--force-with-lease",
                capture=False, cwd=str(temp_dir))
        print_ok("Successfully published to GitHub!")
        print()
        print(f"GitHub repository: {GITHUB_REPO}")
        commit_line = git_output("log", "--oneline", "-1", "HEAD", cwd=str(temp_dir))
        print(f"Latest commit: {commit_line}")
    else:
        print_warn("Publication cancelled")
        print(f"   Staging directory preserved: {temp_dir}")
        print("   You can review and push manually if needed")


def cleanup_staging() -> None:
    print_step(7, "Cleanup")

    temp_dir = TEMP_DIR.resolve()
    if temp_dir.exists():
        if confirm("Remove staging directory?", default=True):
            shutil.rmtree(temp_dir)
            print_ok("Staging directory removed")
        else:
            print_warn(f"Staging directory preserved: {temp_dir}")


def main() -> None:
    print_header(f"{PROJECT_NAME} GITHUB PUBLISHER (Quick)")

    check_prerequisites()
    show_changes_summary()

    if not confirm("Continue with GitHub publication?"):
        print("Publication cancelled")
        sys.exit(0)

    create_release_commit()
    message = prepare_release_message()
    create_final_commit(message)
    publish_to_github()
    cleanup_staging()

    print(f"\n{Color.GREEN}GitHub publication workflow completed!{Color.NC}")


if __name__ == "__main__":
    main()
