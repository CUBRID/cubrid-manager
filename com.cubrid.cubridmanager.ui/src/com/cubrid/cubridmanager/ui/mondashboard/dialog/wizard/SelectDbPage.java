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
package com.cubrid.cubridmanager.ui.mondashboard.dialog.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.HADatabaseStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.model.HostStatusType;
import com.cubrid.cubridmanager.core.mondashboard.task.GetHeartbeatNodeInfoTask;
import com.cubrid.cubridmanager.core.mondashboard.task.VerifyDbUserPasswordTask;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.dialog.AddHADatabaseDialog;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;

/**
 * 
 * Select the database wizard page
 * 
 * @author pangqiren
 * @version 1.0 - 2010-6-2 created by pangqiren
 */
public class SelectDbPage extends
		WizardPage implements
		ModifyListener,
		IPageChangedListener {

	public final static String PAGENAME = "AddHostAndDbWizard/SelectDbPage";
	private Button deleteDbButton;
	private TableViewer dbTableViewer;
	private Table dbTable;
	private List<Map<String, Object>> dbNodeList = new ArrayList<Map<String, Object>>();
	private Combo dbNameCombo;
	private Button addButton;
	private HostNode hostNode;
	private ServerInfo serverInfo;
	private Button haModeBtn;
	private Button addHANodeButton;
	private Text dbaPasswordText;
	private ServerType serverType;
	private Text nickNameText;

	/**
	 * The constructor
	 */
	public SelectDbPage() {
		super(PAGENAME);
	}

	/**
	 * Create the control for this page
	 * 
	 * @param parent Composite
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);

		Group dbInfoGroup = new Group(composite, SWT.NONE);
		dbInfoGroup.setText(Messages.grpSelectDb);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		dbInfoGroup.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 2;
		dbInfoGroup.setLayout(layout);

		Label dbNameLabel = new Label(dbInfoGroup, SWT.LEFT);
		dbNameLabel.setText(Messages.lblDbName);
		dbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dbNameCombo = new Combo(dbInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		dbNameCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
		dbNameCombo.addModifyListener(this);

		Label nickNameLable = new Label(dbInfoGroup, SWT.LEFT);
		nickNameLable.setText(Messages.lblNickName);
		nickNameLable.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		nickNameText = new Text(dbInfoGroup, SWT.LEFT | SWT.BORDER);
		nickNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label dbaPasswordLabel = new Label(dbInfoGroup, SWT.LEFT);
		dbaPasswordLabel.setText(Messages.lblDbaPassword);
		dbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dbaPasswordText = new Text(dbInfoGroup, SWT.LEFT | SWT.BORDER
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

		addButton = new Button(btnComposite, SWT.NONE);
		addButton.setText(Messages.btnAddDb);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String dbName = dbNameCombo.getText();
				String dbaPassword = dbaPasswordText.getText();
				String nickName = nickNameText.getText();
				if (nickName.trim().length() == 0) {
					nickName = dbName;
				}
				//check whether this database already be added
				if (isExist(dbName, hostNode)) {
					CommonUITool.openErrorBox(Messages.bind(Messages.errDbExist,
							dbName));
					return;
				}
				DatabaseInfo dbInfo = serverInfo.getLoginedUserInfo().getDatabaseInfo(
						dbName);
				TaskExecutor executor = new CommonTaskExec(null);
				VerifyDbUserPasswordTask verifyDbUserPasswordTask = null;
				if (dbInfo.isLogined()
						&& dbInfo.getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(
								"dba")) {
					String password = dbInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword();
					if (!dbaPassword.equals(password)) {
						CommonUITool.openErrorBox(Messages.errDbaPassowrd);
						return;
					}
				} else {
					verifyDbUserPasswordTask = new VerifyDbUserPasswordTask(
							serverInfo);
					verifyDbUserPasswordTask.setDbName(dbName);
					verifyDbUserPasswordTask.setDbUser("dba");
					verifyDbUserPasswordTask.setDbPassword(dbaPassword);
					executor.addTask(verifyDbUserPasswordTask);
				}

				GetHeartbeatNodeInfoTask getHeartbeatNodeInfoTask = new GetHeartbeatNodeInfoTask(
						serverInfo);
				getHeartbeatNodeInfoTask.setAllDb(false);
				List<String> dbList = new ArrayList<String>();
				dbList.add(dbName);
				getHeartbeatNodeInfoTask.setDbList(dbList);

				executor.addTask(getHeartbeatNodeInfoTask);

				new ExecTaskWithProgress(executor).exec(true, true);
				if (!executor.isSuccess()) {
					if (verifyDbUserPasswordTask != null
							&& !verifyDbUserPasswordTask.isSuccess()) {
						dbaPasswordText.selectAll();
						dbaPasswordText.setFocus();
					}
					return;
				}

				List<HAHostStatusInfo> haHostStatusInfoList = getHeartbeatNodeInfoTask.getHAHostStatusList();
				HADatabaseStatusInfo haDbStatusInfo = getHeartbeatNodeInfoTask.getDatabaseStatusInfo(dbName);
				HAHostStatusInfo haHostStatusInfo = getHeartbeatNodeInfoTask.getHostStatusInfo(serverInfo.getHostAddress());

				//if this database is HA Mode,will get it's active database or standby database
				List<DatabaseNode> haDbNodeList = getHADatabaseList(
						haDbStatusInfo, haHostStatusInfoList, true);

				if (haHostStatusInfo == null) {
					haHostStatusInfo = HAUtil.getHAHostStatusInfo(serverInfo);
					if (haDbStatusInfo != null) {
						haHostStatusInfo.addHADatabaseStatus(haDbStatusInfo);
					}
				}

				if (haDbStatusInfo == null) {
					haDbStatusInfo = HAUtil.getHADatabaseStatusInfo(dbName,
							haHostStatusInfo, serverInfo);
				}

				DatabaseNode dbNode = new DatabaseNode();
				hostNode.setHostStatusInfo(haHostStatusInfo);
				dbNode.setParent(hostNode);
				dbNode.setDbName(dbName);
				dbNode.setDbUser("dba");
				dbNode.setDbPassword(dbaPassword);
				dbNode.setName(nickName);
				dbNode.setHaDatabaseStatus(haDbStatusInfo);
				dbNode.setConnected(true);
				if (haDbNodeList == null) {
					haDbNodeList = new ArrayList<DatabaseNode>();
				}
				if (dbNode.getDbStatusType() == DBStatusType.ACTIVE
						|| dbNode.getDbStatusType() == DBStatusType.TO_BE_ACTIVE) {
					haDbNodeList.add(0, dbNode);
				} else {
					haDbNodeList.add(dbNode);
				}
				addDbNodeToTable(haDbNodeList, haHostStatusInfoList);
			}
		});

		haModeBtn = new Button(dbInfoGroup, SWT.LEFT | SWT.CHECK);
		haModeBtn.setText(Messages.btnHAMode);
		haModeBtn.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
		haModeBtn.setEnabled(false);

		createTable(composite);
		dbNameCombo.setFocus();
		setTitle(Messages.titleSelectDbPage);
		setMessage(Messages.msgSelectDbPage);
		setControl(composite);
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

		final String[] columnNameArr = new String[]{Messages.colNickName,
				Messages.colDbName, Messages.colDbStatus, Messages.colIP,
				Messages.colPort, Messages.colServerStatus };

		dbTableViewer = CommonUITool.createCommonTableViewer(parent, null,
				columnNameArr, CommonUITool.createGridData(GridData.FILL_BOTH, 1,
						1, -1, 500));
		dbTable = dbTableViewer.getTable();
		for (int i = 0; i < dbTable.getColumnCount(); i++) {
			dbTable.getColumn(i).pack();
		}

		dbTable.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent event) {
				deleteDbButton.setEnabled(dbTable.getSelectionCount() > 0);
				addHANodeButton.setEnabled(false);
				StructuredSelection selection = (StructuredSelection) dbTableViewer.getSelection();
				if (selection == null || selection.isEmpty()
						|| selection.size() > 1) {
					return;
				}
				Map<String, Object> map = (Map<String, Object>) selection.getFirstElement();
				DatabaseNode dbNode = (DatabaseNode) map.get("7");
				DBStatusType type = dbNode.getHaDatabaseStatus().getStatusType();
				if (type == DBStatusType.ACTIVE
						|| type == DBStatusType.TO_BE_ACTIVE
						|| type == DBStatusType.MAINTENANCE
						|| type == DBStatusType.STANDBY
						|| type == DBStatusType.TO_BE_STANDBY) {
					addHANodeButton.setEnabled(true);
				}
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

		addHANodeButton = new Button(composite, SWT.NONE);
		addHANodeButton.setText(Messages.btnAddHADb);
		addHANodeButton.setToolTipText(Messages.tipBtnAddHaDb);
		addHANodeButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent event) {
				StructuredSelection selection = (StructuredSelection) dbTableViewer.getSelection();
				if (selection == null || selection.isEmpty()
						|| selection.size() > 1) {
					return;
				}
				Map<String, Object> map = (Map<String, Object>) selection.getFirstElement();
				DatabaseNode dbNode = (DatabaseNode) map.get("7");
				List<HAHostStatusInfo> haHostStatusInfoList = (List<HAHostStatusInfo>) map.get("8");
				List<DatabaseNode> haDbNodeList = getHADatabaseList(
						dbNode.getHaDatabaseStatus(), haHostStatusInfoList,
						false);
				if (haDbNodeList == null) {
					haDbNodeList = new ArrayList<DatabaseNode>();
				}
				if (dbNode.getDbStatusType() == DBStatusType.ACTIVE
						|| dbNode.getDbStatusType() == DBStatusType.TO_BE_ACTIVE) {
					haDbNodeList.add(0, dbNode);
				} else {
					haDbNodeList.add(dbNode);
				}
				addDbNodeToTable(haDbNodeList, haHostStatusInfoList);
				addHANodeButton.setEnabled(dbTable.getSelectionCount() > 0);
			}
		});
		addHANodeButton.setEnabled(false);

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
			}
		});
		deleteDbButton.setEnabled(false);
	}

	/**
	 * 
	 * Add DatabaseNode to table
	 * 
	 * @param haDbNodeList The List<DatabaseNode>
	 * @param haHostStatusInfoList The List<HAHostStatusInfo>
	 */
	private void addDbNodeToTable(List<DatabaseNode> haDbNodeList,
			List<HAHostStatusInfo> haHostStatusInfoList) {
		for (int i = 0; haDbNodeList != null && i < haDbNodeList.size(); i++) {
			DatabaseNode dbNode = haDbNodeList.get(i);
			if (isDbNodeExist(dbNode)) {
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("0", dbNode.getName());
			map.put("1", dbNode.getDbName());
			map.put("2", DBStatusType.getShowText(dbNode.getDbStatusType()));
			map.put("3", dbNode.getParent().getIp());
			map.put("4", dbNode.getParent().getPort());
			map.put(
					"5",
					HostStatusType.getShowText(dbNode.getParent().getHostStatusInfo().getStatusType()));
			map.put("6", dbNode.getParent());
			map.put("7", dbNode);
			map.put("8", haHostStatusInfoList);
			dbNodeList.add(map);
		}
		dbTableViewer.refresh();
		for (int i = 0; i < dbTable.getColumnCount(); i++) {
			dbTable.getColumn(i).pack();
		}
	}

	/**
	 * 
	 * Check whether this DatabaseNode already exist
	 * 
	 * @param dbNode The DatabaseNode
	 * @return boolean
	 */
	private boolean isDbNodeExist(DatabaseNode dbNode) {
		HostNode hostNode = dbNode.getParent();
		for (int i = 0; i < dbNodeList.size(); i++) {
			Map<String, Object> map = dbNodeList.get(i);
			HostNode hostNode1 = (HostNode) map.get("6");
			DatabaseNode dbNode1 = (DatabaseNode) map.get("7");
			if (com.cubrid.common.core.util.StringUtil.isIpEqual(
					hostNode1.getIp(), hostNode.getIp())
					&& hostNode.getPort().equals(hostNode1.getPort())
					&& hostNode.getUserName().equals(hostNode1.getUserName())
					&& dbNode.getDbName().equals(dbNode1.getDbName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * Get databases in HA mode
	 * 
	 * @param haDbStatusInfo The HADatabaseStatusInfo
	 * @param haHostStatusInfoList The List<HAHostStatusInfo>
	 * @param isShowMsg boolean
	 * @return List<DatabaseNode>
	 */
	private List<DatabaseNode> getHADatabaseList(
			HADatabaseStatusInfo haDbStatusInfo,
			List<HAHostStatusInfo> haHostStatusInfoList, boolean isShowMsg) {
		boolean isConfirm = false;
		if (haDbStatusInfo != null) {
			DBStatusType type = haDbStatusInfo.getStatusType();
			String msg = null;
			if (type == DBStatusType.ACTIVE
					|| type == DBStatusType.TO_BE_ACTIVE) {
				msg = Messages.bind(Messages.confirmMsgAddStandby,
						type.getText());
				isConfirm = true;
			} else if (type == DBStatusType.STANDBY
					|| type == DBStatusType.TO_BE_STANDBY
					|| type == DBStatusType.MAINTENANCE) {
				msg = Messages.bind(Messages.confirmMsgAddActive,
						type.getText());
				isConfirm = true;
			}
			if (isShowMsg) {
				isConfirm = CommonUITool.openConfirmBox(msg);
			}
		}
		if (isConfirm) {
			AddHADatabaseDialog dialog = new AddHADatabaseDialog(getShell(),
					haDbStatusInfo, haHostStatusInfoList);
			int ret = dialog.open();
			if (ret == IDialogConstants.OK_ID) {
				return dialog.getDbNodeList();
			}
		}
		return null;
	}

	/**
	 * 
	 * Check whether the database already exist in database list table
	 * 
	 * @param dbName The String
	 * @param hostNode The HostNode
	 * @return The boolean
	 */
	private boolean isExist(String dbName, HostNode hostNode) {
		for (int i = 0; i < dbNodeList.size(); i++) {
			Map<String, Object> map = dbNodeList.get(i);
			String name = (String) map.get("1");
			String ip = (String) map.get("3");
			String port = (String) map.get("4");
			if (dbName.equals(name) && hostNode.getIp().equals(ip)
					&& hostNode.getPort().equals(port)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event ModifyEvent
	 */
	public void modifyText(ModifyEvent event) {
		String dbName = dbNameCombo.getText();
		if (dbName.trim().length() == 0) {
			addButton.setEnabled(false);
		} else {
			dbaPasswordText.setText("");
			nickNameText.setText(dbName);
			addButton.setEnabled(true);
		}
		changeHAModeBtnStatus();
	}

	/**
	 * @see org.eclipse.jface.dialogs.IPageChangedListener#pageChanged(org.eclipse.jface.dialogs.PageChangedEvent)
	 * @param event PageChangedEvent
	 */
	/**
	 * @see org.eclipse.jface.dialogs.IPageChangedListener#pageChanged(org.eclipse.jface.dialogs.PageChangedEvent)
	 * @param event PageChangedEvent
	 */
	public void pageChanged(PageChangedEvent event) {
		IWizardPage page = (IWizardPage) event.getSelectedPage();
		if (page.getName().equals(PAGENAME)) {
			AddHostAndDbWizard wizard = ((AddHostAndDbWizard) getWizard());
			SetHostInfoPage setHostInfoPage = (SetHostInfoPage) getWizard().getPage(
					SetHostInfoPage.PAGENAME);
			if (setHostInfoPage == null) {
				hostNode = wizard.getSelectedHostNode();
			} else {
				hostNode = setHostInfoPage.getHostNode();
			}
			serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
					hostNode.getIp(), Integer.parseInt(hostNode.getPort()),
					hostNode.getUserName());
			List<DatabaseInfo> dbInfoList = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
			dbNameCombo.removeAll();
			for (int i = 0; i < dbInfoList.size(); i++) {
				dbNameCombo.add(dbInfoList.get(i).getDbName());
			}
			if (dbNameCombo.getItemCount() > 0) {
				dbNameCombo.select(0);
				nickNameText.setText(dbNameCombo.getText());
				addButton.setEnabled(true);
			} else {
				addButton.setEnabled(false);
			}
			changeHAModeBtnStatus();
			dbNameCombo.setFocus();
		}
	}

	/**
	 * 
	 * Change HA Mode button status
	 * 
	 */
	private void changeHAModeBtnStatus() {
		String dbName = dbNameCombo.getText();
		haModeBtn.setSelection(serverInfo.isHAMode(dbName));
	}

	public List<Map<String, Object>> getDbNodeList() {
		return dbNodeList;
	}

	public void setDbNodeList(List<Map<String, Object>> dbNodeList) {
		this.dbNodeList = dbNodeList;
	}

	public HostNode getHostNode() {
		return hostNode;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

	/**
	 * Get next page
	 * 
	 * @return The IWizardPage
	 */
	public IWizardPage getNextPage() {
		AddHostAndDbWizard wizard = ((AddHostAndDbWizard) getWizard());
		int type = wizard.getAddedType();
		if ((type == 0 || type == 2) && serverType == ServerType.BOTH) {
			return wizard.getSelectBrokerPage();
		} else {
			return null;
		}
	}
}
