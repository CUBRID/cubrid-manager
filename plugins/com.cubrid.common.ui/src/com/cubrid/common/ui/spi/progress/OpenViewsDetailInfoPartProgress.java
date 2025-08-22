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

import com.cubrid.common.core.common.model.ViewDetailInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * @author fulei
 * @version 1.0 - 2013-1-7 created by fulei
 */
public class OpenViewsDetailInfoPartProgress implements IRunnableWithProgress {

    private static final Logger LOGGER = LogUtil.getLogger(OpenViewsDetailInfoPartProgress.class);
    private final CubridDatabase database;
    private List<ViewDetailInfo> viewList = null;
    private boolean success = false;

    public OpenViewsDetailInfoPartProgress(CubridDatabase database) {
        this.database = database;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor)
            throws InvocationTargetException,
                    InterruptedException { // FIXME move this logic to core module
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {

            viewList = new ArrayList<ViewDetailInfo>();
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), true);

            StringBuilder sb = new StringBuilder();
            if (database.getDatabaseInfo().isSupportUserSchema()) {
                sb.append("SELECT c.class_name, v.vclass_def, c.owner_name \n");
                sb.append("FROM db_class c, db_attribute a, db_vclass v \n");
                sb.append("WHERE c.class_name=a.class_name \n");
                sb.append("AND c.class_name=v.vclass_name \n");
                sb.append("AND c.owner_name=v.owner_name \n");
                sb.append("AND c.is_system_class='NO' \n");
                sb.append("AND a.from_class_name IS NULL \n");
                sb.append("AND c.class_type='VCLASS' \n");
                sb.append("GROUP BY c.class_name, c.class_type, \n");
                // 8.2.2 need to group by these 2 column
                sb.append("v.vclass_def ,c.owner_name \n");
                sb.append("ORDER BY c.owner_name, c.class_type, c.class_name");
            } else {
                sb.append("SELECT c.class_name, v.vclass_def, c.owner_name \n");
                sb.append("FROM db_class c, db_attribute a, db_vclass v \n");
                sb.append("WHERE c.class_name=a.class_name \n");
                sb.append("AND c.class_name=v.vclass_name \n");
                sb.append("AND c.is_system_class='NO' \n");
                sb.append("AND a.from_class_name IS NULL \n");
                sb.append("AND c.class_type='VCLASS' \n");
                sb.append("GROUP BY c.class_name, c.class_type, \n");
                // 8.2.2 need to group by these 2 column
                sb.append("v.vclass_def ,c.owner_name \n");
                sb.append("ORDER BY c.class_type, c.class_name");
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                String className = rs.getString(1);
                String ownerName = rs.getString(3);
                ViewDetailInfo view = new ViewDetailInfo(className);
                view.setViewDef(rs.getString(2));
                view.setViewOwnerName(ownerName);
                viewList.add(view);
            }
            success = true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            QueryUtil.freeQuery(conn, stmt, rs);
        }
    }

    /**
     * loadViewsInfo
     *
     * @return Catalog
     */
    public void loadViewsInfo() {
        Display display = Display.getDefault();
        display.syncExec(
                new Runnable() {
                    public void run() {
                        try {
                            new ProgressMonitorDialog(null)
                                    .run(true, false, OpenViewsDetailInfoPartProgress.this);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                });
    }

    public List<ViewDetailInfo> getViewList() {
        return viewList;
    }

    public boolean isSuccess() {
        return success;
    }
}
