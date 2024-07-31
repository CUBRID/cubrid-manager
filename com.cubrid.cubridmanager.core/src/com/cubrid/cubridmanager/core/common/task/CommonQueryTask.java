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
package com.cubrid.cubridmanager.core.common.task;

import com.cubrid.common.core.util.CipherUtils;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.common.model.IModel;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.utils.ModelUtil.KillTranType;

/**
 * The Common Query Task used in: 1.Send Message is sample(less than 4 parameter) 2.Response message
 * could be reflect into a Java Model completely.
 *
 * @author robin
 * @version 1.0 - 2009-6-4 created by robin
 * @param <T>
 */
public class CommonQueryTask<T extends IModel> extends SocketTask {

    private final T result;

    /**
     * The constructor
     *
     * @param serverInfo String The server info
     * @param sendMSGItems String The send message items
     * @param bean T The bean that will be set
     */
    public CommonQueryTask(ServerInfo serverInfo, String[] sendMSGItems, T bean) {
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
    public CommonQueryTask(ServerInfo serverInfo, String[] sendMSGItems, T bean, String charset) {
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
     * set dbname into msg
     *
     * @param dbName String The given database name
     */
    public void setDbName(String dbName) {
        super.setMsgItem("dbname", dbName);
    }

    /**
     * set brokerName into msg.this method is used when get broker log file information
     *
     * @param brokerName String The given broker name
     */
    public void setBroker(String brokerName) {
        super.setMsgItem("broker", brokerName);
    }

    /**
     * set Kill Transaction Type
     *
     * <p>taskName:killtransaction
     *
     * @param status KillTranType The given value of KillTranType
     */
    public void setKillTranType(KillTranType status) {
        super.setMsgItem("type", status.getText().toLowerCase());
    }

    /**
     * set Kill Transaction Parameter
     *
     * <p>taskName:killtransaction
     *
     * @param param String The given parameter
     */
    public void setKillTranParameter(String param) {
        super.setMsgItem("parameter", param);
    }

    /**
     * Set standby server parameter dbid
     *
     * <p>taskName:getstandbyserverstat
     *
     * @param param String The given parameter
     */
    public void setDbid(String param) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "dbid", CipherUtils.encrypt(param));
        } else {
            this.setMsgItem("dbid", param);
        }
    }

    /**
     * Set standby server parameter dbpasswd
     *
     * <p>taskName:getstandbyserverstat
     *
     * @param param String The given parameter
     */
    public void setDbpasswd(String param) {
        if (CompatibleUtil.isSupportCipher(serverInfo.getServerVersionKey())) {
            this.setMsgItem(CIPHER_CHARACTER + "dbpasswd", CipherUtils.encrypt(param));
        } else {
            this.setMsgItem("dbpasswd", param);
        }
    }

    /**
     * Set broker name
     *
     * @param name String
     */
    public void setBName(String name) {
        super.setMsgItem("bname", name);
    }

    /**
     * Set db user
     *
     * @param name String
     */
    public void setDbUser(String user) {
        super.setMsgItem("dbuser", user);
    }
}
