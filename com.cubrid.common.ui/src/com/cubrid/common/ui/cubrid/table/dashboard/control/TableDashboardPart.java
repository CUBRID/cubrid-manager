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

package com.cubrid.common.ui.cubrid.table.dashboard.control;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.IDatabaseSpec;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.action.CopyToClipboardAction;
import com.cubrid.common.ui.cubrid.table.action.CreateLikeTableAction;
import com.cubrid.common.ui.cubrid.table.action.DeleteTableAction;
import com.cubrid.common.ui.cubrid.table.action.DropTableAction;
import com.cubrid.common.ui.cubrid.table.action.EditTableAction;
import com.cubrid.common.ui.cubrid.table.action.ExportTableDefinitionAction;
import com.cubrid.common.ui.cubrid.table.action.ExportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.ImportDataFromFileAction;
import com.cubrid.common.ui.cubrid.table.action.ImportWizardAction;
import com.cubrid.common.ui.cubrid.table.action.InsertOneByPstmtAction;
import com.cubrid.common.ui.cubrid.table.action.NewTableAction;
import com.cubrid.common.ui.cubrid.table.action.RenameTableAction;
import com.cubrid.common.ui.cubrid.table.action.SelectByMultiPstmtDataAction;
import com.cubrid.common.ui.cubrid.table.action.SelectByOnePstmtDataAction;
import com.cubrid.common.ui.cubrid.table.action.TableSelectAllAction;
import com.cubrid.common.ui.cubrid.table.action.TableSelectCountAction;
import com.cubrid.common.ui.cubrid.table.action.TableToJavaCodeAction;
import com.cubrid.common.ui.cubrid.table.action.TableToPhpCodeAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeCreateQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeDeleteQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeInsertQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeSelectPstmtQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeSelectQueryAction;
import com.cubrid.common.ui.cubrid.table.action.makequery.MakeUpdateQueryAction;
import com.cubrid.common.ui.cubrid.table.dashboard.control.TableDashboardComposite.TablesDetailInfoCTabItem;
import com.cubrid.common.ui.query.editor.QueryEditorUtil;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.LoadTableColumnsProgress;
import com.cubrid.common.ui.spi.progress.LoadTableDetailInfoTask;
import com.cubrid.common.ui.spi.progress.LoadTableKeysProgress;
import com.cubrid.common.ui.spi.progress.LoadTableProgress;
import com.cubrid.common.ui.spi.progress.LoadTableRecordCountsProgress;
import com.cubrid.common.ui.spi.progress.LoadTableRecordSizeProgress;
import com.cubrid.common.ui.spi.progress.OpenTablesDetailInfoPartProgress;
import com.cubrid.common.ui.spi.table.button.ITableButtonSupportEvent;
import com.cubrid.common.ui.spi.table.button.InputTextDialog;
import com.cubrid.common.ui.spi.table.button.TableEditButtonSupport;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.table.SchemaProvider;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;

public class TableDashboardPart extends CubridEditorPart implements ITableButtonSupportEvent {
	private static final Logger LOGGER = LogUtil.getLogger(TableDashboardPart.class);
	public static final String ID = TableDashboardPart.class.getName();

	private TableViewer tableListView;
	private CubridDatabase database;
	private SashForm topSash;
	private CTabFolder tabFolder;
	private List<TableDetailInfo> tableList;
	private SchemaInfo schemaInfo;
	private boolean isSchemaCommentInstalled;

	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		ToolBar toolBar = new ToolBar(parent, SWT.LEFT_TO_RIGHT| SWT.FLAT);
		toolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
		refreshItem.setText(Messages.tablesDetailInfoPartRefreshBtn);
		refreshItem.setToolTipText(Messages.tablesDetailInfoPartBtnRefreshTip);
		refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
		refreshItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem countItem = new ToolItem(toolBar, SWT.PUSH);
		countItem.setText(Messages.tablesDetailInfoPartBtnEsitmateRecord);
		countItem.setToolTipText(Messages.tablesDetailInfoPartBtnEsitmateRecordTip);
		countItem.setImage(CommonUIPlugin.getImage("icons/action/count.gif"));
		countItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List<TableDetailInfo> list = new ArrayList<TableDetailInfo>();
				TableItem[] items = tableListView.getTable().getSelection();
				for (TableItem item : items) {
					list.add((TableDetailInfo) item.getData());
				}

				// Check selected size and confirm
				if (list.size() == 0) {
					CommonUITool.openWarningBox(Messages.tablesDetailInfoPartAlertNotSelected);
					return;
				}

				if (CommonUITool.openConfirmBox(Messages.tablesDetailInfoPartBtnEsitmateRecordAlert)) {
					LoadTableProgress progress = new LoadTableRecordCountsProgress(
							database, list,
							Messages.loadTableRecordCountsProgressTaskName,
							Messages.loadTableRecordCountsProgressSubTaskName);
					progress.getCount();
					tableListView.refresh();
				}
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem columnItem = new ToolItem(toolBar, SWT.PUSH);
		columnItem.setText(Messages.tablesDetailInfoPartBtnEsitmateColumn);
		columnItem.setToolTipText(Messages.tablesDetailInfoPartBtnEsitmateColumnTip);
		columnItem.setImage(CommonUIPlugin.getImage("icons/action/table_column_item.png"));
		columnItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TableDetailInfo> list = new ArrayList<TableDetailInfo>();
				TableItem[] items = tableListView.getTable().getSelection();
				for (TableItem item : items) {
					list.add((TableDetailInfo) item.getData());
				}

				// Check selected size and confirm
				if (list.size() == 0) {
					CommonUITool.openWarningBox(Messages.tablesDetailInfoPartAlertNotSelected);
					return;
				}

