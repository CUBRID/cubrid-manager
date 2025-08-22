/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.cubrid.table.action.makequery;

import com.cubrid.common.ui.cubrid.table.action.CopyToClipboardAction;
import com.cubrid.common.ui.cubrid.table.dialog.CloneTableDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetTablesTask;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * Copy clone SQL to clipboard
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-07-23 created by Kevin.Wang
 */
public class MakeCloneQueryAction extends CopyToClipboardAction {
    public static final String ID = MakeCloneQueryAction.class.getName();
    private String targetName = "";

    public MakeCloneQueryAction(String id, Shell shell, String text, ImageDescriptor icon) {
        this(id, shell, null, text, icon);
    }

    public MakeCloneQueryAction(
            String id,
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor icon) {
        super(shell, provider, text, icon);
        setId(id);
        setCopyToEditor(true);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportSingleSelection(obj, new String[] {NodeType.USER_TABLE});
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        final Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        final DefaultSchemaNode tableNode = (DefaultSchemaNode) obj[0];
        CubridDatabase db = tableNode.getDatabase();
        DatabaseInfo dbInfo = db.getDatabaseInfo();
        GetTablesTask getTableTask = new GetTablesTask(dbInfo);
        List<String> tableList = getTableTask.getAllTableAndViews();

        String tableName = null;
        if (NodeType.USER_TABLE.equals(tableNode.getType())) {
            tableName = tableNode.getName();
        }

        final CloneTableDialog dialog =
                new CloneTableDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        tableList,
                        tableName);
        if (IDialogConstants.OK_ID == dialog.open()) {
            targetName = dialog.getTargetName();
            ICubridNode[] nodeArray = {tableNode};
            super.doRun(nodeArray);
        }
    }

    /**
     * Create update statement SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    protected String getStmtSQL(DefaultSchemaNode schemaNode, IEditorPart editorPart) {
        String sql = SQLGenerateUtils.generateCloneTableSql(schemaNode, targetName);

        try {
            sql = wrapShardSQL(schemaNode, editorPart, sql);
            sql = SQLGenerateUtils.format(sql);
        } catch (Exception ignored) {
        }

        return sql;
    }
}
