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
package com.cubrid.common.ui.compare.schema.control;

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.schema.TableSchemaCompareUtil;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Table Schema Compare Editor Input
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.12 created by Ray Yin
 */
public class TableSchemaCompareEditorInput implements IEditorInput {
    @SuppressWarnings("unused")
    private final List<TableDetailInfo> sourceTableInfoList;

    @SuppressWarnings("unused")
    private final List<TableDetailInfo> targetTableInfoList;

    @SuppressWarnings("unused")
    private final CubridDatabase sourceDB;

    @SuppressWarnings("unused")
    private final CubridDatabase targetDB;

    private final TableSchemaCompareModel compare;

    /**
     * The constructor
     *
     * @param sourceDB
     * @param targetDB
     * @param sourceTableInfoList
     * @param targetTableInfoList
     */
    public TableSchemaCompareEditorInput(
            CubridDatabase sourceDB,
            CubridDatabase targetDB,
            List<TableDetailInfo> sourceTableInfoList,
            List<TableDetailInfo> targetTableInfoList) {
        this.sourceDB = sourceDB;
        this.targetDB = targetDB;
        this.sourceTableInfoList = sourceTableInfoList;
        this.targetTableInfoList = targetTableInfoList;
        this.compare =
                TableSchemaCompareUtil.createTableSchemaCompareModel(
                        sourceDB, targetDB, sourceTableInfoList, targetTableInfoList);
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter.equals(TableSchemaCompareModel.class)) {
            return compare;
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return Messages.dbSchemaCompareTitle;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return this.getName();
    }
}
