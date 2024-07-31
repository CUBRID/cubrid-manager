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
package com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStat;
import com.cubrid.cubridmanager.core.mondashboard.model.StandbyServerStatProxy;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStat;
import com.cubrid.cubridmanager.core.monitoring.model.DbProcStatProxy;
import com.cubrid.cubridmanager.core.monitoring.model.DbStatDumpData;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.mondashboard.editor.DatabaseDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.helper.DatabaseMonitorPartHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

/**
 * The <code>DatabaseDataProvider</code> is responsible to provide data for monitoring dashboard
 * related part.
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-5 created by pangqiren
 */
public class DatabaseDataProvider implements IDataProvider {

    private static final Logger LOGGER = LogUtil.getLogger(DatabaseDataProvider.class);

    private final DatabaseNode dbNode;
    private DataGenerator generator;

    private StandbyServerStat dbStandbyStatOldOneResult;
    private StandbyServerStat dbStandbyStatOldTwoResult;
    private final StandbyServerStatProxy standbyStatProxy;
    private int standyServerStatRequestCount;

    private DbProcStat dbProcStatOldOneResult;
    private DbProcStat dbProcStatOldTwoResult;
    private final DbProcStatProxy dbProcStatProxy;
    private int dbProcRequestCount;

    private DbStatDumpData dbStatDumpOldOneResult;
    private DbStatDumpData dbStatDumpOldTwoResult;
    private Calendar lastSec;
    private Calendar nowSec;
    private int dbdumpRequestCount;

    private boolean isExecuteStandbyStaTask = false;
    private boolean isExecuteDbDumpTask = false;
    private boolean isExecuteDbProcessTask = false;

    private CommonQueryTask<StandbyServerStat> standbyServerStatTask;
    private CommonQueryTask<DbProcStat> dbProcStatTask;
    private CommonQueryTask<DbStatDumpData> dbStatDumptask;

    private HADatabaseStatusInfo dbStatusInfo = null;
    private String errorMsg = "";

    /**
     * The constructor
     *
     * @param dbNode
     * @param generator
     */
    public DatabaseDataProvider(DatabaseNode dbNode) {
        this.dbNode = dbNode;
        dbProcStatProxy = new DbProcStatProxy();
        standbyStatProxy = new StandbyServerStatProxy();
    }

    public void setDataGenerator(DataGenerator generator) {
        this.generator = generator;
    }

    /**
     * Get DatabaseNode object
     *
     * @return The DatabaseNode
     */
    public DatabaseNode getDatabaseNode() {
        return this.dbNode;
    }

    public void setDbStatusInfo(HADatabaseStatusInfo dbStatusInfo) {
        this.dbStatusInfo = dbStatusInfo;
    }

    /** Set default value */
    private void setDefaultValue() {

        boolean isOldExecuteStandbyStaTask = isExecuteStandbyStaTask;
        boolean isOldExecuteDbProcessTask = isExecuteDbProcessTask;

        isExecuteStandbyStaTask = false;
        isExecuteDbDumpTask = false;
        isExecuteDbProcessTask = false;
        errorMsg = "";

        DBStatusType statusType =
                dbStatusInfo == null ? DBStatusType.UNKNOWN : dbStatusInfo.getStatusType();
        boolean isDatabaseStarted = DBStatusType.isDbStarted(statusType);
        HostNode hostNode = dbNode.getParent();
        if (hostNode != null && hostNode.isConnected()) {
            List<DataUpdateListener> listenerList = generator.getListeners();
            for (DataUpdateListener listener : listenerList) {
                HANode node = listener.getModel();
                if (!dbNode.equals(node)) {
                    continue;
                }
                if (listener instanceof DatabaseMonitorPartHelper
                        || listener instanceof DatabaseDashboardViewPart) {
                    isExecuteDbProcessTask = isDatabaseStarted;
                    isExecuteStandbyStaTask =
                            statusType == DBStatusType.STANDBY
                                    || statusType == DBStatusType.MAINTENANCE;
                }
                if (listener instanceof DatabaseDashboardViewPart) {
                    isExecuteDbDumpTask = isDatabaseStarted;
                }
            }
        }

        if (isOldExecuteStandbyStaTask != isExecuteStandbyStaTask) {
            standyServerStatRequestCount = 0;
        }
        if (isOldExecuteDbProcessTask != isExecuteDbProcessTask) {
            dbProcRequestCount = 0;
        }
    }

