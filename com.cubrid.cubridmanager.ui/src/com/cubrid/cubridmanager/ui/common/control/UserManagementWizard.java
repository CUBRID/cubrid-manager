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
package com.cubrid.cubridmanager.ui.common.control;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.model.StatusMonitorAuthType;
import com.cubrid.cubridmanager.core.common.task.AddCMUserTask;
import com.cubrid.cubridmanager.core.common.task.ChangeCMUserPasswordTask;
import com.cubrid.cubridmanager.core.common.task.DeleteCMUserTask;
import com.cubrid.cubridmanager.core.common.task.UpdateCMUserTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbCreateAuthType;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.common.Messages;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;

/**
 * CUBRID Manager user management wizard
 *
 * @author pangqiren
 * @version 1.0 - 2009-4-23 created by pangqiren
 */
public class UserManagementWizard extends Wizard {

    private UserAuthGeneralInfoPage generalInfoPage = null;
    private UserAuthDbInfoPage authDbInfoPage = null;
    private final CubridServer server;
    private ServerUserInfo userInfo = null;
    private final List<ServerUserInfo> serverUserInfoList;

    /** The constructor */
    public UserManagementWizard(
            CubridServer server, ServerUserInfo userInfo, List<ServerUserInfo> serverUserInfoList) {
        if (userInfo == null) {
            setWindowTitle(Messages.titleAddUser);
        } else {
            setWindowTitle(Messages.titleEditUser);
        }
        this.userInfo = userInfo;
        this.server = server;
        this.serverUserInfoList = serverUserInfoList;
    }

    /** Add wizard pages */
    public void addPages() {
        generalInfoPage = new UserAuthGeneralInfoPage(server, userInfo, serverUserInfoList);
        addPage(generalInfoPage);
        ServerType serverType = server.getServerInfo().getServerType();
        if (serverType == ServerType.BROKER) {
            return;
        }
        if (userInfo == null || !userInfo.isAdmin()) {
            authDbInfoPage = new UserAuthDbInfoPage(server, userInfo);
            addPage(authDbInfoPage);
        }
    }

    /**
     * Return whether can finish
     *
     * @return <code>true</code> if it can be finished;<code>false</code> otherwise
     */
    public boolean canFinish() {
        ServerType serverType = server.getServerInfo().getServerType();
        if ((userInfo != null && userInfo.isAdmin()) || serverType == ServerType.BROKER) {
            return getContainer().getCurrentPage() == generalInfoPage;
        }
        return getContainer().getCurrentPage() == authDbInfoPage && authDbInfoPage.isPageComplete();
    }

    /**
     * Called when user clicks Finish
     *
     * @return boolean
     */
    public boolean performFinish() {

        final String userId = generalInfoPage.getUserId();
        final String password = generalInfoPage.getPassword();
        final String dbCreationAuth = generalInfoPage.getDbCreationAuth();
        final String brokerAuth = generalInfoPage.getBrokerAuth();
        final String statusMonitorAuth = generalInfoPage.getStatusMonitorAuth();
        final List<DatabaseInfo> authDatabaselist = new ArrayList<DatabaseInfo>();
        final Shell shell = getShell();
        final String taskName = Messages.bind(Messages.updateUserTaskName, userId);
        TaskExecutor taskExcutor =
                new TaskExecutor() {
                    public boolean exec(final IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            return false;
                        }
                        monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
                        for (ITask task : taskList) {
                            task.execute();
                            final String msg = task.getErrorMsg();
                            if (openErrorBox(shell, msg, monitor)) {
                                return false;
                            }
                            if (monitor.isCanceled()) {
                                return false;
                            }
                        }
                        ServerUserInfo loginedUserInfo =
                                server.getServerInfo().getLoginedUserInfo();
                        if (userInfo == null) {
                            userInfo = new ServerUserInfo(userId, password);
                        } else {
                            userInfo.setPassword(password);
                        }
                        if (userInfo.getUserName().equals(loginedUserInfo.getUserName())) {
                            loginedUserInfo.setPassword(password);
                        }
                        if (!userInfo.isAdmin()) {
                            if (dbCreationAuth.equals(DbCreateAuthType.AUTH_NONE.getText())) {
                                userInfo.setDbCreateAuthType(DbCreateAuthType.AUTH_NONE);
                            } else if (dbCreationAuth.equals(
                                    DbCreateAuthType.AUTH_ADMIN.getText())) {
                                userInfo.setDbCreateAuthType(DbCreateAuthType.AUTH_ADMIN);
                            }
                            if (brokerAuth.equals(CasAuthType.AUTH_NONE.getText())) {
                                userInfo.setCasAuth(CasAuthType.AUTH_NONE);
                            } else if (brokerAuth.equals(CasAuthType.AUTH_ADMIN.getText())) {
                                userInfo.setCasAuth(CasAuthType.AUTH_ADMIN);
                            } else if (brokerAuth.equals(CasAuthType.AUTH_MONITOR.getText())) {
                                userInfo.setCasAuth(CasAuthType.AUTH_MONITOR);
                            }
                            if (statusMonitorAuth.equals(
                                    StatusMonitorAuthType.AUTH_NONE.getText())) {
                                userInfo.setStatusMonitorAuth(StatusMonitorAuthType.AUTH_NONE);
                            } else if (statusMonitorAuth.equals(
                                    StatusMonitorAuthType.AUTH_ADMIN.getText())) {
                                userInfo.setStatusMonitorAuth(StatusMonitorAuthType.AUTH_ADMIN);
                            } else if (statusMonitorAuth.equals(
                                    StatusMonitorAuthType.AUTH_MONITOR.getText())) {
                                userInfo.setStatusMonitorAuth(StatusMonitorAuthType.AUTH_MONITOR);
                            }
                            userInfo.removeAllDatabaseInfo();
                            userInfo.setDatabaseInfoList(authDatabaselist);
                        }
                        return true;
                    }
                };

