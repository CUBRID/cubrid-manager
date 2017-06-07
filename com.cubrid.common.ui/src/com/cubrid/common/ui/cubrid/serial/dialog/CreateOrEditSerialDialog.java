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
package com.cubrid.common.ui.cubrid.serial.dialog;

import java.math.BigInteger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.serial.Messages;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.loader.CubridSerialFolderLoader;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.serial.task.CreateOrEditSerialTask;

/**
 * The dialog is responsible to collect serial information.
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-3 created by pangqiren
 */
public class CreateOrEditSerialDialog extends CMTitleAreaDialog implements ModifyListener {
	private Text serialNameText = null;
	private Text serialDescriptionText = null;
	private Text startValText = null;
	private Text incrementValText = null;
	private Text maxValText = null;
	private Text minValText = null;
	private Button cycleButton = null;
	private ISchemaNode editedNode = null;
	private TabFolder tabFolder = null;
	private StyledText sqlScriptText = null;
	private static SqlFormattingStrategy formator = new SqlFormattingStrategy();
	private Button noMinValueBtn;
	private Button noMaxValueBtn;
	private Text cacheCountText;
	private Button noCacheBtn;
	private boolean isSupportCache = false;
	private String taskName;
	private CubridDatabase database;
	private boolean isEditAble;
	private String serialName;
	private static final String SERIAL_MIN = "-1000000000000000000000000000000000000";
	private static final String SERIAL_MAX = "10000000000000000000000000000000000000";
	private boolean isCommentSupport = false;

	public CreateOrEditSerialDialog(Shell parentShell, boolean isEditAble) {
		super(parentShell);
		this.isEditAble = isEditAble;
	}

