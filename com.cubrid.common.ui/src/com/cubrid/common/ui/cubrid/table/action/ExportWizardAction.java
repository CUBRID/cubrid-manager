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
package com.cubrid.common.ui.cubrid.table.action;

import com.cubrid.common.ui.cubrid.table.dialog.exp.ExportDataWizard;
import com.cubrid.common.ui.cubrid.table.dialog.exp.ExportDataWizardDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to export table data.
 *
 * @author robin 2009-6-4
 */
public class ExportWizardAction extends SelectionAction {
    public static final String ID = ExportWizardAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public ExportWizardAction(
            Shell shell, String text, ImageDescriptor icon, ImageDescriptor disabledIcon) {
        this(shell, null, text, icon, disabledIcon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public ExportWizardAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor icon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, icon);
        this.setDisabledImageDescriptor(disabledIcon);
        this.setId(ID);
    }

    /**
     * Sets this action support to select multi-object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @return boolean
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(obj, null, false);
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        doRun(obj);
    }

    public void run(Object[] obj) {
        doRun(obj);
    }

    private void doRun(Object[] obj) {
        int len = obj.length;
        final List<String> tableList = new ArrayList<String>();
        CubridDatabase database = null;
        for (int i = 0; i < len; i++) {
            ISchemaNode node = (ISchemaNode) obj[i];
            database = node.getDatabase();
            if (NodeType.USER_TABLE.equals(node.getType())
                    || NodeType.USER_VIEW.equals(node.getType())
                    || NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(node.getType())
                    || NodeType.USER_PARTITIONED_TABLE.equals(node.getType())) {
                final String tableName = node.getName();
                tableList.add(tableName);
            }
        }
        ExportDataWizardDialog dlg =
                new ExportDataWizardDialog(getShell(), new ExportDataWizard(database, tableList));
        dlg.setPageSize(800, Util.isMac() ? 480 : 455);
        dlg.open();
    }
}
