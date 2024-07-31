/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.database.erwin.action;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.cubrid.database.erwin.task.ExportSchemaTask;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllSchemaTask;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * ExportToERXmlAction Description
 *
 * @author Jason You
 * @version 1.0 - 2012-12-5 created by Jason You
 */
public class ExportERwinAction extends SelectionAction {

    public static final String ID = ExportERwinAction.class.getName();

    /**
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public ExportERwinAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor icon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    /**
     * @param shell
     * @param compareSchemaERXml
     * @param imageDescriptor
     */
    public ExportERwinAction(
            Shell shell, String text, ImageDescriptor icon, ImageDescriptor disabledIcon) {
        this(shell, null, text, icon, disabledIcon);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return true;
    }

    public void run() { // FIXME logic code move to core module
        int selected = 0;
        int logined = 0;
        Object[] objects = getSelectedObj();
        if (objects instanceof Object[]) {
            for (Object object : objects) {
                if (object instanceof CubridDatabase) {
                    selected++;
                    CubridDatabase database = (CubridDatabase) object;
                    if (database.isLogined()) {
                        logined++;
                    }
                }
            }
        }

        if (selected > 1) {
            CommonUITool.openWarningBox(
                    com.cubrid.common.ui.cubrid.database.erwin.Messages.errERwinSelectLeastOneDb);
            return;
        }

        if (selected <= 0) {
            CommonUITool.openWarningBox(
                    com.cubrid.common.ui.cubrid.database.erwin.Messages.errERwinSelectExportDb);
            return;
        }

        if (logined <= 0) {
            CommonUITool.openWarningBox(
                    com.cubrid.common.ui.cubrid.database.erwin.Messages.errERwinSelectLoginedDb);
            return;
        }

        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE | SWT.APPLICATION_MODAL);

        dialog.setFilterExtensions(new String[] {"*.xml"});

        String filename = dialog.open();

        if (filename == null) {
            return;
        }

        if (filename.trim().equals("")) {
            CommonUITool.openErrorBox(Messages.errFileNameIsEmpty);
            return;
        }

        for (Object obj : objects) {
            if (!(obj instanceof CubridDatabase)) {
                continue;
            }

            CubridDatabase database = (CubridDatabase) obj;

            final Map<String, SchemaInfo> allSchemaInfos = new HashMap<String, SchemaInfo>();

            TaskExecutor executor =
                    new TaskExecutor() {

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
                                } else if (task instanceof GetAllSchemaTask) {
                                    monitor.beginTask(Messages.msgGenerateInfo, 100);
                                    GetAllSchemaTask gTask = (GetAllSchemaTask) task;
                                    gTask.execute();
                                    if (task.getErrorMsg() == null) {
                                        allSchemaInfos.putAll(gTask.getSchemas());
                                    }

                                    if (allSchemaInfos.size() == 0) {
                                        continue;
                                    }
                                }
                            }
                            return true;
                        }
                    };

            ExportSchemaTask task = new ExportSchemaTask(allSchemaInfos, filename);
            GetAllSchemaTask schemaTask = new GetAllSchemaTask(database.getDatabaseInfo());
            executor.addTask(schemaTask);
            executor.addTask(task);

            new ExecTaskWithProgress(executor).busyCursorWhile();
            if (executor.isSuccess()) {
                CommonUITool.openInformationBox(
                        Messages.titleExportSchema, Messages.msgExportSuccess);
            }
        }
    }
}
