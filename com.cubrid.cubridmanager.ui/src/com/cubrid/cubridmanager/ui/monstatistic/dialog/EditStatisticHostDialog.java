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

import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.ArrayUtil;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
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
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartHost;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.TimeType;
import com.cubrid.cubridmanager.ui.host.dialog.ConnectHostExecutor;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.monstatistic.progress.LoadMonitorStatisticDataProgress;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * This type provides a dialog for user to configuration a monitor statistic
 * item in monitor statistic page
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-08-31 created by Santiago Wang
 */
public class EditStatisticHostDialog extends
		CMTitleAreaDialog {

	private final CMTitleAreaDialog parentDialog;
	private final CMHostNodePersistManager hostNodePersistManager = CMHostNodePersistManager.getInstance();
	private ServerInfo serverInfo = null;
	private StatisticChartHost hostItem;
	private StatisticChartHost oldHostItem;

	private final String[] ITEMS_EMPTY = ArrayUtil.getEmptyStringArray();
	private final String VALUE_DEFAULT = "";

	public final String HOST_STATUS_UNAVAILABLE = Messages.msgHostUnvailable;
	public final String HOST_STATUS_UNSUPPORTED = Messages.msgHostUnsupported;
	public final String HOST_STATUS_OK = Messages.msgHostOk;
	private final String[] itemsHost;
	private final String[] itemsDataType;
	private final String[] itemsTimeType;
	private String[] itemsDbName;
	private String[] itemsBrokerName;
	private String[] itemsVolName;
	private String[] itemsMetric;

	private Composite compRoot;

	private Combo comboHost;
	private Text comboHostStatus;
	private Combo comboDataType;
	private Combo comboTimeType;
	private Combo comboDbName;
	private Combo comboBrokerName;
	private Combo comboVolName;
	private Combo comboMetric;

	private StatisticType dataType;
	private String hostValue;
	private String hostStatusValue;
	private String dataTypeValue;
	private String timeValue;
	private String dbNameValue;
	private String dbVolNameValue;
	private String brokerNameValue;
	private String metricValue;

	/*
	 * isOkEnable[0]: whether host support monitor statistic
	 * isOkEnable[1]: whether has available active DB
	 * isOkEnable[2]: whether has available volume name for specify DB
	 * isOkEnable[3]: whether has available broker
	 * isOkEnable[4]: whether duplicate with host info in table viewer
	 * isOkEnable[5]: whether host need to enable monitor statistic
	 */
	private final boolean[] isOkEnable;

	private int oldHostIndex;
	private int oldDataTypeIndex;
	private final boolean isNewHost;
	//If not first, then Data Type is uneditable
	private boolean isFirstHost;
	private String firstMetric;
	private TimeType firstTime;

	/**
	 * Constructor
	 *
	 * @param parentShell
	 */
	public EditStatisticHostDialog(Shell parentShell,
			CMTitleAreaDialog parentDialog, boolean isNewHost) {
		super(parentShell);
		this.parentDialog = parentDialog;
		this.isNewHost = isNewHost;

		List<CubridServer> cubridServerList = hostNodePersistManager.getAllServers();
		itemsHost = new String[cubridServerList.size() + 1];
		itemsHost[0] = VALUE_DEFAULT;
		for (int i = 0; i < cubridServerList.size(); i++) {
			itemsHost[i + 1] = cubridServerList.get(i).getId();
		}
		itemsDataType = new String[]{StatisticType.DB.getMessage(),
				StatisticType.DB_VOL.getMessage(),
				StatisticType.BROKER.getMessage(),
				StatisticType.OS.getMessage() };
		itemsTimeType = new String[]{TimeType.DAILY.getMessage(),
				TimeType.WEEKLY.getMessage(), TimeType.MONTHLY.getMessage(),
				TimeType.YEARLY.getMessage() };
		itemsDbName = ITEMS_EMPTY;
		itemsBrokerName = ITEMS_EMPTY;

		//initial the default value
		hostValue = VALUE_DEFAULT;
		hostStatusValue = VALUE_DEFAULT;
		dataTypeValue = itemsDataType[0];
		timeValue = itemsTimeType[0];
		dbNameValue = VALUE_DEFAULT;
		dbVolNameValue = VALUE_DEFAULT;
		brokerNameValue = VALUE_DEFAULT;
		metricValue = VALUE_DEFAULT;

		oldDataTypeIndex = 0;

		isOkEnable = new boolean[6];
		for (int i = 0; i < isOkEnable.length; i++) {
			isOkEnable[i] = true;
		}
	}

	public void init(StatisticType dataType, TimeType timeType,
			StatisticChartHost hostInfo) {
		if (hostInfo == null) {
			return;
		}

		this.oldHostItem = hostInfo;
		this.hostValue = hostInfo.getCubridServerId();
		this.dataType = dataType;

		this.timeValue = timeType == null ? VALUE_DEFAULT : timeType.getMessage();
		MetricType metricType = MetricType.getEnumByMetric(hostInfo.getMetric());
		this.metricValue = metricType != null ? metricType.getMessage()
				: VALUE_DEFAULT;
		switch (dataType) {
		case DB:
			dbNameValue = hostInfo.getDbName();
			break;
		case DB_VOL:
			dbNameValue = hostInfo.getDbName();
			dbVolNameValue = hostInfo.getVolName();
			break;
		case BROKER:
			brokerNameValue = hostInfo.getBrokerName();
		case OS:
			break;
		default:
			break;
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

		compRoot = new Composite(parentComp, SWT.RESIZE);
		GridLayout layoutCompDataType = new GridLayout(4, false);
		compRoot.setLayout(layoutCompDataType);
		compRoot.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label lblHost = new Label(compRoot, SWT.NONE);
		lblHost.setText(Messages.lblHost);

		comboHost = new Combo(compRoot, SWT.NONE | SWT.READ_ONLY);
		GridData gdComboHost = new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1);
		gdComboHost.widthHint = 168;
		comboHost.setLayoutData(gdComboHost);
		comboHost.setItems(itemsHost);
		comboHost.setText(hostValue);
		//initial latest comboHost selection index
		for (int i = 0; i < itemsHost.length; i++) {
			if (itemsHost[i].equals(hostValue)) {
				oldHostIndex = i;
				break;
			}
			oldHostIndex = -1;
		}
		comboHost.addSelectionListener(new ComboHostSelectionAdapter());

		final Label lblHostStatus = new Label(compRoot, SWT.NONE);
		lblHostStatus.setText(Messages.lblStatus);

		comboHostStatus = new Text(compRoot, SWT.BORDER);
		GridData gdComboHostStatus = new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1);
		gdComboHostStatus.widthHint = 168;
		comboHostStatus.setLayoutData(gdComboHostStatus);
		comboHostStatus.setText(hostStatusValue);
		comboHostStatus.setEditable(false);

		final Label lblDataType = new Label(compRoot, SWT.NONE);
		lblDataType.setText(Messages.lblDataType);

		comboDataType = new Combo(compRoot, SWT.NONE | SWT.READ_ONLY);
		GridData gdComboDataType = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gdComboDataType.widthHint = 168;
		comboDataType.setLayoutData(gdComboDataType);
		comboDataType.setItems(itemsDataType);
		//init data type value
		if (dataType == null && !isFirstHost && !StringUtil.isEmpty(firstMetric)) {
			dataType = StatisticParamUtil.getTypeByMetric(firstMetric);
		}
		dataType = dataType == null ? StatisticType.DB : dataType;
		dataTypeValue = dataType.getMessage();
		comboDataType.setText(dataTypeValue);
		//initial latest comboDataType selection index
		for (int i = 0; i < itemsHost.length; i++) {
			if (itemsDataType[i].equals(dataTypeValue)) {
				oldDataTypeIndex = i;
				break;
			}
		}
		comboDataType.addSelectionListener(new ComboDataTypeSelectionAdapter());

		final Label lblTimeType = new Label(compRoot, SWT.NONE);
		lblTimeType.setText(Messages.lblTimeType);

		comboTimeType = new Combo(compRoot, SWT.NONE | SWT.READ_ONLY);
		GridData gdComboTimeType = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gdComboTimeType.widthHint = 168;
		comboTimeType.setLayoutData(gdComboTimeType);
		comboTimeType.setItems(itemsTimeType);
		if (!isFirstHost && firstTime != null) {
			TimeType timeType = firstTime;
			timeValue = timeType.getMessage();
		}
		comboTimeType.setText(timeValue);

		refreshChangeableItems(dataType, false);
		if (!isNewHost && serverInfo != null) {
			refreshDbAndBrokerData(serverInfo.isSupportMonitorStatistic(), true);
			boolean isSupportMonitorStatistic = serverInfo.isSupportMonitorStatistic();
			boolean isSupportByVersion = CompatibleUtil.isSupportMonitorStatistic(serverInfo);
			isOkEnable[0] = isSupportMonitorStatistic || isSupportByVersion;
			isOkEnable[5] = isSupportMonitorStatistic && isSupportByVersion;
		}
		refreshMetricData(dataType, isNewHost);

		comboDataType.setEnabled(isFirstHost);
		comboTimeType.setEnabled(isFirstHost);
		return parentComp;
	}

	/**
	 * Add or change items that can be changed. Such as DB name combo, DB volume
	 * name combo, broker name combo and metric combo.
	 *
	 * @param type
	 * @param isRefreshDbVol
	 */
	private void refreshChangeableItems(final StatisticType type,
			boolean isRefreshDbVol) {
		isOkEnable[1] = true;
		isOkEnable[2] = true;
		isOkEnable[3] = true;
		switch (type) {
		case DB:
			addDbNameItem(compRoot);
			break;
		case DB_VOL:
			addDbNameItem(compRoot);
			addVolNameItem(compRoot, dbNameValue, isRefreshDbVol);
			break;
		case BROKER:
			addBrokerNameItem(compRoot);
			break;
		case OS:
			break;
		default:
		}
		addMetricItem(compRoot);
	}

	private void addDbNameItem(final Composite parent) {
		final Label lblDbName = new Label(parent, SWT.NONE);
		lblDbName.setText(Messages.lblDbName);

		comboDbName = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
		comboDbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		comboDbName.setItems(itemsDbName);
		comboDbName.setText(dbNameValue);
		comboDbName.addSelectionListener(new ComboDbNameSelectionAdapter());
		isOkEnable[1] = !VALUE_DEFAULT.equals(dbNameValue);
	}

	private void addVolNameItem(final Composite parent, String dbName,
			boolean isRefresh) {
		final Label lblVolName = new Label(parent, SWT.NONE);
		lblVolName.setText(Messages.lblVolName);

		comboVolName = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
		comboVolName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		if (isRefresh) {
			refreshDbVolData(dbName, false);
			comboVolName.setItems(itemsVolName);
			comboVolName.setText(dbVolNameValue);
			isOkEnable[2] = !VALUE_DEFAULT.equals(itemsVolName);
		}
	}

	private void addBrokerNameItem(final Composite parent) {
		final Label lblBrokerName = new Label(parent, SWT.NONE);
		lblBrokerName.setText(Messages.lblBrokerName);

		comboBrokerName = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
		comboBrokerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		comboBrokerName.setItems(itemsBrokerName);
		comboBrokerName.setText(brokerNameValue);
		isOkEnable[3] = !VALUE_DEFAULT.equals(brokerNameValue);
	}

	private void addMetricItem(final Composite parent) {
		final Label lblMetricName = new Label(parent, SWT.NONE);
		lblMetricName.setText(Messages.lblMetric);

		comboMetric = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
		comboMetric.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		//		comboMetric.setItems(itemsMetric);
		//		comboMetric.setText(metricValue);
	}

	/**
	 * Refresh database and broker data for the selected host.
	 *
	 * @param isSupported whether selected CUBRID host support monitor statistic
	 *        feature
	 */
	private void refreshDbAndBrokerData(boolean isSupported, boolean isInitial) {
		boolean isDbDataValid = false;
		boolean isBrokerDataValid = false;
		boolean isNeedInitDbNameValue = isNewHost || !isInitial
				|| dbNameValue == null;
		boolean isNeedInitBrokerNameValue = isNewHost || !isInitial
				|| brokerNameValue == null;
		boolean isDbContained = false;
		if(isSupported){
			List<String> dbNameList = serverInfo.getAllDatabaseList();
			if (dbNameList != null && dbNameList.size() > 0) {
				GetDatabaseListTask getDatabaseListTask = new GetDatabaseListTask(
						serverInfo);
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
										//check whether the DB name is in the DB name list when edit host
										if(isInitial && !isNewHost && dbName.equals(dbNameValue)){
											isDbContained = true;
										}
										break;
									}
								}
							}
						}
						if (isNeedInitDbNameValue) {
							dbNameValue = itemsDbName[0];
						}
					}
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
					if (isNeedInitBrokerNameValue) {
						brokerNameValue = itemsBrokerName[0];
					}
					isBrokerDataValid = true;
				}
			}
		}
		if (!isDbDataValid) {
			itemsDbName = ITEMS_EMPTY;
			if (isNeedInitDbNameValue) {
				dbNameValue = VALUE_DEFAULT;
			}
		}
		if (!isBrokerDataValid) {
			itemsBrokerName = ITEMS_EMPTY;
			if (isNeedInitBrokerNameValue) {
				brokerNameValue = VALUE_DEFAULT;
			}
		}

		StatisticType type = StatisticType.getEnumByMessage(comboDataType.getText());
		type = type == null ? StatisticType.DB : type;
		switch (type) {
		case DB:
			comboDbName.setItems(itemsDbName);
			comboDbName.setText(dbNameValue);
			isOkEnable[1] = (isInitial && !isNewHost) ? (isDbDataValid && isDbContained)
					: isDbDataValid;
			isOkEnable[2] = true;
			isOkEnable[3] = true;
			break;
		case DB_VOL:
			comboDbName.setItems(itemsDbName);
			comboDbName.setText(dbNameValue);
			isOkEnable[1] = isDbDataValid;
			if (isDbDataValid) {
				refreshDbVolData(dbNameValue, isInitial);
				isOkEnable[2] = !VALUE_DEFAULT.equals(dbVolNameValue);
			} else {
				itemsVolName = ITEMS_EMPTY;
				if (isNewHost || !isInitial || dbVolNameValue == null) {
					dbVolNameValue = VALUE_DEFAULT;
				}
				isOkEnable[2] = false;
			}
			comboVolName.setItems(itemsVolName);
			comboVolName.setText(dbVolNameValue);
			isOkEnable[3] = true;
			break;
		case BROKER:
			comboBrokerName.setItems(itemsBrokerName);
			comboBrokerName.setText(brokerNameValue);
			isOkEnable[1] = true;
			isOkEnable[2] = true;
			isOkEnable[3] = isBrokerDataValid;
			break;
		case OS:
			isOkEnable[1] = true;
			isOkEnable[2] = true;
			isOkEnable[3] = true;
			break;
		default:
		}
	}

	private void refreshDbVolData(String dbName, boolean isInitial) {
		boolean isEmptyData = false;
		boolean isNeedInitVolNameValue = isNewHost || !isInitial
				|| dbVolNameValue == null;
		if (StringUtil.isEmpty(dbName)) {
			isEmptyData = true;
		} else {
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
					if (isNeedInitVolNameValue) {
						dbVolNameValue = itemsVolName[0];
					}
				} else {
					isEmptyData = true;
				}
			} else {
				isEmptyData = true;
			}
		}

		if (isEmptyData) {
			itemsVolName = ITEMS_EMPTY;
			if (isNeedInitVolNameValue) {
				dbVolNameValue = VALUE_DEFAULT;
			}
		}
	}

	private void refreshMetricData(StatisticType type,
			boolean isRefreshMetricValue) {
		if (type == null) {
			itemsMetric = ITEMS_EMPTY;
			if (isRefreshMetricValue) {
				metricValue = VALUE_DEFAULT;
			}
		} else {
			Set<MetricType> metricTypeSet = null;
			if (isFirstHost) {
				metricTypeSet = StatisticParamUtil.getSupportedMetricTypes(type);
			} else {
				metricTypeSet = StatisticParamUtil.getCompatibleMetricsForDisplay(firstMetric);
			}

			if (metricTypeSet != null)
				itemsMetric = new String[metricTypeSet.size()];
			int index = 0;
			for (MetricType metricType : metricTypeSet) {
				itemsMetric[index] = metricType.getMessage();
				index++;
			}
			if (isRefreshMetricValue) {
				metricValue = itemsMetric[0];
			}
		}
		comboMetric.setItems(itemsMetric);
		if (isRefreshMetricValue || metricValue != null) {
			comboMetric.setText(metricValue);
		}
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		if (isNewHost) {
			getShell().setText(Messages.addStatisticHostTitle);
			this.setTitle(Messages.addStatisticHostTitle);
			this.setMessage(Messages.addNewHostItemMsg);
		} else {
			getShell().setText(Messages.editStatisticHostTitle);
			this.setTitle(Messages.editStatisticHostTitle);
			this.setMessage(Messages.editHostItemMsg);
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
		boolean isEnable = !isNewHost;
		for (boolean element : isOkEnable) {
			isEnable = isEnable && element;
		}
		getButton(IDialogConstants.OK_ID).setEnabled(isEnable);
	}

	/**
	 * Enable the "OK" button
	 */
	private void enableOk() {
		String errMsg = null;
		if (!isOkEnable[0]) {
			errMsg = Messages.errNoAvailableHost;
		} else if (!isOkEnable[5]) {
			errMsg = Messages.errNeedEnableMonStatistic;
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
		if (isFirstHost) {
			((EditMultiHostStatisticItemDialog) parentDialog).setType(type);
			((EditMultiHostStatisticItemDialog) parentDialog).setTimeType(getTimeType());
		}

		String cubridServerId = comboHost.getText();
		hostItem = new StatisticChartHost(cubridServerId);
		ServerInfo serverInfo = hostNodePersistManager.getServer(cubridServerId).getServerInfo();
		hostItem.setServerInfo(serverInfo);
		hostItem.setIp(serverInfo.getHostAddress());
		hostItem.setPort(serverInfo.getHostMonPort());
		hostItem.setUser(serverInfo.getUserName());
		hostItem.setPassword(serverInfo.getUserPassword());

		switch (type) {
		case DB:
			hostItem.setDbName(comboDbName.getText());
			break;
		case DB_VOL:
			hostItem.setDbName(comboDbName.getText());
			hostItem.setVolName(comboVolName.getText());
			break;
		case BROKER:
			hostItem.setBrokerName(comboBrokerName.getText());
			break;
		case OS:
			break;
		default:
		}
		hostItem.setMetric(MetricType.getEnumByMessage(comboMetric.getText()).getMetric());
		boolean isDuplicated = ((EditMultiHostStatisticItemDialog) parentDialog).isDuplicatedHostInfo(
				hostItem, oldHostItem);
		if (isDuplicated) {
			CommonUITool.openWarningBox(Messages.errDuplicateHost);
			return;
		}

		super.okPressed();
	}

	private TimeType getTimeType() {
		String text = comboTimeType.getText();
		TimeType timeType = TimeType.getEnumByMessage(text);

		return timeType;
	}

	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	public StatisticChartHost getHostItem() {
		return hostItem;
	}

	public void setHostItem(StatisticChartHost hostItem) {
		this.hostItem = hostItem;
	}

	public void setHostValue(String valueHost) {
		this.hostValue = valueHost;
	}

	public String getHostStatusValue() {
		return hostStatusValue;
	}

	public void setHostStatusValue(String hostStatusValue) {
		this.hostStatusValue = hostStatusValue;
		isOkEnable[0] = HOST_STATUS_OK.equals(hostStatusValue);
	}

	public void setDataValue(String valueData) {
		this.dataTypeValue = valueData;
	}

	public void setTimeValue(String valueTime) {
		this.timeValue = valueTime;
	}

	public void setDbNameValue(String valueDbName) {
		this.dbNameValue = valueDbName;
	}

	public void setDbVolNameValue(String valueDbVolName) {
		this.dbVolNameValue = valueDbVolName;
	}

	public void setBrokerNameValue(String valueBrokerName) {
		this.brokerNameValue = valueBrokerName;
	}

	public void setMetricValue(String valueMetric) {
		this.metricValue = valueMetric;
	}

	public boolean isFirstHost() {
		return isFirstHost;
	}

	public void setFirstHost(boolean isFirstHost) {
		this.isFirstHost = isFirstHost;
	}

	public String getFirstMetric() {
		return firstMetric;
	}

	public void setFirstMetric(String firstMetric) {
		this.firstMetric = firstMetric;
	}

	public TimeType getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(TimeType firstTime) {
		this.firstTime = firstTime;
	}

	class ComboHostSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(oldHostIndex == comboHost.getSelectionIndex()){
				return;
			}
			oldHostIndex = comboHost.getSelectionIndex();
			String hostId = comboHost.getText();
			boolean isUnavailable = false;
			boolean isSupported = false;
			if (StringUtil.isEmpty(comboHost.getText())) {
				isOkEnable[0] = false;
				isOkEnable[5] = false;
			} else {
				CubridServer server = hostNodePersistManager.getServer(hostId);
				if (server != null && server.getServerInfo() != null) {
					serverInfo = server.getServerInfo();
					if (serverInfo.isConnected()) {
						isSupported = serverInfo.isSupportMonitorStatistic();
					} else {
						LoadMonitorStatisticDataProgress.addDisconnectedServer(serverInfo);
						TaskExecutor taskExcutor = new ConnectHostExecutor(
								getShell(), server.getServerInfo(), true);
						((ConnectHostExecutor) taskExcutor).setCheckJdbc(false);
						new ExecTaskWithProgress(taskExcutor).exec(true, true);

						if (taskExcutor.isSuccess()) {
							isSupported = serverInfo.isSupportMonitorStatistic();
						} else {
							isUnavailable = true;
						}
					}
				} else {
					isUnavailable = true;
				}
				if (isUnavailable) {
					isOkEnable[0] = false;
					isOkEnable[5] = false;
					comboHostStatus.setText(HOST_STATUS_UNAVAILABLE);
				} else if (isSupported) {
					isOkEnable[0] = true;
					isOkEnable[5] = true;
					comboHostStatus.setText(HOST_STATUS_OK);
				} else {
					boolean isSupportByVersion = CompatibleUtil.isSupportMonitorStatistic(serverInfo);
					isOkEnable[0] = false || isSupportByVersion;
					isOkEnable[5] = false;
					comboHostStatus.setText(HOST_STATUS_UNSUPPORTED);
				}
			}
			refreshDbAndBrokerData(isSupported, false);
			enableOk();
		}
	}

	class ComboDataTypeSelectionAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent event) {
			int index = comboDataType.getSelectionIndex();
			if (index == oldDataTypeIndex) {
				return;
			}
			oldDataTypeIndex = index;

			Control[] children = compRoot.getChildren();
			if(children == null){
				return;
			}

			for (int i = children.length - 1; i > 7; i--) {
				children[i].dispose();
			}

			StatisticType type = StatisticType.getEnumByMessage(comboDataType.getText());
			type = type == null ? StatisticType.DB : type;
			//reset DB name and broker name value after change data type
			if (itemsDbName.length != 0) {
				dbNameValue = itemsDbName[0];
			}
			if (itemsBrokerName.length != 0) {
				brokerNameValue = itemsBrokerName[0];
			}
			refreshChangeableItems(type, true);
			refreshMetricData(type, true);
			compRoot.layout();
			enableOk();
		}
	}

	class ComboDbNameSelectionAdapter extends
			SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (StatisticType.DB_VOL.getMessage().equals(
					comboDataType.getText())) {
				refreshDbVolData(comboDbName.getText(), false);
				comboVolName.setItems(itemsVolName);
				comboVolName.setText(dbVolNameValue);
			}
			//when edit table, if old DB is not activate,
			//after user select, should has correct status
			if (itemsDbName.length > 0
					&& !StringUtil.isEmpty(comboDbName.getText())) {
				isOkEnable[1] = true;
				enableOk();
			}
		}
	}

}
