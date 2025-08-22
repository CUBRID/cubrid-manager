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
package com.cubrid.cubridmanager.core.shard.task;

import com.cubrid.common.core.task.ITask;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.shard.model.Shard;
import com.cubrid.cubridmanager.core.shard.model.Shards;
import java.util.ArrayList;
import java.util.List;

/**
 * Save, update, delete shard configuration files.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-28
 */
public class ShardsTaskFactory {
    private final ServerInfo serverInfo;
    private final Shards shards;
    private final List<Shard> deletes = new ArrayList<Shard>();
    private final List<Shard> updates = new ArrayList<Shard>();
    private final List<String> deleteFileNames = new ArrayList<String>();

    public ShardsTaskFactory(ServerInfo serverInfo, Shards shards) {
        this.serverInfo = serverInfo;
        this.shards = shards;
    }

    public void addUpdateFile(Shard model, String oldShardName) {
        this.deletes.remove(model);
        this.updates.add(model);
        if (!model.getName().equals(oldShardName) && oldShardName != null) {
            this.deleteFileNames.add(oldShardName);
        }
    }

    public void addDeleteFile(Shard model) {
        this.updates.remove(model);
        this.deletes.add(model);
    }

    public ITask[] generateTasks() {
        ITask[] tasks = new ITask[deletes.size() + updates.size()];
        List<ITask> taskList = new ArrayList<ITask>();

        // First, update connection, key files.
        // TODO How to delete old connection & key files,
        // if the shard has changed the name?
        for (Shard model : updates) {
            taskList.add(new SaveShardConfTask(this.serverInfo, model.getShardConnectionFile()));
            taskList.add(new SaveShardConfTask(this.serverInfo, model.getShardKeysFile()));
        }
        // Then, update shard.conf file.
        taskList.add(new SaveShardConfTask(this.serverInfo, shards));

        // Finally, delete the connection and key files.
        for (Shard model : deletes) {
            taskList.add(
                    new DeleteShardFileTask(this.serverInfo, model.getName() + "_connection.txt"));
            taskList.add(new DeleteShardFileTask(this.serverInfo, model.getName() + "_key.txt"));
        }
        for (String deleteFileName : deleteFileNames) {
            taskList.add(
                    new DeleteShardFileTask(this.serverInfo, deleteFileName + "_connection.txt"));
            taskList.add(new DeleteShardFileTask(this.serverInfo, deleteFileName + "_key.txt"));
        }

        tasks = taskList.toArray(tasks);
        return tasks;
    }
}
