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
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.er.model.CubridTableParser;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Export and import json data for ER tables
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-11-18 created by Yu Guojia
 */
public class ExportImportGsonDataController extends ExportDataController {
    public ExportImportGsonDataController() {
        super();
    }

    public ExportImportGsonDataController(ERSchemaEditor erSchemaEditor) {
        super(erSchemaEditor);
    }

    public ExportImportGsonDataController(ERSchema erSchema) {
        super(erSchema);
    }

    public boolean importGsonData(Shell parentShell, String gsonData) {
        Gson gson = new Gson();
        ERSchema deserializedERSchema = null;
        try {
            deserializedERSchema = gson.fromJson(gsonData, ERSchema.class);
        } catch (Exception e) {
            CommonUITool.openErrorBox(parentShell, e.getMessage());
            return false;
        }

        if (deserializedERSchema == null) {
            return false;
        }

        getERSchema().deleteAllTableAndFire();
        Map<String, SchemaInfo> schemainfoMap = deserializedERSchema.getAllSchemaInfo();
        boolean isImportMap = false;
        if (deserializedERSchema.getPhysicalLogicRelation() != null) {
            isImportMap =
                    CommonUITool.openConfirmBox(
                            com.cubrid.common.ui.er.Messages.msgConfirmImportRelationMap);
        }
        buildERDSchema(getERSchema(), deserializedERSchema, schemainfoMap, isImportMap);

        return true;
    }

    private void buildERDSchema(
            ERSchema originSchema,
            ERSchema deserializedERSchema,
            Map<String, SchemaInfo> schemaInfos,
            boolean isImportMap) {
        String message = "";
        CubridTableParser tableParser = new CubridTableParser(originSchema);
        tableParser.buildERTables(schemaInfos.values(), -1, -1, false);

        if (isImportMap) {
            originSchema.setPhysicalLogicRelation(deserializedERSchema.getPhysicalLogicRelation());
        }
        List<ERTable> successTables = tableParser.getSuccessTables();
        for (ERTable table : successTables) {
            ERTable savedTable = deserializedERSchema.getTable(table.getName());
            table.setLogicalName(savedTable.getLogicalName());
            List<ERTableColumn> columns = table.getColumns();
            for (ERTableColumn column : columns) {
                String colName = column.getName();
                ERTableColumn savedColumn = savedTable.getColumn(colName, true);
                column.setLogicalName(savedColumn.getLogicalName());
                column.setLogicalType(savedColumn.getLogicalType());
            }
            if (originSchema.isLayoutManualDesired()) {
                table.setBounds(savedTable.getBounds());
            }
        }

        originSchema.FireAddedTable(successTables);

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

    public boolean exportData(Shell parentShell, boolean isDirectSave) {
        String fileFullName;
        if (!isDirectSave || latestFileFullName == null) {
            FileDialog dialog = new FileDialog(parentShell, SWT.SAVE | SWT.APPLICATION_MODAL);
            dialog.setFilterExtensions(new String[] {"*.erd"});

            fileFullName = dialog.open();
        } else {
            fileFullName = latestFileFullName;
        }

        if (fileFullName == null) {
            return false;
        }

        if (fileFullName.trim().length() == 0) {
            CommonUITool.openErrorBox(Messages.errFileNameIsEmpty);
            return false;
        }

        TaskExecutor executor =
                new TaskExecutor() {
                    public boolean exec(IProgressMonitor monitor) {
                        for (ITask task : taskList) {
                            if (task instanceof ExportGsonDataTask) {
                                ExportGsonDataTask eTask = (ExportGsonDataTask) task;
                                monitor.setTaskName(Messages.msgGenerateInfo);
                                monitor.worked(50);
                                eTask.execute();
                                monitor.setTaskName(Messages.msgFinished);
                                monitor.worked(100);
                                monitor.done();
                            }
                        }
                        return true;
                    }
                };

        ExportGsonDataTask task = new ExportGsonDataTask(getERSchema(), fileFullName);
        executor.addTask(task);

        new ExecTaskWithProgress(executor).busyCursorWhile();
        if (executor.isSuccess()) {
            latestFileFullName = fileFullName;
            CommonUITool.openInformationBox(
                    com.cubrid.common.ui.er.Messages.titleExport,
                    Messages.bind(com.cubrid.common.ui.er.Messages.msgExportSuccess, fileFullName));
        }

        return executor.isSuccess();
    }
}
