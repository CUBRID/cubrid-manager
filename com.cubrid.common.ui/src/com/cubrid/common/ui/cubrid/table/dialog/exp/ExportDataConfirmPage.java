/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog.exp;

import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.spi.ResourceManager;

/**
 * The ExportDataConfirmPage
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-9-6 created by Kevin.Wang
 */
public class ExportDataConfirmPage extends
		ExportWizardPage {

	public final static String PAGE_NAME = ExportDataConfirmPage.class.getName();
	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final String TAB_SPACE = "        ";

	private Text infoTest;

	/**
	 * The constructor
	 *
	 * @param database
	 */
	public ExportDataConfirmPage() {
		super(PAGE_NAME, Messages.exportShellTitle, null);
		setTitle(Messages.titleExportStep3);
		setDescription(Messages.exportWizardComfimrDescription);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
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

		infoTest = new Text(container2, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL);
		infoTest.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		infoTest.setBackground(ResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
	}

	/**
	 * When displayed current page.
	 *
	 * @param event PageChangedEvent
	 */
	protected void afterShowCurrentPage(PageChangedEvent event) { // FIXME move this logic to core module
		ExportConfig exportConfigModel = getExportConfig();
		StringBuilder info = new StringBuilder();
		if (exportConfigModel.getExportType() == ExportConfig.EXPORT_TO_LOADDB) {
			info.append(Messages.lblExportConfirmExportType).append(NEW_LINE);
			info.append(TAB_SPACE).append(Messages.exportWizardType2).append(NEW_LINE);
			info.append(NEW_LINE);

			info.append(Messages.lblExportConfirmExportObjects).append(NEW_LINE);
			if (exportConfigModel.isExportSchema()) {
				info.append(TAB_SPACE).append(Messages.lblExportConfirmSchema);
				info.append(" - ");
				info.append(exportConfigModel.getSchemaFilePath()).append(NEW_LINE);
			}
			if (exportConfigModel.isExportIndex()) {
				info.append(TAB_SPACE).append(Messages.lblExportConfirmIndex);
				info.append(" - ");
				info.append(exportConfigModel.getIndexFilePath()).append(NEW_LINE);
			}
			if (exportConfigModel.isExportTrigger()) {
				info.append(TAB_SPACE).append(Messages.lblExportConfirmTrigger);
				info.append(" - ");
				info.append(exportConfigModel.getTriggerFilePath()).append(NEW_LINE);
			}
			if (exportConfigModel.isExportData()) {
				info.append(TAB_SPACE).append(Messages.lblExportConfirmData);
				info.append(" - ");
				info.append(exportConfigModel.getDataFileFolder()).append(NEW_LINE);
			}
			info.append(NEW_LINE);
		} else {
			info.append(Messages.lblExportConfirmExportType).append(NEW_LINE);
			info.append(TAB_SPACE).append(Messages.exportWizardType1).append(NEW_LINE);
			info.append(NEW_LINE);

			info.append(Messages.lblExportConfirmExportObjects).append(NEW_LINE);
			if (exportConfigModel.isExportSchema()) {
				info.append(TAB_SPACE).append(Messages.lblExportConfirmSchema).append(NEW_LINE);
			}
			if (exportConfigModel.isExportData()) {
				info.append(TAB_SPACE).append(Messages.lblExportConfirmData).append(NEW_LINE);
			}
			if (exportConfigModel.isExportSchema()) {
				if (exportConfigModel.isExportIndex()) {
					info.append(TAB_SPACE).append(Messages.lblExportConfirmIndex).append(NEW_LINE);
				}
				if (exportConfigModel.isExportSerial()) {
					info.append(TAB_SPACE).append(Messages.lblExportConfirmSerial).append(NEW_LINE);
				}
				if (exportConfigModel.isExportView()) {
					info.append(TAB_SPACE).append(Messages.lblExportConfirmView).append(NEW_LINE);
				}
				if (exportConfigModel.isExportTrigger()) {
					info.append(TAB_SPACE).append(Messages.lblExportConfirmTrigger).append(NEW_LINE);
				}
			}
			info.append(NEW_LINE);

			info.append(Messages.lblExportConfirmFileType).append(NEW_LINE);
			if (exportConfigModel.getExportFileType() == ExportConfig.FILE_TYPE_SQL) {
				info.append(TAB_SPACE).append("SQL").append(NEW_LINE);
			} else if (exportConfigModel.getExportFileType() == ExportConfig.FILE_TYPE_CSV) {
				info.append(TAB_SPACE).append("CSV").append(NEW_LINE);
			} else if (exportConfigModel.getExportFileType() == ExportConfig.FILE_TYPE_XLS) {
				info.append(TAB_SPACE).append("XLS").append(NEW_LINE);
			} else if (exportConfigModel.getExportFileType() == ExportConfig.FILE_TYPE_XLSX) {
				info.append(TAB_SPACE).append("XLSX").append(NEW_LINE);
			} else if (exportConfigModel.getExportFileType() == ExportConfig.FILE_TYPE_OBS) {
				info.append(TAB_SPACE).append("OBS").append(NEW_LINE);
			} else if (exportConfigModel.getExportFileType() == ExportConfig.FILE_TYPE_TXT) {
				info.append(TAB_SPACE).append("TXT").append(NEW_LINE);
			}
			info.append(NEW_LINE);

			info.append(Messages.lblExportConfirmFilePath).append(NEW_LINE);
			info.append(TAB_SPACE).append(exportConfigModel.getDataFileFolder()).append(NEW_LINE);
			info.append(NEW_LINE);

			info.append(Messages.lblExportConfirmThreads).append(NEW_LINE);
			info.append(TAB_SPACE).append(exportConfigModel.getThreadCount()).append(NEW_LINE);
			info.append(NEW_LINE);
		}

		info.append(Messages.lblExportConfirmCharset).append(NEW_LINE);
		info.append(TAB_SPACE).append(exportConfigModel.getFileCharset()).append(NEW_LINE);
		info.append(NEW_LINE);

		info.append(Messages.lblExportConfirmTables).append(NEW_LINE);
		for (String tableName : exportConfigModel.getTableNameList()) {
			info.append(TAB_SPACE).append(tableName);
			String whereCondition = exportConfigModel.getWhereCondition(tableName);
			if (whereCondition != null) {
				info.append(TAB_SPACE).append(whereCondition);
			}
			info.append(NEW_LINE);
		}
		infoTest.setText(info.toString().trim());
	}
}
