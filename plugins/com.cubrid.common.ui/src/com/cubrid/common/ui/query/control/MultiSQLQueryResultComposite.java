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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.persist.RecentlyUsedSQLDetailPersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.UIQueryUtil;
import com.cubrid.cubridmanager.core.common.jdbc.DBConnection;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.jdbc.proxy.driver.CUBRIDCommandType;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * Mulitiple SQL query
 *
 * @author fulei
 * @version 1.0 - 2012-9-24 created by fulei
 * @version 1.1 - 2013-03-31 updated by Ray Yin
 */
public class MultiSQLQueryResultComposite extends Composite implements IMultiQueryExecuter {
    private static final Logger LOGGER = LogUtil.getLogger(MultiSQLQueryResultComposite.class);
    private QueryEditorPart editor;
    private List<String> sqlList = new ArrayList<String>();
    private final CubridDatabase cubridDatabase;
    public LinkedHashMap<String, QueryResultComposite> sqlResultMap =
            new LinkedHashMap<String, QueryResultComposite>();
    private int assignIndex = 0;
    private static int[] perCompInShell = {9, 4};
    private ArrayList<DBConnection> connectionList = new ArrayList<DBConnection>();
    private Map<String, QueryExecuter> queryExecuterMap = new HashMap<String, QueryExecuter>();
    private Map<String, QueryResultComposite> compositeMap =
            new HashMap<String, QueryResultComposite>();
    private Map<String, StringBuilder> logsMap = new HashMap<String, StringBuilder>();
    private String baseSql = null;

    public MultiSQLQueryResultComposite(
            Composite parent,
            int style,
            QueryEditorPart editor,
            CubridDatabase cubridDatabase,
            String queries) {
        super(parent, style);
        this.editor = editor;
        this.cubridDatabase = cubridDatabase;
        final Vector<String> qVector = QueryUtil.queriesToQuery(queries);
        int i = 1;
        for (String sql : qVector) {
            String sqlWithIndex = "/*SQL#" + i + "*/\n" + sql;
            this.sqlList.add(sqlWithIndex);
            i++;
        }
    }

    public SashForm createSashForm(Composite parent, int type, int column) {
        SashForm resultSashForm = new SashForm(parent, type);
        resultSashForm.setLayout(new GridLayout(column, true));
        resultSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return resultSashForm;
    }

    public void createResultQueryResultComposite(SashForm parentForm, String sql) {
        if (sql == null) {
            createBlankResultComposite(parentForm);
            return;
        }
        CTabFolder folder1 = new CTabFolder(parentForm, SWT.TOP);
        folder1.setSimple(false);
        folder1.setUnselectedImageVisible(true);
        folder1.setUnselectedCloseVisible(true);
        folder1.setSelectionBackground(CombinedQueryEditorComposite.BACK_COLOR);
        folder1.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
        folder1.setLayout(new GridLayout(1, true));
        folder1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        QueryResultComposite comp = new QueryResultComposite(folder1, true, this.editor);
        int titleLength = 30;
        String title = sql.length() > titleLength ? sql.substring(0, titleLength) + "..." : sql;
        comp.setQueryResultTabItemName(title);
        comp.setCanDispose(true);
        sqlResultMap.put(sql, comp);
    }

