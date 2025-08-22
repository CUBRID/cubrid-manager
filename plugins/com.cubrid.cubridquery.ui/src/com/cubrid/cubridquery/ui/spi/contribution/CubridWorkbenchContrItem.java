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
package com.cubrid.cubridquery.ui.spi.contribution;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.action.OpenTargetAction;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.contribution.WorkbenchContrItem;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.common.action.ShortSetEditorConfigAction;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import com.cubrid.cubridquery.ui.connection.action.EditQueryConnAction;
import com.cubrid.jdbc.proxy.driver.CUBRIDConnectionProxy;
import java.sql.SQLException;
import java.util.Map;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * CUBRID Query workbench contribution item implements <code>WorkbenchContrItem</code> interface
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridWorkbenchContrItem extends WorkbenchContrItem {
    private static final Logger LOGGER = LogUtil.getLogger(CubridWorkbenchContrItem.class);

    /**
     * @see WorkbenchContrItem.processDoubleClickNavigatorEvent(DoubleClickEvent)
     * @param event DoubleClickEvent
     */
    public void processDoubleClickNavigatorEvent(DoubleClickEvent event) {
        ISelection selection = event.getSelection();
        if (selection == null || selection.isEmpty()) {
            return;
        }
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (!(obj instanceof ICubridNode)) {
            return;
        }
        ICubridNode cubridNode = (ICubridNode) obj;
        if (NodeType.DATABASE.equals(cubridNode.getType())) {
            CubridDatabase database = (CubridDatabase) cubridNode;

            if (!database.isLogined() && database.isAutoSavePassword()) {
                DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, false);
                if (EditorConstance.isNeedSetBackground(editorConfig)) {
                    new ShortSetEditorConfigAction(database).run();
                }
            }

            if (database.isLogined()) {
                ActionManager.getInstance().getAction(EditQueryConnAction.ID).run();
            } else if (database.isAutoSavePassword()
                    && connectDatabase(database.getDatabaseInfo())) {
                try {
                    CommonUITool.openQueryEditor(database, true);
                } catch (PartInitException e) {
                    LOGGER.error(e.getMessage(), e);
                }

                CQBGroupNodePersistManager.getInstance().fix();
                database.getLoader().setLoaded(false);
                CubridNavigatorView view =
                        CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
                TreeViewer treeViewer = view.getViewer();
                treeViewer.refresh(database, true);
                treeViewer.expandToLevel(database, 1);

                ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
                LayoutManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.DATABASE_LOGIN));
            } else {
                ActionManager.getInstance().getAction(EditQueryConnAction.ID).run();
            }
        }

        if (cubridNode.getType() == NodeType.TABLE_FOLDER
                || cubridNode.getType() == NodeType.TABLE_FOLDER
                || cubridNode.getType() == NodeType.VIEW_FOLDER
                || cubridNode.getType() == NodeType.TRIGGER_FOLDER
                || cubridNode.getType() == NodeType.SYNONYM_FOLDER
                || cubridNode.getType() == NodeType.SERIAL_FOLDER
                || cubridNode.getType() == NodeType.USER_FOLDER) {

            CubridDatabase database = (CubridDatabase) cubridNode.getParent();
            CubridNavigatorView view =
                    CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
            // if not expand ,expand the node and wait until all children be added
            TreeViewer treeViewer = view.getViewer();

            if (cubridNode.getType() == NodeType.TABLE_FOLDER) {
                if (!treeViewer.getExpandedState(cubridNode)) {
                    treeViewer.expandToLevel(cubridNode, 1);
                    int sleepCount = 0;
                    while (cubridNode.getChildren().size() == 0) {
                        try {
                            Thread.sleep(500);
                            sleepCount++;
                            if (sleepCount > 5) {
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                openTablesDetailInfoPart(database);
            } else if (cubridNode.getType() == NodeType.VIEW_FOLDER) {
                // if not expand ,expand the node
                // if not open child node ,edit serial from dashboard can not open edit dialog
                if (!treeViewer.getExpandedState(cubridNode)) {
                    treeViewer.expandToLevel(cubridNode, 1);
                }
                openViewsDetailInfoPart(database);
            } else if (cubridNode.getType() == NodeType.TRIGGER_FOLDER) {
                // if not expand ,expand the node
                // if not open child node ,edit serial from dashboard can not open edit dialog
                if (!treeViewer.getExpandedState(cubridNode)) {
                    treeViewer.expandToLevel(cubridNode, 1);
                }
                openTriggersDetailInfoPart(database);
            } else if (cubridNode.getType() == NodeType.SYNONYM_FOLDER) {
                if (!treeViewer.getExpandedState(cubridNode)) {
                    treeViewer.expandToLevel(cubridNode, 1);
                }
                openSynonymsDetailInfoPart(database);
            } else if (cubridNode.getType() == NodeType.SERIAL_FOLDER) {
                // if not expand ,expand the node
                // if not open child node ,edit trigger from dashboard can not open edit dialog
                if (!treeViewer.getExpandedState(cubridNode)) {
                    treeViewer.expandToLevel(cubridNode, 1);
                }
                openSerialsDetailInfoPart(database);
            } else if (StringUtil.isEqual(cubridNode.getType(), NodeType.USER_FOLDER)) {
                if (!treeViewer.getExpandedState(cubridNode)) {
                    treeViewer.expandToLevel(cubridNode, 1);
                }
                openUsersDetailInfoPart(database);
            }
        }

        if (cubridNode.getType() == NodeType.SERIAL) {
            CubridDatabase database = (CubridDatabase) cubridNode.getParent().getParent();
            openSerialsDetailInfoPart(database, cubridNode.getName());
        }

        if (!LayoutManager.getInstance().isUseClickOnce()) {
            if (ActionSupportUtil.isSupportMultiSelection(
                    obj,
                    new String[] {
                        NodeType.USER_TABLE, NodeType.USER_VIEW,
                        NodeType.SYSTEM_TABLE, NodeType.SYSTEM_VIEW,
                        NodeType.USER_PARTITIONED_TABLE_FOLDER
                    },
                    false)) {
                openSelectQuery(selection);
            } else {
                openEditorOrView(cubridNode);
            }
        }
    }

    /**
     * openTablesDetailInfoPart
     *
     * @param CubridDatabase database
     */
    public void openTablesDetailInfoPart(CubridDatabase database) {
        OpenTargetAction action = new OpenTargetAction();
        action.showTableDashboard(database);
    }

    /**
     * openViewsDetailInfoPart
     *
     * @param CubridDatabase database
     */
    public void openViewsDetailInfoPart(CubridDatabase database) {
        OpenTargetAction action = new OpenTargetAction();
        action.openViewsDetailInfoEditor(database);
    }

    /**
     * openSerialsDetailInfoPart
     *
     * @param CubridDatabase database
     */
    public void openSerialsDetailInfoPart(CubridDatabase database) {
        OpenTargetAction action = new OpenTargetAction();
        action.openSerialsDetailInfoEditor(database, null);
    }

    /**
     * openSerialsDetailInfoPart
     *
     * @param CubridDatabase database
     */
    public void openSerialsDetailInfoPart(CubridDatabase database, String serialName) {
        OpenTargetAction action = new OpenTargetAction();
        action.openSerialsDetailInfoEditor(database, serialName);
    }

    /**
     * openTriggersDetailInfoPart
     *
     * @param CubridDatabase database
     */
    public void openTriggersDetailInfoPart(CubridDatabase database) {
        OpenTargetAction action = new OpenTargetAction();
        action.openTriggersDetailInfoEditor(database);
    }

    /**
     * openSynonymsDetailInfoPart
     *
     * @param CubridDatabase database
     */
    public void openSynonymsDetailInfoPart(CubridDatabase database) {
        OpenTargetAction action = new OpenTargetAction();
        action.openSynonymsDetailInfoEditor(database);
    }

    /**
     * open user InfoPart
     *
     * @param CubridDatabase database
     */
    public void openUsersDetailInfoPart(CubridDatabase database) {
        OpenTargetAction action = new OpenTargetAction();
        action.openUsersDetailInfoEditor(database);
    }

    public void openSelectQuery(ISelection selection) {
        final Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (!(obj instanceof ICubridNode)) {
            return;
        }

        ISchemaNode table = (ISchemaNode) obj;
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        boolean existsEditor = false;
        QueryEditorPart editor = null;
        QueryUnit input = new QueryUnit();
        input.setDatabase(table.getDatabase());
        try {
            IEditorPart editorPart = window.getActivePage().getActiveEditor();
            if (editorPart != null && editorPart instanceof QueryEditorPart) {
                QueryEditorPart queryEditorPart = (QueryEditorPart) editorPart;
                if (queryEditorPart.getSelectedDatabase() == input.getDatabase()) {
                    editor = (QueryEditorPart) editorPart;
                    existsEditor = true;
                }
            }

            if (editor == null) {
                editor =
                        (QueryEditorPart)
                                window.getActivePage().openEditor(input, QueryEditorPart.ID);
                editor.connect(table.getDatabase());
            }

            DefaultSchemaNode tableNode = (DefaultSchemaNode) obj;
            String sql = getStmtSQL(tableNode) + StringUtil.NEWLINE + StringUtil.NEWLINE;
            if (existsEditor) {
                editor.newQueryTab(sql, true);
            } else {
                editor.setQuery(sql, false, true, false);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Connect the database
     *
     * @param dbInfo DatabaseInfo
     * @return boolean
     */
    public static boolean connectDatabase(DatabaseInfo dbInfo) { // FIXME extract
        if (dbInfo == null || dbInfo.getServerInfo() == null) {
            return false;
        }
        Map<String, String> jdbcVersionMap = CubridJdbcManager.getInstance().getLoadedJdbc();
        if (jdbcVersionMap == null
                || jdbcVersionMap.get(dbInfo.getServerInfo().getJdbcDriverVersion()) == null) {
            return false;
        }
        CUBRIDConnectionProxy connection = null;
        try {
            connection = (CUBRIDConnectionProxy) JDBCConnectionManager.getConnection(dbInfo, false);

            DbUserInfo userInfo = dbInfo.getAuthLoginedDbUserInfo();
            IsDBAUserTask checkTask = new IsDBAUserTask(dbInfo);
            checkTask.execute();
            userInfo.setDbaAuthority(checkTask.isDBAUser());

            dbInfo.setLogined(true);
            dbInfo.setRunningType(DbRunningType.CS);
            dbInfo.getServerInfo().setConnected(true);
            dbInfo.setLogined(true);
            dbInfo.setSupportUserSchema(CompatibleUtil.isAfter112(dbInfo));
            return true;
        } catch (SQLException e) {
            CommonUITool.openErrorBox(
                    Messages.bind(Messages.errCommonTip, e.getErrorCode(), e.getMessage()));
            return false;
        } catch (Exception e) {
            CommonUITool.openErrorBox(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException e) {
                    connection = null;
                }
            }
        }
    }

    /**
     * Connect the database
     *
     * @param dbInfo DatabaseInfo
     * @return error messages String
     */
    public static String connectDatabaseWithErrMsg(DatabaseInfo dbInfo) { // FIXME extract
        if (dbInfo == null || dbInfo.getServerInfo() == null) {
            return "";
        }
        Map<String, String> jdbcVersionMap = CubridJdbcManager.getInstance().getLoadedJdbc();
        if (jdbcVersionMap == null
                || jdbcVersionMap.get(dbInfo.getServerInfo().getJdbcDriverVersion()) == null) {
            return Messages.errNoSupportDriver;
        }
        CUBRIDConnectionProxy connection = null;
        try {
            connection = (CUBRIDConnectionProxy) JDBCConnectionManager.getConnection(dbInfo, false);

            DbUserInfo userInfo = dbInfo.getAuthLoginedDbUserInfo();
            IsDBAUserTask checkTask = new IsDBAUserTask(dbInfo);
            checkTask.execute();
            userInfo.setDbaAuthority(checkTask.isDBAUser());

            dbInfo.setLogined(true);
            dbInfo.setRunningType(DbRunningType.CS);
            dbInfo.getServerInfo().setConnected(true);
            dbInfo.setLogined(true);
            return null;
        } catch (SQLException e) {
            return Messages.bind(Messages.errCommonTip, e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException e) {
                    connection = null;
                }
            }
        }
    }
}
