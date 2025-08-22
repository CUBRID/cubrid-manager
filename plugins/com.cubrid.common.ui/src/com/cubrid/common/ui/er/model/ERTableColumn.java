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

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.logic.PhysicalLogicRelation;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import org.slf4j.Logger;

/**
 * Column entry in model Table. The ER column handle showing data type. And a real data base type is
 * relation with the showing data type. See the relationship in {@link DataType}.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-8 created by Yu Guojia
 */
public class ERTableColumn extends PropertyChangeProvider {
    private static final long serialVersionUID = 6706199713032961283L;
    private static final Logger LOGGER = LogUtil.getLogger(ERTableColumn.class);
    public static final String SPLIT = ":";

    private boolean isPrimaryKey;
    private String logicalName;
    /** lower logical type for showing */
    private String logicalShowType;
    /** lower physical type for showing */
    private String physicalShowType;

    private transient DBAttribute attr;
    private transient ERTable table;

    public ERTableColumn() {
        super();
    }

    public ERTableColumn(ERTable table, DBAttribute dbAttr, boolean isPK) {
        super();
        this.table = table;
        buildERColumn(dbAttr, isPK);
    }

    protected void buildERColumn(DBAttribute dbAttr, boolean isPK) {
        this.name = dbAttr.getName();
        this.attr = dbAttr;
        this.isPrimaryKey = isPK;
        initShowType(dbAttr);
        initDefaultLogicalInfo(dbAttr);
    }

    public void buildERColumn(ERTableColumn column) {
        this.name = column.getName();
        this.attr = column.getAttr().clone();
        this.isPrimaryKey = column.isPrimaryKey();
        this.logicalName = column.getLogicalName();
        this.logicalShowType = column.getLogicalType();
        this.physicalShowType = column.getShowType();
    }

    private void initShowType(DBAttribute dbAttr) {
        String revisedType = DataType.reviseDataType(DataType.getShownType(dbAttr.getType()));
        if (!dbAttr.getType().toUpperCase().startsWith(DataType.DATATYPE_ENUM)) {
            dbAttr.setType(DataType.getRealType(revisedType));
            this.physicalShowType = DataType.getShownType(dbAttr.getType()).toLowerCase();
        } else {
            String enumerations = dbAttr.getEnumeration();
            if (StringUtil.isEmpty(enumerations)) {
                enumerations = "('" + DataType.ENUM_DAFAULT_VALUE + "')";
                dbAttr.setEnumeration(enumerations);
            }
            physicalShowType = DataType.getLowerEnumType() + enumerations;
        }
    }

    private void initDefaultLogicalInfo(DBAttribute dbAttr) {
        String desc = dbAttr.getDescription();
        if (StringUtil.isNotEmpty(desc)) {
            logicalName = desc;
        } else {
            logicalName = dbAttr.getName();
        }

        String physicalType = getRealType();
        logicalShowType = getERSchema().convert2LogicalShowType(physicalType);
    }

    @Override
    public CubridDatabase getCubridDatabase() {
        return table.getCubridDatabase();
    }

    public String getShowType() {
        return physicalShowType;
    }

    /**
     * Shorter CUBRID type should be shown
     *
     * @param realType String a string includes the info of type. Should be lower case
     * @return String the type name used for showing
     */
    public static String getShownType(String realType) {
        if (StringUtil.isEmpty(realType)) {
            return realType;
        }
        return DataType.getShownType(realType.toLowerCase());
    }

    /**
     * To change upper case and shorter shown type to CUBRID data type.
     *
     * @param String upper shown type.
     * @return String the lower case real database type.
     */
    public static String getRealType(String showType) {
        if (StringUtil.isEmpty(showType)) {
            return showType;
        }
        if (showType.toLowerCase().startsWith(DataType.getLowerEnumType())) {
            showType =
                    showType.replaceFirst(DataType.getLowerEnumType(), DataType.getUpperEnumType());
        } else {
            showType = showType.toUpperCase();
        }
        return DataType.getRealType(showType);
    }

