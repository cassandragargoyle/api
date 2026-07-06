/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.fuzzy;

/**
 * Classic Levenshtein edit distance and a derived normalized similarity.
 *
 * <p>
 * The implementation operates on Unicode code points, not on UTF-16 code units,
 * so supplementary characters (e.g. CJK extension blocks, emoji) are counted as
 * single units. Use this class instead of any {@code String#length()}-based
 * computation when correctness on non-BMP input matters.
 *
 * @author Zdenek
 * @since 2026-05-01
 */
public final class Levenshtein
{
	private Levenshtein()
	{
		throw new IllegalStateException("Utility class.");
	}

	/**
	 * Returns the classic Levenshtein edit distance between {@code a} and {@code b}.
	 *
	 * <p>
	 * The cost of every insertion, deletion, and substitution is 1. The function
	 * is symmetric and operates on Unicode code points.
	 *
	 * @param a first input
	 * @param b second input
	 * @return number of single-code-point edits needed to transform {@code a} into {@code b}
	 * @throws NullPointerException if either input is {@code null}
	 */
	public static int distance(CharSequence a, CharSequence b)
	{
		if (a == null)
		{
			throw new NullPointerException("a");
		}
		if (b == null)
		{
			throw new NullPointerException("b");
		}

		int[] aPoints = toCodePoints(a);
		int[] bPoints = toCodePoints(b);

		// Always iterate the longer sequence in the outer loop so the working
		// row holds min(n, m) + 1 entries.
		if (aPoints.length < bPoints.length)
		{
			int[] tmp = aPoints;
			aPoints = bPoints;
			bPoints = tmp;
		}

		int n = aPoints.length;
		int m = bPoints.length;

		if (m == 0)
		{
			return n;
		}

		int[] previous = new int[m + 1];
		int[] current = new int[m + 1];

		for (int j = 0; j <= m; j++)
		{
			previous[j] = j;
		}

		for (int i = 1; i <= n; i++)
		{
			current[0] = i;
			int aPoint = aPoints[i - 1];

			for (int j = 1; j <= m; j++)
			{
				int substitutionCost = (aPoint == bPoints[j - 1]) ? 0 : 1;

				int costInsert = current[j - 1] + 1;
				int costDelete = previous[j] + 1;
				int costReplace = previous[j - 1] + substitutionCost;

				current[j] = Math.min(Math.min(costInsert, costDelete), costReplace);
			}

			int[] swap = previous;
			previous = current;
			current = swap;
		}

		return previous[m];
	}

	/**
	 * Returns a normalized similarity in {@code [0.0, 1.0]} derived from the
	 * Levenshtein distance.
	 *
	 * <p>
	 * Defined as {@code 1 - distance / max(len(a), len(b))} where the lengths
	 * are measured in Unicode code points. Two empty inputs are considered
	 * identical and yield {@code 1.0}.
	 *
	 * @param a first input
	 * @param b second input
	 * @return similarity score in {@code [0.0, 1.0]}
	 * @throws NullPointerException if either input is {@code null}
	 */
	public static double similarity(CharSequence a, CharSequence b)
	{
		if (a == null)
		{
			throw new NullPointerException("a");
		}
		if (b == null)
		{
			throw new NullPointerException("b");
		}

		int aLen = codePointLength(a);
		int bLen = codePointLength(b);
		int max = Math.max(aLen, bLen);

		if (max == 0)
		{
			return 1.0;
		}

		return 1.0 - ((double) distance(a, b)) / max;
	}

	private static int[] toCodePoints(CharSequence s)
	{
		int len = s.length();
		int[] buffer = new int[len];
		int count = 0;

		for (int i = 0; i < len; )
		{
			int cp = Character.codePointAt(s, i);
			buffer[count++] = cp;
			i += Character.charCount(cp);
		}

		if (count == len)
		{
			return buffer;
		}

		int[] trimmed = new int[count];
		System.arraycopy(buffer, 0, trimmed, 0, count);
		return trimmed;
	}

	private static int codePointLength(CharSequence s)
	{
		int len = s.length();
		int count = 0;
		for (int i = 0; i < len; )
		{
			int cp = Character.codePointAt(s, i);
			i += Character.charCount(cp);
			count++;
		}
		return count;
	}
}
