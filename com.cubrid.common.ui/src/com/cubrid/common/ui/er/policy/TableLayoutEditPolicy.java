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

import com.cubrid.common.ui.er.commands.MoveColumnCommand;
import com.cubrid.common.ui.er.commands.TransferColumnCommand;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.er.part.ColumnPart;
import com.cubrid.common.ui.er.part.TablePart;
import java.util.List;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Handles moving of columns within and between tables
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class TableLayoutEditPolicy extends FlowLayoutEditPolicy {
    @Override
    protected Command createAddCommand(EditPart child, EditPart after) {
        if (!(child instanceof ColumnPart) || !(after instanceof ColumnPart)) {
            return null;
        }

        ERTableColumn toMove = (ERTableColumn) child.getModel();
        ERTableColumn afterModel = (ERTableColumn) after.getModel();

        TablePart originalTablePart = (TablePart) child.getParent();
        ERTable originalTable = (ERTable) originalTablePart.getModel();
        TablePart newTablePart = (TablePart) after.getParent();
        ERTable newTable = newTablePart.getTable();

        int oldIndex = originalTablePart.getChildren().indexOf(child);
        int newIndex = newTablePart.getChildren().indexOf(after);

        return new TransferColumnCommand(
                toMove, afterModel, originalTable, newTable, oldIndex, newIndex);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Command createMoveChildCommand(EditPart child, EditPart after) {
        if (after == null) {
            return null;
        }

        ERTableColumn childModel = (ERTableColumn) child.getModel();
        ERTable parentTable = (ERTable) getHost().getModel();
        List hostParts = getHost().getChildren();
        int oldIndex = hostParts.indexOf(child);
        int newIndex = hostParts.indexOf(after);

        return new MoveColumnCommand(childModel, parentTable, oldIndex, newIndex);
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
