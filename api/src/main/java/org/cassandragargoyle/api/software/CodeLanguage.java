/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.software;

/**
 * Class CodeLanguage
 * @author kurc
 * @since 2024-11-28
 */
public enum CodeLanguage
{
	C("C", "Imperative, Procedural"),
	C_PLUS_PLUS("C++", "Object-oriented, Procedural"),
	C_SHARP("C#", "Object-oriented, Class-based"),
	HASKELL("Haskell", "Functional"),
	JAVA("Java", "Object-oriented, Class-based"),
	JAVASCRIPT("JavaScript", "Scripting, Event-driven"),
	PYTHON("Python", "Interpreted, Object-oriented"),
	SWIFT("Swift", "Compiled, Object-oriented"),
	RUBY_INTERPRED("Ruby", "Interpreted, Object-oriented"),
	R("R", "Statistical Computing"),
	GO("Go", "Compiled, Concurrent"),
	PHP("PHP", "Server-side scripting"),
	KOTLIN("Kotlin", "Object-oriented, Functional"),
	TYPESCRIPT("TypeScript", "Superset of JavaScript, Static types"),
	PERL("Perl", "Interpreted, Scripting"),
	LUA("Lua", "Scripting, Lightweight"),
	SQL("SQL", "Query language for databases"),
	RUBY("Ruby", "Object-oriented, Dynamic");

	private final String name;

	private final String description;

	// Constructor for the enum
	CodeLanguage(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	// Getter for name
	public String getName()
	{
		return name;
	}

	// Getter for description
	public String getDescription()
	{
		return description;
	}

	@Override
	public String toString()
	{
		return name + " (" + description + ")";
	}
}
