/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution. 
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met: 
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE. 
 *
 */
package com.cubrid.cubridmanager.ui.host.control;

import java.util.List;

import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMWizardPage;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * Setting Host Page
 * 
 * @author Kevin.Wang
 * @version 1.0 - 2012-11-27 created by Kevin.Wang
 */
public class SettingHostPage extends
		CMWizardPage {

	private final static String PAGE_NAME = SettingHostPage.class.getName();

	private Text masterHostText;
	private Combo slaveHostCombo;

	private org.eclipse.swt.widgets.List masterDBList;
	private org.eclipse.swt.widgets.List slaveDBList;
	private Text masterHostName;
	private Text slaveHostName;

	private CubridServer selectedSlaveServer;

	protected SettingHostPage() {
		super(PAGE_NAME);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());
		setControl(container);
		setDescription(Messages.descSettingHostPage);
		setPageComplete(false);

		Composite leftComposite = new Composite(container, SWT.NONE);
		leftComposite.setLayout(new GridLayout(2, false));
		FormData leftData = new FormData();
		leftData.top = new FormAttachment(0, 5);
		leftData.bottom = new FormAttachment(100, 0);
		leftData.left = new FormAttachment(0, 5);
		leftData.right = new FormAttachment(50, -5);
		leftComposite.setLayoutData(leftData);

		Label separator = new Label(container, SWT.SEPARATOR);
		FormData separatorData = new FormData();
		separatorData.top = new FormAttachment(0, 5);
		separatorData.bottom = new FormAttachment(100, -5);
		separatorData.left = new FormAttachment(50, -5);
		separatorData.right = new FormAttachment(50, 5);
		separator.setLayoutData(separatorData);

		Composite rightComposite = new Composite(container, SWT.NONE);
		rightComposite.setLayout(new GridLayout(3, false));
		FormData rightData = new FormData();
		rightData.top = new FormAttachment(0, 5);
		rightData.bottom = new FormAttachment(100, 0);
		rightData.left = new FormAttachment(50, 5);
		rightData.right = new FormAttachment(100, -5);
		rightComposite.setLayoutData(rightData);

		/*Create left widget*/
		Label hostALabel = new Label(leftComposite, SWT.None);
		hostALabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		hostALabel.setText(Messages.lblMaster);

		masterHostText = new Text(leftComposite, SWT.BORDER);
		masterHostText.setEnabled(false);
		masterHostText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label masterNameLabel = new Label(leftComposite, SWT.None);
		masterNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		masterNameLabel.setText(Messages.lblMasterHost);

		masterHostName = new Text(leftComposite, SWT.BORDER);
		masterHostName.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		masterHostName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		masterDBList = new org.eclipse.swt.widgets.List(leftComposite,
				SWT.BORDER);
		masterDBList.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_BOTH, 2, 1, -1, -1));
		masterDBList.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				validate();
			}
		});

		/*Create right widget*/
		Label hostBLabel = new Label(rightComposite, SWT.None);
		hostBLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		hostBLabel.setText(Messages.lblSlave);

		slaveHostCombo = new Combo(rightComposite, SWT.READ_ONLY);
		slaveHostCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		slaveHostCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				selectedSlaveServer = null;
				String serverName = slaveHostCombo.getText();
				List<CubridServer> serverList = CMHostNodePersistManager.getInstance().getAllServers();
				for (CubridServer server : serverList) {
					if (serverName.equals(server.getName())) {
						selectedSlaveServer = server;
						break;
					}
				}
				initSlaveDBList(selectedSlaveServer);
				validate();
			}
		});

		Label slaveNameLabel = new Label(rightComposite, SWT.None);
		slaveNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		slaveNameLabel.setText(Messages.lblSlaveHost);

		slaveHostName = new Text(rightComposite, SWT.BORDER);
		slaveHostName.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		slaveHostName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		slaveDBList = new org.eclipse.swt.widgets.List(rightComposite,
				SWT.BORDER);
		slaveDBList.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_BOTH, 3, 1, -1, -1));
		slaveDBList.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				validate();
			}
		});

		init();
	}

	/**
	 * Init data
	 * 
	 */
	private void init() {
		HAModel haModel = getConfigHAWizard().getHaModel();

		masterHostText.setText(haModel.getMasterServer().getServer().getServerName());

		slaveHostCombo.removeAll();
		String master = haModel.getMasterServer().getServer().getServerName();
		List<CubridServer> serverList = CMHostNodePersistManager.getInstance().getAllServers();
		for (CubridServer server : serverList) {
			if (server.isConnected() && !master.equals(server.getServerName())) {
				ServerInfo serverInfo = server.getServerInfo();
				if (!CompatibleUtil.isSupportNewHAConfFile(serverInfo)) {
					continue;
				}

				ServerUserInfo userInfo = serverInfo.getLoginedUserInfo();
				if (userInfo == null
						|| CasAuthType.AUTH_ADMIN != userInfo.getCasAuth()) {
					continue;
				}

				slaveHostCombo.add(server.getName());
			}
		}
		initMasterDBList();
	}

	/**
	 * Init master db list
	 * 
	 */
	private void initMasterDBList() {
		HAServer haServer = getConfigHAWizard().getHaModel().getMasterServer();

		List<ICubridNode> children = haServer.getServer().getChildren();
		masterDBList.removeAll();

		for (ICubridNode child : children) {
			if (NodeType.DATABASE_FOLDER.equals(child.getType())) {
				for (ICubridNode db : child.getChildren()) {
					if (NodeType.DATABASE.equals(db.getType())) {
						masterDBList.add(db.getName());
					}
				}
			}
		}
	}

	/**
	 * Init slave db list
	 * 
	 */
	private void initSlaveDBList(CubridServer cubridServer) {
		slaveDBList.removeAll();

		List<ICubridNode> children = cubridServer.getChildren();
		for (ICubridNode child : children) {
			if (NodeType.DATABASE_FOLDER.equals(child.getType())) {
				for (ICubridNode db : child.getChildren()) {
					if (NodeType.DATABASE.equals(db.getType())) {
						slaveDBList.add(db.getName());
					}
				}
			}
		}
	}

	private boolean validate() {
		setErrorMessage(null);
		setPageComplete(false);

		if (StringUtil.isEmpty(slaveHostCombo.getText())) {
			setErrorMessage(Messages.errSelectSlaveServer);
			return false;
		}

		if (StringUtil.isEmpty(masterHostName.getText())) {
			setErrorMessage(Messages.errMasterHostEmpty);
			return false;
		}

		if (StringUtil.isEmpty(slaveHostName.getText())) {
			setErrorMessage(Messages.errSlaveHostEmpty);
			return false;
		}

		if (masterDBList.getSelectionCount() == 0) {
			setErrorMessage(Messages.errMasterDBEmpty);
			return false;
		}

		if (slaveDBList.getSelectionCount() == 0) {
			setErrorMessage(Messages.errSlaveDBEmpty);
			return false;
		}

		if (!StringUtil.isEqual(masterDBList.getSelection()[0],
				slaveDBList.getSelection()[0])) {
			setErrorMessage(Messages.errSelectDBDiff);
			return false;
		}

		setPageComplete(true);
		return true;
	}

	protected void handlePageLeaving(PageChangingEvent event) {

		if (!validate()) {
			return;
		}
		HAModel haModel = getConfigHAWizard().getHaModel();
		
		/*If selected host changed*/
		if(haModel.getSlaveServer() != null ) {
			/*If selected host changed*/
			if(!haModel.getSlaveServer().getServer().getServerName().equals(
					selectedSlaveServer.getServerName())) {
				haModel.getSlaveServer().setCubridParameters(null);
				haModel.getSlaveServer().setCubridHAParameters(null);
			}
			/*If just selected db changed*/
			if(!masterDBList.getSelection()[0].equals(haModel.getSelectedDB())) {
				haModel.getMasterServer().setCubridParameters(null);
				haModel.getSlaveServer().setCubridParameters(null);
			}
			/*If just change host name*/
			if (!StringUtil.isEqual(masterHostText.getText(),
					haModel.getMasterServer().getHostName())
					|| !StringUtil.isEqual(slaveHostName.getText(),
							haModel.getSlaveServer().getHostName())) {
				haModel.getMasterServer().setCubridHAParameters(null);
				haModel.getSlaveServer().setCubridHAParameters(null);
			}
		}

		/*Setting data*/
		haModel.setSlaveServer(selectedSlaveServer);
		haModel.setSelectedDB(masterDBList.getSelection()[0]);
		haModel.getMasterServer().setHostName(masterHostName.getText());
		haModel.getSlaveServer().setHostName(slaveHostName.getText());

		CommonUITool.openWarningBox(Messages.warnMasterPrimaryKeyNotice);
	}

	protected void handlePageShowing(PageChangingEvent event) {
		setTitle(Messages.haStep1);
	}

	private ConfigHAWizard getConfigHAWizard() {
		return (ConfigHAWizard) getWizard();
	}
}
