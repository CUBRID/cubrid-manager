/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.shard.control;

import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.MessageUtil;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.ui.shard.Messages;
import com.cubrid.cubridmanager.ui.shard.common.ShardGeneralInfoPanel;
import com.cubrid.cubridmanager.ui.shard.common.ShardListGroupPanel;
import java.util.Map;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * CUBRID Shards property page
 *
 * @author Tobi
 * @version 1.0
 * @date 2013-1-9
 */
public class ShardsParameterPropertyPage extends PreferencePage {

    private final ServerInfo serverInfo;
    private final Shards shards;

    private ShardGeneralInfoPanel shardGeneralInfoPanel;
    private ShardListGroupPanel shardListGroupPanel;

    public ShardsParameterPropertyPage(ICubridNode node, String name) {
        super(name, null);
        noDefaultAndApplyButton();
        serverInfo = node.getServer().getServerInfo();
        shards = serverInfo.getShards();

        ModifyListener modifyListener = new PageModifyListener();
        shardGeneralInfoPanel =
                new ShardGeneralInfoPanel(modifyListener, this.shards, this.serverInfo);
        shardListGroupPanel = new ShardListGroupPanel(modifyListener, this.shards, this.serverInfo);
    }

    /**
     * Creates the page content
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createShardLstComp(composite);

        initial();
        return composite;
    }

    /**
     * Creates brokers list Composite
     *
     * @param parent the parent composite
     * @return the composite
     */
    private Control createShardLstComp(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        shardGeneralInfoPanel.build(composite);
        shardListGroupPanel.build(composite);

        return composite;
    }

    /** Initializes the parameters of this dialog */
    private void initial() {}

    /** Restore the default value */
    protected void performDefaults() {
        initial();
        super.performDefaults();
    }

    /**
     * Save the page content
     *
     * @return <code>true</code> if it saved successfully;<code>false</code> otherwise
     */
    public boolean performOk() {
        shardListGroupPanel.save(); // TODO
        CommonTaskExec taskExec = new CommonTaskExec(Messages.msgOperating);
        taskExec.setTask(shardListGroupPanel.generateTasks());
        new ExecTaskWithProgress(taskExec).exec();
        if (taskExec.isSuccess()) {
            CommonUITool.openInformationBox(Messages.titleSuccess, Messages.restartShardBrokerMsg);
            return true;
        } else {
            CommonUITool.openErrorBox(Messages.msgOperationFailed);
            return false;
        }
    }

    /**
     * Judge if there is change on table
     *
     * @return <code>true</code> if changed;<code>false</code> otherwise
     */
    @SuppressWarnings("unused")
    private boolean isTableChange() {
        return false;
    }

    private class PageModifyListener implements ModifyListener {

        public void modifyText(ModifyEvent event) {

            Map<String, String> result = shardGeneralInfoPanel.valid();

            if (!MessageUtil.getResultTag(result)) {
                setErrorMessage(MessageUtil.getResultMessage(result));
                return;
            }

            setErrorMessage(null);
        }
    }
}
