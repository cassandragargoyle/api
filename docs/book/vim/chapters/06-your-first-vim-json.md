---
title: Your First vim.json
description: Practical chapter of the VIM book. Read when you want to produce a vim.json with vim-parser, look at it, read it from Python or jq, and validate it against the contract of this repository.
category: reference
ai_load: on-demand
status: active
language: en
created: 2026-10-25
last_updated: 2026-10-25
related:
  - ../samples/make-sample-invoice.py
  - ../samples/faktura-2026-0042.pdf.vim.json
  - 05-a-promise-in-json-schema.md
---

# Your First vim.json

## From Reading to Doing

Five chapters told the story of VIM and looked inside it. This one is for your hands. By
its end, you will have made a `vim.json` from a PDF, looked at it in a browser, asked it
two questions from a short Python program, and checked it against the contract. Every
command and every output in this chapter was run on the sample invoice of the book, which
is stored next to the chapters in `samples/`.

You need Java 21 for the parser, Python 3.11 or newer for the examples, and optionally
`jq`, the small command line tool for JSON.

## Step 1: Ask for the Plan

The parser is distributed as a directory with one JAR file and a `plugins/` directory of
ZIP files. Inside Portunix, the same parser runs as `portunix vim-parser`; outside, you call
the JAR directly. Before you run anything, ask the parser what it would do:

```bash
java -jar vim-parser-0.1.8-SNAPSHOT.jar auto --dry-run faktura-2026-0042.pdf
```

The answer is the execution plan you saw in the chapter about the machine that reads:
seven plugins in three phases, and the lemmatizer left out because it is not enabled by
default. The plan is a cheap way to see which plugins the parser found. If a plugin is
missing from the list, its ZIP file is missing from `plugins/`.

## Step 2: Make the File

```bash
java -jar vim-parser-0.1.8-SNAPSHOT.jar auto -o faktura-2026-0042.pdf.vim.json faktura-2026-0042.pdf
```

With `-o` the result goes to the given file; without it, to the standard output. The option
`-w` writes it next to the PDF as a *sidecar* file with the suffix `.vim.json`. The most
useful other options of the `auto` command are:

| Option | Default | Effect |
| ------ | ------- | ------ |
| `--metadata` / `--no-metadata` | on | Document metadata with Schema.org names |
| `--fulltext` | off | The full text of the document in the physical layer |
| `--hash` | off | The SHA-256 hash of the file in the metadata |
| `--stemming` | off | Word stems in the semantic layer, for search |
| `--backend <id>`, `--lang <code>` | lucene, auto | Backend and language of stemming |
| `--exclude <plugins>` | none | Plugins to leave out, separated by commas |
| `--compact` | off | JSON without pretty printing |

## Step 3: Look at It

A `vim.json` of one page has hundreds of lines. Before you read them, look at the picture:

```bash
java -jar vim-parser-0.1.8-SNAPSHOT.jar visualize faktura-2026-0042.pdf.vim.json
```

The visualizer prints a single HTML page with a tab for each layer; with `-o` or `-w` it
writes the page to a file. Open it in a browser, switch to the structural tab and find the
block with the header *Odběratel*. Then switch to the physical tab and find the same block
by its ID. You are now doing by hand what
every consumer of VIM does in code: following an ID from a higher layer down to the
evidence.

## Step 4: Ask It Questions with jq

For a quick look from the command line, `jq` is enough. The headers of all structural
blocks:

```bash
jq -r '.layers.structural.blocks[] | "\(.id)\t\(.header)"' faktura-2026-0042.pdf.vim.json
```

```text
block_1	Faktura 2026-0042
block_2	Položka
block_3	null
block_4	Odběratel
block_5	MnožstvíCenaCelkem
```

The output shows the strengths and the weaknesses of the third chapter at a glance: the
customer block has its header, the total has none, and the table header melted into one
word.

## Step 5: Ask It Questions from Code

A `vim.json` is plain JSON, so no special library is needed to read it. This short program
answers the question of the prologue, *who is the customer*, and adds the evidence: the
page and the position of the block the answer came from. Then it reads the payment:

```python
import json
from pathlib import Path

doc = json.loads(Path("faktura-2026-0042.pdf.vim.json").read_text(encoding="utf-8"))
layers = doc["layers"]

# Who is the customer, and where on the page does it come from?
for entity in layers.get("semantic", {}).get("entities", []):
    if entity["type"] == "customer":
        block = next(b for b in layers["physical"]["blocks"] if b["id"] == entity["source_block"])
        print(entity["fields"]["name"], "| page", block["page"], "| bbox", block["bbox"]["x"], block["bbox"]["y"],
              "| confidence", entity["confidence"])

# What is there to pay?
for payment in layers.get("semantic", {}).get("payments", []):
    f = payment["fields"]
    print(f["amount"], f["currency"], "to", f["account"], "VS", f["variable_symbol"])
```

