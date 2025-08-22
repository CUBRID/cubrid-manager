/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.slf4j.Logger;

/**
 * Date and Time and Timestamp and DateTime type utility
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-22 created by pangqiren
 */
public final class DateUtil {
    private static final Logger LOGGER = LogUtil.getLogger(DateUtil.class);
    public static final String EMPTY_STRING = "";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";

    private DateUtil() {}

    private static final String[] SUPPORTED_TIME_PATTERNS = {
        "hh:mm:ss a",
        "a hh:mm:ss",
        "HH:mm:ss",
        "hh:mm a",
        "a hh:mm",
        "HH:mm",
        "''hh:mm:ss a''",
        "''a hh:mm:ss''",
        "''HH:mm:ss''",
        "''hh:mm a''",
        "''a hh:mm''",
        "''HH:mm''",
    };

    /**
     * support multi input time string, return the timestamp, a long type with unit second
     * <li>"hh:mm[:ss] a"
     * <li>"a hh:mm[:ss]"
     * <li>"HH:mm[:ss]"
     *
     * @param timeString String time string eg: 11:12:13 am
     * @return long timestamp
     * @throws ParseException parse exception
     */
    public static long getTime(String timeString) throws ParseException {

        for (String datePattern : SUPPORTED_TIME_PATTERNS) {
            if (validateDateTime(timeString, datePattern)) {
                try {
                    return getTimestamp(timeString, datePattern);
                } catch (Exception e) {
                    // it is designed not to run at here,so throws nothing
                    LOGGER.error("An unexpected exception is throwed.\n" + e.getMessage());
                }
            } else {
                continue;
            }
        }

        throw new ParseException("Unparseable date: \"" + timeString + "\"", 0);
    }

    private static final String[] SUPPORTED_DATE_PATTERNS = {
        "yyyy/MM/dd",
        "yyyy-MM-dd",
        "yyyy.MM.dd",
        "dd/MM/yyyy",
        "MM/dd/yyyy",
        "MM/dd",
        "''MM/dd/yyyy''",
        "''yyyy/MM/dd''",
        "''yyyy-MM-dd''"
    };

    /**
     * support multi input date string, return the timestamp, a long type with unit second
     * <li>"MM/dd/yyyy",
     * <li>"yyyy/MM/dd",
     * <li>"yyyy-MM-dd"
     *
     * @param dateString String date string eg: 2009-02-20
     * @return long timestamp
     * @throws ParseException parse exception
     */
    public static long getDate(String dateString) throws ParseException {
        for (String datePattern : SUPPORTED_DATE_PATTERNS) {
            if (validateDateTime(dateString, datePattern)) {
                try {
                    return getTimestamp(dateString, datePattern);
                } catch (Exception e) {
                    // it is designed not to run at here,so throws nothing
                    LOGGER.debug("An unexpected exception is throwed.\n" + e.getMessage());
                }
            } else {
                continue;
            }
        }
        throw new ParseException("Unparseable date: \"" + dateString + "\"", 0);
    }