    /**
     * Execute task by multi-thread
     *
     * @return List<Runnable>
     */
    public List<Runnable> getExecRunnableList() {
        setDefaultValue();

        List<Runnable> runnableList = new ArrayList<Runnable>();
        // execute in multi thread
        if (isExecuteStandbyStaTask) {
            standbyServerStatTask =
                    new CommonQueryTask<StandbyServerStat>(
                            dbNode.getParent().getServerInfo(),
                            CommonSendMsg.getStandbyServerstatMsgItems(),
                            new StandbyServerStat());
            standbyServerStatTask.setDbName(dbNode.getDbName());
            standbyServerStatTask.setDbid(dbNode.getDbUser());
            standbyServerStatTask.setDbpasswd(
                    dbNode.getDbPassword() == null ? "" : dbNode.getDbPassword());
            standbyServerStatTask.setTimeout(DataProvider.TIME_OUT_MILL);
            runnableList.add(standbyServerStatTask);
        }
        if (isExecuteDbDumpTask) {
            dbStatDumptask =
                    new CommonQueryTask<DbStatDumpData>(
                            dbNode.getParent().getServerInfo(),
                            CommonSendMsg.getCommonDatabaseSendMsg(),
                            new DbStatDumpData());
            dbStatDumptask.setDbName(dbNode.getDbName());
            dbStatDumptask.setTimeout(DataProvider.TIME_OUT_MILL);
            runnableList.add(dbStatDumptask);
        }
        if (isExecuteDbProcessTask) {
            dbProcStatTask =
                    new CommonQueryTask<DbProcStat>(
                            dbNode.getParent().getServerInfo(),
                            CommonSendMsg.getCommonDatabaseSendMsg(),
                            new DbProcStat());
            dbProcStatTask.setDbName(dbNode.getDbName());
            dbProcStatTask.setTimeout(DataProvider.TIME_OUT_MILL);
            runnableList.add(dbProcStatTask);
        }
        return runnableList;
    }

    /**
     * Get the newest update data
     *
     * @return DataChangedEvent
     */
    public DataChangedEvent getUpdateValue() {

        DataChangedEvent dataChangedEvent = new DataChangedEvent(this);
        if (!isExecuteStandbyStaTask && !isExecuteDbDumpTask && !isExecuteDbProcessTask) {
            dbNode.setErrorMsg("");
            return dataChangedEvent;
        }

        MondashDataResult result = new MondashDataResult(dbNode.getDbName());
        Map<IDiagPara, String> dbMap = new HashMap<IDiagPara, String>();

        if (isExecuteStandbyStaTask) {
            performStandbyServerStatTask(dbMap);
        }
        if (isExecuteDbDumpTask) {
            performDbdumpTask(dbMap);
        }
        if (isExecuteDbProcessTask) {
            performDbProcTask(dbMap);
        }
        result.putUpdateMap(dbMap);

        Set<MondashDataResult> resultSet = new HashSet<MondashDataResult>();
        resultSet.add(result);
        dataChangedEvent.setResultSet(resultSet);

        dbNode.setErrorMsg(errorMsg);
        if (errorMsg != null && errorMsg.trim().length() > 0) {
            LOGGER.error(errorMsg);
        }

        return dataChangedEvent;
    }

    /**
     * Perform the standbyserverstat task
     *
     * @param updateMap the given map
     */
    public void performStandbyServerStatTask(Map<IDiagPara, String> updateMap) {
        StandbyServerStat dbStandbyStatResult =
                standbyServerStatTask == null ? null : standbyServerStatTask.getResultModel();
        if (dbStandbyStatResult == null) {
            standyServerStatRequestCount = 0;
            return;
        }
        if (!dbStandbyStatResult.getStatus()) {
            String detailMsg = "can not get delay time and record change count value.";
            showErrorMsg(dbStandbyStatResult.getNote(), detailMsg);
            standyServerStatRequestCount = 0;
            return;
        }
        if (standyServerStatRequestCount == 0) {
            dbStandbyStatOldOneResult = new StandbyServerStat();
            dbStandbyStatOldTwoResult = new StandbyServerStat();

            standyServerStatRequestCount++;

            dbStandbyStatOldOneResult.copyFrom(dbStandbyStatResult);
        } else if (standyServerStatRequestCount == 1) {

            standbyStatProxy.compute(dbStandbyStatResult, dbStandbyStatOldOneResult);
            updateMap.putAll(standbyStatProxy.getStatusResultMap());
            standyServerStatRequestCount++;

            dbStandbyStatOldTwoResult.copyFrom(dbStandbyStatOldOneResult);
            dbStandbyStatOldOneResult.copyFrom(dbStandbyStatResult);
        } else {

            standbyStatProxy.compute(
                    dbStandbyStatResult, dbStandbyStatOldOneResult, dbStandbyStatOldTwoResult);
            updateMap.putAll(standbyStatProxy.getStatusResultMap());

            dbStandbyStatOldTwoResult.copyFrom(dbStandbyStatOldOneResult);
            dbStandbyStatOldOneResult.copyFrom(dbStandbyStatResult);
        }
    }

