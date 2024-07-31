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

/**
 * Count class in order to know how many queries for each types in queries
 *
 * @author Isaiah Choe
 */
public class QueryTypeCounts {
    private int selects;
    private int inserts;
    private int updates;
    private int deletes;
    private int creates;
    private int alters;
    private int drops;
    private int extras;

    public boolean existModifyingQuery() {
        return updates > 0 || inserts > 0 || deletes > 0 || creates > 0 || alters > 0 || drops > 0;
    }

    public int getSelects() {
        return selects;
    }

    public void setSelects(int selects) {
        this.selects = selects;
    }

    public void increaseSelects() {
        this.selects++;
    }

    public int getInserts() {
        return inserts;
    }

    public void setInserts(int inserts) {
        this.inserts = inserts;
    }

    public void increaseInserts() {
        this.inserts++;
    }

    public int getUpdates() {
        return updates;
    }

    public void setUpdates(int updates) {
        this.updates = updates;
    }

    public void increaseUpdates() {
        this.updates++;
    }

    public int getDeletes() {
        return deletes;
    }

    public void setDeletes(int deletes) {
        this.deletes = deletes;
    }

    public void increaseDeletes() {
        this.deletes++;
    }

    public int getCreates() {
        return creates;
    }

    public void setCreates(int creates) {
        this.creates = creates;
    }

    public void increaseCreates() {
        this.creates++;
    }

    public int getAlters() {
        return alters;
    }

    public void setAlters(int alters) {
        this.alters = alters;
    }

    public void increaseAlters() {
        this.alters++;
    }

    public int getDrops() {
        return drops;
    }

    public void setDrops(int drops) {
        this.drops = drops;
    }

    public void increaseDrops() {
        this.drops++;
    }

    public int getExtras() {
        return extras;
    }

    public void setExtras(int extras) {
        this.extras = extras;
    }

    public void increaseExtras() {
        this.extras++;
    }
}
