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
package com.cubrid.common.ui.er.commands;

import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.Relationship;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * Command to delete tables from the schema
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class DeleteTableCommand extends Command {
    private ERTable erTable;
    private ERSchema erSchema;
    private int index = -1;
    private final List foreignKeyRelationships = new ArrayList();
    private final List primaryKeyRelationships = new ArrayList();
    private Rectangle bounds;

    private void deleteRelationships(ERTable t) {
        this.foreignKeyRelationships.addAll(t.getForeignKeyRelationships());

        // for all relationships where current table is foreign key
        for (int i = 0; i < foreignKeyRelationships.size(); i++) {
            Relationship r = (Relationship) foreignKeyRelationships.get(i);
            t.deleteForeignKeyShipAndFire(r);
        }

        // for all relationships where current table is primary key
        this.primaryKeyRelationships.addAll(t.getTargetedRelationships());
        for (int i = 0; i < primaryKeyRelationships.size(); i++) {
            Relationship r = (Relationship) primaryKeyRelationships.get(i);
            r.getForeignKeyTable().deleteForeignKeyShipAndFire(r);
        }
    }

    @Override
    public void execute() {
        primExecute();
    }

    /** Invokes the execution of this command. */
    protected void primExecute() {
        // deleteRelationships(erTable);
        index = erSchema.getTables().indexOf(erTable);
        erSchema.deleteTableAndFire(erTable);
    }

    @Override
    public void redo() {
        primExecute();
    }

    private void restoreRelationships() {
        for (int i = 0; i < foreignKeyRelationships.size(); i++) {
            Relationship r = (Relationship) foreignKeyRelationships.get(i);
            r.getForeignKeyTable().addForeignKeyShipAndFire(r);
        }
        foreignKeyRelationships.clear();
        for (int i = 0; i < primaryKeyRelationships.size(); i++) {
            Relationship r = (Relationship) primaryKeyRelationships.get(i);
            r.getForeignKeyTable().addForeignKeyShipAndFire(r);
        }
        primaryKeyRelationships.clear();
    }

    /**
     * Sets the child to the passed Table
     *
     * @param table the child
     */
    public void setTable(ERTable table) {
        erTable = table;
    }

    /**
     * Sets the parent to the passed Schema
     *
     * @param schema the parent
     */
    public void setSchema(ERSchema schema) {
        erSchema = schema;
    }

    /** @see org.eclipse.gef.commands.Command#undo() */
    @Override
    public void undo() {
        erSchema.addTableAndFire(erTable);
        restoreRelationships();
        erTable.modifyBoundAndFire(bounds);
    }

    /** Sets the original bounds for the table so that these can be restored */
    public void setOriginalBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
