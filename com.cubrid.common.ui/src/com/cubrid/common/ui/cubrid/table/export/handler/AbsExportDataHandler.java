/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.export.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.util.Closer;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.cubrid.table.event.handler.IExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.serial.task.GetSerialInfoListTask;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import com.cubrid.cubridmanager.core.cubrid.trigger.model.TriggerDDL;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.GetTriggerListTask;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerInfoTask;
import com.cubrid.jdbc.proxy.driver.CUBRIDBlobProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDClobProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * Abstract Export Data Handler
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-24 created by Kevin.Wang
 */
public abstract class AbsExportDataHandler {
	private static final Logger LOGGER = LogUtil.getLogger(AbsExportDataHandler.class);

	protected final int RSPAGESIZE = 99999;
	protected long REMAINING_MEMORY_SIZE = 100 * 1024 * 1024;
	protected int COMMIT_LINES = 1000;
	protected boolean isHasBigValue = false;
	protected boolean hasConfirmBigValue = false;
	protected volatile boolean isExit = false;
	protected volatile boolean stop = false;
	protected volatile boolean isPaginating = true;
	protected final DatabaseInfo dbInfo;
	protected final ExportConfig exportConfig;
	protected final IExportDataEventHandler exportDataEventHandler;
	protected final String CLOB_FOLDER_POSTFIX = "(CLOB)";
	protected final String BLOB_FOLDER_POSTFIX = "(BLOB)";
	
	public AbsExportDataHandler(DatabaseInfo dbInfo, ExportConfig exportConfig,
			IExportDataEventHandler exportDataEventHandler) {
		this.dbInfo = dbInfo;
		this.exportConfig = exportConfig;
		this.exportDataEventHandler = exportDataEventHandler;
	}

	public abstract void handle(String tableName) throws IOException, SQLException;

