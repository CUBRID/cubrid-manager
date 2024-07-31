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
package com.cubrid.cubridmanager.ui.replication.editor.action;

import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.ReplicationEditor;
import com.cubrid.cubridmanager.ui.replication.editor.dialog.SetDistributorDbInfoDialog;
import com.cubrid.cubridmanager.ui.replication.editor.dialog.SetHostInfoDialog;
import com.cubrid.cubridmanager.ui.replication.editor.dialog.SetSlaveDbInfoDialog;
import com.cubrid.cubridmanager.ui.replication.editor.dialog.wizard.SetMasterDbInfoWizard;
import com.cubrid.cubridmanager.ui.replication.editor.model.DistributorNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.SlaveNode;
import com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This action is responsible to edit replication component in replication editor
 *
 * @author pangqiren
 * @version 1.0 - 2009-11-25 created by pangqiren
 */
public class EditAction extends SelectionAction {

    public static final String ID = EditAction.class.getName();

    public EditAction(IWorkbenchPart part) {
        super(part);
        setLazyEnablementCalculation(false);
    }

    /** @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init() */
    protected void init() {
        super.init();
        setText(Messages.editActionName);
        setToolTipText(Messages.editActionToolTip);
        setImageDescriptor(CubridManagerUIPlugin.getImageDescriptor("icons/action/property.png"));
        setId(ID);
        setEnabled(false);
    }

    /**
     * @see org.eclipse.gef.ui.actions.SelectionAction#calculateEnabled()
     * @return <code>true</code>if it is enabled;<code>false</code>otherwise
     */
    protected boolean calculateEnabled() {
        if (getSelectedObjects() == null || getSelectedObjects().size() != 1) {
            return false;
        }
        if (getSelectedObjects().get(0) instanceof NodePart) {
            return true;
        }
        return false;
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        IWorkbenchPart workbenchPart = this.getWorkbenchPart();
        if (!(workbenchPart instanceof ReplicationEditor) || !isEnabled()) {
            return;
        }
        ReplicationEditor replEditor = (ReplicationEditor) workbenchPart;
        Shell shell = replEditor.getSite().getShell();
        NodePart nodePart = (NodePart) getSelectedObjects().get(0);
        if (nodePart.getModel() instanceof MasterNode) {
            SetMasterDbInfoWizard wizard =
                    new SetMasterDbInfoWizard((MasterNode) nodePart.getModel());
            wizard.setEditable(replEditor.isEditable());
            CMWizardDialog dialog = new CMWizardDialog(shell, wizard);
            dialog.setPageSize(560, 300);
            dialog.open();
        } else if (nodePart.getModel() instanceof DistributorNode) {
            SetDistributorDbInfoDialog dialog = new SetDistributorDbInfoDialog(shell);
            dialog.setDistributor((DistributorNode) nodePart.getModel());
            dialog.setEditable(replEditor.isEditable());
            dialog.open();
        } else if (nodePart.getModel() instanceof SlaveNode) {
            SetSlaveDbInfoDialog dialog = new SetSlaveDbInfoDialog(shell);
            dialog.setSlave((SlaveNode) nodePart.getModel());
            dialog.setEditable(replEditor.isEditable());
            dialog.open();
        } else if (nodePart.getModel() instanceof HostNode) {
            SetHostInfoDialog dialog = new SetHostInfoDialog(shell);
            dialog.setHostInfo((HostNode) nodePart.getModel());
            dialog.setEditable(replEditor.isEditable());
            dialog.open();
        }
    }
}
