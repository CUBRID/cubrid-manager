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

import com.cubrid.common.ui.er.commands.ModifyColumnLabelCommand;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.er.part.ColumnPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.CellEditor;

/**
 * EditPolicy for the direct editing of Column names
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class ColumnDirectEditPolicy extends DirectEditPolicy {
    private String oldValue;

    @Override
    protected Command getDirectEditCommand(DirectEditRequest request) {

        ERTableColumn column = (ERTableColumn) getHost().getModel();
        ModifyColumnLabelCommand cmd = new ModifyColumnLabelCommand(column);
        CellEditor cellEditor = request.getCellEditor();
        cmd.changeNameType((String) cellEditor.getValue());
        return cmd;
    }

    @Override
    protected void showCurrentEditValue(DirectEditRequest request) {
        String value = (String) request.getCellEditor().getValue();
        ColumnPart columnPart = (ColumnPart) getHost();
        columnPart.handleNameChange(value);
    }

    @Override
    protected void storeOldEditValue(DirectEditRequest request) {
        CellEditor cellEditor = request.getCellEditor();
        oldValue = (String) cellEditor.getValue();
    }

    @Override
    protected void revertOldEditValue(DirectEditRequest request) {
        CellEditor cellEditor = request.getCellEditor();
        cellEditor.setValue(oldValue);
        ColumnPart columnPart = (ColumnPart) getHost();
        columnPart.revertNameChange(oldValue);
    }
}