    /**
     * Set real type and show physical type
     *
     * @param upPhysicalShowType upper case physical show type
     * @return void
     */
    public void setPhysicalDataType(String upPhysicalShowType) {
        String realType = DataType.getRealType(upPhysicalShowType);

        if (!realType.toUpperCase().startsWith(DataType.getUpperEnumType())) {
            attr.setType(realType);
            physicalShowType = DataType.getShownType(realType).toLowerCase();
        } else {
            String enumerations = realType.replaceFirst(DataType.getUpperEnumType(), "");
            attr.setType(DataType.getUpperEnumType());
            attr.setEnumeration(enumerations);
            physicalShowType = DataType.getLowerEnumType() + enumerations;
        }
    }

    public void setType(DBAttribute dbAttr) {
        String revisedType = DataType.reviseDataType(dbAttr.getType());
        if (!dbAttr.getType().toUpperCase().startsWith(DataType.DATATYPE_ENUM)) {
            attr.setType(revisedType);
            physicalShowType = DataType.getShownType(dbAttr.getType().toLowerCase()).toLowerCase();
        } else {
            String enumerations = dbAttr.getEnumeration();
            if (StringUtil.isEmpty(enumerations)) {
                enumerations = "('" + DataType.ENUM_DAFAULT_VALUE + "')";
                dbAttr.setEnumeration(enumerations);
            }
            physicalShowType = DataType.getLowerEnumType() + enumerations;
        }
    }

    /**
     * Set the ER table column physical data type and {@code DBAttribute} type.
     *
     * @param showType The type to set.
     */
    private void setShowType(String showType) {
        if (this.physicalShowType.equals(showType)) {
            return;
        }
        this.physicalShowType = showType;
        String realType;
        if (DataType.isValidEnumShowType(showType)) {
            realType = DataType.getUpperEnumType();
            String enumaretion = DataType.getEnumeration(showType);
            attr.setEnumeration(enumaretion);
        } else {
            realType = getRealType(showType);
        }
        this.attr.setType(realType);
    }

    /**
     * Set the ER table column type and {@code DBAttribute} type. And fire
     *
     * @param showType The type to set.
     * @param isPhysical to set physical or logical shown type.
     */
    public void setShowTypeAndFire(String showType, boolean isPhysical) {
        if (isPhysical) {
            if (this.physicalShowType.equals(showType)) {
                return;
            }
        } else {
            if (StringUtil.isEqual(this.logicalShowType, showType)) {
                return;
            }
        }
        setShowType(showType, isPhysical);
        firePropertyChange(TEXT_CHANGE, null, this);
    }

    /**
     * If now the model view is logical, then physical data type should be updated to new type by
     * the logical type and map. <br>
     * If now the model view is physical, then logical data type should be updated to new type by
     * the physical type and map.
     *
     * @param relation
     * @return void
     */
    public void refreshDataTypeByMap(PhysicalLogicRelation relation) {
        boolean isPhysical = getERSchema().isPhysicModel();
        if (isPhysical) {
            String physicalRealType = getRealType();
            if (relation.hasPhysicalTypeInMap(physicalRealType)) {
                logicalShowType = getERSchema().convert2LogicalShowType(physicalRealType);
            }
        } else {
            if (relation.hasLogicalTypeInMap(logicalShowType)) {
                String physicalRealType = getERSchema().convert2UpPhysicalShowType(logicalShowType);
                setPhysicalDataType(physicalRealType);
            }
        }
    }

    /**
     * Change the er table column name and Fire. If the column is in pk. If is to modify physical
     * name and the constraint in {@link SchemaInfo} should be changed too.
     *
     * @param newName The name to set.
     * @param isPhysical
     */
    public void modifyNameAndFire(String newName, boolean isPhysical) {
        if (isPhysical) {
            setName(newName);
        } else {
            if (StringUtil.isEqual(logicalName, newName)) {
                return;
            }
            this.logicalName = newName;
        }
        firePropertyChange(TEXT_CHANGE, null, newName);
    }

