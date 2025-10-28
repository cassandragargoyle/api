/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cassandragargoyle.api.log.LogFactory;
import org.openide.util.Exceptions;

/**
 * Class SystemUtil
 *
 */
public class SystemUtil
{
	private static final Logger LOG = LogFactory.getLogger(SystemUtil.class);

	private static double screenScaleFactor = -1.0;

	private SystemUtil()
	{
		throw new IllegalStateException("Utility class.");
	}

	public static double getDouble(String propertyName, double dflt)
	{
		String property = System.getProperty(propertyName);
		if (property != null && !property.isEmpty())
		{
			try
			{
				return Double.parseDouble(property);
			}
			catch (Exception e)
			{
				// return default
			}
		}
		return dflt;
	}

	public static double getDefaultScreenDeviceScaleFactor()
	{
		if (screenScaleFactor == -1.0)
		{
			return refreshDefaultScreenDeviceScaleFactor();
		}
		return screenScaleFactor;
	}

	public static double refreshDefaultScreenDeviceScaleFactor()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try
		{
			screenScaleFactor = ge.getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleX();
		}
		catch (HeadlessException e)
		{
			Exceptions.printStackTrace(e);
			screenScaleFactor = 1.0;
		}
		return screenScaleFactor;
	}

	public static int getScreenResolution()
	{
		int screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
		if (screenResolution <= 10 || screenResolution >= 24576)
		{
			screenResolution = 96;
		}
		return screenResolution;
	}

	public static String execute(String command)
	{
		try
		{
			Process process = Runtime.getRuntime().exec(command);

			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();

			return reader.getResult();
		}
		catch (Exception e)
		{
			LOG.log(Level.FINER, "Executing command failed due to: {0}.", LogFactory.args(e.getMessage(), e));
		}
		return null;
	}

	public static int countRunningProcesses(String executableName)
	{
		int count = 0;
		if (OsDetector.isWindows())
		{
			String output = execute("query process " + executableName);
			if (output != null)
			{
				output = output.trim();
				String[] lines = output.split("\n");
				count = lines.length - 1;
			}
		}
		else
		{
			String output = execute("pgrep -f " + executableName);
			if (output != null)
			{
				output = output.trim();
				String[] lines = output.split("\n");
				count = lines.length;
			}
		}
		return count;
	}

	/**
	 * Stream reader thread
	 */
	private static class StreamReader extends Thread
	{
		private final InputStream is;

		private final StringWriter sw = new StringWriter();

		public StreamReader(InputStream is)
		{
			this.is = is;
		}

		@Override
		public void run()
		{
			try
			{
				int c;
				while ((c = is.read()) != -1)
				{
					sw.write(c);
				}
			}
			catch (IOException e)
			{
				//ignore
			}
		}

		public String getResult()
		{
			return sw.toString();
		}
	}

}
