#!/usr/bin/env python3
#
# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details
#
# /// script
# requires-python = ">=3.11"
# dependencies = [
#     "markdown-it-py>=3.0",
#     "mdit-py-plugins>=0.4",
#     "jinja2>=3.1",
#     "pygments>=2.17",
#     "weasyprint>=62",
# ]
# ///
"""Build PDF books of the repository from Markdown chapters.

Every book has its own directory under ``docs/book/`` and is defined by a JSON manifest
(``docs/book/<name>/book.json``); without ``--book``, all books are built:

- ``languages`` — the languages to build; the first one is the source language
- ``meta`` — title, subtitle and other cover texts per language
- ``labels`` — fixed texts of the template per language (Contents, Part, Figure, ...)
- ``parts`` — parts of the book, each with a title per language and a list of chapters;
  a chapter is a Markdown file, the translation of ``name.md`` is ``name.<lang>.md``
- ``style`` — the format configuration (``style.json`` next to the manifest): page, fonts,
  colours and table rules; it is applied on top of the stylesheet of the template
- ``cover_art`` — optional Jinja2 include with the drawn picture of the cover, relative to
  the template (for example ``covers/vim.svg.j2``)

Every chapter is rendered to HTML, put into the Jinja2 template with its stylesheet,
and WeasyPrint prints the result to ``<output_dir>/<file_name>`` for every language.

With ``--file``, single Markdown files are printed with the same template, each to a PDF
next to its source.

Usage:
    uv run scripts/build-book-pdf.py                      # all books, all languages
    uv run scripts/build-book-pdf.py --book docs/book/vim/book.json
    uv run scripts/build-book-pdf.py --lang cs            # one language
    uv run scripts/build-book-pdf.py --html               # also write the HTML (debugging)
    uv run scripts/build-book-pdf.py --style other/style.json
    uv run scripts/build-book-pdf.py --file contract/README.md --book docs/book/vim/book.json
    uv run scripts/build-book-pdf.py --file docs/GLOSSARY.md -o /tmp/glossary.pdf

WeasyPrint needs the Pango library of the system (installed on most Linux desktops).
Diagrams in ```mermaid blocks are rendered with mermaid-cli (``npx``, Node.js) and a
Chrome of the system; the images are cached in ``diagram_cache`` (default
``dist/book/.diagrams/``, outside of Git).
"""

from __future__ import annotations

import argparse
import datetime as dt
import hashlib
import json
import os
import re
import shutil
import struct
import subprocess
import sys
import tempfile
from dataclasses import dataclass, field
from pathlib import Path

from jinja2 import Environment, FileSystemLoader, select_autoescape
from markdown_it import MarkdownIt
from mdit_py_plugins.anchors import anchors_plugin
from pygments import highlight
from pygments.formatters import HtmlFormatter
from pygments.lexers import get_lexer_by_name
from pygments.util import ClassNotFound

REPO_ROOT = Path(__file__).resolve().parent.parent
BOOKS_DIR = REPO_ROOT / "docs" / "book"
VERSION_FILE = REPO_ROOT / "python" / "pyproject.toml"


def log(msg: str) -> None:
    print(f"[book-pdf] {msg}", flush=True)


@dataclass
class Chapter:
    number: int
    slug: str
    title: str
    html: str
    source: Path
    fallback: bool = False


@dataclass
class Part:
    number: int
    title: str
    intro: str
    chapters: list[Chapter] = field(default_factory=list)


def _highlight(code: str, lang: str, _attrs: str) -> str:
    # Fenced code with a known language gets Pygments colours; the rest stays plain
    try:
        lexer = get_lexer_by_name(lang or "text")
    except ClassNotFound:
        lexer = get_lexer_by_name("text")
    return highlight(code, lexer, HtmlFormatter(nowrap=True))


def _markdown() -> MarkdownIt:
    md = MarkdownIt("commonmark", {"html": True, "highlight": _highlight, "typographer": False})
    md.enable(["table", "strikethrough"])
    # Documents of code-textbook embed their figures as data URIs (also SVG), which the default
    # link validator rejects; the sources are files of the repository, so all are allowed
    md.validateLink = lambda url: True
    md.use(anchors_plugin, min_level=1, max_level=4, permalink=False)
    return md


