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
package com.cubrid.common.ui.cubrid.table.editor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.Constraint.ConstraintType;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.IDatabaseSpec;
import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.CommentType;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.ArrayUtil;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.ConstraintNamingUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.TableEditorAdaptor;
import com.cubrid.common.ui.cubrid.table.action.EditTableAction;
import com.cubrid.common.ui.cubrid.table.control.CreatePartitionWizard;
import com.cubrid.common.ui.cubrid.table.control.FKTableViewerContentProvider;
import com.cubrid.common.ui.cubrid.table.control.FKTableViewerLabelProvider;
import com.cubrid.common.ui.cubrid.table.control.IndexTableViewerContentProvider;
import com.cubrid.common.ui.cubrid.table.control.IndexTableViewerLabelProvider;
import com.cubrid.common.ui.cubrid.table.control.PartitionContentProvider;
import com.cubrid.common.ui.cubrid.table.control.PartitionTableLabelProvider;
import com.cubrid.common.ui.cubrid.table.control.RangePartitionComparator;
import com.cubrid.common.ui.cubrid.table.dialog.AddFKDialog;
import com.cubrid.common.ui.cubrid.table.dialog.AddIndexDialog;
import com.cubrid.common.ui.cubrid.table.dialog.SetPKDialog;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.editor.QueryEditorUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.model.loader.schema.CubridPartitionedTableLoader;
import com.cubrid.common.ui.spi.model.loader.schema.CubridTablesFolderLoader;
import com.cubrid.common.ui.spi.model.loader.schema.CubridUserTableLoader;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableViewUtil;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.common.ui.spi.util.WidgetUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonSQLExcuterTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.Collation;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog.SchemeInnerType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClassUtil;
import com.cubrid.cubridmanager.core.cubrid.table.task.CheckSubClassTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetPartitionedClassListTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.UpdateDescriptionTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.UpdateNullToDefault;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;

/**
 * Table Editor
 *
 * @author fulei
 * @version 1.0 - 2012-12-26 created by fulei
 */
