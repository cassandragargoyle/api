# Contract - Unified AI Plugin & Task Platform

Jazykově nezávislé kontrakty pro Unified AI Plugin Platform a Unified AI Task Platform.

> **Pozn.:** Tento dokument je překladem [README.md](README.md). Při rozporu má přednost anglická verze (oficiální jazyk projektu).

## Účel

Tento adresář obsahuje definice JSON Schema, které popisují komunikační kontrakty mezi pluginy a platformou. Všechny pluginy (Java, Python, Go) musí těmto schématům odpovídat.

![Adresář contract: schémata, příklady ověřované schématy a gRPC protos, rozdělené do devíti oblastí kontraktů](data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPSc3NjAnIGhlaWdodD0nNDAwJyB2aWV3Qm94PScwIDAgNzYwIDQwMCc+PHRpdGxlPkNvbnRlbnQgb2YgdGhlIGNvbnRyYWN0IGRpcmVjdG9yeTwvdGl0bGU+PHJlY3Qgd2lkdGg9Jzc2MCcgaGVpZ2h0PSc0MDAnIHJ4PScxMCcgZmlsbD0nI2ZmZmZmZicvPjxkZWZzPjxtYXJrZXIgaWQ9J2EnIHZpZXdCb3g9JzAgMCAxMCAxMCcgcmVmWD0nOScgcmVmWT0nNScgbWFya2VyV2lkdGg9JzcnIG1hcmtlckhlaWdodD0nNycgb3JpZW50PSdhdXRvJz48cGF0aCBkPSdNMCwwIEwxMCw1IEwwLDEwIHonIGZpbGw9JyMxZjI5MzMnLz48L21hcmtlcj48bWFya2VyIGlkPSdiJyB2aWV3Qm94PScwIDAgMTAgMTAnIHJlZlg9JzknIHJlZlk9JzUnIG1hcmtlcldpZHRoPSc3JyBtYXJrZXJIZWlnaHQ9JzcnIG9yaWVudD0nYXV0byc+PHBhdGggZD0nTTAsMCBMMTAsNSBMMCwxMCB6JyBmaWxsPScjMjU2M2ViJy8+PC9tYXJrZXI+PG1hcmtlciBpZD0nbycgdmlld0JveD0nMCAwIDEwIDEwJyByZWZYPSc5JyByZWZZPSc1JyBtYXJrZXJXaWR0aD0nNycgbWFya2VySGVpZ2h0PSc3JyBvcmllbnQ9J2F1dG8nPjxwYXRoIGQ9J00wLDAgTDEwLDUgTDAsMTAgeicgZmlsbD0nI2MyNDEwYycvPjwvbWFya2VyPjxtYXJrZXIgaWQ9J2cnIHZpZXdCb3g9JzAgMCAxMCAxMCcgcmVmWD0nOScgcmVmWT0nNScgbWFya2VyV2lkdGg9JzcnIG1hcmtlckhlaWdodD0nNycgb3JpZW50PSdhdXRvJz48cGF0aCBkPSdNMCwwIEwxMCw1IEwwLDEwIHonIGZpbGw9JyMxNTgwM2QnLz48L21hcmtlcj48L2RlZnM+PHRleHQgeD0nMzgwLjAnIHk9JzM0JyBmb250LXNpemU9JzIwJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5XaGF0IGlzIGluIGNvbnRyYWN0LzwvdGV4dD48cmVjdCB4PSc0MCcgeT0nNTYnIHdpZHRoPScxODAnIGhlaWdodD0nOTInIHJ4PSc4JyBmaWxsPScjZGJlYWZlJyBzdHJva2U9JyMyNTYzZWInIHN0cm9rZS13aWR0aD0nMicvPjx0ZXh0IHg9JzEzMCcgeT0nODQnIGZvbnQtc2l6ZT0nMTcnIGZpbGw9JyMyNTYzZWInIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nQ29uc29sYXMsIE1lbmxvLCBtb25vc3BhY2UnPnNjaGVtYXMvPC90ZXh0Pjx0ZXh0IHg9JzEzMCcgeT0nMTA4JyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPkpTT04gU2NoZW1hIDIwMjAtMTI8L3RleHQ+PHRleHQgeD0nMTMwJyB5PScxMzAnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+dGhlIGRhdGEgZm9ybWF0czwvdGV4dD48cmVjdCB4PScyOTAnIHk9JzU2JyB3aWR0aD0nMTgwJyBoZWlnaHQ9JzkyJyByeD0nOCcgZmlsbD0nI2RjZmNlNycgc3Ryb2tlPScjMTU4MDNkJyBzdHJva2Utd2lkdGg9JzInLz48dGV4dCB4PSczODAnIHk9Jzg0JyBmb250LXNpemU9JzE3JyBmaWxsPScjMTU4MDNkJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5leGFtcGxlcy88L3RleHQ+PHRleHQgeD0nMzgwJyB5PScxMDgnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+dmFsaWQgZG9jdW1lbnRzPC90ZXh0Pjx0ZXh0IHg9JzM4MCcgeT0nMTMwJyBmb250LXNpemU9JzEyJyBmaWxsPScjNmI3Nzg1JyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPisgaW52YWxpZC8gbmVnYXRpdmUgdGVzdHM8L3RleHQ+PHJlY3QgeD0nNTQwJyB5PSc1Nicgd2lkdGg9JzE4MCcgaGVpZ2h0PSc5Micgcng9JzgnIGZpbGw9JyNmZmVkZDUnIHN0cm9rZT0nI2MyNDEwYycgc3Ryb2tlLXdpZHRoPScyJy8+PHRleHQgeD0nNjMwJyB5PSc4NCcgZm9udC1zaXplPScxNycgZmlsbD0nI2MyNDEwYycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+cHJvdG8vPC90ZXh0Pjx0ZXh0IHg9JzYzMCcgeT0nMTA4JyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmdSUEMgc2VydmljZXM8L3RleHQ+PHRleHQgeD0nNjMwJyB5PScxMzAnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+dGhlIHJlbW90ZSBjYWxsczwvdGV4dD48bGluZSB4MT0nMjkwJyB5MT0nMTAyJyB4Mj0nMjI2JyB5Mj0nMTAyJyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMicgbWFya2VyLWVuZD0ndXJsKCNnKScvPjx0ZXh0IHg9JzI1NScgeT0nMTY2JyBmb250LXNpemU9JzExJyBmaWxsPScjMTU4MDNkJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPnZhbGlkYXRlZCBieSB0aGUgc2NoZW1hczwvdGV4dD48dGV4dCB4PSczODAuMCcgeT0nMTg2JyBmb250LXNpemU9JzE1JyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5Db250cmFjdCBhcmVhczwvdGV4dD48cmVjdCB4PSc0Mi4wJyB5PScyMDAnIHdpZHRoPScxNjAnIGhlaWdodD0nMzQnIHJ4PScxNycgZmlsbD0nI2YxZjRmNycgc3Ryb2tlPScjYzlkMWRhJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PScxMjIuMCcgeT0nMjIyJyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPlRhc2sgUGxhdGZvcm08L3RleHQ+PHJlY3QgeD0nMjE0LjAnIHk9JzIwMCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSczNCcgcng9JzE3JyBmaWxsPScjZjFmNGY3JyBzdHJva2U9JyNjOWQxZGEnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzI5NC4wJyB5PScyMjInIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+VklNPC90ZXh0PjxyZWN0IHg9JzM4Ni4wJyB5PScyMDAnIHdpZHRoPScxNjAnIGhlaWdodD0nMzQnIHJ4PScxNycgZmlsbD0nI2YxZjRmNycgc3Ryb2tlPScjYzlkMWRhJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PSc0NjYuMCcgeT0nMjIyJyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPlBsdWdpbiBQbGF0Zm9ybTwvdGV4dD48cmVjdCB4PSc1NTguMCcgeT0nMjAwJyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzM0JyByeD0nMTcnIGZpbGw9JyNmMWY0ZjcnIHN0cm9rZT0nI2M5ZDFkYScgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNjM4LjAnIHk9JzIyMicgZm9udC1zaXplPScxMycgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5WaXN1YWxpemF0aW9uPC90ZXh0PjxyZWN0IHg9JzQyLjAnIHk9JzI0NCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSczNCcgcng9JzE3JyBmaWxsPScjZjFmNGY3JyBzdHJva2U9JyNjOWQxZGEnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzEyMi4wJyB5PScyNjYnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+VHJhbnNjcmlwdGlvbjwvdGV4dD48cmVjdCB4PScyMTQuMCcgeT0nMjQ0JyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzM0JyByeD0nMTcnIGZpbGw9JyNmMWY0ZjcnIHN0cm9rZT0nI2M5ZDFkYScgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nMjk0LjAnIHk9JzI2NicgZm9udC1zaXplPScxMycgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5Nb2RlbGluZzwvdGV4dD48cmVjdCB4PSczODYuMCcgeT0nMjQ0JyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzM0JyByeD0nMTcnIGZpbGw9JyNmMWY0ZjcnIHN0cm9rZT0nI2M5ZDFkYScgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNDY2LjAnIHk9JzI2NicgZm9udC1zaXplPScxMycgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5PcHBvcnR1bml0eSBNZ210PC90ZXh0PjxyZWN0IHg9JzU1OC4wJyB5PScyNDQnIHdpZHRoPScxNjAnIGhlaWdodD0nMzQnIHJ4PScxNycgZmlsbD0nI2YxZjRmNycgc3Ryb2tlPScjYzlkMWRhJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PSc2MzguMCcgeT0nMjY2JyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPkNvbXBldGVuY3kgTW9kZWw8L3RleHQ+PHJlY3QgeD0nMzAwLjAnIHk9JzI4OCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSczNCcgcng9JzE3JyBmaWxsPScjZjFmNGY3JyBzdHJva2U9JyNjOWQxZGEnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzM4MC4wJyB5PSczMTAnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+UGVvcGxlIFJlZ2lzdHJ5PC90ZXh0PjxsaW5lIHgxPSczODAuMCcgeTE9JzMzNCcgeDI9JzM4MC4wJyB5Mj0nMzUyJyBzdHJva2U9JyMxZjI5MzMnIHN0cm9rZS13aWR0aD0nMicgbWFya2VyLWVuZD0ndXJsKCNhKScvPjxyZWN0IHg9JzExMCcgeT0nMzU2JyB3aWR0aD0nNTQwJyBoZWlnaHQ9JzM0JyByeD0nNicgZmlsbD0nI2ZmZmZmZicgc3Ryb2tlPScjMWYyOTMzJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PSczODAuMCcgeT0nMzc4JyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPnVzZWQgYnkgSmF2YSDCtyBQeXRob24gwrcgR28gwrcgVHlwZVNjcmlwdCBtb2R1bGVzIGFuZCBQb3J0dW5peCB0b29sczwvdGV4dD48L3N2Zz4=)