    public void createBlankResultComposite(SashForm parentForm) {
        Composite comp = new Composite(parentForm, SWT.NONE);
        comp.setLayout(new GridLayout());
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    public void initialize() {
        // count column row count and weights
        int columnCount = (int) Math.ceil(Math.sqrt((double) sqlList.size()));
        int rowCount = (int) Math.ceil(((double) sqlList.size() / (double) columnCount));

        int[] columnWeights = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnWeights[i] = 100 / columnCount;
        }
        int[] rowWeights = new int[rowCount];
        for (int i = 0; i < rowCount; i++) {
            rowWeights[i] = 100 / rowCount;
        }

        // create row
        SashForm horizontalMainForm = createSashForm(this, SWT.VERTICAL, columnCount);
        List<SashForm> verticalFormList = new ArrayList<SashForm>();
        for (int i = 0; i < rowCount; i++) {
            SashForm verticalForm = createSashForm(horizontalMainForm, SWT.HORIZONTAL, rowCount);
            verticalFormList.add(verticalForm);
        }
        setWeightsToSashForm(horizontalMainForm, rowWeights);

        // create column
        for (int i = 0; i < verticalFormList.size(); i++) {
            SashForm verticalForm = verticalFormList.get(i);
            for (int j = 0; j < columnCount; j++) {
                createResultQueryResultComposite(verticalForm, getAssignSQL());
            }
            setWeightsToSashForm(verticalForm, columnWeights);
        }

        assignIndex = 0;
    }

    public void setWeightsToSashForm(SashForm form, int[] weights) {
        form.setWeights(weights);
    }

    public void runQueries() { // FIXME move this logic to core module
        int index = 0;
        for (Map.Entry<String, QueryResultComposite> entry : sqlResultMap.entrySet()) {
            String sql = entry.getKey();
            if (index == 0) {
                baseSql = sql;
            }
            QueryResultComposite queryResultComp = entry.getValue();
            MultiQueryThread queryThread =
                    new MultiQueryThread(sql, editor, cubridDatabase, queryResultComp);
            makeProgressBar(queryThread, cubridDatabase, queryResultComp);
            queryThread.run();
            compositeMap.put(sql, queryResultComp);
            queryExecuterMap.put(sql, queryThread.getResult());
            logsMap.put(sql, queryThread.getLogs());
            index++;
        }

        displayResults();
    }

    public Composite getControl() {
        return this;
    }
    /** Display Multiple Query Results */
    public void displayResults() {
        QueryExecuter baseQueryExecuter = (QueryExecuter) queryExecuterMap.get(baseSql);
        Set<String> sqlSet = queryExecuterMap.keySet();

        for (final String sql : sqlSet) {
            final QueryResultComposite queryResultComp = compositeMap.get(sql);
            final QueryExecuter queryExecuter = (QueryExecuter) queryExecuterMap.get(sql);
            final StringBuilder logs = (StringBuilder) logsMap.get(sql);

            queryResultComp.setMultiResultsCompare(true);
            if (sql.equals(baseSql)) {
                queryResultComp.setBaseQueryExecuter(null);
            } else {
                queryResultComp.setBaseQueryExecuter(baseQueryExecuter);
            }

            Display.getDefault()
                    .syncExec(
                            new Runnable() {
                                public void run() {
                                    final String logsBak = logs.toString();
                                    CTabFolder queryResultTabFolder =
                                            queryResultComp.getQueryResultTabFolder();
                                    if (queryResultTabFolder != null
                                            && !queryResultTabFolder.isDisposed()) {
                                        queryResultComp.disposeAllResult();
                                        queryResultComp.disposeTabResult();
                                        if (logsBak.trim().length() > 0) {
                                            queryResultComp.makeLogResult("", logsBak);
                                        } else {
                                            queryResultComp.makeMultiQueryResult(queryExecuter);
                                        }
                                        if (queryResultTabFolder.getItemCount() > 0) {
                                            queryResultTabFolder.setSelection(
                                                    queryResultTabFolder.getItemCount() - 1);
                                        }
                                    }
                                }
                            });
        }
    }

    /** Make the progress bar */
    private void makeProgressBar(
            final MultiQueryThread queryThread,
            CubridDatabase db,
            QueryResultComposite queryResultComp) {
        Runnable runable =
                new Runnable() {
                    public void run() {
                        if (queryThread != null) {
                            try {
                                if (queryThread.getResult() != null) {
                                    queryThread.getResult().dispose();
                                }
                                if (queryThread.getStmt() != null) {
                                    queryThread.getStmt().cancel();
                                }
                            } catch (SQLException e) {
                                LOGGER.error("", e);
                            }
                            QueryUtil.freeQuery(queryThread.getStmt(), queryThread.getRs());
                        }
                    }
                };
        queryResultComp.makeProgressBar(runable, db.getServer().getServerInfo());
    }

