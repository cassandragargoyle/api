#!/usr/bin/env python3
#
# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details
#
"""Generate the SVG figures of the VIM book in English and Czech.

The figures of the sample invoice are computed from the real output of vim-parser
(``docs/book/vim/samples/faktura-2026-0042.pdf.vim.json``); the other figures are drawn
from the facts of the chapters. Every figure is written as ``<name>.en.svg`` and
``<name>.cs.svg`` into ``docs/book/vim/images/``.

Usage:
    python3 scripts/generate-book-vim-figures.py
"""

from __future__ import annotations

import json
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
BOOK_DIR = REPO_ROOT / "docs" / "book" / "vim"
SAMPLE = BOOK_DIR / "samples" / "faktura-2026-0042.pdf.vim.json"
OUT_DIR = BOOK_DIR / "images"

FONT = "font-family='Noto Sans, DejaVu Sans, sans-serif'"
MONO = "font-family='DejaVu Sans Mono, monospace'"
INK = "#1d2a3a"
MUTED = "#5f6b7a"
LINE = "#c4ccd6"
PAPER = "#ffffff"
TINT = "#f4f6f9"

# One colour per layer, the same as on the cover
LAYER_COLORS = {
    "physical": "#e5484d",
    "structural": "#d97706",
    "semantic": "#2f9e6a",
    "toc": "#0f8b8d",
    "extraction": "#1f6fb2",
    "prescriptive": "#7c3aed",
}

TEXTS = {
    "en": {
        "flat_title": "Plain text",
        "flat_note": "the columns are gone",
        "vim_title": "VIM, structural layer",
        "vim_note": "each label keeps its content",
        "customer": "Customer",
        "addressee": "Addressee",
        "q_physical": "what is there",
        "q_structural": "what it looks like",
        "q_semantic": "what it means",
        "q_toc": "how to find it",
        "q_extraction": "what the form holds",
        "q_prescriptive": "what it requires",
        "levels": "interpretation levels",
        "domain": "domain layers",
        "refers": "refers by ID",
        "since": "since",
        "provenance": "provenance: file, page, bbox, confidence, method",
        "page": "page 1 of faktura-2026-0042.pdf, redrawn from the physical layer",
        "entity": "semantic entity",
        "payment": "semantic payment",
        "raw": "raw_value",
        "value": "value",
        "engine": "OCR engine",
        "rule": "rule digit_lookalikes",
        "revision": "revision 1",
        "never_changes": "never changes",
        "current": "current state",
        "timeline": [
            ("30 Jan", "vim-parser starts: text blocks, HTML view, QR codes (1.1)"),
            ("31 Jan", "PF4J plugins, tables, metadata (1.2), stemming (1.3)"),
            ("8 Mar", "table of contents layer (1.4), block significance (1.5)"),
            ("24 Mar", "ADR-004: VIM becomes a universal container (2.0)"),
            ("8 May", "ADR-007: the Python port begins"),
            ("19 May", "extraction layer model in Java"),
            ("9 Oct", "ADR-009 ladder, ADR-010 prescriptive layer (2.1)"),
            ("16 Oct", "api binds layers to vim_version"),
        ],
        "year": "2026",
    },
    "cs": {
        "flat_title": "Prostý text",
        "flat_note": "sloupce zmizely",
        "vim_title": "VIM, strukturální vrstva",
        "vim_note": "každý popisek drží svůj obsah",
        "customer": "Zákazník",
        "addressee": "Adresát",
        "q_physical": "co tam je",
        "q_structural": "jak to vypadá",
        "q_semantic": "co to znamená",
        "q_toc": "jak to najít",
        "q_extraction": "co nese formulář",
        "q_prescriptive": "co předepisuje",
        "levels": "úrovně interpretace",
        "domain": "doménové vrstvy",
        "refers": "odkaz přes ID",
        "since": "od",
        "provenance": "původ: soubor, strana, bbox, spolehlivost, metoda",
        "page": "strana 1 souboru faktura-2026-0042.pdf, překreslená z fyzické vrstvy",
        "entity": "sémantická entita",
        "payment": "sémantická platba",
        "raw": "raw_value",
        "value": "value",
        "engine": "OCR engine",
        "rule": "pravidlo digit_lookalikes",
        "revision": "revize 1",
        "never_changes": "nikdy se nemění",
        "current": "aktuální stav",
        "timeline": [
            ("30. 1.", "vzniká vim-parser: textové bloky, HTML náhled, QR kódy (1.1)"),
            ("31. 1.", "pluginy PF4J, tabulky, metadata (1.2), stemming (1.3)"),
            ("8. 3.", "vrstva obsahu (1.4), význam bloků (1.5)"),
            ("24. 3.", "ADR-004: VIM se stává univerzálním kontejnerem (2.0)"),
            ("8. 5.", "ADR-007: začíná port do Pythonu"),
            ("19. 5.", "model extrakční vrstvy v Javě"),
            ("9. 10.", "žebřík ADR-009, preskriptivní vrstva ADR-010 (2.1)"),
            ("16. 10.", "api váže vrstvy na vim_version"),
        ],
        "year": "2026",
    },
}


