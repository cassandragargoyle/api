/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Class DiagramEntity
 * @author kurc
 * @since 2024-11-08
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

	@Override
	public Set<Entity> getAllNodes()
	{
		var list = new HashSet<Entity>();
		_addDiagramNodes(list, this);
		return list;
	}

	@Override
	public Set<Edge> getAllEdges()
	{
		var list = new HashSet<Edge>();
		_addDiagramEdges(list, this);
		return list;
	}

	private static void _addDiagramNodes(Set<Entity> list, Diagram diagram)
	{
		list.addAll(diagram.getNodes());
		for (var subDiagram : diagram.getDiagrams())
		{
			_addDiagramNodes(list, subDiagram);
		}
	}

	private static void _addDiagramEdges(Set<Edge> list, Diagram diagram)
	{
		list.addAll(diagram.getEdges());
		for (var subDiagram : diagram.getDiagrams())
		{
			_addDiagramEdges(list, subDiagram);
		}
	}
}
