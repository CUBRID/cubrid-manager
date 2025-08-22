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

package com.cubrid.cubridmanager.ui.cubrid.database.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.ParamDumpInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * The dialog is used to show Param Dump result.
 *
 * @author wuyingshi
 * @version 1.0 - 2010-3-24 created by wuyingshi
 */
public class ParamDumpResultDialog extends CMTitleAreaDialog {

    private CubridDatabase database = null;
    private ParamDumpInfo result = new ParamDumpInfo();
    private Text dbNameText;
    private Table table = null;
    private static String blank = "  ";
    private boolean clientItem = false;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public ParamDumpResultDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 5;
        composite.setLayout(layout);

        // dynamicHelp end
        createdbNameGroup(composite);

        table = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        table.setHeaderVisible(true);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 7;
        gridData.verticalSpan = 10;
        gridData.heightHint = 300;
        table.setLayoutData(gridData);
        table.setLinesVisible(true);
        CommonUITool.hackForYosemite(table);
        /*
         * // fill in context menu
         */

        Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
        table.setMenu(menu);
        MenuItem copy = new MenuItem(menu, SWT.PUSH);
        copy.setText(
                Messages.bind(com.cubrid.cubridmanager.ui.logs.Messages.contextCopy, "Ctrl+C"));
        copy.setAccelerator(SWT.CTRL + 'C');
        copy.addListener(
                SWT.Selection,
                new Listener() {
                    public void handleEvent(Event event) {
                        TextTransfer textTransfer = TextTransfer.getInstance();
                        Clipboard clipboard = CommonUITool.getClipboard();
                        StringBuilder content = new StringBuilder();
                        TableItem[] items = table.getSelection();
                        for (int i = 0; i < items.length; i++) {
                            content.append(
                                    items[i].getText(0)
                                            + blank
                                            + items[i].getText(1)
                                            + blank
                                            + items[i].getText(2)
                                            + blank
                                            + System.getProperty("line.separator"));
                        }
                        String data = content.toString();
                        if (data != null && !data.equals("")) {
                            clipboard.setContents(
                                    new Object[] {data}, new Transfer[] {textTransfer});
                        }
                    }
                });
        table.addKeyListener(
                new org.eclipse.swt.events.KeyAdapter() {
                    public void keyPressed(org.eclipse.swt.events.KeyEvent event) {
                        if ((event.stateMask & SWT.CTRL) != 0
                                && (event.stateMask & SWT.SHIFT) == 0
                                && event.keyCode == 'c') {

                            TextTransfer textTransfer = TextTransfer.getInstance();
                            Clipboard clipboard = CommonUITool.getClipboard();
                            StringBuilder content = new StringBuilder();

                            TableItem[] items = table.getSelection();
                            for (int i = 0; i < items.length; i++) {
                                content.append(
                                        items[i].getText(0)
                                                + blank
                                                + items[i].getText(1)
                                                + blank
                                                + items[i].getText(2)
                                                + blank
                                                + System.getProperty("line.separator"));
                            }

                            String data = content.toString();
                            if (data != null && !data.equals("")) {
                                clipboard.setContents(
                                        new Object[] {data}, new Transfer[] {textTransfer});
                            }
                        }
                    }
                });

        setTitle(Messages.titleParamDumpDialog);
        setMessage(Messages.msgParamDumpDialog);
        initial();
        return parentComp;
    }

    /**
     * Create Database Name Group
     *
     * @param composite the parent composite
     */
    private void createdbNameGroup(Composite composite) {

        final Group dbnameGroup = new Group(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        final GridData gdDbnameGroup = new GridData(GridData.FILL_HORIZONTAL);
        dbnameGroup.setLayoutData(gdDbnameGroup);
        dbnameGroup.setLayout(layout);

        final Label databaseName = new Label(dbnameGroup, SWT.LEFT | SWT.WRAP);

        databaseName.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        databaseName.setText(Messages.lblParamDumpDbName);

        dbNameText = new Text(dbnameGroup, SWT.BORDER);
        dbNameText.setEnabled(false);
        final GridData gdDbNameText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dbNameText.setLayoutData(gdDbNameText);
    }

    /** Init the dialog */
    private void initial() {
        dbNameText.setText(result.getDbName());
        TableColumn tblColumn = new TableColumn(table, SWT.LEFT);
        tblColumn.setText(Messages.tblTtlParamName);
        tblColumn.setWidth(250);
        tblColumn = new TableColumn(table, SWT.LEFT);
        tblColumn.setText(Messages.tblTtlServer);
        tblColumn.setWidth(100);
        if (clientItem) {
            tblColumn = new TableColumn(table, SWT.LEFT);
            tblColumn.setText(Messages.tblTtlClient);
            tblColumn.setWidth(100);
        }
        TableItem item;

        for (Map.Entry<String, String> entry : result.getServerData().entrySet()) {
            item = new TableItem(table, SWT.NONE);
            item.setText(0, entry.getKey());
            item.setText(1, entry.getValue());
            if (result.getClientData().get(entry.getKey()) != null && clientItem) {
                item.setText(2, result.getClientData().get(entry.getKey()));
            }
        }
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleParamDumpDialog);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnOK,
                true);
    }

    /**
     * Get the database.
     *
     * @return database
     */
    public CubridDatabase getDatabase() {
        return database;
    }

    /**
     * set the database.
     *
     * @param database CubridDatabase
     */
    public void setDatabase(CubridDatabase database) {
        this.database = database;
    }

    /**
     * get the result.
     *
     * @return result
     */
    public ParamDumpInfo getResult() {
        return result;
    }

    /**
     * set the result.
     *
     * @param result StringBuffer
     */
    public void setResult(ParamDumpInfo result) {
        this.result = result;
    }

    /**
     * the client table item
     *
     * @return clientItem boolean
     */
    public boolean isClientItem() {
        return clientItem;
    }

    /**
     * set the client table item
     *
     * @param clientItem the clientItem to set
     */
    public void setClientItem(boolean clientItem) {
        this.clientItem = clientItem;
    }
}
