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
package com.cubrid.cubridmanager.ui.replication.editor.dialog.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * Select master database wizard page for set master database information wizard
 * 
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class SelectDatabasePage extends
		WizardPage implements
		ModifyListener {

	public final static String PAGENAME = "SetMasterDbWizard/SelectDatabasePage";
	private final MasterNode master;
	private HostNode host = null;
	private Combo masterDbNameCombo;
	private Text dbaPasswordText;
	private Text serverPortText;
	private List<DatabaseInfo> databaseInfoList = null;
	//when view the master information of replication,check whether the master db exist
	private boolean isMdbExist = true;
	private boolean isEditable = true;

	/**
	 * The constructor
	 */
	public SelectDatabasePage(MasterNode master) {
		super(PAGENAME);
		this.master = master;
		if (this.master != null && this.master.getParent() instanceof HostNode) {
			host = (HostNode) master.getParent();
		}
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
		initialize();
		setTitle(Messages.titleSelectMasterDb);
		setControl(composite);
	}

	/**
	 * 
	 * Create database name group
	 * 
	 * @param parent Composite
	 */
	private void createMasterDbGroup(Composite parent) {

		Group masterDbInfoGroup = new Group(parent, SWT.NONE);
		masterDbInfoGroup.setText(Messages.grpSelectMdb);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		masterDbInfoGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		masterDbInfoGroup.setLayout(layout);

		Label masterDbNameLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		masterDbNameLabel.setText(Messages.lblDbName);
		masterDbNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		masterDbNameCombo = new Combo(masterDbInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		masterDbNameCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Label dbaPasswordLabel = new Label(masterDbInfoGroup, SWT.LEFT);
		dbaPasswordLabel.setText(Messages.lblDbaPassword);
		dbaPasswordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dbaPasswordText = new Text(masterDbInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.PASSWORD);
		dbaPasswordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));

		Group replServerPortGroup = new Group(parent, SWT.NONE);
		replServerPortGroup.setText(Messages.grpReplServer);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		replServerPortGroup.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 3;
		replServerPortGroup.setLayout(layout);

		Label serverPortLabel = new Label(replServerPortGroup, SWT.LEFT);
		serverPortLabel.setText(Messages.lblReplServerPort);
		serverPortLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		serverPortText = new Text(replServerPortGroup, SWT.LEFT | SWT.BORDER);
		serverPortText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, 100, -1));
	}

	/**
	 * initialize some values
	 */
	private void initialize() {
		boolean isValidHost = host != null && host.isValid();
		if (isValidHost) {
			setMessage(Messages.msgSelectMasterDb);
		} else {
			setErrorMessage(Messages.errInvalidHostInfo);
		}
		if (isValidHost) {
			ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
					host.getIp(), Integer.parseInt(host.getPort()),
					host.getUserName());
			if (serverInfo != null
					&& !serverInfo.getLoginedUserInfo().isAdmin()) {
				setErrorMessage(Messages.bind(Messages.errInvalidUser,
						host.getIp()));
			}
		}
		List<String> dbNameList = new ArrayList<String>();
		if (host != null && host.isValid()) {
			databaseInfoList = host.getDatabaseInfoList();
			ServerInfo serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(
					host.getIp(), Integer.parseInt(host.getPort()),
					host.getUserName());
			if (serverInfo != null && serverInfo.isConnected()) {
				databaseInfoList = serverInfo.getLoginedUserInfo().getDatabaseInfoList();
			}
			for (int i = 0; databaseInfoList != null
					&& i < databaseInfoList.size(); i++) {
				dbNameList.add(databaseInfoList.get(i).getDbName());
			}
		}
		String mdbName = null;
		if (master != null) {
			mdbName = master.getDbName();
		}
		if (dbNameList.isEmpty()) {
			if (isValidHost) {
				setErrorMessage(Messages.errNoDb);
			}
		} else {
			String[] dbNameArr = new String[dbNameList.size()];
			dbNameList.toArray(dbNameArr);
			masterDbNameCombo.setItems(dbNameArr);
			if (mdbName != null && mdbName.trim().length() > 0) {
				boolean isExistMdbName = false;
				for (int i = 0; i < dbNameArr.length; i++) {
					if (dbNameArr[i].equals(mdbName)) {
						isExistMdbName = true;
						break;
					}
				}
				if (!isExistMdbName) {
					isMdbExist = false;
					setErrorMessage(Messages.bind(Messages.errNoMdb, mdbName));
				}
			}
		}
		if (master != null) {
			if (mdbName != null) {
				masterDbNameCombo.setText(master.getDbName());
			}
			if (master.getDbaPassword() != null) {
				dbaPasswordText.setText(master.getDbaPassword());
			}
			if (master.getReplServerPort() != null) {
				serverPortText.setText(master.getReplServerPort());
			}
		}
		if (isEditable) {
			masterDbNameCombo.addModifyListener(this);
			serverPortText.addModifyListener(this);
		} else {
			masterDbNameCombo.setEnabled(false);
			serverPortText.setEnabled(false);
		}
		dbaPasswordText.addModifyListener(this);
		if (master != null && master.isValid()) {
			setPageComplete(true);
		}
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event ModifyEvent
	 */
	public void modifyText(ModifyEvent event) {
		String mdbName = masterDbNameCombo.getText();
		String dbaPassword = dbaPasswordText.getText();
		String serverPort = serverPortText.getText();

		boolean isValidPassword = dbaPassword.length() >= 4
				&& dbaPassword.indexOf(" ") < 0;
		boolean isValidDbName = ValidateUtil.isValidDBName(mdbName);

		boolean isLogined = false;
		if (isValidDbName && databaseInfoList != null) {
			for (int i = 0; i < databaseInfoList.size(); i++) {
				DatabaseInfo dbInfo = databaseInfoList.get(i);
				if (mdbName.equalsIgnoreCase(dbInfo.getDbName())) {
					isLogined = dbInfo.isLogined()
							&& !dbInfo.getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(
									"dba");
					if (dbInfo.isLogined() && isValidPassword) {
						isValidPassword = dbaPassword.equals(dbInfo.getAuthLoginedDbUserInfo().getNoEncryptPassword());
					}
					break;
				}
			}
		}
		boolean isValidPort = ValidateUtil.isNumber(serverPort);
		if (isValidPort) {
			int portVal = Integer.parseInt(serverPort);
			if (portVal < 1024 || portVal > 65535) {
				isValidPort = false;
			}
		}
		boolean isValidHost = host != null && host.isValid();
		if (!isValidHost) {
			setErrorMessage(Messages.errInvalidHostInfo);
			setPageComplete(false);
			return;
		}
		if (!isMdbExist) {
			setErrorMessage(Messages.bind(Messages.errNoMdb, mdbName));
			setPageComplete(false);
			return;
		}
		if (isLogined) {
			setErrorMessage(Messages.bind(Messages.errInvalidDbUser, mdbName));
			setPageComplete(false);
			return;
		}
		if (!isValidDbName) {
			setErrorMessage(Messages.errDatabaseName);
			setPageComplete(false);
			return;
		}
		if (!isValidPassword) {
			setErrorMessage(Messages.errDbaPassword);
			setPageComplete(false);
			return;
		}
		if (!isValidPort) {
			setErrorMessage(Messages.errReplServerPort);
			setPageComplete(false);
			return;
		}
		setErrorMessage(null);
		setPageComplete(true);
	}

	public String getDbName() {
		return masterDbNameCombo.getText();
	}

	public String getDbaPassword() {
		return dbaPasswordText.getText();
	}

	public String getReplServerPort() {
		return serverPortText.getText();
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

}
