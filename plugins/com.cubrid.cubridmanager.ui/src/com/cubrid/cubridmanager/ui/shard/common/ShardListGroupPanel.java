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
package com.cubrid.cubridmanager.ui.shard.common;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.CubridShardConfParaConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.core.shard.task.ShardsTaskFactory;
import com.cubrid.cubridmanager.ui.shard.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * A panel that shows a shard list info.
 *
 * @author Tobi
 * @version 1.0
 * @date 2013-1-9
 */
public class ShardListGroupPanel extends AbstractModulePanel<Shards> {

    public ShardListGroupPanel(ModifyListener listener, Shards module, ServerInfo serverInfo) {
        super(listener, module);
        this.serverInfo = serverInfo;
        this.taskFactory = new ShardsTaskFactory(this.serverInfo, this.module);
    }

    private Button addBtn;
    private Button editBtn;
    private Button deleteBtn;

    private TableViewer shardsTableViewer;

    private ServerInfo serverInfo;

    private List<Map<String, String>> shardList;

    private List<String> deleteShardNames = new ArrayList<String>();

    private ShardsTaskFactory taskFactory;

    public void build(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Shard List");
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        group.setLayout(layout);

        String[] columnNameArrs = new String[] {"Name", "Port"};
        int[] columnWidthArrs = new int[] {120, 120};
        shardsTableViewer =
                CommonUITool.createCommonTableViewer(
                        group,
                        null,
                        columnNameArrs,
                        columnWidthArrs,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 300));
        createDealButton(group);
        load();
        initial();
    }

    /**
     * Creates the button of add, edit, delete
     *
     * @param parent the parent composite
     */
    private void createDealButton(Composite parent) {
        Composite btnComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        btnComposite.setLayout(layout);
        btnComposite.setLayoutData(new GridData());
        GridData data = new GridData(GridData.VERTICAL_ALIGN_CENTER);

        addBtn = new Button(btnComposite, SWT.PUSH);
        addBtn.setText(Messages.btnAdd);
        addBtn.setLayoutData(data);
        addBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        AddAction addAction = new AddAction();
                        addAction.run();
                    }
                });
        // new Label(btnComposite, SWT.NONE);
        editBtn = new Button(btnComposite, SWT.PUSH);
        editBtn.setText(Messages.btnEdit);
        editBtn.setLayoutData(data);
        IStructuredSelection selection = (IStructuredSelection) shardsTableViewer.getSelection();
        if (0 == selection.size()) {
            editBtn.setEnabled(false);
        }
        editBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        EditAction editAction = new EditAction();
                        editAction.run();
                    }
                });
        // new Label(btnComposite, SWT.NONE);
        deleteBtn = new Button(btnComposite, SWT.PUSH);
        deleteBtn.setText(Messages.btnDelete);
        deleteBtn.setLayoutData(data);
        if (0 == selection.size()) {
            deleteBtn.setEnabled(false);
        }
        deleteBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        DeleteAction deleteAction = new DeleteAction();
                        deleteAction.run();
                    }
                });

        shardsTableViewer.addPostSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        IStructuredSelection selection =
                                (IStructuredSelection) (event.getSelection());
                        if (selection.size() > 1) {
                            deleteBtn.setEnabled(true);
                        } else if (selection.size() == 1) {
                            editBtn.setEnabled(true);
                            // if (userInfo.getCasAuth() == CasAuthType.AUTH_ADMIN) {
                            deleteBtn.setEnabled(true);
                            // } else {
                            // deleteBtn.setEnabled(false);
                            // }
                        } else {
                            editBtn.setEnabled(false);
                            deleteBtn.setEnabled(false);
                        }
                    }
                });
    }

    public void load() {
        shardList = new ArrayList<Map<String, String>>();
        List<Shard> shards = module.getShardList();
        for (Shard shard : shards) {
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("0", shard.getName());
            dataMap.put("1", shard.getValue(CubridShardConfParaConstants.BROKER_PORT));
            shardList.add(dataMap);
        }
    }

    private void initial() {
        shardsTableViewer.setInput(shardList);
    }

    public Map<String, String> valid() {
        boolean tag = true;
        String message = "";
        return MessageUtil.generateResult(tag, message);
    }

    public void save() {
        for (String shardName : deleteShardNames) {
            module.removeShard(shardName);
        }
    }

    public ITask[] generateTasks() {

        return taskFactory.generateTasks();
    }

    /**
     * An action that is an inner class in order to execute adding the parameter of a shard
     *
     * @author Tobi
     * @version 1.0
     * @date 2013-1-9
     */
    private class AddAction extends Action {

        /** Add broker */
        @SuppressWarnings("unchecked")
        public void run() {
            // String sMasterShmId = "aaa";
            // List<Map<String, String>> brokerLst2Dialog = (List<Map<String,
            // String>>) brokersTableViewer.getInput();
            // BrokerParameterDialog brokerParameterDialog = new
            // BrokerParameterDialog(getShell(), AddEditType.ADD, node,
            // brokerLst2Dialog, sMasterShmId);
            // if (brokerParameterDialog.open() == Dialog.OK) {
            // Map<String, String> brokerMap =
            // brokerParameterDialog.getBrokerMap();
            // BrokerIntervalSetting brokerIntervalSetting =
            // brokerParameterDialog.getBrokerIntervalSetting();
            // String serverName = node.getServer().getLabel();
            // brokerIntervalSetting.setServerName(serverName);
            // newIntervalSettingMap.put(serverName + "_" +
            // brokerIntervalSetting.getBrokerName(),
            // brokerIntervalSetting);
            // brokerList.add(brokerMap);
            // brokersTableViewer.add(brokerMap);
            // }
        }
    }

    /**
     * An action that is an inner class in order to execute editing the parameter of a shard
     *
     * @author Tobi
     * @version 1.0
     * @date 2013-1-9
     */
    private class EditAction extends Action {
        private Map<String, String> shardMap;

        @SuppressWarnings("unchecked")
        public EditAction() {
            IStructuredSelection selection =
                    (IStructuredSelection) shardsTableViewer.getSelection();
            shardMap = (Map<String, String>) (selection.getFirstElement());
            // String serverName = node.getServer().getLabel();
            // brokerIntervalSetting = newIntervalSettingMap.get(serverName +
            // "_" + shardMap.get("0"));
        }

        /** Edit the broker */
        @SuppressWarnings("unchecked")
        public void run() {
            // String sMasterShmId = masterShmIdTxt.getText().trim();
            // List<Map<String, String>> brokerLst2Dialog = (List<Map<String,
            // String>>) shardsTableViewer.getInput();
            // BrokerParameterDialog brokerParameterDialog = new
            // BrokerParameterDialog(getShell(), AddEditType.EDIT, node,
            // brokerLst2Dialog, sMasterShmId, shardMap, brokerIntervalSetting);
            // if (brokerParameterDialog.open() == Dialog.OK) {
            // BrokerIntervalSetting brokerIntervalSetting =
            // brokerParameterDialog.getBrokerIntervalSetting();
            // String serverName = node.getServer().getLabel();
            // brokerIntervalSetting.setServerName(serverName);
            // newIntervalSettingMap.put(serverName + "_" +
            // brokerIntervalSetting.getBrokerName(),
            // brokerIntervalSetting);
            //
            // brokersTableViewer.refresh(shardMap);
            //
            // }
        }

        /**
         * Return enabled status
         *
         * @return <code>true</code> if enabled;<code>false</code> otherwise
         */
        public boolean isEnabled() {
            if (null == shardMap) {
                return false;
            }
            return true;
        }
    }

    /**
     * An action that is an inner class in order to execute deleting the parameter of a shard
     *
     * @author Tobi
     * @version 1.0
     * @date 2013-1-10
     */
    private class DeleteAction extends Action {

        private Iterator<Map<String, String>> deleteShards;

        @SuppressWarnings("unchecked")
        public DeleteAction() {
            IStructuredSelection selection =
                    (IStructuredSelection) shardsTableViewer.getSelection();
            deleteShards = selection.iterator();
        }

        /** Delete the shard */
        public void run() {
            while (deleteShards.hasNext()) {
                Map<String, String> shardMap = deleteShards.next();
                shardList.remove(shardMap);
                shardsTableViewer.remove(shardMap);
                // add to cache
                String shardName = shardMap.get("0");
                deleteShardNames.add(shardName);

                taskFactory.addDeleteFile(module.getShard(shardMap.get("0")));
            }
        }

        /**
         * Return enabled status
         *
         * @return <code>true</code> if enabled;<code>false</code> otherwise
         */
        public boolean isEnabled() {
            if (null == deleteShards) {
                return false;
            }
            return true;
        }
    }
}