        if (userInfo != null && userInfo.isAdmin()) {
            ChangeCMUserPasswordTask changeCMUserPasswordTask =
                    new ChangeCMUserPasswordTask(server.getServerInfo());
            changeCMUserPasswordTask.setUserName(userId);
            changeCMUserPasswordTask.setPassword(password);
            taskExcutor.addTask(changeCMUserPasswordTask);
        } else if (userInfo != null && !userInfo.isAdmin()) {
            DeleteCMUserTask deleteCMUserTask = new DeleteCMUserTask(server.getServerInfo());
            deleteCMUserTask.setUserId(userId);
            taskExcutor.addTask(deleteCMUserTask);
        }

        if (userInfo == null || !userInfo.isAdmin()) {
            AddCMUserTask addCMUserTask = new AddCMUserTask(server.getServerInfo());
            addCMUserTask.setUserId(userId);
            addCMUserTask.setPassword(password);
            addCMUserTask.setDbcreate(dbCreationAuth);
            addCMUserTask.setCasAuth(brokerAuth);
            addCMUserTask.setStautsMonitorAuth(statusMonitorAuth);
            taskExcutor.addTask(addCMUserTask);
        }
        ServerType serverType = server.getServerInfo().getServerType();
        if ((userInfo == null || !userInfo.isAdmin())
                && (serverType == ServerType.BOTH || serverType == ServerType.DATABASE)) {
            List<Map<String, Object>> dbAuthInfoList = authDbInfoPage.getDbAuthInfoList();
            List<String> dbNameList = new ArrayList<String>();
            List<String> dbUserList = new ArrayList<String>();
            List<String> dbPasswordList = new ArrayList<String>();
            List<String> dbBrokerPortList = new ArrayList<String>();
            if (dbAuthInfoList != null && !dbAuthInfoList.isEmpty()) {
                int size = dbAuthInfoList.size();
                for (int i = 0; i < size; i++) {
                    Map<String, Object> map = dbAuthInfoList.get(i);
                    String allowConnectedStr = (String) map.get("1");
                    if ("Yes".equals(allowConnectedStr)) {
                        String dbName = (String) map.get("0");
                        dbNameList.add(dbName);
                        String dbUser = (String) map.get("2");
                        dbUserList.add(dbUser);
                        String brokerIP = (String) map.get("3");
                        String brokerPort = (String) map.get("4");
                        String port = "";
                        if (brokerPort.matches("^\\d+$")) {
                            port = brokerPort;
                        } else {
                            port =
                                    brokerPort.substring(
                                            brokerPort.indexOf("[") + 1, brokerPort.indexOf("/"));
                        }
                        dbBrokerPortList.add(brokerIP + "," + port);
                        dbPasswordList.add("");
                        DatabaseInfo databaseInfo =
                                new DatabaseInfo(dbName, server.getServerInfo());
                        databaseInfo.setBrokerPort(brokerPort);
                        databaseInfo.setBrokerIP(brokerIP);
                        DbUserInfo dbUserInfo = new DbUserInfo();
                        dbUserInfo.setName(dbUser);
                        databaseInfo.setAuthLoginedDbUserInfo(dbUserInfo);
                        authDatabaselist.add(databaseInfo);
                    }
                }
            }
            String[] dbNameArr = new String[dbNameList.size()];
            String[] dbUserArr = new String[dbUserList.size()];
            String[] dbPasswordArr = new String[dbPasswordList.size()];
            String[] dbBrokerPortArr = new String[dbBrokerPortList.size()];
            UpdateCMUserTask updateTask = new UpdateCMUserTask(server.getServerInfo());
            updateTask.setCmUserName(userId);
            updateTask.setDbAuth(
                    dbNameList.toArray(dbNameArr),
                    dbUserList.toArray(dbUserArr),
                    dbPasswordList.toArray(dbPasswordArr),
                    dbBrokerPortList.toArray(dbBrokerPortArr));
            updateTask.setCasAuth(brokerAuth);
            updateTask.setDbCreator(dbCreationAuth);
            updateTask.setStatusMonitorAuth(statusMonitorAuth);
            taskExcutor.addTask(updateTask);
        }
        new ExecTaskWithProgress(taskExcutor).exec(true, true);
        return taskExcutor.isSuccess();
    }

    /**
     * Get server user information
     *
     * @return the ServerUserInfo
     */
    public ServerUserInfo getServerUserInfo() {
        return this.userInfo;
    }
}
