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
package com.cubrid.cubridmanager.core.mondashboard.model;

import junit.framework.TestCase;

/**
 * Test DBStatusType
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-10 created by pangqiren
 */
public class DBStatusTypeTest extends TestCase {

    public void testGetText() {
        assertEquals(DBStatusType.ACTIVE.getText(), "active");
    }

    public void testGetType() {
        assertEquals(DBStatusType.getType("unknown", false), DBStatusType.UNKNOWN);
        assertEquals(DBStatusType.getType("stopped", true), DBStatusType.STOPPED_HA);
        assertEquals(DBStatusType.getType("stopped", false), DBStatusType.STOPPED);
        assertEquals(DBStatusType.getType("CS-mode", false), DBStatusType.CS_Mode);
        assertEquals(DBStatusType.getType("standby", false), DBStatusType.STANDBY);
        assertEquals(DBStatusType.getType("active", false), DBStatusType.ACTIVE);
        assertEquals(DBStatusType.getType("to-be-active", false), DBStatusType.TO_BE_ACTIVE);
        assertEquals(DBStatusType.getType("to-be-standby", false), DBStatusType.TO_BE_STANDBY);

        assertTrue(DBStatusType.isDbStarted(DBStatusType.ACTIVE));
        assertTrue(DBStatusType.isDbStarted(DBStatusType.CS_Mode));
        assertTrue(DBStatusType.isDbStarted(DBStatusType.STANDBY));
        assertTrue(DBStatusType.isDbStarted(DBStatusType.TO_BE_ACTIVE));
        assertTrue(DBStatusType.isDbStarted(DBStatusType.TO_BE_STANDBY));
        assertFalse(DBStatusType.isDbStarted(DBStatusType.STOPPED));
        assertFalse(DBStatusType.isDbStarted(DBStatusType.STOPPED_HA));
        assertFalse(DBStatusType.isDbStarted(DBStatusType.UNKNOWN));
    }

    public void testGetShowText() {
        assertEquals(DBStatusType.getShowText(DBStatusType.UNKNOWN), "unknown");
        assertEquals(DBStatusType.getShowText(DBStatusType.STOPPED), "stopped");
        assertEquals(DBStatusType.getShowText(DBStatusType.CS_Mode), "CS-mode");
        assertEquals(DBStatusType.getShowText(DBStatusType.STOPPED_HA), "stopped/HA");
        assertEquals(DBStatusType.getShowText(DBStatusType.ACTIVE), "active/HA");
        assertEquals(DBStatusType.getShowText(DBStatusType.STANDBY), "standby/HA");
        assertEquals(DBStatusType.getShowText(DBStatusType.TO_BE_ACTIVE), "to-be-active/HA");
        assertEquals(DBStatusType.getShowText(DBStatusType.TO_BE_STANDBY), "to-be-standby/HA");
    }
}
