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
package com.cubrid.cubridmanager.core.monitoring.model;

import com.cubrid.cubridmanager.core.common.model.IModel;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that extends IModel and is responsible for the task of "getdbprocstat"
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-4 created by lizhiqiang
 */
public class DbProcStat implements IModel {
    private String dbname;
    private String status;
    private String note;
    private List<DbSysStat> dbProcLst;

    // Constructor
    public DbProcStat() {
        dbProcLst = new ArrayList<DbSysStat>();
    }

    /* (non-Javadoc)
     * @see com.cubrid.cubridmanager.core.common.model.IModel#getTaskName()
     */
    public String getTaskName() {
        return "getdbprocstat";
    }

    /**
     * clone the data from another instance of the DbProcStat
     *
     * @param clone a instance of DbServerProc
     */
    public void copyFrom(DbProcStat clone) {
        dbname = clone.dbname;
        dbProcLst.clear();
        for (DbSysStat dssClone : clone.getDbProcLst()) {
            DbSysStat dss = new DbSysStat();
            dss.copyFrom(dssClone);
            dbProcLst.add(dss);
        }
    }

    /**
     * add a dbstat node
     *
     * @param dsp DbStatProc
     */
    public void addDbstat(DbSysStat dsp) {
        if (dbProcLst == null) {
            dbProcLst = new ArrayList<DbSysStat>();
        }
        if (!dbProcLst.contains(dsp)) {
            dbProcLst.add(dsp);
        }
    }

    /** Remove all the elements in the dbProcLst. */
    public void clearDbstat() {
        if (dbProcLst == null) {
            return;
        } else {
            dbProcLst.clear();
        }
    }

    /**
     * Get the dbname
     *
     * @return the dbname
     */
    public String getDbname() {
        return dbname;
    }

    /** @param dbname the dbname to set */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    /**
     * Get the status
     *
     * @return the status
     */
    public boolean getStatus() {
        if ("success".equals(status)) {
            return true;
        }
        return false;
    }

    /** @param status the status to set */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the note
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /** @param note the note to set */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Get all the database process info
     *
     * @return the dbProcLst
     */
    public List<DbSysStat> getDbProcLst() {
        return dbProcLst;
    }
}
