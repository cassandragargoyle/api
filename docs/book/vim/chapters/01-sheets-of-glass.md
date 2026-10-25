---
title: Sheets of Glass
description: Chapter of the VIM book about the idea of the layered model. Read when you need the principles of VIM, the meaning of each layer and the reason why provenance and confidence are on every element.
category: reference
ai_load: on-demand
status: active
language: en
created: 2026-10-25
last_updated: 2026-10-25
related:
  - 00-who-is-the-customer.md
  - 02-from-1-0-to-2-1.md
---

# Sheets of Glass

## Reading Is Not One Act

Watch a person who reads an invoice for the first time. The eyes do not run from the first
letter to the last. They first take in the page as a picture: a bold line at the top, two
blocks of text side by side, a table, a black square in a corner. Then they group: this
block is the supplier, that one is the customer, this grid is the list of items. Only then
comes meaning: the number at the bottom right is the total, in Czech crowns, and the black
square is a QR code with which the bill can be paid.

Or watch the same person over a page of classified ads in a newspaper. Dozens of small
notices stand in narrow columns, one after another: a flat to let, a used bicycle, a lost
cat. Between them sit boxed advertisements in bold type, a small photograph of a car, the
logo of a shop, a thin rule that separates one section from the next. The eyes do not read
this page line by line either. They first see the columns, the boxes and the pictures.
Then they group: these four lines are one notice, this frame is an advertisement and not a
notice, this picture belongs to the car below it, this heading opens the section of flats.
Only then comes meaning: the price, the phone number, the size of the flat. A machine that
reads the page from left to right, top to bottom, would mix the end of one notice with the
start of another and put the slogan of an advertisement in the middle of a phone number.

Reading is not one act. It is at least three, and each one stands on the one below it. If
we want a machine to keep the result of reading, it is natural to keep all three, and to
keep them apart. Imagine three sheets of glass laid over the page. On the lowest one, you
trace what is really there. On the second one, you draw frames around what belongs
together. On the top one, you write what it means. Each sheet can be lifted and looked at
alone, and each mark on a higher sheet stands exactly over the marks it was made from.

This picture is VIM.

## Five Principles

Before the first line of code, the authors of VIM wrote down five rules that a good
result of reading must follow. They are worth reading slowly, because every later decision
about the format goes back to one of them.

**Separate content, structure and meaning.** The text `123 456` is content. The fact that
it sits in a cell, in a column, in a table, is structure. The fact that it is a net amount
in Czech crowns without VAT is meaning. Three different things, stored in three different
places.

**Be layered.** One layer is never enough. The notes put it as a short chain: raw, layout,
structure, semantics. A model that keeps only the last step throws away the evidence; a
model that keeps only the first step leaves all the work to the reader.

**Preserve provenance.** Every value must be able to say where it came from: which file,
which page, which region of the page. Without that, a value is a rumour.

**Be bidirectional.** From the model, a program must be able to compute: sum the totals of
a year of invoices, for example. And from the model, the document must be at least roughly
reconstructable. A later chapter shows a page of an invoice redrawn from nothing but its
`vim.json`.

**Handle uncertainty.** No machine reads a document with full certainty. So every result
carries a confidence, the method that produced it and, where it makes sense, the
alternatives that were considered and rejected.

## The Three Questions

The first three layers of VIM answer three questions, and their names are short enough to
remember:

| Layer | Question | What it holds |
| ----- | -------- | ------------- |
| `physical` | What is there? | Text with exact positions and fonts, barcodes, tables, graphics, the full text |
| `structural` | What does it look like? | Blocks with a label and a body, their type and importance, barcodes with the blocks near them |
| `semantic` | What does it mean? | Entities such as a customer or a supplier, payments read from QR codes, word stems for search |

The order is the depth of interpretation. The physical layer interprets almost nothing: it
says that the string *Customer* stands at a certain position in bold. The structural layer
interprets a little: it says that this bold string is the label of a block with three lines
under it. The semantic layer interprets the most: it says that the block describes the
customer, and it says so with a confidence of 0.8, not 1.0.

Each higher layer refers to the lower one by a stable identifier instead of copying it. A
structural block `block_4` has the same ID as the physical block it was made from. A
semantic entity says `source_block: block_4`. That is the mark on the upper sheet of glass
standing exactly over the mark below.

## What Happened to the Fourth Layer

The original design notes had a fourth layer: *Provenance and trust*, with the question
*why do we believe it?* It was meant to hold the source, the page, the rectangle, the
confidence and the method of every value.

