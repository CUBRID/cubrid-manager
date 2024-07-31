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
package com.cubrid.cubridmanager.ui.mondashboard.editor.model;

/**
 * connection model base class.
 *
 * @author cyl
 * @version 1.0 - 2010-6-8 created by cyl
 */
public class HANodeConnection {

    protected HANode source;
    protected HANode target;

    /**
     * @param source HANode can't be null
     * @param target HANode can't be null
     */
    public HANodeConnection(HANode source, HANode target) {
        isAvailable(source, target);
        this.source = source;
        this.target = target;
        source.addOutput(this);
        target.addInput(this);
    }

    /**
     * check input is available.
     *
     * @param source can not be null
     * @param target can not be null
     */
    private void isAvailable(HANode source, HANode target) {
        if (null == source || null == target) {
            throw new IllegalArgumentException("Source node or target node can't be null.");
        }
    }

    public HANode getSource() {
        return source;
    }

    /**
     * set source of connection.If param source is null,auto remove this connection from
     * this.source's outputs.
     *
     * @param source HANode
     */
    public void setSource(HANode source) {
        if (null == source) {
            if (null != this.source) {
                this.source.removeOutput(this);
                this.source = null;
            }
        } else {
            this.source = source;
            source.addOutput(this);
        }
    }

    public HANode getTarget() {
        return target;
    }

    /**
     * set target of connection
     *
     * @param target HANode
     */
    public void setTarget(HANode target) {
        if (null == target) {
            if (null != this.target) {
                this.target.removeInput(this);
                this.target = null;
            }
        } else {
            this.target = target;
            target.addInput(this);
        }
    }

    /**
     * override default equals.]
     *
     * @param obj Object
     * @return is equals.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof HANodeConnection) {
            HANodeConnection conn = (HANodeConnection) obj;
            return (this.getSource() == conn.getSource() && this.getTarget() == conn.getTarget());
        }
        return false;
    }

    /**
     * override default hashCode
     *
     * @return hash code.
     */
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * override default toString
     *
     * @return Connection to string.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(source == null ? "Null" : source.toString())
                .append(":")
                .append(target == null ? "Null" : target.toString());
        return sb.toString();
    }
}
