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

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.replication.editor.ReplicationEditor;
import com.cubrid.cubridmanager.ui.replication.editor.dialog.SetHostInfoDialog;
import com.cubrid.cubridmanager.ui.replication.editor.figures.ContainerNodeFigure;
import com.cubrid.cubridmanager.ui.replication.editor.model.ArrowConnection;
import com.cubrid.cubridmanager.ui.replication.editor.model.ContainerNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import com.cubrid.cubridmanager.ui.replication.editor.policies.ContainerNodeLayoutEditPolicy;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.widgets.Shell;

/**
 * Container node edit part is responsible to create container node figure according container node
 * model object
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class ContainerNodePart extends NodePart {

    public ContainerNodePart(ReplicationEditor editor) {
        super(editor);
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse.gef.Request)
     * @param req Request
     */
    public void performRequest(Request req) {
        if (req.getType() == RequestConstants.REQ_OPEN && getContainerNode() instanceof HostNode) {
            Shell shell = replEditor.getSite().getShell();
            SetHostInfoDialog dialog = new SetHostInfoDialog(shell);
            dialog.setHostInfo((HostNode) getContainerNode());
            dialog.setEditable(replEditor.isEditable());
            dialog.open();
        }
    }

    public IFigure getContentPane() {
        return ((ContainerNodeFigure) getFigure()).getAttributeFigure();
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     * @return new ContainerNodeFigure()
     */
    protected IFigure createFigure() {
        return new ContainerNodeFigure();
    }

    /** @see com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#refreshVisuals() */
    protected void refreshVisuals() {
        super.refreshVisuals();
        ContainerNodeFigure figure = (ContainerNodeFigure) getFigure();
        figure.setName(((Node) this.getModel()).getName());

        if (getContainerNode() instanceof HostNode) {
            figure.setIcon(CubridManagerUIPlugin.getImage("icons/navigator/host.png"));
        }
    }

    /** @see com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#createEditPolicies() */
    protected void createEditPolicies() {
        super.createEditPolicies();
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new ContainerNodeLayoutEditPolicy());
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     * @return get ChildNodeList of ContainerNode
     */
    protected List<LeafNode> getModelChildren() {
        return getContainerNode().getChildNodeList();
    }

    /** @see com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#activate() */
    public void activate() {
        super.activate();
        getContainerNode().addPropertyChangeListener(this);
    }

    /** @see com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#deactivate() */
    public void deactivate() {
        super.deactivate();
        getContainerNode().removePropertyChangeListener(this);
    }

    /**
     * @see
     *     com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#propertyChange(java.beans.PropertyChangeEvent)
     * @param evt PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (ContainerNode.PROP_STRUCTURE.equals(evt.getPropertyName())) {
            refreshChildren();
            replEditor.setDirty(true);
        }
        super.propertyChange(evt);
    }

    /**
     * get the container node
     *
     * @return model of ContainerNode
     */
    protected ContainerNode getContainerNode() {
        return (ContainerNode) getModel();
    }

    /**
     * @see
     *     com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#getModelSourceConnections()
     * @return list
     */
    protected List<ArrowConnection> getModelSourceConnections() {
        List<ArrowConnection> list = new ArrayList<ArrowConnection>();
        list.addAll(getContainerNode().getOutgoingConnections());
        for (Iterator<LeafNode> iter = getContainerNode().getChildNodeList().iterator();
                iter.hasNext(); ) {
            LeafNode node = iter.next();
            list.addAll(node.getOutgoingConnections());
        }
        return list;
    }

    /**
     * @see
     *     com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart#getModelTargetConnections()
     * @return list
     */
    protected List<ArrowConnection> getModelTargetConnections() {
        List<ArrowConnection> list = new ArrayList<ArrowConnection>();
        list.addAll(getContainerNode().getIncomingConnections());
        for (Iterator<LeafNode> iter = getContainerNode().getChildNodeList().iterator();
                iter.hasNext(); ) {
            LeafNode node = (LeafNode) iter.next();
            list.addAll(node.getIncomingConnections());
        }
        return list;
    }
}
