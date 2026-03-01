#!/usr/bin/env python3
"""Pre-flight check for sensitive data before GitHub publication.

Computes the publishable file set from JSON config, reports excluded files
as informational, then scans only publishable files for sensitive content,
binary files, and large files.

Project: CassandraGargoyle Api
"""

import re
import subprocess
import sys
from datetime import datetime
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))

from github_config import (
    load_config, get_publishable_files, is_publishable,
    Color, print_ok, print_warn, print_err, print_info, print_header,
)


def report_excluded_files(target_dir: Path, publishable: list[Path]) -> None:
    """Report files that will be excluded from publication (informational)."""
    print(f"{Color.YELLOW}Computing publishable file set...{Color.NC}")

    publishable_set = set(publishable)

    # Collect all files in the directory
    all_files = sorted(
        f for f in target_dir.rglob("*")
        if f.is_file() and ".git" not in f.parts
    )

    excluded = [f for f in all_files if f not in publishable_set]

    print_info(f"Total files: {len(all_files)}")
    print_info(f"Publishable: {len(publishable)}")
    print_info(f"Excluded: {len(excluded)}")

    if excluded:
        print()
        print_info("Excluded files (will NOT be published):")
        for f in excluded[:30]:
            print(f"      {f.relative_to(target_dir)}")
        if len(excluded) > 30:
            print(f"      ... and {len(excluded) - 30} more")


def is_in_docs_example_path(filepath: Path, target_dir: Path) -> bool:
    """Check if file is in a documentation example path."""
    cfg = load_config()
    docs_paths = cfg["scanning"]["docs_example_paths"]
    rel_str = str(filepath.relative_to(target_dir))
    return any(rel_str.startswith(p) for p in docs_paths)


def check_sensitive_content(target_dir: Path, publishable: list[Path]) -> int:
    """Scan publishable file contents for sensitive data patterns."""
    print(f"{Color.YELLOW}Scanning publishable files for sensitive patterns...{Color.NC}")

    cfg = load_config()
    sc = cfg["sensitive_content"]
    patterns = sc["patterns"]
    allowed = sc["allowed_exceptions"]
    non_critical = set(sc.get("non_critical_in_docs", []))
    scan_exts = set(cfg["scanning"]["extensions"])

    findings: list[tuple[str, str, str]] = []

    # Filter to scannable extensions
    files_to_scan = [f for f in publishable if f.suffix in scan_exts]

    for filepath in files_to_scan:
        try:
            content = filepath.read_text(errors="ignore")
        except (OSError, UnicodeDecodeError):
            continue

        lines = content.splitlines()
        rel_path = str(filepath.relative_to(target_dir))

        for pattern in patterns:
            # Skip non-critical patterns in documentation paths
            if pattern in non_critical and is_in_docs_example_path(filepath, target_dir):
                continue

            try:
                regex = re.compile(pattern, re.IGNORECASE)
            except re.error:
                regex = re.compile(re.escape(pattern), re.IGNORECASE)

            for line_num, line in enumerate(lines, 1):
                if regex.search(line):
                    is_exception = any(exc in line for exc in allowed)
                    if not is_exception:
                        findings.append((rel_path, pattern, f"{line_num}: {line.strip()[:120]}"))

    if findings:
        print_err("Potentially sensitive content found:")
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


def check_binary_files(target_dir: Path, publishable: list[Path]) -> int:
    """Check for binary files among publishable files."""
    print(f"{Color.YELLOW}Checking for binary files...{Color.NC}")

    cfg = load_config()
    binary_exts = set(cfg["scanning"]["binary_extensions"])

    found: list[Path] = []
    for filepath in publishable:
        if filepath.suffix in binary_exts:
            found.append(filepath)

    if found:
        print_warn("Binary files found:")
        for f in found:
            print_warn(str(f.relative_to(target_dir)))
        return 1

    print_ok("No binary files found")
    return 0


def check_large_files(target_dir: Path, publishable: list[Path]) -> int:
    """Check for files larger than the threshold among publishable files."""
    cfg = load_config()
    max_size_mb = cfg["scanning"]["max_file_size_mb"]

    print(f"{Color.YELLOW}Checking for large files (>{max_size_mb}MB)...{Color.NC}")
    max_bytes = int(max_size_mb * 1024 * 1024)
    found: list[tuple[Path, int]] = []

    for filepath in publishable:
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


def generate_report(target_dir: Path, publishable: list[Path],
                    report_file: Path | None = None) -> None:
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

    for f in publishable:
        lines.append(str(f.relative_to(target_dir)))

    lines.append("")
    lines.append(f"Total publishable files: {len(publishable)}")

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

    # Compute publishable file set once
    publishable = get_publishable_files(target_dir)

    report_excluded_files(target_dir, publishable)
    print()

    errors = 0
    errors += min(check_sensitive_content(target_dir, publishable), 1)
    print()
    errors += min(check_binary_files(target_dir, publishable), 1)
    print()
    errors += min(check_large_files(target_dir, publishable), 1)
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
        print("   2. Add false positives to allowed_exceptions in github_publish.json")
        print("   3. Continue at your own risk")
        sys.exit(1)


if __name__ == "__main__":
    main()
