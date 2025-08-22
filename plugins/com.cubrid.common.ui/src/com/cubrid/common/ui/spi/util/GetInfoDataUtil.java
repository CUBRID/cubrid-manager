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

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetInfoDataUtil {
    private static final String[] defaultType = {"", "shared", "default"};
    private static final String DATATYPE_VARBIT = "VARBIT";
    private static final String DATATYPE_VARNCHAR = "VARNCHAR";

    /**
     * Get the view column map list,the order is "Name", "Data type", "Default type", "Default
     * value"
     *
     * @param attrList
     * @return
     */
    public static List<Map<String, String>> getViewColMapList(List<DBAttribute> attrList) {
        List<Map<String, String>> viewColListData = new ArrayList<Map<String, String>>();
        // "Name", "Data type", "Default type", "Default value"
        for (DBAttribute attr : attrList) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("0", attr.getName());
            String type = attr.getType();
            if (type.startsWith(DATATYPE_VARNCHAR)) {
                type = type.replaceAll(DATATYPE_VARNCHAR, DataType.DATATYPE_NCHAR_VARYING);
            }
            if (type.startsWith(DATATYPE_VARBIT)) {
                type = type.replaceAll(DATATYPE_VARBIT, DataType.DATATYPE_BIT_VARYING);
            }
            if (DataType.DATATYPE_OBJECT.equalsIgnoreCase(type)) {
                if (attr.getDomainClassName() == null || "".equals(attr.getDomainClassName())) {
                    type = DataType.DATATYPE_OBJECT;
                } else {
                    type = attr.getDomainClassName();
                }
            }
            map.put("1", type);
            map.put("2", defaultType[0]);
            map.put("3", defaultType[0]);

            String dfltType = null;
            String value = null;
            if (attr.getDefault() != null && !attr.getDefault().equals("")) {
                if (attr.isShared()) {
                    dfltType = defaultType[1];
                } else {
                    dfltType = defaultType[2];
                }
                value = attr.getDefault();
            }
            if (value == null) {
                value = "";
            }
            if (type != null
                    && (type.startsWith(DataType.DATATYPE_CHAR)
                            || type.startsWith(DataType.DATATYPE_STRING)
                            || type.startsWith(DataType.DATATYPE_VARCHAR))
                    && (value.startsWith("'") && value.endsWith("'") && value.length() > 1)) {
                value = value.substring(1, value.length() - 1);
            }
            map.put("2", dfltType);
            map.put("3", value);
            viewColListData.add(map);
        }
        return viewColListData;
    }

    /**
     * get Create SQL Script
     *
     * @return string
     */
    public static String getViewCreateSQLScript(
            boolean newFlag,
            DatabaseInfo database,
            ClassInfo classInfo,
            String viewName,
            List<Map<String, String>> viewColListData,
            List<Map<String, String>> queryListData) {
        StringBuffer sb = new StringBuffer();
        if (newFlag) {
            sb.append("CREATE VIEW ");
        } else {
            if (CompatibleUtil.isSupportReplaceView(database)
                    && classInfo.getUniqueName().equalsIgnoreCase(viewName)) {
                sb.append("CREATE OR REPLACE VIEW ");
            } else {
                sb.append("CREATE VIEW ");
            }
        }

        if (viewName == null || viewName.equals("")) {
            sb.append("[VIEWNAME]");
        } else {
            sb.append(QuerySyntax.escapeKeyword(viewName));
        }

        sb.append("(");

        for (Map<String, String> map : viewColListData) { // "Name", "Data
            // type", "Shared",
            // "Default","Default value"
            String type = map.get("1");
            sb.append(com.cubrid.common.core.util.StringUtil.NEWLINE).append(" ");
            sb.append(QuerySyntax.escapeKeyword(map.get("0"))).append(" ").append(type);
            String defaultType = map.get("2");
            String defaultValue = map.get("3");

            if (defaultType != null
                    && !"".equals(defaultType)
                    && defaultValue != null
                    && !"".equals(defaultValue)) {
                if (type != null
                        && (DataType.DATATYPE_CHAR.equalsIgnoreCase(type)
                                || DataType.DATATYPE_STRING.equalsIgnoreCase(type)
                                || DataType.DATATYPE_VARCHAR.equalsIgnoreCase(type))) {
                    sb.append(" " + defaultType).append(" '" + defaultValue + "'");
                } else {
                    sb.append(" " + defaultType).append(" " + defaultValue);
                }
            }
            sb.append(",");
        }

        if (!viewColListData.isEmpty() && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")").append(com.cubrid.common.core.util.StringUtil.NEWLINE);
        sb.append("    AS ");

        for (int i = 0; i < queryListData.size(); i++) {
            Map<String, String> map = queryListData.get(i);
            sb.append(com.cubrid.common.core.util.StringUtil.NEWLINE).append(map.get("0"));
            if (i != queryListData.size() - 1) {
                sb.append(com.cubrid.common.core.util.StringUtil.NEWLINE).append(" UNION ALL ");
            }
        }
        sb.append(";");
        return sb.toString();
    }

    /**
     * get Create SQL Script
     *
     * @return string
     */
    public static String getViewCreateSQLScript(
            boolean newFlag,
            CubridDatabase database,
            ClassInfo classInfo,
            String viewName,
            List<Map<String, String>> viewColListData,
            List<Map<String, String>> queryListData) {
        if (database == null) {
            return null;
        }
        return getViewCreateSQLScript(
                newFlag,
                database.getDatabaseInfo(),
                classInfo,
                viewName,
                viewColListData,
                queryListData);
    }
}
