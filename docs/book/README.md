---
title: Books of the Repository
description: Use when you build, add, or change a PDF book in docs/book/ (the VIM book or a new one), its book.json, style.json, template, or chapters.
category: reference
ai_load: scoped
status: active
language: en
created: 2026-10-25
last_updated: 2026-10-25
related:
  - WRITING-STYLE.md
  - vim/book.json
  - ../../scripts/build-book-pdf.py
---

# Books of the Repository

## Overview

`docs/book/` holds PDF books built from Markdown chapters, in English and in Czech. Each book
has its own directory with a manifest (`book.json`); the generator, the template and the
voice of the chapters are shared. The generator and the template come from the books of
portunix-reco and portunix-reco-studio, so the books of the ecosystem look the same.

| Book | Directory | Output |
| ---- | --------- | ------ |
| VIM: the Layered Information Model | [vim/](vim/) | `vim/vim-book-en.pdf`, `vim/vim-book-cs.pdf` |

```bash
make book                                                    # all books, all languages
uv run scripts/build-book-pdf.py --book docs/book/vim/book.json --lang cs
uv run scripts/build-book-pdf.py --html                      # also write the HTML (debugging)
python3 scripts/generate-book-vim-figures.py                 # figures of the VIM book
```

The generator needs `uv` (it declares its Python dependencies inline), the Pango library of
the system for WeasyPrint, and for Mermaid diagrams Node.js (`npx`) and a Chrome of the
system. The HTML of `--html` is not in Git.

## Directory Layout

| Path | Content |
| ---- | ------- |
| `WRITING-STYLE.md` | The voice of the chapters: a story, not a manual; facts verified and sourced |
| `template/book.html.j2` | Jinja2 template: cover, contents, part pages, chapters |
| `template/book.css` | Print stylesheet (A4, running headers, page numbers, figures, code) |
| `template/covers/` | Drawn cover pictures, one per book (`cover_art` in `book.json`) |
| `<book>/book.json` | Definition of the content: languages, cover texts, labels, parts, chapters |
| `<book>/style.json` | Format configuration: page, fonts, colours, table rules, Mermaid |
| `<book>/chapters/` | Chapters (`NN-name.md` and `NN-name.cs.md`) |
| `<book>/images/` | Figures (`name.en.svg`, `name.cs.svg`) and the optional cover image |
| `<book>/samples/` | Input files and results that the chapters show, so every example can be repeated |
| `../../scripts/build-book-pdf.py` | Generator (markdown-it-py, Jinja2, Pygments, WeasyPrint) |

## The Definition File

| Key | Meaning |
| --- | ------- |
| `languages` | Languages to build; the first one is the source language |
| `template`, `stylesheet` | Template and stylesheet, relative to `book.json` (`../template/...`) |
| `style` | Format configuration; `--style other.json` uses another one for one build |
| `cover_image` | Optional image of the cover; without the file, the cover shows `cover_art` |
| `cover_art` | Drawn cover picture, a Jinja2 include relative to the template directory |
| `output_dir`, `file_name` | Where the PDF goes; `{lang}` in the file name is the language |
| `diagram_cache` | Cache of rendered Mermaid diagrams (`dist/book/.diagrams/`, not in Git) |
| `repository_url` | Links to other files of the repository become GitHub links |
| `chapters[].source` | Markdown file, relative to `book.json`; the translation of `name.md` is `name.<lang>.md` |
| `chapters[].title` | Optional title per language; without it the H1 of the file is the title |
| `strip_sections` | H2 sections to leave out (whole book or one chapter) |
| `drop_translation_note` | Leaves out the blockquote under the H1 of a translation |
| `figure_captions` | An image alone in a paragraph becomes "Figure N.M" with its alt text as caption |
| `labels` | Fixed texts of the template per language, including the texts of the drawn cover |
| `meta.<lang>.dedication` | Optional dedication, a list of paragraphs; printed on its own page after the cover |
| `meta.<lang>.main_authors` | Optional list of the main authors; printed with the author (`meta.author`) at the foot of the page after the cover |

When a translation is missing, the English chapter is used and the chapter shows a note.

## How the Markdown Is Processed

- The YAML frontmatter is removed; it is for AI assistants, not for the reader
- The H1 of a file is the chapter title; all other headings stay
- Anchors get a chapter prefix, so links inside a chapter work in the whole book
- Relative image paths are resolved from the Markdown file; embedded `data:` images work
- A ` ```mermaid ` block becomes a numbered figure; a line `%% caption: ...` gives the caption
- Code blocks are coloured by Pygments, by the language of the fence
- A passage between `<!-- book:skip -->` and `<!-- /book:skip -->` is left out of the book

## Adding a Chapter

1. Write `chapters/NN-name.md` (English) and `chapters/NN-name.cs.md` (Czech), following
   [WRITING-STYLE.md](WRITING-STYLE.md): the book tells a story, it is not a manual
2. Run every command and every code example of the chapter, and copy the real output
3. Add `{ "source": "chapters/NN-name.md" }` to a part in `book.json`
4. Run `make book`, look at the PDF, and run `make lint-md`

An existing document (a contract README, a document of `/code-textbook`) can be added the
same way, with its path relative to `book.json`; it is not copied.

## Adding a Book

1. Create `docs/book/<name>/` with `book.json`, `style.json`, `chapters/` and `images/`
2. Draw a cover picture in `template/covers/<name>.svg.j2` and name it in `cover_art`, or
   provide `images/book-cover.webp`
3. `make book` finds every `docs/book/*/book.json` by itself
