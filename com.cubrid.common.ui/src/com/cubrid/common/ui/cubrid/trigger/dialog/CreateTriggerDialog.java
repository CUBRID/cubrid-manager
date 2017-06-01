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
package com.cubrid.common.ui.cubrid.trigger.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.trigger.Messages;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetTablesTask;
import com.cubrid.cubridmanager.core.cubrid.trigger.model.TriggerDDL;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.AddTriggerTask;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.AlterTriggerTask;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCSqlExecuteTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerActionTime;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerConditionTime;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerEvent;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerStatus;

/**
 *
 * Create trigger UI dialog
 *
 * @author wangmoulin
 * @version 1.0 - 2009-12-28 created by wangmoulin
 */
public class CreateTriggerDialog extends
		CMTitleAreaDialog {
	private StyledText sqlText;
	private final Color white = ResourceManager.getColor(SWT.COLOR_WHITE);
	private Text triggerNameText = null;
	private Text triggerDescriptionText = null;
	private Combo triggerTargetTableCombo = null;
	private Combo triggerTargetColumnCombo = null;
	private Text triggerConditionText = null;
	private Text triggerActionText = null;
	private Text triggerPriorityText = null;

	private final CubridDatabase database;
	private Button[] eventTimeBTNs;
	private Button[] eventTypeBTNs;
	private Button[] actionTimeBTNs;
	private Button[] actionTypeBTNs;
	private Button[] statusBTNs;

	private Trigger trigger = null;
	private TabFolder tabFolder;
	public final static int ALTER_TRIGGER_OK_ID = 100;
	private boolean isCommentSupport = false;

	private final String[][] eventTimeMap = {
			{Messages.eventTimeBefore, "BEFORE" },
			{Messages.eventTimeAfter, "AFTER" },
			{Messages.eventTimeDeferred, "DEFERRED" } };

	private final String[][] eventTypeMap = {
			{Messages.eventTypeInsert, "INSERT" },
			{Messages.eventTypeSInsert, "STATEMENT INSERT" },
			{Messages.eventTypeUpdate, "UPDATE" },
			{Messages.eventTypeSUpdate, "STATEMENT UPDATE" },
			{Messages.eventTypeDelete, "DELETE" },
			{Messages.eventTypeSDelete, "STATEMENT DELETE" },
			{Messages.eventTypeCommit, "COMMIT" },
			{Messages.eventTypeRollback, "ROLLBACK" }, };

	private final String[][] actionTimeMap = {
			{Messages.actionTimeDefault, "DEFAULT" },
			{Messages.actionTimeAfter, "AFTER" },
			{Messages.actionTimeDeferred, "DEFERRED" } };

	private final String[][] actionTypeMap = {
			{Messages.actionTypeReject, "REJECT" },
			{Messages.actionTypePrint, "PRINT" },
			{Messages.actionTypeOtherSQL, "OTHER STATEMENT" },
			{Messages.actionTypeInvalidateTransaction, "INVALIDATE TRANSACTION" } };

	public CreateTriggerDialog(Shell parentShell, CubridDatabase database) {
		super(parentShell);
		this.database = database;
	}

	public CreateTriggerDialog(Shell parentShell, CubridDatabase database,
			Trigger trigger) {
		super(parentShell);
		this.database = database;
		this.trigger = trigger;
	}

	/**
	 * Create dialog area content
	 *
	 * @param parent the parent composite
	 *
	 * @return the composite
	 */
	protected Control createDialogArea(Composite parent) {
		isCommentSupport = CompatibleUtil.isCommentSupports(database.getDatabaseInfo());
		Composite parentComp = (Composite) super.createDialogArea(parent);

		parentComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite tabComposite = new Composite(parentComp, SWT.NONE);
		{
			final GridData gdComposite = new GridData(SWT.FILL, SWT.FILL, true,
					true);
			tabComposite.setLayoutData(gdComposite);
			GridLayout tabCompositeLayout = new GridLayout();
			tabCompositeLayout.numColumns = 1;
			tabCompositeLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			tabCompositeLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			tabCompositeLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			tabCompositeLayout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			tabCompositeLayout.numColumns = 1;
			tabComposite.setLayout(tabCompositeLayout);
		}

		tabFolder = new TabFolder(tabComposite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		//create trigger tab
		final TabItem triggerTabItem = new TabItem(tabFolder, SWT.NONE);
		triggerTabItem.setText(Messages.infoTriggerTab);
		triggerTabItem.setControl(createTriggerComposite(tabFolder));

		//create the SQL tab
		final Composite sqlScriptComposite = new Composite(tabFolder, SWT.NONE);
		sqlScriptComposite.setLayout(new GridLayout());
		final TabItem sqlScriptTabItem = new TabItem(tabFolder, SWT.NONE);
		sqlScriptTabItem.setText(Messages.infoSQLScriptTab);
		sqlScriptTabItem.setControl(sqlScriptComposite);

		sqlText = new StyledText(sqlScriptComposite, SWT.WRAP | SWT.V_SCROLL
				| SWT.READ_ONLY | SWT.H_SCROLL | SWT.BORDER);
		CommonUITool.registerContextMenu(sqlText, false);
		sqlText.setBackground(white);
		sqlText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		setTitle(Messages.newTriggerMSGTitle);
		setMessage(Messages.newTriggerMsg);

		createInit();
		alterInit();
		addListener();
		return parentComp;

	}

	/**
	 *
	 * Create the trigger tab composite
	 *
	 * @param parent Composite
	 * @return Composite
	 */
	private Composite createTriggerComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		{
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout layout = new GridLayout();
			layout.numColumns = 4;
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
		}
		// Trigger Name
		Label triggerNameLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		triggerNameLabel.setText(Messages.triggerName);
		triggerNameText = new Text(composite, SWT.BORDER);
		triggerNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		triggerNameText.setLayoutData(gridData);

		createOptionGroup(composite);
		createTriggerEventGroup(composite);
		createTriggerActionGroup(composite);

		return composite;
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		if (null == trigger) {
			getShell().setText(Messages.newTriggerMSGTitle);
		} else {
			getShell().setText(Messages.alterTriggerShellTitle);
		}
	}

	/**
	 * Create buttons for button bar
	 *
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		if (null == trigger) {
			createButton(parent, IDialogConstants.OK_ID, Messages.okBTN, false);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else {
			createButton(parent, ALTER_TRIGGER_OK_ID, Messages.okBTN, false);
			getButton(ALTER_TRIGGER_OK_ID).setEnabled(false);
		}
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.cancleBTN,
				false);
	}

	/**
	 * When press button,call it
	 *
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId != IDialogConstants.OK_ID
				&& buttonId != ALTER_TRIGGER_OK_ID) {
			super.buttonPressed(buttonId);
			return;
		}

		if (!validateAll()) {
			return;
		}
		executeByJDBC(buttonId);
//		triggerName = triggerNameText.getText();
//		if (ApplicationUtil.getApplicationType() == ApplicationType.CUBRID_MANAGER) {
//			executeBySocket(buttonId);
//		} else {
//		executeByJDBC(buttonId);
//		}
	}

	/**
	 *
	 * Execute to add or alter trigger by JDBC
	 *
	 * @param buttonId int
	 */
	private void executeByJDBC(int buttonId) { // FIXME move this logic to core module
		String createSQL = generateSqlText().toString();
		if (StringUtil.isEmpty(createSQL)) {
			return;
		}
		String taskName = null;
		String message = null;
		if (buttonId == IDialogConstants.OK_ID) {
			taskName = Messages.bind(Messages.addTriggerTaskName, triggerName);
			message = Messages.newTriggerSuccess;
		} else if (buttonId == ALTER_TRIGGER_OK_ID) {
			createSQL = createSQL.substring(createSQL.indexOf("ALTER TRIGGER "));
			message = Messages.alterTriggerSuccess;
			taskName = Messages.bind(Messages.alterTriggerTaskName, triggerName);
		}
		// add or alter triggers by JDBC
		JDBCSqlExecuteTask jdbcTask = new JDBCSqlExecuteTask(taskName,
				database.getDatabaseInfo(), createSQL);
		TaskExecutor taskExecutor = new CommonTaskExec(taskName);
		taskExecutor.addTask(jdbcTask);
		new ExecTaskWithProgress(taskExecutor).busyCursorWhile();
		if (taskExecutor.isSuccess()) {
			triggerName = triggerNameText.getText();
			setReturnCode(buttonId);
			close();
			CommonUITool.openInformationBox(Messages.msgInformation, message);
		}
	}

	/**
	 *
	 * Execute to add or alter trigger by socket
	 *
	 * @param buttonId int
	 */
	private void executeBySocket(int buttonId) { // FIXME remove
		String taskName = null;
		String message = null;
		ITask executedTask = null;
		if (buttonId == IDialogConstants.OK_ID) {
			String eventType = getEventType();
			String triggerEventTargetTable = triggerTargetTableCombo.getText().trim();
			String triggerEventTargetColumn = triggerTargetColumnCombo.getText().trim();

			AddTriggerTask task = new AddTriggerTask(
					database.getServer().getServerInfo());
			task.setDbName(database.getName());
			task.setTriggerName("\"" + triggerName + "\"");

			String eventTime = getEventTime();
			task.setConditionTime(TriggerConditionTime.eval(eventTime));
			task.setEventType(TriggerEvent.eval(eventType));

			String triggerActionType = getActionType();
			String triggerAction = triggerActionText.getText().trim();
			String crChar = "\r";
			String nlChar = "\n";
			triggerAction = triggerAction.replaceAll(crChar, "");
			triggerAction = triggerAction.replaceAll(nlChar, " ");
			task.setAction(Trigger.TriggerAction.eval(triggerActionType), triggerAction);

			if (triggerEventTargetTable.length() > 0) {
				if (triggerEventTargetColumn.length() > 0) {
					task.setEventTarget("\"" + triggerEventTargetTable
							+ "\"(\"" + triggerEventTargetColumn + "\")");
				} else {
					task.setEventTarget("\"" + triggerEventTargetTable + "\"");
				}
			}

			String actionTime = getActionTime();
			if (!actionTime.equals(Messages.actionTimeDefault)) { // action time selected
				task.setActionTime(TriggerActionTime.eval(actionTime));
			}

			String triggerCondition = triggerConditionText.getText().trim();
			triggerCondition = triggerCondition.replaceAll(crChar, "");
			triggerCondition = triggerCondition.replaceAll(nlChar, " ");

			task.setCondition(triggerCondition);

			String triggerStatus = this.getStatus();
			task.setStatus(TriggerStatus.eval(triggerStatus));

			String strPriority = triggerPriorityText.getText();
			task.setPriority(strPriority);

			taskName = Messages.bind(Messages.addTriggerTaskName, triggerName);
			message = Messages.newTriggerSuccess;
			executedTask = task;
		} else if (buttonId == ALTER_TRIGGER_OK_ID) {
			AlterTriggerTask task = new AlterTriggerTask(
					database.getServer().getServerInfo());
			task.setDbName(database.getName());
			task.setTriggerName(trigger.getName());

			String triggerStatus = this.getStatus();
			task.setStatus(TriggerStatus.eval(triggerStatus));

			String strPriority = triggerPriorityText.getText();
			task.setPriority(strPriority);

			taskName = Messages.bind(Messages.alterTriggerTaskName,
					trigger.getName());
			message = Messages.alterTriggerSuccess;
			executedTask = task;
		}

		TaskExecutor taskExecutor = new CommonTaskExec(taskName);
		taskExecutor.addTask(executedTask);
		new ExecTaskWithProgress(taskExecutor).busyCursorWhile();
		if (taskExecutor.isSuccess()) {
			setReturnCode(buttonId);
			close();
			CommonUITool.openInformationBox(Messages.msgInformation, message);
		}
	}

	/**
	 *
	 * Init data when create trigger
	 *
	 */
	private void createInit() {

		triggerNameText.setToolTipText(Messages.triggerToolTipName);
		triggerTargetTableCombo.setToolTipText(Messages.triggerToolTipEventTarget);
		triggerTargetColumnCombo.setToolTipText(Messages.triggerToolTipEventTarget);
		triggerConditionText.setToolTipText(Messages.triggerToolTipCondition);
		triggerActionText.setToolTipText(Messages.triggerToolTipActivity);
		triggerPriorityText.setToolTipText(Messages.triggerToolTipPriority);

		for (Button b : eventTimeBTNs) {
			b.setToolTipText(Messages.triggerToolTipEventTime);
		}
		for (Button b : eventTypeBTNs) {
			b.setToolTipText(Messages.triggerToolTipDatabaseEventType);
		}
		for (Button b : actionTimeBTNs) {
			b.setToolTipText(Messages.triggerToolTipDelayedTime);
		}
		for (Button b : actionTypeBTNs) {
			b.setToolTipText(Messages.triggerToolTipActivityType);
		}
		for (Button b : statusBTNs) {
			b.setToolTipText(Messages.triggerToolTipStatus);
		}

		triggerTargetTableCombo.setVisibleItemCount(20);
		triggerTargetColumnCombo.setVisibleItemCount(20);
		addTables();
		eventTimeBTNs[0].setSelection(true); // Before

		eventTypeBTNs[0].setSelection(true); // insert
		triggerTargetColumnCombo.setEnabled(false);

		actionTimeBTNs[0].setSelection(true); // default
		actionTypeBTNs[0].setSelection(true); // reject
		triggerActionText.setEnabled(false);
		statusBTNs[0].setSelection(true); // active
		triggerNameText.setFocus();

	}

	/**
	 *
	 * Init the data when alter trigger
	 *
	 */
	private void alterInit() {
		if (trigger == null) {
			return;
		}
		setMessage(Messages.triggerAlterMSG);
		setTitle(Messages.triggerAlterMSGTitle);

		triggerNameText.setText(trigger.getName());
		String table = trigger.getTarget_class();

		if (null == table) {
			triggerTargetTableCombo.setText("");
		} else {
			if (!getTableList().contains(table)) {
				triggerTargetTableCombo.add(table);
			}
			triggerTargetTableCombo.setText(table);
		}

		String column = trigger.getTarget_att();
		if (null == column) {
			triggerTargetColumnCombo.setText("");
		} else {
			triggerTargetColumnCombo.add(column);
			triggerTargetColumnCombo.setText(column);
		}

		String condition = trigger.getCondition();
		if (null == condition) {
			triggerConditionText.setText("");
		} else {
			triggerConditionText.setText(condition);
		}

		String action = trigger.getAction();

		if (null == action) {
			triggerActionText.setText("");
		} else {
			triggerActionText.setText(action);
		}

		triggerNameText.setEnabled(false);
		triggerTargetTableCombo.setEnabled(false);
		triggerTargetColumnCombo.setEnabled(false);
		triggerConditionText.setEnabled(false);
		triggerActionText.setEnabled(false);
		String conditionTime = trigger.getConditionTime();
		setEventTime(conditionTime);
		setActionTime(trigger.getActionTime());
		setActionType(trigger.getActionType());

		setEventType(trigger.getEventType());
		setStatus(trigger.getStatus());
		for (Button b : eventTimeBTNs) {
			b.setEnabled(false);
		}
		for (Button b : eventTypeBTNs) {
			b.setEnabled(false);
		}
		for (Button b : actionTimeBTNs) {
			b.setEnabled(false);
		}
		for (Button b : actionTypeBTNs) {
			b.setEnabled(false);
		}
		triggerPriorityText.setText(trigger.getPriority());
		if (isCommentSupport && trigger.getDescription() != null) {
			triggerDescriptionText.setText(trigger.getDescription());
		}
	}

	/**
	 *
	 * Add listener
	 *
	 */
	private void addListener() {
		SelectionAdapter eventTypeSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String curtype = getEventType();
				if (curtype.equals(Messages.eventTypeCommit)
						|| curtype.equals(Messages.eventTypeRollback)) {
					triggerTargetTableCombo.setText("");
					triggerTargetColumnCombo.setText("");
					triggerConditionText.setText("");
					triggerTargetTableCombo.setEnabled(false);
					triggerTargetColumnCombo.setEnabled(false);
					triggerConditionText.setEnabled(false);
				} else {
					triggerTargetTableCombo.setEnabled(true);
					triggerTargetColumnCombo.setEnabled(true);
					triggerConditionText.setEnabled(true);
				}
				if (curtype.equals(Messages.eventTypeInsert)
						|| curtype.equals(Messages.eventTypeSInsert)
						|| curtype.equals(Messages.eventTypeDelete)
						|| curtype.equals(Messages.eventTypeSDelete)) {

					triggerTargetTableCombo.setEnabled(true);
					triggerConditionText.setEnabled(true);

					triggerTargetColumnCombo.setText("");
					triggerTargetColumnCombo.setEnabled(false);
				} else if (curtype.equals(Messages.eventTypeUpdate)
						|| curtype.equals(Messages.eventTypeSUpdate)) {
					triggerTargetTableCombo.setEnabled(true);
					triggerConditionText.setEnabled(true);
					triggerTargetColumnCombo.setEnabled(true);
				}
				validateAll();
			}
		};
		for (Button b : eventTypeBTNs) {
			b.addSelectionListener(eventTypeSelectionAdapter);
		}
		SelectionAdapter actionTypeSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String curtype = getActionType();
				if (curtype.equals(Messages.actionTypeReject)
						|| curtype.equals(Messages.actionTypeInvalidateTransaction)) {
					triggerActionText.setEnabled(false);
					triggerActionText.setText("");
				} else {
					triggerActionText.setEnabled(true);
					triggerActionText.setFocus();
				}
				validateAll();
			}
		};
		for (Button b : actionTypeBTNs) {
			b.addSelectionListener(actionTypeSelectionAdapter);
		}
		SelectionAdapter statusSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				validateAll();
			}
		};
		for (Button b : statusBTNs) {
			b.addSelectionListener(statusSelectionAdapter);
		}
		triggerTargetTableCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int index = triggerTargetTableCombo.getSelectionIndex();
				String table = triggerTargetTableCombo.getItem(index);
				addColumns(table);
				validateAll();
			}
		});
		if (trigger == null) {
			triggerTargetTableCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					boolean valid = validateEventType();
					if (valid) {
						validateAll();
					} else {
						changeOKButtonStatus(false);
					}
					triggerTargetTableCombo.setFocus();
				}
			});
		}

		if (trigger == null) {
			triggerNameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					boolean valid = validateTriggerName();
					if (valid) {
						validateAll();
					} else {
						changeOKButtonStatus(false);
					}
					triggerNameText.setFocus();
				}
			});
		}
		triggerPriorityText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				boolean valid = validatePriority();
				if (valid) {
					validateAll();
				} else {
					changeOKButtonStatus(false);
				}
				triggerPriorityText.setFocus();
			}
		});
		if (isCommentSupport) {
			triggerDescriptionText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					validateAll();
					triggerDescriptionText.setFocus();
				}
			});
		}
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				if (tabFolder.getSelectionIndex() == 0) {
					triggerNameText.setFocus();
				} else if (tabFolder.getSelectionIndex() == tabFolder.getItemCount() - 1) {
					sqlText.setText(generateSqlText().toString());
				}
			}
		});
	}

	/**
	 *
	 * Get new trigger
	 *
	 * @return the Trigger object
	 */
	private Trigger getNewTrigger() { // FIXME move this logic to core module
		Trigger newTrigger = new Trigger();
		String triggerName = triggerNameText.getText();

		String triggerEventTargetTable = triggerTargetTableCombo.getText().trim();
		String triggerEventTargetColumn = triggerTargetColumnCombo.getText().trim();
		String eventTime = getEventTime();
		String eventType = getEventType();
		String triggerCondition = triggerConditionText.getText().trim();
		String crChar = "\r";
		String nlChar = "\n";
		triggerCondition = triggerCondition.replaceAll(crChar, "");
		triggerCondition = triggerCondition.replaceAll(nlChar, " ");

		String triggerActionType = getActionType();
		String triggerActionContent = triggerActionText.getText().trim();
		triggerActionContent = triggerActionContent.replaceAll(crChar, "");
		triggerActionContent = triggerActionContent.replaceAll(nlChar, " ");
		String actionTime = getActionTime();

		String triggerStatus = getStatus();
		String strPriority = triggerPriorityText.getText();

		newTrigger.setName(triggerName);
		newTrigger.setEventType(eventType);
		newTrigger.setTarget_class(triggerEventTargetTable);
		newTrigger.setTarget_att(triggerEventTargetColumn);
		newTrigger.setConditionTime(eventTime);

		newTrigger.setAction(triggerActionContent);
		newTrigger.setActionType(triggerActionType);
		newTrigger.setActionTime(actionTime);
		newTrigger.setCondition(triggerCondition);

		newTrigger.setStatus(triggerStatus);
		newTrigger.setPriority(strPriority);

		if (isCommentSupport) {
			String description = triggerDescriptionText.getText();
			newTrigger.setDescription(description);
		}

		return newTrigger;
	}

	/**
	 *
	 * Get event time
	 *
	 * @return the string
	 */
	private String getEventTime() {
		for (int i = 0; i < eventTimeBTNs.length; i++) {
			if (eventTimeBTNs[i].getSelection()) {
				for (String[] map : eventTimeMap) {
					if (map[0].equals(eventTimeBTNs[i].getText())) {
						return map[1];
					}
				}
			}
		}
		return null;
	}

	/**
	 *
	 * Set event time
	 *
	 * @param eventTime the event time
	 */
	private void setEventTime(String eventTime) {
		String[][] maps = eventTimeMap;
		Button[] buttons = eventTimeBTNs;
		if (eventTime == null) {
			for (Button b : buttons) {
				b.setSelection(false);
			}
			return;
		}
		for (String[] map : maps) {
			if (map[1].equals(eventTime)) {
				for (Button b : buttons) {
					if (b.getText().equals(map[0])) {
						b.setSelection(true);
					} else {
						b.setSelection(false);
					}
				}
			}
		}
	}

	/**
	 *
	 * Get event type
	 *
	 * @return the event type
	 */
	private String getEventType() {
		for (int i = 0; i < eventTypeBTNs.length; i++) {
			if (eventTypeBTNs[i].getSelection()) {
				for (String[] map : eventTypeMap) {
					if (map[0].equals(eventTypeBTNs[i].getText())) {
						return map[1];
					}
				}
			}
		}
		return null;
	}

	/**
	 *
	 * Set event type
	 *
	 * @param innerEventType the event type
	 */
	private void setEventType(String innerEventType) {
		String[][] maps = eventTypeMap;
		Button[] buttons = eventTypeBTNs;
		for (String[] map : maps) {
			if (map[1].equals(innerEventType)) {
				for (Button b : buttons) {
					if (b.getText().equals(map[0])) {
						b.setSelection(true);
					} else {
						b.setSelection(false);
					}
				}
			}
		}
	}

	/**
	 *
	 * Get action time
	 *
	 * @return the action time
	 */
	private String getActionTime() {
		for (int i = 0; i < actionTimeBTNs.length; i++) {
			if (actionTimeBTNs[i].getSelection()) {
				for (String[] map : actionTimeMap) {
					if (map[0].equals(actionTimeBTNs[i].getText())) {
						return map[1];
					}
				}
			}
		}
		return null;
	}

	/**
	 *
	 * Set action time
	 *
	 * @param innerActionTime the action time
	 */
	private void setActionTime(String innerActionTime) {
		String[][] maps = actionTimeMap;
		Button[] buttons = actionTimeBTNs;
		if (("BEFORE").equals(innerActionTime)) {
			actionTimeBTNs[0].setSelection(true);
			eventTimeBTNs[0].setSelection(true);
			return;
		}
		for (String[] map : maps) {
			if (map[1].equals(innerActionTime)) {
				for (Button b : buttons) {
					if (b.getText().equals(map[0])) {
						b.setSelection(true);
					} else {
						b.setSelection(false);
					}
				}
			}
		}
	}

	/**
	 *
	 * Get action type
	 *
	 * @return the action type
	 */
	private String getActionType() {
		for (int i = 0; i < actionTypeBTNs.length; i++) {
			if (actionTypeBTNs[i].getSelection()) {
				for (String[] map : actionTypeMap) {
					if (map[0].equals(actionTypeBTNs[i].getText())) {
						return map[1];
					}
				}
			}
		}
		return null;
	}

	/**
	 *
	 * Set action type
	 *
	 * @param innerActionType the action type
	 */
	private void setActionType(String innerActionType) {
		String[][] maps = actionTypeMap;
		Button[] buttons = actionTypeBTNs;
		for (String[] map : maps) {
			if (map[1].equals(innerActionType)) {
				for (Button b : buttons) {
					if (b.getText().equals(map[0])) {
						b.setSelection(true);
					} else {
						b.setSelection(false);
					}
				}
			}
		}
	}

	String[][] statusMap = {{Messages.triggerStatusActive, "ACTIVE" },
			{Messages.triggerStatusInactive, "INACTIVE" }, };
	private List<String> tableList;
	private String triggerName;

	/**
	 *
	 * Get status
	 *
	 * @return the status
	 */
	private String getStatus() {
		for (int i = 0; i < statusBTNs.length; i++) {
			if (statusBTNs[i].getSelection()) {
				for (String[] map : statusMap) {
					if (map[0].equals(statusBTNs[i].getText())) {
						return map[1];
					}
				}
			}
		}
		return null;
	}

	/**
	 *
	 * Set status
	 *
	 * @param innerStatus the status
	 */
	private void setStatus(String innerStatus) {
		String[][] maps = statusMap;
		Button[] buttons = statusBTNs;
		for (String[] map : maps) {
			if (map[1].equals(innerStatus)) {
				for (Button b : buttons) {
					if (b.getText().equals(map[0])) {
						b.setSelection(true);
					} else {
						b.setSelection(false);
					}
				}
			}
		}
	}

	/**
	 *
	 * Create trigger event group
	 *
	 * @param parent Composite
	 *
	 */
	private void createTriggerEventGroup(Composite parent) {

		//Event group
		Group eventGroup = new Group(parent, SWT.NONE);
		eventGroup.setText(Messages.triggerEvent);
		eventGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				2, 2));
		eventGroup.setLayout(new GridLayout(3, true));

		//event target group
		Group eventTargetGroup = new Group(eventGroup, SWT.NONE);
		eventTargetGroup.setText(Messages.triggerEventTarget);
		eventTargetGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1));
		eventTargetGroup.setLayout(new GridLayout(2, false));
		// target table
		Label lblTriggerEventTargetTable = new Label(eventTargetGroup, SWT.LEFT);
		lblTriggerEventTargetTable.setText(Messages.triggerEventTargetTable);
		triggerTargetTableCombo = new Combo(eventTargetGroup, SWT.BORDER
				| SWT.READ_ONLY);
		triggerTargetTableCombo.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, true));

		// target column
		Label targetColumnLabel = new Label(eventTargetGroup, SWT.LEFT);
		targetColumnLabel.setText(Messages.triggerEventTargetColumn);
		triggerTargetColumnCombo = new Combo(eventTargetGroup, SWT.BORDER
				| SWT.READ_ONLY);
		triggerTargetColumnCombo.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, true));

		// event time group
		Group conditionTimeGroup = new Group(eventGroup, SWT.NONE);
		conditionTimeGroup.setText(Messages.triggerEventTime);
		conditionTimeGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 3, 1));
		conditionTimeGroup.setLayout(new GridLayout(3, true));

		eventTimeBTNs = new Button[3];
		eventTimeBTNs[0] = new Button(conditionTimeGroup, SWT.RADIO);
		eventTimeBTNs[1] = new Button(conditionTimeGroup, SWT.RADIO);
		eventTimeBTNs[1].setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true,
				false));
		eventTimeBTNs[2] = new Button(conditionTimeGroup, SWT.RADIO);
		eventTimeBTNs[0].setText(Messages.eventTimeBefore);
		eventTimeBTNs[1].setText(Messages.eventTimeAfter);
		eventTimeBTNs[2].setText(Messages.eventTimeDeferred);

		// Event type group
		Group eventTypeGroup = new Group(eventGroup, SWT.NONE);
		eventTypeGroup.setText(Messages.triggerEventType);
		eventTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 3, 4));
		eventTypeGroup.setLayout(new GridLayout(2, true));

		eventTypeBTNs = new Button[8];
		eventTypeBTNs[0] = new Button(eventTypeGroup, SWT.RADIO);
		eventTypeBTNs[1] = new Button(eventTypeGroup, SWT.RADIO);
		eventTypeBTNs[1].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		eventTypeBTNs[2] = new Button(eventTypeGroup, SWT.RADIO);
		eventTypeBTNs[3] = new Button(eventTypeGroup, SWT.RADIO);
		eventTypeBTNs[3].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		final GridData gdEventTypeBtn = new GridData(SWT.FILL, SWT.CENTER,
				false, false);
		gdEventTypeBtn.widthHint = 159;
		eventTypeBTNs[3].setLayoutData(gdEventTypeBtn);

		eventTypeBTNs[4] = new Button(eventTypeGroup, SWT.RADIO);
		eventTypeBTNs[5] = new Button(eventTypeGroup, SWT.RADIO);
		eventTypeBTNs[5].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		eventTypeBTNs[6] = new Button(eventTypeGroup, SWT.RADIO);
		eventTypeBTNs[7] = new Button(eventTypeGroup, SWT.RADIO);
		eventTypeBTNs[7].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));

		eventTypeBTNs[0].setText(Messages.eventTypeInsert);
		eventTypeBTNs[1].setText(Messages.eventTypeSInsert);
		eventTypeBTNs[2].setText(Messages.eventTypeUpdate);
		eventTypeBTNs[3].setText(Messages.eventTypeSUpdate);
		eventTypeBTNs[4].setText(Messages.eventTypeDelete);
		eventTypeBTNs[5].setText(Messages.eventTypeSDelete);
		eventTypeBTNs[6].setText(Messages.eventTypeCommit);
		eventTypeBTNs[7].setText(Messages.eventTypeRollback);

		// Event condition group
		Group eventContitionGroup = new Group(eventGroup, SWT.NONE);
		eventContitionGroup.setText(Messages.triggerCondition);
		eventContitionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, 3, 1));
		eventContitionGroup.setLayout(new GridLayout(1, true));

		triggerConditionText = new Text(eventContitionGroup, SWT.BORDER
				| SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gridData.heightHint = 40;
		triggerConditionText.setLayoutData(gridData);
	}

	/**
	 *
	 * Create the option group
	 *
	 * @param parent Composite
	 *
	 */
	private void createOptionGroup(Composite parent) {

		Group optionGroup = new Group(parent, SWT.NONE);
		{
			optionGroup.setText(Messages.triggerOptionalGroupName);
			GridLayout gdLayoutOptionGrpName = new GridLayout();
			gdLayoutOptionGrpName.numColumns = 2;
			optionGroup.setLayout(gdLayoutOptionGrpName);
			optionGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 2, 2));
		}

		if (isCommentSupport) {
			Label triggerDescriptionLabel = new Label(optionGroup, SWT.LEFT | SWT.WRAP);
			triggerDescriptionLabel.setText(Messages.triggerDesscription);
			triggerDescriptionText = new Text(optionGroup, SWT.BORDER);
			triggerDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
			GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			triggerDescriptionText.setLayoutData(gridData);
		}

		final Group statusGroup = new Group(optionGroup, SWT.NONE);
		{
			statusGroup.setText(Messages.triggerStatusGroupText);
			statusGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true));
			statusGroup.setLayout(new GridLayout());
		}

		statusBTNs = new Button[2];
		statusBTNs[0] = new Button(statusGroup, SWT.RADIO);
		statusBTNs[0].setLayoutData(new GridData());
		statusBTNs[0].setText(Messages.triggerStatusActive);
		statusBTNs[1] = new Button(statusGroup, SWT.RADIO);
		statusBTNs[1].setLayoutData(new GridData());
		statusBTNs[1].setText(Messages.triggerStatusInactive);

		Group triggerPriorityGroup = new Group(optionGroup, SWT.NONE);
		{
			triggerPriorityGroup.setText(Messages.triggerPriorityGroupText);
			GridData gdTriggerPriorityGroup = new GridData(SWT.FILL, SWT.FILL,
					true, true);
			triggerPriorityGroup.setLayoutData(gdTriggerPriorityGroup);
			final GridLayout gdLayoutPriorityGrp = new GridLayout();
			gdLayoutPriorityGrp.numColumns = 2;
			triggerPriorityGroup.setLayout(gdLayoutPriorityGrp);
		}

		Label priorityGrpLabel = new Label(triggerPriorityGroup, SWT.LEFT
				| SWT.WRAP);
		priorityGrpLabel.setLayoutData(new GridData(61, SWT.DEFAULT));
		priorityGrpLabel.setText(Messages.triggerPriorityText);

		triggerPriorityText = new Text(triggerPriorityGroup, SWT.BORDER);
		final GridData gdPriority = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		triggerPriorityText.setLayoutData(gdPriority);
		triggerPriorityText.setText("00.00");
	}

	/**
	 *
	 * Create the tigger action group
	 *
	 * @param parent Composite
	 */
	private void createTriggerActionGroup(Composite parent) {
		// trigger action group
		Group actionGroup = new Group(parent, SWT.NONE);
		actionGroup.setText(Messages.triggerActionGroupText);
		actionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				2, 1));
		actionGroup.setLayout(new GridLayout(3, false));

		// Execution time group
		Group actionTimeGroup = new Group(actionGroup, SWT.NONE);
		actionTimeGroup.setText(Messages.triggerExecutionTime);
		actionTimeGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1));
		actionTimeGroup.setLayout(new GridLayout(3, true));

		actionTimeBTNs = new Button[3];
		actionTimeBTNs[0] = new Button(actionTimeGroup, SWT.RADIO);
		actionTimeBTNs[1] = new Button(actionTimeGroup, SWT.RADIO);
		actionTimeBTNs[2] = new Button(actionTimeGroup, SWT.RADIO);
		actionTimeBTNs[1].setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true,
				false));
		actionTimeBTNs[0].setText(Messages.actionTimeDefault);
		actionTimeBTNs[1].setText(Messages.actionTimeAfter);
		actionTimeBTNs[2].setText(Messages.actionTimeDeferred);

		// trigger contents group
		Group actionTypeGroup = new Group(actionGroup, SWT.NONE);
		actionTypeGroup.setText(Messages.triggerActionType);
		actionTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 3, 1));
		actionTypeGroup.setLayout(new GridLayout(2, true));

		actionTypeBTNs = new Button[4];
		actionTypeBTNs[0] = new Button(actionTypeGroup, SWT.RADIO);
		actionTypeBTNs[0].setText(Messages.actionTypeReject);
		actionTypeBTNs[1] = new Button(actionTypeGroup, SWT.RADIO);
		actionTypeBTNs[1].setText(Messages.actionTypeInvalidateTransaction);
		actionTypeBTNs[2] = new Button(actionTypeGroup, SWT.RADIO);
		actionTypeBTNs[2].setText(Messages.actionTypePrint);
		actionTypeBTNs[3] = new Button(actionTypeGroup, SWT.RADIO);
		actionTypeBTNs[3].setText(Messages.actionTypeOtherSQL);

		//create trigger action content
		final Label sqlStatementsOrLabel = new Label(actionGroup, SWT.NONE);
		sqlStatementsOrLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		sqlStatementsOrLabel.setText(Messages.sqlStatementMSG);

		triggerActionText = new Text(actionGroup, SWT.BORDER | SWT.MULTI
				| SWT.WRAP | SWT.V_SCROLL);
		final GridData gdEditTriggerText = new GridData(SWT.FILL, SWT.FILL,
				true, true, 3, 1);
		gdEditTriggerText.heightHint = 157;
		triggerActionText.setLayoutData(gdEditTriggerText);
	}

	/**
	 *
	 * Get table list
	 *
	 * @return the table string list
	 */
	private List<String> getTableList() { // FIXME move this logic to core module
		if (null == tableList) {
			CubridDatabase db = database;
			DatabaseInfo dbInfo = db.getDatabaseInfo();
			GetTablesTask task = new GetTablesTask(dbInfo);
			//9.1.0 can't create trigger on sub-partion table
			if (CompatibleUtil.isNotSupportGetSubPartitionTable (
					database.getServer().getServerInfo())) {
				tableList = task.getUserTablesNotContainSubPartitionTable();
			} else {
				tableList = task.getUserTables();
			}

			if (task.getErrorMsg() != null) {
				CommonUITool.openErrorBox(task.getErrorMsg());
				return tableList;
			}
		}
		return tableList;
	}

	/**
	 *
	 * Add tables to combo
	 *
	 */
	private void addTables() {
		triggerTargetTableCombo.removeAll();
		triggerTargetTableCombo.add("");
		for (String table : getTableList()) {
			triggerTargetTableCombo.add(table);
		}
	}

	/**
	 *
	 * Add columns of this table to combo
	 *
	 * @param tableName the tablename
	 */
	private void addColumns(String tableName) {
		triggerTargetColumnCombo.removeAll();
		if (tableName == null || tableName.trim().length() == 0) {
			return;
		}
		CubridDatabase db = database;
		DatabaseInfo dbInfo = db.getDatabaseInfo();
		GetAllAttrTask task = new GetAllAttrTask(dbInfo);
		List<String> attrNameList = task.getAttrNameList(tableName);
		if (task.getErrorMsg() != null) {
			CommonUITool.openErrorBox(task.getErrorMsg());
			return;
		}
		triggerTargetColumnCombo.add("");
		for (String name : attrNameList) {
			triggerTargetColumnCombo.add(name);
		}
	}

	/**
	 * Validate trigger information
	 *
	 * @return <code>true</code> if it is valid;<code>false</code>otherwise
	 */
	private boolean validateAll() {
		setErrorMessage(null);
		changeOKButtonStatus(false);
		if (!validateTriggerName()) {
			return false;
		}
		if (!validateEventType()) {
			return false;
		}
		if (!validatePriority()) {
			return false;
		}
		if (null != trigger && !isChanged(trigger, getNewTrigger())) {
			return false;
		}
		changeOKButtonStatus(true);
		return true;
	}

	/**
	 * Change ok button status: enabled, or disabled
	 *
	 * @param valid whether it is enabled
	 *
	 */
	private void changeOKButtonStatus(boolean valid) {
		if (null == trigger) {
			getButton(IDialogConstants.OK_ID).setEnabled(valid);
		} else {
			getButton(ALTER_TRIGGER_OK_ID).setEnabled(valid);
		}
	}

	/**
	 * Validate event type
	 *
	 * @return <code>true</code> if it is valid;<code>false</code>otherwise
	 */
	private boolean validateEventType() {
		String eventType = getEventType();
		String triggerEventTargetTable = triggerTargetTableCombo.getText().trim();
		if (eventType != null && !eventType.equals(Messages.eventTypeCommit)
				&& !eventType.equals(Messages.eventTypeRollback)
				&& triggerEventTargetTable.length() <= 0) {
			setErrorMessage(Messages.enterEventTargetMSG);
			return false;
		}
		return true;
	}

	/**
	 * Validate trigger name
	 *
	 * @return <code>true</code> if it is valid;<code>false</code>otherwise
	 */
	private boolean validateTriggerName() {
		String triggerName = triggerNameText.getText();
		if (!ValidateUtil.isValidIdentifier(triggerName)) {
			setErrorMessage(Messages.invalidTriggerNameError);
			triggerNameText.setFocus();
			return false;
		}
		return true;
	}

	/**
	 * Validate priority
	 *
	 * @return <code>true</code> if it is valid;<code>false</code>otherwise
	 */
	private boolean validatePriority() {
		String strPriority = triggerPriorityText.getText();
		try {
			double priority = Double.parseDouble(strPriority);
			if (priority < 0 || priority > 9999.9901) {
				setErrorMessage(Messages.errRangePriority);
				return false;
			}
			String format = Trigger.formatPriority(strPriority);
			if (priority != Double.parseDouble(format)) {
				setErrorMessage(Messages.errPriorityFormat);
				return false;
			}
		} catch (NumberFormatException numberFormatException) {
			setErrorMessage(Messages.errFormatPriority);
			return false;
		}
		return true;
	}

	/**
	 * Return true if trigger is changed.
	 *
	 * @param oldTrigger the old trigger
	 * @param newTrigger the new trigger
	 * @return <code>true</code> if it is changed;<code>false</code>otherwise
	 */
	public boolean isChanged(Trigger oldTrigger, Trigger newTrigger) {
		if (null != oldTrigger && null != newTrigger) {
			String oldPriority = oldTrigger.getPriority();
			String oldStatus = oldTrigger.getStatus();
			String newPriority = newTrigger.getPriority();
			String newStatus = newTrigger.getStatus();
			if (!oldStatus.equals(newStatus)) {
				return true;
			}
			if (!oldPriority.equals(newPriority)) {
				return true;
			}
			if (isCommentSupport) {
				String oldDescription = oldTrigger.getDescription();
				String newDescription = newTrigger.getDescription();
				if (!newDescription.equals(oldDescription)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 *
	 * @return Sql string buffer
	 */
	private StringBuffer generateSqlText() { // FIXME move this logic to core module
		StringBuffer sql = new StringBuffer();
		Trigger newTrigger = getNewTrigger();
		if (null == trigger) {
			sql.append(TriggerDDL.getDDL(newTrigger));
		} else {
			sql.append(TriggerDDL.getDDL(trigger));
			sql.append(StringUtil.NEWLINE);
			sql.append(StringUtil.NEWLINE);
			sql.append(StringUtil.NEWLINE);
			sql.append(TriggerDDL.getAlterDDL(trigger, newTrigger));
		}
		return sql;
	}

	public String getTriggerName() {
		return triggerName;
	}

}
