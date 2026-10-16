/*
 *  SPDX-License-Identifier: MIT
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import org.cassandragargoyle.api.Constants;

/**
 * Default implementation of {@link Node}: a generic entity that can be a node
 * of a diagram or of another graph structure
 *
 * <p>
 * The label and the content are stored as the {@link Constants#PROP_LABEL}
 * and {@link Constants#PROP_CONTENT} properties. The external identifier comes
 * from {@link AbstractEntity#getExternalId()}
 *
 * @author Zdenek
 * @since 2024-11-10
 */
public class NodeEntity extends AbstractEntity implements Node
{
	/**
	 * Creates a node with the given name and without a label and a content
	 *
	 * @param name name of the node, can be {@code null}
	 */
	public NodeEntity(String name)
	{
		super(name);
	}

	/**
	 * Returns the label that a diagram shows for the node, stored in the
	 * {@link Constants#PROP_LABEL} property
	 *
	 * @return the label, or {@code null} when it is not set
	 */
	@Override
	public String getLabel()
	{
		return getPropertyStr(Constants.PROP_LABEL);
	}

	/**
	 * Sets the label that a diagram shows for the node
	 *
	 * @param label new label, can be {@code null}
	 */
	public void setLabel(String label)
	{
		setProperty(Constants.PROP_LABEL, label);
	}

	/**
	 * Sets the content of the node
	 *
	 * @param content new content, can be {@code null}
	 */
	public void setContent(String content)
	{
		setProperty(Constants.PROP_CONTENT, content);
	}

	/**
	 * Returns the content of the node, stored in the
	 * {@link Constants#PROP_CONTENT} property
	 *
	 * @return the content, or {@code null} when it is not set
	 */
	@Override
	public String getContent()
	{
		return getPropertyStr(Constants.PROP_CONTENT);
	}

}
