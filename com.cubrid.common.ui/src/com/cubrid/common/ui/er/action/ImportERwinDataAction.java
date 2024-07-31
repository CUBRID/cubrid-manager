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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.control.ExportImportGsonDataController;
import com.cubrid.common.ui.er.control.ImportERwinDataController;
import com.cubrid.common.ui.er.dialog.ImportERDataDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Import ERwin Data Action
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-5 created by Yu Guojia
 */
public class ImportERwinDataAction extends AbstractSelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(ImportERwinDataAction.class);
    public static String ID = ImportERwinDataAction.class.getName();
    public static String NAME = Messages.titleImportSchemaData;

    public ImportERwinDataAction(IWorkbenchPart part) {
        super(part);
        setLazyEnablementCalculation(false);
    }

    @Override
    public void init() {
        setId(ID);
        setText(NAME);
        setToolTipText(NAME);

        ImageDescriptor icon = CommonUIPlugin.getImageDescriptor("icons/action/import_on.png");
        if (icon != null) {
            setImageDescriptor(icon);
            setEnabled(false);
        }
    }

    @Override
    protected boolean calculateEnabled() {
        return true;
    }

    public Command createImportERwinDataCommand() {
        if (!(getSelectedObjects().get(0) instanceof EditPart)) {
            return null;
        }
        EditPart object = (EditPart) getSelectedObjects().get(0);
        Request addTablesReq = new Request(ImportERwinDataAction.ID);
        return object.getCommand(addTablesReq);
    }

    @Override
    public void run() {
        if (!CommonUITool.openConfirmBox(Messages.msgConfirmLoadERFile)) {
            return;
        }
        Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        ImportERDataDialog dialog = new ImportERDataDialog(parent, getERSchema());
        int returnvalue = dialog.open();
        if (returnvalue != IDialogConstants.OK_ID) {
            return;
        }

        if (dialog.isERWinFile()) {
            ImportERwinDataController importControl = new ImportERwinDataController(getERSchema());
            importControl.importERwinData(parent, dialog.getERWinContainer());
        } else if (dialog.isGsonFile()) {
            ExportImportGsonDataController gsonDataController =
                    new ExportImportGsonDataController(getERSchema());
            gsonDataController.importGsonData(parent, dialog.getGsonData());
        }
    }
}
