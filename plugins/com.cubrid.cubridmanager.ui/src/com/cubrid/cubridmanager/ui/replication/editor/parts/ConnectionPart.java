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
import com.cubrid.cubridmanager.ui.replication.editor.policies.ConnectionEditPolicy;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

/**
 * Connection edit part is responsible to create connection figure according connection model object
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class ConnectionPart extends AbstractConnectionEditPart {

    protected ReplicationEditor replEditor = null;

    public ConnectionPart(ReplicationEditor editor) {
        replEditor = editor;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#createFigure()
     * @return conn
     */
    protected IFigure createFigure() {
        PolylineConnection conn = new PolylineConnection();
        conn.setTargetDecoration(new PolygonDecoration());
        conn.setConnectionRouter(new BendpointConnectionRouter());
        return conn;
    }

    /** @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies() */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new ConnectionEditPolicy());
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#getCommand(org.eclipse.gef.Request)
     * @param request Request
     * @return null or command
     */
    public Command getCommand(Request request) {
        if (replEditor.isEditable()) {
            return super.getCommand(request);
        }
        return null;
    }

    /** @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals() */
    protected void refreshVisuals() {
        // empty
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#setSelected(int)
     * @param value int
     */
    public void setSelected(int value) {
        super.setSelected(value);
        if (value == EditPart.SELECTED_NONE) {
            ((PolylineConnection) getFigure()).setLineWidth(1);
        } else {
            ((PolylineConnection) getFigure()).setLineWidth(2);
        }
    }
}
