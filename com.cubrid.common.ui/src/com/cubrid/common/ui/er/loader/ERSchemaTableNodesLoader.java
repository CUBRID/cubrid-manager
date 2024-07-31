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
package com.cubrid.common.ui.er.loader;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.model.loader.schema.CubridTablesFolderLoader;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

/**
 * ER Schema Table Nodes Loader
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-14 created by Yu Guojia
 */
public class ERSchemaTableNodesLoader {
    private final CubridDatabase dbNode;

    public ERSchemaTableNodesLoader(CubridDatabase dbNode) {
        this.dbNode = dbNode;
    }

    /**
     * Get the tree view of CQB
     *
     * @return TreeViewer
     */
    public TreeViewer getTreeView() {
        // on CM CubridNavigatorView navigatorView =
        // CubridNavigatorView.getNavigatorView("com.cubrid.cubridmanager.host.navigator");
        // CubridNavigatorView navigatorView =
        // CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
        // return navigatorView.getViewer();
        return null;
    }

    /**
     * Get all the user tables node. If the table folder hasnot been expanded, it cannot get all
     * nodes
     *
     * @return List<DefaultSchemaNode>
     */
    public List<DefaultSchemaNode> getAllTablesNode() {
        List<DefaultSchemaNode> nodes = new ArrayList<DefaultSchemaNode>();
        if (!dbNode.isLogined()) {
            return nodes;
        }

        String tablesFolderId =
                dbNode.getId() + CubridTablesFolderLoader.TABLES_FULL_FOLDER_SUFFIX_ID;
        ICubridNode tablesFolder = dbNode.getChild(tablesFolderId);
        if (null == tablesFolder) {
            return nodes;
        }

        List<ICubridNode> children = tablesFolder.getChildren();
        for (ICubridNode node : children) {
            if (NodeType.USER_TABLE.equals(node.getType()) && node instanceof DefaultSchemaNode) {
                nodes.add((DefaultSchemaNode) node);
            }
        }

        return nodes;
    }

    /**
     * Get all the user tables node infos
     *
     * @return List<SchemaInfo>
     */
    public List<SchemaInfo> getAllUserTablesInfo() {
        List<DefaultSchemaNode> tableNodes = getAllTablesNode();
        List<SchemaInfo> tables = new ArrayList<SchemaInfo>();

        for (DefaultSchemaNode node : tableNodes) {
            SchemaInfo table = dbNode.getDatabaseInfo().getSchemaInfo(node.getName());
            if (null != table) {
                tables.add(table);
            }
        }

        return tables;
    }

    /** Load the user table folder */
    public void load() {
        if (!dbNode.isLogined()) {
            return;
        }

        String tablesFolderId =
                dbNode.getId() + CubridTablesFolderLoader.TABLES_FULL_FOLDER_SUFFIX_ID;
        final ICubridNode tablesFolder = dbNode.getChild(tablesFolderId);
        if (null == tablesFolder) {
            return;
        }

        if (tablesFolder.getChildren().size() < 1) {
            final TreeViewer tv = getTreeView();
            Display.getDefault()
                    .syncExec(
                            new Runnable() {
                                public void run() {
                                    tv.expandToLevel(tablesFolder, 1);
                                }
                            });
        }
    }
}
