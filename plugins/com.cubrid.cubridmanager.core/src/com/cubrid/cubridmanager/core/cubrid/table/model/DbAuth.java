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
 * This class includes the information of the database authority
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-09-13 created by lizhiqiang
 */
public class DbAuth {
    private String grantorName;
    private String granteeName;
    private String className;
    private String authType;
    private boolean isGrantable;

    /**
     * Get the grantorName
     *
     * @return the grantorName
     */
    public String getGrantorName() {
        return grantorName;
    }

    /** @param grantorName the grantorName to set */
    public void setGrantorName(String grantorName) {
        this.grantorName = grantorName;
    }

    /**
     * Get the granteeName
     *
     * @return the granteeName
     */
    public String getGranteeName() {
        return granteeName;
    }

    /** @param granteeName the granteeName to set */
    public void setGranteeName(String granteeName) {
        this.granteeName = granteeName;
    }

    /**
     * Get the className
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
     * Get the authType
     *
     * @return the authType
     */
    public String getAuthType() {
        return authType;
    }

    /** @param authType the authType to set */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * Get the isGrantable
     *
     * @return the isGrantable
     */
    public boolean isGrantable() {
        return isGrantable;
    }

    /** @param isGrantable the isGrantable to set */
    public void setGrantable(String isGrantable) {
        if (isGrantable.equalsIgnoreCase("YES")) {
            this.isGrantable = true;
        } else {
            this.isGrantable = false;
        }
    }
}
