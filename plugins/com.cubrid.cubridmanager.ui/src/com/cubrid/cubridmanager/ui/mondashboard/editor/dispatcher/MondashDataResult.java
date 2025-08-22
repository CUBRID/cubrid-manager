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
package com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher;

import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import java.util.HashMap;
import java.util.Map;

/**
 * The type is responsible for providing the result values for monitor dashboard
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-28 created by lizhiqiang
 */
public class MondashDataResult {

    private String name;
    private final Map<IDiagPara, String> updateMap;

    public MondashDataResult() {
        name = "";
        updateMap = new HashMap<IDiagPara, String>();
    }

    public MondashDataResult(String name) {
        this.name = name;
        updateMap = new HashMap<IDiagPara, String>();
    }

    /**
     * Get the name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the newest update data
     *
     * @return Map<IDiagPara, String>
     */
    public Map<IDiagPara, String> getUpdateMap() {
        return updateMap;
    }

    /**
     * Put the inputMap into updateMap
     *
     * @param inputMap an instance of Map<IDiagPara, String>
     */
    public void putUpdateMap(Map<IDiagPara, String> inputMap) {
        if (inputMap != null && !inputMap.isEmpty()) {
            updateMap.putAll(inputMap);
        }
    }

    /**
     * @return int
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((updateMap == null) ? 0 : updateMap.hashCode());
        return result;
    }

    /**
     * @param obj an instance of Object
     * @return boolean
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MondashDataResult other = (MondashDataResult) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (updateMap == null) {
            if (other.updateMap != null) {
                return false;
            }
        } else if (!updateMap.equals(other.updateMap)) {
            return false;
        }
        return true;
    }
}
