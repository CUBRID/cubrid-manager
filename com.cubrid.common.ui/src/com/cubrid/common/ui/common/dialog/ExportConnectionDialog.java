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
package com.cubrid.common.ui.common.dialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;
import org.slf4j.Logger;

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.cubrid.table.control.XlsxWriterHelper;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.NodeUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * Export connection dialog
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 13, 2012 created by Kevin.Wang
 */
public class ExportConnectionDialog extends CMTitleAreaDialog {
	private static final Logger LOGGER = LogUtil.getLogger(ExportConnectionDialog.class);
	private CheckboxTreeViewer tableViewer;
	private List<ICubridNode> selections;
	private boolean selectAll = true;
	private final int COPY_CLIPBOARD_ID = 100;

	/**
	 * @param parentShell
	 */
	public ExportConnectionDialog(Shell parentShell, List<ICubridNode> selections) {
		super(parentShell);
		this.selections = selections;
	}

	/**
	 * Create dialog area content
	 *
	 * @param parent the parent composite
	 * @return the control
	 */
	@SuppressWarnings("deprecation")
	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		tableViewer = new CheckboxTreeViewer(composite, SWT.V_SCROLL
				| SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getTree().setLinesVisible(true);
		tableViewer.getTree().setHeaderVisible(true);
		tableViewer.getTree().setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

		/* Name */
		final TreeColumn nameColumn = new TreeColumn(tableViewer.getTree(), SWT.LEFT);
		nameColumn.setText(Messages.nameColumn);
		nameColumn.setWidth(120);

		nameColumn.setImage(CommonUIPlugin.getImage("icons/checked_green.png"));
		nameColumn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				selectAll = !selectAll;
				tableViewer.setAllChecked(selectAll);
				Image image = selectAll ? CommonUIPlugin.getImage("icons/checked_green.png")
						: CommonUIPlugin.getImage("icons/unchecked.gif");
				nameColumn.setImage(image);
				tableViewer.refresh();
				updateWidgetStatus();
			}
		});

		/* IP */
		TreeColumn ipColumn = new TreeColumn(tableViewer.getTree(), SWT.LEFT);
		ipColumn.setText(Messages.iPColumn);
		ipColumn.setWidth(100);

		/* Port */
		TreeColumn portColumn = new TreeColumn(tableViewer.getTree(), SWT.LEFT);
		portColumn.setText(Messages.portColumn);
		portColumn.setWidth(80);

		/* DB user */
		TreeColumn userColumn = new TreeColumn(tableViewer.getTree(), SWT.LEFT);
		userColumn.setText(Messages.userColumn);
		userColumn.setWidth(100);

		/* Comment */
		TreeColumn commentColumn = new TreeColumn(tableViewer.getTree(), SWT.LEFT);
		commentColumn.setText(Messages.commentColumn);
		commentColumn.setWidth(120);

		/* Java Url */
		TreeColumn javaUrlColumn = new TreeColumn(tableViewer.getTree(), SWT.LEFT);
		javaUrlColumn.setText(Messages.javaUrlColumn);
		javaUrlColumn.setWidth(300);

		TreeColumn phpUrlColumn = new TreeColumn(tableViewer.getTree(), SWT.LEFT);
		phpUrlColumn.setText(Messages.phpUrlColumn);
		phpUrlColumn.setWidth(300);

		tableViewer.setContentProvider(new TreeViewerContentProvider());
		tableViewer.setLabelProvider(new TreeViewerLabelProvider());
		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					CheckboxTreeViewer viewer = (CheckboxTreeViewer) event.getSource();
					/* Select the sub node */
					if (event.getElement() instanceof CubridServer) {
						viewer.setSubtreeChecked(event.getElement(), true);
					} else if (event.getElement() instanceof CubridDatabase) {
						CubridDatabase database = (CubridDatabase) event.getElement();
						viewer.setChecked(database.getServer(), true);
					}
				} else {
					CheckboxTreeViewer viewer = (CheckboxTreeViewer) event.getSource();
					ICubridNode element = (ICubridNode) event.getElement();
					viewer.setSubtreeChecked(element, false);
					if (element instanceof CubridDatabase) {
						CubridDatabase database = (CubridDatabase) element;
						/* Check the parent state */
						ICubridNode parent = database.getParent();
						if (parent != null) {
							List<ICubridNode> children = parent.getChildren();
							boolean isSelected = false;
							for (ICubridNode child : children) {
								DefaultSchemaNode childNode = (DefaultSchemaNode) child;
								if (viewer.getChecked(childNode.getDatabase())) {
									isSelected = true;
									break;
								}
							}
							viewer.setChecked(database.getServer(), isSelected);
						}
					}
				}

				updateWidgetStatus();
			}
		});


		tableViewer.setInput(selections);
		tableViewer.expandAll();
		tableViewer.setAllChecked(true);

		setTitle(Messages.titleExportConnection);
		setMessage(Messages.msgExportConnection);

		return parentComp;
	}

	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setSize(640, 480);
		CommonUITool.centerShell(getShell());
		getShell().setText(Messages.titleExportConnection);
	}

	private void updateWidgetStatus() {
		if (getButton(IDialogConstants.OK_ID) != null) {
			if (getCheckedDatabases().size() > 0) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
				getButton(COPY_CLIPBOARD_ID).setEnabled(true);
			} else {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				getButton(COPY_CLIPBOARD_ID).setEnabled(false);
			}
		}
	}

	/**
	 * Create buttons for button bar
	 *
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, COPY_CLIPBOARD_ID, Messages.expConDialogCopyBtnLabel, false);
		createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
	}

	/**
	 * Call this method when the button in button bar is pressed
	 *
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == COPY_CLIPBOARD_ID) {
			List<CubridDatabase> databaseList = getCheckedDatabases();
			if (databaseList.size() <= 0) {
				CommonUITool.openErrorBox(getShell(), Messages.expConDialogCopyErrorMsg);
				return;
			}

			StringBuilder sb = new StringBuilder();
			for (CubridDatabase db : databaseList) {
				if (sb.length() > 0) {
					sb.append(StringUtil.NEWLINE);
				}
				sb.append(NodeUtil.getJavaConnectionUrl(db.getDatabaseInfo()));
			}

			TextTransfer textTransfer = TextTransfer.getInstance();
			Clipboard clipboard = CommonUITool.getClipboard();
			clipboard.setContents(new Object[] { sb.toString() }, new Transfer[] { textTransfer });

			CommonUITool.openInformationBox(Messages.titleSuccess, Messages.expConDialogCopySuccessMsg);
			return;
		} else if (buttonId == IDialogConstants.OK_ID) {
			if (!verify()) {
				return;
			}

			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE | SWT.APPLICATION_MODAL);
			String[] filterExtensions = new String[]{"*.xls"};
			dialog.setFilterExtensions(filterExtensions); //Windows wild cards

			String fileName = dialog.open();
			if (fileName == null) {
				return;
			}

			/*Process the file extensions*/
			if (!ExportConnectionUtil.isTxtFile(fileName)
					&& !ExportConnectionUtil.isXlsFile(fileName)
					&& !ExportConnectionUtil.isXlsxFile(fileName)) {
				int filterIndex = dialog.getFilterIndex();
				if (filterIndex == 0 || filterIndex == 2) {
					fileName = fileName + ".xls";
				} else if (filterIndex == 1) {
					fileName = fileName + ".txt";
				}
			}

			TaskExecutor taskExec = new CommonTaskExec(Messages.nameExportConnectionTask);
			File file = new File(fileName);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					LOGGER.error("Create file failed:" + e.getMessage());
				}
			}

			ExportConnectionsTask task = new ExportConnectionsTask(getCheckedDatabases(), file);
			taskExec.addTask(task);
			new ExecTaskWithProgress(taskExec).busyCursorWhile();
			if (taskExec.isSuccess()) {
				CommonUITool.openInformationBox(Messages.titleSuccess, Messages.msgConnectionUrlExported);
				super.okPressed();
			}
		}

		super.buttonPressed(buttonId);
	}

	/**
	 * Verify
	 *
	 * @return
	 */
	private boolean verify() {
		setErrorMessage(null);

		if (getCheckedDatabases().size() == 0) {
			setErrorMessage(Messages.errNoDatabaseSelected);
			return false;
		}

		return true;
	}

	/**
	 * Get all checked database
	 *
	 * @return
	 */
	private List<CubridDatabase> getCheckedDatabases() {
		List<CubridDatabase> databaseList = new ArrayList<CubridDatabase>();
		Object[] selection = tableViewer.getCheckedElements();
		for (Object obj : selection) {
			if (obj instanceof CubridDatabase) {
				databaseList.add((CubridDatabase) obj);
			}
		}

		return databaseList;
	}
}

