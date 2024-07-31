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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the pojo template of table
 *
 * @author Kevin.Wang
 * @version 1.0 -2012 create by Kevin.Wang
 */
public class POJOTemplate {
    /* Database attribute */
    private String tableName;
    private List<POJOAttribute> attributes = new ArrayList<POJOAttribute>();

    /* Java attribute */
    private Set<String> importPackages = new LinkedHashSet<String>();
    private String annotation;
    private String typeDeclare;

    /**
     * Get tableName
     *
     * @return tableName String
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * set tableName
     *
     * @param tableName String
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Get attributes
     *
     * @return attributes List<POJOAttribute>
     */
    public List<POJOAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Set attributes
     *
     * @param attributes List<POJOAttribute>
     */
    public void setAttributes(List<POJOAttribute> attributes) {
        this.attributes = attributes;
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
     * Set String
     *
     * @param annotation String
     */
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * Get typeDeclare
     *
     * @return typeDeclare String
     */
    public String getTypeDeclare() {
        return typeDeclare;
    }

    /**
     * Set typeDeclare
     *
     * @param typeDeclare String
     */
    public void setTypeDeclare(String typeDeclare) {
        this.typeDeclare = typeDeclare;
    }

    /**
     * Get importPackages
     *
     * @return importPackages Set<String>
     */
    public Set<String> getImportPackages() {
        return importPackages;
    }

    /**
     * Set importPackages
     *
     * @param importPackages Set<String>
     */
    public void setImportPackages(Set<String> importPackages) {
        this.importPackages = importPackages;
    }
}
