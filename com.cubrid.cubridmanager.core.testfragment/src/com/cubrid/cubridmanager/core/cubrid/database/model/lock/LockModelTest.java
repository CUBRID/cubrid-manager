package com.cubrid.cubridmanager.core.cubrid.database.model.lock;

import java.util.List;
import junit.framework.TestCase;

public class LockModelTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testModelLockInfo() {
        LockInfo bean = new LockInfo();
        bean.setEsc(3);
        assertEquals(bean.getEsc(), 3);
        bean.setDinterval(9);
        assertEquals(bean.getDinterval(), 9);
        bean.addTransaction(new DatabaseTransaction());
        assertEquals(bean.getTransaction() instanceof List, true);
    }

    public void testModelDatabaseTransaction() {
        DatabaseTransaction bean = new DatabaseTransaction();
        bean.setIndex(5);
        assertEquals(bean.getIndex(), 5);
        bean.setPname("pname");
        assertEquals(bean.getPname(), "pname");
        bean.setUid("uid");
        assertEquals(bean.getUid(), "uid");
        bean.setHost("host");
        assertEquals(bean.getHost(), "host");
        bean.setPid("pid");
        assertEquals(bean.getPid(), "pid");
        bean.setIsolevel("isolevel");
        assertEquals(bean.getIsolevel(), "isolevel");
        bean.setTimeout(7);
        assertEquals(bean.getTimeout(), 7);
    }

    public void testModelLockHolders() {
        LockHolders bean = new LockHolders();
        bean.setTran_index(10);
        assertEquals(bean.getTran_index(), 10);
        bean.setGranted_mode("granted_mode");
        assertEquals(bean.getGranted_mode(), "granted_mode");
        bean.setCount(5);
        assertEquals(bean.getCount(), 5);
        bean.setNsubgranules(12);
        assertEquals(bean.getNsubgranules(), 12);
    }

    public void testModelLockWaiters() {
        LockWaiters bean = new LockWaiters();
        bean.setTran_index(10);
        assertEquals(bean.getTran_index(), 10);
        bean.setB_mode("b_mode");
        assertEquals(bean.getB_mode(), "b_mode");
        bean.setStart_at("start_at");
        assertEquals(bean.getStart_at(), "start_at");
        bean.setWaitfornsec("waitfornsec");
        assertEquals(bean.getWaitfornsec(), "waitfornsec");
    }

    public void testModelDbLotInfo() {
        DbLotInfo bean = new DbLotInfo();
        bean.setNumlocked(9);
        assertEquals(bean.getNumlocked(), 9);
        bean.setMaxnumlock(10);
        assertEquals(bean.getMaxnumlock(), 10);
        bean.addEntry(new DbLotEntry());
        assertTrue(bean.getDbLotEntryList().size() == 1);
        // test list not init
        bean = new DbLotInfo();
        bean.addEntry(new DbLotEntry());
        assertTrue(bean.getDbLotEntryList().size() == 1);

        bean = new DbLotInfo();
        assertTrue(bean.getDbLotEntryList() != null);
        bean.addEntry(new DbLotEntry());
    }

    public void testModelBlockedHolders() {
        BlockedHolders bean = new BlockedHolders();
        bean.setTran_index(10);
        assertEquals(bean.getTran_index(), 10);
        bean.setGranted_mode("granted_mode");
        assertEquals(bean.getGranted_mode(), "granted_mode");
        bean.setCount(5);
        assertEquals(bean.getCount(), 5);
        bean.setNsubgranules(12);
        assertEquals(bean.getNsubgranules(), 12);
        bean.setBlocked_mode("blocked_mode");
        assertEquals(bean.getBlocked_mode(), "blocked_mode");
        bean.setStart_at("Start_at");
        assertEquals(bean.getStart_at(), "Start_at");
        bean.setWait_for_sec("wait_for_sec");
        assertEquals(bean.getWait_for_sec(), "wait_for_sec");
    }

    public void testModelDatabaseLockInfo() {
        DatabaseLockInfo bean = new DatabaseLockInfo();
        bean.addLockInfo(new LockInfo());
        assertEquals(bean.getLockInfo() instanceof LockInfo, true);
        assertEquals(bean.getTaskName(), "lockdb");
    }
}
