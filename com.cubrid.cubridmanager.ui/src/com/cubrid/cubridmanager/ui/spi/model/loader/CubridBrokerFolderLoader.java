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
package com.cubrid.cubridmanager.ui.spi.model.loader;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.logs.model.BrokerLogInfos;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.core.logs.model.LogType;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.logs.editor.LogEditorPart;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridBroker;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

/**
 * This class is responsible to load all children(Script log folder and script log) of broker folder
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridBrokerFolderLoader extends CubridNodeLoader {

    private static final String SQL_LOG_FOLDER_NAME = Messages.msgSqlLogFolderName;
    public static final String SQL_LOG_FOLDER_ID = "Sql log";

    /**
     * Load children object for parent
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor object
     */
    public void load(final ICubridNode parent, final IProgressMonitor monitor) {
        synchronized (this) {
            if (isLoaded()) {
                return;
            }
            // when refresh broker,firstly check whether this broker exist
            int ret = checkBrokerExist(parent, monitor);
            if (ret == 1 || ret == 2) {
                setLoaded(true);
                return;
            }
            if (ret != 0) {
                Display display = Display.getDefault();
                display.syncExec(
                        new Runnable() {
                            public void run() {
                                CommonUITool.openErrorBox(Messages.errBrokerNoExist);
                                CubridNavigatorView navigatorView =
                                        CubridNavigatorView.getNavigatorView(
                                                CubridHostNavigatorView.ID);
                                TreeViewer treeViewer =
                                        navigatorView == null ? null : navigatorView.getViewer();
                                if (treeViewer != null) {
                                    CommonUITool.refreshNavigatorTree(
                                            treeViewer, parent.getParent());
                                }
                            }
                        });
                setLoaded(true);
                return;
            }

            // add sql log folder
            String sqlLogFolderId = parent.getId() + NODE_SEPARATOR + SQL_LOG_FOLDER_ID;
            ICubridNode sqlLogFolder = parent.getChild(sqlLogFolderId);
            if (sqlLogFolder == null) {
                sqlLogFolder =
                        new DefaultCubridNode(
                                sqlLogFolderId, SQL_LOG_FOLDER_NAME, "icons/navigator/folder.png");
                sqlLogFolder.setType(CubridNodeType.BROKER_SQL_LOG_FOLDER);
                sqlLogFolder.setContainer(true);
                parent.addChild(sqlLogFolder);
            }

            // add the children of sql log folder
            ServerInfo serverInfo = parent.getServer().getServerInfo();
            BrokerLogInfos brokerLogInfos = new BrokerLogInfos();
            final CommonQueryTask<BrokerLogInfos> task =
                    new CommonQueryTask<BrokerLogInfos>(
                            serverInfo,
                            CommonSendMsg.getGetBrokerLogFileInfoMSGItems(),
                            brokerLogInfos);
            task.setBroker(parent.getLabel());

            monitorCancel(monitor, new ITask[] {task});
            task.execute();
            final String errorMsg = task.getErrorMsg();
            if (!monitor.isCanceled() && errorMsg != null && errorMsg.trim().length() > 0) {
                sqlLogFolder.removeAllChild();
                openErrorBox(errorMsg);
                setLoaded(true);
                return;
            }
            if (monitor.isCanceled()) {
                setLoaded(true);
                return;
            }
            sqlLogFolder.removeAllChild();
            brokerLogInfos = task.getResultModel();

            List<LogInfo> logInfoList =
                    brokerLogInfos == null
                            ? null
                            : brokerLogInfos.getBrokerLogInfoList().getLogFileInfoList();
            if (logInfoList != null && !logInfoList.isEmpty()) {
                for (LogInfo logInfo : logInfoList) {
                    String id = sqlLogFolder.getId() + NODE_SEPARATOR + logInfo.getName();
                    ICubridNode logInfoNode =
                            new DefaultCubridNode(
                                    id, logInfo.getName(), "icons/navigator/sqllog_item.png");
                    logInfoNode.setContainer(false);
                    logInfoNode.setEditorId(LogEditorPart.ID);
                    logInfoNode.setModelObj(logInfo);
                    if (LogType.SCRIPT.getText().toLowerCase().equals(logInfo.getType())) {
                        logInfoNode.setType(CubridNodeType.BROKER_SQL_LOG);
                        sqlLogFolder.addChild(logInfoNode);
                    }
                }
            }
            Collections.sort(sqlLogFolder.getChildren());
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Check broker whether exist
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor
     * @return <code>0</code> exist;<code>1</code> has error;<code>2</code> canceled;<code>3</code>
     *     no exist;
     */
    private int checkBrokerExist(final ICubridNode parent, final IProgressMonitor monitor) {
        ServerInfo serverInfo = parent.getServer().getServerInfo();
        BrokerInfos brokerInfos = new BrokerInfos();
        final CommonQueryTask<BrokerInfos> getBrokersTask =
                new CommonQueryTask<BrokerInfos>(
                        serverInfo, CommonSendMsg.getCommonSimpleSendMsg(), brokerInfos);

        monitorCancel(monitor, new ITask[] {getBrokersTask});
        getBrokersTask.execute();
        final String msg = getBrokersTask.getErrorMsg();
        if (!monitor.isCanceled() && msg != null && msg.trim().length() > 0) {
            parent.removeAllChild();
            openErrorBox(msg);
            setLoaded(true);
            return 1;
        }
        if (monitor.isCanceled()) {
            setLoaded(true);
            return 2;
        }
        CubridBroker broker = (CubridBroker) parent;
        String brokerName = broker.getLabel();
        brokerInfos = getBrokersTask.getResultModel();
        boolean isExist = false;
        List<BrokerInfo> brokerInfoList =
                (brokerInfos == null || brokerInfos.getBorkerInfoList() == null)
                        ? null
                        : brokerInfos.getBorkerInfoList().getBrokerInfoList();
        for (int i = 0; brokerInfoList != null && i < brokerInfoList.size(); i++) {
            BrokerInfo brokerInfo = brokerInfoList.get(i);
            if (brokerInfo.getName().equalsIgnoreCase(brokerName)) {
                broker.setRunning(
                        brokerInfo.getState() == null
                                ? false
                                : brokerInfo.getState().equalsIgnoreCase("ON"));
                isExist = true;
            }
        }
        return isExist ? 0 : 3;
    }
}
