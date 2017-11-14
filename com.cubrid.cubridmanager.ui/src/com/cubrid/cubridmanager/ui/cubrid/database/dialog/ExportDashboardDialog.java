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

import java.io.File;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;

/**
 * Export dashboard information to excel
 * @author fulei
 *
 * @version 1.0 - 2012-10-10 created by fulei
 */

public class ExportDashboardDialog extends CMTitleAreaDialog{
	private static final Logger LOGGER = LogUtil.getLogger(ExportDashboardDialog.class);
	
	private Combo fileCharsetCombo;
	private Text saveExcelPath;
	private Text saveExcelName;
	private Button saveExcelBtn;
	private final List<Table> exportTableList;
	private final String xlsName;
	private final String[] sheetNames;
	
	/**
	 * constructor
	 * @param parentShell
	 * @param exportTableList export table list
	 * @param xlsName excel name
	 * @param sheetNames excel sheet names
	 */
	public ExportDashboardDialog (Shell parentShell, final List<Table> exportTableList,
			String xlsName, final String[] sheetNames) {
		super(parentShell);
		setShellStyle(SWT.APPLICATION_MODAL); 
		this.exportTableList = exportTableList;
		this.xlsName = xlsName;
		this.sheetNames = sheetNames;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.exportDashboardDialogTitle);

		setTitle(Messages.exportDashboardDialogTitle);
		setMessage(Messages.exportDashboardDialogMessage);
		
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp.setLayout(new GridLayout(1, false));
		
		Composite charsetComp = new Composite(comp, SWT.NONE);
		charsetComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		charsetComp.setLayout(new GridLayout(2, false));
		
		new Label(charsetComp, SWT.NONE).setText(com.cubrid.common.ui.cubrid.table.Messages.lblFileCharset);
		fileCharsetCombo = new Combo(charsetComp, SWT.NONE);
		{
			GridData gridData = new GridData(GridData.BEGINNING);
			fileCharsetCombo.setLayoutData(gridData);
			fileCharsetCombo.setItems(QueryOptions.getAllCharset(null));
			fileCharsetCombo.select(0);
		}
		
		Composite excelNameComp = new Composite(comp, SWT.NONE);
		excelNameComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		excelNameComp.setLayout(new GridLayout(2, false));
		
		new Label(excelNameComp, SWT.NONE).setText(Messages.exportDashboardDialogLblFileName);
		saveExcelName = new Text(excelNameComp, SWT.BORDER);
		GridData saveExcelNameGd = new GridData();
		saveExcelNameGd.widthHint = 150;
		saveExcelName.setLayoutData(saveExcelNameGd);
		saveExcelName.setText(xlsName);
		
		Composite exlComp = new Composite(comp, SWT.NONE);
		exlComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		exlComp.setLayout(new GridLayout(3, false));
		
		new Label(exlComp, SWT.NONE).setText(com.cubrid.common.ui.common.Messages.runSQLDialogExcelPathLabel);
		
		saveExcelPath = new Text(exlComp, SWT.BORDER);
		saveExcelPath.setLayoutData(new GridData(GridData.FILL_BOTH));
		saveExcelPath.setEditable(false);
		
		saveExcelBtn = new Button(exlComp, SWT.NONE);
		saveExcelBtn.setText(com.cubrid.common.ui.common.Messages.brokerLogTopMergeOpenBtn);
		saveExcelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(
						PlatformUI.getWorkbench().getDisplay().getActiveShell());
				dialog.setFilterPath(saveExcelPath.getText());

				String dir = dialog.open();
				if (dir != null) {
					if (!dir.endsWith(File.separator)) {
						dir += File.separator;
					}
					saveExcelPath.setText(dir);
				}
			}
			
		});
		
		return parent;
	}
	
	/**
	 * When press button,call it
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (StringUtil.isEmpty(saveExcelPath.getText())) {
				CommonUITool.openErrorBox(Messages.exportDashboardDialogErrMessage);
				return ;
			}
			if (StringUtil.isEmpty(saveExcelName.getText())) {
				CommonUITool.openErrorBox(Messages.exportDashboardDialogErrMessage2);
				return ;
			}
			
			try {
				if(exportTableList()) {
					CommonUITool.openInformationBox(Messages.titleSuccess, Messages.exportDashboardSuccessMessage);
				}
			} catch (Exception e) {
				CommonUITool.openErrorBox(e.getMessage());
				return ;
			}
		}
		setReturnCode(buttonId);
		close();
	}

	/**
	 * export dashboard table list
	 * @return boolean success
	 */
	public boolean exportTableList () throws Exception{
		WritableWorkbook wwb = null;
		WritableSheet ws = null;
		String fileName = saveExcelPath.getText() + saveExcelName.getText() + ".xls";
		try {
			WritableCellFormat normalCellStyle = getNormalCell();
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding(fileCharsetCombo.getText());
			wwb = Workbook.createWorkbook(new File(fileName), workbookSettings);
			for (int i = 0; i < exportTableList.size(); i ++) {
				ws = wwb.createSheet(sheetNames[i], i);
				
				Table table = exportTableList.get(i);
				int rowIndex = 0;
				//title
				for(int j = 0; j < table.getColumnCount(); j ++) {
					String cellString = table.getColumn(j).getText();
					ws.addCell(new jxl.write.Label(j, rowIndex, cellString, normalCellStyle));
					ws.setColumnView(j, 30);
				}
				rowIndex ++;
				//row
				for (int j = 0; j < table.getItemCount(); j ++) {
					TableItem tableItem = table.getItem(j);
					for (int k =0; k < table.getColumnCount(); k ++) {
						String cellString = tableItem.getText(k);
						ws.addCell(new jxl.write.Label(k, rowIndex, cellString, normalCellStyle));
					}
					rowIndex++;
				}
			}
			wwb.write();
			return true;
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		} finally {
			if (wwb != null) {
				try {
					wwb.close();
				} catch (Exception ex) {
					LOGGER.error("close excel stream error", ex);
				}
			}
		}
	}
	
	/**
	 * getNormalCell
	 * @return WritableCellFormat
	 */
	public static WritableCellFormat getNormalCell() {
		WritableFont font = new WritableFont(WritableFont.TIMES, 11);
		WritableCellFormat format = new WritableCellFormat(font);
		try {
			format.setAlignment(jxl.format.Alignment.CENTRE);
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			format.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}
	
	public Combo getFileCharsetCombo() {
		return fileCharsetCombo;
	}

	public void setFileCharsetCombo(Combo fileCharsetCombo) {
		this.fileCharsetCombo = fileCharsetCombo;
	}

	public Text getSaveErrExcelPath() {
		return saveExcelPath;
	}

	public void setSaveErrExcelPath(Text saveExcelPath) {
		this.saveExcelPath = saveExcelPath;
	}
}
