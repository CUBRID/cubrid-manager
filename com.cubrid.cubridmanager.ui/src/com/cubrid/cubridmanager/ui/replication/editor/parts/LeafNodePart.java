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
package com.cubrid.cubridmanager.ui.replication.editor.parts;

import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.replication.editor.ReplicationEditor;
import com.cubrid.cubridmanager.ui.replication.editor.dialog.SetDistributorDbInfoDialog;
import com.cubrid.cubridmanager.ui.replication.editor.dialog.SetSlaveDbInfoDialog;
import com.cubrid.cubridmanager.ui.replication.editor.dialog.wizard.SetMasterDbInfoWizard;
import com.cubrid.cubridmanager.ui.replication.editor.figures.LeafNodeFigure;
import com.cubrid.cubridmanager.ui.replication.editor.model.ArrowConnection;
import com.cubrid.cubridmanager.ui.replication.editor.model.ContainerNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.DistributorNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.SlaveNode;
import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.widgets.Shell;

/**
 * The leafnode edit part is responsible to create the figure according leafnode model object
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class LeafNodePart extends NodePart {

    public LeafNodePart(ReplicationEditor editor) {
        super(editor);
    }

    /**
     * Creates the <code>Figure</code> to be used as this part's <i>visuals</i>. This is called from
     * {@link #getFigure()} if the figure has not been created.
     *
     * @return a Figure
     */
    protected IFigure createFigure() {
        return new LeafNodeFigure();
    }

    /** @see com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#refreshVisuals() */
    protected void refreshVisuals() {
        super.refreshVisuals();
        LeafNode node = (LeafNode) getModel();
        LeafNodeFigure figure = (LeafNodeFigure) getFigure();
        figure.setText(node.getName());

        if (getModel() instanceof MasterNode) {
            figure.setIcon(CubridManagerUIPlugin.getImage("icons/replication/master.png"));
        }
        if (getModel() instanceof DistributorNode) {
            figure.setIcon(CubridManagerUIPlugin.getImage("icons/replication/distributor.gif"));
        }
        if (getModel() instanceof SlaveNode) {
            figure.setIcon(CubridManagerUIPlugin.getImage("icons/replication/slave.png"));
        }
    }

    /** @see com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#activate() */
    public void activate() {
        super.activate();
        ((LeafNode) getModel()).addPropertyChangeListener(this);
        ((ContainerNode) getParent().getModel()).addPropertyChangeListener(this);
    }

    /** @see com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#deactivate() */
    public void deactivate() {
        super.deactivate();
        ((LeafNode) getModel()).removePropertyChangeListener(this);
        ((ContainerNode) getParent().getModel()).removePropertyChangeListener(this);
    }

    /**
     * @see com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#getModelSo
     * @return the List of model source connections
     */
    protected List<ArrowConnection> getModelSourceConnections() {
        LeafNode leafNode = (LeafNode) getModel();
        return leafNode.getOutgoingConnections();
    }

    /**
     * @see
     *     com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#getModelTargetConnections()
     * @return the List of model target connections
     */
    protected List<ArrowConnection> getModelTargetConnections() {
        LeafNode node = (LeafNode) getModel();
        return node.getIncomingConnections();
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse.gef.Request)
     * @param req the request to be performed
     */
    public void performRequest(Request req) {
        Shell shell = replEditor.getSite().getShell();
        if (req.getType() == RequestConstants.REQ_OPEN) {
            if (getModel() instanceof MasterNode) {
                SetMasterDbInfoWizard wizard = new SetMasterDbInfoWizard((MasterNode) getModel());
                wizard.setEditable(replEditor.isEditable());
                CMWizardDialog dialog = new CMWizardDialog(shell, wizard);
                dialog.setPageSize(560, 300);
                dialog.open();
            } else if (getModel() instanceof DistributorNode) {
                SetDistributorDbInfoDialog dialog = new SetDistributorDbInfoDialog(shell);
                dialog.setDistributor((DistributorNode) getModel());
                dialog.setEditable(replEditor.isEditable());
                dialog.open();
            } else if (getModel() instanceof SlaveNode) {
                SetSlaveDbInfoDialog dialog = new SetSlaveDbInfoDialog(shell);
                dialog.setSlave((SlaveNode) getModel());
                dialog.setEditable(replEditor.isEditable());
                dialog.open();
            }
        }
    }
}
