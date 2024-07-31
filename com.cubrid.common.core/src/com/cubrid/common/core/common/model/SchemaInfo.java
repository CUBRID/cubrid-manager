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

package com.cubrid.common.core.common.model;

import static org.apache.commons.lang.StringUtils.defaultString;

import com.cubrid.common.core.common.model.Constraint.ConstraintType;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;

/**
 * to store a table or view schema information
 * <li>for table, including columns, class columns, super classes, resolutions, class resolutions,
 *     constraint
 * <li>for view, including columns, class columns, query specifications
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-5 created by moulinwang
 */
public class SchemaInfo implements Comparable<SchemaInfo>, Cloneable {
    private static final Logger LOGGER = LogUtil.getLogger(SchemaInfo.class);
    public static final String VIRTUAL_VIEW = "view";
    public static final String VIRTUAL_NORMAL = "normal";
    private String classname = null;
    private String description = null;
    private String type = null;
    private String owner = null;
    private String virtual = null;
    private String dbname = null;
    private boolean isReuseOid = false;
    private String isPartitionGroup = null; // "YES" or "NO"
    private List<DBAttribute> classAttributes = null; // DBAttribute
    private List<DBAttribute> attributes = null; // DBAttribute
    private List<DBMethod> classMethods = null; // DBMethod
    private List<DBMethod> methods = null; // DBMethod
    private List<DBResolution> classResolutions = null; // DBResolution
    private List<DBResolution> resolutions = null; // DBResolution
    private List<Constraint> constraints = null; // Constraint
    private List<String> superClasses = null; // add super classes
    private List<String> subClasses = null;
    private List<String> oidList = null;
    private List<String> methodFiles = null;
    private List<String> querySpecs = null;
    private List<PartitionInfo> partitions = null;
    // cubrid 9.1
    private String collation;
    // Added For UserSchema(CUBRID 11.2 Or Higher)
    private String uniqueName = null;

    public DBAttribute getAutoIncrementColumn() {
        if (attributes == null) {
            return null;
        }

        for (DBAttribute attr : attributes) {
            if (attr.getAutoIncrement() != null) {
                return attr;
            }
        }

        return null;
    }

    /**
     * get constraint if exists via constraint name, given a type name for validation
     *
     * @param name String the given constraint name
     * @param type String the given constraint type
     * @return Constraint a instance of the Costraint
     */
    public Constraint getConstraintByName(String name, String type) {
        if (constraints == null) {
            return null;
        } else {
            for (int i = 0; i < constraints.size(); i++) {
                Constraint constraint = constraints.get(i);
                if (constraint.getType().equals(type) && constraint.getName().equals(name)) {
                    return constraint;
                }
            }
        }
        return null;
    }

    /**
     * If the constraint is invalid PK or FK in schema info, remove it. And if the attribute is
     * invalid, remove the attribute in the constraint.
     *
     * @param removeEmptyAttrName if true, the attribute whose name is empty will be deleted.
     */
    public void removeInvalidPKAndIndex(boolean removeEmptyAttrName) {
        List<Constraint> constraints = getConstraints();
        for (Constraint constraint : constraints) {
            if (constraint == null || (!constraint.isPK() && !constraint.isIndex())) {
                continue;
            }
            if (removeEmptyAttrName) {
                constraint.removeEmptyAttrName();
            }
            List<String> attrs = constraint.getAttributes();
            for (int i = attrs.size() - 1; i >= 0; i--) {
                if (i > attrs.size() - 1) { // some elements may be deleted in the list
                    continue;
                }
                if (!removeEmptyAttrName && StringUtil.isEmpty(attrs.get(i))) {
                    continue;
                }
                DBAttribute dbAttr = getDBAttributeByName(attrs.get(i), false);
                if (dbAttr == null) {
                    constraint.removeAttribute(attrs.get(i));
                }
            }
        }
        removeEmptyConstraint();
    }

    public void removeEmptyConstraint() { // FIXME add description
        List<Constraint> constraints = getConstraints();
        for (int i = constraints.size() - 1; i >= 0; i--) {
            if (constraints.get(i) == null || StringUtil.isEmpty(constraints.get(i).getName())) {
                constraints.remove(i);
            } else if (constraints.get(i).isEmptyAttrList()
                    && (constraints.get(i).isPK()
                            || constraints
                                    .get(i)
                                    .isIndex())) { // after editing, check the validity of FK
                constraints.remove(i);
            }
        }
    }

    /**
     * remove constraint if exists via constraint name, given a type name for validation
     *
     * @param name String the given constraint name
     * @param type String the given constraint type
     */
    public void removeConstraintByName(String name, String type) {
        if (constraints == null) {
            return;
        } else {
            for (int i = 0; i < constraints.size(); i++) {
                Constraint constraint = constraints.get(i);
                if (constraint.getType().equals(type)
                        && StringUtil.isEqual(constraint.getName(), name)) {
                    constraints.remove(i);
                    return;
                }
            }
        }
    }

