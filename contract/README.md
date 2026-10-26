# Contract - Unified AI Plugin & Task Platform

Language-independent contracts for the Unified AI Plugin Platform and Unified AI Task Platform.

## Purpose

This directory contains JSON Schema definitions that define the communication contracts between plugins and the platform. All plugins (Java, Python, Go) must conform to these schemas.

![The contract directory: schemas, examples validated by the schemas, and gRPC protos, grouped into nine contract areas](data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPSc3NjAnIGhlaWdodD0nNDAwJyB2aWV3Qm94PScwIDAgNzYwIDQwMCc+PHRpdGxlPkNvbnRlbnQgb2YgdGhlIGNvbnRyYWN0IGRpcmVjdG9yeTwvdGl0bGU+PHJlY3Qgd2lkdGg9Jzc2MCcgaGVpZ2h0PSc0MDAnIHJ4PScxMCcgZmlsbD0nI2ZmZmZmZicvPjxkZWZzPjxtYXJrZXIgaWQ9J2EnIHZpZXdCb3g9JzAgMCAxMCAxMCcgcmVmWD0nOScgcmVmWT0nNScgbWFya2VyV2lkdGg9JzcnIG1hcmtlckhlaWdodD0nNycgb3JpZW50PSdhdXRvJz48cGF0aCBkPSdNMCwwIEwxMCw1IEwwLDEwIHonIGZpbGw9JyMxZjI5MzMnLz48L21hcmtlcj48bWFya2VyIGlkPSdiJyB2aWV3Qm94PScwIDAgMTAgMTAnIHJlZlg9JzknIHJlZlk9JzUnIG1hcmtlcldpZHRoPSc3JyBtYXJrZXJIZWlnaHQ9JzcnIG9yaWVudD0nYXV0byc+PHBhdGggZD0nTTAsMCBMMTAsNSBMMCwxMCB6JyBmaWxsPScjMjU2M2ViJy8+PC9tYXJrZXI+PG1hcmtlciBpZD0nbycgdmlld0JveD0nMCAwIDEwIDEwJyByZWZYPSc5JyByZWZZPSc1JyBtYXJrZXJXaWR0aD0nNycgbWFya2VySGVpZ2h0PSc3JyBvcmllbnQ9J2F1dG8nPjxwYXRoIGQ9J00wLDAgTDEwLDUgTDAsMTAgeicgZmlsbD0nI2MyNDEwYycvPjwvbWFya2VyPjxtYXJrZXIgaWQ9J2cnIHZpZXdCb3g9JzAgMCAxMCAxMCcgcmVmWD0nOScgcmVmWT0nNScgbWFya2VyV2lkdGg9JzcnIG1hcmtlckhlaWdodD0nNycgb3JpZW50PSdhdXRvJz48cGF0aCBkPSdNMCwwIEwxMCw1IEwwLDEwIHonIGZpbGw9JyMxNTgwM2QnLz48L21hcmtlcj48L2RlZnM+PHRleHQgeD0nMzgwLjAnIHk9JzM0JyBmb250LXNpemU9JzIwJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5XaGF0IGlzIGluIGNvbnRyYWN0LzwvdGV4dD48cmVjdCB4PSc0MCcgeT0nNTYnIHdpZHRoPScxODAnIGhlaWdodD0nOTInIHJ4PSc4JyBmaWxsPScjZGJlYWZlJyBzdHJva2U9JyMyNTYzZWInIHN0cm9rZS13aWR0aD0nMicvPjx0ZXh0IHg9JzEzMCcgeT0nODQnIGZvbnQtc2l6ZT0nMTcnIGZpbGw9JyMyNTYzZWInIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nQ29uc29sYXMsIE1lbmxvLCBtb25vc3BhY2UnPnNjaGVtYXMvPC90ZXh0Pjx0ZXh0IHg9JzEzMCcgeT0nMTA4JyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPkpTT04gU2NoZW1hIDIwMjAtMTI8L3RleHQ+PHRleHQgeD0nMTMwJyB5PScxMzAnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+dGhlIGRhdGEgZm9ybWF0czwvdGV4dD48cmVjdCB4PScyOTAnIHk9JzU2JyB3aWR0aD0nMTgwJyBoZWlnaHQ9JzkyJyByeD0nOCcgZmlsbD0nI2RjZmNlNycgc3Ryb2tlPScjMTU4MDNkJyBzdHJva2Utd2lkdGg9JzInLz48dGV4dCB4PSczODAnIHk9Jzg0JyBmb250LXNpemU9JzE3JyBmaWxsPScjMTU4MDNkJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5leGFtcGxlcy88L3RleHQ+PHRleHQgeD0nMzgwJyB5PScxMDgnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+dmFsaWQgZG9jdW1lbnRzPC90ZXh0Pjx0ZXh0IHg9JzM4MCcgeT0nMTMwJyBmb250LXNpemU9JzEyJyBmaWxsPScjNmI3Nzg1JyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPisgaW52YWxpZC8gbmVnYXRpdmUgdGVzdHM8L3RleHQ+PHJlY3QgeD0nNTQwJyB5PSc1Nicgd2lkdGg9JzE4MCcgaGVpZ2h0PSc5Micgcng9JzgnIGZpbGw9JyNmZmVkZDUnIHN0cm9rZT0nI2MyNDEwYycgc3Ryb2tlLXdpZHRoPScyJy8+PHRleHQgeD0nNjMwJyB5PSc4NCcgZm9udC1zaXplPScxNycgZmlsbD0nI2MyNDEwYycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+cHJvdG8vPC90ZXh0Pjx0ZXh0IHg9JzYzMCcgeT0nMTA4JyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmdSUEMgc2VydmljZXM8L3RleHQ+PHRleHQgeD0nNjMwJyB5PScxMzAnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+dGhlIHJlbW90ZSBjYWxsczwvdGV4dD48bGluZSB4MT0nMjkwJyB5MT0nMTAyJyB4Mj0nMjI2JyB5Mj0nMTAyJyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMicgbWFya2VyLWVuZD0ndXJsKCNnKScvPjx0ZXh0IHg9JzI1NScgeT0nMTY2JyBmb250LXNpemU9JzExJyBmaWxsPScjMTU4MDNkJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPnZhbGlkYXRlZCBieSB0aGUgc2NoZW1hczwvdGV4dD48dGV4dCB4PSczODAuMCcgeT0nMTg2JyBmb250LXNpemU9JzE1JyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5Db250cmFjdCBhcmVhczwvdGV4dD48cmVjdCB4PSc0Mi4wJyB5PScyMDAnIHdpZHRoPScxNjAnIGhlaWdodD0nMzQnIHJ4PScxNycgZmlsbD0nI2YxZjRmNycgc3Ryb2tlPScjYzlkMWRhJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PScxMjIuMCcgeT0nMjIyJyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPlRhc2sgUGxhdGZvcm08L3RleHQ+PHJlY3QgeD0nMjE0LjAnIHk9JzIwMCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSczNCcgcng9JzE3JyBmaWxsPScjZjFmNGY3JyBzdHJva2U9JyNjOWQxZGEnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzI5NC4wJyB5PScyMjInIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+VklNPC90ZXh0PjxyZWN0IHg9JzM4Ni4wJyB5PScyMDAnIHdpZHRoPScxNjAnIGhlaWdodD0nMzQnIHJ4PScxNycgZmlsbD0nI2YxZjRmNycgc3Ryb2tlPScjYzlkMWRhJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PSc0NjYuMCcgeT0nMjIyJyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPlBsdWdpbiBQbGF0Zm9ybTwvdGV4dD48cmVjdCB4PSc1NTguMCcgeT0nMjAwJyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzM0JyByeD0nMTcnIGZpbGw9JyNmMWY0ZjcnIHN0cm9rZT0nI2M5ZDFkYScgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNjM4LjAnIHk9JzIyMicgZm9udC1zaXplPScxMycgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5WaXN1YWxpemF0aW9uPC90ZXh0PjxyZWN0IHg9JzQyLjAnIHk9JzI0NCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSczNCcgcng9JzE3JyBmaWxsPScjZjFmNGY3JyBzdHJva2U9JyNjOWQxZGEnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzEyMi4wJyB5PScyNjYnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+VHJhbnNjcmlwdGlvbjwvdGV4dD48cmVjdCB4PScyMTQuMCcgeT0nMjQ0JyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzM0JyByeD0nMTcnIGZpbGw9JyNmMWY0ZjcnIHN0cm9rZT0nI2M5ZDFkYScgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nMjk0LjAnIHk9JzI2NicgZm9udC1zaXplPScxMycgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5Nb2RlbGluZzwvdGV4dD48cmVjdCB4PSczODYuMCcgeT0nMjQ0JyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzM0JyByeD0nMTcnIGZpbGw9JyNmMWY0ZjcnIHN0cm9rZT0nI2M5ZDFkYScgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNDY2LjAnIHk9JzI2NicgZm9udC1zaXplPScxMycgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5PcHBvcnR1bml0eSBNZ210PC90ZXh0PjxyZWN0IHg9JzU1OC4wJyB5PScyNDQnIHdpZHRoPScxNjAnIGhlaWdodD0nMzQnIHJ4PScxNycgZmlsbD0nI2YxZjRmNycgc3Ryb2tlPScjYzlkMWRhJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PSc2MzguMCcgeT0nMjY2JyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPkNvbXBldGVuY3kgTW9kZWw8L3RleHQ+PHJlY3QgeD0nMzAwLjAnIHk9JzI4OCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSczNCcgcng9JzE3JyBmaWxsPScjZjFmNGY3JyBzdHJva2U9JyNjOWQxZGEnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzM4MC4wJyB5PSczMTAnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+UGVvcGxlIFJlZ2lzdHJ5PC90ZXh0PjxsaW5lIHgxPSczODAuMCcgeTE9JzMzNCcgeDI9JzM4MC4wJyB5Mj0nMzUyJyBzdHJva2U9JyMxZjI5MzMnIHN0cm9rZS13aWR0aD0nMicgbWFya2VyLWVuZD0ndXJsKCNhKScvPjxyZWN0IHg9JzExMCcgeT0nMzU2JyB3aWR0aD0nNTQwJyBoZWlnaHQ9JzM0JyByeD0nNicgZmlsbD0nI2ZmZmZmZicgc3Ryb2tlPScjMWYyOTMzJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PSczODAuMCcgeT0nMzc4JyBmb250LXNpemU9JzEzJyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPnVzZWQgYnkgSmF2YSDCtyBQeXRob24gwrcgR28gwrcgVHlwZVNjcmlwdCBtb2R1bGVzIGFuZCBQb3J0dW5peCB0b29sczwvdGV4dD48L3N2Zz4=)

