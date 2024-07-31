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
package com.cubrid.cubridmanager.ui.shard.common;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.CubridShardConfParaConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.ui.shard.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * A panel that shows a shard broker info.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-3
 */
public class ShardBrokerPropertiesPanel extends AbstractModulePanel<Shard> {

    public ShardBrokerPropertiesPanel(
            ModifyListener listener, Shard module, ServerInfo serverInfo) {
        super(listener, module);
        this.serverInfo = serverInfo;
    }

    private Table paraTable;
    private String[] columnNameArrs;
    private TableViewer paraTableViewer;
    private Text shardNameText;

    private ServerInfo serverInfo;

    private List<Map<String, String>> parameterList;

    public void build(Composite parent) {
        Group generalGroup = new Group(parent, SWT.NONE);
        generalGroup.setText(Messages.shardBrokerInfomation);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        generalGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        generalGroup.setLayout(layout);

        Label shardNameLabel = new Label(generalGroup, SWT.LEFT | SWT.WRAP);
        shardNameLabel.setText(Messages.shardName);
        gridData = new GridData();
        gridData.widthHint = 150;
        shardNameLabel.setLayoutData(gridData);

        shardNameText = new Text(generalGroup, SWT.BORDER);
        shardNameText.setTextLimit(16);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        shardNameText.setLayoutData(gridData);

        columnNameArrs =
                new String[] {Messages.tblParameter, Messages.tblValueType, Messages.tblParamValue};
        int[] columnwidthArr = new int[] {200, 200, 190};
        paraTableViewer =
                CommonUITool.createCommonTableViewer(
                        generalGroup,
                        null,
                        columnNameArrs,
                        columnwidthArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 4, 1, -1, 270));
        paraTable = paraTableViewer.getTable();

