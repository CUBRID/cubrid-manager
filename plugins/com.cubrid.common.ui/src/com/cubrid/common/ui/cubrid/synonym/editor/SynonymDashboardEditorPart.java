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
package com.cubrid.common.ui.cubrid.synonym.editor;

import com.cubrid.common.core.common.model.Synonym;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.synonym.Messages;
import com.cubrid.common.ui.cubrid.synonym.action.AlterSynonymAction;
import com.cubrid.common.ui.cubrid.synonym.action.DropSynonymAction;
import com.cubrid.common.ui.cubrid.synonym.action.NewSynonymAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.OpenSynonymDetailInfoPartProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class SynonymDashboardEditorPart extends CubridEditorPart {

    public static final String ID = SynonymDashboardEditorPart.class.getName();
    private boolean synonymChangeFlag;
    private CubridDatabase database;
    private List<Synonym> synonymList = null;
    private TableViewer synonymsDetailInfoTable;

    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(parent, SWT.LEFT_TO_RIGHT | SWT.FLAT);
        toolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
        refreshItem.setText(
                com.cubrid.common.ui.cubrid.table.Messages.tablesDetailInfoPartRefreshBtn);
        refreshItem.setToolTipText(
                com.cubrid.common.ui.cubrid.table.Messages.tablesDetailInfoPartRefreshBtn);
        refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
        refreshItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        refresh();
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);
        ToolItem addItem = new ToolItem(toolBar, SWT.NONE);
        addItem.setText(Messages.synonymsDetailInfoPartCreateSynonymBtn);
        addItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_add.png"));
        addItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addSynonym();
                    }
                });

        ToolItem editlItem = new ToolItem(toolBar, SWT.NONE);
        editlItem.setText(Messages.synonymsDetailInfoPartEditSynonymBtn);
        editlItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_edit.png"));
        editlItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        editSynonym();
                    }
                });

        ToolItem dropItem = new ToolItem(toolBar, SWT.NONE);
        dropItem.setText(Messages.synonymsDetailInfoPartDropSynonymBtn);
        dropItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_delete.png"));
        dropItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        dropSynonym();
                    }
                });
        ;

        createSynonymsDetailInfoTable(parent);
        this.setInputs();
    }

    /**
     * create table
     *
     * @param parent
     */
    public void createSynonymsDetailInfoTable(Composite parent) {
        final Composite tableComposite = new Composite(parent, SWT.NONE);
        tableComposite.setLayout(new FillLayout());
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        synonymsDetailInfoTable =
                new TableViewer(
                        tableComposite,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        synonymsDetailInfoTable.getTable().setHeaderVisible(true);
        synonymsDetailInfoTable.getTable().setLinesVisible(true);

        final TableViewerColumn ownerColumn =
                new TableViewerColumn(synonymsDetailInfoTable, SWT.LEFT);
        ownerColumn.getColumn().setWidth(150);
        ownerColumn.getColumn().setText(Messages.synonymsDetailInfoPartTableOwnerCol);

        final TableViewerColumn nameColumn =
                new TableViewerColumn(synonymsDetailInfoTable, SWT.LEFT);
        nameColumn.getColumn().setWidth(200);
        nameColumn.getColumn().setText(Messages.synonymsDetailInfoPartTableNameCol);

        final TableViewerColumn targetOwnerColumn =
                new TableViewerColumn(synonymsDetailInfoTable, SWT.LEFT);
        targetOwnerColumn.getColumn().setWidth(150);
        targetOwnerColumn.getColumn().setText(Messages.synonymsDetailInfoPartTableTargetOwnerCol);

        final TableViewerColumn targetNameColumn =
                new TableViewerColumn(synonymsDetailInfoTable, SWT.LEFT);
        targetNameColumn.getColumn().setWidth(200);
        targetNameColumn.getColumn().setText(Messages.synonymsDetailInfoPartTableTargetNameCol);

        final TableViewerColumn CommentColumn =
                new TableViewerColumn(synonymsDetailInfoTable, SWT.LEFT);
        CommentColumn.getColumn().setWidth(250);
        CommentColumn.getColumn().setText(Messages.synonymsDetailInfoPartTableCommentCol);

        synonymsDetailInfoTable.setComparator(new ColumnViewerSorter());

        synonymsDetailInfoTable.setContentProvider(new SynonymsDetailTableViewerContentProvider());
        synonymsDetailInfoTable.setLabelProvider(new SynonymsTableViewerLabelProvider());

        registerContextMenu();
    }

    /** register context menu */
    private void registerContextMenu() {
        synonymsDetailInfoTable
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(synonymsDetailInfoTable.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(synonymsDetailInfoTable.getTable());
        synonymsDetailInfoTable.getTable().setMenu(contextMenu);

        Menu menu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);

        final MenuItem addSerialItem = new MenuItem(menu, SWT.PUSH);
        addSerialItem.setText(Messages.synonymsDetailInfoPartCreateSynonymBtn);
        addSerialItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_add.png"));
        addSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addSynonym();
                    }
                });

        final MenuItem editSerialItem = new MenuItem(menu, SWT.PUSH);
        editSerialItem.setText(Messages.synonymsDetailInfoPartEditSynonymBtn);
        editSerialItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_edit.png"));
        editSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        editSynonym();
                    }
                });

        final MenuItem dropSerialItem = new MenuItem(menu, SWT.PUSH);
        dropSerialItem.setText(Messages.synonymsDetailInfoPartDropSynonymBtn);
        dropSerialItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_delete.png"));
        dropSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        dropSynonym();
                    }
                });

        synonymsDetailInfoTable.getTable().setMenu(menu);
    }

    /** addTrigger */
    public void addSynonym() {
        NewSynonymAction action =
                (NewSynonymAction) ActionManager.getInstance().getAction(NewSynonymAction.ID);
        action.run(database);
        refresh();
    }

    /** editTrigger */
    public void editSynonym() {
        TableItem[] items = synonymsDetailInfoTable.getTable().getSelection();
        if (items.length != 0) {
            TableItem item = items[0];
            Synonym synonym = (Synonym) item.getData();
            Set<String> typeSet = new HashSet<String>();
            typeSet.add(NodeType.SYNONYM);

            ICubridNode Node = CommonUITool.findNode(database, typeSet, synonym.getUniqueName());
            if (Node != null) {
                AlterSynonymAction action =
                        (AlterSynonymAction)
                                ActionManager.getInstance().getAction(AlterSynonymAction.ID);
                if (action.run(database, (ISchemaNode) Node) == IDialogConstants.OK_ID) {
                    refresh();
                }
            }
        } else {
            CommonUITool.openWarningBox(Messages.errSynonymNoSelection);
        }
    }

    /** dropTrigger */
    public void dropSynonym() {
        TableItem[] items = synonymsDetailInfoTable.getTable().getSelection();
        if (items.length > 0) {
            List<ISchemaNode> selectNodeList = new ArrayList<ISchemaNode>();
            for (TableItem item : items) {
                Synonym synonym = (Synonym) item.getData();
                Set<String> typeSet = new HashSet<String>();
                typeSet.add(NodeType.SYNONYM);

                ICubridNode Node =
                        CommonUITool.findNode(database, typeSet, synonym.getUniqueName());
                selectNodeList.add((ISchemaNode) Node);
            }

            if (selectNodeList.size() > 0) {
                DropSynonymAction action =
                        (DropSynonymAction)
                                ActionManager.getInstance().getAction(DropSynonymAction.ID);

                ISchemaNode[] nodeArr = new ISchemaNode[selectNodeList.size()];
                action.run(selectNodeList.toArray(nodeArr));
                refresh();
            }
        } else {
            CommonUITool.openWarningBox(Messages.errSynonymNoSelection);
        }
    }

    /** refresh data */
    public void refresh() {
        OpenSynonymDetailInfoPartProgress progress =
                new OpenSynonymDetailInfoPartProgress(database);
        progress.loadSynonymInfoList();
        if (progress.isSuccess()) {
            synonymList = progress.getSynonymList();
            setInputs();
        }
        synonymChangeFlag = false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @SuppressWarnings("unchecked")
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        this.database = (CubridDatabase) input.getAdapter(CubridDatabase.class);
        this.synonymList = (List<Synonym>) input.getAdapter(List.class);

        StringBuilder partName = new StringBuilder(Messages.synonymsDetailInfoPartTitle);
        partName.append(" [")
                .append(database.getUserName())
                .append("@")
                .append(database.getName())
                .append(":")
                .append(database.getDatabaseInfo().getBrokerIP())
                .append("]");
        setPartName(partName.toString());
    }

    public void setInputs() {
        synonymsDetailInfoTable.setInput(synonymList);
        synonymsDetailInfoTable.refresh();
        pack();
    }

    public void pack() {
        for (int i = 0; i < synonymsDetailInfoTable.getTable().getColumnCount(); i++) {
            TableColumn column = synonymsDetailInfoTable.getTable().getColumn(i);
            if (column.getWidth() > 600) {
                column.setWidth(600);
            }
            if (column.getWidth() < 100) {
                column.setWidth(100);
            }
        }
    }

    public void nodeChanged(CubridNodeChangedEvent event) {
        if (event.getSource() instanceof DefaultSchemaNode) {
            DefaultSchemaNode node = (DefaultSchemaNode) event.getSource();
            if ((node.getType().equals(NodeType.SYNONYM_FOLDER)
                    || node.getType().equals(NodeType.SYNONYM)
                            && node.getDatabase().equals(database))) {
                synonymChangeFlag = true;
            }
        }
        if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(event.getType())) {
            close(event, database.getServer());
        }

        if (CubridNodeChangedEventType.DATABASE_LOGOUT.equals(event.getType())
                || CubridNodeChangedEventType.DATABASE_STOP.equals(event.getType())) {
            close(event, database);
        }
    }

    public void setFocus() {
        // if view info chaned, ask whether refresh
        if (synonymChangeFlag) {
            if (CommonUITool.openConfirmBox(
                    com.cubrid.common.ui.common.Messages.dashboardConfirmRefreshDataMsg)) {
                refresh();
            }
            synonymChangeFlag = false;
        }
    }

    /**
     * synonym table label provider
     *
     * @author Administrator
     */
    public class SynonymsTableViewerLabelProvider extends LabelProvider
            implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof Synonym) {
                Synonym synonym = (Synonym) element;
                if (synonym != null) {
                    switch (columnIndex) {
                        case 0:
                            return synonym.getOwner();
                        case 1:
                            return synonym.getName();
                        case 2:
                            return synonym.getTargetOwner();
                        case 3:
                            return synonym.getTargetName();
                        case 4:
                            return synonym.getComment();
                    }
                }
            }
            return null;
        }
    }

    /**
     * trigger table content provider
     *
     * @author fulei
     */
    public class SynonymsDetailTableViewerContentProvider implements IStructuredContentProvider {
        /**
         * getElements
         *
         * @param inputElement Object
         * @return Object[]
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<Synonym> list = (List<Synonym>) inputElement;
                Synonym[] nodeArr = new Synonym[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        /** dispose */
        public void dispose() {}

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

    /**
     * Column Viewer Sorter
     *
     * @author fulei
     * @version 1.0 - 2013-1-9 fulei
     */
    class ColumnViewerSorter extends ViewerSorter {

        public int compare(Viewer viewer, Object e1, Object e2) {
            Synonym t1 = (Synonym) e1;
            Synonym t2 = (Synonym) e2;
            return t1.getOwner().compareTo(t2.getOwner());
        }
    }

    public void doSave(IProgressMonitor monitor) {}

    public void doSaveAs() {}

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public CubridDatabase getDatabase() {
        return database;
    }
}
