/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.mondashboard.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DatabaseMonitorPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Delete the database monitor action
 *
 * @author cyl
 * @version 1.0 - 2010-6-3 created by cyl
 */
public class DeleteDatabaseMonitorAction extends SelectionAction {

    public static final String ID = DeleteDatabaseMonitorAction.class.getName();

    /**
     * constructor.
     *
     * @param shell window.getShell()
     * @param text action text
     * @param icon ImageDescriptor
     */
    public DeleteDatabaseMonitorAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * constructor of DeleteDatabaseMonitorAction
     *
     * @param shell Shell
     * @param provider ISelectionProvider
     * @param text String
     * @param icon ImageDescriptor
     */
    protected DeleteDatabaseMonitorAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * get the action is support multi selections
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return allow multi selections
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * get the action is support the selection.
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj Object is supported
     * @return obj is supported
     */
    public boolean isSupported(Object obj) {
        return obj instanceof DatabaseMonitorPart;
    }

    /** delete database monitor from dashboard. */
    public void run() {
        IStructuredSelection selection = (IStructuredSelection) getSelection();
        DatabaseMonitorPart dmp = (DatabaseMonitorPart) selection.getFirstElement();
        // EditPart dashboardPart = dmp.getParent();
        DatabaseNode dn = (DatabaseNode) dmp.getModel();
        boolean isDelete =
                CommonUITool.openConfirmBox(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        Messages.bind(Messages.msgConfirmDeleteDatabase, dn.getName()));
        if (!isDelete) {
            return;
        }
        dn.getParent().removeDbNode(dn);
        // dashboardPart.refresh();
    }
}
