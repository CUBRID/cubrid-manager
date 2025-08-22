/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.control.BrokerConfigEditorInput;
import com.cubrid.common.ui.common.control.BrokerConfigEditorPart;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * open cubrid broker conf dialog
 *
 * @author fulei
 * @version 1.0 - 2012-10-29 created by fulei
 */
public class BrokerConfOpenFileDialog extends CMTitleAreaDialog {

    private final Logger LOGGER = LogUtil.getLogger(getClass());
    private Combo fileCharsetCombo;
    private Text filePath;
    private Button browseFileBtn;
    public static final String CUBRIDBROKERCONFPATH = "CubridBrokerConf";

    public BrokerConfOpenFileDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.APPLICATION_MODAL);
    }

    protected Control createDialogArea(Composite parent) {

        getShell().setText(Messages.cubridBrokerConfOpenFileDialogTitle);

        setTitle(Messages.cubridBrokerConfOpenFileDialogTitle);
        setMessage(Messages.cubridBrokerConfOpenFileDialogMessage);

        Composite comp = new Composite(parent, SWT.BORDER);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(1, false));

        Composite charsetComp = new Composite(comp, SWT.NONE);
        charsetComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        charsetComp.setLayout(new GridLayout(2, false));

        new Label(charsetComp, SWT.NONE)
                .setText(com.cubrid.common.ui.cubrid.table.Messages.lblFileCharset);
        fileCharsetCombo = new Combo(charsetComp, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.BEGINNING);
            fileCharsetCombo.setLayoutData(gridData);
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            fileCharsetCombo.select(0);
        }

        Composite fileComp = new Composite(comp, SWT.NONE);
        fileComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fileComp.setLayout(new GridLayout(3, false));

        new Label(fileComp, SWT.NONE).setText(Messages.cubridBrokerConfOpenFileDialogFilePathLabel);

        filePath = new Text(fileComp, SWT.BORDER);
        filePath.setLayoutData(new GridData(GridData.FILL_BOTH));
        filePath.setEditable(false);
        filePath.setText(
                PersistUtils.getPreferenceValue(CommonUIPlugin.PLUGIN_ID, CUBRIDBROKERCONFPATH));

        browseFileBtn = new Button(fileComp, SWT.NONE);
        browseFileBtn.setText(Messages.brokerLogTopMergeOpenBtn);
        browseFileBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
                        dialog.setFilterPath(
                                PersistUtils.getPreferenceValue(
                                        CommonUIPlugin.PLUGIN_ID, CUBRIDBROKERCONFPATH));
                        dialog.setText(Messages.runSQLSelectFiles);
                        dialog.setFilterExtensions(new String[] {"*.conf"});
                        dialog.setOverwrite(false);
                        String path = dialog.open();
                        if (path != null) {
                            filePath.setText(path);
                            PersistUtils.setPreferenceValue(
                                    CommonUIPlugin.PLUGIN_ID, CUBRIDBROKERCONFPATH, path);
                        }
                    }
                });

        return parent;
    }

    /**
     * validate whether set file path
     *
     * @return
     */
    public boolean validate() {
        if ("".equals(filePath.getText())) {
            return false;
        }
        return true;
    }
    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!validate()) {
                CommonUITool.openErrorBox(Messages.cubridBrokerConfOpenFileDialogErrMsg);
                return;
            }
        }
        try {
            IEditorInput input =
                    new BrokerConfigEditorInput(filePath.getText(), fileCharsetCombo.getText());
            PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, BrokerConfigEditorPart.ID);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        setReturnCode(buttonId);
        close();
    }
}
