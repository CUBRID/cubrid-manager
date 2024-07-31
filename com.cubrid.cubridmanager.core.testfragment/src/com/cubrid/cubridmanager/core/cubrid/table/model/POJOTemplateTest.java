package com.cubrid.cubridmanager.core.cubrid.table.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

public class POJOTemplateTest extends TestCase {
    private POJOTemplate makeObject() {
        POJOTemplate tpl = new POJOTemplate();
        return tpl;
    }

    public void testGetAnnotation() {
        POJOTemplate tpl = makeObject();
        assertTrue(tpl.getAnnotation() == null);
        tpl.setAnnotation("test");
        assertEquals("test", tpl.getAnnotation());
    }

    public void testSetAttributes() {
        List<POJOAttribute> pa = new ArrayList<POJOAttribute>();
        POJOAttribute att = new POJOAttribute();
        att.setAnnotation("test");
        pa.add(att);

        POJOTemplate tpl = makeObject();
        tpl.setAttributes(pa);

        assertNotNull(tpl.getAttributes());
        assertEquals(tpl.getAttributes().get(0).getAnnotation(), "test");
    }

    public void testSetAnnotation() {
        POJOTemplate tpl = makeObject();
        assertTrue(tpl.getAnnotation() == null);
        tpl.setAnnotation("test");
        assertEquals("test", tpl.getAnnotation());
        tpl.setAnnotation("test2");
        assertEquals("test2", tpl.getAnnotation());
    }

    public void testGetImportPackages() {
        POJOTemplate tpl = makeObject();
        assertNotNull(tpl.getImportPackages());
        Set<String> set = new HashSet<String>();
        set.add("com.cubrid.cubridmanager");
        tpl.setImportPackages(set);
        assertTrue(tpl.getImportPackages().contains("com.cubrid.cubridmanager"));
    }

    public void testSetImportPackages() {
        POJOTemplate tpl = makeObject();
        assertNotNull(tpl.getImportPackages());
        Set<String> set = new HashSet<String>();
        set.add("com.cubrid.cubridmanager");
        tpl.setImportPackages(set);
        assertTrue(tpl.getImportPackages().contains("com.cubrid.cubridmanager"));
    }

    public void testSetTableName() {
        POJOTemplate tpl = makeObject();
        tpl.setTableName("table1");
        assertEquals(tpl.getTableName(), "table1");
    }

    public void testGetTypeDeclare() {
        POJOTemplate tpl = makeObject();
        tpl.setTypeDeclare("Integer");
        assertEquals(tpl.getTypeDeclare(), "Integer");
        tpl.setTypeDeclare(null);
        assertNull(tpl.getTypeDeclare());
    }

    public void testGetAttributes() {
        List<POJOAttribute> pa = new ArrayList<POJOAttribute>();
        POJOAttribute att = new POJOAttribute();
        att.setAnnotation("test");
        pa.add(att);

        POJOTemplate tpl = makeObject();
        tpl.setAttributes(pa);

        assertNotNull(tpl.getAttributes());
        assertEquals(tpl.getAttributes().get(0).getAnnotation(), "test");
    }

    public void testGetTableName() {
        POJOTemplate tpl = makeObject();
        tpl.setTableName("table1");
        assertEquals(tpl.getTableName(), "table1");
    }

    public void testSetTypeDeclare() {
        POJOTemplate tpl = makeObject();
        tpl.setTypeDeclare("Integer");
        assertEquals(tpl.getTypeDeclare(), "Integer");
        tpl.setTypeDeclare(null);
        assertNull(tpl.getTypeDeclare());
    }

    public void testPojoObject() {
        POJOAttribute attr = new POJOAttribute();
        attr.setAnnotation("annotation");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = , dbType = , dbPrecision = , dbScale = , dbElemType = , dbElemPrecision = , dbElemScale = , dbDefaultValue = ]");

        attr.setDbDefaultValue("dbDefaultValue");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = , dbType = , dbPrecision = , dbScale = , dbElemType = , dbElemPrecision = , dbElemScale = , dbDefaultValue = dbDefaultValue]");

        attr.setDbElemPrecision(1);
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = , dbType = , dbPrecision = , dbScale = , dbElemType = , dbElemPrecision = 1, dbElemScale = , dbDefaultValue = dbDefaultValue]");

        attr.setDbElemScale(1);
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = , dbType = , dbPrecision = , dbScale = , dbElemType = , dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setDbElemType("dbElemType");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = , dbType = , dbPrecision = , dbScale = , dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setDbName("dbName");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = , dbPrecision = , dbScale = , dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setDbPrecision(1);
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = , dbPrecision = 1, dbScale = , dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setDbScale(1);
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = , dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setDbType("dbType");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setElemImportPackage("elemImportPackage");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setElemType("elemType");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setGetAnnotation("getAnnotation");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setGetMethod("getMethod");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setImportPackage("importPackage");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setJavaDefaultValue("javaDefaultValue");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setJavaName("javaName");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setJavaType("javaType");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setSetAnnotation("setAnnotation");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        attr.setSetMethod("setMethod");
        assertEquals(
                attr.toString(),
                "POJOAttribute [dbName = dbName, dbType = dbName, dbPrecision = 1, dbScale = 1, dbElemType = dbElemType, dbElemPrecision = 1, dbElemScale = 1, dbDefaultValue = dbDefaultValue]");

        assertEquals("annotation", attr.getAnnotation());
        assertEquals("dbDefaultValue", attr.getDbDefaultValue());
        assertTrue(1 == attr.getDbElemPrecision());
        assertTrue(1 == attr.getDbElemScale());
        assertEquals("dbElemType", attr.getDbElemType());
        assertEquals("dbName", attr.getDbName());
        assertTrue(1 == attr.getDbPrecision());
        assertTrue(1 == attr.getDbScale());
        assertEquals("dbType", attr.getDbType());
        assertEquals("elemImportPackage", attr.getElemImportPackage());
        assertEquals("elemType", attr.getElemType());
        assertEquals("getAnnotation", attr.getGetAnnotation());
        assertEquals("getMethod", attr.getGetMethod());
        assertEquals("importPackage", attr.getImportPackage());
        assertEquals("javaDefaultValue", attr.getJavaDefaultValue());
        assertEquals("javaName", attr.getJavaName());
        assertEquals("javaType", attr.getJavaType());
        assertEquals("setAnnotation", attr.getSetAnnotation());
        assertEquals("setMethod", attr.getSetMethod());
    }
}