    /**
     * Get date from dateString and pattern
     *
     * @param dateString
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static long getDate(String dateString, String pattern) throws ParseException {
        if (!StringUtil.isEmpty(pattern)) {
            try {
                return getTimestamp(dateString, pattern);
            } catch (Exception e) {
                // it is designed not to run at here,so throws nothing
                LOGGER.error("An unexpected exception is throwed.\n" + e.getMessage());
            }
        }

        for (String datePattern : SUPPORTED_DATE_PATTERNS) {
            if (validateDateTime(dateString, datePattern)) {
                try {
                    return getTimestamp(dateString, datePattern);
                } catch (Exception e) {
                    // it is designed not to run at here,so throws nothing
                    LOGGER.error("An unexpected exception is throwed.\n" + e.getMessage());
                }
            } else {
                continue;
            }
        }
        throw new ParseException("Unparseable date: \"" + dateString + "\"", 0);
    }

    private static final String[] SUPPORTED_TIMESTAMP_PATTERNS = {
        "yyyy/MM/dd a hh:mm:ss",
        "yyyy/MM/dd hh:mm:ss a",
        "yyyy/MM/dd a hh:mm",
        "yyyy/MM/dd hh:mm a",
        "yyyy/MM/dd a hh",
        "yyyy/MM/dd hh a",
        "yyyy-MM-dd a hh:mm:ss",
        "yyyy-MM-dd hh:mm:ss a",
        "yyyy-MM-dd a hh:mm",
        "yyyy-MM-dd hh:mm a",
        "yyyy-MM-dd a hh",
        "yyyy-MM-dd hh a",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "hh:mm:ss a MM/dd/yyyy",
        "a hh:mm:ss MM/dd/yyyy",
        "HH:mm:ss MM/dd/yyyy",
        "yyyy/MM/dd a hh:mm",
        "yyyy-MM-dd a hh:mm",
        "yyyy/MM/dd HH:mm",
        "yyyy-MM-dd HH:mm",
        "hh:mm a MM/dd/yyyy",
        "HH:mm MM/dd/yyyy",
        "MM/dd/yyyy hh:mm:ss a",
        "MM/dd/yyyy a hh:mm:ss",
        "MM/dd/yyyy HH:mm:ss",
        "MM-dd-yyyy hh:mm:ss a",
        "MM-dd-yyyy a hh:mm:ss",
        "MM-dd-yyyy HH:mm:ss",
        "''yyyy/MM/dd a hh:mm:ss''",
        "''yyyy-MM-dd a hh:mm:ss''",
        "''yyyy/MM/dd HH:mm:ss''",
        "''yyyy-MM-dd HH:mm:ss''",
        "''hh:mm:ss a MM/dd/yyyy''",
        "''HH:mm:ss MM/dd/yyyy''",
        "''yyyy/MM/dd a hh:mm''",
        "''yyyy-MM-dd a hh:mm''",
        "''yyyy/MM/dd HH:mm''",
        "''yyyy-MM-dd HH:mm''",
        "''hh:mm a MM/dd/yyyy''",
        "''HH:mm MM/dd/yyyy''",
        "''MM/dd/yyyy hh:mm:ss a''",
        "''MM/dd/yyyy a hh:mm:ss''",
        "''MM/dd/yyyy HH:mm:ss''",
        "''MM-dd-yyyy hh:mm:ss a''",
        "''MM-dd-yyyy a hh:mm:ss''",
        "''MM-dd-yyyy HH:mm:ss''",
        "MM/dd/yyyy",
        "MM/dd"
    };

    /**
     * support multi input data string, return the timestamp, a long type with unit second
     * <li>"hh:mm[:ss] a MM/dd/yyyy",
     * <li>"HH:mm[:ss] MM/dd/yyyy",
     * <li>"yyyy/MM/dd a hh:mm[:ss]",
     * <li>"yyyy-MM-dd a hh:mm[:ss]",
     * <li>"yyyy/MM/dd HH:mm[:ss]",
     * <li>"yyyy-MM-dd HH:mm[:ss]"
     *
     * @param dateString String date string eg: 2009-02-20 16:42:46
     * @return long timestamp
     * @throws ParseException parse exception
     */
    public static long getTimestamp(String dateString) throws ParseException {
        try {
            for (String datePattern : SUPPORTED_TIMESTAMP_PATTERNS) {
                if (validateTimestamp(dateString, datePattern)) {
                    return getTimestamp(dateString, datePattern);
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            // it is designed not to run at here,so throws nothing
            LOGGER.error("An unexpected exception is throwed.\n" + e.getMessage());
        }
        throw new ParseException("Unparseable date: \"" + dateString + "\"", 0);
    }

    private static final String[] SUPPORTED_DATETIME_PATTERNS = {
        "yyyy/MM/dd a hh:mm:ss.SSS",
        "yyyy/MM/dd hh:mm:ss.SSS a",
        "yyyy/MM/dd a hh:mm:ss",
        "yyyy/MM/dd hh:mm:ss a",
        "yyyy/MM/dd a hh:mm",
        "yyyy/MM/dd hh:mm a",
        "yyyy/MM/dd a hh",
        "yyyy/MM/dd hh a",
        "yyyy-MM-dd a hh:mm:ss.SSS",
        "yyyy-MM-dd hh:mm:ss.SSS a",
        "yyyy-MM-dd a hh:mm:ss",
        "yyyy-MM-dd hh:mm:ss a",
        "yyyy-MM-dd a hh:mm",
        "yyyy-MM-dd hh:mm a",
        "yyyy-MM-dd a hh",
        "yyyy-MM-dd hh a",
        "yyyy/MM/dd HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "hh:mm:ss.SSS a MM/dd/yyyy",
        "a hh:mm:ss.SSS MM/dd/yyyy",
        "HH:mm:ss.SSS MM/dd/yyyy",
        "yyyy/MM/dd a hh:mm",
        "''yyyy/MM/dd a hh:mm:ss.SSS''",
        "''yyyy/MM/dd hh:mm:ss.SSS a''",
        "''yyyy-MM-dd a hh:mm:ss.SSS''",
        "''yyyy-MM-dd hh:mm:ss.SSS a''",
        "''yyyy/MM/dd HH:mm:ss.SSS''",
        "''yyyy-MM-dd HH:mm:ss.SSS''",
        "''hh:mm:ss.SSS a MM/dd/yyyy''",
        "''HH:mm:ss.SSS MM/dd/yyyy''",
        "yyyy-MM-dd a hh:mm:ss",
        "yyyy-MM-dd hh:mm:ss a",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "hh:mm:ss a MM/dd/yyyy",
        "HH:mm:ss MM/dd/yyyy",
        "yyyy/MM/dd a hh:mm",
        "yyyy-MM-dd a hh:mm",
        "yyyy/MM/dd HH:mm",
        "yyyy-MM-dd HH:mm",
        "hh:mm a MM/dd/yyyy",
        "HH:mm MM/dd/yyyy",
        "''yyyy/MM/dd a hh:mm:ss''",
        "''yyyy/MM/dd hh:mm:ss a''",
        "''yyyy-MM-dd a hh:mm:ss''",
        "''yyyy-MM-dd hh:mm:ss a''",
        "''yyyy/MM/dd HH:mm:ss''",
        "''yyyy-MM-dd HH:mm:ss''",
        "''hh:mm:ss a MM/dd/yyyy''",
        "''HH:mm:ss MM/dd/yyyy''",
        "''yyyy/MM/dd a hh:mm''",
        "''yyyy-MM-dd a hh:mm''",
        "''yyyy/MM/dd HH:mm''",
        "''yyyy-MM-dd HH:mm''",
        "''hh:mm a MM/dd/yyyy''",
        "''HH:mm MM/dd/yyyy''"
    };

    /**
     * support multi input data string, return the timestamp, a long type with unit second
     * <li>"hh:mm[:ss].[SSS] a MM/dd/yyyy",
     * <li>"HH:mm[:ss].[SSS] MM/dd/yyyy",
     * <li>"yyyy/MM/dd a hh:mm[:ss].[SSS]",
     * <li>"yyyy-MM-dd a hh:mm[:ss].[SSS]",
     * <li>"yyyy/MM/dd HH:mm[:ss].[SSS]",
     * <li>"yyyy-MM-dd HH:mm[:ss].[SSS]"
     *
     * @param dateTimeString String date string eg: 2009-02-20 16:42:46
     * @return long timestamp
     * @throws ParseException parse exception
     */
    public static long getDatetime(String dateTimeString) throws ParseException {
        for (String datePattern : SUPPORTED_DATETIME_PATTERNS) {
            if (validateDateTime(dateTimeString, datePattern)) {
                try {
                    Date date = parseTimeFormatLocale(datePattern, dateTimeString);
                    return date.getTime();
                } catch (Exception e) {
                    // it is designed not to run at here,so throws nothing
                    LOGGER.error("An unexpected exception is throwed.\n" + e.getMessage());
                }
            } else {
                continue;
            }
        }
        throw new ParseException("Unparseable date: \"" + dateTimeString + "\"", 0);
    }

    /**
     * return the datetime pattern for a given datetime string
     *
     * @param datetimeString given the date time string
     * @return String
     */
    public static String getDatetimeFormatPattern(String datetimeString) {
        for (String datePattern : SUPPORTED_DATETIME_PATTERNS) {
            if (validateDateTime(datetimeString, datePattern)) {
                return datePattern;
            } else {
                continue;
            }
        }

        return null;
    }

    /**
     * Format a datetime string to another datetime string <br>
     * Note: use SimpleDateFormat to parse a "2009/12/12 12:33:00.4", the result is "2009/12/12
     * 12:33:00.004", but expected "2009/12/12 12:33:00.400", <br>
     * so this function first to check whether the millisecond part is 3 digital, if not, padding
     * with 0
     *
     * @param dateString given the date string
     * @param newDatetimePattern new datetime pattern
     * @return String
     */
    public static String formatDateTime(String dateString, String newDatetimePattern) {
        String srcDatetimePattern = getDatetimeFormatPattern(dateString);
        if (srcDatetimePattern == null) {
            return null;
        }

        long timestamp = 0;
        int start = srcDatetimePattern.indexOf("SSS");
        String paddingDateString = dateString;
        if (-1 != start) {
            String firstPartPattern = srcDatetimePattern.substring(0, start);
            ParsePosition pp = new ParsePosition(0);
            DateFormat formatter = getDateFormat(firstPartPattern);
            formatter.parse(dateString, pp);
            int firstIndex = pp.getIndex();
            StringBuffer bf = new StringBuffer();
            int i = 0;
            bf.append(dateString.substring(0, firstIndex));
            int count = 0;
            for (i = firstIndex; i < dateString.length(); i++) {
                char c = dateString.charAt(i);
                if (c >= '0' && c <= '9') {
                    bf.append(c);
                    count++;
                } else {
                    break;
                }
            }
            if (count < 3) {
                for (int j = 0; j < 3 - count; j++) {
                    bf.append("0");
                }
            }
            for (; i < dateString.length(); i++) {
                char c = dateString.charAt(i);
                bf.append(c);
            }
            paddingDateString = bf.toString();
        }

        try {
            Date date = parseTimeFormatLocale(srcDatetimePattern, paddingDateString);
            if (date == null) {
                LOGGER.error("The parseTimeFormatLocale's result is a null.");
                return null;
            } else {
                timestamp = date.getTime();
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return getDatetimeString(timestamp, newDatetimePattern);
    }

    /**
     * validate whether a date string can be parsed by a given date pattern
     *
     * @param dateString String a date string
     * @param datePattern String a given date pattern
     * @return boolean true: can be parsed; false: can not
     */
    public static boolean validateDateTime(String dateString, String datePattern) {
        try {
            Date date = parseTimeFormatLocale(datePattern, dateString);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * validate whether a date string can be parsed by a given date pattern
     *
     * @param dateString String a date string
     * @param datePattern String a given date pattern
     * @return boolean true: can be parsed; false: can not
     */
    public static boolean validateTimestamp(String dateString, String datePattern) {
        try {
            Date date = parseTimeFormatLocale(datePattern, dateString);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * return a standard DateFormat instance, which has a given Local, TimeZone, and with a strict
     * check
     *
     * @param datePattern the date pattern
     * @return DateFormat
     */
    public static DateFormat getDateFormat(String datePattern) {
        DateFormat formatter = new SimpleDateFormat(datePattern, Locale.getDefault());
        formatter.setLenient(false);
        // formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter;
    }

    /**
     * return a standard DateFormat instance, which has a given Local, TimeZone, and with a strict
     * check
     *
     * @param datePattern the date pattern
     * @param locale the specified locale to get the correspond date format
     * @return DateFormat
     */
    public static DateFormat getDateFormat(String datePattern, Locale locale) {
        DateFormat formatter = new SimpleDateFormat(datePattern, locale);
        formatter.setLenient(false);
        // formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter;
    }

    /**
     * parse date string with a given date pattern, return long type timestamp, unit:micro second
     *
     * <p>precondition: it is better to call cubridmanager.CommonTool.validateTimestamp(String,
     * String) first to void throwing an ParseException
     *
     * @param dateString String date string eg: 2009-02-20 16:42:46
     * @param datePattern String date pattern eg: yyyy-MM-dd HH:mm:ss
     * @return long timestamp
     * @throws ParseException parse exception
     */
    public static long getTimestamp(String dateString, String datePattern) throws ParseException {
        Date date = parseTimeFormatLocale(datePattern, dateString);
        if (date == null) {
            LOGGER.error("The parseTimeFormatLocale is a null.");
            return 0;
        }

        return date.getTime();
    }

    /**
     * format a timestamp into a given date pattern string
     *
     * @param timestamp long type timestamp, unit:micro second
     * @param datePattern String a given date pattern
     * @return String
     */
    public static String getDatetimeString(long timestamp, String datePattern) {
        DateFormat formatter = getDateFormat(datePattern);
        Date date = new Date(timestamp);
        return formatter.format(date);
    }

    public static String getDatetimeString(Date date, String datePattern) {
        DateFormat formatter = getDateFormat(datePattern);
        return formatter.format(date);
    }

    public static String getDatetimeStringOnNow(String datePattern) {
        DateFormat formatter = getDateFormat(datePattern);
        return formatter.format(new Date());
    }

    public static Date parseTimeFormatLocale(String datePattern, String dateString)
            throws ParseException {
        if (StringUtil.isEmpty(datePattern)) {
            LOGGER.debug("The datePattern is empty.");
            return null;
        }

        if (StringUtil.isEmpty(dateString)) {
            LOGGER.debug("The dateString is empty.");
            return null;
        }

        DateFormat format = getDateFormat(datePattern);

        try {
            Date date = format.parse(dateString);
            return date;
        } catch (ParseException pe) {
            try {
                format = getDateFormat(datePattern, Locale.US);
                Date date = format.parse(dateString);
                return date;
            } catch (Exception e) {
                LOGGER.debug("", e);
            }
        }

        throw new ParseException("Unparseable date: \"" + dateString + "\"", 0);
    }

    /**
     * convert source date string to target format.
     *
     * @param dateString
     * @param dstPatternString
     * @return converted string
     * @throws ParseException
     */
    public static String formatTimestamp(String dateString, String dstPatternString)
            throws ParseException {
        return formatTimestamp(dateString, "", dstPatternString);
    }

    /**
     * convert source date string to target format.
     *
     * @param dateString source input date string
     * @param srcPatternString specify the format of source input
     * @param dstPatternString target pattern used for format the input string
     * @return converted string
     * @throws ParseException
     */
    public static String formatTimestamp(
            String dateString, String srcPatternString, String dstPatternString)
            throws ParseException {
        if (null == dateString || EMPTY_STRING.equals(dateString)) {
            throw new ParseException("input String can not be null!", 0);
        }

        if (null == dstPatternString || ("").equals(dstPatternString)) {
            throw new ParseException("target pattern can not be null!", 0);
        }

        long time = -1;
        if (null == srcPatternString || ("").equals(srcPatternString)) {
            time = getTimestamp(dateString);
        } else {
            time = getTimestamp(dateString, srcPatternString);
        }

        if (time != -1) {
            return getDatetimeString(time, dstPatternString);
        }

        throw new ParseException("Unparseable date: \"" + dateString + "\"", 0);
    }
}
