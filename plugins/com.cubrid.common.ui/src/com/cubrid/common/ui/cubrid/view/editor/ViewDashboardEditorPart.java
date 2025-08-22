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
package com.cubrid.common.ui.cubrid.view.editor;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.ViewDetailInfo;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.action.CreateViewAction;
import com.cubrid.common.ui.cubrid.table.action.DropViewAction;
import com.cubrid.common.ui.cubrid.table.action.EditViewAction;
import com.cubrid.common.ui.cubrid.view.editor.ViewDashboardComposite.ViewsDetailInfoCTabItem;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.OpenViewsDetailInfoPartProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

/**
 * all view information editor part
 *
 * @author fulei
 * @version 1.0 - 2013-1-6 created by fulei
 */
public class ViewDashboardEditorPart extends CubridEditorPart {
    public static final String ID = ViewDashboardEditorPart.class.getName();
    private TableViewer viewsDetailInfoTable;
    private CubridDatabase database;
    private CTabFolder tabFolder;
    private List<ViewDetailInfo> viewList;
    private boolean viewChangeFlag;

    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(parent, SWT.LEFT_TO_RIGHT | SWT.FLAT);
        toolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
        refreshItem.setText(Messages.tablesDetailInfoPartRefreshBtn);
        refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
        refreshItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        refresh();
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);
        ToolItem addViewItem = new ToolItem(toolBar, SWT.NONE);
        addViewItem.setText(Messages.viewDetailInfoPartTableCreateViewBtn);
        addViewItem.setImage(CommonUIPlugin.getImage("icons/action/schema_view_add.png"));
        addViewItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addView();
                    }
                });

        ToolItem editViewItem = new ToolItem(toolBar, SWT.NONE);
        editViewItem.setText(Messages.viewDetailInfoPartTableEditViewBtn);
        editViewItem.setImage(CommonUIPlugin.getImage("icons/action/schema_view_edit.png"));
        editViewItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        editView();
                    }
                });

        ToolItem dropViewItem = new ToolItem(toolBar, SWT.NONE);
        dropViewItem.setText(Messages.viewDetailInfoPartTableDropViewBtn);
        dropViewItem.setImage(CommonUIPlugin.getImage("icons/action/schema_view_delete.png"));
        dropViewItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        dropView();
                    }
                });

        createViewsDetailInfoTable(parent);
        createTabFolder(parent);
        this.setInputs();
    }

    public void refresh() {
        OpenViewsDetailInfoPartProgress progress = new OpenViewsDetailInfoPartProgress(database);
        progress.loadViewsInfo();
        if (progress.isSuccess()) {
            viewList = progress.getViewList();
            viewsDetailInfoTable.setInput(viewList);
            viewsDetailInfoTable.refresh();
            List<CTabItem> closeTabItem = new ArrayList<CTabItem>();
            for (CTabItem cTabItem : tabFolder.getItems()) {
                ViewsDetailInfoCTabItem viewsDetailInfoCTabItem =
                        (ViewsDetailInfoCTabItem) cTabItem;
                // refresh column data
                if (findItemName(viewsDetailInfoCTabItem.getText())) {
                    SchemaInfo schemaInfo =
                            database.getDatabaseInfo()
                                    .getSchemaInfo(viewsDetailInfoCTabItem.getText());
                    viewsDetailInfoCTabItem.getViewInfoComposite().setInput(schemaInfo);
                } else { // tag non-exist view tab
                    closeTabItem.add(cTabItem);
                }
            }

            // dispose non-exist view tab
            for (CTabItem cTabItem : closeTabItem) {
                cTabItem.dispose();
            }
            // if the select item is disposed ,set the first on selection
            if (tabFolder.getItems().length > 0 && tabFolder.getSelection().isDisposed()) {
                tabFolder.setSelection(0);
            }
            viewChangeFlag = false;
        }
    }

    public void createViewsDetailInfoTable(Composite parent) {
        final Composite tableComposite = new Composite(parent, SWT.NONE);
        {
            tableComposite.setLayout(new FillLayout());
            tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        }

        viewsDetailInfoTable =
                new TableViewer(
                        tableComposite,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        viewsDetailInfoTable.getTable().setHeaderVisible(true);
        viewsDetailInfoTable.getTable().setLinesVisible(true);

        final TableViewerColumn ownerColumn = new TableViewerColumn(viewsDetailInfoTable, SWT.LEFT);
        ownerColumn.getColumn().setWidth(80);
        ownerColumn.getColumn().setText(Messages.viewDetailInfoPartTableOwnerColumn);

        final TableViewerColumn columnViewName =
                new TableViewerColumn(viewsDetailInfoTable, SWT.LEFT);
        columnViewName.getColumn().setWidth(150);
        columnViewName.getColumn().setText(Messages.viewDetailInfoPartColViewName);

        final TableViewerColumn scriptDescColumn =
                new TableViewerColumn(viewsDetailInfoTable, SWT.LEFT);
        scriptDescColumn.getColumn().setWidth(200);
        scriptDescColumn.getColumn().setText(Messages.viewDetailInfoPartTableDefColumn);

        viewsDetailInfoTable.setContentProvider(new ViewsDetailTableViewerContentProvider());
        viewsDetailInfoTable.setLabelProvider(new ViewsDetailTableViewerLabelProvider());

        viewsDetailInfoTable.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        IStructuredSelection selection =
                                (IStructuredSelection) event.getSelection();
                        ViewDetailInfo oneViewDetail = (ViewDetailInfo) selection.getFirstElement();
                        // if had opend,set it selection
                        for (CTabItem tabItem : tabFolder.getItems()) {
                            if (tabItem.getText()
                                    .equals(
                                            oneViewDetail.getViewUniqueName(
                                                    database.getDatabaseInfo()
                                                            .isSupportUserSchema()))) {
                                tabFolder.setSelection(tabItem);
                                return;
                            }
                        }
                        // if a new view info,create a new tab
                        ViewDashboardComposite viewComp =
                                new ViewDashboardComposite(tabFolder, SWT.NONE);
                        viewComp.initialize();

                        SchemaInfo schemaInfo =
                                database.getDatabaseInfo()
                                        .getSchemaInfo(
                                                oneViewDetail.getViewUniqueName(
                                                        database.getDatabaseInfo()
                                                                .isSupportUserSchema()));
                        viewComp.setInput(schemaInfo);
                    }
                });

        viewsDetailInfoTable
                .getTable()
                .addKeyListener(
                        new KeyAdapter() {
                            public void keyPressed(KeyEvent event) {
                                if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'c') {}
                            }
                        });
        registerContextMenu();
    }

    public void createTabFolder(Composite parent) {
        tabFolder = new CTabFolder(parent, SWT.TOP);
        tabFolder.setLayout(new FillLayout());
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tabFolder.setSimple(false);
        tabFolder.setUnselectedImageVisible(true);
        tabFolder.setUnselectedCloseVisible(true);
        tabFolder.setBorderVisible(true);
        tabFolder.setSelectionBackground(ResourceManager.getColor(136, 161, 227));
        tabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));

        Menu menu = new Menu(tabFolder.getShell(), SWT.POP_UP);
        tabFolder.setMenu(menu);

        MenuItem closeItem = new MenuItem(menu, SWT.PUSH);
        closeItem.setText(Messages.tablesDetailInfoPartCloseMenu);
        closeItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        CTabItem item = tabFolder.getSelection();
                        item.dispose();
                    }
                });

        MenuItem closeOthersItem = new MenuItem(menu, SWT.PUSH);
        closeOthersItem.setText(Messages.tablesDetailInfoPartCloseOthersMenu);
        closeOthersItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        CTabItem[] items = tabFolder.getItems();
                        CTabItem selectedItem = tabFolder.getSelection();
                        for (CTabItem item : items) {
                            if (!item.equals(selectedItem)) {
                                item.dispose();
                            }
                        }
                    }
                });

        MenuItem closeAllItem = new MenuItem(menu, SWT.PUSH);
        closeAllItem.setText(Messages.tablesDetailInfoPartCloseAllMenu);
        closeAllItem.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        CTabItem[] items = tabFolder.getItems();
                        for (CTabItem item : items) {
                            item.dispose();
                        }
                    }
                });
    }

    private void registerContextMenu() {
        viewsDetailInfoTable
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(viewsDetailInfoTable.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(viewsDetailInfoTable.getTable());
        viewsDetailInfoTable.getTable().setMenu(contextMenu);

        Menu menu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);

        final MenuItem editViewItem = new MenuItem(menu, SWT.PUSH);
        editViewItem.setText(Messages.viewDetailInfoPartTableEditViewBtn);
        editViewItem.setImage(CommonUIPlugin.getImage("icons/action/schema_view_edit.png"));
        editViewItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        editView();
                    }
                });

        final MenuItem dropViewItem = new MenuItem(menu, SWT.PUSH);
        dropViewItem.setText(Messages.viewDetailInfoPartTableDropViewBtn);
        dropViewItem.setImage(CommonUIPlugin.getImage("icons/action/schema_view_delete.png"));
        dropViewItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        dropView();
                    }
                });

        new MenuItem(menu, SWT.SEPARATOR);

        final MenuItem addViewItem = new MenuItem(menu, SWT.PUSH);
        addViewItem.setText(Messages.viewDetailInfoPartTableCreateViewBtn);
        addViewItem.setImage(CommonUIPlugin.getImage("icons/action/schema_view_add.png"));
        addViewItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addView();
                    }
                });

        viewsDetailInfoTable.getTable().setMenu(menu);
    }

    public void setInputs() {
        viewsDetailInfoTable.setInput(viewList);
        viewsDetailInfoTable.refresh();
        pack();
        // if a new view info,create a new tab
        ViewDashboardComposite viewComp = new ViewDashboardComposite(tabFolder, SWT.NONE);
        viewComp.initialize();
        if (viewList.size() > 0) {
            ViewDetailInfo oneViewDetail = viewList.get(0);
            SchemaInfo schemaInfo =
                    database.getDatabaseInfo()
                            .getSchemaInfo(
                                    oneViewDetail.getViewUniqueName(
                                            database.getDatabaseInfo().isSupportUserSchema()));
            viewComp.setInput(schemaInfo);
        }
    }

    public void pack() {
        for (int i = 0; i < viewsDetailInfoTable.getTable().getColumnCount(); i++) {
            TableColumn column = viewsDetailInfoTable.getTable().getColumn(i);
            column.pack();
            if (column.getWidth() > 600) {
                column.setWidth(600);
            }
            if (column.getWidth() < 50) {
                column.setWidth(50);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        setSite(site);
        setInput(input);
        setTitleToolTip(Messages.viewsDetailInfoPartTitle);
        setTitleImage(CommonUIPlugin.getImage("icons/navigator/schema_view.png"));

        this.database = (CubridDatabase) input.getAdapter(CubridDatabase.class);
        this.viewList = (List<ViewDetailInfo>) input.getAdapter(List.class);

        StringBuilder partName = new StringBuilder(Messages.viewsDetailInfoPartTitle);
        partName.append(" [")
                .append(database.getUserName())
                .append("@")
                .append(database.getName())
                .append(":")
                .append(database.getDatabaseInfo().getBrokerIP())
                .append("]");
        setPartName(partName.toString());
    }

    public void editView() {
        TableItem[] items = viewsDetailInfoTable.getTable().getSelection();
        if (items.length != 0) {
            TableItem item = items[0];
            ViewDetailInfo viewInfo = (ViewDetailInfo) item.getData();
            Set<String> typeSet = new HashSet<String>();
            typeSet.add(NodeType.USER_VIEW);

            ICubridNode viewNode =
                    CommonUITool.findNode(
                            database,
                            typeSet,
                            viewInfo.getViewUniqueName(
                                    database.getDatabaseInfo().isSupportUserSchema()));
            if (viewNode != null) {
                EditViewAction action =
                        (EditViewAction) ActionManager.getInstance().getAction(EditViewAction.ID);
                if (action.run(database, (ISchemaNode) viewNode) == IDialogConstants.OK_ID) {
                    refresh();
                }
            }
        } else {
            CommonUITool.openWarningBox(Messages.viewDetailInfoPartTableNoSelectionMsg);
        }
    }

    public void dropView() {
        TableItem[] items = viewsDetailInfoTable.getTable().getSelection();
        if (items.length > 0) {
            List<ISchemaNode> selectNodeList = new ArrayList<ISchemaNode>();
            for (TableItem item : items) {
                ViewDetailInfo viewInfo = (ViewDetailInfo) item.getData();
                Set<String> typeSet = new HashSet<String>();
                typeSet.add(NodeType.USER_VIEW);

                ICubridNode viewNode =
                        CommonUITool.findNode(
                                database,
                                typeSet,
                                viewInfo.getViewUniqueName(
                                        database.getDatabaseInfo().isSupportUserSchema()));
                selectNodeList.add((ISchemaNode) viewNode);
            }

            if (selectNodeList.size() > 0) {
                DropViewAction action =
                        (DropViewAction) ActionManager.getInstance().getAction(DropViewAction.ID);

                ISchemaNode[] nodeArr = new ISchemaNode[selectNodeList.size()];
                action.run(selectNodeList.toArray(nodeArr));
                if (!action.isCanceledTask()) {
                    refresh();
                }
            }
        } else {
            CommonUITool.openWarningBox(Messages.viewDetailInfoPartTableNoSelectionMsg);
        }
    }

    public void addView() {
        CreateViewAction action =
                (CreateViewAction) ActionManager.getInstance().getAction(CreateViewAction.ID);
        action.run(database);
        if (!action.isCanceledTask()) {
            refresh();
        }
    }

    /**
     * find whether the column tab is in dataList
     *
     * @param itemName
     * @return
     */
    public boolean findItemName(String itemName) {
        for (ViewDetailInfo view : viewList) {
            if (view.getViewUniqueName(database.getDatabaseInfo().isSupportUserSchema())
                    .equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    public void setFocus() {
        // if view info chaned, ask whether refresh
        if (viewChangeFlag) {
            if (CommonUITool.openConfirmBox(Messages.viewChangeRefreshConfirmMsg)) {
                refresh();
            }
            viewChangeFlag = false;
        }
    }

    public void nodeChanged(CubridNodeChangedEvent event) {
        if (event.getSource() instanceof DefaultSchemaNode) {
            DefaultSchemaNode node = (DefaultSchemaNode) event.getSource();
            if ((node.getType().equals(NodeType.VIEW_FOLDER)
                    || node.getType().equals(NodeType.USER_VIEW)
                            && node.getDatabase().equals(database))) {
                viewChangeFlag = true;
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

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public void doSave(IProgressMonitor monitor) {}

    public void doSaveAs() {}

    public class ViewsDetailTableViewerLabelProvider extends LabelProvider
            implements ITableLabelProvider {
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof ViewDetailInfo) {
                ViewDetailInfo viewDetail = (ViewDetailInfo) element;
                if (viewDetail != null) {
                    switch (columnIndex) {
                        case 0:
                            return viewDetail.getViewOwnerName();
                        case 1:
                            return viewDetail.getViewName();
                        case 2:
                            return viewDetail.getViewDef();
                    }
                }
            }

            return null;
        }
    }

    /**
     * view table content provider
     *
     * @author fulei
     */
    public class ViewsDetailTableViewerContentProvider implements IStructuredContentProvider {
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<ViewDetailInfo> list = (List<ViewDetailInfo>) inputElement;
                ViewDetailInfo[] nodeArr = new ViewDetailInfo[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }

    public CubridDatabase getDatabase() {
        return database;
    }
}
