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
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.mondashboard.model.HAHostStatusInfo;
import com.cubrid.cubridmanager.core.mondashboard.task.GetHeartbeatNodeInfoTask;
import com.cubrid.cubridmanager.ui.host.dialog.ConnectHostExecutor;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.HAUtil;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;

/**
 * 
 * Set host information wizard page
 * 
 * @author pangqiren
 * @version 1.0 - 2010-6-2 created by pangqiren
 */
public class SetHostInfoPage extends
		WizardPage implements
		ModifyListener {

	public final static String PAGENAME = "AddHostAndDbWizard/SetHostInfoPage";
	private Text ipText = null;
	private Text portText = null;
	private Text userNameText = null;
	private Text passwordText = null;
	private Button connectHostButton;
	private final HostNode selectedHostNode;
	private GetHeartbeatNodeInfoTask heartbeatNodeInfoTask;
	private final List<HostNode> addedHostNodeList;
	private final List<HostNode> allHostNodeList;
	private Text nickNameText;

	/**
	 * The constructor
	 */
	public SetHostInfoPage(HostNode hostNode, List<HostNode> hostNodeList,
			List<HostNode> allHostNodeList) {
		super(PAGENAME);
		selectedHostNode = hostNode;
		this.addedHostNodeList = hostNodeList;
		this.allHostNodeList = allHostNodeList;
		setPageComplete(false);
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

		Group cmServerInfoGroup = new Group(composite, SWT.NONE);
		cmServerInfoGroup.setText(Messages.grpHostInfo);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		cmServerInfoGroup.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 2;
		cmServerInfoGroup.setLayout(layout);

		Label nickNameLable = new Label(cmServerInfoGroup, SWT.LEFT);
		nickNameLable.setText(Messages.lblNickName);
		nickNameLable.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		nickNameText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		nickNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label ipLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		ipLabel.setText(Messages.lblIPAddress);
		ipLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		ipText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		ipText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		ipText.addModifyListener(this);

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
		portText.addModifyListener(this);

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
		passwordText.addModifyListener(this);
		passwordText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				passwordText.selectAll();
				passwordText.setFocus();
			}
		});

		Composite btnComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 5;
		btnComposite.setLayout(rowLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = GridData.END;
		btnComposite.setLayoutData(gridData);

		connectHostButton = new Button(btnComposite, SWT.NONE);
		connectHostButton.setText(Messages.btnConnect);
		connectHostButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				String address = ipText.getText();
				String port = portText.getText();
				String userName = userNameText.getText();
				String password = passwordText.getText();
				String nickName = nickNameText.getText();
				if (nickName.trim().length() == 0) {
					nickNameText.setText(address + ":" + port);
				}
				ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
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
				heartbeatNodeInfoTask = new GetHeartbeatNodeInfoTask(serverInfo);
				heartbeatNodeInfoTask.setAllDb(false);
				heartbeatNodeInfoTask.setDbList(new ArrayList<String>());

				TaskExecutor executor = null;
				if (serverInfo.isConnected()) {
					if (CompatibleUtil.isSupportHA(serverInfo)) {
						executor = new CommonTaskExec(null);
						executor.addTask(heartbeatNodeInfoTask);
					}
				} else {
					executor = new ConnectHostExecutor(getShell(), serverInfo, true);
					executor.addTask(heartbeatNodeInfoTask);
				}
				if (executor != null) {
					new ExecTaskWithProgress(executor).exec(true, true);
				}
				changeBtnStatus();
			}
		});
		connectHostButton.setEnabled(false);
		init();
		nickNameText.setFocus();
		setTitle(Messages.titileHostInfoPage);
		setMessage(Messages.msgHostInfoPage);
		setControl(composite);
	}

	/**
	 * 
	 * Initial the node
	 * 
	 */
	private void init() {
		if (selectedHostNode != null) {
			String nickName = selectedHostNode.getName();
			String ip = selectedHostNode.getIp();
			String port = selectedHostNode.getPort();
			String userName = selectedHostNode.getUserName();
			String password = selectedHostNode.getPassword();
			if (nickName != null) {
				nickNameText.setText(nickName);
			}
			if (ip != null) {
				ipText.setText(ip);
			}
			if (port != null) {
				portText.setText(port);
			}
			if (userName != null) {
				userNameText.setText(userName);
			}
			if (password != null) {
				passwordText.setText(password);
			}
			changeBtnStatus();
		}
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event ModifyEvent
	 */
	public void modifyText(ModifyEvent event) {
		setPageComplete(false);
		String ip = ipText.getText();
		boolean isValidIP = ip.trim().length() > 0;
		if (!isValidIP) {
			setErrorMessage(Messages.errIPAddress);
			connectHostButton.setEnabled(false);
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
			connectHostButton.setEnabled(false);
			return;
		}

		String password = passwordText.getText();
		boolean isValidPassword = password.trim().length() >= 4
				&& password.indexOf(" ") < 0;

		ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
				ipText.getText(), Integer.parseInt(port),
				userNameText.getText());
		if (isValidPassword && serverInfo != null
				&& serverInfo.getLoginedUserInfo() != null
				&& serverInfo.getLoginedUserInfo().isAdmin()) {
			isValidPassword = password.equals(serverInfo.getLoginedUserInfo().getPassword());
		}
		if (!isValidPassword) {
			setErrorMessage(Messages.errPassword);
			connectHostButton.setEnabled(false);
			return;
		}
		setErrorMessage(null);
		connectHostButton.setEnabled(true);
		changeBtnStatus();
	}

	/**
	 * 
	 * Change page complete status
	 * 
	 */
	private void changeBtnStatus() {
		String ip = ipText.getText();
		String port = portText.getText();
		String userName = userNameText.getText();
		if (ip != null && port != null && ValidateUtil.isNumber(port)
				&& userName != null) {
			ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(ip,
					Integer.parseInt(port), userName);
			if (serverInfo != null && serverInfo.isConnected()) {
				if (CompatibleUtil.isSupportHA(serverInfo)) {
					setPageComplete(getHostStatusInfo(ip, port) != null
							|| (heartbeatNodeInfoTask != null && heartbeatNodeInfoTask.getServerInfo() == serverInfo));
				} else {
					setErrorMessage(Messages.errNotSupportServer);
				}
				AddHostAndDbWizard wizard = ((AddHostAndDbWizard) getWizard());
				if (wizard.getSelectDbPage() != null) {
					wizard.getSelectDbPage().setServerType(
							serverInfo.getServerType());
				}
				if (wizard.getSelectBrokerPage() != null) {
					wizard.getSelectBrokerPage().setServerType(
							serverInfo.getServerType());
				}
			}
		}
	}

	/**
	 * 
	 * Get Host Node
	 * 
	 * @return The HostNode
	 */
	public HostNode getHostNode() {
		HostNode hostNode = new HostNode();
		String nickName = nickNameText.getText();
		if (nickName.trim().length() == 0) {
			nickName = ipText.getText() + ":" + portText.getText();
		}
		hostNode.setName(nickName);
		hostNode.setIp(ipText.getText());
		hostNode.setPort(portText.getText());
		hostNode.setUserName(userNameText.getText());
		hostNode.setPassword(passwordText.getText());
		hostNode.setConnected(true);
		HAHostStatusInfo hostStatusInfo = null;
		if (heartbeatNodeInfoTask != null) {
			hostStatusInfo = heartbeatNodeInfoTask.getHostStatusInfo(ipText.getText());
		}
		if (hostStatusInfo == null) {
			hostStatusInfo = getHostStatusInfo(ipText.getText(),
					portText.getText());
		}
		if (hostStatusInfo == null) {
			ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
					ipText.getText(), Integer.parseInt(portText.getText()),
					userNameText.getText());
			hostStatusInfo = HAUtil.getHAHostStatusInfo(serverInfo);
		}
		hostNode.setHostStatusInfo(hostStatusInfo);
		return hostNode;
	}

	/**
	 * 
	 * Get host status information
	 * 
	 * @param ip The String
	 * @param port The String
	 * @return The HostNode
	 */
	private HAHostStatusInfo getHostStatusInfo(String ip, String port) {
		for (HostNode node : addedHostNodeList) {
			if (ip.equals(node.getIp()) && port.equals(node.getPort())) {
				return node.getHostStatusInfo();
			}
		}
		for (HostNode node : allHostNodeList) {
			if (ip.equals(node.getIp()) && port.equals(node.getPort())) {
				return node.getHostStatusInfo();
			}
		}
		return null;
	}

	/**
	 * 
	 * Check whether can finish. if the server type is broker, can finish.
	 * 
	 * @return The boolean
	 */
	public boolean isCanFinished() {
		if (!ValidateUtil.isNumber(portText.getText())) {
			return false;
		}
		ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
				ipText.getText(), Integer.parseInt(portText.getText()),
				userNameText.getText());
		if (serverInfo == null) {
			return false;
		}
		return true;
	}

	/**
	 * Get next page
	 * 
	 * @return The IWizardPage
	 */
	public IWizardPage getNextPage() {
		if (!isCanFinished()) {
			return null;
		}
		AddHostAndDbWizard wizard = ((AddHostAndDbWizard) getWizard());
		int type = wizard.getAddedType();
		ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
				ipText.getText(), Integer.parseInt(portText.getText()),
				userNameText.getText());
		if ((type == 0 || type == 2)
				&& serverInfo.getServerType() == ServerType.BROKER) {
			return wizard.getSelectBrokerPage();
		} else {
			return (type == 0 || type == 1) ? wizard.getSelectDbPage() : null;
		}
	}
}
