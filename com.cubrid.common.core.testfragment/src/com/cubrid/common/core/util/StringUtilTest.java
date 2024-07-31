package com.cubrid.common.core.util;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

    public void testStr2Int() {
        String str1 = "1234";
        int val1 = StringUtil.str2Int(str1);
        assertEquals(val1, 1234);
        String str2 = "str2";
        int val2 = StringUtil.str2Int(str2);
        assertEquals(val2, 0);
    }

    public void testStr2Double() {
        String str1 = "123.123";
        double val1 = StringUtil.str2Double(str1);
        assertEquals(val1, 123.123);
        String str2 = "str2";
        double val2 = StringUtil.str2Double(str2);
        assertEquals(val2, 0.0);
    }

    public void testStr2Boolean() {
        StringUtil.strYN2Boolean(null);
        assertFalse(StringUtil.strYN2Boolean(null));
        assertTrue(StringUtil.strYN2Boolean("y"));
        assertFalse(StringUtil.strYN2Boolean("n"));

        assertEquals("y", StringUtil.yn(true));
        assertEquals("y", StringUtil.yn(true));

        assertEquals("YES", StringUtil.yesno(true));
        assertEquals("NO", StringUtil.yesno(false));
    }

    public void testIp() {
        String ip = StringUtil.getIp("localhost");
        StringUtil.isIpEqual("localhost", ip);

        ip = StringUtil.getIp("127.0.0.1");
        StringUtil.isIpEqual("127.0.0.1", ip);

        StringUtil.getIp("whoami");

        assertFalse(StringUtil.isIpEqual("localhost", "255.255.255.255"));
    }

    public void testAppendPrefixOnColumns() {
        String sql =
                ""
                        + "    aa.\"code\" tt, // test1\n"
                        + "    v.\"name\" as aa, --test2\n"
                        + "    t.\"gender\" /* test 3 */, \n"
                        + "    tes.\"nation_code\", /* test -- .// test 4 */\n"
                        + "    \"nation_code\", /* /* test 5 * / */\n"
                        + "    1 AS cc,\n"
                        + "    (1) AS dd,\n"
                        + "    TRIM(code),\n"
                        + "    \"event\" -- asdf";

        String res =
                ""
                        + "    PREFIX.\"code\" tt, // test1\n"
                        + "    PREFIX.\"name\" as aa, --test2\n"
                        + "    PREFIX.\"gender\" /* test 3 */, \n"
                        + "    PREFIX.\"nation_code\", /* test -- .// test 4 */\n"
                        + "    PREFIX.\"nation_code\", /* /* test 5 * / */\n"
                        + "    1 AS cc,\n"
                        + "    (1) AS dd,\n"
                        + "    TRIM(code),\n"
                        + "    PREFIX.\"event\" -- asdf";

        String result = StringUtil.appendPrefixOnColumns(sql, "PREFIX.");
        assertEquals(result, res);
    }

    public void testExtractQueries() {
        StringBuilder sb = new StringBuilder("--");
        sb.append(StringUtil.NEWLINE);
        sb.append("select * from agent;");
        sb.append(StringUtil.NEWLINE);
        sb.append("/* ssss").append("*/");
        sb.append(StringUtil.NEWLINE);
        sb.append("SHOW VIEW TABLES");
        sb.append(StringUtil.NEWLINE);
        sb.append("CREATE TABLE \"aaa\"(\"id\" integer)");
        sb.append(StringUtil.NEWLINE);
        sb.append("DROP TABLE \"aaa\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("ALTER TABLE \"actions\" DROP COLUMN \"action_id\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("INSERT INTO actions values(10,10)");
        sb.append(StringUtil.NEWLINE);
        sb.append("UPDATE actions set \"action_id\" = 3");
        sb.append(StringUtil.NEWLINE);
        sb.append("DELETE FROM ACTIONS");

        assertFalse(StringUtil.extractQueries(sb.toString()).isEmpty());
    }

    public void testIsValidNameLength() {
        assertTrue(StringUtil.isValidNameLength("xxx", 3));

        assertFalse(StringUtil.isValidNameLength("xxx", 2));
        assertFalse(StringUtil.isValidNameLength(null, 5));
    }

    public void testGetIp() {
        StringUtil.getIp("127.0.0.1");
        StringUtil.getIp("");
        StringUtil.isSpaceChar(' ');
        StringUtil.isSpaceChar('\t');
        StringUtil.isSpaceChar('\r');
        StringUtil.isSpaceChar('\n');
        assertFalse(StringUtil.getIpList("127.0.0.1").isEmpty());
        assertTrue(StringUtil.getIpList("").isEmpty());
    }

    public void testImplode() {
        String[] a = {"1", "2"};
        assertEquals("1#2", StringUtil.implode("#", a));
        String[] b = {"1", "2", "3", "4", "5"};
        assertEquals("1#2#3#4#5", StringUtil.implode("#", b, -255));
        assertEquals("1#2#3#4#5", StringUtil.implode("#", b, -1));
        assertEquals("1#2#3#4#5", StringUtil.implode("#", b, 0));
        assertEquals("2#3#4#5", StringUtil.implode("#", b, 1));
        assertEquals("3#4#5", StringUtil.implode("#", b, 2));
        assertEquals("4#5", StringUtil.implode("#", b, 3));
        assertEquals("5", StringUtil.implode("#", b, 4));
        assertEquals("", StringUtil.implode("#", b, 5));
        assertEquals("", StringUtil.implode("#", b, 100));
    }

    public void testMD5() {
        assertNotNull(StringUtil.md5("123"));
        assertEquals("", StringUtil.md5(null));
    }

    public void testReplace() {
        assertEquals("", StringUtil.replace("", "a", "b"));
        assertEquals("a", StringUtil.replace("a", "", "b"));
        assertEquals("bb", StringUtil.replace("aa", "a", "b"));

        StringBuilder sb = new StringBuilder();
        StringUtil.replace(sb, "a", "b");

        sb.append("aa");
        StringUtil.replace(sb, "", "b");
        StringUtil.replace(sb, "a", "b");
    }

    public void testRepeat() {

        assertEquals("aaaa", StringUtil.repeat("a", 4));
    }

    public void testFloat() {
        assertEquals(0.0f, StringUtil.floatValue(null));
        assertEquals(0.0f, StringUtil.floatValue("a"));
        assertEquals(1f, StringUtil.floatValue("1"));
    }

    public void testBooleanValue() {
        assertEquals(false, StringUtil.booleanValue(null));
        assertEquals(false, StringUtil.booleanValue("a"));
        assertEquals(true, StringUtil.booleanValue("1"));

        assertEquals(false, StringUtil.booleanValueWithYN(null));
        assertEquals(false, StringUtil.booleanValueWithYN("a"));
        assertEquals(true, StringUtil.booleanValueWithYN("Y"));
    }

    public void testIntValue() {
        assertEquals(0, StringUtil.intValue(null));
        assertEquals(0, StringUtil.intValue("a"));
        assertEquals(1, StringUtil.intValue("1"));

        assertEquals(-1, StringUtil.intValue(null, -1));
        assertEquals(-1, StringUtil.intValue("a", -1));
        assertEquals(1, StringUtil.intValue("1", -1));
    }

    public void testCountSpace() {
        assertEquals(3, StringUtil.countSpace("   aa"));
    }

    public void testGetStackTrace() {
        assertNotNull(StringUtil.getStackTrace(new Exception("aa"), "\n"));
    }

    public void testEmpty() {
        assertTrue(StringUtil.isEmpty(""));
        assertTrue(StringUtil.isEmpty("\t"));
        assertTrue(!StringUtil.isEmpty("a"));

        assertFalse(StringUtil.isNotEmpty(""));
        assertFalse(StringUtil.isNotEmpty("\t"));
        assertTrue(StringUtil.isNotEmpty("a"));
    }

    public void testOthers() {

        StringUtil.appendPrefixOnColumns("user", "_id");
        StringUtil.appendPrefixOnColumns("-- select * from user", "_id");
        StringUtil.appendPrefixOnColumns("// select * from user", "_id");
        StringUtil.appendPrefixOnColumns("/* select * from user */", "_id");
        StringUtil.appendPrefixOnColumns("/* select * from user */ \n select * from user", "_id");

        List<String> strList1 = new ArrayList<String>();
        List<String> strList2 = new ArrayList<String>();

        strList1.add("a");
        strList2.add("a");

        assertTrue(StringUtil.compare(strList1, strList2));

        strList2.add("b");
        assertFalse(StringUtil.compare(strList1, strList2));

        String[] arr = {};
        assertTrue(StringUtil.isNotEmptyAll(arr));
        assertFalse(StringUtil.isSomeEmpty(arr));

        String[] arr2 = {""};
        assertFalse(StringUtil.isNotEmptyAll(arr2));
        assertTrue(StringUtil.isSomeEmpty(arr2));
        assertTrue(StringUtil.str2Double("a") == 0.0);

        assertTrue(StringUtil.longValue("0") == 0);
        assertTrue(StringUtil.longValue("3") == 3);
        assertTrue(StringUtil.longValue("a") == 0);
        assertTrue(StringUtil.longValue(null) == 0);
        assertTrue(StringUtil.str2Double("1") == 1.0);

        assertTrue(StringUtil.doubleValue(null, 1) == 1.0);
        assertTrue(StringUtil.doubleValue("1", 2) == 1.0);
        assertTrue(StringUtil.doubleValue("a", 2) == 2.0);

        assertTrue(StringUtil.longValue(null, 1) == 1);
        assertTrue(StringUtil.longValue("1", 2) == 1);
        assertTrue(StringUtil.longValue("a", 2) == 2);

        assertFalse(StringUtil.isEqualIgnoreCase(null, "a"));
        assertFalse(StringUtil.isEqualIgnoreCase("a", null));
        assertFalse(StringUtil.isEqualIgnoreCase("a", "b"));
        assertTrue(StringUtil.isEqualIgnoreCase("a", "a"));
    }

    public void testIsEqual() {
        assertFalse(StringUtil.isEqual(null, "a"));
        assertFalse(StringUtil.isEqual("", "a"));
        assertTrue(StringUtil.isEqual("a", "a"));

        assertFalse(StringUtil.isEqual(null, "a", false));
        assertFalse(StringUtil.isEqual("", "a", false));
        assertTrue(StringUtil.isEqual("a", "a", false));
        assertTrue(StringUtil.isEqual("a ", "a", true));
        assertTrue(StringUtil.isEqual(" a", " a", true));

        assertFalse(StringUtil.isEqualNotIgnoreNull(null, "a"));
        assertFalse(StringUtil.isEqualNotIgnoreNull("", "a"));
        assertTrue(StringUtil.isEqualNotIgnoreNull("a", "a"));
        assertTrue(StringUtil.isEqualNotIgnoreNull(null, null));

        assertFalse(StringUtil.isEqualNotIgnoreNullIgnoreCase(null, "a"));
        assertFalse(StringUtil.isEqualNotIgnoreNullIgnoreCase("", "a"));
        assertTrue(StringUtil.isEqualNotIgnoreNullIgnoreCase("A", "a"));
        assertTrue(StringUtil.isEqualNotIgnoreNullIgnoreCase(null, null));

        assertFalse(StringUtil.isTrimEqual(null, "a"));
        assertFalse(StringUtil.isTrimEqual("", "a"));
        assertTrue(StringUtil.isTrimEqual("a", "a"));
        assertTrue(StringUtil.isTrimEqual("a ", " a"));

        assertEquals(StringUtil.nvl(null, "12345"), "12345");
        assertEquals(StringUtil.nvl("aaa"), "aaa");
    }

    public void testEncode() {
        assertNotNull(StringUtil.urlencode("www.163.com", "UTF-8"));
        assertNull(StringUtil.urlencode(null, "UTF-8"));
        assertNull(StringUtil.urlencode("www.163.com", "unknow"));

        assertNotNull(StringUtil.urldecode("www.163.com", "UTF-8"));
        assertNull(StringUtil.urldecode(null, "UTF-8"));
        assertNotNull(StringUtil.urldecode("www.163.com", "unknow"));
    }

    public void testUnit() {

        assertEquals(1024, StringUtil.getByteNumber("1024"));
        assertEquals(1024, StringUtil.getByteNumber("1k"));
        assertEquals(1024 * 1024, StringUtil.getByteNumber("1m"));
        assertEquals(1024 * 1024 * 1024, StringUtil.getByteNumber("1g"));
        assertEquals(1024l * 1024 * 1024 * 1024, StringUtil.getByteNumber("1t"));

        assertEquals(-1, StringUtil.getByteNumber("1024kk"));
        assertEquals(-1, StringUtil.getByteNumber("1kk"));
        assertEquals(-1, StringUtil.getByteNumber("1mm"));
        assertEquals(-1, StringUtil.getByteNumber("1gg"));
        assertEquals(-1, StringUtil.getByteNumber("1tt"));

        assertEquals(1.0d, StringUtil.convertToK(1024));
        assertEquals(1.0d, StringUtil.convertToM(1024 * 1024));
        assertEquals(1.0d, StringUtil.convertToG(1024 * 1024 * 1024));

        assertEquals(-1.0d, StringUtil.convertToK(-1024));
        assertEquals(-1.0d, StringUtil.convertToM(-1024 * 1024));
        assertEquals(-1.0d, StringUtil.convertToG(-1024 * 1024 * 1024));
    }

    public void testFormat() {
        assertNotNull(StringUtil.formatNumber(1.1f, "#,###"));
    }

    public void testGetEnumeration() {
        assertEquals("{a,b}", StringUtil.getEnumeration("ENUM{a,b}"));
    }

    public void testCutAnnotationBeforeSQL() {
        assertNotNull(StringUtil.cutAnnotationBeforeSQL("----aaaa\n select * from db_class"));
        assertNotNull(StringUtil.cutAnnotationBeforeSQL("----aaaa"));

        assertNotNull(StringUtil.cutAnnotationBeforeSQL("/*aaaa*/ select * from db_class"));
        assertNotNull(StringUtil.cutAnnotationBeforeSQL("/*aaaa"));
    }

    public void testToUpper() {
        assertNull(StringUtil.toUpper(null));
        assertEquals("A", StringUtil.toUpper("a"));
    }

    public void testTrim() {
        assertEquals(null, StringUtil.trim(null));
        assertEquals("a", StringUtil.trim("a   "));
    }

    public void testStartsWithIgnoreCase() {
        assertFalse(StringUtil.startsWithIgnoreCase("aaa", "aa"));
        assertFalse(StringUtil.startsWithIgnoreCase("b", "aa"));
        assertTrue(StringUtil.startsWithIgnoreCase("a", "aa"));
    }

    public void testPaddingWith0() {
        assertEquals("0001", StringUtil.leftPad("1", '0', 4));
        assertEquals("0001", StringUtil.leftPad("01", '0', 4));
        assertEquals("0001", StringUtil.leftPad("0001", '0', 4));
        assertEquals("0100", StringUtil.leftPad("100", '0', 4));
        assertEquals("", StringUtil.leftPad("100", '0', 0));
        assertEquals("", StringUtil.leftPad("", '0', 4));
        assertNull(StringUtil.leftPad(null, '0', 4));
        assertNull(StringUtil.leftPad("100", '0', -1));
    }
}
