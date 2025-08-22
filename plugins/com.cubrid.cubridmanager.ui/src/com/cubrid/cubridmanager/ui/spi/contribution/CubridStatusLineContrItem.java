/*
 * Copyright (C) 2010 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.spi.contribution;

import com.cubrid.common.ui.common.action.RestoreQueryEditorAction;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.contribution.StatusLineContrItem;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.ApplicationPersistUtil;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.common.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.List;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * CUBRID Manager status line contribution item,it show server info(serverName:serverUser) and
 * database info(dbName:dbUser) and object number information
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-21 created by pangqiren
 */
@SuppressWarnings("restriction")
public class CubridStatusLineContrItem extends StatusLineContrItem {
    private static final String SERVER_INFO_CONTR_ID = "MANAGER_SERVER_INFO_CONTR_ID";
    private static final String DB_INFO_CONTR_ID = "MANAGER_DB_INFO_CONTR_ID";
    private static final String OBJ_NUM_INFO_CONTR_ID = "MANAGER_OBJ_NUM_INFO_CONTR_ID";
    private static final String RESTORE_QUERY_EDITORS_CONTR_ID =
            "MANAGER_RESTORE_QUERY_EDITORS_CONTR_ID";
    private static final String UPDATE_APP_CONTR_ID = "MANAGER_UPDATE_APP_CONTR_ID";

    /**
     * Update the status line information
     *
     * @param statusLineManager StatusLineManager
     * @param cubridNode The selected ICubridNode object
     */
    protected void updateStatusLine(StatusLineManager statusLineManager, ICubridNode cubridNode) {

        clearStatusLine();

        updateStatusLineForRestoreQueryEditor();

        if (cubridNode == null
                || cubridNode.getServer() == null
                || cubridNode.getServer().getServerInfo() == null) {
            return;
        }
        String serverInfoStr = cubridNode.getServer().getLabel();
        if (!DatabaseNavigatorMenu.SELF_DATABASE_ID.equals(cubridNode.getId())) {
            ServerUserInfo userInfo = cubridNode.getServer().getServerInfo().getLoginedUserInfo();
            if (userInfo != null
                    && userInfo.getUserName() != null
                    && userInfo.getUserName().trim().length() > 0) {
                serverInfoStr = userInfo.getUserName() + "@" + serverInfoStr;
            }
            String monPort = cubridNode.getServer().getMonPort();
            if (monPort != null && monPort.trim().length() > 0) {
                serverInfoStr = serverInfoStr + ":" + monPort;
            }
        }
        StringBuffer dbInfoStrBuffer = new StringBuffer();
        if (cubridNode instanceof ISchemaNode) {
            ISchemaNode schemaNode = (ISchemaNode) cubridNode;
            CubridDatabase database = schemaNode.getDatabase();
            dbInfoStrBuffer.append(database.getDatabaseInfo().getDbName());

            DbUserInfo dbUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            if (database.isLogined()
                    && dbUserInfo != null
                    && dbUserInfo.getName() != null
                    && dbUserInfo.getName().trim().length() > 0) {
                dbInfoStrBuffer =
                        new StringBuffer(dbUserInfo.getName() + "@" + dbInfoStrBuffer.toString());
            }
            String brokerPort = QueryOptions.getBrokerPort(database.getDatabaseInfo());
            BrokerInfos brokerInfos = database.getServer().getServerInfo().getBrokerInfos();
            List<BrokerInfo> brokerInfoList =
                    brokerInfos == null || brokerInfos.getBorkerInfoList() == null
                            ? null
                            : brokerInfos.getBorkerInfoList().getBrokerInfoList();
            boolean isExist = false;
            for (int i = 0; brokerInfoList != null && i < brokerInfoList.size(); i++) {
                BrokerInfo brokerInfo = brokerInfoList.get(i);
                if (brokerPort == null
                        || brokerInfo.getPort() == null
                        || brokerInfo.getPort().trim().length() == 0
                        || !brokerPort.equals(brokerInfo.getPort())) {
                    continue;
                }
                if (brokerPort.equals(brokerInfo.getPort())) {
                    isExist = true;
                    String status =
                            brokerInfo.getState() == null
                                            || brokerInfo.getState().trim().equalsIgnoreCase("OFF")
                                    ? "OFF"
                                    : "ON";
                    String text =
                            brokerInfo.getName() + "[" + brokerInfo.getPort() + "/" + status + "]";
                    dbInfoStrBuffer.append(":").append(text);
                    break;
                }
            }
            if (!isExist && brokerPort != null && brokerPort.trim().length() > 0) {
                dbInfoStrBuffer.append(":").append(brokerPort);
            }
            String charset = database.getDatabaseInfo().getCharSet();
            if (charset != null && charset.trim().length() > 0) {
                dbInfoStrBuffer.append(":charset=").append(charset);
            }
        }

        String numberStr = getChilderenNumStr(cubridNode);
        if (numberStr != null && numberStr.length() > 0) {
            StatusLineContributionItem item =
                    new StatusLineContributionItem(OBJ_NUM_INFO_CONTR_ID, numberStr.length() + 4);
            statusLineManager.add(item);
            item.setText(numberStr);
        }

        int addedWidth = 3;
        if (dbInfoStrBuffer == null || dbInfoStrBuffer.length() == 0) {
            addedWidth = 30;
        }
        StatusLineContributionItem item =
                new StatusLineContributionItem(
                        SERVER_INFO_CONTR_ID, serverInfoStr.length() + addedWidth);
        statusLineManager.add(item);
        item.setText(serverInfoStr);

        if (dbInfoStrBuffer != null && dbInfoStrBuffer.length() > 0) {
            item = new StatusLineContributionItem(DB_INFO_CONTR_ID, dbInfoStrBuffer.length() + 3);
            statusLineManager.add(item);
            item.setText(dbInfoStrBuffer.toString());
        }
    }

