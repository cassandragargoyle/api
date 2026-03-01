/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * Class BaseClass
 * @author kurc
 * @since 2024-11-04
 */
public class BaseClass
{
	protected Properties asProperties(String propertiesString)
	{
		return asProperties(propertiesString, null);
	}

	protected Properties asProperties(StringBuilder propertiesBuilder, String separator)
	{
		return asProperties(safeString(propertiesBuilder), separator);
	}

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
			//TODO:LOG
			e.printStackTrace();
		}
		return properties;
	}

	protected static String safeString(Object obj)
	{
		if (obj instanceof String)
		{
			return (String) obj;
		}
		return null == obj || obj.toString() == null ? "" : obj.toString();
	}

	protected static String asString(Object obj)
	{
		if (obj instanceof String)
		{
			return (String) obj;
		}
		return null == obj || obj.toString() == null ? null : obj.toString();
	}

	protected Integer asInteger(Object obj)
	{
		if (obj != null)
		{
			if (obj instanceof Integer)
			{
				return (Integer) obj;
			}
			else
			{
				return Integer.valueOf(obj.toString());
			}
		}
		return null;
	}

	protected Long asLong(Object obj)
	{
		if (obj != null)
		{
			if (obj instanceof Long)
			{
				return (Long) obj;
			}
			else
			{
				return Long.valueOf(obj.toString());
			}
		}
		return null;
	}

	protected void printf(String formatString, String... arguments)
	{
		System.out.printf(formatString, arguments);
	}
}
