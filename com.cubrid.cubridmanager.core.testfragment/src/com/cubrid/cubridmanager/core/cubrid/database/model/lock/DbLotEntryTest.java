/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.core.cubrid.database.model.lock;

import junit.framework.TestCase;

/**
 * Test DbLotEntry
 *
 * @author Administrator
 * @version 1.0 - 2010-1-11 created by Administrator
 */
public class DbLotEntryTest extends TestCase {
    private DbLotEntry dbLotEntry;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        dbLotEntry = new DbLotEntry();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getOpen()} .
     */
    public void testGetOpen() {
        dbLotEntry.setOpen("open");
        assertEquals(dbLotEntry.getOpen(), "open");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getOid()} .
     */
    public void testGetOid() {
        dbLotEntry.setOid("oid");
        assertEquals(dbLotEntry.getOid(), "oid");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getOb_type()} .
     */
    public void testGetOb_type() {
        dbLotEntry.setOb_type("ob_type");
        assertEquals(dbLotEntry.getOb_type(), "ob_type");
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getNum_holders()} .
     */
    public void testGetNum_holders() {
        dbLotEntry.setNum_holders(11);
        assertEquals(dbLotEntry.getNum_holders(), 11);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getNum_b_holders()} .
     */
    public void testGetNum_b_holders() {
        dbLotEntry.setNum_b_holders(13);
        assertEquals(dbLotEntry.getNum_b_holders(), 13);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getNum_waiters()} .
     */
    public void testGetNum_waiters() {
        dbLotEntry.setNum_waiters(11);
        assertEquals(dbLotEntry.getNum_waiters(), 11);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getLockHoldersList()} .
     */
    public void testGetLockHoldersList() {
        dbLotEntry.getLockHoldersList();
        dbLotEntry.addLock_holders(new LockHolders());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getLockWaitersList()} .
     */
    public void testGetLockWaitersList() {

        dbLotEntry.getLockWaitersList();
        dbLotEntry.addWaiters(new LockWaiters());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.database.model.lock.DbLotEntry#getBlockHoldersList()} .
     */
    public void testGetBlockHoldersList() {
        dbLotEntry.getBlockHoldersList();
        dbLotEntry.addB_holders(new BlockedHolders());
    }

    /** Test all list not initialized */
    public void testListNoInit() {
        DbLotEntry dbLotEntry = new DbLotEntry();
        dbLotEntry.addLock_holders(new LockHolders());
        assertTrue(dbLotEntry.getLockHoldersList().size() == 1);
        dbLotEntry.addWaiters(new LockWaiters());
        assertTrue(dbLotEntry.getLockHoldersList().size() == 1);
        dbLotEntry.addB_holders(new BlockedHolders());
        assertTrue(dbLotEntry.getBlockHoldersList().size() == 1);
        dbLotEntry.addLock_holders(new LockHolders());
        dbLotEntry.addWaiters(new LockWaiters());
        dbLotEntry.addB_holders(new BlockedHolders());
    }
}
