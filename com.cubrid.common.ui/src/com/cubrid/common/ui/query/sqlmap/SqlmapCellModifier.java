/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.sqlmap;

import com.cubrid.common.core.util.LogUtil;
import java.util.Map;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;

/**
 * The editor of SQLMap conditions list.
 *
 * @author CHOE JUNGYEON
 */
public class SqlmapCellModifier implements ICellModifier {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtil.getLogger(SqlmapCellModifier.class);

    private SqlmapNavigatorView parentView;

    public SqlmapCellModifier(SqlmapNavigatorView sqlmapNavigatorView) {
        this.parentView = sqlmapNavigatorView;
    }

    public boolean canModify(Object element, String property) {
        if (!(element instanceof Map)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getValue(Object element, String property) {
        if (!(element instanceof Map)) {
            return false;
        }

        Map<String, String> data = (Map<String, String>) element;
        return parentView.isUseCondition(data.get("1"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void modify(Object element, String property, Object value) {
        if (!(element instanceof TableItem)) {
            return;
        }

        TableItem tableItem = (TableItem) element;
        Map<String, String> data = (Map<String, String>) tableItem.getData();

        parentView.changeUseCondition(data.get("1"), (Boolean) value);
        parentView.refreshView();
    }
}