## Schémata

### Task Platform

| Schéma | Popis |
| ------ | ----- |
| [task-request.schema.json](schemas/task-request.schema.json) | Jednotný formát požadavku na úlohu |
| [task-response.schema.json](schemas/task-response.schema.json) | Jednotný formát odpovědi na úlohu |
| [error.schema.json](schemas/error.schema.json) | Strukturovaný model chyby |
| [task-manifest.schema.json](schemas/task-manifest.schema.json) | Manifest úloh, které plugin umí |
| [ocr-result.schema.json](schemas/ocr-result.schema.json) | Standardizovaný výsledek OCR s pozicemi textu |

### VIM (vrstvený informační model)

| Schéma | Popis |
| ------ | ----- |
| [vim-document.schema.json](schemas/vim-document.schema.json) | Struktura kořenového dokumentu VIM (v2.0, v2.1); objekt `layers` je uzavřený; `prescriptive` vyžaduje `vim_version` `"2.1"`, `extraction` vyžaduje `"2.0"` nebo `"2.1"` |
| [vim-extraction.schema.json](schemas/vim-extraction.schema.json) | Vrstva extrakce (odkaz na šablonu, zdroje, sloučený dokument) |
| [vim-extraction-source.schema.json](schemas/vim-extraction-source.schema.json) | Výsledek extrakce jednoho zdroje s původem (provenance) |
| [vim-extraction-field.schema.json](schemas/vim-extraction-field.schema.json) | Definice extrahovaného pole, tabulky a buňky; `Revision` (1.1.0: nepovinné `property`) a `BoundingBox` sdílí s preskriptivní vrstvou |
| [vim-prescriptive.schema.json](schemas/vim-prescriptive.schema.json) | Preskriptivní vrstva (VIM 2.1): `definition`, `provenance`, `provisions`, `references`, `terms`; definuje `Definition`, `Reference`, `Target`, `Term` |
| [vim-prescriptive-provision.schema.json](schemas/vim-prescriptive-provision.schema.json) | Jedno ustanovení (`requirement`, `recommendation`, `permission`) s kotvami, vazbami, ověřením, stavem a revizemi; definuje `Anchor`, `Relation`, `Verification` |

Preskriptivní vrstva ukládá, co **definiční soubor** předepisuje (specifikace, směrnice,
smlouva). Je nepovinná: dokument VIM 2.0 zůstává platný. `Provenance`, `Revision` a
`BoundingBox` se znovu nedefinují; nová schémata odkazují na definice vrstvy extrakce.
Dokumenty VIM uložené jako příklady mají příponu souboru **`*.vim.json`**.

Vrstva váže dokument na verze VIM, které ji definují: dokument s vrstvou `prescriptive` má
`vim_version` `"2.1"`, dokument s vrstvou `extraction` má `"2.0"` nebo `"2.1"`. Dokument bez
těchto vrstev může mít kteroukoli uvedenou verzi.

