/*
 * SPDX-License-Identifier: MIT
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
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
 * Utility class for system-level operations: screen properties,
 * external process execution, and running process detection
 *
 * <p>
 * The screen methods use AWT. In a headless environment (a server, a CI build, or
 * {@code -Djava.awt.headless=true}) {@link #getDefaultScreenDeviceScaleFactor()} falls back to
 * {@code 1.0}, but {@link #getScreenResolution()} throws {@link HeadlessException}
 *
 * <p>
 * The process methods start the command directly, without a shell, and read only its
 * standard output
 *
 * @author Zdenek
 * @since 2026-03-19
 */
public class SystemUtil
{
	private static final Logger LOG = LogFactory.getLogger(SystemUtil.class);

	/** Cached scale factor of the default screen, -1.0 until the first call */
	private static double screenScaleFactor = -1.0;

	/** Prevents instances, the class has only static methods */
	private SystemUtil()
	{
		throw new IllegalStateException("Utility class.");
	}

	/**
	 * Reads a system property as a {@code double}, or returns a default value
	 *
	 * <p>
	 * Uses {@link Double#parseDouble(String)}, so spaces around the number are allowed and the
	 * decimal separator is always a dot: {@code "1,5"} gives the default value
	 *
	 * @param propertyName name of the system property, for example set with {@code -Dname=1.5}
	 * @param dflt         value to return when the property is missing, empty, or not a number
	 * @return the parsed number or {@code dflt}
	 */
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

	/**
	 * Returns the scale factor of the default screen, computed on the first call and cached
	 *
	 * <p>
	 * The value is {@code 1.0} for 100 % and {@code 2.0} for 200 % (HiDPI). Call
	 * {@link #refreshDefaultScreenDeviceScaleFactor()} after the screen settings change
	 *
	 * @return the horizontal scale factor, {@code 1.0} in a headless environment
	 */
	public static double getDefaultScreenDeviceScaleFactor()
	{
		if (screenScaleFactor == -1.0)
		{
			return refreshDefaultScreenDeviceScaleFactor();
		}
		return screenScaleFactor;
	}

	/**
	 * Reads the scale factor of the default screen again and caches it
	 *
	 * <p>
	 * In a headless environment the {@link HeadlessException} is reported with
	 * {@link Exceptions#printStackTrace(Throwable)}, which logs it at the {@code SEVERE} level,
	 * and the factor is set to {@code 1.0}
	 *
	 * @return the horizontal scale factor, {@code 1.0} in a headless environment
	 */
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

	/**
	 * Returns the screen resolution in dots per inch
	 *
	 * <p>
	 * A value of 10 or less, or of 24576 or more, is not plausible and is replaced with the
	 * common default of 96 DPI
	 *
	 * @return the resolution in DPI
	 * @throws HeadlessException in a headless environment
	 */
	public static int getScreenResolution()
	{
		int screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
		if (screenResolution <= 10 || screenResolution >= 24576)
		{
			screenResolution = 96;
		}
		return screenResolution;
	}

	/**
	 * Runs a command, waits until it ends, and returns its standard output
	 *
	 * <p>
	 * The command is started with {@link Runtime#exec(String)}: it is split at spaces and run
	 * without a shell, so quotes, pipes ({@code |}), redirection, and wildcards ({@code *})
	 * have no special meaning, and an argument cannot contain a space. A separate thread reads
	 * the output while the command runs, so a command with a long output does not block on
	 * a full pipe
	 *
	 * <p>
	 * The error output and the exit code are ignored: a command that fails still returns what
	 * it printed. Every byte of the output becomes one character, so only ASCII output is read
	 * correctly; UTF-8 text such as {@code čaj} comes back garbled
	 *
	 * @param command command and its arguments separated by spaces, for example {@code "pgrep -f java"}
	 * @return the standard output, or {@code null} when the command cannot be started or the
	 *         waiting thread is interrupted; the failure is logged at the {@code FINER} level
	 */
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

	/**
	 * Counts the running processes of a program
	 *
	 * <p>
	 * On Windows it runs {@code query process <name>} and counts the output lines without the
	 * header line. On other systems it runs {@code pgrep -f <name>} and counts the output
	 * lines; {@code -f} matches the whole command line, so a process that only has the name
	 * in its arguments is counted too
	 *
	 * <p>
	 * Note: on Linux, a program with no running process gives 1, not 0, because the empty
	 * output of {@code pgrep} is split into one empty line
	 *
	 * @param executableName name of the program, without spaces
	 * @return the number of processes, 0 when the command cannot be run
	 */
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
	 * Thread that reads a stream to its end and keeps everything it read
	 *
	 * <p>
	 * The output of a process goes through a pipe with a small buffer. When nobody reads the
	 * pipe, the buffer fills up and the process stops. This thread empties the pipe while the
	 * caller waits for the process to end
	 */
	private static class StreamReader extends Thread
	{
		private final InputStream is;

		private final StringWriter sw = new StringWriter();

		/**
		 * Creates a reader for a stream, call {@link #start()} to begin reading
		 *
		 * @param is stream to read, usually {@link Process#getInputStream()}
		 */
		StreamReader(InputStream is)
		{
			this.is = is;
		}

		/** Reads the stream byte by byte until it ends, a read error ends it early */
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

		/**
		 * Returns what was read, call it after {@link #join()}
		 *
		 * @return the text read so far, one character per byte
		 */
		public String getResult()
		{
			return sw.toString();
		}
	}

}
