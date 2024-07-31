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
package com.cubrid.cubridmanager.ui.mondashboard.action;

import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.cubridmanager.ui.common.navigator.CubridMonitorNavigatorView;
import com.cubrid.cubridmanager.ui.mondashboard.dialog.AddDashboardDialog;
import com.cubrid.cubridmanager.ui.mondashboard.editor.MonitorDashboardEditor;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.persist.MonitorDashboardPersistManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Add Monitor Dashboard action
 *
 * @author pangqiren
 * @version 1.0 - 2010-5-27 created by pangqiren
 */
public class AddMonitorDashboardAction extends SelectionAction {

    public static final String ID = AddMonitorDashboardAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public AddMonitorDashboardAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public AddMonitorDashboardAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Return whether this action support to select multi object,if not support,this action will be
     * disabled
     *
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Handle with selection changed object to determine this action's enabled status
     *
     * @param selection the ISelection object
     */
    protected void selectionChanged(ISelection selection) {
        setEnabled(true);
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        return true;
    }

    /** Open the dialog */
    public void run() {
        AddDashboardDialog dialog = new AddDashboardDialog(getShell());
        int returnCode = dialog.open();
        if (returnCode == IDialogConstants.OK_ID) {
            Dashboard dashboard = dialog.getDashboard();
            DefaultCubridNode dashboardNode =
                    new DefaultCubridNode(
                            dashboard.getName(),
                            dashboard.getName(),
                            "icons/navigator/status_item.png");
            dashboardNode.setType(CubridNodeType.MONITOR_DASHBOARD);
            dashboardNode.setEditorId(MonitorDashboardEditor.ID);
            dashboardNode.setModelObj(dashboard);
            MonitorDashboardPersistManager.getInstance().addMonitorDashboard(dashboardNode);
            ISelectionProvider provider = getSelectionProvider();
            if (provider instanceof TreeViewer) {
                TreeViewer treeViewer = (TreeViewer) provider;
                /*TOOLS-3665 Refresh the input of TreeViewer*/
                treeViewer.setInput(CubridMonitorNavigatorView.getTreeViewerInput());
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        dashboardNode, CubridNodeChangedEventType.NODE_ADD));
                LayoutManager.getInstance().getWorkbenchContrItem().openEditorOrView(dashboardNode);
            }
        }
    }
}
