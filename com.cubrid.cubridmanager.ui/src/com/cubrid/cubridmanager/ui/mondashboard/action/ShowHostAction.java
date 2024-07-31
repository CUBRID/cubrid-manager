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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

/**
 * Show the figure which displays the selected broker's clients.
 *
 * @author SC13425
 * @version 1.0 - 2010-8-19 created by SC13425
 */
public class ShowHostAction extends SelectionAction {

    public static final String ID = ShowHostAction.class.getName();

    public ShowHostAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    protected ShowHostAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setChecked(false);
    }

    /**
     * not allow multi selctions
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     * @return boolean false
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Always support
     *
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     * @param obj Object
     * @return boolean support:true;not support:false;
     */
    public boolean isSupported(Object obj) {
        IStructuredSelection selection = (IStructuredSelection) getSelection();
        Iterator<?> iterator = selection.iterator();
        boolean isVisible = true;
        while (iterator.hasNext()) {
            Object objSelected = iterator.next();
            if (objSelected instanceof DatabaseMonitorPart) {
                DatabaseMonitorPart cmp = (DatabaseMonitorPart) objSelected;
                DatabaseNode dn = ((DatabaseNode) cmp.getModel());
                if (!dn.getParent().isVisible()) {
                    isVisible = false;
                    break;
                }
            } else if (objSelected instanceof BrokerMonitorPart) {
                BrokerMonitorPart cmp = (BrokerMonitorPart) objSelected;
                BrokerNode bn = ((BrokerNode) cmp.getModel());
                if (!bn.getParent().isVisible()) {
                    isVisible = false;
                    break;
                }
            }
        }
        setChecked(isVisible);
        return true;
    }

    /**
     * Create a new figure in dashboard.
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        IStructuredSelection selection = (IStructuredSelection) getSelection();
        Iterator<?> iterator = selection.iterator();
        List<HostNode> hosts = new ArrayList<HostNode>();
        EditPart root = null;
        while (iterator.hasNext()) {
            Object objSelected = iterator.next();
            if (objSelected instanceof DatabaseMonitorPart) {
                DatabaseMonitorPart cmp = (DatabaseMonitorPart) objSelected;
                DatabaseNode dn = ((DatabaseNode) cmp.getModel());
                if (!hosts.contains(dn.getParent())) {
                    hosts.add(dn.getParent());
                }
                root = cmp.getParent();
            } else if (objSelected instanceof BrokerMonitorPart) {
                BrokerMonitorPart cmp = (BrokerMonitorPart) objSelected;
                BrokerNode bn = ((BrokerNode) cmp.getModel());
                if (!hosts.contains(bn.getParent())) {
                    hosts.add(bn.getParent());
                }
                root = cmp.getParent();
            }
        }
        for (HostNode host : hosts) {
            if (host.isVisible() == isChecked()) {
                continue;
            }
            host.setVisible(isChecked());
        }
        if (root != null) {
            root.refresh();
        }
    }
}
