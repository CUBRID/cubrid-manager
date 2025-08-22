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
package com.cubrid.common.ui.query.tuner;

import com.cubrid.common.core.queryplan.StructQueryPlan;
import com.cubrid.common.core.util.CubridUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.query.control.QueryInfo;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;

/**
 * Query job used for Query tuner
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-11 created by Kevin.Wang
 */
public class QueryTunerJob extends Job implements IQueryJob {

    private static final Logger LOGGER = LogUtil.getLogger(QueryTunerJob.class);

    private DatabaseInfo databaseInfo;
    private Connection connection;
    private CUBRIDPreparedStatementProxy stmt;
    private CUBRIDResultSetProxy resultSet;
    private List<String> queryList;

    private int queryType;
    private List<QueryRecord> queryRecordList = new ArrayList<QueryRecord>();
    private NumberFormat nf = NumberFormat.getInstance();

    private IQueryChangeListener queryChangeListener;
    private IRecordProcessor recordProcessor;

    /**
     * The constructor
     *
     * @param name
     * @param databaseInfo
     */
    public QueryTunerJob(
            int queryType,
            DatabaseInfo databaseInfo,
            List<String> queryList,
            IRecordProcessor recordProcessor,
            IQueryChangeListener queryChangeListener) {
        super("Running");
        this.queryType = queryType;
        this.databaseInfo = databaseInfo;
        this.queryList = queryList;
        this.recordProcessor = recordProcessor;
        this.queryChangeListener = queryChangeListener;

        nf.setMaximumFractionDigits(3);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run(IProgressMonitor arg0) { // FIXME move this logic to core module
        if (!initConnection()) {
            return Status.CANCEL_STATUS;
        }

        ServerInfo serverInfo =
                databaseInfo.getServerInfo() == null ? null : databaseInfo.getServerInfo();

        for (String query : queryList) {
            long startTime = System.currentTimeMillis();
            if (query != null && query.trim().lastIndexOf(";") == -1) {
                query += ";";
            }
            fireQueryChanged(query, QueryEvent.QUERY_START);
            String sql = getWrapedSQL(query);

            if ((queryType & IQueryJob.COLLECT_STAT) > 0) {
                beginCollectExecStats();
            }

            try {
                stmt =
                        getStatement(
                                connection, sql, (queryType & IQueryJob.INCLUDE_OID) > 0, false);
                //				if (parameterList != null) {
                //					String charset = getSelectedDatabase().getDatabaseInfo().getCharSet();
                //					for (PstmtParameter pstmtParameter : parameterList) {
                //						FieldHandlerUtils.setPreparedStatementValue(pstmtParameter, stmt, charset);
                //					}
                //				}
            } catch (final SQLException ee) {
                LOGGER.error("", ee);
                queryRecordList.add(
                        new QueryRecord(
                                query, startTime, System.currentTimeMillis(), new Date(), ee));

                fireQueryChanged(query, QueryEvent.QUERY_FAILED);
                continue;
            }

            QueryRecord queryRecord = new QueryRecord(query, startTime, new Date());
            /*Run the query*/
            if ((queryType & IQueryJob.RUN_QUERY) > 0) {
                try {
                    if (stmt.hasResultSet()) {
                        stmt.setQueryInfo(false);
                        stmt.setOnlyQueryPlan(false);
                        stmt.executeQuery();
                        resultSet = (CUBRIDResultSetProxy) stmt.getResultSet();
                        queryRecord.setColumnInfoList(getColumnData(resultSet));

                        QueryInfo queryInfo = new QueryInfo();
                        queryRecord.setQueryInfo(queryInfo);
                        recordProcessor.process(resultSet, queryRecord);
                    } else {
                        int threadExecResult = 0;
                        threadExecResult = stmt.executeUpdate();
                        queryRecord.setThreadExecResult(threadExecResult);
                    }
                } catch (final SQLException ee) {
                    LOGGER.error(ee.getMessage(), ee);
                    queryRecord.setErrorException(ee);
                }
            }

            /*Run the query plan*/
            if ((queryType & IQueryJob.RUN_PLAN) > 0) {
                StructQueryPlan queryPlan = null;
                try {
                    stmt.setOnlyQueryPlan(true);
                    String plan = stmt.getQueryplan(sql);
                    queryPlan = new StructQueryPlan(sql, plan, new Date());
                    queryRecord.setQueryPlan(queryPlan);
                } catch (final SQLException ee) {
                    LOGGER.error(ee.getMessage(), ee);
                    queryRecord.setErrorException(ee);
                }
            }

            if ((queryType & IQueryJob.COLLECT_STAT) > 0) {
                queryRecord.setStatistics(CubridUtil.fetchStatistics(connection));
            } else {
                queryRecord.setStatistics(CubridUtil.makeBlankStatistics());
            }

            queryRecord.setStopTime(System.currentTimeMillis());
            queryRecordList.add(queryRecord);

            QueryUtil.freeQuery(stmt, resultSet);
            fireQueryChanged(query, QueryEvent.QUERY_FINISH);
        }
        /*Free the resource*/
        QueryUtil.freeQuery(connection);

        fireQueryChanged("", QueryEvent.QUERY_FINISH_ALL);

        return Status.OK_STATUS;
    }

    private String getWrapedSQL(String query) { // FIXME move this logic to core module
        if (databaseInfo.isShard()) {
            Pattern idPattern =
                    Pattern.compile(
                            QueryUtil.REGEX_PATTERN_SHARD_ID,
                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            Matcher idMatcher = idPattern.matcher(query);

            Pattern valPattern =
                    Pattern.compile(
                            QueryUtil.REGEX_PATTERN_SHARD_VAL,
                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            Matcher valMatcher = valPattern.matcher(query);

            if (!idMatcher.find() && !valMatcher.find()) {
                if (databaseInfo.getShardQueryType() == DatabaseInfo.SHARD_QUERY_TYPE_ID) {
                    query = QueryUtil.wrapShardQueryWithId(query, databaseInfo.getCurrentShardId());
                } else {
                    query =
                            QueryUtil.wrapShardQueryWithVal(
                                    query, databaseInfo.getCurrentShardVal());
                }
            }
        }
        return query;
    }

    /**
     * Initialize the query database
     *
     * @param database CubridDatabase
     * @return boolean
     */
    private boolean initConnection() { // FIXME move this logic to core module
        try {
            if (connection != null) {
                QueryUtil.freeQuery(connection);
            }
            connection =
                    JDBCConnectionManager.getConnection(
                            databaseInfo, (queryType & IQueryJob.AUTO_COMMIT) > 0);
        } catch (SQLException event) {
            String errorMsg = Messages.errDbConnect;
            if (event.getMessage() != null) {
                errorMsg =
                        Messages.bind(
                                com.cubrid.common.ui.common.Messages.errCommonTip,
                                event.getErrorCode(),
                                event.getMessage());
            }
            LOGGER.error(errorMsg);
            CommonUITool.openErrorBox(errorMsg);

            return false;
        }
        return true;
    }

    private void beginCollectExecStats() {
        Statement stmt = null;
        try {
            String sql = "SET @collect_exec_stats = 0;";
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            QueryUtil.freeQuery(stmt);

            sql = "SET @collect_exec_stats = 1;";
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception ignored) {
        } finally {
            QueryUtil.freeQuery(stmt);
        }
    }

    /**
     * Init table column data
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<ColumnInfo> getColumnData(CUBRIDResultSetProxy rs)
            throws SQLException { // FIXME move this logic to core module
        List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();
        CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();
        int cntColumn = rsmt.getColumnCount();

        for (int i = 1; i <= cntColumn; i++) {
            String columnName = rsmt.getColumnName(i);
            String typeName = StringUtil.nvl(rsmt.getColumnTypeName(i));
            int scale = rsmt.getScale(i);
            int precision = rsmt.getPrecision(i);
            String elementTypeName = StringUtil.nvl(rsmt.getElementTypeName(i));
            if (typeName.length() == 0) {
                int typeIndex = rsmt.getColumnType(i);
                switch (typeIndex) {
                    case Types.BLOB:
                        typeName = DataType.DATATYPE_BLOB;
                        break;
                    case Types.CLOB:
                        typeName = DataType.DATATYPE_CLOB;
                        break;
                    default:
                        typeName = "";
                }
            }
            String columnType = typeName.toUpperCase(Locale.getDefault());
            String elementType = elementTypeName.toUpperCase(Locale.getDefault());
            ColumnInfo colInfo =
                    new ColumnInfo(
                            String.valueOf(i),
                            columnName,
                            columnType,
                            elementType,
                            precision,
                            scale);
            columnInfoList.add(colInfo);
        }

        return columnInfoList;
    }

    private void fireQueryChanged(String query, int eventType) {
        if (queryChangeListener != null) {
            queryChangeListener.queryChanged(new QueryEvent(databaseInfo, query, eventType));
        }
    }

    /**
     * Get statement for doing with the connection time out
     *
     * @param conn Connection
     * @param sql String
     * @param doesGetOidInfo boolean
     * @param isSecond boolean check connection time out
     * @return CUBRIDPreparedStatementProxy
     * @throws SQLException The SQL exception
     */
    private CUBRIDPreparedStatementProxy getStatement(
            Connection conn, String sql, boolean doesGetOidInfo, boolean isSecond)
            throws SQLException { // FIXME move this logic to core module
        try {
            CUBRIDPreparedStatementProxy stmt =
                    (CUBRIDPreparedStatementProxy)
                            conn.prepareStatement(
                                    sql,
                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    doesGetOidInfo
                                            ? ResultSet.CONCUR_UPDATABLE
                                            : ResultSet.CONCUR_READ_ONLY,
                                    ResultSet.HOLD_CURSORS_OVER_COMMIT);
            return stmt;
        } catch (SQLException e) {
            if (!isSecond && (e.getErrorCode() == -2003 || e.getErrorCode() == -1003)) {
                return getStatement(conn, sql, doesGetOidInfo, true);
            } else {
                throw e;
            }
        }
    }

    /** @return the queryRecordList */
    public List<QueryRecord> getQueryRecordList() {
        return queryRecordList;
    }
}
