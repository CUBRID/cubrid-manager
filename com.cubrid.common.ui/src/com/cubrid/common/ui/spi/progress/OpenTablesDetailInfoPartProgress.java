package com.cubrid.common.ui.spi.progress;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.NodeUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

public class OpenTablesDetailInfoPartProgress implements IRunnableWithProgress {
	private static final Logger LOGGER = LogUtil.getLogger(OpenTablesDetailInfoPartProgress.class);
	private final CubridDatabase database;
	private List<TableDetailInfo> tableList = null;
	private boolean success = false;

	public OpenTablesDetailInfoPartProgress (CubridDatabase database) {
		this.database = database;
	}

	public boolean loadUserSchemaList(Connection conn, Map<String, TableDetailInfo> tablesMap) { // FIXME move this logic to core module
		final int LIMIT_TABLE_COUNT = 500;

		StringBuilder sql = new StringBuilder()
		.append("SELECT \n")
		.append("    class_name, \n")
		.append("    class_type, \n")
		.append("    partitioned \n")
		.append("FROM \n")
		.append("    db_class \n")
		.append("WHERE \n")
		.append("    is_system_class = 'NO'");

		String query = sql.toString();

		// [TOOLS-2425]Support shard broker
		if (CubridDatabase.hasValidDatabaseInfo(database)) {
			query = database.getDatabaseInfo().wrapShardQuery(query);
		}

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			int recordsCount = rs.last() ? rs.getRow() : 0;
			if (recordsCount >= LIMIT_TABLE_COUNT) {
				rs.beforeFirst();
				final int NOT_EXECUTED_VALUE = -1;
				while (rs.next()) {
					String tableName = rs.getString(1);
					String classType = rs.getString(2);
					String partitioned = rs.getString(3);

					TableDetailInfo info = new TableDetailInfo();
					tablesMap.put(tableName, info);

					info.setTableName(tableName);
					info.setClassType(classType);
					info.setPartitioned(partitioned);
					info.setPkCount(NOT_EXECUTED_VALUE);
					info.setUkCount(NOT_EXECUTED_VALUE);
					info.setFkCount(NOT_EXECUTED_VALUE);
					info.setIndexCount(NOT_EXECUTED_VALUE);
				}
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		QueryUtil.freeQuery(stmt, rs);
		sql.setLength(0);
		sql.append("SELECT \n")
		.append("    c.class_name, \n")
		.append("    COUNT(*) AS count_column, \n")
		.append("    CAST(SUM(\n")
		.append("    CASE \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'BIGINT' THEN 8.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'INTEGER' THEN 4.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'SMALLINT' THEN 2.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'FLOAT' THEN 4.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'DOUBLE' THEN 8.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'MONETARY' THEN 12.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'STRING' THEN a.prec \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'VARCHAR' THEN a.prec \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'NVARCHAR' THEN a.prec \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'CHAR' THEN a.prec \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'NCHAR' THEN a.prec \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'TIMESTAMP' THEN 8.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'DATE' THEN 4.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'TIME' THEN 4.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'DATETIME' THEN 4.0 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'BIT' THEN FLOOR(prec / 8.0) \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'BIT VARYING' THEN FLOOR(prec / 8.0) \n")
		.append("          ELSE 0 \n")
		.append("    END ) AS BIGINT) AS size_column, \n")
		.append("    SUM(\n")
		.append("    CASE \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'STRING' THEN 1 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'VARCHAR' THEN 1 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'NVARCHAR' THEN 1 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'NCHAR' THEN 1 \n")
		.append("          WHEN \n")
		.append("              \"data_type\" = 'BIT VARYING' THEN 1 \n")
		.append("          ELSE 0 \n")
		.append("    END ) AS size_over_column, \n")
		.append("    MAX(c.class_type) AS class_type, \n")
		.append("    MAX(c.partitioned) AS partitioned \n")
		.append("FROM \n")
		.append("    db_class c, \n")
		.append("    db_attribute a \n")
		.append("WHERE \n")
		.append("    c.class_name = a.class_name \n")
		.append("    AND \n")
		.append("    c.is_system_class = 'NO' \n")
		.append("    AND \n")
		.append("    c.class_type = 'CLASS' \n")
		.append("    AND \n")
		.append("    a.from_class_name IS NULL \n")
		.append("GROUP BY c.class_name\n");

		query = sql.toString();

		// [TOOLS-2425]Support shard broker
		if (CubridDatabase.hasValidDatabaseInfo(database)) {
			query = database.getDatabaseInfo().wrapShardQuery(query);
		}

		stmt = null;
		rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String tableName = rs.getString(1);
				int countColumn = rs.getInt(2);
				BigDecimal recordsSize = rs.getBigDecimal(3);
				boolean columnOverSize = rs.getInt(4) > 0;
				String classType = rs.getString(5);
				String partitioned = rs.getString(6);

				TableDetailInfo info = null;
				if (tablesMap.containsKey(tableName)) {
					info = tablesMap.get(tableName);
				} else {
					info = new TableDetailInfo();
					tablesMap.put(tableName, info);
				}

				info.setTableName(tableName);
				info.setColumnsCount(countColumn);
				info.setRecordsSize(recordsSize);
				info.setHasUnCountColumnSize(columnOverSize);
				info.setClassType(classType);
				info.setPartitioned(partitioned);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		sql.setLength(0);
		sql.append("SELECT \n")
		.append("    c.class_name, \n")
		.append("    SUM(\n")
		.append("    CASE \n")
		.append("          WHEN \n")
		.append("              i.is_unique = 'YES' \n")
		.append("              AND \n")
		.append("              i.is_primary_key = 'NO' THEN 1 \n")
		.append("          ELSE 0 \n")
		.append("    END ) AS count_unique, \n")
		.append("    SUM(\n")
		.append("    CASE \n")
		.append("          WHEN \n")
		.append("              i.is_unique = 'YES' \n")
		.append("              AND \n")
		.append("              i.is_primary_key = 'YES' THEN 1 \n")
		.append("          ELSE 0 \n")
		.append("    END ) AS count_primary_key, \n")
		.append("    SUM(DECODE(i.is_foreign_key, 'YES', 1, 0)) AS count_foreign_key, \n")
		.append("    SUM(\n")
		.append("    CASE \n")
		.append("          WHEN \n")
		.append("              i.is_unique = 'NO' \n")
		.append("              AND \n")
		.append("              i.is_primary_key = 'NO' THEN 1 \n")
		.append("          ELSE 0 \n")
		.append("    END ) AS count_index \n")
		.append("FROM \n")
		.append("    db_class c, \n")
		.append("    db_index_key k, \n")
		.append("    db_index i \n")
		.append("WHERE \n")
		.append("    c.class_name = k.class_name \n")
		.append("    AND \n")
		.append("    k.class_name = i.class_name \n")
		.append("    AND \n")
		.append("    k.index_name = i.index_name \n")
		.append("    AND \n")
		.append("    c.class_type = 'CLASS' \n")
		.append("    AND \n")
		.append("    c.is_system_class = 'NO' \n")
		.append("    AND \n")
		.append("    i.key_count >= 1 \n")
		.append("    AND \n")
		.append("    NOT EXISTS (SELECT 1 FROM db_partition p WHERE c.class_name = LOWER(p.partition_class_name)) \n")
		.append("GROUP BY c.class_name;\n");
		query = sql.toString();

		// [TOOLS-2425]Support shard broker
		if (CubridDatabase.hasValidDatabaseInfo(database)) {
			query = database.getDatabaseInfo().wrapShardQuery(query);
		}

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String tableName = rs.getString(1);
				if (ConstantsUtil.isExtensionalSystemTable(tableName)) {
					continue;
				}
				int ukCount = rs.getInt(2);
				int pkCount = rs.getInt(3);
				int fkCount = rs.getInt(4);
				int indexCount = rs.getInt(5);

				TableDetailInfo info = null;
				if (tablesMap.containsKey(tableName)) {
					info = tablesMap.get(tableName);
				} else {
					info = new TableDetailInfo();
					tablesMap.put(tableName, info);
				}

				info.setTableName(tableName);
				info.setUkCount(ukCount);
				info.setPkCount(pkCount);
				info.setFkCount(fkCount);
				info.setIndexCount(indexCount);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}

		return true;
	}

