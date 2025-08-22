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
package com.cubrid.cubridmanager.ui.common.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.cubridmanager.ui.common.dialog.LoginQueryEditDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * action for create new query editor
 *
 * @author wangsl 2009-3-9
 */
public class QueryNewCustomAction extends SelectionAction {

    private static final Logger LOGGER = LogUtil.getLogger(QueryNewCustomAction.class);

    public static final String ID = QueryNewCustomAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public QueryNewCustomAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public QueryNewCustomAction(
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

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        return true;
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        Object[] selected = getSelectedObj();
        LoginQueryEditDialog dialog = new LoginQueryEditDialog(getShell());
        if (selected != null && selected.length == 1 && selected[0] instanceof ISchemaNode) {
            dialog.setSelServerName(((ISchemaNode) selected[0]).getServer().getServerName());
            dialog.setSelDatabaseName(((ISchemaNode) selected[0]).getDatabase().getName());

        } else if (selected != null
                && selected.length == 1
                && selected[0] instanceof CubridServer) {
            dialog.setSelServerName(((CubridServer) selected[0]).getName());
        } else {
            dialog.setSelServerName(DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL);
        }

        if (dialog.open() == IDialogConstants.OK_ID) {
            try {
                IEditorInput input = new QueryUnit();
                IEditorPart editor = window.getActivePage().openEditor(input, QueryEditorPart.ID);
                ((QueryEditorPart) editor).connect(DatabaseNavigatorMenu.SELF_DATABASE);
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * @see
     *     com.cubrid.common.ui.spi.action.SelectionAction#selectionChanged(org.eclipse.jface.viewers.ISelection)
     * @param selection the ISelection object
     */
    protected void selectionChanged(ISelection selection) {
        if (selection == null || selection.isEmpty()) {
            setEnabled(true);
            return;
        }
        super.selectionChanged(selection);
    }
}
