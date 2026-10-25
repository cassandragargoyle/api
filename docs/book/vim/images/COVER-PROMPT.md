---
title: Cover Image Prompt for the VIM Book
description: Use when you generate or replace the cover image of the VIM book (images/book-cover.webp) with an image model such as ChatGPT. Holds the prompt, the format requirements and the steps to put the image into the book.
category: reference
ai_load: on-demand
status: active
language: en
created: 2026-10-25
last_updated: 2026-10-25
related:
  - ../book.json
  - ../../README.md
---

# Cover Image Prompt for the VIM Book

## Overview

The book can show a generated image on its cover. When `images/book-cover.webp` exists, the
template puts it into the lower part of the cover (the whole width of A4, 130 mm high,
cropped to fill) and the dark upper part keeps the kicker, the title and the subtitle. When
the file does not exist, the cover shows its drawn picture of the six layers.

The image must therefore:

- be landscape, about **1.6 : 1** (for example 1536 × 960 or 1792 × 1024 px)
- fade into the cover colour **`#1d2a3a`** (dark navy) along its top edge, so that it
  joins the dark area with the title without a visible line
- contain **no text, letters, numbers or logos**: image models write text badly, and the
  title is printed by the template
- show the idea of the book: **one document, read in transparent layers**, not the text
  editor Vim

## The Prompt

Copy the text below into ChatGPT (image generation) as it is. English works better than
Czech for image models.

```text
Create a wide landscape book cover illustration, aspect ratio about 16:10, ultra modern,
clean and elegant, for a technical book about a "layered information model" that reads
documents in layers.

Scene: a single paper document page (an invoice-like page with abstract grey text lines, a
small table grid and a small square QR-like pattern, but NO readable text) lies at a slight
angle at the bottom left. Above it, six thin sheets of glass float in a vertical stack,
slightly separated, in isometric perspective, each sheet perfectly parallel to the page.
Each glass sheet carries a faint glowing trace of the page: the lowest sheet traces the
exact outlines of the text lines and the table, the next sheet shows soft rounded frames
around groups of lines, the next shows small glowing nodes connected by thin lines, as if
meaning was extracted. Thin vertical light threads connect points on the upper sheets
exactly down to their source spots on the page, like provenance lines.

Colours of the six sheets, from bottom to top, as subtle tinted edges and glows:
coral red #e5484d, amber #d97706, green #2f9e6a, teal #0f8b8d, blue #1f6fb2,
violet #7c3aed. The glass is clear and slightly frosted, with crisp edges and soft
reflections.

Background: deep dark navy #1d2a3a with a very subtle grid and soft volumetric light. The
top 25 percent of the image fades smoothly into solid #1d2a3a, completely flat and empty,
so that a title can be placed above it.

Style: premium editorial 3D render, soft studio lighting, high detail, calm and precise,
minimal, plenty of negative space on the right side. Not cartoonish, not cyberpunk, no
neon overload.

Strictly no text, no letters, no numbers, no words, no logos, no watermarks, no user
interface, no code editor, no keyboard, no computer screen.
```

## Variants

When the first result is too busy or too plain, add one of these sentences to the end of
the prompt:

- *Make it more minimal: fewer details on the sheets, larger empty areas, only the
  outlines and the light threads.*
- *Make it warmer: a soft warm light from the bottom left falls on the paper page, the
  glass reflects it.*
- *Show the stack from a lower angle, so that the sheets look like a small tower of glass
  above the page.*
- *Replace the invoice page with a page of a technical drawing with a title block in the
  bottom right corner, still without any readable text.*

## Putting the Image into the Book

1. Save the generated image, for example as `cover.png`
2. Check it: no text or letters anywhere, the top edge fades into dark navy
3. Convert it to WebP next to this file:

   ```bash
   uv run --with pillow python3 -c "from PIL import Image; Image.open('cover.png').convert('RGB').save('docs/book/vim/images/book-cover.webp', quality=88)"
   ```

4. Build the book with `make book` and look at the cover; the generator writes
   *cover image ... missing, using the drawn cover* when the file is not found
5. When the top edge does not join the dark area well, darken the top of the image or ask
   for a stronger fade in the prompt

The current cover was generated from this prompt; its original is kept as
`multi-layer-document-analysis.png` (1585 × 992 px), and `book-cover.webp` is made from it
with the top 7 % faded into `#1d2a3a`.

To go back to the drawn cover, delete `book-cover.webp`.
