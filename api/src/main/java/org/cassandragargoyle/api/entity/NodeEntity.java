/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import org.cassandragargoyle.api.Constants;

/**
 * Class NodeEntity represents a generic entity that can be included as a node in various types of diagrams or data structures.
 * @author kurc
 * @since 2024-11-10
 */
public class NodeEntity extends AbstractEntity implements Node
{
	public NodeEntity(String name)
	{
		super(name);
	}

	@Override
	public String getLabel()
	{
		return getPropertyStr(Constants.PROP_LABEL);
	}

	public void setLabel(String label)
	{
		setProperty(Constants.PROP_LABEL, label);
	}

	public void setContent(String content)
	{
		setProperty(Constants.PROP_CONTENT, content);
	}

	@Override
	public String getContent()
	{
		return getPropertyStr(Constants.PROP_CONTENT);
	}

}
