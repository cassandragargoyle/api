# Issue #62: Collect review comments on the VIM book

**Type**: Task
**Priority**: Medium
**Status**: Implemented
**Created**: 2026-10-25
**Closed**: 2026-10-25
**GitHub**: #62
**Component**: docs
**Related**: #61

## Summary

A single place for review comments on the VIM book (`docs/book/vim/`, English and Czech
editions). Comments are added to the list below as they come, and applied to the chapters
in batches, followed by a rebuild of the PDFs.

## Problem Description / Motivation

The book was added in #61. Reading the built PDFs brings small comments on wording, story
and facts. Collecting them in one issue keeps them from getting lost and lets them be
applied together, in both language editions, with one rebuild and one review.

## Review Comments

Each comment names the chapter, the place (quoted text) and the proposed change. When a
comment is applied, mark it as done.

| No. | Chapter | Place | Comment | State |
| --- | ------- | ----- | ------- | ----- |
| 1 | 3 Inside a vim.json, section *What the Parser Got Wrong* | EN: "An honest book has to show the other half too." CS: "Poctivá kniha musí ukázat i druhou polovinu." | "The other half" does not read well. Use an image people know: the other side of the coin (CS: *druhou stranu mince*) or the other side of the face (CS: *druhou tvář*). | Done: "the other side of the coin", CS *druhou stranu mince* |
| 2 | 3 Inside a vim.json, section *What the Parser Got Wrong* | EN: "The same file contains three mistakes, and each of them teaches something." CS: "Tentýž soubor obsahuje tři chyby a každá něco učí." | Not "teaches": each mistake opens a new horizon or points in a new direction (CS: *každá otevírá nový obzor nebo směr*). | Done: "opens a new direction", CS *otevírá nový obzor* |

## Scope

### In scope

- Wording, story, facts and figures of the chapters in `docs/book/vim/chapters/`, in both
  editions
- Cover, dedication, part pages and other texts in `docs/book/vim/book.json`
- Rebuild of `vim-book-en.pdf` and `vim-book-cs.pdf` after a batch of changes

### Out of scope (follow-up issues)

- New chapters or new books
- Changes of the generator or the template that are not needed by a comment

## Acceptance Criteria

1. Every comment in the table is applied in both editions, or closed with a reason
2. The rules of `docs/book/WRITING-STYLE.md` still hold (written to be heard, identifiers
   only in *Sources*, examples in the language of the edition)
3. The PDFs are rebuilt and committed with the change

## Testing

```bash
make book       # both PDFs build
make lint-md    # 0 issues
```

Open both PDFs and read the changed passages.
