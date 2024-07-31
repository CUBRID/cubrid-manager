/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.editor;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.CombinedQueryEditorComposite;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;

/**
 * A class to control navigator view drag and drop
 *
 * @author wangsl 2009-5-18
 */
public class QueryEditorDNDController {

    private final QueryEditorPart editor;
    private static Map<String, TreeViewer> perspectiveTreeviewerMap =
            new HashMap<String, TreeViewer>();

    public QueryEditorDNDController(QueryEditorPart editor) {
        this.editor = editor;
    }

    /**
     * Register the drag source
     *
     * @param treeViewer TreeViewer
     */
    public static void registerDragSource(String perspectiveId, TreeViewer treeViewer) {
        synchronized (QueryEditorDNDController.class) {
            if (perspectiveId != null && treeViewer != null) {
                perspectiveTreeviewerMap.put(perspectiveId, treeViewer);
            }
        }
    }

    /**
     * register drag source and text editor target
     *
     * @param combinedQueryComposite CombinedQueryEditorComposite
     */
    public void registerDropTarget(CombinedQueryEditorComposite combinedQueryComposite) {
        synchronized (this) {
            DropTarget sqlTarget =
                    new DropTarget(
                            combinedQueryComposite.getSqlEditorComp().getText(), DND.DROP_MOVE);
            sqlTarget.setTransfer(new Transfer[] {TextTransfer.getInstance()});
            sqlTarget.addDropListener(
                    new DropTargetAdapter() {
                        public void drop(DropTargetEvent event) {
                            replaceSql();
                        }
                    });
        }
    }

    /**
     * register result table target, connect drag target with drag source
     *
     * @param table Table
     */
    public void addTableDropTarget(Table table) {
        DropTarget resultTarget = new DropTarget(table, DND.DROP_MOVE);
        resultTarget.setTransfer(new Transfer[] {TextTransfer.getInstance()});
        resultTarget.addDropListener(
                new DropTargetAdapter() {
                    public void drop(DropTargetEvent event) {
                        boolean isSuccess = replaceSql();
                        if (isSuccess) {
                            editor.getRunItem().notifyListeners(SWT.Selection, new Event());
                        }
                    }
                });
    }

    /**
     * Get the script
     *
     * @param selectedNode ISchemaNode
     * @return script
     */
    private String getScript(ISchemaNode selectedNode) { // FIXME move this logic to core module
        if (selectedNode == null) {
            return null;
        }
        CubridDatabase db = selectedNode.getDatabase();
        DatabaseInfo dbInfo = db.getDatabaseInfo();
        GetAllAttrTask task = new GetAllAttrTask(dbInfo);
        task.setClassName(selectedNode.getName());
        task.getAttrList();
        if (task.getErrorMsg() != null) {
            CommonUITool.openErrorBox(task.getErrorMsg());
            return null;
        }
        List<DBAttribute> allAttrList = task.getAllAttrList();
        String sql = SQLGenerateUtils.getSelectSQLWithLimit(selectedNode.getName(), allAttrList);
        if (sql == null) {
            return "";
        }

        try {
            return new SqlFormattingStrategy().format(sql).trim()
                    + StringUtil.NEWLINE
                    + StringUtil.NEWLINE;
        } catch (Exception ignored) {
            return sql.trim() + StringUtil.NEWLINE + StringUtil.NEWLINE;
        }
    }

    /**
     * Get the script
     *
     * @param tableName String
     * @param columnNodeList List<ISchemaNode>
     * @return String
     */
    private String getScript(
            String tableName,
            List<ISchemaNode> columnNodeList) { // FIXME move this logic to core module
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        for (ISchemaNode node : columnNodeList) {
            String escapedColumnName = QuerySyntax.escapeKeyword(node.getName().split(",")[0]);
            sql.append(" ").append(escapedColumnName).append(", ");
        }
        sql.append(" FROM ")
                .append(QuerySyntax.escapeKeyword(tableName))
                .append(";")
                .append(StringUtil.NEWLINE);
        return sql.toString();
    }

    /**
     * Fill in the selected node
     *
     * @param schemaNodeList all table nodes
     * @param columnNodeMap all columns node
     * @return boolean
     */
    private boolean fillInSelectedNode(
            List<ISchemaNode> schemaNodeList, Map<String, List<ISchemaNode>> columnNodeMap) {
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
        String dbId = null;
        for (Object obj : objs) {
            if (obj instanceof ISchemaNode) {
                ISchemaNode node = (ISchemaNode) obj;
                CubridDatabase database = node.getDatabase();
                if (dbId == null) {
                    dbId = database.getId();
                }
                if (!dbId.equals(database.getId())) {
                    return false;
                }
                String type = node.getType();
                if (NodeType.SYSTEM_TABLE.equals(type)
                        || NodeType.SYSTEM_VIEW.equals(type)
                        || NodeType.USER_TABLE.equals(type)
                        || NodeType.USER_PARTITIONED_TABLE.equals(type)
                        || NodeType.USER_VIEW.equals(type)
                        || NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(type)) {
                    schemaNodeList.add(node);
                } else if (NodeType.TABLE_COLUMN.equals(type)) {
                    String name = node.getParent().getParent().getName();
                    List<ISchemaNode> columnNodeList = columnNodeMap.get(name);
                    if (columnNodeList == null) {
                        columnNodeList = new ArrayList<ISchemaNode>();
                        columnNodeMap.put(name, columnNodeList);
                    }
                    columnNodeList.add(node);
                }
            }
        }
        return true;
    }

