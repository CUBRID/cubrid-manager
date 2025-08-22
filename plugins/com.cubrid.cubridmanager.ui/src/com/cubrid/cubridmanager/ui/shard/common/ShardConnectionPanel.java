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
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.shard.model.ShardConnection;
import com.cubrid.cubridmanager.ui.shard.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A panel that shows a shard connection info.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-3
 */
public class ShardConnectionPanel extends AbstractModulePanel<ShardConnection> {

    public ShardConnectionPanel(ModifyListener listener, ShardConnection module) {
        super(listener, module);
    }

    private String[] columnNameArrs;
    private Table connectionTable;
    private TableViewer connectionTableViewer;
    private Text connectionFileNameText;
    private ToolBar topLeftToolBar;
    private ToolItem insertRecordItem;

    private ToolItem delRecordItem;

    private List<Map<String, String>> parameterList;

    public void build(Composite parent) {
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        parent.setLayout(layout);

        Label connectionFileNameLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
        connectionFileNameLabel.setText(Messages.shardConnnectionFileName);
        gridData = new GridData();
        gridData.widthHint = 200;
        gridData.horizontalSpan = 2;
        connectionFileNameLabel.setLayoutData(gridData);

        connectionFileNameText = new Text(parent, SWT.BORDER);
        connectionFileNameText.setTextLimit(32);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        connectionFileNameText.setLayoutData(gridData);
        connectionFileNameText.setEnabled(false);

        topLeftToolBar = new ToolBar(parent, SWT.FLAT);
        insertRecordItem = new ToolItem(topLeftToolBar, SWT.PUSH);
        insertRecordItem.setToolTipText(Messages.addConnection);
        insertRecordItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_insert.png"));
        insertRecordItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_insert_disabled.png"));
        insertRecordItem.setEnabled(true);

        delRecordItem = new ToolItem(topLeftToolBar, SWT.PUSH);
        delRecordItem.setToolTipText(Messages.deleteConnection);
        delRecordItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete.png"));
        delRecordItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete_disabled.png"));
        delRecordItem.setEnabled(false);

        columnNameArrs =
                new String[] {Messages.tblShardId, Messages.tblDBName, Messages.tblConInfo};
        int[] columnwidthArr = new int[] {100, 180, 300};
        connectionTableViewer =
                CommonUITool.createCommonTableViewer(
                        parent,
                        null,
                        columnNameArrs,
                        columnwidthArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 4, 1, -1, 320));
        connectionTable = connectionTableViewer.getTable();