    /**
     * Get the column name and data type. If the model is logic return logic name and data type.
     *
     * @return String
     */
    public String getLabelText() {
        if (getERSchema().isPhysicModel()) {
            return getPhysicalLabelText();
        } else {
            return getLogicLabelText();
        }
    }

    private String getPhysicalLabelText() {
        String suffix = physicalShowType;
        if (!DataType.isValidEnumShowType(physicalShowType)) {
            suffix = suffix.toLowerCase();
        }
        String labelText = name + SPLIT + suffix;
        return labelText;
    }

    private String getLogicLabelText() {
        String logicName = getShownLogicName();
        String physicalType = DataType.getShownType(attr.getType());
        String enumerations = attr.getEnumeration();
        if (physicalType.equalsIgnoreCase(DataType.DATATYPE_ENUM)
                && StringUtil.isNotEmpty(enumerations)) {
            physicalType += enumerations;
        }
        String suffix = getLogicalType();
        String labelText = logicName + SPLIT + suffix;
        return labelText;
    }

    public String getRealType() {
        String revisedType = DataType.reviseDataType(attr.getType());
        if (attr.getType().toUpperCase().startsWith(DataType.DATATYPE_ENUM)) {
            revisedType += attr.getEnumeration();
        }

        return revisedType;
    }

    /**
     * Get column name by label text
     *
     * @param labelText
     * @return
     */
    public static String getName(String labelText) {
        String[] colInfo = labelText.split(ERTableColumn.SPLIT);
        return colInfo[0];
    }