def translated_path(source: Path, lang: str, source_lang: str) -> Path:
    """Path of the translation of ``source``: ``name.md`` -> ``name.<lang>.md``"""
    if lang == source_lang:
        return source
    return source.with_name(f"{source.stem}.{lang}{source.suffix}")


def _repo_link(target: Path, repo_url: str) -> str | None:
    try:
        rel = target.resolve().relative_to(REPO_ROOT)
    except ValueError:
        return None
    return f"{repo_url.rstrip('/')}/blob/main/{rel.as_posix()}"


def _drop_translation_note(text: str) -> str:
    # A translated file starts with a blockquote that points to the English source;
    # it has no meaning in the book, so the first blockquote after the H1 goes away
    return re.sub(r"\A(#[^\n]*\n+)(?:>[^\n]*\n)+\n*", r"\1", text, count=1)


def strip_h2_sections(text: str, names: list[str]) -> str:
    """Remove H2 sections whose heading, or its first word, matches one of ``names``

    The match ignores case, so ``Limits`` removes ``## Limits`` with all its subsections
    """
    wanted = {n.strip().lower() for n in names if n.strip()}
    if not wanted:
        return text
    out: list[str] = []
    skipping = False
    in_code = False
    for line in text.splitlines(keepends=True):
        if line.lstrip().startswith("```"):
            in_code = not in_code
        if not in_code and re.match(r"^#{1,2}\s", line):
            heading = line.lstrip("#").strip().lower()
            first = heading.split()[0] if heading else ""
            skipping = line.startswith("## ") and (heading in wanted or first in wanted)
        if not skipping:
            out.append(line)
    return "".join(out)


def _chrome(mermaid: dict) -> str | None:
    if mermaid.get("chrome"):
        return mermaid["chrome"]
    if os.environ.get("PUPPETEER_EXECUTABLE_PATH"):
        return os.environ["PUPPETEER_EXECUTABLE_PATH"]
    for name in ("google-chrome", "google-chrome-stable", "chromium", "chromium-browser"):
        if path := shutil.which(name):
            return path
    return None


def _png_size(path: Path) -> tuple[int, int]:
    with path.open("rb") as f:
        head = f.read(24)
    return struct.unpack(">II", head[16:24])


def render_mermaid(code: str, style: dict, cache_dir: Path) -> Path:
    """Render one Mermaid diagram to PNG, reuse the cached image when nothing changed

    PNG and not SVG: mermaid-cli puts the labels into ``foreignObject``, which
    WeasyPrint does not draw
    """
    mermaid = style.get("mermaid", {})
    config = mermaid.get("config", {})
    scale = str(mermaid.get("scale", 3))
    key = hashlib.sha256(
        json.dumps([code, config, scale], sort_keys=True).encode("utf-8")
    ).hexdigest()[:16]
    out = cache_dir / f"mermaid-{key}.png"
    if out.exists():
        return out
    npx = shutil.which("npx")
    if not npx:
        raise SystemExit("error: a mermaid diagram needs npx (Node.js) on PATH")
    cache_dir.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory() as tmp:
        tmp_dir = Path(tmp)
        (tmp_dir / "diagram.mmd").write_text(code, encoding="utf-8")
        (tmp_dir / "config.json").write_text(json.dumps(config), encoding="utf-8")
        puppeteer = {"args": ["--no-sandbox"]}
        if chrome := _chrome(mermaid):
            puppeteer["executablePath"] = chrome
        (tmp_dir / "puppeteer.json").write_text(json.dumps(puppeteer), encoding="utf-8")
        cmd = [
            npx, "-y", mermaid.get("cli", "@mermaid-js/mermaid-cli@11.4.2"),
            "-i", str(tmp_dir / "diagram.mmd"), "-o", str(out),
            "-c", str(tmp_dir / "config.json"), "-p", str(tmp_dir / "puppeteer.json"),
            "-s", scale, "-b", "white",
        ]
        result = subprocess.run(cmd, capture_output=True, text=True)
        if result.returncode != 0 or not out.exists():
            raise SystemExit(f"error: mermaid-cli failed:\n{result.stderr[-2000:]}")
    log(f"  diagram rendered: {_rel(out)}")
    return out


