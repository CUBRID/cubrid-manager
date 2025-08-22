package com.cubrid.common.core.util;

import com.cubrid.common.core.common.model.QueryTypeCounts;
import com.cubrid.common.core.common.model.SQLContextType;
import java.util.Vector;
import junit.framework.Assert;
import junit.framework.TestCase;

public class QueryUtilTest extends TestCase {
    public void testGetQueryType() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "-- [Query Editor Autosave] Host: localhost, Database: demodb, Date: 2012-12-15 15:40:16 --");
        sb.append(StringUtil.NEWLINE);
        sb.append("/* fdsjfaldjslkf jddskfaslfj klasj */UPDATE /* -- */ a10 /* dfsafs fdsafds */");
        sb.append(StringUtil.NEWLINE);
        sb.append("SET aa1='1'");
        sb.append(StringUtil.NEWLINE);
        sb.append(" WHERE 1;");
        sb.append(StringUtil.NEWLINE);
        sb.append("/* fdsjfaldjddslkf jddskfaslfj klasj */");
        sb.append(StringUtil.NEWLINE);
        sb.append("UPDATE a10 ");
        sb.append(StringUtil.NEWLINE);
        sb.append("SET aa1='1'");
        sb.append(StringUtil.NEWLINE);
        sb.append(" WHERE 1;");
        sb.append(StringUtil.NEWLINE);
        sb.append("/* fdsjfaldjslkf jddskfaslfj klasj */ INSERT");
        sb.append(StringUtil.NEWLINE);
        sb.append("  INTO a10 VALUES (1,1,1);");
        sb.append(StringUtil.NEWLINE);
        sb.append("  --");
        sb.append(StringUtil.NEWLINE);
        sb.append("  CREATE TABLE a10;");
        sb.append(StringUtil.NEWLINE);
        sb.append(" -- test ");
        sb.append(StringUtil.NEWLINE);
        sb.append("                DELETE ");
        sb.append(StringUtil.NEWLINE);
        sb.append(" FROM a10 WHERE 1=1;");
        sb.append(StringUtil.NEWLINE);
        sb.append(" /*sdfafas*/");
        sb.append(StringUtil.NEWLINE);
        sb.append(" DROP TABLE aaaa;");

        String querystring = sb.toString();
        Vector<String> queries = QueryUtil.queriesToQuery(querystring);
        QueryTypeCounts counts = QueryUtil.analyzeQueryTypes(queries);
        assertEquals(counts.getSelects(), 0);
        assertEquals(counts.getInserts(), 1);
        assertEquals(counts.getUpdates(), 2);
        assertEquals(counts.getDeletes(), 1);
        assertEquals(counts.getCreates(), 1);
        assertEquals(counts.getDrops(), 1);
        assertEquals(counts.getAlters(), 0);
    }

    public void testGetQueryType2() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "-- [Query Editor Autosave] Host: localhost, Database: demodb, Date: 2012-12-15 20:18:52 --");
        sb.append(StringUtil.NEWLINE);
        sb.append("UPDATE;");
        String querystring = sb.toString();
        Vector<String> queries = QueryUtil.queriesToQuery(querystring);
        QueryTypeCounts counts = QueryUtil.analyzeQueryTypes(queries);
        assertEquals(counts.getSelects(), 0);
        assertEquals(counts.getInserts(), 0);
        assertEquals(counts.getUpdates(), 1);
        assertEquals(counts.getDeletes(), 0);
        assertEquals(counts.getCreates(), 0);
        assertEquals(counts.getDrops(), 0);
        assertEquals(counts.getAlters(), 0);
    }

    public void testQueriesToQuery() {
        StringBuilder sb = new StringBuilder("select * from agent;");
        sb.append(StringUtil.NEWLINE);
        sb.append("select * from game;");
        assertFalse(QueryUtil.queriesToQuery(sb.toString()).isEmpty());
    }

    public void testGetQueryType3() {
        StringBuilder sb = new StringBuilder("--");
        sb.append(StringUtil.NEWLINE);
        sb.append("select * from agent;");
        sb.append(StringUtil.NEWLINE);
        sb.append("/* ssss").append("*/");
        sb.append(StringUtil.NEWLINE);

        SQLContextType type = QueryUtil.getQueryType(sb.toString());
        SQLContextType type2 = QueryUtil.getContextType(sb.toString());
        assertEquals(type, SQLContextType.SELECT);
        assertEquals(type2, SQLContextType.SELECT);

        String query = "SHOW VIEW TABLES";
        type = QueryUtil.getQueryType(query);
        assertEquals(type, SQLContextType.SELECT);

        query = "CREATE TABLE \"aaa\"(\"id\" integer)";
        type = QueryUtil.getQueryType(query);
        type2 = QueryUtil.getContextType(query);
        assertEquals(type, SQLContextType.CREATE);
        assertEquals(type2, SQLContextType.CREATE_TABLE);

        query = "DROP TABLE \"aaa\"";
        type = QueryUtil.getQueryType(query);
        assertEquals(type, SQLContextType.DROP);

        query = "ALTER TABLE \"actions\" DROP COLUMN \"action_id\"";
        type = QueryUtil.getQueryType(query);
        assertEquals(type, SQLContextType.ALTER);

        query = "INSERT INTO actions values(10,10)";
        type = QueryUtil.getQueryType(query);
        type2 = QueryUtil.getContextType(query);
        assertEquals(type, SQLContextType.INSERT);
        assertEquals(type2, SQLContextType.INSERT_VALUES);

        query = "UPDATE actions set \"action_id\" = 3";
        type = QueryUtil.getQueryType(query);
        type2 = QueryUtil.getContextType(query);
        assertEquals(type, SQLContextType.UPDATE);
        assertEquals(type2, SQLContextType.UPDATE);

        query = "DELETE FROM ACTIONS";
        type = QueryUtil.getQueryType(query);
        assertEquals(type, SQLContextType.DELETE);

        query = "";
        type = QueryUtil.getQueryType(query);
        assertEquals(type, SQLContextType.NONE);
    }

    public void testAnalyzeQueryTypes() {
        Vector<String> queries = new Vector<String>();
        queries.add("SHOW VIEW TABLES;");
        queries.add("CREATE TABLE \"aaa\"(\"id\" integer);");
        queries.add("DROP TABLE \"aaa\";");
        queries.add("ALTER TABLE \"actions\" DROP COLUMN \"action_id\";");
        queries.add("INSERT INTO actions values(10,10);");
        queries.add("UPDATE actions set \"action_id\" = 3;");
        queries.add("DELETE FROM ACTIONS;");
        queries.add("");
        assertNotNull(QueryUtil.analyzeQueryTypes(queries));
    }

    public void testFindQueryIdNearbyPosition() {
        String text =
                "<select id = \"test\">SELECT * FROM test WHERE a=1\n\n</select> <update \nid = \"test2\">\n\n\nUPdate * from test where test=1 and b=2 and c in (1,2,3,0)</select>";
        Assert.assertNull(QueryUtil.findNearbyQueryId(text, -1));
        Assert.assertEquals("test", QueryUtil.findNearbyQueryId(text, 0));
        Assert.assertEquals("test", QueryUtil.findNearbyQueryId(text, 10));
        Assert.assertEquals("test", QueryUtil.findNearbyQueryId(text, 50));
        // QueryUtil.findNearbyQueryId() only find the first query ID which start position is less
        // than param 'cursorPosition'
        // Assert.assertEquals("test2", QueryUtil.findNearbyQueryId(text, 90));
        // Assert.assertEquals("test2", QueryUtil.findNearbyQueryId(text, text.length()));
        // Assert.assertNull(QueryUtil.findNearbyQueryId(text, text.length() + 1));
    }
}
