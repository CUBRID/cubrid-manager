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
package com.cubrid.common.ui.cubrid.table.preference;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for query options
 *
 * @author Kevin 2011-3-23
 */
public class ImportPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String NULLVALUES_KEY = "com.cubrid.table.import.nullvalues";
    private static final String EXPORT_NULL_VALUE_KEY = "com.cubrid.table.export.nullvalue";

    public static final String ID = "com.cubrid.common.ui.preference.ImportPreferencePage";

    public static final String[] NULL_LIST = new String[] {"NULL", "(NULL)", "\\N"};
    private static final String EMPTY = "EMPTY";

    private final List<Button> btnList = new ArrayList<Button>();

    private Text importOthersText;

    private Button exportNullButton;
    private Button exportBracketNullButton;
    private Button exportNButton;
    private Button exportOtherButton;
    private Text exportOtherText;

    public ImportPreferencePage() {
        super(Messages.importSetupTitle, null);
    }

    /**
     * Retrieves the null value list setup.
     *
     * @return String array.
     */
    public static String[] getImportNULLValueList() {
        String values = PersistUtils.getPreferenceValue(CommonUIPlugin.PLUGIN_ID, NULLVALUES_KEY);
        if (values.trim().length() == 0) {
            return NULL_LIST.clone();
        }
        return EMPTY.equals(values) ? new String[] {} : values.split(",");
    }

    /**
     * Get the null value for the export data
     *
     * @return
     */
    public static String getExportNullValue() {
        String nullValue =
                PersistUtils.getPreferenceValue(CommonUIPlugin.PLUGIN_ID, EXPORT_NULL_VALUE_KEY);
        if (nullValue.length() == 0) {
            nullValue = "NULL";
        }
        return nullValue;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     * @param workbench the workbench
     */
    public void init(IWorkbench workbench) {
        // empty
    }

    /** load the preference data */
    private void loadPreference() { // FIXME move this logic to core module
        /*Import Options*/
        String[] valueList = getImportNULLValueList();
        StringBuffer sb = new StringBuffer();
        for (String value : valueList) {
            boolean isDefault = false;
            for (Button btn : btnList) {
                if (value.equals(btn.getData().toString())) {
                    btn.setSelection(true);
                    isDefault = true;
                    break;
                }
            }
            if (!isDefault) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(value);
            }
        }
        importOthersText.setText(sb.toString());

        /*Export Options*/
        String nullValue = getExportNullValue();
        if ("NULL".equals(nullValue)) {
            exportNullButton.setSelection(true);

            exportBracketNullButton.setSelection(false);
            exportNButton.setSelection(false);
            exportOtherButton.setSelection(false);
            exportOtherText.setEnabled(false);
        } else if ("(NULL)".equals(nullValue)) {
            exportNullButton.setSelection(false);

            exportBracketNullButton.setSelection(true);

            exportNButton.setSelection(false);
            exportOtherButton.setSelection(false);
            exportOtherText.setEnabled(false);
        } else if ("\\N".equals(nullValue)) {
            exportNullButton.setSelection(false);
            exportBracketNullButton.setSelection(false);

            exportNButton.setSelection(true);

            exportOtherButton.setSelection(false);
            exportOtherText.setEnabled(false);
        } else {
            exportNullButton.setSelection(false);
            exportBracketNullButton.setSelection(false);
            exportNButton.setSelection(false);

            exportOtherButton.setSelection(true);

            exportOtherText.setText(nullValue);
            exportOtherText.setEnabled(true);
        }
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     * @return boolean
     */
    public boolean performOk() {
        performApply();
        return true;
    }

    /**
     * @see
     *     org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     * @param parent the parent composite
     * @return the new control
     */
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        /*Import Options*/
        Group importGroup = new Group(container, SWT.NONE);
        importGroup.setText(Messages.txtImportGroup);
        importGroup.setLayout(new GridLayout(1, true));
        importGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label labelHint = new Label(importGroup, SWT.LEFT);
        labelHint.setLayoutData(new GridData(SWT.END));
        labelHint.setText(Messages.importSetupLabel);

        Composite btnComp = new Composite(importGroup, SWT.NONE);
        btnComp.setLayout(new GridLayout(3, true));
        btnComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        for (String ns : NULL_LIST) {
            Button btnNull = new Button(btnComp, SWT.CHECK);
            btnNull.setText("'" + ns + "'");
            btnNull.setData(ns);
            btnList.add(btnNull);
        }

        Composite otherValueComp = new Composite(importGroup, SWT.NONE);
        otherValueComp.setLayout(new GridLayout(2, false));
        otherValueComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label lblLabel = new Label(otherValueComp, SWT.NONE);
        lblLabel.setText(Messages.lblOtherValue);
        GridData layoutData = new GridData(GridData.BEGINNING);
        lblLabel.setLayoutData(layoutData);

        importOthersText = new Text(otherValueComp, SWT.BORDER);
        importOthersText.setText("");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.widthHint = 150;
        importOthersText.setLayoutData(layoutData);
        importOthersText.setTextLimit(256);

        /*Export options*/
        Group exportGroup = new Group(container, SWT.NONE);
        exportGroup.setText(Messages.txtExportGroup);
        exportGroup.setLayout(new GridLayout(1, true));
        exportGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label exportHint = new Label(exportGroup, SWT.LEFT);
        exportHint.setLayoutData(new GridData(SWT.END));
        exportHint.setText(Messages.exportSetupLabel1);

        Composite exportBtnComp = new Composite(exportGroup, SWT.NONE);
        exportBtnComp.setLayout(new GridLayout(5, false));
        exportBtnComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        exportNullButton = new Button(exportBtnComp, SWT.RADIO);
        exportNullButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        exportNullButton.setText("\'" + "NULL" + "\'");
        exportNullButton.setSelection(true);

        exportBracketNullButton = new Button(exportBtnComp, SWT.RADIO);
        exportBracketNullButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        exportBracketNullButton.setText("\'" + "(NULL)" + "\'");
        exportBracketNullButton.setSelection(false);

        exportNButton = new Button(exportBtnComp, SWT.RADIO);
        exportNButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        exportNButton.setText("\'" + "\\N" + "\'");
        exportNButton.setSelection(false);

        exportOtherButton = new Button(exportBtnComp, SWT.RADIO);
        exportOtherButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        exportOtherButton.setText(Messages.exportOtherValue);
        exportOtherButton.setSelection(false);

        exportOtherButton.addSelectionListener(
                new SelectionListener() {

                    public void widgetSelected(SelectionEvent e) {
                        widgetDefaultSelected(e);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        if (exportOtherButton.getSelection()) {
                            exportOtherText.setEnabled(true);
                        } else {
                            exportOtherText.setEnabled(false);
                        }
                    }
                });

        exportOtherText = new Text(exportBtnComp, SWT.BORDER);
        exportOtherText.setText("");
        exportOtherText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        exportOtherText.setTextLimit(64);
        exportOtherText.setEnabled(false);

        loadPreference();
        return container;
    }

    /** Save setup. */
    protected void performApply() { // FIXME move this logic to core module
        setErrorMessage(null);

        List<String> tl = new ArrayList<String>();

        for (Button btn : btnList) {
            if (btn.getSelection()) {
                tl.add(btn.getData().toString());
            }
        }
        if (importOthersText.getText().length() > 0) {
            String[] others = importOthersText.getText().split(",");
            for (String value : others) {
                if (tl.indexOf(value) < 0) {
                    tl.add(value);
                }
            }
        }

        StringBuffer sb = new StringBuffer();
        for (String value : tl) {
            sb.append(',').append(value);
        }

        if (tl.isEmpty()) {
            sb.append(EMPTY);
        } else {
            sb = sb.replace(0, 1, "");
        }
        PersistUtils.setPreferenceValue(CommonUIPlugin.PLUGIN_ID, NULLVALUES_KEY, sb.toString());
        DataType.setNULLValuesForImport(getImportNULLValueList());

        /*Export options*/
        String exportNullValue = getSettingNullValue();

        if (exportOtherButton.getSelection()) {
            if (exportOtherText.getText().trim().length() == 0) {
                setErrorMessage(Messages.msgErrorOtherValueEmpty);
                return;
            }

            if (exportOtherText.getText().indexOf(",") >= 0) {
                setErrorMessage(Messages.msgErrorContainsComma);
                return;
            }
        }

        PersistUtils.setPreferenceValue(
                CommonUIPlugin.PLUGIN_ID, EXPORT_NULL_VALUE_KEY, exportNullValue);
        loadPreference();
    }

    /**
     * Get the export null value which is in export options
     *
     * @return
     */
    private String getSettingNullValue() { // FIXME move this logic to core module
        if (exportNullButton.getSelection()) {
            return "NULL";
        }

        if (exportBracketNullButton.getSelection()) {
            return "(NULL)";
        }

        if (exportNButton.getSelection()) {
            return "\\N";
        }

        if (exportOtherButton.getSelection()) {
            return exportOtherText.getText();
        }

        return "";
    }

    /** Performs special processing when this page's Defaults button has been pressed. */
    protected void performDefaults() {
        for (Button btn : btnList) {
            btn.setSelection(true);
        }

        exportNullButton.setSelection(true);
        exportBracketNullButton.setSelection(false);
        exportNButton.setSelection(false);
        exportOtherButton.setSelection(false);
        exportOtherText.setEnabled(false);
        exportOtherText.setText("");
    }
}
