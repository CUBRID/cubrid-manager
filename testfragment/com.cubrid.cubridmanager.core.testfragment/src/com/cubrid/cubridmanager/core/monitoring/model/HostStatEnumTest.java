package com.cubrid.cubridmanager.core.monitoring.model;

import junit.framework.TestCase;

public class HostStatEnumTest extends TestCase {
    public void testGetName() {
        HostStatEnum e = HostStatEnum.USER;
        assertEquals("USER", e.getName());

        e = HostStatEnum.KERNEL;
        assertEquals("KERNEL", e.getName());

        e = HostStatEnum.IOWAIT;
        assertEquals("IOWAIT", e.getName());

        e = HostStatEnum.CPU_TOTAL;
        assertEquals("CPU_TOTAL", e.getName());

        e = HostStatEnum.MEMPHY_TOTAL;
        assertEquals("MEMPHY_TOTAL", e.getName());

        e = HostStatEnum.MEMPHY_USED;
        assertEquals("MEMPHY_USED", e.getName());

        e = HostStatEnum.MEMSWAP_TOTAL;
        assertEquals("MEMSWAP_TOTAL", e.getName());

        e = HostStatEnum.MEMSWAP_USED;
        assertEquals("MEMSWAP_USED", e.getName());

        e = HostStatEnum.MEMPHY_PERCENT;
        assertEquals("MEMPHY_PERCENT", e.getName());

        e = HostStatEnum.MEMSWAP_PERCENT;
        assertEquals("MEMSWAP_PERCENT", e.getName());
    }

    public void testValue() {
        assertEquals(HostStatEnum.valueOf("USER"), HostStatEnum.USER);
        assertEquals(HostStatEnum.valueOf("KERNEL"), HostStatEnum.KERNEL);
        assertEquals(HostStatEnum.valueOf("IOWAIT"), HostStatEnum.IOWAIT);
        assertEquals(HostStatEnum.valueOf("CPU_TOTAL"), HostStatEnum.CPU_TOTAL);
        assertEquals(HostStatEnum.valueOf("MEMPHY_TOTAL"), HostStatEnum.MEMPHY_TOTAL);
        assertEquals(HostStatEnum.valueOf("MEMPHY_USED"), HostStatEnum.MEMPHY_USED);
        assertEquals(HostStatEnum.valueOf("MEMSWAP_USED"), HostStatEnum.MEMSWAP_USED);
        assertEquals(HostStatEnum.valueOf("MEMPHY_PERCENT"), HostStatEnum.MEMPHY_PERCENT);
        assertEquals(HostStatEnum.valueOf("MEMSWAP_PERCENT"), HostStatEnum.MEMSWAP_PERCENT);
    }

    public void testValues() {
        HostStatEnum[] arr = HostStatEnum.values();
        assertEquals(arr.length, 10);
    }
}
