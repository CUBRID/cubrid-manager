/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.logic;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.model.ERTableColumn;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The relation configuration model for the physical and logic data type in ER table
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-5-8 created by Yu Guojia
 */
public class PhysicalLogicRelation implements Cloneable {

    public static String DEFAULT_UP_PHYSICAL_SHOW_TYPE = "CHAR(1)";
    Map<String, String> dataTypeMap; // physical<-->logic

    /** Constructor */
    public PhysicalLogicRelation() {
        init();
    }

    public static enum MapType {
        DATATYPE
    }

    public Map<String, String> getMapData(MapType mapType) {
        if (mapType.equals(MapType.DATATYPE)) {
            if (dataTypeMap == null) {
                dataTypeMap = new LinkedHashMap<String, String>();
            }
            return dataTypeMap;
        }

        return null;
    }

    public PhysicalLogicRelation buildDefault() {
        init();
        buildDefaultDatatypeMap();
        return this;
    }

    private void init() {
        if (dataTypeMap == null) {
            dataTypeMap = new LinkedHashMap<String, String>();
        }
    }

    private void buildDefaultDatatypeMap() {
        // default data type map is empty!
        // The element are as : "CHAR(1)"<->"STRING" or "INTEGER"<->"NUMBER"
    }

    /**
     * Convert to upper case physical type, except the value in "ENUM"
     *
     * @param physicalRealType
     * @return String
     */
    public static String getUpperPhysicalShowType(String physicalRealType) {
        String revisedPhysicalShowType =
                DataType.reviseDataType(DataType.getShownType(physicalRealType));
        if (revisedPhysicalShowType.startsWith(DataType.getLowerEnumType())) {
            revisedPhysicalShowType =
                    revisedPhysicalShowType.replaceFirst(
                            DataType.getLowerEnumType(), DataType.getUpperEnumType());
        }
        return revisedPhysicalShowType;
    }

    /**
     * Convert to upper case logicaltype, except the value in "ENUM"
     *
     * @param logicalType
     * @return String
     */
    public static String getUpperLogicalType(String logicalType) {
        if (logicalType.startsWith(DataType.getLowerEnumType())) {
            logicalType =
                    logicalType.replaceFirst(
                            DataType.getLowerEnumType(), DataType.getUpperEnumType());
        } else {
            logicalType = logicalType.toUpperCase();
        }
        return logicalType;
    }

    public boolean hasPhysicalTypeInMap(String physicalRealType) {
        String physicalUpperType = getUpperPhysicalShowType(physicalRealType);
        String logical = getLogicDataTypeByPhysical(physicalUpperType);
        if (StringUtil.isEmpty(logical)) {
            return false;
        }
        return true;
    }

    public boolean hasLogicalTypeInMap(String logicalType) {
        logicalType = getUpperLogicalType(logicalType);
        String physicalType = getPhysicalDataTypeByLogic(logicalType);
        if (StringUtil.isEmpty(physicalType)) {
            return false;
        }
        return true;
    }

    /**
     * Convert full physical data type to lower case logical type.
     *
     * @param physicalRealType
     * @return String If there is no the physical in data type map, return the show physical type of
     *     the physical.
     */
    public String convert2LogicalShowType(String physicalRealType) {
        String physicalUpperType = getUpperPhysicalShowType(physicalRealType);
        String logical = getLogicDataTypeByPhysical(physicalUpperType);
        if (StringUtil.isEmpty(logical)) {
            logical = physicalUpperType;
        }
        if (logical.startsWith(DataType.getUpperEnumType())) {
            return logical.replaceFirst(DataType.getUpperEnumType(), DataType.getLowerEnumType());
        } else {
            return logical.toLowerCase();
        }
    }

