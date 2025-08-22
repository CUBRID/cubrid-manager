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
package com.cubrid.cubridmanager.ui.common.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.broker.task.StartBrokerEnvTask;
import com.cubrid.cubridmanager.core.broker.task.StartBrokerTask;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.common.task.CommonTaskName;
import com.cubrid.cubridmanager.core.common.task.CommonUpdateTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.ui.common.Messages;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to start service
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-6 created by pangqiren
 */
public class StartServiceAction extends SelectionAction {

    public static final String ID = StartServiceAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public StartServiceAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public StartServiceAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, enabledIcon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    /**
     * Return whether this action support to select multi object,if not support,this action will be
     * disabled
     *
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return <code>true</code> if support this object;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            CubridServer server = node.getServer();
            if (server == null) {
                return false;
            }
            ServerInfo serverInfo = server.getServerInfo();
            ServerType serverType = serverInfo == null ? null : serverInfo.getServerType();
            if (serverType == null || serverType == ServerType.BROKER) {
                return false;
            }
            if (server != null
                    && server.isConnected()
                    && serverInfo != null
                    && serverInfo.getLoginedUserInfo() != null
                    && serverInfo.getLoginedUserInfo().isAdmin()) {
                return true;
            }
        }
        return false;
    }

    /** Open start service dialog and set configuration information */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (obj == null || obj.length <= 0 || !isSupported(obj[0])) {
            setEnabled(false);
            return;
        }

        CubridServer[] servers = handleSelectionObj(obj);

