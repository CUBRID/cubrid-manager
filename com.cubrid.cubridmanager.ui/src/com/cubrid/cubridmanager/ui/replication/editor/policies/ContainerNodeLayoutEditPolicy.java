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

import com.cubrid.cubridmanager.ui.replication.editor.commands.ChangeNodeConstraintCommand;
import com.cubrid.cubridmanager.ui.replication.editor.commands.CreateLeafNodeCommand;
import com.cubrid.cubridmanager.ui.replication.editor.model.ContainerNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import com.cubrid.cubridmanager.ui.replication.editor.parts.NodePart;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Change the size and the location of the container node figure policy
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class ContainerNodeLayoutEditPolicy extends XYLayoutEditPolicy {

    protected boolean isHorizontal() {
        return false;
    }

    /**
     * @see
     *     org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart,
     *     java.lang.Object)
     * @param child the EditPart of the child being changed
     * @param constraint the new constraint, after being {@link #translateToModelConstraint(Object)
     *     translated}
     * @return Command
     */
    protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
        if (!(child instanceof NodePart)) {
            return null;
        }
        if (!(constraint instanceof Rectangle)) {
            return null;
        }

        ChangeNodeConstraintCommand cmd = new ChangeNodeConstraintCommand();
        cmd.setNode((Node) child.getModel());
        cmd.setLocation(((Rectangle) constraint).getLocation());
        cmd.setDimension(((Rectangle) constraint).getSize());
        return cmd;
    }

    /**
     * @see
     *     org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart,
     *     java.lang.Object)
     * @param child the EditPart of the child being added
     * @param constraint the model constraint, after being {@link
     *     #translateToModelConstraint(Object) translated}
     * @return the Command to add the child
     */
    protected Command createAddCommand(EditPart child, Object constraint) {
        return null;
    }

    /**
     * @see
     *     org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
     * @param request the CreateRequest
     * @return a Command to perform a create
     */
    protected Command getCreateCommand(CreateRequest request) {
        if (!(request.getNewObject() instanceof LeafNode)) {
            return null;
        }
        CreateLeafNodeCommand cmd = new CreateLeafNodeCommand();
        cmd.setContainerNode((ContainerNode) getHost().getModel());
        cmd.setLeafNode((LeafNode) request.getNewObject());
        Rectangle constraint = (Rectangle) getConstraintFor(request);
        cmd.setLocation(constraint.getLocation());
        return cmd;
    }

    /**
     * @see
     *     org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
     * @param request the Request
     * @return the Command to delete the child
     */
    protected Command getDeleteDependantCommand(Request request) {
        return null;
    }
}
