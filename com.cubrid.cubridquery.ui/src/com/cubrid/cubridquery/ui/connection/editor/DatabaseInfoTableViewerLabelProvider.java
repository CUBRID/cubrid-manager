/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridquery.ui.connection.editor;

import static com.cubrid.common.core.util.NoOp.noOp;
import static org.apache.commons.lang.StringUtils.defaultString;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.cubridquery.ui.common.Messages;
import com.cubrid.cubridquery.ui.spi.model.DatabaseUIWrapper;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class DatabaseInfoTableViewerLabelProvider
        implements ITableLabelProvider, ITableColorProvider {
    @SuppressWarnings("unused")
    private static final Image CHECK_IMAGE = CommonUIPlugin.getImage("icons/checked.gif");

    @SuppressWarnings("unused")
    private static final Image UNCHECK_IMAGE = CommonUIPlugin.getImage("icons/unchecked.gif");

    public void addListener(ILabelProviderListener listener) {
        noOp();
    }

    public void dispose() {
        noOp();
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
        noOp();
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof DatabaseUIWrapper)) {
            return "";
        }

        DatabaseUIWrapper ui = (DatabaseUIWrapper) element;
        switch (columnIndex) {
            case 0:
                return defaultString(ui.getName(), "");
            case 1:
                return defaultString(ui.getAddress(), "");
            case 2:
                return defaultString(ui.getPort(), "");
            case 3:
                if ("null".equals(ui.getJdbcDriverVersion())) {
                    return "";
                } else {
                    return defaultString(ui.getJdbcDriverVersion(), "");
                }
            case 4:
                return defaultString(ui.getUser(), "");
            case 5:
                return ui.isAutoSavePassword()
                        ? Messages.passwordAutoSaveYes
                        : Messages.passwordAutoSaveNo;
            default:
                return "";
        }
    }

    public Color getForeground(Object element, int columnIndex) {
        return null;
    }

    public Color getBackground(Object element, int columnIndex) {
        return null;
    }
}
