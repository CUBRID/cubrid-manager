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
package com.cubrid.cubridmanager.core.cubrid.user.model;

/**
 * DBAuth Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-2 created by Kevin.Wang
 */
public class DBAuth {
    private String className;
    private AuthType authType;
    private String owner;
    private boolean isTable;

    /**
     * The constructor
     *
     * @param grantorName
     * @param granteeName
     * @param className
     * @param authType
     * @param isGrantAble
     */
    public DBAuth(String className, AuthType authType) {
        this.className = className;
        this.authType = authType;
    }

    /**
     * The constructor
     *
     * @param className
     * @param owner
     * @param isTable
     */
    public DBAuth(String className, String owner, boolean isTable) {
        this.className = className;
        this.owner = owner;
        this.isTable = isTable;
    }

    /**
     * Get className
     *
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /** @param className the className to set */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get authType
     *
     * @return the authType
     */
    public AuthType getAuthType() {
        return authType;
    }

    /** @param authType the authType to set */
    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    /**
     * Get the owner
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /** @param owner the owner to set */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Is table
     *
     * @return the isTable
     */
    public boolean isTable() {
        return isTable;
    }

    /** @param isTable the isTable to set */
    public void setTable(boolean isTable) {
        this.isTable = isTable;
    }
}
