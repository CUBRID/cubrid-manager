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

import com.cubrid.common.ui.compare.data.model.DataCompare;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

public class DataCompareCellModifier implements ICellModifier {
    private DataCompareEditorPart dataCompareEditorPart;

    public DataCompareCellModifier(DataCompareEditorPart dataCompareEditorPart) {
        super();
        this.dataCompareEditorPart = dataCompareEditorPart;
    }

    public boolean canModify(Object element, String property) {
        if (!dataCompareEditorPart.isOffline()) {
            return false;
        }

        int index = dataCompareEditorPart.getColumnNames().indexOf(property);
        return index == 0;
    }

    public Object getValue(Object element, String property) {
        Object result = null;
        DataCompare dataCompare = (DataCompare) element;
        int index = dataCompareEditorPart.getColumnNames().indexOf(property);
        if (index == 0) {
            result = new Boolean(dataCompare.isUse());
        } else {
            result = "";
        }
        return result;
    }

    public void modify(Object element, String property, Object value) {
        if (!dataCompareEditorPart.isOffline()) {
            return;
        }

        boolean use = ((Boolean) value).booleanValue();

        final TableItem item = (TableItem) element;
        DataCompare dataCompare = (DataCompare) item.getData();
        int index = dataCompareEditorPart.getColumnNames().indexOf(property);
        if (index == 0) {
            dataCompare.setUse(use);
        }

        dataCompareEditorPart.reloadInput();
    }
}
