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

import com.cubrid.common.core.util.Closer;
import com.cubrid.common.core.util.CubridUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.action.CopyAction;
import com.cubrid.common.ui.query.action.PasteAction;
import com.cubrid.common.ui.query.action.ResultPageTopAction;
import com.cubrid.common.ui.query.dialog.ExportResultDialog;
import com.cubrid.common.ui.query.editor.TextEditorPart;
import com.cubrid.common.ui.query.result.QueryResultFilterSetting;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.FocusAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.table.IShowMoreOperator;
import com.cubrid.common.ui.spi.table.TableSelectSupport;
import com.cubrid.common.ui.spi.table.celleditor.CellViewer;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.UIQueryUtil;
import com.cubrid.common.ui.spi.util.paramSetter.ParamSetException;
import com.cubrid.common.ui.spi.util.paramSetter.ParamSetter;
import com.cubrid.cubridmanager.core.common.jdbc.DBConnection;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.slf4j.Logger;

/**
 * Execute sql script , and return the result table view
 *
 * @author wangsl 2009-6-4
 */
public class QueryExecuter
        implements IShowMoreOperator { // FIXME very complicated so it would be better to split
                                       // several classes
    private static final Logger LOGGER = LogUtil.getLogger(QueryExecuter.class);
    private static final String FORMAT_DOUBLE = "0.000000000000000E000";
    private static final String FORMAT_FLOAT = "0.000000E000";
    private static final String LASTEST_DATA_FLAG = ".lastest_data_flag";
    public static final String NEW_RECORD_FLAG = ".insert_flag";
    public static final String INSERT_RECORD_FAIL = ".fail_insert";
    public static final Color FONT_COLOR =
            ResourceManager.getColor(
                    new RGB(
                            QueryOptions.FONT_COLOR_RED,
                            QueryOptions.FONT_COLOR_GREEN,
                            QueryOptions.FONT_COLOR_BLUE));
    private final int recordLimit;
    public String query = "";
    public final String orignQuery;
    public int idx;
    public int cntRecord = 0;
    public Table tblResult = null;
    public QueryExecuter baseQueryExecuter = null;
    private TableSelectSupport selectableSupport;

    private final TextEditorPart textEditor;
    private QueryInfo queryInfo = null;
    private Action resultCursorTopAction = null;
    private List<Map<String, CellValue>> allDataList = null;
    private List<ColumnInfo> allColumnList = null;
    private String queryMsg;
    private String multiQuerySql = null;
    private Map<String, ColumnComparator> colComparatorMap = null;
    private final CubridDatabase database;
    private String charset;
    private boolean editMode = false;
    private CUBRIDPreparedStatementProxy stmt;
    private CUBRIDResultSetProxy rs;
    // private String selColumnName;
    private final QueryExecuter executer = this;
    // this variable to save the mouse right click position
    protected Point button2Position;
    // tool bar items for inserting and deleting record
    private StyledText logMessageText;
    private Map<String, Map<String, CellValue>> oldValues =
            new HashMap<String, Map<String, CellValue>>();
    private Map<String, Map<String, CellValue>> newValues =
            new HashMap<String, Map<String, CellValue>>();
    private Map<String, Map<String, CellValue>> delValues =
            new HashMap<String, Map<String, CellValue>>();
    private Map<String, Map<String, CellValue>> insValues =
            new HashMap<String, Map<String, CellValue>>();
    private Map<String, String> rsToItemMap = new HashMap<String, String>();
    private List<PstmtParameter> parameterList = null;
    private QueryResultFilterSetting filterSetting;
    private DBConnection connection;
    private Font font;
    private ControlEditor editor;
    private Color color;
    private DecimalFormat formater4Double, formater4Float;
    // private boolean editable = false;
    private boolean isContainPrimayKey;
    private boolean isSingleTableQuery;
    private int loadSize = 0;
    private boolean showEndDialog = true;

    private String statsLog;
    private String queryPlanLog;
    private String textData = "";
    private List<String> columnTableNames;

    public QueryExecuter(
            TextEditorPart qe,
            int idx,
            String query,
            CubridDatabase cubridDatabase,
            DBConnection con,
            List<PstmtParameter> parameterList,
            String orignQuery) {
        if (cubridDatabase == null) {
            throw new IllegalArgumentException(Messages.errMsgServerNull);
        }

        this.database = cubridDatabase;
        this.charset = database.getDatabaseInfo().getCharSet();
        this.parameterList = parameterList;
        this.textEditor = qe;
        this.idx = idx;
        this.query = query;
        this.orignQuery = orignQuery;
        this.connection = con;
        ServerInfo serverInfo =
                cubridDatabase.getServer() == null
                        ? null
                        : cubridDatabase.getServer().getServerInfo();
        recordLimit =
                QueryOptions.isExistPrefix(serverInfo)
                        ? QueryOptions.getSearchUnitCount(serverInfo)
                        : QueryOptions.getSearchUnitCount(null);
        allDataList = new ArrayList<Map<String, CellValue>>();
        allColumnList = new ArrayList<ColumnInfo>();
        colComparatorMap = new HashMap<String, ColumnComparator>();
        loadSize = QueryOptions.getLobLoadSize(serverInfo);

        boolean isUseScientificNotation = QueryOptions.getUseScientificNotation(serverInfo);
        if (isUseScientificNotation) {
            formater4Double = new DecimalFormat();
            formater4Double.applyPattern(FORMAT_DOUBLE);
            formater4Float = new DecimalFormat();
            formater4Float.applyPattern(FORMAT_FLOAT);
        } else {
            formater4Double = new DecimalFormat();
            formater4Double.setMaximumIntegerDigits(308);
            formater4Double.setMaximumFractionDigits(15);
            formater4Float = new DecimalFormat();
            formater4Float.setMaximumIntegerDigits(38);
            formater4Float.setMaximumFractionDigits(7);
        }
    }

    public QueryExecuter(
            TextEditorPart qe,
            int idx,
            String query,
            CubridDatabase cubridDatabase,
            List<PstmtParameter> parameterList,
            String orignQuery) {
        this(qe, idx, query, cubridDatabase, null, parameterList, orignQuery);
    }

    /**
     * Make table by rs,including column data information and item data information
     *
     * @param rs CUBRIDResultSetProxy
     * @throws SQLException if failed
     */
    public void makeResult(CUBRIDResultSetProxy rs) throws SQLException {
        fillColumnData(rs);
        fillTableItemData(rs);
        fillTextData();
    }

    public void fillTextData() {
        int columnCount = 0;
        StringBuilder sb = new StringBuilder();
        if (allColumnList != null) {
            columnCount = allColumnList.size();
        }
        if (allDataList != null) {
            for (Map<String, CellValue> data : allDataList) {
                for (int i = 1; i <= columnCount; i++) {
                    CellValue value = data.get(String.valueOf(i));
                    if (value != null && value.getStringValue() != null) {
                        sb.append(value.getStringValue());
                    }
                    if (i + 1 <= columnCount) {
                        sb.append("\t");
                    }
                }
                sb.append(StringUtil.NEWLINE);
            }
        }
        queryInfo = new QueryInfo(allDataList.size());
        textData = sb.toString().replace("\n", StringUtil.NEWLINE);
    }

    /**
     * Fill table column data by rs,all data is saved to allColumnList
     *
     * @param rs CUBRIDResultSetProxy
     * @throws SQLException if failed
     */
    private void fillColumnData(CUBRIDResultSetProxy rs) throws SQLException {
        CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy) rs.getMetaData();
        int cntColumn = rsmt.getColumnCount();
        if (null == allColumnList) {
            return;
        }

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
            allColumnList.add(colInfo);
        }
    }

    /**
     * Add a record to data set.
     *
     * @param rs result set
     * @return Map<String, CellValue> map type data of a table item
     * @throws SQLException
     */
    public Map<String, CellValue> addTableItemData(CUBRIDResultSetProxy rs, int idxInDataList)
            throws SQLException {
        Map<String, CellValue> map = new HashMap<String, CellValue>();
        int columnPos = 0, columnCount = allColumnList == null ? 0 : allColumnList.size();

        if (allColumnList != null) {
            for (int j = 1; j <= columnCount; j++) {
                ColumnInfo columnInfo = (ColumnInfo) allColumnList.get(columnPos);
                String columnType = columnInfo.getType();
                String index = columnInfo.getIndex();
                String showValue = null;
                Object value = rs.getObject(j);
                CellValue cellValue = new CellValue();
                if (value != null) {
                    if (DataType.DATATYPE_SET.equals(columnType)
                            || DataType.DATATYPE_MULTISET.equals(columnType)
                            || DataType.DATATYPE_SEQUENCE.equals(columnType)) {
                        StringBuffer data = new StringBuffer();
                        Object[] set = (Object[]) rs.getCollection(j);
                        data.append("{");
                        for (int i = 0; i < set.length; i++) {
                            Object setI = set[i];
                            if (setI == null) {
                                data.append(DataType.VALUE_NULL);
                            } else if (setI.getClass()
                                    == CUBRIDOIDProxy.getCUBRIDOIDClass(rs.getJdbcVersion())) {
                                data.append((new CUBRIDOIDProxy(setI)).getOidString());
                            } else {
                                data.append(setI);
                            }
                            if (i < set.length - 1) {
                                data.append(",");
                            }
                        }
                        data.append("}");
                        showValue = data.toString();
                        cellValue.setShowValue(showValue);
                        cellValue.setValue(showValue);
                    } else if (DataType.DATATYPE_DATETIME.equalsIgnoreCase(columnType)) {
                        showValue =
                                CommonUITool.formatDate(
                                        rs.getTimestamp(j), FieldHandlerUtils.FORMAT_DATETIME);
                        cellValue.setValue(rs.getTimestamp(j));
                        cellValue.setShowValue(showValue);
                    } else if (DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(columnType)
                            || DataType.DATATYPE_BIT.equalsIgnoreCase(columnType)) {
                        byte[] dataTmp = rs.getBytes(j);
                        if (dataTmp.length > FieldHandlerUtils.BIT_TYPE_MUCH_VALUE_LENGTH) {
                            showValue = DataType.BIT_EXPORT_FORMAT;
                        } else {
                            showValue =
                                    "X'"
                                            + DBAttrTypeFormatter.getHexString(
                                                    dataTmp, columnInfo.getPrecision())
                                            + "'";
                        }
                        cellValue.setValue(dataTmp);
                        cellValue.setShowValue(showValue);
                    } else if (DataType.DATATYPE_FLOAT.equalsIgnoreCase(columnType)) {
                        float floatValue = rs.getFloat(j);
                        showValue = formater4Float.format(floatValue);
                        cellValue.setValue(floatValue);
                        cellValue.setShowValue(showValue);
                    } else if (DataType.DATATYPE_DOUBLE.equalsIgnoreCase(columnType)) {
                        double doubleValue = rs.getDouble(j);
                        showValue = formater4Double.format(doubleValue);
                        cellValue.setValue(doubleValue);
                        cellValue.setShowValue(showValue);
                    } else if (DataType.DATATYPE_BLOB.equalsIgnoreCase(columnType)
                            || value instanceof Blob) {
                        columnInfo.setType(DataType.DATATYPE_BLOB);
                        loadBlobData(rs, j, cellValue);
                    } else if (DataType.DATATYPE_CLOB.equalsIgnoreCase(columnType)
                            || value instanceof Clob) {
                        columnInfo.setType(DataType.DATATYPE_CLOB);
                        loadClobData(rs, j, cellValue);
                    } else if (DataType.DATATYPE_NCHAR.equalsIgnoreCase(columnType)) {
                        columnInfo.setType(DataType.DATATYPE_NCHAR);
                        String strValue = rs.getString(j);
                        showValue = "N'" + strValue + "'";
                        cellValue.setValue(strValue);
                        cellValue.setShowValue(showValue);

                    } else if (DataType.DATATYPE_NCHAR_VARYING.equalsIgnoreCase(columnType)) {
                        columnInfo.setType(DataType.DATATYPE_NCHAR_VARYING);
                        String strValue = rs.getString(j);
                        showValue = "N'" + strValue + "'";
                        cellValue.setValue(strValue);
                        cellValue.setShowValue(showValue);
                    } else {
                        showValue = rs.getString(j);
                        cellValue.setValue(value);
                        cellValue.setShowValue(showValue);
                    }
                }

                map.put(index, cellValue);
                columnPos++;
            }
        }

        if (allDataList != null) {
            if (idxInDataList < 0 || idxInDataList >= allDataList.size() - 1) {
                allDataList.add(map);
            } else {
                allDataList.add(idxInDataList, map);
            }
        }
        return map;
    }

    /**
     * Create current row data list form CUBRIDResultSetProxy
     *
     * @param rs result set
     * @return LinkedList<String> one row data list
     * @throws SQLException
     */
    public ArrayList<Object> BuildCurrentRowData(CUBRIDResultSetProxy rs) throws SQLException {
        int columnPos = 0, columnCount = allColumnList == null ? 0 : allColumnList.size();
        ArrayList<Object> rowData = new ArrayList<Object>(columnCount);

        if (allColumnList != null) {
            for (int j = 1; j <= columnCount; j++) {
                ColumnInfo columnInfo = (ColumnInfo) allColumnList.get(columnPos);
                String columnType = columnInfo.getType();
                int index = Integer.valueOf(columnInfo.getIndex());
                Object value = null;
                if (DataType.DATATYPE_BLOB.equals(columnType)) {
                    value = rs.getBlob(index);
                } else if (DataType.DATATYPE_CLOB.equals(columnType)) {
                    value = rs.getClob(index);
                } else {
                    value = FieldHandlerUtils.getRsValueForExport(columnType, rs, index, null);
                }
                rowData.add(value);
                columnPos++;
            }
        }
        return rowData;
    }

    /**
     * Load Blob data
     *
     * @param rs
     * @param columnIndex
     * @param cellValue
     * @throws SQLException
     */
    private void loadBlobData(ResultSet rs, int columnIndex, CellValue cellValue)
            throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        if (blob == null) {
            cellValue.setValue(null);
            cellValue.setShowValue(DataType.NULL_EXPORT_FORMAT);
            cellValue.setHasLoadAll(true);
            return;
        }
        long bLength = blob.length();
        if (loadSize > 0) {
            if (loadSize >= bLength) {
                cellValue.setValue(blob.getBytes(1, new Long(bLength).intValue()));
                cellValue.setShowValue(DataType.BLOB_EXPORT_FORMAT);
                cellValue.setHasLoadAll(true);
            } else {
                cellValue.setValue(blob.getBytes(1, loadSize));
                cellValue.setShowValue(DataType.BLOB_EXPORT_FORMAT);
                cellValue.setHasLoadAll(false);
            }
        } else {
            cellValue.setValue(blob.getBytes(1, new Long(bLength).intValue()));
            cellValue.setShowValue(DataType.BLOB_EXPORT_FORMAT);
            cellValue.setHasLoadAll(true);
        }
    }

    /**
     * Load Clob data
     *
     * @param rs
     * @param columnIndex
     * @param cellValue
     * @throws SQLException
     */
    private void loadClobData(ResultSet rs, int columnIndex, CellValue cellValue)
            throws SQLException {
        Reader reader = rs.getCharacterStream(columnIndex);
        if (reader == null) {
            cellValue.setValue(null);
            cellValue.setShowValue(DataType.NULL_EXPORT_FORMAT);
            return;
        }

        try {
            StringBuffer buffer = new StringBuffer();
            if (loadSize > 0) {
                char[] buf = new char[loadSize];
                int len = reader.read(buf);
                if (len != -1) {
                    buffer.append(buf, 0, len);
                }
                if (len >= loadSize && reader.read() != -1) {
                    cellValue.setHasLoadAll(false);
                } else {
                    cellValue.setHasLoadAll(true);
                }
            } else {
                char[] buf = new char[1024];
                int len = reader.read(buf);
                while (len != -1) {
                    buffer.append(buf, 0, len);
                    len = reader.read(buf);
                }
            }
            cellValue.setValue(buffer.toString());
            if (buffer.toString().length() > FieldHandlerUtils.MAX_DISPLAY_CLOB_LENGTH) {
                String showValue =
                        buffer.substring(0, FieldHandlerUtils.MAX_DISPLAY_CLOB_LENGTH) + "...";
                cellValue.setShowValue(showValue);
            } else {
                cellValue.setShowValue(buffer.toString());
            }
        } catch (IOException ex) {
            throw new SQLException(ex);
        } finally {
            Closer.close(reader);
        }
    }

    /**
     * Fill table item data by rs, all data is saved to allDataList
     *
     * @param rs CUBRIDResultSetProxy
     * @throws SQLException if failed
     */
    private void fillTableItemData(CUBRIDResultSetProxy rs) throws SQLException {
        cntRecord = 0;
        while (rs.next()) {
            cntRecord++;
            // add item data to the end of list
            addTableItemData(rs, -1);
        }
        if (multiQuerySql == null) {
            queryInfo = new QueryInfo(cntRecord);
        }
    }

    /**
     * Fill paged action into query editor result toolbar
     *
     * @param toolBarManager ToolBarManager
     */
    public void makeActions(ToolBarManager toolBarManager, Table resultTable) {
        resultCursorTopAction = new ResultPageTopAction(this);
        toolBarManager.add(resultCursorTopAction);
        toolBarManager.update(true);
    }

    /**
     * Make query editor result panel,including table panel and sql text and message text
     *
     * @param resultTbl the Table.
     * @param sqlText the Text.
     * @param messageText the StyledText.
     */
    public void makeResult(
            final TableSelectSupport tableSelectSupport,
            StyledText messageText,
            boolean multiQueryResult) {
        this.selectableSupport = tableSelectSupport;
        this.tblResult = tableSelectSupport.getTable();

        ServerInfo serverInfo =
                database.getServer() == null ? null : database.getServer().getServerInfo();
        String fontString = QueryOptions.getFontString(serverInfo);
        Font tmpFont = ResourceManager.getFont(fontString);
        if (tmpFont == null) {
            String[] fontData = QueryOptions.getDefaultFont();
            tmpFont =
                    ResourceManager.getFont(
                            fontData[0],
                            Integer.valueOf(fontData[1]),
                            Integer.valueOf(fontData[2]));
        }
        font = tmpFont;
        tblResult.setFont(font);
        int[] fontColor = QueryOptions.getFontColor(serverInfo);
        color = ResourceManager.getColor(fontColor[0], fontColor[1], fontColor[2]);
        tblResult.setForeground(color);
        // Set font and foreground
        selectableSupport.getTableCursor().setFont(font);
        selectableSupport.getTableCursor().setForeground(color);
        selectableSupport.setShowDetailOperator(this);
        tblResult.addPaintListener(
                new PaintListener() {
                    public void paintControl(PaintEvent e) {
                        selectableSupport.redrawMoreButton();
                    }
                });

        makeColumn();
        processLogs(messageText);
    }

    private void processLogs(StyledText messageText) {
        logMessageText = messageText = messageText == null ? logMessageText : messageText;
        int[] queryInfoRange = new int[2];
        int[] queryRange = new int[2];
        StringBuilder resultMessage = new StringBuilder();
        resultMessage.append(getQueryMsg() == null ? "" : getQueryMsg().trim());
        queryInfoRange[0] = 0;
        queryInfoRange[1] = resultMessage.length();
        resultMessage
                .append(StringUtil.NEWLINE)
                .append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT)
                .append(StringUtil.NEWLINE);
        queryRange[0] = resultMessage.length();
        resultMessage.append(query);
        queryRange[1] = query.length();

        if (!StringUtil.isEmpty(queryPlanLog)) {
            resultMessage.append(StringUtil.NEWLINE).append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
            resultMessage.append(StringUtil.NEWLINE).append(queryPlanLog);
        }

        if (!StringUtil.isEmpty(statsLog)) {
            resultMessage.append(StringUtil.NEWLINE).append(Messages.queryStat).append(":");
            resultMessage.append(StringUtil.NEWLINE).append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
            resultMessage.append(StringUtil.NEWLINE).append(statsLog);
        }

        messageText.setText(resultMessage.toString());

        // Styled Query info
        StyleRange queryInfoStyle = new StyleRange();
        queryInfoStyle.start = queryInfoRange[0];
        queryInfoStyle.length = queryInfoRange[1];
        queryInfoStyle.fontStyle = SWT.NORMAL;
        queryInfoStyle.foreground = ResourceManager.getColor(SWT.COLOR_BLUE);
        messageText.setStyleRange(queryInfoStyle);

        StyleRange queryStyle = new StyleRange();
        queryStyle.start = queryRange[0];
        queryStyle.length = queryRange[1];
        queryStyle.fontStyle = SWT.BOLD;
        messageText.setStyleRange(queryStyle);

        // styled log text
        updateStyledLogTextForStatistics(messageText);
        updateStyledLogTextForPlan(messageText);
        updateStyledLogTextForTrace(messageText);
    }

    // this variable to save the mouse right click position
    protected MouseEvent mouse2Position;

    private void bindEvents() {
        selectableSupport
                .getTableCursor()
                .addMouseListener(
                        new MouseListener() {
                            public void mouseUp(MouseEvent e) {}

                            public void mouseDown(MouseEvent e) {}

                            public void mouseDoubleClick(MouseEvent e) {
                                performEdit();
                            }
                        });

        selectableSupport
                .getTableCursor()
                .addKeyListener(
                        new KeyListener() {
                            public void keyReleased(KeyEvent e) {}

                            public void keyPressed(KeyEvent e) {
                                if (e.character == SWT.CR) {
                                    performEdit();
                                }
                            }
                        });
        }

    /** Judge is show detail button */
    public boolean isShowButton(int rowIndex, int columnIndex) {
        // Get columnInfo
        ColumnInfo columnInfo = (ColumnInfo) tblResult.getColumn(columnIndex).getData();
        return isShowDetailButton(columnInfo);
    }

    private boolean isShowDetailButton(ColumnInfo columnInfo) {
        if (columnInfo == null) {
            return false;
        }
        return CellViewer.isNeedShowDetail(columnInfo);
    }

    /**
     * Open detail dialog
     *
     * @param event MouseEvent
     */
    @SuppressWarnings("unchecked")
    public void handleButtonEvent(int rowIndex, int columnIndex) {
        // Get columnInfo
        ColumnInfo columnInfo = (ColumnInfo) tblResult.getColumn(columnIndex).getData();
        // Get data
        final TableItem item = selectableSupport.getTableCursor().getRow();
        Map<String, CellValue> dataMap = (Map<String, CellValue>) item.getData();
        Map<String, CellValue> newValueMap =
                (Map<String, CellValue>) item.getData(LASTEST_DATA_FLAG);
        if (newValueMap == null) {
            newValueMap = new HashMap<String, CellValue>();
            newValueMap.putAll(dataMap);
            item.setData(LASTEST_DATA_FLAG, newValueMap);
        }
        String dataIndex = String.valueOf(columnIndex);
        CellValue cellValue = newValueMap.get(dataIndex);
        if (cellValue == null) {
            cellValue = new CellValue();
            newValueMap.put(dataIndex, cellValue);
        }

        String charset = getDatabaseInfo() != null ? getDatabaseInfo().getCharSet() : null;
        cellValue.setFileCharset(charset);
    }

    /** Perform edit data */
    private void performEdit() {
        if (!editMode) {
            return;
        }

        final TableItem item = selectableSupport.getTableCursor().getRow();
        int rowIndex = tblResult.indexOf(item);
        int columnIndex = selectableSupport.getTableCursor().getColumn();

        ColumnInfo columnInfo = (ColumnInfo) tblResult.getColumn(columnIndex).getData();
        String type = columnInfo.getType();
        if (isShowDetailButton(columnInfo)) {
            return;
        }

        if (DataType.VALUE_NULL.equals(item.getData(columnIndex + ""))) {
            item.setText(columnIndex, "");
        }
        // For bug TOOLS-951
        StyledText text = null;
        if (item.getText(columnIndex) != null && item.getText(columnIndex).length() > 128) {
            text =
                    new StyledText(
                            selectableSupport.getTableCursor(),
                            SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        } else {
            text =
                    new StyledText(
                            selectableSupport.getTableCursor(),
                            SWT.SINGLE | SWT.WRAP | SWT.H_SCROLL);
        }
        if (font != null) {
            text.setFont(font);
        }
        if (color != null) {
            text.setForeground(color);
        }

        editor.setEditor(text);

        if (DataType.DATATYPE_CLASS.equals(type) || item.getText(1).equals(DataType.VALUE_NONE)) {
            text.setEditable(false);
            CommonUITool.registerCopyPasteContextMenu(text, false);
        } else if (DataType.DATATYPE_DATETIME.equals(type)
                || DataType.DATATYPE_DATE.equals(type)
                || DataType.DATATYPE_TIMESTAMP.equals(type)
                || DataType.DATATYPE_TIME.equals(type)) {
            final String fType = type;
            final TableItem fItem = item;
            final int fColumn = columnIndex;
            registerInputMethodContextMenu(text, true, fType, fItem, fColumn);
        } else {
            text.setEditable(true);
            CommonUITool.registerCopyPasteContextMenu(text, true);
        }

        text.setText(item.getText(columnIndex));
        text.selectAll();

        try {
            text.setFocus();
        } catch (Exception ex) {
        }
    }

    private void updateStyledLogTextForStatistics(StyledText messageText) {
        String text = messageText.getText();
        final String[] titleString = {Messages.queryStat};
        for (int i = 0, sp = -1, ep = 0, len = titleString.length; i < len; i++) {
            sp = text.indexOf(titleString[i], ep);
            if (sp != -1) {
                StyleRange eachStyle = new StyleRange();
                eachStyle.start = sp;
                eachStyle.length = titleString[i].length();
                eachStyle.fontStyle = SWT.NORMAL;
                eachStyle.foreground = ResourceManager.getColor(SWT.COLOR_BLUE);
                messageText.setStyleRange(eachStyle);
            }
            ep = sp + 1;
        }
    }

    private void updateStyledLogTextForPlan(StyledText messageText) {
        String text = messageText.getText();
        final String[] titleString = {
            "Join graph segments (f indicates final):",
            "Join graph nodes:",
            "Join graph equivalence classes:",
            "Join graph edges:",
            "Join graph terms:",
            "Query plan:",
            "Query stmt:"
        };

        for (int i = 0, sp = -1, ep = 0, len = titleString.length; i < len; i++) {
            sp = text.indexOf(titleString[i], ep);
            if (sp != -1) {
                StyleRange eachStyle = new StyleRange();
                eachStyle.start = sp;
                eachStyle.length = titleString[i].length();
                eachStyle.fontStyle = SWT.BOLD;
                eachStyle.foreground = ResourceManager.getColor(SWT.COLOR_BLUE);
                messageText.setStyleRange(eachStyle);
            }
            ep = sp + 1;
        }
    }

    private void updateStyledLogTextForTrace(StyledText messageText) {
        String text = messageText.getText();
        final String[] titleString = {"Trace Statistics:"};

        for (int i = 0, sp = -1, ep = 0, len = titleString.length; i < len; i++) {
            sp = text.indexOf(titleString[i], ep);
            if (sp != -1) {
                StyleRange eachStyle = new StyleRange();
                eachStyle.start = sp;
                eachStyle.length = titleString[i].length();
                eachStyle.fontStyle = SWT.BOLD;
                eachStyle.foreground = ResourceManager.getColor(SWT.COLOR_BLUE);
                messageText.setStyleRange(eachStyle);
            }
            ep = sp + 1;
        }
    }

    public void reloadQuery() {
        CUBRIDPreparedStatementProxy pstmt = null;
        CUBRIDResultSetProxy prs = null;

        try {
            pstmt =
                    QueryExecuter.getStatement(
                            connection.checkAndConnectQuietly(), query, false, false);
            if (parameterList != null) {
                for (PstmtParameter pstmtParameter : parameterList) {
                    FieldHandlerUtils.setPreparedStatementValue(pstmtParameter, pstmt, charset);
                }
            }

            if (pstmt.hasResultSet()) {
                pstmt.setQueryInfo(false);
                pstmt.setOnlyQueryPlan(false);
                pstmt.executeQuery();
                prs = (CUBRIDResultSetProxy) pstmt.getResultSet();

                if (allColumnList != null) {
                    allColumnList.clear();
                }

                if (allDataList != null) {
                    allDataList.clear();
                }

                makeResult(prs);
            }
        } catch (final Exception ee) {
            LOGGER.error(
                    "execute SQL failed sql  at query editor : " + query + " error message: " + ee);
            ee.printStackTrace();
        } finally {
            QueryUtil.freeQuery(pstmt, prs);
            pstmt = null;
            prs = null;
        }
    }

    /**
     * Register input method context menu for styled text
     *
     * @param text StyledText
     * @param isEditable boolean
     */
    public void registerInputMethodContextMenu(
            final StyledText text,
            final boolean isEditable,
            final String type,
            final TableItem item,
            final int column) {
        text.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent event) {
                        ActionManager.getInstance().changeFocusProvider(text);
                    }
                });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        IAction copyAction = ActionManager.getInstance().getAction(CopyAction.ID);
                        if (copyAction instanceof CopyAction) {
                            manager.add(copyAction);
                            if (!copyAction.isEnabled()) {
                                FocusAction.changeActionStatus(copyAction, text);
                            }
                        }
                        if (!isEditable) {
                            return;
                        }
                        IAction pasteAction = ActionManager.getInstance().getAction(PasteAction.ID);
                        if (pasteAction instanceof PasteAction) {
                            manager.add(pasteAction);
                            if (!pasteAction.isEnabled()) {
                                FocusAction.changeActionStatus(pasteAction, text);
                            }
                        }
                    }
                });
        Menu contextMenu = menuManager.createContextMenu(text);
        text.setMenu(contextMenu);

        text.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        IAction copyAction = ActionManager.getInstance().getAction(CopyAction.ID);
                        if (!copyAction.isEnabled()) {
                            FocusAction.changeActionStatus(copyAction, text);
                        }
                        IAction pasteAction = ActionManager.getInstance().getAction(PasteAction.ID);
                        if (pasteAction != null && !pasteAction.isEnabled()) {
                            FocusAction.changeActionStatus(pasteAction, text);
                        }
                    }
                });
    }

    private void createContextMenuItems() {
        Menu menu = new Menu(textEditor.getEditorSite().getShell(), SWT.POP_UP);

        final MenuItem itemCopy = new MenuItem(menu, SWT.PUSH);
        itemCopy.setText(Messages.copyClipBoard);

        final MenuItem itemDelete = new MenuItem(menu, SWT.PUSH);
        itemDelete.setText(Messages.delete);

        new MenuItem(menu, SWT.SEPARATOR);

        tblResult.setMenu(menu);
        tblResult.addListener(
                SWT.MouseUp,
                new Listener() {
                    public void handleEvent(Event event) {
                        //				Point pt = new Point(event.x, event.y);
                        //				itemOid.setEnabled(false);
                        int index = tblResult.getSelectionIndex();
                        int count = tblResult.getItemCount();
                        if (index < 0 || index >= count) {
                            return;
                        }

                        //				final TableItem item = tblResult.getItem(index);
                        //				for (int i = 0; i < tblResult.getColumnCount(); i++) {
                        //					Rectangle rect = item.getBounds(i);
                        //					if (rect.contains(pt)) {
                        //						selColumnName = tblResult.getColumn(i).getText();
                        ////						Object value = item.getData(i + "");
                        ////						String text = item.getText(i);
                        ////						if (!DataType.VALUE_NULL.equals(value) &&
                        // containsOIDs(tblResult.getColumn(i).getText())
                        ////							&& !DataType.VALUE_NONE.equals(text)) {
                        ////							itemOid.setEnabled(true);
                        ////						} else {
                        ////							itemOid.setEnabled(false);
                        ////						}
                        //
                        //					}
                        //				}
                    }
                });

        final MenuItem itemExportAll = new MenuItem(menu, SWT.PUSH);
        itemExportAll.setText(Messages.allExport);

        final MenuItem itemExportSelection = new MenuItem(menu, SWT.PUSH);
        itemExportSelection.setText(Messages.selectExport);
        new MenuItem(menu, SWT.SEPARATOR);

        final MenuItem itemMakeInsertQuery = new MenuItem(menu, SWT.PUSH);
        itemMakeInsertQuery.setText(Messages.makeInsertFromSelectedRecord);
        itemMakeInsertQuery.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/record_to_insert.png"));

        final MenuItem itemMakeUpdateQuery = new MenuItem(menu, SWT.PUSH);
        itemMakeUpdateQuery.setText(Messages.makeUpdateFromSelectedRecord);
        itemMakeUpdateQuery.setImage(
                CommonUIPlugin.getImage("icons/queryeditor/record_to_update.png"));

        if (isEmpty()) {
            itemExportAll.setEnabled(false);
            itemExportSelection.setEnabled(false);
        }

        menu.addMenuListener(
                new MenuAdapter() {
                    public void menuShown(MenuEvent event) {
                        List<Point> selectedList = selectableSupport.getSelectedLocations();
                        TableItem[] tblItems = selectableSupport.getSelectedTableItems();
                        boolean selectedCol = selectableSupport.hasSelected();
                        boolean enableItemDetail =
                                (selectedList != null && selectedList.size() == 1);

                        itemExportSelection.setEnabled(selectedCol);
                        itemCopy.setEnabled(selectedCol);

                        if (allDataList.size() > 0) {
                            itemExportAll.setEnabled(true);
                        } else {
                            itemExportAll.setEnabled(false);
                        }

                        for (int i = 0; i < tblItems.length; i++) {
                            if (isEditMode()
                                    && getEditable()
                                    && tblItems[i].getText(1).equals(DataType.VALUE_NONE)) {
                                itemDelete.setEnabled(false);
                            }
                        }
                    }
                });

        itemCopy.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        copySelectedItems();
                    }
                });
        itemCopy.setAccelerator(SWT.CTRL + 'c');

        itemExportAll.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ExportResultDialog dialog =
                                new ExportResultDialog(
                                        tblResult.getShell(),
                                        database,
                                        Messages.msgExportAllResults);
                        if (IDialogConstants.OK_ID == dialog.open()) {
                            ExportQueryResultTaskExecutor task =
                                    new ExportQueryResultTaskExecutor(
                                            dialog.getFile(),
                                            dialog.getFileCharset(),
                                            allColumnList,
                                            allDataList,
                                            false);
                            new ExecTaskWithProgress(task).exec();
                        }
                    }
                });

        //		itemOid.addSelectionListener(new SelectionAdapter() {
        //			/**
        //			 * @see
        // org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
        //			 * @param event an event containing information about the
        //			 *        selection
        //			 */
        //			public void widgetSelected(SelectionEvent event) {
        //				openOidNavigator(button2Position);
        //			}
        //
        //		});
        itemExportSelection.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ExportResultDialog dialog =
                                new ExportResultDialog(
                                        tblResult.getShell(),
                                        database,
                                        Messages.msgExportSelectedResults);
                        if (IDialogConstants.OK_ID == dialog.open()) {
                            ExportQueryResultTaskExecutor task =
                                    new ExportQueryResultTaskExecutor(
                                            dialog.getFile(),
                                            dialog.getFileCharset(),
                                            tblResult,
                                            selectableSupport.getSelectedTableItems(),
                                            false);
                            new ExecTaskWithProgress(task).exec();
                        }
                    }
                });

        itemMakeInsertQuery.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        String text = makeInsertQueryWithSelectedRecords();
                        CommonUITool.copyContentToClipboard(text);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        itemMakeUpdateQuery.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        String text = makeUpdateQueryWithSelectedRecords();
                        CommonUITool.copyContentToClipboard(text);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
    }

    public String makeInsertQueryWithSelectedRecords() {
        TableItem[] items = selectableSupport.getSelectedTableItems();
        if (items == null || items.length == 0) {
            return "";
        }

        if (items.length > 100) {
            CommonUITool.openErrorBox(Messages.makeQueryFromSelectedRecordError);
            return "";
        }

        boolean matched = true;
        String tableName =
                UIQueryUtil.getTableNameFromQuery(connection.checkAndConnectQuietly(), query);
        if (StringUtil.isEmpty(tableName)) {
            tableName = "{table}";
        }

        List<ColumnInfo> cols = getAllColumnList();
        int colCount = cols.size();

        StringBuilder notSupportedColumns = new StringBuilder();
        for (int i = 0; i < colCount; i++) {
            ColumnInfo colInfo = cols.get(i);
            if (colInfo == null || colInfo.getName() == null || colInfo.getType() == null) {
                continue;
            }
            if (!isSupportOnMakeQueryWithRecords(colInfo.getType().toUpperCase())) {
                if (notSupportedColumns.length() > 0) {
                    notSupportedColumns.append(", ");
                }
                notSupportedColumns.append(colInfo.getName());
                notSupportedColumns.append(" ");
                notSupportedColumns.append(colInfo.getType().toLowerCase());
            }
        }

        StringBuilder sql = new StringBuilder();
        if (notSupportedColumns.length() > 0) {
            sql.append("-- ");
            sql.append(Messages.makeQueryFromSelectedRecordNotSupported).append(" ");
            sql.append(notSupportedColumns).append(StringUtil.NEWLINE);
        }

        if (!matched) {
            sql.append("-- ");
            sql.append(Messages.makeQueryFromSelectedRecordHasMultipleTableNames);
            sql.append(StringUtil.NEWLINE);
        }

        for (TableItem item : items) {
            sql.append("INSERT INTO ").append(QuerySyntax.escapeKeyword(tableName)).append(" (");

            StringBuilder values = new StringBuilder();

            for (int i = 0; i < colCount; i++) {
                ColumnInfo colInfo = cols.get(i);
                if (colInfo == null) {
                    continue;
                }
                String colName = QuerySyntax.escapeKeyword(colInfo.getName());
                String data = item.getText(i + 1);

                if (i > 0) {
                    sql.append(", ");
                    values.append(", ");
                }

                sql.append(colName);
                if (QueryUtil.isStringDataType(colInfo.getType())) {
                    values.append(StringUtil.escapeQuotes(getFormatedValue(colInfo, data)));
                } else {
                    values.append(getFormatedValue(colInfo, data));
                }
            }

            sql.append(") VALUES (").append(values).append(");").append(StringUtil.NEWLINE);
        }

        return sql.toString();
    }

    /**
     * Format the data that adapt to data type
     *
     * @param colInfo
     * @param data
     * @return
     */
    private String getFormatedValue(ColumnInfo colInfo, String data) {
        String nullValue = "NULL";
        if ("(NULL)".equals(data)) {
            return nullValue;
        } else {
            String quote = StringUtil.getWrappedQuote(data);
            try {
                FormatDataResult formatResult =
                        DBAttrTypeFormatter.formatForInput(
                                colInfo.getComleteType(), data, true, charset, false);
                if (formatResult.isSuccess() && formatResult.getFormatedString() != null) {
                    if (!quote.equals("")) {
                        formatResult.setFormatedString(
                                StringUtil.insertQuotes(quote, formatResult.getFormatedString()));
                    }
                    return formatResult.getFormatedString();
                }
            } catch (Exception ex) {
                LOGGER.error("Get format data failed:" + data, ex);
            }
        }
        return nullValue;
    }

    private boolean isSupportOnMakeQueryWithRecords(String type) {
        if (DataType.DATATYPE_BIGINT.equals(type)
                //				|| DataType.DATATYPE_BIT.equals(type)
                //				|| DataType.DATATYPE_BIT_VARYING.equals(type)
                //				|| DataType.DATATYPE_BLOB.equals(type)
                //				|| DataType.DATATYPE_CLASS.equals(type)
                //				|| DataType.DATATYPE_CLOB.equals(type)
                || DataType.DATATYPE_CHAR.equals(type)
                || DataType.DATATYPE_VARCHAR.equals(type)
                || DataType.DATATYPE_STRING.equals(type)
                || DataType.DATATYPE_CURRENCY.equals(type)
                || DataType.DATATYPE_DATE.equals(type)
                || DataType.DATATYPE_DATETIME.equals(type)
                || DataType.DATATYPE_DECIMAL.equals(type)
                || DataType.DATATYPE_DOUBLE.equals(type)
                || DataType.DATATYPE_FLOAT.equals(type)
                || DataType.DATATYPE_INT.equals(type)
                || DataType.DATATYPE_INTEGER.equals(type)
                || DataType.DATATYPE_MONETARY.equals(type)
                || DataType.DATATYPE_MULTISET.equals(type)
                || DataType.DATATYPE_NATIONAL_CHARACTER.equals(type)
                || DataType.DATATYPE_NATIONAL_CHARACTER_VARYING.equals(type)
                || DataType.DATATYPE_NCHAR.equals(type)
                || DataType.DATATYPE_NCHAR_VARYING.equals(type)
                || DataType.DATATYPE_NUMERIC.equals(type)
                //				|| DataType.DATATYPE_OBJECT.equals(type)
                //				|| DataType.DATATYPE_OID.equals(type)
                || DataType.DATATYPE_REAL.equals(type)
                || DataType.DATATYPE_SEQUENCE.equals(type)
                || DataType.DATATYPE_SET.equals(type)
                || DataType.DATATYPE_SHORT.equals(type)
                || DataType.DATATYPE_SMALLINT.equals(type)
                || DataType.DATATYPE_TIME.equals(type)
                || DataType.DATATYPE_TIMESTAMP.equals(type)
                || DataType.DATATYPE_TINYINT.equals(type)) {
            return true;
        }

        return false;
    }

    public String makeUpdateQueryWithSelectedRecords() {
        TableItem[] items = selectableSupport.getSelectedTableItems();
        if (items == null || items.length == 0) {
            return "";
        }

        if (items.length > 100) {
            CommonUITool.openErrorBox(Messages.makeQueryFromSelectedRecordError);
            return "";
        }

        boolean matched = true;
        String tableName = null;
        try {
            CUBRIDPreparedStatementProxy pstmt =
                    QueryExecuter.getStatement(
                            connection.checkAndConnectQuietly(), query, false, false);
            ResultSetMetaData rsMetaData = pstmt.getMetaData();
            tableName = rsMetaData.getTableName(1);

            for (int i = 2; i <= rsMetaData.getColumnCount(); i++) {
                if (!tableName.equals(rsMetaData.getTableName(i))) {
                    matched = false;
                    break;
                }
            }
        } catch (Exception e) {
            matched = false;
        }

        if (!matched) {
            tableName = "{table}";
        }

        List<String> pkList = UIQueryUtil.getPkList(getDatabaseInfo(), tableName);
        List<ColumnInfo> cols = getAllColumnList();
        int colCount = cols.size();

        StringBuilder notSupportedColumns = new StringBuilder();
        for (int i = 0; i < colCount; i++) {
            ColumnInfo colInfo = cols.get(i);
            if (colInfo == null || colInfo.getName() == null || colInfo.getType() == null) {
                continue;
            }
            if (!isSupportOnMakeQueryWithRecords(colInfo.getType().toUpperCase())) {
                if (notSupportedColumns.length() > 0) {
                    notSupportedColumns.append(", ");
                }
                notSupportedColumns.append(colInfo.getName());
                notSupportedColumns.append(" ");
                notSupportedColumns.append(colInfo.getType().toLowerCase());
            }
        }

        StringBuilder sql = new StringBuilder();
        if (notSupportedColumns.length() > 0) {
            sql.append("-- ");
            sql.append(Messages.makeQueryFromSelectedRecordNotSupported).append(" ");
            sql.append(notSupportedColumns).append(StringUtil.NEWLINE);
        }

        if (pkList.size() == 0) {
            sql.append("-- ");
            sql.append(Messages.makeQueryFromSelectedRecordNoPK);
            sql.append(StringUtil.NEWLINE);
        }

        if (!matched) {
            sql.append("-- ");
            sql.append(Messages.makeQueryFromSelectedRecordHasMultipleTableNames);
            sql.append(StringUtil.NEWLINE);
        }

        for (TableItem item : items) {
            sql.append("UPDATE ").append(QuerySyntax.escapeKeyword(tableName)).append(" SET ");

            for (int i = 0; i < colCount; i++) {
                ColumnInfo colInfo = cols.get(i);
                if (colInfo == null) {
                    continue;
                }
                String colName = QuerySyntax.escapeKeyword(colInfo.getName());
                String data = item.getText(i + 1);

                if (i > 0) {
                    sql.append(", ");
                }

                sql.append(colName).append("=");
                if (QueryUtil.isStringDataType(colInfo.getType())) {
                    sql.append(StringUtil.escapeQuotes(getFormatedValue(colInfo, data)));
                } else {
                    sql.append(getFormatedValue(colInfo, data));
                }
            }

            int count = 0;

            for (int i = 0; i < colCount; i++) {
                ColumnInfo colInfo = cols.get(i);
                String colName = QuerySyntax.escapeKeyword(colInfo.getName());
                String data = item.getText(i + 1);

                if (!pkList.contains(colName)) {
                    continue;
                }

                if (i == 0) {
                    sql.append(" WHERE ");
                }

                if (count > 0) {
                    sql.append(" AND ");
                }

                count++;

                sql.append(colName);
                if ("(NULL)".equals(data)) {
                    sql.append(" IS NULL");
                } else {
                    sql.append("='").append(data.replaceAll("'", "''")).append("'");
                }
            }

            sql.append(";").append(StringUtil.NEWLINE);
        }

        return sql.toString();
    }

    /** add tips to table item. */
    private void addTableItemToolTips() {
        Listener tableListener =
                new Listener() {
                    Shell tip = null;
                    Label label = null;

                    public void handleEvent(Event event) {
                        switch (event.type) {
                            case SWT.Dispose:
                            case SWT.KeyDown:
                            case SWT.MouseMove:
                                {
                                    if (tip != null) {
                                        tip.dispose();
                                        tip = null;
                                        label = null;
                                    }
                                    break;
                                }
                            case SWT.MouseHover:
                                {
                                    TableItem item = tblResult.getItem(new Point(event.x, event.y));
                                    if (item != null) {
                                        SQLException ex =
                                                (SQLException) item.getData(INSERT_RECORD_FAIL);
                                        if (ex == null) {
                                            return;
                                        }
                                        if (tip != null && !tip.isDisposed()) {
                                            tip.dispose();
                                        }
                                        tip =
                                                new Shell(
                                                        tblResult.getShell(),
                                                        SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
                                        tip.setBackground(
                                                Display.getCurrent()
                                                        .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                                        FillLayout layout = new FillLayout();
                                        layout.marginWidth = 2;
                                        tip.setLayout(layout);
                                        label = new org.eclipse.swt.widgets.Label(tip, SWT.NONE);
                                        label.setForeground(
                                                Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                                        label.setBackground(
                                                Display.getCurrent()
                                                        .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                                        label.setText(
                                                Messages.runError
                                                        + ex.getErrorCode()
                                                        + StringUtil.NEWLINE
                                                        + Messages.errorHead
                                                        + ex.getMessage());
                                        Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                                        Rectangle rect = item.getBounds(0);
                                        Point pt =
                                                tblResult.toDisplay(rect.x, rect.y + rect.height);
                                        tip.setBounds(pt.x, pt.y, size.x, size.y);
                                        tip.setVisible(true);
                                    }
                                }
                        } // end of switch
                    } // end of handleEvent
                }; // end of tableListener
        tblResult.addListener(SWT.Dispose, tableListener);
        tblResult.addListener(SWT.KeyDown, tableListener);
        tblResult.addListener(SWT.MouseMove, tableListener);
        tblResult.addListener(SWT.MouseHover, tableListener);
    }

    /** make query editor result table column */
    public void makeColumn() {
        TableColumn tblColumn[];
        tblColumn = new TableColumn[(allColumnList == null ? 0 : allColumnList.size()) + 1];
        tblColumn[0] = new TableColumn(tblResult, SWT.NONE);
        tblColumn[0].setText("NO");
        tblColumn[0].setWidth(40);
        if (allColumnList == null) {
            return;
        }
        for (int j = 0; j < allColumnList.size(); j++) {
            tblColumn[j + 1] = new TableColumn(tblResult, SWT.NONE);
            ColumnInfo columnInfo = (ColumnInfo) allColumnList.get(j);
            String name = columnInfo.getName();
            String type = columnInfo.getType();
            tblColumn[j + 1].setText(name);
            tblColumn[j + 1].setToolTipText(columnInfo.getComleteType());
            tblColumn[j + 1].setData(columnInfo);
            tblColumn[j + 1].pack();
            ColumnComparator comparator = new ColumnComparator(columnInfo.getIndex(), type, true);
            if (colComparatorMap != null) {
                colComparatorMap.put(columnInfo.getIndex(), comparator);
            }
            tblColumn[j + 1].addSelectionListener(
                    new SelectionListener() {
                        @SuppressWarnings("unchecked")
                        public void widgetSelected(SelectionEvent event) {
                            TableColumn column = (TableColumn) event.widget;
                            if (column == null
                                    || column.getText() == null
                                    || column.getText().trim().length() == 0) {
                                return;
                            }

                            if (isEditMode()) {
                                CommonUITool.openWarningBox(Messages.errCanNotSortWhenEditing);
                                return;
                            }

                            TableColumn sortedColumn = tblResult.getSortColumn();
                            int width = column.getWidth();

                            ColumnInfo columnInfo = (ColumnInfo) column.getData();
                            ColumnComparator comparator =
                                    colComparatorMap.get(columnInfo.getIndex());
                            tblResult.setSortColumn(column);
                            tblResult.setSortDirection(comparator.isAsc() ? SWT.UP : SWT.DOWN);
                            Collections.sort(allDataList, comparator);
                            comparator.setAsc(!comparator.isAsc());

                            column.pack();
                            if (column.equals(sortedColumn)) {
                                column.setWidth(width);
                            } else {
                                column.setWidth(width + 25);
                            }
                            tblResult.showColumn(column);
                            selectableSupport.setSelection(new ArrayList<Point>());
                        }

                        public void widgetDefaultSelected(SelectionEvent event) {}
                    });
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
    public static CUBRIDPreparedStatementProxy getStatement(
            Connection conn, String sql, boolean doesGetOidInfo, boolean isSecond)
            throws SQLException {
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

    public static CUBRIDStatementProxy getStatement(Connection conn, boolean isSecond)
            throws SQLException {
        try {
            CUBRIDStatementProxy stmt = (CUBRIDStatementProxy) conn.createStatement();
            return stmt;
        } catch (SQLException e) {
            if (!isSecond && (e.getErrorCode() == -2003 || e.getErrorCode() == -1003)) {
                return getStatement(conn, true);
            } else {
                throw e;
            }
        }
    }

    private boolean isLimitedSql() {
        return multiQuerySql != null;
    }

    public boolean getEditable() {
        return isContainPrimayKey && isSingleTableQuery;
    }

    public boolean isContainPrimayKey() {
        return isContainPrimayKey;
    }

    public void setContainPrimayKey(boolean isContainPrimayKey) {
        this.isContainPrimayKey = isContainPrimayKey;
    }

    public boolean isSingleTableQuery() {
        return isSingleTableQuery;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    /**
     * change new-inserted item's style
     *
     * @param item a new-inserted item
     */
    private void changeInsertedItemStyle(TableItem item) {
        if (item == null) return;
        item.setImage(0, CommonUIPlugin.getImage("icons/action/table_record_adding.png"));

        // Get Max index
        int maxIndex = -1;
        Item[] allItems = tblResult.getItems();
        for (Item it : allItems) {
            if (it.getText() != null) {
                int index = StringUtil.intValue(it.getText(), -1);
                if (index > maxIndex) {
                    maxIndex = index;
                }
            }
        }
        maxIndex++;

        item.setText(0, String.valueOf(maxIndex));
        item.setBackground(0, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        // item.setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        // item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        tblResult.showItem(item);
    }

    public void copySelectedItems() {
        CommonUITool.clearClipboard();

        StringBuilder content = new StringBuilder();
        List<Point> selectedList = selectableSupport.getSelectedLocations();

        int currentRow = -1;
        int len = selectedList.size();
        for (int i = 0; i < len; i++) {
            Point location = selectedList.get(i);
            if (i == 0) {
                currentRow = location.y;
            } else if (currentRow != location.y) {
                content.append(StringUtil.NEWLINE);
                currentRow = location.y;
            }

            TableItem item = tblResult.getItem(currentRow);
            if (!DataType.VALUE_NULL.equals(item.getData(location.x + ""))) {
                content.append(item.getText(location.x));
            } else {
                content.append("");
            }
            if (i + 1 < len) {
                content.append("\t");
            }
        }

        TextTransfer textTransfer = TextTransfer.getInstance();
        Clipboard clipboard = CommonUITool.getClipboard();
        if (clipboard != null) {
            IAction pasteAction = ActionManager.getInstance().getAction(PasteAction.ID);
            pasteAction.setEnabled(true);
            clipboard.setContents(new Object[] {content.toString()}, new Transfer[] {textTransfer});
        }
    }

    public void copyAllItems() {
        selectableSupport.selectAll();
        copySelectedItems();
    }

    private boolean isEmpty() {
        return allDataList == null || allDataList.isEmpty();
    }

    /** dispose the object. */
    public void dispose() {
        disposeAll();
    }

    private void disposeAll() {
        try {
            if (stmt != null) {
                stmt.cancel();
            }
        } catch (SQLException event) {
            LOGGER.error("{}", event);
        }
        QueryUtil.freeQuery(stmt, rs);
        stmt = null;
        rs = null;
        if (allColumnList != null && !allColumnList.isEmpty()) {
            allColumnList.clear();
            allColumnList = null;
        }
        if (allDataList != null && !allDataList.isEmpty()) {
            allDataList.clear();
            allDataList = null;
        }
        if (colComparatorMap != null && !colComparatorMap.isEmpty()) {
            colComparatorMap.clear();
            colComparatorMap = null;
        }
    }

    public void initBeforeRunQuery() {
        disposeAll();
    }

    public QueryInfo getQueryInfo() {
        return queryInfo;
    }

    public void setQueryInfo(QueryInfo queryInfo) {
        this.queryInfo = queryInfo;
    }

    public String getQueryMsg() {
        return queryMsg;
    }

    public void setQueryMsg(String queryMsg) {
        this.queryMsg = queryMsg;
    }

    public String getQuerySql() {
        return query;
    }

    public String getOrignQuery() {
        return orignQuery;
    }

    public String getMultiQuerySql() {
        return multiQuerySql;
    }

    public void setMultiQuerySql(String multiQuerySql) {
        this.multiQuerySql = multiQuerySql;
    }

    /**
     * mark a new-inserted flag and generate unique-id on a table item
     *
     * @param recordItem a TableItem
     * @param existInsertedItems existInsertedItems
     */
    @SuppressWarnings("all")
    public static void markRecordItemAsNewInserted(TableItem recordItem, Map existInsertedItems) {
        if (recordItem != null) {
            long itemId = System.currentTimeMillis();
            while (existInsertedItems.containsKey(itemId)) {
                itemId++;
            }
            recordItem.setData(NEW_RECORD_FLAG, String.valueOf(itemId));
        }
    }

    /**
     * check whether a table item is new-inserted
     *
     * @param recordItem
     * @return true/false
     */
    public static boolean isNewInsertedRecordItem(TableItem recordItem) {
        if (recordItem != null && recordItem.getData(NEW_RECORD_FLAG) != null) {
            return true;
        }
        return false;
    }

    /**
     * Get new inserted item id
     *
     * @param recordItem
     * @return new inserted item id
     */
    public static String getNewInsertedRecordItemId(TableItem recordItem) {
        String itemId = "";
        if (recordItem == null) {
            return itemId;
        }
        Object value = recordItem.getData(NEW_RECORD_FLAG);
        if (value != null) {
            itemId = (String) recordItem.getData(NEW_RECORD_FLAG);
        }
        return itemId;
    }

    public String getQueryPlanLog() {
        return queryPlanLog;
    }

    public void setQueryPlanLog(String queryPlanLog) {
        this.queryPlanLog = queryPlanLog;
    }

    public String getStatsLog() {
        return statsLog;
    }

    public void setStatsLog(String statsLog) {
        this.statsLog = statsLog;
    }

    private DatabaseInfo getDatabaseInfo() {
        if (database == null) {
            return null;
        }
        return database.getDatabaseInfo();
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public boolean isModifiedResult() {
        return newValues.size() > 0 || delValues.size() > 0 || insValues.size() > 0;
    }

    public List<Map<String, CellValue>> getAllDataList() {
        return allDataList;
    }

    public List<ColumnInfo> getAllColumnList() {
        return allColumnList;
    }

    public DBConnection getConnection() {
        return connection;
    }

    public Table getTblResult() {
        return tblResult;
    }

    public void setFilterSetting(QueryResultFilterSetting filterSetting) {
        this.filterSetting = filterSetting;
    }

    public void setBaseQueryExecuter(QueryExecuter baseQueryExecuter) {
        this.baseQueryExecuter = baseQueryExecuter;
    }

    public QueryExecuter getBaseQueryExecuter() {
        return this.baseQueryExecuter;
    }

    public int getIdx() {
        return idx;
    }

    public void setColumnTableNames(List<String> tableNames) {
        this.columnTableNames = tableNames;
    }

    public int getRecordLimit() {
        return recordLimit;
    }

    public int getCurrentTblTotalCount() {
        return tblResult.getItemCount();
    }
}
