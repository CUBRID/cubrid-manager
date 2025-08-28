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
package com.cubrid.common.ui.er.dnd;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.IDatabaseSpec;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.er.SchemaEditorInput;
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.er.model.CubridTableParser;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * ER designer DNDController for controlling table/view dragging and dropping on navigator view tree
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-6 created by Yu Guojia
 */
public class ERDNDController {
    private final ERSchemaEditor editor;
    private static Map<String, TreeViewer> perspectiveTreeviewerMap =
            new HashMap<String, TreeViewer>();
    private static Logger LOGGER = LoggerFactory.getLogger(ERDNDController.class);

    public ERDNDController(ERSchemaEditor editor) {
        this.editor = editor;
    }

    public static void registerDragSource(String perspectiveId, TreeViewer treeViewer) {
        synchronized (ERDNDController.class) {
            if (perspectiveId != null && treeViewer != null) {
                perspectiveTreeviewerMap.put(perspectiveId, treeViewer);
            }
        }
    }

    public void registerDropTarget() {
        synchronized (this) {
            DropTarget dropTt =
                    new DropTarget(
                            editor.getGraphicalControl(),
                            DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
            dropTt.setTransfer(new Transfer[] {TextTransfer.getInstance()});
            dropTt.addDropListener(
                    new DropTargetAdapter() {
                        @Override
                        public void drop(DropTargetEvent event) {
                            int scrolledH = editor.getVerticalScrollHeight();
                            int scrolledW = editor.getHorizontalScrollWidth();

                            List<SchemaInfo> schemaInfoList = getSelectedSchemaInfos();
                            // this event x, y is full screen's
                            int erdXPoint = event.x - getAppXPoint() - getNavigatorPaneWidth() - 15;
                            int erdYPoint = event.y - getAppYPoint() - 160;
                            // 160 is the distance between ERD canvas top and the CM/CQB
                            // app top
                            addTables(schemaInfoList, erdXPoint + scrolledW, erdYPoint + scrolledH);
                        }
                    });
        }
    }

    private int getAppXPoint() {
        Shell shell = editor.getEditorSite().getShell();
        Point point = shell.getLocation();
        return point.x < 0 ? 0 : point.x;
    }

    private int getAppYPoint() {
        Shell shell = editor.getEditorSite().getShell();
        Point point = shell.getLocation();
        return point.y < 0 ? 0 : point.y;
    }

    private int getNavigatorPaneWidth() {
        SchemaEditorInput sInput = editor.getERSchema().getInput();
        Control control = sInput.getTv().getControl();
        Composite comp = control.getParent();
        org.eclipse.swt.graphics.Rectangle rec = comp.getBounds();
        return rec.width;
    }

    private void addTables(List<SchemaInfo> schemaInfoList, int x, int y) {
        if (schemaInfoList == null) {
            return;
        }

        List<String> existTables = new ArrayList<String>();
        String message = "";
        ERSchema erSchema = editor.getERSchema();
        Iterator<SchemaInfo> it = schemaInfoList.iterator();
        while (it.hasNext()) {
            SchemaInfo table = (SchemaInfo) it.next();
            ERTable existTable = erSchema.getTable(table.getUniqueName());
            if (existTable != null) {
                existTables.add(table.getUniqueName());
                it.remove();
                continue;
            }
        }

        CubridTableParser tableParser = new CubridTableParser(erSchema);
        if (schemaInfoList.size() == 1) {
            tableParser.buildERTables(schemaInfoList, x, y, false);
        } else {
            tableParser.buildERTables(schemaInfoList, x, y, true);
        }
        erSchema.FireAddedTable(tableParser.getSuccessTables());

        Map<String, Exception> failedTables = tableParser.getFailedTables();
        Map<String, List<Constraint>> removedFKs = tableParser.getRemovedFKConstraints();

        if (failedTables.size() > 0) {
            message =
                    Messages.bind(
                            com.cubrid.common.ui.er.Messages.errorAddTables, failedTables.keySet());
        }
        if (existTables.size() > 0) {
            if (!message.equals("")) {
                message += "\n";
            }
            message += Messages.bind(com.cubrid.common.ui.er.Messages.errExistTables, existTables);
        }
        if (removedFKs.size() > 0) {
            if (!message.equals("")) {
                message += "\n";
            }
            message +=
                    Messages.bind(
                            com.cubrid.common.ui.er.Messages.cannotBeBuiltFK,
                            tableParser.getOneRemovedFK().getName());
            if (tableParser.getRemovedFKCount() > 1) {
                message += ", ...";
            }
        }

        if (!message.equals("")) {
            CommonUITool.openErrorBox(message);
        }
    }

    /**
     * Get a clone schemaInfo from database table map cache
     *
     * @param selectedNode ISchemaNode
     * @return SchemaInfo
     */
    private SchemaInfo getSchemaInfo(
            ISchemaNode selectedNode, Connection conn) { // FIXME move this logic to core module
        if (selectedNode == null) {
            return null;
        }
        CubridDatabase db = selectedNode.getDatabase();
        DatabaseInfo dbInfo = db.getDatabaseInfo();
        SchemaInfo tmpTable = dbInfo.getSchemaInfo(selectedNode.getName());
        if (tmpTable == null) {
            return null;
        }
        SchemaInfo schemaInfo = tmpTable.clone();
        getDescInformation(schemaInfo, db, conn);
        return schemaInfo;
    }

    private void getDescInformation(
            SchemaInfo newSchemaInfo,
            CubridDatabase database,
            Connection conn) { // FIXME move this logic to core module
        try {
            IDatabaseSpec dbSpec = database.getDatabaseInfo();
            boolean isSupportTableComment = SchemaCommentHandler.isInstalledMetaTable(dbSpec, conn);
            database.getDatabaseInfo().setSupportTableComment(isSupportTableComment);
            if (CompatibleUtil.isAfter112(database.getDatabaseInfo())) {
                database.getDatabaseInfo().setSupportUserSchema(true);
            }

            if (isSupportTableComment && newSchemaInfo != null) {
                Map<String, SchemaComment> map =
                        SchemaCommentHandler.loadDescription(
                                dbSpec,
                                conn,
                                database.getDatabaseInfo().isSupportUserSchema(),
                                newSchemaInfo.getUniqueName());

                for (DBAttribute attr : newSchemaInfo.getAttributes()) {
                    SchemaComment schemaComment =
                            SchemaCommentHandler.find(
                                    map, newSchemaInfo.getUniqueName(), attr.getName());
                    if (schemaComment != null) {
                        attr.setDescription(schemaComment.getDescription());
                    }
                }

                SchemaComment schemaComment =
                        SchemaCommentHandler.find(map, newSchemaInfo.getUniqueName(), null);
                if (schemaComment != null) {
                    newSchemaInfo.setDescription(schemaComment.getDescription());
                }
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Fill in the selected node
     *
     * @param schemaNodeList all table nodes
     * @return boolean
     */
    private boolean fillInSelectedNode(List<ISchemaNode> schemaNodeList) {
        TreeViewer treeViewer =
                perspectiveTreeviewerMap.get(
                        PerspectiveManager.getInstance().getCurrentPerspectiveId());
        if (treeViewer == null) {
            return false;
        }
        ISelection selection = treeViewer.getSelection();
        if (!(selection instanceof TreeSelection)) {
            return false;
        }
        TreeSelection ts = (TreeSelection) selection;
        Object[] objs = ts.toArray();
        boolean hasSystemTable = false;
        for (Object obj : objs) {
            if (obj instanceof ISchemaNode) {
                ISchemaNode node = (ISchemaNode) obj;
                String type = node.getType();
                if (NodeType.USER_TABLE.equals(type)
                        || NodeType.USER_PARTITIONED_TABLE.equals(type)
                        || NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type)) {
                    schemaNodeList.add(node);
                } else if (NodeType.SYSTEM_TABLE.equals(type)) {
                    hasSystemTable = true;
                }
            }
        }
        if (hasSystemTable) {
            CommonUITool.openInformationBox(com.cubrid.common.ui.er.Messages.cannotDragSystemTable);
        }
        return true;
    }

    /**
     * Replace the SQL, and get clone SchemaInfo list
     *
     * @return boolean
     */
    private List<SchemaInfo> getSelectedSchemaInfos() { // FIXME move this logic to core module

        List<ISchemaNode> schemaNodeList = new ArrayList<ISchemaNode>();
        List<SchemaInfo> validSchemaInfoList = new ArrayList<SchemaInfo>();
        boolean isValid = fillInSelectedNode(schemaNodeList);
        if (!isValid || (schemaNodeList.isEmpty())) {
            return null;
        }

        boolean isSame = checkSourceDB(schemaNodeList);
        if (!isSame) {
            CommonUITool.openInformationBox(com.cubrid.common.ui.er.Messages.errDragTableSource);
            return null;
        }

        Connection conn = null;
        String err = null;
        try {
            conn =
                    JDBCConnectionManager.getConnection(
                            schemaNodeList.get(0).getDatabase().getDatabaseInfo(), false);
            // Get all tables SchemaInfo
            for (ISchemaNode node : schemaNodeList) {
                SchemaInfo tableInfo = getSchemaInfo(node, conn);
                if (tableInfo == null) {
                    String error = node.getDatabase().getDatabaseInfo().getErrorMessage();
                    if (StringUtil.isEmpty(error)) {
                        error = com.cubrid.common.ui.er.Messages.errNotExistDraggedTable;
                    }
                    CommonUITool.openErrorBox(error);
                    return null;
                }
                validSchemaInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            err = e.getMessage();
        } finally {
            QueryUtil.freeQuery(conn);
        }

        if (err != null) {
            CommonUITool.openErrorBox(editor.getGraphicalControl().getShell(), err);
        }

        return validSchemaInfoList;
    }

    /**
     * Check whether these nodes is dragged from current database instance. If the ERD based no real
     * database, the source tables could be dragged from any one dabase(but could only from one at
     * one time).
     *
     * @param schemaNodeList
     * @return
     */
    private boolean checkSourceDB(
            List<ISchemaNode> schemaNodeList) { // FIXME move this logic to core module

        CubridDatabase currentDB = editor.getDatabase();
        if (currentDB.isVirtual()) { // for next step check
            currentDB = schemaNodeList.get(0).getDatabase();
        }
        CubridDatabase db = null;
        for (ISchemaNode selectedNode : schemaNodeList) {
            db = selectedNode.getDatabase();
            if (!currentDB.getId().equals(db.getId())) {
                return false;
            }
        }
        return true;
    }
}
