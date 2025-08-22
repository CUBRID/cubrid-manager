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
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.mondashboard.dialog.DatabaseLogListDialog;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DatabaseMonitorPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Open the database log
 *
 * @author SC13425
 * @version 1.0 - 2010-6-10 created by SC13425
 */
public class OpenDatabaseLogAction extends SelectionAction {

    public static final String ID = OpenDatabaseLogAction.class.getName();

    /**
     * constructor.
     *
     * @param shell window.getShell()
     * @param text action text
     * @param icon ImageDescriptor
     */
    public OpenDatabaseLogAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * constructor.
     *
     * @param shell window.getShell()
     * @param provider ISelectionProvider
     * @param text action text
     * @param icon ImageDescriptor
     */
    protected OpenDatabaseLogAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * not allow multi selections
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return boolean false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * only support database monitor and database must be connected.
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj Object must be DatabaseMonitorPart
     * @return is supported.
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof DatabaseMonitorPart) {
            DatabaseMonitorPart dbMonPart = (DatabaseMonitorPart) obj;
            DatabaseNode dbNode = (DatabaseNode) dbMonPart.getModel();
            HostNode hostNode = dbNode.getParent();
            if (hostNode != null
                    && hostNode.getServerInfo() != null
                    && hostNode.getServerInfo().isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * open database log
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0 || !isSupported(objArr[0])) {
            setEnabled(false);
            return;
        }
        DatabaseMonitorPart dbMonPart = (DatabaseMonitorPart) objArr[0];
        DatabaseNode dbNode = (DatabaseNode) dbMonPart.getModel();
        String dbName = dbNode.getDbName();

        HostNode hostNode = dbNode.getParent();
        ServerInfo serverInfo = hostNode.getServerInfo();
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        DatabaseLogListDialog dialog = new DatabaseLogListDialog(shell, dbName, serverInfo);
        dialog.open();
    }
}
