/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Properties;
import org.junit.jupiter.api.Test;

/**
 * Tests for BaseClass
 * @author Zdenek
 */
public class BaseClassTest
{
	// Concrete subclass for testing protected methods
	private static class TestableBaseClass extends BaseClass
	{
		public Properties testAsProperties(String input)
		{
			return asProperties(input);
		}

		public Properties testAsProperties(String input, String separator)
		{
			return asProperties(input, separator);
		}

		public String testSafeString(Object obj)
		{
			return safeString(obj);
		}

		public String testAsString(Object obj)
		{
			return asString(obj);
		}
	}

	private final TestableBaseClass base = new TestableBaseClass();

	@Test
	public void testAsPropertiesParsesValidInput()
	{
		Properties props = base.testAsProperties("key1=value1\nkey2=value2");
		assertNotNull(props);
		assertEquals("value1", props.getProperty("key1"));
		assertEquals("value2", props.getProperty("key2"));
	}

	@Test
	public void testAsPropertiesWithColonSeparator()
	{
		Properties props = base.testAsProperties("key1:value1\nkey2:value2", "=");
		assertNotNull(props);
		assertEquals("value1", props.getProperty("key1"));
		assertEquals("value2", props.getProperty("key2"));
	}

	@Test
	public void testAsPropertiesHandlesInvalidInput()
	{
		// Should not throw, returns empty properties and logs warning
		Properties props = base.testAsProperties("");
		assertNotNull(props);
		assertTrue(props.isEmpty());
	}

	@Test
	public void testSafeStringWithNull()
	{
		assertEquals("", base.testSafeString(null));
	}

	@Test
	public void testSafeStringWithString()
	{
		assertEquals("hello", base.testSafeString("hello"));
	}

	@Test
	public void testSafeStringWithNumber()
	{
		assertEquals("42", base.testSafeString(42));
	}

	@Test
	public void testAsStringWithNull()
	{
		assertNull(base.testAsString(null));
	}

	@Test
	public void testAsStringWithString()
	{
		assertEquals("hello", base.testAsString("hello"));
	}
}
