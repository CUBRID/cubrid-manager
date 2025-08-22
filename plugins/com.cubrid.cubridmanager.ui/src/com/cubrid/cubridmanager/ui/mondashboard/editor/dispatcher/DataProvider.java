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

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.thread.ThreadCountOutOfBoundsException;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.task.GetDbModeTask;
import com.cubrid.cubridmanager.core.mondashboard.task.GetHeartbeatNodeInfoTask;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatData;
import com.cubrid.cubridmanager.core.monitoring.model.HostStatDataProxy;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.mondashboard.editor.ConnectionManager;
import com.cubrid.cubridmanager.ui.mondashboard.editor.DatabaseDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.HostDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.helper.DatabaseMonitorPartHelper;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.helper.HostMonitorPartHelper;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * The <code>DataProvider</code> is responsible to provide data for monitoring dashboard related
 * part. Every <code>DataGenerator</code> has a <code>DataProvider</code> instance.
 *
 * @author pangqiren
 * @version 1.0 - 2010-7-5 created by pangqiren
 */
public class DataProvider implements IDataProvider {

    private static final Logger LOGGER = LogUtil.getLogger(DataProvider.class);

    public static final int TIME_OUT_MILL = 3000;
    private HostStatData hostDiagOldOneStatusResult;
    private HostStatData hostDiagOldTwoStatusResult;
    private final HostStatDataProxy hostStatDataProxy;
    private int hostStatRequestCount;

    private Calendar lastSec;
    private Calendar nowSec;
    private float inter;
    private BrokerDiagData brokerDiagOldOneStatusResult;
    private BrokerDiagData brokerDiagOldTwoStatusResult;
    private int brokerdiagRequestCount;

    private boolean isExecuteHostStatTask = false;
    private boolean isExecuteHeartbeatTask = false;
    private boolean isExecuteBrokerStatTask = false;
    private boolean isExecuteDbModeTask = false;

    private DataGenerator generator;
    private HostNode connectedHostNode;
    private ServerInfo serverInfo;
    // when start to update UI
    private int requestCount;
    private boolean isFirstConnected = true;

    private final List<HostNode> hostNodeList = new ArrayList<HostNode>();
    private final List<DatabaseNode> uniqueDbNodeList = new ArrayList<DatabaseNode>();
    private final List<DatabaseDataProvider> dbDataProviderList =
            new ArrayList<DatabaseDataProvider>();

    private final List<BrokerNode> uniqueBrokerNodeList = new ArrayList<BrokerNode>();
    private final BrokerDataProvider brokerDataProvider;

    private CommonQueryTask<HostStatData> hostStatTask;
    private CommonQueryTask<BrokerDiagData> brokerDiagTask;
    private GetHeartbeatNodeInfoTask getHeartbeatNodeInfoTask;
    private GetDbModeTask getDbModeTask;
    // Determine whether every task use a single thread
    private static final boolean IS_MULTI_THREAD = true;
    private String errorMsg = "";

    public DataProvider() {
        hostStatDataProxy = new HostStatDataProxy();
        brokerDataProvider = new BrokerDataProvider();
    }

    public void setDataGenerator(DataGenerator generator) {
        this.generator = generator;
    }

