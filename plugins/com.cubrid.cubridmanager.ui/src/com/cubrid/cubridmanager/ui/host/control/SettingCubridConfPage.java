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
package com.cubrid.cubridmanager.ui.host.control;

import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.dialog.CMWizardPage;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.CubridConfParaConstants;
import com.cubrid.cubridmanager.core.common.model.HAConfParaConstants;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.host.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Setting Cubrid Conf Page
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-12-11 created by Kevin.Wang
 */
public class SettingCubridConfPage extends CMWizardPage {
    private static final String PAGE_NAME = SettingCubridConfPage.class.getName();

    private Label masterHostLabel;
    private Label slaveHostLabel;
    private Combo masterDBCombo;

    private TableViewer masterViewer;
    private TableViewer slaveViewer;
    public static final String[] PROPS = {"key", "value"};

    protected SettingCubridConfPage() {
        super(PAGE_NAME);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());
        setControl(container);
        setDescription(Messages.descSettingCubridPage);

        Composite leftComposite = new Composite(container, SWT.NONE);
        leftComposite.setLayout(new GridLayout(3, false));
        FormData leftData = new FormData();
        leftData.top = new FormAttachment(0, 5);
        leftData.bottom = new FormAttachment(100, 0);
        leftData.left = new FormAttachment(0, 5);
        leftData.right = new FormAttachment(50, -5);
        leftComposite.setLayoutData(leftData);

        Label separator = new Label(container, SWT.SEPARATOR);
        FormData separatorData = new FormData();
        separatorData.top = new FormAttachment(0, 5);
        separatorData.bottom = new FormAttachment(100, -5);
        separatorData.left = new FormAttachment(50, -5);
        separatorData.right = new FormAttachment(50, 5);
        separator.setLayoutData(separatorData);

        Composite rightComposite = new Composite(container, SWT.NONE);
        rightComposite.setLayout(new GridLayout(3, false));
        FormData rightData = new FormData();
        rightData.top = new FormAttachment(0, 5);
        rightData.bottom = new FormAttachment(100, 0);
        rightData.left = new FormAttachment(50, 5);
        rightData.right = new FormAttachment(100, -5);
        rightComposite.setLayoutData(rightData);

        /*Create left widget*/
        Label hostALabel = new Label(leftComposite, SWT.None);
        hostALabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        hostALabel.setText(Messages.lblMaster);

        masterHostLabel = new Label(leftComposite, SWT.None);
        masterHostLabel.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

