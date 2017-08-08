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
package com.cubrid.cubridmanager.ui.common.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.ui.common.Messages;

/**
 * CUBRID Database property page for cubrid.conf configuration file
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-4 created by pangqiren
 */
public class DatabaseConfigPropertyPage extends PreferencePage implements ModifyListener {
	private final ICubridNode node;
	private boolean isAdmin = false;
	private TableViewer advancedOptionTableViewer;
	private Table advancedOptionTable;
	private String[] columnNameArrs;
	private Text dataBufferPageText;
	private Text sortBufferPageText;
	private Text logBufferPageText;
	private Text lockEscalationText;
	private Text lockTimeOutText;
	private Text deadLockDetectIntervalText;
	private Text checkPointIntervalText;
	private Combo isolationLevelCombo;
	private Text maxClientsText;
	private Button autoRestartServerButton;
	private Button replicationButton;
	private Button jspButton;
	private Text cubridPortIdText;
	private boolean isChanged = false;
	private boolean isApply = false;
	private final boolean isCommonPara;
	private final Map<String, Map<String, String>> initialValueMap = new HashMap<String, Map<String, String>>();
	private final Map<String, String> dbBaseParameterValueMap = new HashMap<String, String>();
	private boolean isCanSetOnForHaMode;
	private Text dataBufferSizeText;
	private Text sortBufferSizeText;
	private Text logBufferSizeText;
	private PageSizeChoiceComposite dataBufferGroup;
	private PageSizeChoiceComposite sortBufferGroup;
	private PageSizeChoiceComposite logBufferGroup;
	private boolean isSupportSizesProp;

	public DatabaseConfigPropertyPage(ICubridNode node, String name, boolean isCommonPara) {
		super(name, null);
		noDefaultAndApplyButton();
		this.node = node;
		this.isCommonPara = isCommonPara;
		if (this.node != null && this.node.getServer().getServerInfo().getLoginedUserInfo().isAdmin()) {
			isAdmin = true;
		}
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		layout = new GridLayout();
		tabFolder.setLayout(layout);

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(Messages.tabItemGeneral);
		item.setControl(createGeneralComp(tabFolder));

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText(Messages.tabItemAdvanceOptions);
		item.setControl(createAdvancedComp(tabFolder));

		initial();
		return composite;
	}

	/**
	 * Create general tabItem composite
	 *
	 * @param parent the parent composite
	 * @return the composite
	 */
	private Composite createGeneralComp(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());

		isSupportSizesProp = CompatibleUtil.isSupportSizesPropInServer(node.getServer().getServerInfo());
		if (isSupportSizesProp) {
			createPageSizeGeneralComp(composite);
		} else {
			createOnlyPageGeneralComp(composite);
		}

