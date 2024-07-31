/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.task.GetHAConfParameterTask;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.task.GetHeartbeatNodeInfoTask;
import com.cubrid.cubridmanager.ui.broker.editor.BrokerEnvStatusView;
import com.cubrid.cubridmanager.ui.host.editor.HostDashboardEditor;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridBrokerFolder;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.loader.logs.CubridLogsFolderLoader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

/**
 * This loader is responsible to load the children of CUBRID Server,these children include
 * Databases,Brokers,Monitoring(Status monitor,Broker monitor),Logs folder.
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridServerLoader extends CubridNodeLoader {
    private static final Logger LOGGER = LogUtil.getLogger(CubridServerLoader.class);

    private static final String DATABASE_FOLDER_NAME = Messages.msgDatabaseFolderName;
    private static final String BROKER_FOLDER_NAME = Messages.msgBrokersFolderName;
    //	private static final String MONITORING_FOLDER_NAME = Messages.msgMonitorFolderName;
    private static final String LOGS_FOLDER_NAME = Messages.msgLogsFolderName;

    public static final String DATABASE_FOLDER_ID = "Databases";
    public static final String BROKER_FOLDER_ID = "Brokers";
    public static final String MONITORING_FOLDER_ID = "Monitors";
    public static final String LOGS_FOLDER_ID = "Logs";

    public static final boolean USE_SHARD = false;

    /**
     * Load children object for parent
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor object
     */
    public void load(ICubridNode parent, IProgressMonitor monitor) {
        synchronized (this) {
            if (isLoaded()) {
                return;
            }
            parent.setEditorId(HostDashboardEditor.ID);
            CubridServer server = parent.getServer();
            if (!server.isConnected()) {
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }
            ServerType serverType = server.getServerInfo().getServerType();
            // add database folder
            if (serverType == ServerType.BOTH || serverType == ServerType.DATABASE) {
                String databaseFolderId = parent.getId() + NODE_SEPARATOR + DATABASE_FOLDER_ID;
                ICubridNode databaseFolder = parent.getChild(databaseFolderId);
                if (databaseFolder == null) {
                    databaseFolder =
                            new DefaultCubridNode(
                                    databaseFolderId,
                                    DATABASE_FOLDER_NAME,
                                    "icons/navigator/database_group.png");
                    databaseFolder.setType(NodeType.DATABASE_FOLDER);
                    databaseFolder.setContainer(true);
                    ICubridNodeLoader loader = new CubridDatabasesFolderLoader();
                    loader.setLevel(getLevel());
                    databaseFolder.setLoader(loader);
                    parent.addChild(databaseFolder);
                    if (getLevel() == DEFINITE_LEVEL) {
                        databaseFolder.getChildren(monitor);
                    }
                } else {
                    if (databaseFolder.getLoader() != null
                            && databaseFolder.getLoader().isLoaded()) {
                        databaseFolder.getLoader().setLoaded(false);
                        databaseFolder.getChildren(monitor);
                    }
                }
            }
            ServerUserInfo userInfo = parent.getServer().getServerInfo().getLoginedUserInfo();
            // add broker folder
            if ((serverType == ServerType.BOTH || serverType == ServerType.BROKER)
                    && userInfo != null
                    && (CasAuthType.AUTH_ADMIN == userInfo.getCasAuth()
                            || CasAuthType.AUTH_MONITOR == userInfo.getCasAuth())) {
                String brokerFolderId = parent.getId() + NODE_SEPARATOR + BROKER_FOLDER_ID;
                ICubridNode brokerFolder = parent.getChild(brokerFolderId);
                if (brokerFolder == null) {
                    brokerFolder =
                            new CubridBrokerFolder(
                                    brokerFolderId,
                                    BROKER_FOLDER_NAME,
                                    "icons/navigator/broker_group.png");
                    ((CubridBrokerFolder) brokerFolder)
                            .setStartedIconPath("icons/navigator/broker_service_started.png");
                    brokerFolder.setContainer(true);
                    brokerFolder.setViewId(BrokerEnvStatusView.ID);
                    ICubridNodeLoader loader = new CubridBrokersFolderLoader();
                    loader.setLevel(getLevel());
                    brokerFolder.setLoader(loader);
                    parent.addChild(brokerFolder);
                    if (getLevel() == DEFINITE_LEVEL) {
                        brokerFolder.getChildren(monitor);
                    }
                } else {
                    if (brokerFolder.getLoader() != null && brokerFolder.getLoader().isLoaded()) {
                        brokerFolder.getLoader().setLoaded(false);
                        brokerFolder.getChildren(monitor);
                    }
                }
            }

            //			#2 Remove the legacy monitoring menu
            //			// add monitor folder
            //			if (userInfo != null
            //					&& (StatusMonitorAuthType.AUTH_ADMIN == userInfo.getStatusMonitorAuth() ||
            // StatusMonitorAuthType.AUTH_MONITOR == userInfo.getStatusMonitorAuth())) {
            //				String monitroingId = parent.getId() + NODE_SEPARATOR
            //						+ MONITORING_FOLDER_ID;
            //				ICubridNode monitoringFolder = parent.getChild(monitroingId);
            //				if (monitoringFolder == null) {
            //					monitoringFolder = new DefaultCubridNode(monitroingId,
            //							MONITORING_FOLDER_NAME,
            //							"icons/navigator/status_group.png");
            //					monitoringFolder.setType(CubridNodeType.MONITOR_FOLDER);
            //					monitoringFolder.setContainer(true);
            //					ICubridNodeLoader loader = new CubridMonitorFolderLoader();
            //					loader.setLevel(getLevel());
            //					monitoringFolder.setLoader(loader);
            //					parent.addChild(monitoringFolder);
            //					if (getLevel() == DEFINITE_LEVEL) {
            //						monitoringFolder.getChildren(monitor);
            //					}
            //				} else {
            //					if (monitoringFolder.getLoader() != null
            //							&& monitoringFolder.getLoader().isLoaded()) {
            //						monitoringFolder.getLoader().setLoaded(false);
            //						monitoringFolder.getChildren(monitor);
            //					}
            //				}
            //			}

            // add logs folder
            String logsFolderId = parent.getId() + NODE_SEPARATOR + LOGS_FOLDER_ID;
            ICubridNode logsFolder = parent.getChild(logsFolderId);
            if (logsFolder == null) {
                logsFolder =
                        new DefaultCubridNode(
                                logsFolderId,
                                LOGS_FOLDER_NAME,
                                "icons/navigator/log_group_big.png");
                logsFolder.setType(CubridNodeType.LOGS_FOLDER);
                logsFolder.setContainer(true);
                ICubridNodeLoader loader = new CubridLogsFolderLoader();
                loader.setLevel(getLevel());
                logsFolder.setLoader(loader);
                parent.addChild(logsFolder);
                if (getLevel() == DEFINITE_LEVEL) {
                    logsFolder.getChildren(monitor);
                }
            } else {
                if (logsFolder.getLoader() != null && logsFolder.getLoader().isLoaded()) {
                    logsFolder.getLoader().setLoaded(false);
                    logsFolder.getChildren(monitor);
                }
            }

            if (server.isConnected() && CompatibleUtil.isSupportHA(server.getServerInfo())) {
                getHostStatus(server);
            }
            setLoaded(true);

            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Get host status
     *
     * @param server - CubridServer
     */
    private void getHostStatus(final CubridServer server) {
        final GetHeartbeatNodeInfoTask getHeartbeatNodeInfoTask =
                new GetHeartbeatNodeInfoTask(server.getServerInfo());

        getHeartbeatNodeInfoTask.setAllDb(true);
        getHeartbeatNodeInfoTask.execute();

        if (getHeartbeatNodeInfoTask.isSuccess()) {
            HAHostStatusInfo haHostStatusInfo =
                    getHeartbeatNodeInfoTask.getHostStatusInfo(
                            server.getServerInfo().getHostAddress());
            if (haHostStatusInfo != null) {
                server.getServerInfo().setHaHostStatusInfo(haHostStatusInfo);
            }
        } else {
            LOGGER.debug("Get host status error:" + getHeartbeatNodeInfoTask.getErrorMsg());
        }

        final GetHAConfParameterTask getHAConfParameterTask =
                new GetHAConfParameterTask(server.getServerInfo());
        getHAConfParameterTask.execute();
        if (getHAConfParameterTask.isSuccess()) {
            server.getServerInfo().setHaConfParaMap(getHAConfParameterTask.getConfParameters());
        } else {
            LOGGER.debug("Get host ha status error:" + getHAConfParameterTask.getErrorMsg());
        }
    }
}
