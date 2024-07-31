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

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.DBMethod;
import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.ConstraintNamingUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.Tool;
import java.util.ArrayList;
import java.util.List;

/**
 * test SchemaInfo model
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-5 created by wuyingshi
 */
public class SchemaInfoTest extends SetupJDBCTestCase {
    String testTableName = "testSchemaInfo";
    String createTestTableSQL = null;
    private String createSuperSQL1;
    private String createSuperSQL2;

    private String classname = null;
    private String type = null;
    private String owner = null;
    private String virtual = null;
    private String dbname = null;
    private String is_partitiongroup = null;
    private String partitiongroupname = null;
    private List<DBAttribute> classAttributes = null;; // DBAttribute
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

    {
        ApplicationUtil.setApplicationType(ApplicationType.CUBRID_MANAGER);
    }

    private void createTestTable() throws Exception {
        dropTestTable();
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/model/test.Schema/sup1.txt");
        String msg = Tool.getFileContent(filepath);
        createSuperSQL1 = msg;
        String[] strs = msg.split(";");
        boolean createSup1 = true;
        if (createSuperSQL1 != null) {
            for (String str : strs) {
                if (!str.trim().equals("")) {
                    createSup1 = executeDDL(str);
                }
            }
        }
        assertTrue(createSup1);
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/model/test.Schema/sup2.txt");
        msg = Tool.getFileContent(filepath);
        createSuperSQL2 = msg;
        strs = msg.split(";");
        boolean createSup2 = true;
        if (createSuperSQL2 != null) {
            for (String str : strs) {
                if (!str.trim().equals("")) {
                    createSup2 = executeDDL(str);
                }
            }
        }
        assertTrue(createSup2);
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/model/test.Schema/testTable.txt");
        msg = Tool.getFileContent(filepath);
        createTestTableSQL = msg;
        strs = msg.split(";");
        boolean createTestTable = true;
        if (createTestTableSQL != null) {
            for (String str : strs) {
                if (!str.trim().equals("")) {
                    createTestTable = executeDDL(str);
                }
            }
        }
        assertTrue(createTestTable);
    }

