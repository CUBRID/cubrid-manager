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
package com.cubrid.cubridmanager.core.cubrid.database.task;

import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;

/**
 * the task of copy database
 *
 * @author robin 2009-3-25
 */
public class CopyDbTask extends SocketTask {

    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "srcdbname",
                "destdbname",
                "destdbpath",
                "exvolpath",
                "logpath",
                "overwrite",
                "move",
                "advanced",
                "open",
                "close"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public CopyDbTask(ServerInfo serverInfo) {
        super("copydb", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set the source database name
     *
     * @param srcdbname String the source database name
     */
    public void setSrcdbname(String srcdbname) {
        super.setMsgItem("srcdbname", srcdbname);
    }

    /**
     * Set the destination database name
     *
     * @param destdbname String the destination database name
     */
    public void setDestdbname(String destdbname) {
        super.setMsgItem("destdbname", destdbname);
    }

    /**
     * Set the destination database path
     *
     * @param destdbpath String the destination database path
     */
    public void setDestdbpath(String destdbpath) {
        super.setMsgItem("destdbpath", destdbpath);
    }

    /**
     * Set the extra volume path
     *
     * @param exvolpath String the extra volume path
     */
    public void setExvolpath(String exvolpath) {
        super.setMsgItem("exvolpath", exvolpath);
    }

    /**
     * Set the log path
     *
     * @param logpath String the log path
     */
    public void setLogpath(String logpath) {
        super.setMsgItem("logpath", logpath);
    }

    /**
     * Set the over writer
     *
     * @param overwrite YesNoType Whether is allowed that be over written
     */
    public void setOverwrite(YesNoType overwrite) {
        super.setMsgItem("overwrite", overwrite.getText().toLowerCase());
    }

    /**
     * Set whether is allowed that be moved
     *
     * @param move YesNoType Whether is allowed that be moved
     */
    public void setMove(YesNoType move) {
        super.setMsgItem("move", move.getText().toLowerCase());
    }

    /**
     * Set whether start advance
     *
     * @param advanced OnOffType Whether start advance
     */
    public void setAdvanced(OnOffType advanced) {
        super.setMsgItem("advanced", advanced.getText().toLowerCase());
    }

    /**
     * Set the open parameter
     *
     * @param open String the open parameter
     */
    public void setOpen(String open) {
        super.setMsgItem("open", open);
    }

    /**
     * Set the close parameter
     *
     * @param close String the close parameter
     */
    public void setClose(String close) {
        super.setMsgItem("close", close);
    }
}
