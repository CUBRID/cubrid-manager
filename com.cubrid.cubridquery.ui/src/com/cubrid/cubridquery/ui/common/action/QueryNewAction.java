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
package com.cubrid.cubridquery.ui.common.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.dialog.ShardIdSelectionDialog;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridquery.ui.common.dialog.LoginQueryEditorDialog;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
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
    public QueryNewAction(
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
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }

        CubridDatabase[] cubridDatabases = handleSelectionObj(obj);

        try {
            if (cubridDatabases.length == 0) {
                openQueryEditor(null);
                return;
            }

            /*Limit max number one time*/
            if (cubridDatabases.length > LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM) {
                CommonUITool.openConfirmBox(
                        Messages.bind(
                                Messages.msgMaxOpenNum, LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM));
                List<CubridDatabase> list =
                        new ArrayList<CubridDatabase>(LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM);
                for (int i = 0; i < LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM; i++) {
                    list.add(cubridDatabases[i]);
                }
                cubridDatabases = new CubridDatabase[LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM];
                list.toArray(cubridDatabases);
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
            if (!(obj instanceof ISchemaNode)) {
                continue;
            }

            database = ((ISchemaNode) obj).getDatabase();
            if (database != null) {
                // if multiple selection ,only open a query editor with one database
                if (!returnArray.contains(database)) {
                    returnArray.add(database);
                }
            }
        }

        return returnArray.toArray(new CubridDatabase[0]);
    }

    /**
     * Open new query editor.
     *
     * @param database of query editor.
     * @throws PartInitException when open editor error.
     */
    private void openQueryEditor(CubridDatabase database) throws PartInitException {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null || window.getActivePage() == null) {
            return;
        }

        if (database == null) {
            window.getActivePage().openEditor(new QueryUnit(), QueryEditorPart.ID);
            return;
        }

        CubridDatabase cdb = database;
        if (database == null || !database.isLogined()) {
            LoginQueryEditorDialog dialog = new LoginQueryEditorDialog(getShell());
            dialog.setSelectedConnName(getInitConnectionName(database));
            if (dialog.open() != IDialogConstants.OK_ID) {
                return;
            }

            cdb = DatabaseNavigatorMenu.SELF_DATABASE;
        }

        // [TOOLS-2425]Support shard broker
        boolean isShrd = false;
        int shardId = 0;
        int shardVal = 0;
        int shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_ID;
        if (cdb != null) {
            DatabaseInfo dbInfo = cdb.getDatabaseInfo();
            if (dbInfo != null && dbInfo.isShard()) {
                isShrd = true;
                ShardIdSelectionDialog dialog = new ShardIdSelectionDialog(getShell());
                dialog.setDatabaseInfo(dbInfo);
                dialog.setShardId(0);
                dialog.setShardVal(0);
                dialog.setShardQueryType(shardQueryType);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    shardId = dialog.getShardId();
                    shardVal = dialog.getShardVal();
                    shardQueryType = dialog.getShardQueryType();
                }
            }
        }

        QueryUnit input = new QueryUnit();
        input.setDatabase(cdb);
        IEditorPart editor = window.getActivePage().openEditor(input, QueryEditorPart.ID);
        if (editor != null) {
            QueryEditorPart editorPart = (QueryEditorPart) editor;
            editorPart.connect(cdb);
            // [TOOLS-2425]Support shard broker
            if (isShrd) {
                editorPart.setShardId(shardId);
                editorPart.setShardVal(shardVal);
                editorPart.setShardQueryType(shardQueryType);
                editorPart.changeQueryEditorPartNameWithShard();
            }
        }
    }

    /**
     * Get the connection dialog's connection name.
     *
     * @param database of dialog
     * @return connection name
     */
    private String getInitConnectionName(CubridDatabase database) {
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