    public String getAssignSQL() {
        if (assignIndex > sqlList.size() - 1) {
            return null;
        }
        return sqlList.get(assignIndex++);
    }

    /** according to selected database,parse the result shell */
    public static List<List<CubridDatabase>> parseMultiQueryResult(
            List<CubridDatabase> sqlList) { // FIXME move this logic to core module
        List<List<CubridDatabase>> resultList = new ArrayList<List<CubridDatabase>>();
        for (int count : perCompInShell) {
            if (sqlList.size() >= count) {
                for (int i = 0; i < sqlList.size(); i = i + count) {
                    List<CubridDatabase> oneShellsqlList = new ArrayList<CubridDatabase>();
                    for (int j = i, x = 0; x < count && j < sqlList.size(); j++, x++) {
                        oneShellsqlList.add(sqlList.get(j));
                    }
                    resultList.add(oneShellsqlList);
                }
                break;
            }
        }
        return resultList;
    }

    /**
     * execute multiple database query operation
     *
     * @author fulei 2012-05-21
     */
    class MultiQueryThread implements Runnable {
        private final QueryEditorPart queryEditor;
        private DBConnection connection;
        private CubridDatabase database;
        final Vector<String> qVector = new Vector<String>();
        private QueryExecuter result = null;
        public CUBRIDPreparedStatementProxy stmt = null;
        private CUBRIDResultSetProxy rs = null;

        @SuppressWarnings("unused")
        private int line;

        private final String sql;
        private QueryResultComposite queryResultComp = null;
        private StringBuilder logs = new StringBuilder(1024);

        public CUBRIDPreparedStatementProxy getStmt() {
            return stmt;
        }

        public CUBRIDResultSetProxy getRs() {
            return rs;
        }

        public QueryExecuter getResult() {
            return result;
        }

        public QueryEditorPart getQueryEditor() {
            return queryEditor;
        }

        public QueryResultComposite getQueryResultComposite() {
            return queryResultComp;
        }

        public StringBuilder getLogs() {
            return logs;
        }

        public MultiQueryThread(
                String sql,
                QueryEditorPart part,
                CubridDatabase database,
                QueryResultComposite queryResultComp) {
            this.queryEditor = part;
            this.database = database;
            this.queryResultComp = queryResultComp;
            this.sql = sql;
            qVector.add(sql);
        }

        public QueryExecuter createQueryExecutor(
                QueryEditorPart queryEditor,
                int cntResults,
                String sql,
                CubridDatabase database,
                String orignSQL,
                List<String> columnTableNameList) {
            QueryExecuter queryExecuter =
                    new QueryExecuter(
                            queryEditor, cntResults, sql, database, connection, null, orignSQL);
            if (columnTableNameList != null) {
                queryExecuter.setColumnTableNames(columnTableNameList);
            }
            return queryExecuter;
        }

