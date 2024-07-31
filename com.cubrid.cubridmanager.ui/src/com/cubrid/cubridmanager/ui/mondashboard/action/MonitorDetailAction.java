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
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.BrokerMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DatabaseMonitorPart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.HANodePart;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.HostMonitorPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * Open monitor detail window.
 *
 * @author SC13425
 * @version 1.0 - 2010-6-11 created by SC13425
 */
public class MonitorDetailAction extends SelectionAction {

    public static final String ID = MonitorDetailAction.class.getName();

    /**
     * constructor.
     *
     * @param shell window.getShell()
     * @param text action text
     * @param icon ImageDescriptor
     */
    public MonitorDetailAction(Shell shell, String text, ImageDescriptor icon) {
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
    protected MonitorDetailAction(
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
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj Object
     * @return is supported.
     */
    public boolean isSupported(Object obj) {
        HostNode hostNode = null;
        if (obj instanceof HostMonitorPart) {
            hostNode = (HostNode) ((HostMonitorPart) obj).getModel();
        } else if (obj instanceof BrokerMonitorPart) {
            BrokerNode brokerNode = (BrokerNode) ((BrokerMonitorPart) obj).getModel();
            hostNode = brokerNode.getParent();
        }
        if (hostNode != null) {
            return hostNode.isConnected();
        }
        if (obj instanceof DatabaseMonitorPart) {
            DatabaseNode dbNode = (DatabaseNode) ((DatabaseMonitorPart) obj).getModel();
            hostNode = dbNode.getParent();
            return hostNode.isConnected() && dbNode.isConnected();
        }
        return false;
    }

    /** Open monitor detail windows. */
    public void run() {
        Object[] objArr = this.getSelectedObj();
        if (objArr == null || objArr.length <= 0 || !isSupported(objArr[0])) {
            setEnabled(false);
            return;
        }
        HANodePart part = null;
        if (objArr[0] instanceof HostMonitorPart) {
            part = (HostMonitorPart) objArr[0];
        } else if (objArr[0] instanceof DatabaseMonitorPart) {
            part = (DatabaseMonitorPart) objArr[0];
        } else if (objArr[0] instanceof BrokerMonitorPart) {
            part = (BrokerMonitorPart) objArr[0];
        }
        if (part != null) {
            SelectionRequest sr = new SelectionRequest();
            sr.setType(RequestConstants.REQ_OPEN);
            part.performRequest(sr);
        }
    }
}
