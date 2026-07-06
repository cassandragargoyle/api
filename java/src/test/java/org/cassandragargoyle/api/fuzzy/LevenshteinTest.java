/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Levenshtein}.
 *
 * <p>
 * The {@code shouldMatchSharedVector*} tests cover the canonical vectors
 * defined in issue #010 and must agree byte-for-byte with the Python and Go
 * implementations of the same package.
 *
 * @author Zdenek
 */
public class LevenshteinTest
{
	private static final double EPS = 1e-9;

	@Test
	public void shouldMatchSharedVectorBothEmpty()
	{
		assertEquals(0, Levenshtein.distance("", ""));
		assertEquals(1.0, Levenshtein.similarity("", ""), EPS);
	}

	@Test
	public void shouldMatchSharedVectorFirstEmpty()
	{
		assertEquals(3, Levenshtein.distance("", "abc"));
		assertEquals(0.0, Levenshtein.similarity("", "abc"), EPS);
	}

	@Test
	public void shouldMatchSharedVectorSecondEmpty()
	{
		assertEquals(3, Levenshtein.distance("abc", ""));
		assertEquals(0.0, Levenshtein.similarity("abc", ""), EPS);
	}

	@Test
	public void shouldMatchSharedVectorIdentical()
	{
		assertEquals(0, Levenshtein.distance("abc", "abc"));
		assertEquals(1.0, Levenshtein.similarity("abc", "abc"), EPS);
	}

	@Test
	public void shouldMatchSharedVectorKittenSitting()
	{
		assertEquals(3, Levenshtein.distance("kitten", "sitting"));
		assertEquals(1.0 - 3.0 / 7.0, Levenshtein.similarity("kitten", "sitting"), EPS);
	}

	@Test
	public void shouldMatchSharedVectorFlawLawn()
	{
		assertEquals(2, Levenshtein.distance("flaw", "lawn"));
		assertEquals(0.5, Levenshtein.similarity("flaw", "lawn"), EPS);
	}

	@Test
	public void shouldMatchSharedVectorGumboGambol()
	{
		assertEquals(2, Levenshtein.distance("gumbo", "gambol"));
		assertEquals(1.0 - 2.0 / 6.0, Levenshtein.similarity("gumbo", "gambol"), EPS);
	}

	@Test
	public void shouldMatchSharedVectorSaturdaySunday()
	{
		assertEquals(3, Levenshtein.distance("Saturday", "Sunday"));
		assertEquals(1.0 - 3.0 / 8.0, Levenshtein.similarity("Saturday", "Sunday"), EPS);
	}

	@Test
	public void shouldMatchSharedVectorCafeAccented()
	{
		assertEquals(1, Levenshtein.distance("café", "cafe"));
		assertEquals(0.75, Levenshtein.similarity("café", "cafe"), EPS);
	}

	@Test
	public void shouldMatchSharedVectorCjkPrefix()
	{
		assertEquals(1, Levenshtein.distance("日本語", "日本"));
		assertEquals(1.0 - 1.0 / 3.0, Levenshtein.similarity("日本語", "日本"), EPS);
	}

	@Test
	public void shouldBeSymmetric()
	{
		assertEquals(
			Levenshtein.distance("kitten", "sitting"),
			Levenshtein.distance("sitting", "kitten"));
		assertEquals(
			Levenshtein.distance("café", "cafe"),
			Levenshtein.distance("cafe", "café"));
	}

	@Test
	public void shouldCountCodePointsNotUtf16Units()
	{
		// U+1F600 GRINNING FACE is a supplementary code point encoded as a UTF-16
		// surrogate pair, i.e. String#length() reports 2. distance("😀", "")
		// must therefore be 1, not 2.
		String emoji = "😀";
		assertEquals(2, emoji.length());
		assertEquals(1, Levenshtein.distance(emoji, ""));
		assertEquals(0.0, Levenshtein.similarity(emoji, ""), EPS);
		assertEquals(1, Levenshtein.distance("", emoji));
	}

	@Test
	public void shouldRejectNullInputs()
	{
		assertThrows(NullPointerException.class, () -> Levenshtein.distance(null, "x"));
		assertThrows(NullPointerException.class, () -> Levenshtein.distance("x", null));
		assertThrows(NullPointerException.class, () -> Levenshtein.similarity(null, "x"));
		assertThrows(NullPointerException.class, () -> Levenshtein.similarity("x", null));
	}

	@Test
	public void similarityIsBounded()
	{
		double s = Levenshtein.similarity("kitten", "sitting");
		assertTrue(s >= 0.0 && s <= 1.0);
	}
}