def _mermaid_blocks(text: str, book: dict) -> str:
    """Replace every ```mermaid block by its image; ``%% caption: ...`` gives the caption"""
    style = book.get("_style", {})
    cache_dir = (REPO_ROOT / book.get("diagram_cache", "dist/book/.diagrams")).resolve()
    scale = float(style.get("mermaid", {}).get("scale", 3))

    def replace(m: re.Match) -> str:
        code = m.group(1)
        cap = re.search(r"^\s*%%\s*caption:\s*(.+)$", code, flags=re.M)
        caption = cap.group(1).strip() if cap else ""
        png = render_mermaid(code, style, cache_dir)
        width = round(_png_size(png)[0] / scale)
        alt = caption.replace('"', "&quot;")
        return (f'\n<p><img src="{png.as_uri()}" alt="{alt}" class="diagram" '
                f'style="width: {width}px"></p>\n')

    return re.sub(r"^```mermaid\n(.*?)^```\s*$", replace, text, flags=re.S | re.M)


def render_chapter(
    source: Path, number: int, book: dict, labels: dict, strip: list[str] | None = None
) -> tuple[str, str]:
    """Render one chapter, return its title and its HTML body (without the H1)"""
    text = source.read_text(encoding="utf-8")
    # The YAML frontmatter is for AI assistants (MARKDOWN-FRONTMATTER.md), not for the reader
    text = re.sub(r"\A---\n.*?\n---\n+", "", text, count=1, flags=re.S)
    if book.get("drop_translation_note", True):
        text = _drop_translation_note(text)
    text = strip_h2_sections(text, book.get("strip_sections", []) + (strip or []))
    # Passages that only matter in the repository (file layout, how GitHub shows the file)
    # are marked in the source and left out of the book
    text = re.sub(r"<!--\s*book:skip\s*-->.*?<!--\s*/book:skip\s*-->\n?", "", text, flags=re.S)
    text = _mermaid_blocks(text, book)

    html = _markdown().render(text)

    # The H1 of the file is the title of the chapter, the template prints it
    title = ""
    h1 = re.search(r"<h1[^>]*>(.*?)</h1>\s*", html, flags=re.S)
    if h1:
        title = re.sub(r"<[^>]+>", "", h1.group(1)).strip()
        html = html[: h1.start()] + html[h1.end():]

    # Anchors must be unique in the whole book, so every id gets the chapter prefix
    prefix = f"ch{number}-"
    html = re.sub(r'id="([^"]+)"', lambda m: f'id="{prefix}{m.group(1)}"', html)
    html = re.sub(r'href="#([^"]+)"', lambda m: f'href="#{prefix}{m.group(1)}"', html)

    repo_url = book.get("repository_url", "")

    def fix_src(m: re.Match) -> str:
        src = m.group(1)
        if re.match(r"^(data:|https?:|file:)", src):
            return m.group(0)
        return f'src="{(source.parent / src).resolve().as_uri()}"'

    def fix_href(m: re.Match) -> str:
        href = m.group(1)
        if re.match(r"^(#|https?:|mailto:|data:)", href):
            return m.group(0)
        path = href.split("#", 1)[0]
        link = _repo_link(source.parent / path, repo_url) if repo_url else None
        return f'href="{link}"' if link else 'href="#"'

    html = re.sub(r'src="([^"]+)"', fix_src, html)
    html = re.sub(r'href="([^"]+)"', fix_href, html)

    # An image alone in a paragraph becomes a numbered figure with its alt text as caption
    fig_no = 0

    def to_figure(m: re.Match) -> str:
        nonlocal fig_no
        fig_no += 1
        img = m.group(1)
        alt = re.search(r'alt="([^"]*)"', img)
        caption = alt.group(1) if alt and book.get("figure_captions", True) else ""
        cap_html = (
            f'<figcaption><span class="fig-no">{labels["figure"]} {number}.{fig_no}</span> '
            f"{caption}</figcaption>"
            if caption
            else ""
        )
        return f'<figure>{img}{cap_html}</figure>'

    html = re.sub(r"<p>\s*(<img [^>]+>)\s*</p>", to_figure, html)

    html = re.sub(
        r"<table>(.*?)</table>",
        lambda m: _render_table(m.group(1), book.get("_style", {})),
        html,
        flags=re.S,
    )
    return title, html


def _cells(row_html: str, tag: str) -> list[str]:
    return [c.strip() for c in re.findall(rf"<{tag}[^>]*>(.*?)</{tag}>", row_html, flags=re.S)]


