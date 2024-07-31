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
package com.cubrid.common.ui.er.control;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import java.util.Map;

/**
 * The task for exporting DDL to file from ERD
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-1-26 created by Yu Guojia
 */
public class ExportSQLDataTask extends AbstractTask {
    private final ERSchema erSchema;
    private final String fileFullName;
    private final String fileCharset;
    private final boolean isContainIndex;
    private boolean isCancel = false;
    private boolean isSuccess = false;

    public ExportSQLDataTask(
            ERSchema erSchema, String filename, String fileCharset, boolean isContainIndex) {
        this.erSchema = erSchema;
        this.fileFullName = filename;
        this.fileCharset = fileCharset;
        this.isContainIndex = isContainIndex;
    }

    public void cancel() {
        isCancel = true;
    }

    public void finish() {
        isSuccess = true;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void execute() {
        DatabaseInfo databaseInfo = erSchema.getCubridDatabase().getDatabaseInfo();
        SchemaDDL schemaDDL =
                new SchemaDDL(new SchemaChangeManager(databaseInfo, true), databaseInfo);
        Map<String, SchemaInfo> tables = erSchema.getAllSchemaInfo();

        StringBuilder text = new StringBuilder("");
        for (SchemaInfo table : tables.values()) {
            String sql = schemaDDL.getSchemaDDL(table, isContainIndex);
            text.append(sql);
            text.append("\n");
        }

        isSuccess = FileUtil.writeToFile(fileFullName, text.toString(), fileCharset, false);
    }
}