        load();
        initial();
    }

    public void load() {
        shardNameText.setText(this.module.getName());
        initParameterList();
        paraTableViewer.setInput(parameterList);
    }

    private void initial() {

        shardNameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent e) {
                        Map<String, String> dataMap = parameterList.get(5);
                        String shardName = shardNameText.getText().trim();
                        dataMap.put("2", shardName + "_connection.txt");
                        dataMap = parameterList.get(6);
                        dataMap.put("2", shardName + "_key.txt");
                        paraTableViewer.refresh();

                        modifyListener.modifyText(e);
                    }
                });

        for (int k = 0; k < paraTable.getItemCount(); k++) {
            String color = parameterList.get(k).get("bgColor");
            if (color.equalsIgnoreCase("grey")) {
                paraTable.getItem(k).setBackground(ResourceManager.getColor(200, 200, 200));
            }
            String para = parameterList.get(k).get("0");
            if (CubridShardConfParaConstants.SHARD_CONNECTION_FILE.equals(para)
                    || CubridShardConfParaConstants.SHARD_KEY_FILE.equals(para)
                    || CubridShardConfParaConstants.APPL_SERVER_SHM_ID.equals(para)) {
                paraTable.getItem(k).setBackground(2, ResourceManager.getColor(220, 220, 220));
            }
        }
        linkEditorForTable();
    }

    /**
     * getParameterList
     *
     * @return Parameter List
     */
    private void initParameterList() {
        parameterList = new ArrayList<Map<String, String>>();
        String[][] shardParameters = CubridShardConfParaConstants.getShardParameters();
        for (int i = 2; i < shardParameters.length; i++) {
            Map<String, String> dataMap = new HashMap<String, String>();
            String para = shardParameters[i][0];
            String type = shardParameters[i][1];
            String defaultValue = shardParameters[i][2];
            String paramType = shardParameters[i][3];
            String bgColor = "grey";
            if ("general".equals(paramType)) {
                continue;
            } else if ("common".equals(paramType)) {
                bgColor = "white";
            }

            if (module != null && !"".equals(module.getValue(para))) {
                defaultValue = module.getValue(para);
                bgColor = "white";
            }

            dataMap.put("0", para);
            dataMap.put("1", type);
            dataMap.put("2", defaultValue);
            dataMap.put("bgColor", bgColor);
            parameterList.add(dataMap);
        }
    }

    /** Links the editable column of table */
    private void linkEditorForTable() {
        paraTableViewer.setColumnProperties(columnNameArrs);
        CellEditor[] editors = new CellEditor[3];
        editors[0] = null;
        editors[1] = null;
        editors[2] = new TextCellEditor(paraTable);
        paraTableViewer.setCellEditors(editors);
        paraTableViewer.setCellModifier(
                new ICellModifier() {

                    public boolean canModify(Object element, String property) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> map = (Map<String, String>) element;
                        String para = map.get("0");
                        if (property.equals(columnNameArrs[2])
                                && (!CubridShardConfParaConstants.SHARD_CONNECTION_FILE.equals(para)
                                        && !CubridShardConfParaConstants.SHARD_KEY_FILE.equals(para)
                                        && !CubridShardConfParaConstants.APPL_SERVER_SHM_ID.equals(
                                                para))) {
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
                        String paramName = map.get("0");
                        String type = map.get("1");
                        boolean isValid = true;
                        String valueStr = value.toString().trim();
                        if (!StringUtil.isEmpty(valueStr)) {
                            if (type.indexOf("int") >= 0) {
                                if (!ValidateUtil.isInteger(value.toString())) {
                                    isValid = false;
                                } else {
                                    int intValue = Integer.parseInt(value.toString());
                                    int start = type.indexOf("(");
                                    int end = type.indexOf(")");
                                    if (start > 0) {
                                        String valueRange = type.substring(start + 1, end);
                                        String[] values = valueRange.split("~");
                                        int min = Integer.parseInt(values[0]);
                                        int max = Integer.parseInt(values[1]);
                                        if (intValue < min || intValue > max || intValue < 1) {
                                            isValid = false;
                                        }
                                    }
                                }
                            } else if (type.startsWith("string")) {
                                int start = type.indexOf("(");
                                int end = type.indexOf(")");
                                if (start > 0) {
                                    String valueStrs = type.substring(start + 1, end);
                                    String[] values = valueStrs.split("\\|");
                                    boolean isExist = false;
                                    for (String val : values) {
                                        if (valueStr.equals(val)) {
                                            isExist = true;
                                            break;
                                        }
                                    }
                                    if (!isExist) {
                                        isValid = false;
                                    }
                                }
                            }

                            if (!isValid) {
                                CommonUITool.openErrorBox(
                                        Messages.bind(
                                                Messages.errParameterValue,
                                                new Object[] {paramName}));
                            }

                            if (isValid
                                    && paramName.equalsIgnoreCase(
                                            CubridShardConfParaConstants.MIN_NUM_APPL_SERVER)) {
                                int intValue = Integer.parseInt(value.toString());
                                Map<String, String> dataMap = parameterList.get(9);
                                String maxNumApplServer = dataMap.get("2");
                                if (maxNumApplServer.trim().length() > 0
                                        && intValue > Integer.parseInt(maxNumApplServer.trim())) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(
                                                    Messages.errMinNumApplServerValue,
                                                    new Object[] {paramName}));
                                }
                            }

                            if (isValid
                                    && paramName.equalsIgnoreCase(
                                            CubridShardConfParaConstants.MAX_NUM_APPL_SERVER)) {
                                int intValue = Integer.parseInt(value.toString());
                                Map<String, String> dataMap = parameterList.get(10);
                                String minNumApplServer = dataMap.get("2");
                                if (minNumApplServer.trim().length() > 0
                                        && intValue < Integer.parseInt(minNumApplServer.trim())) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(
                                                    Messages.errMaxNumApplServeValue,
                                                    new Object[] {paramName}));
                                }
                            }

                            if (isValid
                                    && paramName.equalsIgnoreCase(
                                            CubridShardConfParaConstants.BROKER_PORT)) {
                                Map<String, String> dataMap = parameterList.get(8);
                                String metadataShmId = dataMap.get("2");
                                if (valueStr.equals(metadataShmId)) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(
                                                    Messages.errShmIdExist,
                                                    new Object[] {paramName}));
                                }
                                dataMap = parameterList.get(7);
                                dataMap.put("2", value.toString());
                            }

                            if (isValid
                                    && paramName.equalsIgnoreCase(
                                            CubridShardConfParaConstants.METADATA_SHM_ID)) {
                                Map<String, String> dataMap = parameterList.get(7);
                                String metadataShmId = dataMap.get("2");
                                if (valueStr.equals(metadataShmId)) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(
                                                    Messages.errShmIdExist,
                                                    new Object[] {paramName}));
                                }
                            }
                        }
                        if (isValid) {
                            map.put("2", value.toString());
                        }

                        paraTableViewer.refresh();

                        // Notice the upper layer
                        modifyListener.modifyText(null);
                    }
                });
    }

    public Map<String, String> valid() {
        boolean tag = true;
        String message = "";

        String shardName = shardNameText.getText();
        if (StringUtil.isEmpty(shardName)) {
            tag = false;
            message = Messages.errShardNameEmpty;
            return MessageUtil.generateResult(tag, message);
        }
        if (serverInfo.getShards().checkShardNameConflicts(module, shardName)) {
            tag = false;
            message = Messages.errConflictName;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> parameterList =
                (List<Map<String, String>>) paraTableViewer.getInput();
        for (int i = 0; i < parameterList.size(); i++) {
            Map<String, String> dataMap = parameterList.get(i);
            if (StringUtil.isEmpty(dataMap.get("2"))) {
                tag = false;
                message = Messages.errShardBrokerParameterEmpty;
                return MessageUtil.generateResult(tag, message);
            }
            // check for conflicts: port, shm_id
            if (dataMap.get("0").indexOf("_PORT") > -1) {
                if (serverInfo.checkBrokerPortConflicts(dataMap.get("2"))) {
                    tag = false;
                    message = Messages.errConflictPort;
                    return MessageUtil.generateResult(tag, message);
                }
                if (serverInfo.getShards().checkPortConflicts(module, dataMap.get("2"))) {
                    tag = false;
                    message = Messages.errConflictPort;
                    return MessageUtil.generateResult(tag, message);
                }
            } else if (dataMap.get("0").indexOf("_SHM_ID") > -1) {
                if (serverInfo.checkBrokerShmIdConflicts(dataMap.get("2"))) {
                    tag = false;
                    message = Messages.errConflictShmId;
                    return MessageUtil.generateResult(tag, message);
                }
                if (serverInfo.getShards().checkShmIdConflicts(module, dataMap.get("2"))) {
                    tag = false;
                    message = Messages.errConflictPort;
                    return MessageUtil.generateResult(tag, message);
                }
            }
        }

        return MessageUtil.generateResult(tag, message);
    }

    public void save() {
        String shardName = shardNameText.getText().trim();
        module.setName(shardName);
        module.getShardConnectionFile().setFieName(shardName + "_connection.txt");
        module.getShardKeysFile().setFieName(shardName + "_key.txt");

        for (Map<String, String> para : parameterList) {
            String paramName = para.get("0");
            String paramValue = para.get("2");
            module.setValue(paramName, paramValue);
        }
    }
}
