package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description   : XDE Tester class intended to
 * provide various useful date formatting methods.
 * Created by tchen on 1/11/2016.
 */

public class DateFunctions {

  // integer replacements for time unit types
  public static final int YEAR = -73;

  public static final int MONTH = -63;

  public static final int WEEK = -53;

  public static final int DAY = -43;

  public static final String[] MONTHS_SHORT = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  public static final String[] DAYS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
  public static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
  private static final String[] WEEKS_SHORT = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  //	private static final String[] WEEKS={"Sunday", "Monday", "Tuesday", "Wednsday", "Thursday", "Friday", "Saturday"};

  /**
   * Date format and pattern mapping. for some ambiguous pattern for example "03/04/2013", always assume Month is before day
   */
  public static BiMap<String, String> DATE_FORMATS = HashBiMap.create(new HashMap<String, String>() {
    private static final long serialVersionUID = 1L;

    {
      put("\\d{1,2}/\\d{1,2}/\\d{4}", "M/d/yyyy");
      put("\\d{4}/\\d{1,2}/\\d{1,2}", "yyyy/M/d");
      put("\\d{4}-\\d{1,2}-\\d{1,2}", "yyyy-M-d");
      put("\\d{1,2}-\\d{1,2}-\\d{4}", "M-d-yyyy");
      put("\\d{4}\\d{2}\\d{2}", "yyyyMMdd");
      put("\\d{1,2}-[a-zA-Z]{3}-\\d{2}", "d-MMM-yy");
      put("\\d{1,2}-[a-zA-Z]{3}-\\d{1,2}", "dd-MMM-yy");
      put("\\d{1,2}/[a-zA-Z]{3}/\\d{4}", "dd/MMM/yyyy");
      put("\\d{1,2} [a-z-A-Z]{3,} \\d{4}", "d MMM yyyy");
      put("\\d{4} [a-z-A-Z]{3,} \\d{1,2}", "yyyy MMM d");
      put("[a-zA-Z]{3} \\d{1,2} \\d{4}", "MMM d yyyy");
      put("[a-zA-Z]{3} \\d{1,2}, \\d{4}", "MMM d, yyyy");
      put("[a-zA-Z]{3} \\d{1,2},\\d{4}", "MMM d,yyyy");
      put("[a-zA-Z]{3}, [a-zA-Z]{3} \\d{1,2}, \\d{4}", "EEE, MMM d, yyyy");
      put("[a-zA-Z]{4,}, [a-zA-Z]{4,} \\d{1,2}, \\d{4}", "EEEEE, MMMMM d, yyyy");
      put("[a-zA-Z]{3} [a-zA-Z]{3} \\d{1,2} \\d{4}", "EEE MMM d yyyy");
      put("[a-zA-Z]{4,} [a-zA-Z]{4,} \\d{1,2} \\d{4}", "EEEEE MMMMM d yyyy");

      put("[a-zA-Z]{3} \\d{2},\\d{4} \\d{2}:\\d{2} [A|P]{1}M{1}", "MMM dd,yyyy hh:mm a");
      put("[a-zA-Z]{3} [a-zA-Z]{3} \\d{1,2} \\d{4} \\d{1,2}:\\d{2} [A|P]{1}M{1}", "EEE MMM d yyyy hh:mm a");
      put("[a-zA-Z]{3} [a-zA-Z]{3} \\d{1,2} \\d{4} \\d{1,2}:\\d{2} [A|P]{1}M{1} [A-Z]{3}", "EEE MMM d yyyy hh:mm a z");
      put("[a-zA-Z]{3} [a-zA-Z]{3} \\d{2},\\d{4} \\d{2}:\\d{2} [A|P]{1}M{1} [A-Z]{3}", "EEE MMM dd,yyyy hh:mm a z");
      put("[a-zA-Z]{3} [a-zA-Z]{3} \\d{2},\\d{4} \\d{2}:\\d{2} [A|P]{1}M{1}", "EEE MMM dd,yyyy hh:mm a");
      put("[a-zA-Z]{3} [a-zA-Z]{3} \\d{2}, \\d{4} \\d{2}:\\d{2} [A|P]{1}M{1}", "EEE MMM dd, yyyy hh:mm a");

      put("\\d{4}/\\d{2}/\\d{1,2} \\d{1,2}:\\d{1,2} [A|P]{1}M{1}", "yyyy/MM/dd h:m a");
      put("\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}", "M/d/yyyy hh:mm");

      put("[a-zA-Z]{4,} [a-zA-Z]{3,} \\d{1,2} \\d{4} \\d{1,2}:\\d{2} [A|P]{1}M{1}", "EEEEE MMM dd yyyy hh:mm a");
      put("[a-zA-Z]{4,} [a-zA-Z]{3,} \\d{1,2} \\d{4}", "EEEE MMM dd yyyy");
      put("\\d{1,2}/[a-zA-Z]{3}/\\d{4} \\d{1,2}:\\d{2}", "dd/MMM/yyyy hh:mm");

      put("[a-zA-Z]{3} [a-zA-Z]{3} \\d{1,2} \\d{2}:\\d{2}:\\d{2} [A-Z]{3} \\d{4}", "EEE MMM dd hh:mm:ss z yyyy");


    }
  });


  public static final int MINUTES_OF_DAY = 1440;

  public static String getDateFormatPattern(String dateFormat) {
    return DATE_FORMATS.inverse().get(dateFormat);
  }

  /**
   * Utility method to get current time stamp
   *
   * @return String  the current time stamp in form: mon_dd_hh_mm_ss_yyyy
   */
  public static String getTimeStamp() {
    String timeStamp = getDateStamp("MMM_dd_hh_mm_ss_yyyy", null);
    return timeStamp;
  }

  public static String addTimeMinutes(String baseTime, int offsetMinutes, String fromFormat, String toFormat) {
    Calendar c = Calendar.getInstance();
    c.setTime(DateFunctions.parseDateString(baseTime, fromFormat));
    c.add(Calendar.MINUTE, offsetMinutes);
    return DateFunctions.formatDate(c.getTime(), toFormat);
  }

  /**
   * Utility method to get current date
   * (kudos to SSHERWOOD for example code in SysDate class)
   *
   * @param isLogfileDate whether or not date is for filename
   * @return String  the current date (T: YYYYMMDD, F: (M?)M/(D?)D/YYYY)
   */
  public static String getDateStamp(boolean isLogFileDate) {
    if (isLogFileDate) { // return filename datestamp
      return getDateStamp("yyyyMMdd", null);
    } else { // return file record/row datestamp
      return getDateStamp("M/d/yyyy", null);
    }
  }

  /**
   * Utility method to get current date for specified contract
   * (kudos to SSHERWOOD for example code in SysDate class)
   *
   * @param schema
   * @param isLogfileDate whether or not date is for filename
   * @return String  the current date (T: YYYYMMDD, F: (M?)M/(D?)D/YYYY)
   */
  public static String getDateStamp(boolean isLogFileDate, TimeZone timeZone) {
    if (isLogFileDate) { // return filename datestamp
      return getDateStamp("yyyyMMdd", timeZone);
    } else { // return file record/row datestamp
      return getDateStamp("M/d/yyyy", timeZone);
    }
  }

