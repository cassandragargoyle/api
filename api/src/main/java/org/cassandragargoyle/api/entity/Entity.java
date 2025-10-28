/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Class Entity
 * @author kurc
 * @since 2024-10-24
 */
public interface Entity
{
	String getName();

	boolean isDeleted();

	Object getProperty(String key);

	String getPropertyStr(String key);

	//protected void initialize() throws Error
}
