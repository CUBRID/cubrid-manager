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

package com.cubrid.common.ui.cubrid.user.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.common.persist.IPersisteManager;
import com.cubrid.common.ui.cubrid.user.Messages;
import com.cubrid.common.ui.cubrid.user.dialog.EditUserDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllPartitionClassTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;
import com.cubrid.cubridmanager.core.cubrid.user.task.GetUserAuthorizationsTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.GetUserListTask;
import java.sql.Connection;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * Edit the user
 *
 * @author robin 2009-3-18
 */
public class EditUserAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(EditUserAction.class);
    public static final String ID = EditUserAction.class.getName();
    private final IPersisteManager persisteManager;
    /**
     * The constructor
     *
     * @param shell
     * @param text
     */
    public EditUserAction(
            Shell shell, String text, ImageDescriptor icon, IPersisteManager persisteManager) {
        this(shell, null, text, icon, persisteManager);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     */
    public EditUserAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor icon,
            IPersisteManager persisteManager) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
        this.persisteManager = persisteManager;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     * @return false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof DefaultCubridNode
                && NodeType.USER.equals(((DefaultCubridNode) obj).getType())) {
            return true;
        }
        return false;
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (obj.length == 1 && obj[0] instanceof ISchemaNode) {
            ISchemaNode node = (ISchemaNode) obj[0];
            doRun(node);
        }
    }

    /**
     * Perform do run
     *
     * @param node
     */
    public void doRun(ISchemaNode node) { // FIXME move this logic to core module

        CubridDatabase database = node.getDatabase();
        EditUserDialog dlg = new EditUserDialog(shell);
        Connection con = null;
        try {
            con = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);

            final GetAllClassListTask classInfoTask =
                    new GetAllClassListTask(database.getDatabaseInfo(), con);
            final GetAllPartitionClassTask partitionTask =
                    new GetAllPartitionClassTask(database.getDatabaseInfo(), con);
            GetUserListTask task = new GetUserListTask(database.getDatabaseInfo(), con);
            DbUserInfoList userListInfo = null;
            try {
                userListInfo = task.getResultModel();
            } catch (Exception e) {
                LOGGER.error("load user failed", e);
                return;
            }
            boolean isSuccess =
                    dlg.execTask(-1, new ITask[] {classInfoTask, partitionTask}, getShell());
            if (!isSuccess) {
                return;
            }

            dlg.setUserListInfo(userListInfo);
            dlg.setDatabase(database);
            dlg.setUserName(node.getName());
            dlg.setPartitionClassMap(partitionTask.getPartitionClassMap());
            dlg.setNewFlag(false);

            GetUserAuthorizationsTask privilegeTask =
                    new GetUserAuthorizationsTask(database.getDatabaseInfo(), con);
            try {
                for (DbUserInfo userInfo : userListInfo.getUserList()) {
                    userInfo.setUserAuthorizations(
                            privilegeTask.getUserAuthorizations(userInfo.getName()));
                }
            } catch (Exception e) {
                LOGGER.error("get user failed", e);
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            QueryUtil.freeQuery(con);
        }

        if (dlg.open() == IDialogConstants.OK_ID) {
            if (database.getServer() != null) {
                persisteManager.updateDBPassword(
                        database.getServer().getServerName(),
                        database.getServer().getHostAddress(),
                        database.getServer().getMonPort(),
                        database.getDatabaseInfo().getDbName(),
                        database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName(),
                        dlg.getInputtedPassword(),
                        database.isAutoSavePassword());
            }

            CubridNavigatorView navigationView = CubridNavigatorView.findNavigationView();
            if (navigationView != null) {
                TreeViewer treeViewer = navigationView.getViewer();
                if (treeViewer != null) {
                    DbUserInfo userInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
                    if (userInfo != null && userInfo.getName().equalsIgnoreCase(node.getName())) {
                        CommonUITool.openInformationBox(
                                Messages.titleLogout, Messages.msgLogoutInfomation);
                        database.setLogined(false);
                        database.setAutoSavePassword(false);
                        CubridNodeChangedEvent event =
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.DATABASE_LOGOUT);
                        CubridNodeManager.getInstance().fireCubridNodeChanged(event);
                        database.removeAllChild();
                        treeViewer.refresh(database, true);
                    } else {
                        CommonUITool.refreshNavigatorTree(treeViewer, node.getParent());
                        setEnabled(false);
                    }
                }
            }
            ActionManager.getInstance().fireSelectionChanged(getSelection());
            persisteManager.savaAllServers();
            persisteManager.saveAllGroupNodes();
        }
    }
}
