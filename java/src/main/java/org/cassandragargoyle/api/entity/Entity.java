/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

/**
 * Basic contract of all CassandraGargoyle entities: a named object with
 * properties that can be marked as deleted
 *
 * <p>
 * Entities are read through named properties. {@link AbstractEntity} is the
 * base implementation that keeps the whole state in a property bag; more
 * specific contracts such as {@link Node} extend this interface
 *
 * @author Zdenek
 * @since 2024-10-24
 */
public interface Entity
{
	/**
	 * Returns the name of the entity
	 *
	 * @return the name, or {@code null} when it is not set
	 */
	String getName();

	/**
	 * Tells whether the entity is marked as deleted
	 *
	 * <p>
	 * A deleted entity is only marked, it is not removed from its container
	 *
	 * @return {@code true} when the entity is marked as deleted
	 */
	boolean isDeleted();

	/**
	 * Returns the property stored under the key
	 *
	 * <p>
	 * The type of the result depends on the implementation.
	 * {@link AbstractEntity} returns the {@link org.cassandragargoyle.api.Property}
	 * wrapper, not the raw value
	 *
	 * @param key property key
	 * @return the property, or {@code null} when the key is missing
	 */
	Object getProperty(String key);

	/**
	 * Returns the value of a property as a string
	 *
	 * @param key property key
	 * @return the value as a string, or {@code null} when the key is missing or the value is {@code null}
	 */
	String getPropertyStr(String key);

	//protected void initialize() throws Error
}