				if (CommonUITool.openConfirmBox(Messages.bind(
						Messages.tablesDetailInfoPartBtnEsitmateAlert, "Columns"))) {
					LoadTableProgress progress = new LoadTableColumnsProgress(
							database, list,
							Messages.loadTableColumnsProgressTaskName,
							Messages.loadTableColumnsProgressSubTaskName);
					progress.getCount();
					tableListView.refresh();
				}
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem keyItem = new ToolItem(toolBar, SWT.PUSH);
		keyItem.setText(Messages.tablesDetailInfoPartBtnEsitmateKey);
		keyItem.setToolTipText(Messages.tablesDetailInfoPartBtnEsitmateKeyTip);
		keyItem.setImage(CommonUIPlugin.getImage("icons/action/key.png"));
		keyItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TableDetailInfo> list = new ArrayList<TableDetailInfo>();
				TableItem[] items = tableListView.getTable().getSelection();
				for (TableItem item : items) {
					list.add((TableDetailInfo) item.getData());
				}

				// Check selected size and confirm
				if (list.size() == 0) {
					CommonUITool.openWarningBox(Messages.tablesDetailInfoPartAlertNotSelected);
					return;
				}

				if (CommonUITool.openConfirmBox(Messages.bind(
						Messages.tablesDetailInfoPartBtnEsitmateAlert, "Keys"))) {
					LoadTableProgress progress = new LoadTableKeysProgress(
							database, list,
							Messages.loadTableKeysProgressTaskName,
							Messages.loadTableKeysProgressSubTaskName);
					progress.getCount();
					tableListView.refresh();
				}
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem recordSizeItem = new ToolItem(toolBar, SWT.PUSH);
		recordSizeItem.setText(Messages.tablesDetailInfoPartBtnEsitmateRecordSize);
		recordSizeItem.setToolTipText(Messages.tablesDetailInfoPartBtnEsitmateRecordSizeTip);
		recordSizeItem.setImage(CommonUIPlugin.getImage("icons/action/record_size.png"));
		recordSizeItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TableDetailInfo> list = new ArrayList<TableDetailInfo>();
				TableItem[] items = tableListView.getTable().getSelection();
				for (TableItem item : items) {
					list.add((TableDetailInfo) item.getData());
				}

				// Check selected size and confirm
				if (list.size() == 0) {
					CommonUITool.openWarningBox(Messages.tablesDetailInfoPartAlertNotSelected);
					return;
				}