## Schemas

### Task Platform

| Schema | Description |
| ------ | ----------- |
| [task-request.schema.json](schemas/task-request.schema.json) | Unified task request format |
| [task-response.schema.json](schemas/task-response.schema.json) | Unified task response format |
| [error.schema.json](schemas/error.schema.json) | Structured error model |
| [task-manifest.schema.json](schemas/task-manifest.schema.json) | Plugin task capability manifest |
| [ocr-result.schema.json](schemas/ocr-result.schema.json) | Standardized OCR result with text positioning |

### VIM (Layered Information Model)

| Schema | Description |
| ------ | ----------- |
| [vim-document.schema.json](schemas/vim-document.schema.json) | VIM root document structure (v2.0, v2.1); the `layers` object is closed; `prescriptive` requires `vim_version` `"2.1"`, `extraction` requires `"2.0"` or `"2.1"` |
| [vim-extraction.schema.json](schemas/vim-extraction.schema.json) | Extraction layer (template ref, sources, merged document) |
| [vim-extraction-source.schema.json](schemas/vim-extraction-source.schema.json) | Per-source extraction result with provenance |
| [vim-extraction-field.schema.json](schemas/vim-extraction-field.schema.json) | Extracted field, table, cell definitions; `Revision` (1.1.0: optional `property`) and `BoundingBox` are shared with the prescriptive layer |
| [vim-prescriptive.schema.json](schemas/vim-prescriptive.schema.json) | Prescriptive layer (VIM 2.1): `definition`, `provenance`, `provisions`, `references`, `terms`; defines `Definition`, `Reference`, `Target`, `Term` |
| [vim-prescriptive-provision.schema.json](schemas/vim-prescriptive-provision.schema.json) | One provision (`requirement`, `recommendation`, `permission`) with anchors, relations, verification, status and revisions; defines `Anchor`, `Relation`, `Verification` |

The prescriptive layer stores what a **definition file** prescribes (a specification, a
guideline, a contract). It is optional: a VIM 2.0 document stays valid. `Provenance`,
`Revision` and `BoundingBox` are not defined again; the new schemas refer to the
definitions of the extraction layer. VIM documents saved as examples use the
**`*.vim.json`** filename suffix.

A layer binds the document to the VIM versions that define it: a document with the
`prescriptive` layer has `vim_version` `"2.1"`, a document with the `extraction` layer has
`"2.0"` or `"2.1"`. A document without these layers can have every listed version.

