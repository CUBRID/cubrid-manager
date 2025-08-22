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
package com.cubrid.cubridmanager.ui.replication.editor.commands;

import com.cubrid.cubridmanager.ui.replication.editor.model.ArrowConnection;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import org.eclipse.gef.commands.Command;

/**
 * This command is responsible to delete connection
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class DeleteConnectionCommand extends Command {

    private Node source;
    private Node target;
    private ArrowConnection connection;

    public void setConnection(ArrowConnection connection) {
        this.connection = connection;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    /** @see org.eclipse.gef.commands.Command#execute() */
    public void execute() {
        source.removeOutput(connection);
        target.removeInput(connection);
        connection.setSource(null);
        connection.setTarget(null);
    }

    /**
     * get the label
     *
     * @return label name
     */
    public String getLabel() {
        return "Delete Connection";
    }

    /** @see org.eclipse.gef.commands.Command#redo() */
    public void redo() {
        execute();
    }

    /** @see org.eclipse.gef.commands.Command#undo() */
    public void undo() {
        connection.setSource(source);
        connection.setTarget(target);
        source.addOutput(connection);
        target.addInput(connection);
    }

    /**
     * @see org.eclipse.gef.commands.Command#canExecute()
     * @return boolean
     */
    public boolean canExecute() {
        return !connection.isSaved();
    }
}