        load();
        initial();
    }

    public void load() {
        connectionFileNameText.setText(this.module.getFieName());
        initParameterList();
        connectionTableViewer.setInput(parameterList);
    }

    /** Reload connection file name */
    public void reloadFileName() {
        connectionFileNameText.setText(this.module.getFieName());
    }

    /**
     * add a new item into table result
     *
     * @return a new added TableItem
     */
    private void addNewItem() {
        /* Get Max index */
        int maxIndex = -1;
        Item[] allItems = connectionTable.getItems();
        for (Item it : allItems) {
            if (it.getText() != null) {
                int index = StringUtil.intValue(it.getText(), -1);
                if (index > maxIndex) {
                    maxIndex = index;
                }
            }
        }
        maxIndex++;

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("0", String.valueOf(maxIndex));
        dataMap.put("1", "");
        dataMap.put("2", "");
        parameterList.add(dataMap);
        connectionTableViewer.refresh();
        setTableColor();
    }

    private void setTableColor() {
        for (int i = 0; i < connectionTable.getItemCount(); i++) {
            connectionTable.getItem(i).setBackground(ResourceManager.getColor(230, 230, 230));
        }
    }

    /**
     * delete the record
     *
     * @param selection TableItem[]
     */
    private void deleteRecord() {
        TableItem[] selection = connectionTable.getSelection();
        for (TableItem item : selection) {
            parameterList.remove(item.getData());
        }
        connectionTableViewer.refresh();
        // Notice the upper layer
        modifyListener.modifyText(null);
    }

    private void initial() {
        insertRecordItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {

                        addNewItem();
                    }
                });
        delRecordItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        connectionTable.forceFocus();
                        deleteRecord();
                    }
                });
        connectionTableViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {

                    public void selectionChanged(SelectionChangedEvent arg0) {
                        int selectedItemCount = connectionTable.getSelectionCount();
                        if (selectedItemCount > 0) {
                            delRecordItem.setEnabled(true);
                        } else {
                            delRecordItem.setEnabled(false);
                        }
                    }
                });
        setTableColor();
        linkEditorForTable();
    }

    /**
     * getParameterList
     *
     * @return Parameter List
     */
    private void initParameterList() {
        parameterList = new ArrayList<Map<String, String>>();
        List<String> cons = this.module.getConnections();
        for (String con : cons) {
            String[] tmp = con.split(",");
            String shardId = tmp[0];
            String dbName = tmp[1];
            String conInfo = tmp[2];
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("0", shardId);
            dataMap.put("1", dbName);
            dataMap.put("2", conInfo);
            parameterList.add(dataMap);
        }
    }

    /** Links the editable column of table */
    private void linkEditorForTable() {
        connectionTableViewer.setColumnProperties(columnNameArrs);
        CellEditor[] editors = new CellEditor[3];
        editors[0] = new TextCellEditor(connectionTable);
        editors[1] = new TextCellEditor(connectionTable);
        editors[2] = new TextCellEditor(connectionTable);
        connectionTableViewer.setCellEditors(editors);
        connectionTableViewer.setCellModifier(
                new ICellModifier() {

                    public boolean canModify(Object element, String property) {
                        return true;
                    }

                    @SuppressWarnings("unchecked")
                    public Object getValue(Object element, String property) {
                        Map<String, String> map = (Map<String, String>) element;
                        if (property.equals(columnNameArrs[0])) {
                            return map.get("0");
                        } else if (property.equals(columnNameArrs[1])) {
                            return map.get("1");
                        } else if (property.equals(columnNameArrs[2])) {
                            return map.get("2");
                        }
                        return null;
                    }

                    public void modify(Object element, String property, Object value) {
                        Object obj = null;
                        if (element instanceof Item) {
                            obj = ((Item) element).getData();
                        }
                        if (obj == null) {
                            return;
                        }
                        @SuppressWarnings("unchecked")
                        Map<String, String> map = (Map<String, String>) obj;
                        boolean isValid = true;
                        if (property.equals(columnNameArrs[0])) {
                            isValid = ValidateUtil.isInteger(value.toString());
                            if (isValid) {
                                for (Map<String, String> paras : parameterList) {
                                    if (paras == obj) {
                                        continue;
                                    }
                                    if (paras.get("0").equals(value)) {
                                        isValid = false;
                                        CommonUITool.openErrorBox(
                                                Messages.bind(
                                                        Messages.errShardIdExist,
                                                        new Object[] {value.toString()}));
                                        break;
                                    }
                                }
                            } else {
                                CommonUITool.openErrorBox(Messages.errShardIdNotNumeric);
                            }
                            if (isValid) {
                                map.put("0", value.toString());
                            }
                        } else if (property.equals(columnNameArrs[1])) {
                            map.put("1", value.toString());
                        } else if (property.equals(columnNameArrs[2])) {
                            map.put("2", value.toString());
                        }

                        connectionTableViewer.refresh();

                        // Notice the upper layer
                        modifyListener.modifyText(null);
                    }
                });
    }

    public Map<String, String> valid() {
        boolean tag = true;
        String message = "";
        if (parameterList.isEmpty()) {
            tag = false;
            message = Messages.errShardconnectionEmpty;
            return MessageUtil.generateResult(tag, message);
        }
        for (int i = 0; i < parameterList.size(); i++) {
            Map<String, String> dataMap = parameterList.get(i);
            if (StringUtil.isEmpty(dataMap.get("0"))
                    || StringUtil.isEmpty(dataMap.get("1"))
                    || StringUtil.isEmpty(dataMap.get("2"))) {
                tag = false;
                message = Messages.errShardConnectionParameterEmpty;
                return MessageUtil.generateResult(tag, message);
            }
        }

        return MessageUtil.generateResult(tag, message);
    }

    public void save() {
        module.getConnections().clear();
        for (Map<String, String> para : parameterList) {
            String shardId = para.get("0");
            String dbName = para.get("1");
            String conInfo = para.get("2");

            dbName = StringUtil.isEmpty(dbName) ? " " : dbName;
            conInfo = StringUtil.isEmpty(conInfo) ? " " : conInfo;
            String connection = shardId + "," + dbName + "," + conInfo;
            module.addConnection(connection);
        }
    }
}
