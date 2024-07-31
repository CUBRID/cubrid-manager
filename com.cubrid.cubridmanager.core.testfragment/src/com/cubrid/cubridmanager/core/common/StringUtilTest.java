package com.cubrid.cubridmanager.core.common;

import com.cubrid.common.core.util.StringUtil;
import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

    public void testStringUtil() {
        String str = StringUtil.implode(",", new String[] {"4", "5"});
        assertEquals(str, "4,5");
        StringUtil.md5("what is my md5");
        assertTrue(StringUtil.isEmpty(""));
        assertTrue(StringUtil.isNotEmpty("nihao"));
        assertTrue(!StringUtil.isEqual(null, null));
        StringUtil.isEqual("a", "b");
        assertTrue(!StringUtil.isTrimEqual(null, null));
        StringUtil.isTrimEqual("a", "b");
        assertEquals(StringUtil.nvl(null, "12345"), "12345");
        StringUtil.md5(null);
        StringUtil.replace("string", null, "newString");
        StringUtil.replace("string", "oldString", null);
        StringUtil.replace("", "oldString", "newString");
        StringUtil.replace("string", "oldString", "newString");
        StringUtil.replace("stringoldString", "oldString", "newString");
        StringUtil.replace(new StringBuilder("string"), null, "newString");
        StringUtil.replace(new StringBuilder("string"), "oldString", null);
        StringUtil.replace(new StringBuilder("string"), "", "newString");
        StringUtil.replace(new StringBuilder("string"), "oldString", "");
        StringUtil.replace(new StringBuilder("string"), "oldString", "newString");
        StringUtil.replace(new StringBuilder("stringoldString"), "oldString", "newString");
        StringUtil.replace(new StringBuilder(""), "oldString", "newString");
        StringUtil.countSpace("string");
        StringUtil.countSpace(" string");
        StringUtil.longValue(null);
        StringUtil.longValue("123456");
        StringUtil.longValue("aaa");
        StringUtil.floatValue(null);
        StringUtil.floatValue("aaa");
        StringUtil.booleanValue(null);
        StringUtil.booleanValue("12");
        StringUtil.booleanValue("aaa");
        StringUtil.booleanValueWithYN(null);
        StringUtil.booleanValueWithYN("123");
        StringUtil.booleanValueWithYN("y");
        StringUtil.intValue(null);
        StringUtil.intValue("12");
        StringUtil.intValue("-12");
        StringUtil.intValue("aaa");
        StringUtil.getStackTrace(new Exception(), ";");
        StringUtil.isEmpty((String) null);
        StringUtil.isEmpty("");
        StringUtil.isEmpty("aaa");
        StringUtil.isNotEmpty("aaa");
        StringUtil.isNotEmpty("");
        StringUtil.isNotEmpty(null);
        StringUtil.nvl("aaa");
        StringUtil.yesno(true);
        StringUtil.yesno(false);
        StringUtil.urldecode("url", "charset");
        StringUtil.urldecode(null, "charset");
    }

    public void testBit() {
        byte[] dataTmp = new byte[256];
        byte count = -128;
        for (int j = 0; j < dataTmp.length; j++) {
            dataTmp[j] = count;
            count++;
        }

        int temp = 0;
        short aByteSize = 256;
        long start1 = System.currentTimeMillis();
        int loopNum = 100000;
        for (int j = 0; j < loopNum; j++) {
            //			StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < dataTmp.length; i++) {
                if (dataTmp[i] < 0) temp = (short) dataTmp[i] + aByteSize;
                else temp = (short) dataTmp[i];
                Integer.toHexString(temp);
            }
        }
        long end1 = System.currentTimeMillis();
        long cost1 = end1 - start1;
        System.out.println("cost time of test1:" + cost1);

        long start2 = System.currentTimeMillis();
        for (int j = 0; j < loopNum; j++) {
            for (int i = 0; i < dataTmp.length; i++) {
                temp = dataTmp[i] & 0x00ff;
                Integer.toHexString(temp);
            }
        }
        long end2 = System.currentTimeMillis();
        long cost2 = end2 - start2;
        System.out.println("cost time of test2:" + cost2);
    }

    public void testGetByteNumber() {
        assertEquals(1, StringUtil.getByteNumber("1"));
        assertEquals(1024, StringUtil.getByteNumber("1K"));
        assertEquals(1024 * 1024, StringUtil.getByteNumber("1M"));
        assertEquals(1024 * 1024 * 1024, StringUtil.getByteNumber("1G"));
        assertEquals(1024l * 1024 * 1024 * 1024, StringUtil.getByteNumber("1T"));

        assertEquals(-1, StringUtil.getByteNumber("K"));
        assertEquals(-1, StringUtil.getByteNumber("M"));
        assertEquals(-1, StringUtil.getByteNumber("G"));
        assertEquals(-1, StringUtil.getByteNumber("T"));
    }

    public void testConvertToK() {
        assertEquals(-1.0, StringUtil.convertToK(-1));
        assertEquals(1.0, StringUtil.convertToK(1024));
    }

    public void testConvertToM() {
        assertEquals(-1.0, StringUtil.convertToM(-1));
        assertEquals(1.0, StringUtil.convertToM(1024 * 1024));
    }

    public void testConvertToG() {
        assertEquals(-1.0, StringUtil.convertToG(-1));
        assertEquals(1.0, StringUtil.convertToG(1024 * 1024 * 1024));
    }

    public void testUrlencode() {
        System.out.println(
                StringUtil.urlencode(
                        "http://192.168.1.187:8080/hudson/job/CUBRID-Manager-Dev/", "UTF-8"));
    }

    public void testyn() {
        assertEquals("y", StringUtil.yn(true));
        assertEquals("n", StringUtil.yn(false));
    }

    public void testIsEqualNotIgnoreNull() {
        assertEquals(false, StringUtil.isEqualNotIgnoreNull(null, "a"));
        assertEquals(false, StringUtil.isEqualNotIgnoreNull("a", null));

        assertEquals(true, StringUtil.isEqualNotIgnoreNull(null, null));
        assertEquals(true, StringUtil.isEqualNotIgnoreNull("a", "a"));
    }

    public void testIntValue() {
        assertEquals(0, StringUtil.intValue("a", 0));
        assertEquals(0, StringUtil.intValue(null, 0));

        assertEquals(1, StringUtil.intValue("1", 0));
    }

    public void testRepeat() {
        assertEquals("aaa", StringUtil.repeat("a", 3));
    }

    public void testFloatValue() {
        assertEquals(1.2f, StringUtil.floatValue("1.2"));
    }
}
