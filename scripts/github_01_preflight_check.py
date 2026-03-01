#!/usr/bin/env python3
"""Pre-flight check for sensitive data before GitHub publication.

Scans the project for forbidden files, sensitive content patterns,
binary files, and large files before publishing.

Project: CassandraGargoyle Api
"""

import re
import subprocess
import sys
from datetime import datetime
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))

from github_config import (
    PRIVATE_FILES, SENSITIVE_PATTERNS, ALLOWED_EXCEPTIONS,
    SCAN_EXTENSIONS, SKIP_PATHS, DOCS_EXAMPLE_PATHS,
    Color, print_ok, print_warn, print_err, print_info, print_header,
)

# Binary file extensions to check for
BINARY_EXTENSIONS = {
    ".class", ".jar", ".war", ".ear", ".nar",
    ".exe", ".dll", ".so", ".dylib",
}


def should_skip(filepath: Path, target_dir: Path) -> bool:
    """Check if a file path should be skipped during scanning."""
    rel = filepath.relative_to(target_dir)
    rel_str = str(rel) + ("/" if filepath.is_dir() else "")
    for skip in SKIP_PATHS:
        if rel_str.startswith(skip) or f"/{rel_str}".startswith(f"/{skip}"):
            return True
    return False


def is_in_docs_example_path(filepath: Path, target_dir: Path) -> bool:
    """Check if file is in a documentation example path."""
    rel_str = str(filepath.relative_to(target_dir))
    return any(rel_str.startswith(p) for p in DOCS_EXAMPLE_PATHS)


def check_forbidden_files(target_dir: Path) -> int:
    """Check for files that must not be published."""
    print(f"{Color.YELLOW}Checking for forbidden files...{Color.NC}")
    found = 0

    for pattern in PRIVATE_FILES:
        # Skip glob patterns, check concrete paths
        if "*" in pattern:
            continue
        path = target_dir / pattern.rstrip("/")
        if path.exists():
            print_err(f"FOUND: {pattern}")
            found += 1

    if found == 0:
        print_ok("No forbidden files found")
    else:
        print_err(f"Found {found} forbidden file(s)")
    return found


def check_sensitive_content(target_dir: Path) -> int:
    """Scan file contents for sensitive data patterns."""
    print(f"{Color.YELLOW}Scanning file contents for sensitive patterns...{Color.NC}")
    findings: list[tuple[str, str, str]] = []

    # Collect all scannable files
    files_to_scan: list[Path] = []
    for ext in SCAN_EXTENSIONS:
        for filepath in target_dir.rglob(f"*{ext}"):
            if not should_skip(filepath, target_dir) and filepath.is_file():
                files_to_scan.append(filepath)

    for filepath in files_to_scan:
        try:
            content = filepath.read_text(errors="ignore")
        except (OSError, UnicodeDecodeError):
            continue

        lines = content.splitlines()
        rel_path = str(filepath.relative_to(target_dir))

        for pattern in SENSITIVE_PATTERNS:
            # Skip non-critical patterns in documentation paths
            non_critical = ("192.168.", "10.0.", "PRIVATE", "INTERNAL")
            if pattern in non_critical and is_in_docs_example_path(filepath, target_dir):
                continue

            try:
                regex = re.compile(pattern, re.IGNORECASE)
            except re.error:
                # Treat as literal string
                regex = re.compile(re.escape(pattern), re.IGNORECASE)

            for line_num, line in enumerate(lines, 1):
                if regex.search(line):
                    # Check if line matches any allowed exception
                    is_exception = any(exc in line for exc in ALLOWED_EXCEPTIONS)
                    if not is_exception:
                        findings.append((rel_path, pattern, f"{line_num}: {line.strip()[:120]}"))

    if findings:
        print_err("Potentially sensitive content found:")
        # Deduplicate by file+pattern
        seen: set[tuple[str, str]] = set()
        for fpath, pattern, detail in findings:
            key = (fpath, pattern)
            if key not in seen:
                seen.add(key)
                print_err(f"{fpath} (pattern: {pattern})")
                print(f"      {detail}")
        return 1

    print_ok("No sensitive patterns found")
    return 0


def check_binary_files(target_dir: Path) -> int:
    """Check for binary files that should not be published."""
    print(f"{Color.YELLOW}Checking for binary files...{Color.NC}")
    found: list[Path] = []

    for filepath in target_dir.rglob("*"):
        if not filepath.is_file():
            continue
        if ".git" in filepath.parts:
            continue
        if filepath.suffix in BINARY_EXTENSIONS:
            found.append(filepath)

    if found:
        print_warn("Binary files found:")
        for f in found:
            print_warn(str(f.relative_to(target_dir)))
        return 1

    print_ok("No binary files found")
    return 0


