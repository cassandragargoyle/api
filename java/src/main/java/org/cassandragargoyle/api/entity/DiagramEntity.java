/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Property-backed implementation of {@link Diagram}
 *
 * <p>
 * Nodes, edges, and sub-diagrams are not held in dedicated fields but stored
 * in the inherited property map of {@link AbstractEntity} under the keys
 * {@code "nodes"}, {@code "edges"}, and {@code "diagrams"}. Collections are
 * created lazily on first access, so an empty diagram allocates no sets.
 *
 * @author Zdenek
 * @since 2024-11-08
 * @see Diagram
 */
public class DiagramEntity extends AbstractEntity implements Diagram
{
	public DiagramEntity(String name)
	{
		super(name);
	}

	@Override
	public void addDiagram(Diagram diagram)
	{
		Objects.requireNonNull(diagram, "diagram");
		getDiagrams().add(diagram);
	}

	@Override
	public void addNode(Entity node)
	{
		Objects.requireNonNull(node, "node");
		getNodes().add(node);
	}

	/**
	 * Find a direct child node by its name (linear scan, sub-diagrams not searched)
	 *
	 * <p>
	 * Not part of the {@link Diagram} interface — convenience accessor; returns
	 * {@code null} when no node with the given name exists.
	 */
	public Entity getNode(String name)
	{
		for (var node : getNodes())
		{
			if (node.getName().equals(name))
			{
				return node;
			}
		}
		return null;
	}

	/**
	 * Add an existing edge to the direct edges of this diagram
	 *
	 * <p>
	 * The edges are kept in a set, so a second call with the same edge instance
	 * has no effect. The nodes of the edge are not checked and are not added to
	 * this diagram, call {@link #addNode(Entity)} for them.
	 *
	 * @param edge edge to add
	 * @throws NullPointerException if {@code edge} is {@code null}
	 */
	@Override
	public void addEdge(Edge edge)
	{
		Objects.requireNonNull(edge, "edge");
		getEdges().add(edge);
	}

	/**
	 * Create a new {@link EdgeEntity} between two nodes and add it to the direct
	 * edges of this diagram
	 *
	 * <p>
	 * The edge is named {@code "node1Name-node2Name"}. Every call creates a new
	 * edge instance, so two calls with the same nodes add two edges. The nodes
	 * are not checked and are not added to this diagram, call
	 * {@link #addNode(Entity)} for them.
	 *
	 * @param node1 first node of the edge
	 * @param node2 second node of the edge
	 * @throws NullPointerException if {@code node1} or {@code node2} is {@code null}
	 */
	@Override
	public void addEdge(Entity node1, Entity node2)
	{
		getEdges().add(new EdgeEntity(node1, node2));
	}

	// Lazy-init pattern shared by getNodes/getEdges/getDiagrams: the unchecked cast
	// is safe because nothing outside this class writes these property keys.
	// properties.getValue returns the stored set, getProperty would return the Property wrapper.
	@Override
	public Set<Entity> getNodes()
	{
		Set<Entity> nodes = (Set<Entity>) properties.getValue("nodes");
		if (nodes == null)
		{
			nodes = new HashSet<>();
			setProperty("nodes", nodes);
		}
		return nodes;
	}

	@Override
	public Set<Edge> getEdges()
	{
		Set<Edge> edges = (Set<Edge>) properties.getValue("edges");
		if (edges == null)
		{
			edges = new HashSet<>();
			setProperty("edges", edges);
		}
		return edges;
	}

	@Override
	public Set<Diagram> getDiagrams()
	{
		Set<Diagram> diagrams = (Set<Diagram>) properties.getValue("diagrams");
		if (diagrams == null)
		{
			diagrams = new HashSet<Diagram>();
			setProperty("diagrams", diagrams);
		}
		return diagrams;
	}

	// HashSet result deduplicates nodes/edges that may be referenced from
	// multiple sub-diagrams in the hierarchy.
	@Override
	public Set<Entity> getAllNodes()
	{
		var list = new HashSet<Entity>();
		addDiagramNodes(list, this);
		return list;
	}

	@Override
	public Set<Edge> getAllEdges()
	{
		var list = new HashSet<Edge>();
		addDiagramEdges(list, this);
		return list;
	}

	// Depth-first traversal; assumes the sub-diagram graph is acyclic
	// (a diagram is not expected to be its own ancestor).
	private static void addDiagramNodes(Set<Entity> list, Diagram diagram)
	{
		list.addAll(diagram.getNodes());
		for (var subDiagram : diagram.getDiagrams())
		{
			addDiagramNodes(list, subDiagram);
		}
	}

	private static void addDiagramEdges(Set<Edge> list, Diagram diagram)
	{
		list.addAll(diagram.getEdges());
		for (var subDiagram : diagram.getDiagrams())
		{
			addDiagramEdges(list, subDiagram);
		}
	}
}
