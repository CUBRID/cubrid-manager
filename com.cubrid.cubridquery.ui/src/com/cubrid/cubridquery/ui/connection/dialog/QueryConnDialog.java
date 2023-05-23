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
package com.cubrid.cubridquery.ui.connection.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.common.control.ConnectionComposite;
import com.cubrid.common.ui.common.control.SelectColorCombo;
import com.cubrid.common.ui.common.persist.ConnectionInfo;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ConnectDatabaseExecutor;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.IsDBAUserTask;
import com.cubrid.cubridmanager.ui.spi.model.loader.CQBDbConnectionLoader;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.CQBConnectionUtils;
import com.cubrid.cubridquery.ui.connection.Messages;

/**
 *
 * The dialog is used to add query connection database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class QueryConnDialog extends
		CMTitleAreaDialog implements
		ModifyListener {

	public final static int CONNECT_ID = 0;
	public final static int SAVE_ID = 2;
	public final static int TEST_CONNECT_ID = 3;

	private Text queryConnNameText;
	private Text commentText;
	private CubridDatabase database;
	private ConnectionComposite connectionComp;
	private boolean isBrokerManualChanged = false;
	private final boolean isNewQueryConn;

	private SelectColorCombo selectColorCombo;

	private boolean fireLogoutEvent = false;
	private String oldLoginUserName;
	private boolean oldDatabaseIsLogin = false;
	private ConnectionInfo oldInfo;
	private ConnectionInfo newInfo;

	/**
	 * The constructor
	 *
	 * @param parentShell
	 * @param database
	 * @param isNew
	 */
	public QueryConnDialog(Shell parentShell, CubridDatabase database,
			boolean isNew) {
		super(parentShell);
		this.database = database;
		this.isNewQueryConn = isNew;
		if (database != null) {
			oldLoginUserName = database.getUserName();
			oldDatabaseIsLogin = database.isLogined();
			oldInfo = CQBDBNodePersistManager.getInstance().getConnectionInfo(database);
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @param parent The parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);
		{
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout layout = new GridLayout();
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
		}

		Composite connectionNameComp = new Composite(composite, SWT.NONE);
		{
			connectionNameComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout layout = new GridLayout(4, false);
			connectionNameComp.setLayout(layout);

			Label queryConnNameLabel = new Label(connectionNameComp, SWT.LEFT);
			queryConnNameLabel.setText(Messages.lblConnName);
			queryConnNameLabel.setLayoutData(CommonUITool.createGridData(
					GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

			queryConnNameText = new Text(connectionNameComp, SWT.LEFT | SWT.BORDER);
			queryConnNameText.setLayoutData(CommonUITool.createGridData(
					GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

			Label backgroundLabel = new Label(connectionNameComp, SWT.None);
			backgroundLabel.setText(Messages.lblBackground);
			backgroundLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

			DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, false);
			RGB selectedColor = null;
			if (editorConfig != null) {
				selectedColor = editorConfig.getBackGround();
			}else{
				selectedColor = EditorConstance.getDefaultBackground();
			}
			selectColorCombo = new SelectColorCombo(connectionNameComp, SWT.BORDER,
					selectedColor);
			selectColorCombo.setLayoutData(CommonUITool.createGridData(
					GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

			Label commentLabel = new Label(connectionNameComp, SWT.None);
			commentLabel.setText(Messages.lblComment);
			commentLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

			commentText = new Text(connectionNameComp, SWT.BORDER);
			commentText.setLayoutData(CommonUITool.createGridData(
					GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
			commentText.setTextLimit(64);
			if (editorConfig != null && editorConfig.getDatabaseComment() != null) {
				commentText.setText(editorConfig.getDatabaseComment());
			}
		}

		connectionComp = new ConnectionComposite(composite, true, false);

		initial();

		if (isNewQueryConn) {
			setTitle(Messages.titleNewQueryConnDialog);
			setMessage(Messages.msgNewQueryConnDialog);
			queryConnNameText.setFocus();
		} else {
			setTitle(Messages.titleLoginQueryConnDialog);
			setMessage(Messages.msgLoginQueryConnDialog);
			connectionComp.getPasswordText().setFocus();
		}

		if (isNewQueryConn && database != null) {
			queryConnNameText.selectAll();
		}

		return parentComp;
	}

	/**
	 * initialize some values
	 *
	 */
	private void initial() {
		if (database != null) {
			String name = database.getLabel();
			name = isNewQueryConn ? name + "(1)" : name;
			queryConnNameText.setText(name);
		}
		queryConnNameText.addModifyListener(this);
		connectionComp.init(database, this);
	}

	/**
	 * @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize()
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		CommonUITool.centerShell(getShell());
		if (isNewQueryConn) {
			getShell().setText(Messages.titleNewQueryConnDialog);
		} else {
			getShell().setText(Messages.titleLoginQueryConnDialog);
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, TEST_CONNECT_ID, Messages.btnTestConn, false);
		createButton(parent, SAVE_ID, Messages.btnSave, true);
		createButton(parent, CONNECT_ID, Messages.btnConnect, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.common.ui.common.Messages.btnCancel, false);
		setButtonStatus(false);
		if (database != null) {
			valid();
		}
	}

	/**
	 *
	 * Set button status
	 *
	 * @param isEnabled boolean
	 */
	private void setButtonStatus(boolean isEnabled) {
		if (getButton(SAVE_ID) != null) {
			getButton(SAVE_ID).setEnabled(isEnabled);
		}
		if (getButton(CONNECT_ID) != null) {
			getButton(CONNECT_ID).setEnabled(isEnabled);
		}
		if (getButton(TEST_CONNECT_ID) != null) {
			getButton(TEST_CONNECT_ID).setEnabled(isEnabled);
		}
	}

	/**
	 * Validate the data
	 *
	 */
	private void valid() {
		setErrorMessage(null);
		String queryConnName = queryConnNameText.getText();
		boolean isValidConnName = queryConnName != null
				&& queryConnName.length() > 0;
		if (!isValidConnName) {
			setButtonStatus(false);
			return;
		}
		if (isExistConnection(queryConnName)) {
			setButtonStatus(false);
			setErrorMessage(Messages.errConnNameExist);
			return;
		}

		// if a jdbc driver was not set
		String jdbcDriver = connectionComp.getJdbcCombo().getText();
		if (jdbcDriver == null || jdbcDriver.length() == 0) {
			setErrorMessage(Messages.errConnJdbcNotSet);
			return;
		}

		boolean isValid = connectionComp.valid();
		if (!isValid) {
			setButtonStatus(false);
			String errorMsg = connectionComp.getErrorMsg();
			if (errorMsg != null) {
				setErrorMessage(errorMsg);
			}
			return;
		}
		setButtonStatus(true);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 * @param buttonId the id of the button that was pressed (see
	 *        <code>IDialogConstants.*_ID</code> constants)
	 */
	protected void buttonPressed(int buttonId) {

		if (buttonId == TEST_CONNECT_ID || buttonId == CONNECT_ID
				|| buttonId == SAVE_ID) {
			String name = queryConnNameText.getText();
			String dbName = connectionComp.getDatabaseText().getText();
			String brokerIp = connectionComp.getBrokerIpText().getText();
			String brokerPort = connectionComp.getBrokerPortText().getText();
			String userName = connectionComp.getUserNameText().getText();
			String password = connectionComp.getPasswordText().getText();
			String charset = connectionComp.getCharsetCombo().getText();
			String jdbcDriver = connectionComp.getJdbcCombo().getText();
			String jdbcAttrs = connectionComp.getAttrText().getText().trim();
			boolean isAutoCommit = connectionComp.isAutoCommit();

			// [TOOLS-2425]Support shard broker
			boolean isShard = connectionComp.getBtnShard().getSelection();
			int currentShardId = connectionComp.getCurShardId();
			int currentShardVal = connectionComp.getCurShardVal();
			int defaultShardQueryType = connectionComp.getShardQueryType();

			ServerInfo serverInfo = new ServerInfo();
			serverInfo.setServerName(name);
			serverInfo.setHostAddress(brokerIp);
			serverInfo.setHostMonPort(Integer.parseInt(brokerPort));
			serverInfo.setHostJSPort(Integer.parseInt(brokerPort) + 1);
			serverInfo.setUserName(dbName + "@" + brokerIp);
			serverInfo.setJdbcDriverVersion(jdbcDriver);

			DatabaseInfo dbInfo = new DatabaseInfo(dbName, serverInfo);
			dbInfo.setBrokerIP(brokerIp);
			dbInfo.setBrokerPort(brokerPort);
			dbInfo.setCharSet(charset);
			dbInfo.setJdbcAttrs(jdbcAttrs);

			// [TOOLS-2425]Support shard broker
			dbInfo.setShard(isShard);
			dbInfo.setCurrentShardId(currentShardId);
			dbInfo.setCurrentShardVal(currentShardVal);
			dbInfo.setShardQueryType(defaultShardQueryType);

			DbUserInfo userInfo = new DbUserInfo();
			userInfo.setDbName(dbName);
			userInfo.setName(userName);
			userInfo.setNoEncryptPassword(password);
			dbInfo.setAuthLoginedDbUserInfo(userInfo);
			dbInfo.setSupportUserSchema(CompatibleUtil.isAfter112(dbInfo));

			if (buttonId == TEST_CONNECT_ID || buttonId == CONNECT_ID) {
				TaskExecutor taskExcutor = new ConnectDatabaseExecutor(dbInfo);
				new ExecTaskWithProgress(taskExcutor).exec();

				if(!taskExcutor.isSuccess()) {
					return;
				}

				if(buttonId == TEST_CONNECT_ID) {
					CommonUITool.openInformationBox(
							Messages.titleSuccess,
							Messages.msgTestConnSuccess);
					return;
				}
			}
			if (buttonId == CONNECT_ID) {
				// check whether dba authorization
				IsDBAUserTask checkTask = new IsDBAUserTask(dbInfo);
				checkTask.execute();
				userInfo.setDbaAuthority(checkTask.isDBAUser());

				dbInfo.setRunningType(DbRunningType.CS);
				dbInfo.getServerInfo().setConnected(true);
				dbInfo.setLogined(true);

				boolean userChanged = !name.equals(oldLoginUserName);
				fireLogoutEvent =  oldDatabaseIsLogin && userChanged;
			}

			if (!isNewQueryConn && database != null) {
				boolean isContinue = CQBConnectionUtils.processConnectionLogout(database);
				if (!isContinue) {
					return;
				}
			}

			// If this is new connection,then warning charset setting
			if (isNewQueryConn) {
				boolean sureCharset = CommonUITool.openConfirmBox(Messages.bind(
						com.cubrid.cubridquery.ui.connection.Messages.msgConfirmCharset,
						charset));
				if (!sureCharset) {
					connectionComp.getCharsetCombo().setFocus();
					return;
				}
			}

			if (isNewQueryConn
					&& (buttonId == CONNECT_ID || buttonId == SAVE_ID)
					&& EditorConstance.getDefaultBackground().equals(
							getSelectedBackground())) {
				boolean surePurpose = CommonUITool.openConfirmBox(Messages.msgUseDefaultPurpose);
				if (!surePurpose) {
					selectColorCombo.expandMenu();
					return;
				}
			}

			CubridServer server = new CubridServer(name, name, null, null);
			server.setServerInfo(serverInfo);
			server.setType(NodeType.SERVER);
			String dbId = name + ICubridNodeLoader.NODE_SEPARATOR + name;
			if (isNewQueryConn) {
				database = new CubridDatabase(dbId, name);
			} else {
				database.setId(dbId);
				database.setLabel(name);
				database.setUniqueName(name);
			}
			database.setDatabaseInfo(dbInfo);
			database.setServer(server);
			database.setStartAndLoginIconPath("icons/navigator/database_start_connected.png");
			database.setStartAndLogoutIconPath("icons/navigator/database_start_disconnected.png");

			CubridNodeLoader loader = new CQBDbConnectionLoader();
			loader.setLevel(ICubridNodeLoader.FIRST_LEVEL);
			database.setLoader(loader);
			database.setAutoSavePassword(connectionComp.isAutoSavePassword());

			if (buttonId == CONNECT_ID || buttonId == SAVE_ID) {
				if (database != null) {
					DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, false);
					if (editorConfig == null) {
						editorConfig = new DatabaseEditorConfig();
					}
					editorConfig.setBackGround(getSelectedBackground());
					editorConfig.setDatabaseComment(getDatabaseComment());

					QueryOptions.putEditorConfig(database, editorConfig, false);
				}
				newInfo = CQBDBNodePersistManager.getInstance().getConnectionInfo(database);
				if (isNewQueryConn) {
					CQBDBNodePersistManager.getInstance().fireAddDatabase(database);
				} else if (oldInfo != null) {
					CQBDBNodePersistManager.getInstance().fireModifyDatabase(oldInfo, newInfo);
				}
			}

			QueryOptions.setAutoCommit(serverInfo, isAutoCommit);
		}
		setReturnCode(buttonId);
		close();
	}

	/**
	 *
	 * Check the database connection whether exist
	 *
	 * @param name String
	 * @return boolean
	 */
	private boolean isExistConnection(String name) {
		List<CubridDatabase> databaseList = CQBDBNodePersistManager.getInstance().getAllDatabase();
		for (CubridDatabase db : databaseList) {
			if (isNewQueryConn && database != null && db.getName().equals(name)) {
				return true;
			}
			if (name != null && name.equals(db.getName())
					&& (database == null || !name.equals(database.getName()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event an event containing information about the modify
	 */
	public void modifyText(ModifyEvent event) {
		if (event.widget == connectionComp.getBrokerIpText()) {
			if (connectionComp.getBrokerIpText().getText().length() > 0) {
				isBrokerManualChanged = true;
			} else {
				isBrokerManualChanged = false;
			}
		}
		if (database == null && event.widget == queryConnNameText
				&& !isBrokerManualChanged) {
			connectionComp.getBrokerIpText().removeModifyListener(this);
			//for keeping 'localhost' as default value Broker IP, disable the auto-complete function. commented by Kevin.
			//connectionComp.getBrokerIPText().setText(
			//		queryConnNameText.getText());
			connectionComp.getBrokerIpText().addModifyListener(this);
		}
		valid();
	}

	public CubridDatabase getDatabase() {
		return database;
	}

	public RGB getSelectedBackground() {
		return selectColorCombo.getSelectedColor();
	}

	public String getDatabaseComment() {
		return commentText.getText();
	}

	public boolean isFireLogoutEvent() {
		return fireLogoutEvent;
	}
}
