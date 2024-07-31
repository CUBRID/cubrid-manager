/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.core.cubrid.database.model.lock;

import java.util.ArrayList;
import java.util.List;

/**
 * A java bean that includes the info of Database lock entry
 *
 * @author sq
 * @version 1.0 - 2009-12-28 created by sq
 */
public class DbLotEntry {
    private String open;
    private String oid;
    private String ob_type;
    private int num_holders;
    private int num_b_holders;
    private int num_waiters;

    private List<LockHolders> lockHoldersList;

    private List<LockWaiters> lockWaitersList;

    private List<BlockedHolders> blockHoldersList;

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOb_type() {
        return ob_type;
    }

    public void setOb_type(String obType) {
        this.ob_type = obType;
    }

    public int getNum_holders() {
        return num_holders;
    }

    public void setNum_holders(int numHolders) {
        this.num_holders = numHolders;
    }

    public int getNum_b_holders() {
        return num_b_holders;
    }

    public void setNum_b_holders(int numBholders) {
        this.num_b_holders = numBholders;
    }

    public int getNum_waiters() {
        return num_waiters;
    }

    public void setNum_waiters(int numWaiters) {
        this.num_waiters = numWaiters;
    }

    public List<LockHolders> getLockHoldersList() {
        return lockHoldersList;
    }

    /**
     * add an instance of LockHolders into lockHoldersList
     *
     * @param bean LockHolders an instance of LockHolders
     */
    public void addLock_holders(LockHolders bean) {
        if (lockHoldersList == null) {
            lockHoldersList = new ArrayList<LockHolders>();
        }
        this.lockHoldersList.add(bean);
    }

    public List<LockWaiters> getLockWaitersList() {
        return lockWaitersList;
    }

    /**
     * add an instance of LockWaiters into lockWaitersList
     *
     * @param bean LockWaiters and instance of LockWaiters
     */
    public void addWaiters(LockWaiters bean) {
        if (lockWaitersList == null) {
            lockWaitersList = new ArrayList<LockWaiters>();
        }
        this.lockWaitersList.add(bean);
    }

    public List<BlockedHolders> getBlockHoldersList() {
        return blockHoldersList;
    }

    /**
     * add an instance of BlockedHolders into blockHoldersList
     *
     * @param bean an instance of BlockedHolders
     */
    public void addB_holders(BlockedHolders bean) {
        if (blockHoldersList == null) {
            blockHoldersList = new ArrayList<BlockedHolders>();
        }
        this.blockHoldersList.add(bean);
    }
}