    public void testGetDDL() throws Exception {
        createTestTable();
        /** init test, get schema information */
        SchemaInfo schema = databaseInfo.getSchemaInfo(testTableName);

        List<String> superClasses = schema.getSuperClasses();
        List<SchemaInfo> supers = new ArrayList<SchemaInfo>();
        for (String sup : superClasses) {
            supers.add(databaseInfo.getSchemaInfo(sup));
        }

        /** test inherit attributes */
        List<DBAttribute> inheritAttributes = schema.getInheritAttributes();
        assertEquals("smallint", inheritAttributes.get(0).getName());
        assertEquals(true, inheritAttributes.get(0).isNotNull());
        assertEquals(true, inheritAttributes.get(0).isUnique());
        assertEquals("sup1", inheritAttributes.get(0).getInherit());
        assertEquals("smallint", inheritAttributes.get(0).getType());

        assertEquals("integer", inheritAttributes.get(1).getName());
        assertEquals("bigint", inheritAttributes.get(2).getName());
        assertEquals("numeric1", inheritAttributes.get(3).getName());
        assertEquals("numeric2", inheritAttributes.get(4).getName());
        assertEquals("float", inheritAttributes.get(5).getName());
        assertEquals("setint", inheritAttributes.get(6).getName());
        assertEquals("smallint2", inheritAttributes.get(7).getName());
        assertEquals("sup2", inheritAttributes.get(7).getInherit());
        assertEquals("cache", inheritAttributes.get(8).getName());
        assertEquals("sup2", inheritAttributes.get(8).getInherit());

        assertEquals("integer", inheritAttributes.get(1).getType());
        assertEquals("bigint", inheritAttributes.get(2).getType());
        assertEquals("numeric(15,0)", inheritAttributes.get(3).getType());
        assertEquals("numeric(17,2)", inheritAttributes.get(4).getType());
        assertEquals("float", inheritAttributes.get(5).getType());
        assertEquals("set_of(smallint)", inheritAttributes.get(6).getType());
        assertEquals("smallint", inheritAttributes.get(7).getType());
        assertEquals("object", inheritAttributes.get(8).getType());

        /** test inherit class attributes */
        List<DBAttribute> inheritClassAttributes = schema.getInheritClassAttributes();
        assertEquals("float", inheritClassAttributes.get(0).getName());
        assertEquals("sup1", inheritClassAttributes.get(0).getInherit());
        assertEquals("smallint", inheritClassAttributes.get(0).getType());

        List<DBAttribute> localAttributes = schema.getLocalAttributes();
        assertEquals("code", localAttributes.get(0).getName());
        assertEquals("testschemainfo", localAttributes.get(0).getInherit());
        assertEquals("integer", localAttributes.get(0).getType());

        assertEquals("name", localAttributes.get(1).getName());
        assertEquals("character varying(40)", localAttributes.get(1).getType());

        assertEquals("gender", localAttributes.get(2).getName());
        assertEquals("character(1)", localAttributes.get(2).getType());

        assertEquals("nation_code", localAttributes.get(3).getName());
        assertEquals("character(3)", localAttributes.get(3).getType());

        assertEquals("datetime", localAttributes.get(4).getName());
        assertEquals("datetime", localAttributes.get(4).getType());

        assertEquals("timestamp", localAttributes.get(5).getName());
        assertEquals("timestamp", localAttributes.get(5).getType());

        assertEquals("time", localAttributes.get(6).getName());
        assertEquals("time", localAttributes.get(6).getType());

        assertEquals("date", localAttributes.get(7).getName());
        assertEquals("date", localAttributes.get(7).getType());

        assertEquals("char", localAttributes.get(8).getName());
        assertEquals("character(10)", localAttributes.get(8).getType());

        assertEquals("varchar", localAttributes.get(9).getName());
        assertEquals("character varying(10)", localAttributes.get(9).getType());

        assertEquals("multisetchar", localAttributes.get(10).getName());
        assertEquals("multiset_of(character varying(10))", localAttributes.get(10).getType());

        assertEquals("shared", localAttributes.get(11).getName());
        assertEquals("character varying(1073741823)", localAttributes.get(11).getType());

        assertEquals("nchar", localAttributes.get(12).getName());
        assertEquals("national character(1)", localAttributes.get(12).getType());

        assertEquals("varnchar", localAttributes.get(13).getName());
        assertEquals("national character varying(100)", localAttributes.get(13).getType());

        assertEquals("bit", localAttributes.get(14).getName());
        assertEquals("bit(1)", localAttributes.get(14).getType());

        assertEquals("varbit", localAttributes.get(15).getName());
        assertEquals("bit varying(100)", localAttributes.get(15).getType());

        /** test inherit PK */
        List<Constraint> inheritPKs = schema.getInheritPK(supers);
        Constraint iPK = inheritPKs.get(0);
        String systemPKName = ConstraintNamingUtil.getPKName("sup1", iPK.getAttributes());
        assertEquals(systemPKName, iPK.getName());
        /** test inherit FK */
        String fkName = "fk_sup2_smallint2";
        Constraint constraint = schema.getConstraintByName(fkName);
        Constraint fk = schema.getFKConstraint(fkName);
        assertTrue(constraint == fk);
        assertNotNull(fk);
        assertTrue(schema.isInSuperClasses(supers, fkName));
        /** test inherit \<Reverse\>Unique */
        String uniqueName = "u_sup1_numeric1";
        Constraint unique =
                schema.getConstraintByName(uniqueName, Constraint.ConstraintType.UNIQUE.getText());
        assertNotNull(unique);
        assertTrue(schema.isInSuperClasses(supers, uniqueName));
        /** test pk */
        Constraint pk = schema.getPK(supers);
        assertNotNull(pk);
        String pkName = ConstraintNamingUtil.getPKName(schema.getUniqueName(), pk.getAttributes());
        assertEquals(pkName, pk.getName());
    }

