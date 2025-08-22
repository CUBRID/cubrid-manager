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
 * A java bean that includes the info of LockInfo
 *
 * @author sq
 * @version 1.0 - 2009-12-28 created by sq
 */
public class LockInfo {
    private int esc;
    private int dinterval;
    private List<DatabaseTransaction> transaction;

    private DbLotInfo dbLotInfo;

    public int getEsc() {
        return esc;
    }

    public void setEsc(int esc) {
        this.esc = esc;
    }

    public int getDinterval() {
        return dinterval;
    }

    public void setDinterval(int dinterval) {
        this.dinterval = dinterval;
    }

    public List<DatabaseTransaction> getTransaction() {
        return transaction;
    }

    /**
     * Add an instance of DatabaseTransaction into transaction
     *
     * @param bean DatabaseTransaction an instance of DatabaseTransaction
     */
    public void addTransaction(DatabaseTransaction bean) {

        if (transaction == null) {
            transaction = new ArrayList<DatabaseTransaction>();
        }
        this.transaction.add(bean);
    }

    public DbLotInfo getDbLotInfo() {
        return dbLotInfo;
    }
    /**
     * set an instance of DbLotInfo
     *
     * @param dbLotInfo DbLotInfo an instance of DBLotInfo
     */
    public void addLot(DbLotInfo dbLotInfo) {
        this.dbLotInfo = dbLotInfo;
    }
}