    /**
     * Remove unique constraint on an attribute
     *
     * @param attrName String the given attribute type
     * @return The Constraint
     */
    public Constraint removeUniqueByAttrName(String attrName) {
        if (constraints == null) {
            return null;
        } else {
            for (int i = constraints.size() - 1; i >= 0; i--) {
                Constraint constraint = constraints.get(i);
                if (constraint.getType().equals(ConstraintType.UNIQUE.getText())) {
                    List<String> attributes = constraint.getAttributes();
                    if (attributes.size() == 1 && attributes.get(0).equals(attrName)) {
                        return constraints.remove(i);
                    }
                }
            }
        }
        return null;
    }

    public void removeAttrInConstraints(String attrName) { // FIXME add description
        if (constraints == null) {
            return;
        } else {
            for (int i = 0; i < constraints.size(); i++) {
                Constraint constraint = constraints.get(i);
                constraint.removeAttribute(attrName);
            }
        }
    }

    public void removeConstraintByAttrName(String attrName) { // FIXME add description
        if (constraints == null) {
            return;
        } else {
            for (int i = constraints.size() - 1; i >= 0; i--) {
                Constraint constraint = constraints.get(i);
                List<String> attributes = constraint.getAttributes();
                if (attributes.contains(attrName)) {
                    constraints.remove(i);
                }
            }
        }
    }

    public Constraint getUniqueByAttrName(
            String attrName) { // FIXME add description, use "findXxx" instead of getXxx
        if (constraints == null) {
            return null;
        } else {
            for (int i = constraints.size() - 1; i >= 0; i--) {
                Constraint constraint = constraints.get(i);
                if (ConstraintType.UNIQUE.getText().equals(constraint.getType())) {
                    List<String> attributes = constraint.getAttributes();
                    if (attributes.size() == 1 && attributes.get(0).equals(attrName)) {
                        return constraints.get(i);
                    }
                }
            }
        }
        return null;
    }
    /**
     * get FK constraint by name if exists
     *
     * @param fkName String the given foreign key name
     * @return Constraint the instance of Constraint
     */
    public Constraint getFKConstraint(String fkName) { // FIXME use "findXxx" instead of getXxx
        if (constraints == null) {
            return null;
        } else {
            for (Constraint fk : constraints) {
                if (fk.getType().equals(ConstraintType.FOREIGNKEY.getText())
                        && fkName.equals(fk.getName())) {
                    return fk;
                }
            }
        }
        return null;
    }

    /**
     * get FK constraint by name if exists
     *
     * @return List<Constraint> a list that includes the references of Constraint
     */
    public List<Constraint> getFKConstraints() { // FIXME use getAllFKConstraints()
        List<Constraint> fkList = new ArrayList<Constraint>();
        if (constraints == null) {
            return fkList;
        } else {
            for (Constraint fk : constraints) {
                if (fk.getType().equals(ConstraintType.FOREIGNKEY.getText())) {
                    fkList.add(fk);
                }
            }
        }
        return fkList;
    }

    /**
     * Get all index type constraints:"INDEX", "UNIQUE", "REVERSE INDEX", "REVERSE UNIQUE",
     * excluding : PK
     *
     * @return List<Constraint> a list that includes all index type constraints
     */
    public List<Constraint> getAllIndexTypeConstraints() {
        List<Constraint> result = new LinkedList<Constraint>();
        List<Constraint> constraints = getConstraints();
        for (Constraint constraint : constraints) {
            if (constraint.isIndex()) {
                result.add(constraint);
            }
        }
        return result;
    }

    /**
     * Update the column name in all index type constraint.
     *
     * @param oldName
     * @param newName
     */
    public void updateAttrNameInIndex(String oldName, String newName) {
        if (StringUtil.isEqual(oldName, newName)) {
            return;
        }
        List<Constraint> indexList = getAllIndexTypeConstraints();
        for (Constraint index : indexList) {
            index.updateAttrNameInIndex(oldName, newName);
        }
    }

    /**
     * Remove all of index("INDEX", "UNIQUE", "REVERSE INDEX", "REVERSE UNIQUE"), that contains the
     * column name of attrName
     *
     * @param attrName
     */
    public void removeIndexByAttrName(String attrName) {
        List<Constraint> indexList = getAllIndexTypeConstraints();
        for (Constraint index : indexList) {
            if (index.isIncludeAttr(attrName)) {
                removeConstraintByName(index.getName(), index.getType());
            }
        }
    }

    /**
     * The referenced table name has been changed, update the fk constraints.
     *
     * @param oldName
     * @param newName
     */
    public void updateForeignSchemainfoName(
            String oldName, String newName) { // FIXME updateForeignSchemaName
        List<Constraint> fks = getFKConstraints();
        for (Constraint fk : fks) {
            fk.updateReferenceName(oldName, newName);
        }
    }

