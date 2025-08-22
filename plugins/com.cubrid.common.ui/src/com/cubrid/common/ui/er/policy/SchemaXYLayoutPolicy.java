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

import com.cubrid.common.ui.er.commands.MoveTableCommand;
import com.cubrid.common.ui.er.figures.TableFigure;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.part.TablePart;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Handles manual layout editing for schema diagram. Only available for XYLayoutManagers, not for
 * automatic layout
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class SchemaXYLayoutPolicy extends XYLayoutEditPolicy {
    @Override
    protected Command createAddCommand(EditPart child, Object constraint) {
        return null;
    }

    @Override
    protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
        if (!(child instanceof TablePart) || !(constraint instanceof Rectangle)) {
            return null;
        }

        TablePart tablePart = (TablePart) child;
        ERTable erTable = tablePart.getTable();
        TableFigure figure = (TableFigure) tablePart.getFigure();
        Rectangle oldBounds = figure.getBounds();
        Rectangle newBounds = (Rectangle) constraint;

        if (oldBounds.width != newBounds.width && newBounds.width != -1) {
            return null;
        } else if (oldBounds.height != newBounds.height && newBounds.height != -1) {
            return null;
        }

        return new MoveTableCommand(erTable, oldBounds.getCopy(), newBounds.getCopy());
    }

    @Override
    public Rectangle getCurrentConstraintFor(GraphicalEditPart child) {
        IFigure fig = child.getFigure();
        Rectangle rectangle = (Rectangle) fig.getParent().getLayoutManager().getConstraint(fig);
        if (rectangle == null) {
            rectangle = fig.getBounds();
        }
        return rectangle;
    }

    public EditPolicy createChildEditPolicy(EditPart child) {
        return new NodeResizableEditPolicy();
    }

    @Override
    protected Command getCreateCommand(CreateRequest request) {
        return null;
    }

    @Override
    protected Command getDeleteDependantCommand(Request request) {
        return null;
    }
}
