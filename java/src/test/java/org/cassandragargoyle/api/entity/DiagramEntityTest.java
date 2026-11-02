/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link DiagramEntity}
 *
 * @author Zdenek
 * @since 2026-11-02
 */
public class DiagramEntityTest
{
	private DiagramEntity diagram;
	private NodeEntity nodeA;
	private NodeEntity nodeB;

	@BeforeEach
	public void setUp()
	{
		diagram = new DiagramEntity("d");
		nodeA = new NodeEntity("a");
		nodeB = new NodeEntity("b");
	}

	@Test
	public void emptyDiagramHasEmptyCollections()
	{
		assertTrue(diagram.getNodes().isEmpty());
		assertTrue(diagram.getEdges().isEmpty());
		assertTrue(diagram.getDiagrams().isEmpty());
		assertTrue(diagram.getAllNodes().isEmpty());
		assertTrue(diagram.getAllEdges().isEmpty());
	}

	// Regression test for #90: the second call failed with ClassCastException
	@Test
	public void gettersReturnSameCollectionOnRepeatedCalls()
	{
		assertSame(diagram.getNodes(), diagram.getNodes());
		assertSame(diagram.getEdges(), diagram.getEdges());
		assertSame(diagram.getDiagrams(), diagram.getDiagrams());
	}

	@Test
	public void addNodeCanBeCalledMoreThanOnce()
	{
		diagram.addNode(nodeA);
		diagram.addNode(nodeB);

		assertEquals(Set.of(nodeA, nodeB), diagram.getNodes());
	}

	@Test
	public void addEdgeCanBeCalledMoreThanOnce()
	{
		Edge edge = new EdgeEntity(nodeA, nodeB);

		diagram.addEdge(edge);
		diagram.addEdge(nodeB, nodeA);

		Set<Edge> edges = diagram.getEdges();
		assertEquals(2, edges.size());
		assertTrue(edges.contains(edge));
	}

	@Test
	public void addDiagramCanBeCalledMoreThanOnce()
	{
		DiagramEntity sub1 = new DiagramEntity("sub1");
		DiagramEntity sub2 = new DiagramEntity("sub2");

		diagram.addDiagram(sub1);
		diagram.addDiagram(sub2);

		assertEquals(Set.of(sub1, sub2), diagram.getDiagrams());
	}

	@Test
	public void getNodeFindsDirectChildByName()
	{
		diagram.addNode(nodeA);
		diagram.addNode(nodeB);

		assertSame(nodeB, diagram.getNode("b"));
		assertNull(diagram.getNode("missing"));
	}

	@Test
	public void getNodeDoesNotSearchSubDiagrams()
	{
		DiagramEntity sub = new DiagramEntity("sub");
		sub.addNode(nodeA);
		diagram.addDiagram(sub);

		assertNull(diagram.getNode("a"));
	}

	@Test
	public void getAllNodesIncludesNodesOfSubDiagrams()
	{
		DiagramEntity sub = new DiagramEntity("sub");
		DiagramEntity subSub = new DiagramEntity("subSub");
		NodeEntity nodeC = new NodeEntity("c");
		diagram.addNode(nodeA);
		sub.addNode(nodeB);
		subSub.addNode(nodeC);
		subSub.addNode(nodeA);
		sub.addDiagram(subSub);
		diagram.addDiagram(sub);

		assertEquals(Set.of(nodeA, nodeB, nodeC), diagram.getAllNodes());
	}

	@Test
	public void getAllEdgesIncludesEdgesOfSubDiagrams()
	{
		Edge edge1 = new EdgeEntity(nodeA, nodeB);
		Edge edge2 = new EdgeEntity(nodeB, nodeA);
		DiagramEntity sub = new DiagramEntity("sub");
		diagram.addEdge(edge1);
		sub.addEdge(edge2);
		sub.addEdge(edge1);
		diagram.addDiagram(sub);

		assertEquals(Set.of(edge1, edge2), diagram.getAllEdges());
	}
}
