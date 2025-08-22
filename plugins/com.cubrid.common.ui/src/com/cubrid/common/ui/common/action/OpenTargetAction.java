/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.ThreadUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.cubrid.serial.editor.SerialDashboardEditorPart;
import com.cubrid.common.ui.cubrid.serial.editor.SerialDashboardInput;
import com.cubrid.common.ui.cubrid.synonym.editor.SynonymDashboardEditorPart;
import com.cubrid.common.ui.cubrid.synonym.editor.SynonymDashboardInput;
import com.cubrid.common.ui.cubrid.table.dashboard.control.TableDashboardInput;
import com.cubrid.common.ui.cubrid.table.dashboard.control.TableDashboardPart;
import com.cubrid.common.ui.cubrid.trigger.editor.TriggerDashboardEditorPart;
import com.cubrid.common.ui.cubrid.trigger.editor.TriggerDashboardInput;
import com.cubrid.common.ui.cubrid.user.editor.UserDashboardEditorPart;
import com.cubrid.common.ui.cubrid.user.editor.UsersDashboardInput;
import com.cubrid.common.ui.cubrid.view.editor.ViewDashboardEditorPart;
import com.cubrid.common.ui.cubrid.view.editor.ViewDashboardInput;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.OpenSerialDetailInfoPartProgress;
import com.cubrid.common.ui.spi.progress.OpenSynonymDetailInfoPartProgress;
import com.cubrid.common.ui.spi.progress.OpenTablesDetailInfoPartProgress;
import com.cubrid.common.ui.spi.progress.OpenTriggerDetailInfoPartProgress;
import com.cubrid.common.ui.spi.progress.OpenViewsDetailInfoPartProgress;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.NodeUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Show object tab
 *
 * @author Isaiah Choe
 * @version 1.0 - 2012-05-15 created by Isaiah Choe
 */
public class OpenTargetAction extends SelectionAction {

    public static final String ID = OpenTargetAction.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(OpenTargetAction.class);

    public OpenTargetAction() {
        this(null, null, null, null, null);
    }

