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
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.CubridShardConfParaConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.ui.shard.Messages;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A panel that shows shard general info.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-3
 */
public class ShardGeneralInfoPanel extends AbstractModulePanel<Shards> {

    public ShardGeneralInfoPanel(ModifyListener listener, Shards module, ServerInfo serverInfo) {
        super(listener, module);
        this.serverInfo = serverInfo;
    }

    private Text masterShmIdText;
    private Text adminLogFileText;

    private ServerInfo serverInfo;

    /**
     * Create database name group
     *
     * @param parent the parent composite
     */
    public void build(Composite parent) {
        Group generalGroup = new Group(parent, SWT.NONE);
        generalGroup.setText(Messages.shardGeneralInformation);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        generalGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        generalGroup.setLayout(layout);

        Label masterShmIdLabel = new Label(generalGroup, SWT.LEFT | SWT.WRAP);
        masterShmIdLabel.setText(CubridShardConfParaConstants.MASTER_SHM_ID);
        gridData = new GridData();
        gridData.widthHint = 150;
        masterShmIdLabel.setLayoutData(gridData);

        masterShmIdText = new Text(generalGroup, SWT.BORDER);
        masterShmIdText.setTextLimit(16);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        masterShmIdText.setLayoutData(gridData);

        Label adminLogFileLabel = new Label(generalGroup, SWT.LEFT | SWT.WRAP);
        adminLogFileLabel.setText(CubridShardConfParaConstants.ADMIN_LOG_FILE);
        gridData = new GridData();
        gridData.widthHint = 150;
        adminLogFileLabel.setLayoutData(gridData);

        adminLogFileText = new Text(generalGroup, SWT.BORDER);
        adminLogFileText.setTextLimit(64);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        adminLogFileText.setLayoutData(gridData);

        load();
        initial();
    }

    public void load() {
        String masterShmId = this.module.getValue(CubridShardConfParaConstants.MASTER_SHM_ID);
        masterShmIdText.setText(
                (masterShmId == null || "".equals(masterShmId)) ? "45001" : masterShmId);
        String logFile = this.module.getValue(CubridShardConfParaConstants.ADMIN_LOG_FILE);
        adminLogFileText.setText(
                (logFile == null || "".equals(logFile)) ? "log/broker/cubrid_broker.log" : logFile);
    }

    private void initial() {
        masterShmIdText.addModifyListener(modifyListener);
        masterShmIdText.addVerifyListener(
                new VerifyListener() {
                    public void verifyText(VerifyEvent event) {
                        if ("".equals(event.text)) {
                            return;
                        }
                        if (ValidateUtil.isNumber(event.text)) {
                            event.doit = true;
                        } else {
                            event.doit = false;
                        }
                    }
                });
        adminLogFileText.addModifyListener(modifyListener);
    }

    public Map<String, String> valid() {
        boolean tag = true;
        String message = "";

        String masterShmId = masterShmIdText.getText();
        if (StringUtil.isEmpty(masterShmId)) {
            tag = false;
            message = Messages.errShardMasterShmIdEmpty;
            return MessageUtil.generateResult(tag, message);
        }

        // check for conflicts: MASTER_SHM_ID
        if (serverInfo.checkBrokerShmIdConflicts(masterShmId)) {
            tag = false;
            message = Messages.errConflictShmId;
        }
        if (serverInfo.getShards().checkShmIdConflicts(module, masterShmId)) {
            tag = false;
            message = Messages.errConflictShmId;
        }

        String adminLogFile = adminLogFileText.getText();
        if (StringUtil.isEmpty(adminLogFile)) {
            tag = false;
            message = Messages.errShardAdminLogFileEmpty;
        }

        return MessageUtil.generateResult(tag, message);
    }

    public void save() {
        module.setValue(
                CubridShardConfParaConstants.MASTER_SHM_ID, masterShmIdText.getText().trim());
        module.setValue(
                CubridShardConfParaConstants.ADMIN_LOG_FILE, adminLogFileText.getText().trim());
    }
}