    /**
     * Open transaction confirm dialog
     *
     * @param selectedDb the CubridDatabase
     * @return <code>true</code> if continuous;<code>false</code>otherwise
     */
    private boolean openTransactionDialog(final CubridDatabase selectedDb) {
        String title = com.cubrid.common.ui.common.Messages.titleConfirm;
        String msg = Messages.bind(Messages.connCloseConfirm, new String[] {selectedDb.getLabel()});
        String[] buttons = new String[] {Messages.btnYes, Messages.btnNo, Messages.cancel};
        MessageDialog dialog =
                new MessageDialog(
                        editor.getSite().getShell(),
                        title,
                        null,
                        msg,
                        MessageDialog.QUESTION,
                        buttons,
                        2) {
                    protected void buttonPressed(int buttonId) {
                        switch (buttonId) {
                            case 0:
                                editor.commit();
                                setReturnCode(0);
                                close();
                                break;
                            case 1:
                                editor.rollback();
                                setReturnCode(1);
                                close();
                                break;
                            case 2:
                                setReturnCode(2);
                                close();
                                break;
                            default:
                                break;
                        }
                    }
                };

        int returnVal = dialog.open();
        if (returnVal == 2 || returnVal == -1) {
            return false;
        }

        return true;
    }

    /**
     * Open confirm dialog
     *
     * @return the button id
     */
    private int openConfirmDialog() {
        String title = com.cubrid.common.ui.common.Messages.titleConfirm;
        String[] buttons = new String[] {Messages.btnYes, Messages.btnNo, Messages.cancel};
        MessageDialog dialog =
                new MessageDialog(
                        editor.getSite().getShell(),
                        title,
                        null,
                        Messages.changeDbConfirm,
                        MessageDialog.QUESTION,
                        buttons,
                        0) {
                    protected void buttonPressed(int buttonId) {
                        switch (buttonId) {
                            case 0:
                                setReturnCode(0);
                                close();
                                break;
                            case 1:
                                setReturnCode(1);
                                close();
                                break;
                            case 2:
                                setReturnCode(2);
                                close();
                                break;
                            default:
                                break;
                        }
                    }
                };

        return dialog.open();
    }

    /**
     * Replace the SQL
     *
     * @return boolean
     */
    private boolean replaceSql() { // FIXME move this logic to core module
        List<ISchemaNode> schemaNodeList = new ArrayList<ISchemaNode>();
        Map<String, List<ISchemaNode>> columnNodeMap = new HashMap<String, List<ISchemaNode>>();
        boolean isValid = fillInSelectedNode(schemaNodeList, columnNodeMap);
        if (!isValid || (schemaNodeList.isEmpty() && columnNodeMap.isEmpty())) {
            return false;
        }

        // Confirm whether change database connection
        CubridDatabase db = null;
        for (ISchemaNode selectedNode : schemaNodeList) {
            db = selectedNode.getDatabase();
            break;
        }

        if (db == null) {
            Iterator<List<ISchemaNode>> it = columnNodeMap.values().iterator();
            while (it.hasNext()) {
                db = it.next().get(0).getDatabase();
                break;
            }
        }
        if (db == null) {
            return false;
        }

        CubridDatabase selectedDb = editor.getSelectedDatabase();
        if (!selectedDb.getId().equals(db.getId())) {
            int returnVal = openConfirmDialog();
            if (returnVal == 2 || returnVal == -1) {
                return false;
            }

            if (editor.isTransaction() && returnVal == 0 && !openTransactionDialog(selectedDb)) {
                return false;
            }

            if (returnVal == 0) {
                editor.shutDownConnection();
                editor.connect(db);
            }
        }

        // Get all table SQL
        StringBuilder sqlBuffer = new StringBuilder();
        for (ISchemaNode selectedNode : schemaNodeList) {
            String sql = getScript(selectedNode);
            if (sql != null) {
                sqlBuffer.append(sql);
            }
        }

        // Get the selected columns SQL
        Iterator<Map.Entry<String, List<ISchemaNode>>> it = columnNodeMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<ISchemaNode>> entry = it.next();
            String sql = getScript(entry.getKey(), entry.getValue());
            if (sql != null) {
                sqlBuffer.append(sql);
            }
        }

        if (sqlBuffer.length() > 0) {
            String sql = sqlBuffer.toString();
            int originalQueryLength = editor.getAllQueries().length();
            int newQueryLength = sql.length();
            int caretOffset = editor.getSqlEditorWidget().getCaretOffset();

            if (originalQueryLength > 0 && caretOffset >= 0) {
                String pre = editor.getAllQueries().substring(0, caretOffset);
                String post =
                        editor.getAllQueries()
                                .substring(caretOffset, editor.getAllQueries().length());
                editor.getSqlEditorWidget().setText(pre + sql + post);
                editor.getSqlEditorWidget().setSelection(caretOffset);
            } else {
                editor.setQuery(sql, true, false, false);
                int endIndexOfSelection = originalQueryLength + newQueryLength;
                editor.setSelection(originalQueryLength, endIndexOfSelection);
            }

            ActionManager.getInstance().changeFocusProvider(editor.getSqlEditorWidget());
            return true;
        }

        return false;
    }
}
