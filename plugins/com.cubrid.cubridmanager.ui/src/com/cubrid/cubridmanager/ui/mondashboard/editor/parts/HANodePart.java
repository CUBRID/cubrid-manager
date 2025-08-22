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
package com.cubrid.cubridmanager.ui.mondashboard.editor.parts;

import com.cubrid.cubridmanager.ui.mondashboard.editor.figure.AbstractMonitorFigure;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANodeConnection;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;

/**
 * HANode's edit part base class.Model must be HANode or it's subclasses.
 *
 * @author SC13425
 * @version 1.0 - 2010-6-10 created by SC13425
 */
public abstract class HANodePart extends AbstractMonitorEditPart implements NodeEditPart {

    private boolean minimized = false;

    /**
     * get model's source connection models.
     *
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
     * @return List of HANodeConnection
     */
    protected List<HANodeConnection> getModelSourceConnections() {
        return ((HANode) getModel()).getOutgoingConnections();
    }

    /**
     * get model's target connections models.
     *
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
     * @return List of HANodeConnection
     */
    protected List<HANodeConnection> getModelTargetConnections() {
        return ((HANode) getModel()).getIncomingConnections();
    }

    /**
     * Refreshes this EditPart's <i>visuals</i>. This method is called by {@link #refresh()}, and
     * may also be called in response to notifications from the model.
     */
    protected void refreshVisuals() {
        HANode node = (HANode) getModel();
        Point loc = node.getLocation();
        ((AbstractMonitorFigure) getFigure()).setMinimized(isMinimized());
        // figure's size is stable
        Rectangle rectangle = new Rectangle(loc, figure.getSize());
        GraphicalEditPart graphicalEditPart = (GraphicalEditPart) getParent();
        graphicalEditPart.setLayoutConstraint(this, getFigure(), rectangle);
        getFigure().repaint();
    }

    /**
     * Method to be executed when model's property changed. default is location changed.
     *
     * @param evt PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (HANode.PROP_NAME.equals(evt.getPropertyName())) {
            refreshVisuals();
        } else if (HANode.PROP_LOCATION.equals(evt.getPropertyName())) {
            refreshVisuals();
        } else if (HANode.PROP_ADD_INPUTS.equals(evt.getPropertyName())) {
            this.refreshTargetConnections();
        } else if (HANode.PROP_ADD_OUTPUTS.equals(evt.getPropertyName())) {
            this.refreshSourceConnections();
        } else if (HANode.PROP_REMOVE_INPUTS.equals(evt.getPropertyName())) {
            this.refreshTargetConnections();
        } else if (HANode.PROP_REMOVE_OUTPUTS.equals(evt.getPropertyName())) {
            this.refreshSourceConnections();
        }
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
     */
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * Returns the <i>source</i> <code>ConnectionAnchor</code> for the specified Request. The
     * returned ConnectionAnchor is used only when displaying <i>feedback</i>. The Request is
     * usually a {@link org.eclipse.gef.requests.LocationRequest}, which provides the current mouse
     * location.
     *
     * @param request a Request describing the current interaction
     * @return the ConnectionAnchor to use during feedback
     */
    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * Returns the <code>ConnectionAnchor</code> for the specified <i>target</i> connection. This
     * NodeEditPart is the {@link ConnectionEditPart#getTarget() target} EditPart for the given
     * connection.
     *
     * <p>The anchor may be a function of the connection's model, the node's model, a combination of
     * both, or it may not depend on anything all.
     *
     * @param connection the ConnectionEditPart
     * @return the ConnectionAnchor for the given ConnectionEditPart
     */
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
        /*if (connection instanceof Host2ChildConnectionPart) {
        	return new DatabaseNodeAnchor(getFigure());
        }*/
        return new ChopboxAnchor(getFigure());
    }

    /**
     * Returns the <i>target</i> <code>ConnectionAnchor</code> for the specified Request. The
     * returned ConnectionAnchor is used only when displaying <i>feedback</i>. The Request is
     * usually a {@link org.eclipse.gef.requests.LocationRequest}, which provides the current mouse
     * location.
     *
     * @param request a Request describing the current interaction
     * @return the ConnectionAnchor to use during feedback
     */
    public ConnectionAnchor getTargetConnectionAnchor(Request request) {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * Get figure is minimized.
     *
     * @return the minimized
     */
    public boolean isMinimized() {
        return minimized;
    }

    /**
     * Set figure minimized status.
     *
     * @param minimized the minimized to set
     */
    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }
}
