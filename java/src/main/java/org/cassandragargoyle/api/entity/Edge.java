/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;


/**
 * Link between two entities of a diagram or of another graph structure
 *
 * <p>
 * An edge holds its two ends, {@code node1} and {@code node2}. The ends are
 * {@link Entity} objects, not only {@link Node} objects, so any entity can be
 * linked. The interface does not define a direction; an implementation or a
 * diagram that needs one must document which end is the start
 *
 * <p>
 * {@link Diagram} keeps its edges in a {@code Set<Edge>}.
 * {@link EdgeEntity} is the default implementation that keeps the two ends as
 * properties and names the edge {@code "node1Name-node2Name"}
 *
 * @author Zdenek
 * @since 2024-11-09
 * @see Node
 * @see Diagram#addEdge(Entity, Entity)
 */
public interface Edge
{
	/**
	 * Sets the first end of the edge
	 *
	 * @param entity the first end, can be {@code null}
	 */
	void setNode1(Entity entity);

	/**
	 * Returns the first end of the edge
	 *
	 * @return the first end, or {@code null} when it is not set
	 */
	Entity getNode1();

	/**
	 * Sets the second end of the edge
	 *
	 * @param entity the second end, can be {@code null}
	 */
	void setNode2(Entity entity);

	/**
	 * Returns the second end of the edge
	 *
	 * @return the second end, or {@code null} when it is not set
	 */
	Entity getNode2();
}
