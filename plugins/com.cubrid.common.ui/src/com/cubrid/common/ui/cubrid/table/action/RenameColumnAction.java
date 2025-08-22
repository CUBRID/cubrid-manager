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

import com.cubrid.common.ui.cubrid.table.dialog.RenameColumnDialog;
import com.cubrid.common.ui.query.editor.QueryEditorUtil;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Rename column action
 *
 * @author pangqiren
 * @version 1.0 - 2011-9-28 created by pangqiren
 */
public class RenameColumnAction extends SelectionAction {

    public static final String ID = RenameColumnAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public RenameColumnAction(Shell shell, String text, ImageDescriptor icon) {
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
    public RenameColumnAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Sets this action support to select multi-object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @return boolean
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportSingleSelection(
                obj, new String[] {NodeType.TABLE_COLUMN});
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        ISchemaNode cn = (ISchemaNode) obj[0];
        TableColumn tc = (TableColumn) cn.getAdapter(TableColumn.class);
        RenameColumnDialog dialog =
                new RenameColumnDialog(
                        shell,
                        cn.getParent().getParent().getName(),
                        tc.getColumnName(),
                        cn.getDatabase());
        if (dialog.open() == Dialog.OK) {
            CommonUITool.refreshNavigatorTree((TreeViewer) provider, cn.getParent().getParent());

            ICubridNode tableNode = cn.getParent().getParent();
            if (tableNode != null && tableNode instanceof ISchemaNode) {
                /* Broadcast the view changed */
                QueryEditorUtil.fireSchemaNodeChanged((ISchemaNode) tableNode);
            }
        }
    }
}
