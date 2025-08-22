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
package com.cubrid.cubridquery.ui.common.control;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.query.control.DatabaseMenuItem;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.CubridQueryUIPlugin;
import com.cubrid.cubridquery.ui.common.dialog.LoginQueryEditorDialog;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * A Toolbar Control to show the query editor toolItem and database selection menu
 *
 * @author pangqiren 2009-3-2
 */
public class CQDatabaseNavigatorMenu extends DatabaseNavigatorMenu {

    /** load all database on all server. if not login or database not started, the item disabled. */
    public void loadNavigatorMenu() {
        loadNavigatorMenu(dbSelectionMenu);
    }

    /**
     * load all database on all server. if not login or database not started, the item disabled.
     *
     * @param dbSelectionMenu
     */
    public void loadNavigatorMenu(Menu dbSelectionMenu) {
        setSelectedImage(CubridQueryUIPlugin.getImage("/icons/navigator/eclipse_dot.png"));

        CubridNavigatorView navigatorView =
                CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
        boolean isShowGroup = navigatorView == null ? false : navigatorView.isShowGroup();
        List<CubridGroupNode> groupList =
                CQBGroupNodePersistManager.getInstance().getAllGroupNodes();
        if (groupList == null || groupList.isEmpty()) {
            return;
        }

        Collections.sort(
                groupList,
                new Comparator<CubridGroupNode>() {
                    public int compare(CubridGroupNode o1, CubridGroupNode o2) {
                        if (o1 == null
                                || o2 == null
                                || o1.getLabel() == null
                                || o2.getLabel() == null) {
                            return 0;
                        }
                        return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                    }
                });

        int count = 0;
        for (CubridGroupNode groupNode : groupList) {
            //			if (NodeFilterManager.getInstance().isHidden(groupNode)) {
            //				continue;
            //			}

            List<ICubridNode> nodeList = groupNode.getChildren();
            if (nodeList == null || nodeList.isEmpty()) {
                continue;
            }

            DatabaseMenuItem groupMenuItem = null;
            Menu groupSub = null;
            String groupName = null;

            if (isShowGroup) {
                groupName = groupNode.getLabel();

                groupMenuItem =
                        new DatabaseMenuItem(groupNode.getId(), dbSelectionMenu, SWT.CASCADE);
                groupMenuItem.setImage(CubridQueryUIPlugin.getImage("/icons/navigator/group.png"));
                groupMenuItem.setText(groupNode.getLabel());
                groupSub = new Menu(dbSelectionMenu);
                groupMenuItem.setMenu(groupSub);
            }

            Collections.sort(
                    nodeList,
                    new Comparator<ICubridNode>() {
                        public int compare(ICubridNode o1, ICubridNode o2) {
                            if (o1 == null
                                    || o2 == null
                                    || o1.getLabel() == null
                                    || o2.getLabel() == null) {
                                return 0;
                            }
                            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                        }
                    });

            int itemCountInGroup = 0;
            for (ICubridNode node : nodeList) {
                //				if (NodeFilterManager.getInstance().isHidden(node)) {
                //					continue;
                //				}
                if (node instanceof CubridDatabase) {
                    CubridDatabase database = (CubridDatabase) node;
                    if (!database.isLogined()) {
                        continue;
                    }

                    itemCountInGroup++;

                    DatabaseMenuItem dbItem = null;
                    if (isShowGroup) {
                        dbItem = new DatabaseMenuItem(database.getId(), groupSub, SWT.RADIO);
                    } else {
                        dbItem = new DatabaseMenuItem(database.getId(), dbSelectionMenu, SWT.RADIO);
                    }

                    dbItem.setText(database.getLabelWithUser());
                    dbItem.setDatabase((CubridDatabase) database);
                    dbItem.setEnabled(
                            database.isLogined() && database.getRunningType() == DbRunningType.CS);
                    dbItem.setGroupName(groupName);

                    count++;
                }
            }

            // If it has no active item on the group, it's group should be disposed
            if (isShowGroup && itemCountInGroup <= 0 && groupSub != null && groupMenuItem != null) {
                groupSub.dispose();
                groupMenuItem.dispose();
            }
        }

        if (count > 0) {
            new MenuItem(dbSelectionMenu, SWT.SEPARATOR);
        }
    }

    /**
     * When click self-connection menu item, handle with this event
     *
     * @param dbItem DatabaseMenuItem
     * @return boolean
     */
    public boolean handleWithSelfConn(DatabaseMenuItem dbItem) {
        LoginQueryEditorDialog dialog = new LoginQueryEditorDialog(parent.getShell());
        dialog.setSelectedConnName(DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL);
        if (dialog.open() == IDialogConstants.OK_ID) {
            return true;
        } else {
            dbItem.setSelection(false);
            return false;
        }
    }

    /**
     * return a text of selected database on the query editor
     *
     * @param database
     * @return
     */
    public String getDatabaseLabel(CubridDatabase cubridDatabase) {
        return cubridDatabase.getLabelWithUser();
    }
}
