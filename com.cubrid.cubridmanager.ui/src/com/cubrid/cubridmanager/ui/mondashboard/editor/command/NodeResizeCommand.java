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
package com.cubrid.cubridmanager.ui.mondashboard.editor.command;

import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * Node resize command.
 *
 * @author cyl
 * @version 1.0 - 2010-6-4 created by cyl
 */
public class NodeResizeCommand extends Command {
    private HANode node;

    private final Rectangle oldRect = new Rectangle();

    private final Rectangle newRect = new Rectangle();

    public void setNode(HANode node) {
        this.node = node;
    }

    /**
     * set figure's new rect
     *
     * @param rect new rect
     */
    public void setNewRect(Rectangle rect) {
        newRect.setBounds(rect);
    }

    /** command execute.set new location and new size. */
    public void execute() {
        oldRect.setLocation(this.node.getLocation());
        oldRect.setSize(node.getSize());
        node.setLocation(newRect.getLocation());
    }

    /**
     * get command label
     *
     * @return command's label
     */
    public String getLabel() {
        return "Resize Node";
    }

    /** redo. */
    public void redo() {
        node.setLocation(newRect.getLocation());
        node.setSize(newRect.getSize());
    }

    /** undo. */
    public void undo() {
        node.setLocation(oldRect.getLocation());
        node.setSize(oldRect.getSize());
    }
}
