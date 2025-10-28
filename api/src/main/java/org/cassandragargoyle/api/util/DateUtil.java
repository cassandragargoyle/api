/*
 *  This file is part of CassandraGargoyle Community Project
 *  Licensed under the MIT License - see LICENSE file for details
 */
package org.cassandragargoyle.api.util;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Class DateUtil
 * @author splichal
 */
public class DateUtil
{
	public static final FastDateFormat LIC_DATE_FORMAT = FastDateFormat.getInstance("dd-MM-yyyy");

	private static final Calendar CALENDAR = Calendar.getInstance();

	private static final int[] rgDays =
	{
		0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365
	};

	public static synchronized String formatDate(Date date)
	{
		return date == null ? "" : LIC_DATE_FORMAT.format(date);
	}

	public static Date fromDayCount(int dayCount)
	{
		int leap = 0;
		int year;
		int month;
		int day;

		if (dayCount <= 0)
		{
			year = 2100;
			month = 1;
			day = 1;
		}
		else
		{
			// Every 400 years have constant numb of days: 400*365 + 4*24 + 1 = 146,097 days
			year = (400 * (dayCount / 146097));
			dayCount %= 146097;

			// Within a 400 year every 100 years contains 100*365 + 24 = 36,524 days
			if (dayCount == 146096)
			{
				leap = -1;
			}
			year += (100 * (dayCount / 36524));
			dayCount %= 36524;

			// Within a 100 year every 4 years contains 4*365 + 1 = 1,461 days
			year += (4 * (dayCount / 1461));
			dayCount %= 1461;
			if (dayCount == 1460)
			{
				leap = -1;
			}
			year += dayCount / 365;
			dayCount %= 365;

			// Are days left? If no, it is 31.12.
			if (dayCount == 0)
			{
				month = 12;
				day = 31 + leap;
			}
			else
			{
				year++;
				leap = isLeapYear(year) ? 1 : 0;

				month = dayCount / 29;
				if ((dayCount > rgDays[month] + leap) || ((month < 2) && (dayCount > rgDays[month])))
				{
					month++;
				}

				day = dayCount - rgDays[month - 1];
				if (month > 2)
				{
					day -= leap;
				}
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month - 1, day);
		return cal.getTime();
	}

	public static int toDayCount(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		// Days in current year
		int dayCount = (rgDays[month - 1] + day);
		if (month > 2 && isLeapYear(year))
			dayCount++;

		// Days in complete years
		dayCount += (365 * (year - 1));

		// Each century has 24 leap years
		dayCount += ((year / 100) * 24);

		// 1 leap year every 400 years from complete years
		dayCount += ((year - 1) / 400);

		// leap years in current century
		dayCount += (((year % 100) - 1) / 4);

		return dayCount;
	}

	public static boolean isLeapYear(int year)
	{
		return (0 == year % 400) || ((0 != year % 100) && (0 == year % 4));
	}

	public static Date min(Date val1, Date val2)
	{
		if (val1 == null)
		{
			return val2;
		}
		if (val2 == null)
		{
			return val1;
		}
		return val1.compareTo(val2) < 0 ? val1 : val2;
	}

	public static Date max(Date val1, Date val2)
	{
		if (val1 == null)
		{
			return val2;
		}
		if (val2 == null)
		{
			return val1;
		}
		return val1.compareTo(val2) < 0 ? val2 : val1;
	}

	public static boolean equals(Date val1, Date val2)
	{
		if (val1 == val2)
		{
			return true;
		}
		if (val1 != null && val2 != null)
		{
			return val1.compareTo(val2) == 0;
		}
		return false;
	}

	public static boolean atMidnight(Date date)
	{
		if ((date.getTime() % 3600000) == 0)
		{
			synchronized (CALENDAR)
			{
				CALENDAR.setTime(date);
				return CALENDAR.get(Calendar.HOUR) == 0 && CALENDAR.get(Calendar.MINUTE) == 0 && CALENDAR.get(Calendar.SECOND) == 0 && CALENDAR.get(Calendar.MILLISECOND) == 0;
			}
		}
		return false;
	}

	public static Date toMidnight(Date date)
	{
		synchronized (CALENDAR)
		{
			CALENDAR.setTime(date);
			CALENDAR.set(Calendar.HOUR, 0);
			CALENDAR.set(Calendar.MINUTE, 0);
			CALENDAR.set(Calendar.SECOND, 0);
			CALENDAR.set(Calendar.MILLISECOND, 0);
			return CALENDAR.getTime();
		}
	}

	public static boolean isToday(Date date)
	{
		date = toMidnight(date);
		return date.equals(toMidnight(new Date()));
	}

	public static Date getToday()
	{
		return toMidnight(new Date());
	}

	public static FastDateFormat getSafeDateTimeFormat(String pattern, FastDateFormat defaultFormat)
	{
		try
		{
			return FastDateFormat.getInstance(pattern);
		}
		catch (Exception e)
		{
			return defaultFormat;
		}
	}
}
