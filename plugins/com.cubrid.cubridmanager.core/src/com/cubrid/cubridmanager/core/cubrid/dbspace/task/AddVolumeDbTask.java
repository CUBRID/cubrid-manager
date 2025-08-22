/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.cubridmanager.core.cubrid.dbspace.task;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;

/**
 * A task that add volume DB
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-4-22 created lizhiqiang
 */
public class AddVolumeDbTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dbname",
                "volname",
                "purpose",
                "path",
                "numberofpages",
                "size_need_mb"
            };

    /**
     * The constructor
     *
     * @param serverInfo
     */
    public AddVolumeDbTask(ServerInfo serverInfo) {
        super("addvoldb", serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set database name
     *
     * @param dbname String database name
     */
    public void setDbname(String dbname) {
        super.setMsgItem("dbname", dbname);
    }

    /**
     * set volume name
     *
     * @param volname String volume name
     */
    public void setVolname(String volname) {
        super.setMsgItem("volname", volname);
    }

    /**
     * Set purpose
     *
     * @param purpose String purpose
     */
    public void setPurpose(String purpose) {
        super.setMsgItem("purpose", purpose);
    }

    /**
     * Set the path of volume
     *
     * @param path String th path of volume
     */
    public void setPath(String path) {
        super.setMsgItem("path", path);
    }

    /**
     * Set number of page
     *
     * @param numberofpages String number of pages
     */
    public void setNumberofpages(String numberofpages) {
        super.setMsgItem("numberofpages", numberofpages);
    }

    /**
     * Set the size of need, which units is mb
     *
     * @param sizeNeedMb String the sized of need
     */
    public void setSizeNeedMb(String sizeNeedMb) {
        super.setMsgItem("size_need_mb", sizeNeedMb);
    }
}