	/**
	 * Judge the sql whether paging sql
	 * @param tableName
	 * @param sql
	 * @param whereCondition
	 * @return
	 */
	protected boolean isPagination(String tableName, String sql, String whereCondition) {
		if (!exportConfig.isUsePagination()) {
			return false;
		}
		
		int rowNumPosition = sql.toUpperCase().indexOf("ROWNUM");
		int limitPosition = sql.toUpperCase().indexOf("LIMIT");
		
		if (rowNumPosition > 0 || limitPosition > 0) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * generate SQL with page setting
	 *
	 * @param sql
	 * @param beginIndex
	 * @param endIndex
	 * @param hasWhereCondition
	 * @return
	 */
	protected String getExecuteSQL(String sql, long beginIndex, long endIndex,
			String whereCondition) { // FIXME move this logic to core module
		if (sql == null) {
			return "";
		}
		String newSql = sql.trim();
		if (newSql.endsWith(";")) {
			newSql = newSql.substring(0, newSql.length() - 1);
		}
		StringBuilder sb = new StringBuilder(newSql);
		if (isPaginating) {
			if (!StringUtil.isEmpty(whereCondition)) {
				sb.append(" " + whereCondition).append(" AND ");
			} else {
				sb.append(" WHERE ");
			}
			sb.append("ROWNUM BETWEEN ").append(beginIndex).append(" AND ").append(endIndex);
		} else {
			if (! StringUtil.isEmpty(whereCondition)) {
				sb.append(" " + whereCondition);
			}
		}
		return sb.toString();
	}
	
	/**
	 * generate SQL without page setting
	 * 
	 * @param sql
	 * @param whereCondition
	 * @return
	 */
	protected String getExecuteSQL(String sql, String whereCondition) { // FIXME move this logic to core module
		if (sql == null) {
			return "";
		}
		
		String newSql = sql.trim();
		if (newSql.endsWith(";")) {
			newSql = newSql.substring(0, newSql.length() - 1);
		}
		StringBuilder sb = new StringBuilder(newSql);
		if (!StringUtil.isEmpty(whereCondition)) {
			sb.append(" " + whereCondition);
		}

		return sb.toString();
	}

	/**
	 * set IsHasBigValue
	 *
	 * @param columnType String
	 * @param precision int
	 */
	protected void setIsHasBigValue(String columnType, int precision) { // FIXME move this logic to core module
		if (!isHasBigValue) {
			isHasBigValue = FieldHandlerUtils.isBitValue(columnType, precision);
		}
	}

	protected String getSelectSQL(Connection conn, String name) {
		String sql = null;
		if (exportConfig.getSQL(name) != null) {
			sql = exportConfig.getSQL(name);
		} else {
			sql = QueryUtil.getSelectSQL(conn, name);
		}
		return sql;
	}

	protected Connection getConnection() throws SQLException { // FIXME move this logic to core module
		return JDBCConnectionManager.getConnection(dbInfo, false);
	}


	public synchronized void setStop(boolean stop) {
		this.stop = stop;
	}

	protected CUBRIDPreparedStatementProxy getStatement(Connection conn, String sql,
			String tableName) throws SQLException { // FIXME move this logic to core module
		CUBRIDPreparedStatementProxy stmt = QueryExecuter.getStatement(conn, sql, false, true);
		List<PstmtParameter> pstmList = exportConfig.getParameterList(tableName);
		if (pstmList != null) {
			String charset = dbInfo.getCharSet();
			for (PstmtParameter pstmtParameter : pstmList) {
				FieldHandlerUtils.setPreparedStatementValue(pstmtParameter, stmt, charset);
			}
		}

		return stmt;
	}

	/**
	 * Judge is has next page
	 *
	 * @param beginIndex
	 * @param totalCount
	 * @return
	 */
	protected boolean hasNextPage(long beginIndex, long totalCount) {
		if (stop) {
			return false;
		}
		if (totalCount > 0 && beginIndex <= totalCount && isPaginating) {
			return true;
		}
		return false;
	}

	/**
	 * Export clob data to file
	 *
	 * @param folderName
	 * @param rs
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 */
	protected String exportClobData(String folderName, CUBRIDResultSetProxy rs, int columnIndex) throws SQLException {
		Clob clob = rs.getClob(columnIndex);
		return exportClobData(folderName, clob);
	}
	
	/**
	 * Export clob data to file
	 *
	 * @param folderName
	 * @param clob
	 * @return
	 * @throws SQLException
	 */
	protected String exportClobData(String folderName, Clob clob) throws SQLException {
		if (!exportConfig.isExportLob() || clob == null) { // FIXME move this logic to core module
			return null;
		}
		if(clob instanceof CUBRIDClobProxy){
			CUBRIDClobProxy ccp = (CUBRIDClobProxy)clob;
			if(ccp.getProxyObj() == null){
				return null;
			}
		}
		String fileName = null;
		Writer writer = null;
		Reader reader = null;
		try {
			File folder = new File(exportConfig.getDataFileFolder() + File.separator + folderName
					+ CLOB_FOLDER_POSTFIX);
			if (!folder.exists()) {
				folder.mkdir();
			}
			File file = createLobFile(folder.getAbsolutePath());
			fileName = file.getName();
			writer = new FileWriter(file);
			reader = clob.getCharacterStream();
			char[] buf = new char[1024];
			int len = reader.read(buf);
			while (len != -1) {
				writer.write(buf);
				len = reader.read(buf);
			}
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage());
		} finally {
			Closer.close(writer);
			Closer.close(reader);
		}
		return fileName;
	}

	/**
	 * Export blob data to file
	 *
	 * @param folderName
	 * @param blob
	 * @return
	 * @throws SQLException
	 */
	protected String exportBlobData(String folderName, Blob blob) throws SQLException {
		if (!exportConfig.isExportLob() || blob == null) { // FIXME move this logic to core module
			return null;
		}
		if(blob instanceof CUBRIDBlobProxy){
			CUBRIDBlobProxy cbp = (CUBRIDBlobProxy)blob;
			if(cbp.getProxyObj() == null){
				return null;
			}
		}
		String fileName = null;
		OutputStream writer = null;
		InputStream reader = null;
		try {
			File folder = new File(exportConfig.getDataFileFolder() + File.separator + folderName
					+ BLOB_FOLDER_POSTFIX);
			if (!folder.exists()) {
				folder.mkdir();
			}
			File file = createLobFile(folder.getAbsolutePath());
			fileName = file.getName();
			writer = new FileOutputStream(file);
			if (blob != null) {
				reader = blob.getBinaryStream();
				byte[] buf = new byte[1024];
				int len = reader.read(buf);
				while (len != -1) {
					writer.write(buf);
					len = reader.read(buf);
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage());
		} finally {
			Closer.close(writer);
			Closer.close(reader);
		}
		return fileName;
	}
	
	/**
	 * Export blob data to file
	 *
	 * @param folderName
	 * @param rs
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 */
	protected String exportBlobData(String folderName, CUBRIDResultSetProxy rs, int columnIndex) throws SQLException {
		Blob blob = rs.getBlob(columnIndex);
		return exportBlobData(folderName, blob);
	}

	/**
	 * Create a unique file
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private File createLobFile(String path) throws IOException { // FIXME move this logic to core module
		File file = null;
		String fileName = String.valueOf(System.nanoTime());
		synchronized (this) {
			file = new File(path + File.separator + fileName);
			while (file.exists()) {
				fileName = String.valueOf(System.nanoTime());
				file = new File(path + File.separator + fileName);
			}
			file.createNewFile();
		}

		return file;
	}

	/**
	 * Get the buffered writer
	 *
	 * @param file String
	 * @param fileCharset String
	 * @return BufferedWriter
	 * @throws UnsupportedEncodingException The exception
	 * @throws FileNotFoundException The exception
	 */
	protected static BufferedWriter getBufferedWriter(String file, String fileCharset) throws UnsupportedEncodingException,
			FileNotFoundException { // FIXME move this logic to core module
		BufferedWriter fs = null;
		if (fileCharset != null && fileCharset.trim().length() > 0) {
			fs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
					fileCharset.trim()));
		} else {
			fs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		}

