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
package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.model.ICubridNode;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/**
 * Import Object Label Provider
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-12-13 created by Kevin.Wang
 */
public class ImportObjectLabelProvider extends CellLabelProvider {
    public static final String FILE_PAHT = "filePath";
    public static final String ROW_COUNT = "rowCount";
    public static final String IS_MAPPED = "isMapped";
    public static final String IS_NEW = "isNew";
    public static final String CREATE_DDL = "createDDL";
    public static final String DATE_TYPE = "dataType";
    public static final String IS_USE_FRIST_LINE_AS_COLUMN_NAME = "isUseFirstLineASColumnName";

    /**
     * update
     *
     * @param cell ViewerCell
     */
    public void update(ViewerCell cell) {
        ICubridNode node = (ICubridNode) cell.getElement();
        if (cell.getColumnIndex() == 0) {
            cell.setText(node.getName());
            cell.setImage(CommonUIPlugin.getImage(node.getIconPath()));
        } else if (cell.getColumnIndex() == 1) {
            String path = (String) node.getData(FILE_PAHT);
            cell.setText(path == null ? "" : path);
        } else if (cell.getColumnIndex() == 2) {
            String rowCount = null;
            if (node.getData(ROW_COUNT) != null) {
                rowCount = node.getData(ROW_COUNT).toString();
            }
            cell.setText(rowCount == null ? "" : rowCount);
        } else if (cell.getColumnIndex() == 3) {
            String str = "";
            Object value = node.getData(IS_MAPPED);
            if (value != null) {
                boolean isMapped = Boolean.parseBoolean(value.toString());
                if (isMapped) {
                    str = "V";
                } else {
                    str = "X";
                }
            }
            cell.setText(str);
        } else if (cell.getColumnIndex() == 4) {
            String str = "";
            Object value = node.getData(IS_USE_FRIST_LINE_AS_COLUMN_NAME);
            if (value != null) {
                boolean isUse = Boolean.parseBoolean(value.toString());
                if (isUse) {
                    str = "V";
                } else {
                    str = "X";
                }
            }
            cell.setText(str);
        }
    }
}
