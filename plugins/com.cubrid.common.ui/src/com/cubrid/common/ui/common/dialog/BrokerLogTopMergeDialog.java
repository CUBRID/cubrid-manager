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
package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.BrokerLogTopMergeProgress;
import java.io.File;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * parse BrokerLogTop dialog
 *
 * @author fulei
 * @version 1.0 - 2012-6-15 created by fulei
 */
public class BrokerLogTopMergeDialog extends CMTitleAreaDialog {

    private Text qText;
    private Text resText;
    private Text exlPathText;
    private Text exlNameText;
    private Text excelFullPathText;
    private Spinner lineCountspinner;
    private Combo fileCharsetCombo;

    public BrokerLogTopMergeDialog(Shell parentShell) {
        super(parentShell);
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.brokerLogTopMergeDialogTitle);

        setTitle(Messages.brokerLogTopMergeDialogTitle);
        setMessage(Messages.brokerLogTopMergeDialogMessages);

        Composite comp = new Composite(parent, SWT.BORDER);
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
        comp.setLayout(new GridLayout(3, false));

        Label qLabel = new Label(comp, SWT.NONE);
        qLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        qLabel.setText(Messages.brokerLogTopMergeQLabel);
        qText = new Text(comp, SWT.BORDER | SWT.READ_ONLY);
        GridData text1Gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        text1Gd.grabExcessHorizontalSpace = true;
        qText.setLayoutData(text1Gd);
        Button qButton = new Button(comp, SWT.NONE);
        qButton.setText(Messages.brokerLogTopMergeOpenBtn);

        qButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        final FileDialog fileDialog =
                                new FileDialog(
                                        PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                        SWT.SINGLE);
                        fileDialog.setFilterPath(qText.getText());
                        fileDialog.setFilterExtensions(new String[] {"*.q"});
                        fileDialog.setFilterNames(new String[] {"*.q"});

                        final String fileTmp = fileDialog.open();
                        if (fileTmp == null) {
                            return;
                        }

                        qText.setText(fileTmp);
                        validate();
                    }
                });

        Label resLabel = new Label(comp, SWT.NONE);
        resLabel.setText(Messages.brokerLogTopMergeResLabel);
        resLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        resText = new Text(comp, SWT.BORDER | SWT.READ_ONLY);
        GridData resTextGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        resTextGd.grabExcessHorizontalSpace = true;
        resText.setLayoutData(resTextGd);
        Button resButton = new Button(comp, SWT.NONE);
        resButton.setText(Messages.brokerLogTopMergeOpenBtn);
        resButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        final FileDialog fileDialog =
                                new FileDialog(
                                        PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                        SWT.SINGLE);
                        fileDialog.setFilterPath(resText.getText());
                        fileDialog.setFilterExtensions(new String[] {"*.res"});
                        fileDialog.setFilterNames(new String[] {"*.res"});

                        final String fileTmp = fileDialog.open();
                        if (fileTmp == null) {
                            return;
                        }

                        resText.setText(fileTmp);
                        validate();
                    }
                });

        Label exlPathLabel = new Label(comp, SWT.NONE);
        exlPathLabel.setText(Messages.brokerLogTopMergeExcelPathLabel);
        exlPathLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        exlPathText = new Text(comp, SWT.BORDER | SWT.READ_ONLY);
        GridData exlTextGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        exlTextGd.grabExcessHorizontalSpace = true;
        exlPathText.setLayoutData(exlTextGd);
        Button exlButton = new Button(comp, SWT.NONE);
        exlButton.setText(Messages.brokerLogTopMergeOpenBtn);
        exlButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        DirectoryDialog dialog =
                                new DirectoryDialog(
                                        PlatformUI.getWorkbench().getDisplay().getActiveShell());
                        dialog.setFilterPath(exlPathText.getText());

                        String dir = dialog.open();
                        if (dir != null) {
                            if (!dir.endsWith(File.separator)) {
                                dir += File.separator;
                            }
                            exlPathText.setText(dir);
                            excelFullPathText.setText(dir + exlNameText.getText() + ".xls");
                        }
                        validate();
                    }
                });

        new Label(comp, SWT.NONE).setText(Messages.brokerLogTopMergeExcelNameLabel);
        exlNameText = new Text(comp, SWT.BORDER);
        exlNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        exlNameText.setText("log_top_merge");
        exlNameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(final ModifyEvent event) {
                        excelFullPathText.setText(
                                exlPathText.getText() + exlNameText.getText() + ".xls");
                        validate();
                    }
                });
        new Label(comp, SWT.NONE);

        new Label(comp, SWT.NONE).setText(Messages.brokerLogTopMergeExcelFullNameLabel);
        excelFullPathText = new Text(comp, SWT.READ_ONLY | SWT.BORDER);
        excelFullPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        excelFullPathText.setText(exlPathText.getText() + exlNameText.getText() + ".xls");
        new Label(comp, SWT.NONE);

        new Label(comp, SWT.NONE)
                .setText(com.cubrid.common.ui.cubrid.table.Messages.lblFileCharset);
        Composite paramComp = new Composite(comp, SWT.NONE);
        GridData paramCompGd = new GridData(GridData.FILL_HORIZONTAL);
        paramCompGd.horizontalSpan = 2;
        paramComp.setLayoutData(paramCompGd);
        paramComp.setLayout(new GridLayout(3, false));
        fileCharsetCombo = new Combo(paramComp, SWT.NONE);
        {
            GridData gridData = new GridData(GridData.BEGINNING);
            fileCharsetCombo.setLayoutData(gridData);
            fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
            fileCharsetCombo.select(0);
        }

        new Label(paramComp, SWT.NONE).setText(Messages.brokerLogTopMergePartionLineLabel);
        lineCountspinner = new Spinner(paramComp, SWT.BORDER | SWT.LEFT);
        lineCountspinner.setValues(3000, 1000, 10000, 0, 1000, 1000);
        lineCountspinner.setToolTipText(Messages.brokerLogTopMergePartionLineDescription);

        return parent;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    public void validate() {
        boolean fillInAll =
                StringUtil.isNotEmptyAll(
                        qText.getText(),
                        resText.getText(),
                        exlPathText.getText(),
                        exlNameText.getText());
        getButton(IDialogConstants.OK_ID).setEnabled(fillInAll);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            BrokerLogTopMergeProgress progress =
                    new BrokerLogTopMergeProgress(
                            qText.getText(),
                            resText.getText(),
                            excelFullPathText.getText(),
                            Integer.valueOf(lineCountspinner.getText()),
                            fileCharsetCombo.getText());
            if (!progress.merge()) {
                return;
            }
        }
        setReturnCode(buttonId);
        close();
    }

    @Override
    protected Point getInitialSize() {
        Point p = super.getInitialSize();
        p.x = 520;
        p.y = 340;
        return p;
    }
}
