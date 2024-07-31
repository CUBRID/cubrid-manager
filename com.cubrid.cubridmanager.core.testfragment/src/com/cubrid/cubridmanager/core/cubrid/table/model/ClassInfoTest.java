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

import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;
import junit.framework.TestCase;

/**
 * Test ClassInfo model
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-4 created by wuyingshi
 */
public class ClassInfoTest extends TestCase {

    /** test ClassInfo */
    public void testClassInfo() {
        String className = "className";
        String ownerName = "ownerName";
        ClassType classType = ClassType.NORMAL;
        ;
        boolean isSystemClass = true;
        boolean isPartitionedClass = true;
        boolean isSupportUserSchema = true;
        // test constructor
        ClassInfo classInfo =
                new ClassInfo(
                        className,
                        ownerName,
                        classType,
                        isSystemClass,
                        isPartitionedClass,
                        isSupportUserSchema);
        assertNotNull(classInfo);

        // test 	getters and setters
        classInfo.setClassName(className);
        classInfo.setOwnerName(ownerName);
        classInfo.setClassType(classType);
        classInfo.setSystemClass(isSystemClass);
        classInfo.setPartitionedClass(isPartitionedClass);
        classInfo.setSupportUserSchema(isSupportUserSchema);
        assertEquals(classInfo.getClassName(), className);
        assertEquals(classInfo.getOwnerName(), ownerName);
        assertEquals(classInfo.getClassType(), classType);
        assertTrue(classInfo.isSystemClass());
        assertTrue(classInfo.isPartitionedClass());
        assertTrue(classInfo.isSupportUserSchema());
    }
}
