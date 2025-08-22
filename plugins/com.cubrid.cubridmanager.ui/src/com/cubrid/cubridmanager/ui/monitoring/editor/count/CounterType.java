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
package com.cubrid.cubridmanager.ui.monitoring.editor.count;

/**
 * @author lcl
 * @version 1.1 - 2009-9-10 created by lcl
 */
public class CounterType {

    private static final int BITS_MULTI = 1 << 0; // multi (min, max, sum, last) or single value
    private static final int BITS_RANGE = 3 << 1; // value scope. 1, 2, 4 or 8 bytes
    private static final int BITS_COUNTER = 3 << 3; // GAUGE or COUNTER

    protected final int mode;
    protected final String name;

    public CounterType(String name, boolean multi, boolean counter, RangeType rangeType) {
        this.name = name;
        this.mode = computeMode(multi, counter, rangeType);
    }

    protected CounterType(String name, int mode) {
        this.name = name;
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public boolean isMulti() {
        return (mode & BITS_MULTI) != 0;
    }

    public boolean isCounter() {
        return (mode & BITS_COUNTER) != 0;
    }

    /**
     * get range type
     *
     * @return range type
     */
    public RangeType getRangeType() {

        int val = (mode & BITS_RANGE) >>> 1;

        for (RangeType rt : RangeType.values()) {
            if (rt.getValue() == val) {
                return rt;
            }
        }

        return null;
    }

    public int getMode() {
        return mode;
    }

    /**
     * compute mode
     *
     * @param multi is multi-type or not
     * @param counter is counter or not
     * @param rangeType range type
     * @return mode value as int
     */
    public static int computeMode(boolean multi, boolean counter, RangeType rangeType) {
        int mode = 0;

        if (multi) {
            mode |= BITS_MULTI;
        }

        if (counter) {
            mode |= BITS_COUNTER;
        }

        mode |= rangeType.getValue() << 1;
        return mode;
    }
}
