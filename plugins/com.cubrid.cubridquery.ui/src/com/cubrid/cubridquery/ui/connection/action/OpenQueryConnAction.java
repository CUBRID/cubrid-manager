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

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
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
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.common.action.ShortSetEditorConfigAction;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import com.cubrid.cubridquery.ui.connection.dialog.MultiDatabaseLoginFailedDialog;
import com.cubrid.cubridquery.ui.connection.dialog.MultiDatabaseloginFailedInfo;
import com.cubrid.cubridquery.ui.spi.contribution.CubridWorkbenchContrItem;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Open the query connection action
 *
 * @author pangqiren
 * @version 1.0 - 2011-6-16 created by pangqiren
 */
public class OpenQueryConnAction extends SelectionAction {

    public static final String ID = OpenQueryConnAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public OpenQueryConnAction(Shell shell, String text, ImageDescriptor icon) {
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
    public OpenQueryConnAction(
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
        if (obj instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) obj;
            return !database.isLogined();
        } else if (obj instanceof Object[]) {
            Object[] objArr = (Object[]) obj;
            for (Object object : objArr) {
                if (!isSupported(object)) {
                    return false;
                }
            }
            return objArr != null && objArr.length > 0;
        }
        return false;
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
                if (!((CubridDatabase) obj).isLogined()) returnArray.add((CubridDatabase) obj);
            } else if (obj instanceof CubridGroupNode) {
                CubridGroupNode node = (CubridGroupNode) obj;
                for (ICubridNode icNode : node.getChildren()) {
                    if (icNode instanceof CubridDatabase) {
                        if (!((CubridDatabase) icNode).isLogined()) {
                            returnArray.add((CubridDatabase) icNode);
                        }
                    }
                }
            }
        }

        return returnArray.toArray(new CubridDatabase[0]);
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

        if (cubridDatabases.length > 1) {
            List<MultiDatabaseloginFailedInfo> failedDatabaseList =
                    new ArrayList<MultiDatabaseloginFailedInfo>();
            for (CubridDatabase object : cubridDatabases) {
                CubridDatabase database = (CubridDatabase) object;
                String errMsg = null;
                if (database.isAutoSavePassword()) {
                    errMsg =
                            CubridWorkbenchContrItem.connectDatabaseWithErrMsg(
                                    database.getDatabaseInfo());
                } else {
                    errMsg = "Incorrect or missing password.";
                }
                if (errMsg == null) {
                    database.getLoader().setLoaded(false);
                    database.setLogined(true);
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
                    failedDatabaseList.add(new MultiDatabaseloginFailedInfo(database, errMsg));
                }
            }
            if (failedDatabaseList.size() > 0) {
                MultiDatabaseLoginFailedDialog dialog =
                        new MultiDatabaseLoginFailedDialog(getShell(), failedDatabaseList);
                dialog.open();
            }
            return;
        }

        CubridDatabase database = cubridDatabases[0];
        if (!database.isLogined() && database.isAutoSavePassword()) {
            DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, false);
            if (EditorConstance.isNeedSetBackground(editorConfig)) {
                new ShortSetEditorConfigAction(database).run();
            }
        }

        if (database.isAutoSavePassword()
                && CubridWorkbenchContrItem.connectDatabase(database.getDatabaseInfo())) {
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
}
