/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;

/**
 * This task is responsible to generate certificate file
 *
 * @author Kevin.Wang
 */
public class GenerateCertificateTask extends SocketTask {
    private int validDays = 100 * 365;

    private static final String[] SENDED_MSG_ITEMS = new String[] {"task", "token"};

    public GenerateCertificateTask(ServerInfo serverInfo) {
        super("generatecert", serverInfo, SENDED_MSG_ITEMS);
        setDays(validDays);
    }

    public void setDays(int validDays) {
        setMsgItem("days", String.valueOf(validDays));
    }

    public void setCName(String name) {
        setMsgItem("cname", name);
    }

    public void setStName(String stname) {
        setMsgItem("stname", stname);
    }

    public void setLonName(String stname) {
        setMsgItem("loname", stname);
    }

    public void setOrgname(String loname) {
        setMsgItem("loname", loname);
    }

    public void setComname(String comname) {
        setMsgItem("comname", comname);
    }

    public void setOrgutName(String orgutname) {
        setMsgItem("orgutname", orgutname);
    }

    public void setEmail(String email) {
        setMsgItem("email", email);
    }
}
