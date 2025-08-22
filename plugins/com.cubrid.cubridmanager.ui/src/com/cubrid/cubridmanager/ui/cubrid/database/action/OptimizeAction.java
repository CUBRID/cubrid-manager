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

import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetClassListTask;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.DeleteDatabaseDialog;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.OptimizeDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Optimize the table
 *
 * <p>Known bugs The development/maintenance history of the class Document applicable invariants The
 * concurrency strategy
 *
 * @author robin 2009-3-9
 */
public class OptimizeAction extends SelectionAction {
    public static final String ID = OptimizeAction.class.getName();

    public OptimizeAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    public OptimizeAction(
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
        ISchemaNode node = (ISchemaNode) obj[0];
        final CubridDatabase database = node.getDatabase();
        if (database == null) {
            CommonUITool.openErrorBox(getShell(), Messages.msgSelectDB);
            return;
        }

        OptimizeDialog dlg = new OptimizeDialog();

        GetClassListTask task =
                new GetClassListTask(
                        database.getServer().getServerInfo(),
                        database.getDatabaseInfo().getCharSet());
        task.setDbName(database.getName());
        if (database.getDatabaseInfo().getRunningType() == DbRunningType.STANDALONE) {
            task.setDbStatus(OnOffType.OFF);
        } else {
            task.setDbStatus(OnOffType.ON);
        }

        dlg.executeGetClassListTask(-1, task, true, getShell());
        if (task.getErrorMsg() != null || task.isCancel()) {
            return;
        }
        dlg.setDatabase(database);
        dlg.setUserClassList(task.getDbClassInfo().getUserClassList().getClassList());
        if (dlg.open() == DeleteDatabaseDialog.DELETE_ID) {
            ISelectionProvider provider = getSelectionProvider();
            if (provider instanceof TreeViewer) {
                TreeViewer treeViewer = (TreeViewer) provider;
                CommonUITool.refreshNavigatorTree(treeViewer, database.getParent());
                setEnabled(false);
            }
            ActionManager.getInstance().fireSelectionChanged(getSelection());
        }
    }
}
