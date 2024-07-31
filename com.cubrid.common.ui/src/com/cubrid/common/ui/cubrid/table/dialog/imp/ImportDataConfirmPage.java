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
package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.dialog.CMWizardPage;
import java.io.File;
import java.util.Map;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author fulei
 * @version 1.0 - 2012-8-16 created by fulei
 */
public class ImportDataConfirmPage extends CMWizardPage {
    public static final String PAGE_NAME = ImportDataConfirmPage.class.getName();
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String TAB_SPACE = "        ";

    private Text infoTest;

    /**
     * @param pageName
     * @param title
     * @param titleImage
     */
    protected ImportDataConfirmPage() {
        super(PAGE_NAME, Messages.importShellTitle, null);
        setTitle(Messages.titleImportStep3);
        setDescription(Messages.importWizardComfimrDescription);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayoutRoot = new GridLayout();
        container.setLayout(gridLayoutRoot);
        setControl(container);

        Composite container2 = new Composite(container, SWT.BORDER);
        final GridData gdContainer2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        container2.setLayoutData(gdContainer2);
        container2.setLayout(new GridLayout());

        infoTest =
                new Text(
                        container2,
                        SWT.LEFT | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        infoTest.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        infoTest.setEditable(false);
        infoTest.setBackground(ResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
    }

    /**
     * When displayed current page.
     *
     * @param event PageChangedEvent
     */
    protected void afterShowCurrentPage(PageChangedEvent event) {
        ImportConfig importConfig = getImportDataWizard().getImportConfig();

        String confirmInfo = "";
        if (ImportConfig.IMPORT_FROM_EXCEL == importConfig.getImportType()) {
            confirmInfo = makeExcelConfirmInfoText();
        } else if (ImportConfig.IMPORT_FROM_SQL == importConfig.getImportType()) {
            confirmInfo = makeSQLConfirmInfoText();
        } else if (ImportConfig.IMPORT_FROM_TXT == importConfig.getImportType()) {
            confirmInfo = makeTxtConfirmInfoText();
        }

        infoTest.setText(confirmInfo == null ? "" : confirmInfo);
    }

    private String makeExcelConfirmInfoText() {
        return makeConfirmInfoText("Excel file");
    }

    private String makeTxtConfirmInfoText() {
        return makeConfirmInfoText("TXT file");
    }

    private String makeSQLConfirmInfoText() {
        return makeConfirmInfoText("SQL file");
    }

    private String makeConfirmInfoText(
            String importTypeI18Name) { // FIXME move this logic to core module
        ImportConfig importConfig = getImportDataWizard().getImportConfig();

        StringBuilder sb = new StringBuilder();
        sb.append(Messages.lblImportConfirmImportType).append(NEW_LINE);
        sb.append(TAB_SPACE).append(importTypeI18Name).append(NEW_LINE);
        sb.append(NEW_LINE);

        sb.append(Messages.lblImportConfirmCommitCount).append(NEW_LINE);
        sb.append(TAB_SPACE).append(importConfig.getCommitLine()).append(NEW_LINE);
        sb.append(NEW_LINE);

        sb.append(Messages.lblImportConfirmThreads).append(NEW_LINE);
        sb.append(TAB_SPACE).append(importConfig.getThreadCount()).append(NEW_LINE);
        sb.append(NEW_LINE);

        sb.append(Messages.lblImportConfirmFileList).append(NEW_LINE);
        Map<String, TableConfig> selectedMap = importConfig.getSelectedMap();
        for (TableConfig config : selectedMap.values()) {
            sb.append(TAB_SPACE)
                    .append(new File(config.getFilePath()).getAbsolutePath())
                    .append(NEW_LINE);
        }

        return sb.toString().trim();
    }

    private ImportDataWizard getImportDataWizard() {
        return (ImportDataWizard) getWizard();
    }
}
