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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests of the behavior of {@link StringUtil} described in its Javadoc
 *
 * <p>
 * The null checks are tested in {@link StringUtilNullCheckTest}, the blank checks in {@link StringUtilBlankTest}
 *
 * @author Zdenek
 * @since 1.2.1
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
	@Disabled("Defect: the text is cut one visible character before maxLength, \"abcdef\" with 3 gives \"ab...\"")
	public void abbreviateHtmlStringKeepsMaxLengthVisibleCharacters()
	{
		assertEquals("abc...", StringUtil.abbreviateHtmlString("abcdef", 3, false, null));
		assertEquals("<b>bold...</b>", StringUtil.abbreviateHtmlString("<b>bold text</b> rest", 4, false, null));
	}

	@Test
	public void abbreviateHtmlStringCountsEntityAsOneCharacter()
	{
		String result = StringUtil.abbreviateHtmlString("a&amp;bcdefgh", 4, false, null);
		assertTrue(result.startsWith("a&amp;b"), result);
		assertTrue(result.endsWith("..."), result);
	}

	@Test
	public void abbreviateHtmlStringIntelligentCutsAtWordDelimiter()
	{
		String result = StringUtil.abbreviateHtmlString("one two three four", 10, true, new char[] { ' ' });
		assertEquals("one two...", result);
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
