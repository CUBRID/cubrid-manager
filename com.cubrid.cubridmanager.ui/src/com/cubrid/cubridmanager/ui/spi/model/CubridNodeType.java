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
package com.cubrid.cubridmanager.ui.spi.model;

import com.cubrid.common.ui.spi.model.NodeType;

/**
 * 
 * This enum type provide all CUBRID node type
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */

public class CubridNodeType extends
		NodeType {
	public static final String JOB_FOLDER = "JOB_FOLDER";
	public static final String BACKUP_PLAN_FOLDER = "BACKUP_PLAN_FOLDER";
	public static final String BACKUP_PLAN = "BACKUP_PLAN";
	public static final String QUERY_PLAN_FOLDER = "QUERY_PLAN_FOLDER";
	public static final String QUERY_PLAN = "QUERY_PLAN";
	public static final String DBSPACE_FOLDER = "DBSPACE_FOLDER";
	public static final String GENERIC_VOLUME_FOLDER = "GENERIC_VOLUME_FOLDER";
	public static final String GENERIC_VOLUME = "GENERIC_VOLUME";
	public static final String DATA_VOLUME_FOLDER = "DATA_VOLUME_FOLDER";
	public static final String DATA_VOLUME = "DATA_VOLUME";
	public static final String INDEX_VOLUME_FOLDER = "INDEX_VOLUME_FOLDER";
	public static final String INDEX_VOLUME = "INDEX_VOLUME";
	public static final String TEMP_VOLUME_FOLDER = "TEMP_VOLUME_FOLDER";
	public static final String TEMP_VOLUME = "TEMP_VOLUME";
	public static final String PP_VOLUME_FOLDER = "PP_VOLUME_FOLDER";
	public static final String PT_VOLUME_FOLDER = "PT_VOLUME_FOLDER";
	public static final String TT_VOLUME_FOLDER = "TT_VOLUME_FOLDER";
	public static final String LOG_VOLUEM_FOLDER = "LOG_VOLUEM_FOLDER";
	public static final String ACTIVE_LOG_FOLDER = "ACTIVE_LOG_FOLDER";
	public static final String ACTIVE_LOG = "ACTIVE_LOG";
	public static final String ARCHIVE_LOG_FOLDER = "ARCHIVE_LOG_FOLDER";
	public static final String ARCHIVE_LOG = "ARCHIVE_LOG";
	public static final String BROKER_FOLDER = "BROKER_FOLDER";
	public static final String BROKER = "BROKER";
	public static final String BROKER_SQL_LOG_FOLDER = "BROKER_SQL_LOG_FOLDER";
	public static final String BROKER_SQL_LOG = "BROKER_SQL_LOG";
	public static final String SHARD_FOLDER = "SHARD_FOLDER";
	public static final String SHARD = "SHARD";
	public static final String SHARD_SQL_LOG_FOLDER = "SHARD_SQL_LOG_FOLDER";
	public static final String SHARD_SQL_LOG = "SHARD_SQL_LOG";
	public static final String MONITOR_FOLDER = "MONITOR_FOLDER";
	public static final String STATUS_MONITOR_FOLDER = "STATUS_MONITOR_FOLDER";
	public static final String STATUS_MONITOR_TEMPLATE = "STATUS_MONITOR_TEMPLATE";
	public static final String SYSTEM_MONITOR_FOLDER = "SYSTEM_MONITOR_FOLDER";
	public static final String SYSTEM_MONITOR_TEMPLATE = "SYSTEM_MONITOR_TEMPLATE";
	public static final String MONITOR_STATISTIC_FOLDER = "MONITOR_STATISTIC_FOLDER";
	public static final String LOGS_FOLDER = "LOGS_FOLDER";
	public static final String LOGS_BROKER_FOLDER = "LOGS_BROKER_FOLDER";
	public static final String LOGS_BROKER_ACCESS_LOG_FOLDER = "LOGS_BROKER_ACCESS_LOG_FOLDER";
	public static final String LOGS_BROKER_ACCESS_LOG = "LOGS_BROKER_ACCESS_LOG";
	public static final String LOGS_BROKER_ERROR_LOG_FOLDER = "LOGS_BROKER_ERROR_LOG_FOLDER";
	public static final String LOGS_BROKER_ERROR_LOG = "LOGS_BROKER_ERROR_LOG";
	public static final String LOGS_BROKER_ADMIN_LOG_FOLDER = "LOGS_BROKER_ADMIN_LOG_FOLDER";
	public static final String LOGS_BROKER_ADMIN_LOG = "LOGS_BROKER_ADMIN_LOG";
	public static final String LOGS_MANAGER_FOLDER = "LOGS_MANAGER_FOLDER";
	public static final String LOGS_MANAGER_ACCESS_LOG = "LOGS_MANAGER_ACCESS_LOG";
	public static final String LOGS_MANAGER_ERROR_LOG = "LOGS_MANAGER_ERROR_LOG";
	public static final String LOGS_SERVER_FOLDER = "LOGS_SERVER_FOLDER";
	public static final String LOGS_SERVER_DATABASE_FOLDER = "LOGS_SERVER_DATABASE_FOLDER";
	public static final String LOGS_SERVER_DATABASE_LOG = "LOGS_SERVER_DATABASE_LOG";
	public static final String LOGS_APPLY_DATABASE_LOG = "LOGS_APPLY_DATABASE_LOG";
	public static final String LOGS_COPY_DATABASE_LOG = "LOGS_COPY_DATABASE_LOG";
}
