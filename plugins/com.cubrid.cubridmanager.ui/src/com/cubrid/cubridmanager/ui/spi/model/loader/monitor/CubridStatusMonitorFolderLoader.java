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
package com.cubrid.cubridmanager.ui.spi.model.loader.monitor;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfo;
import com.cubrid.cubridmanager.core.monitoring.model.StatusTemplateInfos;
import com.cubrid.cubridmanager.ui.monitoring.editor.BrokerStatusMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbStatusDumpMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.StatusMonitorViewPart;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.CubridStatusMonitorInstance;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.StatusMonInstanceData;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible to load all status monitor template
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 * @deprecated
 */
public class CubridStatusMonitorFolderLoader extends CubridNodeLoader {

    private static final String DB_STATUS_ID = "dbStatus";
    private static final String BROKER_STATUS_ID = "brokerStatus";

    /**
     * Load children object for parent
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor object
     */
    public void load(ICubridNode parent, final IProgressMonitor monitor) {
        synchronized (this) {
            if (isLoaded()) {
                return;
            }
            ServerInfo serverInfo = parent.getServer().getServerInfo();
            ServerUserInfo userInfo = serverInfo.getLoginedUserInfo();
            if (userInfo == null
                    || StatusMonitorAuthType.AUTH_NONE == userInfo.getStatusMonitorAuth()) {
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }

            if (CompatibleUtil.isSupportBrokerOrDBStatusMonitor(serverInfo)) {
                String brokerStatusId = parent.getId() + NODE_SEPARATOR + BROKER_STATUS_ID;
                ICubridNode brokerStatusNode =
                        new DefaultCubridNode(
                                brokerStatusId,
                                Messages.msgBrokerStatusMonitorName,
                                "icons/navigator/status_item.png");
                brokerStatusNode.setType(CubridNodeType.STATUS_MONITOR_TEMPLATE);
                brokerStatusNode.setViewId(BrokerStatusMonitorViewPart.ID);
                brokerStatusNode.setContainer(false);
                parent.addChild(brokerStatusNode);

                String dbStatusId = parent.getId() + NODE_SEPARATOR + DB_STATUS_ID;
                ICubridNode dbStautsNode =
                        new DefaultCubridNode(
                                dbStatusId,
                                Messages.msgDbStatusMonitorName,
                                "icons/navigator/status_item.png");
                dbStautsNode.setType(CubridNodeType.STATUS_MONITOR_TEMPLATE);
                dbStautsNode.setViewId(DbStatusDumpMonitorViewPart.ID);
                dbStautsNode.setContainer(false);
                parent.addChild(dbStautsNode);

                createTempStatusNode(parent);
            } else {
                if (!loadStatusTempInfoTask(parent, monitor, serverInfo)) {
                    return;
                }
            }
            Collections.sort(parent.getChildren());
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * load sub node by the task of getStatusTemplateInfos
     *
     * @param parent the parent node
     * @param monitor the instance of IProgressMonitor
     * @param serverInfo the instance of serverInfo
     * @return boolean false if error occur, true if succeed
     */
    private boolean loadStatusTempInfoTask(
            ICubridNode parent, final IProgressMonitor monitor, ServerInfo serverInfo) {
        StatusTemplateInfos statusTemplateInfos = new StatusTemplateInfos();
        final CommonQueryTask<StatusTemplateInfos> task =
                new CommonQueryTask<StatusTemplateInfos>(
                        serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), statusTemplateInfos);
        monitorCancel(monitor, new ITask[] {task});
        task.execute();
        final String errorMsg = task.getErrorMsg();
        if (!monitor.isCanceled() && errorMsg != null && errorMsg.trim().length() > 0) {
            parent.removeAllChild();
            openErrorBox(errorMsg);
            setLoaded(true);
            return false;
        }
        if (monitor.isCanceled()) {
            setLoaded(true);
            return false;
        }
        parent.removeAllChild();
        statusTemplateInfos = task.getResultModel();
        if (statusTemplateInfos != null) {
            List<StatusTemplateInfo> list =
                    statusTemplateInfos.getStatusTemplateInfoList().getStatusTemplateInfoList();
            if (list != null) {
                for (StatusTemplateInfo statusTemplateInfo : list) {
                    String id = parent.getId() + NODE_SEPARATOR + statusTemplateInfo.getName();
                    ICubridNode logInfoNode =
                            new DefaultCubridNode(
                                    id,
                                    statusTemplateInfo.getName(),
                                    "icons/navigator/status_item.png");
                    logInfoNode.setType(CubridNodeType.STATUS_MONITOR_TEMPLATE);
                    logInfoNode.setModelObj(statusTemplateInfo);
                    logInfoNode.setViewId(StatusMonitorViewPart.ID);
                    logInfoNode.setContainer(false);
                    parent.addChild(logInfoNode);
                }
            }
        }
        return true;
    }

    /**
     * Create temp status node
     *
     * @param parent the parent node
     */
    private void createTempStatusNode(ICubridNode parent) {
        CubridStatusMonitorInstance instance = CubridStatusMonitorInstance.getInstance();
        ServerInfo serverInfo = parent.getServer().getServerInfo();
        String[] keys = QueryOptions.getAllStatusMonitorKey(serverInfo);
        for (String key : keys) {
            StatusMonInstanceData data = instance.loadSetting(key);
            instance.addData(key, data);
        }
        Map<String, StatusMonInstanceData> map = instance.getDataMap();
        for (Map.Entry<String, StatusMonInstanceData> entry : map.entrySet()) {
            String key = entry.getKey();
            String prefix = QueryOptions.getPrefix(serverInfo);
            if (!key.startsWith(prefix)) {
                continue;
            }
            String label = key.substring(key.lastIndexOf(".") + 1);
            StatusMonInstanceData monInstaceData = entry.getValue();
            String statusId = parent.getId() + NODE_SEPARATOR + label;
            ICubridNode stautsNode =
                    new DefaultCubridNode(statusId, label, "icons/navigator/status_item.png");
            stautsNode.setType(CubridNodeType.STATUS_MONITOR_TEMPLATE);
            stautsNode.setModelObj(monInstaceData);
            String viewId = "";
            switch (monInstaceData.getMonitorType()) {
                case BROKER:
                    viewId = BrokerStatusMonitorViewPart.ID;
                    break;
                case DATABASE:
                    viewId = DbStatusDumpMonitorViewPart.ID;
                    break;
                default:
            }
            if ("".equals(viewId)) {
                return;
            }
            stautsNode.setViewId(viewId);
            stautsNode.setContainer(false);
            parent.addChild(stautsNode);
        }
    }
}
