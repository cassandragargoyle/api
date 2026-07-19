# Issue #17: Product schema — optional vendor / owner organization reference

**Type**: Enhancement
**Priority**: Medium
**Status**: ✅ Implemented
**Created**: 2026-07-16
**Labels**: contract, schema, product, opportunity-management, federation
**Repository**: Api
**Related**: #015 (Opportunity Management v2 — introduced `product.schema.json`)
**GitHub**: #17

## Summary

Add an **optional** field to `contract/schemas/product.schema.json` that records the
**organization that owns / produces the product** (vendor). Today the schema has
`additionalProperties: false` and no place for ownership, so a consumer that reuses the
canonical product schema cannot attribute a product to its vendor while staying conformant.

## Motivation

An external consumer — the InfiniteCare *strategy-ai-ps-veolia* delivery project — reuses
`product.schema.json` **verbatim** as the schema for its shared products register
(`registers/products/<slug>.product.json`) precisely to stay interoperable with the
CassandraGargoyle Opportunity Management store.

In that register the products have distinct vendors:

| Product | id | Vendor |
| ----------------- | ------------------ | --------------------- |
| FLOWIO | `prd-flowio` | Popron Systems |
| SMG Voda/Teplo/Elektro/Data | `prd-smg-*` | Popron Systems |
| HELIOS Nephrite | `prd-helios-nephrite` | Asseco Solutions |
| TINOS / PAROS / MINOS | `prd-tinos` … | Popron Systems (built on Helios Core) |

The vendor is intrinsic product identity (who makes it), yet there is nowhere conformant to
store it. It currently has to live as a model-side relation outside the product asset, which
splits ownership away from the record and breaks the "one asset, one file" expectation.

The same need arises for a future **cross-venture / federation product catalog** (see #015
open question 4): joining products across ventures is far more useful when each product knows
its owning organization.

## Proposal

Add an optional `vendorRef` (string) to `product.schema.json`:

```json
"vendorRef": {
  "type": ["string", "null"],
  "description": "Optional id of the organization that owns/produces this product. Resolved by the consumer's organization/subject registry. Null when unknown or not applicable."
}
```

- Keep `additionalProperties: false`; add the field **explicitly** so conformance is preserved.
- Optional + nullable — no impact on existing records or the `required` set (`id`, `slug`, `name`).
- Bump `$version` (1.0.0 → 1.1.0; additive, backward-compatible).
- Linking principle: `vendorRef` points from the product to a **stable organization asset**;
  the organization never points back. (Product↔organization is stable↔stable; ownership is
  modeled on the product because it is part of the product's identity.)

### Scope

- [x] Add optional `vendorRef` to `contract/schemas/product.schema.json`, bump `$version`.
- [x] Extend the `ai-in-HR.venture` product example with a `vendorRef` (illustrative).
- [x] Update `contract/schemas/README.md` / `contract/README.md` product rows.
- [x] `make validate-contract` green.

### Out of scope (follow-up)

- A dedicated `organization.schema.json` in Api (see open question 1). For now `vendorRef` is
  an **opaque string id** resolved by the consumer's own subjects/organization registry.

## Acceptance Criteria

1. `product.schema.json` accepts an optional, nullable `vendorRef`; `additionalProperties`
   stays `false`; existing examples still validate.
2. `$version` bumped; change is additive and backward-compatible.
3. README rows updated; contract validation green.

## Open Questions

1. **Target of `vendorRef`** — opaque string id (consumer-resolved) now, or introduce a minimal
   `organization.schema.json` in Api and make it a typed ref later?
2. **Naming** — `vendorRef` vs `ownerRef` vs `organizationRef`? (Proposal: `vendorRef`, matching
   the `*Ref` convention.)
3. **Product family / line** — should a separate `family`/`productLine` field capture a product
   line (e.g. Popron's TINOS/PAROS/MINOS built on the Helios Core platform)? Suggested as a
   distinct follow-up, not bundled here.

## References

- `contract/schemas/product.schema.json` (target)
- #015 Opportunity Management v2 (introduced the product schema; open question 4 — cross-venture identity)
- Consumer register + rationale: `InfiniteCare-architecture` →
  `projects/strategy-ai-ps-veolia-2026/ps-veolia-ai-strategy-2026-competency-model/registers/README.md`
