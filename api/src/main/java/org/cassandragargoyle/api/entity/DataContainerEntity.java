/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Class DataContainerEntity
 * @author kurc
 * @since 2024-11-11
 */
public class DataContainerEntity extends AbstractEntity implements DataContainer
{
	private List<Object> objects = new ArrayList<>();

	public DataContainerEntity(String name)
	{
		super(name);
	}

	@Override
	public void add(Object obj)
	{
		objects.add(obj);
	}

	@Override
	public int size()
	{
		return objects.size();
	}

	public void addToList(String name, Object value)
	{
		List<Object> list = (List<Object>) properties.getValue(name);
		if (!properties.containsKey(name))
		{
			list = new ArrayList<>();
			properties.put(name, list);
		}
		list.add(value);
	}

}
