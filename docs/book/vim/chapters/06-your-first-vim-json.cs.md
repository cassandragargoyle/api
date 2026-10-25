---
title: Váš první vim.json
description: Praktická kapitola knihy o VIM. Čti, když chceš vyrobit vim.json pomocí vim-parseru, prohlédnout si ho, číst ho z Pythonu nebo jq a zkontrolovat ho proti kontraktu tohoto repozitáře.
category: reference
ai_load: on-demand
status: active
language: cs
translation_of: 06-your-first-vim-json.md
created: 2026-10-25
last_updated: 2026-10-25
---

# Váš první vim.json

> Český překlad [06-your-first-vim-json.md](06-your-first-vim-json.md). Anglická verze je autoritativní.

## Od čtení k práci

Pět kapitol vyprávělo příběh VIM a nahlíželo do jeho nitra. Tato je pro vaše ruce. Na jejím
konci budete mít z PDF vyrobený `vim.json`, prohlédnete si ho v prohlížeči, položíte mu dvě
otázky krátkým programem v Pythonu a zkontrolujete ho proti kontraktu. Každý příkaz a každý
výstup v této kapitole proběhl nad vzorovou fakturou knihy, která leží vedle kapitol v
adresáři `samples/`.

Potřebujete Javu 21 pro parser, Python 3.11 nebo novější pro příklady a volitelně `jq`,
malý nástroj příkazové řádky pro JSON.

## Krok 1: Zeptejte se na plán

Parser se distribuuje jako adresář s jedním souborem JAR a adresářem `plugins/` se soubory
ZIP. Uvnitř Portunixu běží tentýž parser jako `portunix vim-parser`; mimo něj voláte JAR
přímo. Než cokoli spustíte, zeptejte se parseru, co by udělal:

```bash
java -jar vim-parser-0.1.8-SNAPSHOT.jar auto --dry-run faktura-2026-0042.pdf
```

Odpovědí je plán spuštění, který jste viděli v kapitole o stroji, který čte: sedm pluginů ve
třech fázích a lemmatizátor vynechaný, protože ve výchozím stavu není zapnutý. Plán je
levný způsob, jak zjistit, které pluginy parser našel. Chybí-li v seznamu nějaký plugin,
chybí jeho soubor ZIP v `plugins/`.

## Krok 2: Vyrobte soubor

```bash
java -jar vim-parser-0.1.8-SNAPSHOT.jar auto -o faktura-2026-0042.pdf.vim.json faktura-2026-0042.pdf
```

S volbou `-o` jde výsledek do zadaného souboru; bez ní na standardní výstup. Volba `-w` ho
zapíše vedle PDF jako *sidecar* soubor s příponou `.vim.json`. Nejužitečnější další volby
příkazu `auto` jsou:

| Volba | Výchozí | Účinek |
| ----- | ------- | ------ |
| `--metadata` / `--no-metadata` | zapnuto | Metadata dokumentu se jmény ze Schema.org |
| `--fulltext` | vypnuto | Plný text dokumentu ve fyzické vrstvě |
| `--hash` | vypnuto | Otisk SHA-256 souboru v metadatech |
| `--stemming` | vypnuto | Kmeny slov v sémantické vrstvě, pro vyhledávání |
| `--backend <id>`, `--lang <code>` | lucene, auto | Backend a jazyk stemmingu |
| `--exclude <plugins>` | nic | Pluginy, které se vynechají, oddělené čárkou |
| `--compact` | vypnuto | JSON bez odsazení |

## Krok 3: Prohlédněte si ho

`vim.json` jedné stránky má stovky řádků. Než je začnete číst, podívejte se na obrázek:

```bash
java -jar vim-parser-0.1.8-SNAPSHOT.jar visualize faktura-2026-0042.pdf.vim.json
```

Vizualizér vypíše jedinou HTML stránku se záložkou pro každou vrstvu; s volbou `-o` nebo
`-w` ji zapíše do souboru. Otevřete ji v prohlížeči, přepněte na strukturální záložku a
najděte blok se záhlavím *Odběratel*. Pak přepněte na fyzickou záložku a najděte tentýž blok
podle jeho ID. Právě děláte ručně to, co každý spotřebitel VIM dělá v kódu: sledujete ID z
vyšší vrstvy dolů k důkazu.

## Krok 4: Ptejte se ho přes jq

Pro rychlý pohled z příkazové řádky stačí `jq`. Záhlaví všech strukturálních bloků:

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

Výstup ukazuje na první pohled silné i slabé stránky ze třetí kapitoly: blok odběratele má
své záhlaví, celková částka žádné a záhlaví tabulky se slilo do jednoho slova.

## Krok 5: Ptejte se ho z kódu