        masterViewer = new TableViewer(leftComposite, SWT.BORDER | SWT.FULL_SELECTION);
        masterViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, -1));
        masterViewer.getTable().setLinesVisible(true);
        masterViewer.getTable().setHeaderVisible(true);
        masterViewer.setContentProvider(new TableContentProvider());
        masterViewer.setLabelProvider(new CubridConfTableProvider());
        masterViewer.setColumnProperties(PROPS);

        CellEditor[] editorsA = new CellEditor[2];
        editorsA[0] = null;
        editorsA[1] = new TextCellEditor(masterViewer.getTable());
        masterViewer.setCellEditors(editorsA);
        masterViewer.setCellModifier(
                new ICellModifier() {
                    public boolean canModify(Object element, String property) {
                        if (property.equals(PROPS[1])) {
                            return true;
                        }
                        return false;
                    }

                    public Object getValue(Object element, String property) {
                        DataModel model = (DataModel) element;
                        if (property.equals(PROPS[0])) {
                            return model.getKey() == null ? "" : model.getKey();
                        } else if (property.equals(PROPS[1])) {
                            return model.getValue() == null ? "" : model.getValue();
                        }
                        return "";
                    }

                    public void modify(Object element, String property, Object value) {
                        TableItem item = (TableItem) element;
                        DataModel model = (DataModel) item.getData();
                        model.setValue(value.toString());

                        masterViewer.refresh(model);
                        updateHAModel(masterViewer, model, false);
                    }
                });

        TableColumn keyAColumn = new TableColumn(masterViewer.getTable(), SWT.LEFT);
        keyAColumn.setText(Messages.lblKey);
        keyAColumn.setWidth(160);

        TableColumn valueAColumn = new TableColumn(masterViewer.getTable(), SWT.LEFT);
        valueAColumn.setText(Messages.lblValue);
        valueAColumn.setWidth(200);

        Label masterDBLabel = new Label(leftComposite, SWT.None);
        masterDBLabel.setText(Messages.lblDatabase);
        masterDBLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        masterDBCombo = new Combo(leftComposite, SWT.BORDER | SWT.READ_ONLY);
        masterDBCombo.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        masterDBCombo.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        HAModel haModel = getConfigHAWizard().getHaModel();

                        /*Init table data*/
                        initTableData(haModel.getMasterServer(), true);
                        HAServer haServer = haModel.getSlaveServer();
                        initTableData(haServer, false);
                    }
                });

        ToolBar toolBarA = new ToolBar(leftComposite, SWT.None);
        toolBarA.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

        ToolItem addItemA = new ToolItem(toolBarA, SWT.None);
        addItemA.setToolTipText(Messages.itemAddParameter);
        addItemA.setImage(CubridManagerUIPlugin.getImage("/icons/replication/add_param.gif"));
        addItemA.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        addParameter(masterViewer);
                    }
                });

        ToolItem editItemA = new ToolItem(toolBarA, SWT.None);
        editItemA.setToolTipText(Messages.itemEditParameter);
        editItemA.setImage(CubridManagerUIPlugin.getImage("/icons/replication/edit_param.gif"));
        editItemA.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        editParameter(masterViewer);
                    }
                });

        ToolItem dropItemA = new ToolItem(toolBarA, SWT.None);
        dropItemA.setToolTipText(Messages.itemDeleteParameter);
        dropItemA.setImage(CubridManagerUIPlugin.getImage("/icons/replication/delete_param.gif"));
        dropItemA.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        dropParameter(masterViewer);
                    }
                });

        /*Create right widget*/
        Label hostBLabel = new Label(rightComposite, SWT.None);
        hostBLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        hostBLabel.setText(Messages.lblSlave);

        slaveHostLabel = new Label(rightComposite, SWT.None);
        slaveHostLabel.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

        slaveViewer = new TableViewer(rightComposite, SWT.BORDER | SWT.FULL_SELECTION);
        slaveViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, -1));
        slaveViewer.getTable().setLinesVisible(true);
        slaveViewer.getTable().setHeaderVisible(true);
        slaveViewer.setContentProvider(new TableContentProvider());
        slaveViewer.setLabelProvider(new CubridConfTableProvider());
        slaveViewer.setColumnProperties(PROPS);

        CellEditor[] editorsB = new CellEditor[2];
        editorsB[0] = null;
        editorsB[1] = new TextCellEditor(slaveViewer.getTable());
        slaveViewer.setCellEditors(editorsB);
        slaveViewer.setCellModifier(
                new ICellModifier() {
                    public boolean canModify(Object element, String property) {
                        if (property.equals(PROPS[1])) {
                            return true;
                        }
                        return false;
                    }

                    public Object getValue(Object element, String property) {
                        DataModel model = (DataModel) element;
                        if (property.equals(PROPS[0])) {
                            return model.getKey() == null ? "" : model.getKey();
                        } else if (property.equals(PROPS[1])) {
                            return model.getValue() == null ? "" : model.getValue();
                        }
                        return "";
                    }

                    public void modify(Object element, String property, Object value) {
                        TableItem item = (TableItem) element;
                        DataModel model = (DataModel) item.getData();
                        model.setValue(value.toString());
                        slaveViewer.refresh(model);

                        updateHAModel(slaveViewer, model, false);
                    }
                });

        TableColumn keyBColumn = new TableColumn(slaveViewer.getTable(), SWT.LEFT);
        keyBColumn.setText(Messages.lblKey);
        keyBColumn.setWidth(160);

        TableColumn valueBColumn = new TableColumn(slaveViewer.getTable(), SWT.LEFT);
        valueBColumn.setText(Messages.lblValue);
        valueBColumn.setWidth(200);

        ToolBar toolBarB = new ToolBar(rightComposite, SWT.None);
        toolBarB.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 3, 1, -1, -1));

        ToolItem addItemB = new ToolItem(toolBarB, SWT.None);
        addItemB.setToolTipText(Messages.itemAddParameter);
        addItemB.setImage(CubridManagerUIPlugin.getImage("/icons/replication/add_param.gif"));
        addItemB.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        addParameter(slaveViewer);
                    }
                });

        ToolItem editItemB = new ToolItem(toolBarB, SWT.None);
        editItemB.setToolTipText(Messages.itemEditParameter);
        editItemB.setImage(CubridManagerUIPlugin.getImage("/icons/replication/edit_param.gif"));
        editItemB.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        editParameter(slaveViewer);
                    }
                });

        ToolItem dropItemB = new ToolItem(toolBarB, SWT.None);
        dropItemB.setToolTipText(Messages.itemDeleteParameter);
        dropItemB.setImage(CubridManagerUIPlugin.getImage("/icons/replication/delete_param.gif"));
        dropItemB.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        dropParameter(slaveViewer);
                    }
                });
    }

    /** Init data */
    private void init() {
        HAModel haModel = getConfigHAWizard().getHaModel();

        /*init widget*/
        masterHostLabel.setText(haModel.getMasterServer().getServer().getServerName());
        slaveHostLabel.setText(haModel.getSlaveServer().getServer().getServerName());

        masterDBCombo.removeAll();
        masterDBCombo.add(haModel.getSelectedDB());
        masterDBCombo.add(CubridConfParaConstants.COMMON_SECTION_NAME);
        masterDBCombo.add(CubridConfParaConstants.SERVICE_SECTION_NAME);
        masterDBCombo.select(0);

        /*Init table data*/
        if (haModel.getMasterServer().getCubridParameters() != null) {
            initTableData(haModel.getMasterServer(), true);
        }

        if (haModel.getSlaveServer().getCubridParameters() != null) {
            initTableData(haModel.getSlaveServer(), false);
        }
    }

    /**
     * Init table data
     *
     * @param haServer
     * @param isMaster
     */
    private void initTableData(HAServer haServer, boolean isMaster) {
        String dbName = masterDBCombo.getText();
        String sectionName = null;
        if (CubridConfParaConstants.COMMON_SECTION_NAME.equals(dbName)
                || CubridConfParaConstants.SERVICE_SECTION_NAME.equals(dbName)) {
            sectionName = dbName;
        } else {
            sectionName = getSectionName(dbName);
        }

        Map<String, Map<String, String>> cubridConfMap = haServer.getCubridParameters();
        Map<String, String> dataMap = (Map<String, String>) cubridConfMap.get(sectionName);

        /*Init table data*/
        List<DataModel> list = new ArrayList<DataModel>();
        for (String key : dataMap.keySet()) {
            String value = dataMap.get(key);
            list.add(new DataModel(key, value));
        }

        if (isMaster) {
            masterViewer.setInput(list);
            masterViewer.refresh();
        } else {
            slaveViewer.setInput(list);
            slaveViewer.refresh();
        }
    }

    /** Load the cubrid.conf data for every server */
    private void loadData() {
        HAServer master = getConfigHAWizard().getHaModel().getMasterServer();
        if (master.getCubridParameters() == null) {
            loadServerData(master);
        }

        HAServer slave = getConfigHAWizard().getHaModel().getSlaveServer();
        if (slave.getCubridParameters() == null) {
            loadServerData(slave);
        }
    }

    /**
     * Load server data
     *
     * @param haServer
     */
    private void loadServerData(HAServer haServer) {

        CommonTaskExec taskExcutor =
                new CommonTaskExec(
                        com.cubrid.cubridmanager.ui.host.Messages.getCubridConfTaskRunning);
        GetCubridConfParameterTask getCubridConfParameterTask =
                new GetCubridConfParameterTask(haServer.getServer().getServerInfo());
        taskExcutor.addTask(getCubridConfParameterTask);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            Map<String, Map<String, String>> allConfigMap =
                    getCubridConfParameterTask.getConfParameters();
            haServer.setOriginCubridParameters(allConfigMap);

            haServer.setCubridParameters(getConfigHAWizard().cloneParameters(allConfigMap));
            appendDefaultHAConfig(haServer.getCubridParameters());
        } else {
            CommonUITool.openErrorBox(Messages.errLoadCubridConf);
        }
    }

    /**
     * Append default parameters
     *
     * @param haServer
     */
    private void appendDefaultHAConfig(Map<String, Map<String, String>> cubridConfigMap) {
        HAModel haModel = getConfigHAWizard().getHaModel();

        /*Append common data*/
        Map<String, String> commonMap =
                cubridConfigMap.get(CubridConfParaConstants.COMMON_SECTION_NAME);
        if (commonMap == null) {
            commonMap = new HashMap<String, String>();
            cubridConfigMap.put(CubridConfParaConstants.COMMON_SECTION_NAME, commonMap);
        }
        commonMap.put(HAConfParaConstants.HA_MODE, "on");

        /*Append database data*/
        String nodeName = getSectionName(haModel.getSelectedDB());
        Map<String, String> dataMap = cubridConfigMap.get(nodeName);
        if (dataMap == null) {
            dataMap = new HashMap<String, String>();
            cubridConfigMap.put(nodeName, dataMap);
        }
        dataMap.put(HAConfParaConstants.HA_MODE, "on");
        if (!dataMap.containsKey(HAConfParaConstants.LOG_MAX_ARCHIVES)) {
            dataMap.put(HAConfParaConstants.LOG_MAX_ARCHIVES, "100");
        }
        if (!dataMap.containsKey(CubridConfParaConstants.FORCE_REMOVE_LOG_ARCHIVES)) {
            dataMap.put(CubridConfParaConstants.FORCE_REMOVE_LOG_ARCHIVES, "no");
        }
    }

    private String getSectionName(String dbName) {
        StringBuilder sb = new StringBuilder();
        sb.append("[@").append(dbName).append("]");
        return sb.toString();
    }

    private boolean validate() {
        setErrorMessage(null);
        setPageComplete(false);

        setPageComplete(true);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void addParameter(TableViewer tableViewer) {
        DataModel model = new DataModel("", "");
        EditParameterDialog dialog = new EditParameterDialog(getShell(), model);
        if (IDialogConstants.OK_ID == dialog.open()) {
            List<DataModel> list = (List<DataModel>) tableViewer.getInput();
            list.add(model);
            tableViewer.refresh();

            updateHAModel(tableViewer, model, false);
        }
    }

    @SuppressWarnings("unchecked")
    private void editParameter(TableViewer tableViewer) {
        int[] indeices = tableViewer.getTable().getSelectionIndices();
        if (indeices.length > 0) {
            List<DataModel> list = (List<DataModel>) tableViewer.getInput();
            DataModel model = list.get(indeices[0]);
            EditParameterDialog dialog = new EditParameterDialog(getShell(), model);
            if (IDialogConstants.OK_ID == dialog.open()) {
                tableViewer.refresh();
                updateHAModel(tableViewer, model, false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void dropParameter(TableViewer tableViewer) {
        List<DataModel> list = (List<DataModel>) tableViewer.getInput();
        int[] indeices = tableViewer.getTable().getSelectionIndices();
        if (indeices.length > 0) {
            DataModel model = list.get(indeices[0]);
            if (CommonUITool.openConfirmBox(
                    Messages.bind(Messages.msgConfirmDropParameter, model.getKey()))) {
                list.remove(indeices[0]);
                updateHAModel(tableViewer, model, true);
            }
        }

        tableViewer.refresh();
    }

    private void updateHAModel(TableViewer tableViewer, DataModel model, boolean isDelete) {
        /*Update HAModel data*/
        HAServer haServer = null;

        if (tableViewer == masterViewer) {
            haServer = getConfigHAWizard().getHaModel().getMasterServer();
        } else {
            haServer = getConfigHAWizard().getHaModel().getSlaveServer();
        }

        String dbName = masterDBCombo.getText();
        String sectionName = null;
        if (CubridConfParaConstants.COMMON_SECTION_NAME.equals(dbName)
                || CubridConfParaConstants.SERVICE_SECTION_NAME.equals(dbName)) {
            sectionName = dbName;
        } else {
            sectionName = getSectionName(dbName);
        }

        Map<String, String> dataMap = haServer.getCubridParameters().get(sectionName);

        if (dataMap != null) {
            if (isDelete) {
                dataMap.remove(model.getKey());
            } else {
                dataMap.put(model.getKey(), model.getValue());
            }
        }
    }

    private ConfigHAWizard getConfigHAWizard() {
        return (ConfigHAWizard) getWizard();
    }

    protected void handlePageLeaving(PageChangingEvent event) {
        if (!validate()) {
            event.doit = false;
            return;
        }
    }

    protected void handlePageShowing(PageChangingEvent event) {
        loadData();
        init();
        setTitle(Messages.haStep2);
    }
}
