/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Shortens an HTML fragment to a number of visible characters, used by
 * {@link StringUtil#abbreviateHtmlString(String, int, boolean, char[])}
 *
 * @author Zdenek
 * @since 2026-11-03
 */
final class HtmlAbbreviator
{
	private static final Pattern HTML_ENTITY = Pattern.compile("&(#\\d+|#[xX]\\p{XDigit}+|\\p{Alpha}\\p{Alnum}*);");
	private static final Pattern GRAPHEME = Pattern.compile("\\X");
	private static final Pattern SCRIPT_END = Pattern.compile("</script[\\s/>]", Pattern.CASE_INSENSITIVE);
	private static final Pattern STYLE_END = Pattern.compile("</style[\\s/>]", Pattern.CASE_INSENSITIVE);
	// Number of visible characters before the cut that are searched for a word delimiter
	private static final int WORD_CUT_WINDOW = 10;

	private HtmlAbbreviator()
	{
		throw new IllegalStateException("Utility class.");
	}

	/**
	 * See {@link StringUtil#abbreviateHtmlString(String, int, boolean, char[])}
	 *
	 * @param str            HTML fragment, can be {@code null}
	 * @param maxLength      maximum number of visible characters
	 * @param intelligent    {@code true} to cut at a word delimiter
	 * @param wordDelimeters characters that end a word, must not be {@code null} when {@code intelligent}
	 * @return the shortened fragment, {@code null} for {@code null}
	 */
	static String abbreviate(String str, int maxLength, boolean intelligent, char[] wordDelimeters)
	{
		if (str == null)
		{
			return str;
		}
		if (intelligent)
		{
			Objects.requireNonNull(wordDelimeters, "wordDelimeters");
		}

		str = replaceLineBreaks(str);

		if (str.length() <= maxLength)
		{
			return str;
		}

		return cut(str, maxLength, intelligent ? wordDelimeters : null);
	}

	// Line breaks in text become spaces, tags and script content stay as they are
	private static String replaceLineBreaks(String str)
	{
		StringBuilder buffer = new StringBuilder(str.length());
		int lastTagClose = str.lastIndexOf('>');
		int i = 0;
		while (i < str.length())
		{
			int markupEnd = findMarkupEnd(str, i, lastTagClose);
			if (markupEnd > i)
			{
				String markup = str.substring(i, markupEnd);
				boolean lineBreak = "<br>".equals(markup) || "<br/>".equals(markup) || "<br />".equals(markup);
				buffer.append(lineBreak ? " " : markup);
				i = markupEnd;
			}
			else
			{
				buffer.append(str.charAt(i) == '\n' ? ' ' : str.charAt(i));
				i++;
			}
		}
		return buffer.toString();
	}

	// With wordDelimeters, the text is cut again at the word end found before maxLength
	private static String cut(String str, int maxLength, char[] wordDelimeters)
	{
		int sz = str.length();
		StringBuilder buffer = new StringBuilder(sz);
		BitSet delimiters = new BitSet();
		OpenTags openTags = new OpenTags();
		Matcher grapheme = GRAPHEME.matcher(str);
		int lastTagClose = str.lastIndexOf('>');
		int count = 0;
		boolean chopped = false;
		int i = 0;
		while (i < sz)
		{
			if (count >= maxLength)
			{
				if (!hasVisibleText(str, i, lastTagClose))
				{
					buffer.append(str, i, sz);
					i = sz;
					break;
				}
				if (wordDelimeters != null)
				{
					return cut(str, findWordCut(delimiters, count), null);
				}
				chopped = true;
				break;
			}

			int markupEnd = findMarkupEnd(str, i, lastTagClose);
			if (markupEnd > i)
			{
				trackTag(str, i, openTags);
				buffer.append(str, i, markupEnd);
				i = markupEnd;
				continue;
			}

			int charEnd = findVisibleCharEnd(str, i, grapheme);
			if (wordDelimeters != null && isDelimiter(str.substring(i, charEnd), wordDelimeters))
			{
				delimiters.set(count);
			}
			count++;
			buffer.append(str, i, charEnd);
			i = charEnd;
		}

		if (chopped)
		{
			buffer.append("...");
		}

		if (!openTags.isEmpty())
		{
			openTags.appendClosingTags(buffer, findClosingTags(str, i, lastTagClose));
		}

		return buffer.toString();
	}

	// True when something other than tags, comments, script content and white space follows
	private static boolean hasVisibleText(String str, int start, int lastTagClose)
	{
		int i = start;
		while (i < str.length())
		{
			int markupEnd = findMarkupEnd(str, i, lastTagClose);
			if (markupEnd > i)
			{
				i = markupEnd;
			}
			else if (Character.isWhitespace(str.charAt(i)))
			{
				i++;
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	// Lower-case names of the closing tags from start to the end of the text
	private static Set<String> findClosingTags(String str, int start, int lastTagClose)
	{
		Set<String> tags = new HashSet<>();
		int i = start;
		while (i < str.length())
		{
			int markupEnd = findMarkupEnd(str, i, lastTagClose);
			if (markupEnd == i)
			{
				i++;
				continue;
			}
			if (str.startsWith("</", i))
			{
				tags.add(readTagName(str, i + 2).toLowerCase(Locale.ROOT));
			}
			i = markupEnd;
		}
		return tags;
	}

	// End of the tag or comment at start, with the content of script and style, otherwise start.
	// A tag needs a '>', and lastTagClose avoids a search to the end of the text for every '<'
	private static int findMarkupEnd(String str, int start, int lastTagClose)
	{
		if (!isTagStart(str, start))
		{
			return start;
		}
		if (str.startsWith("<!--", start))
		{
			int commentEnd = str.indexOf("-->", start + 2);
			return commentEnd < 0 ? str.length() : commentEnd + 3;
		}

		if (start > lastTagClose)
		{
			return start;
		}

		int tagEnd = findTagEnd(str, start);
		String name = readTagName(str, start + 1);
		Pattern contentEnd = null;
		if ("script".equalsIgnoreCase(name))
		{
			contentEnd = SCRIPT_END;
		}
		else if ("style".equalsIgnoreCase(name))
		{
			contentEnd = STYLE_END;
		}
		if (contentEnd == null)
		{
			return tagEnd + 1;
		}

		Matcher match = contentEnd.matcher(str).region(tagEnd + 1, str.length());
		return match.find() ? match.start() : str.length();
	}

	// A '<' that is not followed by a tag name, '/', '!' or '?' is plain text, as in "1 < 2"
	private static boolean isTagStart(String str, int index)
	{
		if (str.charAt(index) != '<' || index + 1 >= str.length())
		{
			return false;
		}
		char next = str.charAt(index + 1);
		return next == '/' || next == '!' || next == '?' || next < 128 && Character.isLetter(next);
	}

	// Index of the '>' of the tag at start, which must exist. When a quote is not closed, the
	// first '>' after it ends the tag, so that the text after the tag is not lost
	private static int findTagEnd(String str, int start)
	{
		char quote = 0;
		char previous = 0;
		int quoteStart = start;
		for (int i = start + 1; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if (quote != 0)
			{
				quote = c == quote ? 0 : quote;
			}
			else if (c == '>')
			{
				return i;
			}
			else if ((c == '"' || c == '\'') && previous == '=')
			{
				quote = c;
				quoteStart = i;
			}
			if (!Character.isWhitespace(c))
			{
				previous = c;
			}
		}
		return str.indexOf('>', quoteStart);
	}

	private static String readTagName(String str, int start)
	{
		int end = start;
		while (end < str.length() && !Character.isWhitespace(str.charAt(end)) && str.charAt(end) != '/'
			&& str.charAt(end) != '>')
		{
			end++;
		}
		return str.substring(start, end);
	}

	private static void trackTag(String str, int start, OpenTags openTags)
	{
		char first = str.charAt(start + 1);
		if (first == '!' || first == '?')
		{
			return;
		}

		if (first == '/')
		{
			openTags.removeLast(readTagName(str, start + 2));
			return;
		}

		String name = readTagName(str, start + 1);
		if (str.charAt(start + 1 + name.length()) != '/')
		{
			openTags.add(name);
		}
	}

	// End of the entity or grapheme at start, ASCII text does not need the grapheme search
	private static int findVisibleCharEnd(String str, int start, Matcher grapheme)
	{
		char c = str.charAt(start);
		if (c == '&')
		{
			Matcher match = HTML_ENTITY.matcher(str).region(start, str.length());
			if (match.lookingAt())
			{
				return match.end();
			}
		}

		boolean nextIsAscii = start + 1 >= str.length() || str.charAt(start + 1) < 128;
		if (c < 128 && nextIsAscii)
		{
			return start + 1;
		}
		grapheme.region(start, str.length()).lookingAt();
		return grapheme.end();
	}

	// An entity is compared as the character that it stands for
	private static boolean isDelimiter(String visibleChar, char[] wordDelimeters)
	{
		String text;
		try
		{
			text = visibleChar.length() > 1 ? StringEscapeUtils.unescapeHtml4(visibleChar) : visibleChar;
		}
		catch (IllegalArgumentException e)
		{
			// A numeric entity above U+10FFFF stands for no character
			return false;
		}
		if (text.length() != 1)
		{
			return false;
		}
		for (char delimiter : wordDelimeters)
		{
			if (delimiter == text.charAt(0))
			{
				return true;
			}
		}
		return false;
	}

	// Number of visible characters to keep, the kept text never ends with a delimiter
	private static int findWordCut(BitSet delimiters, int count)
	{
		int windowStart = Math.max(count - WORD_CUT_WINDOW, 0);
		int cut = Math.max(delimiters.previousSetBit(count - 1), windowStart);
		return delimiters.previousClearBit(cut - 1) + 1;
	}

	// Removing a tag does not search the list, so that a long text stays fast
	private static final class OpenTags
	{
		private final List<String> tags = new ArrayList<>(5);
		private final Map<String, Deque<Integer>> indexes = new HashMap<>();

		private boolean isEmpty()
		{
			return tags.isEmpty();
		}

		private void add(String tag)
		{
			indexes.computeIfAbsent(tag.toLowerCase(Locale.ROOT), key -> new ArrayDeque<>()).push(tags.size());
			tags.add(tag);
		}

		// Tag names in HTML are case-insensitive, and the last open tag is the inner one
		private void removeLast(String tag)
		{
			Deque<Integer> tagIndexes = indexes.get(tag.toLowerCase(Locale.ROOT));
			if (tagIndexes != null && !tagIndexes.isEmpty())
			{
				tags.set(tagIndexes.pop(), null);
			}
		}

		private void appendClosingTags(StringBuilder buffer, Set<String> closingTags)
		{
			for (int i = tags.size() - 1; i >= 0; i--)
			{
				String tag = tags.get(i);
				if (tag != null && closingTags.contains(tag.toLowerCase(Locale.ROOT)))
				{
					buffer.append("</").append(tag).append('>');
				}
			}
		}
	}
}
