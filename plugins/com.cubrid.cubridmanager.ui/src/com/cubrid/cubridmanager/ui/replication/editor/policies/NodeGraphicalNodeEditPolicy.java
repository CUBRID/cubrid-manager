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
package com.cubrid.cubridmanager.ui.replication.editor.policies;

import com.cubrid.cubridmanager.ui.replication.editor.commands.ConnectionCreateCommand;
import com.cubrid.cubridmanager.ui.replication.editor.commands.ReconnectSourceCommand;
import com.cubrid.cubridmanager.ui.replication.editor.model.ArrowConnection;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

/**
 * Edit replication component policy
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class NodeGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

    /**
     * @see
     *     org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
     * @param request the CreateConnectionRequest
     * @return the complete command to create a connection
     */
    protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
        ConnectionCreateCommand command = (ConnectionCreateCommand) request.getStartCommand();
        command.setTarget((Node) getHost().getModel());
        return command;
    }

    /**
     * @see
     *     org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
     * @param request the CreateConnectionRequest
     * @see #getConnectionCompleteCommand(CreateConnectionRequest)
     * @return a Command representing half of a connection creation
     */
    protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
        ConnectionCreateCommand command = new ConnectionCreateCommand();
        command.setSource((Node) getHost().getModel());
        request.setStartCommand(command);
        return command;
    }

    /**
     * @see
     *     org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
     * @param request the ReconnectRequest
     * @return a Command
     */
    protected Command getReconnectSourceCommand(ReconnectRequest request) {
        ReconnectSourceCommand cmd = new ReconnectSourceCommand();
        cmd.setConnection((ArrowConnection) request.getConnectionEditPart().getModel());
        cmd.setSource((Node) getHost().getModel());
        return cmd;
    }

    /**
     * @see
     *     org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
     * @param request the ReconnectRequest
     * @return a Command
     */
    protected Command getReconnectTargetCommand(ReconnectRequest request) {
        return null;
    }
}
