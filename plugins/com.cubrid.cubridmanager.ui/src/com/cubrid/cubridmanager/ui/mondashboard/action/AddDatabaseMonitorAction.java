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
import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.ui.mondashboard.dialog.wizard.AddHostAndDbWizard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.Dashboard;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.HostMonitorPart;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * Add database monitors of selected host monitor
 *
 * @author SC13425
 * @version 1.0 - 2010-6-10 created by SC13425
 */
public class AddDatabaseMonitorAction extends SelectionAction {

    public static final String ID = AddDatabaseMonitorAction.class.getName();

    /**
     * AddDatabaseMonitorAction constructor.
     *
     * @param shell window.getShell()
     * @param text action text
     * @param icon ImageDescriptor
     */
    public AddDatabaseMonitorAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * AddDatabaseMonitorAction constructor
     *
     * @param shell Shell
     * @param provider ISelectionProvider
     * @param text String
     * @param icon ImageDescriptor
     */
    protected AddDatabaseMonitorAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * not allow multi selctions
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return boolean false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * selection is Host node host is connected
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj Object
     * @return boolean support:true;not support:false;
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof HostMonitorPart) {
            HostMonitorPart hostMonPart = (HostMonitorPart) obj;
            HostNode hostNode = (HostNode) hostMonPart.getModel();
            if (hostNode == null
                    || hostNode.getServerInfo() == null
                    || hostNode.getServerInfo().getLoginedUserInfo() == null) {
                return false;
            }
            ServerType type = hostNode.getServerInfo().getServerType();
            return type == ServerType.BOTH || type == ServerType.DATABASE;
        }
        return false;
    }

    /**
     * open add database monitor dialog.
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0 || !isSupported(objArr[0])) {
            setEnabled(false);
            return;
        }
        HostMonitorPart hostMonPart = (HostMonitorPart) objArr[0];
        HostNode hostNode = (HostNode) hostMonPart.getModel();
        Dashboard dashboard = (Dashboard) hostMonPart.getParent().getModel();
        if (hostNode != null && dashboard != null) {
            AddHostAndDbWizard wizard =
                    new AddHostAndDbWizard(hostNode, dashboard.getHostNodeList(), 1);
            CMWizardDialog dialog = new CMWizardDialog(getShell(), wizard);
            dialog.setPageSize(660, 380);
            int returnCode = dialog.open();
            if (returnCode == IDialogConstants.OK_ID) {
                List<HostNode> addedHostNodeList = wizard.getAddedHostNodeList();
                HAUtil.mergeHostNode(dashboard, addedHostNodeList);
                HAUtil.calcLocation(dashboard.getHostNodeList());
            }
        }
    }
}
