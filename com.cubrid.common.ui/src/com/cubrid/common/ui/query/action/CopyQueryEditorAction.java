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
package com.cubrid.common.ui.query.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.dialog.ShardIdSelectionDialog;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Create a new query editor with current editor's database information.
 *
 * @author Kevin Cao 2011-2-23
 */
public class CopyQueryEditorAction extends Action implements IMenuListener {

    private static final Logger LOGGER = LogUtil.getLogger(CopyQueryEditorAction.class);

    public static final String ID = CopyQueryEditorAction.class.getName();

    public CopyQueryEditorAction() {
        super();
        setId(ID);
    }

    public CopyQueryEditorAction(String text, ImageDescriptor image) {
        super(text, image);
        setId(ID);
    }

    public CopyQueryEditorAction(String text, int style) {
        super(text, style);
        setId(ID);
    }

    public CopyQueryEditorAction(String text) {
        super(text);
        setId(ID);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor instanceof QueryEditorPart) {
            QueryUnit queryUnit = new QueryUnit();
            QueryEditorPart qep = (QueryEditorPart) editor;
            CubridDatabase database = qep.getSelectedDatabase();

            if (database != null) {
                queryUnit.setDatabase(database);
            }

            // [TOOLS-2425]Support shard broker
            if (database != null) {
                DatabaseInfo dbInfo = database.getDatabaseInfo();
                if (dbInfo != null && dbInfo.isShard()) {
                    ShardIdSelectionDialog dialog =
                            new ShardIdSelectionDialog(Display.getDefault().getActiveShell());
                    dialog.setDatabaseInfo(dbInfo);
                    dialog.setShardId(0);
                    if (dialog.open() == IDialogConstants.OK_ID) {
                        dbInfo.setCurrentShardId(dialog.getShardId());
                    }
                }
            }

            try {
                IEditorPart newEditor =
                        window.getActivePage().openEditor(queryUnit, QueryEditorPart.ID);
                if (newEditor != null && database != null) {
                    ((QueryEditorPart) newEditor).connect(database);
                }
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * When menu show, update the action's status.
     *
     * @param manager IMenuManager
     */
    public void menuAboutToShow(IMenuManager manager) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            manager.remove(ID);
            return;
        }
        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor instanceof QueryEditorPart) {
            if (manager.find(ID) == null) {
                manager.add(this);
            }
        } else {
            manager.remove(ID);
        }
    }
}
