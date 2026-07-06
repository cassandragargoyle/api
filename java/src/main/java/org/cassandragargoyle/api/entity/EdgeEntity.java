/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Concrete implementation of a graph edge connecting two Entity nodes
 *
 * <p>Stores endpoint references (node1, node2) as named properties
 * in the underlying {@link AbstractEntity} property map. The edge name
 * is derived automatically as "node1Name-node2Name" when constructed
 * from two entities.
 *
 * @author Zdenek
 * @since 2024-11-09
 * @see Edge
 * @see AbstractEntity
 */
public class EdgeEntity extends AbstractEntity implements Edge
{
	public EdgeEntity(String name)
	{
		super(name);
	}

	public EdgeEntity(Entity node1, Entity node2)
	{
		super(node1.getName() + "-" + node2.getName());
		setNode1(node1);
		setNode2(node2);
	}

	@Override
	public void setNode1(Entity entity)
	{
		setProperty("node1", entity);
	}

	@Override
	public Entity getNode1()
	{
		return (Entity) getProperty("node1");
	}

	@Override
	public void setNode2(Entity entity)
	{
		setProperty("node2", entity);
	}

	@Override
	public Entity getNode2()
	{
		return (Entity) getProperty("node2");
	}
}
