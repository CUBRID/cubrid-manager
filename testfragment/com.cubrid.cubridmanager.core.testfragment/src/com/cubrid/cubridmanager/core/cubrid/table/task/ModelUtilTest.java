/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;
import com.cubrid.cubridmanager.core.utils.ModelUtil;
import com.cubrid.cubridmanager.core.utils.ModelUtil.AttributeCategory;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;
import com.cubrid.cubridmanager.core.utils.ModelUtil.KillTranType;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerActionTime;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerConditionTime;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerEvent;
import com.cubrid.cubridmanager.core.utils.ModelUtil.TriggerStatus;
import com.cubrid.cubridmanager.core.utils.ModelUtil.YesNoType;
import junit.framework.TestCase;

public class ModelUtilTest extends TestCase {
    public void testAttributeCategory() {
        AttributeCategory a = AttributeCategory.eval("instance");
        assert (a.equals(AttributeCategory.INSTANCE));
        assertTrue(AttributeCategory.eval("") == null);
    }

    public void testClassType() {
        ClassType a = ClassType.eval("normal");
        assert (a.equals(ClassType.NORMAL));
        assertTrue(ClassType.eval("") == null);
    }

    public void testConstraintType() {
        Constraint.ConstraintType a = Constraint.ConstraintType.eval("FOREIGN KEY");
        assert (a.equals(Constraint.ConstraintType.FOREIGNKEY));
        a = Constraint.ConstraintType.eval("PRIMARY KEY");
        assert (a.equals(Constraint.ConstraintType.PRIMARYKEY));
        assertTrue(Constraint.ConstraintType.eval("") == null);
    }

    public void testTriggerAction() {
        Trigger.TriggerAction triggeraction = Trigger.TriggerAction.eval("INVALIDATE TRANSACTION");
        assert (triggeraction.equals(Trigger.TriggerAction.INVALIDATE_TRANSACTION));

        triggeraction = Trigger.TriggerAction.eval("OTHER STATEMENT");
        assert (triggeraction.equals(Trigger.TriggerAction.OTHER_STATEMENT));

        assertTrue(Trigger.TriggerAction.eval("") == null);
    }

    public void testTriggerEvent() {
        TriggerEvent a = TriggerEvent.eval("STATEMENT INSERT");
        assert (a.equals(TriggerEvent.STATEMENTINSERT));

        assertTrue(TriggerEvent.eval("") == null);
    }

    public void testTriggerStatus() {
        TriggerStatus a = TriggerStatus.eval("INACTIVE");
        assert (a.equals(TriggerStatus.INACTIVE));
        assertEquals(a.getText(), "INACTIVE");
    }

    public void testTriggerConditionTime() {
        TriggerConditionTime a = TriggerConditionTime.eval("AFTER");
        assert (a.equals(TriggerConditionTime.AFTER));

        assertEquals(a.getText(), "AFTER");
    }

    public void testTriggerActionTime() {
        TriggerActionTime a = TriggerActionTime.eval("AFTER");
        assert (a.equals(TriggerActionTime.AFTER));

        assertEquals(a.getText(), "AFTER");
    }

    public void testYesNoType() {
        YesNoType a = YesNoType.eval("y");
        assert (a.equals(YesNoType.Y));

        assertTrue(YesNoType.eval("") == null);
    }

    public void testKillTranType() {
        KillTranType a = KillTranType.eval("t");
        assert (a.equals(KillTranType.T));

        assertTrue(KillTranType.eval("") == null);
    }

    public void testOsInfoType() {
        FileUtil.OsInfoType a = FileUtil.OsInfoType.eval("NT");
        assert (a.equals(FileUtil.OsInfoType.NT));

        assertEquals(a.getText(), "NT");
    }

    public void testGetSuperClass() {
        TreeNode node = new TreeNode();
        node.add("open", "class");
        node.add("close", "class");
        ModelUtil.getSuperClass(node);
    }

    public void testGetClassList() {
        TreeNode node = new TreeNode();
        node.add("open", "systemclass");
        node.add("close", "systemclass");
        node.add("open", "userclass");
        node.add("close", "userclass");
        ModelUtil.getClassList(node);
    }

    public void testgetTriggerList() {
        TreeNode node = new TreeNode();
        ModelUtil.getTriggerList(node);

        TreeNode child = new TreeNode();
        child.add("open", "triggerinfo");
        child.add("close", "triggerinfo");
        node.addChild(child);
        ModelUtil.getTriggerList(node);
    }
}