def _render_table(table_html: str, style: dict) -> str:
    """A table of the book: a table with its classes, or subsections by an ``as_sections`` rule

    With ``as_sections``, every row becomes a subsection: the first cell is its heading,
    the other cells are its text (a long test name reads better as a heading than in a
    narrow table column)
    """
    classes = _table_classes(table_html, style)
    rule = next(
        (r for r in style.get("tables", {}).get("rules", [])
         if r.get("as_sections") and f"tbl-{r['id']}" in classes.split()),
        None,
    )
    if rule is None:
        return f'<table class="{classes}">{table_html}</table>'
    level = rule["as_sections"].get("heading_level", 4)
    headers = _cells(table_html, "th")
    body = table_html.split("</thead>", 1)[-1]
    parts = [f'<div class="table-sections tbl-{rule["id"]}">']
    for row in re.findall(r"<tr>(.*?)</tr>", body, flags=re.S):
        cells = _cells(row, "td")
        if not cells:
            continue
        parts.append(f'<section class="row-section"><h{level}>{cells[0]}</h{level}>')
        for i, cell in enumerate(cells[1:], start=1):
            if not cell:
                continue
            label = headers[i] if i < len(headers) and len(cells) > 2 else ""
            prefix = f'<span class="row-label">{label}:</span> ' if label else ""
            parts.append(f"<p>{prefix}{cell}</p>")
        parts.append("</section>")
    parts.append("</div>")
    return "".join(parts)


def _table_classes(table_html: str, style: dict) -> str:
    """Classes of one table: ``wide`` by its number of columns, ``tbl-<id>`` per matching rule

    A rule matches when the header of its column is one of the given texts, so one rule
    covers all languages of the same table (for example ``Area`` and ``Oblast``)
    """
    headers = [
        re.sub(r"<[^>]+>", "", h).strip()
        for h in re.findall(r"<th[^>]*>(.*?)</th>", table_html, flags=re.S)
    ]
    tables = style.get("tables", {})
    classes = []
    if len(headers) >= tables.get("wide", {}).get("min_columns", 4):
        classes.append("wide")
    for rule in tables.get("rules", []):
        match = rule.get("match", {})
        col = match.get("column", 1)
        wanted = {h.lower() for h in match.get("header", [])}
        if len(headers) >= col and headers[col - 1].lower() in wanted:
            classes.append(f"tbl-{rule['id']}")
    return " ".join(classes)


def _column_css(selector: str, col: int, opts: dict) -> str:
    props = []
    if "width" in opts:
        props.append(f"width: {opts['width']}")
    if "min_width" in opts:
        props.append(f"min-width: {opts['min_width']}")
    if opts.get("nowrap"):
        props += ["white-space: nowrap", "hyphens: none"]
    if "align" in opts:
        props.append(f"text-align: {opts['align']}")
    if not props:
        return ""
    cells = f"{selector} th:nth-child({col}), {selector} td:nth-child({col})"
    return f"{cells} {{ {'; '.join(props)}; }}\n"


def style_css(style: dict) -> str:
    """CSS of the format configuration; it comes after the stylesheet, so it wins"""
    css = []
    colors = style.get("colors", {})
    if colors:
        css.append(":root { " + " ".join(f"--{k}: {v};" for k, v in colors.items()) + " }\n")
    page = style.get("page", {})
    if page:
        props = [f"size: {page['size']}"] if "size" in page else []
        if "margin" in page:
            props.append(f"margin: {page['margin']}")
        css.append(f"@page {{ {'; '.join(props)}; }}\n")
    fonts = style.get("fonts", {})
    if "body" in fonts:
        css.append(f"html {{ font-family: {fonts['body']}; }}\n")
    if "size" in fonts:
        css.append(f"html {{ font-size: {fonts['size']}; }}\n")
    if "heading" in fonts:
        css.append(f"h1, h2, h3, h4, .toc, figcaption, table, .cover, .part "
                   f"{{ font-family: {fonts['heading']}; }}\n")
    if "mono" in fonts:
        css.append(f"code, pre {{ font-family: {fonts['mono']}; }}\n")

    tables = style.get("tables", {})
    if "font_size" in tables:
        css.append(f"table {{ font-size: {tables['font_size']}; }}\n")
    if "font_size" in tables.get("wide", {}):
        css.append(f"table.wide {{ font-size: {tables['wide']['font_size']}; }}\n")
    if tables.get("header_nowrap"):
        css.append("th { white-space: nowrap; hyphens: none; }\n")
    for col, opts in tables.get("columns", {}).items():
        css.append(_column_css("table", int(col), opts))
    for rule in tables.get("rules", []):
        selector = f"table.tbl-{rule['id']}"
        if "font_size" in rule:
            css.append(f"{selector} {{ font-size: {rule['font_size']}; }}\n")
        for col, opts in rule.get("columns", {}).items():
            css.append(_column_css(selector, int(col), opts))
    return "".join(css)


