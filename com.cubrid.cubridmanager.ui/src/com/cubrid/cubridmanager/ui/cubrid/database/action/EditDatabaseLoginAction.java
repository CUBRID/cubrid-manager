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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.LoginDatabaseDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is response to login database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class EditDatabaseLoginAction extends SelectionAction {
    public static final String ID = EditDatabaseLoginAction.class.getName();

    public EditDatabaseLoginAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public EditDatabaseLoginAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    public boolean isSupported(Object obj) {
        return (obj instanceof CubridDatabase);
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }

        if (obj[0] != null && obj[0] instanceof CubridDatabase) {
            doRun(new CubridDatabase[] {(CubridDatabase) obj[0]});
        }
    }

    public void doRun(CubridDatabase[] databaseArray) {
        if (databaseArray == null || databaseArray.length == 0) {
            return;
        }

        CubridNavigatorView navigationView = CubridNavigatorView.findNavigationView();
        if (navigationView != null) {
            final TreeViewer treeViewer = navigationView.getViewer();
            CubridDatabase database = (CubridDatabase) databaseArray[0];
            LoginDatabaseDialog dialog = new LoginDatabaseDialog(getShell(), database);
            int returnVal = dialog.open();
            if (returnVal == IDialogConstants.OK_ID && provider instanceof TreeViewer) {
                database.removeAllChild();
                if (database.getLoader() != null) {
                    database.getLoader().setLoaded(false);
                }
                treeViewer.refresh(database, true);
                treeViewer.expandToLevel(database, 1);

                ActionManager.getInstance().fireSelectionChanged(getSelection());
                LayoutManager.getInstance().fireSelectionChanged(getSelection());

                if (dialog.isFireLogoutEvent()) {
                    CubridNodeManager.getInstance()
                            .fireCubridNodeChanged(
                                    new CubridNodeChangedEvent(
                                            database, CubridNodeChangedEventType.DATABASE_LOGOUT));
                }
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.DATABASE_LOGIN));
            } else if (returnVal == LoginDatabaseDialog.SAVE_ID && provider instanceof TreeViewer) {
                treeViewer.refresh(database, true);
                treeViewer.expandToLevel(database, 1);
            }
        }
    }
}
