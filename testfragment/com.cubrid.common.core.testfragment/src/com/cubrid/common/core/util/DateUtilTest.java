package com.cubrid.common.core.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

public class DateUtilTest extends TestCase {
    public void testParseTimeFormatLocale() {
        Calendar cal = Calendar.getInstance();

        try {
            Date d = DateUtil.parseTimeFormatLocale("", "");
            assertNull(d);
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            Date d = DateUtil.parseTimeFormatLocale("yyyy-MM-dd", "");
            assertNull(d);
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            Date d = DateUtil.parseTimeFormatLocale("", "2012-05-10");
            assertNull(d);
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            Date d = DateUtil.parseTimeFormatLocale("yyyy", "-2012-05-10");
            assertNotNull(d);
        } catch (ParseException e) {
        }

        try {
            Date d = DateUtil.parseTimeFormatLocale("yyyy-MM-dd", "2012-05-10");
            cal.setTime(d);
            assertEquals(cal.get(Calendar.YEAR), 2012);
            assertEquals(cal.get(Calendar.MONTH), 4);
            assertEquals(cal.get(Calendar.DAY_OF_MONTH), 10);
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            Date d = DateUtil.parseTimeFormatLocale("yyyy.MM.dd", "2012.05.10");
            cal.setTime(d);
            assertEquals(cal.get(Calendar.YEAR), 2012);
            assertEquals(cal.get(Calendar.MONTH), 4);
            assertEquals(cal.get(Calendar.DAY_OF_MONTH), 10);
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            Date d = DateUtil.parseTimeFormatLocale("yyyy/MM/dd", "2012/05/10");
            cal.setTime(d);
            assertEquals(cal.get(Calendar.YEAR), 2012);
            assertEquals(cal.get(Calendar.MONTH), 4);
            assertEquals(cal.get(Calendar.DAY_OF_MONTH), 10);
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            Date d = DateUtil.parseTimeFormatLocale("yyyy-MM-dd HH:mm:ss", "2012-05-10 13:01:20");
            cal.setTime(d);
            assertEquals(cal.get(Calendar.YEAR), 2012);
            assertEquals(cal.get(Calendar.MONTH), 4);
            assertEquals(cal.get(Calendar.DAY_OF_MONTH), 10);
            assertEquals(cal.get(Calendar.HOUR_OF_DAY), 13);
            assertEquals(cal.get(Calendar.MINUTE), 1);
            assertEquals(cal.get(Calendar.SECOND), 20);
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            Date d = DateUtil.parseTimeFormatLocale("yyyy-MM-dd hh:mm:ss", "2012-05-10 11:01:20");
            cal.setTime(d);
            assertEquals(cal.get(Calendar.YEAR), 2012);
            assertEquals(cal.get(Calendar.MONTH), 4);
            assertEquals(cal.get(Calendar.DAY_OF_MONTH), 10);
            assertEquals(cal.get(Calendar.HOUR_OF_DAY), 11);
            assertEquals(cal.get(Calendar.MINUTE), 1);
            assertEquals(cal.get(Calendar.SECOND), 20);
        } catch (ParseException e) {
            assertTrue(false);
        }
    }

    public void testGetTimestamp() {
        Calendar cal = Calendar.getInstance();

        try {
            long ts = DateUtil.getTimestamp("2012-05-30 pm 2:20:21");
            cal.setTimeInMillis(ts);
            assertEquals(cal.get(Calendar.YEAR), 2012);
            assertEquals(cal.get(Calendar.MONTH), 4);
            assertEquals(cal.get(Calendar.DAY_OF_MONTH), 30);
            assertEquals(cal.get(Calendar.HOUR_OF_DAY), 14);
            assertEquals(cal.get(Calendar.MINUTE), 20);
            assertEquals(cal.get(Calendar.SECOND), 21);
        } catch (ParseException e) {
            assertTrue(false);
        }
    }

    public void testFormatTimestamp() {
        /*Error*/
        try {
            DateUtil.formatTimestamp("", "", "");
            assertTrue(false);
        } catch (ParseException e) {
            assertTrue(true);
        }

        /*Error*/
        try {
            DateUtil.formatTimestamp("2012/05/10 01:11:12", "", "");
        } catch (ParseException e) {
            assertTrue(true);
        }

        /*Error*/
        try {
            DateUtil.formatTimestamp(
                    "-2012/05/10 01:11:12", "MM, dd, yyyy hhmmss", "MM-dd-yyyy hhmmss");
        } catch (ParseException e) {
            assertTrue(true);
        }

        /*Error*/
        try {
            DateUtil.formatTimestamp("2012/05/10 01:11:12", "", "MM-dd-yyyy hhmmss");
        } catch (ParseException e) {
            assertTrue(true);
        }
        /*Right*/
        try {
            DateUtil.formatTimestamp(
                    "2012/05/10 01:11:12", "MM, dd, yyyy hhmmss", "MM-dd-yyyy hhmmss");
        } catch (ParseException e) {
            assertTrue(true);
        }

        try {
            String s = DateUtil.formatTimestamp("2012/05/10 01:11:12", "MM, dd, yyyy hhmmss");
            assertEquals(s, "05, 10, 2012 011112");
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            String s = DateUtil.formatTimestamp("2012-05-10 01:11:12", "MM, dd, yyyy hhmmss");
            assertEquals(s, "05, 10, 2012 011112");
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            DateUtil.formatTimestamp("2012.05.10 01:11:12", "MM, dd, yyyy hhmmss");
            assertFalse(true);
        } catch (ParseException e) {
            assertTrue(true);
        }

        try {
            String s = DateUtil.formatTimestamp("2012/05/10 01:11:12", "MM, dd, yyyy hhmmss");
            assertEquals(s, "05, 10, 2012 011112");
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            String s = DateUtil.formatTimestamp("05-10-2012 01:11:12", "MM, dd, yyyy hhmmss");
            assertEquals(s, "05, 10, 2012 011112");
        } catch (ParseException e) {
            assertTrue(false);
        }

        try {
            DateUtil.formatTimestamp("2012/05/10", "mm, dd, yyyy");
            assertTrue(false);
        } catch (ParseException e) {
            assertTrue(true);
        }
    }

    public void testValidateTimestamp() {
        assertTrue(DateUtil.validateTimestamp("2012/05/10", "yyyy/mm/dd"));
        assertFalse(DateUtil.validateTimestamp("-2012/05/10", "yyyy/mm/dd"));
    }

    public void testValidateDateTime() {
        assertTrue(DateUtil.validateDateTime("2012/05/10", "yyyy/mm/dd"));
        assertFalse(DateUtil.validateDateTime("-2012/05/10", "yyyy/mm/dd"));
    }
}