    public OpenTargetAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    public OpenTargetAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, enabledIcon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        final Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        for (int i = 0; i < obj.length; i++) {
            if (!NodeUtil.isCubridNode(obj[i])) {
                continue;
            }

            ICubridNode node = (ICubridNode) obj[i];
            if (NodeUtil.isTableViewNode(node)) {
                DefaultSchemaNode table = (DefaultSchemaNode) obj[i];
                showObjectInfo(table);
            } else if (NodeUtil.isTableFolderNode(node)) {
                CubridNavigatorView view = CubridNavigatorView.findNavigationView();

                if (view == null) {
                    return;
                }

                // if not expand ,expand the node and wait until all children be added
                TreeViewer treeViewer = view.getViewer();
                if (!treeViewer.getExpandedState(node)) {
                    treeViewer.expandToLevel(node, 1);
                    while (node.getChildren().size() == 0) {
                        ThreadUtil.sleep(500);
                    }
                }

                showTableDashboard(NodeUtil.getCubridDatabase(node));
            } else if (NodeUtil.isViewFolderNode(node)) {
                CubridNavigatorView view =
                        CubridNavigatorView.getNavigatorView(
                                "com.cubrid.cubridquery.connection.navigator");
                if (view == null) {
                    view =
                            CubridNavigatorView.getNavigatorView(
                                    "com.cubrid.cubridmanager.host.navigator");
                }

                if (view == null) {
                    return;
                }

                // if not expand ,expand the node and wait until all children be added
                TreeViewer treeViewer = view.getViewer();
                if (!treeViewer.getExpandedState(node)) {
                    treeViewer.expandToLevel(node, 1);
                    while (node.getChildren().size() == 0) {
                        ThreadUtil.sleep(500);
                    }
                }

                openViewsDetailInfoEditor(NodeUtil.getCubridDatabase(node));
            } else if (NodeUtil.isSerialFolderNode(node)) {
                CubridNavigatorView view = CubridNavigatorView.findNavigationView();

                if (view == null) {
                    return;
                }

                // if not expand ,expand the node and wait until all children be added
                TreeViewer treeViewer = view.getViewer();
                if (!treeViewer.getExpandedState(node)) {
                    treeViewer.expandToLevel(node, 1);
                    while (node.getChildren().size() == 0) {
                        ThreadUtil.sleep(500);
                    }
                }

                openSerialsDetailInfoEditor(NodeUtil.getCubridDatabase(node), null);
            } else if (NodeUtil.isTriggerFolderNode(node)) {
                CubridNavigatorView view = CubridNavigatorView.findNavigationView();

                if (view == null) {
                    return;
                }

                // if not expand ,expand the node and wait until all children be added
                TreeViewer treeViewer = view.getViewer();
                if (!treeViewer.getExpandedState(node)) {
                    treeViewer.expandToLevel(node, 1);
                    while (node.getChildren().size() == 0) {
                        ThreadUtil.sleep(500);
                    }
                }
                openTriggersDetailInfoEditor(NodeUtil.getCubridDatabase(node));
            } else if (NodeUtil.isSynonymFolderNode(node)) {
                CubridNavigatorView view = CubridNavigatorView.findNavigationView();

                if (view == null) {
                    return;
                }

                TreeViewer treeViewer = view.getViewer();
                if (!treeViewer.getExpandedState(node)) {
                    treeViewer.expandToLevel(node, 1);
                    while (node.getChildren().size() == 0) {
                        ThreadUtil.sleep(500);
                    }
                }
                openSynonymsDetailInfoEditor(NodeUtil.getCubridDatabase(node));
            } else if (NodeUtil.isUserFolderNode(node)) {
                CubridNavigatorView view = CubridNavigatorView.findNavigationView();

                if (view == null) {
                    return;
                }

                // if not expand ,expand the node and wait until all children be added
                TreeViewer treeViewer = view.getViewer();
                if (!treeViewer.getExpandedState(node)) {
                    treeViewer.expandToLevel(node, 1);
                    while (node.getChildren().size() == 0) {
                        ThreadUtil.sleep(500);
                    }
                }
                openUsersDetailInfoEditor(NodeUtil.getCubridDatabase(node));
            }
        }
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(
                obj,
                new String[] {
                    NodeType.USER_TABLE,
                    NodeType.USER_PARTITIONED_TABLE_FOLDER,
                    NodeType.TABLE_FOLDER
                },
                false);
    }

    public void showTableDashboard(CubridDatabase database) {
        if (database == null) {
            return;
        }

        if (database.getDatabaseInfo() == null
                || !JDBCConnectionManager.isConnectable(database.getDatabaseInfo())) {
            return;
        }

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        TableDashboardInput input = new TableDashboardInput(database);
        TableDashboardPart tablesDetailInfoPart =
                (TableDashboardPart)
                        PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow()
                                .getActivePage()
                                .findEditor(input);

        if (tablesDetailInfoPart == null) {
            OpenTablesDetailInfoPartProgress progress =
                    new OpenTablesDetailInfoPartProgress(database);
            progress.loadTablesInfo();
            if (progress.isSuccess()) {
                input.setTableList(progress.getList());
                try {
                    window.getActivePage().openEditor(input, TableDashboardPart.ID);
                } catch (PartInitException e) {
                    LOGGER.error("Can not initialize the db table list UI.", e);
                }
            }
        } else {
            PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .activate(tablesDetailInfoPart);
        }
    }

    /**
     * open view detail info part
     *
     * @param database
     */
    public void openViewsDetailInfoEditor(CubridDatabase database) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == window) {
            return;
        }

        if (database == null) {
            return;
        }

        /*Check it open same editor*/
        IEditorPart editorPart = getOpenedEditorPart(database, ViewDashboardEditorPart.ID);
        if (editorPart == null) {
            OpenViewsDetailInfoPartProgress progress =
                    new OpenViewsDetailInfoPartProgress(database);
            progress.loadViewsInfo();
            if (progress.isSuccess()) {
                ViewDashboardInput input = new ViewDashboardInput(database, progress.getViewList());
                try {
                    window.getActivePage().openEditor(input, ViewDashboardEditorPart.ID);
                } catch (PartInitException e) {
                    LOGGER.error("Can not initialize the view view list UI.", e);
                }
            }
        } else {
            ViewDashboardEditorPart viewsDetailInfoPart = (ViewDashboardEditorPart) editorPart;
            window.getActivePage().activate(viewsDetailInfoPart);
            viewsDetailInfoPart.refresh();
        }
    }

    /**
     * open serial detail info part
     *
     * @param database
     */
    public void openSerialsDetailInfoEditor(CubridDatabase database, String serialName) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == window) {
            return;
        }

        if (database == null) {
            return;
        }

        /*Check it open same editor*/
        IEditorPart editorPart = getOpenedEditorPart(database, SerialDashboardEditorPart.ID);
        if (editorPart == null) {
            OpenSerialDetailInfoPartProgress progress =
                    new OpenSerialDetailInfoPartProgress(database);
            progress.loadSerialInfoList();
            if (progress.isSuccess()) {
                SerialDashboardInput input =
                        new SerialDashboardInput(database, progress.getSerialList());
                try {
                    SerialDashboardEditorPart serialDetailInfoPart = null;
                    serialDetailInfoPart =
                            (SerialDashboardEditorPart)
                                    window.getActivePage()
                                            .openEditor(input, SerialDashboardEditorPart.ID);
                    serialDetailInfoPart.select(serialName);
                } catch (PartInitException e) {
                    LOGGER.error("Can not initialize the serial view list UI.", e);
                }
            }
        } else {
            SerialDashboardEditorPart serialDetailInfoPart = (SerialDashboardEditorPart) editorPart;
            window.getActivePage().activate(serialDetailInfoPart);
            serialDetailInfoPart.refresh();
            serialDetailInfoPart.select(serialName);
        }
    }

    /**
     * open trigger detail info part
     *
     * @param database
     */
    public void openTriggersDetailInfoEditor(CubridDatabase database) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == window) {
            return;
        }

        if (database == null) {
            return;
        }

        /*Check it open same editor*/
        IEditorPart editorPart = getOpenedEditorPart(database, TriggerDashboardEditorPart.ID);
        if (editorPart == null) {
            OpenTriggerDetailInfoPartProgress progress =
                    new OpenTriggerDetailInfoPartProgress(database);
            progress.loadTriggerInfoList();
            if (progress.isSuccess()) {
                TriggerDashboardInput input =
                        new TriggerDashboardInput(database, progress.getTriggerList());
                try {
                    window.getActivePage().openEditor(input, TriggerDashboardEditorPart.ID);
                } catch (PartInitException e) {
                    LOGGER.error("Can not initialize the trigger view list UI.", e);
                }
            }
        } else {
            TriggerDashboardEditorPart triggerDetailInfoPart =
                    (TriggerDashboardEditorPart) editorPart;
            window.getActivePage().activate(triggerDetailInfoPart);
            triggerDetailInfoPart.refresh();
        }
    }

    /**
     * open synonym detail info part
     *
     * @param database
     */
    public void openSynonymsDetailInfoEditor(CubridDatabase database) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == window) {
            return;
        }

        if (database == null) {
            return;
        }

        /*Check it open same editor*/
        IEditorPart editorPart = getOpenedEditorPart(database, SynonymDashboardEditorPart.ID);
        if (editorPart == null) {
            OpenSynonymDetailInfoPartProgress progress =
                    new OpenSynonymDetailInfoPartProgress(database);
            progress.loadSynonymInfoList();
            if (progress.isSuccess()) {
                SynonymDashboardInput input =
                        new SynonymDashboardInput(database, progress.getSynonymList());
                try {
                    window.getActivePage().openEditor(input, SynonymDashboardEditorPart.ID);
                } catch (PartInitException e) {
                    LOGGER.error("Can not initialize the synonym view list UI.", e);
                }
            }
        } else {
            SynonymDashboardEditorPart synonymDetailInfoPart =
                    (SynonymDashboardEditorPart) editorPart;
            window.getActivePage().activate(synonymDetailInfoPart);
            synonymDetailInfoPart.refresh();
        }
    }

    /**
     * open users detail info part
     *
     * @param database
     */
    public void openUsersDetailInfoEditor(CubridDatabase database) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == window) {
            return;
        }

        if (database == null) {
            return;
        }

        /*Check it open same editor*/
        IEditorPart editorPart = getOpenedEditorPart(database, UserDashboardEditorPart.ID);
        if (editorPart == null) {
            UsersDashboardInput input = new UsersDashboardInput(database);
            try {
                editorPart = window.getActivePage().openEditor(input, UserDashboardEditorPart.ID);
            } catch (PartInitException e) {
                LOGGER.error("Can not initialize the users view list UI.", e);
            }
        } else {
            UserDashboardEditorPart userDetailInfoPart = (UserDashboardEditorPart) editorPart;
            window.getActivePage().activate(userDetailInfoPart);
            userDetailInfoPart.refresh();
        }
    }

    /**
     * Get opened IEditorPart
     *
     * @param database CubridDatabase
     * @param editorId String
     * @return
     */
    private IEditorPart getOpenedEditorPart(CubridDatabase database, String editorId) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }
        IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();
        for (IEditorReference reference : editorReferences) {
            if (reference.getId().equals(editorId)) {
                IEditorPart editor = reference.getEditor(false);

                if (editor != null) {
                    if (editor instanceof TriggerDashboardEditorPart) {
                        TriggerDashboardEditorPart triggerDetailInfoPart =
                                (TriggerDashboardEditorPart) editor;
                        if (triggerDetailInfoPart.getDatabase().equals(database)) {
                            return editor;
                        }
                    } else if (editor instanceof SerialDashboardEditorPart) {
                        SerialDashboardEditorPart serialDetailInfoPart =
                                (SerialDashboardEditorPart) editor;
                        if (serialDetailInfoPart.getDatabase().equals(database)) {
                            return editor;
                        }
                    } else if (editor instanceof ViewDashboardEditorPart) {
                        ViewDashboardEditorPart viewsDetailInfoPart =
                                (ViewDashboardEditorPart) editor;
                        if (viewsDetailInfoPart.getDatabase().equals(database)) {
                            return editor;
                        }
                    } else if (editor instanceof UserDashboardEditorPart) {
                        UserDashboardEditorPart userDetailInfoPart =
                                (UserDashboardEditorPart) editor;
                        if (userDetailInfoPart.getDatabase().equals(database)) {
                            return editor;
                        }
                    } else {
                        return editor;
                    }
                }
            }
        }
        return null;
    }

    public void showObjectInfo(DefaultSchemaNode table) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        try {
            QueryEditorPart queryEditPart = null;

            IEditorPart editorPart = window.getActivePage().getActiveEditor();
            if (editorPart != null && editorPart instanceof QueryEditorPart) {
                QueryEditorPart activeQueryEditorPart = (QueryEditorPart) editorPart;
                if (table.getDatabase().equals(activeQueryEditorPart.getSelectedDatabase())) {
                    queryEditPart = activeQueryEditorPart;
                }
            }

            if (queryEditPart != null) {
                queryEditPart.getCombinedQueryComposite().createObjInfoFolder(table);
                window.getActivePage().activate(queryEditPart);
            } else {
                QueryUnit input = new QueryUnit();
                input.setDatabase(table.getDatabase());
                queryEditPart =
                        (QueryEditorPart)
                                window.getActivePage().openEditor(input, QueryEditorPart.ID);
                queryEditPart.connect(table.getDatabase());
                queryEditPart.getCombinedQueryComposite().createObjInfoFolder(table);
            }
        } catch (PartInitException e) {
            LOGGER.error("Can not initialize the query editor UI.", e);
        }
    }
}
