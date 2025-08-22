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

import com.cubrid.common.ui.spi.model.CubridDatabase;

/**
 * A virtual Database based on cubrid database for ERD
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-1-24 created by Yu Guojia
 */
public class ERVirtualDatabase extends CubridDatabase {
    private boolean isVirtual = true;
    private static String id = "ER Design";
    private String editorId = "ER Design";
    private String viewId = "ER Design";
    private static String label = "ER Design";
    private static ERVirtualDatabase instance;

    public static ERVirtualDatabase getInstance() {
        if (instance == null) {
            instance = new ERVirtualDatabase();
            instance.setDatabaseInfo(ERVirtualDatabaseInfo.getInstance());
        }
        return instance;
    }

    private ERVirtualDatabase() {
        super(id, label);
    }

    /**
     * Get database information
     *
     * @return the database information
     */
    public ERVirtualDatabaseInfo getDatabaseInfo() {
        if (this.getAdapter(ERVirtualDatabaseInfo.class) != null) {
            return (ERVirtualDatabaseInfo) this.getAdapter(ERVirtualDatabaseInfo.class);
        }
        return null;
    }

    @Override
    public boolean isVirtual() {
        return isVirtual;
    }

    @Override
    public void setVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getEditorId() {
        return editorId;
    }

    @Override
    public void setEditorId(String editorId) {
        this.editorId = editorId;
    }

    @Override
    public String getViewId() {
        return viewId;
    }

    @Override
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }
}
