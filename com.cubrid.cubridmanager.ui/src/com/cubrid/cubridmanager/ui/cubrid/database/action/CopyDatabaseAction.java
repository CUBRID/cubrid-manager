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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDbSizeTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.CopyDatabaseDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Copy the database in the server
 *
 * <p>The development/maintenance history of the class Document applicable invariants The
 * concurrency strategy
 *
 * @author robin 2009-3-9
 */
public class CopyDatabaseAction extends SelectionAction {
    public static final String ID = CopyDatabaseAction.class.getName();

    public CopyDatabaseAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public CopyDatabaseAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.hasAdminPermissionOnStopState(obj);
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }
        ISchemaNode schemaNode = (ISchemaNode) obj[0];
        final CubridDatabase database = schemaNode.getDatabase();
        if (database == null) {
            CommonUITool.openErrorBox(getShell(), Messages.msgSelectDB);
            return;
        }
        GetDbSizeTask dbSizeTask = new GetDbSizeTask(database.getServer().getServerInfo());
        dbSizeTask.setDbName(database.getName());

        final CommonQueryTask<DbSpaceInfoList> dbSpaceInfotask =
                new CommonQueryTask<DbSpaceInfoList>(
                        database.getServer().getServerInfo(),
                        CommonSendMsg.getCommonDatabaseSendMsg(),
                        new DbSpaceInfoList());
        dbSpaceInfotask.setDbName(database.getName());
        ISelectionProvider provider = getSelectionProvider();
        final TreeViewer viewer = (TreeViewer) provider;

        final CopyDatabaseDialog dlg = new CopyDatabaseDialog(getShell(), viewer);
        dlg.execTask(-1, new SocketTask[] {dbSizeTask, dbSpaceInfotask}, true, getShell());
        if (dbSpaceInfotask.getErrorMsg() != null
                || dbSizeTask.isCancel()
                || dbSizeTask.getErrorMsg() != null
                || dbSpaceInfotask.isCancel()) {
            return;
        }
        dlg.setDbSize(dbSizeTask.getDbSize());
        dlg.setDbSpaceInfo(dbSpaceInfotask.getResultModel());
        dlg.setDatabase(database);
        dlg.open();
    }
}
