/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import org.cassandragargoyle.api.entity.Platform;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cassandragargoyle.api.Constants;
import org.cassandragargoyle.api.log.LogFactory;

/**
 * Class OsDetector
 * @author Zdenek
 * @since 2024-10-31
 */
public class OsDetector
{
	private static final Logger LOG = LogFactory.getLogger(OsDetector.class);

	private static Platform platform;

	private OsDetector()
	{

	}

	public static boolean isWindows()
	{
		return "Windows".equalsIgnoreCase(getOS());
	}

	public static boolean isLinux()
	{
		return "Linux/Unix".equalsIgnoreCase(getOS());
	}

	public static boolean isUbuntu()
	{
		var p = OsDetector.getOSProperties();
		return p == null ? false : p.getProperty("NAME") != null && p.getProperty("NAME").contains("ubuntu");
	}

	public static boolean isMacOS()
	{
		return "MacOS".equalsIgnoreCase(getOS());
	}

	public static boolean isSunOS()
	{
		return "Solaris".equalsIgnoreCase(getOS());
	}

	public static String getOperatingSystemName()
	{
		return System.getProperty("os.name");
	}

	public static String getOS()
	{
		String os = getOperatingSystemName().toLowerCase();

		if (os.contains("win"))
		{
			return Constants.PROP_OS_WINDOWS;
		}
		else if (os.contains("mac"))
		{
			return Constants.PROP_OS_MAC;
		}
		else if (os.contains("nix") || os.contains("nux") || os.contains("aix"))
		{
			return Constants.PROP_OS_LINUX_UNIX;
		}
		else if (os.contains("sunos"))
		{
			return "Solaris";
		}

		return Constants.PROP_UNKNOWN_OS;
	}

	/**
	 * Get platform identifier string in format "os-arch" (e.g. "linux-x86_64")
	 *
	 * @return platform string for the current OS and architecture
	 * @throws UnsatisfiedLinkError if the current platform is not supported
	 */
	public static String getPlatformString()
	{
		String os = getOperatingSystemName().toLowerCase();
		String arch = System.getProperty("os.arch").toLowerCase();

		if (os.contains("linux") && (arch.contains("amd64") || arch.contains("x86_64")))
		{
			return "linux-x86_64";
		}
		else if (os.contains("windows") && (arch.contains("amd64") || arch.contains("x86_64")))
		{
			return "windows-x86_64";
		}

		throw new UnsatisfiedLinkError("Unsupported platform: " + os + "/" + arch + ". Supported: linux-x86_64, windows-x86_64");
	}

	public static Properties getOSProperties()
	{
		Properties properties = null;
		if (isWindows())
		{
			properties = new Properties();
			properties.setProperty("NAME", getOperatingSystemName());
			properties.setProperty("VERSION", System.getProperty("os.version"));
			return properties;
		}
		if (isLinux())
		{
			//In older versions of the operating system, /etc/os-release does not exist.
			/*
			 * BSD Variants:
			 *
			 * FreeBSD: It uses /usr/local/etc/rc.conf for configuration but does not have
			 * /etc/os-release. You can check system version using uname -a or freebsd-version.
			 * OpenBSD: Similar to FreeBSD, it doesn't have /etc/os-release but provides
			 * version information through uname.
			 * NetBSD: Also uses uname for system version information.
			 * AIX: IBM's AIX does not use /etc/os-release, but you can find system information with the oslevel command.
			 *
			 * Solaris: Similar to AIX, Solaris does not have /etc/os-release. You can find version
			 * information using the uname command or by checking /etc/release.
			 */
			if (checkFile("/etc/os-release"))
			{
				return getOSPropertiesFromReleaseFile();
			}
		}
		return properties;
	}

	/**
	 * The /etc/os-release file is a standard configuration file found in many modern Linux
	 * distributions. This file provides information about the operating system, such as its
	 * name, version, and other relevant details. Here are some common Linux distributions
	 * where you can find this file:
	 *
	 * Debian and its derivatives:
	 * Ubuntu
	 * Linux Mint
	 * Kubuntu
	 * Lubuntu
	 *
	 * Fedora:
	 * Found in Fedora and its derivatives like CentOS and RHEL (Red Hat Enterprise Linux).
	 *
	 * Arch Linux and its derivatives:
	 * Manjaro
	 * Arch Linux itself.
	 *
	 * openSUSE:
	 * Available in both Leap and Tumbleweed versions.
	 *
	 * Alpine Linux:
	 * This minimalist system also contains /etc/os-release.
	 *
	 * Gentoo:
	 * Many Gentoo systems use this file for version identification.
	 */
	private static Properties getOSPropertiesFromReleaseFile()
	{
		Properties properties = new Properties();

		try (FileInputStream input = new FileInputStream("/etc/os-release"))
		{
			properties.load(input);
		}
		catch (IOException ex)
		{
			LOG.log(Level.WARNING, "Failed to read /etc/os-release", ex);
		}
		return properties;
	}

	private static boolean checkFile(String path)
	{
		Path filePath = Paths.get(path);
		return Files.exists(filePath);
	}

	public static List<String> getPackageManager()
	{
		throw new UnsupportedOperationException("OSDetector.getPackageManager is not supported.");
	}

	public static boolean isRunning64BitJava()
	{
		return "64".equals(System.getProperty("sun.arch.data.model"));
	}

	/**
	 * TODO: Where is used
	 * @return
	 */
	public static Platform getPlatform()
	{
		if (platform == null)
		{
			//TODO: platform == new ....
		}
		return platform;
	}
}