  /**
   * Get the current date string with the given format for
   * specified contract TimeZone,if timezone=null,It will use local time zone
   *
   * @param format
   * @param schema
   * @return
   */
  public static String getDateStamp(String format, TimeZone timeZone) {
    String dateStamp = null;
    SimpleDateFormat dateFormat = null;
    Calendar timeNow = Calendar.getInstance();
    Date date = timeNow.getTime();
    dateFormat = new SimpleDateFormat(format);

    if (timeZone != null) {
      dateFormat.setTimeZone(timeZone);
    }
    dateStamp = dateFormat.format(date);

    return dateStamp;
  }

  /**
   * Utility method to get current date stamp
   *
   * @return String the current date stamp includes hours and minutes (yyyyMMddhhmm)
   */
  public static String getLongDateStamp() {
    return getDateStamp("yyyyMMddhhmm", null);
  }

  /**
   * Utility method to get current date stamp for specified contract
   *
   * @param schema
   * @Return String  the current date stamp includes hours and minutes (yyyyMMddhhmm)
   */
  public static String getLongDateStamp(TimeZone timeZone) {
    return getDateStamp("yyyyMMddhhmm", timeZone);
  }

  /**
   * Utility method to get current time stamp
   *
   * @return String the current time stamp includes hours minutes and seconds(hhmmss)
   */
  public static String getLongTimeStamp() {
    return getDateStamp("hhmmss", null);
  }

  /**
   * Utility method to get current date stamp,not include time
   *
   * @return String the current date(yyyyMMdd)
   */
  public static String getShortDateStamp() {
    return getDateStamp("yyyyMMdd", null);
  }

  /**
   * Utility method to get current date stamp,not include time for specified contract
   *
   * @param schema
   * @Return String
   */
  public static String getShortDateStamp(TimeZone timeZone) {
    return getDateStamp("yyyyMMdd", timeZone);
  }

  /**
   * Utility method to get current date in format MM/dd/yyyy
   *
   * @return - current date in string MM/dd/yyyy
   */
  public static String getToday() {
    return getDateStamp(false);
  }


  /**
   * Utility method to get current date in format MM/dd/yyyy for specified TimeZone
   *
   * @return - current date in string MM/dd/yyyy
   */
  public static String getToday(TimeZone timeZone) {
    return getDateStamp(false, timeZone);
  }

  /**
   * Utility method to get current date in format given format
   *
   * @return - current date in given format
   */
  public static String getToday(String format) {
    return getDateStamp(format, null);
  }

  /**
   * Utility method to get current date in format
   * mm/dd/yyyy for specified contract
   *
   * @param format
   * @param schema
   * @return String
   */
  public static String getToday(String format, TimeZone timeZone) {
    return getDateStamp(format, timeZone);
  }

  /**
   * Utility method to get current year
   *
   * @return
   */
  public static int getCurrentYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  public static int getYearAfterCurrentYear(int offset) {
    return Calendar.getInstance().get(Calendar.YEAR) + offset;
  }

  public static int getYearAfterGivenYear(int numYear, String givenYear) {
    return Integer.valueOf(givenYear) + numYear;
  }

  /**
   * get current month short name
   *
   * @return
   */
  public static String getCurrentMonth() {
    return getMonthAfterThisMonth(0);
  }

  /**
   * Get the month short after current month
   *
   * @param offset
   * @return
   */
  public static String getMonthAfterThisMonth(int offset) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + offset);
    int monthIndex = cal.get(Calendar.MONTH);

    return MONTHS_SHORT[monthIndex];
  }

  /**
   * Utility method to get date given a Calendar object
   * (kudos to SSHERWOOD for example code in SysDate class)
   *
   * @param isLogfileDate whether or not date is for filename
   * @param  date  Calendar object to retrieve date string from
   * @return String  the current date (T: YYYYMMDD, F: (M?)M/(D?)D/YYYY)
   */
  public static String getDateStamp(Calendar date, boolean isLogFileDate) {
    String dateStamp = null;
    SimpleDateFormat dateFormat = null;
    Date tempDate = date.getTime();

    if (isLogFileDate) { // return filename datestamp
      dateFormat = new SimpleDateFormat("yyyyMMdd");
      dateStamp = dateFormat.format(tempDate);
    } else { // return file record/row datestamp
      dateFormat = new SimpleDateFormat("M/d/yyyy");
      dateStamp = dateFormat.format(tempDate);
    }

    return dateStamp;
  }

  /**
   * Method to format month from
   * [month] to M
   *
   * @param  month  month to format
   * @return String  re-formatted month
   */
  public static String parseMonthValue(String month) {
    for (int i = 0; i < MONTHS_SHORT.length; i++) {
      if (MONTHS_SHORT[i].equalsIgnoreCase(month)) {
        return (i + 1) + "";
      }
    }

    return "<err>";
  }

  public static String parseMonthValue(int month, boolean shortName) {
    if (shortName) {
      return MONTHS_SHORT[month - 1];
    } else {
      return MONTHS[month - 1];
    }
  }

  /**
   * Format month date to be short
   *
   * @param date -- date. Such as "7/1/2010"
   * @return short date. -- Jul 1, 2010
   */
  public static String formatDateToShort(String date) {
    String[] temp = date.split("/");
    String curDate = DateFunctions.parseMonthValue(Integer.valueOf(temp[0]), true) + " " + temp[1] + "," + " " + temp[2];

    return curDate;
  }

  /**
   * Method to format date from any date string to M/d/yyyy
   *
   * @param  date  date to format
   * @return String  re-formatted date
   */
  public static String formatDate(String date) {
    String formattedDate = formatDate(date, "M/d/yyyy");

    return formattedDate;
  }

  /**
   * Convenient method to format any date to [month] d, yyyy
   *
   * @param  date  date to format
   * @return String  re-formatted date
   */
  public static String formatToFullDate(String date) {
//		String formattedDate = null;
//		SimpleDateFormat stringToDate = null;
//		SimpleDateFormat dateFormatter = null;
//
//		try {
//
//			stringToDate = new SimpleDateFormat("M/d/yyyy");
//			Date tempDate = stringToDate.parse(date);
//
//			dateFormatter = new SimpleDateFormat("MMMMMMMMM d, yyyy");
//			formattedDate = dateFormatter.format(tempDate);
//		} catch (ParseException parseErr) {
//
//			parseErr.printStackTrace();
//		}

    String formattedDate = formatDate(date, "MMMMMMMMM d, yyyy");
    return formattedDate;
  }

  public static String formatToFullDate(Date date) {
    return formatDate(date, "MMMMMMMMM d, yyyy");
  }

  /**
   * Get the date string pattern
   *
   * @param date - date String
   * @return - the pattern String
   */
  public static String getDateStringPattern(String date) {
    for (String pattern : DATE_FORMATS.keySet()) {
      if (date.matches(pattern)) {
        return DATE_FORMATS.get(pattern);
      }
    }
    throw new ItemNotFoundException("Failed to parse date string \"" + date + "\".");
  }

  /**
   * Format the date string to the given pattern
   *
   * @param date
   * @param pattern
   * @return - formatted date string
   */
  public static String formatDate(String date, String pattern) {
    if (date.length() < 1) {
      return date;
    } else {
      return formatDate(parseDateString(date), pattern);
    }
  }

  /**
   * Format date string with given format to another format
   *
   * @param date    date string to be parsed
   * @param format1 DateFormat1
   * @param format2 DateFormat2
   * @return
   * @throws ParseException
   */
  public static String formatDate(String date, String fromFormat, String toFormat) {
//		SimpleDateFormat sdf1 = new SimpleDateFormat(fromFormat);
//		SimpleDateFormat sdf2 = new SimpleDateFormat(toFormat);
//		return sdf2.format(sdf1.parse(date));ParseException
    return formatDate(parseDateString(date, fromFormat), toFormat);
  }

  public static String formatDate(Date date, String pattern) {
    Locale locale = Locale.US;
    String formattedDate = null;
    SimpleDateFormat dateFormatter = null;

    dateFormatter = new SimpleDateFormat(pattern, locale);
    formattedDate = dateFormatter.format(date);

    return formattedDate;
  }

  /**
   * Change date string of any formats to Date object
   *
   * @param date - date string except "d/M/yyyy"
   * @return - Date object
   */
  public static Date parseDateString(String date) {
    String pattern = getDateStringPattern(date);
    return parseDateString(date, pattern);
  }

  /**
   * Change date string of any formats to Date object
   *
   * @param date    - date string
   * @param pattern - the date string pattern
   * @return - Date object
   */
  public static Date parseDateString(String date, String pattern) {
    return parseDateString(date, pattern, (TimeZone) null);
  }

  /**
   * Change date string of any formats to Date object
   *
   * @param date
   * @param pattern
   * @param timeZone
   * @return
   */
  public static Date parseDateString(String date, String pattern, TimeZone timeZone) {
    SimpleDateFormat stringToDate = null;
    Date toReturn = null;
    Locale locale = Locale.US;
    try {
      stringToDate = new SimpleDateFormat(pattern, locale);
      if (timeZone != null) {
        stringToDate.setTimeZone(timeZone);
      }
      //generate a dateFormat object
      toReturn = stringToDate.parse(date);  //use the dateFormat object to parse a date string to Date object
    } catch (ParseException e) {

      AutomationLogger.getInstance().error(e);
    }

    return toReturn;
  }


  /**
   * Utility method to get current time,it is milliseconds to today
   *
   * @return long  the current time in milliseconds since January 1, 1970, 00:00:00 GMT
   */
  public static long getCurrentTime() {
    Calendar timeNow = Calendar.getInstance();
    long time = timeNow.getTime().getTime();
    return time;
  }

