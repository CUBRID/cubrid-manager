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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;
import junit.framework.TestCase;

public class TableModelTest extends TestCase {
    public void testModelClassAuthorizations() {
        ClassAuthorizations bean = new ClassAuthorizations("name", 1);
        bean.setClassName("className");
        assertEquals(bean.getClassName(), "className");
        bean.setSelectPriv(false);
        assertEquals(bean.isSelectPriv(), false);
        bean.setInsertPriv(false);
        assertEquals(bean.isInsertPriv(), false);
        bean.setUpdatePriv(false);
        assertEquals(bean.isUpdatePriv(), false);
        bean.setAlterPriv(false);
        assertEquals(bean.isAlterPriv(), false);
        bean.setDeletePriv(false);
        assertEquals(bean.isDeletePriv(), false);
        bean.setIndexPriv(false);
        assertEquals(bean.isIndexPriv(), false);
        bean.setExecutePriv(false);
        assertEquals(bean.isExecutePriv(), false);
        bean.setGrantSelectPriv(false);
        assertEquals(bean.isGrantSelectPriv(), false);
        bean.setGrantInsertPriv(false);
        assertEquals(bean.isGrantInsertPriv(), false);
        bean.setGrantUpdatePriv(false);
        assertEquals(bean.isGrantUpdatePriv(), false);
        bean.setGrantAlterPriv(false);
        assertEquals(bean.isGrantAlterPriv(), false);
        bean.setGrantDeletePriv(false);
        assertEquals(bean.isGrantDeletePriv(), false);
        bean.setGrantIndexPriv(false);
        assertEquals(bean.isGrantIndexPriv(), false);
        bean.setGrantExecutePriv(false);
        assertEquals(bean.isGrantExecutePriv(), false);
        bean.setAllPriv(false);
        assertEquals(bean.isAllPriv(), false);
        bean.isSelectPriv();
        bean.isInsertPriv();
        bean.isUpdatePriv();
        bean.isAlterPriv();
        bean.isDeletePriv();
        bean.isIndexPriv();
        bean.isExecutePriv();
        bean.isGrantSelectPriv();
        bean.isGrantInsertPriv();
        bean.isGrantUpdatePriv();
        bean.isGrantAlterPriv();
        bean.isGrantDeletePriv();
        bean.isGrantIndexPriv();
        bean.isGrantExecutePriv();
        bean.isAllPriv();
    }

    public void testModelClassInfo() {
        ClassInfo bean = new ClassInfo("className");
        bean.setClassName("className");
        assertEquals(bean.getClassName(), "className");
        bean.setOwnerName("ownerName");
        assertEquals(bean.getOwnerName(), "ownerName");
        bean.setClassType(ClassType.NORMAL);
        assertEquals(bean.getClassType(), ClassType.NORMAL);

        bean.setSystemClass(true);
        bean.setPartitionedClass(true);
        assertEquals(bean.isSystemClass(), true);
        assertEquals(bean.isPartitionedClass(), true);
    }

    public void testModelConstraint() {
        Constraint bean = new Constraint(false);
        bean.setName("name");
        assertEquals(bean.getName(), "name");
        bean.setType("type");
        assertEquals(bean.getType(), "type");
        bean.setKeyCount(8);
        assertEquals(bean.getKeyCount(), 8);
        bean.addAttribute("Attr");
        assertEquals(bean.getAttributes().size(), 1);
        bean.getReferencedTable();
        bean.getRules();
        bean.getDefaultName("a");
        bean.getClassAttributes();
        bean.addRule("r");
        bean.addClassAttribute("addClassAttribute");
    }

    public void testModelDBAttribute() {
        DBAttribute bean = new DBAttribute();
        bean.setName("name");
        assertEquals(bean.getName(), "name");
        bean.setType("type");
        assertEquals(bean.getType(), "type");
        bean.setInherit("inherit");
        assertEquals(bean.getInherit(), "inherit");
        bean.setIndexed(false);
        assertEquals(bean.isIndexed(), false);
        bean.setNotNull(false);
        assertEquals(bean.isNotNull(), false);
        bean.setShared(false);
        assertEquals(bean.isShared(), false);
        bean.setUnique(false);
        assertEquals(bean.isUnique(), false);
        bean.setAutoIncrement(null);
        assertEquals(bean.getAutoIncrement(), null);
        bean.setDomainClassName("domainClassName");
        assertEquals(bean.getDomainClassName(), "domainClassName");

        bean.setDefault("defaultValue");
        bean.isShared();
        bean.isClassAttribute();
        bean.getSharedValue();
        bean.setSharedValue("defaultValue");
        bean.resetDefault();
        bean.isIndexed();
        bean.isNotNull();
        bean.isUnique();
        // bean.formatValue("atttype","attrValue");
        bean.setClassAttribute(true);
    }

    public void testModelDBResolution() {
        DBResolution bean = new DBResolution();
        bean.setName("name");
        assertEquals(bean.getName(), "name");
        bean.setTableName("className");
        assertEquals(bean.getTableName(), "className");
        bean.setAlias("alias");
        assertEquals(bean.getAlias(), "alias");
        bean.isClassResolution();
        bean.setClassResolution(true);
        assertEquals(bean.isClassResolution(), true);
    }
}
