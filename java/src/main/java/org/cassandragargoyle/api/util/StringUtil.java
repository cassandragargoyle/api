/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import java.io.StringWriter;
import java.text.BreakIterator;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.WordUtils;

/**
 * Small static helpers for strings: safe conversions with a default value, case-insensitive
 * checks, HTML and XML escaping, lists stored in one line of text, hashing, and text wrapping
 *
 * <p>
 * Most methods do not accept {@code null} for the main string argument and throw
 * {@link NullPointerException}; the methods that accept {@code null} say so. Case-insensitive
 * methods use {@link String#equalsIgnoreCase(String)} or {@link String#toLowerCase()} with the
 * default locale
 *
 * <p>
 * The class has only static methods and keeps no state, so it is thread-safe
 *
 * @author Zdenek
 * @since 2026-03-19
 */
@SuppressWarnings("java:S1192")
public class StringUtil
{
	/** Lower-case hexadecimal digits, indexed by the value of one nibble (0 to 15) */
	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	/** Prevents instances, the class has only static methods */
	private StringUtil()
	{
		throw new IllegalStateException("Utility class.");
	}

	/**
	 * Writes bytes as lower-case hexadecimal text, two digits per byte
	 *
	 * <p>
	 * Every byte is followed by {@code cDelim}, also the last one. When {@code nLineLen} is
	 * greater than 0, a new line follows every {@code nLineLen} bytes and one more
	 * {@code cDelim} follows every 8 bytes inside a line, like the output of a hex editor. For
	 * example {@code {0xCA, 0xFE}} with {@code ' '} and {@code 0} gives {@code "ca fe "}
	 *
	 * @param bytes    bytes to write, must not be {@code null}
	 * @param cDelim   character after every byte
	 * @param nLineLen bytes per line, 0 or less for one line
	 * @return the hexadecimal text
	 */
	public static String bytesToHexString(byte[] bytes, char cDelim, int nLineLen)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		int len = 0;