	protected Control createDialogArea(Composite parent) {
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
		item.setText(Messages.grpGeneral);
		composite = createGeneralInfoComp();
		item.setControl(composite);

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText(Messages.grpSqlScript);
		composite = createSqlScriptComposite();
		item.setControl(composite);

		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (tabFolder.getSelectionIndex() == 0) {
					serialNameText.setFocus();
				} else if (tabFolder.getSelectionIndex() == tabFolder.getItemCount() - 1) {
					sqlScriptText.setText(getSQLScript());
				}
			}
		});

		initialize();

		if (editedNode == null) {
			setTitle(Messages.titleCreateSerialDialog);
			setMessage(Messages.msgCreateSerialDialog);
		} else {
			setTitle(Messages.titleEditSerialDialog);
			setMessage(Messages.msgEditSerialDialog);
		}

		return parentComp;
	}

	/**
	 * Create General information composite
	 *
	 * @return the composite
	 */
	private Composite createGeneralInfoComp() {
		isCommentSupport = CompatibleUtil.isCommentSupports(database.getDatabaseInfo());

		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		Label serialNameLabel = new Label(composite, SWT.LEFT);
		serialNameLabel.setText(Messages.lblSerialName);
		serialNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		serialNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
		serialNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

		if (isCommentSupport) {
			Label serialDescriptionLabel = new Label(composite, SWT.LEFT);
			serialDescriptionLabel.setText(Messages.lblSerialDescription);
			serialDescriptionLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
			serialDescriptionText = new Text(composite, SWT.LEFT | SWT.BORDER);
			serialDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
			serialDescriptionText.setLayoutData(CommonUITool.createGridData(
					GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		}

		Label startValLabel = new Label(composite, SWT.LEFT);
		if (editedNode == null) {
			startValLabel.setText(Messages.lblStartValue);
		} else {
			startValLabel.setText(Messages.lblCurrentValue);
		}
		startValLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		startValText = new Text(composite, SWT.LEFT | SWT.BORDER);
		startValText.setTextLimit(38);

		startValText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

		Label incrementValLabel = new Label(composite, SWT.LEFT);
		incrementValLabel.setText(Messages.lblIncrementValue);
		incrementValLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		incrementValText = new Text(composite, SWT.LEFT | SWT.BORDER);
		incrementValText.setTextLimit(38);
		incrementValText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

		Label minValLabel = new Label(composite, SWT.LEFT);
		minValLabel.setText(Messages.lblMinValue);
		minValLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		minValText = new Text(composite, SWT.LEFT | SWT.BORDER);
		minValText.setTextLimit(38);
		minValText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		noMinValueBtn = new Button(composite, SWT.CHECK);
		noMinValueBtn.setText(Messages.btnNoMinValue);
		noMinValueBtn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		noMinValueBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (noMinValueBtn.getSelection()) {
					minValText.setText("");
					minValText.setEnabled(false);
				} else {
					minValText.setEnabled(true);
					minValText.setFocus();
				}
				valid();
			}
		});

		Label maxValLabel = new Label(composite, SWT.LEFT);
		maxValLabel.setText(Messages.lblMaxValue);
		maxValLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		maxValText = new Text(composite, SWT.LEFT | SWT.BORDER);
		maxValText.setTextLimit(38);
		maxValText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		noMaxValueBtn = new Button(composite, SWT.CHECK);
		noMaxValueBtn.setText(Messages.btnNoMaxValue);
		noMaxValueBtn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		noMaxValueBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (noMaxValueBtn.getSelection()) {
					maxValText.setText("");
					maxValText.setEnabled(false);
				} else {
					maxValText.setEnabled(true);
					maxValText.setFocus();
				}
				valid();
			}
		});

		checkSupportCache();
		if (isSupportCache) {
			Label cacheLabel = new Label(composite, SWT.LEFT);
			cacheLabel.setText(Messages.lblCacheCount);
			cacheLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

			cacheCountText = new Text(composite, SWT.LEFT | SWT.BORDER);
			cacheCountText.setTextLimit(38);
			cacheCountText.setLayoutData(CommonUITool.createGridData(
					GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

			noCacheBtn = new Button(composite, SWT.CHECK);
			noCacheBtn.setText(Messages.btnNoCache);
			noCacheBtn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
			noCacheBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (noCacheBtn.getSelection()) {
						cacheCountText.setText("");
						cacheCountText.setEnabled(false);
					} else {
						cacheCountText.setEnabled(true);
						cacheCountText.setFocus();
					}
					valid();
				}
			});
		}
		cycleButton = new Button(composite, SWT.LEFT | SWT.CHECK);
		cycleButton.setText(Messages.btnCycle);
		cycleButton.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
		cycleButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				valid();
			}
		});

		return composite;
	}

	/**
	 * Check whether support cache in current version
	 */
	private void checkSupportCache() {
		if (database.getServer().getServerInfo() != null) {
			isSupportCache = CompatibleUtil.isSupportCache(database.getDatabaseInfo());
		}
	}

	/**
	 * Create SQL script composite
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
	 * Get SQL script about serial
	 *
	 * @return the SQL script string
	 */
	private String getSQLScript() { // FIXME move this logic to core module
		StringBuffer sb = new StringBuffer();
		if (editedNode == null) {
			sb.append("CREATE").append(" SERIAL ");
			String serialName = serialNameText.getText();
			if (serialName.trim().length() == 0) {
				sb.append("<serial_name>");
			} else {
				sb.append(QuerySyntax.escapeKeyword(serialName));
			}
		} else {
			sb.append("ALTER").append(" SERIAL ");
			String serialName = serialNameText.getText();
			if (serialName.trim().length() == 0) {
				sb.append("<serial_name>");
			} else {
				sb.append(QuerySyntax.escapeKeyword(serialName));
			}
		}
		String startedValue = startValText.getText();
		String incrementValue = incrementValText.getText();
		String minxValue = minValText.getText();
		String maxValue = maxValText.getText();
		boolean isCycle = cycleButton.getSelection();
		if (startedValue.trim().length() > 0) {
			sb.append(" START WITH ").append(startedValue);
		}
		if (incrementValue.trim().length() > 0) {
			sb.append(" INCREMENT BY ").append(incrementValue);
		}

		if (noMinValueBtn.getSelection()) {
			sb.append(" NOMINVALUE");
		} else if (minxValue.trim().length() > 0) {
			sb.append(" MINVALUE ").append(minxValue);
		}

		if (noMaxValueBtn.getSelection()) {
			sb.append(" NOMAXVALUE");
		} else if (maxValue.trim().length() > 0) {
			sb.append(" MAXVALUE ").append(maxValue);
		}
		if (isCycle) {
			sb.append(" CYCLE");
		} else {
			sb.append(" NOCYCLE");
		}
		if (isSupportCache) {
			if (noCacheBtn.getSelection()) {
				sb.append(" NOCACHE");
			} else if (cacheCountText.getText().trim().length() > 0) {
				sb.append(" CACHE ").append(cacheCountText.getText().trim());
			}
		}
		if (isCommentSupport) {
			String description = serialDescriptionText.getText();
			if (StringUtil.isNotEmpty(description)) {
				description = String.format("'%s'", description);
				sb.append(String.format(" COMMENT %s", StringUtil.escapeQuotes(description)));
			}
		}

		return formatSql(sb.toString());
	}

	/**
	 * Format the SQL script
	 *
	 * @param sql the SQL
	 * @return the formatted SQL
	 */
	private String formatSql(String sql) {
		String sqlStr = formator.format(sql + ";");
		sqlStr = sqlStr.trim().endsWith(";") ? sqlStr.trim().substring(0,
				sqlStr.trim().length() - 1) : "";
		return sqlStr;
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setSize(500, 600);
		CommonUITool.centerShell(getShell());
		if (editedNode == null) {
			getShell().setText(Messages.titleCreateSerialDialog);
		} else {
			getShell().setText(Messages.titleEditSerialDialog);
		}
	}

	/**
	 * Create buttons for button bar
	 *
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, com.cubrid.common.ui.common.Messages.btnOK, true);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, com.cubrid.common.ui.common.Messages.btnCancel, true);
	}

	/**
	 * Call it when press button
	 *
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			createSerial(buttonId);
		} else {
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * Initial data
	 */
	private void initialize() {
		if (editedNode == null) {
			startValText.setText("0");
			incrementValText.setText("1");
			minValText.setText("");
			minValText.setEnabled(false);
			noMinValueBtn.setSelection(true);
			maxValText.setText("");
			maxValText.setEnabled(false);
			noMaxValueBtn.setSelection(true);
			cycleButton.setSelection(false);
			if (isSupportCache) {
				cacheCountText.setEnabled(false);
				noCacheBtn.setSelection(true);
			}
			serialNameText.setFocus();
		} else {
			SerialInfo serialInfo = (SerialInfo) editedNode.getAdapter(SerialInfo.class);
			if (serialInfo != null) {
				serialNameText.setEditable(false);
				serialNameText.setText(serialInfo.getName());
				String description = serialInfo.getDescription();
				if (isCommentSupport && StringUtil.isNotEmpty(description)) {
					serialDescriptionText.setText(description);
				}
				startValText.setText(String.valueOf(serialInfo.getCurrentValue()));
				String incrValue = serialInfo.getIncrementValue();
				incrementValText.setText(incrValue);
				String minValue = serialInfo.getMinValue();
				if (incrValue.indexOf("-") >= 0 && SERIAL_MIN.equals(minValue)) {
					noMinValueBtn.setSelection(true);
					minValText.setEnabled(false);
				} else {
					minValText.setText(minValue);
				}
				String maxValue = serialInfo.getMaxValue();
				if (incrValue.indexOf("-") < 0 && SERIAL_MAX.equals(maxValue)) {
					noMaxValueBtn.setSelection(true);
					maxValText.setEnabled(false);
				} else {
					maxValText.setText(maxValue);
				}
				if (isSupportCache) {
					String cacheCount = serialInfo.getCacheCount();
					if (cacheCount == null
							|| (cacheCount.trim().matches("\\d+") && Integer.parseInt(cacheCount.trim()) <= 0)) {
						noCacheBtn.setSelection(true);
						cacheCountText.setEnabled(false);
					} else {
						cacheCountText.setText(cacheCount);
					}
				}

				cycleButton.setSelection(serialInfo.isCyclic());
			}
		}
		serialNameText.addModifyListener(this);
		startValText.addModifyListener(this);
		incrementValText.addModifyListener(this);
		minValText.addModifyListener(this);
		maxValText.addModifyListener(this);
		if (isSupportCache) {
			cacheCountText.addModifyListener(this);
		}
		if (isCommentSupport) {
			serialDescriptionText.addModifyListener(this);
		}

		if (!isEditAble) {
			serialNameText.setEditable(false);
			startValText.setEditable(false);
			incrementValText.setEditable(false);
			maxValText.setEditable(false);
			minValText.setEditable(false);
			cycleButton.setEnabled(false);
			noMinValueBtn.setEnabled(false);
			noMaxValueBtn.setEnabled(false);
			noCacheBtn.setEnabled(false);
		}
	}

	/**
	 * Execute task and create serial
	 *
	 * @param buttonId the button id
	 */
	private void createSerial(final int buttonId) { // FIXME move this logic to core module
		serialName = serialNameText.getText();
		final String startVal = startValText.getText();
		final String incrementVal = incrementValText.getText();
		final String minVal = minValText.getText();
		final String maxVal = maxValText.getText();
		final boolean isNoMinValue = noMinValueBtn.getSelection();
		final boolean isNoMaxValue = noMaxValueBtn.getSelection();
		final boolean isCycle = cycleButton.getSelection();
		final String cacheCount = isSupportCache ? cacheCountText.getText().trim() : null;
		final boolean isNoCache = isSupportCache ? noCacheBtn.getSelection() : false;
		final String description = isCommentSupport ? serialDescriptionText.getText() : null;
		if (editedNode == null) {
			taskName = Messages.bind(Messages.createSerialTaskName, serialName);
		} else {
			taskName = Messages.bind(Messages.editSerialTaskName, serialName);
		}

		TaskExecutor taskExcutor = new TaskExecutor() {
			public boolean exec(final IProgressMonitor monitor) {
				if (monitor.isCanceled()) {
					return false;
				}
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
				for (ITask task : taskList) {
					if (task instanceof CreateOrEditSerialTask) {
						CreateOrEditSerialTask createSerialTask = (CreateOrEditSerialTask) task;
						if (editedNode == null) {
							createSerialTask.createSerial(serialName, startVal,
									incrementVal, maxVal, minVal, isCycle, isNoMinValue,
									isNoMaxValue, cacheCount, isNoCache, description);
						} else {
							createSerialTask.editSerial(serialName, startVal,
									incrementVal, maxVal, minVal, isCycle, isNoMinValue,
									isNoMaxValue, cacheCount, isNoCache, description);
						}
					}
					final String msg = task.getErrorMsg();
					if (monitor.isCanceled()) {
						return false;
					}
					if (openErrorBox(getShell(), msg, monitor)) {
						return false;
					}
					if (monitor.isCanceled()) {
						return false;
					}
				}
				return true;
			}
		};

		DatabaseInfo databaseInfo = database.getDatabaseInfo();
		CreateOrEditSerialTask task = new CreateOrEditSerialTask(databaseInfo);
		taskExcutor.addTask(task);
		new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
		if (taskExcutor.isSuccess()) {
			setReturnCode(buttonId);
			close();
		}
	}

	/**
	 * Check the value validity
	 */
	private void valid() {
		String name = serialNameText.getText();
		String startVal = startValText.getText();
		String incrementVal = incrementValText.getText();
		String minVal = minValText.getText();
		String maxVal = maxValText.getText();
		boolean isNoMinValue = noMinValueBtn.getSelection();
		boolean isNoMaxValue = noMaxValueBtn.getSelection();

		if (!ValidateUtil.isValidIdentifier(name)) {
			setErrorMessage(Messages.errSerialName);
			setEnabled(false);
			return;
		}

		if (!StringUtil.isValidNameLength(name, ValidateUtil.MAX_SCHEMA_NAME_LENGTH)) {
			setErrorMessage(Messages.bind(Messages.errSerialNameLength, ValidateUtil.MAX_SCHEMA_NAME_LENGTH));
			setEnabled(false);
			return;
		}

		boolean isExist = false;
		if (editedNode == null) {
			ICubridNode folderNode = database.getChild(database.getId()
					+ ICubridNodeLoader.NODE_SEPARATOR + CubridSerialFolderLoader.SERIAL_FOLDER_ID);
			isExist = folderNode != null
					&& folderNode.getChild(folderNode.getId() + ICubridNodeLoader.NODE_SEPARATOR + name) != null;
		}
		if (isExist) {
			setErrorMessage(Messages.errSerialExist);
			setEnabled(false);
			return;
		}

		boolean isValidStartVal = verifyBigValue(startVal);
		if (!isValidStartVal) {
			if (editedNode == null) {
				setErrorMessage(Messages.bind(Messages.errStartValue, Messages.msgStartValue));
				setEnabled(false);
				return;
			} else {
				setErrorMessage(Messages.bind(Messages.errStartValue, Messages.msgCurrentValue));
				setEnabled(false);
				return;
			}
		}

		boolean isValidIncrementVal = verifyBigValue(incrementVal);
		if (!isValidIncrementVal) {
			setErrorMessage(Messages.errIncrementValue);
			setEnabled(false);
			return;
		}

		boolean isValidMinVal = true;
		if (!isNoMinValue) {
			isValidMinVal = verifyBigValue(minVal);
			if (!isValidMinVal) {
				setErrorMessage(Messages.errMinValue);
				setEnabled(false);
				return;
			}
		}

		boolean isValidMaxVal = true;
		if (!isNoMaxValue) {
			isValidMaxVal = verifyBigValue(maxVal);
			if (!isValidMaxVal) {
				setErrorMessage(Messages.errMaxValue);
				setEnabled(false);
				return;
			}
		}

		BigInteger startBigVal = new BigInteger(startVal);
		BigInteger minBigValue = null;
		if (!isNoMinValue) {
			minBigValue = new BigInteger(minVal);
			isValidStartVal = startBigVal.compareTo(minBigValue) >= 0;
		}

		BigInteger maxBigValue = null;
		if (!isNoMaxValue && isValidStartVal) {
			maxBigValue = new BigInteger(maxVal);
			isValidStartVal = maxBigValue.compareTo(startBigVal) >= 0;
		}

		if (!isValidStartVal) {
			if (editedNode == null) {
				setErrorMessage(Messages.bind(Messages.errValue, Messages.msgStartValue));
				setEnabled(false);
				return;
			} else {
				setErrorMessage(Messages.bind(Messages.errValue, Messages.msgCurrentValue));
				setEnabled(false);
				return;
			}
		}

		if (minBigValue != null && maxBigValue != null) {
			BigInteger incrBigVal = new BigInteger(incrementVal.replaceAll("-", ""));
			isValidIncrementVal = maxBigValue.subtract(minBigValue).compareTo(incrBigVal) >= 0;
			if (!isValidIncrementVal) {
				setErrorMessage(Messages.errDiffValue);
				setEnabled(false);
				return;
			}
		}

		if (isSupportCache && !noCacheBtn.getSelection()) {
			String cacheCount = cacheCountText.getText().trim();
			if (!cacheCount.matches("\\d+") || Integer.parseInt(cacheCount) <= 0) {
				setErrorMessage(Messages.errCacheCount);
				setEnabled(false);
				return;
			}
		}
		setErrorMessage(null);
		setEnabled(true);
	}

	/**
	 * Verify the big value
	 *
	 * @param bigValue the big value string
	 * @return <code>true</code> if it is valid;<code>false</code> otherwise
	 */
	private boolean verifyBigValue(String bigValue) {
		String bigValueTrim = bigValue.trim();
		if (bigValueTrim.length() == 0) {
			return false;
		}

		boolean notLimitValue = !bigValueTrim.equals(SERIAL_MAX) && !bigValueTrim.equals(SERIAL_MIN);
		boolean isValidBigVal = ValidateUtil.isInteger(bigValue);
		if (isValidBigVal && bigValueTrim.length() == 38 && notLimitValue) {
			isValidBigVal = false;
		}

		return isValidBigVal;
	}

	/**
	 * Enable or disable the OK button
	 *
	 * @param isEnabled whether it is enabled
	 */
	private void setEnabled(boolean isEnabled) {
		if (getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
		}
	}

	/**
	 * Listen to modify event
	 *
	 * @param event the modify event
	 */
	public void modifyText(ModifyEvent event) {
		valid();
	}

	public void setEditedNode(ISchemaNode editedNode) {
		this.editedNode = editedNode;
	}

	public String getSerialName() {
		return serialName;
	}

	public void setDatabase(CubridDatabase database) {
		this.database = database;
	}
}