    /**
     * Get column type by label text
     *
     * @param labelText
     * @return
     */
    public static String getType(String labelText) {
        String[] colInfo = labelText.split(ERTableColumn.SPLIT);
        return colInfo[1];
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public void setIsPrimaryKeyAndFire(boolean isPrimaryKey) {
        if (this.isPrimaryKey != isPrimaryKey) {
            this.isPrimaryKey = isPrimaryKey;
            firePropertyChange(TEXT_CHANGE, null, this);
        }
    }

    public boolean isNullable() {
        return attr.isNotNull();
    }

    public void setNullable(boolean nullable) {
        attr.setNotNull(nullable);
    }

    public boolean isUnique() {
        return attr.isUnique();
    }

    public void setUnique(boolean unique) {
        attr.setUnique(unique);
    }

    public boolean isNew() {
        return attr.isNew();
    }

    public void setIsNew(boolean isNew) {
        attr.setNew(isNew);
    }

    public String getDefaultValue() {
        return attr.getDefault();
    }

    public void setDefaultValue(String defaultValue) {
        attr.setDefault(defaultValue);
    }

    public String getDescription() {
        return attr.getDescription();
    }

    public void setDescription(String description) {
        attr.setDescription(description);
    }

    @Override
    public ERSchema getERSchema() {
        return table.getERSchema();
    }

    @Override
    public void setERSchema(ERSchema erSchema) {
        table.setERSchema(erSchema);
    }

    public DBAttribute getAttr() {
        return attr;
    }

    public void setAttr(DBAttribute attr) {
        this.attr = attr;
    }

    public ERTable getTable() {
        return table;
    }

    public void setTable(ERTable table) {
        this.table = table;
    }

    public static String checkName(String name) {
        if (!ValidateUtil.isValidIdentifier(name)) {
            return Messages.bind(Messages.errInvalidName, name);
        }
        return null;
    }

    /**
     * If the the show type on the ERD is not a right format type, return error message.
     *
     * @param showType
     * @return
     */
    public static String checkDataShowType(String showType) {
        if (StringUtil.isEmpty(showType)) {
            return Messages.errColumnDataType;
        }

        if (showType.startsWith(DataType.getLowerEnumType())) { // "enum" type
            if (DataType.isValidEnumShowType(showType)) {
                return null;
            } else {
                return Messages.errColumnDataType;
            }
        }

        return checkRealDataType(getRealType(showType));
    }

    /**
     * If the the real type in database is not a right format type, return error message.
     *
     * @param realType
     * @return
     */
    public static String checkRealDataType(String realType) {
        if (StringUtil.isEmpty(realType)) {
            return Messages.errColumnDataType;
        }
        if (!DataType.isBasicType(realType)) {
            return Messages.errColumnDataType;
        }
        if (DataType.isStringType(realType)) {
            int start = realType.indexOf("(");
            int end = realType.indexOf(")");
            if (start < 0) {
                return null;
            }
            return checkVariableLenth(realType, start + 1, end, 1, DataType.STRING_MAX_SIZE);
        }
        if (DataType.isBitDataType(realType) || DataType.isBitVaryingDataType(realType)) {
            int start = realType.indexOf("(");
            int end = realType.indexOf(")");
            return checkVariableLenth(realType, start + 1, end, 1, DataType.STRING_MAX_SIZE);
        }
        if (DataType.isSetDataType(realType)) {
            int start = realType.indexOf("(");
            int end = realType.indexOf(")");
            if (start < 0) {
                return null;
            }
            if (end < start || realType.length() != end + 1) { // do not end with
                // ")"
                return Messages.errColumnDataType;
            }
            String subType = realType.substring(start + 1, end);
            return checkRealDataType(subType);
        }
        if (DataType.isFloatType(realType)) {
            int start = realType.indexOf("(");
            int end = realType.indexOf(")");
            if (start < 0) {
                return null;
            }
            return checkVariableLenth(realType, start + 1, end, 1, DataType.FLOAT_MAX_PRECISION);
        }
        if (DataType.isNumericType(realType)) {
            int start = realType.indexOf("(");
            int end = realType.indexOf(")");
            if (start < 0) {
                return null;
            }
            return checkNumeric(realType, start + 1, end);
        }

        // following data type should not contains size
        if (realType.contains("(") || realType.contains(")")) {
            return Messages.errColumnDataType;
        }
        return null;
    }

    private static String checkNumeric(String type, int start, int end) {
        if (start < 0) {
            return null;
        }
        if (end < start || type.length() != end + 1) { // do not end with ")"
            return Messages.errColumnDataType;
        }
        String preScaleStr = type.substring(start, end);
        if (preScaleStr.contains(",")) {
            String[] reScale = preScaleStr.split(",");
            if (reScale.length != 2) {
                return Messages.errColumnDataType;
            }
            try {
                int precision = Integer.valueOf(reScale[0]);
                int scale = Integer.valueOf(reScale[1]);
                if (precision > DataType.NUMERIC_MAX_PRECISION
                        || scale > precision
                        || precision < 1
                        || scale < 0) {
                    return Messages.errColumnDataType;
                }
            } catch (Exception e) {
                return Messages.errColumnDataType;
            }
        } else {
            int precision = Integer.valueOf(preScaleStr);
            if (precision > DataType.NUMERIC_MAX_PRECISION || precision < 1) {
                return Messages.errColumnDataType;
            }
        }

        return null;
    }

    /**
     * The type is struct with "XXX(n)". The size constraint is not must.
     *
     * @param type
     * @param start
     * @param end
     * @param lenthMin
     * @param lenthMax
     * @return
     */
    private static String checkVariableLenth(
            String type, int start, int end, int lenthMin, int lenthMax) {
        if (start < 0) {
            return null;
        }
        if (end < start || type.length() != end + 1) { // do not end with ")"
            return Messages.errColumnDataType;
        }
        String sizeStr = type.substring(start, end);
        try {
            int size = Integer.valueOf(sizeStr);
            if (size < lenthMin || size > lenthMax) {
                return Messages.errColumnDataType;
            }
        } catch (Exception e) {
            return Messages.errColumnDataType;
        }

        return null;
    }

    public void setName(String name, boolean isPhysical) {
        if (isPhysical) {
            this.setName(name);
        } else {
            this.setLogicalName(name);
        }
    }

    public void setName(String newName) {
        if (StringUtil.isEqual(name, newName)) {
            return;
        }
        attr.setName(newName);
        SchemaInfo schemaInfo = this.getTable().getSchemaInfo();
        Constraint pk = schemaInfo.getPK();
        if (pk != null && pk.contains(name, false)) {
            pk.replaceAttribute(name, newName);
        }
        schemaInfo.updateAttrNameInIndex(name, newName);
        name = newName;
    }

    /**
     * @param isPhysical Get physical or logical name
     * @return String
     */
    public String getName(boolean isPhysical) {
        if (isPhysical) {
            return this.getName();
        } else {
            return this.getLogicalName();
        }
    }

    /**
     * Get logic column name. If the logical name hasnot been assigned, return the physical name.
     *
     * @return String logic table name
     */
    public String getShownLogicName() {
        if (StringUtil.isEmpty(logicalName)) {
            return name;
        }
        return logicalName;
    }

    public void setShowType(String type, boolean isPhysical) {
        if (isPhysical) {
            this.setShowType(type);
        } else {
            this.setLogicalType(type);
        }
    }

    /**
     * Get lower showing physical type or lower showing logical type.
     *
     * @param isPhysical
     * @return String
     */
    public String getShowType(boolean isPhysical) {
        if (isPhysical) {
            return this.getShowType();
        } else {
            return this.getLogicalType();
        }
    }

    public String getLogicalName() {
        return logicalName;
    }

    public void setLogicalName(String lname) {
        this.logicalName = lname;
    }

    public String getLogicalType() {
        return logicalShowType;
    }

    public void setLogicalType(String lshowType) {
        if (lshowType.startsWith(DataType.DATATYPE_ENUM)) {
            lshowType =
                    lshowType.replaceFirst(
                            DataType.DATATYPE_ENUM, DataType.DATATYPE_ENUM.toLowerCase());
        } else {
            lshowType = lshowType.toLowerCase();
        }
        this.logicalShowType = lshowType;
    }

    public ERTableColumn clone() {
        ERTableColumn column = null;
        try {
            column = (ERTableColumn) super.clone();
            column.setAttr(attr.clone());
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return column;
    }

    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((attr == null) ? 0 : attr.hashCode());
        result =
                prime * result
                        + (isPrimaryKey
                                ? 1231
                                : 1237); // prime number that is difficult to be conflict with other
                                         // number.
        result = prime * result + ((physicalShowType == null) ? 0 : physicalShowType.hashCode());
        result = prime * result + ((logicalShowType == null) ? 0 : logicalShowType.hashCode());
        result = prime * result + ((table == null) ? 0 : table.getName().hashCode());
        result = prime * result + ((logicalName == null) ? 0 : logicalName.hashCode());
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

        ERTableColumn other = (ERTableColumn) obj;
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

        if (attr == null) {
            if (other.attr != null) {
                return false;
            }
        } else if (!attr.equals(other.attr)) {
            return false;
        }
        if (physicalShowType == null) {
            if (other.physicalShowType != null) {
                return false;
            }
        } else if (!physicalShowType.equals(other.physicalShowType)) {
            return false;
        }
        if (logicalShowType == null) {
            if (other.logicalShowType != null) {
                return false;
            }
        } else if (!logicalShowType.equals(other.logicalShowType)) {
            return false;
        }
        if (isPrimaryKey ^ other.isPrimaryKey) {
            return false;
        }
        if (table == null) {
            if (other.table != null) {
                return false;
            }
        } else if (!table.getName().equals(other.table.getName())) {
            return false;
        }

        return true;
    }

    public String toString() {
        return "ERTableColumn [isPrimaryKey="
                + isPrimaryKey
                + ", name="
                + name
                + ", lname="
                + logicalName
                + ", showType="
                + physicalShowType
                + ", nullable="
                + attr.isNotNull()
                + ", defaultValue="
                + attr.getDefault()
                + "]";
    }
}
