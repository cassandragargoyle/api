/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

/**
 * Class PreferencesUtil
 * @author kurc
 * @since 2024-11-09
 */
public class PreferencesUtil
{
	private static final String DIMENSION_WIDTH = "_dimension_width";

	private static final String DIMENSION_HEIGHT = "_dimension_height";

	private static final String BOUNDS_X = "_bounds_x";

	private static final String BOUNDS_Y = "_bounds_y";

	private static final String BOUNDS_WIDTH = "_bounds_width";

	private static final String BOUNDS_HEIGHT = "_bounds_height";

	private PreferencesUtil()
	{
		throw new IllegalStateException("Utility class.");
	}

	/**
	 * Loads list of string stored under the given key in the given preference object
	 * @param prefs source preferences
	 * @param key   key
	 * @return the list or empty list
	 */
	public static List<String> getStringList(Preferences prefs, String key)
	{
		int size = prefs.getInt(key, 0);
		List<String> list = new ArrayList<>(size);
		for (int pos = 0; pos < size; pos++)
		{
			list.add(prefs.get(key + pos, ""));
		}
		return list;
	}

	/**
	 * Stores the given list under the given key - does not remove old values on position > value.size()
	 * @param prefs  destination preferences
	 * @param key    key
	 * @param values values to store
	 */
	public static void putStringList(Preferences prefs, String key, List<String> values)
	{
		putStringList(prefs, key, values, Integer.MAX_VALUE);
	}

	/**
	 * Stores first x entries of the given list under the given key - does not remove old values on position >
	 * value.size()
	 * @param prefs  destination preferences
	 * @param key    key
	 * @param values values to store
	 * @param count  count of values to store
	 */
	public static void putStringList(Preferences prefs, String key, List<String> values, int count)
	{
		int pos = 0;
		for (String value : values)
		{
			if (value != null)
			{
				prefs.put(key + pos, value);
				pos++;
				if (pos >= count)
				{
					break;
				}
			}
		}
		prefs.putInt(key, pos);
	}

	/**
	 * Stores {@link Dimension} into preferences.
	 * @param prefs destination preferences
	 * @param key   key
	 * @param dim   dimension to store
	 */
	public static void putDimension(Preferences prefs, String key, Dimension dim)
	{
		prefs.putInt(key + DIMENSION_WIDTH, dim.width);
		prefs.putInt(key + DIMENSION_HEIGHT, dim.height);
	}

	/**
	 * Loads {@link Dimension} from preferences.
	 * @param prefs source preferences
	 * @param key   key
	 * @return dimension or null if no value is associated with the key
	 */
	public static Dimension getDimension(Preferences prefs, String key)
	{
		int width = prefs.getInt(key + DIMENSION_WIDTH, -1);
		int height = prefs.getInt(key + DIMENSION_HEIGHT, -1);

		if (width > -1 && height > -1)
		{
			return new Dimension(width, height);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Stores {@link Rectangle} into preferences.
	 * @param prefs destination preferences
	 * @param key   key
	 * @param rect  bounds
	 */
	public static void putBounds(Preferences prefs, String key, Rectangle rect)
	{
		prefs.putInt(key + BOUNDS_X, rect.x);
		prefs.putInt(key + BOUNDS_Y, rect.y);
		prefs.putInt(key + BOUNDS_WIDTH, rect.width);
		prefs.putInt(key + BOUNDS_HEIGHT, rect.height);
	}

	/**
	 * Loads {@link Rectangle} from preferences.
	 * @param prefs source preferences
	 * @param key   key
	 * @return bounds or null if no value is associated with the key
	 */
	public static Rectangle getBounds(Preferences prefs, String key)
	{
		int x = prefs.getInt(key + BOUNDS_X, -1);
		int y = prefs.getInt(key + BOUNDS_Y, -1);
		int width = prefs.getInt(key + BOUNDS_WIDTH, -1);
		int height = prefs.getInt(key + BOUNDS_HEIGHT, -1);

		if (x > -1 && y > -1 && width > -1 && height > -1)
		{
			return new Rectangle(x, y, width, height);
		}
		else
		{
			return null;
		}
	}

	public static void putValue(Preferences prefs, String key, String value, String defaultValue)
	{
		String old = prefs.get(key, null);
		if (!Objects.equals(value, defaultValue))
		{
			prefs.put(key, value);
		}
		else if (old != null)
		{
			prefs.remove(key);
		}
	}

	public static void putIntValue(Preferences prefs, String key, int value, int defaultValue)
	{
		String old = prefs.get(key, null);
		if (value != defaultValue)
		{
			prefs.putInt(key, value);
		}
		else if (old != null)
		{
			prefs.remove(key);
		}
	}

	public static void putBooleanValue(Preferences prefs, String key, boolean value, boolean defaultValue)
	{
		String old = prefs.get(key, null);
		if (value != defaultValue)
		{
			prefs.putBoolean(key, value);
		}
		else if (old != null)
		{
			prefs.remove(key);
		}
	}

	public static void putDoubleValue(Preferences prefs, String key, double value, double defaultValue)
	{
		String old = prefs.get(key, null);
		if (value != defaultValue)
		{
			prefs.putDouble(key, value);
		}
		else if (old != null)
		{
			prefs.remove(key);
		}
	}
}
