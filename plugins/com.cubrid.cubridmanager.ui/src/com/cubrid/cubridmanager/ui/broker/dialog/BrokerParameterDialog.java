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
package com.cubrid.cubridmanager.ui.broker.dialog;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.AddEditType;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.utils.CubridBrokerUtils;
import com.cubrid.cubridmanager.ui.broker.Messages;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSetting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog that shows all brokers information and users can <code>add</code>, <code>edit</code>,
 * <code>delete</code> broker.
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-3-31 created by lizhiqiang
 */
public class BrokerParameterDialog extends CMTitleAreaDialog {

    private Text nameTxt;
    private Button refreshBtn;
    private Text intervalTxt;
    private String[] columnNameArrs;
    private TableViewer paraTableViewer;
    private Table paraTable;
    private Map<String, String> brokerMap;
    private BrokerIntervalSetting brokerIntervalSetting;
    private ICubridNode cubridNode;
    private boolean isOkenable[];
    private List<Map<String, String>> brokerLst;

    private AddEditType operation;
    private ServerUserInfo userInfo;
    private String editTitle;
    private String editMsg;
    private String masterShmId;
    private final boolean isQueryOrTransTimeUseMs;
    private ServerInfo serverInfo;

    /**
     * The Constructor
     *
     * @param parentShell
     * @param operation
     * @param node
     * @param brokerLst
     * @param masterShmId
     */
    public BrokerParameterDialog(
            Shell parentShell,
            AddEditType operation,
            ICubridNode node,
            List<Map<String, String>> brokerLst,
            String masterShmId) {
        super(parentShell);
        this.operation = operation;
        isOkenable = new boolean[3];
        this.masterShmId = masterShmId;
        cubridNode = node;
        this.brokerLst = brokerLst;
        serverInfo = node.getServer().getServerInfo();
        userInfo = serverInfo.getLoginedUserInfo();
        isQueryOrTransTimeUseMs = CompatibleUtil.isQueryOrTransTimeUseMs(serverInfo);
    }

    /**
     * The Constructor
     *
     * @param parentShell
     */
    public BrokerParameterDialog(
            Shell parentShell,
            AddEditType operation,
            ICubridNode node,
            List<Map<String, String>> brokerLst,
            String masterShmId,
            Map<String, String> map,
            BrokerIntervalSetting brokerIntervalSetting) {
        this(parentShell, operation, node, brokerLst, masterShmId);
        this.brokerMap = map;
        this.brokerIntervalSetting = brokerIntervalSetting;
        if (null == brokerIntervalSetting) {
            this.brokerIntervalSetting = new BrokerIntervalSetting();
            this.brokerIntervalSetting.setBrokerName(brokerMap.get("0"));
        }
    }