		return fs;
	}

	/**
	 * Export the schema and index DDL to file
	 *
	 * @param databaseInfo DatabaseInfo
	 * @param exportDataEventHandler IExportDataEventHandler
	 * @param tableNameList Set<String>
	 * @param schemaFile String
	 * @param indexFile String
	 * @param fileCharset String
	 * @param exportStartValue whether keep start value
	 * @throws SQLException The SQL exception
	 * @throws IOException The IO exception
	 */
	public static void exportSchemaToOBSFile(DatabaseInfo databaseInfo,
			IExportDataEventHandler exportDataEventHandler, Set<String> tableNameList,
			String schemaFile, String indexFile, String triggerFile, String fileCharset,
			boolean exportStartValue, boolean isLoadDB) throws SQLException,
			IOException { // FIXME move this logic to core module
		if (schemaFile == null && indexFile == null) {
			return;
		}

		Connection conn = null;
		BufferedWriter schemaWriter = null;
		BufferedWriter indexWriter = null;
		BufferedWriter triggerWriter = null;
		LinkedList<SchemaInfo> schemaInfoList = null;
		List<Trigger> triggerList = null;
		boolean hasDataInIndexFile = false;
		try {
			if (schemaFile != null) {
				schemaWriter = getBufferedWriter(schemaFile, fileCharset);
			}

			if (indexFile != null) {
				indexWriter = getBufferedWriter(indexFile, fileCharset);
				schemaInfoList = new LinkedList<SchemaInfo>();
			}

			if (triggerFile != null) {
				triggerWriter = getBufferedWriter(triggerFile, fileCharset);
				triggerList = new ArrayList<Trigger>();
			}

			SchemaDDL schemaDDL = new SchemaDDL(new SchemaChangeManager(databaseInfo, true),
					databaseInfo);
			conn = JDBCConnectionManager.getConnection(databaseInfo, false);

			for (String tableName : tableNameList) {
				SchemaInfo schemaInfo = databaseInfo.getSchemaInfo(conn, tableName);
				if (schemaInfo == null) {
					continue;
				}
				//write the create DDL
				if (schemaWriter == null) {
					continue;
				}
				String ddl = schemaDDL.getSchemaDDL(schemaInfo, false);
				if (ddl != null && ddl.trim().length() > 0) {
					schemaWriter.write(ddl);
					schemaWriter.write(StringUtil.NEWLINE);
				}
				schemaWriter.flush();
				if (exportStartValue) {
					List<SerialInfo> autoIncrementList = schemaDDL.getAutoIncrementList(schemaInfo);
					if (autoIncrementList.size() > 0) {
						for (SerialInfo serialInfo : autoIncrementList) {
							String serialName = getSerailName(tableName, serialInfo.getAttName());
							String currentValue = getCurrentValueFromSystemTable(serialName,
									databaseInfo, conn);
							String alterStartValueDDL = schemaDDL.getAlterSerialStartValueDDL(
									serialName, currentValue);
							schemaWriter.write(alterStartValueDDL);
							schemaWriter.write(StringUtil.NEWLINE);
						}
					}
				}

				if (indexWriter == null) {
					continue;
				}

				schemaInfoList.add(schemaInfo);
			}

			// TOOLS-4299, write the serial to the schema file
			if (isLoadDB) {
				GetSerialInfoListTask task = new GetSerialInfoListTask(databaseInfo);
				task.execute();
				boolean isSupportCache = CompatibleUtil.isSupportCache(databaseInfo);
				for (SerialInfo serial : task.getSerialInfoList()) {
					schemaWriter.write(QueryUtil.createSerialSQLScript(serial, isSupportCache));
					schemaWriter.write(StringUtil.NEWLINE);
				}
				schemaWriter.flush();
			}

			// write PKs, indexes to a file
			if (schemaInfoList != null) {
				String ddl = null;
				// write pk
				for (SchemaInfo schemaInfo : schemaInfoList) {
					ddl = schemaDDL.getPKsDDL(schemaInfo);
					if (ddl != null && ddl.trim().length() > 0) {
						indexWriter.write(ddl.trim());
						indexWriter.newLine();
						hasDataInIndexFile = true;
					}
				}
				if (hasDataInIndexFile) {
					indexWriter.newLine();
				}

				// write index
				for (SchemaInfo schemaInfo : schemaInfoList) {
					ddl = schemaDDL.getIndexsDDL(schemaInfo);
					if (ddl != null && ddl.trim().length() > 0) {
						indexWriter.write(ddl.trim());
						indexWriter.newLine();
						hasDataInIndexFile = true;
					}
				}

				if (hasDataInIndexFile) {
					indexWriter.newLine();
				}

				//write fk
				for (SchemaInfo schemaInfo : schemaInfoList) {
					ddl = schemaDDL.getFKsDDL(schemaInfo);
					if (ddl != null && ddl.trim().length() > 0) {
						indexWriter.write(ddl.trim());
						indexWriter.newLine();
						hasDataInIndexFile = true;
					}
				}
				if (hasDataInIndexFile) {
					indexWriter.flush();
				}
			}

			// TOOLS-4299 export the triggers
			if (triggerList != null) {
				GetTriggerListTask triggerNameTask = new GetTriggerListTask(databaseInfo.getServerInfo());
				triggerNameTask.setDbName(databaseInfo.getDbName());
				triggerNameTask.execute();
				triggerList = triggerNameTask.getTriggerInfoList();
				for (Trigger t: triggerList) {
					triggerWriter.write(TriggerDDL.getDDL(t));
					triggerWriter.write(StringUtil.NEWLINE);
				}
				triggerWriter.flush();
			}
		} finally {
			QueryUtil.freeQuery(conn);
			FileUtil.close(schemaWriter);
			FileUtil.close(indexWriter);
			FileUtil.close(triggerWriter);
			if (!hasDataInIndexFile) {
				FileUtil.delete(indexFile);
			}
		}
	}

	/**
	 * get current value from system table
	 *
	 * @param serialName
	 * @param conn
	 * @return
	 */
	public static String getCurrentValueFromSystemTable(String serialName, DatabaseInfo dbInfo,
			Connection conn) { // FIXME move this logic to core module
		Statement stmt = null;
		CUBRIDResultSetProxy rs = null;

		StringBuilder sqlbuf = new StringBuilder();
		sqlbuf.append("SELECT ");
		sqlbuf.append(QuerySyntax.escapeKeyword("current_val")).append(", ");
		sqlbuf.append(QuerySyntax.escapeKeyword("increment_val")).append(", ");
		sqlbuf.append(QuerySyntax.escapeKeyword("started"));
		sqlbuf.append(" FROM db_serial WHERE name = '").append(serialName).append("'");

		String sql = DatabaseInfo.wrapShardQuery(dbInfo, sqlbuf.toString());

		String currentStringValue = "1";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);

			rs = (CUBRIDResultSetProxy) stmt.executeQuery(sql.toString());
			if (rs.next()) {
				currentStringValue = rs.getString(1);
				String incrementStringValue = rs.getString(2);
				String started = rs.getString(3);
				//Because first insert value does not use current value ,so add incrementValue as current value
				if (StringUtil.isNotEmpty(incrementStringValue) && "1".equals(started)) {
					double currentValue = Double.valueOf(currentStringValue);
					double incrementValue = Double.valueOf(incrementStringValue);
					currentValue += incrementValue;
					currentStringValue = new DecimalFormat("#").format(currentValue);
				}
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(stmt, rs);
		}
		return currentStringValue;
	}

	/**
	 * get SerailName by table and auto increment column name
	 *
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public static String getSerailName(String tableName, String columnName) { // FIXME move this logic to core module
		return tableName + "_ai_" + columnName;
	}

	protected String getFixFileName(String filePath, int number) { // FIXME move this logic to core module
		if (number > 0) {
			int index = filePath.lastIndexOf(".");
			if (index > 0) {
				String prePath = filePath.substring(0, index) + "(" + number + ")";
				filePath = prePath + filePath.substring(index);
			} else {
				filePath = filePath + "(" + number + ")";
			}
		}
		return filePath;
	}
}
