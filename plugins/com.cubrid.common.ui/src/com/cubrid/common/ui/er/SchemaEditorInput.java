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

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Schema Editor Input
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-6 created by Yu Guojia
 */
public class SchemaEditorInput implements IEditorInput {
    private CubridDatabase database = null;
    private TreeViewer tv = null;
    private static String NAME = "ER Design";
    private String toolTip = NAME;

    public SchemaEditorInput(CubridDatabase database, TreeViewer tv) {
        this.database = database;
        this.tv = tv;
    }

    public TreeViewer getTv() {
        return tv;
    }

    public void setTv(TreeViewer tv) {
        this.tv = tv;
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return CommonUIPlugin.getImageDescriptor("icons/action/schema_edit_on.png");
    }

    public String getName() {
        return NAME;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return NAME;
    }

    public String getToolTip() {
        return NAME;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SchemaEditorInput)) {
            return false;
        }

        if (((SchemaEditorInput) obj).getName().equals(this.getName())) {
            return false;
        }

        return this.database.equals(((SchemaEditorInput) obj).getDatabase());
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter.equals(SchemaEditorInput.class)) {
            return this;
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
