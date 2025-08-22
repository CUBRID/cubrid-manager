package com.cubrid.cubridmanager.core.common;

import com.cubrid.common.core.util.CipherUtils;
import junit.framework.TestCase;

public class CipherUtilsTest extends TestCase {

    public void testCiperUtil() {
        String src = "123456abcedf";
        String encSrc = CipherUtils.encrypt(src);
        String decSrc = CipherUtils.decrypt(encSrc);
        assertEquals(src, decSrc);

        assertEquals(CipherUtils.encrypt(""), "");
        assertEquals(CipherUtils.decrypt(""), "");
    }
}