    /**
     * Create the dialog area content
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        GridData gdComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdComposite.heightHint = 440;
        composite.setLayoutData(gdComposite);

        Composite nameComp = new Composite(composite, SWT.NONE);
        nameComp.setLayout(new GridLayout(2, false));
        nameComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        final Label brokerNameLabel = new Label(nameComp, SWT.NONE);
        brokerNameLabel.setText(Messages.brokerNameLbl);

        nameTxt = new Text(nameComp, SWT.BORDER);
        final GridData gdNameTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
        nameTxt.setLayoutData(gdNameTxt);

        TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
        final GridData gdTabFolder = new GridData(SWT.FILL, SWT.FILL, true, true);
        tabFolder.setLayoutData(gdTabFolder);

        tabFolder.setLayout(new GridLayout());

        TabItem item = new TabItem(tabFolder, SWT.NONE);
        item.setText(Messages.paraParameterNameOfTap);
        item.setControl(createParamComp(tabFolder));

        item = new TabItem(tabFolder, SWT.NONE);
        item.setText(Messages.paraRefreshNameOfTap);
        item.setControl(createRefreshComp(tabFolder));
        initialize();
        if (operation == AddEditType.ADD) {
            setTitle(Messages.addTitle);
            setMessage(Messages.addMsg);
            nameTxt.setEnabled(true);
            getShell().setText(Messages.shellAddTitle);
            brokerMap = new HashMap<String, String>();
            isOkenable[0] = false;
            isOkenable[1] = false;
            isOkenable[2] = false;
        } else {
            editTitle = Messages.bind(Messages.editTitle, brokerMap.get("0"));
            editMsg = Messages.bind(Messages.editMsg, brokerMap.get("0"));
            setTitle(editTitle);
            setMessage(editMsg);
            getShell().setText(Messages.shellEditTitle);
            nameTxt.setEnabled(false);
            isOkenable[0] = true;
            isOkenable[1] = true;
            isOkenable[2] = true;
        }

        nameTxt.addModifyListener(
                new ModifyListener() {

                    public void modifyText(ModifyEvent event) {
                        String content = ((Text) event.widget).getText().trim();
                        if (content.length() == 0) {
                            isOkenable[0] = false;
                        } else if (content.equalsIgnoreCase("broker")) {
                            setErrorMessage(Messages.errBrokerName);
                            isOkenable[0] = false;
                        } else {
                            boolean hasName = false;
                            if (brokerLst != null) {
                                for (Map<String, String> brokerInfo : brokerLst) {
                                    if (content.equalsIgnoreCase(brokerInfo.get("0"))) {
                                        hasName = true;
                                        break;
                                    }
                                }
                            }
                            if (hasName) {
                                isOkenable[0] = false;
                                setErrorMessage(Messages.errReduplicateName);
                            } else {
                                isOkenable[0] = true;
                                setErrorMessage(null);
                            }
                        }
                        enableOk();
                    }
                });
        return parentComp;
    }

    /** Initializes the parameters of dialog */
    private void initialize() {
        if (null != brokerMap) {
            nameTxt.setText(brokerMap.get("0"));
        }
        String brokerParameters[][] = ConfConstants.getBrokerParameters(serverInfo);
        List<Map<String, String>> parameterList = new ArrayList<Map<String, String>>();

        for (int i = 0; i < brokerParameters.length; i++) {
            Map<String, String> dataMap = new HashMap<String, String>();
            String para = brokerParameters[i][0];
            String type = brokerParameters[i][1];
            String defaultValue = brokerParameters[i][2];
            String paramType = brokerParameters[i][3];
            String bgColor = "grey";
            if ("general".equals(paramType)) {
                continue;
            } else if ("common".equals(paramType)) {
                bgColor = "white";
            }
            if (para.equals(ConfConstants.APPL_SERVER_PORT)) {
                String newValue =
                        CubridBrokerUtils.getBrokerPort(
                                cubridNode.getServer().getServerInfo(), dataMap);
                if (newValue == null) {
                    continue;
                }
                defaultValue = brokerMap.get(para);
                if (null != defaultValue && !newValue.equals(defaultValue)) {
                    bgColor = "white";
                }
                defaultValue = newValue;
            }

            if (brokerMap != null && brokerMap.get(para) != null) {
                String newValue = brokerMap.get(para);
                if ("advance".equals(paramType)
                        && !defaultValue.equals(newValue)
                        && !para.equalsIgnoreCase(ConfConstants.APPL_SERVER_PORT)) {
                    bgColor = "white";
                }
                defaultValue = newValue;
            }
            if (para.equals(ConfConstants.LONG_QUERY_TIME)
                    || para.equals(ConfConstants.LONG_TRANSACTION_TIME)) {
                if (isQueryOrTransTimeUseMs) {
                    type += "(msec)";
                    if (brokerMap == null) {
                        double dVal = Double.parseDouble(defaultValue) * 1000;
                        defaultValue = Integer.toString((int) (dVal + 0.5));
                    } else {
                        String newValue = brokerMap.get(para);
                        if (null == newValue
                                || (null != newValue && newValue.equals(defaultValue))) {
                            double dVal = Double.parseDouble(defaultValue) * 1000;
                            defaultValue = Integer.toString((int) (dVal + 0.5));
                        }
                    }
                } else {
                    type += "(sec)";
                }
            }
            dataMap.put("0", para);
            dataMap.put("1", type);
            dataMap.put("2", defaultValue);
            dataMap.put("bgColor", bgColor);
            parameterList.add(dataMap);
        }
        paraTableViewer.setInput(parameterList);
        for (int k = 0; k < paraTable.getItemCount(); k++) {
            if (parameterList.get(k).get("bgColor").equalsIgnoreCase("grey")) {
                paraTable.getItem(k).setBackground(ResourceManager.getColor(200, 200, 200));
            }
        }

        for (int i = 0; i < paraTable.getColumnCount(); i++) {
            paraTable.getColumn(i).pack();
        }

        if (userInfo.getCasAuth() == CasAuthType.AUTH_ADMIN) {
            linkEditorForTable();
        }

        // initialize refresh
        if (operation == AddEditType.EDIT) {
            boolean state = brokerIntervalSetting.isOn();
            String interval = brokerIntervalSetting.getInterval();
            if (state) {
                refreshBtn.setSelection(true);
                intervalTxt.setText(interval);
            } else {
                refreshBtn.setSelection(false);
            }
        }
    }

