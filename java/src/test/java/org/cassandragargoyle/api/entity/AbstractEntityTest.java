/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for AbstractEntity (via NodeEntity)
 * @author Zdenek
 */
public class AbstractEntityTest
{
	private NodeEntity entity;

	@BeforeEach
	public void setUp()
	{
		entity = new NodeEntity("test-node");
	}

	@Test
	public void testGetName()
	{
		assertEquals("test-node", entity.getName());
	}

	@Test
	public void testSetAndGetProperty()
	{
		entity.setProperty("color", "red");
		assertEquals("red", entity.getPropertyStr("color"));
	}

	@Test
	public void testSetPropertyWithSubKey()
	{
		entity.setProperty("command", "linux", "apt install");
		// Should use "_" separator between key and subKey
		assertEquals("apt install", entity.getPropertyStr("command_linux"));
	}

	@Test
	public void testSetPropertyByKeyAndSubKeyStr()
	{
		entity.setPropertyByKeyAndSubKeyStr("cmd", "win", "choco install");
		assertEquals("choco install", entity.getPropertyStr("cmd_win"));
	}

	@Test
	public void testGetPropertyByKeyAndSubKeyStr()
	{
		entity.setProperty("cmd_linux", "apt install");
		assertEquals("apt install", entity.getPropertyByKeyAndSubKeyStr("cmd", "linux"));
	}

	@Test
	public void testGetPropertyStrReturnsNullForMissing()
	{
		assertNull(entity.getPropertyStr("nonexistent"));
	}

	@Test
	public void testGetPropertyOrDefault()
	{
		assertEquals("default", entity.getPropertyOrDefault("missing", "default"));
		entity.setProperty("key", "value");
		assertEquals("value", entity.getPropertyStr("key"));
	}

	@Test
	public void testIsDeletedDefaultFalse()
	{
		assertFalse(entity.isDeleted());
	}

	@Test
	public void testSetDeleted()
	{
		entity.setDeleted(true);
		assertTrue(entity.isDeleted());
	}

	@Test
	public void testHasProperty()
	{
		assertFalse(entity.hasProperty("color"));
		entity.setProperty("color", "blue");
		assertTrue(entity.hasProperty("color"));
	}

	@Test
	public void testParentEntity()
	{
		NodeEntity parent = new NodeEntity("parent");
		entity.setParent(parent);
		assertNotNull(entity.getParent());
		assertEquals("parent", entity.getParent().getName());
	}
}
