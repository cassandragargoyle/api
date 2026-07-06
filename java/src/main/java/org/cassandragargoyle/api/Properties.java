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

/**
 * Mutable, name-keyed bag of {@link Property} values backing the entity
 * property model.
 *
 * <p>
 * Each entry is a {@link Property} wrapping an arbitrary {@code Object} value.
 * The bag tracks three pieces of state beyond the map itself:
 * <ul>
 * <li>a "modified" flag flipped on every write (see {@link #isModified()})</li>
 * <li>a cached count of non-null values (refreshed lazily on writes that
 *     could change it)</li>
 * <li>a {@link PropertyChangeSupport} that fires Java-Beans
 *     {@code PropertyChange} events on every {@link #set(String, Object)}
 *     and {@link #addToList(String, Entity)} mutation</li>
 * </ul>
 *
 * <p>
 * <b>Batch updates.</b> {@link #beginUpdate(Object, boolean)} +
 * {@link #updateProperty(String, String)} + {@link #commitUpdate()} provide
 * a transactional flow for changing several properties together. Only
 * {@link #beginUpdate(Object, boolean)} and {@link #commitUpdate()} are
 * {@code synchronized}; ordinary {@link #set(String, Object)} /
 * {@link #get(String)} are <b>not</b> thread-safe and concurrent access from
 * multiple threads needs external synchronization by the caller.
 *
 * <p>
 * <b>Type-safe accessors.</b> {@link #getTypedProperty(String, Class)} and
 * {@link #getTypedListProperty(String, Class)} cast the stored value to the
 * requested type and throw {@link ClassCastException} on mismatch. The
 * {@code getXxxOrDefault} variants return the default for missing keys but
 * propagate {@link ClassCastException} for present-but-wrong-type values.
 *
 * @author Zdenek
 */
public class Properties
{
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private Map<String, Property> properties = new HashMap<>();

	private boolean isModified;

	private PropertyUpdateManager propertyUpdateManager;

	private int sizeNonNull;

	/**
	 * Returns {@code true} iff the bag contains an entry for {@code name}
	 * and that entry's value is non-null. A registered property whose value
	 * is {@code null} reports {@code false}.
	 *
	 * @param name property key
	 * @return {@code true} if a non-null value is associated with {@code name}
	 */
	public boolean hasProperty(String name)
	{
		return properties.containsKey(name) && properties.get(name).getValue() != null;
	}

	/**
	 * Returns a live view of all {@link Property} wrappers currently stored,
	 * including those whose value is {@code null}.
	 *
	 * @return collection of property wrappers; backed by the internal map
	 */
	public Collection<Property> all()
	{
		return properties.values();
	}

	/**
	 * Returns the {@link Property} wrapper associated with {@code name},
	 * or {@code null} if no such entry exists.
	 *
	 * @param name property key
	 * @return the wrapper, or {@code null} if absent
	 */
	public Property get(String name)
	{
		return properties.get(name);
	}

	/**
	 * Returns the raw value associated with {@code name}, unwrapped from its
	 * {@link Property} container.
	 *
	 * @param name property key
	 * @return the stored value, or {@code null} if absent or null-valued
	 */
	public Object getValue(String name)
	{
		var p = properties.get(name);
		return p == null ? null : p.getValue();
	}

	/**
	 * Returns the {@link Property#toString()} representation of the value
	 * associated with {@code name}.
	 *
	 * @param name property key
	 * @return the string form of the value, or {@code null} if absent
	 */
	public String getString(String name)
	{
		return properties.get(name) == null ? null : properties.get(name).toString();
	}

	/**
	 * Returns the string form of the value associated with {@code name}, or
	 * {@code defaultValue} if the entry is absent or has a {@code null}
	 * string form.
	 *
	 * @param name         property key
	 * @param defaultValue value to return when the key is absent or null-valued
	 * @return the string form or the default
	 */
	public String getStringOrDefault(String name, String defaultValue)
	{
		return properties.get(name) == null || properties.get(name).toString() == null ? defaultValue : properties.get(name).toString();
	}

	/**
	 * Returns the {@link Integer} value associated with {@code name}, or
	 * {@code defaultValue} if the entry is absent.
	 *
	 * @param name         property key
	 * @param defaultValue value to return when the key is absent
	 * @return the value as {@code Integer} or the default
	 * @throws ClassCastException if a value is present but is not an {@link Integer}
	 */
	public Integer getIntOrDefault(String name, Integer defaultValue)
	{
		return properties.get(name) == null ? defaultValue : (Integer) properties.get(name).getValue();
	}