`vim.json` je obyčejný JSON, takže ke čtení není potřeba žádná zvláštní knihovna. Tento
krátký program odpovídá na otázku z prologu, *kdo je zákazník*, a přidává důkaz: stránku a
polohu bloku, ze kterého odpověď pochází. Pak přečte platbu:

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

Všimněte si vzoru, protože je to vzor každého dobrého spotřebitele VIM. Význam čte ze
sémantické vrstvy, po `source_block` sestupuje do fyzické vrstvy pro důkaz a spolehlivost
drží vedle odpovědi. Program, který spolehlivost cestou zahodí, udělá z kvalifikovaného
odhadu falešný fakt.

Všechny vrstvy jsou volitelné, a tak program používá u každé vrstvy `.get()` s prázdnou
výchozí hodnotou. `vim.json` z rozpoznávání formulářů nemusí mít sémantickou vrstvu vůbec.

## Krok 6: Zkontrolujte ho proti kontraktu

Kontrakt z páté kapitoly umí kontrolovat i vaše vlastní soubory, nejen příklady tohoto
repozitáře. Poslouží jakýkoli validátor JSON Schema Draft 2020-12, pokud načte všechny
soubory `vim-*.schema.json`, protože kořenové schéma odkazuje na ostatní přes jejich `$id`.
V Pythonu s knihovnami `jsonschema` a `referencing`:

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

Pro vzorovou fakturu vypíše `valid`. Pro jeden z negativních příkladů kontraktu, extrakční
vrstvu s příliš starou verzí, vypíše:

```text
/vim_version '1.5' is not one of ['2.0', '2.1']
1 error(s)
```

Má-li váš dokument preskriptivní vrstvu, spusťte po kontrole schématem ještě kontrolu
identifikátorů; `checkVimIdentifiers()` ve `scripts/vim-identifier-check.mjs` najde
duplicitní ID a odkazy na ustanovení, odkazy a pojmy, které neexistují.

## Krok 7: Dva nástroje, jeden soubor

Poslední krok ukazuje VIM jako sdílený kontejner. Pipeline faktur `portunix-ireco` pracuje
dvěma příkazy nad jedním souborem:

```bash
ptx-ireco parse invoice.pdf -o invoice.vim.json     # PDF → VIM (physical + structural)
ptx-ireco extract invoice.vim.json                  # fills the semantic layer in place
```

První příkaz zapíše fyzickou a strukturální vrstvu pythonovým portem parseru. Druhý je
přečte a přidá sémantickou vrstvu, aniž by sáhl na to, co už tam je. Váš vlastní nástroj se
může do takového řetězu přidat stejně: přečíst vrstvy, které potřebuje, zapsat vrstvu, kterou
vlastní, a zbytek nechat být.

## Vyzkoušejte si

1. Spusťte parser nad vzorovou fakturou s volbou `--exclude barcode`. Co se stane a co musíte
   k volbě přidat, abyste dostali `vim.json` bez čárových kódů?
2. Upravte program v Pythonu tak, aby vypsal dodavatele. Co vypíše a co vám to říká o
   sémantické vrstvě tohoto vzorku?
3. Napište ručně `vim.json` jen s vrstvou `extraction` a s `vim_version` `"1.5"`. Co řekne
   validátor? Co musíte změnit?
4. Najděte v této kapitole větu, která vysvětluje, proč má spotřebitel spolehlivost
   uchovat. Pak najděte ve vzorku hodnotu, jejíž spolehlivost je na to, co říká, příliš
   vysoká.

### Odpovědi

1. Parser skončí dřív, než cokoli přečte: *Plugin 'spayd' depends on missing plugin
   'barcode'*. Orchestrátor pluginy závislé na vynechaném pluginu potichu nevyřadí; nechá
   rozhodnutí na vás. Na čárových kódech závisí i extraktor grafiky, takže fungující volba
   je `--exclude barcode,spayd,graphics`. Fyzická vrstva pak má prázdné `barcodes` a
   `graphics` a sémantická vrstva prázdné `payments`. Takovou změnu si nejdřív ověřte přes
   `--dry-run`.
2. Entita typu `supplier` neexistuje. Adresa dodavatele skončila v `block_1` spolu s
   nadpisem a entita vytvořená z tohoto bloku má typ `invoice` a jméno *Dodavatel*.
   Sémantická vrstva je heuristika a na tomto rozvržení selhává.
3. `/vim_version '1.5' is not one of ['2.0', '2.1']`. Extrakční vrstva existuje od verze
   2.0, takže dokument musí říkat `"2.0"` nebo `"2.1"`.
4. Entita typu `invoice` z `block_1` má spolehlivost 0,8, stejnou jako správný zákazník,
   přestože její `city` je datum. Spolehlivost heuristiky říká, jak moc si je heuristika
   jistá, ne jak moc má pravdu.

Poslední kapitola se dívá dopředu: co VIM ještě neumí dobře a kam směřuje.
