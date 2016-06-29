/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class KapuaDateUtils {

	public static Locale getEdcLocale() {
		return Locale.US;
	}

	public static TimeZone getEdcTimeZone() {
		return TimeZone.getTimeZone("UTC");
	}
	
	public static Calendar getEdcCalendar() {
		Calendar cal = Calendar.getInstance(getEdcTimeZone(), getEdcLocale());
		return cal;
	}
	
	/**
	 * @return current date GMT
	 */
	public static Date getEdcSysDate() {
		Calendar cal = KapuaDateUtils.getEdcCalendar();
		return cal.getTime();
	}
	
	/**
	 * @param millis
	 * @return date GMT
	 */
	public static Date getDateFromMillis(long millis) {
		Calendar cal = KapuaDateUtils.getEdcCalendar();
		cal.setTimeInMillis(millis);
		return cal.getTime();
	}
	
	public static int weekOfTheYear(Date date) {
		Calendar cal = KapuaDateUtils.getEdcCalendar();
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}	
	
	public static Date weekByFirstDay(Date date) {
		Calendar cal = KapuaDateUtils.getEdcCalendar();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of this week in milliseconds
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		return cal.getTime();
	}
	
	public static Date getStartOfWeek(int year, int weekOfYear) {
		Calendar cal = KapuaDateUtils.getEdcCalendar();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
		return cal.getTime();
	}
}
