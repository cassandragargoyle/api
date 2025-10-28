/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.cassandragargoyle.api.entity.Entity;
import org.cassandragargoyle.api.util.StringUtil;

public class Properties
{
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private Map<String, Property> properties = new HashMap<>();

	private boolean isModified;

	private PropertyUpdateManager propertyUpdateManager;

	private int sizeNonNull;

	public boolean hasProperty(String name)
	{
		return properties.containsKey(name) && properties.get(name).getValue() != null;
	}

	public Collection<Property> all()
	{
		return properties.values();
	}

	public Property get(String name)
	{
		return properties.get(name);
	}

	public Object getValue(String name)
	{
		var p = properties.get(name);
		return p == null ? null : p.getValue();
	}

	public String getString(String name)
	{
		return properties.get(name) == null ? null : properties.get(name).toString();
	}

	public String getStringOrDefault(String name, String defaultValue)
	{
		return properties.get(name) == null || properties.get(name).toString() == null ? defaultValue : properties.get(name).toString();
	}

	public Integer getIntOrDefault(String name, Integer defaultValue)
	{
		return properties.get(name) == null ? defaultValue : (Integer) properties.get(name).getValue();
	}

	public Boolean getBooleanOrDefault(String name, Boolean defaultValue)
	{
		return properties.get(name) == null ? defaultValue : (Boolean) properties.get(name).getValue();
	}

	public String getOrDefault(String name, String defaultValue)
	{
		return hasPropertyValue(name) ? properties.get(name).toString() : defaultValue;
	}

	protected boolean hasPropertyValue(String name)
	{
		return properties.containsKey(name) && properties.get(name) != null && !StringUtil.isNullOrEmpty(properties.get(name).toString());
	}

	public void set(String name, Object value)
	{
		Property prop = get(name);
		if (prop == null)
		{
			prop = new Property(name, value);
			properties.put(name, prop);
			isModified = true;
			sizeNonNull = _getNonNullSize();
			support.firePropertyChange(name, null, value);
		}
		else
		{
			if ((prop.getValue() == null && value != null) || (prop.getValue() != null && !prop.getValue().equals(value)))
			{
				var oldValue = prop.getValue();
				prop.set(value);
				isModified = true;
				if (value == null)
				{
					sizeNonNull = _getNonNullSize();
				}
				support.firePropertyChange(name, oldValue, value);
			}
		}
	}

	public void remove(String name)
	{
		properties.remove(name);
	}

	private int _getNonNullSize()
	{
		int size = 0;
		for (Property p : properties.values())
		{
			size = (p.getValue() == null ? 0 : 1) + size;
		}
		return size;
	}

	/**
	 * The function returns an array with property names and values.
	 * @return
	 */
	public String[] getAsArray()
	{
		String[] array = null;
		if (sizeNonNull > 0)
		{
			array = new String[sizeNonNull * 2];
			int index = 0;
			for (Property p : properties.values())
			{
				if (p.getValue() != null)
				{
					array[index++] = p.getName();
					array[index++] = p.toString();
				}
			}
		}
		return array;
	}

	/**
	 * Retrieves a property from the given map and casts it to the specified target type.
	 *
	 * @param name       the key to look up the property in the map
	 * @param targetType the class of the target type to cast to
	 * @param <T>        the type to cast to
	 * @return the casted property value, or null if the property does not exist
	 * @throws ClassCastException if the property cannot be cast to the target type
	 */
	public <T> T getTypedProperty(String name, Class<T> targetType)
	{
		Property property = properties.get(name);

		if (property == null)
		{
			return null;
		}

		if (targetType.isInstance(property.getValue()))
		{
			return targetType.cast(property.getValue());
		}
		else
		{
			throw new ClassCastException("Property value cannot be cast to " + targetType.getName());
		}
	}

