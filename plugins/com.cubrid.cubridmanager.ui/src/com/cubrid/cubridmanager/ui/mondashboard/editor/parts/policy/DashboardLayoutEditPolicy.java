/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.mondashboard.editor.parts.policy;

import com.cubrid.cubridmanager.ui.mondashboard.editor.command.NodeResizeCommand;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HANode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.parts.HANodePart;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * EditPolicy used by {@link.com.cubrid.cubridmanager.ui.mondashboard.editor.parts.DashboardPart}
 *
 * @author cyl
 * @version 1.0 - 2010-6-2 created by cyl
 */
public class DashboardLayoutEditPolicy extends XYLayoutEditPolicy {

    /**
     * Create command when figure's constraint change
     *
     * @param child the figure's editpart
     * @param constraint constraint of figure
     * @return command which controls figure's size.
     */
    protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
        if (child instanceof HANodePart) {
            NodeResizeCommand cmd = new NodeResizeCommand();
            cmd.setNode((HANode) child.getModel());
            cmd.setNewRect((Rectangle) constraint);
            return cmd;
        }
        return null;
    }

    /**
     * Create command when create a new figure.
     *
     * @param request create figure request
     * @return command which creates a new figure.
     */
    protected Command getCreateCommand(CreateRequest request) {
        return null;
    }
}