    /**
     * Convert full logical data type to upper case physical show type. (1)If there is the relation
     * of physical/logical type,return the mapped physical type by the logical type; (2)If the type
     * is a valid real physical type, return the upper physical type; (3)If the type isnot a valid
     * real physical type and there is no the relation of physical/logical type,return "CHAR(1)".
     *
     * @param logicalType
     * @return String
     */
    public String convert2UpPhysicalShowType(String logicalType) {
        String upperLogicalType = getUpperLogicalType(logicalType);
        // (1)
        String physicalType = getPhysicalDataTypeByLogic(upperLogicalType);
        if (StringUtil.isNotEmpty(physicalType)) {
            return physicalType;
        }
        // (2)
        String lowerRealType = DataType.getRealType(upperLogicalType);
        if (lowerRealType.startsWith(DataType.getLowerEnumType())) { // "enum" type
            if (DataType.isValidEnumShowType(lowerRealType)) {
                return lowerRealType.replaceFirst(
                        DataType.getLowerEnumType(), DataType.getUpperEnumType());
            }
        }
        String err = ERTableColumn.checkRealDataType(lowerRealType);
        if (StringUtil.isEmpty(err)) {
            return DataType.getShownType(lowerRealType);
        }
        // (3)
        return DEFAULT_UP_PHYSICAL_SHOW_TYPE;
    }

    /**
     * Get converted type that the type shown on ER table figure from physical type
     *
     * @param physicalType physical type in data type map.
     * @return String
     */
    public static String getShownDataTypeInERTable(String physicalType) {
        String revisedType = DataType.reviseDataType(physicalType);
        String showType = DataType.getShownType(revisedType.toLowerCase()).toLowerCase();
        return showType;
    }

    private String getKeyByValue(Map<String, String> map, String value) {
        if (map != null && value != null) {
            Set<String> set = map.keySet();
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (value.equals(map.get(key))) {
                    return key;
                }
            }
        }
        return null;
    }

    /**
     * @param key upper case physical show type
     * @return String logic data type
     */
    public String getLogicDataTypeByPhysical(String key) {
        if (dataTypeMap != null && key != null) {
            return dataTypeMap.get(key);
        }
        return null;
    }

    /**
     * @param key upper case logic data type
     * @return upper case physical show type
     */
    public String getPhysicalDataTypeByLogic(String value) {
        return getKeyByValue(dataTypeMap, value);
    }

    public void addDataType(String physicalType, String logicType) {
        if (dataTypeMap == null) {
            dataTypeMap = new LinkedHashMap<String, String>(10);
        }
        if (dataTypeMap.containsValue(logicType)) {
            throw new IllegalStateException(Messages.errMultiRelationship);
        }
        dataTypeMap.put(physicalType, logicType);
    }

    public void deleteDataType(String physicalType) {
        if (dataTypeMap != null) {
            dataTypeMap.remove(physicalType);
        }
    }

    public Map<String, String> getDataTypeMap() {
        return dataTypeMap;
    }

    public void setDataTypeMap(Map<String, String> dataTypeMap) {
        this.dataTypeMap = dataTypeMap;
    }

    /**
     * If the key or value is "" or null in the data type, delete the entry.
     *
     * @return void
     */
    public void delEmptyEntry() {
        delEmptyEntry(dataTypeMap);
    }

    /**
     * If the key or value is "" or null in the map, delete the entry.
     *
     * @param map
     * @return void
     */
    public static void delEmptyEntry(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (StringUtil.isEmpty(key) || StringUtil.isEmpty(map.get(key))) {
                it.remove();
                map.remove(key);
            }
        }
    }

    public PhysicalLogicRelation clone() {
        PhysicalLogicRelation r;
        try {
            r = (PhysicalLogicRelation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone exception.");
        }
        if (this.dataTypeMap == null) {
            r.dataTypeMap = new LinkedHashMap<String, String>();
        } else {
            r.dataTypeMap = new LinkedHashMap<String, String>(this.dataTypeMap);
        }
        return r;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataTypeMap == null) ? 0 : dataTypeMap.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        PhysicalLogicRelation other = (PhysicalLogicRelation) obj;
        if (dataTypeMap == null) {
            if (other.dataTypeMap != null) {
                return false;
            }
        } else if (!dataTypeMap.equals(other.dataTypeMap)) {
            return false;
        }
        return true;
    }
}
