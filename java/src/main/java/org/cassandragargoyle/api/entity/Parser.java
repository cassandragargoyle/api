/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Interface Parser
 * @author Zdenek
 */
public interface Parser
{
	DataContainer parse(String filePath);
}
