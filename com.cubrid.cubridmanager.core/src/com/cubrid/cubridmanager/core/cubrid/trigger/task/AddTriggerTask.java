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

package com.cubrid.cubridmanager.core.cubrid.trigger.task;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerActionTime;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerConditionTime;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerEvent;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerStatus;
import java.util.Locale;

/**
 * This class is to add a trigger in CUBRID database.
 *
 * <p>Usage: You must first set fields by invoking setXXX(\<T\>) methods, then call sendMsg() method
 * to send a request message, the response message is the information of the special class.
 *
 * @author moulinwang 2009-3-3
 */
public class AddTriggerTask extends SocketTask {
    private static final String[] SEND_MSG_ITEMS =
            new String[] {
                "task",
                "token",
                "dbname",
                "triggername",
                "conditiontime",
                "eventtype",
                "action",
                "eventtarget",
                "actiontime",
                "status",
                "priority"
            };
    private static final String CR = "\r";
    private static final String NL = "\n";

    public AddTriggerTask(ServerInfo serverInfo) {
        super("addtrigger", serverInfo, AddTriggerTask.SEND_MSG_ITEMS);
    }

    /**
     * Set the key "dbname" in request message
     *
     * @param dbname String
     */
    public void setDbName(String dbname) {
        this.setMsgItem("dbname", dbname);
    }

    /**
     * Set the key "triggername" in request message
     *
     * @param triggerName String
     */
    public void setTriggerName(String triggerName) {
        this.setMsgItem("triggername", triggerName);
    }

    /**
     * Set the key "conditiontime" in request message
     *
     * @param conditionTime TriggerConditionTime
     */
    public void setConditionTime(TriggerConditionTime conditionTime) {
        this.setMsgItem("conditiontime", conditionTime.getText());
    }

    /**
     * Set the key "condition" in request message
     *
     * @param condition String
     */
    public void setCondition(String condition) {
        String triggerCondition = condition.trim();
        if (triggerCondition.length() > 0) {
            if (triggerCondition.toLowerCase(Locale.getDefault()).startsWith("if ")) {
                triggerCondition = triggerCondition.substring(3);
            }
            this.setMsgItem("condition", triggerCondition);
        } else {
            this.setMsgItem("condition", condition);
        }
    }

    /**
     * Set the key "eventtype" in request message
     *
     * @param eventType TriggerEvent
     */
    public void setEventType(TriggerEvent eventType) {
        this.setMsgItem("eventtype", eventType.getText());
    }

    /**
     * Set the key "action" in request message
     *
     * @param action TriggerAction
     * @param addtionalInformation String
     */
    public void setAction(Trigger.TriggerAction action, String addtionalInformation) {
        switch (action) {
            case REJECT:
                this.setMsgItem("action", action.getText());
                break;
            case INVALIDATE_TRANSACTION:
                this.setMsgItem("action", action.getText());
                break;
            case OTHER_STATEMENT:
                this.setMsgItem("action", addtionalInformation);
                break;
            case PRINT:
                String message = addtionalInformation;
                message = message.replaceAll(CR, "");
                message = message.replaceAll(NL, " ");
                message = message.replaceAll("'", "''");
                this.setMsgItem("action", action.getText() + " '" + message + "'");
                break;
            default:
                throw new IllegalArgumentException("not supported TriggerAction");
        }
    }

    /**
     * Set the key "eventtarget" in request message,which indicate a class in database
     *
     * @param eventTarget String
     */
    public void setEventTarget(String eventTarget) {
        this.setMsgItem("eventtarget", eventTarget);
    }

    /**
     * Set the key "actiontime" in request message
     *
     * @param actionTime TriggerActionTime
     */
    public void setActionTime(TriggerActionTime actionTime) {
        this.setMsgItem("actiontime", actionTime.getText());
    }

    /**
     * Set the key "status" in request message
     *
     * @param status TriggerStatus
     */
    public void setStatus(TriggerStatus status) {
        this.setMsgItem("status", status.getText());
    }

    /**
     * Set the key "priority" in request message
     *
     * @param priority String
     */
    public void setPriority(String priority) {
        this.setMsgItem("priority", Trigger.formatPriority(priority));
    }
}
