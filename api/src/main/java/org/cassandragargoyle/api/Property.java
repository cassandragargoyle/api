/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import org.cassandragargoyle.api.util.StringUtil;

/**
 *
 * @author kurc
 * @since 2024-08-16
 */
public class Property
{
	private String name;

	private Object value;

	public Property(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public Object getValue()
	{
		return value;
	}

	public void set(Object value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		if (value == null)
		{
			return null;
		}
		if (value instanceof String)
		{
			return (String) value;
		}
		return value.toString();
	}

	public Integer getInt()
	{
		if (value == null)
		{
			return null;
		}
		if (value instanceof Integer)
		{
			return (Integer) value;
		}
		if (value instanceof String)
		{
			try
			{
				return Integer.parseInt((String) value);
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException("The string cannot be parsed as an integer.");
			}
		}
		try
		{
			return Integer.valueOf(value.toString());
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Wrong number format.");
		}
	}

	public boolean isEmpty()
	{
		return value == null || StringUtil.isNullOrEmpty(value.toString());
	}
}