![Matice vrstev VIM a hodnot vim_version: extraction potřebuje 2.0 nebo 2.1, prescriptive potřebuje 2.1, ostatní vrstvy jsou povolené s každou verzí](data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPSc3NjAnIGhlaWdodD0nNDA2JyB2aWV3Qm94PScwIDAgNzYwIDQwNic+PHRpdGxlPlZJTSBsYXllcnMgYW5kIHRoZSBhbGxvd2VkIHZpbV92ZXJzaW9uIHZhbHVlczwvdGl0bGU+PHJlY3Qgd2lkdGg9Jzc2MCcgaGVpZ2h0PSc0MDYnIHJ4PScxMCcgZmlsbD0nI2ZmZmZmZicvPjx0ZXh0IHg9JzM4MC4wJyB5PSczNCcgZm9udC1zaXplPScxOScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+V2hpY2ggVklNIGxheWVyIGlzIGFsbG93ZWQgd2l0aCB3aGljaCB2aW1fdmVyc2lvbjwvdGV4dD48dGV4dCB4PSczODAuMCcgeT0nNTgnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+dmltLWRvY3VtZW50LnNjaGVtYS5qc29uIDEuMi4wPC90ZXh0Pjx0ZXh0IHg9JzM0NS4wJyB5PSc4NCcgZm9udC1zaXplPScxNScgZmlsbD0nIzI1NjNlYicgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+MS4wIOKAkyAxLjU8L3RleHQ+PHRleHQgeD0nNDk1LjAnIHk9Jzg0JyBmb250LXNpemU9JzE1JyBmaWxsPScjMjU2M2ViJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz4yLjA8L3RleHQ+PHRleHQgeD0nNjQ1LjAnIHk9Jzg0JyBmb250LXNpemU9JzE1JyBmaWxsPScjMjU2M2ViJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz4yLjE8L3RleHQ+PHRleHQgeD0nNDAnIHk9JzEyMScgZm9udC1zaXplPScxNScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5waHlzaWNhbDwvdGV4dD48dGV4dCB4PScxODAnIHk9JzEyMScgZm9udC1zaXplPScxMicgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPkxheWVyIDE8L3RleHQ+PHJlY3QgeD0nMjc0JyB5PScxMDAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzM0NS4wJyB5PScxMjEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNDI0JyB5PScxMDAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzQ5NS4wJyB5PScxMjEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNTc0JyB5PScxMDAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzY0NS4wJyB5PScxMjEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHRleHQgeD0nNDAnIHk9JzE2MScgZm9udC1zaXplPScxNScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5zdHJ1Y3R1cmFsPC90ZXh0Pjx0ZXh0IHg9JzE4MCcgeT0nMTYxJyBmb250LXNpemU9JzEyJyBmaWxsPScjNmI3Nzg1JyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+TGF5ZXIgMjwvdGV4dD48cmVjdCB4PScyNzQnIHk9JzE0MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nMzQ1LjAnIHk9JzE2MScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48cmVjdCB4PSc0MjQnIHk9JzE0MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNDk1LjAnIHk9JzE2MScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48cmVjdCB4PSc1NzQnIHk9JzE0MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNjQ1LjAnIHk9JzE2MScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48dGV4dCB4PSc0MCcgeT0nMjAxJyBmb250LXNpemU9JzE1JyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nQ29uc29sYXMsIE1lbmxvLCBtb25vc3BhY2UnPnNlbWFudGljPC90ZXh0Pjx0ZXh0IHg9JzE4MCcgeT0nMjAxJyBmb250LXNpemU9JzEyJyBmaWxsPScjNmI3Nzg1JyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+TGF5ZXIgMzwvdGV4dD48cmVjdCB4PScyNzQnIHk9JzE4MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nMzQ1LjAnIHk9JzIwMScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48cmVjdCB4PSc0MjQnIHk9JzE4MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNDk1LjAnIHk9JzIwMScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48cmVjdCB4PSc1NzQnIHk9JzE4MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNjQ1LjAnIHk9JzIwMScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48dGV4dCB4PSc0MCcgeT0nMjQxJyBmb250LXNpemU9JzE1JyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nQ29uc29sYXMsIE1lbmxvLCBtb25vc3BhY2UnPnRvYzwvdGV4dD48dGV4dCB4PScxODAnIHk9JzI0MScgZm9udC1zaXplPScxMicgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPkxheWVyIDQ8L3RleHQ+PHJlY3QgeD0nMjc0JyB5PScyMjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzM0NS4wJyB5PScyNDEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNDI0JyB5PScyMjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzQ5NS4wJyB5PScyNDEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNTc0JyB5PScyMjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzY0NS4wJyB5PScyNDEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHRleHQgeD0nNDAnIHk9JzI4MScgZm9udC1zaXplPScxNScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5leHRyYWN0aW9uPC90ZXh0Pjx0ZXh0IHg9JzE4MCcgeT0nMjgxJyBmb250LXNpemU9JzEyJyBmaWxsPScjNmI3Nzg1JyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+TGF5ZXIgNTwvdGV4dD48cmVjdCB4PScyNzQnIHk9JzI2MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNmZmVkZDUnIHN0cm9rZT0nI2MyNDEwYycgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nMzQ1LjAnIHk9JzI4MScgZm9udC1zaXplPScxMycgZmlsbD0nI2MyNDEwYycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+cmVqZWN0ZWQ8L3RleHQ+PHJlY3QgeD0nNDI0JyB5PScyNjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzQ5NS4wJyB5PScyODEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNTc0JyB5PScyNjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzY0NS4wJyB5PScyODEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHRleHQgeD0nNDAnIHk9JzMyMScgZm9udC1zaXplPScxNScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5wcmVzY3JpcHRpdmU8L3RleHQ+PHRleHQgeD0nMTgwJyB5PSczMjEnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdzdGFydCcgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5WSU0gMi4xPC90ZXh0PjxyZWN0IHg9JzI3NCcgeT0nMzAwJyB3aWR0aD0nMTQyJyBoZWlnaHQ9JzMyJyByeD0nNicgZmlsbD0nI2ZmZWRkNScgc3Ryb2tlPScjYzI0MTBjJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PSczNDUuMCcgeT0nMzIxJyBmb250LXNpemU9JzEzJyBmaWxsPScjYzI0MTBjJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5yZWplY3RlZDwvdGV4dD48cmVjdCB4PSc0MjQnIHk9JzMwMCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNmZmVkZDUnIHN0cm9rZT0nI2MyNDEwYycgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNDk1LjAnIHk9JzMyMScgZm9udC1zaXplPScxMycgZmlsbD0nI2MyNDEwYycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+cmVqZWN0ZWQ8L3RleHQ+PHJlY3QgeD0nNTc0JyB5PSczMDAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzY0NS4wJyB5PSczMjEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHRleHQgeD0nMzgwLjAnIHk9JzM3OCcgZm9udC1zaXplPScxMycgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5BIGRvY3VtZW50IHdpdGhvdXQgdGhlIGV4dHJhY3Rpb24gYW5kIHByZXNjcmlwdGl2ZSBsYXllcnMgY2FuIHVzZSBldmVyeSB2ZXJzaW9uPC90ZXh0Pjwvc3ZnPg==)

#### Co kontroluje schéma a co kontrola identifikátorů

JSON Schema neumí porovnat hodnotu s hodnotami jiného pole, proto se pravidla identifikátorů
preskriptivní vrstvy kontrolují ve druhém kroku,
[`scripts/vim-identifier-check.mjs`](../scripts/vim-identifier-check.mjs):

| Pravidlo | Kontroluje |
| -------- | ---------- |
| Struktura, povinné vlastnosti, povolené hodnoty, `vim_version` vrstvy | `vim-document.schema.json` a schémata, na která odkazuje |
| `provision_id`, `reference_id` a `term_id` jsou ve vrstvě jedinečné | kontrola identifikátorů |
| Každá hodnota `provisions[].references` je `reference_id` vrstvy | kontrola identifikátorů |
| Každá hodnota `provisions[].terms` je `term_id` vrstvy | kontrola identifikátorů |
| Každé `provisions[].relations[].provision_id` je `provision_id` vrstvy | kontrola identifikátorů |
| Každá hodnota `terms[].references` je `reference_id` vrstvy | kontrola identifikátorů |

`make validate-contract` spouští oba kroky na každém dokumentu `*.vim.json`. Kontrola je
prostý ES modul bez závislostí; jiný projekt může importovat `checkVimIdentifiers()` a spustit
ji po vlastní kontrole schématem. Nekontroluje se: `reference` s rozsahem `internal`, která
ukazuje na neexistující článek, a identifikátory vrstvy `extraction`.

### Plugin Platform

| Schéma | Popis |
| ------ | ----- |
| [plugin-manifest.schema.json](schemas/plugin-manifest.schema.json) | Registrační manifest pluginu (identita, běhové prostředí, příkazy, integrace AI) |
| [plugin-permissions.schema.json](schemas/plugin-permissions.schema.json) | Model oprávnění pluginu (souborový systém, síť, databáze, systém) |
| [plugin-health.schema.json](schemas/plugin-health.schema.json) | Odpověď kontroly stavu pluginu (stav, metriky, dílčí kontroly) |
| [help-ai.schema.json](schemas/help-ai.schema.json) | Strojově čitelný výstup `--help-ai` (identita nástroje, příkazy, parametry, nepovinné `supported_extensions` / `supported_mime_types` pro každý příkaz) |