    /** Makes a certain column of table can be edited */
    private void linkEditorForTable() {
        paraTableViewer.setColumnProperties(columnNameArrs);
        CellEditor[] editors = new CellEditor[3];
        editors[0] = null;
        editors[1] = null;
        editors[2] = new TextCellEditor(paraTable);
        paraTableViewer.setCellEditors(editors);
        paraTableViewer.setCellModifier(new ParameterCellModifier());
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
    }

    /**
     * Creates the parameter composite
     *
     * @param parent the parent composite
     * @return the composite
     */
    private Control createParamComp(Composite parent) {
        columnNameArrs =
                new String[] {
                    Messages.paraTblParameter, Messages.paraTblValueType, Messages.paraTblParamValue
                };
        paraTableViewer =
                CommonUITool.createCommonTableViewer(
                        parent,
                        null,
                        columnNameArrs,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 220));
        paraTable = paraTableViewer.getTable();

        return paraTable;
    }

    /**
     * Creates the refresh composite
     *
     * @param parent the parent composite
     * @return the composite
     */
    private Control createRefreshComp(Composite parent) {
        Composite refreshComp = new Composite(parent, SWT.None);
        refreshComp.setLayout(new GridLayout());
        final GridData gdRefreshComp = new GridData(SWT.FILL, SWT.TOP, true, false);
        refreshComp.setLayoutData(gdRefreshComp);

        final Label tipLbl = new Label(refreshComp, SWT.NONE);
        final GridData gdTipLbl = new GridData(SWT.LEFT, SWT.TOP, true, false);
        tipLbl.setText(Messages.refreshTitle);
        tipLbl.setLayoutData(gdTipLbl);

        final Composite radioComp = new Composite(refreshComp, SWT.None);
        final GridData gdRadioComp = new GridData(SWT.FILL, SWT.TOP, true, false);
        radioComp.setLayoutData(gdRadioComp);
        radioComp.setLayout(new GridLayout(3, false));

        refreshBtn = new Button(radioComp, SWT.CHECK);
        refreshBtn.setText(Messages.refreshOnLbl);
        refreshBtn.setSelection(false);

        intervalTxt = new Text(radioComp, SWT.BORDER | SWT.RIGHT);
        final GridData gdIntervalTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
        intervalTxt.setLayoutData(gdIntervalTxt);
        intervalTxt.setText("1");
        intervalTxt.setEnabled(false);

        final Label secLbl = new Label(radioComp, SWT.NONE);
        final GridData gdSecLbl = new GridData(SWT.LEFT, SWT.TOP, true, false);
        secLbl.setText(Messages.refreshUnitLbl);
        tipLbl.setLayoutData(gdSecLbl);

        refreshBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (refreshBtn.getSelection()) {
                            intervalTxt.setEnabled(true);
                        } else {
                            intervalTxt.setEnabled(false);
                        }
                    }
                });

        intervalTxt.addVerifyListener(
                new VerifyListener() {
                    public void verifyText(VerifyEvent event) {
                        if (!"".equals(event.text) && !ValidateUtil.isNumber(event.text)) {
                            event.doit = false;
                            return;
                        }
                    }
                });
        return refreshComp;
    }

    /** Call it when "ok" button pressed */
    public void okPressed() {
        performOk();
        super.okPressed();
    }

    /** Executes tasks */
    private void performOk() {
        TableItem[] items = paraTable.getItems();
        for (TableItem item : items) {
            String key = item.getText(0).trim();
            if (key.length() != 0) {
                if (key.equals(ConfConstants.LONG_QUERY_TIME)
                        || key.equals(ConfConstants.LONG_TRANSACTION_TIME)) {
                    String newVal = item.getText(2).trim();
                    if (isQueryOrTransTimeUseMs) {
                        double dVal = Double.parseDouble(newVal) / 1000;
                        newVal = Double.toString(dVal);
                    }
                    brokerMap.put(key, newVal);

                } else {
                    brokerMap.put(key, item.getText(2).trim());
                }
            }
        }

        brokerMap.put("0", nameTxt.getText().trim());
        brokerMap.put("1", brokerMap.get(ConfConstants.BROKER_PORT));

        String brokerName = nameTxt.getText().trim();
        boolean state = refreshBtn.getSelection();
        String interval = intervalTxt.getText().trim();
        if (null == brokerIntervalSetting) {
            brokerIntervalSetting = new BrokerIntervalSetting();
            brokerIntervalSetting.setBrokerName(brokerName);
        }
        brokerIntervalSetting.setInterval(interval);
        brokerIntervalSetting.setOn(state);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        if (operation.equals(AddEditType.ADD)) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    /** Enable the "OK" button */
    private void enableOk() {
        boolean is = true;
        for (int i = 0; i < isOkenable.length; i++) {
            is = is && isOkenable[i];
        }
        if (is) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    /**
     * Gets the brokerMap
     *
     * @return the broker map
     */
    public Map<String, String> getBrokerMap() {
        return brokerMap;
    }

    /**
     * Sets the brokerMap
     *
     * @param brokerMap the broker map
     */
    public void setBrokerMap(Map<String, String> brokerMap) {
        this.brokerMap = brokerMap;
    }

    /**
     * Gets the brokerIntervalSetting;
     *
     * @return the broker interval setting
     */
    public BrokerIntervalSetting getBrokerIntervalSetting() {
        return brokerIntervalSetting;
    }

    /**
     * A class that implements the interface of ICellModifier for paraTableViewer
     *
     * <p>ParameterCellModifier
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-1-7 created by lizhiqiang
     */
    private final class ParameterCellModifier implements ICellModifier {
        /**
         * Checks whether the given property of the given element can be modified.
         *
         * @param element the element
         * @param property the property
         * @return <code>true</code> if the property can be modified, and <code>false</code> if it
         *     is not modifiable
         */
        public boolean canModify(Object element, String property) {
            if (property.equals(columnNameArrs[2])) {
                return true;
            }
            return false;
        }

        /**
         * Returns the value for the given property of the given element. Returns <code>null</code>
         * if the element does not have the given property.
         *
         * @param element the element
         * @param property the property
         * @return the property value
         */
        @SuppressWarnings("unchecked")
        public Object getValue(Object element, String property) {
            Map<String, String> map = (Map<String, String>) element;
            if (property.equals(columnNameArrs[2])) {
                return map.get("2");
            }
            return null;
        }

        /**
         * Modifies the value for the given property of the given element. Has no effect if the
         * element does not have the given property, or if the property cannot be modified.
         *
         * @param element the model element or SWT Item (see above)
         * @param property the property
         * @param value the new property value
         */
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
            String paramName = map.get("0");
            String type = map.get("1");
            boolean isValid = true;
            if (type.indexOf("int") >= 0) {
                boolean isInt = ValidateUtil.isInteger(value.toString());
                if (!isInt) {
                    isValid = false;
                }
            } else if (type.startsWith("string")) {
                String valueStr = value.toString().trim();
                int start = type.indexOf("(");
                int end = type.indexOf(")");
                if (start > 0) {
                    String valueStrs = type.substring(start + 1, end);
                    String[] values = valueStrs.split("\\|");
                    boolean isExist = false;
                    for (String val : values) {
                        if (valueStr.equals(val)) {
                            isExist = true;
                        }
                    }
                    if (!isExist) {
                        isValid = false;
                    }
                }
            }

            if (!isValid) {
                CommonUITool.openErrorBox(
                        Messages.bind(Messages.errParameterValue, new Object[] {paramName}));
            }
            if (type.indexOf("int") >= 0 && isValid) {
                isValid = dealIntValue(value, paramName, isValid);
            }
            if (paramName.equalsIgnoreCase(ConfConstants.BROKER_PORT) && isValid) {
                isValid = dealParaBrokerPort(value, paramName, isValid);
            }
            if (paramName.equalsIgnoreCase(ConfConstants.APPL_SERVER_SHM_ID) && isValid) {
                isValid = dealParaApplServerShmId(value, paramName, isValid);
            }
            if (isValid) {
                map.put("2", value.toString());
            }
            paraTableViewer.refresh();
        }

        /**
         * deal with the case when the parameter is appl_server_shm_id
         *
         * @param value the cell value
         * @param paramName the parameter name
         * @param isValid whether it is valid
         * @return whether it is valid
         */
        private boolean dealParaApplServerShmId(Object value, String paramName, boolean isValid) {
            boolean result = isValid;
            int shmId = Integer.parseInt(value.toString());
            if (shmId < 1024 || shmId > 65535) {
                CommonUITool.openErrorBox(Messages.bind(Messages.errBrokerPortAndShmId, paramName));
                result = false;
                isOkenable[2] = false;
            }
            if (result) {
                String paramValue = value.toString().trim();
                if (paramValue.equals(masterShmId)) {
                    result = false;
                    CommonUITool.openErrorBox(
                            Messages.bind(Messages.errUseMasterShmId, paramValue));
                }
                if (result) {
                    if (null == brokerLst || brokerLst.isEmpty()) {
                        isOkenable[2] = true;
                    } else {
                        for (Map<String, String> brokerMap : brokerLst) {
                            if (operation == AddEditType.EDIT
                                    && brokerMap.get("0").equals(nameTxt.getText())) {
                                isOkenable[2] = true;
                                continue;
                            }
                            String otherPort = brokerMap.get(ConfConstants.APPL_SERVER_SHM_ID);
                            if (paramValue.equalsIgnoreCase(otherPort)) {
                                isOkenable[2] = false;
                                CommonUITool.openErrorBox(Messages.errReduplicateShmId);
                                result = false;
                                break;
                            } else {
                                isOkenable[2] = true;
                                result = true;
                            }
                        }
                    }
                }
                enableOk();
            }
            return result;
        }

        /**
         * Deal with the case when the parameter is broker_port
         *
         * @param value Object
         * @param paramName the parameter name
         * @param isValid whether it is valid
         * @return whether the result is valid
         */
        @SuppressWarnings("unchecked")
        private boolean dealParaBrokerPort(Object value, String paramName, boolean isValid) {
            boolean result = isValid;
            int port = Integer.parseInt(value.toString());
            if (port < 1024 || port > 65535) {
                CommonUITool.openErrorBox(Messages.bind(Messages.errBrokerPortAndShmId, paramName));
                result = false;
            }
            if (result) {
                String paramValue = value.toString().trim();
                if (null == brokerLst || brokerLst.isEmpty()) {
                    isOkenable[1] = true;
                } else {
                    for (Map<String, String> brokerMap : brokerLst) {
                        if (operation == AddEditType.EDIT
                                && brokerMap.get("0").equals(nameTxt.getText())) {
                            isOkenable[1] = true;
                            continue;
                        }
                        String otherPort = brokerMap.get(ConfConstants.BROKER_PORT);
                        if (paramValue.equalsIgnoreCase(otherPort)) {
                            isOkenable[1] = false;
                            CommonUITool.openErrorBox(Messages.errReduplicatePort);
                            result = false;
                            break;
                        } else {
                            isOkenable[1] = true;
                            result = true;
                        }
                    }
                }
                enableOk();
            }
            if (result) {
                String paramValue = value.toString().trim();
                int intServerPortValue = Integer.parseInt(paramValue) + 1;
                List<Map<String, String>> parameterList =
                        (List<Map<String, String>>) paraTableViewer.getInput();
                for (Map<String, String> dataMap : parameterList) {
                    if (dataMap.get("0").equalsIgnoreCase(ConfConstants.APPL_SERVER_PORT)) {
                        String serverPortValue = Integer.toString(intServerPortValue);
                        dataMap.put("2", serverPortValue);
                    }
                }
            }
            return result;
        }

        /**
         * Deal with the data type is int
         *
         * @param value the object of the cell
         * @param paramName the parameter name
         * @param isValid whether it is valid
         * @return whether it is valid
         */
        @SuppressWarnings("unchecked")
        private boolean dealIntValue(Object value, String paramName, boolean isValid) {
            boolean result = isValid;
            int intValue = Integer.parseInt(value.toString());
            if (paramName.equalsIgnoreCase(ConfConstants.MAX_STRING_LENGTH)) {
                if (intValue == 0 || intValue < -1) {
                    result = false;
                    CommonUITool.openErrorBox(
                            Messages.bind(
                                    Messages.errMaxStringLengthValue, new Object[] {paramName}));
                }
            } else {
                if (intValue <= 0) {
                    result = false;
                    CommonUITool.openErrorBox(
                            Messages.bind(Messages.errPositiveValue, new Object[] {paramName}));
                }
                List<Map<String, String>> parameterList =
                        (List<Map<String, String>>) paraTableViewer.getInput();
                if (paramName.equalsIgnoreCase(ConfConstants.MIN_NUM_APPL_SERVER)) {
                    Map<String, String> dataMap = parameterList.get(3);
                    String maxNumApplServer = dataMap.get("2");
                    if (maxNumApplServer.trim().length() > 0
                            && intValue > Integer.parseInt(maxNumApplServer.trim())) {
                        result = false;
                        CommonUITool.openErrorBox(
                                Messages.bind(
                                        Messages.errMinNumApplServerValue,
                                        new Object[] {paramName}));
                    }
                }
                if (paramName.equalsIgnoreCase(ConfConstants.MAX_NUM_APPL_SERVER)) {
                    Map<String, String> dataMap = parameterList.get(2);
                    String minNumApplServer = dataMap.get("2");
                    if (minNumApplServer.trim().length() > 0
                            && intValue < Integer.parseInt(minNumApplServer.trim())) {
                        result = false;
                        CommonUITool.openErrorBox(
                                Messages.bind(
                                        Messages.errMaxNumApplServeValue,
                                        new Object[] {paramName}));
                    }
                }
            }
            return result;
        }
    }
}
