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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.geometry.Rectangle;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.er.ERException;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;

/**
 * Includes the bounds of the table and general info on ERD. So that the diagram
 * can be restored following a save, although ideally this should be in a
 * separate diagram specific model hierarchy. And when the {@code ERTable} is
 * changed, the object of SchemaInfo in it should be changed too.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-13 created by Yu Guojia
 */
public class ERTable extends
		PropertyChangeProvider {
	private static final long serialVersionUID = 4226414891103390375L;
	private static final Logger LOGGER = LogUtil.getLogger(ERTable.class);

	private String logicalName;
	private SchemaInfo schemaInfo;
	private ArrayList<ERTableColumn> columns = new ArrayList<ERTableColumn>();
	private Rectangle bounds = new Rectangle(0, 0, 0, 0);

	private transient ERSchema erSchema;
	private transient boolean needPartitionLayout = false;
	private transient boolean hasSelfFKRef = false;
	private transient List<Relationship> fkships = new ArrayList<Relationship>();

	public ERTable() {
		name = "";
		schemaInfo = createEmptySchemaInfo("", "");
		initDefaultLogicalInfo(schemaInfo);
	}

	public ERTable(SchemaInfo schemaInfo, ERSchema erSchema) {
		super();
		this.name = schemaInfo.getClassname();
		this.schemaInfo = schemaInfo;
		this.erSchema = erSchema;
		initDefaultLogicalInfo(schemaInfo);
	}

	private void initDefaultLogicalInfo(SchemaInfo schemaInfo) {
		String desc = schemaInfo.getDescription();
		if (StringUtil.isNotEmpty(desc)) {
			logicalName = desc;
		} else {
			logicalName = schemaInfo.getClassname();
		}
	}

	/**
	 * Build the columns in the schemaInfo to {@code ERTable}, and add to the er
	 * table.
	 *
	 * @param schemaInfo
	 */
	public void buildColumns(SchemaInfo schemaInfo) {
		List<DBAttribute> attributesList = schemaInfo.getAttributes();
		Constraint pk = schemaInfo.getPK();

		for (DBAttribute attribute : attributesList) {
			if (attribute == null || attribute.getInherit() == null) {
				continue;
			}
			boolean isPK = pk == null ? false : pk.contains(attribute.getName(), false);
			ERTableColumn column = new ERTableColumn(this, attribute, isPK);
			this.addColumn(column);
		}
	}

	/**
	 * @return the schemaInfo
	 */
	public SchemaInfo getSchemaInfo() {
		return schemaInfo;
	}

	/**
	 * mininal width.
	 *
	 * @return int
	 */
	public int getMinWidth() {
		return 60;
	}

	/**
	 * It includes all column height and table header height by multiplying "22"
	 * and also need blank space height under the table by adding "20" height.
	 *
	 * @return int
	 */
	public int getMinHeight() {
		return 22 * (columns.size() + 1) + 20;
	}

	private String getReferenceTableName(Constraint fkConstaint) {
		return fkConstaint.getReferencedTable();
	}

	public static SchemaInfo createEmptySchemaInfo(String tableName, String dbName) {
		SchemaInfo schemaInfo = new SchemaInfo();
		schemaInfo.setType("user");
		schemaInfo.setClassname(tableName);
		schemaInfo.setDbname(dbName);
		schemaInfo.setVirtual(ClassType.NORMAL.getText());
		schemaInfo.setReuseOid(false);

		return schemaInfo;
	}

	private void resetDatabase(CubridDatabase database) {
		schemaInfo.setOwner(database.getUserName());
		schemaInfo.setDbname(database.getName());
		if (database != null && database.getDatabaseInfo() != null) {
			schemaInfo.setCollation(database.getDatabaseInfo().getCollation());
		}
	}

	@Override
	public CubridDatabase getCubridDatabase() {
		return erSchema.getCubridDatabase();
	}

	public ERTableColumn getColumn(String physicalName) {
		if (StringUtil.isEmpty(physicalName)) {
			return null;
		}
		for (ERTableColumn col : columns) {
			if (physicalName.equals(col.getName())) {
				return col;
			}
		}
		return null;
	}

	private ERTableColumn getLogicalColumn(String logicalName) {
		if (StringUtil.isEmpty(logicalName)) {
			return null;
		}
		for (ERTableColumn col : columns) {
			if (logicalName.equals(col.getName(false))) {
				return col;
			}
		}
		return null;
	}

	public ERTableColumn getColumn(String name, boolean isPhysical) {
		if (isPhysical) {
			return getColumn(name);
		}
		return getLogicalColumn(name);
	}

	public boolean hasPK() {
		if (!getPKList().isEmpty()) {
			return true;
		}
		return false;
	}

	public LinkedList<ERTableColumn> getPKList() {
		LinkedList<ERTableColumn> pkColumns = new LinkedList<ERTableColumn>();
		for (ERTableColumn col : columns) {
			if (col.isPrimaryKey()) {
				pkColumns.add(col);
			}
		}

		return pkColumns;
	}

	public Set<String> getPKSet() {
		Set<String> pkColumns = new HashSet<String>();
		for (ERTableColumn col : columns) {
			if (col.isPrimaryKey()) {
				pkColumns.add(col.getName());
			}
		}
		return pkColumns;
	}

	/**
	 * If there exist the name column, the column would not be added.
	 *
	 * @param ERTableColumn column
	 */
	public void addColumn(ERTableColumn column) {
		if (getColumn(column.getName()) != null) {
			return;
		}
		columns.add(column);
		if (schemaInfo.getDBAttributeByName(column.getAttr().getName(),
				column.getAttr().isClassAttribute()) == null) {
			schemaInfo.addAttribute(column.getAttr());
		}
	}

	/**
	 * If there exist the name column, the column would not be added. Add and
	 * fire column
	 *
	 * @param ERTableColumn column
	 */
	public void addColumnAndFire(ERTableColumn column) {
		if (getColumn(column.getName()) != null) {
			return;
		}
		addColumn(column);
		firePropertyChange(CHILD_CHANGE, null, column);
	}

	/**
	 * If there exist the name column, the column would not be added. Add column
	 * to the ER table model and add corresponding {@code Attribute} to
	 * {@code SchemaInfo}, then notify listener.
	 *
	 * @param ERTableColumn column
	 * @param int index
	 */
	public void addColumnAndFire(ERTableColumn column, int index) {
		if (getColumn(column.getName()) != null) {
			return;
		}
		columns.add(index, column);
		if (schemaInfo.getDBAttributeByName(column.getAttr().getName(),
				column.getAttr().isClassAttribute()) == null) {
			schemaInfo.addAttribute(column.getAttr());
		}
		firePropertyChange(CHILD_CHANGE, null, column);
	}

	/**
	 * Delete column from the ER table model and delete corresponding
	 * {@code Attribute} from {@code SchemaInfo}, then notify listener.
	 *
	 * @param column
	 */
	public void removeColumnAndFire(ERTableColumn column) {
		if (column == null) {
			return;
		}
		columns.remove(column);
		schemaInfo.removeDBAttributeByName(column.getName(),
				column.getAttr().isClassAttribute());
		schemaInfo.removeInvalidPKAndIndex(true);
		firePropertyChange(CHILD_CHANGE, column, null);
	}

	/**
	 * Delete column from the ER table model and delete corresponding
	 * {@code Attribute} from {@code SchemaInfo}.
	 *
	 * @param String column name
	 * @param boolean physical or logical column name
	 */
	public void removeColumn(String name, boolean isPhysical) {
		if (StringUtil.isEmpty(name)) {
			return;
		}
		for (ERTableColumn column : columns) {
			if (StringUtil.isEqual(column.getName(isPhysical), name)) {
				columns.remove(column);
				schemaInfo.removeDBAttributeByName(column.getName(),
						column.getAttr().isClassAttribute());
				schemaInfo.removeInvalidPKAndIndex(true);
				return;
			}
		}

	}

	public void switchColumnAndFire(ERTableColumn column, int index) {
		columns.remove(column);
		columns.add(index, column);
		firePropertyChange(REORDER_CHANGE, this, column);
	}

	/**
	 * If the old name don't exist, do nothing.
	 *
	 * @param oldName
	 * @param newColumn
	 * @param isPK
	 */
	public void modifyColumn(String oldName, boolean isPhysical, ERTableColumn newColumn) {
		ERTableColumn oldColumn = getColumn(oldName, isPhysical);
		if (oldColumn == null) {
			return;
		}
		String oldAttr = oldColumn.getName();
		oldColumn.buildERColumn(newColumn);
		schemaInfo.replaceDBAttributeByName(oldAttr, oldColumn.getAttr());
		if (!StringUtil.isEqual(oldColumn.getAttr().getName(), newColumn.getAttr().getName())) {
			schemaInfo.updateAttrNameInIndex(oldColumn.getAttr().getName(),
					newColumn.getAttr().getName());
		}
	}

	/**
	 * Check the table validity
	 *
	 * @throws ERException throw the invalid exception message
	 */
	public void checkValidate() throws ERException {
		checkName();
		checkRelationShipValid();
		for (ERTableColumn column : this.columns) {
			String err = ERTableColumn.checkName(column.getName());
			if (!StringUtil.isEmpty(err)) {
				throw new ERException(err);
			}
			err = ERTableColumn.checkDataShowType(column.getShowType());
			if (!StringUtil.isEmpty(err)) {
				throw new ERException(err);
			}
		}

	}

	/**
	 * Check the FK relation ship validity.
	 *
	 * @throws ERException
	 */
	public void checkRelationShipValid() throws ERException {
		Set<String> pkSet = getPKSet();
		// check columns name in relationship of this table is all in the column
		// list of this table, and its validation
		List<Relationship> referencedRelationships = getTargetedRelationships();
		for (Relationship ship : referencedRelationships) {
			Collection<String> referredPKSet = ship.getReferencedPKs();
			if (referredPKSet.size() != pkSet.size()) {
				throw new ERException(Messages.bind(Messages.errFKcolumnSize,
						new String[] { ship.getName(), ship.getForeignKeyTable().getShownName() }));
			}
			for (String name : referredPKSet) {
				if (!pkSet.contains(name)) {
					throw new ERException(Messages.bind(Messages.errNotExistRefedCol, new String[] {
							name, ship.getName(), ship.getForeignKeyTable().getShownName(),
							ship.getPrimaryKeyTable().getShownName() }));
				}
				// check data type match
				checkDataTypeMatch(name, ship.getRefColByPK(name), ship.getForeignKeyTable());

			}

			// referColSet should be in the foreign table
			Set<String> referColSet = ship.getReferenceColumns();
			for (String col : referColSet) {
				if (ship.getForeignKeyTable().getColumn(col) == null) {
					throw new ERException(Messages.bind(Messages.errNotExistRefCol, new String[] {
							col, ship.getName(), ship.getForeignKeyTable().getShownName() }));
				}
			}
		}

		for (Relationship ship : this.fkships) {
			// referColSet should be in the this table
			Set<String> referColSet = ship.getReferenceColumns();
			for (String col : referColSet) {
				if (this.getColumn(col) == null) {
					throw new ERException(Messages.bind(Messages.errNotExistRefCol, new String[] {
							col, ship.getName(), this.name }));
				}
			}

			// referredPKSet should be in the pk table
			Collection<String> referredPKSet = ship.getReferencedPKs();
			Set<String> pkTablePKSet = ship.getPrimaryKeyTable().getPKSet();
			if (referredPKSet.size() != pkTablePKSet.size()) {
				throw new ERException(Messages.bind(Messages.errFKcolumnSize,
						new String[] { ship.getName(), this.getShownName() }));
			}
			for (String name : referredPKSet) {
				if (!pkTablePKSet.contains(name)) {
					throw new ERException(Messages.bind(Messages.errNotExistRefedCol, new String[] {
							name, ship.getName(), this.getShownName(),
							ship.getPrimaryKeyTable().getShownName() }));
				}

				//
				checkDataTypeMatch(ship.getRefColByPK(name), name, ship.getPrimaryKeyTable());
			}
		}
	}

	public void checkDataTypeMatch(String colName, String relationColName, ERTable relationTable) throws ERException {
		ERTableColumn thisColumn = getColumn(colName);
		ERTableColumn relationColumn = relationTable.getColumn(relationColName);
		if (thisColumn == null) {
			throw new ERException(Messages.bind(Messages.errNotExistColInTable, new String[] {
					colName, name }));
		}
		if (relationColumn == null) {
			throw new ERException(Messages.bind(Messages.errNotExistColInTable, new String[] {
					relationColName, relationTable.getShownName() }));
		}
		if (!thisColumn.getShowType().equals(relationColumn.getShowType())) {
			throw new ERException(Messages.errDonotMatchDataType);
		}
	}

	public Set<String> getNamesSet() {
		Set<String> allColNames = new HashSet<String>();
		for (ERTableColumn column : this.columns) {
			allColNames.add(column.getName());
		}
		return allColNames;
	}

	/**
	 * If the newColumn is null, it is to delete the old column. If there is any
	 * foreign key relationship about the table, the column can't be deleted and
	 * change some information. <br>
	 * (1)newColumn is null, it is to delete the old; <br>
	 * (2)newColumn and oldColumn isnot null, it is to modify the old;
	 *
	 * @param oldColumn
	 * @param newColumn
	 * @return String checked error message
	 */
	public String checkColumnChange(ERTableColumn oldColumn, ERTableColumn newColumn) {
		if (oldColumn == null || this.getColumn(oldColumn.getName()) == null) {
			return null;
		}

		if (newColumn == null) {
			return checkDeleteColumn(oldColumn);
		} else {
			return checkModifyColumn(oldColumn, newColumn);
		}
	}

	private String checkModifyColumn(ERTableColumn oldColumn, ERTableColumn newColumn) {
		if (oldColumn == null || this.getColumn(oldColumn.getName()) == null || newColumn == null
				|| newColumn.getName() == null) {
			return null;
		}

		boolean isNameChanged = !StringUtil.isEqualNotIgnoreNull(oldColumn.getName(),
				newColumn.getName());
		boolean isDataTypeChanged = !StringUtil.isEqualNotIgnoreNull(oldColumn.getShowType(),
				newColumn.getShowType());
		boolean isPK2NotPK = oldColumn.isPrimaryKey() && !newColumn.isPrimaryKey();
		boolean isNotPK2PK = !oldColumn.isPrimaryKey() && newColumn.isPrimaryKey();

		if (isNameChanged && getColumn(newColumn.getName()) != null) {//duplicated name
			return Messages.errExistColumnName;
		}

		if (isOneRefColumn(oldColumn.getName())) {
			if (isNameChanged) {
				return Messages.errChangeColNameInFK;
			}
			if (isDataTypeChanged) {
				return Messages.errChangeColTypeInFK;
			}
		} else if (isOneRefedColumn(oldColumn.getName())) {
			if (isNameChanged) {
				return Messages.errChangeColNameInFK;
			}
			if (isDataTypeChanged) {
				return Messages.errChangeColTypeInFK;
			}
			if (isPK2NotPK) {
				return Messages.errCancelExistedPK;
			}
			if (isNotPK2PK && getTargetedRelationships() != null
					&& getTargetedRelationships().size() > 0) {
				return Messages.errAddNewPK;
			}
		}

		String defaultValue = newColumn.getAttr().getDefault();
		if (!StringUtil.isEmpty(defaultValue)) {
			FormatDataResult formatDataResult = DBAttrTypeFormatter.format(newColumn.getShowType(),
					defaultValue, true, getCubridDatabase().getDatabaseInfo().getCharSet(), false);
			if (!formatDataResult.isSuccess()) {
				return Messages.bind(Messages.errMatchDefaultValue, defaultValue,
						newColumn.getShowType());
			}
		}

		return ERTableColumn.checkDataShowType(newColumn.getShowType());
	}

	private String checkDeleteColumn(ERTableColumn oldColumn) {
		String message = null;
		if (oldColumn == null || this.getColumn(oldColumn.getName()) == null) {
			return null;
		}

		if (isOneRefColumn(oldColumn.getName())) {
			message = Messages.errDeleteColInFK;
		} else if (isOneRefedColumn(oldColumn.getName())) {
			message = Messages.errDeleteColByRefed;
		}
		return message;
	}

	/**
	 *
	 * @param name If the name is a reference column , return true
	 * @return
	 */
	public boolean isOneRefColumn(String name) {
		if (StringUtil.isEmpty(name)) {
			return false;
		}
		for (Relationship ship : fkships) {
			if (ship.getReferenceColumns().contains(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param name If the column is in pk, and it is referenced by other, return
	 *        true.
	 * @return
	 */
	public boolean isOneRefedColumn(String name) {
		if (StringUtil.isEmpty(name)) {
			return false;
		}

		ERTableColumn column = this.getColumn(name);
		if (column == null || !column.isPrimaryKey()) {
			return false;
		}

		List<Relationship> pkRelationships = this.getTargetedRelationships();
		for (Relationship ship : pkRelationships) {
			if (ship.getReferencedPKs().contains(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If the gave column is referenced by one column of table, return true.
	 *
	 * @param colName
	 * @return
	 */
	public boolean isReferencedColumn(String colName) {
		List<Relationship> pkRelationships = this.getTargetedRelationships();
		for (Relationship ship : pkRelationships) {
			Collection<String> set = ship.getReferencedPKs();
			if (set.contains(colName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If the column reference(connect to, line to) one column of table, return
	 * true.
	 *
	 * @param colName
	 * @return
	 */
	public boolean isRef2Other(String colName) {
		for (Relationship ship : fkships) {
			Set<String> set = ship.getReferenceColumns();
			if (set.contains(colName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Modify physical name
	 */
	public void modifyNameAndFire(String name) {
		String oldName = this.name;
		if (!name.equals(oldName)) {
			this.name = name;
			schemaInfo.setClassname(name);
			updateSourceTableConstraint(oldName, name);
			firePropertyChange(TEXT_CHANGE, null, name);
		}
	}

	public void modifyNameAndFire(String name, boolean isPhysical) {
		if (isPhysical) {
			modifyNameAndFire(name);
		} else {
			setLogicalName(name);
		}
		firePropertyChange(TEXT_CHANGE, null, name);
	}

	/**
	 * Update the constraint info that relation to this table
	 *
	 * @param oldName
	 * @param newName
	 */
	public void updateSourceTableConstraint(String oldName, String newName) {
		List<Relationship> sourceShips = getTargetedRelationships();
		for (Relationship ship : sourceShips) {
			ERTable sourceTable = ship.getForeignKeyTable();
			sourceTable.getSchemaInfo().updateForeignSchemainfoName(oldName, newName);
		}
	}

	/**
	 * Add relationship where the current object is the foreign key table in a
	 * relationship, and fire The relation ship do not refresh to
	 * {@code SchemaInfo}.
	 *
	 * @param Relationship the primary key relationship
	 */
	public void addForeignKeyShipAndFire(Relationship relationship) {
		addSourceShipAndFire(relationship);
	}

	/**
	 * Add relationship where the current object is the foreign key table in a
	 * relationship, and fire. The relation ship do not refresh to
	 * {@code SchemaInfo}.
	 *
	 * @param relationship
	 */
	public void addSourceShipAndFire(Relationship relationship) {
		if (fkships.contains(relationship)) {
			return;
		}
		fkships.add(relationship);
		firePropertyChange(OUTPUT_CHANGE, null, relationship);
	}

	/**
	 * The current object is referenced in a relationship, fire that it is
	 * targeted. The relation ship do not refresh to {@code SchemaInfo}.
	 *
	 * @param relationship
	 */
	public void FireTargeted(Relationship relationship) {
		firePropertyChange(INPUT_CHANGE, null, relationship);
	}

	/**
	 * Add relationship to source table part and target table part, and fire
	 * listener. The relation ship do not refresh to {@code SchemaInfo}.
	 *
	 * @param Relationship the primary key relationship
	 */
	public void addSourceTargetShipAndFire(Relationship relationship) {
		addSourceShipAndFire(relationship);
		relationship.getForeignKeyTable().FireTargeted(relationship);
	}

	/**
	 * Removes relationship where the current object is the foreign key table in
	 * a relationship. The relation ship should be deleted form
	 * {@code SchemaInfo} cache too.
	 *
	 * @param Relationship the primary key relationship
	 */
	public void deleteForeignKeyShipAndFire(Relationship relationship) {
		if (!fkships.contains(relationship)) {
			return;
		}
		fkships.remove(relationship);
		this.schemaInfo.removeFKConstraint(relationship.getName());
		firePropertyChange(OUTPUT_CHANGE, relationship, null);
	}

	/**
	 * Delete all fk source lines in the table
	 */
	public void deleteAllFKShipsAndFire() {
		for (Relationship relationship : fkships) {
			this.schemaInfo.removeFKConstraint(relationship.getName());
			firePropertyChange(OUTPUT_CHANGE, relationship, null);
		}
		fkships.clear();
	}

	/**
	 * Delete fk target ships that connects to this table.
	 */
	public void deleteAllTargetedShipsAndFire() {

		List<Relationship> ships = this.getTargetedRelationships();
		for (Relationship ship : ships) {
			ship.getForeignKeyTable().deleteForeignKeyShipAndFire(ship);
		}
	}

	/**
	 * If modified, sets bounds and fires off event notification
	 *
	 * @param bounds The bounds to set.
	 */
	public void modifyBoundAndFire(Rectangle bounds) {
		if (!bounds.equals(this.bounds)) {
			this.bounds = bounds;
			firePropertyChange(BOUNDS_CHANGE, null, bounds);
		}
	}

	/**
	 * If current model is physical view return physical name. If the current
	 * model is logical view return logical table name.
	 *
	 * @return String
	 */
	public String getShownName() {
		if (erSchema.isPhysicModel()) {
			return name;
		}
		return getLogicName();
	}

	public String getName(boolean isPhysical) {
		if (isPhysical) {
			return name;
		}
		return getLogicName();
	}

	public Set<String> getColSet() {
		Set<String> cols = new HashSet<String>();
		for (ERTableColumn col : columns) {
			cols.add(col.getName());
		}
		return cols;
	}

	public List<Relationship> getForeignKeyRelationships() {
		return fkships;
	}

	/**
	 * Get targeted connections. That lines who connect to this table
	 *
	 * @return Returns the primaryKeyRelationships.
	 */
	public List<Relationship> getTargetedRelationships() {

		List<Relationship> results = new LinkedList<Relationship>();
		List<ERTable> allTables = this.erSchema.getTables();
		for (ERTable table : allTables) {
			List<Relationship> ships = table.getForeignKeyRelationships();
			for (Relationship ship : ships) {
				if (ship.getPKTableName().equals(name)) {
					results.add(ship);
				}
			}
		}

		return results;
	}

	/**
	 * Update the schemaInfo object in the <code>ERTable</code>, and update the
	 * Attribute in all of the <code>ERTableColumn</code>
	 *
	 * @param schemaInfo
	 */
	public void updateSchemaInfoMode(SchemaInfo schemaInfo) {
		this.schemaInfo = schemaInfo;
		for (ERTableColumn column : columns) {
			DBAttribute newAttr = schemaInfo.getDBAttributeByName(column.getName(), false);
			if (newAttr == null) {
				LOGGER.warn("Cannot update the column model : " + column.getName());
				continue;
			}
			column.setAttr(newAttr);

		}
	}

	/**
	 *
	 * Get logic column data type list, if the physical data type has relation
	 * logic data type in the global configuration, return the logic data type;<br>
	 * If not, return the physical data type.
	 *
	 * @return List<String>
	 */
	public List<String> getLogicDataType() {
		List<ERTableColumn> columns = getColumns();
		List<String> colTypeList = new LinkedList<String>();

		for (ERTableColumn col : columns) {
			colTypeList.add(col.getRealType());
		}

		Map<String, String> typeMap = getERSchema().getPhysicalLogicRelation().getDataTypeMap();
		for (int i = 0; i < colTypeList.size(); i++) {
			String physicalType = colTypeList.get(i);
			if (typeMap.containsKey(physicalType)
					&& StringUtil.isNotEmpty(typeMap.get(physicalType))) {
				colTypeList.remove(i);
				colTypeList.add(i, typeMap.get(physicalType));
			}
		}

		return colTypeList;
	}

	/**
	 * Sets bounds without firing off any event notifications
	 *
	 * @param bounds The bounds to set.
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public List<ERTableColumn> getColumns() {
		return columns;
	}

	public void setERSchema(ERSchema erSchema) {
		this.erSchema = erSchema;
		if (schemaInfo != null && erSchema.getCubridDatabase() != null) {
			resetDatabase(erSchema.getCubridDatabase());
		}
	}

	public boolean isHasSelfFKRef() {
		return hasSelfFKRef;
	}

	public void setHasSelfFKRef(boolean hasSelfFKRef) {
		this.hasSelfFKRef = hasSelfFKRef;
	}

	public boolean isNeedPartitionLayout() {
		return needPartitionLayout;
	}

	public void setNeedPartitionLayout(boolean needPartitionLayout) {
		this.needPartitionLayout = needPartitionLayout;
	}

	/**
	 *
	 * Get logic table name. If the logical name hasnot been assigned, return
	 * the physical name.
	 *
	 * @return String logic table name
	 */
	public String getLogicName() {
		if (StringUtil.isEmpty(logicalName)) {
			return name;
		}
		return logicalName;
	}

	public ERSchema getERSchema() {
		return erSchema;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setName(String name) {
		String oldName = this.name;
		if (!name.equals(oldName)) {
			this.name = name;
			this.schemaInfo.setClassname(name);
		}
	}

	public void setName(String name, boolean isPhysical) {
		if (isPhysical) {
			this.setName(name);
		} else {
			setLogicalName(name);
		}
	}

	/**
	 * return description of a table.
	 *
	 * @return
	 */
	public String getDescription() {
		return schemaInfo.getDescription();
	}

	/**
	 * set a description of a table.
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		schemaInfo.setDescription(description);
	}

	public String getLogicalName() {
		return logicalName;
	}

	public void setLogicalName(String lname) {
		this.logicalName = lname;
	}

	public String toString() {
		return "ERTable [name=" + name + "]";
	}

	public ERTable clone() {
		ERTable table = null;
		try {
			table = (ERTable) super.clone();
			table.schemaInfo = schemaInfo.clone();

			ArrayList<ERTableColumn> copiedColumns = new ArrayList<ERTableColumn>();
			for (ERTableColumn column : columns) {
				ERTableColumn cloneColumn = column.clone();
				//must come from schemaInfo,that is the same ref object.
				cloneColumn.setAttr(table.schemaInfo.getDBAttributeByName(cloneColumn.getName(),
						false));
				copiedColumns.add(cloneColumn);
			}
			table.columns = copiedColumns;

			List<Relationship> copiedFkships = new ArrayList<Relationship>();
			for (Relationship ship : fkships) {
				copiedFkships.add(ship.clone());
			}
			table.fkships = copiedFkships;
		} catch (CloneNotSupportedException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return table;
	}

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((logicalName == null) ? 0 : logicalName.hashCode());
		result = prime * result + ((bounds == null) ? 0 : bounds.hashCode());
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((erSchema == null) ? 0 : erSchema.getName().hashCode());
		result = prime * result + ((fkships == null) ? 0 : fkships.hashCode());
		result = prime * result + ((schemaInfo == null) ? 0 : schemaInfo.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		ERTable other = (ERTable) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (logicalName == null) {
			if (other.logicalName != null) {
				return false;
			}
		} else if (!logicalName.equals(other.logicalName)) {
			return false;
		}

		if (columns == null) {
			if (other.columns != null) {
				return false;
			}
		} else if (!columns.equals(other.columns)) {
			return false;
		}
		if (erSchema == null) {
			if (other.erSchema != null) {
				return false;
			}
		} else if (!erSchema.equals(other.erSchema)) {
			return false;
		}
		if (fkships == null) {
			if (other.fkships != null) {
				return false;
			}
		} else if (!fkships.equals(other.fkships)) {
			return false;
		}
		if (schemaInfo == null) {
			if (other.schemaInfo != null) {
				return false;
			}
		} else if (!schemaInfo.equals(other.schemaInfo)) {
			return false;
		}

		return true;
	}
}