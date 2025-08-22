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
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This action is responsible to add query database to navigator
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class NewQueryConnAction extends SelectionAction {

    public static final String ID = NewQueryConnAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public NewQueryConnAction(Shell shell, String text, ImageDescriptor icon) {
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
    public NewQueryConnAction(
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

    /** Open the QueryConnDialog */
    public void run() {
        QueryConnDialog dialog = new QueryConnDialog(getShell(), null, true);
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
        if ((selections.length > 0) && (selections[0] instanceof CubridGroupNode)) {
            return (CubridGroupNode) selections[0];
        }
        return CQBGroupNodePersistManager.getInstance().getDefaultGroup();
    }
}
