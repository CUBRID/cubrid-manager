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
package com.cubrid.common.ui.spi.util;

import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import java.io.File;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * Table Utility
 *
 * @author pangqiren 2009-6-4
 */
public final class TableUtil {
    private static final Logger LOGGER = LogUtil.getLogger(TableUtil.class);
    public static final String EXPORT_FILE_PATH_KEY = "Export-ExportFilePath";
    public static final String TABLE_CREATE_FILE_PATH_KEY = "Table-CreateCodeFilePath";

    private TableUtil() {}

    /**
     * Insert Record
     *
     * @param database CubridDatabase
     * @param sqlList List<String>
     * @return int
     * @throws SQLException The SQLException
     */
    public static int insertRecord(CubridDatabase database, List<String> sqlList)
            throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
            stmt = conn.createStatement();
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            int[] countArr = stmt.executeBatch();
            conn.commit();
            int count = 0;
            for (int i = 0; countArr != null && i < countArr.length; i++) {
                count += countArr[i];
            }
            return count;
        } catch (SQLException e) {
            QueryUtil.rollback(conn);
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            QueryUtil.freeQuery(conn, stmt);
        }
    }

    /**
     * get Auto Increment
     *
     * @param database CubridDatabase
     * @param table String
     * @return serialInfoList
     */
    public static List<SerialInfo> getAutoIncrement(CubridDatabase database, String table) {
        List<SerialInfo> serialInfoList = new ArrayList<SerialInfo>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            boolean isSupportCache = CompatibleUtil.isSupportCache(database.getDatabaseInfo());
            // database.getServer().getServerInfo().compareVersionKey("8.2.2") >= 0;
            String sql = "SELECT owner.name,db_serial.*" + " FROM db_serial WHERE class_name=?";

            // [TOOLS-2425]Support shard broker
            if (database != null) {
                sql = DatabaseInfo.wrapShardQuery(database.getDatabaseInfo(), sql);
            }

            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, table);
            stmt.execute();
            rs = stmt.getResultSet();

            while (rs.next()) {
                String name = rs.getString("name");
                String owner = rs.getString("owner.name");
                String currentVal = rs.getString("current_val");
                String incrementVal = rs.getString("increment_val");
                String maxVal = rs.getString("max_val");
                String minVal = rs.getString("min_val");
                String cyclic = rs.getString("cyclic");
                String startVal = rs.getString("started");
                String className = rs.getString("class_name");
                String attName = rs.getString("att_name");
                String cacheCount = null;
                if (isSupportCache) {
                    cacheCount = rs.getString("cached_num");
                }
                boolean isCycle = false;
                if (cyclic != null && cyclic.equals("1")) {
                    isCycle = true;
                }
                SerialInfo serialInfo =
                        new SerialInfo(
                                name,
                                owner,
                                currentVal,
                                incrementVal,
                                maxVal,
                                minVal,
                                isCycle,
                                startVal,
                                cacheCount,
                                className,
                                attName);
                serialInfoList.add(serialInfo);
            }

            return serialInfoList;
        } catch (SQLException e) {
            CommonUITool.openErrorBox(
                    Messages.bind(
                            com.cubrid.common.ui.common.Messages.errCommonTip,
                            e.getErrorCode(),
                            e.getMessage()));
            LOGGER.error(e.getMessage(), e);
        } finally {
            QueryUtil.freeQuery(conn, stmt, rs);
        }
        return serialInfoList;
    }

    /**
     * Check whether this SQL has result set
     *
     * @param database CubridDatabase
     * @param sql String
     * @return boolean
     */
    public static boolean isHasResultSet(CubridDatabase database, String sql) {
        Connection conn = null;
        CUBRIDPreparedStatementProxy pStmt = null;
        try {
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
            pStmt = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            return pStmt.hasResultSet();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            QueryUtil.freeQuery(conn, pStmt);
        }
        return false;
    }

    /**
     * Get parameter meta
     *
     * @param database CubridDatabase
     * @param sql String
     * @return ParameterMetaData
     */
    public static ParameterMetaData getParameterMetaData(CubridDatabase database, String sql) {
        Connection conn = null;
        CUBRIDPreparedStatementProxy pStmt = null;
        try {
            conn = JDBCConnectionManager.getConnection(database.getDatabaseInfo(), false);
            pStmt = (CUBRIDPreparedStatementProxy) conn.prepareStatement(sql);
            // because this method can not be supported by CUBRID JDBC driver
            // return pStmt.getParameterMetaData();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            QueryUtil.freeQuery(conn, pStmt);
        }

        return null;
    }

    /**
     * Get saved file
     *
     * @param shell Shell
     * @param filterExts String[]
     * @param filterNames String[]
     * @param defaultExtName String
     * @param filterPath String
     * @return File
     */
    public static File getSavedFile(
            Shell shell,
            String[] filterExts,
            String[] filterNames,
            String defaultFileName,
            String defaultExtName,
            String filterPath) {

        FileDialog dialog = new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        String filepath = filterPath;
        if (filepath == null || filepath.trim().length() == 0) {
            filepath = CommonUIPlugin.getSettingValue(EXPORT_FILE_PATH_KEY);
        }
        if (null != filepath) {
            dialog.setFilterPath(filepath);
        }
        if (null != filterExts) {
            dialog.setFilterExtensions(filterExts);
        }
        if (null != defaultFileName) {
            dialog.setFileName(defaultFileName);
        }
        if (null != filterNames) {
            dialog.setFilterNames(filterNames);
        }

        String filePath = dialog.open();
        if (filePath == null) {
            return null;
        } else {
            if (!filePath.toLowerCase(Locale.getDefault()).endsWith(".xlsx")
                    && !filePath.toLowerCase(Locale.getDefault()).endsWith(".xls")
                    && !filePath.toLowerCase(Locale.getDefault()).endsWith(".csv")
                    && !filePath.toLowerCase(Locale.getDefault()).endsWith(".sql")
                    && !filePath.toLowerCase(Locale.getDefault()).endsWith(".obs")
                    && defaultExtName != null) {
                filePath = filePath.concat(defaultExtName);
            }
            File tmpFile = new File(filePath);
            if (tmpFile.exists() && !handelFileExist(filePath)) {
                return null;
            }
            File file = new File(filePath);
            CommonUIPlugin.putSettingValue(TableUtil.EXPORT_FILE_PATH_KEY, file.getParent());

            return file;
        }
    }

    /**
     * Handle with file exist
     *
     * @param filePath String
     * @return boolean
     */
    public static boolean handelFileExist(String filePath) {
        File tmpFile = new File(filePath);
        boolean isConfirm =
                CommonUITool.openConfirmBox(
                        Messages.bind(
                                Messages.exportFileOverwriteQuestionMSG,
                                tmpFile.getAbsolutePath()));
        if (!isConfirm) {
            return false;
        }
        String bakfile = filePath + ".bak";
        File oldBackFile = new File(bakfile);
        if (oldBackFile.exists() && !oldBackFile.delete()) {
            CommonUITool.openErrorBox(Messages.bind(Messages.errFileCannotDelete, bakfile));
            return false;
        }
        if (!tmpFile.renameTo(new File(bakfile))) {
            CommonUITool.openErrorBox(
                    Messages.bind(Messages.errFileCannotRename, filePath, bakfile));
            return false;
        }
        return true;
    }

    /**
     * Get saved file
     *
     * @param shell Shell
     * @param filterPath String
     * @return File
     */
    public static File getSavedDir(Shell shell, String title, String message, String filterPath) {
        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        if (title != null) {
            dialog.setText(title);
        }
        if (message != null) {
            dialog.setMessage(message);
        }
        String filepath = filterPath;
        if (StringUtil.isEmpty(filepath)) {
            filepath = CommonUIPlugin.getSettingValue(EXPORT_FILE_PATH_KEY);
        }
        if (filepath != null) {
            dialog.setFilterPath(filepath);
        }
        String filePath = dialog.open();
        if (filePath == null) {
            return null;
        }
        File file = new File(filePath);
        CommonUIPlugin.putSettingValue(TableUtil.EXPORT_FILE_PATH_KEY, file.getAbsolutePath());
        return file;
    }

    /**
     * Get saved file for create php/java files
     *
     * @param shell Shell
     * @param filterPath String
     * @return File
     */
    public static File getSavedDirForCreateCodes(Shell shell, String filterPath) {

        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        String filepath = filterPath;
        if (filepath == null || filepath.trim().length() == 0) {
            filepath = CommonUIPlugin.getSettingValue(TABLE_CREATE_FILE_PATH_KEY);
        }
        if (null != filepath) {
            dialog.setFilterPath(filepath);
        }

        String filePath = dialog.open();
        if (filePath == null) {
            return null;
        } else {
            File file = new File(filePath);
            CommonUIPlugin.putSettingValue(
                    TableUtil.TABLE_CREATE_FILE_PATH_KEY, file.getAbsolutePath());

            return file;
        }
    }
}
