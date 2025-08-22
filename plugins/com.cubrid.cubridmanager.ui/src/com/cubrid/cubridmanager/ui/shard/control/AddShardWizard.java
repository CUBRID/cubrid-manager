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
package com.cubrid.cubridmanager.ui.shard.control;

import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.ShardConnection;
import com.cubrid.cubridmanager.core.shard.model.ShardKeys;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import com.cubrid.cubridmanager.core.shard.task.ShardsTaskFactory;
import com.cubrid.cubridmanager.ui.shard.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.Wizard;

/**
 * This wizard is provided for adding a shard broker.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-3
 */
public class AddShardWizard extends Wizard {
    private ShardWizardBrokerInfoPage shardWizardBrokerInfoPage;
    private ShardWizardConnectionPage ahardWizardConnectionPage;
    private ShardWizardKeysPage shardWizardKeysPage;
    private ShardWizardAllInfoPage shardWizardAllInfoPage;

    private final CubridServer server;
    private Shards shards;
    private final Shard shard = new Shard();

    public AddShardWizard(CubridServer server) {
        setWindowTitle(Messages.addShardWizard);
        this.server = server;
        this.shards = this.server.getServerInfo().getShards();
        // TODO The following code is actually useless. start
        this.shards = shards == null ? new Shards() : shards;
        // end
    }

    public void addPages() {

        shards.addShard(shard);
        ShardConnection shardConnectionFile = new ShardConnection();
        shard.setShardConnectionFile(shardConnectionFile);
        ShardKeys shardKeysFile = new ShardKeys();
        shard.setShardKeysFile(shardKeysFile);
        shardWizardBrokerInfoPage =
                new ShardWizardBrokerInfoPage(shards, shard, server.getServerInfo());
        addPage(shardWizardBrokerInfoPage);
        ahardWizardConnectionPage = new ShardWizardConnectionPage(shardConnectionFile);
        addPage(ahardWizardConnectionPage);
        shardWizardKeysPage = new ShardWizardKeysPage(shard);
        addPage(shardWizardKeysPage);
        shardWizardAllInfoPage = new ShardWizardAllInfoPage(shards, shard);
        addPage(shardWizardAllInfoPage);
    }

    /**
     * Return whether can finish
     *
     * @return <code>true</code>if can finish;<code>false</code> otherwise
     */
    public boolean canFinish() {
        return getContainer().getCurrentPage() == shardWizardAllInfoPage;
    }

    /**
     * Called when user clicks Finish
     *
     * @return boolean
     */
    public boolean performFinish() {

        ShardsTaskFactory taskFactory =
                new ShardsTaskFactory(this.server.getServerInfo(), this.shards);
        taskFactory.addUpdateFile(this.shard, null);

        CommonTaskExec taskExec = new CommonTaskExec(Messages.msgUploading);
        taskExec.setTask(taskFactory.generateTasks());
        new ExecTaskWithProgress(taskExec).exec();
        if (taskExec.isSuccess()) {
            close();
            return true;
        } else {
            CommonUITool.openErrorBox(Messages.errAddShardBroker);
            return false;
        }
    }

    /** Close this wizard dialog */
    private void close() {
        if (getContainer() instanceof Dialog) {
            ((Dialog) getContainer()).close();
        }
    }

    @Override
    public boolean performCancel() {
        // if (!CommonUITool.openConfirmBox(Messages.msgExitShardWizard)) {
        // return false;
        // }
        shards.removeShard(shard);
        return super.performCancel();
    }
}
