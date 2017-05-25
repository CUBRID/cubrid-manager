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
package com.cubrid.common.ui.cubrid.table.dialog;

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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.CommentType;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.task.CommonSQLExcuterTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassAuthorizations;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.task.AnalyseSqlTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetViewAllColumnsTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.GetUserAuthorizationsTask;

/**
 * The dialog of create view
 *
 * @author robin 2009-6-4
 */
public class CreateViewDialog extends
		CMTitleAreaDialog {
	private static final Logger LOGGER = LogUtil.getLogger(CreateViewDialog.class);

	private static final String DATATYPE_VARBIT = "VARBIT";
	private static final String DATATYPE_VARNCHAR = "VARNCHAR";
	public List<Map<String, String>> queryListData = new ArrayList<Map<String, String>>();
	public TableViewer queryTableViewer;
	private static SqlFormattingStrategy formator = new SqlFormattingStrategy();
	private final List<Map<String, String>> viewColListData = new ArrayList<Map<String, String>>();
	private TableViewer viewColTableViewer;
	private boolean isPropertyQuery = false;
	private Composite barComp;
	private Composite parentComp;
	private StyledText sqlText;
	private Combo ownerCombo;
	private Text tableText;
	private Text querydescText;
	private Text viewDescriptionText;
	private final CubridDatabase database;
	private TabFolder tabFolder = null;
	private final boolean isNewTableFlag;
	private final String[] defaultType = { "", "shared", "default" };
	private ClassInfo classInfo = null;
	private List<String> vclassList = null;
	private List<DBAttribute> attrList = null;
	private List<String> dbUserList = null;
	private static final int BUTTON_ADD_ID = 1001;
	private static final int BUTTON_DROP_ID = 1002;
	private static final int BUTTON_EDIT_ID = 1003;
	private String owner;
	private String viewName = "";
	private boolean isCommentSupport = false;

	public CreateViewDialog(Shell parentShell, CubridDatabase database, boolean isNew) {
		super(parentShell);
		this.database = database;
		this.isNewTableFlag = isNew;
		this.isCommentSupport = CompatibleUtil.isCommentSupports(database.getDatabaseInfo());
	}

	protected Control createDialogArea(Composite parent) {
		parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);
		{
			final GridData gdComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
			composite.setLayoutData(gdComposite);
			GridLayout layout = new GridLayout();
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			layout.numColumns = 1;
			composite.setLayout(layout);
		}

		tabFolder = new TabFolder(composite, SWT.NONE);
		final GridData gdTabFolder = new GridData(SWT.FILL, SWT.FILL, true, true);
		tabFolder.setLayoutData(gdTabFolder);

		final TabItem generalTabItem = new TabItem(tabFolder, SWT.NONE);
		generalTabItem.setText(Messages.tabItemGeneral);

		final Composite compositeGenaral = createGeneralComposite();
		generalTabItem.setControl(compositeGenaral);

		final TabItem ddlTabItem = new TabItem(tabFolder, SWT.NONE);
		ddlTabItem.setText(Messages.tabItemSQLScript);

		final Composite sqlScriptComp = createSQLScriptComposite();
		ddlTabItem.setControl(sqlScriptComp);

		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				boolean isFirstTab = tabFolder.getSelectionIndex() == 0;
				boolean isLastTab = tabFolder.getSelectionIndex() == tabFolder.getItemCount() - 1;
				if (isFirstTab) {
					tableText.setFocus();
				} else if (isLastTab) {
					StringBuilder sb = new StringBuilder();
					String dropSql = makeDropSQLScript();
					if (dropSql.length() > 0) {
						sb.append(dropSql);
						sb.append(StringUtil.NEWLINE);
						sb.append(StringUtil.NEWLINE);
						sb.append(StringUtil.NEWLINE);
					}
					sb.append(makeCreateSQLScript());

					String ownerNew = ownerCombo.getText();
					String ownerOld = null;
					if (isNewTableFlag) {
						DatabaseInfo dbInfo = database.getDatabaseInfo();
						ownerOld = dbInfo.getAuthLoginedDbUserInfo().getName();
					} else {
						ownerOld = classInfo.getOwnerName();
					}
					boolean isSameUser = StringUtil.isEqualIgnoreCase(ownerOld, ownerNew);
					if (!isSameUser) {
						sb.append(";");
						sb.append(StringUtil.NEWLINE);
						sb.append(StringUtil.NEWLINE);
						sb.append(StringUtil.NEWLINE);
						sb.append(makeChangeOwnerSQLScript()).append(";");
						sb.append(StringUtil.NEWLINE);
					}

					sqlText.setText(formatSql(sb.toString()));
				}
			}
		});

		init();
		tableText.setFocus();

		return parent;
	}

	/**
	 * create SQL Script Composite
	 *
	 * @return composite
	 */
	private Composite createSQLScriptComposite() {
		final Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout());
		sqlText = new StyledText(composite, SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		CommonUITool.registerContextMenu(sqlText, false);
		final GridData gdSqlText = new GridData(SWT.FILL, SWT.FILL, true, true);
		sqlText.setLayoutData(gdSqlText);

		return composite;
	}

	/**
	 * create General Composite
	 *
	 * @return composite
	 */
	private Composite createGeneralComposite() {
		final Composite composite = new Composite(tabFolder, SWT.NONE);
		{
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.makeColumnsEqualWidth = true;
			composite.setLayout(gridLayout);
		}

		final Group group = new Group(composite, SWT.NONE);
		{
			GridData gdGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdGroup.horizontalSpan = 2;
			group.setLayoutData(gdGroup);
			final GridLayout gridLayout1 = new GridLayout();
			gridLayout1.numColumns = 2;
			group.setLayout(gridLayout1);
		}

		final Label tableNameLabel = new Label(group, SWT.SHADOW_IN);
		tableNameLabel.setText(Messages.lblViewName);

		tableText = new Text(group, SWT.BORDER);
		tableText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
		final GridData gdTableText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdTableText.horizontalIndent = 30;
		tableText.setLayoutData(gdTableText);

		if (isCommentSupport) {
			final Label viewDescriptionLabel = new Label(group, SWT.SHADOW_IN);
			viewDescriptionLabel.setText(Messages.lblViewDescription);
			viewDescriptionText = new Text(group, SWT.BORDER);
			viewDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
			final GridData gdViewDescription = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdViewDescription.horizontalIndent = 30;
			viewDescriptionText.setLayoutData(gdViewDescription);
			viewDescriptionText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent event) {
					if (getButton(IDialogConstants.OK_ID) != null) {
						getButton(IDialogConstants.OK_ID).setEnabled(true);
					}
				}
			});
		}

		final Label ownerLabel = new Label(group, SWT.NONE);
		ownerLabel.setText(Messages.lblViewOwnerName);

		ownerCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData gdCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdCombo.horizontalIndent = 30;
		ownerCombo.setLayoutData(gdCombo);
		ownerCombo.setVisibleItemCount(10);
		ownerCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if (getButton(IDialogConstants.OK_ID) != null) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}
		});

		final Label querySQLLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		querySQLLabel.setText(Messages.lblQueryList);
		querySQLLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		final Label sqlDescLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		sqlDescLabel.setText(Messages.lblSelectQueryList);
		sqlDescLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		final String[] columnNameArr1 = new String[] { "col1" };
		queryTableViewer = CommonUITool.createCommonTableViewer(composite, null, columnNameArr1,
				CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 100));
		queryTableViewer.getTable().setHeaderVisible(false);
		queryTableViewer.setInput(queryListData);
		queryTableViewer.getTable().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
			}

			public void widgetSelected(SelectionEvent event) {
				int index = queryTableViewer.getTable().getSelectionIndex();
				String value = queryListData.get(index).get("0");
				if (value != null) {
					querydescText.setText(formatSql(value));
				}

				setButtonStatus();
			}
		});
		queryTableViewer.getTable().getColumn(0).setWidth(200);
		querydescText = new Text(composite, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL);
		querydescText.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 200));

		barComp = new Composite(composite, SWT.NONE);
		{
			final GridData gdbarComp = new GridData(GridData.FILL_HORIZONTAL);
			gdbarComp.horizontalSpan = 2;
			barComp.setLayoutData(gdbarComp);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 10;
			layout.marginHeight = 10;
			barComp.setLayout(layout);
		}

		final Label columnsLabel = new Label(composite, SWT.NONE);
		columnsLabel.setText(Messages.lblTableNameColumns);
		final String[] columnNameArr = isCommentSupport
				? new String[] {
					Messages.tblColViewName,
					Messages.tblColViewDataType,
					Messages.tblColViewDefaultType,
					Messages.tblColViewDefaultValue,
					Messages.tblColViewMemo}
				: new String[] {
					Messages.tblColViewName,
					Messages.tblColViewDataType,
					Messages.tblColViewDefaultType,
					Messages.tblColViewDefaultValue};

		viewColTableViewer = createCommonTableViewer(composite, null, columnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 2, 4, -1, 200));
		viewColTableViewer.setInput(viewColListData);
		viewColTableViewer.setColumnProperties(columnNameArr);

		CellEditor[] editors = new CellEditor[5];
		editors[0] = new TextCellEditor(viewColTableViewer.getTable());
		editors[1] = null;
		editors[2] = new ComboBoxCellEditor(viewColTableViewer.getTable(), defaultType,
				SWT.READ_ONLY);
		editors[3] = new TextCellEditor(viewColTableViewer.getTable());
		if (isCommentSupport) {
			editors[4] = new TextCellEditor(viewColTableViewer.getTable());
		}

		viewColTableViewer.setCellEditors(editors);
		viewColTableViewer.setCellModifier(new ICellModifier() {
			@SuppressWarnings("unchecked")
			public boolean canModify(Object element, String property) {
				if (isPropertyQuery || property.equals(columnNameArr[1])) {
					return false;
				}

				Map<String, String> map = (Map<String, String>) element;
				if (property.equals(columnNameArr[3])) {
					String defaultTypeStr = map.get("2");
					if (defaultTypeStr == null || defaultType[0].equals(defaultTypeStr)) {
						return false;
					}
				}

				return true;
			}

			@SuppressWarnings("unchecked")
			public Object getValue(Object element, String property) {
				Map<String, String> map = (Map<String, String>) element;
				if (property.equals(columnNameArr[0])) {
					return map.get("0");
				} else if (property.equals(columnNameArr[2])) {
					String str = map.get("2");
					int index = 0;
					if (str != null) {
						for (int i = 0; defaultType != null && i < defaultType.length; i++) {
							if (str.equals(defaultType[i])) {
								index = i;
								break;
							}
						}
					}

					return Integer.valueOf(index);
				} else if (property.equals(columnNameArr[3])) {
					String type = map.get("1");
					String value = map.get("3");
					if (value == null) {
						value = "";
					}

					if (type != null
							&& (type.startsWith(DataType.DATATYPE_CHAR)
									|| type.startsWith(DataType.DATATYPE_STRING) || type.startsWith(DataType.DATATYPE_VARCHAR))
							&& (value.startsWith("'") && value.endsWith("'") && value.length() > 1)) {
						value = value.substring(1, value.length() - 1);
					}

					return value;
				} else if (isCommentSupport && property.equals(columnNameArr[4])) {
					return map.get("4");
				}

				return null;
			}

			@SuppressWarnings("unchecked")
			public void modify(Object element, String property, Object value) {
				Object elementData = null;
				if (element instanceof Item) {
					elementData = ((Item) element).getData();
				}

				Map<String, String> map = (Map<String, String>) elementData;
				if (map == null) {
					return;
				}

				String type = map.get("1");
				if (property.equals(columnNameArr[0])) {
					map.put("0", value.toString());
				} else if (property.equals(columnNameArr[2])) {
					int index = Integer.parseInt(value.toString());
					if (index == 0) {
						map.put("3", "");
					}
					map.put("2", defaultType[index]);
				} else if (property.equals(columnNameArr[3])) {
					String val = FieldHandlerUtils.getValidDefaultValue(value, type);
					if (val != null) {
						map.put("3", val);
					}
				} else if (isCommentSupport && property.equals(columnNameArr[4])) {
					map.put("4", value.toString());
				}
				viewColTableViewer.refresh();
				valid();
			}
		});

		return composite;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == BUTTON_ADD_ID) { // add
			AddQueryDialog addDlg = new AddQueryDialog(parentComp.getShell(), true, -1, this);
			if (addDlg.open() == IDialogConstants.OK_ID) {
				queryTableViewer.getTable().setSelection(
						queryTableViewer.getTable().getItemCount() - 1);
				querydescText.setText(formatSql(queryTableViewer.getTable().getItem(
						queryTableViewer.getTable().getItemCount() - 1).getText()));
			}
			queryTableViewer.getTable().getColumn(0).setWidth(200);
			setButtonStatus();
			valid();
			return;
		} else if (buttonId == BUTTON_DROP_ID) { // delete
			int index = queryTableViewer.getTable().getSelectionIndex();
			queryListData.remove(index);
			queryTableViewer.refresh();
			if (queryListData.size() > index) {
				queryTableViewer.getTable().setSelection(index);
				querydescText.setText(formatSql(queryTableViewer.getTable().getItem(index).getText()));
			} else if (index > 0) {
				queryTableViewer.getTable().setSelection(index - 1);
				querydescText.setText(formatSql(queryTableViewer.getTable().getItem(index - 1).getText()));
			} else {
				queryTableViewer.getTable().setSelection(index - 1);
				querydescText.setText("");
			}

			validateResult(null, false, -1);
			setButtonStatus();
			valid();
			return;
		} else if (buttonId == BUTTON_EDIT_ID) {
			StringBuffer sb = new StringBuffer();
			int index = queryTableViewer.getTable().getSelectionIndex();

			sb.append(queryListData.get(index).get("0"));

			AddQueryDialog addDlg = new AddQueryDialog(parentComp.getShell(), false, index, this);
			if (addDlg.open() == IDialogConstants.OK_ID) { // add
				queryTableViewer.getTable().setSelection(index);
				String sql = queryTableViewer.getTable().getItem(index).getText();
				querydescText.setText(formatSql(sql));
			}
			setButtonStatus();
			valid();
			return;
		} else if (buttonId == IDialogConstants.OK_ID) {
			if (!valid()) {
				return;
			}

			DatabaseInfo databaseInfo = database.getDatabaseInfo();

			owner = ownerCombo.getText();
			Map<String, ClassAuthorizations> authMap = getDropViewAuthorizations();
			CommonSQLExcuterTask task = new CommonSQLExcuterTask(database.getDatabaseInfo());
			String dropSql = makeDropSQLScript();
			if (dropSql.length() > 0) {
				dropSql = DatabaseInfo.wrapShardQuery(databaseInfo, dropSql);
				task.addSqls(dropSql);
			}

			String sql = makeCreateSQLScript();

			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);

			DatabaseInfo dbInfo = database.getDatabaseInfo();
			String ownerOld = dbInfo.getAuthLoginedDbUserInfo().getName();
			String ownerNew = ownerCombo.getText();
			boolean isSameOwner = StringUtil.isEqualIgnoreCase(ownerOld, ownerNew);
			if (!isSameOwner) {
				sql = makeChangeOwnerSQLScript();
				sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
				task.addCallSqls(sql);
			}

			addGrantAuthSQLScriptToTask(authMap, tableText.getText(), task);

			execTask(-1, new ITask[] { task }, true, getParentShell());
			if (task.getErrorMsg() != null) {
				return;
			}

			String title = com.cubrid.common.ui.common.Messages.titleSuccess;
			String message = isNewTableFlag ? Messages.msgSuccessCreateView
					: Messages.msgSuccessEditView;
			CommonUITool.openInformationBox(title, message);
		}
		viewName = tableText.getText();

		super.buttonPressed(buttonId);
	}

	/**
	 * initializes some values
	 */
	private void init() {
		String ownerOld = null;
		fillOwnerCombo();
		if (isNewTableFlag) {
			tableText.setText("");
			tableText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					valid();
				}
			});
			ownerOld = database.getUserName();
			setTitle(Messages.newViewMsgTitle);
			setMessage(Messages.newViewMsg);
			getShell().setText(Messages.newViewShellTitle);
		} else {
			if (classInfo == null) {
				return;
			}
			tableText.setEditable(false);
			tableText.setText(classInfo.getClassName());

			if (isCommentSupport) {
				if (!classInfo.isSystemClass()) {
					String comment = getViewComment();
					if (comment != null) {
						viewDescriptionText.setText(comment);
					}
				} else {
					viewDescriptionText.setEditable(false);
				}
			}

			ownerOld = classInfo.getOwnerName();
			String[] strs = new String[] { classInfo.getClassName(),
					isPropertyQuery ? Messages.msgPropertyInfo : Messages.msgEditInfo };
			setTitle(Messages.bind(Messages.editViewMsgTitle, strs));
			setMessage(Messages.editViewMsg);
			strs = new String[] { classInfo.getClassName(),
					isPropertyQuery ? Messages.msgPropertyInfo : Messages.msgEditInfo };
			String title = Messages.bind(Messages.editViewShellTitle, strs);
			getShell().setText(title);
			for (String sql : vclassList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("0", sql);
				queryListData.add(map);
			}
			if (!vclassList.isEmpty()) {
				querydescText.setText(formatSql(vclassList.get(0)));
			}

			// "Name", "Data type", "Default type", "Default value"
			for (DBAttribute attr : attrList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("0", attr.getName());
				String type = attr.getType();
				if (type.startsWith(DATATYPE_VARNCHAR)) {
					type = type.replaceAll(DATATYPE_VARNCHAR, DataType.DATATYPE_NCHAR_VARYING);
				}
				if (type.startsWith(DATATYPE_VARBIT)) {
					type = type.replaceAll(DATATYPE_VARBIT, DataType.DATATYPE_BIT_VARYING);
				}
				if (DataType.DATATYPE_OBJECT.equalsIgnoreCase(type)) {
					if (attr.getDomainClassName() == null || "".equals(attr.getDomainClassName())) {
						type = DataType.DATATYPE_OBJECT;
					} else {
						type = attr.getDomainClassName();
					}
				}
				map.put("1", type);
				map.put("2", defaultType[0]);
				map.put("3", defaultType[0]);

				if (isCommentSupport) {
					map.put("4", attr.getDescription());
				}

				String dfltType = null;
				String value = null;
				if (attr.getDefault() != null && !attr.getDefault().equals("")) {
					if (attr.isShared()) {
						dfltType = defaultType[1];
					} else {
						dfltType = defaultType[2];
					}
					value = attr.getDefault();
				}
				if (value == null) {
					value = "";
				}
				if (type != null
						&& (type.startsWith(DataType.DATATYPE_CHAR)
								|| type.startsWith(DataType.DATATYPE_STRING) || type.startsWith(DataType.DATATYPE_VARCHAR))
						&& (value.startsWith("'") && value.endsWith("'") && value.length() > 1)) {
					value = value.substring(1, value.length() - 1);
				}
				map.put("2", dfltType);
				map.put("3", value);
				viewColListData.add(map);
			}
			viewColTableViewer.refresh();
			for (int i = 0; i < viewColTableViewer.getTable().getColumnCount(); i++) {
				viewColTableViewer.getTable().getColumn(i).pack();
			}
			queryTableViewer.getTable().select(0);

		}

		for (int i = 0; i < ownerCombo.getItemCount(); i++) {
			String ownerNew = ownerCombo.getItem(i);
			boolean isSame = StringUtil.isEqualIgnoreCase(ownerNew, ownerOld);
			if (isSame) {
				ownerCombo.select(i);
				break;
			}
		}
		queryTableViewer.refresh();
	}

	/**
	 * fill the owner combo.
	 */
	private void fillOwnerCombo() {
		if (dbUserList == null) {
			return;
		}
		for (String userName : dbUserList) {
			ownerCombo.add(userName.toUpperCase(Locale.getDefault()));
		}
	}

	/**
	 * get view's comment
	 *
	 * @return
	 */
	private String getViewComment() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		SchemaComment schemaComment = null;
		String viewName = tableText.getText();

		try {
			DatabaseInfo dbInfo = database.getDatabaseInfo();
			conn = JDBCConnectionManager.getConnection(dbInfo, true);
			schemaComment = SchemaCommentHandler.loadObjectDescription(
					dbInfo, conn, viewName, CommentType.VIEW);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			CommonUITool.openErrorBox(e.getMessage());
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		return schemaComment.getDescription();
	}

	/**
	 * execute the task.
	 *
	 * @param buttonId int
	 * @param tasks ITask[]
	 * @param cancelable boolean
	 * @param shell Shell
	 */
	public void execTask(final int buttonId, final ITask[] tasks, boolean cancelable,
			final Shell shell) {
		if (tasks == null || tasks.length == 0) {
			return;
		}

		TaskExecutor taskExecutor = new TaskExecutor() {
			public boolean exec(final IProgressMonitor monitor) {
				if (monitor.isCanceled()) {
					return false;
				}
				for (ITask task : tasks) {
					if (task instanceof GetAllClassListTask) {
						((GetAllClassListTask) task).getClassInfoTaskExcute();
					} else if (task instanceof GetViewAllColumnsTask) {
						((GetViewAllColumnsTask) task).getAllVclassListTaskExcute();
					} else if (task instanceof GetAllAttrTask) {
						((GetAllAttrTask) task).getAttrList();
					} else {
						task.execute();
					}
					final String msg = task.getErrorMsg();
					if (openErrorBox(shell, msg, monitor)) {
						return false;
					}
					if (monitor.isCanceled()) {
						return false;
					}
				}
				return true;
			}
		};
		taskExecutor.setTask(tasks);
		new ExecTaskWithProgress(taskExecutor).busyCursorWhile();
		if (taskExecutor.isSuccess() && buttonId > 0) {
			setReturnCode(buttonId);
			close();
		}
	}

	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setSize(600, 700);
		CommonUITool.centerShell(getShell());
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(barComp, BUTTON_ADD_ID, Messages.btnAddParameter, true);
		createButton(barComp, BUTTON_DROP_ID, Messages.btnDeleteParameter, true);
		createButton(barComp, BUTTON_EDIT_ID, Messages.btnEditParameter, true);
		createButton(parent, IDialogConstants.OK_ID, com.cubrid.common.ui.common.Messages.btnOK,
				false);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.common.ui.common.Messages.btnCancel, false);
		setButtonStatus();
	}

	/**
	 * Get drop SQL
	 *
	 * @return string
	 */
	private String makeDropSQLScript() {
		if (classInfo == null || "".equals(tableText.getText())) {
			return "";
		}
		String classNameOld = classInfo.getClassName();
		String classNameNew = tableText.getText();
		boolean isSameClass = StringUtil.isEqualIgnoreCase(classNameOld, classNameNew);
		if (isNewTableFlag || isSameClass) {
			return "";
		}

		String ddl = "DROP VIEW " + QuerySyntax.escapeKeyword(classInfo.getClassName()) + ";";
		return ddl;
	}

	/**
	 * get drop view ClassAuthorizations
	 *
	 * @return
	 */
	private Map<String, ClassAuthorizations> getDropViewAuthorizations() {
		try {
			GetUserAuthorizationsTask privilegeTask = new GetUserAuthorizationsTask(
					database.getDatabaseInfo());
			return privilegeTask.getViewAuthorizationsByViewName(ownerCombo.getText(),
					tableText.getText());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * get Create SQL Script
	 *
	 * @return string
	 */
	private String makeCreateSQLScript() { // FIXME move this logic to core module
		StringBuilder ddl = new StringBuilder();
		if (isNewTableFlag) {
			ddl.append("CREATE VIEW ");
		} else {
			boolean isSameClass = StringUtil.isEqualIgnoreCase(classInfo.getClassName(),
					tableText.getText());
			boolean canSupport = CompatibleUtil.isSupportReplaceView(database.getDatabaseInfo());
			if (canSupport && isSameClass) {
				ddl.append("CREATE OR REPLACE VIEW ");
			} else {
				ddl.append("CREATE VIEW ");
			}
		}

		String viewName = "";
		if (tableText == null || StringUtil.isEmpty(tableText.getText())) {
			ddl.append("[VIEWNAME]");
		} else {
			viewName = tableText.getText();
		}
		if (viewName != null) {
			ddl.append(QuerySyntax.escapeKeyword(viewName));
		}
		ddl.append("(");

		for (Map<String, String> map : viewColListData) {
			// "Name", "Data type", "Shared", "Default", "Default value", "comment"
			String type = map.get("1");
			ddl.append(StringUtil.NEWLINE);
			ddl.append(" ").append(QuerySyntax.escapeKeyword(map.get("0"))).append(" ");
			ddl.append(type);

			String defaultType = map.get("2");
			String defaultValue = map.get("3");
			boolean hasDefaultTypeAndValue = StringUtil.isNotEmpty(defaultType)
					&& StringUtil.isNotEmpty(defaultValue);
			if (hasDefaultTypeAndValue) {
				ddl.append(" ").append(defaultType);

				boolean isStringType = DataType.DATATYPE_CHAR.equalsIgnoreCase(type)
						|| DataType.DATATYPE_STRING.equalsIgnoreCase(type)
						|| DataType.DATATYPE_VARCHAR.equalsIgnoreCase(type);
				if (isStringType) {
					ddl.append(" '").append(defaultValue).append("'");
				} else {
					ddl.append(" ").append(defaultValue);
				}
			}

			if (isCommentSupport) {
				String comment = map.get("4");
				if (StringUtil.isNotEmpty(comment)) {
					comment = String.format("'%s'", comment);
					ddl.append(String.format(" COMMENT %s ", StringUtil.escapeQuotes(comment)));
				}
			}

			ddl.append(",");
		}

		if (!viewColListData.isEmpty() && ddl.length() > 0) {
			ddl.deleteCharAt(ddl.length() - 1);
		}
		ddl.append(")").append(StringUtil.NEWLINE);
		ddl.append("    AS ");

		for (int i = 0, total = queryListData.size(); i < total; i++) {
			Map<String, String> map = queryListData.get(i);
			ddl.append(StringUtil.NEWLINE);
			ddl.append(map.get("0"));
			if (i != total - 1) {
				ddl.append(StringUtil.NEWLINE).append(" UNION ALL ");
			}
		}

		if (isCommentSupport) {
			String comment = viewDescriptionText.getText();
			if (StringUtil.isNotEmpty(comment)) {
				comment = String.format("'%s'", comment);
				ddl.append(String.format(" COMMENT %s", StringUtil.escapeQuotes(comment)));
			}
		}
		ddl.append(";");

		return ddl.toString();
	}

	/**
	 * get grant auth Script
	 *
	 * @param Map<String, ClassAuthorizations> authMap
	 * @param String className
	 * @param CommonSQLExcuterTask task;
	 */
	private void addGrantAuthSQLScriptToTask(Map<String, ClassAuthorizations> authMap,
			String className, CommonSQLExcuterTask task) {
		if (authMap == null || className == null) {
			return;
		}

		// GRANT privilege
		for (Map.Entry<String, ClassAuthorizations> entry : authMap.entrySet()) {
			String userName = entry.getKey();
			makeGrantTasks(className, task, entry, userName);
		}
	}

	private void makeGrantTasks(String className, CommonSQLExcuterTask task,
			Map.Entry<String, ClassAuthorizations> entry, String userName) { // FIXME move this logic to core module
		Map<String, Object> revokeMap = getItemAuthMap(entry.getValue());
		DatabaseInfo databaseInfo = database.getDatabaseInfo();
		String escapedTableName = QuerySyntax.escapeKeyword(className);
		String escapedUserName = QuerySyntax.escapeKeyword(userName);

		if ((Boolean) revokeMap.get("1") && !(Boolean) revokeMap.get("8")) {
			String sql = "GRANT SELECT ON " + escapedTableName + " TO " + escapedUserName;
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		} else if ((Boolean) revokeMap.get("8")) {
			String sql = "GRANT SELECT ON " + escapedTableName + " TO " + escapedUserName
					+ " WITH GRANT OPTION";
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		}

		if ((Boolean) revokeMap.get("2") && !(Boolean) revokeMap.get("9")) {
			String sql = "GRANT INSERT ON " + escapedTableName + " TO " + escapedUserName;
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		} else if ((Boolean) revokeMap.get("9")) {
			String sql = "GRANT INSERT ON " + escapedTableName + " TO " + escapedUserName
					+ " WITH GRANT OPTION";
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		}

		if ((Boolean) revokeMap.get("3") && !(Boolean) revokeMap.get("10")) {
			String sql = "GRANT UPDATE ON " + escapedTableName + " TO " + escapedUserName;
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		} else if ((Boolean) revokeMap.get("10")) {
			String sql = "GRANT UPDATE ON " + escapedTableName + " TO " + escapedUserName
					+ " WITH GRANT OPTION";
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		}

		if ((Boolean) revokeMap.get("4") && !(Boolean) revokeMap.get("11")) {
			String sql = "GRANT DELETE ON " + escapedTableName + " TO " + escapedUserName;
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		} else if ((Boolean) revokeMap.get("11")) {
			String sql = "GRANT DELETE ON " + escapedTableName + " TO " + escapedUserName
					+ " WITH GRANT OPTION";
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		}

		if ((Boolean) revokeMap.get("5") && !(Boolean) revokeMap.get("12")) {
			String sql = "GRANT ALTER ON " + escapedTableName + " TO " + escapedUserName;
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		} else if ((Boolean) revokeMap.get("12")) {
			String sql = "GRANT ALTER ON " + escapedTableName + " TO " + escapedUserName
					+ " WITH GRANT OPTION";
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		}

		if ((Boolean) revokeMap.get("6") && !(Boolean) revokeMap.get("13")) {
			String sql = "GRANT INDEX ON " + escapedTableName + " TO " + escapedUserName;
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		} else if ((Boolean) revokeMap.get("13")) {
			String sql = "GRANT INDEX ON " + escapedTableName + " TO " + escapedUserName
					+ " WITH GRANT OPTION";
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		}

		if ((Boolean) revokeMap.get("7") && !(Boolean) revokeMap.get("14")) {
			String sql = "GRANT EXECUTE ON " + escapedTableName + " TO " + escapedUserName;
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		} else if ((Boolean) revokeMap.get("14")) {
			String sql = "GRANT EXECUTE ON " + escapedTableName + " TO " + escapedUserName + " "
					+ "WITH GRANT OPTION";
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);
			task.addSqls(sql);
		}
	}

	/**
	 * Get item map
	 *
	 * @param auth the authorization object
	 * @return the map object
	 */
	private Map<String, Object> getItemAuthMap(ClassAuthorizations auth) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("0", auth.getClassName());
		map.put("1", auth.isSelectPriv());
		map.put("2", auth.isInsertPriv());
		map.put("3", auth.isUpdatePriv());
		map.put("4", auth.isDeletePriv());
		map.put("5", auth.isAlterPriv());
		map.put("6", auth.isIndexPriv());
		map.put("7", auth.isExecutePriv());
		map.put("8", auth.isGrantSelectPriv());
		map.put("9", auth.isGrantInsertPriv());
		map.put("10", auth.isGrantUpdatePriv());
		map.put("11", auth.isGrantDeletePriv());
		map.put("12", auth.isGrantAlterPriv());
		map.put("13", auth.isGrantIndexPriv());
		map.put("14", auth.isGrantExecutePriv());
		return map;
	}

	/**
	 * get the sql of change owner
	 *
	 * @return string
	 */
	private String makeChangeOwnerSQLScript() { // FIXME move this logic to core module
		DatabaseInfo dbInfo = database.getDatabaseInfo();

		String tableName = tableText.getText();
		String ownerNew = ownerCombo.getText();
		String ownerOld = dbInfo.getAuthLoginedDbUserInfo().getName();

		if (ownerOld.equalsIgnoreCase(ownerNew)) {
			return null;
		} else if (StringUtil.isEmpty(tableName) && StringUtil.isEmpty(ownerNew)) {
			return null;
		}

		StringBuffer bf = new StringBuffer();
		bf.append("CALL CHANGE_OWNER ('");

		if (tableName != null) {
			bf.append(tableName);
		}

		bf.append("','");

		if (ownerNew != null) {
			bf.append(ownerNew);
		}

		bf.append("') ON CLASS db_authorizations");

		return bf.toString();
	}

	/**
	 * validate result
	 *
	 * @param plusSql String
	 * @param isNewSql boolean
	 * @param index int
	 * @return boolean
	 */
	public boolean validateResult(String plusSql, boolean isNewSql, int index) {
		AnalyseSqlTask task = new AnalyseSqlTask(database.getDatabaseInfo());
		Map<String, String> oldSql = null;
		if (!isNewSql && index >= 0 && index < queryListData.size()) {
			oldSql = queryListData.get(index);
			Map<String, String> newSql = new HashMap<String, String>();
			newSql.put("0", plusSql);
			queryListData.set(index, newSql);
		}

		for (int i = 0; i < queryListData.size(); i++) {
			Map<String, String> m = queryListData.get(i);
			String sql = m.get("0");
			sql = DatabaseInfo.wrapShardQuery(database.getDatabaseInfo(), sql);
			task.addSqls(sql);
		}

		if (isNewSql && plusSql != null && !"".equals(plusSql)) {
			plusSql = DatabaseInfo.wrapShardQuery(database.getDatabaseInfo(), plusSql);
			task.addSqls(plusSql);
		}

		execTask(-1, new ITask[] { task }, true, getParentShell());

		if (task.getErrorMsg() == null) {
			List<Map<String, String>> result = task.getResult();
			viewColListData.clear();
			if (result != null) {
				viewColListData.addAll(result);
			}
			viewColTableViewer.refresh();

			for (int i = 0; i < viewColTableViewer.getTable().getColumnCount(); i++) {
				viewColTableViewer.getTable().getColumn(i).pack();
			}
		} else {
			if (index >= 0) {
				queryListData.set(index, oldSql);
			}

			return false;
		}

		return true;
	}

	/**
	 * set the button status
	 */
	private void setButtonStatus() {
		int index = queryTableViewer.getTable().getSelectionCount();
		if (index > 0) {
			getButton(BUTTON_DROP_ID).setEnabled(true);
			getButton(BUTTON_EDIT_ID).setEnabled(true);
		} else {
			getButton(BUTTON_DROP_ID).setEnabled(false);
			getButton(BUTTON_EDIT_ID).setEnabled(false);
		}

		if (isPropertyQuery) {
			getButton(BUTTON_ADD_ID).setEnabled(false);
			getButton(BUTTON_DROP_ID).setEnabled(false);
			getButton(BUTTON_EDIT_ID).setEnabled(false);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			tableText.setEditable(false);
			ownerCombo.setEnabled(false);
		}
	}

	public boolean isPropertyQuery() {
		return isPropertyQuery;
	}

	public void setPropertyQuery(boolean isPropertyQuery) {
		this.isPropertyQuery = isPropertyQuery;
	}

	/**
	 * create Common Table Viewer
	 *
	 * @param parent Composite
	 * @param sorter ViewerSorter
	 * @param columnNameArr String[]
	 * @param gridData GridData
	 * @return TableViewer
	 */
	public TableViewer createCommonTableViewer(Composite parent, ViewerSorter sorter,
			final String[] columnNameArr, GridData gridData) {
		final TableViewer tableViewer = new TableViewer(parent, SWT.V_SCROLL | SWT.MULTI
				| SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider());
		if (sorter != null) {
			tableViewer.setSorter(sorter);
		}

		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(gridData);

		for (int i = 0; i < columnNameArr.length; i++) {
			final TableColumn tblColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT
					| (isPropertyQuery ? SWT.NULL : SWT.READ_ONLY));
			tblColumn.setText(columnNameArr[i]);
			if (sorter != null) {
				tblColumn.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						TableColumn column = (TableColumn) event.widget;
						int j = 0;
						for (j = 0; j < columnNameArr.length; j++) {
							if (column.getText().equals(columnNameArr[j])) {
								break;
							}
						}

						TableViewerSorter sorter = ((TableViewerSorter) tableViewer.getSorter());
						if (sorter == null) {
							return;
						}

						sorter.doSort(j);
						tableViewer.getTable().setSortColumn(column);
						tableViewer.getTable().setSortDirection(sorter.isAsc() ? SWT.UP : SWT.DOWN);
						tableViewer.refresh();

						for (int k = 0; k < tableViewer.getTable().getColumnCount(); k++) {
							tableViewer.getTable().getColumn(k).pack();
						}
					}
				});
			}
			tblColumn.pack();
		}

		return tableViewer;
	}

	private boolean valid() {
		setErrorMessage(null);
		if (isNewTableFlag) {
			if (getButton(IDialogConstants.OK_ID) != null) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}

			if (StringUtil.isEmpty(tableText.getText())) {
				setErrorMessage(Messages.errInputViewName);
				return false;
			}

			if (!ValidateUtil.isValidIdentifier(tableText.getText())) {
				setErrorMessage(Messages.bind(Messages.errInputValidViewName, tableText.getText()));
				return false;
			}

			if (tableText.getText().length() > ValidateUtil.MAX_SCHEMA_NAME_LENGTH) {
				setErrorMessage(Messages.bind(Messages.errInputNameLength,
						ValidateUtil.MAX_SCHEMA_NAME_LENGTH));
				return false;
			}
		}

		if (queryListData.isEmpty()) {
			setErrorMessage(Messages.errAddSpecification);
			return false;
		}

		if (viewColListData.isEmpty()) {
			setErrorMessage(Messages.errClickValidate);
			return false;
		}

		if (getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}

		return true;
	}

	/**
	 * Format the SQL script
	 *
	 * @param sql string
	 * @return string
	 */
	private String formatSql(String sql) {
		String sqlStr = formator.format(sql + ";");
		sqlStr = sqlStr.trim().endsWith(";") ? sqlStr.trim().substring(0,
				sqlStr.trim().length() - 1) : "";
		return sqlStr;
	}

	public String getNewViewName() {
		return viewName;
	}

	public String getOwner() {
		return owner;
	}

	public ClassInfo getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(ClassInfo classInfo) {
		this.classInfo = classInfo;
	}

	public List<String> getVclassList() {
		return vclassList;
	}

	public void setVclassList(List<String> vclassList) {
		this.vclassList = vclassList;
	}

	public List<DBAttribute> getAttrList() {
		return attrList;
	}

	public void setAttrList(List<DBAttribute> attrList) {
		this.attrList = attrList;
	}

	public void setDbUserList(List<String> dbUserList) {
		this.dbUserList = dbUserList;
	}
}
