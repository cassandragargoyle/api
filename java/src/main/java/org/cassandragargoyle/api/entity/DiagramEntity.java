/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Property-backed implementation of {@link Diagram}
 *
 * <p>Nodes, edges, and sub-diagrams are not held in dedicated fields but stored
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
		getDiagrams().add(diagram);
	}

	@Override
	public void addNode(Entity node)
	{
		getNodes().add(node);
	}

	/**
	 * Find a direct child node by its name (linear scan, sub-diagrams not searched)
	 *
	 * <p>Not part of the {@link Diagram} interface — convenience accessor; returns
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

	@Override
	public void addEdge(Edge edge)
	{
		getEdges().add(edge);
	}

	@Override
	public void addEdge(Entity node1, Entity node2)
	{
		getEdges().add(new EdgeEntity(node1, node2));
	}

	// Lazy-init pattern shared by getNodes/getEdges/getDiagrams: the unchecked cast
	// is safe because nothing outside this class writes these property keys.
	@Override
	public Set<Entity> getNodes()
	{
		Set<Entity> nodes = (Set<Entity>) getProperty("nodes");
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
		Set<Edge> edges = (Set<Edge>) getProperty("edges");
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
		Set<Diagram> diagrams = (Set<Diagram>) getProperty("diagrams");
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
