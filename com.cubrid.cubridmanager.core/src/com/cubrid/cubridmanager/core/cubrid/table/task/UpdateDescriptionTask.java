/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution. 
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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * UpdateCommentTask Description
 * 
 * @author Kevin.Wang
 * @version 1.0 - 2013-1-9 created by Kevin.Wang
 */
public class UpdateDescriptionTask extends
		JDBCTask {
	private static final Logger LOGGER = LogUtil.getLogger(UpdateDescriptionTask.class);

	private String tableName;
	private String columnName;
	private String description;

	/**
	 * @param taskName
	 * @param dbInfo
	 */
	public UpdateDescriptionTask(String taskName, DatabaseInfo databaseInfo,
			String tableName, String columnName, String description) {
		super(taskName, databaseInfo, false);

		this.tableName = tableName;
		this.columnName = columnName;
		this.description = description;
	}

	public String getTableName() {
		return tableName;
	}
	
	@Override
	public void execute() {
		try {
			SchemaCommentHandler.updateDescription(databaseInfo, connection, tableName, columnName, description);
		} catch (SQLException e) {
			this.errorMsg = e.getMessage();
			LOGGER.error(e.getMessage(), e);
			QueryUtil.rollback(connection);
		} finally {
			finish();
		}
	}
}
