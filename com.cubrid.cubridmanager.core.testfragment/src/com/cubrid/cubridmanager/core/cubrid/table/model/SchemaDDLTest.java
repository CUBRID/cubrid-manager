/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution. 
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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import java.util.ArrayList;
import java.util.List;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.ConstraintNamingUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog.SchemeInnerType;
import com.cubrid.cubridmanager.core.cubrid.table.task.CheckSubClassTask;

/**
 * test SchemaDDL model
 * 
 * @author wuyingshi
 * @version 1.0 - 2010-1-5 created by wuyingshi
 */
public class SchemaDDLTest extends
		SetupJDBCTestCase {
	String testTableName = "testSchemaDDLTest";
	String createTestTableSQL = null;
	private String createSuperSQL1;
	private String createSuperSQL2;

	{
		ApplicationUtil.setApplicationType(ApplicationType.CUBRID_MANAGER);
	}

	private boolean createTestTable() throws Exception {
		String filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/table/model/test.message/sup1.txt");
		String msg = Tool.getFileContent(filepath);
		createSuperSQL1 = msg;
		String[] strs = msg.split(";");
		if (createSuperSQL1 != null) {
			for (String str : strs) {
				if (!str.trim().equals("")) {
					executeDDL(str);
				}
			}
		}
		filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/table/model/test.message/sup2.txt");
		msg = Tool.getFileContent(filepath);
		createSuperSQL2 = msg;
		strs = msg.split(";");
		if (createSuperSQL2 != null) {
			for (String str : strs) {
				if (!str.trim().equals("")) {
					executeDDL(str);
				}
			}
		}
		filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/table/model/test.Schema/test.txt");
		msg = Tool.getFileContent(filepath);
		createSuperSQL1 = msg;
		strs = msg.split(";");
		if (createSuperSQL1 != null) {
			for (String str : strs) {
				if (!str.trim().equals("")) {
					executeDDL(str);
				}
			}
		}
		filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/table/model/test.Schema/testSuperTableName.txt");
		msg = Tool.getFileContent(filepath);
		createSuperSQL1 = msg;
		strs = msg.split(";");
		if (createSuperSQL1 != null) {
			for (String str : strs) {
				if (!str.trim().equals("")) {
					executeDDL(str);
				}
			}
		}
		filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/table/model/test.Schema/testTableName.txt");
		msg = Tool.getFileContent(filepath);
		createSuperSQL2 = msg;
		strs = msg.split(";");
		if (createSuperSQL2 != null) {
			for (String str : strs) {
				if (!str.trim().equals("")) {
					executeDDL(str);
				}
			}
		}
		filepath = this.getFilePathInPlugin("/com/cubrid/cubridmanager/core/cubrid/table/model/test.message/testTable.txt");
		msg = Tool.getFileContent(filepath);
		createTestTableSQL = msg;
		strs = msg.split(";");
		boolean success = true;
		if (createTestTableSQL != null) {
			for (String str : strs) {
				if (!str.trim().equals("")) {
					success = executeDDL(str);
				}
			}
		}
		/**
		 * Check CheckSubClassTask
		 */
		CheckSubClassTask task = new CheckSubClassTask(databaseInfo);
		assertTrue(task.checkSubClass("sup1"));
		
//		assertFalse(task.checkSubClass("sup1"));
		task.setErrorMsg("Error");
		assertFalse(task.checkSubClass("sup1"));
		
		return success;
	}
	
	private boolean dropTestTable() {
		String sql = "DROP TABLE IF EXISTS \"" + testTableName + "\"";
		executeDDL(sql);
		sql = "DROP TABLE IF EXISTS sup2";
		executeDDL(sql);
		sql = "DROP TABLE IF EXISTS testSuperTableName";
		executeDDL(sql);
		sql = "DROP TABLE IF EXISTS testTableName";
		executeDDL(sql);
		sql = "DROP TABLE IF EXISTS \"test\"";
		executeDDL(sql);
		sql = "DROP TABLE IF EXISTS sup1";
		return executeDDL(sql);
	}

	private String trimSQL(String sql) {
		if (sql == null || sql.indexOf("\n") == -1) {
			return sql;
		}
		String[] strs = sql.split("\n");
		StringBuffer bf = new StringBuffer();
		for (String str : strs) {
			String trim = str.trim();
			if (!trim.equals("")) {
				bf.append(trim).append("\n");
			}
		}
		return bf.toString();
	}

	public void testGetDDL() throws Exception {
		boolean success = createTestTable();
		assertTrue(success);
		SchemaInfo sup1 = databaseInfo.getSchemaInfo("sup1");
		SchemaInfo sup2 = databaseInfo.getSchemaInfo("sup2");
		SchemaInfo schema = databaseInfo.getSchemaInfo(testTableName);
		SchemaChangeManager changeList = new SchemaChangeManager(databaseInfo,
				false);
		SchemaDDL ddl = new SchemaDDL(changeList, databaseInfo);
		String retSQL = ddl.getSchemaDDL(schema);
		assertNotSame(trimSQL(createTestTableSQL), trimSQL(retSQL));
		retSQL = ddl.getSchemaDDL(sup1);
		trimSQL(createSuperSQL1);
		trimSQL(retSQL);
		retSQL = ddl.getSchemaDDL(sup2);
		assertNotSame(trimSQL(createSuperSQL2), trimSQL(retSQL));
		SchemaInfo newSchema = schema.clone();
		assertTrue(newSchema.equals(schema));
		newSchema.toString();
		
		ddl.getPKsDDL(sup1);
		ddl.getFKsDDL(sup1);

	}

	@Override
	protected void tearDown() throws Exception {
		dropTestTable();
		super.tearDown();
	}

	/**
	 * test method List<List<String>> getSuperclassChanges()
	 * 
	 */
	public void testSuperClassChanged() {
		SchemaDDL ddl = new SchemaDDL(null, null);
		List<List<String>> result = null;
		List<String> oldSupers = new ArrayList<String>();
		List<String> newSupers = new ArrayList<String>();

		result = ddl.getSuperclassChanges(oldSupers, newSupers);
		assertEquals(result.size(), 0);

		oldSupers.add("1");
		oldSupers.add("2");
		oldSupers.add("3");

		newSupers.add("2"); //expect remove "1"
		newSupers.add("4"); //expect remove "3"
		newSupers.add("1");

		result = ddl.getSuperclassChanges(oldSupers, newSupers);
		assertEquals(result.size(), 2);
		assertEquals(result.get(0).toString(), "[1, 3]");
		assertEquals(result.get(1).toString(), "[4, 1]");

		oldSupers.clear();
		newSupers.clear();

		newSupers.add("2");
		newSupers.add("4");
		newSupers.add("1");

		result = ddl.getSuperclassChanges(oldSupers, newSupers);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).toString(), "[2, 4, 1]");

		oldSupers.clear();
		newSupers.clear();
		oldSupers.add("1");
		oldSupers.add("2");
		oldSupers.add("3");
		oldSupers.add("4");
		oldSupers.add("5");

		newSupers.add("1");
		newSupers.add("2");
		newSupers.add("5");

		result = ddl.getSuperclassChanges(oldSupers, newSupers);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).toString(), "[4, 3]");

		newSupers.clear(); //expect remove "1, 2, 3, 4, 5"
		result = ddl.getSuperclassChanges(oldSupers, newSupers);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).toString(), "[1, 2, 3, 4, 5]");

		oldSupers.clear();
		newSupers.clear();
		oldSupers.add("1");
		oldSupers.add("2");

		newSupers.add("1"); //expect remove "2"

		result = ddl.getSuperclassChanges(oldSupers, newSupers);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).toString(), "[2]");

	}

	/**
	 * test data()
	 * 
	 */
	@SuppressWarnings("unused")
	public void testdata() throws Exception {
		boolean success = createTestTable();
		assertTrue(success);
		SchemaInfo sup1 = databaseInfo.getSchemaInfo("sup1");
		SchemaInfo sup2 = databaseInfo.getSchemaInfo("sup2");
		SchemaInfo test = databaseInfo.getSchemaInfo("test");
		SchemaInfo testSuperTableName = databaseInfo.getSchemaInfo("testSuperTableName");
		SchemaInfo testTableName = databaseInfo.getSchemaInfo("testTableName");
		SchemaChangeManager changeList = new SchemaChangeManager(databaseInfo,
				true);
		List<SchemaChangeLog> changeListNoAuto = new ArrayList<SchemaChangeLog>();
		//SchemeChangeLog schemeChangeLog= new SchemeChangeLog();
		changeList.addSchemeChangeLog(new SchemaChangeLog("a", null,
				SchemeInnerType.TYPE_CLASSATTRIBUTE));
		changeList.addSchemeChangeLog(new SchemaChangeLog("a", null,
				SchemeInnerType.TYPE_ATTRIBUTE));
		changeList.addSchemeChangeLog(new SchemaChangeLog(null, "fk",
				SchemeInnerType.TYPE_FK));
		changeList.addSchemeChangeLog(new SchemaChangeLog("fk", null,
				SchemeInnerType.TYPE_FK));
		Constraint index = testTableName.getConstraintByName("index", "UNIQUE");
		changeList.addSchemeChangeLog(new SchemaChangeLog(
				index.getDefaultName(testTableName.getClassname())
						+ "$" + index.getName(), null, //$NON-NLS-1$
				SchemeInnerType.TYPE_INDEX));
		changeList.addSchemeChangeLog(new SchemaChangeLog("a", "a",
				SchemeInnerType.TYPE_ATTRIBUTE));
		changeList.addSchemeChangeLog(new SchemaChangeLog("a", "a",
				SchemeInnerType.TYPE_ATTRIBUTE));
		//changeListNoAuto.add(o)
		changeList.setChangeList(changeListNoAuto);
		SchemaDDL ddl = new SchemaDDL(changeList, databaseInfo);
		SchemaChangeManager changeList2 = new SchemaChangeManager(databaseInfo,
				false);
		SchemaDDL ddl2 = new SchemaDDL(changeList2, databaseInfo);

		List<String[]> columnConflicts = SuperClassUtil.getColumnConflicts(
				databaseInfo, testTableName, testTableName.getSuperClasses(),
				true);
		String[][] classConflicts = columnConflicts.toArray(new String[columnConflicts.size()][]);

		columnConflicts = SuperClassUtil.getColumnConflicts(databaseInfo,
				testTableName, testTableName.getSuperClasses(), false);
		String[][] conflicts = columnConflicts.toArray(new String[columnConflicts.size()][]);
		ddl.getSchemaDDL(sup1);
		ddl.getSchemaDDL(sup2);
		ddl.getSchemaDDL(sup1, sup2);
		ddl.getAlterDDL(sup1, sup2);
		ddl.getSchemaDDL(testTableName);
		ddl.getSchemaDDL(testTableName, testTableName);
		ddl.getAlterDDL(testTableName, testTableName);
		ddl.getSchemaDDL(testTableName, sup1);
		ddl.getSchemaDDL(sup1, testTableName);

		ddl2.getSchemaDDL(sup1);
		ddl2.getSchemaDDL(sup2);
		ddl2.getSchemaDDL(sup1, sup2);
		ddl2.getAlterDDL(sup1, sup2);
		ddl2.getSchemaDDL(testTableName);
		ddl2.getSchemaDDL(testTableName, testTableName);
		ddl2.getAlterDDL(testTableName, testTableName);
		ddl2.getSchemaDDL(testTableName, sup1);
		ddl2.getSchemaDDL(sup1, testTableName);

		String name = "name";
		String className = "className";
		String alias = "alias";
		boolean isClassResolution = true;
		DBResolution oldResolutions = new DBResolution(name, className, alias);
		oldResolutions.setName(name);
		oldResolutions.setClassName(className);
		oldResolutions.setAlias(alias);
		oldResolutions.setClassResolution(isClassResolution);
		DBResolution newResolutions = new DBResolution(name, className, alias);
		newResolutions.setName(name);
		newResolutions.setClassName(className);
		newResolutions.setAlias(alias);
		newResolutions.setClassResolution(isClassResolution);
		List<DBResolution> oldResolution = new ArrayList<DBResolution>();
		oldResolution.add(oldResolutions);
		List<DBResolution> newResolution = new ArrayList<DBResolution>();
		newResolution.add(newResolutions);
		ddl.getResolutionChanges(oldResolution, newResolution);

		List<String> oldSupers = new ArrayList<String>();
		oldSupers.add("oldstring");
		List<String> newSupers = new ArrayList<String>();
		newSupers.add("newstring");
		ddl.getSuperclassChanges(oldSupers, newSupers);

		ddl.getAddSuperClassDDL("tableName", newSupers, oldResolution,
				newResolution);
		ddl.getDropSuperClassesDDL("tableName", newSupers);
		ddl.getChangeOwnerDDL("tableName", "newOwner");

		String aname = "name";
		String type = "type";
		String inherit = "inherit"; // it belongs to which class
		boolean indexed = true;
		boolean notNull = true;
		boolean shared = true;
		boolean unique = true;
		String defaultValue = "defaultValue";
		SerialInfo autoIncrement = null;
		String domainClassName = "domainClassName";
		boolean isClassAttribute = true;
		DBAttribute dbAttribute = new DBAttribute(aname, type, inherit,
				indexed, notNull, shared, unique, defaultValue, "iso88591_bin");
		ddl.getAddColumnDDL("tableName", dbAttribute, newSupers, sup1);
		ddl.setEndLineChar("endLineChar");

		String aclassName = "className";
		String partitionName = "partitionName";
		String partitionClassName = "partitionClassName";
		PartitionType partitionType = PartitionType.HASH;
		String partitionExpr = "partitionExpr";
		final List<String> partitionValues = new ArrayList<String>();
		partitionValues.add("str");
		partitionValues.add("str1");
		final List<String> partitionValues2 = new ArrayList<String>();
		partitionValues.add("str");
		partitionValues.add(null);
		int rows = -1;
		PartitionInfo partitionInfo4 = new PartitionInfo(aclassName,
				partitionName, partitionClassName, PartitionType.LIST,
				partitionExpr, partitionValues, rows);
		PartitionInfo partitionInfo6 = new PartitionInfo(aclassName,
				partitionName, partitionClassName, partitionType,
				partitionExpr, partitionValues2, rows);
		PartitionInfo partitionInfo7 = new PartitionInfo(aclassName,
				partitionName, partitionClassName, PartitionType.RANGE,
				partitionExpr, partitionValues, rows);
		List<PartitionInfo> partInfoList = new ArrayList<PartitionInfo>();
		partInfoList.add(partitionInfo4);
		ddl.getTransformToPartitionDDL(partInfoList);
		List<PartitionInfo> partInfoListRange = new ArrayList<PartitionInfo>();
		partInfoListRange.add(partitionInfo7);
		ddl.getTransformToPartitionDDL(partInfoListRange);
		ddl.getTransformToGenericDDL("tableName");
		ddl.getAddPartitionDDL(partitionInfo4);
		ddl.getDelPartitionDDL("tableName", "partName");
		PartitionInfo partitionInfo5 = new PartitionInfo(aclassName,
				partitionName, partitionClassName, partitionType,
				"partitionExpr1", partitionValues, -1);
		List<PartitionInfo> newPartInfoList = new ArrayList<PartitionInfo>();
		newPartInfoList.add(partitionInfo5);
		newPartInfoList.add(partitionInfo7);
		ddl.getCoalescePartitionDDL(partInfoList, newPartInfoList);
		ddl.getCoalescePartitionDDL(newPartInfoList, partInfoList);
		ddl.getCoalescePartitionDDL(newPartInfoList, partInfoListRange);
		ddl.getSplitPartitionDDL(partInfoList, newPartInfoList);
		partInfoList.clear();
		partInfoList.add(partitionInfo6);
		ddl.getSplitPartitionDDL(partInfoList, newPartInfoList);

		ddl.getAlterAutoIncrementDDL("tableName", "columnName");
		partInfoList.clear();
		partitionInfo4 = new PartitionInfo(aclassName, partitionName,
				partitionClassName, PartitionType.RANGE, partitionExpr,
				partitionValues, rows);
		partInfoList.add(partitionInfo4);
		ddl.getSplitPartitionDDL(partInfoList, newPartInfoList);
		partInfoList.clear();
		partitionInfo4 = new PartitionInfo(aclassName, partitionName,
				partitionClassName, PartitionType.LIST, partitionExpr,
				partitionValues, rows);
		partInfoList.add(partitionInfo4);
		ddl.getSplitPartitionDDL(partInfoList, newPartInfoList);
	}

	public void testGetAlterDDL2() throws Exception {
		boolean success = createTestTable();
		assertTrue(success);

		SchemaChangeManager changeLogMgr = new SchemaChangeManager(
				databaseInfo, false);

		SchemaInfo schema = databaseInfo.getSchemaInfo(testTableName);

		SchemaInfo alteredschema = schema.clone();
		alteredschema.setClassname("alteredName");
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(
				schema.getClassname(), alteredschema.getClassname(),
				SchemeInnerType.TYPE_SCHEMA));
		/*Change supper*/
		schema.setSuperClasses(new ArrayList<String>());
		alteredschema.setSuperClasses(new ArrayList<String>());
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(
				schema.getClassname(), alteredschema.getClassname(),
				SchemeInnerType.TYPE_SUPER_TABLE));
		/*Remove attr*/
		alteredschema.removeDBAttributeByName("gender", false);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog("gender", null,
				SchemeInnerType.TYPE_ATTRIBUTE));
		/*Change position*/
		DBAttribute charAttr = alteredschema.getDBAttributeByName("char", false);
		alteredschema.getAttributes().remove(charAttr);
		alteredschema.addAttribute(charAttr);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(charAttr.getName(),
				charAttr.getName(), SchemeInnerType.TYPE_POSITION));

		/*Change attr*/
		DBAttribute attr1 = alteredschema.getDBAttributeByName("nation_code",
				false);
		alteredschema.removeDBAttributeByName("nation_code", false);
		attr1.setName("nation_code2");
		attr1.setDefault("c");
		attr1.setNotNull(true);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog("nation_code",
				"nation_code2", SchemeInnerType.TYPE_ATTRIBUTE));
		alteredschema.addAttribute(attr1);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog("nation_code",
				"nation_code2", SchemeInnerType.TYPE_POSITION));

		/*Add attr*/
		DBAttribute newAttr = new DBAttribute();
		newAttr.setName("newAttr");
		newAttr.setDefault("0");
		newAttr.setNotNull(false);
		newAttr.setType("String");
		newAttr.setUnique(false);
		newAttr.setInherit(alteredschema.getClassname());
		/*Add pk attr*/
		DBAttribute pkAttr = new DBAttribute();
		pkAttr.setName("pkAttr");
		pkAttr.setNotNull(true);
		pkAttr.setType("Integer");
		pkAttr.setUnique(true);
		pkAttr.setInherit(alteredschema.getClassname());
		pkAttr.setAutoIncrement(new SerialInfo("pk", "dba", "0", "1", "999999",
				"0", true, "0", "0", alteredschema.getClassname(),
				pkAttr.getName()));
		alteredschema.addAttribute(newAttr);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(null,
				newAttr.getName(), SchemeInnerType.TYPE_ATTRIBUTE));
		/*Add index*/
		Constraint index = new Constraint(false);
		index.setType(Constraint.ConstraintType.INDEX.getText());
		index.addAttribute("newAttr");
		index.addRule("newAttr_index DESC");
		index.setName(ConstraintNamingUtil.getIndexName(
				alteredschema.getClassname(), index.getRules()));
		alteredschema.addConstraint(index);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(null,
				index.getDefaultName(alteredschema.getClassname()) + "$"
						+ index.getName(), SchemeInnerType.TYPE_INDEX));
		/*Remove index*/
		Constraint removeConstraint = alteredschema.getConstraints().get(0);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(
				removeConstraint.getDefaultName(alteredschema.getClassname())
						+ "$" + removeConstraint.getName(), null,
				SchemeInnerType.TYPE_INDEX));
		/*Add pk*/
		Constraint pk = new Constraint(false);
		pk.addAttribute(pkAttr.getName());
		pk.setName(ConstraintNamingUtil.getPKName(alteredschema.getClassname(),
				pk.getAttributes()));
		pk.setType(Constraint.ConstraintType.PRIMARYKEY.getText());
		pk.addRule("pkAttr_pk ASC");
		alteredschema.addConstraint(pk);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(null,
				pk.getDefaultName(alteredschema.getClassname()) + "$"
						+ pk.getName(), SchemeInnerType.TYPE_INDEX));
		/*Remove fk*/
		Constraint fk2 = new Constraint(false);
		fk2.setType(Constraint.ConstraintType.FOREIGNKEY.getText());
		fk2.addAttribute(pkAttr.getName());
		fk2.addRule("REFERENCES " + testTableName);
		fk2.addRule("ON DELETE RESTRICT ON UPDATE RESTRICT");
		String fkName2 = ConstraintNamingUtil.getFKName(schema.getClassname(),
				fk2.getAttributes());
		fk2.setName(fkName2);
		schema.addConstraint(fk2);
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(fk2.getName(),
				null, SchemeInnerType.TYPE_FK));
		/*Alter fk*/
		Constraint alterFK = alteredschema.getFKConstraints().get(0);
		String oldFKName = alterFK.getName();
		alterFK.setName(ConstraintNamingUtil.getFKName(schema.getClassname(),
				fk2.getAttributes())
				+ "temp");
		changeLogMgr.addSchemeChangeLog(new SchemaChangeLog(oldFKName,
				alterFK.getName(), SchemeInnerType.TYPE_FK));

		SchemaDDL schemaDDL = new SchemaDDL(changeLogMgr, databaseInfo);
		assertNotNull(schemaDDL.getAlterDDL(schema, alteredschema));

//		schemaDDL = new SchemaDDL(changeLogMgr, databaseInfo831);
//		assertNotNull(schemaDDL.getAlterDDL(schema, alteredschema));

	}

	public void testGetAutoIncrementList () throws Exception {
		boolean success = createTestTable();
		assertTrue(success);
		
		SchemaDDL ddl = new SchemaDDL(null, databaseInfo);
		ddl.getIndexDDL(databaseInfo.getSchemaInfo("sup1"));
		assertTrue(ddl.getAutoIncrementList(databaseInfo.getSchemaInfo("sup1")).size() > 0);
	}
	
}
