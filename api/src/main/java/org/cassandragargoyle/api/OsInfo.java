/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import java.util.Properties;
import org.cassandragargoyle.api.util.OsDetector;

/**
 * Class OsInfo
 * @author kurc
 * @since 2024-10-24
 */
public class OsInfo
{
	private String osVersion;

	private String osArch;

	Properties properties = new Properties();

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

	public String getOsName()
	{
		return properties.getProperty("os.name");
	}

	public String getOsVersion()
	{
		return osVersion;
	}

	public String getVersionCodeName()
	{
		return getProperty("VERSION_CODENAME");
	}

	public String getId()
	{
		return getProperty("ID");
	}

	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}
}
