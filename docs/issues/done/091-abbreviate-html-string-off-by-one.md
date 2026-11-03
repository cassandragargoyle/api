# Issue #91: StringUtil.abbreviateHtmlString cuts one visible character too early

**Type**: Bug Fix
**Priority**: Low
**Status**: Implemented (PR #96, 2026-11-03)
**Created**: 2026-11-02
**Closed**: 2026-11-03
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

### Related defects found during testing

Tests of the first fix against `main` found these defects in the same method.
They are fixed in this issue, because they break the same rule of the
Javadoc: visible characters are counted exactly and the result stays valid
HTML. In each row, "Result before" is the result on `main`, unless the row
says otherwise.

| ID | Input | `maxLength` | Result before | Expected | Cause |
| -- | ----- | ----------- | ------------- | -------- | ----- |
| B1 | `"one two three four"`, `intelligent` | 3 | `"o..."` (first fix) | `"..."` | The first character is never removed |
| B2 | `"ab cdefghijklmnop"`, `intelligent` | 12 | `"ab ..."` (first fix) | `"ab..."` | The last position of the window is not checked |
| B3 | `"a&eacute;bcdefghijkl"` | 4 | `"a&eacu..."` | `"a&eacute;bc..."` | An entity with more than four characters overflows the counter |
| B4 | `"R&D department budget"` | 3 | `"R&D de..."` | `"R&D..."` | A bare `&` is handled as the start of an entity |
| B5 | `"<b>a</b><i>bcdefgh</i>"` | 3 | `"<b>a</b><i>b..."` | `"<b>a</b><i>bc...</i>"` | `endingTag` is never reset |
| B6 | `"one <b>two</b> three four"`, `intelligent` | 6 | `"one...</b>"` | `"one..."` | The cut moves back over raw characters, also inside tags and entities |
| B7 | `"a😀bcdef"` | 2 | `"a..."` | `"a😀..."` | A surrogate pair counts as two characters and can be split |
| B8 | `"<b><i><b>x</b>yyyy</i></b>"` | 3 | `"...y...</b></i>"` | `"...yy...</i></b>"` | A closing tag removes the first open tag with its name, not the last |
| B9 | `"abcdef"`, `intelligent`, `null` delimiters | 1 | `NullPointerException` (second fix) | `NullPointerException` for every `maxLength` | The `null` check depends on the position of the cut |
| B10 | Any text with a bare `&`, `intelligent` | | | | The CHANGELOG and criterion 9 said that such text does not change |
| B11 | `"1 < 2 and more text here"` | 3 | The whole text | `"1 <..."` | A bare `<` is handled as the start of a tag |
| B12 | `"<B>abcdef</b>"` | 3 | `"<B>ab..."` | `"<B>abc...</B>"` | Tag names are compared with regard to case |
| B13 | `"</b/><i>abcdefgh</i>"` | 3 | `"</b/><i>ab..."` | `"</b/><i>abc...</i>"` | `endingTag` is not reset after `</b/>` |
| B14 | `"<a title='a>b'>abcdef</a>"` | 3 | `"<a title='a>b'...</a>"` | `"<a title='a>b'>abc...</a>"` | A `>` in a quoted attribute value ends the tag |
| B15 | `"ab&amp;cd efghij"`, delimiter `\0` | 5 | `"ab..."` (second fix) | `"..."` | The mark for an entity can be equal to a delimiter |
| B16 | `"one  two three"`, `intelligent` | 5 | `"one ..."` | `"one..."` | Only one delimiter is removed at the cut |
| B17 | `"<b>abc</b>"` | 3 | `"<b>ab...</b>"` | `"<b>abc</b>"` | `"..."` is appended although no visible text is removed |
| B18 | `"e\u0301abcdef"` | 2 | `"e\u0301..."` | `"e\u0301a..."` | A grapheme of more than one code point is counted per `char` and can be split |
| B19 | `"<!-- a > b <i> -->abcdef"` | 3 | Text of the comment is counted | `"<!-- a > b <i> -->abc..."` | Comments, `<?...?>`, and the content of `<script>` and `<style>` are read as tags and text |
| B20 | `"<b>abcdef</b >"` | 3 | `"<b>ab..."` | `"<b>abc...</b>"` | The closing tag is searched as exact text |
| B21 | `"one&nbsp;two&nbsp;three"`, `intelligent`, delimiter U+00A0 | 6 | Entity is not a delimiter | `"one..."` | An entity is not compared as the character that it stands for |
| B22 | `"<a b=c'd>abcdef</a>"` | 3 | The tag does not end (second fix) | `"<a b=c'd>abc...</a>"` | A quote opens a value also when no `=` is before it |
| B23 | `"abc&#1114112;defghijklmnop"`, `intelligent` | 5 | `IllegalArgumentException` (third fix) | `"..."` | A numeric entity above U+10FFFF cannot be decoded |
| B24 | `"<a>"` 200000 times, then `"</x>"` 200000 times | 300000 | More than 20 seconds | Less than 10 seconds | Every closing tag searches the list of open tags |
| B25 | `"<script>// c\nalert(1)</script>abcdef"` | 3 | The new line becomes a space | `"<script>// c\nalert(1)</script>abc..."` | Line breaks are replaced before tags are read |
| B26 | `"<b>abc</b>   "` | 3 | `"<b>abc...</b>"` (third fix) | `"<b>abc</b>   "` | White space after the cut counts as removed text |
| B27 | `"if a<b then c and much more text here"` | 6 | The whole text | `"if a<b..."` | A `<` without a later `>` hides the rest of the text |
| B28 | `"a<b\nc<d\nmore text here"` | 3 | The new lines stay (fourth fix) | `"a<b..."` | A tag without an end is not text for the line break rule |

B1, B2, B9, B15, B22, B23, B26, and B28 come from the fixes of this issue, the
other defects exist on `main`.

## Scope

### In scope

1. Append the character that reaches `maxLength` visible characters, also
   for the last character of an entity such as `&amp;`
2. Check the `intelligent` mode after the change: the cut must still move
   back to the last word delimiter and must not remove one character too
   many
3. Enable `StringUtilTest.abbreviateHtmlStringKeepsMaxLengthVisibleCharacters`
   (remove `@Disabled`) and add a test with an entity at the cut
4. Fix B1 to B28. An entity is `&name;`, `&#123;` or `&#x7B;`, of any length;
   any other `&` is one visible character. A `<` starts a tag only before an
   ASCII letter, `/`, `!` or `?` and with a later `>`; any other `<` is one
   visible character. When a quote in a tag is not closed, the first `>` after
   the quote ends the tag, so that the text after it is not lost
5. With `intelligent`, count the search window in visible characters, so
   that the cut does not depend on the tags in the text
6. Move the logic to the package-private class `HtmlAbbreviator`, because
   `StringUtil.java` reaches the Checkstyle limit of 1500 lines. Read tags as
   units instead of character by character, because comments, script content,
   and the check for visible text after the cut need it
7. Append `"..."` only when visible text is removed. This is the decision on
   the question that the first version of this issue left open; when only
   markup follows the cut, the markup is kept
8. An entity is a word delimiter when `wordDelimeters` contains the character
   that the entity stands for. `&nbsp;` does not match a space, because a
   non-breaking space must not break a line; a caller who wants this adds
   U+00A0

9. In `StringUtil.bytesToHexString`, use `java.util.HexFormat` (Java 17) for
   the digits of a byte instead of the own digit table. This cleanup came up
   during the work on this issue and is done here on request. The method and
   its result stay the same; only the conversion of one byte changes, because
   `HexFormat.formatHex` cannot write a delimiter after the last byte, the
   extra delimiter after 8 bytes, and the line breaks

10. Add a test with generated input and a fixed seed, and a test with a time
    limit for a long text, because defects B8 and B23 were found only with
    generated input

As in a browser, `<script/>` and `<style/>` do not end the element, so all text
up to the closing tag is not visible. The rules for graphemes come from the Java
runtime, so the count for some scripts can change with the Java version. The
Javadoc describes both.

### Out of scope (follow-up issues)

- `<textarea>` and `<title>` content is counted as visible text
- `<BR>` and `<br >` are read as tags, only the forms `<br>`, `<br/>`, and
  `<br />` become spaces
- Names of entities are not checked, `&foo;` counts as one character
- Other changes of the abbreviation rules

## Acceptance Criteria

1. `abbreviateHtmlString("abcdef", 3, false, null)` gives `"abc..."`
2. `abbreviateHtmlString("<b>bold text</b> rest", 4, false, null)` gives
   `"<b>bold...</b>"`
3. An entity of any length counts as one visible character also at the cut
   and is never split
4. The `intelligent` mode still cuts at a word delimiter, for example
   `"one two three four"` with `maxLength` 10 does not split a word
5. `StringUtilTest` runs without disabled tests and
   `mvn -f java/pom.xml clean verify` passes
6. A bare `&` counts as one visible character
7. Every tag that is open at the cut is closed in the correct order when the
   rest of the text contains its closing tag, also after an earlier closing
   tag, with nested tags of the same name, and with a different case
8. The result with `intelligent` has no split tag, no split entity, and no
   closing tag without its opening tag
9. For ASCII text without tags, `&`, `<`, consecutive delimiters, and white
   space at the end, the result with `intelligent` is the same as on `main`
10. A bare `<` counts as one visible character and a `>` in a quoted attribute
    value does not end the tag
11. A grapheme counts as one visible character and is never split
12. With `intelligent`, `null` for `wordDelimeters` throws
    `NullPointerException` for every non-null text, and the result never ends
    with a delimiter before `"..."`
13. `"..."` is in the result only when visible text is removed, otherwise the
    result is the whole text after the line breaks are replaced. White space
    alone is not visible text for this rule
14. Comments, `<?...?>`, and the content of `<script>` and `<style>` are not
    counted, and tags in them do not change the open tags
15. A closing tag such as `</b >` counts as the closing tag of `<b>`
16. `bytesToHexString` gives the same result as before for all 256 byte values,
    and the class has no own table of hexadecimal digits
17. No numeric entity causes an exception, also a value above U+10FFFF
18. `<br>` and new lines are replaced only in the text, script content,
    comments, and attribute values do not change
19. A text with 200000 open tags and 200000 closing tags without a pair takes
    less than 10 seconds, and so does a text with 1000000 `<` without a `>`
20. A `<` without a later `>` counts as one visible character, so such text is
    shortened and its line breaks are replaced

## Testing

- Run `StringUtilTest`; the disabled test must pass after the fix
- Compare the result with `main` on generated input for criteria 8, 9, and 13
- Run `make coverage-java` and check that the branch coverage of
  `StringUtil` does not decrease
- Check the callers of `abbreviateHtmlString` in dependent projects: the
  result can be one character longer after the fix
