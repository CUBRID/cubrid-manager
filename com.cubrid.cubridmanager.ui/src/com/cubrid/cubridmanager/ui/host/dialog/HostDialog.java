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
package com.cubrid.cubridmanager.ui.host.dialog;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.ui.common.dialog.JdbcManageDialog;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.persist.CubridJdbcManager;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.host.action.HostDashboardAction;
import com.cubrid.cubridmanager.ui.spi.contribution.CubridWorkbenchContrItem;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.util.HostUtils;
import com.cubrid.jdbc.proxy.manage.ServerJdbcVersionMapping;

/**
 *
 * This dialog is responsible to add and edit host information
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class HostDialog extends
		CMTitleAreaDialog implements
		ModifyListener {
	public final static int CONNECT_ID = 0;
	public final static int ADD_ID = 2;
	public final static int TEST_CONNECT_ID = 3;
	public final static int SAVE_ID = 4;

	private Text hostNameText = null;
	private Text addressText = null;
	private Text portText = null;
	private Text userNameText = null;
	private Text passwordText = null;
	private Combo jdbcVersionCombo = null;
	private CubridServer server = null;
	private ServerInfo serverInfo = null;
	private boolean isSavePassword;
	private Button btnAutoCommit;
	private final boolean isNewHost;
	private final boolean actionIsConnect;
	private ServerInfo testConnServerInfo = null;

	private Button btnUseTimeOut;
	private Combo timeOutCombo;
	private int[] soTimeOutValues = {10 * 1000, 30 * 1000, 60 * 1000, 0 };
	private String[] soTimeOutMsgs = {Messages.lbl10Second,
			Messages.lbl30Second, Messages.lbl60Second, Messages.lblNoLimit };

	/**
	 * The constructor
	 *
	 * @param parentShell
	 * @param isNewHost
	 */
	public HostDialog(Shell parentShell, boolean isNewHost, boolean actionIsConnect) {
		super(parentShell);
		this.isNewHost = isNewHost;
		this.actionIsConnect = actionIsConnect;
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
		layout.numColumns = 3;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		Label hostNameLabel = new Label(composite, SWT.LEFT);
		hostNameLabel.setText(Messages.lblHostName);
		hostNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		hostNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
		hostNameText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
		if (server != null) {
			hostNameText.setText(server.getLabel());
		}
		hostNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		hostNameText.addModifyListener(this);

		Label addressNameLabel = new Label(composite, SWT.LEFT);
		addressNameLabel.setText(Messages.lblAddress);
		addressNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		addressText = new Text(composite, SWT.LEFT | SWT.BORDER);
		if (server != null) {
			addressText.setText(server.getHostAddress());
		} else {
			addressText.setText("localhost");
		}
		addressText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		addressText.addModifyListener(this);

		Label portNameLabel = new Label(composite, SWT.LEFT);
		portNameLabel.setText(Messages.lblPort);
		portNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		portText = new Text(composite, SWT.LEFT | SWT.BORDER);
		portText.setTextLimit(5);
		if (server != null) {
			portText.setText(server.getMonPort());
		} else {
			portText.setText("8001");
		}

		portText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				portText.selectAll();
				portText.setFocus();
			}
		});
		portText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		portText.addModifyListener(this);

		Label userNameLabel = new Label(composite, SWT.LEFT);
		userNameLabel.setText(Messages.lblUserName);
		userNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		userNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
		userNameText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
		if (server != null) {
			userNameText.setText(server.getUserName());
		} else {
			userNameText.setText("admin");
		}
		userNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		userNameText.addModifyListener(this);

		Label passwordLabel = new Label(composite, SWT.LEFT);
		passwordLabel.setText(Messages.lblPassword);
		passwordLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		passwordText = new Text(composite, SWT.LEFT | SWT.PASSWORD | SWT.BORDER);
		passwordText.setTextLimit(ValidateUtil.MAX_NAME_LENGTH);
		passwordText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		if (server != null && server.getPassword() != null
				&& server.isAutoSavePassword()) {
			passwordText.setText(server.getPassword());
		}
		passwordText.addModifyListener(this);

		new Label(composite, SWT.NONE);
		Label passHelp = new Label(composite, SWT.None);
		passHelp.setText(Messages.msgPasswordHelp);
		passHelp.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

		new Label(composite, SWT.NONE).setLayoutData(CommonUITool.createGridData(
				1, 1, 0, 0));

		Composite btnComposite = new Composite(composite, SWT.NONE);
		btnComposite.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		layout = new GridLayout();
		layout.numColumns = 4;
		btnComposite.setLayout(layout);

		Button btnSavePassword = new Button(btnComposite, SWT.CHECK);
		btnSavePassword.setLayoutData(CommonUITool.createGridData(SWT.LEFT, 1, 1,
				-1, -1));
		btnSavePassword.setText(Messages.btnSavePassword);
		if (server == null) {
			btnSavePassword.setSelection(true);
			isSavePassword = true;
		} else {
			btnSavePassword.setSelection(server.isAutoSavePassword());
			isSavePassword = server.isAutoSavePassword();
		}

		btnSavePassword.addSelectionListener(new SelectionAdapter() {
			/**
			 * Sent when selection occurs in the control. The default behavior
			 * is to do nothing.
			 *
			 * @param e an event containing information about the selection
			 */
			public void widgetSelected(SelectionEvent event) {
				isSavePassword = !isSavePassword;
			}
		});

		btnAutoCommit = new Button(btnComposite, SWT.CHECK);
		btnAutoCommit.setLayoutData(CommonUITool.createGridData(SWT.LEFT, 1, 1,
				-1, -1));
		btnAutoCommit.setText(com.cubrid.common.ui.query.Messages.autoCommitLabel);
		boolean useAutoCommit = QueryOptions.getAutoCommit(server == null ? null : server.getServerInfo());
		btnAutoCommit.setSelection(useAutoCommit);

		btnUseTimeOut = new Button(btnComposite, SWT.CHECK);
		btnUseTimeOut.setLayoutData(CommonUITool.createGridData(SWT.LEFT, 1, 1, -1, -1));
		btnUseTimeOut.setText(Messages.btnSetTimeOut);
		btnUseTimeOut.setSelection(false);

		timeOutCombo = new Combo(btnComposite, SWT.READ_ONLY);
		timeOutCombo.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		timeOutCombo.setItems(soTimeOutMsgs);
		timeOutCombo.setEnabled(false);

		btnUseTimeOut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				timeOutCombo.setEnabled(btnUseTimeOut.getSelection());
				timeOutCombo.select(0);
			}
		});
		if (server != null && server.getServerInfo() != null) {
			int soTimeOut = server.getServerInfo().getSoTimeOut();
			if (soTimeOut != SocketTask.SOCKET_IO_TIMEOUT_MSEC) {
				int index = 0;
				for (int i = 0; i < soTimeOutValues.length; i++) {
					if (soTimeOutValues[i] == soTimeOut) {
						index = i;
						break;
					}
				}
				btnUseTimeOut.setSelection(true);
				timeOutCombo.setEnabled(true);
				timeOutCombo.select(index);
			}
		}

		Label jdbcLabel = new Label(composite, SWT.LEFT);
		jdbcLabel.setText(Messages.lblJdbcVersion);
		jdbcLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		jdbcVersionCombo = new Combo(composite, SWT.LEFT | SWT.READ_ONLY);
		jdbcVersionCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		resetJdbcCombo();
		jdbcVersionCombo.addModifyListener(this);

		Button btnOpen = new Button(composite, SWT.NONE);
		{
			btnOpen.setText(Messages.btnBrowse);
			btnOpen.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
			btnOpen.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					JdbcManageDialog dialog = new JdbcManageDialog(getShell());
					if (IDialogConstants.OK_ID == dialog.open()) {
						resetJdbcCombo();
					}
				}
			});
		}

		if (isNewHost && server != null) {
			hostNameText.selectAll();
			hostNameText.setFocus();
		} else if (server != null) {
			passwordText.selectAll();
			passwordText.setFocus();
		}
		if (isNewHost) {
			setTitle(Messages.titleAddHostDialog);
			setMessage(Messages.msgAddHostDialog);
		} else {
			if(actionIsConnect){
				setTitle(Messages.titleConnectHostDialog);
				setMessage(Messages.msgConnectHostDialog);
			}else{
				setTitle(Messages.titleEditHostDialog);
				setMessage(Messages.msgEditHostDialog);
			}
		}
		
		if(actionIsConnect){
			hostNameText.setEnabled(false);
			portText.setEnabled(false);
			passwordText.setEnabled(false);
			jdbcVersionCombo.setEnabled(false);
			userNameText.setEnabled(false);
			addressText.setEnabled(false);
			btnSavePassword.setEnabled(false);
			btnAutoCommit.setEnabled(false);
			btnUseTimeOut.setEnabled(false);
			timeOutCombo.setEnabled(false);
			btnOpen.setEnabled(false);
			passHelp.setVisible(false);
		}
		
		return parentComp;
	}

	/**
	 *
	 * Reset the JDBC combo
	 *
	 */
	private void resetJdbcCombo() {
		jdbcVersionCombo.removeAll();
		Map<String, String> jdbcMap = CubridJdbcManager.getInstance().getLoadedJdbc();
		if (jdbcMap.isEmpty()) {
			return;
		}
		Iterator<Entry<String, String>> iterator = jdbcMap.entrySet().iterator();
		String jdbcVersion = "";
		jdbcVersionCombo.add(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
		while (iterator.hasNext()) {
			Entry<String, String> next = iterator.next();
			if (server != null
					&& next.getKey().equals(server.getJdbcDriverVersion())) {
				jdbcVersion = server.getJdbcDriverVersion();
			}

			jdbcVersionCombo.add(next.getKey());
		}
		if (jdbcVersion == null || "".equals(jdbcVersion)) {
			jdbcVersionCombo.setText(ServerJdbcVersionMapping.JDBC_SELF_ADAPTING_VERSION);
		} else {
			jdbcVersionCombo.setText(jdbcVersion);
		}
	}

	/**
	 * Constraint the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		if (isNewHost) {
			getShell().setText(Messages.titleAddHostDialog);
		} else {
			if (actionIsConnect) {
				getShell().setText(Messages.titleConnectHostDialog);
			} else {
				getShell().setText(Messages.titleEditHostDialog);
			}
		}
	}

	/**
	 * Create buttons for button bar
	 *
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {

		if (isNewHost) {
			createButton(parent, TEST_CONNECT_ID, Messages.btnTestConn, false);
			createButton(parent, ADD_ID, Messages.btnAddHost, true);
			createButton(parent, CONNECT_ID, Messages.btnConnectHost, false);

			getButton(ADD_ID).setToolTipText(Messages.btnAddHost);
			getButton(ADD_ID).setEnabled(server != null);
			getButton(CONNECT_ID).setEnabled(server != null);
			getButton(TEST_CONNECT_ID).setEnabled(server != null);
			getButton(CONNECT_ID).setToolTipText(Messages.tipConnectHostButton1);
		} else {
			createButton(parent, TEST_CONNECT_ID, Messages.btnTestConn, false);
			createButton(parent, HostDialog.SAVE_ID, Messages.btnConnectSave,
					false);
			createButton(parent, CONNECT_ID, Messages.btnConnectHost, true);

			getButton(CONNECT_ID).setToolTipText(Messages.tipConnectHostButton2);

			boolean isEnabled = server.getPassword() != null
					&& server.getPassword().trim().length() > 0;

			getButton(CONNECT_ID).setEnabled(isEnabled);
			getButton(TEST_CONNECT_ID).setEnabled(isEnabled);
			getButton(SAVE_ID).setEnabled(!actionIsConnect);
		}

		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnCancel, false);
		if (server != null) {
			valid();
		}
	}

	/**
	 * Call this method when button in button bar is pressed
	 *
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == HostDialog.TEST_CONNECT_ID
				|| buttonId == HostDialog.ADD_ID
				|| buttonId == HostDialog.CONNECT_ID
				|| buttonId == HostDialog.SAVE_ID) {

			String hostName = hostNameText.getText();
			String address = addressText.getText();
			String port = portText.getText();
			String userName = userNameText.getText();
			String password = passwordText.getText();
			String jdbcDriverVersion = jdbcVersionCombo.getText();

			serverInfo = CMHostNodePersistManager.getInstance().getServerInfo(address,
					Integer.parseInt(port), userName);
			if (serverInfo == null) {
				serverInfo = new ServerInfo();
				serverInfo.setHostAddress(address);
				serverInfo.setHostMonPort(Integer.parseInt(port));
				serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
				serverInfo.setUserName(userName);
			} else {
				//use a new ServerInfo instance, and never change the existing ServerInfo. for UX improvement.
				if (buttonId == HostDialog.TEST_CONNECT_ID) {
					serverInfo = new ServerInfo();
					serverInfo.setHostAddress(address);
					serverInfo.setHostMonPort(Integer.parseInt(port));
					serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
					serverInfo.setUserName(userName);
				}
			}
			serverInfo.setUserPassword(password);
			serverInfo.setServerName(hostName);
			serverInfo.setJdbcDriverVersion(jdbcDriverVersion);
			if (btnUseTimeOut.getSelection()) {
				int index = timeOutCombo.getSelectionIndex();
				serverInfo.setSoTimeOut(soTimeOutValues[index]);
			}
		}
		//use the serverInfo as testing connection server info.
		testConnServerInfo = serverInfo;
		if (buttonId == HostDialog.CONNECT_ID) {
			//must release current connection first.
			closeTestServerConnection();
			connect(buttonId);
			if (GeneralPreference.isUseHostDashboard() && serverInfo != null && serverInfo.isConnected()) {
				((HostDashboardAction) ActionManager.getInstance().getAction(HostDashboardAction.ID)).doRun(serverInfo);
			}
		} else if (buttonId == HostDialog.TEST_CONNECT_ID) {
			//must release current connection first.
			closeTestServerConnection();
			connect(buttonId);
		} else if (buttonId == HostDialog.ADD_ID) {
			/*Save operate at the action*/
			QueryOptions.setAutoCommit(serverInfo,
					btnAutoCommit.getSelection());
			closeSaveServerConnection();
			setReturnCode(buttonId);
			close();
		} else if (buttonId == HostDialog.SAVE_ID) {
			CommonUITool.openInformationBox(Messages.msgSaveAndClose);

			server.setLabel(serverInfo.getServerName());
			/*Save operate at the action*/
			QueryOptions.setAutoCommit(serverInfo,
					btnAutoCommit.getSelection());
			/*For bug There is no error message after saving the wrong password.*/
			HostUtils.processHostDisconnected(server);
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(server,
							CubridNodeChangedEventType.SERVER_DISCONNECTED));
			closeSaveServerConnection();
			setReturnCode(buttonId);
			close();
		} else {
			setReturnCode(buttonId);
			close();
		}
	}

	/**
	 *
	 * Close the test server connection
	 *
	 */
	public void closeTestServerConnection() {
		if (testConnServerInfo != null) {
			ServerManager.getInstance().setConnected(
					testConnServerInfo.getHostAddress(),
					testConnServerInfo.getHostMonPort(),
					testConnServerInfo.getUserName(), false);
		}
		//clear and reset the tree view items of the server to avoid troublesome error prompt, for UX improvement.
		if (server != null) {
			CubridWorkbenchContrItem.closeAllEditorAndViewInServer(server,
					false);
			server.removeAllChild();
			TreeViewer viewer = (TreeViewer) LayoutManager.getInstance().getSelectionProvider();
			viewer.refresh(server);
			viewer.setSelection(null, true);
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(server,
							CubridNodeChangedEventType.SERVER_DISCONNECTED));
		}
		testConnServerInfo = null;
	}

	public void closeSaveServerConnection() {
		if (server != null) {
			CubridWorkbenchContrItem.closeAllEditorAndViewInServer(server,
					false);
			server.removeAllChild();
			TreeViewer viewer = (TreeViewer) LayoutManager.getInstance().getSelectionProvider();
			viewer.refresh(server);
			viewer.setSelection(null, true);
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(server,
							CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
		}
	}

	/**
	 *
	 * Execute task and connect the host
	 *
	 * @param buttonId the button id
	 */
	private void connect(final int buttonId) {
		boolean isTest = (HostDialog.TEST_CONNECT_ID == buttonId) ? true : false;
		TaskExecutor taskExcutor = new ConnectHostExecutor(getShell(),
				serverInfo, true, isTest);
		new ExecTaskWithProgress(taskExcutor).exec(true, true);

		if (HostDialog.TEST_CONNECT_ID == buttonId) {
			if (taskExcutor.isSuccess()) {
				CommonUITool.openInformationBox(
						com.cubrid.cubridmanager.ui.common.Messages.titleSuccess,
						Messages.msgTestConnSuccess);
			}
		} else {
			if (taskExcutor.isSuccess()) {
				QueryOptions.setAutoCommit(serverInfo,
						btnAutoCommit.getSelection());

//				if (serverInfo.isCheckCertStatus()
//						&& CertStatus.DEFAULT.equals(serverInfo.getCertStatus())) {
//					GenCertDialog dialog = new GenCertDialog(
//							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
//							serverInfo);
//					dialog.open();
//				}

				setReturnCode(buttonId);
				close();
				} else {
				serverInfo = null;
				if (passwordText != null && !passwordText.isDisposed()) {
					passwordText.selectAll();
					passwordText.setFocus();
				}
			}
		}
	}

	/**
	 * When modify the page content and check the validation
	 *
	 * @param event the modify event
	 */
	public void modifyText(ModifyEvent event) {
		valid();
	}

	/**
	 *
	 * Valid the content
	 *
	 */
	private void valid() {

		String hostName = hostNameText.getText();
		boolean isValidHostName = ValidateUtil.isValidHostName(hostName);
		if (!isValidHostName) {
			setErrorMessage(Messages.errHostName);
			setEnabled(false);
			return;
		}
		boolean hostNameExists = CMHostNodePersistManager.getInstance().isContainedByName(
				hostName, isNewHost ? null : server);
		if (hostNameExists) {
			setErrorMessage(Messages.errHostExist);
			setEnabled(false);
			return;
		}
		String address = addressText.getText();
		boolean isValidAddress = address.indexOf(" ") < 0
				&& address.trim().length() > 0;
		if (!isValidAddress) {
			setErrorMessage(Messages.errAddress);
			setEnabled(false);
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
			setEnabled(false);
			return;
		}
		
		String userName = userNameText.getText();
		boolean isAddressExist = CMHostNodePersistManager.getInstance().isContainedByHostAddress(
				address, port, isNewHost ? null : server);
		boolean isUserNameExist = CMHostNodePersistManager.getInstance().isContainedByUserName(userName);
		
		if(isUserNameExist && isAddressExist && isNewHost){
			setErrorMessage(Messages.errDuplicateHost);
			setEnabled(false);
			return;
		}
		
		boolean isValidUserName = userName.indexOf(" ") < 0
				&& userName.trim().length() >= 4
				&& userName.trim().length() <= ValidateUtil.MAX_NAME_LENGTH;

		if (!isValidUserName) {
			setErrorMessage(Messages.errUserName);
			setEnabled(false);
			return;
		}
		String password = passwordText.getText();
		if (password.trim().length() == 0) {
			setErrorMessage(Messages.errUserPassword);
			setEnabled(false);
			return;
		}

		// if a jdbc driver was not set
		String jdbcDriver = jdbcVersionCombo.getText();
		if (jdbcDriver == null || jdbcDriver.length() == 0) {
			setErrorMessage(Messages.errConnJdbcNotSet);
			return;
		}

		boolean isValidJdbc = jdbcVersionCombo.getText() != null
				&& jdbcVersionCombo.getText().length() > 0;
		if (!isValidJdbc) {
			setEnabled(false);
			return;
		}
		setErrorMessage(null);
		setEnabled(true);

	}

	/**
	 * Enable or disable the button
	 *
	 * @param isEnabled whether it is enabled
	 */
	private void setEnabled(boolean isEnabled) {
		if (isNewHost) {
			getButton(ADD_ID).setEnabled(isEnabled);
		}
		getButton(CONNECT_ID).setEnabled(isEnabled);
		getButton(TEST_CONNECT_ID).setEnabled(isEnabled);
	}

	/**
	 *
	 * Get added CubridServer
	 *
	 * @return the CubridServer object
	 */
	public CubridServer getServer() {
		return server;
	}

	/**
	 *
	 * Set edited CubridServer
	 *
	 * @param server the CubridServer object
	 */
	public void setServer(CubridServer server) {
		this.server = server;
	}

	/**
	 *
	 * Return serverInfo
	 *
	 * @return the ServerInfo object
	 */
	public ServerInfo getServerInfo() {
		return this.serverInfo;
	}

	/**
	 * Save or not save password.
	 *
	 * @return save or not save.
	 */
	public boolean isSavePassword() {
		return isSavePassword;
	}
}