```text
Jana Příkladová | page 1 | bbox 299.13782 155.6819 | confidence 0.8
1250.0 CZK to CZ6508000000192000145399 VS 20260042
```

Notice the pattern, because it is the pattern of every good VIM consumer. It reads the
meaning from the semantic layer, follows `source_block` down to the physical layer for the
evidence, and keeps the confidence next to the answer. A program that drops the
confidence on the way turns an educated guess into a false fact.

All layers are optional, so the program uses `.get()` with an empty default for every
layer. A `vim.json` from form recognition may have no semantic layer at all.

## Step 6: Check It against the Contract

The contract of the fifth chapter can check your own files too, not only the examples of
this repository. Any validator of JSON Schema Draft 2020-12 will do, as long as it loads
all `vim-*.schema.json` files, because the root schema refers to the others by their
`$id`. In Python, with the libraries `jsonschema` and `referencing`:

```python
import json
import sys
from pathlib import Path

from jsonschema import Draft202012Validator
from referencing import Registry, Resource

schemas = Path(sys.argv[1])
registry = Registry().with_resources(
    (s["$id"], Resource.from_contents(s))
    for s in (json.loads(p.read_text(encoding="utf-8")) for p in schemas.glob("vim-*.schema.json"))
)
root = json.loads((schemas / "vim-document.schema.json").read_text(encoding="utf-8"))
validator = Draft202012Validator(root, registry=registry)
doc = json.loads(Path(sys.argv[2]).read_text(encoding="utf-8"))
errors = sorted(validator.iter_errors(doc), key=lambda e: list(e.path))
for e in errors:
    print("/" + "/".join(map(str, e.path)), e.message)
print("valid" if not errors else f"{len(errors)} error(s)")
```

For the sample invoice it prints `valid`. For one of the negative examples of the
contract, an extraction layer with a version that is too old, it prints:

```text
/vim_version '1.5' is not one of ['2.0', '2.1']
1 error(s)
```

If your document has a prescriptive layer, run the identifier check after the schema check;
`checkVimIdentifiers()` in `scripts/vim-identifier-check.mjs` finds duplicate IDs and links
to provisions, references and terms that do not exist.

## Step 7: Two Tools, One File

The last step shows VIM as a shared container. The invoice pipeline of `portunix-ireco`
works in two commands on one file:

```bash
ptx-ireco parse invoice.pdf -o invoice.vim.json     # PDF → VIM (physical + structural)
ptx-ireco extract invoice.vim.json                  # fills the semantic layer in place
```

The first command writes the physical and structural layers with the Python port of the
parser. The second one reads them and adds the semantic layer, without touching what is
already there. Your own tool can join such a chain in the same way: read the layers you
need, write the layer you own, and leave the rest alone.

## Try It Yourself

1. Run the parser on the sample invoice with `--exclude barcode`. What happens, and what
   must you add to the option to get a `vim.json` without barcodes?
2. Change the Python program so that it prints the supplier. What does it print, and what
   does that tell you about the semantic layer of this sample?
3. Write a `vim.json` by hand with only the `extraction` layer and `vim_version` `"1.5"`.
   What does the validator say? What must you change?
4. Find a sentence in this chapter that explains why a consumer should keep the confidence.
   Then find a value in the sample whose confidence is too high for what it says.

### Answers

1. The parser stops before it reads anything: *Plugin 'spayd' depends on missing plugin
   'barcode'*. The orchestrator does not quietly drop the plugins that depend on an
   excluded one; it asks you to decide. The graphics extractor depends on the barcodes
   too, so the working option is `--exclude barcode,spayd,graphics`. Then the physical
   layer has empty `barcodes` and `graphics`, and the semantic layer has empty `payments`.
   Check such a change with `--dry-run` first.
2. There is no entity of type `supplier`. The supplier's address landed in `block_1`
   together with the title, and the entity made from that block has the type `invoice` and
   the name *Dodavatel*. The semantic layer is a heuristic, and on this layout it fails.
3. `/vim_version '1.5' is not one of ['2.0', '2.1']`. The extraction layer exists since
   version 2.0, so the document must say `"2.0"` or `"2.1"`.
4. The entity of type `invoice` from `block_1` has the confidence 0.8, the same as the
   correct customer, although its `city` is a date. The confidence of a heuristic says how
   sure the heuristic is, not how right it is.

The last chapter looks ahead: what VIM still cannot do well, and where it is going.
