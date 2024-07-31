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
package com.cubrid.common.ui.query.action;

import com.cubrid.common.ui.query.control.DateTimeComponent;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.spi.action.FocusAction;
import com.cubrid.common.ui.spi.table.CellValue;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * This action is responsible to show input method dialog on query editor
 *
 * @author Isaiah Choe 2012-5-20
 */
public class InputMethodAction extends FocusAction {

    public static final String ID = InputMethodAction.class.getName();
    private String type = null;
    private TableItem item = null;
    private int column = 0;
    private QueryExecuter queryExecuter;

    /**
     * The constructor
     *
     * @param shell
     * @param focusProvider
     * @param text
     * @param icon
     */
    public InputMethodAction(
            Shell shell, Control focusProvider, String text, ImageDescriptor icon) {
        super(shell, focusProvider, text, icon);
        this.setId(ID);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public InputMethodAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTableItem(TableItem item) {
        this.item = item;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setQueryExecuter(QueryExecuter queryExecuter) {
        this.queryExecuter = queryExecuter;
    }

    /**
     * Notifies that the focus gained event
     *
     * @param event an event containing information about the focus change
     */
    public void focusGained(FocusEvent event) {
        setEnabled(true);
        //		if (event.getSource() instanceof StyledText) {
        //			StyledText stext = (StyledText) event.getSource();
        //			boolean isEnabled = stext != null
        //					&& stext.getSelectionText() != null
        //					&& stext.getSelectionText().trim().length() > 0;
        //			setEnabled(isEnabled);
        //		} else if (event.getSource() instanceof Table) {
        //			Table table = (Table) event.getSource();
        //			boolean isEnabled = table.getSelection().length > 0;
        //			setEnabled(isEnabled);
        //		}
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {
        int dateTimeComponentWidth = 300;
        int dateTimeComponentHeight = 230;

        Shell shell =
                new Shell(
                        Display.getDefault().getActiveShell(),
                        SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("");
        shell.setLayout(new GridLayout());
        shell.setLayoutData(new GridData(GridData.FILL_BOTH));

        DateTimeComponent dateTimeComponent = new DateTimeComponent(shell, SWT.BORDER);
        dateTimeComponent.setLayout(new GridLayout());
        dateTimeComponent.setLayoutData(new GridData(GridData.FILL_BOTH));
        Point dateTimeComponentSize = dateTimeComponent.componentSize();
        dateTimeComponentWidth = dateTimeComponentSize.x;
        dateTimeComponentHeight = dateTimeComponentSize.y;
        shell.setSize(dateTimeComponentWidth, dateTimeComponentHeight);

        Point p = Display.getDefault().getCursorLocation();
        Rectangle screenSize = Display.getDefault().getClientArea();
        if (p.x + dateTimeComponentWidth > screenSize.width) {
            p.x = screenSize.width - dateTimeComponentWidth - 50;
        }
        if (p.y + dateTimeComponentHeight > screenSize.height) {
            p.y = screenSize.height - dateTimeComponentHeight - 50;
        }

        shell.setLocation(p);
        shell.open();
        while (!shell.isDisposed()) {
            if (!Display.getDefault().readAndDispatch()) Display.getDefault().sleep();
        }

        if (item != null && type != null && dateTimeComponent.getReturnDateValue() != null) {
            Map<String, CellValue> oldValueMap = new HashMap<String, CellValue>();
            for (int i = 1; i < queryExecuter.getTblResult().getColumnCount(); i++) {
                oldValueMap.put(String.valueOf(i), new CellValue(item.getText(i), item.getText(i)));
            }
            if (type.equalsIgnoreCase("DATE")) {
                item.setText(column, dateTimeComponent.getReturnDateValue());
            } else if (type.equalsIgnoreCase("TIMESTAMP")) {
                item.setText(column, dateTimeComponent.getReturnTimestampValue());
            } else if (type.equalsIgnoreCase("TIME")) {
                item.setText(column, dateTimeComponent.getReturnTimeValue());
            } else {
                item.setText(column, dateTimeComponent.getReturnDateTimeValue());
            }

            Map<String, CellValue> newValueMap = new HashMap<String, CellValue>();
            for (int i = 1; i < queryExecuter.getTblResult().getColumnCount(); i++) {
                newValueMap.put(String.valueOf(i), new CellValue(item.getText(i), item.getText(i)));
            }

            queryExecuter.updateValue(item, oldValueMap, newValueMap);
        }
        //			String data = stext.getSelectionText();
        //			if (data != null && !data.equals("")) {
        //				IAction pasteAction = ActionManager.getInstance().getAction(
        //						PasteAction.ID);
        //				FocusAction.changeActionStatus(pasteAction, stext);
        //			}
    }
}
