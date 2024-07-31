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
package com.cubrid.common.ui.cubrid.database.erwin;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * WrappedDatabaseInfo Description
 *
 * @author Jason You
 * @version 1.0 - 2012-11-23 created by Jason You
 */
public class WrappedDatabaseInfo extends DatabaseInfo {
    private final Map<String, TableSchema> tableSchemas = new HashMap<String, TableSchema>();
    private final Map<String, SchemaInfo> schemaInfos = new HashMap<String, SchemaInfo>();

    public WrappedDatabaseInfo(String dbName, ServerInfo serverInfo) {
        super(dbName, serverInfo);
    }

    public WrappedDatabaseInfo(DatabaseInfo info) {
        super(info.getDbName(), info.getServerInfo());
    }

    public Map<String, TableSchema> getTableSchemas() {
        return tableSchemas;
    }

    public void addTableSchema(String tableName, TableSchema tableSchema) {
        this.tableSchemas.put(tableName, tableSchema);
    }

    public void addTableSchemas(Map<String, TableSchema> tableSchemas) {
        this.tableSchemas.putAll(tableSchemas);
    }

    public Map<String, SchemaInfo> getSchemaInfos() {
        return schemaInfos;
    }

    public void addSchemaInfo(String tableName, SchemaInfo schemaInfo) {
        this.schemaInfos.put(tableName, schemaInfo);
    }

    public void addSchemaInfos(Map<String, SchemaInfo> schemaInfos) {
        this.schemaInfos.putAll(schemaInfos);
    }

    public SchemaInfo getSchemaInfo(String tableName) {
        return schemaInfos.get(tableName);
    }
}
