/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import java.util.Set;

/**
 * Graph-based diagram composed of nodes, edges, and nested sub-diagrams
 *
 * <p>Supports hierarchical composition: a diagram can contain child diagrams,
 * and methods {@link #getAllNodes()} / {@link #getAllEdges()} traverse the
 * full hierarchy recursively.
 *
 * @author Zdenek
 * @since 2024-11-08
 * @see DiagramEntity
 * @see Edge
 */
public interface Diagram extends Entity
{
	/** Add a node to this diagram */
	void addNode(Entity node);

	/** Get direct nodes of this diagram (excluding sub-diagrams) */
	Set<Entity> getNodes();

	/** Get all nodes recursively including nodes from nested sub-diagrams */
	Set<Entity> getAllNodes();

	/** Get all edges recursively including edges from nested sub-diagrams */
	Set<Edge> getAllEdges();

	/** Create and add an edge between two nodes */
	void addEdge(Entity node1, Entity node2);

	/** Add an existing edge to this diagram */
	void addEdge(Edge node);

	/** Get direct edges of this diagram (excluding sub-diagrams) */
	Set<Edge> getEdges();

	/** Add a nested sub-diagram */
	void addDiagram(Diagram diagram);

	/** Get direct child sub-diagrams */
	Set<Diagram> getDiagrams();
}
