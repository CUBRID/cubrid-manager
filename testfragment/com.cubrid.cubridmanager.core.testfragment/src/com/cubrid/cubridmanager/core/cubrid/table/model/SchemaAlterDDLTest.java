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
import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.ConstraintNamingUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.Tool;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog.SchemeInnerType;
import java.util.ArrayList;
import java.util.List;

/**
 * test getting alter table statement
 *
 * @author moulinwang
 * @version 1.0 - 2009-7-1 created by moulinwang
 */
public class SchemaAlterDDLTest extends SetupJDBCTestCase {
    String testTableName = "testSchemaAlterDDL";
    String createTestTableSQL = null;
    private String createSuperSQL1;
    private String createSuperSQL2;
    private SchemaInfo sup2;
    private SchemaChangeManager changeList;
    private SchemaDDL ddl;
    private SchemaInfo testedSchemaInfo;
    private String createSuperSQL3;
    private List<SchemaInfo> superList;
    private SchemaInfo sup3;

    {
        ApplicationUtil.setApplicationType(ApplicationType.CUBRID_MANAGER);
    }

    private void createTestTable() throws Exception {
        String filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/model/test.alter.schema/sup1.txt");
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
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/model/test.alter.schema/sup2.txt");
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
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/model/test.alter.schema/sup3.txt");
        msg = Tool.getFileContent(filepath);
        createSuperSQL3 = msg;
        strs = msg.split(";");
        if (createSuperSQL3 != null) {
            for (String str : strs) {
                if (!str.trim().equals("")) {
                    executeDDL(str);
                }
            }
        }
        filepath =
                this.getFilePathInPlugin(
                        "/com/cubrid/cubridmanager/core/cubrid/table/model/test.alter.schema/testAlterTable.txt");
        msg = Tool.getFileContent(filepath);
        createTestTableSQL = msg;
        strs = msg.split(";");
        if (createTestTableSQL != null) {
            for (String str : strs) {
                if (!str.trim().equals("")) {
                    executeDDL(str);
                }
            }
        }
        //		filepath =
        // this.getFilePathInPlugin("com/cubrid/cubridmanager/core/cubrid/table/model/test.message/testAlterResult");
        //		msg = Tool.getFileContent(filepath);

    }

    private boolean dropTestTable() {
        String sql = "drop table \"" + testTableName + "\"";
        executeDDL(sql);
        sql = "drop table sup3";
        executeDDL(sql);
        sql = "drop table sup2";
        executeDDL(sql);
        sql = "drop table sup1";
        return executeDDL(sql);
    }

    public void testGetAlterDDL() throws Exception {
        createTestTable();
        databaseInfo.getSchemaInfo("sup1");
        sup2 = databaseInfo.getSchemaInfo("sup2");
        sup3 = databaseInfo.getSchemaInfo("sup3");
        testedSchemaInfo = databaseInfo.getSchemaInfo(testTableName);
        superList = SuperClassUtil.getSuperClasses(databaseInfo, testedSchemaInfo);
        ownerTest();
        superAndResolutionTest();
        columnTest();
        pkTest();
        fkTest1();
        fkTest2();
        indexTest1();
        indexTest2();
    }

    // add indexes, then drop them
    private void indexTest1() {
        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        SchemaInfo newSchema = sup3.clone();

        Constraint constraint = null;
        String indexType = null;
        String indexName = "";
        String tableName = newSchema.getUniqueName();

        // add unique
        constraint = new Constraint(false);
        indexType = "UNIQUE";
        constraint.setType(indexType);

        constraint.addAttribute("integer");
        constraint.addRule("integer DESC");

        constraint.addAttribute("bigint");
        constraint.addRule("bigint ASC");

        if (indexName.equals("")) { // $NON-NLS-1$
            indexName = constraint.getDefaultName(tableName);
        }
        constraint.setName(indexName);

        newSchema.addConstraint(constraint);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(
                        null,
                        constraint.getDefaultName(newSchema.getUniqueName())
                                + "$"
                                + constraint.getName(), // $NON-NLS-1$
                        SchemeInnerType.TYPE_INDEX));

        // add index
        constraint = new Constraint(false);
        indexType = "INDEX";
        constraint.setType(indexType);

        constraint.addAttribute("numeric1");
        constraint.addRule("numeric1 ASC");

        constraint.addAttribute("numeric2");
        constraint.addRule("numeric2 DESC");

        constraint.addAttribute("float");
        constraint.addRule("float ASC");

        indexName = constraint.getDefaultName(tableName);
        constraint.setName(indexName);

