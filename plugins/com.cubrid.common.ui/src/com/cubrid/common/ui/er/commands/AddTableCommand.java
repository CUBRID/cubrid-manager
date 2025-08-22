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

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import org.eclipse.gef.commands.Command;

/**
 * Command to create a new table table
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-10 created by Yu Guojia
 */
public class AddTableCommand extends Command {
    private ERSchema erSchema;
    private ERTable erTable;
    private SchemaInfo schemaInfo;
    private static String newTableNamePre = "TABLE_";

    @Override
    public void execute() {

        String name = newTableNamePre + (erSchema.getTables().size() + 1);
        this.erTable.modifyNameAndFire(name);
        this.erTable.setERSchema(erSchema);
        schemaInfo.setClassname(name);
        schemaInfo.setUniqueName(name);
        erSchema.addTableAndFire(erTable);
    }

    /**
     * Sets the parent ActivityDiagram
     *
     * @param sa the parent
     */
    public void setSchema(ERSchema erSchema) {
        this.erSchema = erSchema;
    }

    /**
     * Sets the Activity to create
     *
     * @param erTable the Activity to create
     */
    public void setTable(ERTable erTable, SchemaInfo schemaInfo) {
        this.erTable = erTable;
        this.schemaInfo = schemaInfo;
    }

    @Override
    public void undo() {
        erSchema.deleteTableAndFire(erTable);
    }
}
