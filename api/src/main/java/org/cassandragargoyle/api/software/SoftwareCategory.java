/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.software;

/**
 * Class SoftwareCategory
 * @author kurc
 * @since 2025-11-05
 */
public enum SoftwareCategory
{
	IDE("Integrated Development Environment"),
	DEVELOPMENT_TOOLS("Development Tools"),
	PROGRAMMING_LANGUAGE_SUPPORT("Programming Languages Support"),
	VERSION_CONTROL_INTEGRATION("Version Control Integration"),
	FRAMEWORK_AND_LIBRARY_SUPPORT("Framework and Library Support"),
	CROSS_PLATFORM_DEVELOPMENT("Cross-Platform Development"),
	COLLABORATION_TOOLS("Collaboration Tools"),

	// Categories for CMake
	BUILD_SYSTEM_GENERATOR("Build System Generator"),
	CROSS_PLATFORM_DEVELOPMENT_TOOL("Cross-Platform Development Tool"),
	CONFIGURATION_MANAGEMENT_TOOL("Configuration Management Tool"),
	PACKAGE_MANAGEMENT("Package Management"),
	MODULAR_AND_EXTENSIBLE("Modular and Extensible"),
	BUILD_CONFIGURATION_MANAGEMENT("Build Configuration Management"),
	INTEGRATION_WITH_IDES("Integration with IDEs");

	private final String description;

	SoftwareCategory(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}
}