	/**
	 * Retrieves a property from the map and casts it to a list of the specified element type.
	 *
	 * @param name        the key to look up the property in the map
	 * @param elementType the class of the list's element type to cast to
	 * @param <T>         the type of the elements in the list
	 * @return the casted list, or null if the property does not exist
	 * @throws ClassCastException if the property is not a list or contains elements of a different type
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getTypedListProperty(String name, Class<T> elementType)
	{
		Property property = properties.get(name);

		if (property == null)
		{
			return null;
		}

		if (property.getValue() instanceof List<?>)
		{
			List<?> list = (List<?>) property.getValue();

			// Kontrola, zda všechny prvky v seznamu jsou instancemi elementType
			for (Object item : list)
			{
				if (!elementType.isInstance(item))
				{
					throw new ClassCastException("List element cannot be cast to " + elementType.getName());
				}
			}

			// Bezpečné přetypování
			return (List<T>) list;
		}
		else
		{
			throw new ClassCastException("Property value is not a List");
		}
	}

	public boolean isModified()
	{
		return isModified;
	}

	public void setModified(boolean isModified)
	{
		this.isModified = isModified;
	}

	public void updateProperty(String propertyName, String value)
	{
		if (propertyUpdateManager == null)
		{
			throw new IllegalArgumentException("Update cannot be performed because it was not started.");
		}
		propertyUpdateManager.updateProperty(propertyName, value);
	}

	/**
	 * Implemented for merging property settings, for example, when updating contact properties such as first name and surname together.
	 * @param updateObject
	 * @param force
	 */
	public synchronized void beginUpdate(Object updateObject, boolean force)
	{
		if (propertyUpdateManager != null)
		{
			throw new IllegalArgumentException("Update cannot be started because another object is currently being updated.");
		}
		propertyUpdateManager = new PropertyUpdateManager(updateObject, force);
	}

	public synchronized void commitUpdate()
	{
		if (propertyUpdateManager != null)
		{
			propertyUpdateManager.commitUpdate();
		}
		propertyUpdateManager = null;
	}

	public void putAll(Properties properties)
	{
		if (properties != null && !properties.isEmpty())
		{
			this.properties.putAll(properties.properties);
		}
		sizeNonNull = _getNonNullSize();
	}

	public boolean isEmpty()
	{
		return properties.isEmpty();
	}

	public boolean containsKey(String key)
	{
		return properties.containsKey(key);
	}

	public void put(String key, Object val)
	{
		set(key, val);
	}

	// Method to add listener
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		support.addPropertyChangeListener(listener);
	}

	// Method to remove listener
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		support.removePropertyChangeListener(listener);
	}

	public void addToList(String key, Entity entity)
	{
		ArrayList<Entity> list;
		if (!properties.containsKey(key))
		{
			list = new ArrayList<>();
			properties.put(key, new Property(key, list));
		}
		else
		{
			list = (ArrayList<Entity>) properties.get(key).getValue();
		}
		list.add(entity);
		//TODO:
		support.firePropertyChange(key, null, null);
	}

	private class PropertyUpdateManager
	{
		private boolean force;

		private Object updateObject;

		List<PropertyChange> propertyChanges;

		PropertyUpdateManager(Object updateObject, boolean force)
		{
			this.updateObject = updateObject;
			this.force = force;
			propertyChanges = new ArrayList<>();
		}

		public synchronized void updateProperty(String propertyName, String value)
		{
			if (this.updateObject == null)
			{
				throw new IllegalArgumentException("Update cannot be performed because it was not started.");
			}
			propertyChanges.add(new PropertyChange(propertyName, value));
		}

		public synchronized void commitUpdate()
		{
			if (propertyChanges != null)
			{
				boolean canUpdate = true;
				//Check if can update
				if (!force)
				{
					for (PropertyChange change : propertyChanges)
					{
						if (properties.get(change.propertyName) != null && !properties.get(change.propertyName).isEmpty())
						{
							canUpdate = false;
							break;
						}
					}
				}
				if (canUpdate)
				{
					for (PropertyChange change : propertyChanges)
					{
						set(change.propertyName, change.newValue);
					}
				}
			}
			updateObject = null;
		}
	}

	private class PropertyChange
	{
		private String propertyName;

		private Object newValue;

		PropertyChange(String propertyName, Object newValue)
		{
			this.propertyName = propertyName;
			this.newValue = newValue;
		}
	}
}
