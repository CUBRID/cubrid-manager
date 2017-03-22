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
package com.cubrid.cubridmanager.ui.replication.control;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.ui.replication.Messages;

/**
 * 
 * Slave database information wizard page for change slave database wizard
 * 
 * @author wuyingshi
 * @version 1.0 - 2009-6-4 created by wuyingshi
 */
public class SlaveDbInfoPage extends
		WizardPage implements
		ModifyListener {

	public static final String PAGENAME = "ChangeSlaveDbWizard/SlaveDbInfoPage";
	private Text slaveDbNameText = null;
	private Text slaveDbUserText = null;
	private Text slaveDbaPasswordText = null;
	private Text confirmSlaveDbaPasswordText = null;
	private Text slaveDbPathText = null;
	private Text masterDbHostText = null;
	private Text masterDbHostPortText = null;
	private Text masterDbHostUserText = null;
	private Text masterHostPasswordText = null;
	private Text masterDbNameText = null;
	private Text masterDbUserText = null;
	private Text masterDbDbaPasswordText = null;

	private ReplicationInfo replInfo = null;
	private final CubridDatabase database;
	private String databasePath = null;

	/**
	 * The constructor
	 */
	public SlaveDbInfoPage(CubridDatabase database) {
		super(PAGENAME);
		setPageComplete(false);
		this.database = database;
	}

	/**
	 * Creates the controls for this page
	 * 
	 * @param parent Composite
	 */
	public void createControl(Composite parent) {
		setReplInfo((ReplicationInfo) database.getAdapter(ReplicationInfo.class));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);

		Group masterDbInfoGroup = new Group(composite, SWT.NONE);
		masterDbInfoGroup.setText(Messages.chsldb0grpMasterDbInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		masterDbInfoGroup.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 3;
		masterDbInfoGroup.setLayout(layout);

		Label masterDbHostLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		masterDbHostLabel.setText(Messages.chsldb0lblMasterHost);
		masterDbHostLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		masterDbHostText = new Text(masterDbInfoGroup, SWT.LEFT | SWT.BORDER);
		masterDbHostText.setEnabled(false);
		masterDbHostText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label masterDbHostPortLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		masterDbHostPortLabel.setText(Messages.chsldb0lblMasterDbPort);
		masterDbHostPortLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1,
				-1));
		masterDbHostPortText = new Text(masterDbInfoGroup, SWT.LEFT
				| SWT.BORDER);
		masterDbHostPortText.setTextLimit(5);
		masterDbHostPortText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label masterDbHostUserLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		masterDbHostUserLabel.setText(Messages.chsldb0lblMasterHostUser);
		masterDbHostUserLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1,
				-1));
		masterDbHostUserText = new Text(masterDbInfoGroup, SWT.LEFT
				| SWT.BORDER);
		masterDbHostUserText.setEnabled(false);
		masterDbHostUserText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label masterHostPasswordLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		masterHostPasswordLabel.setText(Messages.chsldb0lblMasterHostPasswd);
		masterHostPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1,
				-1, -1));
		masterHostPasswordText = new Text(masterDbInfoGroup, SWT.LEFT
				| SWT.BORDER | SWT.PASSWORD);
		masterHostPasswordText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
		masterHostPasswordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label masterDbNameLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		masterDbNameLabel.setText(Messages.chsldb0lblMasterDbName);
		masterDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		masterDbNameText = new Text(masterDbInfoGroup, SWT.LEFT | SWT.BORDER);
		masterDbNameText.setTextLimit(ValidateUtil.MAX_DB_NAME_LENGTH);
		masterDbNameText.setEnabled(false);
		masterDbNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label dbMasterUserLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		dbMasterUserLabel.setText(Messages.chsldb0lblMasterDbUser);
		dbMasterUserLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		masterDbUserText = new Text(masterDbInfoGroup, SWT.LEFT | SWT.BORDER);
		masterDbUserText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
		masterDbUserText.setEnabled(false);
		masterDbUserText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label masterDbDbaPasswordLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		masterDbDbaPasswordLabel.setText(Messages.chsldb0lblMasterDbaPasswd);
		masterDbDbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1,
				-1, -1));
		masterDbDbaPasswordText = new Text(masterDbInfoGroup, SWT.LEFT
				| SWT.BORDER | SWT.PASSWORD);
		masterDbDbaPasswordText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
		masterDbDbaPasswordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Group slaveDbInfoGroup = new Group(composite, SWT.NONE);
		slaveDbInfoGroup.setText(Messages.chsldb0grpSlaveDbInfo);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		slaveDbInfoGroup.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 3;
		slaveDbInfoGroup.setLayout(layout);

		Label slaveDbNameLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
		slaveDbNameLabel.setText(Messages.chsldb0lblSlaveDbName);
		slaveDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		slaveDbNameText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER);
		slaveDbNameText.setTextLimit(ValidateUtil.MAX_DB_NAME_LENGTH);
		slaveDbNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label dbUserLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
		dbUserLabel.setText(Messages.chsldb0lblSlaveDbUser);
		dbUserLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		slaveDbUserText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER);
		slaveDbUserText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
		slaveDbUserText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label dbaPasswordLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
		dbaPasswordLabel.setText(Messages.chsldb0lblSlaveDbDbaPasswd);
		dbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		slaveDbaPasswordText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.PASSWORD);
		slaveDbaPasswordText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
		slaveDbaPasswordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label confirmDbaPasswordLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
		confirmDbaPasswordLabel.setText(Messages.chsldb0lblConfirmSlaveDbDbaPasswd);
		confirmDbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1,
				-1, -1));
		confirmSlaveDbaPasswordText = new Text(slaveDbInfoGroup, SWT.LEFT
				| SWT.BORDER | SWT.PASSWORD);
		confirmSlaveDbaPasswordText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
		confirmSlaveDbaPasswordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label dbPathLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
		dbPathLabel.setText(Messages.chsldb0lblSlaveDbPath);
		dbPathLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		slaveDbPathText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER);
		slaveDbPathText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		init();

		setTitle(Messages.chsldb0titleChangeSlaveDbPage);
		setMessage(Messages.chsldb0msgChangeSlaveDbPage);
		setControl(composite);
	}

	/**
	 * input content of to be verified.
	 * 
	 * @return error message
	 */
	private String validInput() {
		String hostIp = masterDbHostText.getText();
		String masterDbHostPort = masterDbHostPortText.getText();
		//verify master database server port
		if (masterDbHostPort == null
				|| "".equals(masterDbHostPort)
				|| !ValidateUtil.isNumber(masterDbHostPort)
				|| (Integer.parseInt(masterDbHostPort) < 1024 || Integer.parseInt(masterDbHostPort) > 65535)) {
			return Messages.chsldb0errInvalidMasterDbPort;
		}

		ServerInfo serverInfo = ServerManager.getServer(hostIp,
				Integer.parseInt(masterDbHostPort),
				masterDbHostUserText.getText());
		//verify whether a user that it is not admin already logined
		if (serverInfo != null && !serverInfo.getLoginedUserInfo().isAdmin()) {
			return Messages.bind(Messages.errInvalidUser, hostIp);
		}
		//verify whether a user that it is not dba already logined
		String dbaPassword = "";
		boolean isDbaLogined = false;
		if (serverInfo != null && serverInfo.isConnected()) {
			DatabaseInfo dbInfo = serverInfo.getLoginedUserInfo().getDatabaseInfo(
					masterDbNameText.getText());
			if (dbInfo != null
					&& dbInfo.isLogined()
					&& !"dba".equalsIgnoreCase(dbInfo.getAuthLoginedDbUserInfo().getName())) {
				return Messages.bind(Messages.errInvalidDbUser,
						masterDbNameText.getText());
			}
			if (dbInfo != null
					&& dbInfo.isLogined()
					&& "dba".equalsIgnoreCase(dbInfo.getAuthLoginedDbUserInfo().getName())) {
				dbaPassword = dbInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
				dbaPassword = dbaPassword == null ? "" : dbaPassword;
				isDbaLogined = true;
			}
		}
		//verify the master database server password
		String hostPassword = masterHostPasswordText.getText();
		boolean isValidPassword = hostPassword.trim().length() >= 4
				&& hostPassword.indexOf(" ") < 0;
		if (isValidPassword && serverInfo != null
				&& serverInfo.getLoginedUserInfo() != null) {
			isValidPassword = hostPassword.equals(serverInfo.getLoginedUserInfo().getPassword());
		}
		if (!isValidPassword) {
			return Messages.chsldb0errInvalidMasterHostPasswd;
		}
		//verify master database password
		String masterDbPassword = masterDbDbaPasswordText.getText();
		boolean isValidMasterDbaPassword = masterDbPassword.length() >= 4
				&& masterDbPassword.indexOf(" ") < 0;
		if (!isValidMasterDbaPassword) {
			return Messages.chsldb0errInvalidMasterDbaPasswd;
		}
		if (isDbaLogined && !masterDbPassword.equals(dbaPassword)) {
			return Messages.chsldb0errInvalidMasterDbaPasswd;
		}
		//verify slave database name
		if (slaveDbNameText.getText() == null
				|| "".equals(slaveDbNameText.getText().trim())
				|| !ValidateUtil.isValidDBName(slaveDbNameText.getText())) {
			return Messages.chsldb0errInvalidSlaveDbName;
		}

		DatabaseInfo databaseInfo = database.getServer().getServerInfo().getLoginedUserInfo().getDatabaseInfo(
				slaveDbNameText.getText());
		boolean isDatabaseNameAlrExist = databaseInfo != null;
		if (isDatabaseNameAlrExist) {
			return Messages.errDbExist;
		}

		if (slaveDbUserText.getText() == null
				|| "".equals(slaveDbUserText.getText().trim())
				|| "dba".equals(slaveDbUserText.getText().trim())
				|| slaveDbUserText.getText().indexOf(" ") >= 0
				|| slaveDbUserText.getText().trim().length() > ValidateUtil.MAX_NAME_LENGTH) {
			return Messages.errDbUser;
		}

		if (slaveDbaPasswordText.getText() == null
				|| "".equals(slaveDbaPasswordText.getText())
				|| slaveDbaPasswordText.getText().length() < 4
				|| slaveDbaPasswordText.getText().indexOf(" ") >= 0) {
			return Messages.errDbPassword;
		}

		if (confirmSlaveDbaPasswordText.getText() == null
				|| "".equals(confirmSlaveDbaPasswordText.getText())
				|| confirmSlaveDbaPasswordText.getText().length() < 4
				|| confirmSlaveDbaPasswordText.getText().indexOf(" ") >= 0) {
			return Messages.errConfirmedDbPassword;
		}

		if (!(slaveDbaPasswordText.getText().equals(confirmSlaveDbaPasswordText.getText()))) {
			return Messages.errPasswordNotEqual;
		}

		if (slaveDbPathText.getText() == null
				|| "".equals(slaveDbPathText.getText().trim())
				|| !ValidateUtil.isValidPathName(slaveDbPathText.getText())) {
			return Messages.chsldb0errInvalidSlaveDbPath;
		}

		return null;
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event ModifyEvent
	 */
	public void modifyText(ModifyEvent event) {
		setPageComplete(false);
		if (event.widget == slaveDbNameText) {
			String separator = database.getServer().getServerInfo().getPathSeparator();
			slaveDbPathText.setText(databasePath + separator
					+ slaveDbNameText.getText());
		}
		String msg = validInput();
		if (msg != null && !"".equals(msg)) {
			setErrorMessage(msg);
			return;
		}
		setErrorMessage(null);
		setPageComplete(true);
	}

	/**
	 * initialize some values
	 */
	private void init() {
		slaveDbUserText.setText("replication_user");
		masterDbUserText.setText("dba");
		masterDbHostText.setText(replInfo.getMasterList().get(0).getMasterIp());
		masterDbHostPortText.setText("8001");
		masterDbHostPortText.selectAll();
		masterDbHostUserText.setText("admin");
		if (database.getServer().getServerInfo().getEnvInfo() != null) {
			databasePath = database.getServer().getServerInfo().getEnvInfo().getDatabaseDir();
		}
		slaveDbPathText.setText(databasePath == null ? "" : databasePath);
		masterDbNameText.setText(replInfo.getMasterList().get(0).getMasterDbName());
		masterDbHostPortText.addModifyListener(this);
		masterHostPasswordText.addModifyListener(this);
		masterDbDbaPasswordText.addModifyListener(this);
		slaveDbNameText.addModifyListener(this);
		slaveDbUserText.addModifyListener(this);
		slaveDbaPasswordText.addModifyListener(this);
		confirmSlaveDbaPasswordText.addModifyListener(this);
		slaveDbPathText.addModifyListener(this);
		String msg = validInput();
		if (msg != null && !"".equals(msg)) {
			setErrorMessage(msg);
		}
	}

	public String getSlaveDbName() {
		return slaveDbNameText.getText();
	}

	public String getSlaveDbUser() {
		return slaveDbUserText.getText();
	}

	public String getSlaveDbaPassword() {
		return slaveDbaPasswordText.getText();
	}

	public String getSlaveDbPath() {
		return slaveDbPathText.getText();
	}

	public String getMasterHostIp() {
		return masterDbHostText.getText();
	}

	public String getMasterHostPassword() {
		return masterHostPasswordText.getText();
	}

	public String getMasterDbName() {
		return masterDbNameText.getText();
	}

	public String getMasterDbUser() {
		return masterDbUserText.getText();
	}

	public String getMasterHostPort() {
		return masterDbHostPortText.getText();
	}

	public String getMasterDbDbaPassword() {
		return masterDbDbaPasswordText.getText();
	}

	/**
	 * @return the replInfo
	 */
	public ReplicationInfo getReplInfo() {
		return replInfo;
	}

	/**
	 * @param replInfo the replInfo to set
	 */
	public void setReplInfo(ReplicationInfo replInfo) {
		this.replInfo = replInfo;
	}

	/**
	 * @return the databasePath
	 */
	public String getDatabasePath() {
		return databasePath;
	}

}
