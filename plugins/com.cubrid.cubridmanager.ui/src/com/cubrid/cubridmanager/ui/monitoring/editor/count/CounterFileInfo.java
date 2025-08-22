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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Counter file information object
 *
 * @author lcl
 */
public class CounterFileInfo {

    int interval;
    long beginTime;
    long endTime;
    int rowSize;
    int rowCount;
    int maxCount;
    int mode;

    final Map<String, CounterType> types = new LinkedHashMap<String, CounterType>();
    final Properties props = new Properties();

    public int getInterval() {
        return interval;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getMode() {
        return mode;
    }

    public String[] getTypeNames() {
        return types.keySet().toArray(new String[types.size()]);
    }

    /**
     * get counter type by name
     *
     * @param name name
     * @return counter type object
     */
    public CounterType getType(String name) {
        return types.get(name);
    }

    /**
     * get all property keys by string array
     *
     * @return string array
     */
    public String[] getPropertyKeys() {
        String[] items = new String[props.size()];
        int i = 0;

        for (Object key : props.keySet()) {
            items[i++] = key.toString();
        }

        return items;
    }

    /**
     * get property value
     *
     * @param name property name
     * @return string value
     */
    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public Map<String, CounterType> getTypes() {
        return Collections.unmodifiableMap(types);
    }

    public Properties getProps() {
        return (Properties) props.clone();
    }
}
