/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cassandragargoyle.api.log.LogFactory;

/**
 * Base class with small helpers that subclasses use to read loosely typed values
 *
 * <p>
 * The helpers turn text in the {@code key=value} format into {@link java.util.Properties},
 * and turn any object into a {@code String}, {@code Integer}, or {@code Long}. They are
 * {@code protected}, so only subclasses use them, for example
 * {@link org.cassandragargoyle.api.entity.AbstractEntity}, which reads its property values
 * with {@link #asString(Object)}
 *
 * <p>
 * Note: {@link #asProperties(String)} returns {@link java.util.Properties} from the JDK,
 * not {@link org.cassandragargoyle.api.Properties} of this module
 *
 * <p>
 * The class has no state, so the helpers are thread-safe
 *
 * @author Zdenek
 * @since 2024-11-04
 */
public class BaseClass
{
	private static final Logger LOG = LogFactory.getLogger(BaseClass.class);

	/**
	 * Parses text in the {@code key=value} format, one entry per line
	 *
	 * @param propertiesString text to parse, must not be {@code null}
	 * @return the parsed properties
	 * @throws NullPointerException if {@code propertiesString} is {@code null}
	 * @see #asProperties(String, String)
	 */
	protected Properties asProperties(String propertiesString)
	{
		return asProperties(propertiesString, null);
	}

	/**
	 * Parses the content of a {@link StringBuilder} in the {@code key=value} format
	 *
	 * @param propertiesBuilder text to parse; {@code null} gives empty properties
	 * @param separator         {@code "="} to replace every {@code ':'} with {@code '='}
	 *                          first, any other value or {@code null} to keep the text
	 * @return the parsed properties
	 * @see #asProperties(String, String)
	 */
	protected Properties asProperties(StringBuilder propertiesBuilder, String separator)
	{
		return asProperties(safeString(propertiesBuilder), separator);
	}

	/**
	 * Parses text in the {@code key=value} format with {@link Properties#load(java.io.Reader)}
	 *
	 * <p>
	 * The rules of {@code Properties.load} apply: the key ends at the first {@code '='},
	 * {@code ':'}, or space, lines that start with {@code #} or {@code !} are comments,
	 * and a backslash starts an escape sequence, so {@code C:\temp} is read as
	 * {@code C:} + tab + {@code emp}. Text that cannot be read is logged as a warning, and the
	 * entries read so far are returned
	 *
	 * <p>
	 * When {@code separator} is {@code "="}, every {@code ':'} in the whole text is replaced
	 * with {@code '='} before parsing, also inside values: {@code url=http://x:8080} gives the
	 * key {@code url} and the value {@code http=//x=8080}. {@code Properties.load} already
	 * accepts {@code ':'} as the separator, so this option is needed only for old callers. Any
	 * other separator value is ignored
	 *
	 * @param propertiesString text to parse, must not be {@code null}
	 * @param separator        {@code "="} to replace every {@code ':'} with {@code '='} first,
	 *                         any other value or {@code null} to keep the text
	 * @return the parsed properties
	 * @throws NullPointerException if {@code propertiesString} is {@code null}
	 */
	protected Properties asProperties(String propertiesString, String separator)
	{
		if (separator != null && "=".equals(separator))
		{
			propertiesString = propertiesString.replace(":", "=");
		}

		Properties properties = new Properties();
		try
		{
			properties.load(new StringReader(propertiesString));
		}
		catch (IOException e)
		{
			LOG.log(Level.WARNING, "Failed to parse properties string", e);
		}
		return properties;
	}

	/**
	 * Returns the text of an object, never {@code null}
	 *
	 * @param obj object to convert, can be {@code null}
	 * @return the string itself, the result of {@code obj.toString()}, or {@code ""} when
	 *         {@code obj} or its {@code toString()} is {@code null}
	 * @see #asString(Object)
	 */
	protected static String safeString(Object obj)
	{
		if (obj instanceof String string)
		{
			return string;
		}
		return null == obj || obj.toString() == null ? "" : obj.toString();
	}

	/**
	 * Returns the text of an object, or {@code null}
	 *
	 * @param obj object to convert, can be {@code null}
	 * @return the string itself, the result of {@code obj.toString()}, or {@code null} when
	 *         {@code obj} or its {@code toString()} is {@code null}
	 * @see #safeString(Object)
	 */
	protected static String asString(Object obj)
	{
		if (obj instanceof String string)
		{
			return string;
		}
		return null == obj || obj.toString() == null ? null : obj.toString();
	}

	/**
	 * Converts an object to an {@link Integer}
	 *
	 * <p>
	 * An {@code Integer} is returned as it is. Any other object is converted through its
	 * text with {@link Integer#valueOf(String)}, so {@code 5L} and {@code "7"} work, but
	 * {@code 5.0}, {@code " 7"}, and numbers outside the {@code int} range throw an exception
	 *
	 * @param obj object to convert, can be {@code null}
	 * @return the number, or {@code null} for {@code null}
	 * @throws NumberFormatException if the text of {@code obj} is not an {@code int}
	 */
	protected Integer asInteger(Object obj)
	{
		if (obj != null)
		{
			if (obj instanceof Integer integer)
			{
				return integer;
			}
			else
			{
				return Integer.valueOf(obj.toString());
			}
		}
		return null;
	}

	/**
	 * Converts an object to a {@link Long}
	 *
	 * <p>
	 * A {@code Long} is returned as it is. Any other object is converted through its text
	 * with {@link Long#valueOf(String)}, so {@code 5} and {@code "9000000000"} work, but
	 * {@code 5.0} and {@code " 7"} throw an exception
	 *
	 * @param obj object to convert, can be {@code null}
	 * @return the number, or {@code null} for {@code null}
	 * @throws NumberFormatException if the text of {@code obj} is not a {@code long}
	 */
	protected Long asLong(Object obj)
	{
		if (obj != null)
		{
			if (obj instanceof Long aLong)
			{
				return aLong;
			}
			else
			{
				return Long.valueOf(obj.toString());
			}
		}
		return null;
	}

	/**
	 * Prints formatted text to standard output with {@link java.io.PrintStream#printf(String, Object...)}
	 *
	 * <p>
	 * The arguments are strings, so use {@code %s} for every argument; a numeric conversion
	 * such as {@code %d} throws {@link java.util.IllegalFormatConversionException}
	 *
	 * @param formatString format string, for example {@code "Hello %s%n"}
	 * @param arguments    values for the format string
	 */
	protected void printf(String formatString, String... arguments)
	{
		System.out.printf(formatString, arguments);
	}
}