    /** test data */
    public void testData() {
        assertNull(classname);
        assertNull(type);
        assertNull(owner);
        assertNull(virtual);
        assertNull(dbname);
        assertNull(is_partitiongroup);
        assertNull(partitiongroupname);
        assertNull(classAttributes);
        assertNull(attributes); // DBAttribute
        assertNull(classMethods); // DBMethod
        assertNull(methods); // DBMethod
        assertNull(classResolutions); // DBResolution
        assertNull(resolutions); // DBResolution
        assertNull(constraints); // Constraint
        assertNull(superClasses); // add super classes
        assertNull(subClasses);
        assertNull(oidList);
        assertNull(methodFiles);
        assertNull(querySpecs);
        assertNull(partitions);

        SchemaInfo schemaInfo = new SchemaInfo();
        schemaInfo.getConstraints();
        schemaInfo.getDBMethodByName("name");
        schemaInfo.addResolution(new DBResolution());
        schemaInfo.addResolution(new DBResolution());
        schemaInfo.addResolution(new DBResolution(), true);
        schemaInfo.addResolution(new DBResolution(), false);
        schemaInfo.clone();
        schemaInfo.getInheritAttributes();
        schemaInfo.getLocalAttributes();
        schemaInfo.getInheritClassAttributes();
        List<SchemaInfo> supers = new ArrayList<SchemaInfo>(); // create List<SchemaInfo>
        supers.add(schemaInfo);
        schemaInfo.getPK(supers);
        schemaInfo.getConstraintByName("constraintName");
        schemaInfo.getConstraintByName("name", "type");
        schemaInfo.removeConstraintByName("name", "type");
        schemaInfo.removeUniqueByAttrName("constraintName");
        schemaInfo.getFKConstraint("name");
        schemaInfo.getFKConstraints();
        schemaInfo.removeFKConstraint("constraintName");
        schemaInfo.isAttributeUnique(new DBAttribute(), supers);
        schemaInfo.getForeignTables();

        String name = "name"; // create constraint
        String type = "FOREIGN KEY";
        int keyCount = 6;
        List<String> rules = new ArrayList<String>(); // String
        rules.add("REFERENCES aa");
        // test getters and setters
        Constraint constraintYes = new Constraint(name, type);
        constraintYes.setName(name);
        constraintYes.setType(type);
        constraintYes.setKeyCount(keyCount);
        constraintYes.setRules(rules);
        schemaInfo.addConstraint(constraintYes);
        schemaInfo.getFKConstraints();
        schemaInfo.getForeignTables();
        schemaInfo.removeFKConstraint(constraintYes);
        schemaInfo.addConstraint(constraintYes);
        schemaInfo.removeFKConstraint("name");

        schemaInfo.addConstraint(constraintYes);
        schemaInfo.addQuerySpec("name");
        schemaInfo.addMethodFile("constraintName");
        schemaInfo.addMethod(null);
        schemaInfo.addClassMethod(null);
        schemaInfo.removeConstraintByName("name", "type");
        schemaInfo.removeDBAttributeByName("name", false);
        partitions = null;
        schemaInfo.getPartitionByName("name");
        schemaInfo.isSystemClass();
        schemaInfo.setType("system");
        schemaInfo.isSystemClass();
        schemaInfo.setClassname("classname");
        schemaInfo.setUniqueName("classname");
        schemaInfo.compareTo(schemaInfo);
        schemaInfo.hashCode();
        // +test the equal()
        assertTrue(schemaInfo.equals(schemaInfo));
        assertFalse(schemaInfo.equals(null));
        assertFalse(schemaInfo.equals("other object"));
        // -test the equal()
        schemaInfo.getMethodFiles();
        schemaInfo.getClassMethods();
        schemaInfo.getMethods();
        schemaInfo.getLocalClassAttributes();
        schemaInfo.getSubClasses();
        schemaInfo.getOidList();
        List<PartitionInfo> partitiona = new ArrayList<PartitionInfo>();
        String className = "className"; // create partition
        String partitionName = "partitionName";
        @SuppressWarnings("unused")
        String partitionClassName = "partitionClassName";
        PartitionType partitionType = PartitionType.HASH;
        String partitionExpr = "partitionExpr";
        final List<String> partitionValues = new ArrayList<String>();
        int rows = -1;
        PartitionInfo partitionInfo3 =
                new PartitionInfo(
                        className,
                        partitionName,
                        partitionType,
                        partitionExpr,
                        partitionValues,
                        rows);
        partitiona.add(partitionInfo3);
        schemaInfo.setPartitionList(partitiona);
        schemaInfo.getPartitionByName("name");
        schemaInfo.getQuerySpecs();
        SchemaInfo schemaInfoOld = new SchemaInfo();
        schemaInfo.setAttributes(attributes);
        schemaInfo.setSuperClasses(superClasses);
        schemaInfo.setClassAttributes(classAttributes);

        schemaInfo.setClassResolutions(classResolutions);
        schemaInfo.setResolutions(classResolutions);
        schemaInfoOld.equals(schemaInfo);
        String sname = "name"; // create DBMethod();
        String inherit = "inherit";
        String function = "function";
        // test getters and setters
        DBMethod dbMethod = new DBMethod();
        dbMethod.setName(sname);
        dbMethod.setInherit(inherit);
        dbMethod.setFunction(function);
        DBMethod dbMethod3 = new DBMethod();
        dbMethod3.setName("notsame");
        dbMethod3.setInherit("notsame");
        dbMethod3.setFunction("notsame");
        SchemaInfo schemaInfo3 = new SchemaInfo();
        schemaInfo3.addMethod(dbMethod);
        schemaInfo3.addClassMethod(dbMethod);
        schemaInfo3.getDBMethodByName("name");
        SchemaInfo schemaInfo2 = new SchemaInfo();
        String dname = "name"; // create DBAttribute
        String dtype = "type";
        String dinherit = "inherit"; // it belongs to which class
        boolean indexed = true;
        boolean notNull = true;
        boolean shared = true;
        boolean unique = true;
        String defaultValue = "defaultValue";
        DBAttribute dbAttribute1 =
                new DBAttribute(
                        dname,
                        dtype,
                        dinherit,
                        indexed,
                        notNull,
                        shared,
                        unique,
                        defaultValue,
                        null);
        DBAttribute dbAttribute2 =
                new DBAttribute(
                        dname,
                        dtype,
                        dinherit,
                        indexed,
                        notNull,
                        shared,
                        unique,
                        defaultValue,
                        null);
        DBAttribute dbAttribute3 =
                new DBAttribute(
                        "notsame",
                        dtype,
                        dinherit,
                        indexed,
                        notNull,
                        shared,
                        unique,
                        defaultValue,
                        null);

        // +test the equal()
        SchemaInfo schemaInfo1 = new SchemaInfo();
        schemaInfo1.addAttribute(dbAttribute2);
        schemaInfo2.addAttribute(dbAttribute3);
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.addAttribute(dbAttribute2);
        schemaInfo2.removeDBAttributeByName("notsame", false);
        schemaInfo2.addAttribute(dbAttribute2);

        schemaInfo1.addClassAttribute(dbAttribute2);
        schemaInfo2.addClassAttribute(dbAttribute3);
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.addClassAttribute(dbAttribute2);
        schemaInfo2.removeDBAttributeByName("notsame", true);
        schemaInfo2.addAttribute(dbAttribute2);

        schemaInfo1.addClassMethod(dbMethod);
        schemaInfo2.addClassMethod(dbMethod3);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.equals(schemaInfo1);
        schemaInfoOld.addClassMethod(dbMethod);
        schemaInfo2.getClassMethods().clear();
        schemaInfo2.addClassMethod(dbMethod);

        String rname = "name"; // create DBResolution
        String rclassName = "className";
        String alias = "alias";
        boolean isClassResolution = true;
        DBResolution dbResolution = new DBResolution(rname, rclassName, alias);
        dbResolution.setName(rname);
        dbResolution.setTableName(rclassName);
        dbResolution.setAlias(alias);
        dbResolution.setClassResolution(isClassResolution);
        DBResolution dbResolution3 = new DBResolution(rname, rclassName, alias);
        dbResolution3.setName("notsame");
        dbResolution3.setTableName(rclassName);
        dbResolution3.setAlias(alias);
        dbResolution3.setClassResolution(isClassResolution);
        schemaInfo1.addClassResolution(dbResolution);
        schemaInfo2.addClassResolution(dbResolution3);
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.addClassResolution(dbResolution);
        schemaInfo2.getClassResolutions().clear();
        schemaInfo2.addClassResolution(dbResolution);

        schemaInfo1.setClassname("classname");
        schemaInfo1.setUniqueName("classname");
        schemaInfo2.setClassname("notsame");
        schemaInfo2.setUniqueName("notsame");
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.setClassname("classname");
        schemaInfoOld.setUniqueName("classname");
        schemaInfo2.setClassname("classname");
        schemaInfo2.setUniqueName("classname");

        schemaInfo1.setClassname("classname");
        schemaInfo1.setUniqueName("classname");
        schemaInfo2.setClassname("notsame");
        schemaInfo2.setUniqueName("notsame");

        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.setClassname("classname");
        schemaInfoOld.setUniqueName("classname");
        schemaInfo2.setClassname("classname");
        schemaInfo2.setUniqueName("classname");

        schemaInfo2.addConstraint(new Constraint(false));
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.addConstraint(constraintYes);
        schemaInfo2.getConstraints().clear();
        schemaInfo2.addConstraint(constraintYes);

        schemaInfo1.setDbname("dbname");
        schemaInfo2.setDbname("notsame");
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.setDbname("dbname");
        schemaInfo2.setDbname("dbname");

        schemaInfo1.setPartitionGroup("isPartitiongroup");
        schemaInfo2.setPartitionGroup("notsame");
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.setPartitionGroup("isPartitiongroup");
        schemaInfo2.setPartitionGroup("isPartitiongroup");

        schemaInfo1.setOwner("owner");
        schemaInfo2.setOwner("notsame");
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.setOwner("owner");
        schemaInfo2.setOwner("owner");

        schemaInfo1.addResolution(dbResolution);
        schemaInfo2.addResolution(dbResolution3);
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.addResolution(dbResolution);
        schemaInfo2.getResolutions().clear();
        schemaInfo2.addResolution(dbResolution);

        schemaInfo1.getSuperClasses().clear();
        schemaInfo1.addSuperClass("superClass");
        schemaInfo2.addSuperClass("notsame");
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.addSuperClass("superClass");
        schemaInfo2.getSuperClasses().clear();
        schemaInfo2.addSuperClass("superClass");

        schemaInfo1.setType("type");
        schemaInfo2.setType("notsame");
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.setType("type");
        schemaInfo2.setType("type");

        schemaInfo1.setVirtual("virtual");
        schemaInfo2.setVirtual("notsame");
        schemaInfoOld.equals(schemaInfo1);
        schemaInfo2.equals(schemaInfo1);
        schemaInfoOld.setVirtual("virtual");
        schemaInfo2.setVirtual("virtual");
        // +test the equal()

        schemaInfo.addDBAttribute(dbAttribute2, true);
        schemaInfo.addDBAttribute(dbAttribute2, false);
        schemaInfo.replaceDBAttributeByName(dbAttribute1, dbAttribute2, true, supers);
        schemaInfo.replaceDBAttributeByName(dbAttribute1, dbAttribute2, false, supers);
        schemaInfo.setClassname("inherit");
        schemaInfo.setUniqueName("inherit");
        schemaInfo.getLocalClassAttributes();
        constraintYes.setType("UNIQUE");
        List<String> a = new ArrayList<String>();
        a.add("name");
        constraintYes.setAttributes(a);
        schemaInfo.addConstraint(constraintYes);
        schemaInfo.removeUniqueByAttrName("name");
    }

    /** drop table */
    private void dropTestTable() {
        String sql = "drop table \"" + testTableName + "\"";
        executeDDL(sql);
        sql = "drop table sup2";
        executeDDL(sql);
        sql = "drop table sup1";
        executeDDL(sql);
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTable();
        super.tearDown();
    }
}
