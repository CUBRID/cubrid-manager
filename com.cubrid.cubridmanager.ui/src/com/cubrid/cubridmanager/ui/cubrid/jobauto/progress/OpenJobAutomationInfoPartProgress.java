/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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
package com.cubrid.cubridmanager.ui.cubrid.jobauto.progress;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.task.GetBackupPlanListTask;
import com.cubrid.cubridmanager.core.cubrid.jobauto.task.GetQueryPlanListTask;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * @author fulei
 * @version 1.0 - 2013-1-14 created by fulei
 */
public class OpenJobAutomationInfoPartProgress implements IRunnableWithProgress {

    private static final Logger LOGGER = LogUtil.getLogger(OpenJobAutomationInfoPartProgress.class);
    private final CubridDatabase database;
    private List<BackupPlanInfo> backupPlanInfoList;
    private List<QueryPlanInfo> queryPlanInfoList;

    private boolean success = false;
    private boolean getBackupPlanInfo = false;
    private boolean getQueryPlanInfo = false;

    public OpenJobAutomationInfoPartProgress(CubridDatabase database) {
        this.database = database;
        getBackupPlanInfo = true;
        getQueryPlanInfo = true;
    }

    public OpenJobAutomationInfoPartProgress(
            CubridDatabase database, boolean getBackupPlanInfo, boolean getQueryPlanInfo) {
        this.database = database;
        this.getBackupPlanInfo = getBackupPlanInfo;
        this.getQueryPlanInfo = getQueryPlanInfo;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException {
        if (getBackupPlanInfo) {
            final GetBackupPlanListTask getBackupPlanInfoListTask =
                    new GetBackupPlanListTask(database.getServer().getServerInfo());
            getBackupPlanInfoListTask.setDbName(database.getLabel());
            getBackupPlanInfoListTask.execute();
            if (getBackupPlanInfoListTask.isSuccess()) {
                backupPlanInfoList = getBackupPlanInfoListTask.getBackupPlanInfoList();
            } else {
                backupPlanInfoList = new ArrayList<BackupPlanInfo>();
            }
        }
        if (getQueryPlanInfo) {
            final GetQueryPlanListTask getQueryPlanListTask =
                    new GetQueryPlanListTask(database.getServer().getServerInfo());
            getQueryPlanListTask.setDbName(database.getLabel());
            getQueryPlanListTask.execute();
            if (getQueryPlanListTask.isSuccess()) {
                queryPlanInfoList = getQueryPlanListTask.getQueryPlanInfoList();
            } else {
                queryPlanInfoList = new ArrayList<QueryPlanInfo>();
            }
        }
        success = true;
    }

    /**
     * load loadJobAutomationInfoList list
     *
     * @return Catalog
     */
    public void loadJobAutomationInfoList() {
        Display display = Display.getDefault();
        display.syncExec(
                new Runnable() {
                    public void run() {
                        try {
                            new ProgressMonitorDialog(null)
                                    .run(true, false, OpenJobAutomationInfoPartProgress.this);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                });
    }

    public List<BackupPlanInfo> getBackupPlanInfoList() {
        return backupPlanInfoList;
    }

    public List<QueryPlanInfo> getQueryPlanInfoList() {
        return queryPlanInfoList;
    }

    public boolean isSuccess() {
        return success;
    }
}
