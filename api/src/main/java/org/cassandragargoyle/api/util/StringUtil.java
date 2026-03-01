/**
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
 * Class StringUtil
 *
 */
@SuppressWarnings("java:S1192")
public class StringUtil
{
	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private StringUtil()
	{
		throw new IllegalStateException("Utility class.");
	}

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

	public static boolean startsIgnoreCase(String str, String prefix)
	{
		return str.length() >= prefix.length() && prefix.equalsIgnoreCase(str.substring(0, prefix.length()));
	}

	public static boolean containsIgnoreCase(String str, String sub)
	{
		return str.length() >= sub.length() && str.toLowerCase().contains(sub.toLowerCase());
	}

	public static boolean endsIgnoreCase(String str, String suffix)
	{
		return str != null && str.length() >= suffix.length() && suffix.equalsIgnoreCase(str.substring(str.length() - suffix.length(), str.length()));
	}

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

	public static void appendHtmlEscaped(StringBuilder sb, String value, int maxLineLength)
	{
		appendHtmlEscaped(sb, value, maxLineLength, Integer.MAX_VALUE);
	}

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

	public static boolean toBoolean(final String value)
	{
		return toBoolean(value, false);
	}

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

	public static boolean isNullOrEmpty(final String value)
	{
		return value == null || value.isEmpty();
	}

	public static boolean notEmpty(final String value)
	{
		return value != null && value.length() > 0;
	}

	public static String safeString(final String value)
	{
		return value == null ? "" : value;
	}

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

	public static String toString(final Collection<String> parts)
	{
		return toString(parts, ';');
	}

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

	public static List<String> fromString(final String value)
	{
		return fromString(value, ';');
	}

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

	public static String valueOrDefault(String value, String defaultValue)
	{
		if (value != null && !value.isEmpty())
		{
			return value;
		}
		return defaultValue;
	}

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

	public static String removeSuffix(String value, String suffix)
	{
		if (value.endsWith(suffix))
		{
			return value.substring(0, value.length() - suffix.length());
		}
		return value;
	}

	public static String removePrefix(String value, String prefix)
	{
		if (value.startsWith(prefix))
		{
			return value.substring(prefix.length());
		}
		return value;
	}

	public static boolean endsWith(StringBuilder sb, String suffix)
	{
		return sb.length() >= suffix.length() && suffix.equals(sb.substring(sb.length() - suffix.length()));
	}

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

	public static String min(String v1, String v2)
	{
		return v1.compareTo(v2) <= 0 ? v1 : v2;
	}

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

	public static int ordinalIndexOf(String str, String substr, int n)
	{
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
		{
			pos = str.indexOf(substr, pos + 1);
		}
		return pos;
	}

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
