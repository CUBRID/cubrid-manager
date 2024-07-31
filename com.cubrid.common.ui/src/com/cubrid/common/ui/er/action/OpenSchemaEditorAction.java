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
import com.cubrid.common.ui.er.SchemaEditorInput;
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.er.model.ERVirtualDatabase;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Open Schema Editor Action
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-10 created by Yu Guojia
 */
public class OpenSchemaEditorAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(OpenSchemaEditorAction.class);
    public static final String ID = OpenSchemaEditorAction.class.getName();

    public OpenSchemaEditorAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public OpenSchemaEditorAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Return whether this action support to select multi object,if not support,this action will be
     * disabled
     *
     * @return true,if allow multi selection
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return true, if support this object(s)
     */
    public boolean isSupported(Object obj) {
        Object[] objs = this.getSelectedObj();
        CubridDatabase[] cubridDatabases = getDBNodes(objs);
        if (objs.length > 1 && cubridDatabases.length != objs.length) {
            return false;
        }

        return true;
    }

    /**
     * Get data base node that related selected objects, Including login and logout database.
     *
     * @param objs
     * @return
     */
    private CubridDatabase[] getDBNodes(Object[] objs) {
        List<CubridDatabase> returnArray = new ArrayList<CubridDatabase>();
        CubridDatabase database = null;
        for (Object obj : objs) {
            if (obj instanceof ISchemaNode) {
                database = ((ISchemaNode) obj).getDatabase();
                if (database != null) {
                    // if multiple selection ,only open a schema editor on this
                    // database node
                    if (!returnArray.contains(database)) {
                        returnArray.add(database);
                    }
                }
            }
        }

        return returnArray.toArray(new CubridDatabase[0]);
    }

    /**
     * Open the selected database Schema Editor. If basing on none-database node, open the ERD by a
     * virtual database
     */
    public void run() { // FIXME move this logic to core module
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        Object[] obj = this.getSelectedObj();
        CubridDatabase[] cubridDatabases = getDBNodes(obj);
        for (int i = 0; i < cubridDatabases.length; i++) { // when right-click
            // multi db nodes
            if (cubridDatabases[i].getDatabaseInfo() == null || !cubridDatabases[i].isLogined()) {
                cubridDatabases[i] = ERVirtualDatabase.getInstance();
            }
        }
        if (cubridDatabases.length == 0) { // when right-click not database nodes
            cubridDatabases = new CubridDatabase[1];
            cubridDatabases[0] = ERVirtualDatabase.getInstance();
        }

        // Limit max number one time
        if (cubridDatabases.length > LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM) {
            CommonUITool.openConfirmBox(
                    Messages.bind("SchemaDesigner max...", LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM));
            List<CubridDatabase> list =
                    new ArrayList<CubridDatabase>(LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM);
            for (int i = 0; i < LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM; i++) {
                list.add(cubridDatabases[i]);
            }
            cubridDatabases = new CubridDatabase[LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM];
            list.toArray(cubridDatabases);
        }

        for (CubridDatabase database : cubridDatabases) {
            SchemaEditorInput schemaEditorInput =
                    new SchemaEditorInput(
                            database,
                            (provider instanceof TreeViewer) ? (TreeViewer) provider : null);
            schemaEditorInput.setDatabase(database);

            DatabaseInfo dbInfo = database.getDatabaseInfo();
            if (dbInfo == null) {
                continue;
            }

            try {
                window.getActivePage().openEditor(schemaEditorInput, ERSchemaEditor.ID);
                if (Util.isMac()) { // refresh for low version mac
                    PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow()
                            .getActivePage()
                            .setEditorAreaVisible(false);
                    PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow()
                            .getActivePage()
                            .setEditorAreaVisible(true);
                }
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
