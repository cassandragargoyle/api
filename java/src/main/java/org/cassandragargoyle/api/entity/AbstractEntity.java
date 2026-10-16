/*
 *  SPDX-License-Identifier: MIT
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.entity;

import org.cassandragargoyle.api.Constants;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.cassandragargoyle.api.BaseClass;
import org.cassandragargoyle.api.Properties;

/**
 * Base class of entities that keep their state in a {@link Properties} bag
 *
 * <p>
 * Every attribute of the entity, including its name, its external identifier
 * and its deleted flag, is stored as a named property. A subclass adds typed
 * accessors on top of the generic {@code getProperty} / {@code setProperty}
 * methods instead of its own fields.
 *
 * <p>
 * Properties with a sub-key are stored under the composite key
 * {@code key + "_" + subKey}, for example {@code label_en}. The entity can
 * also have a parent entity, which is the only state kept outside the
 * property bag
 *
 * @author Zdenek
 * @since 2024-10-24
 */
public abstract class AbstractEntity extends BaseClass implements Entity
{
	//TODO:ZK Move to configuration
	/** Format of date and time values that the entity writes */
	protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/** Format of date and time values that the entity reads from strings */
	protected static final DateTimeFormatter FORMATTER_FOR_STRING_INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/** Property bag that holds the whole state of the entity except the parent */
	protected Properties properties = new Properties();

	//TODO:ZK Move to properties
	Entity parent;

	/**
	 * Creates an entity and stores its name in the {@code name} property
	 *
	 * @param name name of the entity, can be {@code null}
	 */
	public AbstractEntity(String name)
	{
		setProperty("name", name);
	}

	/**
	 * Returns the name of the entity, stored in the {@code name} property
	 *
	 * @return the name, or {@code null} when it is not set
	 */
	@Override
	public String getName()
	{
		return getPropertyStr("name");
	}

	/**
	 * Returns the {@link org.cassandragargoyle.api.Property} wrapper stored under the key, not the raw value
	 *
	 * <p>
	 * Use {@link #getPropertyStr(String)} to get the value as a string
	 *
	 * @param key property key
	 * @return the property wrapper, or {@code null} when the key is missing
	 */
	@Override
	public Object getProperty(String key)
	{
		return properties.get(key);
	}

	/**
	 * Returns the property stored under the key, or a default value when the key is missing
	 *
	 * <p>
	 * For an existing key the result is the {@link org.cassandragargoyle.api.Property}
	 * wrapper, as in {@link #getProperty(String)}, not the raw value
	 *
	 * @param key          property key
	 * @param defaultValue value returned when the key is missing
	 * @return the property wrapper, or {@code defaultValue}
	 */
	public Object getPropertyOrDefault(String key, String defaultValue)
	{
		if (properties.containsKey(key))
		{
			return properties.get(key);
		}
		return defaultValue;
	}

	/**
	 * Returns the value of a property as a string
	 *
	 * @param key property key
	 * @return the value as a string, or {@code null} when the key is missing or the value is {@code null}
	 */
	@Override
	public String getPropertyStr(String key)
	{
		Object p = getProperty(key);
		if (p != null)
		{
			return asString(p);
		}
		return null;
	}

	/**
	 * Returns the value of a property with a sub-key as a string
	 *
	 * @param key    property key
	 * @param subKey sub-key, joined to the key as {@code key + "_" + subKey}
	 * @return the value as a string, or {@code null} when the property is missing
	 */
	public String getPropertyByKeyAndSubKeyStr(String key, String subKey)
	{
		String fullKey = key + "_" + subKey;
		return getPropertyStr(fullKey);
	}

	/**
	 * Stores a string value of a property with a sub-key
	 *
	 * @param key    property key
	 * @param subKey sub-key, joined to the key as {@code key + "_" + subKey}
	 * @param value  new value, can be {@code null}
	 */
	public void setPropertyByKeyAndSubKeyStr(String key, String subKey, String value)
	{
		String fullKey = key + "_" + subKey;
		setProperty(fullKey, value);
	}

	/**
	 * Stores a property value and replaces the previous value
	 *
	 * @param key property key
	 * @param val new value, can be {@code null}
	 */
	public void setProperty(String key, Object val)
	{
		properties.put(key, val);
	}

	/**
	 * Stores a value of a property with a sub-key
	 *
	 * @param key    property key
	 * @param subKey sub-key, joined to the key as {@code key + "_" + subKey}
	 * @param val    new value, can be {@code null}
	 */
	public void setProperty(String key, String subKey, Object val)
	{
		properties.put(key + "_" + subKey, val);
	}

	/**
	 * Returns the identifier of the entity in an external source, stored in the {@code xml:id} property
	 *
	 * @return the external identifier, or {@code null} when it is not set
	 */
	public String getExternalId()
	{
		//TODO:ZK Refactor to use a universal external ID mechanism
		return getPropertyStr("xml:id");
	}

	/**
	 * Returns a list property with elements of the given type
	 *
	 * @param <T>         element type
	 * @param name        property key
	 * @param elementType class of the elements
	 * @return the typed list, see {@link Properties#getTypedListProperty(String, Class)}
	 */
	public <T> List<T> getTypedListProperty(String name, Class<T> elementType)
	{
		return properties.getTypedListProperty(name, elementType);
	}

	/**
	 * Tells whether the entity is marked as deleted
	 *
	 * @return the value of the {@link Constants#PROP_DELETED} property, {@code false} when it is not set
	 */
	@Override
	public boolean isDeleted()
	{
		return properties.getBooleanOrDefault(Constants.PROP_DELETED, false);
	}

	/**
	 * Marks the entity as deleted or not deleted, the entity itself is not removed
	 *
	 * @param deleted new value of the {@link Constants#PROP_DELETED} property
	 */
	public void setDeleted(boolean deleted)
	{
		properties.set(Constants.PROP_DELETED, deleted);
	}

	/**
	 * Stores the identifier of the entity in an external source in the {@code xml:id} property
	 *
	 * @param externalId external identifier, can be {@code null}
	 */
	public void setExternalId(String externalId)
	{
		//TODO:ZK Refactor to use a universal external ID mechanism
		properties.set("xml:id", externalId);
	}

	/**
	 * Copies all properties of another bag into this entity and replaces the values of the same keys
	 *
	 * @param properties properties to copy
	 */
	public void putPropertiesAll(Properties properties)
	{
		this.properties.putAll(properties);
	}

	/**
	 * Sets the parent entity, for example the container that holds this entity
	 *
	 * @param parent parent entity, or {@code null} for a top-level entity
	 */
	public void setParent(Entity parent)
	{
		this.parent = parent;
	}

	/**
	 * Returns the parent entity
	 *
	 * @return the parent entity, or {@code null} for a top-level entity
	 */
	public Entity getParent()
	{
		return parent;
	}

	/**
	 * Tells whether a property with the given key exists, also when its value is {@code null}
	 *
	 * @param name property key
	 * @return {@code true} when the key exists
	 */
	public boolean hasProperty(String name)
	{
		return properties.containsKey(name);
	}
}