def build_parts(book: dict, book_dir: Path, lang: str) -> list[Part]:
    source_lang = book["languages"][0]
    labels = book["labels"][lang]
    parts: list[Part] = []
    chapter_no = 0
    for part_no, part_def in enumerate(book["parts"], start=1):
        part = Part(
            number=part_no,
            title=part_def["title"][lang],
            intro=part_def.get("intro", {}).get(lang, ""),
        )
        for ch_def in part_def["chapters"]:
            chapter_no += 1
            base = (book_dir / ch_def["source"]).resolve()
            source = translated_path(base, lang, source_lang)
            fallback = False
            if not source.exists():
                log(f"  warning: {_rel(source)} missing, using {_rel(base)}")
                source, fallback = base, True
            title, html = render_chapter(
                source, chapter_no, book, labels, ch_def.get("strip_sections")
            )
            if ch_def.get("title"):
                title = ch_def["title"][lang]
            part.chapters.append(
                Chapter(chapter_no, f"ch{chapter_no}", title, html, source, fallback)
            )
        parts.append(part)
    return parts


def _rel(path: Path) -> str:
    try:
        return path.resolve().relative_to(REPO_ROOT).as_posix()
    except ValueError:
        return str(path)


class Renderer:
    """The template and the stylesheet of one book manifest"""

    def __init__(self, book: dict, book_dir: Path):
        self.book = book
        self.book_dir = book_dir
        template_path = book_dir / book["template"]
        env = Environment(
            loader=FileSystemLoader(template_path.parent),
            autoescape=select_autoescape(["html", "j2"]),
        )
        self.template = env.get_template(template_path.name)
        self.stylesheet = book_dir / book["stylesheet"]
        self.style_css = style_css(book.get("_style", {}))
        self.pygments_css = HtmlFormatter(
            style=book.get("code_style", "friendly")
        ).get_style_defs("pre code")

    def cover_image(self) -> str | None:
        # The cover image is optional: the template draws its own picture without it
        cover = self.book.get("cover_image")
        if not cover:
            return None
        path = (self.book_dir / cover).resolve()
        if not path.exists():
            log(f"  cover image {_rel(path)} missing, using the drawn cover")
            return None
        return path.as_uri()

    def write(self, pdf_path: Path, lang: str, meta: dict, parts: list[Part],
              write_html: bool, single: bool = False) -> None:
        from weasyprint import CSS, HTML

        html = self.template.render(
            lang=lang,
            meta=meta,
            labels=self.book["labels"][lang],
            parts=parts,
            single=single,
            cover_image=self.cover_image(),
            cover_art=self.book.get("cover_art"),
            build_date=dt.date.today().isoformat(),
            version=_project_version(),
            pygments_css=self.pygments_css,
        )
        pdf_path.parent.mkdir(parents=True, exist_ok=True)
        if write_html:
            pdf_path.with_suffix(".html").write_text(html, encoding="utf-8")
        HTML(string=html, base_url=str(self.book_dir)).write_pdf(
            pdf_path,
            stylesheets=[CSS(filename=str(self.stylesheet)), CSS(string=self.style_css)],
        )


def load_book(book_path: Path, style_path: Path | None) -> dict:
    """Read the manifest and its format configuration (``style``, or ``--style``)"""
    book = json.loads(book_path.read_text(encoding="utf-8"))
    if style_path is None and book.get("style"):
        style_path = book_path.parent / book["style"]
    book["_style"] = {}
    if style_path is not None:
        if not style_path.exists():
            raise SystemExit(f"error: style file {style_path} does not exist")
        book["_style"] = json.loads(style_path.read_text(encoding="utf-8"))
    return book


