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
package com.cubrid.common.ui.cubrid.table.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.dialog.CMTrayDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.TableUtil;

/**
 * Import Result Dialog to show error messages after import data to cubrid.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-3-17 created by Kevin Cao
 */
public class ImportResultDialog extends
		CMTrayDialog {

	private static final Logger LOGGER = LogUtil.getLogger(ImportResultDialog.class);

	private final List<String> errorList;
	private final String errorLogPath;
	private final String message;
	private Table table;

	protected ImportResultDialog(Shell parentShell, String message,
			List<String> errorList, String errorLogPath) {
		super(parentShell);
		this.errorList = errorList;
		this.errorLogPath = errorLogPath;
		this.message = message;
	}

	/**
	 * Create the dialog area
	 *
	 * @param parent Composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComp, SWT.NONE);
		{
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
		}

		TableViewer tableViewer = new TableViewer(composite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		table = tableViewer.getTable();
		{
			GridData gdData = new GridData(GridData.FILL_BOTH);
			gdData.heightHint = 400;
			gdData.widthHint = 500;
			table.setLayoutData(gdData);
		}

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText(Messages.importColumnNO);
		tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText(Messages.importColumnMessage);

		tableViewer.setLabelProvider(new TableLabelProvider(tableViewer, null));
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setInput(errorList);

		//Create the context menu
		MenuManager contextMenuManager = new MenuManager();
		contextMenuManager.setRemoveAllWhenShown(true);
		contextMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				//copy action
				Action copyAction = new Action(Messages.bind(
						Messages.contextCopy, "Ctrl+C")) {
					public void run() {
						BusyIndicator.showWhile(Display.getDefault(),
								new Runnable() {
									public void run() {
										copyDataToClipboard();
									}
								});
					}
				};
				manager.add(copyAction);
			}
		});
		Menu contextMenu = contextMenuManager.createContextMenu(table);
		table.setMenu(contextMenu);
		//Add listener
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if ((event.stateMask & SWT.CTRL) != 0
						&& (event.stateMask & SWT.SHIFT) == 0
						&& event.keyCode == 'c') {
					copyDataToClipboard();
				}
			}
		});

		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn tblColumn = table.getColumn(i);
			tblColumn.pack();
			if (tblColumn.getWidth() > 400) {
				tblColumn.setWidth(400);
			}
		}

		Label label = new Label(composite, SWT.LEFT);
		label.setForeground(ResourceManager.getColor(255, 0, 0));
		label.setText(message);
		return composite;
	}

	/**
	 *
	 * Copy the selected data to clipboard
	 *
	 */
	private void copyDataToClipboard() { // FIXME move this logic to core module
		TextTransfer textTransfer = TextTransfer.getInstance();
		Clipboard clipboard = CommonUITool.getClipboard();
		StringBuilder content = new StringBuilder();

		TableItem[] items = table.getSelection();
		for (int i = 0; i < items.length; i++) {
			content.append(items[i].getText(1)
					+ System.getProperty("line.separator"));
		}

		String data = content.toString();
		if (data != null && !data.equals("")) {
			clipboard.setContents(new Object[]{data },
					new Transfer[]{textTransfer });
		}
	}

	/**
	 * Create the buttons
	 *
	 * @param parent Composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Messages.btnSaveReport, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.common.ui.query.Messages.close, true);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 * @param buttonId the id of the button that was pressed (see
	 *        <code>IDialogConstants.*_ID</code> constants)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (errorLogPath == null) {
				return;
			}
			String[] filterExts = new String[]{"*.xls" };
			String[] filterNames = new String[]{Messages.xlsFileType };
			File file = TableUtil.getSavedFile(getShell(), filterExts,
					filterNames, null, ".xls", null);
			if (file == null) {
				return;
			}
			writeExcel(file);

			return;
		}
		super.buttonPressed(buttonId);
	}


	/**
	 * wirte error message to exlce
	 * @param file
	 */
	public void writeExcel(File file){ // FIXME move this logic to core module
		WritableWorkbook wwb = null;
		try {
			wwb = Workbook.createWorkbook(file);
			WritableSheet ws = wwb.createSheet("error", 0);
			ws.setColumnView(0, 100);
			jxl.write.Label label = null;
			for (int i = 0; i < errorList.size(); i ++) {
				label = new jxl.write.Label(0, i,errorList.get(i), getNormolCell());
				ws.addCell(label);
			}

			wwb.write();
			CommonUITool.openInformationBox(Messages.infoSuccess, Messages.importResultDialogWriteExcelSuccessInfo);
		} catch (Exception e){
			LOGGER.error("write excel error", e);
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

	public static WritableCellFormat getNormolCell(){ // FIXME move this logic to core module
		WritableFont font = new  WritableFont(WritableFont.TIMES, 12);
		WritableCellFormat format = new  WritableCellFormat(font);
		try {
			format.setAlignment(jxl.format.Alignment.LEFT);
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			format.setBorder(Border.ALL,BorderLineStyle.THIN,Colour.BLACK);
			format.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}

	/**
	 * Constrain shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		this.getShell().setText(Messages.importReport);
	}

	/**
	 *
	 * Result table label provider
	 *
	 * TableLabelProvider Description
	 *
	 * @author pangqiren
	 * @version 1.0 - 2011-4-1 created by pangqiren
	 */
	static class TableLabelProvider implements
			ITableLabelProvider {
		private final TableViewer tableViewer;
		private final List<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>();

		private final Image[] images;

		public TableLabelProvider(TableViewer tableViewer, Image[] images) {
			this.tableViewer = tableViewer;
			this.images = images;
		}

		/**
		 * Get column image
		 *
		 * @param element Object
		 * @param columnIndex int
		 * @return Image
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return images == null ? null : images[columnIndex];
		}

		/**
		 * Get column text
		 *
		 * @param element Object
		 * @param columnIndex int
		 * @return String
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return String.valueOf(tableViewer.getTable().getItemCount());
			} else {
				return element.toString();
			}
		}

		/**
		 * Add listener
		 *
		 * @param listener ILabelProviderListener
		 */
		public void addListener(ILabelProviderListener listener) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}

		/**
		 * Dispose listeners
		 */
		public void dispose() {
			listeners.clear();
		}

		/**
		 * Return whether is label property
		 *
		 * @param element Object
		 * @param property String
		 * @return boolean
		 */
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		/**
		 * Remove the listener
		 *
		 * @param listener ILabelProviderListener
		 */
		public void removeListener(ILabelProviderListener listener) {
			this.listeners.remove(listener);
		}
	}
}
