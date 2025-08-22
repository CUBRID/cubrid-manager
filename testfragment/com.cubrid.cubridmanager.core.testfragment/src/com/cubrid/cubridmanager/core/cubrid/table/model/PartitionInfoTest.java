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

import com.cubrid.common.core.common.model.PartitionInfo;
import com.cubrid.common.core.common.model.PartitionType;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * test PartitionInfo model
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-4 created by wuyingshi
 */
public class PartitionInfoTest extends TestCase {
    /** Test PartitionInfo */
    public final void testPartitionInfo() {

        String tableName = "tableName";
        String partitionName = "partitionName";
        String partitionClassName = "partitionClassName";
        PartitionType partitionType = PartitionType.HASH;
        String partitionExpr = "partitionExpr";
        final List<String> partitionValues = new ArrayList<String>();
        int rows = -1;
        // test getters and setters
        PartitionInfo partitionInfo1 = new PartitionInfo();
        PartitionInfo partitionInfo2 = new PartitionInfo(tableName, partitionType);
        PartitionInfo partitionInfo3 =
                new PartitionInfo(
                        tableName,
                        partitionName,
                        partitionType,
                        partitionExpr,
                        partitionValues,
                        rows);
        PartitionInfo partitionInfo4 =
                new PartitionInfo(
                        tableName,
                        partitionName,
                        partitionClassName,
                        partitionType,
                        partitionExpr,
                        partitionValues,
                        rows);
        partitionInfo4.setPartitionValues(partitionValues);
        partitionInfo4.setTableName(tableName);
        partitionInfo4.setPartitionName(partitionName);
        partitionInfo4.setPartitionType(partitionType);
        partitionInfo4.setPartitionExpr(partitionExpr);
        partitionInfo4.setRows(rows);
        assertEquals(partitionInfo4.getTableName(), tableName);
        assertEquals(partitionInfo4.getPartitionName(), partitionName);
        assertNotSame(partitionInfo4.getPartitionClassName(), partitionClassName);
        assertEquals(partitionInfo4.getPartitionType(), partitionType);
        assertEquals(partitionInfo4.getPartitionExpr(), partitionExpr);
        assertEquals(partitionInfo4.getRows(), -1);

        // test public SerialInfo clone()
        PartitionInfo partitionInfoClone1 = partitionInfo1.clone();
        assertNotNull(partitionInfoClone1);
        PartitionInfo partitionInfoClone2 = partitionInfo2.clone();
        assertNotNull(partitionInfoClone2);
        PartitionInfo partitionInfoClone3 = partitionInfo3.clone();
        assertNotNull(partitionInfoClone3);
        partitionInfo4.toString();
        partitionInfo4.addPartitionValue("value");
        partitionInfo4.setPartitionValues(partitionValues);
        partitionInfo4.removePartitionValue("value");
        partitionInfo4.addPartitionValue("value");
        partitionInfo4.getPartitionValuesString();
        partitionInfo4.getPartitionValuesString(true);
        partitionInfo4.addPartitionValue("value");
        partitionInfo4.getPartitionValuesString();
        partitionInfo4.getPartitionValuesString(false);
        partitionInfo4.addPartitionValue("value1");
        partitionInfo4.addPartitionValue("value2");
        partitionInfo4.addPartitionValue("value3");
        partitionInfo4.removePartitionValue("value");
        partitionInfo4.equals(partitionInfo4);
        partitionInfo4.equals("aaa");
        PartitionInfo partitionInfo5 =
                new PartitionInfo(
                        tableName,
                        partitionName,
                        partitionClassName,
                        partitionType,
                        partitionExpr,
                        partitionValues,
                        rows);
        partitionInfo5.setPartitionValues(partitionValues);
        partitionInfo5.setTableName(tableName);
        partitionInfo5.setPartitionName(partitionName);
        partitionInfo5.setPartitionType(partitionType);
        partitionInfo5.setPartitionExpr(partitionExpr);
        partitionInfo5.setRows(rows);
        partitionInfo4.equals(partitionInfo5);
        partitionInfo5.addPartitionValue("value5");
        partitionInfo5.setPartitionValues(partitionValues);
        partitionInfo5.setTableName("className5");
        partitionInfo5.setPartitionName("partitionName5");
        partitionInfo5.setPartitionType(PartitionType.LIST);
        partitionInfo5.setPartitionExpr("partitionExpr5");
        partitionInfo5.setRows(5);
        partitionInfo4.equals(partitionInfo5);
        partitionInfo5.setPartitionType(partitionType);
        partitionInfo5.setTableName(tableName);
        partitionInfo4.equals(partitionInfo5);
        partitionInfo5.setPartitionType(partitionType);
        partitionInfo5.setTableName(tableName);
        partitionInfo5.setPartitionName(partitionName);
        partitionInfo4.equals(partitionInfo5);
        partitionInfo5.setPartitionType(partitionType);
        partitionInfo5.setTableName(tableName);
        partitionInfo5.setPartitionName(partitionName);
        partitionInfo4.setPartitionType(PartitionType.LIST);
        partitionInfo5.setPartitionType(PartitionType.RANGE);
        partitionInfo4.equals(partitionInfo5);
        partitionInfo4.setPartitionType(PartitionType.LIST);
        partitionInfo5.setPartitionType(PartitionType.LIST);
        partitionInfo5.setTableName(tableName);
        partitionInfo5.setPartitionName(partitionName);
        partitionInfo5.setPartitionExpr(partitionExpr);
        partitionInfo4.equals(partitionInfo5);

        partitionInfo1.setPartitionType(PartitionType.HASH);
        partitionInfo2.setPartitionType(PartitionType.HASH);
        partitionInfo1.setTableName(null);
        partitionInfo2.setTableName(null);
        partitionInfo1.equals(partitionInfo2);
        partitionInfo1.setTableName("a");
        partitionInfo2.setTableName(null);
        partitionInfo1.equals(partitionInfo2);
        partitionInfo1.setTableName(null);
        partitionInfo2.setTableName("b");
        partitionInfo1.equals(partitionInfo2);
    }
}
