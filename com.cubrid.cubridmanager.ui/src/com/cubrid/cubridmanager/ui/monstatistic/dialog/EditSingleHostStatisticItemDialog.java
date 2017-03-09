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
package com.cubrid.cubridmanager.ui.monstatistic.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.GetDatabaseListTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.monstatistic.model.SingleHostChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.TimeType;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.monstatistic.editor.MonitorStatisticEditor;

/**
 * This type provides a dialog for user to configuration a monitor statistic
 * item in monitor statistic page
 * 
 * @author Santiago Wang
 * @version 1.0 - 2013-08-31 created by Santiago Wang
 */
public class EditSingleHostStatisticItemDialog extends
		CMTitleAreaDialog {

	private static final Logger LOGGER = LogUtil.getLogger(EditSingleHostStatisticItemDialog.class);

	private final MonitorStatisticEditor editor;
	private final ServerInfo serverInfo;

	private final String[] ITEMS_EMPTY = new String[0];
	private final String VALUE_DEFAULT = "";
	private final int LINE_HEIGHT = 30;
	private final int DIALOG_AREA_WIDTH = 600;

	private final String[] itemsDataType;
	private final String[] itemsTimeType;
	private String[] itemsDbName;
	private final String[] itemsBrokerName;
	/*
	 * isOkEnable[0]: whether check some metric button
	 * isOkEnable[1]: whether has available active DB
	 * isOkEnable[2]: whether has available volume name for specify DB
	 * isOkEnable[3]: whether has available broker
	 */
	private final boolean isOkEnable[];

	private StatisticChartItem statisticChartItem;
	private String[] itemsVolName;

	private Composite compDataType;
	private Composite compMetrics;

	private Label lblDbName;
	private Label lblVolName;
	private Label lblBrokerName;

	private Combo comboDataType;
	private Combo comboTimeType;
	private Combo comboDbName;
	private Combo comboBrokerName;
	private Combo comboVolName;

	private List<Group> grpMetricList = new ArrayList<Group>();
	private Button btnClearAll;

	private String dataTypeValue;
	private String timeValue;
	private String dbNameValue;
	private String dbVolNameValue;
	private String brokerNameValue;
	
	private StatisticType oldDataType;
	private boolean isNew = true;

	/**
	 * Constructor
	 * 
	 * @param parentShell
	 */
	public EditSingleHostStatisticItemDialog(Shell parentShell,
			MonitorStatisticEditor editor, ServerInfo serverInfo) {
		super(parentShell);
		this.editor = editor;
		this.serverInfo = serverInfo;

		isOkEnable = new boolean[4];
		for (int i = 0; i < isOkEnable.length; i++) {
			isOkEnable[i] = true;
		}

		itemsDataType = new String[]{StatisticType.DB.getMessage(),
				StatisticType.DB_VOL.getMessage(),
				StatisticType.BROKER.getMessage(),
				StatisticType.OS.getMessage() };
		dataTypeValue = itemsDataType[0];
		oldDataType = StatisticType.DB;

		itemsTimeType = new String[]{TimeType.DAILY.getMessage(),
				TimeType.WEEKLY.getMessage(), TimeType.MONTHLY.getMessage(),
				TimeType.YEARLY.getMessage() };
		timeValue = itemsTimeType[0];

		//initial active DB name list
		List<String> dbNameList = this.serverInfo.getAllDatabaseList();
		boolean isDbDataValid = false;
		if (dbNameList != null && dbNameList.size() > 0) {
			GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
					this.serverInfo);
			getDatabaseListTask.execute();
			if (getDatabaseListTask.isSuccess()) {
				List<DatabaseInfo> dbInfoList = getDatabaseListTask.loadDatabaseInfo();
				int activeDbCount = 0;
				for (DatabaseInfo dbInfo : dbInfoList) {
					if (DbRunningType.CS.equals(dbInfo.getRunningType())) {
						for (String dbName : dbNameList) {
							if (dbName.equals(dbInfo.getDbName())) {
								activeDbCount++;
								break;
							}
						}
					}
				}
				if (activeDbCount != 0) {
					isDbDataValid = true;
					itemsDbName = new String[activeDbCount];
					int itemsDbNameIndex = 0;
					for (DatabaseInfo dbInfo : dbInfoList) {
						if (DbRunningType.CS.equals(dbInfo.getRunningType())) {
							for (String dbName : dbNameList) {
								if (dbName.equals(dbInfo.getDbName())) {
									itemsDbName[itemsDbNameIndex++] = dbName;
									break;
								}
							}
						}
					}
					dbNameValue = itemsDbName[0];
				}
			}
		}
		if (!isDbDataValid) {
			itemsDbName = ITEMS_EMPTY;
			dbNameValue = VALUE_DEFAULT;
		}

		if (serverInfo.getBrokerInfos() != null
				&& serverInfo.getBrokerInfos().getBorkerInfoList() != null
				&& serverInfo.getBrokerInfos().getBorkerInfoList().getBrokerInfoList() != null
				&& serverInfo.getBrokerInfos().getBorkerInfoList().getBrokerInfoList().size() > 0) {
			List<BrokerInfo> brokerInfoList = serverInfo.getBrokerInfos().getBorkerInfoList().getBrokerInfoList();
			itemsBrokerName = new String[brokerInfoList.size()];
			for (int i = 0; i < brokerInfoList.size(); i++) {
				itemsBrokerName[i] = brokerInfoList.get(i).getName();
			}
			brokerNameValue = itemsBrokerName[0];
		} else {
			itemsBrokerName = ITEMS_EMPTY;
			brokerNameValue = VALUE_DEFAULT;
		}
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * 
	 * @param parent The parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		final Composite parentComp = (Composite) super.createDialogArea(parent);
		parentComp.setLayout(new GridLayout(1, false));
		GridData gdParent = new GridData(SWT.FILL);
		gdParent.widthHint = DIALOG_AREA_WIDTH;
		gdParent.heightHint = LINE_HEIGHT * 11;
		parentComp.setLayoutData(gdParent);

		//init data
		StatisticType statisticType = StatisticType.DB;
		if (!isNew && statisticChartItem != null) {
			statisticType = statisticChartItem.getType();
			oldDataType = statisticType;
			dataTypeValue = statisticType.getMessage();
			TimeType timeType = TimeType.getEnumByType(statisticChartItem.getDType());
			timeValue = timeType == null ? TimeType.DAILY.getMessage()
					: timeType.getMessage();
			switch (statisticType) {
			case DB:
				dbNameValue = ((SingleHostChartItem) statisticChartItem).getDbName();
				break;
			case DB_VOL:
				dbNameValue = ((SingleHostChartItem) statisticChartItem).getDbName();
				dbVolNameValue = ((SingleHostChartItem) statisticChartItem).getVolName();
				break;
			case BROKER:
				brokerNameValue = ((SingleHostChartItem) statisticChartItem).getBrokerName();
				break;
			case OS:
				break;
			default:
			}
		}

		compDataType = new Composite(parentComp, SWT.RESIZE);
		GridLayout layoutCompDataType = new GridLayout(4, false);
		compDataType.setLayout(layoutCompDataType);

		final Label lblDataType = new Label(compDataType, SWT.NONE);
		GridData gdlblDataType = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gdlblDataType.widthHint = 120;
		lblDataType.setText(Messages.lblDataType);

		comboDataType = new Combo(compDataType, SWT.NONE | SWT.READ_ONLY);
		GridData gdComboDataType = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gdComboDataType.widthHint = 180;
		comboDataType.setLayoutData(gdComboDataType);
		comboDataType.setItems(itemsDataType);
		comboDataType.setText(dataTypeValue);
		comboDataType.addSelectionListener(new ComboDataTypeSelectionAdapter());

		final Label lblTimeType = new Label(compDataType, SWT.NONE);
		GridData gdlblTimeType = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gdlblTimeType.widthHint = 120;
		lblTimeType.setText(Messages.lblTimeType);

		comboTimeType = new Combo(compDataType, SWT.NONE | SWT.READ_ONLY);
		GridData gdComboTimeType = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gdComboTimeType.widthHint = 180;
		comboTimeType.setLayoutData(gdComboTimeType);
		comboTimeType.setItems(itemsTimeType);
		comboTimeType.setText(timeValue);
		
		compMetrics = new Composite(parentComp, SWT.RESIZE);

		refreshChangeableItems(statisticType);

		if (!isNew && statisticChartItem != null) {
			List<String> metricList = ((SingleHostChartItem) statisticChartItem).getMetricList();
			for (Group grp : grpMetricList) {
				if (grp.getChildren() == null) {
					continue;
				}
				boolean isFindInGroup = false;
				for (Control control : grp.getChildren()) {
					if (control instanceof Button) {
						Button btn = ((Button) control);
						btn.setEnabled(false);
						String msg = btn.getText();
						MetricType metricType = MetricType.getEnumByMessage(msg);
						for (String metric : metricList) {
							if (metric.equals(metricType.getMetric())) {
								btn.setSelection(true);
								isFindInGroup = true;
								break;
							}
						}
					}
				}
				if (isFindInGroup) {
					for (Control control : grp.getChildren()) {
						if (control instanceof Button) {
							((Button) control).setEnabled(true);
						}
					}
				}
			}
		}

		//add btn
		final Composite btnComp = new Composite(parentComp, SWT.NONE);
		btnComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		{
			GridLayout layout = new GridLayout();
			layout.marginRight = 5;
			layout.numColumns = 1;
			layout.marginWidth = 0;
			btnComp.setLayout(layout);
		}

		btnClearAll = new Button(btnComp, SWT.NONE);
		btnClearAll.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false));
		btnClearAll.setText(Messages.btnCleanSelected);
		btnClearAll.addSelectionListener(new ButtonCleanAllSelectionAdapter());

		return parentComp;
	}

	private void addDbNameItem(Composite parent) {
		lblDbName = new Label(parent, SWT.NONE);
		lblDbName.setText(Messages.lblDbName);

		comboDbName = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
		comboDbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		comboDbName.setItems(itemsDbName);
		comboDbName.setText(dbNameValue);
		comboDbName.addSelectionListener(new ComboDbNameSelectionAdapter());
	}

	private void addVolNameItem(Composite parent, String dbName) {
		lblVolName = new Label(parent, SWT.NONE);
		lblVolName.setText(Messages.lblVolName);

		comboVolName = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
		comboVolName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		refreshDbVolData(dbName);
		comboVolName.setItems(itemsVolName);
		comboVolName.setText(dbVolNameValue);
	}

	private void addBrokerNameItem(Composite parent) {
		lblBrokerName = new Label(parent, SWT.NONE);
		lblBrokerName.setText(Messages.lblBrokerName);

		comboBrokerName = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
		comboBrokerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		comboBrokerName.setItems(itemsBrokerName);
		comboBrokerName.setText(brokerNameValue);
	}

	private void addDbMetricsArea(Composite parent) {
		GridLayout layoutParent = new GridLayout(4, false);
		parent.setLayout(layoutParent);
		GridData gdParent = new GridData(GridData.FILL_HORIZONTAL);
		parent.setLayoutData(gdParent);

		final Group grpMetricCpu = new Group(parent, SWT.NONE);
		grpMetricCpu.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpMetricCpu.setText(Messages.msgGpCpuUsage);
		grpMetricCpu.setLayout(new GridLayout(1, false));
		grpMetricList.add(grpMetricCpu);

		Button btnDbCpuKernel = new Button(grpMetricCpu, SWT.CHECK);
		btnDbCpuKernel.setText(MetricType.DB_CPU_KERNEL.getMessage());
		//		btnDbCpuKernel.addSelectionListener(new MetricBtnSelectionListener());

		Button btnDbCpuUser = new Button(grpMetricCpu, SWT.CHECK);
		btnDbCpuUser.setText(MetricType.DB_CPU_USER.getMessage());
		//		btnDbCpuUser.addSelectionListener(new MetricBtnSelectionListener());
		//
		final Group grpMetricMemory = new Group(parent, SWT.NONE);
		grpMetricMemory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		grpMetricMemory.setText(Messages.msgGpMemory);
		GridLayout gl_grpMetric = new GridLayout(1, false);
		gl_grpMetric.horizontalSpacing = 4;
		grpMetricMemory.setLayout(gl_grpMetric);
		grpMetricList.add(grpMetricMemory);

		Button btnDbMemPhy = new Button(grpMetricMemory, SWT.CHECK);
		btnDbMemPhy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDbMemPhy.setText(MetricType.DB_MEM_PHY.getMessage());
		//		btnDbMemPhy.addSelectionListener(new MetricBtnSelectionListener());

		Button btnDbMemVir = new Button(grpMetricMemory, SWT.CHECK);
		btnDbMemVir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDbMemVir.setText(MetricType.DB_MEM_VIR.getMessage());
		//		btnDbMemVir.addSelectionListener(new MetricBtnSelectionListener());
		//
		final Group grpApplication = new Group(parent, SWT.NONE);
		grpApplication.setLayout(new GridLayout(1, false));
		grpApplication.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpApplication.setText(Messages.msgGpApplication);
		grpMetricList.add(grpApplication);

		Button btnDbQps = new Button(grpApplication, SWT.CHECK);
		btnDbQps.setText(MetricType.DB_QPS.getMessage());
		//		btnDbQps.addSelectionListener(new MetricBtnSelectionListener());

		Button btnDbTps = new Button(grpApplication, SWT.CHECK);
		btnDbTps.setText(MetricType.DB_TPS.getMessage());
		//		btnDbTps.addSelectionListener(new MetricBtnSelectionListener());
		//
		final Group grpIo = new Group(parent, SWT.NONE);
		grpIo.setLayout(new GridLayout(1, false));
		grpIo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpIo.setText(Messages.msgGpIo);
		grpMetricList.add(grpIo);

		Button btnDbIoRead = new Button(grpIo, SWT.CHECK);
		btnDbIoRead.setText(MetricType.DB_IO_READ.getMessage());
		//		btnDbIoRead.addSelectionListener(new MetricBtnSelectionListener());

		Button btnDbIoWrite = new Button(grpIo, SWT.CHECK);
		btnDbIoWrite.setText(MetricType.DB_IO_WRITE.getMessage());
		//		btnDbIoWrite.addSelectionListener(new MetricBtnSelectionListener());
		//
		final Group grpPages = new Group(parent, SWT.NONE);
		grpPages.setLayout(new GridLayout(1, false));
		grpPages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		grpPages.setText(Messages.msgGpPages);
		grpMetricList.add(grpPages);

		Button btnDbHitRatio = new Button(grpPages, SWT.CHECK);
		btnDbHitRatio.setText(MetricType.DB_HIT_RATIO.getMessage());
		//		btnDbHitRatio.addSelectionListener(new MetricBtnSelectionListener());

		Button btnDbDirtyPages = new Button(grpPages, SWT.CHECK);
		btnDbDirtyPages.setText(MetricType.DB_DIRTY_PAGES.getMessage());
		//		btnDbDirtyPages.addSelectionListener(new MetricBtnSelectionListener());

		Button btnDbFetchPages = new Button(grpPages, SWT.CHECK);
		btnDbFetchPages.setText(MetricType.DB_FETCH_PAGES.getMessage());
		//		btnDbFetchPages.addSelectionListener(new MetricBtnSelectionListener());
		//
		final Group grpHa = new Group(parent, SWT.NONE);
		grpHa.setLayout(new GridLayout(1, false));
		grpHa.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpHa.setText(Messages.msgGpHa);
		grpMetricList.add(grpHa);
		
		Button btnDbHaCopyDelayPage = new Button(grpHa, SWT.CHECK);
		btnDbHaCopyDelayPage.setText(MetricType.DB_HA_COPY_DELAY_PAGE.getMessage());

		Button btnDbHaCopyDelayEstimated = new Button(grpHa, SWT.CHECK);
		btnDbHaCopyDelayEstimated.setText(MetricType.DB_HA_COPY_DELAY_ESTIMATED.getMessage());
		//		btnDbHaCopyDelayEstimated.addSelectionListener(new MetricBtnSelectionListener());

		Button btnDbHaApplyDelayPage = new Button(grpHa, SWT.CHECK);
		btnDbHaApplyDelayPage.setText(MetricType.DB_HA_APPLY_DELAY_PAGE.getMessage());
		//		btnDbHaApplyDelayPage.addSelectionListener(new MetricBtnSelectionListener());

		Button btnDbHaApplyDelayEstimated = new Button(grpHa, SWT.CHECK);
		btnDbHaApplyDelayEstimated.setText(MetricType.DB_HA_APPLY_DELAY_ESTIMATED.getMessage());
		//		btnDbHaApplyDelayEstimated.addSelectionListener(new MetricBtnSelectionListener());
		//
		final Group grpFreeSpace = new Group(parent, SWT.NONE);
		grpFreeSpace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpFreeSpace.setText(MetricType.DB_FREESPACE.getMessage());
		grpFreeSpace.setLayout(new GridLayout(1, false));
		grpMetricList.add(grpFreeSpace);

		Button btnDbFreeSpace = new Button(grpFreeSpace, SWT.CHECK);
		btnDbFreeSpace.setText(MetricType.DB_FREESPACE.getMessage());
		//		btnDbFreeSpace.addSelectionListener(new MetricBtnSelectionListener());
		
		initMetircBtns();
	}
	
	private void addDbVolMetricsArea(Composite parent) {
		GridLayout layoutParent = new GridLayout(1, false);
		parent.setLayout(layoutParent);
		GridData gdParent = new GridData(GridData.FILL_HORIZONTAL);
		parent.setLayoutData(gdParent);

		final Group grpMetricDbVol = new Group(parent, SWT.NONE);
		grpMetricDbVol.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpMetricDbVol.setText(Messages.msgGpVolSpace);
		grpMetricDbVol.setLayout(new GridLayout(4, false));
		grpMetricList.add(grpMetricDbVol);

		Button btnVolFreeSpace = new Button(grpMetricDbVol, SWT.CHECK);
		btnVolFreeSpace.setText(MetricType.VOL_FREESPACE.getMessage());
		
		initMetircBtns();
	}
	
	private void addBrokerMetricsArea(Composite parent) {
		GridLayout layoutParent = new GridLayout(1, false);
		parent.setLayout(layoutParent);
		GridData gdParent = new GridData(GridData.FILL_HORIZONTAL);
		parent.setLayoutData(gdParent);

		final Group grpMetricBroker = new Group(parent, SWT.NONE);
		grpMetricBroker.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpMetricBroker.setText(Messages.msgGpBrokerInfo);
		grpMetricBroker.setLayout(new GridLayout(4, false));
		grpMetricList.add(grpMetricBroker);

		Button btnBrokerTps = new Button(grpMetricBroker, SWT.CHECK);
		btnBrokerTps.setText(MetricType.BROKER_TPS.getMessage());
		
		Button btnBrokerQps = new Button(grpMetricBroker, SWT.CHECK);
		btnBrokerQps.setText(MetricType.BROKER_QPS.getMessage());
		
		Button btnBrokerLongT = new Button(grpMetricBroker, SWT.CHECK);
		btnBrokerLongT.setText(MetricType.BROKER_LONG_T.getMessage());
		
		Button btnBrokerLongQ = new Button(grpMetricBroker, SWT.CHECK);
		btnBrokerLongQ.setText(MetricType.BROKER_LONG_Q.getMessage());
		
		Button btnBrokerReq = new Button(grpMetricBroker, SWT.CHECK);
		btnBrokerReq.setText(MetricType.BROKER_REQ.getMessage());
		
		Button btnBrokerErrQ = new Button(grpMetricBroker, SWT.CHECK);
		btnBrokerErrQ.setText(MetricType.BROKER_ERR_Q.getMessage());
		
		Button btnBrokerJq = new Button(grpMetricBroker, SWT.CHECK);
		btnBrokerJq.setText(MetricType.BROKER_JQ.getMessage());
		
		initMetircBtns();
	}
	
	private void addOsMetricsArea(Composite parent) {
		GridLayout layoutParent = new GridLayout(3, false);
		parent.setLayout(layoutParent);
		GridData gdParent = new GridData(GridData.FILL_HORIZONTAL);
		parent.setLayoutData(gdParent);

		final Group grpMetricOsCpu = new Group(parent, SWT.NONE);
		grpMetricOsCpu.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpMetricOsCpu.setText(Messages.msgGpCpuUsage);
		grpMetricOsCpu.setLayout(new GridLayout(1, false));
		grpMetricList.add(grpMetricOsCpu);

		Button btnCpuIdle = new Button(grpMetricOsCpu, SWT.CHECK);
		btnCpuIdle.setText(MetricType.OS_CPU_IDLE.getMessage());
		
		Button btnCpuIoWait = new Button(grpMetricOsCpu, SWT.CHECK);
		btnCpuIoWait.setText(MetricType.OS_CPU_IOWAIT.getMessage());
		
		Button btnCpuKernel = new Button(grpMetricOsCpu, SWT.CHECK);
		btnCpuKernel.setText(MetricType.OS_CPU_KERNEL.getMessage());
		
		Button btnCpuUser = new Button(grpMetricOsCpu, SWT.CHECK);
		btnCpuUser.setText(MetricType.OS_CPU_USER.getMessage());
		//
		final Group grpMetricOsMem = new Group(parent, SWT.NONE);
		grpMetricOsMem.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpMetricOsMem.setText(Messages.msgGpCpuUsage);
		grpMetricOsMem.setLayout(new GridLayout(1, false));
		grpMetricList.add(grpMetricOsMem);

		Button btnMemPhyFree = new Button(grpMetricOsMem, SWT.CHECK);
		btnMemPhyFree.setText(MetricType.OS_MEM_PHY_FREE.getMessage());

		Button btnMemSwapFree = new Button(grpMetricOsMem, SWT.CHECK);
		btnMemSwapFree.setText(MetricType.OS_MEM_SWAP_FREE.getMessage());
		//
		final Group grpMetricOsFreeSpace = new Group(parent, SWT.NONE);
		grpMetricOsFreeSpace.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		grpMetricOsFreeSpace.setText(Messages.msgGpCpuUsage);
		grpMetricOsFreeSpace.setLayout(new GridLayout(1, false));
		grpMetricList.add(grpMetricOsFreeSpace);

		Button btnDiskFree = new Button(grpMetricOsFreeSpace, SWT.CHECK);
		btnDiskFree.setText(MetricType.OS_DISK_FREE.getMessage());

		initMetircBtns();
	}

	private void initMetircBtns() {
		for (int i = 0; i < grpMetricList.size(); i++) {
			if (grpMetricList.get(i) == null
					|| grpMetricList.get(i).getChildren() == null) {
				continue;
			}
			for (Control control : grpMetricList.get(i).getChildren()) {
				if (control instanceof Button) {
					((Button) control).addSelectionListener(new RadioMetricSelectionAdapter());
				}
			}
			/*if (i == 0) {
				Control[] ctrls = grpMetricList.get(i).getChildren();
				for (int j = 0; j < ctrls.length; j++) {
					if (ctrls[j] instanceof Button) {
						if (j == 0) {
							((Button) ctrls[j]).setSelection(true);
						}
						((Button) ctrls[j]).addSelectionListener(new MetricBtnSelectionListener());
					}
				}
			} else {
				for (Control control : grpMetricList.get(i).getChildren()) {
					if (control instanceof Button) {
						((Button) control).setEnabled(false);
						((Button) control).addSelectionListener(new MetricBtnSelectionListener());
					}
				}
			}*/
		}
	}

	//refresh data of itemsVolName and dbVolName
	private void refreshDbVolData(String dbName) {
		DbSpaceInfoList dbSpaceInfo = new DbSpaceInfoList();
		CommonQueryTask<DbSpaceInfoList> dbSpaceInfoTask = new CommonQueryTask<DbSpaceInfoList>(
				serverInfo, CommonSendMsg.getCommonDatabaseSendMsg(),
				dbSpaceInfo);
		dbSpaceInfoTask.setDbName(dbName);
		dbSpaceInfoTask.execute();
		if (dbSpaceInfoTask.isSuccess()) {
			dbSpaceInfo = dbSpaceInfoTask.getResultModel();
			if (dbSpaceInfo.getSpaceinfo() != null
					&& dbSpaceInfo.getSpaceinfo().size() > 0) {
				List<DbSpaceInfo> spaceInfoList = dbSpaceInfo.getSpaceinfo();
				itemsVolName = new String[spaceInfoList.size()];
				for (int i = 0; i < spaceInfoList.size(); i++) {
					itemsVolName[i] = spaceInfoList.get(i).getSpacename();
				}
				dbVolNameValue = itemsVolName[0];
			} else {
				itemsVolName = ITEMS_EMPTY;
				dbVolNameValue = VALUE_DEFAULT;
			}
		} else {
			itemsVolName = ITEMS_EMPTY;
			dbVolNameValue = VALUE_DEFAULT;
		}
	}

	/**
	 * Add or change items that can be changed. Such as DB name combo, DB volume
	 * name combo, broker name combo and metric combo.
	 * 
	 * @param type
	 * @param isRefreshDbVol
	 */
	private void refreshChangeableItems(final StatisticType type) {
		GridData gdCompDataType = new GridData(GridData.FILL_HORIZONTAL);
		switch (type) {
		case DB:
			gdCompDataType.heightHint = LINE_HEIGHT * 2;
			compDataType.setLayoutData(gdCompDataType);
			addDbNameItem(compDataType);
			addDbMetricsArea(compMetrics);
			isOkEnable[1] = itemsDbName.length != 0;
			isOkEnable[2] = true;
			isOkEnable[3] = true;
			break;
		case DB_VOL:
			gdCompDataType.heightHint = LINE_HEIGHT * 2;
			compDataType.setLayoutData(gdCompDataType);
			addDbNameItem(compDataType);
			addVolNameItem(compDataType, dbNameValue);
			addDbVolMetricsArea(compMetrics);
			isOkEnable[1] = itemsDbName.length != 0;
			isOkEnable[2] = itemsVolName.length != 0;
			isOkEnable[3] = true;
			break;
		case BROKER:
			gdCompDataType.heightHint = LINE_HEIGHT * 2;
			compDataType.setLayoutData(gdCompDataType);
			addBrokerNameItem(compDataType);
			addBrokerMetricsArea(compMetrics);
			isOkEnable[1] = true;
			isOkEnable[2] = true;
			isOkEnable[3] = itemsBrokerName.length != 0;
			break;
		case OS:
			gdCompDataType.heightHint = LINE_HEIGHT + 5;
			compDataType.setLayoutData(gdCompDataType);
			addOsMetricsArea(compMetrics);
			isOkEnable[1] = true;
			isOkEnable[2] = true;
			isOkEnable[3] = true;
			break;
		default:
		}
		compDataType.layout();
		compMetrics.layout();
		isOkEnable[0] = false;
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		if (isNew) {
			getShell().setText(Messages.addSingleHostStatisticItemTitle);
			this.setTitle(Messages.addSingleHostStatisticItemTitle);
			this.setMessage(Messages.addStatisticItemMsg);
		} else {
			getShell().setText(Messages.editSingleHostStatisticItemTitle);
			this.setTitle(Messages.editSingleHostStatisticItemTitle);
			this.setMessage(Messages.editStatisticItemMsg);
		}

		CommonUITool.centerShell(getShell());
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel,
				false);
		getButton(IDialogConstants.OK_ID).setEnabled(!isNew);
	}

	/**
	 * Enable the "OK" button
	 */
	private void enableOk() {
		String errMsg = null;
		if (!isOkEnable[0]) {
			errMsg = Messages.errNoMetricMsg;
		} else if (!isOkEnable[1]) {
			errMsg = Messages.errNoAvailableDbMsg;
		} else if (!isOkEnable[2]) {
			errMsg = Messages.errNoAvailableVolMsg;
		} else if (!isOkEnable[3]) {
			errMsg = Messages.errNoAvailableBrokerMsg;
		}
		getButton(IDialogConstants.OK_ID).setEnabled(errMsg == null);
		setErrorMessage(errMsg);
	}

	/**
	 * When press "ok" button, call it.
	 */
	public void okPressed() {
		StatisticType type = StatisticType.getEnumByMessage(comboDataType.getText());
		String timeType = getTimeType();
		int series = 0;
		if (!isNew) {
			series = statisticChartItem.getSeries();
		}
		statisticChartItem = new SingleHostChartItem(editor.getNodeId(),
				type);
		statisticChartItem.setDType(timeType);
		switch (type) {
		case DB:
			((SingleHostChartItem) statisticChartItem).setDbName(comboDbName.getText());
			break;
		case DB_VOL:
			((SingleHostChartItem) statisticChartItem).setDbName(comboDbName.getText());
			((SingleHostChartItem) statisticChartItem).setVolName(comboVolName.getText());
			break;
		case BROKER:
			((SingleHostChartItem) statisticChartItem).setBrokerName(comboBrokerName.getText());
			break;
		case OS:
			break;
		default:
		}

		for (Group grp : grpMetricList) {
			if (grp.getChildren() == null) {
				continue;
			}
			for (Control control : grp.getChildren()) {
				if (control instanceof Button) {
					if (((Button) control).getSelection()) {
						((SingleHostChartItem) statisticChartItem).addMetric(MetricType.getEnumByMessage(
								((Button) control).getText()).getMetric());
					}
				}
			}
		}
		if (!isNew) {
			statisticChartItem.setSeries(series);
		}

		super.okPressed();
	}

	private String getTimeType() {
		String text = comboTimeType.getText();
		TimeType timeType = TimeType.getEnumByMessage(text);

		return timeType == null ? null : timeType.getType();
	}

	public StatisticChartItem getStatisticChartItem() {
		return statisticChartItem;
	}

	public void setStatisticChartItem(StatisticChartItem statisticChartItem) {
		this.statisticChartItem = statisticChartItem;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	class ComboDataTypeSelectionAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent event) {
			StatisticType dataType = StatisticType.getEnumByMessage(comboDataType.getText());
			if (oldDataType == dataType) {
				return;
			}
			dataType = dataType == null ? StatisticType.DB : dataType;
			oldDataType = dataType;

			if (lblDbName != null && comboDbName != null) {
				lblDbName.dispose();
				comboDbName.dispose();
			}
			if (lblVolName != null && comboVolName != null) {
				lblVolName.dispose();
				comboVolName.dispose();
			}
			if (lblBrokerName != null && comboBrokerName != null) {
				lblBrokerName.dispose();
				comboBrokerName.dispose();
			}

			for (Control ctrl : compMetrics.getChildren()) {
				ctrl.dispose();
			}
			grpMetricList = new ArrayList<Group>();

			refreshChangeableItems(dataType);
			compDataType.getParent().layout();
		}
	}

	class ComboDbNameSelectionAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent event) {
			int index = comboDataType.getSelectionIndex();
			if (index == 0) {
				return;
			}
			String dbName = comboDbName.getText();
			if (StringUtil.isEmpty(dbName)) {
				return;
			}
			refreshDbVolData(dbName);
			comboVolName.setItems(itemsVolName);
			comboVolName.setText(dbVolNameValue);
		}
	}

	class RadioMetricSelectionAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent event) {
			boolean isSelected = false;
			int idx = -1;
			//check whether a button is selected
			for (int i = 0; i < grpMetricList.size(); i++) {
				if (grpMetricList.get(i) == null
						|| grpMetricList.get(i).getChildren() == null) {
					continue;
				}
				for (Control control : grpMetricList.get(i).getChildren()) {
					if (control instanceof Button) {
						if (((Button) control).getSelection()) {
							isSelected = true;
							idx = i;
							break;
						}
					}
				}
				if (isSelected) {
					break;
				}
			}
			if (isSelected) {
				//enable all the buttons in same group
				for (int i = 0; i < grpMetricList.size(); i++) {
					if (grpMetricList.get(i) == null
							|| grpMetricList.get(i).getChildren() == null) {
						continue;
					}
					if (idx != i) {
						for (Control control : grpMetricList.get(i).getChildren()) {
							if (control instanceof Button) {
								control.setEnabled(false);
							}
						}
					}
				}
			} else {
				//enable all the buttons
				for (Group grp : grpMetricList) {
					if (grp.getChildren() == null) {
						continue;
					}
					for (Control control : grp.getChildren()) {
						if (control instanceof Button) {
							control.setEnabled(true);
						}
					}
				}
			}

			isOkEnable[0] = isSelected;
			enableOk();
		}
	}

	class ButtonCleanAllSelectionAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent event) {
			for (Group grp : grpMetricList) {
				if (grp.getChildren() == null) {
					continue;
				}
				for (Control control : grp.getChildren()) {
					if (control instanceof Button) {
						((Button) control).setSelection(false);
						control.setEnabled(true);
					}
				}
			}
			isOkEnable[0] = false;
			enableOk();
		}
	}

}
