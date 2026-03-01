/**
 * This file is part of CassandraGargoyle Community Project
 * Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import org.apache.commons.lang3.time.FastDateFormat;
import org.cassandragargoyle.api.Places;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 * Class Logging
 *
 * @author kurc
 * @since 2024-10-23
 */
public class Logging
{
	private static final Logger LOG = LogFactory.getLogger(Logging.class);

	public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("dd/MM/yyyy HH:mm:ss.SSS");

	public static final Formatter FORMATTER = new CustomFormatter();

	public static final Filter FILTER = new DefaultLoggerFilter();

	private static String ROOT_LOG = "org.cassandragargoyle.api";

	private static NonCloseHandler fileHandler;

	private static NonCloseHandler streamHandler;

	private static final Map<Throwable, Integer> catchIndex = Collections.synchronizedMap(new WeakHashMap<Throwable, Integer>()); // #190623

	/**
	 * reference to the old error stream
	 */
	private static final PrintStream OLD_ERR = System.err;

	private static ConsoleHandler consoleHandler;

	public static void initialize(String logLevel, boolean logOnConsole, boolean logToFile) throws Exception
	{

		java.util.Properties sysProps = System.getProperties();
		//TODO:ZK nefunguje
		sysProps.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n");

		Logger logger = Logger.getLogger(""); // NOI18N

		Handler[] oldHandlers = logger.getHandlers();
		for (Handler oldHandler : oldHandlers)
		{
			logger.removeHandler(oldHandler);
		}
		if (logOnConsole)
		{
			consoleHandler = new CustomConsoleHandler();
			logger.addHandler(consoleHandler);
		}
		if (logToFile)
		{
			logger.addHandler(_getFileHandler());
		}
		setLevel(Level.parse(logLevel));
	}

	/**
	 * Set level of logging	for all required logs used in application
	 * @param level
	 */
	public static void setLevel(Level level)
	{
		Logger rootLog = Logger.getLogger(ROOT_LOG);
		if (rootLog != null && rootLog.getLevel() != level)
		{
			rootLog.setLevel(level);
		}

		Logger wsdlLog = Logger.getLogger("org.apache.cxf.wsdl.service.factory");
		if (wsdlLog != null && wsdlLog.getLevel() != level)
		{
			wsdlLog.setLevel(level);
		}

		Logger sunLog = Logger.getLogger("com.sun.xml");
		if (sunLog != null && sunLog.getLevel() != level)
		{
			sunLog.setLevel(level);
		}

		if (consoleHandler != null)
		{
			consoleHandler.setLevel(level);
		}
	}

	/**
	 * Get current log level.
	 * @return
	 */
	public static Level getLevel()
	{
		return Logger.getLogger(ROOT_LOG).getLevel();
	}

