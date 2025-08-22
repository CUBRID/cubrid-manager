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
package com.cubrid.cubridmanager.ui.cubrid.database.dialog;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.common.task.UpdateCMUserTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.LoginDatabaseTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * Login database task executor
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-4 created by pangqiren
 */
public class LoginDatabaseTaskExecutor extends TaskExecutor {

    private static final Logger LOGGER = LogUtil.getLogger(LoginDatabaseTaskExecutor.class);

    private final Shell shell;
    private final String dbPassword;
    private final String dbName;
    private final ServerInfo serverInfo;
    private final boolean showErrMsg;
    private String errMsg = null;

    /**
     * The constructor
     *
     * @param shell
     * @param serverInfo
     * @param dbName
     * @param dbUser
     * @param dbPassword
     */
    public LoginDatabaseTaskExecutor(
            Shell shell,
            ServerInfo serverInfo,
            String dbName,
            String dbUser,
            String dbPassword,
            boolean showErrMsg) {
        this.shell = shell;
        this.dbName = dbName;
        this.serverInfo = serverInfo;
        this.dbPassword = dbPassword;
        this.showErrMsg = showErrMsg;
        LoginDatabaseTask loginDatabaseTask = new LoginDatabaseTask(serverInfo);
        loginDatabaseTask.setCMUser(serverInfo.getUserName());
        loginDatabaseTask.setDbName(dbName);
        loginDatabaseTask.setDbUser(dbUser);
        loginDatabaseTask.setDbPassword(dbPassword);

        UpdateCMUserTask updateCMUserTask = new UpdateCMUserTask(serverInfo);
        updateCMUserTask.setCmUserName(serverInfo.getUserName());

        addTask(loginDatabaseTask);
        addTask(updateCMUserTask);
    }

    /**
     * Execute to login database
     *
     * @param monitor the IProgressMonitor
     * @return <code>true</code> if successful;<code>false</code>otherwise;
     */
    public boolean exec(final IProgressMonitor monitor) {

        if (monitor.isCanceled()) {
            return false;
        }
        String taskName = Messages.bind(Messages.loginDbTaskName, dbName);
        monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
        DbUserInfo dbUserInfo = null;
        DatabaseInfo dbInfo = serverInfo.getLoginedUserInfo().getDatabaseInfo(dbName);
        DbUserInfo preDbUserInfo = dbInfo.getAuthLoginedDbUserInfo();
        boolean isOldLogined = dbInfo.isLogined();
        for (ITask task : taskList) {
            if (task instanceof UpdateCMUserTask) {
                UpdateCMUserTask updateCMUserTask = (UpdateCMUserTask) task;
                ServerInfo serverInfo = dbInfo.getServerInfo();
                if (serverInfo != null && serverInfo.isConnected()) {
                    ServerUserInfo userInfo = serverInfo.getLoginedUserInfo();
                    updateCMUserTask.setCasAuth(userInfo.getCasAuth().getText());
                    updateCMUserTask.setDbCreator(userInfo.getDbCreateAuthType().getText());
                    updateCMUserTask.setStatusMonitorAuth(
                            userInfo.getStatusMonitorAuth().getText());
                    List<String> dbNameList = new ArrayList<String>();
                    List<String> dbUserList = new ArrayList<String>();
                    List<String> dbPasswordList = new ArrayList<String>();
                    List<String> dbBrokerPortList = new ArrayList<String>();
                    List<DatabaseInfo> authDatabaseList = userInfo.getDatabaseInfoList();

                    for (int i = 0; authDatabaseList != null && i < authDatabaseList.size(); i++) {
                        DatabaseInfo databaseInfo = authDatabaseList.get(i);
                        dbNameList.add(databaseInfo.getDbName());
                        dbUserList.add(databaseInfo.getAuthLoginedDbUserInfo().getName());
                        dbBrokerPortList.add(
                                QueryOptions.getBrokerIp(databaseInfo)
                                        + ","
                                        + databaseInfo.getBrokerPort());
                        String password =
                                databaseInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
                        dbPasswordList.add(password == null ? "" : password);
                    }

                    String[] dbNameArr = new String[dbNameList.size()];
                    String[] dbUserArr = new String[dbUserList.size()];
                    String[] dbPasswordArr = new String[dbPasswordList.size()];
                    String[] dbBrokerPortArr = new String[dbBrokerPortList.size()];
                    updateCMUserTask.setDbAuth(
                            dbNameList.toArray(dbNameArr),
                            dbUserList.toArray(dbUserArr),
                            dbPasswordList.toArray(dbPasswordArr),
                            dbBrokerPortList.toArray(dbBrokerPortArr));
                }
            }
            task.execute();
            final String msg = task.getErrorMsg();
            if (showErrMsg) {
                if (openErrorBox(shell, msg, monitor)) {
                    dbInfo.setLogined(isOldLogined);
                    dbInfo.setAuthLoginedDbUserInfo(preDbUserInfo);
                    // TOOLS-2305 log when reproduce the bug
                    //					String infoMessage = "user : " + preDbUserInfo.getName() + " passwaord :
                    // " + dbPassword;
                    //					LOGGER.error("loggin failed task : " + infoMessage);
                    return false;
                }
            } else if (!monitor.isCanceled()) {
                setErrMsg(msg);
                dbInfo.setLogined(isOldLogined);
                dbInfo.setAuthLoginedDbUserInfo(preDbUserInfo);
                return false;
            }
            if (monitor.isCanceled()) {
                return false;
            }
            if (task instanceof LoginDatabaseTask) {
                dbUserInfo = ((LoginDatabaseTask) task).getLoginedDbUserInfo();
                dbInfo.setLogined(true);
                dbUserInfo.setNoEncryptPassword(dbPassword);
                dbInfo.setAuthLoginedDbUserInfo(dbUserInfo);
                dbInfo.setSupportUserSchema(CompatibleUtil.isAfter112(dbInfo));
            }
        }
        return true;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