### Vizualizace

| Schéma | Popis |
| ------ | ----- |
| [graph-view.schema.json](schemas/graph-view.schema.json) | Dokument 3D silově orientovaného grafu uzlů pro prohlížeč graphlens (`meta` + `nodes` + `links`) |

Dokumenty graph-view mají příponu souboru **`*.glens.json`** (vázanou na graphlens / `ptx-graphlens`), takže nástroje, editory a prohlížeče poznají typ bez čtení obsahu.

> **Liší se od 2D formátu `*.graph.json`.** 2D formát Graph Canvas (`GraphFile`: `nodes` + `edges` + `metadata`, Cytoscape, portunix-vscode #030) je jiný kontrakt. Tento 3D formát graph-view používá `links` (ne `edges`) a `meta` (ne `metadata`) a nesmí použít příponu `*.graph.json`.

### Přepis

| Schéma | Popis |
| ------ | ----- |
| [transcript.schema.json](schemas/transcript.schema.json) | Pracovní dokument přepisu videa nebo zvuku pro backend opravy přepisu scribe (`media` + `speakers` + `segments` + `flags`) |

Dokumenty přepisu mají příponu souboru **`*.scribe.json`** (vázanou na scribe / `ptx-scribe`, portunix-plugins #113), takže nástroje, editory a budoucí editor přepisů ve VS Code poznají typ bez čtení obsahu.

> **Prozatímní.** Tvar se může ještě změnit po porovnání se skutečným vzorkem přepisu (portunix-plugins #113, kritérium 15); vývoj sleduje `schema_version`. Markdown přepisu (front matter se `source` + segmenty `[mm:ss]`) je lehčí příbuzná konvence sdílená s `vox` a (zatím) zde není formálním schématem.

### Recognition Engine Lab

| Schéma | Popis |
| ------ | ----- |
| [reco-engine-lab.schema.json](schemas/reco-engine-lab.schema.json) | Laboratorní dokument jednoho rozpoznávacího enginu: engine (jednoduchý nebo složený s `components`, soubory `config` jako šablona), datové sady se správnými hodnotami (ground truth), metodika, příkazy fází `prepare`, `train`, `test`, `evaluate` (runner `portunix-plugin` nebo `exec`), metriky a umístění záznamů běhů |
| [reco-run.schema.json](schemas/reco-run.schema.json) | Záznam jednoho trénovacího nebo testovacího běhu: stav, verze a kontrolní součty enginu, kontrolní součty datových sad, parametry, souhrnné metriky, `baseline_run` a soubory běhu; `$defs/HistoryLine` je jeden řádek historie trénování |
| [reco-run-sample.schema.json](schemas/reco-run-sample.schema.json) | Jeden řádek výsledků po vzorcích: vzorek, správná hodnota, předpověď a její `prediction_format`, metriky a výsledky po polích |

| Přípona | Soubor |
| ------- | ------ |
| `*.recolab.json` | Laboratorní dokument, píše ho člověk |
| `*.run.json` | Záznam běhu, píše ho nástroj, který spouští engine |
| `*.samples.jsonl` | Výsledky běhu po vzorcích, jeden dokument JSON na řádek |
| `*.history.jsonl` | Historie trénování běhu, jeden dokument JSON na řádek |

Reco Studio (Engine Lab, portunix-reco-studio #55) a `ptx-reco` tyto soubory čtou a zapisují.
Zástupné symboly příkazů, kontrolní součet datové sady, hodnoty `prediction_format` a
porovnání dvou běhů popisuje
[schemas/README.md](schemas/README.md#recognition-engine-lab) (issue #56).

### Výsledek rozpoznání formuláře

| Schéma | Popis |
| ------ | ----- |
| [reco-form-result.schema.json](schemas/reco-form-result.schema.json) | Výsledek JSON jednoho běhu rozpoznání formuláře v portunix-reco (úloha `reco.match.template`): zarovnání, podrobnosti markerů, skóre identifikace, výsledky zón čárových kódů, hodnoty rozpoznávacích zón s confidence a bbox, záznam běhu `run_info` (`$defs/RunInfo`) a `xml_path` |

Soubory výsledků mají příponu **`*.reco-result.json`**. Výsledek JSON a DENS XML jsou výstupem
běhů rozpoznání formulářů v reco (portunix-architecture ADR-011, rozhodnutí 6); DENS XML se zde
nepopisuje. Podrobnosti v [schemas/README.md](schemas/README.md#recognition-form-result)
(issue #69).

### Modelování

Model budovy pro Portunix Modeler (`ptx-modeler`, portunix-plugins #116): plochý úložný model
entit (podlaží, graf stěn ze spojů a stěn, dveře/okna, místnosti, elektrická zařízení a
okruhy, importovaný podklad/anotace, rohové razítko) s vrstvami a logem operací. Model je
autoritativní; výkresy SVG/glTF jsou odvozené pohledy. Souřadnice jsou celé milimetry.

| Schéma | Popis |
| ------ | ----- |
| [modeler-model.schema.json](schemas/modeler-model.schema.json) | Dokument modelu (`schema: portunix.modeler/v1`): `layers` + `entities` (`id`, `kind`, `parent`, `layer`, `props`) + `log`. 1.1.0 (portunix-plugins #120): druh `circuit` (`designation`, `type`, `board`, `cable`, `protection`), na který odkazuje `device.circuit`. 1.2.0 (portunix-plugins #118): `device.standoff` (vzdálenost kreslení symbolu umístěného na stěně od líce stěny) |
| [modeler-ops.schema.json](schemas/modeler-ops.schema.json) | Operace (`op apply`): pole JSON použité jako jedna transakce; data operací používají vlastnosti entit ze schématu modelu. 1.1.0 (portunix-plugins #120): `circuit.add`, `circuit.set`. 1.2.0 (portunix-plugins #118): `device.attach` (absolutní zařízení → relativně k hostiteli na nejbližším líci stěny v toleranci) |
| [modeler-bom.schema.json](schemas/modeler-bom.schema.json) | Kusovník / počet zařízení (`ptx-modeler bom -f json`, portunix-plugins #120): zařízení seskupená podle symbolu, místnosti nebo okruhu (`unassigned` na konci), včetně násobnosti, varianty jako samostatné položky, deterministické pořadí |
| [modeler-symbol.schema.json](schemas/modeler-symbol.schema.json) | Prvek symbolu (`format: portunix.modeler.symbol/v1`, portunix-plugins #124): metadata, vložený fragment SVG (bezpečná podmnožina), svorky, textová pole, shoda se standardem (`compliant`/`derived`/`custom`/`vendor`, např. IEC 60617) a původ v `meta` (`author`, `license` jako SPDX id, `licenseUrl`, `source`, `sourceUrl`, `attribution`). 1.2.0 (portunix-plugins #128): pevně určený `origin` (`type` git/file, `url`, `revision`, `path`, `format`, `sha256`) a neprůhledné `extensions` podle jmenného prostoru (např. `qet` pro bezeztrátový převod tam a zpět s QElectroTech) |
| [modeler-symbol-source.schema.json](schemas/modeler-symbol-source.schema.json) | Zdroj symbolů (`format: portunix.modeler.symbol-source/v1`, portunix-plugins #130): jedna externí sbírka symbolů v registru zdrojů ptx-modeler (např. prvky QElectroTech, knihovny KiCad) s repozitářem, ověřenými údaji o licenci (SPDX, použití, omezení jako `no-ml-training`), formáty, obory, standardy, prohledávatelnými kategoriemi, strategií indexu a sledovacím issue. Stav podpory počítá plugin, nikdy se neukládá |
| [modeler-symbol-discovery.schema.json](schemas/modeler-symbol-discovery.schema.json) | Výstup JSON vyhledávání symbolů (`ptx-modeler symbol search / sources / index / source probe\|add\|report -f json`, portunix-plugins #130) a RPC vyhledávání služby `ModelerSymbolService` (bajtově shodné); `kind` vybírá search, probe, issue-draft, sources, source, index, source-added nebo symbol-ready (`symbol search --use N`). Výsledky nesou úrovně dostupnosti a úplné akce dalšího kroku pro AI agenty |

Dokumenty modelu mají příponu souboru **`*.model.json`** a soubory operací příponu
**`*.ops.json`**, takže nástroje, editory a AI agenti poznají typ bez čtení obsahu.
Prvky symbolů mají příponu **`*.msym.json`** (jeden samostatný soubor na symbol v
`symbols/<category>/<name>.msym.json` uvnitř balíčku symbolů), takže je prohlížeč ve VS Code může otevřít přímo.
Kusovníky uložené jako JSON mají příponu **`*.bom.json`**.
Zdroje symbolů mají příponu **`*.msrc.json`** (`<id>.msrc.json`) a výstupy vyhledávání uložené do
souborů příponu **`*.symbol-discovery.json`**.
Jejich objekt `meta` nese původ kresby, takže symboly převedené z jiných sbírek si při úpravě
nebo dalším šíření zachovají uvedení autorství:

| Pole `meta` | Význam |
| ----------- | ------ |
| `author` | Autor nebo autoři kresby, např. `The QElectroTech team` |
| `license` | Identifikátor licence SPDX, např. `MIT`, `CC-BY-3.0` (`LicenseRef-<name>`, když žádný není) |
| `licenseUrl` | Odkaz http(s) na text licence nebo licenční podmínky sbírky |
| `source` | Čitelný původ, např. sbírka a původní cesta k souboru |
| `sourceUrl` | Odkaz http(s) na původní symbol nebo sbírku |
| `attribution` | Upozornění, které se zobrazí při dalším šíření, jak vyžadují licence s uvedením autora (CC BY) |

Všechna pole jsou nepovinná; další klíče jsou povolené jako volné řetězce.

> **Jediný zdroj pravdy.** Plugin obsahuje shodné kopie všech schémat modeleru a
> `help-ai.schema.json` (`ptx-modeler schema [--ops | --symbol | --source | --discovery | --bom]`); test
> pluginu selže, když se od tohoto adresáře odchýlí.

### Opportunity Management (v2)

Model se dvěma páteřemi, který píše `ptx-pft` a čte Pilot (ADR-008, issue #015 —
**nahrazuje** sadu v1 z #014). Discovery: Venture → Initiative → Idea.
Delivery: Team/Project → Epic → Use-Case. Model ukládání, princip propojení a návrhová
rozhodnutí popisuje [schemas/README.md](schemas/README.md).

| Schéma | Popis |
| ------ | ----- |
| [venture.schema.json](schemas/venture.schema.json) | Neutrální kontejner `.venture` (nahrazuje `discovery.schema.json`) |
| [initiative.schema.json](schemas/initiative.schema.json) | Strategický záměr; `ideaRefs[]` (M:N) |
| [opportunity.schema.json](schemas/opportunity.schema.json) | Nápad (aktivum poptávky); hrubé `complexity` nahrazuje story pointy |
| [use-case.schema.json](schemas/use-case.schema.json) | Jednotka dodávky; `storyPoints`, `productRef`, `implementsIdeaRefs[]`, `solution` vázané na produkt, smluvní podmínky (`contractRef`, `deliverables`, `acceptanceCriteria`, `outOfScope`, `assumptions`) |
| [product.schema.json](schemas/product.schema.json) | Produkt registrovaný uvnitř venture; nepovinné `vendorRef` na vlastnickou organizaci |
| [team.schema.json](schemas/team.schema.json) | Registr týmů/projektů; `kind`, nepovinné `productRefs[]` |
| [backlog.schema.json](schemas/backlog.schema.json) | Plnohodnotný backlog; `kind`, nepovinné `teamRef`, členství + vazby |
| [epic.schema.json](schemas/epic.schema.json) | Seskupení dodávky; `backlogRef`, `initiativeRefs[]`, `useCaseRefs[]` |
| [estimation.schema.json](schemas/estimation.schema.json) | Historie planning pokeru připojená k use-case (`subjectRef`) |
| [discussion.schema.json](schemas/discussion.schema.json) | Obecné vlákno připojitelné k jakékoli entitě (`subjectRef` + `subjectKind`) |

Grafová projekce každého backlogu **znovu používá** `graph-view.schema.json`
(`*.glens.json`); hrana `implements` (use-case → idea) nepotřebuje změnu schématu.

### Model kompetencí

Strojově čitelný kontrakt pro model kompetencí AI — jediný zdroj pravdy, ze kterého vznikají
generované pohledy (mapa pokrytí, analýza mezer, bus-factor, obsazení rolí). Dva základní typy
uzlů (**CompetencyArea**, **Competency**) zůstávají stabilní; lidé, zdroje, sektory a důkazy se
připojují vazbami. Dvě nezávislé taxonomie: `area:*` (**CO** organizace umí) a `sector:*`
(**KDE** se to používá). Kompetence popisuje, *jak* se práce dodává; *co* se dodává zákazníkovi,
je v samostatném Capability Registry.

| Schéma | Popis |
| ------ | ----- |
| [competency.schema.json](schemas/competency.schema.json) | Uzel Competency — konkrétní odborná schopnost (`competency:*`) |
| [competency-area.schema.json](schemas/competency-area.schema.json) | Uzel CompetencyArea — klasifikace, která seskupuje kompetence (`area:*`) |
| [competency-person.schema.json](schemas/competency-person.schema.json) | Uzel Person — nositel kompetence (`person:*`), nepovinný `ref` do externího registru |
| [competency-resource.schema.json](schemas/competency-resource.schema.json) | Uzel Resource — technologie/nástroj/framework/metoda/standard za kompetencí (`resource:*`) |
| [competency-sector.schema.json](schemas/competency-sector.schema.json) | Uzel Sector — oblast použití (`sector:*`) |
| [competency-evidence.schema.json](schemas/competency-evidence.schema.json) | Uzel Evidence — projekt/reference/certifikace/repozitář/metodika/benchmark (`evidence:*`) |
| [competency-assessment.schema.json](schemas/competency-assessment.schema.json) | Hodnocení — úroveň (0-5) + využití AI; přímo na `has_competency` nebo jako samostatný uzel (`assessment:*`) |
| [competency-maturity.schema.json](schemas/competency-maturity.schema.json) | Uzel Maturity — **odvozená** zralost organizace, počítaná z důkazů + hodnocení (`maturity:*`) |
| [competency-verification.schema.json](schemas/competency-verification.schema.json) | Sdílený blok ověření (`status`, `source`, `lastChecked`, `evidence`) připojený ke každému uzlu a hraně |
| [competency-relations.schema.json](schemas/competency-relations.schema.json) | Soubor vazeb — typované hrany (`classified_in`, `owned_by`, `has_competency`, `backed_by`, `applied_in`, `evidenced_by`) |

Id uzlů dodržují gramatiku `<prefix>:<slug>`, kde slug používá úrovně oddělené tečkou ze znaků
`[a-z0-9-]` (např. `competency:rag-knowledge-retrieval`). Pole specifická pro hrany omezuje
schéma vazeb: `has_competency` vyžaduje `assessment` a `primary` je platné jen u `classified_in`.

### Registr lidí

Vrstvený model lidí, který drží kmenová data na jednom místě a projekty na ně odkazují, místo
aby je kopírovaly. Kmenová data se nikdy neupravují dvakrát: projektové vrstvy nesou jen `ref` a
pole specifická pro projekt. Celofiremní registr je **samostatný** registr firmy — projektové
vrstvy na něj **neodkazují** přes `ref`; osoba přítomná v obou má stejné id.

| Schéma | Vrstva | Popis |
| ------ | ------ | ----- |
| [people-registry.schema.json](schemas/people-registry.schema.json) | 1 | Sdílený registr dodávkových projektů (`projects/users.json`); kmenový záznam osoby, cíl `ref` pro vrstvy 2/3 |
| [project-participants.schema.json](schemas/project-participants.schema.json) | 2 | Kdo je na konkrétním projektu (`projects/<slug>/users.json`); `ref` + `projectRole`, nepovinné `override` |
| [project-contacts.schema.json](schemas/project-contacts.schema.json) | 3 | „Čekárna“ kontaktů (`projects/<slug>/contacts.json`); `ref` + poznámka, po ujasnění role se povýší do vrstvy 2 |
| [company-people-registry.schema.json](schemas/company-people-registry.schema.json) | — | Celofiremní registr (kořenový `users.json`); nadmnožina kmenových polí plus `relation`; přítomnost = aktivní spolupráce |

## Proto

Kontrakty služeb gRPC. Jsou to kanonické definice — servery a klienti si kopii převezmou do
vlastního stromu, místo aby drátový formát psali ručně, a při změně kontraktu ji znovu
synchronizují.

| Proto | Služba | Popis |
| ----- | ------ | ----- |
| [task-platform.proto](proto/task-platform.proto) | `portunix.platform.v1.TaskPlatformService` | Univerzální vyhledání a spouštění úloh; odpovídá schématům `task-manifest` / `task-request` / `task-response` |
| [scribe.proto](proto/scribe.proto) | `portunix.scribe.ScribeService` | Backend opravy přepisu (segmenty, mluvčí, příznaky, export do Markdownu); odpovídá [transcript.schema.json](schemas/transcript.schema.json) |
| [modeler.proto](proto/modeler.proto) | `portunix.modeler.ModelerSymbolService` | Převody symbolů Portunix Modeleru (portunix-plugins #128): QElectroTech `.elmt` ↔ `*.msym.json` pro jednotlivé prvky (bajty) a sbírky/balíčky (cesty na serveru), s varováními převodu z CLI. KiCad `.kicad_sym` → `*.msym.json` (portunix-plugins #140): `ImportKicadSymbol` (bajty, jeden symbol na jednotku) a `ImportKicadLibrary` (cesty na serveru); symboly se ověřují proti [modeler-symbol.schema.json](schemas/modeler-symbol.schema.json). Vyhledávání zdrojů symbolů (portunix-plugins #130): `SearchSymbols`, `ListSources`, `GetSource`, `ProbeSource`, `DraftSourceIssue` vracejí dokumenty JSON z CLI ([modeler-symbol-discovery.schema.json](schemas/modeler-symbol-discovery.schema.json)); žádné RPC nezakládá issues. `portunix.modeler.ModelerContractService` (portunix-plugins #144): `GetContractInfo`, `ListContractFiles`, `GetContractFile` poskytují verzovaný snímek kontraktu vložený v pluginu (manifest, schémata, tento soubor); `Health` hlásí `contract_version`. Řádek `// contract-version:` v hlavičce souboru je semver kontraktu modeleru |

## Příklady

`make validate-contract` kontroluje každý soubor příkladu stejně:

![Průběh ověření: přípona souboru vybere schéma, proběhne kontrola schématem, pak doplňková kontrola dokumentů VIM; negativní testy musí být odmítnuty](data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPSc3NjAnIGhlaWdodD0nMzAwJyB2aWV3Qm94PScwIDAgNzYwIDMwMCc+PHRpdGxlPlZhbGlkYXRpb24gZmxvdyBvZiB0aGUgY29udHJhY3QgZXhhbXBsZXM8L3RpdGxlPjxyZWN0IHdpZHRoPSc3NjAnIGhlaWdodD0nMzAwJyByeD0nMTAnIGZpbGw9JyNmZmZmZmYnLz48ZGVmcz48bWFya2VyIGlkPSdhJyB2aWV3Qm94PScwIDAgMTAgMTAnIHJlZlg9JzknIHJlZlk9JzUnIG1hcmtlcldpZHRoPSc3JyBtYXJrZXJIZWlnaHQ9JzcnIG9yaWVudD0nYXV0byc+PHBhdGggZD0nTTAsMCBMMTAsNSBMMCwxMCB6JyBmaWxsPScjMWYyOTMzJy8+PC9tYXJrZXI+PG1hcmtlciBpZD0nYicgdmlld0JveD0nMCAwIDEwIDEwJyByZWZYPSc5JyByZWZZPSc1JyBtYXJrZXJXaWR0aD0nNycgbWFya2VySGVpZ2h0PSc3JyBvcmllbnQ9J2F1dG8nPjxwYXRoIGQ9J00wLDAgTDEwLDUgTDAsMTAgeicgZmlsbD0nIzI1NjNlYicvPjwvbWFya2VyPjxtYXJrZXIgaWQ9J28nIHZpZXdCb3g9JzAgMCAxMCAxMCcgcmVmWD0nOScgcmVmWT0nNScgbWFya2VyV2lkdGg9JzcnIG1hcmtlckhlaWdodD0nNycgb3JpZW50PSdhdXRvJz48cGF0aCBkPSdNMCwwIEwxMCw1IEwwLDEwIHonIGZpbGw9JyNjMjQxMGMnLz48L21hcmtlcj48bWFya2VyIGlkPSdnJyB2aWV3Qm94PScwIDAgMTAgMTAnIHJlZlg9JzknIHJlZlk9JzUnIG1hcmtlcldpZHRoPSc3JyBtYXJrZXJIZWlnaHQ9JzcnIG9yaWVudD0nYXV0byc+PHBhdGggZD0nTTAsMCBMMTAsNSBMMCwxMCB6JyBmaWxsPScjMTU4MDNkJy8+PC9tYXJrZXI+PC9kZWZzPjx0ZXh0IHg9JzM4MC4wJyB5PSczNCcgZm9udC1zaXplPScxOCcgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+V2hhdCBtYWtlIHZhbGlkYXRlLWNvbnRyYWN0IGRvZXMgd2l0aCBvbmUgZXhhbXBsZSBmaWxlPC90ZXh0PjxyZWN0IHg9JzIwJyB5PSc3MCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSc3MCcgcng9JzgnIGZpbGw9JyNmMWY0ZjcnIHN0cm9rZT0nI2M5ZDFkYScgc3Ryb2tlLXdpZHRoPScyJy8+PHRleHQgeD0nMTAwJyB5PScxMDAnIGZvbnQtc2l6ZT0nMTUnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmV4YW1wbGUgZmlsZTwvdGV4dD48dGV4dCB4PScxMDAnIHk9JzEyNCcgZm9udC1zaXplPScxMicgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5ob3VzZS5vcHMuanNvbjwvdGV4dD48cmVjdCB4PScyMDAnIHk9JzcwJyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzcwJyByeD0nOCcgZmlsbD0nI2RiZWFmZScgc3Ryb2tlPScjMjU2M2ViJyBzdHJva2Utd2lkdGg9JzInLz48dGV4dCB4PScyODAnIHk9JzEwMCcgZm9udC1zaXplPScxNScgZmlsbD0nIzI1NjNlYicgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+cGljayBzY2hlbWE8L3RleHQ+PHRleHQgeD0nMjgwJyB5PScxMjQnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+YnkgZmlsZSBzdWZmaXg8L3RleHQ+PHJlY3QgeD0nMzgwJyB5PSc3MCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSc3MCcgcng9JzgnIGZpbGw9JyNkYmVhZmUnIHN0cm9rZT0nIzI1NjNlYicgc3Ryb2tlLXdpZHRoPScyJy8+PHRleHQgeD0nNDYwJyB5PScxMDAnIGZvbnQtc2l6ZT0nMTUnIGZpbGw9JyMyNTYzZWInIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPnNjaGVtYSBjaGVjazwvdGV4dD48dGV4dCB4PSc0NjAnIHk9JzEyNCcgZm9udC1zaXplPScxMicgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5KU09OIFNjaGVtYSAoYWp2KTwvdGV4dD48cmVjdCB4PSc1NjAnIHk9JzcwJyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzcwJyByeD0nOCcgZmlsbD0nI2ZmZWRkNScgc3Ryb2tlPScjYzI0MTBjJyBzdHJva2Utd2lkdGg9JzInLz48dGV4dCB4PSc2NDAnIHk9JzEwMCcgZm9udC1zaXplPScxNScgZmlsbD0nI2MyNDEwYycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+ZXh0cmEgY2hlY2s8L3RleHQ+PHRleHQgeD0nNjQwJyB5PScxMjQnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+Ki52aW0uanNvbiBvbmx5PC90ZXh0PjxsaW5lIHgxPScxODAnIHkxPScxMDUnIHgyPScxOTgnIHkyPScxMDUnIHN0cm9rZT0nIzFmMjkzMycgc3Ryb2tlLXdpZHRoPScyJyBtYXJrZXItZW5kPSd1cmwoI2EpJy8+PGxpbmUgeDE9JzM2MCcgeTE9JzEwNScgeDI9JzM3OCcgeTI9JzEwNScgc3Ryb2tlPScjMWYyOTMzJyBzdHJva2Utd2lkdGg9JzInIG1hcmtlci1lbmQ9J3VybCgjYSknLz48bGluZSB4MT0nNTQwJyB5MT0nMTA1JyB4Mj0nNTU4JyB5Mj0nMTA1JyBzdHJva2U9JyMxZjI5MzMnIHN0cm9rZS13aWR0aD0nMicgbWFya2VyLWVuZD0ndXJsKCNhKScvPjx0ZXh0IHg9JzY0MCcgeT0nMTYwJyBmb250LXNpemU9JzExJyBmaWxsPScjYzI0MTBjJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmlkZW50aWZpZXIgY2hlY2sgb2Y8L3RleHQ+PHRleHQgeD0nNjQwJyB5PScxNzUnIGZvbnQtc2l6ZT0nMTEnIGZpbGw9JyNjMjQxMGMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+dGhlIHByZXNjcmlwdGl2ZSBsYXllcjwvdGV4dD48cmVjdCB4PSc2MCcgeT0nMjAwJyB3aWR0aD0nMzAwJyBoZWlnaHQ9Jzc2JyByeD0nOCcgZmlsbD0nI2RjZmNlNycgc3Ryb2tlPScjMTU4MDNkJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PScyMTAnIHk9JzIyOCcgZm9udC1zaXplPScxNCcgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+bm9ybWFsIGV4YW1wbGU8L3RleHQ+PHRleHQgeD0nMjEwJyB5PScyNTInIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+UEFTUyB3aGVuIGJvdGggY2hlY2tzIGFjY2VwdCBpdDwvdGV4dD48cmVjdCB4PSc0MDAnIHk9JzIwMCcgd2lkdGg9JzMwMCcgaGVpZ2h0PSc3Nicgcng9JzgnIGZpbGw9JyNmZmVkZDUnIHN0cm9rZT0nI2MyNDEwYycgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNTUwJyB5PScyMjgnIGZvbnQtc2l6ZT0nMTQnIGZpbGw9JyNjMjQxMGMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmZpbGUgdW5kZXIgZXhhbXBsZXMvaW52YWxpZC88L3RleHQ+PHRleHQgeD0nNTUwJyB5PScyNTInIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+UEFTUyB3aGVuIG9uZSBjaGVjayByZWplY3RzIGl0PC90ZXh0Pjwvc3ZnPg==)

### Příklady Task Platform

| Příklad | Popis |
| ------- | ----- |
| [image-resize-request.json](examples/image-resize-request.json) | Požadavek na úlohu změny velikosti obrázku |
| [response-success.json](examples/response-success.json) | Úspěšná odpověď na úlohu |
| [response-error.json](examples/response-error.json) | Chybová odpověď na úlohu |
| [image-resize.task-manifest.json](examples/image-resize.task-manifest.json) | Manifest úloh pro plugin změny velikosti obrázku (bez `stability`, tedy `stable`) |
| [reco-extract-form.task-manifest.json](examples/reco-extract-form.task-manifest.json) | Manifest úlohy ve stavu preview (`"stability": "preview"`) |

### Příklady VIM

| Příklad | Popis |
| ------- | ----- |
| [definition-file.vim.json](examples/definition-file.vim.json) | Dokument VIM 2.1 s preskriptivní vrstvou: čtyři ustanovení (všechny tři druhy, jedno bez doslovného `text`), externí a interní reference, termín, vazby, pravidla ověření a revize s `property`. Ověřuje `make validate-contract`. |
| [drawing-definition-file.vim.json](examples/drawing-definition-file.vim.json) | Dokument VIM 2.1 dvojjazyčného výkresu: český doslovný `text` s anglickým `statement` (`definition.language`), měsíc vydání (`issued: "2008-11"`), externí reference známá jen podle `title`, termín definovaný v odkazované normě (`terms[].references`) a kotvy s `bbox` a bez `element_id`. |
| [form-extraction.vim.json](examples/form-extraction.vim.json) | Dokument VIM 2.0 jen s vrstvou extrakce, s `Revision` bez `property`. Ověřuje `make validate-contract`. |
| [invalid/](examples/invalid/) | Negativní testy: dokumenty, které **nesmí** projít ověřením. Každý soubor je stejný malý dokument VIM 2.1 s jednou vadou a jmenuje se podle ní (například `provision-unknown-kind`, `anchor-page-zero`, `reference-external-without-identifier-and-title`); jeden soubor na každé pravidlo preskriptivních schémat, pravidel verzí (`prescriptive-wrong-vim-version`, `extraction-wrong-vim-version`, druhý je dokument VIM 1.5 s vrstvou extrakce) a kontroly identifikátorů (`provision-duplicate-id`, `relation-without-target` a další). `make validate-contract` selže, když schéma i kontrola identifikátorů některý z nich přijmou. |

### Příklady Plugin Platform

| Příklad | Popis |
| ------- | ----- |
| [plugin-manifest-reco.json](examples/plugin-manifest-reco.json) | Manifest pluginu reco (Python, helper) |
| [plugin-manifest-text-extractor.json](examples/plugin-manifest-text-extractor.json) | Manifest pluginu text-extractor (Java, MCP) |
| [plugin-health-serving.json](examples/plugin-health-serving.json) | Zdravý plugin s dílčími kontrolami |
| [help-ai-text-extractor.json](examples/help-ai-text-extractor.json) | Výstup `--help-ai` pro text-extractor (s `supported_extensions` / `supported_mime_types`) |

### Příklady vizualizace

| Příklad | Popis |
| ------- | ----- |
| [voices.glens.json](examples/voices.glens.json) | Malý graf hlasů pro 3D prohlížeč graphlens |

### Příklady Recognition Engine Lab

| Příklad | Popis |
| ------- | ----- |
| [reco-engine-lab/stock-count.recolab.json](examples/reco-engine-lab/stock-count.recolab.json) | Laboratorní dokument pipeline formuláře inventury zásob (číslice `trocr` + text `tesseract`, šablona jako `config`) s trénovací, validační a testovací datovou sadou, příkazy pluginu `reco-worker` a příkazem `exec` pro evaluate. Vzorky a model nejsou součástí příkladu. Ověřuje `make validate-contract`. |
| [reco-engine-lab/runs/](examples/reco-engine-lab/runs/) | Trénovací běh s jeho `*.history.jsonl` a dva testovací běhy s jejich `*.samples.jsonl`; druhý testovací běh má první jako `baseline_run` a ukazuje zlepšená, zhoršená a změněná pole. Výsledky po vzorcích uvádějí jen některá pole, aby byly krátké. Ověřuje `make validate-contract`. |
| [invalid/](examples/invalid/) | Negativní testy s prefixy `recolab-`, `run-`, `samples-` a `history-`: jeden malý dokument s jednou vadou, pojmenovaný podle ní (například `recolab-exec-command-without-argv`, `run-succeeded-without-finished`, `samples-line-not-json`). |

### Příklady výsledku rozpoznání formuláře

| Příklad | Popis |
| ------- | ----- |
| [form-physical-inventory.reco-result.json](examples/form-physical-inventory.reco-result.json) | Výsledek skutečného běhu `reco.match.template` (portunix-reco 0.3.11) na formuláři fyzické inventury: 5 z 5 markerů, jedna zóna čárového kódu, zóny `trocr` a `tesseract`, zaškrtávací pole a `run_info` se všemi fázemi. Anonymizováno: hodnoty, cesty a název šablony jsou nahrazené a ponecháno je jen 7 z 52 rozpoznávacích zón. |
| [form-no-run-info.reco-result.json](examples/form-no-run-info.reco-result.json) | Výsledek verze portunix-reco starší než 0.3.11, bez `run_info`, s transformací `homography`. |
| [form-degraded-alignment.reco-result.json](examples/form-degraded-alignment.reco-result.json) | Degradované zarovnání: jeden marker chybí a jeden je odmítnutý, fáze a běh `partial`, varování, chybějící povinný čárový kód, identifikace podle vzoru čárového kódu a rozpoznávací zóny s `bbox` a `suspicious`. |
| [invalid/](examples/invalid/) | Negativní testy s prefixem `form-result-`: jeden malý dokument s jednou vadou, pojmenovaný podle ní (například `form-result-unknown-phase-status`, `form-result-affine-matrix-with-three-rows`). Testy `form-result-markers-detected-mismatch`, `form-result-markers-expected-mismatch` a `form-result-run-status-mismatch` neodmítá schéma, ale doplňková kontrola [`scripts/reco-form-result-check.mjs`](../scripts/reco-form-result-check.mjs). |

### Příklady modelování

| Příklad | Popis |
| ------- | ----- |
| [house.ops.json](examples/house.ops.json) | Operace, které vytvoří ukázkový dům: 4 místnosti, graf stěn se spoji L/T/X, dveře, okna, zásuvkové okruhy, zásuvky/vypínače umístěné relativně k hostiteli a stropní světla. Ověřuje `make validate-contract`. |
| [house.model.json](examples/house.model.json) | Model, který vznikne z `ptx-modeler init` + `op apply` souboru `house.ops.json` (včetně logu operací). Ověřuje `make validate-contract`. |
| [house.bom.json](examples/house.bom.json) | `ptx-modeler bom -f json --by circuit` pro `house.model.json`: zásuvkové okruhy s jejich zařízeními, zbytek `unassigned`. Ověřuje `make validate-contract`. |
| [socket-single.msym.json](examples/socket-single.msym.json) | Prvek symbolu odvozený z IEC 60617-11 (jednoduchá zásuvka) se svorkou a textovým polem popisku. Ověřuje `make validate-contract`. |
| [sensor-temperature.msym.json](examples/sensor-temperature.msym.json) | Prvek symbolu `custom` (bez odkazu na standardní symbol), jako v základním balíčku ptx-modeler. Ověřuje `make validate-contract`. |
| [smart-relay-qet.msym.json](examples/smart-relay-qet.msym.json) | Symbol importovaný z prvku QElectroTech `.elmt` (`ptx-modeler symbol import-elmt`): atributy SVG `data-qet-*`, `extensions.qet` (typ vazby, kind/element informations, data svorek a dynamických textů) a pevně určený `origin`. Ověřuje `make validate-contract`. |
| [qelectrotech-elements.msrc.json](examples/qelectrotech-elements.msrc.json) | Kurátorovaný zdroj symbolů registru ptx-modeler: prvky QElectroTech (CC BY 3.0 s `no-ml-training`, licence ověřena 2026-09-27), cesta IEC 60617 a lokalizované kategorie. Ověřuje `make validate-contract`. |
| [kicad-symbols.msrc.json](examples/kicad-symbols.msrc.json) | Kurátorovaný zdroj symbolů: knihovny schematických symbolů KiCad na GitLabu (CC BY-SA 4.0 s výjimkou pro návrhy), 223 knihoven jako kategorie, sledovací issue pro převodník `kicad_sym`. Ověřuje `make validate-contract`. |
| [search-en-60617.symbol-discovery.json](examples/search-en-60617.symbol-discovery.json) | `ptx-modeler symbol search -f json "EN 60617 07-02-01"`: výsledek `remote-source` s akcemi kontroly licence, stažení, indexu, převodu a použití. Ověřuje `make validate-contract`. |
| [probe-kicad.symbol-discovery.json](examples/probe-kicad.symbol-discovery.json) | `ptx-modeler symbol source probe -f json` registrovaného zdroje KiCad (zodpovězeno offline z registru, akce `track`). Ověřuje `make validate-contract`. |
| [ready-jistic.symbol-discovery.json](examples/ready-jistic.symbol-discovery.json) | `ptx-modeler symbol search --use N -f json --lang cs jistič`: prvek QElectroTech převedený do spravovaného balíčku, s klíčem symbolu a operací `device.place` (`kind: symbol-ready`). Ověřuje `make validate-contract`. |

### Příklady Opportunity Management

| Příklad | Popis |
| ------- | ----- |
| [ai-in-HR.venture/](examples/ai-in-HR.venture/) | Úplné venture v2: 1 iniciativa, 2 nápady, 2 use-case, 1 produkt, 1 tým, dodávkový backlog + epic, k tomu diskuse, odhad a projekce backlogu `*.glens.json`. Ověřuje `make validate-contract`. |

### Příklady modelu kompetencí

| Příklad | Popis |
| ------- | ----- |
| [llm-foundations.competency-area.json](examples/llm-foundations.competency-area.json) | Uzel CompetencyArea |
| [rag-knowledge-retrieval.competency.json](examples/rag-knowledge-retrieval.competency.json) | Uzel Competency s blokem ověření `production` |
| [sample.competency-relations.json](examples/sample.competency-relations.json) | Soubor vazeb: jedna kompetence zařazená do oblasti, vlastněná osobou (s hodnocením přímo na hraně), podložená zdrojem, použitá v sektoru a doložená důkazem. Ověřuje `make validate-contract`. |

## Verzování

- Schémata používají JSON Schema Draft 2020-12
- Verze schémat se řídí verzí projektu API
- Nekompatibilní změny vyžadují novou hlavní (major) verzi
- Zpětně kompatibilní doplňky (nová nepovinná pole) jsou změny vedlejší (minor) verze
- Schémata jsou zdrojem pravdy; implementace v jednotlivých jazycích se generují nebo ručně sladí

## Související

- [ADR-002: Unified AI Platform Transition](../docs/adr/002-unified-ai-platform-transition.md)
- [ADR-004: VIM as Universal Result Container](../portunix/portunix-architecture/docs/adr/ADR-004-vim-universal-result-container.md)
- [VIM v2.0 Format Specification](../portunix/portunix-architecture/docs/architecture/specifications/vim-v2-format.md)
- [VIM Prescriptive Layer Specification (VIM 2.1)](../portunix/portunix-architecture/docs/architecture/specifications/vim-prescriptive-layer.md)
