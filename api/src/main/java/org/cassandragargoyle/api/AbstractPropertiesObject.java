/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

import java.beans.PropertyChangeListener;

/**
 * Class AbstractPropertiesObject
 * @author kurc
 * @since 2024-11-23
 */
public class AbstractPropertiesObject
{
	protected final Properties properties = new Properties();

	// Method to add listener
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		properties.addPropertyChangeListener(listener);
	}

	// Method to remove listener
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		properties.removePropertyChangeListener(listener);
	}

}