public class TableEditorPart extends
		CubridEditorPart implements
		ITaskExecutorInterceptor {
	public static final String ID = TableEditorPart.class.getName();
	private static final Logger LOGGER = LogUtil.getLogger(TableEditorPart.class);
	private Table indexTable;
	private Table fkTable;
	private Combo ownerCombo;
	private StyledText sqlText;
	private Text classNameText;
	private Text classDescText;
	private CubridDatabase database;
	private TableColumn sharedColumn;
	// Should use loadColumnData method to set input data
	private TableViewer columnTableView;
	private Table columnsTable;
	private AttributeContentProvider attrContentProvider;
	private AttributeLabelProvider attrLabelProvider;
	private boolean isNewTableFlag;
	private SchemaInfo oldSchemaInfo;
	private SchemaChangeManager schemaChangeMgr;
	private SchemaInfo newSchemaInfo;
	private SchemaDDL schemaDDL;
	private TableViewer fkTableView;
	private TableViewer indexTableView;
	private final Color white = ResourceManager.getColor(SWT.COLOR_WHITE);
	private String jobName;
	private final List<PartitionInfo> partitionInfoList = new ArrayList<PartitionInfo>();
	private TableViewer partitionTableView;
	private Button addPartitionBtn;
	private Button editPartitionBtn;
	private Button delPartitionBtn;
	private Button reuseOIDBtn;
	private String className;
	private String owner;
	private ISchemaNode editedTableNode;
	private List<String> dbUserList;
	private List<PartitionInfo> oldPartitionInfoList;
	private List<Collation> collationList;
	private Button upColumnBtn;
	private Button downColumnBtn;
	private Button deleteColumnBtn;
	private Boolean isHasSubClass;
	private boolean isSupportChange;
	private ToolTip toolTip;
	private ToolTip errorBaloon;
	private Button okBtn;
	private boolean isSupportTableComment;
	private TableEditorPart editor = this;
	private Combo collationCombo;
	private TabFolder tabFolder;
	private Composite tableNameComp;
	private boolean supportCharset = false;
	private String[] columnProperites = null;
	private List<DBAttribute> tempDBAttributeList = new ArrayList<DBAttribute>();
	private List<Constraint> originalConstraints = new ArrayList<Constraint>();
	private int showDefaultType = EditTableAction.MODE_TABLE_EDIT;
	private TableEditorAdaptor editorAdaptor;
	private boolean isCommentSupport = false;

	public void showToolTip(Rectangle rect, String title, String message) {
		CommonUITool.showToolTip(columnsTable, toolTip, rect, title, message);
	}

	public void hideToolTip() {
		CommonUITool.hideToolTip(toolTip);
	}

	public CubridDatabase getCubridDatabase() {
		return database;
	}

	public void createPartControl(Composite parent) {
		isCommentSupport = CompatibleUtil.isCommentSupports(database.getDatabaseInfo());
		final Composite composite = new Composite(parent, SWT.NONE);
		{
			final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 500;
			gd.widthHint = 800;
			composite.setLayoutData(gd);

			GridLayout gl = new GridLayout();
			gl.numColumns = 1;
			composite.setLayout(gl);
		}

		tabFolder = new TabFolder(composite, SWT.NONE);
		{
			final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 469;
			gd.widthHint = 621;
			tabFolder.setLayoutData(gd);
		}
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				TabItem[] tabItems = tabFolder.getSelection();
				if (tabItems == null || tabItems.length == 0 || tabItems[0] == null) {
					return;
				}

				String tabTitle = tabItems[0].getText();
				if (!StringUtil.isEqual(tabTitle, Messages.infoSQLScriptTab)) {
					return;
				}

				String className = classNameText.getText();
				newSchemaInfo.setClassname(className);

				String owner = ownerCombo.getText();
				newSchemaInfo.setOwner(owner);

				if (isSupportUserSchema()) {
					newSchemaInfo.setUniqueName(owner + "." + className);
				} else {
					newSchemaInfo.setUniqueName(className);
				}
				
				if (reuseOIDBtn != null) {
					newSchemaInfo.setReuseOid(reuseOIDBtn.getSelection());
				}

				StringBuilder sql = new StringBuilder();
				if (oldSchemaInfo != null) {
					sql.append(schemaDDL.getSchemaDDL(oldSchemaInfo));
					for (int i = 0; i < 3; i++) {
						sql.append(StringUtil.NEWLINE);
					}
				}

				String alterSql = schemaDDL.getSchemaDDL(oldSchemaInfo, newSchemaInfo);
				if (alterSql != null) {
					sql.append(alterSql);
				}
				sql.append(getChangeOwnerDDL());
				sqlText.setText(sql.toString());
			}
		});
		editorAdaptor = new TableEditorAdaptor(this);

		createGeneralTabItem(tabFolder);
		createFkIndexTabItem(tabFolder);
		createPartitionTabItem(tabFolder);
		createSqlScriptTabItem(tabFolder);

		toolTip = new ToolTip(columnsTable.getShell(), SWT.NONE);
		toolTip.setAutoHide(true);

		errorBaloon = new ToolTip(tabFolder.getShell(), SWT.NONE);
		errorBaloon.setAutoHide(true);

		init();

		if (isNewTableFlag) {
			classNameText.setFocus();
		}

		Composite btnComposite = new Composite(composite, SWT.NONE);
		{
			GridLayout gl = new GridLayout();
			gl.numColumns = 2;
			btnComposite.setLayout(gl);
			btnComposite.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
		}

		okBtn = new Button(btnComposite, SWT.NONE);
		{
			GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
			gd.minimumWidth = 80;
			gd.widthHint = 80;
			okBtn.setLayoutData(gd);
		}
		okBtn.setText(Messages.btnOK);
		okBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				okPressed();
			}
		});

		Button cancelBtn = new Button(btnComposite, SWT.NONE);
		{
			GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
			gd.minimumWidth = 80;
			gd.widthHint = 80;
			cancelBtn.setLayoutData(gd);
		}
		cancelBtn.setText(Messages.btnCancel);
		cancelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				if (!CommonUITool.openConfirmBox(Messages.msgCancelEditTableConfirm)) {
					return;
				}

				IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
				if (page != null) {
					page.closeEditor(editor, false);
				}
			}
		});

		addNewColumn();

		if (showDefaultType == EditTableAction.MODE_INDEX_EDIT) {
			tabFolder.setSelection(1);
		}
	}

	protected void okPressed() {
		if (!verifyTableName()) {
			return;
		}

		if (columnsTable.getItemCount() == 0) {
			CommonUITool.openErrorBox(Messages.noAttributes);
			return;
		}

		String message = (oldSchemaInfo == null) ? Messages.msgCreateTableConfirm
				: Messages.msgAlterTableConfirm;
		if (!CommonUITool.openConfirmBox(message)) {
			return;
		}

		className = classNameText.getText();
		String tableDesc = classDescText.getText();
		owner = ownerCombo.getText();
		newSchemaInfo.setOwner(owner);
		newSchemaInfo.setClassname(className);
		newSchemaInfo.setUniqueName(getTableName());
		newSchemaInfo.setDescription(tableDesc);
		
		if (reuseOIDBtn != null) {
			newSchemaInfo.setReuseOid(reuseOIDBtn.getSelection());
		}

		DatabaseInfo dbInfo = database.getDatabaseInfo();
		CommonSQLExcuterTask commonSqlTask = new CommonSQLExcuterTask(dbInfo);

		schemaDDL.setEndLineChar("$$$$");
		String ddlStr = null;
		if (isNewTableFlag) {
			ddlStr = schemaDDL.getSchemaDDL(newSchemaInfo);
			
		} else {
			ddlStr = schemaDDL.getSchemaDDL(oldSchemaInfo, newSchemaInfo);
		}
		
		boolean isExecuteCommonSqlTask = false;
		String[] sqlStr = ddlStr.split("\\$\\$\\$\\$");
		for (String sql : sqlStr) {
			String trimSql = sql.trim();
			if (trimSql.length() > 0 && !trimSql.startsWith("--")) {
				if (dbInfo.isShard()) {
					sql = dbInfo.wrapShardQuery(sql);
				}
				commonSqlTask.addSqls(sql);
				isExecuteCommonSqlTask = true;
			}
		}

		// When create table on 11.2 or higher, execute only create table
		// When create table on less than 11.2, execute create table and alter table owner to.
		boolean isNotExcuteChangeOwner = isNewTableFlag && dbInfo.isSupportUserSchema();
		if (!isNotExcuteChangeOwner) {
			// do with table user change
			String changeOwnerDDL = getChangeOwnerDDL();
			if (StringUtil.isNotEmpty(changeOwnerDDL)) {
				changeOwnerDDL = dbInfo.wrapShardQuery(changeOwnerDDL);
				if (CompatibleUtil.isSupportChangeOwnerWithAlterStatement(dbInfo)) {
					commonSqlTask.addSqls(changeOwnerDDL);
				} else {
					commonSqlTask.addCallSqls(changeOwnerDDL);
				}
				isExecuteCommonSqlTask = true;
			}
		}

		schemaDDL.setEndLineChar(";");

		// do with column null attribute change
		List<String[]> nullAttrChangedColumnList = getNotNullChangedColumn();
		// if the column is null value, when set this column for not null,need
		// change these null value for default value
		List<String> nullToDefaultChangedColumnList = new ArrayList<String>();
		List<String> defaultValList = new ArrayList<String>();
		if (ApplicationType.CUBRID_MANAGER.equals(PerspectiveManager.getInstance().getCurrentMode())) {
			for (Iterator<String[]> it = nullAttrChangedColumnList.iterator(); it.hasNext();) {
				String[] column = it.next();
				if (!Boolean.parseBoolean(column[1])) {
					continue;
				}
				nullToDefaultChangedColumnList.add(column[0]);
			}
			// if the column is null value, when set this column for not null,do
			// not need change these null value for default value
			List<String> keepNullValueColList = new ArrayList<String>();
			for (Iterator<String> it = nullToDefaultChangedColumnList.iterator(); it.hasNext();) {
				String nullColumn = it.next();
				DBAttribute dBAttribute = newSchemaInfo.getDBAttributeByName(nullColumn, false);
				if (dBAttribute == null) {
					continue;
				}

				String defaultVal = dBAttribute.getDefault();
				boolean isUnique = dBAttribute.isUnique();
				if (isUnique) {
					keepNullValueColList.add(nullColumn);
					it.remove();
				} else {
					if (defaultVal == null) {
						keepNullValueColList.add(nullColumn);
						it.remove();
						continue;
					} else {
						FormatDataResult result = DBAttrTypeFormatter.formatForInput(
								dBAttribute.getType(), defaultVal, false);
						if (result.isSuccess()) {
							defaultValList.add(result.getFormatResult());
						}
					}
				}
			}

			String msg = Messages.bind(Messages.confirmSetDef, nullToDefaultChangedColumnList);
			if (!nullToDefaultChangedColumnList.isEmpty() && (!CommonUITool.openConfirmBox(msg))) {
				return;
			}

			msg = Messages.bind(Messages.confirmKeepNull, keepNullValueColList);
			if (!keepNullValueColList.isEmpty() && (!CommonUITool.openConfirmBox(msg))) {
				return;
			}
		}

		TaskJobExecutor taskJobExec = new CommonTaskJobExec(this);

		boolean hasChanges = isExecuteCommonSqlTask || !nullAttrChangedColumnList.isEmpty()
				|| !nullToDefaultChangedColumnList.isEmpty();
		if (hasChanges) {
			if (isExecuteCommonSqlTask) {
				taskJobExec.addTask(commonSqlTask);
			}

			if (database == null || newSchemaInfo == null) {
				return;
			}

			// change all table data from null value to default value
			int nullColSize = nullToDefaultChangedColumnList.size();
			for (int colIndex = 0; colIndex < nullColSize; colIndex++) {
				UpdateNullToDefault updateNullToDefault = new UpdateNullToDefault(dbInfo);
				updateNullToDefault.setTable(getTableName());
				updateNullToDefault.setColumn(nullToDefaultChangedColumnList.get(colIndex));
				updateNullToDefault.setDefaultValue(defaultValList.get(colIndex));
				taskJobExec.addTask(updateNullToDefault);
			}
		}

		List<UpdateDescriptionTask> updateDescriptionTaskList = getUpdateDescriptionTaskList(dbInfo);
		for (UpdateDescriptionTask task : updateDescriptionTaskList) {
			taskJobExec.addTask(task);
		}

		if (taskJobExec.getTaskCount() > 0) {
			String serverName = database.getServer().getName();
			String dbName = database.getDatabaseInfo().getDbName();
			String title = getSite().getShell().getText();
			jobName = title + " - " + owner + "." + className + "@" + dbName;

			JobFamily jobFamily = new JobFamily();
			jobFamily.setServerName(serverName);
			jobFamily.setDbName(dbName);
			taskJobExec.schedule(jobName, jobFamily, true, Job.SHORT);
		} else {
			getSite().getWorkbenchWindow().getActivePage().closeEditor(editor, false);
		}
	}

	/**
	 * Get UpdateDescriptionTaskList
	 *
	 * @param dbInfo
	 * @return
	 */
	private List<UpdateDescriptionTask> getUpdateDescriptionTaskList(DatabaseInfo dbInfo) { // FIXME move this logic to core module
		List<UpdateDescriptionTask> updateDescriptionTaskList = new ArrayList<UpdateDescriptionTask>();
		if (isSupportTableComment) {
			for (DBAttribute newAttr : newSchemaInfo.getAttributes()) {
				DBAttribute oldAttr = null;
				if (oldSchemaInfo != null) {
					oldAttr = oldSchemaInfo.getDBAttributeByName(newAttr.getName(),
							newAttr.isClassAttribute());
				}

				if (oldAttr != null
						&& StringUtil.isEqual(oldAttr.getDescription(), newAttr.getDescription())) {
					continue;
				} else {
					updateDescriptionTaskList.add(new UpdateDescriptionTask(
							Messages.updateDescriptionTask, dbInfo, getTableName(), newAttr.getName(),
							newAttr.getDescription()));
				}
			}

			boolean notSameDescription = false;
			if (oldSchemaInfo != null) {
				notSameDescription = !StringUtil.isEqualNotIgnoreNull(
						oldSchemaInfo.getDescription(), newSchemaInfo.getDescription());
			}

			if (oldSchemaInfo == null || notSameDescription) {
				UpdateDescriptionTask task = new UpdateDescriptionTask(
						Messages.updateDescriptionTask, dbInfo, getTableName(), null,
						newSchemaInfo.getDescription());
				updateDescriptionTaskList.add(task);
			}
		}

		return updateDescriptionTaskList;
	}

	/**
	 * Get all not null changed column
	 *
	 * @return
	 */
	private List<String[]> getNotNullChangedColumn() { // FIXME move this logic to core module
		List<String[]> notNullChangedColumn = new ArrayList<String[]>();
		List<SchemaChangeLog> allAttrChanges = schemaChangeMgr.getAttrChangeLogs();

		List<SchemaInfo> oldSupers = SuperClassUtil.getSuperClasses(database.getDatabaseInfo(),
				oldSchemaInfo);
		if (oldSupers == null) {
			return notNullChangedColumn;
		}

		List<SchemaInfo> newSupers = SuperClassUtil.getSuperClasses(database.getDatabaseInfo(),
				newSchemaInfo);
		if (newSupers == null) {
			return notNullChangedColumn;
		}

		for (SchemaChangeLog changeLog : allAttrChanges) {
			if (changeLog.getOldValue() == null || changeLog.getNewValue() == null) {
				continue;
			}

			boolean isClassAttr = changeLog.getType() == SchemeInnerType.TYPE_CLASSATTRIBUTE;
			DBAttribute oldAttr = oldSchemaInfo.getDBAttributeByName(changeLog.getOldValue(),
					isClassAttr);
			DBAttribute newAttr = newSchemaInfo.getDBAttributeByName(changeLog.getNewValue(),
					isClassAttr);
			if (oldAttr == null || newAttr == null) {
				continue;
			}

			boolean oldNotNull = oldAttr.isNotNull();
			boolean newNotNull = newAttr.isNotNull();

			Constraint newPK = newSchemaInfo.getPK(newSupers);
			List<String> pkAttributes = newPK == null ? new ArrayList<String>()
					: newPK.getAttributes();

			if (oldNotNull == newNotNull) {
				continue;
			}

			boolean isChangedByPK = false;
			if (newNotNull) {
				// add a new PK
				if (pkAttributes.contains(newAttr.getName())) {
					isChangedByPK = true;
				}
			} else {
				// drop an old PK
				Constraint oldPK = oldSchemaInfo.getPK(oldSupers);
				if (oldPK != null) {
					List<String> oldPKAttrs = oldPK.getAttributes();
					if (oldPKAttrs != null && oldPKAttrs.contains(newAttr.getName())) {
						isChangedByPK = true;
					}
				}
			}

			if (!isChangedByPK) {
				String[] newColumn = new String[] { newAttr.getName(), String.valueOf(newNotNull) };
				notNullChangedColumn.add(newColumn);
			}
		}

		return notNullChangedColumn;
	}

	/**
	 * Get Change Owner DDL
	 *
	 * @return string
	 */
	private String getChangeOwnerDDL() { // FIXME move this logic to core module
		String oldOwner = null;
		if (isNewTableFlag) {
			oldOwner = database.getUserName();
		} else {
			oldOwner = oldSchemaInfo.getOwner();
		}

		String newOwner = newSchemaInfo.getOwner();
		if (oldOwner != null && oldOwner.equalsIgnoreCase(newOwner)) {
			return "";
		}

		String className = classNameText.getText();
		String TableName;
		if (database.getDatabaseInfo().isSupportUserSchema()) {
			TableName = oldOwner + "." + className;
		} else {
			TableName = className;
		}
		
		ServerInfo serverInfo = database.getServer().getServerInfo();
		if (CompatibleUtil.isSupportChangeOwnerWithAlterStatement(serverInfo)) {
			return schemaDDL.getChangeOwnerDDLWithAlterStatement(TableName, newOwner);
		} else {
			return schemaDDL.getChangeOwnerDDL(className, newOwner);
		}
	}

	/**
	 * Create general tab item
	 *
	 * @param tabFolder the object of TabFolder
	 */
	private void createGeneralTabItem(final TabFolder tabFolder) {
		final TabItem generalTabItem = new TabItem(tabFolder, SWT.NONE);
		generalTabItem.setText(Messages.infoGeneralTab);

		final Composite compositeGenaral = new Composite(tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		compositeGenaral.setLayout(gridLayout);
		generalTabItem.setControl(compositeGenaral);

		final Group group = new Group(compositeGenaral, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);
		group.setText(Messages.lblTableInfo);

		final Label tableNameLabel = new Label(group, SWT.NONE);
		tableNameLabel.setData(Messages.dataNewKey, null);
		tableNameLabel.setText(Messages.lblTableName);

		tableNameComp = new Composite(group, SWT.NONE);
		{
			GridLayout gl = new GridLayout();
			gl.numColumns = 5;
			gl.marginWidth = 0;
			tableNameComp.setLayout(gl);
			tableNameComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			classNameText = new Text(tableNameComp, SWT.BORDER);
			classNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
			{
				GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
				gd.widthHint = 200;
				classNameText.setLayoutData(gd);
			}
			classNameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					modifySchemaInfo();
				}
			});
			classNameText.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					CommonUITool.hideErrorBaloon(errorBaloon);
				}
			});

			final Label ownerLabel = new Label(tableNameComp, SWT.NONE);
			ownerLabel.setText(Messages.lblOwner);
			ownerCombo = new Combo(tableNameComp, SWT.READ_ONLY);
			{
				GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
				gd.widthHint = 100;
				ownerCombo.setLayoutData(gd);
			}
			ownerCombo.setVisibleItemCount(10);
			ownerCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					modifySchemaInfo();
				}
			});
			fillOwnerCombo();
			if (supportCharset) {
				final Label collationLabel = new Label(tableNameComp, SWT.NONE);
				collationLabel.setText(Messages.lblCollation);
				collationCombo = new Combo(tableNameComp, SWT.READ_ONLY);
				{
					GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
					gd.widthHint = 100;
					collationCombo.setLayoutData(gd);
				}
				collationCombo.setVisibleItemCount(10);
				fillCollationCombo();
				String collation = collationCombo.getText();
				newSchemaInfo.setCollation(collation);
				collationCombo.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						String collation = collationCombo.getText();
						newSchemaInfo.setCollation(collation);
					}

					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

				if (!isNewTableFlag && newSchemaInfo.getCollation() != null) {
					collationCombo.setText(newSchemaInfo.getCollation());
				}
			} else {
				new Label(tableNameComp, SWT.NONE);
				new Label(tableNameComp, SWT.NONE);
			}
		}

		final Label tableDescLabel = new Label(group, SWT.NONE);
		tableDescLabel.setText(Messages.lblTableDesc);

		classDescText = new Text(group, SWT.BORDER);
		classDescText.setTextLimit(512);
		classDescText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (newSchemaInfo != null && newSchemaInfo.getDescription() != null) {
			classDescText.setText(newSchemaInfo.getDescription());
		}
		classDescText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (!isSupportTableComment) {
					CommonUITool.showErrorBaloon(group, classDescText, errorBaloon, "",
							Messages.errNotSupportTableCommentNotice);
					classDescText.setFocus();
				}
			}

			public void focusLost(FocusEvent e) {
				CommonUITool.hideErrorBaloon(errorBaloon);
			}
		});
		classDescText.setEditable(isSupportTableComment);

		if (database == null) {
			return;
		}

		if (CompatibleUtil.isSupportReuseOID(database.getDatabaseInfo())) {
			reuseOIDBtn = new Button(group, SWT.CHECK);
			reuseOIDBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			reuseOIDBtn.setText(Messages.btnReuseOid);
			if (!isNewTableFlag) {
				reuseOIDBtn.setEnabled(false);
			}
		}

		final Label columnsLabel = new Label(compositeGenaral, SWT.NONE);
		columnsLabel.setText(Messages.lblColumn);

		// create attribute table
		columnTableView = new TableViewer(compositeGenaral, SWT.FULL_SELECTION | SWT.BORDER);
		columnTableView.setUseHashlookup(true);
		columnTableView.setColumnProperties(columnProperites);

		columnsTable = columnTableView.getTable();
		//columnsTable.pack();
		final GridData gdColumnsTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdColumnsTable.heightHint = 189;
		columnsTable.setLayoutData(gdColumnsTable);
		columnsTable.setLinesVisible(true);
		columnsTable.setHeaderVisible(true);
		
		// The empty column
		final TableColumn emptyColumn = new TableColumn(columnsTable, SWT.NONE);
		emptyColumn.setWidth(0);
		// The flag column
		final TableColumn flagColumn = new TableColumn(columnsTable, SWT.RIGHT_TO_LEFT);
		flagColumn.setWidth(28);
		// NAME
		final TableColumn nameColumn = new TableColumn(columnsTable, SWT.NONE);
		nameColumn.setWidth(140);
		nameColumn.setText(Messages.tblColumnName);

		// DATATYPE
		final TableColumn dataTypeColumn = new TableColumn(columnsTable, SWT.NONE);
		dataTypeColumn.setWidth(120);
		dataTypeColumn.setText(Messages.tblColumnDataType);

		// DEFAULT
		final TableColumn defaultColumn = new TableColumn(columnsTable, SWT.NONE);
		defaultColumn.setWidth(98);
		defaultColumn.setText(Messages.tblColumnDefault);
		defaultColumn.setToolTipText(Messages.tblColumnDefaultHint);

		// AUTO INCREMENT
		final TableColumn autoIncrTableColumn = new TableColumn(columnsTable, SWT.NONE);
		autoIncrTableColumn.setAlignment(SWT.LEFT);
		autoIncrTableColumn.setWidth(100);
		autoIncrTableColumn.setText(Messages.tblColumnAutoIncr);
		autoIncrTableColumn.setToolTipText(Messages.tblColumnAutoIncrHint);

		// NOT NULL
		final TableColumn notNullColumn = new TableColumn(columnsTable, SWT.NONE);
		notNullColumn.setWidth(65);
		notNullColumn.setText(Messages.tblColumnNotNull);
		notNullColumn.setAlignment(SWT.LEFT);
		notNullColumn.setToolTipText(Messages.tblColumnNotNullHint);

		// PK
		final TableColumn pkColumn = new TableColumn(columnsTable, SWT.NONE);
		pkColumn.setAlignment(SWT.CENTER);
		pkColumn.setWidth(90);
		pkColumn.setText(Messages.tblColumnPK);

		// UK
		final TableColumn uniqueColumn = new TableColumn(columnsTable, SWT.NONE);
		uniqueColumn.setWidth(55);
		uniqueColumn.setText(Messages.tblColumnUnique);
		uniqueColumn.setAlignment(SWT.LEFT);
		uniqueColumn.setToolTipText(Messages.tblColumnUniqueHint);

		// SHARED
		sharedColumn = new TableColumn(columnsTable, SWT.NONE);
		sharedColumn.setWidth(65);
		sharedColumn.setText(Messages.tblColumnShared);
		sharedColumn.setAlignment(SWT.LEFT);
		sharedColumn.setToolTipText(Messages.tblColumnSharedHint);
		if (supportCharset) {
			final TableColumn collationColumn = new TableColumn(columnsTable, SWT.NONE);
			collationColumn.setWidth(120);
			collationColumn.setText(Messages.tblColumnColumnCollation);
			collationColumn.setAlignment(SWT.LEFT);
		}

		final TableColumn descColumn = new TableColumn(columnsTable, SWT.NONE);
		descColumn.setWidth(180);
		descColumn.setText(Messages.tblColumnColumnDesc);
		descColumn.setAlignment(SWT.LEFT);

		boolean canEdit = true;
		if (!editor.isNewTableFlag() && !editor.isSupportChange()) {
			canEdit = false;
		}

		attrContentProvider = new AttributeContentProvider();
		attrLabelProvider = new AttributeLabelProvider(database.getDatabaseInfo(), newSchemaInfo,
				canEdit, editorAdaptor);
		columnTableView.setContentProvider(attrContentProvider);
		columnTableView.setLabelProvider(attrLabelProvider);

		CellEditor[] cellEditor = new CellEditor[columnProperites.length];
		{
			int index = 0;
			// Empty
			cellEditor[index++] = null;
			// Flag
			cellEditor[index++] = null;
			// NAME
			cellEditor[index++] = new TextCellEditor(columnsTable);
			// DATATYPE
			cellEditor[index++] = new DataTypeCellEditor((Composite) columnsTable, listDataTypes(),
					editorAdaptor);
			// DEFAULT
			cellEditor[index++] = new TextCellEditor(columnsTable);
			// AUTO INCREMENT
			cellEditor[index++] = new AutoIncrementCellEditor(columnsTable, editorAdaptor);
			// NOT NULL
			cellEditor[index++] = new CheckboxCellEditor(columnsTable);
			// PK
			cellEditor[index++] = new CheckboxCellEditor(columnsTable);
			// UK
			cellEditor[index++] = new CheckboxCellEditor(columnsTable);
			// SHARD
			cellEditor[index++] = new CheckboxCellEditor(columnsTable);
			// COLLATION
			if (supportCharset) {
				cellEditor[index++] = new ComboBoxCellEditor(columnsTable, getCollationArray(),
						SWT.READ_ONLY);
			}
			// MEMO
			cellEditor[index++] = new TextCellEditor(columnsTable);
		}

		columnTableView.setCellEditors(cellEditor);
		columnTableView.setCellModifier(new AttributeCellModifier(editorAdaptor));

		loadColumnData();
		CommonUITool.hackForYosemite(columnsTable);
		// Create button
		final Composite btnRowComposite = new Composite(compositeGenaral, SWT.NONE);
		btnRowComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginWidth = 5;
			btnRowComposite.setLayout(layout);
		}

		final Composite optComposite = new Composite(btnRowComposite, SWT.NONE);
		optComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginWidth = 0;
			optComposite.setLayout(layout);
		}

		final Composite btnComposite = new Composite(btnRowComposite, SWT.NONE);
		btnComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		{
			GridLayout layout = new GridLayout();
			layout.marginRight = 0;
			layout.numColumns = 6;
			layout.marginWidth = 0;
			btnComposite.setLayout(layout);
		}

		final Button setPkButton = new Button(btnComposite, SWT.NONE);
		setPkButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		setPkButton.setText(Messages.btnPK);
		setPkButton.addSelectionListener(new PkBtnListenerOnGeneTab(isNewTableFlag));

		final GridData gdUpDown = new GridData(60, SWT.DEFAULT);
		upColumnBtn = new Button(btnComposite, SWT.NONE);
		upColumnBtn.setLayoutData(gdUpDown);
		upColumnBtn.setEnabled(false);
		upColumnBtn.setText(Messages.btnUp);
		upColumnBtn.addSelectionListener(new UpBtnListenerOnGeneTab());

		downColumnBtn = new Button(btnComposite, SWT.DOWN);
		downColumnBtn.setLayoutData(gdUpDown);
		downColumnBtn.setEnabled(false);
		downColumnBtn.setText(Messages.btnDown);
		downColumnBtn.addSelectionListener(new DownBtnListenerOnGeneTab());

		final Button addButton = new Button(btnComposite, SWT.NONE);
		final GridData gdAddButton = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gdAddButton.horizontalIndent = 10;
		addButton.setLayoutData(gdAddButton);
		addButton.setText(Messages.btnAddColumn);
		addButton.addSelectionListener(new AddBtnListenerOnGeneTab());

		deleteColumnBtn = new Button(btnComposite, SWT.NONE);
		final GridData gdDeleteButton = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gdDeleteButton.horizontalIndent = 10;
		deleteColumnBtn.setLayoutData(gdDeleteButton);
		deleteColumnBtn.setEnabled(false);
		deleteColumnBtn.setText(Messages.btnDelColumn);
		deleteColumnBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteColumn();
			}
		});

		columnsTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleSelectionChangeInColumnTable();
			}
		});

		buildColumnTableMenu();
	}

	private String[] listDataTypes() {
		if (database == null || database.getDatabaseInfo() == null) {
			return new String[] {};
		}
		List<String> list = new ArrayList<String>();
		String[][] typeMapping = DataType.getTypeMapping(database.getDatabaseInfo(), false, true);
		for (int j = 0; j < typeMapping.length; j++) {
			if (typeMapping[j][0] != null && typeMapping[j][0].startsWith("VARCHAR")) {
				list.add("VARCHAR(255)");
				list.add("VARCHAR(4096)");
			}
			list.add(typeMapping[j][0]);
		}
		Collections.sort(list);
		return list.toArray(new String[list.size()]);
	}

	public void loadColumnData() {
		List<DBAttribute> list = new ArrayList<DBAttribute>();
		list.addAll(newSchemaInfo.getAttributes());
		list.addAll(tempDBAttributeList);
		columnTableView.setInput(list);
	}

	private void buildColumnTableMenu() {
		Menu menu = new Menu(columnsTable.getShell(), SWT.POP_UP);
		columnsTable.setMenu(menu);

		final MenuItem deleteItem = new MenuItem(menu, SWT.PUSH);
		deleteItem.setText(Messages.itemDeleteColumn);
		deleteItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteColumn();
			}
		});

		menu.addMenuListener(new MenuListener() {
			public void menuShown(MenuEvent e) {
				TableItem[] tblItems = columnsTable.getSelection();
				if (tblItems.length > 0) {
					DBAttribute attr = (DBAttribute) tblItems[0].getData();
					List<DBAttribute> items = newSchemaInfo.getAttributes();
					if (!items.contains(attr)) {
						deleteItem.setEnabled(false);
					} else {
						deleteItem.setEnabled(true);
					}
				}
			}

			public void menuHidden(MenuEvent e) {
			}
		});
	}

	/**
	 * Handle selection change event in column table
	 */
	private void handleSelectionChangeInColumnTable() {
		int selectionCount = columnsTable.getSelectionCount();
		if (selectionCount <= 0) {
			deleteColumnBtn.setEnabled(false);
			downColumnBtn.setEnabled(false);
			upColumnBtn.setEnabled(false);
		} else {
			TableItem[] tblItems = columnsTable.getSelection();
			DBAttribute attr = (DBAttribute) tblItems[0].getData();
			String uniqueName = "";
			if (isSupportUserSchema()) {
				uniqueName = ownerCombo.getText() + "." + classNameText.getText().trim();
			} else {
				uniqueName = classNameText.getText().trim();
			}
			boolean isInheritAttr = attr.getInherit() != null
					&& !uniqueName.equalsIgnoreCase(attr.getInherit().trim());
			deleteColumnBtn.setEnabled(!isInheritAttr);

			if (selectionCount > 1) {
				upColumnBtn.setEnabled(false);
				downColumnBtn.setEnabled(false);
			} else {
				if (database == null || database.getDatabaseInfo() == null) {
					return;
				}

				boolean isSupportReorderColumn = CompatibleUtil.isSupportReorderColumn(database.getDatabaseInfo());
				if (isSupportReorderColumn && !isNewTableFlag) {
					// class attribute do not support to reorder
					if (attr.isClassAttribute() || isInheritAttr) {
						isSupportReorderColumn = false;
					}

					// the attribute which was used by subClass, it not support reorder.
					if (isSupportReorderColumn && isHasSubClass()) {
						isSupportReorderColumn = false;
					}
				}

				int count = 0;
				if (newSchemaInfo.getClassAttributes() != null) {
					count = newSchemaInfo.getClassAttributes().size();
				}
				int index = columnsTable.getSelectionIndex();
				if (!attr.isClassAttribute()) {
					List<DBAttribute> attrList = newSchemaInfo.getAttributes();
					count = attrList.size();
					int inheritAttrCount = 0;
					for (DBAttribute dbAttr : attrList) {
						boolean isInheritDbAttr = dbAttr.getInherit() != null
								&& dbAttr.getInherit().trim().length() > 0
								&& uniqueName.equalsIgnoreCase(
										dbAttr.getInherit().trim());
						if (isInheritDbAttr) {
							inheritAttrCount++;
						}
					}
					index = index - inheritAttrCount;
					count = count - inheritAttrCount;
				}
				if (index == 0) {
					upColumnBtn.setEnabled(false);
				} else {
					upColumnBtn.setEnabled(isNewTableFlag ? !isInheritAttr : isSupportReorderColumn);
				}
				if (index == count - 1) {
					downColumnBtn.setEnabled(false);
				} else {
					downColumnBtn.setEnabled(isNewTableFlag ? !isInheritAttr
							: isSupportReorderColumn);
				}
				List<DBAttribute> items = newSchemaInfo.getAttributes();
				if (!items.contains(attr)) {
					deleteColumnBtn.setEnabled(false);
				} else {
					deleteColumnBtn.setEnabled(true);
				}
			}
		}
	}

	/**
	 * Judge the current schemaInfo is has sub class
	 *
	 * @return
	 */
	public boolean isHasSubClass() {
		if (isHasSubClass == null) {
			CheckSubClassTask task = new CheckSubClassTask(database.getDatabaseInfo());
			isHasSubClass = task.checkSubClass(oldSchemaInfo.getOwner(), oldSchemaInfo.getClassname());
		}

		return isHasSubClass;
	}

	/**
	 * Create the SQL script tab item
	 *
	 * @param tabFolder the object of TabFolder
	 */
	private void createSqlScriptTabItem(final TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Messages.infoSQLScriptTab);

		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout());
		tabItem.setControl(composite);

		sqlText = new StyledText(composite, SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL
				| SWT.BORDER);
		CommonUITool.registerContextMenu(sqlText, false);
		sqlText.setBackground(white);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 89;
		sqlText.setLayoutData(gd);
	}

	/**
	 * A class which listens the down button on the general tab item
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-1-7 created by lizhiqiang
	 */
	final class PkBtnListenerOnGeneTab extends
			SelectionAdapter {
		private boolean isNewTable;

		PkBtnListenerOnGeneTab(boolean isNewTable) {
			this.isNewTable = isNewTable;
		}

		/**
		 * Sent when selection occurs in the control.
		 *
		 * @param event an event containing information about the selection
		 */
		public void widgetSelected(SelectionEvent event) {
			SetPKDialog dlg = new SetPKDialog(getSite().getShell(), database, newSchemaInfo,
					isNewTable);
			int ret = dlg.open();
			if (ret == SetPKDialog.OK) {
				Constraint oldPK = dlg.getOldPK();
				Constraint newPK = dlg.getNewPK();
				String op = dlg.getOperation();
				if (("ADD").equals(op)) { //$NON-NLS-1$
					newSchemaInfo.addConstraint(newPK);
					firePKAdded(newSchemaInfo, newPK);
				} else if (("DEL").equals(op)) { //$NON-NLS-1$
					newSchemaInfo.getConstraints().remove(oldPK);
					firePKRemoved(newSchemaInfo, oldPK);
				} else if (("MODIFY").equals(op)) { //$NON-NLS-1$
					newSchemaInfo.getConstraints().remove(oldPK);
					firePKRemoved(newSchemaInfo, oldPK);
					newSchemaInfo.addConstraint(newPK);
					firePKAdded(newSchemaInfo, newPK);
				}
				attrLabelProvider.setSchema(newSchemaInfo);
				loadColumnData();
				indexTableView.setInput(newSchemaInfo);
			}
		}

		/**
		 * Fire the PK added.
		 *
		 * @param newSchema the object of SchemaInfo
		 * @param newPK the object of Constraint
		 */
		private void firePKAdded(SchemaInfo newSchema, Constraint newPK) { // FIXME move this logic to core module
			if (newSchema == null || newPK == null) {
				return;
			}

			List<String> attrList = newPK.getAttributes();
			if (attrList.size() == 1) {
				String attrName = attrList.get(0);
				Constraint index = newSchema.removeUniqueByAttrName(attrName);
				if (index != null) {
					String key = index.getDefaultName(newSchema.getUniqueName()) + "$"
							+ index.getName();
					SchemaChangeLog changeLog = new SchemaChangeLog(key, null,
							SchemeInnerType.TYPE_INDEX);
					schemaChangeMgr.addSchemeChangeLog(changeLog);
				}

				DBAttribute attr = newSchema.getDBAttributeByName(attrName, false);
				if (attr == null) {
					return;
				}

				boolean changed = false;
				if (!attr.isNotNull()) {
					attr.setNotNull(true);
					changed = true;
				}
				if (!attr.isUnique()) {
					attr.setUnique(true);
					changed = true;
				}
				if (changed) {
					SchemaChangeLog changeLog = new SchemaChangeLog(attr.getName(), attr.getName(),
							SchemeInnerType.TYPE_ATTRIBUTE);
					schemaChangeMgr.addSchemeChangeLog(changeLog);
				}
			} else {
				for (String attrName : attrList) {
					DBAttribute attr = newSchema.getDBAttributeByName(attrName, false);
					if (attr != null && !attr.isNotNull()) {
						attr.setNotNull(true);
						SchemaChangeLog changeLog = new SchemaChangeLog(attr.getName(),
								attr.getName(), SchemeInnerType.TYPE_ATTRIBUTE);
						schemaChangeMgr.addSchemeChangeLog(changeLog);
					}
				}
			}
		}

		/**
		 * Fire the PK removed
		 *
		 * @param newSchema the object of SchemaInfo
		 * @param oldPK the object of Constraint
		 */
		public void firePKRemoved(SchemaInfo newSchema, Constraint oldPK) { // FIXME move this logic to core module
			if (newSchema == null || oldPK == null) {
				return;
			}

			List<String> attrList = oldPK.getAttributes();
			if (attrList.size() == 1) {
				String attrName = attrList.get(0);
				Constraint index = newSchema.removeUniqueByAttrName(attrName);
				if (index != null) {
					String key = index.getDefaultName(newSchema.getUniqueName()) + "$"
							+ index.getName();
					SchemaChangeLog changeLog = new SchemaChangeLog(key, null,
							SchemeInnerType.TYPE_INDEX);
					schemaChangeMgr.addSchemeChangeLog(changeLog);
				}
				DBAttribute attr = newSchema.getDBAttributeByName(attrName, false);
				if (attr == null) {
					return;
				}

				attr.setNotNull(false);
				attr.setUnique(false);

				SchemaChangeLog changeLog = new SchemaChangeLog(attr.getName(), attr.getName(),
						SchemeInnerType.TYPE_ATTRIBUTE);
				schemaChangeMgr.addSchemeChangeLog(changeLog);
			} else {
				for (String attrName : attrList) {
					DBAttribute attr = newSchema.getDBAttributeByName(attrName, false);
					if (attr == null) {
						continue;
					}
					attr.setNotNull(false);

					SchemaChangeLog changeLog = new SchemaChangeLog(attr.getName(), attr.getName(),
							SchemeInnerType.TYPE_ATTRIBUTE);
					schemaChangeMgr.addSchemeChangeLog(changeLog);
				}
			}
		}
	}

	/**
	 * A class which listens the down button on the general tab item.
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-1-7 created by lizhiqiang
	 */
	private final class UpBtnListenerOnGeneTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			TableItem[] selection = columnsTable.getSelection();
			int selectionIndex = columnsTable.getSelectionIndex();
			if (selection != null && selection.length >= 1) {
				DBAttribute dbAttr = (DBAttribute) selection[0].getData();
				boolean isClassAttr = dbAttr.isClassAttribute();
				List<DBAttribute> attrList = newSchemaInfo.getClassAttributes();
				if (!isClassAttr) {
					attrList = newSchemaInfo.getAttributes();
				}

				if (selectionIndex > 0) {
					DBAttribute attribute = attrList.get(selectionIndex);

					attrList.remove(selectionIndex);
					attrList.add(selectionIndex - 1, attribute);
					loadColumnData();
					columnTableView.setSelection(new StructuredSelection(dbAttr), true);
					columnsTable.setFocus();
					handleSelectionChangeInColumnTable();

					addPosAttrLog(attribute);
				}
			}
		}
	}

	/**
	 * A class which listens the down button on the general tab item
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-1-7 created by lizhiqiang
	 */
	private final class DownBtnListenerOnGeneTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			TableItem[] selection = columnsTable.getSelection();
			int selectionIndex = columnsTable.getSelectionIndex();
			if (selection != null && selection.length >= 1) {
				DBAttribute dbAttr = (DBAttribute) selection[0].getData();
				boolean isClassAttr = dbAttr.isClassAttribute();
				List<DBAttribute> attrList = newSchemaInfo.getClassAttributes();
				if (!isClassAttr) {
					attrList = newSchemaInfo.getAttributes();
				}

				if (!(selectionIndex == attrList.size() - 1)) {
					DBAttribute attribute = attrList.get(selectionIndex);
					attrList.remove(selectionIndex);
					attrList.add(selectionIndex + 1, attribute);
					loadColumnData();
					columnTableView.setSelection(new StructuredSelection(dbAttr), true);
					columnsTable.setFocus();
					handleSelectionChangeInColumnTable();

					addPosAttrLog(attribute);
				}
			}
		}
	}

	/**
	 * A class which listens the edit button on the general tab item
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-1-7 created by lizhiqiang
	 */
	private final class AddBtnListenerOnGeneTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			addNewColumn();
		}
	}

	public void addNewColumn() { // FIXME move this logic to core module
		if (newSchemaInfo == null) {
			return;
		}

		// boolean hasNotFinishedColumn = false;
		boolean hasDuplicatedColumn = false;

		List<DBAttribute> items = newSchemaInfo.getAttributes();
		if (items != null && items.size() > 0) {
			Set<String> matches = new HashSet<String>();

			// check whether there is no name column
			for (DBAttribute attr : items) {
				if (attr == null) {
					continue;
				}

				if (StringUtil.isEmpty(attr.getName())) {
					continue;
				}

				if (matches.contains(attr.getName())) {
					hasDuplicatedColumn = true;
					break;
				}

				matches.add(attr.getName());
			}
		}

		if (hasDuplicatedColumn) {
			CommonUITool.openErrorBox(Messages.errSameNameOnEditTableAddColumn);
			return;
		}

		String collation = null;
		if (newSchemaInfo != null && newSchemaInfo.getCollation() != null) {
			collation = newSchemaInfo.getCollation();
		} else {
			collation = Collation.DEFAULT_COLLATION;
		}

		String newAttrName = "";
		DBAttribute addAttribute = new DBAttribute(newAttrName, DataType.DATATYPE_CHAR,
				newSchemaInfo.getUniqueName(), false, false, false, false, null, collation);
		addAttribute.setNew(true);
		tempDBAttributeList.add(addAttribute);
		loadColumnData();

		columnTableView.setSelection(new StructuredSelection(addAttribute), true);
		columnsTable.setFocus();
		handleSelectionChangeInColumnTable();
	}

	/**
	 * Make a change log for editing attribute
	 *
	 * @param attrName
	 * @param lastAttr attribute previous changed by the user
	 * @param editAttr attribute to be changed by the user
	 * @param origAttr attribute on a server
	 * @return
	 */
	public boolean changeForEditAttribute(String attrName, DBAttribute editAttr,
			DBAttribute origAttr) { // FIXME move this logic to core module
		if (database == null || editAttr == null) {
			return false;
		}

		String tableName = newSchemaInfo.getUniqueName();
		editAttr.setInherit(tableName);
		String newAttrName = editAttr.getName();

		// new attribute
		if (origAttr == null) {
			if (!StringUtil.isEmpty(attrName)
					&& newSchemaInfo.replaceDBAttributeByName(attrName, editAttr)) {
				// replace
				addDropAttrLog(attrName, false);
				addNewAttrLog(newAttrName, false);
			}
		} else {
			// existed attribute to changed with a name
			addEditAttrLog(attrName, newAttrName, false);
		}

		if (origAttr == null) {
			origAttr = new DBAttribute();
		}

		if (!origAttr.isUnique() && newSchemaInfo.getUniqueByAttrName(editAttr.getName()) == null
				&& editAttr.isUnique()) {
			Constraint unique = new Constraint(true);
			unique.setType(Constraint.ConstraintType.UNIQUE.getText());
			unique.addAttribute(newAttrName);
			unique.addRule(newAttrName + " ASC");
			unique.setName(ConstraintNamingUtil.getUniqueName(tableName, unique.getRules()));
			newSchemaInfo.addConstraint(unique);
			addNewUniqueLog(unique);
		} else if (origAttr.isUnique() && !editAttr.isUnique()) {
			if (oldSchemaInfo != null) {
				Constraint unique = oldSchemaInfo.getUniqueByAttrName(origAttr.getName());
				addDelUniqueLog(unique);
			}
			newSchemaInfo.removeUniqueByAttrName(attrName);
		} else if (!origAttr.isUnique() && !editAttr.isUnique()) {
			Constraint unique = newSchemaInfo.getUniqueByAttrName(attrName);
			if (unique != null) {
				addDelUniqueLog(unique);
			}
			newSchemaInfo.removeUniqueByAttrName(attrName);
		}

		indexTableView.setInput(newSchemaInfo);
		fkTableView.setInput(newSchemaInfo);

		if (database != null && database.getDatabaseInfo() != null && newSchemaInfo != null) {
			SuperClassUtil.fireSuperClassChanged(database.getDatabaseInfo(), oldSchemaInfo,
					newSchemaInfo, newSchemaInfo.getSuperClasses());
		}
		attrLabelProvider.setSchema(newSchemaInfo);
		loadColumnData();
		columnTableView.setSelection(new StructuredSelection(editAttr), true);
		columnsTable.setFocus();
		handleSelectionChangeInColumnTable();

		return true;
	}

	public boolean makeChangeLogForIndex(String attrName, DBAttribute editAttr, DBAttribute origAttr) {
		List<Constraint> constrainList = newSchemaInfo.getConstraints();
		if (constrainList.size() == 0) { // FIXME move this logic to core module
			return false;
		}

		List<Constraint> removedConstrainList = new ArrayList<Constraint>();
		List<Constraint> addedConstrainList = new ArrayList<Constraint>();
		for (Constraint cons : constrainList) {
			if (cons == null) {
				continue;
			}
			if (ConstraintType.INDEX.getText().equals(cons.getType())
					|| ConstraintType.REVERSEINDEX.getText().equals(cons.getType())
					|| ConstraintType.UNIQUE.getText().equals(cons.getType())
					|| ConstraintType.REVERSEUNIQUE.getText().equals(cons.getType())) {
				List<String> attrs = cons.getAttributes();
				if (!(attrs != null && attrs.contains(attrName))) {
					continue;
				}

				removedConstrainList.add(cons);
				Constraint newCons = new Constraint(true);
				newCons.setType(cons.getType());
				newCons.setName(cons.getName());
				cons.replaceAttribute(attrName, editAttr.getName());
				newCons.setAttributes(cons.getAttributes());
				for (String origRule : cons.getRules()) {
					if (cons.getRules().size() == 1) {
						newCons.addRule(editAttr.getName() + origRule.substring(attrName.length()));
						break;
					}
					int spaceIndex = origRule.indexOf(" ");
					String attrNameFromRule = origRule.substring(0, spaceIndex);
					if (attrName.equals(attrNameFromRule)) {
						newCons.addRule(editAttr.getName() + origRule.substring(attrName.length()));
					} else {
						newCons.addRule(origRule);
					}
				}
				addedConstrainList.add(newCons);

				String key = cons.getDefaultName(newSchemaInfo.getUniqueName()) + "$"
						+ cons.getName();
				SchemaChangeLog changeLog = new SchemaChangeLog(key, key,
						SchemeInnerType.TYPE_INDEX);
				schemaChangeMgr.addSchemeChangeLog(changeLog);
			}
		}

		if (removedConstrainList.size() == 0) {
			return false;
		}
		constrainList.removeAll(removedConstrainList);
		constrainList.addAll(addedConstrainList);
		indexTableView.setInput(newSchemaInfo);

		return true;
	}

	private void deleteColumn() { // FIXME move this logic to core module

		if (!CommonUITool.openConfirmBox(Messages.msgDeleteColumnConfirm)) {
			return;
		}

		TableItem[] tblItems = columnsTable.getSelection();
		if (tblItems.length > 0) {
			DBAttribute attr = (DBAttribute) tblItems[0].getData();
			List<DBAttribute> items = newSchemaInfo.getAttributes();
			if (!items.contains(attr)) {
				return;
			}
		}

		TableItem[] selection = columnsTable.getSelection();
		int selectionIndex = columnsTable.getSelectionIndex();
		if (selection != null && selection.length >= 1) {
			List<String> attrNames = new ArrayList<String>();
			for (int m = 0; m < selection.length; m++) {
				attrNames.add(m, selection[m].getText(1));
			}
			List<SchemaInfo> allSupers = SuperClassUtil.getSuperClasses(database.getDatabaseInfo(),
					newSchemaInfo);
			Constraint pk = newSchemaInfo.getPK(allSupers);
			List<String> pkAttributes = pk == null ? new ArrayList<String>() : pk.getAttributes();
			boolean hasPk = false;
			for (String pkAttribute : pkAttributes) {
				if (attrNames.contains(pkAttribute)) {
					hasPk = true;
					break;
				}
			}
			if (hasPk) {
				if (attrNames.containsAll(pkAttributes)) {
					newSchemaInfo.removeConstraintByName(pk.getName(),
							Constraint.ConstraintType.PRIMARYKEY.getText());
				} else {
					CommonUITool.openErrorBox(Messages.errColumnNotDropForPk);
					return;
				}
			}

			for (PartitionInfo partitionInfo : newSchemaInfo.getPartitionList()) {
				String partitionExpr = partitionInfo.getPartitionExpr();
				if (StringUtil.isNotEmpty(partitionExpr)) {
					if (partitionExpr.startsWith("[") && partitionExpr.endsWith("]")) {
						partitionExpr = partitionExpr.substring(1, partitionExpr.length() - 1);
						if (attrNames.contains(partitionExpr)) {
							CommonUITool.openErrorBox(Messages.errDropForPartitonColumn);
							return;
						}
					} else {
						if (attrNames.contains(partitionExpr)) {
							CommonUITool.openErrorBox(Messages.errDropForPartitonColumn);
							return;
						}
					}
				}
			}

			for (TableItem selec : selection) {
				DBAttribute oldAttribute = (DBAttribute) selec.getData();
				if (oldAttribute == null) {
					continue;
				}
				if (!oldAttribute.getInherit().equals(newSchemaInfo.getUniqueName())) {
					CommonUITool.openErrorBox(Messages.errColumnNotDrop);
					return;
				}

				if (oldAttribute.isClassAttribute()) {
					newSchemaInfo.getClassAttributes().remove(oldAttribute);
				} else {
					newSchemaInfo.getAttributes().remove(oldAttribute);
					// newSchemaInfo.removeUniqueByAttrName(selec.getText(1));
					// For bug TOOLS-2390 After delete a column of a table,some
					// related index doesn't been deleted.
					newSchemaInfo.removeConstraintByAttrName(oldAttribute.getName());
					indexTableView.setInput(newSchemaInfo);
					fkTableView.setInput(newSchemaInfo);
				}
				SuperClassUtil.fireSuperClassChanged(database.getDatabaseInfo(), oldSchemaInfo,
						newSchemaInfo, newSchemaInfo.getSuperClasses());
				String oldAttrName = oldAttribute.getName();

				addDropAttrLog(oldAttrName, oldAttribute.isClassAttribute());
			}

			attrLabelProvider.setSchema(newSchemaInfo);
			loadColumnData();

			int itemCount = columnsTable.getItemCount();
			columnsTable.select(selectionIndex < itemCount ? selectionIndex : selectionIndex - 1);
			columnsTable.setFocus();
		}
	}

	/**
	 * Create foreign key/Index tab item.
	 *
	 * @param tabFolder the object of TabFolder
	 */
	private void createFkIndexTabItem(final TabFolder tabFolder) {
		final TabItem foreignKeyTabItem = new TabItem(tabFolder, SWT.NONE);
		foreignKeyTabItem.setText(Messages.infoIndexesTab);

		final Composite composite = new Composite(tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		composite.setLayout(gridLayout);
		foreignKeyTabItem.setControl(composite);

		// create the fk table viewer
		final Label fkLabel = new Label(composite, SWT.NONE);
		fkLabel.setText(Messages.lblFK);

		fkTableView = new TableViewer(composite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		fkTable = fkTableView.getTable();

		final GridData gdFkTable = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		fkTable.setLayoutData(gdFkTable);
		fkTable.setLinesVisible(true);
		fkTable.setHeaderVisible(true);

		TableColumn tblCol = new TableColumn(fkTable, SWT.NONE);
		tblCol.setWidth(100);
		tblCol.setText(Messages.tblColumnFK);

		tblCol = new TableColumn(fkTable, SWT.NONE);
		tblCol.setWidth(119);
		tblCol.setText(Messages.tblColumnColumnName);

		tblCol = new TableColumn(fkTable, SWT.NONE);
		tblCol.setWidth(93);
		tblCol.setText(Messages.tblColumnForeignTable);

		tblCol = new TableColumn(fkTable, SWT.NONE);
		tblCol.setWidth(143);
		tblCol.setText(Messages.tblColumnForeignColumnName);

		tblCol = new TableColumn(fkTable, SWT.NONE);
		tblCol.setWidth(84);
		tblCol.setText(Messages.tblColumnUpdateRule);

		tblCol = new TableColumn(fkTable, SWT.NONE);
		tblCol.setWidth(86);
		tblCol.setText(Messages.tblColumnDeleteRule);

		tblCol = new TableColumn(fkTable, SWT.NONE);
		tblCol.setWidth(100);
		tblCol.setText(Messages.tblColumnCacheColumn);

		FKTableViewerContentProvider fkContentProvider = new FKTableViewerContentProvider();
		FKTableViewerLabelProvider fkLabelProvider = new FKTableViewerLabelProvider(
				database.getDatabaseInfo());
		fkTableView.setContentProvider(fkContentProvider);
		fkTableView.setLabelProvider(fkLabelProvider);
		fkTableView.setInput(newSchemaInfo);
		CommonUITool.hackForYosemite(fkTableView.getTable());
		
		final Composite fkBtnComposite = new Composite(composite, SWT.NONE);
		final GridData gdBtnComposite = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		fkBtnComposite.setLayoutData(gdBtnComposite);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		fkBtnComposite.setLayout(gridLayout);

		final GridData gdFKBTN = new GridData(SWT.LEFT, SWT.CENTER, false, false);

		final Button addFKBTN = new Button(fkBtnComposite, SWT.NONE);
		addFKBTN.setLayoutData(gdFKBTN);
		addFKBTN.setText(Messages.btnAddFk);
		addFKBTN.addSelectionListener(new AddFkBtnListenerOnFkIndexTab());

		final Button editFKBTN = new Button(fkBtnComposite, SWT.NONE);
		editFKBTN.setLayoutData(gdFKBTN);
		editFKBTN.setEnabled(false);
		editFKBTN.setText(Messages.btnEditFk);
		editFKBTN.addSelectionListener(new EditFkBtnListenerOnFkIndexTab());

		final Button deleteFKBTN = new Button(fkBtnComposite, SWT.NONE);
		deleteFKBTN.setLayoutData(gdFKBTN);
		deleteFKBTN.setEnabled(false);
		deleteFKBTN.setText(Messages.btnDelFk);
		deleteFKBTN.addSelectionListener(new DelFkBtnListenerOnFkIndexTab());

		fkTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (database == null || database.getDatabaseInfo() == null || newSchemaInfo == null) {
					return;
				}

				TableItem[] items = fkTable.getSelection();
				if (items == null || items.length == 0) {
					deleteFKBTN.setEnabled(false);
					editFKBTN.setEnabled(false);
				} else {
					deleteFKBTN.setEnabled(true);
					editFKBTN.setEnabled(items.length == 1);
					for (TableItem item : items) {
						String fkName = item.getText(0);
						List<SchemaInfo> superList = SuperClassUtil.getSuperClasses(
								database.getDatabaseInfo(), newSchemaInfo.getSuperClasses());
						if (newSchemaInfo.isInSuperClasses(superList, fkName)) {
							deleteFKBTN.setEnabled(false);
							editFKBTN.setEnabled(false);
						}
					}
				}
			}
		});

		// create index table view
		final Label indexLabel = new Label(composite, SWT.NONE);
		indexLabel.setText(Messages.lblIndexes);

		indexTableView = new TableViewer(composite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		indexTable = indexTableView.getTable();
		indexTable.setLinesVisible(true);
		indexTable.setHeaderVisible(true);
		final GridData gdIndexTable = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		indexTable.setLayoutData(gdIndexTable);
		CommonUITool.hackForYosemite(indexTable);

		tblCol = new TableColumn(indexTable, SWT.NONE);
		tblCol.setWidth(150);
		tblCol.setText(Messages.tblColumnIndexName);

		tblCol = new TableColumn(indexTable, SWT.NONE);
		tblCol.setWidth(78);
		tblCol.setText(Messages.tblColumnIndexType);

		tblCol = new TableColumn(indexTable, SWT.NONE);
		tblCol.setWidth(218);
		tblCol.setText(Messages.tblColumnOnColumns);

		tblCol = new TableColumn(indexTable, SWT.NONE);
		tblCol.setWidth(282);
		tblCol.setText(Messages.tblColumnIndexRule);

		if (isCommentSupport) {
			tblCol = new TableColumn(indexTable, SWT.NONE);
			tblCol.setWidth(250);
			tblCol.setText(Messages.tblColumnIndexMemo);
		}

		IndexTableViewerContentProvider indexContentProvider = new IndexTableViewerContentProvider();
		IndexTableViewerLabelProvider indexLabelProvider = new IndexTableViewerLabelProvider();
		indexTableView.setContentProvider(indexContentProvider);
		indexTableView.setLabelProvider(indexLabelProvider);
		indexTableView.setInput(newSchemaInfo);
		indexTableView.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selected = (StructuredSelection) event.getSelection();
				if (selected == null) {
					return;
				}
				Constraint constraint = (Constraint) selected.getFirstElement();
				openEditIndexDialog(constraint);
			}
		});

		final Composite indexBtnComposite = new Composite(composite, SWT.NONE);
		final GridData gdIndexBtnComposite = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		indexBtnComposite.setLayoutData(gdIndexBtnComposite);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		indexBtnComposite.setLayout(gridLayout);

		final GridData gdIndexBTN = new GridData(SWT.LEFT, SWT.CENTER, false, false);

		final Button addIndexBTN = new Button(indexBtnComposite, SWT.NONE);
		addIndexBTN.setLayoutData(gdIndexBTN);
		addIndexBTN.setText(Messages.btnAddIndex);
		addIndexBTN.addSelectionListener(new AddIndexBtnListenerOnFkIndexTab());

		final Button editIndexBTN = new Button(indexBtnComposite, SWT.NONE);
		editIndexBTN.setLayoutData(gdIndexBTN);
		editIndexBTN.setEnabled(false);
		editIndexBTN.setText(Messages.btnEditIndex);
		editIndexBTN.addSelectionListener(new EditIndexBtnListenerOnFkIndexTab());

		final Button deleteIndexBTN = new Button(indexBtnComposite, SWT.NONE);
		deleteIndexBTN.setLayoutData(gdIndexBTN);
		deleteIndexBTN.setEnabled(false);
		deleteIndexBTN.setText(Messages.btnDelIndex);
		deleteIndexBTN.addSelectionListener(new DelIndexBtnListenerOnFkIndexTab());

		indexTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TableItem[] items = indexTable.getSelection();
				if (items == null || items.length == 0) {
					deleteIndexBTN.setEnabled(false);
					editIndexBTN.setEnabled(false);
				} else {
					deleteIndexBTN.setEnabled(true);
					editIndexBTN.setEnabled(items.length == 1);
				}
			}
		});
	}

	/**
	 * A class which listens the add button of FK part on the Fk/Index tab.
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-1-7 created by lizhiqiang
	 */
	private final class AddFkBtnListenerOnFkIndexTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			AddFKDialog dlg = new AddFKDialog(getSite().getShell(), database, newSchemaInfo, null);
			int returnCode = dlg.open();
			if (returnCode == AddFKDialog.OK) {
				Constraint fk = dlg.getRetFK();
				if (fk == null) {
					return;
				}
				newSchemaInfo.addConstraint(fk);
				SchemaChangeLog changeLog = new SchemaChangeLog(null, fk.getName(),
						SchemeInnerType.TYPE_FK);
				schemaChangeMgr.addSchemeChangeLog(changeLog);
				fkTableView.setInput(newSchemaInfo);
				fkTableView.refresh();
			}
		}
	}

	/**
	 * A class which listens the edit button of FK part on the Fk/Index tab.
	 *
	 * @author pangqiren
	 * @version 1.0 - 2012-7-9 created by pangqiren
	 */
	private final class EditFkBtnListenerOnFkIndexTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			TableItem[] selection = fkTable.getSelection();
			if (selection == null || selection.length == 0) {
				return;
			}
			Constraint editedFk = (Constraint) selection[0].getData();

			AddFKDialog dlg = new AddFKDialog(getSite().getShell(), database, newSchemaInfo,
					editedFk);
			int returnCode = dlg.open();
			if (returnCode == AddFKDialog.OK) {
				Constraint newFk = dlg.getRetFK();
				if (newFk == null) {
					return;
				}

				newSchemaInfo.removeFKConstraint(editedFk.getName());
				newSchemaInfo.addConstraint(newFk);
				SchemaChangeLog changeLog = new SchemaChangeLog(editedFk.getName(),
						newFk.getName(), SchemeInnerType.TYPE_FK);
				schemaChangeMgr.addSchemeChangeLog(changeLog);
				fkTableView.setInput(newSchemaInfo);
				fkTableView.refresh();
			}
		}
	}

	/**
	 * A class which listens the delete button of FK part on the Fk/Index tab.
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-1-7 created by lizhiqiang
	 */
	private final class DelFkBtnListenerOnFkIndexTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			if (database == null || database.getDatabaseInfo() == null || newSchemaInfo == null) {
				return;
			}

			TableItem[] selection = fkTable.getSelection();
			if (selection != null && selection.length >= 1) {
				String fkName = selection[0].getText(0);

				List<SchemaInfo> superList = SuperClassUtil.getSuperClasses(
						database.getDatabaseInfo(), newSchemaInfo.getSuperClasses());
				if (newSchemaInfo.isInSuperClasses(superList, fkName)) {
					CommonUITool.openErrorBox(Messages.errFKNotDrop);
					return;
				}

				newSchemaInfo.removeFKConstraint(fkName);
				SchemaChangeLog changeLog = new SchemaChangeLog(fkName, null,
						SchemeInnerType.TYPE_FK);
				schemaChangeMgr.addSchemeChangeLog(changeLog);
				fkTableView.setInput(newSchemaInfo);
			}
		}
	}

	/**
	 * A class which listens the add button of Index part on the Fk/Index tab.
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-1-7 created by lizhiqiang
	 */
	private final class AddIndexBtnListenerOnFkIndexTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			AddIndexDialog dlg = new AddIndexDialog(getSite().getShell(), newSchemaInfo, database,
					null, true);
			int returnCode = dlg.open();
			if (returnCode == AddIndexDialog.OK) {
				Constraint constraint = dlg.getIndexConstraint();
				if (constraint == null) {
					return;
				}

				newSchemaInfo.addConstraint(constraint);
				// For bug TOOLS-2394 Unique index can't be added again
				if (Constraint.ConstraintType.UNIQUE.getText().equals(constraint.getType())
						&& constraint.getAttributes().size() == 1) {
					DBAttribute attr = newSchemaInfo.getDBAttributeByName(
							constraint.getAttributes().get(0), false);
					attr.setUnique(true);
					loadColumnData();
				}

				String key = constraint.getDefaultName(newSchemaInfo.getUniqueName()) + "$"
						+ constraint.getName();
				schemaChangeMgr.addSchemeChangeLog(new SchemaChangeLog(null, key,
						SchemeInnerType.TYPE_INDEX));
				indexTableView.setInput(newSchemaInfo);
			}
		}
	}

	/**
	 * A class which listens the edit button of Index part on the Fk/Index tab.
	 *
	 * @author pangqiren
	 * @version 1.0 - 2012-7-9 created by pangqiren
	 */
	private final class EditIndexBtnListenerOnFkIndexTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			TableItem[] selection = indexTable.getSelection();
			if (selection == null || selection.length == 0) {
				return;
			}
			Constraint editedIndex = (Constraint) selection[0].getData();
			openEditIndexDialog(editedIndex);
		}
	}

	/**
	 * Open the index edit dialog with the constraint object
	 *
	 * @param editedIndex Constraint
	 */
	private void openEditIndexDialog(Constraint editedIndex) {
		boolean isNewConstraint = true;
		for (int i = 0, len = originalConstraints.size(); i < len; i++) {
			if (originalConstraints.get(i) == editedIndex) {
				isNewConstraint = false;
			}
		}

		AddIndexDialog dlg = new AddIndexDialog(getSite().getShell(), newSchemaInfo, database,
				editedIndex, isNewConstraint);
		int returnCode = dlg.open();
		if (returnCode == AddIndexDialog.OK) {
			Constraint newIndex = dlg.getIndexConstraint();
			if (newIndex == null) {
				return;
			}

			newSchemaInfo.removeConstraintByName(editedIndex.getName(), editedIndex.getType());
			newSchemaInfo.addConstraint(newIndex);

			// For bug TOOLS-2394 Unique index can't be added again
			if (Constraint.ConstraintType.UNIQUE.getText().equals(newIndex.getType())
					&& newIndex.getAttributes().size() == 1) {
				DBAttribute attr = newSchemaInfo.getDBAttributeByName(
						newIndex.getAttributes().get(0), false);
				attr.setUnique(true);
				loadColumnData();
			}

			boolean modifiedUK = Constraint.ConstraintType.UNIQUE.getText().equals(
					editedIndex.getType())
					&& editedIndex.getAttributes().size() == 1;
			boolean noNewUK = !Constraint.ConstraintType.UNIQUE.getText().equals(newIndex.getType())
					|| newIndex.getAttributes().size() != 1;

			if (modifiedUK && noNewUK) {
				String attrName = editedIndex.getAttributes().get(0);
				DBAttribute attr = newSchemaInfo.getDBAttributeByName(attrName, false);
				if (attr != null) {
					attr.setUnique(false);
					loadColumnData();
				}
			}

			String key1 = editedIndex.getDefaultName(newSchemaInfo.getUniqueName()) + "$"
					+ editedIndex.getName();
			String key2 = newIndex.getDefaultName(newSchemaInfo.getUniqueName()) + "$"
					+ newIndex.getName();
			SchemaChangeLog changeLog = new SchemaChangeLog(key1, key2, SchemeInnerType.TYPE_INDEX);
			schemaChangeMgr.addSchemeChangeLog(changeLog);
			indexTableView.setInput(newSchemaInfo);
		}
	}

	/**
	 * A class which listens the delete button of Index part on the Fk/Index tab
	 * item.
	 *
	 * @author lizhiqiang
	 * @version 1.0 - 2010-1-7 created by lizhiqiang
	 */
	private final class DelIndexBtnListenerOnFkIndexTab extends
			SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			if (database == null || database.getDatabaseInfo() == null || newSchemaInfo == null) {
				return;
			}

			TableItem[] selection = indexTable.getSelection();
			if (selection != null && selection.length >= 1) {
				String indexName = selection[0].getText(0);
				String indexType = selection[0].getText(1);
				Constraint index = newSchemaInfo.getConstraintByName(indexName, indexType);
				if (index == null) {
					CommonUITool.openErrorBox(Messages.errCanNotFindIndex);
					return;
				}

				List<SchemaInfo> superList = SuperClassUtil.getSuperClasses(
						database.getDatabaseInfo(), newSchemaInfo.getSuperClasses());
				if (newSchemaInfo.isInSuperClasses(superList, indexName)) {
					CommonUITool.openErrorBox(Messages.errIndexNotDrop);
					return;
				}
				if (indexType.equals(Constraint.ConstraintType.PRIMARYKEY.getText())) {
					newSchemaInfo.getConstraints().remove(index);
					new PkBtnListenerOnGeneTab(isNewTableFlag).firePKRemoved(newSchemaInfo, index);
				} else {
					// For bug TOOLS-2394 Unique index can't be added again
					if (Constraint.ConstraintType.UNIQUE.getText().equals(indexType)) {
						Constraint constraint = newSchemaInfo.getConstraintByName(indexName,
								indexType);
						if (constraint != null && constraint.getAttributes().size() == 1) {
							DBAttribute attr = newSchemaInfo.getDBAttributeByName(
									constraint.getAttributes().get(0), false);
							if (attr.isUnique()) {
								attr.setUnique(false);
							}
						}
					}
					newSchemaInfo.removeConstraintByName(indexName, indexType);

					String key = index.getDefaultName(newSchemaInfo.getUniqueName()) + "$"
							+ index.getName();
					SchemaChangeLog changeLog = new SchemaChangeLog(key, null,
							SchemeInnerType.TYPE_INDEX);
					schemaChangeMgr.addSchemeChangeLog(changeLog);
				}

				indexTableView.setInput(newSchemaInfo);
				loadColumnData();
			}
		}
	}

	private void init() {
		String owner = null;
		String title = null;

		if (oldSchemaInfo == null) {
			owner = database.getUserName();
		} else {
			owner = oldSchemaInfo.getOwner();
		}
		
		for (int i = 0, length = ownerCombo.getItemCount(); i < length; i++) {
			String item = ownerCombo.getItem(i);
			if (item != null && item.equalsIgnoreCase(owner)) {
				ownerCombo.select(i);
				break;
			}
		}
		if (oldSchemaInfo == null) {
			title = Messages.newTableShellTitle;

			classNameText.setText("");
			if (reuseOIDBtn != null) {
				reuseOIDBtn.setSelection(true);
			}
		} else {
			String className = oldSchemaInfo.getClassname();
			String tableName = oldSchemaInfo.getUniqueName();
			title = Messages.bind(Messages.editTableShellTitle, tableName);

			classNameText.setText(className);
			classNameText.setEnabled(false);
			if (reuseOIDBtn != null) {
				reuseOIDBtn.setSelection(oldSchemaInfo.isReuseOid());
			}
		}
		getSite().getShell().setText(title);

		loadColumnData();
	}

	private void fillOwnerCombo() {
		if (dbUserList == null) {
			return;
		}

		for (String userName : dbUserList) {
			if (!StringUtil.isEmpty(userName)) {
				String userNameUcase = userName.toUpperCase(Locale.getDefault());
				ownerCombo.add(userNameUcase);
			}
		}
	}

	private void fillCollationCombo() {
		if (collationList == null || WidgetUtil.disposed(collationCombo)) {
			return;
		}

		String searchCol = null;
		if (oldSchemaInfo != null) {
			searchCol = oldSchemaInfo.getCollation();
		} else if (newSchemaInfo != null) {
			searchCol = newSchemaInfo.getCollation();
		} else {
			searchCol = Collation.DEFAULT_COLLATION;
		}

		for (Collation collation : collationList) {
			if (collation == null) {
				continue;
			}

			String collationName = collation.getName();
			if (collationName == null) {
				continue;
			}

			collationCombo.add(collationName);
			if (collationName.equals(searchCol)) {
				collationCombo.select(collationCombo.getItemCount() - 1);
			}
		}

		if (collationCombo.getItemCount() > 0 && collationCombo.getSelectionIndex() == -1) {
			collationCombo.select(0);
		}
	}

	/**
	 * Verify the table name.
	 *
	 * @return
	 */
	private boolean verifyTableName() {
		if (WidgetUtil.disposed(classNameText)) {
			return false;
		}

		if (classNameText.getEnabled()) {
			String className = classNameText.getText();
			String msg = null;
			if (StringUtil.isEmpty(className)) {
				msg = Messages.errNoTableName;
			} else {
				boolean canNotUseMultibytes = ValidateUtil.notSupportMB(database)
						&& ValidateUtil.notASCII(className);

				if (!ValidateUtil.isValidIdentifier(className)) {
					msg = Messages.bind(Messages.renameInvalidTableNameMSG, "table", className);
				} else if (canNotUseMultibytes) {
					msg = Messages.errMultiBytes;
				} else {
					return true;
				}
			}

			CommonUITool.showErrorBaloon(tableNameComp, classNameText, errorBaloon, "", msg);
			classNameText.setFocus();
			return false;
		}

		return true;

	}

	/**
	 * Add a log of dropping an attribute to change list.
	 *
	 * @param isClassAttribute boolean
	 * @param oldAttrName String
	 */
	private void addDropAttrLog(String oldAttrName, boolean isClassAttribute) {
		SchemaChangeLog changeLog = new SchemaChangeLog(oldAttrName, null,
				getSchemaInnerType(isClassAttribute));
		schemaChangeMgr.addSchemeChangeLog(changeLog);
	}

	/**
	 * Return SchemaInnerType depending on class attribute.
	 *
	 * @param isClassAttr
	 * @return
	 */
	private SchemeInnerType getSchemaInnerType(boolean isClassAttr) {
		return isClassAttr ? SchemeInnerType.TYPE_CLASSATTRIBUTE : SchemeInnerType.TYPE_ATTRIBUTE;
	}

	/**
	 * Add a log of adding an attribute to change list.
	 *
	 * @param newAttrName String
	 * @param isClassAttribute boolean
	 */
	public void addNewAttrLog(String newAttrName, boolean isClassAttribute) {
		SchemaChangeLog changeLog = new SchemaChangeLog(null, newAttrName,
				getSchemaInnerType(isClassAttribute));
		schemaChangeMgr.addSchemeChangeLog(changeLog);
	}

	/**
	 * Add a log of change the attribute position.
	 *
	 * @param attrId
	 * @param isClassAttribute
	 */
	private void addPosAttrLog(DBAttribute attribute) {
		SchemaChangeLog changeLog = new SchemaChangeLog(attribute.getName(), attribute.getName(),
				SchemeInnerType.TYPE_POSITION);
		schemaChangeMgr.addSchemeChangeLog(changeLog);
	}

	/**
	 * Add a log of new unique key.
	 *
	 * @param unique
	 */
	private void addNewUniqueLog(Constraint unique) {
		String key = newSchemaInfo.getUniqueName() + "$" + unique.getName();
		SchemaChangeLog changeLog = new SchemaChangeLog(null, key, SchemeInnerType.TYPE_INDEX);
		schemaChangeMgr.addSchemeChangeLog(changeLog);
	}

	/**
	 * Add a log of delete an unique key.
	 *
	 * @param unique
	 */
	private void addDelUniqueLog(Constraint unique) {
		if (unique == null) {
			return;
		}

		String key = newSchemaInfo.getUniqueName() + "$" + unique.getName();
		SchemaChangeLog changeLog = new SchemaChangeLog(key, null, SchemeInnerType.TYPE_INDEX);
		schemaChangeMgr.addSchemeChangeLog(changeLog);
	}

	/**
	 * Add a log of editing an attribute to change list.
	 *
	 * @param attrName String
	 * @param isClassAttribute boolean
	 * @param newAttrName String
	 */
	private void addEditAttrLog(String attrName, String newAttrName, boolean isClassAttribute) {
		SchemaChangeLog changeLog = new SchemaChangeLog(attrName, newAttrName,
				getSchemaInnerType(isClassAttribute));
		schemaChangeMgr.addSchemeChangeLog(changeLog);
	}

	/**
	 * Get partition type.
	 *
	 * @return the PartitionType
	 */
	private PartitionType getPartitonType() {
		Object partitionInfo = partitionTableView.getElementAt(0);
		if (!(partitionInfo instanceof PartitionInfo)) {
			return null;
		}

		return ((PartitionInfo) partitionInfo).getPartitionType();
	}

	/**
	 * Load partition information list
	 */
	private void loadPartitionInfoList() {
		boolean isPartitionTable = false;
		if (!isNewTableFlag) {
			isPartitionTable = "YES".equals(oldSchemaInfo.isPartitionGroup());
		}

		if (isPartitionTable) {
			GetPartitionedClassListTask task = new GetPartitionedClassListTask(
					database.getDatabaseInfo());
			List<PartitionInfo> list = task.getPartitionItemList(newSchemaInfo.getUniqueName());

			partitionInfoList.clear();
			if (!ArrayUtil.isEmpty(list)) {
				PartitionInfo partitionInfo = list.get(0);
				if (partitionInfo != null
						&& partitionInfo.getPartitionType() == PartitionType.RANGE) {
					RangePartitionComparator comparator = new RangePartitionComparator(
							partitionInfo.getPartitionExprType());
					Collections.sort(list, comparator);
				}
				partitionInfoList.addAll(list);
			}
		}

		oldPartitionInfoList = new ArrayList<PartitionInfo>();
		for (int i = 0; i < partitionInfoList.size(); i++) {
			PartitionInfo partitionInfo = partitionInfoList.get(i);
			if (partitionInfo != null) {
				oldPartitionInfoList.add(partitionInfo.clone());
			}
		}

		if (oldSchemaInfo != null) {
			oldSchemaInfo.setPartitionList(oldPartitionInfoList);
		}

		if (newSchemaInfo != null) {
			newSchemaInfo.setPartitionList(partitionInfoList);
		}
	}

	/**
	 * Create Partition tab buttons
	 *
	 * @param parent Composite
	 */
	private void createPartitionTabButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		{
			GridLayout gl = new GridLayout();
			gl.numColumns = 5;
			composite.setLayout(gl);
			composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		}

		addPartitionBtn = new Button(composite, SWT.PUSH);
		{
			GridData gd = new GridData(SWT.NONE);
			gd.horizontalIndent = 10;
			addPartitionBtn.setLayoutData(gd);
		}
		addPartitionBtn.setText(Messages.btnAddPartition);
		addPartitionBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String ownerName = ownerCombo.getText();
				if (ownerName.trim().length() == 0) {
					CommonUITool.openErrorBox(getSite().getShell(), Messages.msgNoTableName);
					return;
				}
				
				String className = classNameText.getText();
				if (className.trim().length() == 0) {
					CommonUITool.openErrorBox(getSite().getShell(), Messages.msgNoTableName);
					return;
				}
				
				newSchemaInfo.setOwner(ownerName);
				newSchemaInfo.setClassname(className);
				if (isSupportUserSchema()) {
					newSchemaInfo.setUniqueName(ownerName + "." + className);
				} else {
					newSchemaInfo.setUniqueName(className);
				}

				Wizard wizard = new CreatePartitionWizard(database.getDatabaseInfo(),
						newSchemaInfo, partitionInfoList, isNewTableFlag, null);
				CMWizardDialog dialog = new CMWizardDialog(getSite().getShell(), wizard);
				dialog.setPageSize(600, 400);
				if (dialog.open() != IDialogConstants.OK_ID) {
					return;
				}

				partitionTableView.refresh();
				changePartitionTabButtonStatus();
			}
		});

		editPartitionBtn = new Button(composite, SWT.PUSH);
		{
			GridData gd = new GridData(SWT.NONE);
			gd.horizontalIndent = 10;
			editPartitionBtn.setLayoutData(gd);
		}
		editPartitionBtn.setText(Messages.btnEditPartition);
		editPartitionBtn.setEnabled(false);
		editPartitionBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editPartition();
			}
		});

		delPartitionBtn = new Button(composite, SWT.PUSH);
		{
			GridData gd = new GridData(SWT.NONE);
			gd.horizontalIndent = 10;
			delPartitionBtn.setLayoutData(gd);
		}
		delPartitionBtn.setText(Messages.btnDelPartition);
		delPartitionBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String confirmMsg = Messages.msgDelPartition;
				if (getPartitonType() == PartitionType.HASH) {
					confirmMsg = Messages.msgDelHashPartition;
				}
				boolean deleteConfirm = CommonUITool.openConfirmBox(getSite().getShell(),
						confirmMsg);
				if (!deleteConfirm) {
					return;
				}

				if (getPartitonType() == PartitionType.HASH) {
					partitionInfoList.clear();
				} else {
					IStructuredSelection selection = (IStructuredSelection) partitionTableView.getSelection();
					if (selection == null || selection.isEmpty()) {
						return;
					}

					partitionInfoList.removeAll(selection.toList());
					if (getPartitonType() == PartitionType.RANGE) {
						CreatePartitionWizard.resetRangePartitionInfoList(partitionInfoList);
					}
				}

				partitionTableView.refresh();
				changePartitionTabButtonStatus();
			}
		});

		final Table partitionTable = partitionTableView.getTable();
		partitionTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				changePartitionTabButtonStatus();
			}
		});

		changePartitionTabButtonStatus();
	}

	/**
	 * Show the edit dialog for selected partition.
	 */
	private void editPartition() {
		if (WidgetUtil.disposed(partitionTableView)) {
			return;
		}

		PartitionInfo partitionInfo = null;
		if (getPartitonType() == PartitionType.HASH) {
			partitionInfo = partitionInfoList.get(0);
		} else {
			IStructuredSelection selection = (IStructuredSelection) partitionTableView.getSelection();
			if (selection == null || selection.isEmpty()) {
				return;
			}
			partitionInfo = (PartitionInfo) selection.getFirstElement();
		}

		String ownerName = ownerCombo.getText();
		if (WidgetUtil.disposed(ownerCombo) || StringUtil.isEmpty(ownerName)) {
			CommonUITool.openErrorBox(getSite().getShell(), Messages.msgNoTableName);
			return;
		}
		
		String className = classNameText.getText();
		if (WidgetUtil.disposed(classNameText) || StringUtil.isEmpty(className)) {
			CommonUITool.openErrorBox(getSite().getShell(), Messages.msgNoTableName);
			return;
		}
		
		newSchemaInfo.setOwner(ownerName);
		newSchemaInfo.setClassname(className);
		if (database.getDatabaseInfo().isSupportUserSchema()) {
			newSchemaInfo.setUniqueName(ownerName + "." + className);
		} else {
			newSchemaInfo.setUniqueName(className);
		}
		
		CreatePartitionWizard wizard = new CreatePartitionWizard(database.getDatabaseInfo(),
				newSchemaInfo, partitionInfoList, isNewTableFlag, partitionInfo);
		CMWizardDialog dialog = new CMWizardDialog(getSite().getShell(), wizard);
		dialog.setPageSize(600, 400);
		if (dialog.open() != IDialogConstants.OK_ID) {
			return;
		}

		newSchemaInfo.setPartitionList(partitionInfoList);
		partitionTableView.refresh();
		changePartitionTabButtonStatus();
	}

	/**
	 * Change the partition tab's button state.
	 */
	private void changePartitionTabButtonStatus() {
		Table partitionTable = partitionTableView.getTable();
		int selectedCount = partitionTable.getSelectionCount();
		int itemCount = partitionTable.getItemCount();

		boolean canAdd = getPartitonType() != PartitionType.HASH;
		addPartitionBtn.setEnabled(canAdd);

		boolean canModify = (itemCount > 0 && getPartitonType() == PartitionType.HASH)
				|| (selectedCount > 0 && getPartitonType() != null);
		editPartitionBtn.setEnabled(canModify);
		delPartitionBtn.setEnabled(canModify);
	}

	private void createPartitionTabItem(final TabFolder tabFolder) {
		loadPartitionInfoList();

		final TabItem partTabItem = new TabItem(tabFolder, SWT.NONE);
		final Composite parentComp = new Composite(tabFolder, SWT.NONE);
		{
			GridLayout gd = new GridLayout();
			parentComp.setLayout(gd);
		}
		partTabItem.setControl(parentComp);
		partTabItem.setText(Messages.tabItemPartition);

		partitionTableView = new TableViewer(parentComp, SWT.FULL_SELECTION | SWT.MULTI
				| SWT.BORDER);

		final Table partitionTable = partitionTableView.getTable();
		{
			if (isCommentSupport) {
				partitionTable.setLayout(TableViewUtil.createTableViewLayout(
						new int[] { 20, 15, 10,	20, 25, 10, 20 }));
			} else {
				partitionTable.setLayout(TableViewUtil.createTableViewLayout(
						new int[] { 20, 15, 10,	20, 25, 10 }));
			}
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 350;
			partitionTable.setLayoutData(gd);
		}

		partitionTable.setHeaderVisible(true);
		partitionTable.setLinesVisible(true);
		CommonUITool.hackForYosemite(partitionTable);
		
		TableViewUtil.createTableColumn(partitionTable, SWT.CENTER, Messages.tblColTableName);
		TableViewUtil.createTableColumn(partitionTable, SWT.CENTER, Messages.tblColPartitionName);
		TableViewUtil.createTableColumn(partitionTable, SWT.CENTER, Messages.tblColType);
		TableViewUtil.createTableColumn(partitionTable, SWT.CENTER, Messages.tblColExpr);
		TableViewUtil.createTableColumn(partitionTable, SWT.CENTER, Messages.tblColExprValue);
		TableViewUtil.createTableColumn(partitionTable, SWT.CENTER, Messages.tblColRows);
		if (isCommentSupport) {
			TableViewUtil.createTableColumn(partitionTable, SWT.CENTER, Messages.tblColPartitionDescription);
		}

		partitionTableView.setLabelProvider(new PartitionTableLabelProvider());
		partitionTableView.setContentProvider(new PartitionContentProvider());
		partitionTableView.setInput(partitionInfoList);

		partitionTableView.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				editPartition();
			}
		});

		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				if (tabFolder.getSelection()[0].getText().equals(Messages.tabItemPartition)) {
					partitionTableView.refresh();
				}
			}
		});

		createPartitionTabButtons(parentComp);
	}

	public void setDbUserList(List<String> dbUserList) {
		this.dbUserList = dbUserList;
	}

	/**
	 * After a task has been executed, do some thing such as refresh.
	 *
	 * @param task the task
	 * @return IStatus if complete refresh false if run into error
	 *
	 */
	public IStatus postTaskFinished(ITask task) {
		if (database != null && database.getDatabaseInfo() != null
				&& task instanceof CommonSQLExcuterTask) {
			database.getDatabaseInfo().removeSchema(getTableName());
		}
		return Status.OK_STATUS;
	}

	public void completeAll() {
		boolean isSupportUserSchema = database.getDatabaseInfo().isSupportUserSchema();
		String tableName;
		if (isSupportUserSchema) {
			tableName = owner + "." + className;
		} else {
			tableName = className;
		}
		
		CommonUITool.openInformationBox(Messages.titleSuccess,
				Messages.bind(Messages.msgNull2DefComplete, jobName));

		if (this.isNewTableFlag) {
			ICubridNode node = database.getChild(database.getId()
					+ ICubridNodeLoader.NODE_SEPARATOR + CubridTablesFolderLoader.TABLES_FOLDER_ID);
			if (node == null || !node.getLoader().isLoaded()) {
				return;
			}
			String id = node.getId() + ICubridNodeLoader.NODE_SEPARATOR + tableName;
			boolean isPartition = newSchemaInfo.getPartitionList() != null
					&& newSchemaInfo.getPartitionList().size() > 0;
			
			ClassInfo newClassInfo = new ClassInfo(className, owner, ClassType.NORMAL, false,
					isPartition, isSupportUserSchema);
			ICubridNode newNode = CubridTablesFolderLoader.createUserTableNode(node, id,
					newClassInfo, node.getLoader().getLevel(), new NullProgressMonitor());

			if (CubridNavigatorView.findNavigationView() == null) {
				getSite().getWorkbenchWindow().getActivePage().closeEditor(editor, false);
				return;
			}

			TreeViewer treeViewer = CubridNavigatorView.findNavigationView().getViewer();
			if (treeViewer == null || treeViewer.getTree() == null
					|| treeViewer.getTree().isDisposed()) {
				getSite().getWorkbenchWindow().getActivePage().closeEditor(editor, false);
				return;
			}
			CommonUITool.addNodeToTree(treeViewer, node, newNode);
			// refresh table folder count label
			CommonUITool.updateFolderNodeLabelIncludingChildrenCount(treeViewer, node);
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(newNode, CubridNodeChangedEventType.NODE_ADD));
		} else {
			if (database == null) {
				return;
			}

			database.getDatabaseInfo().removeSchema(oldSchemaInfo.getUniqueName());
			if (oldPartitionInfoList.isEmpty() && !partitionInfoList.isEmpty()) {
				editedTableNode.setIconPath("icons/navigator/schema_table_partition.png");
				editedTableNode.setType(NodeType.USER_PARTITIONED_TABLE_FOLDER);
				editedTableNode.setLoader(new CubridPartitionedTableLoader());
			} else if (!oldPartitionInfoList.isEmpty() && partitionInfoList.isEmpty()) {
				editedTableNode.setIconPath("icons/navigator/schema_table_item.png");
				editedTableNode.setType(NodeType.USER_TABLE);
				editedTableNode.setLoader(new CubridUserTableLoader());
			}
			TreeViewer treeViewer = CubridNavigatorView.findNavigationView().getViewer();
			if (treeViewer == null || treeViewer.getTree() == null
					|| treeViewer.getTree().isDisposed()) {
				getSite().getWorkbenchWindow().getActivePage().closeEditor(editor, false);
				return;
			}
			
			if (database.getDatabaseInfo().isSupportUserSchema()) {
				editedTableNode.setLabel("[" + owner + "] " + className);
			} else {
				editedTableNode.setLabel(className);
			}
			editedTableNode.setUniqueName(tableName);
			CommonUITool.refreshNavigatorTree(treeViewer, editedTableNode);
			CommonUITool.updateFolderNodeLabelIncludingChildrenCount(treeViewer, editedTableNode);
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(editedTableNode,
							CubridNodeChangedEventType.NODE_REFRESH));
			/* Broadcast the view changed */
			QueryEditorUtil.fireSchemaNodeChanged(editedTableNode);
		}

		getSite().getWorkbenchWindow().getActivePage().closeEditor(editor, false);
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		TableEditorInput tableInfoEditorInput = (TableEditorInput) input;
		this.database = tableInfoEditorInput.getDatabase();
		this.editedTableNode = tableInfoEditorInput.getEditedTableNode();
		this.isNewTableFlag = tableInfoEditorInput.isNewTableFlag();
		this.dbUserList = tableInfoEditorInput.getDbUserList();
		this.showDefaultType = tableInfoEditorInput.getType();
		this.collationList = tableInfoEditorInput.getCollationList();
		if (collationList != null) {
			Collation emptyCollation = new Collation();
			emptyCollation.setCharset("");
			emptyCollation.setName("");
			collationList.add(0, emptyCollation);
		}
		this.oldSchemaInfo = tableInfoEditorInput.getSchemaInfo();
		this.supportCharset = CompatibleUtil.isSupportCreateDBByCharset(database.getDatabaseInfo());
		if (isNewTableFlag) {
			newSchemaInfo = new SchemaInfo();
			newSchemaInfo.setOwner(database.getUserName());
			newSchemaInfo.setClassname(""); //$NON-NLS-1$
			if (isSupportUserSchema()) {
				newSchemaInfo.setUniqueName(database.getUserName() + ".");
			} else {
				newSchemaInfo.setUniqueName(""); //$NON-NLS-1$
			}
			newSchemaInfo.setDbname(database.getName());
			newSchemaInfo.setType(Messages.userSchema);
			newSchemaInfo.setVirtual(Messages.schemaTypeClass);
			if (database.getDatabaseInfo() != null) {
				newSchemaInfo.setCollation(database.getDatabaseInfo().getCollation());
			}
		} else {
			newSchemaInfo = null;
			if (tableInfoEditorInput.getSchemaInfo() != null) {
				newSchemaInfo = tableInfoEditorInput.getSchemaInfo().clone();
				originalConstraints.addAll(newSchemaInfo.getConstraints());
			}
		}

		if (supportCharset) {
			columnProperites = new String[] { IAttributeColumn.COL_EMPTY,
					IAttributeColumn.COL_FLAG, IAttributeColumn.COL_NAME,
					IAttributeColumn.COL_DATATYPE, IAttributeColumn.COL_DEFAULT,
					IAttributeColumn.COL_AUTO_INCREMENT, IAttributeColumn.COL_NOT_NULL,
					IAttributeColumn.COL_PK, IAttributeColumn.COL_UK, IAttributeColumn.COL_SHARED,
					IAttributeColumn.COL_COLLATION, IAttributeColumn.COL_MEMO };
		} else {
			columnProperites = new String[] { IAttributeColumn.COL_EMPTY,
					IAttributeColumn.COL_FLAG, IAttributeColumn.COL_NAME,
					IAttributeColumn.COL_DATATYPE, IAttributeColumn.COL_DEFAULT,
					IAttributeColumn.COL_AUTO_INCREMENT, IAttributeColumn.COL_NOT_NULL,
					IAttributeColumn.COL_PK, IAttributeColumn.COL_UK, IAttributeColumn.COL_SHARED,
					IAttributeColumn.COL_MEMO };
		}

		Connection conn = null;
		try {
			conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);

			IDatabaseSpec dbSpec = database.getDatabaseInfo();
			isSupportTableComment = SchemaCommentHandler.isInstalledMetaTable(dbSpec, conn);
			database.getDatabaseInfo().setSupportTableComment(isSupportTableComment);
			if (CompatibleUtil.isAfter112(database.getDatabaseInfo())) {
				database.getDatabaseInfo().setSupportUserSchema(true);
			}

			if (isSupportTableComment && !isNewTableFlag && newSchemaInfo != null) {
				Map<String, SchemaComment> map = SchemaCommentHandler.loadDescription(dbSpec, conn,
						database.getDatabaseInfo().isSupportUserSchema(), newSchemaInfo.getUniqueName());

				for (DBAttribute attr : newSchemaInfo.getAttributes()) {
					SchemaComment schemaComment = SchemaCommentHandler.find(map,
							newSchemaInfo.getUniqueName(), attr.getName());
					if (schemaComment != null) {
						attr.setDescription(schemaComment.getDescription());
					}
				}

				// get description for index
				for (Constraint cons : newSchemaInfo.getConstraints()) {
					if (isCommentSupport) {
						String indexName = cons.getName();
						SchemaComment indexComment = SchemaCommentHandler.loadObjectDescription(
								dbSpec, conn, database.getDatabaseInfo().isSupportUserSchema(), indexName, CommentType.INDEX);
						if (indexComment != null) {
							cons.setDescription(indexComment.getDescription());
						}
					}
				}

				SchemaComment schemaComment = SchemaCommentHandler.find(map,
						newSchemaInfo.getUniqueName(), null);
				if (schemaComment != null) {
					newSchemaInfo.setDescription(schemaComment.getDescription());
				}
			}
		} catch (SQLException e) {
			LOGGER.error("", e);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(conn);
		}

		schemaChangeMgr = new SchemaChangeManager(database.getDatabaseInfo(), isNewTableFlag);
		schemaDDL = new SchemaDDL(schemaChangeMgr, database.getDatabaseInfo());
		if (database != null) {
			isSupportChange = CompatibleUtil.isSupportChangeColumn(database.getDatabaseInfo());
		}

		setSite(site);
		setInput(input);
		setPartName(input.getName());
		setTitleToolTip(input.getName());
		setTitleImage(CommonUIPlugin.getImage("icons/navigator/schema_table.png"));
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void setFocus() {
		if (isNewTableFlag) {
			classNameText.setFocus();
		} else {
			okBtn.setFocus();
		}
	}

	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode cubridNode = event.getCubridNode();
		CubridNodeChangedEventType eventType = event.getType();
		if (cubridNode == null || eventType == null) {
			return;
		}

		if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(event.getType())) {
			close(event, database.getServer());
		}

		if (CubridNodeChangedEventType.DATABASE_LOGOUT.equals(event.getType())
				|| CubridNodeChangedEventType.DATABASE_STOP.equals(event.getType())) {
			close(event, database);
		}
	}

	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public String getColumnProperty(int index) {
		if (index >= 0 && index < columnProperites.length) {
			return columnProperites[index];
		}
		return "";
	}

	public SchemaInfo getNewSchemaInfo() {
		return newSchemaInfo;
	}

	public SchemaInfo getOldSchemaInfo() {
		return oldSchemaInfo;
	}

	public boolean isNewTableFlag() {
		return isNewTableFlag;
	}

	public boolean isSupportChange() {
		return isSupportChange;
	}

	/**
	 * Get collation array
	 *
	 * @return
	 */
	public String[] getCollationArray() {
		if (collationList == null) {
			return new String[0];
		}
		String[] array = new String[collationList.size()];
		for (int i = 0; i < collationList.size(); i++) {
			array[i] = collationList.get(i).getName();
		}

		return array;
	}

	/**
	 * Get collation index by name
	 *
	 * @param collationName
	 * @return
	 */
	public int getCollationIndex(String collationName) {
		if (collationList == null) {
			return -1;
		}

		for (int i = 0; i < collationList.size(); i++) {
			if (StringUtil.isEqualNotIgnoreNull(collationName, collationList.get(i).getName())) {
				return i;
			}
		}

		return -1;
	}

	/**
	 *
	 * @return the columnsTable
	 */
	public Table getColumnsTable() {
		return columnsTable;
	}

	/**
	 * Remove temporary attribute by name
	 *
	 * @param name
	 */
	public void removeTempDBAttributeByName(String name) {
		List<DBAttribute> removeList = new ArrayList<DBAttribute>();
		for (DBAttribute attr : tempDBAttributeList) {
			if (!StringUtil.isEmpty(name) && StringUtil.isEqual(name, attr.getName())) {
				removeList.add(attr);
			}
		}
		tempDBAttributeList.removeAll(removeList);
	}

	public boolean isSupportTableComment() {
		return isSupportTableComment;
	}
	
	private boolean isSupportUserSchema() {
		if (database != null) {
			return database.getDatabaseInfo().isSupportUserSchema();
		}
		return false;
	}
	
	private String getTableName() {
		if (database.getDatabaseInfo().isSupportUserSchema()) {
			return owner + "." + className;
		} else {
			return className;
		}
	}
	
	private void modifySchemaInfo() {
		String className = classNameText.getText();
		newSchemaInfo.setClassname(className);
		if (isSupportUserSchema()) {
			if (ownerCombo != null && !ownerCombo.getText().isEmpty()) {
				newSchemaInfo.setUniqueName(ownerCombo.getText() + "." + className);
			} else {
				newSchemaInfo.setUniqueName(className);
			}
		} else {
			newSchemaInfo.setUniqueName(className);
		}
	}
}
