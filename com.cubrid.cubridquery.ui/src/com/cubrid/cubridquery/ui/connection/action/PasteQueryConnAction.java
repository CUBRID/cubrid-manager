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

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import com.cubrid.cubridquery.ui.connection.dialog.QueryConnDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Paste the query connection action
 *
 * @author pangqiren
 * @version 1.0 - 2011-9-22 created by pangqiren
 */
public class PasteQueryConnAction extends SelectionAction {

    public static final String ID = PasteQueryConnAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public PasteQueryConnAction(Shell shell, String text, ImageDescriptor icon) {
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
    public PasteQueryConnAction(
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
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {

        if (ApplicationType.CUBRID_QUERY_BROWSER.equals(
                PerspectiveManager.getInstance().getCurrentMode())) {
            CubridNavigatorView navigatorView =
                    CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);

            if (navigatorView == null) {
                return false;
            }
            boolean isShowGroup = navigatorView.isShowGroup();
            if (isShowGroup
                    && !(obj instanceof CubridDatabase)
                    && !(obj instanceof CubridGroupNode)) {
                return false;
            }

            if (!isShowGroup && obj != null && !(obj instanceof CubridDatabase)) {
                return false;
            }

            Object[] objs = null;
            ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
            if (selection instanceof IStructuredSelection) {
                IStructuredSelection strSelection = (IStructuredSelection) selection;
                objs = strSelection.toArray();
            }
            if (objs == null || objs.length == 0) {
                return false;
            }
            if (!(objs[0] instanceof CubridDatabase)) {
                return false;
            }

            return true;
        }

        return false;
    }

    /** Paste the query connection */
    public void run() {
        Object[] objs = this.getSelectedObj();
        if (!isSupported(objs[0])) {
            setEnabled(false);
            return;
        }

        objs = null;
        ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection strSelection = (IStructuredSelection) selection;
            objs = strSelection.toArray();
        }
        if (objs == null || objs.length == 0) {
            return;
        }
        if (!(objs[0] instanceof CubridDatabase)) {
            return;
        }
        CubridDatabase pastedDb = (CubridDatabase) objs[0];

        QueryConnDialog dialog = new QueryConnDialog(getShell(), pastedDb, true);
        int returnCode = dialog.open();
        if (returnCode == QueryConnDialog.CONNECT_ID || returnCode == QueryConnDialog.SAVE_ID) {
            CubridDatabase database = dialog.getDatabase();

            CQBDBNodePersistManager.getInstance().addDatabase(database, false);

            CubridNavigatorView navigatorView =
                    CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
            TreeViewer treeViewer = navigatorView == null ? null : navigatorView.getViewer();
            if (treeViewer == null) {
                return;
            }
            Tree tree = treeViewer.getTree();

            TreeItem item;
            CubridGroupNode parent = getParentGroupNode();
            if (navigatorView.isShowGroup()) {
                item = new TreeItem(navigatorView.getTreeItemByData(parent), SWT.NONE);
            } else {
                item = new TreeItem(tree, SWT.NONE);
            }
            parent.addChild(database);
            CQBGroupNodePersistManager.getInstance().saveAllGroupNode();
            item.setText(database.getLabel());
            item.setData(database);
            treeViewer.refresh(database, true);
            treeViewer.expandToLevel(database, 1);
            treeViewer.setSelection(new StructuredSelection(database), true);

            if (returnCode == QueryConnDialog.CONNECT_ID) {
                CubridNodeManager.getInstance()
                        .fireCubridNodeChanged(
                                new CubridNodeChangedEvent(
                                        database, CubridNodeChangedEventType.DATABASE_LOGIN));
            }
        }
    }

    /**
     * Get the parent group node if it is show group mode.
     *
     * @return the parent group node.
     */
    private CubridGroupNode getParentGroupNode() {
        Object[] selections = this.getSelectedObj();
        if (selections.length == 0) {
            return CQBGroupNodePersistManager.getInstance().getDefaultGroup();
        } else if (selections[0] instanceof CubridGroupNode) {
            return (CubridGroupNode) selections[0];
        } else if (selections[0] instanceof CubridDatabase) {
            CubridGroupNode groupNode =
                    (CubridGroupNode) ((CubridDatabase) selections[0]).getParent();
            return groupNode == null
                    ? CQBGroupNodePersistManager.getInstance().getDefaultGroup()
                    : groupNode;
        }
        return CQBGroupNodePersistManager.getInstance().getDefaultGroup();
    }
}
