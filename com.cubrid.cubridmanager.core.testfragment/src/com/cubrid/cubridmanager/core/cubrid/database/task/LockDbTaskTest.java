package com.cubrid.cubridmanager.core.cubrid.database.task;

import java.util.List;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.SystemParameter;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DatabaseLockInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DatabaseTransaction;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockHolders;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.lock.LockWaiters;

public class LockDbTaskTest extends
		SetupEnvTestCase {

	public void testLockDbNoLock() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.lockdb.001.req.txt>");

		CommonQueryTask<DatabaseLockInfo> task = new CommonQueryTask<DatabaseLockInfo>(
				ServerManager.getInstance().getServer(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg(),
				new DatabaseLockInfo());
		task.setDbName("unlockeddb");
		task.execute();

		assertTrue(task.isSuccess());
		assertNull(task.getErrorMsg());

		DatabaseLockInfo dli = task.getResultModel();
		assertNotNull(dli);
		assertEquals("lockdb", dli.getTaskName());

		LockInfo li = dli.getLockInfo();
		assertNotNull(li);
		assertEquals(1, li.getDinterval());
		assertEquals(100000, li.getEsc());

		List<DatabaseTransaction> dtList = li.getTransaction();
		assertNotNull(dtList);
		assertEquals(2, dtList.size());

		DatabaseTransaction dt = dtList.get(0);
		assertEquals(0, dt.getIndex());
		assertEquals("(unknown)", dt.getPname());
		assertEquals("(unknown)", dt.getUid());
		assertEquals("(unknown)", dt.getHost());
		assertEquals("-1", dt.getPid());
		assertEquals("REPEATABLE CLASSES AND READ UNCOMMITTED INSTANCES",
				dt.getIsolevel());
		assertEquals(-1, dt.getTimeout());

		dt = dtList.get(1);
		assertEquals(1, dt.getIndex());
		assertEquals("lockdb", dt.getPname());
		assertEquals("dba", dt.getUid());
		assertEquals("localhost.localdomain", dt.getHost());
		assertEquals("5334", dt.getPid());
		assertEquals("READ COMMITTED CLASSES AND READ UNCOMMITTED INSTANCES",
				dt.getIsolevel());
		assertEquals(-2, dt.getTimeout());

		DbLotInfo dbLotInfo = li.getDbLotInfo();
		assertNotNull(dbLotInfo);
		assertEquals(0, dbLotInfo.getNumlocked());
		assertEquals(10000, dbLotInfo.getMaxnumlock());
		assertNotNull(dbLotInfo.getDbLotEntryList());
		assertEquals(0, dbLotInfo.getDbLotEntryList().size());

	}

	public void testLockDbLocked() {

		if (StringUtil.isEqual(
				SystemParameter.getParameterValue("useMockTest"), "n"))
			return;

		System.out.println("<database.lockdb.002.req.txt>");

		CommonQueryTask<DatabaseLockInfo> task = new CommonQueryTask<DatabaseLockInfo>(
				ServerManager.getInstance().getServer(host, monport, userName),
				CommonSendMsg.getCommonDatabaseSendMsg(),
				new DatabaseLockInfo());
		task.setDbName("lockeddb");
		task.execute();

		assertTrue(task.isSuccess());
		assertNull(task.getErrorMsg());

		DatabaseLockInfo dli = task.getResultModel();
		assertNotNull(dli);
		assertEquals("lockdb", dli.getTaskName());

		LockInfo li = dli.getLockInfo();
		assertNotNull(li);
		assertEquals(1, li.getDinterval());
		assertEquals(100000, li.getEsc());

		List<DatabaseTransaction> dtList = li.getTransaction();
		assertNotNull(dtList);
		assertEquals(4, dtList.size());

		DatabaseTransaction dt = dtList.get(0);
		assertEquals(0, dt.getIndex());
		assertEquals("(unknown)", dt.getPname());
		assertEquals("(unknown)", dt.getUid());
		assertEquals("(unknown)", dt.getHost());
		assertEquals("-1", dt.getPid());
		assertEquals("REPEATABLE CLASSES AND READ UNCOMMITTED INSTANCES",
				dt.getIsolevel());
		assertEquals(-1, dt.getTimeout());

		dt = dtList.get(1);
		assertEquals(1, dt.getIndex());
		assertEquals("csql", dt.getPname());
		assertEquals("dba", dt.getUid());
		assertEquals("localhost.localdomain", dt.getHost());
		assertEquals("10731", dt.getPid());
		assertEquals("REPEATABLE CLASSES AND READ UNCOMMITTED INSTANCES",
				dt.getIsolevel());
		assertEquals(-1, dt.getTimeout());

		dt = dtList.get(2);
		assertEquals(2, dt.getIndex());
		assertEquals("csql", dt.getPname());
		assertEquals("dba", dt.getUid());
		assertEquals("localhost.localdomain", dt.getHost());
		assertEquals("10786", dt.getPid());
		assertEquals("REPEATABLE CLASSES AND READ UNCOMMITTED INSTANCES",
				dt.getIsolevel());
		assertEquals(-1, dt.getTimeout());

		dt = dtList.get(3);
		assertEquals(3, dt.getIndex());
		assertEquals("lockdb", dt.getPname());
		assertEquals("dba", dt.getUid());
		assertEquals("localhost.localdomain", dt.getHost());
		assertEquals("10987", dt.getPid());
		assertEquals("READ COMMITTED CLASSES AND READ UNCOMMITTED INSTANCES",
				dt.getIsolevel());
		assertEquals(-2, dt.getTimeout());

		DbLotInfo dbLotInfo = li.getDbLotInfo();
		assertNotNull(dbLotInfo);
		assertEquals(2, dbLotInfo.getNumlocked());
		assertEquals(10000, dbLotInfo.getMaxnumlock());
		assertNotNull(dbLotInfo.getDbLotEntryList());
		assertEquals(2, dbLotInfo.getDbLotEntryList().size());

		List<DbLotEntry> dbLotEntryList = dbLotInfo.getDbLotEntryList();
		assertNotNull(dbLotEntryList);
		assertEquals(2, dbLotEntryList.size());

		DbLotEntry dbLotEntry = dbLotEntryList.get(0);
		assertNotNull(dbLotEntry);
		assertEquals("0|354|2", dbLotEntry.getOid());
		assertEquals("Class = dual", dbLotEntry.getOb_type());
		assertEquals(1, dbLotEntry.getNum_holders());
		assertEquals(0, dbLotEntry.getNum_b_holders());
		assertEquals(0, dbLotEntry.getNum_waiters());
		List<LockHolders> lockHolderList = dbLotEntry.getLockHoldersList();
		assertNotNull(lockHolderList);
		assertEquals(1, lockHolderList.size());
		LockHolders lockHolders = lockHolderList.get(0);
		assertNotNull(lockHolders);
		assertEquals(2, lockHolders.getTran_index());
		assertEquals(2, lockHolders.getCount());
		assertEquals("IS_LOCK", lockHolders.getGranted_mode());
		assertEquals(0, lockHolders.getNsubgranules());
		assertNull(dbLotEntry.getBlockHoldersList());
		assertNull(dbLotEntry.getLockWaitersList());

		dbLotEntry = dbLotEntryList.get(1);
		assertNotNull(dbLotEntry);
		assertEquals("0|60|1", dbLotEntry.getOid());
		assertEquals("Root class", dbLotEntry.getOb_type());
		assertEquals(1, dbLotEntry.getNum_holders());
		assertEquals(0, dbLotEntry.getNum_b_holders());
		assertEquals(1, dbLotEntry.getNum_waiters());
		lockHolderList = dbLotEntry.getLockHoldersList();
		assertNotNull(lockHolderList);
		assertEquals(1, lockHolderList.size());
		lockHolders = lockHolderList.get(0);
		assertNotNull(lockHolders);
		assertEquals(2, lockHolders.getTran_index());
		assertEquals(1, lockHolders.getCount());
		assertEquals("IS_LOCK", lockHolders.getGranted_mode());
		assertEquals(1, lockHolders.getNsubgranules());
		assertNull(dbLotEntry.getBlockHoldersList());
		assertNotNull(dbLotEntry.getLockWaitersList());
		List<LockWaiters> lockWaitersList = dbLotEntry.getLockWaitersList();
		assertNotNull(lockWaitersList);
		assertEquals(1, lockWaitersList.size());
		LockWaiters lockWaiters = lockWaitersList.get(0);
		assertEquals("IX_LOCK", lockWaiters.getB_mode());
		assertEquals("Mon Jun 29 21:29:12 2009", lockWaiters.getStart_at());
		assertEquals("-1", lockWaiters.getWaitfornsec());
		assertEquals(1, lockWaiters.getTran_index());

		//TODO: need a test for the b_holders attribute

	}

}
