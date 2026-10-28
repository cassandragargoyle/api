/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Interface Parser
 * @author Zdenek
 * @since 2026-07-19
 */
public interface Parser
{
	DataContainer parse(String filePath);
}
