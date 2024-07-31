/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er;

import com.cubrid.common.core.common.model.SchemaInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for Cubrid data base tables of SchemaInfo
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-2 created by Yu Guojia
 */
public class CubridSchemaInfoManager {
    private Map<String, SchemaInfo> schemaInfoMap;

    public CubridSchemaInfoManager() {
        schemaInfoMap = new HashMap<String, SchemaInfo>();
    }

    public Map<String, SchemaInfo> getAllSchemaInfo() {
        return schemaInfoMap;
    }

    public SchemaInfo getSchemaInfo(String tableName) {
        return schemaInfoMap.get(tableName);
    }

    public void addSchemaInfo(SchemaInfo schemaInfo) {
        if (schemaInfo == null) {
            return;
        }
        if (schemaInfoMap.get(schemaInfo.getUniqueName()) != null) {
            return;
        }
        schemaInfoMap.put(schemaInfo.getUniqueName(), schemaInfo);
    }

    public void removeSchemaInfo(String name) {
        if (name == null) {
            return;
        }
        schemaInfoMap.remove(name);
    }

    public void removeSchemaInfo(SchemaInfo schemaInfo) {
        if (schemaInfo == null) {
            return;
        }
        if (schemaInfoMap.get(schemaInfo.getUniqueName()) == null) {
            return;
        }
        schemaInfoMap.remove(schemaInfo.getUniqueName());
    }

    public void cleanAllSchemaInfos() {
        schemaInfoMap.clear();
    }
}
