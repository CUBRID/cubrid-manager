package com.cubrid.cubridmanager.core.cubrid.database.model.transaction;

import junit.framework.TestCase;

public class DbTransactionListTest extends TestCase {
    private DbTransactionList dbTransactionList;

    protected void setUp() throws Exception {
        super.setUp();
        dbTransactionList = new DbTransactionList();
    }

    public void testGetDbname() {
        dbTransactionList.setDbname("dbname");
        assertEquals("dbname", dbTransactionList.getDbname());
    }

    public void testGetTaskName() {
        assertEquals("gettransactioninfo", dbTransactionList.getTaskName());
    }

    public void testGetTransationInfo() {
        assertNull(dbTransactionList.getTransationInfo());
    }

    public void testAddTransactionInfo() {
        TransactionInfo transationInfo = new TransactionInfo();
        dbTransactionList.addTransactionInfo(transationInfo);
        assertNotNull(dbTransactionList.getTransationInfo());
    }

    public void testSetTransationInfo() {
        TransactionInfo transationInfo = new TransactionInfo();
        dbTransactionList.setTransationInfo(transationInfo);
        assertNotNull(dbTransactionList.getTransationInfo());
    }
}
