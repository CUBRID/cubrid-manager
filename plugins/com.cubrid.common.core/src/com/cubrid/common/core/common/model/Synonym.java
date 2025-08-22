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


public class Synonym implements Comparable<Synonym> {
    private String name;
    private String owner;
    private String targetName;
    private String targetOwner;
    private String comment;

    /**
     * compare to the argument obj
     *
     * @param obj Synonym
     * @return int
     */
    public int compareTo(Synonym obj) {
        return getUniqueName().compareTo(obj.getUniqueName());
    }

    /**
     * compare to the argument obj
     *
     * @param obj Synonym
     * @return int
     */
    public int targetCompareTo(Synonym obj) {
        return getTargetUniqueName().compareTo(obj.getTargetUniqueName());
    }

    /**
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Synonym && getUniqueName().equals(((Synonym) obj).getUniqueName());
    }

    public boolean targetEquals(Object obj) {
        return obj instanceof Synonym
                && getTargetUniqueName().equals(((Synonym) obj).getTargetUniqueName());
    }

    /** @return a hash code value for this object. */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String name) {
        this.targetName = name;
    }

    public String getTargetOwner() {
        return targetOwner;
    }

    public void setTargetOwner(String owner) {
        this.targetOwner = owner;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getUniqueName() {
        return owner + "." + name;
    }

    public String getTargetUniqueName() {
        return targetOwner + "." + targetName;
    }
}