/**
 * TreeViewer Content Provider
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 15, 2012 created by Kevin.Wang
 */
class TreeViewerContentProvider implements ITreeContentProvider {
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@SuppressWarnings("rawtypes")
	public Object[] getElements(Object element) {
		if (element instanceof List) {
			return ((List) element).toArray();
		} else {
			return new Object[0];
		}
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof CubridServer) {
			List<CubridDatabase> databaseList = new ArrayList<CubridDatabase>();
			addToDatabaseList((CubridServer) parentElement, databaseList);
			return databaseList.toArray();
		} else if (parentElement instanceof CubridGroupNode) {
			List<CubridDatabase> databaseList = new ArrayList<CubridDatabase>();
			addToDatabaseList((CubridGroupNode) parentElement, databaseList);
			return databaseList.toArray();
		}

		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof CubridServer) {
			List<CubridDatabase> databaseList = new ArrayList<CubridDatabase>();
			addToDatabaseList((CubridServer) element, databaseList);
			return databaseList.toArray().length > 0;
		} else if (element instanceof CubridGroupNode) {
			List<CubridDatabase> databaseList = new ArrayList<CubridDatabase>();
			addToDatabaseList((CubridGroupNode) element, databaseList);
			return databaseList.toArray().length > 0;
		}

		return false;
	}

	/**
	 * Add the CubridDatabase to databaseList
	 *
	 * @param cubridDatabase
	 */
	private void addToDatabaseList(CubridDatabase cubridDatabase,
			List<CubridDatabase> databaseList) {
		databaseList.add(cubridDatabase);
	}

	private void addToDatabaseList(CubridServer cubridServer, List<CubridDatabase> databaseList) {
		if (!cubridServer.isConnected()) {
			return;
		}

		List<ICubridNode> children = cubridServer.getChildren();
		if (children == null) {
			return;
		}

		for (ICubridNode child : children) {
			if (child instanceof DefaultCubridNode) {
				List<ICubridNode> dbChildren = ((DefaultCubridNode) child).getChildren();
				for (ICubridNode dbChild : dbChildren) {
					if (dbChild instanceof CubridDatabase) {
						databaseList.add((CubridDatabase) dbChild);
					}
				}
			}
		}
	}

	private void addToDatabaseList(CubridGroupNode groupNode, List<CubridDatabase> databaseList) {
		List<ICubridNode> children = groupNode.getChildren();
		if (children == null) {
			return;
		}

		for (ICubridNode child : children) {
			if (child instanceof CubridDatabase) {
				addToDatabaseList((CubridDatabase) child, databaseList);
			}

			if (child instanceof CubridServer) {
				addToDatabaseList((CubridServer) child, databaseList);
			}
		}
	}
}

