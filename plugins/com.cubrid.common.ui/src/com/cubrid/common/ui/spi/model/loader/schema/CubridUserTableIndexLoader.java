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
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableIndex;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassIndexesTask;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * This class is responsible for loading the indexes of user class folder
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-7 created by lizhiqiang
 */
public class CubridUserTableIndexLoader extends CubridNodeLoader {
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
            DatabaseInfo databaseInfo = database.getDatabaseInfo();
            final GetUserClassIndexesTask task = new GetUserClassIndexesTask(databaseInfo);

            monitorCancel(monitor, new ITask[] {task});

            String tableName = parent.getParent().getName();
            List<TableIndex> indexes = task.getIndexesNames(tableName);
            final String errorMsg = task.getErrorMsg();
            if (!monitor.isCanceled() && errorMsg != null && errorMsg.trim().length() > 0) {
                parent.removeAllChild();
                Display display = Display.getDefault();
                display.syncExec(
                        new Runnable() {
                            public void run() {
                                CommonUITool.openErrorBox(errorMsg);
                            }
                        });
                setLoaded(true);
                return;
            }
            if (monitor.isCanceled()) {
                setLoaded(true);
                return;
            }
            parent.removeAllChild();
            String parentId = parent.getId();
            if (indexes != null && !indexes.isEmpty()) {
                for (TableIndex index : indexes) {
                    String indexName = index.getIndexName();
                    String ownerName = index.getOwnerName();
                    String nodeId = parentId + NODE_SEPARATOR + indexName;
                    String label = indexName;

                    List<String> dependColumns = index.getColumns();
                    if (dependColumns != null && !dependColumns.isEmpty()) {
                        String keyAttrName = index.getColumns().toString();
                        label += keyAttrName.replace('[', '(').replace(']', ')');
                    }

                    ICubridNode node =
                            new DefaultSchemaNode(
                                    nodeId, label, label, "icons/navigator/table_index_item.png");
                    if (index.isPrimaryKey()) {
                        node.setIconPath("icons/primary_key.png");
                    }

                    node.setType(NodeType.TABLE_INDEX);
                    node.setModelObj(index);
                    node.setContainer(false);
                    parent.addChild(node);
                }
            }
            Collections.sort(parent.getChildren());
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }
}
