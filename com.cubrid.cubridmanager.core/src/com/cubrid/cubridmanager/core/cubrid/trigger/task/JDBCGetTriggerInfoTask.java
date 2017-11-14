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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * Get trigger information by JDBC
 * 
 * @author pangqiren
 * @version 1.0 - 2010-11-16 created by pangqiren
 */
public class JDBCGetTriggerInfoTask extends JDBCTask {
	private static final Logger LOGGER = LogUtil.getLogger(JDBCGetTriggerInfoTask.class);

	/**
	 * The constructor
	 * 
	 * @param dbInfo
	 */
	public JDBCGetTriggerInfoTask(DatabaseInfo dbInfo) {
		super("GetTriggerInfo", dbInfo);
	}

	/**
	 * Get trigger information by trigger name
	 * 
	 * @param triggerName String The given serial name
	 * @return Trigger The instance of Trigger
	 */
	public Trigger getTriggerInfo(String triggerName) {
		Trigger trigger = null;
		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return null;
			}

			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return null;
			}

			String sql = "SELECT t.*, c.targetClassName"
					+ " FROM db_trigger t, db_trig c"
					+ " WHERE t.name=c.trigger_name AND t.name=?";

			// [TOOLS-2425]Support shard broker
			sql = DatabaseInfo.wrapShardQuery(databaseInfo, sql);

			stmt = connection.prepareStatement(sql);
			((PreparedStatement) stmt).setString(1, triggerName);
			rs = ((PreparedStatement) stmt).executeQuery();
			while (rs.next()) {
				trigger = new Trigger
					.Builder()
					.name(rs.getString("name"))
					.conditionTime(JDBCGetTriggerListTask.getConditionTime(rs.getInt("condition_time")))
					.eventType(JDBCGetTriggerListTask.getEventType(rs.getInt("event")))
					.targetClass(rs.getString("targetClassName"))
					.targetAttribute(rs.getString("targetAttribute"))
					.condition(rs.getString("condition"))
					.actionTime(JDBCGetTriggerListTask.getActionTime(rs.getInt("action_time")))
					.action(JDBCGetTriggerListTask.getAction(
							rs.getInt("action_type"),
							rs.getString("action_definition")))
					.status(JDBCGetTriggerListTask.getStatus(rs.getInt("status")))
					.priority(rs.getString("priority"))
					.build();
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

		return trigger;
	}
}