/**
 * TreeViewer Label Provider
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 15, 2012 created by Kevin.Wang
 */
class TreeViewerLabelProvider implements ITableLabelProvider {
	private static final Logger LOGGER = LogUtil.getLogger(ExportConnectionDialog.class);
	private boolean isCMMode = PerspectiveManager.getInstance().isManagerMode();
	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof CubridServer && columnIndex == 0) {
			CubridServer server = (CubridServer) element;
			return server.getName();
		}

		if (element instanceof CubridDatabase) {
			CubridDatabase database = (CubridDatabase) element;
			DatabaseInfo dbInfo = database.getDatabaseInfo();
			if (dbInfo == null) {
				LOGGER.warn("DatabaseInfo is a null.");
				return "";
			}

			switch (columnIndex) {
			case 0:
				return dbInfo.getDbName();
			case 1:
				return dbInfo.getBrokerIP();
			case 2:
				return dbInfo.getBrokerPort();
			case 3:
				return dbInfo.getAuthLoginedDbUserInfo().getName();
			case 4:
				DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, isCMMode);
				if (editorConfig != null) {
					return editorConfig.getDatabaseComment() == null ? ""
							: editorConfig.getDatabaseComment();
				}
				return "";
			case 5:
				return NodeUtil.getJavaConnectionUrl(database.getDatabaseInfo());
			case 6:
				return NodeUtil.getPHPConnectionUrl(database.getDatabaseInfo());
			}
		}

		return null;
	}
}

/**
 * Export Connections Task Description
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 15, 2012 created by Kevin.Wang
 */
