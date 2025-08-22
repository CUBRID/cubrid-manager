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
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.dialog.RenameTableDialog;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.PropertyChangeProvider;
import java.util.ArrayList;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Rename table name action
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-15 created by Yu Guojia
 */
public class ModifyTableNameAction extends AbstractSelectionAction {
    public static String ID = ModifyTableNameAction.class.getName();
    public static String NAME = com.cubrid.common.ui.spi.Messages.tableRenameActionName;
    public static String DATA_KEY = "newName";

    public ModifyTableNameAction(IWorkbenchPart part) {
        super(part);
        setLazyEnablementCalculation(false);
    }

    @Override
    protected void init() {
        setText(NAME);
        setToolTipText(NAME);
        setId(ID);

        ImageDescriptor icon = CommonUIPlugin.getImageDescriptor("icons/action/table_rename.png");
        if (icon != null) {
            setImageDescriptor(icon);
            setEnabled(false);
        }
    }

    @Override
    protected boolean calculateEnabled() {
        if (!super.calculateEnabled()) {
            return false;
        }
        if (1 != getSelectedObjects().size()
                || !(getSelectedObjects().get(0) instanceof EditPart)) {
            return false;
        }

        return true;
    }

    @Override
    public void run() {
        PropertyChangeProvider node = getSelectedNode();
        boolean isPhysicModel = getERSchema().isPhysicModel();
        boolean isSupportUserSchema =
                getERSchema().getCubridDatabase().getDatabaseInfo().isSupportUserSchema();
        String oldName = ((ERTable) node).getShownName();
        Set<String> names = getERSchema().getAllShownTableNames();
        names.remove(oldName);

        RenameTableDialog dlg =
                new RenameTableDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        oldName,
                        true,
                        new ArrayList<String>(names),
                        isPhysicModel,
                        isSupportUserSchema);
        int ret = dlg.open();
        if (ret == IDialogConstants.OK_ID) {
            String newName = dlg.getNewName();
            if (StringUtil.isEqual(oldName, newName)) {
                return;
            }
            getERSchema().modifyTableNameAndFire(oldName, newName, isPhysicModel);
        }
    }

    @Override
    public IStatus postTaskFinished(ITask task) {
        return null;
    }

    @Override
    public void completeAll() {}
}