				if (CommonUITool.openConfirmBox(Messages.bind(
						Messages.tablesDetailInfoPartBtnEsitmateAlert, "Record size"))) {
					LoadTableProgress progress = new LoadTableRecordSizeProgress(
							database, list,
							Messages.loadTableRecordSizeProgressTaskName,
							Messages.loadTableRecordSizeProgressSubTaskName);
					progress.getCount();
					tableListView.refresh();
				}
			}
		});

		if (!Util.isWindows()) {
			new ToolItem(toolBar, SWT.SEPARATOR);
			ToolItem viewDataItem = new ToolItem(toolBar, SWT.PUSH);
			viewDataItem.setText(Messages.tablesDetailInfoPartBtnViewData);
			viewDataItem.setToolTipText(Messages.tablesDetailInfoPartBtnViewDataTip);
			viewDataItem.setImage(CommonUIPlugin.getImage("icons/action/table_select_all.png"));
			viewDataItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					TableItem[] items = tableListView.getTable().getSelection();
					if (items.length == 1) {
						TableDetailInfo tableDetailInfo = (TableDetailInfo) items[0].getData();
						String query = SQLGenerateUtils.getSelectSQLWithLimit(tableDetailInfo.getTableName(), 1, 100);
						QueryEditorUtil.openQueryEditorAndRunQuery(database, query, true, true);
					} else {
						CommonUITool.openInformationBox(Messages.tablesDetailInfoPartBtnViewDataSelectOne);
					}
				}
			});
		}

		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem copyTableNamesItem = new ToolItem(toolBar, SWT.PUSH);
		copyTableNamesItem.setText(Messages.tablesDetailInfoPartBtnCopyTableNames);
		copyTableNamesItem.setToolTipText(Messages.tablesDetailInfoPartBtnCopyTableNamesTip);
		copyTableNamesItem.setImage(CommonUIPlugin.getImage("icons/action/copy_table_name.gif"));
		copyTableNamesItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List<String> nameList = new ArrayList<String>();
				for (TableDetailInfo tablesDetailInfoPOJO : tableList) {
					nameList.add(tablesDetailInfoPOJO.getTableName());
				}
				if (nameList.size() == 0) {
					CommonUITool.openWarningBox(Messages.tablesDetailInfoPartBtnCopySuccessFailed);
					return;
				}
				copyNamesToClipboard(nameList);
				CommonUITool.openInformationBox(
						Messages.tablesDetailInfoPartBtnCopySuccessTitle,
						Messages.tablesDetailInfoPartBtnCopySuccessMsg);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem copyColumnNamesItem = new ToolItem(toolBar, SWT.PUSH);
		copyColumnNamesItem.setText(Messages.tablesDetailInfoPartBtnCopyColumnNames);
		copyColumnNamesItem.setToolTipText(Messages.tablesDetailInfoPartBtnCopyColumnNamesTip);
		copyColumnNamesItem.setImage(CommonUIPlugin.getImage("icons/action/copy_column_name.gif"));
		copyColumnNamesItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TablesDetailInfoCTabItem tabItem = (TablesDetailInfoCTabItem) tabFolder.getSelection();
				schemaInfo = tabItem.getTableInfoComposite().getData();
				if (schemaInfo == null) {
					CommonUITool.openWarningBox(Messages.tablesDetailInfoPartBtnCopySuccessFailed);
					return;
				}
				List<String> nameList = new ArrayList<String>();
				for (DBAttribute att : schemaInfo.getAttributes()) {
					nameList.add(att.getName());
				}
				copyNamesToClipboard(nameList);
				CommonUITool.openInformationBox(
						Messages.tablesDetailInfoPartBtnCopySuccessTitle,
						Messages.tablesDetailInfoPartBtnCopySuccessMsg);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		final NewTableAction newTableAction = (NewTableAction) ActionManager.getInstance().getAction(NewTableAction.ID);
		ToolItem newTableItem = new ToolItem(toolBar, SWT.PUSH);
		newTableItem.setText(newTableAction.getText());
		newTableItem.setImage(CommonUITool.getImage(newTableAction.getImageDescriptor()));
		newTableItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newTableAction.run(database);
			}
		});

		ScrolledComposite scrolledComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComp.setLayout(new FillLayout());
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);
		scrolledComp.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
		topSash = new SashForm(scrolledComp, SWT.VERTICAL);
		topSash.setBackground(ResourceManager.getColor(136, 161, 227));
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		topSash.setLayout(gridLayout);
		topSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		topSash.SASH_WIDTH = 1;
		scrolledComp.setContent(topSash);
		createTablesDetailInfoTable(topSash);

		createTabFolder(topSash);
		topSash.setWeights(new int[]{70, 30 });

		this.setInputs();
	}

	/**
	 * createTablesDetailInfoTable
	 *
	 * @param parent
	 */
	public void createTablesDetailInfoTable(Composite parent) {
		final Composite tableComposite = new Composite(parent, SWT.NONE);
		tableComposite.setLayout(new FillLayout());
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tableListView = new TableViewer(tableComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		tableListView.getTable().setHeaderVisible(true);
		tableListView.getTable().setLinesVisible(true);

		final TableViewerColumn columnTableName = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnTableName.getColumn().setWidth(150);
		columnTableName.getColumn().setText(Messages.tablesDetailInfoPartColTableName);
		columnTableName.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc,
						ColumnViewerSorter.PROPERTY_NAME));
				tableListView.getTable().setSortColumn(columnTableName.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		final TableViewerColumn columnTableDesc = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnTableDesc.getColumn().setWidth(200);
		columnTableDesc.getColumn().setText(
				Messages.tablesDetailInfoPartColTableMemo);
		columnTableDesc.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc,
						ColumnViewerSorter.PROPERTY_MEMO));
				tableListView.getTable().setSortColumn(columnTableDesc.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		final TableViewerColumn columnRecordsCount = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnRecordsCount.getColumn().setWidth(60);
		columnRecordsCount.getColumn().setText(
				Messages.tablesDetailInfoPartColRecordsCount);
		columnRecordsCount.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc,
						ColumnViewerSorter.PROPERTY_RECORD));
				tableListView.getTable().setSortColumn(columnRecordsCount.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		final TableViewerColumn columnColumnsCount = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnColumnsCount.getColumn().setWidth(80);
		columnColumnsCount.getColumn().setText(
				Messages.tablesDetailInfoPartColColumnsCount);
		columnColumnsCount.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc,
						ColumnViewerSorter.PROPERTY_COLUMN));
				tableListView.getTable().setSortColumn(columnColumnsCount.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		final TableViewerColumn columnPK = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnPK.getColumn().setWidth(50);
		columnPK.getColumn().setText(Messages.tablesDetailInfoPartColPK);
		columnPK.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc,
						ColumnViewerSorter.PROPERTY_PK));
				tableListView.getTable().setSortColumn(columnPK.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		final TableViewerColumn columnTableUK = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnTableUK.getColumn().setWidth(50);
		columnTableUK.getColumn().setText(Messages.tablesDetailInfoPartColUK);
		columnTableUK.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc,
						ColumnViewerSorter.PROPERTY_UK));
				tableListView.getTable().setSortColumn(columnTableUK.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		final TableViewerColumn columnFK = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnFK.getColumn().setWidth(50);
		columnFK.getColumn().setText(Messages.tablesDetailInfoPartColFK);
		columnFK.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc,
						ColumnViewerSorter.PROPERTY_FK));
				tableListView.getTable().setSortColumn(columnFK.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		final TableViewerColumn columnIndex = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnIndex.getColumn().setWidth(50);
		columnIndex.getColumn().setText(Messages.tablesDetailInfoPartColIndex);
		columnIndex.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc,
						ColumnViewerSorter.PROPERTY_INDEX));
				tableListView.getTable().setSortColumn(columnIndex.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		final TableViewerColumn columnRecordsSize = new TableViewerColumn(
				tableListView, SWT.LEFT);
		columnRecordsSize.getColumn().setWidth(100);
		columnRecordsSize.getColumn().setText(Messages.tablesDetailInfoPartColTableRecordsSize);
		columnRecordsSize.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean isAsc = false;
			public void widgetSelected(SelectionEvent e) {
				tableListView.setSorter(new ColumnViewerSorter(isAsc, ColumnViewerSorter.PROPERTY_SIZE));
				tableListView.getTable().setSortColumn(columnRecordsSize.getColumn());
				tableListView.getTable().setSortDirection(isAsc ? SWT.UP : SWT.DOWN);
				isAsc = !isAsc;
			}
		});

		tableListView.setContentProvider(new TableDashboardContentProvider());
		tableListView.setLabelProvider(new TableDashboardLabelProvider());
		tableListView.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				TableDetailInfo oneTableDetail = (TableDetailInfo) selection.getFirstElement();
				openTableDetail(oneTableDetail);
			}
		});
		tableListView.getTable().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'c') {
					copySelectedTableNamesToClipboard();
				} else if (event.keyCode == SWT.CR) {
					showEditDialog(tableListView.getTable(), tableListView.getTable().getSelectionIndex());
				}
			}
		});

		registerContextMenu();
	}

	public void showEditDialog(Table table, int index) {
		if (tableListView.getTable().getItemCount() < index) {
			return;
		}

		TableItem item = table.getItem(index);
		Object objData = item.getData();
		if (objData != null && objData instanceof TableDetailInfo) {
			TableDetailInfo info = (TableDetailInfo) objData;
			InputTextDialog dialog = new InputTextDialog(
					Display.getCurrent().getActiveShell(),
					Messages.titleTableDescEditor, Messages.msgTableDescEditor,
					Messages.labelTableDescEditor, info.getTableDesc());
			if (dialog.open() == IDialogConstants.OK_ID) {
				String tableName = info.getTableName();
				String columnName = null;
				String description = dialog.getResult();
				Connection conn = null;
				try {
					conn = JDBCConnectionManager.getConnection(
							database.getDatabaseInfo(), true);
					SchemaCommentHandler.updateDescription(
							database.getDatabaseInfo(), conn, tableName,
							columnName, description);
					info.setTableDesc(description);
					tableListView.setInput(tableList);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					QueryUtil.commit(conn);
					QueryUtil.freeQuery(conn);
				}
			}

			//For bug TOOLS-3324
			//			if (info != null) {
			//				openTableDetail(info);
			//			}
		}
	}

	public void setInputs() {
		tableListView.setInput(tableList);
		tableListView.refresh();
		Connection connection = null;
		try {
			connection = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), true);
			isSchemaCommentInstalled = SchemaCommentHandler.isInstalledMetaTable(
					database.getDatabaseInfo(), connection);
			TableDashboardComposite tableComp = new TableDashboardComposite(tabFolder, SWT.NONE);
			tableComp.initialize();
			if (database.getDatabaseInfo().getUserTableInfoList().size() > 0) {
				ClassInfo classInfo = database.getDatabaseInfo().getUserTableInfoList().get(0);
				SchemaInfo schemaInfo = database.getDatabaseInfo().getSchemaInfo(connection,
						classInfo.getClassName());
				IDatabaseSpec dbSpec = database.getDatabaseInfo();
				if (schemaInfo != null && SchemaCommentHandler.isInstalledMetaTable(dbSpec, connection)) {
					Map<String, SchemaComment> comments = SchemaCommentHandler.loadDescription(
							dbSpec, connection, classInfo.getClassName());
					if (comments != null) {
						SchemaCommentHandler.bindSchemaInfo(comments, schemaInfo);
					}
				}
				tableComp.setInput(schemaInfo, database.getDatabaseInfo(), isSchemaCommentInstalled);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			QueryUtil.freeQuery(connection);
		}

		if (isSchemaCommentInstalled) {
			new TableEditButtonSupport(tableListView, this, 1);
		}
	}

	public void createTabFolder(Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.TOP);
		tabFolder.setLayout(new FillLayout());
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tabFolder.setSimple(false);
		tabFolder.setUnselectedImageVisible(true);
		tabFolder.setUnselectedCloseVisible(true);
		tabFolder.setSelectionBackground(ResourceManager.getColor(136, 161, 227));
		tabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
		Menu menu = new Menu(tabFolder.getShell(), SWT.POP_UP);
		tabFolder.setMenu(menu);

		MenuItem closeItem = new MenuItem(menu, SWT.PUSH);
		closeItem.setText(Messages.tablesDetailInfoPartCloseMenu);
		closeItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CTabItem item = tabFolder.getSelection();
				item.dispose();
			}
		});

		MenuItem closeOthersItem = new MenuItem(menu, SWT.PUSH);
		closeOthersItem.setText(Messages.tablesDetailInfoPartCloseOthersMenu);
		closeOthersItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
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
		closeAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CTabItem[] items = tabFolder.getItems();
				for (CTabItem item : items) {
					item.dispose();
				}
			}
		});

	}

	/**
	 * Refresh all data
	 */
	private void refresh() {
		if (CommonUITool.openConfirmBox(Messages.tablesDetailInfoPartRefreshConfirm)) {
			OpenTablesDetailInfoPartProgress progress = new OpenTablesDetailInfoPartProgress(
					database);
			progress.loadTablesInfo();
			if (progress.isSuccess()) {
				reload(progress.getList());
				tableListView.setInput(tableList);
				tableListView.refresh();
			}
		}
	}

	private void reload(List<TableDetailInfo> tableListAll) {
		if (tableListAll == null) {
			return;
		}
		this.tableList = new ArrayList<TableDetailInfo>();
		for (TableDetailInfo info : tableListAll) {
			if (ConstantsUtil.isExtensionalSystemTable(info.getTableName())) {
				continue;
			}
			tableList.add(info);
		}
	}

	/**
	 * Refresh the data
	 *
	 * @param name
	 */
	private void refresh(String name) {
		final LoadTableDetailInfoTask loadTableDetailInfoTask = new LoadTableDetailInfoTask(
				Messages.tablesDetailInfoLoadingDataTitle, database, name);
		CommonTaskExec taskExec = new CommonTaskExec(Messages.bind(
				Messages.tablesDetailInfoLoadingData, name));
		taskExec.addTask(loadTableDetailInfoTask);
		new ExecTaskWithProgress(taskExec).busyCursorWhile();
		if (taskExec.isSuccess()) {
			TableDetailInfo tableInfo = loadTableDetailInfoTask.getTableInfo();
			if (tableInfo != null) {
				TableDetailInfo oldTableInfo = findTableInfo(name);
				if (oldTableInfo != null) {
					TableDetailInfo.copyAllAttribute(tableInfo, oldTableInfo);
					tableListView.refresh();
				} else {
					tableList.add(tableInfo);
					tableListView.refresh();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		setTitleToolTip(Messages.tablesDetailInfoPartTitle);
		setTitleImage(CommonUIPlugin.getImage("icons/navigator/schema_table.png"));
		this.database = (CubridDatabase) input.getAdapter(CubridDatabase.class);
		List<TableDetailInfo> tableListAll = (List<TableDetailInfo>) input.getAdapter(List.class);
		reload(tableListAll);
		StringBuilder partName = new StringBuilder(Messages.tablesDetailInfoPartTitle);
		partName.append(" [").append(database.getUserName()).append("@").append(
				database.getName()).append(":").append(
				database.getDatabaseInfo().getBrokerIP()).append("]");
		setPartName(partName.toString());
	}

	public void copySelectedTableNamesToClipboard() {
		List<String> nameList = new ArrayList<String>();
		List<Integer> selectIndex = new ArrayList<Integer>();
		for (int i = 0; i < tableListView.getTable().getSelectionIndices().length; i++) {
			selectIndex.add(tableListView.getTable().getSelectionIndices()[i]);
		}
		for (int i = 0; i < selectIndex.size(); i++) {
			TableDetailInfo tableInfo = tableList.get(selectIndex.get(i));
			if (tableInfo == null) {
				continue;
			}
			nameList.add(tableInfo.getTableName());
		}
		copyNamesToClipboard(nameList);
	}

	public void copyNamesToClipboard(List<String> nameList) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final Clipboard cb = new Clipboard(shell.getDisplay());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nameList.size(); i++) {
			if (i != 0) {
				sb.append(StringUtil.NEWLINE);
			}
			sb.append(nameList.get(i));
		}
		TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] transfers = new Transfer[]{ textTransfer };
		Object[] data = new Object[]{ sb.toString() };
		cb.setContents(data, transfers);
		cb.dispose();
	}

	private void initializeAction(final Menu parent, final CopyToClipboardAction action) {
		if (action == null) {
			return;
		}
		MenuItem menuItem = new MenuItem(parent, SWT.PUSH);
		menuItem.setText(action.getText());
		menuItem.setImage(CommonUITool.getImage(action.getImageDescriptor()));
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ICubridNode[] nodes = getAllSelectedNodes();
				if (nodes != null && nodes.length > 0) {
					action.run(nodes);
				}
			}
		});
	}

	private CopyToClipboardAction getMakeQueryAction(String actionName) {
		return (CopyToClipboardAction) ActionManager.getInstance().getAction(actionName);
	}

	private void registerContextMenu() {
		final ActionManager manager = ActionManager.getInstance();
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		tableListView.getTable().addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				manager.changeFocusProvider(
						tableListView.getTable());
			}
		});

		Menu menu = new Menu(shell, SWT.POP_UP);

		if (!Util.isWindows()) {
			// SELECT GROUP
			final Menu makeSelectQueryMenu = new Menu(menu);
			{
				MenuItem subMenuItem = new MenuItem(menu, SWT.CASCADE);
				subMenuItem.setText(com.cubrid.common.ui.spi.Messages.lblMakeSelectQueryGrp);
				subMenuItem.setMenu(makeSelectQueryMenu);
			}
			// SELECT
			initializeAction(makeSelectQueryMenu, getMakeQueryAction(MakeSelectQueryAction.ID));
			// Parameterized SELECT
			initializeAction(makeSelectQueryMenu, getMakeQueryAction(MakeSelectPstmtQueryAction.ID));
			// Parameterized INSERT
			initializeAction(menu, getMakeQueryAction(MakeInsertQueryAction.ID));
			// Parameterized UPDATE
			initializeAction(menu, getMakeQueryAction(MakeUpdateQueryAction.ID));
			// Parameterized DELETE
			initializeAction(menu, getMakeQueryAction(MakeDeleteQueryAction.ID));
			// CREATE
			initializeAction(menu, getMakeQueryAction(MakeCreateQueryAction.ID));

			new MenuItem(menu, SWT.SEPARATOR);
		}
		
		final TableToJavaCodeAction createJavaCodeAction = (TableToJavaCodeAction) manager.getAction(TableToJavaCodeAction.ID);
		if (createJavaCodeAction != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(createJavaCodeAction.getText());
			menuItem.setImage(CommonUITool.getImage(createJavaCodeAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode[] nodes = getAllSelectedNodes();
					if (nodes != null && nodes.length > 0) {
						createJavaCodeAction.run(nodes);
					}
				}
			});
		}

		final TableToPhpCodeAction createPhpCodeAction = (TableToPhpCodeAction) manager.getAction(TableToPhpCodeAction.ID);
		if (createPhpCodeAction != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(createPhpCodeAction.getText());
			menuItem.setImage(CommonUITool.getImage(createPhpCodeAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode[] nodes = getAllSelectedNodes();
					if (nodes != null && nodes.length > 0) {
						createPhpCodeAction.run(nodes);
					}
				}
			});
		}

		final ExportTableDefinitionAction exportTableDefinitionAction = (ExportTableDefinitionAction) manager.getAction(ExportTableDefinitionAction.ID);
		if (exportTableDefinitionAction != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(exportTableDefinitionAction.getText());
			menuItem.setImage(CommonUITool.getImage(exportTableDefinitionAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode[] nodes = getAllSelectedNodes();
					if (nodes != null && nodes.length > 0) {
						exportTableDefinitionAction.run(nodes);
					}
				}
			});
		}

		new MenuItem(menu, SWT.SEPARATOR);

		if (!Util.isWindows()) {

			// View data menu
			final Menu viewDataMenu = new Menu(menu);
			{
				final MenuItem subMenuItem = new MenuItem(menu, SWT.CASCADE);
				subMenuItem.setText(com.cubrid.common.ui.spi.Messages.viewDataMenuName);
				subMenuItem.setMenu(viewDataMenu);
			}

			final TableSelectAllAction selectAllAction = (TableSelectAllAction) manager.getAction(TableSelectAllAction.ID);
			if (selectAllAction != null) {
				MenuItem menuItem = new MenuItem(viewDataMenu, SWT.PUSH);
				menuItem.setText(selectAllAction.getText());
				menuItem.setImage(CommonUITool.getImage(selectAllAction.getImageDescriptor()));
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ICubridNode node = getFirstSelectedNode();
						if (node != null) {
							selectAllAction.run((ISchemaNode) node);
						}
					}
				});
			}

			final SelectByOnePstmtDataAction selectPstmtAction = (SelectByOnePstmtDataAction) manager.getAction(SelectByOnePstmtDataAction.ID);
			if (selectPstmtAction != null) {
				MenuItem menuItem = new MenuItem(viewDataMenu, SWT.PUSH);
				menuItem.setText(selectPstmtAction.getText());
				menuItem.setImage(CommonUITool.getImage(selectPstmtAction.getImageDescriptor()));
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ICubridNode node = getFirstSelectedNode();
						if (node != null) {
							selectPstmtAction.run((ISchemaNode) node);
						}
					}
				});
			}

			final SelectByMultiPstmtDataAction selectMultiPstmtAction = (SelectByMultiPstmtDataAction) manager.getAction(SelectByMultiPstmtDataAction.ID);
			if (selectMultiPstmtAction != null) {
				MenuItem menuItem = new MenuItem(viewDataMenu, SWT.PUSH);
				menuItem.setText(selectMultiPstmtAction.getText());
				menuItem.setImage(CommonUITool.getImage(selectMultiPstmtAction.getImageDescriptor()));
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ICubridNode node = getFirstSelectedNode();
						if (node != null) {
							selectMultiPstmtAction.run((ISchemaNode) node);
						}
					}
				});
			}

			new MenuItem(viewDataMenu, SWT.SEPARATOR);

			final TableSelectCountAction selectCountAction = (TableSelectCountAction) manager.getAction(TableSelectCountAction.ID);
			if (selectCountAction != null) {
				MenuItem menuItem = new MenuItem(viewDataMenu, SWT.PUSH);
				menuItem.setText(selectCountAction.getText());
				menuItem.setImage(CommonUITool.getImage(selectAllAction.getImageDescriptor()));
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ICubridNode node = getFirstSelectedNode();
						if (node != null) {
							selectCountAction.run((ISchemaNode) node);
						}
					}
				});
			}

			// Input data menu
			final Menu inputDataMenu = new Menu(menu);
			{
				MenuItem subMenuItem = new MenuItem(menu, SWT.CASCADE);
				subMenuItem.setText(com.cubrid.common.ui.spi.Messages.inputDataMenuName);
				subMenuItem.setMenu(inputDataMenu);
			}

			final InsertOneByPstmtAction insertStmtAction = (InsertOneByPstmtAction) manager.getAction(InsertOneByPstmtAction.ID);
			if (insertStmtAction != null) {
				MenuItem menuItem = new MenuItem(inputDataMenu, SWT.PUSH);
				menuItem.setText(insertStmtAction.getText());
				menuItem.setImage(CommonUITool.getImage(insertStmtAction.getImageDescriptor()));
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ICubridNode node = getFirstSelectedNode();
						if (node != null) {
							insertStmtAction.run((ISchemaNode) node);
						}
					}
				});
			}

			final ImportDataFromFileAction insertMultiStmtAction = (ImportDataFromFileAction) manager.getAction(ImportDataFromFileAction.ID);
			if (insertMultiStmtAction != null) {
				MenuItem menuItem = new MenuItem(inputDataMenu, SWT.PUSH);
				menuItem.setText(insertMultiStmtAction.getText());
				menuItem.setImage(CommonUITool.getImage(insertMultiStmtAction.getImageDescriptor()));
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ICubridNode node = getFirstSelectedNode();
						if (node != null) {
							insertMultiStmtAction.run((ISchemaNode) node);
						}
					}
				});
			}

			new MenuItem(menu, SWT.SEPARATOR);
		}

		// Export & Import
		final ExportWizardAction exportWizardAction = (ExportWizardAction) manager.getAction(ExportWizardAction.ID);
		if (exportWizardAction != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(exportWizardAction.getText());
			menuItem.setImage(CommonUITool.getImage(exportWizardAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode[] nodes = getAllSelectedNodes();
					if (nodes != null && nodes.length > 0) {
						exportWizardAction.run(nodes);
					}
				}
			});
		}

		final ImportWizardAction importWizardAction = (ImportWizardAction) manager.getAction(ImportWizardAction.ID);
		if (importWizardAction != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(importWizardAction.getText());
			menuItem.setImage(CommonUITool.getImage(importWizardAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode[] nodes = getAllSelectedNodes();
					if (nodes != null && nodes.length > 0) {
						importWizardAction.run(nodes);
					}
				}
			});
		}

		new MenuItem(menu, SWT.SEPARATOR);

		final EditTableAction editTableAction = (EditTableAction) manager.getAction(EditTableAction.ID);
		if (editTableAction != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(editTableAction.getText());
			menuItem.setImage(CommonUITool.getImage(editTableAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode node = getFirstSelectedNode();
					if (node != null) {
						editTableAction.run((ISchemaNode) node);
					}
				}
			});
		}

		final RenameTableAction renameTableAction = (RenameTableAction) manager.getAction(RenameTableAction.ID);
		if (renameTableAction != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(renameTableAction.getText());
			menuItem.setImage(CommonUITool.getImage(renameTableAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode node = getFirstSelectedNode();
					if (node != null) {
						renameTableAction.run(database, (ISchemaNode) node);
					}
				}
			});
		}

		new MenuItem(menu, SWT.SEPARATOR);

		final Menu manageTableMenu = new Menu(menu);
		{
			final MenuItem subMenuItem = new MenuItem(menu, SWT.CASCADE);
			subMenuItem.setText(com.cubrid.common.ui.spi.Messages.tableMoreName);
			subMenuItem.setMenu(manageTableMenu);
		}

		final DeleteTableAction deleteTableAction = (DeleteTableAction) manager.getAction(DeleteTableAction.ID);
		if (deleteTableAction != null) {
			MenuItem menuItem = new MenuItem(manageTableMenu, SWT.PUSH);
			menuItem.setText(deleteTableAction.getText());
			menuItem.setImage(CommonUITool.getImage(deleteTableAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode[] nodes = getAllSelectedNodes();
					if (nodes != null && nodes.length > 0) {
						deleteTableAction.run(nodes);
					}
				}
			});
		}

		final DropTableAction dropTableAction = (DropTableAction) manager.getAction(DropTableAction.ID);
		if (dropTableAction != null) {
			MenuItem menuItem = new MenuItem(manageTableMenu, SWT.PUSH);
			menuItem.setText(dropTableAction.getText());
			menuItem.setImage(CommonUITool.getImage(dropTableAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ICubridNode[] nodes = getAllSelectedNodes();
					if (nodes != null && nodes.length > 0) {
						dropTableAction.run(nodes);
					}
				}
			});
		}

		if (CompatibleUtil.isSupportCreateTableLike(database.getDatabaseInfo())) {
			final CreateLikeTableAction createLikeTableAction = (CreateLikeTableAction) manager.getAction(CreateLikeTableAction.ID);
			if (createLikeTableAction != null) {
				MenuItem menuItem = new MenuItem(manageTableMenu, SWT.PUSH);
				menuItem.setText(createLikeTableAction.getText());
				menuItem.setImage(CommonUITool.getImage(createLikeTableAction.getImageDescriptor()));
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ICubridNode node = getFirstSelectedNode();
						if (node != null) {
							createLikeTableAction.run((ISchemaNode) node);
						}
					}
				});
			}
		}

		new MenuItem(menu, SWT.SEPARATOR);

		final NewTableAction newTableAction = (NewTableAction) manager.getAction(NewTableAction.ID);
		if (newTableAction != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(newTableAction.getText());
			menuItem.setImage(CommonUITool.getImage(newTableAction.getImageDescriptor()));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					newTableAction.run(database);
				}
			});
		}

		new MenuItem(menu, SWT.SEPARATOR);

		final MenuItem refreshItem = new MenuItem(menu, SWT.PUSH);
		refreshItem.setText(Messages.tablesDetailInfoPartRefreshMenu);
		refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
		refreshItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				refresh();
			}
		});

		tableListView.getTable().setMenu(menu);
	}

	/**
	 * Get first selected node
	 *
	 * @return
	 */
	private ICubridNode getFirstSelectedNode() {
		TableItem[] items = tableListView.getTable().getSelection();
		if (items.length > 0) {
			TableItem item = items[0];
			TableDetailInfo tableInfo = (TableDetailInfo) item.getData();
			Set<String> typeSet = new HashSet<String>();
			typeSet.add(NodeType.USER_TABLE);
			typeSet.add(NodeType.USER_PARTITIONED_TABLE);
			ICubridNode tableNode = CommonUITool.findNode(database, typeSet,
					tableInfo.getTableName());
			return tableNode;
		}
		return null;
	}

	/**
	 * Get all selected nodes
	 *
	 * @return
	 */
	private ICubridNode[] getAllSelectedNodes() {
		TableItem[] items = tableListView.getTable().getSelection();
		ICubridNode[] nodes = null;
		if (items.length > 0) {
			List<ICubridNode> nodeList = new ArrayList<ICubridNode>();
			for (TableItem item : items) {
				TableDetailInfo tableInfo = (TableDetailInfo) item.getData();
				Set<String> typeSet = new HashSet<String>();
				typeSet.add(NodeType.USER_TABLE);
				typeSet.add(NodeType.USER_PARTITIONED_TABLE);

				ICubridNode tableNode = CommonUITool.findNode(database,
						typeSet, tableInfo.getTableName());
				if (tableNode != null) {
					nodeList.add(tableNode);
				}
			}
			nodes = new ICubridNode[nodeList.size()];
			return nodeList.toArray(nodes);
		}
		return nodes;
	}

	/**
	 * Perform node change event
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(event.getType())) {
			close(event, database.getServer());
		}

		if (CubridNodeChangedEventType.DATABASE_LOGOUT.equals(event.getType())
				|| CubridNodeChangedEventType.DATABASE_STOP.equals(event.getType())) {
			close(event, database);
		}

		ICubridNode node = event.getCubridNode();
		if (NodeType.USER_TABLE.equals(node.getType())
				|| NodeType.USER_PARTITIONED_TABLE.equals(node.getType())) {
			if (CubridNodeChangedEventType.NODE_ADD.equals(event.getType())
					|| CubridNodeChangedEventType.NODE_REFRESH.equals(event.getType())) {
				refresh(node.getName());
			}

			if (CubridNodeChangedEventType.NODE_REMOVE.equals(event.getType())) {
				TableDetailInfo tableInfo = findTableInfo(node.getName());
				if (tableInfo != null) {
					tableList.remove(tableInfo);
					tableListView.refresh();
					fireTableDetailChanged(tableInfo.getTableName());
				}
			}
			if (CubridNodeChangedEventType.NODE_REFRESH.equals(event.getType())) {
				refresh(node.getName());
				fireTableDetailChanged(node.getName());
			}
		}
	}

	/**
	 * Fire the table info changed to tabFolder
	 *
	 * @param name
	 */
	private void fireTableDetailChanged(String name) {
		if (tabFolder != null && !tabFolder.isDisposed()) {
			CTabItem[] items = tabFolder.getItems();
			for (CTabItem item : items) {
				TablesDetailInfoCTabItem tabItem = (TablesDetailInfoCTabItem) item;
				SchemaInfo schema = tabItem.getTableInfoComposite().getData();
				if (schema != null
						&& StringUtil.isEqualNotIgnoreNull(
								schema.getClassname(), name)) {
					item.dispose();
				}
			}
		}
	}

	/**
	 * Find table info
	 *
	 * @param name
	 * @param type
	 * @return
	 */
	private TableDetailInfo findTableInfo(String name) {
		for (TableDetailInfo tableInfo : tableList) {
			if (tableInfo.getTableName().equals(name)) {
				return tableInfo;
			}
		}
		return null;
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void setFocus() {
	}

	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	private void openTableDetail(TableDetailInfo info) {
		//if had opend, set it selection
		for (CTabItem tabItem : tabFolder.getItems()) {
			if (tabItem.getText().equals(info.getTableName())) {
				tabFolder.setSelection(tabItem);
				return;
			}
		}

		//if a new table info, create a new tab
		TableDashboardComposite tableComp = new TableDashboardComposite(
				tabFolder, SWT.NONE);
		tableComp.initialize();

		SchemaProvider schemaProvider = new SchemaProvider(
				database.getDatabaseInfo(), info.getTableName());
		SchemaInfo schemaInfo = schemaProvider.getSchema();
		if (schemaInfo == null && StringUtil.isNotEmpty(schemaProvider.getErrorMessage())) {
			String msg = Messages.bind(Messages.errGetSchemaInfo, info.getTableName());
			CommonUITool.openErrorBox(msg);
			return;
		}

		// load table descriptions
		Connection conn = null; // FIXME move this logic to core module
		try {
			conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), true);
			IDatabaseSpec dbSpec = database.getDatabaseInfo();
			boolean isSchemaCommentInstalled = SchemaCommentHandler.isInstalledMetaTable(dbSpec, conn);
			if (schemaInfo != null && isSchemaCommentInstalled) {
				Map<String, SchemaComment> comments = SchemaCommentHandler.loadDescription(
						dbSpec, conn, schemaInfo.getClassname());
				SchemaCommentHandler.bindSchemaInfo(comments, schemaInfo);
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			QueryUtil.freeQuery(conn);
		}

		tableComp.setInput(schemaInfo, database.getDatabaseInfo(), isSchemaCommentInstalled);
	}
}

