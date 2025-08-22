/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.result;

import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.spi.dialog.CMTrayDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Select the column for filter chooser dialog
 *
 * @author pangqiren
 * @version 1.0 - 2013-3-20 created by pangqiren
 */
public class FilterChooserDialog extends CMTrayDialog {
    private CheckboxTableViewer tv;
    private List<ColumnInfo> colInfoList;
    private List<ColumnInfo> selectedColInfoList = new ArrayList<ColumnInfo>();
    // private Table colTable;
    private Button selectAllBtn;

    public FilterChooserDialog(
            Shell parentShell, List<ColumnInfo> colInfoList, List<ColumnInfo> selectedColInfoList) {
        super(parentShell);
        this.colInfoList = colInfoList;
        this.selectedColInfoList = selectedColInfoList;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);
        createComposite(composite);

        return parentComp;
    }

    /**
     * Create the composite
     *
     * @param parent Composite
     */
    private void createComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);

        Label infoLabel = new Label(composite, SWT.NONE);
        infoLabel.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        infoLabel.setText(Messages.lblFilterChooser);

        String[] columnNames = new String[] {Messages.colColumn};
        tv =
                (CheckboxTableViewer)
                        CommonUITool.createCheckBoxTableViewer(
                                composite,
                                null,
                                columnNames,
                                CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, 300, 200));

        tv.addCheckStateListener(
                new ICheckStateListener() {
                    public void checkStateChanged(CheckStateChangedEvent event) {
                        if (!event.getChecked() && selectAllBtn.getSelection()) {
                            selectAllBtn.setSelection(false);
                        }

                        if (colInfoList != null
                                && colInfoList.size() == tv.getCheckedElements().length) {
                            selectAllBtn.setSelection(true);
                        }
                    }
                });

        final List<Map<String, Object>> colNameList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < colInfoList.size(); i++) {
            ColumnInfo colInfo = colInfoList.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("0", colInfo.getName());
            map.put("1", colInfo);
            colNameList.add(map);
        }
        tv.setInput(colNameList);
        tv.getTable().setFocus();

        selectAllBtn = new Button(composite, SWT.CHECK);
        {
            selectAllBtn.setText(Messages.btnSelectAll);
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalIndent = 0;
            gridData.horizontalSpan = 3;
            selectAllBtn.setLayoutData(gridData);
        }
        selectAllBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        boolean selection = selectAllBtn.getSelection();
                        tv.setAllChecked(selection);
                    }
                });

        for (TableItem item : tv.getTable().getItems()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) item.getData();
            if (map != null && selectedColInfoList.contains(map.get("1"))) {
                item.setChecked(true);
            }
        }

        TableColumn[] tblCols = tv.getTable().getColumns();
        for (TableColumn tblCol : tblCols) {
            tblCol.setWidth(280);
        }

        if (colInfoList != null
                && selectedColInfoList != null
                && colInfoList.size() == selectedColInfoList.size()) {
            selectAllBtn.setSelection(true);
        }
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent, IDialogConstants.OK_ID, com.cubrid.common.ui.common.Messages.btnOK, true);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.common.ui.common.Messages.btnCancel,
                true);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.titleFilterChooser);
    }

    /**
     * Call this method when the button in button bar is pressed
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            selectedColInfoList.clear();
            Object[] objs = tv.getCheckedElements();
            if (objs != null && objs.length > 0) {
                for (Object obj : objs) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) obj;
                    ColumnInfo colInfo = (ColumnInfo) map.get("1");
                    selectedColInfoList.add(colInfo);
                }
            }
        }

        super.buttonPressed(buttonId);
    }

    public List<ColumnInfo> getSelectedColInfoList() {
        return selectedColInfoList;
    }
}
