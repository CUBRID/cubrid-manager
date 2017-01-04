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
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

import com.cubrid.common.core.queryplan.StructQueryPlan;
import com.cubrid.common.core.util.Closer;
import com.cubrid.common.core.util.CubridUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.cubrid.table.export.ResultSetDataCache;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.action.CopyAction;
import com.cubrid.common.ui.query.action.InputMethodAction;
import com.cubrid.common.ui.query.action.LastAction;
import com.cubrid.common.ui.query.action.NextAction;
import com.cubrid.common.ui.query.action.PasteAction;
import com.cubrid.common.ui.query.control.tunemode.TuneModeModel;
import com.cubrid.common.ui.query.dialog.ExportResultDialog;
import com.cubrid.common.ui.query.dialog.RowDetailDialog;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.result.FilterResultContrItem;
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

/**
 * Execute sql script , and return the result table view
 *
 * @author wangsl 2009-6-4
 */
public class QueryExecuter implements IShowMoreOperator{ // FIXME very complicated so it would be better to split several classes
	private static final Logger LOGGER = LogUtil.getLogger(QueryExecuter.class);
	private static final String FORMAT_DOUBLE = "0.000000000000000E000";
	private static final String FORMAT_FLOAT = "0.000000E000";
	private static final String LASTEST_DATA_FLAG = ".lastest_data_flag";
	public static final String NEW_RECORD_FLAG = ".insert_flag";
	public static final String INSERT_RECORD_FAIL = ".fail_insert";
	public static final Color FONT_COLOR = ResourceManager.getColor(
			new RGB(QueryOptions.FONT_COLOR_RED, QueryOptions.FONT_COLOR_GREEN, QueryOptions.FONT_COLOR_BLUE));
	private static final Color RED_COLOR = ResourceManager.getColor(255, 0, 0);
	private static final Color GREEN_COLOR = ResourceManager.getColor(0, 154, 33);
	private static final Color BLUE_COLOR = ResourceManager.getColor(0, 0, 255);
	private final int recordLimit;
	public String query = "";
	public final String orignQuery;
	public int idx;
	public int cntRecord = 0;
	public Table tblResult = null;
	public int pageLimit; // each page size
	public ToolItem insertRecordItem = null;
	public ToolItem insertSaveItem = null;
	public ToolItem delRecordItem = null;
	public ToolItem rollbackModifiedItem = null;
	public ToolItem swRecordItem = null;
	public SQLHistoryDetail sqlDetailHistory;
	public boolean multiResultsCompare = false;
	public QueryExecuter baseQueryExecuter = null;
	private TableSelectSupport selectableSupport;

	private final QueryEditorPart queryEditor;
	private QueryInfo queryInfo = null;
	private Action nextPageAction = null;
	private Action lastPageAction = null;
	private FilterResultContrItem filterResultContrItem;
	private List<Map<String, CellValue>> allDataList = null;
	private ResultSetDataCache resultSetDataCache;
	private List<ColumnInfo> allColumnList = null;
	private boolean isEnd = false;
	private boolean dontTipNext = false;
	private String queryMsg;
	private String multiQuerySql = null;
	private Map<String, ColumnComparator> colComparatorMap = null;
	private final CubridDatabase database;
	private String charset;
	private boolean editMode = false;
	private CUBRIDPreparedStatementProxy stmt;
	private CUBRIDResultSetProxy rs;
	//private String selColumnName;
	private final QueryExecuter executer = this;
	// this variable to save the mouse right click position
	protected Point button2Position;
	// tool bar items for inserting and deleting record
	private StyledText logMessageText;
	private Map<String, Map<String, CellValue>> oldValues = new HashMap<String, Map<String, CellValue>>();
	private Map<String, Map<String, CellValue>> newValues = new HashMap<String, Map<String, CellValue>>();
	private Map<String, Map<String, CellValue>> delValues = new HashMap<String, Map<String, CellValue>>();
	private Map<String, Map<String, CellValue>> insValues = new HashMap<String, Map<String, CellValue>>();
	private Map<String, String> rsToItemMap = new HashMap<String, String>();
	private List<PstmtParameter> parameterList = null;
	private QueryResultFilterSetting filterSetting;
	private DBConnection connection;
	private Font font;
	private ControlEditor editor;
	private Color color;
	private DecimalFormat formater4Double, formater4Float;
	//private boolean editable = false;
	private boolean isContainPrimayKey;
	private boolean isSingleTableQuery;
	private int loadSize = 0;

	private String statsLog;
	private String queryPlanLog;
	private boolean isCommand;
	private String textData = "";
	private List<String> columnTableNames;

	public QueryExecuter(QueryEditorPart qe, int idx, String query, CubridDatabase cubridDatabase, DBConnection con,
			List<PstmtParameter> parameterList, String orignQuery) {
		if (cubridDatabase == null) {
			throw new IllegalArgumentException(Messages.errMsgServerNull);
		}

		this.database = cubridDatabase;
		this.charset = database.getDatabaseInfo().getCharSet();
		this.parameterList = parameterList;
		this.queryEditor = qe;
		this.idx = idx;
		this.query = query;
		this.orignQuery = orignQuery;
		this.connection = con;
		ServerInfo serverInfo = cubridDatabase.getServer() == null ? null : cubridDatabase.getServer().getServerInfo();
		pageLimit = QueryOptions.getPageLimit(serverInfo);
		filterResultContrItem = new FilterResultContrItem(this, pageLimit);
		recordLimit = QueryOptions.getSearchUnitCount(serverInfo);
		allDataList = new ArrayList<Map<String, CellValue>>();
		allColumnList = new ArrayList<ColumnInfo>();
		colComparatorMap = new HashMap<String, ColumnComparator>();
		resultSetDataCache = new ResultSetDataCache();
		boolean enableSearchUnit = QueryOptions.getEnableSearchUnit(serverInfo);
		loadSize = QueryOptions.getLobLoadSize(serverInfo);
		dontTipNext = !QueryOptions.getMultiPageConfirm();
		if (!enableSearchUnit) {
			dontTipNext = true;
		}

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

		isCommand = CubridUtil.isCommand(orignQuery);
	}

	public QueryExecuter(QueryEditorPart qe, int idx, String query, CubridDatabase cubridDatabase,
			List<PstmtParameter> parameterList, String orignQuery) {
		this(qe, idx, query, cubridDatabase, qe.getConnection(), parameterList, orignQuery);
	}

	/**
	 * Make table by rs,including column data information and item data
	 * information
	 *
	 * @param rs CUBRIDResultSetProxy
	 * @throws SQLException if failed
	 */
	public void makeResult(CUBRIDResultSetProxy rs) throws SQLException {
		fillColumnData(rs);
		fillTableItemData(rs);
		if (isCommand) {
			fillTextData();
		}
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
		queryInfo = new QueryInfo(allDataList.size(), pageLimit);
		queryInfo.setCurrentPage(1);
		textData = sb.toString().replace("\n", StringUtil.NEWLINE);
	}