	public static void setLogLevel(String logLevel)
	{
		if (logLevel != null)
		{
			logLevel = logLevel.toUpperCase();
			switch (logLevel)
			{
				case "TRACE":
				case "T":
				case "4":
					logLevel = Level.ALL.getName();
					break;

				case "DEBUG":
				case "D":
				case "3":
					logLevel = Level.FINE.getName();
					break;

				case "INFO":
				case "I":
				case "2":
					logLevel = Level.INFO.getName();
					break;

				case "WARNING":
				case "W":
				case "1":
					logLevel = Level.WARNING.getName();
					break;

				case "ERROR":
				case "E":
				case "0":
					logLevel = Level.SEVERE.getName();
					break;

				default:
					break;
			}

			try
			{
				Logging.initialize(logLevel, true, true);
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "Invalid log level specified.");
			}
		}
	}

	private static synchronized NonCloseHandler _streamHandler()
	{
		if (streamHandler == null)
		{
			StreamHandler sth = new StreamHandler(OLD_ERR, FORMATTER);
			sth.setLevel(Level.ALL);
			sth.setFilter(FILTER);
			streamHandler = new NonCloseHandler(sth, 500);
		}
		return streamHandler;
	}

	@SuppressWarnings("CallToThreadDumpStack")
	private static synchronized NonCloseHandler _getFileHandler() throws Exception
	{
		if (fileHandler != null)
		{
			return fileHandler;
		}
		File home = Places.getUserDirectory();
		if (home != null)
		{
			try
			{
				File dir = new File(new File(home, "var"), "log");
				dir.mkdirs();
				_cleanUpOldFiles(dir, 10, true);
				long pid = ProcessHandle.current().pid();
				String fileName = String.format("messages.%06d.log", pid);
				File f = new File(dir, fileName);
				File f1 = new File(dir, fileName + ".1");
				File f2 = new File(dir, fileName + ".2");
				if (f2.exists())
				{
					f2.delete();
				}
				if (f1.exists())
				{
					f1.renameTo(f2);
				}
				if (f.exists())
				{
					f.renameTo(f1);
				}
				FileOutputStream fout = new FileOutputStream(f, false);
				Handler h = new StreamHandler(fout, FORMATTER);
				h.setLevel(Level.ALL);
				h.setFilter(FILTER);
				fileHandler = new NonCloseHandler(h, 1000);
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		if (fileHandler == null)
		{
			fileHandler = _streamHandler();
		}
		return fileHandler;
	}

	/**
	 * Delete old files.
	 * @param targetDir
	 * @param expirationPeriod Expiration period in days.
	 */
	private static void _cleanUpOldFiles(File targetDir, int expirationPeriod, boolean autoClean) throws Exception
	{
		if (autoClean)
		{
			if (!targetDir.exists())
			{
				return;
			}
			File[] files = targetDir.listFiles();
			for (File file : files)
			{
				long diff = new Date().getTime() - file.lastModified();
				// Granularity = DAYS;
				long desiredLifespan = TimeUnit.DAYS.toMillis(expirationPeriod);
				if (diff > desiredLifespan)
				{
					file.delete();
				}
			}
		}
	}

	/**
	 * Non closing handler.
	 */
	private static final class NonCloseHandler extends Handler implements Runnable
	{
		private static final RequestProcessor RP = new RequestProcessor("Logging Flush", 1, false, false); // NOI18N

		private static final ThreadLocal<Boolean> FLUSHING = new ThreadLocal<>();

		private final Handler delegate;

		private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>(1000);

		private final RequestProcessor.Task flush;

		private final int delay;

		@SuppressWarnings("LeakingThisInConstructor")
		public NonCloseHandler(Handler h, int delay)
		{
			delegate = h;
			flush = RP.create(this, true);
			flush.setPriority(Thread.MIN_PRIORITY);
			this.delay = delay;
		}

		@Override
		@SuppressWarnings("CallToThreadYield")
		public void publish(LogRecord record)
		{
			if (RP.isRequestProcessorThread())
			{
				return;
			}
			if (!queue.offer(record))
			{
				for (;;)
				{
					try
					{
						// queue is full, schedule its clearing
						if (!schedule(0))
						{
							return;
						}
						queue.put(record);
						Thread.yield();
						break;
					}
					catch (InterruptedException ex)
					{
						// OK, ignore and try again
					}
				}
			}
			Throwable t = record.getThrown();
			if (t == null && record.getParameters() != null)
			{
				for (Object o : record.getParameters())
				{
					if (o instanceof Throwable)
					{
						t = (Throwable) o;
						record.setThrown(t);
					}
				}
			}
			if (t != null)
			{
				StackTraceElement[] tStack = t.getStackTrace();
				StackTraceElement[] hereStack = new Throwable().getStackTrace();
				for (int i = 1; i <= Math.min(tStack.length, hereStack.length); i++)
				{
					if (!tStack[tStack.length - i].equals(hereStack[hereStack.length - i]))
					{
						catchIndex.put(t, tStack.length - i);
						break;
					}
				}
			}
			schedule(delay);
		}

		/**
		 *
		 * @param d
		 * @return
		 */
		private boolean schedule(int d)
		{
			if (!Boolean.TRUE.equals(FLUSHING.get()))
			{
				try
				{
					FLUSHING.set(true);
					flush.schedule(d);
				}
				finally
				{
					FLUSHING.set(false);
				}
				return true;
			}
			else
			{
				return false;
			}
		}

		@Override
		public void flush()
		{
			flush.cancel();
			flush.waitFinished();
			run();
		}

		@Override
		public void close() throws SecurityException
		{
			flush();
			delegate.flush();
		}

		public void doClose() throws SecurityException
		{
			flush();
			delegate.close();
		}

		public @Override
		Formatter getFormatter()
		{
			return delegate.getFormatter();
		}

		static Handler getInternal(Handler h)
		{
			if (h instanceof NonCloseHandler)
			{
				return ((NonCloseHandler) h).delegate;
			}
			return h;
		}

		@Override
		public void run()
		{
			while (true)
			{
				LogRecord r = queue.poll();
				if (r == null)
				{
					break;
				}
				delegate.publish(r);
			}
			delegate.flush();
		}
	}

	/**
	 * Custom log record formatter.
	 */
	private static final class CustomFormatter extends Formatter
	{
		private static final String lineSeparator = System.getProperty("line.separator");

		@Override
		public String format(LogRecord record)
		{
			StringBuilder sb = new StringBuilder();
			_print(sb, record, new HashSet<>());
			return sb.toString();
		}

		/**
		 *
		 * @param sb
		 * @param record
		 * @param beenThere
		 */
		private void _print(StringBuilder sb, LogRecord record, Set<Throwable> beenThere)
		{
			if (record.getMessage() == null)
			{
				if (record.getThrown() != null)
				{
					record.setMessage(record.getThrown().getMessage());
				}
				else
				{
					record.setMessage("Message from logger " + record.getLoggerName() + " is null.");
				}
			}
			String message = record.getMessage() != null ? formatMessage(record) : null;
			if (message != null && message.indexOf('\n') != -1 && record.getThrown() == null)
			{
				// multi line messages print witout any wrappings
				sb.append(message);
				if (message.charAt(message.length() - 1) != '\n')
				{
					sb.append(lineSeparator);
				}
				return;
			}
			if ("stderr".equals(record.getLoggerName()) && record.getLevel() == Level.INFO)
			{
				// do not prefix stderr logging...
				sb.append(message);
				return;
			}
			sb.append(DATE_FORMAT.format(new Date(record.getMillis())));
			sb.append(" ");
			sb.append(record.getLevel().getName());
			_addLoggerName(sb, record);
			if (message != null)
			{
				sb.append(": ");
				sb.append(message);
			}
			sb.append(lineSeparator);
			if (record.getThrown() != null && record.getLevel().intValue() != 1973) // 1973 signals ErrorManager.USER
			{
				try
				{
					StringWriter sw = new StringWriter();
					try (PrintWriter pw = new PrintWriter(sw))
					{
						printStackTrace(record.getThrown(), pw);
					}
					sb.append(sw.toString());
				}
				catch (Exception ex)
				{
				}

				LogRecord[] arr = extractDelegates(sb, record.getThrown(), beenThere);
				if (arr != null)
				{
					for (LogRecord r : arr)
					{
						_print(sb, r, beenThere);
					}
				}
			}
		}

		public static void printStackTrace(Throwable t, PrintWriter pw)
		{
			doPrintStackTrace(pw, t, null);
		}

		private static void doPrintStackTrace(PrintWriter pw, Throwable t, Throwable higher)
		{
			try
			{
				if (t.getClass().getMethod("printStackTrace", PrintWriter.class).getDeclaringClass() != Throwable.class)
				{
					t.printStackTrace(pw);
					return;
				}
			}
			catch (NoSuchMethodException e)
			{
				assert false : e;
			}
			Throwable lower = t.getCause();
			if (lower != null)
			{
				doPrintStackTrace(pw, lower, t);
				pw.print("Caused: "); // NOI18N
			}
			String summary = t.toString();
			if (lower != null)
			{
				String suffix = ": " + lower;
				if (summary.endsWith(suffix))
				{
					summary = summary.substring(0, summary.length() - suffix.length());
				}
			}
			pw.println(summary);
			StackTraceElement[] trace = t.getStackTrace();
			int end = trace.length;
			if (higher != null)
			{
				StackTraceElement[] higherTrace = higher.getStackTrace();
				while (end > 0)
				{
					int higherEnd = end + higherTrace.length - trace.length;
					if (higherEnd <= 0 || !higherTrace[higherEnd - 1].equals(trace[end - 1]))
					{
						break;
					}
					end--;
				}
			}
			Integer caughtIndex = catchIndex.get(t);
			for (int i = 0; i < end; i++)
			{
				if (caughtIndex != null && i == caughtIndex)
				{
					// Translate following tab -> space since formatting is bad in
					// Output Window (#8104) and some mail agents screw it up etc.
					pw.print("[catch] at "); // NOI18N
				}
				else
				{
					pw.print("\tat "); // NOI18N
				}
				pw.println(trace[i]);
			}
		}

		/**
		 *
		 * @param sb
		 * @param record
		 */
		private void _addLoggerName(StringBuilder sb, LogRecord record)
		{
			String name = record.getLoggerName();
			if (!"".equals(name))
			{
				sb.append(" [");
				sb.append(name);
				sb.append(']');
			}
		}

		/**
		 *
		 * @param sb
		 * @param t
		 * @param beenThere
		 * @return
		 */
		@SuppressWarnings("CallToThreadDumpStack")
		private LogRecord[] extractDelegates(StringBuilder sb, Throwable t, Set<Throwable> beenThere)
		{
			if (!beenThere.add(t))
			{
				sb.append("warning: cyclic dependency between annotated throwables"); // NOI18N
				return null;
			}

			if (t instanceof Callable)
			{
				Object rec = null;
				try
				{
					rec = ((Callable) t).call();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				if (rec instanceof LogRecord[])
				{
					return (LogRecord[]) rec;
				}
			}
			if (t == null)
			{
				return null;
			}
			return extractDelegates(sb, t.getCause(), beenThere);
		}
	}

	/**
	 * Default Logger Filter.
	 */
	private static final class DefaultLoggerFilter implements Filter
	{
		@Override
		public boolean isLoggable(LogRecord record)
		{
			if ("org.openide.util.SharedClassObject".equals(record.getLoggerName()))
			{
				if ("(Run with -J-Dorg.openide.util.SharedClassObject.level=0 for more details.)".equals(record.getMessage()))
				{
					return false;
				}
			}
			else if ("org.netbeans.TopSecurityManager".equals(record.getLoggerName()))
			{
				if (record.getThrown() != null && record.getThrown().getMessage() != null)
				{
					if (record.getThrown().getMessage().startsWith("Dangerous reflection access to sun.misc.GC by class org.apache.cxf.common.logging.JDKBugHacks detected!")
						|| record.getThrown().getMessage().startsWith("Dangerous reflection access to sun.misc.GC$Daemon by class java.lang.Thread$1 detected!"))
					{
						return false;
					}
				}
			}
			else if ("global".equals(record.getLoggerName()))
			{
				if (record.getThrown() != null && "modification outside the region".equals(record.getThrown().getMessage()))
				{
					return false;
				}
			}
			else if ("An illegal reflective access operation has occurred".equals(record.getMessage()))
			{
				return false;
			}
			else if (record.getThrown() instanceof LogSkipException)
			{
				return false;
			}
			return true;
		}
	}

	private static class CustomConsoleHandler extends ConsoleHandler
	{
		public CustomConsoleHandler()
		{
			super();
			setFormatter(FORMATTER);
			try
			{
				setEncoding("UTF-8");
			}
			catch (Exception ex)
			{
				Exceptions.printStackTrace(ex);
			}
		}
	}
}
