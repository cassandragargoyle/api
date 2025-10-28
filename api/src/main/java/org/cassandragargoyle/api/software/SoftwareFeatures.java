/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.software;

/**
 * Interface SoftwareFeatures
 * @author kurc
 * @since 2024-11-05
 */
public enum SoftwareFeatures
{
	DISTRIBUTED_SYSTEM("Global", "Global"),

	// ---------------------- Version Control ----------------------
	VERSION_TRACKING("Version Control", "Version Tracking"),
	BRANCHING("Version Control", "Branching"),
	MERGING("Version Control", "Merging"),
	COLLABORATION("Version Control", "Collaboration"),
	CODE_INTEGRATION("Version Control", "Code integration"),
	STAGING_AREA("Version Control", "Staging area"),
	INDEX("Version Control", "Indexing"),

	// ---------------------- PackageManagement ----------------------
	INSTALLATION("Package Management", "Install software packages"),
	REMOVAL("Package Management", "Remove software packages"),
	AUTOMATIC_DEPENDENCY_RESOUTION("Package Management", "?"),
	UPDATE("Package Management", "Update installed packages"),
	REPOSITORY_MANAGEMENT("Package Management", "Manage software repositories"),
	SEARCH("Package Management", "Search for software packages"),
	DEPENDENCY_MANAGEMENT("Package Management", "Handle package dependencies"),
	PACKAGE_INFO("Package Management", "Get detailed information about packages"),
	GROUP_INSTALLATIONS("Package Management", "Install software groups"),
	LOCAL_REPOSITORY_SUPPORT("Package Management", "Support for local repositories and RPM files"),
	LOGGING("Package Management", "Keep logs of package management activities"),
	TRANSACTION_HISTORY("Package Management", "Maintain package management history"),
	COMMAND_LINE("Package Management", ""),
	SCRIPTING("Package Management", "");

	private final String description;

	// Constructor to initialize the description for each feature
	SoftwareFeatures(String category, String description)
	{
		this.description = description;
	}

	// Method to get the description of the feature
	public String getDescription()
	{
		return description;
	}
}
