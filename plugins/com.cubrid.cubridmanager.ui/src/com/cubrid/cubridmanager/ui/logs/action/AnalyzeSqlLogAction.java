/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.logs.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogResultList;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.core.logs.task.GetAnalyzeCasLogTask;
import com.cubrid.cubridmanager.ui.logs.Messages;
import com.cubrid.cubridmanager.ui.logs.dialog.SqlLogAnalyzeResultDialog;
import com.cubrid.cubridmanager.ui.logs.dialog.SqlLogFileListDialog;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to analyze sql log.
 *
 * @author wuyingshi
 * @version 1.0 - 2009-3-10 created by wuyingshi
 */
public class AnalyzeSqlLogAction extends SelectionAction {

    public static final String ID = AnalyzeSqlLogAction.class.getName();

    /**
     * The Constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public AnalyzeSqlLogAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The Constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public AnalyzeSqlLogAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     * @return false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj Object
     * @return boolean(whether to support)
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (node.getServer() == null) {
                return false;
            }
            ServerUserInfo serverUserInfo = node.getServer().getServerInfo().getLoginedUserInfo();
            if (serverUserInfo == null || serverUserInfo.getCasAuth() != CasAuthType.AUTH_ADMIN) {
                return false;
            }
            if (CubridNodeType.BROKER_SQL_LOG.equals(node.getType())
                    || CubridNodeType.BROKER_SQL_LOG_FOLDER.equals(node.getType())) {
                return true;
            }
        }
        return false;
    }

    /** Open dialog */
    public void run() {

        Object[] obj = this.getSelectedObj();
        DefaultCubridNode node = null;
        LogInfo logInfo = null;
        node = (DefaultCubridNode) obj[0];
        logInfo = (LogInfo) node.getAdapter(LogInfo.class);
        SqlLogFileListDialog cASLogTopConfigDialog = new SqlLogFileListDialog(getShell());
        List<String> targetStringList = new ArrayList<String>();
        if (logInfo == null) {
            for (int j = 0, m = node.getChildren().size(); j < m; j++) {
                targetStringList.add(
                        ((LogInfo) node.getChildren().get(j).getAdapter(LogInfo.class)).getPath());
            }
        } else {
            targetStringList.add(logInfo.getPath());
        }
        cASLogTopConfigDialog.create();
        cASLogTopConfigDialog.setInfo(targetStringList);
        int returnCode = cASLogTopConfigDialog.open();
        if (returnCode == SqlLogFileListDialog.OK) {
            String optionT;
            final GetAnalyzeCasLogTask task =
                    new GetAnalyzeCasLogTask(node.getServer().getServerInfo());
            if (cASLogTopConfigDialog.isOption()) {
                optionT = "yes";
            } else {
                optionT = "no";
            }
            List<String> selectedStringList = cASLogTopConfigDialog.getSelectedStringList();
            String[] path = new String[selectedStringList.size()];
            selectedStringList.toArray(path);
            task.setLogFiles(path);
            task.setOptionT(optionT);

            TaskExecutor taskExcutor = new CommonTaskExec(Messages.loadLogTaskName);
            taskExcutor.addTask(task);
            new ExecTaskWithProgress(taskExcutor).exec();
            if (!taskExcutor.isSuccess()) {
                return;
            }
            AnalyzeCasLogResultList analyzeCasLogResultList =
                    (AnalyzeCasLogResultList) task.getAnalyzeCasLogResultList();
            SqlLogAnalyzeResultDialog activityCASLogPathDialog =
                    new SqlLogAnalyzeResultDialog(getShell());
            activityCASLogPathDialog.setOption(cASLogTopConfigDialog.isOption());
            activityCASLogPathDialog.setNode(node);
            activityCASLogPathDialog.setResultFile(analyzeCasLogResultList.getResultfile());
            activityCASLogPathDialog.setAnalyzeCasLogResultList(analyzeCasLogResultList);
            activityCASLogPathDialog.create();
            activityCASLogPathDialog.setLabel(selectedStringList);
            activityCASLogPathDialog.insertArrayToTable(analyzeCasLogResultList);
            activityCASLogPathDialog.open();
        }
    }
}
