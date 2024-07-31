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

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.cubrid.database.erwin.task.ExportSchemaTask;
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Controller for exporting ERwin data
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-2 created by Yu Guojia
 */
public class ExportERwinDataController extends ExportDataController {
    public ExportERwinDataController() {
        super();
    }

    public ExportERwinDataController(ERSchemaEditor erSchemaEditor) {
        super(erSchemaEditor);
    }

    public boolean exportData(Shell parentShell, boolean isDirectSave) {
        String fileFullName;
        if (!isDirectSave || latestFileFullName == null) {
            FileDialog dialog = new FileDialog(parentShell, SWT.SAVE | SWT.APPLICATION_MODAL);
            dialog.setFilterExtensions(new String[] {"*.xml"});

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
                    @Override
                    public boolean exec(IProgressMonitor monitor) {
                        for (ITask task : taskList) {
                            if (task instanceof ExportSchemaTask) {
                                ExportSchemaTask eTask = (ExportSchemaTask) task;
                                try {
                                    eTask.initMarshaller();
                                } catch (JAXBException e) {
                                    e.printStackTrace();
                                    eTask.cancel();
                                    return false;
                                }

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

        ExportSchemaTask task =
                new ExportSchemaTask(getERSchema().getAllSchemaInfo(), fileFullName);

        List<ERTable> tables = getERSchema().getTables();
        Map<String, String> tablePLMap = new HashMap<String, String>();
        Map<String, Map<String, List>> tablesPhysicalLogicalMap =
                new HashMap<String, Map<String, List>>();
        for (ERTable table : tables) {
            tablePLMap.put(table.getName(true), table.getName(false));

            Map<String, List> columnsPLMap = new HashMap<String, List>();
            tablesPhysicalLogicalMap.put(table.getName(true), columnsPLMap);
            List<ERTableColumn> columns = table.getColumns();
            for (ERTableColumn column : columns) {
                List<String> logicalNameAndType = new LinkedList<String>();
                columnsPLMap.put(column.getName(true), logicalNameAndType);
                logicalNameAndType.add(column.getName(false));
                logicalNameAndType.add(column.getShowType(false));
            }
        }

        task.setTablePLMap(tablePLMap);
        task.setTablesPhysicalLogicalMap(tablesPhysicalLogicalMap);

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
