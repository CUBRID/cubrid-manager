package com.cubrid.cubridmanager.core.cubrid.database.model.lock;

import java.util.List;
import junit.framework.TestCase;

/**
 * Test LockInfo
 *
 * @author pangqiren
 * @version 1.0 - 2010-1-14 created by pangqiren
 */
public class LockInfoTest extends TestCase {
    private LockInfo bean;

    protected void setUp() throws Exception {
        super.setUp();
        bean = new LockInfo();
    }

    public void testGetEsc() {
        bean.setEsc(3);
        assertEquals(bean.getEsc(), 3);
    }

    public void testGetDinterval() {
        bean.setDinterval(9);
        assertEquals(bean.getDinterval(), 9);
    }

    @SuppressWarnings("unchecked")
    public void testGetTransaction() {
        bean.addTransaction(new DatabaseTransaction());
        assertEquals(bean.getTransaction() instanceof List, true);
    }

    public void testGetDbLotInfo() {
        bean.getDbLotInfo();
        bean.addLot(new DbLotInfo());
    }

    public void testListNoInit() {
        LockInfo lockInfo = new LockInfo();
        lockInfo.addTransaction(new DatabaseTransaction());
        assertTrue(lockInfo.getTransaction().size() == 1);
        lockInfo.addTransaction(new DatabaseTransaction());
    }
}
