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
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerStatusInfosTask;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.monitoring.model.BrokerDiagData;
import com.cubrid.cubridmanager.ui.mondashboard.editor.BrokerDashboardViewPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.helper.BrokerMonitorPartHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.slf4j.Logger;

/**
 * Broker data provider
 *
 * @author pangqiren
 * @version 1.0 - 2010-8-19 created by pangqiren
 */
public class BrokerDataProvider implements IDataProvider {

    private static final Logger LOGGER = LogUtil.getLogger(BrokerDataProvider.class);

    private DataGenerator generator;

    private List<BrokerNode> brokerNodeList = new ArrayList<BrokerNode>();
    private Calendar lastSec;
    private Calendar nowSec;
    private float inter;
    private BrokerDiagData brokerDiagOldOneStatusResult;
    private BrokerDiagData brokerDiagOldTwoStatusResult;
    private int brokerdiagRequestCount;

    private boolean isExecGetBrokerTask = false;

    private GetBrokerStatusInfosTask<BrokerInfos> brokerInfosTask;
    private GetBrokerStatusInfosTask<BrokerStatusInfos> brokerStatusInfosTask;
    private CommonQueryTask<BrokerDiagData> brokerDiagDataTask;

    private String allBrokersStr;
    private String errorMsg = "";
    private ServerInfo serverInfo = null;

    public void setDataGenerator(DataGenerator generator) {
        this.generator = generator;
    }

    public List<BrokerNode> getBrokerNodeList() {
        return brokerNodeList;
    }

    public void setBrokerNodeList(List<BrokerNode> brokerNodeList) {
        this.brokerNodeList = brokerNodeList;
    }

    /** Set default value */
    private void setDefaultValue() {

        isExecGetBrokerTask = false;
        errorMsg = "";
        if (brokerNodeList == null || brokerNodeList.isEmpty()) {
            return;
        }

        BrokerNode brokerNode = null;
        for (BrokerNode node : brokerNodeList) {
            if (node.getParent().isConnected()) {
                brokerNode = node;
                break;
            }
        }
        if (brokerNode == null) {
            return;
        }
        HostNode hostNode = brokerNode.getParent();
        if (hostNode != null && hostNode.isConnected()) {
            List<DataUpdateListener> listenerList = generator.getListeners();
            for (DataUpdateListener listener : listenerList) {
                if (listener instanceof BrokerMonitorPartHelper
                        || listener instanceof BrokerDashboardViewPart) {
                    isExecGetBrokerTask = true;
                    break;
                }
            }
            serverInfo = hostNode.getServerInfo();
        }
        StringBuffer strBuff = new StringBuffer();
        for (BrokerNode node : brokerNodeList) {
            strBuff.append(node.getBrokerName());
            strBuff.append(",");
        }
        strBuff = strBuff.deleteCharAt(strBuff.length() - 1);
        allBrokersStr = strBuff.toString();
    }

    /**
     * Execute task by multi thread
     *
     * @return List<Runnable>
     */
    public List<Runnable> getExecRunnableList() {
        setDefaultValue();
        List<Runnable> runnableList = new ArrayList<Runnable>();
        if (isExecGetBrokerTask) {
            brokerInfosTask =
                    new GetBrokerStatusInfosTask<BrokerInfos>(
                            serverInfo, CommonSendMsg.getGetBrokerStatusItems(), new BrokerInfos());
            brokerInfosTask.setTimeout(DataProvider.TIME_OUT_MILL);
            runnableList.add(brokerInfosTask);

            brokerStatusInfosTask =
                    new GetBrokerStatusInfosTask<BrokerStatusInfos>(
                            serverInfo,
                            CommonSendMsg.getGetBrokerStatusItems(),
                            new BrokerStatusInfos());
            brokerStatusInfosTask.setBrokerName(allBrokersStr);
            brokerStatusInfosTask.setTimeout(DataProvider.TIME_OUT_MILL);
            runnableList.add(brokerStatusInfosTask);

            brokerDiagDataTask =
                    new CommonQueryTask<BrokerDiagData>(
                            serverInfo,
                            CommonSendMsg.getGetBrokerStatusItems(),
                            new BrokerDiagData());
            brokerDiagDataTask.setBName(allBrokersStr);
            brokerDiagDataTask.setTimeout(DataProvider.TIME_OUT_MILL);
            runnableList.add(brokerDiagDataTask);
        }
        return runnableList;
    }