	public void run(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {
		DatabaseInfo databaseInfo = NodeUtil.findDatabaseInfo(database);
		if (databaseInfo == null) {
			return;
		}

		Connection conn = null;
		try{
			monitor.setTaskName(Messages.tablesDetailInfoPartProgressTaskName);
			if (databaseInfo.getUserTableInfoList() == null) {
				return;
			}

			tableList = new ArrayList<TableDetailInfo>();
			Map<String, TableDetailInfo> map = new HashMap<String, TableDetailInfo>();

			conn = JDBCConnectionManager.getConnection(databaseInfo, true);
			if (!loadUserSchemaList(conn, map)) {
				success = false;
				return;
			}

			Set<String> tableNameSet = map.keySet();
			if (tableNameSet != null) {
				Map<String, SchemaComment> comments = null;
				if (SchemaCommentHandler.isInstalledMetaTable(databaseInfo, conn)) {
					try {
						comments = SchemaCommentHandler.loadTableDescriptions(databaseInfo, conn);
					} catch (SQLException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}

				List<String> tableNames = new ArrayList<String>();
				for (String tableName : tableNameSet) {
					tableNames.add(tableName);
				}

				Collections.sort(tableNames);
				for (String tableName : tableNames) {
					TableDetailInfo info = map.get(tableName);
					info.setRecordsCount(-1);

					SchemaComment cmt = SchemaCommentHandler.find(comments, tableName, null);
					if (cmt != null) {
						info.setTableDesc(cmt.getDescription());
					}

					tableList.add(info);
				}
			}

			success = true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			QueryUtil.freeQuery(conn);
		}
	}

	/**
	 * loadTablesInfo
	 *
	 * @return Catalog
	 */
	public void loadTablesInfo() {
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				try {
					new ProgressMonitorDialog(null).run(true, false,
							OpenTablesDetailInfoPartProgress.this);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
	}

	public List<TableDetailInfo> getList() {
		return tableList;
	}

	public boolean isSuccess() {
		return success;
	}
}
