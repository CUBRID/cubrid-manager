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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.Constraint.ConstraintType;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.SerialInfo;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.PartitionUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog.SchemeInnerType;
import com.cubrid.jdbc.proxy.driver.CUBRIDConnectionProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;

/**
 * This class provide a serial of methods to generate DDL of a schema or alter
 * DDL when schema changes
 *
 * @author moulinwang
 * @version 1.0 - 2009-5-22 created by moulinwang
 */
public class SchemaDDL {
	private static final Logger LOGGER = LogUtil.getLogger(SchemaDDL.class);
	
	private String endLineChar = ";";
	private final SchemaChangeManager changeLogMgr;
	protected final DatabaseInfo databaseInfo;

	public SchemaDDL(SchemaChangeManager changeLogMgr, DatabaseInfo databaseInfo) {
		super();
		this.changeLogMgr = changeLogMgr;
		this.databaseInfo = databaseInfo;
	}

	/**
	 * Return the DDL of a schema when creating a new schema, otherwise, return
	 * an alter DDL
	 *
	 * @param oldSchemaInfo SchemaInfo the old reference of oldSchemaInfo
	 * @param newSchemaInfo SchemaInfo the new reference of oldSchemaInfo
	 * @return String a string indicates the info of DDL
	 */
	public String getSchemaDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo) {
		if (changeLogMgr.isNewTableFlag()) {
			return getSchemaDDL(newSchemaInfo);
		} else {
			return getAlterDDL(oldSchemaInfo, newSchemaInfo);
		}
	}

	/**
	 * Return the complete DDL of a schema
	 *
	 * @param schemaInfo SchemaInfo the given reference of a SChemaInfo object
	 * @return String a string indicates a instance of SchemaInfo
	 */
	public String getSchemaDDL(SchemaInfo schemaInfo) {
		return getSchemaDDL(schemaInfo, true);
	}

	/**
	 * Return DDL of a schema
	 *
	 * @param schemaInfo SchemaInfo the given reference of a SChemaInfo object
	 * @param isContainIndex boolean whether include the index DDL
	 * @return String a string indicates a instance of SchemaInfo
	 */
	public String getSchemaDDL(SchemaInfo schemaInfo, boolean isContainIndex) {
		return getSchemaDDL(schemaInfo, isContainIndex, false);
	}

	/**
	 * Return DDL of a schema
	 *
	 * @param schemaInfo SchemaInfo the given reference of a SChemaInfo object
	 * @param isContainIndex boolean whether include the index DDL
	 * @param isVirtual boolean whether be a virtual table
	 * @return String a string indicates a instance of SchemaInfo
	 */
	public String getSchemaDDL(SchemaInfo schemaInfo, boolean isContainIndex, boolean isVirtual) {
		StringBuffer ddlBuffer = new StringBuffer();
		ddlBuffer.append("CREATE TABLE ");
		final String tableName = schemaInfo.getClassname().toLowerCase();
		if (null == tableName || tableName.equals("")) {
			ddlBuffer.append("<class_name>");
		} else {
			ddlBuffer.append(QuerySyntax.escapeKeyword(tableName));
		}

		List<String> slist = schemaInfo.getSuperClasses();
		if (!slist.isEmpty()) {
			ddlBuffer.append(StringUtil.NEWLINE).append("\t\t UNDER ");
			for (int i = 0; i < slist.size(); i++) {
				if (i != 0) {
					ddlBuffer.append(",");
				}
				ddlBuffer.append(QuerySyntax.escapeKeyword(slist.get(i).toLowerCase()));
			}
		}
		boolean attrBegin = false;
		int count = 0;
		// class attribute
		List<DBAttribute> clist = schemaInfo.getClassAttributes();
		if (!clist.isEmpty()) {
			for (int i = 0; i < clist.size(); i++) {
				DBAttribute classAttr = clist.get(i);
				String inherit = classAttr.getInherit();
				if (inherit.equalsIgnoreCase(schemaInfo.getClassname())) {
					if (count == 0) {
						ddlBuffer.append(StringUtil.NEWLINE);
						attrBegin = true;
						ddlBuffer.append("CLASS ATTRIBUTE(").append(StringUtil.NEWLINE);
					} else {
						ddlBuffer.append(",").append(StringUtil.NEWLINE);
					}
					ddlBuffer.append(getClassAttributeDDL(classAttr, isVirtual));
					count++;
				}
			}
			if (attrBegin) {
				ddlBuffer.append(StringUtil.NEWLINE).append(")").append(StringUtil.NEWLINE);
			}
		}
		// instance attribute
		List<SchemaInfo> newSupers = SuperClassUtil.getSuperClasses(databaseInfo, schemaInfo);
		Constraint pk = schemaInfo.getPK(newSupers);
		List<String> pkAttributes = pk == null ? new ArrayList<String>() : pk.getAttributes();
		count = 0;
		attrBegin = false;
		List<DBAttribute> nlist = schemaInfo.getAttributes();
		if (!nlist.isEmpty()) {
			for (int i = 0; i < nlist.size(); i++) {
				DBAttribute instanceAttr = nlist.get(i);
				String inherit = instanceAttr.getInherit();
				String className = schemaInfo.getClassname();
				if (StringUtil.isEqualIgnoreCase(inherit, className)) {
					if (count == 0) {
						if (!attrBegin) {
							ddlBuffer.append("(").append(StringUtil.NEWLINE);
							attrBegin = true;
						}
					} else {
						ddlBuffer.append(",").append(StringUtil.NEWLINE);
					}
					ddlBuffer.append(getInstanceAttributeDDL(instanceAttr, pkAttributes,
							schemaInfo, isVirtual));
					count++;
				}
			}
		}
		// constraint
		List<Constraint> constraintList = new ArrayList<Constraint>();
		if (isContainIndex) {
			constraintList = schemaInfo.getConstraints();
		}
		/*Sort the constaints first*/
		Collections.sort(constraintList, new ConstraintComparator(tableName));

		if (!constraintList.isEmpty()) {
			for (int i = 0; i < constraintList.size(); i++) {
				Constraint constraint = constraintList.get(i);
				List<SchemaInfo> superList = SuperClassUtil.getSuperClasses(databaseInfo,
						schemaInfo);
				if (!schemaInfo.isInSuperClasses(superList, constraint.getName())) {
					String contraintDDL = getContraintDDL(tableName, constraint);
					if (StringUtil.isNotEmpty(contraintDDL)) {
						ddlBuffer.append(",").append(StringUtil.NEWLINE).append(contraintDDL);
					}
				}
			}
		}

		if (count > 0) {
			ddlBuffer.append(StringUtil.NEWLINE).append(")");
		}

		boolean supportCharset = CompatibleUtil.isSupportCreateDBByCharset(databaseInfo);
		if ((isVirtual || supportCharset) && !StringUtil.isEmpty(schemaInfo.getCollation())) {
			ddlBuffer.append(" COLLATE ").append(schemaInfo.getCollation()).append(" ");
		}

		//reuse OID
		if (schemaInfo.isReuseOid()) {
			ddlBuffer.append(" REUSE_OID ");
		}

		String resolutionDDL = getResolutionsDDL(schemaInfo.getClassResolutions(),
				schemaInfo.getResolutions());
		ddlBuffer.append(resolutionDDL);
		ddlBuffer.append(endLineChar).append(StringUtil.NEWLINE);

		if (!constraintList.isEmpty()) {
			for (int i = 0; i < constraintList.size(); i++) {
				Constraint constraint = constraintList.get(i);
				List<SchemaInfo> superList = SuperClassUtil.getSuperClasses(databaseInfo,
						schemaInfo);
				if (!schemaInfo.isInSuperClasses(superList, constraint.getName())) {
					String type = constraint.getType();
					if ("UNIQUE".equals(type) || "INDEX".equals(type)
							|| "REVERSE INDEX".equals(type) || "REVERSE UNIQUE".equals(type)) {
						String indexDDL = getCreateIndexDDL(tableName, constraint);
						if (StringUtil.isNotEmpty(indexDDL)) {
							ddlBuffer.append(indexDDL);
							ddlBuffer.append(endLineChar).append(StringUtil.NEWLINE);
						}
					}
				}
			}
		}

		// partition DDL
		List<PartitionInfo> partitionInfoList = schemaInfo.getPartitionList();
		String transformToPartitionDDL = getTransformToPartitionDDL(partitionInfoList);
		if (transformToPartitionDDL != null) {
			ddlBuffer.append(transformToPartitionDDL);
			ddlBuffer.append(endLineChar).append(StringUtil.NEWLINE);
		}

		return ddlBuffer.toString();
	}

	/**
	 * get auto increament info
	 *
	 * @param schemaInfo
	 * @return List<SerialInfo>
	 */
	public List<SerialInfo> getAutoIncrementList(SchemaInfo schemaInfo) {
		List<DBAttribute> nlist = schemaInfo.getAttributes();
		List<SerialInfo> autoIncrementList = new ArrayList<SerialInfo>();
		if (!nlist.isEmpty()) {
			for (int i = 0; i < nlist.size(); i++) {
				DBAttribute instanceAttr = nlist.get(i);
				if (instanceAttr.getAutoIncrement() != null) {
					autoIncrementList.add(instanceAttr.getAutoIncrement());
				}
			}
		}

		return autoIncrementList;
	}

	/**
	 * get start value DDL of serial
	 *
	 * @param serialName
	 * @param startValue
	 * @return
	 */
	public String getAlterSerialStartValueDDL(String serialName, String startValue) {
		return "ALTER SERIAL " + QuerySyntax.escapeKeyword(serialName) + " START WITH "
				+ startValue + endLineChar;
	}

	/**
	 * get the pk DDL
	 *
	 * @param schemaInfo SchemaInfo
	 * @return pk DDL
	 */
	public String getPKsDDL(SchemaInfo schemaInfo) {
		String tableName = schemaInfo.getClassname();
		StringBuffer ddlBuffer = new StringBuffer();

		//Get the PK
		List<SchemaInfo> allSupers = SuperClassUtil.getSuperClasses(databaseInfo, schemaInfo);
		Constraint pk = schemaInfo.getPK(allSupers);
		if (pk != null) {
			List<String> pkAttributes = pk.getAttributes();
			if (pkAttributes != null && pkAttributes.size() > 0) {
				ddlBuffer.append(getAddPKDDL(tableName, pkAttributes, pk.getName()));
				ddlBuffer.append(endLineChar).append(StringUtil.NEWLINE);
			}
		}

		return ddlBuffer.toString();
	}

	/**
	 * get the pk DDL
	 *
	 * @param schemaInfo SchemaInfo
	 * @return pk DDL
	 */
	public String getFKsDDL(SchemaInfo schemaInfo) {
		String tableName = schemaInfo.getClassname();
		StringBuffer ddlBuffer = new StringBuffer();

		//Get the FK
		List<Constraint> fkList = schemaInfo.getFKConstraints();
		if (fkList != null) {
			for (Constraint fk : fkList) {
				ddlBuffer.append(getAddFKDDL(tableName, fk));
				ddlBuffer.append(endLineChar).append(StringUtil.NEWLINE);
			}
		}
		return ddlBuffer.toString();
	}

	/**
	 * get the pk DDL
	 *
	 * @param schemaInfo SchemaInfo
	 * @return pk DDL
	 */
	public String getIndexsDDL(SchemaInfo schemaInfo) {
		String tableName = schemaInfo.getClassname();
		StringBuffer ddlBuffer = new StringBuffer();

		//Get the index
		List<Constraint> constaintList = schemaInfo.getConstraints();
		if (!constaintList.isEmpty()) {
			for (int i = 0; i < constaintList.size(); i++) {
				Constraint constraint = constaintList.get(i);
				List<SchemaInfo> superList = SuperClassUtil.getSuperClasses(databaseInfo,
						schemaInfo);
				if (!schemaInfo.isInSuperClasses(superList, constraint.getName())) {
					String type = constraint.getType();
					if ("UNIQUE".equals(type) || "INDEX".equals(type)
							|| "REVERSE INDEX".equals(type) || "REVERSE UNIQUE".equals(type)) {
						String indexDDL = getCreateIndexDDL(tableName, constraint);
						if (StringUtil.isNotEmpty(indexDDL)) {
							ddlBuffer.append(indexDDL);
							ddlBuffer.append(endLineChar);
							ddlBuffer.append(StringUtil.NEWLINE);
							ddlBuffer.append(StringUtil.NEWLINE);
						}
					}
				}
			}
		}
		return ddlBuffer.toString();
	}

	/**
	 * Get the index DDL
	 *
	 * @param schemaInfo SchemaInfo
	 * @return The index related DDL
	 */
	public String getIndexDDL(SchemaInfo schemaInfo) {
		String tableName = schemaInfo.getClassname();
		StringBuffer ddlBuffer = new StringBuffer();

		//Get the PK
		List<SchemaInfo> allSupers = SuperClassUtil.getSuperClasses(databaseInfo, schemaInfo);
		Constraint pk = schemaInfo.getPK(allSupers);
		if (pk != null) {
			List<String> pkAttributes = pk.getAttributes();
			if (pkAttributes != null && pkAttributes.size() > 0) {
				ddlBuffer.append(getAddPKDDL(tableName, pkAttributes, pk.getName()));
				ddlBuffer.append(endLineChar);
				ddlBuffer.append(StringUtil.NEWLINE);
				ddlBuffer.append(StringUtil.NEWLINE);
			}
		}

		//Get the FK
		List<Constraint> fkList = schemaInfo.getFKConstraints();
		if (fkList != null) {
			for (Constraint fk : fkList) {
				ddlBuffer.append(getAddFKDDL(tableName, fk));
				ddlBuffer.append(endLineChar);
				ddlBuffer.append(StringUtil.NEWLINE);
				ddlBuffer.append(StringUtil.NEWLINE);
			}
		}

		//Get the index
		List<Constraint> constaintList = schemaInfo.getConstraints();
		if (!constaintList.isEmpty()) {
			for (int i = 0; i < constaintList.size(); i++) {
				Constraint constraint = constaintList.get(i);
				List<SchemaInfo> superList = SuperClassUtil.getSuperClasses(databaseInfo,
						schemaInfo);
				if (!schemaInfo.isInSuperClasses(superList, constraint.getName())) {
					String type = constraint.getType();
					if ("UNIQUE".equals(type) || "INDEX".equals(type)
							|| "REVERSE INDEX".equals(type) || "REVERSE UNIQUE".equals(type)) {
						String indexDDL = getCreateIndexDDL(tableName, constraint);
						if (StringUtil.isNotEmpty(indexDDL)) {
							ddlBuffer.append(indexDDL);
							ddlBuffer.append(endLineChar);
							ddlBuffer.append(StringUtil.NEWLINE);
							ddlBuffer.append(StringUtil.NEWLINE);
						}
					}
				}
			}
		}
		return ddlBuffer.toString();
	}

	/**
	 * Return an alter DDL of schema, some changes stored in change
	 * logs(SchemaChangeManager), others are found by differing old and new
	 * schema objects
	 *
	 * @param oldSchemaInfo SchemaInfo the old reference of oldSchemaInfo
	 * @param newSchemaInfo SchemaInfo the new reference of oldSchemaInfo
	 * @return String a string indicates the info of DDL
	 */
	public String getAlterDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo) {
		DDLGenerator generator = new DDLGenerator();
		if (oldSchemaInfo == null) {
			return null;
		}

		List<SchemaInfo> oldSupers = SuperClassUtil.getSuperClasses(databaseInfo, oldSchemaInfo);
		if (oldSupers == null) {
			return null;
		}

		List<SchemaInfo> newSupers = SuperClassUtil.getSuperClasses(databaseInfo, newSchemaInfo);
		if (newSupers == null) {
			return null;
		}

		// old --> new
		Map<String, String> attrMap = new HashMap<String, String>();

		//Generate the DDL for rename table
		String oldTableName = oldSchemaInfo.getClassname().toLowerCase();
		String newTableName = newSchemaInfo.getClassname().toLowerCase();
		String tableName = oldTableName;
		if (!oldTableName.equals(newTableName)) {
			String renameDDL = getRenameTableDDL(oldTableName, newTableName);
			generator.addSchemaDDLMode(DDLGenerator.TYPE_REBANE_TABLE, newSchemaInfo, renameDDL);
			tableName = newTableName;
		}

		String oldCollation = oldSchemaInfo.getCollation();
		String newCollation = newSchemaInfo.getCollation();
		if (!StringUtil.isEmpty(newCollation)
				&& !StringUtil.isEqualNotIgnoreNull(oldCollation, newCollation)) {
			String alterCollationDDL = getAlterTableCollationDDL(oldSchemaInfo, newSchemaInfo);
			generator.addSchemaDDLMode(DDLGenerator.TYPE_CHANGE_TABLE_COLLATE, newSchemaInfo,
					alterCollationDDL);
		}

		//Generate the DDL for column attribute change
		List<SchemaChangeLog> allAttrChanges = changeLogMgr.getClassAttrChangeLogs();
		allAttrChanges.addAll(changeLogMgr.getAttrChangeLogs());

		// only new added attribute and after version 8.4.0 support to reorder
		boolean isSupportReorderColumn = CompatibleUtil.isSupportReorderColumn(databaseInfo);
		if (isSupportReorderColumn) {
			/*For the bug TOOLS-1258 After add column, change column name, it will pop error.*/
			/*Sort the change log first*/
			Collections.sort(allAttrChanges, new ChangeLogCompartor(newSchemaInfo));

			for (SchemaChangeLog log : allAttrChanges) {
				boolean isClassAttr = false;
				if (log.getType() == SchemeInnerType.TYPE_CLASSATTRIBUTE) {
					isClassAttr = true;
				} else {
					isClassAttr = false;
				}
				appendChangeAttributeDDL(oldSchemaInfo, newSchemaInfo, oldSupers, newSupers,
						attrMap, tableName, log, isClassAttr, generator);
			}

			List<SchemaChangeLog> allPosChangeLogs = changeLogMgr.getPositionChangeLogs();
			for (SchemaChangeLog log : allPosChangeLogs) {
				if (!generator.hasProcessedAttr(log.getOldValue())) {
					for (DBAttribute attr : newSchemaInfo.getAttributes()) {
						if (attr.getName().equals(log.getOldValue())) {
							appendChangeAttributeDDL(oldSchemaInfo, newSchemaInfo, oldSupers,
									newSupers, attrMap, tableName, log, attr.isClassAttribute(),
									generator);
							break;
						}
					}
				}
			}
		} else {
			for (SchemaChangeLog log : allAttrChanges) {
				appendAlterAttributeDDL(oldSchemaInfo, newSchemaInfo, oldSupers, newSupers,
						attrMap, tableName, log, generator);
			}
			if (isSupportReorderColumn) {
				generator.addSchemaDDLMode(DDLGenerator.TYPE_CHANGE_POS, newSchemaInfo,
						getAddReorderColumnDDL(oldSchemaInfo, newSchemaInfo, newSupers, tableName));
			}
		}

		//Generate the DDL for super class change
		List<String> oldSuperClasses = oldSchemaInfo.getSuperClasses();
		List<String> newSuperClasses = newSchemaInfo.getSuperClasses();
		List<List<String>> superChanges = getSuperclassChanges(oldSuperClasses, newSuperClasses);

		generator.addSchemaDDLMode(
				DDLGenerator.TYPE_CHANGE_SUPER,
				newSchemaInfo,
				appendChangedSuperDDL(oldSchemaInfo, newSchemaInfo, tableName, oldSuperClasses,
						newSuperClasses, superChanges));

		//Generate the DDL for PK change
		List<SchemaInfo> allSupers = SuperClassUtil.getSuperClasses(databaseInfo, newSchemaInfo);
		allSupers.addAll(newSupers);
		allSupers.addAll(oldSupers);

		Constraint newPK = newSchemaInfo.getPK(allSupers);
		Constraint oldPK = oldSchemaInfo.getPK(oldSupers);

		if (oldPK == null && newPK != null) { // add pk
			List<String> pkAttributes = newPK.getAttributes();
			if (pkAttributes != null && pkAttributes.size() > 0) {
				String addPKDDL = getAddPKDDL(tableName, pkAttributes, newPK.getName())
						+ endLineChar + StringUtil.NEWLINE;
				generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_INDEX, newPK, addPKDDL);
			}
		} else if (oldPK != null && newPK == null) { // del pk
			String dropPKDDL = dropPK(tableName, oldPK.getName()) + endLineChar
					+ StringUtil.NEWLINE;
			generator.addPreDDLMode(DDLGenerator.TYPE_DROP_INDEX, oldPK, dropPKDDL);
		} else if (oldPK != null && newPK != null) {
			appendChangedPkDDL(attrMap, tableName, newPK, oldPK, generator);
		}

		//Generate the DDL for FK change
		List<SchemaChangeLog> fkChanges = changeLogMgr.getFKChangeLogs();
		for (SchemaChangeLog log : fkChanges) {
			appendChangedFkDDL(oldSchemaInfo, newSchemaInfo, attrMap, tableName, log, generator);
		}
		List<SchemaChangeLog> indexChanges = changeLogMgr.getIndexChangeLogs();
		for (SchemaChangeLog log : indexChanges) {
			appendChanedIndexDDL(oldSchemaInfo, newSchemaInfo, tableName, log, generator);
		}

		// Partitioning
		boolean isPartitionChanged = isPartitonChange(oldSchemaInfo.getPartitionList(),
				newSchemaInfo.getPartitionList());
		if ("YES".equals(oldSchemaInfo.isPartitionGroup()) && isPartitionChanged) {
			String sql = getTransformToGenericDDL(tableName) + endLineChar + StringUtil.NEWLINE;
			generator.addSchemaDDLMode(DDLGenerator.TYPE_DROP_PARTITON,
					oldSchemaInfo.getPartitionList(), sql);
		}
		if (isPartitionChanged) {
			List<PartitionInfo> partitionInfoList = newSchemaInfo.getPartitionList();
			String sql = getTransformToPartitionDDL(partitionInfoList);

			if (sql != null) {
				sql = sql + endLineChar + StringUtil.NEWLINE;
				generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_PARTITON,
						oldSchemaInfo.getPartitionList(), sql);
			}
		}

		return generator.generatorDDL();
	}

	/**
	 * Append changed super class DDL
	 *
	 * @param oldSchemaInfo the old instance of SchemaInfo
	 * @param newSchemaInfo the new instance of SchemaInfo
	 * @param ddlBuffer the instance of StringBuffer
	 * @param tableName the table name
	 * @param oldSuperClasses the list which includes the info of old super
	 *        classed
	 * @param newSuperClasses the list which includes the info of new super
	 *        classes
	 * @param superChanges the list which includes the info of super changed
	 */
	private String appendChangedSuperDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo,
			String tableName, List<String> oldSuperClasses, List<String> newSuperClasses,
			List<List<String>> superChanges) {
		StringBuffer ddlBuffer = new StringBuffer();
		if (superChanges.isEmpty()) {
			List<DBResolution> resolutions = getResolutionChanges(oldSchemaInfo.getResolutions(),
					newSchemaInfo.getResolutions());
			List<DBResolution> classResolutions = getResolutionChanges(
					oldSchemaInfo.getClassResolutions(), newSchemaInfo.getClassResolutions());
			if (!resolutions.isEmpty() || !classResolutions.isEmpty()) {
				String ddl = getAddSuperClassDDL(tableName, null, classResolutions, resolutions);
				ddlBuffer.append(ddl).append(endLineChar).append(StringUtil.NEWLINE);
			}
		} else {
			List<String> removeItems = null;
			List<String> addItems = null;

			if (superChanges.size() == 1) {
				if (newSuperClasses.size() > oldSuperClasses.size()) {
					addItems = superChanges.get(0);
				} else {
					removeItems = superChanges.get(0);
				}
			} else {
				removeItems = superChanges.get(0);
				addItems = superChanges.get(1);
			}
			if (null != removeItems) {
				ddlBuffer.append(getDropSuperClassesDDL(tableName, removeItems));
				ddlBuffer.append(endLineChar).append(StringUtil.NEWLINE);
			}

			if (null != addItems) {
				List<DBResolution> classResolutions = getResolutionChanges(
						oldSchemaInfo.getClassResolutions(), newSchemaInfo.getClassResolutions());
				List<DBResolution> resolutions = getResolutionChanges(
						oldSchemaInfo.getResolutions(), newSchemaInfo.getResolutions());

				String ddl = getAddSuperClassDDL(tableName, addItems, classResolutions, resolutions);
				ddlBuffer.append(ddl).append(endLineChar).append(StringUtil.NEWLINE);
			}

		}
		return ddlBuffer.toString();
	}

	/**
	 * Get changed resolution, just find the new added resolution, for when
	 * sending them to CUBRID, CUBRID would delete unused resolutions
	 *
	 * @param oldResolutions List<DBResolution> the given list includes the old
	 *        reference of DBResolution
	 * @param newResolutions List<DBResolution> the given list includes the new
	 *        reference of DBResolution
	 * @return List<DBResolution> a list includes the changed reference of
	 *         DBResolution
	 */
	public List<DBResolution> getResolutionChanges(List<DBResolution> oldResolutions,
			List<DBResolution> newResolutions) {
		List<DBResolution> list = new ArrayList<DBResolution>();
		for (DBResolution newResolution : newResolutions) {
			DBResolution r = SuperClassUtil.getResolution(oldResolutions, newResolution.getName(),
					newResolution.getClassName());
			if (r == null) {
				list.add(newResolution);
			} else {
				if (!r.getAlias().equals(newResolution.getAlias())) {
					list.add(newResolution);
				}
			}

		}
		return list;
	}

	/**
	 * Get changed super class
	 *
	 * @param oldSupers List<String> a list includes the old super type names
	 * @param newSupers List<String> a list includes the new super type names
	 * @return List<List<String>> a list includes the changed super type
	 */

	public List<List<String>> getSuperclassChanges(List<String> oldSupers, List<String> newSupers) {
		List<List<String>> retList = new ArrayList<List<String>>();
		List<String> removeList = new ArrayList<String>();
		List<String> addList = new ArrayList<String>();
		if (oldSupers == null || oldSupers.isEmpty()) {
			if (newSupers == null || newSupers.isEmpty()) {
				return retList;
			}
			retList.add(newSupers);
			return retList;
		} else if (newSupers.isEmpty()) {
			retList.add(oldSupers);
			return retList;
		}

		List<String> tempList = new ArrayList<String>();
		tempList.addAll(oldSupers);

		for (int i = 0; i < newSupers.size(); i++) {
			String newSuper = newSupers.get(i);
			int index = tempList.indexOf(newSuper);
			if (index == -1) {
				List<String> removeItems = removeItems(tempList, i, tempList.size() - 1);
				removeList.addAll(removeItems);
				tempList.add(newSuper);
				addList.add(newSuper);
			} else {
				if (index != i) {
					List<String> removeItems = removeItems(tempList, i, index - 1);
					removeList.addAll(removeItems);
				}
			}
		}
		if (tempList.size() > newSupers.size()) {
			for (int i = newSupers.size(); i < tempList.size(); i++) {
				removeList.add(tempList.get(i));
			}
		}

		if (!removeList.isEmpty()) {
			retList.add(removeList);
		}
		if (!addList.isEmpty()) {
			retList.add(addList);
		}
		return retList;
	}

	/**
	 * Remove items in a list, only used for method
	 * {@link #getSuperclassChanges(List, List)}
	 *
	 *
	 * @param tempList List<String>
	 * @param from int the index of tempList
	 * @param end int the index of tempList
	 * @return List<String> the list that have removed some items
	 */
	private List<String> removeItems(List<String> tempList, int from, int end) {
		List<String> retList = new ArrayList<String>();
		for (int i = end; i >= from; i--) {
			String str = tempList.remove(i);
			retList.add(str);
		}
		return retList;
	}

	/**
	 * Append changed index DDL
	 *
	 * @param oldSchemaInfo the old instance of SchemaInfo
	 * @param newSchemaInfo the new instance of SchemaInfo
	 * @param ddlBuffer the instance of StringBuffer
	 * @param dropConstraintBF the object of StringBuffer which represent drop
	 *        constraint
	 * @param tableName the table name
	 * @param log the object of SchemeChangeLog
	 */
	private void appendChanedIndexDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo,
			String tableName, SchemaChangeLog log, DDLGenerator generator) {
		Constraint oldConstraint = null;
		Constraint newConstraint = null;

		if (log.getOldValue() != null) {
			oldConstraint = oldSchemaInfo.getConstraintByName(getConstraintName(log.getOldValue()));
		}
		if (log.getNewValue() != null) {
			newConstraint = newSchemaInfo.getConstraintByName(getConstraintName(log.getNewValue()));
		}

		if (oldConstraint == null && newConstraint != null) { // add index
			String addIndexDDL = getCreateIndexDDL(tableName, newConstraint);
			if (!"".equals(addIndexDDL)) {
				addIndexDDL = addIndexDDL + endLineChar + StringUtil.NEWLINE;
				generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_INDEX, newConstraint, addIndexDDL);
			}
		} else if (log.getNewValue() == null && oldConstraint != null) { // delete index
			String dropIndexDDL = getDropIndexDDL(tableName, oldConstraint) + endLineChar
					+ StringUtil.NEWLINE;
			generator.addPreDDLMode(DDLGenerator.TYPE_DROP_INDEX, oldConstraint, dropIndexDDL);
		} else if (newConstraint != null && oldConstraint != null) { // modify index
			String oldDDL = getCreateIndexDDL(tableName, oldConstraint);
			String newDDL = getCreateIndexDDL(tableName, newConstraint);

			if (!oldDDL.equals(newDDL)) {
				String dropIndexDDL = getDropIndexDDL(tableName, oldConstraint) + endLineChar
						+ StringUtil.NEWLINE;
				generator.addPreDDLMode(DDLGenerator.TYPE_DROP_INDEX, oldConstraint, dropIndexDDL);

				String addIndexDDL = newDDL + endLineChar + endLineChar;
				generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_INDEX, newConstraint, addIndexDDL);

			}
		}
	}

	private String getConstraintName(String logName) {
		return logName.split("\\$")[1];
	}

	/**
	 * Append changed primary key DDL
	 *
	 * @param attrMap
	 * @param generator
	 * @param tableName
	 * @param newPK
	 * @param oldPK
	 */
	private void appendChangedPkDDL(Map<String, String> attrMap, String tableName,
			Constraint newPK, Constraint oldPK, DDLGenerator generator) {
		// del and add pk
		if (oldPK == null || newPK == null) {
			return;
		}
		Iterator<String> e1 = newPK.getAttributes().iterator();
		Iterator<String> e2 = oldPK.getAttributes().iterator();
		boolean equal = true;
		if (!newPK.getName().equals(oldPK.getName())) {
			equal = false;
		}
		while (e1.hasNext() && e2.hasNext()) {
			String newAttr = e1.next();
			String oldAttr = e2.next();
			// old attribute should be changed to latest attribute name
			oldAttr = attrMap.get(oldAttr) == null ? oldAttr : attrMap.get(oldAttr);
			if (!(newAttr == null ? oldAttr == null : newAttr.equals(oldAttr))) {
				equal = false;
				break;
			}
		}
		if (equal) {
			equal = !(e1.hasNext() || e2.hasNext());
		}

		if (equal && generator.isNeedReCreatePK(newPK)) {
			equal = false;
		}

		if (!equal) {
			String dropPKDDL = dropPK(tableName, oldPK.getName()) + endLineChar
					+ StringUtil.NEWLINE;
			generator.addPreDDLMode(DDLGenerator.TYPE_DROP_INDEX, oldPK, dropPKDDL);

			List<String> pkAttributes = newPK.getAttributes();
			if (pkAttributes != null && pkAttributes.size() > 0) {
				String addPKDDL = getAddPKDDL(tableName, pkAttributes, newPK.getName())
						+ endLineChar + StringUtil.NEWLINE;
				generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_INDEX, newPK, addPKDDL);
			}
		}
	}

	/**
	 * Append the changed foreign key DDL
	 *
	 * @param oldSchemaInfo the old instance of SchemaInfo
	 * @param newSchemaInfo the new instance of SchemaInfo
	 * @param attrMap the map which includes all the attribute
	 * @param ddlBuffer the object of StringBuffer
	 * @param dropConstraintBF the object of StringBuffer which represent drop
	 *        constraint
	 * @param tableName the table name
	 * @param log the object of SchemeChangeLog
	 */
	private void appendChangedFkDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo,
			Map<String, String> attrMap, String tableName, SchemaChangeLog log,
			DDLGenerator generator) {
		Constraint oldFK = null;
		Constraint newFK = null;

		if (log.getOldValue() != null) {
			oldFK = oldSchemaInfo.getFKConstraint(log.getOldValue());
		}

		if (log.getNewValue() != null) {
			newFK = newSchemaInfo.getFKConstraint(log.getNewValue());
		}

		if (log.getOldValue() == null && newFK != null) { // add fk
			String addFKDDL = getAddFKDDL(tableName, newFK) + endLineChar + StringUtil.NEWLINE;
			generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_FK, newFK, addFKDDL);

		} else if (log.getNewValue() == null && oldFK != null) { // delete fk
			String dropFKDDL = getDropFKDDL(tableName, oldFK.getName()) + endLineChar
					+ StringUtil.NEWLINE;
			generator.addPreDDLMode(DDLGenerator.TYPE_DROP_FK, oldFK, dropFKDDL);
		} else if (oldFK != null && newFK != null) {
			String oldDDL = getAddFKDDL(tableName, oldFK);
			String newDDL = getAddFKDDL(tableName, newFK);
			if (!oldDDL.equals(newDDL)) {
				String dropFKDDL = getDropFKDDL(tableName, oldFK.getName()) + endLineChar
						+ StringUtil.NEWLINE;
				generator.addPreDDLMode(DDLGenerator.TYPE_DROP_FK, oldFK, dropFKDDL);

				String addFKDDL = getAddFKDDL(tableName, newFK) + endLineChar + StringUtil.NEWLINE;
				generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_FK, newFK, addFKDDL);
			}
		}
	}

	/**
	 * Append the change DDL for the attribute
	 *
	 * @param oldSchemaInfo
	 * @param newSchemaInfo
	 * @param oldSupers
	 * @param newSupers
	 * @param attrMap
	 * @param ddlBuffer
	 * @param tableName
	 * @param changeLog
	 * @param processedId
	 * @param isClassAttr
	 */
	private void appendChangeAttributeDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo,
			List<SchemaInfo> oldSupers, List<SchemaInfo> newSupers, Map<String, String> attrMap,
			String tableName, SchemaChangeLog changeLog, boolean isClassAttr, DDLGenerator generator) {
		if (StringUtil.isEmpty(changeLog.getOldValue())) { // add [class] column
			DBAttribute newAttr = newSchemaInfo.getDBAttributeByName(changeLog.getNewValue(),
					isClassAttr);
			StringBuffer addDDL = new StringBuffer();
			if (isClassAttr) {
				addDDL.append(getAddClassColumnDDL(tableName, newAttr)).append(endLineChar).append(
						StringUtil.NEWLINE);
			} else {
				Constraint pk = newSchemaInfo.getPK(newSupers);
				List<String> pkAttributes = pk == null ? new ArrayList<String>()
						: pk.getAttributes();
				addDDL.append(getAddColumnDDL(tableName, newAttr, pkAttributes, newSchemaInfo));

				/*For bug TOOLS-1125.add the position DDL*/
				StringBuffer sb = new StringBuffer();
				String lastName = null;
				for (DBAttribute attr : newSchemaInfo.getAttributes()) {
					if (attr.getName().toLowerCase().equals(newAttr.getName().toLowerCase())) {
						break;
					} else {
						lastName = attr.getName();
					}
				}

				if (lastName == null) {
					sb.append(" FIRST");
				} else {
					sb.append(" AFTER ").append(QuerySyntax.escapeKeyword(lastName));
				}
				addDDL.append(sb.toString());
				addDDL.append(endLineChar);
				addDDL.append(StringUtil.NEWLINE);
			}
			generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_ATTR, newAttr, addDDL.toString());
		} else if (StringUtil.isEmpty(changeLog.getNewValue())) { // drop [class] column
			DBAttribute oldAttr = oldSchemaInfo.getDBAttributeByName(changeLog.getOldValue(),
					isClassAttr);
			String attrName = oldAttr.getName();

			String dropDDL = null;
			if (isClassAttr) {
				dropDDL = getDropClassColumnDDL(tableName, attrName) + endLineChar
						+ StringUtil.NEWLINE;
			} else {
				dropDDL = getDropColumnDDL(tableName, attrName) + endLineChar + StringUtil.NEWLINE;
			}
			generator.addSchemaDDLMode(DDLGenerator.TYPE_DROP_ATTR, oldAttr, dropDDL);
		} else { // edit column
			DBAttribute oldAttr = oldSchemaInfo.getDBAttributeByName(changeLog.getOldValue(),
					isClassAttr);
			DBAttribute newAttr = newSchemaInfo.getDBAttributeByName(changeLog.getNewValue(),
					isClassAttr);

			/*For bug TOOLS - 987*/
			String editDDL = null;

			/*For bug TOOLS-3046*/
			boolean isNeedChangeDDLAfterChangePK = newAttr.isNotNull()
					&& isRemovedFromPK(oldSchemaInfo, newSchemaInfo, oldSupers, newSupers, oldAttr,
							newAttr);
			if (isNeedChangeDDL(oldAttr, newAttr, changeLog) || isNeedChangeDDLAfterChangePK) {
				if (isClassAttr) {
					editDDL = getChangeAttributeDDL(tableName, oldAttr, newAttr, oldSchemaInfo,
							newSchemaInfo, newSupers);
				} else {
					editDDL = getChangeColumnDDL(tableName, oldAttr, newAttr, oldSchemaInfo,
							newSchemaInfo, oldSupers, newSupers);
				}
			} else {
				/*Just don't process SchemeInnerType.TYPE_POSITION log*/
				if (oldAttr == null) {
					return;
				}
				editDDL = getAlterAttrDDL(oldAttr, newAttr, oldSchemaInfo, newSchemaInfo,
						oldSupers, newSupers, isClassAttr, attrMap, tableName, generator);
			}
			generator.addSchemaDDLMode(DDLGenerator.TYPE_EDIT_ATTR, newAttr, editDDL);
		}
	}

	/**
	 * Judge is need change ddl
	 *
	 * @param oldAttr
	 * @param newAttr
	 * @return
	 */
	private boolean isNeedChangeDDL(DBAttribute oldAttr, DBAttribute newAttr,
			SchemaChangeLog changeLog) {
		if (SchemeInnerType.TYPE_POSITION.equals(changeLog.getType())) {
			return true;
		}

		if (!StringUtil.isEqualNotIgnoreNullIgnoreCase(oldAttr.getType(), newAttr.getType())) {
			return true;
		}

		if (!(oldAttr.isNotNull() == newAttr.isNotNull())) {
			return true;
		}

		if (!StringUtil.isEqualNotIgnoreNull(oldAttr.getEnumeration(), newAttr.getEnumeration())) {
			return true;
		}

		if (!StringUtil.isEqualNotIgnoreNull(oldAttr.getInherit().toLowerCase(),
				newAttr.getInherit().toLowerCase())) {
			return true;
		}

		if (oldAttr.isShared() != newAttr.isShared()) {
			return true;
		}

		if (oldAttr.isClassAttribute() != newAttr.isClassAttribute()) {
			return true;
		}

		if (!StringUtil.isEqualNotIgnoreNull(oldAttr.getDomainClassName(),
				newAttr.getDomainClassName())) {
			return true;
		}

		/*For bug TOOLS-1018*/
		if ((oldAttr.getAutoIncrement() == null) != (newAttr.getAutoIncrement() == null)) {
			return true;
		}

		return false;
	}

	/**
	 * Check whether current column was removed from PK attribute list or not.
	 *
	 * @param oldSchemaInfo
	 * @param newSchemaInfo
	 * @param oldSupers
	 * @param newSupers
	 * @param oldAttr
	 * @param newAttr
	 * @return
	 */
	private boolean isRemovedFromPK(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo,
			List<SchemaInfo> oldSupers, List<SchemaInfo> newSupers, DBAttribute oldAttr,
			DBAttribute newAttr) {
		List<SchemaInfo> allSupers = SuperClassUtil.getSuperClasses(databaseInfo, newSchemaInfo);
		allSupers.addAll(newSupers);
		allSupers.addAll(oldSupers);

		Constraint newPK = newSchemaInfo.getPK(allSupers);
		Constraint oldPK = oldSchemaInfo.getPK(oldSupers);

		if (oldPK == null) {
			return false;
		} else if (oldPK.getAttributes() == null) {
			return false;
		} else if (!oldPK.getAttributes().contains(oldAttr.getName())) {
			return false;
		} else if (newPK == null) {
			return true;
		} else if (newPK.getAttributes() == null) {
			return true;
		} else if (!newPK.getAttributes().contains(newAttr.getName())) {
			return true;
		}
		return false;
	}

	/**
	 * Append the changed attribute DDL
	 *
	 * @param oldSchemaInfo the old instance of SchemaInfo
	 * @param newSchemaInfo the new instance of SchemaInfo
	 * @param oldSupers the list which includes the old instance of SchemaInfo
	 * @param newSupers the list which includes the new instance of SchemaInfo
	 * @param attrMap the map which includes all the attribute
	 * @param ddlBuffer the object of StringBuffer
	 * @param tableName the table name
	 * @param changeLog the object of SchemeChangeLog
	 */
	private void appendAlterAttributeDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo,
			List<SchemaInfo> oldSupers, List<SchemaInfo> newSupers, Map<String, String> attrMap,
			String tableName, SchemaChangeLog changeLog, DDLGenerator generator) {
		boolean isClassAttr = false;
		if (changeLog.getType() == SchemeInnerType.TYPE_CLASSATTRIBUTE) {
			isClassAttr = true;
		} else {
			isClassAttr = false;
		}

		if (StringUtil.isEmpty(changeLog.getOldValue())) { // add [class] column
			DBAttribute newAttr = newSchemaInfo.getDBAttributeByName(changeLog.getNewValue(),
					isClassAttr);
			String addDDL = null;
			if (isClassAttr) {
				addDDL = getAddClassColumnDDL(tableName, newAttr) + endLineChar
						+ StringUtil.NEWLINE;
			} else {
				// only new added attribute and after version 8.4.0 support to
				// reorder
				boolean isSupportReorderColumn = CompatibleUtil.isSupportReorderColumn(databaseInfo);
				if (isSupportReorderColumn) {
					return;
				}
				Constraint pk = newSchemaInfo.getPK(newSupers);
				List<String> pkAttributes = pk == null ? new ArrayList<String>()
						: pk.getAttributes();
				addDDL = getAddColumnDDL(tableName, newAttr, pkAttributes, newSchemaInfo)
						+ endLineChar + StringUtil.NEWLINE;
			}
			generator.addSchemaDDLMode(DDLGenerator.TYPE_ADD_ATTR, newAttr, addDDL);
		} else if (StringUtil.isEmpty(changeLog.getNewValue())) { // drop [class] column
			DBAttribute oldAttr = oldSchemaInfo.getDBAttributeByName(changeLog.getOldValue(),
					isClassAttr);
			String attrName = oldAttr.getName();

			StringBuffer dropDDL = new StringBuffer();
			if (isClassAttr) {
				dropDDL.append(getDropClassColumnDDL(tableName, attrName)).append(endLineChar).append(
						StringUtil.NEWLINE);
			} else {
				dropDDL.append(getDropColumnDDL(tableName, attrName)).append(endLineChar).append(
						StringUtil.NEWLINE);
			}
			generator.addSchemaDDLMode(DDLGenerator.TYPE_DROP_ATTR, oldAttr, dropDDL.toString());
		} else { // edit column

			DBAttribute oldAttr = oldSchemaInfo.getDBAttributeByName(changeLog.getOldValue(),
					isClassAttr);
			DBAttribute newAttr = newSchemaInfo.getDBAttributeByName(changeLog.getNewValue(),
					isClassAttr);

			String editDDL = getAlterAttrDDL(oldAttr, newAttr, oldSchemaInfo, newSchemaInfo,
					oldSupers, newSupers, isClassAttr, attrMap, tableName, generator);
			generator.addSchemaDDLMode(DDLGenerator.TYPE_EDIT_ATTR, newAttr, editDDL);
		}
	}

	/**
	 * Get alter DBAttribute ddl
	 *
	 * @param oldAttr
	 * @param newAttr
	 * @param oldSchemaInfo
	 * @param newSchemaInfo
	 * @param oldSupers
	 * @param newSupers
	 * @param isClassAttr
	 * @param attrMap
	 * @param tableName
	 * @return
	 */
	private String getAlterAttrDDL(DBAttribute oldAttr, DBAttribute newAttr,
			SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo, List<SchemaInfo> oldSupers,
			List<SchemaInfo> newSupers, boolean isClassAttr, Map<String, String> attrMap,
			String tableName, DDLGenerator generator) {
		StringBuffer ddlBuffer = new StringBuffer();
		String oldColumnName = oldAttr.getName().toLowerCase();
		String columnName = oldColumnName;
		String newColumnName = newAttr.getName().toLowerCase();
		if (!newColumnName.equals(oldColumnName)) {
			String ddl = getRenameColumnNameDDL(tableName, oldColumnName, newColumnName,
					isClassAttr);
			ddlBuffer.append(ddl).append(endLineChar).append(StringUtil.NEWLINE);
			columnName = newColumnName;
			attrMap.put(oldColumnName, newColumnName);
		}

		boolean oldNotNull = oldAttr.isNotNull();
		boolean newNotNull = newAttr.isNotNull();
		boolean notNullChanged = oldNotNull != newNotNull;
		boolean hasNotNullDDL = false;
		if (notNullChanged) {
			boolean isChangedByPK = false;
			if (newNotNull) {
				// add a new PK
				Constraint pk = newSchemaInfo.getPK(newSupers);
				List<String> pkAttributes = pk == null ? new ArrayList<String>()
						: pk.getAttributes();
				if (pkAttributes.contains(newColumnName)) {
					isChangedByPK = true;
				}
			} else {
				// drop an old PK
				Constraint pk = oldSchemaInfo.getPK(oldSupers);
				List<String> pkAttributes = pk == null ? new ArrayList<String>()
						: pk.getAttributes();
				if (pkAttributes.contains(newColumnName)) {
					isChangedByPK = true;
				}
			}

			if (!isChangedByPK) {
				hasNotNullDDL = true;
				// null constraint changed,it is not support for 8.2.2
				String editDDL = null;
				if (isClassAttr) {
					editDDL = getChangeAttributeDDL(tableName, oldAttr, newAttr, oldSchemaInfo,
							newSchemaInfo, newSupers);
				} else {
					editDDL = getChangeColumnDDL(tableName, oldAttr, newAttr, oldSchemaInfo,
							newSchemaInfo, oldSupers, newSupers);
				}

				return editDDL;
			}
		}

		String oldDefault = oldAttr.getDefault();
		String newDefault = newAttr.getDefault();
		boolean defaultChanged = oldDefault == null ? newDefault != null
				: !oldDefault.equals(newDefault);
		if (defaultChanged && !hasNotNullDDL) {
			if (newDefault == null) {
				newDefault = "null";
			} else {
				FormatDataResult result = DBAttrTypeFormatter.formatForInput(newAttr.getType(),
						newDefault, false);
				if (result.isSuccess()) {
					newDefault = result.getFormatResult();
				}
			}
			String ddl = getChangeDefaultValueDDL(tableName, columnName, newDefault, isClassAttr);
			ddlBuffer.append(ddl).append(endLineChar).append(StringUtil.NEWLINE);
		}

		SerialInfo oldAutoIncrement = oldAttr.getAutoIncrement();
		SerialInfo newAutoIncrement = newAttr.getAutoIncrement();

		if (null != newAutoIncrement && !newAutoIncrement.equals(oldAutoIncrement)) {
			String increment = getAlterAutoIncrementDDL(tableName, newColumnName,
					newAutoIncrement.getStartedValue(), newAutoIncrement.getIncrementValue(),
					newAutoIncrement.getMinValue());
			ddlBuffer.append(increment);
		}

		return ddlBuffer.toString();
	}

	/**
	 * Get the attribute change DDL
	 *
	 * @param tableName
	 * @param oldAttr
	 * @param newAttr
	 * @param oldSchemaInfo
	 * @param newSchemaInfo
	 * @param newSupers
	 * @return
	 */
	private String getChangeAttributeDDL(String tableName, DBAttribute oldAttr,
			DBAttribute newAttr, SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo,
			List<SchemaInfo> newSupers) {

		StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(QuerySyntax.escapeKeyword(tableName));
		sb.append(" CLASS ATTRIBUTE ");
		sb.append(QuerySyntax.escapeKeyword(oldAttr.getName())).append(" ");
		sb.append(getClassAttributeDDL(newAttr, false));

		String reorderString = getReorderString(newAttr, newSchemaInfo);
		if (reorderString != null) {
			sb.append(" " + reorderString);
		}

		sb.append(endLineChar);
		sb.append(StringUtil.NEWLINE);

		return sb.toString();
	}

	/**
	 * Get the column change DDL
	 *
	 * @param tableName
	 * @param oldAttr
	 * @param newAttr
	 * @param oldSchemaInfo
	 * @param newSchemaInfo
	 * @param newSupers
	 * @return
	 */
	private String getChangeColumnDDL(String tableName, DBAttribute oldAttr, DBAttribute newAttr,
			SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo, List<SchemaInfo> oldSupers,
			List<SchemaInfo> newSupers) {
		Constraint newPK = newSchemaInfo.getPK(newSupers);
		List<String> pkAttributes = newPK == null ? new ArrayList<String>() : newPK.getAttributes();

		StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(QuerySyntax.escapeKeyword(tableName));
		sb.append(" CHANGE COLUMN ");
		sb.append(QuerySyntax.escapeKeyword(oldAttr.getName())).append(" ");
		sb.append(getInstanceAttributeDDL(newAttr, pkAttributes, newSchemaInfo, false));

		String reorderString = getReorderString(newAttr, newSchemaInfo);
		if (reorderString != null) {
			sb.append(" " + reorderString);
		}

		sb.append(endLineChar);
		sb.append(StringUtil.NEWLINE);

		SerialInfo oldAutoIncrement = oldAttr.getAutoIncrement();
		SerialInfo newAutoIncrement = newAttr.getAutoIncrement();

		if (null != newAutoIncrement && !newAutoIncrement.equals(oldAutoIncrement)) {
			String increment = getAlterAutoIncrementDDL(tableName, newAttr.getName(),
					newAutoIncrement.getStartedValue(), newAutoIncrement.getIncrementValue(),
					newAutoIncrement.getMinValue());
			sb.append(increment);
		}

		return sb.toString();
	}

	/**
	 * Get reorder String for the change command
	 *
	 * @param newAttr
	 * @param newSchemaInfo
	 * @return
	 */
	private String getReorderString(DBAttribute newAttr, SchemaInfo newSchemaInfo) {
		/*Judge is the position is changed*/
		boolean isChanged = false;
		List<SchemaChangeLog> posChanges = changeLogMgr.getPositionChangeLogs();
		for (SchemaChangeLog log : posChanges) {
			if (log.getOldValue().equals(newAttr.getName())) {
				isChanged = true;
				break;
			}
		}

		if (!isChanged) {
			return null;
		}

		/*Get the position change DDL*/
		StringBuffer sb = new StringBuffer();
		String lastName = null;
		for (DBAttribute attr : newSchemaInfo.getAttributes()) {
			if (attr.getName().equals(newAttr.getName())) {
				break;
			} else if (newSchemaInfo.getClassname().equals(attr.getInherit())) {
				lastName = attr.getName();
			}
		}

		if (lastName == null) {
			sb.append(" FIRST");
		} else {
			sb.append(" AFTER ").append(QuerySyntax.escapeKeyword(lastName));
		}

		return sb.toString();
	}

	/**
	 * Return DDL of adding index, unique, reverse index or reverse unique
	 *
	 * @param tableName String the given table name
	 * @param indexConstaint Constraint the give reference of the Constraint
	 *        object
	 * @return String a string indicates the info of index
	 */
	private String getCreateIndexDDL(String tableName, Constraint indexConstaint) {
		String type = indexConstaint.getType();
		String defaultName = indexConstaint.getDefaultName(tableName);
		StringBuffer bf = new StringBuffer();
		bf.append("CREATE ");
		if ("INDEX".equals(type)) {
			bf.append("INDEX");
		} else if ("REVERSE INDEX".equals(type)) {
			bf.append("REVERSE INDEX");
		} else if ("UNIQUE".equals(type)) {
			bf.append("UNIQUE INDEX");
		} else if ("REVERSE UNIQUE".equals(type)) {
			bf.append("REVERSE UNIQUE INDEX");
		} //TODO: it is needed to handle an exception if the type is not in above conditions.
		if (StringUtil.isNotEmpty(indexConstaint.getName())) {
			bf.append(" ").append(QuerySyntax.escapeKeyword(indexConstaint.getName()));
		} else {
			bf.append(" ").append(QuerySyntax.escapeKeyword(defaultName));
		}

		List<String> rules = indexConstaint.getRules();
		String[][] columnsRuleArray = new String[rules.size()][2];

		if ("UNIQUE".equals(type) || "INDEX".equals(type) || "REVERSE UNIQUE".equals(type)
				|| "REVERSE INDEX".equals(type)) {
			for (int i = 0; i < rules.size(); i++) {
				String rule = rules.get(i);
				String[] strs = rule.trim().split(" ");
				if (strs[0].indexOf("(") > 0) {
					strs[0] = QuerySyntax.escapeKeyword(strs[0].substring(0, strs[0].indexOf("(")))
							+ strs[0].substring(strs[0].indexOf("("));
				} else {
					strs[0] = QuerySyntax.escapeKeyword(strs[0]);
				}
				if (strs.length > 1) {
					columnsRuleArray[i][0] = strs[0];
					columnsRuleArray[i][1] = strs[1];
				} else {
					columnsRuleArray[i][0] = strs[0];
					columnsRuleArray[i][1] = "";
				}
			}

		}
		makeIndexColumns(bf, tableName, columnsRuleArray);

		return bf.toString();
	}

	/**
	 * Return DDL of dropping index, unique, reverse index or reverse unique
	 *
	 * @param tableName String the table name
	 * @param indexConstaint Constraint the reference of the Constraint object
	 * @return String a string indicates the info of drop index
	 */
	private String getDropIndexDDL(String tableName, Constraint indexConstaint) {
		if (indexConstaint == null) {
			return "";
		}

		boolean after830 = CompatibleUtil.isAfter830(databaseInfo);
		String type = indexConstaint.getType();
		StringBuffer bf = new StringBuffer();
		if (after830) {
			bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(" ");
		}
		bf.append("DROP");
		if ("INDEX".equals(type)) {
			bf.append(" INDEX");
		} else if ("REVERSE INDEX".equals(type)) {
			bf.append(" REVERSE INDEX");
		} else if ("UNIQUE".equals(type)) {
			bf.append(" UNIQUE INDEX");
		} else if ("REVERSE UNIQUE".equals(type)) {
			bf.append(" REVERSE UNIQUE INDEX");
		}
		/* For bug TOOLS-2859 */
		bf.append(" ").append(QuerySyntax.escapeKeyword(indexConstaint.getName()));
		//		if (!defaultName.equals(indexConstaint.getName())) {
		//			bf.append(" ").append(QuerySyntax.escapeKeyword(indexConstaint.getName()));
		//		}
		//
		// Don't need this codes
		//		List<String> list = new ArrayList<String>();
		//		if ("UNIQUE".equals(type) || "INDEX".equals(type)) {
		//			String indexName = indexConstaint.getName();
		//			if (StringUtil.isNotEmpty(indexName)) {
		//				bf.append(" \"").append(indexName).append("\"");
		//			} else {
		//				List<String> rules = indexConstaint.getRules();
		//				for (String rule : rules) {
		//					String[] strs = rule.split(" ");
		//					if (strs[0].indexOf("(") != -1) {
		//						strs[0] = strs[0].substring(0, strs[0].indexOf("("));
		//					}
		//					list.add("\"" + strs[0] + "\" " + strs[1]);
		//				}
		//				makeIndexColumns(bf, tableName, list);
		//			}
		//		} else if ("REVERSE UNIQUE".equals(type)
		//				|| "REVERSE INDEX".equals(type)) {
		//			String indexName = indexConstaint.getName();
		//			if (StringUtil.isNotEmpty(indexName)) {
		//				bf.append(" \"").append(indexName).append("\"");
		//			} else {
		//				List<String> attrs = indexConstaint.getAttributes();
		//				for (String attr : attrs) {
		//					list.add("\"" + attr + "\"");
		//				}
		//				makeIndexColumns(bf, tableName, list);
		//			}
		//		}

		return bf.toString();
	}

	private void makeIndexColumns(StringBuffer bf, String tableName, String[][] columnRuleArr) {
		bf.append(" ON ");
		bf.append(QuerySyntax.escapeKeyword(tableName));
		bf.append("(");
		for (int i = 0; i < columnRuleArr.length; i++) {
			bf.append(columnRuleArr[i][0]);

			if (StringUtil.isNotEmpty(columnRuleArr[i][1])
					&& !"ASC".equalsIgnoreCase(columnRuleArr[i][1].trim())) {
				bf.append(" ").append(columnRuleArr[i][1].trim());
			}

			if (i + 1 < columnRuleArr.length) {
				bf.append(",");
			}
		}

		bf.append(")");
	}

	/**
	 * Return the DDL of adding FK
	 *
	 * @param tableName Sring the table name
	 * @param fkConstaint Constraint the reference of the Constraint object
	 * @return String a string indicates the info of add foreign key
	 */
	private String getAddFKDDL(String tableName, Constraint fkConstaint) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ");
		bf.append(QuerySyntax.escapeKeyword(tableName));
		bf.append(" ADD");
		bf.append(getFKDDL(tableName, fkConstaint));
		return bf.toString();
	}

	/**
	 * Return the DDL of dropping super classes
	 *
	 * @param tableName String the table name
	 * @param superClasses List<String> a list includes the info of super
	 *        classes
	 * @return String a string indicates the info of drop super class
	 */
	public String getDropSuperClassesDDL(String tableName, List<String> superClasses) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ");
		bf.append(QuerySyntax.escapeKeyword(tableName));
		bf.append(" DROP SUPERCLASS ");
		int count = 0;
		for (String superClass : superClasses) {
			if (count != 0) {
				bf.append(",");
			}
			bf.append(QuerySyntax.escapeKeyword(superClass));
			count++;
		}
		return bf.toString();
	}

	/**
	 * Return the DDL of adding super classes
	 *
	 * @param tableName String the given table name
	 * @param superClasses List<String> the given list that includes the info of
	 *        super class
	 * @param classResolutions List<DBResolution> the given list that includes
	 *        the instances of DBResoltuion
	 * @param resolutions List<DBResolution> the given list that includes the
	 *        instances of DBresolution
	 * @return String a string that indicates the DDL of add super class
	 */
	public String getAddSuperClassDDL(String tableName, List<String> superClasses,
			List<DBResolution> classResolutions, List<DBResolution> resolutions) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ");
		bf.append(QuerySyntax.escapeKeyword(tableName));
		if (null != superClasses) {
			bf.append(" ADD SUPERCLASS ");
			int count = 0;
			for (String superClass : superClasses) {
				if (count != 0) {
					bf.append(",");
				}
				bf.append(QuerySyntax.escapeKeyword(superClass));
				count++;
			}
		}

		bf.append(getResolutionsDDL(classResolutions, resolutions));
		return bf.toString();
	}

	/**
	 * Return the DDL of resolution part
	 *
	 * @param classResolutions List<DBResolution> the given list includes the
	 *        DBResolution object, which are class resolutions
	 * @param resolutions List<DBResolution> he given list includes the
	 *        DBResolution object, which are plain resolutions
	 * @return String a string indicates the resolution DDL
	 */
	private String getResolutionsDDL(List<DBResolution> classResolutions,
			List<DBResolution> resolutions) {
		StringBuffer bf = new StringBuffer();
		int count = 0;
		for (DBResolution r : classResolutions) {
			if (count == 0) {
				bf.append(StringUtil.NEWLINE).append("INHERIT ");
			} else {
				bf.append(", ");
			}
			bf.append(getDBResolutionDDL(r, true));
			count++;
		}
		for (DBResolution r : resolutions) {
			if (count == 0) {
				bf.append(StringUtil.NEWLINE).append("INHERIT ");
			} else {
				bf.append(", ");
			}
			bf.append(getDBResolutionDDL(r, false));
			count++;
		}
		return bf.toString();
	}

	/**
	 * Return the DDL of changing owner
	 *
	 * @param tableName String the table name
	 * @param newOwner String the new Owner
	 * @return String a string indicates change owner statement
	 */
	public String getChangeOwnerDDL(String tableName, String newOwner) {
		// change_owner (class_name, newOwner) db_authorizations
		StringBuffer bf = new StringBuffer();
		bf.append(StringUtil.NEWLINE);
		bf.append("CALL CHANGE_OWNER (");
		bf.append("'").append(tableName).append("',");
		bf.append("'").append(newOwner).append("'");
		bf.append(") ON CLASS db_authorizations");
		bf.append(StringUtil.NEWLINE);
		return bf.toString();
	}

	/**
	 * Return the DDL of dropping PK
	 *
	 * @param tableName String the given table name
	 * @param pkConstraintName String the given pk constraint name
	 * @return String a string that indicate drop pk statement
	 */
	private String dropPK(String tableName, String pkConstraintName) {
		return getDropConstraintDDL(tableName, pkConstraintName);
	}

	/**
	 * Return the DDL of dropping FK
	 *
	 * @param tableName String the given table name
	 * @param fkConstraintName String the given fk constraint name
	 * @return String a string that indicates drop fk statement
	 */
	private String getDropFKDDL(String tableName, String fkConstraintName) {
		return getDropConstraintDDL(tableName, fkConstraintName);
	}

	/**
	 * Return the DDL of dropping constraint
	 *
	 * @param tableName String the given table name
	 * @param constraint String the given constraint name
	 * @return String a string that indicates drop constraint statement
	 */
	private String getDropConstraintDDL(String tableName, String constraint) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(
				" DROP CONSTRAINT ").append(QuerySyntax.escapeKeyword(constraint));
		return bf.toString();
	}

	/**
	 * Return the DDL of adding PK
	 *
	 * @param tableName String the given table name
	 * @param pkAttributes List<String> the given list includes the pk
	 *        attributes
	 * @return String a string indicates the add primary key statement
	 */
	private String getAddPKDDL(String tableName, List<String> pkAttributes, String pkName) {
		StringBuffer bf = new StringBuffer();
		if (pkName != null) {
			bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(
					" ADD CONSTRAINT ").append(QuerySyntax.escapeKeyword(pkName)).append(
					" PRIMARY KEY (");
		} else {
			bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(
					" ADD PRIMARY KEY(");
		}

		int count = 0;
		for (String column : pkAttributes) {
			if (count > 0) {
				bf.append(",");
			}
			bf.append(QuerySyntax.escapeKeyword(column));
			count++;
		}
		bf.append(")");
		return bf.toString();
	}

	/**
	 * Return the DDL of changing default value of column
	 *
	 * @param tableName String the given table name
	 * @param columnName String the given the column name
	 * @param newDefault String the new default value
	 * @param isClassAttr boolean whether is class attribute
	 * @return String a string that indicates the DDL of change default value
	 */
	private String getChangeDefaultValueDDL(String tableName, String columnName, String newDefault,
			boolean isClassAttr) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(" ALTER");
		if (isClassAttr) {
			bf.append(" CLASS ATTRIBUTE");
		}
		bf.append(" ").append(QuerySyntax.escapeKeyword(columnName)).append(" SET DEFAULT ").append(
				newDefault);
		return bf.toString();
	}

	/**
	 * Return the DDL of renaming a column
	 *
	 * @param tableName String the given table name
	 * @param oldColumnName String the given old column name
	 * @param newColumnName Sring the given new column name
	 * @param isClassAttr boolean whether is class attribute
	 * @return String a string indicates the rename column name statement
	 */
	private String getRenameColumnNameDDL(String tableName, String oldColumnName,
			String newColumnName, boolean isClassAttr) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(" RENAME");
		if (isClassAttr) {
			bf.append(" CLASS");
		}
		bf.append(" ").append(QuerySyntax.escapeKeyword(oldColumnName)).append(" AS ").append(
				QuerySyntax.escapeKeyword(newColumnName));
		return bf.toString();
	}

	/**
	 * Return the DDL of dropping a class attribute
	 *
	 * @param tableName String the given the table name
	 * @param attrName String the given the attribute name
	 * @return String a string indicates the drop class column statement
	 */
	private String getDropClassColumnDDL(String tableName, String attrName) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(
				" DROP ATTRIBUTE CLASS ");
		bf.append(QuerySyntax.escapeKeyword(attrName));
		return bf.toString();
	}

	/**
	 * Return the DDL of dropping a column
	 *
	 * @param tableName String the given table name
	 * @param attrName String the given attribute name
	 * @return String a string indicates the drop column statement
	 */
	private String getDropColumnDDL(String tableName, String attrName) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(
				" DROP  COLUMN ");
		bf.append(QuerySyntax.escapeKeyword(attrName));
		return bf.toString();
	}

	/**
	 * Return the DDL of adding a class attribute
	 *
	 *
	 * @param tableName String the given table name
	 * @param newAttr DBAttribute the given new attribute reference of a
	 *        DBAttribute object
	 * @return String a string indicates the add class attribute statement
	 */
	private String getAddClassColumnDDL(String tableName, DBAttribute newAttr) {

		// Add class attribute
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(
				" ADD CLASS ATTRIBUTE ");
		bf.append(this.getClassAttributeDDL(newAttr, false));
		return bf.toString();
	}

	/**
	 * Return the DDL of adding a column
	 *
	 * @param tableName String the given table name
	 * @param newAttr DBAttribute the given new attribute reference of a
	 *        DBAttribute object
	 * @param pkAttributes List<String> the given list includes the info of pk
	 *        attributes
	 * @param newSchemaInfo SchemaInfo the given new reference of a SchemaInfo
	 *        object
	 * @return String a string that indicates the add column statement
	 */
	public String getAddColumnDDL(String tableName, DBAttribute newAttr, List<String> pkAttributes,
			SchemaInfo newSchemaInfo) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(
				" ADD COLUMN ");
		bf.append(getInstanceAttributeDDL(newAttr, pkAttributes, newSchemaInfo, false));
		return bf.toString();
	}

	/**
	 *
	 * Return the new added and reorder attribute DDL
	 *
	 * @param oldSchemaInfo SchemaInfo
	 * @param newSchemaInfo SchemaInfo
	 * @param newSupers List<SchemaInfo>
	 * @param tableName String
	 * @return String
	 */
	private String getAddReorderColumnDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo,
			List<SchemaInfo> newSupers, String tableName) {
		StringBuffer ddlBuffer = new StringBuffer();
		List<DBAttribute> dbAttributeList = newSchemaInfo.getAttributes();
		List<SchemaChangeLog> attrChanges = changeLogMgr.getAttrChangeLogs();
		DBAttribute lastAttr = null;
		for (DBAttribute dbAttribute : dbAttributeList) {
			boolean isInheritAttr = dbAttribute.getInherit() != null
					&& !dbAttribute.getInherit().trim().equalsIgnoreCase(tableName.trim());
			if (isInheritAttr) {
				continue;
			}
			for (SchemaChangeLog changeLog : attrChanges) {
				String newAttrName = changeLog.getNewValue();
				if (newAttrName != null && newAttrName.equals(dbAttribute.getName())
						&& changeLog.getOldValue() == null) { // add [class] column
					Constraint pk = newSchemaInfo.getPK(newSupers);
					List<String> pkAttributes = pk == null ? new ArrayList<String>()
							: pk.getAttributes();

					ddlBuffer.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(
							" ADD COLUMN ");
					ddlBuffer.append(getInstanceAttributeDDL(dbAttribute, pkAttributes,
							newSchemaInfo, false));
					if (lastAttr == null) {
						ddlBuffer.append(" FIRST");
					} else {
						ddlBuffer.append(" AFTER ").append(
								QuerySyntax.escapeKeyword(lastAttr.getName()));
					}
					ddlBuffer.append(endLineChar).append(StringUtil.NEWLINE);
					break;
				}
			}
			lastAttr = dbAttribute;
		}
		return ddlBuffer.toString();
	}

	/**
	 * Alter ddl for table collation
	 *
	 * @param oldSchemaInfo
	 * @param newSchemaInfo
	 * @return
	 */
	private String getAlterTableCollationDDL(SchemaInfo oldSchemaInfo, SchemaInfo newSchemaInfo) {
		StringBuffer bf = new StringBuffer();
		bf.append("ALTER CLASS ").append(QuerySyntax.escapeKeyword(newSchemaInfo.getClassname()));
		bf.append(" COLLATE ").append(
				newSchemaInfo.getCollation() == null ? "" : newSchemaInfo.getCollation());
		bf.append(endLineChar).append(StringUtil.NEWLINE);
		return bf.toString();
	}

	/**
	 * DDL of renaming a table
	 *
	 * @param oldTableName String the old table name
	 * @param newTableName String the new table name
	 * @return String a string that indicates the rename table statement
	 */
	private String getRenameTableDDL(String oldTableName, String newTableName) {
		StringBuffer bf = new StringBuffer();
		bf.append("RENAME CLASS ").append(QuerySyntax.escapeKeyword(oldTableName));
		bf.append(" AS ").append(QuerySyntax.escapeKeyword(newTableName));
		bf.append(endLineChar).append(StringUtil.NEWLINE);
		return bf.toString();
	}

	/**
	 * Return the DDL of a resolution
	 *
	 * @param resolution DBResolution the given reference of a DBResolution
	 *        object
	 * @param isClass boolean whether is class
	 * @return String a string that indicates the info of the resolution
	 */
	private String getDBResolutionDDL(DBResolution resolution, boolean isClass) {
		StringBuffer bf = new StringBuffer();
		if (isClass) {
			bf.append("CLASS ");
		}
		bf.append(QuerySyntax.escapeKeyword(resolution.getName()));
		bf.append(" OF ").append(QuerySyntax.escapeKeyword(resolution.getClassName()));
		if (StringUtil.isNotEmpty(resolution.getAlias())) {
			bf.append(" AS ").append(QuerySyntax.escapeKeyword(resolution.getAlias()));
		}
		return bf.toString();
	}

	/**
	 * Return the DDL of a constraint <li>PK <li>FK <li>Unique
	 *
	 * @param tableName String the given table name
	 * @param constaint Constraint the given reference of a Constraint object
	 * @return String a string indicates the info of constraint DDL
	 */
	private String getContraintDDL(String tableName, Constraint constaint) {
		String type = constaint.getType();
		if ("PRIMARY KEY".equals(type)) {
			return getPKDDL(tableName, constaint);
		}
		if ("FOREIGN KEY".equals(type)) {
			return getFKDDL(tableName, constaint);
		}
		return "";
	}

	/**
	 * Return the DDL of FK in creating and altering a schema
	 *
	 * @param tableName String the given table name
	 * @param fkConstaint Constraint the given reference of a Constraint
	 *        object,which includes the info of foreign key
	 * @return String a string indicates the info of foreign key DDL
	 */
	private String getFKDDL(String tableName, Constraint fkConstaint) {
		StringBuffer bf = new StringBuffer();

		String defaultName = fkConstaint.getDefaultName(tableName);
		if (!defaultName.equals(fkConstaint.getName())) {
			bf.append(" CONSTRAINT ");
			bf.append(" ").append(QuerySyntax.escapeKeyword(fkConstaint.getName()));
		}

		bf.append(" FOREIGN KEY");

		List<String> list = fkConstaint.getAttributes();
		bf.append(" (");
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				bf.append(",");
			}
			if (list.get(i).indexOf(" DESC") != -1 || list.get(i).indexOf(" ASC") != -1) {
				bf.append(list.get(i));
			} else {
				bf.append(QuerySyntax.escapeKeyword(list.get(i)));
			}
		}
		bf.append(")");
		List<String> rlist = fkConstaint.getRules();
		String refTable = rlist != null && rlist.size() > 0 ? rlist.get(0).replace("REFERENCES ", "") : "";
		bf.append(" REFERENCES ").append(QuerySyntax.escapeKeyword(refTable));
		bf.append("(");

		SchemaInfo schemaInfo = databaseInfo.getSchemaInfo(refTable);
		if (schemaInfo == null) {
			bf.append(" {ERROR : Cannot retrieve foreign key metadata on old version of cubrid} )");
		} else {
			List<SchemaInfo> newSupers = SuperClassUtil.getSuperClasses(databaseInfo, schemaInfo);
			Constraint pkConstaint = schemaInfo.getPK(newSupers);
			List<String> pklist = pkConstaint.getAttributes();
			for (int i = 0; i < pklist.size(); i++) {
				if (i != 0) {
					bf.append(",");
				}
				bf.append(QuerySyntax.escapeKeyword(pklist.get(i).replace(" ASC", "").replace(" DESC", "")));
			}
			bf.append(")");

			for (int i = 1; i < rlist.size(); i++) {
				String rule = rlist.get(i);
				String tmp = rule.trim().toUpperCase();
				if (tmp.startsWith("ON CACHE OBJECT")) {
					tmp = tmp.replace("ON CACHE OBJECT", "").trim().toLowerCase();
					bf.append(" ON CACHE OBJECT ").append(QuerySyntax.escapeKeyword(tmp));
				} else {
					bf.append(" ").append(rule);
				}
			}
		}
		return bf.toString();
	}

	/**
	 * DDL of PK in creating a schema
	 *
	 * @param tableName String the given table name
	 * @param constaint Constraint the given reference of a Constraint object,
	 *        which includes the info of primary key
	 * @return String a string that indicates the info of primary key
	 */
	private String getPKDDL(String tableName, Constraint constaint) {
		if (!(constaint.getAttributes() != null && constaint.getAttributes().size() > 0)) {
			return "";
		}
		StringBuffer bf = new StringBuffer();
		bf.append("CONSTRAINT ");
		if (constaint.getName() == null || "".equals(constaint.getName().trim())) {
			bf.append(QuerySyntax.escapeKeyword(constaint.getDefaultName(tableName).toLowerCase()));
		} else {
			bf.append(QuerySyntax.escapeKeyword(constaint.getName().toLowerCase()));
		}
		bf.append(" ");
		bf.append("PRIMARY KEY(");
		List<String> list = constaint.getAttributes();
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				bf.append(",");
			}
			if (list.get(i).indexOf(" DESC") != -1 || list.get(i).indexOf(" ASC") != -1) {
				bf.append(list.get(i));
			} else {
				bf.append(QuerySyntax.escapeKeyword(list.get(i)));
			}
		}
		bf.append(")");
		return bf.toString();
	}

	/**
	 * DDL of a class attribute in creating a schema
	 *
	 * @param classAttr DBAttribute the given reference of a DBAttribute object
	 * @param isVirtual the classAttr whether is in a virtual table.
	 * @return String a string that indicates the info of class attribute DDL
	 */
	private String getClassAttributeDDL(DBAttribute classAttr, boolean isVirtual) {
		StringBuffer bf = new StringBuffer();

		bf.append(QuerySyntax.escapeKeyword(classAttr.getName()));
		bf.append(" ").append(classAttr.getType());

		if (DataType.DATATYPE_ENUM.equalsIgnoreCase(classAttr.getType())
				&& classAttr.getEnumeration() != null) {
			bf.append(" ").append(classAttr.getEnumeration());
		}

		boolean supportCharset = CompatibleUtil.isSupportCreateDBByCharset(databaseInfo);
		if ((isVirtual || supportCharset) && !StringUtil.isEmpty(classAttr.getCollation())
				&& DataType.canUseCollation(classAttr.getType())) {
			bf.append(" COLLATE ").append(classAttr.getCollation()).append(" ");
		}

		String defaultv = classAttr.getDefault();
		if (defaultv != null) {
			defaultv = DBAttrTypeFormatter.formatValueForInput(classAttr.getType(), defaultv, false);
			bf.append(" DEFAULT ").append(defaultv);
		}

		if (classAttr.isNotNull()) {
			bf.append(" ").append("NOT NULL");
		}

		return bf.toString();
	}

	/**
	 * DDL of a attribute in creating a schema
	 *
	 * @param instanceAttr DBAttribute the given reference of a DBAttribute
	 *        object
	 * @param pkAttributes List<String> the given list that includes the info of
	 *        primary key
	 * @param newSchemaInfo SchemaInfo the given reference of a SchemaInfo
	 *        object
	 * @param isVirtual boolean whether be a virtual table
	 * @return String a string that indicates the info of instance atttribute
	 *         DDL
	 */
	private String getInstanceAttributeDDL(DBAttribute instanceAttr, List<String> pkAttributes,
			SchemaInfo newSchemaInfo, boolean isVirtual) {
		StringBuffer bf = new StringBuffer();
		bf.append(QuerySyntax.escapeKeyword(instanceAttr.getName().toLowerCase()));
		bf.append(" ").append(instanceAttr.getType()); // it don't be modified because instanceAttr.getType() can be "ENUM('Y','N')"

		if (DataType.DATATYPE_ENUM.equalsIgnoreCase(instanceAttr.getType())
				&& instanceAttr.getEnumeration() != null) {
			bf.append(instanceAttr.getEnumeration());
		}

		boolean supportCharset = CompatibleUtil.isSupportCreateDBByCharset(databaseInfo);
		if ((isVirtual || supportCharset) && !StringUtil.isEmpty(instanceAttr.getCollation())
				&& DataType.canUseCollation(instanceAttr.getType())) {
			bf.append(" COLLATE ").append(instanceAttr.getCollation()).append(" ");
		}

		String defaultv = instanceAttr.getDefault();

		if (instanceAttr.isShared()) {
			bf.append(" SHARED ");
			String sharedValue = instanceAttr.getSharedValue();
			if (sharedValue != null) {
				sharedValue = DBAttrTypeFormatter.formatValueForInput(instanceAttr.getType(),
						sharedValue, false);
				bf.append(sharedValue);
			}
		} else {
			if (defaultv == null) {
				SerialInfo autoInc = instanceAttr.getAutoIncrement();
				if (autoInc != null) {
					bf.append(" AUTO_INCREMENT");
					String seed = autoInc.getMinValue();
					String incrementValue = autoInc.getIncrementValue();
					if (seed != null && incrementValue != null
					/*&& !("1".equals(seed) && "1".equals(incrementValue))*/) {
						bf.append("(");
						bf.append(seed).append(",").append(incrementValue);
						bf.append(")");
					}
				}
			} else {
				defaultv = DBAttrTypeFormatter.formatValueForInput(instanceAttr.getType(),
						defaultv, false);
				if (defaultv != null && defaultv.trim().length() == 0) {
					defaultv = "'" + defaultv + "'";
				}
				bf.append(" DEFAULT ").append(defaultv);
			}
		}

		if (pkAttributes.contains(instanceAttr.getName())) {
			bf.append(" NOT NULL");
		} else {
			if (instanceAttr.isNotNull()) {
				bf.append(" NOT NULL");
			}
		}

		return bf.toString();
	}

	/**
	 * set the string to end a statement
	 *
	 * @param endLineChar String the given tag that indicates the end of a line
	 */
	public void setEndLineChar(String endLineChar) {
		this.endLineChar = endLineChar;
	}

	/**
	 * In a normal table into a partitioned table SQL
	 *
	 * @param partInfoList List<PartitionInfo> the given list that includes the
	 *        reference of the PartitionInfo object
	 * @return String a string that indicates the transform PartitionInfo DDL
	 */
	public String getTransformToPartitionDDL(List<PartitionInfo> partInfoList) {

		if (partInfoList == null || partInfoList.isEmpty()) {
			return null;
		}

		StringBuilder ddl = new StringBuilder();

		// Based on the first partition, determine the type of the partition information
		PartitionInfo inf = partInfoList.get(0);
		PartitionType partitionType = inf.getPartitionType();
		String tableName = inf.getClassName();
		String columnName = inf.getPartitionExpr();

		ddl.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName));
		ddl.append(" PARTITION BY ").append(partitionType.getText());
		ddl.append(" (").append(QuerySyntax.escapeKeyword(columnName)).append(")");

		if (partitionType == PartitionType.HASH) {
			ddl.append(" PARTITIONS ").append(partInfoList.size());
			return ddl.toString();
		}

		ddl.append(" (");

		for (int i = 0, len = partInfoList.size(); i < len; i++) {
			inf = partInfoList.get(i);
			if (i > 0) {
				ddl.append(", ");
			}
			ddl.append("PARTITION ").append(QuerySyntax.escapeKeyword(inf.getPartitionName()));
			ddl.append(" VALUES ");
			if (partitionType == PartitionType.RANGE) {
				if (inf.getPartitionValues().get(1) == null) {
					ddl.append("LESS THAN MAXVALUE");
				} else {
					boolean isUsingQuote = PartitionUtil.isUsingQuoteForExprValue(inf.getPartitionExprType());
					ddl.append("LESS THAN (");
					if (isUsingQuote) {
						ddl.append("'");
					}
					ddl.append(inf.getPartitionValues().get(1));
					if (isUsingQuote) {
						ddl.append("'");
					}
					ddl.append(")");
				}
			} else if (partitionType == PartitionType.LIST) {
				ddl.append("IN (");
				ddl.append(inf.getPartitionValuesString(PartitionUtil.isUsingQuoteForExprValue(inf.getPartitionExprType())));
				ddl.append(")");
			} else {
				throw new RuntimeException("Invalid partition type.");
			}
		}

		ddl.append(")");

		return ddl.toString();
	}

	/**
	 * In a partitioned table into a normal table SQL
	 *
	 * @param tableName String the table name
	 * @return String a string that indicates the transform to generic DDL
	 */
	public String getTransformToGenericDDL(String tableName) {

		return "ALTER TABLE " + QuerySyntax.escapeKeyword(tableName) + " REMOVE PARTITIONING";
	}

	/**
	 * Add partition (RANGE, HASH)
	 *
	 * @param inf PartitionInfo the given reference of a PartitionInfo object
	 * @return String a string that indicates add partition DDL statement
	 */
	public String getAddPartitionDDL(PartitionInfo inf) {
		// TODO:must find a way to HASH
		StringBuilder ddl = new StringBuilder();

		if (inf.getPartitionType() == PartitionType.RANGE) {
			ddl.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(inf.getClassName())).append(
					" ADD PARTITION (");
			ddl.append("PARTITION ").append(inf.getPartitionName());
			ddl.append(" VALUES LESS THAN ");
			if (inf.getPartitionValues().get(1).equalsIgnoreCase("MAXVALUE")) {
				ddl.append("MAXVALUE");
			} else {
				ddl.append("(").append(inf.getPartitionValues().get(1)).append(")");
			}
			ddl.append(")");
		} else {
			return null;
		}

		return ddl.toString();
	}

	/**
	 * Get delete partition DDL
	 *
	 * @param tableName String the given table name
	 * @param partName String the partition part name
	 * @return String a string that indicates the delete partition part DDL
	 */
	public String getDelPartitionDDL(String tableName, String partName) {

		return "ALTER TABLE " + QuerySyntax.escapeKeyword(tableName) + " DROP PARTITION "
				+ QuerySyntax.escapeKeyword(partName);
	}

	/**
	 * Get coalesce partition DDL
	 *
	 * @param oldPartList List<PartitionInfo> the given list that includes the
	 *        old instance of PartitionInfo
	 * @param newPartList List<PartitionInfo> the given list that includes the
	 *        new instance of PartitionInfo
	 * @return String a string that indicates the info of coalesce partition DDL
	 */
	public String getCoalescePartitionDDL(List<PartitionInfo> oldPartList,
			List<PartitionInfo> newPartList) {

		PartitionInfo newPart = newPartList.get(0);
		if (newPart.getPartitionType() == PartitionType.HASH) {
			int partCnt = oldPartList.size() - newPartList.size();
			return "ALTER TABLE " + QuerySyntax.escapeKeyword(newPart.getClassName())
					+ " COALESCE PARTITION " + partCnt;
		} // TODO: HASH coalesce to merge as the number means that number is. UI and the means were different note ...

		StringBuilder ddl = new StringBuilder();
		ddl.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(newPart.getClassName())).append(
				" REORGANIZE PARTITION ");
		for (int i = 0, len = oldPartList.size(); i < len; i++) {
			PartitionInfo inf = oldPartList.get(i);
			if (i > 0) {
				ddl.append(", ");
			}
			ddl.append(inf.getPartitionName());
		}
		ddl.append(StringUtil.NEWLINE);
		ddl.append(" INTO (PARTITION ").append(
				QuerySyntax.escapeKeyword(newPart.getPartitionName()));
		ddl.append(" VALUES ");
		if (newPart.getPartitionType() == PartitionType.LIST) {
			ddl.append("IN (");
			ddl.append(newPart.getPartitionValuesString(true));
			ddl.append(")");
		} else if (newPart.getPartitionType() == PartitionType.RANGE) {
			ddl.append("LESS THAN (");
			ddl.append(newPart.getPartitionValues().get(1) == null ? "MAXVALUE"
					: newPart.getPartitionValues().get(1));
			ddl.append(")");
		}
		ddl.append(")");

		return ddl.toString();
	}

	/**
	 * Get split partition DDL
	 *
	 * @param oldPartList List<PartitionInfo> the given list that includes the
	 *        old instance of PartitionInfo
	 * @param newPartList List<PartitionInfo> the given list that includes the
	 *        new instance of PartitionInfo
	 * @return String a string that indicates the split partition DDL
	 */
	public String getSplitPartitionDDL(List<PartitionInfo> oldPartList,
			List<PartitionInfo> newPartList) {

		PartitionInfo oldPart = oldPartList.get(0);

		if (oldPart.getPartitionType() == PartitionType.HASH) {
			int partCnt = newPartList.size() - oldPartList.size();
			return "ALTER TABLE " + QuerySyntax.escapeKeyword(oldPart.getClassName())
					+ " ADD PARTITION PARTITIONS " + partCnt;
		}

		StringBuilder ddl = new StringBuilder();
		ddl.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(oldPart.getClassName()));
		ddl.append(" REORGANIZE PARTITION ").append(
				QuerySyntax.escapeKeyword(oldPart.getPartitionName())).append(" INTO (").append(
				StringUtil.NEWLINE);

		if (oldPart.getPartitionType() == PartitionType.RANGE) {
			for (int i = 0, len = newPartList.size(); i < len; i++) {
				PartitionInfo inf = newPartList.get(i);
				if (i > 0) {
					ddl.append(",").append(StringUtil.NEWLINE);
				}
				ddl.append("PARTITION ").append(QuerySyntax.escapeKeyword(inf.getPartitionName()));
				ddl.append(" VALUES LESS THAN (").append(
						inf.getPartitionValues().get(1) == null ? "MAXVALUE"
								: inf.getPartitionValues().get(1)).append(")");
			}
		} else if (oldPart.getPartitionType() == PartitionType.LIST) {
			for (int i = 0, len = newPartList.size(); i < len; i++) {
				PartitionInfo inf = newPartList.get(i);
				if (i > 0) {
					ddl.append(",").append(StringUtil.NEWLINE);
				}
				ddl.append("PARTITION ").append(QuerySyntax.escapeKeyword(inf.getPartitionName()));
				ddl.append(" VALUES IN (").append(inf.getPartitionValuesString(true)).append(")");
			}
		}

		ddl.append(")");

		return ddl.toString();
	}

	/**
	 *
	 * Check whether the partition change
	 *
	 * @param oldPartitionInfoList the old partition information list
	 * @param newPartitionInfoList the new partition information list
	 * @return <code>true</code> if changed;otherwise <code>false</code>
	 */
	private boolean isPartitonChange(List<PartitionInfo> oldPartitionInfoList,
			List<PartitionInfo> newPartitionInfoList) {

		if (oldPartitionInfoList == null || oldPartitionInfoList.isEmpty()) {
			if (newPartitionInfoList == null || newPartitionInfoList.isEmpty()) {
				return false;
			}
			return true;
		} else {
			if (newPartitionInfoList == null || newPartitionInfoList.isEmpty()) {
				return true;
			}
			if (oldPartitionInfoList.size() != newPartitionInfoList.size()) {
				return true;
			}
			if (oldPartitionInfoList.get(0).getPartitionType() != newPartitionInfoList.get(0).getPartitionType()) {
				return true;
			}
			if (!oldPartitionInfoList.get(0).getPartitionExpr().trim().equals(
					newPartitionInfoList.get(0).getPartitionExpr().trim())) {
				return true;
			}
			for (int i = 0; i < oldPartitionInfoList.size(); i++) {
				PartitionInfo oldInfo = oldPartitionInfoList.get(i);
				boolean isEqual = false;
				for (int j = 0; j < newPartitionInfoList.size(); j++) {
					PartitionInfo newInfo = newPartitionInfoList.get(j);
					if (oldInfo.equals(newInfo)) {
						isEqual = true;
						break;
					}
				}
				if (!isEqual) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * Depending the change auto increment
	 *
	 * @param tableName String the given table name
	 * @param columnName String the given column name
	 * @param startWith String the given startWith
	 * @param incrementby String the given incrementBy
	 * @param minValue String the given columnName
	 * @return String a string that indicates the DDL of alter auto increment
	 */
	public String getAlterAutoIncrementDDL(String tableName, String columnName, String startWith,
			String incrementby, String minValue) {
		int autoIncrementSeed = getAutoIncrementSeed(tableName, columnName);
		boolean isSupportAlterAutoIncrement = CompatibleUtil.isAfter840(databaseInfo);
		if (isSupportAlterAutoIncrement) {
			return generateAutoIncrementUsingAlter(tableName, columnName, autoIncrementSeed);
		} else {
			return generateAutoIncrementUsingSerial(tableName, columnName, autoIncrementSeed, incrementby, minValue);
		}
	}
	
	private int getAutoIncrementSeed(String tableName, String columnName) {
		int seed = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MAX(" + QuerySyntax.escapeKeyword(columnName) + ") + 1 ");
		sql.append("FROM " + QuerySyntax.escapeKeyword(tableName));
		CUBRIDConnectionProxy connection = null;
		CUBRIDResultSetProxy rs = null;
		CUBRIDPreparedStatementProxy pstmt = null;
		
		try {
			connection = (CUBRIDConnectionProxy)JDBCConnectionManager.getConnection(databaseInfo, true);
			pstmt = (CUBRIDPreparedStatementProxy)((Connection)connection).prepareStatement(sql.toString());
			rs = (CUBRIDResultSetProxy)pstmt.executeQuery();
			if (rs.next()) {
				seed = rs.getInt(1);
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		return seed;
	}
	
	private String generateAutoIncrementUsingAlter(String tableName, String columnName, int seed) {
		StringBuilder ddl = new StringBuilder();
            ddl.append("ALTER TABLE ").append(QuerySyntax.escapeKeyword(tableName)).append(" AUTO_INCREMENT=" )
            .append(seed).append(endLineChar).append(StringUtil.NEWLINE);

        return ddl.toString();
	}
	
	private String generateAutoIncrementUsingSerial(String tableName, String columnName, int startWith,
			String incrementby, String minValue) {
		StringBuilder ddl = new StringBuilder();
		// TODO 1. ALTER TABLE tableName RENAME COLUMN columnName as columnName_tmp;
		
		// TODO 2. ALTER TABLE tableName ADD COLUMN columnName TYPE PROPERTIES AUTO_INCREMENT(1, 1) AFTER columnName_tmp;
		
		// TODO 3. UPDATE tableName SET columnName = tableName_ai_columnName.NEXT_VALUE;
		
		// TODO 4. ALTER TABLE tableName DROP COLUMN columnName_tmp;
		
		return ddl.toString();
	}
}

/**
 * The change log comparator
 *
 * @author Kevin.Wang
 * @version 1.0 - Jul 12, 2012 created by Kevin.Wang
 */
class ChangeLogCompartor implements
		Comparator<SchemaChangeLog> {
	private List<DBAttribute> attributes = new ArrayList<DBAttribute>();

	/**
	 * The constructor
	 *
	 * @param schemaInfo
	 */
	ChangeLogCompartor(SchemaInfo schemaInfo) {
		attributes.addAll(schemaInfo.getAttributes());
		attributes.addAll(schemaInfo.getClassAttributes());
	}

	public int compare(SchemaChangeLog o1, SchemaChangeLog o2) {
		String name1 = o1.getNewValue();
		String name2 = o2.getNewValue();
		int index1 = -1, index2 = -1;

		if (name1 != null && name2 != null) {

			for (int i = 0; i < attributes.size(); i++) {
				DBAttribute attr = attributes.get(i);
				if (name1.equals(attr.getName())) {
					index1 = i;
				}
				if (name2.equals(attr.getName())) {
					index2 = i;
				}
			}
		}

		return new Integer(index1).compareTo(index2);
	}
}

class DDLGenerator {
	public static final int TYPE_REBANE_TABLE = 1;
	public static final int TYPE_CHANGE_SUPER = 2;
	public static final int TYPE_ADD_ATTR = 3;
	public static final int TYPE_DROP_ATTR = 4;
	public static final int TYPE_EDIT_ATTR = 5;
	public static final int TYPE_CHANGE_POS = 6;
	public static final int TYPE_ADD_INDEX = 7;
	public static final int TYPE_DROP_INDEX = 8;
	public static final int TYPE_ADD_FK = 9;
	public static final int TYPE_DROP_FK = 10;
	public static final int TYPE_ADD_PARTITON = 11;
	public static final int TYPE_DROP_PARTITON = 12;
	public static final int TYPE_CHANGE_TABLE_COLLATE = 13;
	private List<DDLModel> preModelList = new ArrayList<DDLModel>();
	private List<DDLModel> ddlModelList = new ArrayList<DDLModel>();

	public void addSchemaDDLMode(int type, Object obj, String ddl) {
		/*Don't repeat process constraint*/
		if (hasProcessConstraint(type, ddl)) {
			return;
		}

		DDLModel model = new DDLModel(type, obj, ddl);
		ddlModelList.add(model);
	}

	public void addPreDDLMode(int type, Object obj, String ddl) {
		/*Don't repeat process constraint*/
		if (hasProcessPreConstraint(type, ddl)) {
			return;
		}
		DDLModel model = new DDLModel(type, obj, ddl);
		preModelList.add(model);
	}

	/**
	 * Judge has processed the attribute
	 *
	 * @param attrName
	 * @return
	 */
	public boolean hasProcessedAttr(String attrName) {
		for (DDLModel model : ddlModelList) {
			if (TYPE_EDIT_ATTR == model.getType() || TYPE_ADD_ATTR == model.getType()
					|| TYPE_CHANGE_POS == model.getType()) {
				if (model.getObj() != null && model.getObj() instanceof DBAttribute) {
					DBAttribute attr = (DBAttribute) model.getObj();
					if (attrName.equals(attr.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Judge is process the constraint
	 *
	 * @param type
	 * @param ddl
	 * @return
	 */
	public boolean hasProcessConstraint(int type, String ddl) {
		if (TYPE_ADD_FK == type || TYPE_DROP_FK == type || TYPE_ADD_INDEX == type
				|| TYPE_DROP_INDEX == type) {
			for (DDLModel model : ddlModelList) {
				if (model.getType() == type && StringUtil.isEqualNotIgnoreNull(model.getDdl(), ddl)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Judge is process the constraint
	 *
	 * @param type
	 * @param ddl
	 * @return
	 */
	public boolean hasProcessPreConstraint(int type, String ddl) {
		if (TYPE_ADD_FK == type || TYPE_DROP_FK == type || TYPE_ADD_INDEX == type
				|| TYPE_DROP_INDEX == type) {
			for (DDLModel model : preModelList) {
				if (model.getType() == type && StringUtil.isEqualNotIgnoreNull(model.getDdl(), ddl)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Judje is need create PK again for edit table
	 *
	 * @param pk
	 * @return
	 */
	public boolean isNeedReCreatePK(Constraint pk) {
		List<String> attrList = pk.getAttributes();

		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		for (String attrName : attrList) {
			list1.add(attrName);
			list2.add(attrName);
		}

		for (DDLModel model : ddlModelList) {
			if (model.getType() == TYPE_DROP_ATTR && model.getObj() != null
					&& model.getObj() instanceof DBAttribute) {
				DBAttribute attr = (DBAttribute) model.getObj();
				if (list1.contains(attr.getName())) {
					list1.remove(attr.getName());
				}
			}
		}

		if (list1.size() > 0) {
			return false;
		}

		for (DDLModel model : ddlModelList) {
			if (model.getType() == TYPE_ADD_ATTR && model.getObj() != null
					&& model.getObj() instanceof DBAttribute) {
				DBAttribute attr = (DBAttribute) model.getObj();
				if (list2.contains(attr.getName())) {
					list2.remove(attr.getName());
				}
			}
		}
		if (list2.size() > 0) {
			return false;
		}

		return true;
	}

	public String generatorDDL() {
		StringBuilder sb = new StringBuilder();
		/*Generate prepare ddl*/
		for (DDLModel model : preModelList) {
			sb.append(model.getDdl());
		}

		/*Generate create ddl*/
		for (DDLModel model : ddlModelList) {
			sb.append(model.getDdl());
		}

		return sb.toString();
	}
}

class DDLModel {
	private int type;
	private Object obj;
	private String ddl;

	public DDLModel(int type, Object obj, String ddl) {
		this.type = type;
		this.obj = obj;
		this.ddl = ddl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public String getDdl() {
		return ddl;
	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
	}
}

class ConstraintComparator implements Comparator<Constraint> {

	private final String tableName;

	public ConstraintComparator(String tableName) {
		this.tableName = tableName;
	}

	public int compare(Constraint c1, Constraint c2) {
		Integer c1TypeValue = getConstraintTypeValue(c1.getType());
		Integer c2TypeVaule = getConstraintTypeValue(c2.getType());

		if(c1TypeValue != c2TypeVaule) {
			return c1TypeValue.compareTo(c2TypeVaule);
		}

		String c1Name = c1.getDefaultName(tableName);
		if (c1.getName() != c1Name) {
			c1Name = c1.getName();
		}

		String c2Name = c2.getDefaultName(tableName);
		if (c2.getName() != c2Name) {
			c2Name = c2.getName();
		}

		return c1Name.compareTo(c2Name);
	}


	/*Get the constraint type value, used to sort*/
	private int getConstraintTypeValue(String type) {
		if (type == null) {
			return -1;
		}

		if (ConstraintType.PRIMARYKEY.getText().equalsIgnoreCase(type)) {
			return 0;
		}
		if (ConstraintType.UNIQUE.getText().equalsIgnoreCase(type)) {
			return 1;
		}
		if (ConstraintType.REVERSEUNIQUE.getText().equalsIgnoreCase(type)) {
			return 2;
		}
		if (ConstraintType.INDEX.getText().equalsIgnoreCase(type)) {
			return 3;
		}
		if (ConstraintType.REVERSEINDEX.getText().equalsIgnoreCase(type)) {
			return 4;
		}
		if (ConstraintType.FOREIGNKEY.getText().equalsIgnoreCase(type)) {
			return 5;
		}
		return -1;
	}
}