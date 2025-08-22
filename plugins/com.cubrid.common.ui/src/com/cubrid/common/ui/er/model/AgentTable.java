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
package com.cubrid.common.ui.er.model;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.spi.model.CubridDatabase;

/**
 * An agent table for ertable and database Schema Information table Additional, the SchemaInfo may
 * have foreign relation ship from navigator tree, but ERTable do not have the relation ship because
 * the ERD do not have the relate table.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-8-9 created by Yu Guojia
 */
public class AgentTable {
    private ERTable erTable;
    private SchemaInfo schemaInfo;

    public AgentTable() {}

    public AgentTable(ERTable erTable, SchemaInfo schemaInfo) {
        this.erTable = erTable;
        this.schemaInfo = schemaInfo;
    }

    public CubridDatabase getCubridDatabase() {
        return erTable.getERSchema().getCubridDatabase();
    }

    public void addColumn(DBAttribute attr, boolean isPK) {
        if (erTable.getColumn(attr.getName()) != null) {
            return;
        }

        ERTableColumn erCol = new ERTableColumn(erTable, attr, isPK);
        schemaInfo.addAttribute(attr);
        erTable.addColumnAndFire(erCol);
    }

    public ERTable getERTable() {
        return erTable;
    }

    public void setERTable(ERTable erTable) {
        this.erTable = erTable;
    }
}
