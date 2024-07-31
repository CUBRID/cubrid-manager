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
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog.SchemeInnerType;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * test SchemaChangeManager model
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-4 created by wuyingshi
 */
public class SchemaChangeManagerTest extends TestCase {
    SchemaChangeManager schemaChangeManager = new SchemaChangeManager();
    // schema info
    String tableName = "game";
    String owner = "PUBLIC";
    String type = "user";
    String virtual = "normal";
    // instance attribute
    String attributeName = "nation_code";
    String dataType = "integer";
    String inherit = "game";
    boolean isNotNull = true;
    boolean shared = false;
    boolean unique = true;
    String defaultv = "";

    // pk info
    String pkName = "pk_game_host_year_event_code_athlete_code";
    String pkType = "PRIMARY KEY";
    String[] attributes = {"host_year", "event_code", "athlete_code"};

    // fk info
    String fkName = "fk_game_event_code";
    String fkType = "FOREIGN KEY";
    String[] fkAttributes = {"event_code"};
    String[] fkRules = {"REFERENCES event", "ON DELETE RESTRICT", "ON UPDATE RESTRICT"};

    List<SchemaChangeLog> changeList = null;
    DatabaseInfo database = null;
    boolean isNewTableFlag;

    /** test getting ddl */
    public void testGetDDL() {
        SchemaInfo schema1 = new SchemaInfo();

        schema1.setClassname(tableName);
        schema1.setOwner(owner);
        schema1.setUniqueName(tableName);
        schema1.setVirtual(virtual);
        schema1.setType(type);

        DBAttribute a = new DBAttribute();
        a.setName(attributeName);
        a.setType(dataType);
        a.setInherit(inherit);
        a.setNotNull(isNotNull);
        a.setShared(false);
        a.setUnique(unique);
        a.setDefault(defaultv);
        schema1.addAttribute(a);

        schema1.addClassAttribute(a);

        Constraint pk = new Constraint(false);
        pk.setName(pkName);
        pk.setType(pkType);
        pk.addAttribute(attributes[0]);
        pk.addAttribute(attributes[1]);
        pk.addAttribute(attributes[2]);
        schema1.addConstraint(pk);

        schemaChangeManager.getChangeList();
        schemaChangeManager.isNewAdded(SchemeInnerType.TYPE_ATTRIBUTE, "value");
        schemaChangeManager.isNewAdded("attrName", true);
        schemaChangeManager.isNewAdded("attrName", false);
        schemaChangeManager.getIndexChangeLogs();
        schemaChangeManager.getFKChangeLogs();
        schemaChangeManager.getAttrChangeLogs();
        schemaChangeManager.getClassAttrChangeLogs();
        schemaChangeManager.getChangeLogs(SchemeInnerType.TYPE_ATTRIBUTE);

        schemaChangeManager.setNewTableFlag(true);
        schemaChangeManager.isNewTableFlag();
        schemaChangeManager.setDatabaseInfo(database);
        schemaChangeManager.getDatabaseInfo();

        String oldValue = "oldValue";
        String newValue = "newValue";
        SchemeInnerType stype = SchemeInnerType.TYPE_ATTRIBUTE;
        SchemaChangeLog schemeChangeLog = new SchemaChangeLog(oldValue, newValue, stype);
        changeList = new ArrayList<SchemaChangeLog>();
        changeList.add(schemeChangeLog);
        schemaChangeManager.setChangeList(changeList);
        schemaChangeManager.getChangeLogs(SchemeInnerType.TYPE_ATTRIBUTE);
        schemaChangeManager.addSchemeChangeLog(schemeChangeLog);
        schemaChangeManager.setNewTableFlag(false);
        schemaChangeManager.addSchemeChangeLog(schemeChangeLog);
        schemaChangeManager.isNewAdded(SchemeInnerType.TYPE_ATTRIBUTE, "value");
    }
}
