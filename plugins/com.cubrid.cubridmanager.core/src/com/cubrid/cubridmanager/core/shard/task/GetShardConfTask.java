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
package com.cubrid.cubridmanager.core.shard.task;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.shard.model.IShardModel;

/**
 * Get shard configure file conent
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-12-13
 */
public class GetShardConfTask<M extends IShardModel> extends SocketTask {
    private final M model;

    public GetShardConfTask(ServerInfo serverInfo, M model) {
        super("getallsysparam", serverInfo, null);
        this.setMsgItem("confname", model.getFileName());
        this.model = model;
    }

    public M loadDataToModel() {
        TreeNode response = getResponse();
        if (response == null || !this.isSuccess()) {
            return null;
        }

        // parse from TreeNode to Shards
        for (int i = 0, len = response.childrenSize(); i < len; i++) {
            TreeNode node = response.getChildren().get(i);
            if (node != null && StringUtil.isEqual(node.getValue("open"), "conflist")) {
                String[] confData = node.getValues("confdata");
                if (confData != null && confData.length > 0) {
                    model.parse(confData);
                    break;
                }
            }
        }

        return model;
    }
}
