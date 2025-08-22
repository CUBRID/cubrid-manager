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
package com.cubrid.common.ui.spi.progress;

import com.cubrid.common.core.common.model.Synonym;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.synonym.JDBCGetSynonymListTask;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * @author fulei
 * @version 1.0 - 2013-1-9 created by fulei
 */
public class OpenSynonymDetailInfoPartProgress implements IRunnableWithProgress {

    private static final Logger LOGGER = LogUtil.getLogger(OpenViewsDetailInfoPartProgress.class);
    private final CubridDatabase database;
    private List<Synonym> synonymList = null;
    private boolean success = false;

    public OpenSynonymDetailInfoPartProgress(CubridDatabase database) {
        this.database = database;
    }

    public void run(IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException {
        DatabaseInfo databaseInfo = database.getDatabaseInfo();
        ITask task = null;
        task = new JDBCGetSynonymListTask(databaseInfo);

        task.execute();
        if (!task.isSuccess()) {
            LOGGER.error(task.getErrorMsg());
            return;
        }
        synonymList = ((JDBCGetSynonymListTask) task).getSynonymInfoList();
        success = true;
    }

    /**
     * load trigger info list
     *
     * @return Catalog
     */
    public void loadSynonymInfoList() {
        Display display = Display.getDefault();
        display.syncExec(
                new Runnable() {
                    public void run() {
                        try {
                            new ProgressMonitorDialog(null)
                                    .run(true, false, OpenSynonymDetailInfoPartProgress.this);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                });
    }

    public List<Synonym> getSynonymList() {
        return synonymList;
    }

    public boolean isSuccess() {
        return success;
    }
}
