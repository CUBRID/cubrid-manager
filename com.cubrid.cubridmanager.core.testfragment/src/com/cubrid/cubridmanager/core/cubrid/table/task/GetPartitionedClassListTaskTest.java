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

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;

/**
 * test getting partitioned tables of a table
 *
 * @author moulinwang
 * @version 1.0 - 2009-7-1 created by moulinwang
 */
public class GetPartitionedClassListTaskTest extends SetupJDBCTestCase {
    String testTableName = "athlete2";
    String testTableName2 = "testGetPartitionedClassListTaskTest";
    String sql = null;

    private boolean createTestTable() {
        String sql =
                "create table \""
                        + testTableName2
                        + "\" ("
                        + "empno char(10) not null unique,"
                        + "empname varchar(20) not null,"
                        + "deptname varchar(20), "
                        + "hiredate date"
                        + ")"
                        + "partition by range (extract (year from hiredate)) "
                        + "(partition h2000 values less than (2000),"
                        + "partition h2003 values less than (2003),"
                        + "partition hmax values less than  maxvalue)";
        ;
        return executeDDL(sql);
    }

    private boolean createTestTable2() {
        String sql =
                "create table \""
                        + testTableName
                        + "\" ("
                        + "name VARCHAR(40),"
                        + "event VARCHAR(30)"
                        + ")"
                        + "PARTITION BY LIST (event)"
                        + "(PARTITION event1 VALUES IN ('Swimming', 'Athletics ' ),"
                        + "PARTITION event2 VALUES IN ('Judo', 'Taekwondo','Boxing'),"
                        + "PARTITION event3 VALUES IN ('Football', 'Basketball', 'Baseball'));"
                        + "INSERT INTO athlete2 VALUES ('Hwang Young-Cho', 'Athletics');"
                        + "INSERT INTO athlete2 VALUES ('Lee Seung-Yuop', 'Baseball');"
                        + "INSERT INTO athlete2 VALUES ('Moon Dae-Sung','Taekwondo');";
        ;
        return executeDDL(sql);
    }

    private boolean dropTestTable() {
        String sql = "drop table \"" + testTableName2 + "\"";
        return executeDDL(sql);
    }

    private boolean dropTestTable2() {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(sql);
    }

    public void testGetPartitionedClassListTaskTest() {
        createTestTable();
        createTestTable2();
        GetPartitionedClassListTask task = new GetPartitionedClassListTask(databaseInfo);
        task.getPartitionItemList(testTableName);
        task.getAllPartitionedClassInfoList(testTableName);
        task.getPartitionItemList("aa");
        task.getAllPartitionedClassInfoList("aa");
        GetPartitionedClassListTask task2 = new GetPartitionedClassListTask(databaseInfo);
        task2.getAllPartitionedClassInfoList(testTableName2);
        task2.getPartitionItemList(testTableName);
        GetPartitionedClassListTask task3 = new GetPartitionedClassListTask(databaseInfo);
        task3.getColumnStatistics(testTableName2);
        task3.getDistinctValuesInAttribute(testTableName, "name");
        task.getColumnStatistics("aa");
        task.getDistinctValuesInAttribute("aa", "name");
        GetPartitionedClassListTask task4 = new GetPartitionedClassListTask(databaseInfo);
        task4.getDistinctValuesInAttribute(testTableName, "name");
        task4.getColumnStatistics(testTableName);

        GetPartitionedClassListTask task5 = new GetPartitionedClassListTask(databaseInfo);
        task5.getPartitionItemList(testTableName);
        task5.getColumnStatistics(testTableName);
        GetPartitionedClassListTask task6 = new GetPartitionedClassListTask(databaseInfo);
        task6.getPartitionItemList(testTableName2);
        dropTestTable2();
        dropTestTable();
    }
}
