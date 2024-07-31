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
import com.cubrid.cubridmanager.ui.replication.editor.model.DistributorNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import com.cubrid.cubridmanager.ui.replication.editor.model.SlaveNode;
import java.util.List;
import org.eclipse.gef.commands.Command;

/**
 * This command is responsible to create connection
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class ConnectionCreateCommand extends Command {

    protected ArrowConnection connection;
    protected Node source;
    protected Node target;

    public void setSource(Node source) {
        this.source = source;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    /** @see org.eclipse.gef.commands.Command#execute() */
    public void execute() {
        connection = new ArrowConnection(source, target);
    }

    public String getLabel() {
        return "Create Connection";
    }

    /** @see org.eclipse.gef.commands.Command#redo() */
    public void redo() {
        this.source.addOutput(this.connection);
        this.target.addInput(this.connection);
    }

    /** @see org.eclipse.gef.commands.Command#undo() */
    public void undo() {
        this.source.removeOutput(this.connection);
        this.target.removeInput(this.connection);
    }

    /**
     * check connection validility,it must satisfy the below conditions (1)if source is
     * master,target must be distributor and they are from different host (2)if source is
     * ditributor,target must be slave and they are from the same host and only can connect one
     * slave(3)if source is slave,target must be distributor and they are from different host(4)if
     * target is distributor,the number of source is 1 at most(5)if target is slave,the number of
     * source is 1 at most
     *
     * @return boolean
     */
    public boolean canExecute() {
        if (source.equals(target)) {
            return false;
        }
        if (!(source instanceof LeafNode)) {
            return false;
        }
        if (!(target instanceof LeafNode)) {
            return false;
        }
        boolean isCanExecute = false;

        if (source instanceof MasterNode) {
            isCanExecute =
                    target instanceof DistributorNode
                            && !source.getParent().equals(target.getParent());
        } else if (source instanceof DistributorNode) {
            isCanExecute =
                    target instanceof SlaveNode
                            && source.getParent().equals(target.getParent())
                            && (source.getOutgoingConnections() == null
                                    || source.getOutgoingConnections().size() == 0);
        } else if (source instanceof SlaveNode) {
            isCanExecute =
                    target instanceof DistributorNode
                            && !source.getParent().equals(target.getParent())
                            && !source.getParent().equals(target.getParent());
        }
        if (!isCanExecute) {
            return false;
        }
        if (target instanceof DistributorNode) {
            isCanExecute =
                    target.getIncomingConnections() == null
                            || target.getIncomingConnections().size() == 0;
        } else if (target instanceof SlaveNode) {
            isCanExecute =
                    target.getIncomingConnections() == null
                            || target.getIncomingConnections().size() == 0;
        }
        if (!isCanExecute) {
            return false;
        }
        List<ArrowConnection> connectionList = this.source.getOutgoingConnections();
        for (int i = 0; i < connectionList.size(); i++) {
            if (connectionList.get(i).getTarget().equals(target)) {
                return false;
            }
        }
        return true;
    }
}
