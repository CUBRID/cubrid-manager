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

import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is responsible for loading the columns and indexes of user class.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-10 created by lizhiqiang
 */
public class CubridUserTableLoader extends CubridNodeLoader {

    public static final String COLUMN_FOLDER_ID = "Columns";
    public static final String COLUMN_FOLDER_NAME = Messages.msgTableColumnsFolderName;
    public static final String INDEX_FOLDER_ID = "Indexes";
    public static final String INDEX_FOLDER_NAME = Messages.msgTableIndexesFolderName;

    protected List<TableColumn> columns;

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
            List<ICubridNode> nodes = parent.getChildren();
            for (int i = nodes.size() - 1; i >= 0; i--) {
                ICubridNode node = nodes.get(i);
                if (!node.isContainer()) {
                    parent.removeChild(node);
                }
            }
            loadColumns(parent, getLevel(), monitor);
            loadIndexes(parent, getLevel(), monitor);
            setLoaded(true);
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    (ICubridNode) parent,
                                    CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
        }
    }

    /**
     * Load the indexes and its children node
     *
     * @param parent the parent node
     * @param level the node level
     * @param monitor the IProgressMonitor object
     */
    protected void loadIndexes(ICubridNode parent, int level, final IProgressMonitor monitor) {
        synchronized (this) {
            String indexFolderId = parent.getId() + NODE_SEPARATOR + INDEX_FOLDER_ID;
            ICubridNode indexFolder = parent.getChild(indexFolderId);
            if (indexFolder == null) {
                indexFolder =
                        new DefaultSchemaNode(
                                indexFolderId,
                                INDEX_FOLDER_NAME,
                                INDEX_FOLDER_NAME,
                                "icons/navigator/folder.png");
                indexFolder.setType(NodeType.TABLE_INDEX_FOLDER);
                indexFolder.setContainer(true);
                parent.addChild(indexFolder);
                ICubridNodeLoader indexLoader = new CubridUserTableIndexLoader();
                indexLoader.setLevel(level);
                indexFolder.setLoader(indexLoader);
            } else {
                if (indexFolder.getLoader() != null && indexFolder.getLoader().isLoaded()) {
                    indexFolder.getLoader().setLoaded(false);
                    indexFolder.getChildren(monitor);
                }
            }
        }
    }

    /**
     * Load the columns and its children node
     *
     * @param parent the parent node
     * @param level the node level
     * @param monitor the IProgressMonitor object
     */
    protected void loadColumns(ICubridNode parent, int level, final IProgressMonitor monitor) {
        String columnFolderId = parent.getId() + NODE_SEPARATOR + COLUMN_FOLDER_ID;
        ICubridNode columnFolder = parent.getChild(columnFolderId);
        if (columnFolder == null) {
            columnFolder =
                    new DefaultSchemaNode(
                            columnFolderId,
                            COLUMN_FOLDER_NAME,
                            COLUMN_FOLDER_NAME,
                            "icons/navigator/folder.png");
            columnFolder.setType(NodeType.TABLE_COLUMN_FOLDER);
            columnFolder.setContainer(true);
            parent.addChild(columnFolder);
            CubridUserTableColumnLoader columnLoader = new CubridUserTableColumnLoader();
            columnLoader.setLevel(level);
            columnFolder.setLoader(columnLoader);
            if (level == DEFINITE_LEVEL) {
                columnLoader.setColumns(columns);
                columnFolder.getChildren(monitor);
            }
        } else {
            if (columnFolder.getLoader() != null && columnFolder.getLoader().isLoaded()) {
                columnFolder.getLoader().setLoaded(false);
                columnFolder.getChildren(monitor);
            }
        }
        columns = null;
    }

    /** @param columns the columns to set */
    public void setColumns(List<TableColumn> columns) {
        synchronized (this) {
            this.columns = columns;
        }
    }
}
