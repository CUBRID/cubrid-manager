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
package com.cubrid.cubridmanager.core.cubrid.jobauto.task;

import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;

/**
 * Base on the name task,this task is responsible for add or edit the backup plan.
 *
 * @author lizhiqiang Apr 1, 2009
 */
public class BackupPlanTask extends SocketTask {

    public static final String ADD_BACKUP_INFO = "addbackupinfo";
    public static final String SET_BACKUP_INFO = "setbackupinfo";

    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dbname",
                "backupid",
                "path",
                "period_type",
                "period_date",
                "time",
                "level",
                "archivedel",
                "updatestatus",
                "storeold",
                "onoff",
                "zip",
                "check",
                "mt",
                "bknum"
            };

    /**
     * The constructor
     *
     * @param taskname admit only "addbackupinfo","setbackupinfo"
     * @param serverInfo ServerInfo
     */
    public BackupPlanTask(String taskname, ServerInfo serverInfo) {
        super(taskname, serverInfo, SEND_MSG_ITEMS);
    }

    /**
     * Set database name
     *
     * @param dbname String The database name
     */
    public void setDbname(String dbname) {
        super.setMsgItem("dbname", dbname);
    }

    /**
     * Set the backup id
     *
     * @param backupid String The given backup id
     */
    public void setBackupid(String backupid) {
        super.setMsgItem("backupid", backupid);
    }

    /**
     * Set the backup plan path
     *
     * @param path String The given backup plan path
     */
    public void setPath(String path) {
        super.setMsgItem("path", path);
    }

    /**
     * Set the period type
     *
     * @param periodType String the period type
     */
    public void setPeriodType(String periodType) {
        super.setMsgItem("period_type", periodType);
    }

    /**
     * Set the period date
     *
     * @param periodDate String The given period date
     */
    public void setPeriodDate(String periodDate) {
        super.setMsgItem("period_date", periodDate);
    }

    /**
     * Set the time
     *
     * @param time String The given time
     */
    public void setTime(String time) {
        super.setMsgItem("time", time);
    }

    /**
     * Set the backup level
     *
     * @param level String The given level
     */
    public void setLevel(String level) {
        super.setMsgItem("level", level);
    }

    /**
     * Set the archivedel is on or off
     *
     * @param onOffType OnOffType The given state of OnOffType
     */
    public void setArchivedel(OnOffType onOffType) {
        super.setMsgItem("archivedel", onOffType.getText());
    }

    /**
     * Set the store old value
     *
     * @param onOffType OnOffType The given state of OnOffType
     */
    public void setStoreold(OnOffType onOffType) {
        super.setMsgItem("storeold", onOffType.getText());
    }

    /**
     * Set the state of on or off
     *
     * @param onOffType OnOffType The given state of OnOffType
     */
    public void setOnoff(OnOffType onOffType) {
        super.setMsgItem("onoff", onOffType.getText());
    }

    /**
     * Set the state of zip
     *
     * @param yesNoType YesNoType The given state of YesNoType
     */
    public void setZip(YesNoType yesNoType) {
        super.setMsgItem("zip", yesNoType.getText());
    }

    /**
     * Set the state of check
     *
     * @param yesNoType YesNoType The given state of YesNoType
     */
    public void setCheck(YesNoType yesNoType) {
        super.setMsgItem("check", yesNoType.getText());
    }

    /**
     * Set the state of updatestatus
     *
     * @param onOffType OnOffType The given state of OnOffType
     */
    public void setUpdatestatus(OnOffType onOffType) {
        super.setMsgItem("updatestatus", onOffType.getText());
    }

    /**
     * set the number of thread
     *
     * @param mt String A String that is numeric and
     */
    public void setMt(String mt) {
        super.setMsgItem("mt", mt);
    }

    /**
     * set the bknum
     *
     * @param bknum
     */
    public void setBknum(String bknum) {
        super.setMsgItem("bknum", bknum);
    }
}
