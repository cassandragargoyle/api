#!/usr/bin/env python3
"""Generate the CassandraGargoyle mark as SVG.

The mark is a C made of a network of nodes around a G whose bar ends in a node.
One node of the network is green: a verified source.

Usage:
    python brand/mark.py [preset] > mark.svg     # presets: dark (default)

Render PNG files with ImageMagick:
    magick -background none brand/mark-dark.svg -resize 256x256 brand/png/mark-dark-256.png
"""

from __future__ import annotations

import math
import sys

GREEN = "#76B900"
GRAPHITE = "#1A1A1A"
LIGHT = "#F2F2F2"

# preset name -> background, network color, G and accent color
PRESETS = {
    "dark": (GRAPHITE, LIGHT, GREEN),
}

SIZE = 1024
CENTRE = SIZE / 2
NETWORK_RADIUS = 290
NODE_RADIUS = 58
EDGE_WIDTH = 36
NODE_ANGLES = (50, 115, 180, 245, 310)
G_OUTER = 200
G_THICKNESS = 70
G_START_ANGLE = 45


def fmt(value: float) -> str:
    """Number without needless decimals"""
    return f"{value:.1f}".rstrip("0").rstrip(".")


def polar(radius: float, angle: float) -> tuple[float, float]:
    """Point on a circle around the centre, angle in degrees counter-clockwise from the right"""
    rad = math.radians(angle)
    return CENTRE + radius * math.cos(rad), CENTRE - radius * math.sin(rad)


def circle(x: float, y: float, r: float, fill: str) -> str:
    return f'<circle cx="{fmt(x)}" cy="{fmt(y)}" r="{fmt(r)}" fill="{fill}"/>'


def edge(a: tuple[float, float], b: tuple[float, float], width: float, fill: str) -> str:
    """Straight edge between two nodes as a filled polygon (renders the same everywhere)"""
    dx, dy = b[0] - a[0], b[1] - a[1]
    length = math.hypot(dx, dy)
    px, py = -dy / length * width / 2, dx / length * width / 2
    points = [(a[0] + px, a[1] + py), (b[0] + px, b[1] + py), (b[0] - px, b[1] - py), (a[0] - px, a[1] - py)]
    return f'<path d="M{" L".join(f"{fmt(x)} {fmt(y)}" for x, y in points)} Z" fill="{fill}"/>'


def letter_g(fill: str) -> str:
    """G: an arc from the upper right round to the middle right, and a bar inwards ending in a node"""
    outer, inner = G_OUTER, G_OUTER - G_THICKNESS
    x0, y0 = polar(outer, G_START_ANGLE)
    x1, y1 = polar(outer, 360)
    x2, y2 = polar(inner, 360)
    x3, y3 = polar(inner, G_START_ANGLE)
    arc = (f'<path d="M{fmt(x0)} {fmt(y0)} A{outer} {outer} 0 1 0 {fmt(x1)} {fmt(y1)} '
           f'L{fmt(x2)} {fmt(y2)} A{inner} {inner} 0 1 1 {fmt(x3)} {fmt(y3)} Z" fill="{fill}"/>')
    half = G_THICKNESS / 2
    bar_end = CENTRE + math.sqrt(outer * outer - half * half)
    # the right end of the bar follows the outer circle, so the bar and the arc share one edge
    bar = (f'<path d="M{fmt(CENTRE)} {fmt(CENTRE - half)} H{fmt(bar_end)} '
           f'A{outer} {outer} 0 0 1 {fmt(bar_end)} {fmt(CENTRE + half)} H{fmt(CENTRE)} Z" fill="{fill}"/>')
    return arc + bar + circle(CENTRE, CENTRE, half + 18, fill)


def mark(preset: str = "dark") -> str:
    background, network, accent = PRESETS[preset]
    nodes = [polar(NETWORK_RADIUS, a) for a in NODE_ANGLES]
    parts = [edge(a, b, EDGE_WIDTH, network) for a, b in zip(nodes, nodes[1:])]
    parts += [circle(x, y, NODE_RADIUS, accent if i == 0 else network) for i, (x, y) in enumerate(nodes)]
    parts.append(letter_g(accent))
    # optical centring: the network opens to the right, so centre its bounding box
    left = min(x for x, _ in nodes) - NODE_RADIUS
    right = max(max(x for x, _ in nodes) + NODE_RADIUS, CENTRE + G_OUTER)
    shift = CENTRE - (left + right) / 2
    body = "".join(parts)
    return (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {SIZE} {SIZE}" width="{SIZE}" height="{SIZE}" '
            f'role="img" aria-label="CassandraGargoyle">'
            f'<title>CassandraGargoyle</title>'
            f'<rect width="{SIZE}" height="{SIZE}" rx="200" fill="{background}"/>'
            f'<g transform="translate({fmt(shift)} 0)">{body}</g></svg>\n')


if __name__ == "__main__":
    sys.stdout.write(mark(sys.argv[1] if len(sys.argv) > 1 else "dark"))
