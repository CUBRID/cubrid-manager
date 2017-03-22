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
package com.cubrid.cubridmanager.ui.replication.dialog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.replication.model.MasterInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.task.GetReplServerStatusTask;
import com.cubrid.cubridmanager.core.replication.task.StartReplServerTask;
import com.cubrid.cubridmanager.core.replication.task.StopReplServerTask;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * This dialog is responsible to collect start/stop replication server needed
 * information
 * 
 * @author pangqiren
 * @version 1.0 - 2009-8-28 created by pangqiren
 */
public class ReplServerDialog extends
		CMTitleAreaDialog implements
		ModifyListener {
	private Text ipText = null;
	private Text portText = null;
	private Text userNameText = null;
	private Text passwordText = null;
	private final boolean isStartReplServer;
	private boolean isActive = false;
	private Text mdbNameText;
	private Text replServerPortText;
	private ICubridNode node = null;

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 */
	public ReplServerDialog(Shell parentShell, boolean isStartReplServer) {
		super(parentShell);
		this.isStartReplServer = isStartReplServer;
	}

	/**
	 * Create the dialog area
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
		ipText.setEnabled(false);
		ipText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label portNameLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		portNameLabel.setText(Messages.lblPort);
		portNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		portText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		portText.setText("8001");
		portText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label userNameLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		userNameLabel.setText(Messages.lblUserName);
		userNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		userNameText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		userNameText.setEnabled(false);
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

		Group replServerInfoGroup = new Group(composite, SWT.NONE);
		replServerInfoGroup.setText(Messages.grpReplServer);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		replServerInfoGroup.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 2;
		replServerInfoGroup.setLayout(layout);

		Label mdbNameLabel = new Label(replServerInfoGroup, SWT.LEFT);
		mdbNameLabel.setText(Messages.lblMdbName);
		mdbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		mdbNameText = new Text(replServerInfoGroup, SWT.LEFT | SWT.BORDER);
		mdbNameText.setEnabled(false);
		mdbNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label replServerPortLabel = new Label(replServerInfoGroup, SWT.LEFT);
		replServerPortLabel.setText(Messages.lblReplServerPort);
		replServerPortLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1,
				-1));
		replServerPortText = new Text(replServerInfoGroup, SWT.LEFT
				| SWT.BORDER);
		replServerPortText.setEnabled(false);
		replServerPortText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		initialize();
		if (this.isStartReplServer) {
			setTitle(Messages.titleStartReplServer);
		} else {
			setTitle(Messages.titleStopReplServer);
		}
		setMessage(Messages.msgReplServerDialog);
		return parentComp;
	}

	/**
	 * initialize some values
	 */
	private void initialize() {
		if (node != null) {
			ReplicationInfo replInfo = (ReplicationInfo) node.getAdapter(ReplicationInfo.class);
			if (replInfo != null && replInfo.getMasterList() != null
					&& replInfo.getMasterList().size() > 0) {
				MasterInfo masterInfo = replInfo.getMasterList().get(0);
				ipText.setText(masterInfo.getMasterIp());
				portText.setText("8001");
				portText.selectAll();
				userNameText.setText("admin");
				passwordText.setText("");
				mdbNameText.setText(masterInfo.getMasterDbName());
				replServerPortText.setText(masterInfo.getReplServerPort());
			}
			portText.addModifyListener(this);
			passwordText.addModifyListener(this);
		}
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setSize(400, 380);
		CommonUITool.centerShell(getShell());
		if (this.isStartReplServer) {
			getShell().setText(Messages.titleStartReplServer);
		} else {
			getShell().setText(Messages.titleStopReplServer);
		}
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
	 * When press button,call it
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			execute(buttonId);
		} else {
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * excute the task.
	 * 
	 * @param buttonId int
	 */
	private void execute(final int buttonId) {
		final String ip = ipText.getText();
		final String port = portText.getText();
		final String userName = userNameText.getText();
		final String password = passwordText.getText();
		final String mdbName = mdbNameText.getText();
		final String replServerPort = replServerPortText.getText();

		TaskExecutor taskExcutor = new TaskExecutor() {

			public void disConnect() {
				ServerManager.setConnected(ip,
						Integer.parseInt(port), userName, false);
			}

			public boolean exec(final IProgressMonitor monitor) {
				Display display = Display.getDefault();
				if (monitor.isCanceled()) {
					return false;
				}
				boolean isConnected = false;
				
				MonitorDashboardPreference monPref = new MonitorDashboardPreference();
				
				for (ITask task : taskList) {
					if (task instanceof MonitoringTask) {
						MonitoringTask monitoringTask = (MonitoringTask) task;
						monitoringTask.connectServer(Version.releaseVersion,
								monPref.getHAHeartBeatTimeout());
						isConnected = true;
					} else if (task instanceof GetReplServerStatusTask) {
						GetReplServerStatusTask getReplServerStatusTask = (GetReplServerStatusTask) task;
						getReplServerStatusTask.execute();
						isActive = getReplServerStatusTask.isActive();
					} else if (task instanceof StartReplServerTask) {
						if (isActive) {
							openErrorBox(getShell(),
									Messages.msgReplServerStarted, monitor);
						} else {
							StartReplServerTask startReplServerTask = (StartReplServerTask) task;
							startReplServerTask.execute();
							if (task.isSuccess()) {
								openInformationgBox(
										getShell(),
										com.cubrid.cubridmanager.ui.common.Messages.titleSuccess,
										Messages.msgReplServerStartedSuccess,
										monitor);
							}
						}
					} else if (task instanceof StopReplServerTask) {
						if (isActive) {
							StopReplServerTask stopReplServerTask = (StopReplServerTask) task;
							stopReplServerTask.execute();
							if (task.isSuccess()) {
								openInformationgBox(
										getShell(),
										com.cubrid.cubridmanager.ui.common.Messages.titleSuccess,
										Messages.msgReplServerStopedSuccess,
										monitor);
							}
						} else {
							openErrorBox(getShell(),
									Messages.msgReplServerStoped, monitor);
						}
					}
					if (monitor.isCanceled()) {
						if (isConnected) {
							disConnect();
						}
						return false;
					}
					final String msg = task.getErrorMsg();
					if (openErrorBox(getShell(), msg, monitor)) {
						if (isConnected) {
							ServerManager.setConnected(ip,
									Integer.parseInt(port), userName, false);

						}
						return false;
					}
				}
				if (isConnected) {
					disConnect();
				}
				if (!monitor.isCanceled()) {
					display.syncExec(new Runnable() {
						public void run() {
							setReturnCode(buttonId);
							close();
						}
					});
				}
				return true;
			}
		};

		boolean isConnected = ServerManager.isConnected(ip,
				Integer.parseInt(port), userName);
		ServerInfo serverInfo = new ServerInfo();
		if (isConnected) {
			serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(ip,
					Integer.parseInt(port), userName);
		} else {
			serverInfo.setHostAddress(ip);
			serverInfo.setHostMonPort(Integer.parseInt(port));
			serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
			serverInfo.setUserName(userName);
			serverInfo.setUserPassword(password);
			CMHostNodePersistManager.getInstance().addServer(ip, Integer.parseInt(port),
					userName, serverInfo);
			MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
			taskExcutor.addTask(monitoringTask);
		}

		GetReplServerStatusTask getReplServerStatusTask = new GetReplServerStatusTask(
				serverInfo);
		getReplServerStatusTask.setDbName(mdbName);
		taskExcutor.addTask(getReplServerStatusTask);
		if (isStartReplServer) {
			StartReplServerTask task = new StartReplServerTask(serverInfo);
			task.setDbName(mdbName);
			task.setServerPort(replServerPort);
			taskExcutor.addTask(task);
		} else {
			StopReplServerTask task = new StopReplServerTask(serverInfo);
			task.setDbName(mdbName);
			taskExcutor.addTask(task);
		}
		new ExecTaskWithProgress(taskExcutor).exec();
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event ModifyEvent
	 */
	public void modifyText(ModifyEvent event) {
		String port = portText.getText();
		boolean isValidPort = ValidateUtil.isNumber(port);
		if (isValidPort) {
			int portVal = Integer.parseInt(port);
			if (portVal < 1024 || portVal > 65535) {
				isValidPort = false;
			}
		}
		String password = passwordText.getText();
		boolean isValidPassword = password.trim().length() >= 4
				&& password.indexOf(" ") < 0;
		boolean isValidUser = true;
		if (isValidPort) {
			ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
					ipText.getText(), Integer.parseInt(port),
					userNameText.getText());
			if (serverInfo != null
					&& !serverInfo.getLoginedUserInfo().isAdmin()) {
				isValidUser = false;
			}
			if (isValidPassword && serverInfo != null
					&& serverInfo.getLoginedUserInfo() != null) {
				isValidPassword = password.equals(serverInfo.getLoginedUserInfo().getPassword());
			}
		}
		if (!isValidPort) {
			setErrorMessage(Messages.errPort);
			setEnabled(false);
			return;
		}
		if (!isValidUser) {
			setErrorMessage(Messages.bind(Messages.errInvalidUser,
					ipText.getText()));
			setEnabled(false);
			return;
		}
		if (!isValidPassword) {
			setErrorMessage(Messages.errInvalidPassword);
			setEnabled(false);
			return;
		}
		setErrorMessage(null);
		setEnabled(true);
	}

	/**
	 * set button to enabled
	 * 
	 * @param isEnabled boolean
	 */
	private void setEnabled(boolean isEnabled) {
		if (getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
		}
	}

	public ICubridNode getNode() {
		return node;
	}

	public void setNode(ICubridNode node) {
		this.node = node;
	}

}
