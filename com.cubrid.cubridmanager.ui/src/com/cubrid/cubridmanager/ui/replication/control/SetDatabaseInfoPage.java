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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.MonitoringTask;
import com.cubrid.cubridmanager.core.cubrid.database.task.LoginDatabaseTask;
import com.cubrid.cubridmanager.core.replication.model.DistributorInfo;
import com.cubrid.cubridmanager.core.replication.model.MasterInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.model.SlaveInfo;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.spi.Version;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * Set master database information Page for changing replicated tables wizard
 * 
 * @author pangqiren
 * @version 1.0 - 2009-11-26 created by pangqiren
 */
public class SetDatabaseInfoPage extends
		WizardPage implements
		ModifyListener {

	public final static String PAGENAME = "ChangeReplTablesWizard/SetDatabaseInfoPage";
	private final ICubridNode replNode;
	private Text ipText;
	private Text portText;
	private Text userNameText;
	private Text passwordText;
	private Text masterDbNameText;
	private Text masteDbaPasswordText;
	private Text distDbNameText;
	private Text distDbaPasswordText;
	private Text slaveDbNameText;

	/**
	 * The constructor
	 */
	public SetDatabaseInfoPage(ICubridNode node) {
		super(PAGENAME);
		replNode = node;
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

		createMasterDbGroup(composite);
		createDistDbGroup(composite);
		createSlaveDbGroup(composite);
		initialize();
		setTitle(Messages.titleSetDatabaseInfo);
		setMessage(Messages.msgSetDatabaseInfo);
		setControl(composite);
	}

	/**
	 * 
	 * Create master database information
	 * 
	 * @param parent Composite
	 */
	private void createMasterDbGroup(Composite parent) {

		Group cmServerInfoGroup = new Group(parent, SWT.NONE);
		cmServerInfoGroup.setText(Messages.grpMdbInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		cmServerInfoGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
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

		Label masterDbNameLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		masterDbNameLabel.setText(Messages.lblDbName);
		masterDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		masterDbNameText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		masterDbNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

		Label dbaPasswordLabel = new Label(cmServerInfoGroup, SWT.LEFT);
		dbaPasswordLabel.setText(Messages.lblDbaPassword);
		dbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		masteDbaPasswordText = new Text(cmServerInfoGroup, SWT.LEFT
				| SWT.BORDER | SWT.PASSWORD);
		masteDbaPasswordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
	}

	/**
	 * 
	 * Create distributor database group
	 * 
	 * @param parent Composite
	 */
	private void createDistDbGroup(Composite parent) {

		Group distDbInfoGroup = new Group(parent, SWT.NONE);
		distDbInfoGroup.setText(Messages.grpDistdbInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		distDbInfoGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		distDbInfoGroup.setLayout(layout);

		Label distDbNameLabel = new Label(distDbInfoGroup, SWT.LEFT);
		distDbNameLabel.setText(Messages.lblDbName);
		distDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		distDbNameText = new Text(distDbInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		distDbNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label dbaPasswordLabel = new Label(distDbInfoGroup, SWT.LEFT);
		dbaPasswordLabel.setText(Messages.lblDbaPassword);
		dbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		distDbaPasswordText = new Text(distDbInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.PASSWORD);
		distDbaPasswordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
	}

	/**
	 * 
	 * Create slave database group
	 * 
	 * @param parent Composite
	 */
	private void createSlaveDbGroup(Composite parent) {

		Group slaveDbInfoGroup = new Group(parent, SWT.NONE);
		slaveDbInfoGroup.setText(Messages.grpSdbInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		slaveDbInfoGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		slaveDbInfoGroup.setLayout(layout);

		Label slaveDbNameLabel = new Label(slaveDbInfoGroup, SWT.LEFT);
		slaveDbNameLabel.setText(Messages.lblDbName);
		slaveDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		slaveDbNameText = new Text(slaveDbInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		slaveDbNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

	}

	/**
	 * initialize some values
	 */
	private void initialize() {
		if (replNode != null && NodeType.DATABASE.equals(replNode.getType())) {
			ReplicationInfo replInfo = (ReplicationInfo) replNode.getAdapter(ReplicationInfo.class);
			if (replInfo != null && replInfo.getMasterList() != null
					&& replInfo.getMasterList().size() > 0) {
				MasterInfo masterInfo = replInfo.getMasterList().get(0);
				ipText.setText(masterInfo.getMasterIp());
				masterDbNameText.setText(masterInfo.getMasterDbName());
			}
			if (replInfo != null && replInfo.getDistInfo() != null) {
				DistributorInfo distInfo = replInfo.getDistInfo();
				distDbNameText.setText(distInfo.getDistDbName());
			}
			if (replInfo != null && replInfo.getSlaveList() != null
					&& replInfo.getSlaveList().size() > 0) {
				SlaveInfo slaveInfo = replInfo.getSlaveList().get(0);
				slaveDbNameText.setText(slaveInfo.getSlaveDbName());
			}
		}

		ipText.setEnabled(false);
		userNameText.setEnabled(false);
		masterDbNameText.setEnabled(false);
		distDbNameText.setEnabled(false);
		slaveDbNameText.setEnabled(false);
		portText.addModifyListener(this);
		passwordText.addModifyListener(this);
		distDbaPasswordText.addModifyListener(this);
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event ModifyEvent
	 */
	public void modifyText(ModifyEvent event) {
		final String port = portText.getText();
		String password = passwordText.getText();
		String mdbPassword = masteDbaPasswordText.getText();
		String distdbPassword = distDbaPasswordText.getText();

		boolean isValidPort = ValidateUtil.isNumber(port);
		if (isValidPort) {
			int portVal = Integer.parseInt(port);
			if (portVal < 1024 || portVal > 65535) {
				isValidPort = false;
			}
		}
		if (!isValidPort) {
			setErrorMessage(Messages.errInvalidPort);
			setPageComplete(false);
			return;
		}

		boolean isValidPassword = password.length() >= 4
				&& password.indexOf(" ") < 0;
		if (!isValidPassword) {
			setErrorMessage(Messages.errInvalidPassword);
			setPageComplete(false);
			return;
		}

		boolean isValidMdbPassword = mdbPassword.length() >= 4
				&& mdbPassword.indexOf(" ") < 0;
		if (!isValidMdbPassword) {
			setErrorMessage(Messages.errInvalidMdbPassword);
			setPageComplete(false);
			return;
		}

		boolean isValidDistdbPassword = distdbPassword.length() >= 4
				&& distdbPassword.indexOf(" ") < 0;
		if (!isValidDistdbPassword) {
			setErrorMessage(Messages.errInvalidDistdbPassword);
			setPageComplete(false);
			return;
		}
		boolean isValid = isValidPort && isValidPassword && isValidMdbPassword
				&& isValidDistdbPassword;
		setPageComplete(false);
		if (isValid) {
			final String ip = ipText.getText();
			final String userName = userNameText.getText();
			final MonitorDashboardPreference monPref = new MonitorDashboardPreference();
			
			TaskExecutor taskExcutor = new TaskExecutor() {
				public boolean exec(final IProgressMonitor monitor) {
					Display display = getShell().getDisplay();
					if (monitor.isCanceled()) {
						return false;
					}
					boolean isConnected = false;
					for (ITask task : taskList) {
						if (task instanceof MonitoringTask) {
							MonitoringTask monitoringTask = (MonitoringTask) task;
							monitoringTask.connectServer(Version.releaseVersion,
									monPref.getHAHeartBeatTimeout());
							isConnected = true;
						} else if (task instanceof LoginDatabaseTask) {
							task.execute();
						}
						final String msg = task.getErrorMsg();
						if (monitor.isCanceled()) {
							if (isConnected) {
								ServerManager.setConnected(ip,
										Integer.parseInt(port), userName, false);
							}
							return false;
						}
						if (msg != null && msg.length() > 0
								&& !monitor.isCanceled()) {
							monitor.done();
							display.syncExec(new Runnable() {
								public void run() {
									setErrorMessage(msg);
								}
							});
							if (isConnected) {
								ServerManager.setConnected(ip,
										Integer.parseInt(port), userName, false);
							}
							return false;
						}
					}
					if (!monitor.isCanceled()) {
						if (isConnected) {
							ServerManager.setConnected(ip,
									Integer.parseInt(port), userName, false);
						}
						display.syncExec(new Runnable() {
							public void run() {
								setErrorMessage(null);
								setPageComplete(true);
							}
						});
					}
					return true;
				}
			};
			ServerInfo serverInfo = new ServerInfo();
			if (ServerManager.isConnected(ip,
					Integer.parseInt(port), userName)) {
				serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(ip,
						Integer.parseInt(port), userName);
			} else {
				serverInfo.setHostAddress(ip);
				serverInfo.setHostMonPort(Integer.parseInt(port));
				serverInfo.setHostJSPort(Integer.parseInt(port + 1));
				serverInfo.setUserName(userNameText.getText());
				serverInfo.setUserPassword(password);
				CMHostNodePersistManager.getInstance().addServer(ip,
						Integer.parseInt(port), userName, serverInfo);
				MonitoringTask monitoringTask = new MonitoringTask(serverInfo);
				taskExcutor.addTask(monitoringTask);
			}
			LoginDatabaseTask loginMDbTask = new LoginDatabaseTask(serverInfo);
			loginMDbTask.setCMUser("admin");
			loginMDbTask.setDbName(masterDbNameText.getText());
			loginMDbTask.setDbUser("dba");
			loginMDbTask.setDbPassword(masteDbaPasswordText.getText());
			taskExcutor.addTask(loginMDbTask);

			LoginDatabaseTask loginDistdbTask = new LoginDatabaseTask(
					replNode.getServer().getServerInfo());
			loginDistdbTask.setCMUser("admin");
			loginDistdbTask.setDbName(distDbNameText.getText());
			loginDistdbTask.setDbUser("dba");
			loginDistdbTask.setDbPassword(distDbaPasswordText.getText());
			taskExcutor.addTask(loginDistdbTask);
			new ExecTaskWithProgress(taskExcutor).exec(true, true);
		}
	}

	public String getMasterDbName() {
		return masterDbNameText.getText();
	}

	public String getMasterDbaPassword() {
		return masteDbaPasswordText.getText();
	}

	public String getDistributorDbName() {
		return distDbNameText.getText();
	}

	public String getSlaveDbName() {
		return slaveDbNameText.getText();
	}

	public String getDistdbPassword() {
		return distDbaPasswordText.getText();
	}

	public String getIp() {
		return ipText.getText();
	}

	public String getPort() {
		return portText.getText();
	}

	public String getUserName() {
		return userNameText.getText();
	}

	public String getPassword() {
		return passwordText.getText();
	}
}
