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
package com.cubrid.cubridmanager.core.monstatistic.task;

import com.cubrid.cubridmanager.core.common.model.IModel;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

/**
 * Used for CMS API "get_mon_statistic".
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-08-20 created by Santiago Wang
 * @param <T>
 */
public class GetMonitorStatisticDataTask<T extends IModel> extends SocketTask {

    private final T result;

    /**
     * The constructor
     *
     * @param serverInfo String The server info
     * @param sendMSGItems String The send message items
     * @param bean T The bean that will be set
     */
    public GetMonitorStatisticDataTask(ServerInfo serverInfo, String[] sendMSGItems, T bean) {
        super(bean.getTaskName(), serverInfo, sendMSGItems);
        this.result = bean;
    }

    /**
     * The constructor
     *
     * @param serverInfo String The server info
     * @param sendMSGItems String The send message items
     * @param bean T The bean that will be set
     * @param charset The send and response charSet
     */
    public GetMonitorStatisticDataTask(
            ServerInfo serverInfo, String[] sendMSGItems, T bean, String charset) {
        super(bean.getTaskName(), serverInfo, sendMSGItems, charset, charset);
        this.result = bean;
    }

    /**
     * Show the result of the database space information
     *
     * @return T
     */
    public T getResultModel() {
        TreeNode node = (TreeNode) getResponse();
        if (node != null) {
            setFieldValue(node, result);
        }
        return result;
    }

    /**
     * set metric into msg
     *
     * @param metric String The specified metric type
     */
    public void setMetric(String metric) {
        super.setMsgItem("metric", metric);
    }

    /**
     * set dtype into msg
     *
     * @param dateType String The specified time type
     */
    public void setDateType(String dateType) {
        super.setMsgItem("dtype", dateType);
    }

    /**
     * set dbname into msg
     *
     * @param dbName String The given database name
     */
    public void setDbName(String dbName) {
        super.setMsgItem("dbname", dbName);
    }

    /**
     * set volname into msg
     *
     * @param volName String The given volume name
     */
    public void setVolName(String volName) {
        super.setMsgItem("volname", volName);
    }

    /**
     * Set broker name
     *
     * @param name String
     */
    public void setBrokerName(String name) {
        super.setMsgItem("bname", name);
    }
}
