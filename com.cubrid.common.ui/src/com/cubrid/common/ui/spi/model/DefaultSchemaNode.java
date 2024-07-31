/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi.model;

/**
 * All database schema node must extend this class or use this class
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class DefaultSchemaNode extends DefaultCubridNode implements ISchemaNode {

    private CubridDatabase cubridDatabase = null;
    private String UniqueName = null;

    /**
     * The constructor
     *
     * @param id
     * @param label
     * @param iconPath
     */
    public DefaultSchemaNode(String id, String label, String iconPath) {
        super(id, label, iconPath);
        this.UniqueName = label;
    }

    public DefaultSchemaNode(String id, String label, String UniqueName, String iconPath) {
        super(id, label, UniqueName, iconPath);
        this.UniqueName = UniqueName;
    }

    /**
     * Get CUBRID Database node
     *
     * @return the CubridDatabase object
     */
    public CubridDatabase getDatabase() {
        return cubridDatabase;
    }

    /**
     * Set CUBRID database node
     *
     * @param database the CubridDatabase object
     */
    public void setDatabase(CubridDatabase database) {
        this.cubridDatabase = database;
    }

    /**
     * Add child object to this node
     *
     * @param obj the ICubridNode object
     */
    public void addChild(ICubridNode obj) {
        if (obj instanceof ISchemaNode && !isContained(obj)) {
            ISchemaNode schemaNode = (ISchemaNode) obj;
            schemaNode.setDatabase(this.getDatabase());
        }
        super.addChild(obj);
    }

    /**
     * Compare the object for sorter in the same tree level
     *
     * @param obj the ICubridNode object
     * @return <code>1<code> greater;<code>0</code>equal;<code>-1</code> less
     */
    public int compareTo(ISchemaNode obj) {
        if (obj == null) {
            return 1;
        }
        // sort for tables, SYSTEM_TABLE_FOLDER and SYSTEM_VIEW_FOLDER is the first
        if (NodeType.SYSTEM_TABLE_FOLDER.equals(getType())
                || NodeType.SYSTEM_VIEW_FOLDER.equals(getType())) {
            return -1;
        } else if (NodeType.SYSTEM_TABLE_FOLDER.equals(obj.getType())
                || NodeType.SYSTEM_VIEW_FOLDER.equals(obj.getType())) {
            return 1;
        }
        return super.compareTo(obj);
    }

    /**
     * Return whether the current object is equal the obj
     *
     * @param obj the object
     * @return <code>true</code> if they are equal;<code>false</code> otherwise
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof DefaultSchemaNode)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Return the hash code value
     *
     * @return the hash code value
     */
    public int hashCode() {
        return this.getId().hashCode();
    }

    public DefaultSchemaNode clone() throws CloneNotSupportedException {
        DefaultSchemaNode obj = (DefaultSchemaNode) super.clone();
        return obj;
    }

    @Override
    public String getName() {
        return this.UniqueName;
    }

    public void setUniqueName(String UniqueName) {
        this.UniqueName = UniqueName;
    }
}
