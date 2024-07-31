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
package com.cubrid.common.ui.compare.schema.model;

import com.cubrid.common.core.common.model.TableDetailInfo;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.compare.ITypedElement;
import org.eclipse.swt.graphics.Image;

/**
 * Table Schema Model
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.08 created by Ray Yin
 */
public class TableSchemaModel implements ITypedElement {
    private Map<String, TableSchema> tableSchemaMap = new HashMap<String, TableSchema>();
    private Map<String, TableDetailInfo> tableDetailInfoMap =
            new HashMap<String, TableDetailInfo>();
    private String name = "";

    /** The constructor */
    public TableSchemaModel() {}

    public void setTableSchemaMap(String tableName, String tableSchema) {
        tableSchemaMap.put(tableName, new TableSchema(tableName, tableSchema));
    }

    public Map<String, TableSchema> getTableSchemaMap() {
        return tableSchemaMap;
    }

    public void setTableDetailInfoMap(String tableName, TableDetailInfo tableDetailInfo) {
        tableDetailInfoMap.put(tableName, tableDetailInfo);
    }

    public Map<String, TableDetailInfo> getTableDetailInfoMap() {
        return tableDetailInfoMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return null;
    }

    public Image getImage() {
        return null;
    }
}
