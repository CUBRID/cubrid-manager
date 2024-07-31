/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog.imp;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.List;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * The Import Data Wizard
 *
 * @author Kevin.Wang
 * @version 1.0 - Jul 30, 2012 created by Kevin.Wang
 */
public class ImportDataWizard extends Wizard {
    public static final String SESSION_IMPORT_KEY =
            "ImportTableDataDialog-ImportFilePath"; //$NON-NLS-1$

    private static final Logger LOGGER = LogUtil.getLogger(ImportDataWizard.class);

    private ImportConfig importConfig;
    private ImportTypePage importTypePage;
    private ImportSettingExcelPage importSettingPage;
    private ImportSettingTxtPage importSettingForTxtPage;
    private ImportSettingSQLPage importSettingForSQLPage;
    private ImportDataConfirmPage importDataConfirmPage;
    private CubridDatabase database;

    /**
     * The constructor
     *
     * @param database
     * @param tableName
     */
    public ImportDataWizard(CubridDatabase database, List<String> selectedTables) {
        this.database = database;
        importConfig = new ImportConfig();

        for (String table : selectedTables) {
            importConfig.addTableConfig(new TableConfig(table));
        }

        setWindowTitle(Messages.importShellTitle);
    }

    /**
     * The constructor
     *
     * @param database
     * @param tableName
     * @param configModel
     */
    public ImportDataWizard(CubridDatabase database, ImportConfig configModel) {
        this.database = database;
        this.importConfig = configModel;
    }

    /** Add wizard page */
    public void addPages() {
        importTypePage = new ImportTypePage();
        addPage(importTypePage);
        importSettingPage = new ImportSettingExcelPage(database);
        addPage(importSettingPage);
        importSettingForTxtPage = new ImportSettingTxtPage(database);
        addPage(importSettingForTxtPage);
        importSettingForSQLPage = new ImportSettingSQLPage(database);
        addPage(importSettingForSQLPage);
        importDataConfirmPage = new ImportDataConfirmPage();
        addPage(importDataConfirmPage);
    }

    public ImportConfig getImportConfig() {
        return importConfig;
    }

    public void setImportConfig(ImportConfig importConfig) {
        this.importConfig = importConfig;
    }

    /** Perform finish */
    public boolean performFinish() {
        boolean result = CommonUITool.openConfirmBox(Messages.confirmStartImportWizard);
        if (result) {
            ImportDataEditorInput input = new ImportDataEditorInput();
            input.setDatabase(database);
            input.setImportConfig(importConfig);
            try {
                PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .openEditor(input, ImportDataViewPart.ID);
            } catch (Exception e) {
                CommonUITool.openErrorBox(getShell(), e.getMessage());
                LOGGER.error("", e);
            }
        }

        return result;
    }

    /**
     * perform Cancel
     *
     * @return boolean
     */
    public boolean performCancel() {
        return CommonUITool.openConfirmBox(Messages.confirmExitImportWizard);
    }

    public IWizardPage getNextPage(IWizardPage page) {
        if (page.equals(importTypePage)) {
            if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_TXT) {
                return importSettingForTxtPage;
            } else if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_SQL) {
                return importSettingForSQLPage;
            } else if (importConfig.getImportType() == ImportConfig.IMPORT_FROM_EXCEL) {
                return importSettingPage;
            }
        } else if (page.equals(importSettingForSQLPage)
                || page.equals(importSettingPage)
                || page.equals(importSettingForTxtPage)) {
            return importDataConfirmPage;
        }
        return super.getNextPage(page);
    }

    public boolean canFinish() {
        if (this.getContainer().getCurrentPage() instanceof ImportDataConfirmPage) {
            return true;
        }
        return false;
    }
}
