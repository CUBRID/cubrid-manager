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
package com.cubrid.cubridmanager.ui.cubrid.dbspace.editor;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.util.Rotation;
import org.slf4j.Logger;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoListNew;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoListOld;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.VolumeType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.control.PieRenderer;
import com.cubrid.cubridmanager.ui.cubrid.database.editor.DatabaseStatusEditor;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;

/**
 *
 * This query editor part is responsible to show the status of space info
 *
 * @author robin 20090318
 */
public class VolumeFolderInfoEditor extends
		CubridEditorPart {

	private static final Logger LOGGER = LogUtil.getLogger(VolumeFolderInfoEditor.class);
	public static final String ID = "com.cubrid.cubridmanager.ui.cubrid.dbspace.editor.VolumeFolderInfoEditor";
	private CubridDatabase database = null;
	private final List<DbSpaceInfo> dbSpaceList;

	private boolean isRunning = false;

	private final List<Map<String, String>> spInfoListData = new ArrayList<Map<String, String>>();
	private TableViewer spInfoTableViewer;
	private Table spInfoTable;

	private Label spaceNameLabel;
	private Composite parentComp;
	private Composite chartComp;

	private ScrolledComposite scrolledComp = null;
	private final org.eclipse.swt.graphics.Color color;

	private String volumeFolderName = "";
	private String volumeType = null;
	
	private static HashMap<String, String> folderTypeMap;

	static {
		folderTypeMap = new HashMap<String, String>();
		folderTypeMap.put(CubridNodeType.GENERIC_VOLUME_FOLDER, VolumeType.GENERIC.toString());
		folderTypeMap.put(CubridNodeType.DATA_VOLUME_FOLDER, VolumeType.DATA.toString());
		folderTypeMap.put(CubridNodeType.INDEX_VOLUME_FOLDER, VolumeType.INDEX.toString());
		folderTypeMap.put(CubridNodeType.TEMP_VOLUME_FOLDER, VolumeType.TEMP.toString());
		folderTypeMap.put(CubridNodeType.ARCHIVE_LOG_FOLDER, VolumeType.ARCHIVE_LOG.toString());
		folderTypeMap.put(CubridNodeType.ACTIVE_LOG_FOLDER, VolumeType.ACTIVE_LOG.toString());
		folderTypeMap.put(CubridNodeType.PP_VOLUME_FOLDER, VolumeType.PP.getText());
		folderTypeMap.put(CubridNodeType.PT_VOLUME_FOLDER, VolumeType.PT.getText());
		folderTypeMap.put(CubridNodeType.TT_VOLUME_FOLDER, VolumeType.TT.getText());
	}

	public VolumeFolderInfoEditor() {
		dbSpaceList = new ArrayList<DbSpaceInfo>();
		color = ResourceManager.getColor(230, 230, 230);
	}

	/**
	 * Initializes this editor with the given editor site and input.
	 *
	 * @param site the editor site
	 * @param input the editor input
	 * @exception PartInitException if this editor was not initialized
	 *            successfully
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		if (input instanceof DefaultSchemaNode) {
			ICubridNode node = (DefaultSchemaNode) input;
			database = ((DefaultSchemaNode) node).getDatabase();
			
			String type = node.getType();
			if ((volumeType = folderTypeMap.get(type)) != null
					&& (((DefaultSchemaNode) node).getChildren() != null && ((DefaultSchemaNode) node).getChildren().size() > 0)) {
				for (ICubridNode child : ((DefaultSchemaNode) node).getChildren()) {
					dbSpaceList.add((DbSpaceInfo) ((DefaultSchemaNode) child).getAdapter(DbSpaceInfo.class));
				}
			}
			volumeFolderName = node.getName();
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * @param parent the parent control
	 * @see IWorkbenchPart
	 */
	public void createPartControl(Composite parent) {
		scrolledComp = new ScrolledComposite(parent, SWT.H_SCROLL
				| SWT.V_SCROLL);
		FillLayout flayout = new FillLayout();
		scrolledComp.setLayout(flayout);

		parentComp = new Composite(scrolledComp, SWT.NONE);
		parentComp.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		parentComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		parentComp.setLayout(layout);
		parentComp.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		//the description composite
		Composite descComp = new Composite(parentComp, SWT.NONE);
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		GridData gdDescComp = new GridData(GridData.FILL_HORIZONTAL);
		descComp.setLayoutData(gdDescComp);
		descComp.setLayout(layout);
		descComp.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		spaceNameLabel = new Label(descComp, SWT.LEFT | SWT.WRAP);
		spaceNameLabel.setBackground(ResourceManager.getColor(255, 255, 255));
		spaceNameLabel.setFont(ResourceManager.getFont("", 15, SWT.NONE));

		spaceNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		Font font = ResourceManager.getFont("", 14, SWT.BOLD);
		spaceNameLabel.setFont(font);
		spaceNameLabel.setText("");

		final String[] columnNameArr = new String[]{"col1", "col2" };
		spInfoTableViewer = DatabaseStatusEditor.createCommonTableViewer(
				descComp, null, columnNameArr, CommonUITool.createGridData(
						GridData.FILL_BOTH, 1, 1, -1, -1));
		spInfoTableViewer.setInput(spInfoListData);
		spInfoTable = spInfoTableViewer.getTable();
		spInfoTable.setLinesVisible(true);
		spInfoTable.setHeaderVisible(false);

		//the chart composite
		chartComp = new Composite(parentComp, SWT.NONE);
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.numColumns = 2;
		GridData gdChartComp = new GridData(GridData.FILL_BOTH);
		chartComp.setLayoutData(gdChartComp);
		chartComp.setLayout(layout);
		chartComp.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		loadData();
	}

	/**
	 * paint the composite
	 *
	 */
	public void paintComp() {
		if (chartComp == null || chartComp.isDisposed()) {
			return;
		}
		chartComp.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				// FIXME more simple
				try {
					FontMetrics fm = event.gc.getFontMetrics();
					int yy = 0, chary = fm.getHeight() + 2;
					Font forg = event.gc.getFont();
					Font flarge = new Font(parentComp.getDisplay(),
							event.gc.getFont().toString(), 14, SWT.BOLD);

					event.gc.setFont(flarge);
					event.gc.drawText(Messages.msgVolumeFolderInfo, 10, 20);
					flarge.dispose();
					event.gc.setFont(forg);

					event.gc.drawText(Messages.msgVolumeFolderName, 20, 50);
					if (volumeType.equalsIgnoreCase(VolumeType.ACTIVE_LOG.toString())
							|| volumeType.equalsIgnoreCase(VolumeType.ARCHIVE_LOG.toString())) {
						event.gc.drawText(Messages.msgVolumeFolderSize, 130, 50);
					} else {
						event.gc.drawText(Messages.msgVolumeFolderUsedSize,
								130, 50);
					}
					event.gc.drawText(Messages.msgVolumeFolderTotalSize, 350,
							50);
					event.gc.drawText(Messages.msgVolumeFolderTotalPage, 445,
							50);

					yy++;
					event.gc.setForeground(Display.getCurrent().getSystemColor(
							SWT.COLOR_DARK_BLUE));
					event.gc.fillGradientRectangle(5, 50 + chary * yy, 530, 8,
							true);
					yy++;
					// VolumeInfo virec;
					int freeint, totint, usedint, usedpage;
					// for (int i = 0, n = Volinfo.size(); i < n; i++) {
					// virec = (VolumeInfo) Volinfo.get(i);
					synchronized (cubridNode) {
						if (database.getDatabaseInfo().getDbSpaceInfoList() != null
								&& database.getDatabaseInfo().getDbSpaceInfoList().getSpaceinfo() != null) {
							// calcColumnLength();
							ArrayList<DbSpaceInfoList.FreeTotalSizeSpacename> volumeInfos = database.getDatabaseInfo().getDbSpaceInfoList().getVolumesInfoByType(volumeType);
							for (DbSpaceInfoList.FreeTotalSizeSpacename info : volumeInfos) {
								totint = info.totalSize;
								freeint = info.freeSize;
								if (totint <= 0) {
									continue;
								}
								event.gc.setForeground(Display.getCurrent().getSystemColor(
										SWT.COLOR_BLACK));

								alignText(info.spaceName, event.gc, 50
										+ chary * yy, 20, 50, 1);

								// e.gc.drawText(bean.getSpacename(), 10, 50 +
								// chary * yy);

								usedpage = totint - freeint;
								freeint = ((freeint * 100) / totint);
								usedint = 100 - freeint;
								if (usedint > 0) {
									event.gc.setBackground(Display.getCurrent().getSystemColor(
											SWT.COLOR_DARK_BLUE));
									event.gc.fillRectangle(130,
											50 + chary * yy,
											170 * usedint / 100, chary - 2);
								}
								if (freeint > 0) {
									event.gc.setBackground(Display.getCurrent().getSystemColor(
											SWT.COLOR_DARK_YELLOW));
									event.gc.fillRectangle(
											130 + (170 * usedint / 100), 50
													+ chary * yy,
											170 - (170 * usedint / 100),
											chary - 2);
								}
								event.gc.setBackground(Display.getCurrent().getSystemColor(
										SWT.COLOR_WHITE));
								event.gc.setForeground(Display.getCurrent().getSystemColor(
										SWT.COLOR_WHITE));
								if (!volumeType.equalsIgnoreCase(VolumeType.ACTIVE_LOG.toString())
										&& !volumeType.equalsIgnoreCase(VolumeType.ARCHIVE_LOG.toString())) {
									event.gc.drawText(
											StringUtil.formatNumber(
													usedpage
															* database.getDatabaseInfo().getDbSpaceInfoList().getPagesize()
															/ (1048576.0f),
													"#,###.##")
													+ "/"
													+ StringUtil.formatNumber(
															freeint
																	* (database.getDatabaseInfo().getDbSpaceInfoList().getPagesize() / (1048576.0f)),
															"#,###.##"), 170,
											50 + chary * yy, true);
								}
								event.gc.setForeground(Display.getCurrent().getSystemColor(
										SWT.COLOR_BLACK));
								if (CompatibleUtil.isSupportLogPageSize(database.getServer().getServerInfo())
										&& (volumeType.equalsIgnoreCase(VolumeType.ACTIVE_LOG.toString()) || volumeType.equalsIgnoreCase(VolumeType.ARCHIVE_LOG.toString()))) {
									alignText(
											StringUtil.formatNumber(
													(totint * (database.getDatabaseInfo().getDbSpaceInfoList().getLogpagesize() / (1048576.0f))),
													"#,###.##")
													+ " M", event.gc, 50
													+ chary * yy, 320, 390, 2);
								} else {
									alignText(
											StringUtil.formatNumber(
													(totint * (database.getDatabaseInfo().getDbSpaceInfoList().getPagesize() / (1048576.0f))),
													"#,###.##")
													+ " M", event.gc, 50
													+ chary * yy, 320, 390, 2);
								}

								event.gc.setForeground(Display.getCurrent().getSystemColor(
										SWT.COLOR_BLACK));
								alignText(StringUtil.formatNumber(
										totint, "#,###")
										+ " pages", event.gc, 50 + chary * yy,
										430, 500, 2);
								yy++;
							}
						}
					}
					scrolledComp.setMinHeight(800);
					scrolledComp.setMinWidth(800);
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
		});
	}

	/**
	 * paint the composite
	 *
	 */
	public void paintComp1() {
		for (DbSpaceInfo dbSpaceInfo : dbSpaceList) {
			JFreeChart chart = createChart(createDataset(dbSpaceInfo),
					dbSpaceInfo);

			final ChartComposite frame = new ChartComposite(chartComp,
					SWT.NONE, chart, false, true, false, true, true);

			GridData gdDescGroup = new GridData();
			gdDescGroup.widthHint = 350;
			gdDescGroup.heightHint = 250;
			frame.setLayoutData(gdDescGroup);
		}
	}

	/**
	 * create the dataset
	 *
	 * @param dbSpaceInfo DbSpaceInfo
	 * @return dataset
	 */
	private DefaultPieDataset createDataset(DbSpaceInfo dbSpaceInfo) {
		int freeSize = dbSpaceInfo.getFreepage();
		int totalSize = dbSpaceInfo.getTotalpage();

		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue(Messages.chartMsgUsedSize, new Double(totalSize
				- freeSize));
		dataset.setValue(Messages.chartMsgFreeSize, new Double(freeSize));
		return dataset;

	}

	/**
	 * Init the value of editor field
	 *
	 */
	private void initialize() { // FIXME extract to utility class
		if (database == null || database.getDatabaseInfo() == null) {
			return;
		}

		if (database.getDatabaseInfo().getDbSpaceInfoList() == null) {
			return;
		}
		int totalSize = 0;
		int freeSize = 0;
		// String volumeType = "";
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("0", Messages.tblVolumeFolderType);
		map3.put("1", volumeType);
		spInfoListData.add(map3);
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("0", Messages.tblVolumeFolderVolumeCount);
		map2.put(
				"1",
				dbSpaceList.size()
						+ "                                                                                   ");
		spInfoListData.add(map2);
		for (DbSpaceInfo dbSpaceInfo : dbSpaceList) {
			totalSize += dbSpaceInfo.getTotalpage();
			freeSize += dbSpaceInfo.getFreepage();
		}
		if (!VolumeType.ACTIVE_LOG.toString().equalsIgnoreCase(volumeType)
				&& !VolumeType.ARCHIVE_LOG.toString().equalsIgnoreCase(
						volumeType)) {
			Map<String, String> map4 = new HashMap<String, String>();
			map4.put("0", Messages.tblVolumeFolderFreeSize);
			map4.put(
					"1",
					StringUtil.formatNumber(
							freeSize
									* (database.getDatabaseInfo().getDbSpaceInfoList().getPagesize() / (1048576.0f)),
							"#,###.##")
							+ "M ("
							+ StringUtil.formatNumber(freeSize, "#,###")
							+ " pages)");
			spInfoListData.add(map4);
		}
		Map<String, String> map5 = new HashMap<String, String>();
		map5.put("0", Messages.tblVolumeFolderTotalSize);
		map5.put(
				"1",
				StringUtil.formatNumber(
						totalSize
								* (database.getDatabaseInfo().getDbSpaceInfoList().getPagesize() / (1048576.0f)),
						"#,###.##")
						+ "M ("
						+ StringUtil.formatNumber(totalSize, "#,###")
						+ " pages)");
		spInfoListData.add(map5);

		if (CompatibleUtil.isSupportLogPageSize(database.getServer().getServerInfo())
				&& (VolumeType.ACTIVE_LOG.toString().equalsIgnoreCase(
						volumeType) || VolumeType.ARCHIVE_LOG.toString().equalsIgnoreCase(
						volumeType))) {
			Map<String, String> map6 = new HashMap<String, String>();
			map6.put("0", Messages.tblVolumeFolderLogPageSize);
			map6.put(
					"1",
					StringUtil.formatNumber(
							database.getDatabaseInfo().getDbSpaceInfoList().getLogpagesize(),
							"#,###")
							+ " byte");
			spInfoListData.add(map6);
		} else {
			Map<String, String> map6 = new HashMap<String, String>();
			map6.put("0", Messages.tblVolumeFolderPageSize);
			map6.put(
					"1",
					StringUtil.formatNumber(
							database.getDatabaseInfo().getDbSpaceInfoList().getPagesize(),
							"#,###")
							+ " byte");
			spInfoListData.add(map6);
		}

		spaceNameLabel.setText(volumeFolderName);

		while (!spInfoListData.isEmpty()) {
			spInfoListData.remove(0);
		}

		if (spInfoTable != null && !spInfoTable.isDisposed()) {
			spInfoTableViewer.refresh();
			for (int i = 0; i < spInfoTable.getColumnCount(); i++) {
				spInfoTable.getColumn(i).pack();
			}
			for (int i = 0; i < (spInfoTable.getItemCount() + 1) / 2; i++) {
				spInfoTable.getItem(i * 2).setBackground(color);
			}
		}
	}

	/**
	 * Do save
	 *
	 * @param monitor the progress monitor
	 */
	public void doSave(IProgressMonitor monitor) {
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * Do save as
	 */
	public void doSaveAs() {
		//empty
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * set the file
	 *
	 * @param file File
	 */
	public void setFile(File file) {
		//
	}

	/**
	 * load the editor data
	 *
	 * @return boolean
	 */
	public boolean loadData() {

		TaskJobExecutor taskJobExecutor = new TaskJobExecutor() {
			@SuppressWarnings("unchecked")
			@Override
			public IStatus exec(IProgressMonitor monitor) {

				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				for (ITask t : taskList) {
					t.execute();
					final String msg = t.getErrorMsg();

					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					if (msg != null && msg.length() > 0
							&& !monitor.isCanceled()) {
						return new Status(IStatus.ERROR,
								CubridManagerUIPlugin.PLUGIN_ID, msg);
					} else {
						final DbSpaceInfoList model = ((CommonQueryTask<? extends DbSpaceInfoList>)t).getResultModel();
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								database.getDatabaseInfo().setDbSpaceInfoList(
										model);
								if (scrolledComp == null
										|| scrolledComp.isDisposed()) {
									return;
								}
								initialize();
								paintComp();
								scrolledComp.setContent(parentComp);
								scrolledComp.setExpandHorizontal(true);
								scrolledComp.setExpandVertical(true);
							}
						});
					}
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}
				return Status.OK_STATUS;
			}

		};
		CommonQueryTask<? extends DbSpaceInfoList> task = DbSpaceInfoList.useOld(database.getServer().getServerInfo().getEnvInfo()) ?
				new CommonQueryTask<DbSpaceInfoListOld>(database.getServer().getServerInfo(),
														CommonSendMsg.getCommonDatabaseSendMsg(),
														new DbSpaceInfoListOld()) :
				new CommonQueryTask<DbSpaceInfoListNew>(database.getServer().getServerInfo(),
														CommonSendMsg.getCommonDatabaseSendMsg(),
														new DbSpaceInfoListNew());
		task.setDbName(database.getName());
		taskJobExecutor.addTask(task);
		
		String serverName = database.getServer().getName();
		String dbName = database.getName();
		String jobName = Messages.viewVolumeInfoJobName + " - "
				+ volumeFolderName + "@" + dbName + "@" + serverName;
		taskJobExecutor.schedule(jobName, null, false, Job.LONG);
		return true;

	}

	/**
	 * execute the task
	 *
	 * @param buttonId int
	 * @param tasks SocketTask[]
	 * @param cancelable boolean
	 */
	public void execTask(final int buttonId, final SocketTask[] tasks,
			boolean cancelable) {
		final Shell shell = parentComp.getShell();
		final Display display = shell.getDisplay();
		isRunning = false;
		try {

			new ProgressMonitorDialog(shell).run(true, cancelable,
					new IRunnableWithProgress() {
						public void run(final IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException { // FIXME more simple
							monitor.beginTask(
									com.cubrid.common.ui.spi.Messages.msgRunning,
									IProgressMonitor.UNKNOWN);

							if (monitor.isCanceled()) {
								return;
							}

							isRunning = true;
							Thread thread = new Thread() {
								public void run() {
									while (!monitor.isCanceled() && isRunning) {
										try {
											sleep(1);
										} catch (InterruptedException e) {
										}
									}
									if (monitor.isCanceled()) {
										for (SocketTask t : tasks) {
											if (t != null) {
												t.cancel();
											}
										}

									}
								}
							};
							thread.start();
							if (monitor.isCanceled()) {
								isRunning = false;
								return;
							}
							for (SocketTask task : tasks) {
								if (task != null) {
									task.execute();
									final String msg = task.getErrorMsg();
									if (monitor.isCanceled()) {
										isRunning = false;
										return;
									}
									if (msg != null && msg.length() > 0
											&& !monitor.isCanceled()) {
										display.syncExec(new Runnable() {
											public void run() {
												CommonUITool.openErrorBox(shell,
														msg);
											}
										});
										isRunning = false;
										return;
									}
								}
								if (monitor.isCanceled()) {
									isRunning = false;
									return;
								}
							}
							if (monitor.isCanceled()) {
								isRunning = false;
								return;
							}

							isRunning = false;
							monitor.done();
						}
					});
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @see com.cubrid.common.ui.spi.event.ICubridNodeChangedListener#nodeChanged(com.cubrid.common.ui.spi.event.CubridNodeChangedEvent)
	 * @param event the CubridNodeChangedEvent object
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode eventNode = event.getCubridNode();
		if (!(eventNode instanceof ISchemaNode)
				|| event.getType() != CubridNodeChangedEventType.CONTAINER_NODE_REFRESH
				|| cubridNode == null) {
			return;
		}
		if (!CubridNodeType.DBSPACE_FOLDER.equals(eventNode.getType())) {
			return;
		}
		ISchemaNode eventSchemaNode = (ISchemaNode) eventNode;
		ISchemaNode schemaNode = (ISchemaNode) cubridNode;
		if (!eventSchemaNode.getDatabase().getId().equals(
				schemaNode.getDatabase().getId())) {
			return;
		}
		synchronized (this) {
			if (database.getDatabaseInfo().getDbSpaceInfoList() == null) {
				loadData();
			}
		}
	}

	/**
	 *
	 * Creates a chart.
	 *
	 * @param dataset DefaultPieDataset
	 * @param dbSpaceInfo DbSpaceInfo
	 * @return jFreeChart
	 */
	private static JFreeChart createChart(DefaultPieDataset dataset,
			DbSpaceInfo dbSpaceInfo) {

		JFreeChart chart = ChartFactory.createPieChart3D(
				dbSpaceInfo.getSpacename() + " Chart", // chart

				// title

				dataset, // data

				true, // include legend

				true, false);

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN,
				12));
		plot.setDirection(Rotation.ANTICLOCKWISE);
		plot.setCircular(false);
		plot.setLabelLinkMargin(0.0);
		plot.setLabelGap(0.0);
		plot.setLabelLinkStyle(PieLabelLinkStyle.QUAD_CURVE);
		plot.setOutlinePaint(ChartColor.VERY_DARK_BLUE);
		plot.setToolTipGenerator(new StandardPieToolTipGenerator(
				"{0}={1} pages {2}", new DecimalFormat("00.0"),
				new DecimalFormat("0.00%")));
		// plot.setSectionPaint("", SWTResourceManager.getColor(230, 230, 230));
		Color[] colors = {new Color(235, 139, 82), new Color(119, 119, 253) };
		PieRenderer renderer = new PieRenderer(colors);
		renderer.setColor(plot, dataset);
		return chart;

	}

	/**
	 * align the text
	 *
	 * @param str String
	 * @param pg org.eclipse.swt.graphics.GC
	 * @param valueY int
	 * @param start int
	 * @param end int
	 * @param mode int
	 */
	private void alignText(String str, org.eclipse.swt.graphics.GC pg,
			int valueY, int start, int end, int mode) { // FIXME extract to utility class
		FontMetrics fm = pg.getFontMetrics();
		int wString = str.length() * fm.getAverageCharWidth();
		int x = start;
		switch (mode) {
		case 0:
			if ((end - start - wString) > 0) {
				x = start + (end - start - wString) / 2;
			}
			break;
		case 1:
			break;
		case 2:
			if ((end - start - wString) > 0) {
				x = start + (end - start - wString);
			}
			break;
		default:
			break;
		}
		pg.drawString(str, x, valueY);
	}

}
