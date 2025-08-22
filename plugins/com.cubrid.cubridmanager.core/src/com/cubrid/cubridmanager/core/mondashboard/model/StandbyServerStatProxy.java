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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatData;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.core.monitoring.model.StandbyServerStatEnum;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * This type is responsible for transforming the data in the instance of StandbyServerStat to the
 * data in the instance of this type
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-24 created by lizhiqiang
 */
public class StandbyServerStatProxy {
    private static final Logger LOGGER = LogUtil.getLogger(HostStatData.class);

    private String dbname;
    private String status;
    private String note;
    private String delayTime;
    private String insertCounter;
    private String updateCounter;
    private String deleteCounter;
    private String commitCounter;
    private String failCounter;
    private final Map<IDiagPara, String> statusResultMap;

    public StandbyServerStatProxy() {
        delayTime = "0";
        insertCounter = "0";
        updateCounter = "0";
        deleteCounter = "0";
        commitCounter = "0";
        failCounter = "0";
        statusResultMap = new HashMap<IDiagPara, String>();
    }

    /** Put the new value of fields to map */
    private void putVauleInMap() {
        statusResultMap.put(StandbyServerStatEnum.DELAY_TIME, delayTime);
        statusResultMap.put(StandbyServerStatEnum.INSERT_COUNTER, insertCounter);
        statusResultMap.put(StandbyServerStatEnum.UPDATE_COUNTER, updateCounter);
        statusResultMap.put(StandbyServerStatEnum.DELETE_COUNTER, deleteCounter);
        statusResultMap.put(StandbyServerStatEnum.COMMIT_COUNTER, commitCounter);
        statusResultMap.put(StandbyServerStatEnum.FAIL_COUNTER, failCounter);
    }

    /**
     * * Compute the fields values
     *
     * @param statA an instance of StandbyServerStat
     * @param statB an instance of StandbyServerStat
     */
    public void compute(StandbyServerStat statA, StandbyServerStat statB) {

        long insertCounterStatA = Long.parseLong(statA.getInsert_counter());
        long insertCounterStatB = Long.parseLong(statB.getInsert_counter());
        insertCounter = Long.toString(insertCounterStatA - insertCounterStatB);

        long updateCounterStatA = Long.parseLong(statA.getUpdate_counter());
        long updateCounterStatB = Long.parseLong(statB.getUpdate_counter());
        updateCounter = Long.toString(updateCounterStatA - updateCounterStatB);

        long deleteCounterStatA = Long.parseLong(statA.getDelete_counter());
        long deleteCounterStatB = Long.parseLong(statB.getDelete_counter());
        deleteCounter = Long.toString(deleteCounterStatA - deleteCounterStatB);

        long commitCounterStatA = Long.parseLong(statA.getCommit_counter());
        long commitCounterStatB = Long.parseLong(statB.getCommit_counter());
        commitCounter = Long.toString(commitCounterStatA - commitCounterStatB);

        long failureCounterStatA = Long.parseLong(statA.getFail_counter());
        long failureCounterStatB = Long.parseLong(statB.getFail_counter());
        failCounter = Long.toString(failureCounterStatA - failureCounterStatB);

        if (Long.parseLong(commitCounter) == 0) {
            delayTime = "0";
        } else {
            delayTime = statA.getDelay_time();
            if (delayTime.matches("^\\d+$")) {
                if (delayTime.length() > 9) {
                    delayTime = "0";
                }
            } else {
                delayTime = "0";
            }
        }

        putVauleInMap();
    }

    /**
     * Compute the fields values
     *
     * @param statA an instance of StandbyServerStat
     * @param statB an instance of StandbyServerStat
     * @param statC an instance of StandbyServerStat
     */
    public void compute(StandbyServerStat statA, StandbyServerStat statB, StandbyServerStat statC) {
        delayTime = statA.getDelay_time();
        if (delayTime != null && delayTime.matches("^\\d+$")) {
            if (delayTime.length() > 9) {
                delayTime = "0";
            }
        } else {
            delayTime = "0";
        }

        insertCounter = Long.toString(getInsertCounter(statA, statB, statC));
        updateCounter = Long.toString(getUpdateCounter(statA, statB, statC));
        deleteCounter = Long.toString(getDeleteCounter(statA, statB, statC));
        long commitCountLong = getCommitCounter(statA, statB, statC);
        commitCounter = Long.toString(commitCountLong);
        if (commitCountLong == 0) {
            delayTime = "0";
        }
        failCounter = Long.toString(getFailCounter(statA, statB, statC));
        putVauleInMap();
    }

    /**
     * Get the field of insertCounter
     *
     * @param statA an instance of StandbyServerStat
     * @param statB an instance of StandbyServerStat
     * @param statC an instance of StandbyServerStat
     * @return long
     */
    private long getInsertCounter(
            StandbyServerStat statA, StandbyServerStat statB, StandbyServerStat statC) {
        return getDeltaLong(
                statA,
                "Insert_counter",
                statA.getInsert_counter(),
                statB.getInsert_counter(),
                statC.getInsert_counter());
    }

