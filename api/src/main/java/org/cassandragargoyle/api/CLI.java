/*
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api;

/**
 * Class CLI
 * @author kurc
 * @since 2024-10-31
 */
public abstract class CLI implements Runnable
{
	protected String[] args;

	public CLI()
	{

	}

	public CLI(String[] args)
	{
		this.args = args;
	}

}
