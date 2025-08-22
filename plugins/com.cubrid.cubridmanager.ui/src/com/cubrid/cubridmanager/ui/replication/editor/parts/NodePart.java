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

import com.cubrid.cubridmanager.ui.replication.editor.ReplicationEditor;
import com.cubrid.cubridmanager.ui.replication.editor.model.ArrowConnection;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import com.cubrid.cubridmanager.ui.replication.editor.policies.NodeEditPolicy;
import com.cubrid.cubridmanager.ui.replication.editor.policies.NodeGraphicalNodeEditPolicy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * The diagram edit part is responsible to create the figure according node model object
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public abstract class NodePart extends AbstractGraphicalEditPart
        implements PropertyChangeListener, NodeEditPart {

    protected ReplicationEditor replEditor = null;

    public NodePart(ReplicationEditor editor) {
        replEditor = editor;
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has
     *     changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Node.PROP_NAME)) {
            refreshVisuals();
            replEditor.setDirty(true);
        }
        if (evt.getPropertyName().equals(Node.PROP_LOCATION)) {
            refreshVisuals();
        } else if (evt.getPropertyName().equals(Node.PROP_SIZE)) {
            refreshVisuals();
        } else if (evt.getPropertyName().equals(Node.PROP_INPUTS)) {
            refreshTargetConnections();
            replEditor.setDirty(true);
        } else if (evt.getPropertyName().equals(Node.PROP_OUTPUTS)) {
            refreshSourceConnections();
            replEditor.setDirty(true);
        }
    }

    /** @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies() */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeGraphicalNodeEditPolicy());
    }

    /**
     * Subclasses should rarely extend this method. The default implementation combines the
     * contributions from each installed <code>EditPolicy</code>. This method is implemented
     * indirectly using EditPolicies.
     *
     * <p>
     *
     * <table>
     * <tr>
     * <td><img src="../doc-files/important.gif"/>
     * <td>It is recommended that Command creation be handled by EditPolicies,
     * and not directly by the EditPart.
     * </tr>
     * </table>
     *
     * @see EditPart#getCommand(Request)
     * @see EditPolicy#getCommand(Request)
     * @param request the Request
     * @return a Command
     */
    public Command getCommand(Request request) {
        if (!replEditor.isEditable()) {
            if (request.getType() == RequestConstants.REQ_RESIZE
                    || request.getType() == RequestConstants.REQ_RESIZE_CHILDREN
                    || request.getType() == RequestConstants.REQ_MOVE
                    || request.getType() == RequestConstants.REQ_MOVE_CHILDREN
                    || request.getType() == RequestConstants.REQ_ALIGN
                    || request.getType() == RequestConstants.REQ_ALIGN_CHILDREN) {
                return super.getCommand(request);
            } else {
                return null;
            }
        }
        return super.getCommand(request);
    }

    /** @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate() */
    public void activate() {
        if (isActive()) {
            return;
        }
        super.activate();
    }

    /** @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate() */
    public void deactivate() {
        if (!isActive()) {
            return;
        }
        super.deactivate();
    }

    /** @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals() */
    protected void refreshVisuals() {
        Node node = (Node) getModel();
        Point loc = node.getLocation();
        Dimension size = new Dimension(node.getSize());
        Rectangle rectangle = new Rectangle(loc, size);
        ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), rectangle);
    }

    /**
     * Returns the <code>ConnectionAnchor</code> for the specified <i>source</i> connection. This
     * NodeEditPart is the {@link ConnectionEditPart#getSource() source} EditPart for the given
     * connection.
     *
     * <p>The anchor may be a function of the connection's model, the node's model, a combination of
     * both, or it may not depend on anything all.
     *
     * @param connection the ConnectionEditPart
     * @return the ConnectionAnchor for the given ConnectionEditPart
     * @see
     *     org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
     * @param request a Request describing the current interaction
     * @return the ConnectionAnchor to use during feedback
     */
    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * @see
     *     org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     * @param connection the ConnectionEditPart
     * @return the ConnectionAnchor for the given ConnectionEditPart
     */
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
     * @param request a Request describing the current interaction
     * @return the ConnectionAnchor to use during feedback
     */
    public ConnectionAnchor getTargetConnectionAnchor(Request request) {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
     * @return the List of model source connections
     */
    protected List<ArrowConnection> getModelSourceConnections() {
        return ((Node) this.getModel()).getOutgoingConnections();
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
     * @return the List of model target connections
     */
    protected List<ArrowConnection> getModelTargetConnections() {
        return ((Node) this.getModel()).getIncomingConnections();
    }
}
