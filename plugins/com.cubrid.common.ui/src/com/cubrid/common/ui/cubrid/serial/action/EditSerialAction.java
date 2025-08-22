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
package com.cubrid.common.ui.cubrid.serial.action;

import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.cubrid.serial.Messages;
import com.cubrid.common.ui.cubrid.serial.dialog.CreateOrEditSerialDialog;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.serial.task.GetSerialInfoTask;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to edit serial
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-11 created by pangqiren
 */
public class EditSerialAction extends SelectionAction {

    public static final String ID = EditSerialAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public EditSerialAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public EditSerialAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
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
        if (!(obj instanceof ISchemaNode)) {
            return false;
        }

        return true;
    }

    /** get edit serial node */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        final ISchemaNode schemaNode = (ISchemaNode) objArr[0];
        CubridDatabase database = schemaNode.getDatabase();
        run(database, schemaNode);
    }

    /** Open the editSerial dialog and edit serial */
    public int run(CubridDatabase database, final ISchemaNode node) {
        final Shell shell = getShell();
        TaskExecutor taskExcutor =
                new TaskExecutor() {
                    public boolean exec(final IProgressMonitor monitor) {
                        if (monitor.isCanceled()) {
                            return false;
                        }
                        monitor.beginTask(Messages.loadSerialTaskName, IProgressMonitor.UNKNOWN);
                        for (ITask task : taskList) {
                            SerialInfo serialInfo = null;
                            if (task instanceof GetSerialInfoTask) {
                                GetSerialInfoTask getSerialInfoTask = (GetSerialInfoTask) task;
                                serialInfo = getSerialInfoTask.getSerialInfo(node.getName());
                            }
                            final String msg = task.getErrorMsg();
                            if (openErrorBox(shell, msg, monitor)) {
                                return false;
                            }
                            if (monitor.isCanceled()) {
                                return false;
                            }
                            if (serialInfo == null) {
                                openErrorBox(shell, Messages.errNameNotExist, monitor);
                                return false;
                            }
                            node.setModelObj(serialInfo);
                        }
                        return true;
                    }
                };
        DatabaseInfo databaseInfo = database.getDatabaseInfo();
        GetSerialInfoTask task = new GetSerialInfoTask(databaseInfo);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (!taskExcutor.isSuccess()) {
            return IDialogConstants.CANCEL_ID;
        }

        boolean isEditorAble = ActionSupportUtil.isSupportSinSelCheckDbUser(node, NodeType.SERIAL);
        CreateOrEditSerialDialog dialog = new CreateOrEditSerialDialog(getShell(), isEditorAble);
        dialog.setEditedNode(node);
        dialog.setDatabase(database);
        if (dialog.open() == IDialogConstants.OK_ID) {
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    node, CubridNodeChangedEventType.NODE_REFRESH));
            ActionManager.getInstance().fireSelectionChanged(getSelection());
            return IDialogConstants.OK_ID;
        }
        return IDialogConstants.CANCEL_ID;
    }
}
