/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link StringUtil#abbreviateHtmlString(String, int, boolean, char[])} with generated
 * input, the fixed seed makes every run the same
 *
 * @author Zdenek
 * @since 2026-11-03
 */
public class HtmlAbbreviatorPropertyTest
{
	private static final int RUNS = 20000;
	private static final long SEED = 91L;
	private static final char[] SPACE = { ' ' };
	private static final Pattern TAG = Pattern.compile("<(/?)([a-z]+)( [^>]*)?>");
	private static final Pattern ENTITY = Pattern.compile("&(#\\d+|#x\\p{XDigit}+|[a-z]+);");
	private static final String[] TEXT = {
		"a", "b", " ", "  ", "&amp;", "&eacute;", "&#8211;", "&#1114112;", "& ", "\uD83D\uDE00", "e\u0301"
	};
	private static final String[] BROKEN = {
		"<", ">", "/", "&", ";", "#", "'", "\"", "=", "a", " ", "\n", "<b>", "</b>", "</b/>", "<br>", "<!--", "-->",
		"<?", "<script>", "</script>", "<style", "&#x110000;", "&#;", "\uD83D", "\uDE00", "\u0301", "\u200D"
	};

	@Test
	public void cutKeepsExactlyMaxLengthVisibleCharacters()
	{
		Random random = new Random(SEED);
		for (int run = 0; run < RUNS; run++)
		{
			String html = generateHtml(random, 0);
			int maxLength = 1 + random.nextInt(15);
			String result = StringUtil.abbreviateHtmlString(html, maxLength, false, null);
			int ellipsis = result.lastIndexOf("...");
			if (ellipsis >= 0)
			{
				assertEquals(maxLength, countVisible(result.substring(0, ellipsis)), html);
			}
		}
	}

	@Test
	public void resultHasBalancedTagsInBothModes()
	{
		Random random = new Random(SEED);
		for (int run = 0; run < RUNS; run++)
		{
			String html = generateHtml(random, 0);
			int maxLength = 1 + random.nextInt(15);
			assertTrue(hasBalancedTags(StringUtil.abbreviateHtmlString(html, maxLength, false, null)), html);
			assertTrue(hasBalancedTags(StringUtil.abbreviateHtmlString(html, maxLength, true, SPACE)), html);
		}
	}

	@Test
	public void intelligentResultDoesNotEndWithDelimiter()
	{
		Random random = new Random(SEED);
		for (int run = 0; run < RUNS; run++)
		{
			String html = generateHtml(random, 0);
			String result = StringUtil.abbreviateHtmlString(html, 1 + random.nextInt(15), true, SPACE);
			assertFalse(result.contains(" ..."), html);
		}
	}

	@Test
	public void brokenHtmlCausesNoException()
	{
		Random random = new Random(SEED);
		char[][] delimiters = { SPACE, { '&', ';' }, { '<', '>' }, { '\0' }, {} };
		for (int run = 0; run < RUNS; run++)
		{
			StringBuilder html = new StringBuilder();
			int parts = 1 + random.nextInt(20);
			for (int i = 0; i < parts; i++)
			{
				html.append(BROKEN[random.nextInt(BROKEN.length)]);
			}
			int maxLength = random.nextInt(23) - 2;
			assertNotNull(StringUtil.abbreviateHtmlString(html.toString(), maxLength, false, null));
			assertNotNull(StringUtil.abbreviateHtmlString(html.toString(), maxLength, true,
				delimiters[random.nextInt(delimiters.length)]));
		}
	}

	@Test
	public void manyClosingTagsWithoutOpenTagAreFast()
	{
		String html = "<a>".repeat(200000) + "</x>".repeat(200000) + "z".repeat(400000);
		assertTimeoutPreemptively(Duration.ofSeconds(10),
			() -> StringUtil.abbreviateHtmlString(html, 300000, false, null));
	}

	@Test
	public void manyLessThanSignsWithoutTagEndAreFast()
	{
		String html = "<a ".repeat(1000000);
		assertTimeoutPreemptively(Duration.ofSeconds(10), () ->
		{
			assertEquals("<a <...", StringUtil.abbreviateHtmlString(html, 4, false, null));
			assertEquals("<a...", StringUtil.abbreviateHtmlString(html, 4, true, SPACE));
		});
	}

	@Test
	public void manyLessThanSignsBeforeOpenQuoteAreFast()
	{
		String html = "<a ".repeat(400000) + "='>" + "z".repeat(10);
		assertTimeoutPreemptively(Duration.ofSeconds(10),
			() -> assertEquals(html.substring(0, html.length() - 9) + "...",
				StringUtil.abbreviateHtmlString(html, 1, false, null)));
	}

	@Test
	public void closingTagsDeepInOpenTagsAreFast()
	{
		String html = "<b>".repeat(200000) + "<a>".repeat(200000) + "</b>".repeat(200000) + "z".repeat(400000);
		assertTimeoutPreemptively(Duration.ofSeconds(10),
			() -> StringUtil.abbreviateHtmlString(html, 300000, true, SPACE));
	}

	private static String generateHtml(Random random, int depth)
	{
		StringBuilder html = new StringBuilder();
		int parts = 1 + random.nextInt(3);
		for (int i = 0; i < parts; i++)
		{
			if (depth > 3 || random.nextInt(3) == 0)
			{
				int words = 1 + random.nextInt(5);
				for (int j = 0; j < words; j++)
				{
					html.append(TEXT[random.nextInt(TEXT.length)]);
				}
			}
			else
			{
				String tag = random.nextBoolean() ? "b" : "i";
				html.append('<').append(tag).append('>').append(generateHtml(random, depth + 1));
				html.append("</").append(tag).append('>');
			}
		}
		return html.toString();
	}

	private static int countVisible(String html)
	{
		String text = ENTITY.matcher(TAG.matcher(html).replaceAll("")).replaceAll("E");
		int count = 0;
		Matcher grapheme = Pattern.compile("\\X").matcher(text);
		while (grapheme.find())
		{
			count++;
		}
		return count;
	}

	private static boolean hasBalancedTags(String html)
	{
		Deque<String> open = new ArrayDeque<>();
		Matcher tag = TAG.matcher(html);
		while (tag.find())
		{
			if (tag.group(1).isEmpty())
			{
				open.push(tag.group(2));
			}
			else if (open.isEmpty() || !open.pop().equals(tag.group(2)))
			{
				return false;
			}
		}
		return open.isEmpty();
	}
}
