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
package com.cubrid.cubridmanager.ui.cubrid.jobauto.editor;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.BackupPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.Messages;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.AddBackupPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.AddQueryPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.BackupErrLogAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.DeleteBackupPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.DeleteQueryPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.EditBackupPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.EditQueryPlanAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.action.QueryLogAction;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.progress.OpenJobAutomationInfoPartProgress;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import com.cubrid.cubridmanager.ui.spi.model.loader.CubridDatabaseLoader;
import com.cubrid.cubridmanager.ui.spi.model.loader.jobauto.CubridJobAutoFolderLoader;
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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
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

/**
 * @author fulei
 * @version 1.0 - 2013-1-14 created by fulei
 */
public class JobAutoDashboardEditorPart extends CubridEditorPart {
    public static final String ID = JobAutoDashboardEditorPart.class.getName();
    private boolean backupPlanInfoFlag = false;
    private boolean queryPlanInfoFlag = false;
    private CubridDatabase database;
    private TableViewer backupPlanInfoTable;
    private TableViewer queryPlanInfoTable;
    private List<BackupPlanInfo> backupPlanInfoList;
    private List<QueryPlanInfo> queryPlanInfoList;
    private boolean canAddOrEdit = false;
    private boolean isSupportPeriodicAutoJob = false;

    private final String EMPTY_STRING = "";
    private final String BACKUP_PLAN_DETAIL_EVERYDAY = "nothing";
    private final String QUERY_PLAN_DETAIL_EVERYDAY = "EVERYDAY";
    private final String COMMA = ",";

    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        createBackupPlanInfoTable(parent);
        createQueryPlanInfoTable(parent);

