/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author kurc
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
		System.out.println("getOperatingSystem");
		String expResult = "";
		String result = OsDetector.getOS();
	}

}
