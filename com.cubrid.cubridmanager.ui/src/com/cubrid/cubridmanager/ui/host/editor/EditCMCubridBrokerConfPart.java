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
package com.cubrid.cubridmanager.ui.host.editor;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.control.AbstractBrokerConfigEditorPart;
import com.cubrid.common.ui.common.control.BrokerConfigEditComposite;
import com.cubrid.common.ui.spi.model.BrokerConfig;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.host.action.ConfEditInput;
import com.cubrid.cubridmanager.ui.host.dialog.ConfigType;
import com.cubrid.cubridmanager.ui.host.dialog.ExportConfigDialog;
import com.cubrid.cubridmanager.ui.host.dialog.ImportConfigDialog;
import com.cubrid.cubridmanager.ui.spi.util.ConfigParaHelp;

/**
 * cubrid broker editor
 * @author fulei
 * @version 1.0 - 2012-10-29 created by fulei
 *
 */
public class EditCMCubridBrokerConfPart extends AbstractBrokerConfigEditorPart{

	public static final String ID = EditCMCubridBrokerConfPart.class.getName();
	private final Logger LOGGER = LogUtil.getLogger(getClass());

	private boolean firstView = true;
	
	private ToolItem saveCubridBrokerConfItem;
	private ToolItem saveAsCubridBrokerConfItem;
	
	private ConfEditInput editorInput;
	
	private String defaultImportFileName;
	private String defaultImportFileCharset;
	