![Matrix of VIM layers and vim_version values: extraction needs 2.0 or 2.1, prescriptive needs 2.1, the other layers are allowed with every version](data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPSc3NjAnIGhlaWdodD0nNDA2JyB2aWV3Qm94PScwIDAgNzYwIDQwNic+PHRpdGxlPlZJTSBsYXllcnMgYW5kIHRoZSBhbGxvd2VkIHZpbV92ZXJzaW9uIHZhbHVlczwvdGl0bGU+PHJlY3Qgd2lkdGg9Jzc2MCcgaGVpZ2h0PSc0MDYnIHJ4PScxMCcgZmlsbD0nI2ZmZmZmZicvPjx0ZXh0IHg9JzM4MC4wJyB5PSczNCcgZm9udC1zaXplPScxOScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+V2hpY2ggVklNIGxheWVyIGlzIGFsbG93ZWQgd2l0aCB3aGljaCB2aW1fdmVyc2lvbjwvdGV4dD48dGV4dCB4PSczODAuMCcgeT0nNTgnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+dmltLWRvY3VtZW50LnNjaGVtYS5qc29uIDEuMi4wPC90ZXh0Pjx0ZXh0IHg9JzM0NS4wJyB5PSc4NCcgZm9udC1zaXplPScxNScgZmlsbD0nIzI1NjNlYicgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+MS4wIOKAkyAxLjU8L3RleHQ+PHRleHQgeD0nNDk1LjAnIHk9Jzg0JyBmb250LXNpemU9JzE1JyBmaWxsPScjMjU2M2ViJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz4yLjA8L3RleHQ+PHRleHQgeD0nNjQ1LjAnIHk9Jzg0JyBmb250LXNpemU9JzE1JyBmaWxsPScjMjU2M2ViJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz4yLjE8L3RleHQ+PHRleHQgeD0nNDAnIHk9JzEyMScgZm9udC1zaXplPScxNScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5waHlzaWNhbDwvdGV4dD48dGV4dCB4PScxODAnIHk9JzEyMScgZm9udC1zaXplPScxMicgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPkxheWVyIDE8L3RleHQ+PHJlY3QgeD0nMjc0JyB5PScxMDAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzM0NS4wJyB5PScxMjEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNDI0JyB5PScxMDAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzQ5NS4wJyB5PScxMjEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNTc0JyB5PScxMDAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzY0NS4wJyB5PScxMjEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHRleHQgeD0nNDAnIHk9JzE2MScgZm9udC1zaXplPScxNScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5zdHJ1Y3R1cmFsPC90ZXh0Pjx0ZXh0IHg9JzE4MCcgeT0nMTYxJyBmb250LXNpemU9JzEyJyBmaWxsPScjNmI3Nzg1JyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+TGF5ZXIgMjwvdGV4dD48cmVjdCB4PScyNzQnIHk9JzE0MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nMzQ1LjAnIHk9JzE2MScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48cmVjdCB4PSc0MjQnIHk9JzE0MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNDk1LjAnIHk9JzE2MScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48cmVjdCB4PSc1NzQnIHk9JzE0MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNjQ1LjAnIHk9JzE2MScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48dGV4dCB4PSc0MCcgeT0nMjAxJyBmb250LXNpemU9JzE1JyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nQ29uc29sYXMsIE1lbmxvLCBtb25vc3BhY2UnPnNlbWFudGljPC90ZXh0Pjx0ZXh0IHg9JzE4MCcgeT0nMjAxJyBmb250LXNpemU9JzEyJyBmaWxsPScjNmI3Nzg1JyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+TGF5ZXIgMzwvdGV4dD48cmVjdCB4PScyNzQnIHk9JzE4MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nMzQ1LjAnIHk9JzIwMScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48cmVjdCB4PSc0MjQnIHk9JzE4MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNDk1LjAnIHk9JzIwMScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48cmVjdCB4PSc1NzQnIHk9JzE4MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNkY2ZjZTcnIHN0cm9rZT0nIzE1ODAzZCcgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNjQ1LjAnIHk9JzIwMScgZm9udC1zaXplPScxMycgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+YWxsb3dlZDwvdGV4dD48dGV4dCB4PSc0MCcgeT0nMjQxJyBmb250LXNpemU9JzE1JyBmaWxsPScjMWYyOTMzJyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nQ29uc29sYXMsIE1lbmxvLCBtb25vc3BhY2UnPnRvYzwvdGV4dD48dGV4dCB4PScxODAnIHk9JzI0MScgZm9udC1zaXplPScxMicgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPkxheWVyIDQ8L3RleHQ+PHJlY3QgeD0nMjc0JyB5PScyMjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzM0NS4wJyB5PScyNDEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNDI0JyB5PScyMjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzQ5NS4wJyB5PScyNDEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNTc0JyB5PScyMjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzY0NS4wJyB5PScyNDEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHRleHQgeD0nNDAnIHk9JzI4MScgZm9udC1zaXplPScxNScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5leHRyYWN0aW9uPC90ZXh0Pjx0ZXh0IHg9JzE4MCcgeT0nMjgxJyBmb250LXNpemU9JzEyJyBmaWxsPScjNmI3Nzg1JyB0ZXh0LWFuY2hvcj0nc3RhcnQnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+TGF5ZXIgNTwvdGV4dD48cmVjdCB4PScyNzQnIHk9JzI2MCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNmZmVkZDUnIHN0cm9rZT0nI2MyNDEwYycgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nMzQ1LjAnIHk9JzI4MScgZm9udC1zaXplPScxMycgZmlsbD0nI2MyNDEwYycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+cmVqZWN0ZWQ8L3RleHQ+PHJlY3QgeD0nNDI0JyB5PScyNjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzQ5NS4wJyB5PScyODEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHJlY3QgeD0nNTc0JyB5PScyNjAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzY0NS4wJyB5PScyODEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHRleHQgeD0nNDAnIHk9JzMyMScgZm9udC1zaXplPScxNScgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J3N0YXJ0JyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5wcmVzY3JpcHRpdmU8L3RleHQ+PHRleHQgeD0nMTgwJyB5PSczMjEnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdzdGFydCcgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5WSU0gMi4xPC90ZXh0PjxyZWN0IHg9JzI3NCcgeT0nMzAwJyB3aWR0aD0nMTQyJyBoZWlnaHQ9JzMyJyByeD0nNicgZmlsbD0nI2ZmZWRkNScgc3Ryb2tlPScjYzI0MTBjJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PSczNDUuMCcgeT0nMzIxJyBmb250LXNpemU9JzEzJyBmaWxsPScjYzI0MTBjJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nYm9sZCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5yZWplY3RlZDwvdGV4dD48cmVjdCB4PSc0MjQnIHk9JzMwMCcgd2lkdGg9JzE0MicgaGVpZ2h0PSczMicgcng9JzYnIGZpbGw9JyNmZmVkZDUnIHN0cm9rZT0nI2MyNDEwYycgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNDk1LjAnIHk9JzMyMScgZm9udC1zaXplPScxMycgZmlsbD0nI2MyNDEwYycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+cmVqZWN0ZWQ8L3RleHQ+PHJlY3QgeD0nNTc0JyB5PSczMDAnIHdpZHRoPScxNDInIGhlaWdodD0nMzInIHJ4PSc2JyBmaWxsPScjZGNmY2U3JyBzdHJva2U9JyMxNTgwM2QnIHN0cm9rZS13aWR0aD0nMScvPjx0ZXh0IHg9JzY0NS4wJyB5PSczMjEnIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxNTgwM2QnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmFsbG93ZWQ8L3RleHQ+PHRleHQgeD0nMzgwLjAnIHk9JzM3OCcgZm9udC1zaXplPScxMycgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J1NlZ29lIFVJLCBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmJz5BIGRvY3VtZW50IHdpdGhvdXQgdGhlIGV4dHJhY3Rpb24gYW5kIHByZXNjcmlwdGl2ZSBsYXllcnMgY2FuIHVzZSBldmVyeSB2ZXJzaW9uPC90ZXh0Pjwvc3ZnPg==)

