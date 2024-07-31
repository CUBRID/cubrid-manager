/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridquery.ui.connection.dialog;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CQBGroupNodePersistManager;
import com.cubrid.cubridquery.ui.common.navigator.CubridQueryNavigatorView;
import com.cubrid.cubridquery.ui.connection.Messages;
import java.util.List;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * The create connection by URL wizard
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 20, 2012 created by Kevin.Wang
 */
public class CreateConnectionByUrlWizard extends Wizard {

    private InputUrlPage inputUrlPage;
    private ConnectionPriviewPage connectionPriviewPage;

    /** The constructor */
    public CreateConnectionByUrlWizard() {
        setWindowTitle(Messages.titleCreateByURLDialog);
    }

    /** Add wizard page */
    public void addPages() {
        inputUrlPage = new InputUrlPage();
        addPage(inputUrlPage);
        connectionPriviewPage = new ConnectionPriviewPage(inputUrlPage);
        addPage(connectionPriviewPage);

        WizardDialog dialog = (WizardDialog) getContainer();
        dialog.addPageChangedListener(connectionPriviewPage);
    }

    /**
     * Return whether can finish
     *
     * @return <code>true</code>if can finish;<code>false</code> otherwise
     */
    public boolean canFinish() {
        return getContainer().getCurrentPage() == connectionPriviewPage;
    }

    /** Perform finish */
    public boolean performFinish() {
        List<CubridDatabase> parsedDatabaseList = connectionPriviewPage.getParsedConnection();

        /*Add the database list to the tree*/
        CubridNavigatorView navigatorView =
                CubridNavigatorView.getNavigatorView(CubridQueryNavigatorView.ID);
        TreeViewer treeViewer = navigatorView == null ? null : navigatorView.getViewer();
        if (treeViewer == null) {
            return false;
        }

        for (CubridDatabase database : parsedDatabaseList) {
            CQBDBNodePersistManager.getInstance().addDatabase(database, false);

            Tree tree = treeViewer.getTree();
            TreeItem item;
            CubridGroupNode parent = CQBGroupNodePersistManager.getInstance().getDefaultGroup();

            if (navigatorView.isShowGroup()) {
                item = new TreeItem(navigatorView.getTreeItemByData(parent), SWT.NONE);
            } else {
                item = new TreeItem(tree, SWT.NONE);
            }

            parent.addChild(database);
            item.setText(database.getLabel());
            item.setData(database);
            treeViewer.refresh(database, true);
            treeViewer.expandToLevel(database, 1);
            treeViewer.setSelection(new StructuredSelection(database), true);
        }
        CQBGroupNodePersistManager.getInstance().saveAllGroupNode();

        return true;
    }
}
