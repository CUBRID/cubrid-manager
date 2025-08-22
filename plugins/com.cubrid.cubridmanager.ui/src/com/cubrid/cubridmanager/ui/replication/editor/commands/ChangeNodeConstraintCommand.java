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

import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

/**
 * This command is responsible to change replication component size and position
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class ChangeNodeConstraintCommand extends Command {

    private Node node;
    private Point oldPos;
    private Point newPos;
    private Dimension oldSize;
    private Dimension newSize;

    /**
     * set the location
     *
     * @param point Composite
     */
    public void setLocation(Point point) {
        oldPos = this.node.getLocation();
        this.newPos = point;
    }

    /**
     * set the Dimension
     *
     * @param size Dimension
     */
    public void setDimension(Dimension size) {
        oldSize = this.node.getSize();
        this.newSize = size;
    }

    /**
     * set the Node
     *
     * @param node Node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /** set the location & set the size */
    public void execute() {
        node.setLocation(newPos);
        node.setSize(newSize);
    }

    /**
     * get the label
     *
     * @return label
     */
    public String getLabel() {
        if (node == null) {
            return "Move/Resize";
        } else {
            return "Move/Resize " + node.getName();
        }
    }

    /** @see org.eclipse.gef.commands.Command#redo() */
    public void redo() {
        execute();
    }

    /** @see org.eclipse.gef.commands.Command#undo() */
    public void undo() {
        this.node.setLocation(oldPos);
        this.node.setSize(oldSize);
    }
}
