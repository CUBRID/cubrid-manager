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
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.ShardKey;
import com.cubrid.cubridmanager.ui.shard.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A panel that shows a shard keys info.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-3
 */
public class ShardKeysPanel extends AbstractModulePanel<Shard> {

    public ShardKeysPanel(ModifyListener listener, Shard module) {
        super(listener, module);
    }

    private Text keysFileNameText;
    private Group keyInfoGroup;

    private String[] keyListColumnNameArrs;
    private Table keyListTable;
    private TableViewer keyListTableViewer;
    private ToolBar keyListToolBar;
    private ToolItem insertKeyItem;
    private ToolItem deletekeyItem;

    private String[] keyInfoColumnNameArrs;
    private Table keyInfoTable;
    private TableViewer keyInfoTableViewer;
    private ToolBar keyInfoToolBar;
    private ToolItem insertKeyInfoItem;
    private ToolItem deletekeyInfoItem;
    private ComboBoxCellEditor shardIdCellEditor;

    private List<Map<String, String>> keyList;
    private List<Map<String, String>> keyInfoList = new ArrayList<Map<String, String>>();
    private ShardKey currentShardKey = new ShardKey();

    private List<String> shardIdList = new ArrayList<String>();;

    public void build(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);

        Label connectionFileNameLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
        connectionFileNameLabel.setText(Messages.shardKeyFileName);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData = new GridData();
        gridData.widthHint = 200;
        gridData.horizontalSpan = 1;
        connectionFileNameLabel.setLayoutData(gridData);

        keysFileNameText = new Text(parent, SWT.BORDER);
        keysFileNameText.setTextLimit(32);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        keysFileNameText.setLayoutData(gridData);
        keysFileNameText.setEnabled(false);

        buildKeyList(parent);
        buildKeyInfo(parent);