    /**
     * remove FK constraint if exists
     *
     * @param fk Constraint the given reference of Constraint
     */
    public void removeFKConstraint(Constraint fk) {
        removeFKConstraint(fk.getName());
    }

    /**
     * remove FK constraint by name if exists
     *
     * @param fkName String the given foreign key
     */
    public void removeFKConstraint(String fkName) {
        if (constraints == null) {
            return;
        } else {
            for (int i = 0; i < constraints.size(); i++) {
                Constraint fk = constraints.get(i);
                if (fk.getType().equals(ConstraintType.FOREIGNKEY.getText())
                        && fkName.equals(fk.getName())) {
                    constraints.remove(i);
                }
            }
        }
    }

    /**
     * get foreign table list
     *
     * @return List<String> a list that includes the foreign table name
     */
    public List<String> getForeignTables() {
        List<String> list = new ArrayList<String>();
        if (constraints == null) {
            return list;
        } else {
            for (Constraint fk : constraints) {
                if (fk.getType().equals(ConstraintType.FOREIGNKEY.getText())) {
                    List<String> rules = fk.getRules();
                    for (String rule : rules) {
                        if (rule.startsWith("REFERENCES ")) {
                            list.add(rule.replace("REFERENCES ", ""));
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * return whether the given column is unique, by checking whether exists a unique constraint
     * defined on just this column
     *
     * @param attr DBAttribute the given reference of DBAttribute instance
     * @param superList List<SchemaInfo> the given list that includes the instance of SchemaInfo
     * @return boolean true if attribute is unique,false otherwise
     */
    public boolean isAttributeUnique(DBAttribute attr, List<SchemaInfo> superList) {
        if (constraints == null) {
            return false;
        } else {
            String attrName = attr.getName();
            boolean isInherited = attr.getInherit().equals(this.getUniqueName()) ? false : true;
            for (Constraint constraint : constraints) {
                String constraintName = constraint.getName();
                if (isInherited) {
                    if (!isInSuperClasses(superList, constraintName)) {
                        continue;
                    }
                } else {
                    if (isInSuperClasses(superList, constraintName)) {
                        continue;
                    }
                }
                String constraintType = constraint.getType();
                if (constraintType.equals(ConstraintType.UNIQUE.getText())
                        && constraint.getAttributes().size() == 1
                        && constraint.getAttributes().get(0).equals(attrName)
                        && constraint.getRules().get(0).equals(attrName + " ASC")) {
                    return true;
                }
                if (constraintType.equals(ConstraintType.PRIMARYKEY.getText())
                        && constraint.getAttributes().size() == 1
                        && constraint.getAttributes().get(0).equals(attrName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * trigger local defined columns(not inherit from super classes) to changed its table name when
     * schema name changed
     *
     * @param newClassName String the given new class name
     */
    private void fireClassNameChanged(String newTableName) {
        List<DBAttribute> list = new ArrayList<DBAttribute>();
        list.addAll(getAttributes());
        list.addAll(getClassAttributes());
        for (DBAttribute a : list) {
            if (a.getInherit().equals(uniqueName)) {
                a.setInherit(newTableName);
            }
        }
        if (this.partitions != null && !this.partitions.isEmpty()) {
            for (PartitionInfo info : partitions) {
                info.setTableName(newTableName);
            }
        }
    }

    /**
     * when column name changed, make constraints containing the column to change the column name
     *
     * @param oldAttrName String the given old attribute name
     * @param newAttrName String the given new attribute name
     * @param superList List<SchemaInfo> the given list that includes the references of SchemaInfo
     */
    private void fireAttributeNameChanged(
            String oldAttrName, String newAttrName, List<SchemaInfo> superList) {
        List<Constraint> clist = this.getConstraints();
        for (Constraint c : clist) {
            if (!isInSuperClasses(superList, c.getName())) {
                List<String> attributes = c.getAttributes();
                if (attributes.contains(oldAttrName)) {
                    int index = attributes.indexOf(oldAttrName);
                    attributes.remove(index);
                    attributes.add(index, newAttrName);

                    /*For TOOLS-1013,Update the rule's attribute name*/
                    for (int i = 0; i < c.getRules().size(); i++) {
                        String rule = c.getRules().get(i);
                        if (rule != null && rule.startsWith(oldAttrName)) {
                            c.getRules().remove(i);
                            c.getRules().add(newAttrName + rule.substring(oldAttrName.length()));
                        }
                    }
                }
            }
        }
    }

    /** @return a new copy of current schema instance */
    @Override
    public SchemaInfo clone() {
        SchemaInfo newSchemaInfo = null;
        try {
            newSchemaInfo = (SchemaInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (newSchemaInfo == null) {
            return null;
        }
        if (classAttributes == null) {
            newSchemaInfo.classAttributes = null;
        } else {
            newSchemaInfo.classAttributes = new ArrayList<DBAttribute>();
            for (DBAttribute a : classAttributes) {
                newSchemaInfo.classAttributes.add(a.clone());
            }
        }

        if (attributes == null) {
            newSchemaInfo.attributes = null;
        } else {
            newSchemaInfo.attributes = new ArrayList<DBAttribute>();
            for (DBAttribute a : attributes) {
                newSchemaInfo.attributes.add(a.clone());
            }
        }

        if (classResolutions == null) {
            newSchemaInfo.classResolutions = null;
        } else {
            newSchemaInfo.classResolutions = new ArrayList<DBResolution>();
            for (DBResolution a : classResolutions) {
                newSchemaInfo.classResolutions.add(a.clone());
            }
        }

        if (resolutions == null) {
            newSchemaInfo.resolutions = null;
        } else {
            newSchemaInfo.resolutions = new ArrayList<DBResolution>();
            for (DBResolution a : resolutions) {
                newSchemaInfo.resolutions.add(a.clone());
            }
        }

        if (constraints == null) {
            newSchemaInfo.constraints = null;
        } else {
            newSchemaInfo.constraints = new ArrayList<Constraint>();
            for (Constraint a : constraints) {
                newSchemaInfo.constraints.add(a.clone());
            }
        }
        if (superClasses == null) {
            newSchemaInfo.superClasses = null;
        } else {
            newSchemaInfo.superClasses = new ArrayList<String>();
            for (String a : superClasses) {
                newSchemaInfo.superClasses.add(a);
            }
        }
        if (subClasses == null) {
            newSchemaInfo.subClasses = null;
        } else {
            newSchemaInfo.subClasses = new ArrayList<String>();
            for (String a : subClasses) {
                newSchemaInfo.subClasses.add(a);
            }
        }
        if (oidList == null) {
            newSchemaInfo.oidList = null;
        } else {
            newSchemaInfo.oidList = new ArrayList<String>();
            for (String a : oidList) {
                newSchemaInfo.oidList.add(a);
            }
        }
        if (querySpecs == null) {
            newSchemaInfo.querySpecs = null;
        } else {
            newSchemaInfo.querySpecs = new ArrayList<String>();
            for (String a : querySpecs) {
                newSchemaInfo.querySpecs.add(a);
            }
        }
        return newSchemaInfo;
    }

    /** @return description string for debug */
    public String toString() {
        StringBuffer bf = new StringBuffer();
        bf.append("DB name:" + this.dbname + "\n");
        bf.append("unique name:" + this.uniqueName + "\n");
        bf.append("class name:" + this.classname + "\n");
        bf.append("\tOwner:" + this.owner + "\n");
        bf.append("\ttype:" + this.type + "\n");
        bf.append("\tvirtual:" + this.virtual + "\n");
        List<DBAttribute> list = new ArrayList<DBAttribute>();
        list.addAll(this.getClassAttributes());
        list.addAll(this.getAttributes());
        for (DBAttribute a : list) {
            bf.append("\n" + a.toString());
        }

        List<Constraint> clist = getConstraints();
        for (Constraint c : clist) {
            bf.append("\n" + c.toString());
        }

        List<String> slist = getSuperClasses();
        bf.append("Supper Class:");
        for (String str : slist) {
            bf.append(str + "\n");
        }
        List<DBResolution> rlist = new ArrayList<DBResolution>();
        rlist.addAll(this.getClassResolutions());
        rlist.addAll(this.getResolutions());
        for (DBResolution r : rlist) {
            bf.append(r.toString() + "\n");
        }

        return bf.toString();
    }

    /**
     * add a query specification to list
     *
     * @param querySpec String the given query specification
     */
    public void addQuerySpec(String querySpec) {
        if (null == querySpecs) {
            querySpecs = new ArrayList<String>();
        }
        querySpecs.add(querySpec);
    }

    /**
     * add a super class to list
     *
     * @param superClass String the given super class name
     */
    public void addSuperClass(String superClass) {
        if (null == superClasses) {
            superClasses = new ArrayList<String>();
        }
        superClasses.add(superClass);
    }

    /**
     * add a method file to list
     *
     * @param methodfile String the given method files
     */
    public void addMethodFile(String methodfile) {
        if (null == methodFiles) {
            methodFiles = new ArrayList<String>();
        }
        methodFiles.add(methodfile);
    }

    /**
     * add a constraint to list
     *
     * @param constraint Constraint the given reference of Constraint object
     */
    public void addConstraint(Constraint constraint) {
        if (null == constraints) {
            constraints = new ArrayList<Constraint>();
        }
        constraints.add(constraint);
    }

    /**
     * add a resolution to list
     *
     * @param resolution DBResolultion the reference of a DBResolution reference
     */
    public void addResolution(DBResolution resolution) {
        if (null == resolutions) {
            resolutions = new ArrayList<DBResolution>();
        }
        resolutions.add(resolution);
        resolution.setClassResolution(false);
    }

    /**
     * add a class resolution to list
     *
     * @param classResolution DBResolution the reference of DBResolution object
     */
    public void addClassResolution(DBResolution classResolution) {
        if (null == classResolutions) {
            classResolutions = new ArrayList<DBResolution>();
        }
        classResolutions.add(classResolution);
        classResolution.setClassResolution(true);
    }

    /**
     * add a method to list
     *
     * @param method DBMethod the reference of DBMethod object
     */
    public void addMethod(DBMethod method) {
        if (null == methods) {
            methods = new ArrayList<DBMethod>();
        }
        methods.add(method);
    }

    /**
     * add a class method to list
     *
     * @param classMethod DBMethod the reference of DBMethod object
     */
    public void addClassMethod(DBMethod classMethod) {
        if (null == classMethods) {
            classMethods = new ArrayList<DBMethod>();
        }
        classMethods.add(classMethod);
    }

    /**
     * add an instance attribute to list
     *
     * @param attribute DBAttribute the reference of DBAttribute object
     */
    public void addAttribute(DBAttribute attribute) {
        if (null == attributes) {
            attributes = new ArrayList<DBAttribute>();
        }
        attributes.add(attribute);
        attribute.setClassAttribute(false);
    }

    /**
     * add a class attribute to list
     *
     * @param classAttribute DBAttribute the reference of DBAttribute object
     */
    public void addClassAttribute(DBAttribute classAttribute) {
        if (null == classAttributes) {
            classAttributes = new ArrayList<DBAttribute>();
        }
        classAttributes.add(classAttribute);
        classAttribute.setClassAttribute(true);
    }

    /**
     * remove attribute by name
     *
     * @param attributeName String the given attribute name
     * @param isClassAttribute boolean whether attribute is class attribute
     * @return boolean true if the list that includes DBAttribute instance contained the specified
     *     element
     */
    public boolean removeDBAttributeByName(String attributeName, boolean isClassAttribute) {
        DBAttribute attr = getDBAttributeByName(attributeName, isClassAttribute);
        if (attr != null) {
            if (isClassAttribute) {
                return getClassAttributes().remove(attr);
            } else {
                return getAttributes().remove(attr);
            }
        }
        return false;
    }

    public boolean replaceDBAttributeByName(
            String attributeName, DBAttribute newAttribute) { // FIXME add description
        for (int i = 0, len = getAttributes().size(); i < len; i++) {
            DBAttribute origAttr = getAttributes().get(i);
            if (StringUtil.isEqualIgnoreCase(origAttr.getName(), attributeName)) {
                getAttributes().set(i, newAttribute);
                return true;
            }
        }

        return false;
    }

    /**
     * Add a new attribute
     *
     * @param newDBAttribute DBAttribute the reference of the DBAttribute object
     * @param isClassAttribute boolean whether this new attribute is class attribute
     */
    public void addDBAttribute(DBAttribute newDBAttribute, boolean isClassAttribute) {
        if (isClassAttribute) {
            addClassAttribute(newDBAttribute);
        } else {
            addAttribute(newDBAttribute);
        }
    }

    /**
     * replace a column in its origin position
     *
     * @param oldDBAttribute DBAttribute the given reference of the old DBAttribute object
     * @param newDBAttribute DBAttribute the given reference of the new DBAttribute object
     * @param isClassAttribute boolean whether is class attribute
     * @param superList List<SchemaInfo> the given list that includes the instance of SchemaInfo
     * @return boolean true if replace,false otherwise
     */
    public boolean replaceDBAttributeByName(
            DBAttribute oldDBAttribute,
            DBAttribute newDBAttribute,
            boolean isClassAttribute,
            List<SchemaInfo> superList) {
        String attributeName = oldDBAttribute.getName();
        if (isClassAttribute) {
            if (null != classAttributes) {
                for (int i = 0; i < classAttributes.size(); i++) {
                    if (classAttributes.get(i).getName().equalsIgnoreCase(attributeName)) {
                        classAttributes.remove(i);
                        classAttributes.add(i, newDBAttribute);
                        return true;
                    }
                }
            }
        } else {
            if (null != attributes) { // FIXME variable != constant
                for (int i = 0; i < attributes.size(); i++) {
                    if (attributes.get(i).getName().equalsIgnoreCase(attributeName)) {
                        attributes.remove(i);
                        attributes.add(i, newDBAttribute);
                        fireAttributeNameChanged(
                                attributeName, newDBAttribute.getName(), superList);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * get DBAttribute by name, <br>
     * if is class attribute,search in class attributes; otherwise search in instance attributes
     *
     * @param attributeName String the given attribute name
     * @param isClassAttribute boolean whether is class attribute
     * @return DBAtribute a DBAttribute instance
     */
    public DBAttribute getDBAttributeByName(String attributeName, boolean isClassAttribute) {
        if (isClassAttribute) {
            if (null != classAttributes) {
                for (DBAttribute a : classAttributes) {
                    if (a.getName().equalsIgnoreCase(attributeName)) {
                        return a;
                    }
                }
            }
        } else {
            if (null != attributes) { // FIXME variable != constant
                for (DBAttribute a : attributes) {
                    if (a.getName().equalsIgnoreCase(attributeName)) {
                        return a;
                    }
                }
            }
        }
        return null;
    }

    /**
     * get DBAttribute by name, first search in instance attributes, if not found, search in class
     * attributes
     *
     * @param methodName String the given method name
     * @return DBMethod a DBMethod instance
     */
    public DBMethod getDBMethodByName(String methodName) {
        if (null != methods) { // FIXME variable != constant
            for (DBMethod a : methods) {
                if (a.name.equals(methodName)) {
                    return a;
                }
            }
        }
        if (null != classMethods) { // FIXME variable != constant
            for (DBMethod a : classMethods) {
                if (a.name.equals(methodName)) {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     * get Constraint by name
     *
     * @param constraintName String the given constraint name
     * @return Constraint a Constraint instance
     */
    public Constraint getConstraintByName(String constraintName) {
        if (null != constraints) { // FIXME variable != constant
            for (Constraint c : constraints) {
                if (c.getName().equals(constraintName)) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * return PK constraint <br>
     * Note: when inheritance, current schema will inherit super classes' PK constraint
     *
     * @param superList List<SchemaInfo> the given list that includes the instance of SchemaInfo
     * @return Constraint the Constraint object
     */
    public Constraint getPK(List<SchemaInfo> superList) {
        if (null != constraints) { // FIXME variable != constant
            for (Constraint constraint : constraints) {
                if (constraint.getType().equals(ConstraintType.PRIMARYKEY.getText())) {
                    String constraintName = constraint.getName();
                    if (!isInSuperClasses(superList, constraintName)) {
                        return constraint;
                    }
                }
            }
        }
        return null;
    }

    /**
     * return inherited PK constraints from super classes
     *
     * @param superList List<SchemaInfo> the given list that includes the instance of SchemaInfo
     * @return List<Constraint> a list that includes the instance of Constraint
     */
    public List<Constraint> getInheritPK(List<SchemaInfo> superList) {
        List<Constraint> inheritPKList = new ArrayList<Constraint>();
        if (null != constraints) { // FIXME variable != constant
            for (Constraint constraint : constraints) {
                if (constraint.getType().equals(ConstraintType.PRIMARYKEY.getText())) {
                    String constraintName = constraint.getName();
                    if (isInSuperClasses(superList, constraintName)) {
                        inheritPKList.add(constraint);
                    }
                }
            }
        }
        return inheritPKList;
    }

    /**
     * check whether a constraint is inherited from super classes
     *
     * @param superList List<SchemaInfo> the given list that includes the instance of SchemaInfo
     * @param constraintName String the given constraint name
     * @return boolean true if constraint inherits the supper class,false otherwise
     */
    public boolean isInSuperClasses(List<SchemaInfo> superList, String constraintName) {
        boolean found = false;
        for (SchemaInfo sup : superList) {
            Constraint c = sup.getConstraintByName(constraintName);
            if (c == null) {
                continue;
            } else {
                return true;
            }
        }
        return found;
    }

    /**
     * get partition if exists via partition name
     *
     * @param name String the given partition name
     * @return PartitionInfo the PartitionInfo instance
     */
    public PartitionInfo getPartitionByName(String name) {
        if (partitions == null) {
            return null;
        } else {
            for (int i = 0; i < partitions.size(); i++) {
                PartitionInfo partition = partitions.get(i);
                if (partition.getPartitionName().equals(name)) {
                    return partition;
                }
            }
        }
        return null;
    }

    /**
     * returning of all partition info list
     *
     * @return List<PartitionInfo> a list that includes the instances of PartitionInfo
     */
    public List<PartitionInfo> getPartitionList() {
        return partitions;
    }

    /**
     * updating of all partition info list
     *
     * @param partitionInfoList List<PartitionInfo> the given list that includes the instances of
     *     ParttionInfo
     */
    public void setPartitionList(List<PartitionInfo> partitionInfoList) {
        partitions = partitionInfoList;
    }

    /**
     * Whether is system class
     *
     * @return boolean true if is system class, false otherwise
     */
    public boolean isSystemClass() {
        if ("system".equals(type)) {
            return true;
        }
        return false;
    }

    /**
     * @param obj SchemaInfo the given object to compare
     * @return int the value 0 if the name of argument obj is equal to class name; a value less than
     *     0 if the class name is lexicographically less than the name of argument obj; and a value
     *     greater than 0 if the class name is lexicographically greater than the name of argument
     *     obj.
     */
    public int compareTo(SchemaInfo obj) {
        return uniqueName.compareTo(obj.uniqueName);
    }

    /** @return int a hash code value for this object */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((classAttributes == null) ? 0 : classAttributes.hashCode());
        result = prime * result + ((classMethods == null) ? 0 : classMethods.hashCode());
        result = prime * result + ((classResolutions == null) ? 0 : classResolutions.hashCode());
        result = prime * result + ((classname == null) ? 0 : classname.hashCode());
        result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
        result = prime * result + ((dbname == null) ? 0 : dbname.hashCode());
        result = prime * result + ((isPartitionGroup == null) ? 0 : isPartitionGroup.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((resolutions == null) ? 0 : resolutions.hashCode());
        result = prime * result + ((superClasses == null) ? 0 : superClasses.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((virtual == null) ? 0 : virtual.hashCode());
        return result;
    }

    /**
     * @param obj Object the reference object with which to compare
     * @return boolean true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) { // FIXME need to find to reduce code lines
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SchemaInfo other = (SchemaInfo) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (classAttributes == null) {
            if (other.classAttributes != null) {
                return false;
            }
        } else if (!classAttributes.equals(other.classAttributes)) {
            return false;
        }
        if (classMethods == null) {
            if (other.classMethods != null) {
                return false;
            }
        } else if (!classMethods.equals(other.classMethods)) {
            return false;
        }
        if (classResolutions == null) {
            if (other.classResolutions != null) {
                return false;
            }
        } else if (!classResolutions.equals(other.classResolutions)) {
            return false;
        }
        if (classname == null) {
            if (other.classname != null) {
                return false;
            }
        } else if (!classname.equals(other.classname)) {
            return false;
        }
        if (constraints == null) {
            if (other.constraints != null) {
                return false;
            }
        } else if (!constraints.equals(other.constraints)) {
            return false;
        }
        if (dbname == null) {
            if (other.dbname != null) {
                return false;
            }
        } else if (!dbname.equals(other.dbname)) {
            return false;
        }
        if (isPartitionGroup == null) {
            if (other.isPartitionGroup != null) {
                return false;
            }
        } else if (!isPartitionGroup.equals(other.isPartitionGroup)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (resolutions == null) {
            if (other.resolutions != null) {
                return false;
            }
        } else if (!resolutions.equals(other.resolutions)) {
            return false;
        }
        if (superClasses == null) {
            if (other.superClasses != null) {
                return false;
            }
        } else if (!superClasses.equals(other.superClasses)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (virtual == null) {
            if (other.virtual != null) {
                return false;
            }
        } else if (!virtual.equals(other.virtual)) {
            return false;
        }
        if (this.isReuseOid != other.isReuseOid) {
            return false;
        }
        return true;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getClassname() {
        return classname;
    }

    /** @param classname String the given class name */
    public void setClassname(String classname) {
        // fireClassNameChanged(classname);
        this.classname = classname;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        fireClassNameChanged(uniqueName);
        this.uniqueName = uniqueName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVirtual() {
        return virtual;
    }

    public void setVirtual(String virtual) {
        this.virtual = virtual;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String isPartitionGroup() {
        return isPartitionGroup;
    }

    public void setPartitionGroup(String isPartitionGroup) {
        this.isPartitionGroup = isPartitionGroup;
    }

    public List<String> getMethodFiles() {
        return methodFiles;
    }

    public List<DBMethod> getClassMethods() {
        return classMethods;
    }

    public List<DBMethod> getMethods() {
        return methods;
    }

    /**
     * Get the class attributes
     *
     * @return List<DBAttribute> a list that includes the instances of DBAttribute
     */
    public List<DBAttribute> getClassAttributes() {
        if (null == classAttributes) { // FIXME variable == null
            return new ArrayList<DBAttribute>();
        }
        return classAttributes;
    }

    /**
     * Get the plain attributes
     *
     * @return List<DBATtribute> a list that includes the instances of DBAttribute
     */
    public List<DBAttribute> getAttributes() {
        if (null == attributes) { // FIXME variable == null
            return new ArrayList<DBAttribute>();
        }
        return attributes;
    }

    /**
     * return local defined class attributes, not inherit from super classes
     *
     * @return List<DBATtribute> a list that includes the instances of DBAttribute
     */
    public List<DBAttribute> getLocalClassAttributes() {
        if (null == classAttributes) { // FIXME variable == null
            return new ArrayList<DBAttribute>();
        }
        List<DBAttribute> list = new ArrayList<DBAttribute>();
        for (DBAttribute classAttribute : classAttributes) {
            if (classAttribute.getInherit().equals(this.getUniqueName())) {
                list.add(classAttribute);
            }
        }
        return list;
    }

    /**
     * return local defined attributes, not inherit from super classes
     *
     * @return List<DBATtribute> a list that includes the instances of DBAttribute
     */
    public List<DBAttribute> getLocalAttributes() {
        if (null == attributes) { // FIXME variable == null
            return new ArrayList<DBAttribute>();
        }
        List<DBAttribute> list = new ArrayList<DBAttribute>();
        for (DBAttribute attribute : attributes) {
            if (attribute.getInherit().equals(this.getUniqueName())) {
                list.add(attribute);
            }
        }
        return list;
    }

    /**
     * return class attributes inherited from super classes
     *
     * @return List<DBATtribute> a list that includes the instances of DBAttribute
     */
    public List<DBAttribute> getInheritClassAttributes() {
        if (null == classAttributes) { // FIXME variable == null
            return new ArrayList<DBAttribute>();
        }
        List<DBAttribute> list = new ArrayList<DBAttribute>();
        for (DBAttribute classAttribute : classAttributes) {
            if (!classAttribute.getInherit().equals(this.getUniqueName())) {
                list.add(classAttribute);
            }
        }
        return list;
    }

    /**
     * return attributes inherited from super classes
     *
     * @return List<DBATtribute> a list that includes the instances of DBAttribute
     */
    public List<DBAttribute> getInheritAttributes() {
        if (null == attributes) { // FIXME variable == null
            return new ArrayList<DBAttribute>();
        }
        List<DBAttribute> list = new ArrayList<DBAttribute>();
        for (DBAttribute attribute : attributes) {
            if (!attribute.getInherit().equals(this.getUniqueName())) {
                list.add(attribute);
            }
        }
        return list;
    }

    /**
     * Get the class resolutions
     *
     * @return List<DBResolution> a list that includes the instances of DBResolution
     */
    public List<DBResolution> getClassResolutions() {
        if (null == classResolutions) { // FIXME variable == null
            return new ArrayList<DBResolution>();
        }
        return classResolutions;
    }

    /**
     * Get the plain resolutions
     *
     * @return List<DBResolution> a list that includes the instances of DBResolution
     */
    public List<DBResolution> getResolutions() {
        if (null == resolutions) { // FIXME variable == null
            return new ArrayList<DBResolution>();
        } else {
            return resolutions;
        }
    }

    /**
     * Get the plain Constraint
     *
     * @return List<Constraint> a list that includes the instances of Constraint
     */
    public List<Constraint> getConstraints() {
        if (null == constraints) { // FIXME variable == null
            return new ArrayList<Constraint>();
        } else {
            return constraints;
        }
    }

    /**
     * Get the super classes
     *
     * @return List<String> a list that includes the info of super classes
     */
    public List<String> getSuperClasses() {
        if (null == superClasses) { // FIXME variable == null
            return new ArrayList<String>();
        } else {
            return superClasses;
        }
    }

    public List<String> getSubClasses() {
        return subClasses;
    }

    public List<String> getOidList() {
        return oidList;
    }

    public List<String> getQuerySpecs() {
        return querySpecs;
    }

    public void setSuperClasses(List<String> superClasses) {
        this.superClasses = superClasses;
    }

    public void setClassAttributes(List<DBAttribute> classAttributes) {
        this.classAttributes = classAttributes;
    }

    public void setAttributes(List<DBAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setClassResolutions(List<DBResolution> classResolutions) {
        this.classResolutions = classResolutions;
    }

    public void setResolutions(List<DBResolution> resolutions) {
        this.resolutions = resolutions;
    }

    public boolean isReuseOid() {
        return isReuseOid;
    }

    public void setReuseOid(boolean isReuseOid) {
        this.isReuseOid = isReuseOid;
    }

    /**
     * add resolution
     *
     * @param resolution DBResolution the given reference of DBResolution object
     * @param isClassType boolean whether is class type
     */
    public void addResolution(DBResolution resolution, boolean isClassType) {
        if (isClassType) {
            addClassResolution(resolution);
        } else {
            addResolution(resolution);
        }
    }

    /**
     * return description of a table.
     *
     * @return
     */
    public String getDescription() {
        // It shouldn't be a null value.
        return defaultString(description, "");
    }

    /**
     * set a description of a table.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasPK() {
        List<Constraint> constraints = getConstraints();
        if (constraints == null) {
            return false;
        }

        for (Constraint constraint : constraints) {
            if (ConstraintType.PRIMARYKEY.getText().equals(constraint.getType())) {
                return true;
            }
        }

        return false;
    }

    public Constraint getPK() {
        List<Constraint> constraints = getConstraints();
        if (constraints == null) {
            return null;
        }

        for (Constraint constraint : constraints) {
            if (ConstraintType.PRIMARYKEY.getText().equals(constraint.getType())) {
                return constraint;
            }
        }

        return null;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }
}
