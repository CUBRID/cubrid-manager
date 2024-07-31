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
package com.cubrid.common.ui.cubrid.table.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * This action is responsible to select all table.
 *
 * @author robin 2009-6-4
 */
public class TableSelectAllAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(TableSelectAllAction.class);
    public static final String ID = TableSelectAllAction.class.getName();

    public TableSelectAllAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public TableSelectAllAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportSingleSelection(
                obj,
                new String[] {
                    NodeType.USER_TABLE,
                    NodeType.USER_VIEW,
                    NodeType.USER_PARTITIONED_TABLE_FOLDER,
                    NodeType.USER_PARTITIONED_TABLE,
                    NodeType.SYSTEM_TABLE,
                    NodeType.SYSTEM_VIEW
                });
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        ISchemaNode table = (ISchemaNode) obj[0];
        doRun(table);
    }

    public void run(ISchemaNode object) {
        doRun(object);
    }

    private void doRun(ISchemaNode table) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        QueryUnit input = new QueryUnit();
        input.setDatabase(table.getDatabase());
        try {
            QueryEditorPart editor =
                    (QueryEditorPart) window.getActivePage().openEditor(input, QueryEditorPart.ID);
            editor.connect(table.getDatabase());

            String escapedTableName =
                    QuerySyntax.escapeKeyword(
                            table.getName()); // FIXME move this logic to core module
            String sql = "SELECT * FROM " + escapedTableName + ";";

            if (table.getDatabase() != null) {
                sql = DatabaseInfo.wrapShardQuery(table.getDatabase().getDatabaseInfo(), sql);
            }

            editor.setQuery(sql, false, true, false);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