		for (int b : bytes)
		{
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(cDelim);

			if (nLineLen > 0)
			{
				len++;
				if (len >= nLineLen)
				{
					sb.append('\n');
					len = 0;
				}
				else if (0 == (len % 8))
				{
					sb.append(cDelim);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Tests if a string starts with a prefix, ignoring case
	 *
	 * @param str    string to test, must not be {@code null}
	 * @param prefix prefix to look for, must not be {@code null}
	 * @return {@code true} if {@code str} starts with {@code prefix} in any case
	 * @throws NullPointerException if {@code str} or {@code prefix} is {@code null}
	 */
	public static boolean startsIgnoreCase(String str, String prefix)
	{
		return str.length() >= prefix.length() && prefix.equalsIgnoreCase(str.substring(0, prefix.length()));
	}

	/**
	 * Tests if a string contains a substring, ignoring case
	 *
	 * <p>
	 * Both strings are converted with {@link String#toLowerCase()} in the default locale
	 *
	 * @param str string to search in, must not be {@code null}
	 * @param sub substring to look for, must not be {@code null}
	 * @return {@code true} if {@code str} contains {@code sub} in any case
	 * @throws NullPointerException if {@code str} or {@code sub} is {@code null}
	 */
	public static boolean containsIgnoreCase(String str, String sub)
	{
		return str.length() >= sub.length() && str.toLowerCase().contains(sub.toLowerCase());
	}

	/**
	 * Tests if a string ends with a suffix, ignoring case
	 *
	 * @param str    string to test, can be {@code null}
	 * @param suffix suffix to look for, must not be {@code null}
	 * @return {@code true} if {@code str} ends with {@code suffix} in any case,
	 *         {@code false} if {@code str} is {@code null}
	 */
	public static boolean endsIgnoreCase(String str, String suffix)
	{
		return str != null
			&& str.length() >= suffix.length()
			&& suffix.equalsIgnoreCase(str.substring(str.length() - suffix.length(), str.length()));
	}

	/**
	 * Appends a value to HTML text, escaped unless it is already HTML
	 *
	 * <p>
	 * A value that starts with {@code <html>} (any case) is appended without the
	 * {@code <html>} and {@code </html>} tags and without escaping. Any other value is appended
	 * with {@link #appendHtmlEscaped(StringBuilder, String)}
	 *
	 * @param sb    builder to append to
	 * @param value plain text or an HTML fragment, must not be {@code null}
	 */
	public static void appendToHtml(StringBuilder sb, String value)
	{
		if (startsIgnoreCase(value, "<html>"))
		{
			if (endsIgnoreCase(value, "</html>"))
			{
				sb.append(value.substring(6, value.length() - 7));
			}
			else
			{
				sb.append(value.substring(6));
			}
		}
		else
		{
			appendHtmlEscaped(sb, value);
		}
	}

	/**
	 * Appends plain text to HTML text with {@code <}, {@code >}, and {@code &} escaped
	 *
	 * <p>
	 * A new line becomes {@code <br>} followed by a new line. Quotes are not escaped, so the
	 * result is safe in element content but not inside an attribute value
	 *
	 * @param sb    builder to append to
	 * @param value plain text, must not be {@code null}
	 */
	public static void appendHtmlEscaped(StringBuilder sb, String value)
	{
		int length = value.length();
		char c;

		for (int i = 0; i < length; i++)
		{
			c = value.charAt(i);
			switch (c)
			{
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '\n':
					sb.append("<br>\n");
					break;
				default:
					sb.append(c);
					break;
			}
		}
	}

	/**
	 * Appends plain text to HTML text, escaped and wrapped to lines of a maximum length
	 *
	 * @param sb            builder to append to
	 * @param value         plain text, must not be {@code null}
	 * @param maxLineLength maximum number of characters of the text in one line
	 * @see #appendHtmlEscaped(StringBuilder, String, int, int)
	 */
	public static void appendHtmlEscaped(StringBuilder sb, String value, int maxLineLength)
	{
		appendHtmlEscaped(sb, value, maxLineLength, Integer.MAX_VALUE);
	}

	/**
	 * Appends plain text to HTML text, escaped, wrapped, and cut after a number of lines
	 *
	 * <p>
	 * Escaping is the same as in {@link #appendHtmlEscaped(StringBuilder, String)}. When a line
	 * reaches {@code maxLineLength} characters, a {@code <br>} is inserted after the last space
	 * of the line, or at the current position when the line has no space. When the text has
	 * more than {@code maxLines} lines, the rest is replaced with {@code "..."}. The length
	 * counts the characters of the text, not of the escape sequences
	 *
	 * @param sb            builder to append to
	 * @param value         plain text, must not be {@code null}
	 * @param maxLineLength maximum number of characters of the text in one line
	 * @param maxLines      maximum number of lines
	 */
	public static void appendHtmlEscaped(StringBuilder sb, String value, int maxLineLength, int maxLines)
	{
		int length = value.length();
		int lineLength = 0;
		int lastSpaceI = -1;
		int lastSpace = -1;
		int lines = 0;
		int i;
		char c;

		for (i = 0; i < length && lines < maxLines; i++)
		{
			lineLength++;
			c = value.charAt(i);
			switch (c)
			{
				case '<':
					sb.append("&lt;");
					break;

				case '>':
					sb.append("&gt;");
					break;

				case '&':
					sb.append("&amp;");
					break;

				case '\n':
					sb.append("<br>\n");
					lineLength = 0;
					lastSpace = -1;
					lines++;
					break;

				case ' ':
					lastSpace = sb.length() + 1;
					lastSpaceI = i;
					sb.append(c);
					break;

				default:
					sb.append(c);
					break;
			}

			if (lineLength >= maxLineLength)
			{
				if (lastSpace > 0)
				{
					sb.insert(lastSpace, "<br>\n");
					lineLength = i - lastSpaceI;
					lastSpace = -1;
				}
				else
				{
					sb.append("<br>\n");
					lineLength = 0;
					lastSpace = -1;
				}
				lines++;
			}
		}
		if (i < length)
		{
			sb.append("...");
		}
	}

	/**
	 * Parses an {@code int}, or returns a default value when the text is not a number
	 *
	 * <p>
	 * The text is not trimmed, so {@code " 7"} gives the default value
	 *
	 * @param value text to parse, can be {@code null}
	 * @param dflt  value to return when {@code value} is {@code null} or not an {@code int}
	 * @return the parsed number or {@code dflt}
	 */
	public static int toInt(final String value, int dflt)
	{
		if (value != null)
		{
			try
			{
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				// will return default
			}
		}
		return dflt;
	}

	/**
	 * Parses a {@code long}, or returns a default value when the text is not a number
	 *
	 * <p>
	 * The text is not trimmed
	 *
	 * @param value text to parse, can be {@code null}
	 * @param dflt  value to return when {@code value} is {@code null} or not a {@code long}
	 * @return the parsed number or {@code dflt}
	 */
	public static long toLong(final String value, long dflt)
	{
		if (value != null)
		{
			try
			{
				return Long.parseLong(value);
			}
			catch (NumberFormatException e)
			{
				// will return default
			}
		}
		return dflt;
	}

	/**
	 * Parses a {@code double}, or returns a default value when the text is not a number
	 *
	 * <p>
	 * Uses {@link Double#parseDouble(String)}, so leading and trailing spaces are allowed, the
	 * decimal separator is always a dot, and {@code "1.5e3"} is accepted
	 *
	 * @param value text to parse, can be {@code null}
	 * @param dflt  value to return when {@code value} is {@code null} or not a number
	 * @return the parsed number or {@code dflt}
	 */
	public static double toDouble(final String value, double dflt)
	{
		if (value != null)
		{
			try
			{
				return Double.parseDouble(value);
			}
			catch (NumberFormatException e)
			{
				// will return default
			}
		}
		return dflt;
	}

	/**
	 * Parses a boolean, {@code false} for {@code null} and empty text
	 *
	 * @param value text to parse, can be {@code null}
	 * @return {@code true} only for {@code "true"} in any case
	 * @see #toBoolean(String, boolean)
	 */
	public static boolean toBoolean(final String value)
	{
		return toBoolean(value, false);
	}

	/**
	 * Parses a boolean, or returns a default value for {@code null} and empty text
	 *
	 * <p>
	 * Uses {@link Boolean#parseBoolean(String)}: only {@code "true"} in any case is
	 * {@code true}, any other non-empty text, for example {@code "yes"}, is {@code false}
	 *
	 * @param value text to parse, can be {@code null}
	 * @param dflt  value to return when {@code value} is {@code null} or empty
	 * @return the parsed value or {@code dflt}
	 */
	public static boolean toBoolean(final String value, boolean dflt)
	{
		try
		{
			return isNullOrEmpty(value) ? dflt : Boolean.parseBoolean(value);
		}
		catch (Exception e)
		{
			return dflt;
		}
	}

	/**
	 * Tests if a string is {@code null} or empty
	 *
	 * <p>
	 * A string with only spaces is not empty
	 *
	 * @param value string to test, can be {@code null}
	 * @return {@code true} for {@code null} and {@code ""}
	 */
	public static boolean isNullOrEmpty(final String value)
	{
		return value == null || value.isEmpty();
	}

	/**
	 * Tests if a string has at least one character, the opposite of {@link #isNullOrEmpty(String)}
	 *
	 * @param value string to test, can be {@code null}
	 * @return {@code false} for {@code null} and {@code ""}
	 */
	public static boolean notEmpty(final String value)
	{
		return value != null && value.length() > 0;
	}

	/**
	 * Tests if a string is {@code null}, empty or has only white space
	 *
	 * <p>
	 * Unlike {@link #isNullOrEmpty(String)}, {@code "   "} is blank
	 *
	 * @param value string to test, can be {@code null}
	 * @return {@code true} for {@code null}, {@code ""} and white space only
	 * @since 1.2.0
	 */
	public static boolean isNullOrBlank(final String value)
	{
		return value == null || value.isBlank();
	}

	/**
	 * Tests if a string has at least one character that is not white space, the opposite of
	 * {@link #isNullOrBlank(String)}
	 *
	 * @param value string to test, can be {@code null}
	 * @return {@code false} for {@code null}, {@code ""} and white space only
	 * @since 1.2.0
	 */
	public static boolean notBlank(final String value)
	{
		return !isNullOrBlank(value);
	}

	/**
	 * Returns the string, or an empty string instead of {@code null}
	 *
	 * @param value string, can be {@code null}
	 * @return {@code value}, or {@code ""} when it is {@code null}
	 */
	public static String safeString(final String value)
	{
		return value == null ? "" : value;
	}

	/**
	 * Tests if all characters of a string are ASCII (code 0 to 127)
	 *
	 * @param input string to test, must not be {@code null}
	 * @return {@code true} if no character is above 127; {@code true} for an empty string
	 */
	public static boolean isAllASCII(final String input)
	{
		boolean isASCII = true;
		for (int i = 0; i < input.length(); i++)
		{
			int c = input.charAt(i);
			if (c > 0x7F)
			{
				isASCII = false;
				break;
			}
		}
		return isASCII;
	}

	/**
	 * Tests if all characters of a string are the ASCII letters {@code a-z} or {@code A-Z}
	 *
	 * @param input string to test, must not be {@code null}
	 * @return {@code true} if there are only ASCII letters; {@code true} for an empty string
	 */
	public static boolean isAllASCIILetters(final String input)
	{
		boolean isASCII = true;
		for (int i = 0; i < input.length(); i++)
		{
			int c = input.charAt(i);
			if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
			{
				isASCII = false;
				break;
			}
		}
		return isASCII;
	}

	/**
	 * Returns the first argument that is not {@code null} and not empty
	 *
	 * <p>
	 * A string with only spaces counts as not empty
	 *
	 * @param s strings to check in order, items can be {@code null}
	 * @return the first non-empty string, or {@code ""} when there is none
	 */
	public static String firstNotEmpty(String... s)
	{
		for (String str : s)
		{
			if (str != null && !str.isEmpty())
			{
				return str;
			}
		}
		return "";
	}

	/**
	 * Joins strings into one line with {@code ';'}, so that {@link #fromString(String)} can split
	 * them again
	 *
	 * @param parts strings to join, items must not be {@code null}
	 * @return the joined text
	 * @see #toString(Collection, char)
	 */
	public static String toString(final Collection<String> parts)
	{
		return toString(parts, ';');
	}

	/**
	 * Joins strings into one line with a separator, so that {@link #fromString(String, char)}
	 * can split them again
	 *
	 * <p>
	 * The backslash is the escape character: a separator or a backslash inside a part gets a
	 * backslash before it. Every part ends with the separator, also the last one. For example
	 * {@code ["a;b", "c"]} gives {@code a\;b;c;}, and an empty collection gives {@code ""}
	 *
	 * @param parts strings to join, items must not be {@code null}
	 * @param sep   separator character
	 * @return the joined text
	 */
	public static String toString(final Collection<String> parts, final char sep)
	{
		StringBuilder sb = new StringBuilder();

		for (String part : parts)
		{
			int length = part.length();
			for (int i = 0; i < length; i++)
			{
				char c = part.charAt(i);
				if (c == '\\' || c == sep)
				{
					sb.append("\\");
				}
				sb.append(c);
			}
			sb.append(sep);
		}
		return sb.toString();
	}

	/**
	 * Splits text made by {@link #toString(Collection)} back into strings, with the separator
	 * {@code ';'}
	 *
	 * @param value joined text, must not be {@code null}
	 * @return the parts
	 * @see #fromString(String, char)
	 */
	public static List<String> fromString(final String value)
	{
		return fromString(value, ';');
	}

	/**
	 * Splits text made by {@link #toString(Collection, char)} back into strings
	 *
	 * <p>
	 * A backslash makes the next character part of the data. A part is added only when its
	 * separator is found, so text after the last separator is lost: {@code "a;b"} gives only
	 * {@code ["a"]}. Text made by {@link #toString(Collection, char)} always ends with the
	 * separator, so it is read back completely
	 *
	 * @param value joined text, must not be {@code null}
	 * @param sep   separator character
	 * @return the parts, a new modifiable list
	 */
	public static List<String> fromString(final String value, final char sep)
	{
		List<String> parts = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		int length = value.length();
		boolean forceNext = false;
		for (int i = 0; i < length; i++)
		{
			char c = value.charAt(i);
			if (forceNext)
			{
				sb.append(c);
				forceNext = false;
			}
			else if (c == '\\')
			{
				forceNext = true;
			}
			else if (c == sep)
			{
				parts.add(sb.toString());
				sb.setLength(0);
			}
			else
			{
				sb.append(c);
			}
		}
		return parts;
	}

	/**
	 * Returns one part of text made by {@link #toString(Collection, char)}, without splitting the
	 * whole text
	 *
	 * <p>
	 * Escaped characters are handled as in {@link #fromString(String, char)}. Note: the part
	 * counter is never increased, so only part 0 is found, and any other {@code partNr} gives
	 * {@code null}
	 *
	 * @param value  joined text, must not be {@code null}
	 * @param partNr index of the part, starting with 0
	 * @param sep    separator character
	 * @return the part, or {@code null} when it is not found
	 */
	public static String stringPart(final String value, final int partNr, final char sep)
	{
		StringBuilder sb = new StringBuilder();
		boolean forceNext = false;
		int length = value.length();
		int readingNr = 0;
		for (int i = 0; i < length; i++)
		{
			char c = value.charAt(i);
			if (forceNext)
			{
				if (readingNr == partNr)
				{
					sb.append(c);
				}
				forceNext = false;
			}
			else if (c == '\\')
			{
				forceNext = true;
			}
			else if (c == sep)
			{
				if (readingNr == partNr)
				{
					return sb.toString();
				}
			}
			else if (readingNr == partNr)
			{
				sb.append(c);
			}

		}
		return readingNr == partNr ? sb.toString() : null;
	}

	/**
	 * Returns the value, or a default value when the value is {@code null} or empty
	 *
	 * @param value        value, can be {@code null}
	 * @param defaultValue value to return instead of {@code null} or {@code ""}, can be {@code null}
	 * @return {@code value} or {@code defaultValue}
	 */
	public static String valueOrDefault(String value, String defaultValue)
	{
		if (value != null && !value.isEmpty())
		{
			return value;
		}
		return defaultValue;
	}

	/**
	 * Finds the first character that is not whitespace, starting at a position
	 *
	 * @param value string to search, must not be {@code null}
	 * @param pos   index to start at; a value at or after the end gives 0
	 * @return the character, or the character with code 0 when there is only whitespace
	 */
	public static char firstNonWhiteChar(String value, int pos)
	{
		int len = value.length();
		while (pos < len)
		{
			char c = value.charAt(pos);
			if (!Character.isWhitespace(c))
			{
				return c;
			}
			pos++;
		}
		return 0;
	}

	/**
	 * Makes a short label from a longer text, for example a title from a description
	 *
	 * <p>
	 * The text is trimmed. Text shorter than {@code maxLength} is returned as it is. Longer text
	 * is cut to its first sentence (found with {@link BreakIterator} for the default locale),
	 * then before the first {@code . ! ? : ;} or new line, and finally at a space between
	 * {@code maxLength - 5} and {@code maxLength + 5} characters, without an ellipsis. So the
	 * label can be up to 5 characters longer than {@code maxLength}. For example
	 * {@code "Title: some longer description"} gives {@code "Title"}
	 *
	 * @param content   text to shorten, can be {@code null}
	 * @param maxLength wanted maximum length, should be greater than 5
	 * @return the label, {@code ""} for {@code null} or empty text
	 */
	public static String generateLabel(String content, int maxLength)
	{
		if (StringUtil.isNullOrEmpty(content))
		{
			return "";
		}
		content = content.trim();
		if (content.length() < maxLength)
		{
			return content;
		}

		BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.getDefault());
		boundary.setText(content);
		content = content.substring(boundary.first(), boundary.next());
		Matcher match = Pattern.compile("^(.*?)[.!?:;\\n]").matcher(content);
		if (match.find())
		{
			String sentence = match.group(0).replace("\n", "");
			if (!sentence.isEmpty() && ".!?:;".indexOf(sentence.charAt(sentence.length() - 1)) >= 0)
			{
				sentence = sentence.substring(0, sentence.length() - 1);
			}
			content = sentence;
		}
		return WordUtils.abbreviate(content, maxLength - 5, maxLength + 5, "");
	}

	/**
	 * Removes a suffix from a string if the string ends with it (case-sensitive)
	 *
	 * @param value  string, must not be {@code null}
	 * @param suffix suffix to remove, must not be {@code null}
	 * @return {@code value} without the suffix, or {@code value} unchanged
	 */
	public static String removeSuffix(String value, String suffix)
	{
		if (value.endsWith(suffix))
		{
			return value.substring(0, value.length() - suffix.length());
		}
		return value;
	}

	/**
	 * Removes a prefix from a string if the string starts with it (case-sensitive)
	 *
	 * @param value  string, must not be {@code null}
	 * @param prefix prefix to remove, must not be {@code null}
	 * @return {@code value} without the prefix, or {@code value} unchanged
	 */
	public static String removePrefix(String value, String prefix)
	{
		if (value.startsWith(prefix))
		{
			return value.substring(prefix.length());
		}
		return value;
	}

	/**
	 * Tests if the content of a {@link StringBuilder} ends with a suffix (case-sensitive)
	 *
	 * @param sb     builder to test, must not be {@code null}
	 * @param suffix suffix to look for, must not be {@code null}
	 * @return {@code true} if {@code sb} ends with {@code suffix}
	 */
	public static boolean endsWith(StringBuilder sb, String suffix)
	{
		return sb.length() >= suffix.length() && suffix.equals(sb.substring(sb.length() - suffix.length()));
	}

	/**
	 * Escapes text for XML element content and attribute values in double quotes
	 *
	 * <p>
	 * {@code & < > "} become entities, a new line becomes {@code &#10;}, and every character
	 * above 127 becomes a decimal character reference, for example {@code č} becomes
	 * {@code &#269;}. The apostrophe, tab, and carriage return are not changed. A character
	 * outside the Basic Multilingual Plane, for example an emoji, is written as two references
	 * to its surrogate {@code char} values, which XML parsers reject
	 *
	 * @param value text to escape, must not be {@code null}
	 * @return the escaped text, ASCII only
	 */
	public static String escapeForXml(String value)
	{
		StringWriter writer = new StringWriter((int) (value.length() * 1.1));

		int len = value.length();
		for (int i = 0; i < len; i++)
		{
			char c = value.charAt(i);
			String entityName = null;
			switch (c)
			{
				case '&':
					entityName = "amp";
					break;
				case '<':
					entityName = "lt";
					break;
				case '>':
					entityName = "gt";
					break;
				case '"':
					entityName = "quot";
					break;
				case '\n':
					entityName = "#10";
					break;
				default:
					break;
			}
			if (entityName == null)
			{
				if (c > 0x7F)
				{
					writer.write("&#");
					writer.write(Integer.toString(c, 10));
					writer.write(';');
				}
				else
				{
					writer.write(c);
				}
			}
			else
			{
				writer.write('&');
				writer.write(entityName);
				writer.write(';');
			}
		}

		return writer.toString();
	}

	/**
	 * Returns the smaller of two strings by {@link String#compareTo(String)}
	 *
	 * <p>
	 * The comparison is by character codes, so upper-case letters come before lower-case
	 * letters: {@code min("a", "B")} is {@code "B"}
	 *
	 * @param v1 first string, must not be {@code null}
	 * @param v2 second string, must not be {@code null}
	 * @return the smaller string, {@code v1} when they are equal
	 */
	public static String min(String v1, String v2)
	{
		return v1.compareTo(v2) <= 0 ? v1 : v2;
	}

	/**
	 * Trims text and puts it between {@code <html>} and {@code </html>}, unless it already
	 * starts with {@code <html>} (any case)
	 *
	 * <p>
	 * Swing components show such text as HTML. The content is not escaped
	 *
	 * @param text text or HTML fragment, can be {@code null}
	 * @return the wrapped text, {@code null} for {@code null}
	 * @see #unwrapHTMLFragment(String)
	 */
	public static String wrapHTMLFragment(String text)
	{
		if (text != null)
		{
			text = text.trim();
			if (!startsIgnoreCase(text, "<html>"))
			{
				text = "<html>" + text + "</html>";
			}
		}
		return text;
	}

	/**
	 * Trims text and removes a leading {@code <html>} and a trailing {@code </html>} (any case)
	 *
	 * @param text HTML fragment or plain text, can be {@code null}
	 * @return the text without the tags, {@code null} for {@code null}
	 * @see #wrapHTMLFragment(String)
	 */
	public static String unwrapHTMLFragment(String text)
	{
		if (text != null)
		{
			text = text.trim();
			if (startsIgnoreCase(text, "<html>"))
			{
				text = text.substring(6);
			}
			if (endsIgnoreCase(text, "</html>"))
			{
				text = text.substring(0, text.length() - 7);
			}
		}
		return text;
	}

	/**
	 * Compares two strings with a {@link Collator}, with {@code null} before any string
	 *
	 * <p>
	 * A collator compares by the rules of a language, for example in Czech {@code ch} comes
	 * after {@code h}. The name means "safe", the method does not save anything
	 *
	 * @param collator collator to use, must not be {@code null}
	 * @param value1   first string, can be {@code null}
	 * @param value2   second string, can be {@code null}
	 * @return a negative number, zero, or a positive number as {@code value1} is before, equal
	 *         to, or after {@code value2}
	 */
	public static int saveCollatorCompare(Collator collator, String value1, String value2)
	{
		if (value1 == null)
		{
			return value2 == null ? 0 : -1;
		}
		else
		{
			return value2 == null ? 1 : collator.compare(value1, value2);
		}
	}

	/**
	 * Shortens an HTML fragment to a maximum number of visible characters, keeping the tags valid
	 *
	 * <p>
	 * First {@code <br>}, {@code <br/>}, {@code <br />}, and new lines become spaces. Text that
	 * is then not longer than {@code maxLength} is returned. Otherwise only the visible
	 * characters are counted: tags do not count, and an entity such as {@code &amp;} counts as
	 * one character. The text is cut at {@code maxLength} visible characters and {@code "..."}
	 * is appended. Tags that are open at the cut are closed when the rest of the text contains
	 * their closing tag
	 *
	 * <p>
	 * With {@code intelligent}, the cut moves back to the last word delimiter, but at most 9
	 * characters, so a word is not split in the middle when possible
	 *
	 * @param str            HTML fragment, can be {@code null}
	 * @param maxLength      maximum number of visible characters
	 * @param intelligent    {@code true} to cut at a word delimiter
	 * @param wordDelimeters characters that end a word, used only when {@code intelligent}
	 * @return the shortened fragment, {@code null} for {@code null}
	 */
	@SuppressWarnings("java:S3776")
	public static String abbreviateHtmlString(String str, int maxLength, boolean intelligent, char[] wordDelimeters)
	{
		if (str == null)
		{
			return str;
		}

		str = str.replace("<br>", " ");
		str = str.replace("<br/>", " ");
		str = str.replace("<br />", " ");
		str = str.replace("\n", " ");

		if (str.length() <= maxLength)
		{
			return str;
		}

		int sz = str.length();
		StringBuilder buffer = new StringBuilder(sz);

		boolean inTag = false;
		boolean inTagName = false;
		boolean endingTag = false;
		int count = 0;
		boolean chopped = false;
		int entityChars = 0;

		StringBuilder currentTag = new StringBuilder(5);
		List<String> openTags = new ArrayList<>(5);
		int i;
		for (i = 0; i < sz; i++)
		{
			if (count >= maxLength)
			{
				if (intelligent)
				{
					for (int j = i - 1; j > 0 && j > i - 10; j--)
					{
						boolean find = false;
						int c2 = str.charAt(j);
						for (int d : wordDelimeters)
						{
							if (c2 == d)
							{
								find = true;
							}
						}
						if (find)
						{
							break;
						}
						else
						{
							buffer.setLength(buffer.length() - 1);
						}
					}
				}
				chopped = true;
				break;
			}

			char c = str.charAt(i);
			if (c == '<')
			{
				inTag = true;
				inTagName = true;
			}
			else if (inTag)
			{
				if (inTagName && c == '/')
				{

					if (currentTag.length() == 0)
					{
						endingTag = true;
					}
					else
					{
						inTagName = false;
					}
					currentTag.setLength(0);
				}
				else if (inTagName && (c == ' ' || c == '>'))
				{
					inTagName = false;

					if (!endingTag)
					{
						openTags.add(currentTag.toString());
					}
					else
					{
						openTags.remove(currentTag.toString());
					}
					currentTag.setLength(0);
					if (c == '>')
					{
						inTag = false;
					}
				}
				else if (c == '>')
				{
					inTag = false;
				}
				else if (inTagName)
				{
					currentTag.append(c);
				}

			}
			else
			{
				if (c == '&')
				{
					entityChars = 1;
				}
				else if (entityChars == 0)
				{
					count++;
				}
				else
				{
					if (entityChars > 0 && c == ';')
					{
						entityChars = 0;
						count++;
					}
					else
					{
						entityChars++;
					}
					if (entityChars > 5)
					{
						count += entityChars;
						entityChars = 0;
					}
				}
			}

			if (inTag || count < maxLength)
			{
				buffer.append(c);
			}
		}

		if (chopped)
		{
			buffer.append("...");
		}

		if (!openTags.isEmpty())
		{
			String remainingToken = str.substring(i);
			for (int j = openTags.size() - 1; j >= 0; j--)
			{
				String closingTag = "</" + openTags.get(j) + ">";
				if (remainingToken.contains(closingTag))
				{
					buffer.append(closingTag);
				}
			}
		}

		return buffer.toString();
	}

	/**
	 * Finds the index of the n-th occurrence of a substring
	 *
	 * <p>
	 * Occurrences can overlap: the 2nd {@code "aa"} in {@code "aaa"} is at index 1. An
	 * {@code n} of 0 or less works like 1
	 *
	 * @param str    string to search in, must not be {@code null}
	 * @param substr substring to look for, must not be {@code null}
	 * @param n      which occurrence, starting with 1
	 * @return the index, or -1 when there are fewer than {@code n} occurrences
	 */
	public static int ordinalIndexOf(String str, String substr, int n)
	{
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
		{
			pos = str.indexOf(substr, pos + 1);
		}
		return pos;
	}

	/**
	 * Computes a 64-bit hash of a string
	 *
	 * <p>
	 * The formula is the same as in {@link String#hashCode()}, {@code h = 31 * h + c} for every
	 * character, but with a {@code long} and the start value {@code 1125899906842597}. Equal
	 * strings always give equal hashes; different strings can give the same hash, for example
	 * {@code "Aa"} and {@code "BB"}
	 *
	 * @param value string to hash, must not be {@code null}
	 * @return the hash
	 */
	public static long longHash(String value)
	{
		long h = 1125899906842597L;
		int len = value.length();
		for (int i = 0; i < len; i++)
		{
			h = 31 * h + value.charAt(i);
		}
		return h;
	}

	/**
	 * Continues a 64-bit hash with the characters of a string
	 *
	 * <p>
	 * {@code longHash(b, longHash(a))} is the same as {@code longHash(a + b)}, so a hash of
	 * several strings can be computed without joining them
	 *
	 * @param value     string to add to the hash, must not be {@code null}
	 * @param startHash hash computed so far
	 * @return the new hash
	 * @see #longHash(String)
	 */
	public static long longHash(String value, long startHash)
	{
		long h = startHash;
		int len = value.length();
		for (int i = 0; i < len; i++)
		{
			h = 31 * h + value.charAt(i);
		}
		return h;
	}

	/**
	 * Wraps plain text at spaces into lines of about a maximum length
	 *
	 * <p>
	 * Text shorter than {@code maxLineLength} is returned unchanged. Otherwise every existing
	 * line is wrapped between words, and every line of the result ends with a new line, also
	 * the last one. The space between two words is not counted, so a line can be
	 * {@code maxLineLength + 1} characters long, and a word longer than the limit stays in one
	 * line
	 *
	 * @param description text to wrap, must not be {@code null}
	 * @param maxLineLength wanted maximum line length
	 * @return the wrapped text
	 */
	public static String multiLine(String description, int maxLineLength)
	{
		if (description.length() < maxLineLength)
		{
			return description;
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			String[] lines = description.split("\n");
			for (String line : lines)
			{
				String[] words = line.split(" ");
				int lineLen = 0;
				for (String word : words)
				{
					if (lineLen > 0 && lineLen + word.length() > maxLineLength)
					{
						sb.append('\n');
						lineLen = 0;
					}
					else if (lineLen > 0)
					{
						sb.append(' ');
						lineLen++;
					}
					sb.append(word);
					lineLen += word.length();
				}
				sb.append('\n');
			}
			return sb.toString();
		}
	}

	/**
	 * Computes the Levenshtein distance of two strings: the smallest number of inserted,
	 * deleted, or replaced characters that changes one string into the other
	 *
	 * <p>
	 * Works on {@code char} values, so a character outside the Basic Multilingual Plane counts
	 * as two. {@link org.cassandragargoyle.api.fuzzy.Levenshtein} works on code points and also
	 * computes a similarity
	 *
	 * @param val0 first string, must not be {@code null}
	 * @param val1 second string, must not be {@code null}
	 * @return the distance, 0 for equal strings
	 */
	public static int levenshteinDistance(String val0, String val1)
	{
		char[] chars0 = val0.toCharArray();
		int len0 = val0.length() + 1;

		char[] chars1 = val1.toCharArray();
		int len1 = val1.length() + 1;

		// initial cost of skipping prefix in String s0
		int[] cost = new int[len0];
		for (int i = 0; i < len0; i++)
		{
			cost[i] = i;
		}

		// dynamically computing the array of distances transformation cost for each letter in s1
		int[] newCost = new int[len0];
		for (int j = 1; j < len1; j++)
		{
			// initial cost of skipping prefix in String s1
			newCost[0] = j;

			// transformation cost for each letter in s0
			for (int i = 1; i < len0; i++)
			{
				// matching current letters in both strings
				int match = (chars0[i - 1] == chars1[j - 1]) ? 0 : 1;

				// computing cost for each transformation
				int costReplace = cost[i - 1] + match;
				int costInsert = cost[i] + 1;
				int costDelete = newCost[i - 1] + 1;

				// keep minimum cost
				newCost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
			}

			int[] tmp = cost;
			cost = newCost;
			newCost = tmp;
		}

		// the distance is the cost for transforming all letters in both strings
		return cost[len0 - 1];
	}

	/**
	 * Computes the Levenshtein distance of two arrays of strings, with whole strings as the units
	 *
	 * <p>
	 * For example the word lists {@code [the, cat, sat]} and {@code [the, dog, sat, down]} have
	 * the distance 2: one replaced word and one inserted word. Items are compared with
	 * {@link String#equals(Object)}
	 *
	 * @param val0 first array, items must not be {@code null}
	 * @param val1 second array
	 * @return the distance, 0 for equal arrays
	 */
	public static int levenshteinDistance(String[] val0, String[] val1)
	{
		int len0 = val0.length + 1;
		int len1 = val1.length + 1;

		// initial cost of skipping prefix in String s0
		int[] cost = new int[len0];
		for (int i = 0; i < len0; i++)
		{
			cost[i] = i;
		}

		// dynamically computing the array of distances transformation cost for each letter in s1
		int[] newCost = new int[len0];
		for (int j = 1; j < len1; j++)
		{
			// initial cost of skipping prefix in String s1
			newCost[0] = j;

			// transformation cost for each letter in s0
			for (int i = 1; i < len0; i++)
			{
				// matching current letters in both strings
				int match = (val0[i - 1].equals(val1[j - 1])) ? 0 : 1;

				// computing cost for each transformation
				int costReplace = cost[i - 1] + match;
				int costInsert = cost[i] + 1;
				int costDelete = newCost[i - 1] + 1;

				// keep minimum cost
				newCost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
			}

			int[] tmp = cost;
			cost = newCost;
			newCost = tmp;
		}

		// the distance is the cost for transforming all letters in both strings
		return cost[len0 - 1];
	}

	/**
	 * Splits a string at a delimiter and removes the whitespace around every part
	 *
	 * <p>
	 * Empty parts are kept: {@code "a, b,,c"} gives {@code [a, b, , c]}
	 *
	 * @param value     string to split, can be {@code null}
	 * @param delimiter delimiter character
	 * @return the parts, a new list; an unmodifiable empty list for {@code null} or {@code ""}
	 */
	public static List<String> splitAndStrip(String value, char delimiter)
	{
		if (StringUtil.notEmpty(value))
		{
			ArrayList<String> list = new ArrayList<>();
			int off = 0;
			int next;
			while ((next = value.indexOf(delimiter, off)) != -1)
			{
				list.add(value.substring(off, next).strip());
				off = next + 1;
			}
			list.add(value.substring(off).strip());
			return list;
		}
		return Collections.emptyList();
	}

	/**
	 * Splits a string at a delimiter
	 *
	 * <p>
	 * Unlike {@link String#split(String)}, the delimiter is a plain character, not a regular
	 * expression, and empty parts at the end are kept: {@code ","} gives two empty strings
	 *
	 * @param value     string to split, can be {@code null}
	 * @param delimiter delimiter character
	 * @return the parts, a new list; an unmodifiable empty list for {@code null} or {@code ""}
	 */
	public static List<String> split(String value, char delimiter)
	{
		if (StringUtil.notEmpty(value))
		{
			ArrayList<String> list = new ArrayList<>();
			int off = 0;
			int next;
			while ((next = value.indexOf(delimiter, off)) != -1)
			{
				list.add(value.substring(off, next));
				off = next + 1;
			}
			list.add(value.substring(off));
			return list;
		}
		return Collections.emptyList();
	}
}
