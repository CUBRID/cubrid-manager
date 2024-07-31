/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;

/**
 * to provide methods when a schema adds or removes super classes
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-4 created by moulinwang
 */
public class SuperClassUtil {
    // Constructor
    private SuperClassUtil() {
        // empty
    }

    private static final Logger LOGGER = LogUtil.getLogger(SuperClassUtil.class);

    /**
     * check whether a resolution exists in a list
     *
     * @param resolutions List<DBResolution> the given list that contains the instances of
     *     DBResolution
     * @param column String the given column name
     * @param table String the given table name
     * @return boolean true if the argument resolutions contains the specific items
     */
    public static boolean isInResolutions(
            List<DBResolution> resolutions, String column, String table) {
        for (DBResolution r : resolutions) {
            if (r.getName().equals(column) && r.getTableName().equals(table)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get a resolution from a list
     *
     * @param resolutions List<DBResolution> the given list that contains the instances of
     *     DBResolution
     * @param column String the given column name
     * @param table String the given table name
     * @return DBResolution the reference of the specific column and table
     */
    public static DBResolution getResolution(
            List<DBResolution> resolutions, String column, String table) {
        for (DBResolution r : resolutions) {
            if (r.getName().equals(column) && r.getTableName().equals(table)) {
                return r;
            }
        }
        return null;
    }

    /**
     * get next resolution from a list,given removed resolution is not an alias resolution
     *
     * @param resolutions List<DBResolution> the given list that contains the instances of
     *     DBResolution
     * @param removedResolution DBResolution the given reference of DBResolution object
     * @param conflicts List<String[]> the given list that contains the conflicts items
     * @return DBResolution the next reference of DBResolution
     */
    public static DBResolution getNextResolution(
            List<DBResolution> resolutions,
            DBResolution removedResolution,
            List<String[]> conflicts) {
        String column = removedResolution.getName();
        String table = removedResolution.getTableName();
        boolean started = false;
        boolean found = false;
        DBResolution firstResolution = null;
        DBResolution nextResolution = null;
        for (String[] strs : conflicts) {
            if (!strs[0].equals(column)) {
                continue;
            }
            if (!started) {
                DBResolution r = SuperClassUtil.getResolution(resolutions, strs[0], strs[2]);
                if (r == null) {
                    started = true;
                    firstResolution = new DBResolution(strs[0], strs[2], null);
                }
            }
            if (!found && strs[2].equals(table)) {
                found = true;
                continue;
            }
            if (started && found) {
                DBResolution r = SuperClassUtil.getResolution(resolutions, strs[0], strs[2]);
                if (r == null) {
                    nextResolution = new DBResolution(strs[0], strs[2], null);
                    break;
                }
            }
            continue;
        }
        assert (found);
        if (nextResolution == null) {
            return firstResolution;
        } else {
            return nextResolution;
        }
    }

    /**
     * get an attribute from a list by attribute name
     *
     * @param list List<DBAttribute> the given list that contains the instances of DBAttribute
     *     object
     * @param column String the given column name
     * @return DBAttribute
     */
    public static DBAttribute getAttrInList(List<DBAttribute> list, String column) {
        for (DBAttribute attr : list) {
            if (attr.getName().equals(column)) {
                return attr;
            }
        }
        return null;
    }

    /**
     * an inner class for storing an attribute and schema which the attribute is in
     *
     * @author moulinwang
     * @version 1.0 - 2009-6-4 created by moulinwang
     */
    static class NewAttribute {
        DBAttribute attr;
        SchemaInfo schema;

        public NewAttribute(DBAttribute attr, SchemaInfo schema) {
            this.attr = attr;
            this.schema = schema;
        }
    }

    /**
     * reset attributes and resolutions when resolution changes, eg:
     * <li>when adding an alias resolution, an attribute inherited should be added to the schema
     * <li>when adding another resolution, an attribute inherited should be changed to the schema
     *
     * @param database DatabaseInfo the given reference of DatabaseInfo object
     * @param oldSchemaInfo SchemaInfo the given reference of old SchemaInfo object
     * @param newSchemaInfo SchemaInfo the given reference of new SchemaInfo object
     * @param isClassType boolean whether is class type
     */
    public static void fireResolutionChanged(
            DatabaseInfo database,
            SchemaInfo oldSchemaInfo,
            SchemaInfo newSchemaInfo,
            boolean isClassType) {
        // checking attribute
        List<DBResolution> newResolutions = null;
        List<DBAttribute> localAttributes = null;
        if (isClassType) {
            newResolutions = getCloneResolutions(newSchemaInfo.getClassResolutions());
            localAttributes = newSchemaInfo.getLocalClassAttributes();
        } else {
            newResolutions = getCloneResolutions(newSchemaInfo.getResolutions());
            localAttributes = newSchemaInfo.getLocalAttributes();
        }

        Map<String, List<SchemaInfo>> columnInheritSchemaMap =
                new HashMap<String, List<SchemaInfo>>();
        checkingOnSuperClassChanged(
                columnInheritSchemaMap,
                database,
                oldSchemaInfo,
                newSchemaInfo,
                newSchemaInfo.getSuperClasses(),
                localAttributes,
                newResolutions,
                isClassType);

        // reset resolution, super classes and resolution
        List<SchemaInfo> schemalist = new ArrayList<SchemaInfo>();
        List<String> newSupers = newSchemaInfo.getSuperClasses();
        for (String sup : newSupers) {
            schemalist.add(database.getSchemaInfo(sup).clone());
        }
        if (isClassType) {
            resetAttribute(
                    newSchemaInfo,
                    schemalist,
                    newResolutions,
                    localAttributes,
                    columnInheritSchemaMap,
                    true);
            newSchemaInfo.setClassResolutions(newResolutions);
        } else {
            resetAttribute(
                    newSchemaInfo,
                    schemalist,
                    newResolutions,
                    localAttributes,
                    columnInheritSchemaMap,
                    false);
            newSchemaInfo.setResolutions(newResolutions);
        }
    }

    /**
     * reset attributes and resolutions when super classes change, eg:
     * <li>when adding a super class, some naming conflicts maybe occurs, so some resolution would
     *     be added, attributes to the schema should be reset and ordered
     *
     * @param database DatabaseInfo the given reference of a DatabaseInfo object
     * @param oldSchemaInfo SchemaInfo the given reference of old SchemaInfo object
     * @param newSchemaInfo SchemaInfo the given reference of new SchemaInfo object
     * @param newSupers List<String>
     * @return boolean whether is succeed
     */
    public static boolean fireSuperClassChanged(
            DatabaseInfo database,
            SchemaInfo oldSchemaInfo,
            SchemaInfo newSchemaInfo,
            List<String> newSupers) {

        // checking attribute
        List<DBResolution> newResolutions = getCloneResolutions(newSchemaInfo.getResolutions());
        List<DBAttribute> localAttributes = newSchemaInfo.getLocalAttributes();
        Map<String, List<SchemaInfo>> columnInheritSchemaMap =
                new HashMap<String, List<SchemaInfo>>();
        boolean success =
                checkingOnSuperClassChanged(
                        columnInheritSchemaMap,
                        database,
                        oldSchemaInfo,
                        newSchemaInfo,
                        newSupers,
                        localAttributes,
                        newResolutions,
                        false);
        if (!success) {
            return false;
        }
        // checking class attributes
        List<DBResolution> newClassResolutions =
                getCloneResolutions(newSchemaInfo.getClassResolutions());
        List<DBAttribute> localClassAttributes = newSchemaInfo.getLocalClassAttributes();
        Map<String, List<SchemaInfo>> classColumnInheritSchemaMap =
                new HashMap<String, List<SchemaInfo>>();
        boolean classSuccess =
                checkingOnSuperClassChanged(
                        classColumnInheritSchemaMap,
                        database,
                        oldSchemaInfo,
                        newSchemaInfo,
                        newSupers,
                        localClassAttributes,
                        newClassResolutions,
                        true);
        if (!classSuccess) {
            return false;
        }

        if (database == null) {
            return false;
        }

        // reset resolution, super classes and resolution
        List<SchemaInfo> schemalist = new ArrayList<SchemaInfo>();
        for (String sup : newSupers) {
            schemalist.add(database.getSchemaInfo(sup).clone());
        }

        if (success && classSuccess) {
            // remove inherit constraint
            List<SchemaInfo> superList = getSuperClasses(database, newSchemaInfo);
            List<Constraint> constraints = newSchemaInfo.getConstraints();
            for (int j = constraints.size() - 1; j >= 0; j--) {
                Constraint constraint = constraints.get(j);
                String constraintType = constraint.getType();
                String constraintName = constraint.getName();
                boolean isConstraintInheritSupported = isConstraintTypeInherit(constraintType);
                if (isConstraintInheritSupported
                        && newSchemaInfo.isInSuperClasses(superList, constraintName)) {
                    constraints.remove(j);
                }
            }
            resetAttribute(
                    newSchemaInfo,
                    schemalist,
                    newResolutions,
                    localAttributes,
                    columnInheritSchemaMap,
                    false);
            newSchemaInfo.setResolutions(newResolutions);

            resetAttribute(
                    newSchemaInfo,
                    schemalist,
                    newClassResolutions,
                    localClassAttributes,
                    classColumnInheritSchemaMap,
                    true);
            newSchemaInfo.setClassResolutions(newClassResolutions);

            newSchemaInfo.setSuperClasses(newSupers);
            // add inherit constraint
            superList = getSuperClasses(database, newSupers);
            for (SchemaInfo schema : superList) {
                constraints = schema.getConstraints();
                for (int i = 0; i < constraints.size(); i++) {
                    Constraint constraint = constraints.get(i);
                    String constraintType = constraint.getType();
                    boolean isConstraintInheritSupported = isConstraintTypeInherit(constraintType);
                    if (isConstraintInheritSupported) {
                        newSchemaInfo.addConstraint(constraint);
                    }
                }
            }
        }
        return true;
    }

    /**
     * check whether a type of constraint can be inherited
     *
     * @param constraintType String the given constraint type
     * @return boolean true if the specific string is the name of constraint,false otherwise
     */
    private static boolean isConstraintTypeInherit(String constraintType) {
        if (constraintType.equals(Constraint.ConstraintType.PRIMARYKEY.getText())
                || constraintType.equals(Constraint.ConstraintType.FOREIGNKEY.getText())
                || constraintType.equals(Constraint.ConstraintType.REVERSEUNIQUE.getText())
                || constraintType.equals(Constraint.ConstraintType.UNIQUE.getText())) {
            return true;
        }
        return false;
    }

    /**
     * clone a list of resolution and return
     *
     * @param resolutions List<DBResolution> the given list that contains the DBResolution instances
     * @return List<DBResolution> a clone of the specific argument
     */
    private static List<DBResolution> getCloneResolutions(List<DBResolution> resolutions) {
        List<DBResolution> newResolutions = new ArrayList<DBResolution>();
        for (DBResolution r : resolutions) {
            newResolutions.add(r.clone());
        }
        return newResolutions;
    }

    /**
     * validate whether columns are data type compatible when super classes change
     *
     * <p>if not compatible, return false
     *
     * @param columnInheritSchemaMap Map<String, List<SchemaInfo>> the given map that contains the
     *     list instance of SchemaInfo
     * @param database DatabaseInfo the given reference of DatabaseInfo object
     * @param oldSchemaInfo SchemaInfo the given reference of the old SchemaInfo object
     * @param newSchemaInfo SchemaInfo the given reference of the new SchemaInfo object
     * @param newSupers List<String> the given list that contains the info of super class
     * @param localAttributes List<DBAttribute> the given list that contains the instances of
     *     DBAttribute
     * @param newResolutions List<DBResolution> the give list that contains the instances of
     *     DBResolution
     * @param isClassAttr boolean whether is class attribute
     * @return boolean whether is class attribute
     */
    public static boolean checkingOnSuperClassChanged(
            Map<String, List<SchemaInfo>> columnInheritSchemaMap,
            DatabaseInfo database,
            SchemaInfo oldSchemaInfo,
            SchemaInfo newSchemaInfo,
            List<String> newSupers,
            List<DBAttribute> localAttributes,
            List<DBResolution> newResolutions,
            boolean isClassAttr) {

        List<String[]> conflicts =
                getColumnConflicts(database, newSchemaInfo, newSupers, isClassAttr);
        removeUnusedResolutionForSuper(newResolutions, newSupers);
        removeUnusedResolutionForConflict(newResolutions, conflicts);
        List<DBResolution> oldResolutions = null;
        if (oldSchemaInfo == null) {
            oldResolutions = new ArrayList<DBResolution>();
        } else {
            if (isClassAttr) {
                oldResolutions = oldSchemaInfo.getClassResolutions();
            } else {
                oldResolutions = oldSchemaInfo.getResolutions();
            }
        }
        addDefaultResolution(oldResolutions, newResolutions, conflicts, newSchemaInfo);

        if (database == null) {
            throw new NullPointerException();
        }

        // reset attributes
        List<SchemaInfo> schemalist = new ArrayList<SchemaInfo>();
        for (String sup : newSupers) {
            schemalist.add(database.getSchemaInfo(sup).clone());
        }
        // apply changes
        boolean success =
                checkingAttributeCompatible(
                        columnInheritSchemaMap,
                        database,
                        newSchemaInfo,
                        schemalist,
                        localAttributes,
                        newResolutions,
                        isClassAttr);
        return success;
    }

    /**
     * remove unneeded resolution for there is no conflict
     *
     * @param resolutions List<DBResolution> the given list that contains the instances of
     *     DBResolution object
     * @param conflicts List<String[]> the given list that contains the info of conflicts
     */
    private static void removeUnusedResolutionForConflict(
            List<DBResolution> resolutions, List<String[]> conflicts) {
        // columnName, a.getType(), s.getClassname()
        for (int j = 0; j < resolutions.size(); ) {
            DBResolution resolution = resolutions.get(j);
            boolean found = false;
            for (String[] conflict : conflicts) {
                if (conflict[2].equals(resolution.getTableName())
                        && conflict[0].equals(resolution.getName())) {
                    found = true;
                }
            }
            // if not found in conflicts, so should be removed
            if (found) {
                j++;
            } else {
                resolutions.remove(j);
            }
        }
    }

    /**
     * get a list of schema object by a schema's super classes
     *
     * @param database DatabaseInfo the given reference of DatabaseInfo object
     * @param newSchemaInfo SchemaInfo the given reference of SchemaInfo object
     * @return List<SchemaInfo> a list that contains the instances of SchemaInfo
     */
    public static List<SchemaInfo> getSuperClasses(
            DatabaseInfo database, SchemaInfo newSchemaInfo) {
        List<SchemaInfo> schemalist = new ArrayList<SchemaInfo>();

        if (database == null || newSchemaInfo == null) {
            return schemalist;
        }

        List<String> newSupers = newSchemaInfo.getSuperClasses();
        if (newSupers != null) {
            for (String sup : newSupers) {
                schemalist.add(database.getSchemaInfo(sup));
            }
        }

        return schemalist;
    }

    /**
     * get a list of schema object by a list of table names
     *
     * @param database DatabaseInfo the given reference of DatabaseInfo object
     * @param tableList List<String> the given list that contains the table name
     * @return List<SchemaInfo> a list that contains the instances of SchemaInfo
     */
    public static List<SchemaInfo> getSuperClasses(DatabaseInfo database, List<String> tableList) {
        List<SchemaInfo> schemalist = new ArrayList<SchemaInfo>();
        for (String sup : tableList) {
            schemalist.add(database.getSchemaInfo(sup));
        }
        return schemalist;
    }

    /**
     * validate whether columns are data type compatible when inherited other super classes
     *
     * @param columnInheritSchemaMap Map<String, List<SchemaInfo>>
     * @param database DatabaseInfo
     * @param newSchemaInfo SchemaInfo
     * @param schemalist List<SchemaInfo>
     * @param localAttributes List<DBAttribute>
     * @param resolutions List<DBResolution>
     * @param isClassAttr boolean
     * @return boolean
     */
    private static boolean checkingAttributeCompatible(
            Map<String, List<SchemaInfo>> columnInheritSchemaMap,
            DatabaseInfo database,
            SchemaInfo newSchemaInfo, // List<String> newSupers,
            List<SchemaInfo> schemalist,
            List<DBAttribute> localAttributes,
            List<DBResolution> resolutions,
            boolean isClassAttr) {

        // statistic
        // add local attributes first
        addAttributes2StatisticMap(
                columnInheritSchemaMap, newSchemaInfo, localAttributes, isClassAttr);
        // add super schemas' attributes
        addAttributes2StatisticMap(columnInheritSchemaMap, schemalist, isClassAttr);

        boolean success =
                computingAttributeList(
                        database, newSchemaInfo, resolutions, columnInheritSchemaMap, isClassAttr);

        return success;
    }

    /**
     * when super classes change, attributes of a schema changes, not only the number of attributes,
     * but also the order of attributes
     *
     * @param newSchemaInfo SchemaInfo
     * @param schemalist List<SchemaInfo>
     * @param resolutions List<DBResolution>
     * @param localAttributes List<DBAttribute>
     * @param columnInheritSchemaMap Map<String, List<SchemaInfo>>
     * @param isClassAttr boolean
     */
    private static void resetAttribute(
            SchemaInfo newSchemaInfo,
            List<SchemaInfo> schemalist,
            List<DBResolution> resolutions,
            List<DBAttribute> localAttributes,
            Map<String, List<SchemaInfo>> columnInheritSchemaMap,
            boolean isClassAttr) {
        // reorder attributes
        List<DBAttribute> newAttrList = new ArrayList<DBAttribute>();
        // add inherit attributes at first
        for (int j = 0; j < schemalist.size(); j++) {
            SchemaInfo schema = schemalist.get(j);
            List<DBAttribute> attrList = null;
            if (isClassAttr) {
                attrList = schema.getClassAttributes();
            } else {
                attrList = schema.getAttributes();
            }

            for (int i = 0; i < attrList.size(); i++) {
                DBAttribute attr = attrList.get(i);
                String columnName = attr.getName();
                List<SchemaInfo> list = columnInheritSchemaMap.get(columnName);
                /**
                 *
                 * <li>if it is in statistic map, it should be remained;
                 * <li>if it is not in, but it has an alias name, it should be remained too;
                 */
                if (list.contains(schema)) {
                    newAttrList.add(attr);
                } else {
                    String tableName = schema.getUniqueName();
                    DBResolution r = getResolution(resolutions, columnName, tableName);
                    if (r != null && r.getAlias() != null && !r.getAlias().equals("")) {
                        attr.setName(r.getAlias()); // modify attr name
                        newAttrList.add(attr);
                    }
                }
            }
        }
        // at last, add local attributes
        newAttrList.addAll(localAttributes);
        if (isClassAttr) {
            newSchemaInfo.setClassAttributes(newAttrList);
        } else {
            newSchemaInfo.setAttributes(newAttrList);
        }
    }

    /**
     * computing which attributes will be remained
     *
     * @param database DatabaseInfo
     * @param newSchemaInfo SchemaInfo
     * @param resolutions List<DBResolution>
     * @param columnInheritSchemaMap Map<String, List<SchemaInfo>>
     * @param isClassAttr boolean
     * @return boolean
     */
    public static boolean computingAttributeList(
            DatabaseInfo database,
            SchemaInfo newSchemaInfo,
            List<DBResolution> resolutions,
            Map<String, List<SchemaInfo>> columnInheritSchemaMap,
            boolean isClassAttr) {
        for (Iterator<Entry<String, List<SchemaInfo>>> i =
                        columnInheritSchemaMap.entrySet().iterator();
                i.hasNext(); ) {
            Entry<String, List<SchemaInfo>> entry = i.next();
            String columnName = entry.getKey();
            List<SchemaInfo> schemaList = entry.getValue();

            if (schemaList.size() > 1) {
                // there should be a resolution
                List<NewAttribute> attrList = new ArrayList<NewAttribute>();
                // local attribute has the highest priority
                NewAttribute localAttr = null;
                for (SchemaInfo schema : schemaList) {
                    DBAttribute attr = schema.getDBAttributeByName(columnName, isClassAttr);
                    if (attr == null) {
                        continue;
                    }

                    if (attr.getInherit().equals(newSchemaInfo.getUniqueName())) {
                        localAttr = new NewAttribute(attr, schema);
                    } else {
                        attrList.add(new NewAttribute(attr, schema));
                    }
                }
                /**
                 * compute:
                 * <li>if local attribute exists, check whether it is the lowest in the class
                 *     hierarchy
                 * <li>otherwise, return the lowest attributes, one or more in a list
                 */
                List<NewAttribute> lowestAttrList;
                try {
                    lowestAttrList = getLowestAttributes(attrList, localAttr, database);
                } catch (Exception e) {
                    LOGGER.error("", e);
                    return false;
                }
                int size = lowestAttrList.size();
                if (size == 1) {
                    // there is only one
                    schemaList.clear();
                    schemaList.add(lowestAttrList.get(0).schema);
                } else {
                    // select one
                    schemaList.clear();
                    boolean found = false;
                    for (int j = size - 1; j >= 0; j--) {
                        NewAttribute attr = lowestAttrList.get(j);
                        String column = attr.attr.getName();
                        String table = attr.schema.getUniqueName();

                        DBResolution r = getResolution(resolutions, column, table);
                        if (r != null && (r.getAlias() == null || "".equals(r.getAlias()))) {
                            found = true;
                            schemaList.add(attr.schema);
                        }
                    }
                    if (!found) {
                        schemaList.add(lowestAttrList.get(0).schema);
                    }
                }
            }
        }
        return true;
    }

    /**
     * add default resolution aligning with the list of conflicts
     *
     * @param oldResolutions List<DBResolution>
     * @param newResolutions List<DBResolution>
     * @param conflicts List<String[]>
     * @param newSchemaInfo SchemaInfo
     */
    public static void addDefaultResolution(
            List<DBResolution> oldResolutions,
            List<DBResolution> newResolutions,
            List<String[]> conflicts,
            SchemaInfo newSchemaInfo) {
        List<String> localAttrList = new ArrayList<String>();
        String table = newSchemaInfo.getUniqueName();
        if (table == null) {
            table = "";
        }
        for (String[] strs : conflicts) {
            String tbl = strs[2];
            String col = strs[0];
            // local attribute has the highest priority
            if (table.equals(tbl)) {
                localAttrList.add(col);
                continue;
            } else {
                if (localAttrList.contains(col)) {
                    continue;
                }
            }
            boolean found = false;
            boolean hasAlias = false;
            for (DBResolution r : newResolutions) {
                String columnName = r.getName();
                if (col.equals(columnName)) {
                    if (r.getAlias().equals("")) {
                        found = true;
                    } else {
                        if (tbl.equals(r.getTableName())) {
                            hasAlias = true;
                        }
                    }
                }
            }
            // if current conflict has an alias, it should not be considered.
            if (hasAlias) {
                continue;
            }
            if (!found) { // if not found in conflicts, check whether an resolution exist
                for (DBResolution r : oldResolutions) {
                    String columnName = r.getName();
                    if (col.equals(columnName)) {
                        newResolutions.add(r.clone());
                        found = true;
                        break;
                    }
                }
            }
            if (!found) { // give a default resolution
                newResolutions.add(new DBResolution(col, tbl, ""));
            }
        }
    }

    /**
     * sometime local defined attribute and super classes' attribute would be conflicted, these data
     * types must be compatible, and get the lowest attribute. the rule is :
     * <li>0. check these data types are compatible
     * <li>1. to choose local defined attribute if exist
     * <li>2. to choose the most special data type, if exist more than one, choose them in a list
     *
     * @param attrList List<NewAttribute>
     * @param localAttr NewAttribute
     * @param database DatabaseInfo
     * @throws Exception a possible exception
     * @return List<NewAttribute>
     */
    private static List<NewAttribute> getLowestAttributes(
            List<NewAttribute> attrList, NewAttribute localAttr, DatabaseInfo database)
            throws Exception {
        // check whether data types of localAttr and other inherit attrs are compatible
        List<NewAttribute> lowestAttribute = new ArrayList<NewAttribute>();
        if (localAttr == null) {
            lowestAttribute.add(attrList.get(0));
            for (int i = 1; i < attrList.size(); i++) {
                String dataType = lowestAttribute.get(0).attr.getType();
                DBAttribute attr = attrList.get(i).attr;
                Integer ret = DataType.isCompatibleType(database, dataType, attr.getType());
                if (ret == null) {
                    throw new Exception(
                            "inherit attribtues' data type are not compatible with each other");
                } else if (ret == 0) {
                    lowestAttribute.add(attrList.get(i));
                } else if (ret < 0) {
                    lowestAttribute.clear();
                    lowestAttribute.add(attrList.get(i));
                }
            }
        } else {
            String dataType = localAttr.attr.getType();
            for (NewAttribute attr : attrList) {
                Integer ret = DataType.isCompatibleType(database, dataType, attr.attr.getType());
                if (ret == null || ret < 0) {
                    throw new Exception(
                            "inherit attribtue's data type is not compatible with local defined attribute's data type");
                }
            }
            lowestAttribute.add(localAttr);
        }
        return lowestAttribute;
    }

    /**
     * remove unused resolution when some super class is removed.
     *
     * @param resolutions List<DBResolution>
     * @param newSupers List<String>
     */
    private static void removeUnusedResolutionForSuper(
            List<DBResolution> resolutions, List<String> newSupers) {
        for (int j = 0; j < resolutions.size(); ) {
            DBResolution resolution = resolutions.get(j);
            boolean found = false;
            for (String superClassName : newSupers) {
                if (superClassName.equals(resolution.getTableName())) {
                    found = true;
                }
            }
            // if not found in conflicts, so should be removed
            if (found) {
                j++;
            } else {
                resolutions.remove(j);
            }
        }
    }

    /**
     * given a set of super classes, return the conflict attribute information, including:
     * <li>attribute name
     * <li>data type
     * <li>name of the table which contains the attribute
     *
     * @param database DatabaseInfo
     * @param newSchemaInfo SchemaInfo
     * @param superClasses List<String>
     * @param isClassAttr boolean
     * @return List<String[]>
     */
    public static List<String[]> getColumnConflicts(
            DatabaseInfo database,
            SchemaInfo newSchemaInfo,
            List<String> superClasses,
            boolean isClassAttr) {
        List<DBAttribute> localAttributes = new ArrayList<DBAttribute>();

        if (isClassAttr) {
            localAttributes.addAll(newSchemaInfo.getLocalClassAttributes());
        } else {
            localAttributes.addAll(newSchemaInfo.getLocalAttributes());
        }

        //		List<String> superClasses = newSchema.getSuperClasses();
        Map<String, List<SchemaInfo>> columnInheritSchemaMap =
                new HashMap<String, List<SchemaInfo>>();
        addAttributes2StatisticMap(
                columnInheritSchemaMap, newSchemaInfo, localAttributes, isClassAttr);

        addAttributes2StatisticMap(columnInheritSchemaMap, database, superClasses, isClassAttr);
        List<String[]> retList = new ArrayList<String[]>();
        for (Iterator<Entry<String, List<SchemaInfo>>> i =
                        columnInheritSchemaMap.entrySet().iterator();
                i.hasNext(); ) {
            Entry<String, List<SchemaInfo>> entry = i.next();
            String columnName = entry.getKey();
            List<SchemaInfo> schemaList = entry.getValue();

            if (schemaList.size() > 1) {
                for (SchemaInfo s : schemaList) {
                    DBAttribute a = s.getDBAttributeByName(columnName, isClassAttr);
                    if (a == null) {
                        continue;
                    }
                    String[] strs = {columnName, a.getType(), s.getUniqueName()};
                    retList.add(strs);
                }
            }
        }
        return retList;
    }

    /**
     * add super classes' attribute to map for statistic
     *
     * @param columnSchemaMap Map<String, List<SchemaInfo>> the map for statistic,structure:
     *     key=attribute name, value=List\<SchemaInfo\>
     * @param database DatabaseInfo
     * @param superClasses List<String>
     * @param isClassAttr boolean
     */
    private static void addAttributes2StatisticMap(
            Map<String, List<SchemaInfo>> columnSchemaMap,
            DatabaseInfo database,
            List<String> superClasses,
            boolean isClassAttr) {

        List<SchemaInfo> schemalist = new ArrayList<SchemaInfo>();
        for (int i = 0, n = superClasses.size(); i < n; i++) {
            String superClass = superClasses.get(i);
            SchemaInfo superSchema = database.getSchemaInfo(superClass);
            schemalist.add(superSchema);
        }
        addAttributes2StatisticMap(columnSchemaMap, schemalist, isClassAttr);
    }

    /**
     * add super classes' attribute to map for statistic
     *
     * @param columnSchemaMap Map<String, List<SchemaInfo>>
     * @param superslist List<SchemaInfo>
     * @param isClassAttr boolean
     */
    private static void addAttributes2StatisticMap(
            Map<String, List<SchemaInfo>> columnSchemaMap,
            List<SchemaInfo> superslist,
            boolean isClassAttr) {
        for (int i = 0; i < superslist.size(); i++) {
            SchemaInfo superSchema = superslist.get(i);
            List<DBAttribute> list = null;
            if (isClassAttr) {
                list = superSchema.getClassAttributes();
            } else {
                list = superSchema.getAttributes();
            }
            addAttributes2StatisticMap(columnSchemaMap, superSchema, list, isClassAttr);
        }
    }

    /**
     * add attribute to map for statistic
     *
     * @param columnSchemaMap the map for statistic,structure: key=attribute name,
     *     value=List\<SchemaInfo\>
     * @param superSchema SchemaInfo
     * @param list List<DBAttribute>
     * @param isClassAttr boolean
     */
    private static void addAttributes2StatisticMap(
            Map<String, List<SchemaInfo>> columnSchemaMap,
            SchemaInfo superSchema,
            List<DBAttribute> list,
            boolean isClassAttr) {
        for (DBAttribute a : list) {
            String columnName = a.getName();
            List<SchemaInfo> schemaList = columnSchemaMap.get(columnName);
            if (schemaList == null) {
                schemaList = new ArrayList<SchemaInfo>();
                schemaList.add(superSchema);
                columnSchemaMap.put(columnName, schemaList);
            } else {
                boolean found = false;
                for (SchemaInfo s : schemaList) {
                    DBAttribute attrIN = s.getDBAttributeByName(columnName, isClassAttr);
                    if (attrIN == null) {
                        continue;
                    }
                    if (attrIN.getInherit().equals(a.getInherit())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    schemaList.add(superSchema);
                }
            }
        }
    }
}