	/**
	 * Returns the {@link Boolean} value associated with {@code name}, or
	 * {@code defaultValue} if the entry is absent.
	 *
	 * @param name         property key
	 * @param defaultValue value to return when the key is absent
	 * @return the value as {@code Boolean} or the default
	 * @throws ClassCastException if a value is present but is not a {@link Boolean}
	 */
	public Boolean getBooleanOrDefault(String name, Boolean defaultValue)
	{
		return properties.get(name) == null ? defaultValue : (Boolean) properties.get(name).getValue();
	}

	/**
	 * Returns the string form of the value associated with {@code name}, or
	 * {@code defaultValue} if the entry is absent or its string form is empty.
	 *
	 * <p>
	 * Differs from {@link #getStringOrDefault(String, String)} in treating an
	 * empty string as "missing" and substituting the default.
	 *
	 * @param name         property key
	 * @param defaultValue value to return when the key is absent or empty
	 * @return the non-empty string form or the default
	 */
	public String getOrDefault(String name, String defaultValue)
	{
		return hasPropertyValue(name) ? properties.get(name).toString() : defaultValue;
	}

	/**
	 * Returns {@code true} iff the bag contains an entry for {@code name}
	 * with a non-null, non-empty string form.
	 *
	 * @param name property key
	 * @return {@code true} if the entry exists and its string form is non-empty
	 */
	protected boolean hasPropertyValue(String name)
	{
		return properties.containsKey(name) && properties.get(name) != null && !StringUtil.isNullOrEmpty(properties.get(name).toString());
	}

