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
package com.cubrid.common.ui.cubrid.table.action;

import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.task.UpdateStatisticsTask;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * Update statistics on partition table
 *
 * @author pangqiren
 * @version 1.0 - 2010-3-23 created by pangqiren
 */
public class UpdateStatisticsAction extends SelectionAction {
    public static final String ID = UpdateStatisticsAction.class.getName();

    public UpdateStatisticsAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public UpdateStatisticsAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(
                obj, new String[] {NodeType.USER_PARTITIONED_TABLE}, false);
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        List<String> sqlList = new ArrayList<String>(); // FIXME move this logic to core module
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < obj.length; i++) {
            ISchemaNode schemaNode = (ISchemaNode) obj[i];
            String tableName = schemaNode.getParent().getName();
            int partitionNameLoc = schemaNode.getName().lastIndexOf("__p__");
            if (partitionNameLoc == -1) {
                continue;
            }
            partitionNameLoc += 5;
            String partitionName = schemaNode.getName().substring(partitionNameLoc);
            String sql =
                    "ALTER TABLE "
                            + QuerySyntax.escapeKeyword(tableName)
                            + " ANALYZE PARTITION "
                            + QuerySyntax.escapeKeyword(partitionName);
            sqlList.add(sql);
            buffer.append(",");
            buffer.append(schemaNode.getName());
        }

        String str = buffer.toString().replaceFirst(",", "");
        if (CommonUITool.openConfirmBox(Messages.bind(Messages.msgConfirmUpdateStatis, str))) {
            DefaultSchemaNode node = (DefaultSchemaNode) obj[0];
            String taskName = Messages.bind(Messages.updateStatisTaskName, node.getName());
            TaskExecutor executor = new CommonTaskExec(taskName);
            UpdateStatisticsTask task =
                    new UpdateStatisticsTask(node.getDatabase().getDatabaseInfo());
            task.setSqlList(sqlList);
            executor.addTask(task);
            new ExecTaskWithProgress(executor).exec();
            if (executor.isSuccess()) {
                CommonUITool.openInformationBox(
                        Messages.titleSuccess, Messages.msgSuccessUpdateStatis);
            }
        }
    }
}
