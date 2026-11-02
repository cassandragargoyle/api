# Issue #91: StringUtil.abbreviateHtmlString cuts one visible character too early

**Type**: Bug Fix
**Priority**: Low
**Status**: Open
**Created**: 2026-11-02
**GitHub**: #91
**Component**: java
**Related**: #85

## Summary

`StringUtil.abbreviateHtmlString` keeps `maxLength - 1` visible characters
instead of `maxLength`, as its Javadoc says. The defect was found by the
behavior tests for `StringUtil` added in #85.

## Problem Description / Motivation

The Javadoc says: "The text is cut at `maxLength` visible characters and
`"..."` is appended". The result has one visible character less:

| Input | `maxLength` | Result | Expected by the Javadoc |
| ----- | ----------- | ------ | ----------------------- |
| `"abcdef"` | 3 | `"ab..."` | `"abc..."` |
| `"<b>bold text</b> rest"` | 4 | `"<b>bol...</b>"` | `"<b>bold...</b>"` |
| `"a&amp;bcdefgh"` | 4 | `"a&amp;b..."` | `"a&amp;bc..."` |

Cause: in the loop, `count` is increased for a visible character before the
check `if (inTag || count < maxLength)` decides whether the character is
appended. The character that makes `count` equal to `maxLength` is counted
but not appended.

Text that is not longer than `maxLength` in total is not affected, because
the method returns it before the loop.

## Scope

### In scope

1. Append the character that reaches `maxLength` visible characters, also
   for the last character of an entity such as `&amp;`
2. Check the `intelligent` mode after the change: the cut must still move
   back to the last word delimiter and must not remove one character too
   many
3. Enable `StringUtilTest.abbreviateHtmlStringKeepsMaxLengthVisibleCharacters`
   (remove `@Disabled`) and add a test with an entity at the cut

### Out of scope (follow-up issues)

- `"..."` is also appended when only tags follow the last visible
  character, for example `"<b>abc</b>"` with `maxLength` 3. Decide in a
  separate issue whether this is wanted
- Other changes of the abbreviation rules

## Acceptance Criteria

1. `abbreviateHtmlString("abcdef", 3, false, null)` gives `"abc..."`
2. `abbreviateHtmlString("<b>bold text</b> rest", 4, false, null)` gives
   `"<b>bold...</b>"`
3. An entity counts as one visible character also at the cut
4. The `intelligent` mode still cuts at a word delimiter, for example
   `"one two three four"` with `maxLength` 10 does not split a word
5. `StringUtilTest` runs without disabled tests and
   `mvn -f java/pom.xml clean verify` passes

## Testing

- Run `StringUtilTest`; the disabled test must pass after the fix
- Run `make coverage-java` and check that the branch coverage of
  `StringUtil` does not decrease
- Check the callers of `abbreviateHtmlString` in dependent projects: the
  result can be one character longer after the fix