    /**
     * Get the field of updateCounter
     *
     * @param statA an instance of StandbyServerStat
     * @param statB an instance of StandbyServerStat
     * @param statC an instance of StandbyServerStat
     * @return long
     */
    private long getUpdateCounter(
            StandbyServerStat statA, StandbyServerStat statB, StandbyServerStat statC) {
        return getDeltaLong(
                statA,
                "Update_counter",
                statA.getUpdate_counter(),
                statB.getUpdate_counter(),
                statC.getUpdate_counter());
    }

    /**
     * Get the field of deleteCounter
     *
     * @param statA an instance of StandbyServerStat
     * @param statB an instance of StandbyServerStat
     * @param statC an instance of StandbyServerStat
     * @return long
     */
    private long getDeleteCounter(
            StandbyServerStat statA, StandbyServerStat statB, StandbyServerStat statC) {
        return getDeltaLong(
                statA,
                "Delete_counter",
                statA.getDelete_counter(),
                statB.getDelete_counter(),
                statC.getDelete_counter());
    }

    /**
     * Get the field of commitCounter
     *
     * @param statA an instance of StandbyServerStat
     * @param statB an instance of StandbyServerStat
     * @param statC an instance of StandbyServerStat
     * @return long
     */
    private long getCommitCounter(
            StandbyServerStat statA, StandbyServerStat statB, StandbyServerStat statC) {
        return getDeltaLong(
                statA,
                "Commit_counter",
                statA.getCommit_counter(),
                statB.getCommit_counter(),
                statC.getCommit_counter());
    }

    /**
     * Get the field of failCounter
     *
     * @param statA an instance of StandbyServerStat
     * @param statB an instance of StandbyServerStat
     * @param statC an instance of StandbyServerStat
     * @return long
     */
    private long getFailCounter(
            StandbyServerStat statA, StandbyServerStat statB, StandbyServerStat statC) {
        return getDeltaLong(
                statA,
                "Fail_counter",
                statA.getFail_counter(),
                statB.getFail_counter(),
                statC.getFail_counter());
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @param object the object of this StandbyServerStat
     * @param fieldName the field name but the initial character should be capital
     * @return a field of this object
     */
    private long getDeltaLong(
            StandbyServerStat object,
            String fieldName,
            String fieldA,
            String fieldB,
            String fieldC) {
        long result = 0;
        try {
            if (Long.parseLong(fieldA) < 0 && Long.parseLong(fieldB) > 0) {
                long partA = Long.MAX_VALUE - Long.parseLong(fieldB);
                long partB = Long.parseLong(fieldA) - Long.MIN_VALUE;
                result = partA + partB;
            } else {
                result = Long.parseLong(fieldA) - Long.parseLong(fieldB);
                if (result < 0) {
                    result = Long.parseLong(fieldB) - Long.parseLong(fieldC);
                    long aValue = Long.parseLong(fieldB) + result;
                    Class<?> cc = StandbyServerStat.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Long.toString(aValue));
                }
            }

        } catch (NumberFormatException ee) {
            result = 0;
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
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
    public String getStatus() {
        return status;
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
     * Get the delayTime
     *
     * @return the delayTime
     */
    public String getDelayTime() {
        return delayTime;
    }

    /** @param delayTime the delayTime to set */
    public void setDelayTime(String delayTime) {
        this.delayTime = delayTime;
    }

    /**
     * Get the insertCounter
     *
     * @return the insertCounter
     */
    public String getInsertCounter() {
        return insertCounter;
    }

    /** @param insertCounter the insertCounter to set */
    public void setInsertCounter(String insertCounter) {
        this.insertCounter = insertCounter;
    }

    /**
     * Get the updateCounter
     *
     * @return the updateCounter
     */
    public String getUpdateCounter() {
        return updateCounter;
    }

    /** @param updateCounter the updateCounter to set */
    public void setUpdateCounter(String updateCounter) {
        this.updateCounter = updateCounter;
    }

    /**
     * Get the deleteCounter
     *
     * @return the deleteCounter
     */
    public String getDeleteCounter() {
        return deleteCounter;
    }

    /** @param deleteCounter the deleteCounter to set */
    public void setDeleteCounter(String deleteCounter) {
        this.deleteCounter = deleteCounter;
    }

    /**
     * Get the commitCounter
     *
     * @return the commitCounter
     */
    public String getCommitCounter() {
        return commitCounter;
    }

    /** @param commitCounter the commitCounter to set */
    public void setCommitCounter(String commitCounter) {
        this.commitCounter = commitCounter;
    }

    /**
     * Get the failCounter
     *
     * @return the failCounter
     */
    public String getFailCounter() {
        return failCounter;
    }

    /** @param failCounter the failCounter to set */
    public void setFailCounter(String failCounter) {
        this.failCounter = failCounter;
    }

    /**
     * Get the statusResultMap
     *
     * @return the statusResultMap
     */
    public Map<IDiagPara, String> getStatusResultMap() {
        return statusResultMap;
    }
}
