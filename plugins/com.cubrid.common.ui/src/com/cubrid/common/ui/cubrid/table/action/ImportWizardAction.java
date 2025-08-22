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

import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportDataWizard;
import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportDataWizardDialog;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to import table data.
 *
 * @author robin 2009-6-4
 */
public class ImportWizardAction extends SelectionAction {

    public static final String ID = ImportWizardAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public ImportWizardAction(
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
    public ImportWizardAction(
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

    /**
     * Run
     *
     * @param nodes
     */
    public void run(ICubridNode[] nodes) {
        doRun(nodes);
    }

    /**
     * Do run
     *
     * @param obj
     */
    private void doRun(Object[] obj) {
        int len = obj.length;
        final List<String> tableList = new ArrayList<String>();
        CubridDatabase database = null;
        for (int i = 0; i < len; i++) {
            ISchemaNode node = (ISchemaNode) obj[i];
            database = node.getDatabase();
            if (NodeType.USER_TABLE.equals(node.getType())) {
                final String tableName = node.getName();
                tableList.add(tableName);
            }
        }

        ImportDataWizardDialog dlg =
                new ImportDataWizardDialog(getShell(), new ImportDataWizard(database, tableList));
        dlg.setPageSize(880, 420);
        dlg.open();
    }
}
