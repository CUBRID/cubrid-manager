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
package com.cubrid.common.core.queryplan.model;

/**
 * Plan Cost model class
 *
 * <p>PlanCost Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public class PlanCost {
    // TODO There will be deprecated since CUBRID Tools 2013 version.
    private float fixedTotal = 0;
    private float fixedCpu = 0;
    private float fixedDisk = 0;
    private float varTotal = 0;
    private float varCpu = 0;
    private float varDisk = 0;

    private int total = 0;
    private int card = 0;

    /**
     * Convert to string
     *
     * @return string
     */
    public String toString() { // FIXME use ToStringBuilder
        StringBuilder out = new StringBuilder();
        out.append("PlanCost[");
        out.append("total=").append(total);
        out.append(", card=").append(card);
        out.append("]");
        return out.toString();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public float getFixedTotal() {
        return fixedTotal;
    }

    public void setFixedTotal(float fixedTotal) {
        this.fixedTotal = fixedTotal;
    }

    public float getFixedCpu() {
        return fixedCpu;
    }

    public void setFixedCpu(float fixedCpu) {
        this.fixedCpu = fixedCpu;
    }

    public float getFixedDisk() {
        return fixedDisk;
    }

    public void setFixedDisk(float fixedDisk) {
        this.fixedDisk = fixedDisk;
    }

    public float getVarTotal() {
        return varTotal;
    }

    public void setVarTotal(float varTotal) {
        this.varTotal = varTotal;
    }

    public float getVarCpu() {
        return varCpu;
    }

    public void setVarCpu(float varCpu) {
        this.varCpu = varCpu;
    }

    public float getVarDisk() {
        return varDisk;
    }

    public void setVarDisk(float varDisk) {
        this.varDisk = varDisk;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }
}
