/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.IModelAttributeConstants;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.common.Messages;
import com.cubrid.cubridmanager.ui.common.navigator.CubridHostNavigatorView;
import com.cubrid.cubridmanager.ui.host.action.ActionExecutor;
import com.cubrid.cubridmanager.ui.host.editor.ServerInfoTableViewerContentProvider;
import com.cubrid.cubridmanager.ui.host.editor.ServerInfoTableViewerLabelProvider;
import com.cubrid.cubridmanager.ui.spi.persist.CMGroupNodePersistManager;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * Import and export connection informations for CM
 */
public class ImportExportConnectionDialog extends CMTitleAreaDialog {
	private static final Logger LOGGER = LogUtil.getLogger(ImportExportConnectionDialog.class);
	private static final String SERVER_XML_CONTENT = "CUBRID_SERVERS";
	private int WIDTH_NAME = 180;
	private int WIDTH_ADDRESS = 110;
	private int WIDTH_PORT = 80;
	private int WIDTH_JDBC = 150;
	private int WIDTH_USER = 60;
//	private int WIDTH_SAVEPASS = 90;
	private static final String EMPTY_STR = "";
	private final String serverOnSelection = "SERVERONSELECTION";
	private Label lblFromFolder;
	private Text txFromFolder;
	private Button btnBrowser;
	private CheckboxTableViewer hostListView;
	private boolean selectAll;
	private boolean isExport = true;

	public ImportExportConnectionDialog(Shell parentShell, boolean isExport) {
		super(parentShell);
		this.isExport = isExport;
		selectAll = isExport;
	}

	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);
		composite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
		}

		if (!isExport) {
			lblFromFolder = new Label(composite, SWT.SHADOW_IN);
			lblFromFolder.setText(Messages.lblFromFile);
			lblFromFolder.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

			GridData fromData = CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1);
			fromData.horizontalIndent = 0;
			txFromFolder = new Text(composite, SWT.BORDER | SWT.LEFT | SWT.READ_ONLY);
			txFromFolder.setLayoutData(fromData);

			GridData browserData = CommonUITool.createGridData(1, 1, -1, -1);
			browserData.horizontalIndent = 10;
			browserData.widthHint = 80;
			btnBrowser = new Button(composite, SWT.CENTER);
			btnBrowser.setText(Messages.lblBrowser);
			btnBrowser.setLayoutData(browserData);
			btnBrowser.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getParentShell(), (isExport) ? SWT.SAVE : SWT.OPEN);
					dialog.setFilterExtensions(new String[]{ "*.xml" });
					String fileName = dialog.open();
					if (fileName != null) {
						txFromFolder.setText(fileName);
					}

					if (!isExport) {
						parseFile(fileName);
					}
				}
			});
		}
		createTable(composite);

		setMessage((isExport) ? Messages.msgExportServer : Messages.msgImportServer);
		setTitle((isExport) ? Messages.tlExportServer : Messages.tlImportServer);
		getShell().setText((isExport) ? Messages.tlExportServer : Messages.tlImportServer);

		return parentComp;
	}

	private void createTable(Composite composite) {
		hostListView = CheckboxTableViewer.newCheckList(composite, SWT.V_SCROLL
				| SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		hostListView.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if(isExport){
					valid();
				}
			}
		});

		final GridData gdColumnsTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdColumnsTable.horizontalSpan = 3;
		gdColumnsTable.heightHint = 189;
		hostListView.getTable().setLayoutData(gdColumnsTable);
		hostListView.getTable().setLinesVisible(true);
		hostListView.getTable().setHeaderVisible(true);

		final TableColumn hostName = new TableColumn(hostListView.getTable(), SWT.NONE);
		hostName.setAlignment(SWT.LEFT);
		hostName.setWidth(WIDTH_NAME);
		hostName.setText(Messages.columnHeaderServerInfo);
		if (isExport) {
			hostName.setImage(CommonUIPlugin.getImage("icons/checked_green.png"));
		} else {
			hostName.setImage(CommonUIPlugin.getImage("icons/unchecked.gif"));
		}
		hostName.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				selectAll = !selectAll;
				hostListView.setAllChecked(selectAll);
				Image image = selectAll ? CommonUIPlugin.getImage("icons/checked_green.png")
						: CommonUIPlugin.getImage("icons/unchecked.gif");
				hostName.setImage(image);
				hostListView.refresh();
				if(isExport){
					valid();
				}
			}
		});

		final TableColumn hostAddress = new TableColumn(hostListView.getTable(), SWT.NONE);
		hostAddress.setAlignment(SWT.CENTER);
		hostAddress.setWidth(WIDTH_ADDRESS);
		hostAddress.setText(Messages.columnHeaderServerAddress);

		final TableColumn hostPort = new TableColumn(hostListView.getTable(), SWT.NONE);
		hostPort.setAlignment(SWT.CENTER);
		hostPort.setWidth(WIDTH_PORT);
		hostPort.setText(Messages.columnHeaderServerPort);

		final TableColumn jdbcVer = new TableColumn(hostListView.getTable(), SWT.NONE);
		jdbcVer.setAlignment(SWT.CENTER);
		jdbcVer.setWidth(WIDTH_JDBC);
		jdbcVer.setText(Messages.columnHeaderServerJdbcVersion);

		final TableColumn hostUser = new TableColumn(hostListView.getTable(), SWT.NONE);
		hostUser.setAlignment(SWT.CENTER);
		hostUser.setWidth(WIDTH_USER);
		hostUser.setText(Messages.columnHeaderServerUserName);