def esc(s: str) -> str:
    return str(s).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


def svg(width: int, height: int, body: str, title: str) -> str:
    marker = (
        "<defs><marker id='a' viewBox='0 0 10 10' refX='9' refY='5' markerWidth='7' "
        f"markerHeight='7' orient='auto'><path d='M0,0 L10,5 L0,10 z' fill='{MUTED}'/></marker></defs>"
    )
    return (
        f"<svg xmlns='http://www.w3.org/2000/svg' width='{width}' height='{height}' "
        f"viewBox='0 0 {width} {height}'><title>{esc(title)}</title>{marker}"
        f"<rect width='{width}' height='{height}' rx='10' fill='{PAPER}'/>{body}</svg>\n"
    )


def text(x, y, s, size=13, color=INK, anchor="start", weight="normal", mono=False) -> str:
    return (
        f"<text x='{x}' y='{y}' font-size='{size}' fill='{color}' text-anchor='{anchor}' "
        f"font-weight='{weight}' {MONO if mono else FONT}>{esc(s)}</text>"
    )


def box(x, y, w, h, fill=TINT, stroke=LINE, rx=5, sw=1.4, opacity=1.0) -> str:
    return (
        f"<rect x='{x}' y='{y}' width='{w}' height='{h}' rx='{rx}' fill='{fill}' "
        f"fill-opacity='{opacity}' stroke='{stroke}' stroke-width='{sw}'/>"
    )


def arrow(x1, y1, x2, y2, color=MUTED, dash="") -> str:
    d = f" stroke-dasharray='{dash}'" if dash else ""
    return (
        f"<line x1='{x1}' y1='{y1}' x2='{x2}' y2='{y2}' stroke='{color}' stroke-width='1.6'"
        f"{d} marker-end='url(#a)'/>"
    )


def fig_fulltext_vs_vim(t: dict) -> str:
    """The experiment of the prologue: a two-column header, flattened and kept"""
    b = []
    b.append(text(30, 36, t["flat_title"], 16, weight="bold"))
    b.append(text(30, 56, t["flat_note"], 12, MUTED))
    b.append(box(30, 70, 300, 150, fill=TINT))
    lines = [f"{t['customer']} {t['addressee']}", "Ing. Jan Novák Ing. Jan Novák",
             "Hlavní 123/45 Hlavní 123/45", "120 00 Praha 2 120 00 Praha 2"]
    for i, s in enumerate(lines):
        b.append(text(46, 100 + i * 30, s, 13, mono=True))
    b.append(text(400, 36, t["vim_title"], 16, weight="bold"))
    b.append(text(400, 56, t["vim_note"], 12, MUTED))
    color = LAYER_COLORS["structural"]
    for i, header in enumerate([t["customer"], t["addressee"]]):
        x = 400 + i * 160
        b.append(box(x, 70, 148, 150, fill=color, stroke=color, opacity=0.08))
        b.append(text(x + 12, 96, header, 13, color, weight="bold"))
        for j, s in enumerate(["Ing. Jan Novák", "Hlavní 123/45", "120 00 Praha 2"]):
            b.append(text(x + 12, 126 + j * 26, s, 12.5))
    b.append(arrow(336, 145, 392, 145))
    return svg(730, 240, "".join(b), f"{t['flat_title']} / {t['vim_title']}")


