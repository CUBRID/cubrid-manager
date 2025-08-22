/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.spi.model.loader.schema;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassColumnsTask;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * This class is responsible for loading the columns of user class folder
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-7 created by lizhiqiang
 */
public class CubridUserTableColumnLoader extends CubridUserTableLoader {

    /**
     * Load children object for parent
     *
     * @param parent the parent node
     * @param monitor the IProgressMonitor object
     */
    public void load(ICubridNode parent, final IProgressMonitor monitor) {
        synchronized (this) {
            if (isLoaded()) {
                return;
            }
            CubridDatabase database = ((ISchemaNode) parent).getDatabase();
            if (!database.isLogined() || database.getRunningType() == DbRunningType.STANDALONE) {
                parent.removeAllChild();
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        (ICubridNode) parent,
                                        CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
                return;
            }
            if (columns == null && !getColumns(parent, monitor, database)) {
                return;
            }
            if (monitor.isCanceled()) {
                columns = null;
                setLoaded(true);
                return;
            }

            parent.removeAllChild();
            String parentId = parent.getId();
            if (columns != null && !columns.isEmpty()) {
                for (TableColumn column : columns) {
                    String label = column.getColumnName() + ",";
                    label +=
                            FieldHandlerUtils.getComleteType(
                                    column.getTypeName(),
                                    column.getSubElementTypeName(),
                                    column.getPrecision(),
                                    column.getScale());
                    String nodeId = parentId + NODE_SEPARATOR + column.getColumnName();
                    ICubridNode node =
                            new DefaultSchemaNode(
                                    nodeId, label, label, "icons/navigator/table_column_item.png");
                    if (column.isPrimaryKey()) {
                        node.setIconPath("icons/primary_key.png");
                    }
                    node.setType(NodeType.TABLE_COLUMN);
                    node.setModelObj(column);
                    node.setContainer(false);
                    parent.addChild(node);
                }
            }
            columns = null;
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Get the columns based upon the given parent node
     *
     * @param parent the given parent node
     * @param monitor the IProgressMonitor object
     * @param database the given database
     * @return whether is in transaction
     */
    private boolean getColumns(
            ICubridNode parent, final IProgressMonitor monitor, CubridDatabase database) {
        DatabaseInfo databaseInfo = database.getDatabaseInfo();
        final GetUserClassColumnsTask task = new GetUserClassColumnsTask(databaseInfo);
        monitorCancel(monitor, new ITask[] {task});
        String tableName = parent.getParent().getName();
        columns = task.getColumns(tableName);
        final String errorMsg = task.getErrorMsg();
        if (!monitor.isCanceled() && errorMsg != null && errorMsg.trim().length() > 0) {
            Display display = Display.getDefault();
            display.syncExec(
                    new Runnable() {
                        public void run() {
                            CommonUITool.openErrorBox(errorMsg);
                        }
                    });
            columns = null;
            if (!task.isInTransation()) {
                parent.removeAllChild();
                setLoaded(true);
                return false;
            }
        }
        return true;
    }
}