	/**
	 * Fill table column data by rs,all data is saved to allColumnList
	 *
	 * @param rs CUBRIDResultSetProxy
	 * @throws SQLException if failed
	 */
	private void fillColumnData(CUBRIDResultSetProxy rs) throws SQLException {
		CUBRIDResultSetMetaDataProxy rsmt = (CUBRIDResultSetMetaDataProxy)rs.getMetaData();
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
			ColumnInfo colInfo = new ColumnInfo(String.valueOf(i), columnName,
					columnType, elementType, precision, scale);
			allColumnList.add(colInfo);
		}
		resultSetDataCache.setColumnInfos(new ArrayList<ColumnInfo>(allColumnList));
	}

	/**
	 * Add a record to data set.
	 *
	 * @param rs result set
	 * @return Map<String, CellValue> map type data of a table item
	 * @throws SQLException
	 */
	public Map<String, CellValue> addTableItemData(CUBRIDResultSetProxy rs, int idxInDataList) throws SQLException {
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
							} else if (setI.getClass() == CUBRIDOIDProxy.getCUBRIDOIDClass(rs.getJdbcVersion())) {
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
						showValue = CommonUITool.formatDate(
								rs.getTimestamp(j),
								FieldHandlerUtils.FORMAT_DATETIME);
						cellValue.setValue(rs.getTimestamp(j));
						cellValue.setShowValue(showValue);
					} else if (DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(columnType)
							|| DataType.DATATYPE_BIT.equalsIgnoreCase(columnType)) {
						byte[] dataTmp = rs.getBytes(j);
						if (dataTmp.length > FieldHandlerUtils.BIT_TYPE_MUCH_VALUE_LENGTH) {
							showValue = DataType.BIT_EXPORT_FORMAT;
						} else {
							showValue = "X'" + DBAttrTypeFormatter.getHexString(dataTmp,
										columnInfo.getPrecision()) + "'";
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
				}else{
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
	private void loadBlobData(ResultSet rs, int columnIndex, CellValue cellValue) throws SQLException {
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
	private void loadClobData(ResultSet rs, int columnIndex, CellValue cellValue) throws SQLException {
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
				buffer.append(buf, 0, len);
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
				String showValue = buffer.substring(0, FieldHandlerUtils.MAX_DISPLAY_CLOB_LENGTH) + "...";
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
		int limit = recordLimit;
		while (rs.next()) {
			cntRecord++;
			//add item data to the end of list
			addTableItemData(rs, -1);
			resultSetDataCache.AddData(BuildCurrentRowData(rs));
			if (recordLimit > 0 && cntRecord >= limit && multiQuerySql == null) {
				final String msg = Messages.bind(Messages.tooManyRecord, limit);
				showQueryTip(msg);
				if (isEnd) {
					break;
				} else {
					limit += recordLimit;
				}
			}
		}
		if (multiQuerySql == null) {
			queryInfo = new QueryInfo(cntRecord, pageLimit);
			queryInfo.setCurrentPage(1);
		}
	}

	/**
	 * Fill paged action into query editor result toolbar
	 *
	 * @param toolBarManager ToolBarManager
	 */
	public void makeActions(ToolBarManager toolBarManager, Table resultTable) {
		toolBarManager.add(filterResultContrItem);
		toolBarManager.add(new Separator());
		lastPageAction = new LastAction(this);
		nextPageAction = new NextAction(this);
		toolBarManager.add(nextPageAction);
		toolBarManager.add(lastPageAction);
		updateActions();
		toolBarManager.update(true);
	}

	/**
	 * Set the action disabled
	 */
	private void disableActions() {
		if (lastPageAction != null) {
			lastPageAction.setEnabled(false);
		}
		if (nextPageAction != null) {
			nextPageAction.setEnabled(false);
		}
	}

	/**
	 * Update paged action state
	 */
	public void updateActions() {
		if (queryInfo.getCurrentPage() >= queryInfo.getPages()) {
			lastPageAction.setEnabled(false);
			nextPageAction.setEnabled(false);
		} else {
			lastPageAction.setEnabled(true);
			nextPageAction.setEnabled(true);
		}
	}

	/**
	 * Make query editor result panel,including table panel and sql text and
	 * message text
	 *
	 * @param resultTbl the Table.
	 * @param sqlText the Text.
	 * @param messageText the StyledText.
	 */
	public void makeResult(final TableSelectSupport tableSelectSupport,
			StyledText messageText, boolean multiQueryResult) {
		this.selectableSupport = tableSelectSupport;
		this.tblResult = tableSelectSupport.getTable();
		logMessageText = messageText;
		int[] queryInfoRange = new int[2];
		int[] queryRange = new int[2];
		StringBuilder resultMessage = new StringBuilder();
		resultMessage.append(getQueryMsg() == null ? "" : getQueryMsg().trim());
		queryInfoRange[0] = 0;
		queryInfoRange[1] = resultMessage.length();
		resultMessage.append(StringUtil.NEWLINE)
				.append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT)
				.append(StringUtil.NEWLINE);
		queryRange[0] = resultMessage.length();
		resultMessage.append(query);
		queryRange[1] = query.length();

		if (!isCommand) {
			ServerInfo serverInfo = database.getServer() == null ? null
					: database.getServer().getServerInfo();
			String fontString = QueryOptions.getFontString(serverInfo);
			Font tmpFont = ResourceManager.getFont(fontString);
			if (tmpFont == null) {
				String[] fontData = QueryOptions.getDefaultFont();
				tmpFont = ResourceManager.getFont(fontData[0],
						Integer.valueOf(fontData[1]),
						Integer.valueOf(fontData[2]));
			}
			font = tmpFont;
			tblResult.setFont(font);
			int[] fontColor = QueryOptions.getFontColor(serverInfo);
			color = ResourceManager.getColor(fontColor[0], fontColor[1],fontColor[2]);
			tblResult.setForeground(color);
			// Set font and foreground
			selectableSupport.getTableCursor().setFont(font);
			selectableSupport.getTableCursor().setForeground(color);
			selectableSupport.setShowDetailOperator(this);
			tblResult.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					selectableSupport.redrawMoreButton();
				}
			});

			if (queryEditor != null) {
				if (!multiQueryResult) {
					createContextMenuItems();
				}

				editor = new ControlEditor(selectableSupport.getTableCursor());
				editor.horizontalAlignment = SWT.LEFT;
				editor.grabHorizontal = true;
				editor.grabVertical = true;

				bindEvents();
				addTableItemToolTips();
			}

			makeColumn();
			makeItem();

			if (!StringUtil.isEmpty(queryPlanLog)) {
				resultMessage.append(StringUtil.NEWLINE).append(
						QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
				resultMessage.append(StringUtil.NEWLINE).append(queryPlanLog);
			}

			if (!StringUtil.isEmpty(statsLog)) {
				resultMessage.append(StringUtil.NEWLINE)
						.append(Messages.queryStat).append(":");
				resultMessage.append(StringUtil.NEWLINE).append(
						QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
				resultMessage.append(StringUtil.NEWLINE).append(statsLog);
			}

			messageText.setText(resultMessage.toString());
		} else {
			resultMessage.append(StringUtil.NEWLINE).append(
					QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
			resultMessage.append(StringUtil.NEWLINE);

			resultMessage.append(textData);
			messageText.setText(resultMessage.toString());

		}
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
		selectableSupport.getTableCursor().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
			}
			public void mouseDoubleClick(MouseEvent e) {
				performEdit();
			}
		});

		selectableSupport.getTableCursor().addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					performEdit();
				}
			}
		});

		selectableSupport.getTableCursor().addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			public void keyReleased(KeyEvent event) {
				if (isEditMode() && event.keyCode == SWT.DEL) {
					deleteRecord(tblResult, null);
				} else if (((event.stateMask & SWT.CTRL) != 0 || (event.stateMask & SWT.COMMAND) != 0)
						&& (event.keyCode == 'c' || event.character == '')) {
					// key press 'ctrl + c' is intercept by editor text so add
					// here
					copySelectedItems();
				} else if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'a') {
					selectableSupport.selectAll();
				}
			}
		});
	}

	/**
	 * Judge is show detail button
	 */
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
		Map<String, CellValue> newValueMap = (Map<String, CellValue>) item.getData(LASTEST_DATA_FLAG);
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
		String dataType = DataType.makeType(
				columnInfo.getType(),
				columnInfo.getChildElementType(),
				columnInfo.getPrecision(),
				columnInfo.getScale());
		cellValue.setFileCharset(charset);
		CellViewer cellViewer = new CellViewer(columnInfo, editMode, charset);

		if (IDialogConstants.OK_ID == cellViewer.openCellViewer(tblResult.getShell(), cellValue)) {
			CellValue newValue = cellViewer.getValue();
			if (!CellViewer.isCellValueEqual(cellValue, newValue)) {
				String showValue = null;
				if (newValue.getValue() == null) {
					showValue = DataType.NULL_EXPORT_FORMAT;
					item.setText(columnIndex, showValue);
					newValueMap.put(dataIndex, newValue);
					updateValue(item, dataMap, newValueMap);
				} else if (newValue.getValue() instanceof String) {
					String strValue = newValue.getValue().toString();
					FormatDataResult result = DBAttrTypeFormatter.format(
							dataType, strValue, null, false, charset, false);
					if (result.isSuccess()) {
						// Update the data
						showValue = newValue.getShowValue();
						item.setText(columnIndex, showValue);
						newValueMap.put(dataIndex, newValue);
						updateValue(item, dataMap, newValueMap);
					} else {
						CommonUITool.openErrorBox(Messages.bind(
								Messages.errTextTypeNotMatch, dataType));
						return;
					}
				} else if (newValue.getValue() instanceof byte[]) {
					if (DataType.DATATYPE_BIT.equalsIgnoreCase(columnInfo.getType()) ||
							DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(columnInfo.getType())) {
						byte[] bValues = (byte[])newValue.getValue();
						if (bValues.length * 8 > columnInfo.getPrecision() + 7) {
							String msg = Messages.bind(Messages.errTextTypeNotMatch, dataType);
							CommonUITool.openErrorBox(msg);
							return;
						}
					}
 					showValue = newValue.getShowValue();
					item.setText(columnIndex, showValue);
					newValueMap.put(dataIndex, newValue);
					updateValue(item, dataMap, newValueMap);
				} else {
 					showValue = newValue.getShowValue();
					item.setText(columnIndex, showValue);
					newValueMap.put(dataIndex, newValue);
					updateValue(item, dataMap, newValueMap);
				}
				selectableSupport.getTableCursor().redraw();
			}
		}
	}

	/**
	 * Perform edit data
	 */
	private void performEdit() {
		if (!editMode) {
			return;
		}

		final TableItem item = selectableSupport.getTableCursor().getRow();
		int rowIndex = tblResult.indexOf(item);
		int columnIndex = selectableSupport.getTableCursor().getColumn();

		ColumnInfo columnInfo = (ColumnInfo)tblResult.getColumn(columnIndex).getData();
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
			text = new StyledText(selectableSupport.getTableCursor(), SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		} else {
			text = new StyledText(selectableSupport.getTableCursor(), SWT.SINGLE | SWT.WRAP | SWT.H_SCROLL);
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
		} else if (DataType.DATATYPE_DATETIME.equals(type) ||
				DataType.DATATYPE_DATE.equals(type) ||
				DataType.DATATYPE_TIMESTAMP.equals(type) ||
				DataType.DATATYPE_TIME.equals(type)) {
			final String fType = type;
			final TableItem fItem = item;
			final int fColumn = columnIndex;
			registerInputMethodContextMenu(text, true, fType, fItem, fColumn);
		} else {
			text.setEditable(true);
			CommonUITool.registerCopyPasteContextMenu(text, true);
		}

		Listener textListener = new TableItemEditor(text, item, rowIndex, columnIndex);
		text.addListener(SWT.FocusOut, textListener);
		text.addListener(SWT.Traverse, textListener);
		text.addListener(SWT.FocusIn, textListener);
		text.addListener(SWT.MouseDown, textListener);
		text.setText(item.getText(columnIndex));
		text.selectAll();

		try {
			text.setFocus();
		} catch (Exception ex) {
		}
	}

	private void updateStyledLogTextForStatistics(StyledText messageText) {
		String text = messageText.getText();
		final String[] titleString = { Messages.queryStat };
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
				"Join graph nodes:", "Join graph equivalence classes:",
				"Join graph edges:", "Join graph terms:", "Query plan:",
				"Query stmt:" };

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
		final String[] titleString = { "Trace Statistics:" };

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
			pstmt = QueryExecuter.getStatement(connection.checkAndConnectQuietly(), query, false, false);
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

				int page = queryInfo.getCurrentPage();
				makeResult(prs);
				queryInfo.setCurrentPage(page);
				makeItem();
				updateActions();

			}
		} catch (final Exception ee) {
			LOGGER.error("execute SQL failed sql  at query editor : " + query + " error message: " + ee);
			ee.printStackTrace();
		} finally {
			QueryUtil.freeQuery(pstmt, prs);
			pstmt = null;
			prs = null;
		}
	}

	/**
	 *
	 * Register input method context menu for styled text
	 *
	 * @param text StyledText
	 * @param isEditable boolean
	 */
	public void registerInputMethodContextMenu(final StyledText text, final boolean isEditable, final String type, final TableItem item, final int column) {
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent event) {
				ActionManager.getInstance().changeFocusProvider(text);
			}

		});

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
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
				IAction inputAction = ActionManager.getInstance().getAction(InputMethodAction.ID);
				if (inputAction instanceof InputMethodAction) {
					manager.add(inputAction);
					if (!inputAction.isEnabled()) {
						FocusAction.changeActionStatus(inputAction, text);
					} else {
						((InputMethodAction)inputAction).setType(type);
						((InputMethodAction)inputAction).setTableItem(item);
						((InputMethodAction)inputAction).setColumn(column);
						((InputMethodAction)inputAction).setQueryExecuter(executer);

					}
					if (isEditMode()) {
						inputAction.setEnabled(true);
					} else {
						inputAction.setEnabled(false);
					}
				}
			}
		});
		Menu contextMenu = menuManager.createContextMenu(text);
		text.setMenu(contextMenu);

		text.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				IAction copyAction = ActionManager.getInstance().getAction(CopyAction.ID);
				if (!copyAction.isEnabled()) {
					FocusAction.changeActionStatus(copyAction, text);
				}
				IAction pasteAction = ActionManager.getInstance().getAction(PasteAction.ID);
				if (pasteAction != null && !pasteAction.isEnabled()) {
					FocusAction.changeActionStatus(pasteAction, text);
				}
				IAction inputAction = ActionManager.getInstance().getAction(InputMethodAction.ID);
				if (inputAction != null) {
					FocusAction.changeActionStatus(inputAction, text);
				}
			}
		});
	}

	private void createContextMenuItems() {
		Menu menu = new Menu(queryEditor.getEditorSite().getShell(), SWT.POP_UP);

		final MenuItem itemInsert = new MenuItem(menu, SWT.PUSH);
		itemInsert.setText(Messages.insertRecord);
		itemInsert.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (!getEditable()) {
					CommonUITool.openErrorBox(Display.getDefault().getActiveShell(), Messages.errNotEditable);
					return;
				}

				insertSaveItem.setEnabled(getEditable());
				rollbackModifiedItem.setEnabled(getEditable());
				addNewItem();
			}
		});

		final MenuItem itemCopy = new MenuItem(menu, SWT.PUSH);
		itemCopy.setText(Messages.copyClipBoard);

		final MenuItem itemDelete = new MenuItem(menu, SWT.PUSH);
		itemDelete.setText(Messages.delete);

		new MenuItem(menu, SWT.SEPARATOR);

		final MenuItem itemDetail = new MenuItem(menu, SWT.PUSH);
		itemDetail.setText(Messages.detailView);
		itemDetail.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				List<Point> selectedList = selectableSupport.getSelectedLocations();
				if (selectedList == null) {
					CommonUITool.openErrorBox(Messages.errShowDetailNoSelected);
					return;
				}
				if (selectedList.size() != 1) {
					CommonUITool.openErrorBox(Messages.errShowDetailMultiSelected);
					return;
				}
				Point location = selectedList.get(0);
				if (location == null) {
					CommonUITool.openErrorBox(Messages.errShowDetailFailed);
					return;
				}

				//Bug fixed by Kevin.Qian. FYI. allDataList is a global query result to the current query.
				int pageBeginIndex = (queryInfo.getCurrentPage() - 1) * queryInfo.getPageSize();
				Map<String, CellValue> map = allDataList.get(pageBeginIndex + location.y);
				TableItem item = tblResult.getItem(location.y);
				ColumnInfo colInfo = allColumnList.get(location.x - 1);
				RowDetailDialog dialog = new RowDetailDialog(tblResult.getShell(), allColumnList, map, item,
					colInfo.getName(), executer);
				dialog.open();
			}
		});

		tblResult.setMenu(menu);
		tblResult.addListener(SWT.MouseUp, new Listener() {
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
////						if (!DataType.VALUE_NULL.equals(value) && containsOIDs(tblResult.getColumn(i).getText())
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
		itemMakeInsertQuery.setImage(CommonUIPlugin.getImage("icons/queryeditor/record_to_insert.png"));

		final MenuItem itemMakeUpdateQuery = new MenuItem(menu, SWT.PUSH);
		itemMakeUpdateQuery.setText(Messages.makeUpdateFromSelectedRecord);
		itemMakeUpdateQuery.setImage(CommonUIPlugin.getImage("icons/queryeditor/record_to_update.png"));

		if (isEmpty()) {
			itemExportAll.setEnabled(false);
			itemExportSelection.setEnabled(false);
			itemDetail.setEnabled(false);
		}

		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent event) {
				TableItem[] tblItems = selectableSupport.getSelectedTableItems();
				boolean selectedCol = selectableSupport.hasSelected();

				itemExportSelection.setEnabled(selectedCol);
				itemCopy.setEnabled(selectedCol);

				if (allDataList.size() > 0) {
					itemExportAll.setEnabled(true);
				} else {
					itemExportAll.setEnabled(false);
				}

				if (executer.getQueryEditor() != null && executer.getQueryEditor().getDatabaseInfo() != null
						&& executer.getQueryEditor().getDatabaseInfo().equals(executer.getDatabaseInfo())) {
					itemInsert.setEnabled(isEditMode());
					itemDetail.setEnabled(selectedCol);
					itemDelete.setEnabled(getEditable() && isEditMode());
				} else {
					itemInsert.setEnabled(false);
					itemDetail.setEnabled(false);
					itemDelete.setEnabled(false);
				}

				for (int i = 0; i < tblItems.length; i++) {
					if (isEditMode() && getEditable() && tblItems[i].getText(1).equals( DataType.VALUE_NONE)) {
						itemDelete.setEnabled(false);
					}
				}
			}
		});

		itemCopy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				copySelectedItems();
			}
		});
		itemCopy.setAccelerator(SWT.CTRL + 'c');

		itemDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isEditMode()) {
					deleteRecord(tblResult, null);
				}
			}
		});

		itemExportAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ExportResultDialog dialog = new ExportResultDialog(tblResult.getShell(), database,
					Messages.msgExportAllResults);
				if (IDialogConstants.OK_ID == dialog.open()) {
					ExportQueryResultTaskExecutor task = new ExportQueryResultTaskExecutor(dialog.getFile(),
						dialog.getFileCharset(), allColumnList, allDataList, false);
					new ExecTaskWithProgress(task).exec();
				}
			}
		});

