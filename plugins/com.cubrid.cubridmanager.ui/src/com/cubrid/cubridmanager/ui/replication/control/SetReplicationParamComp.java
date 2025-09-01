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
package com.cubrid.cubridmanager.ui.replication.control;

import com.cubrid.common.ui.spi.dialog.IUpdatable;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamConstants;
import com.cubrid.cubridmanager.ui.replication.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;

/**
 * This composite provide a replication parameter editor which implement by table viewer
 *
 * @author pangqiren
 * @version 1.0 - 2009-11-27 created by pangqiren
 */
public class SetReplicationParamComp {
    private String[] columnNameArrs;
    private TableViewer replicationParamTableViewer;
    private Table replicationParamTable;
    private final List<Map<String, String>> replicationParamList =
            new ArrayList<Map<String, String>>();
    private String errorMsg = null;
    private final IUpdatable updatableComp;

    /**
     * The constructor
     *
     * @param composite
     */
    public SetReplicationParamComp(IUpdatable composite) {
        updatableComp = composite;
    }

    /**
     * Create replication parameter editor composite
     *
     * @param parent Composite
     * @return replParaGroup
     */
    public Control createReplicationParamComp(Composite parent) {
        Group replParaGroup = new Group(parent, SWT.NONE);
        replParaGroup.setText(Messages.grpReplParaSetting);
        replParaGroup.setLayout(new GridLayout());
        replParaGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        columnNameArrs =
                new String[] {
                    Messages.repparm0tblColumnParameterName,
                    Messages.repparm0tblColumnValueType,
                    Messages.repparm0tblColumnParameterValue
                };
        replicationParamTableViewer =
                CommonUITool.createCommonTableViewer(
                        replParaGroup,
                        null,
                        columnNameArrs,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 200));
        replicationParamTable = replicationParamTableViewer.getTable();

        TableLayout tlayout = new TableLayout();
        tlayout.addColumnData(new ColumnWeightData(50, 50, true));
        tlayout.addColumnData(new ColumnWeightData(50, 50, true));
        tlayout.addColumnData(new ColumnWeightData(50, 50, true));
        replicationParamTable.setLayout(tlayout);

        linkEditorForTable();

