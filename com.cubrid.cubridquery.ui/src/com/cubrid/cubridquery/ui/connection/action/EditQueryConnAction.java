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
package com.cubrid.cubridquery.ui.connection.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.persist.ConnectionInfo;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.CQBConnectionUtils;
import com.cubrid.cubridquery.ui.connection.dialog.MultiQueryConnEditDialog;
import com.cubrid.cubridquery.ui.connection.dialog.QueryConnDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * This action is responsible to edit query database
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-8 created by pangqiren
 */
public class EditQueryConnAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(EditQueryConnAction.class);
    public static final String ID = EditQueryConnAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public EditQueryConnAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public EditQueryConnAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Return whether this action support to select multi object,if not support,this action will be
     * disabled
     *
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        return true;
    }

    /** Open the query connection */
    public void run() {
        CQBGroupNodePersistManager.getInstance().fix(); // TODO remove later

        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }
        ISelectionProvider provider = getSelectionProvider();
        if (!(provider instanceof TreeViewer)) {
            return;
        }

        CubridDatabase[] cubridDatabases = handleSelectionObj(obj);
        if (cubridDatabases.length == 0) {
            return;
        }

        ConnectionInfo oldInfo = null;
        ConnectionInfo newInfo = null;
        if (cubridDatabases.length > 1) {
            MultiQueryConnEditDialog dialog =
                    new MultiQueryConnEditDialog(getShell(), Arrays.asList(cubridDatabases));
            if (dialog.open() != MultiQueryConnEditDialog.SAVE_ID) {
                return;
            }
            List<CubridDatabase> newDBList = dialog.getNewDBList();
            for (int i = 0; i < cubridDatabases.length; i++) {
                CubridDatabase saveCubridDatabase = cubridDatabases[i];
                CubridDatabase newCubridDatabase = newDBList.get(i);

                oldInfo =
                        CQBDBNodePersistManager.getInstance().getConnectionInfo(saveCubridDatabase);
                newInfo =
                        CQBDBNodePersistManager.getInstance().getConnectionInfo(newCubridDatabase);

                saveCubridDatabase.setLabel(newCubridDatabase.getLabel());
                saveCubridDatabase
                        .getDatabaseInfo()
                        .setDbName(newCubridDatabase.getDatabaseInfo().getDbName());
                saveCubridDatabase
                        .getDatabaseInfo()
                        .getAuthLoginedDbUserInfo()
                        .setName(
                                newCubridDatabase
                                        .getDatabaseInfo()
                                        .getAuthLoginedDbUserInfo()
                                        .getName());

                saveCubridDatabase.setAutoSavePassword(newCubridDatabase.isAutoSavePassword());
                if (saveCubridDatabase.isAutoSavePassword()) {
                    saveCubridDatabase
                            .getDatabaseInfo()
                            .getAuthLoginedDbUserInfo()
                            .setNoEncryptPassword(
                                    newCubridDatabase
                                            .getDatabaseInfo()
                                            .getAuthLoginedDbUserInfo()
                                            .getNoEncryptPassword());
                } else {
                    saveCubridDatabase
                            .getDatabaseInfo()
                            .getAuthLoginedDbUserInfo()
                            .setNoEncryptPassword(null);
                }
                saveCubridDatabase
                        .getDatabaseInfo()
                        .setBrokerIP(newCubridDatabase.getDatabaseInfo().getBrokerIP());
                saveCubridDatabase
                        .getDatabaseInfo()
                        .setBrokerPort(newCubridDatabase.getDatabaseInfo().getBrokerPort());

                DatabaseEditorConfig editorConfig =
                        QueryOptions.getEditorConfig(saveCubridDatabase, false);
                if (editorConfig == null) {
                    editorConfig = new DatabaseEditorConfig();
                    editorConfig.setBackGround(EditorConstance.getDefaultBackground());
                } else if (editorConfig.getBackGround() == null) {
                    editorConfig.setBackGround(EditorConstance.getDefaultBackground());
                }
                if (newCubridDatabase.getData(MultiQueryConnEditDialog.COMMENTKEY) != null) {
                    editorConfig.setDatabaseComment(
                            (String)
                                    newCubridDatabase.getData(MultiQueryConnEditDialog.COMMENTKEY));
                }
                QueryOptions.putEditorConfig(saveCubridDatabase, editorConfig, false);

                CQBDBNodePersistManager.getInstance().fireModifyDatabase(oldInfo, newInfo);
                boolean isContinue = CQBConnectionUtils.processConnectionLogout(saveCubridDatabase);
                if (isContinue) {
                    TreeViewer viewer = (TreeViewer) provider;
                    viewer.refresh(saveCubridDatabase, true);
                    CubridNodeManager.getInstance()
                            .fireCubridNodeChanged(
                                    new CubridNodeChangedEvent(
                                            saveCubridDatabase,
                                            CubridNodeChangedEventType.DATABASE_LOGOUT));
                }
            }
            LayoutManager.getInstance().fireSelectionChanged(getSelection());
            ActionManager.getInstance().fireSelectionChanged(getSelection());

            CQBGroupNodePersistManager.getInstance().saveAllGroupNode();
            CQBDBNodePersistManager.getInstance().saveDatabases();
            return;
        }

        CubridDatabase database = (CubridDatabase) cubridDatabases[0];
        if (database.getParent() == null) {
            CQBGroupNodePersistManager.getInstance();
        }

        String preName =
                (database == null || database.getServer() == null)
                        ? ""
                        : database.getServer().getName();
        ServerInfo preServerInfo =
                (database == null || database.getServer() == null)
                        ? null
                        : database.getServer().getServerInfo();
        oldInfo = CQBDBNodePersistManager.getInstance().getConnectionInfo(database);

        QueryConnDialog dialog = new QueryConnDialog(getShell(), database, false);
        int returnCode = dialog.open();
        if (returnCode == QueryConnDialog.CONNECT_ID) {
            database.removeAllChild();
            newInfo = CQBDBNodePersistManager.getInstance().getConnectionInfo(database);
            TreeViewer treeViewer = (TreeViewer) provider;

            CQBGroupNodePersistManager.getInstance().saveAllGroupNode();
            CQBDBNodePersistManager.getInstance().saveDatabases();

            if (!preName.equals(database.getServer().getName())) {
                QueryOptions.removePref(preServerInfo);
            }

            treeViewer.refresh(database, true);
            treeViewer.expandToLevel(database, 1);

            ActionManager.getInstance().fireSelectionChanged(getSelection());
            LayoutManager.getInstance().fireSelectionChanged(getSelection());
            if (dialog.isFireLogoutEvent()) {
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.DATABASE_LOGOUT));
            }
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    database, CubridNodeChangedEventType.DATABASE_LOGIN));
            CQBDBNodePersistManager.getInstance().fireModifyDatabase(oldInfo, newInfo);

            try {
                CommonUITool.openQueryEditor(database, true);
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else if (returnCode == QueryConnDialog.SAVE_ID) {
            TreeViewer treeViewer = (TreeViewer) provider;
            treeViewer.refresh(database, true);
            treeViewer.expandToLevel(database, 1);

            CQBGroupNodePersistManager.getInstance().saveAllGroupNode();
            CQBDBNodePersistManager.getInstance().saveDatabases();
        }
    }

    /**
     * get CubridDatabase node
     *
     * @param objs
     * @return
     */
    private CubridDatabase[] handleSelectionObj(Object[] objs) {
        List<CubridDatabase> returnArray = new ArrayList<CubridDatabase>();
        for (Object obj : objs) {
            if (obj instanceof CubridDatabase) {
                returnArray.add((CubridDatabase) obj);
            } else if (obj instanceof CubridGroupNode) {
                CubridGroupNode node = (CubridGroupNode) obj;
                for (ICubridNode icNode : node.getChildren()) {
                    if (icNode instanceof CubridDatabase) {
                        returnArray.add((CubridDatabase) icNode);
                    }
                }
            }
        }

        return returnArray.toArray(new CubridDatabase[0]);
    }
}
