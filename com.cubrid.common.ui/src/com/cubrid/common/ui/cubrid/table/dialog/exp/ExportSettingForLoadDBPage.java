/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog.exp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.dialog.FilterTreeContentProvider;
import com.cubrid.common.ui.common.navigator.ExportObjectLabelProvider;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * The ExportSettingForLoadDBPage
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-9-6 created by Kevin.Wang
 */
public class ExportSettingForLoadDBPage extends
		ExportWizardPage {

	public final static String PAGE_NAME = ExportSettingForLoadDBPage.class.getName();
	private static final Logger LOGGER = LogUtil.getLogger(ExportSettingForLoadDBPage.class);

	private CheckboxTreeViewer ctv;

	private Button schemaButton;
	private Text schemaPathText;
	private Button schemaBrowseButton;

	private Button indexButton;
	private Text indexPathText;
	private Button indexBrowseButton;

	private Button triggerButton;
	private Text triggerPathText;
	private Button triggerBrowseButton;

	private Button dataButton;
	private Text dataPathText;
	private Button dataBrowseButton;

	private Combo dbCharsetCombo;
	private Combo fileCharsetCombo;
	private boolean isFirstVisible = true;

	private Button startValueButton;

	private List<ICubridNode> tablesOrViewLst;

	/**
	 * The constructor
	 *
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected ExportSettingForLoadDBPage() {
		super(PAGE_NAME, Messages.exportShellTitle, null);
		super.setTitle(Messages.titleExportStep2);
	}

	/**
	 * Create the page content
	 *
	 * @param parent Composite
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());
		Composite leftComposite = new Composite(container, SWT.NONE);
		leftComposite.setLayout(new GridLayout());
		FormData leftData = new FormData();
		leftData.top = new FormAttachment(0, 5);
		leftData.bottom = new FormAttachment(100, 0);
		leftData.left = new FormAttachment(0, 5);
		leftData.right = new FormAttachment(45, 0);
		leftComposite.setLayoutData(leftData);

		Composite rightComposite = new Composite(container, SWT.NONE);
		FormData rightData = new FormData();
		rightData.top = new FormAttachment(0, 5);
		rightData.bottom = new FormAttachment(100, 0);
		rightData.left = new FormAttachment(45, 0);
		rightData.right = new FormAttachment(100, -5);
		rightComposite.setLayoutData(rightData);
		GridLayout rightCompositeLayout = new GridLayout();
		rightCompositeLayout.verticalSpacing = 10;
		rightComposite.setLayout(rightCompositeLayout);

		Label tableInfoLabel = new Label(leftComposite, SWT.None);
		tableInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		tableInfoLabel.setText(Messages.exportWizardSourceTableLable);

		ctv = new CheckboxTreeViewer(leftComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER | SWT.FULL_SELECTION);
		ctv.getTree().setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
		ctv.setContentProvider(new FilterTreeContentProvider());

		ctv.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateDialogStatus();
			}
		});

		final TreeViewerColumn dbObjectCol = new TreeViewerColumn(ctv, SWT.NONE);
		dbObjectCol.setLabelProvider(new ExportObjectLabelProvider());

		final TreeViewerColumn whereCnd = new TreeViewerColumn(ctv, SWT.NONE);
		whereCnd.setLabelProvider(new ExportObjectLabelProvider());
		whereCnd.setEditingSupport(new EditingSupport(ctv) {
			TextCellEditor textCellEditor;

			protected boolean canEdit(Object element) {
				if (element instanceof ICubridNode) {
					ICubridNode node = (ICubridNode) element;
					if (node.getType() == NodeType.TABLE_COLUMN_FOLDER) {
						return true;
					}
				}
				return false;
			}

			protected CellEditor getCellEditor(Object element) {
				if (textCellEditor == null) {
					textCellEditor = new TextCellEditor(ctv.getTree());
				}
				return textCellEditor;
			}

			protected Object getValue(Object element) {
				final ICubridNode node = (ICubridNode) element;
				String condition = (String) node.getData(ExportObjectLabelProvider.CONDITION);
				if (condition == null) {
					return "";
				} else {
					return condition;
				}
			}

			protected void setValue(Object element, Object value) {
				final ICubridNode node = (ICubridNode) element;
				node.setData(ExportObjectLabelProvider.CONDITION, value);
				ctv.refresh();
			}
		});

		dbObjectCol.getColumn().setWidth(160);
		dbObjectCol.getColumn().setText(Messages.tableLabel);
		whereCnd.getColumn().setWidth(120);
		whereCnd.getColumn().setText(Messages.conditionLabel);

		final Button selectAllBtn = new Button(leftComposite, SWT.CHECK);
		{
			selectAllBtn.setText(Messages.btnSelectAll);
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalIndent = 0;
			gridData.horizontalSpan = 3;
			selectAllBtn.setLayoutData(gridData);
		}
		selectAllBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				boolean selection = selectAllBtn.getSelection();
				for (ICubridNode node : tablesOrViewLst) {
					ctv.setGrayed(node, false);
					ctv.setChecked(node, selection);
				}
				updateDialogStatus();
			}
		});

		Group fileOptionGroup = new Group(rightComposite, SWT.None);
		fileOptionGroup.setText(Messages.exportWizardWhereExport);
		fileOptionGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1,
				-1, -1));
		fileOptionGroup.setLayout(new GridLayout(3, false));

		schemaButton = new Button(fileOptionGroup, SWT.CHECK);
		schemaButton.setText("Schema");
		schemaButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		schemaButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (schemaButton.getSelection()) {
					schemaPathText.setEnabled(true);
					schemaBrowseButton.setEnabled(true);
					startValueButton.setEnabled(true);
				} else {
					schemaPathText.setEnabled(false);
					schemaBrowseButton.setEnabled(false);
					startValueButton.setEnabled(false);
				}
				updateDialogStatus();
			}
		});

		schemaPathText = new Text(fileOptionGroup, SWT.BORDER);
		schemaPathText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1,
				-1, -1));
		schemaPathText.setEnabled(true);
		schemaPathText.setEditable(false);
		schemaPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				updateDialogStatus();
			}
		});

		schemaBrowseButton = new Button(fileOptionGroup, SWT.None);
		schemaBrowseButton.setText(Messages.btnBrowse);
		schemaBrowseButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		schemaBrowseButton.setEnabled(true);
		schemaBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DatabaseInfo databaseInfo = getDatabase().getDatabaseInfo();
				String databaseName = databaseInfo.getDbName();
				String fileNameForLoaddbSchema = databaseName + "_schema";
				File savedFile = TableUtil.getSavedFile(getShell(), new String[]{"*.*" },
						new String[]{"All Files" }, fileNameForLoaddbSchema, null, null);
				if (savedFile != null) {
					schemaPathText.setText(savedFile.getAbsolutePath());
				}
				updateDialogStatus();
			}
		});

		indexButton = new Button(fileOptionGroup, SWT.CHECK);
		indexButton.setText("Index");
		indexButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		indexButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (indexButton.getSelection()) {
					indexPathText.setEnabled(true);
					indexBrowseButton.setEnabled(true);
				} else {
					indexPathText.setEnabled(false);
					indexBrowseButton.setEnabled(false);
				}
				updateDialogStatus();
			}
		});

		indexPathText = new Text(fileOptionGroup, SWT.BORDER);
		indexPathText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1,
				-1));
		indexPathText.setEnabled(true);
		indexPathText.setEditable(false);
		indexPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				updateDialogStatus();
			}
		});

		indexBrowseButton = new Button(fileOptionGroup, SWT.None);
		indexBrowseButton.setText(Messages.btnBrowse);
		indexBrowseButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		indexBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DatabaseInfo databaseInfo = getDatabase().getDatabaseInfo();
				String databaseName = databaseInfo.getDbName();
				String fileNameForLoaddbIndex = databaseName + "_indexes";
				File savedFile = TableUtil.getSavedFile(getShell(), new String[]{"*.*" },
						new String[]{"All Files" }, fileNameForLoaddbIndex, null, null);
				if (savedFile != null) {
					indexPathText.setText(savedFile.getAbsolutePath());
				}
				updateDialogStatus();
			}
		});

		triggerButton = new Button(fileOptionGroup, SWT.CHECK);
		triggerButton.setText("Trigger");
		triggerButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		triggerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (triggerButton.getSelection()) {
					triggerPathText.setEnabled(true);
					triggerBrowseButton.setEnabled(true);
				} else {
					triggerPathText.setEnabled(false);
					triggerBrowseButton.setEnabled(false);
				}
				updateDialogStatus();	// TODO update for trigger
			}
		});

		triggerPathText = new Text(fileOptionGroup, SWT.BORDER);
		triggerPathText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1,
				-1));
		triggerPathText.setEnabled(true);
		triggerPathText.setEditable(false);
		triggerPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				updateDialogStatus();
			}
		});

		triggerBrowseButton = new Button(fileOptionGroup, SWT.None);
		triggerBrowseButton.setText(Messages.btnBrowse);
		triggerBrowseButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		triggerBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DatabaseInfo databaseInfo = getDatabase().getDatabaseInfo();
				String databaseName = databaseInfo.getDbName();
				String fileNameForLoaddbTrigger = databaseName + "_triggers";
				File savedFile = TableUtil.getSavedFile(getShell(), new String[]{"*.*" },
						new String[]{"All Files" }, fileNameForLoaddbTrigger, null, null);
				if (savedFile != null) {
					triggerPathText.setText(savedFile.getAbsolutePath());
				}
				updateDialogStatus();
			}
		});

		dataButton = new Button(fileOptionGroup, SWT.CHECK);
		dataButton.setText("Data");
		dataButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dataButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (dataButton.getSelection()) {
					dataPathText.setEnabled(true);
					dataBrowseButton.setEnabled(true);
				} else {
					dataPathText.setEnabled(false);
					dataBrowseButton.setEnabled(false);
				}
				updateDialogStatus();
			}
		});

		dataPathText = new Text(fileOptionGroup, SWT.BORDER);
		dataPathText.setEditable(false);
		dataPathText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1,
				-1));
		dataPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				updateDialogStatus();
			}
		});

		dataBrowseButton = new Button(fileOptionGroup, SWT.None);
		dataBrowseButton.setText(Messages.btnBrowse);
		dataBrowseButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dataBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DatabaseInfo databaseInfo = getDatabase().getDatabaseInfo();
				String databaseName = databaseInfo.getDbName();
				String fileNameForLoaddbData = databaseName + "_objects";
				File savedFile = TableUtil.getSavedFile(getShell(), new String[]{"*.*" },
						new String[]{"All Files" }, fileNameForLoaddbData, null, null);
				if (savedFile != null) {
					dataPathText.setText(savedFile.getAbsolutePath());
				}
				updateDialogStatus();
			}
		});

		Group enCodingOptionGroup = new Group(rightComposite, SWT.None);
		enCodingOptionGroup.setText(Messages.exportWizardDataOption);
		enCodingOptionGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1,
				1, -1, -1));
		enCodingOptionGroup.setLayout(new GridLayout(4, false));

		Label dbCharsetLabel = new Label(enCodingOptionGroup, SWT.None);
		dbCharsetLabel.setLayoutData(CommonUITool.createGridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
		dbCharsetLabel.setText(Messages.lblJDBCCharset);

		dbCharsetCombo = new Combo(enCodingOptionGroup, SWT.BORDER);
		dbCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, 50, 21));
		dbCharsetCombo.setItems(QueryOptions.getAllCharset(null));
		dbCharsetCombo.setEnabled(false);

		Label fileCharsetLabel = new Label(enCodingOptionGroup, SWT.None);
		fileCharsetLabel.setLayoutData(CommonUITool.createGridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
		fileCharsetLabel.setText(Messages.lblFileCharset);

		fileCharsetCombo = new Combo(enCodingOptionGroup, SWT.BORDER);
		fileCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, 50, 21));
		fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
		fileCharsetCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				updateDialogStatus();
			}
		});

		startValueButton = new Button(enCodingOptionGroup, SWT.CHECK);
		startValueButton.setText(Messages.lblExportTargetStartValue);
		startValueButton.setLayoutData(CommonUITool.createGridData(4, 1, -1, -1));
		startValueButton.setToolTipText(Messages.tipExportTargetStartValue);

		setControl(container);
	}

	/**
	 * Initial the page values.
	 *
	 */
	private void init() {
		initTableColumnInfo();
		ctv.setInput(tablesOrViewLst);
		if (getExportConfig().isHistory()) { //history
			ExportConfig exportConfig = getExportConfig();
			for (String table : exportConfig.getTableNameList()) {
				for (ICubridNode node : tablesOrViewLst) {
					if (table.equalsIgnoreCase(node.getName())) {
						ctv.setChecked(node, true);
						String whereCondition = getExportConfig().getWhereCondition(table);
						if (StringUtil.isNotEmpty(whereCondition)) {
							node.setData(ExportObjectLabelProvider.CONDITION, whereCondition);
						}
					}
				}
			}
			ctv.refresh();

			if (exportConfig.isExportSchema()) {
				schemaButton.setSelection(true);
				schemaPathText.setText(exportConfig.getSchemaFilePath());
				schemaPathText.setEnabled(true);
				schemaBrowseButton.setEnabled(true);
				if (exportConfig.isExportSerialStartValue()) {
					startValueButton.setEnabled(true);
				}
			}
			if (exportConfig.isExportData()) {
				dataButton.setSelection(true);
				dataPathText.setText(exportConfig.getDataFileFolder());
				dataPathText.setEnabled(true);
				dataBrowseButton.setEnabled(true);
			}
			if (exportConfig.isExportIndex()) {
				indexButton.setSelection(true);
				indexPathText.setText(exportConfig.getIndexFilePath());
				indexPathText.setEnabled(true);
				indexBrowseButton.setEnabled(true);
			}
		} else {
			List<String> tableList = ((ExportDataWizard) getWizard()).getTableNameList();
			for (String table : tableList) {
				for (ICubridNode node : tablesOrViewLst) {
					if (table.equalsIgnoreCase(node.getName())) {
						ctv.setChecked(node, true);
					}
				}
			}
			ctv.refresh();
			schemaButton.setSelection(true);
			indexButton.setSelection(true);
			dataButton.setSelection(true);
			triggerButton.setSelection(true);
			startValueButton.setSelection(true);
		}

		String[] charsets = QueryOptions.getAllCharset(null);
		int index = 0;
		for (int i = 0; i < charsets.length; i++) {
			String charset = charsets[i];
			if (charset.equals(getDatabase().getDatabaseInfo().getCharSet())) {
				index = i;
				break;
			}
		}
		dbCharsetCombo.select(index);
		index = 0;
		for (int i = 0; i < charsets.length; i++) {
			String charset = charsets[i];
			if (charset.equals(getExportConfig().getFileCharset())) {
				index = i;
				break;
			}
		}
		fileCharsetCombo.select(index);

		updateDialogStatus();
	}

	/**
	 * load tables and columns
	 */
	public void initTableColumnInfo() { // FIXME move this logic to core module
		try {
			ProgressMonitorDialog progress = new ProgressMonitorDialog(
					Display.getCurrent().getActiveShell());
			progress.setCancelable(true);
			progress.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					monitor.beginTask(Messages.taskLoading, 2);
					tablesOrViewLst = new ArrayList<ICubridNode>();
					List<String> tableNames = new ArrayList<String>();
					Connection conn = null;
					Statement stmt = null;
					ResultSet rs = null;

					StringBuilder sql = new StringBuilder();
					sql.append("SELECT c.class_name, c.class_type ");
					sql.append("FROM db_class c, db_attribute a ");
					sql.append("WHERE c.class_name=a.class_name AND c.is_system_class='NO' ");
					sql.append("AND a.from_class_name IS NULL ");
					sql.append("GROUP BY c.class_name, c.class_type ");
					sql.append("ORDER BY c.class_type, c.class_name");

					String query = sql.toString();

					// [TOOLS-2425]Support shard broker
					if (getDatabase() != null) {
						query = DatabaseInfo.wrapShardQuery(getDatabase().getDatabaseInfo(), query);
					}

					try {
						monitor.subTask(Messages.taskLoadingTable);
						conn = JDBCConnectionManager.getConnection(getDatabase().getDatabaseInfo(),
								true);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) {
							String tableName = rs.getString(1); //$NON-NLS-1$
							String tableType = rs.getString(2); //$NON-NLS-1$
							if (ConstantsUtil.isExtensionalSystemTable(tableName)) {
								continue;
							}
							String iconPath = "icons/navigator/schema_table_item.png";
							if ("VCLASS".equalsIgnoreCase(tableType)) {
								//iconPath = "icons/navigator/schema_view_item.png";
								//export all view now so don't need select view node
								continue;
							}
							ICubridNode classNode = new DefaultSchemaNode(tableName, tableName,
									iconPath);
							classNode.setContainer(true);
							classNode.setType(NodeType.TABLE_COLUMN_FOLDER);
							tablesOrViewLst.add(classNode);
							tableNames.add(tableName);
						}
						QueryUtil.freeQuery(rs);
						monitor.worked(1);
					} catch (SQLException e) {
						CommonUITool.openErrorBox(getShell(),
								e.getErrorCode() + System.getProperty("line.separator")
										+ Messages.importErrorHead + e.getMessage());
						LOGGER.error("", e);
					} finally {
						QueryUtil.freeQuery(conn, stmt, rs);
					}

				}
			});
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * Update the dialog status
	 *
	 */
	private void updateDialogStatus() {
		if (ctv.getCheckedElements().length == 0) {
			setErrorMessage(Messages.exportSelectTargetTableERRORMSG);
			setPageComplete(false);
			return;
		}
		if (!schemaButton.getSelection() && !indexButton.getSelection()
				&& !triggerButton.getSelection() && !dataButton.getSelection()) {
			setErrorMessage(Messages.exportWizardLoadDBPageErrMsg1);
			setPageComplete(false);
			return;
		}

		if (schemaButton.getSelection()) {
			String schemaPath = schemaPathText.getText().trim();
			if (schemaPath.length() == 0) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg2);
				setPageComplete(false);
				return;
			}
			if (getExportConfig().isHistory()) {
				File testFile = new File(schemaPathText.getText());
				if (!testFile.getParentFile().exists()) {
					setErrorMessage(Messages.exportWizardLoadDBPageFilepathErrMsg1);
					setPageComplete(false);
					return;
				}

			}
			if (indexButton.getSelection()
					&& schemaPathText.getText().equals(indexPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			} else if (dataButton.getSelection()
					&& schemaPathText.getText().equals(dataPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			} else if (triggerButton.getSelection()
					&& schemaPathText.getText().equals(triggerPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			}
		}

		if (indexButton.getSelection()) {
			String indexPath = indexPathText.getText().trim();
			if (indexPath.length() == 0) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg3);
				setPageComplete(false);
				return;
			}
			if (getExportConfig().isHistory()) {
				File testFile = new File(indexPathText.getText());
				if (!testFile.getParentFile().exists()) {
					setErrorMessage(Messages.exportWizardLoadDBPageFilepathErrMsg2);
					setPageComplete(false);
					return;
				}

			}

			if (schemaButton.getSelection()
					&& schemaPathText.getText().equals(indexPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			} else if (dataButton.getSelection()
					&& indexPathText.getText().equals(dataPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			} else if (triggerButton.getSelection()
					&& indexPathText.getText().equals(triggerPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			}
		}
		if (dataButton.getSelection()) {
			String dataPath = dataPathText.getText().trim();
			if (dataPath.length() == 0) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg4);
				setPageComplete(false);
				return;
			}
			if (getExportConfig().isHistory()) {
				File testFile = new File(dataPathText.getText());
				if (!testFile.getParentFile().exists()) {
					setErrorMessage(Messages.exportWizardLoadDBPageFilepathErrMsg3);
					setPageComplete(false);
					return;
				}
			}
			if (schemaButton.getSelection()
					&& schemaPathText.getText().equals(dataPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			} else if (indexButton.getSelection()
					&& indexPathText.getText().equals(dataPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			} else if (triggerButton.getSelection()
					&& triggerPathText.getText().equals(dataPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			}
		}
		if (triggerButton.getSelection()) {
			String triggerPath = triggerPathText.getText().trim();
			if (triggerPath.length() == 0) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg8);
				setPageComplete(false);
				return;
			}
			if (getExportConfig().isHistory()) {
				File testFile = new File(triggerPathText.getText());
				if (!testFile.getParentFile().exists()) {
					setErrorMessage(Messages.exportWizardLoadDBPageFilepathErrMsg3);
					setPageComplete(false);
					return;
				}
			}
			if (schemaButton.getSelection()
					&& schemaPathText.getText().equals(triggerPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			} else if (indexButton.getSelection()
					&& indexPathText.getText().equals(triggerPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			} else if (dataButton.getSelection()
					&& dataPathText.getText().equals(triggerPathText.getText())) {
				setErrorMessage(Messages.exportWizardLoadDBPageErrMsg7);
				setPageComplete(false);
				return;
			}
		}

		String fileCharset = fileCharsetCombo.getText();
		if (fileCharset.trim().length() == 0) {
			setErrorMessage(Messages.exportWizardLoadDBPageErrMsg5);
			setPageComplete(false);
			return;
		}

		try {
			"".getBytes(fileCharset);
		} catch (UnsupportedEncodingException e) {
			setErrorMessage(Messages.exportWizardLoadDBPageErrMsg6);
			setPageComplete(false);
			return;
		}
		setErrorMessage(null);
		setDescription(Messages.titleExportSetting);
		setPageComplete(true);
	}

	/**
	 * When displayed current page.
	 *
	 * @param event PageChangedEvent
	 */

	protected void afterShowCurrentPage(PageChangedEvent event) {
		if (isFirstVisible) {
			init();
			isFirstVisible = false;
		}
	}

	/**
	 * When leave current page
	 *
	 * @param event PageChangingEvent
	 */
	protected void handlePageLeaving(PageChangingEvent event) {
		if (!(event.getTargetPage() instanceof ExportTypePage)) {
			if (checkWhereConditions()) {
				event.doit = false;
				return;
			}
			setOptionsToExportConfigModel(getExportConfig());
		} else {
			if (!CommonUITool.openConfirmBox(Messages.exportWizardBackConfirmMsg)) {
				event.doit = false;
				return;
			} else {
				clearOptions();
			}
		}
	}

	/**
	 * set page parameter to config model
	 *
	 * @param exportConfig
	 */
	public void setOptionsToExportConfigModel(ExportConfig exportConfig) {
		if (schemaButton.getSelection()) {
			exportConfig.setExportSchema(true);
			exportConfig.setSchemaFilePath(schemaPathText.getText().trim());
			exportConfig.setDataFilePath(ExportConfig.LOADDB_SCHEMAFILEKEY,
					schemaPathText.getText().trim());
		} else {
			exportConfig.setExportSchema(false);
		}

		if (indexButton.getSelection()) {
			exportConfig.setExportIndex(true);
			exportConfig.setIndexFilePath(indexPathText.getText().trim());
			exportConfig.setDataFilePath(ExportConfig.LOADDB_INDEXFILEKEY,
					indexPathText.getText().trim());
		} else {
			exportConfig.setExportIndex(false);
		}

		if (dataButton.getSelection()) {
			exportConfig.setExportData(true);
			exportConfig.setDataFileFolder(dataPathText.getText().trim());
			exportConfig.setDataFilePath(ExportConfig.LOADDB_DATAFILEKEY,
					dataPathText.getText().trim());
		} else {
			exportConfig.setExportData(false);
		}

		if (triggerButton.getSelection()) {
			exportConfig.setExportTrigger(true);
			exportConfig.setTriggerFilePath(triggerPathText.getText().trim());
			exportConfig.setDataFilePath(ExportConfig.LOADDB_TRIGGERFILEKEY,
					triggerPathText.getText().trim());
		} else {
			exportConfig.setExportTrigger(false);
		}
		exportConfig.setExportSerialStartValue(startValueButton.getSelection());
		exportConfig.setFileCharset(fileCharsetCombo.getText());

		List<String> checkedTableList = new ArrayList<String>();
		List<ICubridNode> selectedTableOrViews = new ArrayList<ICubridNode>();
		Object[] objects = ctv.getCheckedElements();
		for (Object object : objects) {
			ICubridNode node = (ICubridNode) object;
			if (node.getType() == NodeType.TABLE_COLUMN_FOLDER) {
				selectedTableOrViews.add(node);
				checkedTableList.add(node.getName());
			}
		}
		exportConfig.setTableNameList(checkedTableList);
		removeUncheckedTablesOnConfig(exportConfig, checkedTableList);

		for (ICubridNode tableOrView : selectedTableOrViews) { // FIXME move this logic to core module
			List<ICubridNode> columnNodes = tableOrView.getChildren();
			ArrayList<String> columnNames = new ArrayList<String>();
			for (ICubridNode columnNode : columnNodes) {
				for (Object object : objects) {
					ICubridNode node = (ICubridNode) object;
					if (node.getType() == NodeType.TABLE_COLUMN) {
						ICubridNode parent = node.getParent();
						if (parent != null && parent.getId().equalsIgnoreCase(tableOrView.getId())
								&& columnNode.getName().equalsIgnoreCase(node.getName())) {
							columnNames.add(columnNode.getName());
						}
					}
				}
			}

			exportConfig.setColumnNameList(tableOrView.getName(), columnNames);
			Object whereCondition = tableOrView.getData(ExportObjectLabelProvider.CONDITION);
			if (whereCondition != null) {
				String sqlFilterPart = ((String) whereCondition).trim();
				//append "where" if necessary
				if (sqlFilterPart.length() > 0) {
					if (!sqlFilterPart.startsWith("where")) { //TODO: if it has not a lowercase?
						sqlFilterPart = " where " + sqlFilterPart;
					}
					exportConfig.setWhereCondition(tableOrView.getName(), sqlFilterPart);
				}
			}
		}

		exportConfig.setThreadCount(1);
		exportConfig.setExportType(ExportConfig.EXPORT_TO_LOADDB);
		exportConfig.setExportFileType(ExportConfig.FILE_TYPE_LOADDB);
	}

	private void removeUncheckedTablesOnConfig(ExportConfig configModel, List<String> checkedTables) {
		List<String> shouldBeRemovedTables = new ArrayList<String>();
		for (String tableName : configModel.getTableNameList()) {
			if (!checkedTables.contains(tableName)) {
				shouldBeRemovedTables.add(tableName);
			}
		}
		for (String tableName : shouldBeRemovedTables) {
			configModel.removeTable(tableName);
		}
	}

	/**
	 * check where condition
	 *
	 * @param conn
	 * @param table
	 * @param sqlFilter
	 * @return
	 */
	private boolean checkOneCondition(Connection conn, String table, String sqlFilter) { // FIXME move this logic to core module
		if (sqlFilter == null || "".equals(sqlFilter.trim())) {
			return true;
		}
		//build query sql statement
		StringBuilder bf = new StringBuilder();
		bf.append("SELECT COUNT(*) FROM ");
		bf.append(QuerySyntax.escapeKeyword(table)).append(" ");

		String filter = sqlFilter;
		String filterLower = sqlFilter.toLowerCase(Locale.US).trim();
		//append "where" if necessary
		if (!filterLower.startsWith("where")) {
			filter = "WHERE " + filter;
		}
		bf.append(filter);

		//verify sql statement on the server side
		String sql = bf.toString();
		Statement stmt = null; //NOPMD
		ResultSet rs = null; //NOPMD
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			return true;
		} catch (SQLException e) {
			LOGGER.error("verify syntax of sql:" + sql, e);
			return false;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	/**
	 * check where condition
	 *
	 * @return error table list
	 */
	public boolean checkWhereConditions() { // FIXME move this logic to core module
		Connection conn = null;
		List<String> errorList = new ArrayList<String>();
		try {
			conn = JDBCConnectionManager.getConnection(getDatabase().getDatabaseInfo(), false);
			for (Object object : ctv.getCheckedElements()) {
				ICubridNode node = (ICubridNode) object;
				if (node.getType() == NodeType.TABLE_COLUMN_FOLDER) {
					Object o = node.getData(ExportObjectLabelProvider.CONDITION);
					if (o == null) {
						continue;
					}
					String tableName = node.getName();
					if (!checkOneCondition(conn, tableName, o.toString())) {
						errorList.add(tableName);
					}
				}
			}
		} catch (Exception e) {
			CommonUITool.openErrorBox(getShell(), Messages.importErrorHead + e.getMessage());
			LOGGER.error("", e);
			return true;
		} finally {
			QueryUtil.freeQuery(conn);
		}
		if (errorList.isEmpty()) {
			setErrorMessage(null);
			return false;
		} else {
			CommonUITool.openErrorBox(getShell(), Messages.conditionError + errorList.toString());
			setErrorMessage(Messages.conditionErrorTitle);
			return true;
		}
	}

	/**
	 * clear all options
	 */
	public void clearOptions() {
		this.isFirstVisible = true;
		schemaButton.setSelection(false);
		schemaPathText.setText("");
		indexButton.setSelection(false);
		indexPathText.setText("");
		dataButton.setSelection(false);
		dataPathText.setText("");
		triggerButton.setSelection(false);
		triggerPathText.setText("");
		Object[] objects = ctv.getCheckedElements();
		for (Object o : objects) {
			ctv.setChecked(o, false);
		}
		tablesOrViewLst.clear();
	}
}
