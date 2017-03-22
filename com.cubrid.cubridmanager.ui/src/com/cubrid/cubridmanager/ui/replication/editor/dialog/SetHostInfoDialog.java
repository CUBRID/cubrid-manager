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
package com.cubrid.cubridmanager.ui.replication.editor.dialog;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.common.task.GetCMConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetEnvInfoTask;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.model.ContainerNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Diagram;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * Set host information dialog
 * 
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class SetHostInfoDialog extends
		CMTitleAreaDialog implements
		ModifyListener {

	private Text ipText = null;
	private Text portText = null;
	private Text userNameText = null;
	private Text passwordText = null;
	private HostNode hostInfo = null;
	private boolean isEditable = true;

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 */
	public SetHostInfoDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create dialog area
	 * 
	 * @param parent the parent composite
	 * @return the composite
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		Group cmServerInfoGroup = new Group(composite, SWT.NONE);
		cmServerInfoGroup.setText(Messages.grpHostInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		cmServerInfoGroup.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 2;
		cmServerInfoGroup.setLayout(layout);

		Label ipLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		ipLabel.setText(Messages.lblIpAddress);
		ipLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		ipText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		ipText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label portNameLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		portNameLabel.setText(Messages.lblPort);
		portNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		portText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
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
		initialize();
		setTitle(Messages.titleSetHostInfoDialog);
		setMessage(Messages.msgSetHostInfoDialog);
		return parentComp;
	}

	/**
	 * initialize some values
	 */
	private void initialize() {
		if (hostInfo != null) {
			String ip = hostInfo.getIp() == null ? "" : hostInfo.getIp();
			String port = hostInfo.getPort() == null ? "8001"
					: hostInfo.getPort();
			ipText.setText(ip);
			portText.setText(port);
			userNameText.setText(hostInfo.getUserName() == null ? "admin"
					: hostInfo.getUserName());
			passwordText.setText(hostInfo.getPassword() == null ? ""
					: hostInfo.getPassword());
		}
		userNameText.setEnabled(false);
		portText.addModifyListener(this);
		passwordText.addModifyListener(this);
		if (isEditable) {
			ipText.addModifyListener(this);
		} else {
			ipText.setEnabled(false);
		}
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setSize(400, 300);
		CommonUITool.centerShell(getShell());
		getShell().setText(Messages.titleSetHostInfoDialog);
	}

	/**
	 * Create buttons for button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnOK, true);
		if (!hostInfo.isValid()) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When press button,call it
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			connect(buttonId);
		} else {
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * connect the host
	 * 
	 * @param buttonId int
	 */
	private void connect(final int buttonId) {
		final String ip = ipText.getText();
		final String port = portText.getText();
		final String userName = userNameText.getText();
		final String password = passwordText.getText();
		boolean isConnected = ServerManager.getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		if (isConnected && hostInfo != null) {
			ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(ip,
					Integer.parseInt(port), userName);
			if (!serverInfo.getLoginedUserInfo().isAdmin()) {
				CommonUITool.openErrorBox(Messages.bind(Messages.errInvalidUser,
						ip));
				return;
			}
			hostInfo.setIp(ip);
			hostInfo.setPort(port);
			hostInfo.setUserName(userName);
			hostInfo.setPassword(password);
			hostInfo.setName(ip + ":" + port);

			List<DatabaseInfo> databaseInfoList = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
			hostInfo.setDatabaseInfoList(databaseInfoList);
			hostInfo.setDbPath(serverInfo.getEnvInfo().getDatabaseDir());
			hostInfo.setOsInfoType(serverInfo.getServerOsInfo());
			setReturnCode(buttonId);
			close();
			return;
		}
		if (!isConnected) {
			final ServerInfo serverInfo = new ServerInfo();
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);
			TaskExecutor taskExcutor = new ConnectHostTaskExecutor(serverInfo,
					buttonId);

			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskExcutor.addTask(monitoringTask);
			GetEnvInfoTask getEnvInfoTask = new GetEnvInfoTask(serverInfo);
			taskExcutor.addTask(getEnvInfoTask);
			GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(
					serverInfo);
			taskExcutor.addTask(getCMConfParameterTask);
			GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
					serverInfo);
			taskExcutor.addTask(getDatabaseListTask);
			new ExecTaskWithProgress(taskExcutor).exec(true, true);
		}
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event ModifyEvent
	 */
	public void modifyText(ModifyEvent event) {
		String ip = ipText.getText();
		String port = portText.getText();
		String userName = userNameText.getText();
		String password = passwordText.getText();
		boolean isValidIp = ip.indexOf(" ") < 0 && ip.trim().length() > 0;
		boolean isValidPort = ValidateUtil.isNumber(port);
		if (isValidPort) {
			int portVal = Integer.parseInt(port);
			if (portVal < 1024 || portVal > 65535) {
				isValidPort = false;
			}
		}
		boolean isHostExist = false;
		if (isValidIp && isValidPort) {
			isHostExist = checkHostExist(ip, port);
		}
		boolean isValidUserName = userName.indexOf(" ") < 0
				&& "admin".equals(userName);
		boolean isValidPassword = password.trim().length() >= 4
				&& password.indexOf(" ") < 0;
		if (!isValidIp) {
			setErrorMessage(Messages.errIpAddress);
			setEnabled(false);
			return;
		}
		if (!isValidPort) {
			setErrorMessage(Messages.errPort);
			setEnabled(false);
			return;
		}
		if (isHostExist) {
			setErrorMessage(Messages.errHostExist);
			setEnabled(false);
			return;
		}
		if (!isValidUserName) {
			setErrorMessage(Messages.errUserName);
			setEnabled(false);
			return;
		}
		if (!isValidPassword) {
			setErrorMessage(Messages.errInvalidPassword);
			setEnabled(false);
			return;
		}
		setErrorMessage(null);
		boolean isEnabled = isValidIp && isValidPort && isValidUserName
				&& !isHostExist && isValidPassword;
		if (isEnabled) {
			isEnabled = isSupportReplication(ip, port, userName, password);
		}
		setEnabled(isEnabled);
	}

	/**
	 * 
	 * Check whether support replication according to filled host information
	 * 
	 * @param ip the ip
	 * @param port the port
	 * @param userName the string
	 * @param password the admin password
	 * @return <code>true</code> if supported;<code>false</code>otherwise
	 */
	private boolean isSupportReplication(String ip, String port,
			String userName, String password) {
		boolean isEnabled = true;
		boolean isValidPassword;
		boolean isConnected = ServerManager.getInstance().getInstance().isConnected(ip,
				Integer.parseInt(port), userName);
		if (isConnected) {
			ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(ip,
					Integer.parseInt(port), userName);
			isValidPassword = password.equals(serverInfo.getLoginedUserInfo().getPassword());
			boolean isAdmin = serverInfo.getLoginedUserInfo().isAdmin();
			if (isAdmin && isValidPassword) {
				int ret = serverInfo.isSupportReplication();
				if (ret == 1) {
					setErrorMessage(Messages.errInvalidPlatform);
					isEnabled = false;
				} else if (ret == 2) {
					setErrorMessage(Messages.errInvalidServerType);
					isEnabled = false;
				} else if (ret == 3) {
					setErrorMessage(Messages.errValidVersion);
					isEnabled = false;
				}
			}
			if (!isAdmin) {
				setErrorMessage(Messages.bind(Messages.errInvalidUser, ip));
				isEnabled = false;
			}
			if (!isValidPassword) {
				setErrorMessage(Messages.errInvalidPassword);
				isEnabled = false;
			}
		}
		return isEnabled;
	}

	/**
	 * Check whether the host exist
	 * 
	 * @param ip the ip address
	 * @param port the port
	 * @return <code>true</code> if exist;<code>false</code>otherwise
	 */
	private boolean checkHostExist(String ip, String port) {
		boolean isHostExist = false;
		Diagram diagram = (Diagram) hostInfo.getParent();
		List<ContainerNode> nodeList = diagram.getChildNodeList();
		for (int i = 0; nodeList != null && i < nodeList.size(); i++) {
			ContainerNode node = nodeList.get(i);
			if (node instanceof HostNode) {
				HostNode host = (HostNode) node;
				String name = host.getName();
				if (name.equals(ip + ":" + port) && !host.equals(hostInfo)) {
					isHostExist = true;
					break;
				}
			}
		}
		return isHostExist;
	}

	/**
	 * set button to enable
	 * 
	 * @param isEnabled boolean
	 */
	private void setEnabled(boolean isEnabled) {
		if (getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
		}
	}

	public HostNode getHostInfo() {
		return hostInfo;
	}

	public void setHostInfo(HostNode hostInfo) {
		this.hostInfo = hostInfo;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	/**
	 * Connect the host task executor
	 * 
	 * @author pangqiren
	 * @version 1.0 - 2010-1-7 created by pangqiren
	 */
	private final class ConnectHostTaskExecutor extends
			TaskExecutor {
		private final String password;
		private final String userName;
		private final ServerInfo serverInfo;
		private final String ip;
		private final int buttonId;
		private final String port;

		public ConnectHostTaskExecutor(ServerInfo serverInfo, int buttonId) {
			this.password = serverInfo.getUserPassword();
			this.userName = serverInfo.getUserName();
			this.serverInfo = serverInfo;
			this.ip = serverInfo.getHostAddress();
			this.buttonId = buttonId;
			this.port = String.valueOf(serverInfo.getHostMonPort());
		}

		/**
		 * 
		 * Disconnect the server
		 * 
		 */
		public void disConnect() {
			ServerManager.getInstance().setConnected(ip,
					Integer.parseInt(port), userName, false);
		}

		/**
		 * Execute to connect
		 * 
		 * @param monitor the IProgressMonitor
		 * @return <code>true</code> if valid;<code>false</code> otherwise
		 */
		public boolean exec(final IProgressMonitor monitor) {
			Display display = Display.getDefault();
			if (monitor.isCanceled()) {
				return false;
			}
			List<DatabaseInfo> databaseInfoList = null;
			String dbPath = null;
			FileUtil.OsInfoType osInfoType = null;
			
			MonitorDashboardPreference monPref = new MonitorDashboardPreference();
			
			for (ITask task : taskList) {
				if (task instanceof MonitoringTask) {
					CMHostNodePersistManager.getInstance().addServer(ip,
							Integer.parseInt(port), userName, serverInfo);
					MonitoringTask monitoringTask = (MonitoringTask) task;
					monitoringTask.connectServer(Version.releaseVersion,
							monPref.getHAHeartBeatTimeout());
				} else {
					task.execute();
				}
				if (monitor.isCanceled()) {
					disConnect();
					return false;
				}
				if (openErrorBox(getShell(), task.getErrorMsg(), monitor)) {
					disConnect();
					return false;
				}
				if (task instanceof GetEnvInfoTask) {
					GetEnvInfoTask getEnvInfoTask = (GetEnvInfoTask) task;
					EnvInfo envInfo = getEnvInfoTask.loadEnvInfo();
					if (envInfo != null) {
						serverInfo.setEnvInfo(envInfo);
						dbPath = envInfo.getDatabaseDir();
						osInfoType = serverInfo.getServerOsInfo();
					}
					int ret = serverInfo.isSupportReplication();
					String errorMsg = null;
					if (ret == 1) {
						errorMsg = Messages.errInvalidPlatform;
					} else if (ret == 2) {
						errorMsg = Messages.errInvalidServerType;
					} else if (ret == 3) {
						errorMsg = Messages.errValidVersion;
					}
					if (openErrorBox(getShell(), errorMsg, monitor)) {
						disConnect();
						return false;
					}
				} else if (task instanceof GetDatabaseListTask) {
					GetDatabaseListTask getDatabaseListTask = (GetDatabaseListTask) task;
					databaseInfoList = getDatabaseListTask.loadDatabaseInfo();
				} else if (task instanceof GetCMConfParameterTask) {
					GetCMConfParameterTask getCMConfParameterTask = (GetCMConfParameterTask) task;
					Map<String, String> confParameters = getCMConfParameterTask.getConfParameters();
					ServerType serverType = ServerType.BOTH;
					if (confParameters != null) {
						String target = confParameters.get(ConfConstants.CM_TARGET);
						if (target != null) {
							if (target.indexOf("broker") >= 0
									&& target.indexOf("server") >= 0) {
								serverType = ServerType.BOTH;
							} else if (target.indexOf("broker") >= 0) {
								serverType = ServerType.BROKER;
							} else if (target.indexOf("server") >= 0) {
								serverType = ServerType.DATABASE;
							}
						}
					}
					serverInfo.setServerType(serverType);
				}
			}
			disConnect();
			if (!monitor.isCanceled()) {
				hostInfo.setIp(ip);
				hostInfo.setPort(port);
				hostInfo.setUserName(userName);
				hostInfo.setPassword(password);
				hostInfo.setDbPath(dbPath);
				hostInfo.setOsInfoType(osInfoType);
				hostInfo.setDatabaseInfoList(databaseInfoList);
				display.syncExec(new Runnable() {
					public void run() {
						hostInfo.setName(ip + ":" + port);
						setReturnCode(buttonId);
						close();
					}
				});
			}
			return true;
		}
	}
}