        replicationParamTableViewer.setInput(replicationParamList);
        for (int i = 0; i < replicationParamTable.getColumnCount(); i++) {
            replicationParamTable.getColumn(i).pack();
        }
        return replParaGroup;
    }

    /** check & update table content of being edited */
    private void linkEditorForTable() {
        replicationParamTableViewer.setColumnProperties(columnNameArrs);
        CellEditor[] editors = new CellEditor[3];
        editors[0] = null;
        editors[1] = null;
        editors[2] = new TextCellEditor(replicationParamTable);

        replicationParamTableViewer.setCellEditors(editors);
        replicationParamTableViewer.setCellModifier(
                new ICellModifier() {
                    public boolean canModify(Object element, String property) {
                        if (property.equals(columnNameArrs[2])) {
                            return true;
                        }
                        return false;
                    }

                    @SuppressWarnings("unchecked")
                    public Object getValue(Object element, String property) {
                        Map<String, String> map = (Map<String, String>) element;
                        if (property.equals(columnNameArrs[2])) {
                            return map.get("2");
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
                        String parameter = map.get("0");
                        String type = map.get("1");
                        String paraValue = map.get("2");
                        boolean isValid = true;
                        String errorMsg = null;
                        if (type.startsWith("bool")) {
                            if (!value.toString().equalsIgnoreCase("Y")
                                    && !value.toString().equalsIgnoreCase("N")) {
                                isValid = false;
                                errorMsg =
                                        Messages.bind(
                                                Messages.repparm0errYesNoParameter,
                                                new String[] {parameter});
                            }
                        } else if (type.startsWith("int")) {
                            String paraVal = value.toString();
                            if (paraVal.length() > 8 || !paraVal.matches("\\d+")) {
                                isValid = false;
                            }
                            if (isValid) {
                                int intValue = Integer.parseInt(paraVal);
                                if (parameter.equals(
                                        ReplicationParamConstants.PERF_POLL_INTERVAL)) {
                                    if (intValue < 10 || intValue > 60) {
                                        isValid = false;
                                        errorMsg =
                                                Messages.bind(
                                                        Messages.repparm0errPerfPollInterval,
                                                        new String[] {parameter});
                                    }
                                } else if (parameter.equals(
                                        ReplicationParamConstants.SIZE_OF_LOG_BUFFER)) {
                                    if (intValue < 100 || intValue > 1000) {
                                        isValid = false;
                                        errorMsg =
                                                Messages.bind(
                                                        Messages.repparm0errSizeOfLogBuffer,
                                                        new String[] {parameter});
                                    }
                                } else if (parameter.equals(
                                        ReplicationParamConstants.SIZE_OF_CACHE_BUFFER)) {
                                    if (intValue < 100 || intValue > 500) {
                                        isValid = false;
                                        errorMsg =
                                                Messages.bind(
                                                        Messages.repparm0errSizeOfCacheBuffer,
                                                        new String[] {parameter});
                                    }
                                } else if (parameter.equals(
                                        ReplicationParamConstants.SIZE_OF_COPYLOG)) {
                                    if (intValue < 1000 || intValue > 10000) {
                                        isValid = false;
                                        errorMsg =
                                                Messages.bind(
                                                        Messages.repparm0errSizeOfCopylog,
                                                        new String[] {parameter});
                                    }
                                } else if (parameter.equals(
                                        ReplicationParamConstants.LOG_APPLY_INTERVAL)) {
                                    if (intValue < 0 || intValue > 600) {
                                        isValid = false;
                                        errorMsg =
                                                Messages.bind(
                                                        Messages.repparm0errLogApplyInterval,
                                                        new String[] {parameter});
                                    }
                                } else if (parameter.equals(
                                                ReplicationParamConstants.RESTART_INTERVAL)
                                        && (intValue < 1 || intValue > 60)) {
                                    isValid = false;
                                    errorMsg =
                                            Messages.bind(
                                                    Messages.repparm0errRestartInterval,
                                                    new String[] {parameter});
                                }
                            } else {
                                errorMsg =
                                        Messages.bind(
                                                Messages.repparm0errOnlyInteger,
                                                new String[] {parameter});
                                isValid = false;
                            }
                        }

                        if (!isValid && errorMsg != null) {
                            setErrorMsg(errorMsg);
                            updatableComp.updateUI();
                        }
                        setErrorMsg(null);
                        if (isValid
                                && property.equals(columnNameArrs[2])
                                && !paraValue.equals(value)) {
                            map.put("2", value.toString());
                            updatableComp.updateUI();
                        }
                        for (int i = 0; i < replicationParamTable.getColumnCount(); i++) {
                            replicationParamTable.getColumn(i).pack();
                        }
                        replicationParamTableViewer.refresh();
                    }
                });
    }

    /**
     * When input the invalid value in replication parameter editor table,call this method to change
     * UI
     *
     * @param errorMsg String
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * Get the errror msg When input the invalid value in replication parameter editor table
     *
     * @return errorMsg
     */
    public String getErrorMsg() {
        return this.errorMsg;
    }

    /** Initial the replication parameter table viewer */
    public void init() {
        replicationParamList.clear();
        String replicationParameters[][] = ReplicationParamConstants.getReplicationParameters();
        for (int i = 0; i < replicationParameters.length; i++) {
            Map<String, String> dataMap = new HashMap<String, String>();
            String para = replicationParameters[i][0];
            String valueType = replicationParameters[i][1];
            String value = replicationParameters[i][2];
            dataMap.put("0", para);
            dataMap.put("1", valueType);
            dataMap.put("2", value);
            replicationParamList.add(dataMap);
        }
        for (int i = 0; i < replicationParamTable.getColumnCount(); i++) {
            replicationParamTable.getColumn(i).pack();
        }
        replicationParamTableViewer.refresh();
    }

    /**
     * Set replication parameter map and refresh parameter editor
     *
     * @param paramMap Map<String, String>
     */
    public void setReplicationParamMap(Map<String, String> paramMap) {
        if (paramMap == null) {
            return;
        }
        replicationParamList.clear();
        String replicationParameters[][] = ReplicationParamConstants.getReplicationParameters();
        for (int i = 0; i < replicationParameters.length; i++) {
            Map<String, String> dataMap = new HashMap<String, String>();
            String para = replicationParameters[i][0];
            String valueType = replicationParameters[i][1];
            String value = paramMap.get(para);
            dataMap.put("0", para);
            dataMap.put("1", valueType);
            dataMap.put("2", value == null ? "" : value);
            replicationParamList.add(dataMap);
        }
        for (int i = 0; i < replicationParamTable.getColumnCount(); i++) {
            replicationParamTable.getColumn(i).pack();
        }
        replicationParamTableViewer.refresh();
    }

    /**
     * Get parameters map
     *
     * @return paramMap
     */
    public Map<String, String> getParamMap() {
        Map<String, String> paramMap = new HashMap<String, String>();
        for (int i = 0; i < replicationParamList.size(); i++) {
            Map<String, String> dataMap = replicationParamList.get(i);
            String para = dataMap.get("0");
            String value = dataMap.get("2");
            paramMap.put(para, value);
        }
        return paramMap;
    }

    /**
     * Disabled parameter editor
     *
     * @param isEditable boolean
     */
    public void setEditable(boolean isEditable) {
        replicationParamTable.setEnabled(isEditable);
    }
}
