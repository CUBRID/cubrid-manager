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
package com.cubrid.common.ui.query.control;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.control.AttributeTableViewerContentProvider;
import com.cubrid.common.ui.cubrid.table.control.AttributeTableViewerLabelProvider;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.GetInfoDataUtil;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCTask;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetViewAllColumnsTask;
import com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetMetaDataProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * Get (Table or View) info task.
 *
 * @author Kevin.Wang
 * @version 1.0 - Apr 19, 2012 created by Kevin.Wang
 */
public class GetInfoDataTask extends JDBCTask {
	private static final Logger LOGGER = LogUtil.getLogger(GetInfoDataTask.class);
	private static final String FORMAT_DOUBLE = "0.000000000000000E000";
	private static final String FORMAT_FLOAT = "0.000000E000";
	private final ObjectInfoComposite objectInfoComposite;
	private final Table demoDataTable;
	private DefaultSchemaNode schemaNode;
	private SchemaInfo schemaInfo;
	private List<Map<String, Object>> allDataList = new ArrayList<Map<String, Object>>();
	private List<ColumnInfo> allColumnList = new ArrayList<ColumnInfo>();
	private final boolean doesGetOidInfo = false;
	private boolean isTable;
	// "Name", "Data type", "Default type", "Default value"
	private List<Map<String, String>> viewColListData;

	public GetInfoDataTask(String taskName,
			ObjectInfoComposite objectInfoComposite,
			DefaultSchemaNode schemaNode, boolean isTable) {
		super(taskName, schemaNode.getDatabase().getDatabaseInfo());

		this.objectInfoComposite = objectInfoComposite;
		this.schemaNode = schemaNode;
		this.isTable = isTable;
		this.demoDataTable = objectInfoComposite.getDemoDataTable();
	}

	public void execute() {
		try {
			initDemoDataTable();
		} catch (SQLException e) {
			openError(Messages.bind(Messages.titleGetDataErr,
					schemaNode.getName()), Messages.bind(
					Messages.msgGetDataErr, schemaNode.getName(),
					e.getMessage()));
			LOGGER.error(e.getMessage());
		}
		/*Check is canceled*/
		if (isCancel) {
			return;
		}

		initDDLText();

		/*Check is canceled*/
		if (isCancel) {
			return;
		}

		if (isTable) {
			/*Get schemaInfo data*/
			databaseInfo.removeSchema(schemaNode.getName());
			schemaInfo = databaseInfo.getSchemaInfo(schemaNode.getName());

			if (schemaInfo == null) {
				openError(Messages.bind(Messages.titleGetMetaDataErr, schemaNode.getName()),
						Messages.bind(Messages.msgGetMetaDataErr, schemaNode.getName(), databaseInfo.getErrorMessage()));
				return;
			}

			initTableColumnTable();
			initIndexAndFKTable();
		} else {
			initViewColumnTable();
		}
	}