    private void updateStatusLineForRestoreQueryEditor() {
        final int countOfRestorableQueryEditors =
                ApplicationPersistUtil.getInstance().countOfRestorableQueryEditorsAtLastSession();
        if (countOfRestorableQueryEditors <= 0) {
            return;
        }

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        StatusLineManager statusLineManager = null;
        if (window instanceof WorkbenchWindow) {
            statusLineManager = ((WorkbenchWindow) window).getStatusLineManager();
        }

        if (statusLineManager == null) {
            return;
        }

        IContributionItem scaleItem =
                new ControlContribution(RESTORE_QUERY_EDITORS_CONTR_ID) {
                    protected Control createControl(Composite parent) {
                        Button btn = new Button(parent, SWT.None);
                        String buttonTitle =
                                Messages.bind(
                                        com.cubrid.common.ui.common.Messages
                                                .restoreQueryEditorTitle,
                                        countOfRestorableQueryEditors);
                        btn.setText(buttonTitle);
                        btn.addSelectionListener(
                                new SelectionAdapter() {
                                    public void widgetSelected(SelectionEvent e) {
                                        ActionManager manager = ActionManager.getInstance();
                                        IAction action =
                                                manager.getAction(RestoreQueryEditorAction.ID);
                                        if (action != null
                                                && action instanceof RestoreQueryEditorAction) {
                                            action.run();
                                        }
                                    }
                                });
                        return btn;
                    };
                };
        statusLineManager.add(scaleItem);
    }

    /**
     * Change status line for navigator selection
     *
     * @param selection the ISelection object
     */
    public void changeStuatusLineForNavigator(ISelection selection) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        WorkbenchWindow workbenchWindow = null;
        StatusLineManager statusLineManager = null;
        if (window instanceof WorkbenchWindow) {
            workbenchWindow = (WorkbenchWindow) window;
            statusLineManager = workbenchWindow.getStatusLineManager();
        }
        // workbenchWindow.setStatus("");
        clearStatusLine();

