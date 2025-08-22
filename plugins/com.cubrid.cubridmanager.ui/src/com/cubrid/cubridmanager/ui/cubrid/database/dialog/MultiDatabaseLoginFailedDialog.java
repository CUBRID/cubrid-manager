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
package com.cubrid.cubridmanager.ui.cubrid.database.dialog;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMDBNodePersistManager;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MultiDatabaseLoginFailedDialog extends CMTitleAreaDialog {

    private List<MultiDatabaseloginFailedInfo> failedDatabaseList;
    public TableViewer databaseTable = null;
    private static final int EDIT_ID = -5;

    public MultiDatabaseLoginFailedDialog(
            Shell parentShell, List<MultiDatabaseloginFailedInfo> failedDatabaseList) {
        super(parentShell);
        this.failedDatabaseList = failedDatabaseList;
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.multiDatabaseLoginDialogTitle);
        Composite parentComp = (Composite) super.createDialogArea(parent);
        setTitle(Messages.multiDatabaseLoginDialogTitle);
        setMessage(Messages.multiDatabaseLoginDialogMessages);

        databaseTable =
                new TableViewer(
                        parentComp,
                        SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        databaseTable.getTable().setLayoutData(gridData);
        databaseTable.getTable().setHeaderVisible(true);
        databaseTable.getTable().setLinesVisible(true);

        final TableViewerColumn columnHost = new TableViewerColumn(databaseTable, SWT.CENTER);
        columnHost.getColumn().setWidth(120);
        columnHost.getColumn().setText(Messages.multiDatabaseLoginDialogColumnHostAddress);

        final TableViewerColumn columnDatabase = new TableViewerColumn(databaseTable, SWT.CENTER);
        columnDatabase.getColumn().setWidth(150);
        columnDatabase.getColumn().setText(Messages.multiDatabaseLoginDialogColumnDbName);

        final TableViewerColumn columnUser = new TableViewerColumn(databaseTable, SWT.CENTER);
        columnUser.getColumn().setWidth(100);
        columnUser.getColumn().setText(Messages.multiDatabaseLoginDialogColumnUser);

        final TableViewerColumn columnErrMsg = new TableViewerColumn(databaseTable, SWT.CENTER);
        columnErrMsg.getColumn().setWidth(200);
        columnErrMsg.getColumn().setText(Messages.multiDatabaseLoginDialogColumnErrMsg);

        final TableViewerColumn columnStatus = new TableViewerColumn(databaseTable, SWT.CENTER);
        columnStatus.getColumn().setWidth(100);
        columnStatus.getColumn().setText(Messages.multiDatabaseLoginDialogColumnStatus);

        databaseTable.setContentProvider(new ServerListContentProvider());
        databaseTable.setLabelProvider(new ServerListLabelProvider());

        databaseTable.addDoubleClickListener(
                new IDoubleClickListener() {

                    public void doubleClick(DoubleClickEvent event) {
                        IStructuredSelection selection =
                                (IStructuredSelection) event.getSelection();
                        MultiDatabaseloginFailedInfo multiDatabaseloginFailedInfo =
                                (MultiDatabaseloginFailedInfo) selection.getFirstElement();
                        editHost(multiDatabaseloginFailedInfo);
                        // if all database login , close this dialog
                        if (checkAllDatabaseLogin()) {
                            close();
                        }
                    }
                });
        databaseTable.setInput(failedDatabaseList);
        MenuManager menuManager = new MenuManager();
        Menu contextMenu = menuManager.createContextMenu(databaseTable.getTable());
        databaseTable.getTable().setMenu(contextMenu);

        Menu menu = new Menu(getShell(), SWT.POP_UP);

        final MenuItem itemEdit = new MenuItem(menu, SWT.PUSH);
        itemEdit.setText(Messages.multiDatabaseLoginDialogEditLabel);
        itemEdit.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        IStructuredSelection selection =
                                (StructuredSelection) databaseTable.getSelection();
                        MultiDatabaseloginFailedInfo multiDatabaseloginFailedInfo =
                                (MultiDatabaseloginFailedInfo) selection.getFirstElement();
                        editHost(multiDatabaseloginFailedInfo);
                    }
                });

        menu.addMenuListener(
                new MenuAdapter() {
                    public void menuShown(MenuEvent event) {
                        IStructuredSelection selection =
                                (IStructuredSelection) databaseTable.getSelection();
                        MultiDatabaseloginFailedInfo multiDatabaseloginFailedInfo =
                                (MultiDatabaseloginFailedInfo) selection.getFirstElement();
                        if (multiDatabaseloginFailedInfo.getCubridDatabase().isLogined()) {
                            itemEdit.setEnabled(false);
                        } else {
                            itemEdit.setEnabled(true);
                        }
                    }
                });
        databaseTable.getTable().setMenu(menu);
        databaseTable
                .getTable()
                .addSelectionListener(
                        new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                if (databaseTable.getTable().getSelectionIndices().length > 0) {
                                    getButton(EDIT_ID).setEnabled(true);
                                } else {
                                    getButton(EDIT_ID).setEnabled(false);
                                }
                            }
                        });
        return parentComp;
    }

    public void editHost(MultiDatabaseloginFailedInfo multiDatabaseloginFailedInfo) {
        if (multiDatabaseloginFailedInfo == null) {
            return;
        }
        CubridDatabase database = multiDatabaseloginFailedInfo.getCubridDatabase();
        // if login ,can't edit
        if (database.isLogined()) {
            return;
        }
        //		CubridDatabase oldDatabase = null;
        //		try {
        //			oldDatabase = database.clone();
        //		} catch (CloneNotSupportedException e) {
        //			//Ignore
        //		}
        LoginDatabaseDialog dialog = new LoginDatabaseDialog(getShell(), database);
        int returnVal = dialog.open();
        if (returnVal == IDialogConstants.OK_ID) {
            CubridNavigatorView view =
                    CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
            TreeViewer treeViewer = view.getViewer();
            database.removeAllChild();
            if (database.getLoader() != null) {
                database.getLoader().setLoaded(false);
            }
            treeViewer.refresh(database, true);
            treeViewer.expandToLevel(database, 1);

            /*Save the data*/
            DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, true);
            if (editorConfig == null) {
                editorConfig = new DatabaseEditorConfig();
            }
            editorConfig.setBackGround(dialog.getSelectedColor());
            CMDBNodePersistManager.getInstance().addDatabase(database, editorConfig);

            ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
            LayoutManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
            CubridNodeManager.getInstance()
                    .fireCubridNodeChanged(
                            new CubridNodeChangedEvent(
                                    database, CubridNodeChangedEventType.DATABASE_LOGIN));
            multiDatabaseloginFailedInfo.setErrMsg("");
        }

        databaseTable.refresh();
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == EDIT_ID) {
            IStructuredSelection selection = (StructuredSelection) databaseTable.getSelection();
            MultiDatabaseloginFailedInfo multiDatabaseloginFailedInfo =
                    (MultiDatabaseloginFailedInfo) selection.getFirstElement();
            editHost(multiDatabaseloginFailedInfo);
            // if all database login , close this dialog
            if (!checkAllDatabaseLogin()) {
                return;
            }
        }
        setReturnCode(buttonId);
        close();
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, EDIT_ID, Messages.multiDatabaseLoginDialogEditLabel, false)
                .setEnabled(false);
        createButton(
                parent, IDialogConstants.CANCEL_ID, Messages.multiDatabaseLoginDialogClose, false);
    }

    /**
     * if all edit database connect ,close this dialog
     *
     * @return whether all database is login
     */
    public boolean checkAllDatabaseLogin() {
        for (MultiDatabaseloginFailedInfo failedInfo : failedDatabaseList) {
            if (!failedInfo.getCubridDatabase().isLogined()) {
                return false;
            }
        }
        return true;
    }

    /** @author fulei */
    class ServerListContentProvider implements IStructuredContentProvider {

        /**
         * getElements
         *
         * @param inputElement Object
         * @return Object[]
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<MultiDatabaseloginFailedInfo> list =
                        (List<MultiDatabaseloginFailedInfo>) inputElement;
                MultiDatabaseloginFailedInfo[] nodeArr =
                        new MultiDatabaseloginFailedInfo[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        /** dispose */
        public void dispose() {
            // do nothing
        }

        /**
         * inputChanged
         *
         * @param viewer Viewer
         * @param oldInput Object
         * @param newInput Object
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
        }
    }

    /** @author fulei */
    class ServerListLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * getColumnImage
         *
         * @param element Object
         * @param columnIndex int
         * @return Image
         */
        public final Image getColumnImage(Object element, int columnIndex) {

            return null;
        }
        /**
         * getColumnText
         *
         * @param element Object
         * @param columnIndex int
         * @return String
         */
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof MultiDatabaseloginFailedInfo) {
                MultiDatabaseloginFailedInfo dbFailedInfo = (MultiDatabaseloginFailedInfo) element;
                if (columnIndex == 0) {
                    return dbFailedInfo.getCubridDatabase().getServer().getHostAddress();
                } else if (columnIndex == 1) {
                    return dbFailedInfo.getCubridDatabase().getDatabaseInfo().getDbName();
                } else if (columnIndex == 2) {
                    return dbFailedInfo
                            .getCubridDatabase()
                            .getDatabaseInfo()
                            .getAuthLoginedDbUserInfo()
                            .getName();
                } else if (columnIndex == 3) {
                    return dbFailedInfo.getErrMsg();
                } else if (columnIndex == 4) {
                    return dbFailedInfo.getCubridDatabase().isLogined()
                            ? Messages.multiDatabaseLoginDialogStatusLogin
                            : Messages.multiDatabaseLoginDialogStatusNotLogin;
                }
            }
            return null;
        }
    }
}
