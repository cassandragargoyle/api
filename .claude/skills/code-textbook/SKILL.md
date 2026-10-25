---
name: code-textbook
description: 'Write Markdown documentation for source code as a textbook chapter for secondary school students who learn to program: plain language, step-by-step code walk-through, embedded base64 SVG figures, verified examples, exercises with answers. Use whenever you create or substantially rewrite a .md file that documents code (a class, a package, an algorithm, a module), unless the user asks for a short reference. Examples: "write documentation for Levenshtein.java", "vytvoř md dokumentaci ke kódu", "popiš tuto třídu v md", "/code-textbook java/.../Foo.java".'
argument-hint: [path to the source file or package]
allowed-tools: Read, Glob, Grep, Bash, Write, Edit, AskUserQuestion
user-invocable: true
---

# Code documentation as a textbook chapter

Document **$ARGUMENTS** as a textbook chapter. The reader is a secondary school student who
knows basic programming (variables, `if`, loops, arrays) and wants to understand real code. A
professional developer must still find the document correct and useful.

The reference example is
[`java/src/main/java/org/cassandragargoyle/api/fuzzy/README.md`](../../../java/src/main/java/org/cassandragargoyle/api/fuzzy/README.md).
Read it before you start, and follow its structure and tone.

If no file is given, ask which source file or package to document.

## Principles

1. **Explain the problem before the code.** Start with a question from everyday life that the
   code answers. Show where the idea is used in the real world.
2. **Build the idea step by step.** Show why the simple solution is not good enough, then the smart
   idea, then the code. Never show a line of code that the text has not prepared.
3. **Walk through the real code.** Quote the real code of the file in small blocks (`### Step N`)
   and explain each block. Map every rule of the explanation to the exact expression in the code,
   for example in a table "In the picture | In the code".
4. **Teach the general lesson.** Name the programming technique or pattern (dynamic programming,
   utility class, swap with a temporary variable, integer division) so the student can use it
   elsewhere.
5. **Point out traps.** Show the details where beginners make mistakes (index `i - 1`, `(double)`
   cast, Unicode surrogate pairs, division by zero) in a `> **Watch out:**` block.
6. **Pictures where a picture is clearer than text.** Every chapter has 3 to 6 figures.
7. **Let the student act.** Add "Try it yourself" tasks in the text, a runnable example program,
   and exercises from easy to challenge with answers.
8. **Everything is true.** Every number, output, and claim is checked against the real code (see
   Verification).

## Language and style

- English, as for all project documentation (see `AGENTS.md`). Use short sentences and simple
  words; explain every technical term the first time it appears
- Address the reader as "you"
- Follow the Markdown lint rules of the project (`.markdownlint-cli2.jsonc`); no inline HTML
  (`<details>` fails MD033), so the answers go to a separate last section
- A Czech version is a translation, made later with `/translate`

## File and frontmatter

- Put the file next to the code as `README.md` of the package, also when it explains one class
- Frontmatter per `docs/contributing/MARKDOWN-FRONTMATTER.md`:

  ```yaml
  ---
  title: "<Topic>: A Textbook Chapter for <File>"
  description: Use when you learn, teach, or explain <topic> in <package>, or need a beginner-friendly walk-through of <File>. Written for secondary school students who learn to program.
  category: reference
  ai_load: on-demand
  status: active
  language: en
  created: <today>
  last_updated: <today>
  related:
    - <File>
    - <the same feature in the other language modules>
  tags:
    - textbook
  ---
  ```

- After the H1, add the AI assistant load-scope blockquote. It says that the code and its doc
  comments are authoritative and this chapter explains them

## Structure

Adapt the sections to the code, but keep this order:

1. **H1 and introduction**: what the file is, what the reader needs to know, a numbered list
   "What you will learn"
2. **The problem**: an everyday question, where it is used in practice
3. **The basic idea**: the core concept with the simplest possible example and a figure
4. **Why the naive way is not enough** (when it applies)
5. **The smart idea**: the algorithm or design, with figures and a "Try it yourself" task
6. **From the idea to the code**: a table of the public methods, then `### Step N` blocks with the
   real code and an explanation of each block
7. **Special topics**: edge cases, traps, language details (one section each)
8. **How fast is it?**: time and memory in plain words and in O-notation, with concrete numbers
9. **The same code in other languages**: links to the Python, Go, and TypeScript twins, if they exist
10. **Use it**: a complete runnable program with its real output
11. **Exercises**: 5 to 7 tasks, from easy questions to a challenge that extends the code
12. **Words to remember**: a glossary table
13. **Answers**: answers to the exercises that have one answer

## Figures

Figures are SVG files, embedded as base64 data URIs, so the document has no extra files:
`![alt text that describes the content](data:image/svg+xml;base64,...)`.

1. Write a figure script in the scratchpad, not in the repository. Import the helpers from
   [`svgkit.py`](svgkit.py) (add the skill directory to `sys.path`)
2. **Compute every number in a figure with the same algorithm as the code**, never type it by hand.
   For example, fill a table with a Python copy of the loop and assert the result that the unit
   tests expect
3. Good figure types: a step-by-step transformation, one step of the algorithm with its inputs, a
   filled table or grid with the path to the answer, memory layout (arrays, references), a
   comparison of values (bars), a data flow
4. Use the `svgkit` colors with meaning: blue for input and structure, orange for change or problem,
   green for result or success. Keep a white background, a title in the figure, and text of at
   least 12 px
5. Render every figure with `svgkit.render_png()` and **look at the PNG** with the Read tool. Fix
   overlapping text, clipped titles, and arrows that cross boxes, then render again
6. Write the alt text as a full description of what the figure shows
7. Keep the whole document under 200 KB

GitHub does not show `data:` images in Markdown files, VS Code, IntelliJ, and most Markdown viewers
do. Tell the user about this when the document is mainly read on GitHub.

## Verification

- Compile and run the example program against the real classes (for Java:
  `java/target/classes`, after `mvn -o compile` if needed) and paste the real output. Mention
  locale differences (decimal comma) when the output has numbers
- Check every exercise answer and every number in the text by calling the real code
- Check that every relative link points to an existing file
- Run `npx --prefix typescript markdownlint-cli2 <file>` (files outside `docs/` are not part of
  `make lint-md`)

## After the document

- Add the document to the list in the **Learning to Program** section of `README.md` and in the
  **Chcete se naučit programovat?** section of `README.cs.md` (one line with a link and a short
  description)
- Add a link from the feature description in `README.md`, if the feature has one
- Do not change the code itself. When you find a bug or a confusing behavior, describe it to the
  user and suggest an issue
- A document can also become a chapter of a PDF book: add its path to a `docs/book/<book>/book.json`
  (see [`docs/book/README.md`](../../../docs/book/README.md)); a story-like chapter written for a
  book follows the `book-chapter` skill instead

## Report

Tell the user:

- the path of the new file and its sections
- the list of figures, with what each one shows
- what you verified and how (example output, answers, links, lint)
- bugs or unclear behavior that you found in the code