//	public static String getCurrentTime(TimeZone timeZone,boolean hours24) {
//		Calendar timeNow = Calendar.getInstance();
//		timeNow.setTimeZone(timeZone);
//
//		int hour = timeNow.get(Calendar.HOUR);
//		if(hours24) {
//			hour = timeNow.get(Calendar.HOUR_OF_DAY);
//		}
//		int minute = timeNow.get(Calendar.MINUTE);
//
//		String time = hour + ":" + minute;
//
//		return time;
//	}

  /**
   * Get the current time in format hh:mm
   *
   * @param hours24
   * @return
   */
  public static String getCurrentTimeFormated(boolean hours24) {
    return getCurrentTimeFormated(hours24, false);
  }

  public static String getCurrentTimeFormated(boolean hours24, TimeZone timeZone) {
    return getCurrentTimeFormated(hours24, 0, false, timeZone);
  }

  public static String getCurrentTimeFormated(boolean hours24, int increasingMin, TimeZone timeZone) {
    return getCurrentTimeFormated(hours24, increasingMin, false, timeZone);
  }

  public static String getCurrentTimeFormated(boolean hours24, boolean includeSec) {
    return getCurrentTimeFormated(hours24, 0, includeSec, null);
  }

  public static String getCurrentTimeFormated(boolean hours24, int increasingMin, boolean includeSec, TimeZone timeZone) {
    Calendar cal = Calendar.getInstance();
    if (timeZone != null) cal.setTimeZone(timeZone);
    int hour = cal.get(Calendar.HOUR);
    if (hours24) {
      hour = cal.get(Calendar.HOUR_OF_DAY);
    }
    int minute = cal.get(Calendar.MINUTE);
    if (increasingMin != 0) {
      minute = minute + increasingMin;
    }

    String time = hour + ":" + minute;
    if (includeSec) {
      int second = cal.get(Calendar.SECOND);
      time = time + ":" + second;
    }

    return time;
  }

  public static String getCurrentAMPM() {
    return getCurrentAMPM(null);
  }

  /**
   * Get current AMPM
   *
   * @return
   */
  public static String getCurrentAMPM(TimeZone timeZone) {
    Calendar cal = Calendar.getInstance();
    if (timeZone != null) cal.setTimeZone(timeZone);

    int AMPM = cal.get(Calendar.AM_PM);

    if (AMPM == Calendar.AM) {
      return "AM";
    } else {
      return "PM";
    }
  }

  /**
   * Get the next hour value after the give minutes in 24 hours format
   *
   * @param after
   * @return
   */
  public static int getNextHour(int minutesAfter) {
    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    int minute = Calendar.getInstance().get(Calendar.MINUTE) + minutesAfter;

    int c = minute / 60;

    return hour + c + 1;
  }

  /**
   * Utility method to get month, day, and year as integer components given a string of date
   *
   * @param date - a string of any format except d/M/yyyy
   * @return int[]  an integer array containing {month,day,year}
   */
  public static int[] getDateComponents(String dateString) {
    String pattern = getDateStringPattern(dateString);

    return getDateComponents(dateString, pattern);
  }

  /**
   * Utility method to get month, day, and year as integer components given a string with the given format
   *
   * @param dateFormat the date-format used to do the date calculations (i.e. M/d/yyyy, MM/dd/yyyy, etc)
   * @param  date  the formatted date string input
   * @return int[]  an integer array containing {month,day,year}
   */
  public static int[] getDateComponents(String date, String dateFormat) {
    int[] dateParts = new int[3];
//		int subIndex = 0;
//		int partIndex = 0;
//		String datePart;

    try {
      Date day = parseDateString(date, dateFormat);
      Calendar c = Calendar.getInstance();
      c.setTime(day);

      dateParts[0] = c.get(Calendar.MONTH) + 1;
      dateParts[1] = c.get(Calendar.DAY_OF_MONTH);
      dateParts[2] = c.get(Calendar.YEAR);

//			SimpleDateFormat checker = new SimpleDateFormat(dateFormat);
//			checker.parse(date);
//
//			// ensure date exists and has proper length
//			if ((date!=null)&&((date.length()>7)&&(date.length()<11))) {
//				dateParts = new int[3];
//
//				// go through string removing components separated by '/'
//				// converting them into integers and storing them
//				for (int i=0; i < (date.length()-4);) {
//					subIndex = date.indexOf('/',i);
//					datePart = date.substring(i,subIndex);
//					i = ++subIndex;
//
//					dateParts[partIndex] = Integer.parseInt(datePart);
//					partIndex++;
//				}
//				datePart = date.substring(subIndex,date.length());	// remove year
//				dateParts[partIndex] = Integer.parseInt(datePart);	// store year
//			}
    } catch (Exception parseErr) {
      parseErr.printStackTrace();
    }

    return dateParts;
  }

  /**
   * Utility method to get the difference in days between two
   * weekdays. One weekday is specified as being before the other and
   * the difference calculated is the difference between the days moving
   * from the first weekday to the second weekday.
   * NOTE: Calendar.SUNDAY -> Calendar.SATURDAY equals 1 -> 7 (default)
   *
   * @param  beforeWeekDay  the weekday to measure FROM
   * @param  afterWeekDay  the weekday to measure TO
   * @return int   the integer difference in days between two weekdays
   */
  public static int diffWeekDays(int beforeWeekDay, int afterWeekDay) {
    int weekLength = 7;

    if (beforeWeekDay == afterWeekDay) {
      return 0;
    } else if (beforeWeekDay < afterWeekDay) {
      return (afterWeekDay - beforeWeekDay);
    } else if (afterWeekDay < beforeWeekDay) {
      return (weekLength - (beforeWeekDay - afterWeekDay));
    } else {
      return -1; // error
    }
  }

  public static int diffMonthBetween(String firstDay, String anotherDay) {
    return diffMonthBetween(parseDateString(firstDay), parseDateString(anotherDay));
  }

  public static int diffMonthBetween(Date firstDate, Date anotherDate) {
    Calendar firstCal = Calendar.getInstance();
    firstCal.setTime(firstDate);
    Calendar anotherCal = Calendar.getInstance();
    anotherCal.setTime(anotherDate);

    return firstCal.get(Calendar.MONTH) - anotherCal.get(Calendar.MONTH);
  }

  /**
   * Utility method to convert a Date into a formatted string
   *
   * @param  theDay  the Date object to convert
   * @param  theFormat  the format to use for conversion
   * @return String   the formatted date string
   */
  public static String dateToFormattedString(Date theDay, SimpleDateFormat theFormat) {
    String otherDay = null;
    otherDay = theFormat.format(theDay);
    return otherDay;
  }

  /**
   * Utility method to convert a formatted date string into a Calendar object
   *
   * @param  dateString  the formatted date string input
   * @return Calendar  the Calendar object derived from the date string
   */
  public static Calendar getCalendarFromString(String dateString) {
    String pattern = getDateStringPattern(dateString);
    return getCalendarFromString(dateString, pattern);
  }

  /**
   * Utility method to convert a formatted date string into a Calendar object
   *
   * @param dateFormat the date-format used to do the date calculations (i.e. M/d/yyyy, MM/dd/yyyy, etc)
   * @param  dateString  the formatted date string input
   * @return Calendar  the Calendar object derived from the date string
   */
  public static Calendar getCalendarFromString(String dateString, String dateFormat) {

    Calendar startDate = Calendar.getInstance();

    int[] dateParts = DateFunctions.getDateComponents(dateString, dateFormat);
    startDate.clear();  // clear all time fields

    if ((dateParts != null) && (dateParts.length == 3)) {

      // set Calendar date (month has zero-based value)
      startDate.set(dateParts[2], dateParts[0] - 1, dateParts[1]);

      return startDate;
    } else {
      return null;
    }
  }

  /**
   * Utility method to convert a formatted date string (M-d-yyyy)
   * into  (M/d/yyyy)
   *
   * @param dateString the formatted date string input (M-d-yyyy)
   * @return String the formatted date string output (M/d/yyyy)
   */
  public static String convertDateString(String dateString) {
    String inPattern = "(1[0-2]|[1-9])-([1-3][0-9]|[1-9])-[2]0[0-1][0-9]";
    String splitChar = "-";
    String outPattern = "(1[0-2]|[1-9])/([1-3][0-9]|[1-9])/[2]0[0-1][0-9]";
    String glueChar = "/";
    return convertDateString(dateString, inPattern, outPattern, splitChar,
        glueChar, true);
  }

  /**
   * Utility method to convert a formatted date string (inPattern encodes format)
   * into  (M/d/yyyy)
   *
   * @param dateString the formatted date string input (inPattern encodes format)
   * @return String the formatted date string output (M/d/yyyy)
   */
  public static String convertDateString(String dateString, String inPattern,
                                         String splitChar) {
    String pattern1 = inPattern;
    String pattern2 = "(1[0-2]|[1-9])/([1-3][0-9]|[1-9])/[2]0[0-1][0-9]";
    String glueChar = "/";
    return convertDateString(dateString, pattern1, pattern2, splitChar,
        glueChar, true);
  }

  /**
   * Utility method to convert a formatted date string (inPattern encodes format)
   * into  (outPattern encodes format)
   *
   * @param dateString the formatted date string input (inPattern encodes format)
   * @return String the formatted date string output (outPattern encodes format)
   */
  public static String convertDateString(String dateString, String inPattern,
                                         String outPattern, String splitChar, String glueChar,
                                         boolean removeZeroPadding) {
//		Regex pattern1 = new Regex(inPattern);
//		Regex pattern2 = new Regex(outPattern);

    if (dateString.matches(inPattern)) {
      String[] dateParts = dateString.split(splitChar);
      if ((dateParts != null) && (dateParts.length == 3)) {

        if (removeZeroPadding == true) {
          if (dateParts[0].substring(0, 1).equals("0")) {
            dateParts[0] = dateParts[0].substring(1); // remove zero-padding
          }
          if (dateParts[1].substring(0, 1).equals("0")) {
            dateParts[1] = dateParts[1].substring(1); // remove zero-padding
          }
        }

        return dateParts[0] + glueChar + dateParts[1] + glueChar
            + dateParts[2]; // TODO: add zero-padding when required
      } else {
        return null;
      }
    } else if (dateString.matches(outPattern)) {
      return dateString;
    }

    return null;
  }

  /**
   * Utility method to get the date string after a number of days of the given date
   *
   * @param dateString the formatted date string input (M/d/yyyy) or [Weekday] [Month] [day][ |,][Year]
   * @param numDays    the number of days
   * @return String the formatted date string output (M/d/yyyy) after numDays of the given date
   */
  public static String getDateAfterGivenDay(String dateString, int numDays) {
    String pattern = getDateStringPattern(dateString);

    return getDateAfterGivenDay(dateString, numDays, pattern);

  }

  /**
   * Utility method to get the date string after a number of days of the given date with given pattern
   *
   * @param dateString the formatted date string input (such as M/d/yyyy)
   * @param numDays    the number of days
   * @param dateFormat the date-format used to do the date calculations (i.e. M/d/yyyy, MM-dd-yyyy, etc)
   * @return String the formatted date string output (such as M/d/yyyy) after numDays of the
   * given date
   */
  public static String getDateAfterGivenDay(String dateString, int numDays, String dateFormat) {
    Calendar startDate = getCalendarFromString(dateString, dateFormat);

    startDate.add(Calendar.DAY_OF_MONTH, numDays);

    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

    Date newDate = startDate.getTime();

    return formatter.format(newDate);

  }

  /**
   * Method to get the date string after adding a number of month of the given date
   *
   * @param dateString - the formatted date string input (such as M/d/yyyy)
   * @param numOfMonth - the number of month
   * @return string the formatted date string output (such as M/d/yyyy) after numOfMonth of the given date
   */
  public static String getDateAfterGivenMonth(String dateString, int numOfMonth) {
    return getDateAfterGivenMonth(dateString, numOfMonth, getDateStringPattern(dateString), false);
  }

  public static String getDateAfterGivenMonth(String dateString, int numOfMonth, boolean advance) {
    return getDateAfterGivenMonth(dateString, numOfMonth, getDateStringPattern(dateString), advance);
  }

  public static String getDateAfterGivenMonth(String dateString, int numOfMonth, String dateFormat) {
    return getDateAfterGivenMonth(dateString, numOfMonth, dateFormat, false);
  }

  /**
   * Method to get the date string after adding a number of month of the given date with the given pattern
   *
   * @param dateString - the formatted date string input (such as M/d/yyyy)
   * @param numOfMonth - the number of month
   * @param dateFormat - the date format used to do the date calculation (i.e. M/d/yyyy, MM-dd-yyyy, etc)
   * @return string the formatted date string output (such as M/d/yyyy) after numOfMonth of the given date
   */
  public static String getDateAfterGivenMonth(String dateString, int numOfMonth, String dateFormat, boolean advance) {

    Calendar calendar = getCalendarFromString(dateString, dateFormat);
    int dateValue = calendar.get(Calendar.DAY_OF_MONTH);
    //add numOfMonth month
    calendar.add(Calendar.MONTH, numOfMonth);
    int newDateValue = calendar.get(Calendar.DAY_OF_MONTH);
    int diff = dateValue - newDateValue;
    if (diff > 0 && advance) {
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);
      int date = calendar.get(Calendar.DAY_OF_MONTH);
      month = month + 1;
      date = diff;
      calendar.set(year, month, date);
    }
    SimpleDateFormat formater = new SimpleDateFormat(dateFormat);
    Date newDate = calendar.getTime();

    return formater.format(newDate);
  }

  /**
   * Get the Date after local today several days controled by param
   *
   * @param numDays days after today
   * @return date string and format is MM/dd/yyyy
   */
  public static String getDateAfterToday(int numDays) {
    return getDateAfterToday(numDays, "MM/dd/yyyy", (TimeZone) null);
  }

  public static String getDateAfterToday(int numDays, int day_of_week) {
    return getDateAfterToday(numDays, "MM/dd/yyyy", null, day_of_week);
  }

  /**
   * Get the Date after local today several days controled by param for specified TimeZone
   *
   * @param numDays days after today
   * @return date string and format is MM/dd/yyyy
   */
  public static String getDateAfterToday(int numDays, TimeZone timeZone) {
    return getDateAfterToday(numDays, "MM/dd/yyyy", timeZone);
  }

  /**
   * Get the Date after local today several days controled by param
   *
   * @param numDays
   * @param format
   * @return String
   */
  public static String getDateAfterToday(int numDays, String format) {
    return getDateAfterToday(numDays, format, (TimeZone) null);
  }

  /**
   * Get the Date after today several days controled for specified TimeZone
   * by param for specified contract
   *
   * @param numDays
   * @param format
   * @param schema
   * @Return String
   */
  public static String getDateAfterToday(int numDays, String format, TimeZone timeZone) {
    return getDateAfterGivenDay(getDateStamp(format, timeZone), numDays, format);
  }

  /**
   * Get a date string with required day_of_week after given number of days from today
   *
   * @param numDays
   * @param format
   * @param timeZone
   * @param day_of_week - MONDAY/TUESDAY/WEDNSDAY/THURSDAY/FRIDAY/SATURDAY/SUNDAY
   * @return
   */
  public static String getDateAfterToday(int numDays, String format, TimeZone timeZone, String day_of_week) {
    Calendar timeNow = Calendar.getInstance();
    timeNow.setTimeZone(timeZone);

    return getDateAfterGivenDate(numDays, timeNow, day_of_week, format);
  }

  /**
   * Get a date string with required day_of_week after given number of days from today
   *
   * @param numDays
   * @param format
   * @param timeZone
   * @param day_of_week - Calendar.MONDAY/TUESDAY/WEDNSDAY/THURSDAY/FRIDAY/SATURDAY/SUNDAY
   * @return
   */
  public static String getDateAfterToday(int numDays, String format, TimeZone timeZone, int day_of_week) {
    Calendar timeNow = Calendar.getInstance();
    if (timeZone != null) {
      timeNow.setTimeZone(timeZone);
    }

    return getDateAfterGivenDate(numDays, timeNow, day_of_week, format);
  }

  /**
   * Get a date with required day of week after the given number of days from the given date
   *
   * @param numDays
   * @param dateString
   * @param day_of_week - MONDAY/TUESDAY/WEDNSDAY/THURSDAY/FRIDAY/SATURDAY/SUNDAY
   * @return
   */
  public static String getDateAfterGivenDate(int numDays, String dateString, String day_of_week) {
    int day;
    if (day_of_week.toUpperCase().startsWith("MON")) {
      day = Calendar.MONDAY;
    } else if (day_of_week.toUpperCase().startsWith("TUE")) {
      day = Calendar.TUESDAY;
    } else if (day_of_week.toUpperCase().startsWith("WED")) {
      day = Calendar.WEDNESDAY;
    } else if (day_of_week.toUpperCase().startsWith("THU")) {
      day = Calendar.THURSDAY;
    } else if (day_of_week.toUpperCase().startsWith("FRI")) {
      day = Calendar.FRIDAY;
    } else if (day_of_week.toUpperCase().startsWith("SAT")) {
      day = Calendar.SATURDAY;
    } else if (day_of_week.toUpperCase().startsWith("SUN")) {
      day = Calendar.SUNDAY;
    } else {
      throw new ItemNotFoundException("Unknown day of week: " + day_of_week);
    }

    return getDateAfterGivenDate(numDays, dateString, day);
  }

  /**
   * Get a date with required day of week after the given number of days from the given date
   *
   * @param numDays
   * @param dateString
   * @param day_of_week - Calendar.MONDAY/TUESDAY/WEDNSDAY/THURSDAY/FRIDAY/SATURDAY/SUNDAY
   * @return
   */
  public static String getDateAfterGivenDate(int numDays, String dateString, int day_of_week) {
    String dateFormat = getDateStringPattern(dateString);
    Calendar startDate = getCalendarFromString(dateString, dateFormat);

    return getDateAfterGivenDate(numDays, startDate, day_of_week, dateFormat);
  }

  /**
   * Get a date string with required day of week after the given number of days from the given date
   *
   * @param numDays
   * @param startDate
   * @param day_of_week - MONDAY/TUESDAY/WEDNSDAY/THURSDAY/FRIDAY/SATURDAY/SUNDAY
   * @param dateFormat
   * @return
   */
  public static String getDateAfterGivenDate(int numDays, Calendar startDate, String day_of_week, String dateFormat) {
    int day;
    if (day_of_week.toUpperCase().startsWith("MON")) {
      day = Calendar.MONDAY;
    } else if (day_of_week.toUpperCase().startsWith("TUE")) {
      day = Calendar.TUESDAY;
    } else if (day_of_week.toUpperCase().startsWith("WED")) {
      day = Calendar.WEDNESDAY;
    } else if (day_of_week.toUpperCase().startsWith("THU")) {
      day = Calendar.THURSDAY;
    } else if (day_of_week.toUpperCase().startsWith("FRI")) {
      day = Calendar.FRIDAY;
    } else if (day_of_week.toUpperCase().startsWith("SAT")) {
      day = Calendar.SATURDAY;
    } else if (day_of_week.toUpperCase().startsWith("SUN")) {
      day = Calendar.SUNDAY;
    } else {
      throw new ItemNotFoundException("Unknown day of week: " + day_of_week);
    }

    return getDateAfterGivenDate(numDays, startDate, day, dateFormat);
  }

  /**
   * Get a date string with required day of week after the given number of days from the given date
   *
   * @param numDays
   * @param startDate
   * @param day_of_week - Calendar.MONDAY/TUESDAY/WEDNSDAY/THURSDAY/FRIDAY/SATURDAY/SUNDAY
   * @param dateFormat
   * @return
   */
  public static String getDateAfterGivenDate(int numDays, Calendar startDate, int day_of_week, String dateFormat) {
    startDate.add(Calendar.DAY_OF_MONTH, numDays);

    int dayCode = startDate.get(Calendar.DAY_OF_WEEK);
    if (day_of_week > dayCode) {
      startDate.add(Calendar.DAY_OF_MONTH, day_of_week - dayCode);
    } else if (day_of_week < dayCode) {
      startDate.add(Calendar.DAY_OF_MONTH, day_of_week - dayCode + 7);
    }

    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

    Date newDate = startDate.getTime();

    return formatter.format(newDate);
  }

  /**
   * Utility method to find if the given date is before, on, or after today
   *
   * @param date
   * @return -1 if before today, 0 is same as today, 1 if after today
   */
  public static int compareToToday(String when) {
    return compareToToday(when, null);
  }

  /**
   * Utility method to find if the given date is before, on, or after today
   *
   * @param schema
   * @param date
   * @return -1 if before today, 0 is same as today, 1 if after today
   * @throws
   */
  public static int compareToToday(String when, TimeZone timeZone) {
//		String todayStr=getToday("MM/dd/yyyy",timeZone);
    Date today = timeZone == null ? Calendar.getInstance().getTime() : Calendar.getInstance(timeZone).getTime();
    Date date = getCalendarFromString(when).getTime();
    return compareDates(date, today);

  }

  public static boolean isValidDate(String date) {
    try {
      getCalendarFromString(date).getTime();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Compare exact dates, the date can be accurate to seconds
   *
   * @param date1
   * @param date2
   * @return
   */
  public static int compareExactDates(String date1, String date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(DateFunctions.parseDateString(date1));
    cal2.setTime(DateFunctions.parseDateString(date2));

    if (cal1.before(cal2)) {
      return -1;
    } else if (cal1.after(cal2)) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Utility method to judge the given date1 string is before  on or after given date2 string
   *
   * @param date1
   * @param date2
   * @return -1 if date1 before date2,0 the two date string is same date,1  date1 is after date2
   */
  public static int compareDates(String date1, String date2) {
    if (date1.equalsIgnoreCase(date2)) //shortcut for equal string
      return 0;

    Date d1 = getCalendarFromString(date1).getTime();
    Date d2 = getCalendarFromString(date2).getTime();
    return compareDates(d1, d2);
  }

  /**
   * Utility method to judge the given date1 object is before  on or after given date2 object
   *
   * @param d1
   * @param d2
   * @return -1 if date1 before date2,0 the two date is same date,1 date1 is after date2
   */
  public static int compareDates(Date d1, Date d2) {
    if (d1.before(d2))
      return -1;
    else if (d1.after(d2))
      return 1;
    else
      return 0;
  }

  /**
   * @param firstDay
   * @param anotherDay
   * @return
   */
  public static int diffBetween(String firstDay, String anotherDay) {
    Date firstDate = parseDateString(firstDay);
    Date anotherDate = parseDateString(anotherDay);
    return diffBetween(firstDate, anotherDate);
  }

  /**
   * Utility method to get the different days between two given dates
   *
   * @param today
   * @param anotheDay
   * @return number of different days between two date
   */
  public static int diffBetween(Date today, Date anotheDay) {
    long diff = today.getTime() - anotheDay.getTime();
    return Math.round(diff / (float) (1000 * 60 * 60 * 24));
  }

  /**
   * Return different minutes between two dates
   *
   * @param today
   * @param anotheDay
   * @return
   */
  public static long diffMinBetween(Date today, Date anotheDay) {
    long diff = today.getTime() - anotheDay.getTime();
    return diff / (1000 * 60);
  }

  /**
   * Return different minutes between two dates
   *
   * @param today
   * @param anotheDay
   * @return
   */
  public static long diffMinBetween(String today, String anotheDay) {
    return diffMinBetween(parseDateString(today), parseDateString(anotheDay));
  }

  public static String[][] parseCalendarString(String text) {
    String[][] toReturn = new String[2][14];

    int i = text.indexOf(">");
    text = text.substring(i + 1);

    String[] temp = text.split(" ");

    for (i = 0; i < 14; i++) {
      toReturn[0][i] = temp[i * 2];
      toReturn[1][i] = temp[i + 28];
    }
    return toReturn;
  }

  /**
   * Calculate the time difference in seconds
   *
   * @param startTime - the start time in Milliseconds
   * @return - the difference in seconds
   */
  public static int getTimeDiff(long startTime) {
    long endTime = Calendar.getInstance().getTimeInMillis();
    long longDiff = endTime - startTime;
    float seconds = longDiff / (float) 1000;
    int diff = Math.round(seconds);

    return diff;
  }

  /**
   * Verify if date1 is after date2
   *
   * @param date1
   * @param date2
   * @return
   */
  public static boolean isAfter(String date1, String date2) {
    return isAfter(date1, date2, 1);
  }

  /**
   * Verify if date1 is number of days after date2
   *
   * @param date1
   * @param date2
   * @param days
   * @return
   */
  public static boolean isAfter(String date1, String date2, int days) {
    String afterDate = DateFunctions.getDateAfterGivenDay(date1, days);
    return DateFunctions.compareDates(afterDate, date2) == 0;
  }


  /**
   * Utility method to get the different days number between two given dates
   * only compare the days
   *
   * @param fDate
   * @param sDate
   * @return number of different days between two date
   */
  public static int daysBetween(Date fDate, Date sDate) {
    return Integer.parseInt((sDate.getTime() - fDate.getTime()) / (long) (1000 * 24 * 60 * 60) + "");
  }


  public static int daysBetween(String fDate, String sDate) {
    return daysBetween(getCalendarFromString(fDate).getTime(), getCalendarFromString(sDate).getTime());
  }

  /**
   * Method to get the date which is nearest to the day of week
   *
   * @param dateStr
   * @param needDayOfWeek
   * @return
   */
  @SuppressWarnings("deprecation")
  public static String getDateNearestDayOfWeek(String dateStr, String needDayOfWeek) {
    if (StringUtil.isEmpty(dateStr)) {
      dateStr = getToday();
    }
    if (needDayOfWeek.length() > 3) {
      needDayOfWeek = needDayOfWeek.substring(0, 3);
    }

    Date date = parseDateString(dateStr);
    int knownWeekDayIndex = date.getDay() + 1;
    int neededWeekDayIndex = -1;
    for (int i = 0; i < WEEKS_SHORT.length; i++) {
      if (needDayOfWeek.equalsIgnoreCase(WEEKS_SHORT[i])) {
        neededWeekDayIndex = i + 1;
      }
    }

    int plusDaysNum = neededWeekDayIndex - knownWeekDayIndex;
    int minusDaysNum = 7 - neededWeekDayIndex + knownWeekDayIndex;

    if (plusDaysNum < minusDaysNum) {
      return getDateAfterGivenDay(dateStr, plusDaysNum);
    } else {
      return getDateAfterGivenDay(dateStr, -minusDaysNum);
    }
  }

  /**
   * Method to get the date combined by year, month and day with the given date pattern.
   *
   * @param year       - the year string input
   * @param month      - the month string input
   * @param day        - the month string input
   * @param dateFormat - the date format used to generate the full date
   * @return string the formatted date string output (such as M/d/yyyy) after compounding
   */
  public static String combineStringToDate(String year, String month, String day, String dateFormat) {
    if (year.length() <= 0) {
      year = getToday().split("/")[2].trim();
    }
    if (month.length() <= 0) {
      month = getToday().split("/")[0].trim();
    }
    if (day.length() <= 0) {
      day = getToday().split("/")[1].trim();
    }

    Calendar calendar = Calendar.getInstance();
    calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

    return formatDate(calendar.getTime(), dateFormat);
  }

  public static String combineIntToDate(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, day);

    return formatDate(calendar.getTime(), "M/d/yyyy");
  }

  public static String combineIntToDate(int year, int month, int day, String dateFormat) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, day);

    return formatDate(calendar.getTime(), dateFormat);
  }

  /**
   * Overload method to get the date compounded by year, month and day with the default pattern - "M/d/yyyy".
   *
   * @param year
   * @param month
   * @param day
   * @return string the formatted date string output after compounding
   */
  public static String combineStringToDate(String year, String month, String day) {
    return combineStringToDate(year, month, day, "M/d/yyyy");
  }

  /**
   * Get the date which fits the dayOfWeekWithinMonthIndex and needed day of week with year and month conditions,
   * such as 'the 1st Tuesday of November,2010'
   *
   * @param year
   * @param month
   * @param needDayOfWeekWithinMonthIndex - the index of the day of week within this month, such as '2nd Monday'
   * @param needDayOfWeek                 - the day of week needed
   * @return - the date with the default formate 'M/d/yyyy'
   */
  public static String getDateByDayOfWeekWithinMonth(String year, String month, int needDayOfWeekWithinMonthIndex, String needDayOfWeek) {
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    //set the calendar as 1st Day of (month - 1) Month, year Year
    calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);

    //get need day of week's index(from Sunday to Saturday --- 1 to 7)
    int needWeekIndex = -1;
    for (int i = 0; i < WEEKS_SHORT.length; i++) {
      if (WEEKS_SHORT[i].equalsIgnoreCase(needDayOfWeek.substring(0, 3))) {
        needWeekIndex = i + 1;
        break;
      }
    }

    if (needWeekIndex == -1) {
      throw new ActionFailedException("Please check the parameter - week.");
    }

    int counter = 0;
    do {
      if ((calendar.get(Calendar.DAY_OF_WEEK) == needWeekIndex) && (calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) == needDayOfWeekWithinMonthIndex)) {
        break;
      }
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      counter++;
    } while (counter < 30);

    return formatDate(calendar.getTime(), "M/d/yyyy");
  }

  /**
   * get date time for 'toTimeZone'
   *
   * @param dateTime
   * @param format
   * @param fromTimeZone
   * @param toFormat
   * @param toTimeZone
   * @return
   */
  public static String changeDateStampTimeZone(String dateTime, String format, TimeZone fromTimeZone, String toFormat, TimeZone toTimeZone) {
    Date date = DateFunctions.parseDateString(dateTime, format, fromTimeZone);
    SimpleDateFormat dateFormat = new SimpleDateFormat(toFormat);
    dateFormat.setTimeZone(toTimeZone);
    return dateFormat.format(date);
  }

  /**
   * @param baseDate
   * @param years
   * @param months
   * @param days
   * @param toDateFormat
   * @return
   */
  public static String calculateDate(String baseDate, int years, int months, int days, String toDateFormat) {
    Date bDate = DateFunctions.parseDateString(baseDate);
    Calendar cal = Calendar.getInstance();
    cal.setTime(bDate);
    cal.add(Calendar.YEAR, years);
    cal.add(Calendar.MONTH, months);
    cal.add(Calendar.DAY_OF_YEAR, days);
    return DateFunctions.formatDate(cal.getTime(), toDateFormat);
  }

  public static String calculateDate(String baseDate, int years, int months, int days) {
    return calculateDate(baseDate, years, months, days, "M/d/yyyy");
  }

  public static String calculateDate(String baseDate, int years, int days) {
    return calculateDate(baseDate, years, 0, days);
  }

  public static String getLastDateOfMonth(String date) {
    return getLastDateOfMonth(DateFunctions.formatDate(date, "M/d/yyyy"), "M/d/yyyy");
  }

  /**
   * This method was used to return maxnium date of given date
   * For example: given date:2/15/2012, return 2/29/2012
   * given date:2/15/2011, return 2/28/2011
   *
   * @param date
   * @param format
   * @return
   */
  public static String getLastDateOfMonth(String date, String format) {
    Calendar calendar = DateFunctions.getCalendarFromString(date, format);
    int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//maximum day of given month
    calendar.set(Calendar.DATE, day);
    SimpleDateFormat formater = new SimpleDateFormat(format);

    return formater.format(calendar.getTime());
  }

  public static String getFirstDateOfMonth(String date) {
    return getFirstDateOfMonth(DateFunctions.formatDate(date, "M/d/yyyy"), "M/d/yyyy");
  }

  public static String getFirstDateOfMonth(String date, String format) {
    Calendar calendar = DateFunctions.getCalendarFromString(date, format);
    int day = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);//minimum day of given month
    calendar.set(Calendar.DATE, day);
    SimpleDateFormat formater = new SimpleDateFormat(format);

    return formater.format(calendar.getTime());
  }

  public static String getDayOfYear(String date) {
    Date bDate = DateFunctions.parseDateString(date);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(bDate);
    String dayOfYear = String.valueOf(calendar.get(Calendar.DAY_OF_YEAR));
    if (dayOfYear.matches("\\d{1}"))
      dayOfYear = "00" + dayOfYear;
    else if (dayOfYear.matches("\\d{2}"))
      dayOfYear = "0" + dayOfYear;
    return dayOfYear;
  }

  /**
   * Compare date time.
   *
   * @param d1
   * @param d2
   * @return -1 d1 is before d2.
   * 0  d1 is d2.
   * 1  d1 is after d2.
   */
  public static int compareDateTime(String d1, String d2) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    try {
      Date dt1 = df.parse(d1);
      Date dt2 = df.parse(d2);
      if (dt1.getTime() > dt2.getTime()) {
        return 1;// d1 is after d2
      } else if (dt1.getTime() < dt2.getTime()) {
        return -1;// d1 is before d2
      } else {
        return 0;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return 0;
  }

  public static String getDateWithGivenYear(String dateString, String year, int numOfYear, String dateFormat) {
    Calendar calendar = DateFunctions.getCalendarFromString(dateString, dateFormat);
    calendar.set(Calendar.YEAR, Integer.parseInt(year) + numOfYear);
    SimpleDateFormat formater = new SimpleDateFormat(dateFormat);
    Date newDate = calendar.getTime();
    return formater.format(newDate);
  }

  /**
   * This method was used to return a date String with (year+numOfYear)-(month and date for dateString)
   * eg: dateString:2013-5-31, year=2015, numOfYear=1, return value=2016-5-31
   *
   * @param dateString
   * @param year
   * @param numOfYear
   * @return
   */
  public static String getDateWithGivenYear(String dateString, String year, int numOfYear) {
    String pattern = getDateStringPattern(dateString);
    return getDateWithGivenYear(dateString, year, numOfYear, pattern);
  }

  public static XMLGregorianCalendar getXMLGregorianCalendarNow() throws DatatypeConfigurationException {
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
    return now;
  }

  /**
   * Get the booking end day per use type, entry date and number of stay
   */
  public static String generateBookingEndDate(String entUseType, String entDate, String numOfStay, String format) {
    String exp;
    if (entUseType.matches("(d|D)(ay(s)?|aily)")) { // day, days, Day, Days, Daily, daily
      exp = DateFunctions.formatDate(DateFunctions.getDateAfterGivenDay(entDate, Integer.valueOf(numOfStay) - 1), format);
    } else {
      exp = DateFunctions.formatDate(DateFunctions.getDateAfterGivenDay(entDate, Integer.valueOf(numOfStay)), format);
    }
    return exp;
  }

  public static Calendar convertTimeToCalendar(String time) {
    Calendar cal = Calendar.getInstance();
    String times[] = time.split(":");
//		cal.set(Calendar.HOUR, Integer.parseInt(times[0]) > 12 ? Integer.parseInt(times[0]) - 12 : Integer.parseInt(times[0]));
    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
    cal.set(Calendar.MINUTE, Integer.parseInt(times[1]));
    if (times.length < 3) {
      cal.set(Calendar.SECOND, 0);
    } else {
      cal.set(Calendar.SECOND, Integer.parseInt(times[2]));
    }
    cal.set(Calendar.MILLISECOND, 0);

    return cal;
  }

  public static Calendar getDayStart(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal;
  }

  public static Calendar getTodayStart() {
    return getDayStart(Calendar.getInstance());
  }

  public static int getDayOfWeek(String dateStr) {
    Calendar cal = getCalendarFromString(dateStr);
    int dayOfWeek = 0;
    if (cal.get(Calendar.DAY_OF_WEEK) == 1)
      dayOfWeek = 7;
    else
      dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
    return dayOfWeek;
  }

  public static int getDayOfWeekByWeekDay(String dayOfWeek) {
    int value;
    if (dayOfWeek.equalsIgnoreCase("Sun")) {
      value = 1;
    } else if (dayOfWeek.equalsIgnoreCase("Mon")) {
      value = 2;
    } else if (dayOfWeek.equalsIgnoreCase("Tue")) {
      value = 3;
    } else if (dayOfWeek.equalsIgnoreCase("Wed")) {
      value = 4;
    } else if (dayOfWeek.equalsIgnoreCase("Thu")) {
      value = 5;
    } else if (dayOfWeek.equalsIgnoreCase("Fri")) {
      value = 6;
    } else if (dayOfWeek.equalsIgnoreCase("Sat")) {
      value = 7;
    } else {
      throw new ItemNotFoundException("Unknown week day " + dayOfWeek);
    }
    return value;
  }

  /**
   * Utility method to get current date based on defined timezone
   *
   * @return Date  the current date include weekday and timezone like 'Thu Nov 19 01:37:38 EST 2009'
   */
  public static Date getCurrentDate(TimeZone timeZone) {
    Calendar c = timeZone == null ? Calendar.getInstance() : Calendar.getInstance(timeZone);

    return c.getTime();

  }

  public static Date getCurrentDate() {
    return getCurrentDate(null);

  }

  /**
   * Given Date time compare with current date time.
   * -1 given date is before current date time.
   * 0 given date is current date time.
   * 1 given date is after current date time.
   */
  public static int compareWithCurrentDateTime(TimeZone timezone, String date) {
    String current = DateFunctions.formatDate(DateFunctions.getCurrentDate(timezone), "yyyy-MM-dd hh:mm:ss");
    return compareDateTime(date, current);
  }

  public static Date parseDate(String dateStr, String pattern) {
    Locale locale = Locale.US;
    SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern, locale);
    Date formattedDate = null;
    try {
      formattedDate = dateFormatter.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return formattedDate;
  }

  public static List<String> getAllWeekdaysBetween(String firstDate, String secondDate, String pattern) {
    List<String> weekdays = new ArrayList<String>();
    int diff = diffBetween(secondDate, firstDate);
    Calendar cal = Calendar.getInstance();
    String date = "";
    for (int i = 0; i <= diff; i++) {
      date = DateFunctions.getDateAfterGivenDay(firstDate, i);
      cal.setTime(parseDate(date, pattern));
      if (cal.get(Calendar.DAY_OF_WEEK) != 1 && cal.get(Calendar.DAY_OF_WEEK) != 7) {
        weekdays.add(date);
      }
    }
    return weekdays;
  }
}

