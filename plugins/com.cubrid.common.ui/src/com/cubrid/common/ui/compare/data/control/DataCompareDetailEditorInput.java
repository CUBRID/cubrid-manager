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
package com.cubrid.common.ui.compare.data.control;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class DataCompareDetailEditorInput implements IEditorInput {
    private DatabaseInfo sourceDB;
    private DatabaseInfo targetDB;
    private String diffFilePath;
    private SchemaInfo sourceSchemaInfo;

    public DataCompareDetailEditorInput(
            DatabaseInfo sourceNode,
            DatabaseInfo targetNode,
            String diffFilePath,
            SchemaInfo sourceSchemaInfo) {
        this.sourceDB = sourceNode;
        this.targetDB = targetNode;
        this.diffFilePath = diffFilePath;
        this.sourceSchemaInfo = sourceSchemaInfo;
    }

    public String getDiffFilePath() {
        return diffFilePath;
    }

    /** @return the schemaInfo */
    public SchemaInfo getSourceSchemaInfo() {
        return sourceSchemaInfo;
    }

    public DatabaseInfo getSourceDB() {
        return sourceDB;
    }

    public DatabaseInfo getTargetDB() {
        return targetDB;
    }

    @SuppressWarnings("rawtypes")
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
        return Messages.titleCompareDataDetail;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return getName();
    }
}