class ExportConnectionsTask extends AbstractTask {
	private static final Logger LOGGER = LogUtil.getLogger(ExportConnectionsTask.class);
	private List<CubridDatabase> input;
	private File file;
	private boolean isSuccess = false;
	private boolean isCancel = false;
	boolean managerMode = PerspectiveManager.getInstance().isManagerMode();
	ExportConnectionsTask(List<CubridDatabase> input, File file) {
		this.input = input;
		this.file = file;
	}

	public void execute() {
		/* xls */
		if (ExportConnectionUtil.isXlsFile(file.getName())) {
			try {
				writeToXls();
			} catch (RowsExceededException e) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						CommonUITool.openConfirmBox(Messages.msgExportConnectionFailed);
					}
				});
				LOGGER.error("Export connections error:" + e.getMessage());
			} catch (WriteException e) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						CommonUITool.openConfirmBox(Messages.msgExportConnectionFailed);
					}
				});
				LOGGER.error("Export connections error:" + e.getMessage());
			} catch (IOException e) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						CommonUITool.openConfirmBox(Messages.msgExportConnectionFailed);
					}
				});
				LOGGER.error("Export connections error:" + e.getMessage());
			}
			return;
		}

		/* xlsx */
		if (ExportConnectionUtil.isXlsxFile(file.getName())) {
			try {
				writeToXlsx();
			} catch (IOException e) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						CommonUITool.openConfirmBox(Messages.msgExportConnectionFailed);
					}
				});
				LOGGER.error("Export connections error:" + e.getMessage());
			}
			return;
		}

		/* txt */
		if (ExportConnectionUtil.isTxtFile(file.getName())) {
			try {
				writeToTxt();
			} catch (IOException e) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						CommonUITool.openConfirmBox(Messages.msgExportConnectionFailed);
					}
				});
				LOGGER.error("Export connections error:" + e.getMessage());
			}
			return;
		}
	}

	private void writeToTxt() throws IOException {
		int[] columnMaxLength = {0, 0, 0, 0, 0, 0, 0, 0 };
		columnMaxLength[0] = Messages.nameColumn.length();
		columnMaxLength[1] = Messages.iPColumn.length();
		columnMaxLength[2] = Messages.portColumn.length();
		columnMaxLength[3] = Messages.userColumn.length();
		columnMaxLength[4] = Messages.commentColumn.length();
		columnMaxLength[5] = Messages.javaUrlColumn.length();
		columnMaxLength[6] = Messages.phpUrlColumn.length();

		for (CubridDatabase database : input) {
			if (database == null) {
				continue;
			}

			DatabaseInfo dbInfo = database.getDatabaseInfo();
			if (dbInfo == null) {
				continue;
			}

			if (dbInfo.getDbName() != null && columnMaxLength[0] < dbInfo.getDbName().length()) {
				columnMaxLength[0] = dbInfo.getDbName().length();
			}

			if (dbInfo.getBrokerIP() != null && columnMaxLength[1] < dbInfo.getBrokerIP().length()) {
				columnMaxLength[1] = dbInfo.getBrokerIP().length();
			}

			if (dbInfo.getBrokerPort() != null && columnMaxLength[2] < dbInfo.getBrokerPort().length()) {
				columnMaxLength[2] = dbInfo.getBrokerPort().length();
			}

			if (columnMaxLength[3] < getDbUser(dbInfo).length()) {
				columnMaxLength[3] = getDbUser(dbInfo).length();
			}

			DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, managerMode);
			if (editorConfig != null && editorConfig.getDatabaseComment() != null) {
				if (columnMaxLength[4] < editorConfig.getDatabaseComment().length()) {
					columnMaxLength[4] = editorConfig.getDatabaseComment().length();
				}
			}

			String javaUrl = NodeUtil.getJavaConnectionUrl(dbInfo);
			if (javaUrl != null && columnMaxLength[5] < javaUrl.length()) {
				columnMaxLength[5] = javaUrl.length();
			}

			String phpUrl = NodeUtil.getPHPConnectionUrl(dbInfo);
			if (phpUrl != null && columnMaxLength[6] < phpUrl.length()) {
				columnMaxLength[6] = phpUrl.length();
			}
		}

		BufferedWriter fs = null;
		try {
			fs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

			/* Write the header */
			fs.write(getWrapValue(Messages.nameColumn, columnMaxLength[0]));
			fs.write(getWrapValue(Messages.iPColumn, columnMaxLength[1]));
			fs.write(getWrapValue(Messages.portColumn, columnMaxLength[2]));
			fs.write(getWrapValue(Messages.userColumn, columnMaxLength[3]));
			fs.write(getWrapValue(Messages.commentColumn, columnMaxLength[4]));
			fs.write(getWrapValue(Messages.javaUrlColumn, columnMaxLength[5]));
			fs.write(getWrapValue(Messages.phpUrlColumn, columnMaxLength[6]));
			fs.write(StringUtil.NEWLINE);

			/* Write the data */
			for (CubridDatabase database : input) {
				if (database == null) {
					continue;
				}

				DatabaseInfo dbInfo = database.getDatabaseInfo();
				if (dbInfo == null) {
					continue;
				}

				/* name */
				fs.write(getWrapValue(dbInfo.getDbName(), columnMaxLength[0]));

				/* ip */
				fs.write(getWrapValue(dbInfo.getBrokerIP(), columnMaxLength[1]));

				/* port */
				fs.write(getWrapValue(dbInfo.getBrokerPort(), columnMaxLength[2]));

				/* user */
				fs.write(getWrapValue(getDbUser(dbInfo), columnMaxLength[3]));

				/* comment */
				String comment = "";
				DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, managerMode);
				if (editorConfig != null && editorConfig.getDatabaseComment() != null) {
					comment = editorConfig.getDatabaseComment();
				}
				fs.write(getWrapValue(comment, columnMaxLength[4]));

				/* java url */
				String javaUrl = NodeUtil.getJavaConnectionUrl(dbInfo);
				fs.write(getWrapValue(javaUrl == null ? "" : javaUrl, columnMaxLength[5]));

				/* php url */
				String phpUrl = NodeUtil.getPHPConnectionUrl(dbInfo);
				fs.write(getWrapValue(phpUrl == null ? "" : phpUrl, columnMaxLength[6]));
				fs.write(StringUtil.NEWLINE);
			}

			fs.flush();
		} finally {
			FileUtil.close(fs);
		}
	}

	/**
	 * Get the wrap value
	 *
	 * @param value String
	 * @param index int
	 * @return String
	 */
	private String getWrapValue(String value, int width) {
		StringBuilder sb = new StringBuilder();
		sb.append(value);
		for (int i = width - value.length(); i > 0; i--) {
			sb.append(" ");
		}
		sb.append("\t");

		return sb.toString();
	}

	/**
	 * Write to xls
	 *
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void writeToXls() throws IOException, RowsExceededException, WriteException { // FIXME split logic and ui
		WritableWorkbook workbook = null;

		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("UTF-8");
		workbook = Workbook.createWorkbook(file, workbookSettings);

		WritableSheet sheet = workbook.createSheet(Messages.sheetNameConnections, 0);
		WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
		WritableCellFormat wcf = new WritableCellFormat(wf);

		jxl.write.Label label = null;

		label = new jxl.write.Label(0, 0, Messages.nameColumn, wcf);
		sheet.addCell(label);

		label = new jxl.write.Label(1, 0, Messages.iPColumn, wcf);
		sheet.addCell(label);

		label = new jxl.write.Label(2, 0, Messages.portColumn, wcf);
		sheet.addCell(label);

		label = new jxl.write.Label(3, 0, Messages.userColumn, wcf);
		sheet.addCell(label);

		label = new jxl.write.Label(4, 0, Messages.commentColumn, wcf);
		sheet.addCell(label);

		label = new jxl.write.Label(5, 0, Messages.javaUrlColumn, wcf);
		sheet.addCell(label);

		label = new jxl.write.Label(6, 0, Messages.phpUrlColumn, wcf);
		sheet.addCell(label);

		int index = 1;
		for (CubridDatabase database : input) {
			if (database == null) {
				continue;
			}

			DatabaseInfo dbInfo = database.getDatabaseInfo();
			if (dbInfo == null) {
				continue;
			}

			/* name */
			sheet.addCell(new jxl.write.Label(0, index, dbInfo.getDbName()));

			/* ip */
			sheet.addCell(new jxl.write.Label(1, index, dbInfo.getBrokerIP()));

			/* port */
			sheet.addCell(new jxl.write.Label(2, index, dbInfo.getBrokerPort()));

			/* user */
			sheet.addCell(new jxl.write.Label(3, index, getDbUser(dbInfo)));

			/* comment */
			String comment = "";
			DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, managerMode);
			if (editorConfig != null && editorConfig.getDatabaseComment() != null) {
				comment = editorConfig.getDatabaseComment();
			}
			sheet.addCell(new jxl.write.Label(4, index, comment));

			/* java url */
			String javaUrl = NodeUtil.getJavaConnectionUrl(dbInfo);
			sheet.addCell(new jxl.write.Label(5, index, javaUrl));

			/* php url */
			String phpUrl = NodeUtil.getPHPConnectionUrl(dbInfo);
			sheet.addCell(new jxl.write.Label(6, index, phpUrl));

			index++;
		}
		workbook.write();
		workbook.close();
	}

	/**
	 * Write to xlsx
	 *
	 * @throws IOException
	 */
	private void writeToXlsx() throws IOException { // FIXME split logic and ui
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(Messages.sheetNameConnections);
		String sheetRef = sheet.getPackagePart().getPartName().getName().substring(1);

		File tmp = File.createTempFile("Connections", ".xml");

		Map<String, File> fileMap = new HashMap<String, File>();
		fileMap.put(sheetRef, tmp);

		OutputStreamWriter writer = null;

		XlsxWriterHelper xlsxWriterhelper = null;
		XlsxWriterHelper.SpreadsheetWriter sheetWriter = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8");
			xlsxWriterhelper = new XlsxWriterHelper();
			sheetWriter = new XlsxWriterHelper.SpreadsheetWriter(writer);
			sheetWriter.setCharset("UTF-8");
			sheetWriter.beginSheet();

			/* Write the header */
			int styleIndex = ((XSSFCellStyle) xlsxWriterhelper.getStyles(workbook).get("header")).getIndex();
			sheetWriter.insertRow(0);
			sheetWriter.createCell(0, Messages.nameColumn, styleIndex);
			sheetWriter.createCell(1, Messages.iPColumn, styleIndex);
			sheetWriter.createCell(2, Messages.portColumn, styleIndex);
			sheetWriter.createCell(3, Messages.userColumn, styleIndex);
			sheetWriter.createCell(4, Messages.commentColumn, styleIndex);
			sheetWriter.createCell(5, Messages.javaUrlColumn, styleIndex);
			sheetWriter.createCell(6, Messages.phpUrlColumn, styleIndex);
			sheetWriter.endRow();

			/* Write the data */
			int rowIndex = 1;
			for (CubridDatabase database : input) {
				if (database == null) {
					continue;
				}

				DatabaseInfo dbInfo = database.getDatabaseInfo();
				if (dbInfo == null) {
					continue;
				}

				sheetWriter.insertRow(rowIndex++);

				/* name */
				sheetWriter.createCell(0, dbInfo.getDbName());

				/* ip */
				sheetWriter.createCell(1, dbInfo.getBrokerIP());

				/* port */
				sheetWriter.createCell(2, dbInfo.getBrokerPort());

				/* user */
				sheetWriter.createCell(3, getDbUser(dbInfo));

				/* comment */
				String comment = "";
				DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, managerMode);
				if (editorConfig != null && editorConfig.getDatabaseComment() != null) {
					comment = editorConfig.getDatabaseComment();
				}
				sheetWriter.createCell(4, comment);

				/* javaUrl */
				String url = NodeUtil.getJavaConnectionUrl(dbInfo);
				sheetWriter.createCell(5, url);

				/* phpUrl */
				String phpUrl = NodeUtil.getPHPConnectionUrl(dbInfo);
				sheetWriter.createCell(6, phpUrl);

				sheetWriter.endRow();
			}

			XlsxWriterHelper.writeSheetWriter(sheetWriter);
		} finally {
			XlsxWriterHelper.writeWorkbook(workbook, xlsxWriterhelper, fileMap, file);
		}
	}

	private String getDbUser(DatabaseInfo databaseInfo) {
		if (databaseInfo != null && databaseInfo.getAuthLoginedDbUserInfo() != null
				&& databaseInfo.getAuthLoginedDbUserInfo().getName() != null) {
			return databaseInfo.getAuthLoginedDbUserInfo().getName();
		}

		return "";
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

class ExportConnectionUtil { // FIXME split logic and ui
	public static boolean isXlsFile(String fileName) {
		Pattern p = Pattern.compile(".+\\.xls$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(fileName);

		return m.find();
	}

	public static boolean isXlsxFile(String fileName) {
		Pattern p = Pattern.compile(".+\\.xlsx$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(fileName);

		return m.find();
	}

	public static boolean isTxtFile(String fileName) {
		Pattern p = Pattern.compile(".+\\.txt$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(fileName);

		return m.find();
	}
}
