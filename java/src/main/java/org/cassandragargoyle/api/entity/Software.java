/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import org.cassandragargoyle.api.software.SoftwareFeatures;
import org.cassandragargoyle.api.software.SoftwareCategory;
import java.util.List;
import java.util.Map;
import org.cassandragargoyle.api.software.CodeLanguage;
import org.cassandragargoyle.api.software.OSType;

/**
 * Interface SoftwareManager
 * 
 * There are many English terms used for software. These terms can vary depending on context or specific area of use.
 * Here is an overview of common terms:
 * 
 * Software – general term for computer programs.
 * Application – program designed for a specific task or user need (e.g., text processor, web browser).
 * Program – set of instructions that perform a specific task.
 * App – abbreviation for "application", often used for mobile applications.
 * System Software – software that ensures hardware operation and basic computer functions
 * (e.g., operating system).
 * Utility – small program that performs a simple task (e.g., file manager, antivirus program).
 * Tool – instrument that serves a specific technical purpose (e.g., developer tools).
 * Platform – software environment in which applications run (e.g., Windows, Linux).
 * Framework – predefined set of libraries and rules to facilitate application development (e.g., .NET, Django).
 * Library – set of pre-written code and functions that can be used in various programs (e.g., Boost, jQuery).
 * Module – part of a program that performs a specific task and is separable from the rest of the system.
 * Executable – executable file that can be directly executed by a computer (e.g., .exe files).
 * Script – shorter program that is usually executed in an interpreter (e.g., Python, Bash).
 * Middleware – software that mediates communication between other programs or system parts.
 * Firmware – special software stored on a hardware device that enables its functionality.
 * Suite – set of interconnected applications (e.g., Microsoft Office Suite).
 * Engine – software that performs specific tasks, often in games or databases (e.g., game engine, database engine).
 * Component – part of software that can be used in various contexts.
 * Plugin / Add-on – extension or supplement that adds new features to existing software.
 * Beta / Alpha Software – preliminary software versions used for testing before final release.
 * Prototype – first version of software that serves to test a concept.
 * Client / Server Software – software that communicates in a client-server architecture.
 *
 * @author Zdenek
 * @since 2024-11-05
 */
public interface Software extends Entity
{
	/**
	 * Check if is software instaled on local platform (operating system, image or container).
	 * @return
	 */
	boolean isInstalled(Object checkMethod);

	void install();

	List<Version> getVersions();

	SoftwareFeatures[] getFeatures();

	SoftwareCategory[] getCategories();

	OSType[] getSupportedOperatingSystems();

	Map<String, String> getSourceCodeUrl();

	String getInstallScript(Platform platform, CodeLanguage language);
}
