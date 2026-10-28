/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link StringUtil#isNullOrBlank(String)} and {@link StringUtil#notBlank(String)}
 *
 * @author Zdenek
 * @since 1.2.0
 */
public class StringUtilBlankTest
{
	@Test
	public void isNullOrBlankTreatsWhiteSpaceAsBlank()
	{
		assertTrue(StringUtil.isNullOrBlank(null));
		assertTrue(StringUtil.isNullOrBlank(""));
		assertTrue(StringUtil.isNullOrBlank("   "));
		assertTrue(StringUtil.isNullOrBlank(" \t\n"));
		assertFalse(StringUtil.isNullOrBlank("a"));
		assertFalse(StringUtil.isNullOrBlank("  a  "));
	}

	@Test
	public void notBlankIsTheOpposite()
	{
		for (String value : new String[] { null, "", "   ", " \t\n", "a", "  a  " })
		{
			assertTrue(StringUtil.notBlank(value) != StringUtil.isNullOrBlank(value), String.valueOf(value));
		}
	}

	@Test
	public void blankDiffersFromEmptyOnlyForWhiteSpace()
	{
		assertFalse(StringUtil.isNullOrEmpty("   "));
		assertTrue(StringUtil.isNullOrBlank("   "));
		assertTrue(StringUtil.notEmpty("   "));
		assertFalse(StringUtil.notBlank("   "));
	}
}
