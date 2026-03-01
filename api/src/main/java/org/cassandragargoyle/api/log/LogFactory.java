/**
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 * Class LogFactory
 *
 * Message levels will be used in following way:
 * <ul>
 * <li> {@link java.util.logging.Level#CONFIG} not localised message to information about current configuration
 * values</li>
 * <li> {@link java.util.logging.Level#FINEST} not localised message to log finest debugging info like object states.
 * These messages will appear only in log files.</li>
 * <li> {@link java.util.logging.Level#FINER} not localised message to log finer debugging info like method calls. These
 * messages will appear only in log files.</li>
 * <li> {@link java.util.logging.Level#FINE} not localised message to log debugging about executed actions. These
 * messages will appear only in log files.</li>
 * <li> {@link java.util.logging.Level#INFO} localised message to inform the user about succeeded important background
 * actions. These messages will appear on the status bar by default.</li>
 * <li> {@link java.util.logging.Level#WARNING} localised message to warn the user about a bad or suspicious state.
 * These messages will appear on the status bar by default.</li>
 * <li> {@link java.util.logging.Level#SEVERE} localised message to inform the user about an error. These messages will
 * appear in a pop-up dialog by default.</li>
 * </ul>
 *
 * @author kurc
 */
public class LogFactory
{
	// ----------- Loggers Paths in Layer XML -----------
	//
	/**
	 * List of ignored logger.
	 */
	public static final String LOG_IGNOREDLOGGERS = "CassandraGargoyle/Log/IngoredLoggers/";

	/**
	 * List of forced messages.
	 */
	public static final String LOG_FORCEDMESSAGES = "CassandraGargoyle/Log/ForcedMessages/";

	/**
	 * List of shown messages that would by be minimised to status bar by default.
	 */
	public static final String LOG_SHOWNMESSAGES = "CassandraGargoyle/Log/ShownMessages/";

	/**
	 * Creates a logger for the given class. Assumes that a resource bundle
	 * called Bundle exists in the same package. If the bundle does not exist
	 * then the allocated logger will not be able to translate log messages.
	 *
	 * @param clazz class that uses the logger
	 * @return the allocated logger
	 */
	public static Logger getLogger(Class clazz)
	{
		String className = clazz.getName();
		String resourceBundleName = clazz.getPackage().getName() + ".Bundle";
		try
		{
			return Logger.getLogger(className, resourceBundleName);
		}
		catch (MissingResourceException ex)
		{
			return Logger.getLogger(className);
		}
	}

	/**
	 * Logs the message with level FINE, when -J-Dcassandragargoyle.debug=true and logCondition true
	 */
	public static void logDebugWithTrace(String message, boolean logCondition)
	{
		if (logCondition && Boolean.getBoolean("cassandragargoyle.debug"))
		{
			Logger log = Logger.getLogger(LogFactory.class.getName());
			if (log.isLoggable(Level.FINE))
			{
				try
				{
					throw new Exception("Callstack");
				}
				catch (Exception e)
				{
					log.log(Level.FINE, message);
					Exceptions.printStackTrace(e);
				}
			}
		}
	}

	/**
	 * Argument array wrapper method
	 * @param args argument array
	 * @return argument array
	 */
	public static Object[] args(Object... args)
	{
		return args;
	}

	/**
	 * Registers ignored logger into layer.xml.
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(
		{
			ElementType.FIELD
		})
	public @interface IgnoreLoggerForUI
	{
		// no parameters needed
	}

	/**
	 * Registers forced message into layer.xml.
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(
		{
			ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR
		})
	public @interface ForcedLogMessages
	{
		/**
		 * List of message ids
		 * @return message ids
		 */
		String[] value();
	}

	/**
	 * Registers shown log messages into layer.xml.
	 */
	@Retention(RetentionPolicy.SOURCE)
	@Target(
		{
			ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR
		})
	public @interface ShownLogMessages
	{
		/**
		 * List of message ids
		 * @return message ids
		 */
		String[] value();
	}
}
