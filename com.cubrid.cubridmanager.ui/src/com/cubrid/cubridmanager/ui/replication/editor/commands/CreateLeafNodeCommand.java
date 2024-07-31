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
import com.cubrid.cubridmanager.ui.replication.editor.model.DistributorNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.SlaveNode;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

/**
 * This command is responsible to create leafNode(master,distributor,slave) in the graphicViewer of
 * the replication editor
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class CreateLeafNodeCommand extends Command {

    private ContainerNode containerNode;
    private LeafNode leafNode;
    private Point location;

    public void setLeafNode(LeafNode node) {
        this.leafNode = node;
    }

    public void setContainerNode(ContainerNode node) {
        this.containerNode = node;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    /** @see org.eclipse.gef.commands.Command#execute() */
    public void execute() {
        if (this.leafNode instanceof MasterNode) {
            leafNode.setName("Master" + (getSize(MasterNode.class.getName()) + 1));
        }
        if (this.leafNode instanceof DistributorNode) {
            leafNode.setName("Dist" + (getSize(DistributorNode.class.getName()) + 1));
        }
        if (this.leafNode instanceof SlaveNode) {
            leafNode.setName("Slave" + (getSize(SlaveNode.class.getName()) + 1));
        }
        leafNode.setLocation(this.location);
        containerNode.addChildNode(leafNode);
    }

    /**
     * get the size
     *
     * @param name String
     * @return size
     */
    public int getSize(String name) {
        int count = containerNode.getChildNodeList().size();
        int size = 0;
        for (int i = 0; i < count; i++) {
            LeafNode node = (LeafNode) containerNode.getChildNodeList().get(i);
            if (node.getClass().getName().equals(name)) {
                size++;
            }
        }
        return size;
    }

    /**
     * get the label
     *
     * @return label name
     */
    public String getLabel() {
        if (leafNode == null) {
            return "Create Node";
        } else {
            return "Create " + leafNode.getName();
        }
    }

    /** @see org.eclipse.gef.commands.Command#redo() */
    public void redo() {
        execute();
    }

    /** @see org.eclipse.gef.commands.Command#undo() */
    public void undo() {
        containerNode.removeChildNode(leafNode);
    }
}
