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

package com.cubrid.cubridmanager.core.cubrid.trigger.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * This class is to get all triggers' information in CUBRID database.
 * 
 * Usage: You must first set fields by invoking setXXX(\<T\>) methods, then call
 * sendMsg() method to send a request message, the response message is the
 * information of the special class.
 * 
 * @author caoyilin 2010-11-5
 */
public class JDBCGetTriggerListTask extends JDBCTask {
	private static final Logger LOGGER = LogUtil.getLogger(JDBCGetTriggerListTask.class);
	private final List<Trigger> triggers = new ArrayList<Trigger>();

	public JDBCGetTriggerListTask(DatabaseInfo dbInfo) {
		super("GetTriggers", dbInfo);
	}

	/**
	 * Execute select sql.
	 */
	public void execute() {
		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return;
			}
			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return;
			}
			stmt = connection.createStatement();
			//			select count(*) from db_user d where
			//			{'DBA'} SUBSETEQ 
			//			(SELECT SET{CURRENT_USER}+COALESCE(SUM(SET{t.g.name}), SET{})  
			//				FROM db_user u, TABLE(groups) AS t(g)  
			//				WHERE u.name = d.name)
			//			and d.name=CURRENT_USER
			String sql = "SELECT t.*, c.targetClassName"
					+ " FROM db_trigger t, db_trig c"
					+ " WHERE t.name=c.trigger_name";

			// [TOOLS-2425]Support shard broker
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Trigger trigger = new Trigger.Builder()
					.name(rs.getString("name"))
					.conditionTime(getConditionTime(rs.getInt("condition_time")))
					.eventType(getEventType(rs.getInt("event")))
					.targetClass(rs.getString("targetClassName"))
					.targetAttribute(rs.getString("targetAttribute"))
					.condition(rs.getString("condition"))
					.actionTime(getActionTime(rs.getInt("action_time")))
					.action(getAction(rs.getInt("action_type"),
							rs.getString("action_definition")))
					.status(getStatus(rs.getInt("status")))
					.priority(rs.getString("priority"))
					.build();
				triggers.add(trigger);
			}
		} catch (SQLException e) {
			errorMsg = e.getMessage();
			if (errorMsg.indexOf("Select is not authorized on db_trigger") >= 0) {
				errorMsg = "";
			}

			LOGGER.error(e.getMessage(), e);
		} finally {
			finish();
		}
	}

	/**
	 * Get action time
	 * 
	 * @param at Integer Action Time
	 * @return DEFAULT AFTER OR DEFERRED
	 */
	public static String getActionTime(Integer at) {
		switch (at) {
		case 1:
			return "DEFAULT";
		case 2:
			return "AFTER";
		case 3:
			return "DEFERRED";
		default:
			return "DEFAULT";
		}
	}

	/**
	 * Get string of trigger status
	 * 
	 * @param status integer
	 * @return String of trigger status
	 */
	protected static String getStatus(int status) {
		String result = "";
		switch (status) {
		case 0:
			result = "INVALID";
			break;
		case 1:
			result = "INACTIVE";
			break;
		case 2:
			result = "ACTIVE";
			break;
		default:
			break;
		}

		return result;
	}

	/**
	 * Get string of trigger event type
	 * 
	 * @param event integer
	 * @return String of trigger event
	 */
	protected static String getEventType(int event) {
		String result = "";
		switch (event) {
		case 0:
			result = "UPDATE";
			break;
		case 1:
			result = "STATEMENT UPDATE";
			break;
		case 2:
			result = "DELETE";
			break;
		case 3:
			result = "STATEMENT DELETE";
			break;
		case 4:
			result = "INSERT";
			break;
		case 5:
			result = "STATEMENT INSERT";
			break;
		case 8:
			result = "COMMIT";
			break;
		case 9:
			result = "ROLLBACK";
			break;
		default:
			break;
		}

		return result;
	}

	/**
	 * Get string of trigger action type
	 * 
	 * @param at integer
	 * @param ad String
	 * @return String of trigger action
	 */
	protected static String getAction(int at, String ad) {
		String action = "";
		switch (at) {
		case 2:
			action = Trigger.TriggerAction.REJECT.getText();
			break;
		case 3:
			action = Trigger.TriggerAction.INVALIDATE_TRANSACTION.getText();
			break;
		case 4:
			action = Trigger.TriggerAction.PRINT.getText() + " '"
					+ (ad == null ? "" : ad) + "'";
			break;
		default:
			//action = TriggerAction.OTHER_STATEMENT.getText();
			action = ad;
			break;
		}

		return action;
	}

	/**
	 * Get the string of condition time.
	 * 
	 * @param ct integer
	 * @return the string of condition time
	 */
	protected static String getConditionTime(int ct) {
		String ctString = "";
		switch (ct) {
		case 0:
			ctString = "";
			break;
		case 1:
			ctString = "BEFORE";
			break;
		case 2:
			ctString = "AFTER";
			break;
		case 3:
			ctString = "DEFERRED";
			break;
		default:
			break;
		}

		return ctString;
	}

	/**
	 * Return whether adding trigger task is executed well
	 * 
	 * @return List<Trigger>
	 */
	public List<Trigger> getTriggerInfoList() {
		return triggers;
	}
}
