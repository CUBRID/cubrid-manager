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
package com.cubrid.common.ui.compare.schema.control;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlDatabaseInfoMapper;
import com.cubrid.common.ui.cubrid.database.erwin.WrappedDatabaseInfo;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.OpenTablesDetailInfoPartProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;

/**
 * Table Schema Compare Part (Viewer)
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.05 created by Ray Yin
 */
public class TableSchemaCompareInfoPart extends
		EditorPart {
	public static final String ID = TableSchemaCompareInfoPart.class.getName();
	private static final Logger LOGGER = LogUtil.getLogger(TableSchemaCompareInfoPart.class);

	private TableViewer tablesSchemaCompareTable;
	private SashForm topSash;
	private CTabFolder tabFolder;
	private TableSchemaCompareModel compareModel;
	private CubridDatabase sourceDB;
	private CubridDatabase targetDB;
	private Composite labelComposite;
	private Label labelYellowIcon;
	private Label labelYellowIconMsg;
	private TableSchemaCompareComposite tableComp;
	private boolean compareRealDatabase;

	public void createPartControl(final Composite parent) {
		ScrolledComposite scrolledComp = new ScrolledComposite(
				parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComp.setLayout(new FillLayout());
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);

		topSash = new SashForm(scrolledComp, SWT.VERTICAL);
		topSash.setBackground(ResourceManager.getColor(136, 161, 227));
		{
			GridLayout gridLayout = new GridLayout();
			gridLayout.verticalSpacing = 0;
			gridLayout.marginWidth = 0;
			gridLayout.marginHeight = 0;
			gridLayout.horizontalSpacing = 0;
			topSash.setLayout(gridLayout);
		}
		topSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		topSash.SASH_WIDTH = 1;
		scrolledComp.setContent(topSash);

		showTopButtons();

		Composite tableComposite = new Composite(topSash, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			tableComposite.setLayout(layout);
			createCommonColumnsOnTable(tableComposite);
		}

		labelComposite = new Composite(tableComposite, SWT.NONE);
		{
			GridLayout labelLayout = new GridLayout(6, false);
			labelComposite.setLayout(labelLayout);
			labelComposite.setLayoutData(CommonUITool.createGridData(
					GridData.FILL_HORIZONTAL, 1, 1, -1, 25));
		}

		Label labelRedIcon = new Label(labelComposite, SWT.NONE);
		Image redIcon = CommonUIPlugin.getImage("icons/compare/red_ball.png");
		labelRedIcon.setImage(redIcon);
		Label labelRedIconMsg = new Label(labelComposite, SWT.NONE);
		labelRedIconMsg.setText(Messages.differentSchemaMsg);

		boolean isERwinCompareMode = sourceDB.isVirtual() || targetDB.isVirtual();

		if (!isERwinCompareMode) {
			labelYellowIcon = new Label(labelComposite, SWT.NONE);
			labelYellowIcon.setVisible(false);
			{
				Image yellowIcon = CommonUIPlugin.getImage("icons/compare/yellow_ball.png");
				labelYellowIcon.setImage(yellowIcon);
			}
			labelYellowIconMsg = new Label(labelComposite, SWT.NONE);
			labelYellowIconMsg.setText(Messages.diffferntDataMsg);
			labelYellowIconMsg.setVisible(false);
		}

		Label labelGreenIcon = new Label(labelComposite, SWT.NONE);
		Image greenIcon = CommonUIPlugin.getImage("icons/compare/green_ball.png");
		labelGreenIcon.setImage(greenIcon);
		Label labelGreenIconMsg = new Label(labelComposite, SWT.NONE);
		if (isERwinCompareMode) {
			labelGreenIconMsg.setText(Messages.equalSchemaMsg);
		} else {
			labelGreenIconMsg.setText(Messages.totallyEqualMsg);
		}

		createTabFolder(topSash);
		topSash.setWeights(new int[] { 5, 60, 35 });

		// In order to check whether the database is real or virtual.
		// Virtual database is from a ER-Win xml file.
		if (compareRealDatabase) {
			createDetailColumnsOnTable();
		}

		this.setInputs();
	}

	private void showTopButtons() {
		final Composite buttonsComposite = new Composite(topSash, SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(5, false));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		if (compareRealDatabase) {
			showTopButtonsForRealDatabase(buttonsComposite);
		}

		final Button viewComparisonBtn = new Button(buttonsComposite, SWT.NONE);
		viewComparisonBtn.setText(Messages.viewEntireSchemaComparison);
		viewComparisonBtn.setToolTipText(Messages.aboutViewEntireSchemaComparison);
		viewComparisonBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				final List<String> sourceDBSchema = new ArrayList<String>();
				final List<String> targetDBSchema = new ArrayList<String>();

				viewComparisonBtn.setEnabled(false);
				ITask reportBugTask = new AbstractUITask() {
					public void cancel() {
					}

					public void finish() {
					}

					public boolean isCancel() {
						return false;
					}

					public boolean isSuccess() {
						return true;
					}

					public void execute(IProgressMonitor monitor) {
						Map<String, SchemaInfo> source = compareModel.getSourceSchemas();
						Map<String, SchemaInfo> target = compareModel.getTargetSchemas();
						List<SchemaInfo> commonTables = new LinkedList<SchemaInfo>();
						for (SchemaInfo sourceTable : source.values()) {
							if (target.containsKey(sourceTable.getClassname())) {
								commonTables.add(sourceTable);
							}
						}
						Collections.sort(commonTables);
						String s_schema = getDBSchema(sourceDB, source, commonTables);
						sourceDBSchema.add(s_schema);
						String t_schema = getDBSchema(targetDB, target, commonTables);
						targetDBSchema.add(t_schema);
					}
				};

				TaskExecutor taskExecutor = new CommonTaskExec(
						Messages.loadEntireSchemaComparison);
				taskExecutor.addTask(reportBugTask);
				new ExecTaskWithProgress(taskExecutor).exec();
				if (taskExecutor.isSuccess()) {
					String targetDbName = "";
					if (targetDB.isVirtual()) {
						targetDbName = Messages.targetDatabase;
						if (StringUtil.isNotEmpty(targetDB.getName())) {
							targetDbName += " : " + targetDB.getName();
						}
					} else {
						targetDbName = Messages.targetDatabase + ": "
								+ targetDB.getDatabaseInfo().getBrokerIP() + "@"
								+ targetDB.getName();
					}

					String sourceBrokerIp = sourceDB.getDatabaseInfo().getBrokerIP();
					String sourceDbName = sourceDB.getName();
					showEntireSchemaCompareEditor(Messages.sourceDatabase
							+ ": " + sourceBrokerIp + "@" + sourceDbName,
							Messages.targetDatabase + ": " + targetDbName,
							sourceDBSchema.get(0), targetDBSchema.get(0));
				}
				viewComparisonBtn.setEnabled(true);
			}
		});

		Button copyAlterFromSourceBtn = new Button(buttonsComposite, SWT.NONE);
		copyAlterFromSourceBtn.setText(Messages.copyWholeSchemaAlter + "["
				+ Messages.fromSourceToTargetLabel + "]");
		copyAlterFromSourceBtn.setToolTipText(Messages.aboutCopyAlterSource);
		copyAlterFromSourceBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				copyTableAlterDDL(sourceDB, targetDB, true);
			}
		});

		Button copyAlterFromTargetBtn = new Button(buttonsComposite, SWT.NONE);
		copyAlterFromTargetBtn.setText(Messages.copyWholeSchemaAlter + "["
				+ Messages.fromTargetToSourceLabel + "]");
		copyAlterFromTargetBtn.setToolTipText(Messages.aboutCopyAlterTarget);
		copyAlterFromTargetBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				copyTableAlterDDL(targetDB, sourceDB, false);
			}
		});
	}

	private void showTopButtonsForRealDatabase(Composite composite) {
		final Button countRecordsBtn = new Button(composite, SWT.NONE);
		countRecordsBtn.setText(Messages.viewDetailInfo);
		countRecordsBtn.setToolTipText(Messages.aboutViewDetailInfo);
		countRecordsBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				fetchCountOfRecords();
			}
		});
	}

	public void createCommonColumnsOnTable(Composite parent) {
		final Composite tableComposite = new Composite(parent, SWT.NONE);
		tableComposite.setLayout(new FillLayout());
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tablesSchemaCompareTable = new TableViewer(tableComposite, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		tablesSchemaCompareTable.getTable().setHeaderVisible(true);
		tablesSchemaCompareTable.getTable().setLinesVisible(true);

		final TableViewerColumn statusColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.NONE);
		statusColumn.getColumn().setWidth(20);
		statusColumn.getColumn().setToolTipText(Messages.compareStatusTip);
		statusColumn.getColumn().setResizable(false);
		tablesSchemaCompareTable.setSorter(TableSchemaCompareTableViewerSorter.STATUS_DESC);
		statusColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			public void widgetSelected(SelectionEvent e) {
				tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.STATUS_ASC
						: TableSchemaCompareTableViewerSorter.STATUS_DESC);
				tablesSchemaCompareTable.getTable().setSortColumn(
						statusColumn.getColumn());
				tablesSchemaCompareTable.getTable().setSortDirection(
						asc ? SWT.UP : SWT.DOWN);
				asc = !asc;
			}
		});

		final TableViewerColumn sourceDBColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		sourceDBColumn.getColumn().setWidth(300);
		sourceDBColumn.getColumn().setText(
				"  " + sourceDB.getDatabaseInfo().getBrokerIP() + "@"
						+ sourceDB.getName() + " [" + Messages.sourceDatabase
						+ "]");
		sourceDBColumn.getColumn().setToolTipText(Messages.sourceDatabaseTip);
		sourceDBColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			public void widgetSelected(SelectionEvent e) {
				tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.SOURCE_DB_ASC
						: TableSchemaCompareTableViewerSorter.SOURCE_DB_DESC);
				tablesSchemaCompareTable.getTable().setSortColumn(
						sourceDBColumn.getColumn());
				tablesSchemaCompareTable.getTable().setSortDirection(
						asc ? SWT.UP : SWT.DOWN);
				asc = !asc;
			}
		});

		final TableViewerColumn targetDBColoum = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		targetDBColoum.getColumn().setWidth(300);
		if (targetDB.isVirtual()) {
			String targetDbName = Messages.erwinVirtualTable;
			if (StringUtil.isNotEmpty(targetDB.getName())) {
				targetDbName += " : " + targetDB.getName();
			}
			targetDBColoum.getColumn().setText(targetDbName);
		} else {
			targetDBColoum.getColumn().setText(
					"  " + targetDB.getDatabaseInfo().getBrokerIP() + "@"
							+ targetDB.getName() + " ["
							+ Messages.targetDatabase + "]");
		}
		targetDBColoum.getColumn().setToolTipText(Messages.targetDatabaseTip);
		targetDBColoum.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			public void widgetSelected(SelectionEvent e) {
				tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.TARGET_DB_ASC
						: TableSchemaCompareTableViewerSorter.TARGET_DB_DESC);
				tablesSchemaCompareTable.getTable().setSortColumn(
						targetDBColoum.getColumn());
				tablesSchemaCompareTable.getTable().setSortDirection(
						asc ? SWT.UP : SWT.DOWN);
				asc = !asc;
			}
		});

		tablesSchemaCompareTable.setContentProvider(
				new TableSchemaCompareTableViewerContentProvider());
		tablesSchemaCompareTable.setLabelProvider(
				new TableSchemaCompareDetailTableViewerLabelProvider());

		tablesSchemaCompareTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				TableSchemaCompareModel compSchemaModel = (TableSchemaCompareModel) selection.getFirstElement();
				compSchemaModel.setSourceDB(sourceDB);
				compSchemaModel.setTargetDB(targetDB);

				TableSchema leftTableSchema = (TableSchema) compSchemaModel.getLeft();
				TableSchema rightTableSchema = (TableSchema) compSchemaModel.getRight();
				String tabItemText = leftTableSchema.getName();
				if (StringUtil.isEmpty(leftTableSchema.getName()) || StringUtil.isEmpty(tabItemText)) {
					tabItemText = rightTableSchema.getName();
				}

				//if had opend,set it selection and refresh the contents
				for (CTabItem tabItem : tabFolder.getItems()) {
					if (tabItem.getText().equals(tabItemText)) {
						tableComp.setInput(compSchemaModel);
						tableComp.refreshMergeViewer(tabItemText);
						tabFolder.setSelection(tabItem);
						return;
					}
				}

				tableComp.setInput(compSchemaModel);
				tableComp.initialize();
			}
		});

		registerContextMenu();
	}

	public void createTabFolder(Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.TOP);
		tabFolder.setLayout(new FillLayout());
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tabFolder.setSimple(true);
		tabFolder.setUnselectedImageVisible(true);
		tabFolder.setUnselectedCloseVisible(true);
		tabFolder.setSelectionBackground(ResourceManager.getColor(136, 161, 227));
		tabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));

		Menu contextMenu = new Menu(tabFolder);
		tabFolder.setMenu(contextMenu);
		MenuItem closeItem = new MenuItem(contextMenu, SWT.POP_UP);
		closeItem.setText(Messages.closeAllTabs);
		closeItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				CTabItem[] items =  tabFolder.getItems();
				for (CTabItem item : items) {
					item.dispose();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);

		if (input instanceof TableSchemaCompareModelInputLazy) {
			compareRealDatabase = false;
		} else {
			compareRealDatabase = true;
		}

		this.compareModel = (TableSchemaCompareModel) input.getAdapter(TableSchemaCompareModel.class);
		this.sourceDB = compareModel.getSourceDB();
		this.targetDB = compareModel.getTargetDB();
	}

	public void setInputs() {
		Map<String, List<String>> duplicateNameMap = compareModel.getDuplicateNameMap();
		for (Entry<String, List<String>> entry : duplicateNameMap.entrySet()) {
			List<String> list = entry.getValue();
			if (list.size() == 1) {
				continue;
			}

			for (String model : list) {
				compareModel.getTableCompareList().remove(model);
			}
		}

		tablesSchemaCompareTable.getTable().setData(duplicateNameMap);
		tablesSchemaCompareTable.setInput(compareModel);
		tablesSchemaCompareTable.refresh();
		refreshFolder();
	}

	private void refreshFolder() {
		try {
			CTabItem[] tabItems = tabFolder.getItems();
			for (CTabItem tabItem : tabItems) {
				tabItem.dispose();
			}

			List<TableSchemaCompareModel> list = compareModel.getTableCompareList();
			if (list.size() < 1) {
				return;
			}
			TableSchemaCompareModel compSchemaModel = list.get(0);
			compSchemaModel.setSourceDB(sourceDB);
			compSchemaModel.setTargetDB(targetDB);

			tableComp = new TableSchemaCompareComposite(tabFolder, SWT.NONE);
			tableComp.setInput(compSchemaModel);
			tableComp.initialize();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * register context menu
	 */
	private void registerContextMenu() {
		tablesSchemaCompareTable.getTable().addFocusListener(
				new FocusAdapter() {
					public void focusGained(FocusEvent event) {
						ActionManager.getInstance().changeFocusProvider(
								tablesSchemaCompareTable.getTable());
					}
				});

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		Menu contextMenu = menuManager.createContextMenu(tablesSchemaCompareTable.getTable());
		tablesSchemaCompareTable.getTable().setMenu(contextMenu);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Menu menu = new Menu(shell, SWT.POP_UP);

		final MenuItem tableLeftDDL = new MenuItem(menu, SWT.PUSH);
		tableLeftDDL.setText(Messages.copyTablesLeftDDL);
		tableLeftDDL.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				copyTableAlterDDL(sourceDB, targetDB, true);
			}
		});

		final MenuItem tableRightDDL = new MenuItem(menu, SWT.PUSH);
		tableRightDDL.setText(Messages.copyTablesRightDDL);
		tableRightDDL.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				copyTableAlterDDL(targetDB, sourceDB, false);
			}
		});

		tablesSchemaCompareTable.getTable().setMenu(menu);
	}

	/**
	 * copyTableLeftAlterDDL
	 */
	private void copyTableAlterDDL(CubridDatabase leftDB,
			CubridDatabase rightDB, boolean leftToRight) { // FIXME logic code move to core module
		StringBuffer clipboardDataString = new StringBuffer();
		TableItem[] tableItems = tablesSchemaCompareTable.getTable().getSelection();

		String title = "";
		String msg = "";

		for (int i = 0; i < tableItems.length; i++) {
			if (i > 0) {
				clipboardDataString.append(StringUtil.NEWLINE);
				clipboardDataString.append(StringUtil.NEWLINE);
			}

			TableSchemaCompareModel compareModel = (TableSchemaCompareModel) tableItems[i].getData();

			TableSchema leftTableSchema = (TableSchema) compareModel.getLeft();
			TableSchema rightTableSchema = (TableSchema) compareModel.getRight();
			String tableCompare = leftTableSchema.getName();
			if (StringUtil.isEmpty(leftTableSchema.getName())) {
				tableCompare = rightTableSchema.getName();
			}

			String alterDDL = null;
			if (compareModel.getCompareStatus() != TableSchemaCompareModel.SCHEMA_EQUAL
					&& compareModel.getCompareStatus() != TableSchemaCompareModel.RECORDS_DIFF) {

				Map<String, SchemaInfo> sourceSchemas = compareModel.getSourceSchemas();
				Map<String, SchemaInfo> targetSchemas = compareModel.getTargetSchemas();

				SchemaInfo sourceTableSchemaInfo = null;
				SchemaInfo targetTableSchemaInfo = null;
				if (leftToRight) {
					sourceTableSchemaInfo = sourceSchemas.get(tableCompare);
					targetTableSchemaInfo = targetSchemas.get(tableCompare);
				} else {
					targetTableSchemaInfo = sourceSchemas.get(tableCompare);
					sourceTableSchemaInfo = targetSchemas.get(tableCompare);
				}

				alterDDL = tableComp.getTableAlterScript(leftDB, rightDB,
						tableCompare, sourceTableSchemaInfo,
						targetTableSchemaInfo);
				if (StringUtil.isNotEmpty(alterDDL)) {
					clipboardDataString.append(alterDDL.trim());
				}
			}
		}

		Clipboard clipboard = CommonUITool.getClipboard();
		if (clipboardDataString.length() != 0) {
			if ("--NotSupportAlterAutoIncrement".equals(clipboardDataString.toString())) {
				title = Messages.compareStatusTip;
				msg = Messages.alterAutoIncrementNotSupport;
			} else {
				TextTransfer textTransfer = TextTransfer.getInstance();
				Transfer[] transfers = new Transfer[] { textTransfer };
				Object[] data = new Object[] { clipboardDataString.toString() };
				clipboard.setContents(data, transfers);
				title = Messages.alterScript;
				msg = Messages.tableSchemaAlterCopyMessage;
			}
		} else {
			clipboard.clearContents();
			title = Messages.emptyAlterScript;
			msg = Messages.schemaIdenticalMessage;
		}

		CommonUITool.openInformationBox(title, msg);
	}

	private String getDBSchema(CubridDatabase db, Map<String, SchemaInfo> schemaInfos,
			List<SchemaInfo> commonTables) { // FIXME logic code move to core module
		if (schemaInfos == null) {
			return "";
		}

		Set<String> commonNames = new HashSet<String>();
		for (SchemaInfo table : commonTables) {
			commonNames.add(table.getClassname());
		}
		StringBuilder buf = new StringBuilder();

		if (!db.isVirtual()) {
			SchemaDDL schemaDDL = new SchemaDDL(null, db.getDatabaseInfo());
			List<TableDetailInfo> tableList = getTableInfoList(db);
			Collections.sort(tableList);
			try {
				for (SchemaInfo schemaInfo : commonTables) {
					addSchemaDDL(buf, schemaDDL, schemaInfo, true, false);
				}
				for (TableDetailInfo table : tableList) {
					if (commonNames.contains(table.getTableName())) {
						continue;
					}
					SchemaInfo schemaInfo = schemaInfos.get(table.getTableName());
					addSchemaDDL(buf, schemaDDL, schemaInfo, true, false);
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else {
			WrappedDatabaseInfo info = ERXmlDatabaseInfoMapper.getWrappedDatabaseInfo(db.getDatabaseInfo());
			SchemaDDL schemaDDL = new SchemaDDL(null, info);
			List<SchemaInfo> tables = Arrays.asList(schemaInfos.values().toArray(
					new SchemaInfo[0]));
			Collections.sort(tables);
			for (SchemaInfo schemaInfo : commonTables) {
				addSchemaDDL(buf, schemaDDL, schemaInfo, true, true);
			}
			for (SchemaInfo si : tables) {
				if (commonNames.contains(si.getClassname())) {
					continue;
				}
				addSchemaDDL(buf, schemaDDL, si, true, true);
			}
		}

		return buf.toString();
	}

	private void addSchemaDDL(StringBuilder buf, SchemaDDL schemaDDL, SchemaInfo schemaInfo,
			boolean isContainIndex, boolean isVirtual) { // FIXME logic code move to core module
		if (!schemaInfo.isSystemClass()) {
			buf.append(schemaDDL.getSchemaDDL(schemaInfo, isContainIndex, isVirtual));
			buf.append(StringUtil.NEWLINE);
		}
	}

	/**
	 * Display entire schemas comparison
	 */
	private void showEntireSchemaCompareEditor(String leftDatabase,
			String rightDatabase, final String leftContent,
			final String rightContent) {
		CompareConfiguration config = new CompareConfiguration();
		config.setProperty(CompareConfiguration.SHOW_PSEUDO_CONFLICTS, Boolean.FALSE);
		config.setProperty(CompareConfiguration.IGNORE_WHITESPACE, Boolean.TRUE);
		config.setLeftEditable(false);
		config.setLeftLabel(leftDatabase);
		config.setRightEditable(false);
		config.setRightLabel(rightDatabase);

		CompareEditorInput editorInput = new CompareEditorInput(config) {
			protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				return new DiffNode(null, Differencer.CHANGE, null,
						new TextCompareInput(leftContent),
						new TextCompareInput(rightContent));
			}

			public void saveChanges(IProgressMonitor pm) throws CoreException {
				super.saveChanges(pm);
			}
		};
		editorInput.setTitle(Messages.entireDbSchemaComparison);
		CompareUI.openCompareEditor(editorInput);
	}

	private void fetchCountOfRecords() { // FIXME logic code move to core module
		final Set<String> selectedItemKeys = new HashSet<String>();
		TableItem[] items = tablesSchemaCompareTable.getTable().getSelection();
		for (TableItem item : items) {
			if (item == null) {
				continue;
			}

			String src = item.getText(1).equals(Messages.statusMissing) ? "" : item.getText(1);
			String dst = item.getText(2).equals(Messages.statusMissing) ? "" : item.getText(2);
			String key = src + "$" + dst;
			selectedItemKeys.add(key);
		}

		if (selectedItemKeys.size() == 0) {
			CommonUITool.openWarningBox(Messages.warnNotSelectedTables);
			return;
		}

		String msg = Messages.tablesDetailCompareBtnEsitmateRecordAlert;
		if (!CommonUITool.openConfirmBox(msg)) {
			return;
		}

		try {
			new ProgressMonitorDialog(this.getSite().getShell()).run(true, true,
					new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) {
							fetchRecordCountProcess(monitor, selectedItemKeys);
						}
					});
		} catch (Exception e) {
			LOGGER.debug("", e);
		}

		tablesSchemaCompareTable.refresh();
	}

	private void fetchRecordCountProcess(IProgressMonitor monitor,
			Set<String> selectedItemKeys) { // FIXME logic code move to core module
		if (compareModel == null) {
			return;
		}

		List<TableSchemaCompareModel> compareList = compareModel.getTableCompareList();
		if (compareList == null) {
			return;
		}

		int total = compareList.size();
		monitor.beginTask(Messages.loadDetailInfo, total);

		for (TableSchemaCompareModel compareModel : compareList) {
			monitor.worked(1);

			if (monitor.isCanceled()) {
				break;
			}

			TableDetailInfo tableInfo1 = compareModel.getSourceTableDetailInfo();
			TableDetailInfo tableInfo2 = compareModel.getTargetTableDetailInfo();

			// It should be collected only selected list on comparision table;
			String srcTableName = "";
			if (tableInfo1 != null) {
				srcTableName = StringUtil.nvl(tableInfo1.getTableName());
			}
			String dstTableName = "";
			if (tableInfo2 != null) {
				dstTableName = StringUtil.nvl(tableInfo2.getTableName());
			}
			String key = srcTableName + "$" + dstTableName;
			if (!selectedItemKeys.contains(key)) {
				continue;
			}

			if (tableInfo1 != null) {
				SchemaInfo schemaInfo = compareModel.getSourceSchemas().get(
						tableInfo1.getTableName());
				if (schemaInfo != null) {
					long counts = countTableRecords(
							sourceDB.getDatabaseInfo(),
							tableInfo1.getTableName());
					tableInfo1.setRecordsCount(counts);
				}
			}

			if (monitor.isCanceled()) {
				break;
			}

			if (tableInfo2 != null) {
				SchemaInfo schemaInfo = compareModel.getTargetSchemas().get(
						tableInfo2.getTableName());
				if (schemaInfo != null) {
					long counts = countTableRecords(
							targetDB.getDatabaseInfo(),
							tableInfo2.getTableName());
					tableInfo2.setRecordsCount(counts);
				}
			}
		}

		monitor.done();
	}

	/**
	 * Display detailed table information includes records count, attributes
	 * count, indexes count, PK status
	 */
	private void createDetailColumnsOnTable() {
		final Table table = tablesSchemaCompareTable.getTable();
		if (labelYellowIcon != null) {
			labelYellowIcon.setVisible(true);
		}

		if (labelYellowIconMsg != null) {
			labelYellowIconMsg.setVisible(true);
		}

		final TableViewerColumn sourceRecordCountColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		sourceRecordCountColumn.setLabelProvider(new TableSchemaCompareExtraColumnLabelProvider(
				TableSchemaCompareExtraColumnLabelProvider.RECORDS_COUNT, 0));

		final TableViewerColumn targetRecordCountColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		targetRecordCountColumn.setLabelProvider(new TableSchemaCompareExtraColumnLabelProvider(
				TableSchemaCompareExtraColumnLabelProvider.RECORDS_COUNT, 1));

		final TableViewerColumn sourceAttrCountColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		sourceAttrCountColumn.setLabelProvider(new TableSchemaCompareExtraColumnLabelProvider(
				TableSchemaCompareExtraColumnLabelProvider.ATTRIBUTES_COUNT, 0));

		final TableViewerColumn targetAttrCountColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		targetAttrCountColumn.setLabelProvider(new TableSchemaCompareExtraColumnLabelProvider(
				TableSchemaCompareExtraColumnLabelProvider.ATTRIBUTES_COUNT, 1));

		final TableViewerColumn sourceIndexCountColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		sourceIndexCountColumn.setLabelProvider(new TableSchemaCompareExtraColumnLabelProvider(
				TableSchemaCompareExtraColumnLabelProvider.INDEX_COUNT, 0));

		final TableViewerColumn targetIndexCountColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		targetIndexCountColumn.setLabelProvider(new TableSchemaCompareExtraColumnLabelProvider(
				TableSchemaCompareExtraColumnLabelProvider.INDEX_COUNT, 1));

		final TableViewerColumn sourcePKColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		sourcePKColumn.setLabelProvider(new TableSchemaCompareExtraColumnLabelProvider(
				TableSchemaCompareExtraColumnLabelProvider.PK_STATUS, 0));

		final TableViewerColumn targetPKColumn = new TableViewerColumn(
				tablesSchemaCompareTable, SWT.LEFT);
		targetPKColumn.setLabelProvider(new TableSchemaCompareExtraColumnLabelProvider(
				TableSchemaCompareExtraColumnLabelProvider.PK_STATUS, 1));

		tablesSchemaCompareTable.refresh();

		sourceRecordCountColumn.getColumn().setWidth(75);
		sourceRecordCountColumn.getColumn().setText(Messages.recordsCountSource);
		sourceRecordCountColumn.getColumn().setToolTipText(Messages.recordsCountSourceTip);
		sourceRecordCountColumn.getColumn().addSelectionListener(
				new SelectionAdapter() {
					boolean asc = true;

					public void widgetSelected(SelectionEvent e) {
						tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.SOURCE_RECORDS_ASC
								: TableSchemaCompareTableViewerSorter.SOURCE_RECORDS_DESC);
						tablesSchemaCompareTable.getTable().setSortColumn(
								sourceRecordCountColumn.getColumn());
						tablesSchemaCompareTable.getTable().setSortDirection(
								asc ? SWT.UP : SWT.DOWN);
						asc = !asc;
					}
				});

		targetRecordCountColumn.getColumn().setWidth(75);
		targetRecordCountColumn.getColumn().setText(Messages.recordsCountTarget);
		targetRecordCountColumn.getColumn().setToolTipText(Messages.recordsCountTargetTip);
		targetRecordCountColumn.getColumn().addSelectionListener(
				new SelectionAdapter() {
					boolean asc = true;

					public void widgetSelected(SelectionEvent e) {
						tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.TARGET_RECORDS_ASC
								: TableSchemaCompareTableViewerSorter.TARGET_RECORDS_DESC);
						tablesSchemaCompareTable.getTable().setSortColumn(
								targetRecordCountColumn.getColumn());
						tablesSchemaCompareTable.getTable().setSortDirection(
								asc ? SWT.UP : SWT.DOWN);
						asc = !asc;
					}
				});

		sourceAttrCountColumn.getColumn().setWidth(75);
		sourceAttrCountColumn.getColumn().setText(Messages.attrCountSource);
		sourceAttrCountColumn.getColumn().setToolTipText(Messages.attrCountSourceTip);
		sourceAttrCountColumn.getColumn().addSelectionListener(
				new SelectionAdapter() {
					boolean asc = true;

					public void widgetSelected(SelectionEvent e) {
						tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.SOURCE_ATTRS_ASC
								: TableSchemaCompareTableViewerSorter.SOURCE_ATTRS_DESC);
						tablesSchemaCompareTable.getTable().setSortColumn(
								sourceAttrCountColumn.getColumn());
						tablesSchemaCompareTable.getTable().setSortDirection(
								asc ? SWT.UP : SWT.DOWN);
						asc = !asc;
					}
				});

		targetAttrCountColumn.getColumn().setWidth(75);
		targetAttrCountColumn.getColumn().setText(Messages.attrCountTarget);
		targetAttrCountColumn.getColumn().setToolTipText(Messages.attrCountTargetTip);
		targetAttrCountColumn.getColumn().addSelectionListener(
				new SelectionAdapter() {
					boolean asc = true;

					public void widgetSelected(SelectionEvent e) {
						tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.TARGET_ATTRS_ASC
								: TableSchemaCompareTableViewerSorter.TARGET_ATTRS_DESC);
						tablesSchemaCompareTable.getTable().setSortColumn(
								targetAttrCountColumn.getColumn());
						tablesSchemaCompareTable.getTable().setSortDirection(
								asc ? SWT.UP : SWT.DOWN);
						asc = !asc;
					}
				});

		sourceIndexCountColumn.getColumn().setWidth(75);
		sourceIndexCountColumn.getColumn().setText(Messages.indexCountSource);
		sourceIndexCountColumn.getColumn().setToolTipText(Messages.indexCountSourceTip);
		sourceIndexCountColumn.getColumn().addSelectionListener(
				new SelectionAdapter() {
					boolean asc = true;

					public void widgetSelected(SelectionEvent e) {
						tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.SOURCE_INDEX_ASC
								: TableSchemaCompareTableViewerSorter.SOURCE_INDEX_DESC);
						tablesSchemaCompareTable.getTable().setSortColumn(
								sourceIndexCountColumn.getColumn());
						tablesSchemaCompareTable.getTable().setSortDirection(
								asc ? SWT.UP : SWT.DOWN);
						asc = !asc;
					}
				});

		targetIndexCountColumn.getColumn().setWidth(75);
		targetIndexCountColumn.getColumn().setText(Messages.indexCountTarget);
		targetIndexCountColumn.getColumn().setToolTipText(Messages.indexCountTargetTip);
		targetIndexCountColumn.getColumn().addSelectionListener(
				new SelectionAdapter() {
					boolean asc = true;

					public void widgetSelected(SelectionEvent e) {
						tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.TARGET_INDEX_ASC
								: TableSchemaCompareTableViewerSorter.TARGET_INDEX_DESC);
						tablesSchemaCompareTable.getTable().setSortColumn(
								targetIndexCountColumn.getColumn());
						tablesSchemaCompareTable.getTable().setSortDirection(
								asc ? SWT.UP : SWT.DOWN);
						asc = !asc;
					}
				});

		sourcePKColumn.getColumn().setWidth(75);
		sourcePKColumn.getColumn().setText(Messages.pkStatusSource);
		sourcePKColumn.getColumn().setToolTipText(Messages.pkStatusSourceTip);
		sourcePKColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean asc = true;

			public void widgetSelected(SelectionEvent e) {
				tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.SOURCE_PK_ASC
						: TableSchemaCompareTableViewerSorter.SOURCE_PK_DESC);
				tablesSchemaCompareTable.getTable().setSortColumn(
						sourcePKColumn.getColumn());
				tablesSchemaCompareTable.getTable().setSortDirection(
						asc ? SWT.UP : SWT.DOWN);
				asc = !asc;
			}
		});

		targetPKColumn.getColumn().setWidth(75);
		targetPKColumn.getColumn().setText(Messages.pkStatusTarget);
		targetPKColumn.getColumn().setToolTipText(Messages.pkStatusTargetTip);
		targetPKColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			boolean asc = true;

			public void widgetSelected(SelectionEvent e) {
				tablesSchemaCompareTable.setSorter(asc ? TableSchemaCompareTableViewerSorter.TARGET_PK_ASC
						: TableSchemaCompareTableViewerSorter.TARGET_PK_DESC);
				tablesSchemaCompareTable.getTable().setSortColumn(
						targetPKColumn.getColumn());
				tablesSchemaCompareTable.getTable().setSortDirection(
						asc ? SWT.UP : SWT.DOWN);
				asc = !asc;
			}
		});

		TableColumn tableColumn = table.getColumn(0);
		tableColumn.setText("");
		tableColumn.setWidth(20);
		tableColumn.setResizable(false);
		tableColumn = table.getColumn(1);
		tableColumn.setWidth(265);
		tableColumn = table.getColumn(2);
		tableColumn.setWidth(265);

		tablesSchemaCompareTable.refresh();
	}

	/**
	 * Returns data records count of a table
	 *
	 * @param tableName
	 * @return
	 */
	private long countTableRecords(DatabaseInfo dbInfo, String tableName) { // FIXME logic code move to core module
		long tableRecordCount = 0;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = JDBCConnectionManager.getConnection(dbInfo, true);

			String escapedTableName = QuerySyntax.escapeKeyword(tableName);
			String sql = "SELECT COUNT(*) FROM " + escapedTableName;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				tableRecordCount = rs.getLong(1);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
			tableRecordCount = 0;
		} finally {
			QueryUtil.freeQuery(conn, stmt, rs);
		}

		return tableRecordCount;
	}

	/**
	 * Returns all tables detail of a database
	 *
	 * @param db
	 * @return
	 */
	public List<TableDetailInfo> getTableInfoList(CubridDatabase db) { // FIXME logic code move to core module
		OpenTablesDetailInfoPartProgress progress = new OpenTablesDetailInfoPartProgress(db);
		List<TableDetailInfo> tableList = new ArrayList<TableDetailInfo>();
		Map<String, TableDetailInfo> map = new HashMap<String, TableDetailInfo>();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = JDBCConnectionManager.getConnection(db.getDatabaseInfo(), true);

			if (!progress.loadUserSchemaList(conn, map)) {
				return tableList;
			}

			Set<String> tableNameSet = map.keySet();
			if (tableNameSet == null) {
				return tableList;
			}

			List<String> tableNames = new ArrayList<String>();
			for (String tableName : tableNameSet) {
				tableNames.add(tableName);
			}
			Collections.sort(tableNames);

			List<String> partitionClasses = new ArrayList<String>();
			for (String tableName : tableNames) {
				TableDetailInfo info = map.get(tableName);
				if ("YES".equals(info.getPartitioned())) {
					String sql = "SELECT b.* FROM db_partition a, db_class b "
							+ "WHERE a.class_name='"
							+ tableName.toLowerCase(Locale.getDefault())
							+ "' AND LOWER(b.class_name)=LOWER(a.partition_class_name)";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					while (rs.next()) {
						String className = rs.getString("class_name");
						partitionClasses.add(className);
					}

					QueryUtil.freeQuery(stmt, rs);
				}

				if (info.getClassType().equals("CLASS") && !partitionClasses.contains(tableName)) {
					info.setRecordsCount(-1);
					tableList.add(info);
				}
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(conn, stmt, rs);
		}

		return tableList;
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
}
