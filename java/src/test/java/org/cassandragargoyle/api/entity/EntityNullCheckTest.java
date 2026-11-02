/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import static org.cassandragargoyle.api.NullAssertions.assertRejectsNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Tests of the null checks in {@link EdgeEntity} and {@link DiagramEntity}
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public class EntityNullCheckTest
{
	@Test
	public void edgeRejectsNullNodes()
	{
		NodeEntity node = new NodeEntity("a");
		assertRejectsNull("node1", () -> new EdgeEntity(null, node));
		assertRejectsNull("node2", () -> new EdgeEntity(node, null));
	}

	@Test
	public void edgeWithNodesHasDerivedName()
	{
		EdgeEntity edge = new EdgeEntity(new NodeEntity("a"), new NodeEntity("b"));
		assertEquals("a-b", edge.getName());
	}

	// Each call uses a new diagram, because a second call of getNodes, getEdges or getDiagrams on one diagram
	// fails with ClassCastException (getProperty returns the Property wrapper), which is a separate defect
	@Test
	public void diagramRejectsNullNode()
	{
		assertRejectsNull("node", () -> new DiagramEntity("d").addNode(null));
	}

	@Test
	public void diagramRejectsNullEdge()
	{
		assertRejectsNull("edge", () -> new DiagramEntity("d").addEdge(null));
		assertRejectsNull("node1", () -> new DiagramEntity("d").addEdge(null, new NodeEntity("b")));
		assertRejectsNull("node2", () -> new DiagramEntity("d").addEdge(new NodeEntity("a"), null));
	}

	@Test
	public void diagramRejectsNullSubDiagram()
	{
		assertRejectsNull("diagram", () -> new DiagramEntity("d").addDiagram(null));
	}
}
