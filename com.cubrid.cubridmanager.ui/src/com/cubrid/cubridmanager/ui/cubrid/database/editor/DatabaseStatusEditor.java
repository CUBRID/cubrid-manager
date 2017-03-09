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
package com.cubrid.cubridmanager.ui.cubrid.database.editor;

import static com.cubrid.common.core.util.NoOp.noOp;

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
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
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
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerVersion;
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
import com.cubrid.cubridmanager.ui.cubrid.dbspace.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;

/**
 *
 * This query editor part is responsible to show the status of database
 *
 * @author robin 20090318
 */
public class DatabaseStatusEditor extends
		CubridEditorPart {

	private static final Logger LOGGER = LogUtil.getLogger(DatabaseStatusEditor.class);
	public static final String ID = "com.cubrid.cubridmanager.ui.cubrid.database.editor.DatabaseStatusEditor";
	private CubridDatabase database = null;
	public boolean isChanged = false;
	private boolean isRunning = false;
	private Composite parentComp;
	private Composite chartComp;

	private ScrolledComposite scrolledComp = null;
	private final List<Map<String, String>> dbInfoListData = new ArrayList<Map<String, String>>();
	private final List<Map<String, String>> dbSpaceDescriptionData = new ArrayList<Map<String,String>>();
	private final List<Map<String, String>> volumeDescriptionData = new ArrayList<Map<String,String>>();
	private final List<Map<String, String>> fileSpaceDescriptionData = new ArrayList<Map<String,String>>();

	private TableViewer dbInfoTableViewer;
	private Table dbInfoTable;
	
	private TableViewer dbSpaceDescriptionTableViewer;
	private Table dbSpaceDescriptionTable;
	
	private TableViewer volumeDescriptionTableViewer;
	private Table volumeDescriptionTable;
	
	private TableViewer fileSpaceDescriptionTableViewer;
	private Table fileSpaceDescriptionTable;
	
	private final Color color;
	public DatabaseStatusEditor() {
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

		if (input instanceof CubridDatabase) {
			database = (CubridDatabase) input;
		} else if (input instanceof DefaultSchemaNode) {
			ICubridNode node = (DefaultSchemaNode) input;
			if (CubridNodeType.DBSPACE_FOLDER.equals(node.getType())) {
				database = ((DefaultSchemaNode) node).getDatabase();
			}
		}
	}

	/**
	 * Create page content
	 *
	 * @param parent the parent composite
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

		//database description information composite
		Composite descComp = new Composite(parentComp, SWT.NONE);
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		descComp.setLayout(layout);

		GridData gdDescComp = new GridData(GridData.FILL_HORIZONTAL);
		descComp.setLayoutData(gdDescComp);

		descComp.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		Label descriptionLabel = new Label(descComp, SWT.LEFT | SWT.WRAP);
		descriptionLabel.setBackground(ResourceManager.getColor(255, 255, 255));
		Font font = ResourceManager.getFont("", 20, SWT.BOLD);
		descriptionLabel.setFont(font);

		GridData descGrid = new GridData(SWT.FILL, SWT.FILL, false, false);
		descriptionLabel.setLayoutData(descGrid);
		descriptionLabel.setText(database.getName());

		final String[] columnNameArr = new String[]{"col1", "col2" };
		dbInfoTableViewer = createCommonTableViewer(descComp, null,
				columnNameArr, CommonUITool.createGridData(GridData.FILL_BOTH, 1,
						1, -1, -1));
		dbInfoTableViewer.setInput(dbInfoListData);
		dbInfoTable = dbInfoTableViewer.getTable();
		dbInfoTable.setLinesVisible(true);
		dbInfoTable.setHeaderVisible(false);
		
		if (!DbSpaceInfoList.useOld(database.getDatabaseInfo().getServerInfo().getEnvInfo())){
			dbSpaceDescriptionTableViewer = createCommonTableViewer(descComp, null, 
							new String[]{"type","purpose","volume_count","used_size","free_size","total_size"}, 
							CommonUITool.createGridData(GridData.FILL_BOTH, 1,
							1, -1, -1));
			dbSpaceDescriptionTableViewer.setInput(dbSpaceDescriptionData);
			dbSpaceDescriptionTable = dbSpaceDescriptionTableViewer.getTable();
			dbSpaceDescriptionTable.setLinesVisible(true);
			dbSpaceDescriptionTable.setHeaderVisible(true);
			
			volumeDescriptionTableViewer = createCommonTableViewer(descComp, null, 
					new String[]{"volid","type","purpose","used_size","free_size","total_size","volume_name"}, 
					CommonUITool.createGridData(GridData.FILL_BOTH, 1,
					1, -1, -1));
			volumeDescriptionTableViewer.setInput(volumeDescriptionData);
			volumeDescriptionTable = volumeDescriptionTableViewer.getTable();
			volumeDescriptionTable.setLinesVisible(true);
			volumeDescriptionTable.setHeaderVisible(true);
	
			fileSpaceDescriptionTableViewer = createCommonTableViewer(descComp, null, 
					new String[]{"data_type","file_count","used_size","file_table_size","reserved_size","total_size"}, 
					CommonUITool.createGridData(GridData.FILL_BOTH, 1,
					1, -1, -1));
			fileSpaceDescriptionTableViewer.setInput(fileSpaceDescriptionData);
			fileSpaceDescriptionTable = fileSpaceDescriptionTableViewer.getTable();
			fileSpaceDescriptionTable.setLinesVisible(true);
			fileSpaceDescriptionTable.setHeaderVisible(true);
		}

		//chart compostie
		chartComp = new Composite(parentComp, SWT.NONE);
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		GridData gdChartComp = new GridData(GridData.FILL_HORIZONTAL);
		chartComp.setLayoutData(gdChartComp);
		chartComp.setLayout(layout);
		chartComp.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		scrolledComp.setContent(parentComp);
		scrolledComp.setMinHeight(800);
		scrolledComp.setMinWidth(800);

		loadData();

	}

	/**
	 *
	 * Paint the chart
	 *
	 */
	public void paintComp() {
		if (chartComp == null || chartComp.isDisposed()) {
			return;
		}
		Control[] controls = chartComp.getChildren();
		for (Control control : controls) {
			control.dispose();
		}
		if (database.getDatabaseInfo().getDbSpaceInfoList() != null
				&& database.getDatabaseInfo().getDbSpaceInfoList().getSpaceinfo() != null) {
			Map<String, DbSpaceInfo> map = database.getDatabaseInfo().getDbSpaceInfoList().getSpaceInfoMap();

			if (map.containsKey(VolumeType.GENERIC.toString().toUpperCase())) {
				paintOnePie(map.get(VolumeType.GENERIC.toString().toUpperCase()));
			}
			if (map.containsKey(VolumeType.DATA.toString().toUpperCase())) {
				paintOnePie(map.get(VolumeType.DATA.toString().toUpperCase()));
			}
			if (map.containsKey(VolumeType.INDEX.toString().toUpperCase())) {
				paintOnePie(map.get(VolumeType.INDEX.toString().toUpperCase()));
			}
			if (map.containsKey(VolumeType.TEMP.toString().toUpperCase())) {
				paintOnePie(map.get(VolumeType.TEMP.toString().toUpperCase()));
			}
		}
	}

	/**
	 *
	 * Paint the pie chart
	 *
	 * @param dbSpaceInfo the DbSpace information
	 */

	public void paintOnePie(DbSpaceInfo dbSpaceInfo) {
		JFreeChart chart = createChart(createDataset(dbSpaceInfo), dbSpaceInfo);

		final ChartComposite frame = new ChartComposite(chartComp, SWT.NONE,
				chart, false, true, false, true, true);
		GridData gdDescGroup = new GridData(GridData.FILL_HORIZONTAL);
		gdDescGroup.heightHint = 220;

		frame.setLayoutData(gdDescGroup);
	}

	/**
	 *
	 * Create the chart dataset
	 *
	 * @param dbSpaceInfo the DbSpaceInfo
	 * @return the dataset
	 */
	private DefaultPieDataset createDataset(DbSpaceInfo dbSpaceInfo) {
		int freeSize = dbSpaceInfo.getFreepage();
		int totalSize = dbSpaceInfo.getTotalpage();

		DefaultPieDataset dataset = new DefaultPieDataset();

		dataset.setValue(
				Messages.chartMsgUsedSize,
				(totalSize - freeSize)
						* (database.getDatabaseInfo().getDbSpaceInfoList().getPagesize() / (1048576.0f)));
		dataset.setValue(
				Messages.chartMsgFreeSize,
				(freeSize)
						* (database.getDatabaseInfo().getDbSpaceInfoList().getPagesize() / (1048576.0f)));
		return dataset;

	}

	/**
	 * Create the jfreeChart of pie
	 *
	 * @param dataset the dataset
	 * @param dbSpaceInfo the DbSpaceInfo
	 * @return the chart
	 */
	private static JFreeChart createChart(DefaultPieDataset dataset,
			DbSpaceInfo dbSpaceInfo) {

		JFreeChart chart = ChartFactory.createPieChart3D(dbSpaceInfo.getType(),
				dataset, true, true, false);

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN,
				12));
		plot.setDirection(Rotation.ANTICLOCKWISE);
		plot.setCircular(false);
		plot.setLabelLinkMargin(0.0);
		plot.setLabelGap(0.0);
		plot.setLabelLinksVisible(false);
		plot.setOutlinePaint(ChartColor.VERY_DARK_BLUE);
		plot.setLabelGenerator(null);
		plot.setLegendLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
				"{0}:{1} Mbyte", new DecimalFormat("0.00"), new DecimalFormat(
						"0.00")));
		plot.setToolTipGenerator(new StandardPieToolTipGenerator(
				"{0}={1}Mbyte {2}", new DecimalFormat("0.00"),
				new DecimalFormat("0.00%")));
		java.awt.Color[] colors = {new java.awt.Color(235, 139, 82),
				new java.awt.Color(119, 119, 253) };
		PieRenderer renderer = new PieRenderer(colors);
		renderer.setColor(plot, dataset);
		return chart;

	}

	/**
	 *
	 * Initial the data
	 *
	 */
	private void initial() {
		if (database.getDatabaseInfo().getDbSpaceInfoList() == null) {
			return;
		}
		dbInfoListData.clear();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("0", Messages.lblDataBaseVersion);
		map1.put(
				"1",
				database.getServer().getServerInfo().getEnvInfo().getServerVersion()
						+ "                                                      ");
		dbInfoListData.add(map1);

		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("0", Messages.lblDataBaseStatus);
		map2.put(
				"1",
				(database.getDatabaseInfo().getRunningType() == DbRunningType.STANDALONE ? Messages.lblDataBaseStopStatus
						: Messages.lblDataBaseStartedStatus));
		dbInfoListData.add(map2);

		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("0", Messages.lblDataBaseUserAuthority);
		map3.put("1",
				database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName());
		dbInfoListData.add(map3);

		Map<String, String> map4 = new HashMap<String, String>();
		map4.put("0", Messages.lblDatabasePaseSize);
		map4.put("1", StringUtil.formatNumber(
				database.getDatabaseInfo().getDbSpaceInfoList().getPagesize(),
				"#,###")
				+ " bytes");
		dbInfoListData.add(map4);

		if (CompatibleUtil.isSupportLogPageSize(database.getServer().getServerInfo())) {
			Map<String, String> logPageSizeMap = new HashMap<String, String>();
			logPageSizeMap.put("0", Messages.tblVolumeFolderLogPageSize);
			logPageSizeMap.put(
					"1",
					StringUtil.formatNumber(
							database.getDatabaseInfo().getDbSpaceInfoList().getLogpagesize(),
							"#,###")
							+ " bytes");
			dbInfoListData.add(logPageSizeMap);
		}

		int totalSize = database.getDatabaseInfo().getDbSpaceInfoList().getTotalSize();
		int freeSize = database.getDatabaseInfo().getDbSpaceInfoList().getFreeSize();
		
		Map<String, String> map5 = new HashMap<String, String>();
		map5.put("0", Messages.lblDatabaseTotalSize);
		map5.put(
				"1",
				StringUtil.formatNumber(
						totalSize
								* (database.getDatabaseInfo().getDbSpaceInfoList().getPagesize() / (1048576.0f)),
						"#,###.##")
						+ "M ("
						+ StringUtil.formatNumber(totalSize, "#,###")
						+ " pages)");
		dbInfoListData.add(map5);

		Map<String, String> map6 = new HashMap<String, String>();
		map6.put("0", Messages.lblDatabaseRemainedSize);
		map6.put(
				"1",
				StringUtil.formatNumber(
						freeSize
								* (database.getDatabaseInfo().getDbSpaceInfoList().getPagesize() / (1048576.0f)),
						"#,###.##")
						+ "M ("
						+ StringUtil.formatNumber(freeSize, "#,###")
						+ " pages)");
		dbInfoListData.add(map6);
		
		if (!DbSpaceInfoList.useOld(database.getDatabaseInfo().getServerInfo().getEnvInfo())){
		
			if (dbSpaceDescriptionTable != null && !dbSpaceDescriptionTable.isDisposed()) {
				((DbSpaceInfoListNew)database.getDatabaseInfo().getDbSpaceInfoList()).createDbSpaceDescriptionData(dbSpaceDescriptionData);
				dbSpaceDescriptionTableViewer.refresh();
				for (int i = 0; i < dbSpaceDescriptionTable.getColumnCount(); i++) {
					dbSpaceDescriptionTable.getColumn(i).pack();
				}
				for (int i = 0; i < (1 + dbSpaceDescriptionTable.getItemCount()) / 2; i++) {
					dbSpaceDescriptionTable.getItem(i * 2).setBackground(color);
				}
			}
			
			((DbSpaceInfoListNew)database.getDatabaseInfo().getDbSpaceInfoList()).createFileSpaceDescriptionData(fileSpaceDescriptionData);
			if (fileSpaceDescriptionTable != null && !fileSpaceDescriptionTable.isDisposed()) {
				fileSpaceDescriptionTableViewer.refresh();
				for (int i = 0; i < fileSpaceDescriptionTable.getColumnCount(); i++) {
					fileSpaceDescriptionTable.getColumn(i).pack();
				}
				for (int i = 0; i < (1 + fileSpaceDescriptionTable.getItemCount()) / 2; i++) {
					fileSpaceDescriptionTable.getItem(i * 2).setBackground(color);
				}
			}
			
			((DbSpaceInfoListNew)database.getDatabaseInfo().getDbSpaceInfoList()).createVolumeDescriptionData(volumeDescriptionData);
			if (volumeDescriptionTable != null && !volumeDescriptionTable.isDisposed()) {
				volumeDescriptionTableViewer.refresh();
				for (int i = 0; i < volumeDescriptionTable.getColumnCount(); i++) {
					volumeDescriptionTable.getColumn(i).pack();
				}
				for (int i = 0; i < (1 + volumeDescriptionTable.getItemCount()) / 2; i++) {
					volumeDescriptionTable.getItem(i * 2).setBackground(color);
				}
			}
			
		}

		if (dbInfoTable != null && !dbInfoTable.isDisposed()) {
			dbInfoTableViewer.refresh();
			for (int i = 0; i < dbInfoTable.getColumnCount(); i++) {
				dbInfoTable.getColumn(i).pack();
			}
			for (int i = 0; i < (1 + dbInfoTable.getItemCount()) / 2; i++) {
				dbInfoTable.getItem(i * 2).setBackground(color);
			}
		}
	}

	/**
	 * Save operation and not implement
	 *
	 * @param monitor the monitor
	 */
	public void doSave(IProgressMonitor monitor) {
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * @return false
	 */
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 *
	 * Load data
	 *
	 * @return <code>true</code> whether it is successful;<code>false</code>
	 *         otherwise
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
						final DbSpaceInfoList dbSpaceInfoList = ((CommonQueryTask<? extends DbSpaceInfoList>)t).getResultModel();
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								database.getDatabaseInfo().setDbSpaceInfoList(
										dbSpaceInfoList);
								if (scrolledComp == null
										|| scrolledComp.isDisposed()) {
									return;
								}
								initial();
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
		String jobName = Messages.viewDbStatusJobName + " - " + dbName + "@"
				+ serverName;
		taskJobExecutor.schedule(jobName, null, false, Job.SHORT);
		return true;
		
	}

	/**
	 *
	 * Execute the tasks
	 *
	 * @param buttonId the button id
	 * @param tasks the tasks array
	 * @param cancelable whether can be cancelled
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
								InterruptedException {
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
	 * Call this method when node changed
	 *
	 * @param event the node changed event
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode eventNode = event.getCubridNode();
		if (eventNode == null
				|| event.getType() != CubridNodeChangedEventType.CONTAINER_NODE_REFRESH) {
			return;
		}
		ICubridNode node = eventNode.getChild(cubridNode == null ? ""
				: cubridNode.getId());
		if (node == null) {
			return;
		}
		cubridNode = node;
		synchronized (this) {
			if (database.getDatabaseInfo().getDbSpaceInfoList() == null) {
				loadData();
			}
		}
	}

	/**
	 *
	 * Create the common table viewer that can be sorted by TableViewerSorter
	 * object,this viewer's input object must be List<Map<String,Object>> and
	 * Map's key must be column index,Map's value of the column must be String.
	 *
	 * @param parent the parent composite
	 * @param sorter the sorter
	 * @param columnNameArr the column name array
	 * @param gridData the griddata
	 * @return the table viewer
	 */
	public static TableViewer createCommonTableViewer(Composite parent,
			ViewerSorter sorter, final String[] columnNameArr, GridData gridData) {
		final TableViewer tableViewer = new TableViewer(parent, SWT.MULTI
				| SWT.BORDER | SWT.FULL_SELECTION | SWT.NO_SCROLL);
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider());
		if (sorter != null) {
			tableViewer.setSorter(sorter);
		}

		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(gridData);

		for (int i = 0; i < columnNameArr.length; i++) {
			final TableColumn tblColumn = new TableColumn(
					tableViewer.getTable(), SWT.LEFT);
			tblColumn.setText(columnNameArr[i]);
			if (sorter != null) {
				tblColumn.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						TableColumn column = (TableColumn) event.widget;
						int j = 0;
						for (j = 0; j < columnNameArr.length; j++) {
							if (column.getText().equals(columnNameArr[j])) {
								break;
							}
						}
						TableViewerSorter sorter = ((TableViewerSorter) tableViewer.getSorter());
						if (sorter == null) {
							return;
						}
						sorter.doSort(j);
						tableViewer.getTable().setSortColumn(column);
						tableViewer.getTable().setSortDirection(
								sorter.isAsc() ? SWT.UP : SWT.DOWN);
						tableViewer.refresh();
						for (int k = 0; k < tableViewer.getTable().getColumnCount(); k++) {
							tableViewer.getTable().getColumn(k).pack();
						}
					}
				});
			}
			tblColumn.pack();
		}
		return tableViewer;
	}

	public void doSaveAs() {
		noOp();
	}
}