//		final TableColumn savedPass = new TableColumn(hostListView.getTable(), SWT.NONE);
//		savedPass.setAlignment(SWT.CENTER);
//		savedPass.setWidth(WIDTH_SAVEPASS);
//		savedPass.setText(Messages.columnHeaderServerAutoSave);

		hostListView.setContentProvider(new ServerInfoTableViewerContentProvider());
		hostListView.setLabelProvider(new ServerInfoTableViewerLabelProvider());

		if (isExport) {
			initExportTableData();
		}
	}

	/**
	 * Init export data
	 */
	private void initExportTableData() {
		List<CubridServer> servers = CMHostNodePersistManager.getInstance().getAllServers();
		for (CubridServer server : servers) {
			server.setData(serverOnSelection,true);
		}
		hostListView.setInput(servers);
		hostListView.setAllChecked(true);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	protected void okPressed() {
		ActionExecutor executor = null;
		if (isExport) {
			FileDialog dialog = new FileDialog(getParentShell(), (isExport) ? SWT.SAVE : SWT.OPEN);
			dialog.setText(Messages.exportServerSelectPathMsg);
			dialog.setFilterExtensions(new String[]{ "*.xml" });
			dialog.setFileName("export_servers");
			String fileName = dialog.open();
			if (fileName == null) {
				return;
			}
			executor = new ExportExecutor(fileName);
		} else {
			executor = new ImportExecutor();
		}
		
		if (executor.execute()) {
			String msg = isExport ? Messages.exportServerSuccessMsg : Messages.importServerSuccessMsg;
			CommonUITool.openInformationBox(com.cubrid.common.ui.common.Messages.titleSuccess, msg);
			super.okPressed();
		}
	}

	private static class ImportTask extends AbstractTask {
		private List<CubridServer> input;
		private boolean isSuccess = false;
		private boolean isCancel = false;

		public ImportTask(List<CubridServer> input) {
			this.input = input;
		}

		public void execute() {
			createHosts();
			createGroups();
		}

		private void createHosts() {
			IXMLMemento memento = PersistUtils.getXMLMemento(
					CubridManagerUIPlugin.PLUGIN_ID, SERVER_XML_CONTENT);
			if (memento == null) {
				memento = XMLMemento.createWriteRoot("hosts");
			}

			for (CubridServer ui : input) {
				IXMLMemento child = memento.createChild("host");
				child.putString(IModelAttributeConstants.SERVER_INFO_ADDRESS, ui.getHostAddress());
				child.putString(IModelAttributeConstants.SERVER_INFO_ID, ui.getId());
				child.putString(IModelAttributeConstants.SERVER_INFO_JDBC_DRV, ui.getJdbcDriverVersion());
				child.putString(IModelAttributeConstants.SERVER_INFO_PORT, ui.getMonPort());
				child.putString(IModelAttributeConstants.SERVER_INFO_NAME, ui.getName());
				child.putString(IModelAttributeConstants.SERVER_INFO_PASS, CipherUtils.encrypt(ui.getPassword()));
				child.putString(IModelAttributeConstants.SERVER_INFO_USER, ui.getUserName());
				child.putString(IModelAttributeConstants.SERVER_INFO_SAVE_PASS, ui.isAutoSavePassword() + "");
			}

			PersistUtils.saveXMLMemento(CubridManagerUIPlugin.PLUGIN_ID,
					SERVER_XML_CONTENT, (XMLMemento) memento);
		}

		private void createGroups() {
			IXMLMemento memento = PersistUtils.getXMLMemento(
					CubridManagerUIPlugin.PLUGIN_ID,
					CMGroupNodePersistManager.COM_CUBRID_MANAGER_HOSTGROUP);
			if (memento == null) {
				memento = XMLMemento.createWriteRoot("groups");
			}

			IXMLMemento grpNode = null;
			IXMLMemento[] children = memento.getChildren("group");
			boolean willCreateDefaultGroup = true;
			if (children != null && children.length > 0) {
				for (int i = 0; i < children.length; i++) {
					IXMLMemento child = children[i];
					if (child.getString("id") != null && child.getString("id").equals("Default Group")) {
						willCreateDefaultGroup = false;
						grpNode = child;
						break;
					}
				}
			}

			if (willCreateDefaultGroup) {
				IXMLMemento child = memento.createChild("group");
				child.putString("id", "Default Group");
				child.putString("name", "Default Group");
				grpNode = child;
			}

			for (CubridServer ui : input) {
				IXMLMemento item = grpNode.createChild("item");
				item.putString("id", ui.getId());
			}

			PersistUtils.saveXMLMemento(CubridManagerUIPlugin.PLUGIN_ID,
					CMGroupNodePersistManager.COM_CUBRID_MANAGER_HOSTGROUP,
					(XMLMemento) memento);
		}

		public void cancel() {
			isCancel = true;
		}

		public void finish() {
			isSuccess = true;
		}

		public boolean isCancel() {
			return isCancel;
		}

		public boolean isSuccess() {
			return isSuccess;
		}
	}

	private class ImportExecutor implements ActionExecutor {
		public boolean execute() {
			String file = txFromFolder.getText();
			if (file == null || EMPTY_STR.equals(file)) {
				CommonUITool.openErrorBox(Messages.msgErrorMissingImportFile);
				txFromFolder.forceFocus();
				return false;
			}

			Object[] objects = hostListView.getCheckedElements();
			List<CubridServer> input = new ArrayList<CubridServer>();
			for (Object object : objects) {
				if (object != null && object instanceof CubridServer)
					input.add((CubridServer) object);
			}

			if (input.size() == 0) {
				CommonUITool.openErrorBox(Messages.msgErrorMissingImportServer);
				hostListView.getTable().forceFocus();
				return false;
			}

			TaskExecutor executor = new CommonTaskExec(Messages.taskNameImportServer);
			executor.addTask(new ImportTask(input));
			new ExecTaskWithProgress(executor).exec();
			if (executor.isSuccess()) {
				refreshTreeView();
				return true;
			} else {
				return false;
			}
		}

		private void refreshTreeView() {
			CMHostNodePersistManager.getInstance().reloadServers();
			CMGroupNodePersistManager.getInstance().reloadGroups();

			CubridHostNavigatorView view = (CubridHostNavigatorView) CubridNavigatorView.getNavigatorView(CubridHostNavigatorView.ID);
			if (view != null && view.getViewer() != null) {
				view.getViewer().refresh(true);
			}
		}
	}

	private static class ExportTask extends AbstractTask {
		private List<CubridServer> exports;
		private String fileName;
		private boolean isCancel = false;
		private boolean isSuccess = false;

		public ExportTask(String fileName, List<CubridServer> exports) {
			this.setTaskname(Messages.taskNameExportServer);
			this.exports = exports;
			this.fileName = fileName;
		}

		public void execute() {
			CMHostNodePersistManager.getInstance().saveServer(exports, fileName);
		}

		public void cancel() {
			isCancel = true;
		}

		public void finish() {
			isSuccess = true;
		}

		public boolean isCancel() {
			return isCancel;
		}

		public boolean isSuccess() {
			return isSuccess;
		}
	}

	private class ExportExecutor implements ActionExecutor {
		private final String filePath;

		public ExportExecutor (String filePath) {
			this.filePath = filePath;
		}

		public boolean execute() {
			File f = new File(filePath);
			try {
				if(f.exists()){
					if(!CommonUITool.openConfirmBox(Messages.msgConfirmExistFile)){
						return false;
					}
				}
				f.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			Object[] objects = hostListView.getCheckedElements();
			if (objects.length == 0) {
				return false;
			}

			List<CubridServer> exports = new ArrayList<CubridServer>();
			for (Object object : objects) {
				if (object != null && object instanceof CubridServer) {
					exports.add((CubridServer) object);
				}
			}

			if (exports.size() == 0) {
				CommonUITool.openErrorBox(Messages.msgErrorMissingExportServer);
				return false;
			}

			TaskExecutor taskExecutor = new CommonTaskExec(Messages.taskNameExportServer);
			taskExecutor.addTask(new ExportTask(filePath, exports));
			new ExecTaskWithProgress(taskExecutor).exec();

			return true;
		}
	}

	protected void parseFile(String fileName) {
		int repeatCount = 0;
		List<CubridServer> newServerList = new ArrayList<CubridServer>();
		File file = new File(fileName);
		if (!file.exists()) {
			return;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			IXMLMemento xmlContent = XMLMemento.loadMemento(fis);
			List<CubridServer> list = new ArrayList<CubridServer>();
			CMHostNodePersistManager.getInstance().loadServers(xmlContent, false, EMPTY_STR, list);
			for (CubridServer server : list) {
				if (!isContainSameHost(server)) {
					newServerList.add(server);
				} else {
					repeatCount++;
				}
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			FileUtil.close(fis);
		}

		if (repeatCount > 0) {
			setMessage(Messages.bind(Messages.msgInfoSame, repeatCount),
					IMessageProvider.WARNING);
		} else {
			setMessage(null);
		}

		hostListView.setInput(newServerList);
		hostListView.refresh();
	}

	/**
	 * Judge is contain same host in current workspace
	 *
	 * @param server
	 * @return
	 */
	public boolean isContainSameHost(CubridServer server) {
		for (CubridServer temp : CMHostNodePersistManager.getInstance().getAllServers()) {
			if (temp.getHostAddress().equals(server.getHostAddress())
					&& temp.getMonPort().equals(server.getMonPort())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check whether there are some elements be selected or not
	 */
	private void valid() {
		if(hostListView.getCheckedElements().length <= 0){
			getButton(OK).setEnabled(false);
		} else {
			getButton(OK).setEnabled(true);
		}
	}
}
