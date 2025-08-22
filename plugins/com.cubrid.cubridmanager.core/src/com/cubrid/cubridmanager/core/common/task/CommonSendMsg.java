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

import com.cubrid.cubridmanager.core.common.socket.SocketTask;

/**
 * This class list a lot of common send messages,these messages will be used by CommonQueryTask and
 * CommonUpdateTask class.
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class CommonSendMsg {

    // Constructor
    private CommonSendMsg() {
        // empty
    }

    private static final String[] COMMON_SIMPLE_SEND_MSG = {"task", "token"};

    /** taskName:dbspaceinfo,userinfo,getloginfo,stopdb,checkdb ,lockInfo,statdump */
    private static final String[] COMMON_DB_SEND_MSG = {"task", "token", "dbname"};

    /** taskName:checkdb */
    private static final String[] CHECK_DB_SEND_MSG = {"task", "token", "dbname", "repairdb"};

    /** taskName:compactdb */
    private static final String[] COMPACT_DB_SEND_MSG = {"task", "token", "dbname", "verbose"};

    /** taskName:optimizedb */
    private static final String[] OPTIMIZE_DB_SEND_MSG = {"task", "token", "dbname", "classname"};

    /** taskName:deletedb */
    private static final String[] DELETE_DB_SEND_MSG = {"task", "token", "dbname", "delbackup"};

    /** taskName:classinfo */
    private static final String[] CLASS_INFO_SEND_MSG = {"task", "token", "dbname", "dbstatus"};

    /** taskName:getlogfileinfo */
    private static final String[] GET_BROKER_LOG_FILE_INFO_MSG_ITEMS = {"task", "token", "broker"};

    /** taskName:killtransaction */
    private static final String[] KILL_TRANSACTION_MSG_ITEMS = {
        "task", "token", "dbname", "type", "parameter"
    };

    /** taskName:deleteuser */
    private static final String[] DELETE_USER_MSG_ITEMS = {
        "task", "token", "dbname", "username", SocketTask.CIPHER_CHARACTER + "userName"
    };

    /** taskName:getstandbyserverstat */
    private static final String[] STANDBY_SERVERSTAT_MSG_ITEMS = {
        "task",
        "token",
        "dbname",
        "dbid",
        SocketTask.CIPHER_CHARACTER + "dbid",
        "dbpasswd",
        SocketTask.CIPHER_CHARACTER + "dbpasswd"
    };
    /** taskName:getbrokerstatus, getbrokersinfo, getbrokerdiagdata */
    private static final String[] GET_BROKER_STATUS_ITEMS = {"task", "token", "bname"};

    /**
     * Get the clone of COMMON_SIMPLE_SEND_MSG
     *
     * @return the COMMON_SIMPLE_SEND_MSG
     */
    public static String[] getCommonSimpleSendMsg() {
        return (String[]) (COMMON_SIMPLE_SEND_MSG.clone());
    }

    /**
     * Get the clone of COMMON_DATABASE_SEND_MSG
     *
     * @return the COMMON_DATABASE_SEND_MSG
     */
    public static String[] getCommonDatabaseSendMsg() {
        return (String[]) (COMMON_DB_SEND_MSG.clone());
    }

    /**
     * Get the clone of CHECK_DB_SEND_MSG
     *
     * @return the CHECK_DB_SEND_MSG
     */
    public static String[] getCheckDbSendMsg() {
        return (String[]) (CHECK_DB_SEND_MSG.clone());
    }

    /**
     * Get the clone of COMPACT_DB_SEND_MSG
     *
     * @return the COMPACT_DB_SEND_MSG
     */
    public static String[] getCompactDbSendMsg() {
        return (String[]) (COMPACT_DB_SEND_MSG.clone());
    }

    /**
     * Get the clone of OPTIMIZE_DB_SEND_MSG
     *
     * @return the OPTIMIZE_DB_SEND_MSG
     */
    public static String[] getOptimizeDbSendMsg() {
        return (String[]) (OPTIMIZE_DB_SEND_MSG.clone());
    }

    /**
     * Get the clone of DELETE_DB_SEND_MSG
     *
     * @return the DELETE_DB_SEND_MSG
     */
    public static String[] getDeletedbSendMsg() {
        return (String[]) (DELETE_DB_SEND_MSG.clone());
    }

    /**
     * Get the clone of CLASS_INFO_SEND_MSG
     *
     * @return the CLASS_INFO_SEND_MSG
     */
    public static String[] getClassInfoSendMsg() {
        return (String[]) (CLASS_INFO_SEND_MSG.clone());
    }

    /**
     * Get the clone of GET_BROKER_LOG_FILE_INFO_MSG_ITEMS
     *
     * @return the GET_BROKER_LOG_FILE_INFO_MSG_ITEMS
     */
    public static String[] getGetBrokerLogFileInfoMSGItems() {
        return (String[]) (GET_BROKER_LOG_FILE_INFO_MSG_ITEMS.clone());
    }

    /**
     * Get the clone of KILL_TRANSACTION_MSG_ITEMS
     *
     * @return the KILL_TRANSACTION_MSG_ITEMS
     */
    public static String[] getKillTransactionMSGItems() {
        return (String[]) (KILL_TRANSACTION_MSG_ITEMS.clone());
    }

    /**
     * Get the clone of DELETE_USER_MSG_ITEMS
     *
     * @return the DELETE_USER_MSG_ITEMS
     */
    public static String[] getDeleteUserMSGItems() {
        return (String[]) (DELETE_USER_MSG_ITEMS.clone());
    }

    /**
     * Get the clone of GET_BROKER_STATUS_ITEMS
     *
     * @return the GET_BROKER_STATUS_ITEMS
     */
    public static String[] getGetBrokerStatusItems() {
        return (String[]) (GET_BROKER_STATUS_ITEMS.clone());
    }

    /**
     * Get the clone of STANDBY_SERVERSTAT_MSG_ITEMS
     *
     * @return the standbyServerstatMsgItems
     */
    public static String[] getStandbyServerstatMsgItems() {
        return (String[]) (STANDBY_SERVERSTAT_MSG_ITEMS.clone());
    }
}
