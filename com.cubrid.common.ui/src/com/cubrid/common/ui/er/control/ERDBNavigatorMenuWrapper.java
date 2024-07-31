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
package com.cubrid.common.ui.er.control;

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.DatabaseMenuItem;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A pop-up menu in ER-D canvas toolbar, it can be override to customize the pop-up menu items.
 * Reuse the <code>DatabaseNavigatorMenu</code>.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-25 created by Yu Guojia
 */
public class ERDBNavigatorMenuWrapper extends DatabaseNavigatorMenu {
    private final DatabaseNavigatorMenu dbMenu;
    private ERSchemaEditor erSchemaEditor;

    public ERDBNavigatorMenuWrapper(DatabaseNavigatorMenu dbMenu, ERSchemaEditor erSchemaEditor) {
        this.dbMenu = dbMenu;
        this.erSchemaEditor = erSchemaEditor;
    }

    /**
     * load all database on all server. if not login or database not started, the item disabled. The
     * dbMenu is a proxy of CMDatabaseNavigatorMenu or CQDatabaseNavigatorMenu
     */
    public void loadNavigatorMenu() {
        dbMenu.loadNavigatorMenu(dbSelectionMenu);
    }

    /** Add selection listener */
    public void addSelectionListener() { // FIXME need modulation because of
        // complicated codes
        MenuItem[] allNodes = getAllMenuNodes();
        for (final MenuItem item : allNodes) {
            item.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            if (item.getStyle() != SWT.CHECK && item.getStyle() != SWT.RADIO) {
                                CommonUITool.openErrorBox(parent.getShell(), Messages.plsSelectDb);
                                return;
                            }
                            CubridDatabase oldSelectedDb = (CubridDatabase) getSelectedDb();
                            final DatabaseMenuItem dbItem = (DatabaseMenuItem) item;
                            CubridDatabase selectedDb = dbItem.getDatabase();

                            if (selectedDb != null && selectedDb.getId().equals(IND_DATABASE_ID)) {
                                if (oldSelectedDb == null) {
                                    return;
                                }

                                dbItem.setSelection(false);

                                // on CM mode
                                if (ApplicationType.CUBRID_MANAGER.equals(
                                        PerspectiveManager.getInstance().getCurrentMode())) {
                                    CubridNavigatorView navigatorView =
                                            CubridNavigatorView.getNavigatorView(
                                                    CubridNavigatorView.ID_CM);
                                    if (navigatorView != null) {
                                        TreeViewer treeViewer = navigatorView.getViewer();
                                        Tree tree = treeViewer.getTree();
                                        if (tree == null) {
                                            return;
                                        }
                                        for (int i = 0; i < tree.getItemCount(); i++) {
                                            TreeItem itm = tree.getItem(i);
                                            if (itm == null) {
                                                continue;
                                            }

                                            showDatabaseOnEditingWithHostOnGroup(
                                                    dbItem, oldSelectedDb, treeViewer, tree, itm);
                                        }
                                        return;
                                    }
                                } else if (ApplicationType.CUBRID_QUERY_BROWSER.equals(
                                        PerspectiveManager.getInstance().getCurrentMode())) {
                                    // on CQB mode
                                    CubridNavigatorView navigatorView =
                                            CubridNavigatorView.getNavigatorView(
                                                    CubridNavigatorView.ID_CQB);
                                    if (navigatorView != null) {
                                        TreeViewer treeViewer = navigatorView.getViewer();
                                        Tree tree = treeViewer.getTree();
                                        if (tree == null) {
                                            return;
                                        }
                                        for (int i = 0; i < tree.getItemCount(); i++) {
                                            TreeItem itm = tree.getItem(i);
                                            if (itm == null) {
                                                continue;
                                            }

                                            showDatabaseOnEditingOnGroup(
                                                    oldSelectedDb, treeViewer, tree, itm);
                                        }
                                        return;
                                    }
                                }

                                return;
                            } else if (oldSelectedDb != null
                                    && selectedDb != null
                                    && oldSelectedDb.getId().equals(selectedDb.getId())) {
                                return;
                            } else if (selectedDb != null
                                    && selectedDb.getId().equals(SELF_DATABASE_ID)) {
                                if (!NULL_DATABASE_ID.equals(oldSelectedDb.getId())) {
                                    boolean confirm =
                                            CommonUITool.openConfirmBox(
                                                    erSchemaEditor.getSite().getShell(),
                                                    Messages.changeDbConfirm);
                                    if (!confirm) {
                                        dbItem.setSelection(false);
                                        return;
                                    }
                                }
                                if (!handleWithSelfConn(dbItem)) {
                                    return;
                                }
                            } else if (oldSelectedDb != null
                                    && !oldSelectedDb.getId().equals(NULL_DATABASE_ID)) {
                                boolean confirm =
                                        CommonUITool.openConfirmBox(
                                                erSchemaEditor.getSite().getShell(),
                                                Messages.changeDbConfirm);
                                if (!confirm) {
                                    dbItem.setSelection(false);
                                    return;
                                }
                            }
                            selectMenuItem(dbItem);
                        }

                        // on cm
                        private void showDatabaseOnEditingWithHost(
                                final CubridDatabase oldSelectedDb,
                                final TreeViewer treeViewer,
                                final Tree tree,
                                final TreeItem itm) {
                            if (itm != null
                                    && itm.getData() != null
                                    && itm.getData() instanceof ICubridNode
                                    && NodeType.SERVER.equals(
                                            ((ICubridNode) itm.getData()).getType())) {
                                String serverName =
                                        ((ICubridNode) itm.getData()).getServer().getServerName();
                                if (serverName == null
                                        || oldSelectedDb == null
                                        || oldSelectedDb.getServer() == null) {
                                    return;
                                }

                                if (!serverName.equals(oldSelectedDb.getServer().getName())) {
                                    return;
                                }

                                if (treeViewer != null && !itm.getExpanded()) {
                                    treeViewer.expandToLevel(itm.getData(), 1);
                                }

                                Display.getDefault()
                                        .timerExec(
                                                100,
                                                new Runnable() {
                                                    public void run() {
                                                        showDatabaseOnEditingWithHostLoop(
                                                                oldSelectedDb, tree, itm, 1);
                                                    }
                                                });
                            }
                        }

                        // on cqb
                        private void showDatabaseOnEditing(
                                CubridDatabase oldSelectedDb,
                                TreeViewer treeViewer,
                                Tree tree,
                                TreeItem itm) {
                            ISchemaNode cNode = (ISchemaNode) itm.getData();
                            String user = cNode.getDatabase().getUserName();
                            String dbName = cNode.getDatabase().getDatabaseInfo().getDbName();
                            String hostName = cNode.getDatabase().getDatabaseInfo().getBrokerIP();
                            if (user == null || dbName == null || hostName == null) {
                                return;
                            }
                            if (user.equals(oldSelectedDb.getUserName())
                                    && dbName.equals(oldSelectedDb.getDatabaseInfo().getDbName())
                                    && hostName.equals(
                                            oldSelectedDb.getDatabaseInfo().getBrokerIP())) {
                                tree.setSelection(itm);
                                tree.setTopItem(itm);

                                final String origDbName = itm.getText();
                                final TreeItem updatableItem = itm;
                                itm.setText("[ [ [ " + origDbName + " ] ] ]");
                                Display.getDefault()
                                        .timerExec(
                                                500,
                                                new Runnable() {
                                                    public void run() {
                                                        updatableItem.setText(
                                                                "[ [ " + origDbName + " ] ]");
                                                    }
                                                });
                                Display.getDefault()
                                        .timerExec(
                                                530,
                                                new Runnable() {
                                                    public void run() {
                                                        updatableItem.setText(
                                                                "[ " + origDbName + " ]");
                                                    }
                                                });
                                Display.getDefault()
                                        .timerExec(
                                                560,
                                                new Runnable() {
                                                    public void run() {
                                                        updatableItem.setText(origDbName);
                                                    }
                                                });
                            }
                        }

                        private void showDatabaseOnEditingWithHostLoop(
                                final CubridDatabase oldSelectedDb,
                                final Tree tree,
                                final TreeItem itm,
                                final int count) {
                            if (count > 10) {
                                return;
                            }

                            boolean ok = false;
                            try {
                                for (int j = 0; j < itm.getItemCount(); j++) {
                                    TreeItem sub = itm.getItem(j);
                                    if (sub.getData() instanceof ICubridNode
                                            && NodeType.DATABASE_FOLDER.equals(
                                                    ((ICubridNode) sub.getData()).getType())) {
                                        ok = true;
                                        for (int k = 0; k < sub.getItemCount(); k++) {
                                            TreeItem dbNode = sub.getItem(k);
                                            if (dbNode == null || dbNode.getData() == null) {
                                                continue;
                                            }
                                            String dbName =
                                                    ((ICubridNode) dbNode.getData()).getName();
                                            if (dbName != null
                                                    && dbName.equals(oldSelectedDb.getName())) {
                                                tree.setSelection(dbNode);
                                                tree.setTopItem(dbNode);

                                                final String origDbName = dbNode.getText();
                                                final TreeItem updatableItem = dbNode;
                                                dbNode.setText("[ [ [ " + origDbName + " ] ] ]");
                                                Display.getDefault()
                                                        .timerExec(
                                                                500,
                                                                new Runnable() {
                                                                    public void run() {
                                                                        updatableItem.setText(
                                                                                "[ [ "
                                                                                        + origDbName
                                                                                        + " ] ]");
                                                                    }
                                                                });
                                                Display.getDefault()
                                                        .timerExec(
                                                                530,
                                                                new Runnable() {
                                                                    public void run() {
                                                                        updatableItem.setText(
                                                                                "[ "
                                                                                        + origDbName
                                                                                        + " ]");
                                                                    }
                                                                });
                                                Display.getDefault()
                                                        .timerExec(
                                                                560,
                                                                new Runnable() {
                                                                    public void run() {
                                                                        updatableItem.setText(
                                                                                origDbName);
                                                                    }
                                                                });
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ignored) {
                                ok = false;
                            }

                            if (!ok) {
                                final int nextCount = count + 1;
                                Display.getDefault()
                                        .timerExec(
                                                500,
                                                new Runnable() {
                                                    public void run() {
                                                        showDatabaseOnEditingWithHostLoop(
                                                                oldSelectedDb,
                                                                tree,
                                                                itm,
                                                                nextCount);
                                                    }
                                                });
                            }
                        }

                        // on group mode
                        private void showDatabaseOnEditingWithHostOnGroup(
                                final DatabaseMenuItem dbItem,
                                final CubridDatabase oldSelectedDb,
                                final TreeViewer treeViewer,
                                final Tree tree,
                                final TreeItem item) {
                            if (item == null || item.getData() == null) {
                                return;
                            }
                            if (item.getData() instanceof CubridGroupNode) {
                                CubridGroupNode grp = (CubridGroupNode) item.getData();
                                if (lastSelectedDatabaseMenu == null
                                        || lastSelectedDatabaseMenu.getGroupName() == null
                                        || grp == null
                                        || !lastSelectedDatabaseMenu
                                                .getGroupName()
                                                .equals(grp.getName())) {
                                    return;
                                }

                                if (treeViewer != null && !item.getExpanded()) {
                                    treeViewer.expandToLevel(item.getData(), 1);
                                    Display.getDefault()
                                            .timerExec(
                                                    500,
                                                    new Runnable() {
                                                        public void run() {
                                                            for (int i = 0;
                                                                    i < item.getItemCount();
                                                                    i++) {
                                                                final TreeItem itm =
                                                                        item.getItem(i);
                                                                if (itm == null) {
                                                                    continue;
                                                                }

                                                                showDatabaseOnEditingWithHost(
                                                                        oldSelectedDb, treeViewer,
                                                                        tree, itm);
                                                            }
                                                        }
                                                    });
                                } else {
                                    for (int i = 0; i < item.getItemCount(); i++) {
                                        final TreeItem itm = item.getItem(i);
                                        if (itm == null) {
                                            continue;
                                        }

                                        showDatabaseOnEditingWithHost(
                                                oldSelectedDb, treeViewer, tree, itm);
                                    }
                                }
                            } else {
                                showDatabaseOnEditingWithHost(
                                        oldSelectedDb, treeViewer, tree, item);
                            }
                        }

                        // on group mode
                        private void showDatabaseOnEditingOnGroup(
                                final CubridDatabase oldSelectedDb,
                                final TreeViewer treeViewer,
                                final Tree tree,
                                final TreeItem item) {
                            if (item == null || item.getData() == null) {
                                return;
                            }
                            if (item.getData() instanceof CubridGroupNode) {
                                CubridGroupNode grp = (CubridGroupNode) item.getData();
                                if (lastSelectedDatabaseMenu == null
                                        || lastSelectedDatabaseMenu.getGroupName() == null
                                        || grp == null
                                        || !lastSelectedDatabaseMenu
                                                .getGroupName()
                                                .equals(grp.getName())) {
                                    return;
                                }

                                if (treeViewer != null && !item.getExpanded()) {
                                    treeViewer.expandToLevel(item.getData(), 1);
                                    Display.getDefault()
                                            .timerExec(
                                                    500,
                                                    new Runnable() {
                                                        public void run() {
                                                            for (int i = 0;
                                                                    i < item.getItemCount();
                                                                    i++) {
                                                                final TreeItem itm =
                                                                        item.getItem(i);
                                                                if (itm == null) {
                                                                    continue;
                                                                }

                                                                showDatabaseOnEditing(
                                                                        oldSelectedDb,
                                                                        treeViewer,
                                                                        tree,
                                                                        itm);
                                                            }
                                                        }
                                                    });
                                } else {
                                    for (int i = 0; i < item.getItemCount(); i++) {
                                        final TreeItem itm = item.getItem(i);
                                        if (itm == null) {
                                            continue;
                                        }

                                        showDatabaseOnEditing(oldSelectedDb, treeViewer, tree, itm);
                                    }
                                }
                            } else {
                                showDatabaseOnEditing(oldSelectedDb, treeViewer, tree, item);
                            }
                        }
                    });
        }
    }

    /** When tree node in navigation view change, refresh the database list */
    public void refresh() {
        Display.getDefault()
                .asyncExec(
                        new Runnable() {
                            /** @see org.eclipse.jface.action.Action#run() */
                            public void run() {
                                loadDatabaseMenu();
                                // add for extend function
                            }
                        });
    }

    /**
     * Set the database
     *
     * @param database CubridDatabase
     */
    public void setDatabase(CubridDatabase database) {
        // show the selected database menu item
        if ((database.isLogined() && database.getRunningType() == DbRunningType.CS)) {
            DatabaseMenuItem item = findById(database.getId());
            selectMenuItem(item);
        }
    }

    /**
     * target a database selection change
     *
     * @param item DatabaseMenuItem
     */
    public void selectMenuItem(DatabaseMenuItem item) {
        DatabaseMenuItem tmpItem = item;
        if (tmpItem != null) {
            if (selectedMenuItem != null && !selectedMenuItem.isDisposed()) {
                selectedMenuItem.setSelection(false);
            }

            if (listener != null) {
                Event e = new Event();
                e.data = tmpItem.getDatabase();
                listener.handleEvent(e);
            }

            tmpItem.setSelection(true);
            selectedMenuItem = tmpItem;
            setText(tmpItem);
            selectdDb = tmpItem.getDatabase();

            /* Save current selectDB */
            lastSelectdDb = selectdDb;
            lastUser = (selectdDb == null ? null : selectdDb.getUserName());

            LayoutManager.getInstance()
                    .getTitleLineContrItem()
                    .changeTitleForQueryEditor(selectdDb);
            LayoutManager.getInstance()
                    .getStatusLineContrItem()
                    .changeStuatusLineForViewOrEditPart(selectdDb, erSchemaEditor);
            erSchemaEditor.changeDataBase(lastSelectdDb);
        }
    }

    /**
     * When click self-connection menu item, handle with this event
     *
     * @param dbItem DatabaseMenuItem
     * @return boolean
     */
    public boolean handleWithSelfConn(DatabaseMenuItem dbItem) {

        return dbMenu.handleWithSelfConn(dbItem);
    }

    /**
     * return a text of selected database on the query editor
     *
     * @param database
     * @return
     */
    public String getDatabaseLabel(CubridDatabase cubridDatabase) {
        return dbMenu.getDatabaseLabel(cubridDatabase);
    }

    public void setEditor(ERSchemaEditor erSchemaEditor) {
        this.erSchemaEditor = erSchemaEditor;
    }
}
