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
package com.cubrid.cubridmanager.ui.common.navigator;

import com.cubrid.common.ui.common.navigator.NavigatorViewSorter;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;

/**
 * 
 * CUBRID Manager Navigator view sorter
 * 
 * @author pangqiren
 * @version 1.0 - 2010-12-13 created by pangqiren
 */
public class CMNavigatorViewSorter extends
		NavigatorViewSorter {

	/**
	 * 
	 * @see NavigatorViewSorter.category(ICubridNode)
	 * 
	 * @param node ICubridNode
	 * @return int
	 */
	protected int category(ICubridNode node) {
		String type = node.getType();
		if (NodeType.DATABASE_FOLDER.equals(type)
				|| CubridNodeType.BROKER_FOLDER.equals(type)
				|| CubridNodeType.MONITOR_FOLDER.equals(type)
				|| CubridNodeType.LOGS_FOLDER.equals(type)
				|| CubridNodeType.SHARD_FOLDER.equals(type)) {
			return 1;
		}
		if (NodeType.TABLE_FOLDER.equals(type)
				|| NodeType.VIEW_FOLDER.equals(type)
				|| NodeType.SERIAL_FOLDER.equals(type)
				|| NodeType.TRIGGER_FOLDER.equals(type)
				|| NodeType.STORED_PROCEDURE_FOLDER.equals(type)
				|| CubridNodeType.USER_FOLDER.equals(type)
				|| CubridNodeType.JOB_FOLDER.equals(type)
				|| CubridNodeType.DBSPACE_FOLDER.equals(type)) {
			return 2;
		}
		if (NodeType.TABLE_COLUMN_FOLDER.equals(type)
				|| NodeType.TABLE_INDEX_FOLDER.equals(type)
				|| NodeType.USER_PARTITIONED_TABLE.equals(type)) {
			return 3;
		}
		if (NodeType.TABLE_COLUMN.equals(type)) {
			return 4;
		}
		if (NodeType.STORED_PROCEDURE_FUNCTION_FOLDER.equals(type)
				|| NodeType.STORED_PROCEDURE_PROCEDURE_FOLDER.equals(type)) {
			return 5;
		}
		if (CubridNodeType.BACKUP_PLAN_FOLDER.equals(type)
				|| CubridNodeType.QUERY_PLAN_FOLDER.equals(type)) {
			return 6;
		}
		if (CubridNodeType.GENERIC_VOLUME_FOLDER.equals(type)
				|| CubridNodeType.DATA_VOLUME_FOLDER.equals(type)
				|| CubridNodeType.INDEX_VOLUME_FOLDER.equals(type)
				|| CubridNodeType.TEMP_VOLUME_FOLDER.equals(type)
				|| CubridNodeType.LOG_VOLUEM_FOLDER.equals(type)) {
			return 7;
		}
		if (CubridNodeType.ACTIVE_LOG_FOLDER.equals(type)
				|| CubridNodeType.ARCHIVE_LOG_FOLDER.equals(type)) {
			return 8;
		}
		if (CubridNodeType.STATUS_MONITOR_FOLDER.equals(type)
				|| CubridNodeType.SYSTEM_MONITOR_FOLDER.equals(type)
				|| CubridNodeType.MONITOR_STATISTIC_FOLDER.equals(type)) {
			return 9;
		}
		if (CubridNodeType.LOGS_BROKER_FOLDER.equals(type)
				|| CubridNodeType.LOGS_MANAGER_FOLDER.equals(type)
				|| CubridNodeType.LOGS_SERVER_FOLDER.equals(type)) {
			return 10;
		}
		if (CubridNodeType.LOGS_BROKER_ACCESS_LOG_FOLDER.equals(type)
				|| CubridNodeType.LOGS_BROKER_ERROR_LOG_FOLDER.equals(type)
				|| CubridNodeType.LOGS_BROKER_ADMIN_LOG_FOLDER.equals(type)) {
			return 11;
		}
		if (CubridNodeType.LOGS_MANAGER_ACCESS_LOG.equals(type)
				|| CubridNodeType.LOGS_MANAGER_ERROR_LOG.equals(type)) {
			return 12;
		}
		if (NodeType.MORE.equals(type)) {
			return 13;
		}
		return -1;
	}
}
