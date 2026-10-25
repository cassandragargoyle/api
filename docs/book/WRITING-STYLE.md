---
title: Book Writing Style
description: Use when you write or rewrite a chapter of a book in docs/book/. Sets the voice (a story, not a manual), the rules for facts and sources, the structure of a chapter, and the two languages.
category: style-guide
ai_load: scoped
status: active
language: en
created: 2026-10-25
last_updated: 2026-10-25
related:
  - README.md
---

# Book Writing Style

## Overview

This guide sets the voice of the chapters of every book in `docs/book/`. A book is not a
manual. A manual answers "which field do I fill"; the book tells the reader **why a model
or a tool exists, where its ideas come from and how its parts fit together**. It should read
like a good book on history or popular science: a story that pulls the reader in and still
stays exact in every fact.

The rules here apply to the book chapters only. Module READMEs, contract documentation,
issues and ADRs keep their plain reference style. A README or a `/code-textbook` document
that is pulled into a book as a chapter keeps its own style.

The guide is taken over from the book of portunix-reco-studio; the examples below come from
that book.

## The Voice

- **Tell, then show.** A chapter or a section opens with a scene, a question or a short
  story, and only then comes to files, commands and tables
- **Give the reader a reason to read on.** Name the problem before the solution, the
  tension before the answer
- **Look back and around.** Connect a feature to the history of its field, to a habit of
  the people who do the work, or to what the AI era changes and what it forgets
- **Tell stories from history.** Stories and parallels from history are welcome and make a
  chapter worth reading: show that an idea is older than its code (the table of contents
  and the list of sources in the first book of Pliny's *Natural History* are a layer
  over a text, almost two thousand years before VIM). Every such story must be true and
  must be listed in *Sources*, see [Facts and Sources](#facts-and-sources)
- **Reflective sentences are welcome.** For example: *"When we look closer at form
  recognition, we find a varied and very interesting world, one that is often overlooked
  in the age of AI, when we chase new goals and forget the processes and skills we had
  learned long before, and without AI."*
- **We and you.** "We" for the shared journey through ideas and history; "you" for the
  moments when the reader works with the tool
- **Vary the rhythm.** Mix long, flowing sentences with short ones. A short sentence stops
  the reader at the point that matters

## What to Avoid

| Avoid | Prefer |
| ----- | ------ |
| A definition as the first sentence ("X is an application for Y") | A scene or a problem that X answers; the definition follows |
| A bullet list as the main carrier of a section | Prose; a list or table only for real reference data (file types, test suites) |
| Marketing words ("revolutionary", "powerful", "seamless") | Concrete facts that make the point by themselves |
| Invented anecdotes about real people or companies | Real, verified events, or a clearly hypothetical scene ("imagine a processing centre ...") |
| Idioms and slang that a non-native reader does not know | Rich but plain language; images that work in any culture |
| Long technical detail in the story | A short mention in the story; the detail in a later section or chapter |
| Identifiers in the running text ("ADR-009 decided", "issue #51", "I-PDF-002", "criterion C3") | What was decided and when ("the decision of October 2026 about new layers"); the identifier goes to *Sources* |

### Example

Manual style:

> **Reco Studio** is a desktop application for preparing and checking form recognition.

Book style:

> Every form that passes through recognition carries a piece of invisible work behind it.
> Someone had to decide where each field lies on the page, what belongs in it and how to
> tell that the machine read it right. And someone had to teach, try and measure the
> engine that reads. Reco Studio is the place where this work happens.

## Facts and Sources

A story is only as good as its facts. History and numbers in the book must be true.

- **Verify every date, number and name** before it goes into a chapter, and prefer primary
  or well-known sources (standards, patents, the institutions themselves)
- **Mark uncertainty in the text.** Use "around", "roughly", "in the late 1980s" when the
  sources differ or are not exact; do not invent precision
- **Name the source.** A chapter with history ends with a section *Sources* (*Prameny*)
  that lists the links; the reader can check every claim. This applies to every story and
  parallel from history, not only to numbers and dates
- **Experience of the team is welcome**, but it is written as experience ("in the
  processing centres of that time, people remember ..."), not as a measured fact
- **Technical facts** (file names, commands, behaviour) come from the code and stay in
  `code` formatting, exact as in any reference document
- **Run what you show.** Every command, program and output in a chapter is run before it
  goes in, and the output is copied, not retyped; the input files go to `<book>/samples/`
- **Show the failures too.** When a tool gets something wrong on the sample, the chapter
  says so; a book that only shows successes is a brochure
- **Public content only.** `docs/` is public: no real personal data, no customer documents,
  no names of partners that the team does not name in public; make synthetic samples

## Written to Be Heard

A chapter must read well aloud, because the book may also become an audiobook, and because
a text that sounds right is also easier to read.

- **No identifiers in the running text, where it is possible.** Numbers of architecture
  decisions (ADR-004), issues and pull requests (#51), catalog entries (I-PDF-002),
  challenges (C001) and short codes of criteria (G1, C3) do not go into sentences. Name the
  thing by what it is and when it happened: "the architecture decision of March 2026 that
  made VIM a universal container", "the multi-column problem of the catalog", "the
  criterion of revisions". The identifiers go to *Sources*, where a reader who wants to
  check them finds them
- **Tables name, they do not code.** A table of gates or criteria gets a short name per
  row ("Provenance", "Revisions"), not an ID column, so that the text can refer to the
  row by its name
- **Code names stay, but stay few.** A field name such as `vim_version` or a layer key such
  as `prescriptive` is part of the subject and stays in `code`; do not pile several of them
  into one sentence
- **A sentence carries one parenthesis at most**, and never one that holds only a code
- **Figures are explained in the text.** The text says what a figure shows, so a listener
  who cannot see it does not lose the thread

## Structure of a Chapter

1. **Opening**: a scene, a question or a look back; it says why the chapter matters
2. **The story of the part**: what problem it solves and how the solution grew
3. **The concrete**: files, flows, diagrams and reference tables
4. **Closing**: one or two sentences that connect the chapter to the next one or to the
   whole book
5. **Sources** (only when the chapter uses outside facts)

Headings stay short and may be evocative ("A camera over the belt"), but the contents of
the book must still tell the reader where to find a topic.

## Two Languages

The English chapter is the source; the Czech chapter is not a word-by-word translation.
Each version must read as if it was written in its own language: Czech sentences have their
own rhythm, word order and images. The facts, the structure and the headings stay the same
in both versions, so the chapters match in the contents and in the cross references.

Examples belong to the language of the edition. The English edition has English labels,
names and addresses (*Customer*, *Jane Sample*, *Prague*), the Czech edition Czech ones
(*Odběratel*, *Jana Příkladová*, *Praha*). When a sample document is shown, each edition
gets its own sample in its language, run through the same tool, and the chapter says that
the other edition uses the twin. When the source of a story was in another language (a
Czech bill in an English chapter), the chapter says so and gives the labels translated.

---

**Created**: 2026-10-19 (portunix-reco-studio), taken over 2026-10-25
**Version**: 1.2
