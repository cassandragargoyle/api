#!/usr/bin/env python3
"""Shared configuration and utilities for GitHub publishing scripts.

Loads config from github_publish.json and provides shared functions
for preflight checks and publish workflows.

Project: CassandraGargoyle Api
"""

import fnmatch
import json
import os
import shutil
import subprocess
import sys
from pathlib import Path

# --- JSON config loading ---

_config_cache: dict | None = None


def _config_path() -> Path:
    """Return path to the JSON config file."""
    return Path(__file__).parent / "github_publish.json"


def load_config() -> dict:
    """Load and cache the JSON configuration."""
    global _config_cache
    if _config_cache is None:
        path = _config_path()
        if not path.exists():
            print(f"ERROR: Config file not found: {path}", file=sys.stderr)
            sys.exit(1)
        with open(path) as f:
            _config_cache = json.load(f)
    return _config_cache


# --- Backward-compatible constants derived from JSON ---

def _build_private_files() -> list[str]:
    """Build flat PRIVATE_FILES list from structured JSON config."""
    cfg = load_config()
    pf = cfg["private_files"]
    result: list[str] = []
    result.extend(pf.get("exact", []))
    result.extend(pf.get("directories", []))
    result.extend(pf.get("globs", []))
    result.extend(pf.get("scripts", []))
    result.extend(pf.get("internal_docs", []))
    return result


def _init_constants() -> None:
    """Initialize module-level constants from JSON config."""
    global GITHUB_REMOTE, GITHUB_REPO, PROJECT_NAME
    global PRIVATE_FILES, SENSITIVE_PATTERNS, ALLOWED_EXCEPTIONS
    global SCAN_EXTENSIONS, SKIP_DIR_NAMES, DOCS_EXAMPLE_PATHS

    cfg = load_config()

    gh = cfg["github"]
    GITHUB_REMOTE = gh["remote_name"]
    GITHUB_REPO = gh["repo_url"]
    PROJECT_NAME = gh["project_name"]

    PRIVATE_FILES = _build_private_files()

    sc = cfg["sensitive_content"]
    SENSITIVE_PATTERNS = sc["patterns"]
    ALLOWED_EXCEPTIONS = sc["allowed_exceptions"]

    scan = cfg["scanning"]
    SCAN_EXTENSIONS = scan["extensions"]
    SKIP_DIR_NAMES = set(scan["skip_dir_names"])
    DOCS_EXAMPLE_PATHS = scan["docs_example_paths"]


# Initialize constants at import time
_init_constants()


# --- Terminal colors and output helpers ---

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


# --- Git helpers ---

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


# --- Shared publish logic ---

def is_publishable(filepath: Path, source_dir: Path) -> bool:
    """Check if a single file would be published.

    Returns False for files matching any private_files entry.
    filepath must be a file (not directory).
    """
    cfg = load_config()
    pf = cfg["private_files"]
    rel = filepath.relative_to(source_dir)
    rel_str = str(rel)

    # Skip .git directory
    if ".git" in rel.parts:
        return False

    # Skip directory names that should be excluded anywhere in tree
    skip_dirs = set(cfg["scanning"]["skip_dir_names"])
    if skip_dirs.intersection(rel.parts):
        return False

    # Check exact file matches
    for exact in pf.get("exact", []):
        if rel_str == exact:
            return False

    # Check directory matches (entry ends with /)
    for d in pf.get("directories", []):
        d_clean = d.rstrip("/")
        if rel_str == d_clean or rel_str.startswith(d_clean + "/"):
            return False

    # Check script matches
    for s in pf.get("scripts", []):
        if rel_str == s:
            return False

    # Check internal docs
    for doc in pf.get("internal_docs", []):
        if rel_str == doc:
            return False

    # Check glob patterns using fnmatch against the filename
    for pattern in pf.get("globs", []):
        if fnmatch.fnmatch(rel.name, pattern):
            return False

    return True


def get_publishable_files(source_dir: Path) -> list[Path]:
    """Return sorted list of files that would be published from source_dir.

    Walks the source directory, skipping private directories early,
    and returns only files that pass is_publishable().
    """
    cfg = load_config()
    pf = cfg["private_files"]
    skip_dirs = set(cfg["scanning"]["skip_dir_names"])

    # Build set of directory prefixes to skip during walk
    skip_prefixes: set[str] = set()
    for d in pf.get("directories", []):
        skip_prefixes.add(d.rstrip("/"))

    result: list[Path] = []
    for root, dirs, files in os.walk(source_dir):
        root_path = Path(root)
        rel_root = root_path.relative_to(source_dir)

        # Prune directories we never want to enter
        dirs[:] = [
            d for d in dirs
            if d not in skip_dirs
            and d != ".git"
            and str(rel_root / d) not in skip_prefixes
        ]

        for fname in files:
            fpath = root_path / fname
            if is_publishable(fpath, source_dir):
                result.append(fpath)

    return sorted(result)


def remove_private_files(target_dir: Path) -> int:
    """Remove all private files and directories from a staging directory.

    Returns count of items removed.
    """
    cfg = load_config()
    pf = cfg["private_files"]
    removed = 0

    # Remove exact files
    for name in pf.get("exact", []):
        target = target_dir / name
        if target.exists():
            target.unlink()
            removed += 1
            print(f"   Removed: {name}")

    # Remove directories
    for d in pf.get("directories", []):
        target = target_dir / d.rstrip("/")
        if target.exists():
            shutil.rmtree(target)
            removed += 1
            print(f"   Removed: {d}")

    # Remove scripts
    for s in pf.get("scripts", []):
        target = target_dir / s
        if target.exists():
            target.unlink()
            removed += 1
            print(f"   Removed: {s}")

    # Remove internal docs
    for doc in pf.get("internal_docs", []):
        target = target_dir / doc
        if target.exists():
            target.unlink()
            removed += 1
            print(f"   Removed: {doc}")

    # Remove glob-matched files
    for pattern in pf.get("globs", []):
        for match in target_dir.rglob(pattern):
            if match.is_file():
                rel = match.relative_to(target_dir)
                match.unlink()
                removed += 1
                print(f"   Removed: {rel} (glob: {pattern})")
            elif match.is_dir():
                rel = match.relative_to(target_dir)
                shutil.rmtree(match)
                removed += 1
                print(f"   Removed: {rel}/ (glob: {pattern})")

    return removed


def apply_readme_dual_system(target_dir: Path) -> bool:
    """Apply the dual README system: rename source to target.

    Returns True if rename was performed.
    """
    cfg = load_config()
    rds = cfg["readme_dual_system"]

    if not rds.get("enabled", False):
        return False

    source = target_dir / rds["source_filename"]
    target = target_dir / rds["target_filename"]

    if not source.exists():
        return False

    print_info("Applying dual README system...")
    if target.exists():
        target.unlink()
    source.rename(target)
    print(f"   {rds['source_filename']} -> {rds['target_filename']}")
    return True
