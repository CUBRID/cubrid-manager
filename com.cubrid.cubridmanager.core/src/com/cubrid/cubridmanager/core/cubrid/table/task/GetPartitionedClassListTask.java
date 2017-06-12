/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.cubridmanager.core.Messages;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttributeStatistic;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;

/**
 * This task is responsible to get partition class list of some class
 * 
 * @author pangqiren
 * @version 1.0 - 2009-5-13 created by pangqiren
 */
public class GetPartitionedClassListTask extends JDBCTask {
	private static final Logger LOGGER = LogUtil.getLogger(GetPartitionedClassListTask.class);
	private boolean isCommentSupport = false;

	public GetPartitionedClassListTask(DatabaseInfo dbInfo) {
		super("GetPartitionedClassList", dbInfo);
		isCommentSupport = CompatibleUtil.isCommentSupports(dbInfo);
	}

	/**
	 * Get all partitioned class info list
	 * 
	 * @param tableName String the table name
	 * @return List<ClassInfo>
	 */
	public List<ClassInfo> getAllPartitionedClassInfoList(String tableName) {
		List<ClassInfo> allClassInfoList = new ArrayList<ClassInfo>();
		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return allClassInfoList;
			}
			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return allClassInfoList;
			}
			String sql = "SELECT b.* FROM db_partition a, db_class b WHERE a.class_name=?"
					+ " AND LOWER(b.class_name)=LOWER(a.partition_class_name)";
			stmt = connection.prepareStatement(sql);
			((PreparedStatement) stmt).setString(1,
					tableName.toLowerCase(Locale.getDefault()));
			rs = ((PreparedStatement) stmt).executeQuery();
			while (rs.next()) {
				String className = rs.getString("class_name");
				String ownerName = rs.getString("owner_name");
				String classType = rs.getString("class_type");
				ClassType type = ClassType.NORMAL;
				if (classType != null
						&& classType.trim().equalsIgnoreCase("VCLASS")) {
					type = ClassType.VIEW;
				}
				String isSystemClazz = rs.getString("is_system_class");
				boolean isSystemClass = false;
				if (isSystemClazz != null
						&& isSystemClazz.trim().equalsIgnoreCase("YES")) {
					isSystemClass = true;
				}
				ClassInfo classInfo = new ClassInfo(className, ownerName, type,
						isSystemClass, true);
				allClassInfoList.add(classInfo);
			}
		} catch (SQLException e) {
			LOGGER.error("", e);
			errorMsg = e.getMessage();
		} finally {
			finish();
		}
		return allClassInfoList;
	}

	/**
	 * Returning of partitioning sub-table meta information
	 * 
	 * @param tableName String partitioned table name
	 * @return List<PartitionInfo>
	 */
	public List<PartitionInfo> getPartitionItemList(String tableName) {

		List<PartitionInfo> result = new ArrayList<PartitionInfo>();
		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return result;
			}
			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return result;
			}

			String sql = "SELECT * FROM db_partition WHERE class_name='"
					+ tableName.trim().toLowerCase() + "'";

			// [TOOLS-2425]Support shard broker
			sql = databaseInfo.wrapShardQuery(sql);

			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			String exprDataType = null;
			while (rs.next()) {
				String className = rs.getString("class_name");
				String partitionName = rs.getString("partition_name");
				String partitionClassName = rs.getString("partition_class_name");
				String partitionExpr = rs.getString("partition_expr");

				PartitionType partitionType = null;
				String partitionTypeStr = rs.getString("partition_type");
				if (partitionTypeStr.equalsIgnoreCase("HASH")) {
					partitionType = PartitionType.HASH;
				} else if (partitionTypeStr.equalsIgnoreCase("LIST")) {
					partitionType = PartitionType.LIST;
				} else if (partitionTypeStr.equalsIgnoreCase("RANGE")) {
					partitionType = PartitionType.RANGE;
				}

				List<String> partitionValues = new ArrayList<String>();
				if (partitionType != PartitionType.HASH) {
					Object obj = rs.getObject("partition_values");
					if (obj == null) {
						continue;
					}

					Object[] arr = (Object[]) obj;
					for (int i = 0, len = arr.length; i < len; i++) {
						if (arr[i] == null) {
							partitionValues.add(null);
						} else {
							partitionValues.add(arr[i].toString());
						}
					}
				}
				PartitionInfo partitionItem = new PartitionInfo(className,
						partitionName, partitionClassName, partitionType,
						partitionExpr, partitionValues, -1);
				if (isCommentSupport) {
					partitionItem.setDescription(rs.getString("comment"));
				}
				if (exprDataType == null && partitionExpr != null
						&& partitionExpr.trim().length() > 0) {
					exprDataType = getExprDataType(className, partitionExpr);
				}
				partitionItem.setPartitionExprType(exprDataType);
				result.add(partitionItem);
			}

			// counting rows
			int len = result.size();
			if (len > 0) {
				QueryUtil.freeQuery(stmt, rs);
				stmt = null;
				rs = null;
				StringBuilder qry = new StringBuilder();
				qry.append("SELECT ");
				for (int i = 0; i < len; i++) {
					qry.append(" SUM(DECODE(code, 'p").append(i + 1).append(
							"', cnt, 0)) ");
					if (i < len - 1) {
						qry.append(" , ");
					}
				}
				qry.append(" FROM ( ");
				for (int i = 0; i < len; i++) {
					PartitionInfo item = result.get(i);
					qry.append(" SELECT 'p").append(i + 1).append(
							"' AS code, COUNT(*) AS cnt ");
					qry.append(" from ").append(item.getPartitionClassName());
					if (i < len - 1) {
						qry.append(" UNION ALL ");
					}
				}
				qry.append(" ) t");
				stmt = connection.createStatement();
				rs = stmt.executeQuery(qry.toString());

				if (rs.next()) {
					for (int i = 0; i < len; i++) {
						PartitionInfo item = result.get(i);
						item.setRows(rs.getInt(i + 1));
					}
				}

			}

		} catch (SQLException e) {
			errorMsg = e.getMessage();
		} finally {
			finish();
		}

		return result;
	}

	/**
	 * 
	 * Get partition expression data type
	 * 
	 * @param tableName The table name
	 * @param expr The partition expression
	 * @return The string
	 */
	private String getExprDataType(String tableName, String expr) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT " + expr + " FROM " + QuerySyntax.escapeKeyword(tableName) + " WHERE ROWNUM = 1";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmt = rs.getMetaData();
			return rsmt.getColumnTypeName(1);
		} catch (SQLException e) {
			return null;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
	}

	/**
	 * Returning column statistic informations.
	 * 
	 * @param tableName String the given table name
	 * @return List<DBAttributeStatistic>
	 */
	public List<DBAttributeStatistic> getColumnStatistics(String tableName) {

		List<DBAttributeStatistic> list = null;

		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return null;
			}
			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return null;
			}

			list = new ArrayList<DBAttributeStatistic>();

			int cnt = 0;
			StringBuilder qry = new StringBuilder();
			qry.append("SELECT ");

			String sql = "SELECT attr_name, data_type FROM db_attribute WHERE class_name = '"
					+ tableName.trim().toLowerCase() + "'";
			connection.setAutoCommit(true);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String attrName = rs.getString("attr_name");
				String dataType = rs.getString("data_type");

				qry.append("'").append(attrName).append("',");
				qry.append("'").append(dataType).append("',");
				qry.append("MIN(").append(attrName).append("), ");
				qry.append("MAX(").append(attrName).append("), ");
				qry.append("COUNT(DISTINCT ").append(attrName).append("), ");
				cnt += 5;
			}
			qry.append("NULL FROM ").append(tableName);
			QueryUtil.freeQuery(stmt, rs);

			stmt = connection.createStatement();
			rs = stmt.executeQuery(qry.toString());
			if (rs.next()) {
				for (int i = 1; i < cnt; i += 5) {
					DBAttributeStatistic attr = new DBAttributeStatistic();
					attr.setName(rs.getString(i));
					attr.setType(rs.getString(i + 1));
					attr.setMinValue(rs.getString(i + 2));
					attr.setMaxValue(rs.getString(i + 3));
					attr.setValueDistinctCount(rs.getInt(i + 4));
					list.add(attr);
				}
			}

		} catch (SQLException e) {
			errorMsg = e.getMessage();
		} finally {
			finish();
		}

		return list;
	}

	/**
	 * Returning distinct values in table attribute
	 * 
	 * @param tableName String
	 * @param partitionExpr String
	 * @return String[]
	 */
	public String[] getDistinctValuesInAttribute(String tableName, String partitionExpr) {
		List<String> list = new ArrayList<String>();

		try {
			if (errorMsg != null && errorMsg.trim().length() > 0) {
				return null;
			}
			if (connection == null || connection.isClosed()) {
				errorMsg = Messages.error_getConnection;
				return null;
			}

			// The following query will failed, if you use the partitioning expression.
			// Then, Do not use double quote in attributeName statement.
			// String sql = "select distinct \""+attributeName+"\" from \""+tableName+"\"";
			String sql = "SELECT DISTINCT " + partitionExpr + " FROM " + QuerySyntax.escapeKeyword(tableName);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			errorMsg = e.getMessage();
		} finally {
			finish();
		}
		return list.toArray(new String[list.size()]);
	}

}
