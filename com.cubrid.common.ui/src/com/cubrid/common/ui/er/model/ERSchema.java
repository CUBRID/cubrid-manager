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

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.er.SchemaEditorInput;
import com.cubrid.common.ui.er.logic.PhysicalLogicRelation;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.loader.schema.CubridTablesFolderLoader;
import com.cubrid.cubridmanager.core.cubrid.database.model.Collation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.draw2d.geometry.Point;
import org.slf4j.Logger;

/**
 * Represents a Schema in the model. Note that this class also includes diagram specific information
 * (layoutManualDesired and layoutManualAllowed fields) although ideally these should be in a
 * separate model hiearchy
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-8 created by Yu Guojia
 */
public class ERSchema extends PropertyChangeProvider {
    private static final long serialVersionUID = -8636039602911861974L;
    private static final Logger LOGGER = LogUtil.getLogger(ERSchema.class);

    private transient SchemaEditorInput input;
    private List<ERTable> tablesList = new ArrayList<ERTable>();
    private PhysicalLogicRelation physicalLogicalRelation;

    private transient List<Collation> collections;
    private transient boolean layoutManualDesired = true;
    private transient boolean layoutManualAllowed = true;
    private transient boolean isPhysicModel = true;

    public ERSchema(String name, SchemaEditorInput input) {
        super();
        this.name = name;
        this.input = input;

        physicalLogicalRelation = new PhysicalLogicRelation();
        physicalLogicalRelation.buildDefault();
    }

    public SchemaEditorInput getInput() {
        return input;
    }

    public void setInput(SchemaEditorInput input) {
        this.input = input;
    }

    @Override
    public CubridDatabase getCubridDatabase() {
        return input.getDatabase();
    }

