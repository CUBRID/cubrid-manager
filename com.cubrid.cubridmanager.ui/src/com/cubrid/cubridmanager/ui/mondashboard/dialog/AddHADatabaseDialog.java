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
package com.cubrid.cubridmanager.ui.mondashboard.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HostStatusType;
import com.cubrid.cubridmanager.core.mondashboard.task.GetDbModeTask;
import com.cubrid.cubridmanager.core.mondashboard.task.VerifyDbUserPasswordTask;
import com.cubrid.cubridmanager.ui.host.dialog.ConnectHostExecutor;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;

/**
 * 
 * Add HA database dialog
 * 
 * @author pangqiren
 * @version 1.0 - 2010-6-10 created by pangqiren
 */
public class AddHADatabaseDialog extends
		CMTitleAreaDialog implements
		ModifyListener {

	private Combo ipCombo;
	private Text portText;
	private Text userNameText;
	private Text passwordText;
	private Text dbNameText;
	private Button addDatabaseButton;
	private Button deleteDbButton;
	private TableViewer dbTableViewer;
	private Table dbTable;
	private final HADatabaseStatusInfo haDbStatusInfo;
	private final List<HAHostStatusInfo> haHostStatusInfoList;
	private final List<Map<String, Object>> dbNodeList = new ArrayList<Map<String, Object>>();
	private Text dbaPasswordText;

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 */
	public AddHADatabaseDialog(Shell parentShell,
			HADatabaseStatusInfo haDbStatusInfo,
			List<HAHostStatusInfo> haHostStatusInfoList) {
		super(parentShell);
		this.haDbStatusInfo = haDbStatusInfo;
		this.haHostStatusInfoList = haHostStatusInfoList;
	}

	/**
	 * Create dialog area content
	 * 
	 * @param parent the parent composite
	 * @return the control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComp, SWT.NONE);
		{
			GridLayout compLayout = new GridLayout();
			compLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			compLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			compLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			compLayout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(compLayout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		Group cmServerInfoGroup = new Group(composite, SWT.NONE);
		cmServerInfoGroup.setText(Messages.grpHostInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		cmServerInfoGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		cmServerInfoGroup.setLayout(layout);

		Label ipLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		ipLabel.setText(Messages.lblIPAddress);
		ipLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		ipCombo = new Combo(cmServerInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		ipCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label portNameLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		portNameLabel.setText(Messages.lblPort);
		portNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		portText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		portText.setTextLimit(5);
		portText.setText("8001");
		portText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		portText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				portText.selectAll();
				portText.setFocus();
			}
		});

		Label userNameLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		userNameLabel.setText(Messages.lblUserName);
		userNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		userNameText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		userNameText.setText("admin");
		userNameText.setEnabled(false);
		userNameText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
		userNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label passwordLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		passwordLabel.setText(Messages.lblPassword);
		passwordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		passwordText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.PASSWORD
				| SWT.BORDER);
		passwordText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
		passwordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		passwordText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				passwordText.selectAll();
				passwordText.setFocus();
			}
		});

		Label dbNameLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		dbNameLabel.setText(Messages.lblDbName);
		dbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dbNameText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		dbNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

		Label dbaPasswordLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		dbaPasswordLabel.setText(Messages.lblDbaPassword);
		dbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dbaPasswordText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.PASSWORD);
		dbaPasswordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

		Composite btnComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 5;
		btnComposite.setLayout(rowLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = GridData.END;
		btnComposite.setLayoutData(gridData);

		addDatabaseButton = new Button(btnComposite, SWT.NONE);
		addDatabaseButton.setText(Messages.btnAddDb);
		addDatabaseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//check this database whether already be added
				String dbName = dbNameText.getText();
				if (isDbExist()) {
					setErrorMessage(Messages.bind(Messages.errDbExist, dbName));
					return;
				}

				//connect server
				String address = ipCombo.getText();
				String port = portText.getText();
				String userName = userNameText.getText();
				String password = passwordText.getText();
				ServerInfo serverInfo = ServerManager.getServer(
						address, Integer.parseInt(port), userName);
				if (serverInfo == null) {
					serverInfo = new ServerInfo();
					serverInfo.setServerName(address);
					serverInfo.setHostAddress(address);
					serverInfo.setHostMonPort(Integer.parseInt(port));
					serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
					serverInfo.setUserName(userName);
					serverInfo.setUserPassword(password);
					serverInfo.setJdbcDriverVersion(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
				}

				String dbaPassword = dbaPasswordText.getText();
				TaskExecutor executor = null;

				VerifyDbUserPasswordTask verifyDbUserPasswordTask = new VerifyDbUserPasswordTask(
						serverInfo);
				verifyDbUserPasswordTask.setDbName(dbName);
				verifyDbUserPasswordTask.setDbUser("dba");
				verifyDbUserPasswordTask.setDbPassword(dbaPassword);

				if (serverInfo.isConnected()) {
					DatabaseInfo dbInfo = serverInfo.getLoginedUserInfo().getDatabaseInfo(
							dbName);
					if (dbInfo == null) {
						setErrorMessage(Messages.errDbNoExist);
						return;
					}
					executor = new CommonTaskExec(null);
					if (dbInfo.isLogined()
							&& dbInfo.getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(
									"dba")) {
						String pwd = dbInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
						if (!dbaPassword.equals(pwd)) {
							CommonUITool.openErrorBox(Messages.errDbaPassowrd);
							dbaPasswordText.selectAll();
							dbaPasswordText.setFocus();
							return;
						}
					} else {
						executor.addTask(verifyDbUserPasswordTask);
					}
				} else {
					executor = new ConnectHostExecutor(getShell(), serverInfo, true);
					executor.addTask(verifyDbUserPasswordTask);
				}
				//get this database status
				HAHostStatusInfo haHostStatusInfo = null;
				HADatabaseStatusInfo haDbStatusInfo = null;
				GetDbModeTask getDbModeTask = new GetDbModeTask(serverInfo);
				List<String> dbList = new ArrayList<String>();
				dbList.add(dbNameText.getText());
				getDbModeTask.setDbList(dbList);
				executor.addTask(getDbModeTask);

				new ExecTaskWithProgress(executor).exec(true, true);
				if (!executor.isSuccess()) {
					if (verifyDbUserPasswordTask != null
							&& verifyDbUserPasswordTask.getErrorMsg() != null
							&& verifyDbUserPasswordTask.getErrorMsg().length() > 0) {
						dbaPasswordText.selectAll();
						dbaPasswordText.setFocus();
					}
					return;
				}

				if (getDbModeTask.getDbModes() != null
						&& getDbModeTask.getDbModes().size() > 0) {
					List<HADatabaseStatusInfo> dbModeList = getDbModeTask.getDbModes();
					haDbStatusInfo = dbModeList.get(0);
					haHostStatusInfo = getHAHostStatusInfo(serverInfo.getHostAddress());
					if (haHostStatusInfo != null) {
						haDbStatusInfo.setHaHostStatusInfo(haHostStatusInfo);
						haHostStatusInfo.addHADatabaseStatus(haDbStatusInfo);
					}
				}

				if (haDbStatusInfo == null) {
					haDbStatusInfo = HAUtil.getHADatabaseStatusInfo(
							dbNameText.getText(), haHostStatusInfo, serverInfo);
				}

				if (haHostStatusInfo == null) {
					haHostStatusInfo = HAUtil.getHAHostStatusInfo(serverInfo);
					haHostStatusInfo.addHADatabaseStatus(haDbStatusInfo);
					haDbStatusInfo.setHaHostStatusInfo(haHostStatusInfo);
				}

				DatabaseNode dbNode = new DatabaseNode();
				dbNode.setDbName(dbNameText.getText());
				dbNode.setDbUser("dba");
				dbNode.setDbPassword(dbaPassword);
				dbNode.setName(dbNameText.getText());
				dbNode.setConnected(true);
				dbNode.setHaDatabaseStatus(haDbStatusInfo);

				HostNode hostNode = new HostNode();
				hostNode.setName(ipCombo.getText() + ":" + portText.getText());
				hostNode.setIp(ipCombo.getText());
				hostNode.setPort(portText.getText());
				hostNode.setUserName(userNameText.getText());
				hostNode.setPassword(passwordText.getText());
				hostNode.setHostStatusInfo(haHostStatusInfo);
				hostNode.setConnected(true);
				dbNode.setParent(hostNode);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("0", dbNode.getDbName());
				map.put("1", DBStatusType.getShowText(dbNode.getDbStatusType()));
				map.put("2", dbNode.getParent().getIp());
				map.put("3", dbNode.getParent().getPort());
				map.put(
						"4",
						HostStatusType.getShowText(haHostStatusInfo.getStatusType()));
				map.put("5", dbNode);

				if (dbNode.getDbStatusType() == DBStatusType.ACTIVE
						|| dbNode.getDbStatusType() == DBStatusType.TO_BE_ACTIVE) {
					dbNodeList.add(0, map);
				} else {
					dbNodeList.add(map);
				}
				dbTableViewer.refresh();
				for (int i = 0; i < dbTable.getColumnCount(); i++) {
					dbTable.getColumn(i).pack();
				}
				verify();
			}
		});
		addDatabaseButton.setEnabled(false);

		createTable(composite);
		setTitle(Messages.titleAddHADbDialog);
		initial();
		return parentComp;
	}

	/**
	 * 
	 * Create table area
	 * 
	 * @param parent the parent composite
	 */
	private void createTable(Composite parent) {

		Label tipLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
		tipLabel.setText(Messages.lblDbListInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		tipLabel.setLayoutData(gridData);

		final String[] columnNameArr = new String[]{Messages.colDbName,
				Messages.colDbStatus, Messages.colIP, Messages.colPort,
				Messages.colServerStatus };

		dbTableViewer = CommonUITool.createCommonTableViewer(parent, null,
				columnNameArr, CommonUITool.createGridData(GridData.FILL_BOTH, 1,
						1, -1, 100));
		dbTable = dbTableViewer.getTable();
		for (int i = 0; i < dbTable.getColumnCount(); i++) {
			dbTable.getColumn(i).pack();
		}

		dbTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				deleteDbButton.setEnabled(dbTable.getSelectionCount() > 0);
			}
		});
		dbTableViewer.setInput(dbNodeList);

		Composite composite = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 5;
		composite.setLayout(rowLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = GridData.END;
		composite.setLayoutData(gridData);

		deleteDbButton = new Button(composite, SWT.NONE);
		deleteDbButton.setText(Messages.btnDelete);
		deleteDbButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				StructuredSelection selection = (StructuredSelection) dbTableViewer.getSelection();
				if (selection != null && !selection.isEmpty()) {
					dbNodeList.removeAll(selection.toList());
				}
				dbTableViewer.refresh();
				deleteDbButton.setEnabled(dbTable.getSelectionCount() > 0);
				verify();
			}
		});
		deleteDbButton.setEnabled(false);
	}

	/**
	 * 
	 * Check whether the database already exist
	 * 
	 * @return boolean
	 */
	private boolean isDbExist() {
		String ip = ipCombo.getText();
		String port = portText.getText();
		String userName = userNameText.getText();
		String dbName = dbNameText.getText();
		for (int i = 0; i < dbNodeList.size(); i++) {
			DatabaseNode dbNode = (DatabaseNode) dbNodeList.get(i).get("5");
			HostNode hostNode = dbNode.getParent();
			if (ip.equals(hostNode.getIp()) && port.equals(hostNode.getPort())
					&& userName.equals(hostNode.getUserName())
					&& dbName.equals(dbNode.getDbName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * Initial the value of dialog field
	 * 
	 */
	private void initial() {
		DBStatusType type = haDbStatusInfo.getStatusType();
		String ip = haDbStatusInfo.getHaHostStatusInfo().getIp();
		String dbName = haDbStatusInfo.getDbName();
		if (type == DBStatusType.ACTIVE || type == DBStatusType.TO_BE_ACTIVE) {
			setMessage(Messages.bind(Messages.msgAddStadnbyDbDialog, dbName));
		} else if (type == DBStatusType.STANDBY
				|| type == DBStatusType.TO_BE_STANDBY
				|| type == DBStatusType.MAINTENANCE) {
			setMessage(Messages.bind(Messages.msgAddActiveDbDialog, dbName));
		}
		for (int i = 0; haHostStatusInfoList != null
				&& i < haHostStatusInfoList.size(); i++) {
			String address = haHostStatusInfoList.get(i).getIp();
			if (!address.equals(ip)) {
				ipCombo.add(address);
			}
		}
		if (ipCombo.getItemCount() > 0) {
			ipCombo.select(0);
		}
		dbNameText.setText(dbName);
		ipCombo.addModifyListener(this);
		portText.addModifyListener(this);
		passwordText.addModifyListener(this);
		portText.selectAll();
		portText.setFocus();
	}

	/**
	 * 
	 * Get HAHostStatusInfo by IP
	 * 
	 * @param ip The String
	 * @return HAHostStatusInfo
	 */
	private HAHostStatusInfo getHAHostStatusInfo(String ip) {
		for (int i = 0; haHostStatusInfoList != null
				&& i < haHostStatusInfoList.size(); i++) {
			HAHostStatusInfo haHostStatusInfo = haHostStatusInfoList.get(i);
			if (com.cubrid.common.core.util.StringUtil.isIpEqual(ip,
					haHostStatusInfo.getIp())) {
				return haHostStatusInfo;
			}
		}
		return null;
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setText(Messages.titleAddHADbDialog);
	}

	/**
	 * Create buttons for button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnOK, true);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When modify the page content and check the validation
	 * 
	 * @param event the modify event
	 */
	public void modifyText(ModifyEvent event) {
		String ip = ipCombo.getText();
		boolean isValidIP = ip.trim().length() > 0;
		if (!isValidIP) {
			setErrorMessage(Messages.errIPAddress);
			addDatabaseButton.setEnabled(false);
			return;
		}

		String port = portText.getText();
		boolean isValidPort = ValidateUtil.isNumber(port);
		if (isValidPort) {
			int portVal = Integer.parseInt(port);
			if (portVal < 1024 || portVal > 65535) {
				isValidPort = false;
			}
		}
		if (!isValidPort) {
			setErrorMessage(Messages.errPort);
			addDatabaseButton.setEnabled(false);
			return;
		}

		String password = passwordText.getText();
		boolean isValidPassword = password.trim().length() >= 4
				&& password.indexOf(" ") < 0;

		ServerInfo serverInfo = ServerManager.getServer(
				ipCombo.getText(), Integer.parseInt(port),
				userNameText.getText());
		if (isValidPassword && serverInfo != null
				&& serverInfo.getLoginedUserInfo() != null
				&& serverInfo.getLoginedUserInfo().isAdmin()) {
			isValidPassword = password.equals(serverInfo.getLoginedUserInfo().getPassword());
		}
		if (!isValidPassword) {
			setErrorMessage(Messages.errPassword);
			addDatabaseButton.setEnabled(false);
			return;
		}
		setErrorMessage(null);
		addDatabaseButton.setEnabled(true);
	}

	/**
	 * 
	 * Verify the data
	 * 
	 */
	public void verify() {
		if (dbNodeList.isEmpty()) {
			setErrorMessage(Messages.errDbList);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else {
			setErrorMessage(null);
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
	}

	/**
	 * 
	 * Get added database node list
	 * 
	 * @return The List<DatabaseNode>
	 */
	public List<DatabaseNode> getDbNodeList() {
		List<DatabaseNode> nodeList = new ArrayList<DatabaseNode>();
		for (int i = 0; i < dbNodeList.size(); i++) {
			DatabaseNode dbNode = (DatabaseNode) dbNodeList.get(i).get("5");
			nodeList.add(dbNode);
		}
		return nodeList;
	}
}
