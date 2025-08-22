/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.action.FocusAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Schema Info View Action for a query editor and a query explain tool in a query editor
 *
 * <p>ShowSchemaAction Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public class ShowSchemaAction extends FocusAction {
    public static final String ID = ShowSchemaAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     */
    public ShowSchemaAction(Shell shell, String text) {
        this(shell, text, null);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public ShowSchemaAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param text
     */
    protected ShowSchemaAction(Shell shell, Control provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Notifies that the focus gained event
     *
     * @param event an event containing information about the focus change
     */
    public void focusGained(FocusEvent event) {
        setEnabled(false);
        if (event.getSource() instanceof StyledText) {
            StyledText stext = (StyledText) event.getSource();
            if (stext != null
                    && stext.getSelectionText() != null
                    && stext.getSelectionText().trim().length() > 0) {
                setEnabled(true);
            }
        }
    }

    /**
     * @see org.eclipse.jface.action.Action#run() Override the run method in order to complete
     *     showing brokers status server to a broker
     */
    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            showError();
            return;
        }

        IEditorPart editor = window.getActivePage().getActiveEditor();
        if (editor == null) {
            showError();
            return;
        }

        if (!(editor instanceof QueryEditorPart)) {
            showError();
            return;
        }

        QueryEditorPart queryEditorPart = (QueryEditorPart) editor;
        CubridDatabase db = queryEditorPart.getSelectedDatabase();
        if (db == null || !db.isLogined()) {
            showError();
            return;
        }

        String tableName = queryEditorPart.getSelectedText();
        if (tableName == null) {
            showError();
            return;
        }

        DatabaseInfo databaseInfo = db.getDatabaseInfo();
        if (databaseInfo == null) {
            showError();
            return;
        }

        CubridNavigatorView mainNav = CubridNavigatorView.findNavigationView();
        if (mainNav != null) {
            mainNav.showQuickView(databaseInfo, tableName, true);
        }
    }

    private void showError() {
        CommonUITool.openErrorBox(Messages.qedit_select_table_not_exist_in_db);
    }
}