    /**
     * Get broker related update value
     *
     * @return DataChangedEvent
     */
    public DataChangedEvent getUpdateValue() {

        DataChangedEvent dataChangedEvent = new DataChangedEvent(this);
        if (!isExecGetBrokerTask) {
            setErrorMsg("");
            return dataChangedEvent;
        }

        if (isExecGetBrokerTask) {
            BrokerInfos brokerInfos = performGetBrokersInfoTask();
            if (serverInfo != null) {
                serverInfo.setBrokerInfos(brokerInfos);
            }
            BrokerStatusInfos brokerStatusInfos = performGetBrokersStatusTask();
            if (brokerdiagRequestCount < 2) {
                performGetBrokerDiagDataTask(null);
            }
            for (BrokerNode brokerNode : brokerNodeList) {
                String brokerName = brokerNode.getBrokerName();

                dataChangedEvent.getBrokerInfosMap().put(brokerName, brokerInfos);

                dataChangedEvent
                        .getBrokerStatusInfosMap()
                        .put(
                                brokerName,
                                brokerStatusInfos == null
                                        ? null
                                        : brokerStatusInfos.getSubBrokerByName(brokerName));
                if (brokerdiagRequestCount >= 2) {
                    dataChangedEvent
                            .getBrokerDiagDataMap()
                            .put(brokerName, performGetBrokerDiagDataTask(brokerName));
                }
            }
        }
        setErrorMsg(errorMsg);
        if (errorMsg != null && errorMsg.trim().length() > 0) {
            LOGGER.error(errorMsg);
        }
        return dataChangedEvent;
    }

    /**
     * Perform "getbrokersinfo" task
     *
     * @return BrokerInfos
     */
    private BrokerInfos performGetBrokersInfoTask() {
        String detailMsg = "can not get access broker status information and access mode.";
        if (brokerInfosTask == null || showErrorMsg(brokerInfosTask.getErrorMsg(), detailMsg)) {
            return null;
        } else {
            return brokerInfosTask.getResultModel();
        }
    }

    /**
     * Perform "getbrokerstatus" task
     *
     * @return BrokerStatusInfos
     */
    private BrokerStatusInfos performGetBrokersStatusTask() {
        String detailMsg = "can not get apply server information.";
        if (brokerStatusInfosTask == null
                || showErrorMsg(brokerStatusInfosTask.getErrorMsg(), detailMsg)) {
            return null;
        } else {
            return brokerStatusInfosTask.getResultModel();
        }
    }

    /**
     * Perform "getbrokerdiagdata" task
     *
     * @param brokerName String
     * @return BrokerDiagData
     */
    public BrokerDiagData performGetBrokerDiagDataTask(String brokerName) {
        BrokerDiagData brokerDiagStatusResult =
                brokerDiagDataTask == null ? null : brokerDiagDataTask.getResultModel();
        if (brokerDiagStatusResult == null) {
            return null;
        }
        if (!brokerDiagStatusResult.getStatus()) {
            String detailMsg = "can not get session and active session and TPS information.";
            showErrorMsg(brokerDiagDataTask.getErrorMsg(), detailMsg);
            return null;
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

            brokerdiagRequestCount++;

            brokerDiagOldTwoStatusResult.copyFrom(brokerDiagOldOneStatusResult);
            brokerDiagOldOneStatusResult.copyFrom(brokerDiagStatusResult);
        } else {
            nowSec = Calendar.getInstance();
            double interval =
                    (double) (nowSec.getTimeInMillis() - lastSec.getTimeInMillis()) / 1000;
            inter = (float) interval;
            lastSec = nowSec;

            BrokerDiagData latest = brokerDiagStatusResult.getSubBrokerByName(brokerName);
            BrokerDiagData oldOne = brokerDiagOldOneStatusResult.getSubBrokerByName(brokerName);
            BrokerDiagData oldTwo = brokerDiagOldTwoStatusResult.getSubBrokerByName(brokerName);
            if (latest == null || oldOne == null || oldTwo == null) {
                return null;
            }

            BrokerDiagData brokerDiagStatusResultDelta = new BrokerDiagData();
            brokerDiagStatusResultDelta.getDelta(latest, oldOne, oldTwo, inter);

            brokerDiagOldTwoStatusResult.copyFrom(brokerDiagOldOneStatusResult);
            brokerDiagOldOneStatusResult.copyFrom(brokerDiagStatusResult);

            return brokerDiagStatusResultDelta;
        }
        return null;
    }

    /**
     * Show error message
     *
     * @param errMsg String
     * @param detailMsg String
     * @return boolean
     */
    private boolean showErrorMsg(String errMsg, String detailMsg) {
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
            return true;
        }
        return false;
    }

    /**
     * Set error message
     *
     * @param errMsg String
     */
    private void setErrorMsg(String errMsg) {
        for (BrokerNode brokerNode : brokerNodeList) {
            if (brokerNode.getParent().isConnected()) {
                brokerNode.setErrorMsg(errMsg);
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
