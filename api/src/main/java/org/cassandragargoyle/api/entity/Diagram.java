/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import java.util.Set;

/**
 * Interface Diagram
 * @author kurc
 * @since 2024-11-08
 */
public interface Diagram extends Entity
{
	void addNode(Entity node);

	Set<Entity> getNodes();

	Set<Entity> getAllNodes();

	Set<Edge> getAllEdges();

	void addEdge(Entity node1, Entity node2);

	void addEdge(Edge node);

	public Set<Edge> getEdges();

	void addDiagram(Diagram diagram);

	public Set<Diagram> getDiagrams();
}