	/**
	 * Sets the property identified by {@code name} to {@code value}.
	 *
	 * <p>
	 * If the key is new, a fresh {@link Property} is created. If it already
	 * exists, the value is replaced only when it actually differs from the
	 * current one (null-aware equals), and a {@code PropertyChange} event
	 * with the old and new values is fired. The "modified" flag is set on
	 * any change.
	 *
	 * @param name  property key
	 * @param value new value (may be {@code null})
	 */
	public void set(String name, Object value)
	{
		Property prop = get(name);
		if (prop == null)
		{
			prop = new Property(name, value);
			properties.put(name, prop);
			isModified = true;
			sizeNonNull = getNonNullSize();
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
					sizeNonNull = getNonNullSize();
				}
				support.firePropertyChange(name, oldValue, value);
			}
		}
	}

	/**
	 * Removes the entry for {@code name}.
	 *
	 * <p>
	 * Does not fire a {@code PropertyChange} event and does not update the
	 * cached non-null size. Both behaviours diverge from
	 * {@link #set(String, Object) set(name, null)} and the asymmetry is
	 * intentional in the existing call sites.
	 *
	 * @param name property key to remove
	 */
	public void remove(String name)
	{
		properties.remove(name);
	}

	private int getNonNullSize()
	{
		int size = 0;
		for (Property p : properties.values())
		{
			size = (p.getValue() == null ? 0 : 1) + size;
		}
		return size;
	}

	/**
	 * Returns a flat {@code String[]} of {@code [name, value, name, value, ...]}
	 * pairs covering every property whose value is non-null.
	 *
	 * <p>
	 * Returns {@code null} (not an empty array) when the bag holds no
	 * non-null values. Iteration order matches the underlying {@link HashMap}
	 * and is therefore not deterministic across JVMs or rehashings.
	 *
	 * @return the flattened name/value pairs, or {@code null} if there are none
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

			// Verify that every element in the list is an instance of elementType
			for (Object item : list)
			{
				if (!elementType.isInstance(item))
				{
					throw new ClassCastException("List element cannot be cast to " + elementType.getName());
				}
			}

			// Safe cast — every element passed the elementType check above
			return (List<T>) list;
		}
		else
		{
			throw new ClassCastException("Property value is not a List");
		}
	}

	/**
	 * Returns whether any mutating operation has been performed on this bag
	 * since construction or the last {@link #setModified(boolean) setModified(false)}.
	 *
	 * @return the dirty flag
	 */
	public boolean isModified()
	{
		return isModified;
	}

	/**
	 * Sets the dirty flag. Typically called with {@code false} after a snapshot
	 * has been persisted, to mark the bag as clean again.
	 *
	 * @param isModified new value of the dirty flag
	 */
	public void setModified(boolean isModified)
	{
		this.isModified = isModified;
	}

	/**
	 * Records a property change inside an active batch update.
	 *
	 * <p>
	 * Must be called between {@link #beginUpdate(Object, boolean)} and
	 * {@link #commitUpdate()}. Outside of a batch the call throws
	 * {@link IllegalArgumentException}.
	 *
	 * @param propertyName property key to update
	 * @param value        new string value
	 * @throws IllegalArgumentException if no batch update is currently active
	 */
	public void updateProperty(String propertyName, String value)
	{
		if (propertyUpdateManager == null)
		{
			throw new IllegalArgumentException("Update cannot be performed because it was not started.");
		}
		propertyUpdateManager.updateProperty(propertyName, value);
	}

	/**
	 * Begins a batch property update — used to merge several related property
	 * changes (e.g. first name + surname together) and apply them atomically
	 * via {@link #commitUpdate()}.
	 *
	 * <p>
	 * Only one batch may be active per bag at a time; opening a second batch
	 * before committing the first throws {@link IllegalArgumentException}.
	 * The method is {@code synchronized} to make begin/commit pairing safe
	 * across threads.
	 *
	 * @param updateObject opaque tag identifying the originator of the batch
	 * @param force        if {@code true}, applies recorded changes even when
	 *                     the target properties already hold a non-empty value;
	 *                     if {@code false}, the entire batch is silently
	 *                     skipped at commit time when any target is non-empty
	 * @throws IllegalArgumentException if a batch is already active
	 */
	public synchronized void beginUpdate(Object updateObject, boolean force)
	{
		if (propertyUpdateManager != null)
		{
			throw new IllegalArgumentException("Update cannot be started because another object is currently being updated.");
		}
		propertyUpdateManager = new PropertyUpdateManager(updateObject, force);
	}

	/**
	 * Applies (or discards) the recorded batch and clears the active update.
	 *
	 * <p>
	 * If no batch is active this is a no-op. If the batch was opened with
	 * {@code force=false} and any target property is already non-empty, the
	 * entire batch is discarded; otherwise every queued change is applied via
	 * {@link #set(String, Object)}, firing change events as usual.
	 */
	public synchronized void commitUpdate()
	{
		if (propertyUpdateManager != null)
		{
			propertyUpdateManager.commitUpdate();
		}
		propertyUpdateManager = null;
	}

	/**
	 * Bulk-copies entries from another {@link Properties} bag into this one.
	 *
	 * <p>
	 * Uses {@link Map#putAll(Map)} semantics — incoming keys overwrite local
	 * ones and no {@code PropertyChange} events are fired (asymmetric vs
	 * {@link #set(String, Object)}).
	 *
	 * @param properties source bag; ignored if {@code null} or empty
	 */
	public void putAll(Properties properties)
	{
		if (properties != null && !properties.isEmpty())
		{
			this.properties.putAll(properties.properties);
		}
		sizeNonNull = getNonNullSize();
	}

	/**
	 * Returns {@code true} iff the bag holds no entries (independent of
	 * whether existing entries have non-null values).
	 *
	 * @return {@code true} if the underlying map is empty
	 */
	public boolean isEmpty()
	{
		return properties.isEmpty();
	}

	/**
	 * Returns {@code true} iff a property with the given key exists, even
	 * when its value is {@code null}.
	 *
	 * @param key property key
	 * @return {@code true} if the key is registered
	 */
	public boolean containsKey(String key)
	{
		return properties.containsKey(key);
	}

	/**
	 * Map-style alias for {@link #set(String, Object)}.
	 *
	 * @param key property key
	 * @param val new value (may be {@code null})
	 */
	public void put(String key, Object val)
	{
		set(key, val);
	}

	/**
	 * Registers a listener that receives a {@code PropertyChange} event for
	 * every {@link #set(String, Object)} that actually changes a value and
	 * for every {@link #addToList(String, Entity)}.
	 *
	 * <p>
	 * Note that {@link #remove(String)}, {@link #putAll(Properties)} and
	 * batch-commit changes do <b>not</b> fire events through this listener.
	 *
	 * @param listener listener to register
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		support.addPropertyChangeListener(listener);
	}

	/**
	 * Unregisters a previously registered {@link PropertyChangeListener}.
	 *
	 * @param listener listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		support.removePropertyChangeListener(listener);
	}

	/**
	 * Appends an {@link Entity} to a list-valued property, creating the list
	 * on first call.
	 *
	 * <p>
	 * The property's value is assumed to be (or absent and therefore
	 * initialized as) an {@link ArrayList} of {@link Entity}; an unchecked
	 * cast is performed if the entry already exists, so callers must not mix
	 * other value types under the same key. Fires a {@code PropertyChange}
	 * event whose new value is the (now-extended) list.
	 *
	 * @param key    property key
	 * @param entity entity to append
	 */
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
		support.firePropertyChange(key, null, list);
	}

	/**
	 * Holds the in-flight changes of a single batch update opened via
	 * {@link Properties#beginUpdate(Object, boolean)}.
	 */
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
				// Decide whether the batch may be applied
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

	/** Single name/value change recorded inside a batch update. */
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
