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
package com.cubrid.cubridmanager.ui.common.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.ui.common.control.ConnectionComposite;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ConnectDatabaseExecutor;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;

/**
 * 
 * The dialog is used to login database
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class LoginQueryEditDialog extends
		CMTitleAreaDialog implements
		ModifyListener {

	public final static int TEST_CONNECT_ID = 3;

	private Combo allHostCombo = null;
	private ConnectionComposite connectionComp;

	private HashMap<String, List<CubridDatabase>> databaseMap;
	private HashMap<String, CubridServer> serverMap;
	private CubridDatabase selectDatabase;
	private String selServerName;
	private String selDatabaseName;

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 */
	public LoginQueryEditDialog(Shell parentShell) {
		super(parentShell);
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

		Group serverInfoGroup = new Group(composite, SWT.NONE);
		{
			serverInfoGroup.setText(Messages.grpAddeHosts);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			serverInfoGroup.setLayoutData(gridData);
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			serverInfoGroup.setLayout(layout);

			Label allHostLabel = new Label(serverInfoGroup, SWT.LEFT);
			allHostLabel.setText(Messages.lblAddedHost);
			allHostLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

			allHostCombo = new Combo(serverInfoGroup, SWT.LEFT | SWT.BORDER
					| SWT.READ_ONLY);
			allHostCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					selServerName = allHostCombo.getText();
					changeSelection();
					valid();
				}
			});
			allHostCombo.setLayoutData(CommonUITool.createGridData(
					GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
		}

		connectionComp = new ConnectionComposite(composite, false, true);
		connectionComp.getDatabaseCombo().addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						selDatabaseName = connectionComp.getDatabaseCombo().getText();
						changeSelection();
						valid();
					}
				});

		setTitle(com.cubrid.cubridmanager.ui.common.Messages.titleNewQueryDialog);
		setMessage(com.cubrid.cubridmanager.ui.common.Messages.msgNewQueryDialog);
		initial();
		return parentComp;
	}

	/**
	 * initialize some values
	 * 
	 */
	private void initial() {
		//load the register CUBRID server and CUBRID database
		List<CubridServer> servers = CMHostNodePersistManager.getInstance().getAllServers();
		serverMap = new HashMap<String, CubridServer>();
		databaseMap = new HashMap<String, List<CubridDatabase>>();
		allHostCombo.removeAll();
		for (CubridServer server : servers) {
			serverMap.put(server.getName(), server);
			allHostCombo.add(server.getName());
			List<ICubridNode> children = server.getChildren();
			for (ICubridNode child : children) {
				if (!NodeType.DATABASE_FOLDER.equals(child.getType())) {
					continue;
				}
				ICubridNode[] dbs = child.getChildren(new NullProgressMonitor());
				if (dbs.length == 0) {
					continue;
				}
				List<CubridDatabase> dbList = new ArrayList<CubridDatabase>();
				for (ICubridNode database : dbs) {
					if (NodeType.DATABASE.equals(database.getType())) {
						CubridDatabase db = (CubridDatabase) database;
						dbList.add(db);
					}
				}
				databaseMap.put(server.getName(), dbList);
			}
		}

		//load the self defined CUBRID server and CUBRID database
		CubridServer selfDefinedServer = DatabaseNavigatorMenu.SELF_DATABASE.getServer();
		if (selfDefinedServer == null) {
			selfDefinedServer = new CubridServer(
					DatabaseNavigatorMenu.SELF_DATABASE_ID,
					DatabaseNavigatorMenu.SELF_DATABASE_SELECTED_LABEL, null,
					null);
			DatabaseNavigatorMenu.SELF_DATABASE.setServer(selfDefinedServer);
		}
		serverMap.put(selfDefinedServer.getName(), selfDefinedServer);
		allHostCombo.add(selfDefinedServer.getName());
		List<CubridDatabase> dbList = new ArrayList<CubridDatabase>();
		dbList.add(DatabaseNavigatorMenu.SELF_DATABASE);
		databaseMap.put(selfDefinedServer.getName(), dbList);

		connectionComp.init(null, this);
		changeSelection();
	}

	/**
	 * @see com.cubrid.cubridmanager.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize()
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		CommonUITool.centerShell(getShell());
		getShell().setText(
				com.cubrid.cubridmanager.ui.common.Messages.titleNewQueryDialog);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, TEST_CONNECT_ID,
				com.cubrid.cubridmanager.ui.host.Messages.btnTestConn, false);
		createButton(parent, IDialogConstants.OK_ID,
				com.cubrid.cubridmanager.ui.host.Messages.btnConnectHost, true);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnCancel, false);
		valid();
	}

	/**
	 * validate the data
	 * 
	 */
	private void valid() {
		setErrorMessage(null);
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
	 * 
	 * Set button status
	 * 
	 * @param isEnabled boolean
	 */
	private void setButtonStatus(boolean isEnabled) {
		if (getButton(TEST_CONNECT_ID) != null) {
			getButton(TEST_CONNECT_ID).setEnabled(isEnabled);
		}
		if (getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 * @param buttonId the id of the button that was pressed (see
	 *        <code>IDialogConstants.*_ID</code> constants)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID || buttonId == TEST_CONNECT_ID) {
			String brokerIp = connectionComp.getBrokerIpText().getText();

			CubridServer cubridServer = DatabaseNavigatorMenu.SELF_DATABASE.getServer();
			ServerInfo serverInfo = new ServerInfo();
			serverInfo.setServerName(DatabaseNavigatorMenu.SELF_DATABASE_ID);
			serverInfo.setHostAddress(brokerIp);
			cubridServer.setServerInfo(serverInfo);

			String databaseName = connectionComp.getDatabaseCombo().getText();
			DatabaseInfo dbInfo = new DatabaseInfo(databaseName,
					cubridServer.getServerInfo());
			dbInfo.setBrokerIP(brokerIp);
			dbInfo.setBrokerPort(connectionComp.getBrokerPortCombo().getText());
			dbInfo.getServerInfo().setJdbcDriverVersion(
					connectionComp.getJdbcCombo().getText());

			String userName = connectionComp.getUserNameText().getText();
			String password = connectionComp.getPasswordText().getText();
			DbUserInfo userInfo = new DbUserInfo();
			userInfo.setDbName(databaseName);
			userInfo.setName(userName);
			userInfo.setNoEncryptPassword(password);
			dbInfo.setAuthLoginedDbUserInfo(userInfo);

			int currentShardId = connectionComp.getCurShardId();
			dbInfo.setCurrentShardId(currentShardId);

			String charset = connectionComp.getCharsetCombo().getText();
			dbInfo.setCharSet(charset);
			
			boolean isShard = connectionComp.getBtnShard().getSelection();
			dbInfo.setShard(isShard);

			boolean sureCharset = CommonUITool.openConfirmBox(
					Messages.bind(com.cubrid.cubridmanager.ui.host.Messages.msgConfirmCharset, charset));			
			if (!sureCharset) {
				connectionComp.getCharsetCombo().setFocus();
				return;
			}
			
			TaskExecutor taskExcutor = new ConnectDatabaseExecutor(dbInfo);
			new ExecTaskWithProgress(taskExcutor).exec();	
			
			if(! taskExcutor.isSuccess()) {
				return;
			}

			if (buttonId == TEST_CONNECT_ID) {
				CommonUITool.openInformationBox(
						com.cubrid.cubridmanager.ui.common.Messages.titleSuccess,
						com.cubrid.cubridmanager.ui.host.Messages.msgTestConnSuccess);
				return;
			}
			dbInfo.setLogined(true);
			dbInfo.setRunningType(DbRunningType.CS);

			DatabaseNavigatorMenu.SELF_DATABASE.setDatabaseInfo(dbInfo);
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event an event containing information about the modify
	 */
	public void modifyText(ModifyEvent event) {
		valid();
	}

	/**
	 * 
	 * Fill in the database combo
	 * 
	 * @param list the database list
	 */
	private void fillDatabaseCombo(List<CubridDatabase> list) {
		selectDatabase = null;
		connectionComp.getDatabaseCombo().removeAll();
		if (list == null || list.isEmpty()) {
			//For Cubrid Manager, if the server host is disconnected or has no database, 'demodb' will be set as default.
			connectionComp.getDatabaseCombo().setText("demodb");
			return;
		}
		CubridDatabase firstDb = null;
		for (CubridDatabase db : list) {
			DatabaseInfo databaseInfo = db.getDatabaseInfo();
			if (databaseInfo == null) {
				continue;
			}
			connectionComp.getDatabaseCombo().add(databaseInfo.getDbName());
			firstDb = firstDb == null ? db : firstDb;
			if (selDatabaseName != null
					&& selDatabaseName.equals(databaseInfo.getDbName())) {
				selectDatabase = db;
			}
		}
		if (selectDatabase == null && firstDb != null) {
			selectDatabase = firstDb;
			selDatabaseName = selectDatabase.getDatabaseInfo() == null ? ""
					: selectDatabase.getDatabaseInfo().getDbName();
		}
		if (selectDatabase == null || null == selectDatabase.getDatabaseInfo()) {
			return;
		}
		connectionComp.getDatabaseCombo().setText(
				selectDatabase.getDatabaseInfo().getDbName());
	}

	/**
	 * 
	 * Fill in the broker port combo
	 * 
	 * @param serverName the server name
	 */
	private void fillBrokerPortCombo(String serverName) {
		CubridServer cubridServer = serverMap.get(serverName);
		BrokerInfos brokerInfos = cubridServer.getServerInfo().getBrokerInfos();
		if (brokerInfos != null && brokerInfos.getBorkerInfoList() != null
				&& brokerInfos.getBorkerInfoList().getBrokerInfoList() != null) {
			List<BrokerInfo> brokerList = brokerInfos.getBorkerInfoList().getBrokerInfoList();
			Combo brokerPortCombo =	connectionComp.getBrokerPortCombo();
			brokerPortCombo.removeAll();
			for (int i = 0; i < brokerList.size(); i++) {
				brokerPortCombo.add(brokerList.get(i).getPort());
			}
			if (!brokerList.isEmpty()) {
				brokerPortCombo.select(0);
			}
		}
	}

	/**
	 * Change the selection
	 * 
	 */
	private void changeSelection() {
		connectionComp.changeData(null);
		allHostCombo.setText(selServerName);
		String serverName = allHostCombo.getText();
		CubridServer cubridServer = serverMap.get(serverName);
		String charset = "";
		if (cubridServer != null && cubridServer.getHostAddress() != null) {
			connectionComp.getBrokerIpText().setText(
					cubridServer.getHostAddress());
		}
		fillDatabaseCombo(databaseMap.get(serverName));

		boolean isAdminLogin = (cubridServer == null
				|| cubridServer.getServerInfo() == null || cubridServer.getServerInfo().getLoginedUserInfo() == null) ? false
				: cubridServer.getServerInfo().getLoginedUserInfo().isAdmin();
		if (isAdminLogin) {
			fillBrokerPortCombo(serverName);
		}

		DatabaseInfo dbInfo = selectDatabase == null ? null
				: selectDatabase.getDatabaseInfo();
		if (dbInfo != null) {
			connectionComp.getBrokerIpText().setText(
					QueryOptions.getBrokerIp(dbInfo));
			if (connectionComp.getBrokerPortCombo().getItemCount() > 0) {
				connectionComp.getBrokerPortCombo().setText(
						QueryOptions.getBrokerPort(dbInfo));
			} else {
				connectionComp.getBrokerPortCombo().add(
						QueryOptions.getBrokerPort(dbInfo));
				connectionComp.getBrokerPortCombo().select(0);
			}
			DbUserInfo dbUserInfo = dbInfo.getAuthLoginedDbUserInfo();
			if (dbUserInfo != null) {
				connectionComp.getUserNameText().setText(
						dbUserInfo.getName() == null ? ""
								: dbUserInfo.getName());
				connectionComp.getPasswordText().setText(
						dbUserInfo.getNoEncryptPassword() == null ? ""
								: dbUserInfo.getNoEncryptPassword());
			}
			charset = dbInfo.getCharSet();
		}

		String jdbcDriverVersion = cubridServer == null ? null
				: cubridServer.getJdbcDriverVersion();
		if (jdbcDriverVersion == null
				|| "".equals(jdbcDriverVersion)
				|| ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION.equals(jdbcDriverVersion)) {
			if (connectionComp.getJdbcCombo().getItemCount() > 0) {
				connectionComp.getJdbcCombo().select(0);
			}
		} else {
			connectionComp.getJdbcCombo().setText(
					cubridServer.getJdbcDriverVersion());
		}
		connectionComp.getCharsetCombo().setItems(
				QueryOptions.getAllCharset(charset));
		if (charset != null && charset.trim().length() > 0) {
			connectionComp.getCharsetCombo().setText(charset);
		} else {
			connectionComp.getCharsetCombo().select(0);
		}

		if (dbInfo != null) {
			connectionComp.getBtnShard().setSelection(dbInfo.isShard());
		}
	}

	public String getSelServerName() {
		return selServerName;
	}

	public void setSelServerName(String selServerName) {
		this.selServerName = selServerName;
	}

	public String getSelDatabaseName() {
		return selDatabaseName;
	}

	public void setSelDatabaseName(String selDatabaseName) {
		this.selDatabaseName = selDatabaseName;
	}

}
