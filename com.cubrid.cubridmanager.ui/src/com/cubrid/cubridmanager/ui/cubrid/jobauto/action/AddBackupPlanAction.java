/*
 * Copyright (C) 2013 NHN Corporation. All rights reserved by NHN.
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
package com.cubrid.cubridmanager.ui.cubrid.jobauto.action;

import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.AddEditType;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.dialog.EditBackupPlanDialog;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridDatabaseLoader;
import com.cubrid.cubridmanager.ui.spi.model.loader.jobauto.CubridJobAutoFolderLoader;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This is an action to listen to adding backup plan selection event and open an instance of
 * EditBackupPlanDialog class
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-3-13 created by lizhiqiang
 */
public class AddBackupPlanAction extends SelectionAction {
    public static final String ID = AddBackupPlanAction.class.getName();
    private static final String ICON_PATH = "icons/navigator/auto_backup_item.png";
    private boolean canceledTask = false;

    public AddBackupPlanAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    protected AddBackupPlanAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
    }

    public void run() {
        Object[] obj = this.getSelectedObj();

        CubridDatabase database = null;
        DefaultSchemaNode selection = null;
        if (obj.length > 0 && obj[0] instanceof DefaultSchemaNode) {
            selection = (DefaultSchemaNode) obj[0];
            database = selection.getDatabase();
        }
        if (database == null) {
            CommonUITool.openErrorBox(Messages.msgSelectDB);
            return;
        }
        run(database);
    }

    /**
     * Creates a Dialog which is the instance of EditBackupPlanDialog to add a backup plan
     *
     * @param database
     */
    public void run(CubridDatabase database) {
        TreeViewer treeViewer = (TreeViewer) this.getSelectionProvider();
        ICubridNode jobAutoFolderNode =
                database.getChild(
                        database.getId()
                                + ICubridNodeLoader.NODE_SEPARATOR
                                + CubridDatabaseLoader.JOB_AUTO_FOLDER_ID);
        DefaultSchemaNode buckupPlanFolderNode =
                (DefaultSchemaNode)
                        jobAutoFolderNode.getChild(
                                jobAutoFolderNode.getId()
                                        + ICubridNodeLoader.NODE_SEPARATOR
                                        + CubridJobAutoFolderLoader.BACKUP_PLAN_FOLDER_ID);
        EditBackupPlanDialog editBackupPlanDlg = new EditBackupPlanDialog(getShell(), true);
        editBackupPlanDlg.setOperation(AddEditType.ADD);
        editBackupPlanDlg.initPara(buckupPlanFolderNode);

        if (editBackupPlanDlg.open() == Dialog.OK) {
            BackupPlanInfo backupPlanInfo = editBackupPlanDlg.getBackupPlanInfo();
            String newBackupId = backupPlanInfo.getBackupid();
            DefaultSchemaNode newNode =
                    new DefaultSchemaNode(newBackupId, newBackupId, newBackupId, ICON_PATH);
            newNode.setContainer(false);
            newNode.setType(CubridNodeType.BACKUP_PLAN);
            newNode.setModelObj(backupPlanInfo);
            buckupPlanFolderNode.addChild(newNode);
            treeViewer.add(buckupPlanFolderNode, newNode);

            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    newNode, CubridNodeChangedEventType.NODE_ADD));
        } else {
            canceledTask = true;
        }
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        if (!(obj instanceof ISchemaNode)) {
            return false;
        }
        ISchemaNode node = (ISchemaNode) obj;
        CubridDatabase database = node.getDatabase();
        if (CubridNodeType.BACKUP_PLAN_FOLDER.equals(node.getType())
                && database != null
                && database.isLogined()) {
            DbUserInfo dbUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
            if (dbUserInfo != null && dbUserInfo.isDbaAuthority()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCanceledTask() {
        return canceledTask;
    }
}
