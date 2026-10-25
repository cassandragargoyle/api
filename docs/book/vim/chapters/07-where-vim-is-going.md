---
title: Where VIM Is Going
description: Closing chapter of the VIM book about open problems and the future of the format. Read when you plan work on VIM, look for known extraction problems, or want the open questions of the VIM architecture decisions.
category: reference
ai_load: on-demand
status: active
language: en
created: 2026-10-25
last_updated: 2026-10-25
related:
  - 03-inside-a-vim-json.md
  - 05-a-promise-in-json-schema.md
---

# Where VIM Is Going

## The Honest Middle

Every young format lives in an honest middle. It is good enough to be used every day, and
not yet good enough to be forgotten. VIM is there now. The sample invoice of this book
showed both halves on one page: the customer and the payment read right, with their
evidence, and a title that swallowed the supplier, a table whose columns melted. The
format held both kinds of result equally well. The reading itself still has a long way to
go.

This last chapter looks at that way: the problems that are known, the place where they are
studied, the questions that the architecture decisions left open on purpose, and the idea
that holds it all together.

## A Laboratory for Hard Pages

In May 2026, the team founded a separate repository for the research on the parser,
`portunix-vim-parser-research-lab`. It has an unusual rule: it never changes the parser
directly. It keeps the parser as a read-only submodule, runs it against collections of
documents, compares it with other tools and hands its findings to the parser team as
issues.

The lab works with two kinds of records. A **catalog entry** describes a known extraction
problem and lives for a long time; it is not closed just because one experiment ended. A
**challenge** is one measurement: a versioned, repeatable comparison with a clear win
condition and a written verdict. The first three catalog entries are exactly the problems
that any reader of PDFs meets sooner or later:

| ID | Problem | What goes wrong |
| --- | ------- | --------------- |
| I-PDF-001 | Page footers | Page numbers, running titles and copyright lines appear in the text at every page break |
| I-PDF-002 | Multi-column layout | Text read in stored order zig-zags between the columns |
| I-PDF-003 | Tables across page breaks | One logical table becomes two, with the header row twice |

The first challenge, C001, compares `vim-parser` with Marker, an open-source converter
from PDF to Markdown, on these three problems. At the time of writing it is a stub: the
pipeline is defined, the runs have not started. That is not a weakness of the lab. It is
its method: first agree on what will be measured and what counts as a win, then measure.

The melted columns of our invoice belong to the family of I-PDF-002, and the table that
the detector found over a large part of the page belongs to the family of I-PDF-003. The sample
of this book could become a small entry in that collection.

## Questions Left Open on Purpose

Good architecture decisions do not pretend to know everything. The ADRs of VIM end with
open questions, and they are worth listing, because they show where the format will grow.

**A place for experiments.** The `layers` object is closed, and a new layer needs a schema
and two implementations. That protects consumers, but it leaves no place for content that
is not yet stable enough for a contract. ADR-009 names a possible *extension area* for such
content and leaves it as a separate decision.

**A registry of layers.** With six layers, the list of layers with their owners and
producers lives comfortably in the format specification. With more, a short registry may
be needed.

**A language for rules.** A provision of the prescriptive layer can say that it is checked
automatically, and it carries a `rule` object. What may be written in that object is not
fixed by the contract; tools must agree on it outside. A small rule language is an open
question of ADR-010, together with the format of the assessment document that records the
result of a compliance check.

**The home of the LLM producer.** The prescriptive layer is written by a producer that uses
a language model. Where that producer lives, and how it is run, is also still open; ADR-010
suggests that tools which use a language model at development time run as Claude Code
skills.

**The second half of the Python port.** The Python model writes the extraction and
prescriptive layers but does not read them back, and the recognition pipeline of reco does
not fill the extraction layer yet. Until it does, the contract names a producer that is
planned rather than real, and the documentation says so.

## Small Truths for Large Models

There is a temptation in the age of large language models to think that structure no
longer matters. A model can read a PDF, or even a photograph of a page, and answer
questions about it. Why keep three sheets of glass, IDs and confidences, when a model can
simply look?

The experiment of the prologue is the first answer. The same model, given the same
document, was unsure with plain text and sure with the layers. Structure is not a crutch
for weak readers; it is how a strong reader shows its work. The second answer is
provenance. A model that answers *Jana Příkladová* is useful. A system that answers *Jana
Příkladová, page 1, block 4, confidence 0.8, found by matching the label Odběratel* can be
checked, corrected and trusted. When we look closer at the reading of documents, we find
a varied world of small, patient skills: grouping, labelling, measuring, doubting. It is
easy to overlook them when we chase new goals. VIM keeps them, and it keeps them in a form
that a person, a program and a language model can all read.

The prescriptive layer points to the third answer. There, a language model is invited in
as a producer, and the format makes room for it honestly: every provision it proposes
starts as `proposed`, and only a person makes it `confirmed`. The model brings speed; the
layers bring evidence; the person brings the final word.

## Back to the Glass

We started with three sheets of glass over a page. Nine months later there are six, and
more may come, each one through the gates and criteria of the ladder. The idea underneath
has not changed: keep what is there, keep how it is grouped, keep what it means, and let
every value say where it came from and how sure we are.

A document that went through VIM can say what it means. That was the promise of the name,
and it is the promise that the next versions will keep working on.

## Sources

- `README.md`, `docs/catalog/pdf/` and `challenges/C001-pdf-vs-marker/` of
  `portunix-vim-parser-research-lab`
- ADR-009 and ADR-010, section *Open Questions*, `portunix-architecture`
- `src/ptx_reco/vim/README.md` of `portunix-reco`
