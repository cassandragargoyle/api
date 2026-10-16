/*
 *  SPDX-License-Identifier: MIT
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import org.cassandragargoyle.api.util.StringUtil;

/**
 * One named value in a {@link Properties} bag
 *
 * <p>
 * The value can be of any type, including {@code null}. {@link Properties} keeps one
 * {@code Property} per key and changes its value with {@link #set(Object)}, so the
 * name of a property never changes. The class adds helpers that read the value as a
 * string or as an integer
 *
 * <p>
 * The class is not thread-safe
 *
 * @author Zdenek
 * @since 2024-08-16
 */
public class Property
{
	/** Key of the property in its {@link Properties} bag */
	private String name;

	/** Current value, can be {@code null} */
	private Object value;

	/**
	 * Creates a property with a name and a first value
	 *
	 * @param name  key of the property
	 * @param value first value, can be {@code null}
	 */
	public Property(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns the key of the property
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the raw value without a conversion
	 *
	 * @return the value, or {@code null}
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Replaces the value
	 *
	 * <p>
	 * The property does not notify anybody about the change, the change events are
	 * fired by {@link Properties#set(String, Object)}
	 *
	 * @param value new value, can be {@code null}
	 */
	public void set(Object value)
	{
		this.value = value;
	}

	/**
	 * Returns the value as a string
	 *
	 * <p>
	 * Unlike the usual contract of {@link Object#toString()}, the result is {@code null}
	 * when the value is {@code null}
	 *
	 * @return the value itself for a {@code String}, {@code value.toString()} for other types, or {@code null}
	 */
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

	/**
	 * Returns the value as an integer
	 *
	 * <p>
	 * An {@code Integer} is returned as it is, a {@code String} is parsed, and other types
	 * are converted through their {@code toString()} value. A {@code Long} or a
	 * {@code Double} such as {@code 2.0} therefore fails unless its text is a valid
	 * {@code int}
	 *
	 * @return the integer value, or {@code null} when the value is {@code null}
	 * @throws IllegalArgumentException when the value cannot be read as an {@code int}
	 */
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

	/**
	 * Tells whether the property has no usable value
	 *
	 * @return {@code true} when the value is {@code null} or its {@code toString()} is an empty string
	 */
	public boolean isEmpty()
	{
		return value == null || StringUtil.isNullOrEmpty(value.toString());
	}
}
