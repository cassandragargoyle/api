/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import org.cassandragargoyle.api.entity.Entity;

/**
 * Interface Edge
 * @author kurc
 * @since 2024-11-09
 */
public interface Edge
{
	void setNode1(Entity entity);

	Entity getNode1();

	void setNode2(Entity entity);

	Entity getNode2();
}