//		itemOid.addSelectionListener(new SelectionAdapter() {
//			/**
//			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
//			 * @param event an event containing information about the
//			 *        selection
//			 */
//			public void widgetSelected(SelectionEvent event) {
//				openOidNavigator(button2Position);
//			}
//
//		});
		itemExportSelection.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ExportResultDialog dialog = new ExportResultDialog(tblResult.getShell(), database,
					Messages.msgExportSelectedResults);
				if (IDialogConstants.OK_ID == dialog.open()) {
					ExportQueryResultTaskExecutor task = new ExportQueryResultTaskExecutor(
							dialog.getFile(),
							dialog.getFileCharset(),
							tblResult,
							selectableSupport.getSelectedTableItems(), false);
					new ExecTaskWithProgress(task).exec();
				}
			}
		});

		itemMakeInsertQuery.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String text = makeInsertQueryWithSelectedRecords();
				CommonUITool.copyContentToClipboard(text);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		itemMakeUpdateQuery.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String text = makeUpdateQueryWithSelectedRecords();
				CommonUITool.copyContentToClipboard(text);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
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
		String tableName = UIQueryUtil.getTableNameFromQuery(connection.checkAndConnectQuietly(), query);
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
				values.append(getFormatedValue(colInfo, data));

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
			try {
				FormatDataResult formatResult = DBAttrTypeFormatter.formatForInput(
						colInfo.getComleteType(), data, true, charset, false);
				if (formatResult.isSuccess()
						&& formatResult.getFormatedString() != null) {
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
			CUBRIDPreparedStatementProxy pstmt = QueryExecuter.getStatement(
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
				sql.append(getFormatedValue(colInfo, data));
			}

			sql.append(" WHERE ");
			int count = 0;

			for (int i = 0; i < colCount; i++) {
				ColumnInfo colInfo = cols.get(i);
				String colName = QuerySyntax.escapeKeyword(colInfo.getName());
				String data = item.getText(i + 1);

				if (!pkList.contains(colName)) {
					continue;
				}

				if (count > 0) {
					sql.append(" AND ");
				}

				count++;

				sql.append(colName);
				if (QueryEditorPart.isNullEmpty(colInfo.getType(), data)) {
					sql.append(" IS NULL");
				} else if ("(NULL)".equals(data)) {
					sql.append(" IS NULL");
				} else if (QueryEditorPart.isNotNeedQuote(colInfo.getType())) {
					sql.append("=").append(data);
				} else {
					sql.append("='").append(data.replaceAll("'", "''")).append("'");
				}
			}

			sql.append(";").append(StringUtil.NEWLINE);
		}

		return sql.toString();
	}

	/**
	 * add tips to table item.
	 */
	private void addTableItemToolTips() {
		Listener tableListener = new Listener() {
			Shell tip = null;
			Label label = null;
			public void handleEvent(Event event) {
				switch (event.type) {
		        	case SWT.Dispose:
		        	case SWT.KeyDown:
		        	case SWT.MouseMove: {
		        		if (tip != null) {
		        			tip.dispose();
		        			tip = null;
		        			label = null;
		        		}
		        		break;
		        	}
		        	case SWT.MouseHover: {
		        		TableItem item = tblResult.getItem(new Point(event.x, event.y));
		        		if (item != null) {
			        		SQLException ex = (SQLException)item.getData(INSERT_RECORD_FAIL);
				            if ( ex == null ) {
				            	return;
				            }
		        			if (tip != null && !tip.isDisposed()) {
		        				tip.dispose();
		        			}
			        		tip = new Shell(tblResult.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
			        		tip.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			        		FillLayout layout = new FillLayout();
			        		layout.marginWidth = 2;
			        		tip.setLayout(layout);
			        		label = new org.eclipse.swt.widgets.Label(tip, SWT.NONE);
				            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				            label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				            label.setText(
				            		Messages.runError + ex.getErrorCode() +
				            		StringUtil.NEWLINE +
				            		Messages.errorHead + ex.getMessage());
				            Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				            Rectangle rect = item.getBounds(0);
				            Point pt = tblResult.toDisplay(rect.x, rect.y + rect.height);
				            tip.setBounds(pt.x, pt.y, size.x, size.y);
				            tip.setVisible(true);
		        		}
		        	}
				}//end of switch
			}//end of handleEvent
		};//end of tableListener
	    tblResult.addListener(SWT.Dispose, tableListener);
	    tblResult.addListener(SWT.KeyDown, tableListener);
	    tblResult.addListener(SWT.MouseMove, tableListener);
	    tblResult.addListener(SWT.MouseHover, tableListener);
	}

	/**
	 * make query editor result table column
	 */
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
			ColumnInfo columnInfo = (ColumnInfo)allColumnList.get(j);
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
			tblColumn[j + 1].addSelectionListener(new SelectionListener() {
				@SuppressWarnings("unchecked")
				public void widgetSelected(SelectionEvent event) {
					TableColumn column = (TableColumn)event.widget;
					if (column == null || column.getText() == null || column.getText().trim().length() == 0) {
						return;
					}

					if (isEditMode()) {
						CommonUITool.openWarningBox(Messages.errCanNotSortWhenEditing);
						return;
					}

					TableColumn sortedColumn = tblResult.getSortColumn();
					int width = column.getWidth();

					ColumnInfo columnInfo = (ColumnInfo)column.getData();
					ColumnComparator comparator = colComparatorMap.get(columnInfo.getIndex());
					tblResult.setSortColumn(column);
					tblResult.setSortDirection(comparator.isAsc() ? SWT.UP : SWT.DOWN);
					Collections.sort(allDataList, comparator);
					comparator.setAsc(!comparator.isAsc());
					makeItem();
					updateActions();

					column.pack();
					if (column.equals(sortedColumn)) {
						column.setWidth(width);
					} else {
						column.setWidth(width + 25);
					}
					tblResult.showColumn(column);
					selectableSupport.setSelection(new ArrayList<Point>());
				}

				public void widgetDefaultSelected(SelectionEvent event) {
				}
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
	public static CUBRIDPreparedStatementProxy getStatement(Connection conn, String sql, boolean doesGetOidInfo,
		boolean isSecond) throws SQLException {
		try {
			CUBRIDPreparedStatementProxy stmt = (CUBRIDPreparedStatementProxy)conn.prepareStatement(sql,
				ResultSet.TYPE_SCROLL_INSENSITIVE, doesGetOidInfo ? ResultSet.CONCUR_UPDATABLE
					: ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
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

	/**
	 * make query editor result table
	 *
	 * @param start int
	 * @throws SQLException if failed
	 */
	public TuneModeModel makeTable(int start, boolean useTuneMode) throws SQLException {
		int end = start + recordLimit - 1;
		String sql = multiQuerySql;
		if (multiQuerySql.indexOf(SqlParser.ROWNUM_CONDITION_MARK) != -1) {
			if (dontTipNext) {
				if (recordLimit > 0) {
					sql = this.multiQuerySql.replace(SqlParser.ROWNUM_CONDITION_MARK,
						"\r\nWHERE ROWNUM  >= " + String.valueOf(start));
				}
			} else {
				sql = this.multiQuerySql.replace(SqlParser.ROWNUM_CONDITION_MARK,
					"\r\nWHERE ROWNUM BETWEEN " + String.valueOf(start) + " AND " + String.valueOf(end));
			}
		}

		TuneModeModel tuneModeModel = null;
		long beginTimestamp = 0;
		long endTimestamp = 0;
		double elapsedTime = 0.0;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		stmt = null;
		rs = null;
		boolean isHasError = false;

		try {
			beginTimestamp = System.currentTimeMillis();
			stmt = getStatement(connection.checkAndConnectQuietly(), sql, false, false);
			stmt.setQueryInfo(false);
			stmt.setOnlyQueryPlan(false);

			// begin tune mode
			if (queryEditor.isCollectExecStats()) {
				queryEditor.beginCollectExecStats();
			}
			if (columnTableNames == null || columnTableNames.size() == 0) {
				columnTableNames = UIQueryUtil.loadColumnTableNameList(stmt);
			}
			stmt.executeQuery();

			String queryPlan = null;

			rs = (CUBRIDResultSetProxy) stmt.getResultSet();

			endTimestamp = System.currentTimeMillis();
			elapsedTime = (endTimestamp - beginTimestamp) * 0.001;
			String elapsedTimeStr = new String(nf.format(elapsedTime));

			if (start == 1) {
				makeResult(rs);
			} else {
				fillTableItemData(rs);
			}

			// collect statistics and query plan on tune mode
			if (useTuneMode && queryEditor.isCollectExecStats()) {
				Map<String, String> stat = CubridUtil.fetchStatistics(connection.checkAndConnectQuietly());

				StructQueryPlan sq = new StructQueryPlan(sql,
						stmt.getQueryplan(sql), new Date());
				queryPlan = sq.getPlanRaw();

				tuneModeModel = new TuneModeModel(sq, stat);
			}

			queryMsg += "[ " + elapsedTimeStr + " " + Messages.second + " , " + Messages.totalRows + " : "
				+ cntRecord + " ]" + StringUtil.NEWLINE;

			if (useTuneMode && queryEditor.isCollectExecStats() && queryPlan != null) {
				this.queryPlanLog = queryPlan;
			}

			query += sql + StringUtil.NEWLINE;
			recordSQLDetail(elapsedTimeStr.toString(), queryMsg);
		} catch (SQLException event) {
			queryMsg += Messages.runError + event.getErrorCode() + StringUtil.NEWLINE + Messages.errorHead
				+ event.getMessage() + StringUtil.NEWLINE;
			query += sql + StringUtil.NEWLINE;
			isHasError = true;
			LOGGER.error("execute SQL failed sql at query editor: " + query
					+ " error message: " + event.getMessage(), event);
			throw event;
		} finally {
			queryInfo = new QueryInfo(allDataList == null ? 0 : allDataList.size(), pageLimit);
			queryInfo.setCurrentPage(1);
			QueryUtil.freeQuery(stmt, rs);
			stmt = null;
			rs = null;
			if (!isHasError && cntRecord == recordLimit && recordLimit > 0) {
				isEnd = false;
				final String msg = Messages.bind(Messages.tooManyRecord, end);
				showQueryTip(msg);
				if (!isEnd) {
					makeTable(end + 1, false);
				}
			}
		}

		return tuneModeModel;
	}

	private void recordSQLDetail(String elapseTime, String info) {
		elapseTime = elapseTime.trim();
		try {
			if (elapseTime.split(" ").length > 1) {
				double totalTime = 0;
				for (String time : elapseTime.split(" ")) {
					totalTime += Double.valueOf(time);
				}
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(3);
				nf.setMinimumFractionDigits(3);
				elapseTime = nf.format(totalTime);
			}
		} catch(Exception e) {
			LOGGER.error("parse execute sql time error", e);
		}
		sqlDetailHistory.setExecuteInfo(info);
		sqlDetailHistory.setElapseTime(elapseTime);
	}

	/**
	 * show the query tip
	 *
	 * @param msg String
	 */
	private void showQueryTip(final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				if (!dontTipNext) {

					MessageDialog dialog = new MessageDialog(queryEditor.getEditorSite().getShell(), Messages.warning, null,
						msg, MessageDialog.QUESTION, new String[] {Messages.btnYes, Messages.btnNo}, 1) {
						Button btn = null;

						/**
						 * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
						 * @param parent parent composite to contain the custom
						 *        area
						 * @return the custom area control, or <code>null</code>
						 */
						protected Control createCustomArea(Composite parent) {
							btn = new Button(parent, SWT.CHECK);
							btn.setText(Messages.showOneTimeTip);
							return btn;
						}

						/**
						 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
						 * @param buttonId the id of the button that was pressed
						 *        (see <code>IDialogConstants.*_ID</code>
						 *        constants)
						 */
						protected void buttonPressed(int buttonId) {
							dontTipNext = btn.getSelection();
							if (dontTipNext) {
								QueryOptions.setMultiPageConfirm(false);
								QueryOptions.savePref();
							}
							if (buttonId == 1) {
								isEnd = true;
							}
							close();
						}

					};
					int ret = dialog.open();
					if (ret != 0 && ret != 1) {
						isEnd = true;
					}
				}
			}
		});
	}

	/**
	 * make a table item by its data.
	 *
	 * @param item			a TableItem instance
	 * @param mapItemData	a item data instance with type of Map<String, Object>
	 */
	private void makeItemValue(TableItem item, Map<String, CellValue> mapItemData) {
		for (int j = 0; allColumnList != null && j < allColumnList.size(); j++) {
			String columnIndex = allColumnList.get(j).getIndex();
			String type = allColumnList.get(j).getType();
			CellValue colValue = mapItemData.get(columnIndex);
			if (colValue == null || colValue.getShowValue() == null) {
				item.setText(j + 1, DataType.NULL_EXPORT_FORMAT);
				item.setBackground(j + 1,
						Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
				item.setData((j + 1) + "", DataType.VALUE_NULL);
			} else if (DataType.DATATYPE_BLOB.equalsIgnoreCase(type)) {
				item.setText(j + 1, DataType.BLOB_EXPORT_FORMAT);
				item.setBackground(j + 1,
						Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			} else if (DataType.DATATYPE_CLOB.equalsIgnoreCase(type)) {
				item.setText(j + 1, DataType.CLOB_EXPORT_FORMAT);
				item.setBackground(j + 1,
						Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			} else if ((DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(type) || DataType.DATATYPE_BIT.equalsIgnoreCase(type))
					&& DataType.BIT_EXPORT_FORMAT.equals(colValue.getShowValue())) {
				String data = colValue.getShowValue();
				item.setText(j + 1, data);
				item.setBackground(j + 1,
						Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			} else {
				item.setText(j + 1, colValue.getShowValue());
			}
		}
	}

	/**
	 * make table item by the data in allDataList
	 */
	public void makeItem() {
		isSingleTableQuery = false;

		if (!queryEditor.isCollectExecStats()) {
			String tableName = null;
			if (columnTableNames != null && columnTableNames.size() > 0) {
				tableName = columnTableNames.get(0);
				if (tableName != null) {
					isSingleTableQuery = true;
					for (int i = 1, len = columnTableNames.size(); i < len; i++) {
						if (!tableName.equals(columnTableNames.get(i))) {
							isSingleTableQuery = false;
							break;
						}
					}
				}
			}

			if (isSingleTableQuery) {
				int matchedCnt = 0;
				List<String> pkList = UIQueryUtil.getPkList(getDatabaseInfo(), tableName);
				for (String pk : pkList) {
					for (int j = 0; allColumnList != null && j < allColumnList.size(); j++) {
						String columnName = allColumnList.get(j).getName();
						if (pk.equalsIgnoreCase(columnName)) {
							matchedCnt++;
						}
					}
				}

				if (matchedCnt > 0 && matchedCnt == pkList.size()) {
					isContainPrimayKey = true;
				} else {
					isContainPrimayKey = false;
				}
			}
		}

		if (insertRecordItem != null && !insertRecordItem.isDisposed()) {
			insertRecordItem.setEnabled(getEditable() && isEditMode());
		}

		disableActions();
		clearModifiedLog();
		tblResult.removeAll();
		rsToItemMap.clear();

		int begin = (queryInfo.getCurrentPage() - 1) * queryInfo.getPageSize();
		int last = begin + queryInfo.getPageSize();

		List<Point> matchedPointList = new ArrayList<Point>();

		int itemNo = 0;
		int index = (queryInfo.getCurrentPage() - 1) * queryInfo.getPageSize() + 1;
		for (int i = 0; allDataList != null && i < last && i < queryInfo.getTotalRs(); i++) {
			Map<String, CellValue> dataMap = allDataList.get(i);

			// filter the data
			boolean isAccepted = filterResultContrItem.select(dataMap, filterSetting);
			if (!isAccepted) {
				continue;
			}

			TableItem item = new TableItem(tblResult, SWT.MULTI);
			rsToItemMap.put(""+item.hashCode(), ""+i);
			item.setText(0, String.valueOf(index + i - begin));
			item.setData(dataMap);
			makeItemValue(item, dataMap);
			item.setBackground(0, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

			int columnNum = 1;
			for (int j = 0; allColumnList != null && j < allColumnList.size(); j++, columnNum++) {
				ColumnInfo columnInfo = allColumnList.get(j);
				String columnIndex = columnInfo.getIndex();

				// display compare data for multiple queries
				if (multiResultsCompare == true && baseQueryExecuter != null) {
					compareTableItemData(item, i, columnIndex);
				}

				Object colValue = dataMap.get(columnIndex);
				String showValue = null;
				if (colValue instanceof String) {
					showValue = (String) colValue;
				} else if (colValue instanceof CellValue) {
					showValue = ((CellValue) colValue).getShowValue();
				}

				if (showValue == null) {
					item.setText(columnNum, DataType.NULL_EXPORT_FORMAT);
					item.setData((columnNum) + "", DataType.VALUE_NULL);
				} else {
					item.setText(columnNum, showValue);
				}

				if (DataType.isSelfDefinedData(showValue)) {
					item.setBackground(columnNum, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
				}

				// Select the matched data
				if (showValue != null && filterResultContrItem.isMatch(filterSetting, showValue, columnInfo)) {
					Point ponit = new Point(j + 1, itemNo);
					matchedPointList.add(ponit);
				}
			}

			itemNo++;
		}

		if (filterResultContrItem.isUseFilter()) {
			selectableSupport.setSelection(matchedPointList);
		}

		tblResult.setTopIndex(begin);

		//recover and display the inserted records which have not been committed yet.
//		for (Map.Entry<String,Map<String,Object>> itemEntry : allInsertedItemList.entrySet()) {
//			TableItem itemNew = new TableItem(tblResult, SWT.MULTI);
//			changeInsertedItemStyle(itemNew);
//			itemNew.setData(NEW_RECORD_FLAG, itemEntry.getKey());
//			int colCount = tblResult.getColumnCount();
//			for (int i = 1; i < colCount; i++) {
//				itemNew.setText(i, "" + itemEntry.getValue().get("" + (i - 1)));
//			}
//		}

		if (delRecordItem != null && !delRecordItem.isDisposed()) {
			delRecordItem.setEnabled(false);
		}
	}

	/**
	 * Compare data and mart item foreground
	 */
	private void compareTableItemData(TableItem item, int row, String columnIndex) {
		boolean dataDiff = false, extraRow = false, extraColumn = false;
		CellValue cellValue0 = executer.getAllDataList().get(row).get(
				columnIndex);
		int baseRowSize = baseQueryExecuter.getAllDataList().size();
		if (row < baseRowSize) {
			int baseColumnSize = baseQueryExecuter.getAllDataList().get(row).size();
			if (Integer.parseInt(columnIndex) <= baseColumnSize) {
				CellValue cellValue1 = baseQueryExecuter.getAllDataList().get(row).get(columnIndex);
				if (isCellValueEqual(cellValue0, cellValue1)) {
					dataDiff = false;
				} else {
					dataDiff = true;
				}
			} else {
				extraColumn = true;
			}
		} else {
			extraRow = true;
		}

		if (dataDiff == true) {
			item.setForeground(Integer.parseInt(columnIndex), RED_COLOR);
		} else if (extraColumn == true) {
			item.setForeground(Integer.parseInt(columnIndex), GREEN_COLOR);
		} else if (extraRow) {
			item.setForeground(Integer.parseInt(columnIndex), BLUE_COLOR);
		}
	}

	/**
	 * Judge the value is equal
	 *
	 * @param value0
	 * @param value1
	 * @return
	 */
	private boolean isCellValueEqual(CellValue value0, CellValue value1) {
		if (value0 == null || value1 == null) {
			if (value0 == null && value1 == null) {
				return true;
			}
			return false;
		}

		// Judge the data is null
		if (value0.getValue() == null || value1.getValue() == null) {
			if (value0.getValue() == null && value1.getValue() == null) {
				return true;
			}
			return false;
		}

		if (!value0.getValue().getClass().equals(value1.getValue().getClass())) {
			return false;
		}

		// TODO replace another way
		if (value0.getValue() instanceof File
				&& value1.getValue() instanceof File) {
			File oldFile = (File) value0.getValue();
			File newFile = (File) value1.getValue();
			return oldFile.getAbsolutePath().equals(newFile.getAbsolutePath())
					&& oldFile.lastModified() == newFile.lastModified();
		} else if (value0.getValue() instanceof byte[]
				&& value1.getValue() instanceof byte[]) {
			byte[] oldBytes = (byte[]) value0.getValue();
			byte[] newBytes = (byte[]) value1.getValue();
			if (oldBytes.length != newBytes.length) {
				return false;
			} else {
				for (int i = 0; i < oldBytes.length; i++) {
					if (oldBytes[i] != newBytes[i]) {
						return false;
					}
				}
				return true;
			}
		} else if (value0.getValue() instanceof java.sql.Date
				&& value1.getValue() instanceof java.sql.Date) {
			return ((java.sql.Date) value0.getValue()).getTime() == ((java.sql.Date) value1.getValue()).getTime();
		}

		return StringUtil.isEqual(value0.getShowValue(), value1.getShowValue());
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
	 * @param item  a new-inserted item
	 */
	private void changeInsertedItemStyle(TableItem item) {
		if (item == null) return;
		item.setImage(0, CommonUIPlugin.getImage("icons/action/table_record_adding.png"));

		// Get Max index
		int maxIndex = -1;
		Item[] allItems = tblResult.getItems();
		for(Item it : allItems) {
			if(it.getText() != null) {
				int index = StringUtil.intValue(it.getText(), -1);
				if(index > maxIndex) {
					maxIndex = index;
				}
			}
		}
		maxIndex++;

		item.setText(0, String.valueOf(maxIndex));
		item.setBackground(0, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		//item.setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		//item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		tblResult.showItem(item);
	}

	/**
	 * add a new item into table result
	 * @return a new added TableItem
	 */
	public TableItem addNewItem() {
		TableItem itemNew = new TableItem(tblResult, SWT.MULTI);
		changeInsertedItemStyle(itemNew);
		selectableSupport.setSelection(itemNew, 0);

		String key = ""+itemNew.hashCode();
		// The data is necessary
		Map<String, CellValue> dataMap = new HashMap<String, CellValue>();
		itemNew.setData(dataMap);
		itemNew.setData(LASTEST_DATA_FLAG, dataMap);
		itemNew.setData(NEW_RECORD_FLAG, key);
		insValues.put(key, dataMap);
		return itemNew;
	}

	public void updateValue(TableItem item, Map<String, CellValue> oldValue,
			Map<String, CellValue> newValue) {
		String key = ""+item.hashCode();
		if (this.insValues.get(key) != null) {
			return;
		}
		if (this.oldValues.get(key) == null) {
			this.oldValues.put(key, oldValue);
		}
		this.newValues.put(key, newValue);
		insertSaveItem.setEnabled(getEditable());
		rollbackModifiedItem.setEnabled(getEditable());
	}

	/**
	 * update row data
	 *
	 * @param oid String
	 * @param value String[]
	 * @param colName String[]
	 * @throws SQLException if failed
	 */
	public void updateValue(String oid, String[] colName, Object[] value) throws SQLException {
		queryEditor.updateResult(oid, colName, value);
	}

	/**
	 * Save all new inserted/updated records to database
	 * @throws ParamSetException
	 */
	@SuppressWarnings("unchecked")
	public boolean saveInsertedUpdatedDeletedRecords() throws SQLException, ParamSetException {
		logMessageText.setText("");

		// delete
		if (delValues.size() > 0) {
			Map<String, Map<String, CellValue>> deleteValuesMap = deleteValues();
			if (deleteValuesMap.size() == 0) {
				return false;
			}
		}

		// insert
		int insertCounts = 0;
		if (insValues.size() > 0) {
			Map<String, TableItem> insertedTableItems = new HashMap<String, TableItem>();
			for (TableItem recordItem : tblResult.getItems()) {
				String key = ""+recordItem.hashCode();
				Map<String, CellValue> newValue = (Map<String, CellValue>)recordItem.getData(LASTEST_DATA_FLAG);
				if (insValues.containsKey(key) && newValue != null) {
					Map<String, CellValue> insertValue = new HashMap<String, CellValue>();
					insertValue.putAll(newValue);
					insValues.put(key, insertValue);
					insertedTableItems.put(key, recordItem);
				}
				insertCounts++;
			}
			Map<String, Map<String, CellValue>> insertValuesMap = insertValues();
			if(insertValuesMap.size() == 0) {
				return false;
			}
		}

		// update
		if (oldValues.size() > 0 && newValues.size() > 0) {
			Map<String, Map<String, CellValue>> updateValuesMap = updateValues();
			if (updateValuesMap.size() == 0) {
				return false;
			}
		}

		clearModifiedLog();

		if (insertCounts > 0) {
			reloadQuery();
		}

		try {
			for (CombinedQueryEditorComposite combinedQueryEditorComposite : getQueryEditor().getAllCombinedQueryEditorComposite()) {
				combinedQueryEditorComposite.getRecentlyUsedSQLComposite().refreshRecentlyUsedSQLList();
			}
		} catch (Exception ignored) {
		}
		return true;
	}

	public void clearModifiedLog() {
		oldValues.clear();
		newValues.clear();
		insValues.clear();
		delValues.clear();
	}

	/**
	 * delete the record
	 *
	 * @param selection TableItem[]
	 */
	@SuppressWarnings("unchecked")
	protected void deleteRecord(Table table, TableItem[] selection) {
		if (selection == null) {
			selection = selectableSupport.getSelectedTableItems();
			if (selection.length == 0) {
				return;
			}
		} else {
			if (selection.length == 0) {
				return;
			}
		}

		ArrayList<TableItem> itemsWithOIDList = new ArrayList<TableItem>();
		ArrayList<TableItem> itemsInsertedList = new ArrayList<TableItem>();
		// dispatch the selected items into corresponding list
		for (TableItem item : selection) {
			if (isNewInsertedRecordItem(item)) {
				itemsInsertedList.add(item);
				insValues.remove(""+item.hashCode());
			} else{
				itemsWithOIDList.add(item);
				delValues.put(""+item.hashCode(), (Map<String, CellValue>)item.getData());
				insertSaveItem.setEnabled(getEditable());
				rollbackModifiedItem.setEnabled(getEditable());
			}
		}

		TableItem[] itemsInserted = new TableItem[itemsInsertedList.size()];
		itemsInserted = itemsInsertedList.toArray(itemsInserted);

		TableItem[] itemsWithOID = new TableItem[itemsWithOIDList.size()];
		itemsWithOID = itemsWithOIDList.toArray(itemsWithOID);
		selection = itemsWithOID;

		for (TableItem item : itemsWithOIDList) {
			item.dispose();
		}

//			String[] oid = new String[selection.length];
//			for (int i = 0; i < selection.length; i++) {
//				oid[i] = selection[i].getText(1);
//			}

//			try {
//				qe.deleteResult(oid);
//				for (int i = 0; i < oid.length; i++) {
//					if (allDataList == null) {
//						break;
//					}
//					for (int j = 0; j < allDataList.size(); j++) {
//						Map<String, Object> deletedRecordMap = allDataList.get(j);
//						if (deletedRecordMap.get("0").equals(oid[i])) {
//							allDataList.remove(deletedRecordMap);
//							queryInfo.setTotalRs(queryInfo.getTotalRs() - 1);
//							break;
//						}
//					}
//				}
//			deleteNewInsertedRecords(itemsInserted);

		for (TableItem item : itemsInsertedList) {
			item.dispose();
		}
//				int iDeletedItemCount = oid.length + itemsInserted.length;
//				CommonTool.openInformationBox(Messages.delete,
//						Messages.bind(Messages.deleteOk, iDeletedItemCount));
//				makeItem();
//				updateActions();
//			} catch (SQLException event) {
//				CommonTool.openErrorBox(event.getErrorCode() + CommonTool.NEWLINE + Messages.errorHead
//					+ event.getMessage());
//			}
//		}
	}

	public void copySelectedItems(){
		CommonUITool.clearClipboard();

		StringBuilder content = new StringBuilder();
		List<Point> selectedList = selectableSupport.getSelectedLocations();

		int currentRow = -1;
		int len = selectedList.size();
		for (int i = 0; i < len; i++) {
			Point location = selectedList.get(i);
			if (i == 0) {
				currentRow = location.y;
			} else if(currentRow != location.y){
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

	/**
	 * dispose the object.
	 */
	public void dispose() {
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
		freeResultSetCache();
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

	public QueryEditorPart getQueryEditor() {
		return queryEditor;
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

	/**
	 * Delete values on the query result editor.
	 *
	 * @param queryConn Connection
	 * @throws SQLException the exception
	 */
	private Map<String, Map<String, CellValue>> deleteValues() throws SQLException, ParamSetException {
		ParamSetter paramSetter = new ParamSetter();
		Map<String, Map<String, CellValue>> successedMap = new HashMap<String, Map<String, CellValue>>();

		Connection conn = connection.checkAndConnect();
		try {
			String tableName = UIQueryUtil.getTableNameFromQuery(conn, query);
			String escapedTable = QuerySyntax.escapeKeyword(tableName);
			if (tableName == null) {
				CommonUITool.openErrorBox(Messages.errModifiedOneTable);
				return successedMap;
			}

			PreparedStatement pstmt = null;
			List<ColumnInfo> allColumnList = getAllColumnList();

			for (String key : delValues.keySet()) {
				Map<String, CellValue> valuesMap = delValues.get(key);
				if (valuesMap == null) {
					continue;
				}
				try {
					List<ColumnInfo> colInfoList = new ArrayList<ColumnInfo>();
					List<ColumnInfo> unPColInfoList = new ArrayList<ColumnInfo>();

					for (int i = 0; i < allColumnList.size(); i++) {
						ColumnInfo colInfo = allColumnList.get(i);
						if (queryEditor.isIgnoreType(colInfo.getType())) {
							continue;
						}

						CellValue value = valuesMap.get(colInfo.getIndex());
						if (value == null || value.getValue() == null) {
							continue;
						}

						if (DataType.DATATYPE_NATIONAL_CHARACTER.equalsIgnoreCase(colInfo.getType())
								|| DataType.DATATYPE_NCHAR_VARYING.equalsIgnoreCase(colInfo.getType())
								|| DataType.DATATYPE_NCHAR.equalsIgnoreCase(colInfo.getType())) {
							unPColInfoList.add(colInfo);
							continue;
						}

						if ((DataType.DATATYPE_BIT.equalsIgnoreCase(colInfo.getType()) || DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(colInfo.getType()))
								&& value.getValue() instanceof String) {
							unPColInfoList.add(colInfo);
							continue;
						}

						colInfoList.add(colInfo);
					}

					StringBuilder sqlBuffer = new StringBuilder();
					sqlBuffer.append("DELETE FROM ").append(QuerySyntax.escapeKeyword(escapedTable)).append(" WHERE ");

					List<PstmtParameter> pstmtParaList = new ArrayList<PstmtParameter>();
					int paramCount = 1;
					for (ColumnInfo columnInfo : colInfoList) {
						if (paramCount > 1) {
							sqlBuffer.append(" AND ");
						}
						sqlBuffer.append(QuerySyntax.escapeKeyword(columnInfo.getName())).append(" = ? ");

						CellValue value = valuesMap.get(columnInfo.getIndex());
						PstmtParameter pstmtParameter = new PstmtParameter(
								columnInfo.getName(), paramCount,
								columnInfo.getComleteType(), value.getValue());
						pstmtParaList.add(pstmtParameter);
						paramCount++;
					}

					for (ColumnInfo columnInfo : unPColInfoList) {
						if (paramCount > 1) {
							sqlBuffer.append(" AND ");
						}
						sqlBuffer.append(QuerySyntax.escapeKeyword(columnInfo.getName())).append("=");
						CellValue cellValue = valuesMap.get(columnInfo.getIndex());
						String dataType = DataType.makeType(columnInfo.getType(),
								columnInfo.getChildElementType(),
								columnInfo.getPrecision(), columnInfo.getScale());

						FormatDataResult result = DBAttrTypeFormatter.format(
								dataType, cellValue.getStringValue(), null, false,
								charset, false);
						if (result.isSuccess()) {
							sqlBuffer.append(result.getFormatedString());
						} else {
							throw new ParamSetException("Format data \"" + cellValue.getStringValue() + "\"error for data type " + dataType);
						}

						paramCount++;
					}

					pstmt = conn.prepareStatement(sqlBuffer.toString());
					for (PstmtParameter pstmtParameter : pstmtParaList) {
						paramSetter.handle(pstmt, pstmtParameter);
					}
					pstmt.executeUpdate();
					successedMap.put(key, valuesMap);

					if (!connection.isAutoCommit() && queryEditor.getConnection() == connection) {
						queryEditor.setHaveActiveTransaction(true);
					}
				} catch (SQLException ex) {
					if (successedMap.containsKey(key)) {
						successedMap.remove(key);
					}
					LOGGER.error("", ex);
					throw ex;
				} finally {
					QueryUtil.freeQuery(pstmt);
				}
			}
		} finally {
			if (connection != null && connection.isAutoClosable()) {
				connection.commit();
				connection.close();
			}
		}
		return successedMap;
	}

	/**
	 * Insert values on the query result editor.
	 *
	 * @return
	 * @throws SQLException
	 */
	private Map<String, Map<String, CellValue>> insertValues() throws SQLException, ParamSetException {
		Map<String, Map<String, CellValue>> successedMap = new HashMap<String, Map<String, CellValue>>();
		if (insValues == null || insValues.size() == 0) {
			return successedMap;
		}

		ParamSetter paramSetter = new ParamSetter();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);

		Connection conn = connection.checkAndConnect();
		try {
			String tableName = UIQueryUtil.getTableNameFromQuery(conn, query);
			String escapedTable = QuerySyntax.escapeKeyword(tableName);
			if (tableName == null) {
				CommonUITool.openErrorBox(Messages.errModifiedOneTable);
				return successedMap;
			}

			PreparedStatement pstmt = null;
			List<ColumnInfo> allColumnList = getAllColumnList();
			for (String key : insValues.keySet()) {
				Map<String, CellValue> valuesMap = insValues.get(key);
				if (valuesMap == null) {
					continue;
				}
				try {
					List<ColumnInfo> colInfoList = new ArrayList<ColumnInfo>();
					List<ColumnInfo> unPColInfoList = new ArrayList<ColumnInfo>();
					for (int i = 0; i < allColumnList.size(); i++) {
						ColumnInfo colInfo = allColumnList.get(i);
						if (queryEditor.isIgnoreType(colInfo.getType())) {
							continue;
						}

						CellValue value = valuesMap.get(colInfo.getIndex());
						if (value == null || value.getValue() == null) {
							continue;
						}

						if (DataType.DATATYPE_NATIONAL_CHARACTER.equalsIgnoreCase(colInfo.getType())
								|| DataType.DATATYPE_NCHAR_VARYING.equalsIgnoreCase(colInfo.getType())
								|| DataType.DATATYPE_NCHAR.equalsIgnoreCase(colInfo.getType())) {
							unPColInfoList.add(colInfo);
							continue;
						}

						if ((DataType.DATATYPE_BIT.equalsIgnoreCase(colInfo.getType()) || DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(colInfo.getType()))
								&& value.getValue() instanceof String) {
							unPColInfoList.add(colInfo);
							continue;
						}

						colInfoList.add(colInfo);
					}

					StringBuilder sqlBuffer = new StringBuilder();
					sqlBuffer.append("INSERT INTO ").append(escapedTable).append(" (");
					int paramCount = 0;
					for (ColumnInfo columnInfo : colInfoList) {
						if (paramCount > 0) {
							sqlBuffer.append(",");
						}
						sqlBuffer.append(QuerySyntax.escapeKeyword(columnInfo.getName()));
						paramCount++;
					}
					for (ColumnInfo columnInfo : unPColInfoList) {
						if (paramCount > 0) {
							sqlBuffer.append(",");
						}
						sqlBuffer.append(QuerySyntax.escapeKeyword(columnInfo.getName()));
						paramCount++;
					}
					sqlBuffer.append(") VALUES (");

					int dataIndex = 1;
					List<PstmtParameter> pstmtParaList = new ArrayList<PstmtParameter>();
					for (ColumnInfo columnInfo : colInfoList) {
						if (dataIndex > 1) {
							sqlBuffer.append(",");
						}
						sqlBuffer.append("?");

						CellValue value = valuesMap.get(columnInfo.getIndex());
						PstmtParameter pstmtParameter = new PstmtParameter(
								columnInfo.getName(), dataIndex,
								columnInfo.getComleteType(), value.getValue());
						pstmtParaList.add(pstmtParameter);
						dataIndex++;
					}

					String charset = getDatabaseInfo() != null ? getDatabaseInfo().getCharSet() : null;
					for (ColumnInfo columnInfo : unPColInfoList) {
						if (dataIndex > 1) {
							sqlBuffer.append(",");
						}
						CellValue value = valuesMap.get(columnInfo.getIndex());
						String dataType = DataType.makeType(columnInfo.getType(),
								columnInfo.getChildElementType(),
								columnInfo.getPrecision(), columnInfo.getScale());

						FormatDataResult result = DBAttrTypeFormatter.format(
								dataType, value.getStringValue(), null, false,
								charset, false);
						if (result.isSuccess()) {
							sqlBuffer.append(result.getFormatedString());
						} else {
							throw new ParamSetException("Format data \"" + value.getStringValue() + "\"error for data type " + dataType);
						}
					}
					sqlBuffer.append(")");
					pstmt = conn.prepareStatement(sqlBuffer.toString());
					for (PstmtParameter pstmtParameter : pstmtParaList) {
						paramSetter.handle(pstmt, pstmtParameter);
					}
					pstmt.executeUpdate();
					successedMap.put(key, valuesMap);

					if (!connection.isAutoCommit() && queryEditor.getConnection() == connection) {
						queryEditor.setHaveActiveTransaction(true);
					}
				} catch (SQLException e) {
					if (successedMap.containsKey(key)) {
						successedMap.remove(key);
					}
					LOGGER.error("", e);
					logMessageText.setText(e.getLocalizedMessage());
					throw e;
				} finally {
					QueryUtil.freeQuery(pstmt);
				}
			}
		} finally {
			if (connection != null && connection.isAutoClosable()) {
				connection.commit();
				connection.close();
			}
		}
		return successedMap;
	}

	/**
	 * Update values on the query result editor.
	 *
	 * @param queryConn Connection
	 * @return
	 * @throws SQLException
	 * @throws ParamSetException
	 */
	private Map<String, Map<String, CellValue>> updateValues() throws SQLException, ParamSetException {
		Map<String, Map<String, CellValue>> successedMap = new HashMap<String, Map<String, CellValue>>();
		ParamSetter paramSetter = new ParamSetter();
		if (oldValues == null || oldValues.size() == 0) {
			return successedMap;
		}
		if (newValues == null || newValues.size() == 0) {
			return successedMap;
		}

		Connection conn = connection.checkAndConnect();
		try {
			String tableName = UIQueryUtil.getTableNameFromQuery(conn, query);
			String escapedTable = QuerySyntax.escapeKeyword(tableName);
			if (tableName == null) {
				CommonUITool.openErrorBox(Messages.errModifiedOneTable);
				return successedMap;
			}

			List<ColumnInfo> colInfoList = getAllColumnList();
			PreparedStatement pstmt = null;

			for (String key : oldValues.keySet()) {
				try {
					Map<String, CellValue> oldValueMap = oldValues.get(key);
					Map<String, CellValue> newValueMap = newValues.get(key);
					if (oldValueMap == null || oldValueMap.size() == 0
							|| newValueMap == null || newValueMap.size() == 0) {
						continue;
					}
					StringBuilder updateSQLBuffer = new StringBuilder();
					List<ColumnInfo> updatedColInfoList = new ArrayList<ColumnInfo>();
					List<CellValue> newValueList = new ArrayList<CellValue>();

					for (int i = 0; i < colInfoList.size(); i++) {
						ColumnInfo colInfo = colInfoList.get(i);
						CellValue newValue = newValueMap.get(colInfo.getIndex());
						CellValue oldValue = oldValueMap.get(colInfo.getIndex());
						if ((oldValue == null && newValue != null) || (newValue == null && oldValue != null)) {
							newValueList.add(newValue);
							updatedColInfoList.add(colInfo);
						} else if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
							newValueList.add(newValue);
							updatedColInfoList.add(colInfo);
						}
					}
					if (updatedColInfoList.isEmpty()) {
						continue;
					}

					updateSQLBuffer.append("UPDATE ").append(escapedTable).append(" SET ");
					StringBuilder setSQLBf = new StringBuilder();
					List<PstmtParameter> pstmtParaList = new ArrayList<PstmtParameter>();
					int valueParamIndex = 1;
					for (int i = 0; i < updatedColInfoList.size(); i++) {
						ColumnInfo colInfo = updatedColInfoList.get(i);
						CellValue newValue = newValueMap.get(colInfo.getIndex());
						String colName = colInfo.getName();
						if (queryEditor.isIgnoreType(colInfo.getType())) {
							continue;
						}
						if (setSQLBf.length() > 0) {
							setSQLBf.append(", ");
						}
						CellValue cellValue = newValueMap.get(colInfo.getIndex());
						if (DataType.DATATYPE_NATIONAL_CHARACTER.equalsIgnoreCase(colInfo.getType())
								|| DataType.DATATYPE_NCHAR_VARYING.equalsIgnoreCase(colInfo.getType())
								|| DataType.DATATYPE_NCHAR.equalsIgnoreCase(colInfo.getType())) {
							String dataType = DataType.makeType(colInfo.getType(),
									colInfo.getChildElementType(),
									colInfo.getPrecision(), colInfo.getScale());
							String charset = getDatabaseInfo() != null ? getDatabaseInfo().getCharSet() : null;
							FormatDataResult result = DBAttrTypeFormatter.format(
									dataType, cellValue.getStringValue(), null, false, charset, false);
							if (result.isSuccess()) {
								setSQLBf.append(QuerySyntax.escapeKeyword(colName));
								setSQLBf.append(" = ").append(result.getFormatedString());
							} else {
								throw new ParamSetException("Format data \"" + cellValue.getStringValue() + "\"error for data type " + dataType);
							}
						} else if ((DataType.DATATYPE_BIT.equalsIgnoreCase(colInfo.getType()) || DataType.DATATYPE_BIT_VARYING.equalsIgnoreCase(colInfo.getType()))
								&& newValue.getValue() instanceof String) {
							String dataType = DataType.makeType(colInfo.getType(),
									colInfo.getChildElementType(),
									colInfo.getPrecision(), colInfo.getScale());
							String charset = getDatabaseInfo() != null ? getDatabaseInfo().getCharSet() : null;
							FormatDataResult result = DBAttrTypeFormatter.format(
									dataType, cellValue.getStringValue(), null, false, charset, false);
							setSQLBf.append(QuerySyntax.escapeKeyword(colName));
							setSQLBf.append(" = ").append(result.getFormatedString());
						} else {
							setSQLBf.append(QuerySyntax.escapeKeyword(colName)).append(" = ?");
							PstmtParameter pstmtParameter = new PstmtParameter(
									colInfo.getName(), valueParamIndex++, colInfo.getComleteType(), cellValue.getValue());
							pstmtParaList.add(pstmtParameter);
						}
					}
					if (setSQLBf.length() < 1) {
						continue;
					}
					updateSQLBuffer.append(setSQLBf);
					updateSQLBuffer.append(" WHERE ");

					List<String> pkList = UIQueryUtil.getPkList(getDatabaseInfo(), tableName);
					int pkParamIndex = 0;
					for (int i = 0; i < newValueMap.size(); i++) {
						ColumnInfo colInfo = ((ColumnInfo) getAllColumnList().get(i));
						String col = colInfo.getName();
						if (!pkList.contains(col)) {
							continue;
						}
						if (queryEditor.isIgnoreType(colInfo.getType())) {
							continue;
						}
						if (pkParamIndex > 0) {
							updateSQLBuffer.append(" AND ");
						}

						updateSQLBuffer.append(QuerySyntax.escapeKeyword(col)).append(" = ?");
						CellValue object = oldValueMap.get(colInfo.getIndex());

						PstmtParameter pstmtParameter = new PstmtParameter(
								colInfo.getName(), valueParamIndex++, colInfo.getComleteType(), object.getValue());
						pstmtParaList.add(pstmtParameter);
						pkParamIndex++;
					}

					pstmt = conn.prepareStatement(updateSQLBuffer.toString());
					for (PstmtParameter pstmtParameter : pstmtParaList) {
						paramSetter.handle(pstmt, pstmtParameter);
					}
					pstmt.executeUpdate();
					successedMap.put(key, newValueMap);

					if (!connection.isAutoCommit() && queryEditor.getConnection() == connection) {
						queryEditor.setHaveActiveTransaction(true);
					}

					for (ColumnInfo colInfo : updatedColInfoList) {
						CellValue newValue = newValueMap.get(colInfo.getIndex());
						CellValue oldValue = oldValueMap.get(colInfo.getIndex());
						if (newValue != null && oldValue != null) {
							oldValue.setValue(newValue.getValue());
						}
					}
				} catch (SQLException e) {
					if (successedMap.containsKey(key)) {
						successedMap.remove(key);
					}
					LOGGER.error("", e);
					logMessageText.setText(e.getLocalizedMessage());
					throw e;
				} finally {
					QueryUtil.freeQuery(pstmt);
				}
			}
		} finally {
			if (connection != null && connection.isAutoClosable()) {
				connection.commit();
				connection.close();
			}
		}
		return successedMap;
	}

	private void freeResultSetCache(){
		if(resultSetDataCache == null){
			return;
		}
		resultSetDataCache.free();
		resultSetDataCache = null;
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

	public void setSqlDetailHistory(SQLHistoryDetail sqlDetailHistory) {
		this.sqlDetailHistory = sqlDetailHistory;
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

	public void setMultiResultsCompare(boolean multiResultsCompare) {
		this.multiResultsCompare = multiResultsCompare;
	}

	public boolean getMultiResultsCompare() {
		return this.multiResultsCompare;
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

	/**
	 * Table item editor
	 *
	 * @author pangqiren
	 * @version 1.0 - 2009-12-18 created by pangqiren
	 */
	private class TableItemEditor implements Listener {
		private boolean isRunning = false;
		private final TableItem item;
		private final int column;
		private final StyledText text;
		private Shell shell;
		private DateTimeComponent dateTimeComponent;
		private int dateTimeComponentWidth = 300;
		private int dateTimeComponentHeight = 230;

		public TableItemEditor(StyledText text, TableItem item, int row, int column) {
			this.text = text;
			this.item = item;
			this.column = column;
			shell = new Shell(Display.getDefault().getActiveShell(),
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			shell.setText("");
			shell.setLayout(new GridLayout());
			shell.setLayoutData(new GridData(GridData.FILL_BOTH));

			dateTimeComponent = new DateTimeComponent(shell, SWT.BORDER);
			dateTimeComponent.setLayout(new GridLayout());
			dateTimeComponent.setLayoutData(new GridData(GridData.FILL_BOTH));
			Point dateTimeComponentSize = dateTimeComponent.componentSize();
			dateTimeComponentWidth = dateTimeComponentSize.x;
			dateTimeComponentHeight = dateTimeComponentSize.y;
			shell.setSize(dateTimeComponentWidth,dateTimeComponentHeight);
		}

		/**
		 * to process cell value editing event on inserting time.
		 * @param event  source event
		 */
		@SuppressWarnings("unchecked")
		public void processInsertingEditEvent(Event event) {
			int oldCol = column;
			String showValue = text.getText();
			String value = text.getText();
			Map<String, CellValue> newValMap = (Map<String, CellValue>) item.getData(LASTEST_DATA_FLAG);
			if (newValMap != null) {
				if (DataType.VALUE_NULL.equals(value)) {
					showValue = DataType.VALUE_NULL;
					value = null;
				}
				if (DataType.VALUE_NULL.equals(showValue)) {
					item.setBackground(oldCol, null);
				}
				item.setText(column, showValue);
				newValMap.put("" + column, new CellValue(value, showValue));
			}
		}

		/**
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 * @param event the event which occurred
		 */
		public void handleEvent(final Event event) {
			text.setEditable(getEditable() && isEditMode());

			int oldCol = column;
			if (event.type == SWT.FocusOut) {
				focusOutAction(event, oldCol);
				return;
			} else if (event.type == SWT.Traverse) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					focusOutAction(event, oldCol);
					return;
				}
				else if (event.detail == SWT.TRAVERSE_ESCAPE) {
					if (isRunning) {
						return;
					}
					isRunning = true;
					text.dispose();
					event.doit = false;
					isRunning = false;
				}
			} else if (event.type == SWT.FocusIn) {
				if (!getQueryEditor().isConnected()) {
					CommonUITool.openErrorBox(Display.getDefault().getActiveShell(),
							Messages.errMsgExecuteInResult);
					event.doit = false;
					isRunning = false;
					text.dispose();
					return;
				}
				String attrType = ((ColumnInfo) tblResult.getColumn(column).getData()).getType();
				if (queryEditor.isIgnoreType(attrType)) {
					CommonUITool.openErrorBox(Display.getDefault().getActiveShell(),
							Messages.bind(Messages.errEditableOnResultTab,attrType));
					event.doit = false;
					isRunning = false;
					text.dispose();
					return;
				}
				/*
				if (attrType.equalsIgnoreCase("DATE")
						|| attrType.equalsIgnoreCase("DATETIME")
						|| attrType.equalsIgnoreCase("TIMESTAMP")) {

					//compute location
					Point p = Display.getDefault().getCursorLocation();
					Rectangle screenSize = Display.getDefault().getClientArea();
					if (p.x + dateTimeComponentWidth > screenSize.width) {
						p.x = screenSize.width - dateTimeComponentWidth - 50;
					}
					if (p.y + dateTimeComponentHeight > screenSize.height) {
						p.y = screenSize.height - dateTimeComponentHeight - 50 ;
					}

					shell.setLocation(p);
					shell.open();
					while (!shell.isDisposed()) {
						if (!Display.getDefault().readAndDispatch())
							Display.getDefault().sleep();
					}
					//if select a date
					if (dateTimeComponent.getReturnDateValue() != null) {
						if (isNewInsertedRecordItem(item) ) {//if this is a new record
							if (attrType.equalsIgnoreCase("DATE")) {
								item.setText(column, dateTimeComponent.getReturnDateValue());
							} else if (attrType.equalsIgnoreCase("TIMESTAMP")) {
								item.setText(column, dateTimeComponent.getReturnTimestampValue());
							} else {
								item.setText(column, dateTimeComponent.getReturnDateTimeValue());
							}
						} else {
							//if the date value is changed
							if ((attrType.equalsIgnoreCase("DATE") && item.getText(column)
									.equals(dateTimeComponent.getReturnDateValue()))
									//timestamp or date time
									|| (item.getText(column).equals(dateTimeComponent.getReturnDateTimeValue()))) {
								return ;
							}
//							if (CommonTool.openConfirmBox(Messages.cfmUpdateChangedValue)) {
//								try {
									if (attrType.equalsIgnoreCase("DATE")) {
										item.setText(column, dateTimeComponent.getReturnDateValue());
									} else if (attrType.equalsIgnoreCase("TIMESTAMP")) {
										item.setText(column, dateTimeComponent.getReturnTimestampValue());
									} else {
										item.setText(column, dateTimeComponent.getReturnDateTimeValue());
									}
//									updateValue(row, column);
									if (DataType.VALUE_NULL.equals(item.getData(oldCol
											+ ""))) {
										item.setData(oldCol + "", "");
										item.setBackground(oldCol, null);
									}

									//TODO: pendingUpdateQueries.add(sql);
//								} catch (SQLException e1) {
//									CommonTool.openErrorBox(e1.getErrorCode()
//											+ CommonTool.NEWLINE
//											+ e1.getMessage());
//									tblResult.getItem(oldRow).setText(oldCol, old);
//									event.doit = false;
//									if (DataType.VALUE_NULL.equals(item.getData(oldCol + ""))) {
//										item.setText(oldCol, DataType.NULL_EXPORT_FORMAT);
//									}
//								}
//							}
						}

					}
				}
				*/
			}
		}

		@SuppressWarnings("unchecked")
		private void focusOutAction(final Event event, int oldCol) {
			if (isRunning) {
				return;
			}
			if (text == null) {
				return;
			}
			isRunning = true;

			boolean isChanged = !text.getText().equals(item.getText(column));
			if (isChanged) {
				if (isNewInsertedRecordItem(item) ) {
					processInsertingEditEvent(event);
				} else {
					Map<String, CellValue> oldValueMap = (Map<String, CellValue>) item.getData();
					item.setText(column, text.getText());

					Map<String, CellValue> newValMap = (Map<String, CellValue>) item.getData(LASTEST_DATA_FLAG);
					if (newValMap == null) {
						newValMap = new HashMap<String, CellValue>();
						newValMap.putAll(oldValueMap);
						item.setData(LASTEST_DATA_FLAG, newValMap);
					}
					newValMap.put(String.valueOf(column), new CellValue(item.getText(column), item.getText(column)));
					updateValue(item, oldValueMap, newValMap);

					if (DataType.VALUE_NULL.equals(item.getData(""+oldCol))) {
						item.setData(""+oldCol, "");
						item.setBackground(oldCol, null);
					}
				}
			} else if ("".equals(text.getText())) {
				if (DataType.VALUE_NULL.equals(item.getData(""+oldCol))) {
					item.setText(oldCol, DataType.NULL_EXPORT_FORMAT);
				} else {
					item.setText(column, "");
				}
			}
			text.dispose();
			isRunning = false;
		}
	}

	public void setColumnTableNames(List<String> tableNames) {
		this.columnTableNames = tableNames;
	}
	
	public ResultSetDataCache getTableDataCache() {
		return resultSetDataCache;
	}
}
