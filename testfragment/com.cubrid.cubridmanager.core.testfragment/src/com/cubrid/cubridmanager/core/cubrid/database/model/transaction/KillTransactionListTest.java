package com.cubrid.cubridmanager.core.cubrid.database.model.transaction;

import junit.framework.TestCase;

public class KillTransactionListTest extends TestCase {
    private KillTransactionList killTransactionList;

    protected void setUp() throws Exception {
        super.setUp();
        killTransactionList = new KillTransactionList();
    }

    public void testGetDbname() {
        killTransactionList.setDbname("dbname");
        assertEquals("dbname", killTransactionList.getDbname());
    }

    public void testGetTaskName() {
        assertEquals("killtransaction", killTransactionList.getTaskName());
    }

    public void testGetTransationInfo() {
        TransactionInfo transactionInfo = new TransactionInfo();
        killTransactionList.addTransactionInfo(transactionInfo);
        assertNotNull(killTransactionList.getTransationInfo());
    }
}