	/**
	 * Initial table column table
	 */
	private void initTableColumnTable() {

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				AttributeTableViewerContentProvider attrContentProvider = new AttributeTableViewerContentProvider();
				attrContentProvider.setShowClassAttribute(true);
				AttributeTableViewerLabelProvider attrLabelProvider = new AttributeTableViewerLabelProvider(
						databaseInfo, schemaInfo);

				objectInfoComposite.getTableColViewer().setContentProvider(attrContentProvider);
				objectInfoComposite.getTableColViewer().setLabelProvider(attrLabelProvider);
				objectInfoComposite.getTableColViewer().setInput(schemaInfo);

				// Auto set column size, maximum is 300px,minimum is 50px
				CommonUITool.packTable(objectInfoComposite.getTableColViewer().getTable(), 50, 300);
			}
		});
	}

	/**
	 * Initial index and FK table
	 */
	private void initIndexAndFKTable() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				objectInfoComposite.getIndexTableViewer().setInput(schemaInfo);
				objectInfoComposite.getFkTableViewer().setInput(schemaInfo);
				// Auto set column size, maximum is 300px,minimum is 50px
				CommonUITool.packTable(objectInfoComposite.getFkTableViewer().getTable(), 50, 300);
			}
		});
	}

	/**
	 * Initial view column table
	 */
	private void initViewColumnTable() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				objectInfoComposite.getViewColTableViewer().setInput(viewColListData);
				// Auto set column size, maximum is 300px,minimum is 50px
				CommonUITool.packTable(objectInfoComposite.getViewColTableViewer().getTable(), 50, 300);
			}
		});
	}

	/**
	 * Initial DDL text
	 */
	private void initDDLText() {
		final StringBuffer sqlScript = new StringBuffer();

		if (!isTable) {
			/*Get class info*/
			GetAllClassListTask getAllClassListTask = new GetAllClassListTask(
					schemaNode.getDatabase().getDatabaseInfo());
			getAllClassListTask.setTableName(schemaNode.getName());
			getAllClassListTask.getClassInfoTaskExcute();

			/*If failed*/
			if (getAllClassListTask.getErrorMsg() != null || getAllClassListTask.isCancel()) {
				LOGGER.error(getAllClassListTask.getErrorMsg());
				openError(
						Messages.bind(Messages.titleGetMetaDataErr, schemaNode.getName()),
						Messages.bind(Messages.msgGetMetaDataErr, schemaNode.getName(),
								getAllClassListTask.getErrorMsg()));
				return;
			}
			ClassInfo classInfo = getAllClassListTask.getClassInfo();

			/*Get view column*/
			GetViewAllColumnsTask getAllDBVclassTask = new GetViewAllColumnsTask(
					schemaNode.getDatabase().getDatabaseInfo());
			getAllDBVclassTask.setClassName(schemaNode.getName());
			getAllDBVclassTask.getAllVclassListTaskExecute();

			/*If failed*/
			if (getAllDBVclassTask.getErrorMsg() != null || getAllDBVclassTask.isCancel()) {
				LOGGER.error(getAllDBVclassTask.getErrorMsg());
				openError(
						Messages.bind(Messages.titleGetMetaDataErr, schemaNode.getName()),
						Messages.bind(Messages.msgGetMetaDataErr, schemaNode.getName(),
								getAllClassListTask.getErrorMsg()));
				return;
			}
			/*Get query list*/
			List<String> vclassList = getAllDBVclassTask.getAllVclassList();
			List<Map<String, String>> queryListData = new ArrayList<Map<String, String>>();
			for (String sql : vclassList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("0", sql);
				queryListData.add(map);
			}

			/*Get all attribute*/
			GetAllAttrTask getAllAttrTask = new GetAllAttrTask(schemaNode.getDatabase().getDatabaseInfo());
			getAllAttrTask.setClassName(schemaNode.getName());
			getAllAttrTask.getAttrList();

			/*If failed*/
			if (getAllAttrTask.getErrorMsg() != null) {
				LOGGER.error(getAllAttrTask.getErrorMsg());
				openError(
						Messages.bind(Messages.titleGetMetaDataErr, schemaNode.getName()),
						Messages.bind(Messages.msgGetMetaDataErr, schemaNode.getName(),
								getAllClassListTask.getErrorMsg()));
				return;
			}
			List<DBAttribute> attrList = getAllAttrTask.getAllAttrList();

			viewColListData = GetInfoDataUtil.getViewColMapList(attrList);
			sqlScript.append(GetInfoDataUtil.getViewCreateSQLScript(false,
					schemaNode.getDatabase(), classInfo, schemaNode.getName(),
					viewColListData, queryListData));
		} else {
			String ddl = SQLGenerateUtils.getCreateSQL(schemaNode);
			sqlScript.append(ddl == null ? "" : ddl);
		}

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				objectInfoComposite.getSqlText().setText(sqlScript.toString());
			}
		});
	}

	/**
	 * Initial demo data table
	 *
	 * @throws SQLException
	 */
	private void initDemoDataTable() throws SQLException {
		String sql = getStmtSQL();

		stmt = null;
		rs = null;
		try {
			stmt = getStatement(connection, sql, doesGetOidInfo, false);
			CUBRIDPreparedStatementProxy cubridStmt = (CUBRIDPreparedStatementProxy) stmt;
			cubridStmt.setQueryInfo(false);
			cubridStmt.setOnlyQueryPlan(false);
			cubridStmt.executeQuery();
			rs = (CUBRIDResultSetProxy) stmt.getResultSet();

			makeTableData((CUBRIDResultSetProxy) rs);
		} catch (SQLException event) {
			errorMsg += Messages.runError + event.getErrorCode()
					+ StringUtil.NEWLINE + Messages.errorHead
					+ event.getMessage() + StringUtil.NEWLINE;
			throw event;
		} finally {
			QueryUtil.freeQuery(connection, stmt, rs);
			rs = null;
			stmt = null;
			connection = null;
		}

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				diaplayDemoDataTable();
			}
		});
	}

	/**
	 * Display the demo data table
	 */
	private void diaplayDemoDataTable() {
		displayDemoDataTableColumn();
		displayDemoDataTableItem();
	}

	/**
	 * make query editor result table column
	 */
	private void displayDemoDataTableColumn() {
		TableColumn tblColumn[];
		tblColumn = new TableColumn[(allColumnList == null ? 0 : allColumnList.size())];
		if (allColumnList == null) {
			return;
		}
		for (int j = 0; j < allColumnList.size(); j++) {
			tblColumn[j] = new TableColumn(demoDataTable, SWT.NONE);
			ColumnInfo columnInfo = (ColumnInfo) allColumnList.get(j);
			String name = columnInfo.getName();
			tblColumn[j].setText(name + " : " + columnInfo.getComleteType().toLowerCase());
			tblColumn[j].setToolTipText(columnInfo.getComleteType().toLowerCase());
			tblColumn[j].setData(columnInfo);
			tblColumn[j].pack();
		}
	}

	/**
	 * Display table item by the data in allDataList
	 */
	private void displayDemoDataTableItem() {
		for (int i = 0; i < allDataList.size(); i++) {
			TableItem item = new TableItem(demoDataTable, SWT.MULTI);
			Map<String, Object> map = allDataList.get(i);
			makeItemValue(item, map);
			if (i % 2 == 0) {
				item.setBackground(ResourceManager.getColor(230, 230, 230));
			}
			if (doesGetOidInfo) {
				item.setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			}
		}

		// Auto set column size, maximum is 300px,minimum is 50px
		CommonUITool.packTable(demoDataTable, 50, 300);
	}

	/**
	 * make a table item by its data.
	 *
	 * @param item a TableItem instance
	 * @param mapItemData a item data instance with type of Map<String, Object>
	 */
	private void makeItemValue(TableItem item, Map<String, Object> mapItemData) {

		for (int j = 0; allColumnList != null && j < allColumnList.size(); j++) {
			String columnIndex = allColumnList.get(j).getIndex();
			String type = allColumnList.get(j).getType();
			Object colValue = mapItemData.get(columnIndex);
			if (colValue == null) {
				item.setText(j, DataType.NULL_EXPORT_FORMAT);
				item.setBackground(j, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
				item.setData((j) + "", DataType.VALUE_NULL);
			} else if (DataType.DATATYPE_BLOB.equalsIgnoreCase(type)) {
				item.setText(j, DataType.BLOB_EXPORT_FORMAT);
				item.setBackground(j, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			} else if (DataType.DATATYPE_CLOB.equalsIgnoreCase(type)) {
				item.setText(j, DataType.CLOB_EXPORT_FORMAT);
				item.setBackground(j, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			} else if ((DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(type) || DataType.DATATYPE_BIT.equalsIgnoreCase(type))
					&& DataType.BIT_EXPORT_FORMAT.equals(colValue)) {
				String data = (String) colValue;
				item.setText(j, data);
				item.setBackground(j, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			} else if (colValue instanceof String) {
				String data = (String) colValue;
				item.setText(j, data);
			}
		}
	}

	/**
	 * make table by rs,including column data information and item data
	 * information
	 *
	 * @param rs CUBRIDResultSetProxy
	 * @throws SQLException if failed
	 */
	private void makeTableData(CUBRIDResultSetProxy rs) throws SQLException {
		fillColumnData(rs);
		fillTableItemData(rs);
	}

	/**
	 * fill table column data by rs,all data is saved to allColumnList
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
		if (doesGetOidInfo) {
			allColumnList.add(0, new ColumnInfo("0", DataType.DATATYPE_OID,
					DataType.DATATYPE_OID, null, 0, 0));
		}
		for (int i = 1; i <= cntColumn; i++) {
			String columnName = rsmt.getColumnName(i);
			String typeName = rsmt.getColumnTypeName(i) == null ? "" : rsmt.getColumnTypeName(i);
			int scale = rsmt.getScale(i);
			int precision = rsmt.getPrecision(i);
			String elementTypeName = rsmt.getElementTypeName(i) == null ? "" : rsmt.getElementTypeName(i);
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
			ColumnInfo colInfo = new ColumnInfo(i + "", columnName,
					typeName.toUpperCase(Locale.getDefault()),
					elementTypeName.toUpperCase(Locale.getDefault()),
					precision, scale);
			allColumnList.add(colInfo);
		}
	}

	/**
	 * fill table item data by rs,all data is saved to allDataList
	 *
	 * @param rs CUBRIDResultSetProxy
	 * @throws SQLException if failed
	 */
	private void fillTableItemData(CUBRIDResultSetProxy rs) throws SQLException {
		while (rs.next()) {
			//add item data to the end of list
			addTableItemData(rs, -1);
		}
	}

	/**
	 * add a record to data set.
	 *
	 * @param rs result set
	 * @return Map<String, Object> map type data of a table item
	 * @throws SQLException
	 */
	private Map<String, Object> addTableItemData(CUBRIDResultSetProxy rs,
			int idxInDataList) throws SQLException { // FIXME move this logic to core module
		DecimalFormat formater = new DecimalFormat();
		Map<String, Object> map = new HashMap<String, Object>();
		int columnPos = 0, columnCount = allColumnList == null ? 0 : allColumnList.size();
		if (doesGetOidInfo) {
			CUBRIDOIDProxy oid = ((CUBRIDResultSetProxy) rs).getOID();
			if (oid == null || oid.getProxyObject() == null || oid.getOidString() == null) {
				map.put("0", DataType.VALUE_NONE);
			} else {
				map.put("0", oid.getOidString());
			}
			columnPos++;
			columnCount--;
		}
		if (allColumnList != null) {
			for (int j = 1; j <= columnCount; j++) {
				Object dataToput = null;
				ColumnInfo columnInfo = (ColumnInfo) allColumnList.get(columnPos);
				String columnType = columnInfo.getType();
				String index = columnInfo.getIndex();
				Object rsObj = rs.getObject(j);
				if (rsObj != null) {
					if (DataType.DATATYPE_SET.equals(columnType)
							|| DataType.DATATYPE_MULTISET.equals(columnType)
							|| DataType.DATATYPE_SEQUENCE.equals(columnType)) {
						StringBuffer data = new StringBuffer();
						Object[] set = (Object[]) rs.getCollection(j);
						data.append("{");
						for (int i = 0; i < set.length; i++) {
							Object setI = set[i];
							if (null == setI) {
								data.append(DataType.VALUE_NULL);
							} else if (setI.getClass() == CUBRIDOIDProxy.getCUBRIDOIDClass(rs.getJdbcVersion())) {
								data.append((new CUBRIDOIDProxy(setI)).getOidString());
							} else {
								data.append(setI);
							}
							if (i < set.length - 1) {
								data.append(", ");
							}
						}
						data.append("}");
						dataToput = data.toString();
					} else if (DataType.DATATYPE_DATETIME.equalsIgnoreCase(columnType)) {
						dataToput = CommonUITool.formatDate(rs.getTimestamp(j), FieldHandlerUtils.FORMAT_DATETIME);
					} else if (DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(columnType)
							|| DataType.DATATYPE_BIT.equalsIgnoreCase(columnType)) {
						byte[] dataTmp = rs.getBytes(j);
						if (dataTmp.length > FieldHandlerUtils.BIT_TYPE_MUCH_VALUE_LENGTH) {
							dataToput = DataType.BIT_EXPORT_FORMAT;
						} else {
							dataToput = "X'" + DBAttrTypeFormatter.getHexString(dataTmp) + "'";
						}
					} else if (DataType.DATATYPE_FLOAT.equalsIgnoreCase(columnType)) {
						formater.applyPattern(FORMAT_FLOAT);
						dataToput = formater.format(rs.getFloat(j));
					} else if (DataType.DATATYPE_DOUBLE.equalsIgnoreCase(columnType)) {
						formater.applyPattern(FORMAT_DOUBLE);
						dataToput = formater.format(rs.getDouble(j));
					} else if (DataType.DATATYPE_BLOB.equalsIgnoreCase(columnType) || rsObj instanceof Blob) {
						columnInfo.setType(DataType.DATATYPE_BLOB);
						dataToput = DataType.BLOB_EXPORT_FORMAT;
					} else if (DataType.DATATYPE_CLOB.equalsIgnoreCase(columnType) || rsObj instanceof Clob) {
						columnInfo.setType(DataType.DATATYPE_CLOB);
						dataToput = DataType.CLOB_EXPORT_FORMAT;
					} else {
						dataToput = rs.getString(j);
					}
				}
				map.put(index, dataToput);
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
	 * Get statement for doing with the connection time out
	 *
	 * @param conn Connection
	 * @param sql String
	 * @param doesGetOidInfo boolean
	 * @param isSecond boolean check connection time out
	 * @return CUBRIDPreparedStatementProxy
	 * @throws SQLException The SQL exception
	 */
	public static CUBRIDPreparedStatementProxy getStatement(Connection conn,
			String sql, boolean doesGetOidInfo, boolean isSecond) throws SQLException { // FIXME move this logic to core module
		try {
			CUBRIDPreparedStatementProxy stmt = (CUBRIDPreparedStatementProxy) conn.prepareStatement(
					sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					doesGetOidInfo ? ResultSet.CONCUR_UPDATABLE
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

	/**
	 *
	 * Create select statement SQL
	 *
	 * @return String
	 */
	private String getStmtSQL() { // FIXME move this logic to core module
		StringBuffer sql = new StringBuffer();
		if (schemaNode != null) {
			sql.append(" SELECT * FROM " + QuerySyntax.escapeKeyword(schemaNode.getName()));
			sql.append(" WHERE ROWNUM BETWEEN 1 AND 100;");
		}

		String res = sql.toString();
		try {
			return new SqlFormattingStrategy().format(res).trim();
		} catch (Exception ignored) {
			return res;
		}
	}

	/**
	 * Open the error dialog
	 *
	 * @param title
	 * @param message
	 */
	private void openError(final String title, final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						title, message);
			}
		});
	}
}
