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
package com.cubrid.cubridmanager.ui.replication.control;

import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassItem;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassList;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBClasses;
import com.cubrid.cubridmanager.ui.replication.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;

/**
 * This composite provide a table selection editor which implement by table viewer
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-7 created by pangqiren
 */
public class SelectTableComp {

    private CheckboxTableViewer tableViewer = null;
    private Table selectClassesTable = null;
    private Button allTableBtn;

    /**
     * Create the group of select tables
     *
     * @param parent Composite
     */
    public void createTableGroup(Composite parent) {
        Group replTableInfoGroup = new Group(parent, SWT.NONE);
        replTableInfoGroup.setText(Messages.chsldb2grpSelectTablesPage);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        replTableInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        replTableInfoGroup.setLayout(layout);

        allTableBtn = new Button(replTableInfoGroup, SWT.CHECK);
        allTableBtn.setText(Messages.btnSelectAllTables);
        allTableBtn.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        allTableBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (allTableBtn.getSelection()) {
                            for (int i = 0; i < selectClassesTable.getItemCount(); i++) {
                                selectClassesTable.getItem(i).setChecked(true);
                            }
                        } else {
                            for (int i = 0; i < selectClassesTable.getItemCount(); i++) {
                                selectClassesTable.getItem(i).setChecked(false);
                            }
                        }
                    }
                });

        String[] columnNameArr = new String[] {Messages.tblColTableName};
        tableViewer =
                (CheckboxTableViewer)
                        CommonUITool.createCheckBoxTableViewer(
                                replTableInfoGroup,
                                new TableViewerSorter(),
                                columnNameArr,
                                CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, 150));
        selectClassesTable = tableViewer.getTable();
        tableViewer.addCheckStateListener(
                new ICheckStateListener() {
                    public void checkStateChanged(CheckStateChangedEvent event) {
                        allTableBtn.setSelection(false);
                    }
                });
    }

    /**
     * Fill the data for table viewer
     *
     * @param dbClasses DBClasses
     */
    public void fillTableViewer(DBClasses dbClasses) {
        List<Map<String, String>> classList = new ArrayList<Map<String, String>>();
        if (dbClasses != null) {
            ClassList userClassList = dbClasses.getUserClassList();
            if (userClassList != null) {
                List<ClassItem> classItemList = userClassList.getClassList();
                for (int i = 0; classItemList != null && i < classItemList.size(); i++) {
                    Map<String, String> map = new HashMap<String, String>();
                    ClassItem item = classItemList.get(i);
                    if (item != null && !item.isVirtual()) {
                        map.put("0", item.getClassname());
                        classList.add(map);
                    }
                }
            }
        }
        if (tableViewer != null && selectClassesTable != null && !selectClassesTable.isDisposed()) {
            tableViewer.setInput(classList);
            for (int i = 0; i < selectClassesTable.getColumnCount(); i++) {
                selectClassesTable.getColumn(i).pack();
            }
        }
    }

    /**
     * Get selected tables
     *
     * @return tableList
     */
    public List<String> getSelectedTableList() {
        List<String> tableList = new ArrayList<String>();
        for (int i = 0; i < selectClassesTable.getItemCount(); i++) {
            if (selectClassesTable.getItem(i).getChecked()) {
                String className = selectClassesTable.getItem(i).getText();
                tableList.add(className);
            }
        }
        return tableList;
    }

    /**
     * Set selected tables
     *
     * @param replClassList List<String>
     */
    public void setSelectedTableList(List<String> replClassList) {
        for (int i = 0; i < selectClassesTable.getItemCount(); i++) {
            selectClassesTable.getItem(i).setChecked(false);
            String className = selectClassesTable.getItem(i).getText();
            for (int j = 0; replClassList != null && j < replClassList.size(); j++) {
                String name = replClassList.get(j);
                if (className.equals(name)) {
                    selectClassesTable.getItem(i).setChecked(true);
                }
            }
        }
    }

    /**
     * Return whether select all tables
     *
     * @return boolean
     */
    public boolean isReplAllTables() {
        return allTableBtn.getSelection();
    }

    /**
     * Set whether select all tables
     *
     * @param isSelected boolean
     */
    public void setReplAllTables(boolean isSelected) {
        allTableBtn.setSelection(isSelected);
        for (int i = 0; i < selectClassesTable.getItemCount(); i++) {
            selectClassesTable.getItem(i).setChecked(isSelected);
        }
    }

    /**
     * Set this composite whether can be edited
     *
     * @param isEditable boolean
     */
    public void setEditable(boolean isEditable) {
        allTableBtn.setEnabled(isEditable);
        selectClassesTable.setEnabled(isEditable);
    }
}