#### What the schema checks and what the identifier check covers

JSON Schema cannot compare a value with the values of another array, so the
identifier rules of the prescriptive layer are checked in a second step,
[`scripts/vim-identifier-check.mjs`](../scripts/vim-identifier-check.mjs):

| Rule | Checked by |
| ---- | ---------- |
| Structure, required properties, allowed values, `vim_version` of a layer | `vim-document.schema.json` and the schemas it refers to |
| `provision_id`, `reference_id`, and `term_id` are unique in the layer | identifier check |
| Every value of `provisions[].references` is a `reference_id` of the layer | identifier check |
| Every value of `provisions[].terms` is a `term_id` of the layer | identifier check |
| Every `provisions[].relations[].provision_id` is a `provision_id` of the layer | identifier check |
| Every value of `terms[].references` is a `reference_id` of the layer | identifier check |

`make validate-contract` runs both steps on every `*.vim.json` document. The check is a
plain ES module without dependencies; another project can import `checkVimIdentifiers()`
and run it after its own schema check. Not checked: a `reference` with the scope
`internal` that points to a clause that does not exist, and the identifiers of the
`extraction` layer.

### Plugin Platform

| Schema | Description |
| ------ | ----------- |
| [plugin-manifest.schema.json](schemas/plugin-manifest.schema.json) | Plugin registration manifest (identity, runtime, commands, AI integration) |
| [plugin-permissions.schema.json](schemas/plugin-permissions.schema.json) | Plugin permission model (filesystem, network, database, system) |
| [plugin-health.schema.json](schemas/plugin-health.schema.json) | Plugin health check response (status, metrics, sub-checks) |
| [help-ai.schema.json](schemas/help-ai.schema.json) | Machine-readable `--help-ai` output (tool identity, commands, parameters, optional per-command `supported_extensions` / `supported_mime_types`) |

### Visualization

| Schema | Description |
| ------ | ----------- |
| [graph-view.schema.json](schemas/graph-view.schema.json) | 3D force-directed node-graph document for the graphlens viewer (`meta` + `nodes` + `links`) |

Graph-view documents use the **`*.glens.json`** filename suffix (tied to graphlens / `ptx-graphlens`) so tools, editors, and viewers recognize the type without inspecting contents.

