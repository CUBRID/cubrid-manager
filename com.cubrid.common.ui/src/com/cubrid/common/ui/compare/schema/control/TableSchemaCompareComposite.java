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
package com.cubrid.common.ui.compare.schema.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareUpdateDDL;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlDatabaseInfoMapper;
import com.cubrid.common.ui.cubrid.database.erwin.WrappedDatabaseInfo;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.GetInfoDataUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetViewAllColumnsTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;

/**
 * Table Schema Compare Composite
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.15 created by Ray Yin
 */
public class TableSchemaCompareComposite extends
		Composite {
	private static final Logger LOGGER = LogUtil.getLogger(TableSchemaCompareComposite.class);

	private final CTabFolder tabFolder;
	private CTabItem tabItem;
	private TextMergeViewer textMergeViewer;
	private Map<String, TextMergeViewer> textMergeViewerMap;
	private TableSchemaCompareModel compSchemaModel;
	private TableSchema l_tableSchema;
	private TableSchema r_tableSchema;
	private CubridDatabase sourceDB;
	private CubridDatabase targetDB;
	private final Image schema_compare_icon = CommonUIPlugin.getImage("icons/navigator/schema_table_partition.png");
	public static final Color BACK_COLOR = ResourceManager.getColor(136, 161, 227);

	/**
	 * The constructor
	 *
	 * @param parent
	 * @param style
	 */
	public TableSchemaCompareComposite(CTabFolder parent, int style) {
		super(parent, style);
		this.tabFolder = parent;
		this.textMergeViewerMap = new HashMap<String, TextMergeViewer>();
		GridLayout tLayout = new GridLayout(1, true);
		tLayout.verticalSpacing = 0;
		tLayout.horizontalSpacing = 0;
		tLayout.marginWidth = 0;
		tLayout.marginHeight = 0;

		setLayout(tLayout);
	}

	public void initialize() {
		SashForm tailSash = new SashForm(tabFolder, SWT.VERTICAL);
		tailSash.SASH_WIDTH = 1;
		tailSash.setBackground(BACK_COLOR);

		Composite tableComp = new Composite(tailSash, SWT.NONE);
		tableComp.setLayout(new FillLayout());

		createSchemaViewer(tableComp);

		tabItem = new TableSchemaCompareCTabItem(tabFolder, SWT.NONE, this);
		tabItem.setControl(tailSash);
		tabItem.setShowClose(true);
		tabItem.setImage(schema_compare_icon);

		String tabItemText = l_tableSchema.getName();
		if (StringUtil.isEmpty(l_tableSchema.getName())) {
			tabItemText = r_tableSchema.getName();
		}
		tabItem.setText(tabItemText);
		tabItem.setData("Source", tabItemText);
		tabItem.setData("Target",r_tableSchema.getName());
		tabFolder.setSelection(tabItem);
	}

	public void createSchemaViewer(Composite parent) {
		String sourceSchema = getTableSchema(sourceDB,
				compSchemaModel.getSourceSchemas(), l_tableSchema.getName());

		String targetSchema = null;
		if (targetDB.isVirtual()) {
			targetSchema = r_tableSchema.getSchemaInfo();
		} else {
			targetSchema = getTableSchema(targetDB,
					compSchemaModel.getTargetSchemas(), r_tableSchema.getName());
		}
		if (targetSchema == null)
			targetSchema = "";

		String source_tableName = Messages.statusMissing;
		String table_Name = "";
		if (StringUtil.isNotEmpty(sourceSchema)) {
			source_tableName = l_tableSchema.getName();
			table_Name = source_tableName;
		}
		String target_tableName = Messages.statusMissing;
		if (StringUtil.isNotEmpty(targetSchema)) {
			target_tableName = r_tableSchema.getName();
			table_Name = target_tableName;
		}

		CompareConfiguration config = new CompareConfiguration();
		config.setProperty(CompareConfiguration.SHOW_PSEUDO_CONFLICTS, Boolean.FALSE);
		config.setProperty(CompareConfiguration.IGNORE_WHITESPACE, Boolean.TRUE);
		config.setLeftEditable(false);
		String sourceDbName = "";
		if (sourceDB != null && sourceDB.getDatabaseInfo() != null
				&& sourceDB.getDatabaseInfo().getBrokerIP() != null
				&& sourceDB.getName() != null) {
			sourceDbName += sourceDB.getDatabaseInfo().getBrokerIP()
					+ "@" + sourceDB.getName() + " : ";
		}
		config.setLeftLabel(sourceDbName + source_tableName);
		config.setRightEditable(false);
		String targetDbName = "";
		if (targetDB != null && targetDB.getDatabaseInfo() != null
				&& targetDB.getDatabaseInfo().getBrokerIP() != null
				&& targetDB.getName() != null) {
			targetDbName += targetDB.getDatabaseInfo().getBrokerIP()
					+ "@" + targetDB.getName() + " : ";
		}
		config.setRightLabel(targetDbName + target_tableName);
		DiffNode schemaDiffNode = new DiffNode(null, Differencer.CHANGE, null,
				new TextCompareInput(sourceSchema), new TextCompareInput(targetSchema));

		textMergeViewer = new TextMergeViewer(parent, config);
		textMergeViewer.setInput(schemaDiffNode);

		if (!textMergeViewerMap.containsKey(table_Name)) {
			textMergeViewerMap.put(table_Name, textMergeViewer);
		}
	}

	public void refreshMergeViewer(String table_Name) {
		if (!textMergeViewerMap.containsKey(table_Name)) {
			return;
		}

		String sourceSchema = getTableSchema(sourceDB,
				compSchemaModel.getSourceSchemas(), l_tableSchema.getName());

		String targetSchema = null;
		if (targetDB.isVirtual()) {
			targetSchema = r_tableSchema.getSchemaInfo();
		} else {
			targetSchema = getTableSchema(targetDB,
					compSchemaModel.getTargetSchemas(), r_tableSchema.getName());
		}
		if (targetSchema == null) {
			targetSchema = "";
		}

		DiffNode schemaDiffNode = new DiffNode(null, Differencer.CHANGE, null,
				new TextCompareInput(sourceSchema), new TextCompareInput(
						targetSchema));

		TextMergeViewer selectedViewer = textMergeViewerMap.get(table_Name);
		selectedViewer.setInput(schemaDiffNode);
		selectedViewer.refresh();
	}

	private String getTableSchema(CubridDatabase db,
			Map<String, SchemaInfo> schemas, String tableName) { // FIXME logic code move to core module
		String tableSchemaInfo = "";

		try {
			SchemaInfo schemaInfo = schemas.get(tableName);
			if (schemaInfo == null) {
				return "";
			}
			if (schemaInfo.getVirtual().equals(ClassType.VIEW.getText())) {
				GetAllClassListTask getAllClassListTask = new GetAllClassListTask(
						db.getDatabaseInfo());
				getAllClassListTask.setTableName(tableName);
				getAllClassListTask.getClassInfoTaskExcute();
				ClassInfo classInfo = getAllClassListTask.getClassInfo();

				GetAllAttrTask getAllAttrTask = new GetAllAttrTask(
						db.getDatabaseInfo());
				getAllAttrTask.setClassName(tableName);
				getAllAttrTask.getAttrList();
				List<DBAttribute> attrList = getAllAttrTask.getAllAttrList();

				List<Map<String, String>> viewColListData = GetInfoDataUtil.getViewColMapList(attrList);

				/*Get view column*/
				GetViewAllColumnsTask getAllDBVclassTask = new GetViewAllColumnsTask(
						db.getDatabaseInfo());
				getAllDBVclassTask.setClassName(tableName);
				getAllDBVclassTask.getAllVclassListTaskExecute();

				/*Get query list*/
				List<String> vclassList = getAllDBVclassTask.getAllVclassList();
				List<Map<String, String>> queryListData = new ArrayList<Map<String, String>>();
				for (String sql : vclassList) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("0", sql);
					queryListData.add(map);
				}

				tableSchemaInfo = GetInfoDataUtil.getViewCreateSQLScript(true,
						db, classInfo, tableName, viewColListData,
						queryListData);
			} else {
				SchemaDDL schemaDDL = null;
				schemaDDL = new SchemaDDL(null, db.getDatabaseInfo());
				tableSchemaInfo = schemaDDL.getSchemaDDL(schemaInfo);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
			return "";
		}

		return tableSchemaInfo;
	}

	public void setInput(TableSchemaCompareModel compSchemaModel) {
		this.compSchemaModel = compSchemaModel;
		this.sourceDB = compSchemaModel.getSourceDB();
		this.targetDB = compSchemaModel.getTargetDB();
		this.l_tableSchema = (TableSchema) compSchemaModel.getLeft();
		this.r_tableSchema = (TableSchema) compSchemaModel.getRight();
	}

	/**
	 * Returns table schema update/alter script
	 *
	 * @param sourceDatabase
	 * @param targetDatabase
	 * @param tableCompare
	 */
	public String getTableAlterScript(CubridDatabase sourceDatabase,
			CubridDatabase targetDatabase, String tableCompare,
			SchemaInfo sourceSchemaInfo, SchemaInfo targetSchemaInfo) { // FIXME logic code move to core module
		String alterDDL = null;

		try {
			DatabaseInfo source_dbInfo = sourceDatabase.getDatabaseInfo();
			DatabaseInfo target_dbInfo = targetDatabase.getDatabaseInfo();

			if (sourceSchemaInfo == null
					|| !"normal".equals(sourceSchemaInfo.getVirtual()))
				sourceSchemaInfo = null;

			if (targetSchemaInfo == null
					|| !"normal".equals(targetSchemaInfo.getVirtual()))
				targetSchemaInfo = null;

			SchemaChangeManager sourceChangeManger = new SchemaChangeManager(
					source_dbInfo, false);
			SchemaDDL sourceSchemaDDL = new SchemaDDL(sourceChangeManger,
					sourceDatabase.getDatabaseInfo());

			SchemaChangeManager targetChangeManger = new SchemaChangeManager(
					target_dbInfo, false);
			SchemaDDL targetSchemaDDL = null;
			if (targetDatabase.isVirtual()) {
				WrappedDatabaseInfo info = ERXmlDatabaseInfoMapper.getWrappedDatabaseInfo(target_dbInfo);
				targetSchemaDDL = new SchemaDDL(targetChangeManger, info);
			} else {
				targetSchemaDDL = new SchemaDDL(targetChangeManger,
						targetDatabase.getDatabaseInfo());
			}

			TableSchemaCompareUpdateDDL tableCompareDDL = new TableSchemaCompareUpdateDDL(
					sourceChangeManger, sourceSchemaDDL, targetSchemaDDL,
					sourceSchemaInfo, targetSchemaInfo);
			alterDDL = tableCompareDDL.getTableSchemaAlterDDL();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return alterDDL;
	}

	/**
	 * A self CTabItem class ,store SchemaDetailInfoTableInfoComposite
	 */
	public class TableSchemaCompareCTabItem extends
			CTabItem {
		private TableSchemaCompareComposite tableInfoComposite;

		public TableSchemaCompareCTabItem(CTabFolder parent, int style,
				TableSchemaCompareComposite tableInfoComposite) {
			super(parent, style);
			this.tableInfoComposite = tableInfoComposite;
		}

		public TableSchemaCompareComposite getTableInfoComposite() {
			return tableInfoComposite;
		}

		public void setTableInfoComposite(
				TableSchemaCompareComposite tableInfoComposite) {
			this.tableInfoComposite = tableInfoComposite;
		}
	}
}
