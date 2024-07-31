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
package com.cubrid.common.ui.query.tuner.dialog;

import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.tuner.QueryRecord;
import java.util.List;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;

/**
 * QueryPlanLabelPrivoder Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-16 created by Kevin.Wang
 */
public class QueryPlanLabelPrivoder extends LabelProvider implements ITableLabelProvider {
    private final String LABEL_FETCHES = "data_page_fetches";
    private final String LABEL_DIRTIES = "data_page_dirties";
    private final String LABEL_IOREADS = "data_page_ioreads";
    private final String LABEL_IOWRITES = "data_page_iowrites";

    private boolean showNameFlag;
    private final TableViewer tableViewer;

    public QueryPlanLabelPrivoder(TableViewer tableViewer, boolean showNameFlag) {
        this.tableViewer = tableViewer;
        this.showNameFlag = showNameFlag;
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element != null && element instanceof QueryRecord) {
            QueryRecord queryRecord = (QueryRecord) element;
            if (queryRecord.getStatistics() != null) {
                if (showNameFlag) {
                    switch (columnIndex) {
                        case 0:
                            return getLabel(queryRecord);
                        case 1:
                            return queryRecord.getStatistics().get(LABEL_FETCHES);
                        case 2:
                            return queryRecord.getStatistics().get(LABEL_DIRTIES);
                        case 3:
                            return queryRecord.getStatistics().get(LABEL_IOREADS);
                        case 4:
                            return queryRecord.getStatistics().get(LABEL_IOWRITES);
                        case 5:
                            return getCost(queryRecord);
                    }
                } else {
                    switch (columnIndex) {
                        case 0:
                            return queryRecord.getStatistics().get(LABEL_FETCHES);
                        case 1:
                            return queryRecord.getStatistics().get(LABEL_DIRTIES);
                        case 2:
                            return queryRecord.getStatistics().get(LABEL_IOREADS);
                        case 3:
                            return queryRecord.getStatistics().get(LABEL_IOWRITES);
                        case 4:
                            return getCost(queryRecord);
                    }
                }
            }
        }

        return null;
    }

    private String getCost(QueryRecord queryRecord) {
        String costStr = "";
        if (queryRecord.getQueryPlan() != null) {
            costStr = String.valueOf(queryRecord.getQueryPlan().calCost());
        }
        return costStr;
    }

    private String getLabel(QueryRecord queryRecord) {
        Object obj = tableViewer.getInput();
        if (obj != null && obj instanceof List) {
            @SuppressWarnings("rawtypes")
            List list = (List) obj;
            if (list.indexOf(queryRecord) == 0) {
                return Messages.lblNow;
            }
            if (list.indexOf(queryRecord) == 1) {
                return Messages.lblLast;
            }
        }
        return "";
    }
}
