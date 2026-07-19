/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Class Platform
 * @author Zdenek
 * @since 2024-11-05
 */
public interface Platform extends Software
{
	String getOsShortName();

	boolean isWindows();
}
