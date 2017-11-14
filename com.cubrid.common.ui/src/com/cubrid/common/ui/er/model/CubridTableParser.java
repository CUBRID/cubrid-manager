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
package com.cubrid.common.ui.er.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.geometry.Rectangle;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERWinDBAttribute;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERWinSchemaInfo;
import com.cubrid.common.ui.er.ERException;
import com.cubrid.common.ui.er.Messages;

/**
 * Parser cubrid table of {@code SchemaInfo} struct data to {@code ERTable} data
 * 
 * @author Yu Guojia
 * @version 1.0 - 2013-7-2 created by Yu Guojia
 */
public class CubridTableParser {
	private static final Logger LOGGER = LogUtil.getLogger(CubridTableParser.class);
	private final ERSchema erSchema;
	private Map<String, SchemaInfo> schemaInfos = new HashMap<String, SchemaInfo>();
	private List<ERTable> successTables = new LinkedList<ERTable>();
	// the tables built failed and fail reason
	private Map<String, Exception> failedTables = new HashMap<String, Exception>();
	// some fk referenced table may not exist, cannot build the ship, remove the
	// Constraint from SchemaInfo
	private Map<String, List<Constraint>> removedFKConstraint = new HashMap<String, List<Constraint>>();
	// schemaInfos, who will be built, may include some tables that has existed
	// in ERD
	private List<String> existedTableNames = new LinkedList<String>();
	public static int DEFAULT_VERTICAL_DISTANCE = 20;

	public CubridTableParser(ERSchema erSchema) {
		this.erSchema = erSchema;
	}

	/**
	 * Build the ER tables node information and its relationship. If its
	 * relationship tables are not added, donot build these relationship for
	 * efficiency. <br>
	 * This method only build and add , donot fire listener for UI updating
	 * 
	 * @param schemaInfos Collection<schemaInfo> should be built and added
	 *        tables
	 * @param successTables be built successfully
	 * @param startX startX > 0. if it is less than 0, the param will be
	 *        omitted.
	 * @param startY
	 * @param isPartitionLayout if it is true, omit the startX and startY. These
	 *        table will be arranged at the bottom of the canvas.
	 * @throws Exception If throw exception, then all of input table donot be
	 *         built
	 */
	public void buildERTables(Collection<SchemaInfo> schemaInfos, int startX, int startY,
			boolean isPartitionLayout) {

		if (schemaInfos == null || schemaInfos.size() == 0) {
			return;
		}
		for (SchemaInfo schemaInfo : schemaInfos) {
			if (erSchema.getTable(schemaInfo.getClassname()) != null) {
				existedTableNames.add(schemaInfo.getClassname());
				continue;
			}
			this.schemaInfos.put(schemaInfo.getClassname(), schemaInfo);
		}

		Set<String> tables = this.schemaInfos.keySet();
		for (String name : tables) {
			SchemaInfo schemaInfo = this.schemaInfos.get(name);
			ERTable erTable = null;
			try {
				if (erSchema.getTable(schemaInfo.getClassname()) != null) {
					// the table has been built by referenced table
					erTable = erSchema.getTable(schemaInfo.getClassname());
				} else {
					erTable = new ERTable(schemaInfo, erSchema);
					boolean success = erSchema.addTable(erTable);
					if (success && !successTables.contains(erTable)) {
						successTables.add(erTable);
					}
					buildERTable(erTable, schemaInfo);
				}

				if (isPartitionLayout) {
					erTable.setNeedPartitionLayout(true);
				} else if (startX > -1 && startY > -1) {
					erTable.setBounds(new Rectangle(startX, startY, erTable.getMinWidth(),
							erTable.getMinHeight()));
					startY += erTable.getMinHeight() + DEFAULT_VERTICAL_DISTANCE;
				}
			} catch (Exception e) {
				failedTables.put(name, e);
				erSchema.deleteTableAndFire(erTable);
				successTables.remove(erTable);
				LOGGER.warn(e.getMessage());
			}
		}
		this.schemaInfos.clear();
	}

