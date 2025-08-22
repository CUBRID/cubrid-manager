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
package com.cubrid.cubridmanager.core.cubrid.serial.model;

import com.cubrid.common.core.common.model.SerialInfo;
import junit.framework.TestCase;

/**
 * Test SerialInfo model
 *
 * @author wuyingshi
 * @version 1.0 - 2009-12-30 created by wuyingshi
 */
public class SerialInfoTest extends TestCase {

    /** test Serial Information */
    public void testSerialInfo() {
        String name = "serial1";
        String owner = "dba";
        String currentValue = "1000";
        String incrementValue = "1";
        String maxValue = "10000";
        String minValue = "1";
        boolean isCyclic = true;
        String startedValue = "1";
        String cacheCount = "cacheCount";
        String className = "code";
        String attName = "id";
        // test constructor
        SerialInfo serialInfoNo = new SerialInfo();
        assertNotNull(serialInfoNo);
        SerialInfo serialInfo =
                new SerialInfo(
                        "serial1",
                        "dba",
                        "1000",
                        "1",
                        "10000",
                        "1",
                        true,
                        "1",
                        cacheCount,
                        "code",
                        "id");
        assertNotNull(serialInfo);

        // test 	getters and setters
        serialInfo.setAttName(attName);
        serialInfo.setClassName(className);
        serialInfo.setCurrentValue(currentValue);
        serialInfo.setCyclic(isCyclic);
        serialInfo.setIncrementValue(incrementValue);
        serialInfo.setMaxValue(maxValue);
        serialInfo.setMinValue(minValue);
        serialInfo.setName(name);
        serialInfo.setOwner(owner);
        serialInfo.setStartedValue(startedValue);

        assertEquals(serialInfo.getAttName(), attName);
        assertEquals(serialInfo.getClassName(), className);
        assertEquals(serialInfo.getCurrentValue(), currentValue);
        assertTrue(serialInfo.isCyclic());
        assertEquals(serialInfo.getIncrementValue(), incrementValue);
        assertEquals(serialInfo.getMaxValue(), maxValue);
        assertEquals(serialInfo.getMinValue(), minValue);
        assertEquals(serialInfo.getName(), name);
        assertEquals(serialInfo.getOwner(), owner);
        assertEquals(serialInfo.getStartedValue(), startedValue);

        // test 	public boolean equals(Object obj)
        assertTrue(serialInfo.equals(serialInfo));
        assertFalse(serialInfo.equals(null));
        assertFalse(serialInfo.equals("other object"));
        // test public int hashCode()
        serialInfo.hashCode();
        // test public SerialInfo clone()
        SerialInfo clonedSerialInfo = serialInfo.clone();
        assertEquals(serialInfo, clonedSerialInfo);
        SerialInfo serialInfoOther =
                new SerialInfo(null, null, null, null, null, null, false, null, null, null, null);
        serialInfo.setAttName(null);
        serialInfo.setClassName(null);
        serialInfo.setCurrentValue(null);
        serialInfo.setIncrementValue(null);
        serialInfo.setMaxValue(null);
        serialInfo.setMinValue(null);
        serialInfo.setName(null);
        serialInfo.setOwner(null);
        serialInfo.setStartedValue(null);
        serialInfo.setCacheCount(null);
        serialInfo.equals(serialInfoOther);
        SerialInfo serialInfoOther2 =
                new SerialInfo(
                        "serial11",
                        "dba1",
                        "1001",
                        "11",
                        "10001",
                        "11",
                        true,
                        "11",
                        "cacheCount",
                        "code1",
                        "id1");
        serialInfo.equals(serialInfoOther2);
        serialInfoOther2.equals(serialInfoOther);
    }
}
