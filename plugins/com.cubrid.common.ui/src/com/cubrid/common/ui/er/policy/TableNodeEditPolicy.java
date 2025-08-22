/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.policy;

import com.cubrid.common.ui.er.commands.CreateRelationshipCommand;
import com.cubrid.common.ui.er.commands.ReconnectForeignKeyCommand;
import com.cubrid.common.ui.er.commands.ReconnectPrimaryKeyCommand;
import com.cubrid.common.ui.er.model.Relationship;
import com.cubrid.common.ui.er.part.TablePart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

/**
 * Handles manipulation of relationships between tables
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class TableNodeEditPolicy extends GraphicalNodeEditPolicy {
    /** @see GraphicalNodeEditPolicy#getConnectionCreateCommand(CreateConnectionRequest) */
    @Override
    protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
        CreateRelationshipCommand cmd = new CreateRelationshipCommand();
        TablePart part = (TablePart) getHost();
        cmd.setForeignTable(part.getTable());
        request.setStartCommand(cmd);
        return cmd;
    }

    /** @see GraphicalNodeEditPolicy#getConnectionCompleteCommand(CreateConnectionRequest) */
    @Override
    protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
        CreateRelationshipCommand cmd = (CreateRelationshipCommand) request.getStartCommand();
        TablePart part = (TablePart) request.getTargetEditPart();
        cmd.setPrimaryTable(part.getTable());
        return cmd;
    }

    /** @see GraphicalNodeEditPolicy#getReconnectSourceCommand(ReconnectRequest) */
    @Override
    protected Command getReconnectSourceCommand(ReconnectRequest request) {
        ReconnectForeignKeyCommand cmd = new ReconnectForeignKeyCommand();
        cmd.setRelationship((Relationship) request.getConnectionEditPart().getModel());
        TablePart tablePart = (TablePart) getHost();
        cmd.setSourceForeignKey(tablePart.getTable());
        return cmd;
    }

    /** @see GraphicalNodeEditPolicy#getReconnectTargetCommand(ReconnectRequest) */
    @Override
    protected Command getReconnectTargetCommand(ReconnectRequest request) {
        ReconnectPrimaryKeyCommand cmd = new ReconnectPrimaryKeyCommand();
        cmd.setRelationship((Relationship) request.getConnectionEditPart().getModel());
        TablePart tablePart = (TablePart) getHost();
        cmd.setTargetPrimaryKey(tablePart.getTable());
        return cmd;
    }
}
