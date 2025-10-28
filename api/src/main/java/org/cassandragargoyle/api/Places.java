/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import java.io.File;

/**
 * Class Places
 * @author kurc
 * @since 2024-11-29
 */
public class Places
{

	private static final String USER_DIRECTORY = System.getProperty("user.home") + File.separator + ".myapp";

	// Returns the directory for user-specific configuration files
	public static File getConfigDirectory()
	{
		File configDir = new File(USER_DIRECTORY, "config");
		if (!configDir.exists())
		{
			configDir.mkdirs();
		}
		return configDir;
	}

	// Returns the directory for user-specific cache files
	public static File getCacheDirectory()
	{
		File cacheDir = new File(USER_DIRECTORY, "cache");
		if (!cacheDir.exists())
		{
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

	// Returns the directory for user-specific data files
	public static File getDataDirectory()
	{
		File dataDir = new File(USER_DIRECTORY, "data");
		if (!dataDir.exists())
		{
			dataDir.mkdirs();
		}
		return dataDir;
	}

	// Returns the user-specific base directory
	public static File getUserDirectory()
	{
		File userDir = new File(USER_DIRECTORY);
		if (!userDir.exists())
		{
			userDir.mkdirs();
		}
		return userDir;
	}
}
