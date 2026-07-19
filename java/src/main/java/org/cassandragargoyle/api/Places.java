/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import java.io.File;

/**
 * Resolves user-specific application directories under a single root in
 * the user's home folder.
 *
 * <p>
 * The class follows a simplified XDG-style layout with three sub-directories
 * — {@code config/}, {@code cache/}, {@code data/} — co-located under one
 * application root (currently {@code ~/.myapp}). Each directory is created
 * lazily on first access; subsequent calls return the existing directory.
 *
 * <p>
 * All accessors are static. {@link File#mkdirs()} is idempotent, so concurrent
 * first-access from multiple threads is safe even though no synchronization
 * is performed.
 *
 * <p>
 * <b>Failure mode:</b> the {@link File#mkdirs()} return value is intentionally
 * ignored — callers receive a {@link File} handle even if the directory could
 * not be created (e.g. due to permissions or a full filesystem). Callers that
 * require strict creation guarantees should re-check {@link File#isDirectory()}
 * on the returned handle.
 *
 * @author Zdenek
 * @since 2024-11-29
 */
public class Places
{

	private static final String USER_DIRECTORY = System.getProperty("user.home") + File.separator + ".myapp";

	/**
	 * Returns the directory for user-specific configuration files,
	 * creating it if it does not yet exist.
	 *
	 * @return {@code ~/.myapp/config}, created on first access
	 */
	public static File getConfigDirectory()
	{
		File configDir = new File(USER_DIRECTORY, "config");
		if (!configDir.exists())
		{
			configDir.mkdirs();
		}
		return configDir;
	}

	/**
	 * Returns the directory for user-specific cache files,
	 * creating it if it does not yet exist.
	 *
	 * <p>
	 * Cached content is non-authoritative — callers must tolerate its absence
	 * or eviction by external tools.
	 *
	 * @return {@code ~/.myapp/cache}, created on first access
	 */
	public static File getCacheDirectory()
	{
		File cacheDir = new File(USER_DIRECTORY, "cache");
		if (!cacheDir.exists())
		{
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

	/**
	 * Returns the directory for user-specific persistent data files,
	 * creating it if it does not yet exist.
	 *
	 * @return {@code ~/.myapp/data}, created on first access
	 */
	public static File getDataDirectory()
	{
		File dataDir = new File(USER_DIRECTORY, "data");
		if (!dataDir.exists())
		{
			dataDir.mkdirs();
		}
		return dataDir;
	}

	/**
	 * Returns the application's root user directory,
	 * creating it if it does not yet exist.
	 *
	 * <p>
	 * Prefer the more specific accessors ({@link #getConfigDirectory()},
	 * {@link #getCacheDirectory()}, {@link #getDataDirectory()}) when the
	 * content has a known kind. Use this method only when the caller genuinely
	 * needs the root path, e.g. for a custom sub-directory layout.
	 *
	 * @return {@code ~/.myapp}, created on first access
	 */
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
