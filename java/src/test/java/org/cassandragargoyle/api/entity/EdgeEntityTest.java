/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link EdgeEntity}
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public class EdgeEntityTest
{
	// Regression test for #90: getNode1 and getNode2 failed with ClassCastException
	@Test
	public void constructorStoresBothNodes()
	{
		NodeEntity nodeA = new NodeEntity("a");
		NodeEntity nodeB = new NodeEntity("b");

		EdgeEntity edge = new EdgeEntity(nodeA, nodeB);

		assertEquals("a-b", edge.getName());
		assertSame(nodeA, edge.getNode1());
		assertSame(nodeB, edge.getNode2());
	}

	@Test
	public void namedEdgeHasNoNodesUntilTheyAreSet()
	{
		NodeEntity nodeA = new NodeEntity("a");
		NodeEntity nodeB = new NodeEntity("b");
		EdgeEntity edge = new EdgeEntity("e");

		assertNull(edge.getNode1());
		assertNull(edge.getNode2());

		edge.setNode1(nodeA);
		edge.setNode2(nodeB);

		assertSame(nodeA, edge.getNode1());
		assertSame(nodeB, edge.getNode2());
	}

	@Test
	public void setNodeReplacesThePreviousNode()
	{
		NodeEntity nodeA = new NodeEntity("a");
		NodeEntity nodeB = new NodeEntity("b");
		NodeEntity nodeC = new NodeEntity("c");
		EdgeEntity edge = new EdgeEntity(nodeA, nodeB);

		edge.setNode1(nodeC);

		assertSame(nodeC, edge.getNode1());
		assertSame(nodeB, edge.getNode2());
	}
}
