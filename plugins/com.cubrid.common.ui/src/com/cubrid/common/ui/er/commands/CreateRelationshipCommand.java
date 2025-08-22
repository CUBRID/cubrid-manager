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

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.dialog.AddFKDialog;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.model.CubridTableParser;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.Relationship;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.List;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Command to create a new relationship between tables
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class CreateRelationshipCommand extends Command {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtil.getLogger(CreateRelationshipCommand.class);

    protected ERTable foreignTable;
    protected ERTable primaryTable;

    @Override
    public boolean canExecute() {
        boolean returnValue = true;
        if (primaryTable == null || foreignTable.equals(primaryTable)) {
            returnValue = false;
        }
        return returnValue;
    }

    private boolean check() {
        if (!primaryTable.hasPK()) {
            CommonUITool.openErrorBox(Messages.errAddFKNoPK);
            return false;
        }
        List relationships = primaryTable.getTargetedRelationships();
        for (int i = 0; i < relationships.size(); i++) {
            Relationship currentRelationship = (Relationship) relationships.get(i);
            if (currentRelationship.getForeignKeyTable().equals(foreignTable)) {
                CommonUITool.openErrorBox(Messages.errAddFKExist);
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute() {
        if (!check()) {
            return;
        }
        ERSchema erSchema = foreignTable.getERSchema();
        SchemaInfo fkSchemaInfo = erSchema.getSchemaInfo(foreignTable.getName());

        AddFKDialog dlg =
                new AddFKDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        erSchema.getCubridDatabase(),
                        fkSchemaInfo,
                        null,
                        false,
                        erSchema.getAllSchemaInfo());
        dlg.setDefaultTableName(primaryTable.getName());
        int returnCode = dlg.open();
        if (returnCode == AddFKDialog.OK) {
            Constraint fk = dlg.getRetFK();
            if (fk == null) {
                return;
            }
            CubridTableParser parser = new CubridTableParser(erSchema);
            try {
                parser.addFKShip(foreignTable, fkSchemaInfo, fk);
                fkSchemaInfo.addConstraint(fk);
            } catch (Exception e) {
                CommonUITool.openErrorBox(e.getMessage());
            }
        }
    }

    public ERTable getForeignTable() {
        return foreignTable;
    }

    public ERTable getPrimaryTable() {
        return primaryTable;
    }

    public void setForeignTable(ERTable foreignTable) {
        this.foreignTable = foreignTable;
    }

    public void setPrimaryTable(ERTable primaryTable) {
        this.primaryTable = primaryTable;
    }
}
