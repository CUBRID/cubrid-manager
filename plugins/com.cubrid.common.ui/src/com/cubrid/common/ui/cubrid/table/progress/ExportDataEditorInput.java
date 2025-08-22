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
package com.cubrid.common.ui.cubrid.table.progress;

import com.cubrid.common.ui.spi.model.CubridDatabase;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * ExportDataEditorInput
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-27 created by Kevin.Wang
 */
public class ExportDataEditorInput implements IEditorInput {

    private ExportConfig exportConfig;
    private CubridDatabase database;

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter.equals(CubridDatabase.class)) {
            return database;
        }
        return null;
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return "Export " + database.getName() + "@" + database.getDatabaseInfo().getBrokerIP();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return this.getName();
    }

    /** @return the exportConfig */
    public ExportConfig getExportConfig() {
        return exportConfig;
    }

    /** @param exportConfig the exportConfig to set */
    public void setExportConfig(ExportConfig exportConfig) {
        this.exportConfig = exportConfig;
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }
}