    /**
     * Perform the task of dbProcstat
     *
     * @param updateMap the given map
     */
    public void performDbProcTask(Map<IDiagPara, String> updateMap) {
        DbProcStat dbProcStatResult =
                dbProcStatTask == null ? null : dbProcStatTask.getResultModel();
        if (dbProcStatResult == null) {
            dbProcRequestCount = 0;
            return;
        }
        if (!dbProcStatResult.getStatus()) {
            String detailMsg = "can not get cpu and memory information.";
            showErrorMsg(dbProcStatResult.getNote(), detailMsg);
            dbProcRequestCount = 0;
            return;
        }
        if (dbProcRequestCount == 0) {
            dbProcStatOldOneResult = new DbProcStat();
            dbProcStatOldTwoResult = new DbProcStat();

            dbProcRequestCount++;

            dbProcStatOldOneResult.copyFrom(dbProcStatResult);
        } else if (dbProcRequestCount == 1) {

            dbProcStatProxy.compute(dbNode.getDbName(), dbProcStatResult, dbProcStatOldOneResult);
            updateMap.putAll(dbProcStatProxy.getDiagStatusResultMap());

            dbProcRequestCount++;
        } else {

            dbProcStatProxy.compute(dbNode.getDbName(), dbProcStatResult, dbProcStatOldOneResult);
            updateMap.putAll(dbProcStatProxy.getDiagStatusResultMap());

            dbProcStatOldTwoResult.copyFrom(dbProcStatOldOneResult);
            dbProcStatOldOneResult.copyFrom(dbProcStatResult);
        }
    }

    /**
     * Perform the task of dbdump
     *
     * @param updateMap an instance of Map
     */
    public void performDbdumpTask(Map<IDiagPara, String> updateMap) {
        DbStatDumpData dbStatDumpResult =
                dbStatDumptask == null ? null : dbStatDumptask.getResultModel();
        if (dbStatDumpResult == null) {
            return;
        }
        if (!dbStatDumpResult.getStatus()) {
            String detailMsg = "can not get database dump information.";
            showErrorMsg(dbStatDumpResult.getNote(), detailMsg);
            return;
        }
        float inter = 0.0f;
        if (dbdumpRequestCount == 0) {
            dbStatDumpOldOneResult = new DbStatDumpData();
            dbStatDumpOldTwoResult = new DbStatDumpData();

            dbdumpRequestCount++;

            dbStatDumpOldOneResult.copy_from(dbStatDumpResult);
        } else if (dbdumpRequestCount == 1) {
            lastSec = Calendar.getInstance();

            DbStatDumpData dbStatDumpDataDelta = new DbStatDumpData();
            dbStatDumpDataDelta.getDelta(dbStatDumpResult, dbStatDumpOldOneResult);
            updateMap.putAll(dbStatDumpDataDelta.getDiagStatusResultMap());
            dbdumpRequestCount++;

            dbStatDumpOldTwoResult.copy_from(dbStatDumpOldOneResult);
            dbStatDumpOldOneResult.copy_from(dbStatDumpResult);
        } else {
            nowSec = Calendar.getInstance();
            double interval =
                    (double) (nowSec.getTimeInMillis() - lastSec.getTimeInMillis()) / 1000;
            inter = (float) interval;
            lastSec = nowSec;

            DbStatDumpData dbStatDumpDataDelta = new DbStatDumpData();
            dbStatDumpDataDelta.getDelta(
                    dbStatDumpResult, dbStatDumpOldOneResult, dbStatDumpOldTwoResult, inter);

            updateMap.putAll(dbStatDumpDataDelta.getDiagStatusResultMap());

            dbStatDumpOldTwoResult.copy_from(dbStatDumpOldOneResult);
            dbStatDumpOldOneResult.copy_from(dbStatDumpResult);
        }
    }

    /**
     * Show error message
     *
     * @param errMsg String
     * @param detailMsg String
     */
    private void showErrorMsg(String errMsg, String detailMsg) {
        if (errMsg != null && errMsg.length() > 0 && dbNode.getParent().isConnected()) {
            String tmpMsg =
                    errMsg.lastIndexOf(".") >= 0
                            ? errMsg.substring(0, errMsg.length() - 1)
                            : errMsg;
            if (errorMsg.trim().length() > 0) {
                errorMsg = errorMsg + "\r\n" + tmpMsg + " " + detailMsg;
            } else {
                errorMsg = tmpMsg + "," + detailMsg;
            }
        }
    }

    /**
     * Return whether to allow update
     *
     * @return The boolean
     */
    public boolean isAllowUpdate() {
        return true;
    }
}
