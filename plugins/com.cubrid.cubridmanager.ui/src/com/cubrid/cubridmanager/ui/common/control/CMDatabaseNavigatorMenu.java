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
package com.cubrid.cubridmanager.ui.common.control;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.query.control.DatabaseMenuItem;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.common.dialog.LoginQueryEditDialog;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridServerLoader;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * A Toolbar Control to show the query editor toolItem and database selection menu
 *
 * @author pangqiren 2009-3-2
 */
public class CMDatabaseNavigatorMenu extends DatabaseNavigatorMenu {

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
        setSelectedImage(CubridManagerUIPlugin.getImage("/icons/navigator/eclipse_dot.png"));

        CubridNavigatorView navigatorView =
                CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
        boolean isShowGroup = navigatorView == null ? false : navigatorView.isShowGroup();
        List<CubridGroupNode> groupList =
                CMGroupNodePersistManager.getInstance().getAllGroupNodes();
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
                groupMenuItem.setImage(
                        CubridManagerUIPlugin.getImage("/icons/navigator/group.png"));
                groupMenuItem.setText(groupName);
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
                if (node instanceof CubridServer) {
                    CubridServer server = (CubridServer) node;
                    ICubridNode databaseFolderNode =
                            server.getChild(
                                    server.getId()
                                            + ICubridNodeLoader.NODE_SEPARATOR
                                            + CubridServerLoader.DATABASE_FOLDER_ID);
                    if (databaseFolderNode == null) {
                        continue;
                    }
                    //					if (NodeFilterManager.getInstance().isHidden(databaseFolderNode)) {
                    //						continue;
                    //					}

                    ICubridNode[] dbs = databaseFolderNode.getChildren(new NullProgressMonitor());
                    if (dbs == null || dbs.length == 0) {
                        continue;
                    }

                    String serverName = server.getLabel();
                    DatabaseMenuItem serverMenuItem = null;
                    Menu serverSub = null;
                    if (isShowGroup) {
                        serverMenuItem =
                                new DatabaseMenuItem(server.getId(), groupSub, SWT.CASCADE);
                        serverMenuItem.setImage(
                                CubridManagerUIPlugin.getImage("/icons/navigator/host.png"));
                        serverMenuItem.setText(serverName);
                        serverSub = new Menu(groupSub);
                        serverMenuItem.setMenu(serverSub);
                    } else {
                        serverMenuItem =
                                new DatabaseMenuItem(server.getId(), dbSelectionMenu, SWT.CASCADE);
                        serverMenuItem.setImage(
                                CubridManagerUIPlugin.getImage("/icons/navigator/host.png"));
                        serverMenuItem.setText(serverName);
                        serverSub = new Menu(dbSelectionMenu);
                        serverMenuItem.setMenu(serverSub);
                    }

                    count++;

                    for (ICubridNode database : dbs) {
                        //						if (NodeFilterManager.getInstance().isHidden(database)) {
                        //							continue;
                        //						}
                        CubridDatabase db = (CubridDatabase) database;
                        if (!db.isLogined()) {
                            continue;
                        }

                        itemCountInGroup++;
                        DatabaseMenuItem dbItem =
                                new DatabaseMenuItem(database.getId(), serverSub, SWT.RADIO);
                        dbItem.setText(database.getLabel());
                        dbItem.setDatabase((CubridDatabase) database);
                        dbItem.setEnabled(
                                db.isLogined() && db.getRunningType() == DbRunningType.CS);
                        dbItem.setGroupName(groupName);

                        count++;
                    }
                }
            }

            if (itemCountInGroup <= 0 && groupSub != null && groupMenuItem != null) {
                groupSub.dispose();
                groupMenuItem.dispose();
                continue;
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
        LoginQueryEditDialog dialog = new LoginQueryEditDialog(parent.getShell());
        dialog.setSelServerName(DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL);
        if (DatabaseNavigatorMenu.SELF_DATABASE.getDatabaseInfo() != null) {
            dialog.setSelDatabaseName(
                    DatabaseNavigatorMenu.SELF_DATABASE.getDatabaseInfo().getDbName());
        }
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
        DatabaseInfo dbInfo = cubridDatabase.getDatabaseInfo();
        ServerInfo svrInfo = null;
        String text = "";
        if (dbInfo != null) {
            text = dbInfo.getDbName();
            svrInfo = dbInfo.getServerInfo();
        }

        String userName = cubridDatabase.getUserName();
        if (userName != null && userName.trim().length() > 0) {
            text = userName + "@" + text;
        }

        if (svrInfo != null) {
            String hostName = svrInfo.getServerName();
            text = text + ":" + hostName;
        }

        return text;
    }
}