	/**
	 * Create part controls
	 * 
	 * @param parent
	 *            of the controls
	 * 
	 */
	public void createPartControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, false);
		comp.setLayout(layout);
		
		createToolBarComp (comp);
		editorComp = new BrokerConfigEditComposite(comp, SWT.NONE, 
				this, editorInput.getName());
		
		loadData();
	}
	
	
	/**
	 * create tool barcomp
	 * @param parent
	 */
	public void createToolBarComp (Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gd);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = -1;
		comp.setLayout(layout);

		ToolBar toolbar = new ToolBar(comp, SWT.LEFT_TO_RIGHT
				| SWT.FLAT);
		toolbar.setLayoutData(CommonUITool.createGridData(
				GridData.HORIZONTAL_ALIGN_END, 1, 1, -1, -1));
		saveCubridBrokerConfItem = new ToolItem(toolbar, SWT.PUSH);
		saveCubridBrokerConfItem.setImage(CubridManagerUIPlugin.getImage("icons/queryeditor/file_save.png"));
		saveCubridBrokerConfItem.setToolTipText(com.cubrid.cubridmanager.ui.host.Messages.msgTipSaveAction);
		saveCubridBrokerConfItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//check whether contains duplicate property name
				String errMsg = editorComp.validate();
				if (errMsg != null) {
					CommonUITool.openErrorBox(errMsg);
					return;
				}
				doSave();
			}
		});
		
		// Show/hide of the history pane
		saveAsCubridBrokerConfItem = new ToolItem(toolbar, SWT.PUSH);
		saveAsCubridBrokerConfItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_saveas.png"));
		saveAsCubridBrokerConfItem.setToolTipText(Messages.cubridBrokerConfEditorSaveAsItemLabel);
		saveAsCubridBrokerConfItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//check whether contains duplicate property name
				String errMsg = editorComp.validate();
				if (errMsg != null) {
					CommonUITool.openErrorBox(errMsg);
					return;
				}
				ExportConfigDialog dialog = new ExportConfigDialog(
						getSite().getShell(), ConfigType.CUBRID_BROKER, true);
				
				if (defaultExportFilePath != null && !"".equals(defaultExportFilePath)) {
					dialog.setDefaultFilePath(defaultExportFilePath);
				}
				if (defaultExportFileName != null && !"".equals(defaultExportFileName)) {
					dialog.setDefaultFileName(defaultExportFileName);
				}
				if (defaultExportFileExtName != null
						&& !"".equals(defaultExportFileExtName)) {
					dialog.setDefaultFileExtName(defaultExportFileExtName);
				}
				if (defaultExportFileCharset != null
						&& !"".equals(defaultExportFileCharset)) {
					dialog.setOutputFileCharset(defaultExportFileCharset);
				}
				
				if (dialog.open() == Dialog.OK) {
					defaultExportFilePath = dialog.getDefaultFilePath();
					defaultExportFileName = dialog.getDefaultFileName();
					defaultExportFileExtName = dialog.getDefaultFileExtName();
					String fileFullName = dialog.getOutputFileFullName();
					defaultExportFileCharset = dialog.getOutputFileCharset();
					
					BrokerConfig cubridBrokerConfig = editorComp.getBrokerConfig();
					errMsg = editorComp.validate();
					if (errMsg != null) {
						CommonUITool.openErrorBox(errMsg);
						return;
					}
					
					String contents = brokerConfPersistUtil.readBrokerConfig(cubridBrokerConfig);
					try {
						brokerConfPersistUtil.writeBrokerConfig(new File(fileFullName), defaultExportFileCharset, contents);
						CommonUITool.openInformationBox(Messages.titleSuccess, Messages.cubridBrokerConfEditorSaveSuccessMsg);
						setDirty(false);
					} catch (Exception e) {
						CommonUITool.openErrorBox(e.getMessage());
					}
				}
			}
		});
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		ToolItem importItem = new ToolItem(toolbar, SWT.PUSH);
		importItem.setImage(CubridManagerUIPlugin.getImage("icons/queryeditor/file_open.png"));
		importItem.setToolTipText(com.cubrid.cubridmanager.ui.host.Messages.msgTipOpenAction);
		importItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ImportConfigDialog dialog = new ImportConfigDialog(
						getSite().getShell(), ConfigType.CUBRID_BROKER, true);
				if (defaultImportFileName != null && !"".equals(defaultImportFileName)) {
					dialog.setDefaultFileName(defaultImportFileName);
				}
				if (defaultImportFileCharset != null
						&& !"".equals(defaultImportFileCharset)) {
					dialog.setDefaultCharset(defaultImportFileCharset);
				}

				if (dialog.open() == Dialog.OK) {
					defaultImportFileName = dialog.getDefaultFileName();
					defaultImportFileCharset = dialog.getDefaultCharset();
					List<String> contents = dialog.getImportFileContent();
					StringBuilder contentBuilder = new StringBuilder();
					for (String content : contents) {
						contentBuilder.append(content).append(StringUtil.NEWLINE);
					}
					BrokerConfig cubridBrokerConfig = brokerConfPersistUtil.parseStringLineToBrokerConfig(contentBuilder.toString());
					firstView = false;
					editorComp.setBrokerConfig(cubridBrokerConfig);
					editorComp.createBrokerConfTableData();
				}
			}
		});
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		addPropItem  = new ToolItem(toolbar, SWT.PUSH);
		addPropItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/table_record_insert.png"));
		addPropItem.setToolTipText(Messages.cubridBrokerConfEditorAddPropItemLabel);
		addPropItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editorComp.addPropData();
			}
		});
		
		deletePropItem  = new ToolItem(toolbar, SWT.PUSH);
		deletePropItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/table_record_delete.png"));
		deletePropItem.setToolTipText(Messages.cubridBrokerConfEditorDeletePropItemLabel);
		deletePropItem.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("all")
			public void widgetSelected(SelectionEvent event) {
				editorComp.deletePropData();
			}
		});
	}
	
	/**
	 * parseCommonTableValueToCubridBrokerConfig
	 * @param dataList
	 * @param tableColumnCount
	 * @return
	 */
	public BrokerConfig parseCommonTableValueToBrokerConfig (List<Map<String, String>> dataList, int tableColumnCount) {
		return brokerConfPersistUtil.parseCommonTableValueToBrokerConfig(dataList,
				tableColumnCount);
	}
	
	/**
	 * parseStringLineToCubridBrokerConfig
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public BrokerConfig parseStringLineToBrokerConfig (String content) {
		return brokerConfPersistUtil.parseStringLineToBrokerConfig(content);
	}

	/**
	 *  parse a CubridBrokerConfig model to a document string
	 * @param config CubridBrokerConfig
	 * @return String
	 */
	public String parseBrokerConfigToDocumnetString (BrokerConfig config) {
		return brokerConfPersistUtil.readBrokerConfig(config);
	}
	
	/**
	 * parse CubridBrokerConfig model to common table value
	 * @param config
	 * @return
	 */
	public List<Map<String, String>> parseBrokerConfigToCommonTableValue (BrokerConfig config) {
		return brokerConfPersistUtil.parseBrokerConfigToCommonTableValue(config);
	}
	
	/**
	 * Init the editor part
	 * 
	 * @param site
	 *            IEditorSite
	 * @param input
	 *            MigrationCfgEditorInput
	 * @throws PartInitException
	 *             when error
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (input instanceof ConfEditInput) {
			editorInput = (ConfEditInput) input;
		}
		setSite(site);
		setInput(input);
		String title = this.getPartName();
		String partName = title + " - " + input.getName() + "@"
				+ editorInput.getServerName() + ":"
				+ editorInput.getServerPort();
		this.setTitleToolTip(partName);
		this.setPartName(partName);
		setTitleImage(CommonUIPlugin.getImage("icons/queryeditor/query_editor.png"));
	}
	
	/**
	 * load cubrid broker config info
	 */
	public void loadData () {
		try {
			if (firstView) {
				List<String> contentsList = ConfigParaHelp.performGetBrokerConf(editorInput.getServerInfo());
				StringBuilder contentBuilder = new StringBuilder();
				for (String content : contentsList) {
					contentBuilder.append(content).append(StringUtil.NEWLINE);
				}
				BrokerConfig cubridBrokerConfig = 
					brokerConfPersistUtil.parseStringLineToBrokerConfig(contentBuilder.toString());
				firstView = false;
				editorComp.setBrokerConfig(cubridBrokerConfig);
				editorComp.createBrokerConfTableData();
			}
		} catch (Exception e) {
			LOGGER.error("load curbid broker conf file error", e.getMessage());
		}
	
	}
	
	public void doSave(IProgressMonitor monitor) {
		BrokerConfig cubridBrokerConfig = editorComp.getBrokerConfig();
		String errMsg = editorComp.validate();
		if (errMsg != null) {
			CommonUITool.openErrorBox(errMsg);
			return;
		}
		String contents = brokerConfPersistUtil.readBrokerConfig(cubridBrokerConfig);
		ConfigParaHelp.performImportBrokerConf(editorInput.getServerInfo(),
				contents);
		setDirty(false);
		
	}
	
	/**
	 * save to server
	 */
	public void doSave() {
		if (editorInput == null) {
			return;
		}
		if (!isSaveAllowed()) {
			return;
		}
		
		BrokerConfig cubridBrokerConfig = editorComp.getBrokerConfig();
		String errMsg = editorComp.validate();
		if (errMsg != null) {
			CommonUITool.openErrorBox(errMsg);
			return;
		}
		String contents = brokerConfPersistUtil.readBrokerConfig(cubridBrokerConfig);
		ConfigParaHelp.performImportBrokerConf(editorInput.getServerInfo(),
				contents);
		setDirty(false);
	}
	
	public boolean isSaveAllowed() {
		return CommonUITool.openConfirmBox(com.cubrid.cubridmanager.ui.host.Messages.msgConfirmImport);
	}
}
