/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.core.monitoring.model;

import com.cubrid.cubridmanager.core.Messages;

/**
 * This enumeration providers the names for broker diagnose name showing in the CUBRID MANAGER
 * Client
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-30 created by lizhiqiang
 */
public enum BrokerDiagEnum implements IDiagPara {
    RPS,
    TPS,
    ACTIVE_SESSION,
    QPS,
    LONG_Q,
    LONG_T,
    ERR_Q,
    SESSION,
    ACTIVE;

    public String getName() {
        return getNick();
    }

    /**
     * Get the nick name of enum
     *
     * @return String the nick name
     */
    public String getNick() {
        String nick = "";
        switch (this) {
            case RPS:
                nick = Messages.rps;
                break;
            case TPS:
                nick = Messages.tps;
                break;
            case ACTIVE_SESSION:
                nick = Messages.active_session;
                break;
            case QPS:
                nick = Messages.qps;
                break;
            case LONG_Q:
                nick = Messages.long_q;
                break;
            case LONG_T:
                nick = Messages.long_t;
                break;
            case ERR_Q:
                nick = Messages.err_q;
                break;
            case SESSION:
                nick = Messages.session;
                break;
            case ACTIVE:
                nick = Messages.active;
                break;
            default:
        }
        return nick;
    }

    /**
     * Search the enum by the nick name
     *
     * @param nick the nick name
     * @return BrokerDiagEnum
     */
    public static BrokerDiagEnum searchNick(String nick) {
        for (BrokerDiagEnum enumeration : BrokerDiagEnum.values()) {
            if (nick.equals(enumeration.getNick())) {
                return enumeration;
            }
        }
        return null;
    }
}