        newSchema.addConstraint(constraint);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(
                        null,
                        constraint.getDefaultName(newSchema.getUniqueName())
                                + "$"
                                + constraint.getName(), // $NON-NLS-1$
                        SchemeInnerType.TYPE_INDEX));

        // add reverse unique
        constraint = new Constraint(false);
        indexType = "REVERSE UNIQUE";
        constraint.setType(indexType);

        constraint.addAttribute("numeric2");
        constraint.addRule("numeric2 DESC");

        constraint.addAttribute("float");
        constraint.addRule("float DESC");

        constraint.addAttribute("setint");
        constraint.addRule("setint DESC");

        indexName = constraint.getDefaultName(tableName);
        constraint.setName(indexName);

        newSchema.addConstraint(constraint);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(
                        null,
                        constraint.getDefaultName(newSchema.getUniqueName())
                                + "$"
                                + constraint.getName(), // $NON-NLS-1$
                        SchemeInnerType.TYPE_INDEX));

        // add reverse index
        constraint = new Constraint(false);
        indexType = "REVERSE INDEX";
        constraint.setType(indexType);

        constraint.addAttribute("integer");
        constraint.addRule("integer DESC");

        constraint.addAttribute("bigint");
        constraint.addRule("bigint DESC");

        indexName = constraint.getDefaultName(tableName);
        constraint.setName(indexName);

        newSchema.addConstraint(constraint);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(
                        null,
                        constraint.getDefaultName(newSchema.getUniqueName())
                                + "$"
                                + constraint.getName(), // $NON-NLS-1$
                        SchemeInnerType.TYPE_INDEX));

        String expected =
                "CREATE UNIQUE INDEX u_sup3_integer_d_bigint ON sup3([integer] DESC,[bigint]);"
                        + StringUtil.NEWLINE;
        expected +=
                "CREATE INDEX i_sup3_numeric1_numeric2_d_float ON sup3(numeric1,numeric2 DESC,[float]);"
                        + StringUtil.NEWLINE;
        expected +=
                "CREATE REVERSE UNIQUE INDEX ru_sup3_numeric2_float_setint ON sup3(numeric2 DESC,[float] DESC,setint DESC);"
                        + StringUtil.NEWLINE;
        expected +=
                "CREATE REVERSE INDEX ri_sup3_integer_bigint ON sup3([integer] DESC,[bigint] DESC);";

        String alterDDL = ddl.getAlterDDL(sup3, newSchema);
        assertEquals(expected, alterDDL.trim());
    }

    // drop exist index
    private void indexTest2() {
        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        SchemaInfo newSchema = sup2.clone();

        String indexName = null;
        String indexType = null;

        indexName = "u_sup2_date_d";
        indexType = "UNIQUE";
        Constraint index = newSchema.getConstraintByName(indexName, indexType);
        newSchema.removeConstraintByName(indexName, indexType);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(
                        index.getDefaultName(newSchema.getUniqueName()) + "$" + index.getName(),
                        null, //$NON-NLS-1$
                        SchemeInnerType.TYPE_INDEX));

        indexName = "i_sup2_bigint_numeric1";
        indexType = "INDEX";
        index = newSchema.getConstraintByName(indexName, indexType);
        newSchema.removeConstraintByName(indexName, indexType);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(
                        index.getDefaultName(newSchema.getUniqueName()) + "$" + index.getName(),
                        null, //$NON-NLS-1$
                        SchemeInnerType.TYPE_INDEX));

        indexName = "ru_sup2_numeric2_float";
        indexType = "REVERSE UNIQUE";
        index = newSchema.getConstraintByName(indexName, indexType);
        newSchema.removeConstraintByName(indexName, indexType);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(
                        index.getDefaultName(newSchema.getUniqueName()) + "$" + index.getName(),
                        null, //$NON-NLS-1$
                        SchemeInnerType.TYPE_INDEX));

        indexName = "ri_sup2_numeric1_float";
        indexType = "REVERSE INDEX";
        index = newSchema.getConstraintByName(indexName, indexType);
        newSchema.removeConstraintByName(indexName, indexType);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(
                        index.getDefaultName(newSchema.getUniqueName()) + "$" + index.getName(),
                        null, //$NON-NLS-1$
                        SchemeInnerType.TYPE_INDEX));

        String expected = "ALTER TABLE sup2 DROP UNIQUE INDEX u_sup2_date_d;" + StringUtil.NEWLINE;
        expected += "ALTER TABLE sup2 DROP INDEX i_sup2_bigint_numeric1;" + StringUtil.NEWLINE;
        expected +=
                "ALTER TABLE sup2 DROP REVERSE UNIQUE INDEX ru_sup2_numeric2_float;"
                        + StringUtil.NEWLINE;
        expected += "ALTER TABLE sup2 DROP REVERSE INDEX ri_sup2_numeric1_float;";

        String alterDDL = ddl.getAlterDDL(sup2, newSchema);
        assertEquals(expected, alterDDL.trim());
    }

    // add a new fk, then delete it
    private void fkTest1() {
        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        SchemaInfo newSchema = sup3.clone();

        Constraint fk = null;
        String foreignTable = "sup2";
        String deleteRule = "RESTRICT";
        String updateRule = "RESTRICT";

        fk = new Constraint(false);
        fk.setType(Constraint.ConstraintType.FOREIGNKEY.getText());
        fk.addAttribute("numeric1");
        fk.addAttribute("numeric2");

        String fkName = "";
        if (fkName.equals("")) { // $NON-NLS-1$
            fkName = ConstraintNamingUtil.getFKName(newSchema.getUniqueName(), fk.getAttributes());
        }
        fk.setName(fkName);

        fk.addRule("REFERENCES " + foreignTable); // $NON-NLS-1$	
        fk.addRule("ON DELETE " + deleteRule);
        fk.addRule("ON UPDATE " + updateRule);
        fk.addRule("ON CACHE OBJECT " + "cachedtable");

        newSchema.addConstraint(fk);
        changeList.addSchemeChangeLog(
                new SchemaChangeLog(null, fk.getName(), SchemeInnerType.TYPE_FK));
        String expected =
                "ALTER TABLE sup3 ADD FOREIGN KEY (numeric1,numeric2) "
                        + "REFERENCES sup2(numeric1,numeric2) "
                        + "ON DELETE RESTRICT ON UPDATE RESTRICT ON CACHE OBJECT cachedtable;";
        String alterDDL = ddl.getAlterDDL(sup3, newSchema);
        assertEquals(expected, alterDDL.trim());

        // drop this new added fk
        newSchema.removeFKConstraint(fkName);
        changeList.addSchemeChangeLog(new SchemaChangeLog(fkName, null, SchemeInnerType.TYPE_FK));
        expected = "";
        alterDDL = ddl.getAlterDDL(sup3, newSchema);
        assertEquals(expected, alterDDL.trim());
    }

    // drop an exist fk
    private void fkTest2() {
        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        SchemaInfo newSchema = sup2.clone();
        String fkName = "fk_sup2_smallint2";

        // drop this new added fk
        newSchema.removeFKConstraint(fkName);
        changeList.addSchemeChangeLog(new SchemaChangeLog(fkName, null, SchemeInnerType.TYPE_FK));
        String expected = "ALTER TABLE sup2 DROP CONSTRAINT fk_sup2_smallint2;";
        String alterDDL = ddl.getAlterDDL(sup2, newSchema);
        assertEquals(expected, alterDDL.trim());
    }

    private void pkTest() {
        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        SchemaInfo newSchema = sup3.clone();

        Constraint newPK = new Constraint(false);
        newPK.setType(Constraint.ConstraintType.PRIMARYKEY.getText());
        newPK.addAttribute("smallint");
        newPK.addAttribute("integer");
        String op = "ADD";
        resetPK(newSchema, null, newPK, op);

        String alterDDL = ddl.getAlterDDL(sup3, newSchema);
        String expected = "ALTER TABLE sup3 ADD PRIMARY KEY([smallint],[integer]);";
        //		assertEquals(expected, alterDDL.trim());
        assertTrue(alterDDL.trim().indexOf(expected) > -1);

        /*	ALTER TABLE sup3 CHANGE COLUMN [smallint] [smallint] smallint AUTO_INCREMENT(1,1) NOT NULL;
        ALTER TABLE sup3 CHANGE COLUMN [integer] [integer] integer AUTO_INCREMENT(2,1) NOT NULL;
        ALTER TABLE sup3 ADD PRIMARY KEY([smallint],[integer]);
        */

        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        newSchema = testedSchemaInfo.clone();

        String testColumn = "pkattr";
        boolean isClassAttribute = false;
        DBAttribute attr = new DBAttribute();
        attr.setName(testColumn);
        attr.setType("smallint");
        attr.setInherit(newSchema.getUniqueName());
        addColumn(newSchema, isClassAttribute, attr);

        Constraint oldPK = newSchema.getPK(superList);
        newPK = oldPK.clone();
        newPK.addAttribute(testColumn);
        op = "MODIFY";
        resetPK(newSchema, oldPK, newPK, op);

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected =
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " DROP CONSTRAINT pk_testschemaalterddl_name_gender;"
                        + StringUtil.NEWLINE;
        expected +=
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " ADD COLUMN pkattr smallint NOT NULL AFTER [shared];"
                        + StringUtil.NEWLINE;
        expected +=
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " ADD CONSTRAINT pk_testschemaalterddl_name_gender PRIMARY KEY ([name],gender,pkattr);";
        assertEquals(expected, alterDDL.trim());

        // drop pk
        oldPK = newPK;
        newPK = null;
        op = "DEL";
        resetPK(newSchema, oldPK, newPK, op);

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected =
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " DROP CONSTRAINT pk_testschemaalterddl_name_gender;"
                        + StringUtil.NEWLINE;

        assertTrue(alterDDL.trim().indexOf(expected) > -1);

        expected =
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " ADD COLUMN pkattr smallint AFTER [shared];";
        // assertEquals(expected, alterDDL.trim());
        assertTrue(alterDDL.trim().indexOf(expected) > -1);
    }

    /**
     * reset PK
     *
     * @param newSchema
     * @param oldPK
     * @param newPK
     * @param op
     */
    private void resetPK(SchemaInfo newSchema, Constraint oldPK, Constraint newPK, String op) {
        if (op.equals("ADD")) { // $NON-NLS-1$
            newSchema.addConstraint(newPK);
            firePKAdded(newSchema, newPK);
        } else if (op.equals("DEL")) { // $NON-NLS-1$
            newSchema.getConstraints().remove(oldPK);
            firePKRemoved(newSchema, oldPK);
        } else if (op.equals("MODIFY")) { // $NON-NLS-1$
            newSchema.getConstraints().remove(oldPK);
            firePKRemoved(newSchema, oldPK);
            newSchema.addConstraint(newPK);
            firePKAdded(newSchema, newPK);
        }
    }

    private void ownerTest() {
        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        SchemaInfo newSchema = testedSchemaInfo.clone();
        newSchema.setOwner("PUBLIC");
        String expected =
                "CALL CHANGE_OWNER ('testschemaalterddl','PUBLIC') ON CLASS db_authorizations";
        String alterDDL = ddl.getChangeOwnerDDL(testTableName.toLowerCase(), "PUBLIC");
        assertEquals(expected, alterDDL.trim());
        newSchema.setOwner("DBA");
    }

    private void columnTest() {
        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        SchemaInfo newSchema = testedSchemaInfo.clone();

        String alterDDL = null;
        boolean isClassAttribute = false;
        DBAttribute attr = null;
        String expected = null;

        // "smallint" smallint AUTO_INCREMENT PRIMARY KEY is inherited from "sup1",
        // now, add attribute "smallint smallint"
        String testColumn = "smallint";
        isClassAttribute = false;
        attr = new DBAttribute();
        attr.setName(testColumn);
        attr.setType("smallint");

        attr.setUnique(false);
        attr.setInherit(newSchema.getUniqueName());

        addColumn(newSchema, isClassAttribute, attr);

        DBAttribute findAttr = newSchema.getDBAttributeByName(testColumn, isClassAttribute);
        assertNotNull(findAttr);
        assertFalse(newSchema.isAttributeUnique(findAttr, superList));
        assertNull(findAttr.getAutoIncrement());

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected =
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " ADD COLUMN [smallint] smallint AFTER [shared];";
        assertEquals(expected, alterDDL.trim());

        // edit the column again, rename the column
        DBAttribute oldAttribute = findAttr;
        DBAttribute editAttribute = oldAttribute.clone();
        String newColumnName = "smallintlocal";
        editAttribute.setName(newColumnName);
        boolean isEditAll = true;
        editColumn(newSchema, editAttribute, oldAttribute, isEditAll);

        // expected inherited column exist
        findAttr = newSchema.getDBAttributeByName(testColumn, isClassAttribute);
        assertNotNull(findAttr);
        assertTrue(newSchema.isAttributeUnique(findAttr, superList));
        assertNotNull(findAttr.getAutoIncrement());

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected =
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " ADD COLUMN smallintlocal smallint AFTER [shared];";
        assertEquals(expected, alterDDL.trim());

        // drop the renamed column, expected no change
        String attrName = newColumnName;
        isClassAttribute = false;
        dropColumn(newSchema, isClassAttribute, attrName);

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected = "";
        assertEquals(expected, alterDDL.trim());

        // edit the column again, rename the column
        testColumn = "timestamp";
        oldAttribute = newSchema.getDBAttributeByName(testColumn, isClassAttribute);
        editAttribute = oldAttribute.clone();
        newColumnName = "newtimestamp";
        editAttribute.setName(newColumnName);
        editAttribute.setDefault("TIMESTAMP'12/01/2000 03:07:51'");
        isEditAll = false;
        editColumn(newSchema, editAttribute, oldAttribute, isEditAll);

        // expected inherited column exist
        findAttr = newSchema.getDBAttributeByName(newColumnName, isClassAttribute);
        assertNotNull(findAttr);
        assertEquals("TIMESTAMP'12/01/2000 03:07:51'", editAttribute.getDefault());

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected =
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " RENAME [timestamp] AS newtimestamp;"
                        + StringUtil.NEWLINE;
        expected +=
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " ALTER newtimestamp SET DEFAULT TIMESTAMP'2000-12-01 03:07:51';";
        assertEquals(expected, alterDDL.trim());

        // drop this column, expected drop this column
        attrName = newColumnName;
        isClassAttribute = false;
        dropColumn(newSchema, isClassAttribute, attrName);

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected = "ALTER TABLE " + testTableName.toLowerCase() + " DROP  COLUMN [timestamp];";
        assertEquals(expected, alterDDL.trim());

        // add class column ff2
        testColumn = "ff2";
        isClassAttribute = true;
        attr = new DBAttribute();
        attr.setName(testColumn);
        attr.setType("smallint");
        attr.setInherit(newSchema.getUniqueName());

        addColumn(newSchema, isClassAttribute, attr);

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected =
                "ALTER TABLE "
                        + testTableName.toLowerCase()
                        + " ADD CLASS ATTRIBUTE ff2 smallint;"
                        + StringUtil.NEWLINE
                        + expected;
        assertEquals(expected.trim(), alterDDL.trim());

        // edit class column ff, rename the column to ff3
        testColumn = "ff";
        oldAttribute = newSchema.getDBAttributeByName(testColumn, isClassAttribute);
        editAttribute = oldAttribute.clone();
        newColumnName = "ff3";
        editAttribute.setName(newColumnName);
        editAttribute.setDefault("4");
        isEditAll = false;
        editColumn(newSchema, editAttribute, oldAttribute, isEditAll);

        findAttr = newSchema.getDBAttributeByName(newColumnName, isClassAttribute);
        assertNotNull(findAttr);
        assertEquals("4", editAttribute.getDefault());

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected = "ALTER TABLE " + testTableName.toLowerCase() + " RENAME CLASS ff AS ff3;";
        assertNotSame(-1, alterDDL.indexOf(expected));
        expected = "ALTER TABLE " + testTableName.toLowerCase() + " ALTER CLASS ff3 SET DEFAULT 4;";
        assertNotSame(expected, alterDDL);

        // drop this column ff3, expected drop this column
        attrName = newColumnName;
        isClassAttribute = true;
        dropColumn(newSchema, isClassAttribute, attrName);

        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        expected = "ALTER TABLE " + testTableName.toLowerCase() + " DROP ATTRIBUTE CLASS ff;";
        assertNotSame(-1, alterDDL.indexOf(expected));
    }

    /**
     * drop a column
     *
     * @param newSchema
     * @param isClassAttribute
     * @param attrName
     */
    private void dropColumn(SchemaInfo newSchema, boolean isClassAttribute, String attrName) {
        DBAttribute oldAttribute;
        oldAttribute = newSchema.getDBAttributeByName(attrName, isClassAttribute);
        if (!isClassAttribute) {
            newSchema.getAttributes().remove(oldAttribute);
            newSchema.removeUniqueByAttrName(attrName);
        } else {
            newSchema.getClassAttributes().remove(oldAttribute);
        }
        SuperClassUtil.fireSuperClassChanged(
                databaseInfo, testedSchemaInfo, newSchema, newSchema.getSuperClasses());
        String oldAttrName = oldAttribute.getName();
        addDropAttrLog(oldAttrName, isClassAttribute);
    }

    /**
     * edit a column
     *
     * @param newSchema
     * @param superList
     * @param editAttribute
     * @param oldAttribute
     * @param isEditAll
     */
    private void editColumn(
            SchemaInfo newSchema,
            DBAttribute editAttribute,
            DBAttribute oldAttribute,
            boolean isEditAll) {
        if (editAttribute != null) {
            editAttribute.setInherit(newSchema.getUniqueName());
        } else {
            return;
        }
        String newAttrName = editAttribute.getName();
        boolean isNewAttrClass = editAttribute.isClassAttribute();
        boolean isOldAttrClass = oldAttribute.isClassAttribute();
        String attrName = oldAttribute.getName();
        String tableName = newSchema.getUniqueName();
        if (isEditAll) {

            if (isOldAttrClass != isNewAttrClass) { // attribute
                // type
                // changed
                newSchema.removeDBAttributeByName(attrName, isOldAttrClass);
                addDropAttrLog(attrName, isOldAttrClass);

                newSchema.addDBAttribute(editAttribute, isNewAttrClass);
                addNewAttrLog(newAttrName, isNewAttrClass);
            } else {
                newSchema.replaceDBAttributeByName(
                        oldAttribute, editAttribute, isNewAttrClass, superList);
                addEditAttrLog(attrName, newAttrName, isNewAttrClass);
            }
            if (!oldAttribute.isUnique() && editAttribute.isUnique()) {
                Constraint unique = new Constraint(false);
                unique.setType(Constraint.ConstraintType.UNIQUE.getText());

                unique.addAttribute(newAttrName);
                unique.addRule(newAttrName + " ASC");
                unique.setName(ConstraintNamingUtil.getUniqueName(tableName, unique.getRules()));

                newSchema.addConstraint(unique);
            } else if (oldAttribute.isUnique() && !editAttribute.isUnique()) {
                newSchema.removeUniqueByAttrName(attrName);
            }

        } else {
            newSchema.replaceDBAttributeByName(
                    oldAttribute, editAttribute, isNewAttrClass, superList);
            addEditAttrLog(attrName, newAttrName, isNewAttrClass);
        }
        SuperClassUtil.fireSuperClassChanged(
                databaseInfo, testedSchemaInfo, newSchema, newSchema.getSuperClasses());
    }

    /**
     * add a column
     *
     * @param newSchema
     * @param newAttrName
     * @param isClassAttribute
     * @param addAttribute
     */
    private void addColumn(
            SchemaInfo newSchema, boolean isClassAttribute, DBAttribute addAttribute) {
        String newAttrName = addAttribute.getName();
        String tableName = newSchema.getUniqueName();
        if (addAttribute != null) {
            addAttribute.setInherit(tableName);
        } else {
            return;
        }
        newSchema.removeDBAttributeByName(newAttrName, isClassAttribute);
        newSchema.addDBAttribute(addAttribute, isClassAttribute);
        if (addAttribute.isUnique()) {
            Constraint unique = new Constraint(false);
            unique.setType(Constraint.ConstraintType.UNIQUE.getText());
            unique.addAttribute(newAttrName);
            unique.addRule(newAttrName + " ASC");
            unique.setName(ConstraintNamingUtil.getUniqueName(tableName, unique.getRules()));

            newSchema.addConstraint(unique);
        }
        if (!isClassAttribute) {
            changeList.addSchemeChangeLog(
                    new SchemaChangeLog(null, newAttrName, SchemeInnerType.TYPE_ATTRIBUTE));
        } else {
            changeList.addSchemeChangeLog(
                    new SchemaChangeLog(null, newAttrName, SchemeInnerType.TYPE_CLASSATTRIBUTE));
        }
    }

    /**
     * test table inherits table "sup1", then add super class "sup2";
     *
     * @return
     */
    private void superAndResolutionTest() {
        changeList = new SchemaChangeManager(databaseInfo, false);
        ddl = new SchemaDDL(changeList, databaseInfo);
        SchemaInfo newSchema = testedSchemaInfo.clone();

        String alterDDL = null;

        List<String> newSuperclass = new ArrayList<String>();
        // add sup2 and sup3
        newSuperclass.add("sup1");
        newSuperclass.add("sup2");
        newSuperclass.add("sup3");
        boolean success =
                SuperClassUtil.fireSuperClassChanged(
                        databaseInfo, testedSchemaInfo, newSchema, newSuperclass);
        assertTrue(success);
        newSchema.setSuperClasses(newSuperclass);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(
                alterDDL.indexOf("ALTER TABLE testschemaalterddl ADD SUPERCLASS sup2,sup3") > -1);
        assertTrue(alterDDL.indexOf("INHERIT CLASS [float] OF sup1") > -1);
        assertTrue(alterDDL.indexOf("[bigint] OF sup1") > -1);
        assertTrue(alterDDL.indexOf("[float] OF sup1") > -1);
        assertTrue(alterDDL.indexOf("numeric1 OF sup1") > -1);
        assertTrue(alterDDL.indexOf("numeric2 OF sup1") > -1);

        String type = "";
        String superTable = "";
        String column = "";
        String alias = "";
        boolean isClassType = false;

        // add "numeric1" OF "sup2", expected "numeric1" OF "sup1" dropped
        column = "numeric1";
        superTable = "sup2";
        alias = "";
        isClassType = false;
        addResolution(newSchema, column, superTable, alias, isClassType);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("numeric1 OF sup2") > -1);
        assertTrue(alterDDL.indexOf("numeric1 OF sup1") == -1);

        // add alias "numeric1" OF "sup3",
        // expected "numeric1" OF "sup2" exist, and alias resolution exist too
        column = "numeric1";
        superTable = "sup3";
        alias = "numeric1sup3";
        isClassType = false;
        addResolution(newSchema, column, superTable, alias, isClassType);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("numeric1 OF sup2") > -1);
        assertTrue(alterDDL.indexOf("numeric1 OF sup3 AS numeric1sup3") > -1);

        // drop "numeric1" OF "sup2", expected "numeric1" OF "sup1"
        type = "";
        superTable = "sup2";
        column = "numeric1";
        removeResolution(newSchema, type, superTable, column);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("numeric1 OF sup1") > -1);

        // drop "numeric1" OF "sup1", expected "numeric1" OF "sup2"
        type = "";
        superTable = "sup1";
        column = "numeric1";
        removeResolution(newSchema, type, superTable, column);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("numeric1 OF sup2") > -1);

        // add alias "numeric1" OF "sup2",
        // expected "numeric1" OF "sup1" exist, and alias resolution of(sup2,sup3) exist too
        column = "numeric1";
        superTable = "sup2";
        alias = "numeric1sup2";
        isClassType = false;
        addResolution(newSchema, column, superTable, alias, isClassType);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("numeric1 OF sup1") > -1);
        assertTrue(alterDDL.indexOf("numeric1 OF sup2 AS numeric1sup2") > -1);
        assertTrue(alterDDL.indexOf("numeric1 OF sup3 AS numeric1sup3") > -1);

        // drop "bigint" OF "sup1", expected "bigint" OF "sup2"
        type = "";
        superTable = "sup1";
        column = "bigint";
        removeResolution(newSchema, type, superTable, column);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("[bigint] OF sup2") > -1);

        // drop "bigint" OF "sup2", expected "bigint" OF "sup3"
        type = "";
        superTable = "sup2";
        column = "bigint";
        removeResolution(newSchema, type, superTable, column);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("[bigint] OF sup3") > -1);

        // drop "bigint" OF "sup3", expected "bigint" OF "sup1"
        type = "";
        superTable = "sup3";
        column = "bigint";
        removeResolution(newSchema, type, superTable, column);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("[bigint] OF sup1") > -1);

        // drop Class "float" OF "sup1", expected Class "float" OF "sup2"
        type = "Class";
        superTable = "sup1";
        column = "float";
        removeResolution(newSchema, type, superTable, column);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("CLASS [float] OF sup2") > -1);

        // drop Class "float" OF "sup2", expected Class "float" OF "sup3"
        type = "Class";
        superTable = "sup2";
        column = "float";
        removeResolution(newSchema, type, superTable, column);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("CLASS [float] OF sup3") > -1);

        // drop Class "float" OF "sup3", expected Class "float" OF "sup1"
        type = "Class";
        superTable = "sup3";
        column = "float";
        removeResolution(newSchema, type, superTable, column);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("CLASS [float] OF sup1") > -1);

        // drop sup1
        newSuperclass.clear();
        success =
                SuperClassUtil.fireSuperClassChanged(
                        databaseInfo, testedSchemaInfo, newSchema, newSuperclass);
        assertTrue(success);
        newSchema.setSuperClasses(newSuperclass);
        alterDDL = ddl.getAlterDDL(testedSchemaInfo, newSchema);
        assertTrue(alterDDL.indexOf("ALTER TABLE testschemaalterddl DROP SUPERCLASS sup1;") > -1);
    }

    /**
     * add a resolution
     *
     * @param newSchema
     * @param column
     * @param superTable
     * @param alias
     * @param isClassType
     */
    private void addResolution(
            SchemaInfo newSchema,
            String column,
            String superTable,
            String alias,
            boolean isClassType) {
        DBResolution newResolution = new DBResolution(column, superTable, alias);
        newResolution.setClassResolution(isClassType);
        String tbl = newResolution.getTableName();
        if (newResolution != null) {
            List<DBResolution> resolutions = null;
            if (isClassType) {
                resolutions = newSchema.getClassResolutions();
            } else {
                resolutions = newSchema.getResolutions();
            }
            if (alias.equals("")) {
                for (int i = resolutions.size() - 1; i >= 0; i--) {
                    DBResolution r = resolutions.get(i);
                    // remove resolution
                    if (r.getName().equals(column)) {
                        if (r.getAlias().equals("")) { // $NON-NLS-1$
                            resolutions.remove(i);
                        }
                    }
                }
            } else {
                for (int i = resolutions.size() - 1; i >= 0; i--) {
                    DBResolution r = resolutions.get(i);
                    // remove resolution
                    if (r.getName().equals(column) && r.getTableName().equals(tbl)) {
                        resolutions.remove(i);
                    }
                }
            }
            if (isClassType) {
                newSchema.addClassResolution(newResolution);
                SuperClassUtil.fireResolutionChanged(
                        databaseInfo, testedSchemaInfo, newSchema, true);
            } else {
                newSchema.addResolution(newResolution);
                SuperClassUtil.fireResolutionChanged(
                        databaseInfo, testedSchemaInfo, newSchema, false);
            }
        }
    }

    /**
     * remove a resolution
     *
     * @param newSchema
     * @param type
     * @param superTable
     * @param column
     * @return
     */
    private void removeResolution(
            SchemaInfo newSchema, String type, String superTable, String column) {
        List<DBResolution> resolutions = null;
        DBResolution removedResolution = null;
        if (type.equals("Class")) { // $NON-NLS-1$
            resolutions = newSchema.getClassResolutions();
        } else {
            resolutions = newSchema.getResolutions();
        }
        for (int i = 0; i < resolutions.size(); i++) {
            DBResolution r = resolutions.get(i);
            if (r.getName().equals(column) && r.getTableName().equals(superTable)) {
                removedResolution = resolutions.remove(i);
            }
        }
        if (removedResolution.getAlias().equals("")) {
            List<String[]> columnConflicts = null;
            boolean isClassType;
            if (type.equals("Class")) { // $NON-NLS-1$
                isClassType = true;
            } else {
                isClassType = false;
            }
            columnConflicts =
                    SuperClassUtil.getColumnConflicts(
                            databaseInfo, newSchema, newSchema.getSuperClasses(), isClassType);
            DBResolution nextResolution =
                    SuperClassUtil.getNextResolution(
                            resolutions, removedResolution, columnConflicts);
            assert (nextResolution != null);
            newSchema.addResolution(nextResolution, isClassType);

            SuperClassUtil.fireResolutionChanged(
                    databaseInfo, testedSchemaInfo, newSchema, isClassType);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTable();
        super.tearDown();
    }

    private void addDropAttrLog(String oldAttrName, boolean isClassAttribute) {
        if (!isClassAttribute) {
            changeList.addSchemeChangeLog(
                    new SchemaChangeLog(oldAttrName, null, SchemeInnerType.TYPE_ATTRIBUTE));
        } else {
            changeList.addSchemeChangeLog(
                    new SchemaChangeLog(oldAttrName, null, SchemeInnerType.TYPE_CLASSATTRIBUTE));
        }
    }

    private void addNewAttrLog(String newAttrName, boolean isClassAttribute) {
        if (!isClassAttribute) {
            changeList.addSchemeChangeLog(
                    new SchemaChangeLog(null, newAttrName, SchemeInnerType.TYPE_ATTRIBUTE));
        } else {
            changeList.addSchemeChangeLog(
                    new SchemaChangeLog(null, newAttrName, SchemeInnerType.TYPE_CLASSATTRIBUTE));
        }
    }

    private void addEditAttrLog(String attrName, String newAttrName, boolean isClassAttribute) {
        if (!isClassAttribute) {
            changeList.addSchemeChangeLog(
                    new SchemaChangeLog(attrName, newAttrName, SchemeInnerType.TYPE_ATTRIBUTE));
        } else {
            changeList.addSchemeChangeLog(
                    new SchemaChangeLog(
                            attrName, newAttrName, SchemeInnerType.TYPE_CLASSATTRIBUTE));
        }
    }

    private void firePKAdded(SchemaInfo newSchema, Constraint newPK) {
        List<String> attrList = newPK.getAttributes();
        if (attrList.size() == 1) {
            String attr = attrList.get(0);
            DBAttribute a = newSchema.getDBAttributeByName(attr, false);
            boolean changed = false;
            if (!a.isNotNull()) {
                a.setNotNull(true);
                changed = true;
            }
            if (!a.isUnique()) {
                a.setUnique(true);
                changed = true;
            }
            if (changed) {
                changeList.addSchemeChangeLog(
                        new SchemaChangeLog(
                                a.getName(), a.getName(), SchemeInnerType.TYPE_ATTRIBUTE));
            }
        } else {
            for (String attr : attrList) {
                DBAttribute a = newSchema.getDBAttributeByName(attr, false);
                boolean changed = false;
                if (!a.isNotNull()) {
                    a.setNotNull(true);
                    changed = true;
                }
                if (changed) {
                    changeList.addSchemeChangeLog(
                            new SchemaChangeLog(
                                    a.getName(), a.getName(), SchemeInnerType.TYPE_ATTRIBUTE));
                }
            }
        }
    }

    private void firePKRemoved(SchemaInfo newSchema, Constraint oldPK) {
        List<String> attrList = oldPK.getAttributes();
        if (attrList.size() == 1) {
            String attr = attrList.get(0);
            DBAttribute a = newSchema.getDBAttributeByName(attr, false);
            boolean changed = true;
            a.setNotNull(false);
            a.setUnique(false);
            if (changed) {
                changeList.addSchemeChangeLog(
                        new SchemaChangeLog(
                                a.getName(), a.getName(), SchemeInnerType.TYPE_ATTRIBUTE));
            }
        } else {
            for (String attr : attrList) {
                DBAttribute a = newSchema.getDBAttributeByName(attr, false);
                boolean changed = true;
                a.setNotNull(false);
                if (changed) {
                    changeList.addSchemeChangeLog(
                            new SchemaChangeLog(
                                    a.getName(), a.getName(), SchemeInnerType.TYPE_ATTRIBUTE));
                }
            }
        }
    }
}
