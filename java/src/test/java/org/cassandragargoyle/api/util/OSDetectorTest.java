/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Zdenek
 * @since 2024-10-31
 */
public class OSDetectorTest
{

	public OSDetectorTest()
	{
	}

	@BeforeAll
	public static void setUpClass()
	{
	}

	@BeforeEach
	public void setUp()
	{
	}

	@Test
	public void testIsWin()
	{
		System.out.println("isWin");
		boolean expResult = false;
		boolean result = OsDetector.isWindows();
	}

	@Test
	public void testIsLinux()
	{
		System.out.println("isLinux");
		boolean expResult = false;
		boolean result = OsDetector.isLinux();
	}

	@Test
	public void testIsUbuntu()
	{
		System.out.println("isUbuntu");
		boolean expResult = false;
		boolean result = OsDetector.isUbuntu();
	}

	@Test
	public void testIsMacOS()
	{
		System.out.println("isMacOS");
		boolean expResult = false;
		boolean result = OsDetector.isMacOS();
	}

	@Test
	public void testIsSunOS()
	{
		System.out.println("isSunOS");
		boolean expResult = false;
		boolean result = OsDetector.isSunOS();
	}

	@Test
	public void testGetOperatingSystem()
	{
		String result = OsDetector.getOS();
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Test
	public void testGetOSPropertiesReturnsNonNull()
	{
		java.util.Properties props = OsDetector.getOSProperties();
		// On any supported OS (Windows/Linux) should return properties
		if (OsDetector.isWindows() || OsDetector.isLinux())
		{
			assertNotNull(props);
			assertNotNull(props.getProperty("NAME"));
		}
	}

	@Test
	public void testGetPlatformString()
	{
		String platform = OsDetector.getPlatformString();
		assertNotNull(platform);
		assertTrue(platform.contains("-"));
		// Should match current OS
		if (OsDetector.isLinux())
		{
			assertTrue(platform.startsWith("linux"));
		}
		else if (OsDetector.isWindows())
		{
			assertTrue(platform.startsWith("windows"));
		}
	}

	@Test
	public void testExactlyOneOsDetected()
	{
		int count = 0;
		if (OsDetector.isWindows()) count++;
		if (OsDetector.isLinux()) count++;
		if (OsDetector.isMacOS()) count++;
		if (OsDetector.isSunOS()) count++;
		assertEquals(1, count, "Exactly one OS should be detected");
	}

}
