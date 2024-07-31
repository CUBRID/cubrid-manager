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
package com.cubrid.common.ui.cubrid.table.editor;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.cubridmanager.core.cubrid.database.model.Collation;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Replace editor table from dialog mode to editor part mode
 *
 * @author fulei
 * @version 1.0 - 2012-12-26 created by fulei
 */
public class TableEditorInput implements IEditorInput {
    private final CubridDatabase database;
    private final boolean isNewTableFlag;
    private final SchemaInfo schemaInfo;
    private final ISchemaNode editedTableNode;
    private List<String> dbUserList;
    private List<Collation> collations;
    private int type;

    /**
     * @param database
     * @param isNewTableFlag
     * @param schemaInfo
     * @param table
     * @param type EditTableAction.MODE_TABLE_EDIT or EditTableAction.MODE_INDEX_EDIT
     */
    public TableEditorInput(
            CubridDatabase database,
            boolean isNewTableFlag,
            SchemaInfo schemaInfo,
            ISchemaNode table,
            int type) {
        this.database = database;
        this.editedTableNode = table;
        this.isNewTableFlag = isNewTableFlag;
        this.schemaInfo = schemaInfo;
        this.type = type;
    }

    @SuppressWarnings("all")
    public Object getAdapter(Class adapter) {
        return null;
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        if (isNewTableFlag) {
            return Messages.newTableMsgTitle;
        } else {
            return Messages.bind(Messages.editTableMsgTitle, schemaInfo.getClassname());
        }
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        if (isNewTableFlag) {
            return Messages.newTableMsgTitle;
        } else {
            return Messages.bind(Messages.editTableMsgTitle, schemaInfo.getClassname());
        }
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public boolean isNewTableFlag() {
        return isNewTableFlag;
    }

    public SchemaInfo getSchemaInfo() {
        return schemaInfo;
    }

    public ISchemaNode getEditedTableNode() {
        return editedTableNode;
    }

    public List<String> getDbUserList() {
        return dbUserList;
    }

    public void setDbUserList(List<String> dbUserList) {
        this.dbUserList = dbUserList;
    }

    public List<Collation> getCollationList() {
        return collations;
    }

    public void setCollationList(List<Collation> collations) {
        this.collations = collations;
    }

    public int getType() {
        return type;
    }
}
