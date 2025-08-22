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

import com.cubrid.cubridmanager.ui.replication.editor.model.ContainerNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Diagram;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import org.eclipse.gef.commands.Command;

/**
 * This command is responsible to delete replication component in the graphicView of the replication
 * editor
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class DeleteNodeCommand extends Command {

    private Object parent;
    private Node node;

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    /** @see org.eclipse.gef.commands.Command#execute() */
    public void execute() {
        if (parent instanceof Diagram) {
            Diagram diagram = (Diagram) parent;
            diagram.removeNode((ContainerNode) node);
        } else if (parent instanceof ContainerNode) {
            ContainerNode containerNode = (ContainerNode) parent;
            containerNode.removeChildNode((LeafNode) node);
        }
    }

    /**
     * get the label
     *
     * @return label name
     */
    public String getLabel() {
        if (node == null) {
            return "Delete Node";
        } else {
            return "Delete " + node.getName();
        }
    }

    /** @see org.eclipse.gef.commands.Command#redo() */
    public void redo() {
        execute();
    }

    /** @see org.eclipse.gef.commands.Command#undo() */
    public void undo() {
        if (parent instanceof Diagram) {
            Diagram diagram = (Diagram) parent;
            diagram.addNode((ContainerNode) node);
        } else if (parent instanceof ContainerNode) {
            ContainerNode containerNode = (ContainerNode) parent;
            containerNode.addChildNode((LeafNode) node);
        }
    }
}