/**
 * Column Viewer Sorter
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-1-6 created by Kevin.Wang
 */
class ColumnViewerSorter extends
		ViewerSorter {
	static final String PROPERTY_NAME = "name";
	static final String PROPERTY_MEMO = "memo";
	static final String PROPERTY_PK = "pk";
	static final String PROPERTY_UK = "uk";
	static final String PROPERTY_FK = "fk";
	static final String PROPERTY_INDEX = "index";
	static final String PROPERTY_SIZE = "size";
	static final String PROPERTY_RECORD = "record";
	static final String PROPERTY_COLUMN = "column";

	private boolean isAsc = false;
	private String property;

	ColumnViewerSorter(boolean isAsc, String property) {
		this.isAsc = isAsc;
		this.property = property;
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		TableDetailInfo t1 = (TableDetailInfo) e1;
		TableDetailInfo t2 = (TableDetailInfo) e2;

		if (PROPERTY_NAME.equals(property)) {
			return compareName(t1, t2);
		} else if (PROPERTY_MEMO.equals(property)) {
			return compareMemo(t1, t2);
		} else if (PROPERTY_RECORD.equals(property)) {
			return compareRecord(t1, t2);
		} else if (PROPERTY_COLUMN.equals(property)) {
			return compareColumn(t1, t2);
		} else if (PROPERTY_PK.equals(property)) {
			return comparePK(t1, t2);
		} else if (PROPERTY_UK.equals(property)) {
			return compareUK(t1, t2);
		} else if (PROPERTY_FK.equals(property)) {
			return compareFK(t1, t2);
		} else if (PROPERTY_INDEX.equals(property)) {
			return compareIndex(t1, t2);
		} else if (PROPERTY_SIZE.equals(property)) {
			return compareSize(t1, t2);
		}

		return super.compare(viewer, e1, e2);
	}

	private int compareRecord(TableDetailInfo t1, TableDetailInfo t2) {
		int compared = new Long(t1.getRecordsCount()).compareTo(t2.getRecordsCount());
		if (isAsc) {
			return compared;
		} else {
			return compared * -1;
		}
	}

	private int compareColumn(TableDetailInfo t1, TableDetailInfo t2) {
		int compared = new Integer(t1.getColumnsCount()).compareTo(t2.getColumnsCount());
		if (isAsc) {
			return compared;
		} else {
			return compared * -1;
		}
	}

	private int compareName(TableDetailInfo t1, TableDetailInfo t2) {
		String name1 = t1.getTableName() == null ? "" : t1.getTableName();
		String name2 = t2.getTableName() == null ? "" : t2.getTableName();
		if (isAsc) {
			return name1.compareTo(name2);
		} else {
			return name2.compareTo(name1);
		}
	}

	private int compareMemo(TableDetailInfo t1, TableDetailInfo t2) {
		String memo1 = t1.getTableDesc() == null ? "" : t1.getTableDesc();
		String memo2 = t2.getTableDesc() == null ? "" : t2.getTableDesc();
		if (isAsc) {
			return memo1.compareTo(memo2);
		} else {
			return memo2.compareTo(memo1);
		}
	}

	private int comparePK(TableDetailInfo t1, TableDetailInfo t2) {
		int compared = new Integer(t1.getPkCount()).compareTo(t2.getPkCount());
		if (isAsc) {
			return compared;
		} else {
			return compared * -1;
		}
	}

	private int compareUK(TableDetailInfo t1, TableDetailInfo t2) {
		int compared = new Integer(t1.getUkCount()).compareTo(t2.getUkCount());
		if (isAsc) {
			return compared;
		} else {
			return compared * -1;
		}
	}

	private int compareFK(TableDetailInfo t1, TableDetailInfo t2) {
		int compared = new Integer(t1.getFkCount()).compareTo(t2.getFkCount());
		if (isAsc) {
			return compared;
		} else {
			return compared * -1;
		}
	}

	private int compareIndex(TableDetailInfo t1, TableDetailInfo t2) {
		int compared = new Integer(t1.getIndexCount()).compareTo(t2.getIndexCount());
		if (isAsc) {
			return compared;
		} else {
			return compared * -1;
		}
	}

	private int compareSize(TableDetailInfo t1, TableDetailInfo t2) {
		BigDecimal value1 = t1.getRecordsSize() == null ? new BigDecimal(0)
				: t1.getRecordsSize();
		BigDecimal value2 = t2.getRecordsSize() == null ? new BigDecimal(0)
				: t2.getRecordsSize();
		if (isAsc) {
			return value1.compareTo(value2);
		} else {
			return value2.compareTo(value1);
		}
	}
}