def fig_layer_stack(t: dict) -> str:
    """Six layers: three interpretation levels and three domain layers"""
    rows = [
        ("prescriptive", "2.1", t["q_prescriptive"]),
        ("extraction", "2.0", t["q_extraction"]),
        ("toc", "1.4", t["q_toc"]),
        ("semantic", "1.0", t["q_semantic"]),
        ("structural", "1.0", t["q_structural"]),
        ("physical", "1.0", t["q_physical"]),
    ]
    b = []
    for i, (key, ver, q) in enumerate(rows):
        y = 24 + i * 56
        color = LAYER_COLORS[key]
        b.append(f"<path d='M70 {y} L520 {y} L490 {y + 44} L40 {y + 44} Z' fill='{color}' "
                 f"fill-opacity='0.12' stroke='{color}' stroke-width='2'/>")
        b.append(text(72, y + 28, key, 15, color, weight="bold", mono=True))
        b.append(text(250, y + 28, q, 13))
        b.append(text(476, y + 28, f"{t['since']} {ver}", 11.5, MUTED, anchor="end"))
    # Brackets for the two kinds of layers
    b.append(f"<path d='M540 24 h10 v156 h-10' fill='none' stroke='{MUTED}' stroke-width='1.4'/>")
    b.append(text(560, 108, t["domain"], 13, MUTED))
    b.append(f"<path d='M540 192 h10 v156 h-10' fill='none' stroke='{MUTED}' stroke-width='1.4'/>")
    b.append(text(560, 276, t["levels"], 13, MUTED))
    # Domain layers refer down by ID
    b.append(arrow(30, 60, 30, 330, dash="4 4"))
    b.append(f"<text x='18' y='200' font-size='11.5' fill='{MUTED}' {FONT} "
             f"transform='rotate(-90 18 200)' text-anchor='middle'>{esc(t['refers'])}</text>")
    return svg(720, 370, "".join(b), t["levels"])


def fig_timeline(t: dict) -> str:
    """Nine months of 2026: one row per event on a vertical line"""
    b = []
    events = t["timeline"]
    x, y0, step = 120, 64, 34
    b.append(text(30, 36, t["year"], 16, weight="bold"))
    b.append(f"<line x1='{x}' y1='{y0 - 10}' x2='{x}' y2='{y0 + step * (len(events) - 1) + 10}' "
             f"stroke='{INK}' stroke-width='2'/>")
    for i, (date, label) in enumerate(events):
        y = y0 + i * step
        b.append(text(x - 16, y + 5, date, 13, MUTED, anchor="end", weight="bold"))
        b.append(f"<circle cx='{x}' cy='{y}' r='6' fill='{LAYER_COLORS['extraction']}'/>")
        b.append(text(x + 18, y + 5, label, 13))
    return svg(640, y0 + step * len(events), "".join(b), "VIM 2026")


