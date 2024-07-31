/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.spi.table.celleditor;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.dialog.CMTrayDialog;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

/**
 * The DateCellPopupDialog Description : DateCellPopupDialog Author : Kevin.Wang Create date :
 * 2014-2-21
 */
public class DateCellPopupDialog extends CMTrayDialog implements ICellPopupEditor {

    private DateTime dateComposite;

    protected CellValue value;
    private CellValue newValue;

    protected Button setNullBtn;
    protected boolean isEditable = true;
    protected String defaultCharset;

    /** @param parentShell */
    public DateCellPopupDialog(
            Shell parentShell, ColumnInfo columnInfo, CellValue value, boolean isEditable) {
        super(parentShell);
        this.value = value;
        this.isEditable = isEditable;
    }

    /**
     * Create dialog area
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);

        dateComposite = new DateTime(composite, SWT.CALENDAR | SWT.BORDER);
        dateComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));

        setNullBtn = new Button(composite, SWT.CHECK);
        setNullBtn.setText(Messages.btnSetNull);
        setNullBtn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        setNullBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        changeBtnStatus();
                    }
                });

        initValue();
        return composite;
    }

    protected void updateButtonStatus() {
        if (!isEditable) {
            dateComposite.setEnabled(false);
            setNullBtn.setEnabled(false);
        }
    }

    /** Change the button status */
    private void changeBtnStatus() {
        if (setNullBtn.getSelection()) {
            dateComposite.setEnabled(false);
        } else {
            dateComposite.setEnabled(true);
        }
    }

    /** Init the value */
    private void initValue() {
        if (value == null || value.getValue() == null || NULL_VALUE.equals(value.getValue())) {
            setNullBtn.setSelection(true);
            changeBtnStatus();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTimeMillSecond());

            dateComposite.setYear(calendar.get(Calendar.YEAR));
            dateComposite.setMonth(calendar.get(Calendar.MONTH));
            dateComposite.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * Get the time mill seconds
     *
     * @return long
     */
    private long getTimeMillSecond() {
        if (value.getValue() instanceof String) {
            String str = (String) value.getValue();
            if (str.trim().length() == 0) {
                return System.currentTimeMillis();
            }
            try {
                return DateUtil.getDatetime(str);
            } catch (ParseException e) {
                return System.currentTimeMillis();
            }
        } else if (value.getValue() instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) value.getValue();
            return date.getTime();
        } else if (value.getValue() instanceof java.sql.Date) {
            java.sql.Date date = (java.sql.Date) value.getValue();
            return date.getTime();
        }

        return System.currentTimeMillis();
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
        updateButtonStatus();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            String stringValue = NULL_VALUE;
            Date dateValue = null;
            Calendar calendar = null;

            if (!setNullBtn.getSelection()) {
                if (dateComposite != null) {
                    int year = dateComposite.getYear();
                    int month = dateComposite.getMonth();
                    int day = dateComposite.getDay();

                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    dateValue = new Date(calendar.getTimeInMillis());
                    stringValue = DateUtil.getDatetimeString(dateValue, DateUtil.DATE_FORMAT);
                }
            }

            newValue = new CellValue(dateValue);
            newValue.setShowValue(stringValue);
        }

        super.buttonPressed(buttonId);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        getShell().pack();
        super.constrainShellSize();
        if (isEditable) {
            getShell().setText(Messages.titleEditData);
        } else {
            getShell().setText(Messages.titleViewData);
        }
    }

    protected int getShellStyle() {
        return SWT.RESIZE | SWT.CLOSE;
    }

    public CellValue getValue() {
        return newValue;
    }

    public int show() {
        return this.open();
    }
}
