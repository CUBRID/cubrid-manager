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
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.ui.common.Messages;
import com.cubrid.cubridmanager.ui.common.dialog.LoginQueryEditDialog;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * action for create new query editor
 *
 * @author wangsl 2009-3-9
 */
public class QueryNewAction extends SelectionAction {

    private static final Logger LOGGER = LogUtil.getLogger(QueryNewAction.class);
    public static final String ID = QueryNewAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public QueryNewAction(
            Shell shell,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon,
            boolean isBig) {
        this(shell, null, text, enabledIcon, disabledIcon, isBig);
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
    public QueryNewAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon,
            boolean bigButton) {
        super(shell, provider, text, enabledIcon);
        this.setId(bigButton ? ID + ".big" : ID);
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
        Object[] obj = this.getSelectedObj();
        CubridDatabase[] cubridDatabases = handleSelectionObj(obj);
        try {
            if (cubridDatabases.length == 0) {
                openQueryEditor(null);
                return;
            }
            for (CubridDatabase database : cubridDatabases) {
                openQueryEditor(database);
            }
        } catch (PartInitException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * handleSelectionObj
     *
     * @param objs
     * @return
     */
    private CubridDatabase[] handleSelectionObj(Object[] objs) {
        List<CubridDatabase> returnArray = new ArrayList<CubridDatabase>();
        CubridDatabase database = null;
        for (Object obj : objs) {
            if (obj instanceof ISchemaNode) {
                database = ((ISchemaNode) obj).getDatabase();
                if (database != null) {
                    // if multiple selection ,only open a query editor with one database
                    if (!returnArray.contains(database)) {
                        returnArray.add(database);
                    }
                }
            }
        }
        return returnArray.toArray(new CubridDatabase[0]);
    }

    /**
     * Get current selected server
     *
     * @return CubridServer
     */
    private CubridServer getSelectedSever() {
        Object[] selected = getSelectedObj();
        CubridServer server = null;
        if (selected != null && selected.length >= 1) {
            if (selected[0] instanceof ISchemaNode) {
                server = ((ISchemaNode) selected[0]).getServer();
            } else if (selected[0] instanceof ICubridNode) {
                server = ((ICubridNode) selected[0]).getServer();
            }
        }
        return server;
    }

    /**
     * Open new query editor.
     *
     * @param database of query editor.
     * @throws PartInitException when open editor error.
     */
    private void openQueryEditor(CubridDatabase database) throws PartInitException {
        IWorkbenchPage page = LayoutUtil.getActivePage();
        if (page == null) {
            return;
        }
        if (database == null) {
            page.openEditor(new QueryUnit(), QueryEditorPart.ID);
            return;
        }
        if (database != null && database.getRunningType() != DbRunningType.CS) {
            CommonUITool.openErrorBox(Messages.bind(Messages.msgStartDb, database.getLabel()));
            return;
        }

        CubridDatabase cdb = database;
        if (database == null || !database.isLogined()) {
            LoginQueryEditDialog dialog = new LoginQueryEditDialog(getShell());
            dialog.setSelServerName(getInitConnectionName(getSelectedSever()));
            dialog.setSelDatabaseName(getInitDatabaseName(database));
            if (dialog.open() == IDialogConstants.OK_ID) {
                cdb = DatabaseNavigatorMenu.SELF_DATABASE;
            } else {
                return;
            }
        }

        QueryUnit input = new QueryUnit();
        input.setDatabase(cdb);
        IEditorPart editor = page.openEditor(input, QueryEditorPart.ID);
        if (editor != null) {
            ((QueryEditorPart) editor).connect(cdb);
        }
    }

    /**
     * Get the connection dialog's connection name.
     *
     * @param server CubridServer
     * @return connection name
     */
    private String getInitConnectionName(CubridServer server) {
        String connName = DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL;
        if (server != null) {
            connName = server.getName();
        }
        return connName;
    }

    /**
     * Get the connection dialog's connection name.
     *
     * @param database of dialog
     * @return connection name
     */
    private String getInitDatabaseName(CubridDatabase database) {
        String connName = DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL;
        if (database != null) {
            connName = database.getName();
        }
        return connName;
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
