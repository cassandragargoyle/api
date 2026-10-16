/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;


/**
 * Entity that can be shown as a node of a diagram or of another graph structure
 *
 * <p>
 * A node adds a display label, a content, and an external identifier to the
 * name of an {@link Entity}. Two nodes are linked by an {@link Edge}.
 * {@link NodeEntity} is the default implementation that keeps these values as
 * properties
 *
 * @author Zdenek
 * @since 2024-11-10
 */
public interface Node extends Entity
{
	/**
	 * Returns the label that a diagram shows for the node
	 *
	 * <p>
	 * The label can differ from the name, which identifies the node
	 *
	 * @return the label, or {@code null} when it is not set
	 */
	String getLabel();

	/**
	 * Returns the content of the node, for example a description or a body
	 * text that a diagram can show next to the label
	 *
	 * @return the content, or {@code null} when it is not set
	 */
	String getContent();

	/**
	 * Returns the identifier of the node in an external source, for example
	 * the identifier of an element in an imported file
	 *
	 * @return the external identifier, or {@code null} when it is not set
	 */
	String getExternalId();
}
