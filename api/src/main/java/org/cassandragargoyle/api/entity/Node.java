/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import org.cassandragargoyle.api.entity.Entity;

/**
 * Interface Node
 * @author kurc
 * @since 2024-11-10
 */
public interface Node extends Entity
{
	/**
	 * Get label for display in diagram.
	 * @return
	 */
	String getLabel();

	String getContent();

	String getExternalId();
}
