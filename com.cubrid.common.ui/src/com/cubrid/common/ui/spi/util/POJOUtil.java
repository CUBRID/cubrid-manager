/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.spi.util;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.POJOAttribute;
import com.cubrid.cubridmanager.core.cubrid.table.model.POJOTemplate;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;

/**
 * The POJO generate tool.
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-02-29 create by Kevin.Wang
 */
public class POJOUtil {
    private static final Logger LOGGER = LogUtil.getLogger(POJOUtil.class);

    private static final String NEW_LINE = StringUtil.NEWLINE;
    private static final String SEMICOLON = ";";
    private static final String ONE_INDENTATION = "\t";

    private static final Map<String, JavaType> TYPE_MAPPIING = new HashMap<String, JavaType>();

    /** Data type mapping */
    static {
        /* Text */
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_NATIONAL_CHARACTER, -1, -1), new JavaType(null, "String"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_NCHAR, -1, -1), new JavaType(null, "String"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_NCHAR_VARYING, -1, -1), new JavaType(null, "String"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_NATIONAL_CHARACTER_VARYING, -1, -1),
                new JavaType(null, "String"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_VARCHAR, -1, -1), new JavaType(null, "String"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_CHARACTER_VARYING, -1, -1), new JavaType(null, "String"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_CHAR, -1, -1), new JavaType(null, "String"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_CHARACTER, -1, -1), new JavaType(null, "String"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_STRING, -1, -1), new JavaType(null, "String"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_OID, -1, -1), new JavaType(null, "String"));
        /*Enum*/
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_ENUM, -1, -1), new JavaType(null, "String"));
        /* Number */
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_INT, -1, -1), new JavaType(null, "Integer"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_INTEGER, -1, -1), new JavaType(null, "Integer"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_SHORT, -1, -1), new JavaType(null, "Short"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_BIGINT, -1, -1), new JavaType(null, "Long"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_DECIMAL, 9, 0), new JavaType(null, "Integer"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_DECIMAL, 18, 0), new JavaType(null, "Long"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_DECIMAL, 38, 0),
                new JavaType("java.math.BigInteger", "BigInteger"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_DECIMAL, -1, -1),
                new JavaType("java.math.BigDecimal", "BigDecimal"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_NUMERIC, 9, 0), new JavaType(null, "Integer"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_NUMERIC, 18, 0), new JavaType(null, "Long"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_NUMERIC, 38, 0),
                new JavaType("java.math.BigInteger", "BigInteger"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_NUMERIC, -1, -1),
                new JavaType("java.math.BigDecimal", "BigDecimal"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_REAL, -1, -1), new JavaType(null, "Float"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_FLOAT, -1, -1), new JavaType(null, "Float"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_DOUBLE, -1, -1), new JavaType(null, "Double"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_SMALLINT, -1, -1), new JavaType(null, "Short"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_TINYINT, -1, -1), new JavaType(null, "Short"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_MONETARY, -1, -1), new JavaType(null, "Double"));
        /* Date */
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_TIMESTAMP, -1, -1),
                new JavaType("java.util.Date", "Date"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_TIME, -1, -1), new JavaType("java.util.Date", "Date"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_DATE, -1, -1), new JavaType("java.util.Date", "Date"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_DATETIME, -1, -1), new JavaType("java.util.Date", "Date"));
        /* Byte[] */
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_BIT_VARYING, -1, -1), new JavaType(null, "byte[]"));
        TYPE_MAPPIING.put(getKey(DataType.DATATYPE_BIT, -1, -1), new JavaType(null, "byte[]"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_CLOB, -1, -1), new JavaType("java.sql.Clob", "Clob"));
        TYPE_MAPPIING.put(
                getKey(DataType.DATATYPE_BLOB, -1, -1), new JavaType("java.sql.Blob", "Blob"));
        /* Collection */
        TYPE_MAPPIING.put(getKey("SEQUENCE_OF", -1, -1), new JavaType("java.util.List", "List"));
        TYPE_MAPPIING.put(getKey("MULTISET_OF", -1, -1), new JavaType("java.util.List", "List"));
        TYPE_MAPPIING.put(getKey("SET_OF", -1, -1), new JavaType("java.util.List", "List"));

        TYPE_MAPPIING.put(getKey("", -1, -1), new JavaType("", "Object"));
    }

    /**
     * Get the POJO String, The type of schemaNode should be table or class
     *
     * @param schemaNode
     * @return the POJO String
     */
    public static String getJavaPOJOString(Connection connection, DefaultSchemaNode schemaNode) {
        CubridDatabase database = schemaNode.getDatabase();
        String tableName = schemaNode.getName();
        SchemaInfo schemaInfo = null;
        if (connection == null) {
            schemaInfo = database.getDatabaseInfo().getSchemaInfo(tableName);
        } else {
            schemaInfo = database.getDatabaseInfo().getSchemaInfo(connection, tableName);
        }

        if (schemaInfo == null) {
            com.cubrid.common.ui.spi.util.CommonUITool.openErrorBox(
                    Messages.bind(Messages.errGetSchemaInfo, tableName));
            LOGGER.debug("Can't get the SchemaInfo:" + tableName);
            return "";
        }

        POJOTemplate template = new POJOTemplate();
        template.setTableName(tableName);

        StringBuffer typeDeclareSB = new StringBuffer();
        typeDeclareSB.append("public class ");
        typeDeclareSB.append(getUpperName(tableName));
        template.setTypeDeclare(typeDeclareSB.toString());

        StringBuffer annotationSB = new StringBuffer();
        annotationSB.append("/**" + NEW_LINE);
        annotationSB.append(" * Table name : " + tableName + NEW_LINE);
        annotationSB.append(" * Generated by CUBRID Tools." + NEW_LINE);
        annotationSB.append(" */");
        template.setAnnotation(annotationSB.toString());

        /* Attributes */
        for (DBAttribute dbAttribute : schemaInfo.getAttributes()) {
            POJOAttribute attribute = getPOJOAttribute(dbAttribute, true);
            if (attribute != null) {
                template.getAttributes().add(attribute);
            }
        }

        /* Class Attribute */
        for (DBAttribute dbAttribute : schemaInfo.getClassAttributes()) {
            POJOAttribute attribute = getPOJOAttribute(dbAttribute, true);
            if (attribute != null) {
                template.getAttributes().add(attribute);
            }
        }

        return getJavaPOJOString(template);
    }

    /**
     * Get the PHP POJO String, The type of schemaNode should be table or class
     *
     * @param schemaNode
     * @return the POJO String
     */
    public static String getPhpPOJOString(Connection connection, DefaultSchemaNode schemaNode) {
        CubridDatabase database = schemaNode.getDatabase();
        String tableName = schemaNode.getName();
        SchemaInfo schemaInfo = database.getDatabaseInfo().getSchemaInfo(connection, tableName);

        if (schemaInfo == null) {
            com.cubrid.common.ui.spi.util.CommonUITool.openErrorBox(
                    Messages.bind(Messages.errGetSchemaInfo, tableName));
            LOGGER.debug("Can't get the SchemaInfo:" + tableName);
            return "";
        }

        POJOTemplate template = new POJOTemplate();
        template.setTableName(tableName);

        StringBuffer typeDeclareSB = new StringBuffer();
        typeDeclareSB.append("class ");
        typeDeclareSB.append(getUpperName(tableName));
        template.setTypeDeclare(typeDeclareSB.toString());

        StringBuffer annotationSB = new StringBuffer();
        annotationSB.append("/**" + NEW_LINE);
        annotationSB.append(" * Table name : " + tableName + NEW_LINE);
        annotationSB.append(" * Generated by CUBRID Tools." + NEW_LINE);
        annotationSB.append(" */");
        template.setAnnotation(annotationSB.toString());

        /* Attributes */
        for (DBAttribute dbAttribute : schemaInfo.getAttributes()) {
            POJOAttribute attribute = getPOJOAttribute(dbAttribute, false);
            if (attribute != null) {
                template.getAttributes().add(attribute);
            }
        }

        /* Class Attribute */
        for (DBAttribute dbAttribute : schemaInfo.getClassAttributes()) {
            POJOAttribute attribute = getPOJOAttribute(dbAttribute, false);
            if (attribute != null) {
                template.getAttributes().add(attribute);
            }
        }

        return getPhpPOJOString(template);
    }

    /**
     * Get java class file name
     *
     * @param tableName
     * @return
     */
    public static String getJavaClassFileName(String tableName) {
        if (tableName == null) {
            return null;
        }

        return getUpperName(tableName) + ".java";
    }

    /**
     * Get php class file name
     *
     * @param tableName
     * @return
     */
    public static String getPhpClassFileName(String tableName) {
        if (tableName == null) {
            return null;
        }

        return getUpperName(tableName) + ".php";
    }

    /**
     * Get POJOAttribute by DBAttribute
     *
     * @param dbAttribute DBAttribute
     * @return attribute POJOAttribute
     */
    private static POJOAttribute getPOJOAttribute(DBAttribute dbAttribute, boolean isJava) {
        String strType = dbAttribute.getType();

        String type = DataType.getTypePart(strType);
        Integer precision = DataType.getSize(strType);
        Integer scale = DataType.getScale(strType);

        /* init data type */
        JavaType model = getJaveType(type, precision, scale);
        if (model == null) {
            LOGGER.debug("Can process complex type:" + dbAttribute.toString());
            return null;
        }

        String elemStrType = DataType.getElemType(strType);
        Integer elemPrecision = -1;
        Integer elemScale = -1;
        String elemType = null;
        JavaType elemModel = null;
        if (elemStrType != null && elemStrType.length() > 0) {
            elemType = DataType.getTypePart(elemStrType);
            elemPrecision = DataType.getSize(elemStrType);
            elemScale = DataType.getScale(elemStrType);
            elemModel = getJaveType(elemType, elemPrecision, elemScale);

            if (elemModel == null) {
                LOGGER.debug("Can process complex element type:" + dbAttribute.toString());
            }
        }

        POJOAttribute attribute = new POJOAttribute();

        attribute.setDbName(dbAttribute.getName());
        attribute.setDbType(type);
        attribute.setDbPrecision(precision);
        attribute.setDbScale(scale);
        attribute.setDbElemType(elemType);
        attribute.setDbElemPrecision(elemPrecision);
        attribute.setDbElemScale(elemScale);
        attribute.setDbDefaultValue(dbAttribute.getDefault());
        attribute.setJavaName(getAttrbuteStyleName(attribute.getDbName()));
        attribute.setJavaType(model.getType());
        attribute.setImportPackage(model.getJavaPackage());
        if (elemModel != null) {
            attribute.setElemType(elemModel.getType());
            attribute.setElemImportPackage(elemModel.getJavaPackage());
        }

        if (isJava) initJavaPOJOAttribute(attribute);
        else initPhpPOJOAttribute(attribute);

        return attribute;
    }

    private static String getAttrbuteStyleName(String name) {
        char[] chars = name.toCharArray();
        StringBuffer nameSB = new StringBuffer();

        nameSB.append(Character.toLowerCase(chars[0]));
        boolean nextToUpper = false;
        for (int i = 1; i < name.length(); i++) {
            char c = chars[i];

            if ('_' == c || '-' == c) {
                nextToUpper = true;
                continue;
            }

            if (nextToUpper) {
                nameSB.append(Character.toUpperCase(c));
                nextToUpper = false;
            } else {
                nameSB.append(c);
            }
        }
        return nameSB.toString();
    }

    /**
     * Get the upper name,e.g. student_object,return StudentObject.
     *
     * @param name
     * @return UpperName
     */
    private static String getUpperName(String name) {
        char[] chars = name.toCharArray();
        StringBuffer nameSB = new StringBuffer();

        nameSB.append(Character.toUpperCase(chars[0]));
        boolean nextToUpper = false;
        for (int i = 1; i < name.length(); i++) {
            char c = chars[i];

            if ('_' == c || '-' == c) {
                nextToUpper = true;
                continue;
            }

            if (nextToUpper) {
                nameSB.append(Character.toUpperCase(c));
                nextToUpper = false;
            } else {
                nameSB.append(c);
            }
        }
        return nameSB.toString();
    }

    private static String getCRUDQuery(POJOTemplate template) {
        StringBuilder sql = new StringBuilder();
        sql.append(NEW_LINE);

        //
        // SELECT
        //
        int len = template.getAttributes().size();
        int index = 0;
        StringBuilder cols = new StringBuilder();
        StringBuilder cons = new StringBuilder();
        for (POJOAttribute attrbute : template.getAttributes()) {
            index++;
            String col = QuerySyntax.escapeKeyword(attrbute.getDbName());
            cols.append("\t").append(col);
            if (len > index) {
                cols.append(",");
            }
            cols.append(NEW_LINE);

            cons.append("\t");
            if (index > 1) {
                cons.append("AND ");
            }
            cons.append(col).append(" = #").append(getAttrbuteStyleName(col)).append("#");
            cons.append(NEW_LINE);
        }

        sql.append("// SELECT").append(NEW_LINE);
        sql.append("SELECT").append(NEW_LINE);
        sql.append(cols);
        sql.append("FROM").append(NEW_LINE);
        sql.append("\t")
                .append(QuerySyntax.escapeKeyword(template.getTableName()))
                .append(NEW_LINE);
        sql.append("WHERE").append(NEW_LINE);
        sql.append(cons);
        sql.append(NEW_LINE);

        //
        // INSERT
        //
        len = template.getAttributes().size();
        index = 0;
        cols.delete(0, cols.length());
        cons.delete(0, cons.length());
        for (POJOAttribute attrbute : template.getAttributes()) {
            index++;
            String col = QuerySyntax.escapeKeyword(attrbute.getDbName());
            cols.append("\t").append(col);
            if (len > index) {
                cols.append(",");
            }
            cols.append(NEW_LINE);

            cons.append("\t#").append(getAttrbuteStyleName(col)).append("#");
            if (len > index) {
                cons.append(",");
            }
            cons.append(NEW_LINE);
        }

        sql.append("// INSERT").append(NEW_LINE);
        sql.append("INSERT INTO ")
                .append(QuerySyntax.escapeKeyword(template.getTableName()))
                .append(NEW_LINE);
        sql.append("(").append(NEW_LINE);
        sql.append(cols);
        sql.append(")").append(NEW_LINE);
        sql.append("VALUES").append(NEW_LINE);
        sql.append("(").append(NEW_LINE);
        sql.append(cons);
        sql.append(")").append(NEW_LINE);
        sql.append(NEW_LINE);

        //
        // UPDATE
        //
        len = template.getAttributes().size();
        index = 0;
        cols.delete(0, cols.length());
        cons.delete(0, cons.length());
        for (POJOAttribute attrbute : template.getAttributes()) {
            index++;
            String col = QuerySyntax.escapeKeyword(attrbute.getDbName());
            cols.append("\t");
            cols.append(col).append(" = #").append(getAttrbuteStyleName(col)).append("#");
            if (len > index) {
                cols.append(",");
            }
            cols.append(NEW_LINE);

            cons.append("\t");
            if (index > 1) {
                cons.append("AND ");
            }
            cons.append(col).append(" = #").append(getAttrbuteStyleName(col)).append("#");
            cons.append(NEW_LINE);
        }

        sql.append("// UPDATE").append(NEW_LINE);
        sql.append("UPDATE").append(NEW_LINE);
        sql.append("\t")
                .append(QuerySyntax.escapeKeyword(template.getTableName()))
                .append(NEW_LINE);
        sql.append("SET").append(NEW_LINE);
        sql.append(cols);
        sql.append("WHERE").append(NEW_LINE);
        sql.append(cons);
        sql.append(NEW_LINE);

        //
        // DELETE
        //
        len = template.getAttributes().size();
        index = 0;
        cols.delete(0, cols.length());
        cons.delete(0, cons.length());
        for (POJOAttribute attrbute : template.getAttributes()) {
            index++;
            String col = QuerySyntax.escapeKeyword(attrbute.getDbName());
            cons.append("\t");
            if (index > 1) {
                cons.append("AND ");
            }
            cons.append(col).append(" = #").append(getAttrbuteStyleName(col)).append("#");
            cons.append(NEW_LINE);
        }

        sql.append("// DELETE").append(NEW_LINE);
        sql.append("DELETE FROM").append(NEW_LINE);
        sql.append("\t")
                .append(QuerySyntax.escapeKeyword(template.getTableName()))
                .append(NEW_LINE);
        sql.append("WHERE").append(NEW_LINE);
        sql.append(cons);
        sql.append(NEW_LINE);

        return sql.toString();
    }

    /**
     * Get the Java POJO content string
     *
     * @param template
     * @return String - The POJO String
     */
    private static String getJavaPOJOString(POJOTemplate template) {
        StringBuffer sb = new StringBuffer();

        sb.append("/*").append(NEW_LINE);
        sb.append(getCRUDQuery(template));
        sb.append("*/").append(NEW_LINE);

        /* Imported package */
        for (POJOAttribute attrbute : template.getAttributes()) {
            if (StringUtil.isNotEmpty(attrbute.getImportPackage())) {
                template.getImportPackages().add(attrbute.getImportPackage());
            }
            if (StringUtil.isNotEmpty(attrbute.getElemImportPackage())) {
                template.getImportPackages().add(attrbute.getElemImportPackage());
            }
        }
        for (String importedPackage : template.getImportPackages()) {
            sb.append("import " + importedPackage + SEMICOLON + NEW_LINE);
        }

        /* Type declare */
        if (sb.length() > 0) {
            sb.append(NEW_LINE);
        }

        if (StringUtil.isNotEmpty(template.getAnnotation())) {
            sb.append(template.getAnnotation() + NEW_LINE);
        }
        sb.append(template.getTypeDeclare() + " {" + NEW_LINE);

        /* Attribute */
        for (POJOAttribute attribute : template.getAttributes()) {
            if (StringUtil.isNotEmpty(attribute.getAnnotation())) {
                sb.append(ONE_INDENTATION + attribute.getAnnotation() + NEW_LINE);
            }
            sb.append(
                    ONE_INDENTATION
                            + "private "
                            + getPOJOType(attribute)
                            + " "
                            + attribute.getJavaName()
                            + " = null");
            sb.append(SEMICOLON + NEW_LINE);
        }
        sb.append(NEW_LINE);
        /* Get and Set method */
        for (POJOAttribute attribute : template.getAttributes()) {
            /* Get method */
            if (StringUtil.isNotEmpty(attribute.getGetAnnotation())) {
                sb.append(attribute.getGetAnnotation() + NEW_LINE);
            }
            sb.append(attribute.getGetMethod() + NEW_LINE);
            /* Set method */
            if (StringUtil.isNotEmpty(attribute.getSetAnnotation())) {
                sb.append(attribute.getSetAnnotation() + NEW_LINE);
            }
            sb.append(attribute.getSetMethod() + NEW_LINE);
        }

        sb.append("}" + NEW_LINE);
        return sb.toString();
    }

    /**
     * Get the PHP POJO content string
     *
     * @param template
     * @return String - The POJO String
     */
    private static String getPhpPOJOString(POJOTemplate template) {
        StringBuffer sb = new StringBuffer();

        sb.append("<?php").append(NEW_LINE).append(NEW_LINE);

        sb.append("/*").append(NEW_LINE);
        sb.append(getCRUDQuery(template));
        sb.append("*/").append(NEW_LINE);

        /* Type declare */
        if (StringUtil.isNotEmpty(template.getAnnotation())) {
            sb.append(template.getAnnotation() + NEW_LINE);
        }
        sb.append(template.getTypeDeclare() + " {" + NEW_LINE);

        /* Attribute */
        for (POJOAttribute attribute : template.getAttributes()) {
            if (StringUtil.isNotEmpty(attribute.getAnnotation())) {
                sb.append(ONE_INDENTATION + attribute.getAnnotation() + NEW_LINE);
            }
            sb.append(ONE_INDENTATION + "private $" + attribute.getJavaName());
            sb.append(SEMICOLON + NEW_LINE);
        }
        sb.append(NEW_LINE);
        /* Get and Set method */
        for (POJOAttribute attribute : template.getAttributes()) {
            /* Get method */
            if (StringUtil.isNotEmpty(attribute.getGetAnnotation())) {
                sb.append(attribute.getGetAnnotation() + NEW_LINE);
            }
            sb.append(attribute.getGetMethod() + NEW_LINE);
            /* Set method */
            if (StringUtil.isNotEmpty(attribute.getSetAnnotation())) {
                sb.append(attribute.getSetAnnotation() + NEW_LINE);
            }
            sb.append(attribute.getSetMethod() + NEW_LINE);
        }

        sb.append("}" + NEW_LINE);
        sb.append("?>");
        return sb.toString();
    }

    /**
     * Init POJOAttribute
     *
     * @param attribute
     */
    private static void initJavaPOJOAttribute(POJOAttribute attribute) {

        /* The get method annotation */
        StringBuffer getAnnotationSB = new StringBuffer();
        getAnnotationSB.append(ONE_INDENTATION + "/**" + NEW_LINE);
        getAnnotationSB.append(
                ONE_INDENTATION
                        + " * "
                        + attribute.getJavaName()
                        + " ("
                        + attribute.getDbType()
                        + ")"
                        + NEW_LINE);
        getAnnotationSB.append(ONE_INDENTATION + " *" + NEW_LINE);
        getAnnotationSB.append(
                ONE_INDENTATION + " * @return " + attribute.getJavaName() + NEW_LINE);
        getAnnotationSB.append(ONE_INDENTATION + " */");
        attribute.setGetAnnotation(getAnnotationSB.toString());

        /* The get method */
        StringBuffer getMethodSB = new StringBuffer();
        getMethodSB.append(
                ONE_INDENTATION
                        + "public "
                        + getPOJOType(attribute)
                        + " get"
                        + attribute.getJavaName().substring(0, 1).toUpperCase()
                        + attribute.getJavaName().substring(1)
                        + "() {"
                        + NEW_LINE);
        getMethodSB.append(
                ONE_INDENTATION + "\treturn " + attribute.getJavaName() + SEMICOLON + NEW_LINE);
        getMethodSB.append(ONE_INDENTATION + "}" + NEW_LINE);
        attribute.setGetMethod(getMethodSB.toString());

        /* The set method annotation */
        StringBuffer setAnnotationSB = new StringBuffer();
        setAnnotationSB.append(ONE_INDENTATION + "/**" + NEW_LINE);
        setAnnotationSB.append(
                ONE_INDENTATION
                        + " * "
                        + attribute.getJavaName()
                        + " ("
                        + attribute.getDbType()
                        + ")"
                        + NEW_LINE);
        setAnnotationSB.append(ONE_INDENTATION + " *" + NEW_LINE);
        setAnnotationSB.append(ONE_INDENTATION + " * @param " + attribute.getJavaName() + NEW_LINE);
        setAnnotationSB.append(ONE_INDENTATION + " */");
        attribute.setSetAnnotation(setAnnotationSB.toString());

        /* The set method */
        StringBuffer setMethodSB = new StringBuffer();
        setMethodSB.append(
                ONE_INDENTATION
                        + "public void "
                        + "set"
                        + attribute.getJavaName().substring(0, 1).toUpperCase()
                        + attribute.getJavaName().substring(1)
                        + "("
                        + getPOJOType(attribute)
                        + " "
                        + attribute.getJavaName()
                        + ") {"
                        + NEW_LINE);

        setMethodSB.append(
                ONE_INDENTATION
                        + "\tthis."
                        + attribute.getJavaName()
                        + " = "
                        + attribute.getJavaName()
                        + SEMICOLON
                        + NEW_LINE);
        setMethodSB.append(ONE_INDENTATION + "}" + NEW_LINE);
        attribute.setSetMethod(setMethodSB.toString());
    }

    /**
     * Init POJOAttribute
     *
     * @param attribute
     */
    private static void initPhpPOJOAttribute(POJOAttribute attribute) {

        /* The get method annotation */
        StringBuffer getAnnotationSB = new StringBuffer();
        getAnnotationSB.append(ONE_INDENTATION + "/**" + NEW_LINE);
        getAnnotationSB.append(
                ONE_INDENTATION
                        + " * "
                        + attribute.getJavaName()
                        + " ("
                        + attribute.getDbType()
                        + ")"
                        + NEW_LINE);
        getAnnotationSB.append(ONE_INDENTATION + " *" + NEW_LINE);
        getAnnotationSB.append(
                ONE_INDENTATION + " * @return " + attribute.getJavaName() + NEW_LINE);
        getAnnotationSB.append(ONE_INDENTATION + " */");
        attribute.setGetAnnotation(getAnnotationSB.toString());

        /* The get method */
        StringBuffer getMethodSB = new StringBuffer();
        getMethodSB.append(
                ONE_INDENTATION
                        + "public function get"
                        + attribute.getJavaName().substring(0, 1).toUpperCase()
                        + attribute.getJavaName().substring(1)
                        + "() {"
                        + NEW_LINE);
        getMethodSB.append(
                ONE_INDENTATION
                        + "\treturn $this->"
                        + attribute.getJavaName()
                        + SEMICOLON
                        + NEW_LINE);
        getMethodSB.append(ONE_INDENTATION + "}" + NEW_LINE);
        attribute.setGetMethod(getMethodSB.toString());

        /* The set method annotation */
        StringBuffer setAnnotationSB = new StringBuffer();
        setAnnotationSB.append(ONE_INDENTATION + "/**" + NEW_LINE);
        setAnnotationSB.append(
                ONE_INDENTATION
                        + " * "
                        + attribute.getJavaName()
                        + " ("
                        + attribute.getDbType()
                        + ")"
                        + NEW_LINE);
        setAnnotationSB.append(ONE_INDENTATION + " *" + NEW_LINE);
        setAnnotationSB.append(ONE_INDENTATION + " * @param " + attribute.getJavaName() + NEW_LINE);
        setAnnotationSB.append(ONE_INDENTATION + " */");
        attribute.setSetAnnotation(setAnnotationSB.toString());

        /* The set method */
        StringBuffer setMethodSB = new StringBuffer();
        setMethodSB.append(
                ONE_INDENTATION
                        + "public function "
                        + "set"
                        + attribute.getJavaName().substring(0, 1).toUpperCase()
                        + attribute.getJavaName().substring(1)
                        + "($"
                        + attribute.getJavaName()
                        + ") {"
                        + NEW_LINE);

        setMethodSB.append(
                ONE_INDENTATION
                        + "\t$this->"
                        + attribute.getJavaName()
                        + " = $"
                        + attribute.getJavaName()
                        + SEMICOLON
                        + NEW_LINE);
        setMethodSB.append(ONE_INDENTATION + "}" + NEW_LINE);
        attribute.setSetMethod(setMethodSB.toString());
    }

    /**
     * Get the POJO type by POJOAttribute
     *
     * @param attribute
     * @return String - The POJO type
     */
    private static String getPOJOType(POJOAttribute attribute) {
        if (attribute.getElemType() == null) {
            return attribute.getJavaType();
        } else {
            return attribute.getJavaType() + "<" + attribute.getElemType() + ">";
        }
    }

    /**
     * Get the java type model
     *
     * @param type String
     * @param precision Integer
     * @param scale Integer
     * @return ClassModel - The java type model
     */
    private static JavaType getJaveType(String type, Integer precision, Integer scale) {
        if (type == null) {
            return null;
        }

        String upperType = type.toUpperCase(Locale.ENGLISH);
        String key = null;
        if (DataType.DATATYPE_DECIMAL.equals(upperType)
                || DataType.DATATYPE_NUMERIC.equals(upperType)) {
            if (scale == 0) {
                if (precision <= 9) {
                    key = getKey(upperType, 9, 0);
                } else if (precision <= 18) {
                    key = getKey(upperType, 18, 0);
                } else {
                    key = getKey(upperType, 38, 0);
                }
            } else {
                key = getKey(upperType, -1, -1);
            }
        } else {
            key = getKey(upperType, -1, -1);
        }
        return TYPE_MAPPIING.get(key);
    }

    /**
     * Get the map key
     *
     * @param type
     * @param precision
     * @param scale
     * @return
     */
    private static String getKey(String type, int precision, int scale) {
        StringBuffer key = new StringBuffer();

        key.append(type.toUpperCase());
        if (precision > -1) {
            key.append("_" + precision);
        }
        if (scale > -1) {
            key.append("_" + scale);
        }
        return key.toString();
    }
}

/**
 * The model of java type. Used for map database type to java type
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-02-29 create by Kevin.Wang
 */
class JavaType {

    private String javaPackage;
    private String type;

    /**
     * The constructor
     *
     * @param javaPackage - String
     * @param type - String
     */
    public JavaType(String javaPackage, String type) {
        this.javaPackage = javaPackage;
        this.type = type;
    }

    /**
     * Get type
     *
     * @return type String
     */
    public String getType() {
        return type;
    }

    /**
     * Set type
     *
     * @param type String
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get javaPackage
     *
     * @return javaPackage String
     */
    public String getJavaPackage() {
        return javaPackage;
    }

    /**
     * Set javaPackage
     *
     * @param javaPackage String
     */
    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }
}
