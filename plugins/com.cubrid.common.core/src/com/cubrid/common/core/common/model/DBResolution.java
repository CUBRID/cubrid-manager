/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.cubrid.common.core.common.model;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.core.util.LogUtil;
import org.slf4j.Logger;

/**
 * this class is to store information to choose inherit which column in super classes when
 * inheriting some super classes
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-5 created by moulinwang
 */
public class DBResolution implements Cloneable {
    private static final Logger LOGGER = LogUtil.getLogger(DBResolution.class);
    private String name;
    private String tableName;
    private String alias;
    private boolean isClassResolution;

    public DBResolution(String name, String tableName, String alias) {
        this.name = name;
        this.tableName = tableName;
        this.alias = alias;
    }

    /**
     * Override the method of Object
     *
     * @return DBResolution a clone of this instance.
     */
    @Override
    public DBResolution clone() {
        try {
            return (DBResolution) super.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.debug(e.getMessage(), e);
        }
        return null;
    }

    public DBResolution() {
        noOp();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Get the alias
     *
     * @return String The alias
     */
    public String getAlias() {
        if (alias == null) {
            return "";
        }
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /** @return int a hash code value for this object */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * @param obj Object the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) { // FIXME more simplify
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DBResolution other = (DBResolution) obj;
        if (tableName == null) {
            if (other.tableName != null) {
                return false;
            }
        } else if (!tableName.equals(other.tableName)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public boolean isClassResolution() {
        return isClassResolution;
    }

    public void setClassResolution(boolean isClassResolution) {
        this.isClassResolution = isClassResolution;
    }
}