    /**
     * Whether the er schema contains this name full information table
     *
     * @param tableName
     * @param isPhysical
     * @return
     */
    public boolean isContainsTable(String tableName, boolean isPhysical) {
        for (ERTable table : tablesList) {
            if (table.getName(isPhysical).equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    public SchemaInfo getSchemaInfo(String tableName) {
        ERTable table = getTable(tableName);
        if (table == null) {
            return null;
        }
        return table.getSchemaInfo();
    }

    public Map<String, SchemaInfo> getAllSchemaInfo() {
        Map<String, SchemaInfo> allTableInfos =
                new HashMap<String, SchemaInfo>(2 * tablesList.size());
        for (ERTable table : tablesList) {
            allTableInfos.put(table.getName(), table.getSchemaInfo());
        }
        return allTableInfos;
    }

    /**
     * Include added tables and loading tables
     *
     * @return
     */
    public Set<String> getAllTableNames() {
        Set<String> result = new HashSet<String>();
        for (ERTable table : tablesList) {
            result.add(table.getName());
        }
        return result;
    }

    /**
     * Include added tables names on ERD that are shown on editor view
     *
     * @return
     */
    public Set<String> getAllShownTableNames() {
        if (isPhysicModel()) {
            return getAllTableNames();
        }
        Set<String> result = new HashSet<String>();
        for (ERTable table : tablesList) {
            result.add(table.getLogicName());
        }
        return result;
    }

    /**
     * Add a batch tables to the ERSchema.
     *
     * @param Collection <ERTable> erTables
     */
    public void addTables(Collection<ERTable> erTables) {
        for (ERTable table : erTables) {
            addTable(table);
        }
    }

    /**
     * Add a new table to the ERSchema. Donot build foreigner relationships in this method. If the
     * table has existed, update the new to the cache.
     *
     * @param ERTable erTable
     */
    public boolean addTable(ERTable erTable) {
        int index = -1;
        if (getTable(erTable.getName()) != null) {
            for (int i = 0; i < tablesList.size(); i++) {
                if (tablesList.get(i).getName().equals(erTable.getName())) {
                    index = i;
                    break;
                }
            }
        }
        if (index > -1) {
            tablesList.remove(index);
            tablesList.add(index, erTable);
        } else {
            tablesList.add(erTable);
        }

        return true;
    }

    /**
     * A group tables added, then fire listener
     *
     * @param Collection <ERTable>
     */
    public void AddTablesAndFire(Collection<ERTable> erTables) {
        addTables(erTables);
        FireAddedTable(erTables);
    }

    /**
     * A group tables added to the ERSchema, fire listener.
     *
     * @param Collection <ERTable> erTables
     */
    public void FireAddedTable(Collection<ERTable> erTables) {
        for (ERTable table : erTables) {
            FireAddedTable(table);
        }
    }

    /**
     * The table has been added, fire listener.
     *
     * @param erTable
     */
    public void FireAddedTable(ERTable erTable) {
        firePropertyChange(CHILD_CHANGE, null, erTable);
    }

    /**
     * Add a new table to the ERSchema. Donot build foreigner relationships in this method.
     *
     * @param ERTable erTable
     */
    public void addTableAndFire(ERTable erTable) {
        if (getTable(erTable.getName()) != null) {
            return;
        }
        tablesList.add(erTable);
        firePropertyChange(CHILD_CHANGE, null, erTable);
    }

    /**
     * Delete the table, and its relations ships, and fire listener
     *
     * @param erTable
     */
    public void deleteTableAndFire(ERTable erTable) {
        if (getTable(erTable.getName()) == null) {
            return;
        }
        erTable.deleteAllFKShipsAndFire();
        erTable.deleteAllTargetedShipsAndFire();
        tablesList.remove(erTable);
        firePropertyChange(CHILD_CHANGE, erTable, null);
    }

    /**
     * Rename table name and fire
     *
     * @param oldName
     * @param newName
     * @param isPhysical
     * @return void
     */
    public void modifyTableNameAndFire(String oldName, String newName, boolean isPhysical) {
        ERTable table = getTable(oldName, isPhysical);
        if (table == null) {
            return;
        }

        table.modifyNameAndFire(newName, isPhysical);
    }

    public void deleteAllTableAndFire() {
        for (ERTable erTable : tablesList) {
            firePropertyChange(CHILD_CHANGE, erTable, null);
        }
        tablesList.clear();
    }

    public void clearCache() {
        tablesList.clear();
    }

    /**
     * Returns an individual physical named table
     *
     * @param name String
     * @return ERTable
     */
    public ERTable getTable(String name) {
        for (ERTable table : tablesList) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        return null;
    }

    public ERTable getTable(String name, boolean isPhysical) {
        if (isPhysical) {
            return getTable(name);
        } else {
            for (ERTable table : tablesList) {
                if (table.getLogicName().equals(name)) {
                    return table;
                }
            }
        }
        return null;
    }

    /**
     * Get all tables that references the pointed table
     *
     * @param targetTable be pointed table. Target table name.
     * @return all tables points the tableName. Source tables
     */
    public Set<String> getSourceTables(String targetTable) {
        Set<String> sourceTables = new HashSet<String>();
        for (ERTable table : tablesList) {
            List<Relationship> ships = table.getForeignKeyRelationships();
            for (Relationship ship : ships) {
                if (targetTable.equals(ship.getPrimaryKeyTable().getName())) {
                    sourceTables.add(table.getName());
                    break;
                }
            }
        }

        return sourceTables;
    }

    public List<ERTable> getTables() {
        return tablesList;
    }

    /**
     * Get the table that is arranged at the down right corner on the erd canvas.
     *
     * @return if there is no table, return null;
     */
    public ERTable getBottomRightTable() {
        ERTable result = null;
        int maxY = 0;
        int maxX = 0;
        for (ERTable table : tablesList) {
            Point bottomRightPoint = table.getBounds().getBottomRight();
            if (bottomRightPoint.y > maxY) {
                result = table;
            } else if (bottomRightPoint.y == maxY && bottomRightPoint.x > maxX) {
                result = table;
            }
        }

        return result;
    }

    public void setLayoutManualAllowed(boolean layoutManualAllowed) {
        this.layoutManualAllowed = layoutManualAllowed;
    }

    public boolean isLayoutManualDesired() {
        return layoutManualDesired;
    }

    /** Trigger the action of auto layout for all tables */
    public void TriggerAutoLayout() {
        firePropertyChange(LAYOUT_CHANGE, null, new Boolean(true));
    }

    public void setLayoutManualDesiredAndFire(boolean layoutManualDesired) {
        this.layoutManualDesired = layoutManualDesired;
        firePropertyChange(LAYOUT_CHANGE, null, new Boolean(layoutManualDesired));
    }

    public void setTmpAutoLayoutAndFire() {
        firePropertyChange(AUTO_LAYOUT_TEMP, null, new Boolean(true));
    }

    /** @return Returns whether we can lay out individual tables manually using the XYLayout */
    public boolean isLayoutManualAllowed() {
        return layoutManualAllowed;
    }

    @Override
    public ERSchema getERSchema() {
        return this;
    }

    public List<Collation> getCollections() {
        return collections;
    }

    public void setCollections(List<Collation> collections) {
        this.collections = collections;
    }

    public ICubridNode getTableTreeNode(String tableName) {
        ICubridNode node =
                this.getCubridDatabase()
                        .getChild(
                                getCubridDatabase().getId()
                                        + ICubridNodeLoader.NODE_SEPARATOR
                                        + CubridTablesFolderLoader.TABLES_FOLDER_ID);
        if (node == null || !node.getLoader().isLoaded()) {
            return null;
        }

        List<ICubridNode> tableNodes = node.getChildren();
        for (ICubridNode tableNode : tableNodes) {
            if (tableNode == null) {
                continue;
            }
            if (tableNode.getName().equals(tableName)) {
                return tableNode;
            }
        }

        return null;
    }

    @Override
    public void setERSchema(ERSchema es) {}

    /** The model view is changed from logical to physical, or from physical to logical. */
    public void FireModelViewChanged() {
        firePropertyChange(
                VIEW_MODEL_CHANGE,
                isPhysicModel ? LOGIC_MODEL : PHYSICAL_MODEL,
                isPhysicModel ? PHYSICAL_MODEL : LOGIC_MODEL);
    }

    /**
     * When the relation map between physical and logical is changed, refresh the table figures.
     * Only for logical model shown
     */
    public void FireModelRelationChanged() {
        firePropertyChange(RELATION_MAP_CHANGE, null, null);
    }

    public boolean isPhysicModel() {
        return isPhysicModel;
    }

    public void setPhysicModel(boolean isPhysicModel) {
        this.isPhysicModel = isPhysicModel;
    }

    public boolean hasPhysicalTypeInMap(String physicalRealType) {
        return physicalLogicalRelation.hasPhysicalTypeInMap(physicalRealType);
    }

    public boolean hasLogicalTypeInMap(String logicalType) {
        return physicalLogicalRelation.hasLogicalTypeInMap(logicalType);
    }

    /**
     * Convert full physical data type to lower case logical type.
     *
     * @param physicalRealType
     * @return String If there is no the physical in data type map, return the copy of physical.
     */
    public String convert2LogicalShowType(String physicalRealType) {
        return physicalLogicalRelation.convert2LogicalShowType(physicalRealType);
    }

    /**
     * Convert full logical data type to upper case physical show type.
     *
     * @param logicalType
     * @return String if there is no the relation of physical/logical type, return "CHAR(1)".
     */
    public String convert2UpPhysicalShowType(String logicalType) {
        return physicalLogicalRelation.convert2UpPhysicalShowType(logicalType);
    }

    /**
     * Get default upper case physical show type.
     *
     * @return String
     */
    public String getDefaultPhysicalType() {
        return PhysicalLogicRelation.DEFAULT_UP_PHYSICAL_SHOW_TYPE;
    }

    public PhysicalLogicRelation getPhysicalLogicRelation() {
        return physicalLogicalRelation;
    }

    /**
     * Set the physical/logical data type map and refresh physical or logical data type by the map
     *
     * @param physicalLogicalRelation
     * @return void
     */
    public void setPhysicalLogicRelation(PhysicalLogicRelation physicalLogicalRelation) {
        this.physicalLogicalRelation = physicalLogicalRelation;
        for (ERTable table : tablesList) {
            List<ERTableColumn> columns = table.getColumns();
            for (ERTableColumn column : columns) {
                column.refreshDataTypeByMap(physicalLogicalRelation);
            }
        }
    }

    public ERSchema clone() {
        ERSchema schema = null;
        try {
            schema = (ERSchema) super.clone();
            schema.physicalLogicalRelation = physicalLogicalRelation.clone();

            List<ERTable> copiedTables = new ArrayList<ERTable>();
            for (ERTable table : tablesList) {
                copiedTables.add(table.clone());
            }
            schema.tablesList = copiedTables;

            List<Collation> copiedCollections = new LinkedList<Collation>();
            for (Collation collation : collections) {
                copiedCollections.add(collation.clone());
            }
            schema.collections = copiedCollections;
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return schema;
    }
}
