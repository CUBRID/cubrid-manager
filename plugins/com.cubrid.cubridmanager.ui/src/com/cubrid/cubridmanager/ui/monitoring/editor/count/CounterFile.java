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

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Storage one counter data.
 *
 * @author lcl
 * @version 1.1 - 2009-9-10 created by lcl
 */
public interface CounterFile extends Closeable {

    public static final int INVALID_VALUE = -1;
    public static final int NULL_TIME = -1;

    /**
     * Update counter data
     *
     * @param time time
     * @param type type
     * @param value value
     * @throws IOException exception for IO operation
     */
    void updateData(long time, String type, long value) throws IOException;

    /**
     * Read counter data
     *
     * @param time time
     * @param types types
     * @return result object
     * @throws IOException exception for IO operation
     */
    Result readData(long time, String... types) throws IOException;

    /**
     * Read counter as long
     *
     * @param time time
     * @param typeArgs typeArgs
     * @return long integer value array
     * @throws IOException exception for IO operation
     */
    long[] readDataAsLong(long time, String... typeArgs) throws IOException;

    /**
     * Get minimum time for all counter data. since 1970-01-01 00:00:00 UTC
     *
     * @return begin time since 1970-01-01 UTC in milliseconds
     */
    long getBeginTime();

    /**
     * Get time duration for all counter data (in second). equals to (getEndTime() - getBeginTime())
     * / 1000
     *
     * @return total seconds
     */
    int getDuration();

    /**
     * Get maximum time for all counter data. since 1970-01-01 00:00:00 UTC
     *
     * @return end time since 1970-01-01 UTC in milliseconds
     */
    long getEndTime();

    /**
     * Get duration between two neighboring row. in second.
     *
     * @return interval in second
     */
    int getInterval();

    /**
     * Row capacity.
     *
     * @return count of the counter file capacity
     */
    int getMaxCount();

    /** @return current store counter data count */
    int getCount();

    /**
     * The mode specfied when create counter file.
     *
     * @return mode
     */
    int getMode();

    /**
     * Index of the type. one counter file can have more counter data.
     *
     * @param type type
     * @return index. if type not fount, return -1
     */
    int indexOfType(String type);

    /**
     * Get all type from file
     *
     * @return type object array
     */
    CounterType[] getTypes();

    /**
     * Retrieve external properties of file
     *
     * @return properties
     */
    Properties getProperties();

    /**
     * get property as string
     *
     * @param key key
     * @return string value
     */
    String getProperty(String key);

    /**
     * get property as int
     *
     * @param key key
     * @param def default value. if key not found or conversion failed.
     * @return int value
     */
    int getPropertyInt(String key, int def);

    /**
     * get property as long
     *
     * @param key key
     * @param def default value
     * @return long value
     */
    long getPropertyLong(String key, long def);

    /**
     * get property as boolean
     *
     * @param key key
     * @param def default value
     * @return boolean value
     */
    boolean getPropertyBool(String key, boolean def);

    /**
     * get property as double
     *
     * @param key key
     * @param def default value
     * @return double value
     */
    double getPropertyDouble(String key, double def);

    /**
     * set property
     *
     * @param key key
     * @param value value
     * @throws IOException exception for IO operation
     */
    void setProperty(String key, String value) throws IOException;

    /**
     * set multi properties in one IO operation
     *
     * @param pairs key-value pairs
     * @throws IOException exception for IO operation
     */
    void setProperties(Map<String, String> pairs) throws IOException;
}
