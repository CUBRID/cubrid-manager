package com.cubrid.cubridmanager.core.cubrid.database.model.transaction;

import com.cubrid.cubridmanager.core.cubrid.database.model.lock.DatabaseTransaction;
import java.util.List;
import junit.framework.TestCase;

public class TransactionModelTest extends TestCase {
    public void testModelDbTransactionList() {
        DbTransactionList bean = new DbTransactionList();
        bean.setDbname("dbname");
        assertEquals(bean.getDbname(), "dbname");
    }

    public void testModelKillTransactionList() {
        KillTransactionList bean = new KillTransactionList();
        bean.setDbname("dbname");
        assertEquals(bean.getDbname(), "dbname");
    }

    public void testModelTransaction() {
        Transaction bean = new Transaction();
        bean.setTranindex("tranindex");
        assertEquals(bean.getTranindex(), "tranindex");
        bean.setUser("user");
        assertEquals(bean.getUser(), "user");
        bean.setHost("host");
        assertEquals(bean.getHost(), "host");
        bean.setPid("pid");
        assertEquals(bean.getPid(), "pid");
        bean.setProgram("program");
        assertEquals(bean.getProgram(), "program");
    }

    public void testModelTransactionInfo() {
        TransactionInfo bean = new TransactionInfo();
        bean.setTransactionList(null);
        bean.addTransaction(null);
        bean.addTransaction(new Transaction());
        assertEquals(bean.getTransactionList() instanceof List<?>, true);
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
}
