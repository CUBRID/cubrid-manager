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
package com.cubrid.common.ui.cubrid.procedure.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.procedure.Messages;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.task.CommonSQLExcuterTask;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPArgsInfo;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPArgsType;
import com.cubrid.cubridmanager.core.cubrid.sp.model.SPInfo;

/**
 * Show the edit user dialog
 *
 * @author robin 2009-3-18
 */
public class EditProcedureDialog extends CMTitleAreaDialog {
	private static final Logger LOGGER = LogUtil.getLogger(EditProcedureDialog.class);
	private Table procParamsTable;
	private final List<Map<String, String>> procParamsListData = new ArrayList<Map<String, String>>();
	private TableViewer procParamsTableViewer;
	private static SqlFormattingStrategy formator = new SqlFormattingStrategy();
	private Text procNameText;
	private Text procDescriptionText;
	private CubridDatabase database = null;
	private TabFolder tabFolder;
	private StyledText sqlScriptText;
	private Text javaNameText;
	private Composite barComp;
	private Map<String, String> sqlTypeMap = null;
	private Map<String, List<String>> javaTypeMap = null;
	private SPInfo spInfo = null;
	private boolean newFlag = false;
	private static final int BUTTON_ADD_ID = 1002;
	private static final int BUTTON_EDIT_ID = 1003;
	private static final int BUTTON_UP_ID = 1004;
	private static final int BUTTON_DOWN_ID = 1005;
	private static final int BUTTON_DROP_ID = 1006;
	private String procedureName = null;
	private boolean isCommentSupport = false;

	public EditProcedureDialog(Shell parentShell) {
		super(parentShell);
		initJavaType();
		initSqlTypeMap();
	}

