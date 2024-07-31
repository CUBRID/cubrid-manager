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
package com.cubrid.cubridmanager.core.cubrid.table.model;

/**
 * This is POJO attribute, similar as table's column
 *
 * @author Kevin.Wang
 * @version 1.0 -2012 create by Kevin.Wang
 */
public class POJOAttribute {
    /* Database object properties */
    private String dbName;
    private String dbType;
    private Integer dbPrecision;
    private Integer dbScale;
    private String dbElemType;
    private Integer dbElemPrecision;
    private Integer dbElemScale;
    private String dbDefaultValue;

    /* Java attribute */
    private String annotation;
    private String javaType;
    private String javaName;
    private String importPackage;
    private String javaDefaultValue;
    private String getAnnotation;
    private String getMethod;
    private String setAnnotation;
    private String setMethod;
    private String elemType;
    private String elemImportPackage;

    /**
     * Get the column name in database
     *
     * @return dbName String
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Set the dbName
     *
     * @param dbName String
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Get the dbType
     *
     * @return dbType String
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * Set the dbType
     *
     * @param dbType String
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    /**
     * Get the dbPrecision
     *
     * @return dbPrecision Integer
     */
    public Integer getDbPrecision() {
        return dbPrecision;
    }

    /**
     * Get dbPrecision
     *
     * @param dbPrecision Integer
     */
    public void setDbPrecision(Integer dbPrecision) {
        this.dbPrecision = dbPrecision;
    }

    /**
     * Get dbScale
     *
     * @return dbScale Integer
     */
    public Integer getDbScale() {
        return dbScale;
    }

    /**
     * Set dbScale
     *
     * @param dbScale Integer
     */
    public void setDbScale(Integer dbScale) {
        this.dbScale = dbScale;
    }

    /**
     * Get dbDefaultValue
     *
     * @return dbDefaultValue String
     */
    public String getDbDefaultValue() {
        return dbDefaultValue;
    }

    /**
     * Set dbDefaultValue
     *
     * @param dbDefaultValue String
     */
    public void setDbDefaultValue(String dbDefaultValue) {
        this.dbDefaultValue = dbDefaultValue;
    }

    /**
     * Get javaName
     *
     * @return javaName String
     */
    public String getJavaName() {
        return javaName;
    }

    /**
     * Set javaName
     *
     * @param javaName String
     */
    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    /**
     * Get annotation
     *
     * @return annotation String
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * Set annotation
     *
     * @param annotation String
     */
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * Get javaType
     *
     * @return javaType String
     */
    public String getJavaType() {
        return javaType;
    }

    /**
     * Set javaType
     *
     * @param javaType String
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /**
     * Get importPackage
     *
     * @return importPackage String
     */
    public String getImportPackage() {
        return importPackage;
    }

    /**
     * Set importPackage
     *
     * @param importPackage String
     */
    public void setImportPackage(String importPackage) {
        this.importPackage = importPackage;
    }

    /**
     * Get getAnnotation
     *
     * @return getAnnotation String
     */
    public String getGetAnnotation() {
        return getAnnotation;
    }

    /**
     * Set getAnnotation
     *
     * @param getAnnotation String
     */
    public void setGetAnnotation(String getAnnotation) {
        this.getAnnotation = getAnnotation;
    }

    /**
     * Get getMethod
     *
     * @return getMethod String
     */
    public String getGetMethod() {
        return getMethod;
    }

    /**
     * Set getMethod
     *
     * @param getMethod String
     */
    public void setGetMethod(String getMethod) {
        this.getMethod = getMethod;
    }

    /**
     * Get setAnnotation
     *
     * @return setAnnotation String
     */
    public String getSetAnnotation() {
        return setAnnotation;
    }

    /**
     * Set setAnnotation String
     *
     * @param setAnnotation
     */
    public void setSetAnnotation(String setAnnotation) {
        this.setAnnotation = setAnnotation;
    }

    /**
     * Get setMethod
     *
     * @return setMethod String
     */
    public String getSetMethod() {
        return setMethod;
    }

    /**
     * Set setMethod
     *
     * @param setMethod String
     */
    public void setSetMethod(String setMethod) {
        this.setMethod = setMethod;
    }

    /**
     * Get javaDefaultValue
     *
     * @return javaDefaultValue String
     */
    public String getJavaDefaultValue() {
        return javaDefaultValue;
    }

    /**
     * Set javaDefaultValue
     *
     * @param javaDefaultValue String
     */
    public void setJavaDefaultValue(String javaDefaultValue) {
        this.javaDefaultValue = javaDefaultValue;
    }

    /**
     * Get dbElemType
     *
     * @return dbElemType String
     */
    public String getDbElemType() {
        return dbElemType;
    }

    /**
     * Set dbElemType
     *
     * @param dbElemType String
     */
    public void setDbElemType(String dbElemType) {
        this.dbElemType = dbElemType;
    }

    /**
     * Get elemType
     *
     * @return elemType String
     */
    public String getElemType() {
        return elemType;
    }

    /**
     * Set elemType
     *
     * @param elemType String
     */
    public void setElemType(String elemType) {
        this.elemType = elemType;
    }

    /**
     * Get elemImportPackage
     *
     * @return elemImportPackage String
     */
    public String getElemImportPackage() {
        return elemImportPackage;
    }

    /**
     * Set elemImportPackage
     *
     * @param elemImportPackage String
     */
    public void setElemImportPackage(String elemImportPackage) {
        this.elemImportPackage = elemImportPackage;
    }

    /**
     * Get dbElemPrecision
     *
     * @return Integer
     */
    public Integer getDbElemPrecision() {
        return dbElemPrecision;
    }

    /**
     * Set dbElemPrecision
     *
     * @param dbElemPrecision Integer
     */
    public void setDbElemPrecision(Integer dbElemPrecision) {
        this.dbElemPrecision = dbElemPrecision;
    }

    /**
     * Get dbElemScale
     *
     * @return dbElemScale Integer
     */
    public Integer getDbElemScale() {
        return dbElemScale;
    }

    /**
     * Set dbElemScale
     *
     * @param dbElemScale Integer
     */
    public void setDbElemScale(Integer dbElemScale) {
        this.dbElemScale = dbElemScale;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("POJOAttribute [");
        sb.append("dbName = ").append(dbName == null ? "" : dbName);
        sb.append(", dbType = ").append(dbType == null ? "" : dbName);
        sb.append(", dbPrecision = ").append(dbPrecision == null ? "" : dbPrecision);
        sb.append(", dbScale = ").append(dbScale == null ? "" : dbScale);
        sb.append(", dbElemType = ").append(dbElemType == null ? "" : dbElemType);
        sb.append(", dbElemPrecision = ").append(dbElemPrecision == null ? "" : dbElemPrecision);
        sb.append(", dbElemScale = ").append(dbElemScale == null ? "" : dbElemScale);
        sb.append(", dbDefaultValue = ").append(dbDefaultValue == null ? "" : dbDefaultValue);
        sb.append("]");

        return sb.toString();
    }
}
