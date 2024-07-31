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
package com.cubrid.common.ui.er.control;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlContainer;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERWinDBAttribute;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERWinSchemaInfo;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.model.CubridTableParser;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.swt.widgets.Shell;

/**
 * Import ERwin Data Control
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-1 created by Yu Guojia
 */
public class ImportERwinDataController {
    private final ERSchema erSchema;

    public ImportERwinDataController(ERSchema erSchema) {
        this.erSchema = erSchema;
    }

    /**
     * Import the ER win XML file data to ERD schema
     *
     * @param parentShell
     * @param erSchema
     */
    public boolean importERwinData(Shell parentShell, ERXmlContainer container) {
        if (container.getErrMsg() != null && !container.getErrMsg().equals("")) {
            CommonUITool.openErrorBox(container.getErrMsg());
            return false;
        }

        erSchema.deleteAllTableAndFire();
        Map<String, ERWinSchemaInfo> schemaInfos = container.getSchemaInfos();
        buildERDSchema(erSchema, schemaInfos);

        return true;
    }

    private void buildERDSchema(ERSchema erSchema, Map<String, ERWinSchemaInfo> schemaInfos) {
        String message = "";
        CubridTableParser tableParser = new CubridTableParser(erSchema);
        Set<SchemaInfo> dbSchemaInfos = new HashSet<SchemaInfo>();
        Collection<ERWinSchemaInfo> erwinSchemas = schemaInfos.values();
        for (ERWinSchemaInfo erwinSchema : erwinSchemas) {
            SchemaInfo schemaInfo = (SchemaInfo) erwinSchema;
            dbSchemaInfos.add(schemaInfo);
        }
        tableParser.buildERTables(dbSchemaInfos, -1, -1, true);
        tableParser.appendLogicalInfo(schemaInfos);

        List<ERTable> successTables = tableParser.getSuccessTables();
        for (ERTable table : successTables) {
            ERWinSchemaInfo savedTable = schemaInfos.get(table.getName());
            table.setLogicalName(savedTable.getLogicalName());
            List<ERTableColumn> columns = table.getColumns();
            for (ERTableColumn column : columns) {
                String colName = column.getName();
                ERWinDBAttribute savedDBAttr = savedTable.getERWinDBAttr(colName);
                column.setLogicalName(savedDBAttr.getLogicalName());
                column.setLogicalType(savedDBAttr.getLogicalDataType());
            }
        }

        erSchema.FireAddedTable(successTables);

        Map<String, Exception> failedTables = tableParser.getFailedTables();
        Map<String, List<Constraint>> removedFKs = tableParser.getRemovedFKConstraints();

        if (failedTables.size() > 0) {
            message =
                    Messages.bind(
                            com.cubrid.common.ui.er.Messages.errorAddTables, failedTables.keySet());
        }
        if (removedFKs.size() > 0) {
            if (!message.equals("")) {
                message += "\n";
            }
            message +=
                    Messages.bind(
                            com.cubrid.common.ui.er.Messages.cannotBeBuiltFK,
                            tableParser.getOneRemovedFK().getName());
            if (tableParser.getRemovedFKCount() > 1) {
                message += ", ...";
            }
        }

        if (!message.equals("")) {
            CommonUITool.openErrorBox(message);
        }
    }
}
