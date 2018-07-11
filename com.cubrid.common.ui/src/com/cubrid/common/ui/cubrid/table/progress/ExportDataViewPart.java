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

package com.cubrid.common.ui.cubrid.table.progress;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.dialog.exp.ExportConfigManager;
import com.cubrid.common.ui.cubrid.table.dialog.exp.ExportMonitor;
import com.cubrid.common.ui.cubrid.table.event.ExportDataBeginOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFailedOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishAllTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataSuccessEvent;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;

/**
 * 
 * Export Query Result Data ViewPart
 * 
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-31 created by Kevin.Wang
 */
public class ExportDataViewPart extends
		EditorPart implements
		ISaveablePart2,
		IExportDataMonitor {

	public static final String ID = ExportDataViewPart.class.getName();
	private ProgressIndicator progressIndicator;
	private TableViewer tvProgress;
	private Button btnStop;
	private boolean stop = false;
	private Label historyLabel;
	private Text historyText;
	private Button saveButton;
	private Button closeButton;

	private IExportDataProcessManager manager;
	private ExportConfig exportConfig = null;
	private IEditorInput input;
	private CubridDatabase database = null;
	private List<ExportMonitor> monitorList = new ArrayList<ExportMonitor>();

	/**
	 * Create part controls
	 * 
	 * @param parent of the controls
	 * 
	 */
	public void createPartControl(Composite parent) {
		Composite backPanel = new Composite(parent, SWT.NONE);
		backPanel.setLayout(new GridLayout(1, false));
		backPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite progressPanel = new Composite(backPanel, SWT.NONE);
		progressPanel.setLayout(new GridLayout(2, false));
		progressPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		progressIndicator = new ProgressIndicator(progressPanel, SWT.NONE);
		progressIndicator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		btnStop = new Button(progressPanel, SWT.NONE);
		btnStop.setText(com.cubrid.common.ui.common.Messages.btnStop);
		btnStop.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_run_stop.png"));
		btnStop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				processStop();
			}
		});
		tvProgress = new TableViewer(backPanel, SWT.BORDER | SWT.FULL_SELECTION);
		tvProgress.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		tvProgress.getTable().setLinesVisible(true);
		tvProgress.getTable().setHeaderVisible(true);

		final TableViewerColumn tableName = new TableViewerColumn(tvProgress, SWT.NONE);
		final TableViewerColumn totalCount = new TableViewerColumn(tvProgress, SWT.NONE);
		final TableViewerColumn parseCount = new TableViewerColumn(tvProgress, SWT.NONE);
		final TableViewerColumn status = new TableViewerColumn(tvProgress, SWT.NONE);
		final TableViewerColumn elapsedTime = new TableViewerColumn(tvProgress, SWT.NONE);

		tableName.getColumn().setWidth(250);
		tableName.getColumn().setText(Messages.exportMonitorPartColumnFileName);

		totalCount.getColumn().setWidth(100);
		totalCount.getColumn().setText(Messages.exportMonitorPartColumnTotalcount);

		parseCount.getColumn().setWidth(100);
		parseCount.getColumn().setText(Messages.exportMonitorPartColumnParsecount);

		status.getColumn().setWidth(100);
		status.getColumn().setText(com.cubrid.common.ui.common.Messages.columnStatus);

		elapsedTime.getColumn().setWidth(130);
		elapsedTime.getColumn().setText(com.cubrid.common.ui.common.Messages.columnElapsedTime);

		tvProgress.setContentProvider(new TableContentProvider());
		tvProgress.setLabelProvider(new TableLabelProvider());

		Composite historyComposite = new Composite(backPanel, SWT.NONE);
		historyComposite.setLayout(new GridLayout(4, false));
		historyComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		historyLabel = new Label(historyComposite, SWT.NONE);
		historyLabel.setText(Messages.exportMonitorPartSaveLabel);
		historyText = new Text(historyComposite, SWT.BORDER);
		historyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		saveButton = new Button(historyComposite, SWT.NONE);
		saveButton.setText(Messages.exportMonitorPartSaveButton);
		saveButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				if (StringUtil.isEmpty(historyText.getText())) {
					CommonUITool.openErrorBox(Messages.exportMonitorPartSaveErrMsg1);
					return;
				}
				if (ExportConfigManager.getInstance().getConfig(historyText.getText()) != null) {
					CommonUITool.openErrorBox(Messages.exportMonitorPartSaveErrMsg2);
					return;
				}
				exportConfig.setName(historyText.getText().trim());
				ExportConfigManager.getInstance().addConfig(exportConfig);
				close();
			}
		});

		closeButton = new Button(historyComposite, SWT.NONE);
		closeButton.setText(Messages.closeButtonName);
		closeButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				close();
			}
		});

		setHistroyWidgetStatus(false);

		init();
	}

	public void dispose() {
		if (!stop) {
			processStop();
		}
		super.dispose();
	}

	/**
	 * dispose
	 */
	public void close() {
		super.dispose();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ExportDataViewPart editor = (ExportDataViewPart) page.findEditor(input);
		if (editor != null) {
			page.activate(editor);
			page.closeEditor(editor, false);
		}
	}

	/**
	 * process stop logic
	 */
	public void processStop() {
		if (manager != null) {
			if (!btnStop.isDisposed()) {
				btnStop.setEnabled(false);
				progressIndicator.done();
				for (ExportMonitor po : monitorList) {
					if (po.getStatus() == ExportMonitor.STATUS_RUNNING) {
						po.setStatus(ExportMonitor.STATUS_STOPED);
					}
				}
				tvProgress.refresh(monitorList);
			}
			stop = true;
			manager.stopProcess();
		}
		setHistroyWidgetStatus(true);
	}

	/**
	 * Init the editor part
	 * 
	 * @param site IEditorSite
	 * @param input editorInput
	 * @throws PartInitException when error
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		ExportDataEditorInput exportDataEditorInput = (ExportDataEditorInput) input;
		this.database = exportDataEditorInput.getDatabase();
		this.exportConfig = exportDataEditorInput.getExportConfig();
		this.input = input;
		setSite(site);
		setInput(input);
		setTitleToolTip(input.getName());
		setTitleImage(CommonUIPlugin.getImage("icons/action/table_data_export.png"));
		setPartName(input.getName());

	}

	/**
	 * set history widget status
	 * 
	 * @param isEnable
	 */
	private void setHistroyWidgetStatus(boolean isEnable) {
		historyLabel.setEnabled(isEnable);
		historyText.setEnabled(isEnable);
		saveButton.setEnabled(isEnable);
		closeButton.setEnabled(isEnable);
		if (isEnable) {
			String dateStringNow = DateUtil.getDatetimeStringOnNow("MM/dd HH:mm");
			String databaseName = database.getDatabaseInfo().getDbName();
			String historyName = Messages.bind(Messages.defaultExportHistoryName, databaseName,
					dateStringNow);
			historyText.setText(historyName);
			saveButton.forceFocus();
		}
	}

	/**
	 * initManager
	 * 
	 * @return load file result
	 */
	public void init() {
		manager = new CommonExportDataProcessManager(database.getDatabaseInfo(), exportConfig,
				this, progressIndicator);
		if (manager.isInitSuccess()) {
			initTableView();
			manager.startProcess();
		}
	}

	/**
	 * intiTableView
	 */
	private void initTableView() {
		if (exportConfig.getExportType() == ExportConfig.EXPORT_TO_FILE) {
			if (exportConfig.isExportSchema()) {
				ExportMonitor po = new ExportMonitor(ExportConfig.TASK_NAME_SCHEMA);
				po.setTotalCount(1);
				monitorList.add(po);

				if (exportConfig.isExportIndex()) {
					ExportMonitor indexPo = new ExportMonitor(ExportConfig.TASK_NAME_INDEX);
					indexPo.setTotalCount(1);
					monitorList.add(indexPo);
				}
				if (exportConfig.isExportSerial()) {
					ExportMonitor serialPo = new ExportMonitor(ExportConfig.TASK_NAME_SERIAL);
					serialPo.setTotalCount(1);
					monitorList.add(serialPo);
				}
				if (exportConfig.isExportView()) {
					ExportMonitor viewPo = new ExportMonitor(ExportConfig.TASK_NAME_VIEW);
					viewPo.setTotalCount(1);
					monitorList.add(viewPo);
				}
				if (exportConfig.isExportTrigger()) {
					ExportMonitor triggerPo = new ExportMonitor(ExportConfig.TASK_NAME_TRIGGER);
					triggerPo.setTotalCount(1);
					monitorList.add(triggerPo);
				}
			}
			if (exportConfig.isExportData()) {
				for (String table : exportConfig.getTableNameList()) {
					ExportMonitor po = new ExportMonitor(table);
					po.setTotalCount(exportConfig.getTotalCount(table));
					monitorList.add(po);
				}

				for (String sqlName : exportConfig.getSQLNameList()) {
					ExportMonitor po = new ExportMonitor(sqlName);
					po.setTotalCount(exportConfig.getTotalCount(sqlName));
					monitorList.add(po);
				}
			}

		} else if (exportConfig.getExportType() == ExportConfig.EXPORT_TO_LOADDB) {
			if (exportConfig.isExportIndex()) {
				ExportMonitor indexPo = new ExportMonitor(
						exportConfig.getDataFilePath(ExportConfig.LOADDB_INDEXFILEKEY));
				indexPo.setTotalCount(1);
				monitorList.add(indexPo);
			}
			if (exportConfig.isExportSchema()) {
				ExportMonitor schemaPo = new ExportMonitor(
						exportConfig.getDataFilePath(ExportConfig.LOADDB_SCHEMAFILEKEY));
				schemaPo.setTotalCount(1);
				monitorList.add(schemaPo);
			}
			if (exportConfig.isExportTrigger()) {
				ExportMonitor triggerPo = new ExportMonitor(
						exportConfig.getDataFilePath(ExportConfig.LOADDB_TRIGGERFILEKEY));
				triggerPo.setTotalCount(1);
				monitorList.add(triggerPo);
			}
			if (exportConfig.isExportData()) {
				ExportMonitor po = new ExportMonitor(
						exportConfig.getDataFilePath(ExportConfig.LOADDB_DATAFILEKEY));
				po.setTotalCount(exportConfig.getTotalCount(ExportConfig.LOADDB_DATAFILEKEY));
				monitorList.add(po);
			}
		}

		tvProgress.setInput(monitorList);
	}

	public int promptToSaveOnClose() {
		return 0;
	}

	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void setFocus() {
	}

	public void finished() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				progressIndicator.done();
				setHistroyWidgetStatus(true);
			}
		});
	}

	public void addEvent(ExportDataEvent event) {
		if (stop) {
			return;
		}
		flushUI(event);
	}

	/**
	 * Write events to UI
	 * 
	 */
	public void flushUI(final ExportDataEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (stop) {
					return;
				}
				int pbvalue = 0;
				if (event instanceof ExportDataBeginOneTableEvent) {
					updateOneTableData((ExportDataBeginOneTableEvent) event);
				} else if (event instanceof ExportDataSuccessEvent) {
					updateTableData((ExportDataSuccessEvent) event);
					pbvalue += ((ExportDataSuccessEvent) event).getSuccessCount();
				} else if (event instanceof ExportDataFinishOneTableEvent) {
					updateOneTableData((ExportDataFinishOneTableEvent) event);
				} else if (event instanceof ExportDataFailedOneTableEvent) {
					updateOneTableData((ExportDataFailedOneTableEvent) event);
				}
				if (event instanceof ExportDataFinishAllTableEvent) {
					btnStop.setEnabled(false);
					progressIndicator.done();
					return;
				}
				if (pbvalue > 0) {
					progressIndicator.worked(pbvalue);
				}
			}
		});
	}

	/**
	 * when one file begin, update table
	 * 
	 * @param evt BeginOneFileEvent
	 */
	private void updateOneTableData(ExportDataBeginOneTableEvent evt) {
		if (stop) {
			return;
		}
		String tableName = evt.getTableName();
		for (int i = 0; i < monitorList.size(); i++) {
			ExportMonitor po = monitorList.get(i);
			if (po.getTableName().equals(tableName)) {
				po.setBeginTime(evt.getEventTime());
				po.setStatus(ExportMonitor.STATUS_RUNNING);
				tvProgress.refresh(monitorList);
				break;
			}
		}
	}

	/**
	 * when one file begin, update table
	 * 
	 * @param evt BeginOneFileEvent
	 */
	private void updateOneTableData(ExportDataFailedOneTableEvent evt) {
		if (stop) {
			return;
		}
		String tableName = evt.getTableName();
		for (int i = 0; i < monitorList.size(); i++) {
			ExportMonitor po = monitorList.get(i);
			if (po.getTableName().equals(tableName)) {
				po.setBeginTime(evt.getEventTime());
				po.setStatus(ExportMonitor.STATUS_FAILED);
				tvProgress.refresh(monitorList);
				break;
			}
		}
	}

	/**
	 * when one file finish ,update table
	 * 
	 * @param evt FinishOneFileEvent
	 */
	private void updateOneTableData(ExportDataFinishOneTableEvent evt) {
		if (stop) {
			return;
		}
		String tableName = evt.getTableName();
		for (int i = 0; i < monitorList.size(); i++) {
			ExportMonitor po = monitorList.get(i);
			if (po.getTableName().equals(tableName)) {
				po.setStatus(ExportMonitor.STATUS_FINISHED);
				po.setElapsedTime(evt.getEventTime() - po.getBeginTime());
				tvProgress.refresh(monitorList);
				break;
			}
		}
	}

	private void updateTableData(ExportDataSuccessEvent evt) {
		if (stop) {
			return;
		}

		String tableName = "";
		ExportDataSuccessEvent event = (ExportDataSuccessEvent) evt;
		tableName = event.getTableName();

		for (ExportMonitor po : monitorList) {
			if (po.getTableName().equals(tableName)) {
				po.setElapsedTime(evt.getEventTime() - po.getBeginTime());
				po.setParseCount(po.getParseCount() + event.getSuccessCount());
				po.setStatus(ExportMonitor.STATUS_RUNNING);
				tvProgress.refresh(monitorList);
				break;
			}
		}
	}
}