	protected Control createDialogArea(Composite parent) {
		isCommentSupport = CompatibleUtil.isCommentSupports(database.getDatabaseInfo());
		Composite parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		layout = new GridLayout();
		tabFolder.setLayout(layout);

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(Messages.tabItemProcSetting);
		Composite procedureSettingComposite = createProcedureSettingComposite();
		item.setControl(procedureSettingComposite);

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText(Messages.tabItemProcSQLScript);
		Composite sqlCompo = createSqlScriptComposite();
		item.setControl(sqlCompo);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (tabFolder.getSelectionIndex() == 0) {
					procNameText.setFocus();
				} else if (tabFolder.getSelectionIndex() == tabFolder.getItemCount() - 1) {
					sqlScriptText.setText(getSQLScript());
				}
			}
		});

		initialize();

		if (isNewFlag()) {
			setTitle(Messages.titleAddProcedureDialog);
			setMessage(Messages.msgAddProcedureDialog);
			procNameText.setFocus();
		} else {
			setTitle(Messages.titleEditProcedureDialog);
			setMessage(Messages.msgEditProcedureDialog);
		}

		return parentComp;
	}

	/**
	 * Create the procedure setting composite
	 *
	 * @return the composite
	 */
	private Composite createProcedureSettingComposite() {
		final Composite composite = new Composite(tabFolder, SWT.LEFT | SWT.WRAP);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layoutComp;
		layoutComp = new GridLayout();
		layoutComp.marginWidth = 10;
		layoutComp.marginHeight = 10;
		layoutComp.numColumns = 2;
		composite.setLayout(layoutComp);
		final Label functionNameLabel = new Label(composite, SWT.NONE);
		functionNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		functionNameLabel.setText(Messages.lblProcedureName);

		procNameText = new Text(composite, SWT.BORDER);
		procNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
		procNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		procNameText.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			public void keyPressed(KeyEvent event) {
			}

			public void keyReleased(KeyEvent event) {
				if (StringUtil.isEmpty(procNameText.getText())) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		});

		if (isCommentSupport) {
			final Label procDescriptionLabel = new Label(composite, SWT.NONE);
			procDescriptionLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
			procDescriptionLabel.setText(Messages.lblProcedureDescription);

			procDescriptionText = new Text(composite, SWT.BORDER);
			procDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
			procDescriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}

		final String[] userColumnNameArr = isCommentSupport
				? new String[] {
					Messages.tblColProcedureParamName,
					Messages.tblColProcedureParamType,
					Messages.tblColProcedureJavaParamType,
					Messages.tblColProcedureModel,
					Messages.tblColProcedureMemo }
				: new String[] {
					Messages.tblColProcedureParamName,
					Messages.tblColProcedureParamType,
					Messages.tblColProcedureJavaParamType,
					Messages.tblColProcedureModel };
		procParamsTableViewer = CommonUITool.createCommonTableViewer(composite,
				null, userColumnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 6, 4, -1, 200));
		procParamsTable = procParamsTableViewer.getTable();
		procParamsTableViewer.getTable().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
			}

			public void widgetSelected(SelectionEvent event) {
				if (procParamsTableViewer.getTable().getSelectionCount() > 0) {
					getButton(BUTTON_EDIT_ID).setEnabled(true);
					getButton(BUTTON_UP_ID).setEnabled(true);
					getButton(BUTTON_DOWN_ID).setEnabled(true);
					getButton(BUTTON_DROP_ID).setEnabled(true);
				}
			}

		});
		procParamsTableViewer.setInput(procParamsListData);
		procParamsTable.setLinesVisible(true);
		procParamsTable.setHeaderVisible(true);
		procParamsTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// setBtnEnableDisable();
			}
		});

		barComp = new Composite(composite, SWT.NONE);
		final GridData gdbarComp = new GridData(GridData.FILL_HORIZONTAL);
		gdbarComp.horizontalSpan = 2;
		barComp.setLayoutData(gdbarComp);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		barComp.setLayout(layout);

		final Label javaNameLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		javaNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		javaNameLabel.setText(Messages.lblJavaFunctionName);

		javaNameText = new Text(composite, SWT.BORDER);
		GridData gdJavaNameText = new GridData(GridData.FILL_HORIZONTAL);
		javaNameText.setLayoutData(gdJavaNameText);

		return composite;
	}

	/**
	 * Create the sql script composite
	 *
	 * @return the composite
	 */
	private Composite createSqlScriptComposite() {
		final Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		sqlScriptText = new StyledText(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
		CommonUITool.registerContextMenu(sqlScriptText, false);
		sqlScriptText.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(layout);

		return composite;
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		CommonUITool.centerShell(getShell());
		if (isNewFlag()) {
			getShell().setText(Messages.titleAddProcedureDialog);
		} else {
			getShell().setText(Messages.titleEditProcedureDialog);
		}
	}

	/**
	 * Create buttons for button bar
	 *
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(barComp, BUTTON_ADD_ID, Messages.btnAddParameter, true);
		createButton(barComp, BUTTON_EDIT_ID, Messages.btnEditParameter, true);
		createButton(barComp, BUTTON_DROP_ID, Messages.btnDropParameter, true);
		createButton(barComp, BUTTON_UP_ID, Messages.btnUpParameter, true);
		createButton(barComp, BUTTON_DOWN_ID, Messages.btnDownParameter, true);
		createButton(parent, IDialogConstants.OK_ID, com.cubrid.common.ui.common.Messages.btnOK, true);

		getButton(BUTTON_EDIT_ID).setEnabled(false);
		getButton(BUTTON_UP_ID).setEnabled(false);
		getButton(BUTTON_DOWN_ID).setEnabled(false);
		getButton(BUTTON_DROP_ID).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, com.cubrid.common.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When press button,call it
	 *
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == BUTTON_ADD_ID) {
			Map<String, String> model = new HashMap<String, String>();

			try {
				AddFuncParamsDialog addDlg = new AddFuncParamsDialog(
						getShell(), model, sqlTypeMap, javaTypeMap, true, procParamsListData,
						database);
				if (addDlg.open() == IDialogConstants.OK_ID) { // add
					procParamsListData.add(model);
					procParamsTableViewer.refresh();
					for (int i = 0; i < procParamsTableViewer.getTable().getColumnCount(); i++) {
						procParamsTableViewer.getTable().getColumn(i).pack();
					}
				}
			} catch (Exception e) {
				LOGGER.error("", e);
			}

		} else if (buttonId == BUTTON_EDIT_ID) { // edit
			int index = procParamsTable.getSelectionIndex();
			if (index < 0) {
				return;
			}
			Map<String, String> map = procParamsListData.get(index);
			AddFuncParamsDialog editDlg = new AddFuncParamsDialog(getShell(),
					map, sqlTypeMap, javaTypeMap, false, procParamsListData,
					database);
			if (editDlg.open() == IDialogConstants.OK_ID) {
				procParamsTableViewer.refresh();
				for (int i = 0; i < procParamsTableViewer.getTable().getColumnCount(); i++) {
					procParamsTableViewer.getTable().getColumn(i).pack();
				}
			}
		} else if (buttonId == BUTTON_UP_ID) { // up
			int index = procParamsTable.getSelectionIndex();
			if (index <= 0) {
				return;
			}
			Map<String, String> map = procParamsListData.get(index);
			Map<String, String> preMap = procParamsListData.get(index - 1);
			procParamsListData.set(index - 1, map);
			procParamsListData.set(index, preMap);
			procParamsTableViewer.refresh();
		} else if (buttonId == BUTTON_DOWN_ID) { // down
			int index = procParamsTable.getSelectionIndex();
			if (index < 0 || index >= procParamsListData.size() - 1) {
				return;
			}
			Map<String, String> map = procParamsListData.get(index);
			Map<String, String> nextMap = procParamsListData.get(index + 1);
			procParamsListData.set(index + 1, map);
			procParamsListData.set(index, nextMap);
			procParamsTableViewer.refresh();
		} else if (buttonId == BUTTON_DROP_ID) { // drop
			int index = procParamsTable.getSelectionIndex();
			if (index < 0) {
				return;
			}
			procParamsListData.remove(index);
			procParamsTableViewer.refresh();
			getButton(BUTTON_EDIT_ID).setEnabled(false);
			getButton(BUTTON_UP_ID).setEnabled(false);
			getButton(BUTTON_DOWN_ID).setEnabled(false);
			getButton(BUTTON_DROP_ID).setEnabled(false);
		} else if (buttonId == IDialogConstants.OK_ID) {
			if (valid()) {
				procedureName = procNameText.getText();
				CommonSQLExcuterTask task = new CommonSQLExcuterTask(database.getDatabaseInfo());
				if (!newFlag) {
					String dropSql = "DROP PROCEDURE " + QuerySyntax.escapeKeyword(procNameText.getText());
					task.addSqls(dropSql);
				}
				task.addSqls(getSQLScript());
				execute(IDialogConstants.OK_ID, new ITask[]{task });
			}

			return;
		}

		super.buttonPressed(buttonId);
	}

	/**
	 * Initial data
	 */
	private void initialize() {

		if (!newFlag && spInfo != null) {
			procNameText.setEnabled(false);
			String target = spInfo.getTarget();
			String[] javaParamType = null;

			procNameText.setText(spInfo.getSpName());

			if (isCommentSupport
					&& StringUtil.isNotEmpty(spInfo.getDescription())) {
				procDescriptionText.setText(spInfo.getDescription());
			}

			if (target != null && target.length() > 0
					&& target.indexOf("(") > 0 && target.indexOf(")") > 0) {

				String javaFuncName = target.substring(0, target.indexOf("(")).trim();

				javaParamType = (target.substring(target.indexOf("(") + 1,
						target.indexOf(")"))).split(",");

				javaNameText.setText(javaFuncName);

			}

			List<SPArgsInfo> argsInfoList = spInfo.getArgsInfoList();
			if (javaParamType == null
					|| argsInfoList.size() != javaParamType.length) {
				return;
			}
			while (!procParamsListData.isEmpty()) {
				procParamsListData.remove(0);
			}

			for (int i = 0; i < javaParamType.length; i++) {
				for (SPArgsInfo spArgsInfo : argsInfoList) {
					if (spArgsInfo.getIndex() == i) {
						Map<String, String> model = new HashMap<String, String>();
						model.put("0", spArgsInfo.getArgName());
						model.put("1", spArgsInfo.getDataType());
						model.put("2", javaParamType[i]);
						model.put("3", spArgsInfo.getSpArgsType().toString());
						if (isCommentSupport) {
							model.put("4", spArgsInfo.getDescription());
						}
						procParamsListData.add(model);
					}
				}
			}
			procParamsTableViewer.refresh();
			for (int i = 0; i < procParamsTableViewer.getTable().getColumnCount(); i++) {
				procParamsTableViewer.getTable().getColumn(i).pack();
			}
		}
	}

	/**
	 * Check the data validation
	 *
	 * @return <code>true</code> if it is valid;<code>false</code>otherwise
	 */
	public boolean valid() {
		if (newFlag) {
			if (procNameText.getText() == null || StringUtil.isEmpty(procNameText.getText())) {
				CommonUITool.openErrorBox(getShell(), Messages.errInputProcedureName);
				return false;
			}
			if (!ValidateUtil.isValidIdentifier(procNameText.getText())) {
				CommonUITool.openErrorBox(getShell(),
						Messages.bind(Messages.errInputParameterNameValid, procNameText.getText()));
				return false;
			}
			if (procNameText.getText().length() > ValidateUtil.MAX_SCHEMA_NAME_LENGTH) {
				CommonUITool.openErrorBox(Messages.bind(Messages.errInputFunctionNameLength, "procedure",
						ValidateUtil.MAX_SCHEMA_NAME_LENGTH));
				return false;
			}
		}
		String javaName = javaNameText.getText();
		if (javaName == null || "".equals(javaName)) {
			CommonUITool.openErrorBox(getShell(), Messages.errInputJavaProcedureName);
			return false;
		}

		String[] javaNames = javaName.split("\\.");
		boolean isValidJavaName = true;
		if (javaNames == null || javaNames.length != 2) {
			isValidJavaName = false;
		}
		for (int i = 0; isValidJavaName && i < javaNames.length; i++) {
			if (!javaNames[i].matches("^[a-zA-Z_\\$].*")) {
				isValidJavaName = false;
				break;
			}
			for (int j = 0; isValidJavaName && j < javaNames[i].length(); j++) {
				if (!Character.isJavaIdentifierPart(javaNames[i].charAt(j))) {
					isValidJavaName = false;
					break;
				}
			}
		}
		if (!isValidJavaName) {
			CommonUITool.openErrorBox(getShell(), Messages.errValidJavaFunctionName);
			return false;
		}

		return true;
	}

	/**
	 * Get added CubridDatabase
	 *
	 * @return the CubridDatabase object
	 */
	public CubridDatabase getDatabase() {
		return database;
	}

	/**
	 * Set edited CubridDatabase
	 *
	 * @param database the CubridDatabase object
	 */
	public void setDatabase(CubridDatabase database) {
		this.database = database;
	}

	/**
	 * Execute tasks
	 *
	 * @param buttonId the button id
	 * @param tasks the tasks array
	 */
	public void execute(final int buttonId, final ITask[] tasks) {
		TaskExecutor taskExcutor = new CommonTaskExec(null);
		taskExcutor.setTask(tasks);
		new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
		if (taskExcutor.isSuccess() && buttonId >= 0) {
			setReturnCode(buttonId);
			close();
		}
	}

	public boolean isNewFlag() {
		return newFlag;
	}

	public void setNewFlag(boolean newFlag) {
		this.newFlag = newFlag;
	}

	static class ExTableLabelProvider extends TableLabelProvider {
		@SuppressWarnings("unchecked")
		public Image getColumnImage(Object element, int columnIndex) {
			Map<String, Object> item = (Map<String, Object>) element;
			if (columnIndex > 0) {
				Boolean flag = (Boolean) item.get("" + columnIndex);
				return flag ? CommonUIPlugin.getImage("icons/checked.gif")
						: CommonUIPlugin.getImage("icons/unchecked.gif");

			}
			return null;
		}

		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof Map)) {
				return "";
			}
			if (columnIndex != 0) {
				return "";
			}
			Map<String, Object> map = (Map<String, Object>) element;
			return map.get("" + columnIndex).toString();
		}
	}

	private String getSQLScript() { // FIXME move this logic to core module
		StringBuffer sb = new StringBuffer();
		StringBuffer javaSb = new StringBuffer();
		sb.append("CREATE").append(" PROCEDURE ");
		String procedureName = procNameText.getText();
		if (procedureName == null) {
			procedureName = "";
		}
		sb.append(QuerySyntax.escapeKeyword(procedureName)).append("(");
		for (Map<String, String> map : procParamsListData) {
			// "PARAMS_INDEX", "PARAM_NAME", "PARAM_TYPE", "JAVA_PARAM_TYPE"
			String name = map.get("0");
			String type = map.get("1");
			String javaType = map.get("2");
			String paramModel = map.get("3");
			String description = map.get("4");
			sb.append(QuerySyntax.escapeKeyword(name)).append(" ");
			if (!paramModel.equalsIgnoreCase(SPArgsType.IN.toString())) {
				sb.append(paramModel).append(" ");
			}
			sb.append(type);
			if (isCommentSupport && StringUtil.isNotEmpty(description)) {
				description = String.format("'%s'", description);
				sb.append(String.format(" COMMENT %s", StringUtil.escapeQuotes(description)));
			}
			sb.append(",");
			javaSb.append(javaType).append(",");
		}
		if (!procParamsListData.isEmpty()) {
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			if (javaSb.length() > 0) {
				javaSb.deleteCharAt(javaSb.length() - 1);
			}
		}
		sb.append(")");

		sb.append(StringUtil.NEWLINE).append("AS LANGUAGE JAVA ").append(StringUtil.NEWLINE);
		String javaFuncName = javaNameText.getText();
		if (javaFuncName == null) {
			javaFuncName = "";
		}
		sb.append("NAME '").append(javaFuncName).append("(").append(javaSb).append(")").append("'");
		if (isCommentSupport) {
			String description = procDescriptionText.getText();
			if (StringUtil.isNotEmpty(description)) {
				description = String.format("'%s'", description);
				sb.append(String.format(" COMMENT %s", StringUtil.escapeQuotes(description)));
			}
		}
		return formatSql(sb.toString());
	}

	/**
	 * Init the sql type map
	 */
	private void initSqlTypeMap() {
		if (sqlTypeMap == null) {
			sqlTypeMap = new TreeMap<String, String>();
		}
		sqlTypeMap.put("--void--", "0");
		FieldHandlerUtils.initSqlTypeMap(sqlTypeMap);
	}

	/**
	 * Init the java type map
	 */
	private void initJavaType() { // FIXME move this logic to core module
		if (javaTypeMap == null) {
			javaTypeMap = new TreeMap<String, List<String>>();
		}
		if (!javaTypeMap.containsKey("1")) {
			List<String> list = new ArrayList<String>();
			list.add("java.lang.String");
			list.add("java.sql.Date");
			list.add("java.sql.Time");
			list.add("java.sql.Timestamp");
			list.add("java.lang.Byte");
			list.add("java.lang.Short");
			list.add("java.lang.Integer");
			list.add("java.lang.Long");
			list.add("java.lang.Float");
			list.add("java.lang.Double");
			list.add("java.math.BigDecimal");
			list.add("byte");
			list.add("short");
			list.add("int");
			list.add("long");
			list.add("float");
			list.add("double");
			javaTypeMap.put("1", list);
		}
		if (!javaTypeMap.containsKey("2")) {
			List<String> list = new ArrayList<String>();
			list.add("java.lang.Byte");
			list.add("java.lang.Short");
			list.add("java.lang.Integer");
			list.add("java.lang.Long");
			list.add("java.lang.Float");
			list.add("java.lang.Double");
			list.add("java.math.BigDecimal");
			list.add("java.lang.String");
			list.add("byte");
			list.add("short");
			list.add("int");
			list.add("long");
			list.add("float");
			list.add("double");
			javaTypeMap.put("2", list);
		}
		if (!javaTypeMap.containsKey("3")) {
			List<String> list = new ArrayList<String>();
			list.add("java.sql.Date");
			list.add("java.sql.Time");
			list.add("java.sql.Timestamp");
			list.add("java.lang.String");
			javaTypeMap.put("3", list);
		}
		if (!javaTypeMap.containsKey("4")) {
			List<String> list = new ArrayList<String>();
			javaTypeMap.put("4", list);
		}
		if (!javaTypeMap.containsKey("5")) {
			List<String> list = new ArrayList<String>();
			list.add("cubrid.sql.CUBRIDOID");
			javaTypeMap.put("5", list);
		}
		if (!javaTypeMap.containsKey("6")) {
			List<String> list = new ArrayList<String>();
			list.add("cubrid.jdbc.driver.CUBRIDResultSet");
			javaTypeMap.put("6", list);
		}
	}

	public SPInfo getSpInfo() {
		return spInfo;
	}

	public void setSpInfo(SPInfo spInfo) {
		this.spInfo = spInfo;
	}

	/**
	 * Format the sql script
	 *
	 * @param sql the sql
	 * @return the formated sql
	 */
	private String formatSql(String sql) {
		String sqlStr = formator.format(sql + ";");
		if (sqlStr == null) {
			LOGGER.error("The formator.format(sql) is a null.");
			return null;
		}

		String trimmedSql = sqlStr.trim();
		if (!trimmedSql.endsWith(";")) {
			return "";
		}

		String formattedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
		return formattedSql;
	}

	public String getProcedureName() {
		return procedureName;
	}

}