        /** Execute query */
        public void run() { // FIXME move this logic to core module
            try {
                if (connection == null) {
                    connection = new DBConnection(database.getDatabaseInfo());
                }
                connection.checkAndConnect();
                connection.setAutoClosable(true);
                connectionList.add(connection); // add list when dispose composite, disconnect all
            } catch (final SQLException event) {
                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        String errorMsg = Messages.errDbConnect;
                                        if (event.getMessage() != null) {
                                            errorMsg =
                                                    Messages.bind(
                                                            com.cubrid.common.ui.common.Messages
                                                                    .errCommonTip,
                                                            event.getErrorCode(),
                                                            event.getMessage());
                                        }
                                        LOGGER.error(errorMsg);
                                        CommonUITool.openErrorBox(getShell(), errorMsg);
                                        CTabFolder queryResultTabFolder =
                                                queryResultComp.getQueryResultTabFolder();
                                        queryResultComp.disposeAllResult();
                                        if (event.getMessage().length() <= 0) {
                                            queryResultComp.makeEmptyResult();
                                        } else {
                                            if (event.getMessage().length() > 0) {
                                                queryResultComp.makeLogResult(
                                                        sql, event.getMessage());
                                            }
                                        }

                                        if (queryResultTabFolder.getItemCount() > 0) {
                                            queryResultTabFolder.setSelection(
                                                    queryResultTabFolder.getItemCount() - 1);
                                        }
                                        queryResultComp.setCanDispose(true);
                                    }
                                });
                return;
            }

            int i = 0;
            @SuppressWarnings("unused")
            int currentQueryIndex = 0;
            int cntResults = 0;
            String noSelectSql = "";
            StringBuilder log = new StringBuilder(256);
            @SuppressWarnings("unused")
            boolean hasModifyQuery = false;
            @SuppressWarnings("unused")
            boolean isIsolationHigher = false;
            long beginTimestamp = 0;
            double elapsedTime = 0.0;
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);
            result = null;
            String multiQuerySql = null;

            final Vector<QueryExecuter> curResult = new Vector<QueryExecuter>();
            if (database == null) {
                database = ((QueryUnit) queryEditor.getEditorInput()).getDatabase();
            }

            try {
                if (qVector.isEmpty()) {
                    return;
                } else if (CubridDatabase.hasValidDatabaseInfo(database)
                        && database.getDatabaseInfo().isShard()) {
                    // Shard do not support to get an isolation yet.
                } else {
                    isIsolationHigher =
                            queryEditor.isIsolationHigherThanRepeatableRead(
                                    connection.getConnection(), queryEditor.isActive());
                }

                ServerInfo serverInfo = null;
                if (database != null) {
                    serverInfo = this.database.getServer().getServerInfo();
                }

                int unitCount = QueryOptions.getSearchUnitCount(serverInfo);
                int sqlTotalCount = qVector.size();
                for (i = 0; i < sqlTotalCount; i++) {
                    currentQueryIndex = i;
                    log.delete(0, log.length());
                    long endTimestamp = 0;

                    SQLHistoryDetail sqlHistoryDetail = new SQLHistoryDetail();
                    sqlHistoryDetail.setExecuteTime(
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));

                    String sql = qVector.get(i).toString();
                    if (database.getDatabaseInfo().isShard()) {
                        if (queryEditor.getDefaultShardQueryType()
                                == DatabaseInfo.SHARD_QUERY_TYPE_ID) {
                            sql = QueryUtil.wrapShardQueryWithId(sql, queryEditor.getShardId());
                        } else {
                            sql = QueryUtil.wrapShardQueryWithVal(sql, queryEditor.getShardVal());
                        }
                    }

                    if (sql != null && sql.trim().lastIndexOf(";") == -1) {
                        sql += ";";
                    }

                    String orignSQL = sql;

                    if (unitCount > 0) {
                        multiQuerySql = SqlParser.getPaginatingSqlClause(sql);
                    }
                    String order = StringUtil.getOrdinalFromCardinalNumber(i + 1);
                    if (multiQuerySql == null) {
                        sql = SqlParser.convertComment(sql);
                        beginTimestamp = System.currentTimeMillis();
                        try {
                            stmt =
                                    QueryExecuter.getStatement(
                                            connection.getConnection(), sql, false, false);
                        } catch (final Exception ee) {
                            throw ee;
                        }
                        if (stmt.hasResultSet()) {
                            stmt.setQueryInfo(false);
                            stmt.setOnlyQueryPlan(false);
                            try {
                                stmt.executeQuery();
                                endTimestamp = System.currentTimeMillis();
                                rs = (CUBRIDResultSetProxy) stmt.getResultSet();
                            } catch (final SQLException ee) {
                                throw ee;
                            }
                            elapsedTime = (endTimestamp - beginTimestamp) * 0.001;
                            String elapsedTimeStr = nf.format(elapsedTime);
                            if (elapsedTime < 0.001) {
                                elapsedTimeStr = "0.000";
                            }
                            result =
                                    createQueryExecutor(
                                            queryEditor,
                                            cntResults,
                                            sql,
                                            database,
                                            orignSQL,
                                            UIQueryUtil.loadColumnTableNameList(stmt));
                            result.makeResult(rs);

                            String queryMsg =
                                    Messages.bind(Messages.querySeq, order)
                                            + "[ "
                                            + elapsedTimeStr
                                            + " "
                                            + Messages.second
                                            + " , "
                                            + Messages.totalRows
                                            + " : "
                                            + result.cntRecord
                                            + " ]"
                                            + StringUtil.NEWLINE;
                            result.setQueryMsg(queryMsg);
                            sqlHistoryDetail.setExecuteInfo(queryMsg);
                            sqlHistoryDetail.setElapseTime(elapsedTimeStr);
                            if (stmt.getStatementType() == CUBRIDCommandType.CUBRID_STMT_EVALUATE
                                    || stmt.getStatementType()
                                            == CUBRIDCommandType.CUBRID_STMT_CALL) {
                                hasModifyQuery = true;
                            }
                            curResult.addElement(result);
                            cntResults++;
                        } else {
                            byte execType = stmt.getStatementType();
                            /*
                             * the previous version , the variable
                             * threadExecResult is class field, but why ? is it
                             * necessary?
                             */
                            int threadExecResult = 0;
                            try {
                                threadExecResult = stmt.executeUpdate();
                                endTimestamp = System.currentTimeMillis();
                            } catch (final SQLException ee) {
                                throw ee;
                            }
                            elapsedTime = (endTimestamp - beginTimestamp) * 0.001;
                            int cntModify = threadExecResult;
                            noSelectSql += sql + StringUtil.NEWLINE;
                            hasModifyQuery = true;
                            log.append(Messages.bind(Messages.querySeq, order)).append(" ");
                            switch (execType) {
                                case CUBRIDCommandType.CUBRID_STMT_ALTER_CLASS:
                                case CUBRIDCommandType.CUBRID_STMT_ALTER_SERIAL:
                                case CUBRIDCommandType.CUBRID_STMT_RENAME_CLASS:
                                case CUBRIDCommandType.CUBRID_STMT_RENAME_TRIGGER:
                                    log.append(Messages.alterOk);
                                    break;
                                case CUBRIDCommandType.CUBRID_STMT_CREATE_CLASS:
                                case CUBRIDCommandType.CUBRID_STMT_CREATE_INDEX:
                                case CUBRIDCommandType.CUBRID_STMT_CREATE_TRIGGER:
                                case CUBRIDCommandType.CUBRID_STMT_CREATE_SERIAL:
                                    log.append(Messages.createOk);
                                    break;
                                case CUBRIDCommandType.CUBRID_STMT_DROP_DATABASE:
                                case CUBRIDCommandType.CUBRID_STMT_DROP_CLASS:
                                case CUBRIDCommandType.CUBRID_STMT_DROP_INDEX:
                                case CUBRIDCommandType.CUBRID_STMT_DROP_LABEL:
                                case CUBRIDCommandType.CUBRID_STMT_DROP_TRIGGER:
                                case CUBRIDCommandType.CUBRID_STMT_DROP_SERIAL:
                                case CUBRIDCommandType.CUBRID_STMT_REMOVE_TRIGGER:
                                    log.append(Messages.dropOk);
                                    break;
                                case CUBRIDCommandType.CUBRID_STMT_INSERT:
                                    log.append(Messages.bind(Messages.insertOk, cntModify));
                                    break;
                                case CUBRIDCommandType.CUBRID_STMT_SELECT:
                                    break;
                                case CUBRIDCommandType.CUBRID_STMT_UPDATE:
                                    log.append(Messages.bind(Messages.updateOk2, cntModify));
                                    break;
                                case CUBRIDCommandType.CUBRID_STMT_DELETE:
                                    log.append(Messages.bind(Messages.deleteOk, cntModify));
                                    break;
                                    /* others are 'Successfully execution' */
                                    /*
                                     * Under two line works disable button when query's
                                     * last command is commit/rollback
                                     */
                                case CUBRIDCommandType.CUBRID_STMT_COMMIT_WORK:
                                case CUBRIDCommandType.CUBRID_STMT_ROLLBACK_WORK:
                                    hasModifyQuery = false;
                                default:
                                    log.append(Messages.queryOk);
                                    break;
                            }
                            String elapsedTimeStr = nf.format(elapsedTime);
                            if (elapsedTime < 0.001) {
                                elapsedTimeStr = "0.000";
                            }

                            log.append(" [").append(elapsedTimeStr).append(" ");
                            log.append(Messages.second).append("]").append(StringUtil.NEWLINE);

                            logs.append(log);
                            logs.append(QueryEditorPart.makeSqlLogOnResult(sql));

                            sqlHistoryDetail.setExecuteInfo(log.toString());
                            sqlHistoryDetail.setElapseTime(elapsedTimeStr);
                        }
                    } else {
                        result =
                                createQueryExecutor(
                                        queryEditor, cntResults, "", database, orignSQL, null);
                        result.setMultiQuerySql(multiQuerySql);
                        result.setQueryMsg(
                                Messages.bind(Messages.querySeq, order) + StringUtil.NEWLINE);
                        result.setSqlDetailHistory(sqlHistoryDetail);
                        try {
                            result.makeTable(1, false);
                        } catch (final SQLException ee) {
                            throw ee;
                        }
                        curResult.addElement(result);
                        cntResults++;
                    }

                    QueryUtil.freeQuery(stmt, rs);
                    stmt = null;
                    rs = null;

                    // SQL execution log
                    sqlHistoryDetail.setSql(sql);
                    RecentlyUsedSQLDetailPersistUtils.addLog(database, sqlHistoryDetail);
                }
            } catch (final SQLException event) {
                LOGGER.error(event.getMessage(), event);

                if (multiQuerySql == null || result == null) {
                    final String errorSql = (String) qVector.get(i);
                    noSelectSql += errorSql;
                    logs.append(QueryEditorPart.makeSqlErrorOnResult(i, errorSql, event));
                } else {
                    noSelectSql += result.getQuerySql();
                    logs.append(result.getQueryMsg());
                }
            } catch (final Exception event) {
                LOGGER.error(event.getMessage(), event);

                if (multiQuerySql == null || result == null) {
                    final String errorSql = (String) qVector.get(i);
                    noSelectSql += errorSql;
                    logs.append(QueryEditorPart.makeSqlErrorOnResult(i, errorSql, event));
                } else {
                    noSelectSql += result.getQuerySql();
                    logs.append(result.getQueryMsg());
                }
            } finally {
                RecentlyUsedSQLDetailPersistUtils.save(database);

                QueryUtil.freeQuery(stmt, rs);
                stmt = null;
                rs = null;

                // There should be auto commit mode because it can be caused lack of connections.
                if (connection != null && connection.isAutoClosable()) {
                    connection.close();
                    LOGGER.debug("CLOSE CONN");
                }
            }
        }
    }

    public void dispose() {
        for (DBConnection con : connectionList) {
            if (con != null) {
                con.close();
            }
        }
        for (Map.Entry<String, QueryResultComposite> entry : sqlResultMap.entrySet()) {
            QueryResultComposite queryResultComp = entry.getValue();
            queryResultComp.disposeAllResult();
            queryResultComp.disposeTabResult();
            queryResultComp.dispose();
        }
        super.dispose();
    }

    public List<String> getsqlList() {
        return sqlList;
    }
}
