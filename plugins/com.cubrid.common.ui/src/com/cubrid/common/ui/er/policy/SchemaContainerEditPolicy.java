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

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.er.commands.AddTableCommand;
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.part.SchemaDiagramPart;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

/**
 * Handles creation of new tables using drag and drop or point and click from the palette
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class SchemaContainerEditPolicy extends ContainerEditPolicy {
    @Override
    protected Command getAddCommand(GroupRequest request) {
        EditPart host = getTargetEditPart(request);
        return null;
    }

    @Override
    protected Command getCreateCommand(CreateRequest request) {
        Object newObject = request.getNewObject();
        if (!(newObject instanceof ERTable)) {
            return null;
        }

        Point location = request.getLocation();
        SchemaDiagramPart schemaPart = (SchemaDiagramPart) getHost();
        ERSchema erSchema = schemaPart.getSchema();
        ERTable erTable = (ERTable) newObject;
        ERSchemaEditor editor = schemaPart.getEditor();
        int offsetX = 0;
        int offsetY = 0;
        if (editor != null) {
            offsetX = editor.getHorizontalScrollWidth();
            offsetY = editor.getVerticalScrollHeight();
        }
        erTable.setBounds(
                new Rectangle(
                        location.x + offsetX,
                        location.y + offsetY,
                        erTable.getMinWidth(),
                        erTable.getMinHeight()));
        AddTableCommand addTableCommand = new AddTableCommand();
        addTableCommand.setSchema(erSchema);
        SchemaInfo schemaInfo =
                ERTable.createEmptySchemaInfo(
                        erTable.getName(), erTable.getCubridDatabase().getName());
        addTableCommand.setTable(erTable, schemaInfo);
        return addTableCommand;
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        if (REQ_CREATE.equals(request.getType())) {
            return getHost();
        } else if (REQ_ADD.equals(request.getType())) {
            return getHost();
        } else if (REQ_MOVE.equals(request.getType())) {
            return getHost();
        } else {
            return super.getTargetEditPart(request);
        }
    }
}