	/**
	 * After build physical info to ERTable, append logical info to success
	 * tables.
	 * 
	 * @param schemaInfos
	 * @return void
	 */
	public void appendLogicalInfo(Map<String, ERWinSchemaInfo> schemaInfos) {
		for (ERTable table : successTables) {
			String physicalTName = table.getName();
			ERWinSchemaInfo erwinSchemaInfo = schemaInfos.get(physicalTName);
			String logicalTableName = erwinSchemaInfo.getLogicalName();
			if (StringUtil.isEmpty(logicalTableName)) {
				logicalTableName = physicalTName;
			}
			table.setName(logicalTableName, false);
			if (!logicalTableName.equals(physicalTName)) {
				table.setDescription(logicalTableName);
			}

			List<ERWinDBAttribute> erwinAttrList = erwinSchemaInfo.getERWinAttributes();
			for (ERWinDBAttribute attr : erwinAttrList) {
				String physicalColName = attr.getName();
				ERTableColumn column = table.getColumn(physicalColName);
				String logicalColName = attr.getLogicalName();
				if (StringUtil.isEmpty(logicalColName)) {
					logicalColName = physicalColName;
				}
				column.setName(logicalColName, false);
				if (!logicalColName.equals(physicalColName)) {
					column.setDescription(logicalColName);
				}
				String logicType = attr.getLogicalDataType();
				if (StringUtil.isEmpty(logicType)) {
					String physicalRealType = column.getRealType();
					logicType = column.getERSchema().convert2LogicalShowType(physicalRealType);
				}
				column.setShowType(logicType, false);
			}
		}
	}

	/**
	 * Build the ER table node information and its relationship. If its
	 * relationship tables are not added, donot build these relationship for
	 * efficiency.<br>
	 * if there do not exist referenced table, delete the FK in {@code}
	 * SchemaInfo!
	 * 
	 * @param schemaInfo
	 * @return
	 */
	public void buildERTable(ERTable erTable, SchemaInfo schemaInfo) throws Exception {
		erTable.buildColumns(schemaInfo);
		buildReferenceShip(erTable, schemaInfo);
	}

	/**
	 * Just only build reference ship keeped in its {@code SchemaInfo} struct.
	 * If the relationship that its pk referenced by others table, the ship
	 * donot be built in the method.<br>
	 * If there do not exist referenced table, delete the FK in {@code}
	 * SchemaInfo, and add to removedFKShips cache
	 * 
	 * @param erTable
	 * @param schemaInfo
	 * @return
	 * @throws Exception
	 */
	public void buildReferenceShip(ERTable erTable, SchemaInfo schemaInfo) throws Exception {
		List<Constraint> fkList = schemaInfo.getFKConstraints();
		if (fkList != null) {
			for (Constraint fkConstaint : fkList) {
				addFKShip(erTable, schemaInfo, fkConstaint);
			}
		}
	}

	public void addFKShip(ERTable erTable, SchemaInfo schemaInfo, Constraint fkConstaint) throws Exception {
		String referencedTableName = getReferencedTableName(fkConstaint);
		SchemaInfo referencedTable = getReferencedTable(fkConstaint);
		List<String> referenceNames = getReferenceColumns(fkConstaint);
		List<String> referredPkColumnNames = getReferredColumns(schemaInfo, fkConstaint);
		boolean isSelfRef = false;
		if (referencedTable == null) {
			// If the referenced Table do not be added to the ERShema. Do not
			// load it and do not build the relationship and delete the
			// constraint
			schemaInfo.removeFKConstraint(fkConstaint);
			addEle2RemovedFKConstraints(schemaInfo.getClassname(), fkConstaint);
			return;
		}
		if (StringUtil.isEqualNotIgnoreNull(referencedTableName, erTable.getName())) {// self reference FK
			isSelfRef = true;
		}

		if (referenceNames.size() != referredPkColumnNames.size()) {
			throw new ERException(Messages.bind(Messages.errFKColumnMatch, new String[] {
					schemaInfo.getClassname(), fkConstaint.getName() }));
		}

		ERTable referencedT = erSchema.getTable(referencedTable.getClassname());
		boolean needBuildRefedTable = false;
		if (referencedT == null && !isSelfRef) {
			referencedT = new ERTable(referencedTable, erSchema);
			needBuildRefedTable = true;
		} else {
			// the referenceTable has been built, do not build again.
		}

		if (isSelfRef) {
			erTable.setHasSelfFKRef(true);
			return;
		}
		Relationship ship = new Relationship(fkConstaint.getName(), erTable, referencedT);
		for (int i = 0; i < referenceNames.size(); i++) {
			ship.addRelation(referenceNames.get(i), referredPkColumnNames.get(i));
		}
		erTable.addForeignKeyShipAndFire(ship);

		if (needBuildRefedTable) {
			buildERTable(referencedT, referencedTable);
			boolean success = erSchema.addTable(referencedT);
			if (success && !successTables.contains(referencedT)) {
				successTables.add(referencedT);
			}
		}
		referencedT.FireTargeted(ship);

		return;
	}

