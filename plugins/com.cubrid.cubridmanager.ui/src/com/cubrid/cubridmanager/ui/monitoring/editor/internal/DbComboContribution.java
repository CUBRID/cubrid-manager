/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.monitoring.editor.internal;

import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.DbStatusDumpMonitorViewPart;
import java.util.List;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * An concrete ControlContribution implementation for adding a database combo to a tool bar.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-29 created by lizhiqiang
 */
public class DbComboContribution extends ControlContribution {
    private List<String> databaseLst;
    private int selected;
    private String selectedDb;
    private final DbStatusDumpMonitorViewPart dbStatDumpView;

    // Constuctor
    public DbComboContribution(String id, DbStatusDumpMonitorViewPart dbStatDumpView) {
        super(id);
        this.dbStatDumpView = dbStatDumpView;
    }

    /**
     * Creates and returns the control for this contribution item under the given parent composite.
     *
     * @param parent the parent composite
     * @return the control under th e given parent composite.
     */
    protected Control createControl(Composite parent) {
        final Combo dbCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        dbCombo.setToolTipText(Messages.dbSelectTip);
        if (databaseLst != null && !databaseLst.isEmpty()) {
            dbCombo.setItems(databaseLst.toArray(new String[databaseLst.size()]));
            dbCombo.select(0);
            selected = dbCombo.getSelectionIndex();
            selectedDb = dbCombo.getItem(selected);
            dbCombo.addSelectionListener(
                    new SelectionAdapter() {

                        /**
                         * Sent when selection occurs in the control.
                         *
                         * @param event an event containing information about the selection
                         */
                        public void widgetSelected(SelectionEvent event) {
                            widgetDefaultSelected(event);
                        }

                        /**
                         * Sent when default selection occurs in the control.
                         *
                         * @param event an event containing information about the default selection
                         */
                        public void widgetDefaultSelected(SelectionEvent event) {
                            int newSelected = dbCombo.getSelectionIndex();
                            if (selected == newSelected) {
                                return;
                            }
                            String newSelectedDb = dbCombo.getItem(newSelected);
                            if (CommonUITool.openConfirmBox(
                                    Messages.bind(Messages.msgChangeDb, newSelectedDb))) {
                                selected = newSelected;
                                selectedDb = newSelectedDb;
                                if (null != dbStatDumpView) {
                                    dbStatDumpView.setStartRun(0);
                                    dbStatDumpView.getChartPart().updateChart();
                                    dbStatDumpView.updateHistoryPath(selectedDb);
                                }
                            } else {
                                dbCombo.select(selected);
                                return;
                            }
                        }
                    });
        }

        return dbCombo;
    }

    /**
     * Get the database list
     *
     * @return the databaseLst
     */
    public List<String> getDatabaseLst() {
        return databaseLst;
    }

    /** @param databaseLst the databaseLst to set */
    public void setDatabaseLst(List<String> databaseLst) {
        this.databaseLst = databaseLst;
    }

    /**
     * Get the selected database.
     *
     * @return the selectedDb
     */
    public String getSelectedDb() {
        return selectedDb;
    }

    /** @param selectedDb the selectedDb to set */
    public void setSelectedDb(String selectedDb) {
        this.selectedDb = selectedDb;
    }
}