    /** Set the default value */
    private void setDefaultValue() {

        boolean isOldExecuteHostStatTask = isExecuteHostStatTask;

        isExecuteHostStatTask = false;
        isExecuteHeartbeatTask = false;
        isExecuteBrokerStatTask = false;
        isExecuteDbModeTask = false;

        errorMsg = "";
        getHeartbeatNodeInfoTask = null;
        hostNodeList.clear();
        connectedHostNode = null;
        uniqueDbNodeList.clear();
        uniqueBrokerNodeList.clear();

        List<DataUpdateListener> listenerList = generator.getListeners();
        for (DataUpdateListener listener : listenerList) {
            addHaNode(listener.getModel());
        }

        if (connectedHostNode == null && !hostNodeList.isEmpty()) {
            connectedHostNode = hostNodeList.get(0);
        }

        if (connectedHostNode == null) {
            return;
        }

        filterDatabaseDataProvider();

        if (!uniqueBrokerNodeList.isEmpty()) {
            brokerDataProvider.setDataGenerator(generator);
            brokerDataProvider.setBrokerNodeList(uniqueBrokerNodeList);
        }

        // Connect host and set time out
        if (!connectedHostNode.isConnected() && !connectedHostNode.isConnecting()) {
            for (final HostNode hostNode : hostNodeList) {
                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        hostNode.setConnected(false);
                                    }
                                });
            }
            ConnectionManager.connectHost(connectedHostNode, true, !isFirstConnected);
        }

        final boolean isCanConnected = connectedHostNode.isConnected();
        for (final HostNode hostNode : hostNodeList) {
            if (hostNode != connectedHostNode) {
                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        hostNode.setConnected(isCanConnected);
                                        hostNode.setErrorMsg(connectedHostNode.getLastErrorMsg());
                                    }
                                });
            }
        }
        if (isCanConnected) {
            isFirstConnected = true;
            serverInfo = connectedHostNode.getServerInfo();
            if (serverInfo != null) {
                MonitoringTask monitoringTask = serverInfo.getMonitoringTask();
                monitoringTask.setTimeout(DataProvider.TIME_OUT_MILL);
            }
        } else {
            isFirstConnected = false;
        }

        for (DataUpdateListener listener : listenerList) {
            if (listener instanceof HostMonitorPartHelper) {
                isExecuteHostStatTask = isCanConnected;
                isExecuteHeartbeatTask = isCanConnected;
            } else if (listener instanceof DatabaseMonitorPartHelper) {
                isExecuteHostStatTask = isCanConnected;
                isExecuteHeartbeatTask = isCanConnected;
                isExecuteDbModeTask = isCanConnected;
            } else if (listener instanceof HostDashboardViewPart) {
                isExecuteHostStatTask = isCanConnected;
                isExecuteBrokerStatTask = isCanConnected;
            } else if (listener instanceof DatabaseDashboardViewPart) {
                isExecuteHostStatTask = isCanConnected;
                isExecuteHeartbeatTask = isCanConnected;
                isExecuteDbModeTask = isCanConnected;
            }
        }

        ServerType serverType = serverInfo == null ? null : serverInfo.getServerType();
        isExecuteBrokerStatTask =
                isExecuteBrokerStatTask
                        && (serverType == ServerType.BOTH || serverType == ServerType.BROKER);

        if (isOldExecuteHostStatTask != isExecuteHostStatTask) {
            hostStatRequestCount = 0;
        }
    }

    /**
     * Set error message to all host node
     *
     * @param errorMsg The String
     */
    private void setErrorMsg(String errorMsg) {
        for (HostNode hostNode : hostNodeList) {
            if (hostNode.isConnected()) {
                hostNode.setErrorMsg(errorMsg);
            }
        }
    }

    /** Filter the database provider */
    private void filterDatabaseDataProvider() {
        // delete the unused DatabaseDataProvider
        List<DatabaseDataProvider> providerList = new ArrayList<DatabaseDataProvider>();
        providerList.addAll(dbDataProviderList);
        for (DatabaseDataProvider dbDataProvider : providerList) {
            DatabaseNode node = dbDataProvider.getDatabaseNode();
            boolean isNodeExist = false;
            for (DatabaseNode dbNode : uniqueDbNodeList) {
                if (node.equals(dbNode)) {
                    isNodeExist = true;
                    break;
                }
            }
            if (!isNodeExist) {
                dbDataProviderList.remove(dbDataProvider);
            }
        }

        // add the DatabaseDataProvider
        for (DatabaseNode dbNode : uniqueDbNodeList) {
            boolean isHaveProvider = false;
            for (DatabaseDataProvider dbDataProvider : dbDataProviderList) {
                DatabaseNode databaseNode = dbDataProvider.getDatabaseNode();
                if (dbNode.equals(databaseNode)) {
                    isHaveProvider = true;
                    break;
                }
            }
            if (!isHaveProvider) {
                IDataProvider dbDataProvider = new DatabaseDataProvider(dbNode);
                dbDataProvider.setDataGenerator(generator);
                dbDataProviderList.add((DatabaseDataProvider) dbDataProvider);
            }
        }
    }

    /**
     * Add the HANode object
     *
     * @param haNode The HANode
     */
    private void addHaNode(HANode haNode) {
        if (haNode instanceof HostNode) {
            addHostNode((HostNode) haNode);
        } else if (haNode instanceof DatabaseNode) {
            addDatabaseNode((DatabaseNode) haNode);
        } else if (haNode instanceof BrokerNode) {
            addBrokerNode((BrokerNode) haNode);
        }
    }

    /**
     * Add the HostNode object
     *
     * @param hostNode The HostNode
     */
    private void addHostNode(HostNode hostNode) {
        for (HostNode node : hostNodeList) {
            if (node == hostNode) {
                return;
            }
        }
        if (hostNode.isConnected()) {
            this.connectedHostNode = hostNode;
        }
        hostNodeList.add(hostNode);
    }

    /**
     * Add the DatabaseNode object
     *
     * @param dbNode The DatabaseNode
     */
    private void addDatabaseNode(final DatabaseNode dbNode) {
        if (!uniqueDbNodeList.contains(dbNode)) {
            uniqueDbNodeList.add(dbNode);
            addHostNode(dbNode.getParent());
        }
    }

    /**
     * Add the BrokerNode object
     *
     * @param brokerNode The BrokerNode
     */
    private void addBrokerNode(final BrokerNode brokerNode) {
        if (!uniqueBrokerNodeList.contains(brokerNode)) {
            uniqueBrokerNodeList.add(brokerNode);
            addHostNode(brokerNode.getParent());
        }
    }

    /**
     * Return whether to allow update
     *
     * @return The boolean
     */
    public boolean isAllowUpdate() {
        if (requestCount >= 2) {
            return true;
        }
        return false;
    }

    /**
     * Execute task by multi-thread
     *
     * @throws InterruptedException the exception
     */
    public void executeInThread() throws InterruptedException {
        // multi thread execution
        List<Runnable> runnableList = new ArrayList<Runnable>();
        if (isExecuteHostStatTask) {
            hostStatTask =
                    new CommonQueryTask<HostStatData>(
                            serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), new HostStatData());
            hostStatTask.setTimeout(TIME_OUT_MILL);
            runnableList.add(hostStatTask);
        }

        if (isExecuteBrokerStatTask) {
            brokerDiagTask =
                    new CommonQueryTask<BrokerDiagData>(
                            serverInfo,
                            CommonSendMsg.getGetBrokerStatusItems(),
                            new BrokerDiagData());
            brokerDiagTask.setTimeout(TIME_OUT_MILL);
            runnableList.add(brokerDiagTask);
        }

        List<String> dbList = new ArrayList<String>();
        for (DatabaseNode dbNode : uniqueDbNodeList) {
            dbList.add(dbNode.getDbName());
        }
        if (isExecuteHeartbeatTask) {
            getHeartbeatNodeInfoTask = new GetHeartbeatNodeInfoTask(serverInfo);
            getHeartbeatNodeInfoTask.setAllDb(false);
            getHeartbeatNodeInfoTask.setDbList(dbList);
            getHeartbeatNodeInfoTask.setTimeout(TIME_OUT_MILL);
            runnableList.add(getHeartbeatNodeInfoTask);
        }

        if (isExecuteDbModeTask) {
            getDbModeTask = new GetDbModeTask(serverInfo);
            getDbModeTask.setDbList(dbList);
            getDbModeTask.setTimeout(TIME_OUT_MILL);
            runnableList.add(getDbModeTask);
        }
        // execute broker related task
        if (!uniqueBrokerNodeList.isEmpty()) {
            runnableList.addAll(brokerDataProvider.getExecRunnableList());
        }

        if (IS_MULTI_THREAD && !runnableList.isEmpty()) {
            try {
                generator.getThreadPoolManager().execute(runnableList, true);
            } catch (ThreadCountOutOfBoundsException e) {
                LOGGER.error("", e);
            }
            join(runnableList, 350);
            runnableList.clear();
        }

        // get database status information
        List<HADatabaseStatusInfo> dbStatusInfoList = null;
        if (isExecuteDbModeTask) {
            dbStatusInfoList = getDbModeTask.getDbModes();
        }
        // execute database related task
        for (int i = 0; i < dbDataProviderList.size(); i++) {
            DatabaseNode dbNode = dbDataProviderList.get(i).getDatabaseNode();
            HADatabaseStatusInfo dbStatusInfo =
                    HAUtil.getDatabaseStatusInfo(dbStatusInfoList, dbNode.getDbName());
            dbDataProviderList.get(i).setDbStatusInfo(dbStatusInfo);
            runnableList.addAll(dbDataProviderList.get(i).getExecRunnableList());
        }
        if (IS_MULTI_THREAD && !runnableList.isEmpty()) {
            try {
                generator.getThreadPoolManager().execute(runnableList, true);
            } catch (ThreadCountOutOfBoundsException e) {
                LOGGER.error("", e);
            }
            join(runnableList, 20);
        } else {
            execute(runnableList);
        }
    }

    /**
     * Join the current thread
     *
     * @param runnableList List<Runnable>
     * @param time long
     */
    private void join(List<Runnable> runnableList, long time) {
        while (!isDone(runnableList)) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * Whether all runnable object is done
     *
     * @param runnableList List<Runnable>
     * @return boolean
     */
    private boolean isDone(List<Runnable> runnableList) {
        boolean isDone = true;
        for (Runnable runnableObj : runnableList) {
            if (runnableObj instanceof AbstractTask) {
                isDone = isDone && ((AbstractTask) runnableObj).isDone();
            }
        }
        return isDone;
    }

    /**
     * Execute all runnable orderly in current thread
     *
     * @param runnableList List<Runnable>
     */
    private void execute(List<Runnable> runnableList) {
        for (Runnable runnableObj : runnableList) {
            runnableObj.run();
        }
    }

    /**
     * Get the newest update data
     *
     * @return DataChangedEvent
     */
    public DataChangedEvent getUpdateValue() {

        setDefaultValue();
        DataChangedEvent dataChangedEvent = new DataChangedEvent(this);
        Set<MondashDataResult> resultSet = new HashSet<MondashDataResult>();

        try {
            executeInThread();
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }

        // handle with the data
        // get host status
        Map<IDiagPara, String> hostMap = new HashMap<IDiagPara, String>();
        if (isExecuteHostStatTask) {
            performHostStatTask(hostMap);
        }
        // get broker status
        if (isExecuteBrokerStatTask) {
            performBrokerDiagTask(hostMap);
        }
        if (isExecuteHostStatTask || isExecuteBrokerStatTask) {
            MondashDataResult hostResult = new MondashDataResult();
            hostResult.setName(generator.getName());
            hostResult.putUpdateMap(hostMap);
            resultSet.add(hostResult);
        }

        // get HA node status
        List<HAHostStatusInfo> haHostStatusInfoList = null;
        if (isExecuteHeartbeatTask) {
            performHeartbeatNodeInfoTask();
            haHostStatusInfoList =
                    getHeartbeatNodeInfoTask == null
                            ? null
                            : getHeartbeatNodeInfoTask.getHAHostStatusList();
        }

        // get database status
        List<HADatabaseStatusInfo> dbStatusInfoList = null;
        String dbStatusErrorInfo = null;
        if (isExecuteDbModeTask) {
            dbStatusErrorInfo = getDbModeTask.getErrorMsg();
            if (dbStatusErrorInfo != null && dbStatusErrorInfo.trim().length() > 0) {
                dbStatusErrorInfo += ", can not get database status.";
            }
            dbStatusInfoList = getDbModeTask.getDbModes();
        }

        // get some broker status
        DataChangedEvent event =
                brokerDataProvider == null ? null : brokerDataProvider.getUpdateValue();
        for (int i = 0; i < uniqueBrokerNodeList.size(); i++) {
            BrokerNode brokerNode = uniqueBrokerNodeList.get(i);
            if (event != null) {
                String brokerName = brokerNode.getBrokerName();
                dataChangedEvent
                        .getBrokerInfosMap()
                        .put(brokerName, event.getBrokerInfosMap().get(brokerName));
                dataChangedEvent
                        .getBrokerStatusInfosMap()
                        .put(brokerName, event.getBrokerStatusInfosMap().get(brokerName));
                dataChangedEvent
                        .getBrokerDiagDataMap()
                        .put(brokerName, event.getBrokerDiagDataMap().get(brokerName));
            }
        }

        // get some databases status
        for (int i = 0; i < dbDataProviderList.size(); i++) {
            Set<MondashDataResult> dbResultSet =
                    dbDataProviderList.get(i).getUpdateValue().getResultSet();
            if (dbResultSet != null) {
                resultSet.addAll(dbResultSet);
            }

            // set error
            DatabaseNode dbNode = dbDataProviderList.get(i).getDatabaseNode();
            if (StringUtil.isNotEmpty(dbStatusErrorInfo) && dbNode.getParent().isConnected()) {
                dbNode.setErrorMsg(dbStatusErrorInfo);
            }

            HADatabaseStatusInfo dbStatusInfo =
                    HAUtil.getDatabaseStatusInfo(dbStatusInfoList, dbNode.getDbName());
            String errorInfo = dbStatusInfo == null ? null : dbStatusInfo.getErrorInfo();
            if (StringUtil.isNotEmpty(errorInfo) && dbNode.getParent().isConnected()) {
                dbNode.setErrorMsg(errorInfo);
            }
        }

        if (requestCount < 2) {
            requestCount++;
        }
        dataChangedEvent.setResultSet(resultSet);
        dataChangedEvent.setHaHostStatusInfoList(haHostStatusInfoList);
        dataChangedEvent.setDbStatusInfoList(dbStatusInfoList);

        setErrorMsg(errorMsg);
        if (errorMsg != null && errorMsg.trim().length() > 0) {
            LOGGER.error(errorMsg);
        }

        return dataChangedEvent;
    }

    /**
     * Perform the task of HostStatData
     *
     * @param returnMap an instance of Map<IDiagPara, String>
     */
    private void performHostStatTask(Map<IDiagPara, String> returnMap) {
        HostStatData hostDiagStatusResult =
                hostStatTask == null ? null : hostStatTask.getResultModel();
        if (hostDiagStatusResult == null) {
            hostStatRequestCount = 0;
            return;
        }
        if (!hostDiagStatusResult.getStatus()) {
            String detailMsg = "can not get host cup and memory and iowait information.";
            showErrorMsg(hostDiagStatusResult.getNote(), detailMsg);
            hostStatRequestCount = 0;
            return;
        }
        if (hostStatRequestCount == 0) {
            hostDiagOldOneStatusResult = new HostStatData();
            hostDiagOldTwoStatusResult = new HostStatData();

            returnMap.putAll(hostStatDataProxy.getDiagStatusResultMap());
            hostStatRequestCount++;

            hostDiagOldOneStatusResult.copyFrom(hostDiagStatusResult);
        } else if (hostStatRequestCount == 1) {

            hostStatDataProxy.compute(hostDiagStatusResult, hostDiagOldOneStatusResult);
            returnMap.putAll(hostStatDataProxy.getDiagStatusResultMap());
            hostStatRequestCount++;

            hostDiagOldTwoStatusResult.copyFrom(hostDiagOldOneStatusResult);
            hostDiagOldOneStatusResult.copyFrom(hostDiagStatusResult);
        } else {
            hostStatDataProxy.compute(
                    hostDiagStatusResult, hostDiagOldOneStatusResult, hostDiagOldTwoStatusResult);
            returnMap.putAll(hostStatDataProxy.getDiagStatusResultMap());

            hostDiagOldTwoStatusResult.copyFrom(hostDiagOldOneStatusResult);
            hostDiagOldOneStatusResult.copyFrom(hostDiagStatusResult);
        }
    }

    /** Execute to HeartbeatNodeInfoTask */
    private void performHeartbeatNodeInfoTask() {
        if (getHeartbeatNodeInfoTask == null) {
            return;
        }
        if (!getHeartbeatNodeInfoTask.isSuccess()) {
            String detailMsg = "can not get database HA topology information";
            showErrorMsg(getHeartbeatNodeInfoTask.getErrorMsg(), detailMsg);
            getHeartbeatNodeInfoTask = null;
            return;
        }
    }

    /**
     * Perform the task of BrokerDiagData
     *
     * @param returnMap the instance of TreeMap<IDiagPara, String>
     */
    public void performBrokerDiagTask(Map<IDiagPara, String> returnMap) {
        BrokerDiagData brokerDiagStatusResult =
                brokerDiagTask == null ? null : brokerDiagTask.getResultModel();
        if (brokerDiagStatusResult == null) {
            return;
        }
        if (!brokerDiagStatusResult.getStatus()) {
            String detailMsg = "can not get broker diagnosis information.";
            showErrorMsg(brokerDiagStatusResult.getNote(), detailMsg);
            return;
        }
        if (brokerdiagRequestCount == 0) {
            brokerDiagOldOneStatusResult = new BrokerDiagData();
            brokerDiagOldTwoStatusResult = new BrokerDiagData();

            brokerdiagRequestCount++;

            brokerDiagOldOneStatusResult.copyFrom(brokerDiagStatusResult);
        } else if (brokerdiagRequestCount == 1) {
            lastSec = Calendar.getInstance();

            BrokerDiagData brokerDiagDataDelta = new BrokerDiagData();
            brokerDiagDataDelta.getDelta(brokerDiagStatusResult, brokerDiagOldOneStatusResult);
            returnMap.putAll(brokerDiagDataDelta.getDiagStatusResultMap());
            brokerdiagRequestCount++;

            brokerDiagOldTwoStatusResult.copyFrom(brokerDiagOldOneStatusResult);
            brokerDiagOldOneStatusResult.copyFrom(brokerDiagStatusResult);
        } else {
            nowSec = Calendar.getInstance();
            double interval =
                    (double) (nowSec.getTimeInMillis() - lastSec.getTimeInMillis()) / 1000;
            inter = (float) interval;
            lastSec = nowSec;

            BrokerDiagData brokerDiagStatusResultDelta = new BrokerDiagData();
            brokerDiagStatusResultDelta.getDelta(
                    brokerDiagStatusResult,
                    brokerDiagOldOneStatusResult,
                    brokerDiagOldTwoStatusResult,
                    inter);
            returnMap.putAll(brokerDiagStatusResultDelta.getDiagStatusResultMap());

            brokerDiagOldTwoStatusResult.copyFrom(brokerDiagOldOneStatusResult);
            brokerDiagOldOneStatusResult.copyFrom(brokerDiagStatusResult);
        }
    }

    /**
     * Show error message
     *
     * @param errMsg String
     * @param detailMsg String
     */
    private void showErrorMsg(String errMsg, String detailMsg) {
        if (errMsg != null && errMsg.length() > 0) {
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

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