	/**
	 * Get the columns name on the foreign key constraint
	 * 
	 * @param fkConstaint
	 * @return They are relation with the results of {@link
	 *         getReferredColumns(Constraint fkConstaint)}
	 */
	public static List<String> getReferenceColumns(Constraint fkConstaint) {
		List<String> list = fkConstaint.getAttributes();
		List<String> resultList = new ArrayList<String>(list.size());
		for (String name : list) {
			String refKey = name.replace(" ASC", "").replace(" DESC", "");
			resultList.add(refKey);
		}

		return resultList;
	}

	/**
	 * Get the other table columns name which is referred on the foreign key
	 * constraint
	 * 
	 * @param schemaInfo
	 * @param fkConstaint
	 * @return The results is all columns of pk in the primary table actually.
	 * @throws Exception
	 */
	private List<String> getReferredColumns(SchemaInfo schemaInfo, Constraint fkConstaint) throws Exception {
		List<String> resultList = new ArrayList<String>();
		String refTable = getReferencedTableName(fkConstaint);
		SchemaInfo referredSchemaInfo = getReferencedTable(refTable);
		if (referredSchemaInfo != null) {
			Constraint pkConstaint = referredSchemaInfo.getPK();
			if (pkConstaint == null) {
				throw new ERException(Messages.bind(Messages.errFKColumnMatch, new String[] {
						schemaInfo.getClassname(), fkConstaint.getName() }));
			}
			List<String> pklist = pkConstaint.getAttributes();
			for (int i = 0; i < pklist.size(); i++) {
				String referredKey = pklist.get(i).replace(" ASC", "").replace(" DESC", "");
				resultList.add(referredKey);
			}
		}

		return resultList;
	}

	/**
	 * The referenced table may be in erSchema, or schemaInfos Map
	 * 
	 * @param fkConstaint
	 * @return
	 */
	private SchemaInfo getReferencedTable(Constraint fkConstaint) {
		String refedTable = getReferencedTableName(fkConstaint);
		if (StringUtil.isEmpty(refedTable)) {
			return null;
		}

		return getReferencedTable(refedTable);
	}

	private SchemaInfo getReferencedTable(String name) {
		SchemaInfo table = erSchema.getSchemaInfo(name);
		if (table == null) {
			table = this.schemaInfos.get(name);
		}

		return table;
	}

	/**
	 * Get the referenced table name
	 * 
	 * @param fkConstaint
	 * @return
	 */
	private String getReferencedTableName(Constraint fkConstaint) {
		List<String> rlist = fkConstaint.getRules();
		return rlist.get(0).replace("REFERENCES ", "");
	}

	public List<ERTable> getSuccessTables() {
		return successTables;
	}

	public void clearSuccessTables() {
		this.successTables.clear();
	}

	public Map<String, Exception> getFailedTables() {
		return failedTables;
	}

	public void clearFailedTables() {
		this.failedTables.clear();
	}

	public Map<String, List<Constraint>> getRemovedFKConstraints() {
		return removedFKConstraint;
	}

	public int getRemovedFKCount() {
		int count = 0;
		Iterator<String> it = removedFKConstraint.keySet().iterator();
		while (it.hasNext()) {
			List<Constraint> fks = removedFKConstraint.get(it.next());
			count += fks.size();
		}

		return count;
	}

	public Constraint getOneRemovedFK() {
		Iterator<String> it = removedFKConstraint.keySet().iterator();
		if (it.hasNext()) {
			List<Constraint> fks = removedFKConstraint.get(it.next());
			return fks.get(0);
		}

		return null;
	}

	private void addEle2RemovedFKConstraints(String tableName, Constraint constraint) {
		List<Constraint> ships = removedFKConstraint.get(tableName);
		if (ships == null) {
			ships = new LinkedList<Constraint>();
		}
		if (!ships.contains(constraint)) {
			ships.add(constraint);
			removedFKConstraint.put(tableName, ships);
		}
	}

	public void clearRemovedFKShips() {
		this.removedFKConstraint.clear();
	}

	public List<String> getExistedTableName() {
		return existedTableNames;
	}

	public void clearExistedTableName() {
		this.existedTableNames.clear();
	}
}
