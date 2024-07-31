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

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.data.model.DataCompare;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class DataCompareSchemaListLabelProvider extends LabelProvider
        implements ITableLabelProvider, ITableColorProvider {
    private static final Color NOT_EXISTS_COLOR = new Color(Display.getCurrent(), 128, 128, 128);
    private static final Color NORMAL_COLOR = new Color(Display.getCurrent(), 0, 0, 0);
    private static final Color ERROR_COLOR = new Color(Display.getCurrent(), 255, 0, 0);
    private static final int LAST_COLUMN_INDEX = 8;

    public Image getColumnImage(Object element, int columnIndex) {
        if (!(element instanceof DataCompare)) {
            return null;
        }

        DataCompare comp = (DataCompare) element;
        if (columnIndex != 0) {
            return null;
        }

        if (comp.isUse()) {
            return CommonUIPlugin.getImage("icons/checked.gif");
        }

        return CommonUIPlugin.getImage("icons/unchecked.gif");
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof DataCompare) {
            DataCompare dataCompare = (DataCompare) element;
            switch (columnIndex) {
                case 1:
                    return dataCompare.getTableName();

                case 2:
                    return String.valueOf(dataCompare.getRecordsSource());

                case 3:
                    if (dataCompare.getRecordsTarget() < 0) {
                        return "-";
                    }
                    return String.valueOf(dataCompare.getRecordsTarget());

                case 4:
                    return String.valueOf(dataCompare.getProgressPosition());

                case 5:
                    return String.valueOf(dataCompare.getMatches());

                case 6:
                    return String.valueOf(dataCompare.getNotMatches());

                case 7:
                    return String.valueOf(dataCompare.getNotExists());

                case 8:
                    {
                        if (!dataCompare.isSameSchema()) {
                            return Messages.lblSchemaDifferent;
                        }
                        if (!dataCompare.isRefreshed()) {
                            return "";
                        } else if (dataCompare.getRecordsTarget() < 0
                                && dataCompare.getRecordsSource() == 0) {
                            return Messages.lblError1;
                        } else if (dataCompare.getRecordsTarget() < 0) {
                            return Messages.lblError2;
                        } else if (dataCompare.getRecordsSource() == 0) {
                            return Messages.lblError3;
                        } else {
                            return "";
                        }
                    }

                default:
                    break;
            }
        }

        return "";
    }

    public Color getForeground(Object element, int columnIndex) {
        if (element instanceof DataCompare) {
            DataCompare dataCompare = (DataCompare) element;
            if (columnIndex == LAST_COLUMN_INDEX) {
                return ERROR_COLOR;
            }
            if (columnIndex < LAST_COLUMN_INDEX
                            && dataCompare.isRefreshed()
                            && (dataCompare.getRecordsTarget() == -1
                                    || dataCompare.getRecordsSource() == 0)
                    || !dataCompare.isSameSchema()) {
                return NOT_EXISTS_COLOR;
            }
        }
        return NORMAL_COLOR;
    }

    public Color getBackground(Object element, int columnIndex) {
        return null;
    }
}
