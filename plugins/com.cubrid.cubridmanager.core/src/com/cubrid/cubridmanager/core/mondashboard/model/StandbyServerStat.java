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
package com.cubrid.cubridmanager.core.mondashboard.model;

import com.cubrid.cubridmanager.core.common.model.IModel;

/**
 * A class that extends IModel and is responsible for the task of "getstandbyserverstat"
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-24 created by lizhiqiang
 */
public class StandbyServerStat implements IModel {
    private String dbname;
    private String status;
    private String note;
    private String delay_time;
    private String insert_counter;
    private String update_counter;
    private String delete_counter;
    private String commit_counter;
    private String fail_counter;

    /* (non-Javadoc)
     * @see com.cubrid.cubridmanager.core.common.model.IModel#getTaskName()
     */
    public String getTaskName() {
        return "getstandbyserverstat";
    }

    /**
     * Set the dynamic field from another instance of StandbyServerStat
     *
     * @param clone an instance of StandbyServerStat
     */
    public void copyFrom(StandbyServerStat clone) {
        this.dbname = clone.dbname;
        this.status = clone.status;
        this.delay_time = clone.delay_time;
        this.insert_counter = clone.insert_counter;
        this.update_counter = clone.update_counter;
        this.delete_counter = clone.delete_counter;
        this.commit_counter = clone.commit_counter;
        this.fail_counter = clone.fail_counter;
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
        if (status != null && "success".equals(status.trim())) {
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
     * Get the delay_time
     *
     * @return the delay_time
     */
    public String getDelay_time() {
        return delay_time;
    }

    /** @param delayTime the delay_time to set */
    public void setDelay_time(String delayTime) {
        delay_time = delayTime;
    }

    /**
     * Get the insert_counter
     *
     * @return the insert_counter
     */
    public String getInsert_counter() {
        return insert_counter;
    }

    /** @param insertCounter the insert_counter to set */
    public void setInsert_counter(String insertCounter) {
        insert_counter = insertCounter;
    }

    /**
     * Get the update_counter
     *
     * @return the update_counter
     */
    public String getUpdate_counter() {
        return update_counter;
    }

    /** @param updateCounter the update_counter to set */
    public void setUpdate_counter(String updateCounter) {
        update_counter = updateCounter;
    }

    /**
     * Get the delete_counter
     *
     * @return the delete_counter
     */
    public String getDelete_counter() {
        return delete_counter;
    }

    /** @param deleteCounter the delete_counter to set */
    public void setDelete_counter(String deleteCounter) {
        delete_counter = deleteCounter;
    }

    /**
     * Get the commit_counter
     *
     * @return the commit_counter
     */
    public String getCommit_counter() {
        return commit_counter;
    }

    /** @param commitCounter the commit_counter to set */
    public void setCommit_counter(String commitCounter) {
        commit_counter = commitCounter;
    }

    /**
     * Get the fail_counter
     *
     * @return the fail_counter
     */
    public String getFail_counter() {
        return fail_counter;
    }

    /** @param failCounter the fail_counter to set */
    public void setFail_counter(String failCounter) {
        fail_counter = failCounter;
    }
}