        doRun(servers);
    }

    /**
     * Perform do run
     *
     * @param servers
     */
    public void doRun(CubridServer[] servers) {
        CubridNavigatorView view = CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
        final TreeViewer viewer = view.getViewer();

        if (servers.length > 0) {
            CubridServer server = servers[0];
            if (isSupported(server)) {
                final JobFamily jobFamily = new JobFamily();
                final String serverName = server.getName();
                String dbName = JobFamily.ALL_DB;
                jobFamily.setServerName(serverName);
                jobFamily.setDbName(dbName);
                Job[] jobs = Job.getJobManager().find(jobFamily);
                if (jobs.length > 0) {
                    CommonUITool.openWarningBox(
                            Messages.bind(Messages.msgStartServiceWithJob, serverName));
                    return;
                }

                TaskExecutor taskExcutor = new StartServiceExecutor(server, getShell(), viewer);
                ServerInfo serverInfo = server.getServerInfo();
                GetCubridConfParameterTask task = new GetCubridConfParameterTask(serverInfo);
                taskExcutor.addTask(task);
                new ExecTaskWithProgress(taskExcutor).exec();
            }
        }

        //		for(CubridServer server : servers) {
        //			final JobFamily jobFamily = new JobFamily();
        //			final String serverName = server.getName();
        //			String dbName = JobFamily.ALL_DB;
        //			jobFamily.setServerName(serverName);
        //			jobFamily.setDbName(dbName);
        //			Job[] jobs = Job.getJobManager().find(jobFamily);
        //			if (jobs.length > 0) {
        //				CommonUITool.openWarningBox(Messages.bind(
        //						Messages.msgStartServiceWithJob, serverName));
        //				return;
        //			}
        //
        //			TaskExecutor taskExcutor = new StartServiceExecutor(server, getShell(),
        //					viewer);
        //			ServerInfo serverInfo = server.getServerInfo();
        //			GetCubridConfParameterTask task = new GetCubridConfParameterTask(
        //					serverInfo);
        //			taskExcutor.addTask(task);
        //			new ExecTaskWithProgress(taskExcutor).exec();
        //		}
    }

    /**
     * Get all selected servers
     *
     * @param objs
     * @return
     */
    private CubridServer[] handleSelectionObj(Object[] objs) {
        Set<CubridServer> list = new LinkedHashSet<CubridServer>();
        for (Object obj : objs) {
            if (obj instanceof CubridServer && isSupported(obj) && !list.contains(obj)) {
                list.add((CubridServer) obj);
            } else if (obj instanceof CubridGroupNode) {
                CubridGroupNode node = (CubridGroupNode) obj;
                for (ICubridNode childNode : node.getChildren()) {
                    if (childNode instanceof CubridServer
                            && isSupported(childNode)
                            && !list.contains(childNode)) {
                        list.add((CubridServer) childNode);
                    }
                }
            }
        }

        return list.toArray(new CubridServer[0]);
    }
    /**
     * Start service executor
     *
     * @author pangqiren
     * @version 1.0 - 2010-1-6 created by pangqiren
     */
    static class StartServiceExecutor extends TaskExecutor {
        private final CubridServer server;
        private final TreeViewer viewer;
        private final Shell shell;

        public StartServiceExecutor(CubridServer server, Shell shell, TreeViewer viewer) {
            this.server = server;
            this.shell = shell;
            this.viewer = viewer;
        }

        /**
         * Execute to start service
         *
         * @param monitor the IProgressMonitor
         * @return <code>true</code> if successful;<code>false</code> otherwise
         */
        public boolean exec(final IProgressMonitor monitor) {
            if (monitor.isCanceled()) {
                return false;
            }
            monitor.beginTask(
                    Messages.bind(Messages.msgStartServiceInHost, server.getName()),
                    IProgressMonitor.UNKNOWN);
            List<ITask> otherTaskList = new ArrayList<ITask>();
            for (ITask task : taskList) {
                task.execute();
                if (openErrorBox(shell, task.getErrorMsg(), monitor) || monitor.isCanceled()) {
                    return false;
                }
                openWarningBox(shell, task.getWarningMsg(), monitor);
                if (task instanceof GetCubridConfParameterTask) {
                    GetCubridConfParameterTask getCubridConfParameterTask =
                            (GetCubridConfParameterTask) task;
                    Map<String, Map<String, String>> confParas =
                            getCubridConfParameterTask.getConfParameters();
                    String services = "";
                    String servers = "";
                    Map<String, String> map = confParas.get(ConfConstants.SERVICE_SECTION_NAME);
                    if (map != null) {
                        services = map.get(ConfConstants.COMMON_SERVICE);
                        servers = map.get(ConfConstants.COMMON_SERVER);
                    }
                    if (services == null) {
                        continue;
                    }
                    if (services.indexOf("server") >= 0
                            && servers != null
                            && servers.length() > 0) {
                        String[] databases = servers.split(",");
                        for (int i = 0; i < databases.length; i++) {
                            CommonUpdateTask commonTask =
                                    new CommonUpdateTask(
                                            CommonTaskName.START_DB_TASK_NAME,
                                            server.getServerInfo(),
                                            CommonSendMsg.getCommonDatabaseSendMsg());
                            commonTask.setDbName(databases[i]);
                            otherTaskList.add(commonTask);
                        }
                    }
                    BrokerInfos brokerInfos = server.getServerInfo().getBrokerInfos();
                    if (services.indexOf("broker") >= 0
                            && brokerInfos != null
                            && OnOffType.ON
                                    .getText()
                                    .equalsIgnoreCase(brokerInfos.getBrokerstatus())
                            && brokerInfos.getBorkerInfoList() != null) {
                        List<BrokerInfo> list = brokerInfos.getBorkerInfoList().getBrokerInfoList();
                        for (int i = 0; list != null && i < list.size(); i++) {
                            BrokerInfo brokerInfo = list.get(i);
                            if (OnOffType.OFF.getText().equalsIgnoreCase(brokerInfo.getState())) {
                                StartBrokerTask startBrokerTask =
                                        new StartBrokerTask(server.getServerInfo());
                                startBrokerTask.setBrokerName(brokerInfo.getName());
                                otherTaskList.add(startBrokerTask);
                            }
                        }
                    } else if (services.indexOf("broker") >= 0
                            && brokerInfos != null
                            && OnOffType.OFF
                                    .getText()
                                    .equalsIgnoreCase(brokerInfos.getBrokerstatus())) {
                        StartBrokerEnvTask startBrokerEnvTask =
                                new StartBrokerEnvTask(server.getServerInfo());
                        otherTaskList.add(startBrokerEnvTask);
                    }
                }
            }
            for (ITask task : otherTaskList) {
                addTask(task);
                task.execute();
                if (openErrorBox(shell, task.getErrorMsg(), monitor) || monitor.isCanceled()) {
                    return false;
                }
            }
            if (!otherTaskList.isEmpty()) {
                refreshViewer();
            }
            return true;
        }

        /** Refresh navigator */
        private void refreshViewer() {
            Display display = Display.getDefault();
            display.syncExec(
                    new Runnable() {
                        public void run() {
                            CommonUITool.refreshNavigatorTree(viewer, server);
                        }
                    });
        }
    }
}
