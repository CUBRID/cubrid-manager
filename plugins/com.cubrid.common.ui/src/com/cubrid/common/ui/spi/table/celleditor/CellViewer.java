/* Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

package com.cubrid.common.ui.spi.table.celleditor;

import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.io.File;
import org.eclipse.swt.widgets.Shell;

public class CellViewer {
    private ICellPopupEditor dialog;
    private ColumnInfo columnInfo;
    private boolean isEditable = true;
    private String defaultCharset;

    public CellViewer(ColumnInfo columnInfo, boolean isEditable, String defaultCharset) {
        this.columnInfo = columnInfo;
        this.isEditable = isEditable;
        this.defaultCharset = defaultCharset;
    }

    /**
     * Open a dialog to view the data
     *
     * @param parent Shell
     * @param value CellValue
     * @return int
     */
    public int openCellViewer(Shell parent, CellValue value) {
        createDialog(parent, value);

        if (null == dialog) {
            return 0;
        }
        return dialog.show();
    }

    /**
     * Initial the dialog
     *
     * @param parent Shell
     */
    private void createDialog(Shell parent, CellValue value) {
        if (null == columnInfo) {
            return;
        }

        if (DataType.isTimeDataType(columnInfo.getType())) {
            dialog = new TimeCellPopuoDialog(parent, columnInfo, value, isEditable);
        }

        if (DataType.isDateDataType(columnInfo.getType())) {
            dialog = new DateCellPopupDialog(parent, columnInfo, value, isEditable);
        }

        if (DataType.isDateTimeDataType(columnInfo.getType())
                || DataType.isTimeStampDataType(columnInfo.getType())) {
            dialog = new DateTimeCellPopuoDialog(parent, columnInfo, value, isEditable);
        }

        if (DataType.isClobDataType(columnInfo.getType())
                || DataType.isStringDataType(columnInfo.getType())) {
            dialog =
                    new LongTextCellPopupDialog(
                            parent, columnInfo, defaultCharset, value, isEditable);
        } else if (DataType.isBlobDataType(columnInfo.getType())
                || DataType.isBitDataType(columnInfo.getType())
                || DataType.isBitVaryingDataType(columnInfo.getType())) {
            dialog = new BLOBCellPopupDialog(parent, columnInfo, defaultCharset, value, isEditable);
        }
        return;
    }

    public static boolean isNeedShowDetail(ColumnInfo columnInfo) {
        if (DataType.isTimeDataType(columnInfo.getType())
                || DataType.isDateDataType(columnInfo.getType())
                || DataType.isDateTimeDataType(columnInfo.getType())
                || DataType.isTimeStampDataType(columnInfo.getType())
                || DataType.isClobDataType(columnInfo.getType())
                || DataType.isStringDataType(columnInfo.getType())
                || DataType.isBlobDataType(columnInfo.getType())
                || DataType.isBitDataType(columnInfo.getType())
                || DataType.isBitVaryingDataType(columnInfo.getType())) {
            return true;
        }
        return false;
    }

    public CellValue getValue() {
        return dialog.getValue();
    }

    /**
     * Check whether these two objects are equal
     *
     * @param oldObj Object
     * @param newObj Object
     * @return boolean
     */
    public static boolean isCellValueEqual(Object oldObj, Object newObj) {
        // Judge the cell value is null
        if (oldObj == null || newObj == null) {
            if (oldObj == null && newObj == null) {
                return true;
            }
            return false;
        }
        if (oldObj instanceof CellValue) {
            oldObj = ((CellValue) oldObj).getValue();
        }
        if (newObj instanceof CellValue) {
            newObj = ((CellValue) newObj).getValue();
        }
        // Judge the data is null
        if (oldObj == null || newObj == null) {
            if (oldObj == null && newObj == null) {
                return true;
            }
            return false;
        }
        if (!oldObj.getClass().equals(newObj.getClass())) {
            return false;
        }

        if (oldObj instanceof File && newObj instanceof File) {
            File oldFile = (File) oldObj;
            File newFile = (File) newObj;
            return oldFile.getAbsolutePath().equals(newFile.getAbsolutePath())
                    && oldFile.lastModified() == newFile.lastModified();
        } else if (oldObj instanceof byte[] && newObj instanceof byte[]) {
            byte[] oldBytes = (byte[]) oldObj;
            byte[] newBytes = (byte[]) newObj;
            if (oldBytes.length != newBytes.length) {
                return false;
            } else {
                for (int i = 0; i < oldBytes.length; i++) {
                    if (oldBytes[i] != newBytes[i]) {
                        return false;
                    }
                }
                return true;
            }
        }

        return oldObj.toString().equals(newObj.toString());
    }
}
