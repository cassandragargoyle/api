# Issue #107: Contract: Character confidences and unrecognized characters in reco-form-result

**Type**: Enhancement
**Priority**: High
**Status**: ✅ Implemented (PR #108, 2026-11-15)
**Created**: 2026-11-15
**Closed**: 2026-11-15
**GitHub**: #107
**Component**: contract
**Related**: #82, portunix-reco #125

## Summary

`RecognitionZoneResult` of `reco-form-result.schema.json` has `additionalProperties: false` and does
not allow the new keys of portunix-reco #125: `char_confidences`, `unrecognized_chars` and
`suspicious_chars`. The schema gets the three optional properties, so a result with a TrOCR zone
passes the schema check again.

## Problem Description / Motivation

A mean confidence of a handwritten value hides one bad digit: when `10` is read as `90`, the `0` is
certain, only the `9` is not, and the mean stays high. portunix-reco #125 gives every character of a
TrOCR reading its own confidence. A character below the threshold `unrecognized_char_confidence` is
reported as unrecognized: it is replaced by a placeholder (default `*`) or only marked, and the value
is `suspicious`, so a person checks it.

The engine `trocr` has the default threshold 0.6. So every detected TrOCR zone writes
`char_confidences`, and a zone with an unrecognized character also writes `unrecognized_chars` and
`suspicious_chars`. In the acceptance test of portunix-reco #125, all 59 TrOCR zones of 5 real scans
failed the schema check; the same scans recognized by the current `main` of portunix-reco gave
0 errors. The validator reports:

```text
Additional properties are not allowed ('char_confidences', 'suspicious_chars', 'unrecognized_chars' were unexpected)
```

This blocks the merge of portunix-reco #125.

## The New Properties

All three are optional and written only for a detected zone (`detected: true`).

| Property | Type | Meaning |
| --- | --- | --- |
| `char_confidences` | array of `Probability` | Confidence of each character of `text`, in order; one item per character. Written when the engine has a confidence per character and the zone has a threshold |
| `unrecognized_chars` | array of integers, minimum 0, unique | Positions (0-based) in `text` of the characters below the threshold. Written only when not empty |
| `suspicious_chars` | array of integers, minimum 0, unique | Positions in `text` of the characters that need a check. Written only with `suspicious: true` and when not the whole value needs a check; without it, a suspicious value needs a check of every character |

Example of a zone result with an unrecognized digit:

```json
{
  "zone_id": "row_005",
  "detected": true,
  "text": "1*0",
  "confidence": 0.8123,
  "recognition_type": "handwritten",
  "engine": "trocr",
  "char_confidences": [0.9512, 0.3105, 0.9704],
  "unrecognized_chars": [1],
  "suspicious": true,
  "suspicious_chars": [1]
}
```

## Scope

### In scope

- The three properties in `$defs/RecognitionZoneResult`, with descriptions as in the table
- `suspicious_chars` requires `suspicious` (`dependentRequired`)
- A valid example with an unrecognized character, and a valid example of a zone below
  `suspicious_confidence` (with `unrecognized_chars` and `suspicious`, without `suspicious_chars`)
- Negative examples: a confidence above 1, a negative position, a position that is not an integer,
  `suspicious_chars` without `suspicious`

### Out of scope (follow-up issues)

- A check that the length of `char_confidences` equals the length of `text`, and that the positions
  are inside `text`: JSON Schema cannot compare them; portunix-reco tests them
- Code changes in portunix-reco: done in portunix-reco #125

## Acceptance Criteria

1. A zone result with `char_confidences`, `unrecognized_chars` and `suspicious_chars` passes the schema
   check
2. A zone result without the three properties passes the schema check as before
3. A confidence outside 0 to 1 in `char_confidences` fails the schema check
4. A negative or a non-integer position in `unrecognized_chars` or `suspicious_chars` fails the schema
   check
5. `suspicious_chars` without `suspicious` fails the schema check
6. Other unknown properties are still rejected (`additionalProperties: false` stays)

## Compatibility

The change only adds optional properties. Every document that was valid stays valid. A consumer
that reads `SuspiciousSymbols` or `suspicious` does not need to change; a consumer that wants to mark
single characters reads `suspicious_chars`.

## Testing

- `make validate-contract` with the new valid examples
- The negative examples of the scope section
- In portunix-reco: the acceptance job `125-acc-real-compare` (check C5) gives 0 contract errors for
  the default threshold
