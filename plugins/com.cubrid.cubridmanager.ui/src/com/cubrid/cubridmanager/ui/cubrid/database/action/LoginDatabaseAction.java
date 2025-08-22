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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.ui.common.action.ShortSetEditorConfigAction;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.MultiDatabaseLoginFailedDialog;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.MultiDatabaseloginFailedInfo;
import com.cubrid.cubridmanager.ui.spi.contribution.CubridWorkbenchContrItem;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Login database action
 *
 * @author pangqiren
 * @version 1.0 - 2011-6-17 created by pangqiren
 */
public class LoginDatabaseAction extends SelectionAction {
    public static final String ID = LoginDatabaseAction.class.getName();

    public LoginDatabaseAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public LoginDatabaseAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        //		return isSupportedNode(obj);
        return true;
    }

    public static boolean isSupportedNode(Object obj) {
        if (obj instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) obj;
            return !database.isLogined();
        } else if (obj instanceof Object[]) {
            return true;
        }
        return false;
    }

    private CubridDatabase[] handleSelectionObj(Object[] objs) {
        List<CubridDatabase> returnArray = new ArrayList<CubridDatabase>();
        for (Object obj : objs) {
            if (obj instanceof CubridDatabase) {
                if (!((CubridDatabase) obj).isLogined()) returnArray.add((CubridDatabase) obj);
            }
        }

        return returnArray.toArray(new CubridDatabase[0]);
    }

    public void run() {
        Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            setEnabled(false);
            return;
        }
        CubridDatabase[] cubridDatabases = handleSelectionObj(obj);

        doRun(cubridDatabases);
    }

    public void doRun(CubridDatabase[] databaseArray) {
        if (databaseArray == null || databaseArray.length == 0) {
            return;
        }

        CubridNavigatorView navigationView = CubridNavigatorView.findNavigationView();
        if (navigationView != null && databaseArray.length > 0) {
            final TreeViewer treeViewer = navigationView.getViewer();
            if (databaseArray.length > 1) {
                List<MultiDatabaseloginFailedInfo> failedDatabaseList =
                        new ArrayList<MultiDatabaseloginFailedInfo>();
                for (CubridDatabase object : databaseArray) {
                    CubridDatabase database = (CubridDatabase) object;
                    String errMsg = null;
                    if (database.isAutoSavePassword()) {
                        errMsg =
                                CubridWorkbenchContrItem.connectDatabaseWithErrMsg(
                                        database.getDatabaseInfo(), false);
                    } else {
                        errMsg = "Incorrect or missing password.";
                    }
                    if (errMsg == null) {
                        database.getLoader().setLoaded(false);
                        database.setLogined(true);
                        treeViewer.refresh(database, true);
                        treeViewer.expandToLevel(database, 1);

                        ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
                        LayoutManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
                        CubridNodeManager.getInstance()
                                .fireCubridNodeChanged(
                                        new CubridNodeChangedEvent(
                                                database,
                                                CubridNodeChangedEventType.DATABASE_LOGIN));
                        // open database dashboard
                        if (GeneralPreference.isUseDatabaseDashboard()) {
                            LayoutManager.getInstance()
                                    .getWorkbenchContrItem()
                                    .openEditorOrView(database);
                        }
                    } else {
                        failedDatabaseList.add(new MultiDatabaseloginFailedInfo(database, errMsg));
                    }
                }
                if (failedDatabaseList.size() > 0) {
                    MultiDatabaseLoginFailedDialog dialog =
                            new MultiDatabaseLoginFailedDialog(getShell(), failedDatabaseList);
                    dialog.open();
                }
                return;
            } else {
                CubridDatabase database = databaseArray[0];
                if (DbRunningType.STANDALONE.equals(database.getRunningType())
                        && ActionSupportUtil.hasAdminPermissionOnStopState(database)) {
                    if (CommonUITool.openConfirmBox(
                            Messages.bind(Messages.msgWhetherStartDB, database.getName()))) {
                        StartDatabaseAction startDatabaseAction =
                                (StartDatabaseAction)
                                        ActionManager.getInstance()
                                                .getAction(StartDatabaseAction.ID);
                        startDatabaseAction.doRun(new CubridDatabase[] {database});
                    }
                }

                if (!database.isLogined() && database.isAutoSavePassword()) {
                    DatabaseEditorConfig editorConfig =
                            QueryOptions.getEditorConfig(database, true);
                    if (EditorConstance.isNeedSetBackground(editorConfig)) {
                        new ShortSetEditorConfigAction(database).run();
                    }
                }

                if (!database.isLogined()
                        && database.isAutoSavePassword()
                        && CubridWorkbenchContrItem.connectDatabase(
                                database.getDatabaseInfo(), true)) {
                    database.getLoader().setLoaded(false);
                    database.setLogined(true);
                    treeViewer.refresh(database, true);
                    treeViewer.expandToLevel(database, 1);

                    ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
                    LayoutManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
                    CubridNodeManager.getInstance()
                            .fireCubridNodeChanged(
                                    new CubridNodeChangedEvent(
                                            database, CubridNodeChangedEventType.DATABASE_LOGIN));
                    // open database dashboard
                    if (GeneralPreference.isUseDatabaseDashboard()) {
                        LayoutManager.getInstance()
                                .getWorkbenchContrItem()
                                .openEditorOrView(database);
                    }
                } else if (!database.isLogined()) {
                    EditDatabaseLoginAction editDatabaseLoginAction =
                            (EditDatabaseLoginAction)
                                    ActionManager.getInstance()
                                            .getAction(EditDatabaseLoginAction.ID);
                    editDatabaseLoginAction.doRun(databaseArray);
                }
            }
        }
    }

    /**
     * Open and reopen the editor part of this CUBRID node
     *
     * @param cubridNode the ICubridNode object
     */
    public void openEditorOrView(ICubridNode cubridNode) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        // close the editor part that has been open
        String editorId = cubridNode.getEditorId();
        if (editorId != null && editorId.trim().length() > 0) {
            IEditorPart editorPart = LayoutUtil.getEditorPart(cubridNode, editorId);
            if (editorPart != null) {
                window.getActivePage().closeEditor(editorPart, false);
            }
        }
        LayoutManager.getInstance().getWorkbenchContrItem().openEditorOrView(cubridNode);
    }
}
