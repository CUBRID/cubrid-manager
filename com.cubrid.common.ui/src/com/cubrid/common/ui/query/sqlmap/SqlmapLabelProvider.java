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
package com.cubrid.common.ui.query.sqlmap;

import com.cubrid.common.ui.CommonUIPlugin;
import java.util.Map;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * SQLMap label provider.
 *
 * @author CHOE JUNGYEON
 */
public class SqlmapLabelProvider implements ITableLabelProvider {

    private SqlmapNavigatorView parentView;
    private static final Image CHECK_IMAGE = CommonUIPlugin.getImage("icons/checked.gif");
    private static final Image UNCHECK_IMAGE = CommonUIPlugin.getImage("icons/unchecked.gif");

    public SqlmapLabelProvider(SqlmapNavigatorView sqlmapNavigatorView) {
        this.parentView = sqlmapNavigatorView;
    }

    public void addListener(ILabelProviderListener listener) {}

    public void dispose() {}

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {}

    @SuppressWarnings("unchecked")
    public Image getColumnImage(Object element, int columnIndex) {
        if (!(element instanceof Map)) {
            return null;
        }

        Map<String, String> data = (Map<String, String>) element;
        switch (columnIndex) {
            case 0:
                return parentView.isUseCondition(data.get("1")) ? CHECK_IMAGE : UNCHECK_IMAGE;
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof Map)) {
            return "";
        }
        Map<String, Object> data = (Map<String, Object>) element;
        Object obj = data.get(String.valueOf(columnIndex));
        return obj == null ? "" : obj.toString();
    }
}
