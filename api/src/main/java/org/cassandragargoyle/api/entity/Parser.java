/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Interface Parser
 * @author kurc
 */
public interface Parser
{
	DataContainer Parse(String filePath);
}