def check_large_files(target_dir: Path, max_size_mb: float = 1.0) -> int:
    """Check for files larger than the threshold."""
    print(f"{Color.YELLOW}Checking for large files (>{max_size_mb}MB)...{Color.NC}")
    max_bytes = int(max_size_mb * 1024 * 1024)
    found: list[tuple[Path, int]] = []

    for filepath in target_dir.rglob("*"):
        if not filepath.is_file():
            continue
        if ".git" in filepath.parts:
            continue
        try:
            size = filepath.stat().st_size
            if size > max_bytes:
                found.append((filepath, size))
        except OSError:
            continue

    if found:
        print_warn("Large files found:")
        for f, size in found:
            size_mb = size / (1024 * 1024)
            print_warn(f"{f.relative_to(target_dir)} ({size_mb:.1f}MB)")
        return 1

    print_ok("No large files found")
    return 0


def check_maven_compile(target_dir: Path) -> int:
    """Run Maven compile check if pom.xml exists."""
    print(f"{Color.YELLOW}Running Maven compile check...{Color.NC}")

    pom = target_dir / "pom.xml"
    if not pom.exists():
        print_warn("No pom.xml found, skipping Maven check")
        return 0

    if not _command_available("mvn"):
        print_warn("mvn not installed, skipping Maven check")
        return 0

    print_info("Running mvn compile...")
    result = subprocess.run(
        ["mvn", "compile", "-q"],
        cwd=target_dir,
        capture_output=True,
        text=True,
    )

    if result.returncode == 0:
        print_ok("Maven compile passed")
        return 0

    print_err("Maven compile failed")
    for line in result.stdout.splitlines()[-10:]:
        print(f"      {line}")
    for line in result.stderr.splitlines()[-10:]:
        print(f"      {line}")
    return 1


def generate_report(target_dir: Path, report_file: Path | None = None) -> None:
    """Generate a pre-flight check report file."""
    if report_file is None:
        report_file = target_dir / "preflight-report.txt"

    lines = [
        "Pre-flight Security Check Report",
        "=================================",
        f"Date: {datetime.now().isoformat()}",
        f"Directory: {target_dir}",
        "",
        "Files to be published:",
    ]

    all_files = sorted(
        f for f in target_dir.rglob("*")
        if f.is_file() and ".git" not in f.parts
    )
    for f in all_files:
        lines.append(str(f.relative_to(target_dir)))

    lines.append("")
    lines.append(f"Total files: {len(all_files)}")

    report_file.write_text("\n".join(lines))
    print_ok(f"Report saved to: {report_file}")


def _command_available(cmd: str) -> bool:
    """Check if a command is available in PATH."""
    try:
        subprocess.run(
            ["which", cmd],
            capture_output=True,
            check=True,
        )
        return True
    except (subprocess.CalledProcessError, FileNotFoundError):
        return False


def main() -> None:
    print_header("PRE-FLIGHT SECURITY CHECK")

    target_dir = Path(sys.argv[1]) if len(sys.argv) > 1 else Path.cwd()
    target_dir = target_dir.resolve()

    print(f"Checking directory: {target_dir}\n")

    errors = 0
    errors += min(check_forbidden_files(target_dir), 1)
    print()
    errors += min(check_sensitive_content(target_dir), 1)
    print()
    errors += min(check_binary_files(target_dir), 1)
    print()
    errors += min(check_large_files(target_dir), 1)
    print()
    errors += min(check_maven_compile(target_dir), 1)
    print()

    print("=" * 40)
    if errors == 0:
        print(f"{Color.GREEN}PRE-FLIGHT CHECK PASSED{Color.NC}")
        print("   Safe to publish to GitHub")
    else:
        print(f"{Color.RED}PRE-FLIGHT CHECK FAILED{Color.NC}")
        print(f"   Found {errors} issue(s) that need review")
        print()
        print(f"{Color.YELLOW}Options:{Color.NC}")
        print("   1. Fix the issues and run check again")
        print("   2. Add false positives to ALLOWED_EXCEPTIONS in github_config.py")
        print("   3. Continue at your own risk")
        sys.exit(1)


if __name__ == "__main__":
    main()
