/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Interface DataContainer
 * @author kurc
 * @since 2024-11-11
 */
public interface DataContainer extends Entity
{
	void add(Object obj);

	int size();
}