        updateStatusLineForRestoreQueryEditor();

        if (selection == null || selection.isEmpty()) {
            return;
        }
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (!(obj instanceof ICubridNode)) {
            return;
        }

        CubridNavigatorView navigatorView = CubridNavigatorView.findNavigationView();
        boolean isShowGroup = navigatorView == null ? false : navigatorView.savedIsShowGroup();
        ICubridNode cubridNode = (ICubridNode) obj;
        String nodePath = cubridNode.getLabel();
        ICubridNode parent = cubridNode.getParent();
        while (parent != null) {
            if (!isShowGroup && NodeType.GROUP.equals(parent.getType())) {
                break;
            }
            nodePath = parent.getLabel() + "/" + nodePath;
            parent = parent.getParent();
        }
        // workbenchWindow.setStatus(nodePath);
        CubridServer server = cubridNode.getServer();
        ServerInfo serverInfo = server == null ? null : server.getServerInfo();
        if (serverInfo == null || !serverInfo.isConnected()) {
            return;
        }

        if (statusLineManager != null) {
            updateStatusLine(statusLineManager, cubridNode);
        }
    }

    /** Clear the status line information of CUBRID Manager */
    public void clearStatusLine() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        if (window instanceof WorkbenchWindow) {
            StatusLineManager statusLineManager = ((WorkbenchWindow) window).getStatusLineManager();
            if (statusLineManager != null) {
                statusLineManager.remove(RESTORE_QUERY_EDITORS_CONTR_ID);
                statusLineManager.remove(UPDATE_APP_CONTR_ID);
                statusLineManager.remove(SERVER_INFO_CONTR_ID);
                statusLineManager.remove(DB_INFO_CONTR_ID);
                statusLineManager.remove(OBJ_NUM_INFO_CONTR_ID);
                statusLineManager.update(true);
            }
        }
    }

    /**
     * Get children number of the selected CUBRID node
     *
     * @param cubridNode ICubridNode
     * @return String
     */
    protected String getChilderenNumStr(ICubridNode cubridNode) {
        ICubridNode containerNode = cubridNode;
        if (!cubridNode.isContainer()) {
            containerNode = cubridNode.getParent();
        }
        if (containerNode == null) {
            return "";
        }
        String nodeType = containerNode.getType();
        boolean isLoaded =
                containerNode.getLoader() != null && containerNode.getLoader().isLoaded();
        if (CubridNodeType.BROKER_SQL_LOG_FOLDER.equals(nodeType)
                || CubridNodeType.LOGS_BROKER_ACCESS_LOG_FOLDER.equals(nodeType)
                || CubridNodeType.LOGS_BROKER_ERROR_LOG_FOLDER.equals(nodeType)) {
            isLoaded =
                    containerNode.getParent().getLoader() != null
                            && containerNode.getParent().getLoader().isLoaded();
        }

        if (!isLoaded) {
            return "";
        }
        int size = containerNode.getChildren() == null ? 0 : containerNode.getChildren().size();
        if (NodeType.DATABASE_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgDatabaseNum, size);
        } else if (CubridNodeType.BROKER_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgBrokerNum, size);
        } else if (CubridNodeType.USER_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgUserNum, size);
        } else if (CubridNodeType.BROKER_SQL_LOG_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgBrokerSqlLogNum, size);
        } else if (CubridNodeType.LOGS_BROKER_ACCESS_LOG_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgBrokerAccessLogNum, size);
        } else if (CubridNodeType.LOGS_BROKER_ERROR_LOG_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgBrokerErrorLogNum, size);
        } else if (CubridNodeType.LOGS_SERVER_DATABASE_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgDbServerLogNum, size);
        } else {
            return super.getChilderenNumStr(cubridNode);
        }
    }
}
