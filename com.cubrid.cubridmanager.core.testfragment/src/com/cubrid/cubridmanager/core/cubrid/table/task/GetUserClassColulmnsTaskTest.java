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
package com.cubrid.cubridmanager.core.cubrid.table.task;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import com.cubrid.cubridmanager.core.cubrid.table.model.TableColumn;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Test the type of GetUserClassColulmnsTask
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-9 created by lizhiqiang
 */
public class GetUserClassColulmnsTaskTest extends SetupJDBCTestCase {
    String testTableName = "testGetUserClassColumnsTask";
    String testColumn = "testColumn";
    String testPkColumn = "testpk";
    String sql = null;

    private boolean createTestTable() {
        String sql =
                "create table \""
                        + testTableName
                        + "\" ("
                        + testPkColumn
                        + " integer,"
                        + testColumn
                        + " integer,"
                        + "CONSTRAINT pk_"
                        + testTableName
                        + "_"
                        + "testPkColumn"
                        + " PRIMARY KEY(\""
                        + testPkColumn
                        + "\")"
                        + " )";
        return executeDDL(sql);
    }

    private boolean dropTestTable() {
        String sql = "drop table \"" + testTableName + "\"";
        return executeDDL(sql);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createTestTable();
    }

    @Override
    protected void tearDown() throws Exception {
        dropTestTable();
        super.tearDown();
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassColumnsTask#getColumns(java.lang.String)}
     * .
     */
    public void testGetColumns() {
        GetUserClassColumnsTask task = new GetUserClassColumnsTask(databaseInfo);
        List<TableColumn> list = task.getColumns(testTableName);
        String actualColumnName = list.get(0).getColumnName();
        assertEquals(testPkColumn.toLowerCase(Locale.getDefault()), actualColumnName);
        actualColumnName = list.get(1).getColumnName();
        assertEquals(testColumn.toLowerCase(Locale.getDefault()), actualColumnName);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassColumnsTask#getColumns(java.lang.String)}
     * .
     */
    public void testGetColumns2() {
        GetUserClassColumnsTask task = new GetUserClassColumnsTask(databaseInfo);
        task.setErrorMsg("Error");
        List<TableColumn> list = task.getColumns(testTableName);
        assertTrue(list.isEmpty());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassColumnsTask#getColumns(java.lang.String)}
     * .
     *
     * @throws SQLException the SQLException
     */
    public void testGetColumns3() throws SQLException {
        GetUserClassColumnsTask task = new GetUserClassColumnsTask(databaseInfo);
        task.getConnection().close();
        List<TableColumn> list = task.getColumns(testTableName);
        assertTrue(list.isEmpty());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassColumnsTask#getColumns(java.util.List)}
     * .
     */
    public void testGetColumnsList() {
        GetUserClassColumnsTask task = new GetUserClassColumnsTask(databaseInfo);

        List<String> tableNames = new ArrayList<String>();
        tableNames.add(testTableName);
        Map<String, List<TableColumn>> map = task.getColumns(tableNames);
        assertFalse(map.isEmpty());
        List<TableColumn> list = map.get(testTableName);
        String actualColumnName = list.get(0).getColumnName();
        assertEquals(testPkColumn.toLowerCase(Locale.getDefault()), actualColumnName);
        actualColumnName = list.get(1).getColumnName();
        assertEquals(testColumn.toLowerCase(Locale.getDefault()), actualColumnName);
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassColumnsTask#getColumns(java.util.List)}
     * .
     */
    public void testGetColumnsList2() {
        GetUserClassColumnsTask task = new GetUserClassColumnsTask(databaseInfo);
        task.setErrorMsg("Error");
        List<String> tableNames = new ArrayList<String>();
        tableNames.add(testTableName);
        Map<String, List<TableColumn>> map = task.getColumns(tableNames);
        assertTrue(map.isEmpty());
    }

    /**
     * Test method for {@link
     * com.cubrid.cubridmanager.core.cubrid.table.task.GetUserClassColumnsTask#getColumns(java.util.List)}
     * .
     *
     * @throws SQLException the SQLException
     */
    public void testGetColumnsList3() throws SQLException {
        GetUserClassColumnsTask task = new GetUserClassColumnsTask(databaseInfo);
        task.getConnection().close();
        List<String> tableNames = new ArrayList<String>();
        tableNames.add(testTableName);
        Map<String, List<TableColumn>> map = task.getColumns(tableNames);
        assertTrue(map.isEmpty());
    }
}
