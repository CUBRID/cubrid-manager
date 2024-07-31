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
import com.cubrid.cubridmanager.ui.replication.editor.model.ContainerNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Diagram;
import com.cubrid.cubridmanager.ui.replication.editor.policies.DiagramLayoutEditPolicy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * The diagram edit part is responsible to create the figure according diagram model object
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class DiagramPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

    private final ReplicationEditor replEditor;

    public DiagramPart(ReplicationEditor editor) {
        replEditor = editor;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#getCommand(org.eclipse.gef.Request)
     * @param request Request
     * @return null or command
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

    /**
     * Returns a <code>List</code> containing the children model objects. If this EditPart's model
     * is a container, this method should be overridden to returns its children. This is what causes
     * children EditParts to be created.
     *
     * <p>Callers must not modify the returned List. Must not return <code>null</code>.
     *
     * @return the List of children
     */
    protected List<ContainerNode> getModelChildren() {
        return ((Diagram) this.getModel()).getChildNodeList();
    }

    /** @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate() */
    public void activate() {
        super.activate();
        ((Diagram) getModel()).addPropertyChangeListener(this);
    }

    /** @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate() */
    public void deactivate() {
        super.deactivate();
        ((Diagram) getModel()).removePropertyChangeListener(this);
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has
     *     changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (Diagram.PROP_STRUCTURE.equals(prop)) {
            refreshChildren();
            replEditor.setDirty(true);
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     * @return figure
     */
    protected IFigure createFigure() {
        Figure figure = new FreeformLayer();
        figure.setLayoutManager(new FreeformLayout());
        return figure;
    }

    /** @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies() */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
    }
}
