---
name: book-chapter
description: 'Write or rewrite a chapter of a PDF book in docs/book/ (the VIM book or a new book) in the voice of a popular science book: a story with a scene, verified history with sources, real examples run on real files, figures, English and Czech versions; then build the PDF. Also starts a new book. Use when the user asks for a book, a chapter, or an engaging narrative description of a model, a tool, or a part of the code. Examples: "napiš kapitolu knihy o VIM", "add a chapter about the contract to the VIM book", "založ knihu pro ...", "/book-chapter vim contract history".'
argument-hint: [book name and topic of the chapter]
allowed-tools: Read, Glob, Grep, Bash, Write, Edit, WebFetch, WebSearch, AskUserQuestion
user-invocable: true
---

# Chapter of a book in docs/book/

Write **$ARGUMENTS** as a chapter of a book in `docs/book/`. Taken over from the books of
portunix-reco and portunix-reco-studio, together with the generator and the template.

Read first:

- [`docs/book/README.md`](../../../docs/book/README.md): layout, `book.json`, build
- [`docs/book/WRITING-STYLE.md`](../../../docs/book/WRITING-STYLE.md): the voice, facts and sources
- the `book.json` of the book and one existing chapter, for example
  [`docs/book/vim/chapters/03-inside-a-vim-json.md`](../../../docs/book/vim/chapters/03-inside-a-vim-json.md)

If the book or the topic is not clear, ask. For a new book, follow *Adding a Book* in the
README and ask for the title, the subtitle and the parts.

## Steps

1. **Collect the facts.** Read the code, the ADRs, the specifications and the Git history
   (`git log --reverse --format="%ad %s" --date=short -- <path>`) of every project the
   chapter talks about. Note dates, numbers and names with their source. Verify outside
   history (standards, years, institutions) on the web and keep the links for *Sources*
2. **Find the scene and the story.** Open with a real event from the history (a first
   commit, an experiment, a decision) or a clearly hypothetical scene. Name the problem
   before the solution. Look for a parallel from older history that shows the idea is not
   new (a table of contents, an index, a catalogue card), verify it and list it in
   *Sources*
3. **Run what you show.** Make a synthetic sample (no real personal or customer data) in
   `<book>/samples/` with the script that makes it, one per language (English names and
   *Prague* for the English edition, Czech ones for the Czech edition), run the real tool
   on each, and copy the real output into the chapter of that language. Show what the tool
   gets wrong too
4. **Draw the figures.** Compute figures from real data where possible. Add them to the
   figure script of the book (`scripts/generate-book-<book>-figures.py`), write both
   `name.en.svg` and `name.cs.svg`, render a PNG preview with headless Chrome and look at
   it before you use it. Use a ` ```mermaid ` block for flows and decision diagrams
5. **Write English, then Czech.** `chapters/NN-name.md` is the source; `NN-name.cs.md` is
   written as Czech, not translated word by word, with the same facts, headings and
   figures. Both get the frontmatter of
   [MARKDOWN-FRONTMATTER.md](../../../docs/contributing/MARKDOWN-FRONTMATTER.md)
   (`category: reference`, `ai_load: on-demand`; the Czech one `language: cs` and
   `translation_of`) and the Czech one the blockquote *Český překlad ...* under the H1
6. **Register and build.** Add the chapter to `book.json`, run `make book`, render a few
   pages with `pdftoppm -r 45 -png` and look at them: cover, figures, tables, code
7. **Check.** `make lint-md`; every relative link points to an existing file; every
   number in the text matches the sample or the source

## Rules

- `docs/` is public: no real personal data, no customer documents, no internal names that
  the team does not use in public, no e-mail addresses from commits
- Facts that the sources do not agree on get "around", "in the late ..."; nothing is
  invented, no invented anecdotes about real people
- Technical names (files, commands, fields) stay in `code` formatting and exact
- Written to be heard: no ADR numbers, issue numbers, catalog IDs or criterion codes in the
  running text; name the thing and the date, and put the identifier into *Sources*. Before
  you finish, run
  `grep -nE "ADR-[0-9]+|#[0-9]{2,}|I-PDF-[0-9]+|C00[0-9]|\b[CG][1-4]\b" <chapters>` and
  check that every hit is in *Sources*
- A chapter ends with one or two sentences that lead to the next chapter, then *Sources*
  (*Prameny*) when it uses outside facts
- Do not commit; the user decides. The PDFs are build output; ask whether to commit them

## Report

Tell the user, in their language:

- the new or changed chapters and figures
- what you verified and how (runs of the tool, sources, build, lint)
- facts you could not verify and left out or marked
- bugs or surprises in the tool that the chapter shows
