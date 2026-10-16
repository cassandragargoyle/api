/*
 *  SPDX-License-Identifier: MIT
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import java.util.Properties;
import org.cassandragargoyle.api.util.OsDetector;

/**
 * Information about the operating system that runs the JVM
 *
 * <p>
 * The values are read once, in the constructor. The name, the version and
 * the architecture come from the Java system properties {@code os.name},
 * {@code os.version} and {@code os.arch}. More details come from
 * {@link OsDetector#getOSProperties()}: on Linux these are the keys of
 * {@code /etc/os-release} (for example {@code ID}, {@code VERSION_CODENAME}),
 * on Windows the keys {@code NAME} and {@code VERSION}
 *
 * @author Zdenek
 * @since 2024-10-24
 */
public class OsInfo
{
	/** Value of the {@code os.version} system property */
	private String osVersion;

	/** Value of the {@code os.arch} system property, not exposed yet */
	private String osArch;

	/** {@code os.name} and the properties from {@link OsDetector#getOSProperties()} */
	Properties properties = new Properties();

	/**
	 * Reads the information about the current operating system
	 */
	public OsInfo()
	{
		// Get the operating system name
		properties.put("os.name", System.getProperty("os.name"));
		// Get the operating system version
		osVersion = System.getProperty("os.version");
		// Get the operating system architecture
		osArch = System.getProperty("os.arch");

		properties.putAll(OsDetector.getOSProperties());
	}

	/**
	 * Returns the name of the operating system as Java reports it, for example {@code Linux} or {@code Windows 11}
	 *
	 * @return value of the {@code os.name} system property
	 */
	public String getOsName()
	{
		return properties.getProperty("os.name");
	}

	/**
	 * Returns the version of the operating system as Java reports it, on Linux it is the kernel version
	 *
	 * @return value of the {@code os.version} system property
	 */
	public String getOsVersion()
	{
		return osVersion;
	}

	/**
	 * Returns the code name of the distribution release, for example {@code noble} on Ubuntu 24.04
	 *
	 * @return value of {@code VERSION_CODENAME} from {@code /etc/os-release}, or {@code null} when it is not available
	 */
	public String getVersionCodeName()
	{
		return getProperty("VERSION_CODENAME");
	}

	/**
	 * Returns the identifier of the distribution in lower case, for example {@code ubuntu} or {@code fedora}
	 *
	 * @return value of {@code ID} from {@code /etc/os-release}, or {@code null} when it is not available
	 */
	public String getId()
	{
		return getProperty("ID");
	}

	/**
	 * Returns one property of the operating system
	 *
	 * @param key {@code os.name} or a key from {@link OsDetector#getOSProperties()}
	 * @return the value, or {@code null} when the key is missing
	 */
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}
}