        load();
        initial();
    }

    private void buildKeyList(Composite parent) {
        Group keyListGroup = new Group(parent, SWT.NONE);
        keyListGroup.setText(Messages.shardKeyList);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        keyListGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        keyListGroup.setLayout(layout);

        keyListToolBar = new ToolBar(keyListGroup, SWT.FLAT);
        insertKeyItem = new ToolItem(keyListToolBar, SWT.PUSH);
        insertKeyItem.setToolTipText(Messages.AddKey);
        insertKeyItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_insert.png"));
        insertKeyItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_insert_disabled.png"));
        insertKeyItem.setEnabled(true);

        deletekeyItem = new ToolItem(keyListToolBar, SWT.PUSH);
        deletekeyItem.setToolTipText(Messages.deleteKey);
        deletekeyItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete.png"));
        deletekeyItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete_disabled.png"));
        deletekeyItem.setEnabled(false);

        keyListColumnNameArrs = new String[] {Messages.tblKeyColumnName};
        int[] columnwidthArr = new int[] {180};
        keyListTableViewer =
                CommonUITool.createCommonTableViewer(
                        keyListGroup,
                        null,
                        keyListColumnNameArrs,
                        columnwidthArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 320));
        keyListTable = keyListTableViewer.getTable();
    }

    private void buildKeyInfo(Composite parent) {
        keyInfoGroup = new Group(parent, SWT.NONE);
        keyInfoGroup.setText(Messages.shardKeyInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        keyInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        keyInfoGroup.setLayout(layout);

        keyInfoToolBar = new ToolBar(keyInfoGroup, SWT.FLAT);

        insertKeyInfoItem = new ToolItem(keyInfoToolBar, SWT.PUSH);
        insertKeyInfoItem.setToolTipText(Messages.addData);
        insertKeyInfoItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_insert.png"));
        insertKeyInfoItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_insert_disabled.png"));
        insertKeyInfoItem.setEnabled(false);

        deletekeyInfoItem = new ToolItem(keyInfoToolBar, SWT.PUSH);
        deletekeyInfoItem.setToolTipText(Messages.deleteData);
        deletekeyInfoItem.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete.png"));
        deletekeyInfoItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/table_record_delete_disabled.png"));
        deletekeyInfoItem.setEnabled(false);

        keyInfoColumnNameArrs =
                new String[] {Messages.tblMin, Messages.tblMax, Messages.tblShardId};
        int[] columnwidthArr = new int[] {100, 100, 200};
        keyInfoTableViewer =
                CommonUITool.createCommonTableViewer(
                        keyInfoGroup,
                        null,
                        keyInfoColumnNameArrs,
                        columnwidthArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 320));
        keyInfoTable = keyInfoTableViewer.getTable();
    }

    public void load() {
        keysFileNameText.setText(this.module.getShardKeysFile().getFieName());

        initKeyList();
        keyListTableViewer.setInput(keyList);

        initKeyInfoList();
        keyInfoTableViewer.setInput(keyInfoList);
    }

    /** Reload keys file name. */
    public void reloadFileName() {
        keysFileNameText.setText(this.module.getShardKeysFile().getFieName());
    }

    /** Reload shard id list. */
    public void reloadShardIdList() {
        this.shardIdList.clear();
        for (String con : this.module.getShardConnectionFile().getConnections()) {
            this.shardIdList.add(con.split(",")[0]);
        }
        String[] shardIds = new String[shardIdList.size()];
        shardIds = shardIdList.toArray(shardIds);
        shardIdCellEditor.setItems(shardIds);
    }

    private void initial() {
        insertKeyItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addNewKey();
                    }
                });
        deletekeyItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        keyListTable.forceFocus();
                        deleteKey();
                    }
                });
        keyListTableViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    @SuppressWarnings("unchecked")
                    public void selectionChanged(SelectionChangedEvent arg0) {
                        int selectedItemCount = keyListTable.getSelectionCount();
                        if (selectedItemCount > 0) {
                            deletekeyItem.setEnabled(true);
                        } else {
                            deletekeyItem.setEnabled(false);
                        }
                        if (selectedItemCount == 1) {
                            String keyName =
                                    ((Map<String, String>) keyListTable.getSelection()[0].getData())
                                            .get("0");
                            keyInfoGroup.setText("Key Info: " + keyName);
                            insertKeyInfoItem.setEnabled(true);

                            saveShardKey();

                            currentShardKey = module.getShardKeysFile().getKey(keyName);
                            initKeyInfoList();
                            keyInfoTableViewer.refresh();
                            setKeyInfoColor();
                        } else {
                            insertKeyInfoItem.setEnabled(false);
                        }
                    }
                });
        setKeyListColor();
        linkEditorForKeyListTable();

        insertKeyInfoItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addNewKeyInfo();
                    }
                });
        deletekeyInfoItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        keyInfoTable.forceFocus();
                        deleteKeyInfo();
                    }
                });
        keyInfoTableViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent arg0) {
                        int selectedItemCount = keyInfoTable.getSelectionCount();
                        if (selectedItemCount > 0) {
                            deletekeyInfoItem.setEnabled(true);
                        } else {
                            deletekeyInfoItem.setEnabled(false);
                        }
                    }
                });
        setKeyInfoColor();
        linkEditorForKeyInfoTable();
    }

    private void setKeyListColor() {
        for (int i = 0; i < keyListTable.getItemCount(); i++) {
            keyListTable.getItem(i).setBackground(ResourceManager.getColor(230, 230, 230));
        }
    }

    private void setKeyInfoColor() {
        for (int i = 0; i < keyInfoTable.getItemCount(); i++) {
            keyInfoTable.getItem(i).setBackground(ResourceManager.getColor(230, 230, 230));
        }
    }

    /** add a new item into table */
    private void addNewKey() {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("0", "");
        keyList.add(dataMap);
        keyListTableViewer.refresh();
        setKeyListColor();

        ShardKey key = new ShardKey();
        key.setName("");
        this.module.getShardKeysFile().addKey(key);
        currentShardKey = key;
        initKeyInfoList();
        keyInfoTableViewer.refresh();
    }

    /** delete the record */
    private void deleteKey() {
        TableItem[] selection = keyListTable.getSelection();
        for (TableItem item : selection) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) item.getData();
            this.module.getShardKeysFile().removeKey(data.get("0"));
            keyList.remove(data);
        }
        keyListTableViewer.refresh();

        currentShardKey = new ShardKey();
        initKeyInfoList();
        keyInfoTableViewer.refresh();
        // Notice the upper layer
        modifyListener.modifyText(null);
    }

    /** add a new item into table */
    private void addNewKeyInfo() {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("0", "");
        dataMap.put("1", "");
        dataMap.put("2", "");
        keyInfoList.add(dataMap);
        keyInfoTableViewer.refresh();
        setKeyInfoColor();
    }

    /** delete the record */
    private void deleteKeyInfo() {
        TableItem[] selection = keyInfoTable.getSelection();
        for (TableItem item : selection) {
            keyInfoList.remove(item.getData());
        }
        keyInfoTableViewer.refresh();
        saveShardKey();
        // Notice the upper layer
        modifyListener.modifyText(null);
    }

    private void initKeyList() {
        keyList = new ArrayList<Map<String, String>>();
        List<ShardKey> keys = this.module.getShardKeysFile().getKeys();
        for (ShardKey key : keys) {
            String keyName = key.getName();
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("0", keyName);
            keyList.add(dataMap);
        }
    }

    private void initKeyInfoList() {
        keyInfoList.clear();
        List<String> sections = this.currentShardKey.getSections();
        for (String section : sections) {
            String[] tmp = section.split(",");
            String min = tmp[0];
            String max = tmp[1];
            String shardId = tmp[2];
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("0", min);
            dataMap.put("1", max);
            dataMap.put("2", shardId);
            keyInfoList.add(dataMap);
        }
    }

    /** Links the editable column of table */
    private void linkEditorForKeyListTable() {
        keyListTableViewer.setColumnProperties(keyListColumnNameArrs);
        CellEditor[] editors = new CellEditor[3];
        editors[0] = new TextCellEditor(keyListTable);
        keyListTableViewer.setCellEditors(editors);
        keyListTableViewer.setCellModifier(
                new ICellModifier() {

                    public boolean canModify(Object element, String property) {
                        return true;
                    }

                    @SuppressWarnings("unchecked")
                    public Object getValue(Object element, String property) {
                        Map<String, String> map = (Map<String, String>) element;
                        return map.get("0");
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
                        if (!StringUtil.isEmpty(value.toString())) {
                            for (Map<String, String> paras : keyList) {
                                if (paras == obj) {
                                    continue;
                                }
                                if (paras.get("0").equals(value)) {
                                    isValid = false;
                                    CommonUITool.openErrorBox(
                                            Messages.bind(
                                                    Messages.errShardKeyNameExist,
                                                    new Object[] {value.toString()}));
                                    break;
                                }
                            }
                        }

                        if (isValid) {
                            map.put("0", value.toString());
                            currentShardKey.setName(value.toString());
                            keyInfoGroup.setText("Key Info: " + value);
                        }

                        keyListTableViewer.refresh();

                        // Notice the upper layer
                        modifyListener.modifyText(null);
                    }
                });
    }

    /** Links the editable column of table */
    private void linkEditorForKeyInfoTable() {
        keyInfoTableViewer.setColumnProperties(keyInfoColumnNameArrs);
        CellEditor[] editors = new CellEditor[3];
        editors[0] = new TextCellEditor(keyInfoTable);
        editors[1] = new TextCellEditor(keyInfoTable);
        String[] shardIds = new String[shardIdList.size()];
        shardIds = shardIdList.toArray(shardIds);
        shardIdCellEditor =
                new ComboBoxCellEditor(keyInfoTable, shardIds, SWT.READ_ONLY) {

                    {
                        if (!shardIdList.isEmpty()) {
                            this.doSetValue(shardIdList.get(0));
                        }
                    }

                    protected void doSetValue(Object value) {
                        for (int i = 0; i < shardIdList.size(); i++) {
                            if (shardIdList.get(i).equals((String) value)) {
                                super.doSetValue(i);
                            }
                        }
                    }

                    protected Object doGetValue() {
                        int selection = ((Integer) super.doGetValue()).intValue();
                        if (selection == -1) {
                            return "";
                        }
                        return shardIdList.get(selection);
                    }
                };
        editors[2] = shardIdCellEditor;

        keyInfoTableViewer.setCellEditors(editors);
        keyInfoTableViewer.setCellModifier(
                new ICellModifier() {

                    public boolean canModify(Object element, String property) {
                        return true;
                    }

                    @SuppressWarnings("unchecked")
                    public Object getValue(Object element, String property) {
                        Map<String, String> map = (Map<String, String>) element;
                        if (property.equals(keyInfoColumnNameArrs[0])) {
                            return map.get("0");
                        } else if (property.equals(keyInfoColumnNameArrs[1])) {
                            return map.get("1");
                        } else if (property.equals(keyInfoColumnNameArrs[2])) {
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
                        if (!StringUtil.isEmpty(value.toString())) {

                            if (isValid && !ValidateUtil.isInteger(value.toString())) {
                                isValid = false;
                                CommonUITool.openErrorBox(Messages.errShardKeyParameterNotNumeric);
                            }
                            if (isValid) {
                                if (property.equals(keyInfoColumnNameArrs[0])) {
                                    map.put("0", value.toString());
                                } else if (property.equals(keyInfoColumnNameArrs[1])) {
                                    map.put("1", value.toString());
                                } else if (property.equals(keyInfoColumnNameArrs[2])) {
                                    map.put("2", value.toString());
                                }
                            }
                        }

                        keyInfoTableViewer.refresh();
                        saveShardKey();

                        // Notice the upper layer
                        modifyListener.modifyText(null);
                    }
                });
    }

    public Map<String, String> valid() {
        boolean tag = true;
        String message = "";
        if (keyList.isEmpty()) {
            tag = false;
            message = Messages.errShardKeyEmpty;
            return MessageUtil.generateResult(tag, message);
        }
        for (int i = 0; i < keyList.size(); i++) {
            Map<String, String> dataMap = keyList.get(i);
            if (StringUtil.isEmpty(dataMap.get("0"))) {
                tag = false;
                message = Messages.errShardKeyParameterEmpty;
                return MessageUtil.generateResult(tag, message);
            }
        }
        for (ShardKey key : this.module.getShardKeysFile().getKeys()) {
            List<String> sections = key.getSections();
            if (sections.isEmpty()) {
                tag = false;
                message =
                        Messages.bind(
                                Messages.errShardKeyParameterDataEmpty,
                                new Object[] {key.getName()});
                return MessageUtil.generateResult(tag, message);
            }
            int size = sections.size();
            List<Integer> mins = new ArrayList<Integer>(size);
            List<Integer> maxs = new ArrayList<Integer>(size);
            List<String> shardIds = new ArrayList<String>(size);
            for (String section : sections) {
                String[] tmp = section.split(",");
                for (String tp : tmp) {
                    if (StringUtil.isEmpty(tp)) {
                        tag = false;
                        message =
                                Messages.bind(
                                        Messages.errShardKeyParameterDataParameterEmpty,
                                        new Object[] {key.getName()});
                        return MessageUtil.generateResult(tag, message);
                    }
                }
                mins.add(Integer.valueOf(tmp[0]));
                maxs.add(Integer.valueOf(tmp[1]));
                shardIds.add(tmp[2]);
            }

            // 0~n/n+1~n+m/n+m+1~......./n+k+1~255(k>m)
            int flag = -1;
            int min = 0;
            int max = 0;
            for (int i = 0; i < size; i++) {
                min = mins.get(i);
                max = maxs.get(i);
                for (int j = i + 1; j < size; j++) {
                    if (min > mins.get(j)) {
                        mins.set(i, mins.get(j));
                        maxs.set(i, maxs.get(j));
                        mins.set(j, min);
                        maxs.set(j, max);
                        min = mins.get(i);
                        max = maxs.get(i);
                    }
                }
                if (min != flag + 1) {
                    tag = false;
                    message =
                            Messages.bind(
                                    Messages.errShardKeyDataRange, new Object[] {key.getName()});
                    return MessageUtil.generateResult(tag, message);
                }
                flag = max;
            }
            if (max != 255) {
                tag = false;
                message =
                        Messages.bind(Messages.errShardKeyDataRange, new Object[] {key.getName()});
                return MessageUtil.generateResult(tag, message);
            }

            for (String shardId : shardIds) {
                if (!shardIdList.contains(shardId)) {
                    tag = false;
                    message = Messages.bind(Messages.errShardIdNotExist, new Object[] {shardId});
                    return MessageUtil.generateResult(tag, message);
                }
            }
        }

        return MessageUtil.generateResult(tag, message);
    }

    private void saveShardKey() {
        currentShardKey.getSections().clear();
        for (Map<String, String> keyInfo : keyInfoList) {
            String min = keyInfo.get("0");
            String max = keyInfo.get("1");
            String shardId = keyInfo.get("2");
            min = StringUtil.isEmpty(min) ? " " : min;
            max = StringUtil.isEmpty(max) ? " " : max;
            shardId = StringUtil.isEmpty(shardId) ? " " : shardId;
            currentShardKey.addSection(min + "," + max + "," + shardId);
        }
    }

    public void save() {
        saveShardKey();
    }
}
