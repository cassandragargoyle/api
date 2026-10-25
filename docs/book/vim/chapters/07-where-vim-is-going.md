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

| Problem | What goes wrong |
| ------- | --------------- |
| Page footers | Page numbers, running titles and copyright lines appear in the text at every page break |
| Multi-column layout | Text read in stored order zig-zags between the columns |
| Tables across page breaks | One logical table becomes two, with the header row twice |

The first challenge compares `vim-parser` with Marker, an open-source converter
from PDF to Markdown, on these three problems. At the time of writing it is a stub: the
pipeline is defined, the runs have not started. That is not a weakness of the lab. It is
its method: first agree on what will be measured and what counts as a win, then measure.

The melted columns of our invoice belong to the family of the multi-column problem, and
the table that the detector found over a large part of the page to the family of the
table problem. The sample
of this book could become a small entry in that collection.

## Questions Left Open on Purpose

Good architecture decisions do not pretend to know everything. The architecture decisions of VIM end with
open questions, and they are worth listing, because they show where the format will grow.

**A place for experiments.** The `layers` object is closed, and a new layer needs a schema
and two implementations. That protects consumers, but it leaves no place for content that
is not yet stable enough for a contract. The decision about new layers names a possible *extension area* for such
content and leaves it as a separate decision.

**A registry of layers.** With six layers, the list of layers with their owners and
producers lives comfortably in the format specification. With more, a short registry may
be needed.

**A language for rules.** A provision of the prescriptive layer can say that it is checked
automatically, and it carries a `rule` object. What may be written in that object is not
fixed by the contract; tools must agree on it outside. A small rule language is an open
question of the decision that created the prescriptive layer, together with the format of the assessment document that records the
result of a compliance check.

**The home of the LLM producer.** The prescriptive layer is written by a producer that uses
a language model. Where that producer lives, and how it is run, is also still open; the same decision
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
provenance. A model that answers *Jane Sample* is useful. A system that answers *Jane Sample,
page 1, block 4, confidence 0.8, found by matching the label Customer* can be
checked, corrected and trusted. When we look closer at the reading of documents, we find
a varied world of small, patient skills: grouping, labelling, measuring, doubting. It is
easy to overlook them when we chase new goals. VIM keeps them, and it keeps them in a form
that a person, a program and a language model can all read.

The prescriptive layer points to the third answer. There, a language model is invited in
as a producer, and the format makes room for it honestly: every provision it proposes
starts as `proposed`, and only a person makes it `confirmed`. The model brings speed; the
layers bring evidence; the person brings the final word.

The Czech author Lukáš Sedláček, in his book about people in the age of AI, recalls a
distinction made by the French anthropologist Claude Lévi-Strauss. There is *wild*
thinking, free and associative, which looks for likenesses without fixed rules, and there
is *domesticated* thinking, which keeps strictly to categories and gave us science, law
and, in the end, AI itself. Domesticated thinking alone, Sedláček writes, would not be
capable of innovation without the wild one. VIM is a small place where the two meet. The
heuristics of the parser and the language model of the prescriptive layer guess, associate
and propose. The closed schema, the stable identifiers and the person who confirms keep the
order. Neither half would be enough alone.

## Back to the Glass

We started with three sheets of glass over a page. Nine months later there are six, and
more may come, each one through the gates and criteria of the ladder. The idea underneath
has not changed: keep what is there, keep how it is grouped, keep what it means, and let
every value say where it came from and how sure we are.

A document that went through VIM can say what it means. That was the promise of the name,
and it is the promise that the next versions will keep working on.

> VIM was made with the help of artificial intelligence, for people who live in the age of
> artificial intelligence, and for artificial intelligence itself: one model that a person
> and a machine can read alike.
>
> — Zdeněk Kurc

## Sources

- Catalog entries I-PDF-001, I-PDF-002 and I-PDF-003 and challenge C001: `README.md`,
  `docs/catalog/pdf/` and `challenges/C001-pdf-vs-marker/` of
  `portunix-vim-parser-research-lab`
- ADR-009 and ADR-010, section *Open Questions*, `portunix-architecture`
- `src/ptx_reco/vim/README.md` of `portunix-reco`
- Lukáš Sedláček, *Nazí v AI době* (2025, ISBN 978-80-53061-04-9): the introduction (pages
  13 to 16) and the passage on wild and domesticated thinking (pages 21 and 22)
- Claude Lévi-Strauss, *La Pensée sauvage* (1962), in English *The Savage Mind*
