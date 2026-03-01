/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.software;

import org.cassandragargoyle.api.entity.AbstractEntity;
import org.cassandragargoyle.api.Constants;
import org.cassandragargoyle.api.entity.Software;
import org.cassandragargoyle.api.entity.Version;
import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.cassandragargoyle.api.entity.Platform;
import org.springframework.stereotype.Component;

/**
 * Class SoftwareEntity
 * @author kurc
 * @since 2024-10-24
 */
/**
 * Class SoftwareEntity
 * @author kurc
 * @since 2024-10-24
 */
@Component
public class SoftwareEntity extends AbstractEntity implements Software
{
	private String fullName;

	public SoftwareEntity(String name)
	{
		super(name);
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	@Override
	public SoftwareCategory[] getCategories()
	{
		throw new UnsupportedOperationException("SoftwareEntity.getCategories not supported.");
	}

	@Override
	public OSType[] getSupportedOperatingSystems()
	{
		throw new UnsupportedOperationException("SoftwareEntity.getSupportedOperatingSystems not supported.");
	}

	@Override
	public boolean isInstalled(Object checkMethod)
	{
		//TODO:
		//if(checkMethod == "where")
		return isInstalledByWhere(getName());
		//if(checkMethod == "version")
		//isInstalledByVersion()
	}

	public String getVersion() throws Exception
	{
		var pb = new ProcessBuilder(getName(), "--version");
		pb.redirectErrorStream(true);
		Process process = pb.start();
		int exitCode = process.waitFor();
		if (exitCode == 0)
		{
			return _toString(process.getInputStream());
		}
		/* TODO:
		 * Error: 740
		 *
		 * Elevated permissions are required to run DISM.
		 * Use an elevated command prompt to complete these tasks.
		 * */
		return null;
	}

	private String _toString(InputStream in) throws IOException
	{
		StringBuilder versionOutput = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = reader.readLine()) != null)
		{
			versionOutput.append(line).append("\n");
		}
		return versionOutput.toString();
	}

	protected final boolean isInstalledByWhere(String name)
	{
		try
		{
			//TODO: on linux Process process = Runtime.getRuntime().exec("which msys2");
			Process process = new ProcessBuilder("where", name).start();
			int exitCode = process.waitFor();
			return exitCode == 0;
		}
		catch (IOException | InterruptedException e)
		{
			return false;
		}
	}

	protected final boolean isInstalledByVersion()
	{
		try
		{
			Process process = new ProcessBuilder("wsl", "--version").start();
			int exitCode = process.waitFor();
			return exitCode == 0;
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	public String commandWhere()
	{
		try
		{
			//TODO: on linux Process process = Runtime.getRuntime().exec("which msys2");
			var pb = new ProcessBuilder("where", getName());
			pb.redirectErrorStream(true);
			var process = pb.start();
			int exitCode = process.waitFor();
			if (exitCode == 0)
			{
				return _toString(process.getInputStream());
			}
			return null;
		}
		catch (IOException | InterruptedException e)
		{
			return null;
		}
	}

	@Override
	public SoftwareFeatures[] getFeatures()
	{
		throw new UnsupportedOperationException("SoftwareEntity.getFeatures not supported.");
	}

	@Override
	public void install()
	{
		Platform platform = null; //TODO: getCurrent
		CodeLanguage cl = null; //TODO: podle čeho se vybere ?
		var script = getInstallScript(platform, cl);
	}

	@Override
	public List<Version> getVersions()
	{
		return getTypedListProperty(Constants.PROP_VERSIONS, Version.class);
	}

	@Override
	public String getInstallScript(Platform platform, CodeLanguage language)
	{
		return null;
	}

	protected final void defCommand(String os, String command)
	{
		setPropertyByKeyAndSubKeyStr(Constants.PROP_COMMAND_NAME, os, command);
	}

	protected final void defSourceCode(String source)
	{
		setProperty(Constants.PROP_SOURCE_CODE, source);
	}

	@Override
	public Map<String, String> getSourceCodeUrl()
	{
		return null;
	}

}
