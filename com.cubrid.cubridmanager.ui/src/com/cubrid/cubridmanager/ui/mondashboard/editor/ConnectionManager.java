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
package com.cubrid.cubridmanager.ui.mondashboard.editor;

import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJob;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Connection Manager is responsible to connect host in monitoring dash board
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-17 created by pangqiren
 */
public final class ConnectionManager {

    private ConnectionManager() {}

    /**
     * Connect all hosts in dash board
     *
     * @param dashboard The Dashboard
     */
    public static void connectAll(Dashboard dashboard) {
        if (dashboard == null) {
            return;
        }
        List<HostNode> hostNodList = dashboard.getHostNodeList();
        for (int i = 0; i < hostNodList.size(); i++) {
            HostNode hostNode = hostNodList.get(i);
            if (hostNode.isConnecting()) {
                continue;
            }
            connectHostInJob(hostNode, true, false);
        }
    }

    /**
     * Connect Host
     *
     * @param hostNode The HostNode
     * @param isConnectChild The boolean
     * @param isPing The boolean
     */
    public static void connectHostInJob(HostNode hostNode, boolean isConnectChild, boolean isPing) {

        if (hostNode.isConnected() || hostNode.isConnecting()) {
            return;
        }

        ServerInfo serverInfo = hostNode.getServerInfo();
        if (serverInfo == null) {
            serverInfo = new ServerInfo();
            serverInfo.setServerName(hostNode.getIp());
            serverInfo.setHostAddress(hostNode.getIp());
            serverInfo.setHostMonPort(Integer.parseInt(hostNode.getPort()));
            serverInfo.setHostJSPort(Integer.parseInt(hostNode.getPort()) + 1);
            serverInfo.setUserName(hostNode.getUserName());
            serverInfo.setUserPassword(hostNode.getPassword());
            serverInfo.setJdbcDriverVersion(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
        }

        TaskJob job =
                new TaskJob(Messages.bind(Messages.jobConnectHost, serverInfo.getHostAddress()));

        hostNode.setConnecting(true);
        ConnectHostJobExecutor executor = new ConnectHostJobExecutor(hostNode, serverInfo, isPing);
        job.addTaskJobExecutor(executor);

        if (isConnectChild) {
            List<DatabaseNode> dbNodeList = hostNode.getDbNodeList();
            for (DatabaseNode dbNode : dbNodeList) {
                if (dbNode.isConnecting() || dbNode.isConnected()) {
                    continue;
                }
                dbNode.setConnecting(true);
                ConnectDatabaseNodeJobExecutor dbJobExecutor =
                        new ConnectDatabaseNodeJobExecutor(dbNode, serverInfo);
                job.addTaskJobExecutor(dbJobExecutor);
            }
        }

        JobFamily jobFamily = new JobFamily();
        jobFamily.setServerName(serverInfo.getServerName());
        jobFamily.setDbName(JobFamily.ALL_DB);
        job.setJobFamily(jobFamily);
        job.setPriority(Job.SHORT);
        job.setUser(false);
        job.schedule();
    }

    /**
     * Connect database
     *
     * @param dbNode The DatabaseNode
     */
    public static void connectDatabaseInJob(DatabaseNode dbNode) {
        HostNode hostNode = dbNode.getParent();
        if (hostNode.isConnecting() || dbNode.isConnecting() || dbNode.isConnected()) {
            return;
        }
        ServerInfo serverInfo = hostNode.getServerInfo();
        if (serverInfo == null) {
            serverInfo = new ServerInfo();
            serverInfo.setServerName(hostNode.getIp());
            serverInfo.setHostAddress(hostNode.getIp());
            serverInfo.setHostMonPort(Integer.parseInt(hostNode.getPort()));
            serverInfo.setHostJSPort(Integer.parseInt(hostNode.getPort()) + 1);
            serverInfo.setUserName(hostNode.getUserName());
            serverInfo.setUserPassword(hostNode.getPassword());
            serverInfo.setJdbcDriverVersion(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
        }

        TaskJob job = new TaskJob(Messages.bind(Messages.jobConnectDatabase, dbNode.getDbName()));
        if (!hostNode.isConnected()) {
            hostNode.setConnecting(true);
            ConnectHostJobExecutor connectHostJobExecutor =
                    new ConnectHostJobExecutor(hostNode, serverInfo, false);
            job.addTaskJobExecutor(connectHostJobExecutor);
        }

        dbNode.setConnecting(true);
        ConnectDatabaseNodeJobExecutor dbJobExecutor =
                new ConnectDatabaseNodeJobExecutor(dbNode, serverInfo);
        job.addTaskJobExecutor(dbJobExecutor);

        JobFamily jobFamily = new JobFamily();
        jobFamily.setServerName(serverInfo.getServerName());
        jobFamily.setDbName(dbNode.getDbName());
        job.setJobFamily(jobFamily);
        job.setPriority(Job.SHORT);
        job.setUser(false);
        job.schedule();
    }

    /**
     * Connect Host
     *
     * @param hostNode The HostNode
     * @param isConnectChild The boolean
     * @param isPing The boolean
     */
    public static void connectHost(HostNode hostNode, boolean isConnectChild, boolean isPing) {

        if (hostNode.isConnected() || hostNode.isConnecting()) {
            return;
        }

        ServerInfo serverInfo = hostNode.getServerInfo();
        if (serverInfo == null) {
            serverInfo = new ServerInfo();
            serverInfo.setServerName(hostNode.getIp());
            serverInfo.setHostAddress(hostNode.getIp());
            serverInfo.setHostMonPort(Integer.parseInt(hostNode.getPort()));
            serverInfo.setHostJSPort(Integer.parseInt(hostNode.getPort()) + 1);
            serverInfo.setUserName(hostNode.getUserName());
            serverInfo.setUserPassword(hostNode.getPassword());
            serverInfo.setJdbcDriverVersion(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
        }

        hostNode.setConnecting(true);
        ConnectHostJobExecutor executor = new ConnectHostJobExecutor(hostNode, serverInfo, isPing);
        IStatus status = executor.exec(new NullProgressMonitor());
        executor.finish(status);

        if (isConnectChild && hostNode.isConnected()) {
            List<DatabaseNode> dbNodeList = hostNode.getDbNodeList();
            for (DatabaseNode dbNode : dbNodeList) {
                if (dbNode.isConnecting() || dbNode.isConnected()) {
                    continue;
                }
                dbNode.setConnecting(true);
                ConnectDatabaseNodeJobExecutor dbJobExecutor =
                        new ConnectDatabaseNodeJobExecutor(dbNode, serverInfo);
                status = dbJobExecutor.exec(new NullProgressMonitor());
                dbJobExecutor.finish(status);
            }
        }
    }
}