> **Distinct from the 2D `*.graph.json` format.** The 2D Graph Canvas format (`GraphFile`: `nodes` + `edges` + `metadata`, Cytoscape, portunix-vscode #030) is a different contract. This 3D graph-view format uses `links` (not `edges`) and `meta` (not `metadata`), and must not reuse the `*.graph.json` suffix.

### Transcription

| Schema | Description |
| ------ | ----------- |
| [transcript.schema.json](schemas/transcript.schema.json) | Video/audio transcript working document for the scribe correction backend (`media` + `speakers` + `segments` + `flags`) |

Transcript documents use the **`*.scribe.json`** filename suffix (tied to scribe / `ptx-scribe`, portunix-plugins #113) so tools, editors, and the future VS Code transcript editor recognize the type without inspecting contents.

> **Provisional.** The shape may still change once reconciled against a real transcript sample (portunix-plugins #113, criterion 15); `schema_version` tracks that evolution. The transcript Markdown (front matter with `source` + `[mm:ss]` segments) is a lighter, related convention shared with `vox` and is not (yet) a formal schema here.

### Recognition Engine Lab

| Schema | Description |
| ------ | ----------- |
| [reco-engine-lab.schema.json](schemas/reco-engine-lab.schema.json) | Lab document of one recognition engine: engine (simple or composite with `components`, `config` files such as a template), datasets with ground truth, methodology, commands of the phases `prepare`, `train`, `test`, `evaluate` (runner `portunix-plugin` or `exec`), metrics, and the location of the run records |
| [reco-run.schema.json](schemas/reco-run.schema.json) | Record of one training or test run: status, engine version and checksums, dataset checksums, parameters, summary metrics, `baseline_run`, and the files of the run; `$defs/HistoryLine` is one line of the training history |
| [reco-run-sample.schema.json](schemas/reco-run-sample.schema.json) | One line of the per-sample results: sample, ground truth, prediction and its `prediction_format`, metrics, and the results per field |

| Suffix | File |
| ------ | ---- |
| `*.recolab.json` | Lab document, written by a person |
| `*.run.json` | Run record, written by the tool that runs the engine |
| `*.samples.jsonl` | Per-sample results of a run, one JSON document per line |
| `*.history.jsonl` | Training history of a run, one JSON document per line |

Reco Studio (Engine Lab, portunix-reco-studio #55) and `ptx-reco` read and write these files.
The placeholders of the commands, the dataset checksum, the `prediction_format` values, and the
comparison of two runs are described in
[schemas/README.md](schemas/README.md#recognition-engine-lab) (issue #56).

### Modeling

Building model of the Portunix Modeler (`ptx-modeler`, portunix-plugins #116): a flat entity
store (storeys, wall graph of junctions and walls, doors/windows, rooms, electrical devices and
circuits, imported underlay/annotations, title block) with layers and an operation log. The model is
authoritative; SVG/glTF drawings are derived views. Coordinates are integer millimetres.

| Schema | Description |
| ------ | ----------- |
| [modeler-model.schema.json](schemas/modeler-model.schema.json) | Model document (`schema: portunix.modeler/v1`): `layers` + `entities` (`id`, `kind`, `parent`, `layer`, `props`) + `log`. 1.1.0 (portunix-plugins #120): kind `circuit` (`designation`, `type`, `board`, `cable`, `protection`) referenced by `device.circuit`. 1.2.0 (portunix-plugins #118): `device.standoff` (drawing distance of a wall-hosted symbol from the wall face) |
| [modeler-ops.schema.json](schemas/modeler-ops.schema.json) | Operations (`op apply`): JSON array applied as one transaction; payloads reuse the entity props of the model schema. 1.1.0 (portunix-plugins #120): `circuit.add`, `circuit.set`. 1.2.0 (portunix-plugins #118): `device.attach` (absolute device → host-relative on the nearest wall face within a tolerance) |
| [modeler-bom.schema.json](schemas/modeler-bom.schema.json) | Bill of materials / device count (`ptx-modeler bom -f json`, portunix-plugins #120): devices grouped by symbol, room or circuit (`unassigned` last), multiplicity included, variants as separate items, deterministic order |
| [modeler-symbol.schema.json](schemas/modeler-symbol.schema.json) | Symbol element (`format: portunix.modeler.symbol/v1`, portunix-plugins #124): metadata, embedded SVG fragment (safe subset), terminals, text fields, standard compliance (`compliant`/`derived`/`custom`/`vendor`, e.g. IEC 60617) and provenance in `meta` (`author`, `license` as SPDX id, `licenseUrl`, `source`, `sourceUrl`, `attribution`). 1.2.0 (portunix-plugins #128): pinned `origin` (`type` git/file, `url`, `revision`, `path`, `format`, `sha256`) and opaque `extensions` keyed by namespace (e.g. `qet` for lossless QElectroTech round trips) |
| [modeler-symbol-source.schema.json](schemas/modeler-symbol-source.schema.json) | Symbol source (`format: portunix.modeler.symbol-source/v1`, portunix-plugins #130): one external symbol collection of the ptx-modeler source registry (e.g. QElectroTech elements, KiCad libraries) with repository, verified license facts (SPDX, usage, restrictions such as `no-ml-training`), formats, domains, standards, searchable categories, index strategy and tracking issue. The support status is computed by the plugin, never stored |
| [modeler-symbol-discovery.schema.json](schemas/modeler-symbol-discovery.schema.json) | JSON output of the symbol discovery (`ptx-modeler symbol search / sources / index / source probe\|add\|report -f json`, portunix-plugins #130) and of the `ModelerSymbolService` discovery RPCs (byte-identical); `kind` selects search, probe, issue-draft, sources, source, index, source-added or symbol-ready (`symbol search --use N`). Results carry availability levels and complete next-step actions for AI agents |

Model documents use the **`*.model.json`** filename suffix and operation files the
**`*.ops.json`** suffix, so tools, editors and AI agents recognize them without inspecting contents.
Symbol elements use the **`*.msym.json`** suffix (one self-contained file per symbol at
`symbols/<category>/<name>.msym.json` inside a symbol pack), so the VS Code viewer can open them directly.
Bills of materials saved as JSON use the **`*.bom.json`** suffix.
Symbol sources use the **`*.msrc.json`** suffix (`<id>.msrc.json`) and discovery outputs saved to
files the **`*.symbol-discovery.json`** suffix.
Their `meta` object carries the provenance of the drawing, so symbols converted from other
collections keep their attribution when edited or redistributed:

| `meta` field | Meaning |
| ------------ | ------- |
| `author` | Author(s) of the drawing, e.g. `The QElectroTech team` |
| `license` | SPDX license identifier, e.g. `MIT`, `CC-BY-3.0` (`LicenseRef-<name>` when there is none) |
| `licenseUrl` | http(s) link to the license text or the collection's license terms |
| `source` | Human-readable origin, e.g. collection and original file path |
| `sourceUrl` | http(s) link to the original symbol or collection |
| `attribution` | Notice to show when redistributing, as required by attribution licenses (CC BY) |

All fields are optional; further keys are allowed as free strings.

> **Single source of truth.** The plugin embeds identical copies of all modeler schemas and of
> `help-ai.schema.json` (`ptx-modeler schema [--ops | --symbol | --source | --discovery | --bom]`); a plugin
> test fails when they drift from this directory.

### Opportunity Management (v2)

Two-spine model authored by `ptx-pft` and read by Pilot (ADR-008, issue #015 —
**supersedes** the v1 set in #014). Discovery: Venture → Initiative → Idea.
Delivery: Team/Project → Epic → Use-Case. See
[schemas/README.md](schemas/README.md) for the storage model, linking principle,
and design decisions.

| Schema | Description |
| ------ | ----------- |
| [venture.schema.json](schemas/venture.schema.json) | Neutral `.venture` container (supersedes `discovery.schema.json`) |
| [initiative.schema.json](schemas/initiative.schema.json) | Strategic intent; `ideaRefs[]` (M:N) |
| [opportunity.schema.json](schemas/opportunity.schema.json) | Idea (demand asset); coarse `complexity` replaces story points |
| [use-case.schema.json](schemas/use-case.schema.json) | Delivery unit; `storyPoints`, `productRef`, `implementsIdeaRefs[]`, product-bound `solution`, contract terms (`contractRef`, `deliverables`, `acceptanceCriteria`, `outOfScope`, `assumptions`) |
| [product.schema.json](schemas/product.schema.json) | Product registered inside the venture; optional `vendorRef` to the owning organization |
| [team.schema.json](schemas/team.schema.json) | Team/Project registry; `kind`, optional `productRefs[]` |
| [backlog.schema.json](schemas/backlog.schema.json) | First-class backlog; `kind`, optional `teamRef`, membership + relations |
| [epic.schema.json](schemas/epic.schema.json) | Delivery grouping; `backlogRef`, `initiativeRefs[]`, `useCaseRefs[]` |
| [estimation.schema.json](schemas/estimation.schema.json) | Planning-poker history attached to a use-case (`subjectRef`) |
| [discussion.schema.json](schemas/discussion.schema.json) | General thread attachable to any entity (`subjectRef` + `subjectKind`) |

Each backlog's graph projection **reuses** `graph-view.schema.json`
(`*.glens.json`); the `implements` (use-case → idea) edge needs no schema change.

### Competency Model

Machine-readable contract for the AI competency model — a single source of truth
that projects into generated views (coverage map, gap analysis, bus-factor, role
staffing). Two core node types (**CompetencyArea**, **Competency**) stay stable;
people, resources, sectors and evidence attach through relations. Two independent
taxonomies: `area:*` (**WHAT** the organization can do) and `sector:*`
(**WHERE** it is applied). A competency describes *how* work is delivered; *what*
is delivered to a customer lives in the separate Capability Registry.

| Schema | Description |
| ------ | ----------- |
| [competency.schema.json](schemas/competency.schema.json) | Competency node — a concrete professional capability (`competency:*`) |
| [competency-area.schema.json](schemas/competency-area.schema.json) | CompetencyArea node — classification grouping competencies (`area:*`) |
| [competency-person.schema.json](schemas/competency-person.schema.json) | Person node — bearer of a competency (`person:*`), optional external registry `ref` |
| [competency-resource.schema.json](schemas/competency-resource.schema.json) | Resource node — technology/tool/framework/method/standard behind a competency (`resource:*`) |
| [competency-sector.schema.json](schemas/competency-sector.schema.json) | Sector node — field of application (`sector:*`) |
| [competency-evidence.schema.json](schemas/competency-evidence.schema.json) | Evidence node — project/reference/certification/repository/methodology/benchmark (`evidence:*`) |
| [competency-assessment.schema.json](schemas/competency-assessment.schema.json) | Assessment — proficiency (0-5) + AI enablement; inline on `has_competency` or a standalone node (`assessment:*`) |
| [competency-maturity.schema.json](schemas/competency-maturity.schema.json) | Maturity node — **derived** organizational maturity, computed from evidence + assessments (`maturity:*`) |
| [competency-verification.schema.json](schemas/competency-verification.schema.json) | Shared verification block (`status`, `source`, `lastChecked`, `evidence`) attached to every node and edge |
| [competency-relations.schema.json](schemas/competency-relations.schema.json) | Relations file — typed edges (`classified_in`, `owned_by`, `has_competency`, `backed_by`, `applied_in`, `evidenced_by`) |

Node ids follow the grammar `<prefix>:<slug>` where the slug uses dot-separated
levels of `[a-z0-9-]` (e.g. `competency:rag-knowledge-retrieval`). Edge-specific
fields are constrained by the relations schema: `has_competency` requires an
`assessment`, and `primary` is only valid on `classified_in`.

### People Registry

Layered people model that keeps master data in one place and lets projects
reference it instead of copying. Master data is never edited twice: per-project
layers carry only a `ref` plus project-specific fields. The company-wide registry
is a **separate** firm-scoped registry — project layers do **not** `ref` it; a
person present in both is keyed by the same id.

| Schema | Layer | Description |
| ------ | ----- | ----------- |
| [people-registry.schema.json](schemas/people-registry.schema.json) | 1 | Delivery-project shared registry (`projects/users.json`); master record per person, the `ref` target for layers 2/3 |
| [project-participants.schema.json](schemas/project-participants.schema.json) | 2 | Who is on a concrete project (`projects/<slug>/users.json`); `ref` + `projectRole`, optional `override` |
| [project-contacts.schema.json](schemas/project-contacts.schema.json) | 3 | Pending-contacts "waiting room" (`projects/<slug>/contacts.json`); `ref` + note, promoted to layer 2 once the role is clear |
| [company-people-registry.schema.json](schemas/company-people-registry.schema.json) | — | Firm-wide registry (root `users.json`); superset of the master fields plus `relation`; presence = active cooperation |

## Proto

gRPC service contracts. These are the canonical definitions — servers and clients vendor a copy
into their own tree rather than hand-writing the wire format, and re-sync when the contract moves.

| Proto | Service | Description |
| ----- | ------- | ----------- |
| [task-platform.proto](proto/task-platform.proto) | `portunix.platform.v1.TaskPlatformService` | Universal task discovery and execution; mirrors `task-manifest` / `task-request` / `task-response` schemas |
| [scribe.proto](proto/scribe.proto) | `portunix.scribe.ScribeService` | Transcript correction backend (segments, speakers, flags, Markdown export); mirrors [transcript.schema.json](schemas/transcript.schema.json) |
| [modeler.proto](proto/modeler.proto) | `portunix.modeler.ModelerSymbolService` | Portunix Modeler symbol conversions (portunix-plugins #128): QElectroTech `.elmt` ↔ `*.msym.json` for single elements (bytes) and collections/packs (server paths), with the CLI's conversion warnings. KiCad `.kicad_sym` → `*.msym.json` (portunix-plugins #140): `ImportKicadSymbol` (bytes, one symbol per unit) and `ImportKicadLibrary` (server paths); symbols validate against [modeler-symbol.schema.json](schemas/modeler-symbol.schema.json). Symbol source discovery (portunix-plugins #130): `SearchSymbols`, `ListSources`, `GetSource`, `ProbeSource`, `DraftSourceIssue` return the CLI's JSON documents ([modeler-symbol-discovery.schema.json](schemas/modeler-symbol-discovery.schema.json)); no RPC creates issues. `portunix.modeler.ModelerContractService` (portunix-plugins #144): `GetContractInfo`, `ListContractFiles`, `GetContractFile` serve the versioned contract snapshot embedded in the plugin (manifest, schemas, this file); `Health` reports `contract_version`. The `// contract-version:` line in the file header is the semver of the modeler contract |

## Examples

`make validate-contract` checks every example file in the same way:

![Validation flow: the file suffix selects the schema, the schema check runs, then the extra check for VIM documents; negative tests must be rejected](data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPSc3NjAnIGhlaWdodD0nMzAwJyB2aWV3Qm94PScwIDAgNzYwIDMwMCc+PHRpdGxlPlZhbGlkYXRpb24gZmxvdyBvZiB0aGUgY29udHJhY3QgZXhhbXBsZXM8L3RpdGxlPjxyZWN0IHdpZHRoPSc3NjAnIGhlaWdodD0nMzAwJyByeD0nMTAnIGZpbGw9JyNmZmZmZmYnLz48ZGVmcz48bWFya2VyIGlkPSdhJyB2aWV3Qm94PScwIDAgMTAgMTAnIHJlZlg9JzknIHJlZlk9JzUnIG1hcmtlcldpZHRoPSc3JyBtYXJrZXJIZWlnaHQ9JzcnIG9yaWVudD0nYXV0byc+PHBhdGggZD0nTTAsMCBMMTAsNSBMMCwxMCB6JyBmaWxsPScjMWYyOTMzJy8+PC9tYXJrZXI+PG1hcmtlciBpZD0nYicgdmlld0JveD0nMCAwIDEwIDEwJyByZWZYPSc5JyByZWZZPSc1JyBtYXJrZXJXaWR0aD0nNycgbWFya2VySGVpZ2h0PSc3JyBvcmllbnQ9J2F1dG8nPjxwYXRoIGQ9J00wLDAgTDEwLDUgTDAsMTAgeicgZmlsbD0nIzI1NjNlYicvPjwvbWFya2VyPjxtYXJrZXIgaWQ9J28nIHZpZXdCb3g9JzAgMCAxMCAxMCcgcmVmWD0nOScgcmVmWT0nNScgbWFya2VyV2lkdGg9JzcnIG1hcmtlckhlaWdodD0nNycgb3JpZW50PSdhdXRvJz48cGF0aCBkPSdNMCwwIEwxMCw1IEwwLDEwIHonIGZpbGw9JyNjMjQxMGMnLz48L21hcmtlcj48bWFya2VyIGlkPSdnJyB2aWV3Qm94PScwIDAgMTAgMTAnIHJlZlg9JzknIHJlZlk9JzUnIG1hcmtlcldpZHRoPSc3JyBtYXJrZXJIZWlnaHQ9JzcnIG9yaWVudD0nYXV0byc+PHBhdGggZD0nTTAsMCBMMTAsNSBMMCwxMCB6JyBmaWxsPScjMTU4MDNkJy8+PC9tYXJrZXI+PC9kZWZzPjx0ZXh0IHg9JzM4MC4wJyB5PSczNCcgZm9udC1zaXplPScxOCcgZmlsbD0nIzFmMjkzMycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+V2hhdCBtYWtlIHZhbGlkYXRlLWNvbnRyYWN0IGRvZXMgd2l0aCBvbmUgZXhhbXBsZSBmaWxlPC90ZXh0PjxyZWN0IHg9JzIwJyB5PSc3MCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSc3MCcgcng9JzgnIGZpbGw9JyNmMWY0ZjcnIHN0cm9rZT0nI2M5ZDFkYScgc3Ryb2tlLXdpZHRoPScyJy8+PHRleHQgeD0nMTAwJyB5PScxMDAnIGZvbnQtc2l6ZT0nMTUnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmV4YW1wbGUgZmlsZTwvdGV4dD48dGV4dCB4PScxMDAnIHk9JzEyNCcgZm9udC1zaXplPScxMicgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5ob3VzZS5vcHMuanNvbjwvdGV4dD48cmVjdCB4PScyMDAnIHk9JzcwJyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzcwJyByeD0nOCcgZmlsbD0nI2RiZWFmZScgc3Ryb2tlPScjMjU2M2ViJyBzdHJva2Utd2lkdGg9JzInLz48dGV4dCB4PScyODAnIHk9JzEwMCcgZm9udC1zaXplPScxNScgZmlsbD0nIzI1NjNlYicgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+cGljayBzY2hlbWE8L3RleHQ+PHRleHQgeD0nMjgwJyB5PScxMjQnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+YnkgZmlsZSBzdWZmaXg8L3RleHQ+PHJlY3QgeD0nMzgwJyB5PSc3MCcgd2lkdGg9JzE2MCcgaGVpZ2h0PSc3MCcgcng9JzgnIGZpbGw9JyNkYmVhZmUnIHN0cm9rZT0nIzI1NjNlYicgc3Ryb2tlLXdpZHRoPScyJy8+PHRleHQgeD0nNDYwJyB5PScxMDAnIGZvbnQtc2l6ZT0nMTUnIGZpbGw9JyMyNTYzZWInIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPnNjaGVtYSBjaGVjazwvdGV4dD48dGV4dCB4PSc0NjAnIHk9JzEyNCcgZm9udC1zaXplPScxMicgZmlsbD0nIzZiNzc4NScgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J25vcm1hbCcgZm9udC1mYW1pbHk9J0NvbnNvbGFzLCBNZW5sbywgbW9ub3NwYWNlJz5KU09OIFNjaGVtYSAoYWp2KTwvdGV4dD48cmVjdCB4PSc1NjAnIHk9JzcwJyB3aWR0aD0nMTYwJyBoZWlnaHQ9JzcwJyByeD0nOCcgZmlsbD0nI2ZmZWRkNScgc3Ryb2tlPScjYzI0MTBjJyBzdHJva2Utd2lkdGg9JzInLz48dGV4dCB4PSc2NDAnIHk9JzEwMCcgZm9udC1zaXplPScxNScgZmlsbD0nI2MyNDEwYycgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+ZXh0cmEgY2hlY2s8L3RleHQ+PHRleHQgeD0nNjQwJyB5PScxMjQnIGZvbnQtc2l6ZT0nMTInIGZpbGw9JyM2Yjc3ODUnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdDb25zb2xhcywgTWVubG8sIG1vbm9zcGFjZSc+Ki52aW0uanNvbiBvbmx5PC90ZXh0PjxsaW5lIHgxPScxODAnIHkxPScxMDUnIHgyPScxOTgnIHkyPScxMDUnIHN0cm9rZT0nIzFmMjkzMycgc3Ryb2tlLXdpZHRoPScyJyBtYXJrZXItZW5kPSd1cmwoI2EpJy8+PGxpbmUgeDE9JzM2MCcgeTE9JzEwNScgeDI9JzM3OCcgeTI9JzEwNScgc3Ryb2tlPScjMWYyOTMzJyBzdHJva2Utd2lkdGg9JzInIG1hcmtlci1lbmQ9J3VybCgjYSknLz48bGluZSB4MT0nNTQwJyB5MT0nMTA1JyB4Mj0nNTU4JyB5Mj0nMTA1JyBzdHJva2U9JyMxZjI5MzMnIHN0cm9rZS13aWR0aD0nMicgbWFya2VyLWVuZD0ndXJsKCNhKScvPjx0ZXh0IHg9JzY0MCcgeT0nMTYwJyBmb250LXNpemU9JzExJyBmaWxsPScjYzI0MTBjJyB0ZXh0LWFuY2hvcj0nbWlkZGxlJyBmb250LXdlaWdodD0nbm9ybWFsJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmlkZW50aWZpZXIgY2hlY2sgb2Y8L3RleHQ+PHRleHQgeD0nNjQwJyB5PScxNzUnIGZvbnQtc2l6ZT0nMTEnIGZpbGw9JyNjMjQxMGMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+dGhlIHByZXNjcmlwdGl2ZSBsYXllcjwvdGV4dD48cmVjdCB4PSc2MCcgeT0nMjAwJyB3aWR0aD0nMzAwJyBoZWlnaHQ9Jzc2JyByeD0nOCcgZmlsbD0nI2RjZmNlNycgc3Ryb2tlPScjMTU4MDNkJyBzdHJva2Utd2lkdGg9JzEnLz48dGV4dCB4PScyMTAnIHk9JzIyOCcgZm9udC1zaXplPScxNCcgZmlsbD0nIzE1ODAzZCcgdGV4dC1hbmNob3I9J21pZGRsZScgZm9udC13ZWlnaHQ9J2JvbGQnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+bm9ybWFsIGV4YW1wbGU8L3RleHQ+PHRleHQgeD0nMjEwJyB5PScyNTInIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+UEFTUyB3aGVuIGJvdGggY2hlY2tzIGFjY2VwdCBpdDwvdGV4dD48cmVjdCB4PSc0MDAnIHk9JzIwMCcgd2lkdGg9JzMwMCcgaGVpZ2h0PSc3Nicgcng9JzgnIGZpbGw9JyNmZmVkZDUnIHN0cm9rZT0nI2MyNDEwYycgc3Ryb2tlLXdpZHRoPScxJy8+PHRleHQgeD0nNTUwJyB5PScyMjgnIGZvbnQtc2l6ZT0nMTQnIGZpbGw9JyNjMjQxMGMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdib2xkJyBmb250LWZhbWlseT0nU2Vnb2UgVUksIEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWYnPmZpbGUgdW5kZXIgZXhhbXBsZXMvaW52YWxpZC88L3RleHQ+PHRleHQgeD0nNTUwJyB5PScyNTInIGZvbnQtc2l6ZT0nMTMnIGZpbGw9JyMxZjI5MzMnIHRleHQtYW5jaG9yPSdtaWRkbGUnIGZvbnQtd2VpZ2h0PSdub3JtYWwnIGZvbnQtZmFtaWx5PSdTZWdvZSBVSSwgSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZic+UEFTUyB3aGVuIG9uZSBjaGVjayByZWplY3RzIGl0PC90ZXh0Pjwvc3ZnPg==)

### Task Platform Examples

| Example | Description |
| ------- | ----------- |
| [image-resize-request.json](examples/image-resize-request.json) | Image resize task request |
| [response-success.json](examples/response-success.json) | Successful task response |
| [response-error.json](examples/response-error.json) | Error task response |
| [task-manifest-image-resize.json](examples/task-manifest-image-resize.json) | Task manifest for image resize plugin |

### VIM Examples

| Example | Description |
| ------- | ----------- |
| [definition-file.vim.json](examples/definition-file.vim.json) | VIM 2.1 document with the prescriptive layer: four provisions (all three kinds, one without verbatim `text`), an external and an internal reference, a term, relations, verification rules and revisions with `property`. Validated by `make validate-contract`. |
| [drawing-definition-file.vim.json](examples/drawing-definition-file.vim.json) | VIM 2.1 document of a bilingual drawing: Czech verbatim `text` with English `statement` (`definition.language`), a month of issue (`issued: "2008-11"`), an external reference known only by its `title`, a term defined in a referenced standard (`terms[].references`), and anchors with `bbox` and without `element_id`. |
| [form-extraction.vim.json](examples/form-extraction.vim.json) | VIM 2.0 document with the extraction layer only, with a `Revision` without `property`. Validated by `make validate-contract`. |
| [invalid/](examples/invalid/) | Negative tests: documents that must **not** validate. Each file is the same small VIM 2.1 document with one defect, named after it (for example `provision-unknown-kind`, `anchor-page-zero`, `reference-external-without-identifier-and-title`); one file per rule of the prescriptive schemas, of the version rules (`prescriptive-wrong-vim-version`, `extraction-wrong-vim-version`, the latter a VIM 1.5 document with the extraction layer), and of the identifier check (`provision-duplicate-id`, `relation-without-target` and others). `make validate-contract` fails when the schema and the identifier check both accept one of them. |

### Plugin Platform Examples

| Example | Description |
| ------- | ----------- |
| [plugin-manifest-reco.json](examples/plugin-manifest-reco.json) | Plugin manifest for reco (Python, helper) |
| [plugin-manifest-text-extractor.json](examples/plugin-manifest-text-extractor.json) | Plugin manifest for text-extractor (Java, MCP) |
| [plugin-health-serving.json](examples/plugin-health-serving.json) | Healthy plugin with sub-checks |
| [help-ai-text-extractor.json](examples/help-ai-text-extractor.json) | `--help-ai` output for text-extractor (with `supported_extensions` / `supported_mime_types`) |

### Visualization Examples

| Example | Description |
| ------- | ----------- |
| [voices.glens.json](examples/voices.glens.json) | Small voices graph for the graphlens 3D viewer |

### Recognition Engine Lab Examples

| Example | Description |
| ------- | ----------- |
| [reco-engine-lab/stock-count.recolab.json](examples/reco-engine-lab/stock-count.recolab.json) | Lab document of the stock count form pipeline (`trocr` digits + `tesseract` text, template as `config`), with train, validation, and test datasets, plugin commands of `reco-worker` and an `exec` evaluate command. The samples and the model are not part of the example. Validated by `make validate-contract`. |
| [reco-engine-lab/runs/](examples/reco-engine-lab/runs/) | A training run with its `*.history.jsonl`, and two test runs with their `*.samples.jsonl`; the second test run has the first one as `baseline_run` and shows improved, regressed, and changed fields. The per-sample results list only some fields to stay short. Validated by `make validate-contract`. |
| [invalid/](examples/invalid/) | Negative tests with the prefixes `recolab-`, `run-`, `samples-`, and `history-`: one small document with one defect, named after it (for example `recolab-exec-command-without-argv`, `run-succeeded-without-finished`, `samples-line-not-json`). |

### Modeling Examples

| Example | Description |
| ------- | ----------- |
| [house.ops.json](examples/house.ops.json) | Operations creating a sample house: 4 rooms, wall graph with L/T/X joins, doors, windows, socket circuits, host-relative sockets/switches and ceiling lights. Validated by `make validate-contract`. |
| [house.model.json](examples/house.model.json) | The model produced by `ptx-modeler init` + `op apply` of `house.ops.json` (incl. operation log). Validated by `make validate-contract`. |
| [house.bom.json](examples/house.bom.json) | `ptx-modeler bom -f json --by circuit` of `house.model.json`: socket circuits with their devices, the rest `unassigned`. Validated by `make validate-contract`. |
| [socket-single.msym.json](examples/socket-single.msym.json) | Symbol element derived from IEC 60617-11 (single socket outlet) with a terminal and a label text field. Validated by `make validate-contract`. |
| [sensor-temperature.msym.json](examples/sensor-temperature.msym.json) | `custom` symbol element (no standard symbol referenced), as in the ptx-modeler core pack. Validated by `make validate-contract`. |
| [smart-relay-qet.msym.json](examples/smart-relay-qet.msym.json) | Symbol imported from a QElectroTech `.elmt` element (`ptx-modeler symbol import-elmt`): `data-qet-*` SVG attributes, `extensions.qet` (link type, kind/element informations, terminal and dynamic text data) and a pinned `origin`. Validated by `make validate-contract`. |
| [qelectrotech-elements.msrc.json](examples/qelectrotech-elements.msrc.json) | Curated symbol source of the ptx-modeler registry: QElectroTech elements (CC BY 3.0 with `no-ml-training`, license verified 2026-09-27), IEC 60617 path and localized categories. Validated by `make validate-contract`. |
| [kicad-symbols.msrc.json](examples/kicad-symbols.msrc.json) | Curated symbol source: KiCad schematic symbol libraries on GitLab (CC BY-SA 4.0 with the design exception), 223 libraries as categories, tracking issue for the `kicad_sym` converter. Validated by `make validate-contract`. |
| [search-en-60617.symbol-discovery.json](examples/search-en-60617.symbol-discovery.json) | `ptx-modeler symbol search -f json "EN 60617 07-02-01"`: a `remote-source` result with license-review, fetch, index, convert and use actions. Validated by `make validate-contract`. |
| [probe-kicad.symbol-discovery.json](examples/probe-kicad.symbol-discovery.json) | `ptx-modeler symbol source probe -f json` of the registered KiCad source (answered offline from the registry, `track` action). Validated by `make validate-contract`. |
| [ready-jistic.symbol-discovery.json](examples/ready-jistic.symbol-discovery.json) | `ptx-modeler symbol search --use N -f json --lang cs jistič`: a QElectroTech element converted into the managed pack, with the symbol key and the `device.place` operation (`kind: symbol-ready`). Validated by `make validate-contract`. |

### Opportunity Management Examples

| Example | Description |
| ------- | ----------- |
| [ai-in-HR.venture/](examples/ai-in-HR.venture/) | Full v2 venture: 1 initiative, 2 ideas, 2 use-cases, 1 product, 1 team, a delivery backlog + epic, plus a discussion, an estimation, and the backlog's `*.glens.json` projection. Validated by `make validate-contract`. |

### Competency Model Examples

| Example | Description |
| ------- | ----------- |
| [llm-foundations.competency-area.json](examples/llm-foundations.competency-area.json) | A CompetencyArea node |
| [rag-knowledge-retrieval.competency.json](examples/rag-knowledge-retrieval.competency.json) | A Competency node with a `production` verification block |
| [sample.competency-relations.json](examples/sample.competency-relations.json) | A relations file: one competency classified into an area, owned by a person (with inline assessment), backed by a resource, applied in a sector, and evidenced. Validated by `make validate-contract`. |

## Versioning

- Schemas use JSON Schema Draft 2020-12
- Schema versions follow the API project version
- Breaking changes require a new major version
- Backward-compatible additions (new optional fields) are minor version changes
- Schemas are the source of truth; language-specific implementations are generated or manually aligned

## Related

- [ADR-002: Unified AI Platform Transition](../docs/adr/002-unified-ai-platform-transition.md)
- [ADR-004: VIM as Universal Result Container](../portunix/portunix-architecture/docs/adr/ADR-004-vim-universal-result-container.md)
- [VIM v2.0 Format Specification](../portunix/portunix-architecture/docs/architecture/specifications/vim-v2-format.md)
- [VIM Prescriptive Layer Specification (VIM 2.1)](../portunix/portunix-architecture/docs/architecture/specifications/vim-prescriptive-layer.md)