When the format was built, this layer did not become a separate sheet. It became something
better: a property of every element on every sheet. A text element in the physical layer
carries its `bbox`, its `page` and its `confidence`. A structural barcode carries the IDs of
the blocks near it and a confidence. A semantic entity carries the ID of its source block
and a confidence. Provenance is not a place you go to; it travels with each value. A
consumer never has to join two layers to learn where a number came from.

## Layers That Came Later

The three interpretation levels are a closed set. A fourth level of interpretation would
change the core idea of VIM, and the architecture decisions of the ecosystem say so
explicitly. But a document can be seen not only deeper, but also from another side. Over
time, VIM got three more layers of a different kind, the **domain layers**:

| Layer | Since | Question | Producer |
| ----- | ----- | -------- | -------- |
| `toc` | 1.4 | How do I find things in it? | vim-parser |
| `extraction` | 2.0 | What does this form hold? | form recognition (reco) and others |
| `prescriptive` | 2.1 | What does this document require? | an LLM-based producer, checked by a person |

A domain layer does not interpret the page more deeply. It holds a view built for one
purpose: navigation in a long book, the values of a filled form, the rules written in a
specification. Every domain layer refers to the interpretation levels by ID, so the
anchors stay in one place.

![The six layers of VIM: three interpretation levels below, three domain layers above, which refer down by ID](../images/layer-stack.en.svg)

All layers are optional. A document from an image scanner may have only the `extraction`
layer. A long PDF book may have `physical`, `structural` and `toc`. A reader ignores the
layers it does not know. This is what lets very different tools share one container
without stepping on each other.

## Layers Older Than Computers

It would be easy to think that layers over a document are an invention of the computer age.
They are not. Open almost any book and look at its first pages. Before the first chapter,
the author or the publisher tells you what the book is about, how it is divided and where
to find each part. That short text is not the content of the book. It is a description
that stands above the content and points into it: a layer.

The idea is very old. Around the year 77, the Roman writer Pliny the Elder dedicated his
*Natural History* to the future emperor Titus. The work had thirty-seven books, and Pliny
gave the whole first book to a summary of the others: a list of the contents of each book,
so that a busy reader could find a topic without reading everything. Next to the contents,
he listed the authorities he had drawn from, more than four hundred names in the lists that
survive. A table of contents and a list of sources, in one book written almost two thousand
years ago. In the words of this book: a `toc` layer, and provenance.

Later centuries added more layers of the same kind. In England, alphabetical indexes at the
back of books appeared in the late sixteenth century; an index printed in 1595 in Thomas
North's translation of Plutarch's *Lives* is one of the early examples. In 1658 a book of
the acts of Parliament closed with *An Alphabetical Table of the most material contents of
the whole book*. An index is a small semantic layer: it says which pages are about which
idea, and it points to the place with a page number, the way a VIM entity points to its
block.

The habit is alive today. A Czech book about people in the age of artificial intelligence,
published in 2025, opens with an introduction in which the author walks the reader through
all seven chapters, one after another, before the first of them begins.

VIM did not invent these layers. It gives them a form that a machine can write, read and
check, and it adds what a printed index never had: the confidence of every entry and the
method that produced it.

## A Model, Not a File Format

There is one more idea in the design notes that is easy to miss. The *perfect format*, the
notes say, is not one thing but a **pipeline plus a data model**. VIM is the model. It
fixes what the result of understanding a document looks like. It does not fix how the
understanding happens. One tool may read text from the PDF directly; another may run OCR on
a scan; a third may ask a language model. They all write the same layers, and every value
says which method produced it.

This is also why VIM could grow so fast. Its first version was made for invoices in PDF.
Within months it held the results of form recognition from scanned images and the
requirements of technical specifications. The model did not have to change its idea; it
only had to gain new sheets of glass. How that happened, and in what order, is the story of
the next chapter.

## Sources

- Design notes *Universal "Perfect" Format for Storing Information from Any File*,
  `plugins/vim-parser/README.md` in `portunix-plugins`
- VIM format specification v1.5 and v2.0, `docs/architecture/` in `portunix-architecture`
- ADR-009 *Criteria for Adding a New VIM Layer* (9 October 2026), `portunix-architecture`
- [Natural History (Pliny)](https://en.wikipedia.org/wiki/Natural_History_(Pliny)): Book I
  as preface, list of contents and lists of authorities; the first books written in AD 77
- Lukáš Sedláček, *Nazí v AI době* (2025, ISBN 978-80-53061-04-9), introduction, pages 13
  to 16
- [Index (publishing)](https://en.wikipedia.org/wiki/Index_(publishing)): early indexes in
  English books, North's Plutarch (1595) and Scobell's *Acts and Ordinances of Parliament*
  (1658)