		return composite;
	}

	/**
	 * Create general info including all
	 *
	 * @param composite the composite
	 * @return composite
	 */
	private Composite createPageSizeGeneralComp(Composite composite) {
		Group generalGroup = new Group(composite, SWT.NONE);
		generalGroup.setText(Messages.grpGeneralPara);
		generalGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		generalGroup.setLayout(layout);

		dataBufferGroup = new PageSizeChoiceComposite();
		dataBufferGroup.setGroupTxt(Messages.grpDataBuffer);
		dataBufferGroup.setPageBtnTxt(ConfConstants.DATA_BUFFER_PAGES + ":");
		dataBufferGroup.setSizeBtnTxt(ConfConstants.DATA_BUFFER_SIZE + ":");
		dataBufferGroup.createContent(generalGroup, isAdmin);
		dataBufferPageText = dataBufferGroup.getPageText();
		dataBufferSizeText = dataBufferGroup.getSizeText();

		sortBufferGroup = new PageSizeChoiceComposite();
		sortBufferGroup.setGroupTxt(Messages.grpSortBuffer);
		sortBufferGroup.setPageBtnTxt(ConfConstants.SORT_BUFFER_PAGES + ":");
		sortBufferGroup.setSizeBtnTxt(ConfConstants.SORT_BUFFER_SIZE + ":");
		sortBufferGroup.createContent(generalGroup, isAdmin);
		sortBufferPageText = sortBufferGroup.getPageText();
		sortBufferSizeText = sortBufferGroup.getSizeText();

		logBufferGroup = new PageSizeChoiceComposite();
		logBufferGroup.setGroupTxt(Messages.grpLogBuffer);
		logBufferGroup.setPageBtnTxt(ConfConstants.LOG_BUFFER_PAGES + ":");
		logBufferGroup.setSizeBtnTxt(ConfConstants.LOG_BUFFER_SIZE + ":");
		logBufferGroup.createContent(generalGroup, isAdmin);
		logBufferPageText = logBufferGroup.getPageText();
		logBufferSizeText = logBufferGroup.getSizeText();

		Group othersGroup = new Group(generalGroup, SWT.NONE);
		othersGroup.setText(Messages.grpOthersProperties);
		GridData othersGd = new GridData(GridData.FILL_HORIZONTAL);
		othersGroup.setLayoutData(othersGd);
		GridLayout othersLayout = new GridLayout();
		othersLayout.numColumns = 2;
		othersGroup.setLayout(othersLayout);

		createGeneralCommonPart(othersGroup);

		return composite;
	}

	/**
	 * Create general info but the size info not to be covered
	 *
	 * @param composite the Composite
	 * @return Composite
	 */
	private Composite createOnlyPageGeneralComp(Composite composite) {
		Group generalGroup = new Group(composite, SWT.NONE);
		generalGroup.setText(Messages.grpGeneralPara);
		generalGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		generalGroup.setLayout(layout);

		Label dataBufferPageLabel = new Label(generalGroup, SWT.LEFT);
		dataBufferPageLabel.setText(ConfConstants.DATA_BUFFER_PAGES + ":");
		dataBufferPageLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		dataBufferPageText = new Text(generalGroup, SWT.LEFT | SWT.BORDER);
		dataBufferPageText.setTextLimit(8);
		dataBufferPageText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		dataBufferPageText.setEditable(isAdmin);

		Label sortBufferPageLabel = new Label(generalGroup, SWT.LEFT);
		sortBufferPageLabel.setText(ConfConstants.SORT_BUFFER_PAGES + ":");
		sortBufferPageLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		sortBufferPageText = new Text(generalGroup, SWT.LEFT | SWT.BORDER);
		sortBufferPageText.setTextLimit(8);
		sortBufferPageText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		sortBufferPageText.setEditable(isAdmin);

		Label logBufferPageLabel = new Label(generalGroup, SWT.LEFT);
		logBufferPageLabel.setText(ConfConstants.LOG_BUFFER_PAGES + ":");
		logBufferPageLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		logBufferPageText = new Text(generalGroup, SWT.LEFT | SWT.BORDER);
		logBufferPageText.setTextLimit(8);
		logBufferPageText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		logBufferPageText.setEditable(isAdmin);

		createGeneralCommonPart(generalGroup);
		return composite;
	}

	/**
	 * Create the common part of the General
	 *
	 * @param parent the Composite
	 * @return Composite
	 */
	private Composite createGeneralCommonPart(Composite parent) {
		Label lockEscalationLabel = new Label(parent, SWT.LEFT);
		lockEscalationLabel.setText(ConfConstants.LOCK_ESCALATION + ":");
		lockEscalationLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		lockEscalationText = new Text(parent, SWT.LEFT | SWT.BORDER);
		lockEscalationText.setTextLimit(8);
		lockEscalationText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		lockEscalationText.setEditable(isAdmin);

		Label lockTimeOutLabel = new Label(parent, SWT.LEFT);
		lockTimeOutLabel.setText(ConfConstants.LOCK_TIMEOUT_IN_SECS + ":");
		lockTimeOutLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		lockTimeOutText = new Text(parent, SWT.LEFT | SWT.BORDER);
		lockTimeOutText.setTextLimit(8);
		lockTimeOutText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		lockTimeOutText.setEditable(isAdmin);

		Label deadLockDetectIntervalLabel = new Label(parent, SWT.LEFT);
		deadLockDetectIntervalLabel.setText(ConfConstants.DEADLOCK_DETECTION_INTERVAL_IN_SECS + ":");
		deadLockDetectIntervalLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		deadLockDetectIntervalText = new Text(parent, SWT.LEFT | SWT.BORDER);
		deadLockDetectIntervalText.setTextLimit(8);
		deadLockDetectIntervalText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		deadLockDetectIntervalText.setEditable(isAdmin);

		Label checkPointIntervalLabel = new Label(parent, SWT.LEFT);
		checkPointIntervalLabel.setText(ConfConstants.CHECKPOINT_INTERVAL_IN_MINS + ":");
		checkPointIntervalLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		checkPointIntervalText = new Text(parent, SWT.LEFT | SWT.BORDER);
		checkPointIntervalText.setTextLimit(8);
		checkPointIntervalText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		checkPointIntervalText.setEditable(isAdmin);

		Label isolationLevelLabel = new Label(parent, SWT.LEFT);
		isolationLevelLabel.setText(ConfConstants.ISOLATION_LEVEL + ":");
		isolationLevelLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		isolationLevelCombo = new Combo(parent, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
		isolationLevelCombo.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		isolationLevelCombo.setEnabled(isAdmin);

		Label cubridPortIdLabel = new Label(parent, SWT.LEFT);
		cubridPortIdLabel.setText(ConfConstants.CUBRID_PORT_ID + ":");
		cubridPortIdLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		cubridPortIdText = new Text(parent, SWT.LEFT | SWT.BORDER);
		cubridPortIdText.setTextLimit(8);
		cubridPortIdText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		cubridPortIdText.setEditable(isAdmin);

		Label maxClientsLabel = new Label(parent, SWT.LEFT);
		maxClientsLabel.setText(ConfConstants.MAX_CLIENTS + ":");
		maxClientsLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		maxClientsText = new Text(parent, SWT.LEFT | SWT.BORDER);
		maxClientsText.setTextLimit(8);
		maxClientsText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
		maxClientsText.setEditable(isAdmin);

		autoRestartServerButton = new Button(parent, SWT.LEFT | SWT.CHECK);
		autoRestartServerButton.setText(ConfConstants.AUTO_RESTART_SERVER);
		autoRestartServerButton.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		autoRestartServerButton.setEnabled(isAdmin);

		replicationButton = new Button(parent, SWT.LEFT | SWT.CHECK);
		replicationButton.setText(ConfConstants.REPLICATION);
		replicationButton.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		replicationButton.setEnabled(isAdmin);

		if (CompatibleUtil.isSupportHA(node.getServer().getServerInfo())) {
			replicationButton.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, 0, 0));
			replicationButton.setEnabled(false);
			replicationButton.setVisible(false);
		}

		jspButton = new Button(parent, SWT.LEFT | SWT.CHECK);
		jspButton.setText(ConfConstants.JAVA_STORED_PROCEDURE);
		jspButton.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		jspButton.setEnabled(isAdmin);
		return parent;
	}

	/**
	 * Create the advanced tabItem composite
	 *
	 * @param parent the parent composite
	 * @return the composite
	 */
	private Composite createAdvancedComp(Composite parent) {
		columnNameArrs = new String[] {Messages.tblColumnParameterName,
				Messages.tblColumnParameterType, Messages.tblColumnValueType,
				Messages.tblColumnParameterValue };
		advancedOptionTableViewer = CommonUITool.createCommonTableViewer(parent, null, columnNameArrs,
				CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 100));
		advancedOptionTable = advancedOptionTableViewer.getTable();
		if (isAdmin) {
			linkEditorForTable();
		}

		return advancedOptionTable;
	}

	/**
	 * Link editor for table
	 */
	private void linkEditorForTable() {
		advancedOptionTableViewer.setColumnProperties(columnNameArrs);
		CellEditor[] editors = new CellEditor[4];
		editors[0] = null;
		editors[1] = null;
		editors[2] = null;
		editors[3] = new TextCellEditor(advancedOptionTable);
		advancedOptionTableViewer.setCellEditors(editors);
		advancedOptionTableViewer.setCellModifier(new ICellModifier() {
			@SuppressWarnings("unchecked")
			public boolean canModify(Object element, String property) {
				Object obj = null;
				if (element instanceof Item) {
					obj = ((Item) element).getData();
				} else if (element instanceof Map) {
					obj = element;
				}
				if (obj == null) {
					return false;
				}
				Map<String, String> map = (Map<String, String>) obj;
				String parameter = map.get("0");
				if (parameter.equals(ConfConstants.HA_MODE) && !isCanSetOnForHaMode) {
					return false;
				}
				if (property.equals(columnNameArrs[3])) {
					return true;
				}
				return false;
			}

			@SuppressWarnings("unchecked")
			public Object getValue(Object element, String property) {
				Map<String, String> map = (Map<String, String>) element;
				if (property.equals(columnNameArrs[3])) {
					return map.get("3");
				}
				return null;
			}

			@SuppressWarnings("unchecked")
			public void modify(Object element, String property, Object value) {
				Object obj = null;
				if (element instanceof Item) {
					obj = ((Item) element).getData();
				}
				if (obj == null) {
					return;
				}
				Map<String, String> map = (Map<String, String>) obj;
				modifyTable(map, property, value);
			}
		});
	}

	/**
	 * Modify parameter value in table
	 *
	 * @param map the initial value map
	 * @param property the property name
	 * @param value the modified value
	 */
	private void modifyTable(Map<String, String> map, String property, Object value) {
		String parameter = map.get("0");
		String type = map.get("2");
		String paraValue = map.get("3");
		boolean isValid = true;
		String errorMsg = null;
		if (type.startsWith("bool")) {
			if (!value.toString().equalsIgnoreCase("yes") && !value.toString().equalsIgnoreCase("no")) {
				isValid = false;
				errorMsg = Messages.bind(Messages.errYesNoParameter, new String[] { parameter });
			}
		} else if (type.startsWith("int")) {
			errorMsg = verifyIntParam(parameter, value.toString());
			isValid = (errorMsg == null);
		} else if (type.startsWith("float")) {
			boolean isFloat = ValidateUtil.isDouble(value.toString());
			if (isFloat) {
				float fValue = Float.parseFloat(value.toString());
				if (parameter.equals(ConfConstants.UNFILL_FACTOR) && (fValue < 0 || fValue > 0.3)) {
					isValid = false;
					errorMsg = Messages.bind(Messages.errUnfillFactor, new String[] {parameter });
				} else if (parameter.equals(ConfConstants.INDEX_SCAN_OID_BUFFER_PAGES)
						&& (fValue < 0.05 || fValue > 16)) {
					isValid = false;
					errorMsg = Messages.bind(Messages.errIndexScanInOidBuffPageFloat, new String[] {parameter });
				}
			} else {
				isValid = false;
				errorMsg = Messages.bind(Messages.errOnlyFloat, new String[] {parameter });
			}
		} else if (type.startsWith("string")) {
			String valueStr = value.toString().trim();
			int start = type.indexOf("(");
			int end = type.lastIndexOf(")");
			if (start > 0 && start < end) {
				String valueStrs = type.substring(start + 1, end);
				String[] values = valueStrs.split("\\|");
				boolean isExist = false;
				for (String val : values) {
					if (valueStr.equalsIgnoreCase(val)) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					isValid = false;
					errorMsg = Messages.bind(Messages.errParameterValue, new String[] { parameter });
				}
			}
		}
		if (!isValid && errorMsg != null) {
			CommonUITool.openErrorBox(errorMsg);
		}
		if (isValid && property.equals(columnNameArrs[3]) && !paraValue.equals(value)) {
			isChanged = true;
			map.put("3", value.toString());
			Map<String, Map<String, String>> confParaMap = node.getServer().getServerInfo().getCubridConfParaMap();
			Map<String, String> parameterMap = null;
			if (confParaMap == null) {
				confParaMap = new HashMap<String, Map<String, String>>();
				node.getServer().getServerInfo().setCubridConfParaMap(confParaMap);
			}
			String sectionName = "";
			if (isCommonPara) {
				sectionName = ConfConstants.COMMON_SECTION_NAME;
				parameterMap = confParaMap.get(ConfConstants.COMMON_SECTION_NAME);
			} else {
				sectionName = "[@" + node.getLabel() + "]";
				parameterMap = confParaMap.get(sectionName);
			}
			if (parameterMap == null) {
				parameterMap = new HashMap<String, String>();
				confParaMap.put(sectionName, parameterMap);
			}
			if (isChanged(parameter, value.toString())) {
				String str = value.toString();
				if (parameter.equals(ConfConstants.BACKUP_VOLUME_MAX_SIZE_BYTES)
						|| parameter.equals(ConfConstants.THREAD_STACK_SIZE)) {
					String[] valArr = str.split("\\*");
					int intValue = 1;
					for (int i = 0; valArr != null && i < valArr.length; i++) {
						intValue = intValue * Integer.parseInt(valArr[i]);
					}
					str = String.valueOf(intValue);
				}
				if (str.trim().length() == 0) {
					parameterMap.remove(parameter);
				} else {
					parameterMap.put(parameter, str);
				}
			}
		}
		advancedOptionTableViewer.refresh();
	}

	/**
	 * Verify the integer parameter value
	 *
	 * @param parameter the parameter name
	 * @param value the parameter value
	 * @return the error message if it is null,no error.
	 */
	private String verifyIntParam(String parameter, String value) {
		String paraVal = value;
		boolean isInt = true;
		String errorMsg = null;
		if (parameter.equals(ConfConstants.BACKUP_VOLUME_MAX_SIZE_BYTES)
				|| parameter.equals(ConfConstants.THREAD_STACK_SIZE)) {
			String[] valArr = paraVal.split("\\*");
			int specialValue = 1;
			if (valArr == null) {
				isInt = false;
			} else {
				for (int i = 0; i < valArr.length; i++) {
					if (!ValidateUtil.isInteger(valArr[i]) || valArr[i].length() > 8) {
						isInt = false;
						break;
					} else {
						specialValue = specialValue * Integer.parseInt(valArr[i]);
					}
				}
			}
			if (isInt) {
				paraVal = String.valueOf(specialValue);
			}
		} else {
			isInt = ValidateUtil.isInteger(paraVal);
		}
		if (isInt && paraVal.length() <= 8) {
			int intValue = Integer.parseInt(paraVal);
			if (parameter.equals(ConfConstants.BACKUP_VOLUME_MAX_SIZE_BYTES)) {
				if (intValue != -1 && intValue < 32 * 1024) {
					errorMsg = Messages.bind(Messages.errBackupVolumeMaxSize, new String[] { parameter });
				}
			} else if (parameter.equals(ConfConstants.CSQL_HISTORY_NUM)) {
				if (intValue < 1 || intValue > 200) {
					errorMsg = Messages.bind(Messages.errCsqlHistoryNum, new String[] {parameter });
				}
			} else if (parameter.equals(ConfConstants.GROUP_COMMIT_INTERVAL_IN_MSECS)) {
				if (intValue < 0) {
					errorMsg = Messages.bind(Messages.errGroupCommitInterval, new String[] { parameter });
				}
			} else if (parameter.equals(ConfConstants.INDEX_SCAN_OID_BUFFER_PAGES)) {
				if (intValue < 1 || intValue > 16) {
					errorMsg = Messages.bind(Messages.errIndexScanInOidBuffPage, new String[] { parameter });
				}
			} else if (parameter.equals(ConfConstants.INSERT_EXECUTION_MODE)) {
				if (intValue < 1 || intValue > 7) {
					errorMsg = Messages.bind(Messages.errInsertExeMode, new String[] {parameter });
				}
			} else if (parameter.equals(ConfConstants.LOCK_TIMEOUT_MESSAGE_TYPE)) {
				if (intValue < 0 || intValue > 2) {
					errorMsg = Messages.bind(Messages.errLockTimeOutMessageType, new String[] { parameter });
				}
			} else if (parameter.equals(ConfConstants.QUERY_CACHE_MODE)) {
				if (intValue < 0 || intValue > 2) {
					errorMsg = Messages.bind(Messages.errQueryCachMode, new String[] {parameter });
				}
			} else if (parameter.equals(ConfConstants.TEMP_FILE_MEMORY_SIZE_IN_PAGES)) {
				if (intValue < 0 || intValue > 20) {
					errorMsg = Messages.bind(Messages.errTempFileMemorySize, new String[] {parameter });
				}
			} else if (parameter.equals(ConfConstants.THREAD_STACK_SIZE)
					&& intValue < 64 * 1024) {
				errorMsg = Messages.bind(Messages.errThreadStackSize, new String[] { parameter });
			} else if (parameter.equals(ConfConstants.HA_PORT_ID)
					&& (intValue < 1024 || intValue > 65535)) {
				errorMsg = Messages.bind(Messages.errHaPortId, new String[] { parameter });
			}
		} else {
			if (parameter.equals(ConfConstants.HA_PORT_ID) && (paraVal == null || paraVal.length() == 0)) {
				errorMsg = null;
			} else {
				errorMsg = Messages.bind(Messages.errOnlyInteger, new String[] {parameter });
			}
		}

		return errorMsg;
	}

	/**
	 * initial the page content
	 */
	private void initial() {
		Map<String, Map<String, String>> confParaMap = node.getServer().getServerInfo().getCubridConfParaMap();
		if (confParaMap != null) {
			Iterator<Map.Entry<String, Map<String, String>>> confParaMapIt = confParaMap.entrySet().iterator();
			while (confParaMapIt.hasNext()) {
				Map.Entry<String, Map<String, String>> entry = confParaMapIt.next();
				String key = entry.getKey();
				Map<String, String> map = entry.getValue();
				if (map != null) {
					Map<String, String> sectionMap = new HashMap<String, String>();
					Iterator<Map.Entry<String, String>> mapIt = map.entrySet().iterator();
					while (mapIt.hasNext()) {
						Map.Entry<String, String> mapEntry = mapIt.next();
						String mapKey = mapEntry.getKey();
						String mapValue = mapEntry.getValue();
						sectionMap.put(mapKey, mapValue);
					}
					initialValueMap.put(key, sectionMap);
				}
			}
		}

		//change ha_mode default value in some database section according to the value in common section
		Map<String, String> comonParaMap = initialValueMap.get(ConfConstants.COMMON_SECTION_NAME);
		String haModeValueInComm = comonParaMap.get(ConfConstants.HA_MODE);
		String haModeDefValueInDb = haModeValueInComm == null ? "off" : haModeValueInComm;

		String dbBaseParameters[][] = ConfConstants.getDbBaseParameters(node.getServer().getServerInfo());
		for (int i = 0; i < dbBaseParameters.length; i++) {
			String key = dbBaseParameters[i][0];
			String value = dbBaseParameters[i][1];
			if (key.equals(ConfConstants.HA_MODE) && !isCommonPara) {
				value = haModeDefValueInDb;
			}
			dbBaseParameterValueMap.put(key, value);
		}

		//if ha_mode value is off or no in common section,this value can not be on or yes in database server section
		isCanSetOnForHaMode = haModeValueInComm != null
				&& (haModeValueInComm.equalsIgnoreCase("on") || haModeValueInComm.equalsIgnoreCase("yes"));
		isCanSetOnForHaMode = isCommonPara || (!isCommonPara && isCanSetOnForHaMode);

		defaultValue();
		dataBufferPageText.addModifyListener(this);
		if (dataBufferSizeText != null) {
			dataBufferSizeText.addModifyListener(this);
		}
		sortBufferPageText.addModifyListener(this);
		if (sortBufferSizeText != null) {
			sortBufferSizeText.addModifyListener(this);
		}
		logBufferPageText.addModifyListener(this);
		if (logBufferSizeText != null) {
			logBufferSizeText.addModifyListener(this);
		}
		lockEscalationText.addModifyListener(this);
		lockTimeOutText.addModifyListener(this);
		deadLockDetectIntervalText.addModifyListener(this);
		checkPointIntervalText.addModifyListener(this);
		cubridPortIdText.addModifyListener(this);
		maxClientsText.addModifyListener(this);
	}

	/**
	 *
	 * Restore the default value
	 *
	 */
	private void defaultValue() {
		isolationLevelCombo.setItems(new String[] {
				ConfConstants.TRAN_SERIALIZABLE,
				ConfConstants.TRAN_REP_CLASS_REP_INSTANCE,
				ConfConstants.TRAN_REP_CLASS_COMMIT_INSTANCE,
				ConfConstants.TRAN_REP_CLASS_UNCOMMIT_INSTANCE,
				ConfConstants.TRAN_COMMIT_CLASS_COMMIT_INSTANCE,
				ConfConstants.TRAN_COMMIT_CLASS_UNCOMMIT_INSTANCE });
		// initial the general parameter value from default value
		initValueFromConf(dbBaseParameterValueMap);

		// Initial the general parameter value from common section([common] in
		// cubrid.conf)
		Map<String, String> comonParaMap = initialValueMap.get(ConfConstants.COMMON_SECTION_NAME);
		initValueBaseInitial(comonParaMap);

		// initial the general parameter value from database section([@demodb]
		// in cubrid.conf)
		Map<String, String> paraMap = null;
		if (!isCommonPara) {
			paraMap = initialValueMap.get("[@" + node.getLabel() + "]");
			initValueFromConf(paraMap);
		}
		// Initial the advanced parameters
		String dbAdvancedParameters[][] = ConfConstants.getDbAdvancedParameters(
				node.getServer().getServerInfo(), isCommonPara);
		List<Map<String, String>> advancedParameterList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < dbAdvancedParameters.length; i++) {
			Map<String, String> dataMap = new HashMap<String, String>();
			String para = dbAdvancedParameters[i][0];
			String valueType = dbAdvancedParameters[i][1];
			String defaultValue = dbAdvancedParameters[i][2];
			String paraType = dbAdvancedParameters[i][3];

			if (comonParaMap != null && comonParaMap.get(para) != null) {
				defaultValue = comonParaMap.get(para);
			}
			if (paraMap != null && paraMap.get(para) != null) {
				defaultValue = paraMap.get(para);
			}

			dataMap.put("0", para);
			dataMap.put("1", paraType);
			dataMap.put("2", valueType);
			dataMap.put("3", defaultValue);
			advancedParameterList.add(dataMap);
		}

		advancedOptionTableViewer.setInput(advancedParameterList);
		for (int i = 0; i < advancedOptionTable.getColumnCount(); i++) {
			advancedOptionTable.getColumn(i).pack();
		}
	}

	/**
    * Init the value base initial
    *
    * @param paraMap Map<String, String>
    */
	private void initValueBaseInitial(Map<String, String> paraMap) {
		initValueFromConf(paraMap);
		if (!isSupportSizesProp) {
			return;
		}
		boolean containDataBufferPages = paraMap.containsKey(ConfConstants.DATA_BUFFER_PAGES);
		boolean containDataBufferSize = paraMap.containsKey(ConfConstants.DATA_BUFFER_SIZE);
		boolean containSortBufferPages = paraMap.containsKey(ConfConstants.SORT_BUFFER_PAGES);
		boolean containSortBufferSize = paraMap.containsKey(ConfConstants.SORT_BUFFER_SIZE);
		boolean containLogBufferPages = paraMap.containsKey(ConfConstants.LOG_BUFFER_PAGES);
		boolean containLogBufferSize = paraMap.containsKey(ConfConstants.LOG_BUFFER_SIZE);
		if (!containDataBufferPages && containDataBufferSize) {
			dataBufferGroup.setSizeBtnSelected();
		} else {
			dataBufferGroup.setPageBtnSelected();
		}
		if (!containSortBufferPages && containSortBufferSize) {
			sortBufferGroup.setSizeBtnSelected();
		} else {
			sortBufferGroup.setPageBtnSelected();
		}
		if (!containLogBufferPages && containLogBufferSize) {
			logBufferGroup.setSizeBtnSelected();
		} else {
			logBufferGroup.setPageBtnSelected();
		}
	}

	/**
	 * Initial the general parameter value from common section([common] in
	 * cubrid.conf)
	 *
	 * @param paraMap the pamateter map
	 */
	private void initValueFromConf(Map<String, String> paraMap) {
		if (paraMap == null) {
			return;
		}

		String str = paraMap.get(ConfConstants.DATA_BUFFER_PAGES);
		if (str != null && str.trim().length() > 0) {
			dataBufferPageText.setText(str);
		}

		str = paraMap.get(ConfConstants.DATA_BUFFER_SIZE);
		if (str != null && str.trim().length() > 0 && dataBufferSizeText != null) {
			if (str.matches("^\\d+[KkMmGgTt][Bb]*$")) {
				String subStr = str.replaceAll("\\D+$", "");
				dataBufferSizeText.setText(subStr);
				subStr = str.substring(str.length() - 1);
				dataBufferGroup.setUnitOfSize(subStr);
			} else {
				dataBufferSizeText.setText(str);
				dataBufferGroup.setUnitOfSize("");
				CommonUITool.openErrorBox(Messages.errFormatDataBufferSize);
			}
		}

		str = paraMap.get(ConfConstants.SORT_BUFFER_PAGES);
		if (str != null && str.trim().length() > 0) {
			sortBufferPageText.setText(str);
		}

		str = paraMap.get(ConfConstants.SORT_BUFFER_SIZE);
		if (str != null && str.trim().length() > 0 && sortBufferSizeText != null) {
			if (str.matches("^\\d+[KkMmGgTt][Bb]*$")) {
				String subStr = str.replaceAll("\\D+$", "");
				sortBufferSizeText.setText(subStr);
				subStr = str.substring(str.length() - 1);
				sortBufferGroup.setUnitOfSize(subStr);
			} else {
				sortBufferSizeText.setText(str);
				sortBufferGroup.setUnitOfSize("");
				CommonUITool.openErrorBox(Messages.errFormatSortBufferSize);
			}
		}

		str = paraMap.get(ConfConstants.LOG_BUFFER_PAGES);
		if (str != null && str.trim().length() > 0) {
			logBufferPageText.setText(str);
		}

		str = paraMap.get(ConfConstants.LOG_BUFFER_SIZE);
		if (str != null && str.trim().length() > 0 && logBufferSizeText != null) {
			if (str.matches("^\\d+[KkMmGgTt][Bb]*$")) {
				String subStr = str.replaceAll("\\D+$", "");
				logBufferSizeText.setText(subStr);
				subStr = str.substring(str.length() - 1);
				logBufferGroup.setUnitOfSize(subStr);
			} else {
				logBufferSizeText.setText(str);
				logBufferGroup.setUnitOfSize("");
				CommonUITool.openErrorBox(Messages.errFormatLogBufferSize);
			}
		}

		str = paraMap.get(ConfConstants.LOCK_ESCALATION);
		if (str != null && str.trim().length() > 0) {
			lockEscalationText.setText(str);
		}

		str = paraMap.get(ConfConstants.LOCK_TIMEOUT_IN_SECS);
		if (str != null && str.trim().length() > 0) {
			lockTimeOutText.setText(str);
		}

		str = paraMap.get(ConfConstants.DEADLOCK_DETECTION_INTERVAL_IN_SECS);
		if (str != null && str.trim().length() > 0) {
			deadLockDetectIntervalText.setText(str);
		}

		str = paraMap.get(ConfConstants.CHECKPOINT_INTERVAL_IN_MINS);
		if (str != null && str.trim().length() > 0) {
			checkPointIntervalText.setText(str);
		}

		str = paraMap.get(ConfConstants.ISOLATION_LEVEL);
		if (str != null && str.trim().length() > 0) {
			str = str.replaceAll("\"", "");
			isolationLevelCombo.setText(str);
		}

		str = paraMap.get(ConfConstants.CUBRID_PORT_ID);
		if (str != null && str.trim().length() > 0) {
			cubridPortIdText.setText(str);
		}

		str = paraMap.get(ConfConstants.MAX_CLIENTS);
		if (str != null && str.trim().length() > 0) {
			maxClientsText.setText(str);
		}

		str = paraMap.get(ConfConstants.AUTO_RESTART_SERVER);
		if (str != null && str.trim().length() > 0) {
			if ("yes".equalsIgnoreCase(str)) {
				autoRestartServerButton.setSelection(true);
			} else {
				autoRestartServerButton.setSelection(false);
			}
		}

		str = paraMap.get(ConfConstants.REPLICATION);
		if (str != null && str.trim().length() > 0) {
			if ("yes".equalsIgnoreCase(str)) {
				replicationButton.setSelection(true);
			} else {
				replicationButton.setSelection(false);
			}
		}

		str = paraMap.get(ConfConstants.JAVA_STORED_PROCEDURE);
		if (str != null && str.trim().length() > 0) {
			if ("yes".equalsIgnoreCase(str)) {
				jspButton.setSelection(true);
			} else {
				jspButton.setSelection(false);
			}
		}
	}

	/**
	 * When modify page content and check the validation
	 *
	 * @param event the modify event
	 */
	public void modifyText(ModifyEvent event) {
		boolean isValidDataBufferPage = true;
		boolean isValidDataBufferSize = true;
		boolean isValidSortBufferPage = true;
		boolean isValidSortBufferSize = true;
		boolean isValidLogBufferPage = true;
		boolean isValidLogBufferSize = true;
		if (isSupportSizesProp) {
			if (dataBufferGroup == null) {
				isValidDataBufferPage = false;
				isValidDataBufferSize = false;
			} else {
				if (dataBufferGroup.getPageBtnState()) {
					isValidDataBufferPage = verifyDataBufferPage();
				}
				if (dataBufferGroup.getSizeBtnState()) {
					isValidDataBufferSize = verifyDataBufferSize();
				}
			}
			if (sortBufferGroup == null) {
				isValidSortBufferPage = false;
				isValidSortBufferSize = false;
			} else {
				if (sortBufferGroup.getPageBtnState()) {
					isValidSortBufferPage = verifySortBufferPage();
				}
				if (sortBufferGroup.getSizeBtnState()) {
					isValidSortBufferSize = verifySortBufferSize();
				}
			}
			if (logBufferGroup == null) {
				isValidLogBufferPage = false;
				isValidLogBufferSize = false;
			} else {
				if (logBufferGroup.getPageBtnState()) {
					isValidLogBufferPage = verifyLogBufferPage();
				}
				if (logBufferGroup.getSizeBtnState()) {
					isValidLogBufferSize = verifyLogBufferSize();
				}
			}
		} else {
			isValidDataBufferPage = verifyDataBufferPage();
			isValidSortBufferPage = verifySortBufferPage();
			isValidLogBufferPage = verifyLogBufferPage();
		}

		String lockEscalation = lockEscalationText.getText();
		boolean isValidLockEscalation = ValidateUtil.isInteger(lockEscalation);
		if (isValidLockEscalation) {
			int intValue = Integer.parseInt(lockEscalation);
			if (intValue < 5) {
				isValidLockEscalation = false;
			}
		}
		if (!isValidLockEscalation) {
			setErrorMessage(Messages.errLockEscalation);
			setValid(false);
			return;
		}
		String lockTimeOut = lockTimeOutText.getText();
		boolean isValidLockTimeOut = ValidateUtil.isInteger(lockTimeOut);
		if (isValidLockTimeOut) {
			int intValue = Integer.parseInt(lockTimeOut);
			if (intValue < -1) {
				isValidLockTimeOut = false;
			}
		}
		if (!isValidLockTimeOut) {
			setErrorMessage(Messages.errLockTimeout);
			setValid(false);
			return;
		}
		String deadLockDetectInterval = deadLockDetectIntervalText.getText();
		boolean isValidDeadLockDetectInterval = ValidateUtil.isInteger(deadLockDetectInterval);
		if (isValidDeadLockDetectInterval) {
			int intValue = Integer.parseInt(deadLockDetectInterval);
			if (intValue < 1) {
				isValidDeadLockDetectInterval = false;
			}
		}
		if (!isValidDeadLockDetectInterval) {
			setErrorMessage(Messages.errDeadLock);
			setValid(false);
			return;
		}
		String checkPointInterval = checkPointIntervalText.getText();
		boolean isValidCheckPointInterval = ValidateUtil.isInteger(checkPointInterval);
		if (isValidCheckPointInterval) {
			int intValue = Integer.parseInt(checkPointInterval);
			if (intValue < 1) {
				isValidCheckPointInterval = false;
			}
		}
		if (!isValidCheckPointInterval) {
			setErrorMessage(Messages.errCheckpoint);
			setValid(false);
			return;
		}
		String cubridPortId = cubridPortIdText.getText();
		boolean isValidCubridPortId = ValidateUtil.isInteger(cubridPortId);
		if (isValidCubridPortId) {
			int intValue = Integer.parseInt(cubridPortId);
			if (intValue < 1) {
				isValidCubridPortId = false;
			}
		}
		if (!isValidCubridPortId) {
			setErrorMessage(Messages.errCubridPortId);
			setValid(false);
			return;
		}
		String maxClients = maxClientsText.getText();
		boolean isValidMaxClients = ValidateUtil.isInteger(maxClients);
		if (isValidMaxClients) {
			int intValue = Integer.parseInt(maxClients);
			if (intValue < 10) {
				isValidMaxClients = false;
			}
		}
		if (!isValidMaxClients) {
			setErrorMessage(Messages.errMaxClients);
			setValid(false);
			return;
		}
		boolean isValid = isValidDataBufferPage && isValidDataBufferSize
				&& isValidSortBufferPage && isValidSortBufferSize
				&& isValidLogBufferPage && isValidLogBufferSize
				&& isValidLockEscalation && isValidLockTimeOut
				&& isValidDeadLockDetectInterval && isValidCheckPointInterval
				&& isValidCubridPortId && isValidMaxClients;
		if (isValid) {
			setErrorMessage(null);
		}
		setValid(isValid);
	}

	/**
	 * Verify the log_buffer_size
	 *
	 * @return boolean
	 */
	private boolean verifyLogBufferSize() {
		if (logBufferSizeText == null) {
			return false;
		}
		boolean isValidLogBufferSize;
		String logBufferSize = logBufferSizeText.getText();
		isValidLogBufferSize = ValidateUtil.isInteger(logBufferSize);
		if (isValidLogBufferSize) {
			int intValue = Integer.parseInt(logBufferSize);
			if (intValue < 1) {
				isValidLogBufferSize = false;
			}
		}
		if (!isValidLogBufferSize) {
			setErrorMessage(Messages.errLogBufferSize);
			setValid(false);
		}
		return isValidLogBufferSize;
	}

	/**
	 * Verify log_buffer_page
	 *
	 * @return boolean
	 */
	private boolean verifyLogBufferPage() {
		boolean isValidLogBufferPage;
		if (logBufferPageText == null) {
			return false;
		}
		String logBufferPage = logBufferPageText.getText();
		isValidLogBufferPage = ValidateUtil.isInteger(logBufferPage);
		if (isValidLogBufferPage) {
			int intValue = Integer.parseInt(logBufferPage);
			if (intValue < 3) {
				isValidLogBufferPage = false;
			}
		}
		if (!isValidLogBufferPage) {
			setErrorMessage(Messages.errLogBufferPages);
			setValid(false);
		}
		return isValidLogBufferPage;
	}

	/**
	 * Verify sort_buffer_size
	 *
	 * @return boolean
	 */
	private boolean verifySortBufferSize() {
		if (sortBufferSizeText == null) {
			return false;
		}
		boolean isValidSortBufferSize;
		String sortBufferSize = sortBufferSizeText.getText();
		isValidSortBufferSize = ValidateUtil.isInteger(sortBufferSize);
		if (isValidSortBufferSize) {
			int intValue = Integer.parseInt(sortBufferSize);
			if (intValue < 1) {
				isValidSortBufferSize = false;
			}
		}
		if (!isValidSortBufferSize) {
			setErrorMessage(Messages.errSortBufferSize);
			setValid(false);
		}

		return isValidSortBufferSize;
	}

	/**
	 * Verify sort_buffer_page
	 *
	 * @return boolean
	 */
	private boolean verifySortBufferPage() {
		boolean isValidSortBufferPage;
		if (sortBufferPageText == null) {
			return false;
		}
		String sortBufferPage = sortBufferPageText.getText();
		isValidSortBufferPage = ValidateUtil.isInteger(sortBufferPage);
		if (isValidSortBufferPage) {
			int intValue = Integer.parseInt(sortBufferPage);
			if (intValue < 1) {
				isValidSortBufferPage = false;
			}
		}
		if (!isValidSortBufferPage) {
			setErrorMessage(Messages.errSortBufferPages);
			setValid(false);
		}

		return isValidSortBufferPage;
	}

	/**
	 * Verify data_buffer_size
	 *
	 * @return boolean
	 */
	private boolean verifyDataBufferSize() {
		if (dataBufferSizeText == null) {
			return false;
		}
		boolean isValidDataBufferSize;
		String dataBufferSize = dataBufferSizeText.getText();
		isValidDataBufferSize = ValidateUtil.isInteger(dataBufferSize);
		if (isValidDataBufferSize) {
			int intValue = Integer.parseInt(dataBufferSize);
			if (intValue < 1) {
				isValidDataBufferSize = false;
			}
		}
		if (!isValidDataBufferSize) {
			setErrorMessage(Messages.errDataBufferSize);
			setValid(false);
		}

		return isValidDataBufferSize;
	}

	/**
	 * Verify data_buffer_page
	 *
	 * @return boolean
	 */
	private boolean verifyDataBufferPage() {
		boolean isValidDataBufferPage;
		String dataBufferPage = dataBufferPageText.getText();
		isValidDataBufferPage = ValidateUtil.isInteger(dataBufferPage);
		if (isValidDataBufferPage) {
			int intValue = Integer.parseInt(dataBufferPage);
			if (intValue < 1) {
				isValidDataBufferPage = false;
			}
		}
		if (!isValidDataBufferPage) {
			setErrorMessage(Messages.errDataBufferPages);
			setValid(false);
		}

		return isValidDataBufferPage;
	}

	/**
	 * Restore the default values
	 */
	protected void performDefaults() {
		defaultValue();
		if (isApply) {
			perform(initialValueMap);
		}
		isChanged = false;
		isApply = false;
	}

	/**
	 * Execute and save
	 *
	 * @return <code>true</code>if it is successful;<code>false</code>otherwise
	 */
	public boolean performOk() {
		if (dataBufferPageText == null || dataBufferPageText.isDisposed()) {
			return true;
		}
		if (!isAdmin) {
			return true;
		}
		Map<String, Map<String, String>> confParaMap = node.getServer().getServerInfo().getCubridConfParaMap();
		if (confParaMap == null) {
			confParaMap = new HashMap<String, Map<String, String>>();
			node.getServer().getServerInfo().setCubridConfParaMap(confParaMap);
		}
		Map<String, String> paraMap = null;
		String sectionName = "";
		if (isCommonPara) {
			sectionName = ConfConstants.COMMON_SECTION_NAME;
			paraMap = confParaMap.get(ConfConstants.COMMON_SECTION_NAME);
		} else {
			sectionName = "[@" + node.getLabel() + "]";
			paraMap = confParaMap.get(sectionName);
		}
		if (paraMap == null) {
			paraMap = new HashMap<String, String>();
			confParaMap.put(sectionName, paraMap);
		}

		if (paraMap != null) {
			if (isSupportSizesProp) {
				if (dataBufferGroup.getPageBtnState()) {
					paraMap.put(ConfConstants.DATA_BUFFER_PAGES,
							dataBufferPageText.getText());
					isChanged = true;
				} else {
					paraMap.remove(ConfConstants.DATA_BUFFER_PAGES);
				}
			} else {
				if (isChanged(ConfConstants.DATA_BUFFER_PAGES,
						dataBufferPageText.getText())) {
					paraMap.put(ConfConstants.DATA_BUFFER_PAGES,
							dataBufferPageText.getText());
					isChanged = true;
				}
			}
			if (isSupportSizesProp) {
				if (dataBufferGroup.getSizeBtnState()) {
					String dataBufferSize = dataBufferSizeText.getText().trim()
							+ dataBufferGroup.getUnitOfSize();
					paraMap.put(ConfConstants.DATA_BUFFER_SIZE, dataBufferSize);
					isChanged = true;
				} else {
					paraMap.remove(ConfConstants.DATA_BUFFER_SIZE);
				}
			}
			if (isSupportSizesProp) {
				if (sortBufferGroup.getPageBtnState()) {
					paraMap.put(ConfConstants.SORT_BUFFER_PAGES,
							sortBufferPageText.getText());
					isChanged = true;
				} else {
					paraMap.remove(ConfConstants.SORT_BUFFER_PAGES);
				}
			} else {
				if (isChanged(ConfConstants.SORT_BUFFER_PAGES,
						sortBufferPageText.getText())) {
					paraMap.put(ConfConstants.SORT_BUFFER_PAGES,
							sortBufferPageText.getText());
					isChanged = true;
				}
			}
			if (isSupportSizesProp) {
				if (sortBufferGroup.getSizeBtnState()) {
					String sortBufferSize = sortBufferSizeText.getText().trim()
							+ sortBufferGroup.getUnitOfSize();
					paraMap.put(ConfConstants.SORT_BUFFER_SIZE, sortBufferSize);
					isChanged = true;
				} else {
					paraMap.remove(ConfConstants.SORT_BUFFER_SIZE);
				}
			}
			if (isSupportSizesProp) {
				if (logBufferGroup.getPageBtnState()) {
					paraMap.put(ConfConstants.LOG_BUFFER_PAGES,
							logBufferPageText.getText());
					isChanged = true;
				} else {
					paraMap.remove(ConfConstants.LOG_BUFFER_PAGES);
				}
			} else {
				if (isChanged(ConfConstants.LOG_BUFFER_PAGES,
						logBufferPageText.getText())) {
					paraMap.put(ConfConstants.LOG_BUFFER_PAGES,
							logBufferPageText.getText());
					isChanged = true;
				}
			}
			if (isSupportSizesProp) {
				if (logBufferGroup.getSizeBtnState()) {
					String logBufferSize = logBufferSizeText.getText().trim()
							+ logBufferGroup.getUnitOfSize();
					paraMap.put(ConfConstants.LOG_BUFFER_SIZE, logBufferSize);
					isChanged = true;
				} else {
					paraMap.remove(ConfConstants.LOG_BUFFER_SIZE);
				}
			}

			if (isChanged(ConfConstants.LOCK_ESCALATION, lockEscalationText.getText())) {
				paraMap.put(ConfConstants.LOCK_ESCALATION, lockEscalationText.getText());
				isChanged = true;
			}
			if (isChanged(ConfConstants.LOCK_TIMEOUT_IN_SECS, lockTimeOutText.getText())) {
				paraMap.put(ConfConstants.LOCK_TIMEOUT_IN_SECS, lockTimeOutText.getText());
				isChanged = true;
			}
			if (isChanged(ConfConstants.DEADLOCK_DETECTION_INTERVAL_IN_SECS, deadLockDetectIntervalText.getText())) {
				paraMap.put(ConfConstants.DEADLOCK_DETECTION_INTERVAL_IN_SECS, deadLockDetectIntervalText.getText());
				isChanged = true;
			}
			if (isChanged(ConfConstants.CHECKPOINT_INTERVAL_IN_MINS, checkPointIntervalText.getText())) {
				paraMap.put(ConfConstants.CHECKPOINT_INTERVAL_IN_MINS, checkPointIntervalText.getText());
				isChanged = true;
			}
			if (isChanged(ConfConstants.ISOLATION_LEVEL, "\"" + isolationLevelCombo.getText() + "\"")) {
				paraMap.put(ConfConstants.ISOLATION_LEVEL, "\"" + isolationLevelCombo.getText() + "\"");
				isChanged = true;
			}
			if (isChanged(ConfConstants.MAX_CLIENTS, maxClientsText.getText())) {
				paraMap.put(ConfConstants.MAX_CLIENTS, maxClientsText.getText());
				isChanged = true;
			}
			if (isChanged(ConfConstants.AUTO_RESTART_SERVER, autoRestartServerButton.getSelection() ? "yes" : "no")) {
				paraMap.put(ConfConstants.AUTO_RESTART_SERVER, autoRestartServerButton.getSelection() ? "yes" : "no");
				isChanged = true;
			}
			if (isChanged(ConfConstants.REPLICATION, replicationButton.getSelection() ? "yes" : "no")) {
				paraMap.put(ConfConstants.REPLICATION, replicationButton.getSelection() ? "yes" : "no");
				isChanged = true;
			}
			if (isChanged(ConfConstants.JAVA_STORED_PROCEDURE, jspButton.getSelection() ? "yes" : "no")) {
				paraMap.put(ConfConstants.JAVA_STORED_PROCEDURE, jspButton.getSelection() ? "yes" : "no");
				isChanged = true;
			}
		}

		if (!isChanged) {
			return true;
		}
		perform(confParaMap);
		isChanged = false;
		isApply = true;
		return true;
	}

	/**
	 * Check the page content is changed
	 *
	 * @param paraName the parameter name
	 * @param uiValue the value
	 * @return <code>true</code> if it is changed;<code>false</code>otherwise
	 */
	private boolean isChanged(String paraName, String uiValue) {
		String dbAdvancedParameters[][] = ConfConstants.getDbAdvancedParameters(
				node.getServer().getServerInfo(), isCommonPara);
		String defaultValue = this.dbBaseParameterValueMap.get(paraName);
		if (defaultValue == null) {
			for (int i = 0; i < dbAdvancedParameters.length; i++) {
				String key = dbAdvancedParameters[i][0];
				String value = dbAdvancedParameters[i][2];
				if (key.equals(paraName)) {
					defaultValue = value;
					break;
				}
			}
		}
		Map<String, Map<String, String>> confParaMap = node.getServer().getServerInfo().getCubridConfParaMap();
		if (confParaMap == null) {
			if (uiValue.equals(defaultValue)) {
				return false;
			}
			return true;
		}
		Map<String, String> paraMap = null;
		Map<String, String> commonParaMap = confParaMap.get(ConfConstants.COMMON_SECTION_NAME);
		if (!isCommonPara) {
			paraMap = confParaMap.get("[@" + node.getLabel() + "]");
		}
		if (isCommonPara) {
			String commonValue = commonParaMap == null ? null : commonParaMap.get(paraName);
			if (commonValue == null && !uiValue.equals(defaultValue)) {
				return true;
			}
			if (commonValue != null && !uiValue.equals(commonValue)) {
				return true;
			}
		} else if (!isCommonPara) {
			String paraValue = paraMap == null ? null : paraMap.get(paraName);
			String commonValue = commonParaMap == null ? null : commonParaMap.get(paraName);
			if (paraValue == null && commonValue == null && !uiValue.equals(defaultValue)) {
				return true;
			}
			if (paraValue == null && commonValue != null && !uiValue.equals(commonValue)) {
				return true;
			}
			if (paraValue != null && !uiValue.equals(paraValue)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Perform the task and set cubrid.conf file parameter
	 *
	 * @param confParaMap the conf parameters map
	 */
	private void perform(Map<String, Map<String, String>> confParaMap) {
		SetCubridConfParameterTask task = new SetCubridConfParameterTask(node.getServer().getServerInfo());
		task.setConfParameters(confParaMap);
		CommonTaskExec taskExcutor = new CommonTaskExec(Messages.setCubridParameterTaskName);
		taskExcutor.addTask(task);
		new ExecTaskWithProgress(taskExcutor).exec(true, true);
		if (taskExcutor.isSuccess()) {
			CommonUITool.openInformationBox(Messages.titleSuccess, Messages.msgChangeServerParaSuccess);
		}
	}
}