        // expand backup and query plan node on the navigator tree
        expandBackOrQueryPlanChildNode();
        setInputs(true, true);
    }

    public void createBackupPlanInfoTable(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(parent, SWT.RIGHT);
        toolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
        refreshItem.setText(
                com.cubrid.common.ui.cubrid.table.Messages.tablesDetailInfoPartRefreshBtn);
        refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
        refreshItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        refreshBackupPlan();
                    }
                });

        ToolItem createItem = new ToolItem(toolBar, SWT.PUSH);
        createItem.setText(Messages.jobAutoInfoDetailPartCreateBackupBtn);
        createItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_backup_add.png"));
        createItem.setEnabled(canAddOrEdit);
        createItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addBackupPlan();
                    }
                });

        ToolItem editItem = new ToolItem(toolBar, SWT.PUSH);
        editItem.setText(Messages.jobAutoInfoDetailPartEditBackupBtn);
        editItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_backup_edit.png"));
        editItem.setEnabled(canAddOrEdit);
        editItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        editBackupPlan();
                    }
                });

        ToolItem deleteItem = new ToolItem(toolBar, SWT.PUSH);
        deleteItem.setText(Messages.jobAutoInfoDetailPartDeleteBackupBtn);
        deleteItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_backup_delete.png"));
        deleteItem.setEnabled(canAddOrEdit);
        deleteItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        deleteBackupPlan();
                    }
                });

        ToolItem openLogItem = new ToolItem(toolBar, SWT.PUSH);
        openLogItem.setText(Messages.jobAutoInfoDetailPartOpenBackupLogBtn);
        openLogItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_log.png"));
        openLogItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        openBackupPlanLog();
                    }
                });

        final Composite tableComposite = new Composite(parent, SWT.NONE);
        tableComposite.setLayout(new FillLayout());
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        backupPlanInfoTable =
                new TableViewer(
                        tableComposite,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        backupPlanInfoTable.getTable().setHeaderVisible(true);
        backupPlanInfoTable.getTable().setLinesVisible(true);

        backupPlanInfoTable.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        editBackupPlan();
                    }
                });

        final TableViewerColumn idColumn = new TableViewerColumn(backupPlanInfoTable, SWT.LEFT);
        idColumn.getColumn().setWidth(80);
        idColumn.getColumn().setText(Messages.backupPlanInfoTableIDColumn);

        final TableViewerColumn levelColumn = new TableViewerColumn(backupPlanInfoTable, SWT.LEFT);
        levelColumn.getColumn().setWidth(80);
        levelColumn.getColumn().setText(Messages.backupPlanInfoTableLevelColumn);

        final TableViewerColumn pathColumn = new TableViewerColumn(backupPlanInfoTable, SWT.LEFT);
        pathColumn.getColumn().setWidth(200);
        pathColumn.getColumn().setText(Messages.backupPlanInfoTablePathColumn);

        final TableViewerColumn periodTypeColumn =
                new TableViewerColumn(backupPlanInfoTable, SWT.LEFT);
        periodTypeColumn.getColumn().setWidth(100);
        periodTypeColumn.getColumn().setText(Messages.backupPlanInfoTablePeriodTypeColumn);

        final TableViewerColumn backupDetailColumn =
                new TableViewerColumn(backupPlanInfoTable, SWT.LEFT);
        backupDetailColumn.getColumn().setWidth(150);
        backupDetailColumn.getColumn().setText(Messages.backupPlanInfoTablePeriodDetailColumn);

        final TableViewerColumn backupTimeColumn =
                new TableViewerColumn(backupPlanInfoTable, SWT.LEFT);
        backupTimeColumn.getColumn().setWidth(90);
        backupTimeColumn.getColumn().setText(Messages.backupPlanInfoTablePeriodTimeColumn);

        if (isSupportPeriodicAutoJob) {
            final TableViewerColumn backupIntervalColumn =
                    new TableViewerColumn(backupPlanInfoTable, SWT.LEFT);
            backupIntervalColumn.getColumn().setWidth(100);
            backupIntervalColumn
                    .getColumn()
                    .setText(Messages.backupPlanInfoTablePeriodIntervalColumn);
        }

        final TableViewerColumn onOffLineColumn =
                new TableViewerColumn(backupPlanInfoTable, SWT.LEFT);
        onOffLineColumn.getColumn().setWidth(100);
        onOffLineColumn.getColumn().setText(Messages.backupPlanInfoTableOnlineOfflineColumn);

        backupPlanInfoTable.setContentProvider(new BackupPlanTableViewerContentProvider());
        backupPlanInfoTable.setLabelProvider(new BackupPlanTableViewerLabelProvider());

        backupPlanInfoTable
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(backupPlanInfoTable.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(backupPlanInfoTable.getTable());
        backupPlanInfoTable.getTable().setMenu(contextMenu);

        Menu menu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);

        final MenuItem createMenuItem = new MenuItem(menu, SWT.PUSH);
        createMenuItem.setText(Messages.jobAutoInfoDetailPartCreateBackupBtn);
        createMenuItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_backup_add.png"));
        createMenuItem.setEnabled(canAddOrEdit);
        createMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addBackupPlan();
                    }
                });

        final MenuItem editMenuItem = new MenuItem(menu, SWT.PUSH);
        editMenuItem.setText(Messages.jobAutoInfoDetailPartEditBackupBtn);
        editMenuItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_backup_edit.png"));
        editMenuItem.setEnabled(canAddOrEdit);
        editMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        editBackupPlan();
                    }
                });

        final MenuItem dropMenuItem = new MenuItem(menu, SWT.PUSH);
        dropMenuItem.setText(Messages.jobAutoInfoDetailPartDeleteBackupBtn);
        dropMenuItem.setImage(
                CubridManagerUIPlugin.getImage("icons/action/auto_backup_delete.png"));
        dropMenuItem.setEnabled(canAddOrEdit);
        dropMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        deleteBackupPlan();
                    }
                });

        final MenuItem openLogMenuItem = new MenuItem(menu, SWT.PUSH);
        openLogMenuItem.setText(Messages.jobAutoInfoDetailPartOpenBackupLogBtn);
        openLogMenuItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_log.png"));
        openLogMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        openBackupPlanLog();
                    }
                });

        backupPlanInfoTable.getTable().setMenu(menu);
    }

    public void createQueryPlanInfoTable(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(parent, SWT.RIGHT);
        toolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
        refreshItem.setText(
                com.cubrid.common.ui.cubrid.table.Messages.tablesDetailInfoPartRefreshBtn);
        refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
        refreshItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        refreshQueryPlan();
                    }
                });

        ToolItem createItem = new ToolItem(toolBar, SWT.PUSH);
        createItem.setText(Messages.jobAutoInfoDetailPartCreateQueryPlanBtn);
        createItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_query_add.png"));
        createItem.setEnabled(canAddOrEdit);
        createItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addQueryPlan();
                    }
                });

        ToolItem editItem = new ToolItem(toolBar, SWT.PUSH);
        editItem.setText(Messages.jobAutoInfoDetailPartEditQueryPlanBtn);
        editItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_query_edit.png"));
        editItem.setEnabled(canAddOrEdit);
        editItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        editQueryPlan();
                    }
                });

        ToolItem deleteItem = new ToolItem(toolBar, SWT.PUSH);
        deleteItem.setText(Messages.jobAutoInfoDetailPartDeleteQueryPlanBtn);
        deleteItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_query_delete.png"));
        deleteItem.setEnabled(canAddOrEdit);
        deleteItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        deleteQueryPlan();
                    }
                });

        ToolItem openLogItem = new ToolItem(toolBar, SWT.PUSH);
        openLogItem.setText(Messages.jobAutoInfoDetailPartOpenQueryLogBtn);
        openLogItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_log.png"));
        openLogItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        openQueryPlanLog();
                    }
                });

        final Composite tableComposite = new Composite(parent, SWT.NONE);
        tableComposite.setLayout(new FillLayout());
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        queryPlanInfoTable =
                new TableViewer(
                        tableComposite,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        queryPlanInfoTable.getTable().setHeaderVisible(true);
        queryPlanInfoTable.getTable().setLinesVisible(true);

        queryPlanInfoTable.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        editQueryPlan();
                    }
                });

        final TableViewerColumn nameColumn = new TableViewerColumn(queryPlanInfoTable, SWT.LEFT);
        nameColumn.getColumn().setWidth(120);
        nameColumn.getColumn().setText(Messages.queryPlanInfoTableNameColumn);

        final TableViewerColumn periodTypeColumn =
                new TableViewerColumn(queryPlanInfoTable, SWT.LEFT);
        periodTypeColumn.getColumn().setWidth(100);
        periodTypeColumn.getColumn().setText(Messages.queryPlanInfoTablePeriodTypeColumn);

        final TableViewerColumn periodDetailColumn =
                new TableViewerColumn(queryPlanInfoTable, SWT.LEFT);
        periodDetailColumn.getColumn().setWidth(150);
        periodDetailColumn.getColumn().setText(Messages.queryPlanInfoTablePeriodDetailColumn);

        final TableViewerColumn queryTimeColumn =
                new TableViewerColumn(queryPlanInfoTable, SWT.LEFT);
        queryTimeColumn.getColumn().setWidth(90);
        queryTimeColumn.getColumn().setText(Messages.queryPlanInfoTablePeriodTimeColumn);

        if (isSupportPeriodicAutoJob) {
            final TableViewerColumn queryIntervalColumn =
                    new TableViewerColumn(queryPlanInfoTable, SWT.LEFT);
            queryIntervalColumn.getColumn().setWidth(100);
            queryIntervalColumn
                    .getColumn()
                    .setText(Messages.queryPlanInfoTablePeriodIntervalColumn);
        }

        final TableViewerColumn queryColumn = new TableViewerColumn(queryPlanInfoTable, SWT.LEFT);
        queryColumn.getColumn().setWidth(200);
        queryColumn.getColumn().setText(Messages.queryPlanInfoTableQueryColumn);

        queryPlanInfoTable.setContentProvider(new QueryPlanTableViewerContentProvider());
        queryPlanInfoTable.setLabelProvider(new QueryPlanTableViewerLabelProvider());

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(queryPlanInfoTable.getTable());
        queryPlanInfoTable.getTable().setMenu(contextMenu);

        Menu menu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);

        final MenuItem createMenuItem = new MenuItem(menu, SWT.PUSH);
        createMenuItem.setText(Messages.jobAutoInfoDetailPartCreateQueryPlanBtn);
        createMenuItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_query_add.png"));
        createMenuItem.setEnabled(canAddOrEdit);
        createMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addQueryPlan();
                    }
                });

        final MenuItem editMenuItem = new MenuItem(menu, SWT.PUSH);
        editMenuItem.setText(Messages.jobAutoInfoDetailPartEditQueryPlanBtn);
        editMenuItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_query_edit.png"));
        editMenuItem.setEnabled(canAddOrEdit);
        editMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        editQueryPlan();
                    }
                });

        final MenuItem dropMenuItem = new MenuItem(menu, SWT.PUSH);
        dropMenuItem.setText(Messages.jobAutoInfoDetailPartDeleteQueryPlanBtn);
        dropMenuItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_query_delete.png"));
        dropMenuItem.setEnabled(canAddOrEdit);
        dropMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        deleteQueryPlan();
                    }
                });

        final MenuItem openLogMenuItem = new MenuItem(menu, SWT.PUSH);
        openLogMenuItem.setText(Messages.jobAutoInfoDetailPartOpenQueryLogBtn);
        openLogMenuItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_log.png"));
        openLogMenuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        openQueryPlanLog();
                    }
                });

        queryPlanInfoTable.getTable().setMenu(menu);
    }

    public void addBackupPlan() {
        AddBackupPlanAction action =
                (AddBackupPlanAction) ActionManager.getInstance().getAction(AddBackupPlanAction.ID);
        action.run(database);
        if (!action.isCanceledTask()) {
            refreshBackupPlan();
        }
    }

    public void addQueryPlan() {
        AddQueryPlanAction action =
                (AddQueryPlanAction) ActionManager.getInstance().getAction(AddQueryPlanAction.ID);
        action.run(database);
        if (!action.isCanceledTask()) {
            refreshQueryPlan();
        }
    }

    public void editBackupPlan() {
        TableItem[] items = backupPlanInfoTable.getTable().getSelection();
        if (items.length != 0) {
            TableItem item = items[0];
            BackupPlanInfo backupPlanInfo = (BackupPlanInfo) item.getData();
            ICubridNode backupPlanNode =
                    findAutoJobInfoNode(CubridNodeType.BACKUP_PLAN, backupPlanInfo.getBackupid());
            if (backupPlanNode != null) {
                EditBackupPlanAction action =
                        (EditBackupPlanAction)
                                ActionManager.getInstance().getAction(EditBackupPlanAction.ID);
                if (action.run(database, (DefaultSchemaNode) backupPlanNode)
                        == IDialogConstants.OK_ID) {
                    refreshBackupPlan();
                }
            }
        } else {
            CommonUITool.openWarningBox(Messages.errJobAutoNoSelection);
        }
    }

    public void editQueryPlan() {
        TableItem[] items = queryPlanInfoTable.getTable().getSelection();
        if (items.length != 0) {
            TableItem item = items[0];
            QueryPlanInfo queryPlanInfo = (QueryPlanInfo) item.getData();
            Set<String> typeSet = new HashSet<String>();
            typeSet.add(CubridNodeType.QUERY_PLAN);

            ICubridNode queryPlanNode =
                    findAutoJobInfoNode(CubridNodeType.QUERY_PLAN, queryPlanInfo.getQuery_id());
            if (queryPlanNode != null) {
                EditQueryPlanAction action =
                        (EditQueryPlanAction)
                                ActionManager.getInstance().getAction(EditQueryPlanAction.ID);
                if (action.run(database, (DefaultSchemaNode) queryPlanNode)
                        == IDialogConstants.OK_ID) {
                    refreshQueryPlan();
                }
            }
        } else {
            CommonUITool.openWarningBox(Messages.errJobAutoNoSelection);
        }
    }

    public void deleteBackupPlan() {
        TableItem[] items = backupPlanInfoTable.getTable().getSelection();
        if (items.length > 0) {
            List<ISchemaNode> selectNodeList = new ArrayList<ISchemaNode>();
            for (TableItem item : items) {
                BackupPlanInfo backupPlanInfo = (BackupPlanInfo) item.getData();
                Set<String> typeSet = new HashSet<String>();
                typeSet.add(CubridNodeType.BACKUP_PLAN);
                ICubridNode backupPlanNode =
                        CommonUITool.findNode(database, typeSet, backupPlanInfo.getBackupid());
                selectNodeList.add((ISchemaNode) backupPlanNode);
            }

            if (selectNodeList.size() > 0) {
                DeleteBackupPlanAction action =
                        (DeleteBackupPlanAction)
                                ActionManager.getInstance().getAction(DeleteBackupPlanAction.ID);

                ISchemaNode[] nodeArr = new ISchemaNode[selectNodeList.size()];
                action.run(selectNodeList.toArray(nodeArr));
                refreshBackupPlan();
            }
        } else {
            CommonUITool.openWarningBox(Messages.errJobAutoNoSelection);
        }
    }

    public void deleteQueryPlan() {
        TableItem[] items = queryPlanInfoTable.getTable().getSelection();
        if (items.length > 0) {
            List<ISchemaNode> selectNodeList = new ArrayList<ISchemaNode>();
            for (TableItem item : items) {
                QueryPlanInfo queryPlanInfo = (QueryPlanInfo) item.getData();
                Set<String> typeSet = new HashSet<String>();
                typeSet.add(CubridNodeType.QUERY_PLAN);
                ICubridNode queryPlanNode =
                        CommonUITool.findNode(database, typeSet, queryPlanInfo.getQuery_id());
                selectNodeList.add((ISchemaNode) queryPlanNode);
            }

            if (selectNodeList.size() > 0) {
                DeleteQueryPlanAction action =
                        (DeleteQueryPlanAction)
                                ActionManager.getInstance().getAction(DeleteQueryPlanAction.ID);

                ISchemaNode[] nodeArr = new ISchemaNode[selectNodeList.size()];
                action.run(selectNodeList.toArray(nodeArr));
                refreshQueryPlan();
            }
        } else {
            CommonUITool.openWarningBox(Messages.errJobAutoNoSelection);
        }
    }

    public void expandBackOrQueryPlanChildNode() {
        Set<String> typeSet = new HashSet<String>();
        typeSet.add(CubridNodeType.JOB_FOLDER);
        ICubridNode jobFolderNode = CommonUITool.findNode(database, typeSet);
        if (jobFolderNode != null) {
            CubridNavigatorView view =
                    CubridNavigatorView.getNavigatorView("com.cubrid.cubridmanager.host.navigator");
            if (view == null) {
                return;
            }
            TreeViewer treeViewer = view.getViewer();
            for (ICubridNode backOrQueryFolderNode : jobFolderNode.getChildren()) {
                // if not expand ,expand the node and wait until all children be added
                if (!treeViewer.getExpandedState(backOrQueryFolderNode)) {
                    treeViewer.expandToLevel(backOrQueryFolderNode, 1);
                }
            }
        }
    }

    public void openBackupPlanLog() {
        BackupErrLogAction action =
                (BackupErrLogAction) ActionManager.getInstance().getAction(BackupErrLogAction.ID);
        action.run(database);
    }

    public void openQueryPlanLog() {
        QueryLogAction action =
                (QueryLogAction) ActionManager.getInstance().getAction(QueryLogAction.ID);
        action.run();
    }

    public void refreshBackupPlan() {
        OpenJobAutomationInfoPartProgress progress =
                new OpenJobAutomationInfoPartProgress(database, true, false);
        progress.loadJobAutomationInfoList();
        if (progress.isSuccess()) {
            backupPlanInfoList = progress.getBackupPlanInfoList();
            setInputs(true, false);
        }
        backupPlanInfoFlag = false;
    }

    public void refreshQueryPlan() {
        OpenJobAutomationInfoPartProgress progress =
                new OpenJobAutomationInfoPartProgress(database, false, true);
        progress.loadJobAutomationInfoList();
        if (progress.isSuccess()) {
            queryPlanInfoList = progress.getQueryPlanInfoList();
            setInputs(false, true);
        }
        queryPlanInfoFlag = false;
    }

    public void refreshAll() {
        OpenJobAutomationInfoPartProgress progress =
                new OpenJobAutomationInfoPartProgress(database);
        progress.loadJobAutomationInfoList();
        if (progress.isSuccess()) {
            backupPlanInfoList = progress.getBackupPlanInfoList();
            queryPlanInfoList = progress.getQueryPlanInfoList();
            setInputs(true, true);
        }
        backupPlanInfoFlag = false;
        queryPlanInfoFlag = false;
    }

    public void setInputs(boolean setBackupPlanInfo, boolean setQueryPlanInfo) {
        if (setBackupPlanInfo) {
            backupPlanInfoTable.setInput(backupPlanInfoList);
            backupPlanInfoTable.refresh();
        }
        if (setQueryPlanInfo) {
            queryPlanInfoTable.setInput(queryPlanInfoList);
            queryPlanInfoTable.refresh();
        }
        pack();
    }

    public void pack() {
        for (int i = 0; i < backupPlanInfoTable.getTable().getColumnCount(); i++) {
            TableColumn column = backupPlanInfoTable.getTable().getColumn(i);
            if (column.getWidth() > 600) {
                column.setWidth(600);
            }
            if (column.getWidth() < 100) {
                column.setWidth(100);
            }
        }

        for (int i = 0; i < queryPlanInfoTable.getTable().getColumnCount(); i++) {
            TableColumn column = queryPlanInfoTable.getTable().getColumn(i);
            if (column.getWidth() > 600) {
                column.setWidth(600);
            }
            if (column.getWidth() < 100) {
                column.setWidth(100);
            }
        }
    }

    /**
     * find queryPlanoNode or backupPlanNode from treeView
     *
     * @param nodeType String
     * @param nodeType id
     * @return
     */
    public ICubridNode findAutoJobInfoNode(String nodeType, String id) {
        Set<String> typeSet = new HashSet<String>();
        typeSet.add(nodeType);
        ICubridNode node = CommonUITool.findNode(database, typeSet, id);
        // if backupPlanNode is null,expand the backupPlanNodeFolder then find again
        if (node == null) {
            CubridNavigatorView view =
                    CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
            TreeViewer treeViewer = view.getViewer();
            ICubridNode jobAutoFolderNode =
                    database.getChild(
                            database.getId()
                                    + ICubridNodeLoader.NODE_SEPARATOR
                                    + CubridDatabaseLoader.JOB_AUTO_FOLDER_ID);
            String childNodeFolderID =
                    nodeType.equals(CubridNodeType.BACKUP_PLAN)
                            ? CubridJobAutoFolderLoader.BACKUP_PLAN_FOLDER_ID
                            : CubridJobAutoFolderLoader.QUERY_PLAN_FOLDER_ID;
            DefaultSchemaNode folderNode =
                    (DefaultSchemaNode)
                            jobAutoFolderNode.getChild(
                                    jobAutoFolderNode.getId()
                                            + ICubridNodeLoader.NODE_SEPARATOR
                                            + childNodeFolderID);
            treeViewer.expandToLevel(folderNode, 1);
            // wait 5 times expand the folder node
            int time = 0;
            while (folderNode.getChildren().size() == 0 && time++ < 5) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }
        }

        return CommonUITool.findNode(database, typeSet, id);
    }

    public void setFocus() {
        String msg = com.cubrid.common.ui.common.Messages.dashboardConfirmRefreshDataMsg;
        if (backupPlanInfoFlag && queryPlanInfoFlag) {
            if (CommonUITool.openConfirmBox(msg)) {
                refreshAll();
                backupPlanInfoFlag = false;
                queryPlanInfoFlag = false;
            }
        } else if (backupPlanInfoFlag) {
            if (CommonUITool.openConfirmBox(msg)) {
                refreshBackupPlan();
            }
            backupPlanInfoFlag = false;
        } else if (queryPlanInfoFlag) {
            if (CommonUITool.openConfirmBox(msg)) {
                refreshQueryPlan();
            }
            queryPlanInfoFlag = false;
        }
    }

    public void nodeChanged(CubridNodeChangedEvent event) {
        if (event.getSource() instanceof DefaultSchemaNode) {
            DefaultSchemaNode node = (DefaultSchemaNode) event.getSource();
            if ((node.getType().equals(CubridNodeType.BACKUP_PLAN)
                            || node.getType().equals(CubridNodeType.BACKUP_PLAN_FOLDER))
                    && node.getDatabase().equals(database)) {
                backupPlanInfoFlag = true;
            }
            if ((node.getType().equals(CubridNodeType.QUERY_PLAN)
                            || node.getType().equals(CubridNodeType.QUERY_PLAN_FOLDER))
                    && node.getDatabase().equals(database)) {
                queryPlanInfoFlag = true;
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

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        this.database = (CubridDatabase) input.getAdapter(CubridDatabase.class);

        JobAutoDashboardInput jobAutoDetailInfoPartInput = (JobAutoDashboardInput) input;
        backupPlanInfoList = jobAutoDetailInfoPartInput.getBackupPlanInfoList();
        queryPlanInfoList = jobAutoDetailInfoPartInput.getQueryPlanInfoList();

        StringBuilder partName = new StringBuilder(Messages.jobAutoDetailInfoPartTitle);
        partName.append(" [")
                .append(database.getUserName())
                .append("@")
                .append(database.getName())
                .append(":")
                .append(database.getDatabaseInfo().getBrokerIP())
                .append("]");
        setPartName(partName.toString());

        DbUserInfo dbUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
        if (dbUserInfo != null && dbUserInfo.isDbaAuthority()) {
            canAddOrEdit = true;
        }

        isSupportPeriodicAutoJob =
                CompatibleUtil.isSupportPeriodicAutoJob(database.getDatabaseInfo());
    }

    /**
     * backup plab table label provider
     *
     * @author Administrator
     */
    public class BackupPlanTableViewerLabelProvider extends LabelProvider
            implements ITableLabelProvider {
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof BackupPlanInfo) {
                BackupPlanInfo backupPlanInfo = (BackupPlanInfo) element;
                if (backupPlanInfo != null) {
                    String detail = backupPlanInfo.getPeriod_date();
                    String rawTime = backupPlanInfo.getTime();
                    String time = null;
                    String interval = null;
                    if (isSupportPeriodicAutoJob && rawTime.startsWith("i")) {
                        interval = rawTime.substring(1);
                    } else {
                        time = rawTime.substring(0, 2) + ":" + rawTime.substring(2);
                    }
                    boolean online = CommonUITool.str2Boolean(backupPlanInfo.getOnoff());
                    String onOffLine = online ? "Online" : "Offline";
                    switch (columnIndex) {
                        case 0:
                            return backupPlanInfo.getBackupid();
                        case 1:
                            String level = backupPlanInfo.getLevel();
                            if (level.equals("0")) {
                                return Messages.zeroLever;
                            } else if (level.equals("1")) {
                                return Messages.oneLever;
                            } else if (level.equals("2")) {
                                return Messages.twoLever;
                            } else {
                                return level;
                            }
                        case 2:
                            return backupPlanInfo.getPath();
                        case 3:
                            String type = backupPlanInfo.getPeriod_type();
                            if (type.equalsIgnoreCase("Monthly")) {
                                return Messages.monthlyPeriodType;
                            } else if (type.equalsIgnoreCase("Weekly")) {
                                return Messages.weeklyPeriodType;
                            } else if (type.equalsIgnoreCase("Daily")) {
                                return Messages.dailyPeriodType;
                            } else if (type.equalsIgnoreCase("Special")) {
                                return Messages.specialdayPeriodType;
                            } else {
                                return type;
                            }
                        case 4:
                            return getDetailValueForBackupPlanDisplay(detail);
                        case 5:
                            return time;
                        case 6:
                            if (isSupportPeriodicAutoJob) {
                                return interval;
                            } else {
                                return onOffLine;
                            }
                        case 7:
                            if (isSupportPeriodicAutoJob) {
                                return onOffLine;
                            } else {
                                return null;
                            }
                    }
                }
            }

            return null;
        }
    }

    /**
     * query plan table label provider
     *
     * @author Administrator
     */
    public class QueryPlanTableViewerLabelProvider extends LabelProvider
            implements ITableLabelProvider {
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof QueryPlanInfo) {
                QueryPlanInfo queryPlanInfo = (QueryPlanInfo) element;
                if (queryPlanInfo != null) {
                    String detail =
                            queryPlanInfo
                                    .getDetail()
                                    .substring(0, queryPlanInfo.getDetail().indexOf(" "));
                    String rawTime =
                            queryPlanInfo
                                    .getDetail()
                                    .substring(queryPlanInfo.getDetail().indexOf(" ") + 1)
                                    .trim();
                    String time = null;
                    String interval = null;
                    if (isSupportPeriodicAutoJob && rawTime.startsWith("i")) {
                        interval = rawTime.substring(1);
                    } else {
                        time = rawTime;
                    }

                    switch (columnIndex) {
                        case 0:
                            return queryPlanInfo.getQuery_id();
                        case 1:
                            String type = queryPlanInfo.getPeriod();
                            if (type.equalsIgnoreCase("Month")) {
                                return Messages.monthlyPeriodType;
                            } else if (type.equalsIgnoreCase("Week")) {
                                return Messages.weeklyPeriodType;
                            } else if (type.equalsIgnoreCase("Day")) {
                                return Messages.dailyPeriodType;
                            } else if (type.equalsIgnoreCase("One")) {
                                return Messages.specialdayPeriodType;
                            } else {
                                return type;
                            }
                        case 2:
                            return getDetailValueForQueryPlanDisplay(detail);
                        case 3:
                            return time;
                        case 4:
                            if (isSupportPeriodicAutoJob) {
                                return interval;
                            } else {
                                return queryPlanInfo.getQuery_string();
                            }
                        case 5:
                            if (isSupportPeriodicAutoJob) {
                                return queryPlanInfo.getQuery_string();
                            } else {
                                return null;
                            }
                    }
                }
            }

            return null;
        }
    }

    /**
     * backup plan table content provider
     *
     * @author fulei
     */
    public class BackupPlanTableViewerContentProvider implements IStructuredContentProvider {
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<BackupPlanInfo> list = (List<BackupPlanInfo>) inputElement;
                BackupPlanInfo[] nodeArr = new BackupPlanInfo[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }

    /**
     * query plan table content provider
     *
     * @author fulei
     */
    public class QueryPlanTableViewerContentProvider implements IStructuredContentProvider {
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<QueryPlanInfo> list = (List<QueryPlanInfo>) inputElement;
                QueryPlanInfo[] nodeArr = new QueryPlanInfo[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
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

    private String getDetailValueForBackupPlanDisplay(String rawDetail) {
        if (StringUtil.isEmpty(rawDetail) || BACKUP_PLAN_DETAIL_EVERYDAY.equals(rawDetail)) {
            return null;
        }
        String[] detailAr = rawDetail.split(COMMA);
        StringBuilder sb = new StringBuilder();
        for (String detailVal : detailAr) {
            if (sb.length() > 0) {
                sb.append(COMMA);
            }
            String temp = EMPTY_STRING;
            if (detailVal.equalsIgnoreCase("Sunday")) {
                temp = Messages.sundayOfWeek;
            } else if (detailVal.equalsIgnoreCase("Monday")) {
                temp = Messages.mondayOfWeek;
            } else if (detailVal.equalsIgnoreCase("Tuesday")) {
                temp = Messages.tuesdayOfWeek;
            } else if (detailVal.equalsIgnoreCase("Wednesday")) {
                temp = Messages.wednesdayOfWeek;
            } else if (detailVal.equalsIgnoreCase("Thursday")) {
                temp = Messages.thursdayOfWeek;
            } else if (detailVal.equalsIgnoreCase("Friday")) {
                temp = Messages.fridayOfWeek;
            } else if (detailVal.equalsIgnoreCase("Saturday")) {
                temp = Messages.saturdayOfWeek;
            } else {
                temp = detailVal;
            }
            sb.append(temp);
        }

        return sb.toString();
    }

    private String getDetailValueForQueryPlanDisplay(String rawDetail) {
        if (StringUtil.isEmpty(rawDetail) || QUERY_PLAN_DETAIL_EVERYDAY.equals(rawDetail)) {
            return null;
        }
        String[] detailAr = rawDetail.split(COMMA);
        StringBuilder sb = new StringBuilder();
        for (String detailVal : detailAr) {
            if (sb.length() > 0) {
                sb.append(COMMA);
            }
            String temp = EMPTY_STRING;
            if (detailVal.equalsIgnoreCase("SUN")) {
                temp = Messages.sundayOfWeek;
            } else if (detailVal.equalsIgnoreCase("MON")) {
                temp = Messages.mondayOfWeek;
            } else if (detailVal.equalsIgnoreCase("TUE")) {
                temp = Messages.tuesdayOfWeek;
            } else if (detailVal.equalsIgnoreCase("WED")) {
                temp = Messages.wednesdayOfWeek;
            } else if (detailVal.equalsIgnoreCase("THU")) {
                temp = Messages.thursdayOfWeek;
            } else if (detailVal.equalsIgnoreCase("FRI")) {
                temp = Messages.fridayOfWeek;
            } else if (detailVal.equalsIgnoreCase("SAT")) {
                temp = Messages.saturdayOfWeek;
            } else {
                temp = detailVal;
            }
            sb.append(temp);
        }

        return sb.toString();
    }
}
