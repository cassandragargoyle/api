/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.software;

/**
 * Class OperatingSystems
 * @author kurc
 * @since 2025-11-05
 */
public enum OSType
{
	WINDOWS,
	MACOS,
	LINUX,
	UNIX,
	ANDROID,
	IOS,
	OTHER;

	/**
	 * Gets an OSType based on a string representation.
	 * @param osName The name of the OS.
	 * @return The corresponding OSType.
	 */
	public static OSType fromString(String osName)
	{
		if (osName == null)
		{
			return OTHER;
		}
		osName = osName.toLowerCase();
		if (osName.contains("win"))
		{
			return WINDOWS;
		}
		else if (osName.contains("mac"))
		{
			return MACOS;
		}
		else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix"))
		{
			return LINUX;
		}
		else if (osName.contains("sunos"))
		{
			return UNIX;
		}
		else if (osName.contains("android"))
		{
			return ANDROID;
		}
		else if (osName.contains("ios"))
		{
			return IOS;
		}
		else
		{
			return OTHER;
		}
	}
}
