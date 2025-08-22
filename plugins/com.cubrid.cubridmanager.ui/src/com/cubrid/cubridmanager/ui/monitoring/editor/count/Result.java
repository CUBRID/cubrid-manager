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
public abstract class Result {

    /**
     * get index by name
     *
     * @param name type name
     * @return int object. or null if not found.
     */
    protected abstract Integer nameToIndex(String name);

    /**
     * get count
     *
     * @param col col
     * @return count
     */
    public abstract long getCount(int col);

    /**
     * get sum
     *
     * @param col col
     * @return sum
     */
    public abstract long getSum(int col);

    /**
     * get minimal value
     *
     * @param col col
     * @return minumal value
     */
    protected abstract long getMinRaw(int col);

    /**
     * get maximum value
     *
     * @param col col
     * @return maximum value
     */
    protected abstract long getMaxRaw(int col);

    /**
     * get last value
     *
     * @param col col
     * @return last value
     */
    protected abstract long getLastRaw(int col);

    /**
     * get count by name
     *
     * @param name name
     * @return count
     */
    public long getCount(String name) {
        Integer col = nameToIndex(name);
        return col == null ? 0 : getCount(col);
    }

    /**
     * get sum by name
     *
     * @param name name
     * @return sum.
     */
    public long getSum(String name) {
        Integer col = nameToIndex(name);
        return col == null ? 0 : getSum(col);
    }

    /**
     * get min by name
     *
     * @param name name
     * @return min
     */
    public long getMin(String name) {
        Integer col = nameToIndex(name);
        return col == null ? 0 : getMinRaw(col) / 100L;
    }

    /**
     * get min by column index
     *
     * @param col column index
     * @return min value
     */
    public long getMin(int col) {
        return getMinRaw(col) / 100L;
    }

    /**
     * get min value as double
     *
     * @param name type name
     * @return min value
     */
    public double getMinAsDouble(String name) {
        Integer col = nameToIndex(name);
        return col == null ? 0f : getMinRaw(col) / 100f;
    }

    /**
     * get min value by column index
     *
     * @param col column index
     * @return min value
     */
    public double getMinAsDouble(int col) {
        return getMinRaw(col) / 100f;
    }

    /**
     * get max value by type
     *
     * @param name type name
     * @return max value
     */
    public long getMax(String name) {
        Integer col = nameToIndex(name);
        return col == null ? 0 : getMaxRaw(col) / 100L;
    }

    /**
     * get max value by column index
     *
     * @param col column index
     * @return max value
     */
    public long getMax(int col) {
        return getMaxRaw(col) / 100L;
    }

    /**
     * get max value as double
     *
     * @param name type name
     * @return max value
     */
    public double getMaxAsDouble(String name) {
        Integer col = nameToIndex(name);
        return col == null ? 0f : getMaxRaw(col) / 100f;
    }

    /**
     * get max value as double
     *
     * @param col column index
     * @return max value
     */
    public double getMaxAsDouble(int col) {
        return getMaxRaw(col) / 100f;
    }

    /**
     * get last value by type
     *
     * @param name type name
     * @return last value
     */
    public long getLast(String name) {
        Integer col = nameToIndex(name);
        return col == null ? 0 : getLastRaw(col) / 100L;
    }

    /**
     * get last value by column index
     *
     * @param col column index
     * @return last value
     */
    public long getLast(int col) {
        return getLastRaw(col) / 100L;
    }

    /**
     * get last value as double
     *
     * @param name type name
     * @return last value
     */
    public double getLastAsDouble(String name) {
        Integer col = nameToIndex(name);
        return col == null ? 0f : getLastRaw(col) / 100f;
    }

    /**
     * get last value as double
     *
     * @param col column index
     * @return last value
     */
    public double getLastAsDouble(int col) {
        return getLastRaw(col) / 100f;
    }

    /**
     * get avg value by type
     *
     * @param name type name
     * @return avg value
     */
    public long getAvg(String name) {
        Integer col = nameToIndex(name);

        if (col == null) {
            return 0;
        }

        long c = getCount(col);
        return c == 0 ? CounterFile.INVALID_VALUE : (getSum(col) / c);
    }

    /**
     * get avg value by column index
     *
     * @param col column index
     * @return avg value
     */
    public long getAvg(int col) {
        long c = getCount(col);
        return c == 0 ? CounterFile.INVALID_VALUE : getSum(col) / c;
    }

    /**
     * get avg as double
     *
     * @param name type name
     * @return avg value
     */
    public double getAvgAsDouble(String name) {
        Integer col = nameToIndex(name);
        if (col == null) {
            return 0f;
        }
        long c = getCount(col);
        return c == 0 ? CounterFile.INVALID_VALUE : (double) getSum(col) / c;
    }

    /**
     * get avg as double
     *
     * @param col column index
     * @return avg value
     */
    public double getAvgAsDouble(int col) {
        return (double) getSum(col) / getCount(col);
    }
}