def build(book_path: Path, langs: list[str] | None, strip: list[str],
          write_html: bool, style_path: Path | None = None) -> list[Path]:
    book = load_book(book_path, style_path)
    book["strip_sections"] = book.get("strip_sections", []) + strip
    langs = langs or book["languages"]
    renderer = Renderer(book, book_path.parent)
    out_dir = (REPO_ROOT / book.get("output_dir", _rel(book_path.parent))).resolve()

    written = []
    for lang in langs:
        if lang not in book["languages"]:
            raise SystemExit(f"Language '{lang}' is not in {book_path}")
        log(f"building '{lang}' ...")
        parts = build_parts(book, book_path.parent, lang)
        pdf_path = out_dir / book.get("file_name", "book-{lang}.pdf").format(lang=lang)
        renderer.write(pdf_path, lang, book["meta"][lang], parts, write_html)
        chapters = sum(len(p.chapters) for p in parts)
        log(f"  {_rel(pdf_path)}: {len(parts)} parts, {chapters} chapters")
        written.append(pdf_path)
    return written


def _language_of(source: Path, book: dict) -> str:
    # name.cs.md is Czech; a file without a language suffix is in the source language
    suffix = source.suffixes[-2][1:] if len(source.suffixes) >= 2 else ""
    return suffix if suffix in book["languages"] else book["languages"][0]


def build_files(book_path: Path, inputs: list[Path], output: Path | None,
                strip: list[str], write_html: bool,
                style_path: Path | None = None) -> list[Path]:
    """Print single Markdown files with the template of the book, each next to its source"""
    book = load_book(book_path, style_path)
    renderer = Renderer(book, book_path.parent)

    sources: list[Path] = []
    for item in inputs:
        item = item.resolve()
        if item.is_dir():
            sources.extend(sorted(item.rglob("*.md")))
        elif item.is_file():
            sources.append(item)
        else:
            raise SystemExit(f"error: {item} does not exist")
    if output and len(sources) != 1:
        raise SystemExit("error: --output needs exactly one input file")

    written = []
    for source in sources:
        lang = _language_of(source, book)
        title, html = render_chapter(source, 1, book, book["labels"][lang], strip)
        meta = dict(book["meta"][lang])
        meta.update(title=title or source.stem, subtitle=_rel(source))
        part = Part(number=1, title="", intro="",
                    chapters=[Chapter(1, "ch1", title, html, source)])
        pdf_path = output.resolve() if output else source.with_suffix(".pdf")
        renderer.write(pdf_path, lang, meta, [part], write_html, single=True)
        log(f"{_rel(source)} -> {_rel(pdf_path)}")
        written.append(pdf_path)
    return written


def _project_version() -> str:
    # The module versions are kept the same (ADR-005), so the Python one stands for all
    m = re.search(r'^version\s*=\s*"([^"]+)"', VERSION_FILE.read_text(encoding="utf-8"), re.M)
    return m.group(1) if m else ""


def _all_books() -> list[Path]:
    books = sorted(BOOKS_DIR.glob("*/book.json"))
    if not books:
        raise SystemExit(f"error: no book.json under {_rel(BOOKS_DIR)}")
    return books


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--book", type=Path,
                        help="book manifest (JSON); default: every docs/book/*/book.json")
    parser.add_argument("--lang", action="append", help="language to build (repeatable)")
    parser.add_argument("--html", action="store_true", help="also write the rendered HTML")
    parser.add_argument("--style", type=Path,
                        help="format configuration (JSON) instead of the one of the manifest")
    parser.add_argument("--file", type=Path, nargs="+",
                        help="print these Markdown files (or all *.md under a directory) "
                             "instead of the book, each to a PDF next to its source")
    parser.add_argument("-o", "--output", type=Path, help="output PDF of a single --file")
    parser.add_argument("--strip-sections", default="",
                        help="comma separated H2 sections to leave out (e.g. Limits,Summary)")
    args = parser.parse_args()

    books = [args.book.resolve()] if args.book else _all_books()
    strip = [s for s in args.strip_sections.split(",") if s.strip()]
    if args.file:
        build_files(books[0], args.file, args.output, strip, args.html, args.style)
        return 0
    for book_path in books:
        build(book_path, args.lang, strip, args.html, args.style)
    return 0


if __name__ == "__main__":
    sys.exit(main())
