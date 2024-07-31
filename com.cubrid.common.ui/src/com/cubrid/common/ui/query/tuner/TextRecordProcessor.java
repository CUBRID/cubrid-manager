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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.query.control.QueryInfo;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * Text Record Processor
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-12 created by Kevin.Wang
 */
public class TextRecordProcessor implements IRecordProcessor {

    private static final DecimalFormat formater = new DecimalFormat();
    private static final String FORMAT_DOUBLE = "0.000000000000000E000";
    private static final String FORMAT_FLOAT = "0.000000E000";
    private static final Logger LOGGER = LogUtil.getLogger(TextRecordProcessor.class);

    public void process(CUBRIDResultSetProxy resultSet, QueryRecord queryRecord)
            throws SQLException {
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

        try {
            int columnCount = queryRecord.getColumnInfoList().size();
            QueryInfo queryInfo = queryRecord.getQueryInfo();
            if (queryInfo == null) {
                queryInfo = new QueryInfo();
                queryRecord.setQueryInfo(queryInfo);
            }
            int dataID = 1;
            while (resultSet.next()) {
                Map<String, String> data = new HashMap<String, String>();
                data.put(String.valueOf(0), String.valueOf(dataID++));
                for (int i = 1; i <= columnCount; i++) {
                    data.put(String.valueOf(i), getTextData(resultSet, i, queryRecord));
                }
                dataList.add(data);
            }

            resultSet.last();
            int totalRow = resultSet.getRow();
            queryInfo.setTotalRs(totalRow);

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        queryRecord.setDataList(dataList);
    }

    /**
     * Get text data
     *
     * @param dataIndex - column index
     * @return
     * @throws SQLException
     */
    private String getTextData(
            CUBRIDResultSetProxy resultSet, int dataIndex, QueryRecord queryRecord)
            throws SQLException { // FIXME move this logic to core module
        ColumnInfo columnInfo = (ColumnInfo) queryRecord.getColumnInfoList().get(dataIndex - 1);
        String columnType = columnInfo.getType();
        Object rsObj = resultSet.getObject(dataIndex);
        String dataToput = null;
        if (rsObj != null) {
            if (DataType.DATATYPE_SET.equals(columnType)
                    || DataType.DATATYPE_MULTISET.equals(columnType)
                    || DataType.DATATYPE_SEQUENCE.equals(columnType)) {
                StringBuffer data = new StringBuffer();
                Object[] set = (Object[]) resultSet.getCollection(dataIndex);
                data.append("{");
                for (int i = 0; i < set.length; i++) {
                    Object setI = set[i];
                    if (null == setI) {
                        data.append(DataType.VALUE_NULL);
                    } else if (setI.getClass()
                            == CUBRIDOIDProxy.getCUBRIDOIDClass(resultSet.getJdbcVersion())) {
                        data.append((new CUBRIDOIDProxy(setI)).getOidString());
                    } else {
                        data.append(setI);
                    }
                    if (i < set.length - 1) {
                        data.append(",");
                    }
                }
                data.append("}");
                dataToput = data.toString();
            } else if (DataType.DATATYPE_DATETIME.equalsIgnoreCase(columnType)) {
                dataToput =
                        CommonUITool.formatDate(
                                resultSet.getTimestamp(dataIndex),
                                FieldHandlerUtils.FORMAT_DATETIME);
            } else if (DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(columnType)
                    || DataType.DATATYPE_BIT.equalsIgnoreCase(columnType)) {
                byte[] dataTmp = resultSet.getBytes(dataIndex);
                if (dataTmp.length > FieldHandlerUtils.BIT_TYPE_MUCH_VALUE_LENGTH) {
                    dataToput = DataType.BIT_EXPORT_FORMAT;
                } else {
                    dataToput = "X'" + DBAttrTypeFormatter.getHexString(dataTmp) + "'";
                }
            } else if (DataType.DATATYPE_FLOAT.equalsIgnoreCase(columnType)) {
                formater.applyPattern(FORMAT_FLOAT);
                dataToput = formater.format(resultSet.getFloat(dataIndex));
            } else if (DataType.DATATYPE_DOUBLE.equalsIgnoreCase(columnType)) {
                formater.applyPattern(FORMAT_DOUBLE);
                dataToput = formater.format(resultSet.getDouble(dataIndex));
            } else if (DataType.DATATYPE_BLOB.equalsIgnoreCase(columnType)
                    || rsObj instanceof Blob) {
                columnInfo.setType(DataType.DATATYPE_BLOB);
                dataToput = DataType.BLOB_EXPORT_FORMAT;
            } else if (DataType.DATATYPE_CLOB.equalsIgnoreCase(columnType)
                    || rsObj instanceof Clob) {
                columnInfo.setType(DataType.DATATYPE_CLOB);
                dataToput = DataType.CLOB_EXPORT_FORMAT;
            } else if (DataType.DATATYPE_NCHAR.equalsIgnoreCase(columnType)) {
                columnInfo.setType(DataType.DATATYPE_NCHAR);
                dataToput = "N'" + resultSet.getString(dataIndex) + "'";
            } else if (DataType.DATATYPE_NCHAR_VARYING.equalsIgnoreCase(columnType)) {
                columnInfo.setType(DataType.DATATYPE_NCHAR_VARYING);
                dataToput = "N'" + resultSet.getString(dataIndex) + "'";
            } else {
                dataToput = resultSet.getString(dataIndex);
            }
        }
        return dataToput;
    }
}
