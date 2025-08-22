/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.action;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.dialog.EditVirtualTableDialog;
import com.cubrid.common.ui.er.part.ColumnPart;
import com.cubrid.common.ui.er.part.TablePart;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Edit er table action
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-16 created by Yu Guojia
 */
public class EditTableAction extends AbstractSelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(EditTableAction.class);
    public static String ID = EditTableAction.class.getName();
    public static String NAME = Messages.actionEditTableName;

    public EditTableAction(IWorkbenchPart part) {
        super(part);
        setLazyEnablementCalculation(false);
    }

    protected void init() {
        setText(NAME);
        setToolTipText(NAME);
        setId(ID);
        ImageDescriptor icon =
                CommonUIPlugin.getImageDescriptor("icons/action/table_record_edit.png");
        if (icon != null) {
            setImageDescriptor(icon);
            setEnabled(true);
        }
    }

    protected boolean calculateEnabled() {
        if (!super.calculateEnabled()) {
            return false;
        }

        if (getSelectedObjects().size() == 1) {
            if ((getSelectedObjects().get(0) instanceof TablePart)
                    || (getSelectedObjects().get(0) instanceof ColumnPart)) {
                return true;
            }
        }

        return false;
    }

    public void run() {
        EditVirtualTableDialog editDlg =
                new EditVirtualTableDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        getERSchema().getCubridDatabase(),
                        false,
                        getERTable());

        int ret = editDlg.open();
        if (ret == IDialogConstants.OK_ID) {
            editDlg.postEdittedTable(getERSchema());
        }
    }

    public IStatus postTaskFinished(ITask task) {
        return null;
    }

    public void completeAll() {}
}
