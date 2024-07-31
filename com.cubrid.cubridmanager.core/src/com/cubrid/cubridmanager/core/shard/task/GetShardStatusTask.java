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
package com.cubrid.cubridmanager.core.shard.task;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.shard.model.Column;
import com.cubrid.cubridmanager.core.shard.model.ShardStatus;
import com.cubrid.cubridmanager.core.shard.model.ShardsStatus;
import java.lang.reflect.Field;

/**
 * Get shard status information.
 *
 * @author Tobi
 * @version 1.0
 * @date 2013-3-13
 */
public class GetShardStatusTask extends SocketTask {
    public GetShardStatusTask(ServerInfo serverInfo, String shardName) {
        super("getshardinfo", serverInfo, null);
        this.setMsgItem("shardname", shardName == null ? "" : shardName);
    }

    // TODO
    public ShardsStatus getShardsStatus() {
        TreeNode node = (TreeNode) getResponse();
        ShardsStatus result = new ShardsStatus();
        setFieldValueNew(node, result);
        return result;
    }

    private static void setFieldValueNew(TreeNode node, final ShardsStatus shardsStatus) {
        if (node == null || shardsStatus == null) {
            return;
        }

        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return;
        }
        for (TreeNode shard : node.getChildren()) {
            ShardStatus shardStatus = new ShardStatus();

            Class<?> clazz = shardStatus.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column == null || !column.enable()) {
                    continue;
                }
                String propertyName = column.name();
                String value = shard.getValue(propertyName);
                field.setAccessible(true);
                try {
                    field.set(shardStatus, field.getType().cast(value));
                } catch (IllegalArgumentException e) {
                    LOGGER.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            shardsStatus.addShardStatus(shardStatus);
        }
    }
}
