/*
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
 * Class AbstractEntity
 * @author kurc
 * @since 2024-10-24
 */
public abstract class AbstractEntity extends BaseClass implements Entity
{
	//TODO: Dát do configurace
	protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	protected static final DateTimeFormatter formatterForStringInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	protected Properties properties = new Properties();

	//TODO:ZK Move to properties
	Entity parent;

	public AbstractEntity(String name)
	{
		setProperty("name", name);
	}

	@Override
	public String getName()
	{
		return getPropertyStr("name");
	}

	// Method to get a property value by key
	@Override
	public Object getProperty(String key)
	{
		return properties.get(key);
	}

	// Method with a default value if the key is missing
	public Object getPropertyOrDefault(String key, String defaultValue)
	{
		if (properties.containsKey(key))
		{
			return properties.get(key);
		}
		return defaultValue;
	}

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

	public String getPropertyByKeyAndSubKeyStr(String key, String subKey)
	{
		String fullKey = key + "_" + subKey;
		return getPropertyStr(fullKey);
	}

	public void setPropertyByKeyAndSubKeyStr(String key, String subKey, String value)
	{
		String fullKey = key + "_" + subKey;
		setProperty(fullKey, value);
	}

	public void setProperty(String key, Object val)
	{
		properties.put(key, val);
	}

	public void setProperty(String key, String subKey, Object val)
	{
		//TODO:Safe connect
		properties.put(key + subKey, val);
	}

	public String getExternalId()
	{
		//TODO:Předělat nějak univerzálně.
		return getPropertyStr("xml:id");
	}

	public <T> List<T> getTypedListProperty(String name, Class<T> elementType)
	{
		return properties.getTypedListProperty(name, elementType);
	}

	@Override
	public boolean isDeleted()
	{
		return properties.getBooleanOrDefault(Constants.PROP_DELETED, false);
	}

	public void setDeleted(boolean deleted)
	{
		properties.set(Constants.PROP_DELETED, deleted);
	}

	public void setExternalId(String externalId)
	{
		//TODO: Univerzálně
		properties.set("xml:id", externalId);
	}

	public void putPropertiesAll(Properties properties)
	{
		this.properties.putAll(properties);
	}

	public void setParent(Entity parent)
	{
		this.parent = parent;
	}

	public Entity getParent()
	{
		return parent;
	}

	public boolean hasProperty(String name)
	{
		return properties.containsKey(name);
	}
}
