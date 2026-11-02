/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.text.Collator;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests of the behavior of {@link StringUtil} described in its Javadoc
 *
 * <p>
 * The null checks are tested in {@link StringUtilNullCheckTest}, the blank checks in {@link StringUtilBlankTest}
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public class StringUtilTest
{
	// --- Hexadecimal text

	@Test
	public void bytesToHexStringWritesTwoDigitsAndDelimiterPerByte()
	{
		assertEquals("ca fe ", StringUtil.bytesToHexString(new byte[] { (byte) 0xCA, (byte) 0xFE }, ' ', 0));
		assertEquals("", StringUtil.bytesToHexString(new byte[0], ' ', 0));
	}

	@Test
	public void bytesToHexStringAddsDelimiterAfterEightBytesAndNewLineAfterLineLength()
	{
		byte[] bytes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		assertEquals("00 01 02 03 04 05 06 07  08 09 ", StringUtil.bytesToHexString(bytes, ' ', 16));
		assertEquals("00 01 \n02 ", StringUtil.bytesToHexString(new byte[] { 0, 1, 2 }, ' ', 2));
	}

	// --- Case-insensitive tests

	@Test
	public void startsIgnoreCaseIgnoresCase()
	{
		assertTrue(StringUtil.startsIgnoreCase("Hello", "HE"));
		assertTrue(StringUtil.startsIgnoreCase("Hello", ""));
		assertFalse(StringUtil.startsIgnoreCase("He", "Hello"));
		assertFalse(StringUtil.startsIgnoreCase("Hello", "lo"));
	}

	@Test
	public void containsIgnoreCaseIgnoresCase()
	{
		assertTrue(StringUtil.containsIgnoreCase("Hello World", "WORLD"));
		assertFalse(StringUtil.containsIgnoreCase("ab", "abc"));
		assertFalse(StringUtil.containsIgnoreCase("Hello", "x"));
	}

	@Test
	public void endsIgnoreCaseIgnoresCaseAndAcceptsNullString()
	{
		assertTrue(StringUtil.endsIgnoreCase("File.TXT", ".txt"));
		assertFalse(StringUtil.endsIgnoreCase("a", "abc"));
		assertFalse(StringUtil.endsIgnoreCase(null, "x"));
	}

	// --- HTML

	@Test
	public void appendToHtmlRemovesHtmlTagsWithoutEscaping()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendToHtml(sb, "<HTML><b>x</b></HTML>");
		assertEquals("<b>x</b>", sb.toString());

		sb.setLength(0);
		StringUtil.appendToHtml(sb, "<html><b>x</b>");
		assertEquals("<b>x</b>", sb.toString());
	}

	@Test
	public void appendToHtmlEscapesPlainText()
	{
		StringBuilder sb = new StringBuilder("start:");
		StringUtil.appendToHtml(sb, "a<b");
		assertEquals("start:a&lt;b", sb.toString());
	}

	@Test
	public void appendHtmlEscapedEscapesTagsAmpersandAndNewLine()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "a<b>&c\nd\"e");
		assertEquals("a&lt;b&gt;&amp;c<br>\nd\"e", sb.toString());
	}

	@Test
	public void appendHtmlEscapedWrapsAfterLastSpace()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "aaa bbb ccc", 5);
		assertEquals("aaa <br>\nbbb <br>\nccc", sb.toString());
	}

	@Test
	public void appendHtmlEscapedBreaksLineWithoutSpace()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "abcdefgh", 4);
		assertEquals("abcd<br>\nefgh<br>\n", sb.toString());
	}

	@Test
	public void appendHtmlEscapedCutsTextAfterMaxLines()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "aaa bbb ccc", 5, 1);
		assertTrue(sb.toString().startsWith("aaa <br>\n"), sb.toString());
		assertTrue(sb.toString().endsWith("..."), sb.toString());
		assertFalse(sb.toString().contains("ccc"), sb.toString());
	}

	@Test
	public void appendHtmlEscapedWithWrappingEscapesLikeWithoutWrapping()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "a<b>&c\nd", 20, 5);
		assertEquals("a&lt;b&gt;&amp;c<br>\nd", sb.toString());
	}

	@Test
	public void appendHtmlEscapedCountsTextCharactersNotEscapeSequences()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "<&>", 3, 5);
		assertEquals("&lt;&amp;&gt;<br>\n", sb.toString());
	}

	@Test
	public void appendHtmlEscapedStartsNewLineCountAfterNewLine()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "abc\nabc", 4, 5);
		assertEquals("abc<br>\nabc", sb.toString());
	}

	@Test
	public void appendHtmlEscapedCountsNewLinesToMaxLines()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "a\nb\nc", 10, 2);
		assertEquals("a<br>\nb<br>\n...", sb.toString());
	}

	@Test
	public void appendHtmlEscapedKeepsShortTextWithoutEllipsis()
	{
		StringBuilder sb = new StringBuilder();
		StringUtil.appendHtmlEscaped(sb, "a<b", 10, 1);
		assertEquals("a&lt;b", sb.toString());
	}

	@Test
	public void wrapHTMLFragmentTrimsAndWrapsOnlyPlainText()
	{
		assertEquals("<html>text</html>", StringUtil.wrapHTMLFragment("  text "));
		assertEquals("<HTML>x", StringUtil.wrapHTMLFragment("<HTML>x"));
		assertNull(StringUtil.wrapHTMLFragment(null));
	}

	@Test
	public void unwrapHTMLFragmentRemovesTagsInAnyCase()
	{
		assertEquals("x", StringUtil.unwrapHTMLFragment(" <html>x</HTML> "));
		assertEquals("plain", StringUtil.unwrapHTMLFragment("plain"));
		assertNull(StringUtil.unwrapHTMLFragment(null));
		assertEquals("text", StringUtil.unwrapHTMLFragment(StringUtil.wrapHTMLFragment("text")));
	}

	@Test
	public void abbreviateHtmlStringKeepsShortTextAndReplacesLineBreaks()
	{
		assertNull(StringUtil.abbreviateHtmlString(null, 10, false, null));
		assertEquals("short", StringUtil.abbreviateHtmlString("short", 10, false, null));
		assertEquals("a b c d", StringUtil.abbreviateHtmlString("a<br>b<br/>c\nd", 10, false, null));
	}

	@Test
	public void abbreviateHtmlStringCutsLongTextAndClosesOpenTags()
	{
		String result = StringUtil.abbreviateHtmlString("<b>bold text</b> rest", 4, false, null);
		assertTrue(result.startsWith("<b>bo"), result);
		assertTrue(result.endsWith("...</b>"), result);
	}

	@Test
	public void abbreviateHtmlStringKeepsMaxLengthVisibleCharacters()
	{
		assertEquals("abc...", StringUtil.abbreviateHtmlString("abcdef", 3, false, null));
		assertEquals("<b>bold...</b>", StringUtil.abbreviateHtmlString("<b>bold text</b> rest", 4, false, null));
	}

	@Test
	public void abbreviateHtmlStringCountsEntityAsOneCharacter()
	{
		assertEquals("a&amp;bc...", StringUtil.abbreviateHtmlString("a&amp;bcdefgh", 4, false, null));
	}

	@Test
	public void abbreviateHtmlStringKeepsWholeEntityAtTheCut()
	{
		assertEquals("a&amp;...", StringUtil.abbreviateHtmlString("a&amp;bcdefgh", 2, false, null));
	}

	@Test
	public void abbreviateHtmlStringKeepsLastCharacterAtEndOfText()
	{
		assertEquals("<b>abc", StringUtil.abbreviateHtmlString("<b>abc", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringIntelligentCutsAtWordDelimiter()
	{
		String result = StringUtil.abbreviateHtmlString("one two three four", 10, true, new char[] { ' ' });
		assertEquals("one two...", result);
	}

	@Test
	public void abbreviateHtmlStringIntelligentRemovesDelimiterAtTheCut()
	{
		String result = StringUtil.abbreviateHtmlString("one two three four", 8, true, new char[] { ' ' });
		assertEquals("one two...", result);
	}

	@Test
	public void abbreviateHtmlStringCountsLongEntityAsOneCharacter()
	{
		assertEquals("&ndash;...", StringUtil.abbreviateHtmlString("&ndash;abcdef", 1, false, null));
		assertEquals("a&eacute;bc...", StringUtil.abbreviateHtmlString("a&eacute;bcdefghijkl", 4, false, null));
		assertEquals("&#8211;a...", StringUtil.abbreviateHtmlString("&#8211;abcdefgh", 2, false, null));
		assertEquals("&#x2013;a...", StringUtil.abbreviateHtmlString("&#x2013;abcdefgh", 2, false, null));
	}

	@Test
	public void abbreviateHtmlStringCountsBareAmpersandAsOneCharacter()
	{
		assertEquals("R&D...", StringUtil.abbreviateHtmlString("R&D department budget", 3, false, null));
		assertEquals("a &...", StringUtil.abbreviateHtmlString("a & b c d e f", 3, false, null));
		assertEquals("<i>ab&", StringUtil.abbreviateHtmlString("<i>ab&", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringClosesTagOpenedAfterClosingTag()
	{
		assertEquals("<b>a</b><i>bc...</i>", StringUtil.abbreviateHtmlString("<b>a</b><i>bcdefgh</i>", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringIntelligentKeepsTagsValid()
	{
		char[] space = { ' ' };
		assertEquals("one...", StringUtil.abbreviateHtmlString("one <b>two</b> three four", 6, true, space));
		assertEquals("aa...", StringUtil.abbreviateHtmlString("aa <b>bbbbbbbbbb</b> cc", 6, true, space));
		assertEquals("<b>one...</b>", StringUtil.abbreviateHtmlString("<b>one two</b> three four", 6, true, space));
		assertEquals("...", StringUtil.abbreviateHtmlString("<b>abcdefghij</b>", 5, true, space));
	}

	@Test
	public void abbreviateHtmlStringIntelligentKeepsEntitiesWhole()
	{
		char[] space = { ' ' };
		assertEquals("...", StringUtil.abbreviateHtmlString("&amp;c&amp;bbab", 2, true, space));
		assertEquals("one &amp;...", StringUtil.abbreviateHtmlString("one &amp; two three", 7, true, space));
		assertEquals("abcd...", StringUtil.abbreviateHtmlString("abcdefghijkl&amp;mnopqrstuv", 14, true, space));
	}

	@Test
	public void abbreviateHtmlStringIntelligentMovesBackTenCharactersWithoutDelimiter()
	{
		char[] space = { ' ' };
		assertEquals("...", StringUtil.abbreviateHtmlString("one two three four", 3, true, space));
		assertEquals("ab...", StringUtil.abbreviateHtmlString("abcdefghijklmnopqrst", 12, true, space));
	}

	@Test
	public void abbreviateHtmlStringIntelligentFindsDelimiterAtWindowStart()
	{
		assertEquals("ab...", StringUtil.abbreviateHtmlString("ab cdefghijklmnop", 12, true, new char[] { ' ' }));
		assertEquals("ab c...", StringUtil.abbreviateHtmlString("ab cdefghijklmnop", 14, true, new char[] { ' ' }));
	}

	@Test
	public void abbreviateHtmlStringClosesNestedSameTagsInCorrectOrder()
	{
		assertEquals("<b><i><b>x</b>yy...</i></b>",
			StringUtil.abbreviateHtmlString("<b><i><b>x</b>yyyy</i></b>", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringComparesTagNamesWithoutCase()
	{
		assertEquals("<B>abc...</B>", StringUtil.abbreviateHtmlString("<B>abcdef</b>", 3, false, null));
		assertEquals("<B>a</b><i>bc...</i>", StringUtil.abbreviateHtmlString("<B>a</b><i>bcdef</i>", 3, false, null));
		assertEquals("<B>a</b>bc...", StringUtil.abbreviateHtmlString("<B>a</b>bcdefg</b>", 3, false, null));
		assertEquals("<b>abc...</b>", StringUtil.abbreviateHtmlString("<b>abcdef</B>", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringHandlesMalformedClosingTag()
	{
		assertEquals("<b>a</b/><i>bc...</i>", StringUtil.abbreviateHtmlString("<b>a</b/><i>bcdefgh</i>", 3, false, null));
		assertEquals("</b/><i>abc...</i>", StringUtil.abbreviateHtmlString("</b/><i>abcdefgh</i>", 3, false, null));
		assertEquals("<b>a</b/>bc...", StringUtil.abbreviateHtmlString("<b>a</b/>bcdefgh</b>", 3, false, null));
		assertEquals("</<b>abc...", StringUtil.abbreviateHtmlString("</<b>abcdef</b>", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringCountsBareLessThanAsOneCharacter()
	{
		assertEquals("1 <...", StringUtil.abbreviateHtmlString("1 < 2 and more text here", 3, false, null));
		assertEquals("a<3...", StringUtil.abbreviateHtmlString("a<3 and more", 3, false, null));
		assertEquals("<i>ab<", StringUtil.abbreviateHtmlString("<i>ab<", 3, false, null));
		assertEquals("<\u010D ...", StringUtil.abbreviateHtmlString("<\u010D and more", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringRecognizesTagStart()
	{
		assertEquals("<!-- x -->abc...", StringUtil.abbreviateHtmlString("<!-- x -->abcdefgh", 3, false, null));
		assertEquals("<i>ab<c>", StringUtil.abbreviateHtmlString("<i>ab<c>", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringCountsLessThanWithoutTagEndAsText()
	{
		assertEquals("if a<b...", StringUtil.abbreviateHtmlString("if a<b then c and more text", 6, false, null));
		assertEquals("x<y...", StringUtil.abbreviateHtmlString("x<y and more text", 3, false, null));
		assertEquals("<i>ab<...", StringUtil.abbreviateHtmlString("<i>ab<c", 3, false, null));
		assertEquals("a<b c<d", StringUtil.abbreviateHtmlString("a<b\nc<d", 10, false, null));
		assertEquals("a<b...", StringUtil.abbreviateHtmlString("a<b\nc<d\nmore text here", 3, false, null));
		assertEquals("if...", StringUtil.abbreviateHtmlString("if a<b then c and more text", 6, true, new char[] { ' ' }));
	}

	@Test
	public void abbreviateHtmlStringIgnoresTagCharactersInQuotedAttribute()
	{
		assertEquals("<a title='a>b'>abc...</a>", StringUtil.abbreviateHtmlString("<a title='a>b'>abcdef</a>", 3, false, null));
		assertEquals("<a title=\"<b>\">abc...</a>", StringUtil.abbreviateHtmlString("<a title=\"<b>\">abcdef</a>", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringCountsSurrogatePairAsOneCharacter()
	{
		String emoji = "\uD83D\uDE00";
		assertEquals("a" + emoji + "...", StringUtil.abbreviateHtmlString("a" + emoji + "bcdef", 2, false, null));
		assertEquals("a" + emoji, StringUtil.abbreviateHtmlString("a" + emoji, 2, false, null));
		assertEquals("a\uD83D...", StringUtil.abbreviateHtmlString("a\uD83Dbcdef", 2, false, null));
		assertEquals("<i>a\uD83D", StringUtil.abbreviateHtmlString("<i>a\uD83D", 2, false, null));
	}

	@Test
	public void abbreviateHtmlStringIntelligentRemovesAllDelimitersAtTheCut()
	{
		char[] space = { ' ' };
		assertEquals("one...", StringUtil.abbreviateHtmlString("one  two three", 5, true, space));
		assertEquals("ab...", StringUtil.abbreviateHtmlString("ab cdefghijklmnop", 13, true, space));
		assertEquals("...", StringUtil.abbreviateHtmlString("   abcdefgh", 2, true, space));
	}

	@Test
	public void abbreviateHtmlStringIntelligentDoesNotMatchDelimiterInEntity()
	{
		assertEquals("...", StringUtil.abbreviateHtmlString("ab&amp;cdefgh", 4, true, new char[] { ';', 'a', '\0' }));
		assertEquals("a...", StringUtil.abbreviateHtmlString("a\0bcdefghij", 4, true, new char[] { '\0' }));
	}

	@Test
	public void abbreviateHtmlStringIntelligentComparesEntityAsItsCharacter()
	{
		String text = "one&nbsp;two&nbsp;three";
		assertEquals("...", StringUtil.abbreviateHtmlString(text, 6, true, new char[] { ' ' }));
		assertEquals("one...", StringUtil.abbreviateHtmlString(text, 6, true, new char[] { '\u00A0' }));
		assertEquals("ab...", StringUtil.abbreviateHtmlString("ab&amp;cdefgh", 4, true, new char[] { '&' }));
		assertEquals("ab...", StringUtil.abbreviateHtmlString("ab&#32;cdefgh", 4, true, new char[] { ' ' }));
		assertEquals("...", StringUtil.abbreviateHtmlString("ab&foo;cdefgh", 4, true, new char[] { ' ' }));
	}

	@Test
	public void abbreviateHtmlStringAddsNoEllipsisWhenOnlyTagsFollow()
	{
		assertEquals("<b>abc</b>", StringUtil.abbreviateHtmlString("<b>abc</b>", 3, false, null));
		assertEquals("<i><b>abc</b></i><!-- x -->", StringUtil.abbreviateHtmlString("<i><b>abc</b></i><!-- x -->", 3, false, null));
		assertEquals("<b>abc def</b>", StringUtil.abbreviateHtmlString("<b>abc def</b>", 7, true, new char[] { ' ' }));
		assertEquals("<b></b>", StringUtil.abbreviateHtmlString("<b></b>", 0, false, null));
		assertEquals("<b>abc...</b>", StringUtil.abbreviateHtmlString("<b>abc</b>d", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringCountsGraphemeAsOneCharacter()
	{
		String family = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67";
		String flag = "\uD83C\uDDE8\uD83C\uDDFF";
		assertEquals("e\u0301a...", StringUtil.abbreviateHtmlString("e\u0301abcdef", 2, false, null));
		assertEquals("a" + family + "...", StringUtil.abbreviateHtmlString("a" + family + "bcdef", 2, false, null));
		assertEquals(flag + "a...", StringUtil.abbreviateHtmlString(flag + "abcdef", 2, false, null));
		assertEquals("\u010Dau...", StringUtil.abbreviateHtmlString("\u010Daute svete", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringSkipsComments()
	{
		assertEquals("<!-- a > b <i> -->abc...", StringUtil.abbreviateHtmlString("<!-- a > b <i> -->abcdef", 3, false, null));
		assertEquals("<!-->abc...", StringUtil.abbreviateHtmlString("<!-->abcdef", 3, false, null));
		assertEquals("abc<!-- x", StringUtil.abbreviateHtmlString("abc<!-- x", 3, false, null));
		assertEquals("<?xml version='1.0'?>abc...", StringUtil.abbreviateHtmlString("<?xml version='1.0'?>abcdef", 3, false, null));
		assertEquals("<b>abc...", StringUtil.abbreviateHtmlString("<b>abcdef<!-- </b> -->", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringDoesNotCountScriptAndStyleContent()
	{
		assertEquals("<script>if (a<b) x='</b>';</script>abc...",
			StringUtil.abbreviateHtmlString("<script>if (a<b) x='</b>';</script>abcdef", 3, false, null));
		assertEquals("<STYLE>b > i { }</style>abc...",
			StringUtil.abbreviateHtmlString("<STYLE>b > i { }</style>abcdef", 3, false, null));
		assertEquals("abc<script>x", StringUtil.abbreviateHtmlString("abc<script>x", 3, false, null));
		assertEquals("<b>abc...", StringUtil.abbreviateHtmlString("<b>abcdef<script>'</b>'</script>", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringFindsClosingTagWithSpace()
	{
		assertEquals("<b>abc...</b>", StringUtil.abbreviateHtmlString("<b>abcdef</b >", 3, false, null));
		assertEquals("<b>a</b >bc...", StringUtil.abbreviateHtmlString("<b>a</b >bcdef</b>", 3, false, null));
		assertEquals("<b>abc...", StringUtil.abbreviateHtmlString("<b>abcdef</b", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringOpensQuotesOnlyForAttributeValues()
	{
		assertEquals("<a b=c'd>abc...</a>", StringUtil.abbreviateHtmlString("<a b=c'd>abcdef</a>", 3, false, null));
		assertEquals("<a b= 'c>d'>abc...</a>", StringUtil.abbreviateHtmlString("<a b= 'c>d'>abcdef</a>", 3, false, null));
		assertEquals("<a b='c>abc...", StringUtil.abbreviateHtmlString("<a b='c>abcdef", 3, false, null));
		assertEquals("<a b=\"c>abc...", StringUtil.abbreviateHtmlString("<a b=\"c>abcdef", 3, false, null));
		assertEquals("<a b='x>y' c=\"z>abc...", StringUtil.abbreviateHtmlString("<a b='x>y' c=\"z>abcdef", 3, false, null));
		assertEquals("<hr/>abc...", StringUtil.abbreviateHtmlString("<hr/>abcdef</hr>", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringIntelligentAcceptsEntityOutsideUnicode()
	{
		char[] space = { ' ' };
		assertEquals("...", StringUtil.abbreviateHtmlString("abc&#1114111;defghijklmnop", 5, true, space));
		assertEquals("...", StringUtil.abbreviateHtmlString("abc&#1114112;defghijklmnop", 5, true, space));
		assertEquals("...", StringUtil.abbreviateHtmlString("abc&#x110000;defghijklmnop", 5, true, space));
		assertEquals("...", StringUtil.abbreviateHtmlString("abc&#xFFFFFFF;defghijklmnop", 5, true, space));
		assertEquals("...", StringUtil.abbreviateHtmlString("abc&#99999999999999;defghijklmnop", 5, true, space));
		assertEquals("abc&#1114112;d...", StringUtil.abbreviateHtmlString("abc&#1114112;defghijklmnop", 5, false, null));
	}

	@Test
	public void abbreviateHtmlStringReplacesLineBreaksOnlyInText()
	{
		assertEquals("<script>// c\nalert(1)</script>abc...",
			StringUtil.abbreviateHtmlString("<script>// c\nalert(1)</script>abcdef", 3, false, null));
		assertEquals("<script>a='<br>'</script>abc...",
			StringUtil.abbreviateHtmlString("<script>a='<br>'</script>abcdef", 3, false, null));
		assertEquals("<a title='x<br>y'>abc...</a>", StringUtil.abbreviateHtmlString("<a title='x<br>y'>abcdef</a>", 3, false, null));
		assertEquals("<!-- a\nb -->abc...", StringUtil.abbreviateHtmlString("<!-- a\nb -->abcdef", 3, false, null));
		assertEquals("<a\nb='c'>abc...</a>", StringUtil.abbreviateHtmlString("<a\nb='c'>abcdef</a>", 3, false, null));
		assertEquals("a b...", StringUtil.abbreviateHtmlString("a\nb<br />cdefgh", 3, false, null));
		assertEquals("a b...", StringUtil.abbreviateHtmlString("a<br />bcdefgh", 3, false, null));
		assertEquals("<i>a b", StringUtil.abbreviateHtmlString("<i>a<br/>b", 10, false, null));
	}

	@Test
	public void abbreviateHtmlStringAddsNoEllipsisWhenOnlyWhiteSpaceFollows()
	{
		assertEquals("<b>abc</b>   ", StringUtil.abbreviateHtmlString("<b>abc</b>   ", 3, false, null));
		assertEquals("abc  \t", StringUtil.abbreviateHtmlString("abc  \t", 3, false, null));
		assertEquals("abc   ", StringUtil.abbreviateHtmlString("abc\n<br> ", 3, false, null));
		assertEquals("one two   ", StringUtil.abbreviateHtmlString("one two   ", 7, true, new char[] { ' ' }));
		assertEquals("abc...", StringUtil.abbreviateHtmlString("abc  d", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringKeepsTextAfterSelfClosedScript()
	{
		assertEquals("<script src='x'/>abcdef", StringUtil.abbreviateHtmlString("<script src='x'/>abcdef", 3, false, null));
		assertEquals("<style/>abc</style>def...",
			StringUtil.abbreviateHtmlString("<style/>abc</style>defghi", 3, false, null));
	}

	@Test
	public void abbreviateHtmlStringIntelligentKeepsTextEndingAtMaxLength()
	{
		assertEquals("<b>abc", StringUtil.abbreviateHtmlString("<b>abc", 3, true, new char[] { ' ' }));
	}

	// --- Number and boolean parsing

	@Test
	public void toIntParsesOrReturnsDefault()
	{
		assertEquals(42, StringUtil.toInt("42", 0));
		assertEquals(-3, StringUtil.toInt("-3", 0));
		assertEquals(-1, StringUtil.toInt(" 7", -1));
		assertEquals(5, StringUtil.toInt(null, 5));
		assertEquals(5, StringUtil.toInt("x", 5));
		assertEquals(5, StringUtil.toInt("9000000000", 5));
	}

	@Test
	public void toLongParsesOrReturnsDefault()
	{
		assertEquals(9_000_000_000L, StringUtil.toLong("9000000000", 0));
		assertEquals(-1L, StringUtil.toLong(" 1", -1));
		assertEquals(5L, StringUtil.toLong(null, 5));
	}

	@Test
	public void toDoubleParsesOrReturnsDefault()
	{
		assertEquals(1500.0, StringUtil.toDouble(" 1.5e3 ", 0));
		assertEquals(-1.0, StringUtil.toDouble("1,5", -1));
		assertEquals(2.0, StringUtil.toDouble(null, 2.0));
	}

	@Test
	public void toBooleanIsTrueOnlyForTrueInAnyCase()
	{
		assertTrue(StringUtil.toBoolean("TRUE"));
		assertFalse(StringUtil.toBoolean("yes"));
		assertFalse(StringUtil.toBoolean(null));
		assertTrue(StringUtil.toBoolean("", true));
		assertTrue(StringUtil.toBoolean(null, true));
		assertFalse(StringUtil.toBoolean("no", true));
	}

	// --- Empty checks and defaults

	@Test
	public void emptyChecksTreatSpacesAsNotEmpty()
	{
		assertTrue(StringUtil.isNullOrEmpty(null));
		assertTrue(StringUtil.isNullOrEmpty(""));
		assertFalse(StringUtil.isNullOrEmpty(" "));
		assertFalse(StringUtil.notEmpty(null));
		assertFalse(StringUtil.notEmpty(""));
		assertTrue(StringUtil.notEmpty(" "));
	}

	@Test
	public void safeStringReplacesNullWithEmptyString()
	{
		assertEquals("", StringUtil.safeString(null));
		assertEquals("a", StringUtil.safeString("a"));
	}

	@Test
	public void firstNotEmptyReturnsFirstNonEmptyArgument()
	{
		assertEquals(" ", StringUtil.firstNotEmpty(null, "", " ", "x"));
		assertEquals("", StringUtil.firstNotEmpty());
		assertEquals("", StringUtil.firstNotEmpty(null, ""));
	}

	@Test
	public void valueOrDefaultReplacesNullAndEmptyValue()
	{
		assertEquals("d", StringUtil.valueOrDefault("", "d"));
		assertEquals("d", StringUtil.valueOrDefault(null, "d"));
		assertEquals("v", StringUtil.valueOrDefault("v", "d"));
		assertNull(StringUtil.valueOrDefault(null, null));
	}

	// --- Character tests

	@Test
	public void isAllASCIIAcceptsCodesUpTo127()
	{
		assertTrue(StringUtil.isAllASCII("abc~\u007F"));
		assertTrue(StringUtil.isAllASCII(""));
		assertFalse(StringUtil.isAllASCII("č"));
	}

	@Test
	public void isAllASCIILettersAcceptsOnlyLetters()
	{
		assertTrue(StringUtil.isAllASCIILetters("abcXYZ"));
		assertTrue(StringUtil.isAllASCIILetters(""));
		assertFalse(StringUtil.isAllASCIILetters("ab1"));
		assertFalse(StringUtil.isAllASCIILetters("a b"));
		assertFalse(StringUtil.isAllASCIILetters("č"));
	}

	@Test
	public void firstNonWhiteCharSkipsWhitespaceFromPosition()
	{
		assertEquals('x', StringUtil.firstNonWhiteChar("  x", 0));
		assertEquals('b', StringUtil.firstNonWhiteChar("a b", 1));
		assertEquals(0, StringUtil.firstNonWhiteChar(" \t\n", 0));
		assertEquals(0, StringUtil.firstNonWhiteChar("ab", 5));
	}

	// --- Join and split

	@Test
	public void toStringEscapesSeparatorAndBackslash()
	{
		assertEquals("a\\;b;c;", StringUtil.toString(List.of("a;b", "c")));
		assertEquals("a\\\\b;", StringUtil.toString(List.of("a\\b")));
		assertEquals("a,b,", StringUtil.toString(List.of("a", "b"), ','));
		assertEquals("", StringUtil.toString(List.of()));
	}

	@Test
	public void fromStringReadsBackTextFromToString()
	{
		List<String> parts = List.of("a;b", "c\\d", "", "e");
		assertEquals(parts, StringUtil.fromString(StringUtil.toString(parts)));
		assertEquals(parts, StringUtil.fromString(StringUtil.toString(parts, '|'), '|'));
	}

	@Test
	public void fromStringLosesTextAfterLastSeparator()
	{
		assertEquals(List.of("a"), StringUtil.fromString("a;b"));
		assertEquals(List.of(), StringUtil.fromString(""));
	}

	@Test
	public void fromStringReturnsModifiableList()
	{
		List<String> parts = StringUtil.fromString("a;");
		parts.add("b");
		assertEquals(List.of("a", "b"), parts);
	}

	@Test
	public void stringPartReturnsFirstPart()
	{
		assertEquals("a", StringUtil.stringPart("a;b;", 0, ';'));
		assertEquals("a;b", StringUtil.stringPart("a\\;b;c;", 0, ';'));
		assertEquals("abc", StringUtil.stringPart("abc", 0, ';'));
	}

	@Test
	public void stringPartFindsOnlyPartZero()
	{
		// Documented defect: the part counter is never increased, so other parts are not found
		assertNull(StringUtil.stringPart("a;b;", 1, ';'));
	}

	@Test
	public void splitKeepsEmptyParts()
	{
		assertEquals(List.of("a", "b"), StringUtil.split("a,b", ','));
		assertEquals(List.of("", ""), StringUtil.split(",", ','));
		assertEquals(List.of("a", "", "c"), StringUtil.split("a,,c", ','));
		assertEquals(List.of("abc"), StringUtil.split("abc", ';'));
		assertEquals(List.of(" a "), StringUtil.split(" a ", ','));
	}

	@Test
	public void splitAndStripRemovesWhitespaceAroundParts()
	{
		assertEquals(List.of("a", "b", "", "c"), StringUtil.splitAndStrip("a, b,,c", ','));
		assertEquals(List.of("a", ""), StringUtil.splitAndStrip(" a ,  ", ','));
	}

	@Test
	public void splitReturnsUnmodifiableEmptyListForNullAndEmpty()
	{
		for (List<String> list : List.of(StringUtil.split(null, ','), StringUtil.split("", ','),
			StringUtil.splitAndStrip(null, ','), StringUtil.splitAndStrip("", ',')))
		{
			assertTrue(list.isEmpty());
			assertThrows(UnsupportedOperationException.class, () -> list.add("x"));
		}
	}

	// --- Prefix and suffix

	@Test
	public void removeSuffixIsCaseSensitive()
	{
		assertEquals("file", StringUtil.removeSuffix("file.txt", ".txt"));
		assertEquals("file.TXT", StringUtil.removeSuffix("file.TXT", ".txt"));
		assertEquals("file", StringUtil.removeSuffix("file", ""));
	}

	@Test
	public void removePrefixIsCaseSensitive()
	{
		assertEquals("Name", StringUtil.removePrefix("getName", "get"));
		assertEquals("GetName", StringUtil.removePrefix("GetName", "get"));
	}

	@Test
	public void endsWithTestsBuilderContent()
	{
		assertTrue(StringUtil.endsWith(new StringBuilder("abc"), "bc"));
		assertTrue(StringUtil.endsWith(new StringBuilder("abc"), ""));
		assertFalse(StringUtil.endsWith(new StringBuilder("ab"), "abc"));
		assertFalse(StringUtil.endsWith(new StringBuilder("abc"), "BC"));
	}

	// --- XML

	@Test
	public void escapeForXmlEscapesSpecialAndNonAsciiCharacters()
	{
		assertEquals("a&amp;b&lt;c&gt;&quot;d'&#10;\t&#233;", StringUtil.escapeForXml("a&b<c>\"d'\n\té"));
		assertEquals("&#269;", StringUtil.escapeForXml("č"));
		assertEquals("", StringUtil.escapeForXml(""));
	}

	// --- Comparison

	@Test
	public void minComparesByCharacterCodes()
	{
		assertEquals("B", StringUtil.min("a", "B"));
		assertEquals("a", StringUtil.min("a", "b"));
		String first = new String("same");
		assertSame(first, StringUtil.min(first, new String("same")));
	}

	@Test
	public void saveCollatorComparePutsNullFirst()
	{
		Collator collator = Collator.getInstance(Locale.ENGLISH);
		assertTrue(StringUtil.saveCollatorCompare(collator, null, "a") < 0);
		assertTrue(StringUtil.saveCollatorCompare(collator, "a", null) > 0);
		assertEquals(0, StringUtil.saveCollatorCompare(collator, null, null));
		assertTrue(StringUtil.saveCollatorCompare(collator, "a", "B") < 0);
	}

	@Test
	public void saveCollatorCompareUsesLanguageRules()
	{
		Collator czech = Collator.getInstance(Locale.forLanguageTag("cs"));
		assertTrue(StringUtil.saveCollatorCompare(czech, "chata", "hrad") > 0);
	}

	// --- Search

	@Test
	public void ordinalIndexOfFindsOverlappingOccurrences()
	{
		assertEquals(1, StringUtil.ordinalIndexOf("aaa", "aa", 2));
		assertEquals(4, StringUtil.ordinalIndexOf("abcabc", "bc", 2));
		assertEquals(-1, StringUtil.ordinalIndexOf("abab", "ab", 3));
		assertEquals(-1, StringUtil.ordinalIndexOf("abc", "x", 1));
	}

	@Test
	public void ordinalIndexOfTreatsZeroAsFirst()
	{
		assertEquals(1, StringUtil.ordinalIndexOf("xab", "ab", 0));
		assertEquals(1, StringUtil.ordinalIndexOf("xab", "ab", -2));
	}

	// --- Hash

	@Test
	public void longHashStartsWithDocumentedValue()
	{
		long start = 1125899906842597L;
		assertEquals(start, StringUtil.longHash(""));
		assertEquals(31 * start + 'a', StringUtil.longHash("a"));
	}

	@Test
	public void longHashCanContinueOverSeveralStrings()
	{
		assertEquals(StringUtil.longHash("abcdef"), StringUtil.longHash("def", StringUtil.longHash("abc")));
		assertEquals(StringUtil.longHash("Aa"), StringUtil.longHash("BB"));
	}

	// --- Text layout

	@Test
	public void multiLineKeepsShortTextUnchanged()
	{
		assertEquals("short", StringUtil.multiLine("short", 10));
	}

	@Test
	public void multiLineWrapsBetweenWordsAndEndsEveryLine()
	{
		assertEquals("aaa bbb\nccc\n", StringUtil.multiLine("aaa bbb ccc", 7));
		assertEquals("aaaa\nbbbb\ncc\n", StringUtil.multiLine("aaaa bbbb\ncc", 7));
		assertEquals("abcdefghij\nx\n", StringUtil.multiLine("abcdefghij x", 5));
	}

	@Test
	public void generateLabelReturnsEmptyOrShortTextAsItIs()
	{
		assertEquals("", StringUtil.generateLabel(null, 10));
		assertEquals("", StringUtil.generateLabel("", 10));
		assertEquals("short", StringUtil.generateLabel("  short  ", 20));
	}

	@Test
	public void generateLabelCutsBeforePunctuation()
	{
		assertEquals("Title", StringUtil.generateLabel("Title: some longer description", 10));
	}

	@Test
	public void generateLabelIsAtMostFiveCharactersLonger()
	{
		String label = StringUtil.generateLabel("This is a rather long first sentence without any break", 20);
		assertTrue(label.length() <= 25, label);
		assertFalse(label.isEmpty());
	}

	// --- Levenshtein distance

	@Test
	public void levenshteinDistanceCountsCharacterEdits()
	{
		assertEquals(3, StringUtil.levenshteinDistance("kitten", "sitting"));
		assertEquals(3, StringUtil.levenshteinDistance("", "abc"));
		assertEquals(3, StringUtil.levenshteinDistance("abc", ""));
		assertEquals(0, StringUtil.levenshteinDistance("same", "same"));
	}

	@Test
	public void levenshteinDistanceCountsWordEdits()
	{
		String[] a = { "the", "cat", "sat" };
		String[] b = { "the", "dog", "sat", "down" };
		assertEquals(2, StringUtil.levenshteinDistance(a, b));
		assertEquals(0, StringUtil.levenshteinDistance(a, a.clone()));
		assertEquals(3, StringUtil.levenshteinDistance(new String[0], a));
	}
}
