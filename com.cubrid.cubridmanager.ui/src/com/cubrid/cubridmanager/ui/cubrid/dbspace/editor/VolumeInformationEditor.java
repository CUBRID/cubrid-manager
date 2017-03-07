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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoListNew;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoListOld;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.control.PieRenderer;
import com.cubrid.cubridmanager.ui.cubrid.database.editor.DatabaseStatusEditor;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;

/**
 *
 * This query editor part is responsible to show the status of database
 *
 * @author robin 20090318
 */
public class VolumeInformationEditor extends
		CubridEditorPart {

	private static final Logger LOGGER = LogUtil.getLogger(VolumeInformationEditor.class);
	public static final String ID = "com.cubrid.cubridmanager.ui.cubrid.dbspace.editor.VolumeInformationEditor";
	private CubridDatabase database = null;
	private DbSpaceInfoList.DbSpaceInfo dbSpaceInfo = null;
	private DbSpaceInfoListNew.VolumeInfo volumeInfo = null;
	public static boolean isChanged = false;
	private boolean isRunning = false;

	private final List<Map<String, String>> spInfoListData = new ArrayList<Map<String, String>>();
	private TableViewer spInfoTableViewer;
	private Table spInfoTable;
	private Label spaceNameLabel;
	private Composite parentComp;
	private Composite chartComp;
	
	private int majorVersion, minorVersion;

	private ScrolledComposite scrolledComp = null;
	private final org.eclipse.swt.graphics.Color color;

	public VolumeInformationEditor() {
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
			String type = node.getType();
			
			database = ((DefaultSchemaNode) node).getDatabase();
			String fullVersion = database.getServer().getServerInfo().getEnvInfo().getServerVersion();
			StringTokenizer st = new StringTokenizer(fullVersion);
			st.nextToken();
			String versionNo = st.nextToken();
			
			majorVersion = Integer.parseInt(versionNo.substring(0, versionNo.indexOf('.')));
			minorVersion = Integer.parseInt(versionNo.substring(versionNo.indexOf('.')+1));
			
			if (majorVersion < 10 || (majorVersion == 10 && minorVersion == 0)) {
				if (CubridNodeType.GENERIC_VOLUME.equals(type)
						|| CubridNodeType.DATA_VOLUME.equals(type)
						|| CubridNodeType.INDEX_VOLUME.equals(type)
						|| CubridNodeType.TEMP_VOLUME.equals(type)
						|| CubridNodeType.ARCHIVE_LOG.equals(type)
						|| CubridNodeType.ACTIVE_LOG.equals(type)) {
					dbSpaceInfo = (DbSpaceInfoList.DbSpaceInfo) ((DefaultSchemaNode) node).getAdapter(DbSpaceInfoList.DbSpaceInfo.class);
				}
			} else {
				if (CubridNodeType.PP_VOLUME_FOLDER.equals(type) ||
					CubridNodeType.PT_VOLUME_FOLDER.equals(type) ||
					CubridNodeType.TT_VOLUME_FOLDER.equals(type)) {
					volumeInfo = (DbSpaceInfoListNew.VolumeInfo) ((DefaultSchemaNode) node).getAdapter(DbSpaceInfoListNew.VolumeInfo.class);
				}
			}
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
		spaceNameLabel.setFont(ResourceManager.getFont("", 20, SWT.BOLD));

		spaceNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		spaceNameLabel.setText("");

		final String[] columnNameArr = new String[]{"col1", "col2" };
		spInfoTableViewer = DatabaseStatusEditor.createCommonTableViewer(descComp, null,
				columnNameArr, CommonUITool.createGridData(GridData.FILL_BOTH, 1,
						1, -1, -1));
		spInfoTableViewer.setInput(spInfoListData);
		spInfoTable = spInfoTableViewer.getTable();
		spInfoTable.setLinesVisible(true);
		spInfoTable.setHeaderVisible(false);

		chartComp = new Composite(parentComp, SWT.NONE);
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		GridData gdChartComp = new GridData();
		chartComp.setLayoutData(gdChartComp);
		chartComp.setLayout(layout);
		chartComp.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		loadData();
	}

	/**
	 * paint composite
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
		JFreeChart chart = createChart(createDataset());

		final ChartComposite frame = new ChartComposite(chartComp, SWT.NONE,
				chart, false, true, false, true, true);

		GridData gdDescGroup = new GridData(GridData.FILL_HORIZONTAL);
		gdDescGroup.widthHint = 600;
		gdDescGroup.heightHint = 400;
		frame.setLayoutData(gdDescGroup);

	}

	/**
	 * create the dataset
	 *
	 * @return dataset
	 */
	private DefaultPieDataset createDataset() {
		int freeSize, totalSize;
		if (majorVersion < 10 || (majorVersion == 10 && minorVersion == 0)) {
			freeSize = dbSpaceInfo.getFreepage();
			totalSize = dbSpaceInfo.getTotalpage();
		} else {
			freeSize = volumeInfo.getFree_size();
			totalSize = volumeInfo.getTotal_size();
		}

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
	 * initializes some values
	 *
	 */
	private void initial() {
		if (database == null || database.getDatabaseInfo() == null) {
			return;
		}
		if (database.getDatabaseInfo().getDbSpaceInfoList() == null) {
			return;
		}
		
		int totalSize, freeSize;
		String volumeLocation, volumeDate, volumeType, volumePurpose, spacename;
		
		if (majorVersion < 10 || (majorVersion == 10 && minorVersion == 0)) {
			totalSize = dbSpaceInfo.getTotalpage();
			freeSize = dbSpaceInfo.getFreepage();
			volumeType = dbSpaceInfo.getType();
			volumeDate = dbSpaceInfo.getDate();
			spacename = dbSpaceInfo.getSpacename();
			volumeLocation = dbSpaceInfo.getLocation();
		} else {
			totalSize = volumeInfo.getTotal_size();
			freeSize = volumeInfo.getFree_size();
			volumeType = volumeInfo.getType();
			volumePurpose = volumeInfo.getPurpose();
			spacename = volumeInfo.getShortVolumeName();
			volumeLocation = volumeInfo.getVolume_name();
		}
	
		spaceNameLabel.setText(spacename);

		while (!spInfoListData.isEmpty()) {
			spInfoListData.remove(0);
		}
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("0", Messages.lblSpaceLocation);
		map1.put("1", volumeLocation);
		spInfoListData.add(map1);

		Map<String, String> map2 = new HashMap<String, String>();
		Map<String, String> map3 = new HashMap<String, String>();
		if (majorVersion < 10 || (majorVersion == 10 && minorVersion == 0)) {
			map2.put("0", Messages.lblSpaceDate);
			map2.put("1", dbSpaceInfo.getDate());
			map3.put("0", Messages.lblSpaceType);
			map3.put(
					"1",
					volumeType
							+ "                                                                               ");
		} else {
			map2.put("0", Messages.lblSpaceType);
			map2.put(
					"1",
					volumeInfo.getType()
							+ "                                                                               ");
			map3.put("0", "Purpose");
			map3.put(
					"1",
					volumeInfo.getPurpose()
							+ "                                                                               ");
		}	
		spInfoListData.add(map2);
		spInfoListData.add(map3);

		Map<String, String> map4 = new HashMap<String, String>();
		map4.put("0", Messages.lblFreeSize);
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
		spInfoListData.add(map5);

		Map<String, String> map6 = new HashMap<String, String>();
		map6.put("0", Messages.lblDatabasePaseSize);
		map6.put("1", StringUtil.formatNumber(
				database.getDatabaseInfo().getDbSpaceInfoList().getPagesize(),
				"#,###")
				+ " byte");
		spInfoListData.add(map6);
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
		// empty

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
		// empty

	}

	/**
	 * load the data
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
						final DbSpaceInfoList model;
						if (majorVersion < 10 || (majorVersion == 10 && minorVersion == 0)) {
							model = ((CommonQueryTask<DbSpaceInfoListOld>) t).getResultModel();
						} else {
							model = ((CommonQueryTask<DbSpaceInfoListNew>) t).getResultModel();
						}
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								database.getDatabaseInfo().setDbSpaceInfoList(
										model);
								if (scrolledComp == null
										|| scrolledComp.isDisposed()) {
									return;
								}
								initial();
								paintComp();
								scrolledComp.setContent(parentComp);
								scrolledComp.setExpandHorizontal(true);
								scrolledComp.setExpandVertical(true);
								scrolledComp.setMinHeight(800);
								scrolledComp.setMinWidth(800);
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
		String spacename;
		if (majorVersion < 10 || (majorVersion == 10 && minorVersion == 0)) {
			CommonQueryTask<DbSpaceInfoListOld> task = new CommonQueryTask<DbSpaceInfoListOld>(
					database.getServer().getServerInfo(),
					CommonSendMsg.getCommonDatabaseSendMsg(), new DbSpaceInfoListOld());
			task.setDbName(database.getName());
			taskJobExecutor.addTask(task);
			spacename = dbSpaceInfo.getSpacename();
		} else {
			CommonQueryTask<DbSpaceInfoListNew> task = new CommonQueryTask<DbSpaceInfoListNew>(
					database.getServer().getServerInfo(),
					CommonSendMsg.getCommonDatabaseSendMsg(), new DbSpaceInfoListNew());
			task.setDbName(database.getName());
			taskJobExecutor.addTask(task);
			spacename = volumeInfo.getShortVolumeName();
		}
		
		String jobName = Messages.viewVolumeInfoJobName + " - "
				+ spacename + "@" + database.getName() + "@"
				+ database.getServer().getName();
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
	 * @see com.cubrid.common.ui.spi.event.ICubridNodeChangedListener#nodeChanged(com.cubrid.common.ui.spi.event.CubridNodeChangedEvent)
	 * @param event the CubridNodeChangedEvent object
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode eventNode = event.getCubridNode();
		if (eventNode == null
				|| event.getType() != CubridNodeChangedEventType.CONTAINER_NODE_REFRESH) {
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
	 * Create jfreechart
	 *
	 * @param dataset DefaultPieDataset
	 * @return JFreeChart
	 */
	private static JFreeChart createChart(DefaultPieDataset dataset) {
		JFreeChart chart = ChartFactory.createPieChart3D("", // chart

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

		plot.setLabelGenerator(null);
		plot.setLabelLinksVisible(false);
		plot.setLegendLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
				"{0}:{1} Mbyte", new DecimalFormat("0.00"), new DecimalFormat(
						"0.00")));
		plot.setToolTipGenerator(new StandardPieToolTipGenerator(
				"{0}={1}Mbyte {2}", new DecimalFormat("0.00"),
				new DecimalFormat("0.00%")));
		// plot.setSectionPaint("", SWTResourceManager.getColor(230, 230, 230));
		Color[] colors = {new Color(235, 139, 82), new Color(119, 119, 253) };
		PieRenderer renderer = new PieRenderer(colors);
		renderer.setColor(plot, dataset);
		return chart;

	}
}
