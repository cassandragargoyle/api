/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import static org.cassandragargoyle.api.NullAssertions.assertRejectsNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

/**
 * Tests of the null checks in {@link Properties}, {@link BaseClass}, and {@link OsInfo}
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public class PropertiesNullCheckTest
{
	@Test
	public void typedPropertyRejectsNullType()
	{
		Properties properties = new Properties();
		assertRejectsNull("targetType", () -> properties.getTypedProperty("missing", null));
		assertRejectsNull("elementType", () -> properties.getTypedListProperty("missing", null));
	}

	@Test
	public void beginUpdateRejectsNullUpdateObject()
	{
		Properties properties = new Properties();
		assertRejectsNull("updateObject", () -> properties.beginUpdate(null, false));
		// The failed call does not leave an active batch behind
		assertDoesNotThrow(() -> properties.beginUpdate(this, false));
	}

	@Test
	public void asPropertiesRejectsNullString()
	{
		BaseClass base = new BaseClass();
		assertRejectsNull("propertiesString", () -> base.asProperties((String) null));
		assertRejectsNull("propertiesString", () -> base.asProperties((String) null, "="));
		assertDoesNotThrow(() -> base.asProperties((StringBuilder) null, "="));
	}

	@Test
	public void printfRejectsNullFormat()
	{
		assertRejectsNull("formatString", () -> new BaseClass().printf(null));
	}

	@Test
	public void osInfoRejectsNullKey()
	{
		assertRejectsNull("key", () -> new OsInfo().getProperty(null));
	}
}
