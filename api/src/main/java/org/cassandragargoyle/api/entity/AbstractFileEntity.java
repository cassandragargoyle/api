/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Class AbstractFileEntity
 * @author kurc
 */
public abstract class AbstractFileEntity extends AbstractEntity
{
	public AbstractFileEntity(String name, String fileName)
	{
		super(name);
		setProperty("fileName", fileName);
	}

	public String getFileName()
	{
		return getPropertyStr("fileName");
	}

}
