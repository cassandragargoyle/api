# SPDX-License-Identifier: MIT
"""Small helpers for the figures of code-textbook documents

Import this module from a figure script in the scratchpad, build each figure with svg(),
save it, render a preview with render_png(), look at the preview, and embed the figure
with data_uri(). Colors work on a white background, so the figures stay readable when
the Markdown viewer uses a dark theme.
"""
import base64
import pathlib
import subprocess

FONT = "font-family='Segoe UI, Helvetica, Arial, sans-serif'"
MONO = "font-family='Consolas, Menlo, monospace'"
INK = "#1f2933"
MUTED = "#6b7785"
LINE = "#c9d1da"
BLUE = "#2563eb"
BLUE_BG = "#dbeafe"
ORANGE = "#c2410c"
ORANGE_BG = "#ffedd5"
GREEN = "#15803d"
GREEN_BG = "#dcfce7"
GREY_BG = "#f1f4f7"
WHITE = "#ffffff"

# Marker ids for line(): a = ink, b = blue, o = orange, g = green
_MARKERS = (("a", INK), ("b", BLUE), ("o", ORANGE), ("g", GREEN))


def svg(width, height, body, title):
	"""Wrap the body in an SVG document with a white rounded background and the arrow markers"""
	defs = "<defs>" + "".join(
		f"<marker id='{name}' viewBox='0 0 10 10' refX='9' refY='5' markerWidth='7' markerHeight='7' orient='auto'>"
		f"<path d='M0,0 L10,5 L0,10 z' fill='{color}'/></marker>" for name, color in _MARKERS) + "</defs>"
	return (f"<svg xmlns='http://www.w3.org/2000/svg' width='{width}' height='{height}' viewBox='0 0 {width} {height}'>"
		f"<title>{_escape(title)}</title>{defs}<rect width='{width}' height='{height}' rx='10' fill='{WHITE}'/>{body}</svg>")


def text(x, y, s, size=14, color=INK, anchor="middle", weight="normal", mono=False):
	"""Text at (x, y); y is the baseline"""
	return (f"<text x='{x}' y='{y}' font-size='{size}' fill='{color}' text-anchor='{anchor}' "
		f"font-weight='{weight}' {MONO if mono else FONT}>{_escape(s)}</text>")


def box(x, y, w, h, fill=GREY_BG, stroke=LINE, rx=4, sw=1):
	"""Rounded rectangle"""
	return f"<rect x='{x}' y='{y}' width='{w}' height='{h}' rx='{rx}' fill='{fill}' stroke='{stroke}' stroke-width='{sw}'/>"


def line(x1, y1, x2, y2, color=INK, marker="a", sw=2):
	"""Arrow from (x1, y1) to (x2, y2); marker=None draws a plain line"""
	end = f" marker-end='url(#{marker})'" if marker else ""
	return f"<line x1='{x1}' y1='{y1}' x2='{x2}' y2='{y2}' stroke='{color}' stroke-width='{sw}'{end}/>"


def data_uri(svg_text):
	"""Base64 data URI for a Markdown image: ![alt text](data:image/svg+xml;base64,...)"""
	return "data:image/svg+xml;base64," + base64.b64encode(svg_text.encode("utf-8")).decode("ascii")


def render_png(svg_path, width, height, browser="google-chrome"):
	"""Render a PNG preview with a headless browser; ImageMagick draws arrow markers wrong"""
	svg_path = pathlib.Path(svg_path).resolve()
	png_path = svg_path.with_suffix(".png")
	subprocess.run([browser, "--headless=new", "--disable-gpu", "--hide-scrollbars",
		f"--window-size={int(width)},{int(height)}", f"--screenshot={png_path}", svg_path.as_uri()],
		check=False, capture_output=True, timeout=60)
	return png_path


def _escape(s):
	return str(s).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
