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
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.dialog.FilterTreeContentProvider;
import com.cubrid.common.ui.common.navigator.ExportObjectLabelProvider;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.spi.StatusInfo;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn;

/**
 * The Export Setting Page
 *
 * @author Kevin.Wang
 * @version 1.0 - Jul 30, 2012 created by Kevin.Wang
 */
public class ExportSettingPage extends
		ExportWizardPage {

	public final static String PAGE_NAME = ExportSettingPage.class.getName();
	private static final Logger LOGGER = LogUtil.getLogger(ExportSettingPage.class);
	public final static int DEFAULT_EXPORT_THREAD_COUNT = 1;
	public final static int MIN_EXPORT_THREAD_COUNT = 1;
	public final static int MAX_EXPORT_THREAD_COUNT = 10;
	public final static String VIEWNODEFLAG = "view";
	private boolean isFirstVisible = true;
	private CheckboxTreeViewer treeViewer;
	private Button schemaButton;
	private Button dataButton;
	private Button indexButton;
	private Button serialButton;
	private Button viewButton;
	private Button triggerButton;
	private Button startValueButton;
	private Button exportLobButton;

	private Button sqlButton;
	private Button csvButton;
	private Button xlsButton;
	private Button xlsxButton;
	private Button obsButton;
	private Button txtButton;
	private Text pathText;
	private Spinner threadCountSpinner;
	private Combo rowDelimiterCombo;
	private Combo columnDelimiterCombo;
	private Button useFirstAsColumnBtn = null;
	private Button nullOneButton;
	private Button nullTwoButton;
	private Button nullThreeButton;
	private Button otherButton;
	private Text otherText;
	private Combo dbCharsetCombo;
	private Combo fileCharsetCombo;
	private List<ICubridNode> tablesOrViewLst;
	private String[] columnDelimeter = {",", "\t", "'" };
	private String[] columnDelimeterName = {Messages.lblNameComma, Messages.lblNameTab,
			Messages.lblNameQuote };
	private String[] rowDelimeter = {",", StringUtil.NEWLINE, "\t", "'" };
	private String[] rowDelimeterName = {Messages.lblNameComma, Messages.lblNameEnter,
			Messages.lblNameTab, Messages.lblNameQuote };
	private Map<String, List<TableColumn>> tableColumMap = new HashMap<String, List<TableColumn>>();

	public ExportSettingPage() {
		super(PAGE_NAME, Messages.exportShellTitle, null);
		setTitle(Messages.titleExportStep2);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());

		setControl(container);
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

		treeViewer = new CheckboxTreeViewer(leftComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER | SWT.FULL_SELECTION);
		treeViewer.getTree().setLayoutData(
				CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
		treeViewer.setContentProvider(new FilterTreeContentProvider());
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setHeaderVisible(true);

		treeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateDialogStatus(null);
			}
		});

		final TreeViewerColumn dbObjectCol = new TreeViewerColumn(treeViewer, SWT.NONE);
		dbObjectCol.setLabelProvider(new ExportObjectLabelProvider());

		final TreeViewerColumn whereCnd = new TreeViewerColumn(treeViewer, SWT.NONE);
		whereCnd.setLabelProvider(new ExportObjectLabelProvider());
		whereCnd.setEditingSupport(new EditingSupport(treeViewer) {
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
					textCellEditor = new TextCellEditor(treeViewer.getTree());
					textCellEditor.addListener(new ICellEditorListener() {

						public void applyEditorValue() {
						}

						public void cancelEditor() {
						}

						public void editorValueChanged(boolean oldValidState, boolean newValidState) {
						}
					});
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
				treeViewer.refresh();
				updateDialogStatus(null);
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
					treeViewer.setGrayed(node, false);
					treeViewer.setChecked(node, selection);
				}
				updateDialogStatus(null);
			}
		});

		/* Export content option */
		Group exportOptionGroup = new Group(rightComposite, SWT.None);
		exportOptionGroup.setText(Messages.exportWizardWhatExport);
		exportOptionGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1,
				-1, -1));
		RowLayout layout = new RowLayout();
		layout.spacing = 8;
		layout.marginWidth = 5;
		exportOptionGroup.setLayout(layout);

		schemaButton = new Button(exportOptionGroup, SWT.CHECK);
		schemaButton.setText(Messages.lblExportTargetSchema);
		schemaButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				boolean selection = schemaButton.getSelection();
				indexButton.setEnabled(selection);
				serialButton.setEnabled(selection);
				viewButton.setEnabled(selection);
				triggerButton.setEnabled(selection);
				startValueButton.setEnabled(selection);
				updateDialogStatus(null);
			}
		});

		dataButton = new Button(exportOptionGroup, SWT.CHECK);
		dataButton.setText(Messages.lblExportTargetData);
		dataButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				updateDialogStatus(null);
				updateExportLobButtonStatus();
			}
		});

		indexButton = new Button(exportOptionGroup, SWT.CHECK);
		indexButton.setText(Messages.lblExportTargetIndex);

		serialButton = new Button(exportOptionGroup, SWT.CHECK);
		serialButton.setText(Messages.lblExportTargetSerial);

		viewButton = new Button(exportOptionGroup, SWT.CHECK);
		viewButton.setText(Messages.lblExportTargetView);

		triggerButton = new Button(exportOptionGroup, SWT.CHECK);
		triggerButton.setText(Messages.lblExportTargetTrigger);

		exportLobButton = new Button(exportOptionGroup, SWT.CHECK);
		exportLobButton.setText(Messages.lblExportLobData);
		exportLobButton.setToolTipText(Messages.lblExportLobData);
		exportLobButton.setEnabled(false);

		startValueButton = new Button(exportOptionGroup, SWT.CHECK);
		startValueButton.setText(Messages.lblExportTargetStartValue);
		startValueButton.setToolTipText(Messages.tipExportTargetStartValue);

		/* Type group */
		Group typeOptionGroup = new Group(rightComposite, SWT.None);
		typeOptionGroup.setText(Messages.exportWizardWhereExport);
		typeOptionGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1,
				-1, -1));
		typeOptionGroup.setLayout(new GridLayout(2, false));

		Label typeLabel = new Label(typeOptionGroup, SWT.None);
		typeLabel.setText(Messages.exportWizardFileType);
		typeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		Composite typeComposite = new Composite(typeOptionGroup, SWT.None);
		typeComposite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
		typeComposite.setLayout(new GridLayout(6, false));

		sqlButton = new Button(typeComposite, SWT.RADIO);
		sqlButton.setText("SQL");
		sqlButton.setToolTipText("SQL");
		sqlButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		sqlButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				useFirstAsColumnBtn.setEnabled(false);
				setNullWidgetStatus(false);
				setDelimiterWidgetStatus(false);
				updateDialogStatus(null);
				updateExportLobButtonStatus();
			}
		});

		csvButton = new Button(typeComposite, SWT.RADIO);
		csvButton.setText("CSV");
		csvButton.setToolTipText("CSV");
		csvButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		csvButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				useFirstAsColumnBtn.setEnabled(true);
				setNullWidgetStatus(true);
				setDelimiterWidgetStatus(false);
				updateDialogStatus(null);
				updateExportLobButtonStatus();
			}
		});

		xlsButton = new Button(typeComposite, SWT.RADIO);
		xlsButton.setText("XLS");
		xlsButton.setToolTipText("XLS");
		xlsButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		xlsButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				useFirstAsColumnBtn.setEnabled(true);
				setNullWidgetStatus(true);
				setDelimiterWidgetStatus(false);
				updateDialogStatus(null);
				updateExportLobButtonStatus();
			}
		});

		xlsxButton = new Button(typeComposite, SWT.RADIO);
		xlsxButton.setText("XLSX");
		xlsxButton.setToolTipText("XLSX");
		xlsxButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		xlsxButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				useFirstAsColumnBtn.setEnabled(true);
				setNullWidgetStatus(true);
				setDelimiterWidgetStatus(false);
				updateDialogStatus(null);
				updateExportLobButtonStatus();
			}
		});

		txtButton = new Button(typeComposite, SWT.RADIO);
		txtButton.setText("TXT");
		txtButton.setToolTipText("TXT");
		txtButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		txtButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				useFirstAsColumnBtn.setEnabled(true);
				setNullWidgetStatus(true);
				setDelimiterWidgetStatus(true);
				updateDialogStatus(null);
				updateExportLobButtonStatus();
			}
		});

		obsButton = new Button(typeComposite, SWT.RADIO);
		obsButton.setText("OBS");
		obsButton.setToolTipText("OBS");
		obsButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		obsButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				useFirstAsColumnBtn.setEnabled(false);
				setNullWidgetStatus(false);
				setDelimiterWidgetStatus(false);
				updateDialogStatus(null);
				updateExportLobButtonStatus();
			}
		});

		Label pathLabel = new Label(typeOptionGroup, SWT.None);
		pathLabel.setText(Messages.exportWizardFilepath);
		pathLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		Composite fileComposite = new Composite(typeOptionGroup, SWT.None);
		fileComposite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
		fileComposite.setLayout(new GridLayout(2, false));

		pathText = new Text(fileComposite, SWT.BORDER);
		pathText.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
		pathText.setEditable(false);

		Button browseButton = new Button(fileComposite, SWT.None);
		browseButton.setText(Messages.btnBrowse);
		browseButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				File savedDirFile = TableUtil.getSavedDir(getShell(),
						Messages.titleSelectFolderToBeExported,
						Messages.msgSelectFolderToBeExported, pathText.getText());
				if (savedDirFile != null) {
					String savePathString = null;

					String[] files = savedDirFile.list();
					String databaseName = getDatabase().getDatabaseInfo().getDbName();
					if (files != null && files.length > 0) {
						String confirmMessage = Messages.bind(
								Messages.errorExportExistsFilesInFolder, databaseName);
						boolean useCreate = CommonUITool.openConfirmBox(confirmMessage);
						if (useCreate) {
							File newFolder = new File(savedDirFile.getAbsolutePath()
									+ File.separator + databaseName);
							boolean existsDbNameFolder = newFolder.exists();
							files = newFolder.list();
							if (existsDbNameFolder && files != null && files.length > 0) {
								String newFolderName = databaseName + "_"
										+ DateUtil.getDatetimeStringOnNow("HHmmss");
								savePathString = savedDirFile.getAbsolutePath() + File.separator
										+ newFolderName;
								String warnMessage = Messages.bind(
										Messages.errorExportExistsFilesInFolderWithRename,
										newFolderName);
								CommonUITool.openWarningBox(warnMessage);
							} else {
								savePathString = savedDirFile.getAbsolutePath() + File.separator
										+ databaseName;
							}

							new File(savePathString).mkdirs();
						} else {
							return;
						}
					} else {
						savePathString = savedDirFile.getAbsolutePath();
					}
					pathText.setText(savePathString);
				}
				updateDialogStatus(null);
			}
		});

		Group parsingGroup = new Group(rightComposite, SWT.None);
		parsingGroup.setText(Messages.exportWizardParsingOption);
		parsingGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1,
				-1));
		GridLayout parsingGroupLayout = new GridLayout(4, false);
		parsingGroupLayout.horizontalSpacing = 10;
		parsingGroup.setLayout(parsingGroupLayout);

		Label threadCountLabel = new Label(parsingGroup, SWT.None);
		threadCountLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		threadCountLabel.setText(Messages.lblThreadCount);

		threadCountSpinner = new Spinner(parsingGroup, SWT.BORDER | SWT.LEFT);
		threadCountSpinner.setValues(DEFAULT_EXPORT_THREAD_COUNT, MIN_EXPORT_THREAD_COUNT,
				MAX_EXPORT_THREAD_COUNT, 0, 1, 2);
		threadCountSpinner.setLayoutData(CommonUITool.createGridData(
				GridData.HORIZONTAL_ALIGN_FILL, 1, 1, -1, -1));

		Label emptyLabel = new Label(parsingGroup, SWT.None);
		emptyLabel.setLayoutData(CommonUITool.createGridData(2, 1, -1, -1));
		emptyLabel.setText("");

		Label dbCharsetLabel = new Label(parsingGroup, SWT.None);
		dbCharsetLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dbCharsetLabel.setText(Messages.lblJDBCCharset);

		dbCharsetCombo = new Combo(parsingGroup, SWT.BORDER);
		dbCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dbCharsetCombo.setItems(QueryOptions.getAllCharset(null));
		dbCharsetCombo.setEnabled(false);

		Label fileCharsetLabel = new Label(parsingGroup, SWT.None);
		fileCharsetLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		fileCharsetLabel.setText(Messages.lblFileCharset);

		fileCharsetCombo = new Combo(parsingGroup, SWT.BORDER);
		fileCharsetCombo.setLayoutData(CommonUITool.createGridData(1, 1, -1, 21));
		fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));

		Group dataOptionGroup = new Group(rightComposite, SWT.None);
		dataOptionGroup.setText(Messages.exportWizardDataOption);
		dataOptionGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1,
				-1, -1));
		dataOptionGroup.setLayout(new GridLayout(2, false));

		useFirstAsColumnBtn = new Button(dataOptionGroup, SWT.CHECK);
		{
			useFirstAsColumnBtn.setText(Messages.exportFirstLineFLAG);
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalIndent = 0;
			gridData.horizontalSpan = 2;
			useFirstAsColumnBtn.setLayoutData(gridData);
			useFirstAsColumnBtn.setSelection(false);
		}

		Group delimiterOptionGroup = new Group(dataOptionGroup, SWT.None);
		delimiterOptionGroup.setText(Messages.exportWizardDelimiterOptions);
		delimiterOptionGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1,
				1, -1, -1));
		delimiterOptionGroup.setLayout(new GridLayout(2, false));

		Label columnLabel = new Label(delimiterOptionGroup, SWT.None);
		columnLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		columnLabel.setText(Messages.exportWizardColumnSeperator);

		columnDelimiterCombo = new Combo(delimiterOptionGroup, SWT.BORDER);
		columnDelimiterCombo.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1,
				1, -1, -1));
		columnDelimiterCombo.setTextLimit(32);
		columnDelimiterCombo.setItems(columnDelimeterName);
		columnDelimiterCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				updateDialogStatus(null);
			}
		});

		Label rowLabel = new Label(delimiterOptionGroup, SWT.None);
		rowLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		rowLabel.setText(Messages.exportWizardRowSeperator);

		rowDelimiterCombo = new Combo(delimiterOptionGroup, SWT.BORDER);
		rowDelimiterCombo.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1,
				-1, -1));
		rowDelimiterCombo.setTextLimit(32);
		rowDelimiterCombo.setItems(rowDelimeterName);
		rowDelimiterCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				updateDialogStatus(null);
			}
		});

		Group nullValueGroup = new Group(dataOptionGroup, SWT.None);
		nullValueGroup.setText(Messages.exportWizardNullOptions);
		nullValueGroup.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
		nullValueGroup.setLayout(new GridLayout(3, false));

		nullOneButton = new Button(nullValueGroup, SWT.RADIO);
		nullOneButton.setText("'NULL'");
		nullOneButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		nullTwoButton = new Button(nullValueGroup, SWT.RADIO);
		nullTwoButton.setText("'\\N'");
		nullTwoButton.setLayoutData(CommonUITool.createGridData(2, 1, -1, -1));

		nullThreeButton = new Button(nullValueGroup, SWT.RADIO);
		nullThreeButton.setText("'(NULL)'");
		nullThreeButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		otherButton = new Button(nullValueGroup, SWT.RADIO);
		otherButton.setText(Messages.btnOther);
		otherButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		otherButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				updateDialogStatus(null);
			}
		});

		otherText = new Text(nullValueGroup, SWT.BORDER);
		otherText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		otherText.setTextLimit(64);
		otherText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				updateDialogStatus(null);
			}
		});

		useFirstAsColumnBtn.setEnabled(false);
		setNullWidgetStatus(false);
		setDelimiterWidgetStatus(false);
	}

	/**
	 * Set null value widget status
	 *
	 * @param isEnable
	 */
	private void setNullWidgetStatus(boolean isEnable) {
		nullOneButton.setEnabled(isEnable);
		nullTwoButton.setEnabled(isEnable);
		nullThreeButton.setEnabled(isEnable);
		nullOneButton.setEnabled(isEnable);
		otherButton.setEnabled(isEnable);
		otherText.setEnabled(isEnable);
	}

	/**
	 * Set Delimiter widget status
	 *
	 * @param isEnable
	 */
	private void setDelimiterWidgetStatus(boolean isEnable) {
		rowDelimiterCombo.setEnabled(isEnable);
		columnDelimiterCombo.setEnabled(isEnable);
	}

	/**
	 * Initials some values.
	 */
	private void init() {
		initTableColumnInfo();
		treeViewer.setInput(tablesOrViewLst);
		if (getExportConfig().isHistory()) { // history
			ExportConfig exportConfig = getExportConfig();
			for (String table : exportConfig.getTableNameList()) {
				for (ICubridNode node : tablesOrViewLst) {
					if (table.equalsIgnoreCase(node.getName())) {
						treeViewer.setChecked(node, true);
						String whereCondition = exportConfig.getWhereCondition(table);
						if (whereCondition != null) {
							node.setData(ExportObjectLabelProvider.CONDITION, whereCondition);
						}
					}
				}
			}

			treeViewer.refresh();

			if (exportConfig.isExportSchema()) {
				schemaButton.setSelection(true);
				indexButton.setEnabled(true);
				serialButton.setEnabled(true);
				viewButton.setEnabled(true);
				triggerButton.setEnabled(true);
				startValueButton.setEnabled(true);
				if (exportConfig.isExportIndex()) {
					indexButton.setSelection(true);
				}
				if (exportConfig.isExportSerial()) {
					serialButton.setSelection(true);
				}
				if (exportConfig.isExportView()) {
					viewButton.setSelection(true);
				}
				if (exportConfig.isExportTrigger()) {
					triggerButton.setSelection(true);
				}
				if (exportConfig.isExportSerialStartValue()) {
					startValueButton.setSelection(true);
				}
			}
			if (exportConfig.isExportData()) {
				dataButton.setSelection(true);
			}

			if (exportConfig.getExportFileType() == ExportConfig.FILE_TYPE_SQL) {
				sqlButton.setSelection(true);
			} else if (exportConfig.getExportFileType() == ExportConfig.FILE_TYPE_CSV) {
				csvButton.setSelection(true);
				useFirstAsColumnBtn.setEnabled(true);
				setNullWidgetStatus(true);
			} else if (exportConfig.getExportFileType() == ExportConfig.FILE_TYPE_XLS) {
				xlsButton.setSelection(true);
				useFirstAsColumnBtn.setEnabled(true);
				setNullWidgetStatus(true);
			} else if (exportConfig.getExportFileType() == ExportConfig.FILE_TYPE_XLSX) {
				xlsxButton.setSelection(true);
				useFirstAsColumnBtn.setEnabled(true);
				setNullWidgetStatus(true);
			} else if (exportConfig.getExportFileType() == ExportConfig.FILE_TYPE_OBS) {
				obsButton.setSelection(true);
			} else if (exportConfig.getExportFileType() == ExportConfig.FILE_TYPE_TXT) {
				txtButton.setSelection(true);
				setNullWidgetStatus(true);
				setDelimiterWidgetStatus(true);
				useFirstAsColumnBtn.setEnabled(true);
			}

			updateExportLobButtonStatus();
			if (exportLobButton.isEnabled()) {
				exportLobButton.setSelection(exportConfig.isExportLob());
			}
			pathText.setText(exportConfig.getDataFileFolder());
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
				if (charset.equals(exportConfig.getFileCharset())) {
					index = i;
					break;
				}
			}
			fileCharsetCombo.select(index);
			threadCountSpinner.setSelection(exportConfig.getThreadCount());
			useFirstAsColumnBtn.setSelection(exportConfig.isFirstRowAsColumnName());

			setColumnDelimeter(exportConfig.getColumnDelimeter());
			setRowDelimeter(exportConfig.getRowDelimeter());

			if (exportConfig.getNULLValueTranslation() == null
					|| "NULL".equals(exportConfig.getNULLValueTranslation())) {
				nullOneButton.setSelection(true);
			} else if ("\\N".equals(exportConfig.getNULLValueTranslation())) {
				nullTwoButton.setSelection(true);
			} else if ("(NULL)".equals(exportConfig.getNULLValueTranslation())) {
				nullThreeButton.setSelection(true);
			} else {
				otherButton.setSelection(true);
				String nullValueTranslation = exportConfig.getNULLValueTranslation() == null ? ""
						: exportConfig.getNULLValueTranslation();
				otherText.setText(nullValueTranslation);
			}
		} else {
			// initial check element
			List<String> tableList = getExportDataWizardWizard().getTableNameList();
			for (String table : tableList) {
				for (ICubridNode node : tablesOrViewLst) {
					if (table.equalsIgnoreCase(node.getName())) {
						treeViewer.setChecked(node, true);
					}
				}
			}
			treeViewer.refresh();
			String[] charsets = QueryOptions.getAllCharset(null);
			int index = 0;
			for (int i = 0; i < charsets.length; i++) {
				String charset = charsets[i];
				if (charset.equals(getDatabase().getDatabaseInfo().getCharSet())) {
					index = i;
					break;
				}
			}
			schemaButton.setSelection(true);
			dataButton.setSelection(true);
			indexButton.setSelection(true);
			serialButton.setSelection(true);
			viewButton.setSelection(true);
			triggerButton.setSelection(true);
			startValueButton.setSelection(true);

			sqlButton.setSelection(true);
			nullOneButton.setSelection(true);
			dbCharsetCombo.select(index);
			rowDelimiterCombo.setEnabled(false);
			fileCharsetCombo.select(index);
			rowDelimiterCombo.select(1);
			columnDelimiterCombo.select(0);
			setDelimiterWidgetStatus(false);
			useFirstAsColumnBtn.setEnabled(false);
			updateExportLobButtonStatus();
		}
	}

	/**
	 * load tables and columns
	 */
	public void initTableColumnInfo() { // FIXME move this logic to core module
		tableColumMap.clear();
		try {
			ProgressMonitorDialog progress = new ProgressMonitorDialog(
					Display.getCurrent().getActiveShell());
			progress.setCancelable(true);
			progress.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					monitor.beginTask(Messages.taskLoading, 2);
					tablesOrViewLst = new ArrayList<ICubridNode>();
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
					if (CubridDatabase.hasValidDatabaseInfo(getDatabase())) {
						query = getDatabase().getDatabaseInfo().wrapShardQuery(query);
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
								//								iconPath = "icons/navigator/schema_view_item.png";
								//export all view now so don't need select view node
								continue;
							}
							ICubridNode classNode = new DefaultSchemaNode(tableName, tableName,
									iconPath);
							if ("VCLASS".equalsIgnoreCase(tableType)) {
								classNode.setData(VIEWNODEFLAG, "true");
							} else {
								classNode.setData(VIEWNODEFLAG, "false");
							}

							classNode.setContainer(true);
							classNode.setType(NodeType.TABLE_COLUMN_FOLDER);
							tablesOrViewLst.add(classNode);
						}
						QueryUtil.freeQuery(rs);
						monitor.worked(1);
						monitor.subTask(Messages.taskLoadingColumn);
					} catch (SQLException e) {
						String msg = e.getErrorCode() + StringUtil.NEWLINE
								+ Messages.importErrorHead + e.getMessage();
						CommonUITool.openErrorBox(getShell(), msg);
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
	 * at least export one type data
	 *
	 * @return
	 */
	public boolean checkExportOptions() {
		if (schemaButton.getSelection() || dataButton.getSelection()) {
			return true;
		}

		return false;
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
			for (Object object : treeViewer.getCheckedElements()) {
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
			LOGGER.error(e.getMessage(), e);
			return true;
		} finally {
			QueryUtil.freeQuery(conn);
		}

		if (errorList.isEmpty()) {
			setErrorMessage(null);
			return false;
		}

		CommonUITool.openErrorBox(getShell(), Messages.conditionError + errorList.toString());
		setErrorMessage(Messages.conditionErrorTitle);
		return true;
	}

	/**
	 * check where condition
	 *
	 * @return error table list
	 */
	public void checkWhereConditions(List<IStatus> statusList) { // FIXME move this logic to core module
		Connection conn = null;
		List<String> errorList = new ArrayList<String>();
		try {
			conn = JDBCConnectionManager.getConnection(getDatabase().getDatabaseInfo(), false);
			for (Object object : treeViewer.getCheckedElements()) {
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
		} finally {
			QueryUtil.freeQuery(conn);
		}

		if (!errorList.isEmpty()) {
			statusList.add(new StatusInfo(IStatus.ERROR, Messages.conditionError + errorList));
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
		if (StringUtil.isEmpty(sqlFilter)) {
			return true;
		}

		// build query sql statement
		// verify sql statement on the server side
		StringBuilder bf = new StringBuilder();
		bf.append("SELECT COUNT(*) FROM ").append(QuerySyntax.escapeKeyword(table)).append(" ");

		String filter = sqlFilter;
		String filterLower = sqlFilter.toLowerCase(Locale.US).trim();
		// append "where" if necessary
		if (!filterLower.startsWith("where")) {
			filter = "WHERE " + filter;
		}
		bf.append(filter);
		String sql = bf.toString();

		// [TOOLS-2425]Support shard broker
		if (CubridDatabase.hasValidDatabaseInfo(getDatabase())) {
			sql = getDatabase().getDatabaseInfo().wrapShardQuery(sql);
		}

		Statement stmt = null; // NOPMD
		ResultSet rs = null; // NOPMD
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			return true;
		} catch (Exception e) {
			LOGGER.error("verify syntax of sql:" + sql, e);
			return false;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	/**
	 * update dialog status
	 *
	 * @param errorStatus IStatus errorStatus
	 */
	private void updateDialogStatus(IStatus errorStatus) {
		if (errorStatus != null) {
			firePageStatusChanged(errorStatus);
			return;
		}

		List<IStatus> statusList = new ArrayList<IStatus>();
		IStatus dialogStatus = new StatusInfo(IStatus.INFO, Messages.titleExportSetting);
		statusList.add(dialogStatus);

		if (treeViewer.getCheckedElements().length == 0) {
			statusList.add(new StatusInfo(IStatus.ERROR, Messages.exportSelectTargetTableERRORMSG));
		} else if (StringUtil.isEmpty(pathText.getText())) {
			statusList.add(new StatusInfo(IStatus.ERROR, Messages.exportSelectFileERRORMSG));
		} else if (!checkExportOptions()) {
			statusList.add(new StatusInfo(IStatus.ERROR, Messages.exportDataCheckErrorMsg));
		} else if (otherButton.getSelection()) {
			if (otherText.getText().trim().length() == 0) {
				statusList.add(new StatusInfo(IStatus.ERROR, Messages.msgErrorOtherValueEmpty));
			} else if (otherText.getText().indexOf(",") >= 0) {
				statusList.add(new StatusInfo(IStatus.ERROR, Messages.msgErrorContainsComma));
			}
		} else if (txtButton.getSelection()) {
			if (columnDelimiterCombo.getText().indexOf("\"") >= 0
					|| columnDelimiterCombo.getText().indexOf("\n") >= 0) {
				statusList.add(new StatusInfo(IStatus.ERROR, Messages.errorSeparatorInvalid));
			} else if (columnDelimiterCombo.getText().equals(rowDelimiterCombo.getText())) {
				statusList.add(new StatusInfo(IStatus.ERROR, Messages.errorSeparatorInvalid2));
			}
		}

		if (getExportConfig().isHistory()) {
			File testFilePath = new File(pathText.getText());
			if (!testFilePath.exists()) {
				statusList.add(new StatusInfo(IStatus.ERROR,
						Messages.exportWizardSettngPageFilepathErrMsg));
			}
		}

		IStatus status = CommonUITool.getMostSevere(statusList);
		firePageStatusChanged(status);
	}

	private void updateExportLobButtonStatus() {
		if (dataButton.getSelection()
				&& (csvButton.getSelection() || xlsButton.getSelection()
						|| xlsxButton.getSelection() || txtButton.getSelection())) {
			exportLobButton.setEnabled(true);
		} else {
			exportLobButton.setEnabled(false);
		}
	}

	/**
	 * fire page changed
	 *
	 * @param status IStatus
	 */
	private void firePageStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.INFO) {
			setErrorMessage(null);
			setMessage(status.getMessage());
			setPageComplete(true);
		} else {
			setErrorMessage(status.getMessage());
			setPageComplete(false);
		}
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
		updateDialogStatus(null);
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
			if (!setOptionsToExportConfigModel(getExportConfig())) {
				event.doit = false;
				return;
			}
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
	public boolean setOptionsToExportConfigModel(ExportConfig exportConfig) {
		exportConfig.setExportSchema(schemaButton.getSelection());
		exportConfig.setExportData(dataButton.getSelection());
		exportConfig.setExportIndex(indexButton.getSelection());
		exportConfig.setExportSerial(serialButton.getSelection());
		exportConfig.setExportView(viewButton.getSelection());
		exportConfig.setExportTrigger(triggerButton.getSelection());
		exportConfig.setExportSerialStartValue(startValueButton.getSelection());

		int fileType = ExportConfig.FILE_TYPE_SQL;
		if (sqlButton.getSelection()) {
			fileType = ExportConfig.FILE_TYPE_SQL;
		} else if (csvButton.getSelection()) {
			fileType = ExportConfig.FILE_TYPE_CSV;
		} else if (xlsButton.getSelection()) {
			fileType = ExportConfig.FILE_TYPE_XLS;
		} else if (xlsxButton.getSelection()) {
			fileType = ExportConfig.FILE_TYPE_XLSX;
		} else if (obsButton.getSelection()) {
			fileType = ExportConfig.FILE_TYPE_OBS;
		} else if (txtButton.getSelection()) {
			fileType = ExportConfig.FILE_TYPE_TXT;
		}

		exportConfig.setExportFileType(fileType);
		exportConfig.setThreadCount(threadCountSpinner.getSelection());

		exportConfig.setFileCharset(fileCharsetCombo.getText());
		exportConfig.setFirstRowAsColumnName(useFirstAsColumnBtn.getSelection());
		exportConfig.setRowDelimeter(getRowDelimeter());
		exportConfig.setColumnDelimeter(getColumnDelimeter());

		String nullValue = "";
		if (nullOneButton.getSelection()) {
			nullValue = "NULL";
		} else if (nullTwoButton.getSelection()) {
			nullValue = "\\N";
		} else if (nullThreeButton.getSelection()) {
			nullValue = "(NULL)";
		} else if (otherButton.getSelection()) {
			nullValue = otherText.getText();
		}
		exportConfig.setNULLValueTranslation(nullValue);

		List<String> checkedTableList = new ArrayList<String>();
		List<ICubridNode> selectedTableOrViews = new ArrayList<ICubridNode>();
		Object[] objects = treeViewer.getCheckedElements();
		for (Object object : objects) {
			ICubridNode node = (ICubridNode) object;
			if (node.getType() == NodeType.TABLE_COLUMN_FOLDER
					&& "false".equals(node.getData(VIEWNODEFLAG))) {
				selectedTableOrViews.add(node);
				checkedTableList.add(node.getName());
			}
		}
		exportConfig.setTableNameList(checkedTableList);
		removeUncheckedTablesOnConfig(exportConfig, checkedTableList);

		String fileExt = getFileType();
		File savedDirFile = new File(pathText.getText());

		String[] filesInSavedDir = savedDirFile.list();
		if (filesInSavedDir.length > 0) {
			String duplicatedErrorMessage = Messages.bind(
					Messages.canceledBecauseOfExistsWithFolderName, pathText.getText());
			CommonUITool.openErrorBox(duplicatedErrorMessage);
			return false;
		}

		for (ICubridNode tableOrView : selectedTableOrViews) {
			String filePath = savedDirFile.getAbsolutePath() + File.separator
					+ tableOrView.getName() + fileExt;
			Object whereCondition = tableOrView.getData(ExportObjectLabelProvider.CONDITION);
			if (whereCondition != null) {
				String sqlFilterPart = ((String) whereCondition).trim();
				// append "where" if necessary
				if (StringUtil.isNotEmpty(sqlFilterPart)) {
					if (!sqlFilterPart.startsWith("where")) {
						sqlFilterPart = " where " + sqlFilterPart;
					}
					exportConfig.setWhereCondition(tableOrView.getName(), sqlFilterPart);
				}
			}
			exportConfig.setDataFilePath(tableOrView.getName(), filePath);
		}

		exportConfig.setDataFileFolder(pathText.getText());
		if (exportLobButton.isEnabled()) {
			exportConfig.setExportLob(exportLobButton.getSelection());
		}
		return true;

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
	 * Get export file type
	 *
	 * @return
	 */
	private String getFileType() {
		if (csvButton.getSelection()) {
			return ".csv";
		} else if (sqlButton.getSelection()) {
			return ".sql";
		} else if (xlsButton.getSelection()) {
			return ".xls";
		} else if (xlsxButton.getSelection()) {
			return ".xlsx";
		} else if (obsButton.getSelection()) {
			return ".obs";
		} else {
			return ".txt";
		}
	}

	/**
	 * clear all options
	 */
	public void clearOptions() {
		this.isFirstVisible = true;
		schemaButton.setSelection(false);
		dataButton.setSelection(false);
		indexButton.setSelection(false);
		indexButton.setEnabled(true);
		serialButton.setSelection(false);
		serialButton.setEnabled(true);
		viewButton.setSelection(false);
		viewButton.setEnabled(true);
		triggerButton.setSelection(false);
		triggerButton.setEnabled(true);
		startValueButton.setSelection(false);
		startValueButton.setEnabled(true);

		sqlButton.setSelection(false);
		csvButton.setSelection(false);
		xlsButton.setSelection(false);
		xlsxButton.setSelection(false);
		obsButton.setSelection(false);
		txtButton.setSelection(false);
		pathText.setText("");
		threadCountSpinner.setSelection(DEFAULT_EXPORT_THREAD_COUNT);
		rowDelimiterCombo.select(1);
		columnDelimiterCombo.select(0);
		useFirstAsColumnBtn.setSelection(false);
		nullOneButton.setSelection(false);
		nullTwoButton.setSelection(false);
		nullThreeButton.setSelection(false);
		otherButton.setSelection(false);
		otherText.setText("");
		Object[] objects = treeViewer.getCheckedElements();
		for (Object o : objects) {
			treeViewer.setChecked(o, false);
		}
		tablesOrViewLst.clear();
	}

	private String getColumnDelimeter() {
		if (columnDelimiterCombo.getSelectionIndex() < 0) {
			return columnDelimiterCombo.getText();
		} else {
			int index = columnDelimiterCombo.getSelectionIndex();
			return columnDelimeter[index];
		}
	}

	private String getRowDelimeter() {
		if (rowDelimiterCombo.getSelectionIndex() < 0) {
			return rowDelimiterCombo.getText();
		} else {
			int index = rowDelimiterCombo.getSelectionIndex();
			return rowDelimeter[index];
		}
	}

	/**
	 * Set the column delimeter
	 *
	 * @param delimeter
	 */
	private void setColumnDelimeter(String delimeter) {
		boolean isFound = false;
		for (int i = 0; i < columnDelimeter.length; i++) {
			if (columnDelimeter[i].equals(delimeter)) {
				columnDelimiterCombo.select(i);
				isFound = true;
				break;
			}
		}
		if (!isFound) {
			columnDelimiterCombo.setText(delimeter);
		}
	}

	/**
	 * Set the row delimeter
	 *
	 * @param delimeter
	 */
	private void setRowDelimeter(String delimeter) {
		boolean isFound = false;
		for (int i = 0; i < rowDelimeter.length; i++) {
			if (rowDelimeter[i].equals(delimeter)) {
				rowDelimiterCombo.select(i);
				isFound = true;
				break;
			}
		}
		if (!isFound) {
			rowDelimiterCombo.setText(delimeter);
		}
	}
}
