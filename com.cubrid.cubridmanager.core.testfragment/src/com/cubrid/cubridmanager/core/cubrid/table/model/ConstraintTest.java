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
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * test Constraint
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-4 created by wuyingshi
 */
public class ConstraintTest extends TestCase {
    /** Test Constraint */
    public final void testConstraint() {
        String name = "name";
        String type = "type";
        int keyCount = 6;
        List<String> classAttributes = new ArrayList<String>(); // String
        List<String> attributes = new ArrayList<String>(); // String
        List<String> rules = new ArrayList<String>(); // String
        // test getters and setters
        Constraint constraintNo = new Constraint(false);
        Constraint constraintYes = new Constraint(name, type);
        constraintYes.getAttributes();
        constraintYes.setName(name);
        constraintYes.setType(type);
        constraintYes.setKeyCount(keyCount);
        constraintYes.setAttributes(attributes);
        constraintYes.setRules(rules);

        assertEquals(constraintYes.getName(), name);
        assertEquals(constraintYes.getType(), type);
        assertEquals(constraintYes.getKeyCount(), keyCount);
        assertEquals(constraintYes.getAttributes(), attributes);
        assertEquals(constraintYes.getRules(), rules);

        // test public boolean equals(Object obj)
        assertTrue(constraintYes.equals(constraintYes));
        assertFalse(constraintYes.equals(null));
        assertFalse(constraintYes.equals("other object"));
        constraintYes.setName(null);
        Constraint constraintOther = new Constraint(name, type);
        constraintOther.setName(name);
        constraintOther.setType(type);
        constraintOther.setKeyCount(keyCount);
        constraintOther.setAttributes(attributes);
        constraintOther.setRules(rules);
        constraintYes.equals(constraintOther);
        constraintYes.setName("name1");
        constraintYes.equals(constraintOther);
        constraintYes.setType(null);
        constraintYes.equals(constraintOther);
        constraintYes.setType("type1");
        constraintYes.equals(constraintOther);
        constraintYes.setAttributes(null);
        constraintYes.equals(constraintOther);
        constraintYes.setRules(null);
        constraintYes.equals(constraintOther);

        // test public int hashCode()
        constraintYes.hashCode();
        // test public SerialInfo clone()
        Constraint clonedSerialInfo = constraintYes.clone();
        assertEquals(constraintYes, clonedSerialInfo);
        constraintYes.addAttribute("attributename");
        constraintYes.addClassAttribute("classAttributeName");
        constraintYes.addRule("ruleName");

        constraintYes.toString();
        constraintYes.getDefaultName("tableName");
        constraintYes.getReferencedTable();
        constraintNo.setName(name);
        classAttributes.add("class");
    }
}
