/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import static org.cassandragargoyle.api.NullAssertions.assertRejectsNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.prefs.Preferences;
import org.junit.jupiter.api.Test;

/**
 * Tests of the null checks in {@link Base64Encoder}, {@link DateUtil}, {@link PreferencesUtil}, and {@link SystemUtil}
 *
 * <p>
 * The checks run before the preferences are read or written, so the tests do not change the user preferences
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public class UtilNullCheckTest
{
	private static final Preferences PREFS = Preferences.userRoot();

	@Test
	public void base64RejectsNullInput()
	{
		assertRejectsNull("s", () -> Base64Encoder.encodeString(null));
		assertRejectsNull("in", () -> Base64Encoder.encode((byte[]) null));
		assertRejectsNull("in", () -> Base64Encoder.encode(null, 0));
		assertRejectsNull("s", () -> Base64Encoder.decodeString(null));
		assertRejectsNull("s", () -> Base64Encoder.decode((String) null));
		assertRejectsNull("in", () -> Base64Encoder.decode((char[]) null));
	}

	@Test
	public void dateUtilRejectsNullDate()
	{
		assertRejectsNull("date", () -> DateUtil.toDayCount(null));
		assertRejectsNull("date", () -> DateUtil.atMidnight(null));
		assertRejectsNull("date", () -> DateUtil.toMidnight(null));
		assertRejectsNull("date", () -> DateUtil.isToday(null));
	}

	@Test
	public void dateUtilStillAcceptsNullWhereDocumented()
	{
		assertEquals("", DateUtil.formatDate(null));
		assertNull(DateUtil.min(null, null));
		assertNull(DateUtil.max(null, null));
	}

	@Test
	public void preferencesUtilRejectsNullPreferences()
	{
		assertRejectsNull("prefs", () -> PreferencesUtil.getStringList(null, "k"));
		assertRejectsNull("prefs", () -> PreferencesUtil.putStringList(null, "k", List.of()));
		assertRejectsNull("prefs", () -> PreferencesUtil.putDimension(null, "k", new Dimension()));
		assertRejectsNull("prefs", () -> PreferencesUtil.getDimension(null, "k"));
		assertRejectsNull("prefs", () -> PreferencesUtil.putBounds(null, "k", new Rectangle()));
		assertRejectsNull("prefs", () -> PreferencesUtil.getBounds(null, "k"));
		assertRejectsNull("prefs", () -> PreferencesUtil.putValue(null, "k", "v", "d"));
		assertRejectsNull("prefs", () -> PreferencesUtil.putIntValue(null, "k", 1, 0));
		assertRejectsNull("prefs", () -> PreferencesUtil.putBooleanValue(null, "k", true, false));
		assertRejectsNull("prefs", () -> PreferencesUtil.putDoubleValue(null, "k", 1.0, 0.0));
	}

	@Test
	public void preferencesUtilRejectsNullKey()
	{
		assertRejectsNull("key", () -> PreferencesUtil.getStringList(PREFS, null));
		assertRejectsNull("key", () -> PreferencesUtil.putStringList(PREFS, null, List.of()));
		assertRejectsNull("key", () -> PreferencesUtil.putDimension(PREFS, null, new Dimension()));
		assertRejectsNull("key", () -> PreferencesUtil.getDimension(PREFS, null));
		assertRejectsNull("key", () -> PreferencesUtil.putBounds(PREFS, null, new Rectangle()));
		assertRejectsNull("key", () -> PreferencesUtil.getBounds(PREFS, null));
		assertRejectsNull("key", () -> PreferencesUtil.putValue(PREFS, null, "v", "d"));
		assertRejectsNull("key", () -> PreferencesUtil.putIntValue(PREFS, null, 1, 0));
		assertRejectsNull("key", () -> PreferencesUtil.putBooleanValue(PREFS, null, true, false));
		assertRejectsNull("key", () -> PreferencesUtil.putDoubleValue(PREFS, null, 1.0, 0.0));
	}

	@Test
	public void preferencesUtilRejectsNullValues()
	{
		assertRejectsNull("values", () -> PreferencesUtil.putStringList(PREFS, "k", null));
		assertRejectsNull("values", () -> PreferencesUtil.putStringList(PREFS, "k", null, 1));
		assertRejectsNull("dim", () -> PreferencesUtil.putDimension(PREFS, "k", null));
		assertRejectsNull("rect", () -> PreferencesUtil.putBounds(PREFS, "k", null));
	}

	@Test
	public void systemUtilRejectsNullPropertyName()
	{
		assertRejectsNull("propertyName", () -> SystemUtil.getDouble(null, 1.0));
	}
}
