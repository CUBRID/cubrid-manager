/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.cubrid.table.dialog;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.cubrid.table.progress.ExportTableDefinitionProgress;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.File;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * dialog to export table definition to excel
 *
 * @author fulei 2012-12-06
 */
public class ExportTableDefinitionDialog extends CMTitleAreaDialog {
    private final String PATHFILTER = "definitionPathFilter";
    private List<String> exportTableList;
    private boolean exportAllTables;
    private final CubridDatabase database;
    private Text exlPathText;
    private Text exlNameText;
    private String excelFullPathText;
    private Combo fileCharsetCombo;
    private int exportLayoutType;
    private String fileName;

    public ExportTableDefinitionDialog(
            Shell parentShell,
            CubridDatabase database,
            boolean exportAllTables,
            List<String> exportTableList) {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE | SWT.MAX);
        this.database = database;
        this.exportAllTables = exportAllTables;
        this.exportTableList = exportTableList;
        this.excelFullPathText = database.getDatabase().getName();
        this.exportLayoutType = 1;
        this.fileName = "tablelist";
        try {
            fileName += "_" + database.getDatabaseInfo().getDbName();
        } catch (Exception ignored) {
            this.fileName = "tablelist";
        }
        this.fileName += "_" + DateUtil.getDatetimeStringOnNow("yyyyMMdd");
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.exportTableDefinitionTitle);

        setTitle(Messages.exportTableDefinitionTitle);
        setMessage(Messages.exportTableDefinitionMessage);

        Composite comp = new Composite(parent, SWT.BORDER);
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
        comp.setLayout(new GridLayout(4, false));

        Label exlPathLabel = new Label(comp, SWT.NONE);
        exlPathLabel.setText(Messages.exportTableDefinitionExcelPathLabel);
        exlPathLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        exlPathText = new Text(comp, SWT.BORDER | SWT.READ_ONLY);
        exlPathText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

        Button exlButton = new Button(comp, SWT.NONE);
        exlButton.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));
        exlButton.setText(Messages.brokerLogTopMergeOpenBtn);
        exlButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        DirectoryDialog dialog =
                                new DirectoryDialog(
                                        PlatformUI.getWorkbench().getDisplay().getActiveShell());
                        dialog.setFilterPath(
                                PersistUtils.getPreferenceValue(
                                        CommonUIPlugin.PLUGIN_ID, PATHFILTER));
                        String dir = dialog.open();
                        if (dir != null) {
                            if (!dir.endsWith(File.separator)) {
                                dir += File.separator;
                            }
                            exlPathText.setText(dir);
                            excelFullPathText = dir + exlNameText.getText() + ".xls";
                            PersistUtils.setPreferenceValue(
                                    CommonUIPlugin.PLUGIN_ID, PATHFILTER, dir);
                        }
                        //				validate();
                    }
                });

        Label exlNameLabel = new Label(comp, SWT.NONE);
        exlNameLabel.setText(Messages.exportTableDefinitionExcelNameLabel);
        exlNameLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        exlNameText = new Text(comp, SWT.BORDER);
        exlNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        exlNameText.setText(fileName);
        exlNameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(final ModifyEvent event) {
                        excelFullPathText = exlPathText.getText() + exlNameText.getText() + ".xls";
                        //				validate();
                    }
                });
        Label exlExtLabel = new Label(comp, SWT.NONE);
        exlExtLabel.setText(".xls");
        exlExtLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));
        //
        Label exlFileCharsetLabel = new Label(comp, SWT.NONE);
        exlFileCharsetLabel.setText(Messages.exportTableDefinitionExcelFileCharsetLabel);
        exlFileCharsetLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        fileCharsetCombo = new Combo(comp, SWT.NONE);
        fileCharsetCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        {
            String[] charsets = QueryOptions.getAllCharset(null);
            fileCharsetCombo.setItems(charsets);
            fileCharsetCombo.select(0);
            int i = 0;
            for (String charset : charsets) {
                if (charset != null
                        && charset.equalsIgnoreCase(Messages.exportTableDefinitionDefaultCharset)) {
                    fileCharsetCombo.select(i);
                }
                i++;
            }
        }

        new Label(comp, SWT.NONE);

        Label exlLayoutTypeLabel = new Label(comp, SWT.NONE);
        exlLayoutTypeLabel.setText(Messages.exportTableDefinitionExcelLayoutTypeLabel);
        exlLayoutTypeLabel.setLayoutData(
                CommonUITool.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 1, 1, -1, -1));

        Button type1Btn = new Button(comp, SWT.RADIO);
        type1Btn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        type1Btn.setText(Messages.exportTableDefinitionExcelLayoutTypeSimple);
        type1Btn.setSelection(true);
        type1Btn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        exportLayoutType = 1;
                    }
                });

        Button type2Btn = new Button(comp, SWT.RADIO);
        type2Btn.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        type2Btn.setText(Messages.exportTableDefinitionExcelLayoutTypeGeneric);
        type2Btn.setSelection(false);
        type1Btn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        exportLayoutType = 2;
                    }
                });

        return parent;
    }

    public boolean validate() {
        return StringUtil.isNotEmptyAll(
                exlPathText.getText(),
                exlNameText.getText(),
                exlPathText.getText(),
                exlNameText.getText());
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!validate()) {
                CommonUITool.openErrorBox(Messages.exportTableDefinitionDialogErrMsg);
                return;
            }
            ExportTableDefinitionProgress progress =
                    new ExportTableDefinitionProgress(
                            database,
                            excelFullPathText,
                            fileCharsetCombo.getText(),
                            exportAllTables,
                            exportTableList,
                            exportLayoutType);
            if (!progress.export()) {
                return;
            }
        }
        setReturnCode(buttonId);
        close();
    }
}
