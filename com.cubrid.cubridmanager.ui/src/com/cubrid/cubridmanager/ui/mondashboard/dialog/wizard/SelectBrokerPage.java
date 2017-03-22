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
import java.util.Locale;
import java.util.Map;

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

import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfos;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.mondashboard.model.HostStatusType;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * 
 * Select broker wizard page
 * 
 * @author pangqiren
 * @version 1.0 - 2010-8-16 created by pangqiren
 */
public class SelectBrokerPage extends
		WizardPage implements
		ModifyListener,
		IPageChangedListener {

	public final static String PAGENAME = "AddHostAndDbWizard/SelectBrokerPage";
	private Button deleteBrokerButton;
	private TableViewer brokerTableViewer;
	private Table brokerTable;
	private List<Map<String, Object>> brokerNodeList = new ArrayList<Map<String, Object>>();
	private Combo brokerNameCombo;
	private Button addButton;
	private HostNode hostNode;
	private ServerInfo serverInfo;
	private Text brokerStatusText;
	private List<BrokerInfo> brokerInfoList;
	private Text brokerPortText;
	private ServerType serverType;
	private Text accessModeText;
	private Text nickNameText;

	/**
	 * The constructor
	 */
	public SelectBrokerPage() {
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

		Group brokerInfoGroup = new Group(composite, SWT.NONE);
		brokerInfoGroup.setText(Messages.grpSelectBroker);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		brokerInfoGroup.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 2;
		brokerInfoGroup.setLayout(layout);

		Label brokerNameLabel = new Label(brokerInfoGroup, SWT.LEFT);
		brokerNameLabel.setText(Messages.lblBrokerName);
		brokerNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		brokerNameCombo = new Combo(brokerInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		brokerNameCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));
		brokerNameCombo.addModifyListener(this);

		Label nickNameLable = new Label(brokerInfoGroup, SWT.LEFT);
		nickNameLable.setText(Messages.lblNickName);
		nickNameLable.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		nickNameText = new Text(brokerInfoGroup, SWT.LEFT | SWT.BORDER);
		nickNameText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		Label brokerPortLabel = new Label(brokerInfoGroup, SWT.LEFT);
		brokerPortLabel.setText(Messages.lblBrokerPort);
		brokerPortLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		brokerPortText = new Text(brokerInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		brokerPortText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

		Label brokerStatusLabel = new Label(brokerInfoGroup, SWT.LEFT);
		brokerStatusLabel.setText(Messages.lblBrokerStatus);
		brokerStatusLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		brokerStatusText = new Text(brokerInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		brokerStatusText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

		Label accessModeLabel = new Label(brokerInfoGroup, SWT.LEFT);
		accessModeLabel.setText(Messages.lblAccessMode);
		accessModeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		accessModeText = new Text(brokerInfoGroup, SWT.LEFT | SWT.BORDER
				| SWT.READ_ONLY);
		accessModeText.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, 100, -1));

		Composite btnComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 5;
		btnComposite.setLayout(rowLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = GridData.END;
		btnComposite.setLayoutData(gridData);

		addButton = new Button(btnComposite, SWT.NONE);
		addButton.setText(Messages.btnAddBroker);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isBrokerNodeExist(brokerNameCombo.getText())) {
					CommonUITool.openErrorBox(getShell(), Messages.errBrokerExist);
					return;
				}
				addBrokerNodeToTable();
			}
		});

		createTable(composite);
		brokerNameCombo.setFocus();
		setTitle(Messages.titleSelectBrokerPage);
		setMessage(Messages.msgSelectBrokerPage);
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
		tipLabel.setText(Messages.lblBrokerListInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		tipLabel.setLayoutData(gridData);

		final String[] columnNameArr = new String[]{Messages.colNickName,
				Messages.colBrokerName, Messages.colBrokerPort,
				Messages.colBrokerStatus, Messages.colAccessMode,
				Messages.colIP, Messages.colPort, Messages.colServerStatus };

		brokerTableViewer = CommonUITool.createCommonTableViewer(parent, null,
				columnNameArr, CommonUITool.createGridData(GridData.FILL_BOTH, 1,
						1, -1, 400));
		brokerTable = brokerTableViewer.getTable();
		for (int i = 0; i < brokerTable.getColumnCount(); i++) {
			brokerTable.getColumn(i).pack();
		}

		brokerTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				deleteBrokerButton.setEnabled(brokerTable.getSelectionCount() > 0);
			}
		});
		brokerTableViewer.setInput(brokerNodeList);

		Composite composite = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 5;
		composite.setLayout(rowLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = GridData.END;
		composite.setLayoutData(gridData);

		deleteBrokerButton = new Button(composite, SWT.NONE);
		deleteBrokerButton.setText(Messages.btnDelete);
		deleteBrokerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				StructuredSelection selection = (StructuredSelection) brokerTableViewer.getSelection();
				if (selection != null && !selection.isEmpty()) {
					brokerNodeList.removeAll(selection.toList());
				}
				brokerTableViewer.refresh();
				deleteBrokerButton.setEnabled(brokerTable.getSelectionCount() > 0);
			}
		});
		deleteBrokerButton.setEnabled(false);
	}

	/**
	 * 
	 * Add broker node to table
	 * 
	 */
	private void addBrokerNodeToTable() {
		String brokerName = brokerNameCombo.getText();
		String nickName = nickNameText.getText();
		BrokerInfo brokerInfo = getBrokerInfo(brokerName);
		if (nickName.trim().length() == 0) {
			nickName = brokerName;
			nickName = brokerInfo == null || brokerInfo.getPort() == null ? nickName
					: nickName + "[" + brokerInfo.getPort() + "]";
		}
		BrokerNode brokerNode = new BrokerNode();
		brokerNode.setName(nickName);
		brokerNode.setBrokerName(brokerName);
		brokerNode.setBrokerInfo(brokerInfo);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("0", nickName);
		map.put("1", brokerNode.getBrokerName());
		map.put("2", brokerInfo == null ? "" : brokerInfo.getPort());
		map.put("3", brokerInfo == null ? "" : brokerInfo.getState());
		map.put("4", brokerInfo == null ? "" : brokerInfo.getAccess_mode());
		map.put("5", hostNode.getIp());
		map.put("6", hostNode.getPort());
		map.put(
				"7",
				HostStatusType.getShowText(hostNode.getHostStatusInfo().getStatusType()));
		map.put("8", hostNode);
		map.put("9", brokerNode);
		brokerNodeList.add(map);

		brokerTableViewer.refresh();
		for (int i = 0; i < brokerTable.getColumnCount(); i++) {
			brokerTable.getColumn(i).pack();
		}
	}

	/**
	 * 
	 * Whether broker node exist
	 * 
	 * @param brokerName String
	 * @return boolean
	 */
	private boolean isBrokerNodeExist(String brokerName) {
		for (int i = 0; i < brokerNodeList.size(); i++) {
			Map<String, Object> map = brokerNodeList.get(i);
			HostNode hostNode1 = (HostNode) map.get("8");
			BrokerNode brokerNode1 = (BrokerNode) map.get("9");
			if (hostNode.getIp().equals(hostNode1.getIp())
					&& hostNode.getPort().equals(hostNode1.getPort())
					&& hostNode.getUserName().equals(hostNode1.getUserName())
					&& brokerName.equalsIgnoreCase(brokerNode1.getBrokerName())) {
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
		String brokerName = brokerNameCombo.getText();
		if (brokerName.trim().length() == 0) {
			addButton.setEnabled(false);
			brokerStatusText.setText("");
			brokerPortText.setText("");
			accessModeText.setText("");
		} else {
			BrokerInfo brokerInfo = getBrokerInfo(brokerName);
			nickNameText.setText(brokerName);
			brokerPortText.setText(brokerInfo == null ? ""
					: brokerInfo.getPort());
			brokerStatusText.setText(brokerInfo == null ? ""
					: brokerInfo.getState());
			accessModeText.setText(brokerInfo == null
					|| brokerInfo.getAccess_mode() == null ? ""
					: brokerInfo.getAccess_mode().toUpperCase(
							Locale.getDefault()));
			addButton.setEnabled(true);
		}
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
			BrokerInfos brokerInfos = serverInfo.getBrokerInfos();
			brokerInfoList = (brokerInfos == null || brokerInfos.getBorkerInfoList() == null) ? null
					: brokerInfos.getBorkerInfoList().getBrokerInfoList();
			BrokerInfo brokerInfo = null;
			brokerNameCombo.removeAll();
			for (int i = 0; brokerInfos != null && i < brokerInfoList.size(); i++) {
				if (i == 0) {
					brokerInfo = brokerInfoList.get(i);
				}
				brokerNameCombo.add(brokerInfoList.get(i).getName());
			}
			if (brokerNameCombo.getItemCount() > 0) {
				brokerNameCombo.select(0);
				brokerStatusText.setText(brokerInfo.getState());
				nickNameText.setText(brokerInfo.getName());
				addButton.setEnabled(true);
			} else {
				addButton.setEnabled(false);
			}
			brokerNameCombo.setFocus();
		}
	}

	/**
	 * 
	 * Get broker info
	 * 
	 * @param brokerName String
	 * @return BrokerInfo
	 */
	private BrokerInfo getBrokerInfo(String brokerName) {
		for (int i = 0; brokerInfoList != null && i < brokerInfoList.size(); i++) {
			BrokerInfo brokerInfo = brokerInfoList.get(i);
			String name = brokerInfoList.get(i).getName();
			if (brokerName.equalsIgnoreCase(name)) {
				return brokerInfo;
			}
		}
		return null;
	}

	public List<Map<String, Object>> getBrokerNodeList() {
		return brokerNodeList;
	}

	public void setBrokerNodeList(List<Map<String, Object>> brokerNodeList) {
		this.brokerNodeList = brokerNodeList;
	}

	public HostNode getHostNode() {
		return hostNode;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

	/**
	 * Get previous page
	 * 
	 * @return IWizardPage
	 */
	public IWizardPage getPreviousPage() {
		AddHostAndDbWizard wizard = ((AddHostAndDbWizard) getWizard());
		int type = wizard.getAddedType();
		if (type == 0 && serverType == ServerType.BOTH) {
			return wizard.getSelectDbPage();
		} else {
			return wizard.getSetHostInfoPage();
		}
	}
}
