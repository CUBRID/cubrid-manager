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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;

/**
 * 
 * Connect host dialog
 * 
 * @author pangqiren
 * @version 1.0 - 2010-6-17 created by pangqiren
 */
public class ConnectHostNodeDialog extends
		CMTitleAreaDialog implements
		ModifyListener {

	private Text ipText = null;
	private Text portText = null;
	private Text userNameText = null;
	private Text passwordText = null;
	private final HostNode selectedHostNode;
	private String password;
	private String port;
	private final String errorMsg;

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 */
	public ConnectHostNodeDialog(Shell parentShell, HostNode hostNode,
			String msg) {
		super(parentShell);
		selectedHostNode = hostNode;
		errorMsg = msg;
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
		composite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH,
				1, 1, -1, -1));
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
		ipLabel.setText(Messages.lblIPAddress);
		ipLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		ipText = new Text(cmServerInfoGroup, SWT.LEFT | SWT.BORDER);
		ipText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		ipText.setEnabled(false);

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
		setTitle(Messages.titileHostInfoPage);
		setMessage(Messages.msgHostInfoPage);
		init();
		return composite;
	}

	/**
	 * 
	 * Initial the node
	 * 
	 */
	private void init() {
		if (selectedHostNode != null) {
			String ip = selectedHostNode.getIp();
			String port = selectedHostNode.getPort();
			String userName = selectedHostNode.getUserName();
			String password = selectedHostNode.getPassword();
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
		}
		portText.addModifyListener(this);
		passwordText.addModifyListener(this);
		if (errorMsg != null) {
			setErrorMessage(errorMsg);
		}
		passwordText.setFocus();
	}

	/**
	 * Constraint the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		CommonUITool.centerShell(getShell());
		getShell().setSize(500, 400);
		getShell().setText(Messages.titileHostInfoPage);
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
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * @param event ModifyEvent
	 */
	public void modifyText(ModifyEvent event) {
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		String ip = ipText.getText();
		boolean isValidIP = ip.trim().length() > 0;
		if (!isValidIP) {
			setErrorMessage(Messages.errIPAddress);
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
			return;
		}

		String password = passwordText.getText();
		boolean isValidPassword = password.trim().length() >= 4
				&& password.indexOf(" ") < 0;

		ServerInfo serverInfo = ServerManager.getServer(
				ipText.getText(), Integer.parseInt(port),
				userNameText.getText());
		if (isValidPassword && serverInfo != null
				&& serverInfo.getLoginedUserInfo() != null
				&& serverInfo.getLoginedUserInfo().isAdmin()) {
			isValidPassword = password.equals(serverInfo.getLoginedUserInfo().getPassword());
		}
		if (!isValidPassword) {
			setErrorMessage(Messages.errPassword);
			return;
		}
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		setErrorMessage(null);
	}

	/**
	 * Call this method when button in button bar is pressed
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			port = portText.getText();
			password = passwordText.getText();
		}
		super.buttonPressed(buttonId);
	}

	public String getPassword() {
		return password;
	}

	public String getPort() {
		return port;
	}

}
