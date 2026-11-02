/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import static org.cassandragargoyle.api.NullAssertions.assertRejectsNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.text.Collator;
import java.util.Collection;
import org.junit.jupiter.api.Test;

/**
 * Tests of the null checks in {@link StringUtil}
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public class StringUtilNullCheckTest
{
	@Test
	public void compareMethodsRejectNull()
	{
		assertRejectsNull("str", () -> StringUtil.startsIgnoreCase(null, "a"));
		assertRejectsNull("prefix", () -> StringUtil.startsIgnoreCase("a", null));
		assertRejectsNull("str", () -> StringUtil.containsIgnoreCase(null, "a"));
		assertRejectsNull("sub", () -> StringUtil.containsIgnoreCase("a", null));
		assertRejectsNull("suffix", () -> StringUtil.endsIgnoreCase("a", null));
		assertRejectsNull("sb", () -> StringUtil.endsWith(null, "a"));
		assertRejectsNull("suffix", () -> StringUtil.endsWith(new StringBuilder(), null));
		assertRejectsNull("v1", () -> StringUtil.min(null, "a"));
		assertRejectsNull("v2", () -> StringUtil.min("a", null));
		assertRejectsNull("collator", () -> StringUtil.saveCollatorCompare(null, "a", "b"));
		assertRejectsNull("str", () -> StringUtil.ordinalIndexOf(null, "a", 1));
		assertRejectsNull("substr", () -> StringUtil.ordinalIndexOf("a", null, 1));
	}

	@Test
	public void compareMethodsStillAcceptNullWhereDocumented()
	{
		assertFalse(StringUtil.endsIgnoreCase(null, "a"));
		assertEquals(0, StringUtil.saveCollatorCompare(Collator.getInstance(), null, null));
	}

	@Test
	public void htmlMethodsRejectNull()
	{
		StringBuilder sb = new StringBuilder();
		assertRejectsNull("sb", () -> StringUtil.appendToHtml(null, "a"));
		assertRejectsNull("value", () -> StringUtil.appendToHtml(sb, null));
		assertRejectsNull("sb", () -> StringUtil.appendHtmlEscaped(null, "a"));
		assertRejectsNull("value", () -> StringUtil.appendHtmlEscaped(sb, null));
		assertRejectsNull("sb", () -> StringUtil.appendHtmlEscaped(null, "a", 10));
		assertRejectsNull("value", () -> StringUtil.appendHtmlEscaped(sb, null, 10));
		assertRejectsNull("sb", () -> StringUtil.appendHtmlEscaped(null, "a", 10, 1));
		assertRejectsNull("value", () -> StringUtil.appendHtmlEscaped(sb, null, 10, 1));
		assertRejectsNull("value", () -> StringUtil.escapeForXml(null));
		assertNull(StringUtil.wrapHTMLFragment(null));
		assertNull(StringUtil.abbreviateHtmlString(null, 10, false, null));
	}

	@Test
	public void textMethodsRejectNull()
	{
		assertRejectsNull("bytes", () -> StringUtil.bytesToHexString(null, ' ', 0));
		assertRejectsNull("input", () -> StringUtil.isAllASCII(null));
		assertRejectsNull("input", () -> StringUtil.isAllASCIILetters(null));
		assertRejectsNull("s", () -> StringUtil.firstNotEmpty((String[]) null));
		assertRejectsNull("value", () -> StringUtil.firstNonWhiteChar(null, 0));
		assertRejectsNull("value", () -> StringUtil.removeSuffix(null, "a"));
		assertRejectsNull("suffix", () -> StringUtil.removeSuffix("a", null));
		assertRejectsNull("value", () -> StringUtil.removePrefix(null, "a"));
		assertRejectsNull("prefix", () -> StringUtil.removePrefix("a", null));
		assertRejectsNull("value", () -> StringUtil.longHash(null));
		assertRejectsNull("value", () -> StringUtil.longHash(null, 1L));
		assertRejectsNull("description", () -> StringUtil.multiLine(null, 10));
		assertEquals("a", StringUtil.firstNotEmpty(null, "", "a"));
	}

	@Test
	public void joinAndSplitMethodsRejectNull()
	{
		assertRejectsNull("parts", () -> StringUtil.toString((Collection<String>) null));
		assertRejectsNull("parts", () -> StringUtil.toString(null, ','));
		assertRejectsNull("value", () -> StringUtil.fromString(null));
		assertRejectsNull("value", () -> StringUtil.fromString(null, ','));
		assertRejectsNull("value", () -> StringUtil.stringPart(null, 0, ','));
		assertEquals(0, StringUtil.split(null, ',').size());
		assertEquals(0, StringUtil.splitAndStrip(null, ',').size());
	}

	@Test
	public void levenshteinDistanceRejectsNull()
	{
		assertRejectsNull("val0", () -> StringUtil.levenshteinDistance((String) null, "a"));
		assertRejectsNull("val1", () -> StringUtil.levenshteinDistance("a", (String) null));
		assertRejectsNull("val0", () -> StringUtil.levenshteinDistance((String[]) null, new String[0]));
		assertRejectsNull("val1", () -> StringUtil.levenshteinDistance(new String[0], (String[]) null));
	}
}