def fig_sample_page(t: dict) -> str:
    """The sample invoice redrawn from the physical layer, with blocks and their meaning"""
    doc = json.loads(SAMPLE.read_text(encoding="utf-8"))
    layers = doc["layers"]
    scale = 1.15

    def at(bx: float, by: float) -> tuple[float, float]:
        # Page coordinates of the parser (points, top left origin) to figure coordinates
        return (bx - 100) * scale + 22, (by - 100) * scale + 44

    b = [box(10, 10, 450, 500, fill=PAPER, stroke=LINE)]
    color = LAYER_COLORS["physical"]
    for block in layers["physical"]["blocks"]:
        bb = block["bbox"]
        x, y = at(bb["x"], bb["y"])
        b.append(box(f"{x - 4:.1f}", f"{y - 4:.1f}", f"{bb['width'] * scale + 8:.1f}",
                     f"{bb['height'] * scale + 10:.1f}", fill=color, stroke=color, rx=3,
                     sw=1.4, opacity=0.06))
        b.append(text(f"{x - 4:.1f}", f"{y - 8:.1f}", block["id"], 10, color, mono=True))
        for el in block["text_elements"]:
            eb = el["bbox"]
            size = 15 if el["font_size"] > 20 else 10
            tx, ty = at(eb["x"], eb["y"] + eb["height"])
            weight = "bold" if "Bold" in el.get("font_name", "") else "normal"
            b.append(text(f"{tx:.1f}", f"{ty:.1f}", el["text"], size, weight=weight))
    for code in layers["physical"].get("barcodes", []):
        bb = code["bbox"]
        x, y = at(bb["x"], bb["y"])
        s = bb["width"] * scale
        b.append(box(f"{x:.1f}", f"{y:.1f}", f"{s:.1f}", f"{s:.1f}", fill=INK, stroke=INK, rx=1))
        # Three finder squares make the dark square read as a QR code
        for fx, fy in ((0, 0), (s - 22, 0), (0, s - 22)):
            b.append(box(f"{x + fx + 4:.1f}", f"{y + fy + 4:.1f}", "14", "14", fill=PAPER,
                         stroke=PAPER, rx=1))
        b.append(text(f"{x + s + 8:.1f}", f"{y + 14:.1f}", f"{code['id']} · {code['format']}",
                      10, color, mono=True))
    # Meaning on the right: what the semantic layer made of the blocks
    sem = layers["semantic"]
    sx = 480
    green = LAYER_COLORS["semantic"]
    y = 60
    for ent in sem.get("entities", []):
        lines = [f"{t['entity']}: {ent['type']}", f"source_block: {ent['source_block']}"]
        lines += [f"{k}: {v}" for k, v in ent["fields"].items()]
        lines.append(f"confidence: {ent['confidence']}")
        h = 20 + len(lines) * 17
        b.append(box(sx, y, 270, h, fill=green, stroke=green, opacity=0.07))
        for i, s in enumerate(lines):
            b.append(text(sx + 10, y + 22 + i * 17, s, 10.5, INK if i else green,
                          weight="bold" if i == 0 else "normal", mono=i > 0))
        y += h + 16
    for pay in sem.get("payments", []):
        fields = {k: v for k, v in pay["fields"].items() if v is not None}
        lines = [f"{t['payment']}: {pay['format']}", f"source_barcode: {pay['source_barcode']}"]
        lines += [f"{k}: {v}" for k, v in fields.items()]
        h = 20 + len(lines) * 17
        b.append(box(sx, y, 270, h, fill=green, stroke=green, opacity=0.07))
        for i, s in enumerate(lines):
            b.append(text(sx + 10, y + 22 + i * 17, s, 10.5, INK if i else green,
                          weight="bold" if i == 0 else "normal", mono=i > 0))
        y += h + 16
    b.append(text(20, 532, t["page"], 11, MUTED))
    return svg(770, 548, "".join(b), t["page"])


def fig_revision(t: dict) -> str:
    """One field of the extraction layer: raw value, revision by a rule, current value"""
    b = []
    blue = LAYER_COLORS["extraction"]
    b.append(box(30, 40, 190, 90, fill=TINT))
    b.append(text(125, 30, t["engine"], 12, MUTED, anchor="middle"))
    b.append(text(46, 72, t["raw"], 12, MUTED, mono=True))
    b.append(text(46, 106, "\"A-I007\"", 20, INK, weight="bold", mono=True))
    b.append(text(125, 152, t["never_changes"], 11.5, MUTED, anchor="middle"))
    b.append(arrow(226, 85, 300, 85))
    b.append(box(306, 40, 210, 90, fill=blue, stroke=blue, opacity=0.08))
    b.append(text(411, 30, t["revision"], 12, blue, anchor="middle", weight="bold"))
    b.append(text(320, 70, "actor: rule", 11.5, mono=True))
    b.append(text(320, 90, "reason: character_replacement", 11, mono=True))
    b.append(text(320, 110, "I → 1", 13, blue, weight="bold", mono=True))
    b.append(text(411, 152, t["rule"], 11.5, MUTED, anchor="middle"))
    b.append(arrow(522, 85, 596, 85))
    b.append(box(602, 40, 190, 90, fill=TINT, stroke=INK))
    b.append(text(618, 72, t["value"], 12, MUTED, mono=True))
    b.append(text(618, 106, "\"A-1007\"", 20, INK, weight="bold", mono=True))
    b.append(text(697, 152, t["current"], 11.5, MUTED, anchor="middle"))
    return svg(820, 170, "".join(b), t["revision"])


FIGURES = {
    "fulltext-vs-vim": fig_fulltext_vs_vim,
    "layer-stack": fig_layer_stack,
    "timeline-2026": fig_timeline,
    "sample-invoice-layers": fig_sample_page,
    "extraction-revision": fig_revision,
}


def main() -> int:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    for lang, texts in TEXTS.items():
        for name, draw in FIGURES.items():
            path = OUT_DIR / f"{name}.{lang}.svg"
            path.write_text(draw(texts), encoding="utf-8")
            print(f"[figures] {path.relative_to(REPO_ROOT)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
